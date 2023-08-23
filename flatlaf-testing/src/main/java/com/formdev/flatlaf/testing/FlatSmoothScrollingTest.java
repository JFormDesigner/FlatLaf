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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.*;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.HSLColor;
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

		oneSecondWidthChanged();
		updateChartDelayedChanged();

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

		// allow clearing chart with Alt+C without moving focus to button
		registerKeyboardAction(
			e -> {
				clearChart();
			},
			KeyStroke.getKeyStroke( "alt " + (char) clearChartButton.getMnemonic() ),
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

		// clear chart on startup
		addHierarchyListener( e -> {
			if( (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing() )
				EventQueue.invokeLater( this::clearChart );
		});

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

	private void oneSecondWidthChanged() {
		int oneSecondWidth = oneSecondWidthSlider.getValue();
		int msPerLineX =
			oneSecondWidth <= 2000 ? 100 :
			oneSecondWidth <= 4000 ? 50 :
			oneSecondWidth <= 8000 ? 25 :
			10;

		lineChartPanel.setOneSecondWidth( oneSecondWidth );
		lineChartPanel.setMsPerLineX( msPerLineX );
		lineChartPanel.revalidate();
		lineChartPanel.repaint();

		if( xLabelText == null )
			xLabelText = xLabel.getText();
		xLabel.setText( MessageFormat.format( xLabelText, msPerLineX ) );
	}
	private String xLabelText;

	private void clearChart() {
		lineChartPanel.clear();
	}

	private void updateChartDelayedChanged() {
		lineChartPanel.setUpdateDelayed( updateChartDelayedCheckBox.isSelected() );
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
		panel3 = new JPanel();
		chartScrollPane = new JScrollPane();
		lineChartPanel = new FlatSmoothScrollingTest.LineChartPanel();
		panel4 = new JPanel();
		xLabel = new JLabel();
		JLabel rectsLabel = new JLabel();
		JLabel yLabel = new JLabel();
		JLabel dotsLabel = new JLabel();
		JLabel oneSecondWidthLabel = new JLabel();
		oneSecondWidthSlider = new JSlider();
		updateChartDelayedCheckBox = new JCheckBox();
		clearChartButton = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[200,grow,fill]",
			// rows
			"[]" +
			"[grow,fill]" +
			"[]"));

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

			//======== panel3 ========
			{
				panel3.setLayout(new MigLayout(
					"insets 3,hidemode 3",
					// columns
					"[grow,fill]",
					// rows
					"[100:300,grow,fill]"));

				//======== chartScrollPane ========
				{
					chartScrollPane.putClientProperty("JScrollPane.smoothScrolling", false);
					chartScrollPane.setViewportView(lineChartPanel);
				}
				panel3.add(chartScrollPane, "cell 0 0");
			}
			splitPane1.setBottomComponent(panel3);
		}
		add(splitPane1, "cell 0 1");

		//======== panel4 ========
		{
			panel4.setLayout(new MigLayout(
				"insets 0,hidemode 3,gapy 0",
				// columns
				"[fill]para" +
				"[fill]",
				// rows
				"[]" +
				"[]"));

			//---- xLabel ----
			xLabel.setText("X: time ({0}ms per line)");
			panel4.add(xLabel, "cell 0 0");

			//---- rectsLabel ----
			rectsLabel.setText("Rectangles: scrollbar values (mouse hover shows stack)");
			panel4.add(rectsLabel, "cell 1 0");

			//---- yLabel ----
			yLabel.setText("Y: scroll bar value (10% per line)");
			panel4.add(yLabel, "cell 0 1");

			//---- dotsLabel ----
			dotsLabel.setText("Dots: disabled blitting mode in JViewport");
			panel4.add(dotsLabel, "cell 1 1");
		}
		add(panel4, "cell 0 2");

		//---- oneSecondWidthLabel ----
		oneSecondWidthLabel.setText("Scale X:");
		oneSecondWidthLabel.setDisplayedMnemonic('A');
		oneSecondWidthLabel.setLabelFor(oneSecondWidthSlider);
		add(oneSecondWidthLabel, "cell 0 2,alignx right,growx 0");

		//---- oneSecondWidthSlider ----
		oneSecondWidthSlider.setMinimum(1000);
		oneSecondWidthSlider.setMaximum(10000);
		oneSecondWidthSlider.addChangeListener(e -> oneSecondWidthChanged());
		add(oneSecondWidthSlider, "cell 0 2,alignx right,growx 0,wmax 100");

		//---- updateChartDelayedCheckBox ----
		updateChartDelayedCheckBox.setText("Update chart delayed");
		updateChartDelayedCheckBox.setMnemonic('P');
		updateChartDelayedCheckBox.setSelected(true);
		updateChartDelayedCheckBox.addActionListener(e -> updateChartDelayedChanged());
		add(updateChartDelayedCheckBox, "cell 0 2,alignx right,growx 0");

		//---- clearChartButton ----
		clearChartButton.setText("Clear Chart");
		clearChartButton.setMnemonic('C');
		clearChartButton.addActionListener(e -> clearChart());
		add(clearChartButton, "cell 0 2,alignx right,growx 0");
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
	private JPanel panel3;
	private JScrollPane chartScrollPane;
	private FlatSmoothScrollingTest.LineChartPanel lineChartPanel;
	private JPanel panel4;
	private JLabel xLabel;
	private JSlider oneSecondWidthSlider;
	private JCheckBox updateChartDelayedCheckBox;
	private JButton clearChartButton;
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

					lineChartPanel.addValue( value, true, chartColor, name );
				}
			} );
		}

		@Override
		public void stateChanged( ChangeEvent e ) {
			DefaultBoundedRangeModel m = (DefaultBoundedRangeModel) e.getSource();
			boolean smoothScrolling = smoothScrollingCheckBox.isSelected();

			lineChartPanel.addValue( getChartValue( m ), false, smoothScrolling ? chartColor : chartColor2, name );

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

	//---- class LineChartPanel -----------------------------------------------

	static class LineChartPanel
		extends JComponent
		implements Scrollable
	{
		private static final int NEW_SEQUENCE_TIME_LAG = 500;
		private static final int NEW_SEQUENCE_GAP = 100;
		private static final int HIT_OFFSET = 4;

		private int oneSecondWidth = 1000;
		private int msPerLineX = 200;

		private static class Data {
			final double value;
			final boolean dot;
			final long time; // in milliseconds
			final String name;
			final Exception stack;

			Data( double value, boolean dot, long time, String name, Exception stack ) {
				this.value = value;
				this.dot = dot;
				this.time = time;
				this.name = name;
				this.stack = stack;
			}

			@Override
			public String toString() {
				// for debugging
				return "value=" + value + ", dot=" + dot + ", time=" + time + ", name=" + name;
			}
		}

		private final Map<Color, List<Data>> color2dataMap = new HashMap<>();
		private final Timer repaintTime;
		private Color lastUsedChartColor;
		private boolean updateDelayed;

		private final List<Point> lastPoints = new ArrayList<>();
		private final List<Data> lastDatas = new ArrayList<>();
		private double lastSystemScaleFactor = 1;
		private String lastToolTipPrinted;

		LineChartPanel() {
			int resolution = FlatUIUtils.getUIInt( "ScrollPane.smoothScrolling.resolution", 10 );

			repaintTime = new Timer( resolution * 2, e -> repaintAndRevalidate() );
			repaintTime.setRepeats( false );

			ToolTipManager.sharedInstance().registerComponent( this );
		}

		void addValue( double value, boolean dot, Color chartColor, String name ) {
			List<Data> chartData = color2dataMap.computeIfAbsent( chartColor, k -> new ArrayList<>() );
			chartData.add( new Data( value, dot, System.nanoTime() / 1000000, name, new Exception() ) );

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

		void setOneSecondWidth( int oneSecondWidth ) {
			this.oneSecondWidth = oneSecondWidth;
		}

		void setMsPerLineX( int msPerLineX ) {
			this.msPerLineX = msPerLineX;
		}

		private void repaintAndRevalidate() {
			repaint();
			revalidate();

			// scroll horizontally
			if( lastUsedChartColor != null ) {
				// compute chart width of last used color and start of last sequence
				int[] lastSeqX = new int[1];
				int cw = chartWidth( color2dataMap.get( lastUsedChartColor ), lastSeqX );

				// scroll to end of last sequence (of last used color)
				int lastSeqWidth = cw - lastSeqX[0];
				int width = Math.min( lastSeqWidth, getParent().getWidth() );
				int x = cw - width;
				scrollRectToVisible( new Rectangle( x, 0, width, getHeight() ) );
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

			int oneSecondWidth = (int) (this.oneSecondWidth * scaleFactor);
			int seqGapWidth = (int) (NEW_SEQUENCE_GAP * scaleFactor);
			int hitOffset = (int) Math.round( UIScale.scale( HIT_OFFSET ) * scaleFactor );

			Color lineColor = FlatUIUtils.getUIColor( "Component.borderColor", Color.lightGray );
			Color lineColor2 = FlatLaf.isLafDark()
				? new HSLColor( lineColor ).adjustTone( 30 )
				: new HSLColor( lineColor ).adjustShade( 30 );

			g.translate( x, y );

			// fill background
			g.setColor( UIManager.getColor( "Table.background" ) );
			g.fillRect( x, y, width, height );

			// paint horizontal lines
			for( int i = 1; i < 10; i++ ) {
				int hy = (height * i) / 10;
				g.setColor( (i != 5) ? lineColor : lineColor2 );
				g.drawLine( 0, hy, width, hy );
			}

			// paint vertical lines
			int perLineXWidth = Math.round( (oneSecondWidth / 1000f) * msPerLineX );
			for( int i = 1, xv = perLineXWidth; xv < width; xv += perLineXWidth, i++ ) {
				g.setColor( (i % 5 != 0) ? lineColor : lineColor2 );
				g.drawLine( xv, 0, xv, height );
			}

			lastPoints.clear();
			lastDatas.clear();
			lastSystemScaleFactor = scaleFactor;

			// paint lines
			for( Map.Entry<Color, List<Data>> e : color2dataMap.entrySet() ) {
				List<Data> chartData = e.getValue();
				Color chartColor = e.getKey();
				if( FlatLaf.isLafDark() )
					chartColor = new HSLColor( chartColor ).adjustTone( 50 );
				Color temporaryValueColor = ColorFunctions.fade( chartColor, FlatLaf.isLafDark() ? 0.7f : 0.3f );
				Color dataPointColor = ColorFunctions.fade( chartColor, FlatLaf.isLafDark() ? 0.6f : 0.2f );

				// sequence start time and x coordinate
				long seqTime = 0;
				int seqX = 0;

				// "previous" data point time, x/y coordinates and count
				long ptime = 0;
				int px = 0;
				int py = 0;
				int pcount = 0;

				boolean first = true;
				boolean isTemporaryValue = false;
				int lastTemporaryValueIndex = -1;

				int size = chartData.size();
				for( int i = 0; i < size; i++ ) {
					Data data = chartData.get( i );

					boolean newSeq = (data.time > ptime + NEW_SEQUENCE_TIME_LAG);
					ptime = data.time;

					if( newSeq ) {
						// paint short horizontal line for previous sequence that has only one data point
						if( !first && pcount == 0 ) {
							g.setColor( chartColor );
							g.drawLine( px, py, px + (int) Math.round( UIScale.scale( 8 ) * scaleFactor ), py );
						}

						// start new sequence
						seqTime = data.time;
						seqX = !first ? px + seqGapWidth : 0;
						px = seqX;
						pcount = 0;
						first = false;
						isTemporaryValue = false;
					}

					// x/y coordinates of current data point
					int dy = (int) ((height - 1) * data.value);
					int dx = (int) (seqX + (((data.time - seqTime) / 1000.) * oneSecondWidth));

					// paint rectangle to indicate data point
					g.setColor( dataPointColor );
					g.drawRect( dx - hitOffset, dy - hitOffset, hitOffset * 2, hitOffset * 2 );

					// remember data point for tooltip
					lastPoints.add( new Point( dx, dy ) );
					lastDatas.add( data );

					if( data.dot ) {
						int s1 = (int) Math.round( UIScale.scale( 1 ) * scaleFactor );
						int s3 = (int) Math.round( UIScale.scale( 3 ) * scaleFactor );
						g.setColor( chartColor );
						g.fillRect( dx - s1, dy - s1, s3, s3 );
						continue;
					}

					if( !newSeq ) {
						if( isTemporaryValue && i > lastTemporaryValueIndex )
							isTemporaryValue = false;

						g.setColor( isTemporaryValue ? temporaryValueColor : chartColor );

						// line in sequence
						g.drawLine( px, py, dx, dy );

						px = dx;
						pcount++;

						// check next data points for "temporary" value(s)
						if( !isTemporaryValue ) {
							// one or two values between two equal values are considered "temporary",
							// which means that they are the target value for the following scroll animation
							int stage = 0;
							for( int j = i + 1; j < size && stage <= 2 && !isTemporaryValue; j++ ) {
								Data nextData = chartData.get( j );
								if( nextData.dot )
									continue; // ignore dots

								// check whether next data point is within 10 milliseconds
								if( nextData.time > data.time + 10 )
									break;

								if( stage >= 1 && stage <= 2 && nextData.value == data.value ) {
									isTemporaryValue = true;
									lastTemporaryValueIndex = j;
								}
								stage++;
							}
						}
					}

					py = dy;
				}
			}
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
					int dx = (int) (seqX + (((data.time - seqTime) / 1000.) * oneSecondWidth));
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
			return oneSecondWidth;
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

		@Override
		public String getToolTipText( MouseEvent e ) {
			int x = (int) Math.round( e.getX() * lastSystemScaleFactor );
			int y = (int) Math.round( e.getY() * lastSystemScaleFactor );
			int hitOffset = (int) Math.round( UIScale.scale( HIT_OFFSET ) * lastSystemScaleFactor );
			StringBuilder buf = null;

			int pointsCount = lastPoints.size();
			for( int i = 0; i < pointsCount; i++ ) {
				Point pt = lastPoints.get( i );

				// check X/Y coordinates
				if( x < pt.x - hitOffset || x > pt.x + hitOffset ||
					y < pt.y - hitOffset || y > pt.y + hitOffset )
				  continue;

				if( buf == null ) {
					buf = new StringBuilder( 5000 );
					buf.append( "<html>" );
				}

				Data data = lastDatas.get( i );
				buf.append( "<h2>" );
				if( data.dot )
					buf.append( "DOT: " );
				buf.append( data.name ).append( ' ' ).append( data.value ).append( "</h2>" );

				StackTraceElement[] stackTrace = data.stack.getStackTrace();
				for( int j = 0; j < stackTrace.length; j++ ) {
					StackTraceElement stackElement = stackTrace[j];
					String className = stackElement.getClassName();
					String methodName = stackElement.getMethodName();

					// ignore methods from this class
					if( className.startsWith( FlatSmoothScrollingTest.class.getName() ) )
						continue;

					int repeatCount = 0;
					for( int k = j + 1; k < stackTrace.length; k++ ) {
						if( !stackElement.equals( stackTrace[k] ) )
							break;
						repeatCount++;
					}
					j += repeatCount;

					// append method
					buf.append( className )
						.append( ".<b>" )
						.append( methodName )
						.append( "</b> <span color=\"#888888\">" );
					if( stackElement.getFileName() != null ) {
						buf.append( '(' );
						buf.append( stackElement.getFileName() );
						if( stackElement.getLineNumber() >= 0 )
							buf.append( ':' ).append( stackElement.getLineNumber() );
						buf.append( ')' );
					} else
						buf.append( "(Unknown Source)" );
					buf.append( "</span>" );
					if( repeatCount > 0 )
						buf.append( " <b>" ).append( repeatCount + 1 ).append( "x</b>" );
					buf.append( "<br>" );

					// break at some methods to make stack smaller
					if( (className.startsWith( "java.awt.event.InvocationEvent" ) && methodName.equals( "dispatch" )) ||
						(className.startsWith( "java.awt.Component" ) && methodName.equals( "processMouseWheelEvent" )) ||
						(className.startsWith( "javax.swing.JComponent" ) && methodName.equals( "processKeyBinding" )) )
					  break;
				}
				buf.append( "..." );
			}

			if( buf == null )
				return null;

			buf.append( "<html>" );
			String toolTip = buf.toString();

			// print to console
			if( !Objects.equals( toolTip, lastToolTipPrinted ) ) {
				lastToolTipPrinted = toolTip;

				System.out.println( toolTip
					.replace( "<br>", "\n" )
					.replace( "<h2>", "\n---- " )
					.replace( "</h2>", " ----\n" )
					.replaceAll( "<[^>]+>", "" ) );
			}

			return buf.toString();
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
