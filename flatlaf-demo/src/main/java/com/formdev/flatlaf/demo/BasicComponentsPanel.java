/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.demo;

import java.awt.Component;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.DimConstraint;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class BasicComponentsPanel
	extends JPanel
{
	BasicComponentsPanel() {
		initComponents();

		// show reveal button for password field
		//   to enable this for all password fields use:
		//   UIManager.put( "PasswordField.showRevealButton", true );
		passwordField1.putClientProperty( FlatClientProperties.STYLE, "showRevealButton: true" );

		// add leading/trailing icons to text fields
		leadingIconTextField.putClientProperty( FlatClientProperties.PLACEHOLDER_TEXT, "Search" );
		leadingIconTextField.putClientProperty( FlatClientProperties.TEXT_FIELD_LEADING_ICON,
			new FlatSearchIcon() );
		trailingIconTextField.putClientProperty( FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
			new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/DataTables.svg" ) );
		iconsTextField.putClientProperty( FlatClientProperties.TEXT_FIELD_LEADING_ICON,
			new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/user.svg" ) );
		iconsTextField.putClientProperty( FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
			new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/bookmarkGroup.svg" ) );

		// search history button
		JButton searchHistoryButton = new JButton( new FlatSearchWithHistoryIcon( true ) );
		searchHistoryButton.setToolTipText( "Search History" );
		searchHistoryButton.addActionListener( e -> {
			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.add( "(empty)" );
			popupMenu.show( searchHistoryButton, 0, searchHistoryButton.getHeight() );
		} );
		compsTextField.putClientProperty( FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, searchHistoryButton );

		// match case button
		JToggleButton matchCaseButton = new JToggleButton( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/matchCase.svg" ) );
		matchCaseButton.setRolloverIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/matchCaseHovered.svg" ) );
		matchCaseButton.setSelectedIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/matchCaseSelected.svg" ) );
		matchCaseButton.setToolTipText( "Match Case" );

		// whole words button
		JToggleButton wordsButton = new JToggleButton( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/words.svg" ) );
		wordsButton.setRolloverIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/wordsHovered.svg" ) );
		wordsButton.setSelectedIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/wordsSelected.svg" ) );
		wordsButton.setToolTipText( "Whole Words" );

		// regex button
		JToggleButton regexButton = new JToggleButton( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/regex.svg" ) );
		regexButton.setRolloverIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/regexHovered.svg" ) );
		regexButton.setSelectedIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/regexSelected.svg" ) );
		regexButton.setToolTipText( "Regular Expression" );

		// search toolbar
		JToolBar searchToolbar = new JToolBar();
		searchToolbar.add( matchCaseButton );
		searchToolbar.add( wordsButton );
		searchToolbar.addSeparator();
		searchToolbar.add( regexButton );
		compsTextField.putClientProperty( FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, searchToolbar );

		// show clear button (if text field is not empty)
		compsTextField.putClientProperty( FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true );
		clearTextField.putClientProperty( FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel labelLabel = new JLabel();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JLabel buttonLabel = new JLabel();
		JButton button1 = new JButton();
		JButton button2 = new JButton();
		JButton button5 = new JButton();
		JButton button6 = new JButton();
		JButton button3 = new JButton();
		JButton button4 = new JButton();
		JButton button13 = new JButton();
		JButton button14 = new JButton();
		JButton button15 = new JButton();
		JButton button16 = new JButton();
		JLabel checkBoxLabel = new JLabel();
		JCheckBox checkBox1 = new JCheckBox();
		JCheckBox checkBox2 = new JCheckBox();
		JCheckBox checkBox3 = new JCheckBox();
		JCheckBox checkBox4 = new JCheckBox();
		JLabel radioButtonLabel = new JLabel();
		JRadioButton radioButton1 = new JRadioButton();
		JRadioButton radioButton2 = new JRadioButton();
		JRadioButton radioButton3 = new JRadioButton();
		JRadioButton radioButton4 = new JRadioButton();
		JLabel comboBoxLabel = new JLabel();
		JComboBox<String> comboBox1 = new JComboBox<>();
		JComboBox<String> comboBox2 = new JComboBox<>();
		JComboBox<String> comboBox3 = new JComboBox<>();
		JComboBox<String> comboBox4 = new JComboBox<>();
		JComboBox<String> comboBox5 = new JComboBox<>();
		JLabel spinnerLabel = new JLabel();
		JSpinner spinner1 = new JSpinner();
		JSpinner spinner2 = new JSpinner();
		JComboBox<String> comboBox6 = new JComboBox<>();
		JLabel textFieldLabel = new JLabel();
		JTextField textField1 = new JTextField();
		JTextField textField2 = new JTextField();
		JTextField textField3 = new JTextField();
		JTextField textField4 = new JTextField();
		JTextField textField6 = new JTextField();
		JLabel formattedTextFieldLabel = new JLabel();
		JFormattedTextField formattedTextField1 = new JFormattedTextField();
		JFormattedTextField formattedTextField2 = new JFormattedTextField();
		JFormattedTextField formattedTextField3 = new JFormattedTextField();
		JFormattedTextField formattedTextField4 = new JFormattedTextField();
		JFormattedTextField formattedTextField5 = new JFormattedTextField();
		JLabel passwordFieldLabel = new JLabel();
		passwordField1 = new JPasswordField();
		JPasswordField passwordField2 = new JPasswordField();
		JPasswordField passwordField3 = new JPasswordField();
		JPasswordField passwordField4 = new JPasswordField();
		JPasswordField passwordField5 = new JPasswordField();
		JLabel textAreaLabel = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		JTextArea textArea1 = new JTextArea();
		JScrollPane scrollPane2 = new JScrollPane();
		JTextArea textArea2 = new JTextArea();
		JScrollPane scrollPane3 = new JScrollPane();
		JTextArea textArea3 = new JTextArea();
		JScrollPane scrollPane4 = new JScrollPane();
		JTextArea textArea4 = new JTextArea();
		JTextArea textArea5 = new JTextArea();
		JLabel editorPaneLabel = new JLabel();
		JScrollPane scrollPane5 = new JScrollPane();
		JEditorPane editorPane1 = new JEditorPane();
		JScrollPane scrollPane6 = new JScrollPane();
		JEditorPane editorPane2 = new JEditorPane();
		JScrollPane scrollPane7 = new JScrollPane();
		JEditorPane editorPane3 = new JEditorPane();
		JScrollPane scrollPane8 = new JScrollPane();
		JEditorPane editorPane4 = new JEditorPane();
		JEditorPane editorPane5 = new JEditorPane();
		JLabel textPaneLabel = new JLabel();
		JScrollPane scrollPane9 = new JScrollPane();
		JTextPane textPane1 = new JTextPane();
		JScrollPane scrollPane10 = new JScrollPane();
		JTextPane textPane2 = new JTextPane();
		JScrollPane scrollPane11 = new JScrollPane();
		JTextPane textPane3 = new JTextPane();
		JScrollPane scrollPane12 = new JScrollPane();
		JTextPane textPane4 = new JTextPane();
		JTextPane textPane5 = new JTextPane();
		JLabel hintsLabel = new JLabel();
		JTextField errorHintsTextField = new JTextField();
		JTextField warningHintsTextField = new JTextField();
		JTextField successHintsTextField = new JTextField();
		JLabel iconsLabel = new JLabel();
		leadingIconTextField = new JTextField();
		trailingIconTextField = new JTextField();
		iconsTextField = new JTextField();
		JLabel compsLabel = new JLabel();
		compsTextField = new JTextField();
		clearTextField = new JTextField();
		JLabel fontsLabel = new JLabel();
		JLabel h00Label = new JLabel();
		JLabel h0Label = new JLabel();
		JLabel h1Label = new JLabel();
		JLabel h2Label = new JLabel();
		JLabel h3Label = new JLabel();
		JLabel h4Label = new JLabel();
		JLabel lightLabel = new JLabel();
		JLabel semiboldLabel = new JLabel();
		JLabel fontZoomLabel = new JLabel();
		JLabel largeLabel = new JLabel();
		JLabel defaultLabel = new JLabel();
		JLabel mediumLabel = new JLabel();
		JLabel smallLabel = new JLabel();
		JLabel miniLabel = new JLabel();
		JLabel monospacedLabel = new JLabel();
		JPopupMenu popupMenu1 = new JPopupMenu();
		JMenuItem cutMenuItem = new JMenuItem();
		JMenuItem copyMenuItem = new JMenuItem();
		JMenuItem pasteMenuItem = new JMenuItem();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[]" +
			"[sizegroup 1]" +
			"[sizegroup 1]" +
			"[sizegroup 1]" +
			"[]" +
			"[]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]para" +
			"[]" +
			"[]" +
			"[]" +
			"[]0" +
			"[]"));

		//---- labelLabel ----
		labelLabel.setText("JLabel:");
		add(labelLabel, "cell 0 0");

		//---- label1 ----
		label1.setText("Enabled");
		label1.setDisplayedMnemonic('E');
		add(label1, "cell 1 0");

		//---- label2 ----
		label2.setText("Disabled");
		label2.setDisplayedMnemonic('D');
		label2.setEnabled(false);
		add(label2, "cell 2 0");

		//---- buttonLabel ----
		buttonLabel.setText("JButton:");
		add(buttonLabel, "cell 0 1");

		//---- button1 ----
		button1.setText("Enabled");
		button1.setDisplayedMnemonicIndex(0);
		add(button1, "cell 1 1");

		//---- button2 ----
		button2.setText("Disabled");
		button2.setDisplayedMnemonicIndex(0);
		button2.setEnabled(false);
		add(button2, "cell 2 1");

		//---- button5 ----
		button5.setText("Square");
		button5.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_SQUARE);
		add(button5, "cell 3 1");

		//---- button6 ----
		button6.setText("Round");
		button6.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
		add(button6, "cell 4 1");

		//---- button3 ----
		button3.setText("Help");
		button3.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(button3, "cell 4 1");

		//---- button4 ----
		button4.setText("Help");
		button4.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		button4.setEnabled(false);
		add(button4, "cell 4 1");

		//---- button13 ----
		button13.setIcon(UIManager.getIcon("Tree.closedIcon"));
		add(button13, "cell 5 1");

		//---- button14 ----
		button14.setText("...");
		add(button14, "cell 5 1");

		//---- button15 ----
		button15.setText("\u2026");
		add(button15, "cell 5 1");

		//---- button16 ----
		button16.setText("#");
		add(button16, "cell 5 1");

		//---- checkBoxLabel ----
		checkBoxLabel.setText("JCheckBox");
		add(checkBoxLabel, "cell 0 2");

		//---- checkBox1 ----
		checkBox1.setText("Enabled");
		checkBox1.setMnemonic('A');
		add(checkBox1, "cell 1 2");

		//---- checkBox2 ----
		checkBox2.setText("Disabled");
		checkBox2.setEnabled(false);
		checkBox2.setMnemonic('D');
		add(checkBox2, "cell 2 2");

		//---- checkBox3 ----
		checkBox3.setText("Selected");
		checkBox3.setSelected(true);
		add(checkBox3, "cell 3 2");

		//---- checkBox4 ----
		checkBox4.setText("Selected disabled");
		checkBox4.setSelected(true);
		checkBox4.setEnabled(false);
		add(checkBox4, "cell 4 2");

		//---- radioButtonLabel ----
		radioButtonLabel.setText("JRadioButton:");
		add(radioButtonLabel, "cell 0 3");

		//---- radioButton1 ----
		radioButton1.setText("Enabled");
		radioButton1.setMnemonic('N');
		add(radioButton1, "cell 1 3");

		//---- radioButton2 ----
		radioButton2.setText("Disabled");
		radioButton2.setEnabled(false);
		radioButton2.setMnemonic('S');
		add(radioButton2, "cell 2 3");

		//---- radioButton3 ----
		radioButton3.setText("Selected");
		radioButton3.setSelected(true);
		add(radioButton3, "cell 3 3");

		//---- radioButton4 ----
		radioButton4.setText("Selected disabled");
		radioButton4.setSelected(true);
		radioButton4.setEnabled(false);
		add(radioButton4, "cell 4 3");

		//---- comboBoxLabel ----
		comboBoxLabel.setText("JComboBox:");
		comboBoxLabel.setDisplayedMnemonic('C');
		comboBoxLabel.setLabelFor(comboBox1);
		add(comboBoxLabel, "cell 0 4");

		//---- comboBox1 ----
		comboBox1.setEditable(true);
		comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
			"Editable",
			"a",
			"bb",
			"ccc"
		}));
		add(comboBox1, "cell 1 4,growx");

		//---- comboBox2 ----
		comboBox2.setEditable(true);
		comboBox2.setEnabled(false);
		comboBox2.setModel(new DefaultComboBoxModel<>(new String[] {
			"Disabled",
			"a",
			"bb",
			"ccc"
		}));
		add(comboBox2, "cell 2 4,growx");

		//---- comboBox3 ----
		comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {
			"Not editable",
			"a",
			"bb",
			"ccc"
		}));
		add(comboBox3, "cell 3 4,growx");

		//---- comboBox4 ----
		comboBox4.setModel(new DefaultComboBoxModel<>(new String[] {
			"Not editable disabled",
			"a",
			"bb",
			"ccc"
		}));
		comboBox4.setEnabled(false);
		add(comboBox4, "cell 4 4,growx");

		//---- comboBox5 ----
		comboBox5.setModel(new DefaultComboBoxModel<>(new String[] {
			"Wide popup if text is longer",
			"aa",
			"bbb",
			"cccc"
		}));
		add(comboBox5, "cell 5 4,growx,wmax 100");

		//---- spinnerLabel ----
		spinnerLabel.setText("JSpinner:");
		spinnerLabel.setLabelFor(spinner1);
		spinnerLabel.setDisplayedMnemonic('S');
		add(spinnerLabel, "cell 0 5");
		add(spinner1, "cell 1 5,growx");

		//---- spinner2 ----
		spinner2.setEnabled(false);
		add(spinner2, "cell 2 5,growx");

		//---- comboBox6 ----
		comboBox6.setEditable(true);
		comboBox6.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Placeholder");
		add(comboBox6, "cell 5 5,growx");

		//---- textFieldLabel ----
		textFieldLabel.setText("JTextField:");
		textFieldLabel.setDisplayedMnemonic('T');
		textFieldLabel.setLabelFor(textField1);
		add(textFieldLabel, "cell 0 6");

		//---- textField1 ----
		textField1.setText("Editable");
		textField1.setComponentPopupMenu(popupMenu1);
		add(textField1, "cell 1 6,growx");

		//---- textField2 ----
		textField2.setText("Disabled");
		textField2.setEnabled(false);
		add(textField2, "cell 2 6,growx");

		//---- textField3 ----
		textField3.setText("Not editable");
		textField3.setEditable(false);
		add(textField3, "cell 3 6,growx");

		//---- textField4 ----
		textField4.setText("Not editable disabled");
		textField4.setEnabled(false);
		textField4.setEditable(false);
		add(textField4, "cell 4 6,growx");

		//---- textField6 ----
		textField6.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Placeholder");
		add(textField6, "cell 5 6,growx");

		//---- formattedTextFieldLabel ----
		formattedTextFieldLabel.setText("JFormattedTextField:");
		formattedTextFieldLabel.setLabelFor(formattedTextField1);
		formattedTextFieldLabel.setDisplayedMnemonic('O');
		add(formattedTextFieldLabel, "cell 0 7");

		//---- formattedTextField1 ----
		formattedTextField1.setText("Editable");
		formattedTextField1.setComponentPopupMenu(popupMenu1);
		add(formattedTextField1, "cell 1 7,growx");

		//---- formattedTextField2 ----
		formattedTextField2.setText("Disabled");
		formattedTextField2.setEnabled(false);
		add(formattedTextField2, "cell 2 7,growx");

		//---- formattedTextField3 ----
		formattedTextField3.setText("Not editable");
		formattedTextField3.setEditable(false);
		add(formattedTextField3, "cell 3 7,growx");

		//---- formattedTextField4 ----
		formattedTextField4.setText("Not editable disabled");
		formattedTextField4.setEnabled(false);
		formattedTextField4.setEditable(false);
		add(formattedTextField4, "cell 4 7,growx");

		//---- formattedTextField5 ----
		formattedTextField5.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Placeholder");
		add(formattedTextField5, "cell 5 7,growx");

		//---- passwordFieldLabel ----
		passwordFieldLabel.setText("JPasswordField:");
		add(passwordFieldLabel, "cell 0 8");

		//---- passwordField1 ----
		passwordField1.setText("Editable");
		add(passwordField1, "cell 1 8,growx");

		//---- passwordField2 ----
		passwordField2.setText("Disabled");
		passwordField2.setEnabled(false);
		add(passwordField2, "cell 2 8,growx");

		//---- passwordField3 ----
		passwordField3.setText("Not editable");
		passwordField3.setEditable(false);
		add(passwordField3, "cell 3 8,growx");

		//---- passwordField4 ----
		passwordField4.setText("Not editable disabled");
		passwordField4.setEnabled(false);
		passwordField4.setEditable(false);
		add(passwordField4, "cell 4 8,growx");

		//---- passwordField5 ----
		passwordField5.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Placeholder");
		add(passwordField5, "cell 5 8,growx");

		//---- textAreaLabel ----
		textAreaLabel.setText("JTextArea:");
		add(textAreaLabel, "cell 0 9");

		//======== scrollPane1 ========
		{
			scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea1 ----
			textArea1.setText("Editable");
			textArea1.setRows(2);
			scrollPane1.setViewportView(textArea1);
		}
		add(scrollPane1, "cell 1 9,growx");

		//======== scrollPane2 ========
		{
			scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea2 ----
			textArea2.setText("Disabled");
			textArea2.setRows(2);
			textArea2.setEnabled(false);
			scrollPane2.setViewportView(textArea2);
		}
		add(scrollPane2, "cell 2 9,growx");

		//======== scrollPane3 ========
		{
			scrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea3 ----
			textArea3.setText("Not editable");
			textArea3.setRows(2);
			textArea3.setEditable(false);
			scrollPane3.setViewportView(textArea3);
		}
		add(scrollPane3, "cell 3 9,growx");

		//======== scrollPane4 ========
		{
			scrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea4 ----
			textArea4.setText("Not editable disabled");
			textArea4.setRows(2);
			textArea4.setEditable(false);
			textArea4.setEnabled(false);
			scrollPane4.setViewportView(textArea4);
		}
		add(scrollPane4, "cell 4 9,growx");

		//---- textArea5 ----
		textArea5.setRows(2);
		textArea5.setText("No scroll pane");
		add(textArea5, "cell 5 9,growx");

		//---- editorPaneLabel ----
		editorPaneLabel.setText("JEditorPane");
		add(editorPaneLabel, "cell 0 10");

		//======== scrollPane5 ========
		{
			scrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane1 ----
			editorPane1.setText("Editable");
			scrollPane5.setViewportView(editorPane1);
		}
		add(scrollPane5, "cell 1 10,growx");

		//======== scrollPane6 ========
		{
			scrollPane6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane6.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane2 ----
			editorPane2.setText("Disabled");
			editorPane2.setEnabled(false);
			scrollPane6.setViewportView(editorPane2);
		}
		add(scrollPane6, "cell 2 10,growx");

		//======== scrollPane7 ========
		{
			scrollPane7.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane7.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane3 ----
			editorPane3.setText("Not editable");
			editorPane3.setEditable(false);
			scrollPane7.setViewportView(editorPane3);
		}
		add(scrollPane7, "cell 3 10,growx");

		//======== scrollPane8 ========
		{
			scrollPane8.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane8.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane4 ----
			editorPane4.setText("Not editable disabled");
			editorPane4.setEditable(false);
			editorPane4.setEnabled(false);
			scrollPane8.setViewportView(editorPane4);
		}
		add(scrollPane8, "cell 4 10,growx");

		//---- editorPane5 ----
		editorPane5.setText("No scroll pane");
		add(editorPane5, "cell 5 10,growx");

		//---- textPaneLabel ----
		textPaneLabel.setText("JTextPane:");
		add(textPaneLabel, "cell 0 11");

		//======== scrollPane9 ========
		{
			scrollPane9.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane9.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane1 ----
			textPane1.setText("Editable");
			scrollPane9.setViewportView(textPane1);
		}
		add(scrollPane9, "cell 1 11,growx");

		//======== scrollPane10 ========
		{
			scrollPane10.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane10.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane2 ----
			textPane2.setText("Disabled");
			textPane2.setEnabled(false);
			scrollPane10.setViewportView(textPane2);
		}
		add(scrollPane10, "cell 2 11,growx");

		//======== scrollPane11 ========
		{
			scrollPane11.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane11.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane3 ----
			textPane3.setText("Not editable");
			textPane3.setEditable(false);
			scrollPane11.setViewportView(textPane3);
		}
		add(scrollPane11, "cell 3 11,growx");

		//======== scrollPane12 ========
		{
			scrollPane12.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane12.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane4 ----
			textPane4.setText("Not editable disabled");
			textPane4.setEditable(false);
			textPane4.setEnabled(false);
			scrollPane12.setViewportView(textPane4);
		}
		add(scrollPane12, "cell 4 11,growx");

		//---- textPane5 ----
		textPane5.setText("No scroll pane");
		add(textPane5, "cell 5 11,growx");

		//---- hintsLabel ----
		hintsLabel.setText("Error/warning/success:");
		add(hintsLabel, "cell 0 12");

		//---- errorHintsTextField ----
		errorHintsTextField.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
		add(errorHintsTextField, "cell 1 12,growx");

		//---- warningHintsTextField ----
		warningHintsTextField.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_WARNING);
		add(warningHintsTextField, "cell 2 12,growx");

		//---- successHintsTextField ----
		successHintsTextField.putClientProperty(FlatClientProperties.OUTLINE, "success");
		add(successHintsTextField, "cell 3 12,growx");

		//---- iconsLabel ----
		iconsLabel.setText("Leading/trailing icons:");
		add(iconsLabel, "cell 0 13");
		add(leadingIconTextField, "cell 1 13,growx");

		//---- trailingIconTextField ----
		trailingIconTextField.setText("text");
		add(trailingIconTextField, "cell 2 13,growx");

		//---- iconsTextField ----
		iconsTextField.setText("text");
		add(iconsTextField, "cell 3 13,growx");

		//---- compsLabel ----
		compsLabel.setText("Leading/trailing comp.:");
		add(compsLabel, "cell 0 14");
		add(compsTextField, "cell 1 14 2 1,growx");

		//---- clearTextField ----
		clearTextField.setText("clear me");
		add(clearTextField, "cell 3 14,growx");

		//---- fontsLabel ----
		fontsLabel.setText("Typography / Fonts:");
		add(fontsLabel, "cell 0 15");

		//---- h00Label ----
		h00Label.setText("H00");
		h00Label.putClientProperty(FlatClientProperties.STYLE_CLASS, "h00");
		add(h00Label, "cell 1 15 5 1");

		//---- h0Label ----
		h0Label.setText("H0");
		h0Label.putClientProperty(FlatClientProperties.STYLE_CLASS, "h0");
		add(h0Label, "cell 1 15 5 1");

		//---- h1Label ----
		h1Label.setText("H1");
		h1Label.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(h1Label, "cell 1 15 5 1");

		//---- h2Label ----
		h2Label.setText("H2");
		h2Label.putClientProperty(FlatClientProperties.STYLE_CLASS, "h2");
		add(h2Label, "cell 1 15 5 1");

		//---- h3Label ----
		h3Label.setText("H3");
		h3Label.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(h3Label, "cell 1 15 5 1");

		//---- h4Label ----
		h4Label.setText("H4");
		h4Label.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");
		add(h4Label, "cell 1 15 5 1");

		//---- lightLabel ----
		lightLabel.setText("light");
		lightLabel.putClientProperty(FlatClientProperties.STYLE, "font: 200% $light.font");
		add(lightLabel, "cell 1 15 5 1,gapx 30");

		//---- semiboldLabel ----
		semiboldLabel.setText("semibold");
		semiboldLabel.putClientProperty(FlatClientProperties.STYLE, "font: 200% $semibold.font");
		add(semiboldLabel, "cell 1 15 5 1");

		//---- fontZoomLabel ----
		fontZoomLabel.setText("(200%)");
		fontZoomLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		fontZoomLabel.setEnabled(false);
		add(fontZoomLabel, "cell 1 15 5 1");

		//---- largeLabel ----
		largeLabel.setText("large");
		largeLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
		add(largeLabel, "cell 1 16 5 1");

		//---- defaultLabel ----
		defaultLabel.setText("default");
		add(defaultLabel, "cell 1 16 5 1");

		//---- mediumLabel ----
		mediumLabel.setText("medium");
		mediumLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "medium");
		add(mediumLabel, "cell 1 16 5 1");

		//---- smallLabel ----
		smallLabel.setText("small");
		smallLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		add(smallLabel, "cell 1 16 5 1");

		//---- miniLabel ----
		miniLabel.setText("mini");
		miniLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "mini");
		add(miniLabel, "cell 1 16 5 1");

		//---- monospacedLabel ----
		monospacedLabel.setText("monospaced");
		monospacedLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "monospaced");
		add(monospacedLabel, "cell 1 16 5 1,gapx 30");

		//======== popupMenu1 ========
		{

			//---- cutMenuItem ----
			cutMenuItem.setText("Cut");
			cutMenuItem.setMnemonic('C');
			popupMenu1.add(cutMenuItem);

			//---- copyMenuItem ----
			copyMenuItem.setText("Copy");
			copyMenuItem.setMnemonic('O');
			popupMenu1.add(copyMenuItem);

			//---- pasteMenuItem ----
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setMnemonic('P');
			popupMenu1.add(pasteMenuItem);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		cutMenuItem.addActionListener( new DefaultEditorKit.CutAction() );
		copyMenuItem.addActionListener( new DefaultEditorKit.CopyAction() );
		pasteMenuItem.addActionListener( new DefaultEditorKit.PasteAction() );

		if( FlatLafDemo.screenshotsMode ) {
			// hide some components
			Component[] hiddenComponents = {
				labelLabel, label1, label2,
				button13, button14, button15, button16, comboBox5, comboBox6,

				textFieldLabel, textField2, textField4, textField6,
				formattedTextFieldLabel, formattedTextField1, formattedTextField2, formattedTextField3, formattedTextField4, formattedTextField5,
				passwordFieldLabel, passwordField1, passwordField2, passwordField3, passwordField4, passwordField5,
				textAreaLabel, scrollPane1, scrollPane2, scrollPane3, scrollPane4, textArea5,
				editorPaneLabel, scrollPane5, scrollPane6, scrollPane7, scrollPane8, editorPane5,
				textPaneLabel, scrollPane9, scrollPane10, scrollPane11, scrollPane12, textPane5,

				hintsLabel, errorHintsTextField, warningHintsTextField, successHintsTextField,

				fontZoomLabel,
			};
			for( Component c : hiddenComponents )
				c.setVisible( false );

			// update layout (change row gaps to zero)
			MigLayout layout = (MigLayout) getLayout();
			Object rowCons = layout.getRowConstraints();
			AC ac = (rowCons instanceof String)
				? ConstraintParser.parseColumnConstraints( (String) rowCons )
				: (AC) rowCons;
			BoundSize zeroGap = ConstraintParser.parseBoundSize( "0", true, true );
			DimConstraint[] rows = ac.getConstaints();
			rows[6].setGapBefore( zeroGap );
			rows[7].setGapBefore( zeroGap );
			rows[8].setGapBefore( zeroGap );
			rows[9].setGapBefore( zeroGap );
			rows[10].setGapBefore( zeroGap );
			rows[11].setGapBefore( zeroGap );
			rows[11].setGapAfter( zeroGap );
			rows[12].setGapBefore( zeroGap );
			rows[15].setGapBefore( zeroGap );
			layout.setRowConstraints( ac );

			// move two text field into same row as spinners
			spinnerLabel.setText( "JSpinner / JTextField:" );
			layout.setComponentConstraints( textField1, "cell 3 5,growx" );
			layout.setComponentConstraints( textField3, "cell 4 5,growx" );

			// make "Not editable disabled" combobox smaller
			Object cons = layout.getComponentConstraints( comboBox4 );
			layout.setComponentConstraints( comboBox4, cons + ",width 50:50" );

			revalidate();
			repaint();
		}
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPasswordField passwordField1;
	private JTextField leadingIconTextField;
	private JTextField trailingIconTextField;
	private JTextField iconsTextField;
	private JTextField compsTextField;
	private JTextField clearTextField;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
