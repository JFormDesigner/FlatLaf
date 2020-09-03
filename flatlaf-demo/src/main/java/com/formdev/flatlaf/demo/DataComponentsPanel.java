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

package com.formdev.flatlaf.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class DataComponentsPanel
	extends JPanel
{
	DataComponentsPanel() {
		initComponents();
	}

	private void dndChanged() {
		boolean dnd = dndCheckBox.isSelected();
		list1.setDragEnabled( dnd );
		list2.setDragEnabled( dnd );
		tree1.setDragEnabled( dnd );
		tree2.setDragEnabled( dnd );
		table1.setDragEnabled( dnd );

		DropMode dropMode = dnd ? DropMode.ON_OR_INSERT : DropMode.USE_SELECTION;
		list1.setDropMode( dropMode );
		tree1.setDropMode( dropMode );
		table1.setDropMode( dropMode );

		String key = "FlatLaf.oldTransferHandler";
		if( dnd ) {
			list1.putClientProperty( key, list1.getTransferHandler() );
			list1.setTransferHandler( new DummyTransferHandler() );

			tree1.putClientProperty( key, tree1.getTransferHandler() );
			tree1.setTransferHandler( new DummyTransferHandler() );

			table1.putClientProperty( key, table1.getTransferHandler() );
			table1.setTransferHandler( new DummyTransferHandler() );
		} else {
			list1.setTransferHandler( (TransferHandler) list1.getClientProperty( key ) );
			tree1.setTransferHandler( (TransferHandler) tree1.getClientProperty( key ) );
			table1.setTransferHandler( (TransferHandler) table1.getClientProperty( key ) );
		}
	}

	private void rowSelectionChanged() {
		table1.setRowSelectionAllowed( rowSelectionCheckBox.isSelected() );
	}

	private void columnSelectionChanged() {
		table1.setColumnSelectionAllowed( columnSelectionCheckBox.isSelected() );
	}

	private void showHorizontalLinesChanged() {
		table1.setShowHorizontalLines( showHorizontalLinesCheckBox.isSelected() );
	}

	private void showVerticalLinesChanged() {
		table1.setShowVerticalLines( showVerticalLinesCheckBox.isSelected() );
	}

	private void intercellSpacingChanged() {
		table1.setIntercellSpacing( intercellSpacingCheckBox.isSelected() ? new Dimension( 1, 1 ) : new Dimension() );
	}

	private void redGridColorChanged() {
		table1.setGridColor( redGridColorCheckBox.isSelected() ? Color.red : UIManager.getColor( "Table.gridColor" ) );
	}

	@Override
	public void updateUI() {
		super.updateUI();

		EventQueue.invokeLater( () -> {
			showHorizontalLinesChanged();
			showVerticalLinesChanged();
			intercellSpacingChanged();
		} );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel listLabel = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		list1 = new JList<>();
		JScrollPane scrollPane2 = new JScrollPane();
		list2 = new JList<>();
		JLabel treeLabel = new JLabel();
		JScrollPane scrollPane3 = new JScrollPane();
		tree1 = new JTree();
		JScrollPane scrollPane4 = new JScrollPane();
		tree2 = new JTree();
		JLabel tableLabel = new JLabel();
		JScrollPane scrollPane5 = new JScrollPane();
		table1 = new JTable();
		JPanel tableOptionsPanel = new JPanel();
		showHorizontalLinesCheckBox = new JCheckBox();
		showVerticalLinesCheckBox = new JCheckBox();
		intercellSpacingCheckBox = new JCheckBox();
		redGridColorCheckBox = new JCheckBox();
		rowSelectionCheckBox = new JCheckBox();
		columnSelectionCheckBox = new JCheckBox();
		dndCheckBox = new JCheckBox();
		JPopupMenu popupMenu2 = new JPopupMenu();
		JMenuItem menuItem3 = new JMenuItem();
		JMenuItem menuItem4 = new JMenuItem();
		JMenuItem menuItem5 = new JMenuItem();
		JMenuItem menuItem6 = new JMenuItem();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[]" +
			"[200,fill]" +
			"[200,fill]" +
			"[fill]",
			// rows
			"[150,grow,sizegroup 1,fill]" +
			"[150,grow,sizegroup 1,fill]" +
			"[150,grow,sizegroup 1,fill]"));

		//---- listLabel ----
		listLabel.setText("JList:");
		add(listLabel, "cell 0 0,aligny top,growy 0");

		//======== scrollPane1 ========
		{

			//---- list1 ----
			list1.setModel(new AbstractListModel<String>() {
				String[] values = {
					"item 1",
					"item 2",
					"item 3",
					"item 4",
					"item 5",
					"item 6",
					"item 7",
					"item 8",
					"item 9",
					"item 10",
					"item 11",
					"item 12",
					"item 13",
					"item 14",
					"item 15"
				};
				@Override
				public int getSize() { return values.length; }
				@Override
				public String getElementAt(int i) { return values[i]; }
			});
			list1.setComponentPopupMenu(popupMenu2);
			scrollPane1.setViewportView(list1);
		}
		add(scrollPane1, "cell 1 0");

		//======== scrollPane2 ========
		{

			//---- list2 ----
			list2.setModel(new AbstractListModel<String>() {
				String[] values = {
					"item 1",
					"item 2",
					"item 3",
					"item 4",
					"item 5",
					"item 6",
					"item 7",
					"item 8",
					"item 9",
					"item 10",
					"item 11",
					"item 12",
					"item 13",
					"item 14",
					"item 15"
				};
				@Override
				public int getSize() { return values.length; }
				@Override
				public String getElementAt(int i) { return values[i]; }
			});
			list2.setEnabled(false);
			scrollPane2.setViewportView(list2);
		}
		add(scrollPane2, "cell 2 0");

		//---- treeLabel ----
		treeLabel.setText("JTree:");
		add(treeLabel, "cell 0 1,aligny top,growy 0");

		//======== scrollPane3 ========
		{

			//---- tree1 ----
			tree1.setShowsRootHandles(true);
			tree1.setEditable(true);
			tree1.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("JTree") {
					{
						DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("colors");
							node1.add(new DefaultMutableTreeNode("blue"));
							node1.add(new DefaultMutableTreeNode("violet"));
							node1.add(new DefaultMutableTreeNode("red"));
							node1.add(new DefaultMutableTreeNode("yellow"));
						add(node1);
						node1 = new DefaultMutableTreeNode("sports");
							node1.add(new DefaultMutableTreeNode("basketball"));
							node1.add(new DefaultMutableTreeNode("soccer"));
							node1.add(new DefaultMutableTreeNode("football"));
							node1.add(new DefaultMutableTreeNode("hockey"));
						add(node1);
						node1 = new DefaultMutableTreeNode("food");
							node1.add(new DefaultMutableTreeNode("hot dogs"));
							DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("pizza");
								node2.add(new DefaultMutableTreeNode("pizza aglio e olio"));
								node2.add(new DefaultMutableTreeNode("pizza margherita bianca"));
							node1.add(node2);
							node1.add(new DefaultMutableTreeNode("ravioli"));
							node1.add(new DefaultMutableTreeNode("bananas"));
						add(node1);
					}
				}));
			tree1.setComponentPopupMenu(popupMenu2);
			scrollPane3.setViewportView(tree1);
		}
		add(scrollPane3, "cell 1 1");

		//======== scrollPane4 ========
		{

			//---- tree2 ----
			tree2.setEnabled(false);
			scrollPane4.setViewportView(tree2);
		}
		add(scrollPane4, "cell 2 1");

		//---- tableLabel ----
		tableLabel.setText("JTable:");
		add(tableLabel, "cell 0 2,aligny top,growy 0");

		//======== scrollPane5 ========
		{

			//---- table1 ----
			table1.setModel(new DefaultTableModel(
				new Object[][] {
					{"item 1", "item 1b", "January", "July", 123, null},
					{"item 2", "item 2b", "February", "August", 456, true},
					{"item 3", null, "March", null, null, null},
					{"item 4", null, "April", null, null, null},
					{"item 5", null, "May", null, null, null},
					{"item 6", null, "June", null, null, null},
					{"item 7", null, "July", null, null, null},
					{"item 8", null, "August", null, null, null},
					{"item 9", null, "September", null, null, null},
					{"item 10", null, "October", null, null, null},
					{"item 11", null, "November", null, null, null},
					{"item 12", null, "December", null, null, null},
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
			table1.setComponentPopupMenu(popupMenu2);
			scrollPane5.setViewportView(table1);
		}
		add(scrollPane5, "cell 1 2 2 1,width 300");

		//======== tableOptionsPanel ========
		{
			tableOptionsPanel.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[]",
				// rows
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0"));

			//---- showHorizontalLinesCheckBox ----
			showHorizontalLinesCheckBox.setText("show horizontal lines");
			showHorizontalLinesCheckBox.addActionListener(e -> showHorizontalLinesChanged());
			tableOptionsPanel.add(showHorizontalLinesCheckBox, "cell 0 0");

			//---- showVerticalLinesCheckBox ----
			showVerticalLinesCheckBox.setText("show vertical lines");
			showVerticalLinesCheckBox.addActionListener(e -> showVerticalLinesChanged());
			tableOptionsPanel.add(showVerticalLinesCheckBox, "cell 0 1");

			//---- intercellSpacingCheckBox ----
			intercellSpacingCheckBox.setText("intercell spacing");
			intercellSpacingCheckBox.addActionListener(e -> intercellSpacingChanged());
			tableOptionsPanel.add(intercellSpacingCheckBox, "cell 0 2");

			//---- redGridColorCheckBox ----
			redGridColorCheckBox.setText("red grid color");
			redGridColorCheckBox.addActionListener(e -> redGridColorChanged());
			tableOptionsPanel.add(redGridColorCheckBox, "cell 0 3");

			//---- rowSelectionCheckBox ----
			rowSelectionCheckBox.setText("row selection");
			rowSelectionCheckBox.setSelected(true);
			rowSelectionCheckBox.addActionListener(e -> rowSelectionChanged());
			tableOptionsPanel.add(rowSelectionCheckBox, "cell 0 4");

			//---- columnSelectionCheckBox ----
			columnSelectionCheckBox.setText("column selection");
			columnSelectionCheckBox.addActionListener(e -> columnSelectionChanged());
			tableOptionsPanel.add(columnSelectionCheckBox, "cell 0 5");

			//---- dndCheckBox ----
			dndCheckBox.setText("enable drag and drop");
			dndCheckBox.setMnemonic('D');
			dndCheckBox.addActionListener(e -> dndChanged());
			tableOptionsPanel.add(dndCheckBox, "cell 0 6");
		}
		add(tableOptionsPanel, "cell 3 2");

		//======== popupMenu2 ========
		{

			//---- menuItem3 ----
			menuItem3.setText("Some Action");
			popupMenu2.add(menuItem3);

			//---- menuItem4 ----
			menuItem4.setText("More Action");
			popupMenu2.add(menuItem4);
			popupMenu2.addSeparator();

			//---- menuItem5 ----
			menuItem5.setText("No Action");
			popupMenu2.add(menuItem5);

			//---- menuItem6 ----
			menuItem6.setText("Noop Action");
			popupMenu2.add(menuItem6);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		((JComboBox)((DefaultCellEditor)table1.getColumnModel().getColumn( 3 ).getCellEditor()).getComponent()).setEditable( true );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JList<String> list1;
	private JList<String> list2;
	private JTree tree1;
	private JTree tree2;
	private JTable table1;
	private JCheckBox showHorizontalLinesCheckBox;
	private JCheckBox showVerticalLinesCheckBox;
	private JCheckBox intercellSpacingCheckBox;
	private JCheckBox redGridColorCheckBox;
	private JCheckBox rowSelectionCheckBox;
	private JCheckBox columnSelectionCheckBox;
	private JCheckBox dndCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class DummyTransferHandler -----------------------------------------

	private static class DummyTransferHandler
		extends TransferHandler
	{
		@Override
		protected Transferable createTransferable( JComponent c ) {
			if( c instanceof JList && ((JList<?>)c).isSelectionEmpty() )
				return null;
			if( c instanceof JTree && ((JTree)c).isSelectionEmpty() )
				return null;
			if( c instanceof JTable && ((JTable)c).getSelectionModel().isSelectionEmpty() )
				return null;

			return new StringSelection( "dummy" );
		}

		@Override
		public int getSourceActions( JComponent c ) {
			return COPY;
		}

		@Override
		public boolean canImport( TransferSupport support ) {
			return support.isDataFlavorSupported( DataFlavor.stringFlavor );
		}

		@Override
		public boolean importData( TransferSupport support ) {
			String message = String.valueOf( support.getDropLocation() );
			SwingUtilities.invokeLater( () -> {
				JOptionPane.showMessageDialog( null, message, "Drop", JOptionPane.PLAIN_MESSAGE );
			} );
			return false;
		}
	}
}
