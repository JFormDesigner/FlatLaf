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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatFileViewFloppyDriveIcon;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatCustomBordersTest
	extends FlatTestPanel
{
	private static final Color RED = new Color( 0x60ff0000, true );
	private static final Color GREEN = new Color( 0x6000ff00, true );
	private static final Color MAGENTA = new Color( 0x60ff00ff, true );
	private static final Color BLUE = new Color( 0x300000ff, true );

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatCustomBordersTest" );
			frame.showFrame( FlatCustomBordersTest::new );
		} );
	}

	@SuppressWarnings( "unchecked" )
	FlatCustomBordersTest() {
		initComponents();
		applyCustomBorders();
		applySpecialComboBoxRenderers();

		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>( new String[] {
			"text",
			"123",
			"4567",
			"abc",
			"def"
		} );

		for( Component c : getComponents() ) {
			if( c instanceof JComboBox )
				((JComboBox<String>)c).setModel( model );
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();

		if( textField2 != null )
			applyCustomBorders();
	}

	private void applyCustomBorders() {
		LineBorder lineBorder = new LineBorder( MAGENTA, UIScale.scale( 3 ) );

		applyCustomInsideBorder( button2, "Button.border" );
		applyCustomInsideBorder( button6, "Button.border" );
		applyCustomOutsideBorder( button3, "Button.border" );
		applyCustomOutsideBorder( button7, "Button.border" );
		button4.setBorder( lineBorder );
		button8.setBorder( lineBorder );

		applyCustomInsideBorder( comboBox2, "ComboBox.border" );
		applyCustomInsideBorder( comboBox6, "ComboBox.border" );
		applyCustomOutsideBorder( comboBox3, "ComboBox.border" );
		applyCustomOutsideBorder( comboBox7, "ComboBox.border" );
		comboBox4.setBorder( lineBorder );
		comboBox8.setBorder( lineBorder );

		applyCustomInsideBorder( comboBox10, "ComboBox.border" );
		applyCustomInsideBorder( comboBox14, "ComboBox.border" );
		applyCustomOutsideBorder( comboBox11, "ComboBox.border" );
		applyCustomOutsideBorder( comboBox15, "ComboBox.border" );
		comboBox12.setBorder( lineBorder );
		comboBox16.setBorder( lineBorder );

		applyCustomInsideBorder( spinner2, "Spinner.border" );
		applyCustomInsideBorder( spinner6, "Spinner.border" );
		applyCustomOutsideBorder( spinner3, "Spinner.border" );
		applyCustomOutsideBorder( spinner7, "Spinner.border" );
		spinner4.setBorder( lineBorder );
		spinner8.setBorder( lineBorder );

		applyCustomInsideBorder( textField2, "TextField.border" );
		applyCustomInsideBorder( textField6, "TextField.border" );
		applyCustomOutsideBorder( textField3, "TextField.border" );
		applyCustomOutsideBorder( textField7, "TextField.border" );
		textField4.setBorder( lineBorder );
		textField8.setBorder( lineBorder );

		applyCustomComboBoxEditorBorder( comboBox17 );
		applyCustomComboBoxEditorBorder( comboBox18 );
		applyCustomComboBoxEditorBorderWithIcon( comboBox19 );
		applyCustomComboBoxEditorBorderWithIcon( comboBox20 );
		applyCustomComboBoxEditorBorder( comboBox21, null );
		applyCustomComboBoxEditorBorder( comboBox22, null );

		applyCustomComboBoxRendererBorder( comboBox23 );
		applyCustomComboBoxRendererBorder( comboBox24 );
		applyCustomComboBoxRendererBorderWithIcon( comboBox25 );
		applyCustomComboBoxRendererBorderWithIcon( comboBox26 );
		applyCustomComboBoxRendererBorder( comboBox27, null );
		applyCustomComboBoxRendererBorder( comboBox28, null );
	}

	@SuppressWarnings( "unchecked" )
	private void applySpecialComboBoxRenderers() {
		BasicComboBoxRenderer sharedRenderer = new BasicComboBoxRenderer();
		sharedRenderer.setBorder( new LineBorder( BLUE, UIScale.scale( 2 ) ) );
		comboBox29.setRenderer( sharedRenderer );
		comboBox30.setRenderer( sharedRenderer );

		comboBox31.setRenderer( new ListCellRenderer<String>() {
			JLabel l1 = new JLabel();
			JLabel l2 = new JLabel();

			@Override
			public Component getListCellRendererComponent( JList<? extends String> list,
				String value, int index, boolean isSelected, boolean cellHasFocus )
			{
				JLabel l = (index % 2 == 0) ? l1 : l2;
				l.setText( (value != null) ? value.toString() : "" );
				l.setBorder( new LineBorder( (index % 2 == 0) ? GREEN : RED, UIScale.scale( 2 ) ) );
				l.setBackground( isSelected ? list.getSelectionBackground() : list.getBackground() );
				l.setForeground( isSelected ? list.getSelectionForeground() : list.getForeground() );
				l.setFont( list.getFont() );
				l.setOpaque( true );
				return l;
			}
		} );
	}

	private void applyCustomInsideBorder( JComponent c, String uiKey ) {
		c.setBorder( new CompoundBorder( UIManager.getBorder( uiKey ), new LineBorder( RED, UIScale.scale( 3 ) ) ) );
	}

	private void applyCustomOutsideBorder( JComponent c, String uiKey ) {
		c.setBorder( new CompoundBorder( new LineBorder( GREEN, UIScale.scale( 3 ) ), UIManager.getBorder( uiKey ) ) );
	}

	private void applyCustomComboBoxEditorBorder( JComboBox<String> comboBox ) {
		applyCustomComboBoxEditorBorder( comboBox, new LineBorder( BLUE, UIScale.scale( 6 ) ) );
	}

	private void applyCustomComboBoxEditorBorderWithIcon( JComboBox<String> comboBox ) {
		applyCustomComboBoxEditorBorder( comboBox, new BorderWithIcon() );
	}

	private void applyCustomComboBoxEditorBorder( JComboBox<String> comboBox, Border border ) {
		JTextField customTextField = new JTextField();
		if( border != null )
			customTextField.setBorder( border );
		comboBox.setEditor( new BasicComboBoxEditor() {
			@Override
			protected JTextField createEditorComponent() {
				return customTextField;
			}
		} );
	}

	private void applyCustomComboBoxRendererBorder( JComboBox<String> comboBox ) {
		applyCustomComboBoxRendererBorder( comboBox, new LineBorder( BLUE, UIScale.scale( 6 ) ) );
	}

	private void applyCustomComboBoxRendererBorderWithIcon( JComboBox<String> comboBox ) {
		applyCustomComboBoxRendererBorder( comboBox, new BorderWithIcon() );
	}

	@SuppressWarnings( "unchecked" )
	private void applyCustomComboBoxRendererBorder( JComboBox<String> comboBox, Border border ) {
		BasicComboBoxRenderer customRenderer = new BasicComboBoxRenderer();
		customRenderer.setBorder( border );
		comboBox.setRenderer( customRenderer );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label1 = new JLabel();
		label3 = new JLabel();
		label4 = new JLabel();
		label2 = new JLabel();
		label8 = new JLabel();
		label9 = new JLabel();
		label10 = new JLabel();
		label7 = new JLabel();
		button1 = new JButton();
		button2 = new JButton();
		button3 = new JButton();
		button4 = new JButton();
		button5 = new JButton();
		button6 = new JButton();
		button7 = new JButton();
		button8 = new JButton();
		label5 = new JLabel();
		comboBox1 = new JComboBox<>();
		comboBox2 = new JComboBox<>();
		comboBox3 = new JComboBox<>();
		comboBox4 = new JComboBox<>();
		comboBox23 = new JComboBox<>();
		comboBox25 = new JComboBox<>();
		comboBox27 = new JComboBox<>();
		comboBox5 = new JComboBox<>();
		comboBox6 = new JComboBox<>();
		comboBox7 = new JComboBox<>();
		comboBox8 = new JComboBox<>();
		comboBox24 = new JComboBox<>();
		comboBox26 = new JComboBox<>();
		comboBox28 = new JComboBox<>();
		comboBox9 = new JComboBox<>();
		comboBox10 = new JComboBox<>();
		comboBox11 = new JComboBox<>();
		comboBox12 = new JComboBox<>();
		comboBox17 = new JComboBox<>();
		comboBox19 = new JComboBox<>();
		comboBox21 = new JComboBox<>();
		comboBox13 = new JComboBox<>();
		comboBox14 = new JComboBox<>();
		comboBox15 = new JComboBox<>();
		comboBox16 = new JComboBox<>();
		comboBox18 = new JComboBox<>();
		comboBox20 = new JComboBox<>();
		comboBox22 = new JComboBox<>();
		label6 = new JLabel();
		spinner1 = new JSpinner();
		spinner2 = new JSpinner();
		spinner3 = new JSpinner();
		spinner4 = new JSpinner();
		spinner5 = new JSpinner();
		spinner6 = new JSpinner();
		spinner7 = new JSpinner();
		spinner8 = new JSpinner();
		textFieldLabel = new JLabel();
		textField1 = new JTextField();
		textField2 = new JTextField();
		textField3 = new JTextField();
		textField4 = new JTextField();
		textField5 = new JTextField();
		textField6 = new JTextField();
		textField7 = new JTextField();
		textField8 = new JTextField();
		label11 = new JLabel();
		label12 = new JLabel();
		comboBox29 = new JComboBox<>();
		comboBox30 = new JComboBox<>();
		comboBox31 = new JComboBox<>();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
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
			"[]"));

		//---- label1 ----
		label1.setText("plain");
		add(label1, "cell 1 0");

		//---- label3 ----
		label3.setText("custom inside");
		add(label3, "cell 2 0");

		//---- label4 ----
		label4.setText("custom outside");
		add(label4, "cell 3 0");

		//---- label2 ----
		label2.setText("LineBorder");
		add(label2, "cell 4 0");

		//---- label8 ----
		label8.setText("custom editor");
		add(label8, "cell 5 0");

		//---- label9 ----
		label9.setText("with icon");
		add(label9, "cell 6 0");

		//---- label10 ----
		label10.setText("with default border");
		add(label10, "cell 7 0");

		//---- label7 ----
		label7.setText("JButton:");
		add(label7, "cell 0 1");

		//---- button1 ----
		button1.setText("text");
		add(button1, "cell 1 1");

		//---- button2 ----
		button2.setText("text");
		add(button2, "cell 2 1");

		//---- button3 ----
		button3.setText("text");
		add(button3, "cell 3 1");

		//---- button4 ----
		button4.setText("text");
		add(button4, "cell 4 1");

		//---- button5 ----
		button5.setText("text");
		button5.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
		add(button5, "cell 1 2");

		//---- button6 ----
		button6.setText("text");
		button6.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
		add(button6, "cell 2 2");

		//---- button7 ----
		button7.setText("text");
		button7.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
		add(button7, "cell 3 2");

		//---- button8 ----
		button8.setText("text");
		button8.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
		add(button8, "cell 4 2");

		//---- label5 ----
		label5.setText("JComboBox:");
		add(label5, "cell 0 3");
		add(comboBox1, "cell 1 3");
		add(comboBox2, "cell 2 3");
		add(comboBox3, "cell 3 3");
		add(comboBox4, "cell 4 3");
		add(comboBox23, "cell 5 3");
		add(comboBox25, "cell 6 3");
		add(comboBox27, "cell 7 3");

		//---- comboBox5 ----
		comboBox5.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(comboBox5, "cell 1 4");

		//---- comboBox6 ----
		comboBox6.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(comboBox6, "cell 2 4");

		//---- comboBox7 ----
		comboBox7.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(comboBox7, "cell 3 4");

		//---- comboBox8 ----
		comboBox8.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(comboBox8, "cell 4 4");

		//---- comboBox24 ----
		comboBox24.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(comboBox24, "cell 5 4");

		//---- comboBox26 ----
		comboBox26.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(comboBox26, "cell 6 4");

		//---- comboBox28 ----
		comboBox28.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(comboBox28, "cell 7 4");

		//---- comboBox9 ----
		comboBox9.setEditable(true);
		add(comboBox9, "cell 1 5");

		//---- comboBox10 ----
		comboBox10.setEditable(true);
		add(comboBox10, "cell 2 5");

		//---- comboBox11 ----
		comboBox11.setEditable(true);
		add(comboBox11, "cell 3 5");

		//---- comboBox12 ----
		comboBox12.setEditable(true);
		add(comboBox12, "cell 4 5");

		//---- comboBox17 ----
		comboBox17.setEditable(true);
		add(comboBox17, "cell 5 5");

		//---- comboBox19 ----
		comboBox19.setEditable(true);
		add(comboBox19, "cell 6 5");

		//---- comboBox21 ----
		comboBox21.setEditable(true);
		add(comboBox21, "cell 7 5");

		//---- comboBox13 ----
		comboBox13.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		comboBox13.setEditable(true);
		add(comboBox13, "cell 1 6");

		//---- comboBox14 ----
		comboBox14.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		comboBox14.setEditable(true);
		add(comboBox14, "cell 2 6");

		//---- comboBox15 ----
		comboBox15.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		comboBox15.setEditable(true);
		add(comboBox15, "cell 3 6");

		//---- comboBox16 ----
		comboBox16.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		comboBox16.setEditable(true);
		add(comboBox16, "cell 4 6");

		//---- comboBox18 ----
		comboBox18.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		comboBox18.setEditable(true);
		add(comboBox18, "cell 5 6");

		//---- comboBox20 ----
		comboBox20.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		comboBox20.setEditable(true);
		add(comboBox20, "cell 6 6");

		//---- comboBox22 ----
		comboBox22.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		comboBox22.setEditable(true);
		add(comboBox22, "cell 7 6");

		//---- label6 ----
		label6.setText("JSpinner:");
		add(label6, "cell 0 7");
		add(spinner1, "cell 1 7");
		add(spinner2, "cell 2 7");
		add(spinner3, "cell 3 7");
		add(spinner4, "cell 4 7");

		//---- spinner5 ----
		spinner5.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(spinner5, "cell 1 8");

		//---- spinner6 ----
		spinner6.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(spinner6, "cell 2 8");

		//---- spinner7 ----
		spinner7.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(spinner7, "cell 3 8");

		//---- spinner8 ----
		spinner8.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(spinner8, "cell 4 8");

		//---- textFieldLabel ----
		textFieldLabel.setText("JTextField:");
		add(textFieldLabel, "cell 0 9");

		//---- textField1 ----
		textField1.setText("text");
		textField1.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, false);
		add(textField1, "cell 1 9,growx");

		//---- textField2 ----
		textField2.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, false);
		textField2.setText("text");
		add(textField2, "cell 2 9");

		//---- textField3 ----
		textField3.setText("text");
		add(textField3, "cell 3 9");

		//---- textField4 ----
		textField4.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, false);
		textField4.setText("text");
		add(textField4, "cell 4 9");

		//---- textField5 ----
		textField5.setText("text");
		textField5.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(textField5, "cell 1 10,growx");

		//---- textField6 ----
		textField6.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		textField6.setText("text");
		add(textField6, "cell 2 10");

		//---- textField7 ----
		textField7.setText("text");
		textField7.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		add(textField7, "cell 3 10");

		//---- textField8 ----
		textField8.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
		textField8.setText("text");
		add(textField8, "cell 4 10");

		//---- label11 ----
		label11.setText("JComboBox with shared renderer:");
		add(label11, "cell 1 11 2 1");

		//---- label12 ----
		label12.setText("JComboBox with renderer that uses varying components:");
		add(label12, "cell 3 11 3 1");
		add(comboBox29, "cell 1 12");
		add(comboBox30, "cell 2 12");
		add(comboBox31, "cell 3 12");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private JLabel label3;
	private JLabel label4;
	private JLabel label2;
	private JLabel label8;
	private JLabel label9;
	private JLabel label10;
	private JLabel label7;
	private JButton button1;
	private JButton button2;
	private JButton button3;
	private JButton button4;
	private JButton button5;
	private JButton button6;
	private JButton button7;
	private JButton button8;
	private JLabel label5;
	private JComboBox<String> comboBox1;
	private JComboBox<String> comboBox2;
	private JComboBox<String> comboBox3;
	private JComboBox<String> comboBox4;
	private JComboBox<String> comboBox23;
	private JComboBox<String> comboBox25;
	private JComboBox<String> comboBox27;
	private JComboBox<String> comboBox5;
	private JComboBox<String> comboBox6;
	private JComboBox<String> comboBox7;
	private JComboBox<String> comboBox8;
	private JComboBox<String> comboBox24;
	private JComboBox<String> comboBox26;
	private JComboBox<String> comboBox28;
	private JComboBox<String> comboBox9;
	private JComboBox<String> comboBox10;
	private JComboBox<String> comboBox11;
	private JComboBox<String> comboBox12;
	private JComboBox<String> comboBox17;
	private JComboBox<String> comboBox19;
	private JComboBox<String> comboBox21;
	private JComboBox<String> comboBox13;
	private JComboBox<String> comboBox14;
	private JComboBox<String> comboBox15;
	private JComboBox<String> comboBox16;
	private JComboBox<String> comboBox18;
	private JComboBox<String> comboBox20;
	private JComboBox<String> comboBox22;
	private JLabel label6;
	private JSpinner spinner1;
	private JSpinner spinner2;
	private JSpinner spinner3;
	private JSpinner spinner4;
	private JSpinner spinner5;
	private JSpinner spinner6;
	private JSpinner spinner7;
	private JSpinner spinner8;
	private JLabel textFieldLabel;
	private JTextField textField1;
	private JTextField textField2;
	private JTextField textField3;
	private JTextField textField4;
	private JTextField textField5;
	private JTextField textField6;
	private JTextField textField7;
	private JTextField textField8;
	private JLabel label11;
	private JLabel label12;
	private JComboBox<String> comboBox29;
	private JComboBox<String> comboBox30;
	private JComboBox<String> comboBox31;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class BorderWithIcon -----------------------------------------------

	private static class BorderWithIcon
		implements Border
	{
		private final FlatFileViewFloppyDriveIcon icon = new FlatFileViewFloppyDriveIcon();

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			icon.paintIcon( c, g, x + width - icon.getIconWidth() - 2, y + ((height - icon.getIconHeight()) / 2) );

			g.setColor( RED );
			g.drawRect( x, y, width - 1, height - 1 );
		}

		@Override
		public boolean isBorderOpaque() {
			return false;
		}

		@Override
		public Insets getBorderInsets( Component c ) {
			return new Insets( 0, 0, 0, icon.getIconWidth() + 4 );
		}
	}
}
