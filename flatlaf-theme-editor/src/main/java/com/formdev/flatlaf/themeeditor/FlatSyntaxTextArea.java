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
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.Token;
import com.formdev.flatlaf.UIDefaultsLoaderAccessor;

/**
 * A text area that supports editing FlatLaf themes.
 *
 * @author Karl Tauber
 */
class FlatSyntaxTextArea
	extends TextEditorPane
{
	private boolean useColorOfColorTokens;

	private final Map<String, Color> parsedColorsMap = new HashMap<>();

	FlatSyntaxTextArea() {
	}

	boolean isUseColorOfColorTokens() {
		return useColorOfColorTokens;
	}

	void setUseColorOfColorTokens( boolean useColorOfColorTokens ) {
		this.useColorOfColorTokens = useColorOfColorTokens;
		setHighlightCurrentLine( !useColorOfColorTokens );
	}

	@Override
	public Color getBackgroundForToken( Token t ) {
		if( useColorOfColorTokens && t.getType() == FlatThemeTokenMaker.TOKEN_COLOR ) {
			Color color = parseColor( t );
			if( color != null )
				return color;
		}

		return super.getBackgroundForToken( t );
	}

	@Override
	public Color getForegroundForToken( Token t ) {
		if( useColorOfColorTokens && t.getType() == FlatThemeTokenMaker.TOKEN_COLOR && !isCurrentLineHighlighted( t.getOffset() )) {
			Color color = parseColor( t );
			if( color != null ) {
				return (colorLuminance( color ) > 164 || color.getAlpha() < 96)
					? Color.black
					: Color.white;
			}
		}

		return super.getForegroundForToken( t );
	}

	private Color parseColor( Token token ) {
		return parsedColorsMap.computeIfAbsent( token.getLexeme(), s -> {
			try {
				return new Color( UIDefaultsLoaderAccessor.parseColorRGBA( s ), true );
			} catch( IllegalArgumentException ex ) {
				return null;
			}
		} );

	}

	private int colorLuminance( Color c ) {
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();

		int min = Math.min( red, Math.min( green, blue ) );
		int max = Math.max( red, Math.max( green, blue ) );

		return (max + min) / 2;
	}

	private boolean isCurrentLineHighlighted( int offset ) {
		try {
			return getHighlightCurrentLine() &&
				getSelectionStart() == getSelectionEnd() &&
				getLineOfOffset( offset ) == getLineOfOffset( getSelectionStart() );
		} catch( BadLocationException ex ) {
			return false;
		}
	}
}
