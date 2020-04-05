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

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.JPanel;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;
import com.formdev.flatlaf.util.UIScale;

/**
 * A pane that supports editing FlatLaf themes.
 *
 * @author Karl Tauber
 */
class FlatThemeEditorPane
	extends JPanel
{
	private static final String FLATLAF_STYLE = "text/flatlaf";

	private final RTextScrollPane scrollPane;
	private final FlatSyntaxTextArea textArea;

	FlatThemeEditorPane() {
		super( new BorderLayout() );

		// register FlatLaf token maker
		AbstractTokenMakerFactory tmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		tmf.putMapping( FLATLAF_STYLE, FlatThemeTokenMaker.class.getName() );

		// create text area
		textArea = new FlatSyntaxTextArea();
		textArea.setSyntaxEditingStyle( FLATLAF_STYLE );

		// theme
		try {
			Theme theme = Theme.load( getClass().getResourceAsStream( "light.xml" ) );
			theme.apply( textArea );
		} catch( IOException ex ) {
			ex.printStackTrace();
		}

		// create scroll pane
		scrollPane = new RTextScrollPane( textArea );
		scrollPane.setLineNumbersEnabled( true );

		// scale fonts
		if( UIScale.getUserScaleFactor() != 1 ) {
			textArea.setFont( scaleFont( textArea.getFont() ) );
			scrollPane.getGutter().setLineNumberFont( scaleFont( scrollPane.getGutter().getLineNumberFont() ) );
		}

		add( scrollPane, BorderLayout.CENTER );
	}

	private static Font scaleFont( Font font ) {
		int newFontSize = UIScale.scale( font.getSize() );
		return font.deriveFont( (float) newFontSize );
	}

	public void load( FileLocation loc ) throws IOException {
		textArea.load( loc, StandardCharsets.ISO_8859_1 );
	}
}
