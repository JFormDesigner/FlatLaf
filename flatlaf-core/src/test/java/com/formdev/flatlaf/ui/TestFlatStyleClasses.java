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
import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatSystemProperties;

/**
 * @author Karl Tauber
 */
public class TestFlatStyleClasses
{
	private static final String BUTTON_PRIMARY = "borderColor: #08f; background: #08f; foreground: #fff";
	private static final String SECONDARY = "borderColor: #0f8; background: #0f8";
	private static final String TOGGLE_BUTTON_SECONDARY = "selectedBackground: #f00";
	private static final String BACKGROUND = "background: #f0f";

	@BeforeAll
	static void setup() {
		System.setProperty( FlatSystemProperties.UI_SCALE, "1x" );
		TestUtils.setup( false );

		UIManager.put( "[style]Button.primary", BUTTON_PRIMARY );
		UIManager.put( "[style].secondary", SECONDARY );
		UIManager.put( "[style]ToggleButton.secondary", TOGGLE_BUTTON_SECONDARY );
		UIManager.put( "[style].test", BACKGROUND );

		UIManager.put( "[style]Button.test", "foreground: #000001" );
		UIManager.put( "[style]CheckBox.test", "foreground: #000002" );
		UIManager.put( "[style]ComboBox.test", "foreground: #000003" );
		UIManager.put( "[style]EditorPane.test", "foreground: #000004" );
		UIManager.put( "[style]FormattedTextField.test", "foreground: #000005" );
		UIManager.put( "[style]InternalFrame.test", "foreground: #000006" );
		UIManager.put( "[style]Label.test", "foreground: #000007" );
		UIManager.put( "[style]List.test", "foreground: #000008" );
		UIManager.put( "[style]MenuBar.test", "foreground: #000009" );
		UIManager.put( "[style]Menu.test", "foreground: #000010" );
		UIManager.put( "[style]MenuItem.test", "foreground: #000011" );
		UIManager.put( "[style]CheckBoxMenuItem.test", "foreground: #000012" );
		UIManager.put( "[style]RadioButtonMenuItem.test", "foreground: #000013" );
		UIManager.put( "[style]Panel.test", "foreground: #000034" );
		UIManager.put( "[style]PasswordField.test", "foreground: #000014" );
		UIManager.put( "[style]PopupMenu.test", "foreground: #000015" );
		UIManager.put( "[style]PopupMenuSeparator.test", "foreground: #000016" );
		UIManager.put( "[style]ProgressBar.test", "foreground: #000017" );
		UIManager.put( "[style]RadioButton.test", "foreground: #000018" );
		UIManager.put( "[style]ScrollBar.test", "foreground: #000019" );
		UIManager.put( "[style]ScrollPane.test", "foreground: #000020" );
		UIManager.put( "[style]Separator.test", "foreground: #000021" );
		UIManager.put( "[style]Slider.test", "foreground: #000022" );
		UIManager.put( "[style]Spinner.test", "foreground: #000023" );
		UIManager.put( "[style]SplitPane.test", "foreground: #000024" );
		UIManager.put( "[style]TabbedPane.test", "foreground: #000025" );
		UIManager.put( "[style]Table.test", "foreground: #000026" );
		UIManager.put( "[style]TableHeader.test", "foreground: #000027" );
		UIManager.put( "[style]TextArea.test", "foreground: #000028" );
		UIManager.put( "[style]TextField.test", "foreground: #000029" );
		UIManager.put( "[style]TextPane.test", "foreground: #000030" );
		UIManager.put( "[style]ToggleButton.test", "foreground: #000031" );
		UIManager.put( "[style]ToolBar.test", "foreground: #000032" );
		UIManager.put( "[style]Tree.test", "foreground: #000033" );

		// for shared UIs
		UIManager.put( "[style]Button.test2", "foreground: #000100" );
		UIManager.put( "[style]CheckBox.test2", "foreground: #000200" );
		UIManager.put( "[style]Label.test2", "foreground: #000700" );
		UIManager.put( "[style]PopupMenuSeparator.test2", "foreground: #001600" );
		UIManager.put( "[style]RadioButton.test2", "foreground: #001800" );
		UIManager.put( "[style]Separator.test2", "foreground: #002100" );
		UIManager.put( "[style]ToggleButton.test2", "foreground: #003100" );

		// JToolBar.Separator
		UIManager.put( "[style]ToolBarSeparator.toolbar-separator-test", "separatorWidth: 21" );
		UIManager.put( "[style]ToolBarSeparator.toolbar-separator-test2", "separatorWidth: 31" );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
		System.clearProperty( FlatSystemProperties.UI_SCALE );
	}

	@Test
	void styleForClass() {
		assertEquals( null, FlatStylingSupport.getStyleForClasses( "foo", "Button" ) );

		assertEquals( BUTTON_PRIMARY, FlatStylingSupport.getStyleForClasses( "primary", "Button" ) );
		assertEquals( SECONDARY, FlatStylingSupport.getStyleForClasses( "secondary", "Button" ) );

		assertEquals(
			FlatStylingSupport.concatStyles( SECONDARY, TOGGLE_BUTTON_SECONDARY ),
			FlatStylingSupport.getStyleForClasses( "secondary", "ToggleButton" ) );

		assertEquals(
			FlatStylingSupport.concatStyles( BUTTON_PRIMARY, SECONDARY ),
			FlatStylingSupport.getStyleForClasses( "primary secondary", "Button" ) );
		assertEquals(
			FlatStylingSupport.concatStyles( SECONDARY, BUTTON_PRIMARY ),
			FlatStylingSupport.getStyleForClasses( "secondary primary", "Button" ) );

		// String
		assertEquals(
			FlatStylingSupport.concatStyles( SECONDARY, BUTTON_PRIMARY ),
			FlatStylingSupport.getStyleForClasses( "  secondary    primary  bla blu  ", "Button" ) );

		// String[]
		assertEquals(
			FlatStylingSupport.concatStyles( SECONDARY, BUTTON_PRIMARY ),
			FlatStylingSupport.getStyleForClasses( new String[] { "secondary", "primary" }, "Button" ) );

		// List<String>
		assertEquals(
			FlatStylingSupport.concatStyles( SECONDARY, BUTTON_PRIMARY ),
			FlatStylingSupport.getStyleForClasses( Arrays.asList( "secondary", "primary" ), "Button" ) );
	}

	@Test
	void apply1() {
		JButton c = new JButton();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "primary" );

		assertEquals( new Color( 0x0088ff ), c.getBackground() );
		assertEquals( Color.white, c.getForeground() );
	}

	@Test
	void apply2() {
		JButton c = new JButton();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "primary secondary" );

		assertEquals( new Color( 0x00ff88 ), c.getBackground() );
		assertEquals( Color.white, c.getForeground() );
	}

	@Test
	void apply3() {
		JButton c = new JButton();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "secondary primary" );

		assertEquals( new Color( 0x0088ff ), c.getBackground() );
		assertEquals( Color.white, c.getForeground() );
	}

	//---- components ---------------------------------------------------------

	@Test
	void button() {
		JButton c = new JButton();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000001 ), c.getForeground() );

		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test2" );
		assertEquals( new Color( 0x000100 ), c.getForeground() );
	}

	@Test
	void checkBox() {
		JCheckBox c = new JCheckBox();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000002 ), c.getForeground() );

		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test2" );
		assertEquals( new Color( 0x000200 ), c.getForeground() );
	}

	@Test
	void comboBox() {
		JComboBox<Object> c = new JComboBox<>();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000003 ), c.getForeground() );
	}

	@Test
	void editorPane() {
		JEditorPane c = new JEditorPane();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000004 ), c.getForeground() );
	}

	@Test
	void formattedTextField() {
		JFormattedTextField c = new JFormattedTextField();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000005 ), c.getForeground() );
	}

	@Test
	void internalFrame() {
		JInternalFrame c = new JInternalFrame();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000006 ), c.getForeground() );
	}

	@Test
	void label() {
		JLabel c = new JLabel();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000007 ), c.getForeground() );

		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test2" );
		assertEquals( new Color( 0x000700 ), c.getForeground() );
	}

	@Test
	void list() {
		JList<Object> c = new JList<>();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000008 ), c.getForeground() );
	}

	@Test
	void menuBar() {
		JMenuBar c = new JMenuBar();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000009 ), c.getForeground() );
	}

	@Test
	void menu() {
		JMenu c = new JMenu();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000010 ), c.getForeground() );
	}

	@Test
	void menuItem() {
		JMenuItem c = new JMenuItem();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000011 ), c.getForeground() );
	}

	@Test
	void checkBoxMenuItem() {
		JCheckBoxMenuItem c = new JCheckBoxMenuItem();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000012 ), c.getForeground() );
	}

	@Test
	void radioButtonMenuItem() {
		JRadioButtonMenuItem c = new JRadioButtonMenuItem();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000013 ), c.getForeground() );
	}

	@Test
	void panel() {
		JPanel c = new JPanel();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000034 ), c.getForeground() );
	}

	@Test
	void passwordField() {
		JPasswordField c = new JPasswordField();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000014 ), c.getForeground() );
	}

	@Test
	void popupMenu() {
		JPopupMenu c = new JPopupMenu();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000015 ), c.getForeground() );
	}

	@Test
	void popupMenuSeparator() {
		JPopupMenu.Separator c = new JPopupMenu.Separator();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000016 ), c.getForeground() );

		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test2" );
		assertEquals( new Color( 0x001600 ), c.getForeground() );
	}

	@Test
	void progressBar() {
		JProgressBar c = new JProgressBar();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000017 ), c.getForeground() );
	}

	@Test
	void radioButton() {
		JRadioButton c = new JRadioButton();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000018 ), c.getForeground() );

		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test2" );
		assertEquals( new Color( 0x001800 ), c.getForeground() );
	}

	@Test
	void scrollBar() {
		JScrollBar c = new JScrollBar();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000019 ), c.getForeground() );
	}

	@Test
	void scrollPane() {
		JScrollPane c = new JScrollPane();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000020 ), c.getForeground() );
	}

	@Test
	void separator() {
		JSeparator c = new JSeparator();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000021 ), c.getForeground() );

		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test2" );
		assertEquals( new Color( 0x002100 ), c.getForeground() );
	}

	@Test
	void slider() {
		JSlider c = new JSlider();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000022 ), c.getForeground() );
	}

	@Test
	void spinner() {
		JSpinner c = new JSpinner();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000023 ), c.getForeground() );
	}

	@Test
	void splitPane() {
		JSplitPane c = new JSplitPane();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000024 ), c.getForeground() );
	}

	@Test
	void tabbedPane() {
		JTabbedPane c = new JTabbedPane();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000025 ), c.getForeground() );
	}

	@Test
	void table() {
		JTable c = new JTable();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000026 ), c.getForeground() );
	}

	@Test
	void tableHeader() {
		JTableHeader c = new JTableHeader();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000027 ), c.getForeground() );
	}

	@Test
	void textArea() {
		JTextArea c = new JTextArea();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000028 ), c.getForeground() );
	}

	@Test
	void textField() {
		JTextField c = new JTextField();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000029 ), c.getForeground() );
	}

	@Test
	void textPane() {
		JTextPane c = new JTextPane();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000030 ), c.getForeground() );
	}

	@Test
	void toggleButton() {
		JToggleButton c = new JToggleButton();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000031 ), c.getForeground() );

		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test2" );
		assertEquals( new Color( 0x003100 ), c.getForeground() );
	}

	@Test
	void toolBar() {
		JToolBar c = new JToolBar();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000032 ), c.getForeground() );
	}

	@Test
	void toolBarSeparator() {
		JToolBar.Separator c = new JToolBar.Separator();
		assertEquals( new Dimension( 0, 7 ), c.getPreferredSize() );

		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "toolbar-separator-test" );
		assertEquals( new Dimension( 0, 21 ), c.getPreferredSize() );

		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "toolbar-separator-test2" );
		assertEquals( new Dimension( 0, 31 ), c.getPreferredSize() );
	}

	@Test
	void tree() {
		JTree c = new JTree();
		c.putClientProperty( FlatClientProperties.STYLE_CLASS, "test" );
		assertEquals( Color.magenta, c.getBackground() );
		assertEquals( new Color( 0x000033 ), c.getForeground() );
	}
}
