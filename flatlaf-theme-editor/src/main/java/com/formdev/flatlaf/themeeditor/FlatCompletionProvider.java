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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.CompletionProviderBase;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterChoicesProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import com.formdev.flatlaf.FlatLaf;

/**
 * @author Karl Tauber
 */
class FlatCompletionProvider
	extends CompletionProviderBase
{
	private KeyCompletionProvider keyProvider;
	private ReferenceCompletionProvider referenceProvider;
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
	public boolean isAutoActivateOkay( JTextComponent comp ) {
		CompletionProvider provider = getProviderFor( comp );
		return (provider != null) ? provider.isAutoActivateOkay( comp ) : false;
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
					case ',':
						return getValueProvider();

					case '$':
					case '@':
						return getReferenceProvider();

					case '#': // colors
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

	private CompletionProvider getReferenceProvider() {
		if( referenceProvider == null )
			referenceProvider = new ReferenceCompletionProvider();
		return referenceProvider;
	}

	private CompletionProvider getValueProvider() {
		if( valueProvider == null )
			valueProvider = new ValueCompletionProvider( getReferenceProvider() );
		return valueProvider;
	}

	//---- class KeyCompletionProvider ----------------------------------------

	/**
	 * A completion provider for keys, which always uses all known/predefined keys.
	 */
	private static final class KeyCompletionProvider
		extends BaseCompletionProvider
	{
		private static KeyCompletionProvider instance;

		private final Set<String> knownKeys;
		private Set<String> lastKeys;

		static KeyCompletionProvider getInstance() {
			if( instance == null )
				instance = new KeyCompletionProvider();
			return instance;
		}

		KeyCompletionProvider() {
			setAutoActivationRules( true, "@." );

			knownKeys = new HashSet<>();
			try {
				try( InputStream in = getClass().getResourceAsStream( "/com/formdev/flatlaf/themeeditor/FlatLafUIKeys.txt" ) ) {
					if( in != null ) {
						try( BufferedReader reader = new BufferedReader( new InputStreamReader( in, "UTF-8" ) ) ) {
							String key;
							while( (key = reader.readLine()) != null ) {
								if( !isIgnored( key ) )
									knownKeys.add( key );
							}
						}
					}
				}
			} catch( IOException ex ) {
				ex.printStackTrace(); // TODO
			}

			setCompletions( knownKeys );
		}

		private boolean isIgnored( String key ) {
			return key.endsWith( "InputMap" );
		}

		private void setCompletions( Set<String> keys ) {
			HashSet<String> keyParts = new HashSet<>();
			for( String key : keys ) {
				int delimIndex = key.length() + 1;
				while( (delimIndex = key.lastIndexOf( '.', delimIndex - 1 )) >= 0 )
					keyParts.add( key.substring( 0, delimIndex ) );
			}

			completions.clear();

			// add key parts
			for( String key : keyParts )
				completions.add( new BasicCompletion( this, key ) );

			// add all keys
			for( String key : keys ) {
				if( !keyParts.contains( key ) )
					completions.add( new BasicCompletion( this, key.concat( " = " ) ) );
			}

			Collections.sort( completions );
		}

		@Override
		protected boolean isValidChar( char ch ) {
			return super.isValidChar( ch ) || ch == '.' || ch == '@';
		}

		@Override
		protected List<Completion> getCompletionsImpl( JTextComponent comp ) {
			updateCompletions( comp );
			return super.getCompletionsImpl( comp );
		}

		@Override
		public List<Completion> getCompletionsAt( JTextComponent comp, Point pt ) {
			updateCompletions( comp );
			return super.getCompletionsAt( comp, pt );
		}

		@Override
		public List<ParameterizedCompletion> getParameterizedCompletions( JTextComponent comp ) {
			updateCompletions( comp );
			return super.getParameterizedCompletions( comp );
		}

		private void updateCompletions( JTextComponent comp ) {
			FlatSyntaxTextArea fsta = (FlatSyntaxTextArea) comp;
			Set<String> keys = fsta.propertiesSupport.getAllKeys();
			if( Objects.equals( keys, lastKeys ) )
				return;
			lastKeys = keys;

			// get key at current line
			String keyAtCurrentLine = null;
			try {
				int caretPosition = fsta.getCaretPosition();
				int currentLine = fsta.getLineOfOffset( caretPosition );
				int lineStart = fsta.getLineStartOffset( currentLine );
				int lineEnd = fsta.getLineEndOffset( currentLine );
				String line = fsta.getText( lineStart, lineEnd - lineStart );
				Properties properties = new Properties();
				properties.load( new StringReader( line ) );
				if( !properties.isEmpty() )
					keyAtCurrentLine = (String) properties.keys().nextElement();
			} catch( BadLocationException | IOException ex ) {
				ex.printStackTrace();
			}

			Set<String> completionKeys = new HashSet<>( knownKeys );
			for( String key : keys ) {
				if( key.startsWith( "*." ) || key.startsWith( "[" ) )
					continue;

				// ignore key at current line
				if( key.equals( keyAtCurrentLine ) )
					continue;

				completionKeys.add( key );
			}
			setCompletions( completionKeys );
		}
	}

	//---- class BaseCompletionProvider ---------------------------------------

	//TODO remove if https://github.com/bobbylight/AutoComplete/issues/77 is fixed
	private static class BaseCompletionProvider
		extends DefaultCompletionProvider
	{
		private boolean autoActivateAfterLetters;
		private String autoActivateChars;

		@Override
		public boolean isAutoActivateOkay( JTextComponent comp ) {
			int caretPosition = comp.getCaretPosition();
			if( caretPosition <= 0 )
				return false;

			try {
				char ch = comp.getText( caretPosition - 1, 1 ).charAt( 0 );
				return (autoActivateAfterLetters && Character.isLetter( ch )) ||
					(autoActivateChars != null && autoActivateChars.indexOf( ch ) >= 0);
			} catch( BadLocationException | IndexOutOfBoundsException ex ) {
				// ignore
				return false;
			}
		}

		@Override
		public void setAutoActivationRules( boolean letters, String others ) {
			autoActivateAfterLetters = letters;
			autoActivateChars = others;
		}
	}

	//---- class ReferenceCompletionProvider ----------------------------------

	/**
	 * A completion provider for references within values. Only keys defined
	 * in current properties file and in base properties files are used.
	 */
	private static class ReferenceCompletionProvider
		extends BaseCompletionProvider
	{
		private Set<String> lastKeys;

		ReferenceCompletionProvider() {
			setAutoActivationRules( true, "$@." );
		}

		@Override
		protected boolean isValidChar( char ch ) {
			return super.isValidChar( ch ) || ch == '.' || ch == '$' || ch == '@';
		}

		@Override
		protected List<Completion> getCompletionsImpl( JTextComponent comp ) {
			updateCompletions( comp );
			return super.getCompletionsImpl( comp );
		}

		@Override
		public List<Completion> getCompletionsAt( JTextComponent comp, Point pt ) {
			updateCompletions( comp );
			return super.getCompletionsAt( comp, pt );
		}

		@Override
		public List<ParameterizedCompletion> getParameterizedCompletions( JTextComponent comp ) {
			updateCompletions( comp );
			return super.getParameterizedCompletions( comp );
		}

		private void updateCompletions( JTextComponent comp ) {
			FlatSyntaxTextArea fsta = (FlatSyntaxTextArea) comp;
			Set<String> keys = fsta.propertiesSupport.getAllKeys();
			if( Objects.equals( keys, lastKeys ) )
				return;
			lastKeys = keys;

			completions.clear();
			outer:
			for( String key : keys ) {
				while( key.startsWith( "[" ) ) {
					// remove prefix
					int closeIndex = key.indexOf( ']' );
					if( closeIndex < 0 )
						continue outer;

					String prefix = key.substring( 0, closeIndex + 1 );
					if( FlatLaf.getUIKeySpecialPrefixes().contains( prefix ) )
						continue outer; // can not reference properties with special prefix

					key = key.substring( closeIndex + 1 );
				}

				if( key.startsWith( "*." ) ) {
					// resolve wildcard
					for( String k : FlatThemePropertiesSupport.getKeysForWildcard( key ) ) {
						if( !keys.contains( k ) )
							completions.add( new BasicCompletion( this, "$".concat( k ) ) );
					}
					continue;
				}

				if( !key.startsWith( "@" ) )
					key = "$".concat( key );

				BasicCompletion completion = new BasicCompletion( this, key );
				if( key.startsWith( "@" ) )
					completion.setRelevance( 1 );
				completions.add( completion );
			}
			Collections.sort( completions );
		}
	}

	//---- class ValueCompletionProvider --------------------------------------

	/**
	 * A completion provider for values.
	 */
	private static class ValueCompletionProvider
		extends BaseCompletionProvider
		implements ParameterChoicesProvider
	{
		ValueCompletionProvider( CompletionProvider parent ) {
			setParent( parent );
			setAutoActivationRules( true, null );
			setParameterizedCompletionParams( '(', ",", ')' );
			setParameterChoicesProvider( this );

			addFunction( "if",
				"condition", "evaluates to true if: is not \"null\" and is not \"false\" and is not an integer with zero value",
				"trueValue", "used if condition is true",
				"falseValue", "used if condition is false" );
			addFunction( "lazy",
				"uiKey", "UI key (without leading '$')" );

			addFunction( "systemColor",
				"name", "system color name",
				"defaultValue", "default color value used if system color is not available" );

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

			String colorParamDesc = "a color (e.g. #f00), a reference (e.g. $Other.key) or a color function";
			String[] hslIncreaseDecreaseParams = {
				"color", colorParamDesc,
				"amount", "0-100%",
				"options", "(optional) [relative] [autoInverse] [noAutoInverse] [lazy] [derived]"
			};
			addFunction( "lighten", hslIncreaseDecreaseParams );
			addFunction( "darken", hslIncreaseDecreaseParams );
			addFunction( "saturate", hslIncreaseDecreaseParams );
			addFunction( "desaturate", hslIncreaseDecreaseParams );
			addFunction( "fadein", hslIncreaseDecreaseParams );
			addFunction( "fadeout", hslIncreaseDecreaseParams );

			addFunction( "fade",
				"color", colorParamDesc,
				"amount", "0-100%",
				"options", "(optional) [derived]" );
			addFunction( "spin",
				"color", colorParamDesc,
				"angle", "number of degrees to rotate (0-360)",
				"options", "(optional) [derived]" );

			addFunction( "changeHue",
				"color", colorParamDesc,
				"angle", "number of degrees (0-360)",
				"options", "(optional) [derived]" );
			String[] hslChangeParams = {
				"color", colorParamDesc,
				"value", "0-100%",
				"options", "(optional) [derived]"
			};
			addFunction( "changeSaturation", hslChangeParams );
			addFunction( "changeLightness", hslChangeParams );
			addFunction( "changeAlpha", hslChangeParams );

			String weightParamDesc = "(optional) 0-100%, default is 50%";
			addFunction( "mix",
				"color1", colorParamDesc,
				"color2", colorParamDesc,
				"weight", weightParamDesc );
			addFunction( "tint",
				"color", colorParamDesc,
				"weight", weightParamDesc );
			addFunction( "shade",
				"color", colorParamDesc,
				"weight", weightParamDesc );

			addFunction( "contrast",
				"color", colorParamDesc,
				"dark", colorParamDesc,
				"light", colorParamDesc,
				"threshold", "(optional) 0-100%, default is 43%" );

			addFunction( "over",
				"foreground", colorParamDesc,
				"background", colorParamDesc );
		}

		private void addFunction( String name, String... paramNamesAndDescs ) {
			List<Parameter> params = new ArrayList<>();
			for( int i = 0; i < paramNamesAndDescs.length; i += 2 ) {
//				boolean endParam = i + 2 >= paramNamesAndDescs.length;
				boolean endParam = false;
				Parameter param = new Parameter( null, paramNamesAndDescs[i], endParam );
				param.setDescription( paramNamesAndDescs[i + 1] );
				params.add( param );
			}

			FunctionCompletion f = new FunctionCompletion( this, name, null ) {
				@Override
				public String toString() {
					return getDefinitionString().replace( "(", " (" ).replace( ",", ", " );
				}
			};

			f.setParams( params );
			f.setRelevance( 10 );
			addCompletion( f );
		}

		@Override
		public List<Completion> getParameterChoices( JTextComponent tc, Parameter param ) {
			switch( param.getName() ) {
				case "amount":
					return createParameterChoices( "5%", "10%", "15%", "20%", "25%" );

				case "options":
					return createParameterChoices( "relative", "autoInverse", "noAutoInverse", "lazy", "derived" );
			}

			return null;
		}

		private List<Completion> createParameterChoices( String... values ) {
			List<Completion> result = new ArrayList<>();
			for( String value : values )
				result.add( new BasicCompletion( this, value ) );
			return result;
		}
	}
}
