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

import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaUI;
import org.fife.ui.rtextarea.RUndoManager;
import com.formdev.flatlaf.UIDefaultsLoaderAccessor;
import com.formdev.flatlaf.themeeditor.FlatSyntaxTextAreaActions.InsertColorAction;
import com.formdev.flatlaf.themeeditor.FlatSyntaxTextAreaActions.PickColorAction;
import com.formdev.flatlaf.themeeditor.FlatSyntaxTextAreaActions.DuplicateLinesAction;
import com.formdev.flatlaf.themeeditor.FlatSyntaxTextAreaActions.IncrementNumberAction;

/**
 * A text area that supports editing FlatLaf themes.
 *
 * @author Karl Tauber
 */
class FlatSyntaxTextArea
	extends TextEditorPane
{
	private RUndoManager undoManager;
	private boolean useColorOfColorTokens;

	final FlatThemePropertiesSupport propertiesSupport = new FlatThemePropertiesSupport( this );
	private final Map<String, Color> parsedColorsMap = new HashMap<>();

	FlatSyntaxTextArea() {
		// this is necessary because RTextAreaBase.init() always sets foreground to black
		setForeground( UIManager.getColor( "TextArea.foreground" ) );

		// remove Ctrl+Tab and Ctrl+Shift+Tab focus traversal keys to allow tabbed pane to process them
		setFocusTraversalKeys( KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet() );
		setFocusTraversalKeys( KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.emptySet() );

		// add editor actions
		ActionMap actionMap = getActionMap();
		actionMap.put( FlatSyntaxTextAreaActions.duplicateLinesUpAction, new DuplicateLinesAction( FlatSyntaxTextAreaActions.duplicateLinesUpAction, true ) );
		actionMap.put( FlatSyntaxTextAreaActions.duplicateLinesDownAction, new DuplicateLinesAction( FlatSyntaxTextAreaActions.duplicateLinesDownAction, false ) );
		actionMap.put( FlatSyntaxTextAreaActions.incrementNumberAction, new IncrementNumberAction( FlatSyntaxTextAreaActions.incrementNumberAction, true ) );
		actionMap.put( FlatSyntaxTextAreaActions.decrementNumberAction, new IncrementNumberAction( FlatSyntaxTextAreaActions.decrementNumberAction, false ) );
		actionMap.put( FlatSyntaxTextAreaActions.insertColorAction, new InsertColorAction( FlatSyntaxTextAreaActions.insertColorAction ) );
		actionMap.put( FlatSyntaxTextAreaActions.pickColorAction, new PickColorAction( FlatSyntaxTextAreaActions.pickColorAction ) );

		// add editor key strokes
		InputMap inputMap = getInputMap();
		int defaultModifier = RTextArea.getDefaultModifier();
		int alt = InputEvent.ALT_DOWN_MASK;
		inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_UP,   defaultModifier|alt), FlatSyntaxTextAreaActions.duplicateLinesUpAction );
		inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, defaultModifier|alt), FlatSyntaxTextAreaActions.duplicateLinesDownAction );
		inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_UP,   defaultModifier), FlatSyntaxTextAreaActions.incrementNumberAction );
		inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, defaultModifier), FlatSyntaxTextAreaActions.decrementNumberAction );
		// add Ctrl+7 for German keyboards where Ctrl+/ does not work
		inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_7,    defaultModifier), RSyntaxTextAreaEditorKit.rstaToggleCommentAction );
	}

	@Override
	protected RTextAreaUI createRTextAreaUI() {
		return new FlatRSyntaxTextAreaUI( this );
	}

	@Override
	protected RUndoManager createUndoManager() {
		undoManager = super.createUndoManager();
		return undoManager;
	}

	void runWithoutUndo( Runnable runnable ) {
		getDocument().removeUndoableEditListener( undoManager );
		try {
			runnable.run();
		} finally {
			getDocument().addUndoableEditListener( undoManager );
		}
	}

	boolean isUseColorOfColorTokens() {
		return useColorOfColorTokens;
	}

	void setUseColorOfColorTokens( boolean useColorOfColorTokens ) {
		this.useColorOfColorTokens = useColorOfColorTokens;
		setHighlightCurrentLine( !useColorOfColorTokens );
	}

	@Override
	public Color getBackgroundForToken( Token t ) {
		if( useColorOfColorTokens && t.getType() == FlatThemeTokenMaker.TOKEN_COLOR ) {
			Color color = parseColor( t );
			if( color != null )
				return color;
		}

		return super.getBackgroundForToken( t );
	}

	@Override
	public Color getForegroundForToken( Token t ) {
		if( useColorOfColorTokens && t.getType() == FlatThemeTokenMaker.TOKEN_COLOR && !isCurrentLineHighlighted( t.getOffset() )) {
			Color color = parseColor( t );
			if( color != null ) {
				return (colorLuminance( color ) > 164 || color.getAlpha() < 96)
					? Color.black
					: Color.white;
			}
		}

		return super.getForegroundForToken( t );
	}

	private Color parseColor( Token token ) {
		return parsedColorsMap.computeIfAbsent( token.getLexeme(), s -> {
			try {
				return new Color( UIDefaultsLoaderAccessor.parseColorRGBA( s ), true );
			} catch( IllegalArgumentException ex ) {
				return null;
			}
		} );

	}

	private int colorLuminance( Color c ) {
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();

		int min = Math.min( red, Math.min( green, blue ) );
		int max = Math.max( red, Math.max( green, blue ) );

		return (max + min) / 2;
	}

	private boolean isCurrentLineHighlighted( int offset ) {
		try {
			return getHighlightCurrentLine() &&
				getSelectionStart() == getSelectionEnd() &&
				getLineOfOffset( offset ) == getLineOfOffset( getSelectionStart() );
		} catch( BadLocationException ex ) {
			return false;
		}
	}
}
