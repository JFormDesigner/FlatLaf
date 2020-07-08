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

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.CompletionProviderBase;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * @author Karl Tauber
 */
class FlatCompletionProvider
	extends CompletionProviderBase
{
	private KeyCompletionProvider keyProvider;
	private ValueCompletionProvider valueProvider;

	FlatCompletionProvider() {
	}

	@Override
	public String getAlreadyEnteredText( JTextComponent comp ) {
		CompletionProvider provider = getProviderFor( comp );
		return (provider != null) ? provider.getAlreadyEnteredText( comp ) : null;
	}

	@Override
	public List<Completion> getCompletionsAt( JTextComponent comp, Point p ) {
		CompletionProvider provider = getProviderFor( comp );
		return (provider != null) ? provider.getCompletionsAt( comp, p ) : null;
	}

	@Override
	public List<ParameterizedCompletion> getParameterizedCompletions( JTextComponent comp ) {
		CompletionProvider provider = getProviderFor( comp );
		return (provider != null) ? provider.getParameterizedCompletions( comp ) : null;
	}

	@Override
	protected List<Completion> getCompletionsImpl( JTextComponent comp ) {
		CompletionProvider provider = getProviderFor( comp );
		return (provider != null) ? provider.getCompletions( comp ) : null;
	}

	@Override
	public boolean isAutoActivateOkay( JTextComponent tc ) {
		int caretPosition = tc.getCaretPosition();
		if( caretPosition <= 0 )
			return false;

		try {
			char ch = tc.getText( caretPosition - 1, 1 ).charAt( 0 );
			return ch == '$';
		} catch( BadLocationException | IndexOutOfBoundsException ex ) {
			// ignore
			return false;
		}
	}

	private CompletionProvider getProviderFor( JTextComponent comp ) {
		RSyntaxTextArea rsta = (RSyntaxTextArea) comp;
		try {
			int caretPosition = rsta.getCaretPosition();
			int currentLine = rsta.getLineOfOffset( caretPosition );
			int lineStart = rsta.getLineStartOffset( currentLine );
			int lineEnd = rsta.getLineEndOffset( currentLine );

			if( caretPosition <= lineStart ) {
				// caret is at the start of the line
				String line = rsta.getText( lineStart, lineEnd - lineStart );
				if( line.trim().startsWith( "#" ) )
					return null;
			}

			String lineBeforeCaret = rsta.getText( lineStart, caretPosition - lineStart );
			if( lineBeforeCaret.trim().startsWith( "#" ) )
				return null;

			// key
			if( lineBeforeCaret.indexOf( '=' ) < 0 )
				return getKeyProvider();

			// value
			for( int i = lineBeforeCaret.length() - 1; i >= 0; i-- ) {
				switch( lineBeforeCaret.charAt( i ) ) {
					case '=':
					case '(':
						return getValueProvider();

					case '$':
						return getKeyProvider();

					case ' ':
					case '\t':
						return null;
				}
			}
			return null;

		} catch( BadLocationException ex ) {
			// ignore
			return null;
		}
	}

	private CompletionProvider getKeyProvider() {
		if( keyProvider == null )
			keyProvider = KeyCompletionProvider.getInstance();
		return keyProvider;
	}

	private CompletionProvider getValueProvider() {
		if( valueProvider == null )
			valueProvider = new ValueCompletionProvider();
		return valueProvider;
	}

	//---- class KeyCompletionProvider ----------------------------------------

	private static class KeyCompletionProvider
		extends DefaultCompletionProvider
	{
		private static KeyCompletionProvider instance;

		static KeyCompletionProvider getInstance() {
			if( instance == null )
				instance = new KeyCompletionProvider();
			return instance;
		}

		KeyCompletionProvider() {
			// load all keys
			HashSet<String> keys = new HashSet<>();
			try {
				try( InputStream in = getClass().getResourceAsStream( "/com/formdev/flatlaf/themeeditor/FlatLafUIKeys.txt" ) ) {
					if( in != null ) {
						try( BufferedReader reader = new BufferedReader( new InputStreamReader( in, "UTF-8" ) ) ) {
							String key;
							while( (key = reader.readLine()) != null ) {
								keys.add( key );
							}
						}
					}
				}
			} catch( IOException ex ) {
				ex.printStackTrace(); // TODO
			}

			// collect key parts
			HashSet<String> keyParts = new HashSet<>();
			for( String key : keys ) {
				int delimIndex = key.length() + 1;
				while( (delimIndex = key.lastIndexOf( '.', delimIndex - 1 )) >= 0 )
					keyParts.add( key.substring( 0, delimIndex + 1 ) );
			}

			// add key parts
			addWordCompletions( keyParts.toArray( new String[keyParts.size()] ) );

			// add all keys
			addWordCompletions( keys.toArray( new String[keys.size()] ) );
		}

		@Override
		protected boolean isValidChar( char ch ) {
			return super.isValidChar( ch ) || ch == '.';
		}
	}

	//---- class ValueCompletionProvider --------------------------------------

	private static class ValueCompletionProvider
		extends DefaultCompletionProvider
	{
		ValueCompletionProvider() {
			setParameterizedCompletionParams( '(', ", ", ')' );

			addFunction( "rgb",
				"red", "0-255 or 0-100%",
				"green", "0-255 or 0-100%",
				"blue", "0-255 or 0-100%" );
			addFunction( "rgba",
				"red", "0-255 or 0-100%",
				"green", "0-255 or 0-100%",
				"blue", "0-255 or 0-100%",
				"alpha", "0-255 or 0-100%" );

			addFunction( "hsl",
				"hue", "0-360 representing degrees",
				"saturation", "0-100%",
				"lightness",  "0-100%" );
			addFunction( "hsla",
				"hue", "0-360 representing degrees",
				"saturation", "0-100%",
				"lightness",  "0-100%",
				"alpha", "0-100%" );

			String[] hslIncreaseDecreaseParams = {
				"color", "a color (e.g. #f00), a reference (e.g. $Other.key) or a color function",
				"amount", "0-100%",
				"options", "(optional) [relative] [autoInverse] [noAutoInverse] [lazy] [derived]"
			};
			addFunction( "lighten", hslIncreaseDecreaseParams );
			addFunction( "darken", hslIncreaseDecreaseParams );
			addFunction( "saturate", hslIncreaseDecreaseParams );
			addFunction( "desaturate", hslIncreaseDecreaseParams );
		}

		private void addFunction( String name, String... paramNamesAndDescs ) {
			List<Parameter> params = new ArrayList<>();
			for( int i = 0; i < paramNamesAndDescs.length; i += 2 ) {
				boolean endParam = i + 2 >= paramNamesAndDescs.length;
				Parameter param = new Parameter( null, paramNamesAndDescs[i], endParam );
				param.setDescription( paramNamesAndDescs[i + 1] );
				params.add( param );
			}

			FunctionCompletion f = new FunctionCompletion( this, name, null ) {
				@Override
				public String toString() {
					return getDefinitionString().replace( "(", " (" );
				}
			};

			f.setParams( params );
			addCompletion( f );
		}
	}
}
