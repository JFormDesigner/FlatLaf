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

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatBaselineTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatBaselineTest" );
			frame.showFrame( FlatBaselineTest::new );
		} );
	}

	public FlatBaselineTest() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label1 = new JLabel();
		JButton button1 = new JButton();
		JToggleButton toggleButton1 = new JToggleButton();
		JCheckBox checkBox1 = new JCheckBox();
		JRadioButton radioButton1 = new JRadioButton();
		JTextField textField4 = new JTextField();
		JLabel label2 = new JLabel();
		JTextField textField1 = new JTextField();
		JFormattedTextField formattedTextField1 = new JFormattedTextField();
		JPasswordField passwordField1 = new JPasswordField();
		JComboBox<String> comboBox1 = new JComboBox<>();
		JComboBox<String> comboBox2 = new JComboBox<>();
		JSpinner spinner1 = new JSpinner();
		JLabel label6 = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		JTextArea textArea1 = new JTextArea();
		JTextArea textArea2 = new JTextArea();
		JTextField textField2 = new JTextField();
		JLabel label7 = new JLabel();
		JScrollPane scrollPane3 = new JScrollPane();
		JList<String> list1 = new JList<>();
		JScrollPane scrollPane4 = new JScrollPane();
		JTree tree1 = new JTree();
		JScrollPane scrollPane5 = new JScrollPane();
		JTable table1 = new JTable();
		JTextField textField3 = new JTextField();
		JLabel label3 = new JLabel();
		JSlider slider1 = new JSlider();
		JSlider slider6 = new JSlider();
		JLabel label8 = new JLabel();
		JSlider slider7 = new JSlider();
		JSlider slider8 = new JSlider();
		JLabel label4 = new JLabel();
		JProgressBar progressBar1 = new JProgressBar();
		JProgressBar progressBar3 = new JProgressBar();
		JSeparator separator1 = new JSeparator();
		JLabel label5 = new JLabel();
		JSlider slider2 = new JSlider();
		JSlider slider3 = new JSlider();
		JProgressBar progressBar2 = new JProgressBar();
		JProgressBar progressBar4 = new JProgressBar();
		JPanel hSpacer1 = new JPanel(null);
		JLabel label9 = new JLabel();
		JSlider slider4 = new JSlider();
		JSlider slider5 = new JSlider();
		JPanel hSpacer2 = new JPanel(null);

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]",
			// rows
			"[]" +
			"[]" +
			"[50]" +
			"[::80]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]"));

		//---- label1 ----
		label1.setText("Dext");
		add(label1, "cell 0 0");

		//---- button1 ----
		button1.setText("Dext");
		add(button1, "cell 1 0");

		//---- toggleButton1 ----
		toggleButton1.setText("Dext");
		add(toggleButton1, "cell 2 0");

		//---- checkBox1 ----
		checkBox1.setText("Dext");
		add(checkBox1, "cell 3 0");

		//---- radioButton1 ----
		radioButton1.setText("Dext");
		add(radioButton1, "cell 4 0");

		//---- textField4 ----
		textField4.setText("Dext field");
		add(textField4, "cell 7 0");

		//---- label2 ----
		label2.setText("Dext");
		add(label2, "cell 0 1");

		//---- textField1 ----
		textField1.setText("Dext");
		add(textField1, "cell 1 1");

		//---- formattedTextField1 ----
		formattedTextField1.setText("Dext");
		add(formattedTextField1, "cell 2 1");

		//---- passwordField1 ----
		passwordField1.setText("Dext");
		add(passwordField1, "cell 3 1");

		//---- comboBox1 ----
		comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
			"Dext"
		}));
		add(comboBox1, "cell 4 1");

		//---- comboBox2 ----
		comboBox2.setModel(new DefaultComboBoxModel<>(new String[] {
			"Dext"
		}));
		comboBox2.setEditable(true);
		add(comboBox2, "cell 5 1");
		add(spinner1, "cell 6 1");

		//---- label6 ----
		label6.setText("Dext");
		add(label6, "cell 0 2");

		//======== scrollPane1 ========
		{

			//---- textArea1 ----
			textArea1.setText("Dext");
			textArea1.setRows(4);
			scrollPane1.setViewportView(textArea1);
		}
		add(scrollPane1, "cell 1 2");

		//---- textArea2 ----
		textArea2.setText("Dext");
		textArea2.setRows(4);
		add(textArea2, "cell 2 2");

		//---- textField2 ----
		textField2.setText("Dext field");
		add(textField2, "cell 7 2");

		//---- label7 ----
		label7.setText("Dext");
		add(label7, "cell 0 3");

		//======== scrollPane3 ========
		{

			//---- list1 ----
			list1.setModel(new AbstractListModel<String>() {
				String[] values = {
					"Dext",
					"Dext",
					"Dext"
				};
				@Override
				public int getSize() { return values.length; }
				@Override
				public String getElementAt(int i) { return values[i]; }
			});
			scrollPane3.setViewportView(list1);
		}
		add(scrollPane3, "cell 1 3");

		//======== scrollPane4 ========
		{

			//---- tree1 ----
			tree1.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("Dext") {
					{
						add(new DefaultMutableTreeNode("Dext"));
						add(new DefaultMutableTreeNode("Dext"));
					}
				}));
			scrollPane4.setViewportView(tree1);
		}
		add(scrollPane4, "cell 2 3");

		//======== scrollPane5 ========
		{

			//---- table1 ----
			table1.setModel(new DefaultTableModel(
				new Object[][] {
					{"Dext", "0"},
					{"Dext ", "1"},
				},
				new String[] {
					"Dext", null
				}
			));
			scrollPane5.setViewportView(table1);
		}
		add(scrollPane5, "cell 3 3,wmax 100");

		//---- textField3 ----
		textField3.setText("Dext field");
		add(textField3, "cell 7 3");

		//---- label3 ----
		label3.setText("Dext");
		add(label3, "cell 0 4");
		add(slider1, "cell 1 4 7 1");

		//---- slider6 ----
		slider6.setPaintTicks(true);
		slider6.setMajorTickSpacing(25);
		slider6.setMinorTickSpacing(5);
		add(slider6, "cell 1 4 7 1");

		//---- label8 ----
		label8.setText("Dext");
		add(label8, "cell 0 5");

		//---- slider7 ----
		slider7.setPaintLabels(true);
		slider7.setMajorTickSpacing(25);
		slider7.setMinorTickSpacing(5);
		add(slider7, "cell 1 5 7 1");

		//---- slider8 ----
		slider8.setPaintLabels(true);
		slider8.setPaintTicks(true);
		slider8.setMajorTickSpacing(25);
		slider8.setMinorTickSpacing(5);
		add(slider8, "cell 1 5 7 1");

		//---- label4 ----
		label4.setText("Dext");
		add(label4, "cell 0 6");

		//---- progressBar1 ----
		progressBar1.setValue(30);
		add(progressBar1, "cell 1 6 7 1");

		//---- progressBar3 ----
		progressBar3.setStringPainted(true);
		progressBar3.setValue(30);
		add(progressBar3, "cell 1 6 7 1");
		add(separator1, "cell 1 6 7 1");

		//---- label5 ----
		label5.setText("Dext");
		add(label5, "cell 0 7");

		//---- slider2 ----
		slider2.setOrientation(SwingConstants.VERTICAL);
		add(slider2, "cell 1 7 7 1");

		//---- slider3 ----
		slider3.setOrientation(SwingConstants.VERTICAL);
		slider3.setPaintTicks(true);
		slider3.setMajorTickSpacing(25);
		slider3.setMinorTickSpacing(5);
		add(slider3, "cell 1 7 7 1");

		//---- progressBar2 ----
		progressBar2.setOrientation(SwingConstants.VERTICAL);
		progressBar2.setValue(30);
		add(progressBar2, "cell 1 7 7 1");

		//---- progressBar4 ----
		progressBar4.setOrientation(SwingConstants.VERTICAL);
		progressBar4.setStringPainted(true);
		progressBar4.setValue(30);
		add(progressBar4, "cell 1 7 7 1");
		add(hSpacer1, "cell 1 7 7 1,growx");

		//---- label9 ----
		label9.setText("Dext");
		add(label9, "cell 0 8");

		//---- slider4 ----
		slider4.setOrientation(SwingConstants.VERTICAL);
		slider4.setPaintLabels(true);
		slider4.setMajorTickSpacing(25);
		slider4.setMinorTickSpacing(5);
		add(slider4, "cell 1 8 7 1");

		//---- slider5 ----
		slider5.setOrientation(SwingConstants.VERTICAL);
		slider5.setPaintLabels(true);
		slider5.setPaintTicks(true);
		slider5.setMajorTickSpacing(25);
		slider5.setMinorTickSpacing(5);
		add(slider5, "cell 1 8 7 1");
		add(hSpacer2, "cell 1 8 7 1,growx");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
