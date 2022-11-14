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

import org.fife.ui.rsyntaxtextarea.OccurrenceMarker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rsyntaxtextarea.modes.PropertiesFileTokenMaker;

/**
 * Token maker for FlatLaf properties files.
 * <p>
 * Lets the super class parse the properties file and modify the added tokens.
 * The super class uses {@link TokenTypes#RESERVED_WORD} for property keys and
 * {@link TokenTypes#LITERAL_STRING_DOUBLE_QUOTE} for property values.
 *
 * @author Karl Tauber
 */
public class FlatThemeTokenMaker
	extends PropertiesFileTokenMaker
{
	static final int TOKEN_PROPERTY = Token.IDENTIFIER;
	static final int TOKEN_VARIABLE = Token.VARIABLE;
	static final int TOKEN_NUMBER = Token.LITERAL_NUMBER_DECIMAL_INT;
	static final int TOKEN_COLOR = Token.LITERAL_NUMBER_HEXADECIMAL;
	static final int TOKEN_STRING = Token.LITERAL_STRING_DOUBLE_QUOTE;
	static final int TOKEN_FUNCTION = Token.FUNCTION;
	static final int TOKEN_TYPE = Token.DATA_TYPE;

	private final TokenMap tokenMap = new TokenMap();

	public FlatThemeTokenMaker() {
		// null, false, true
		tokenMap.put( "null", Token.RESERVED_WORD );
		tokenMap.put( "false", Token.LITERAL_BOOLEAN );
		tokenMap.put( "true", Token.LITERAL_BOOLEAN );

		// general functions
		tokenMap.put( "if", TOKEN_FUNCTION );
		tokenMap.put( "lazy", TOKEN_FUNCTION );

		// color functions
		tokenMap.put( "systemColor", TOKEN_FUNCTION );
		tokenMap.put( "rgb", TOKEN_FUNCTION );
		tokenMap.put( "rgba", TOKEN_FUNCTION );
		tokenMap.put( "hsl", TOKEN_FUNCTION );
		tokenMap.put( "hsla", TOKEN_FUNCTION );
		tokenMap.put( "lighten", TOKEN_FUNCTION );
		tokenMap.put( "darken", TOKEN_FUNCTION );
		tokenMap.put( "saturate", TOKEN_FUNCTION );
		tokenMap.put( "desaturate", TOKEN_FUNCTION );
		tokenMap.put( "fadein", TOKEN_FUNCTION );
		tokenMap.put( "fadeout", TOKEN_FUNCTION );
		tokenMap.put( "fade", TOKEN_FUNCTION );
		tokenMap.put( "spin", TOKEN_FUNCTION );
		tokenMap.put( "changeHue", TOKEN_FUNCTION );
		tokenMap.put( "changeSaturation", TOKEN_FUNCTION );
		tokenMap.put( "changeLightness", TOKEN_FUNCTION );
		tokenMap.put( "changeAlpha", TOKEN_FUNCTION );
		tokenMap.put( "mix", TOKEN_FUNCTION );
		tokenMap.put( "tint", TOKEN_FUNCTION );
		tokenMap.put( "shade", TOKEN_FUNCTION );
		tokenMap.put( "contrast", TOKEN_FUNCTION );
		tokenMap.put( "over", TOKEN_FUNCTION );

		// function options
		tokenMap.put( "relative", Token.RESERVED_WORD );
		tokenMap.put( "derived", Token.RESERVED_WORD );
		tokenMap.put( "autoInverse", Token.RESERVED_WORD );
		tokenMap.put( "noAutoInverse", Token.RESERVED_WORD );
	}

	/**
	 * This method is only invoked from the super class.
	 */
	@Override
	public void addToken( char[] array, int start, int end, int tokenType, int startOffset, boolean hyperlink ) {
//		debugInputToken( array, start, end, tokenType, startOffset, hyperlink );

		// ignore invalid token
		if( end < start )
			return;

		if( tokenType == Token.RESERVED_WORD ) {
			// key
			int newTokenType = (array[start] == '@') ? TOKEN_VARIABLE : TOKEN_PROPERTY;
			super.addToken( array, start, end, newTokenType, startOffset, hyperlink );
		} else if( tokenType == Token.LITERAL_STRING_DOUBLE_QUOTE ) {
			// value
			tokenizeValue( array, start, end, startOffset );
		} else if( tokenType == Token.VARIABLE ) {
			// '{variable}'
			super.addToken( array, start, end, TOKEN_TYPE, startOffset, hyperlink );
		} else {
			// comments or operators
			super.addToken( array, start, end, tokenType, startOffset, hyperlink );
		}
	}

	private void tokenizeValue( char[] array, int start, int end, int startOffset ) {
		int newStartOffset = startOffset - start;

		int currentTokenStart = start;
		int currentTokenType = Token.NULL;
		int parenthesisLevel = 0;

		for( int i = start; i <= end; i++ ) {
			int newTokenType;
			char ch = array[i];
			if( ch <= ' ' )
				newTokenType = Token.WHITESPACE;
			else if( ch == '#' || (currentTokenType == TOKEN_COLOR && RSyntaxUtilities.isHexCharacter( ch )) )
				newTokenType = TOKEN_COLOR;
			else if( ch == '$' || (currentTokenType == TOKEN_PROPERTY && isPropertyChar( ch )) )
				newTokenType = TOKEN_PROPERTY;
			else if( ch == '@' || (currentTokenType == TOKEN_VARIABLE && isPropertyChar( ch )) )
				newTokenType = TOKEN_VARIABLE;
			else if( currentTokenType != TOKEN_STRING && (RSyntaxUtilities.isDigit( ch ) || (currentTokenType == TOKEN_NUMBER && ch == '.')) )
				newTokenType = TOKEN_NUMBER;
			else if( ch == ',' || ch == '"' || ch == '%' )
				newTokenType = TokenTypes.OPERATOR;
			else if( ch == '(' || ch == ')' )
				newTokenType = TokenTypes.SEPARATOR; // necessary for bracket matching
			else
				newTokenType = TOKEN_STRING;

			if( currentTokenType == Token.NULL )
				currentTokenType = newTokenType;
			else if( newTokenType != currentTokenType ) {
				addTokenImpl( array, currentTokenStart, i - 1, currentTokenType, newStartOffset + currentTokenStart, parenthesisLevel );
				currentTokenType = newTokenType;
				currentTokenStart = i;
			}

			if( ch == '(' )
				parenthesisLevel++;
			else if( ch == ')' )
				parenthesisLevel--;
		}

		if( currentTokenType != Token.NULL )
			addTokenImpl( array, currentTokenStart, end, currentTokenType, newStartOffset + currentTokenStart, parenthesisLevel );
	}

	private void addTokenImpl( char[] array, int start, int end, int tokenType, int startOffset, int parenthesisLevel ) {
		if( tokenType == TOKEN_PROPERTY && array[start] == '$' ) {
			// separate '$' from property token for mark occurrences to work
			super.addToken( array, start, start, TokenTypes.OPERATOR, startOffset, false );
			start++;
			startOffset++;
		} else if( tokenType == TOKEN_STRING ) {
			// check for reserved words, functions, etc
			int type = tokenMap.get( array, start, end );
			if( type != -1 )
				tokenType = type;
			else if( parenthesisLevel > 0 ) {
				// assume property reference if in function parameters
				tokenType = TOKEN_PROPERTY;
			}
		}

//		debugOutputToken( array, start, end, tokenType );
		super.addToken( array, start, end, tokenType, startOffset, false );
	}

	private boolean isPropertyChar( char ch ) {
		return RSyntaxUtilities.isLetterOrDigit( ch ) || ch == '.' || ch == '_' || ch == '-';
	}

	@Override
	protected OccurrenceMarker createOccurrenceMarker() {
		return new FlatOccurrenceMarker( super.createOccurrenceMarker() );
	}

	@Override
	public boolean getMarkOccurrencesOfTokenType( int type ) {
		switch( type ) {
			case TOKEN_PROPERTY:
			case TOKEN_VARIABLE:
			case TOKEN_COLOR:
			case TOKEN_FUNCTION:
			case TOKEN_TYPE:
				return true;

			default:
				return false;
		}
	}

	@Override
	public boolean isIdentifierChar( int languageIndex, char ch ) {
		return super.isIdentifierChar( languageIndex, ch ) || ch == '@';
	}

/*debug
	private java.util.HashMap<Integer, String> tokenTypeStrMap;

	private void debugInputToken( char[] array, int start, int end, int tokenType, int startOffset, boolean hyperlink ) {
		if( tokenTypeStrMap == null ) {
			tokenTypeStrMap = new java.util.HashMap<>();
			for( java.lang.reflect.Field f : TokenTypes.class.getFields() ) {
				try {
					tokenTypeStrMap.put( (Integer) f.get( null ), f.getName() );
				} catch( IllegalArgumentException | IllegalAccessException ex ) {
					ex.printStackTrace();
				}
			}
		}

		String tokenTypeStr = tokenTypeStrMap.computeIfAbsent( tokenType, t -> {
			return "(unknown " + t + ")";
		} );

		System.out.printf( "%d-%d (%d)  %-30s '%s'\n",
			start, end, end - start, tokenTypeStr, new String( array, start, end - start + 1 ) );
	}

	private void debugOutputToken( char[] array, int start, int end, int tokenType ) {
		String tokenTypeStr = null;
		switch( tokenType ) {
			case TOKEN_PROPERTY: tokenTypeStr = "PROPERTY"; break;
			case TOKEN_VARIABLE: tokenTypeStr = "VARIABLE"; break;
			case TOKEN_NUMBER: tokenTypeStr = "NUMBER"; break;
			case TOKEN_COLOR: tokenTypeStr = "COLOR"; break;
			case TOKEN_STRING: tokenTypeStr = "STRING"; break;
			case TOKEN_FUNCTION: tokenTypeStr = "FUNCTION"; break;
			case TOKEN_TYPE: tokenTypeStr = "TYPE"; break;
			case TokenTypes.OPERATOR: tokenTypeStr = "OPERATOR"; break;
			case TokenTypes.WHITESPACE: tokenTypeStr = "WHITESPACE"; break;
			case TokenTypes.LITERAL_BOOLEAN: tokenTypeStr = "BOOLEAN"; break;
			case TokenTypes.RESERVED_WORD: tokenTypeStr = "RESERVED_WORD"; break;
			default:
				throw new IllegalArgumentException( String.valueOf( tokenType ) );
		}

		System.out.printf( "    %d-%d (%d)  %-15s '%s'\n",
			start, end, end - start, tokenTypeStr, new String( array, start, end - start + 1 ) );
	}
debug*/
}
