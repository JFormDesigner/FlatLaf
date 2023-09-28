/*
 * Copyright 2023 FormDev Software GmbH
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

import java.awt.*;
import java.util.function.Predicate;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatRadioButtonsTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatRadioButtonsTest" );
			frame.showFrame( FlatRadioButtonsTest::new );
		} );
	}

	FlatRadioButtonsTest() {
		initComponents();
		focusAll();
	}

	private void focusAll() {
		boolean focusAll = focusAllCheckBox.isSelected();
		for( Component c : getComponents() ) {
			if( c instanceof JRadioButton )
				((JRadioButton) c).putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER,
					focusAll ? (Predicate<JComponent>) comp -> true : null );
		}
		repaint();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label5 = new JLabel();
		JLabel label6 = new JLabel();
		JLabel label1 = new JLabel();
		JLabel label3 = new JLabel();
		JLabel label13 = new JLabel();
		JLabel label14 = new JLabel();
		JLabel label7 = new JLabel();
		JRadioButton radioButton1 = new JRadioButton();
		JRadioButton radioButton13 = new JRadioButton();
		JRadioButton radioButton7 = new JRadioButton();
		JRadioButton radioButton19 = new JRadioButton();
		JRadioButton radioButton25 = new JRadioButton();
		JRadioButton radioButton31 = new JRadioButton();
		JLabel label8 = new JLabel();
		JRadioButton radioButton2 = new JRadioButton();
		JRadioButton radioButton14 = new JRadioButton();
		JRadioButton radioButton8 = new JRadioButton();
		JRadioButton radioButton20 = new JRadioButton();
		JRadioButton radioButton26 = new JRadioButton();
		JRadioButton radioButton32 = new JRadioButton();
		JLabel label9 = new JLabel();
		JRadioButton radioButton3 = new JRadioButton();
		JRadioButton radioButton15 = new JRadioButton();
		JRadioButton radioButton9 = new JRadioButton();
		JRadioButton radioButton21 = new JRadioButton();
		JRadioButton radioButton27 = new JRadioButton();
		JRadioButton radioButton33 = new JRadioButton();
		JLabel label2 = new JLabel();
		JLabel label4 = new JLabel();
		JLabel label10 = new JLabel();
		JRadioButton radioButton4 = new JRadioButton();
		JRadioButton radioButton16 = new JRadioButton();
		JRadioButton radioButton10 = new JRadioButton();
		JRadioButton radioButton22 = new JRadioButton();
		JRadioButton radioButton28 = new JRadioButton();
		JRadioButton radioButton34 = new JRadioButton();
		JLabel label11 = new JLabel();
		JRadioButton radioButton5 = new JRadioButton();
		JRadioButton radioButton17 = new JRadioButton();
		JRadioButton radioButton11 = new JRadioButton();
		JRadioButton radioButton23 = new JRadioButton();
		JRadioButton radioButton29 = new JRadioButton();
		JRadioButton radioButton35 = new JRadioButton();
		JLabel label12 = new JLabel();
		JRadioButton radioButton6 = new JRadioButton();
		JRadioButton radioButton18 = new JRadioButton();
		JRadioButton radioButton12 = new JRadioButton();
		JRadioButton radioButton24 = new JRadioButton();
		JRadioButton radioButton30 = new JRadioButton();
		JRadioButton radioButton36 = new JRadioButton();
		focusAllCheckBox = new JCheckBox();

		//======== this ========
		setBackground(new Color(0xe0e0e0));
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[80,fill]para" +
			"[]para" +
			"[80,fill]" +
			"[]para" +
			"[70,fill]" +
			"[70,fill]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]para" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[grow]" +
			"[]"));

		//---- label5 ----
		label5.setText("Default Border");
		add(label5, "cell 1 0");

		//---- label6 ----
		label6.setText("Empty Border");
		add(label6, "cell 3 0");

		//---- label1 ----
		label1.setText("horzTextPos TRAILING");
		add(label1, "cell 1 1 2 1");

		//---- label3 ----
		label3.setText("horzTextPos TRAILING");
		add(label3, "cell 3 1 2 1");

		//---- label13 ----
		label13.setText("left 20, right 0");
		add(label13, "cell 5 1");

		//---- label14 ----
		label14.setText("left 0, right 20");
		add(label14, "cell 6 1");

		//---- label7 ----
		label7.setText("hAlign LEADING");
		add(label7, "cell 0 2");

		//---- radioButton1 ----
		radioButton1.setText("text");
		radioButton1.setOpaque(true);
		add(radioButton1, "cell 1 2");

		//---- radioButton13 ----
		radioButton13.setText("text");
		radioButton13.setOpaque(true);
		add(radioButton13, "cell 2 2");

		//---- radioButton7 ----
		radioButton7.setText("text");
		radioButton7.setOpaque(true);
		radioButton7.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton7, "cell 3 2");

		//---- radioButton19 ----
		radioButton19.setText("text");
		radioButton19.setOpaque(true);
		radioButton19.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton19, "cell 4 2");

		//---- radioButton25 ----
		radioButton25.setText("text");
		radioButton25.setOpaque(true);
		radioButton25.setBorder(new EmptyBorder(0, 20, 0, 0));
		add(radioButton25, "cell 5 2");

		//---- radioButton31 ----
		radioButton31.setText("text");
		radioButton31.setOpaque(true);
		radioButton31.setBorder(new EmptyBorder(0, 0, 0, 20));
		add(radioButton31, "cell 6 2");

		//---- label8 ----
		label8.setText("hAlign CENTER");
		add(label8, "cell 0 3");

		//---- radioButton2 ----
		radioButton2.setText("text");
		radioButton2.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton2.setOpaque(true);
		add(radioButton2, "cell 1 3");

		//---- radioButton14 ----
		radioButton14.setText("text");
		radioButton14.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton14.setOpaque(true);
		add(radioButton14, "cell 2 3");

		//---- radioButton8 ----
		radioButton8.setText("text");
		radioButton8.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton8.setOpaque(true);
		radioButton8.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton8, "cell 3 3");

		//---- radioButton20 ----
		radioButton20.setText("text");
		radioButton20.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton20.setOpaque(true);
		radioButton20.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton20, "cell 4 3");

		//---- radioButton26 ----
		radioButton26.setText("text");
		radioButton26.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton26.setOpaque(true);
		radioButton26.setBorder(new EmptyBorder(0, 20, 0, 0));
		add(radioButton26, "cell 5 3");

		//---- radioButton32 ----
		radioButton32.setText("text");
		radioButton32.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton32.setOpaque(true);
		radioButton32.setBorder(new EmptyBorder(0, 0, 0, 20));
		add(radioButton32, "cell 6 3");

		//---- label9 ----
		label9.setText("hAlign TRAILING");
		add(label9, "cell 0 4");

		//---- radioButton3 ----
		radioButton3.setText("text");
		radioButton3.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton3.setOpaque(true);
		add(radioButton3, "cell 1 4");

		//---- radioButton15 ----
		radioButton15.setText("text");
		radioButton15.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton15.setOpaque(true);
		add(radioButton15, "cell 2 4");

		//---- radioButton9 ----
		radioButton9.setText("text");
		radioButton9.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton9.setOpaque(true);
		radioButton9.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton9, "cell 3 4");

		//---- radioButton21 ----
		radioButton21.setText("text");
		radioButton21.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton21.setOpaque(true);
		radioButton21.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton21, "cell 4 4");

		//---- radioButton27 ----
		radioButton27.setText("text");
		radioButton27.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton27.setOpaque(true);
		radioButton27.setBorder(new EmptyBorder(0, 20, 0, 0));
		add(radioButton27, "cell 5 4");

		//---- radioButton33 ----
		radioButton33.setText("text");
		radioButton33.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton33.setOpaque(true);
		radioButton33.setBorder(new EmptyBorder(0, 0, 0, 20));
		add(radioButton33, "cell 6 4");

		//---- label2 ----
		label2.setText("horzTextPos LEADING");
		add(label2, "cell 1 5 2 1");

		//---- label4 ----
		label4.setText("horzTextPos LEADING");
		add(label4, "cell 3 5 2 1");

		//---- label10 ----
		label10.setText("hAlign LEADING");
		add(label10, "cell 0 6");

		//---- radioButton4 ----
		radioButton4.setText("text");
		radioButton4.setOpaque(true);
		radioButton4.setHorizontalTextPosition(SwingConstants.LEADING);
		add(radioButton4, "cell 1 6");

		//---- radioButton16 ----
		radioButton16.setText("text");
		radioButton16.setOpaque(true);
		radioButton16.setHorizontalTextPosition(SwingConstants.LEADING);
		add(radioButton16, "cell 2 6");

		//---- radioButton10 ----
		radioButton10.setText("text");
		radioButton10.setOpaque(true);
		radioButton10.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton10.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton10, "cell 3 6");

		//---- radioButton22 ----
		radioButton22.setText("text");
		radioButton22.setOpaque(true);
		radioButton22.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton22.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton22, "cell 4 6");

		//---- radioButton28 ----
		radioButton28.setText("text");
		radioButton28.setOpaque(true);
		radioButton28.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton28.setBorder(new EmptyBorder(0, 20, 0, 0));
		add(radioButton28, "cell 5 6");

		//---- radioButton34 ----
		radioButton34.setText("text");
		radioButton34.setOpaque(true);
		radioButton34.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton34.setBorder(new EmptyBorder(0, 0, 0, 20));
		add(radioButton34, "cell 6 6");

		//---- label11 ----
		label11.setText("hAlign CENTER");
		add(label11, "cell 0 7");

		//---- radioButton5 ----
		radioButton5.setText("text");
		radioButton5.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton5.setOpaque(true);
		radioButton5.setHorizontalTextPosition(SwingConstants.LEADING);
		add(radioButton5, "cell 1 7");

		//---- radioButton17 ----
		radioButton17.setText("text");
		radioButton17.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton17.setOpaque(true);
		radioButton17.setHorizontalTextPosition(SwingConstants.LEADING);
		add(radioButton17, "cell 2 7");

		//---- radioButton11 ----
		radioButton11.setText("text");
		radioButton11.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton11.setOpaque(true);
		radioButton11.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton11.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton11, "cell 3 7");

		//---- radioButton23 ----
		radioButton23.setText("text");
		radioButton23.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton23.setOpaque(true);
		radioButton23.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton23.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton23, "cell 4 7");

		//---- radioButton29 ----
		radioButton29.setText("text");
		radioButton29.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton29.setOpaque(true);
		radioButton29.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton29.setBorder(new EmptyBorder(0, 20, 0, 0));
		add(radioButton29, "cell 5 7");

		//---- radioButton35 ----
		radioButton35.setText("text");
		radioButton35.setHorizontalAlignment(SwingConstants.CENTER);
		radioButton35.setOpaque(true);
		radioButton35.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton35.setBorder(new EmptyBorder(0, 0, 0, 20));
		add(radioButton35, "cell 6 7");

		//---- label12 ----
		label12.setText("hAlign TRAILING");
		add(label12, "cell 0 8");

		//---- radioButton6 ----
		radioButton6.setText("text");
		radioButton6.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton6.setOpaque(true);
		radioButton6.setHorizontalTextPosition(SwingConstants.LEADING);
		add(radioButton6, "cell 1 8");

		//---- radioButton18 ----
		radioButton18.setText("text");
		radioButton18.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton18.setOpaque(true);
		radioButton18.setHorizontalTextPosition(SwingConstants.LEADING);
		add(radioButton18, "cell 2 8");

		//---- radioButton12 ----
		radioButton12.setText("text");
		radioButton12.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton12.setOpaque(true);
		radioButton12.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton12.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton12, "cell 3 8");

		//---- radioButton24 ----
		radioButton24.setText("text");
		radioButton24.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton24.setOpaque(true);
		radioButton24.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton24.setBorder(BorderFactory.createEmptyBorder());
		add(radioButton24, "cell 4 8");

		//---- radioButton30 ----
		radioButton30.setText("text");
		radioButton30.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton30.setOpaque(true);
		radioButton30.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton30.setBorder(new EmptyBorder(0, 20, 0, 0));
		add(radioButton30, "cell 5 8");

		//---- radioButton36 ----
		radioButton36.setText("text");
		radioButton36.setHorizontalAlignment(SwingConstants.TRAILING);
		radioButton36.setOpaque(true);
		radioButton36.setHorizontalTextPosition(SwingConstants.LEADING);
		radioButton36.setBorder(new EmptyBorder(0, 0, 0, 20));
		add(radioButton36, "cell 6 8");

		//---- focusAllCheckBox ----
		focusAllCheckBox.setText("focus all");
		focusAllCheckBox.setSelected(true);
		focusAllCheckBox.addActionListener(e -> focusAll());
		add(focusAllCheckBox, "cell 0 10,alignx left,growx 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox focusAllCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
