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

import java.awt.*;
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

	private void busyChanged() {
		boolean busy = busyCheckBox.isSelected();
		xBusyLabel1.setBusy( busy );
		xBusyLabel2.setBusy( busy );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel hyperlinkLabel = new JLabel();
		JXHyperlink xHyperlink1 = new JXHyperlink();
		JXHyperlink xHyperlink2 = new JXHyperlink();
		JLabel label2 = new JLabel();
		xBusyLabel1 = new JXBusyLabel();
		xBusyLabel2 = new JXBusyLabel();
		busyCheckBox = new JCheckBox();
		JPanel panel1 = new JPanel();
		JLabel taskPaneContainerLabel = new JLabel();
		JLabel taskPaneLabel = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		JXTaskPaneContainer xTaskPaneContainer1 = new JXTaskPaneContainer();
		JXTaskPane xTaskPane3 = new JXTaskPane();
		JXHyperlink xHyperlink3 = new JXHyperlink();
		JXHyperlink xHyperlink4 = new JXHyperlink();
		JXHyperlink xHyperlink5 = new JXHyperlink();
		JXTaskPane xTaskPane4 = new JXTaskPane();
		JXHyperlink xHyperlink6 = new JXHyperlink();
		JXHyperlink xHyperlink7 = new JXHyperlink();
		JXTaskPane xTaskPane5 = new JXTaskPane();
		JXHyperlink xHyperlink8 = new JXHyperlink();
		JXTaskPane xTaskPane6 = new JXTaskPane();
		JXHyperlink xHyperlink9 = new JXHyperlink();
		JXHyperlink xHyperlink10 = new JXHyperlink();
		JLabel headerLabel = new JLabel();
		JXHeader xHeader1 = new JXHeader();

		//======== this ========
		setLayout(new MigLayout(
			"hidemode 3,ltr",
			// columns
			"[left]" +
			"[]" +
			"[]" +
			"[]",
			// rows
			"[]" +
			"[]" +
			"[]" +
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

		//---- label2 ----
		label2.setText("JXBusyLabel:");
		add(label2, "cell 0 1");

		//---- xBusyLabel1 ----
		xBusyLabel1.setText("enabled");
		add(xBusyLabel1, "cell 1 1");

		//---- xBusyLabel2 ----
		xBusyLabel2.setText("disabled");
		xBusyLabel2.setEnabled(false);
		add(xBusyLabel2, "cell 2 1");

		//---- busyCheckBox ----
		busyCheckBox.setText("busy");
		busyCheckBox.setMnemonic('B');
		busyCheckBox.addActionListener(e -> busyChanged());
		add(busyCheckBox, "cell 3 1");

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3",
				// columns
				"[left]",
				// rows
				"[]" +
				"[]"));

			//---- taskPaneContainerLabel ----
			taskPaneContainerLabel.setText("JXTaskPaneContainer:");
			panel1.add(taskPaneContainerLabel, "cell 0 0");

			//---- taskPaneLabel ----
			taskPaneLabel.setText("JXTaskPane:");
			panel1.add(taskPaneLabel, "cell 0 1");
		}
		add(panel1, "cell 0 2,aligny top,growy 0");

		//======== scrollPane1 ========
		{

			//======== xTaskPaneContainer1 ========
			{

				//======== xTaskPane3 ========
				{
					xTaskPane3.setTitle("Basic Tasks");
					Container xTaskPane3ContentPane = xTaskPane3.getContentPane();

					//---- xHyperlink3 ----
					xHyperlink3.setText("New");
					xTaskPane3ContentPane.add(xHyperlink3);

					//---- xHyperlink4 ----
					xHyperlink4.setText("Open");
					xTaskPane3ContentPane.add(xHyperlink4);

					//---- xHyperlink5 ----
					xHyperlink5.setText("Save");
					xTaskPane3ContentPane.add(xHyperlink5);
				}
				xTaskPaneContainer1.add(xTaskPane3);

				//======== xTaskPane4 ========
				{
					xTaskPane4.setTitle("Other Tasks");
					xTaskPane4.setIcon(UIManager.getIcon("Tree.closedIcon"));
					Container xTaskPane4ContentPane = xTaskPane4.getContentPane();

					//---- xHyperlink6 ----
					xHyperlink6.setText("Duplicate");
					xTaskPane4ContentPane.add(xHyperlink6);

					//---- xHyperlink7 ----
					xHyperlink7.setText("Delete");
					xTaskPane4ContentPane.add(xHyperlink7);
				}
				xTaskPaneContainer1.add(xTaskPane4);

				//======== xTaskPane5 ========
				{
					xTaskPane5.setTitle("Special Tasks");
					xTaskPane5.setSpecial(true);
					Container xTaskPane5ContentPane = xTaskPane5.getContentPane();

					//---- xHyperlink8 ----
					xHyperlink8.setText("Go to space");
					xTaskPane5ContentPane.add(xHyperlink8);
				}
				xTaskPaneContainer1.add(xTaskPane5);

				//======== xTaskPane6 ========
				{
					xTaskPane6.setTitle("Collapsed");
					xTaskPane6.setCollapsed(true);
					Container xTaskPane6ContentPane = xTaskPane6.getContentPane();

					//---- xHyperlink9 ----
					xHyperlink9.setText("text");
					xTaskPane6ContentPane.add(xHyperlink9);

					//---- xHyperlink10 ----
					xHyperlink10.setText("text");
					xTaskPane6ContentPane.add(xHyperlink10);
				}
				xTaskPaneContainer1.add(xTaskPane6);
			}
			scrollPane1.setViewportView(xTaskPaneContainer1);
		}
		add(scrollPane1, "cell 1 2,width 150,height 350");

		//---- headerLabel ----
		headerLabel.setText("JXHeader:");
		add(headerLabel, "cell 0 3");

		//---- xHeader1 ----
		xHeader1.setTitle("Title");
		xHeader1.setDescription("Description\nMore description");
		xHeader1.setIcon(new ImageIcon(getClass().getResource("/org/jdesktop/swingx/plaf/windows/resources/tipoftheday.png")));
		add(xHeader1, "cell 1 3 2 1,width 200");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JXBusyLabel xBusyLabel1;
	private JXBusyLabel xBusyLabel2;
	private JCheckBox busyCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
