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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.*;

/**
 * java -classpath "<FlatLaf-root>/flatlaf-testing/bin/main" com.formdev.flatlaf.testing.FlatClassLoaderTest
 *
 * @author Karl Tauber
 */
public class FlatClassLoaderTest
	extends JPanel
{
	public static void main( String[] args ) {
		try {
			Class.forName( "com.formdev.flatlaf.FlatDarkLaf" );
			System.err.println( "Run without FlatLaf on classpath" );
			return;
		} catch( ClassNotFoundException ex ) {
			// continue
		} catch( Exception ex ) {
			ex.printStackTrace();
			return;
		}

		if( FlatClassLoaderTest.class.getResource( "/META-INF/services/com.formdev.flatlaf.FlatDefaultsAddon" ) != null ) {
			System.err.println( "Remove file 'META-INF/services/com.formdev.flatlaf.FlatDefaultsAddon'" );
			return;
		}

		try {
			@SuppressWarnings( "resource" )
			URLClassLoader cl = new URLClassLoader( new URL[] {
				new URL( "file", null, "../flatlaf-core/bin/main/" )
			}, FlatClassLoaderTest.class.getClassLoader() );

			Class<?> lafClass = cl.loadClass( "com.formdev.flatlaf.FlatDarkLaf" );
			LookAndFeel laf = (LookAndFeel) lafClass.getDeclaredConstructor().newInstance();
			UIManager.setLookAndFeel( laf );

			JFrame frame = new JFrame( "FlatClassloaderTest" );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.add( new FlatClassLoaderTest() );
			frame.setBounds( 100, 100, 600, 400 );
			frame.setVisible( true );
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	private FlatClassLoaderTest() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label1 = new JLabel();
		JTextField textField1 = new JTextField();
		JComboBox<String> comboBox1 = new JComboBox<>();
		JButton button1 = new JButton();
		JCheckBox checkBox1 = new JCheckBox();
		JRadioButton radioButton1 = new JRadioButton();
		JToggleButton toggleButton1 = new JToggleButton();
		JScrollPane scrollPane1 = new JScrollPane();
		JTextArea textArea1 = new JTextArea();
		JFormattedTextField formattedTextField1 = new JFormattedTextField();
		JPasswordField passwordField1 = new JPasswordField();
		JScrollPane scrollPane2 = new JScrollPane();
		JTextPane textPane1 = new JTextPane();
		JScrollPane scrollPane3 = new JScrollPane();
		JEditorPane editorPane1 = new JEditorPane();
		JSpinner spinner1 = new JSpinner();
		JScrollPane scrollPane4 = new JScrollPane();
		JList<String> list1 = new JList<>();
		JScrollPane scrollPane5 = new JScrollPane();
		JTable table1 = new JTable();
		JScrollPane scrollPane6 = new JScrollPane();
		JTree tree1 = new JTree();
		JProgressBar progressBar1 = new JProgressBar();
		JScrollBar scrollBar1 = new JScrollBar();
		JSeparator separator1 = new JSeparator();
		JSlider slider1 = new JSlider();
		JPanel panel1 = new JPanel();
		JTabbedPane tabbedPane1 = new JTabbedPane();
		JSplitPane splitPane1 = new JSplitPane();
		JToolBar toolBar1 = new JToolBar();
		JMenuBar menuBar1 = new JMenuBar();
		JMenu menu1 = new JMenu();
		JMenuItem menuItem1 = new JMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem1 = new JCheckBoxMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem1 = new JRadioButtonMenuItem();

		//======== this ========
		setLayout(new FlowLayout());

		//---- label1 ----
		label1.setText("text");
		add(label1);
		add(textField1);
		add(comboBox1);

		//---- button1 ----
		button1.setText("text");
		add(button1);

		//---- checkBox1 ----
		checkBox1.setText("text");
		add(checkBox1);

		//---- radioButton1 ----
		radioButton1.setText("text");
		add(radioButton1);

		//---- toggleButton1 ----
		toggleButton1.setText("text");
		add(toggleButton1);

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(textArea1);
		}
		add(scrollPane1);
		add(formattedTextField1);
		add(passwordField1);

		//======== scrollPane2 ========
		{
			scrollPane2.setViewportView(textPane1);
		}
		add(scrollPane2);

		//======== scrollPane3 ========
		{
			scrollPane3.setViewportView(editorPane1);
		}
		add(scrollPane3);
		add(spinner1);

		//======== scrollPane4 ========
		{
			scrollPane4.setViewportView(list1);
		}
		add(scrollPane4);

		//======== scrollPane5 ========
		{

			//---- table1 ----
			table1.setPreferredScrollableViewportSize(new Dimension(100, 80));
			scrollPane5.setViewportView(table1);
		}
		add(scrollPane5);

		//======== scrollPane6 ========
		{

			//---- tree1 ----
			tree1.setVisibleRowCount(6);
			scrollPane6.setViewportView(tree1);
		}
		add(scrollPane6);
		add(progressBar1);
		add(scrollBar1);
		add(separator1);
		add(slider1);

		//======== panel1 ========
		{
			panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
		}
		add(panel1);
		add(tabbedPane1);
		add(splitPane1);

		//======== toolBar1 ========
		{
			toolBar1.addSeparator();
		}
		add(toolBar1);

		//======== menuBar1 ========
		{

			//======== menu1 ========
			{
				menu1.setText("text");

				//---- menuItem1 ----
				menuItem1.setText("text");
				menu1.add(menuItem1);

				//---- checkBoxMenuItem1 ----
				checkBoxMenuItem1.setText("text");
				menu1.add(checkBoxMenuItem1);

				//---- radioButtonMenuItem1 ----
				radioButtonMenuItem1.setText("text");
				menu1.add(radioButtonMenuItem1);
				menu1.addSeparator();
			}
			menuBar1.add(menu1);
		}
		add(menuBar1);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
