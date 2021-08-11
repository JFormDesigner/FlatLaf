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

import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaUI;

/**
 * @author Karl Tauber
 */
class FlatRSyntaxTextAreaUI
	extends RSyntaxTextAreaUI
{
	FlatRSyntaxTextAreaUI( JComponent rSyntaxTextArea ) {
		super( rSyntaxTextArea );
	}

	@Override
	protected void paintCurrentLineHighlight( Graphics g, Rectangle visibleRect ) {
		if( !textArea.getHighlightCurrentLine() )
			return;

		// paint current line highlight always in the line where the caret is
		try {
			int dot = textArea.getCaret().getDot();
			Rectangle dotRect = textArea.modelToView( dot );
			int height = textArea.getLineHeight();

			g.setColor( textArea.getCurrentLineHighlightColor() );
			g.fillRect( visibleRect.x, dotRect.y, visibleRect.width, height );
		} catch( BadLocationException ex ) {
			super.paintCurrentLineHighlight( g, visibleRect );
		}
	}
}
