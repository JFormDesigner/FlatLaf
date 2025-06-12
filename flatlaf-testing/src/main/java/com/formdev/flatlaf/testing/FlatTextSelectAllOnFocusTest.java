/*
 * Copyright 2025 FormDev Software GmbH
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

import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.EventQueue;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatTextSelectAllOnFocusTest
	extends FlatSingleComponentTest
{
	public static void main( String[] args ) {
		EventQueue.invokeLater( () -> {
			launch( FlatTextSelectAllOnFocusTest::new, args );
		} );
	}

	@Override
	protected JComponent createSingleComponent() {
		initComponents();

		try {
			MaskFormatter formatter = new MaskFormatter( "###-####-###" );
			formatter.setPlaceholderCharacter( '_' );
			DefaultFormatterFactory factory = new DefaultFormatterFactory( formatter );

			formattedTextField1.setFormatterFactory( factory );
			formattedTextField2.setFormatterFactory( factory );
			formattedTextField3.setFormatterFactory( factory );
			formattedTextField4.setFormatterFactory( factory );
			formattedTextField5.setFormatterFactory( factory );
			formattedTextField6.setFormatterFactory( factory );

			formattedTextField1.setValue( "123-4567-890" );
			formattedTextField2.setValue( "123-4567-890" );
			formattedTextField3.setValue( "123-4567-890" );
			formattedTextField4.setValue( "123-4567-890" );
			formattedTextField5.setValue( "123-4567-890" );
			formattedTextField6.setValue( "123-4567-890" );
		} catch( ParseException ex ) {
			ex.printStackTrace();
		}

		textField2.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_NEVER );
		textField3.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
		textField4.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
		textField5.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
		textField5.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );
		textField6.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
		textField6.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );
		textField7.select( 5, 7 );

		formattedTextField2.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_NEVER );
		formattedTextField3.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
		formattedTextField4.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
		formattedTextField5.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
		formattedTextField5.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );
		formattedTextField6.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
		formattedTextField6.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );

		comboBox2.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_NEVER );
		comboBox3.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
		comboBox4.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
		comboBox5.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
		comboBox5.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );
		comboBox6.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
		comboBox6.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );
		((JTextField)comboBox7.getEditor().getEditorComponent()).select( 5, 7 );

		spinner2.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_NEVER );
		spinner3.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
		spinner4.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
		spinner5.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
		spinner5.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );
		spinner6.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
		spinner6.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );

//		textArea1.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
//		textPane1.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
//		editorPane1.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ALWAYS );
//
//		textArea1.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
//		textPane1.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
//		editorPane1.putClientProperty( SELECT_ALL_ON_FOCUS_POLICY, SELECT_ALL_ON_FOCUS_POLICY_ONCE );
//
//		textArea1.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );
//		textPane1.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );
//		editorPane1.putClientProperty( SELECT_ALL_ON_MOUSE_CLICK, true );
//
//		textArea1.select( 5, 7 );
//		textPane1.select( 5, 7 );
//		editorPane1.select( 5, 7 );

		return panel;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel = new FlatTestPanel();
		label1 = new JLabel();
		label2 = new JLabel();
		label3 = new JLabel();
		label4 = new JLabel();
		label5 = new JLabel();
		label6 = new JLabel();
		label7 = new JLabel();
		textFieldLabel = new JLabel();
		textField1 = new JTextField();
		textField2 = new JTextField();
		textField3 = new JTextField();
		textField4 = new JTextField();
		textField5 = new JTextField();
		textField6 = new JTextField();
		textField7 = new JTextField();
		formattedTextFieldLabel = new JLabel();
		formattedTextField1 = new JFormattedTextField();
		formattedTextField2 = new JFormattedTextField();
		formattedTextField3 = new JFormattedTextField();
		formattedTextField4 = new JFormattedTextField();
		formattedTextField5 = new JFormattedTextField();
		formattedTextField6 = new JFormattedTextField();
		comboBoxLabel = new JLabel();
		comboBox1 = new JComboBox<>();
		comboBox2 = new JComboBox<>();
		comboBox3 = new JComboBox<>();
		comboBox4 = new JComboBox<>();
		comboBox5 = new JComboBox<>();
		comboBox6 = new JComboBox<>();
		comboBox7 = new JComboBox<>();
		spinnerLabel = new JLabel();
		spinner1 = new JSpinner();
		spinner2 = new JSpinner();
		spinner3 = new JSpinner();
		spinner4 = new JSpinner();
		spinner5 = new JSpinner();
		spinner6 = new JSpinner();
		scrollPane1 = new JScrollPane();
		textArea1 = new JTextArea();
		scrollPane2 = new JScrollPane();
		textPane1 = new JTextPane();
		scrollPane3 = new JScrollPane();
		editorPane1 = new JEditorPane();

		//======== panel ========
		{
			panel.setLayout(new MigLayout(
				"ltr,insets dialog,hidemode 3",
				// columns
				"[]" +
				"[100,sizegroup 1,fill]" +
				"[sizegroup 1,fill]" +
				"[sizegroup 1,fill]" +
				"[sizegroup 1,fill]" +
				"[sizegroup 1,fill]" +
				"[fill]" +
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[fill]" +
				"[30]"));

			//---- label1 ----
			label1.setText("default");
			panel.add(label1, "cell 1 0");

			//---- label2 ----
			label2.setText("never");
			panel.add(label2, "cell 2 0");

			//---- label3 ----
			label3.setText("once");
			panel.add(label3, "cell 3 0");

			//---- label4 ----
			label4.setText("always");
			panel.add(label4, "cell 4 0");

			//---- label5 ----
			label5.setText("once on click");
			panel.add(label5, "cell 5 0");

			//---- label6 ----
			label6.setText("always on click");
			panel.add(label6, "cell 6 0");

			//---- label7 ----
			label7.setText("custom selection");
			panel.add(label7, "cell 7 0");

			//---- textFieldLabel ----
			textFieldLabel.setText("JTextField:");
			textFieldLabel.setDisplayedMnemonic('T');
			textFieldLabel.setLabelFor(textField1);
			panel.add(textFieldLabel, "cell 0 1");

			//---- textField1 ----
			textField1.setText("1234567890");
			panel.add(textField1, "cell 1 1");

			//---- textField2 ----
			textField2.setText("1234567890");
			panel.add(textField2, "cell 2 1");

			//---- textField3 ----
			textField3.setText("1234567890");
			panel.add(textField3, "cell 3 1");

			//---- textField4 ----
			textField4.setText("1234567890");
			panel.add(textField4, "cell 4 1");

			//---- textField5 ----
			textField5.setText("1234567890");
			panel.add(textField5, "cell 5 1");

			//---- textField6 ----
			textField6.setText("1234567890");
			panel.add(textField6, "cell 6 1");

			//---- textField7 ----
			textField7.setText("1234567890");
			panel.add(textField7, "cell 7 1");

			//---- formattedTextFieldLabel ----
			formattedTextFieldLabel.setText("JFormattedTextField:");
			formattedTextFieldLabel.setDisplayedMnemonic('F');
			formattedTextFieldLabel.setLabelFor(formattedTextField1);
			panel.add(formattedTextFieldLabel, "cell 0 2");
			panel.add(formattedTextField1, "cell 1 2");
			panel.add(formattedTextField2, "cell 2 2");
			panel.add(formattedTextField3, "cell 3 2");
			panel.add(formattedTextField4, "cell 4 2");
			panel.add(formattedTextField5, "cell 5 2");
			panel.add(formattedTextField6, "cell 6 2");

			//---- comboBoxLabel ----
			comboBoxLabel.setText("JComboBox:");
			comboBoxLabel.setDisplayedMnemonic('C');
			comboBoxLabel.setLabelFor(comboBox1);
			panel.add(comboBoxLabel, "cell 0 3");

			//---- comboBox1 ----
			comboBox1.setEditable(true);
			comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
				"1234567890"
			}));
			panel.add(comboBox1, "cell 1 3");

			//---- comboBox2 ----
			comboBox2.setEditable(true);
			comboBox2.setModel(new DefaultComboBoxModel<>(new String[] {
				"1234567890"
			}));
			panel.add(comboBox2, "cell 2 3");

			//---- comboBox3 ----
			comboBox3.setEditable(true);
			comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {
				"1234567890"
			}));
			panel.add(comboBox3, "cell 3 3");

			//---- comboBox4 ----
			comboBox4.setEditable(true);
			comboBox4.setModel(new DefaultComboBoxModel<>(new String[] {
				"1234567890"
			}));
			panel.add(comboBox4, "cell 4 3");

			//---- comboBox5 ----
			comboBox5.setEditable(true);
			comboBox5.setModel(new DefaultComboBoxModel<>(new String[] {
				"1234567890"
			}));
			panel.add(comboBox5, "cell 5 3");

			//---- comboBox6 ----
			comboBox6.setEditable(true);
			comboBox6.setModel(new DefaultComboBoxModel<>(new String[] {
				"1234567890"
			}));
			panel.add(comboBox6, "cell 6 3");

			//---- comboBox7 ----
			comboBox7.setEditable(true);
			comboBox7.setModel(new DefaultComboBoxModel<>(new String[] {
				"1234567890"
			}));
			panel.add(comboBox7, "cell 7 3");

			//---- spinnerLabel ----
			spinnerLabel.setText("JSpinner:");
			spinnerLabel.setDisplayedMnemonic('S');
			spinnerLabel.setLabelFor(spinner1);
			panel.add(spinnerLabel, "cell 0 4");

			//---- spinner1 ----
			spinner1.setModel(new SpinnerNumberModel(1234, null, null, 100));
			panel.add(spinner1, "cell 1 4");

			//---- spinner2 ----
			spinner2.setModel(new SpinnerNumberModel(1234, null, null, 100));
			panel.add(spinner2, "cell 2 4");

			//---- spinner3 ----
			spinner3.setModel(new SpinnerNumberModel(1234, null, null, 100));
			panel.add(spinner3, "cell 3 4");

			//---- spinner4 ----
			spinner4.setModel(new SpinnerNumberModel(1234, null, null, 100));
			panel.add(spinner4, "cell 4 4");

			//---- spinner5 ----
			spinner5.setModel(new SpinnerNumberModel(1234, null, null, 100));
			panel.add(spinner5, "cell 5 4");

			//---- spinner6 ----
			spinner6.setModel(new SpinnerNumberModel(1234, null, null, 100));
			panel.add(spinner6, "cell 6 4");

			//======== scrollPane1 ========
			{

				//---- textArea1 ----
				textArea1.setRows(3);
				textArea1.setText("1234567890\nabc");
				scrollPane1.setViewportView(textArea1);
			}
			panel.add(scrollPane1, "cell 1 5");

			//======== scrollPane2 ========
			{

				//---- textPane1 ----
				textPane1.setText("1234567890\nabc");
				scrollPane2.setViewportView(textPane1);
			}
			panel.add(scrollPane2, "cell 2 5");

			//======== scrollPane3 ========
			{

				//---- editorPane1 ----
				editorPane1.setText("1234567890\nabc");
				scrollPane3.setViewportView(editorPane1);
			}
			panel.add(scrollPane3, "cell 3 5 2 1");
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private FlatTestPanel panel;
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel label4;
	private JLabel label5;
	private JLabel label6;
	private JLabel label7;
	private JLabel textFieldLabel;
	private JTextField textField1;
	private JTextField textField2;
	private JTextField textField3;
	private JTextField textField4;
	private JTextField textField5;
	private JTextField textField6;
	private JTextField textField7;
	private JLabel formattedTextFieldLabel;
	private JFormattedTextField formattedTextField1;
	private JFormattedTextField formattedTextField2;
	private JFormattedTextField formattedTextField3;
	private JFormattedTextField formattedTextField4;
	private JFormattedTextField formattedTextField5;
	private JFormattedTextField formattedTextField6;
	private JLabel comboBoxLabel;
	private JComboBox<String> comboBox1;
	private JComboBox<String> comboBox2;
	private JComboBox<String> comboBox3;
	private JComboBox<String> comboBox4;
	private JComboBox<String> comboBox5;
	private JComboBox<String> comboBox6;
	private JComboBox<String> comboBox7;
	private JLabel spinnerLabel;
	private JSpinner spinner1;
	private JSpinner spinner2;
	private JSpinner spinner3;
	private JSpinner spinner4;
	private JSpinner spinner5;
	private JSpinner spinner6;
	private JScrollPane scrollPane1;
	private JTextArea textArea1;
	private JScrollPane scrollPane2;
	private JTextPane textPane1;
	private JScrollPane scrollPane3;
	private JEditorPane editorPane1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
