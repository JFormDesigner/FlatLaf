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

package com.formdev.flatlaf.demo;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class NewDialog
	extends JDialog
{
	NewDialog( Window owner ) {
		super( owner );
		initComponents();

		// hide menubar, which is here for testing
		menuBar1.setVisible( false );

		getRootPane().setDefaultButton( okButton );

		// register ESC key to close frame
		((JComponent)getContentPane()).registerKeyboardAction(
			e -> dispose(),
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	private void okActionPerformed() {
		System.out.println( "ok" );
		dispose();
	}

	private void cancelActionPerformed() {
		System.out.println( "cancel" );
		dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		textField1 = new JTextField();
		label3 = new JLabel();
		comboBox2 = new JComboBox<>();
		label2 = new JLabel();
		comboBox1 = new JComboBox<>();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		menuItem8 = new JMenuItem();
		menuItem7 = new JMenuItem();
		menuItem6 = new JMenuItem();
		menuItem5 = new JMenuItem();
		menuItem4 = new JMenuItem();
		menuItem3 = new JMenuItem();
		menuItem2 = new JMenuItem();
		menuItem1 = new JMenuItem();
		menu2 = new JMenu();
		menuItem18 = new JMenuItem();
		menuItem17 = new JMenuItem();
		menuItem16 = new JMenuItem();
		menuItem15 = new JMenuItem();
		menuItem14 = new JMenuItem();
		menuItem13 = new JMenuItem();
		menuItem12 = new JMenuItem();
		menuItem11 = new JMenuItem();
		menuItem10 = new JMenuItem();
		menuItem9 = new JMenuItem();
		menu3 = new JMenu();
		menuItem25 = new JMenuItem();
		menuItem26 = new JMenuItem();
		menuItem24 = new JMenuItem();
		menuItem23 = new JMenuItem();
		menuItem22 = new JMenuItem();
		menuItem21 = new JMenuItem();
		menuItem20 = new JMenuItem();
		menuItem19 = new JMenuItem();
		popupMenu1 = new JPopupMenu();
		cutMenuItem = new JMenuItem();
		copyMenuItem = new JMenuItem();
		pasteMenuItem = new JMenuItem();

		//======== this ========
		setTitle("New");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new MigLayout(
					"insets dialog,hidemode 3",
					// columns
					"[fill]" +
					"[grow,fill]",
					// rows
					"[]" +
					"[]" +
					"[]"));

				//---- label1 ----
				label1.setText("Name:");
				contentPanel.add(label1, "cell 0 0");

				//---- textField1 ----
				textField1.setComponentPopupMenu(popupMenu1);
				contentPanel.add(textField1, "cell 1 0");

				//---- label3 ----
				label3.setText("Package:");
				contentPanel.add(label3, "cell 0 1");

				//---- comboBox2 ----
				comboBox2.setEditable(true);
				comboBox2.setModel(new DefaultComboBoxModel<>(new String[] {
					"com.myapp",
					"com.myapp.core",
					"com.myapp.ui",
					"com.myapp.util",
					"com.myapp.extras",
					"com.myapp.components",
					"com.myapp.dialogs",
					"com.myapp.windows"
				}));
				contentPanel.add(comboBox2, "cell 1 1");

				//---- label2 ----
				label2.setText("Type:");
				contentPanel.add(label2, "cell 0 2");

				//---- comboBox1 ----
				comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
					"Class",
					"Interface",
					"Package",
					"Annotation",
					"Enum",
					"Record",
					"Java Project",
					"Project",
					"Folder",
					"File"
				}));
				contentPanel.add(comboBox1, "cell 1 2");
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setLayout(new MigLayout(
					"insets dialog,alignx right",
					// columns
					"[button,fill]" +
					"[button,fill]",
					// rows
					null));

				//---- okButton ----
				okButton.setText("OK");
				okButton.addActionListener(e -> okActionPerformed());
				buttonBar.add(okButton, "cell 0 0");

				//---- cancelButton ----
				cancelButton.setText("Cancel");
				cancelButton.addActionListener(e -> cancelActionPerformed());
				buttonBar.add(cancelButton, "cell 1 0");
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);

			//======== menuBar1 ========
			{

				//======== menu1 ========
				{
					menu1.setText("text");

					//---- menuItem8 ----
					menuItem8.setText("text");
					menu1.add(menuItem8);

					//---- menuItem7 ----
					menuItem7.setText("text");
					menu1.add(menuItem7);

					//---- menuItem6 ----
					menuItem6.setText("text");
					menu1.add(menuItem6);

					//---- menuItem5 ----
					menuItem5.setText("text");
					menu1.add(menuItem5);

					//---- menuItem4 ----
					menuItem4.setText("text");
					menu1.add(menuItem4);

					//---- menuItem3 ----
					menuItem3.setText("text");
					menu1.add(menuItem3);

					//---- menuItem2 ----
					menuItem2.setText("text");
					menu1.add(menuItem2);

					//---- menuItem1 ----
					menuItem1.setText("text");
					menu1.add(menuItem1);
				}
				menuBar1.add(menu1);

				//======== menu2 ========
				{
					menu2.setText("text");

					//---- menuItem18 ----
					menuItem18.setText("text");
					menu2.add(menuItem18);

					//---- menuItem17 ----
					menuItem17.setText("text");
					menu2.add(menuItem17);

					//---- menuItem16 ----
					menuItem16.setText("text");
					menu2.add(menuItem16);

					//---- menuItem15 ----
					menuItem15.setText("text");
					menu2.add(menuItem15);

					//---- menuItem14 ----
					menuItem14.setText("text");
					menu2.add(menuItem14);

					//---- menuItem13 ----
					menuItem13.setText("text");
					menu2.add(menuItem13);

					//---- menuItem12 ----
					menuItem12.setText("text");
					menu2.add(menuItem12);

					//---- menuItem11 ----
					menuItem11.setText("text");
					menu2.add(menuItem11);

					//---- menuItem10 ----
					menuItem10.setText("text");
					menu2.add(menuItem10);

					//---- menuItem9 ----
					menuItem9.setText("text");
					menu2.add(menuItem9);
				}
				menuBar1.add(menu2);

				//======== menu3 ========
				{
					menu3.setText("text");

					//---- menuItem25 ----
					menuItem25.setText("text");
					menu3.add(menuItem25);

					//---- menuItem26 ----
					menuItem26.setText("text");
					menu3.add(menuItem26);

					//---- menuItem24 ----
					menuItem24.setText("text");
					menu3.add(menuItem24);

					//---- menuItem23 ----
					menuItem23.setText("text");
					menu3.add(menuItem23);

					//---- menuItem22 ----
					menuItem22.setText("text");
					menu3.add(menuItem22);

					//---- menuItem21 ----
					menuItem21.setText("text");
					menu3.add(menuItem21);

					//---- menuItem20 ----
					menuItem20.setText("text");
					menu3.add(menuItem20);

					//---- menuItem19 ----
					menuItem19.setText("text");
					menu3.add(menuItem19);
				}
				menuBar1.add(menu3);
			}
			dialogPane.add(menuBar1, BorderLayout.NORTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());

		//======== popupMenu1 ========
		{

			//---- cutMenuItem ----
			cutMenuItem.setText("Cut");
			cutMenuItem.setMnemonic('C');
			popupMenu1.add(cutMenuItem);

			//---- copyMenuItem ----
			copyMenuItem.setText("Copy");
			copyMenuItem.setMnemonic('O');
			popupMenu1.add(copyMenuItem);

			//---- pasteMenuItem ----
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setMnemonic('P');
			popupMenu1.add(pasteMenuItem);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel label1;
	private JTextField textField1;
	private JLabel label3;
	private JComboBox<String> comboBox2;
	private JLabel label2;
	private JComboBox<String> comboBox1;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	private JMenuBar menuBar1;
	private JMenu menu1;
	private JMenuItem menuItem8;
	private JMenuItem menuItem7;
	private JMenuItem menuItem6;
	private JMenuItem menuItem5;
	private JMenuItem menuItem4;
	private JMenuItem menuItem3;
	private JMenuItem menuItem2;
	private JMenuItem menuItem1;
	private JMenu menu2;
	private JMenuItem menuItem18;
	private JMenuItem menuItem17;
	private JMenuItem menuItem16;
	private JMenuItem menuItem15;
	private JMenuItem menuItem14;
	private JMenuItem menuItem13;
	private JMenuItem menuItem12;
	private JMenuItem menuItem11;
	private JMenuItem menuItem10;
	private JMenuItem menuItem9;
	private JMenu menu3;
	private JMenuItem menuItem25;
	private JMenuItem menuItem26;
	private JMenuItem menuItem24;
	private JMenuItem menuItem23;
	private JMenuItem menuItem22;
	private JMenuItem menuItem21;
	private JMenuItem menuItem20;
	private JMenuItem menuItem19;
	private JPopupMenu popupMenu1;
	private JMenuItem cutMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
