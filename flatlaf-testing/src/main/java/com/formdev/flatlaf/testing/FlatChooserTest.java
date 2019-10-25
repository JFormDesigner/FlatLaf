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

import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatChooserTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatChooserTest" );
			frame.showFrame( FlatChooserTest::new );
		} );
	}

	FlatChooserTest() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel colorChooserLabel = new JLabel();
		JColorChooser colorChooser1 = new JColorChooser();
		JLabel fileChooserLabel = new JLabel();
		JFileChooser fileChooser1 = new JFileChooser();
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
			"[]",
			// rows
			"[top]" +
			"[top]" +
			"[]"));

		//---- colorChooserLabel ----
		colorChooserLabel.setText("JColorChooser:");
		add(colorChooserLabel, "cell 0 0");
		add(colorChooser1, "cell 1 0");

		//---- fileChooserLabel ----
		fileChooserLabel.setText("JFileChooser:");
		add(fileChooserLabel, "cell 0 1");
		add(fileChooser1, "cell 1 1");

		//---- label1 ----
		label1.setText("icons:");
		add(label1, "cell 0 2");

		//---- label2 ----
		label2.setIcon(UIManager.getIcon("FileView.directoryIcon"));
		add(label2, "cell 1 2");

		//---- label3 ----
		label3.setIcon(UIManager.getIcon("FileView.fileIcon"));
		add(label3, "cell 1 2");

		//---- label4 ----
		label4.setIcon(UIManager.getIcon("FileView.computerIcon"));
		add(label4, "cell 1 2");

		//---- label5 ----
		label5.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
		add(label5, "cell 1 2");

		//---- label6 ----
		label6.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
		add(label6, "cell 1 2");

		//---- label7 ----
		label7.setIcon(UIManager.getIcon("FileChooser.newFolderIcon"));
		add(label7, "cell 1 2");

		//---- label8 ----
		label8.setIcon(UIManager.getIcon("FileChooser.upFolderIcon"));
		add(label8, "cell 1 2");

		//---- label9 ----
		label9.setIcon(UIManager.getIcon("FileChooser.homeFolderIcon"));
		add(label9, "cell 1 2");

		//---- label10 ----
		label10.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
		add(label10, "cell 1 2");

		//---- label11 ----
		label11.setIcon(UIManager.getIcon("FileChooser.listViewIcon"));
		add(label11, "cell 1 2");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
