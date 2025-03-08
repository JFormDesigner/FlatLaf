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

package com.formdev.flatlaf.testing.swingx;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.*;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.jdesktop.swingx.tips.DefaultTip;
import org.jdesktop.swingx.tips.DefaultTipOfTheDayModel;
import org.jdesktop.swingx.tips.TipOfTheDayModel.Tip;
import com.formdev.flatlaf.testing.FlatTestFrame;
import com.formdev.flatlaf.testing.FlatTestPanel;

/**
 * @author Karl Tauber
 */
public class FlatSwingXTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatSwingXTest" );

			// without this, painting becomes very slow as soon as JXTitledPanel
			// is used, because JXTitledPanel sets opaque to false and JXPanel
			// then installs its own repaint manager
			UIManager.put( "JXPanel.patch", true );

			frame.useApplyComponentOrientation = true;
			frame.showFrame( FlatSwingXTest::new );
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

		table.setDefaultEditor( Date.class, new DatePickerCellEditor() );

		// status bar
		statusBar1.add( new JLabel( "Ready" ), new JXStatusBar.Constraint( 100 ) );
		statusBar1.add( new JLabel( "0 files loaded" ), new JXStatusBar.Constraint( 100 ) );
		JProgressBar statusProgressBar = new JProgressBar();
		statusProgressBar.setValue( 50 );
		statusBar1.add( statusProgressBar, new JXStatusBar.Constraint( JXStatusBar.Constraint.ResizeBehavior.FILL ) );

		xTipOfTheDay1.setModel( new DefaultTipOfTheDayModel( new Tip[] {
			new DefaultTip( "testTip", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." )
		} ) );
	}

	private void busyChanged() {
		boolean busy = busyCheckBox.isSelected();
		xBusyLabel1.setBusy( busy );
		xBusyLabel2.setBusy( busy );
	}

	private void showTipOfTheDayDialog() {
		JXTipOfTheDay tipOfTheDay = new JXTipOfTheDay( xTipOfTheDay1.getModel() );
		tipOfTheDay.showDialog( this );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label1 = new JLabel();
		JLabel label3 = new JLabel();
		JLabel label6 = new JLabel();
		JLabel datePickerLabel = new JLabel();
		JXDatePicker xDatePicker1 = new JXDatePicker();
		JLabel label4 = new JLabel();
		JXDatePicker xDatePicker3 = new JXDatePicker();
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
		JXDatePicker xDatePicker2 = new JXDatePicker();
		JLabel label5 = new JLabel();
		JXDatePicker xDatePicker4 = new JXDatePicker();
		JScrollPane scrollPane2 = new JScrollPane();
		table = new JTable();
		JLabel headerLabel = new JLabel();
		JXHeader xHeader1 = new JXHeader();
		JXTitledPanel xTitledPanel1 = new JXTitledPanel();
		JLabel label7 = new JLabel();
		JTextField textField1 = new JTextField();
		JLabel label8 = new JLabel();
		JTextField textField2 = new JTextField();
		JLabel titledPanelLabel = new JLabel();
		JXTitledPanel xTitledPanel2 = new JXTitledPanel();
		JLabel label9 = new JLabel();
		JTextField textField3 = new JTextField();
		JLabel label10 = new JLabel();
		JTextField textField4 = new JTextField();
		JLabel label11 = new JLabel();
		JXSearchField xSearchField1 = new JXSearchField();
		JXSearchField xSearchField2 = new JXSearchField();
		JXSearchField xSearchField3 = new JXSearchField();
		JXSearchField xSearchField4 = new JXSearchField();
		JLabel label12 = new JLabel();
		statusBar1 = new JXStatusBar();
		JLabel label13 = new JLabel();
		xTipOfTheDay1 = new JXTipOfTheDay();
		JButton showTipOfTheDayDialogButton = new JButton();
		JButton button1 = new JButton();
		JButton button2 = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[left]" +
			"[]" +
			"[]" +
			"[]" +
			"[fill]",
			// rows
			"[]0" +
			"[]" +
			"[]0" +
			"[top]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[top]" +
			"[37]"));

		//---- label1 ----
		label1.setText("enabled");
		add(label1, "cell 1 0");

		//---- label3 ----
		label3.setText("disabled");
		add(label3, "cell 2 0");

		//---- label6 ----
		label6.setText("DatePickerCellEditor:");
		add(label6, "cell 3 0");

		//---- datePickerLabel ----
		datePickerLabel.setText("JXDatePicker:");
		add(datePickerLabel, "cell 0 1");
		add(xDatePicker1, "cell 1 1");

		//---- label4 ----
		label4.setText("not editable");
		add(label4, "cell 1 2");

		//---- xDatePicker3 ----
		xDatePicker3.setEditable(false);
		add(xDatePicker3, "cell 1 3");

		//---- monthViewLabel ----
		monthViewLabel.setText("JXMonthView:");
		add(monthViewLabel, "cell 0 4,aligny top,growy 0");

		//---- monthView1 ----
		monthView1.setTraversable(true);
		monthView1.setShowingLeadingDays(true);
		monthView1.setShowingTrailingDays(true);
		monthView1.setShowingWeekNumber(true);
		add(monthView1, "cell 1 4 2 1");

		//---- monthView2 ----
		monthView2.setTraversable(true);
		monthView2.setShowingLeadingDays(true);
		monthView2.setShowingTrailingDays(true);
		monthView2.setShowingWeekNumber(true);
		monthView2.setEnabled(false);
		add(monthView2, "cell 3 4");

		//---- hyperlinkLabel ----
		hyperlinkLabel.setText("JXHyperlink:");
		add(hyperlinkLabel, "cell 0 5");

		//---- xHyperlink1 ----
		xHyperlink1.setText("enabled");
		add(xHyperlink1, "cell 1 5 2 1");

		//---- xHyperlink2 ----
		xHyperlink2.setText("disabled");
		xHyperlink2.setEnabled(false);
		add(xHyperlink2, "cell 3 5");

		//---- label2 ----
		label2.setText("JXBusyLabel:");
		add(label2, "cell 0 6");

		//---- xBusyLabel1 ----
		xBusyLabel1.setText("enabled");
		add(xBusyLabel1, "cell 1 6 2 1");

		//---- xBusyLabel2 ----
		xBusyLabel2.setText("disabled");
		xBusyLabel2.setEnabled(false);
		add(xBusyLabel2, "cell 3 6,growx");

		//---- busyCheckBox ----
		busyCheckBox.setText("busy");
		busyCheckBox.setMnemonic('Y');
		busyCheckBox.addActionListener(e -> busyChanged());
		add(busyCheckBox, "cell 3 6");

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
		add(panel2, "cell 4 0 1 8,aligny top,growy 0");

		//---- xDatePicker2 ----
		xDatePicker2.setEnabled(false);
		add(xDatePicker2, "cell 2 1");

		//---- label5 ----
		label5.setText("not editable disabled");
		add(label5, "cell 2 2");

		//---- xDatePicker4 ----
		xDatePicker4.setEnabled(false);
		xDatePicker4.setEditable(false);
		add(xDatePicker4, "cell 2 3");

		//======== scrollPane2 ========
		{

			//---- table ----
			table.setModel(new DefaultTableModel(
				new Object[][] {
					{new Date(1574636400000L) /* 2019-11-25 */},
					{new Date(1517439600000L) /* 2018-02-01 */},
				},
				new String[] {
					"Date"
				}
			) {
				Class<?>[] columnTypes = {
					Date.class
				};
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
			});
			table.setPreferredScrollableViewportSize(new Dimension(150, 50));
			scrollPane2.setViewportView(table);
		}
		add(scrollPane2, "cell 3 1 1 3,growy");

		//---- headerLabel ----
		headerLabel.setText("JXHeader:");
		add(headerLabel, "cell 0 7,aligny top,growy 0");

		//---- xHeader1 ----
		xHeader1.setTitle("Title");
		xHeader1.setDescription("Description\nMore description");
		xHeader1.setIcon(new ImageIcon(getClass().getResource("/org/jdesktop/swingx/plaf/windows/resources/tipoftheday.png")));
		add(xHeader1, "cell 1 7 3 1,width 200");

		//======== xTitledPanel1 ========
		{
			xTitledPanel1.setTitle("Title");
			xTitledPanel1.setOpaque(true);
			Container xTitledPanel1ContentContainer = xTitledPanel1.getContentContainer();
			xTitledPanel1ContentContainer.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[100,fill]",
				// rows
				"[]" +
				"[]"));

			//---- label7 ----
			label7.setText("text");
			xTitledPanel1ContentContainer.add(label7, "cell 0 0");
			xTitledPanel1ContentContainer.add(textField1, "cell 1 0");

			//---- label8 ----
			label8.setText("text");
			xTitledPanel1ContentContainer.add(label8, "cell 0 1");
			xTitledPanel1ContentContainer.add(textField2, "cell 1 1");
		}
		add(xTitledPanel1, "cell 1 8 2 1,grow");

		//---- titledPanelLabel ----
		titledPanelLabel.setText("JXTitledPanel:");
		add(titledPanelLabel, "cell 0 8,aligny top,growy 0");

		//======== xTitledPanel2 ========
		{
			xTitledPanel2.setTitle("Title");
			xTitledPanel2.setOpaque(true);
			xTitledPanel2.setLeftDecoration(button1);
			xTitledPanel2.setRightDecoration(button2);
			Container xTitledPanel2ContentContainer = xTitledPanel2.getContentContainer();
			xTitledPanel2ContentContainer.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[100,fill]",
				// rows
				"[]" +
				"[]"));

			//---- label9 ----
			label9.setText("text");
			xTitledPanel2ContentContainer.add(label9, "cell 0 0");
			xTitledPanel2ContentContainer.add(textField3, "cell 1 0");

			//---- label10 ----
			label10.setText("text");
			xTitledPanel2ContentContainer.add(label10, "cell 0 1");
			xTitledPanel2ContentContainer.add(textField4, "cell 1 1");
		}
		add(xTitledPanel2, "cell 3 8,grow");

		//---- label11 ----
		label11.setText("JXSearchField:");
		add(label11, "cell 0 9");

		//---- xSearchField1 ----
		xSearchField1.setText("abc");
		add(xSearchField1, "cell 1 9,growx");

		//---- xSearchField2 ----
		xSearchField2.setEnabled(false);
		xSearchField2.setText("abc");
		add(xSearchField2, "cell 2 9,growx");

		//---- xSearchField3 ----
		xSearchField3.setRecentSearchesSaveKey("flatlaf.swingx.search.recent");
		xSearchField3.setText("abc");
		add(xSearchField3, "cell 1 10,growx");

		//---- xSearchField4 ----
		xSearchField4.setRecentSearchesSaveKey("flatlaf.swingx.search.recent");
		xSearchField4.setEnabled(false);
		xSearchField4.setText("abc");
		add(xSearchField4, "cell 2 10,growx");

		//---- label12 ----
		label12.setText("JXStatusBar:");
		add(label12, "cell 0 11");
		add(statusBar1, "cell 1 11 3 1,grow");

		//---- label13 ----
		label13.setText("JXTipOfTheDay:");
		add(label13, "cell 0 12");
		add(xTipOfTheDay1, "cell 1 12 3 1");

		//---- showTipOfTheDayDialogButton ----
		showTipOfTheDayDialogButton.setText("Show Dialog...");
		showTipOfTheDayDialogButton.addActionListener(e -> showTipOfTheDayDialog());
		add(showTipOfTheDayDialogButton, "cell 1 12 3 1");

		//---- button1 ----
		button1.setText("<");

		//---- button2 ----
		button2.setText(">");
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
	private JTable table;
	private JXStatusBar statusBar1;
	private JXTipOfTheDay xTipOfTheDay1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
