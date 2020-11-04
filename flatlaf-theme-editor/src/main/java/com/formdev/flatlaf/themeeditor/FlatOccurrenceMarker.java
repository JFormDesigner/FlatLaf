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
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;
import org.fife.ui.rtextarea.SmartHighlightPainter;

/**
 * Delegating occurrence marker that does not mark token at caret if it does
 * not occur elsewhere.
 *
 * @author Karl Tauber
 */
class FlatOccurrenceMarker
	implements OccurrenceMarker
{
	private final OccurrenceMarker delegate;

	FlatOccurrenceMarker( OccurrenceMarker delegate ) {
		this.delegate = delegate;
	}

	@Override
	public Token getTokenToMark( RSyntaxTextArea textArea ) {
		return delegate.getTokenToMark( textArea );
	}

	@Override
	public boolean isValidType( RSyntaxTextArea textArea, Token t ) {
		return delegate.isValidType( textArea, t );
	}

	@Override
	public void markOccurrences( RSyntaxDocument doc, Token t, RSyntaxTextAreaHighlighter h, SmartHighlightPainter p ) {
		char[] lexeme = t.getLexeme().toCharArray();
		int type = t.getType();
		int lineCount = doc.getDefaultRootElement().getElementCount();

		// make a copy of the token because it is overwritten in getTokenListForLine()
		Token t2 = new TokenImpl( t );

		// check whether token occurs more than once
		boolean mark = false;
		for( int i = 0; i < lineCount && !mark; i++ ) {
			Token temp = doc.getTokenListForLine( i );
			while( temp != null && temp.isPaintable() ) {
				if( temp.is( type, lexeme ) && temp.getOffset() != t2.getOffset() ) {
					mark = true;
					break;
				}
				temp = temp.getNextToken();
			}
		}

		if( mark )
			delegate.markOccurrences( doc, t2, h, p );
	}
}
