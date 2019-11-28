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

package com.formdev.flatlaf.testing;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatComponentsTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatComponentsTest" );
			frame.showFrame( FlatComponentsTest::new );
		} );
	}

	FlatComponentsTest() {
		initComponents();
	}

	private void indeterminateCheckBoxActionPerformed() {
		boolean indeterminate = indeterminateCheckBox.isSelected();
		progressBar1.setIndeterminate( indeterminate );
		progressBar2.setIndeterminate( indeterminate );
		progressBar3.setIndeterminate( indeterminate );
		progressBar4.setIndeterminate( indeterminate );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel labelLabel = new JLabel();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JLabel buttonLabel = new JLabel();
		JButton button1 = new JButton();
		JButton button2 = new JButton();
		FlatComponentsTest.TestDefaultButton button5 = new FlatComponentsTest.TestDefaultButton();
		JButton button3 = new JButton();
		JButton button12 = new JButton();
		JButton button13 = new JButton();
		JButton button14 = new JButton();
		JButton button15 = new JButton();
		JButton button16 = new JButton();
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
		JComboBox<String> comboBox5 = new JComboBox<>();
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
		JLabel scrollPaneLabel = new JLabel();
		JScrollPane scrollPane13 = new JScrollPane();
		JPanel panel1 = new JPanel();
		JScrollBar scrollBar2 = new JScrollBar();
		JScrollBar scrollBar3 = new JScrollBar();
		JSeparator separator2 = new JSeparator();
		JSlider slider2 = new JSlider();
		JSlider slider4 = new JSlider();
		JScrollPane scrollPane14 = new JScrollPane();
		progressBar3 = new JProgressBar();
		progressBar4 = new JProgressBar();
		JToolBar toolBar2 = new JToolBar();
		JButton button9 = new JButton();
		JButton button10 = new JButton();
		JButton button11 = new JButton();
		JToggleButton toggleButton7 = new JToggleButton();
		JLabel scrollBarLabel = new JLabel();
		JScrollBar scrollBar1 = new JScrollBar();
		JLabel label4 = new JLabel();
		JScrollBar scrollBar4 = new JScrollBar();
		JPanel panel3 = new JPanel();
		JLabel label3 = new JLabel();
		JScrollPane scrollPane15 = new JScrollPane();
		JEditorPane editorPane6 = new JEditorPane();
		JScrollPane scrollPane16 = new JScrollPane();
		JTextPane textPane6 = new JTextPane();
		JLabel separatorLabel = new JLabel();
		JSeparator separator1 = new JSeparator();
		JPanel panel2 = new JPanel();
		JLabel sliderLabel = new JLabel();
		JSlider slider1 = new JSlider();
		JSlider slider6 = new JSlider();
		JSlider slider3 = new JSlider();
		JSlider slider5 = new JSlider();
		JLabel progressBarLabel = new JLabel();
		progressBar1 = new JProgressBar();
		progressBar2 = new JProgressBar();
		indeterminateCheckBox = new JCheckBox();
		JLabel toolTipLabel = new JLabel();
		JToolTip toolTip1 = new JToolTip();
		JToolTip toolTip2 = new JToolTip();
		JLabel toolBarLabel = new JLabel();
		JToolBar toolBar1 = new JToolBar();
		JButton button4 = new JButton();
		JButton button6 = new JButton();
		JButton button7 = new JButton();
		JButton button8 = new JButton();
		JToggleButton toggleButton6 = new JToggleButton();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
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
		button1.setToolTipText("This button is enabled.");
		add(button1, "cell 1 1");

		//---- button2 ----
		button2.setText("disabled");
		button2.setDisplayedMnemonicIndex(0);
		button2.setEnabled(false);
		button2.setToolTipText("This button is disabled.");
		add(button2, "cell 2 1");

		//---- button5 ----
		button5.setText("default");
		button5.setDisplayedMnemonicIndex(0);
		button5.setToolTipText("Tool tip with\nmultiple\nlines.");
		add(button5, "cell 3 1");

		//---- button3 ----
		button3.setText("Help");
		button3.putClientProperty("JButton.buttonType", "help");
		add(button3, "cell 4 1");

		//---- button12 ----
		button12.setText("Help");
		button12.putClientProperty("JButton.buttonType", "help");
		button12.setEnabled(false);
		add(button12, "cell 4 1");

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
			"ccc",
			"dd",
			"e",
			"ff",
			"ggg",
			"hh",
			"i",
			"jj",
			"kkk"
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
			"ccc",
			"dd",
			"e",
			"ff",
			"ggg",
			"hh",
			"i",
			"jj",
			"kkk"
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

		//---- comboBox5 ----
		comboBox5.setPrototypeDisplayValue("12345");
		comboBox5.setModel(new DefaultComboBoxModel<>(new String[] {
			"wide popup if text is longer",
			"aa",
			"bbb",
			"cccc"
		}));
		add(comboBox5, "cell 5 5,growx");

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

		//---- scrollPaneLabel ----
		scrollPaneLabel.setText("JScrollPane:");
		add(scrollPaneLabel, "cell 0 13");

		//======== scrollPane13 ========
		{
			scrollPane13.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane13.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//======== panel1 ========
			{
				panel1.setPreferredSize(new Dimension(200, 200));
				panel1.setLayout(new BorderLayout());
			}
			scrollPane13.setViewportView(panel1);
		}
		add(scrollPane13, "cell 1 13,grow,width 70,height 70");
		add(scrollBar2, "cell 2 13 1 4,growy");

		//---- scrollBar3 ----
		scrollBar3.setEnabled(false);
		add(scrollBar3, "cell 2 13 1 4,growy");

		//---- separator2 ----
		separator2.setOrientation(SwingConstants.VERTICAL);
		add(separator2, "cell 2 13 1 4,growy");

		//---- slider2 ----
		slider2.setOrientation(SwingConstants.VERTICAL);
		slider2.setValue(30);
		add(slider2, "cell 2 13 1 4,growy");

		//---- slider4 ----
		slider4.setMinorTickSpacing(10);
		slider4.setPaintTicks(true);
		slider4.setMajorTickSpacing(50);
		slider4.setPaintLabels(true);
		slider4.setOrientation(SwingConstants.VERTICAL);
		slider4.setValue(30);
		add(slider4, "cell 2 13 1 4,growy");
		add(scrollPane14, "cell 3 13,grow");

		//---- progressBar3 ----
		progressBar3.setOrientation(SwingConstants.VERTICAL);
		progressBar3.setValue(50);
		add(progressBar3, "cell 4 13 1 4,growy");

		//---- progressBar4 ----
		progressBar4.setOrientation(SwingConstants.VERTICAL);
		progressBar4.setValue(55);
		progressBar4.setStringPainted(true);
		add(progressBar4, "cell 4 13 1 4,growy");

		//======== toolBar2 ========
		{
			toolBar2.setOrientation(SwingConstants.VERTICAL);

			//---- button9 ----
			button9.setIcon(UIManager.getIcon("Tree.closedIcon"));
			toolBar2.add(button9);

			//---- button10 ----
			button10.setIcon(UIManager.getIcon("Tree.openIcon"));
			toolBar2.add(button10);
			toolBar2.addSeparator();

			//---- button11 ----
			button11.setIcon(UIManager.getIcon("Tree.leafIcon"));
			toolBar2.add(button11);

			//---- toggleButton7 ----
			toggleButton7.setIcon(UIManager.getIcon("Tree.closedIcon"));
			toolBar2.add(toggleButton7);
		}
		add(toolBar2, "cell 4 13 1 4,growy");

		//---- scrollBarLabel ----
		scrollBarLabel.setText("JScrollBar:");
		add(scrollBarLabel, "cell 0 14");

		//---- scrollBar1 ----
		scrollBar1.setOrientation(Adjustable.HORIZONTAL);
		add(scrollBar1, "cell 1 14,growx");

		//---- label4 ----
		label4.setText("HTML:");
		add(label4, "cell 5 14");

		//---- scrollBar4 ----
		scrollBar4.setOrientation(Adjustable.HORIZONTAL);
		scrollBar4.setEnabled(false);
		add(scrollBar4, "cell 1 15,growx");

		//======== panel3 ========
		{
			panel3.setOpaque(false);
			panel3.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3",
				// columns
				"[]",
				// rows
				"[]" +
				"[]" +
				"[]"));

			//---- label3 ----
			label3.setText("<html>JLabel HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a></html>");
			panel3.add(label3, "cell 0 0");

			//======== scrollPane15 ========
			{

				//---- editorPane6 ----
				editorPane6.setContentType("text/html");
				editorPane6.setText("JEditorPane HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a>");
				scrollPane15.setViewportView(editorPane6);
			}
			panel3.add(scrollPane15, "cell 0 1,grow");

			//======== scrollPane16 ========
			{

				//---- textPane6 ----
				textPane6.setContentType("text/html");
				textPane6.setText("JTextPane HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a>");
				scrollPane16.setViewportView(textPane6);
			}
			panel3.add(scrollPane16, "cell 0 2,grow");
		}
		add(panel3, "cell 5 15 1 7,aligny top,grow 100 0");

		//---- separatorLabel ----
		separatorLabel.setText("JSeparator:");
		add(separatorLabel, "cell 0 16");
		add(separator1, "cell 1 16,growx");

		//======== panel2 ========
		{
			panel2.setBorder(new TitledBorder("TitledBorder"));
			panel2.setOpaque(false);
			panel2.setLayout(new FlowLayout());
		}
		add(panel2, "cell 3 16,grow");

		//---- sliderLabel ----
		sliderLabel.setText("JSlider:");
		add(sliderLabel, "cell 0 17");

		//---- slider1 ----
		slider1.setValue(30);
		add(slider1, "cell 1 17 3 1,aligny top,grow 100 0");

		//---- slider6 ----
		slider6.setEnabled(false);
		slider6.setValue(30);
		add(slider6, "cell 1 17 3 1,aligny top,growy 0");

		//---- slider3 ----
		slider3.setMinorTickSpacing(10);
		slider3.setPaintTicks(true);
		slider3.setMajorTickSpacing(50);
		slider3.setPaintLabels(true);
		slider3.setValue(30);
		add(slider3, "cell 1 18 3 1,aligny top,grow 100 0");

		//---- slider5 ----
		slider5.setMinorTickSpacing(10);
		slider5.setPaintTicks(true);
		slider5.setMajorTickSpacing(50);
		slider5.setPaintLabels(true);
		slider5.setEnabled(false);
		slider5.setValue(30);
		add(slider5, "cell 1 18 3 1,aligny top,growy 0");

		//---- progressBarLabel ----
		progressBarLabel.setText("JProgressBar:");
		add(progressBarLabel, "cell 0 19");

		//---- progressBar1 ----
		progressBar1.setValue(50);
		add(progressBar1, "cell 1 19 3 1,growx");

		//---- progressBar2 ----
		progressBar2.setStringPainted(true);
		progressBar2.setValue(55);
		add(progressBar2, "cell 1 19 3 1,growx");

		//---- indeterminateCheckBox ----
		indeterminateCheckBox.setText("indeterminate");
		indeterminateCheckBox.addActionListener(e -> indeterminateCheckBoxActionPerformed());
		add(indeterminateCheckBox, "cell 4 19");

		//---- toolTipLabel ----
		toolTipLabel.setText("JToolTip:");
		add(toolTipLabel, "cell 0 20");

		//---- toolTip1 ----
		toolTip1.setTipText("Some text in tool tip.");
		add(toolTip1, "cell 1 20 3 1");

		//---- toolTip2 ----
		toolTip2.setTipText("Tool tip with\nmultiple\nlines.");
		add(toolTip2, "cell 1 20 3 1");

		//---- toolBarLabel ----
		toolBarLabel.setText("JToolBar:");
		add(toolBarLabel, "cell 0 21");

		//======== toolBar1 ========
		{

			//---- button4 ----
			button4.setIcon(UIManager.getIcon("Tree.closedIcon"));
			toolBar1.add(button4);

			//---- button6 ----
			button6.setIcon(UIManager.getIcon("Tree.openIcon"));
			toolBar1.add(button6);
			toolBar1.addSeparator();

			//---- button7 ----
			button7.setIcon(UIManager.getIcon("Tree.leafIcon"));
			toolBar1.add(button7);
			toolBar1.addSeparator();

			//---- button8 ----
			button8.setText("Text");
			button8.setIcon(UIManager.getIcon("Tree.expandedIcon"));
			toolBar1.add(button8);

			//---- toggleButton6 ----
			toggleButton6.setText("Toggle");
			toggleButton6.setIcon(UIManager.getIcon("Tree.leafIcon"));
			toggleButton6.setSelected(true);
			toolBar1.add(toggleButton6);
		}
		add(toolBar1, "cell 1 21 3 1,growx");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

//		BasicComboBoxRenderer customaRenderer = new BasicComboBoxRenderer();
//		customaRenderer.setBorder( new LineBorder( Color.red ) );
//		comboBox1.setRenderer( customaRenderer );
//		comboBox3.setRenderer( customaRenderer );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JProgressBar progressBar3;
	private JProgressBar progressBar4;
	private JProgressBar progressBar1;
	private JProgressBar progressBar2;
	private JCheckBox indeterminateCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class TestDefaultButton --------------------------------------------

	private static class TestDefaultButton
		extends JButton
	{
		@Override
		public boolean isDefaultButton() {
			return true;
		}
	}
}
