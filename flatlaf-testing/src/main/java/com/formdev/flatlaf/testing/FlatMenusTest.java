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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatMenusTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatMenusTest" );
			frame.showFrame( FlatMenusTest::new );
		} );
	}

	FlatMenusTest() {
		initComponents();
	}

	private void armedChanged() {
		boolean armed = armedCheckBox.isSelected();
		arm( this, armed );
	}

	private void arm( Container container, boolean armed ) {
		for( Component c : container.getComponents() ) {
			if( c instanceof JMenuItem )
				((JMenuItem)c).setArmed( armed );

			if( c instanceof Container )
				arm( (Container) c, armed );
		}
	}

	private void showPopupMenuButtonActionPerformed(ActionEvent e) {
		Component invoker = (Component) e.getSource();
		PopupMenu popupMenu = new PopupMenu();
		popupMenu.applyComponentOrientation( getComponentOrientation() );
		popupMenu.show( invoker, 0, invoker.getHeight() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel menuBarLabel = new JLabel();
		JMenuBar menuBar1 = new JMenuBar();
		JMenu menu5 = new JMenu();
		JMenuItem menuItem7 = new JMenuItem();
		JMenuItem menuItem8 = new JMenuItem();
		JMenu menu6 = new JMenu();
		JMenuItem menuItem5 = new JMenuItem();
		JMenuItem menuItem6 = new JMenuItem();
		JPanel panel1 = new JPanel();
		JLabel menuLabel = new JLabel();
		JMenu menu1 = new JMenu();
		JLabel menuItemLabel = new JLabel();
		JMenuItem menuItem1 = new JMenuItem();
		JLabel checkBoxMenuItemLabel = new JLabel();
		JCheckBoxMenuItem checkBoxMenuItem1 = new JCheckBoxMenuItem();
		JLabel radioButtonMenuItemLabel = new JLabel();
		JRadioButtonMenuItem radioButtonMenuItem1 = new JRadioButtonMenuItem();
		JLabel popupMenuSeparatorLabel = new JLabel();
		JPopupMenu.Separator separator1 = new JPopupMenu.Separator();
		JPanel panel2 = new JPanel();
		JMenu menu2 = new JMenu();
		JMenuItem menuItem2 = new JMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem2 = new JCheckBoxMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem2 = new JRadioButtonMenuItem();
		JPanel panel3 = new JPanel();
		JMenu menu3 = new JMenu();
		JMenuItem menuItem3 = new JMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem3 = new JCheckBoxMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem3 = new JRadioButtonMenuItem();
		JPanel panel4 = new JPanel();
		JMenu menu4 = new JMenu();
		JMenuItem menuItem4 = new JMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem4 = new JCheckBoxMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem4 = new JRadioButtonMenuItem();
		JLabel popupMenuLabel = new JLabel();
		JButton showPopupMenuButton = new JButton();
		armedCheckBox = new JCheckBox();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[125]" +
			"[]" +
			"[]" +
			"[]" +
			"[]",
			// rows
			"[]" +
			"[top]" +
			"[]" +
			"[]"));

		//---- menuBarLabel ----
		menuBarLabel.setText("JMenuBar:");
		add(menuBarLabel, "cell 0 0");

		//======== menuBar1 ========
		{

			//======== menu5 ========
			{
				menu5.setText("text");

				//---- menuItem7 ----
				menuItem7.setText("text");
				menu5.add(menuItem7);

				//---- menuItem8 ----
				menuItem8.setText("text");
				menu5.add(menuItem8);
			}
			menuBar1.add(menu5);

			//======== menu6 ========
			{
				menu6.setText("text");

				//---- menuItem5 ----
				menuItem5.setText("text");
				menu6.add(menuItem5);

				//---- menuItem6 ----
				menuItem6.setText("text");
				menu6.add(menuItem6);
			}
			menuBar1.add(menu6);
		}
		add(menuBar1, "cell 1 0 4 1,growx");

		//======== panel1 ========
		{
			panel1.setOpaque(false);
			panel1.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3",
				// columns
				"[125,left]" +
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//---- menuLabel ----
			menuLabel.setText("JMenu:");
			panel1.add(menuLabel, "cell 0 0");

			//======== menu1 ========
			{
				menu1.setText("enabled");
			}
			panel1.add(menu1, "cell 1 0");

			//---- menuItemLabel ----
			menuItemLabel.setText("JMenuItem:");
			panel1.add(menuItemLabel, "cell 0 1");

			//---- menuItem1 ----
			menuItem1.setText("enabled");
			menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
			panel1.add(menuItem1, "cell 1 1");

			//---- checkBoxMenuItemLabel ----
			checkBoxMenuItemLabel.setText("JCheckBoxMenuItem:");
			panel1.add(checkBoxMenuItemLabel, "cell 0 2");

			//---- checkBoxMenuItem1 ----
			checkBoxMenuItem1.setText("enabled");
			checkBoxMenuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			panel1.add(checkBoxMenuItem1, "cell 1 2");

			//---- radioButtonMenuItemLabel ----
			radioButtonMenuItemLabel.setText("JRadioButtonMenuItem:");
			panel1.add(radioButtonMenuItemLabel, "cell 0 3");

			//---- radioButtonMenuItem1 ----
			radioButtonMenuItem1.setText("enabled");
			radioButtonMenuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			panel1.add(radioButtonMenuItem1, "cell 1 3");

			//---- popupMenuSeparatorLabel ----
			popupMenuSeparatorLabel.setText("JPopupMenu.Separator:");
			panel1.add(popupMenuSeparatorLabel, "cell 0 4");
			panel1.add(separator1, "cell 1 4");
		}
		add(panel1, "cell 0 1 2 1");

		//======== panel2 ========
		{
			panel2.setOpaque(false);
			panel2.setLayout(new MigLayout(
				"insets 0",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//======== menu2 ========
			{
				menu2.setText("disabled");
				menu2.setEnabled(false);
			}
			panel2.add(menu2, "cell 0 0");

			//---- menuItem2 ----
			menuItem2.setText("disabled");
			menuItem2.setEnabled(false);
			menuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.ALT_MASK|KeyEvent.SHIFT_MASK));
			panel2.add(menuItem2, "cell 0 1");

			//---- checkBoxMenuItem2 ----
			checkBoxMenuItem2.setText("disabled");
			checkBoxMenuItem2.setEnabled(false);
			checkBoxMenuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			panel2.add(checkBoxMenuItem2, "cell 0 2");

			//---- radioButtonMenuItem2 ----
			radioButtonMenuItem2.setText("disabled");
			radioButtonMenuItem2.setEnabled(false);
			radioButtonMenuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			panel2.add(radioButtonMenuItem2, "cell 0 3");
		}
		add(panel2, "cell 2 1");

		//======== panel3 ========
		{
			panel3.setOpaque(false);
			panel3.setLayout(new MigLayout(
				"insets 0",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//======== menu3 ========
			{
				menu3.setText("text");
				menu3.setVisible(false);
			}
			panel3.add(menu3, "cell 0 0");

			//---- menuItem3 ----
			menuItem3.setText("selected");
			menuItem3.setSelected(true);
			menuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			panel3.add(menuItem3, "cell 0 1");

			//---- checkBoxMenuItem3 ----
			checkBoxMenuItem3.setText("selected");
			checkBoxMenuItem3.setSelected(true);
			checkBoxMenuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			panel3.add(checkBoxMenuItem3, "cell 0 2");

			//---- radioButtonMenuItem3 ----
			radioButtonMenuItem3.setText("selected");
			radioButtonMenuItem3.setSelected(true);
			radioButtonMenuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			panel3.add(radioButtonMenuItem3, "cell 0 3");
		}
		add(panel3, "cell 3 1");

		//======== panel4 ========
		{
			panel4.setOpaque(false);
			panel4.setLayout(new MigLayout(
				"insets 0",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//======== menu4 ========
			{
				menu4.setText("text");
				menu4.setVisible(false);
			}
			panel4.add(menu4, "cell 0 0");

			//---- menuItem4 ----
			menuItem4.setText("selected disabled");
			menuItem4.setSelected(true);
			menuItem4.setEnabled(false);
			menuItem4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			panel4.add(menuItem4, "cell 0 1");

			//---- checkBoxMenuItem4 ----
			checkBoxMenuItem4.setText("selected disabled");
			checkBoxMenuItem4.setEnabled(false);
			checkBoxMenuItem4.setSelected(true);
			checkBoxMenuItem4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			panel4.add(checkBoxMenuItem4, "cell 0 2");

			//---- radioButtonMenuItem4 ----
			radioButtonMenuItem4.setText("selected disabled");
			radioButtonMenuItem4.setEnabled(false);
			radioButtonMenuItem4.setSelected(true);
			radioButtonMenuItem4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			panel4.add(radioButtonMenuItem4, "cell 0 3");
		}
		add(panel4, "cell 4 1");

		//---- popupMenuLabel ----
		popupMenuLabel.setText("JPopupMenu:");
		add(popupMenuLabel, "cell 0 2");

		//---- showPopupMenuButton ----
		showPopupMenuButton.setText("show JPopupMenu");
		showPopupMenuButton.addActionListener(e -> showPopupMenuButtonActionPerformed(e));
		add(showPopupMenuButton, "cell 1 2");

		//---- armedCheckBox ----
		armedCheckBox.setText("armed");
		armedCheckBox.setMnemonic('A');
		armedCheckBox.addActionListener(e -> armedChanged());
		add(armedCheckBox, "cell 0 3");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox armedCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	private class PopupMenu extends JPopupMenu {
		private PopupMenu() {
			initComponents();
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			JMenuItem menuItem9 = new JMenuItem();
			JMenuItem menuItem10 = new JMenuItem();
			JCheckBoxMenuItem checkBoxMenuItem5 = new JCheckBoxMenuItem();
			JMenu menu7 = new JMenu();
			JMenuItem menuItem11 = new JMenuItem();
			JMenuItem menuItem12 = new JMenuItem();

			//======== this ========

			//---- menuItem9 ----
			menuItem9.setText("text");
			add(menuItem9);

			//---- menuItem10 ----
			menuItem10.setText("text");
			add(menuItem10);
			addSeparator();

			//---- checkBoxMenuItem5 ----
			checkBoxMenuItem5.setText("text");
			checkBoxMenuItem5.setSelected(true);
			add(checkBoxMenuItem5);

			//======== menu7 ========
			{
				menu7.setText("text");

				//---- menuItem11 ----
				menuItem11.setText("text");
				menu7.add(menuItem11);

				//---- menuItem12 ----
				menuItem12.setText("text");
				menu7.add(menuItem12);
			}
			add(menu7);
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}
}
