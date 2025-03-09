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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.*;
import javax.swing.UIDefaults.ActiveValue;
import javax.swing.table.*;
import javax.swing.tree.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.ColorFunctions;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class DataComponentsPanel
	extends JPanel
{
	DataComponentsPanel() {
		initComponents();

		// copy list and tree models
		list2.setModel( list1.getModel() );
		list3.setModel( list1.getModel() );
		tree2.setModel( tree1.getModel() );
		tree3.setModel( tree1.getModel() );

		// list selection
		int[] listSelection = { 1, 4, 5 };
		list1.setSelectedIndices( listSelection );
		list3.setSelectedIndices( listSelection );

		// tree selection
		int[] treeSelection = { 1, 4, 5 };
		for( JTree tree : new JTree[] { tree1, tree3 } ) {
			tree.expandRow( 1 );
			tree.setSelectionRows( treeSelection );
		}
		tree2.expandRow( 1 );

		// table selection
		table1.addRowSelectionInterval( 1, 1 );

		// rounded selection
		list3.putClientProperty( FlatClientProperties.STYLE, "selectionInsets: 0,1,0,1; selectionArc: 6" );
		tree3.putClientProperty( FlatClientProperties.STYLE, "selectionInsets: 0,1,0,1; selectionArc: 6" );
	}

	private void listAlternatingRowsChanged() {
		ActiveValue alternateRowColor = null;
		if( listAlternatingRowsCheckBox.isSelected() ) {
			alternateRowColor = table -> {
				Color background = list1.getBackground();
				return FlatLaf.isLafDark()
					? ColorFunctions.lighten( background, 0.05f )
					: ColorFunctions.darken( background, 0.05f );
			};
		}
		UIManager.put( "List.alternateRowColor", alternateRowColor );
		list1.updateUI();
		list2.updateUI();
		list3.updateUI();
	}

	private void treeWideSelectionChanged() {
		boolean wideSelection = treeWideSelectionCheckBox.isSelected();
		tree1.putClientProperty( FlatClientProperties.TREE_WIDE_SELECTION, wideSelection );
		tree2.putClientProperty( FlatClientProperties.TREE_WIDE_SELECTION, wideSelection );
		tree3.putClientProperty( FlatClientProperties.TREE_WIDE_SELECTION, wideSelection );
	}

	private void treeAlternatingRowsChanged() {
		ActiveValue alternateRowColor = null;
		if( treeAlternatingRowsCheckBox.isSelected() ) {
			alternateRowColor = table -> {
				Color background = tree1.getBackground();
				return FlatLaf.isLafDark()
					? ColorFunctions.lighten( background, 0.05f )
					: ColorFunctions.darken( background, 0.05f );
			};
		}
		UIManager.put( "Tree.alternateRowColor", alternateRowColor );
		tree1.updateUI();
		tree2.updateUI();
		tree3.updateUI();
	}

	private void dndChanged() {
		boolean dnd = dndCheckBox.isSelected();
		DropMode dropMode = dnd ? DropMode.ON_OR_INSERT : DropMode.USE_SELECTION;

		for( JList<?> list : new JList<?>[] { list1, list2, list3 } ) {
			list.setDragEnabled( dnd );
			list.setDropMode( dropMode );
		}

		for( JTree tree : new JTree[] { tree1, tree2, tree3 } ) {
			tree.setDragEnabled( dnd );
			tree.setDropMode( dropMode );
		}

		table1.setDragEnabled( dnd );
		table1.setDropMode( dropMode );

		String key = "FlatLaf.oldTransferHandler";
		JComponent[] components = { list1, list2, list3, tree1, tree2, tree3, table1 };
		for( JComponent c : components ) {
			if( dnd ) {
				c.putClientProperty( key, c.getTransferHandler() );
				c.setTransferHandler( new DummyTransferHandler() );
			} else
				c.setTransferHandler( (TransferHandler) c.getClientProperty( key ) );
		}
	}

	private void rowSelectionChanged() {
		table1.setRowSelectionAllowed( rowSelectionCheckBox.isSelected() );
		roundedSelectionChanged();
	}

	private void columnSelectionChanged() {
		table1.setColumnSelectionAllowed( columnSelectionCheckBox.isSelected() );
		roundedSelectionChanged();
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

	private void showHorizontalLinesPropertyChange() {
		showHorizontalLinesCheckBox.setSelected( table1.getShowHorizontalLines() );
	}

	private void showVerticalLinesPropertyChange() {
		showVerticalLinesCheckBox.setSelected( table1.getShowVerticalLines() );
	}

	private void intercellSpacingPropertyChange() {
		intercellSpacingCheckBox.setSelected( table1.getRowMargin() != 0 );
	}

	private void roundedSelectionChanged() {
		String style = null;
		if( roundedSelectionCheckBox.isSelected() ) {
			style = rowSelectionCheckBox.isSelected()
				? "selectionArc: 6; selectionInsets: 0,1,0,1"
				: "selectionArc: 6";
		}
		table1.putClientProperty( FlatClientProperties.STYLE, style );
	}

	private void alternatingRowsChanged() {
		ActiveValue alternateRowColor = null;
		if( alternatingRowsCheckBox.isSelected() ) {
			alternateRowColor = table -> {
				Color background = table1.getBackground();
				return FlatLaf.isLafDark()
					? ColorFunctions.lighten( background, 0.05f )
					: ColorFunctions.darken( background, 0.05f );
			};
		}
		UIManager.put( "Table.alternateRowColor", alternateRowColor );
		table1.repaint();
	}

	@SuppressWarnings( { "rawtypes" } )
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JLabel label3 = new JLabel();
		JLabel listLabel = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		list1 = new JList<>();
		JScrollPane scrollPane6 = new JScrollPane();
		list3 = new JList<>();
		JScrollPane scrollPane2 = new JScrollPane();
		list2 = new JList<>();
		JPanel listOptionsPanel = new JPanel();
		listAlternatingRowsCheckBox = new JCheckBox();
		JLabel treeLabel = new JLabel();
		JScrollPane scrollPane3 = new JScrollPane();
		tree1 = new JTree();
		JScrollPane scrollPane7 = new JScrollPane();
		tree3 = new JTree();
		JScrollPane scrollPane4 = new JScrollPane();
		tree2 = new JTree();
		JPanel treeOptionsPanel = new JPanel();
		treeWideSelectionCheckBox = new JCheckBox();
		treeAlternatingRowsCheckBox = new JCheckBox();
		JLabel tableLabel = new JLabel();
		JScrollPane scrollPane5 = new JScrollPane();
		table1 = new JTable();
		JPanel tableOptionsPanel = new JPanel();
		roundedSelectionCheckBox = new JCheckBox();
		showHorizontalLinesCheckBox = new JCheckBox();
		showVerticalLinesCheckBox = new JCheckBox();
		intercellSpacingCheckBox = new JCheckBox();
		redGridColorCheckBox = new JCheckBox();
		rowSelectionCheckBox = new JCheckBox();
		columnSelectionCheckBox = new JCheckBox();
		alternatingRowsCheckBox = new JCheckBox();
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
			"[150,fill]" +
			"[150,fill]" +
			"[150,fill]" +
			"[fill]",
			// rows
			"[]" +
			"[150,grow,sizegroup 1,fill]" +
			"[150,grow,sizegroup 1,fill]" +
			"[150,grow,fill]"));

		//---- label1 ----
		label1.setText("Square Selection");
		add(label1, "cell 1 0");

		//---- label2 ----
		label2.setText("Rounded Selection");
		add(label2, "cell 2 0");

		//---- label3 ----
		label3.setText("Disabled");
		add(label3, "cell 3 0");

		//---- listLabel ----
		listLabel.setText("JList:");
		add(listLabel, "cell 0 1,aligny top,growy 0");

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
		add(scrollPane1, "cell 1 1");

		//======== scrollPane6 ========
		{

			//---- list3 ----
			list3.setComponentPopupMenu(popupMenu2);
			scrollPane6.setViewportView(list3);
		}
		add(scrollPane6, "cell 2 1");

		//======== scrollPane2 ========
		{

			//---- list2 ----
			list2.setEnabled(false);
			scrollPane2.setViewportView(list2);
		}
		add(scrollPane2, "cell 3 1");

		//======== listOptionsPanel ========
		{
			listOptionsPanel.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]"));

			//---- listAlternatingRowsCheckBox ----
			listAlternatingRowsCheckBox.setText("alternating rows");
			listAlternatingRowsCheckBox.addActionListener(e -> listAlternatingRowsChanged());
			listOptionsPanel.add(listAlternatingRowsCheckBox, "cell 0 0");
		}
		add(listOptionsPanel, "cell 4 1");

		//---- treeLabel ----
		treeLabel.setText("JTree:");
		add(treeLabel, "cell 0 2,aligny top,growy 0");

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
		add(scrollPane3, "cell 1 2");

		//======== scrollPane7 ========
		{

			//---- tree3 ----
			tree3.setShowsRootHandles(true);
			tree3.setEditable(true);
			tree3.setComponentPopupMenu(popupMenu2);
			scrollPane7.setViewportView(tree3);
		}
		add(scrollPane7, "cell 2 2");

		//======== scrollPane4 ========
		{

			//---- tree2 ----
			tree2.setEnabled(false);
			scrollPane4.setViewportView(tree2);
		}
		add(scrollPane4, "cell 3 2");

		//======== treeOptionsPanel ========
		{
			treeOptionsPanel.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]0" +
				"[]"));

			//---- treeWideSelectionCheckBox ----
			treeWideSelectionCheckBox.setText("wide selection");
			treeWideSelectionCheckBox.setSelected(true);
			treeWideSelectionCheckBox.addActionListener(e -> treeWideSelectionChanged());
			treeOptionsPanel.add(treeWideSelectionCheckBox, "cell 0 0");

			//---- treeAlternatingRowsCheckBox ----
			treeAlternatingRowsCheckBox.setText("alternating rows");
			treeAlternatingRowsCheckBox.addActionListener(e -> treeAlternatingRowsChanged());
			treeOptionsPanel.add(treeAlternatingRowsCheckBox, "cell 0 1");
		}
		add(treeOptionsPanel, "cell 4 2");

		//---- tableLabel ----
		tableLabel.setText("JTable:");
		add(tableLabel, "cell 0 3,aligny top,growy 0");

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
				Class<?>[] columnTypes = {
					Object.class, Object.class, String.class, String.class, Integer.class, Boolean.class
				};
				boolean[] columnEditable = {
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
					new JComboBox<>(new DefaultComboBoxModel<>(new String[] {
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
					new JComboBox<>(new DefaultComboBoxModel<>(new String[] {
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
			table1.addPropertyChangeListener("showHorizontalLines", e -> showHorizontalLinesPropertyChange());
			table1.addPropertyChangeListener("showVerticalLines", e -> showVerticalLinesPropertyChange());
			table1.addPropertyChangeListener("rowMargin", e -> intercellSpacingPropertyChange());
			scrollPane5.setViewportView(table1);
		}
		add(scrollPane5, "cell 1 3 3 1,width 300");

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
				"[]0" +
				"[]0" +
				"[]0"));

			//---- roundedSelectionCheckBox ----
			roundedSelectionCheckBox.setText("rounded selection");
			roundedSelectionCheckBox.addActionListener(e -> roundedSelectionChanged());
			tableOptionsPanel.add(roundedSelectionCheckBox, "cell 0 0");

			//---- showHorizontalLinesCheckBox ----
			showHorizontalLinesCheckBox.setText("show horizontal lines");
			showHorizontalLinesCheckBox.addActionListener(e -> showHorizontalLinesChanged());
			tableOptionsPanel.add(showHorizontalLinesCheckBox, "cell 0 1");

			//---- showVerticalLinesCheckBox ----
			showVerticalLinesCheckBox.setText("show vertical lines");
			showVerticalLinesCheckBox.addActionListener(e -> showVerticalLinesChanged());
			tableOptionsPanel.add(showVerticalLinesCheckBox, "cell 0 2");

			//---- intercellSpacingCheckBox ----
			intercellSpacingCheckBox.setText("intercell spacing");
			intercellSpacingCheckBox.addActionListener(e -> intercellSpacingChanged());
			tableOptionsPanel.add(intercellSpacingCheckBox, "cell 0 3");

			//---- redGridColorCheckBox ----
			redGridColorCheckBox.setText("red grid color");
			redGridColorCheckBox.addActionListener(e -> redGridColorChanged());
			tableOptionsPanel.add(redGridColorCheckBox, "cell 0 4");

			//---- rowSelectionCheckBox ----
			rowSelectionCheckBox.setText("row selection");
			rowSelectionCheckBox.setSelected(true);
			rowSelectionCheckBox.addActionListener(e -> rowSelectionChanged());
			tableOptionsPanel.add(rowSelectionCheckBox, "cell 0 5");

			//---- columnSelectionCheckBox ----
			columnSelectionCheckBox.setText("column selection");
			columnSelectionCheckBox.addActionListener(e -> columnSelectionChanged());
			tableOptionsPanel.add(columnSelectionCheckBox, "cell 0 6");

			//---- alternatingRowsCheckBox ----
			alternatingRowsCheckBox.setText("alternating rows");
			alternatingRowsCheckBox.addActionListener(e -> alternatingRowsChanged());
			tableOptionsPanel.add(alternatingRowsCheckBox, "cell 0 7");

			//---- dndCheckBox ----
			dndCheckBox.setText("enable drag and drop");
			dndCheckBox.setMnemonic('D');
			dndCheckBox.addActionListener(e -> dndChanged());
			tableOptionsPanel.add(dndCheckBox, "cell 0 8");
		}
		add(tableOptionsPanel, "cell 4 3");

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
	private JList<String> list3;
	private JList<String> list2;
	private JCheckBox listAlternatingRowsCheckBox;
	private JTree tree1;
	private JTree tree3;
	private JTree tree2;
	private JCheckBox treeWideSelectionCheckBox;
	private JCheckBox treeAlternatingRowsCheckBox;
	private JTable table1;
	private JCheckBox roundedSelectionCheckBox;
	private JCheckBox showHorizontalLinesCheckBox;
	private JCheckBox showVerticalLinesCheckBox;
	private JCheckBox intercellSpacingCheckBox;
	private JCheckBox redGridColorCheckBox;
	private JCheckBox rowSelectionCheckBox;
	private JCheckBox columnSelectionCheckBox;
	private JCheckBox alternatingRowsCheckBox;
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
