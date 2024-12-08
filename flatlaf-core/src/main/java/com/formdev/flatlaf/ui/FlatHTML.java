/*
 * Copyright 2024 FormDev Software GmbH
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

package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.LabelView;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

/**
 * @author Karl Tauber
 * @since 3.5
 */
public class FlatHTML
{
	private FlatHTML() {}

	/**
	 * Adds CSS rule BASE_SIZE to the style sheet of the HTML view,
	 * which re-calculates font sizes based on current component font size.
	 * This is necessary for "absolute-size" keywords (e.g. "x-large")
	 * for "font-size" attributes in default style sheet (see javax/swing/text/html/default.css).
	 * See also <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-size#values">CSS font-size</a>.
	 * <p>
	 * This method should be invoked after {@link BasicHTML#updateRenderer(JComponent, String)}.
	 */
	public static void updateRendererCSSFontBaseSize( JComponent c ) {
		View view = (View) c.getClientProperty( BasicHTML.propertyKey );
		if( view == null )
			return;

//		dumpViews( view, 0 );

		Document doc = view.getDocument();
		if( !(doc instanceof HTMLDocument) )
			return;

		// add BASE_SIZE rule if necessary
		//  - if point size at index 7 is not 36, then probably HTML text contains BASE_SIZE rule
		//  - if point size at index 4 is equal to given font size, then it is not necessary to add BASE_SIZE rule
		StyleSheet styleSheet = ((HTMLDocument)doc).getStyleSheet();
/*debug
		for( int i = 1; i <= 7; i++ )
			System.out.println( i+": "+ styleSheet.getPointSize( i ) );
debug*/
		Font font = c.getFont();
		if( styleSheet.getPointSize( 7 ) != 36f ||
			font == null || styleSheet.getPointSize( 4 ) == font.getSize() )
		  return;

		// check whether view uses "absolute-size" keywords (e.g. "x-large") for font-size
		if( !usesAbsoluteSizeKeywordForFontSize( view ) )
			return;

		// get HTML text from component
		String text;
		if( c instanceof JLabel )
			text = ((JLabel)c).getText();
		else if( c instanceof AbstractButton )
			text = ((AbstractButton)c).getText();
		else if( c instanceof JToolTip )
			text = ((JToolTip)c).getTipText();
		else
			return;
		if( text == null || !BasicHTML.isHTMLString( text ) )
			return;

		// BASE_SIZE rule is parsed in javax.swing.text.html.StyleSheet.addRule()
		String style = "<style>BASE_SIZE " + font.getSize() + "</style>";
		String openTag = "";
		String closeTag = "";

		int headIndex;
		int styleIndex;

		int insertIndex;
		if( (headIndex = indexOfTag( text, "head", true )) >= 0 ) {
			// there is a <head> tag --> insert after <head> tag
			insertIndex = headIndex;
		} else if( (styleIndex = indexOfTag( text, "style", false )) >= 0 ) {
			// there is a <style> tag --> insert before <style> tag
			insertIndex = styleIndex;
		} else {
			// no <head> or <style> tag --> insert <head> tag after <html> tag
			insertIndex = "<html>".length();
			openTag = "<head>";
			closeTag = "</head>";
		}

		String newText = text.substring( 0, insertIndex )
			+ openTag + style + closeTag
			+ text.substring( insertIndex );

		BasicHTML.updateRenderer( c, newText );

		// for unit tests
		if( testUpdateRenderer != null )
			testUpdateRenderer.accept( c, newText );
	}

	// for unit tests
	static BiConsumer<JComponent, String> testUpdateRenderer;

	/**
	 * Returns start or end index of a HTML tag.
	 * Checks only for leading '<' character and (case-ignore) tag name.
	 */
	private static int indexOfTag( String html, String tag, boolean endIndex ) {
		int tagLength = tag.length();
		int maxLength = html.length() - tagLength - 2;
		char lastTagChar = tag.charAt( tagLength - 1 );

		for( int i = "<html>".length(); i < maxLength; i++ ) {
			// check for leading '<' and last tag name character
			if( html.charAt( i ) == '<' && Character.toLowerCase( html.charAt( i + tagLength ) ) == lastTagChar ) {
				// compare tag characters from last to first
				for( int j = tagLength - 2; j >= 0; j-- ) {
					if( Character.toLowerCase( html.charAt( i + 1 + j ) ) != tag.charAt( j ) )
						break; // not equal

					if( j == 0 ) {
						// tag found
						return endIndex ? html.indexOf( '>', i + tagLength ) + 1 : i;
					}
				}
			}
		}

		return -1;
	}

	private static final Set<String> absoluteSizeKeywordsSet = new HashSet<>( Arrays.asList(
		"xx-small", "x-small", "small", "medium", "large", "x-large", "xx-large" ) );

	/**
	 * Checks whether view uses "absolute-size" keywords (e.g. "x-large") for font-size
	 * (see javax/swing/text/html/default.css).
	 */
	private static boolean usesAbsoluteSizeKeywordForFontSize( View view ) {
		AttributeSet attributes = view.getAttributes();
		if( attributes != null ) {
			Object fontSize = attributes.getAttribute( CSS.Attribute.FONT_SIZE );
			if( fontSize != null ) {
				if( absoluteSizeKeywordsSet.contains( fontSize.toString() ) )
					return true;
			}
		}

		int viewCount = view.getViewCount();
		for( int i = 0; i < viewCount; i++ ) {
			if( usesAbsoluteSizeKeywordForFontSize( view.getView( i ) ) )
				return true;
		}

		return false;
	}

	/**
	 * Updates foreground in style sheet of the HTML view.
	 * Adds "body { color: #&lt;foreground-hex&gt;; }"
	 */
	public static void updateRendererCSSForeground( View view, Color foreground ) {
		Document doc = view.getDocument();
		if( !(doc instanceof HTMLDocument) || foreground == null )
			return;

		// add foreground rule if necessary
		//  - use tag 'body' because BasicHTML.createHTMLView() also uses this tag
		//    to set font and color styles to component font/color
		//    see: SwingUtilities2.displayPropertiesToCSS()
		//  - this color is not used if component is disabled;
		//    JTextComponent.getDisabledTextColor() is used for disabled text components;
		//    UIManager.getColor("textInactiveText") is used for other disabled components
		//    see: javax.swing.text.GlyphView.paint()
		Style bodyStyle = ((HTMLDocument)doc).getStyle( "body" );
		if( bodyStyle == null ) {
			StyleSheet styleSheet = ((HTMLDocument)doc).getStyleSheet();
			styleSheet.addRule( String.format( "body { color: #%06x; }", foreground.getRGB() & 0xffffff ) );
			clearViewCaches( view );
		} else if( !foreground.equals( bodyStyle.getAttribute( StyleConstants.Foreground ) ) ) {
			bodyStyle.addAttribute( StyleConstants.Foreground, foreground );
			clearViewCaches( view );
		}
	}

	/**
	 * Clears cached values in view so that CSS changes take effect.
	 */
	private static void clearViewCaches( View view ) {
		if( view instanceof LabelView )
			((LabelView)view).changedUpdate( null, null, null );

		int viewCount = view.getViewCount();
		for( int i = 0; i < viewCount; i++ )
			clearViewCaches( view.getView( i ) );
	}

	public static PropertyChangeListener createPropertyChangeListener( PropertyChangeListener superListener ) {
		return e -> {
			if( superListener != null )
				superListener.propertyChange( e );
			propertyChange( e );
		};
	}

	/**
	 * Invokes {@link #updateRendererCSSFontBaseSize(JComponent)}
	 * for {@link BasicHTML#propertyKey} property change events,
	 * which are fired when {@link BasicHTML#updateRenderer(JComponent, String)}
	 * updates the HTML view.
	 */
	public static void propertyChange( PropertyChangeEvent e ) {
		if( BasicHTML.propertyKey.equals( e.getPropertyName() ) && e.getNewValue() instanceof View )
			updateRendererCSSFontBaseSize( (JComponent) e.getSource() );
	}

/*debug
	public static void dumpView( JComponent c ) {
		View view = (View) c.getClientProperty( BasicHTML.propertyKey );
		if( view != null )
			dumpViews( view, 0 );
	}

	public static void dumpViews( View view, int indent ) {
		for( int i = 0; i < indent; i++ )
			System.out.print( "    " );

		System.out.printf( "%s @%-8x   %3d,%2d",
			view.getClass().isAnonymousClass() ? view.getClass().getName() : view.getClass().getSimpleName(),
			System.identityHashCode( view ),
			(int) view.getPreferredSpan( View.X_AXIS ),
			(int) view.getPreferredSpan( View.Y_AXIS ) );

		AttributeSet attrs = view.getAttributes();
		if( attrs != null ) {
			Object fontSize = attrs.getAttribute( CSS.Attribute.FONT_SIZE );
			System.out.printf( "  %-8s", fontSize );
		}

		if( view instanceof javax.swing.text.GlyphView ) {
			javax.swing.text.GlyphView gview = ((javax.swing.text.GlyphView)view);
			java.awt.Font font = gview.getFont();
			System.out.printf( "   %3d-%-3d  %s %2d (@%x)  #%06x  '%s'",
				gview.getStartOffset(), gview.getEndOffset() - 1,
				font.getName(), font.getSize(), System.identityHashCode( font ),
				gview.getForeground().getRGB() & 0xffffff,
				gview.getText( gview.getStartOffset(), gview.getEndOffset() ) );
		}
		System.out.println();

		int viewCount = view.getViewCount();
		for( int i = 0; i < viewCount; i++ ) {
			View child = view.getView( i );
			dumpViews( child, indent + 1 );
		}
	}
debug*/
}
