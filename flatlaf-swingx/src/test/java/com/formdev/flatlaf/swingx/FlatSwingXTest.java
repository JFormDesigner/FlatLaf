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

package com.formdev.flatlaf.swingx;

import javax.swing.*;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import com.formdev.flatlaf.FlatTestFrame;

/**
 * @author Karl Tauber
 */
public class FlatSwingXTest
	extends JPanel
{
	public static void main( String[] args ) {
		FlatTestFrame frame = FlatTestFrame.create( args, "FlatSwingXTest" );
		frame.showFrame( new FlatSwingXTest() );
	}

	FlatSwingXTest() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel hyperlinkLabel = new JLabel();
		JXHyperlink xHyperlink1 = new JXHyperlink();
		JXHyperlink xHyperlink2 = new JXHyperlink();

		//======== this ========
		setLayout(new MigLayout(
			"hidemode 3,ltr",
			// columns
			"[left]" +
			"[]" +
			"[]",
			// rows
			"[]"));

		//---- hyperlinkLabel ----
		hyperlinkLabel.setText("JXHyperlink:");
		add(hyperlinkLabel, "cell 0 0");

		//---- xHyperlink1 ----
		xHyperlink1.setText("enabled");
		add(xHyperlink1, "cell 1 0");

		//---- xHyperlink2 ----
		xHyperlink2.setText("disabled");
		xHyperlink2.setEnabled(false);
		add(xHyperlink2, "cell 2 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
