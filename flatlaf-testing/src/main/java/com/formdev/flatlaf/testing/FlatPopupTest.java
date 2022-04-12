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

package com.formdev.flatlaf.testing;

import java.awt.*;
import java.awt.Point;
import javax.swing.*;
import com.formdev.flatlaf.util.Animator;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatPopupTest
	extends FlatTestPanel
{
	private Popup popup;

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatPopupTest" );
			frame.showFrame( FlatPopupTest::new );
		} );
	}

	FlatPopupTest() {
		initComponents();
	}

	private void showPopupMenu() {
		popupMenu1.show( showPopupMenuButton, 0, showPopupMenuButton.getHeight() );
	}

	private void showLargePopupMenu() {
		popupMenu2.show( showLargePopupMenuButton, 0, showLargePopupMenuButton.getHeight() );
	}

	private void showPopup() {
		showPopup( 0, 0 );
	}

	private void showPopup( int xoffset, int yoffset ) {
		hidePopup();

		Point pt = showPopupButton.getLocationOnScreen();
		popup = PopupFactory.getSharedInstance().getPopup( showPopupButton, popupPanel,
			pt.x + xoffset, pt.y + showPopupButton.getHeight() + yoffset );
		popup.show();
	}

	private void hidePopup() {
		if( popup == null )
			return;

		popup.hide();
		popup = null;
	}

	private void movePopupDown() {
		movePopup( 0, 600 );
	}

	private void movePopupRight() {
		movePopup( 600, 0 );
	}

	private void movePopup( int xoffset, int yoffset ) {
		showPopup();

		Animator animator = new Animator( 1000, fraction -> {
			System.out.println(fraction);
			showPopup( (int) (fraction * xoffset), (int) (fraction * yoffset) );
		} );
		animator.start();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label1 = new JLabel();
		label2 = new JLabel();
		showPopupMenuButton = new JButton();
		showLargePopupMenuButton = new JButton();
		showPopupButton = new JButton();
		hidePopupButton = new JButton();
		movePopupDownButton = new JButton();
		movePopuprightButton = new JButton();
		label4 = new JLabel();
		popupMenu1 = new JPopupMenu();
		menuItem1 = new JMenuItem();
		menuItem2 = new JMenuItem();
		menu1 = new JMenu();
		menuItem3 = new JMenuItem();
		menuItem4 = new JMenuItem();
		popupPanel = new JPanel();
		label3 = new JLabel();
		popupMenu2 = new JPopupMenu();
		menuItem5 = new JMenuItem();
		menuItem6 = new JMenuItem();
		menuItem7 = new JMenuItem();
		menuItem8 = new JMenuItem();
		menuItem9 = new JMenuItem();
		menuItem10 = new JMenuItem();
		menuItem11 = new JMenuItem();
		menuItem12 = new JMenuItem();
		menuItem13 = new JMenuItem();
		menuItem14 = new JMenuItem();
		menuItem15 = new JMenuItem();
		menuItem16 = new JMenuItem();
		menuItem17 = new JMenuItem();
		menuItem18 = new JMenuItem();
		menuItem19 = new JMenuItem();
		menuItem20 = new JMenuItem();
		menuItem21 = new JMenuItem();
		menu2 = new JMenu();
		menuItem22 = new JMenuItem();
		menuItem23 = new JMenuItem();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
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
			"[]"));

		//---- label1 ----
		label1.setText("Label with light-weight tooltip");
		label1.setToolTipText("some tip");
		add(label1, "cell 0 0");

		//---- label2 ----
		label2.setText("Label with heavy-weight tooltip");
		label2.setToolTipText("some tip\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19\n20\n21\n22\n23\n24\n25");
		add(label2, "cell 0 1");

		//---- showPopupMenuButton ----
		showPopupMenuButton.setText("show light-weight JPopupMenu");
		showPopupMenuButton.addActionListener(e -> showPopupMenu());
		add(showPopupMenuButton, "cell 0 2");

		//---- showLargePopupMenuButton ----
		showLargePopupMenuButton.setText("show heavy-weight JPopupMenu");
		showLargePopupMenuButton.addActionListener(e -> showLargePopupMenu());
		add(showLargePopupMenuButton, "cell 0 3");

		//---- showPopupButton ----
		showPopupButton.setText("show medium-weight popup");
		showPopupButton.addActionListener(e -> showPopup());
		add(showPopupButton, "cell 0 4");

		//---- hidePopupButton ----
		hidePopupButton.setText("hide");
		hidePopupButton.addActionListener(e -> hidePopup());
		add(hidePopupButton, "cell 1 4");

		//---- movePopupDownButton ----
		movePopupDownButton.setText("move down");
		movePopupDownButton.addActionListener(e -> movePopupDown());
		add(movePopupDownButton, "cell 2 4");

		//---- movePopuprightButton ----
		movePopuprightButton.setText("move right");
		movePopuprightButton.addActionListener(e -> movePopupRight());
		add(movePopuprightButton, "cell 3 4");

		//---- label4 ----
		label4.setText("(switches to heavy-weight when moving outside of window)");
		add(label4, "cell 0 5 4 1,alignx right,growx 0");

		//======== popupMenu1 ========
		{

			//---- menuItem1 ----
			menuItem1.setText("text");
			popupMenu1.add(menuItem1);

			//---- menuItem2 ----
			menuItem2.setText("text");
			popupMenu1.add(menuItem2);

			//======== menu1 ========
			{
				menu1.setText("text");

				//---- menuItem3 ----
				menuItem3.setText("text");
				menu1.add(menuItem3);

				//---- menuItem4 ----
				menuItem4.setText("text");
				menu1.add(menuItem4);
			}
			popupMenu1.add(menu1);
		}

		//======== popupPanel ========
		{
			popupPanel.setBackground(new Color(153, 255, 153));
			popupPanel.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]"));

			//---- label3 ----
			label3.setText("popup");
			popupPanel.add(label3, "cell 0 0");
		}

		//======== popupMenu2 ========
		{

			//---- menuItem5 ----
			menuItem5.setText("text");
			popupMenu2.add(menuItem5);

			//---- menuItem6 ----
			menuItem6.setText("text");
			popupMenu2.add(menuItem6);

			//---- menuItem7 ----
			menuItem7.setText("text");
			popupMenu2.add(menuItem7);

			//---- menuItem8 ----
			menuItem8.setText("text");
			popupMenu2.add(menuItem8);

			//---- menuItem9 ----
			menuItem9.setText("text");
			popupMenu2.add(menuItem9);

			//---- menuItem10 ----
			menuItem10.setText("text");
			popupMenu2.add(menuItem10);

			//---- menuItem11 ----
			menuItem11.setText("text");
			popupMenu2.add(menuItem11);

			//---- menuItem12 ----
			menuItem12.setText("text");
			popupMenu2.add(menuItem12);

			//---- menuItem13 ----
			menuItem13.setText("text");
			popupMenu2.add(menuItem13);

			//---- menuItem14 ----
			menuItem14.setText("text");
			popupMenu2.add(menuItem14);

			//---- menuItem15 ----
			menuItem15.setText("text");
			popupMenu2.add(menuItem15);

			//---- menuItem16 ----
			menuItem16.setText("text");
			popupMenu2.add(menuItem16);

			//---- menuItem17 ----
			menuItem17.setText("text");
			popupMenu2.add(menuItem17);

			//---- menuItem18 ----
			menuItem18.setText("text");
			popupMenu2.add(menuItem18);

			//---- menuItem19 ----
			menuItem19.setText("text");
			popupMenu2.add(menuItem19);

			//---- menuItem20 ----
			menuItem20.setText("text");
			popupMenu2.add(menuItem20);

			//---- menuItem21 ----
			menuItem21.setText("text");
			popupMenu2.add(menuItem21);

			//======== menu2 ========
			{
				menu2.setText("text");

				//---- menuItem22 ----
				menuItem22.setText("text");
				menu2.add(menuItem22);

				//---- menuItem23 ----
				menuItem23.setText("text");
				menu2.add(menuItem23);
			}
			popupMenu2.add(menu2);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private JLabel label2;
	private JButton showPopupMenuButton;
	private JButton showLargePopupMenuButton;
	private JButton showPopupButton;
	private JButton hidePopupButton;
	private JButton movePopupDownButton;
	private JButton movePopuprightButton;
	private JLabel label4;
	private JPopupMenu popupMenu1;
	private JMenuItem menuItem1;
	private JMenuItem menuItem2;
	private JMenu menu1;
	private JMenuItem menuItem3;
	private JMenuItem menuItem4;
	private JPanel popupPanel;
	private JLabel label3;
	private JPopupMenu popupMenu2;
	private JMenuItem menuItem5;
	private JMenuItem menuItem6;
	private JMenuItem menuItem7;
	private JMenuItem menuItem8;
	private JMenuItem menuItem9;
	private JMenuItem menuItem10;
	private JMenuItem menuItem11;
	private JMenuItem menuItem12;
	private JMenuItem menuItem13;
	private JMenuItem menuItem14;
	private JMenuItem menuItem15;
	private JMenuItem menuItem16;
	private JMenuItem menuItem17;
	private JMenuItem menuItem18;
	private JMenuItem menuItem19;
	private JMenuItem menuItem20;
	private JMenuItem menuItem21;
	private JMenu menu2;
	private JMenuItem menuItem22;
	private JMenuItem menuItem23;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
