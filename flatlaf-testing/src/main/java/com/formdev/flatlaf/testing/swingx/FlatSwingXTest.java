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
import java.util.Calendar;
import java.util.Date;
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
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatSwingXTest" );
			frame.useApplyComponentOrientation = true;
			frame.showFrame( new FlatSwingXTest() );
		} );
	}

	FlatSwingXTest() {
		initComponents();

		Calendar calendar = Calendar.getInstance();

		calendar.set( Calendar.DAY_OF_MONTH, 2 );
		monthView1.setSelectionDate( calendar.getTime() );

		calendar.set( Calendar.DAY_OF_MONTH, 9 );
		monthView1.setFlaggedDates( calendar.getTime() );

		calendar.set( Calendar.DAY_OF_MONTH, 16 );
		monthView1.setUnselectableDates( calendar.getTime() );
	}

	private void busyChanged() {
		boolean busy = busyCheckBox.isSelected();
		xBusyLabel1.setBusy( busy );
		xBusyLabel2.setBusy( busy );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label1 = new JLabel();
		JLabel label3 = new JLabel();
		JLabel datePickerLabel = new JLabel();
		JXDatePicker xDatePicker1 = new JXDatePicker();
		JXDatePicker xDatePicker2 = new JXDatePicker();
		JLabel label4 = new JLabel();
		JLabel label5 = new JLabel();
		JXDatePicker xDatePicker3 = new JXDatePicker();
		JXDatePicker xDatePicker4 = new JXDatePicker();
		JLabel monthViewLabel = new JLabel();
		monthView1 = new JXMonthView();
		monthView2 = new JXMonthView();
		JLabel hyperlinkLabel = new JLabel();
		JXHyperlink xHyperlink1 = new JXHyperlink();
		JXHyperlink xHyperlink2 = new JXHyperlink();
		JLabel label2 = new JLabel();
		xBusyLabel1 = new JXBusyLabel();
		xBusyLabel2 = new JXBusyLabel();
		busyCheckBox = new JCheckBox();
		JPanel panel2 = new JPanel();
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
			"[fill]",
			// rows
			"[]0" +
			"[]" +
			"[]0" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]"));

		//---- label1 ----
		label1.setText("enabled");
		add(label1, "cell 1 0");

		//---- label3 ----
		label3.setText("disabled");
		add(label3, "cell 2 0");

		//---- datePickerLabel ----
		datePickerLabel.setText("JXDatePicker:");
		add(datePickerLabel, "cell 0 1");
		add(xDatePicker1, "cell 1 1");

		//---- xDatePicker2 ----
		xDatePicker2.setEnabled(false);
		add(xDatePicker2, "cell 2 1");

		//---- label4 ----
		label4.setText("not editable");
		add(label4, "cell 1 2");

		//---- label5 ----
		label5.setText("not editable disabled");
		add(label5, "cell 2 2");

		//---- xDatePicker3 ----
		xDatePicker3.setEditable(false);
		add(xDatePicker3, "cell 1 3");

		//---- xDatePicker4 ----
		xDatePicker4.setEnabled(false);
		xDatePicker4.setEditable(false);
		add(xDatePicker4, "cell 2 3");

		//---- monthViewLabel ----
		monthViewLabel.setText("JXMonthView:");
		add(monthViewLabel, "cell 0 4,aligny top,growy 0");

		//---- monthView1 ----
		monthView1.setTraversable(true);
		monthView1.setShowingLeadingDays(true);
		monthView1.setShowingTrailingDays(true);
		monthView1.setShowingWeekNumber(true);
		add(monthView1, "cell 1 4");

		//---- monthView2 ----
		monthView2.setTraversable(true);
		monthView2.setShowingLeadingDays(true);
		monthView2.setShowingTrailingDays(true);
		monthView2.setShowingWeekNumber(true);
		monthView2.setEnabled(false);
		add(monthView2, "cell 2 4");

		//---- hyperlinkLabel ----
		hyperlinkLabel.setText("JXHyperlink:");
		add(hyperlinkLabel, "cell 0 5");

		//---- xHyperlink1 ----
		xHyperlink1.setText("enabled");
		add(xHyperlink1, "cell 1 5");

		//---- xHyperlink2 ----
		xHyperlink2.setText("disabled");
		xHyperlink2.setEnabled(false);
		add(xHyperlink2, "cell 2 5");

		//---- label2 ----
		label2.setText("JXBusyLabel:");
		add(label2, "cell 0 6");

		//---- xBusyLabel1 ----
		xBusyLabel1.setText("enabled");
		add(xBusyLabel1, "cell 1 6");

		//---- xBusyLabel2 ----
		xBusyLabel2.setText("disabled");
		xBusyLabel2.setEnabled(false);
		add(xBusyLabel2, "cell 2 6,growx");

		//---- busyCheckBox ----
		busyCheckBox.setText("busy");
		busyCheckBox.setMnemonic('B');
		busyCheckBox.addActionListener(e -> busyChanged());
		add(busyCheckBox, "cell 2 6");

		//======== panel2 ========
		{
			panel2.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]"));

			//---- taskPaneContainerLabel ----
			taskPaneContainerLabel.setText("JXTaskPaneContainer:");
			panel2.add(taskPaneContainerLabel, "cell 0 0");

			//---- taskPaneLabel ----
			taskPaneLabel.setText("JXTaskPane:");
			panel2.add(taskPaneLabel, "cell 0 1");

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
			panel2.add(scrollPane1, "cell 0 2,width 150,height 350");
		}
		add(panel2, "cell 3 0 1 8,aligny top,growy 0");

		//---- headerLabel ----
		headerLabel.setText("JXHeader:");
		add(headerLabel, "cell 0 7,aligny top,growy 0");

		//---- xHeader1 ----
		xHeader1.setTitle("Title");
		xHeader1.setDescription("Description\nMore description");
		xHeader1.setIcon(new ImageIcon(getClass().getResource("/org/jdesktop/swingx/plaf/windows/resources/tipoftheday.png")));
		add(xHeader1, "cell 1 7 2 1,width 200");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		xDatePicker1.setDate( new Date() );
		xDatePicker2.setDate( new Date() );
		xDatePicker3.setDate( new Date() );
		xDatePicker4.setDate( new Date() );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JXMonthView monthView1;
	private JXMonthView monthView2;
	private JXBusyLabel xBusyLabel1;
	private JXBusyLabel xBusyLabel2;
	private JCheckBox busyCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
