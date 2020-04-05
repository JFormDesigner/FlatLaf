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

import javax.swing.text.Element;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import com.formdev.flatlaf.UIDefaultsLoaderAccessor;

/**
 * Parser for FlatLaf properties files that checks for invalid values.
 *
 * @author Karl Tauber
 */
class FlatThemeParser
	extends AbstractParser
{
	private final DefaultParseResult result;

	FlatThemeParser() {
		result = new DefaultParseResult( this );
	}

	@Override
	public ParseResult parse( RSyntaxDocument doc, String style ) {
		Element root = doc.getDefaultRootElement();

		result.clearNotices();
		result.setParsedLines( 0, root.getElementCount() - 1 );

		for( Token token : doc ) {
			if( token.getType() == FlatThemeTokenMaker.TOKEN_COLOR ) {
				try {
					UIDefaultsLoaderAccessor.parseColorRGBA( token.getLexeme() );
				} catch( IllegalArgumentException ex ) {
					result.addNotice( new DefaultParserNotice( this,
						"Invalid color.\n\nUse #RRGGBB, #RRGGBBAA, #RGB or #RGBA",
						root.getElementIndex( token.getOffset() ),
						token.getOffset(), token.length() ) );
				}
			}
		}

		return result;
	}
}
