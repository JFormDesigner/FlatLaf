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

package com.formdev.flatlaf.testing.jideoss;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.formdev.flatlaf.testing.*;
import com.formdev.flatlaf.testing.FlatTestFrame;
import com.formdev.flatlaf.util.ScaledImageIcon;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatJideOssTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatJideOssTest" );
			frame.showFrame( FlatJideOssTest::new );
		} );
	}

	FlatJideOssTest() {
		initComponents();

		tristateCheckBox1Changed();
	}

	private void showJidePopup( ActionEvent e ) {
		Component invoker = (Component) e.getSource();

		JPanel panel = new JPanel( new MigLayout() );
		panel.add( new JLabel( "Name:") );
		panel.add( new JTextField( 20 ) );

		JidePopup popup = new JidePopup();
		popup.add( panel );
		popup.setDetached( true );
		popup.setOwner( invoker );
		popup.showPopup();
	}

	private void showJidePopupMenu( ActionEvent e ) {
		Component invoker = (Component) e.getSource();

		JidePopupMenu popupMenu = new JidePopupMenu();
		for( int i = 1; i <= 100; i++ )
			popupMenu.add( "menu item " + i );
		popupMenu.show( invoker, 0, invoker.getHeight() );
	}

	private void tristateCheckBox1Changed() {
		String text = null;
		switch( tristateCheckBox1.getState() ) {
			case TristateCheckBox.STATE_UNSELECTED:	text = "UNSELECTED"; break;
			case TristateCheckBox.STATE_SELECTED:	text = "SELECTED"; break;
			case TristateCheckBox.STATE_MIXED:		text = "MIXED"; break;
		}
		triStateLabel1.setText( text );
	}

	private void verticalChanged() {
		FlatTestFrame frame = (FlatTestFrame) SwingUtilities.getAncestorOfClass( FlatTestFrame.class, this );
		if( frame == null )
			return;

		int orientation = verticalCheckBox.isSelected()
			? SwingUtilities.VERTICAL
			: SwingUtilities.HORIZONTAL;

		frame.updateComponentsRecur( this, (c, type) -> {
			if( c instanceof JideButton )
				((JideButton)c).setOrientation( orientation );
		} );

		revalidate();
	}

	private void iconChanged() {
		FlatTestFrame frame = (FlatTestFrame) SwingUtilities.getAncestorOfClass( FlatTestFrame.class, this );
		if( frame == null )
			return;

		Icon icon = iconCheckBox.isSelected()
			? new ScaledImageIcon( new ImageIcon(getClass().getResource(
				"/com/formdev/flatlaf/testing/test16.png" ) ) )
			: null;

		frame.updateComponentsRecur( this, (c, type) -> {
			if( c instanceof JideButton )
				((JideButton)c).setIcon( icon );
		} );

		revalidate();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel jidePopupLabel = new JLabel();
		JButton showJidePopupButton = new JButton();
		JLabel jidePopupMenuLabel = new JLabel();
		JButton showJidePopupMenuButton = new JButton();
		JLabel label9 = new JLabel();
		tristateCheckBox1 = new TristateCheckBox();
		triStateLabel1 = new JLabel();
		JLabel jideButtonLabel = new JLabel();
		JideButton jideButton1 = new JideButton();
		JideButton jideButton2 = new JideButton();
		JideButton jideButton3 = new JideButton();
		JideButton jideButton4 = new JideButton();
		JToolBar toolBar1 = new JToolBar();
		JideButton jideButton9 = new JideButton();
		JButton button1 = new JButton();
		verticalCheckBox = new JCheckBox();
		JLabel label1 = new JLabel();
		JideButton jideButton5 = new JideButton();
		JideButton jideButton6 = new JideButton();
		JideButton jideButton7 = new JideButton();
		JideButton jideButton8 = new JideButton();
		JToolBar toolBar2 = new JToolBar();
		JideButton jideButton10 = new JideButton();
		iconCheckBox = new JCheckBox();
		JLabel jideToggleButtonLabel = new JLabel();
		JideToggleButton jideToggleButton1 = new JideToggleButton();
		JideToggleButton jideToggleButton2 = new JideToggleButton();
		JideToggleButton jideToggleButton3 = new JideToggleButton();
		JideToggleButton jideToggleButton4 = new JideToggleButton();
		JToolBar toolBar3 = new JToolBar();
		JideToggleButton jideToggleButton9 = new JideToggleButton();
		JToggleButton toggleButton1 = new JToggleButton();
		JLabel label2 = new JLabel();
		JideToggleButton jideToggleButton5 = new JideToggleButton();
		JideToggleButton jideToggleButton6 = new JideToggleButton();
		JideToggleButton jideToggleButton7 = new JideToggleButton();
		JideToggleButton jideToggleButton8 = new JideToggleButton();
		JToolBar toolBar4 = new JToolBar();
		JideToggleButton jideToggleButton10 = new JideToggleButton();
		JToggleButton toggleButton2 = new JToggleButton();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[]" +
			"[left]" +
			"[fill]" +
			"[fill]" +
			"[fill]",
			// rows
			"[]" +
			"[]" +
			"[]para" +
			"[]" +
			"[]" +
			"[]" +
			"[]"));

		//---- jidePopupLabel ----
		jidePopupLabel.setText("JidePopup:");
		add(jidePopupLabel, "cell 0 0");

		//---- showJidePopupButton ----
		showJidePopupButton.setText("show JidePopup");
		showJidePopupButton.addActionListener(e -> showJidePopup(e));
		add(showJidePopupButton, "cell 1 0,growx");

		//---- jidePopupMenuLabel ----
		jidePopupMenuLabel.setText("JidePopupMenu:");
		add(jidePopupMenuLabel, "cell 0 1");

		//---- showJidePopupMenuButton ----
		showJidePopupMenuButton.setText("show JidePopupMenu");
		showJidePopupMenuButton.addActionListener(e -> showJidePopupMenu(e));
		add(showJidePopupMenuButton, "cell 1 1,growx");

		//---- label9 ----
		label9.setText("TristateCheckBox:");
		add(label9, "cell 0 2");

		//---- tristateCheckBox1 ----
		tristateCheckBox1.setText("three states");
		tristateCheckBox1.addActionListener(e -> tristateCheckBox1Changed());
		add(tristateCheckBox1, "cell 1 2");

		//---- triStateLabel1 ----
		triStateLabel1.setText("text");
		triStateLabel1.setEnabled(false);
		add(triStateLabel1, "cell 2 2");

		//---- jideButtonLabel ----
		jideButtonLabel.setText("JideButton:");
		add(jideButtonLabel, "cell 0 3");

		//---- jideButton1 ----
		jideButton1.setText("TOOLBAR");
		add(jideButton1, "cell 1 3 3 1");

		//---- jideButton2 ----
		jideButton2.setText("TOOLBOX");
		jideButton2.setButtonStyle(1);
		add(jideButton2, "cell 1 3 3 1");

		//---- jideButton3 ----
		jideButton3.setText("FLAT");
		jideButton3.setButtonStyle(2);
		add(jideButton3, "cell 1 3 3 1");

		//---- jideButton4 ----
		jideButton4.setText("HYPERLINK");
		jideButton4.setButtonStyle(3);
		add(jideButton4, "cell 1 3 3 1");

		//======== toolBar1 ========
		{

			//---- jideButton9 ----
			jideButton9.setText("JideButton");
			toolBar1.add(jideButton9);

			//---- button1 ----
			button1.setText("JButton");
			toolBar1.add(button1);
		}
		add(toolBar1, "cell 1 3 3 1");

		//---- verticalCheckBox ----
		verticalCheckBox.setText("vertical");
		verticalCheckBox.addActionListener(e -> verticalChanged());
		add(verticalCheckBox, "cell 4 3");

		//---- label1 ----
		label1.setText("selected");
		label1.setEnabled(false);
		add(label1, "cell 0 4,alignx right,growx 0");

		//---- jideButton5 ----
		jideButton5.setText("TOOLBAR");
		jideButton5.setSelected(true);
		add(jideButton5, "cell 1 4 3 1");

		//---- jideButton6 ----
		jideButton6.setText("TOOLBOX");
		jideButton6.setButtonStyle(1);
		jideButton6.setSelected(true);
		add(jideButton6, "cell 1 4 3 1");

		//---- jideButton7 ----
		jideButton7.setText("FLAT");
		jideButton7.setButtonStyle(2);
		jideButton7.setSelected(true);
		add(jideButton7, "cell 1 4 3 1");

		//---- jideButton8 ----
		jideButton8.setText("HYPERLINK");
		jideButton8.setButtonStyle(3);
		jideButton8.setSelected(true);
		add(jideButton8, "cell 1 4 3 1");

		//======== toolBar2 ========
		{

			//---- jideButton10 ----
			jideButton10.setText("JideButton");
			jideButton10.setSelected(true);
			toolBar2.add(jideButton10);
		}
		add(toolBar2, "cell 1 4 3 1");

		//---- iconCheckBox ----
		iconCheckBox.setText("icon");
		iconCheckBox.addActionListener(e -> iconChanged());
		add(iconCheckBox, "cell 4 4");

		//---- jideToggleButtonLabel ----
		jideToggleButtonLabel.setText("JideToggleButton:");
		add(jideToggleButtonLabel, "cell 0 5");

		//---- jideToggleButton1 ----
		jideToggleButton1.setText("TOOLBAR");
		add(jideToggleButton1, "cell 1 5 3 1");

		//---- jideToggleButton2 ----
		jideToggleButton2.setText("TOOLBOX");
		jideToggleButton2.setButtonStyle(1);
		add(jideToggleButton2, "cell 1 5 3 1");

		//---- jideToggleButton3 ----
		jideToggleButton3.setText("FLAT");
		jideToggleButton3.setButtonStyle(2);
		add(jideToggleButton3, "cell 1 5 3 1");

		//---- jideToggleButton4 ----
		jideToggleButton4.setText("HYPERLINK");
		jideToggleButton4.setButtonStyle(3);
		add(jideToggleButton4, "cell 1 5 3 1");

		//======== toolBar3 ========
		{

			//---- jideToggleButton9 ----
			jideToggleButton9.setText("JideToggleButton");
			toolBar3.add(jideToggleButton9);

			//---- toggleButton1 ----
			toggleButton1.setText("JToggleButton");
			toolBar3.add(toggleButton1);
		}
		add(toolBar3, "cell 1 5 3 1");

		//---- label2 ----
		label2.setText("selected");
		label2.setEnabled(false);
		add(label2, "cell 0 6,alignx right,growx 0");

		//---- jideToggleButton5 ----
		jideToggleButton5.setText("TOOLBAR");
		jideToggleButton5.setSelected(true);
		add(jideToggleButton5, "cell 1 6 3 1");

		//---- jideToggleButton6 ----
		jideToggleButton6.setText("TOOLBOX");
		jideToggleButton6.setSelected(true);
		jideToggleButton6.setButtonStyle(1);
		add(jideToggleButton6, "cell 1 6 3 1");

		//---- jideToggleButton7 ----
		jideToggleButton7.setText("FLAT");
		jideToggleButton7.setSelected(true);
		jideToggleButton7.setButtonStyle(2);
		add(jideToggleButton7, "cell 1 6 3 1");

		//---- jideToggleButton8 ----
		jideToggleButton8.setText("HYPERLINK");
		jideToggleButton8.setSelected(true);
		jideToggleButton8.setButtonStyle(3);
		add(jideToggleButton8, "cell 1 6 3 1");

		//======== toolBar4 ========
		{

			//---- jideToggleButton10 ----
			jideToggleButton10.setText("JideToggleButton");
			jideToggleButton10.setSelected(true);
			toolBar4.add(jideToggleButton10);

			//---- toggleButton2 ----
			toggleButton2.setText("JToggleButton");
			toggleButton2.setSelected(true);
			toolBar4.add(toggleButton2);
		}
		add(toolBar4, "cell 1 6 3 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private TristateCheckBox tristateCheckBox1;
	private JLabel triStateLabel1;
	private JCheckBox verticalCheckBox;
	private JCheckBox iconCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
