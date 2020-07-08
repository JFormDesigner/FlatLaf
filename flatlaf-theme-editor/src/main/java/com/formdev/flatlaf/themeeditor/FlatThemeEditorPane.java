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
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.swing.JLayer;
import javax.swing.JPanel;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
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
		textArea.setMarkOccurrences( true );
		textArea.addParser( new FlatThemeParser() );
//		textArea.setUseColorOfColorTokens( true );

		// theme
		try {
			Theme theme = Theme.load( getClass().getResourceAsStream( "light.xml" ) );
			theme.apply( textArea );
		} catch( IOException ex ) {
			ex.printStackTrace();
		}

		// use semitransparent token background because token background
		// is painted over mark occurrences background
		SyntaxScheme scheme = textArea.getSyntaxScheme();
		scheme.getStyle( FlatThemeTokenMaker.TOKEN_COLOR ).background = new Color( 0x0a000000, true );
		scheme.getStyle( FlatThemeTokenMaker.TOKEN_VARIABLE ).background = new Color( 0x1800cc00, true );

		// autocomplete
		CompletionProvider provider = new FlatCompletionProvider();
		AutoCompletion ac = new AutoCompletion( provider );
		ac.setAutoActivationEnabled( true );
		ac.setParameterAssistanceEnabled( true );
		ac.setChoicesWindowSize( UIScale.scale( 300 ), UIScale.scale( 400 ) );
		ac.setDescriptionWindowSize( UIScale.scale( 300 ), UIScale.scale( 400 ) );
		ac.install( textArea );

		// create overlay layer
		JLayer<FlatSyntaxTextArea> overlay = new JLayer<>( textArea, new FlatThemeEditorOverlay() );

		// create scroll pane
		scrollPane = new RTextScrollPane( overlay );
		scrollPane.setLineNumbersEnabled( true );

		// scale fonts
		if( UIScale.getUserScaleFactor() != 1 )
			textArea.setFont( scaleFont( textArea.getFont() ) );

		// use same font for line numbers as in editor
		scrollPane.getGutter().setLineNumberFont( textArea.getFont() );

		add( scrollPane, BorderLayout.CENTER );
	}

	private static Font scaleFont( Font font ) {
		int newFontSize = UIScale.scale( font.getSize() );
		return font.deriveFont( (float) newFontSize );
	}

	void setBaseFiles( List<File> baseFiles ) {
		textArea.propertiesSupport.setBaseFiles( baseFiles );
	}

	void load( FileLocation loc ) throws IOException {
		textArea.load( loc, StandardCharsets.ISO_8859_1 );
	}

	void save() {
		try {
			textArea.save();
		} catch( IOException ex ) {
			ex.printStackTrace(); // TODO
		}
	}
}
