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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.*;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.UIScale;
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

		ToolTipManager.sharedInstance().setInitialDelay( 0 );
		ToolTipManager.sharedInstance().setDismissDelay( Integer.MAX_VALUE );

		// allow enabling/disabling smooth scrolling with Alt+S without moving focus to checkbox
		registerKeyboardAction(
			e -> {
				smoothScrollingCheckBox.setSelected( !smoothScrollingCheckBox.isSelected() );
				smoothScrollingChanged();
			},
			KeyStroke.getKeyStroke( "alt " + (char) smoothScrollingCheckBox.getMnemonic() ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		listLabel.setIcon( new ColorIcon( Color.red.darker() ) );
		treeLabel.setIcon( new ColorIcon( Color.blue.darker() ) );
		tableLabel.setIcon( new ColorIcon( Color.green.darker() ) );
		textAreaLabel.setIcon( new ColorIcon( Color.magenta.darker() ) );
		textPaneLabel.setIcon( new ColorIcon( Color.cyan.darker() ) );
		editorPaneLabel.setIcon( new ColorIcon( Color.orange.darker() ) );
		customLabel.setIcon( new ColorIcon( Color.pink ) );

		listScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( listScrollPane, true, "list vert", Color.red.darker() ) );
		listScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( listScrollPane, false, "list horz", Color.red ) );

		treeScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( treeScrollPane, true, "tree vert", Color.blue.darker() ) );
		treeScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( treeScrollPane, false, "tree horz", Color.blue ) );

		tableScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( tableScrollPane, true, "table vert", Color.green.darker() ) );
		tableScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( tableScrollPane, false, "table horz", Color.green ) );

		textAreaScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( textAreaScrollPane, true, "textArea vert", Color.magenta.darker() ) );
		textAreaScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( textAreaScrollPane, false, "textArea horz", Color.magenta ) );

		textPaneScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( textPaneScrollPane, true, "textPane vert", Color.cyan.darker() ) );
		textPaneScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( textPaneScrollPane, false, "textPane horz", Color.cyan ) );

		editorPaneScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( editorPaneScrollPane, true, "editorPane vert", Color.orange.darker() ) );
		editorPaneScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( editorPaneScrollPane, false, "editorPane horz", Color.orange ) );

		customScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( customScrollPane, true, "custom vert", Color.pink ) );
		customScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( customScrollPane, false, "custom horz", Color.pink.darker() ) );

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

		EventQueue.invokeLater( () -> {
			EventQueue.invokeLater( () -> {
				list.requestFocusInWindow();
			} );
		} );
	}

	private void smoothScrollingChanged() {
		UIManager.put( "ScrollPane.smoothScrolling", smoothScrollingCheckBox.isSelected() );
	}

	private void showTableGridChanged() {
		boolean showGrid = showTableGridCheckBox.isSelected();
		table.setShowHorizontalLines( showGrid );
		table.setShowVerticalLines( showGrid );
		table.setIntercellSpacing( showGrid ? new Dimension( 1, 1 ) : new Dimension() );
		table.setGridColor( Color.gray );
	}

	private void autoResizeModeChanged() {
		table.setAutoResizeMode( autoResizeModeCheckBox.isSelected() ? JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS : JTable.AUTO_RESIZE_OFF );
	}

	@Override
	public void updateUI() {
		super.updateUI();

		EventQueue.invokeLater( () -> {
			showTableGridChanged();
		} );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		smoothScrollingCheckBox = new JCheckBox();
		splitPane1 = new JSplitPane();
		splitPane2 = new JSplitPane();
		panel1 = new JPanel();
		listLabel = new JLabel();
		treeLabel = new JLabel();
		tableLabel = new JLabel();
		showTableGridCheckBox = new JCheckBox();
		autoResizeModeCheckBox = new JCheckBox();
		listScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		list = new JList<>();
		treeScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		tree = new JTree();
		tableScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		table = new JTable();
		panel2 = new JPanel();
		textAreaLabel = new JLabel();
		textPaneLabel = new JLabel();
		editorPaneLabel = new JLabel();
		customLabel = new JLabel();
		textAreaScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		textArea = new JTextArea();
		textPaneScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		textPane = new JTextPane();
		editorPaneScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		editorPane = new JEditorPane();
		customScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		button1 = new JButton();
		lineChartPanel = new LineChartPanel();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[200,grow,fill]",
			// rows
			"[]" +
			"[grow,fill]"));

		//---- smoothScrollingCheckBox ----
		smoothScrollingCheckBox.setText("Smooth scrolling");
		smoothScrollingCheckBox.setSelected(true);
		smoothScrollingCheckBox.setMnemonic('S');
		smoothScrollingCheckBox.addActionListener(e -> smoothScrollingChanged());
		add(smoothScrollingCheckBox, "cell 0 0,alignx left,growx 0");

		//======== splitPane1 ========
		{
			splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane1.setResizeWeight(1.0);

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

					//---- treeLabel ----
					treeLabel.setText("JTree:");
					treeLabel.setHorizontalTextPosition(SwingConstants.LEADING);
					panel1.add(treeLabel, "cell 1 0");

					//---- tableLabel ----
					tableLabel.setText("JTable:");
					tableLabel.setHorizontalTextPosition(SwingConstants.LEADING);
					panel1.add(tableLabel, "cell 2 0 2 1");

					//---- showTableGridCheckBox ----
					showTableGridCheckBox.setText("Show table grid");
					showTableGridCheckBox.setMnemonic('G');
					showTableGridCheckBox.addActionListener(e -> showTableGridChanged());
					panel1.add(showTableGridCheckBox, "cell 2 0 2 1,alignx right,growx 0");

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
			splitPane1.setTopComponent(splitPane2);

			//---- lineChartPanel ----
			lineChartPanel.setLegend1Text("Rectangles: scrollbar values (mouse hover shows stack)");
			lineChartPanel.setLegend2Text("Dots: disabled blitting mode in JViewport");
			lineChartPanel.setLegendYValueText("scroll bar value");
			splitPane1.setBottomComponent(lineChartPanel);
		}
		add(splitPane1, "cell 0 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox smoothScrollingCheckBox;
	private JSplitPane splitPane1;
	private JSplitPane splitPane2;
	private JPanel panel1;
	private JLabel listLabel;
	private JLabel treeLabel;
	private JLabel tableLabel;
	private JCheckBox showTableGridCheckBox;
	private JCheckBox autoResizeModeCheckBox;
	private FlatSmoothScrollingTest.DebugScrollPane listScrollPane;
	private JList<String> list;
	private FlatSmoothScrollingTest.DebugScrollPane treeScrollPane;
	private JTree tree;
	private FlatSmoothScrollingTest.DebugScrollPane tableScrollPane;
	private JTable table;
	private JPanel panel2;
	private JLabel textAreaLabel;
	private JLabel textPaneLabel;
	private JLabel editorPaneLabel;
	private JLabel customLabel;
	private FlatSmoothScrollingTest.DebugScrollPane textAreaScrollPane;
	private JTextArea textArea;
	private FlatSmoothScrollingTest.DebugScrollPane textPaneScrollPane;
	private JTextPane textPane;
	private FlatSmoothScrollingTest.DebugScrollPane editorPaneScrollPane;
	private JEditorPane editorPane;
	private FlatSmoothScrollingTest.DebugScrollPane customScrollPane;
	private JButton button1;
	private LineChartPanel lineChartPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class ScrollBarChangeHandler ---------------------------------------

	private class ScrollBarChangeHandler
		implements ChangeListener
	{
		private final String name;
		private final Color chartColor;		// for smooth scrolling
		private final Color chartColor2;	// for non-smooth scrolling
		private int count;
		private long lastTime;

		ScrollBarChangeHandler( DebugScrollPane scrollPane, boolean vertical, String name, Color chartColor ) {
			this.name = name;
			this.chartColor = chartColor;
			this.chartColor2 = ColorFunctions.lighten( chartColor, 0.1f );

			// add change listener to viewport that is invoked from JViewport.setViewPosition()
			scrollPane.getViewport().addChangeListener( e -> {
				// add dot to chart if blit scroll mode is disabled
				if( vertical == scrollPane.lastScrollingWasVertical &&
					scrollPane.getViewport().getScrollMode() != JViewport.BLIT_SCROLL_MODE )
				{
					// calculate value from view position because scrollbar value is not yet up-to-date
					JViewport viewport = scrollPane.getViewport();
					Point viewPosition = viewport.getViewPosition();
					Dimension viewSize = viewport.getViewSize();
					double value = vertical
						? ((double) viewPosition.y) / (viewSize.height - viewport.getHeight())
						: ((double) viewPosition.x) / (viewSize.width - viewport.getWidth());
					int ivalue = vertical ? viewPosition.y : viewPosition.x;

					lineChartPanel.addValue( value, ivalue, true, chartColor, name );
				}
			} );
		}

		@Override
		public void stateChanged( ChangeEvent e ) {
			DefaultBoundedRangeModel m = (DefaultBoundedRangeModel) e.getSource();
			boolean smoothScrolling = smoothScrollingCheckBox.isSelected();

			lineChartPanel.addValue( getChartValue( m ), m.getValue(), false,
				smoothScrolling ? chartColor : chartColor2, name );

			long t = System.nanoTime() / 1000000;

			System.out.printf( "%s (%d):  %4d  %3d ms   %b%n",
				name, ++count,
				m.getValue(),
				t - lastTime,
				m.getValueIsAdjusting() );

			lastTime = t;
		}

		private double getChartValue( BoundedRangeModel m ) {
			int value = m.getValue();
			return (double) (value - m.getMinimum()) / (double) (m.getMaximum() - m.getExtent());
		}
	}

	//---- class DebugViewport ------------------------------------------------

	private static class DebugScrollPane
		extends JScrollPane
	{
		boolean lastScrollingWasVertical;

		@Override
		protected JViewport createViewport() {
			return new JViewport() {
				@Override
				public Point getViewPosition() {
					Point viewPosition = super.getViewPosition();
//					System.out.println( "    viewPosition  " + viewPosition.x + "," + viewPosition.y );
					return viewPosition;
				}

				@Override
				public void setViewPosition( Point p ) {
					// remember whether scrolling vertically or horizontally
					Component view = getView();
					if( view != null ) {
						int oldY = (view instanceof JComponent)
							? ((JComponent) view).getY()
							: view.getBounds().y;

						int newY = -p.y;
						lastScrollingWasVertical = (oldY != newY);
					} else
						lastScrollingWasVertical = true;

					super.setViewPosition( p );
				}

				@Override
				public void paint( Graphics g ) {
					super.paint( g );

					if( backingStoreImage != null ) {
						System.out.println( "---------------------------------------------" );
						System.out.println( "WARNING: backingStoreImage was used for painting" );
						System.out.println( "View: " + getView() );
						System.out.println( "Clip: " + g.getClipBounds() );
						new Exception().printStackTrace( System.out );
						System.out.println( "---------------------------------------------" );
					}
				}
			};
		}
	}

	//---- class ColorIcon ----------------------------------------------------

	private static class ColorIcon
		implements Icon
	{
		private final Color color;

		ColorIcon( Color color ) {
			this.color = color;
		}

		@Override
		public void paintIcon( Component c, Graphics g, int x, int y ) {
			int width = getIconWidth();
			int height = getIconHeight();

			g.setColor( color );
			g.fillRect( x, y, width, height );
		}

		@Override
		public int getIconWidth() {
			return UIScale.scale( 24 );
		}

		@Override
		public int getIconHeight() {
			return UIScale.scale( 12 );
		}
	}
}
