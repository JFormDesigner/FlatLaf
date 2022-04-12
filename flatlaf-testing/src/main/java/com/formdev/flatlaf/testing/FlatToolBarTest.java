/*
 * Copyright 2021 FormDev Software GmbH
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
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatToolBarTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatToolBarTest" );
			frame.showFrame( FlatToolBarTest::new );
		} );
	}

	FlatToolBarTest() {
		initComponents();

		String notFocusableStyle = "background: darken($Panel.background,3%); focusableButtons: false";
		String focusableStyle    = "background: darken($Panel.background,3%); focusableButtons: true; arrowKeysOnlyNavigation:true";

		toolBar1.putClientProperty( FlatClientProperties.STYLE, notFocusableStyle );
		toolBar2.putClientProperty( FlatClientProperties.STYLE, notFocusableStyle );
		toolBar3.putClientProperty( FlatClientProperties.STYLE, focusableStyle );
		toolBar4.putClientProperty( FlatClientProperties.STYLE, focusableStyle );
		toolBar5.putClientProperty( FlatClientProperties.STYLE, notFocusableStyle );
		toolBar6.putClientProperty( FlatClientProperties.STYLE, notFocusableStyle );
		toolBar7.putClientProperty( FlatClientProperties.STYLE, focusableStyle );
		toolBar8.putClientProperty( FlatClientProperties.STYLE, focusableStyle );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label9 = new JLabel();
		JLabel label7 = new JLabel();
		JTextField textField1 = new JTextField();
		JLabel label1 = new JLabel();
		toolBar1 = new JToolBar();
		JButton button1 = new JButton();
		JButton button2 = new JButton();
		JLabel label11 = new JLabel();
		JButton button3 = new JButton();
		JButton button4 = new JButton();
		JLabel label3 = new JLabel();
		toolBar2 = new JToolBar();
		JButton button5 = new JButton();
		JButton button6 = new JButton();
		JButton button7 = new JButton();
		JButton button8 = new JButton();
		JLabel label2 = new JLabel();
		toolBar3 = new JToolBar();
		JButton button9 = new JButton();
		JButton button10 = new JButton();
		JLabel label10 = new JLabel();
		JButton button11 = new JButton();
		JButton button12 = new JButton();
		JLabel label4 = new JLabel();
		toolBar4 = new JToolBar();
		JButton button13 = new JButton();
		JButton button14 = new JButton();
		JButton button15 = new JButton();
		JButton button16 = new JButton();
		JLabel label8 = new JLabel();
		JTextField textField2 = new JTextField();
		JLabel label5 = new JLabel();
		toolBar5 = new JToolBar();
		JButton button17 = new JButton();
		JButton button18 = new JButton();
		JButton button19 = new JButton();
		JComboBox<String> comboBox1 = new JComboBox<>();
		JButton button20 = new JButton();
		JButton button33 = new JButton();
		JButton button34 = new JButton();
		JComboBox<String> comboBox8 = new JComboBox<>();
		JButton button41 = new JButton();
		JButton button42 = new JButton();
		JButton button43 = new JButton();
		toolBar6 = new JToolBar();
		JButton button21 = new JButton();
		JButton button22 = new JButton();
		JButton button23 = new JButton();
		JComboBox<String> comboBox2 = new JComboBox<>();
		JComboBox<String> comboBox3 = new JComboBox<>();
		JComboBox<String> comboBox7 = new JComboBox<>();
		JButton button24 = new JButton();
		JComboBox<String> comboBox10 = new JComboBox<>();
		JTextField textField3 = new JTextField();
		JButton button37 = new JButton();
		JButton button38 = new JButton();
		JButton button44 = new JButton();
		JButton button45 = new JButton();
		JButton button46 = new JButton();
		JLabel label6 = new JLabel();
		toolBar7 = new JToolBar();
		JButton button25 = new JButton();
		JButton button26 = new JButton();
		JButton button27 = new JButton();
		JComboBox<String> comboBox4 = new JComboBox<>();
		JButton button28 = new JButton();
		JButton button35 = new JButton();
		JButton button36 = new JButton();
		JComboBox<String> comboBox9 = new JComboBox<>();
		JButton button47 = new JButton();
		JButton button48 = new JButton();
		JButton button49 = new JButton();
		toolBar8 = new JToolBar();
		JButton button29 = new JButton();
		JButton button30 = new JButton();
		JButton button31 = new JButton();
		JComboBox<String> comboBox5 = new JComboBox<>();
		JComboBox<String> comboBox6 = new JComboBox<>();
		JComboBox<String> comboBox13 = new JComboBox<>();
		JButton button32 = new JButton();
		JButton button39 = new JButton();
		JButton button40 = new JButton();
		JComboBox<String> comboBox12 = new JComboBox<>();
		JTextField textField4 = new JTextField();
		JButton button50 = new JButton();
		JButton button51 = new JButton();
		JButton button52 = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[fill]para" +
			"[fill]" +
			"[fill]",
			// rows
			"[]para" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]"));

		//---- label9 ----
		label9.setText("Use to test focus traversal. Tab key skips buttons within one toolbar. Arrow keys focus next/previous button.");
		add(label9, "cell 0 0 4 1");

		//---- label7 ----
		label7.setText("Text field:");
		add(label7, "cell 0 1");
		add(textField1, "cell 1 1");

		//---- label1 ----
		label1.setText("Only buttons / not focusable:");
		add(label1, "cell 0 2");

		//======== toolBar1 ========
		{

			//---- button1 ----
			button1.setText("A");
			toolBar1.add(button1);

			//---- button2 ----
			button2.setText("B");
			toolBar1.add(button2);

			//---- label11 ----
			label11.setText("label");
			toolBar1.add(label11);

			//---- button3 ----
			button3.setText("C");
			toolBar1.add(button3);

			//---- button4 ----
			button4.setText("D");
			toolBar1.add(button4);
		}
		add(toolBar1, "cell 1 2");

		//---- label3 ----
		label3.setText("2nd:");
		add(label3, "cell 2 2");

		//======== toolBar2 ========
		{

			//---- button5 ----
			button5.setText("A");
			toolBar2.add(button5);

			//---- button6 ----
			button6.setText("B");
			toolBar2.add(button6);
			toolBar2.addSeparator();

			//---- button7 ----
			button7.setText("C");
			toolBar2.add(button7);

			//---- button8 ----
			button8.setText("D");
			toolBar2.add(button8);
		}
		add(toolBar2, "cell 3 2,alignx left,growx 0");

		//---- label2 ----
		label2.setText("Only buttons / focusable:");
		add(label2, "cell 0 3");

		//======== toolBar3 ========
		{

			//---- button9 ----
			button9.setText("A");
			toolBar3.add(button9);

			//---- button10 ----
			button10.setText("B");
			toolBar3.add(button10);

			//---- label10 ----
			label10.setText("label");
			toolBar3.add(label10);

			//---- button11 ----
			button11.setText("C");
			toolBar3.add(button11);

			//---- button12 ----
			button12.setText("D");
			toolBar3.add(button12);
		}
		add(toolBar3, "cell 1 3");

		//---- label4 ----
		label4.setText("2nd:");
		add(label4, "cell 2 3");

		//======== toolBar4 ========
		{

			//---- button13 ----
			button13.setText("A");
			toolBar4.add(button13);

			//---- button14 ----
			button14.setText("B");
			toolBar4.add(button14);
			toolBar4.addSeparator();

			//---- button15 ----
			button15.setText("C");
			toolBar4.add(button15);

			//---- button16 ----
			button16.setText("D");
			toolBar4.add(button16);
		}
		add(toolBar4, "cell 3 3,alignx left,growx 0");

		//---- label8 ----
		label8.setText("Text field:");
		add(label8, "cell 0 4");
		add(textField2, "cell 1 4");

		//---- label5 ----
		label5.setText("Combo boxes / not focusable:");
		add(label5, "cell 0 5");

		//======== toolBar5 ========
		{

			//---- button17 ----
			button17.setText("A");
			toolBar5.add(button17);

			//---- button18 ----
			button18.setText("B");
			toolBar5.add(button18);

			//---- button19 ----
			button19.setText("C");
			toolBar5.add(button19);

			//---- comboBox1 ----
			comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
				"read-only"
			}));
			toolBar5.add(comboBox1);

			//---- button20 ----
			button20.setText("D");
			toolBar5.add(button20);

			//---- button33 ----
			button33.setText("E");
			toolBar5.add(button33);

			//---- button34 ----
			button34.setText("F");
			toolBar5.add(button34);

			//---- comboBox8 ----
			comboBox8.setEditable(true);
			comboBox8.setModel(new DefaultComboBoxModel<>(new String[] {
				"editable"
			}));
			toolBar5.add(comboBox8);

			//---- button41 ----
			button41.setText("G");
			toolBar5.add(button41);

			//---- button42 ----
			button42.setText("H");
			toolBar5.add(button42);

			//---- button43 ----
			button43.setText("I");
			toolBar5.add(button43);
		}
		add(toolBar5, "cell 1 5 3 1,alignx left,growx 0");

		//======== toolBar6 ========
		{

			//---- button21 ----
			button21.setText("A");
			toolBar6.add(button21);

			//---- button22 ----
			button22.setText("B");
			toolBar6.add(button22);

			//---- button23 ----
			button23.setText("C");
			toolBar6.add(button23);

			//---- comboBox2 ----
			comboBox2.setModel(new DefaultComboBoxModel<>(new String[] {
				"read-only"
			}));
			toolBar6.add(comboBox2);

			//---- comboBox3 ----
			comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {
				"read-only"
			}));
			toolBar6.add(comboBox3);

			//---- comboBox7 ----
			comboBox7.setModel(new DefaultComboBoxModel<>(new String[] {
				"read-only"
			}));
			toolBar6.add(comboBox7);

			//---- button24 ----
			button24.setText("D");
			toolBar6.add(button24);

			//---- comboBox10 ----
			comboBox10.setEditable(true);
			comboBox10.setModel(new DefaultComboBoxModel<>(new String[] {
				"editable"
			}));
			toolBar6.add(comboBox10);

			//---- textField3 ----
			textField3.setText("text field");
			toolBar6.add(textField3);

			//---- button37 ----
			button37.setText("E");
			toolBar6.add(button37);

			//---- button38 ----
			button38.setText("F");
			toolBar6.add(button38);

			//---- button44 ----
			button44.setText("G");
			toolBar6.add(button44);

			//---- button45 ----
			button45.setText("H");
			toolBar6.add(button45);

			//---- button46 ----
			button46.setText("I");
			toolBar6.add(button46);
		}
		add(toolBar6, "cell 1 6 3 1,alignx left,growx 0");

		//---- label6 ----
		label6.setText("Combo boxes / focusable:");
		add(label6, "cell 0 7");

		//======== toolBar7 ========
		{

			//---- button25 ----
			button25.setText("A");
			toolBar7.add(button25);

			//---- button26 ----
			button26.setText("B");
			toolBar7.add(button26);

			//---- button27 ----
			button27.setText("C");
			toolBar7.add(button27);

			//---- comboBox4 ----
			comboBox4.setModel(new DefaultComboBoxModel<>(new String[] {
				"read-only"
			}));
			toolBar7.add(comboBox4);

			//---- button28 ----
			button28.setText("D");
			toolBar7.add(button28);

			//---- button35 ----
			button35.setText("E");
			toolBar7.add(button35);

			//---- button36 ----
			button36.setText("F");
			toolBar7.add(button36);

			//---- comboBox9 ----
			comboBox9.setEditable(true);
			comboBox9.setModel(new DefaultComboBoxModel<>(new String[] {
				"editable"
			}));
			toolBar7.add(comboBox9);

			//---- button47 ----
			button47.setText("G");
			toolBar7.add(button47);

			//---- button48 ----
			button48.setText("H");
			toolBar7.add(button48);

			//---- button49 ----
			button49.setText("I");
			toolBar7.add(button49);
		}
		add(toolBar7, "cell 1 7 3 1,alignx left,growx 0");

		//======== toolBar8 ========
		{

			//---- button29 ----
			button29.setText("A");
			toolBar8.add(button29);

			//---- button30 ----
			button30.setText("B");
			toolBar8.add(button30);

			//---- button31 ----
			button31.setText("C");
			toolBar8.add(button31);

			//---- comboBox5 ----
			comboBox5.setModel(new DefaultComboBoxModel<>(new String[] {
				"read-only"
			}));
			toolBar8.add(comboBox5);

			//---- comboBox6 ----
			comboBox6.setModel(new DefaultComboBoxModel<>(new String[] {
				"read-only"
			}));
			toolBar8.add(comboBox6);

			//---- comboBox13 ----
			comboBox13.setModel(new DefaultComboBoxModel<>(new String[] {
				"read-only"
			}));
			toolBar8.add(comboBox13);

			//---- button32 ----
			button32.setText("D");
			toolBar8.add(button32);

			//---- button39 ----
			button39.setText("E");
			toolBar8.add(button39);

			//---- button40 ----
			button40.setText("F");
			toolBar8.add(button40);

			//---- comboBox12 ----
			comboBox12.setEditable(true);
			comboBox12.setModel(new DefaultComboBoxModel<>(new String[] {
				"editable"
			}));
			toolBar8.add(comboBox12);

			//---- textField4 ----
			textField4.setText("text field");
			toolBar8.add(textField4);

			//---- button50 ----
			button50.setText("G");
			toolBar8.add(button50);

			//---- button51 ----
			button51.setText("H");
			toolBar8.add(button51);

			//---- button52 ----
			button52.setText("I");
			toolBar8.add(button52);
		}
		add(toolBar8, "cell 1 8 3 1,alignx left,growx 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JToolBar toolBar1;
	private JToolBar toolBar2;
	private JToolBar toolBar3;
	private JToolBar toolBar4;
	private JToolBar toolBar5;
	private JToolBar toolBar6;
	private JToolBar toolBar7;
	private JToolBar toolBar8;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
