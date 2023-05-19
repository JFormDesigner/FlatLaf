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

package com.formdev.flatlaf.themeeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.plaf.LayerUI;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.Token;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.UIDefaultsLoaderAccessor;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.HSLColor;
import com.formdev.flatlaf.util.UIScale;

/**
 * An overlay layer that paints additional information about line content on the right side.
 *
 * @author Karl Tauber
 */
class FlatThemeEditorOverlay
	extends LayerUI<FlatSyntaxTextArea>
{
	private static final int COLOR_PREVIEW_WIDTH = 100;

	static boolean showHSL = true;
	static boolean showRGB;
	static boolean showLuma;

	private Font font;
	private Font baseFont;

	@SuppressWarnings( "FormatString" ) // Error Prone
	@Override
	public void paint( Graphics g, JComponent c ) {
		// paint the syntax text area
		super.paint( g, c );

		@SuppressWarnings( "unchecked" )
		FlatSyntaxTextArea textArea = ((JLayer<FlatSyntaxTextArea>)c).getView();
		Rectangle clipBounds = g.getClipBounds();

		// determine first and last visible lines
		int firstVisibleLine;
		int lastVisibleLine;
		try {
			int startOffset = textArea.viewToModel( new Point( 0, clipBounds.y ) );
			int endOffset = textArea.viewToModel( new Point( 0, clipBounds.y + clipBounds.height ) );
			firstVisibleLine = textArea.getLineOfOffset( startOffset );
			lastVisibleLine = textArea.getLineOfOffset( endOffset );
		} catch( BadLocationException ex ) {
			// ignore
			return;
		}

		// compute font (and cache it)
		if( baseFont != textArea.getFont() ) {
			baseFont = textArea.getFont();
			int fontSize = Math.max( (int) Math.round( baseFont.getSize() * 0.8 ), 8 );
			font = baseFont.deriveFont( (float) fontSize );
		}

		FontMetrics fm = c.getFontMetrics( font );
		int space = fm.stringWidth( "  " );
		int maxTextWidth = 0;
		if( showHSL )
			maxTextWidth += fm.stringWidth( "HSL 360 100 100" ) + space;
		if( showRGB )
			maxTextWidth += fm.stringWidth( "#ffffff" ) + space;
		if( showLuma )
			maxTextWidth += fm.stringWidth( "100" ) + space;
		maxTextWidth = Math.max( maxTextWidth - space, 0 );
		int textHeight = fm.getAscent() - fm.getLeading();

		int width = c.getWidth();
		int previewWidth = UIScale.scale( COLOR_PREVIEW_WIDTH );
		int gap = UIScale.scale( 4 );
		int textGap = (showHSL || showRGB || showLuma) ? UIScale.scale( 6 ) : 0;

		// check whether preview is outside of clip bounds
		if( clipBounds.x + clipBounds.width < width - previewWidth - maxTextWidth - gap - textGap )
			return;

		g.setFont( font );

		// paint additional information
		for( int line = firstVisibleLine; line <= lastVisibleLine; line++ ) {
			Color color = getColorInLine( textArea, line );
			if( color == null )
				continue;

			try {
				// paint color preview
				int lineEndOffset = textArea.getLineEndOffset( line );
				Rectangle r = textArea.modelToView( lineEndOffset - 1 );
				int pw = Math.min( width - r.x - gap, previewWidth );
				int px = width - pw;
				g.setColor( color );
				g.fillRect( px, r.y, pw, r.height );

				// if color is semi-transparent paint also none-transparent color
				int alpha = color.getAlpha();
				if( alpha != 255 && pw > r.height * 2 ) {
					g.setColor( new Color( color.getRGB() ) );
					g.fillRect( px + pw - r.height, r.y, r.height, r.height );
				}

				// paint text
				if( showHSL || showRGB || showLuma ) {
					int textX = px - textGap - maxTextWidth;
					if( textX > r.x + gap) {
						String colorStr = null;
						if( showHSL ) {
							float[] hsl = HSLColor.fromRGB( color );
							colorStr = String.format( (alpha != 255) ? "HSLA %3d %3d %3d %3d" : "HSL %3d %3d %3d",
								Math.round( hsl[0] ), Math.round( hsl[1] ), Math.round( hsl[2] ),
								Math.round( alpha / 255f * 100 ) );
						}
						if( showRGB ) {
							String rgbStr = String.format( (alpha != 255) ? "#%06x%02x" : "#%06x",
								color.getRGB() & 0xffffff, alpha );
							if( colorStr != null )
								colorStr += "  " + rgbStr;
							else
								colorStr = rgbStr;
						}
						if( showLuma ) {
							String lumaStr = String.format( "%3d",
								Math.round( ColorFunctions.luma( color ) * 100 ) );
							if( colorStr != null )
								colorStr += "  " + lumaStr;
							else
								colorStr = lumaStr;
						}

						int textWidth = fm.stringWidth( colorStr );
						if( textWidth > maxTextWidth )
							textX -= (textWidth - maxTextWidth);

						g.setColor( textArea.getForeground() );
						FlatUIUtils.drawString( textArea, g, colorStr, textX,
							r.y + ((r.height - textHeight) / 2) + textHeight );
					}
				}
			} catch( BadLocationException ex ) {
				// ignore
			}
		}
	}

	private Color getColorInLine( FlatSyntaxTextArea textArea, int line ) {
		Object value = textArea.propertiesSupport.getParsedValueAtLine( line );

		// resolve lazy value
		if( value instanceof LazyValue ) {
			Object[] pValue = { value };
			FlatLaf.runWithUIDefaultsGetter( key -> {
				return (key instanceof String)
					? textArea.propertiesSupport.getParsedProperty( (String) key )
					: null;
			}, () -> {
				pValue[0] = ((LazyValue)pValue[0]).createValue( null );
			} );
			value = pValue[0];
		}

		if( value instanceof Color )
			return (Color) value;

		Token token = textArea.getTokenListForLine( line );
		for( Token t = token; t != null && t.isPaintable(); t = t.getNextToken() ) {
			if( t.getType() == FlatThemeTokenMaker.TOKEN_COLOR ) {
				try {
					return new Color( UIDefaultsLoaderAccessor.parseColorRGBA( t.getLexeme() ), true );
				} catch( IllegalArgumentException ex ) {
					break;
				}
			}
		}

		return null;
	}
}
