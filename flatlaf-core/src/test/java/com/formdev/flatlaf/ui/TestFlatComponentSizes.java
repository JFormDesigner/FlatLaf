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

package com.formdev.flatlaf.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import com.formdev.flatlaf.util.UIScale;

/**
 * @author Karl Tauber
 */
public class TestFlatComponentSizes
{
	@BeforeAll
	static void setup() {
		TestUtils.setup( false );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
	}

	static float[] factors() {
		return TestUtils.FACTORS;
	}

	@ParameterizedTest
	@MethodSource( "factors" )
	void sizes( float factor ) {
		TestUtils.scaleFont( factor );


		// should have same default size (minimumWidth is 64)
		JTextField textField = new JTextField();
		JFormattedTextField formattedTextField = new JFormattedTextField();
		JPasswordField passwordField = new JPasswordField();
		JSpinner spinner = new JSpinner();

		Dimension textFieldSize = textField.getPreferredSize();
		assertEquals( textFieldSize, formattedTextField.getPreferredSize() );
		assertEquals( textFieldSize, passwordField.getPreferredSize() );
		assertEquals( textFieldSize, spinner.getPreferredSize() );


		// should have same default size (minimumWidth is 72)
		JButton button = new JButton( "text" );
		JComboBox<String> comboBox = new JComboBox<>();
		JComboBox<String> comboBoxEditable = new JComboBox<>();
		comboBoxEditable.setEditable( true );

		Dimension buttonSize = button.getPreferredSize();
		assertEquals( buttonSize, comboBox.getPreferredSize() );
		assertEquals( buttonSize, comboBoxEditable.getPreferredSize() );


		// should have same height
		JToggleButton toggleButton = new JToggleButton( "text" );

		assertEquals( textFieldSize.height, button.getPreferredSize().height );
		assertEquals( textFieldSize.height, toggleButton.getPreferredSize().height );


		// should have same size
		JCheckBox checkBox = new JCheckBox( "text" );
		JRadioButton radioButton = new JRadioButton( "text" );
		assertEquals( checkBox.getPreferredSize(), radioButton.getPreferredSize() );


		// should have same size
		JMenu menu = new JMenu( "text" );
		JMenuItem menuItem = new JMenuItem( "text" );
		JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem( "text" );
		JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem( "text" );

		Dimension menuSize = menu.getPreferredSize();
		assertEquals( menuSize, menuItem.getPreferredSize() );
		assertEquals( menuSize, checkBoxMenuItem.getPreferredSize() );
		assertEquals( menuSize, radioButtonMenuItem.getPreferredSize() );


		TestUtils.resetFont();
	}

	@ParameterizedTest
	@MethodSource( "factors" )
	void comboBox( float factor ) {
		TestUtils.scaleFont( factor );

		String[] items = { "t" };
		JComboBox<String> comboBox = new JComboBox<>( items );
		JComboBox<String> comboBox2 = new JComboBox<>( items );
		JComboBox<String> comboBox3 = new JComboBox<>( items );
		JComboBox<String> comboBox4 = new JComboBox<>( items );

		applyCustomComboBoxRendererBorder( comboBox2, new LineBorder( Color.orange, UIScale.scale( 6 ) ) );
		applyCustomComboBoxRendererBorder( comboBox3, new BorderWithIcon() );
		applyCustomComboBoxRendererBorder( comboBox4, null );

		Dimension size = comboBox.getPreferredSize();
		assertEquals( size.width, comboBox2.getPreferredSize().width );
		assertEquals( size.height - (2 * UIScale.scale( 2 )) + (2 * UIScale.scale( 6 )), comboBox2.getPreferredSize().height );
		assertEquals( size, comboBox3.getPreferredSize() );
		assertEquals( size, comboBox4.getPreferredSize() );

		TestUtils.resetFont();
	}

	@SuppressWarnings( "unchecked" )
	private void applyCustomComboBoxRendererBorder( JComboBox<String> comboBox, Border border ) {
		BasicComboBoxRenderer customRenderer = new BasicComboBoxRenderer();
		customRenderer.setBorder( border );
		comboBox.setRenderer( customRenderer );
	}

	@ParameterizedTest
	@MethodSource( "factors" )
	void comboBoxEditable( float factor ) {
		TestUtils.scaleFont( factor );

		String[] items = { "t" };
		JComboBox<String> comboBox = new JComboBox<>( items );
		JComboBox<String> comboBox2 = new JComboBox<>( items );
		JComboBox<String> comboBox3 = new JComboBox<>( items );
		JComboBox<String> comboBox4 = new JComboBox<>( items );

		comboBox.setEditable( true );
		comboBox2.setEditable( true );
		comboBox3.setEditable( true );
		comboBox4.setEditable( true );

		applyCustomComboBoxEditorBorder( comboBox2, new LineBorder( Color.orange, UIScale.scale( 6 ) ) );
		applyCustomComboBoxEditorBorder( comboBox3, new BorderWithIcon() );
		applyCustomComboBoxEditorBorder( comboBox4, null );

		Dimension size = comboBox.getPreferredSize();
		assertEquals( size.width, comboBox2.getPreferredSize().width );
		assertEquals( size.height - (2 * UIScale.scale( 2 )) + (2 * UIScale.scale( 6 )), comboBox2.getPreferredSize().height );
		assertEquals( size, comboBox3.getPreferredSize() );
		assertEquals( size, comboBox4.getPreferredSize() );

		TestUtils.resetFont();
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

	//---- class BorderWithIcon -----------------------------------------------

	private static class BorderWithIcon
		implements Border
	{
		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		}

		@Override
		public boolean isBorderOpaque() {
			return false;
		}

		@Override
		public Insets getBorderInsets( Component c ) {
			return new Insets( 0, 0, 0, UIScale.scale( 16 ) + 4 );
		}
	}
}
