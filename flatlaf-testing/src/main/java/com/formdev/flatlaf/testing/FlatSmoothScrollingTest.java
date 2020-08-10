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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.*;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HiDPIUtils;
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

		updateChartDelayedChanged();

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

		listScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "list vert", Color.red.darker() ) );
		listScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "list horz", Color.red ) );

		treeScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "tree vert", Color.blue.darker() ) );
		treeScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "tree horz", Color.blue ) );

		tableScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "table vert", Color.green.darker() ) );
		tableScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "table horz", Color.green ) );

		textAreaScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "textArea vert", Color.magenta.darker() ) );
		textAreaScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "textArea horz", Color.magenta ) );

		textPaneScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "textPane vert", Color.cyan.darker() ) );
		textPaneScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "textPane horz", Color.cyan ) );

		editorPaneScrollPane.getVerticalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "editorPane vert", Color.orange.darker() ) );
		editorPaneScrollPane.getHorizontalScrollBar().getModel().addChangeListener( new ScrollBarChangeHandler( "editorPane horz", Color.orange ) );

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

		textArea.select( 0, 0 );
		textPane.select( 0, 0 );
		editorPane.select( 0, 0 );
	}

	private void smoothScrollingChanged() {
		UIManager.put( "ScrollPane.smoothScrolling", smoothScrollingCheckBox.isSelected() );
	}

	private void clearChart() {
		lineChartPanel.clear();
	}

	private void updateChartDelayedChanged() {
		lineChartPanel.setUpdateDelayed( updateChartDelayedCheckBox.isSelected() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		smoothScrollingCheckBox = new JCheckBox();
		listLabel = new JLabel();
		treeLabel = new JLabel();
		tableLabel = new JLabel();
		listScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		list = new JList<>();
		treeScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		tree = new JTree();
		tableScrollPane = new JScrollPane();
		table = new JTable();
		textAreaLabel = new JLabel();
		textPaneLabel = new JLabel();
		editorPaneLabel = new JLabel();
		textAreaScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		textArea = new JTextArea();
		textPaneScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		textPane = new JTextPane();
		editorPaneScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		editorPane = new JEditorPane();
		panel1 = new JPanel();
		updateChartDelayedCheckBox = new JCheckBox();
		clearChartButton = new JButton();
		scrollPane1 = new JScrollPane();
		lineChartPanel = new FlatSmoothScrollingTest.LineChartPanel();

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
			"[200,grow,fill]" +
			"[300,grow,fill]"));

		//---- smoothScrollingCheckBox ----
		smoothScrollingCheckBox.setText("Smooth scrolling");
		smoothScrollingCheckBox.setSelected(true);
		smoothScrollingCheckBox.setMnemonic('S');
		smoothScrollingCheckBox.addActionListener(e -> smoothScrollingChanged());
		add(smoothScrollingCheckBox, "cell 0 0,alignx left,growx 0");

		//---- listLabel ----
		listLabel.setText("JList:");
		listLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		add(listLabel, "cell 0 1,aligny top,growy 0");

		//---- treeLabel ----
		treeLabel.setText("JTree:");
		treeLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		add(treeLabel, "cell 1 1");

		//---- tableLabel ----
		tableLabel.setText("JTable:");
		tableLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		add(tableLabel, "cell 2 1");

		//======== listScrollPane ========
		{
			listScrollPane.setViewportView(list);
		}
		add(listScrollPane, "cell 0 2,growx");

		//======== treeScrollPane ========
		{
			treeScrollPane.setViewportView(tree);
		}
		add(treeScrollPane, "cell 1 2");

		//======== tableScrollPane ========
		{
			tableScrollPane.setViewportView(table);
		}
		add(tableScrollPane, "cell 2 2 2 1,width 100,height 100");

		//---- textAreaLabel ----
		textAreaLabel.setText("JTextArea:");
		textAreaLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		add(textAreaLabel, "cell 0 3");

		//---- textPaneLabel ----
		textPaneLabel.setText("JTextPane:");
		textPaneLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		add(textPaneLabel, "cell 1 3");

		//---- editorPaneLabel ----
		editorPaneLabel.setText("JEditorPane:");
		editorPaneLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		add(editorPaneLabel, "cell 2 3");

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

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3,aligny bottom",
				// columns
				"[200,right]",
				// rows
				"[]" +
				"[]"));

			//---- updateChartDelayedCheckBox ----
			updateChartDelayedCheckBox.setText("Update chart delayed");
			updateChartDelayedCheckBox.setMnemonic('U');
			updateChartDelayedCheckBox.setSelected(true);
			updateChartDelayedCheckBox.addActionListener(e -> updateChartDelayedChanged());
			panel1.add(updateChartDelayedCheckBox, "cell 0 0");

			//---- clearChartButton ----
			clearChartButton.setText("Clear Chart");
			clearChartButton.addActionListener(e -> clearChart());
			panel1.add(clearChartButton, "cell 0 1");
		}
		add(panel1, "cell 3 4");

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(lineChartPanel);
		}
		add(scrollPane1, "cell 0 5 4 1,width 100");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox smoothScrollingCheckBox;
	private JLabel listLabel;
	private JLabel treeLabel;
	private JLabel tableLabel;
	private FlatSmoothScrollingTest.DebugScrollPane listScrollPane;
	private JList<String> list;
	private FlatSmoothScrollingTest.DebugScrollPane treeScrollPane;
	private JTree tree;
	private JScrollPane tableScrollPane;
	private JTable table;
	private JLabel textAreaLabel;
	private JLabel textPaneLabel;
	private JLabel editorPaneLabel;
	private FlatSmoothScrollingTest.DebugScrollPane textAreaScrollPane;
	private JTextArea textArea;
	private FlatSmoothScrollingTest.DebugScrollPane textPaneScrollPane;
	private JTextPane textPane;
	private FlatSmoothScrollingTest.DebugScrollPane editorPaneScrollPane;
	private JEditorPane editorPane;
	private JPanel panel1;
	private JCheckBox updateChartDelayedCheckBox;
	private JButton clearChartButton;
	private JScrollPane scrollPane1;
	private FlatSmoothScrollingTest.LineChartPanel lineChartPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class ScrollBarChangeHandler ---------------------------------------

	private class ScrollBarChangeHandler
		implements ChangeListener
	{
		private final String name;
		private final Color chartColor;
		private int count;
		private long lastTime = System.nanoTime() / 1000000;

		ScrollBarChangeHandler( String name, Color chartColor ) {
			this.name = name;
			this.chartColor = chartColor;
		}

		@Override
		public void stateChanged( ChangeEvent e ) {
			DefaultBoundedRangeModel m = (DefaultBoundedRangeModel) e.getSource();
			int value = m.getValue();
			boolean valueIsAdjusting = m.getValueIsAdjusting();

			if( chartColor != null ) {
				double chartValue = (double) (value - m.getMinimum()) / (double) (m.getMaximum() - m.getExtent());
				lineChartPanel.addValue( chartValue, chartColor );
			}

			long t = System.nanoTime() / 1000000;

			System.out.printf( "%s (%d):  %4d  %3d ms   %b%n",
				name, ++count,
				value,
				t - lastTime,
				valueIsAdjusting );

			lastTime = t;
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
//					System.out.println( "    viewPosition  " + viewPosition.x + "," + viewPosition.y );
					return viewPosition;
				}
			};
		}
	}

	//---- class LineChartPanel -----------------------------------------------

	private static class LineChartPanel
		extends JComponent
		implements Scrollable
	{
		private static final int SECOND_WIDTH = 200;
		private static final int NEW_SEQUENCE_TIME_LAG = 500;
		private static final int NEW_SEQUENCE_GAP = 20;

		private static class Data {
			final double value;
			final long time; // in milliseconds

			Data( double value, long time ) {
				this.value = value;
				this.time = time;
			}

			@Override
			public String toString() {
				// for debugging
				return String.valueOf( value );
			}
		}

		private final Map<Color, List<Data>> color2dataMap = new HashMap<>();
		private final Timer repaintTime;
		private Color lastUsedChartColor;
		private boolean updateDelayed;

		LineChartPanel() {
			int resolution = FlatUIUtils.getUIInt( "ScrollPane.smoothScrolling.resolution", 10 );

			repaintTime = new Timer( resolution * 2, e -> repaintAndRevalidate() );
			repaintTime.setRepeats( false );
		}

		void addValue( double value, Color chartColor ) {
			List<Data> chartData = color2dataMap.computeIfAbsent( chartColor, k -> new ArrayList<>() );
			chartData.add( new Data( value, System.nanoTime() / 1000000) );

			lastUsedChartColor = chartColor;

			if( updateDelayed ) {
				repaintTime.stop();
				repaintTime.start();
			} else
				repaintAndRevalidate();
		}

		void clear() {
			color2dataMap.clear();
			lastUsedChartColor = null;

			repaint();
			revalidate();
		}

		void setUpdateDelayed( boolean updateDelayed ) {
			this.updateDelayed = updateDelayed;
		}

		private void repaintAndRevalidate() {
			repaint();
			revalidate();

			// scroll horizontally
			if( lastUsedChartColor != null ) {
				int[] lastSeqX = new int[1];
				int cw = chartWidth( color2dataMap.get( lastUsedChartColor ), lastSeqX );
				scrollRectToVisible( new Rectangle( lastSeqX[0], 0, cw - lastSeqX[0], getHeight() ) );
			}
		}

		@Override
		protected void paintComponent( Graphics g ) {
			Graphics g2 = g.create();
			try {
				HiDPIUtils.paintAtScale1x( (Graphics2D) g2, this, this::paintImpl );
			} finally {
				g2.dispose();
			}
		}

		private void paintImpl( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
			FlatUIUtils.setRenderingHints( g );

			int secondWidth = (int) (SECOND_WIDTH * scaleFactor);
			int seqGapWidth = (int) (NEW_SEQUENCE_GAP * scaleFactor);

			g.translate( x, y );

			// fill background
			g.setColor( Color.white );
			g.fillRect( x, y, width, height );

			// paint horizontal lines
			g.setColor( Color.LIGHT_GRAY );
			for( int i = 1; i < 10; i++ ) {
				int hy = (height * i) / 10;
				g.drawLine( 0, hy, width, hy );
			}

			// paint vertical lines
			g.setColor( Color.LIGHT_GRAY );
			int twoHundredMillisWidth = secondWidth / 5;
			for( int i = twoHundredMillisWidth; i < width; i += twoHundredMillisWidth )
				g.drawLine( i, 0, i, height );

			// paint lines
			for( Map.Entry<Color, List<Data>> e : color2dataMap.entrySet() ) {
				Color chartColor = e.getKey();
				List<Data> chartData = e.getValue();
				Color temporaryValueColor = new Color( (chartColor.getRGB() & 0xffffff) | 0x40000000, true );

				long seqTime = 0;
				int seqX = 0;
				long ptime = 0;
				int px = 0;
				int py = 0;
				int pcount = 0;

				g.setColor( chartColor );

				int size = chartData.size();
				for( int i = 0; i < size; i++ ) {
					Data data = chartData.get( i );
					int dy = (int) ((height - 1) * data.value);

					if( data.time > ptime + NEW_SEQUENCE_TIME_LAG ) {
						if( i > 0 && pcount == 0 )
							g.drawLine( px, py, px + (int) (4 * scaleFactor), py );

						// start new sequence
						seqTime = data.time;
						seqX = (i > 0) ? px + seqGapWidth : 0;
						px = seqX;
						pcount = 0;
					} else {
						boolean isTemporaryValue = isTemporaryValue( chartData, i ) || isTemporaryValue( chartData, i - 1 );
						if( isTemporaryValue )
							g.setColor( temporaryValueColor );

						// line in sequence
						int dx = (int) (seqX + (((data.time - seqTime) / 1000.) * secondWidth));
						g.drawLine( px, py, dx, dy );
						px = dx;
						pcount++;

						if( isTemporaryValue )
							g.setColor( chartColor );
					}

					py = dy;
					ptime = data.time;
				}
			}
		}

		private boolean isTemporaryValue( List<Data> chartData, int i ) {
			if( i == 0 || i == chartData.size() - 1 )
				return false;

			return chartData.get( i - 1 ).value == chartData.get( i + 1 ).value;
		}

		private int chartWidth() {
			int width = 0;
			for( List<Data> chartData : color2dataMap.values() )
				width = Math.max( width, chartWidth( chartData, null ) );
			return width;
		}

		private int chartWidth( List<Data> chartData, int[] lastSeqX ) {
			long seqTime = 0;
			int seqX = 0;
			long ptime = 0;
			int px = 0;

			int size = chartData.size();
			for( int i = 0; i < size; i++ ) {
				Data data = chartData.get( i );

				if( data.time > ptime + NEW_SEQUENCE_TIME_LAG ) {
					// start new sequence
					seqTime = data.time;
					seqX = (i > 0) ? px + NEW_SEQUENCE_GAP : 0;
					px = seqX;
				} else {
					// line in sequence
					int dx = (int) (seqX + (((data.time - seqTime) / 1000.) * SECOND_WIDTH));
					px = dx;
				}

				ptime = data.time;
			}

			if( lastSeqX != null )
				lastSeqX[0] = seqX;

			return px;
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension( chartWidth(), 200 );
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return new Dimension( chartWidth(), 200 );
		}

		@Override
		public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
			return SECOND_WIDTH;
		}

		@Override
		public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
			JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass( JViewport.class, this );
			return (viewport != null) ? viewport.getWidth() : 200;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass( JViewport.class, this );
			return (viewport != null) ? viewport.getWidth() > chartWidth() : true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return true;
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
