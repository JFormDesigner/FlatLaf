/*
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HSLColor;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class LineChartPanel
	extends JPanel
{
	LineChartPanel() {
		initComponents();

		lineChartScrollPane.putClientProperty( FlatClientProperties.SCROLL_PANE_SMOOTH_SCROLLING, false );

		oneSecondWidthChanged();
		updateChartDelayedChanged();

		// clear chart on startup
		addHierarchyListener( e -> {
			if( (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing() )
				EventQueue.invokeLater( this::clearChart );
		} );

		// show chart tooltips immediately and forever
		ToolTipManager.sharedInstance().setInitialDelay( 0 );
		ToolTipManager.sharedInstance().setDismissDelay( Integer.MAX_VALUE );
	}

	@Override
	public void addNotify() {
		super.addNotify();

		// allow clearing chart with Alt+C without moving focus to button
		getRootPane().registerKeyboardAction(
			e -> clearChart(),
			KeyStroke.getKeyStroke( "alt " + (char) clearChartButton.getMnemonic() ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	public String getLegendYValueText() {
		return yValueLabel.getText();
	}

	public void setLegendYValueText( String s ) {
		yValueLabel.setText( s );
	}

	public String getLegend1Text() {
		return legend1Label.getText();
	}

	public void setLegend1Text( String s ) {
		legend1Label.setText( s );
	}

	public String getLegend2Text() {
		return legend2Label.getText();
	}

	public void setLegend2Text( String s ) {
		legend2Label.setText( s );
	}

	public boolean isUpdateChartDelayed() {
		return updateChartDelayedCheckBox.isSelected();
	}

	public void setUpdateChartDelayed( boolean updateChartDelayed ) {
		updateChartDelayedCheckBox.setSelected( updateChartDelayed );
	}

	void addValue( Color chartColor, double value, int ivalue, String name ) {
		lineChart.addValue( chartColor, value, ivalue, null, false, name );
	}

	void addValueWithDot( Color chartColor, double value, int ivalue, Color dotColor, String name ) {
		if( dotColor == null )
			dotColor = chartColor;
		lineChart.addValue( chartColor, value, ivalue, dotColor, false, name );
	}

	void addDot( Color chartColor, double value, int ivalue, Color dotColor, String name ) {
		if( dotColor == null )
			dotColor = chartColor;
		lineChart.addValue( chartColor, value, ivalue, dotColor, true, name );
	}

	void addMethodHighlight( String classAndMethod, String highlightColor ) {
		lineChart.methodHighlightMap.put( classAndMethod, highlightColor );
	}

	private void oneSecondWidthChanged() {
		int oneSecondWidth = oneSecondWidthSlider.getValue();
		int msPerLineX =
			oneSecondWidth <= 2000 ? 100 :
			oneSecondWidth <= 4000 ? 50 :
			oneSecondWidth <= 8000 ? 25 :
			10;

		lineChart.setOneSecondWidth( oneSecondWidth );
		lineChart.setMsPerLineX( msPerLineX );
		lineChart.revalidate();
		lineChart.repaint();

		if( xLabelText == null )
			xLabelText = xLabel.getText();
		xLabel.setText( MessageFormat.format( xLabelText, msPerLineX ) );
	}
	private String xLabelText;

	private void updateChartDelayedChanged() {
		lineChart.setUpdateDelayed( updateChartDelayedCheckBox.isSelected() );
	}

	private void clearChart() {
		lineChart.clear();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		lineChartScrollPane = new JScrollPane();
		lineChart = new LineChartPanel.LineChart();
		JPanel legendPanel = new JPanel();
		xLabel = new JLabel();
		legend1Label = new JLabel();
		JLabel yLabel = new JLabel();
		yValueLabel = new JLabel();
		JLabel yLabel2 = new JLabel();
		JPanel hSpacer1 = new JPanel(null);
		legend2Label = new JLabel();
		JLabel oneSecondWidthLabel = new JLabel();
		oneSecondWidthSlider = new JSlider();
		updateChartDelayedCheckBox = new JCheckBox();
		clearChartButton = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"hidemode 3",
			// columns
			"[grow,fill]",
			// rows
			"[100:300,grow,fill]" +
			"[]"));

		//======== lineChartScrollPane ========
		{
			lineChartScrollPane.setViewportView(lineChart);
		}
		add(lineChartScrollPane, "cell 0 0");

		//======== legendPanel ========
		{
			legendPanel.setLayout(new MigLayout(
				"insets 0,hidemode 3,gapy 0",
				// columns
				"[fill]para" +
				"[fill]",
				// rows
				"[]" +
				"[]"));

			//---- xLabel ----
			xLabel.setText("X: time ({0}ms per line)");
			legendPanel.add(xLabel, "cell 0 0");
			legendPanel.add(legend1Label, "cell 1 0");

			//---- yLabel ----
			yLabel.setText("Y: ");
			legendPanel.add(yLabel, "cell 0 1,gapx 0 0");

			//---- yValueLabel ----
			yValueLabel.setText("value");
			legendPanel.add(yValueLabel, "cell 0 1,gapx 0 0");

			//---- yLabel2 ----
			yLabel2.setText(" (10% per line)");
			legendPanel.add(yLabel2, "cell 0 1,gapx 0 0");
			legendPanel.add(hSpacer1, "cell 0 1,growx");
			legendPanel.add(legend2Label, "cell 1 1");
		}
		add(legendPanel, "cell 0 1");

		//---- oneSecondWidthLabel ----
		oneSecondWidthLabel.setText("Scale X:");
		oneSecondWidthLabel.setDisplayedMnemonic('A');
		oneSecondWidthLabel.setLabelFor(oneSecondWidthSlider);
		add(oneSecondWidthLabel, "cell 0 1,alignx right,growx 0");

		//---- oneSecondWidthSlider ----
		oneSecondWidthSlider.setMinimum(1000);
		oneSecondWidthSlider.setMaximum(10000);
		oneSecondWidthSlider.addChangeListener(e -> oneSecondWidthChanged());
		add(oneSecondWidthSlider, "cell 0 1,alignx right,growx 0,wmax 100");

		//---- updateChartDelayedCheckBox ----
		updateChartDelayedCheckBox.setText("Update chart delayed");
		updateChartDelayedCheckBox.setMnemonic('P');
		updateChartDelayedCheckBox.setSelected(true);
		updateChartDelayedCheckBox.addActionListener(e -> updateChartDelayedChanged());
		add(updateChartDelayedCheckBox, "cell 0 1,alignx right,growx 0");

		//---- clearChartButton ----
		clearChartButton.setText("Clear Chart");
		clearChartButton.setMnemonic('C');
		clearChartButton.addActionListener(e -> clearChart());
		add(clearChartButton, "cell 0 1,alignx right,growx 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	private JScrollPane lineChartScrollPane;
	private LineChartPanel.LineChart lineChart;
	private JLabel xLabel;
	private JLabel legend1Label;
	private JLabel yValueLabel;
	private JLabel legend2Label;
	private JSlider oneSecondWidthSlider;
	private JCheckBox updateChartDelayedCheckBox;
	private JButton clearChartButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on


	//---- class LineChart ----------------------------------------------------

	private static class LineChart
		extends JComponent
		implements Scrollable
	{
		private static final int UPDATE_DELAY_MS = 20;

		private static final int NEW_SEQUENCE_TIME_LAG = 500;
		private static final int NEW_SEQUENCE_GAP = 100;
		private static final int HIT_OFFSET = 4;

		private int oneSecondWidth = 1000;
		private int msPerLineX = 200;
		private final HashMap<String, String> methodHighlightMap = new HashMap<>();

		private static class Data {
			final double value;
			final int ivalue;
			final Color dotColor;
			final boolean dotOnly;
			final long time; // in milliseconds
			final String name;
			final Exception stack;

			Data( double value, int ivalue, Color dotColor, boolean dotOnly, long time, String name, Exception stack ) {
				this.value = value;
				this.ivalue = ivalue;
				this.dotColor = dotColor;
				this.dotOnly = dotOnly;
				this.time = time;
				this.name = name;
				this.stack = stack;
			}

			@Override
			public String toString() {
				// for debugging
				return "value=" + value + ", ivalue=" + ivalue + ", dotColor=" + dotColor
					+ ", dotOnly=" + dotOnly + ", time=" + time + ", name=" + name;
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

		LineChart() {
			repaintTime = new Timer( UPDATE_DELAY_MS, e -> repaintAndRevalidate() );
			repaintTime.setRepeats( false );

			ToolTipManager.sharedInstance().registerComponent( this );
		}

		void addValue( Color chartColor, double value, int ivalue, Color dotColor, boolean dotOnly, String name ) {
			List<Data> chartData = color2dataMap.computeIfAbsent( chartColor, k -> new ArrayList<>() );
			chartData.add( new Data( value, ivalue, dotColor, dotOnly, System.nanoTime() / 1000000, name, new Exception() ) );

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

			int oneSecondWidth = (int) (UIScale.scale( this.oneSecondWidth ) * scaleFactor);
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
				Color temporaryValueColor = fade( chartColor, FlatLaf.isLafDark() ? 0.7f : 0.3f );
				Color dataPointColor = fade( chartColor, FlatLaf.isLafDark() ? 0.6f : 0.2f );

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

					if( data.dotColor != null ) {
						int s1 = (int) Math.round( UIScale.scale( 1 ) * scaleFactor );
						int s3 = (int) Math.round( UIScale.scale( 3 ) * scaleFactor );
						g.setColor( data.dotColor );
						g.fillRect( dx - s1, dy - s1, s3, s3 );
						if( data.dotOnly )
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
								if( nextData.dotOnly )
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
					int dx = (int) (seqX + (((data.time - seqTime) / 1000.) * UIScale.scale( oneSecondWidth )));
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
			return UIScale.scale( oneSecondWidth );
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
				if( data.dotOnly )
					buf.append( "DOT: " );
				buf.append( data.name ).append( ' ' ).append( data.ivalue ).append( "</h2>" );

				StackTraceElement[] stackTrace = data.stack.getStackTrace();
				for( int j = 0; j < stackTrace.length; j++ ) {
					StackTraceElement stackElement = stackTrace[j];
					String className = stackElement.getClassName();
					String methodName = stackElement.getMethodName();
					String classAndMethod = className + '.' + methodName;

					// ignore methods from this class
					if( className.startsWith( LineChartPanel.class.getName() ) )
						continue;

					int repeatCount = 0;
					for( int k = j + 1; k < stackTrace.length; k++ ) {
						if( !stackElement.equals( stackTrace[k] ) )
							break;
						repeatCount++;
					}
					j += repeatCount;

					String highlight = methodHighlightMap.get( classAndMethod );
					if( highlight == null )
						highlight = methodHighlightMap.get( className );
					if( highlight == null )
						highlight = methodHighlightMap.get( methodName );
					if( highlight != null )
						buf.append( "<span color=\"" ).append( highlight ).append( "\">" );

					// append method
					buf.append( className )
						.append( ".<b>" )
						.append( methodName )
						.append( "</b>" );
					if( highlight != null )
						buf.append( "</span>" );

					// append source
					buf.append( " <span color=\"#888888\">" );
					if( stackElement.getFileName() != null ) {
						buf.append( '(' );
						buf.append( stackElement.getFileName() );
						if( stackElement.getLineNumber() >= 0 )
							buf.append( ':' ).append( stackElement.getLineNumber() );
						buf.append( ')' );
					} else
						buf.append( "(Unknown Source)" );
					buf.append( "</span>" );

					// append repeat count
					if( repeatCount > 0 )
						buf.append( " <b>" ).append( repeatCount + 1 ).append( "x</b>" );
					buf.append( "<br>" );

					// break at some methods to make stack smaller
					if( classAndMethod.equals( "java.awt.event.InvocationEvent.dispatch" ) ||
						classAndMethod.equals( "java.awt.Component.processMouseEvent" ) ||
						classAndMethod.equals( "java.awt.Component.processMouseWheelEvent" ) ||
						classAndMethod.equals( "java.awt.Component.processMouseMotionEvent" ) ||
						classAndMethod.equals( "javax.swing.JComponent.processKeyBinding" ) ||
						classAndMethod.equals( "com.formdev.flatlaf.util.Animator.timingEvent" ) )
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

		//TODO remove and use ColorFunctions.fade() when merging to main
		private static Color fade( Color color, float amount ) {
			int newAlpha = Math.round( 255 * amount );
			return new Color( (color.getRGB() & 0xffffff) | (newAlpha << 24), true );
		}
	}
}
