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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.components.*;
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
		updatePreferredSizes();

		// exclude from tab focus traversal
		controlPanel.setFocusTraversalPolicyProvider( true );
		controlPanel.setFocusTraversalPolicy( new NotFocusableTraversalPolicy() );
		placeholderPanel.setFocusTraversalPolicyProvider( true );
		placeholderPanel.setFocusTraversalPolicy( new NotFocusableTraversalPolicy() );
	}

	@Override
	public void updateUI() {
		super.updateUI();

		if( comboBox5 != null )
			updatePreferredSizes();
	}

	private void updatePreferredSizes() {
		Dimension size40 = UIScale.scale( new Dimension( 60, 40 ) );
		comboBox5.setPreferredSize( size40 );
		spinner4.setPreferredSize( size40 );

		Dimension size14 = UIScale.scale( new Dimension( 60, 14 ) );
		comboBox6.setPreferredSize( size14 );
		comboBox6.setMinimumSize( size14 );
		spinner5.setPreferredSize( size14 );
		spinner5.setMinimumSize( size14 );
	}

	private void editableChanged() {
		boolean editable = editableCheckBox.isSelected();
		for( Component c : getComponents() ) {
			if( c instanceof JTextComponent )
				((JTextComponent)c).setEditable( editable );
		}
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
			l.setVisible( leadingComponentVisibleCheckBox.isSelected() );
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
			l.setVisible( trailingComponentVisibleCheckBox.isSelected() );
			return l;
		} );
	}

	private void leadingComponentVisible() {
		setLeadingTrailingComponentVisible( FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT,
			leadingComponentVisibleCheckBox.isSelected() );
	}

	private void trailingComponentVisible() {
		setLeadingTrailingComponentVisible( FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT,
			trailingComponentVisibleCheckBox.isSelected() );
	}

	private void setLeadingTrailingComponentVisible( String key, boolean visible ) {
		for( Component c : getComponents() ) {
			if( c instanceof JTextField ) {
				Object value = ((JTextField)c).getClientProperty( key );
				if( value instanceof JComponent ) {
					((JComponent)value).setVisible( visible );
					c.revalidate();
					c.repaint();
				}
			}
		}
	}

	private void showClearButton() {
		putTextFieldClientProperty( FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON,
			showClearButtonCheckBox.isSelected() );
	}

	private void showRevealButton() {
		for( Component c : getComponents() ) {
			if( c instanceof JPasswordField )
				((JPasswordField)c).putClientProperty(FlatClientProperties.STYLE,
					showRevealButtonCheckBox.isSelected() ? "showRevealButton: true" : null );
		}
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

	private void dragEnabledChanged() {
		boolean dragEnabled = dragEnabledCheckBox.isSelected();
		textField.setDragEnabled( dragEnabled );
		textArea.setDragEnabled( dragEnabled );
		textPane.setDragEnabled( dragEnabled );
		editorPane.setDragEnabled( dragEnabled );
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
		controlPanel = new JPanel();
		editableCheckBox = new JCheckBox();
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
		leadingComponentVisibleCheckBox = new JCheckBox();
		trailingComponentVisibleCheckBox = new JCheckBox();
		showClearButtonCheckBox = new JCheckBox();
		showRevealButtonCheckBox = new JCheckBox();
		placeholderPanel = new JPanel();
		JLabel label7 = new JLabel();
		FlatTextField flatTextField1 = new FlatTextField();
		JLabel label8 = new JLabel();
		FlatTextField flatTextField2 = new FlatTextField();
		JLabel label9 = new JLabel();
		FlatTextField flatTextField3 = new FlatTextField();
		JLabel label10 = new JLabel();
		FlatTextField flatTextField4 = new FlatTextField();
		JLabel label11 = new JLabel();
		FlatTextField flatTextField5 = new FlatTextField();
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
		JSpinner spinner6 = new JSpinner();
		JLabel label2 = new JLabel();
		JComboBox<String> comboBox2 = new JComboBox<>();
		JSpinner spinner2 = new JSpinner();
		JLabel label1 = new JLabel();
		comboBox5 = new JComboBox<>();
		spinner4 = new JSpinner();
		JLabel label3 = new JLabel();
		JComboBox<String> comboBox4 = new JComboBox<>();
		JSpinner spinner3 = new JSpinner();
		JLabel label4 = new JLabel();
		comboBox6 = new JComboBox<>();
		spinner5 = new JSpinner();
		JLabel label5 = new JLabel();
		textField = new JTextField();
		dragEnabledCheckBox = new JCheckBox();
		JLabel label6 = new JLabel();
		JScrollPane scrollPane2 = new JScrollPane();
		textArea = new JTextArea();
		JScrollPane scrollPane4 = new JScrollPane();
		textPane = new JTextPane();
		JScrollPane scrollPane6 = new JScrollPane();
		editorPane = new JEditorPane();
		JPopupMenu popupMenu1 = new JPopupMenu();
		JMenuItem cutMenuItem = new JMenuItem();
		JMenuItem copyMenuItem = new JMenuItem();
		JMenuItem pasteMenuItem = new JMenuItem();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[]" +
			"[::100]" +
			"[100,fill]" +
			"[fill]" +
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
			"[]para" +
			"[]" +
			"[90,fill]"));

		//---- textFieldLabel ----
		textFieldLabel.setText("JTextField:");
		textFieldLabel.setDisplayedMnemonic('T');
		textFieldLabel.setLabelFor(textField1);
		add(textFieldLabel, "cell 0 0");

		//---- textField1 ----
		textField1.setText("editable");
		textField1.setComponentPopupMenu(popupMenu1);
		textField1.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "place");
		add(textField1, "cell 1 0,growx");

		//---- textField3 ----
		textField3.setText("longer text for testing horizontal scrolling");
		textField3.setComponentPopupMenu(popupMenu1);
		textField3.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "place");
		add(textField3, "cell 2 0,growx");

		//---- textField2 ----
		textField2.setText("partly selected");
		textField2.setSelectionStart(1);
		textField2.setSelectionEnd(4);
		textField2.setComponentPopupMenu(popupMenu1);
		textField2.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "place");
		add(textField2, "cell 3 0");

		//---- formattedTextFieldLabel ----
		formattedTextFieldLabel.setText("JFormattedTextField:");
		formattedTextFieldLabel.setDisplayedMnemonic('F');
		formattedTextFieldLabel.setLabelFor(formattedTextField1);
		add(formattedTextFieldLabel, "cell 0 1");

		//---- formattedTextField1 ----
		formattedTextField1.setText("editable");
		formattedTextField1.setComponentPopupMenu(popupMenu1);
		formattedTextField1.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "place");
		add(formattedTextField1, "cell 1 1,growx");

		//---- formattedTextField3 ----
		formattedTextField3.setText("longer text for testing horizontal scrolling");
		formattedTextField3.setComponentPopupMenu(popupMenu1);
		formattedTextField3.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "place");
		add(formattedTextField3, "cell 2 1,growx");

		//======== controlPanel ========
		{
			controlPanel.setBorder(new TitledBorder("Control"));
			controlPanel.putClientProperty("FlatLaf.internal.testing.ignore", true);
			controlPanel.setLayout(new MigLayout(
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
				"[]" +
				"[]0" +
				"[]" +
				"[]0" +
				"[]" +
				"[]0" +
				"[]" +
				"[]" +
				"[]"));

			//---- editableCheckBox ----
			editableCheckBox.setText("editable");
			editableCheckBox.setSelected(true);
			editableCheckBox.setFocusable(false);
			editableCheckBox.addActionListener(e -> editableChanged());
			controlPanel.add(editableCheckBox, "cell 0 0 2 1,alignx left,growx 0");

			//---- button1 ----
			button1.setText("change text");
			button1.setFocusable(false);
			button1.addActionListener(e -> changeText());
			controlPanel.add(button1, "cell 0 1 2 1,alignx left,growx 0");

			//---- leftPaddingLabel ----
			leftPaddingLabel.setText("Left padding:");
			controlPanel.add(leftPaddingLabel, "cell 0 2");

			//---- leftPaddingField ----
			leftPaddingField.addChangeListener(e -> paddingChanged());
			controlPanel.add(leftPaddingField, "cell 1 2");

			//---- rightPaddingLabel ----
			rightPaddingLabel.setText("Right padding:");
			controlPanel.add(rightPaddingLabel, "cell 0 3");

			//---- rightPaddingField ----
			rightPaddingField.addChangeListener(e -> paddingChanged());
			controlPanel.add(rightPaddingField, "cell 1 3");

			//---- topPaddingLabel ----
			topPaddingLabel.setText("Top padding:");
			controlPanel.add(topPaddingLabel, "cell 0 4");

			//---- topPaddingField ----
			topPaddingField.addChangeListener(e -> paddingChanged());
			controlPanel.add(topPaddingField, "cell 1 4");

			//---- bottomPaddingLabel ----
			bottomPaddingLabel.setText("Bottom padding:");
			controlPanel.add(bottomPaddingLabel, "cell 0 5");

			//---- bottomPaddingField ----
			bottomPaddingField.addChangeListener(e -> paddingChanged());
			controlPanel.add(bottomPaddingField, "cell 1 5");

			//---- leadingIconCheckBox ----
			leadingIconCheckBox.setText("leading icon");
			leadingIconCheckBox.setFocusable(false);
			leadingIconCheckBox.addActionListener(e -> leadingIcon());
			controlPanel.add(leadingIconCheckBox, "cell 0 6 2 1,alignx left,growx 0");

			//---- trailingIconCheckBox ----
			trailingIconCheckBox.setText("trailing icon");
			trailingIconCheckBox.setFocusable(false);
			trailingIconCheckBox.addActionListener(e -> trailingIcon());
			controlPanel.add(trailingIconCheckBox, "cell 0 7 2 1,alignx left,growx 0");

			//---- leadingComponentCheckBox ----
			leadingComponentCheckBox.setText("leading component");
			leadingComponentCheckBox.setFocusable(false);
			leadingComponentCheckBox.addActionListener(e -> leadingComponent());
			controlPanel.add(leadingComponentCheckBox, "cell 0 8 2 1,alignx left,growx 0");

			//---- trailingComponentCheckBox ----
			trailingComponentCheckBox.setText("trailing component");
			trailingComponentCheckBox.setFocusable(false);
			trailingComponentCheckBox.addActionListener(e -> trailingComponent());
			controlPanel.add(trailingComponentCheckBox, "cell 0 9 2 1,alignx left,growx 0");

			//---- leadingComponentVisibleCheckBox ----
			leadingComponentVisibleCheckBox.setText("leading component visible");
			leadingComponentVisibleCheckBox.setSelected(true);
			leadingComponentVisibleCheckBox.setFocusable(false);
			leadingComponentVisibleCheckBox.addActionListener(e -> leadingComponentVisible());
			controlPanel.add(leadingComponentVisibleCheckBox, "cell 0 10 2 1,alignx left,growx 0");

			//---- trailingComponentVisibleCheckBox ----
			trailingComponentVisibleCheckBox.setText("trailing component visible");
			trailingComponentVisibleCheckBox.setSelected(true);
			trailingComponentVisibleCheckBox.setFocusable(false);
			trailingComponentVisibleCheckBox.addActionListener(e -> trailingComponentVisible());
			controlPanel.add(trailingComponentVisibleCheckBox, "cell 0 11 2 1,alignx left,growx 0");

			//---- showClearButtonCheckBox ----
			showClearButtonCheckBox.setText("clear button");
			showClearButtonCheckBox.setFocusable(false);
			showClearButtonCheckBox.addActionListener(e -> showClearButton());
			controlPanel.add(showClearButtonCheckBox, "cell 0 12 2 1,alignx left,growx 0");

			//---- showRevealButtonCheckBox ----
			showRevealButtonCheckBox.setText("password reveal button");
			showRevealButtonCheckBox.setFocusable(false);
			showRevealButtonCheckBox.addActionListener(e -> showRevealButton());
			controlPanel.add(showRevealButtonCheckBox, "cell 0 13 2 1,alignx left,growx 0");
		}
		add(controlPanel, "cell 4 0 1 10,aligny top,growy 0");

		//======== placeholderPanel ========
		{
			placeholderPanel.setBorder(new TitledBorder("Placeholder"));
			placeholderPanel.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//---- label7 ----
			label7.setText("leading");
			placeholderPanel.add(label7, "cell 0 0");

			//---- flatTextField1 ----
			flatTextField1.setHorizontalAlignment(SwingConstants.LEADING);
			flatTextField1.setPlaceholderText("text");
			flatTextField1.setColumns(10);
			placeholderPanel.add(flatTextField1, "cell 1 0");

			//---- label8 ----
			label8.setText("left");
			placeholderPanel.add(label8, "cell 0 1");

			//---- flatTextField2 ----
			flatTextField2.setHorizontalAlignment(SwingConstants.LEFT);
			flatTextField2.setPlaceholderText("text");
			flatTextField2.setColumns(10);
			placeholderPanel.add(flatTextField2, "cell 1 1");

			//---- label9 ----
			label9.setText("center");
			placeholderPanel.add(label9, "cell 0 2");

			//---- flatTextField3 ----
			flatTextField3.setHorizontalAlignment(SwingConstants.CENTER);
			flatTextField3.setPlaceholderText("text");
			flatTextField3.setColumns(10);
			placeholderPanel.add(flatTextField3, "cell 1 2");

			//---- label10 ----
			label10.setText("right");
			placeholderPanel.add(label10, "cell 0 3");

			//---- flatTextField4 ----
			flatTextField4.setHorizontalAlignment(SwingConstants.RIGHT);
			flatTextField4.setPlaceholderText("text");
			flatTextField4.setColumns(10);
			placeholderPanel.add(flatTextField4, "cell 1 3");

			//---- label11 ----
			label11.setText("trailing");
			placeholderPanel.add(label11, "cell 0 4");

			//---- flatTextField5 ----
			flatTextField5.setHorizontalAlignment(SwingConstants.TRAILING);
			flatTextField5.setPlaceholderText("text");
			flatTextField5.setColumns(10);
			placeholderPanel.add(flatTextField5, "cell 1 4");
		}
		add(placeholderPanel, "cell 5 0 1 10,aligny top,growy 0");

		//---- passwordFieldLabel ----
		passwordFieldLabel.setText("JPasswordField:");
		passwordFieldLabel.setDisplayedMnemonic('P');
		passwordFieldLabel.setLabelFor(passwordField1);
		add(passwordFieldLabel, "cell 0 2");

		//---- passwordField1 ----
		passwordField1.setText("editable");
		passwordField1.setComponentPopupMenu(popupMenu1);
		passwordField1.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "place");
		add(passwordField1, "cell 1 2,growx");

		//---- passwordField3 ----
		passwordField3.setText("longer text for testing horizontal scrolling");
		passwordField3.setComponentPopupMenu(popupMenu1);
		passwordField3.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "place");
		add(passwordField3, "cell 2 2,growx");

		//---- textAreaLabel ----
		textAreaLabel.setText("JTextArea:");
		textAreaLabel.setDisplayedMnemonic('A');
		textAreaLabel.setLabelFor(textArea1);
		add(textAreaLabel, "cell 0 3");

		//======== scrollPane1 ========
		{

			//---- textArea1 ----
			textArea1.setText("editable");
			textArea1.setComponentPopupMenu(popupMenu1);
			scrollPane1.setViewportView(textArea1);
		}
		add(scrollPane1, "cell 1 3,growx");

		//======== scrollPane3 ========
		{

			//---- textArea3 ----
			textArea3.setText("longer text for testing horizontal scrolling");
			textArea3.setComponentPopupMenu(popupMenu1);
			scrollPane3.setViewportView(textArea3);
		}
		add(scrollPane3, "cell 2 3,growx");

		//---- editorPaneLabel ----
		editorPaneLabel.setText("JEditorPane");
		editorPaneLabel.setDisplayedMnemonic('J');
		editorPaneLabel.setLabelFor(editorPane1);
		add(editorPaneLabel, "cell 0 4");

		//======== scrollPane5 ========
		{

			//---- editorPane1 ----
			editorPane1.setText("editable");
			editorPane1.setComponentPopupMenu(popupMenu1);
			scrollPane5.setViewportView(editorPane1);
		}
		add(scrollPane5, "cell 1 4,growx");

		//======== scrollPane7 ========
		{

			//---- editorPane3 ----
			editorPane3.setText("longer text for testing horizontal scrolling");
			editorPane3.setComponentPopupMenu(popupMenu1);
			scrollPane7.setViewportView(editorPane3);
		}
		add(scrollPane7, "cell 2 4,growx");

		//---- textPaneLabel ----
		textPaneLabel.setText("JTextPane:");
		textPaneLabel.setDisplayedMnemonic('N');
		textPaneLabel.setLabelFor(textPane1);
		add(textPaneLabel, "cell 0 5");

		//======== scrollPane9 ========
		{

			//---- textPane1 ----
			textPane1.setText("editable");
			textPane1.setComponentPopupMenu(popupMenu1);
			scrollPane9.setViewportView(textPane1);
		}
		add(scrollPane9, "cell 1 5,growx");

		//======== scrollPane11 ========
		{

			//---- textPane3 ----
			textPane3.setText("longer text for testing horizontal scrolling");
			textPane3.setComponentPopupMenu(popupMenu1);
			scrollPane11.setViewportView(textPane3);
		}
		add(scrollPane11, "cell 2 5,growx");

		//---- comboBoxLabel ----
		comboBoxLabel.setText("JComboBox:");
		comboBoxLabel.setDisplayedMnemonic('C');
		comboBoxLabel.setLabelFor(comboBox1);
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
		add(comboBox3, "cell 2 6,growx,wmin 50");

		//---- spinnerLabel ----
		spinnerLabel.setText("JSpinner:");
		spinnerLabel.setDisplayedMnemonic('S');
		spinnerLabel.setLabelFor(spinner1);
		add(spinnerLabel, "cell 0 7");

		//---- spinner1 ----
		spinner1.setComponentPopupMenu(popupMenu1);
		add(spinner1, "cell 1 7,growx");

		//---- spinner6 ----
		spinner6.setBorder(BorderFactory.createEmptyBorder());
		add(spinner6, "cell 2 7,growx");

		//---- label2 ----
		label2.setText("<html>Large row height:<br>(default pref height)</html>");
		add(label2, "cell 0 8,aligny top,growy 0");

		//---- comboBox2 ----
		comboBox2.setEditable(true);
		add(comboBox2, "cell 1 8,grow");
		add(spinner2, "cell 1 9,grow");

		//---- label1 ----
		label1.setText("Large pref height:");
		add(label1, "cell 0 10,aligny top,growy 0");

		//---- comboBox5 ----
		comboBox5.setPreferredSize(new Dimension(60, 40));
		comboBox5.setEditable(true);
		add(comboBox5, "cell 1 10,growx");

		//---- spinner4 ----
		spinner4.setPreferredSize(new Dimension(60, 40));
		add(spinner4, "cell 1 11,growx");

		//---- label3 ----
		label3.setText("<html>Small row height:<br>(default pref height)</html>");
		add(label3, "cell 0 12 1 2,aligny top,growy 0");

		//---- comboBox4 ----
		comboBox4.setEditable(true);
		add(comboBox4, "cell 1 12,growx");
		add(spinner3, "cell 1 13,growx");

		//---- label4 ----
		label4.setText("Small pref height:");
		add(label4, "cell 0 14 1 2,aligny top,growy 0");

		//---- comboBox6 ----
		comboBox6.setEditable(true);
		comboBox6.setPreferredSize(new Dimension(60, 14));
		comboBox6.setMinimumSize(new Dimension(60, 14));
		add(comboBox6, "cell 1 14,growx");

		//---- spinner5 ----
		spinner5.setMinimumSize(new Dimension(60, 14));
		spinner5.setPreferredSize(new Dimension(60, 14));
		add(spinner5, "cell 1 15,growx,hmax 14");

		//---- label5 ----
		label5.setText("Double-click-and-drag:");
		add(label5, "cell 0 16");

		//---- textField ----
		textField.setText("123 456 789 abc def");
		add(textField, "cell 1 16 2 1,growx");

		//---- dragEnabledCheckBox ----
		dragEnabledCheckBox.setText("Drag enabled");
		dragEnabledCheckBox.addActionListener(e -> dragEnabledChanged());
		add(dragEnabledCheckBox, "cell 3 16 2 1,alignx left,growx 0");

		//---- label6 ----
		label6.setText("<html>JTextArea<br>JTextPane<br>JEditorPane</html>");
		add(label6, "cell 0 17,align right top,grow 0 0");

		//======== scrollPane2 ========
		{

			//---- textArea ----
			textArea.setText("1 123 456 789 abc def\n2 123 456 789 abc def\n3 123 456 789 abc def\n4 123 456 789 abc def\n5 123 456 789 abc def\n6 123 456 789 abc def\n7 123 456 789 abc def\n8 123 456 789 abc def");
			scrollPane2.setViewportView(textArea);
		}
		add(scrollPane2, "cell 1 17 4 1,growx");

		//======== scrollPane4 ========
		{

			//---- textPane ----
			textPane.setText("1 123 456 789 abc def\n2 123 456 789 abc def\n3 123 456 789 abc def\n4 123 456 789 abc def\n5 123 456 789 abc def\n6 123 456 789 abc def\n7 123 456 789 abc def\n8 123 456 789 abc def");
			scrollPane4.setViewportView(textPane);
		}
		add(scrollPane4, "cell 1 17 4 1,growx");

		//======== scrollPane6 ========
		{

			//---- editorPane ----
			editorPane.setText("1 123 456 789 abc def\n2 123 456 789 abc def\n3 123 456 789 abc def\n4 123 456 789 abc def\n5 123 456 789 abc def\n6 123 456 789 abc def\n7 123 456 789 abc def\n8 123 456 789 abc def");
			scrollPane6.setViewportView(editorPane);
		}
		add(scrollPane6, "cell 1 17 4 1,growx");

		//======== popupMenu1 ========
		{

			//---- cutMenuItem ----
			cutMenuItem.setText("Cut");
			popupMenu1.add(cutMenuItem);

			//---- copyMenuItem ----
			copyMenuItem.setText("Copy");
			popupMenu1.add(copyMenuItem);

			//---- pasteMenuItem ----
			pasteMenuItem.setText("Paste");
			popupMenu1.add(pasteMenuItem);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		cutMenuItem.addActionListener( new DefaultEditorKit.CutAction() );
		copyMenuItem.addActionListener( new DefaultEditorKit.CopyAction() );
		pasteMenuItem.addActionListener( new DefaultEditorKit.PasteAction() );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTextField textField1;
	private JPanel controlPanel;
	private JCheckBox editableCheckBox;
	private JSpinner leftPaddingField;
	private JSpinner rightPaddingField;
	private JSpinner topPaddingField;
	private JSpinner bottomPaddingField;
	private JCheckBox leadingIconCheckBox;
	private JCheckBox trailingIconCheckBox;
	private JCheckBox leadingComponentCheckBox;
	private JCheckBox trailingComponentCheckBox;
	private JCheckBox leadingComponentVisibleCheckBox;
	private JCheckBox trailingComponentVisibleCheckBox;
	private JCheckBox showClearButtonCheckBox;
	private JCheckBox showRevealButtonCheckBox;
	private JPanel placeholderPanel;
	private JComboBox<String> comboBox5;
	private JSpinner spinner4;
	private JComboBox<String> comboBox6;
	private JSpinner spinner5;
	private JTextField textField;
	private JCheckBox dragEnabledCheckBox;
	private JTextArea textArea;
	private JTextPane textPane;
	private JEditorPane editorPane;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- NotFocusableTraversalPolicy ----------------------------------------

	private static class NotFocusableTraversalPolicy
		extends FocusTraversalPolicy
	{
		@Override public Component getComponentAfter( Container aContainer, Component aComponent ) { return null; }
		@Override public Component getComponentBefore( Container aContainer, Component aComponent ) { return null; }
		@Override public Component getFirstComponent( Container aContainer ) { return null; }
		@Override public Component getLastComponent( Container aContainer ) { return null; }
		@Override public Component getDefaultComponent( Container aContainer ) { return null; }
	}

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
