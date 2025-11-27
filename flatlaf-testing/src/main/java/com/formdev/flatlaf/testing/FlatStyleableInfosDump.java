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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.icons.*;
import com.formdev.flatlaf.ui.*;
import com.formdev.flatlaf.ui.FlatInternalFrameUI.FlatInternalFrameBorder;

/**
 * Dumps FlatLaf styleable infos.
 *
 * @author Karl Tauber
 */
public class FlatStyleableInfosDump
{
	public static void main( String[] args ) {
		System.setProperty( "line.separator", "\n" );

		SwingUtilities.invokeLater( () -> {
			printStyleableInfos();
		} );
	}

	private static void printStyleableInfos() {
		FlatLightLaf.setup();

		Map<String, Map<String, Class<?>>> map = collectStyleableInfos();

		// dump to string
		StringWriter stringWriter = new StringWriter( 100000 );
		PrintWriter out = new PrintWriter( stringWriter );
		map.entrySet().forEach( e -> {
			dump( out, e.getKey(), e.getValue() );
		});
		String content = stringWriter.toString().replace( "\r", "" );

		// write to file
		File file = new File( "dumps/styleable-infos.txt" );
		file.getParentFile().mkdirs();
		try( Writer fileWriter = new OutputStreamWriter(
			new FileOutputStream( file ), StandardCharsets.UTF_8 ) )
		{
			fileWriter.write( content );
		} catch( IOException ex ) {
			ex.printStackTrace();
		}
	}

	private static Map<String, Map<String, Class<?>>> collectStyleableInfos() {
		Map<String, Map<String, Class<?>>> map = new LinkedHashMap<>();

		// components
		map.put( "JButton", FlatLaf.getStyleableInfos( new JButton() ) );
		map.put( "JCheckBox", FlatLaf.getStyleableInfos( new JCheckBox() ) );
		map.put( "JCheckBoxMenuItem", FlatLaf.getStyleableInfos( new JCheckBoxMenuItem() ) );
		map.put( "JComboBox", FlatLaf.getStyleableInfos( new JComboBox<>() ) );
		map.put( "JEditorPane", FlatLaf.getStyleableInfos( new JEditorPane() ) );
		map.put( "JFormattedTextField", FlatLaf.getStyleableInfos( new JFormattedTextField() ) );
		map.put( "JInternalFrame", FlatLaf.getStyleableInfos( new JInternalFrame() ) );
		map.put( "JLabel", FlatLaf.getStyleableInfos( new JLabel() ) );
		map.put( "JList", FlatLaf.getStyleableInfos( new JList<>() ) );
		map.put( "JMenu", FlatLaf.getStyleableInfos( new JMenu() ) );
		map.put( "JMenuBar", FlatLaf.getStyleableInfos( new JMenuBar() ) );
		map.put( "JMenuItem", FlatLaf.getStyleableInfos( new JMenuItem() ) );
		map.put( "JPanel", FlatLaf.getStyleableInfos( new JPanel() ) );
		map.put( "JPasswordField", FlatLaf.getStyleableInfos( new JPasswordField() ) );
		map.put( "JPopupMenu", FlatLaf.getStyleableInfos( new JPopupMenu() ) );
		map.put( "JPopupMenu.Separator", FlatLaf.getStyleableInfos( new JPopupMenu.Separator() ) );
		map.put( "JProgressBar", FlatLaf.getStyleableInfos( new JProgressBar() ) );
		map.put( "JRadioButton", FlatLaf.getStyleableInfos( new JRadioButton() ) );
		map.put( "JRadioButtonMenuItem", FlatLaf.getStyleableInfos( new JRadioButtonMenuItem() ) );
		map.put( "JScrollBar", FlatLaf.getStyleableInfos( new JScrollBar() ) );
		map.put( "JScrollPane", FlatLaf.getStyleableInfos( new JScrollPane() ) );
		map.put( "JSeparator", FlatLaf.getStyleableInfos( new JSeparator() ) );
		map.put( "JSlider", FlatLaf.getStyleableInfos( new JSlider() ) );
		map.put( "JSpinner", FlatLaf.getStyleableInfos( new JSpinner() ) );
		map.put( "JSplitPane", FlatLaf.getStyleableInfos( new JSplitPane() ) );
		map.put( "JTabbedPane", FlatLaf.getStyleableInfos( new JTabbedPane() ) );
		map.put( "JTable", FlatLaf.getStyleableInfos( new JTable() ) );
		map.put( "JTableHeader", FlatLaf.getStyleableInfos( new JTableHeader() ) );
		map.put( "JTextArea", FlatLaf.getStyleableInfos( new JTextArea() ) );
		map.put( "JTextField", FlatLaf.getStyleableInfos( new JTextField() ) );
		map.put( "JTextPane", FlatLaf.getStyleableInfos( new JTextPane() ) );
		map.put( "JToggleButton", FlatLaf.getStyleableInfos( new JToggleButton() ) );
		map.put( "JToolBar", FlatLaf.getStyleableInfos( new JToolBar() ) );
		map.put( "JToolBar.Separator", FlatLaf.getStyleableInfos( new JToolBar.Separator() ) );
		map.put( "JTree", FlatLaf.getStyleableInfos( new JTree() ) );

		// borders
		map.put( "FlatBorder", new FlatBorder().getStyleableInfos() );
		map.put( "FlatButtonBorder", new FlatButtonBorder().getStyleableInfos() );
		map.put( "FlatDropShadowBorder", new FlatDropShadowBorder().getStyleableInfos() );
		map.put( "FlatMenuBarBorder", new FlatMenuBarBorder().getStyleableInfos() );
		map.put( "FlatPopupMenuBorder", new FlatPopupMenuBorder().getStyleableInfos() );
		map.put( "FlatRoundBorder", new FlatRoundBorder().getStyleableInfos() );
		map.put( "FlatScrollPaneBorder", new FlatScrollPaneBorder().getStyleableInfos() );
		map.put( "FlatTextBorder", new FlatTextBorder().getStyleableInfos() );
		map.put( "FlatInternalFrameBorder", new FlatInternalFrameBorder().getStyleableInfos() );

		// icons
		map.put( "FlatCapsLockIcon", new FlatCapsLockIcon().getStyleableInfos() );
		map.put( "FlatCheckBoxIcon", new FlatCheckBoxIcon().getStyleableInfos() );
		map.put( "FlatCheckBoxMenuItemIcon", new FlatCheckBoxMenuItemIcon().getStyleableInfos() );
		map.put( "FlatClearIcon", new FlatClearIcon().getStyleableInfos() );
		map.put( "FlatHelpButtonIcon", new FlatHelpButtonIcon().getStyleableInfos() );
		map.put( "FlatMenuArrowIcon", new FlatMenuArrowIcon().getStyleableInfos() );
		map.put( "FlatRadioButtonIcon", new FlatRadioButtonIcon().getStyleableInfos() );
		map.put( "FlatRadioButtonMenuItemIcon", new FlatRadioButtonMenuItemIcon().getStyleableInfos() );
		map.put( "FlatSearchIcon", new FlatSearchIcon().getStyleableInfos() );
		map.put( "FlatSearchWithHistoryIcon", new FlatSearchWithHistoryIcon().getStyleableInfos() );
		map.put( "FlatTabbedPaneCloseIcon", new FlatTabbedPaneCloseIcon().getStyleableInfos() );

		return map;
	}

	private static void dump( PrintWriter out, String name, Map<String, Class<?>> infos ) {
		out.printf( "#---- %s ----%n%n", name );

		infos.entrySet().stream()
			.sorted( (e1, e2) -> e1.getKey().compareToIgnoreCase( e2.getKey() ) )
			.forEach( e -> {
				String key = e.getKey();
				Class<?> value = e.getValue();

				out.printf( "%-50s %s%n", key, value.getName() );
			} );

		out.println();
		out.println();
	}
}
