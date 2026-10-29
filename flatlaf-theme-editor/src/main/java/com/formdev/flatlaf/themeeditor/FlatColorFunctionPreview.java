/*
 * Copyright 2026 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.themeeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.DoubleFunction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.UIDefaultsLoaderAccessor;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.HSLColor;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;

/**
 * @author Karl Tauber
 */
class FlatColorFunctionPreview
	extends JPanel
{
	private final FlatSyntaxTextArea textArea;

	FlatColorFunctionPreview( FlatSyntaxTextArea textArea ) {
		this.textArea = textArea;

		initComponents();

		textArea.addCaretListener( e -> updateLater() );
		updateLater();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		updateLater();
	}

	private void updateLater() {
		EventQueue.invokeLater( this::update );
	}

	private void update() {
		if( !isDisplayable() )
			return;

		boolean visible = false;
		try {
			int caretPosition = textArea.getCaretPosition();
			int line = textArea.getLineOfOffset( caretPosition );
			int startOffset = textArea.getLineStartOffset( line );
			int endOffset = textArea.getLineEndOffset( line );
			String text = textArea.getText( startOffset, endOffset - startOffset );
			if( StringUtils.isTrimmedEmpty( text ) )
				return;

			// find function parameters start
			int caretIndex = Math.min( caretPosition - startOffset, text.length() - 1 );
			int paramsStart = findFunctionParamsStart( text, caretIndex );
			if( paramsStart < 0 )
				return;

			// find function parameters end
			int paramsEnd = findFunctionParamsEnd( text, paramsStart );
			if( paramsEnd < 0 )
				return;

			// find function name start
			int funNameStart = findFunctionNameStart( text, paramsStart );
			if( funNameStart < 0 )
				return;

			try {
				parseColorFunctions( text, funNameStart, paramsStart, paramsEnd );
				visible = true;
			} catch( IllegalArgumentException | IndexOutOfBoundsException ex ) {
				// ignore
			}

		} catch( BadLocationException ex ) {
			ex.printStackTrace();
		} finally {
			setVisible( visible );
		}
	}

	private int findFunctionNameStart( String text, int paramsStart ) {
		int funNameEnd = paramsStart;
		while( funNameEnd > 0 && Character.isWhitespace( text.charAt( funNameEnd - 1 ) ) )
			funNameEnd--;
		int funNameStart = funNameEnd;
		while( funNameStart > 0 && Character.isLetter( text.charAt( funNameStart - 1 ) ) )
			funNameStart--;
		return (funNameStart < funNameEnd) ? funNameStart : -1;
	}

	private int findFunctionParamsStart( String text, int caretIndex ) {
		char charAtCaret = text.charAt( caretIndex );
		int textLength = text.length();

		// if caret is at '(', use it
		if( charAtCaret == '(' )
			return caretIndex;

		// if caret is at a letter, check whether it is a function name followed by '('
		if( Character.isLetter( charAtCaret ) ) {
			int index = skipLetter( text, caretIndex );
			index = skipWhitespace( text, index );
			if( index < textLength && text.charAt( index ) == '(' )
				return index;
		}

		// if caret is at a whitespace, check whether it is followed by '('
		if( Character.isWhitespace( charAtCaret ) ) {
			int index = skipWhitespace( text, caretIndex );
			if( index < textLength && text.charAt( index ) == '(' )
				return index;
		}

		// find '(' left to caret, ignore nested '(' and ')'
		int nestLevel = 0;
		for( int i = caretIndex - 1; i >= 0; i-- ) {
			char ch = text.charAt( i );
			if( ch == ')' )
				nestLevel++;
			else if( ch == '(' ) {
				if( nestLevel == 0 )
					return i;
				nestLevel--;
			}
		}

		// find first '('
		return text.indexOf( '(' );
	}

	private int findFunctionParamsEnd( String text, int paramsStart ) {
		// find ')' right to opening '(', ignore nested '(' and ')'
		int textLength = text.length();
		int nestLevel = 0;
		for( int i = paramsStart + 1; i < textLength; i++ ) {
			char ch = text.charAt( i );
			if( ch == '(' )
				nestLevel++;
			else if( ch == ')' ) {
				if( nestLevel == 0 )
					return i;
				nestLevel--;
			}
		}
		return -1;
	}

	private int skipLetter( String text, int fromIndex ) {
		int textLength = text.length();
		for( int i = fromIndex; i < textLength; i++ ) {
			if( !Character.isLetter( text.charAt( i ) ) )
				return i;
		}
		return textLength;
	}

	private int skipWhitespace( String text, int fromIndex ) {
		int textLength = text.length();
		for( int i = fromIndex; i < textLength; i++ ) {
			if( !Character.isWhitespace( text.charAt( i ) ) )
				return i;
		}
		return textLength;
	}

	private void parseColorFunctions( String text, int funNameStart, int paramsStart, int paramsEnd )
		throws IllegalArgumentException
	{
		String function = StringUtils.substringTrimmed( text, funNameStart, paramsStart );
		List<String> params = UIDefaultsLoaderAccessor.splitFunctionParams( text.substring( paramsStart + 1, paramsEnd ), ',' );
		if( params.isEmpty() )
			throw new IllegalArgumentException();

		functionLabel.setText( function + text.substring( paramsStart, paramsEnd + 1 ) );

		switch( function ) {
			case "lighten":			parseColorHSLIncreaseDecrease( 2, true, params ); return;
			case "darken":			parseColorHSLIncreaseDecrease( 2, false, params ); return;
			case "saturate":		parseColorHSLIncreaseDecrease( 1, true, params ); return;
			case "desaturate":		parseColorHSLIncreaseDecrease( 1, false, params ); return;
			case "fadein":			parseColorHSLIncreaseDecrease( 3, true, params ); return;
			case "fadeout":			parseColorHSLIncreaseDecrease( 3, false, params ); return;
			case "fade":			parseColorFade( params ); return;
			case "spin":			parseColorSpin( params ); return;
			case "changeHue":		parseColorChange( 0, params ); return;
			case "changeSaturation":parseColorChange( 1, params ); return;
			case "changeLightness":	parseColorChange( 2, params ); return;
			case "changeAlpha":		parseColorChange( 3, params ); return;
			case "mix":				parseColorMix( false, null, params ); return;
			case "tint":			parseColorMix( true, "#fff", params ); return;
			case "shade":			parseColorMix( true, "#000", params ); return;
		}

		// unknown function
		throw new IllegalArgumentException();
	}

	/**
	 * Syntax: lighten(color,amount[,options]) or darken(color,amount[,options]) or
	 *         saturate(color,amount[,options]) or desaturate(color,amount[,options]) or
	 *         fadein(color,amount[,options]) or fadeout(color,amount[,options])
	 *   - color: a color (e.g. #f00) or a color function
	 *   - amount: percentage 0-100%
	 *   - options: [relative] [autoInverse] [noAutoInverse] [derived] [lazy]
	 */
	private void parseColorHSLIncreaseDecrease( int hslIndex, boolean increase, List<String> params )
		throws IllegalArgumentException
	{
		String colorStr = params.get( 0 );
		String amountStr = params.get( 1 );
		int amount = UIDefaultsLoaderAccessor.parsePercentage( amountStr );
		boolean relative = false;
		boolean autoInverse = false;
		boolean derived = false;
		boolean lazy = false;

		if( params.size() > 2 ) {
			String options = params.get( 2 );
			relative = UIDefaultsLoaderAccessor.hasOption( options, "relative" );
			autoInverse = UIDefaultsLoaderAccessor.hasOption( options, "autoInverse" );
			derived = UIDefaultsLoaderAccessor.hasOption( options, "derived" );
			lazy = UIDefaultsLoaderAccessor.hasOption( options, "lazy" );

			// use autoInverse by default for derived colors, except if noAutoInverse is set
			if( derived && !UIDefaultsLoaderAccessor.hasOption( options, "noAutoInverse" ) )
				autoInverse = true;
		}
		boolean relative2 = relative;
		boolean autoInverse2 = autoInverse;

		// parse base color
		Color baseColor = lazy ? getColorLazy( colorStr ) : getParsedColor( colorStr );

		// create function
		DoubleFunction<Color> function = weight -> {
			return ColorFunctions.applyFunctions( baseColor, new ColorFunctions.HSLIncreaseDecrease(
				hslIndex, increase, (float) (weight * 100), relative2, autoInverse2 ) );
		};

		// update preview
		updatePreview( baseColor, null, function.apply( amount / 100. ), amountStr );
		gradientPreview.set( function, amount / 100f );
	}

	/**
	 * Syntax: fade(color,amount[,options])
	 *   - color: a color (e.g. #f00) or a color function
	 *   - amount: percentage 0-100%
	 *   - options: [derived] [lazy]
	 */
	private void parseColorFade( List<String> params )
		throws IllegalArgumentException
	{
		String colorStr = params.get( 0 );
		String amountStr = params.get( 1 );
		int amount = UIDefaultsLoaderAccessor.parsePercentage( amountStr );
		boolean lazy = false;

		if( params.size() > 2 ) {
			String options = params.get( 2 );
			lazy = UIDefaultsLoaderAccessor.hasOption( options, "lazy" );
		}

		// parse base color
		Color baseColor = lazy ? getColorLazy( colorStr ) : getParsedColor( colorStr );

		// create function
		DoubleFunction<Color> function = weight -> {
			return ColorFunctions.applyFunctions( baseColor, new ColorFunctions.Fade(
				(float) (weight * 100) ) );
		};

		// update preview
		updatePreview( baseColor, null, function.apply( amount / 100. ), amountStr );
		gradientPreview.set( function, amount / 100f );
	}

	/**
	 * Syntax: spin(color,angle[,options])
	 *   - color: a color (e.g. #f00) or a color function
	 *   - angle: number of degrees to rotate
	 *   - options: [derived] [lazy]
	 */
	private void parseColorSpin( List<String> params )
		throws IllegalArgumentException
	{
		String colorStr = params.get( 0 );
		String amountStr = params.get( 1 );
		int amount = UIDefaultsLoaderAccessor.parseInteger( amountStr );
		boolean lazy = false;

		if( params.size() > 2 ) {
			String options = params.get( 2 );
			lazy = UIDefaultsLoaderAccessor.hasOption( options, "lazy" );
		}

		// parse base color
		Color baseColor = lazy ? getColorLazy( colorStr ) : getParsedColor( colorStr );

		// create function
		DoubleFunction<Color> function = weight -> {
			return ColorFunctions.applyFunctions( baseColor, new ColorFunctions.HSLIncreaseDecrease(
				0, true, (float) (weight * 360), false, false ) );
		};

		// update preview
		updatePreview( baseColor, null, function.apply( amount / 360. ), amountStr );
		gradientPreview.set( function, amount / 360f );
	}

	/**
	 * Syntax: changeHue(color,value[,options]) or
	 *         changeSaturation(color,value[,options]) or
	 *         changeLightness(color,value[,options]) or
	 *         changeAlpha(color,value[,options])
	 *   - color: a color (e.g. #f00) or a color function
	 *   - value: for hue: number of degrees; otherwise: percentage 0-100%
	 *   - options: [derived] [lazy]
	 */
	private void parseColorChange( int hslIndex, List<String> params )
		throws IllegalArgumentException
	{
		String colorStr = params.get( 0 );
		String valueStr = params.get( 1 );
		int value = (hslIndex == 0)
			? UIDefaultsLoaderAccessor.parseInteger( valueStr )
			: UIDefaultsLoaderAccessor.parsePercentage( valueStr );
		boolean lazy = false;

		if( params.size() > 2 ) {
			String options = params.get( 2 );
			lazy = UIDefaultsLoaderAccessor.hasOption( options, "lazy" );
		}

		// parse base color
		Color baseColor = lazy ? getColorLazy( colorStr ) : getParsedColor( colorStr );

		// create function
		float factor = (hslIndex == 0) ? 360 : 100;
		DoubleFunction<Color> function = weight -> {
			return ColorFunctions.applyFunctions( baseColor, new ColorFunctions.HSLChange(
				hslIndex, (float) (weight * factor) ) );
		};

		// update preview
		updatePreview( baseColor, null, function.apply( value / factor ), valueStr );
		gradientPreview.set( function, value / factor );
	}

	/**
	 * Syntax: mix(color1,color2[,weight][,options]) or
	 *         tint(color[,weight][,options]) or
	 *         shade(color[,weight][,options])
	 *   - color1: a color (e.g. #f00) or a color function
	 *   - color2: a color (e.g. #f00) or a color function
	 *   - weight: the weight (in range 0-100%) to mix the two colors
	 *             larger weight uses more of first color, smaller weight more of second color
	 *   - options: [rgb|lrgb|oklab] [derived] [lazy]
	 */
	private void parseColorMix( boolean isTintOrShade, String color1Str, List<String> params )
		throws IllegalArgumentException
	{
		int i = 0;
		if( color1Str == null )
			color1Str = params.get( i++ );
		String color2Str = params.get( i++ );
		String weightStr = "50%";
		int weight = 50;
		boolean lazy = false;
		int method = ColorFunctions.RGB;

		if( params.size() > i ) {
			weightStr = params.get( i );
			if( !weightStr.isEmpty() && Character.isDigit( weightStr.charAt( 0 ) ) ) {
				weight = UIDefaultsLoaderAccessor.parsePercentage( weightStr );
				i++;
			}
		}
		if( params.size() > i ) {
			String options = params.get( i );
			if( UIDefaultsLoaderAccessor.hasOption( options, "rgb" ) )
				method = ColorFunctions.RGB;
			else if( UIDefaultsLoaderAccessor.hasOption( options, "lrgb" ) )
				method = ColorFunctions.LRGB;
			else if( UIDefaultsLoaderAccessor.hasOption( options, "oklab" ) )
				method = ColorFunctions.OKLAB;
			lazy = UIDefaultsLoaderAccessor.hasOption( options, "lazy" );
		}
		int method2 = method;

		// parse colors
		Color color1 = getParsedColor( color1Str );
		Color color2 = lazy ? getColorLazy( color2Str ) : getParsedColor( color2Str );

		// create function
		DoubleFunction<Color> function = weight2 -> {
			return ColorFunctions.applyFunctions( color2, new ColorFunctions.Mix2(
				color1, (float) (weight2 * 100), method2 ) );
		};

		// update preview
		if( isTintOrShade )
			updatePreview( color2, null, function.apply( weight / 100. ), weightStr );
		else
			updatePreview( color1, color2, function.apply( weight / 100. ), weightStr );
		gradientPreview.set( function, weight / 100f );
	}

	private Color getParsedColor( String colorStr )
		throws IllegalArgumentException
	{
		Object value = textArea.propertiesSupport.getParsedValue( "someColor", colorStr );
		if( !(value instanceof Color) )
			throw new IllegalArgumentException();
		return (Color) value;
	}

	private Color getColorLazy( String colorStr )
		throws IllegalArgumentException
	{
		Object[] pValue = { null };
		FlatLaf.runWithUIDefaultsGetter( key -> {
			return (key instanceof String)
				? textArea.propertiesSupport.getParsedProperty( (String) key )
				: null;
		}, () -> {
			pValue[0] = UIDefaultsLoaderAccessor.lazyUIManagerGet( colorStr );
		} );
		if( !(pValue[0] instanceof Color) )
			throw new IllegalArgumentException();
		return (Color) pValue[0];
	}

	private void updatePreview( Color color1, Color color2, Color newColor, String newText ) {
		color1HSLLabel.setText( colorToHSLString( color1 ) );
		newColorHSLLabel.setText( colorToHSLString( newColor ) );

		color1RGBLabel.setText( FlatSyntaxTextAreaActions.colorToString( color1 ) );
		newColorRGBLabel.setText( FlatSyntaxTextAreaActions.colorToString( newColor ) );

		color1Preview.setBackground( color1 );
		newColorPreview.setBackground( newColor );
		newColorPreview.setForeground( ColorFunctions.isDark( newColor ) ? Color.white : Color.black );
		newColorPreview.setText( newText );

		boolean hasColor2 = (color2 != null);
		color2HSLLabel.setVisible( hasColor2 );
		color2RGBLabel.setVisible( hasColor2 );
		color2Preview.setVisible( hasColor2 );
		if( hasColor2 ) {
			color2HSLLabel.setText( colorToHSLString( color2 ) );
			color2RGBLabel.setText( FlatSyntaxTextAreaActions.colorToString( color2 ) );
			color2Preview.setBackground( color2 );
		}
	}

	@SuppressWarnings( "FormatString" ) // Error Prone
	private String colorToHSLString( Color color ) {
		// necessary to convert float colors (produced by class HSLColor)  to int colors
		// (e.g. HUE may be 359.99997 in float colors, but becomes 0 in int colors)
		color = new Color( color.getRGB(), true );

		float[] hsl = HSLColor.fromRGB( color );
		int alpha = color.getAlpha();
		return String.format( (alpha != 255) ? "HSLA %d %d %d %d" : "HSL %d %d %d",
			Math.round( hsl[0] ), Math.round( hsl[1] ), Math.round( hsl[2] ),
			Math.round( alpha / 255f * 100 ) );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		JPanel previewPanel = new JPanel();
		functionLabel = new JLabel();
		color1HSLLabel = new JLabel();
		newColorHSLLabel = new JLabel();
		color2HSLLabel = new JLabel();
		color1RGBLabel = new JLabel();
		newColorRGBLabel = new JLabel();
		color2RGBLabel = new JLabel();
		color1Preview = new FlatColorFunctionPreview.Preview();
		newColorPreview = new FlatColorFunctionPreview.Preview();
		color2Preview = new FlatColorFunctionPreview.Preview();
		gradientPreview = new FlatColorFunctionPreview.GradientPreview();

		//======== this ========
		setLayout(new MigLayout(
			"hidemode 3",
			// columns
			"[grow,fill]",
			// rows
			"[]" +
			"[]"));

		//======== previewPanel ========
		{
			previewPanel.setLayout(new MigLayout(
				"fillx,insets 0,hidemode 3",
				// columns
				"[grow,sizegroup 1,fill]0" +
				"[grow,sizegroup 1,fill]0" +
				"[grow,sizegroup 1,fill]",
				// rows
				"[]" +
				"[]0" +
				"[]0" +
				"[30,fill]"));

			//---- functionLabel ----
			functionLabel.setText("text");
			functionLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
			previewPanel.add(functionLabel, "cell 0 0 3 1,wmax 100%");

			//---- color1HSLLabel ----
			color1HSLLabel.setText("text");
			previewPanel.add(color1HSLLabel, "cell 0 1,wmax 33%");

			//---- newColorHSLLabel ----
			newColorHSLLabel.setText("text");
			previewPanel.add(newColorHSLLabel, "cell 1 1,wmax 33%");

			//---- color2HSLLabel ----
			color2HSLLabel.setText("text");
			previewPanel.add(color2HSLLabel, "cell 2 1,wmax 33%");

			//---- color1RGBLabel ----
			color1RGBLabel.setText("text");
			previewPanel.add(color1RGBLabel, "cell 0 2,wmax 33%");

			//---- newColorRGBLabel ----
			newColorRGBLabel.setText("text");
			previewPanel.add(newColorRGBLabel, "cell 1 2,wmax 33%");

			//---- color2RGBLabel ----
			color2RGBLabel.setText("text");
			previewPanel.add(color2RGBLabel, "cell 2 2,wmax 33%");

			//---- color1Preview ----
			color1Preview.setBackground(Color.red);
			color1Preview.setHorizontalAlignment(SwingConstants.CENTER);
			previewPanel.add(color1Preview, "cell 0 3,width 33%");

			//---- newColorPreview ----
			newColorPreview.setBackground(Color.blue);
			newColorPreview.setHorizontalAlignment(SwingConstants.CENTER);
			previewPanel.add(newColorPreview, "cell 1 3,width 33%");

			//---- color2Preview ----
			color2Preview.setBackground(Color.green);
			color2Preview.setHorizontalAlignment(SwingConstants.CENTER);
			previewPanel.add(color2Preview, "cell 2 3,width 33%");
		}
		add(previewPanel, "cell 0 0");
		add(gradientPreview, "cell 0 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	private JLabel functionLabel;
	private JLabel color1HSLLabel;
	private JLabel newColorHSLLabel;
	private JLabel color2HSLLabel;
	private JLabel color1RGBLabel;
	private JLabel newColorRGBLabel;
	private JLabel color2RGBLabel;
	private FlatColorFunctionPreview.Preview color1Preview;
	private FlatColorFunctionPreview.Preview newColorPreview;
	private FlatColorFunctionPreview.Preview color2Preview;
	private FlatColorFunctionPreview.GradientPreview gradientPreview;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

	//---- class Preview ------------------------------------------------------

	private static class Preview
		extends JLabel
	{
		@Override
		protected void paintComponent( Graphics g ) {
			int width = getWidth();
			int height = getHeight();

			Color background = getBackground();

			// paint checkerboard pattern
			if( background.getAlpha() != 255 )
				GradientPreview.paintCheckerboard( g, 0, 0, width, height );

			// paint color preview
			g.setColor( background );
			g.fillRect( 0, 0, width, height );

			// paint text
			super.paintComponent( g );
		}
	}

	//---- class GradientPreview ----------------------------------------------

	private static class GradientPreview
		extends JComponent
	{
		private static final int
			GRADIENT_HEIGHT = 30,
			TICK_HEIGHT = 8;

		private DoubleFunction<Color> function;
		private float weight;

		private BufferedImage image;
		private final JLabel label = new JLabel( "123" );

		void set( DoubleFunction<Color> function, float weight ) {
			this.function = function;
			this.weight = weight;

			image = null;
			repaint();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(
				UIScale.scale( 100 ),
				UIScale.scale( GRADIENT_HEIGHT + TICK_HEIGHT ) + this.label.getPreferredSize().height );
		}

		@Override
		protected void paintComponent( Graphics g ) {
			int width = getWidth();
			int height = getHeight();

			if( image == null || image.getWidth() != width ) {
				image = new BufferedImage( width, 1, BufferedImage.TYPE_INT_ARGB );

				for( int i = 0; i < width; i++ ) {
					float weight = (float) i / width;
					Color result = (function != null)
						? function.apply( weight )
						: ColorFunctions.mix( Color.red, Color.green, 1f - weight );
					image.setRGB( i, 0, result.getRGB() );
				}
			}

			int gradientHeight = UIScale.scale( GRADIENT_HEIGHT );
			GradientPreview.paintCheckerboard( g, 0, 0, width, gradientHeight );
			g.drawImage( image, 0, 0, width, gradientHeight, null );

			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

			// paint ticks
			int tickHeight = UIScale.scale( TICK_HEIGHT );
			int y = gradientHeight + UIScale.scale( 2 );
			float w10th = width / 10f;
			int w20th = Math.round( width / 20f );
			g.setColor( UIManager.getColor( "Slider.tickColor" ) );
			for( int i = 0; i <= 10; i++ ) {
				int x = Math.min( Math.round( (w10th * i) ), width - 1 );
				g.drawLine( x, y, x, y + tickHeight );
				g.drawLine( x + w20th, y, x + w20th, y + (tickHeight / 2) );
			}

			// paint current weight
			g.setColor( UIManager.getColor( "Objects.RedStatus" ) );
			((Graphics2D)g).setStroke( new BasicStroke( UIScale.scale( 3 ) ) );
			int wx = Math.min( Math.round( width * weight ), width - 1 );
			g.drawLine( wx, y, wx, y + tickHeight );

			FlatUIUtils.resetRenderingHints( g, oldRenderingHints );

			// paint labels
			g.setColor( UIManager.getColor( "Label.foreground" ) );
			g.setFont( UIManager.getFont( "small.font" ) );
			FontMetrics fm = g.getFontMetrics();
			for( int i = 0; i <= 10; i++ ) {
				if( i == 9 )
					continue;

				int x = Math.min( Math.round( (w10th * i) ), width - 1 );

				String str = String.format( "%d%%", i * 10 );
				int lw = fm.stringWidth( str );
				int lx = Math.min( Math.max( 0, x - (lw / 2) ), width - lw );
				FlatUIUtils.drawString( label, g, str, lx, height );
			}
		}

		static void paintCheckerboard( Graphics g, int x, int y, int width, int height ) {
			Color c = UIManager.getColor( "Panel.background" );
			g.setColor( FlatLaf.isLafDark()
				? ColorFunctions.lighten( c, 0.2f )
				: ColorFunctions.darken( c, 0.2f ) );

			Shape oldClip = g.getClip();
			g.setClip( x, y, width, height );

			int cw = UIScale.scale( 8 );
			int cw2 = cw * 2;
			for( int cx = x; cx < width; cx += cw2 ) {
				for( int cy = y; cy < height; cy += cw2 ) {
					g.fillRect( cx, cy, cw, cw );
					g.fillRect( cx + cw, cy + cw, cw, cw );
				}
			}

			g.setClip( oldClip );
		}
	}
}
