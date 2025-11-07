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
import javax.swing.*;
import javax.swing.table.JTableHeader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.FlatSystemProperties;

/**
 * @author Karl Tauber
 */
public class TestFlatStyleType
{
	@BeforeAll
	static void setup() {
		System.setProperty( FlatSystemProperties.UI_SCALE_ENABLED, "false" );
		TestUtils.setup( false );

		UIManager.put( "[style]Button", "foreground: #000001" );
		UIManager.put( "[style]CheckBox", "foreground: #000002" );
		UIManager.put( "[style]ComboBox", "foreground: #000003" );
		UIManager.put( "[style]EditorPane", "foreground: #000004" );
		UIManager.put( "[style]FormattedTextField", "foreground: #000005" );
		UIManager.put( "[style]InternalFrame", "foreground: #000006" );
		UIManager.put( "[style]Label", "foreground: #000007" );
		UIManager.put( "[style]List", "foreground: #000008" );
		UIManager.put( "[style]MenuBar", "foreground: #000009" );
		UIManager.put( "[style]Menu", "foreground: #000010" );
		UIManager.put( "[style]MenuItem", "foreground: #000011" );
		UIManager.put( "[style]CheckBoxMenuItem", "foreground: #000012" );
		UIManager.put( "[style]RadioButtonMenuItem", "foreground: #000013" );
		UIManager.put( "[style]PasswordField", "foreground: #000014" );
		UIManager.put( "[style]PopupMenu", "foreground: #000015" );
		UIManager.put( "[style]PopupMenuSeparator", "foreground: #000016" );
		UIManager.put( "[style]ProgressBar", "foreground: #000017" );
		UIManager.put( "[style]RadioButton", "foreground: #000018" );
		UIManager.put( "[style]ScrollBar", "foreground: #000019" );
		UIManager.put( "[style]ScrollPane", "foreground: #000020" );
		UIManager.put( "[style]Separator", "foreground: #000021" );
		UIManager.put( "[style]Slider", "foreground: #000022" );
		UIManager.put( "[style]Spinner", "foreground: #000023" );
		UIManager.put( "[style]SplitPane", "foreground: #000024" );
		UIManager.put( "[style]TabbedPane", "foreground: #000025" );
		UIManager.put( "[style]Table", "foreground: #000026" );
		UIManager.put( "[style]TableHeader", "foreground: #000027" );
		UIManager.put( "[style]TextArea", "foreground: #000028" );
		UIManager.put( "[style]TextField", "foreground: #000029" );
		UIManager.put( "[style]TextPane", "foreground: #000030" );
		UIManager.put( "[style]ToggleButton", "foreground: #000031" );
		UIManager.put( "[style]ToolBar", "foreground: #000032" );
		UIManager.put( "[style]Tree", "foreground: #000033" );

		// JToolBar.Separator
		UIManager.put( "[style]ToolBarSeparator", "separatorWidth: 21" );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
		System.clearProperty( FlatSystemProperties.UI_SCALE_ENABLED );
	}

	@Test
	void styleForType() {
		assertEquals( "foreground: #000001", FlatStylingSupport.getStyleForType( "Button" ) );
	}

	//---- components ---------------------------------------------------------

	@Test
	void button() {
		JButton c = new JButton();
		assertEquals( new Color( 0x000001 ), c.getForeground() );
	}

	@Test
	void checkBox() {
		JCheckBox c = new JCheckBox();
		assertEquals( new Color( 0x000002 ), c.getForeground() );
	}

	@Test
	void comboBox() {
		JComboBox<Object> c = new JComboBox<>();
		assertEquals( new Color( 0x000003 ), c.getForeground() );
	}

	@Test
	void editorPane() {
		JEditorPane c = new JEditorPane();
		assertEquals( new Color( 0x000004 ), c.getForeground() );
	}

	@Test
	void formattedTextField() {
		JFormattedTextField c = new JFormattedTextField();
		assertEquals( new Color( 0x000005 ), c.getForeground() );
	}

	@Test
	void internalFrame() {
		JInternalFrame c = new JInternalFrame();
		assertEquals( new Color( 0x000006 ), c.getForeground() );
	}

	@Test
	void label() {
		JLabel c = new JLabel();
		assertEquals( new Color( 0x000007 ), c.getForeground() );
	}

	@Test
	void list() {
		JList<Object> c = new JList<>();
		assertEquals( new Color( 0x000008 ), c.getForeground() );
	}

	@Test
	void menuBar() {
		JMenuBar c = new JMenuBar();
		assertEquals( new Color( 0x000009 ), c.getForeground() );
	}

	@Test
	void menu() {
		JMenu c = new JMenu();
		assertEquals( new Color( 0x000010 ), c.getForeground() );
	}

	@Test
	void menuItem() {
		JMenuItem c = new JMenuItem();
		assertEquals( new Color( 0x000011 ), c.getForeground() );
	}

	@Test
	void checkBoxMenuItem() {
		JCheckBoxMenuItem c = new JCheckBoxMenuItem();
		assertEquals( new Color( 0x000012 ), c.getForeground() );
	}

	@Test
	void radioButtonMenuItem() {
		JRadioButtonMenuItem c = new JRadioButtonMenuItem();
		assertEquals( new Color( 0x000013 ), c.getForeground() );
	}

	@Test
	void passwordField() {
		JPasswordField c = new JPasswordField();
		assertEquals( new Color( 0x000014 ), c.getForeground() );
	}

	@Test
	void popupMenu() {
		JPopupMenu c = new JPopupMenu();
		assertEquals( new Color( 0x000015 ), c.getForeground() );
	}

	@Test
	void popupMenuSeparator() {
		JPopupMenu.Separator c = new JPopupMenu.Separator();
		assertEquals( new Color( 0x000016 ), c.getForeground() );
	}

	@Test
	void progressBar() {
		JProgressBar c = new JProgressBar();
		assertEquals( new Color( 0x000017 ), c.getForeground() );
	}

	@Test
	void radioButton() {
		JRadioButton c = new JRadioButton();
		assertEquals( new Color( 0x000018 ), c.getForeground() );
	}

	@Test
	void scrollBar() {
		JScrollBar c = new JScrollBar();
		assertEquals( new Color( 0x000019 ), c.getForeground() );
	}

	@Test
	void scrollPane() {
		JScrollPane c = new JScrollPane();
		assertEquals( new Color( 0x000020 ), c.getForeground() );
	}

	@Test
	void separator() {
		JSeparator c = new JSeparator();
		assertEquals( new Color( 0x000021 ), c.getForeground() );
	}

	@Test
	void slider() {
		JSlider c = new JSlider();
		assertEquals( new Color( 0x000022 ), c.getForeground() );
	}

	@Test
	void slider2() {
		JSlider c = new JSlider();

		// when slider labels are painted, then a Java private subclass of JLabel
		// is used that overrides getForeground(), which is not accessible via reflection
		// see class JSlider.SmartHashtable.LabelUIResource
		c.setPaintLabels( true );
		c.setMajorTickSpacing( 50 );

		assertEquals( new Color( 0x000022 ), c.getForeground() );
	}

	@Test
	void spinner() {
		JSpinner c = new JSpinner();
		assertEquals( new Color( 0x000023 ), c.getForeground() );
	}

	@Test
	void splitPane() {
		JSplitPane c = new JSplitPane();
		assertEquals( new Color( 0x000024 ), c.getForeground() );
	}

	@Test
	void tabbedPane() {
		JTabbedPane c = new JTabbedPane();
		assertEquals( new Color( 0x000025 ), c.getForeground() );
	}

	@Test
	void table() {
		JTable c = new JTable();
		assertEquals( new Color( 0x000026 ), c.getForeground() );
	}

	@Test
	void tableHeader() {
		JTableHeader c = new JTableHeader();
		assertEquals( new Color( 0x000027 ), c.getForeground() );
	}

	@Test
	void textArea() {
		JTextArea c = new JTextArea();
		assertEquals( new Color( 0x000028 ), c.getForeground() );
	}

	@Test
	void textField() {
		JTextField c = new JTextField();
		assertEquals( new Color( 0x000029 ), c.getForeground() );
	}

	@Test
	void textPane() {
		JTextPane c = new JTextPane();
		assertEquals( new Color( 0x000030 ), c.getForeground() );
	}

	@Test
	void toggleButton() {
		JToggleButton c = new JToggleButton();
		assertEquals( new Color( 0x000031 ), c.getForeground() );
	}

	@Test
	void toolBar() {
		JToolBar c = new JToolBar();
		assertEquals( new Color( 0x000032 ), c.getForeground() );
	}

	@Test
	void toolBarSeparator() {
		JToolBar.Separator c = new JToolBar.Separator();
		assertEquals( new Dimension( 0, 21 ), c.getPreferredSize() );
	}

	@Test
	void tree() {
		JTree c = new JTree();
		assertEquals( new Color( 0x000033 ), c.getForeground() );
	}
}
