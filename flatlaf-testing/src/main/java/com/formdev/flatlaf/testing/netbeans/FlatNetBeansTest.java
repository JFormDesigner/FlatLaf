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

package com.formdev.flatlaf.testing.netbeans;

import java.awt.*;
import javax.swing.*;
import com.formdev.flatlaf.testing.*;
import com.formdev.flatlaf.testing.FlatTestFrame;
import net.miginfocom.swing.*;
import org.openide.awt.*;

/**
 * @author Karl Tauber
 */
public class FlatNetBeansTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatNetBeansTest" );
			frame.showFrame( FlatNetBeansTest::new );
		} );
	}

	FlatNetBeansTest() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel colorComboBoxLabel = new JLabel();
		ColorComboBox colorComboBox1 = new ColorComboBox();
		JComboBox<String> comboBox4 = new JComboBox<>();
		ColorComboBox colorComboBox2 = new ColorComboBox();
		JComboBox<String> comboBox3 = new JComboBox<>();

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

		//---- colorComboBoxLabel ----
		colorComboBoxLabel.setText("ColorComboBox:");
		add(colorComboBoxLabel, "cell 0 0");
		add(colorComboBox1, "cell 1 0");

		//---- comboBox4 ----
		comboBox4.setModel(new DefaultComboBoxModel<>(new String[] {
			"JComboBox",
			"a",
			"bb",
			"ccc",
			"dd",
			"e",
			"ff",
			"ggg",
			"hh",
			"i",
			"jj",
			"kkk"
		}));
		add(comboBox4, "cell 2 0,growx");

		//---- colorComboBox2 ----
		colorComboBox2.setSelectedColor(new Color(176, 62, 62));
		add(colorComboBox2, "cell 1 1");

		//---- comboBox3 ----
		comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {
			"JComboBox",
			"a",
			"bb",
			"ccc",
			"dd",
			"e",
			"ff",
			"ggg",
			"hh",
			"i",
			"jj",
			"kkk"
		}));
		add(comboBox3, "cell 1 2,growx");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
