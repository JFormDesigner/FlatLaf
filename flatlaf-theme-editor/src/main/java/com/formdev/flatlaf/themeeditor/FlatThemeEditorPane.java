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
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
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
	static final String DIRTY_PROPERTY = TextEditorPane.DIRTY_PROPERTY;

	private static final String FLATLAF_STYLE = "text/flatlaf";

	private final CollapsibleSectionPanel collapsiblePanel;
	private final RTextScrollPane scrollPane;
	private final FlatSyntaxTextArea textArea;
	private FlatFindReplaceBar findReplaceBar;

	private File file;

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

		textArea.addPropertyChangeListener( TextEditorPane.DIRTY_PROPERTY, e -> {
			firePropertyChange( DIRTY_PROPERTY, e.getOldValue(), e.getNewValue() );
		} );

		// theme
		try( InputStream in = getClass().getResourceAsStream( "light.xml" ) ) {
			Theme theme = Theme.load( in );
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
		ac.setAutoCompleteSingleChoices( false );
		ac.setAutoActivationEnabled( true );
		ac.setParameterAssistanceEnabled( true );
		ac.setChoicesWindowSize( UIScale.scale( 300 ), UIScale.scale( 400 ) );
		ac.setDescriptionWindowSize( UIScale.scale( 300 ), UIScale.scale( 400 ) );
		ac.install( textArea );

		// create overlay layer
		JLayer<FlatSyntaxTextArea> overlay = new JLayer<>( textArea, new FlatThemeEditorOverlay() );

		// create scroll pane
		scrollPane = new RTextScrollPane( overlay );
		scrollPane.setBorder( null );
		scrollPane.setLineNumbersEnabled( true );

		// scale fonts
		if( UIScale.getUserScaleFactor() != 1 )
			textArea.setFont( scaleFont( textArea.getFont() ) );

		// use same font for line numbers as in editor
		scrollPane.getGutter().setLineNumberFont( textArea.getFont() );

		// create error strip
		ErrorStrip errorStrip = new ErrorStrip( textArea );

		// create collapsible panel
		collapsiblePanel = new CollapsibleSectionPanel();
		collapsiblePanel.add( scrollPane );
		collapsiblePanel.add( errorStrip, BorderLayout.LINE_END );
		add( collapsiblePanel, BorderLayout.CENTER );
	}

	private static Font scaleFont( Font font ) {
		int newFontSize = UIScale.scale( font.getSize() );
		return font.deriveFont( (float) newFontSize );
	}

	void setBaseFiles( List<File> baseFiles ) {
		textArea.propertiesSupport.setBaseFiles( baseFiles );
	}

	File getFile() {
		return file;
	}

	void load( File file ) throws IOException {
		this.file = file;

		textArea.load( FileLocation.create( file ), StandardCharsets.ISO_8859_1 );
	}

	boolean reloadIfNecessary() {
		if( !file.isFile() ) {
			if( textArea.isDirty() ) {
				if( JOptionPane.showOptionDialog( this,
					"The file '" + textArea.getFileName()
					+ "' has been deleted. Replace the editor contents with these changes?",
					getWindowTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, new Object[] { "Save", "Close" }, "Save" ) == JOptionPane.YES_OPTION )
				{
					saveIfDirty();
					return true;
				}
			}

			return false;
		}

		if( textArea.isModifiedOutsideEditor() ) {
			if( textArea.isDirty() ) {
				if( JOptionPane.showConfirmDialog( this,
					"The file '" + textArea.getFileName()
					+ "' has been changed. Replace the editor contents with these changes?",
					getWindowTitle(), JOptionPane.YES_NO_OPTION ) != JOptionPane.YES_OPTION )
				{
					textArea.syncLastSaveOrLoadTimeToActualFile();
					return true;
				}
			}

			try {
				textArea.reload();
			} catch( IOException ex ) {
				JOptionPane.showMessageDialog( this,
					"Failed to reload '" + textArea.getFileName() + "'\n\nReason: " + ex.getMessage(),
					getWindowTitle(), JOptionPane.WARNING_MESSAGE );
			}
		}

		return true;
	}

	boolean saveIfDirty() {
		try {
			if( textArea.isDirty() )
				textArea.save();
			return true;
		} catch( IOException ex ) {
			JOptionPane.showMessageDialog( this,
				"Failed to save '" + textArea.getFileName() + "'\n\nReason: " + ex.getMessage(),
				getWindowTitle(), JOptionPane.WARNING_MESSAGE );
			return false;
		}
	}

	boolean isDirty() {
		return textArea.isDirty();
	}

	private String getWindowTitle() {
		Window window = SwingUtilities.windowForComponent( this );
		return (window instanceof JFrame) ? ((JFrame)window).getTitle() : null;
	}

	void showFindReplaceBar() {
		if( findReplaceBar == null ) {
			findReplaceBar = new FlatFindReplaceBar( textArea );
			findReplaceBar.setBorder( new MatteBorder( 1, 0, 0, 0,
				UIManager.getColor( "Component.borderColor" ) ) );
			collapsiblePanel.addBottomComponent( findReplaceBar );
		}

		collapsiblePanel.showBottomComponent( findReplaceBar );
	}
}
