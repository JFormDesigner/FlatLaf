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
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatMnemonicsTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatMnemonicsTest" );
			frame.showFrame( FlatMnemonicsTest::new, panel -> ((FlatMnemonicsTest)panel).menuBar );
		} );
	}

	FlatMnemonicsTest() {
		initComponents();
	}

	private void menuItemActionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater( () -> {
			JOptionPane.showMessageDialog( this, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE );
		} );
	}

	private void textArea1KeyReleased(KeyEvent e) {
		if( e.getKeyCode() == KeyEvent.VK_ALT ) {
			System.out.println( "++++ consume Alt key on release +++++++++++++++++++++++++++++++++++++++++++++++++++++" );
			e.consume();
		}
	}

	private void alwaysShowMnemonicsChanged() {
		UIManager.put( "Component.hideMnemonics", !alwaysShowMnemonicsCheckBox.isSelected() );
		SwingUtilities.windowForComponent( this ).repaint();
	}

	private void openDialog() {
		JOptionPane.showMessageDialog( this, new FlatMnemonicsTest() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label1 = new JLabel();
		JTextField textField1 = new JTextField();
		JLabel label2 = new JLabel();
		JTextField textField2 = new JTextField();
		JLabel label3 = new JLabel();
		JComboBox<String> comboBox1 = new JComboBox<>();
		JCheckBox checkBox1 = new JCheckBox();
		JButton button1 = new JButton();
		JLabel label4 = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		JTextArea textArea1 = new JTextArea();
		JTabbedPane tabbedPane1 = new JTabbedPane();
		JPanel panel1 = new JPanel();
		JLabel label7 = new JLabel();
		JPanel panel2 = new JPanel();
		JLabel label6 = new JLabel();
		JPanel panel3 = new JPanel();
		JLabel label5 = new JLabel();
		alwaysShowMnemonicsCheckBox = new JCheckBox();
		JButton button2 = new JButton();
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu();
		JMenuItem newMenuItem = new JMenuItem();
		JMenuItem openMenuItem = new JMenuItem();
		JMenuItem closeMenuItem = new JMenuItem();
		JMenuItem closeMenuItem2 = new JMenuItem();
		JMenuItem exitMenuItem = new JMenuItem();
		JMenu editMenu = new JMenu();
		JMenuItem undoMenuItem = new JMenuItem();
		JMenuItem redoMenuItem = new JMenuItem();
		JMenuItem cutMenuItem = new JMenuItem();
		JMenuItem copyMenuItem = new JMenuItem();
		JMenuItem pasteMenuItem = new JMenuItem();
		JMenuItem deleteMenuItem = new JMenuItem();
		JMenu viewMenu = new JMenu();
		JCheckBoxMenuItem checkBoxMenuItem1 = new JCheckBoxMenuItem();
		JMenu menu1 = new JMenu();
		JMenu subViewsMenu = new JMenu();
		JMenu subSubViewsMenu = new JMenu();
		JMenuItem errorLogViewMenuItem = new JMenuItem();
		JMenuItem searchViewMenuItem = new JMenuItem();
		JMenuItem projectViewMenuItem = new JMenuItem();
		JMenuItem structureViewMenuItem = new JMenuItem();
		JMenuItem propertiesViewMenuItem = new JMenuItem();
		JMenu helpMenu = new JMenu();
		JMenuItem aboutMenuItem = new JMenuItem();
		JPopupMenu popupMenu1 = new JPopupMenu();
		JMenuItem cutMenuItem2 = new JMenuItem();
		JMenuItem copyMenuItem2 = new JMenuItem();
		JMenuItem pasteMenuItem2 = new JMenuItem();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[150,fill]para" +
			"[300,grow,fill]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]para" +
			"[]" +
			"[]para" +
			"[]"));

		//---- label1 ----
		label1.setText("Name");
		label1.setLabelFor(textField1);
		label1.setDisplayedMnemonic('N');
		add(label1, "cell 0 0,alignx left,growx 0");

		//---- textField1 ----
		textField1.setComponentPopupMenu(popupMenu1);
		add(textField1, "cell 1 0");

		//---- label2 ----
		label2.setText("Phone");
		label2.setLabelFor(textField2);
		label2.setDisplayedMnemonic('P');
		add(label2, "cell 0 1");

		//---- textField2 ----
		textField2.setComponentPopupMenu(popupMenu1);
		add(textField2, "cell 1 1");

		//---- label3 ----
		label3.setText("Planet");
		label3.setDisplayedMnemonic('A');
		label3.setLabelFor(comboBox1);
		add(label3, "cell 0 2");

		//---- comboBox1 ----
		comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
			"Earth",
			"Moon",
			"Mars"
		}));
		comboBox1.setComponentPopupMenu(popupMenu1);
		add(comboBox1, "cell 1 2");

		//---- checkBox1 ----
		checkBox1.setText("Astronaut");
		checkBox1.setMnemonic('S');
		checkBox1.setComponentPopupMenu(popupMenu1);
		add(checkBox1, "cell 1 3");

		//---- button1 ----
		button1.setText("Lift off");
		button1.setMnemonic('L');
		button1.setComponentPopupMenu(popupMenu1);
		add(button1, "cell 1 4,alignx left,growx 0");

		//---- label4 ----
		label4.setText("Text area that consumes Alt key:");
		label4.setLabelFor(textArea1);
		label4.setDisplayedMnemonic('T');
		add(label4, "cell 1 5");

		//======== scrollPane1 ========
		{

			//---- textArea1 ----
			textArea1.setRows(4);
			textArea1.setComponentPopupMenu(popupMenu1);
			textArea1.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					textArea1KeyReleased(e);
				}
			});
			scrollPane1.setViewportView(textArea1);
		}
		add(scrollPane1, "cell 1 6,grow");

		//======== tabbedPane1 ========
		{

			//======== panel1 ========
			{
				panel1.setLayout(new FlowLayout());

				//---- label7 ----
				label7.setText("Guitar");
				panel1.add(label7);
			}
			tabbedPane1.addTab("Guitar", panel1);
			tabbedPane1.setMnemonicAt(0, 'G');

			//======== panel2 ========
			{
				panel2.setLayout(new FlowLayout());

				//---- label6 ----
				label6.setText("Drums");
				panel2.add(label6);
			}
			tabbedPane1.addTab("Drums", panel2);
			tabbedPane1.setMnemonicAt(1, 'D');

			//======== panel3 ========
			{
				panel3.setLayout(new FlowLayout());

				//---- label5 ----
				label5.setText("Keyboard");
				panel3.add(label5);
			}
			tabbedPane1.addTab("Keyboard", panel3);
			tabbedPane1.setMnemonicAt(2, 'K');
		}
		add(tabbedPane1, "cell 2 6,aligny top,growy 0");

		//---- alwaysShowMnemonicsCheckBox ----
		alwaysShowMnemonicsCheckBox.setText("Always show mnemonics");
		alwaysShowMnemonicsCheckBox.setMnemonic('M');
		alwaysShowMnemonicsCheckBox.putClientProperty("FlatLaf.internal.testing.ignore", true);
		alwaysShowMnemonicsCheckBox.addActionListener(e -> alwaysShowMnemonicsChanged());
		add(alwaysShowMnemonicsCheckBox, "cell 0 7 2 1,alignx left,growx 0");

		//---- button2 ----
		button2.setText("Open Dialog");
		button2.putClientProperty("FlatLaf.internal.testing.ignore", true);
		button2.addActionListener(e -> openDialog());
		add(button2, "cell 2 7,alignx left,growx 0");

		//======== menuBar ========
		{

			//======== fileMenu ========
			{
				fileMenu.setText("File");
				fileMenu.setMnemonic('F');

				//---- newMenuItem ----
				newMenuItem.setText("New");
				newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK));
				newMenuItem.setMnemonic('N');
				newMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(newMenuItem);

				//---- openMenuItem ----
				openMenuItem.setText("Open");
				openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.ALT_DOWN_MASK));
				openMenuItem.setMnemonic('O');
				openMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(openMenuItem);
				fileMenu.addSeparator();

				//---- closeMenuItem ----
				closeMenuItem.setText("Close");
				closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK|KeyEvent.ALT_DOWN_MASK));
				closeMenuItem.setMnemonic('C');
				closeMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(closeMenuItem);

				//---- closeMenuItem2 ----
				closeMenuItem2.setText("Close All");
				closeMenuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.ALT_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK));
				closeMenuItem2.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(closeMenuItem2);
				fileMenu.addSeparator();

				//---- exitMenuItem ----
				exitMenuItem.setText("Exit");
				exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()|KeyEvent.ALT_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK));
				exitMenuItem.setMnemonic('X');
				exitMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(exitMenuItem);
			}
			menuBar.add(fileMenu);

			//======== editMenu ========
			{
				editMenu.setText("Edit");
				editMenu.setMnemonic('E');

				//---- undoMenuItem ----
				undoMenuItem.setText("Undo");
				undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				undoMenuItem.setMnemonic('U');
				undoMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				editMenu.add(undoMenuItem);

				//---- redoMenuItem ----
				redoMenuItem.setText("Redo");
				redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				redoMenuItem.setMnemonic('R');
				redoMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				editMenu.add(redoMenuItem);
				editMenu.addSeparator();

				//---- cutMenuItem ----
				cutMenuItem.setText("Cut");
				cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				cutMenuItem.setMnemonic('C');
				editMenu.add(cutMenuItem);

				//---- copyMenuItem ----
				copyMenuItem.setText("Copy");
				copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				copyMenuItem.setMnemonic('O');
				editMenu.add(copyMenuItem);

				//---- pasteMenuItem ----
				pasteMenuItem.setText("Paste");
				pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				pasteMenuItem.setMnemonic('P');
				editMenu.add(pasteMenuItem);
				editMenu.addSeparator();

				//---- deleteMenuItem ----
				deleteMenuItem.setText("Delete");
				deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
				deleteMenuItem.setMnemonic('D');
				deleteMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				editMenu.add(deleteMenuItem);
			}
			menuBar.add(editMenu);

			//======== viewMenu ========
			{
				viewMenu.setText("View");
				viewMenu.setMnemonic('V');

				//---- checkBoxMenuItem1 ----
				checkBoxMenuItem1.setText("Show Toolbar");
				checkBoxMenuItem1.setSelected(true);
				checkBoxMenuItem1.setMnemonic('T');
				checkBoxMenuItem1.addActionListener(e -> menuItemActionPerformed(e));
				viewMenu.add(checkBoxMenuItem1);

				//======== menu1 ========
				{
					menu1.setText("Show View");
					menu1.setMnemonic('V');

					//======== subViewsMenu ========
					{
						subViewsMenu.setText("Sub Views");
						subViewsMenu.setMnemonic('S');

						//======== subSubViewsMenu ========
						{
							subSubViewsMenu.setText("Sub sub Views");
							subSubViewsMenu.setMnemonic('U');

							//---- errorLogViewMenuItem ----
							errorLogViewMenuItem.setText("Error Log");
							errorLogViewMenuItem.setMnemonic('E');
							errorLogViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
							subSubViewsMenu.add(errorLogViewMenuItem);
						}
						subViewsMenu.add(subSubViewsMenu);

						//---- searchViewMenuItem ----
						searchViewMenuItem.setText("Search");
						searchViewMenuItem.setMnemonic('S');
						searchViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
						subViewsMenu.add(searchViewMenuItem);
					}
					menu1.add(subViewsMenu);

					//---- projectViewMenuItem ----
					projectViewMenuItem.setText("Project");
					projectViewMenuItem.setMnemonic('P');
					projectViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
					menu1.add(projectViewMenuItem);

					//---- structureViewMenuItem ----
					structureViewMenuItem.setText("Structure");
					structureViewMenuItem.setMnemonic('T');
					structureViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
					menu1.add(structureViewMenuItem);

					//---- propertiesViewMenuItem ----
					propertiesViewMenuItem.setText("Properties");
					propertiesViewMenuItem.setMnemonic('O');
					propertiesViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
					menu1.add(propertiesViewMenuItem);
				}
				viewMenu.add(menu1);
			}
			menuBar.add(viewMenu);

			//======== helpMenu ========
			{
				helpMenu.setText("Help");
				helpMenu.setMnemonic('H');

				//---- aboutMenuItem ----
				aboutMenuItem.setText("About");
				aboutMenuItem.setMnemonic('A');
				aboutMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				helpMenu.add(aboutMenuItem);
			}
			menuBar.add(helpMenu);
		}

		//======== popupMenu1 ========
		{

			//---- cutMenuItem2 ----
			cutMenuItem2.setText("Cut");
			cutMenuItem2.setMnemonic('C');
			popupMenu1.add(cutMenuItem2);

			//---- copyMenuItem2 ----
			copyMenuItem2.setText("Copy");
			copyMenuItem2.setMnemonic('O');
			popupMenu1.add(copyMenuItem2);

			//---- pasteMenuItem2 ----
			pasteMenuItem2.setText("Paste");
			pasteMenuItem2.setMnemonic('P');
			popupMenu1.add(pasteMenuItem2);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox alwaysShowMnemonicsCheckBox;
	private JMenuBar menuBar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
