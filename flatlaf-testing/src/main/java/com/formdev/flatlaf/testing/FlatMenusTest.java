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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.util.UIScale;
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
			frame.useApplyComponentOrientation = true;
			frame.showFrame( FlatMenusTest::new );
		} );
	}

	FlatMenusTest() {
		initComponents();

		largerCheckBox.setSelected( LargerMenuItem.useLargerSize );
		verticalMenuBar.setLayout( new GridLayout( 0, 1 ) );
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

	private void underlineChanged() {
		UIManager.put( "MenuItem.selectionType", underlineCheckBox.isSelected() ? "underline" : null );

		if( armedCheckBox.isSelected() )
			FlatLaf.updateUI();
	}

	private void popupMenubackgroundChanged() {
		UIManager.put( "PopupMenu.background", popupMenubackgroundCheckBox.isSelected()
			? new ColorUIResource( Color.yellow )
			: null );
		FlatLaf.updateUI();
	}

	private void showPopupMenuButton(ActionEvent e) {
		Component invoker = (Component) e.getSource();
		PopupMenu popupMenu = new PopupMenu();
		popupMenu.applyComponentOrientation( getComponentOrientation() );
		popupMenu.show( invoker, 0, invoker.getHeight() );
	}

	private void showScrollingPopupMenu(ActionEvent e) {
		Component invoker = (Component) e.getSource();
		JPopupMenu popupMenu = new JPopupMenu();
		for( int i = 1; i <= 100; i++ ) {
			popupMenu.add( "menu item " + i + (i % 5 == 0 ? " test" : "") )
				.addActionListener( e2 -> System.out.println( ((JMenuItem)e2.getSource()).getText() ) );
		}
		popupMenu.applyComponentOrientation( getComponentOrientation() );
		popupMenu.show( invoker, 0, invoker.getHeight() );
	}

	private void largerChanged() {
		LargerMenuItem.useLargerSize = largerCheckBox.isSelected();
		menuBar2.revalidate();
	}

	private void accelChanged() {
		updateAccel( menuBar2, () -> {
			return accelCheckBox.isSelected() ? getRandomKeyStroke() : null;
		} );
	}

	private void updateAccel( Component c, Supplier<KeyStroke> keyStrokeSupplier ) {
		if( c instanceof JMenuItem && !(c instanceof JMenu) )
			((JMenuItem)c).setAccelerator( keyStrokeSupplier.get() );

		if( c instanceof Container ) {
			for( Component c2 : ((Container)c).getComponents() )
				updateAccel( c2, keyStrokeSupplier );
		}
		if( c instanceof JMenu ) {
			randomKeyStrokeIndex = 0;
			JMenu menu = (JMenu) c;
			int itemCount = menu.getItemCount();
			for( int i = 0; i < itemCount; i++ )
				updateAccel( menu.getItem( i ), keyStrokeSupplier );
		}
	}

	private KeyStroke getRandomKeyStroke() {
		if( randomKeyStrokeIndex >= randomKeyStrokes.length )
			randomKeyStrokeIndex = 0;
		return randomKeyStrokes[randomKeyStrokeIndex++];
	}

	private int randomKeyStrokeIndex = 0;
	private final KeyStroke[] randomKeyStrokes = {
		KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ),
		KeyStroke.getKeyStroke( KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK ),
		KeyStroke.getKeyStroke( KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK ),
		KeyStroke.getKeyStroke( KeyEvent.VK_BACK_SPACE, 0 ),
		KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP, 0 ),
		KeyStroke.getKeyStroke( KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK ),
		KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ),
		KeyStroke.getKeyStroke( KeyEvent.VK_F10, 0 ),
		KeyStroke.getKeyStroke( KeyEvent.VK_0, 0 ),
	};

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel menuBarLabel = new JLabel();
		JMenuBar menuBar1 = new JMenuBar();
		JMenu menu5 = new JMenu();
		JMenuItem menuItem35 = new JMenuItem();
		JMenuItem menuItem7 = new JMenuItem();
		JMenuItem menuItem34 = new JMenuItem();
		JMenuItem menuItem8 = new JMenuItem();
		JMenuItem menuItem38 = new JMenuItem();
		JMenu menu11 = new JMenu();
		JMenuItem menuItem36 = new JMenuItem();
		JMenuItem menuItem37 = new JMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem6 = new JCheckBoxMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem7 = new JCheckBoxMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem8 = new JCheckBoxMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem5 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem6 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem8 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem9 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem10 = new JRadioButtonMenuItem();
		JMenu menu6 = new JMenu();
		JMenuItem menuItem5 = new JMenuItem();
		JMenuItem menuItem6 = new JMenuItem();
		FlatMenusTest.MenuWithAccelerator menuWithAccelerator1 = new FlatMenusTest.MenuWithAccelerator();
		FlatMenusTest.MenuWithAccelerator menuWithAccelerator2 = new FlatMenusTest.MenuWithAccelerator();
		JMenuItem menuItem40 = new JMenuItem();
		JMenuItem menuItem39 = new JMenuItem();
		JMenu menu12 = new JMenu();
		JMenuItem menuItem41 = new JMenuItem();
		JMenuItem menuItem42 = new JMenuItem();
		JMenuItem menuItem43 = new JMenuItem();
		JMenuItem menuItem44 = new JMenuItem();
		JMenuItem menuItem45 = new JMenuItem();
		JMenuItem menuItem46 = new JMenuItem();
		JMenuItem menuItem47 = new JMenuItem();
		JMenu menu13 = new JMenu();
		JMenuItem menuItem48 = new JMenuItem();
		JMenuItem menuItem49 = new JMenuItem();
		JMenuItem menuItem50 = new JMenuItem();
		menuBar2 = new JMenuBar();
		JMenu menu8 = new JMenu();
		FlatMenusTest.LargerMenuItem menuItem13 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem14 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem27 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem15 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem16 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem28 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem18 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem17 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem19 = new FlatMenusTest.LargerMenuItem();
		JMenuItem menuItem31 = new JMenuItem();
		JMenu menu9 = new JMenu();
		FlatMenusTest.LargerMenuItem menuItem20 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem21 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem29 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem22 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem23 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem30 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem25 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem24 = new FlatMenusTest.LargerMenuItem();
		FlatMenusTest.LargerMenuItem menuItem26 = new FlatMenusTest.LargerMenuItem();
		JMenuItem menuItem32 = new JMenuItem();
		JMenu menu10 = new JMenu();
		FlatMenusTest.LargerMenuItem menuItem33 = new FlatMenusTest.LargerMenuItem();
		JPanel panel5 = new JPanel();
		largerCheckBox = new JCheckBox();
		accelCheckBox = new JCheckBox();
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
		JCheckBoxMenuItem checkBoxMenuItem9 = new JCheckBoxMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem7 = new JRadioButtonMenuItem();
		JPanel panel4 = new JPanel();
		JMenu menu4 = new JMenu();
		JMenuItem menuItem4 = new JMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem4 = new JCheckBoxMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem4 = new JRadioButtonMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem10 = new JCheckBoxMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem11 = new JRadioButtonMenuItem();
		JLabel popupMenuLabel = new JLabel();
		JButton showPopupMenuButton = new JButton();
		showScrollingPopupMenuButton = new JButton();
		JLabel label1 = new JLabel();
		armedCheckBox = new JCheckBox();
		verticalMenuBar = new JMenuBar();
		JMenu menu14 = new JMenu();
		JMenuItem menuItem53 = new JMenuItem();
		JMenu menu15 = new JMenu();
		JMenuItem menuItem54 = new JMenuItem();
		JMenu menu16 = new JMenu();
		JMenuItem menuItem55 = new JMenuItem();
		underlineCheckBox = new JCheckBox();
		popupMenubackgroundCheckBox = new JCheckBox();

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
			"[]" +
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
				menu5.setMnemonic('T');
				menu5.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess.png")));

				//---- menuItem35 ----
				menuItem35.setText("text");
				menu5.add(menuItem35);

				//---- menuItem7 ----
				menuItem7.setText("text");
				menuItem7.setMnemonic('X');
				menuItem7.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showWriteAccess.png")));
				menuItem7.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK));
				menu5.add(menuItem7);

				//---- menuItem34 ----
				menuItem34.setText("longer text longer text");
				menuItem34.setMnemonic('E');
				menuItem34.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
				menu5.add(menuItem34);

				//---- menuItem8 ----
				menuItem8.setText("longer text longer text longer");
				menuItem8.setMnemonic('E');
				menu5.add(menuItem8);

				//---- menuItem38 ----
				menuItem38.setText("<html>some <b color=\"red\">HTML</b> <i color=\"blue\">text</i></html>");
				menu5.add(menuItem38);

				//======== menu11 ========
				{
					menu11.setText("sub menu");

					//---- menuItem36 ----
					menuItem36.setText("text");
					menu11.add(menuItem36);

					//---- menuItem37 ----
					menuItem37.setText("text");
					menu11.add(menuItem37);
				}
				menu5.add(menu11);
				menu5.addSeparator();

				//---- checkBoxMenuItem6 ----
				checkBoxMenuItem6.setText("check");
				checkBoxMenuItem6.setSelected(true);
				menu5.add(checkBoxMenuItem6);

				//---- checkBoxMenuItem7 ----
				checkBoxMenuItem7.setText("check with icon");
				checkBoxMenuItem7.setSelected(true);
				checkBoxMenuItem7.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess.png")));
				menu5.add(checkBoxMenuItem7);

				//---- checkBoxMenuItem8 ----
				checkBoxMenuItem8.setText("check with larger icon");
				checkBoxMenuItem8.setSelected(true);
				menu5.add(checkBoxMenuItem8);
				menu5.addSeparator();

				//---- radioButtonMenuItem5 ----
				radioButtonMenuItem5.setText("radio 1");
				radioButtonMenuItem5.setSelected(true);
				menu5.add(radioButtonMenuItem5);

				//---- radioButtonMenuItem6 ----
				radioButtonMenuItem6.setText("radio 2");
				menu5.add(radioButtonMenuItem6);
				menu5.addSeparator();

				//---- radioButtonMenuItem8 ----
				radioButtonMenuItem8.setText("radio with icon 1");
				radioButtonMenuItem8.setSelected(true);
				radioButtonMenuItem8.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess.png")));
				menu5.add(radioButtonMenuItem8);

				//---- radioButtonMenuItem9 ----
				radioButtonMenuItem9.setText("radio with icon 2");
				radioButtonMenuItem9.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showWriteAccess.png")));
				menu5.add(radioButtonMenuItem9);

				//---- radioButtonMenuItem10 ----
				radioButtonMenuItem10.setText("radio with larger icon");
				menu5.add(radioButtonMenuItem10);
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

			//======== menuWithAccelerator1 ========
			{
				menuWithAccelerator1.setText("text");
				menuWithAccelerator1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));

				//======== menuWithAccelerator2 ========
				{
					menuWithAccelerator2.setText("text");
					menuWithAccelerator2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));

					//---- menuItem40 ----
					menuItem40.setText("text");
					menuWithAccelerator2.add(menuItem40);
				}
				menuWithAccelerator1.add(menuWithAccelerator2);

				//---- menuItem39 ----
				menuItem39.setText("text");
				menuItem39.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
				menuWithAccelerator1.add(menuItem39);
			}
			menuBar1.add(menuWithAccelerator1);

			//======== menu12 ========
			{
				menu12.setText("icons");

				//---- menuItem41 ----
				menuItem41.setText("selected icon");
				menuItem41.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-cut.png")));
				menuItem41.setSelectedIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-show_dark.png")));
				menu12.add(menuItem41);

				//---- menuItem42 ----
				menuItem42.setText("disabled icon");
				menuItem42.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-cut.png")));
				menuItem42.setDisabledIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste.png")));
				menuItem42.setEnabled(false);
				menu12.add(menuItem42);
				menu12.addSeparator();

				//---- menuItem43 ----
				menuItem43.setText("text");
				menuItem43.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/test16.png")));
				menu12.add(menuItem43);

				//---- menuItem44 ----
				menuItem44.setText("text");
				menuItem44.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/test24.png")));
				menu12.add(menuItem44);

				//---- menuItem45 ----
				menuItem45.setText("text");
				menuItem45.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/test32.png")));
				menu12.add(menuItem45);

				//---- menuItem46 ----
				menuItem46.setText("text");
				menuItem46.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/test48.png")));
				menu12.add(menuItem46);

				//---- menuItem47 ----
				menuItem47.setText("text");
				menuItem47.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/test64.png")));
				menu12.add(menuItem47);
			}
			menuBar1.add(menu12);

			//======== menu13 ========
			{
				menu13.setText("HTML");

				//---- menuItem48 ----
				menuItem48.setText("<html>some <b color=\"red\">HTML</b> <i color=\"blue\">text</i></html>");
				menu13.add(menuItem48);

				//---- menuItem49 ----
				menuItem49.setText("<html>some longer <b color=\"red\">HTML</b> <i color=\"blue\">text</i></html>");
				menu13.add(menuItem49);

				//---- menuItem50 ----
				menuItem50.setText("<html>another <b color=\"red\">HTML</b> <i color=\"blue\">text</i></html>");
				menu13.add(menuItem50);
			}
			menuBar1.add(menu13);
		}
		add(menuBar1, "cell 1 0 2 1,growx");

		//======== menuBar2 ========
		{

			//======== menu8 ========
			{
				menu8.setText("text position");
				menu8.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste.png")));
				menu8.setHorizontalTextPosition(SwingConstants.CENTER);
				menu8.setVerticalTextPosition(SwingConstants.BOTTOM);

				//---- menuItem13 ----
				menuItem13.setText("vert top");
				menuItem13.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem13.setVerticalTextPosition(SwingConstants.TOP);
				menu8.add(menuItem13);

				//---- menuItem14 ----
				menuItem14.setText("vert bottom");
				menuItem14.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem14.setVerticalTextPosition(SwingConstants.BOTTOM);
				menu8.add(menuItem14);

				//---- menuItem27 ----
				menuItem27.setText("horz leading");
				menuItem27.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem27.setHorizontalTextPosition(SwingConstants.LEADING);
				menu8.add(menuItem27);

				//---- menuItem15 ----
				menuItem15.setText("horz left");
				menuItem15.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem15.setHorizontalTextPosition(SwingConstants.LEFT);
				menu8.add(menuItem15);

				//---- menuItem16 ----
				menuItem16.setText("horz right");
				menuItem16.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem16.setHorizontalTextPosition(SwingConstants.RIGHT);
				menu8.add(menuItem16);

				//---- menuItem28 ----
				menuItem28.setText("horz trailing");
				menuItem28.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menu8.add(menuItem28);

				//---- menuItem18 ----
				menuItem18.setText("horz center / vert top");
				menuItem18.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem18.setHorizontalTextPosition(SwingConstants.CENTER);
				menuItem18.setVerticalTextPosition(SwingConstants.TOP);
				menu8.add(menuItem18);

				//---- menuItem17 ----
				menuItem17.setText("horz center / vert bottom");
				menuItem17.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem17.setHorizontalTextPosition(SwingConstants.CENTER);
				menuItem17.setVerticalTextPosition(SwingConstants.BOTTOM);
				menu8.add(menuItem17);

				//---- menuItem19 ----
				menuItem19.setText("horz center / vert center");
				menuItem19.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem19.setHorizontalTextPosition(SwingConstants.CENTER);
				menu8.add(menuItem19);

				//---- menuItem31 ----
				menuItem31.setText("1234567890123456789012345678901234567890");
				menu8.add(menuItem31);
			}
			menuBar2.add(menu8);

			//======== menu9 ========
			{
				menu9.setText("alignment");
				menu9.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste.png")));
				menu9.setHorizontalTextPosition(SwingConstants.CENTER);
				menu9.setVerticalTextPosition(SwingConstants.TOP);

				//---- menuItem20 ----
				menuItem20.setText("vert top");
				menuItem20.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem20.setVerticalAlignment(SwingConstants.TOP);
				menu9.add(menuItem20);

				//---- menuItem21 ----
				menuItem21.setText("vert bottom");
				menuItem21.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem21.setVerticalAlignment(SwingConstants.BOTTOM);
				menu9.add(menuItem21);

				//---- menuItem29 ----
				menuItem29.setText("horz leading");
				menuItem29.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menu9.add(menuItem29);

				//---- menuItem22 ----
				menuItem22.setText("horz left");
				menuItem22.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem22.setHorizontalAlignment(SwingConstants.LEFT);
				menu9.add(menuItem22);

				//---- menuItem23 ----
				menuItem23.setText("horz right");
				menuItem23.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem23.setHorizontalAlignment(SwingConstants.RIGHT);
				menu9.add(menuItem23);

				//---- menuItem30 ----
				menuItem30.setText("horz trailing");
				menuItem30.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem30.setHorizontalAlignment(SwingConstants.TRAILING);
				menu9.add(menuItem30);

				//---- menuItem25 ----
				menuItem25.setText("horz center / vert top");
				menuItem25.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem25.setHorizontalAlignment(SwingConstants.CENTER);
				menuItem25.setVerticalAlignment(SwingConstants.TOP);
				menu9.add(menuItem25);

				//---- menuItem24 ----
				menuItem24.setText("horz center / vert bottom");
				menuItem24.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem24.setHorizontalAlignment(SwingConstants.CENTER);
				menuItem24.setVerticalAlignment(SwingConstants.BOTTOM);
				menu9.add(menuItem24);

				//---- menuItem26 ----
				menuItem26.setText("horz center / vert center");
				menuItem26.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menuItem26.setHorizontalAlignment(SwingConstants.CENTER);
				menu9.add(menuItem26);

				//---- menuItem32 ----
				menuItem32.setText("1234567890123456789012345678901234567890");
				menu9.add(menuItem32);
			}
			menuBar2.add(menu9);

			//======== menu10 ========
			{
				menu10.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menu10.setHorizontalTextPosition(SwingConstants.CENTER);
				menu10.setVerticalTextPosition(SwingConstants.TOP);

				//---- menuItem33 ----
				menuItem33.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png")));
				menu10.add(menuItem33);
			}
			menuBar2.add(menu10);
		}
		add(menuBar2, "cell 3 0 2 1,growx");

		//======== panel5 ========
		{
			panel5.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3",
				// columns
				"[]",
				// rows
				"[]0" +
				"[]"));

			//---- largerCheckBox ----
			largerCheckBox.setText("larger");
			largerCheckBox.addActionListener(e -> largerChanged());
			panel5.add(largerCheckBox, "cell 0 0");

			//---- accelCheckBox ----
			accelCheckBox.setText("accel");
			accelCheckBox.addActionListener(e -> accelChanged());
			panel5.add(accelCheckBox, "cell 0 1");
		}
		add(panel5, "cell 3 0 2 1");

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
				menu1.setMnemonic('E');
			}
			panel1.add(menu1, "cell 1 0");

			//---- menuItemLabel ----
			menuItemLabel.setText("JMenuItem:");
			panel1.add(menuItemLabel, "cell 0 1");

			//---- menuItem1 ----
			menuItem1.setText("enabled");
			menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
			menuItem1.setMnemonic('N');
			panel1.add(menuItem1, "cell 1 1");

			//---- checkBoxMenuItemLabel ----
			checkBoxMenuItemLabel.setText("JCheckBoxMenuItem:");
			panel1.add(checkBoxMenuItemLabel, "cell 0 2");

			//---- checkBoxMenuItem1 ----
			checkBoxMenuItem1.setText("<html>en<b>abl</b>ed</html>");
			checkBoxMenuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			checkBoxMenuItem1.setMnemonic('A');
			panel1.add(checkBoxMenuItem1, "cell 1 2");

			//---- radioButtonMenuItemLabel ----
			radioButtonMenuItemLabel.setText("JRadioButtonMenuItem:");
			panel1.add(radioButtonMenuItemLabel, "cell 0 3");

			//---- radioButtonMenuItem1 ----
			radioButtonMenuItem1.setText("<html>en<b color=\"red\">abl</b><i color=\"blue\">ed</i></html>");
			radioButtonMenuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			radioButtonMenuItem1.setMnemonic('B');
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
				menu2.setMnemonic('D');
			}
			panel2.add(menu2, "cell 0 0");

			//---- menuItem2 ----
			menuItem2.setText("disabled");
			menuItem2.setEnabled(false);
			menuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.ALT_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK));
			menuItem2.setMnemonic('I');
			panel2.add(menuItem2, "cell 0 1");

			//---- checkBoxMenuItem2 ----
			checkBoxMenuItem2.setText("disabled");
			checkBoxMenuItem2.setEnabled(false);
			checkBoxMenuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			checkBoxMenuItem2.setMnemonic('S');
			panel2.add(checkBoxMenuItem2, "cell 0 2");

			//---- radioButtonMenuItem2 ----
			radioButtonMenuItem2.setText("disabled");
			radioButtonMenuItem2.setEnabled(false);
			radioButtonMenuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			radioButtonMenuItem2.setMnemonic('L');
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

			//---- checkBoxMenuItem9 ----
			checkBoxMenuItem9.setText("selected");
			checkBoxMenuItem9.setSelected(true);
			checkBoxMenuItem9.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			checkBoxMenuItem9.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess.png")));
			panel3.add(checkBoxMenuItem9, "cell 0 4");

			//---- radioButtonMenuItem7 ----
			radioButtonMenuItem7.setText("selected");
			radioButtonMenuItem7.setSelected(true);
			radioButtonMenuItem7.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			radioButtonMenuItem7.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess.png")));
			panel3.add(radioButtonMenuItem7, "cell 0 5");
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

			//---- checkBoxMenuItem10 ----
			checkBoxMenuItem10.setText("selected disabled");
			checkBoxMenuItem10.setEnabled(false);
			checkBoxMenuItem10.setSelected(true);
			checkBoxMenuItem10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			checkBoxMenuItem10.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess.png")));
			panel4.add(checkBoxMenuItem10, "cell 0 4");

			//---- radioButtonMenuItem11 ----
			radioButtonMenuItem11.setText("selected disabled");
			radioButtonMenuItem11.setEnabled(false);
			radioButtonMenuItem11.setSelected(true);
			radioButtonMenuItem11.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			radioButtonMenuItem11.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess.png")));
			panel4.add(radioButtonMenuItem11, "cell 0 5");
		}
		add(panel4, "cell 4 1");

		//---- popupMenuLabel ----
		popupMenuLabel.setText("JPopupMenu:");
		popupMenuLabel.putClientProperty("FlatLaf.internal.testing.ignore", true);
		add(popupMenuLabel, "cell 0 2");

		//---- showPopupMenuButton ----
		showPopupMenuButton.setText("show JPopupMenu");
		showPopupMenuButton.putClientProperty("FlatLaf.internal.testing.ignore", true);
		showPopupMenuButton.addActionListener(e -> showPopupMenuButton(e));
		add(showPopupMenuButton, "cell 1 2");

		//---- showScrollingPopupMenuButton ----
		showScrollingPopupMenuButton.setText("show scrolling JPopupMenu");
		showScrollingPopupMenuButton.putClientProperty("FlatLaf.internal.testing.ignore", true);
		showScrollingPopupMenuButton.addActionListener(e -> showScrollingPopupMenu(e));
		add(showScrollingPopupMenuButton, "cell 2 2");

		//---- label1 ----
		label1.setText("Vertical JMenuBar:");
		add(label1, "cell 4 2");

		//---- armedCheckBox ----
		armedCheckBox.setText("armed");
		armedCheckBox.setMnemonic('A');
		armedCheckBox.putClientProperty("FlatLaf.internal.testing.ignore", true);
		armedCheckBox.addActionListener(e -> armedChanged());
		add(armedCheckBox, "cell 0 3");

		//======== verticalMenuBar ========
		{

			//======== menu14 ========
			{
				menu14.setText("menu");

				//---- menuItem53 ----
				menuItem53.setText("text");
				menu14.add(menuItem53);
			}
			verticalMenuBar.add(menu14);

			//======== menu15 ========
			{
				menu15.setText("another menu");

				//---- menuItem54 ----
				menuItem54.setText("text");
				menu15.add(menuItem54);
			}
			verticalMenuBar.add(menu15);

			//======== menu16 ========
			{
				menu16.setText("menu 3");
				menu16.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/test16.png")));

				//---- menuItem55 ----
				menuItem55.setText("text");
				menu16.add(menuItem55);
			}
			verticalMenuBar.add(menu16);
		}
		add(verticalMenuBar, "cell 4 3 1 3");

		//---- underlineCheckBox ----
		underlineCheckBox.setText("underline menu selection");
		underlineCheckBox.putClientProperty("FlatLaf.internal.testing.ignore", true);
		underlineCheckBox.addActionListener(e -> underlineChanged());
		add(underlineCheckBox, "cell 0 4 2 1");

		//---- popupMenubackgroundCheckBox ----
		popupMenubackgroundCheckBox.setText("yellow popup menu background");
		popupMenubackgroundCheckBox.putClientProperty("FlatLaf.internal.testing.ignore", true);
		popupMenubackgroundCheckBox.addActionListener(e -> popupMenubackgroundChanged());
		add(popupMenubackgroundCheckBox, "cell 0 5 2 1");

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radioButtonMenuItem5);
		buttonGroup1.add(radioButtonMenuItem6);

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(radioButtonMenuItem8);
		buttonGroup2.add(radioButtonMenuItem9);
		buttonGroup2.add(radioButtonMenuItem10);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		Icon largerIcon = new FlatAbstractIcon( 16, 16, Color.orange ) {
			@Override
			protected void paintIcon( Component c, Graphics2D g2 ) {
				g2.fillRect( 0, 0, 16, 16 );
			}
		};
		checkBoxMenuItem8.setIcon( largerIcon );
		radioButtonMenuItem10.setIcon( largerIcon );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JMenuBar menuBar2;
	private JCheckBox largerCheckBox;
	private JCheckBox accelCheckBox;
	private JButton showScrollingPopupMenuButton;
	private JCheckBox armedCheckBox;
	private JMenuBar verticalMenuBar;
	private JCheckBox underlineCheckBox;
	private JCheckBox popupMenubackgroundCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class PopupMenu ----------------------------------------------------

	private static class PopupMenu extends JPopupMenu {
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

	//---- class LargerMenuItem -----------------------------------------------

	public static class LargerMenuItem
		extends JMenuItem
	{
		static boolean useLargerSize = true;

		@Override
		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();
			return useLargerSize
				? new Dimension( size.width + UIScale.scale( 40 ),
								 size.height + UIScale.scale( 30 ) )
				: size;
		}

		@Override
		protected void paintComponent( Graphics g ) {
			super.paintComponent( g );

			g.setColor( UIManager.getColor( "Separator.foreground" ) );
			g.drawLine( 0, 0, getWidth(), 0 );
			g.drawLine( 0, getHeight(), getWidth(), getHeight() );
		}
	}

	//---- class MenuWithAccelerator ------------------------------------------

	public static class MenuWithAccelerator
		extends JMenu
	{
		private KeyStroke accelerator;

		@Override
		public KeyStroke getAccelerator() {
			return accelerator;
		}

		@Override
		public void setAccelerator( KeyStroke keyStroke ) {
			KeyStroke oldAccelerator = accelerator;
			this.accelerator = keyStroke;

			revalidate();
			repaint();

			firePropertyChange( "accelerator", oldAccelerator, accelerator );
		}
	}
}
