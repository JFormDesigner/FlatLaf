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
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatPopupTest
	extends FlatTestPanel
{
	private Popup[] popups;
	private JPanel[] popupPanels;

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatPopupTest" );
			frame.showFrame( FlatPopupTest::new );
		} );
	}

	FlatPopupTest() {
		initComponents();
		addPopupMenuListener( popupMenu1, "popupMenu1" );
		addPopupMenuListener( popupMenu2, "popupMenu2" );
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
		createPopupPanels();
		int xoffset2 = popupPanels[0].getPreferredSize().width + UIScale.scale( 10 );

		Point pt = showPopupButton.getLocationOnScreen();
		popups = new Popup[popupPanels.length];
		for( int i = 0; i < popupPanels.length; i++ ) {
			popups[i] = PopupFactory.getSharedInstance().getPopup( this, popupPanels[i],
				pt.x + xoffset + (xoffset2 * i), pt.y + showPopupButton.getHeight() + yoffset );
			popups[i].show();
		}
	}

	private void hidePopup() {
		if( popups == null )
			return;

		for( Popup popup : popups )
			popup.hide();
		popups = null;
	}

	private void movePopupDown() {
		movePopup( 0, 600 );
	}

	private void movePopupRight() {
		movePopup( 600, 0 );
	}

	private void movePopup( int xoffset, int yoffset ) {
		showPopup();

		Animator animator = new Animator( 1500, fraction -> {
//			System.out.println(fraction);
			showPopup( (int) (fraction * xoffset), (int) (fraction * yoffset) );
		} );
		animator.start();
	}

	private void createPopupPanels() {
		int count = (int) countField.getValue();
		if( popupPanels != null && popupPanels.length == count )
			return;

		Random random = new Random();
		popupPanels = new JPanel[count];
		for( int i = 0; i < popupPanels.length; i++ ) {
			JLabel l = new JLabel( "popup " + (i + 1) );
			JPanel p = new JPanel();
			p.setBackground( new Color( random.nextInt( 0xffffff ) ) );
			p.add( l );
			popupPanels[i] = p;
		}
	}

	private void showDirectPopup() {
		DirectPopupContent content = new DirectPopupContent();
		content.putClientProperty( FlatClientProperties.POPUP_FORCE_HEAVY_WEIGHT, true );
		Point pt = showDirectPopupButton.getLocationOnScreen();

		System.setProperty( FlatSystemProperties.USE_ROUNDED_POPUP_BORDER, "false" );
		UIManager.put( "Popup.dropShadowColor", Color.red );
		UIManager.put( "Popup.dropShadowInsets", new Insets( 5, 5, 5, 5 ) );
		UIManager.put( "Popup.dropShadowOpacity", 1f );

		Popup popup = PopupFactory.getSharedInstance().getPopup( showDirectPopupButton,
			content, pt.x, pt.y + showDirectPopupButton.getHeight() + 10 );
		content.popup = popup;
		popup.show();

		System.clearProperty( FlatSystemProperties.USE_ROUNDED_POPUP_BORDER );
		UIManager.put( "Popup.dropShadowColor", null );
		UIManager.put( "Popup.dropShadowInsets", null );
		UIManager.put( "Popup.dropShadowOpacity", null );
	}

	private void addPopupMenuListener( JPopupMenu popupMenu, String name ) {
		popupMenu.addPopupMenuListener( new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
				System.out.println( "popupMenuWillBecomeVisible    " + name );
			}

			@Override
			public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
				System.out.println( "popupMenuWillBecomeInvisible  " + name );
			}

			@Override
			public void popupMenuCanceled( PopupMenuEvent e ) {
				System.out.println( "popupMenuCanceled             " + name );
			}
		} );
	}

	@Override
	public void updateUI() {
		super.updateUI();

		if( popupMenu1 != null ) {
			SwingUtilities.updateComponentTreeUI( popupMenu1 );
			SwingUtilities.updateComponentTreeUI( popupMenu2 );
		}
		if( popupPanels != null ) {
			for( JPanel popupPanel : popupPanels )
				SwingUtilities.updateComponentTreeUI( popupPanel );
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label1 = new JLabel();
		label2 = new JLabel();
		showPopupMenuButton = new JButton();
		showDirectPopupButton = new JButton();
		showLargePopupMenuButton = new JButton();
		showPopupButton = new JButton();
		hidePopupButton = new JButton();
		movePopupDownButton = new JButton();
		movePopupRightButton = new JButton();
		countLabel = new JLabel();
		countField = new JSpinner();
		label4 = new JLabel();
		movingToolTipPanel = new MovingToolTipPanel();
		popupMenu1 = new JPopupMenu();
		menuItem1 = new JMenuItem();
		menuItem2 = new JMenuItem();
		menu1 = new JMenu();
		menuItem3 = new JMenuItem();
		menuItem4 = new JMenuItem();
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
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[grow,fill]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[grow,fill]"));

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

		//---- showDirectPopupButton ----
		showDirectPopupButton.setText("show direct move/resize popup");
		showDirectPopupButton.addActionListener(e -> showDirectPopup());
		add(showDirectPopupButton, "cell 2 2 2 1");

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

		//---- movePopupRightButton ----
		movePopupRightButton.setText("move right");
		movePopupRightButton.addActionListener(e -> movePopupRight());
		add(movePopupRightButton, "cell 3 4");

		//---- countLabel ----
		countLabel.setText("Count:");
		add(countLabel, "cell 4 4");

		//---- countField ----
		countField.setModel(new SpinnerNumberModel(1, 1, null, 1));
		add(countField, "cell 5 4");

		//---- label4 ----
		label4.setText("(switches to heavy-weight when moving outside of window)");
		add(label4, "cell 0 5 4 1,alignx right,growx 0");
		add(movingToolTipPanel, "cell 0 6 7 1");

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
	private JButton showDirectPopupButton;
	private JButton showLargePopupMenuButton;
	private JButton showPopupButton;
	private JButton hidePopupButton;
	private JButton movePopupDownButton;
	private JButton movePopupRightButton;
	private JLabel countLabel;
	private JSpinner countField;
	private JLabel label4;
	private MovingToolTipPanel movingToolTipPanel;
	private JPopupMenu popupMenu1;
	private JMenuItem menuItem1;
	private JMenuItem menuItem2;
	private JMenu menu1;
	private JMenuItem menuItem3;
	private JMenuItem menuItem4;
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

	//---- class MovingToolTipPanel -------------------------------------------

	private static class MovingToolTipPanel
		extends JPanel
	{
		private MovingToolTipPanel() {
			initComponents();
		}

		@Override
		public String getToolTipText( MouseEvent e ) {
			return e.getX() + "," + e.getY();
		}

		@Override
		public Point getToolTipLocation( MouseEvent e ) {
			// multiply Y by two to make it possible to move tooltip outside of window,
			// which forces use of heavy weight popups for all Lafs
			return new Point( e.getX() , e.getY() * 2 );
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
			label6 = new JLabel();

			//======== this ========
			setBorder(new LineBorder(Color.red));
			setToolTipText("text");
			setLayout(new FlowLayout());

			//---- label6 ----
			label6.setText("moving tooltip area");
			add(label6);
			// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
		private JLabel label6;
		// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
	}

	//---- class MyPopupContent -----------------------------------------------

	private static class DirectPopupContent
		extends JPanel
	{
		Popup popup;

		DirectPopupContent() {
			initComponents();
		}

		private void resizePopup() {
			Window popupWindow = SwingUtilities.windowForComponent( this );
			popupWindow.setSize( popupWindow.getWidth() + 20, popupWindow.getHeight() + 50 );
		}

		private void movePopup() {
			Window popupWindow = SwingUtilities.windowForComponent( this );
			popupWindow.setLocation( popupWindow.getX() + 20, popupWindow.getY() + 50 );
		}

		private void hidePopup() {
			popup.hide();
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
			resizeButton = new JButton();
			moveButton = new JButton();
			hideButton = new JButton();

			//======== this ========
			setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[fill]" +
				"[fill]",
				// rows
				"[]"));

			//---- resizeButton ----
			resizeButton.setText("Resize");
			resizeButton.addActionListener(e -> resizePopup());
			add(resizeButton, "cell 0 0");

			//---- moveButton ----
			moveButton.setText("Move");
			moveButton.addActionListener(e -> movePopup());
			add(moveButton, "cell 1 0");

			//---- hideButton ----
			hideButton.setText("Hide");
			hideButton.addActionListener(e -> hidePopup());
			add(hideButton, "cell 2 0");
			// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
		private JButton resizeButton;
		private JButton moveButton;
		private JButton hideButton;
		// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
	}
}
