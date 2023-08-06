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

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.icons.FlatFileChooserHomeFolderIcon;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatChooserTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
//		Locale.setDefault( Locale.FRENCH );
//		Locale.setDefault( Locale.GERMAN );
//		Locale.setDefault( Locale.ITALIAN );
//		Locale.setDefault( Locale.JAPANESE );
//		Locale.setDefault( Locale.SIMPLIFIED_CHINESE );
//		Locale.setDefault( Locale.TRADITIONAL_CHINESE );

		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatChooserTest" );

			UIManager.put( "FileChooser.shortcuts.filesFunction", (Function<File[], File[]>) files -> {
				ArrayList<File> list = new ArrayList<>( Arrays.asList( files ) );
				list.add( 0, new File( System.getProperty( "user.home" ) ) );
				return list.toArray( new File[list.size()] );
			} );

			UIManager.put( "FileChooser.shortcuts.displayNameFunction", (Function<File, String>) file -> {
				if( file.getAbsolutePath().equals( System.getProperty( "user.home" ) ) )
					return "Home";
				return null;
			} );
			UIManager.put( "FileChooser.shortcuts.iconFunction", (Function<File, Icon>) file -> {
				if( file.getAbsolutePath().equals( System.getProperty( "user.home" ) ) )
					return new FlatFileChooserHomeFolderIcon();
				return null;
			} );

			frame.showFrame( FlatChooserTest::new );
		} );
	}

	FlatChooserTest() {
		initComponents();
	}

	private void showShortcuts() {
		UIManager.put( "FileChooser.noPlacesBar", !showShortcutsCheckBox.isSelected() ? true : null );
		fileChooser1.updateUI();
	}

	private void showAccessory() {
		JPanel accessory = null;
		if( showAccessoryCheckBox.isSelected() ) {
			accessory = new JPanel( new BorderLayout() );
			accessory.setBackground( Color.green );
			accessory.add( new JLabel( "  Accessory  " ), BorderLayout.CENTER );
		}
		fileChooser1.setAccessory( accessory );
		fileChooser1.revalidate();
		fileChooser1.repaint();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel colorChooserLabel = new JLabel();
		JPanel panel2 = new JPanel();
		JColorChooser colorChooser1 = new JColorChooser();
		JLabel fileChooserLabel = new JLabel();
		JPanel panel1 = new JPanel();
		fileChooser1 = new JFileChooser();
		JPanel panel3 = new JPanel();
		showShortcutsCheckBox = new JCheckBox();
		showAccessoryCheckBox = new JCheckBox();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JLabel label3 = new JLabel();
		JLabel label4 = new JLabel();
		JLabel label5 = new JLabel();
		JLabel label6 = new JLabel();
		JLabel label7 = new JLabel();
		JLabel label8 = new JLabel();
		JLabel label9 = new JLabel();
		JLabel label10 = new JLabel();
		JLabel label11 = new JLabel();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[grow]",
			// rows
			"[top]" +
			"[grow,fill]" +
			"[]" +
			"[]"));

		//---- colorChooserLabel ----
		colorChooserLabel.setText("JColorChooser:");
		add(colorChooserLabel, "cell 0 0");

		//======== panel2 ========
		{
			panel2.setBorder(new MatteBorder(4, 4, 4, 4, Color.red));
			panel2.setLayout(new BorderLayout());
			panel2.add(colorChooser1, BorderLayout.CENTER);
		}
		add(panel2, "cell 1 0");

		//---- fileChooserLabel ----
		fileChooserLabel.setText("JFileChooser:");
		add(fileChooserLabel, "cell 0 1,aligny top,growy 0");

		//======== panel1 ========
		{
			panel1.setBorder(new MatteBorder(4, 4, 4, 4, Color.red));
			panel1.setLayout(new BorderLayout());
			panel1.add(fileChooser1, BorderLayout.CENTER);
		}
		add(panel1, "cell 1 1,growx");

		//======== panel3 ========
		{
			panel3.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[fill]",
				// rows
				"[]"));

			//---- showShortcutsCheckBox ----
			showShortcutsCheckBox.setText("Show Shortcuts");
			showShortcutsCheckBox.setSelected(true);
			showShortcutsCheckBox.addActionListener(e -> showShortcuts());
			panel3.add(showShortcutsCheckBox, "cell 0 0");

			//---- showAccessoryCheckBox ----
			showAccessoryCheckBox.setText("Show Accessory");
			showAccessoryCheckBox.addActionListener(e -> showAccessory());
			panel3.add(showAccessoryCheckBox, "cell 1 0");
		}
		add(panel3, "cell 1 2");

		//---- label1 ----
		label1.setText("icons:");
		add(label1, "cell 0 3");

		//---- label2 ----
		label2.setIcon(UIManager.getIcon("FileView.directoryIcon"));
		add(label2, "cell 1 3");

		//---- label3 ----
		label3.setIcon(UIManager.getIcon("FileView.fileIcon"));
		add(label3, "cell 1 3");

		//---- label4 ----
		label4.setIcon(UIManager.getIcon("FileView.computerIcon"));
		add(label4, "cell 1 3");

		//---- label5 ----
		label5.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
		add(label5, "cell 1 3");

		//---- label6 ----
		label6.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
		add(label6, "cell 1 3");

		//---- label7 ----
		label7.setIcon(UIManager.getIcon("FileChooser.newFolderIcon"));
		add(label7, "cell 1 3");

		//---- label8 ----
		label8.setIcon(UIManager.getIcon("FileChooser.upFolderIcon"));
		add(label8, "cell 1 3");

		//---- label9 ----
		label9.setIcon(UIManager.getIcon("FileChooser.homeFolderIcon"));
		add(label9, "cell 1 3");

		//---- label10 ----
		label10.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
		add(label10, "cell 1 3");

		//---- label11 ----
		label11.setIcon(UIManager.getIcon("FileChooser.listViewIcon"));
		add(label11, "cell 1 3");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JFileChooser fileChooser1;
	private JCheckBox showShortcutsCheckBox;
	private JCheckBox showAccessoryCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
