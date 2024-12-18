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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatMenuArrowIcon;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatListUI;
import com.formdev.flatlaf.ui.FlatTableUI;
import com.formdev.flatlaf.util.UIScale;
import com.jidesoft.swing.*;
import com.jidesoft.swing.CheckBoxTreeCellRenderer;
import com.jidesoft.tree.StyledTreeCellRenderer;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.decorator.ShadingColorHighlighter;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.treetable.FileSystemModel;

/**
 * @author Karl Tauber
 */
public class FlatComponents2Test
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatComponents2Test" );
			frame.useApplyComponentOrientation = true;
			UIManager.put( "FlatLaf.experimental.tree.widePathForLocation", true );
			frame.showFrame( FlatComponents2Test::new );
		} );
	}

	private final TestListModel listModel;
	private final TestTreeModel treeModel;
	private final TestTableModel tableModel;
	@SuppressWarnings( "rawtypes" )
	private final JList[] allLists;
	private final JTree[] allTrees;
	private final List<JTable> allTables = new ArrayList<>();
	private final List<JTable> allTablesInclRowHeader = new ArrayList<>();
	private JTable rowHeaderTable1;

	FlatComponents2Test() {
		initComponents();

		allLists = new JList[] { list1, list2 };

		treeWideSelectionCheckBox.setSelected( UIManager.getBoolean( "Tree.wideSelection" ) );
		allTrees = new JTree[] { tree1, tree2, xTree1, checkBoxTree1 };


		// list model
		listModel = new TestListModel( (Integer) listRowCountSpinner.getValue() );
		list1.setModel( listModel );
		list2.setModel( listModel );

		// tree model
		treeModel = new TestTreeModel( (Integer) treeRowCountSpinner.getValue() );
		for( JTree tree : allTrees )
			tree.setModel( treeModel );

		// table model
		tableModel = new TestTableModel( (Integer) tableRowCountSpinner.getValue() );
		table1.setModel( tableModel );
		xTable1.setModel( tableModel );

		// table header popup menu
		JMenuItem addMenuItem = new JMenuItem( "Add column" );
		addMenuItem.addActionListener( e -> {
			tableModel.setColumnCount( tableModel.getColumnCount() + 1 );
		});
		JMenuItem removeMenuItem = new JMenuItem( "Remove last column" );
		removeMenuItem.addActionListener( e -> {
			tableModel.setColumnCount( tableModel.getColumnCount() - 1 );
		});
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add( addMenuItem );
		popupMenu.add( removeMenuItem );
		table1.getTableHeader().setComponentPopupMenu( popupMenu );

		// table column editors
		initTableEditors( table1 );
		initTableEditors( xTable1 );

		// table selection listeners
		table1.getSelectionModel().addListSelectionListener( e -> {
			System.out.printf( "row sel     %d-%d   adj=%b\n", e.getFirstIndex(), e.getLastIndex(), e.getValueIsAdjusting() );
		} );
		table1.getColumnModel().getSelectionModel().addListSelectionListener( e -> {
			System.out.printf( "column sel  %d-%d   adj=%b\n", e.getFirstIndex(), e.getLastIndex(), e.getValueIsAdjusting() );
		} );

		// JXTable
		Highlighter simpleStriping = HighlighterFactory.createSimpleStriping();
		PatternPredicate patternPredicate = new PatternPredicate( "^J", 2 );
		ColorHighlighter magenta = new ColorHighlighter( patternPredicate, null, Color.MAGENTA, null, Color.MAGENTA );
		ColorHighlighter rollover = new ColorHighlighter( HighlightPredicate.ROLLOVER_ROW, Color.cyan, null );
		Highlighter shading = new ShadingColorHighlighter( new HighlightPredicate.ColumnHighlightPredicate( 1 ) );
		xTable1.setHighlighters( simpleStriping, magenta, rollover, shading );
		xTable1.setColumnControlVisible( true );

		// JXTreeTable
		xTreeTable1.setTreeTableModel( new FileSystemModel( new File( "." ) ) );
		xTreeTable1.setHighlighters( simpleStriping, magenta, rollover, shading );

		allTables.add( table1 );
		allTables.add( xTable1 );
		allTables.add( xTreeTable1 );
		allTablesInclRowHeader.addAll( allTables );

		for( JTree tree : allTrees )
			expandTree( tree );
	}

	private void initTableEditors( JTable table ) {
		TableColumnModel cm = table.getColumnModel();
		String[] months = {
			"January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December"
		};
		cm.getColumn(2).setCellRenderer( new TestComboBoxTableCellRenderer() );
		cm.getColumn(2).setCellEditor( new DefaultCellEditor( new JComboBox<>( months ) ) );
		JComboBox<String> editableComboBox = new JComboBox<>( months );
		editableComboBox.setEditable( true );
		cm.getColumn(3).setCellEditor( new DefaultCellEditor( editableComboBox ) );

//		table.setDefaultRenderer( Object.class, new TestLabelRoundedTableCellRenderer() );
	}

	private void expandTree( JTree tree ) {
		int count = tree.getRowCount();
		for( int i = count - 1; i >= 0; i-- )
			tree.expandRow( i );
	}

	private void listRowCountChanged() {
		listModel.setSize( (Integer) listRowCountSpinner.getValue() );
	}

	private void treeRowCountChanged() {
		int rowCount = (Integer) treeRowCountSpinner.getValue();

		// round to 20
		if( rowCount % 20 != 0 ) {
			rowCount += 20 - (rowCount % 20);
			treeRowCountSpinner.setValue( rowCount );
		}

		int oldCount1 = tree1.getRowCount();
		int oldCount2 = tree2.getRowCount();

		treeModel.setRowCount( rowCount );

		int newCount1 = tree1.getRowCount();
		int newCount2 = tree2.getRowCount();

		// expand added rows
		for( int i = newCount1 - 1; i >= oldCount1; i-- )
			tree1.expandRow( i );
		for( int i = newCount2 - 1; i >= oldCount2; i-- )
			tree2.expandRow( i );
	}

	private void tableRowCountChanged() {
		tableModel.setRowCount( (Integer) tableRowCountSpinner.getValue() );
	}

	private void autoResizeModeChanged() {
		int autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
		Object sel = autoResizeModeField.getSelectedItem();
		if( sel instanceof String ) {
			switch( (String) sel ) {
				case "off": autoResizeMode = JTable.AUTO_RESIZE_OFF; break;
				case "nextColumn": autoResizeMode = JTable.AUTO_RESIZE_NEXT_COLUMN; break;
				case "subsequentColumns": autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS; break;
				case "lastColumn": autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN; break;
				case "allColumns": autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS; break;
			}
		}
		for( JTable table : allTables )
			table.setAutoResizeMode( autoResizeMode );
	}

	private void sortIconPositionChanged() {
		Object sel = sortIconPositionComboBox.getSelectedItem();
		if( "right".equals( sel ) )
			sel = null;

		UIManager.put( "TableHeader.sortIconPosition", sel );
		FlatLaf.updateUILater();
	}

	private void roundedSelectionChanged() {
		String style = roundedSelectionCheckBox.isSelected() ? "selectionArc: 12; " : "";
		int left = leftSelectionInsetsCheckBox.isSelected() ? 2 : 0;
		int right = rightSelectionInsetsCheckBox.isSelected() ? 2 : 0;
		int top = topSelectionInsetsCheckBox.isSelected() ? 2 : 0;
		int bottom = bottomSelectionInsetsCheckBox.isSelected() ? 2 : 0;
		if( left > 0 || right > 0 || top > 0 || bottom > 0 )
			style += "selectionInsets: " + top + ',' + left + ',' + bottom + ',' + right;
		if( style.isEmpty() )
			style = null;

		list1.putClientProperty( FlatClientProperties.STYLE, style );
		list2.putClientProperty( FlatClientProperties.STYLE, style );
		tree1.putClientProperty( FlatClientProperties.STYLE, style );
		tree2.putClientProperty( FlatClientProperties.STYLE, style );
		xTree1.putClientProperty( FlatClientProperties.STYLE, style );
		checkBoxTree1.putClientProperty( FlatClientProperties.STYLE, style );
		table1.putClientProperty( FlatClientProperties.STYLE, style );
		xTable1.putClientProperty( FlatClientProperties.STYLE, style );
		xTreeTable1.putClientProperty( FlatClientProperties.STYLE, style );

		// initial selection
		if( style != null ) {
			initSelection( list1 );
			initSelection( list2 );
			initSelection( tree1 );
			initSelection( tree2 );
			initSelection( xTree1 );
			initSelection( checkBoxTree1 );
			initSelection( table1 );
			initSelection( xTable1 );
			initSelection( xTreeTable1 );
		}

		if( paintOutsideAlternateRowsCheckBox.isSelected() )
			table1ScrollPane.repaint();
	}

	private static void initSelection( JList<?> list ) {
		if( list.isSelectionEmpty() ) {
			list.addSelectionInterval( 1, 2 );
			list.addSelectionInterval( 5, 5 );
		}
	}

	private static void initSelection( JTree tree ) {
		if( tree.isSelectionEmpty() ) {
			tree.addSelectionInterval( 1, 2 );
			tree.addSelectionInterval( 5, 5 );
		}
	}

	private static void initSelection( JTable table ) {
		if( table.getSelectedRowCount() == 0 ) {
			table.addRowSelectionInterval( 1, 2 );
			table.addRowSelectionInterval( 5, 5 );
		}
	}

	private void dndChanged() {
		boolean dnd = dndCheckBox.isSelected();
		list1.setDragEnabled( dnd );
		list2.setDragEnabled( dnd );
		tree1.setDragEnabled( dnd );
		tree2.setDragEnabled( dnd );
		table1.setDragEnabled( dnd );
		xTable1.setDragEnabled( dnd );
		xTreeTable1.setDragEnabled( dnd );

		DropMode dropMode = dnd ? DropMode.ON_OR_INSERT : DropMode.USE_SELECTION;
		list1.setDropMode( dropMode );
		tree1.setDropMode( dropMode );
		table1.setDropMode( dropMode );
		xTable1.setDropMode( dropMode );
		xTreeTable1.setDropMode( dropMode );

		String key = "FlatLaf.oldTransferHandler";
		if( dnd ) {
			list1.putClientProperty( key, list1.getTransferHandler() );
			list1.setTransferHandler( new DummyTransferHandler() );

			tree1.putClientProperty( key, tree1.getTransferHandler() );
			tree1.setTransferHandler( new DummyTransferHandler() );

			table1.putClientProperty( key, table1.getTransferHandler() );
			xTable1.putClientProperty( key, xTable1.getTransferHandler() );
			xTreeTable1.putClientProperty( key, xTreeTable1.getTransferHandler() );
			table1.setTransferHandler( new DummyTransferHandler() );
			xTable1.setTransferHandler( new DummyTransferHandler() );
			xTreeTable1.setTransferHandler( new DummyTransferHandler() );
		} else {
			list1.setTransferHandler( (TransferHandler) list1.getClientProperty( key ) );
			tree1.setTransferHandler( (TransferHandler) tree1.getClientProperty( key ) );
			table1.setTransferHandler( (TransferHandler) table1.getClientProperty( key ) );
			xTable1.setTransferHandler( (TransferHandler) xTable1.getClientProperty( key ) );
			xTreeTable1.setTransferHandler( (TransferHandler) xTreeTable1.getClientProperty( key ) );
		}
	}

	private void tableHeaderButtonChanged() {
		tableHeaderButtonChanged( table1ScrollPane );
		tableHeaderButtonChanged( xTable1ScrollPane );
		tableHeaderButtonChanged( xTreeTable1ScrollPane );
	}

	private void tableHeaderButtonChanged( JScrollPane scrollPane ) {
		boolean show = tableHeaderButtonCheckBox.isSelected();
		JButton button = null;
		if( show ) {
			button = new JButton( new FlatMenuArrowIcon() );
			button.applyComponentOrientation( getComponentOrientation() );
			button.addActionListener( e -> {
				JOptionPane.showMessageDialog( this, "hello" );
			} );
		}
		scrollPane.setCorner( JScrollPane.UPPER_TRAILING_CORNER, button );
	}

	private void rowSelectionChanged() {
		for( JTable table : allTables )
			table.setRowSelectionAllowed( rowSelectionCheckBox.isSelected() );
	}

	private void columnSelectionChanged() {
		for( JTable table : allTables )
			table.setColumnSelectionAllowed( columnSelectionCheckBox.isSelected() );
	}

	private void showHorizontalLinesChanged() {
		for( JTable table : allTablesInclRowHeader )
			table.setShowHorizontalLines( showHorizontalLinesCheckBox.isSelected() );
	}

	private void showVerticalLinesChanged() {
		for( JTable table : allTablesInclRowHeader )
			table.setShowVerticalLines( showVerticalLinesCheckBox.isSelected() );
	}

	private void intercellSpacingChanged() {
		for( JTable table : allTablesInclRowHeader )
			table.setIntercellSpacing( intercellSpacingCheckBox.isSelected() ? new Dimension( 1, 1 ) : new Dimension() );
	}

	private void redGridColorChanged() {
		for( JTable table : allTablesInclRowHeader )
			table.setGridColor( redGridColorCheckBox.isSelected() ? Color.red : UIManager.getColor( "Table.gridColor" ) );
	}

	private void rowHeaderChanged() {
		if( rowHeaderCheckBox.isSelected() ) {
			TestTableRowHeaderModel rowHeaderModel = new TestTableRowHeaderModel( tableModel );
			rowHeaderTable1 = new JTable( rowHeaderModel );
			rowHeaderTable1.setPreferredScrollableViewportSize( UIScale.scale( new Dimension( 50, 50 ) ) );
			rowHeaderTable1.setSelectionModel( table1.getSelectionModel() );

			DefaultTableCellRenderer rowHeaderRenderer = new DefaultTableCellRenderer();
			rowHeaderRenderer.setHorizontalAlignment( JLabel.CENTER );
			rowHeaderTable1.setDefaultRenderer( Object.class, rowHeaderRenderer );
			table1ScrollPane.setRowHeaderView( rowHeaderTable1 );

			JViewport headerViewport = new JViewport();
			headerViewport.setView( rowHeaderTable1.getTableHeader() );
			table1ScrollPane.setCorner( ScrollPaneConstants.UPPER_LEADING_CORNER, headerViewport );

			table1ScrollPane.applyComponentOrientation( getComponentOrientation() );

			allTablesInclRowHeader.add( rowHeaderTable1 );

			showHorizontalLinesChanged();
			showVerticalLinesChanged();
			intercellSpacingChanged();
			redGridColorChanged();
		} else {
			table1ScrollPane.setRowHeader( null );
			table1ScrollPane.setCorner( ScrollPaneConstants.UPPER_LEADING_CORNER, null );
			allTablesInclRowHeader.remove( rowHeaderTable1 );

			((TestTableRowHeaderModel)rowHeaderTable1.getModel()).dispose();
			rowHeaderTable1 = null;
		}
	}

	private void focusCellEditorChanged() {
		for( JTable table : allTables )
			table.setSurrendersFocusOnKeystroke( focusCellEditorCheckBox.isSelected() );
	}

	private void alternatingRowsChanged() {
		UIManager.put( "Table.alternateRowColor", alternatingRowsCheckBox.isSelected() ? Color.orange : null );
		table1ScrollPane.repaint();
	}

	private void paintOutsideAlternateRowsChanged() {
		UIManager.put( "Table.paintOutsideAlternateRows", paintOutsideAlternateRowsCheckBox.isSelected() ? true : null );
		table1ScrollPane.repaint();
	}

	@SuppressWarnings( "unchecked" )
	private void listRendererChanged() {
		Object sel = listRendererComboBox.getSelectedItem();
		if( !(sel instanceof String) )
			return;

		switch( (String) sel ) {
			case "default":
				for( JList<String> list : allLists )
					list.setCellRenderer( new DefaultListCellRenderer() );
				break;

			case "defaultSubclass":
				for( JList<String> list : allLists )
					list.setCellRenderer( new TestDefaultListCellRenderer() );
				break;

			case "label":
				for( JList<String> list : allLists )
					list.setCellRenderer( new TestLabelListCellRenderer() );
				break;

			case "labelRounded":
				for( JList<String> list : allLists )
					list.setCellRenderer( new TestLabelRoundedListCellRenderer() );
				break;
		}

		String style = sel.equals( "labelRounded" )
			? "selectionArc: 6; selectionInsets: 0,1,0,1"
			: null;
		for( JList<String> list : allLists )
			list.putClientProperty( FlatClientProperties.STYLE, style );
	}

	private void listLayoutOrientationChanged() {
		int layoutOrientation = JList.VERTICAL;
		Object sel = listLayoutOrientationField.getSelectedItem();
		if( sel instanceof String ) {
			switch( (String) sel ) {
				case "vertical": layoutOrientation = JList.VERTICAL; break;
				case "vertical wrap": layoutOrientation = JList.VERTICAL_WRAP; break;
				case "horzontal wrap": layoutOrientation = JList.HORIZONTAL_WRAP; break;
			}
		}
		for( JList<?> list : allLists )
			list.setLayoutOrientation( layoutOrientation );
	}

	private void listVisibleRowCountChanged() {
		int visibleRowCount = (Integer) listVisibleRowCountSpinner.getValue();
		for( JList<?> list : allLists )
			list.setVisibleRowCount( visibleRowCount );
	}

	private void listAlternatingRowsChanged() {
		UIManager.put( "List.alternateRowColor", listAlternatingRowsCheckBox.isSelected() ? Color.YELLOW : null );
		FlatLaf.updateUILater();
	}

	private void treeRendererChanged() {
		Object sel = treeRendererComboBox.getSelectedItem();
		if( !(sel instanceof String) )
			return;

		Supplier<TreeCellRenderer> creator;
		switch( (String) sel ) {
			default:
			case "default":				creator = DefaultTreeCellRenderer::new; break;
			case "defaultSubclass":		creator = TestDefaultTreeCellRenderer::new; break;
			case "defaultWithIcons":	creator = TestDefaultWithIconsTreeCellRenderer::new; break;
			case "defaultWithIcon":		creator = TestDefaultWithIconTreeCellRenderer::new; break;
			case "label":				creator = TestLabelTreeCellRenderer::new; break;
			case "wide":				creator = TestWideTreeCellRenderer::new; break;
			case "swingxDefault":		creator = DefaultTreeRenderer::new; break;
			case "jideCheckBox":		creator = () -> new CheckBoxTreeCellRenderer( new DefaultTreeCellRenderer() ); break;
			case "jideStyled":			creator = StyledTreeCellRenderer::new; break;
		}

		JTree[] trees = { tree1, tree2, xTree1 };
		for( JTree tree : trees )
			tree.setCellRenderer( creator.get() );
	}

	private void treeWideSelectionChanged() {
		boolean wideSelection = treeWideSelectionCheckBox.isSelected();
		for( JTree tree : allTrees )
			tree.putClientProperty( FlatClientProperties.TREE_WIDE_SELECTION, wideSelection );
	}

	private void treeWideCellRendererChanged() {
		boolean wideCellRenderer = treeWideCellRendererCheckBox.isSelected();
		for( JTree tree : allTrees )
			tree.putClientProperty( FlatClientProperties.TREE_WIDE_CELL_RENDERER, wideCellRenderer );
	}

	private void treeAlternatingRowsChanged() {
		UIManager.put( "Tree.alternateRowColor", treeAlternatingRowsCheckBox.isSelected() ? Color.cyan : null );
		FlatLaf.updateUILater();
	}

	private void treePaintSelectionChanged() {
		boolean paintSelection = treePaintSelectionCheckBox.isSelected();
		for( JTree tree : allTrees )
			tree.putClientProperty( FlatClientProperties.TREE_PAINT_SELECTION, paintSelection );
	}

	private void treePaintLinesChanged() {
		boolean paintLines = treePaintLinesCheckBox.isSelected();
		UIManager.put( "Tree.paintLines", paintLines ? true : null );
		for( JTree tree : allTrees )
			tree.updateUI();

		treeRedLinesCheckBox.setEnabled( paintLines );
	}

	private void treeRedLinesChanged() {
		boolean redLines = treeRedLinesCheckBox.isSelected();
		UIManager.put( "Tree.hash", redLines ? Color.red : null );
		for( JTree tree : allTrees )
			tree.updateUI();
	}

	private void treeEditableChanged() {
		boolean editable = treeEditableCheckBox.isSelected();
		for( JTree tree : allTrees )
			tree.setEditable( editable );
	}

	private void treeShowDefaultIconsChanged() {
		boolean showDefaultIcons = treeShowDefaultIconsCheckBox.isSelected();
		UIManager.put( "Tree.showDefaultIcons", showDefaultIcons ? true : null );
		for( JTree tree : allTrees )
			tree.updateUI();

		treeRendererChanged();
	}

	private void treeMouseClicked( MouseEvent e ) {
		JTree tree = (JTree) e.getSource();
		int x = e.getX();
		int y = e.getY();

		TreePath path = tree.getPathForLocation( x, y );
		TreePath closestPath = tree.getClosestPathForLocation( x, y );
		int row = tree.getRowForLocation( x, y );
		int closestRow = tree.getClosestRowForLocation( x, y );

		System.out.println( "---- tree mouseClicked " + x + "," + y + " ----" );
		System.out.println( "        path:  " + path );
		System.out.println( "closest path:  " + closestPath );
		System.out.println( "        row:   " + row );
		System.out.println( "closest row:   " + closestRow );
	}

	@Override
	public void applyComponentOrientation( ComponentOrientation o ) {
		super.applyComponentOrientation( o );

		// always use left-to-right for options panels
		generalOptionsPanel.applyComponentOrientation( ComponentOrientation.LEFT_TO_RIGHT );
		listOptionsPanel.applyComponentOrientation( ComponentOrientation.LEFT_TO_RIGHT );
		treeOptionsPanel.applyComponentOrientation( ComponentOrientation.LEFT_TO_RIGHT );
		tableOptionsPanel.applyComponentOrientation( ComponentOrientation.LEFT_TO_RIGHT );

		// swap upper right and left corners (other corners are not used in this app)
		Component leftCorner = table1ScrollPane.getCorner( ScrollPaneConstants.UPPER_LEFT_CORNER );
		Component rightCorner = table1ScrollPane.getCorner( ScrollPaneConstants.UPPER_RIGHT_CORNER );
		table1ScrollPane.setCorner( ScrollPaneConstants.UPPER_LEFT_CORNER, null );
		table1ScrollPane.setCorner( ScrollPaneConstants.UPPER_RIGHT_CORNER, null );
		table1ScrollPane.setCorner( ScrollPaneConstants.UPPER_LEFT_CORNER, rightCorner );
		table1ScrollPane.setCorner( ScrollPaneConstants.UPPER_RIGHT_CORNER, leftCorner );
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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JTextField textField1 = new JTextField();
		JTextField textField2 = new JTextField();
		JTextField textField3 = new JTextField();
		JPanel panel1 = new JPanel();
		JLabel listLabel = new JLabel();
		JLabel listRowCountLabel = new JLabel();
		listRowCountSpinner = new JSpinner();
		JScrollPane scrollPane1 = new JScrollPane();
		list1 = new JList<>();
		JScrollPane scrollPane2 = new JScrollPane();
		list2 = new JList<>();
		JPanel panel3 = new JPanel();
		JLabel tableLabel = new JLabel();
		JLabel tableRowCountLabel = new JLabel();
		tableRowCountSpinner = new JSpinner();
		table1ScrollPane = new JScrollPane();
		table1 = new JTable();
		JPanel panel2 = new JPanel();
		JLabel treeLabel = new JLabel();
		JLabel treeRowCountLabel = new JLabel();
		treeRowCountSpinner = new JSpinner();
		JScrollPane scrollPane3 = new JScrollPane();
		tree1 = new JTree();
		JScrollPane scrollPane4 = new JScrollPane();
		tree2 = new JTree();
		JLabel label1 = new JLabel();
		xTable1ScrollPane = new JScrollPane();
		xTable1 = new JXTable();
		JPanel panel4 = new JPanel();
		JLabel label3 = new JLabel();
		JLabel label4 = new JLabel();
		JLabel label5 = new JLabel();
		JScrollPane scrollPane5 = new JScrollPane();
		xTree1 = new JXTree();
		JScrollPane scrollPane6 = new JScrollPane();
		checkBoxTree1 = new CheckBoxTree();
		JLabel label2 = new JLabel();
		xTreeTable1ScrollPane = new JScrollPane();
		xTreeTable1 = new JXTreeTable();
		generalOptionsPanel = new JPanel();
		roundedSelectionCheckBox = new JCheckBox();
		JLabel label6 = new JLabel();
		topSelectionInsetsCheckBox = new JCheckBox();
		bottomSelectionInsetsCheckBox = new JCheckBox();
		leftSelectionInsetsCheckBox = new JCheckBox();
		rightSelectionInsetsCheckBox = new JCheckBox();
		dndCheckBox = new JCheckBox();
		listOptionsPanel = new JPanel();
		JLabel listRendererLabel = new JLabel();
		listRendererComboBox = new JComboBox<>();
		JLabel listLayoutOrientationLabel = new JLabel();
		listLayoutOrientationField = new JComboBox<>();
		JLabel listVisibleRowCountLabel = new JLabel();
		listVisibleRowCountSpinner = new JSpinner();
		listAlternatingRowsCheckBox = new JCheckBox();
		treeOptionsPanel = new JPanel();
		JLabel treeRendererLabel = new JLabel();
		treeRendererComboBox = new JComboBox<>();
		treeWideSelectionCheckBox = new JCheckBox();
		treeWideCellRendererCheckBox = new JCheckBox();
		treePaintSelectionCheckBox = new JCheckBox();
		treeAlternatingRowsCheckBox = new JCheckBox();
		treePaintLinesCheckBox = new JCheckBox();
		treeRedLinesCheckBox = new JCheckBox();
		treeEditableCheckBox = new JCheckBox();
		treeShowDefaultIconsCheckBox = new JCheckBox();
		tableOptionsPanel = new JPanel();
		JLabel autoResizeModeLabel = new JLabel();
		autoResizeModeField = new JComboBox<>();
		JLabel sortIconPositionLabel = new JLabel();
		sortIconPositionComboBox = new JComboBox<>();
		showHorizontalLinesCheckBox = new JCheckBox();
		rowSelectionCheckBox = new JCheckBox();
		focusCellEditorCheckBox = new JCheckBox();
		showVerticalLinesCheckBox = new JCheckBox();
		columnSelectionCheckBox = new JCheckBox();
		alternatingRowsCheckBox = new JCheckBox();
		intercellSpacingCheckBox = new JCheckBox();
		rowHeaderCheckBox = new JCheckBox();
		paintOutsideAlternateRowsCheckBox = new JCheckBox();
		redGridColorCheckBox = new JCheckBox();
		tableHeaderButtonCheckBox = new JCheckBox();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[200,grow,sizegroup 1,fill]" +
			"[200,grow,sizegroup 1,fill]para" +
			"[fill]" +
			"[200,grow,sizegroup 1,fill]" +
			"[200,grow,sizegroup 1,fill]",
			// rows
			"[]" +
			"[150,grow,sizegroup 1,fill]" +
			"[150,grow,sizegroup 1,fill]" +
			"[150,grow,sizegroup 1,fill]" +
			"[fill]"));

		//---- textField1 ----
		textField1.setText("item");
		add(textField1, "cell 1 0,growx");

		//---- textField2 ----
		textField2.setText("item (check vertical text alignment)");
		textField2.setEnabled(false);
		add(textField2, "cell 2 0,growx");

		//---- textField3 ----
		textField3.setText("item");
		add(textField3, "cell 4 0 2 1,growx");

		//======== panel1 ========
		{
			panel1.putClientProperty("FlatLaf.internal.testing.ignore", true);
			panel1.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[grow]" +
				"[]0" +
				"[]"));

			//---- listLabel ----
			listLabel.setText("JList:");
			panel1.add(listLabel, "cell 0 0,aligny top,growy 0");

			//---- listRowCountLabel ----
			listRowCountLabel.setText("Row count:");
			panel1.add(listRowCountLabel, "cell 0 2");

			//---- listRowCountSpinner ----
			listRowCountSpinner.setModel(new SpinnerNumberModel(20, 0, null, 10));
			listRowCountSpinner.addChangeListener(e -> listRowCountChanged());
			panel1.add(listRowCountSpinner, "cell 0 3");
		}
		add(panel1, "cell 0 1");

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(list1);
		}
		add(scrollPane1, "cell 1 1");

		//======== scrollPane2 ========
		{

			//---- list2 ----
			list2.setEnabled(false);
			scrollPane2.setViewportView(list2);
		}
		add(scrollPane2, "cell 2 1");

		//======== panel3 ========
		{
			panel3.putClientProperty("FlatLaf.internal.testing.ignore", true);
			panel3.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[grow]" +
				"[]0" +
				"[]"));

			//---- tableLabel ----
			tableLabel.setText("JTable:");
			panel3.add(tableLabel, "cell 0 0");

			//---- tableRowCountLabel ----
			tableRowCountLabel.setText("Row count:");
			panel3.add(tableRowCountLabel, "cell 0 2");

			//---- tableRowCountSpinner ----
			tableRowCountSpinner.setModel(new SpinnerNumberModel(20, 0, null, 5));
			tableRowCountSpinner.addChangeListener(e -> tableRowCountChanged());
			panel3.add(tableRowCountSpinner, "cell 0 3");
		}
		add(panel3, "cell 3 1");

		//======== table1ScrollPane ========
		{

			//---- table1 ----
			table1.setAutoCreateRowSorter(true);
			table1ScrollPane.setViewportView(table1);
		}
		add(table1ScrollPane, "cell 4 1 2 1,width 300");

		//======== panel2 ========
		{
			panel2.putClientProperty("FlatLaf.internal.testing.ignore", true);
			panel2.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[grow]" +
				"[]0" +
				"[]"));

			//---- treeLabel ----
			treeLabel.setText("JTree:");
			panel2.add(treeLabel, "cell 0 0,aligny top,growy 0");

			//---- treeRowCountLabel ----
			treeRowCountLabel.setText("Row count:");
			panel2.add(treeRowCountLabel, "cell 0 2");

			//---- treeRowCountSpinner ----
			treeRowCountSpinner.setModel(new SpinnerNumberModel(20, 20, null, 20));
			treeRowCountSpinner.addChangeListener(e -> treeRowCountChanged());
			panel2.add(treeRowCountSpinner, "cell 0 3");
		}
		add(panel2, "cell 0 2");

		//======== scrollPane3 ========
		{

			//---- tree1 ----
			tree1.setShowsRootHandles(true);
			tree1.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					treeMouseClicked(e);
				}
			});
			scrollPane3.setViewportView(tree1);
		}
		add(scrollPane3, "cell 1 2");

		//======== scrollPane4 ========
		{

			//---- tree2 ----
			tree2.setEnabled(false);
			tree2.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					treeMouseClicked(e);
				}
			});
			scrollPane4.setViewportView(tree2);
		}
		add(scrollPane4, "cell 2 2");

		//---- label1 ----
		label1.setText("JXTable:");
		add(label1, "cell 3 2,aligny top,growy 0");

		//======== xTable1ScrollPane ========
		{
			xTable1ScrollPane.setViewportView(xTable1);
		}
		add(xTable1ScrollPane, "cell 4 2 2 1");

		//======== panel4 ========
		{
			panel4.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[]0" +
				"[]"));

			//---- label3 ----
			label3.setText("JXTree:");
			panel4.add(label3, "cell 0 0");

			//---- label4 ----
			label4.setText("CheckBoxTree:");
			panel4.add(label4, "cell 0 1");

			//---- label5 ----
			label5.setText("(JIDE)");
			panel4.add(label5, "cell 0 2");
		}
		add(panel4, "cell 0 3");

		//======== scrollPane5 ========
		{

			//---- xTree1 ----
			xTree1.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					treeMouseClicked(e);
				}
			});
			scrollPane5.setViewportView(xTree1);
		}
		add(scrollPane5, "cell 1 3");

		//======== scrollPane6 ========
		{

			//---- checkBoxTree1 ----
			checkBoxTree1.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					treeMouseClicked(e);
				}
			});
			scrollPane6.setViewportView(checkBoxTree1);
		}
		add(scrollPane6, "cell 2 3");

		//---- label2 ----
		label2.setText("JXTreeTable:");
		add(label2, "cell 3 3,aligny top,growy 0");

		//======== xTreeTable1ScrollPane ========
		{
			xTreeTable1ScrollPane.setViewportView(xTreeTable1);
		}
		add(xTreeTable1ScrollPane, "cell 4 3 2 1");

		//======== generalOptionsPanel ========
		{
			generalOptionsPanel.setBorder(new TitledBorder("General Control"));
			generalOptionsPanel.putClientProperty("FlatLaf.internal.testing.ignore", true);
			generalOptionsPanel.setLayout(new MigLayout(
				"insets 8,hidemode 3",
				// columns
				"[left]",
				// rows
				"[]" +
				"[]0" +
				"[]0" +
				"[]rel" +
				"[]"));

			//---- roundedSelectionCheckBox ----
			roundedSelectionCheckBox.setText("rounded selection");
			roundedSelectionCheckBox.setMnemonic('D');
			roundedSelectionCheckBox.addActionListener(e -> roundedSelectionChanged());
			generalOptionsPanel.add(roundedSelectionCheckBox, "cell 0 0");

			//---- label6 ----
			label6.setText("Selection insets:");
			generalOptionsPanel.add(label6, "cell 0 1");

			//---- topSelectionInsetsCheckBox ----
			topSelectionInsetsCheckBox.setText("top");
			topSelectionInsetsCheckBox.setMnemonic('D');
			topSelectionInsetsCheckBox.addActionListener(e -> roundedSelectionChanged());
			generalOptionsPanel.add(topSelectionInsetsCheckBox, "cell 0 2,gapx ind");

			//---- bottomSelectionInsetsCheckBox ----
			bottomSelectionInsetsCheckBox.setText("bottom");
			bottomSelectionInsetsCheckBox.setMnemonic('D');
			bottomSelectionInsetsCheckBox.addActionListener(e -> roundedSelectionChanged());
			generalOptionsPanel.add(bottomSelectionInsetsCheckBox, "cell 0 2");

			//---- leftSelectionInsetsCheckBox ----
			leftSelectionInsetsCheckBox.setText("left");
			leftSelectionInsetsCheckBox.setMnemonic('D');
			leftSelectionInsetsCheckBox.addActionListener(e -> roundedSelectionChanged());
			generalOptionsPanel.add(leftSelectionInsetsCheckBox, "cell 0 3,gapx ind");

			//---- rightSelectionInsetsCheckBox ----
			rightSelectionInsetsCheckBox.setText("right");
			rightSelectionInsetsCheckBox.setMnemonic('D');
			rightSelectionInsetsCheckBox.addActionListener(e -> roundedSelectionChanged());
			generalOptionsPanel.add(rightSelectionInsetsCheckBox, "cell 0 3");

			//---- dndCheckBox ----
			dndCheckBox.setText("drag and drop");
			dndCheckBox.setMnemonic('D');
			dndCheckBox.addActionListener(e -> dndChanged());
			generalOptionsPanel.add(dndCheckBox, "cell 0 4");
		}
		add(generalOptionsPanel, "cell 0 4 4 1");

		//======== listOptionsPanel ========
		{
			listOptionsPanel.setBorder(new TitledBorder("JList Control"));
			listOptionsPanel.setLayout(new MigLayout(
				"insets 8,hidemode 3",
				// columns
				"[fill]" +
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//---- listRendererLabel ----
			listRendererLabel.setText("Renderer:");
			listOptionsPanel.add(listRendererLabel, "cell 0 0");

			//---- listRendererComboBox ----
			listRendererComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
				"default",
				"defaultSubclass",
				"label",
				"labelRounded"
			}));
			listRendererComboBox.addActionListener(e -> listRendererChanged());
			listOptionsPanel.add(listRendererComboBox, "cell 1 0");

			//---- listLayoutOrientationLabel ----
			listLayoutOrientationLabel.setText("Orientation:");
			listOptionsPanel.add(listLayoutOrientationLabel, "cell 0 1");

			//---- listLayoutOrientationField ----
			listLayoutOrientationField.setModel(new DefaultComboBoxModel<>(new String[] {
				"vertical",
				"vertical wrap",
				"horzontal wrap"
			}));
			listLayoutOrientationField.addActionListener(e -> listLayoutOrientationChanged());
			listOptionsPanel.add(listLayoutOrientationField, "cell 1 1");

			//---- listVisibleRowCountLabel ----
			listVisibleRowCountLabel.setText("Visible row count:");
			listOptionsPanel.add(listVisibleRowCountLabel, "cell 0 2");

			//---- listVisibleRowCountSpinner ----
			listVisibleRowCountSpinner.setModel(new SpinnerNumberModel(8, 0, null, 1));
			listVisibleRowCountSpinner.addChangeListener(e -> listVisibleRowCountChanged());
			listOptionsPanel.add(listVisibleRowCountSpinner, "cell 1 2");

			//---- listAlternatingRowsCheckBox ----
			listAlternatingRowsCheckBox.setText("alternating rows");
			listAlternatingRowsCheckBox.addActionListener(e -> listAlternatingRowsChanged());
			listOptionsPanel.add(listAlternatingRowsCheckBox, "cell 0 3 2 1,alignx left,growx 0");
		}
		add(listOptionsPanel, "cell 0 4 4 1");

		//======== treeOptionsPanel ========
		{
			treeOptionsPanel.setBorder(new TitledBorder("JTree Control"));
			treeOptionsPanel.putClientProperty("FlatLaf.internal.testing.ignore", true);
			treeOptionsPanel.setLayout(new MigLayout(
				"insets 8,hidemode 3",
				// columns
				"[left]",
				// rows
				"[]" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]"));

			//---- treeRendererLabel ----
			treeRendererLabel.setText("Renderer:");
			treeOptionsPanel.add(treeRendererLabel, "cell 0 0");

			//---- treeRendererComboBox ----
			treeRendererComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
				"default",
				"defaultSubclass",
				"defaultWithIcons",
				"defaultWithIcon",
				"label",
				"wide",
				"swingxDefault",
				"jideCheckBox",
				"jideStyled"
			}));
			treeRendererComboBox.addActionListener(e -> treeRendererChanged());
			treeOptionsPanel.add(treeRendererComboBox, "cell 0 0");

			//---- treeWideSelectionCheckBox ----
			treeWideSelectionCheckBox.setText("wide selection");
			treeWideSelectionCheckBox.addActionListener(e -> treeWideSelectionChanged());
			treeOptionsPanel.add(treeWideSelectionCheckBox, "cell 0 1");

			//---- treeWideCellRendererCheckBox ----
			treeWideCellRendererCheckBox.setText("wide cell renderer");
			treeWideCellRendererCheckBox.addActionListener(e -> treeWideCellRendererChanged());
			treeOptionsPanel.add(treeWideCellRendererCheckBox, "cell 0 1");

			//---- treePaintSelectionCheckBox ----
			treePaintSelectionCheckBox.setText("paint selection");
			treePaintSelectionCheckBox.setSelected(true);
			treePaintSelectionCheckBox.addActionListener(e -> treePaintSelectionChanged());
			treeOptionsPanel.add(treePaintSelectionCheckBox, "cell 0 2");

			//---- treeAlternatingRowsCheckBox ----
			treeAlternatingRowsCheckBox.setText("alternating rows");
			treeAlternatingRowsCheckBox.addActionListener(e -> treeAlternatingRowsChanged());
			treeOptionsPanel.add(treeAlternatingRowsCheckBox, "cell 0 2");

			//---- treePaintLinesCheckBox ----
			treePaintLinesCheckBox.setText("paint lines");
			treePaintLinesCheckBox.addActionListener(e -> treePaintLinesChanged());
			treeOptionsPanel.add(treePaintLinesCheckBox, "cell 0 3");

			//---- treeRedLinesCheckBox ----
			treeRedLinesCheckBox.setText("red lines");
			treeRedLinesCheckBox.setEnabled(false);
			treeRedLinesCheckBox.addActionListener(e -> treeRedLinesChanged());
			treeOptionsPanel.add(treeRedLinesCheckBox, "cell 0 3");

			//---- treeEditableCheckBox ----
			treeEditableCheckBox.setText("editable");
			treeEditableCheckBox.addActionListener(e -> treeEditableChanged());
			treeOptionsPanel.add(treeEditableCheckBox, "cell 0 4");

			//---- treeShowDefaultIconsCheckBox ----
			treeShowDefaultIconsCheckBox.setText("show default icons");
			treeShowDefaultIconsCheckBox.addActionListener(e -> treeShowDefaultIconsChanged());
			treeOptionsPanel.add(treeShowDefaultIconsCheckBox, "cell 0 4");
		}
		add(treeOptionsPanel, "cell 0 4 4 1");

		//======== tableOptionsPanel ========
		{
			tableOptionsPanel.setBorder(new TitledBorder("JTable Control"));
			tableOptionsPanel.putClientProperty("FlatLaf.internal.testing.ignore", true);
			tableOptionsPanel.setLayout(new MigLayout(
				"insets 8,hidemode 3",
				// columns
				"[]" +
				"[fill]" +
				"[fill]",
				// rows
				"[]" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0"));

			//---- autoResizeModeLabel ----
			autoResizeModeLabel.setText("Auto resize mode:");
			tableOptionsPanel.add(autoResizeModeLabel, "cell 0 0 3 1");

			//---- autoResizeModeField ----
			autoResizeModeField.setModel(new DefaultComboBoxModel<>(new String[] {
				"off",
				"nextColumn",
				"subsequentColumns",
				"lastColumn",
				"allColumns"
			}));
			autoResizeModeField.setSelectedIndex(2);
			autoResizeModeField.addActionListener(e -> autoResizeModeChanged());
			tableOptionsPanel.add(autoResizeModeField, "cell 0 0 3 1");

			//---- sortIconPositionLabel ----
			sortIconPositionLabel.setText("Sort icon:");
			tableOptionsPanel.add(sortIconPositionLabel, "cell 0 0 3 1");

			//---- sortIconPositionComboBox ----
			sortIconPositionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
				"right",
				"left",
				"top",
				"bottom"
			}));
			sortIconPositionComboBox.addActionListener(e -> sortIconPositionChanged());
			tableOptionsPanel.add(sortIconPositionComboBox, "cell 0 0 3 1");

			//---- showHorizontalLinesCheckBox ----
			showHorizontalLinesCheckBox.setText("show horizontal lines");
			showHorizontalLinesCheckBox.addActionListener(e -> showHorizontalLinesChanged());
			tableOptionsPanel.add(showHorizontalLinesCheckBox, "cell 0 1");

			//---- rowSelectionCheckBox ----
			rowSelectionCheckBox.setText("row selection");
			rowSelectionCheckBox.setSelected(true);
			rowSelectionCheckBox.addActionListener(e -> rowSelectionChanged());
			tableOptionsPanel.add(rowSelectionCheckBox, "cell 1 1");

			//---- focusCellEditorCheckBox ----
			focusCellEditorCheckBox.setText("focus cell editor");
			focusCellEditorCheckBox.addActionListener(e -> focusCellEditorChanged());
			tableOptionsPanel.add(focusCellEditorCheckBox, "cell 2 1");

			//---- showVerticalLinesCheckBox ----
			showVerticalLinesCheckBox.setText("show vertical lines");
			showVerticalLinesCheckBox.addActionListener(e -> showVerticalLinesChanged());
			tableOptionsPanel.add(showVerticalLinesCheckBox, "cell 0 2");

			//---- columnSelectionCheckBox ----
			columnSelectionCheckBox.setText("column selection");
			columnSelectionCheckBox.addActionListener(e -> columnSelectionChanged());
			tableOptionsPanel.add(columnSelectionCheckBox, "cell 1 2");

			//---- alternatingRowsCheckBox ----
			alternatingRowsCheckBox.setText("alternating rows");
			alternatingRowsCheckBox.addActionListener(e -> alternatingRowsChanged());
			tableOptionsPanel.add(alternatingRowsCheckBox, "cell 2 2");

			//---- intercellSpacingCheckBox ----
			intercellSpacingCheckBox.setText("intercell spacing");
			intercellSpacingCheckBox.addActionListener(e -> intercellSpacingChanged());
			tableOptionsPanel.add(intercellSpacingCheckBox, "cell 0 3");

			//---- rowHeaderCheckBox ----
			rowHeaderCheckBox.setText("row header");
			rowHeaderCheckBox.addActionListener(e -> rowHeaderChanged());
			tableOptionsPanel.add(rowHeaderCheckBox, "cell 1 3");

			//---- paintOutsideAlternateRowsCheckBox ----
			paintOutsideAlternateRowsCheckBox.setText("outside alternating rows");
			paintOutsideAlternateRowsCheckBox.addActionListener(e -> paintOutsideAlternateRowsChanged());
			tableOptionsPanel.add(paintOutsideAlternateRowsCheckBox, "cell 2 3");

			//---- redGridColorCheckBox ----
			redGridColorCheckBox.setText("red grid color");
			redGridColorCheckBox.addActionListener(e -> redGridColorChanged());
			tableOptionsPanel.add(redGridColorCheckBox, "cell 0 4");

			//---- tableHeaderButtonCheckBox ----
			tableHeaderButtonCheckBox.setText("show button in table header");
			tableHeaderButtonCheckBox.addActionListener(e -> tableHeaderButtonChanged());
			tableOptionsPanel.add(tableHeaderButtonCheckBox, "cell 1 4 2 1");
		}
		add(tableOptionsPanel, "cell 4 4 2 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JSpinner listRowCountSpinner;
	private JList<String> list1;
	private JList<String> list2;
	private JSpinner tableRowCountSpinner;
	private JScrollPane table1ScrollPane;
	private JTable table1;
	private JSpinner treeRowCountSpinner;
	private JTree tree1;
	private JTree tree2;
	private JScrollPane xTable1ScrollPane;
	private JXTable xTable1;
	private JXTree xTree1;
	private CheckBoxTree checkBoxTree1;
	private JScrollPane xTreeTable1ScrollPane;
	private JXTreeTable xTreeTable1;
	private JPanel generalOptionsPanel;
	private JCheckBox roundedSelectionCheckBox;
	private JCheckBox topSelectionInsetsCheckBox;
	private JCheckBox bottomSelectionInsetsCheckBox;
	private JCheckBox leftSelectionInsetsCheckBox;
	private JCheckBox rightSelectionInsetsCheckBox;
	private JCheckBox dndCheckBox;
	private JPanel listOptionsPanel;
	private JComboBox<String> listRendererComboBox;
	private JComboBox<String> listLayoutOrientationField;
	private JSpinner listVisibleRowCountSpinner;
	private JCheckBox listAlternatingRowsCheckBox;
	private JPanel treeOptionsPanel;
	private JComboBox<String> treeRendererComboBox;
	private JCheckBox treeWideSelectionCheckBox;
	private JCheckBox treeWideCellRendererCheckBox;
	private JCheckBox treePaintSelectionCheckBox;
	private JCheckBox treeAlternatingRowsCheckBox;
	private JCheckBox treePaintLinesCheckBox;
	private JCheckBox treeRedLinesCheckBox;
	private JCheckBox treeEditableCheckBox;
	private JCheckBox treeShowDefaultIconsCheckBox;
	private JPanel tableOptionsPanel;
	private JComboBox<String> autoResizeModeField;
	private JComboBox<String> sortIconPositionComboBox;
	private JCheckBox showHorizontalLinesCheckBox;
	private JCheckBox rowSelectionCheckBox;
	private JCheckBox focusCellEditorCheckBox;
	private JCheckBox showVerticalLinesCheckBox;
	private JCheckBox columnSelectionCheckBox;
	private JCheckBox alternatingRowsCheckBox;
	private JCheckBox intercellSpacingCheckBox;
	private JCheckBox rowHeaderCheckBox;
	private JCheckBox paintOutsideAlternateRowsCheckBox;
	private JCheckBox redGridColorCheckBox;
	private JCheckBox tableHeaderButtonCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	private final String[] randomRowStrings = new String[1000];
	private final Random random = new Random();

	private String randomRowString( int row ) {
		int index = row % randomRowStrings.length;

		String s = randomRowStrings[index];
		if( s != null )
			return s;

		char[] chars = new char[3 + random.nextInt( 15 - 3 )];
		for( int i = 0; i < chars.length; i++ )
			chars[i] = (char) ((i == 0 ? 'A' : 'a') + random.nextInt( 26 ));
		if( chars.length > 6 )
			chars[chars.length / 2] = ' ';
		s = new String( chars );

		randomRowStrings[index] = s;
		return s;
	}

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
			System.out.println( support.getDropLocation() );
			return false;
		}
	}

	//---- class TestListModel ------------------------------------------------

	private class TestListModel
		extends AbstractListModel<String>
	{
		private int size;

		TestListModel( int size ) {
			setSize( size );
		}

		void setSize( int size ) {
			int oldSize = this.size;
			this.size = size;

			// fire event
			if( size > oldSize )
				fireIntervalAdded( this, oldSize, size - 1 );
			else if( size < oldSize )
				fireIntervalRemoved( this, size, oldSize - 1 );
		}

		@Override
		public int getSize() {
			return size;
		}

		@Override
		public String getElementAt( int index ) {
			return (index < 20)
				? "item " + (index + 1) + ((index + 1) % 5 == 0 ? " ####" : "")
				: "item " + (index + 1) + "   " + randomRowString( index );
		}
	}

	//---- TestTreeModel ------------------------------------------------------

	private class TestTreeModel
		extends DefaultTreeModel
	{
		private int rowCount;

		TestTreeModel( int rowCount ) {
			super( new DefaultMutableTreeNode( "JTree" ) );
			setRowCount( rowCount );
		}

		void setRowCount( int rowCount ) {
			if( rowCount < 20 )
				rowCount = 20;

			if( rowCount > this.rowCount ) {
				// add nodes
				int oldRootChildCount = root.getChildCount();
				while( rowCount > this.rowCount ) {
					addTwentyNodes( (DefaultMutableTreeNode) root, this.rowCount );
					this.rowCount += 20;
				}

				// fire event
				int newRootChildCount = root.getChildCount();
				if( newRootChildCount > oldRootChildCount ) {
					int[] childIndices = new int[newRootChildCount - oldRootChildCount];
					for( int i = 0; i < childIndices.length; i++ )
						childIndices[i] = oldRootChildCount + i;
					nodesWereInserted( root, childIndices );
				}
			} else if( rowCount < this.rowCount ) {
				// remove nodes
				int oldRootChildCount = root.getChildCount();
				List<Object> removedChildren = new ArrayList<>();
				while( this.rowCount > rowCount ) {
					int index = root.getChildCount() - 1;
					removedChildren.add( 0, root.getChildAt( index ) );
					((DefaultMutableTreeNode)root).remove( index );
					this.rowCount -= 20;
				}

				// fire event
				int newRootChildCount = root.getChildCount();
				if( newRootChildCount < oldRootChildCount ) {
					int[] childIndices = new int[oldRootChildCount - newRootChildCount];
					for( int i = 0; i < childIndices.length; i++ )
						childIndices[i] = newRootChildCount + i;
					nodesWereRemoved( root, childIndices, removedChildren.toArray() );
				}
			}
		}

		private void addTwentyNodes( DefaultMutableTreeNode root, int firstRowIndex ) {
			if( firstRowIndex == 0 ) {
				DefaultMutableTreeNode n = new DefaultMutableTreeNode( "colors" );
				n.add( new DefaultMutableTreeNode( "blue" ) );
				n.add( new DefaultMutableTreeNode( "violet" ) );
				n.add( new DefaultMutableTreeNode( "red" ) );
				n.add( new DefaultMutableTreeNode( "yellow" ) );
				root.add( n );

				n = new DefaultMutableTreeNode( "sports" );
				n.add( new DefaultMutableTreeNode( "basketball" ) );
				n.add( new DefaultMutableTreeNode( "soccer" ) );
				n.add( new DefaultMutableTreeNode( "football" ) );
				n.add( new DefaultMutableTreeNode( "hockey" ) );
				root.add( n );

				n = new DefaultMutableTreeNode( "food" );
				n.add( new DefaultMutableTreeNode( "hot dogs" ) );

				DefaultMutableTreeNode n2 = new DefaultMutableTreeNode( "pizza" );
				n2.add( new DefaultMutableTreeNode( "pizza aglio e olio" ) );
				n2.add( new DefaultMutableTreeNode( "pizza calabrese" ) );
				n2.add( new DefaultMutableTreeNode( "pizza infernale" ) );
				n2.add( new DefaultMutableTreeNode( "pizza margherita bianca" ) );
				n2.add( new DefaultMutableTreeNode( "pizza quattro stagioni" ) );
				n.add( n2 );

				n.add( new DefaultMutableTreeNode( "ravioli" ) );
				n.add( new DefaultMutableTreeNode( "bananas" ) );
				root.add( n );
			} else {
				DefaultMutableTreeNode n = new DefaultMutableTreeNode( "item " + firstRowIndex + "   " + randomRowString( firstRowIndex ) );
				for( int i = 1; i < 20; i++ ) {
					int index = firstRowIndex + i;
					n.add( new DefaultMutableTreeNode( "item " + index + "   " + randomRowString( index ) ) );
				}
				root.add( n );
			}
		}
	}

	//---- TestTableModel -----------------------------------------------------

	private class TestTableModel
		extends AbstractTableModel
	{
		private final String[] columnNames = {
			"Not editable", "Text", "Combo", "Combo Editable", "Integer", "Boolean"
		};

		private final Class<?>[] columnTypes = {
			Object.class, Object.class, String.class, String.class, Integer.class, Boolean.class
		};

		private final boolean[] columnEditable = {
			false, true, true, true, true, true
		};

		private final Object[][] rows = {
			{ "item 1", "item 1b", "January", "July", 123, null },
			{ "item 2", "item 2b", "February", "August", 456, true },
			{ "item 3", null, "March", null, null, null },
			{ "item 4", null, "April", null, null, null },
			{ "item 5", null, "May", null, null, null },
			{ "item 6", null, "June", null, null, null },
			{ "item 7", null, "July", null, null, null },
			{ "item 8", null, "August", null, null, null },
			{ "item 9", null, "September", null, null, null },
			{ "item 10", null, "October", null, null, null },
			{ "item 11", null, "November", null, null, null },
			{ "item 12", null, "December", null, null, null },
		};

		private int columnCount = columnNames.length;
		private int rowCount = rows.length;
		private final Map<Integer, Object[]> moreRowsMap = new HashMap<>();

		TestTableModel( int rowCount ) {
			setRowCount( rowCount );
		}

		void setColumnCount( int columnCount ) {
			if( columnCount > columnNames.length )
				columnCount = columnNames.length;

			this.columnCount = columnCount;

			// fire event
			fireTableStructureChanged();
		}

		void setRowCount( int rowCount ) {
			int oldRowCount = this.rowCount;
			this.rowCount = rowCount;

			// fire event
			if( rowCount > oldRowCount )
				fireTableRowsInserted( oldRowCount, rowCount - 1 );
			else if( rowCount < oldRowCount )
				fireTableRowsDeleted( rowCount, oldRowCount - 1 );
		}

		@Override
		public int getRowCount() {
			return rowCount;
		}

		@Override
		public int getColumnCount() {
			return columnCount;
		}

		@Override
		public String getColumnName( int columnIndex ) {
			return columnNames[columnIndex];
		}

		@Override
		public Class<?> getColumnClass( int columnIndex ) {
			return columnTypes[columnIndex];
		}

		@Override
		public boolean isCellEditable( int rowIndex, int columnIndex ) {
			return columnEditable[columnIndex];
		}

		@Override
		public Object getValueAt( int rowIndex, int columnIndex ) {
			if( rowIndex < rows.length )
				return rows[rowIndex][columnIndex];

			Object[] row = moreRowsMap.get( rowIndex );
			Object value = (row != null)
				? row[columnIndex]
				: (columnIndex == 1 ? randomRowString( rowIndex ) : null);
			return (columnIndex == 0 && value == null) ? "item " + (rowIndex + 1) : value;
		}

		@Override
		public void setValueAt( Object value, int rowIndex, int columnIndex ) {
			if( rowIndex < rows.length )
				rows[rowIndex][columnIndex] = value;
			else {
				Object[] row = moreRowsMap.computeIfAbsent( rowIndex, k -> new Object[getColumnCount()] );
				row[columnIndex] = value;
			}

			fireTableCellUpdated( rowIndex, columnIndex );
		}
	}

	//---- TestTableRowHeaderModel --------------------------------------------

	private static class TestTableRowHeaderModel
		extends AbstractTableModel
		implements TableModelListener
	{
		private final TableModel model;

		TestTableRowHeaderModel( TableModel model ) {
			this.model = model;

			model.addTableModelListener( this );
		}

		void dispose() {
			model.removeTableModelListener( this );
		}

		@Override
		public int getRowCount() {
			return model.getRowCount();
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public String getColumnName( int columnIndex ) {
			return "Row #";
		}

		@Override
		public Object getValueAt( int rowIndex, int columnIndex ) {
			return rowIndex + 1;
		}

		@Override
		public void tableChanged( TableModelEvent e ) {
			switch( e.getType() ) {
				case TableModelEvent.INSERT:
					fireTableRowsInserted( e.getFirstRow(), e.getLastRow() );
					break;

				case TableModelEvent.DELETE:
					fireTableRowsDeleted( e.getFirstRow(), e.getLastRow() );
					break;
			}
		}
	}

	//---- class TestDefaultListCellRenderer ----------------------------------

	private static class TestDefaultListCellRenderer
		extends DefaultListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent( JList<?> list, Object value, int index,
			boolean isSelected, boolean cellHasFocus )
		{
			super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

			Color nonSelectionBg = null;
			Color nonSelectionFg = null;
			switch( String.valueOf( value ) ) {
				case "item 2":	nonSelectionFg = Color.blue; break;
				case "item 4":	nonSelectionFg = Color.red; break;
				case "item 3":	nonSelectionBg = Color.yellow; break;
				case "item 5":	nonSelectionBg = Color.magenta; break;
			}
			setBackground( isSelected ? Color.green : (nonSelectionBg != null ? nonSelectionBg : list.getBackground()) );
			setForeground( isSelected ? Color.blue : (nonSelectionFg != null ? nonSelectionFg : list.getForeground()) );

			return this;
		}
	}

	//---- class TestLabelListCellRenderer ------------------------------------

	private static class TestLabelListCellRenderer
		extends JLabel
		implements ListCellRenderer<String>
	{
		@Override
		public Component getListCellRendererComponent( JList<? extends String> list,
			String value, int index, boolean isSelected, boolean cellHasFocus )
		{
			setText( String.valueOf( value ) );
			setBackground( isSelected ? Color.green : list.getBackground() );
			setForeground( isSelected ? Color.blue : list.getForeground() );
			setOpaque( true );
			return this;
		}
	}

	//---- class TestLabelRoundedListCellRenderer -----------------------------

	private static class TestLabelRoundedListCellRenderer
		extends JLabel
		implements ListCellRenderer<String>
	{
		private JList<? extends String> list;
		private int index;
		private boolean isSelected;

		TestLabelRoundedListCellRenderer() {
			setBorder( new FlatEmptyBorder( 1, 6, 1, 6 ) );
		}

		@Override
		public Component getListCellRendererComponent( JList<? extends String> list,
			String value, int index, boolean isSelected, boolean cellHasFocus )
		{
			this.list = list;
			this.index = index;
			this.isSelected = isSelected;

			setText( String.valueOf( value ) );
			setBackground( isSelected ? Color.green : list.getBackground() );
			setForeground( isSelected ? Color.blue : list.getForeground() );
			return this;
		}

		@Override
		protected void paintComponent( Graphics g ) {
			if( isSelected ) {
				g.setColor( getBackground() );
				FlatListUI.paintCellSelection( list, g, index, 0, 0, getWidth(), getHeight() );
			}

			super.paintComponent( g );
		}
	}

	//---- class TestDefaultTreeCellRenderer ----------------------------------

	private static class TestDefaultTreeCellRenderer
		extends DefaultTreeCellRenderer
	{
		public TestDefaultTreeCellRenderer() {
			setBackgroundSelectionColor( Color.green );
			setTextSelectionColor( Color.blue );
		}

		@Override
		public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus )
		{
			Color nonSelectionBg = null;
			Color nonSelectionFg = null;
			switch( String.valueOf( value ) ) {
				case "blue":		nonSelectionFg = Color.blue; break;
				case "red":		nonSelectionFg = Color.red; break;
				case "yellow":	nonSelectionBg = Color.yellow; break;
				case "violet":	nonSelectionBg = Color.magenta; break;
			}
			setBackgroundNonSelectionColor( nonSelectionBg );
			setTextNonSelectionColor( (nonSelectionFg != null) ? nonSelectionFg : UIManager.getColor( "Tree.textForeground" ) );

			return super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
		}
	}

	//---- class TestDefaultWithIconsTreeCellRenderer -------------------------

	private static class TestDefaultWithIconsTreeCellRenderer
		extends TestDefaultTreeCellRenderer
	{
		public TestDefaultWithIconsTreeCellRenderer() {
			setLeafIcon( UIManager.getIcon( "FileView.floppyDriveIcon" ) );
			setClosedIcon( UIManager.getIcon( "FileView.hardDriveIcon" ) );
			setOpenIcon( UIManager.getIcon( "FileView.computerIcon" ) );
		}
	}

	//---- class TestDefaultWithIconTreeCellRenderer --------------------------

	private static class TestDefaultWithIconTreeCellRenderer
		extends TestDefaultTreeCellRenderer
	{
		@Override
		public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus )
		{
			super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );

			// set icon for enabled state, but not for disabled state,
			// which allows testing whether tree node layout is updated correctly
			// when enabled state changes
			setIcon( UIManager.getIcon( "FileView.floppyDriveIcon" ) );

			return this;
		}
	}

	//---- class TestLabelTreeCellRenderer ------------------------------------

	private static class TestLabelTreeCellRenderer
		extends JLabel
		implements TreeCellRenderer
	{
		@Override
		public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus )
		{
			setText( String.valueOf( value ) );
			return this;
		}
	}

	//---- class TestLabelTreeCellRenderer ------------------------------------

	private static class TestWideTreeCellRenderer
		extends JPanel
		implements TreeCellRenderer
	{
		private final JLabel label = new JLabel();
		private final JLabel icon = new JLabel( UIManager.getIcon( "FileView.floppyDriveIcon" ) );

		TestWideTreeCellRenderer() {
			super( new BorderLayout() );
			setOpaque( false );
			add( label, BorderLayout.CENTER );
			add( icon, BorderLayout.LINE_END );
			setBorder( new LineBorder( Color.red ) );
		}

		@Override
		public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus )
		{
			label.setText( String.valueOf( value ) );
			return this;
		}
	}

	//---- class TestComboBoxTableCellRenderer --------------------------------

	private static class TestComboBoxTableCellRenderer
		extends JComboBox<String>
		implements TableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column )
		{
			setModel( new DefaultComboBoxModel<>( new String[] { String.valueOf( value ) } ) );

			setBackground( isSelected ? table.getSelectionBackground() : table.getBackground() );
			setForeground( isSelected ? table.getSelectionForeground() : table.getForeground() );
			setBorder( null );
			return this;
		}
	}

	//---- class TestLabelRoundedTableCellRenderer ----------------------------

	@SuppressWarnings( "unused" )
	private static class TestLabelRoundedTableCellRenderer
		extends JLabel
		implements TableCellRenderer
	{
		private JTable table;
		private int row;
		private int column;
		private boolean isSelected;

		TestLabelRoundedTableCellRenderer() {
			setBorder( new FlatEmptyBorder( 1, 6, 1, 6 ) );
		}

		@Override
		public Component getTableCellRendererComponent( JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column )
		{
			this.table = table;
			this.row = row;
			this.column = column;
			this.isSelected = isSelected;

			setText( String.valueOf( value ) );
			setBackground( isSelected ? Color.green : table.getBackground() );
			setForeground( isSelected ? Color.blue : table.getForeground() );
			return this;
		}

		@Override
		protected void paintComponent( Graphics g ) {
			if( isSelected ) {
				g.setColor( getBackground() );
				FlatTableUI.paintCellSelection( table, g, row, column, 0, 0, getWidth(), getHeight() );
			}

			super.paintComponent( g );
		}
	}
}
