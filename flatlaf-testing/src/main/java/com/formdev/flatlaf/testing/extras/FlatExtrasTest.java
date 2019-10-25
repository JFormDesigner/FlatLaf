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

package com.formdev.flatlaf.testing.extras;

import javax.swing.*;
import com.formdev.flatlaf.extras.*;
import com.formdev.flatlaf.testing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatExtrasTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatExtrasTest" );
			frame.showFrame( FlatExtrasTest::new );
		} );
	}

	public FlatExtrasTest() {
		initComponents();

		triStateLabel1.setText( triStateCheckBox1.getState().toString() );
		triStateLabel2.setText( triStateCheckBox2.getState().toString() );
	}

	private void triStateCheckBox1Changed() {
		triStateLabel1.setText( triStateCheckBox1.getState().toString() );
	}

	private void triStateCheckBox2Changed() {
		triStateLabel2.setText( triStateCheckBox2.getState().toString() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label1 = new JLabel();
		triStateCheckBox1 = new TriStateCheckBox();
		triStateLabel1 = new JLabel();
		triStateCheckBox2 = new TriStateCheckBox();
		triStateLabel2 = new JLabel();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[]" +
			"[left]",
			// rows
			"[]" +
			"[]"));

		//---- label1 ----
		label1.setText("TriStateCheckBox:");
		add(label1, "cell 0 0");

		//---- triStateCheckBox1 ----
		triStateCheckBox1.setText("three states");
		triStateCheckBox1.addActionListener(e -> triStateCheckBox1Changed());
		add(triStateCheckBox1, "cell 1 0");

		//---- triStateLabel1 ----
		triStateLabel1.setText("text");
		add(triStateLabel1, "cell 2 0");

		//---- triStateCheckBox2 ----
		triStateCheckBox2.setText("third state disabled");
		triStateCheckBox2.setThirdStateEnabled(false);
		triStateCheckBox2.addActionListener(e -> triStateCheckBox2Changed());
		add(triStateCheckBox2, "cell 1 1");

		//---- triStateLabel2 ----
		triStateLabel2.setText("text");
		add(triStateLabel2, "cell 2 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private TriStateCheckBox triStateCheckBox1;
	private JLabel triStateLabel1;
	private TriStateCheckBox triStateCheckBox2;
	private JLabel triStateLabel2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
