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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.formdev.flatlaf.extras.components.*;
import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rtextarea.RTextAreaHighlighter;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.StringUtils;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class FlatFindReplaceBar
	extends JPanel
{
	static final String PROP_CLOSED = "closed";

	private final RSyntaxTextArea textArea;

	private SearchContext context;
	private boolean inSetContext;
	private boolean markAllPending;

	FlatFindReplaceBar( RSyntaxTextArea textArea ) {
		this.textArea = textArea;

		initComponents();

		findField.getDocument().addDocumentListener( new MarkAllUpdater() );

		// find previous/next with UP/DOWN keys; focus editor with F12 key
		InputMap inputMap = getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
		inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_UP, 0 ), "findPrevious" );
		inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, 0 ), "findNext" );
		inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP, 0 ), "editorPageUp" );
		inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_DOWN, 0 ), "editorPageDown" );
		ActionMap actionMap = getActionMap();
		actionMap.put( "findPrevious", new ConsumerAction( e -> findPrevious() ) );
		actionMap.put( "findNext", new ConsumerAction( e -> findNext() ) );
		actionMap.put( "editorPageUp", new ConsumerAction( e -> notifyEditorAction( "page-up" ) ) );
		actionMap.put( "editorPageDown", new ConsumerAction( e -> notifyEditorAction( "page-down" ) ) );

		registerKeyboardAction( e -> close(),
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		SearchContext context = new SearchContext();
		context.setSearchWrap( true );
		setSearchContext( context );
	}

	@Override
	public void updateUI() {
		super.updateUI();

		setBorder( new MatteBorder( 1, 0, 0, 0, UIManager.getColor( "Component.borderColor" ) ) );
	}

	SearchContext getSearchContext() {
		return context;
	}

	void setSearchContext( SearchContext context ) {
		this.context = context;

		inSetContext = true;
		try {
			findField.setText( context.getSearchFor() );
			replaceField.setText( context.getReplaceWith() );
			matchCaseToggleButton.setSelected( context.getMatchCase() );
			matchWholeWordToggleButton.setSelected( context.getWholeWord() );
			regexToggleButton.setSelected( context.isRegularExpression() );
		} finally {
			inSetContext = false;
		}
	}

	void activate( boolean findEditorSelection ) {
		// use selected text of editor for searching
		if( findEditorSelection ) {
			String selectedText = textArea.getSelectedText();
			if( !StringUtils.isEmpty( selectedText ) && selectedText.indexOf( '\n' ) < 0 )
				findField.setText( selectedText );
			else
				findField.selectAll();
		}

		// if showing bar, highlight matches in editor
		markAll();

		findField.requestFocusInWindow();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		// if hiding bar, clear all highlighted matches in editor and focus it
		SearchEngine.markAll( textArea, new SearchContext() );
		textArea.requestFocusInWindow();
	}

	private void findNext() {
		context.setSearchForward( true );
		find();
	}

	private void findPrevious() {
		context.setSearchForward( false );
		find();
	}

	private void find() {
		findOrMarkAll( true );
	}

	void markAll() {
		if( inSetContext )
			return;

		// do mark all only once
		if( markAllPending )
			return;
		markAllPending = true;

		EventQueue.invokeLater( () -> {
			markAllPending = false;

			findOrMarkAll( false );
		} );

	}

	private void findOrMarkAll( boolean find ) {
		// update search context
		String searchFor = findField.getText();
		context.setSearchFor( searchFor );

		// find
		SearchResult result = find
			? SearchEngine.find( textArea, context )
			: SearchEngine.markAll( textArea, context );

		// select (and scroll to) match near caret
		if( !find && result.getMarkedCount() > 0 )
			selectMatchNearCaret();

		// update matches info label
		updateMatchesLabel( result, false );

		// enabled/disable
		boolean findEnabled = !StringUtils.isEmpty( searchFor );
		findPreviousButton.setEnabled( findEnabled );
		findNextButton.setEnabled( findEnabled );
	}

	private void matchCaseChanged() {
		context.setMatchCase( matchCaseToggleButton.isSelected() );
		markAll();
	}

	private void matchWholeWordChanged() {
		context.setWholeWord( matchWholeWordToggleButton.isSelected() );
		markAll();
	}

	private void regexChanged() {
		context.setRegularExpression( regexToggleButton.isSelected() );
		markAll();
	}

	private void replace() {
		// update search context
		context.setSearchFor( findField.getText() );
		context.setReplaceWith( replaceField.getText() );

		// replace
		SearchResult result = SearchEngine.replace( textArea, context );

		// update matches info labels
		updateMatchesLabel( result, true );
	}

	private void replaceAll() {
		// update search context
		context.setSearchFor( findField.getText() );
		context.setReplaceWith( replaceField.getText() );

		// make sure that search wrap is disabled because otherwise it is easy
		// to have endless loop when replacing e.g. "a" with "aa"
		boolean oldSearchWrap = context.getSearchWrap();
		context.setSearchWrap( false );

		// replace all
		SearchResult result = SearchEngine.replaceAll( textArea, context );

		// restore search wrap
		context.setSearchWrap( oldSearchWrap );

		// update matches info labels
		updateMatchesLabel( result, true );
	}

	private void selectMatchNearCaret() {
		RTextAreaHighlighter highlighter = (RTextAreaHighlighter) textArea.getHighlighter();
		if( highlighter == null )
			return;

		List<DocumentRange> ranges = highlighter.getMarkAllHighlightRanges();
		if( ranges.isEmpty() )
			return;

		DocumentRange selectRange = null;
		if( ranges.size() > 1 ) {
			int selStart = textArea.getSelectionStart();
			for( DocumentRange range : ranges ) {
				if( range.getEndOffset() >= selStart ) {
					selectRange = range;
					break;
				}
			}
		}
		if( selectRange == null )
			selectRange = ranges.get( 0 );

		RSyntaxUtilities.selectAndPossiblyCenter( textArea, selectRange, true );
	}

	private void updateMatchesLabel( SearchResult result, boolean replace ) {
		matchesLabel.setText( result.getMarkedCount() + " matches" );
		replaceMatchesLabel.setText( replace ? result.getCount() + " matches replaced" : null );

		findField.setOutline( result.getMarkedCount() > 0 ? null : "error" );
	}

	private void notifyEditorAction( String actionKey ) {
		Action action = textArea.getActionMap().get( actionKey );
		if( action != null )
			action.actionPerformed( new ActionEvent( textArea, ActionEvent.ACTION_PERFORMED, null ) );
	}

	private void close() {
		setVisible( false );
		firePropertyChange( PROP_CLOSED, false, true );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		findLabel = new JLabel();
		findField = new FlatTextField();
		findToolBar = new JToolBar();
		findPreviousButton = new JButton();
		findNextButton = new JButton();
		matchCaseToggleButton = new JToggleButton();
		matchWholeWordToggleButton = new JToggleButton();
		regexToggleButton = new JToggleButton();
		matchesLabel = new JLabel();
		closeToolBar = new JToolBar();
		closeButton = new JButton();
		replaceLabel = new JLabel();
		replaceField = new FlatTextField();
		toolBar1 = new JToolBar();
		replaceButton = new JButton();
		replaceAllButton = new JButton();
		replaceMatchesLabel = new JLabel();

		//======== this ========
		setFocusCycleRoot(true);
		setLayout(new MigLayout(
			"insets 3 6 3 3,hidemode 3",
			// columns
			"[fill]" +
			"[fill]0" +
			"[fill]" +
			"[grow,fill]" +
			"[fill]",
			// rows
			"[]3" +
			"[]"));

		//---- findLabel ----
		findLabel.setText("Find:");
		findLabel.setDisplayedMnemonic('F');
		findLabel.setLabelFor(findField);
		add(findLabel, "cell 0 0");

		//---- findField ----
		findField.setColumns(16);
		findField.setSelectAllOnFocusPolicy(FlatTextField.SelectAllOnFocusPolicy.always);
		findField.setShowClearButton(true);
		findField.addActionListener(e -> find());
		add(findField, "cell 1 0");

		//======== findToolBar ========
		{
			findToolBar.setFloatable(false);
			findToolBar.setBorder(BorderFactory.createEmptyBorder());

			//---- findPreviousButton ----
			findPreviousButton.setToolTipText("Previous Occurrence");
			findPreviousButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/themeeditor/icons/findAndShowPrevMatches.svg"));
			findPreviousButton.addActionListener(e -> findPrevious());
			findToolBar.add(findPreviousButton);

			//---- findNextButton ----
			findNextButton.setToolTipText("Next Occurrence");
			findNextButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/themeeditor/icons/findAndShowNextMatches.svg"));
			findNextButton.addActionListener(e -> findNext());
			findToolBar.add(findNextButton);
			findToolBar.addSeparator();

			//---- matchCaseToggleButton ----
			matchCaseToggleButton.setToolTipText("Match Case");
			matchCaseToggleButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/themeeditor/icons/matchCase.svg"));
			matchCaseToggleButton.addActionListener(e -> matchCaseChanged());
			findToolBar.add(matchCaseToggleButton);

			//---- matchWholeWordToggleButton ----
			matchWholeWordToggleButton.setToolTipText("Match Whole Word");
			matchWholeWordToggleButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/themeeditor/icons/words.svg"));
			matchWholeWordToggleButton.addActionListener(e -> matchWholeWordChanged());
			findToolBar.add(matchWholeWordToggleButton);

			//---- regexToggleButton ----
			regexToggleButton.setToolTipText("Regex");
			regexToggleButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/themeeditor/icons/regex.svg"));
			regexToggleButton.addActionListener(e -> regexChanged());
			findToolBar.add(regexToggleButton);
		}
		add(findToolBar, "cell 2 0");

		//---- matchesLabel ----
		matchesLabel.setEnabled(false);
		add(matchesLabel, "cell 3 0");

		//======== closeToolBar ========
		{
			closeToolBar.setFloatable(false);
			closeToolBar.setBorder(BorderFactory.createEmptyBorder());

			//---- closeButton ----
			closeButton.setToolTipText("Close");
			closeButton.addActionListener(e -> close());
			closeToolBar.add(closeButton);
		}
		add(closeToolBar, "cell 4 0");

		//---- replaceLabel ----
		replaceLabel.setText("Replace:");
		replaceLabel.setDisplayedMnemonic('R');
		replaceLabel.setLabelFor(replaceField);
		add(replaceLabel, "cell 0 1");

		//---- replaceField ----
		replaceField.setColumns(16);
		replaceField.setSelectAllOnFocusPolicy(FlatTextField.SelectAllOnFocusPolicy.always);
		replaceField.setShowClearButton(true);
		add(replaceField, "cell 1 1");

		//======== toolBar1 ========
		{
			toolBar1.setFloatable(false);
			toolBar1.setBorder(BorderFactory.createEmptyBorder());

			//---- replaceButton ----
			replaceButton.setText("Replace");
			replaceButton.setMnemonic('E');
			replaceButton.addActionListener(e -> replace());
			toolBar1.add(replaceButton);

			//---- replaceAllButton ----
			replaceAllButton.setText("Replace All");
			replaceAllButton.setMnemonic('A');
			replaceAllButton.addActionListener(e -> replaceAll());
			toolBar1.add(replaceAllButton);
		}
		add(toolBar1, "cell 2 1");

		//---- replaceMatchesLabel ----
		replaceMatchesLabel.setEnabled(false);
		add(replaceMatchesLabel, "cell 3 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel findLabel;
	private FlatTextField findField;
	private JToolBar findToolBar;
	private JButton findPreviousButton;
	private JButton findNextButton;
	private JToggleButton matchCaseToggleButton;
	private JToggleButton matchWholeWordToggleButton;
	private JToggleButton regexToggleButton;
	private JLabel matchesLabel;
	private JToolBar closeToolBar;
	private JButton closeButton;
	private JLabel replaceLabel;
	private FlatTextField replaceField;
	private JToolBar toolBar1;
	private JButton replaceButton;
	private JButton replaceAllButton;
	private JLabel replaceMatchesLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class MarkAllUpdater -----------------------------------------------

	private class MarkAllUpdater
		implements DocumentListener
	{
		@Override
		public void insertUpdate( DocumentEvent e ) {
			markAll();
		}

		@Override
		public void removeUpdate( DocumentEvent e ) {
			markAll();
		}

		@Override
		public void changedUpdate( DocumentEvent e ) {
			markAll();
		}
	}

	//---- class ConsumerAction -----------------------------------------------

	private static class ConsumerAction
		extends AbstractAction
	{
		private final Consumer<ActionEvent> consumer;

		ConsumerAction( Consumer<ActionEvent> consumer ) {
			this.consumer = consumer;
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			consumer.accept( e );
		}
	}
}
