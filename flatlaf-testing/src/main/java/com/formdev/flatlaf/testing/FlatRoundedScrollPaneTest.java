/*
 * Copyright 2023 FormDev Software GmbH
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatScrollPaneBorder;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatRoundedScrollPaneTest
	extends FlatTestPanel
{
	private JScrollPane[] allJScrollPanes;

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatRoundedScrollPaneTest" );
			frame.useApplyComponentOrientation = true;
			frame.showFrame( FlatRoundedScrollPaneTest::new );
		} );
	}

	FlatRoundedScrollPaneTest() {
		initComponents();

		allJScrollPanes = new JScrollPane[] {
			listScrollPane, treeScrollPane, tableScrollPane,
			textAreaScrollPane, textPaneScrollPane, editorPaneScrollPane, customScrollPane
		};

		ArrayList<String> items = new ArrayList<>();
		for( char ch = '0'; ch < 'z'; ch++ ) {
			if( (ch > '9' && ch < 'A') || (ch > 'Z' && ch < 'a') )
				continue;

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

		// select some rows to better see smooth scrolling issues
		for( int i = 5; i < items.size(); i += 10 ) {
			list.addSelectionInterval( i, i );
			tree.addSelectionInterval( i, i );
			table.addRowSelectionInterval( i, i );
		}

		// text components
		String longText = items.stream().collect( Collectors.joining( " " ) ) + ' '
			+ items.stream().limit( 20 ).collect( Collectors.joining( " " ) );
		String text = items.stream().collect( Collectors.joining( "\n" ) ) + '\n';
		textArea.setText( longText + '\n' + text );
		textPane.setText( text );
		editorPane.setText( text );

		textArea.select( 0, 0 );
		textPane.select( 0, 0 );
		editorPane.select( 0, 0 );

		arcSliderChanged();

		EventQueue.invokeLater( () -> {
			EventQueue.invokeLater( () -> {
				list.requestFocusInWindow();
			} );
		} );
	}

	private void autoResizeModeChanged() {
		table.setAutoResizeMode( autoResizeModeCheckBox.isSelected() ? JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS : JTable.AUTO_RESIZE_OFF );
	}

	private void cornersChanged() {
		boolean sel = cornersCheckBox.isSelected();
		for( JScrollPane scrollPane : allJScrollPanes ) {
			scrollPane.setCorner( JScrollPane.UPPER_LEADING_CORNER, sel ? new Corner( Color.magenta ) : null );
			scrollPane.setCorner( JScrollPane.UPPER_TRAILING_CORNER, sel ? new Corner( Color.red ) : null );
			scrollPane.setCorner( JScrollPane.LOWER_LEADING_CORNER, sel ? new Corner( Color.cyan ) : null );
			scrollPane.setCorner( JScrollPane.LOWER_TRAILING_CORNER, sel ? new Corner( Color.pink ) : null );
		}
	}

	private void columnHeaderChanged() {
		boolean sel = columnHeaderCheckBox.isSelected();
		for( JScrollPane scrollPane : allJScrollPanes ) {
			if( scrollPane == tableScrollPane )
				continue;

			JViewport header = null;
			if( sel ) {
				header = new JViewport();
				Corner view = new Corner( Color.cyan );
				view.setPreferredSize( new Dimension( 0, UIScale.scale( 20 ) ) );
				header.setView( view );
			}
			scrollPane.setColumnHeader( header );
		}
	}

	private void rowHeaderChanged() {
		boolean sel = rowHeaderCheckBox.isSelected();
		for( JScrollPane scrollPane : allJScrollPanes ) {
			JViewport header = null;
			if( sel ) {
				header = new JViewport();
				Corner view = new Corner( Color.yellow );
				view.setPreferredSize( new Dimension( UIScale.scale( 20 ), 0 ) );
				header.setView( view );
			}
			scrollPane.setRowHeader( header );
		}
	}

	private void horizontalScrollBarChanged() {
		int policy = horizontalScrollBarCheckBox.isSelected()
			? JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
			: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;

		for( JScrollPane scrollPane : allJScrollPanes )
			scrollPane.setHorizontalScrollBarPolicy( policy );
	}

	private void verticalScrollBarChanged() {
		int policy = verticalScrollBarCheckBox.isSelected()
			? JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
			: JScrollPane.VERTICAL_SCROLLBAR_NEVER;

		for( JScrollPane scrollPane : allJScrollPanes )
			scrollPane.setVerticalScrollBarPolicy( policy );
	}

	private void arcSliderChanged() {
		int arc = arcSlider.getValue();
		for( JScrollPane scrollPane : allJScrollPanes )
			scrollPane.putClientProperty( FlatClientProperties.STYLE, "arc: " + arc );

		Border border = allJScrollPanes[0].getBorder();
		paddingField.setText( border instanceof FlatScrollPaneBorder
			? Integer.toString( ((FlatScrollPaneBorder)border).getLeftRightPadding( allJScrollPanes[0] ) )
			: "?" );
	}

	private void viewportBorderChanged() {
		Border viewportBorder = viewportBorderCheckBox.isSelected()
			? new CompoundBorder(
				new MatteBorder( 1, 1, 0, 0, Color.red ),
				new MatteBorder( 0, 0, 1, 1, Color.blue ) )
			: null;
		for( JScrollPane scrollPane : allJScrollPanes ) {
			scrollPane.setViewportBorder( viewportBorder );
			scrollPane.revalidate();
			scrollPane.repaint();
		}
	}

	private void emptyViewportChanged() {
		boolean empty = emptyViewportCheckBox.isSelected();
		for( JScrollPane scrollPane : allJScrollPanes ) {
			JViewport viewport = scrollPane.getViewport();
			Component view = viewport.getView();
			if( empty ) {
				scrollPane.putClientProperty( getClass().getName(), view );
				JComponent emptyView = new JComponent() {
				};
				emptyView.setBorder( new EmptyViewBorder() );
				emptyView.setFocusable( true );
				emptyView.addMouseListener( new MouseAdapter() {
					@Override
					public void mousePressed( MouseEvent e ) {
						emptyView.requestFocusInWindow();
					}
				} );
				viewport.setView( emptyView );
			} else {
				Object oldView = scrollPane.getClientProperty( getClass().getName() );
				scrollPane.putClientProperty( getClass().getName(), null );
				if( oldView instanceof Component )
					viewport.setView( (Component) oldView );
				else
					viewport.setView( null );
			}
			viewport.setOpaque( !empty );
			scrollPane.revalidate();
			scrollPane.repaint();
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		splitPane2 = new JSplitPane();
		panel1 = new FlatTestPanel();
		listLabel = new JLabel();
		paddingLabel = new JLabel();
		paddingField = new JLabel();
		treeLabel = new JLabel();
		tableLabel = new JLabel();
		autoResizeModeCheckBox = new JCheckBox();
		listScrollPane = new JScrollPane();
		list = new JList<>();
		treeScrollPane = new JScrollPane();
		tree = new JTree();
		tableScrollPane = new JScrollPane();
		table = new JTable();
		panel2 = new FlatTestPanel();
		textAreaLabel = new JLabel();
		textPaneLabel = new JLabel();
		editorPaneLabel = new JLabel();
		customLabel = new JLabel();
		textAreaScrollPane = new JScrollPane();
		textArea = new JTextArea();
		textPaneScrollPane = new JScrollPane();
		textPane = new JTextPane();
		editorPaneScrollPane = new JScrollPane();
		editorPane = new JEditorPane();
		customScrollPane = new JScrollPane();
		button1 = new JButton();
		panel3 = new JPanel();
		JLabel arcLabel = new JLabel();
		arcSlider = new JSlider();
		cornersCheckBox = new JCheckBox();
		columnHeaderCheckBox = new JCheckBox();
		horizontalScrollBarCheckBox = new JCheckBox();
		viewportBorderCheckBox = new JCheckBox();
		rowHeaderCheckBox = new JCheckBox();
		verticalScrollBarCheckBox = new JCheckBox();
		emptyViewportCheckBox = new JCheckBox();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[200,grow,fill]",
			// rows
			"[grow,fill]" +
			"[]"));

		//======== splitPane2 ========
		{
			splitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane2.setResizeWeight(0.5);

			//======== panel1 ========
			{
				panel1.setLayout(new MigLayout(
					"ltr,insets 3,hidemode 3",
					// columns
					"[200,grow,fill]" +
					"[200,grow,fill]" +
					"[200,grow,fill]" +
					"[200,grow,fill]",
					// rows
					"[]0" +
					"[200,grow,fill]"));

				//---- listLabel ----
				listLabel.setText("JList:");
				listLabel.setHorizontalTextPosition(SwingConstants.LEADING);
				panel1.add(listLabel, "cell 0 0,aligny top,growy 0");

				//---- paddingLabel ----
				paddingLabel.setText("Padding:");
				panel1.add(paddingLabel, "cell 0 0,alignx trailing,growx 0");

				//---- paddingField ----
				paddingField.setText("0");
				panel1.add(paddingField, "cell 0 0,alignx trailing,growx 0");

				//---- treeLabel ----
				treeLabel.setText("JTree:");
				treeLabel.setHorizontalTextPosition(SwingConstants.LEADING);
				panel1.add(treeLabel, "cell 1 0");

				//---- tableLabel ----
				tableLabel.setText("JTable:");
				tableLabel.setHorizontalTextPosition(SwingConstants.LEADING);
				panel1.add(tableLabel, "cell 2 0 2 1");

				//---- autoResizeModeCheckBox ----
				autoResizeModeCheckBox.setText("Auto-resize mode");
				autoResizeModeCheckBox.setSelected(true);
				autoResizeModeCheckBox.addActionListener(e -> autoResizeModeChanged());
				panel1.add(autoResizeModeCheckBox, "cell 2 0 2 1,alignx right,growx 0");

				//======== listScrollPane ========
				{
					listScrollPane.setViewportView(list);
				}
				panel1.add(listScrollPane, "cell 0 1,growx");

				//======== treeScrollPane ========
				{
					treeScrollPane.setViewportView(tree);
				}
				panel1.add(treeScrollPane, "cell 1 1");

				//======== tableScrollPane ========
				{
					tableScrollPane.setViewportView(table);
				}
				panel1.add(tableScrollPane, "cell 2 1 2 1,width 100,height 100");
			}
			splitPane2.setTopComponent(panel1);

			//======== panel2 ========
			{
				panel2.setLayout(new MigLayout(
					"ltr,insets 3,hidemode 3",
					// columns
					"[200,grow,fill]" +
					"[200,grow,fill]" +
					"[200,grow,fill]" +
					"[200,grow,fill]",
					// rows
					"[]0" +
					"[200,grow,fill]"));

				//---- textAreaLabel ----
				textAreaLabel.setText("JTextArea:");
				textAreaLabel.setHorizontalTextPosition(SwingConstants.LEADING);
				panel2.add(textAreaLabel, "cell 0 0");

				//---- textPaneLabel ----
				textPaneLabel.setText("JTextPane:");
				textPaneLabel.setHorizontalTextPosition(SwingConstants.LEADING);
				panel2.add(textPaneLabel, "cell 1 0");

				//---- editorPaneLabel ----
				editorPaneLabel.setText("JEditorPane:");
				editorPaneLabel.setHorizontalTextPosition(SwingConstants.LEADING);
				panel2.add(editorPaneLabel, "cell 2 0");

				//---- customLabel ----
				customLabel.setText("Custom:");
				panel2.add(customLabel, "cell 3 0");

				//======== textAreaScrollPane ========
				{
					textAreaScrollPane.setViewportView(textArea);
				}
				panel2.add(textAreaScrollPane, "cell 0 1");

				//======== textPaneScrollPane ========
				{
					textPaneScrollPane.setViewportView(textPane);
				}
				panel2.add(textPaneScrollPane, "cell 1 1");

				//======== editorPaneScrollPane ========
				{
					editorPaneScrollPane.setViewportView(editorPane);
				}
				panel2.add(editorPaneScrollPane, "cell 2 1");

				//======== customScrollPane ========
				{

					//---- button1 ----
					button1.setText("I'm a large button, but do not implement Scrollable interface");
					button1.setPreferredSize(new Dimension(800, 800));
					button1.setHorizontalAlignment(SwingConstants.LEADING);
					button1.setVerticalAlignment(SwingConstants.TOP);
					customScrollPane.setViewportView(button1);
				}
				panel2.add(customScrollPane, "cell 3 1");
			}
			splitPane2.setBottomComponent(panel2);
		}
		add(splitPane2, "cell 0 0");

		//======== panel3 ========
		{
			panel3.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[grow,fill]para" +
				"[]" +
				"[]" +
				"[]",
				// rows
				"[]" +
				"[]" +
				"[]"));

			//---- arcLabel ----
			arcLabel.setText("Arc:");
			panel3.add(arcLabel, "cell 0 0");

			//---- arcSlider ----
			arcSlider.setMaximum(40);
			arcSlider.setValue(20);
			arcSlider.setSnapToTicks(true);
			arcSlider.setMajorTickSpacing(10);
			arcSlider.setMinorTickSpacing(1);
			arcSlider.setPaintTicks(true);
			arcSlider.setPaintLabels(true);
			arcSlider.addChangeListener(e -> arcSliderChanged());
			panel3.add(arcSlider, "cell 1 0 1 2");

			//---- cornersCheckBox ----
			cornersCheckBox.setText("Corners");
			cornersCheckBox.addActionListener(e -> cornersChanged());
			panel3.add(cornersCheckBox, "cell 2 0");

			//---- columnHeaderCheckBox ----
			columnHeaderCheckBox.setText("Column Header");
			columnHeaderCheckBox.addActionListener(e -> columnHeaderChanged());
			panel3.add(columnHeaderCheckBox, "cell 3 0");

			//---- horizontalScrollBarCheckBox ----
			horizontalScrollBarCheckBox.setText("Horizontal ScrollBar");
			horizontalScrollBarCheckBox.setSelected(true);
			horizontalScrollBarCheckBox.addActionListener(e -> horizontalScrollBarChanged());
			panel3.add(horizontalScrollBarCheckBox, "cell 4 0");

			//---- viewportBorderCheckBox ----
			viewportBorderCheckBox.setText("Viewport border");
			viewportBorderCheckBox.addActionListener(e -> viewportBorderChanged());
			panel3.add(viewportBorderCheckBox, "cell 2 1");

			//---- rowHeaderCheckBox ----
			rowHeaderCheckBox.setText("Row Header");
			rowHeaderCheckBox.addActionListener(e -> rowHeaderChanged());
			panel3.add(rowHeaderCheckBox, "cell 3 1");

			//---- verticalScrollBarCheckBox ----
			verticalScrollBarCheckBox.setText("Vertical ScrollBar");
			verticalScrollBarCheckBox.setSelected(true);
			verticalScrollBarCheckBox.addActionListener(e -> verticalScrollBarChanged());
			panel3.add(verticalScrollBarCheckBox, "cell 4 1");

			//---- emptyViewportCheckBox ----
			emptyViewportCheckBox.setText("Empty viewport");
			emptyViewportCheckBox.addActionListener(e -> emptyViewportChanged());
			panel3.add(emptyViewportCheckBox, "cell 2 2");
		}
		add(panel3, "cell 0 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JSplitPane splitPane2;
	private FlatTestPanel panel1;
	private JLabel listLabel;
	private JLabel paddingLabel;
	private JLabel paddingField;
	private JLabel treeLabel;
	private JLabel tableLabel;
	private JCheckBox autoResizeModeCheckBox;
	private JScrollPane listScrollPane;
	private JList<String> list;
	private JScrollPane treeScrollPane;
	private JTree tree;
	private JScrollPane tableScrollPane;
	private JTable table;
	private FlatTestPanel panel2;
	private JLabel textAreaLabel;
	private JLabel textPaneLabel;
	private JLabel editorPaneLabel;
	private JLabel customLabel;
	private JScrollPane textAreaScrollPane;
	private JTextArea textArea;
	private JScrollPane textPaneScrollPane;
	private JTextPane textPane;
	private JScrollPane editorPaneScrollPane;
	private JEditorPane editorPane;
	private JScrollPane customScrollPane;
	private JButton button1;
	private JPanel panel3;
	private JSlider arcSlider;
	private JCheckBox cornersCheckBox;
	private JCheckBox columnHeaderCheckBox;
	private JCheckBox horizontalScrollBarCheckBox;
	private JCheckBox viewportBorderCheckBox;
	private JCheckBox rowHeaderCheckBox;
	private JCheckBox verticalScrollBarCheckBox;
	private JCheckBox emptyViewportCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class Corner -------------------------------------------------------

	private static class Corner
		extends JPanel
	{
		Corner( Color color ) {
			super.setBackground( color );
		}

		@Override
		public void setBackground( Color bg ) {
			// do not change background when checkbox "explicit colors" is selected
		}
	}

	//---- class EmptyViewBorder ----------------------------------------------

	private static class EmptyViewBorder
		extends EmptyBorder
	{
		public EmptyViewBorder() {
			super( 0, 0, 0, 0 );
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			g.setColor( Color.red );
			int x2 = x + width - 1;
			int y2 = y + height - 1;
			for( int px = x; px <= x2; px += 4 ) {
				g.fillRect( px, y, 1, 1 );
				g.fillRect( px, y2, 1, 1 );
			}
			for( int py = y; py <= y2; py += 4 ) {
				g.fillRect( x, py, 1, 1 );
				g.fillRect( x2, py, 1, 1 );
			}
		}
	}
}
