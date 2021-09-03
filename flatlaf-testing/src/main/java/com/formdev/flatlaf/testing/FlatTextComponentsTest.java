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

package com.formdev.flatlaf.testing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.DefaultEditorKit;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatTextComponentsTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatTextComponentsTest" );
			frame.showFrame( FlatTextComponentsTest::new );
		} );
	}

	FlatTextComponentsTest() {
		initComponents();
	}

	private void changeText() {
		textField1.setText( "new text" );
	}

	private void paddingChanged() {
		Insets padding = new Insets(
			(int) topPaddingField.getValue(),
			(int) leftPaddingField.getValue(),
			(int) bottomPaddingField.getValue(),
			(int) rightPaddingField.getValue() );
		if( padding.equals( new Insets( 0, 0, 0, 0 ) ) )
			padding = null;

		putTextFieldClientProperty( FlatClientProperties.TEXT_FIELD_PADDING, padding );
	}

	private void leadingIcon() {
		putTextFieldClientProperty( FlatClientProperties.TEXT_FIELD_LEADING_ICON, leadingIconCheckBox.isSelected()
			? new TestIcon( 8, 16, Color.blue ) : null );
	}

	private void trailingIcon() {
		putTextFieldClientProperty( FlatClientProperties.TEXT_FIELD_TRAILING_ICON, trailingIconCheckBox.isSelected()
			? new TestIcon( 24, 12, Color.magenta ) : null );
	}

	private void leadingComponent() {
		putTextFieldClientProperty( FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, () -> {
			if( !leadingComponentCheckBox.isSelected() )
				return null;

			JLabel l = new JLabel( "lead" );
			l.setOpaque( true );
			l.setBackground( Color.green );
			return l;
		} );
	}

	private void trailingComponent() {
		putTextFieldClientProperty( FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, () -> {
			if( !trailingComponentCheckBox.isSelected() )
				return null;

			JLabel l = new JLabel( "tr" );
			l.setOpaque( true );
			l.setBackground( Color.magenta );
			return l;
		} );
	}

	private void putTextFieldClientProperty( String key, Object value ) {
		for( Component c : getComponents() ) {
			if( c instanceof JTextField )
				((JTextField)c).putClientProperty( key, value );
		}
	}

	private void putTextFieldClientProperty( String key, Supplier<Component> value ) {
		for( Component c : getComponents() ) {
			if( c instanceof JTextField )
				((JTextField)c).putClientProperty( key, value.get() );
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel textFieldLabel = new JLabel();
		textField1 = new JTextField();
		JTextField textField3 = new JTextField();
		JTextField textField2 = new JTextField();
		JLabel formattedTextFieldLabel = new JLabel();
		JFormattedTextField formattedTextField1 = new JFormattedTextField();
		JFormattedTextField formattedTextField3 = new JFormattedTextField();
		JPanel panel1 = new JPanel();
		JButton button1 = new JButton();
		JLabel leftPaddingLabel = new JLabel();
		leftPaddingField = new JSpinner();
		JLabel rightPaddingLabel = new JLabel();
		rightPaddingField = new JSpinner();
		JLabel topPaddingLabel = new JLabel();
		topPaddingField = new JSpinner();
		JLabel bottomPaddingLabel = new JLabel();
		bottomPaddingField = new JSpinner();
		leadingIconCheckBox = new JCheckBox();
		trailingIconCheckBox = new JCheckBox();
		leadingComponentCheckBox = new JCheckBox();
		trailingComponentCheckBox = new JCheckBox();
		JLabel passwordFieldLabel = new JLabel();
		JPasswordField passwordField1 = new JPasswordField();
		JPasswordField passwordField3 = new JPasswordField();
		JLabel textAreaLabel = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		JTextArea textArea1 = new JTextArea();
		JScrollPane scrollPane3 = new JScrollPane();
		JTextArea textArea3 = new JTextArea();
		JLabel editorPaneLabel = new JLabel();
		JScrollPane scrollPane5 = new JScrollPane();
		JEditorPane editorPane1 = new JEditorPane();
		JScrollPane scrollPane7 = new JScrollPane();
		JEditorPane editorPane3 = new JEditorPane();
		JLabel textPaneLabel = new JLabel();
		JScrollPane scrollPane9 = new JScrollPane();
		JTextPane textPane1 = new JTextPane();
		JScrollPane scrollPane11 = new JScrollPane();
		JTextPane textPane3 = new JTextPane();
		JLabel comboBoxLabel = new JLabel();
		JComboBox<String> comboBox1 = new JComboBox<>();
		JComboBox<String> comboBox3 = new JComboBox<>();
		JLabel spinnerLabel = new JLabel();
		JSpinner spinner1 = new JSpinner();
		JLabel label2 = new JLabel();
		JComboBox<String> comboBox2 = new JComboBox<>();
		JSpinner spinner2 = new JSpinner();
		JLabel label1 = new JLabel();
		JComboBox<String> comboBox5 = new JComboBox<>();
		JSpinner spinner4 = new JSpinner();
		JLabel label3 = new JLabel();
		JComboBox<String> comboBox4 = new JComboBox<>();
		JSpinner spinner3 = new JSpinner();
		JLabel label4 = new JLabel();
		JComboBox<String> comboBox6 = new JComboBox<>();
		JSpinner spinner5 = new JSpinner();
		JPopupMenu popupMenu1 = new JPopupMenu();
		JMenuItem cutMenuItem = new JMenuItem();
		JMenuItem copyMenuItem = new JMenuItem();
		JMenuItem pasteMenuItem = new JMenuItem();

		//======== this ========
		setName("this");
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[]" +
			"[::100]" +
			"[100,fill]" +
			"[fill]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[50,fill]" +
			"[50,fill]" +
			"[50,fill]" +
			"[]" +
			"[]para" +
			"[40]" +
			"[40]" +
			"[]" +
			"[]" +
			"[::14]" +
			"[::14]" +
			"[]" +
			"[]"));

		//---- textFieldLabel ----
		textFieldLabel.setText("JTextField:");
		textFieldLabel.setDisplayedMnemonic('T');
		textFieldLabel.setLabelFor(textField1);
		textFieldLabel.setName("textFieldLabel");
		add(textFieldLabel, "cell 0 0");

		//---- textField1 ----
		textField1.setText("editable");
		textField1.setComponentPopupMenu(popupMenu1);
		textField1.putClientProperty("JTextField.placeholderText", "place");
		textField1.setName("textField1");
		add(textField1, "cell 1 0,growx");

		//---- textField3 ----
		textField3.setText("longer text for testing horizontal scrolling");
		textField3.setComponentPopupMenu(popupMenu1);
		textField3.putClientProperty("JTextField.placeholderText", "place");
		textField3.setName("textField3");
		add(textField3, "cell 2 0,growx");

		//---- textField2 ----
		textField2.setText("partly selected");
		textField2.setSelectionStart(1);
		textField2.setSelectionEnd(4);
		textField2.setComponentPopupMenu(popupMenu1);
		textField2.putClientProperty("JTextField.placeholderText", "place");
		textField2.setName("textField2");
		add(textField2, "cell 3 0");

		//---- formattedTextFieldLabel ----
		formattedTextFieldLabel.setText("JFormattedTextField:");
		formattedTextFieldLabel.setDisplayedMnemonic('F');
		formattedTextFieldLabel.setLabelFor(formattedTextField1);
		formattedTextFieldLabel.setName("formattedTextFieldLabel");
		add(formattedTextFieldLabel, "cell 0 1");

		//---- formattedTextField1 ----
		formattedTextField1.setText("editable");
		formattedTextField1.setComponentPopupMenu(popupMenu1);
		formattedTextField1.putClientProperty("JTextField.placeholderText", "place");
		formattedTextField1.setName("formattedTextField1");
		add(formattedTextField1, "cell 1 1,growx");

		//---- formattedTextField3 ----
		formattedTextField3.setText("longer text for testing horizontal scrolling");
		formattedTextField3.setComponentPopupMenu(popupMenu1);
		formattedTextField3.putClientProperty("JTextField.placeholderText", "place");
		formattedTextField3.setName("formattedTextField3");
		add(formattedTextField3, "cell 2 1,growx");

		//======== panel1 ========
		{
			panel1.setBorder(new TitledBorder("Control"));
			panel1.setName("panel1");
			panel1.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]0" +
				"[]" +
				"[]0" +
				"[]"));

			//---- button1 ----
			button1.setText("change text");
			button1.setName("button1");
			button1.addActionListener(e -> changeText());
			panel1.add(button1, "cell 0 0 2 1,alignx left,growx 0");

			//---- leftPaddingLabel ----
			leftPaddingLabel.setText("Left padding:");
			leftPaddingLabel.setName("leftPaddingLabel");
			panel1.add(leftPaddingLabel, "cell 0 1");

			//---- leftPaddingField ----
			leftPaddingField.setName("leftPaddingField");
			leftPaddingField.addChangeListener(e -> paddingChanged());
			panel1.add(leftPaddingField, "cell 1 1");

			//---- rightPaddingLabel ----
			rightPaddingLabel.setText("Right padding:");
			rightPaddingLabel.setName("rightPaddingLabel");
			panel1.add(rightPaddingLabel, "cell 0 2");

			//---- rightPaddingField ----
			rightPaddingField.setName("rightPaddingField");
			rightPaddingField.addChangeListener(e -> paddingChanged());
			panel1.add(rightPaddingField, "cell 1 2");

			//---- topPaddingLabel ----
			topPaddingLabel.setText("Top padding:");
			topPaddingLabel.setName("topPaddingLabel");
			panel1.add(topPaddingLabel, "cell 0 3");

			//---- topPaddingField ----
			topPaddingField.setName("topPaddingField");
			topPaddingField.addChangeListener(e -> paddingChanged());
			panel1.add(topPaddingField, "cell 1 3");

			//---- bottomPaddingLabel ----
			bottomPaddingLabel.setText("Bottom padding:");
			bottomPaddingLabel.setName("bottomPaddingLabel");
			panel1.add(bottomPaddingLabel, "cell 0 4");

			//---- bottomPaddingField ----
			bottomPaddingField.setName("bottomPaddingField");
			bottomPaddingField.addChangeListener(e -> paddingChanged());
			panel1.add(bottomPaddingField, "cell 1 4");

			//---- leadingIconCheckBox ----
			leadingIconCheckBox.setText("leading icon");
			leadingIconCheckBox.setName("leadingIconCheckBox");
			leadingIconCheckBox.addActionListener(e -> leadingIcon());
			panel1.add(leadingIconCheckBox, "cell 0 5 2 1,alignx left,growx 0");

			//---- trailingIconCheckBox ----
			trailingIconCheckBox.setText("trailing icon");
			trailingIconCheckBox.setName("trailingIconCheckBox");
			trailingIconCheckBox.addActionListener(e -> trailingIcon());
			panel1.add(trailingIconCheckBox, "cell 0 6 2 1,alignx left,growx 0");

			//---- leadingComponentCheckBox ----
			leadingComponentCheckBox.setText("leading component");
			leadingComponentCheckBox.setName("leadingComponentCheckBox");
			leadingComponentCheckBox.addActionListener(e -> leadingComponent());
			panel1.add(leadingComponentCheckBox, "cell 0 7 2 1,alignx left,growx 0");

			//---- trailingComponentCheckBox ----
			trailingComponentCheckBox.setText("trailing component");
			trailingComponentCheckBox.setName("trailingComponentCheckBox");
			trailingComponentCheckBox.addActionListener(e -> trailingComponent());
			panel1.add(trailingComponentCheckBox, "cell 0 8 2 1,alignx left,growx 0");
		}
		add(panel1, "cell 4 0 1 6,aligny top,growy 0");

		//---- passwordFieldLabel ----
		passwordFieldLabel.setText("JPasswordField:");
		passwordFieldLabel.setDisplayedMnemonic('P');
		passwordFieldLabel.setLabelFor(passwordField1);
		passwordFieldLabel.setName("passwordFieldLabel");
		add(passwordFieldLabel, "cell 0 2");

		//---- passwordField1 ----
		passwordField1.setText("editable");
		passwordField1.setComponentPopupMenu(popupMenu1);
		passwordField1.putClientProperty("JTextField.placeholderText", "place");
		passwordField1.setName("passwordField1");
		add(passwordField1, "cell 1 2,growx");

		//---- passwordField3 ----
		passwordField3.setText("longer text for testing horizontal scrolling");
		passwordField3.setComponentPopupMenu(popupMenu1);
		passwordField3.putClientProperty("JTextField.placeholderText", "place");
		passwordField3.setName("passwordField3");
		add(passwordField3, "cell 2 2,growx");

		//---- textAreaLabel ----
		textAreaLabel.setText("JTextArea:");
		textAreaLabel.setDisplayedMnemonic('A');
		textAreaLabel.setLabelFor(textArea1);
		textAreaLabel.setName("textAreaLabel");
		add(textAreaLabel, "cell 0 3");

		//======== scrollPane1 ========
		{
			scrollPane1.setName("scrollPane1");

			//---- textArea1 ----
			textArea1.setText("editable");
			textArea1.setComponentPopupMenu(popupMenu1);
			textArea1.setName("textArea1");
			scrollPane1.setViewportView(textArea1);
		}
		add(scrollPane1, "cell 1 3,growx");

		//======== scrollPane3 ========
		{
			scrollPane3.setName("scrollPane3");

			//---- textArea3 ----
			textArea3.setText("longer text for testing horizontal scrolling");
			textArea3.setComponentPopupMenu(popupMenu1);
			textArea3.setName("textArea3");
			scrollPane3.setViewportView(textArea3);
		}
		add(scrollPane3, "cell 2 3,growx");

		//---- editorPaneLabel ----
		editorPaneLabel.setText("JEditorPane");
		editorPaneLabel.setDisplayedMnemonic('J');
		editorPaneLabel.setLabelFor(editorPane1);
		editorPaneLabel.setName("editorPaneLabel");
		add(editorPaneLabel, "cell 0 4");

		//======== scrollPane5 ========
		{
			scrollPane5.setName("scrollPane5");

			//---- editorPane1 ----
			editorPane1.setText("editable");
			editorPane1.setComponentPopupMenu(popupMenu1);
			editorPane1.setName("editorPane1");
			scrollPane5.setViewportView(editorPane1);
		}
		add(scrollPane5, "cell 1 4,growx");

		//======== scrollPane7 ========
		{
			scrollPane7.setName("scrollPane7");

			//---- editorPane3 ----
			editorPane3.setText("longer text for testing horizontal scrolling");
			editorPane3.setComponentPopupMenu(popupMenu1);
			editorPane3.setName("editorPane3");
			scrollPane7.setViewportView(editorPane3);
		}
		add(scrollPane7, "cell 2 4,growx");

		//---- textPaneLabel ----
		textPaneLabel.setText("JTextPane:");
		textPaneLabel.setDisplayedMnemonic('N');
		textPaneLabel.setLabelFor(textPane1);
		textPaneLabel.setName("textPaneLabel");
		add(textPaneLabel, "cell 0 5");

		//======== scrollPane9 ========
		{
			scrollPane9.setName("scrollPane9");

			//---- textPane1 ----
			textPane1.setText("editable");
			textPane1.setComponentPopupMenu(popupMenu1);
			textPane1.setName("textPane1");
			scrollPane9.setViewportView(textPane1);
		}
		add(scrollPane9, "cell 1 5,growx");

		//======== scrollPane11 ========
		{
			scrollPane11.setName("scrollPane11");

			//---- textPane3 ----
			textPane3.setText("longer text for testing horizontal scrolling");
			textPane3.setComponentPopupMenu(popupMenu1);
			textPane3.setName("textPane3");
			scrollPane11.setViewportView(textPane3);
		}
		add(scrollPane11, "cell 2 5,growx");

		//---- comboBoxLabel ----
		comboBoxLabel.setText("JComboBox:");
		comboBoxLabel.setDisplayedMnemonic('C');
		comboBoxLabel.setLabelFor(comboBox1);
		comboBoxLabel.setName("comboBoxLabel");
		add(comboBoxLabel, "cell 0 6");

		//---- comboBox1 ----
		comboBox1.setEditable(true);
		comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
			"editable",
			"a",
			"bb",
			"ccc"
		}));
		comboBox1.setComponentPopupMenu(popupMenu1);
		comboBox1.setName("comboBox1");
		add(comboBox1, "cell 1 6,growx");

		//---- comboBox3 ----
		comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {
			"longer text for testing horizontal scrolling",
			"a",
			"bb",
			"ccc"
		}));
		comboBox3.setEditable(true);
		comboBox3.setPrototypeDisplayValue("12345");
		comboBox3.setComponentPopupMenu(popupMenu1);
		comboBox3.setName("comboBox3");
		add(comboBox3, "cell 2 6,growx,wmin 50");

		//---- spinnerLabel ----
		spinnerLabel.setText("JSpinner:");
		spinnerLabel.setDisplayedMnemonic('S');
		spinnerLabel.setLabelFor(spinner1);
		spinnerLabel.setName("spinnerLabel");
		add(spinnerLabel, "cell 0 7");

		//---- spinner1 ----
		spinner1.setComponentPopupMenu(popupMenu1);
		spinner1.setName("spinner1");
		add(spinner1, "cell 1 7,growx");

		//---- label2 ----
		label2.setText("<html>Large row height:<br>(default pref height)</html>");
		label2.setName("label2");
		add(label2, "cell 0 8,aligny top,growy 0");

		//---- comboBox2 ----
		comboBox2.setEditable(true);
		comboBox2.setName("comboBox2");
		add(comboBox2, "cell 1 8,grow");

		//---- spinner2 ----
		spinner2.setName("spinner2");
		add(spinner2, "cell 1 9,grow");

		//---- label1 ----
		label1.setText("Large pref height:");
		label1.setName("label1");
		add(label1, "cell 0 10,aligny top,growy 0");

		//---- comboBox5 ----
		comboBox5.setPreferredSize(new Dimension(60, 40));
		comboBox5.setEditable(true);
		comboBox5.setName("comboBox5");
		add(comboBox5, "cell 1 10,growx");

		//---- spinner4 ----
		spinner4.setPreferredSize(new Dimension(60, 40));
		spinner4.setName("spinner4");
		add(spinner4, "cell 1 11,growx");

		//---- label3 ----
		label3.setText("<html>Small row height:<br>(default pref height)</html>");
		label3.setName("label3");
		add(label3, "cell 0 12 1 2,aligny top,growy 0");

		//---- comboBox4 ----
		comboBox4.setEditable(true);
		comboBox4.setName("comboBox4");
		add(comboBox4, "cell 1 12,growx");

		//---- spinner3 ----
		spinner3.setName("spinner3");
		add(spinner3, "cell 1 13,growx");

		//---- label4 ----
		label4.setText("Small pref height:");
		label4.setName("label4");
		add(label4, "cell 0 14 1 2,aligny top,growy 0");

		//---- comboBox6 ----
		comboBox6.setEditable(true);
		comboBox6.setPreferredSize(new Dimension(60, 14));
		comboBox6.setMinimumSize(new Dimension(60, 14));
		comboBox6.setName("comboBox6");
		add(comboBox6, "cell 1 14,growx");

		//---- spinner5 ----
		spinner5.setMinimumSize(new Dimension(60, 14));
		spinner5.setPreferredSize(new Dimension(60, 14));
		spinner5.setName("spinner5");
		add(spinner5, "cell 1 15,growx,hmax 14");

		//======== popupMenu1 ========
		{
			popupMenu1.setName("popupMenu1");

			//---- cutMenuItem ----
			cutMenuItem.setText("Cut");
			cutMenuItem.setName("cutMenuItem");
			popupMenu1.add(cutMenuItem);

			//---- copyMenuItem ----
			copyMenuItem.setText("Copy");
			copyMenuItem.setName("copyMenuItem");
			popupMenu1.add(copyMenuItem);

			//---- pasteMenuItem ----
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setName("pasteMenuItem");
			popupMenu1.add(pasteMenuItem);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		cutMenuItem.addActionListener( new DefaultEditorKit.CutAction() );
		copyMenuItem.addActionListener( new DefaultEditorKit.CopyAction() );
		pasteMenuItem.addActionListener( new DefaultEditorKit.PasteAction() );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTextField textField1;
	private JSpinner leftPaddingField;
	private JSpinner rightPaddingField;
	private JSpinner topPaddingField;
	private JSpinner bottomPaddingField;
	private JCheckBox leadingIconCheckBox;
	private JCheckBox trailingIconCheckBox;
	private JCheckBox leadingComponentCheckBox;
	private JCheckBox trailingComponentCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- TestIcon -----------------------------------------------------------

	private static class TestIcon
		implements Icon
	{
		private final int width;
		private final int height;
		private final Color color;

		TestIcon( int width, int height, Color color ) {
			this.width = width;
			this.height = height;
			this.color = color;
		}

		@Override
		public void paintIcon( Component c, Graphics g, int x, int y ) {
			g.setColor( color );
			g.drawRect( x, y, getIconWidth() - 1, getIconHeight() - 1 );
		}

		@Override
		public int getIconWidth() {
			return UIScale.scale( width );
		}

		@Override
		public int getIconHeight() {
			return UIScale.scale( height );
		}
	}
}
