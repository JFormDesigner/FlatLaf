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

package com.formdev.flatlaf;

import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatComponentsTest
	extends JPanel
{
	public static void main( String[] args ) {
		try {
			if( args.length > 0 )
				UIManager.setLookAndFeel( args[0] );
			else
				UIManager.setLookAndFeel( new FlatTestLaf() );
		} catch( Exception ex ) {
			ex.printStackTrace();
		}

		JOptionPane.showMessageDialog( null,
			new FlatComponentsTest(),
			"FlatComponentsTest (Java " + System.getProperty( "java.version" ) + ")",
			JOptionPane.PLAIN_MESSAGE );
	}

	public FlatComponentsTest() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel labelLabel = new JLabel();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JLabel buttonLabel = new JLabel();
		JButton button1 = new JButton();
		JButton button2 = new JButton();
		JButton button3 = new JButton();
		JButton button4 = new JButton();
		FlatComponentsTest.TestDefaultButton button5 = new FlatComponentsTest.TestDefaultButton();

		//======== this ========
		setLayout(new MigLayout(
			"insets 0,hidemode 3,gap 5 5",
			// columns
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]",
			// rows
			"[]" +
			"[]"));

		//---- labelLabel ----
		labelLabel.setText("JLabel:");
		add(labelLabel, "cell 0 0");

		//---- label1 ----
		label1.setText("enabled");
		label1.setDisplayedMnemonic('E');
		add(label1, "cell 1 0");

		//---- label2 ----
		label2.setText("disabled");
		label2.setDisplayedMnemonic('D');
		label2.setEnabled(false);
		add(label2, "cell 2 0");

		//---- buttonLabel ----
		buttonLabel.setText("JButton:");
		add(buttonLabel, "cell 0 1");

		//---- button1 ----
		button1.setText("enabled");
		button1.setDisplayedMnemonicIndex(0);
		add(button1, "cell 1 1");

		//---- button2 ----
		button2.setText("disabled");
		button2.setDisplayedMnemonicIndex(0);
		button2.setEnabled(false);
		add(button2, "cell 2 1");

		//---- button3 ----
		button3.setText("selected");
		button3.setSelected(true);
		add(button3, "cell 3 1");

		//---- button4 ----
		button4.setText("selected disabled");
		button4.setSelected(true);
		button4.setEnabled(false);
		add(button4, "cell 4 1");

		//---- button5 ----
		button5.setText("default");
		button5.setDisplayedMnemonicIndex(0);
		add(button5, "cell 5 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class TestDefaultButton --------------------------------------------

	private static class TestDefaultButton
		extends JButton
	{
		@Override
		public boolean isDefaultButton() {
			return true;
		}
	}
}
