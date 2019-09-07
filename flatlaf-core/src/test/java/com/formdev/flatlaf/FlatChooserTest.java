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
public class FlatChooserTest
	extends JPanel
{
	public static void main( String[] args ) {
		FlatTestFrame frame = FlatTestFrame.create( args, "FlatChooserTest" );
		frame.showFrame( new FlatChooserTest() );
	}

	FlatChooserTest() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel colorChooserLabel = new JLabel();
		JColorChooser colorChooser1 = new JColorChooser();

		//======== this ========
		setLayout(new MigLayout(
			"insets 0,hidemode 3,gap 5 5,ltr",
			// columns
			"[]" +
			"[]",
			// rows
			"[top]"));

		//---- colorChooserLabel ----
		colorChooserLabel.setText("JColorChooser:");
		add(colorChooserLabel, "cell 0 0");
		add(colorChooser1, "cell 1 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
