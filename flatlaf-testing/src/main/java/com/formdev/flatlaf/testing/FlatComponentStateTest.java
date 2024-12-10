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

import java.util.function.Predicate;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatComponentStateTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatComponentStateTest" );
			frame.showFrame( FlatComponentStateTest::new );
		} );
	}

	FlatComponentStateTest() {
		initComponents();

		Predicate<JComponent> unfocused = c -> false;
		Predicate<JComponent> focused = c -> true;
		textField1.putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER, unfocused );
		textField2.putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER, focused );
		comboBox1.putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER, unfocused );
		comboBox2.putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER, focused );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label11 = new JLabel();
		label12 = new JLabel();
		label32 = new JLabel();
		label5 = new JLabel();
		label7 = new JLabel();
		label6 = new JLabel();
		label8 = new JLabel();
		label13 = new JLabel();
		label14 = new JLabel();
		label15 = new JLabel();
		label16 = new JLabel();
		label9 = new JLabel();
		label33 = new JLabel();
		label1 = new JLabel();
		testStateButton1 = new FlatComponentStateTest.TestStateButton();
		testStateButton7 = new FlatComponentStateTest.TestStateButton();
		testStateButton4 = new FlatComponentStateTest.TestStateButton();
		testStateButton10 = new FlatComponentStateTest.TestStateButton();
		testStateToggleButton1 = new FlatComponentStateTest.TestStateToggleButton();
		testStateToggleButton5 = new FlatComponentStateTest.TestStateToggleButton();
		testStateToggleButton9 = new FlatComponentStateTest.TestStateToggleButton();
		testStateToggleButton12 = new FlatComponentStateTest.TestStateToggleButton();
		testStateButton15 = new FlatComponentStateTest.TestStateButton();
		testStateButton19 = new FlatComponentStateTest.TestStateButton();
		label2 = new JLabel();
		testStateButton2 = new FlatComponentStateTest.TestStateButton();
		testStateButton8 = new FlatComponentStateTest.TestStateButton();
		testStateButton5 = new FlatComponentStateTest.TestStateButton();
		testStateButton11 = new FlatComponentStateTest.TestStateButton();
		testStateToggleButton2 = new FlatComponentStateTest.TestStateToggleButton();
		testStateToggleButton6 = new FlatComponentStateTest.TestStateToggleButton();
		testStateToggleButton10 = new FlatComponentStateTest.TestStateToggleButton();
		testStateToggleButton13 = new FlatComponentStateTest.TestStateToggleButton();
		testStateButton16 = new FlatComponentStateTest.TestStateButton();
		testStateButton20 = new FlatComponentStateTest.TestStateButton();
		label3 = new JLabel();
		testStateButton3 = new FlatComponentStateTest.TestStateButton();
		testStateButton9 = new FlatComponentStateTest.TestStateButton();
		testStateButton6 = new FlatComponentStateTest.TestStateButton();
		testStateButton12 = new FlatComponentStateTest.TestStateButton();
		testStateToggleButton3 = new FlatComponentStateTest.TestStateToggleButton();
		testStateToggleButton7 = new FlatComponentStateTest.TestStateToggleButton();
		testStateToggleButton11 = new FlatComponentStateTest.TestStateToggleButton();
		testStateToggleButton14 = new FlatComponentStateTest.TestStateToggleButton();
		testStateButton17 = new FlatComponentStateTest.TestStateButton();
		testStateButton21 = new FlatComponentStateTest.TestStateButton();
		label4 = new JLabel();
		testStateButton13 = new FlatComponentStateTest.TestStateButton();
		testStateButton14 = new FlatComponentStateTest.TestStateButton();
		testStateToggleButton4 = new FlatComponentStateTest.TestStateToggleButton();
		testStateToggleButton8 = new FlatComponentStateTest.TestStateToggleButton();
		testStateButton18 = new FlatComponentStateTest.TestStateButton();
		label10 = new JLabel();
		button1 = new JButton();
		testDefaultButton1 = new FlatComponentStateTest.TestDefaultButton();
		toggleButton1 = new JToggleButton();
		toggleButton2 = new JToggleButton();
		button2 = new JButton();
		separator1 = new JSeparator();
		label22 = new JLabel();
		label27 = new JLabel();
		label23 = new JLabel();
		label28 = new JLabel();
		label24 = new JLabel();
		label29 = new JLabel();
		label25 = new JLabel();
		label30 = new JLabel();
		label26 = new JLabel();
		label31 = new JLabel();
		label17 = new JLabel();
		testStateCheckBox1 = new FlatComponentStateTest.TestStateCheckBox();
		testStateCheckBox8 = new FlatComponentStateTest.TestStateCheckBox();
		testStateCheckBox5 = new FlatComponentStateTest.TestStateCheckBox();
		testStateCheckBox12 = new FlatComponentStateTest.TestStateCheckBox();
		testStateRadioButton1 = new FlatComponentStateTest.TestStateRadioButton();
		testStateRadioButton8 = new FlatComponentStateTest.TestStateRadioButton();
		testStateRadioButton5 = new FlatComponentStateTest.TestStateRadioButton();
		testStateRadioButton9 = new FlatComponentStateTest.TestStateRadioButton();
		label18 = new JLabel();
		testStateCheckBox2 = new FlatComponentStateTest.TestStateCheckBox();
		testStateCheckBox9 = new FlatComponentStateTest.TestStateCheckBox();
		testStateCheckBox6 = new FlatComponentStateTest.TestStateCheckBox();
		testStateCheckBox13 = new FlatComponentStateTest.TestStateCheckBox();
		testStateRadioButton2 = new FlatComponentStateTest.TestStateRadioButton();
		testStateRadioButton10 = new FlatComponentStateTest.TestStateRadioButton();
		testStateRadioButton6 = new FlatComponentStateTest.TestStateRadioButton();
		testStateRadioButton11 = new FlatComponentStateTest.TestStateRadioButton();
		label19 = new JLabel();
		testStateCheckBox3 = new FlatComponentStateTest.TestStateCheckBox();
		testStateCheckBox10 = new FlatComponentStateTest.TestStateCheckBox();
		testStateCheckBox7 = new FlatComponentStateTest.TestStateCheckBox();
		testStateCheckBox14 = new FlatComponentStateTest.TestStateCheckBox();
		testStateRadioButton3 = new FlatComponentStateTest.TestStateRadioButton();
		testStateRadioButton12 = new FlatComponentStateTest.TestStateRadioButton();
		testStateRadioButton7 = new FlatComponentStateTest.TestStateRadioButton();
		testStateRadioButton13 = new FlatComponentStateTest.TestStateRadioButton();
		label20 = new JLabel();
		testStateCheckBox4 = new FlatComponentStateTest.TestStateCheckBox();
		testStateCheckBox11 = new FlatComponentStateTest.TestStateCheckBox();
		testStateRadioButton4 = new FlatComponentStateTest.TestStateRadioButton();
		testStateRadioButton14 = new FlatComponentStateTest.TestStateRadioButton();
		label21 = new JLabel();
		checkBox1 = new JCheckBox();
		checkBox2 = new JCheckBox();
		radioButton1 = new JRadioButton();
		radioButton2 = new JRadioButton();
		separator2 = new JSeparator();
		label35 = new JLabel();
		textField1 = new JTextField();
		textField2 = new JTextField();
		label38 = new JLabel();
		comboBox1 = new JComboBox<>();
		comboBox2 = new JComboBox<>();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[fill]" +
			"[fill]para" +
			"[fill]" +
			"[fill]para" +
			"[fill]" +
			"[fill]para" +
			"[fill]" +
			"[fill]para" +
			"[fill]" +
			"[fill]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]para" +
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
			"[]"));

		//---- label11 ----
		label11.setText("JButton");
		label11.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(label11, "cell 1 0 2 1");

		//---- label12 ----
		label12.setText("JToggleButton");
		label12.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(label12, "cell 5 0 3 1");

		//---- label32 ----
		label32.setText("Help Button");
		label32.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(label32, "cell 9 0 2 1");

		//---- label5 ----
		label5.setText("regular");
		add(label5, "cell 1 1");

		//---- label7 ----
		label7.setText("default");
		add(label7, "cell 2 1");

		//---- label6 ----
		label6.setText("focused");
		add(label6, "cell 3 1");

		//---- label8 ----
		label8.setText("default");
		add(label8, "cell 4 1");

		//---- label13 ----
		label13.setText("unsel.");
		add(label13, "cell 5 1");

		//---- label14 ----
		label14.setText("selected");
		add(label14, "cell 6 1");

		//---- label15 ----
		label15.setText("focused");
		add(label15, "cell 7 1");

		//---- label16 ----
		label16.setText("selected");
		add(label16, "cell 8 1");

		//---- label9 ----
		label9.setText("regular");
		add(label9, "cell 9 1");

		//---- label33 ----
		label33.setText("focused");
		add(label33, "cell 10 1");

		//---- label1 ----
		label1.setText("none");
		add(label1, "cell 0 2");

		//---- testStateButton1 ----
		testStateButton1.setText("text");
		testStateButton1.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton1, "cell 1 2");

		//---- testStateButton7 ----
		testStateButton7.setText("text");
		testStateButton7.setStateDefault(true);
		testStateButton7.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton7, "cell 2 2");

		//---- testStateButton4 ----
		testStateButton4.setText("text");
		testStateButton4.setStateFocused(true);
		testStateButton4.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton4, "cell 3 2");

		//---- testStateButton10 ----
		testStateButton10.setText("text");
		testStateButton10.setStateFocused(true);
		testStateButton10.setStateDefault(true);
		testStateButton10.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton10, "cell 4 2");

		//---- testStateToggleButton1 ----
		testStateToggleButton1.setText("text");
		add(testStateToggleButton1, "cell 5 2");

		//---- testStateToggleButton5 ----
		testStateToggleButton5.setText("text");
		testStateToggleButton5.setStateSelected(true);
		add(testStateToggleButton5, "cell 6 2");

		//---- testStateToggleButton9 ----
		testStateToggleButton9.setText("text");
		testStateToggleButton9.setStateFocused(true);
		add(testStateToggleButton9, "cell 7 2");

		//---- testStateToggleButton12 ----
		testStateToggleButton12.setText("text");
		testStateToggleButton12.setStateSelected(true);
		testStateToggleButton12.setStateFocused(true);
		add(testStateToggleButton12, "cell 8 2");

		//---- testStateButton15 ----
		testStateButton15.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton15.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton15, "cell 9 2");

		//---- testStateButton19 ----
		testStateButton19.setStateFocused(true);
		testStateButton19.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton19.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton19, "cell 10 2");

		//---- label2 ----
		label2.setText("hover");
		add(label2, "cell 0 3");

		//---- testStateButton2 ----
		testStateButton2.setText("text");
		testStateButton2.setStateHover(true);
		testStateButton2.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton2, "cell 1 3");

		//---- testStateButton8 ----
		testStateButton8.setText("text");
		testStateButton8.setStateHover(true);
		testStateButton8.setStateDefault(true);
		testStateButton8.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton8, "cell 2 3");

		//---- testStateButton5 ----
		testStateButton5.setText("text");
		testStateButton5.setStateHover(true);
		testStateButton5.setStateFocused(true);
		testStateButton5.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton5, "cell 3 3");

		//---- testStateButton11 ----
		testStateButton11.setText("text");
		testStateButton11.setStateHover(true);
		testStateButton11.setStateFocused(true);
		testStateButton11.setStateDefault(true);
		testStateButton11.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton11, "cell 4 3");

		//---- testStateToggleButton2 ----
		testStateToggleButton2.setText("text");
		testStateToggleButton2.setStateHover(true);
		add(testStateToggleButton2, "cell 5 3");

		//---- testStateToggleButton6 ----
		testStateToggleButton6.setText("text");
		testStateToggleButton6.setStateHover(true);
		testStateToggleButton6.setStateSelected(true);
		add(testStateToggleButton6, "cell 6 3");

		//---- testStateToggleButton10 ----
		testStateToggleButton10.setText("text");
		testStateToggleButton10.setStateHover(true);
		testStateToggleButton10.setStateFocused(true);
		add(testStateToggleButton10, "cell 7 3");

		//---- testStateToggleButton13 ----
		testStateToggleButton13.setText("text");
		testStateToggleButton13.setStateHover(true);
		testStateToggleButton13.setStateSelected(true);
		testStateToggleButton13.setStateFocused(true);
		add(testStateToggleButton13, "cell 8 3");

		//---- testStateButton16 ----
		testStateButton16.setStateHover(true);
		testStateButton16.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton16.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton16, "cell 9 3");

		//---- testStateButton20 ----
		testStateButton20.setStateHover(true);
		testStateButton20.setStateFocused(true);
		testStateButton20.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton20.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton20, "cell 10 3");

		//---- label3 ----
		label3.setText("pressed");
		add(label3, "cell 0 4");

		//---- testStateButton3 ----
		testStateButton3.setText("text");
		testStateButton3.setStatePressed(true);
		testStateButton3.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton3, "cell 1 4");

		//---- testStateButton9 ----
		testStateButton9.setText("text");
		testStateButton9.setStatePressed(true);
		testStateButton9.setStateDefault(true);
		testStateButton9.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton9, "cell 2 4");

		//---- testStateButton6 ----
		testStateButton6.setText("text");
		testStateButton6.setStatePressed(true);
		testStateButton6.setStateFocused(true);
		testStateButton6.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton6, "cell 3 4");

		//---- testStateButton12 ----
		testStateButton12.setText("text");
		testStateButton12.setStatePressed(true);
		testStateButton12.setStateFocused(true);
		testStateButton12.setStateDefault(true);
		testStateButton12.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton12, "cell 4 4");

		//---- testStateToggleButton3 ----
		testStateToggleButton3.setText("text");
		testStateToggleButton3.setStatePressed(true);
		add(testStateToggleButton3, "cell 5 4");

		//---- testStateToggleButton7 ----
		testStateToggleButton7.setText("text");
		testStateToggleButton7.setStatePressed(true);
		testStateToggleButton7.setStateSelected(true);
		add(testStateToggleButton7, "cell 6 4");

		//---- testStateToggleButton11 ----
		testStateToggleButton11.setText("text");
		testStateToggleButton11.setStatePressed(true);
		testStateToggleButton11.setStateFocused(true);
		add(testStateToggleButton11, "cell 7 4");

		//---- testStateToggleButton14 ----
		testStateToggleButton14.setText("text");
		testStateToggleButton14.setStatePressed(true);
		testStateToggleButton14.setStateSelected(true);
		testStateToggleButton14.setStateFocused(true);
		add(testStateToggleButton14, "cell 8 4");

		//---- testStateButton17 ----
		testStateButton17.setStatePressed(true);
		testStateButton17.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton17.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton17, "cell 9 4");

		//---- testStateButton21 ----
		testStateButton21.setStatePressed(true);
		testStateButton21.setStateFocused(true);
		testStateButton21.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton21.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton21, "cell 10 4");

		//---- label4 ----
		label4.setText("disabled");
		add(label4, "cell 0 5");

		//---- testStateButton13 ----
		testStateButton13.setText("text");
		testStateButton13.setEnabled(false);
		testStateButton13.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton13, "cell 1 5");

		//---- testStateButton14 ----
		testStateButton14.setText("text");
		testStateButton14.setEnabled(false);
		testStateButton14.setStateDefault(true);
		testStateButton14.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton14, "cell 2 5");

		//---- testStateToggleButton4 ----
		testStateToggleButton4.setText("text");
		testStateToggleButton4.setEnabled(false);
		add(testStateToggleButton4, "cell 5 5");

		//---- testStateToggleButton8 ----
		testStateToggleButton8.setText("text");
		testStateToggleButton8.setEnabled(false);
		testStateToggleButton8.setStateSelected(true);
		add(testStateToggleButton8, "cell 6 5");

		//---- testStateButton18 ----
		testStateButton18.setEnabled(false);
		testStateButton18.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton18.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton18, "cell 9 5");

		//---- label10 ----
		label10.setText("raw");
		add(label10, "cell 0 6");

		//---- button1 ----
		button1.setText("text");
		button1.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(button1, "cell 1 6");

		//---- testDefaultButton1 ----
		testDefaultButton1.setText("text");
		testDefaultButton1.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testDefaultButton1, "cell 2 6");

		//---- toggleButton1 ----
		toggleButton1.setText("text");
		add(toggleButton1, "cell 5 6");

		//---- toggleButton2 ----
		toggleButton2.setText("text");
		toggleButton2.setSelected(true);
		add(toggleButton2, "cell 6 6");

		//---- button2 ----
		button2.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		button2.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(button2, "cell 9 6");
		add(separator1, "cell 0 7 11 1");

		//---- label22 ----
		label22.setText("JCheckBox");
		label22.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(label22, "cell 1 8 2 1");

		//---- label27 ----
		label27.setText("JRadioButton");
		label27.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(label27, "cell 5 8 2 1");

		//---- label23 ----
		label23.setText("unsel.");
		add(label23, "cell 1 9");

		//---- label28 ----
		label28.setText("selected");
		add(label28, "cell 2 9");

		//---- label24 ----
		label24.setText("focused");
		add(label24, "cell 3 9");

		//---- label29 ----
		label29.setText("selected");
		add(label29, "cell 4 9");

		//---- label25 ----
		label25.setText("unsel.");
		add(label25, "cell 5 9");

		//---- label30 ----
		label30.setText("selected");
		add(label30, "cell 6 9");

		//---- label26 ----
		label26.setText("focused");
		add(label26, "cell 7 9");

		//---- label31 ----
		label31.setText("selected");
		add(label31, "cell 8 9");

		//---- label17 ----
		label17.setText("none");
		add(label17, "cell 0 10");

		//---- testStateCheckBox1 ----
		testStateCheckBox1.setText("text");
		add(testStateCheckBox1, "cell 1 10");

		//---- testStateCheckBox8 ----
		testStateCheckBox8.setText("text");
		testStateCheckBox8.setStateSelected(true);
		add(testStateCheckBox8, "cell 2 10");

		//---- testStateCheckBox5 ----
		testStateCheckBox5.setText("text");
		testStateCheckBox5.setStateFocused(true);
		add(testStateCheckBox5, "cell 3 10");

		//---- testStateCheckBox12 ----
		testStateCheckBox12.setText("text");
		testStateCheckBox12.setStateFocused(true);
		testStateCheckBox12.setStateSelected(true);
		add(testStateCheckBox12, "cell 4 10");

		//---- testStateRadioButton1 ----
		testStateRadioButton1.setText("text");
		add(testStateRadioButton1, "cell 5 10");

		//---- testStateRadioButton8 ----
		testStateRadioButton8.setText("text");
		testStateRadioButton8.setStateSelected(true);
		add(testStateRadioButton8, "cell 6 10");

		//---- testStateRadioButton5 ----
		testStateRadioButton5.setText("text");
		testStateRadioButton5.setStateFocused(true);
		add(testStateRadioButton5, "cell 7 10");

		//---- testStateRadioButton9 ----
		testStateRadioButton9.setText("text");
		testStateRadioButton9.setStateFocused(true);
		testStateRadioButton9.setStateSelected(true);
		add(testStateRadioButton9, "cell 8 10");

		//---- label18 ----
		label18.setText("hover");
		add(label18, "cell 0 11");

		//---- testStateCheckBox2 ----
		testStateCheckBox2.setText("text");
		testStateCheckBox2.setStateHover(true);
		add(testStateCheckBox2, "cell 1 11");

		//---- testStateCheckBox9 ----
		testStateCheckBox9.setText("text");
		testStateCheckBox9.setStateHover(true);
		testStateCheckBox9.setStateSelected(true);
		add(testStateCheckBox9, "cell 2 11");

		//---- testStateCheckBox6 ----
		testStateCheckBox6.setText("text");
		testStateCheckBox6.setStateFocused(true);
		testStateCheckBox6.setStateHover(true);
		add(testStateCheckBox6, "cell 3 11");

		//---- testStateCheckBox13 ----
		testStateCheckBox13.setText("text");
		testStateCheckBox13.setStateFocused(true);
		testStateCheckBox13.setStateHover(true);
		testStateCheckBox13.setStateSelected(true);
		add(testStateCheckBox13, "cell 4 11");

		//---- testStateRadioButton2 ----
		testStateRadioButton2.setText("text");
		testStateRadioButton2.setStateHover(true);
		add(testStateRadioButton2, "cell 5 11");

		//---- testStateRadioButton10 ----
		testStateRadioButton10.setText("text");
		testStateRadioButton10.setStateHover(true);
		testStateRadioButton10.setStateSelected(true);
		add(testStateRadioButton10, "cell 6 11");

		//---- testStateRadioButton6 ----
		testStateRadioButton6.setText("text");
		testStateRadioButton6.setStateFocused(true);
		testStateRadioButton6.setStateHover(true);
		add(testStateRadioButton6, "cell 7 11");

		//---- testStateRadioButton11 ----
		testStateRadioButton11.setText("text");
		testStateRadioButton11.setStateFocused(true);
		testStateRadioButton11.setStateHover(true);
		testStateRadioButton11.setStateSelected(true);
		add(testStateRadioButton11, "cell 8 11");

		//---- label19 ----
		label19.setText("pressed");
		add(label19, "cell 0 12");

		//---- testStateCheckBox3 ----
		testStateCheckBox3.setText("text");
		testStateCheckBox3.setStatePressed(true);
		add(testStateCheckBox3, "cell 1 12");

		//---- testStateCheckBox10 ----
		testStateCheckBox10.setText("text");
		testStateCheckBox10.setStatePressed(true);
		testStateCheckBox10.setStateSelected(true);
		add(testStateCheckBox10, "cell 2 12");

		//---- testStateCheckBox7 ----
		testStateCheckBox7.setText("text");
		testStateCheckBox7.setStateFocused(true);
		testStateCheckBox7.setStatePressed(true);
		add(testStateCheckBox7, "cell 3 12");

		//---- testStateCheckBox14 ----
		testStateCheckBox14.setText("text");
		testStateCheckBox14.setStateFocused(true);
		testStateCheckBox14.setStatePressed(true);
		testStateCheckBox14.setStateSelected(true);
		add(testStateCheckBox14, "cell 4 12");

		//---- testStateRadioButton3 ----
		testStateRadioButton3.setText("text");
		testStateRadioButton3.setStatePressed(true);
		add(testStateRadioButton3, "cell 5 12");

		//---- testStateRadioButton12 ----
		testStateRadioButton12.setText("text");
		testStateRadioButton12.setStatePressed(true);
		testStateRadioButton12.setStateSelected(true);
		add(testStateRadioButton12, "cell 6 12");

		//---- testStateRadioButton7 ----
		testStateRadioButton7.setText("text");
		testStateRadioButton7.setStateFocused(true);
		testStateRadioButton7.setStatePressed(true);
		add(testStateRadioButton7, "cell 7 12");

		//---- testStateRadioButton13 ----
		testStateRadioButton13.setText("text");
		testStateRadioButton13.setStateFocused(true);
		testStateRadioButton13.setStatePressed(true);
		testStateRadioButton13.setStateSelected(true);
		add(testStateRadioButton13, "cell 8 12");

		//---- label20 ----
		label20.setText("disabled");
		add(label20, "cell 0 13");

		//---- testStateCheckBox4 ----
		testStateCheckBox4.setText("text");
		testStateCheckBox4.setEnabled(false);
		add(testStateCheckBox4, "cell 1 13");

		//---- testStateCheckBox11 ----
		testStateCheckBox11.setText("text");
		testStateCheckBox11.setEnabled(false);
		testStateCheckBox11.setStateSelected(true);
		add(testStateCheckBox11, "cell 2 13");

		//---- testStateRadioButton4 ----
		testStateRadioButton4.setText("text");
		testStateRadioButton4.setEnabled(false);
		add(testStateRadioButton4, "cell 5 13");

		//---- testStateRadioButton14 ----
		testStateRadioButton14.setText("text");
		testStateRadioButton14.setEnabled(false);
		testStateRadioButton14.setStateSelected(true);
		add(testStateRadioButton14, "cell 6 13");

		//---- label21 ----
		label21.setText("raw");
		add(label21, "cell 0 14");

		//---- checkBox1 ----
		checkBox1.setText("text");
		add(checkBox1, "cell 1 14");

		//---- checkBox2 ----
		checkBox2.setText("text");
		checkBox2.setSelected(true);
		add(checkBox2, "cell 2 14");

		//---- radioButton1 ----
		radioButton1.setText("text");
		add(radioButton1, "cell 5 14");

		//---- radioButton2 ----
		radioButton2.setText("text");
		radioButton2.setSelected(true);
		add(radioButton2, "cell 6 14");
		add(separator2, "cell 0 15 11 1");

		//---- label35 ----
		label35.setText("JTextField");
		add(label35, "cell 0 16");
		add(textField1, "cell 1 16 2 1");
		add(textField2, "cell 3 16 2 1");

		//---- label38 ----
		label38.setText("JComboBox");
		add(label38, "cell 0 17");
		add(comboBox1, "cell 1 17 2 1");
		add(comboBox2, "cell 3 17 2 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label11;
	private JLabel label12;
	private JLabel label32;
	private JLabel label5;
	private JLabel label7;
	private JLabel label6;
	private JLabel label8;
	private JLabel label13;
	private JLabel label14;
	private JLabel label15;
	private JLabel label16;
	private JLabel label9;
	private JLabel label33;
	private JLabel label1;
	private FlatComponentStateTest.TestStateButton testStateButton1;
	private FlatComponentStateTest.TestStateButton testStateButton7;
	private FlatComponentStateTest.TestStateButton testStateButton4;
	private FlatComponentStateTest.TestStateButton testStateButton10;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton1;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton5;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton9;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton12;
	private FlatComponentStateTest.TestStateButton testStateButton15;
	private FlatComponentStateTest.TestStateButton testStateButton19;
	private JLabel label2;
	private FlatComponentStateTest.TestStateButton testStateButton2;
	private FlatComponentStateTest.TestStateButton testStateButton8;
	private FlatComponentStateTest.TestStateButton testStateButton5;
	private FlatComponentStateTest.TestStateButton testStateButton11;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton2;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton6;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton10;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton13;
	private FlatComponentStateTest.TestStateButton testStateButton16;
	private FlatComponentStateTest.TestStateButton testStateButton20;
	private JLabel label3;
	private FlatComponentStateTest.TestStateButton testStateButton3;
	private FlatComponentStateTest.TestStateButton testStateButton9;
	private FlatComponentStateTest.TestStateButton testStateButton6;
	private FlatComponentStateTest.TestStateButton testStateButton12;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton3;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton7;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton11;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton14;
	private FlatComponentStateTest.TestStateButton testStateButton17;
	private FlatComponentStateTest.TestStateButton testStateButton21;
	private JLabel label4;
	private FlatComponentStateTest.TestStateButton testStateButton13;
	private FlatComponentStateTest.TestStateButton testStateButton14;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton4;
	private FlatComponentStateTest.TestStateToggleButton testStateToggleButton8;
	private FlatComponentStateTest.TestStateButton testStateButton18;
	private JLabel label10;
	private JButton button1;
	private FlatComponentStateTest.TestDefaultButton testDefaultButton1;
	private JToggleButton toggleButton1;
	private JToggleButton toggleButton2;
	private JButton button2;
	private JSeparator separator1;
	private JLabel label22;
	private JLabel label27;
	private JLabel label23;
	private JLabel label28;
	private JLabel label24;
	private JLabel label29;
	private JLabel label25;
	private JLabel label30;
	private JLabel label26;
	private JLabel label31;
	private JLabel label17;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox1;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox8;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox5;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox12;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton1;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton8;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton5;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton9;
	private JLabel label18;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox2;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox9;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox6;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox13;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton2;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton10;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton6;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton11;
	private JLabel label19;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox3;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox10;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox7;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox14;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton3;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton12;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton7;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton13;
	private JLabel label20;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox4;
	private FlatComponentStateTest.TestStateCheckBox testStateCheckBox11;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton4;
	private FlatComponentStateTest.TestStateRadioButton testStateRadioButton14;
	private JLabel label21;
	private JCheckBox checkBox1;
	private JCheckBox checkBox2;
	private JRadioButton radioButton1;
	private JRadioButton radioButton2;
	private JSeparator separator2;
	private JLabel label35;
	private JTextField textField1;
	private JTextField textField2;
	private JLabel label38;
	private JComboBox<String> comboBox1;
	private JComboBox<String> comboBox2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class TestStateButton ----------------------------------------------

	private static class TestStateButton
		extends JButton
	{
		private boolean stateHover;
		private boolean statePressed;
		private boolean stateFocused;
		private boolean stateDefault;

		public TestStateButton() {
			setModel( new DefaultButtonModel() {
				@Override
				public boolean isRollover() {
					return isStateHover();
				}
				@Override
				public boolean isPressed() {
					return isStatePressed();
				}
			} );

			putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER,
				(Predicate<JComponent>) c -> {
					return ((TestStateButton)c).isStateFocused();
				} );
		}

		public boolean isStateHover() {
			return stateHover;
		}

		public void setStateHover( boolean stateHover ) {
			this.stateHover = stateHover;
		}

		public boolean isStatePressed() {
			return statePressed;
		}

		public void setStatePressed( boolean statePressed ) {
			this.statePressed = statePressed;
		}

		public boolean isStateFocused() {
			return stateFocused;
		}

		public void setStateFocused( boolean stateFocused ) {
			this.stateFocused = stateFocused;
		}

		public boolean isStateDefault() {
			return stateDefault;
		}

		public void setStateDefault( boolean stateDefault ) {
			this.stateDefault = stateDefault;
		}

		@Override
		public boolean isDefaultButton() {
			return isStateDefault();
		}
	}

	//---- class TestStateToggleButton ----------------------------------------

	private static class TestStateToggleButton
		extends JToggleButton
	{
		private boolean stateHover;
		private boolean statePressed;
		private boolean stateFocused;
		private boolean stateSelected;

		public TestStateToggleButton() {
			setModel( new DefaultButtonModel() {
				@Override
				public boolean isRollover() {
					return isStateHover();
				}
				@Override
				public boolean isPressed() {
					return isStatePressed();
				}
				@Override
				public boolean isSelected() {
					return isStateSelected();
				}
			} );

			putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER,
				(Predicate<JComponent>) c -> {
					return ((TestStateToggleButton)c).isStateFocused();
				} );
		}

		public boolean isStateHover() {
			return stateHover;
		}

		public void setStateHover( boolean stateHover ) {
			this.stateHover = stateHover;
		}

		public boolean isStatePressed() {
			return statePressed;
		}

		public void setStatePressed( boolean statePressed ) {
			this.statePressed = statePressed;
		}

		public boolean isStateFocused() {
			return stateFocused;
		}

		public void setStateFocused( boolean stateFocused ) {
			this.stateFocused = stateFocused;
		}

		public boolean isStateSelected() {
			return stateSelected;
		}

		public void setStateSelected( boolean stateSelected ) {
			this.stateSelected = stateSelected;
		}
	}

	//---- class TestStateCheckBox --------------------------------------------

	private static class TestStateCheckBox
		extends JCheckBox
	{
		private boolean stateHover;
		private boolean statePressed;
		private boolean stateFocused;
		private boolean stateSelected;

		public TestStateCheckBox() {
			setModel( new DefaultButtonModel() {
				@Override
				public boolean isRollover() {
					return isStateHover();
				}
				@Override
				public boolean isPressed() {
					return isStatePressed();
				}
				@Override
				public boolean isSelected() {
					return isStateSelected();
				}
			} );

			putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER,
				(Predicate<JComponent>) c -> {
					return ((TestStateCheckBox)c).isStateFocused();
				} );
		}

		public boolean isStateHover() {
			return stateHover;
		}

		public void setStateHover( boolean stateHover ) {
			this.stateHover = stateHover;
		}

		public boolean isStatePressed() {
			return statePressed;
		}

		public void setStatePressed( boolean statePressed ) {
			this.statePressed = statePressed;
		}

		public boolean isStateFocused() {
			return stateFocused;
		}

		public void setStateFocused( boolean stateFocused ) {
			this.stateFocused = stateFocused;
		}

		public boolean isStateSelected() {
			return stateSelected;
		}

		public void setStateSelected( boolean stateSelected ) {
			this.stateSelected = stateSelected;
		}
	}

	//---- class TestStateRadioButton -----------------------------------------

	private static class TestStateRadioButton
		extends JRadioButton
	{
		private boolean stateHover;
		private boolean statePressed;
		private boolean stateFocused;
		private boolean stateSelected;

		public TestStateRadioButton() {
			setModel( new DefaultButtonModel() {
				@Override
				public boolean isRollover() {
					return isStateHover();
				}
				@Override
				public boolean isPressed() {
					return isStatePressed();
				}
				@Override
				public boolean isSelected() {
					return isStateSelected();
				}
			} );

			putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER,
				(Predicate<JComponent>) c -> {
					return ((TestStateRadioButton)c).isStateFocused();
				} );
		}

		public boolean isStateHover() {
			return stateHover;
		}

		public void setStateHover( boolean stateHover ) {
			this.stateHover = stateHover;
		}

		public boolean isStatePressed() {
			return statePressed;
		}

		public void setStatePressed( boolean statePressed ) {
			this.statePressed = statePressed;
		}

		public boolean isStateFocused() {
			return stateFocused;
		}

		public void setStateFocused( boolean stateFocused ) {
			this.stateFocused = stateFocused;
		}

		public boolean isStateSelected() {
			return stateSelected;
		}

		public void setStateSelected( boolean stateSelected ) {
			this.stateSelected = stateSelected;
		}
	}

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
