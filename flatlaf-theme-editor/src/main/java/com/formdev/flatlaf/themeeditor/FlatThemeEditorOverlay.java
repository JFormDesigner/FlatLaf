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
import javax.swing.plaf.LayerUI;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.Token;
import com.formdev.flatlaf.UIDefaultsLoaderAccessor;
import com.formdev.flatlaf.ui.FlatUIUtils;
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

	private Font font;
	private Font baseFont;

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
		int maxTextWidth = fm.stringWidth( "HSL 360 100 100" );
		int textHeight = fm.getAscent() - fm.getLeading();

		int width = c.getWidth();
		int previewWidth = UIScale.scale( COLOR_PREVIEW_WIDTH );
		int gap = UIScale.scale( 4 );

		// check whether preview is outside of clip bounds
		if( clipBounds.x + clipBounds.width < width - previewWidth - maxTextWidth - gap )
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
				int textX = px - maxTextWidth;
				if( textX > r.x + gap) {
					float[] hsl = HSLColor.fromRGB( color );
					String hslStr = String.format( "HSL %3d %2d %2d",
						Math.round( hsl[0] ), Math.round( hsl[1] ), Math.round( hsl[2] ) );
					g.setColor( textArea.getForeground() );
					FlatUIUtils.drawString( textArea, g, hslStr, textX,
						r.y + ((r.height - textHeight) / 2) + textHeight );
				}
			} catch( BadLocationException ex ) {
				// ignore
			}
		}
	}

	private Color getColorInLine( FlatSyntaxTextArea textArea, int line ) {
		Object value = textArea.propertiesSupport.getParsedValueAtLine( line );
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
