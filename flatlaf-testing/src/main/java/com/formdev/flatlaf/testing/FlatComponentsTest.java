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
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;
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

	private final JSlider[] allSliders;
	private final JSlider[] roundSliders;
	private final JSlider[] directionalSliders;

	FlatComponentsTest() {
		initComponents();

		buttonTypeComboBox.init( ButtonType.class, true );

		allSliders = new JSlider[] { slider1, slider2, slider3, slider4, slider5, slider6 };
		roundSliders = new JSlider[] { slider1, slider2, slider6 };
		directionalSliders = new JSlider[] { slider3, slider4, slider5 };

		ChangeListener sliderChanged = e -> {
			JSlider slider = (JSlider) e.getSource();
			String text = slider.getValue() + "  " + slider.getValueIsAdjusting();
			sliderValueLabel.setText( text );
//			System.out.println( text );
		};
		for( JSlider slider : allSliders )
			slider.addChangeListener( sliderChanged );

		UIManager.addPropertyChangeListener( e -> {
			if( "lookAndFeel".equals( e.getPropertyName() ) && hideArrowButtonCheckBox.isSelected() )
				SwingUtilities.invokeLater( () -> hideArrowButton() );
		} );
	}

	private void changeProgress() {
		int value = slider3.getValue();
		progressBar1.setValue( value );
		progressBar2.setValue( value );
		progressBar3.setValue( value );
		progressBar4.setValue( value );
	}

	private void indeterminateProgress() {
		boolean indeterminate = indeterminateCheckBox.isSelected();
		progressBar1.setIndeterminate( indeterminate );
		progressBar2.setIndeterminate( indeterminate );
		progressBar3.setIndeterminate( indeterminate );
		progressBar4.setIndeterminate( indeterminate );
	}

	private void squareChanged() {
		boolean square = squareCheckBox.isSelected();
		progressBar1.setSquare( square );
		progressBar2.setSquare( square );
		progressBar3.setSquare( square );
		progressBar4.setSquare( square );
	}

	private void largeHeightChanged() {
		boolean largeHeight = largeHeightCheckBox.isSelected();
		progressBar1.setLargeHeight( largeHeight );
		progressBar2.setLargeHeight( largeHeight );
		progressBar3.setLargeHeight( largeHeight );
		progressBar4.setLargeHeight( largeHeight );
	}

	private void borderPaintedChanged() {
		boolean borderPainted = borderPaintedCheckBox.isSelected();

		for( Component c : getComponents() ) {
			if( c instanceof AbstractButton )
				((AbstractButton)c).setBorderPainted( borderPainted );
			else if( c instanceof JProgressBar )
				((JProgressBar)c).setBorderPainted( borderPainted );
			else if( c instanceof JToolBar )
				((JToolBar)c).setBorderPainted( borderPainted );

			if( c instanceof JCheckBox )
				((JCheckBox)c).setBorderPaintedFlat( borderPainted );
		}
	}

	private void contentAreaFilledChanged() {
		boolean contentAreaFilled = contentAreaFilledCheckBox.isSelected();

		for( Component c : getComponents() ) {
			if( c instanceof AbstractButton )
				((AbstractButton)c).setContentAreaFilled( contentAreaFilled );
		}
	}

	private void focusPaintedChanged() {
		boolean focusPainted = focusPaintedCheckBox.isSelected();

		for( Component c : getComponents() ) {
			if( c instanceof AbstractButton )
				((AbstractButton)c).setFocusPainted( focusPainted );
		}
	}

	private void hideArrowButton() {
		boolean hideArrowButton = hideArrowButtonCheckBox.isSelected();

		for( Component c : getComponents() ) {
			if( c instanceof JComboBox ) {
				Component b = ((JComboBox<?>)c).getComponent( 0 );
				if( b instanceof AbstractButton )
					b.setVisible( !hideArrowButton );
			}
		}
	}

	private void transparentBackground() {
		EventQueue.invokeLater( () -> {
			boolean transparent = transparentBackgroundCheckBox.isSelected();
			Color transparentColor = new Color( 0, true );
			ColorUIResource restoreColor = new ColorUIResource( Color.white );

			FlatTestFrame.updateComponentsRecur( this, (c, type) -> {
				if( c instanceof JComboBox ) {
					((JComboBox<?>)c).putClientProperty( FlatClientProperties.STYLE,
						transparent ? "background: #0000; buttonBackground: #0000; buttonEditableBackground: #0000" : null );
				} else if( c instanceof JSpinner ) {
					((JSpinner)c).putClientProperty( FlatClientProperties.STYLE,
						transparent ? "background: #0000; buttonBackground: #0000" : null );
				} else if( c instanceof JComponent )
					c.setBackground( transparent ? transparentColor : restoreColor );
				else
					return;

				// if background color is transparent it is also required to make the component non-opaque
				// DO NOT USE LookAndFeel.installProperty() in real-world applications
				//            instead use c.setOpaque( false )
				if( transparent )
					LookAndFeel.installProperty( (JComponent) c, "opaque", false );
			} );

			if( !transparent )
				SwingUtilities.updateComponentTreeUI( this );
		} );
	}

	private void roundRectChanged() {
		Boolean roundRect = roundRectCheckBox.isSelected() ? true : null;

		for( Component c : getComponents() ) {
			if( c instanceof JComponent )
				((JComponent)c).putClientProperty( FlatClientProperties.COMPONENT_ROUND_RECT, roundRect );
		}
	}

	private void buttonTypeChanged() {
		ButtonType buttonType = buttonTypeComboBox.getSelectedValue();
		String buttonTypeStr = (buttonType != null && buttonType != ButtonType.none) ? buttonType.toString() : null;

		for( Component c : getComponents() ) {
			if( c instanceof FlatButton )
				((FlatButton)c).setButtonType( buttonType );
			else if( c instanceof FlatToggleButton )
				((FlatToggleButton)c).setButtonType( buttonType );
			else if( c instanceof AbstractButton )
				((AbstractButton)c).putClientProperty( FlatClientProperties.BUTTON_TYPE, buttonTypeStr );
		}
	}

	private void outlineChanged() {
		Object outline = errorOutlineRadioButton.isSelected() ? "error"
			: warningOutlineRadioButton.isSelected() ? "warning"
			: magentaOutlineRadioButton.isSelected() ? Color.magenta
			: magentaCyanOutlineRadioButton.isSelected() ? new Color[] { Color.magenta, Color.cyan }
			: null;

		FlatTestFrame.updateComponentsRecur( this, (c, type) -> {
			if( c instanceof JComponent )
				((JComponent)c).putClientProperty( FlatClientProperties.OUTLINE, outline );
		} );

		textField1.requestFocusInWindow();
	}

	private void sliderPaintTrackChanged() {
		boolean paintTrack = sliderPaintTrackCheckBox.isSelected();
		for( JSlider slider : allSliders )
			slider.setPaintTrack( paintTrack );
	}

	private void sliderPaintTicksChanged() {
		Boolean paintTicks = sliderPaintTicksCheckBox.getChecked();
		if( paintTicks != null ) {
			for( JSlider slider : allSliders )
				slider.setPaintTicks( paintTicks );
		} else {
			for( JSlider slider : roundSliders )
				slider.setPaintTicks( false );
			for( JSlider slider : directionalSliders )
				slider.setPaintTicks( true );
		}
	}

	private void sliderPaintLabelsChanged() {
		Boolean paintLabels = sliderPaintLabelsCheckBox.getChecked();
		if( paintLabels != null ) {
			for( JSlider slider : allSliders )
				slider.setPaintLabels( paintLabels );
		} else {
			for( JSlider slider : roundSliders )
				slider.setPaintLabels( false );
			for( JSlider slider : directionalSliders )
				slider.setPaintLabels( true );
		}
	}

	private void sliderInvertedChanged() {
		boolean inverted = sliderInvertedCheckBox.isSelected();
		for( JSlider slider : allSliders )
			slider.setInverted( inverted );
	}

	private void sliderSnapToTicksChanged() {
		boolean snapToTicks = sliderSnapToTicksCheckBox.isSelected();
		for( JSlider slider : allSliders )
			slider.setSnapToTicks( snapToTicks );
	}

	private void sliderBorderChanged() {
		boolean border = sliderBorderCheckBox.isSelected();
		UIManager.put( "Slider.thumbBorderColor", border ? Color.green : null );
		for( JSlider slider : allSliders )
			slider.updateUI();
	}

	private void majorThickSpacingChanged() {
		int majorTickSpacing = (Integer) majorTickSpacingSpinner.getValue();
		for( JSlider slider : directionalSliders ) {
			slider.setLabelTable( null );
			slider.setMajorTickSpacing( majorTickSpacing );
		}
	}

	private void minorThickSpacingChanged() {
		int minorTickSpacing = (Integer) minorTickSpacingSpinner.getValue();
		for( JSlider slider : directionalSliders )
			slider.setMinorTickSpacing( minorTickSpacing );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel labelLabel = new JLabel();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		FlatComponentsTest.TestMultiLineLabel testMultiLineLabel1 = new FlatComponentsTest.TestMultiLineLabel();
		JLabel buttonLabel = new JLabel();
		JButton button1 = new JButton();
		FlatButton button17 = new FlatButton();
		FlatButton button22 = new FlatButton();
		JButton button2 = new JButton();
		FlatButton button18 = new FlatButton();
		FlatButton button23 = new FlatButton();
		FlatComponentsTest.TestDefaultButton button5 = new FlatComponentsTest.TestDefaultButton();
		FlatButton button3 = new FlatButton();
		FlatButton button12 = new FlatButton();
		JButton button13 = new JButton();
		JButton button14 = new JButton();
		JButton button15 = new JButton();
		JButton button16 = new JButton();
		JButton button24 = new JButton();
		JButton button20 = new JButton();
		FlatButton button25 = new FlatButton();
		JLabel toggleButtonLabel = new JLabel();
		JToggleButton toggleButton1 = new JToggleButton();
		FlatToggleButton toggleButton9 = new FlatToggleButton();
		FlatToggleButton toggleButton19 = new FlatToggleButton();
		JToggleButton toggleButton2 = new JToggleButton();
		FlatToggleButton toggleButton10 = new FlatToggleButton();
		FlatToggleButton toggleButton20 = new FlatToggleButton();
		JToggleButton toggleButton3 = new JToggleButton();
		JToggleButton toggleButton4 = new JToggleButton();
		JToggleButton toggleButton11 = new JToggleButton();
		JToggleButton toggleButton12 = new JToggleButton();
		JToggleButton toggleButton13 = new JToggleButton();
		JToggleButton toggleButton14 = new JToggleButton();
		JToggleButton toggleButton21 = new JToggleButton();
		JToggleButton toggleButton18 = new JToggleButton();
		FlatToggleButton toggleButton22 = new FlatToggleButton();
		JLabel checkBoxLabel = new JLabel();
		JCheckBox checkBox1 = new JCheckBox();
		JCheckBox checkBox2 = new JCheckBox();
		JCheckBox checkBox3 = new JCheckBox();
		JCheckBox checkBox4 = new JCheckBox();
		FlatToggleButton toggleButton5 = new FlatToggleButton();
		FlatToggleButton toggleButton8 = new FlatToggleButton();
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
		JComboBox<String> comboBox6 = new JComboBox<>();
		JLabel spinnerLabel = new JLabel();
		JSpinner spinner1 = new JSpinner();
		JSpinner spinner2 = new JSpinner();
		FlatComponentsTest.ButtonlessSpinner buttonlessSpinner1 = new FlatComponentsTest.ButtonlessSpinner();
		FlatComponentsTest.ButtonlessSpinner buttonlessSpinner2 = new FlatComponentsTest.ButtonlessSpinner();
		FlatComboBox<String> comboBox7 = new FlatComboBox<>();
		JSpinner spinner3 = new JSpinner();
		JLabel textFieldLabel = new JLabel();
		textField1 = new JTextField();
		JTextField textField2 = new JTextField();
		JTextField textField3 = new JTextField();
		JTextField textField4 = new JTextField();
		FlatTextField textField6 = new FlatTextField();
		JTextField textField5 = new JTextField();
		JLabel formattedTextFieldLabel = new JLabel();
		JFormattedTextField formattedTextField1 = new JFormattedTextField();
		JFormattedTextField formattedTextField2 = new JFormattedTextField();
		JFormattedTextField formattedTextField3 = new JFormattedTextField();
		JFormattedTextField formattedTextField4 = new JFormattedTextField();
		FlatFormattedTextField formattedTextField5 = new FlatFormattedTextField();
		JFormattedTextField formattedTextField6 = new JFormattedTextField();
		JLabel passwordFieldLabel = new JLabel();
		JPasswordField passwordField1 = new JPasswordField();
		JPasswordField passwordField2 = new JPasswordField();
		JPasswordField passwordField3 = new JPasswordField();
		JPasswordField passwordField4 = new JPasswordField();
		FlatPasswordField passwordField5 = new FlatPasswordField();
		JPasswordField passwordField6 = new JPasswordField();
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
		JButton button19 = new JButton();
		JScrollBar scrollBar2 = new JScrollBar();
		JScrollBar scrollBar3 = new JScrollBar();
		FlatScrollBar scrollBar7 = new FlatScrollBar();
		FlatScrollBar scrollBar8 = new FlatScrollBar();
		JSeparator separator2 = new JSeparator();
		slider2 = new JSlider();
		slider4 = new JSlider();
		JScrollPane scrollPane14 = new JScrollPane();
		progressBar3 = new FlatProgressBar();
		progressBar4 = new FlatProgressBar();
		FlatComponentsTest.TestToolBar toolBar2 = new FlatComponentsTest.TestToolBar();
		JButton button9 = new JButton();
		JButton button10 = new JButton();
		JButton button11 = new JButton();
		JToggleButton toggleButton7 = new JToggleButton();
		JScrollPane scrollPane15 = new JScrollPane();
		JPanel panel3 = new JPanel();
		JButton button21 = new JButton();
		JPanel panel5 = new JPanel();
		buttonTypeComboBox = new FlatTestEnumComboBox<>();
		borderPaintedCheckBox = new JCheckBox();
		roundRectCheckBox = new JCheckBox();
		contentAreaFilledCheckBox = new JCheckBox();
		JPanel panel4 = new JPanel();
		noOutlineRadioButton = new JRadioButton();
		errorOutlineRadioButton = new JRadioButton();
		warningOutlineRadioButton = new JRadioButton();
		magentaOutlineRadioButton = new JRadioButton();
		magentaCyanOutlineRadioButton = new JRadioButton();
		focusPaintedCheckBox = new JCheckBox();
		hideArrowButtonCheckBox = new JCheckBox();
		transparentBackgroundCheckBox = new JCheckBox();
		JLabel scrollBarLabel = new JLabel();
		JScrollBar scrollBar1 = new JScrollBar();
		JScrollBar scrollBar4 = new JScrollBar();
		FlatScrollBar scrollBar5 = new FlatScrollBar();
		FlatScrollBar scrollBar6 = new FlatScrollBar();
		JLabel separatorLabel = new JLabel();
		JSeparator separator1 = new JSeparator();
		JPanel panel2 = new JPanel();
		JLabel sliderLabel = new JLabel();
		slider1 = new JSlider();
		slider6 = new JSlider();
		JPanel panel8 = new JPanel();
		JPanel panel6 = new JPanel();
		sliderPaintTrackCheckBox = new JCheckBox();
		sliderPaintTicksCheckBox = new FlatTriStateCheckBox();
		sliderPaintLabelsCheckBox = new FlatTriStateCheckBox();
		sliderInvertedCheckBox = new JCheckBox();
		sliderSnapToTicksCheckBox = new JCheckBox();
		majorTickSpacingSpinner = new JSpinner();
		sliderBorderCheckBox = new JCheckBox();
		minorTickSpacingSpinner = new JSpinner();
		sliderValueLabel = new JLabel();
		JPanel panel7 = new JPanel();
		indeterminateCheckBox = new JCheckBox();
		squareCheckBox = new JCheckBox();
		largeHeightCheckBox = new JCheckBox();
		JLabel sliderLabel2 = new JLabel();
		slider3 = new JSlider();
		slider5 = new JSlider();
		JLabel progressBarLabel = new JLabel();
		progressBar1 = new FlatProgressBar();
		progressBar2 = new FlatProgressBar();
		JLabel toolTipLabel = new JLabel();
		JToolTip toolTip1 = new JToolTip();
		JToolTip toolTip2 = new JToolTip();
		JLabel toolBarLabel = new JLabel();
		FlatComponentsTest.TestToolBar toolBar1 = new FlatComponentsTest.TestToolBar();
		JButton button4 = new JButton();
		JButton button6 = new JButton();
		JButton button7 = new JButton();
		JButton button8 = new JButton();
		JToggleButton toggleButton6 = new JToggleButton();
		JToggleButton toggleButton15 = new JToggleButton();
		JToggleButton toggleButton16 = new JToggleButton();
		JToggleButton toggleButton17 = new JToggleButton();
		JLabel label3 = new JLabel();
		FlatComponentsTest.TestToolBar toolBar3 = new FlatComponentsTest.TestToolBar();
		FlatButton button26 = new FlatButton();
		FlatButton button27 = new FlatButton();
		FlatToggleButton toggleButton23 = new FlatToggleButton();
		FlatToggleButton toggleButton24 = new FlatToggleButton();
		JLabel label4 = new JLabel();
		FlatComponentsTest.TestToolBar toolBar4 = new FlatComponentsTest.TestToolBar();
		FlatButton button28 = new FlatButton();
		FlatButton button29 = new FlatButton();
		FlatToggleButton toggleButton25 = new FlatToggleButton();
		FlatToggleButton toggleButton26 = new FlatToggleButton();
		JLabel label5 = new JLabel();
		FlatComponentsTest.TestToolBar toolBar5 = new FlatComponentsTest.TestToolBar();
		FlatToggleButton toggleButton27 = new FlatToggleButton();
		FlatToggleButton toggleButton28 = new FlatToggleButton();
		FlatToggleButton toggleButton29 = new FlatToggleButton();
		FlatToggleButton toggleButton30 = new FlatToggleButton();
		FlatToggleButton toggleButton31 = new FlatToggleButton();
		FlatToggleButton toggleButton32 = new FlatToggleButton();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[left]",
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
			"[]" +
			"[]" +
			"[]"));

		//---- labelLabel ----
		labelLabel.setText("JLabel:");
		add(labelLabel, "cell 0 0");

		//---- label1 ----
		label1.setText("Enabled");
		label1.setDisplayedMnemonic('E');
		add(label1, "cell 1 0");

		//---- label2 ----
		label2.setText("Disabled");
		label2.setDisplayedMnemonic('D');
		label2.setEnabled(false);
		add(label2, "cell 2 0");

		//---- testMultiLineLabel1 ----
		testMultiLineLabel1.setText("Multi-line label based on JTextArea\n2nd line");
		add(testMultiLineLabel1, "cell 3 0 2 1");

		//---- buttonLabel ----
		buttonLabel.setText("JButton:");
		add(buttonLabel, "cell 0 1");

		//---- button1 ----
		button1.setText("Enabled");
		button1.setDisplayedMnemonicIndex(0);
		button1.setToolTipText("This button is enabled.");
		add(button1, "cell 1 1");

		//---- button17 ----
		button17.setText("Sq");
		button17.setButtonType(FlatButton.ButtonType.square);
		button17.setMinimumWidth(0);
		add(button17, "cell 1 1");

		//---- button22 ----
		button22.setText("Rd");
		button22.setButtonType(FlatButton.ButtonType.roundRect);
		button22.setMinimumWidth(0);
		add(button22, "cell 1 1");

		//---- button2 ----
		button2.setText("Disabled");
		button2.setDisplayedMnemonicIndex(0);
		button2.setEnabled(false);
		button2.setToolTipText("This button is disabled.");
		add(button2, "cell 2 1");

		//---- button18 ----
		button18.setText("Sq");
		button18.setButtonType(FlatButton.ButtonType.square);
		button18.setEnabled(false);
		button18.setMinimumWidth(0);
		add(button18, "cell 2 1");

		//---- button23 ----
		button23.setText("Rd");
		button23.setButtonType(FlatButton.ButtonType.roundRect);
		button23.setEnabled(false);
		button23.setMinimumWidth(0);
		add(button23, "cell 2 1");

		//---- button5 ----
		button5.setText("Default");
		button5.setDisplayedMnemonicIndex(0);
		button5.setToolTipText("Tool tip with\nmultiple\nlines.");
		add(button5, "cell 3 1");

		//---- button3 ----
		button3.setText("Help");
		button3.setButtonType(FlatButton.ButtonType.help);
		add(button3, "cell 4 1");

		//---- button12 ----
		button12.setText("Help");
		button12.setButtonType(FlatButton.ButtonType.help);
		button12.setEnabled(false);
		add(button12, "cell 4 1");

		//---- button13 ----
		button13.setIcon(UIManager.getIcon("Tree.closedIcon"));
		add(button13, "cell 5 1 2 1");

		//---- button14 ----
		button14.setText("...");
		add(button14, "cell 5 1 2 1");

		//---- button15 ----
		button15.setText("\u2026");
		add(button15, "cell 5 1 2 1");

		//---- button16 ----
		button16.setText("#");
		add(button16, "cell 5 1 2 1");

		//---- button24 ----
		button24.setText("A");
		add(button24, "cell 5 1 2 1");

		//---- button20 ----
		button20.setText("Empty border");
		button20.setBorder(BorderFactory.createEmptyBorder());
		add(button20, "cell 5 1 2 1");

		//---- button25 ----
		button25.setIcon(UIManager.getIcon("Tree.closedIcon"));
		button25.setButtonType(FlatButton.ButtonType.borderless);
		add(button25, "cell 5 1 2 1");

		//---- toggleButtonLabel ----
		toggleButtonLabel.setText("JToggleButton:");
		add(toggleButtonLabel, "cell 0 2");

		//---- toggleButton1 ----
		toggleButton1.setText("Enabled");
		toggleButton1.setToolTipText("LOOOOOOOOOOOOOONG TEXT");
		add(toggleButton1, "cell 1 2");

		//---- toggleButton9 ----
		toggleButton9.setText("Sq");
		toggleButton9.setButtonType(FlatButton.ButtonType.square);
		add(toggleButton9, "cell 1 2");

		//---- toggleButton19 ----
		toggleButton19.setText("Rd");
		toggleButton19.setButtonType(FlatButton.ButtonType.roundRect);
		add(toggleButton19, "cell 1 2");

		//---- toggleButton2 ----
		toggleButton2.setText("Disabled");
		toggleButton2.setEnabled(false);
		add(toggleButton2, "cell 2 2");

		//---- toggleButton10 ----
		toggleButton10.setText("Sq");
		toggleButton10.setButtonType(FlatButton.ButtonType.square);
		toggleButton10.setEnabled(false);
		add(toggleButton10, "cell 2 2");

		//---- toggleButton20 ----
		toggleButton20.setText("Rd");
		toggleButton20.setButtonType(FlatButton.ButtonType.roundRect);
		toggleButton20.setEnabled(false);
		add(toggleButton20, "cell 2 2");

		//---- toggleButton3 ----
		toggleButton3.setText("Selected");
		toggleButton3.setSelected(true);
		add(toggleButton3, "cell 3 2");

		//---- toggleButton4 ----
		toggleButton4.setText("Selected disabled");
		toggleButton4.setEnabled(false);
		toggleButton4.setSelected(true);
		add(toggleButton4, "cell 4 2");

		//---- toggleButton11 ----
		toggleButton11.setIcon(UIManager.getIcon("Tree.closedIcon"));
		toggleButton11.setSelected(true);
		add(toggleButton11, "cell 5 2 2 1");

		//---- toggleButton12 ----
		toggleButton12.setText("...");
		toggleButton12.setSelected(true);
		add(toggleButton12, "cell 5 2 2 1");

		//---- toggleButton13 ----
		toggleButton13.setText("\u2026");
		toggleButton13.setSelected(true);
		add(toggleButton13, "cell 5 2 2 1");

		//---- toggleButton14 ----
		toggleButton14.setText("#");
		toggleButton14.setSelected(true);
		add(toggleButton14, "cell 5 2 2 1");

		//---- toggleButton21 ----
		toggleButton21.setText("A");
		toggleButton21.setSelected(true);
		add(toggleButton21, "cell 5 2 2 1");

		//---- toggleButton18 ----
		toggleButton18.setText("Empty border");
		toggleButton18.setBorder(BorderFactory.createEmptyBorder());
		add(toggleButton18, "cell 5 2 2 1");

		//---- toggleButton22 ----
		toggleButton22.setIcon(UIManager.getIcon("Tree.closedIcon"));
		toggleButton22.setSelected(true);
		toggleButton22.setButtonType(FlatButton.ButtonType.borderless);
		add(toggleButton22, "cell 5 2 2 1");

		//---- checkBoxLabel ----
		checkBoxLabel.setText("JCheckBox");
		add(checkBoxLabel, "cell 0 3");

		//---- checkBox1 ----
		checkBox1.setText("Enabled");
		checkBox1.setMnemonic('A');
		add(checkBox1, "cell 1 3");

		//---- checkBox2 ----
		checkBox2.setText("Disabled");
		checkBox2.setEnabled(false);
		checkBox2.setMnemonic('D');
		add(checkBox2, "cell 2 3");

		//---- checkBox3 ----
		checkBox3.setText("Selected");
		checkBox3.setSelected(true);
		add(checkBox3, "cell 3 3");

		//---- checkBox4 ----
		checkBox4.setText("Selected disabled");
		checkBox4.setSelected(true);
		checkBox4.setEnabled(false);
		add(checkBox4, "cell 4 3");

		//---- toggleButton5 ----
		toggleButton5.setText("Tab");
		toggleButton5.setButtonType(FlatButton.ButtonType.tab);
		toggleButton5.setSelected(true);
		add(toggleButton5, "cell 5 3");

		//---- toggleButton8 ----
		toggleButton8.setText("Tab");
		toggleButton8.setButtonType(FlatButton.ButtonType.tab);
		toggleButton8.setEnabled(false);
		toggleButton8.setSelected(true);
		add(toggleButton8, "cell 5 3");

		//---- radioButtonLabel ----
		radioButtonLabel.setText("JRadioButton:");
		add(radioButtonLabel, "cell 0 4");

		//---- radioButton1 ----
		radioButton1.setText("Enabled");
		radioButton1.setMnemonic('N');
		add(radioButton1, "cell 1 4");

		//---- radioButton2 ----
		radioButton2.setText("Disabled");
		radioButton2.setEnabled(false);
		radioButton2.setMnemonic('S');
		add(radioButton2, "cell 2 4");

		//---- radioButton3 ----
		radioButton3.setText("Selected");
		radioButton3.setSelected(true);
		add(radioButton3, "cell 3 4");

		//---- radioButton4 ----
		radioButton4.setText("Selected disabled");
		radioButton4.setSelected(true);
		radioButton4.setEnabled(false);
		add(radioButton4, "cell 4 4");

		//---- comboBoxLabel ----
		comboBoxLabel.setText("JComboBox:");
		add(comboBoxLabel, "cell 0 5");

		//---- comboBox1 ----
		comboBox1.setEditable(true);
		comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
			"Editable",
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
		comboBox1.setMaximumRowCount(6);
		add(comboBox1, "cell 1 5,growx");

		//---- comboBox2 ----
		comboBox2.setEditable(true);
		comboBox2.setEnabled(false);
		comboBox2.setModel(new DefaultComboBoxModel<>(new String[] {
			"Disabled",
			"a",
			"bb",
			"ccc"
		}));
		add(comboBox2, "cell 2 5,growx");

		//---- comboBox3 ----
		comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {
			"Not editable",
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
		comboBox3.setMaximumRowCount(6);
		add(comboBox3, "cell 3 5,growx");

		//---- comboBox4 ----
		comboBox4.setModel(new DefaultComboBoxModel<>(new String[] {
			"Not editable disabled",
			"a",
			"bb",
			"ccc"
		}));
		comboBox4.setEnabled(false);
		add(comboBox4, "cell 4 5,growx");

		//---- comboBox5 ----
		comboBox5.setModel(new DefaultComboBoxModel<>(new String[] {
			"Wide popup if text is longer",
			"aa",
			"bbb",
			"cccc"
		}));
		add(comboBox5, "cell 5 5,growx,wmax 100");

		//---- comboBox6 ----
		comboBox6.setBorder(BorderFactory.createEmptyBorder());
		comboBox6.setModel(new DefaultComboBoxModel<>(new String[] {
			"Empty border",
			"a",
			"b",
			"c"
		}));
		add(comboBox6, "cell 6 5");

		//---- spinnerLabel ----
		spinnerLabel.setText("JSpinner:");
		add(spinnerLabel, "cell 0 6");
		add(spinner1, "cell 1 6,growx");

		//---- spinner2 ----
		spinner2.setEnabled(false);
		add(spinner2, "cell 2 6,growx");
		add(buttonlessSpinner1, "cell 3 6,growx");
		add(buttonlessSpinner2, "cell 4 6,growx");

		//---- comboBox7 ----
		comboBox7.setEditable(true);
		comboBox7.setPlaceholderText("Placeholder");
		add(comboBox7, "cell 5 6,growx");

		//---- spinner3 ----
		spinner3.setBorder(BorderFactory.createEmptyBorder());
		spinner3.setModel(new SpinnerNumberModel(12345, null, null, 1));
		add(spinner3, "cell 6 6");

		//---- textFieldLabel ----
		textFieldLabel.setText("JTextField:");
		add(textFieldLabel, "cell 0 7");

		//---- textField1 ----
		textField1.setText("Editable");
		add(textField1, "cell 1 7,growx");

		//---- textField2 ----
		textField2.setText("Disabled");
		textField2.setEnabled(false);
		add(textField2, "cell 2 7,growx");

		//---- textField3 ----
		textField3.setText("Not editable");
		textField3.setEditable(false);
		add(textField3, "cell 3 7,growx");

		//---- textField4 ----
		textField4.setText("Not editable disabled");
		textField4.setEnabled(false);
		textField4.setEditable(false);
		add(textField4, "cell 4 7,growx");

		//---- textField6 ----
		textField6.setPlaceholderText("Placeholder");
		add(textField6, "cell 5 7,growx");

		//---- textField5 ----
		textField5.setText("Empty border");
		textField5.setBorder(BorderFactory.createEmptyBorder());
		add(textField5, "cell 6 7");

		//---- formattedTextFieldLabel ----
		formattedTextFieldLabel.setText("JFormattedTextField:");
		add(formattedTextFieldLabel, "cell 0 8");

		//---- formattedTextField1 ----
		formattedTextField1.setText("Editable");
		add(formattedTextField1, "cell 1 8,growx");

		//---- formattedTextField2 ----
		formattedTextField2.setText("Disabled");
		formattedTextField2.setEnabled(false);
		add(formattedTextField2, "cell 2 8,growx");

		//---- formattedTextField3 ----
		formattedTextField3.setText("Not editable");
		formattedTextField3.setEditable(false);
		add(formattedTextField3, "cell 3 8,growx");

		//---- formattedTextField4 ----
		formattedTextField4.setText("Not editable disabled");
		formattedTextField4.setEnabled(false);
		formattedTextField4.setEditable(false);
		add(formattedTextField4, "cell 4 8,growx");

		//---- formattedTextField5 ----
		formattedTextField5.setPlaceholderText("Placeholder");
		add(formattedTextField5, "cell 5 8,growx");

		//---- formattedTextField6 ----
		formattedTextField6.setText("Empty border");
		formattedTextField6.setBorder(BorderFactory.createEmptyBorder());
		add(formattedTextField6, "cell 6 8");

		//---- passwordFieldLabel ----
		passwordFieldLabel.setText("JPasswordField:");
		add(passwordFieldLabel, "cell 0 9");

		//---- passwordField1 ----
		passwordField1.setText("Editable");
		add(passwordField1, "cell 1 9,growx");

		//---- passwordField2 ----
		passwordField2.setText("Disabled");
		passwordField2.setEnabled(false);
		add(passwordField2, "cell 2 9,growx");

		//---- passwordField3 ----
		passwordField3.setText("Not editable");
		passwordField3.setEditable(false);
		add(passwordField3, "cell 3 9,growx");

		//---- passwordField4 ----
		passwordField4.setText("Not editable disabled");
		passwordField4.setEnabled(false);
		passwordField4.setEditable(false);
		add(passwordField4, "cell 4 9,growx");

		//---- passwordField5 ----
		passwordField5.setPlaceholderText("Placeholder");
		add(passwordField5, "cell 5 9,growx");

		//---- passwordField6 ----
		passwordField6.setText("empty border");
		passwordField6.setBorder(BorderFactory.createEmptyBorder());
		add(passwordField6, "cell 6 9");

		//---- textAreaLabel ----
		textAreaLabel.setText("JTextArea:");
		add(textAreaLabel, "cell 0 10");

		//======== scrollPane1 ========
		{
			scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea1 ----
			textArea1.setText("Editable");
			textArea1.setRows(2);
			scrollPane1.setViewportView(textArea1);
		}
		add(scrollPane1, "cell 1 10,growx");

		//======== scrollPane2 ========
		{
			scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea2 ----
			textArea2.setText("Disabled");
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
			textArea3.setText("Not editable");
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
			textArea4.setText("Not editable disabled");
			textArea4.setRows(2);
			textArea4.setEditable(false);
			textArea4.setEnabled(false);
			scrollPane4.setViewportView(textArea4);
		}
		add(scrollPane4, "cell 4 10,growx");

		//---- textArea5 ----
		textArea5.setRows(2);
		textArea5.setText("No scroll pane");
		add(textArea5, "cell 5 10,growx");

		//---- editorPaneLabel ----
		editorPaneLabel.setText("JEditorPane");
		add(editorPaneLabel, "cell 0 11");

		//======== scrollPane5 ========
		{
			scrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane1 ----
			editorPane1.setText("Editable");
			scrollPane5.setViewportView(editorPane1);
		}
		add(scrollPane5, "cell 1 11,growx");

		//======== scrollPane6 ========
		{
			scrollPane6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane6.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane2 ----
			editorPane2.setText("Disabled");
			editorPane2.setEnabled(false);
			scrollPane6.setViewportView(editorPane2);
		}
		add(scrollPane6, "cell 2 11,growx");

		//======== scrollPane7 ========
		{
			scrollPane7.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane7.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane3 ----
			editorPane3.setText("Not editable");
			editorPane3.setEditable(false);
			scrollPane7.setViewportView(editorPane3);
		}
		add(scrollPane7, "cell 3 11,growx");

		//======== scrollPane8 ========
		{
			scrollPane8.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane8.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane4 ----
			editorPane4.setText("Not editable disabled");
			editorPane4.setEditable(false);
			editorPane4.setEnabled(false);
			scrollPane8.setViewportView(editorPane4);
		}
		add(scrollPane8, "cell 4 11,growx");

		//---- editorPane5 ----
		editorPane5.setText("No scroll pane");
		add(editorPane5, "cell 5 11,growx");

		//---- textPaneLabel ----
		textPaneLabel.setText("JTextPane:");
		add(textPaneLabel, "cell 0 12");

		//======== scrollPane9 ========
		{
			scrollPane9.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane9.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane1 ----
			textPane1.setText("Editable");
			scrollPane9.setViewportView(textPane1);
		}
		add(scrollPane9, "cell 1 12,growx");

		//======== scrollPane10 ========
		{
			scrollPane10.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane10.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane2 ----
			textPane2.setText("Disabled");
			textPane2.setEnabled(false);
			scrollPane10.setViewportView(textPane2);
		}
		add(scrollPane10, "cell 2 12,growx");

		//======== scrollPane11 ========
		{
			scrollPane11.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane11.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane3 ----
			textPane3.setText("Not editable");
			textPane3.setEditable(false);
			scrollPane11.setViewportView(textPane3);
		}
		add(scrollPane11, "cell 3 12,growx");

		//======== scrollPane12 ========
		{
			scrollPane12.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane12.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane4 ----
			textPane4.setText("Not editable disabled");
			textPane4.setEditable(false);
			textPane4.setEnabled(false);
			scrollPane12.setViewportView(textPane4);
		}
		add(scrollPane12, "cell 4 12,growx");

		//---- textPane5 ----
		textPane5.setText("No scroll pane");
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
				panel1.setPreferredSize(new Dimension(800, 400));
				panel1.setLayout(new BorderLayout());

				//---- button19 ----
				button19.setText("I'm a large button");
				button19.setVerticalAlignment(SwingConstants.TOP);
				button19.setHorizontalAlignment(SwingConstants.LEFT);
				panel1.add(button19, BorderLayout.CENTER);
			}
			scrollPane13.setViewportView(panel1);
		}
		add(scrollPane13, "cell 1 13,grow,width 70,height 70");
		add(scrollBar2, "cell 2 13 1 6,growy");

		//---- scrollBar3 ----
		scrollBar3.setEnabled(false);
		add(scrollBar3, "cell 2 13 1 6,growy");

		//---- scrollBar7 ----
		scrollBar7.setShowButtons(true);
		add(scrollBar7, "cell 2 13 1 6,growy");

		//---- scrollBar8 ----
		scrollBar8.setEnabled(false);
		scrollBar8.setShowButtons(true);
		add(scrollBar8, "cell 2 13 1 6,growy");

		//---- separator2 ----
		separator2.setOrientation(SwingConstants.VERTICAL);
		add(separator2, "cell 2 13 1 6,growy");

		//---- slider2 ----
		slider2.setOrientation(SwingConstants.VERTICAL);
		slider2.setValue(30);
		add(slider2, "cell 2 13 1 6,growy");

		//---- slider4 ----
		slider4.setMinorTickSpacing(10);
		slider4.setPaintTicks(true);
		slider4.setMajorTickSpacing(50);
		slider4.setPaintLabels(true);
		slider4.setOrientation(SwingConstants.VERTICAL);
		slider4.setValue(30);
		add(slider4, "cell 2 13 1 6,growy");
		add(scrollPane14, "cell 3 13,grow");

		//---- progressBar3 ----
		progressBar3.setOrientation(SwingConstants.VERTICAL);
		progressBar3.setValue(60);
		add(progressBar3, "cell 4 13 1 6,growy");

		//---- progressBar4 ----
		progressBar4.setOrientation(SwingConstants.VERTICAL);
		progressBar4.setValue(60);
		progressBar4.setStringPainted(true);
		add(progressBar4, "cell 4 13 1 6,growy");

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
		add(toolBar2, "cell 4 13 1 6,growy");

		//======== scrollPane15 ========
		{
			scrollPane15.setBorder(BorderFactory.createEmptyBorder());

			//======== panel3 ========
			{
				panel3.setPreferredSize(new Dimension(800, 400));
				panel3.setLayout(new BorderLayout());

				//---- button21 ----
				button21.setText("I'm a large button in a scrollpane with empty border");
				button21.setVerticalAlignment(SwingConstants.TOP);
				button21.setHorizontalAlignment(SwingConstants.LEFT);
				panel3.add(button21, BorderLayout.CENTER);
			}
			scrollPane15.setViewportView(panel3);
		}
		add(scrollPane15, "cell 6 10 1 3,growy,width 100,height 50");

		//======== panel5 ========
		{
			panel5.setBorder(new TitledBorder("Control"));
			panel5.putClientProperty("FlatLaf.internal.testing.ignore", true);
			panel5.setLayout(new MigLayout(
				"ltr,insets dialog,hidemode 3",
				// columns
				"[]" +
				"[]",
				// rows
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]"));

			//---- buttonTypeComboBox ----
			buttonTypeComboBox.addActionListener(e -> buttonTypeChanged());
			panel5.add(buttonTypeComboBox, "cell 0 0");

			//---- borderPaintedCheckBox ----
			borderPaintedCheckBox.setText("borderPainted");
			borderPaintedCheckBox.setSelected(true);
			borderPaintedCheckBox.addActionListener(e -> borderPaintedChanged());
			panel5.add(borderPaintedCheckBox, "cell 1 0");

			//---- roundRectCheckBox ----
			roundRectCheckBox.setText("roundRect");
			roundRectCheckBox.addActionListener(e -> roundRectChanged());
			panel5.add(roundRectCheckBox, "cell 0 1");

			//---- contentAreaFilledCheckBox ----
			contentAreaFilledCheckBox.setText("contentAreaFilled");
			contentAreaFilledCheckBox.setSelected(true);
			contentAreaFilledCheckBox.addActionListener(e -> contentAreaFilledChanged());
			panel5.add(contentAreaFilledCheckBox, "cell 1 1");

			//======== panel4 ========
			{
				panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));

				//---- noOutlineRadioButton ----
				noOutlineRadioButton.setText("no outline");
				noOutlineRadioButton.setSelected(true);
				noOutlineRadioButton.addActionListener(e -> outlineChanged());
				panel4.add(noOutlineRadioButton);

				//---- errorOutlineRadioButton ----
				errorOutlineRadioButton.setText("error");
				errorOutlineRadioButton.addActionListener(e -> outlineChanged());
				panel4.add(errorOutlineRadioButton);

				//---- warningOutlineRadioButton ----
				warningOutlineRadioButton.setText("warning");
				warningOutlineRadioButton.addActionListener(e -> outlineChanged());
				panel4.add(warningOutlineRadioButton);

				//---- magentaOutlineRadioButton ----
				magentaOutlineRadioButton.setText("magenta");
				magentaOutlineRadioButton.addActionListener(e -> outlineChanged());
				panel4.add(magentaOutlineRadioButton);

				//---- magentaCyanOutlineRadioButton ----
				magentaCyanOutlineRadioButton.setText("magenta / cyan");
				magentaCyanOutlineRadioButton.addActionListener(e -> outlineChanged());
				panel4.add(magentaCyanOutlineRadioButton);
			}
			panel5.add(panel4, "cell 0 2 1 4");

			//---- focusPaintedCheckBox ----
			focusPaintedCheckBox.setText("focusPainted");
			focusPaintedCheckBox.setSelected(true);
			focusPaintedCheckBox.addActionListener(e -> focusPaintedChanged());
			panel5.add(focusPaintedCheckBox, "cell 1 2");

			//---- hideArrowButtonCheckBox ----
			hideArrowButtonCheckBox.setText("hide arrow button");
			hideArrowButtonCheckBox.addActionListener(e -> hideArrowButton());
			panel5.add(hideArrowButtonCheckBox, "cell 1 3");

			//---- transparentBackgroundCheckBox ----
			transparentBackgroundCheckBox.setText("transparent background");
			transparentBackgroundCheckBox.addActionListener(e -> transparentBackground());
			panel5.add(transparentBackgroundCheckBox, "cell 1 4");
		}
		add(panel5, "cell 5 13 2 10,grow");

		//---- scrollBarLabel ----
		scrollBarLabel.setText("JScrollBar:");
		add(scrollBarLabel, "cell 0 14");

		//---- scrollBar1 ----
		scrollBar1.setOrientation(Adjustable.HORIZONTAL);
		add(scrollBar1, "cell 1 14,growx");

		//---- scrollBar4 ----
		scrollBar4.setOrientation(Adjustable.HORIZONTAL);
		scrollBar4.setEnabled(false);
		add(scrollBar4, "cell 1 15,growx");

		//---- scrollBar5 ----
		scrollBar5.setOrientation(Adjustable.HORIZONTAL);
		scrollBar5.setShowButtons(true);
		add(scrollBar5, "cell 1 16,growx");

		//---- scrollBar6 ----
		scrollBar6.setOrientation(Adjustable.HORIZONTAL);
		scrollBar6.setEnabled(false);
		scrollBar6.setShowButtons(true);
		add(scrollBar6, "cell 1 17,growx");

		//---- separatorLabel ----
		separatorLabel.setText("JSeparator:");
		add(separatorLabel, "cell 0 18");
		add(separator1, "cell 1 18,growx");

		//======== panel2 ========
		{
			panel2.setBorder(new TitledBorder("TitledBorder"));
			panel2.setOpaque(false);
			panel2.setLayout(new FlowLayout());
		}
		add(panel2, "cell 3 18,grow");

		//---- sliderLabel ----
		sliderLabel.setText("JSlider:");
		add(sliderLabel, "cell 0 19");

		//---- slider1 ----
		slider1.setValue(30);
		add(slider1, "cell 1 19 3 1,growx");

		//---- slider6 ----
		slider6.setEnabled(false);
		slider6.setValue(30);
		add(slider6, "cell 1 19 3 1");

		//======== panel8 ========
		{
			panel8.putClientProperty("FlatLaf.internal.testing.ignore", true);
			panel8.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3",
				// columns
				"[]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//======== panel6 ========
			{
				panel6.setBorder(new TitledBorder("JSlider Control"));
				panel6.setLayout(new MigLayout(
					"ltr,insets 0,hidemode 3",
					// columns
					"[]",
					// rows
					"[]0" +
					"[]0" +
					"[]0" +
					"[]"));

				//---- sliderPaintTrackCheckBox ----
				sliderPaintTrackCheckBox.setText("track");
				sliderPaintTrackCheckBox.setSelected(true);
				sliderPaintTrackCheckBox.addActionListener(e -> sliderPaintTrackChanged());
				panel6.add(sliderPaintTrackCheckBox, "cell 0 0");

				//---- sliderPaintTicksCheckBox ----
				sliderPaintTicksCheckBox.setText("ticks");
				sliderPaintTicksCheckBox.addActionListener(e -> sliderPaintTicksChanged());
				panel6.add(sliderPaintTicksCheckBox, "cell 0 0");

				//---- sliderPaintLabelsCheckBox ----
				sliderPaintLabelsCheckBox.setText("labels");
				sliderPaintLabelsCheckBox.addActionListener(e -> sliderPaintLabelsChanged());
				panel6.add(sliderPaintLabelsCheckBox, "cell 0 0");

				//---- sliderInvertedCheckBox ----
				sliderInvertedCheckBox.setText("inverted");
				sliderInvertedCheckBox.addActionListener(e -> sliderInvertedChanged());
				panel6.add(sliderInvertedCheckBox, "cell 0 1");

				//---- sliderSnapToTicksCheckBox ----
				sliderSnapToTicksCheckBox.setText("snap to ticks");
				sliderSnapToTicksCheckBox.addActionListener(e -> sliderSnapToTicksChanged());
				panel6.add(sliderSnapToTicksCheckBox, "cell 0 1");

				//---- majorTickSpacingSpinner ----
				majorTickSpacingSpinner.setModel(new SpinnerNumberModel(50, 0, 100, 5));
				majorTickSpacingSpinner.addChangeListener(e -> majorThickSpacingChanged());
				panel6.add(majorTickSpacingSpinner, "cell 0 2");

				//---- sliderBorderCheckBox ----
				sliderBorderCheckBox.setText("border");
				sliderBorderCheckBox.addActionListener(e -> sliderBorderChanged());
				panel6.add(sliderBorderCheckBox, "cell 0 2");

				//---- minorTickSpacingSpinner ----
				minorTickSpacingSpinner.setModel(new SpinnerNumberModel(10, 0, 100, 5));
				minorTickSpacingSpinner.addChangeListener(e -> minorThickSpacingChanged());
				panel6.add(minorTickSpacingSpinner, "cell 0 3");

				//---- sliderValueLabel ----
				sliderValueLabel.setText("slider value");
				panel6.add(sliderValueLabel, "cell 0 3");
			}
			panel8.add(panel6, "cell 0 0 1 2,grow");

			//======== panel7 ========
			{
				panel7.setBorder(new TitledBorder("JProgressBar Control"));
				panel7.setLayout(new MigLayout(
					"ltr,insets 0,hidemode 3",
					// columns
					"[]" +
					"[fill]",
					// rows
					"[]0" +
					"[]"));

				//---- indeterminateCheckBox ----
				indeterminateCheckBox.setText("indeterminate");
				indeterminateCheckBox.addActionListener(e -> indeterminateProgress());
				panel7.add(indeterminateCheckBox, "cell 0 0");

				//---- squareCheckBox ----
				squareCheckBox.setText("square");
				squareCheckBox.addActionListener(e -> squareChanged());
				panel7.add(squareCheckBox, "cell 1 0");

				//---- largeHeightCheckBox ----
				largeHeightCheckBox.setText("large height");
				largeHeightCheckBox.addActionListener(e -> largeHeightChanged());
				panel7.add(largeHeightCheckBox, "cell 0 1,aligny top,growy 0");
			}
			panel8.add(panel7, "cell 0 2 1 2,grow");
		}
		add(panel8, "cell 4 19 1 4");

		//---- sliderLabel2 ----
		sliderLabel2.setText("baseline");
		add(sliderLabel2, "cell 0 20,alignx right,growx 0");

		//---- slider3 ----
		slider3.setMinorTickSpacing(10);
		slider3.setPaintTicks(true);
		slider3.setMajorTickSpacing(50);
		slider3.setPaintLabels(true);
		slider3.setValue(30);
		slider3.addChangeListener(e -> changeProgress());
		add(slider3, "cell 1 20 3 1,growx");

		//---- slider5 ----
		slider5.setMinorTickSpacing(10);
		slider5.setPaintTicks(true);
		slider5.setMajorTickSpacing(50);
		slider5.setPaintLabels(true);
		slider5.setEnabled(false);
		slider5.setValue(30);
		add(slider5, "cell 1 20 3 1");

		//---- progressBarLabel ----
		progressBarLabel.setText("JProgressBar:");
		add(progressBarLabel, "cell 0 21");

		//---- progressBar1 ----
		progressBar1.setValue(60);
		add(progressBar1, "cell 1 21 3 1,growx");

		//---- progressBar2 ----
		progressBar2.setStringPainted(true);
		progressBar2.setValue(60);
		add(progressBar2, "cell 1 21 3 1,growx");

		//---- toolTipLabel ----
		toolTipLabel.setText("JToolTip:");
		add(toolTipLabel, "cell 0 22");

		//---- toolTip1 ----
		toolTip1.setTipText("Some text in tool tip.");
		add(toolTip1, "cell 1 22 3 1");

		//---- toolTip2 ----
		toolTip2.setTipText("Tool tip with\nmultiple\nlines.");
		add(toolTip2, "cell 1 22 3 1");

		//---- toolBarLabel ----
		toolBarLabel.setText("JToolBar:");
		add(toolBarLabel, "cell 0 23");

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

			//---- toggleButton15 ----
			toggleButton15.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toggleButton15.setSelected(true);
			toolBar1.add(toggleButton15);

			//---- toggleButton16 ----
			toggleButton16.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
			toggleButton16.setSelected(true);
			toolBar1.add(toggleButton16);

			//---- toggleButton17 ----
			toggleButton17.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
			toggleButton17.setSelected(true);
			toolBar1.add(toggleButton17);
		}
		add(toolBar1, "cell 1 23 6 1");

		//---- label3 ----
		label3.setText("Square:");
		add(label3, "cell 1 23 6 1");

		//======== toolBar3 ========
		{

			//---- button26 ----
			button26.setIcon(UIManager.getIcon("Tree.closedIcon"));
			button26.setButtonType(FlatButton.ButtonType.square);
			toolBar3.add(button26);

			//---- button27 ----
			button27.setIcon(UIManager.getIcon("Tree.openIcon"));
			button27.setButtonType(FlatButton.ButtonType.square);
			toolBar3.add(button27);

			//---- toggleButton23 ----
			toggleButton23.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toggleButton23.setSelected(true);
			toggleButton23.setButtonType(FlatButton.ButtonType.square);
			toolBar3.add(toggleButton23);

			//---- toggleButton24 ----
			toggleButton24.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
			toggleButton24.setSelected(true);
			toggleButton24.setButtonType(FlatButton.ButtonType.square);
			toolBar3.add(toggleButton24);
		}
		add(toolBar3, "cell 1 23 6 1");

		//---- label4 ----
		label4.setText("Round:");
		add(label4, "cell 1 23 6 1");

		//======== toolBar4 ========
		{

			//---- button28 ----
			button28.setIcon(UIManager.getIcon("Tree.closedIcon"));
			button28.setButtonType(FlatButton.ButtonType.roundRect);
			toolBar4.add(button28);

			//---- button29 ----
			button29.setIcon(UIManager.getIcon("Tree.openIcon"));
			button29.setButtonType(FlatButton.ButtonType.roundRect);
			toolBar4.add(button29);

			//---- toggleButton25 ----
			toggleButton25.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toggleButton25.setSelected(true);
			toggleButton25.setButtonType(FlatButton.ButtonType.roundRect);
			toolBar4.add(toggleButton25);

			//---- toggleButton26 ----
			toggleButton26.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
			toggleButton26.setSelected(true);
			toggleButton26.setButtonType(FlatButton.ButtonType.roundRect);
			toolBar4.add(toggleButton26);
		}
		add(toolBar4, "cell 1 23 6 1");

		//---- label5 ----
		label5.setText("Group:");
		add(label5, "cell 1 23 6 1");

		//======== toolBar5 ========
		{

			//---- toggleButton27 ----
			toggleButton27.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toggleButton27.setSelected(true);
			toolBar5.add(toggleButton27);

			//---- toggleButton28 ----
			toggleButton28.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toolBar5.add(toggleButton28);
			toolBar5.addSeparator();

			//---- toggleButton29 ----
			toggleButton29.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toolBar5.add(toggleButton29);
			toolBar5.addSeparator();

			//---- toggleButton30 ----
			toggleButton30.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
			toggleButton30.setSelected(true);
			toolBar5.add(toggleButton30);

			//---- toggleButton31 ----
			toggleButton31.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
			toolBar5.add(toggleButton31);
			toolBar5.addSeparator();

			//---- toggleButton32 ----
			toggleButton32.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toolBar5.add(toggleButton32);
		}
		add(toolBar5, "cell 1 23 6 1");

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(noOutlineRadioButton);
		buttonGroup1.add(errorOutlineRadioButton);
		buttonGroup1.add(warningOutlineRadioButton);
		buttonGroup1.add(magentaOutlineRadioButton);
		buttonGroup1.add(magentaCyanOutlineRadioButton);

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(toggleButton27);
		buttonGroup2.add(toggleButton28);
		buttonGroup2.add(toggleButton29);
		buttonGroup2.add(toggleButton32);

		//---- buttonGroup3 ----
		ButtonGroup buttonGroup3 = new ButtonGroup();
		buttonGroup3.add(toggleButton30);
		buttonGroup3.add(toggleButton31);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		// Unicode surrogate character pair "script capital A"
		// https://www.compart.com/en/unicode/U+1D49C
		button24.setText("\uD835\uDC9C");
		toggleButton21.setText("\uD835\uDC9C");

//		BasicComboBoxRenderer customRenderer = new BasicComboBoxRenderer();
//		customRenderer.setBorder( new LineBorder( Color.red ) );
//		comboBox1.setRenderer( customRenderer );
//		comboBox3.setRenderer( customRenderer );
//		comboBox5.setRenderer( new DefaultListCellRenderer() );

		// for testing issue #382
//		spinner1.setModel( new SpinnerNumberModel( 0, null, 100, 1 ) );
//		comboBox1.setEditor( new BasicComboBoxEditor() );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTextField textField1;
	private JSlider slider2;
	private JSlider slider4;
	private FlatProgressBar progressBar3;
	private FlatProgressBar progressBar4;
	private FlatTestEnumComboBox<ButtonType> buttonTypeComboBox;
	private JCheckBox borderPaintedCheckBox;
	private JCheckBox roundRectCheckBox;
	private JCheckBox contentAreaFilledCheckBox;
	private JRadioButton noOutlineRadioButton;
	private JRadioButton errorOutlineRadioButton;
	private JRadioButton warningOutlineRadioButton;
	private JRadioButton magentaOutlineRadioButton;
	private JRadioButton magentaCyanOutlineRadioButton;
	private JCheckBox focusPaintedCheckBox;
	private JCheckBox hideArrowButtonCheckBox;
	private JCheckBox transparentBackgroundCheckBox;
	private JSlider slider1;
	private JSlider slider6;
	private JCheckBox sliderPaintTrackCheckBox;
	private FlatTriStateCheckBox sliderPaintTicksCheckBox;
	private FlatTriStateCheckBox sliderPaintLabelsCheckBox;
	private JCheckBox sliderInvertedCheckBox;
	private JCheckBox sliderSnapToTicksCheckBox;
	private JSpinner majorTickSpacingSpinner;
	private JCheckBox sliderBorderCheckBox;
	private JSpinner minorTickSpacingSpinner;
	private JLabel sliderValueLabel;
	private JCheckBox indeterminateCheckBox;
	private JCheckBox squareCheckBox;
	private JCheckBox largeHeightCheckBox;
	private JSlider slider3;
	private JSlider slider5;
	private FlatProgressBar progressBar1;
	private FlatProgressBar progressBar2;
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

	//---- class TestMultiLineLabel -------------------------------------------

	private static class TestMultiLineLabel
		extends JTextArea
	{
		public TestMultiLineLabel() {
			setEditable( false );
			setFocusable( false );
		}

		@Override
		public void updateUI() {
			super.updateUI();
			setBackground( UIManager.getColor( "Label.background" ) );
			setForeground( UIManager.getColor( "Label.foreground" ) );
			setFont( UIManager.getFont( "Label.font" ) );
			setBorder( null );
		}
	}

	//---- class ButtonlessSpinner --------------------------------------------

	private static class ButtonlessSpinner
		extends JSpinner
	{
		@Override
		public void updateUI() {
			super.updateUI();

			// remove arrow buttons
			for( Component c : getComponents() ) {
				String name = c.getName();
				if( "Spinner.nextButton".equals( name ) || "Spinner.previousButton".equals( name ) )
					remove( c );
			}
		}
	}

	//---- class TestToolBar --------------------------------------------------

	private static class TestToolBar
		extends JToolBar
	{
		@Override
		protected void paintComponent( Graphics g ) {
			super.paintComponent( g );

			if( isPaintBackgroundPattern() && isOpaque() ) {
				int width = getWidth();
				int height = getHeight();

				g.setColor( Color.blue );
				for( int y = 0; y < height; y += 2 )
					g.drawLine( 0, y, width - 1, y );
			}
		}

		/**
		 * Overridden to see which components paint background with color from parent.
		 */
		@Override
		public Color getBackground() {
			return isPaintBackgroundPattern() ? Color.orange : super.getBackground();
		}

		private boolean isPaintBackgroundPattern() {
			FlatTestFrame frame = (FlatTestFrame) SwingUtilities.getAncestorOfClass( FlatTestFrame.class, this );
			return frame != null && frame.isPaintBackgroundPattern();
		}
	}
}
