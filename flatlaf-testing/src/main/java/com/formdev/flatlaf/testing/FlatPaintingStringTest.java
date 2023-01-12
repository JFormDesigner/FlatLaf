/*
 * Copyright 2020 FormDev Software GmbH
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

package com.formdev.flatlaf.testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import javax.swing.*;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.fonts.roboto_mono.FlatRobotoMonoFont;
import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.JavaCompatibility;
import com.formdev.flatlaf.util.SystemInfo;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatPaintingStringTest
	extends JPanel
{
	public static void main( String[] args ) {
		System.setProperty( FlatSystemProperties.UI_SCALE, "1x" );
		System.setProperty( "sun.java2d.uiScale", "1x" );
		System.setProperty( "FlatLaf.debug.HiDPIUtils.useDebugScaleFactor", "true" );

		SwingUtilities.invokeLater( () -> {
			FlatInterFont.installLazy();
			FlatJetBrainsMonoFont.installLazy();
			FlatRobotoFont.installLazy();
			FlatRobotoMonoFont.installLazy();

			FlatTestFrame frame = FlatTestFrame.create( args, "FlatPaintingStringTest" );

			ToolTipManager.sharedInstance().setInitialDelay( 0 );
			ToolTipManager.sharedInstance().setDismissDelay( 10000 );

			frame.showFrame( FlatPaintingStringTest::new );
		} );
	}

	FlatPaintingStringTest() {
		initComponents();

		String[] availableFontFamilyNames = FontUtils.getAvailableFontFamilyNames().clone();
		Arrays.sort( availableFontFamilyNames );

		Font currentFont = UIManager.getFont( "Label.font" );
		String currentFamily = currentFont.getFamily();

		// initialize font families combobox
		String[] families = {
			// regular
			"Arial", "Cantarell", "DejaVu Sans",
			"Dialog", "Helvetica Neue", "Liberation Sans", "Noto Sans", "Open Sans",
			"SansSerif", "Segoe UI", "Tahoma", "Ubuntu", "Verdana", ".SF NS Text",
			FlatInterFont.FAMILY,
			FlatRobotoFont.FAMILY,

			// light, semibold
			"Segoe UI Light", "Segoe UI Semibold",
			"HelveticaNeue-Thin", "HelveticaNeue-Medium",
			"Lato Light", "Ubuntu Light", "Cantarell Light",
			"Lato Semibold", "Ubuntu Medium", "Montserrat SemiBold",
			FlatInterFont.FAMILY_LIGHT, FlatInterFont.FAMILY_SEMIBOLD,
			FlatRobotoFont.FAMILY_LIGHT, FlatRobotoFont.FAMILY_SEMIBOLD,

			// monospaced
			"Monospaced", "Consolas", "Courier New", "Menlo", "Liberation Mono", "Ubuntu Mono",
			FlatJetBrainsMonoFont.FAMILY, FlatRobotoMonoFont.FAMILY,
		};
		Arrays.sort( families, String.CASE_INSENSITIVE_ORDER );
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		for( String family : families ) {
			if( Arrays.binarySearch( availableFontFamilyNames, family ) >= 0 )
				model.addElement( family );
		}
		fontField.setModel( model );
		fontField.setSelectedItem( currentFamily );
		updateFontMetricsLabel();

		add( new JLabel(), "newLine" );
		add( new JLabel( "none" ) );
		add( new JLabel( "flatlaf" ) );
		add( new JLabel() );
		if( SystemInfo.isJava_9_orLater ) {
			add( new JLabel( "0.125" ) );
			add( new JLabel( "0.25" ) );
			add( new JLabel( "0.5" ) );
			add( new JLabel( "0.625" ) );
			add( new JLabel( "0.75" ) );
			add( new JLabel( "0.875" ) );
			add( new JLabel( "1" ) );
			add( new JLabel( "1.25" ) );
			add( new JLabel() );
			add( new JLabel( "0.25*scale" ) );
			add( new JLabel( "0.5*scale" ) );
			add( new JLabel( "ty" ) );
		} else {
			add( new JLabel( "0.25*scale" ) );
			add( new JLabel( "0.5*scale" ) );
			add( new JLabel( "0.625*scale" ) );
			add( new JLabel( "0.75*scale" ) );
			add( new JLabel( "0.875*scale" ) );
		}

		YCorrectionFunction none = (g, scaleFactor) -> 0;
		YCorrectionFunction flatlaf = (g, scaleFactor) -> {
			System.setProperty( "FlatLaf.debug.HiDPIUtils.debugScaleFactor", Float.toString( scaleFactor ) );
			return HiDPIUtils.computeTextYCorrection( g );
		};
		YCorrectionFunction ty = (g, scaleFactor) -> {
			// Based on translateY, which is the scaled Y coordinate translation of the graphics context.
			// When painting whole window, translateY is from top of window, and this works fine.
			// But when repainting only parts of the window, then translateY starts somewhere
			// else and the text if (re-)painted at the wrong Y location.
			double y = g.getTransform().getTranslateY();
			return (float) -(y - (int) y);
		};

		float[] scaleFactors = { 1f, 1.25f, 1.5f, 1.75f, 2f, 2.25f, 2.5f, 3f, 3.5f, 4f };

		for( float scaleFactor : scaleFactors ) {
			add( new JLabel( String.valueOf( scaleFactor ) ), "newLine" );

			add( scaleFactor, none );
			add( scaleFactor, flatlaf );
			add( new JLabel( "  " ) );
			if( SystemInfo.isJava_9_orLater ) {
				add( scaleFactor, (g, sf) -> -0.125f );
				add( scaleFactor, (g, sf) -> -0.25f );
				add( scaleFactor, (g, sf) -> -0.5f );
				add( scaleFactor, (g, sf) -> -0.625f );
				add( scaleFactor, (g, sf) -> -0.75f );
				add( scaleFactor, (g, sf) -> -0.875f );
				add( scaleFactor, (g, sf) -> -1f );
				add( scaleFactor, (g, sf) -> -1.25f );
				add( new JLabel( "  " ) );
				add( scaleFactor, (g, sf) -> -(0.25f * sf) );
				add( scaleFactor, (g, sf) -> -(0.5f * sf) );
				add( scaleFactor, ty );
			} else {
				add( scaleFactor, (g, sf) -> -(0.25f * sf) );
				add( scaleFactor, (g, sf) -> -(0.5f * sf) );
				add( scaleFactor, (g, sf) -> -(0.625f * sf) );
				add( scaleFactor, (g, sf) -> -(0.75f * sf) );
				add( scaleFactor, (g, sf) -> -(0.875f * sf) );
			}

			add( new JLabel( String.valueOf( scaleFactor ) ) );
		}
	}

	private void add( float scaleFactor, YCorrectionFunction correctionFunction ) {
		if( SystemInfo.isJava_9_orLater ) {
			add( new Painter( scaleFactor, correctionFunction, 0 ), "split 4, gapx 0 0" );
			add( new Painter( scaleFactor, correctionFunction, 1 ), "gapx 0 0" );
			add( new Painter( scaleFactor, correctionFunction, 2 ), "gapx 0 0" );
			add( new Painter( scaleFactor, correctionFunction, 3 ), "gapx 0 0" );
		} else
			add( new Painter( scaleFactor, correctionFunction, 0 ) );
	}

	private void fontChanged() {
		String family = (String) fontField.getSelectedItem();

		Font font = UIManager.getFont( "defaultFont" );
		if( font.getFamily().equals( family ) )
			return;

		Font newFont = FontUtils.getCompositeFont( family, font.getStyle(), font.getSize() );
		UIManager.put( "defaultFont", newFont );
		updateFontMetricsLabel();

		FlatLaf.updateUILater();
	}

	private void updateFontMetricsLabel() {
		Font font = UIManager.getFont( "defaultFont" );
		FontMetrics fm = getFontMetrics( font );
		fontMetricsLabel.setText( String.format( "%s %d    height %d   ascent %d   descent %d   max ascent %d   max descent %d   leading %d",
			font.getFamily(), font.getSize(),
			fm.getHeight(), fm.getAscent(), fm.getDescent(),
			fm.getMaxAscent(), fm.getMaxDescent(), fm.getLeading()
		) );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel1 = new JPanel();
		JLabel fontLabel = new JLabel();
		fontField = new JComboBox<>();
		fontMetricsLabel = new JLabel();

		//======== this ========
		setBorder(null);
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]",
			// rows
			"[shrink 0,top]unrel" +
			"[shrink 0,top]"));

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[fill]" +
				"[fill]",
				// rows
				"[]"));

			//---- fontLabel ----
			fontLabel.setText("Font:");
			panel1.add(fontLabel, "cell 0 0");

			//---- fontField ----
			fontField.setMaximumRowCount(25);
			fontField.addActionListener(e -> fontChanged());
			panel1.add(fontField, "cell 1 0");

			//---- fontMetricsLabel ----
			fontMetricsLabel.setText("text");
			panel1.add(fontMetricsLabel, "cell 2 0");
		}
		add(panel1, "north");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JComboBox<String> fontField;
	private JLabel fontMetricsLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	private interface YCorrectionFunction {
		float computeTextYCorrection( Graphics2D g, float scaleFactor );
	}

	//---- class Painter ------------------------------------------------------

	public static class Painter
		extends JLabel
	{
		private final float scaleFactor;
		private final YCorrectionFunction correctionFunction;
		private final int yOffset;

		public Painter( float scaleFactor, YCorrectionFunction correctionFunction, int yOffset ) {
			super( "E" );
			this.scaleFactor = scaleFactor;
			this.correctionFunction = correctionFunction;
			this.yOffset = yOffset;

			updateFont();
		}

		@Override
		public void updateUI() {
			super.updateUI();
			updateFont();
		}

		private void updateFont() {
			if( scaleFactor == 0 )
				return; // invoked from super constructor

			if( !SystemInfo.isJava_9_orLater ) {
				Font font = UIManager.getFont( "defaultFont" );
				setFont( font.deriveFont( (float) Math.round( font.getSize() * scaleFactor ) ) );
			}
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();
			if( SystemInfo.isJava_9_orLater ) {
				// compute component size using JRE scaling
				//
				// The y offset is used to simulate different vertical component positions,
				// which may result in different component heights.
				// E.g. scaling following bounds by 150% results in different heights:
				//   0,0, 10,15  -->  [x=0,y=0,width=15,height=22]
				//   0,1, 10,15  -->  [x=0,y=1,width=15,height=23]
				Rectangle r = scaleTo1x( scaleFactor, 0, yOffset, size.width, size.height );
				return new Dimension( r.width, r.height );
			} else
				return size;
		}

		@Override
		protected void paintComponent( Graphics g ) {
			Graphics2D g2 = (Graphics2D) g;

			int width = getWidth();
			int height = getHeight();
			FontMetrics fm = getFontMetrics( getFont() );

			// paint lines at 1x
			HiDPIUtils.paintAtScale1x( g2, 0, 0, width, height,
				(g2d, x2, y2, width2, height2, scaleFactor2) -> {
					g.setColor( Color.blue );
					g.drawLine( 0, 0, width2, 0 );
					g.drawLine( 0, height2 - 1, width2, height2 - 1 );

					int baseline = (int) Math.round( fm.getAscent() * scaleFactor2
						* (SystemInfo.isJava_9_orLater ? scaleFactor : 1f) ) - 1;
					int topline = height2 - baseline - 1;

					g.setColor( Color.red );
					g.drawLine( 0, baseline, width2, baseline );
					g.drawLine( 0, topline, width2, topline );
				} );

			// simulate different vertical component positions
			if( yOffset > 0 && SystemInfo.isJava_9_orLater ) {
				double ty = yOffset * scaleFactor;
				ty -= (int) ty;
				if( ty == 0 )
					return; // no need to paint

				g2.translate( 0, ty );
			}

			// scale
			if( SystemInfo.isJava_9_orLater )
				((Graphics2D)g).scale( scaleFactor, scaleFactor );

			// compute Y correction
			float yCorrection = correctionFunction.computeTextYCorrection( g2, scaleFactor );

			// create graphics that applies Y correction
			Graphics2D cg = new Graphics2DProxy( g2 ) {
				@Override
				public void drawString( String str, int x, int y ) {
					super.drawString( str, x, y + yCorrection );
				}

				@Override
				public void drawString( String str, float x, float y ) {
					super.drawString( str, x, y + yCorrection );
				}
			};

			// draw string
			g.setColor( getForeground() );
			int y = fm.getAscent();
			JavaCompatibility.drawStringUnderlineCharAt( this, cg, "E", -1, 0, y );

			// set tooltip text
			AffineTransform t = g2.getTransform();
			double textY = t.getTranslateY() + (y * t.getScaleY());
			setToolTipText( textY + " + " + yCorrection + " = " + (textY + yCorrection) );
		}

		/**
		 * Scales a rectangle in the same way as the JRE does in
		 * sun.java2d.pipe.PixelToParallelogramConverter.fillRectangle(),
		 * which is used by Graphics.fillRect().
		 *
		 * This is a copy of HiDPIUtils.scale()
		 */
		public static Rectangle scaleTo1x( double scaleFactor, int x, int y, int width, int height ) {
			double px = (x * scaleFactor);
			double py = (y * scaleFactor);

			double newX = normalize( px );
			double newY = normalize( py );
			double newWidth  = normalize( px + (width * scaleFactor) ) - newX;
			double newHeight = normalize( py + (height * scaleFactor) ) - newY;

			return new Rectangle( (int) Math.floor( newX ), (int) Math.floor( newY ), (int) newWidth, (int) newHeight );
		}

		private static double normalize( double value ) {
			return Math.floor( value + 0.25 ) + 0.25;
		}
	}
}
