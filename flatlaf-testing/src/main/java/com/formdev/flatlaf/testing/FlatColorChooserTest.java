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
import javax.swing.*;
import javax.swing.border.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatColorChooserTest
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
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatColorChooserTest" );
			frame.showFrame( FlatColorChooserTest::new );
		} );
	}

	FlatColorChooserTest() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel colorChooserLabel = new JLabel();
		JPanel panel2 = new JPanel();
		JColorChooser colorChooser1 = new JColorChooser();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[grow]",
			// rows
			"[top]"));

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
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
