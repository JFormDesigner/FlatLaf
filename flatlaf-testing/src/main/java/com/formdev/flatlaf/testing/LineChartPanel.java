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

	public boolean isYZeroAtTop() {
		return lineChart.yZeroAtTop;
	}

	public void setYZeroAtTop( boolean yZeroAtTop ) {
		lineChart.yZeroAtTop = yZeroAtTop;
		lineChart.repaint();
	}

	public boolean isAsynchron() {
		return lineChart.asynchron;
	}

	public void setAsynchron( boolean asynchron ) {
		lineChart.asynchron = asynchron;
		lineChart.repaint();
	}

	public boolean isTemporaryValueDetection() {
		return lineChart.temporaryValueDetection;
	}

	public void setTemporaryValueDetection( boolean temporaryValueDetection ) {
		lineChart.temporaryValueDetection = temporaryValueDetection;
		lineChart.repaint();
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

	public int getOneSecondWidth() {
		return oneSecondWidthSlider.getValue();
	}

	public void setOneSecondWidth( int oneSecondWidth ) {
		oneSecondWidthSlider.setValue( oneSecondWidth );
	}

	public boolean isUpdateChartDelayed() {
		return updateChartDelayedCheckBox.isSelected();
	}

	public void setUpdateChartDelayed( boolean updateChartDelayed ) {
		updateChartDelayedCheckBox.setSelected( updateChartDelayed );
		updateChartDelayedChanged();
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

		lineChart.oneSecondWidth = oneSecondWidth;
		lineChart.msPerLineX = msPerLineX;
		lineChart.revalidate();
		lineChart.repaint();

		if( xLabelText == null )
			xLabelText = xLabel.getText();
		xLabel.setText( MessageFormat.format( xLabelText, msPerLineX ) );
	}
	private String xLabelText;

	private void updateChartDelayedChanged() {
		lineChart.updateDelayed = updateChartDelayedCheckBox.isSelected();
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
		oneSecondWidthSlider.setMinimum(100);
		oneSecondWidthSlider.setMaximum(10000);
		oneSecondWidthSlider.setSnapToTicks(true);
		oneSecondWidthSlider.setMajorTickSpacing(100);
		oneSecondWidthSlider.setValue(500);
		oneSecondWidthSlider.addChangeListener(e -> oneSecondWidthChanged());
		add(oneSecondWidthSlider, "cell 0 1,alignx right,growx 0");

		//---- updateChartDelayedCheckBox ----
		updateChartDelayedCheckBox.setText("Update chart delayed");
		updateChartDelayedCheckBox.setMnemonic('P');
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
		private static final int NEW_SEQUENCE_TIME_LAG_MS = 500;
		private static final int NEW_SEQUENCE_GAP_MS = 100;
		private static final int HIT_OFFSET = 4;

		private static final boolean TEST = false;

		// asynchron means that chart for each color starts at x=0
		private boolean asynchron;
		private boolean temporaryValueDetection;
		private boolean yZeroAtTop;
		private int oneSecondWidth = 500;
		private int msPerLineX = 100;
		private final HashMap<String, String> methodHighlightMap = new HashMap<>();

		private static class Data {
			final double value;
			final int ivalue;
			final Color chartColor;
			final Color dotColor;
			final boolean dotOnly;
			final long time; // in milliseconds
			final String name;
			final Exception stack;

			Data( double value, int ivalue, Color chartColor, Color dotColor,
				boolean dotOnly, long time, String name, Exception stack )
			{
				this.value = value;
				this.ivalue = ivalue;
				this.chartColor = chartColor;
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

		private final List<Data> syncChartData = new ArrayList<>();
		private final Map<Color, List<Data>> asyncColor2dataMap = new HashMap<>();
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

			if( TEST )
				initTestData();
		}

		void addValue( Color chartColor, double value, int ivalue, Color dotColor, boolean dotOnly, String name ) {
			if( TEST )
				return;

			List<Data> chartData = asyncColor2dataMap.computeIfAbsent( chartColor, k -> new ArrayList<>() );
			Data data = new Data( value, ivalue, chartColor, dotColor, dotOnly, System.nanoTime() / 1_000_000, name, new Exception() );
			if( asynchron )
				chartData.add( data );
			else
				syncChartData.add( data );

			lastUsedChartColor = chartColor;

			if( updateDelayed ) {
				repaintTime.stop();
				repaintTime.start();
			} else
				repaintAndRevalidate();
		}

		void clear() {
			if( TEST ) {
				repaint();
				return;
			}

			syncChartData.clear();
			asyncColor2dataMap.clear();
			lastUsedChartColor = null;

			repaint();
			revalidate();
		}

		private void repaintAndRevalidate() {
			repaint();
			revalidate();

			// scroll horizontally
			if( lastUsedChartColor != null ) {
				// compute chart width of last used color and start of last sequence
				int[] lastSeqX = new int[1];
				int cw = chartWidth( asynchron ? asyncColor2dataMap.get( lastUsedChartColor ) : syncChartData, lastSeqX );

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
				HiDPIUtils.paintAtScale1x( (Graphics2D) g2, this, this::paintAt1x );
			} finally {
				g2.dispose();
			}
		}

		private void paintAt1x( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
			FlatUIUtils.setRenderingHints( g );

			int oneSecondWidth = (int) (UIScale.scale( this.oneSecondWidth ) * scaleFactor);
			int seqGapWidth = (oneSecondWidth * NEW_SEQUENCE_GAP_MS) / 1000;
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
			for( Map.Entry<Color, List<Data>> e : asyncColor2dataMap.entrySet() ) {
				List<Data> chartData = asynchron ? e.getValue() : syncChartData;
				Color chartColor = e.getKey();
				if( FlatLaf.isLafDark() )
					chartColor = new HSLColor( chartColor ).adjustTone( 50 );
				Color temporaryValueColor = fade( chartColor, FlatLaf.isLafDark() ? 0.7f : 0.3f );
				Color dataPointColor = fade( chartColor, FlatLaf.isLafDark() ? 0.6f : 0.2f );

				// sequence start time and x coordinate
				long seqStartTime = 0;
				int seqStartX = 0;

				// "previous" data point time and x coordinate (used for "new sequence" detection)
				long ptime = Long.MIN_VALUE;
				int px = 0;

				// "line" data point x/y coordinates
				int lx = -1;
				int ly = -1;

				boolean isTemporaryValue = false;
				int lastTemporaryValueIndex = -1;

				int size = chartData.size();
				for( int i = 0; i < size; i++ ) {
					Data data = chartData.get( i );
					boolean useData = (data.chartColor == chartColor);

					// start new sequence if there is a larger time gap to previous data point
					boolean newSeq = (data.time > ptime + NEW_SEQUENCE_TIME_LAG_MS);
					ptime = data.time;

					if( newSeq ) {
						// start new sequence
						seqStartTime = data.time;
						seqStartX = (i > 0) ? px + seqGapWidth : 0;
						px = seqStartX;
						lx = -1;
						ly = -1;
						isTemporaryValue = false;
					}

					// x/y coordinates of current data point
					int dx = (int) (seqStartX + (((data.time - seqStartTime) / 1000.) * oneSecondWidth));
					int dy = (int) ((height - 1) * data.value);
					if( !yZeroAtTop )
						dy = height - 1 - dy;

					// remember x coordinate for "new sequence" detection
					px = dx;

					if( !useData )
						continue;

					// remember data point for tooltip
					lastPoints.add( new Point( dx, dy ) );
					lastDatas.add( data );

					// paint rectangle to indicate data point
					g.setColor( dataPointColor );
					g.drawRect( dx - hitOffset, dy - hitOffset, hitOffset * 2, hitOffset * 2 );

					// paint dot
					if( data.dotColor != null ) {
						int s1 = (int) Math.round( UIScale.scale( 1 ) * scaleFactor );
						int s3 = (int) Math.round( UIScale.scale( 3 ) * scaleFactor );
						g.setColor( data.dotColor );
						g.fillRect( dx - s1, dy - s1, s3, s3 );

						if( data.dotOnly )
							continue;
					}

					// start of line?
					if( lx < 0 ) {
						// remember x/y coordinates for first line
						lx = dx;
						ly = dy;
						continue;
					}

					if( isTemporaryValue && i > lastTemporaryValueIndex )
						isTemporaryValue = false;

					// draw line in sequence
					g.setColor( isTemporaryValue ? temporaryValueColor : chartColor );
					g.drawLine( lx, ly, dx, dy );

					// remember x/y coordinates for next line
					lx = dx;
					ly = dy;

					// check next data points for "temporary" value(s)
					if( temporaryValueDetection && !isTemporaryValue ) {
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
			}
		}

		private int chartWidth() {
			int width = 0;
			if( asynchron ) {
				for( List<Data> chartData : asyncColor2dataMap.values() )
					width = Math.max( width, chartWidth( chartData, null ) );
			} else
				width = Math.max( width, chartWidth( syncChartData, null ) );
			return width;
		}

		private int chartWidth( List<Data> chartData, int[] lastSeqX ) {
			long seqTime = 0;
			int seqX = 0;
			long ptime = 0;
			int px = 0;
			int oneSecondWidth = UIScale.scale( this.oneSecondWidth );
			int seqGapWidth = (oneSecondWidth * NEW_SEQUENCE_GAP_MS) / 1000;

			int size = chartData.size();
			for( int i = 0; i < size; i++ ) {
				Data data = chartData.get( i );

				if( data.time > ptime + NEW_SEQUENCE_TIME_LAG_MS ) {
					// start new sequence
					seqTime = data.time;
					seqX = (i > 0) ? px + seqGapWidth : 0;
					px = seqX;
				} else {
					// line in sequence
					int dx = (int) (seqX + (((data.time - seqTime) / 1000.) * oneSecondWidth ));
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
				buf.append( data.name );
				if( data.ivalue != Integer.MIN_VALUE )
					buf.append( ' ' ).append( data.ivalue );
				buf.append( " (" ).append( String.format( "%.3f", data.value ) ).append( ')' );
				buf.append( "</h2>" );

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
						classAndMethod.equals( "javax.swing.JComponent.paintComponent" ) ||
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

		private void initTestData() {
//			asynchron = true;

			addTestSimpleLine( Color.red, 0.0, "red" );
			addTestSimpleLine( Color.green, 0.1, "green" );
			addTestSimpleLine( Color.blue, 0.2, "blue" );
			addTestSimpleLine( Color.magenta, 0.3, "magenta" );

			addTestMiddleDotOnly( Color.red, 0.0, "red" );
			addTestMiddleDotOnly( Color.green, 0.1, "green" );
			addTestMiddleDotOnly( Color.blue, 0.2, "blue" );
			addTestMiddleDotOnly( Color.magenta, 0.3, "magenta" );

			addTestLeadingDotOnly( Color.red, 0.0, "red" );
			addTestLeadingDotOnly( Color.green, 0.1, "green" );
			addTestLeadingDotOnly( Color.blue, 0.2, "blue" );
			addTestLeadingDotOnly( Color.magenta, 0.3, "magenta" );

			addTestTrailingDotOnly( Color.red, 0.0, "red" );
			addTestTrailingDotOnly( Color.green, 0.1, "green" );
			addTestTrailingDotOnly( Color.blue, 0.2, "blue" );
			addTestTrailingDotOnly( Color.magenta, 0.3, "magenta" );

			addTestSingleData( Color.red, 0.0, "red" );
			addTestSingleData( Color.green, 0.1, "green" );
			addTestSingleData( Color.blue, 0.2, "blue" );
			addTestSingleData( Color.magenta, 0.3, "magenta" );

			temporaryValueDetection = true;
			addTestWithTemporaryValues( Color.red, 0.0, "red" );
			addTestWithTemporaryValues( Color.green, 0.1, "green" );
			addTestWithTemporaryValues( Color.blue, 0.2, "blue" );
			addTestWithTemporaryValues( Color.magenta, 0.3, "magenta" );
		}

		private void addTestSimpleLine( Color chartColor, double baseValue, String name ) {
			addTestValue(  0, chartColor, baseValue + 0.0, null, false, name );
			addTestValue( 50, chartColor, baseValue + 0.1, null, false, name );
			addTestValue( 50, chartColor, baseValue + 0.4, null, false, name );
			testTime += 1000;
		}

		private void addTestMiddleDotOnly( Color chartColor, double baseValue, String name ) {
			addTestValue(  0, chartColor, baseValue + 0.0, null, false, name );
			addTestValue( 20, chartColor, baseValue + 0.3, chartColor, true, name );
			addTestValue( 30, chartColor, baseValue + 0.1, null, false, name );
			addTestValue( 20, chartColor, baseValue + 0.05, chartColor, true, name );
			addTestValue( 30, chartColor, baseValue + 0.4, null, false, name );
			testTime += 1000;
		}

		private void addTestLeadingDotOnly( Color chartColor, double baseValue, String name ) {
			addTestValue(  0, chartColor, baseValue + 0.05, chartColor, true, name );
			addTestValue( 20, chartColor, baseValue + 0.0, null, false, name );
			addTestValue( 50, chartColor, baseValue + 0.1, null, false, name );
			addTestValue( 30, chartColor, baseValue + 0.4, null, false, name );
			testTime += 1000;
		}

		private void addTestTrailingDotOnly( Color chartColor, double baseValue, String name ) {
			addTestValue(  0, chartColor, baseValue + 0.0, null, false, name );
			addTestValue( 50, chartColor, baseValue + 0.1, null, false, name );
			addTestValue( 30, chartColor, baseValue + 0.4, null, false, name );
			addTestValue( 20, chartColor, baseValue + 0.05, chartColor, true, name );
			testTime += 1000;
		}

		private void addTestSingleData( Color chartColor, double baseValue, String name ) {
			addTestValue(  0, chartColor, baseValue + 0.15, chartColor, false, name );
			testTime += 1000;
		}

		private void addTestWithTemporaryValues( Color chartColor, double baseValue, String name ) {
			addTestValue(  0, chartColor, baseValue + 0.0, null, false, name );
			addTestValue( 50, chartColor, baseValue + 0.1, null, false, name );
			addTestValue( 5, chartColor, baseValue + 0.4, null, false, name );
			addTestValue( 5, chartColor, baseValue + 0.1, null, false, name );
			addTestValue( 40, chartColor, baseValue + 0.3, null, false, name );
			testTime += 1000;
		}

		private void addTestValue( int timeDelta, Color chartColor, double value, Color dotColor, boolean dotOnly, String name ) {
			testTime += timeDelta;

			List<Data> chartData = asyncColor2dataMap.computeIfAbsent( chartColor, k -> new ArrayList<>() );
			Data data = new Data( value, testIValue++, chartColor, dotColor, dotOnly, testTime, name, new Exception() );
			if( asynchron )
				chartData.add( data );
			else
				syncChartData.add( data );

			lastUsedChartColor = chartColor;
		}

		private int testIValue;
		private long testTime;

		//TODO remove and use ColorFunctions.fade() when merging to main
		private static Color fade( Color color, float amount ) {
			int newAlpha = Math.round( 255 * amount );
			return new Color( (color.getRGB() & 0xffffff) | (newAlpha << 24), true );
		}
	}
}
