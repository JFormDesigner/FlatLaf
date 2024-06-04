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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.Document;
import javax.swing.text.LabelView;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
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
	 * See also <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-size?retiredLocale=de#values">CSS font-size</a>.
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
		int fontBaseSize = c.getFont().getSize();
		if( styleSheet.getPointSize( 7 ) != 36f ||
			styleSheet.getPointSize( 4 ) == fontBaseSize )
		  return;

		// BASE_SIZE rule is parsed in javax.swing.text.html.StyleSheet.addRule()
		styleSheet.addRule( "BASE_SIZE " + fontBaseSize );
		clearViewCaches( view );

//		dumpViews( view, 0 );
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
		if( BasicHTML.propertyKey.equals( e.getPropertyName() ) )
			FlatHTML.updateRendererCSSFontBaseSize( (JComponent) e.getSource() );
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
		System.out.print( view.getClass().isAnonymousClass() ? view.getClass().getName() : view.getClass().getSimpleName() );
		if( view instanceof LabelView ) {
			LabelView lview = ((LabelView)view);
			Font font = lview.getFont();
			Color foreground = lview.getForeground();
			System.out.printf( "  %2d-%-2d  %-14s %d  #%06x",
				lview.getStartOffset(), lview.getEndOffset() - 1,
				font.getName(), font.getSize(),
				foreground.getRGB() & 0xffffff );
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
