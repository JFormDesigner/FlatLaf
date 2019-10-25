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
import javax.swing.table.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatComponents2Test
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatComponents2Test" );
			frame.showFrame( FlatComponents2Test::new );
		} );
	}

	FlatComponents2Test() {
		initComponents();
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel labelLabel = new JLabel();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JLabel textFieldLabel = new JLabel();
		JTextField textField1 = new JTextField();
		JTextField textField2 = new JTextField();
		JLabel listLabel = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		JList<String> list1 = new JList<>();
		JScrollPane scrollPane2 = new JScrollPane();
		JList<String> list2 = new JList<>();
		JLabel treeLabel = new JLabel();
		JScrollPane scrollPane3 = new JScrollPane();
		JTree tree1 = new JTree();
		JScrollPane scrollPane4 = new JScrollPane();
		JTree tree2 = new JTree();
		JLabel tableLabel = new JLabel();
		JScrollPane scrollPane5 = new JScrollPane();
		JTable table1 = new JTable();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[200]" +
			"[200]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[::200]" +
			"[::150]"));

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

		//---- textFieldLabel ----
		textFieldLabel.setText("JTextField:");
		add(textFieldLabel, "cell 0 1");

		//---- textField1 ----
		textField1.setText("editable");
		add(textField1, "cell 1 1,growx");

		//---- textField2 ----
		textField2.setText("disabled");
		textField2.setEnabled(false);
		add(textField2, "cell 2 1,growx");

		//---- listLabel ----
		listLabel.setText("JList:");
		add(listLabel, "cell 0 2");

		//======== scrollPane1 ========
		{
			scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- list1 ----
			list1.setModel(new AbstractListModel<String>() {
				String[] values = {
					"abc",
					"de",
					"f"
				};
				@Override
				public int getSize() { return values.length; }
				@Override
				public String getElementAt(int i) { return values[i]; }
			});
			scrollPane1.setViewportView(list1);
		}
		add(scrollPane1, "cell 1 2,growx");

		//======== scrollPane2 ========
		{
			scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- list2 ----
			list2.setModel(new AbstractListModel<String>() {
				String[] values = {
					"abc",
					"de",
					"f"
				};
				@Override
				public int getSize() { return values.length; }
				@Override
				public String getElementAt(int i) { return values[i]; }
			});
			list2.setEnabled(false);
			scrollPane2.setViewportView(list2);
		}
		add(scrollPane2, "cell 2 2,growx");

		//---- treeLabel ----
		treeLabel.setText("JTree:");
		add(treeLabel, "cell 0 3");

		//======== scrollPane3 ========
		{

			//---- tree1 ----
			tree1.setShowsRootHandles(true);
			tree1.setEditable(true);
			scrollPane3.setViewportView(tree1);
		}
		add(scrollPane3, "cell 1 3,growx");

		//======== scrollPane4 ========
		{

			//---- tree2 ----
			tree2.setEnabled(false);
			scrollPane4.setViewportView(tree2);
		}
		add(scrollPane4, "cell 2 3,growx");

		//---- tableLabel ----
		tableLabel.setText("JTable:");
		add(tableLabel, "cell 0 4");

		//======== scrollPane5 ========
		{

			//---- table1 ----
			table1.setModel(new DefaultTableModel(
				new Object[][] {
					{"Item 1a", "Item 2a", "January", "July", 123, null},
					{"Item 1b", "Item 2b", "February", "August", 456, true},
				},
				new String[] {
					"Not editable", "Text", "Combo", "Combo Editable", "Integer", "Boolean"
				}
			) {
				Class<?>[] columnTypes = new Class<?>[] {
					Object.class, Object.class, String.class, String.class, Integer.class, Boolean.class
				};
				boolean[] columnEditable = new boolean[] {
					false, true, true, true, true, true
				};
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return columnEditable[columnIndex];
				}
			});
			{
				TableColumnModel cm = table1.getColumnModel();
				cm.getColumn(2).setCellEditor(new DefaultCellEditor(
					new JComboBox(new DefaultComboBoxModel(new String[] {
						"January",
						"February",
						"March",
						"April",
						"May",
						"June",
						"July",
						"August",
						"September",
						"October",
						"November",
						"December"
					}))));
				cm.getColumn(3).setCellEditor(new DefaultCellEditor(
					new JComboBox(new DefaultComboBoxModel(new String[] {
						"January",
						"February",
						"March",
						"April",
						"May",
						"June",
						"July",
						"August",
						"September",
						"October",
						"November",
						"December"
					}))));
			}
			table1.setAutoCreateRowSorter(true);
			scrollPane5.setViewportView(table1);
		}
		add(scrollPane5, "cell 1 4 2 1,growx,width 300");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		((JComboBox)((DefaultCellEditor)table1.getColumnModel().getColumn( 3 ).getCellEditor()).getComponent()).setEditable( true );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
