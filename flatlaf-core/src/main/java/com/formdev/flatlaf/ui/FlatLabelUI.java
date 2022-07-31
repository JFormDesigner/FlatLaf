/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JLabel}.
 *
 * <!-- BasicLabelUI -->
 *
 * @uiDefault Label.font				Font
 * @uiDefault Label.background			Color	only used if opaque
 * @uiDefault Label.foreground			Color
 *
 * <!-- FlatLabelUI -->
 *
 * @uiDefault Label.disabledForeground	Color
 *
 * @author Karl Tauber
 */
public class FlatLabelUI
	extends BasicLabelUI
	implements StyleableUI
{
	@Styleable protected Color disabledForeground;

	private final boolean shared;
	private boolean defaults_initialized = false;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatLabelUI.class, () -> new FlatLabelUI( true ) )
			: new FlatLabelUI( false );
	}

	/** @since 2 */
	protected FlatLabelUI( boolean shared ) {
		this.shared = shared;
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle( (JLabel) c );
	}

	@Override
	protected void installDefaults( JLabel c ) {
		super.installDefaults( c );

		if( !defaults_initialized ) {
			disabledForeground = UIManager.getColor( "Label.disabledForeground" );

			defaults_initialized = true;
		}
	}

	@Override
	protected void uninstallDefaults( JLabel c ) {
		super.uninstallDefaults( c );

		defaults_initialized = false;
		oldStyleValues = null;
	}

	@Override
	protected void installComponents( JLabel c ) {
		super.installComponents( c );

		// update HTML renderer if necessary
		updateHTMLRenderer( c, c.getText(), false );
	}

	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		String name = e.getPropertyName();
		if( name == "text" || name == "font" || name == "foreground" ) {
			JLabel label = (JLabel) e.getSource();
			updateHTMLRenderer( label, label.getText(), true );
		} else if( name.equals( FlatClientProperties.STYLE ) || name.equals( FlatClientProperties.STYLE_CLASS ) ) {
			JLabel label = (JLabel) e.getSource();
			if( shared && FlatStylingSupport.hasStyleProperty( label ) ) {
				// unshare component UI if necessary
				// updateUI() invokes installStyle() from installUI()
				label.updateUI();
			} else
				installStyle( label );
			label.revalidate();
			label.repaint();
		} else
			super.propertyChange( e );
	}

	/** @since 2 */
	protected void installStyle( JLabel c ) {
		try {
			applyStyle( c, FlatStylingSupport.getResolvedStyle( c, "Label" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( JLabel c, Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style,
			(key, value) -> applyStyleProperty( c, key, value ) );
	}

	/** @since 2 */
	protected Object applyStyleProperty( JLabel c, String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, c, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	/**
	 * Checks whether text contains HTML tags that use "absolute-size" keywords
	 * (e.g. "x-large") for font-size in default style sheet
	 * (see javax/swing/text/html/default.css).
	 * If yes, adds a special CSS rule (BASE_SIZE) to the HTML text, which
	 * re-calculates font sizes based on current component font size.
	 */
	static void updateHTMLRenderer( JComponent c, String text, boolean always ) {
		if( BasicHTML.isHTMLString( text ) &&
			c.getClientProperty( "html.disable" ) != Boolean.TRUE &&
			needsFontBaseSize( text ) )
		{
			// BASE_SIZE rule is parsed in javax.swing.text.html.StyleSheet.addRule()
			String style = "<style>BASE_SIZE " + c.getFont().getSize() + "</style>";

			String lowerText = text.toLowerCase();
			int headIndex;
			int styleIndex;

			int insertIndex;
			if( (headIndex = lowerText.indexOf( "<head>" )) >= 0 ) {
				// there is a <head> tag --> insert after <head> tag
				insertIndex = headIndex + "<head>".length();
			} else if( (styleIndex = lowerText.indexOf( "<style>" )) >= 0 ) {
				// there is a <style> tag --> insert before <style> tag
				insertIndex = styleIndex;
			} else {
				// no <head> or <style> tag --> insert <head> tag after <html> tag
				style = "<head>" + style + "</head>";
				insertIndex = "<html>".length();
			}

			text = text.substring( 0, insertIndex )
				+ style
				+ text.substring( insertIndex );
		} else if( !always )
			return; // not necessary to invoke BasicHTML.updateRenderer()

		BasicHTML.updateRenderer( c, text );
	}

	private static Set<String> tagsUseFontSizeSet;

	private static boolean needsFontBaseSize( String text ) {
		if( tagsUseFontSizeSet == null ) {
			// tags that use font-size in javax/swing/text/html/default.css
			tagsUseFontSizeSet = new HashSet<>( Arrays.asList(
				"h1", "h2", "h3", "h4", "h5", "h6", "code", "kbd", "big", "small", "samp" ) );
		}

		// search for tags in HTML text
		int textLength = text.length();
		for( int i = 6; i < textLength - 1; i++ ) {
			if( text.charAt( i ) == '<' ) {
				switch( text.charAt( i + 1 ) ) {
					// first letters of tags in tagsUseFontSizeSet
					case 'b': case 'B':
					case 'c': case 'C':
					case 'h': case 'H':
					case 'k': case 'K':
					case 's': case 'S':
						int tagBegin = i + 1;
						for( i += 2; i < textLength; i++ ) {
							if( !Character.isLetterOrDigit( text.charAt( i ) ) ) {
								String tag = text.substring( tagBegin, i ).toLowerCase();
								if( tagsUseFontSizeSet.contains( tag ) )
									return true;

								break;
							}
						}
						break;
				}
			}
		}

		return false;
	}

	static Graphics createGraphicsHTMLTextYCorrection( Graphics g, JComponent c ) {
		return (c.getClientProperty( BasicHTML.propertyKey ) != null)
			? HiDPIUtils.createGraphicsTextYCorrection( (Graphics2D) g )
			: g;
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		super.paint( createGraphicsHTMLTextYCorrection( g, c ), c );
	}

	@Override
	protected void paintEnabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
		int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
		g.setColor( l.getForeground() );
		FlatUIUtils.drawStringUnderlineCharAt( l, g, s, mnemIndex, textX, textY );
	}

	@Override
	protected void paintDisabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
		int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
		g.setColor( disabledForeground );
		FlatUIUtils.drawStringUnderlineCharAt( l, g, s, mnemIndex, textX, textY );
	}

	/**
	 * Overridden to scale iconTextGap.
	 */
	@Override
	protected String layoutCL( JLabel label, FontMetrics fontMetrics, String text, Icon icon, Rectangle viewR,
		Rectangle iconR, Rectangle textR )
	{
		return SwingUtilities.layoutCompoundLabel( label, fontMetrics, text, icon,
			label.getVerticalAlignment(), label.getHorizontalAlignment(),
			label.getVerticalTextPosition(), label.getHorizontalTextPosition(),
			viewR, iconR, textR,
			UIScale.scale( label.getIconTextGap() ) );
	}
}
