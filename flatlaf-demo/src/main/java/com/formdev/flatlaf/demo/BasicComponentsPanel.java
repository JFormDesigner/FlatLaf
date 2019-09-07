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

import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class BasicComponentsPanel
	extends JPanel
{
	BasicComponentsPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel labelLabel = new JLabel();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JLabel buttonLabel = new JLabel();
		JButton button1 = new JButton();
		JButton button2 = new JButton();
		JLabel toggleButtonLabel = new JLabel();
		JToggleButton toggleButton1 = new JToggleButton();
		JToggleButton toggleButton2 = new JToggleButton();
		JToggleButton toggleButton3 = new JToggleButton();
		JToggleButton toggleButton4 = new JToggleButton();
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
		JLabel spinnerLabel = new JLabel();
		JSpinner spinner1 = new JSpinner();
		JSpinner spinner2 = new JSpinner();
		JLabel textFieldLabel = new JLabel();
		JTextField textField1 = new JTextField();
		JTextField textField2 = new JTextField();
		JTextField textField3 = new JTextField();
		JTextField textField4 = new JTextField();
		JLabel formattedTextFieldLabel = new JLabel();
		JFormattedTextField formattedTextField1 = new JFormattedTextField();
		JFormattedTextField formattedTextField2 = new JFormattedTextField();
		JFormattedTextField formattedTextField3 = new JFormattedTextField();
		JFormattedTextField formattedTextField4 = new JFormattedTextField();
		JLabel passwordFieldLabel = new JLabel();
		JPasswordField passwordField1 = new JPasswordField();
		JPasswordField passwordField2 = new JPasswordField();
		JPasswordField passwordField3 = new JPasswordField();
		JPasswordField passwordField4 = new JPasswordField();
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

		//======== this ========
		setLayout(new MigLayout(
			"ltr,hidemode 3",
			// columns
			"[]" +
			"[]" +
			"[]" +
			"[]" +
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
			"[]" +
			"[]"));

		//---- labelLabel ----
		labelLabel.setText("JLabel:");
		add(labelLabel, "cell 0 0");

		//---- label1 ----
		label1.setText("enabled");
		label1.setDisplayedMnemonic('E');
		add(label1, "cell 1 0");

		//---- label2 ----
		label2.setText("disabled");
		label2.setDisplayedMnemonic('D');
		label2.setEnabled(false);
		add(label2, "cell 2 0");

		//---- buttonLabel ----
		buttonLabel.setText("JButton:");
		add(buttonLabel, "cell 0 1");

		//---- button1 ----
		button1.setText("enabled");
		button1.setDisplayedMnemonicIndex(0);
		add(button1, "cell 1 1");

		//---- button2 ----
		button2.setText("disabled");
		button2.setDisplayedMnemonicIndex(0);
		button2.setEnabled(false);
		add(button2, "cell 2 1");

		//---- toggleButtonLabel ----
		toggleButtonLabel.setText("JToggleButton:");
		add(toggleButtonLabel, "cell 0 2");

		//---- toggleButton1 ----
		toggleButton1.setText("enabled");
		add(toggleButton1, "cell 1 2");

		//---- toggleButton2 ----
		toggleButton2.setText("disabled");
		toggleButton2.setEnabled(false);
		add(toggleButton2, "cell 2 2");

		//---- toggleButton3 ----
		toggleButton3.setText("selected");
		toggleButton3.setSelected(true);
		add(toggleButton3, "cell 3 2");

		//---- toggleButton4 ----
		toggleButton4.setText("selected disabled");
		toggleButton4.setEnabled(false);
		toggleButton4.setSelected(true);
		add(toggleButton4, "cell 4 2");

		//---- checkBoxLabel ----
		checkBoxLabel.setText("JCheckBox");
		add(checkBoxLabel, "cell 0 3");

		//---- checkBox1 ----
		checkBox1.setText("enabled");
		checkBox1.setMnemonic('A');
		add(checkBox1, "cell 1 3");

		//---- checkBox2 ----
		checkBox2.setText("disabled");
		checkBox2.setEnabled(false);
		checkBox2.setMnemonic('D');
		add(checkBox2, "cell 2 3");

		//---- checkBox3 ----
		checkBox3.setText("selected");
		checkBox3.setSelected(true);
		add(checkBox3, "cell 3 3");

		//---- checkBox4 ----
		checkBox4.setText("selected disabled");
		checkBox4.setSelected(true);
		checkBox4.setEnabled(false);
		add(checkBox4, "cell 4 3");

		//---- radioButtonLabel ----
		radioButtonLabel.setText("JRadioButton:");
		add(radioButtonLabel, "cell 0 4");

		//---- radioButton1 ----
		radioButton1.setText("enabled");
		radioButton1.setMnemonic('N');
		add(radioButton1, "cell 1 4");

		//---- radioButton2 ----
		radioButton2.setText("disabled");
		radioButton2.setEnabled(false);
		radioButton2.setMnemonic('S');
		add(radioButton2, "cell 2 4");

		//---- radioButton3 ----
		radioButton3.setText("selected");
		radioButton3.setSelected(true);
		add(radioButton3, "cell 3 4");

		//---- radioButton4 ----
		radioButton4.setText("selected disabled");
		radioButton4.setSelected(true);
		radioButton4.setEnabled(false);
		add(radioButton4, "cell 4 4");

		//---- comboBoxLabel ----
		comboBoxLabel.setText("JComboBox:");
		add(comboBoxLabel, "cell 0 5");

		//---- comboBox1 ----
		comboBox1.setEditable(true);
		comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
			"editable",
			"a",
			"bb",
			"ccc"
		}));
		add(comboBox1, "cell 1 5,growx");

		//---- comboBox2 ----
		comboBox2.setEditable(true);
		comboBox2.setEnabled(false);
		comboBox2.setModel(new DefaultComboBoxModel<>(new String[] {
			"disabled",
			"a",
			"bb",
			"ccc"
		}));
		add(comboBox2, "cell 2 5,growx");

		//---- comboBox3 ----
		comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {
			"not editable",
			"a",
			"bb",
			"ccc"
		}));
		add(comboBox3, "cell 3 5,growx");

		//---- comboBox4 ----
		comboBox4.setModel(new DefaultComboBoxModel<>(new String[] {
			"not editable disabled",
			"a",
			"bb",
			"ccc"
		}));
		comboBox4.setEnabled(false);
		add(comboBox4, "cell 4 5,growx");

		//---- spinnerLabel ----
		spinnerLabel.setText("JSpinner:");
		add(spinnerLabel, "cell 0 6");
		add(spinner1, "cell 1 6,growx");

		//---- spinner2 ----
		spinner2.setEnabled(false);
		add(spinner2, "cell 2 6,growx");

		//---- textFieldLabel ----
		textFieldLabel.setText("JTextField:");
		add(textFieldLabel, "cell 0 7");

		//---- textField1 ----
		textField1.setText("editable");
		add(textField1, "cell 1 7,growx");

		//---- textField2 ----
		textField2.setText("disabled");
		textField2.setEnabled(false);
		add(textField2, "cell 2 7,growx");

		//---- textField3 ----
		textField3.setText("not editable");
		textField3.setEditable(false);
		add(textField3, "cell 3 7,growx");

		//---- textField4 ----
		textField4.setText("not editable disabled");
		textField4.setEnabled(false);
		textField4.setEditable(false);
		add(textField4, "cell 4 7,growx");

		//---- formattedTextFieldLabel ----
		formattedTextFieldLabel.setText("JFormattedTextField:");
		add(formattedTextFieldLabel, "cell 0 8");

		//---- formattedTextField1 ----
		formattedTextField1.setText("editable");
		add(formattedTextField1, "cell 1 8,growx");

		//---- formattedTextField2 ----
		formattedTextField2.setText("disabled");
		formattedTextField2.setEnabled(false);
		add(formattedTextField2, "cell 2 8,growx");

		//---- formattedTextField3 ----
		formattedTextField3.setText("not editable");
		formattedTextField3.setEditable(false);
		add(formattedTextField3, "cell 3 8,growx");

		//---- formattedTextField4 ----
		formattedTextField4.setText("not editable disabled");
		formattedTextField4.setEnabled(false);
		formattedTextField4.setEditable(false);
		add(formattedTextField4, "cell 4 8,growx");

		//---- passwordFieldLabel ----
		passwordFieldLabel.setText("JPasswordField:");
		add(passwordFieldLabel, "cell 0 9");

		//---- passwordField1 ----
		passwordField1.setText("editable");
		add(passwordField1, "cell 1 9,growx");

		//---- passwordField2 ----
		passwordField2.setText("disabled");
		passwordField2.setEnabled(false);
		add(passwordField2, "cell 2 9,growx");

		//---- passwordField3 ----
		passwordField3.setText("not editable");
		passwordField3.setEditable(false);
		add(passwordField3, "cell 3 9,growx");

		//---- passwordField4 ----
		passwordField4.setText("not editable disabled");
		passwordField4.setEnabled(false);
		passwordField4.setEditable(false);
		add(passwordField4, "cell 4 9,growx");

		//---- textAreaLabel ----
		textAreaLabel.setText("JTextArea:");
		add(textAreaLabel, "cell 0 10");

		//======== scrollPane1 ========
		{
			scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea1 ----
			textArea1.setText("editable");
			textArea1.setRows(2);
			scrollPane1.setViewportView(textArea1);
		}
		add(scrollPane1, "cell 1 10,growx");

		//======== scrollPane2 ========
		{
			scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea2 ----
			textArea2.setText("disabled");
			textArea2.setRows(2);
			textArea2.setEnabled(false);
			scrollPane2.setViewportView(textArea2);
		}
		add(scrollPane2, "cell 2 10,growx");

		//======== scrollPane3 ========
		{
			scrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea3 ----
			textArea3.setText("not editable");
			textArea3.setRows(2);
			textArea3.setEditable(false);
			scrollPane3.setViewportView(textArea3);
		}
		add(scrollPane3, "cell 3 10,growx");

		//======== scrollPane4 ========
		{
			scrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea4 ----
			textArea4.setText("not editable disabled");
			textArea4.setRows(2);
			textArea4.setEditable(false);
			textArea4.setEnabled(false);
			scrollPane4.setViewportView(textArea4);
		}
		add(scrollPane4, "cell 4 10,growx");

		//---- textArea5 ----
		textArea5.setRows(2);
		textArea5.setText("no scroll pane");
		add(textArea5, "cell 5 10,growx");

		//---- editorPaneLabel ----
		editorPaneLabel.setText("JEditorPane");
		add(editorPaneLabel, "cell 0 11");

		//======== scrollPane5 ========
		{
			scrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane1 ----
			editorPane1.setText("editable");
			scrollPane5.setViewportView(editorPane1);
		}
		add(scrollPane5, "cell 1 11,growx");

		//======== scrollPane6 ========
		{
			scrollPane6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane6.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane2 ----
			editorPane2.setText("disabled");
			editorPane2.setEnabled(false);
			scrollPane6.setViewportView(editorPane2);
		}
		add(scrollPane6, "cell 2 11,growx");

		//======== scrollPane7 ========
		{
			scrollPane7.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane7.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane3 ----
			editorPane3.setText("not editable");
			editorPane3.setEditable(false);
			scrollPane7.setViewportView(editorPane3);
		}
		add(scrollPane7, "cell 3 11,growx");

		//======== scrollPane8 ========
		{
			scrollPane8.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane8.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane4 ----
			editorPane4.setText("not editable disabled");
			editorPane4.setEditable(false);
			editorPane4.setEnabled(false);
			scrollPane8.setViewportView(editorPane4);
		}
		add(scrollPane8, "cell 4 11,growx");

		//---- editorPane5 ----
		editorPane5.setText("no scroll pane");
		add(editorPane5, "cell 5 11,growx");

		//---- textPaneLabel ----
		textPaneLabel.setText("JTextPane:");
		add(textPaneLabel, "cell 0 12");

		//======== scrollPane9 ========
		{
			scrollPane9.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane9.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane1 ----
			textPane1.setText("editable");
			scrollPane9.setViewportView(textPane1);
		}
		add(scrollPane9, "cell 1 12,growx");

		//======== scrollPane10 ========
		{
			scrollPane10.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane10.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane2 ----
			textPane2.setText("disabled");
			textPane2.setEnabled(false);
			scrollPane10.setViewportView(textPane2);
		}
		add(scrollPane10, "cell 2 12,growx");

		//======== scrollPane11 ========
		{
			scrollPane11.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane11.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane3 ----
			textPane3.setText("not editable");
			textPane3.setEditable(false);
			scrollPane11.setViewportView(textPane3);
		}
		add(scrollPane11, "cell 3 12,growx");

		//======== scrollPane12 ========
		{
			scrollPane12.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane12.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane4 ----
			textPane4.setText("not editable disabled");
			textPane4.setEditable(false);
			textPane4.setEnabled(false);
			scrollPane12.setViewportView(textPane4);
		}
		add(scrollPane12, "cell 4 12,growx");

		//---- textPane5 ----
		textPane5.setText("no scroll pane");
		add(textPane5, "cell 5 12,growx");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
