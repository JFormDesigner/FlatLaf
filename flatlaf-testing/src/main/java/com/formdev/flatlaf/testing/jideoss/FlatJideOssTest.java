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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel jidePopupLabel = new JLabel();
		JButton showJidePopupButton = new JButton();
		JLabel jidePopupMenuLabel = new JLabel();
		JButton showJidePopupMenuButton = new JButton();
		JLabel label9 = new JLabel();
		tristateCheckBox1 = new TristateCheckBox();
		triStateLabel1 = new JLabel();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[]" +
			"[fill]" +
			"[fill]",
			// rows
			"[]" +
			"[]" +
			"[]"));

		//---- jidePopupLabel ----
		jidePopupLabel.setText("JidePopup:");
		add(jidePopupLabel, "cell 0 0");

		//---- showJidePopupButton ----
		showJidePopupButton.setText("show JidePopup");
		showJidePopupButton.addActionListener(e -> showJidePopup(e));
		add(showJidePopupButton, "cell 1 0");

		//---- jidePopupMenuLabel ----
		jidePopupMenuLabel.setText("JidePopupMenu:");
		add(jidePopupMenuLabel, "cell 0 1");

		//---- showJidePopupMenuButton ----
		showJidePopupMenuButton.setText("show JidePopupMenu");
		showJidePopupMenuButton.addActionListener(e -> showJidePopupMenu(e));
		add(showJidePopupMenuButton, "cell 1 1");

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
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private TristateCheckBox tristateCheckBox1;
	private JLabel triStateLabel1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
