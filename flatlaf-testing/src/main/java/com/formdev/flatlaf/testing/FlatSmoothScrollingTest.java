/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.testing;

import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatSmoothScrollingTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatSmoothScrollingTest" );
			UIManager.put( "ScrollBar.showButtons", true );
			frame.showFrame( FlatSmoothScrollingTest::new );
		} );
	}

	FlatSmoothScrollingTest() {
		initComponents();

		listScrollPane.getVerticalScrollBar().addAdjustmentListener( new AdjustmentHandler( "list vert" ) );
		listScrollPane.getHorizontalScrollBar().addAdjustmentListener( new AdjustmentHandler( "list horz" ) );

		treeScrollPane.getVerticalScrollBar().addAdjustmentListener( new AdjustmentHandler( "tree vert" ) );
		treeScrollPane.getHorizontalScrollBar().addAdjustmentListener( new AdjustmentHandler( "tree horz" ) );

		tableScrollPane.getVerticalScrollBar().addAdjustmentListener( new AdjustmentHandler( "table vert" ) );
		tableScrollPane.getHorizontalScrollBar().addAdjustmentListener( new AdjustmentHandler( "table horz" ) );

		textAreaScrollPane.getVerticalScrollBar().addAdjustmentListener( new AdjustmentHandler( "textArea vert" ) );
		textAreaScrollPane.getHorizontalScrollBar().addAdjustmentListener( new AdjustmentHandler( "textArea horz" ) );

		textPaneScrollPane.getVerticalScrollBar().addAdjustmentListener( new AdjustmentHandler( "textPane vert" ) );
		textPaneScrollPane.getHorizontalScrollBar().addAdjustmentListener( new AdjustmentHandler( "textPane horz" ) );

		editorPaneScrollPane.getVerticalScrollBar().addAdjustmentListener( new AdjustmentHandler( "editorPane vert" ) );
		editorPaneScrollPane.getHorizontalScrollBar().addAdjustmentListener( new AdjustmentHandler( "editorPane horz" ) );

		ArrayList<String> items = new ArrayList<>();
		for( char ch = '0'; ch < 'z'; ch++ ) {
			char[] chars = new char[ch - '0' + 1];
			Arrays.fill( chars, ch );
			items.add( new String( chars ) );
		}

		// list model
		list.setModel( new AbstractListModel<String>() {
			@Override
			public int getSize() {
				return items.size();
			}
			@Override
			public String getElementAt( int index ) {
				return items.get( index );
			}
		} );

		// tree model
		DefaultMutableTreeNode root = new DefaultMutableTreeNode( items.get( 0 ) );
		DefaultMutableTreeNode last = null;
		for( int i = 1; i < items.size(); i++ ) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode( items.get( i ) );
			if( i % 5 == 1 ) {
				root.add( node );
				last = node;
			} else
				last.add( node );
		}
		tree.setModel( new DefaultTreeModel( root ) );
		for( int row = tree.getRowCount() - 1; row >= 0; row-- )
			tree.expandRow( row );

		// table model
		table.setModel( new AbstractTableModel() {
			@Override
			public int getRowCount() {
				return items.size();
			}
			@Override
			public int getColumnCount() {
				return 4;
			}
			@Override
			public Object getValueAt( int rowIndex, int columnIndex ) {
				if( columnIndex > 0 )
					rowIndex = (items.size() + rowIndex - ((items.size() / 4) * columnIndex)) % items.size();
				return items.get( rowIndex );
			}
		} );

		// text components
		String text = items.stream().collect( Collectors.joining( "\n" ) );
		textArea.setText( text );
		textPane.setText( text );
		editorPane.setText( text );
	}

	private void smoothScrollingChanged() {
		UIManager.put( "ScrollPane.smoothScrolling", smoothScrollingCheckBox.isSelected() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		smoothScrollingCheckBox = new JCheckBox();
		listLabel = new JLabel();
		label1 = new JLabel();
		label5 = new JLabel();
		listScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		list = new JList<>();
		treeScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		tree = new JTree();
		tableScrollPane = new JScrollPane();
		table = new JTable();
		label2 = new JLabel();
		label3 = new JLabel();
		label4 = new JLabel();
		textAreaScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		textArea = new JTextArea();
		textPaneScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		textPane = new JTextPane();
		editorPaneScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		editorPane = new JEditorPane();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[200,fill]" +
			"[200,fill]" +
			"[200,fill]" +
			"[200,fill]",
			// rows
			"[]" +
			"[]" +
			"[200,grow,fill]" +
			"[]" +
			"[200,grow,fill]"));

		//---- smoothScrollingCheckBox ----
		smoothScrollingCheckBox.setText("Smooth scrolling");
		smoothScrollingCheckBox.setSelected(true);
		smoothScrollingCheckBox.addActionListener(e -> smoothScrollingChanged());
		add(smoothScrollingCheckBox, "cell 0 0,alignx left,growx 0");

		//---- listLabel ----
		listLabel.setText("JList:");
		add(listLabel, "cell 0 1,aligny top,growy 0");

		//---- label1 ----
		label1.setText("JTree:");
		add(label1, "cell 1 1");

		//---- label5 ----
		label5.setText("JTable:");
		add(label5, "cell 2 1");

		//======== listScrollPane ========
		{
			listScrollPane.setViewportView(list);
		}
		add(listScrollPane, "cell 0 2,growx");

		//======== treeScrollPane ========
		{

			//---- tree ----
			tree.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("root") {
					{
						add(new DefaultMutableTreeNode("a"));
						add(new DefaultMutableTreeNode("b"));
						add(new DefaultMutableTreeNode("c"));
					}
				}));
			treeScrollPane.setViewportView(tree);
		}
		add(treeScrollPane, "cell 1 2");

		//======== tableScrollPane ========
		{
			tableScrollPane.setViewportView(table);
		}
		add(tableScrollPane, "cell 2 2 2 1,width 100,height 100");

		//---- label2 ----
		label2.setText("JTextArea:");
		add(label2, "cell 0 3");

		//---- label3 ----
		label3.setText("JTextPane:");
		add(label3, "cell 1 3");

		//---- label4 ----
		label4.setText("JEditorPane:");
		add(label4, "cell 2 3");

		//======== textAreaScrollPane ========
		{
			textAreaScrollPane.setViewportView(textArea);
		}
		add(textAreaScrollPane, "cell 0 4");

		//======== textPaneScrollPane ========
		{
			textPaneScrollPane.setViewportView(textPane);
		}
		add(textPaneScrollPane, "cell 1 4");

		//======== editorPaneScrollPane ========
		{
			editorPaneScrollPane.setViewportView(editorPane);
		}
		add(editorPaneScrollPane, "cell 2 4");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox smoothScrollingCheckBox;
	private JLabel listLabel;
	private JLabel label1;
	private JLabel label5;
	private FlatSmoothScrollingTest.DebugScrollPane listScrollPane;
	private JList<String> list;
	private FlatSmoothScrollingTest.DebugScrollPane treeScrollPane;
	private JTree tree;
	private JScrollPane tableScrollPane;
	private JTable table;
	private JLabel label2;
	private JLabel label3;
	private JLabel label4;
	private FlatSmoothScrollingTest.DebugScrollPane textAreaScrollPane;
	private JTextArea textArea;
	private FlatSmoothScrollingTest.DebugScrollPane textPaneScrollPane;
	private JTextPane textPane;
	private FlatSmoothScrollingTest.DebugScrollPane editorPaneScrollPane;
	private JEditorPane editorPane;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class AdjustmentHandler --------------------------------------------

	private static class AdjustmentHandler
		implements AdjustmentListener
	{
		private final String name;
		private int count;

		AdjustmentHandler( String name ) {
			this.name = name;
		}

		@Override
		public void adjustmentValueChanged( AdjustmentEvent e ) {
			System.out.printf( "%s (%d):  %s  %3d  %b%n",
				name, ++count,
				adjustmentType2Str( e.getAdjustmentType() ),
				e.getValue(),
				e.getValueIsAdjusting() );
		}

		private String adjustmentType2Str( int adjustmentType ) {
			switch( adjustmentType ) {
				case AdjustmentEvent.UNIT_INCREMENT:  return "UNIT_INCREMENT";
				case AdjustmentEvent.UNIT_DECREMENT:  return "UNIT_DECREMENT";
				case AdjustmentEvent.BLOCK_INCREMENT: return "BLOCK_INCREMENT";
				case AdjustmentEvent.BLOCK_DECREMENT: return "BLOCK_DECREMENT";
				case AdjustmentEvent.TRACK:           return "TRACK";
				default:                              return "unknown type";
			}
		}
	}

	//---- class DebugViewport ------------------------------------------------

	private static class DebugScrollPane
		extends JScrollPane
	{
		@Override
		protected JViewport createViewport() {
			return new JViewport() {
				@Override
				public Point getViewPosition() {
					Point viewPosition = super.getViewPosition();
					System.out.println( "    viewPosition  " + viewPosition.x + "," + viewPosition.y );
					return viewPosition;
				}
			};
		}
	}
}
