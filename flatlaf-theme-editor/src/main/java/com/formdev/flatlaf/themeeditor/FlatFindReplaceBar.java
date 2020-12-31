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

import java.awt.Container;
import javax.swing.*;
import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class FlatFindReplaceBar
	extends JPanel
{
	private final RSyntaxTextArea textArea;

	private SearchContext context;

	FlatFindReplaceBar( RSyntaxTextArea textArea ) {
		this.textArea = textArea;

		initComponents();

		findPreviousButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/findAndShowPrevMatches.svg" ) );
		findNextButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/findAndShowNextMatches.svg" ) );
		matchCaseToggleButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/matchCase.svg" ) );
		matchWholeWordToggleButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/words.svg" ) );
		regexToggleButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/regex.svg" ) );
		closeButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/close.svg" ) );

		SearchContext context = new SearchContext();
		context.setSearchWrap( true );
		setSearchContext( context );
	}

	SearchContext getSearchContext() {
		return context;
	}

	void setSearchContext( SearchContext context ) {
		this.context = context;

		findField.setText( context.getSearchFor() );
		matchCaseToggleButton.setSelected( context.getMatchCase() );
		matchWholeWordToggleButton.setSelected( context.getWholeWord() );
		regexToggleButton.setSelected( context.isRegularExpression() );
	}

	@Override
	public boolean requestFocusInWindow() {
		// invoked from CollapsibleSectionPanel
		return findField.requestFocusInWindow();
	}

	@Override
	public void addNotify() {
		super.addNotify();

		// if showing bar, highlight matches in editor
		markAll();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		// if hiding bar, clear all highlighted matches in editor
		SearchEngine.markAll( textArea, new SearchContext() );
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

	private void markAll() {
		findOrMarkAll( false );
	}

	private void findOrMarkAll( boolean find ) {
		// update search context
		context.setSearchFor( findField.getText() );

		// find
		SearchResult result = find
			? SearchEngine.find( textArea, context )
			: SearchEngine.markAll( textArea, context );

		// update matches info label
		matchesLabel.setText( result.getMarkedCount() + " matches" );
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

	private void close() {
		Container parent = getParent();
		if( parent instanceof CollapsibleSectionPanel )
			((CollapsibleSectionPanel)parent).hideBottomComponent();
		else if( parent != null )
			parent.remove( this );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		findLabel = new JLabel();
		findField = new JTextField();
		findToolBar = new JToolBar();
		findPreviousButton = new JButton();
		findNextButton = new JButton();
		matchCaseToggleButton = new JToggleButton();
		matchWholeWordToggleButton = new JToggleButton();
		regexToggleButton = new JToggleButton();
		matchesLabel = new JLabel();
		hSpacer1 = new JPanel(null);
		closeButton = new JButton();

		//======== this ========
		setFocusCycleRoot(true);
		setLayout(new MigLayout(
			"insets 3,hidemode 3",
			// columns
			"[fill]" +
			"[fill]0" +
			"[grow,fill]",
			// rows
			"[]"));

		//---- findLabel ----
		findLabel.setText("Find:");
		findLabel.setDisplayedMnemonic('F');
		findLabel.setLabelFor(findField);
		add(findLabel, "cell 0 0");

		//---- findField ----
		findField.setColumns(16);
		findField.addActionListener(e -> find());
		add(findField, "cell 1 0");

		//======== findToolBar ========
		{
			findToolBar.setFloatable(false);
			findToolBar.setBorder(null);

			//---- findPreviousButton ----
			findPreviousButton.setToolTipText("Previous Occurrence");
			findPreviousButton.addActionListener(e -> findPrevious());
			findToolBar.add(findPreviousButton);

			//---- findNextButton ----
			findNextButton.setToolTipText("Next Occurrence");
			findNextButton.addActionListener(e -> findNext());
			findToolBar.add(findNextButton);
			findToolBar.addSeparator();

			//---- matchCaseToggleButton ----
			matchCaseToggleButton.setToolTipText("Match Case");
			matchCaseToggleButton.addActionListener(e -> matchCaseChanged());
			findToolBar.add(matchCaseToggleButton);

			//---- matchWholeWordToggleButton ----
			matchWholeWordToggleButton.setToolTipText("Match Whole Word");
			matchWholeWordToggleButton.addActionListener(e -> matchWholeWordChanged());
			findToolBar.add(matchWholeWordToggleButton);

			//---- regexToggleButton ----
			regexToggleButton.setToolTipText("Regex");
			regexToggleButton.addActionListener(e -> regexChanged());
			findToolBar.add(regexToggleButton);
			findToolBar.addSeparator();

			//---- matchesLabel ----
			matchesLabel.setEnabled(false);
			findToolBar.add(matchesLabel);
			findToolBar.add(hSpacer1);

			//---- closeButton ----
			closeButton.setToolTipText("Close");
			closeButton.addActionListener(e -> close());
			findToolBar.add(closeButton);
		}
		add(findToolBar, "cell 2 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel findLabel;
	private JTextField findField;
	private JToolBar findToolBar;
	private JButton findPreviousButton;
	private JButton findNextButton;
	private JToggleButton matchCaseToggleButton;
	private JToggleButton matchWholeWordToggleButton;
	private JToggleButton regexToggleButton;
	private JLabel matchesLabel;
	private JPanel hSpacer1;
	private JButton closeButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
