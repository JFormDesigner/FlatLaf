/*
 * Copyright 2021 FormDev Software GmbH
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

import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RecordableTextAction;

/**
 * @author Karl Tauber
 */
class FlatSyntaxTextAreaActions
{
	static final String duplicateLinesUpAction = "FlatLaf.DuplicateLinesUpAction";
	static final String duplicateLinesDownAction = "FlatLaf.DuplicateLinesDownAction";
	static final String incrementNumberAction = "FlatLaf.IncrementNumberAction";
	static final String decrementNumberAction = "FlatLaf.DecrementNumberAction";

	//---- class DuplicateLinesAction -----------------------------------------

	static class DuplicateLinesAction
		extends RecordableTextAction
	{
		private final boolean up;

		public DuplicateLinesAction( String name, boolean up ) {
			super( name );
			this.up = up;
		}

		@Override
		public void actionPerformedImpl( ActionEvent e, RTextArea textArea ) {
			try {
				int selStartLine = textArea.getLineOfOffset( textArea.getSelectionStart() );
				int selEndLine = textArea.getLineOfOffset( textArea.getSelectionEnd() );

				int linesStart = textArea.getLineStartOffset( selStartLine );
				int linesEnd = textArea.getLineEndOffset( selEndLine );

				String linesText = textArea.getText( linesStart, linesEnd - linesStart );
				if( !linesText.endsWith( "\n" ) )
					linesText += "\n";

				textArea.replaceRange( linesText, linesStart, linesStart );

				if( up )
					textArea.select( linesStart, linesStart + linesText.length() - 1 );
				else {
					int newSelStart = linesStart + linesText.length();
					int newSelEnd = newSelStart + linesText.length();
					if( linesText.endsWith( "\n" ) )
						newSelEnd--;
					textArea.select( newSelStart, newSelEnd );
				}
			} catch( BadLocationException ex ) {
				ex.printStackTrace();
			}
		}

		@Override
		public String getMacroID() {
			return getName();
		}
	}

	//---- class IncrementNumberAction ----------------------------------------

	static class IncrementNumberAction
		extends RecordableTextAction
	{
		private final boolean increment;

		IncrementNumberAction( String name, boolean increment ) {
			super( name );
			this.increment = increment;
		}

		@Override
		public void actionPerformedImpl( ActionEvent e, RTextArea textArea ) {
			if( !incrementRGBColor( textArea ) )
				incrementNumber( textArea );
		}

		private void incrementNumber( RTextArea textArea ) {
			try {
				int caretPosition = textArea.getCaretPosition();
				int start = caretPosition;
				int end = caretPosition;

				// find first digit
				for( int i = caretPosition - 1; i >= 0; i-- ) {
					if( !Character.isDigit( textArea.getText( i, 1 ).charAt( 0 ) ) )
						break;
					start = i;
				}

				// find last digit
				int length = textArea.getDocument().getLength();
				for( int i = caretPosition; i < length; i++ ) {
					if( !Character.isDigit( textArea.getText( i, 1 ).charAt( 0 ) ) )
						break;
					end = i + 1;
				}

				if( start == end )
					return;

				// parse number
				String str = textArea.getText( start, end - start );
				long number = Long.parseLong( str );

				// increment/decrement number
				if( increment )
					number++;
				else
					number--;

				if( number < 0 )
					return;

				// update editor
				textArea.replaceRange( Long.toString( number ), start, end );
			} catch( BadLocationException | IndexOutOfBoundsException | NumberFormatException ex ) {
				ex.printStackTrace();
			}
		}

		private boolean incrementRGBColor( RTextArea textArea ) {
			try {
				int caretPosition = textArea.getCaretPosition();
				int start = caretPosition;
				int end = caretPosition;

				// find first '#' or hex digit
				for( int i = caretPosition - 1; i >= 0; i-- ) {
					char ch = textArea.getText( i, 1 ).charAt( 0 );
					if( ch != '#' && !isHexDigit( ch ) )
						break;
					start = i;
				}

				// find last hex digit
				int length = textArea.getDocument().getLength();
				for( int i = caretPosition; i < length; i++ ) {
					if( !isHexDigit( textArea.getText( i, 1 ).charAt( 0 ) ) )
						break;
					end = i + 1;
				}

				// check for valid length (#RGB, #RGBA, #RRGGBB or #RRGGBBAA)
				int len = end - start;
				if( len != 4 && len != 5 && len != 7 && len != 9 )
					return false;

				// check whether starts with '#'
				if( textArea.getText( start, 1 ).charAt( 0 ) != '#' )
					return false;

				// find start of color part that should be changed (red, green, blue or alpha)
				int start2;
				int hexDigitCount = (len == 4 || len == 5) ? 1 : 2;
				if( hexDigitCount == 1 ) {
					// #RGB or #RGBA
					start2 = caretPosition - 1;
				} else {
					// #RRGGBB or #RRGGBBAA
					int offset = caretPosition - (start + 1);
					offset += (offset % 2);
					start2 = start + 1 + offset - 2;
				}
				start2 = Math.max( start2, start + 1 );

				// parse number
				String str = textArea.getText( start2, hexDigitCount );
				int number = Integer.parseInt( str, 16 );

				// increment/decrement number
				if( increment )
					number++;
				else
					number--;

				// wrap numbers if less than zero or too large
				int maxNumber = (hexDigitCount == 1) ? 15 : 255;
				if( number < 0 )
					number = maxNumber;
				else if( number > maxNumber )
					number = 0;

				// update editor
				String newStr = String.format( hexDigitCount == 1 ? "%1x" : "%02x", number );
				textArea.replaceRange( newStr, start2, start2 + hexDigitCount );
				return true;
			} catch( BadLocationException | IndexOutOfBoundsException | NumberFormatException ex ) {
				ex.printStackTrace();
				return false;
			}
		}

		private boolean isHexDigit( char ch ) {
			return Character.isDigit( ch ) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
		}

		@Override
		public String getMacroID() {
			return getName();
		}
	}
}
