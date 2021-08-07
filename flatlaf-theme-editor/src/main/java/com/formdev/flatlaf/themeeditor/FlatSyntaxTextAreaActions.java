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
}
