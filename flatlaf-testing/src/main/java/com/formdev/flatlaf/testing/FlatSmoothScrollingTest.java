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
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.*;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Animator;
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

		initializeDurationAndResolution();

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
		for( int i = 0; i < 10; i++ ) {
			for( int j = 0; j < 10; j++ ) {
				char[] chars = new char[i*10 + j + 1];
				Arrays.fill( chars, ' ' );
				chars[chars.length - 1] = (char) ('0' + j);
				if( i >= 5 )
					chars[50 - 1 - ((i-5)*10) - j] = (char) ('0' + j);
				items.add( new String( chars ) );
			}
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
				return (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF) ? 100 : 2;
			}
			@Override
			public Object getValueAt( int rowIndex, int columnIndex ) {
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
		String longText = "";
		for( int i = 0; i < 100; i++ )
			longText += String.format( "%-5d     ", i );
		longText += "100";
		String text = items.stream().collect( Collectors.joining( "\n" ) ) + '\n';
		textArea.setText( longText + '\n' + text );
		textPane.setText( text );
		editorPane.setText( text );

		// move selection to beginning in text components
		textArea.select( 0, 0 );
		textPane.select( 0, 0 );
		editorPane.select( 0, 0 );

		// custom scrollable
		StringBuilder buf = new StringBuilder()
			.append( "<html>" )
			.append( customButton.getText() );
		for( String item : items ) {
			buf.append( "<br>" );
			for( int i = 0; i < item.length(); i++ ) {
				char ch = item.charAt( i );
				if( ch == ' ' )
					buf.append( "&nbsp;" );
				else
					buf.append( ch );
			}
		}
		buf.append( "</html>" );
		customButton.setText( buf.toString() );

		// line chart
		lineChartPanel.addMethodHighlight( JViewport.class.getName() + ".setViewPosition", "#0000bb" );
		lineChartPanel.addMethodHighlight( JViewport.class.getName() + ".scrollRectToVisible", "#00bbbb" );
		lineChartPanel.addMethodHighlight( JScrollBar.class.getName() + ".setValue", "#00aa00" );
		lineChartPanel.addMethodHighlight( Animator.class.getName() + ".timingEvent", "#bb0000" );
		lineChartPanel.addMethodHighlight( JComponent.class.getName() + ".processKeyBinding", "#bb0000" );
		lineChartPanel.addMethodHighlight( Component.class.getName() + ".processMouseEvent", "#bb0000" );
		lineChartPanel.addMethodHighlight( Component.class.getName() + ".processMouseWheelEvent", "#bb0000" );
		lineChartPanel.addMethodHighlight( Component.class.getName() + ".processMouseMotionEvent", "#bb0000" );
		lineChartPanel.addMethodHighlight( "actionPerformed", "#bbbb00" );

		// request focus for list
		EventQueue.invokeLater( () -> {
			EventQueue.invokeLater( () -> {
				list.requestFocusInWindow();
			} );
		} );
	}

	private void smoothScrollingChanged() {
		UIManager.put( "ScrollPane.smoothScrolling", smoothScrollingCheckBox.isSelected() );
	}

	private void initializeDurationAndResolution() {
		int duration = FlatUIUtils.getUIInt( "ScrollPane.smoothScrolling.duration", 200 );
		int resolution = FlatUIUtils.getUIInt( "ScrollPane.smoothScrolling.resolution", 10 );

		durationSlider.setValue( duration );
		resolutionSlider.setValue( resolution );

		updateDurationAndResolutionLabels( duration, resolution );
	}

	private void durationOrResolutionChanged() {
		int duration = durationSlider.getValue();
		int resolution = resolutionSlider.getValue();

		updateDurationAndResolutionLabels( duration, resolution );

		UIManager.put( "ScrollPane.smoothScrolling.duration", duration );
		UIManager.put( "ScrollPane.smoothScrolling.resolution", resolution );

		// update UI of scroll bars to force re-creation of animator
		JScrollPane[] scrollPanes = { listScrollPane, treeScrollPane, tableScrollPane,
			textAreaScrollPane, textPaneScrollPane, editorPaneScrollPane, customScrollPane };
		for( JScrollPane scrollPane : scrollPanes ) {
			scrollPane.getVerticalScrollBar().updateUI();
			scrollPane.getHorizontalScrollBar().updateUI();
		}
	}

	private void updateDurationAndResolutionLabels( int duration, int resolution ) {
		durationValueLabel.setText( duration + " ms" );
		resolutionValueLabel.setText( resolution + " ms" );
	}

	private void scrollToChanged() {
		if( scrollToSlider.getValueIsAdjusting() )
			return;

		int value = scrollToSlider.getValue();
		JComponent[] comps = { list, tree, table, textArea, textPane, editorPane, customButton };
		for( JComponent c : comps ) {
			int x = (c.getWidth() * value) / 100;
			int y = (c.getHeight() * value) / 100;
			c.scrollRectToVisible( new Rectangle( x, y, 1, 1 ) );
		}
	}

	private void rowHeaderChanged() {
		JTable rowHeader = null;
		if( rowHeaderCheckBox.isSelected() ) {
			rowHeader = new JTable();
			rowHeader.setPreferredScrollableViewportSize( new Dimension( UIScale.scale( 50 ), 100 ) );
			rowHeader.setModel( new AbstractTableModel() {
				@Override
				public int getRowCount() {
					return table.getRowCount();
				}
				@Override
				public int getColumnCount() {
					return 1;
				}
				@Override
				public Object getValueAt( int rowIndex, int columnIndex ) {
					char[] chars = new char[10];
					Arrays.fill( chars, ' ' );
					int i = rowIndex % 10;
					if( (rowIndex / 10) % 2 == 0 )
						chars[i] = (char) ('0' + i);
					else
						chars[9 - i] = (char) ('A' + i);
					return new String( chars );
				}
			} );
		}
		tableScrollPane.setRowHeaderView( rowHeader );
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
		((AbstractTableModel)table.getModel()).fireTableStructureChanged();
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
		JPanel hSpacer1 = new JPanel(null);
		JLabel durationLabel = new JLabel();
		durationSlider = new JSlider();
		durationValueLabel = new JLabel();
		JLabel resolutionLabel = new JLabel();
		resolutionSlider = new JSlider();
		resolutionValueLabel = new JLabel();
		splitPane1 = new JSplitPane();
		splitPane2 = new JSplitPane();
		panel1 = new JPanel();
		listLabel = new JLabel();
		treeLabel = new JLabel();
		tableLabel = new JLabel();
		rowHeaderCheckBox = new JCheckBox();
		showTableGridCheckBox = new JCheckBox();
		autoResizeModeCheckBox = new JCheckBox();
		listScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		list = new JList<>();
		treeScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		tree = new JTree();
		tableScrollPane = new FlatSmoothScrollingTest.DebugScrollPane();
		table = new JTable();
		scrollToSlider = new JSlider();
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
		customButton = new JButton();
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
		add(hSpacer1, "cell 0 0,growx");

		//---- durationLabel ----
		durationLabel.setText("Duration:");
		add(durationLabel, "cell 0 0");

		//---- durationSlider ----
		durationSlider.setMaximum(5000);
		durationSlider.setValue(200);
		durationSlider.setSnapToTicks(true);
		durationSlider.setMinimum(100);
		durationSlider.setMinorTickSpacing(50);
		durationSlider.addChangeListener(e -> durationOrResolutionChanged());
		add(durationSlider, "cell 0 0");

		//---- durationValueLabel ----
		durationValueLabel.setText("0000 ms");
		add(durationValueLabel, "cell 0 0,width 50");

		//---- resolutionLabel ----
		resolutionLabel.setText("Resolution:");
		add(resolutionLabel, "cell 0 0");

		//---- resolutionSlider ----
		resolutionSlider.setMaximum(1000);
		resolutionSlider.setMinimum(10);
		resolutionSlider.setValue(10);
		resolutionSlider.setMinorTickSpacing(10);
		resolutionSlider.setSnapToTicks(true);
		resolutionSlider.addChangeListener(e -> durationOrResolutionChanged());
		add(resolutionSlider, "cell 0 0");

		//---- resolutionValueLabel ----
		resolutionValueLabel.setText("0000 ms");
		add(resolutionValueLabel, "cell 0 0,width 50");

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
						"[200,grow,fill]" +
						"[fill]",
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

					//---- rowHeaderCheckBox ----
					rowHeaderCheckBox.setText("Row header");
					rowHeaderCheckBox.addActionListener(e -> rowHeaderChanged());
					panel1.add(rowHeaderCheckBox, "cell 2 0 2 1,alignx right,growx 0");

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

					//---- scrollToSlider ----
					scrollToSlider.setOrientation(SwingConstants.VERTICAL);
					scrollToSlider.setValue(0);
					scrollToSlider.setInverted(true);
					scrollToSlider.addChangeListener(e -> scrollToChanged());
					panel1.add(scrollToSlider, "cell 4 1");
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

						//---- customButton ----
						customButton.setText("I'm a large button, but do not implement Scrollable interface");
						customButton.setHorizontalAlignment(SwingConstants.LEADING);
						customButton.setVerticalAlignment(SwingConstants.TOP);
						customScrollPane.setViewportView(customButton);
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
	private JSlider durationSlider;
	private JLabel durationValueLabel;
	private JSlider resolutionSlider;
	private JLabel resolutionValueLabel;
	private JSplitPane splitPane1;
	private JSplitPane splitPane2;
	private JPanel panel1;
	private JLabel listLabel;
	private JLabel treeLabel;
	private JLabel tableLabel;
	private JCheckBox rowHeaderCheckBox;
	private JCheckBox showTableGridCheckBox;
	private JCheckBox autoResizeModeCheckBox;
	private FlatSmoothScrollingTest.DebugScrollPane listScrollPane;
	private JList<String> list;
	private FlatSmoothScrollingTest.DebugScrollPane treeScrollPane;
	private JTree tree;
	private FlatSmoothScrollingTest.DebugScrollPane tableScrollPane;
	private JTable table;
	private JSlider scrollToSlider;
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
	private JButton customButton;
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
		private int lastValue;
		private long lastTime;

		ScrollBarChangeHandler( DebugScrollPane scrollPane, boolean vertical, String name, Color chartColor ) {
			this.name = name;
			this.chartColor = chartColor;
			this.chartColor2 = ColorFunctions.lighten( chartColor, 0.1f );

			// add change listener to viewport that is invoked from JViewport.setViewPosition()
			scrollPane.getViewport().addChangeListener( e -> {
				JViewport viewport = scrollPane.getViewport();
				Point viewPosition = viewport.getViewPosition();

				if( (vertical && viewPosition.y != scrollPane.previousViewPosition.y) ||
					(!vertical && viewPosition.x != scrollPane.previousViewPosition.x) )
				{
					// calculate value from view position because scrollbar value is not yet up-to-date
					Dimension viewSize = viewport.getViewSize();
					double value = vertical
						? ((double) viewPosition.y) / (viewSize.height - viewport.getHeight())
						: ((double) viewPosition.x) / (viewSize.width - viewport.getWidth());
					int ivalue = vertical ? viewPosition.y : viewPosition.x;

					// add dot to chart if blit scroll mode is disabled
					boolean dot = (scrollPane.getViewport().getScrollMode() != JViewport.BLIT_SCROLL_MODE);

					Color color = smoothScrollingCheckBox.isSelected() ? this.chartColor : chartColor2;
					if( dot )
						lineChartPanel.addValueWithDot( color, value, ivalue, null, name );
					else
						lineChartPanel.addValue( color, value, ivalue, name );
				}
			} );
		}

		@Override
		public void stateChanged( ChangeEvent e ) {
			DefaultBoundedRangeModel m = (DefaultBoundedRangeModel) e.getSource();
			int value = m.getValue();
/*
			double chartValue = (double) (value - m.getMinimum()) / (double) (m.getMaximum() - m.getExtent());
			lineChartPanel.addValue( chartValue, value, false, false,
				smoothScrollingCheckBox.isSelected() ? chartColor : chartColor2, name );
*/
			long t = System.nanoTime() / 1000000;

			System.out.printf( "%s (%d):  %4d --> %4d  %3d ms   %-5b   %s%n",
				name, ++count,
				lastValue,
				value,
				t - lastTime,
				m.getValueIsAdjusting(),
				value > lastValue ? "down" : value < lastValue ? "up" : "" );

			lastValue = value;
			lastTime = t;
		}
	}

	//---- class DebugViewport ------------------------------------------------

	private static class DebugScrollPane
		extends JScrollPane
	{
		Point previousViewPosition = new Point();

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
					// remember previous view position
					previousViewPosition = getViewPosition();

					super.setViewPosition( p );
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
