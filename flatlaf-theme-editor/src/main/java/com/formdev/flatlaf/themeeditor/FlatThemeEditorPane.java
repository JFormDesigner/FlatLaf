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
import java.nio.charset.StandardCharsets;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextArea;
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
	private final ErrorStrip errorStrip;
	private FlatFindReplaceBar findReplaceBar;
	private JScrollPane previewScrollPane;
	private FlatThemePreview preview;

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
		scrollPane.setBorder( BorderFactory.createEmptyBorder() );
		scrollPane.setLineNumbersEnabled( true );

		// map Ctrl+PageUp/Down to a not-existing action to avoid that the scrollpane catches them
		InputMap inputMap = scrollPane.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
		inputMap.put( KeyStroke.getKeyStroke( "ctrl PAGE_UP" ), "__dummy__" );
		inputMap.put( KeyStroke.getKeyStroke( "ctrl PAGE_DOWN" ), "__dummy__" );

		// create error strip
		errorStrip = new ErrorStrip( textArea );

		// create collapsible panel
		collapsiblePanel = new CollapsibleSectionPanel();
		collapsiblePanel.add( scrollPane );
		collapsiblePanel.add( errorStrip, BorderLayout.LINE_END );
		add( collapsiblePanel, BorderLayout.CENTER );

		updateTheme();
	}

	void updateTheme() {
		Font defaultFont = RTextArea.getDefaultFont();
		Font font = defaultFont.deriveFont( (float) UIManager.getFont( "defaultFont" ).getSize() );

		textArea.setFont( font );
		textArea.setBackground( UIManager.getColor( "FlatThemeEditorPane.background" ) );
		textArea.setCaretColor( UIManager.getColor( "FlatThemeEditorPane.caretColor" ) );
		textArea.setSelectionColor( UIManager.getColor( "FlatThemeEditorPane.selectionBackground" ) );
		textArea.setCurrentLineHighlightColor( UIManager.getColor( "FlatThemeEditorPane.currentLineHighlight" ) );
		textArea.setMarkAllHighlightColor( UIManager.getColor( "FlatThemeEditorPane.markAllHighlightColor" ) );
		textArea.setMarkOccurrencesColor( UIManager.getColor( "FlatThemeEditorPane.markOccurrencesColor" ) );
		textArea.setMatchedBracketBGColor( UIManager.getColor( "FlatThemeEditorPane.matchedBracketBackground" ) );
		textArea.setMatchedBracketBorderColor( UIManager.getColor( "FlatThemeEditorPane.matchedBracketBorderColor" ) );
		textArea.setPaintMatchedBracketPair( true );
		textArea.setAnimateBracketMatching( false );

		// syntax
		textArea.setSyntaxScheme( new FlatSyntaxScheme( font ) );

		// gutter
		Gutter gutter = scrollPane.getGutter();
		gutter.setBackground( UIManager.getColor( "FlatThemeEditorPane.gutter.background" ) );
		gutter.setBorderColor( UIManager.getColor( "FlatThemeEditorPane.gutter.borderColor" ) );
		gutter.setLineNumberColor( UIManager.getColor( "FlatThemeEditorPane.gutter.lineNumberColor" ) );
		gutter.setLineNumberFont( font );

		// error strip
		errorStrip.setCaretMarkerColor( UIManager.getColor( "FlatThemeEditorPane.errorstrip.caretMarkerColor" ) );

		if( preview != null )
			preview.updateLater();
	}

	void updateFontSize( int sizeIncr ) {
		Font defaultFont = RTextArea.getDefaultFont();
		Font font = defaultFont.deriveFont( (float) UIManager.getFont( "defaultFont" ).getSize() + sizeIncr );

		textArea.setFont( font );
		textArea.setSyntaxScheme( new FlatSyntaxScheme( font ) );
		scrollPane.getGutter().setLineNumberFont( font );
	}

	@Override
	public boolean requestFocusInWindow() {
		return textArea.requestFocusInWindow();
	}

	void initBasePropertyProvider( FlatThemePropertiesBaseManager propertiesBaseManager ) {
		textArea.propertiesSupport.setBasePropertyProvider( propertiesBaseManager.create( file, textArea.propertiesSupport ) );
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
				int selectionStart = textArea.getSelectionStart();
				int selectionEnd = textArea.getSelectionEnd();

				textArea.reload();

				textArea.select( selectionStart, selectionEnd );
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

	void showPreview( boolean show ) {
		if( show ) {
			if( preview != null )
				return;

			preview = new FlatThemePreview( textArea );
			previewScrollPane = new JScrollPane( preview );
			previewScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
			previewScrollPane.setBorder( BorderFactory.createEmptyBorder() );
			previewScrollPane.getVerticalScrollBar().setUnitIncrement( 20 );
			previewScrollPane.getHorizontalScrollBar().setUnitIncrement( 20 );
			add( previewScrollPane, BorderLayout.LINE_END );
		} else {
			if( preview == null )
				return;

			remove( previewScrollPane );
			previewScrollPane = null;
			preview = null;
		}

		revalidate();
	}

	//---- class FlatSyntaxScheme ---------------------------------------------

	private static class FlatSyntaxScheme
		extends SyntaxScheme
	{
		FlatSyntaxScheme( Font baseFont ) {
			super( false );

			Style[] styles = getStyles();
			for( int i = 0; i < styles.length; i++ )
				styles[i] = new Style( Color.red );

			init( "property", FlatThemeTokenMaker.TOKEN_PROPERTY, baseFont );
			init( "variable", FlatThemeTokenMaker.TOKEN_VARIABLE, baseFont );
			init( "number", FlatThemeTokenMaker.TOKEN_NUMBER, baseFont );
			init( "color", FlatThemeTokenMaker.TOKEN_COLOR, baseFont );
			init( "string", FlatThemeTokenMaker.TOKEN_STRING, baseFont );
			init( "function", FlatThemeTokenMaker.TOKEN_FUNCTION, baseFont );
			init( "type", FlatThemeTokenMaker.TOKEN_TYPE, baseFont );
			init( "reservedWord", TokenTypes.RESERVED_WORD, baseFont );
			init( "literalBoolean", TokenTypes.LITERAL_BOOLEAN, baseFont );
			init( "operator", TokenTypes.OPERATOR, baseFont );
			init( "separator", TokenTypes.SEPARATOR, baseFont );
			init( "whitespace", TokenTypes.WHITESPACE, baseFont );
			init( "comment", TokenTypes.COMMENT_EOL, baseFont );
		}

		private void init( String key, int token, Font baseFont ) {
			String prefix = "FlatThemeEditorPane.style.";
			Color fg = UIManager.getColor( prefix + key );
			Color bg = UIManager.getColor( prefix + key + ".background" );
			boolean italic = UIManager.getBoolean( prefix + key + ".italic" );
			Font font = Style.DEFAULT_FONT;
			if( italic )
				font = baseFont.deriveFont( Font.ITALIC );
			getStyles()[token] = new Style( fg, bg, font );
		}
	}
}
