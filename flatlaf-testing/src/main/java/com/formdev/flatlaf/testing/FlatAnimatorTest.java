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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
import com.formdev.flatlaf.util.HSLColor;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.formdev.flatlaf.util.Animator.Interpolator;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatAnimatorTest
	extends FlatTestPanel
{
	private static final Color CHART_LINEAR = Color.blue;
	private static final Color CHART_EASE_IN_OUT = Color.magenta;
	private static final Color CHART_STANDARD_EASING = Color.red;

	private Animator linearAnimator;
	private Animator easeInOutAnimator;
	private Animator standardEasingAnimator;

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatAnimatorTest" );
			frame.showFrame( FlatAnimatorTest::new );
		} );
	}

	FlatAnimatorTest() {
		initComponents();

		linearChartColor.setForeground( CHART_LINEAR );
		easeInOutChartColor.setForeground( CHART_EASE_IN_OUT );
		standardEasingChartColor.setForeground( CHART_STANDARD_EASING );
	}

	private void start() {
		linearAnimator = start( linearAnimator, null, linearScrollBar, CHART_LINEAR );
		easeInOutAnimator = start( easeInOutAnimator, CubicBezierEasing.EASE_IN_OUT, easeInOutScrollBar, CHART_EASE_IN_OUT );
		standardEasingAnimator = start( standardEasingAnimator, CubicBezierEasing.STANDARD_EASING, standardEasingScrollBar, CHART_STANDARD_EASING );
	}

	private Animator start( Animator animator, Interpolator interpolator, JScrollBar scrollBar, Color chartColor ) {
		if( animator != null ) {
			animator.stop();
			animator.start();
		} else {
			animator = new Animator( 1000, fraction -> {
				scrollBar.setValue( Math.round( fraction * scrollBar.getMaximum() ) );
				lineChartPanel.lineChart.addValue( fraction, chartColor );
			} );
			animator.setInterpolator( interpolator );
			animator.start();
		}
		return animator;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		linearLabel = new JLabel();
		linearChartColor = new FlatAnimatorTest.JChartColor();
		linearScrollBar = new JScrollBar();
		easeInOutLabel = new JLabel();
		easeInOutChartColor = new FlatAnimatorTest.JChartColor();
		easeInOutScrollBar = new JScrollBar();
		standardEasingLabel = new JLabel();
		standardEasingChartColor = new FlatAnimatorTest.JChartColor();
		standardEasingScrollBar = new JScrollBar();
		startButton = new JButton();
		lineChartPanel = new FlatAnimatorTest.LineChartPanel();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[fill]" +
			"[grow,fill]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[]para" +
			"[400,grow,fill]"));

		//---- linearLabel ----
		linearLabel.setText("Linear:");
		add(linearLabel, "cell 0 0");
		add(linearChartColor, "cell 1 0");

		//---- linearScrollBar ----
		linearScrollBar.setOrientation(Adjustable.HORIZONTAL);
		linearScrollBar.setBlockIncrement(1);
		add(linearScrollBar, "cell 2 0");

		//---- easeInOutLabel ----
		easeInOutLabel.setText("Ease in out:");
		add(easeInOutLabel, "cell 0 1");
		add(easeInOutChartColor, "cell 1 1");

		//---- easeInOutScrollBar ----
		easeInOutScrollBar.setOrientation(Adjustable.HORIZONTAL);
		easeInOutScrollBar.setBlockIncrement(1);
		add(easeInOutScrollBar, "cell 2 1");

		//---- standardEasingLabel ----
		standardEasingLabel.setText("Standard easing:");
		add(standardEasingLabel, "cell 0 2");
		add(standardEasingChartColor, "cell 1 2");

		//---- standardEasingScrollBar ----
		standardEasingScrollBar.setOrientation(Adjustable.HORIZONTAL);
		standardEasingScrollBar.setBlockIncrement(1);
		add(standardEasingScrollBar, "cell 2 2");

		//---- startButton ----
		startButton.setText("Start");
		startButton.addActionListener(e -> start());
		add(startButton, "cell 0 3");
		add(lineChartPanel, "cell 0 4 3 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel linearLabel;
	private FlatAnimatorTest.JChartColor linearChartColor;
	private JScrollBar linearScrollBar;
	private JLabel easeInOutLabel;
	private FlatAnimatorTest.JChartColor easeInOutChartColor;
	private JScrollBar easeInOutScrollBar;
	private JLabel standardEasingLabel;
	private FlatAnimatorTest.JChartColor standardEasingChartColor;
	private JScrollBar standardEasingScrollBar;
	private JButton startButton;
	private FlatAnimatorTest.LineChartPanel lineChartPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class LineChartPanel -----------------------------------------------

	static class LineChartPanel
		extends JPanel
	{
		LineChartPanel() {
			initComponents();

			updateChartDelayedChanged();
			lineChart.setSecondWidth( 500 );
		}

		private void updateChartDelayedChanged() {
			lineChart.setUpdateDelayed( updateChartDelayedCheckBox.isSelected() );
		}

		private void clearChart() {
			lineChart.clear();
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			JScrollPane lineChartScrollPane = new JScrollPane();
			lineChart = new FlatAnimatorTest.LineChart();
			JLabel lineChartInfoLabel = new JLabel();
			updateChartDelayedCheckBox = new JCheckBox();
			JButton clearChartButton = new JButton();

			//======== this ========
			setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3",
				// columns
				"[fill]" +
				"[grow,fill]",
				// rows
				"[400,grow,fill]" +
				"[]"));

			//======== lineChartScrollPane ========
			{
				lineChartScrollPane.putClientProperty("JScrollPane.smoothScrolling", false);
				lineChartScrollPane.setViewportView(lineChart);
			}
			add(lineChartScrollPane, "cell 0 0 2 1");

			//---- lineChartInfoLabel ----
			lineChartInfoLabel.setText("X: time (500ms per line) / Y: value (10% per line)");
			add(lineChartInfoLabel, "cell 0 1 2 1");

			//---- updateChartDelayedCheckBox ----
			updateChartDelayedCheckBox.setText("Update chart delayed");
			updateChartDelayedCheckBox.setMnemonic('U');
			updateChartDelayedCheckBox.addActionListener(e -> updateChartDelayedChanged());
			add(updateChartDelayedCheckBox, "cell 0 1 2 1,alignx right,growx 0");

			//---- clearChartButton ----
			clearChartButton.setText("Clear Chart");
			clearChartButton.setMnemonic('C');
			clearChartButton.addActionListener(e -> clearChart());
			add(clearChartButton, "cell 0 1 2 1,alignx right,growx 0");
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		FlatAnimatorTest.LineChart lineChart;
		private JCheckBox updateChartDelayedCheckBox;
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}

	//---- class LineChart ----------------------------------------------------

	static class LineChart
		extends JComponent
		implements Scrollable
	{
		private static final int NEW_SEQUENCE_TIME_LAG = 500;
		private static final int NEW_SEQUENCE_GAP = 50;

		private int secondWidth = 1000;

		private static class Data {
			final double value;
			final boolean dot;
			final long time; // in milliseconds

			Data( double value, boolean dot, long time ) {
				this.value = value;
				this.dot = dot;
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

		LineChart() {
			repaintTime = new Timer( 20, e -> repaintAndRevalidate() );
			repaintTime.setRepeats( false );
		}

		void addValue( double value, Color chartColor ) {
			addValue( value, false, chartColor );
		}

		void addValue( double value, boolean dot, Color chartColor ) {
			List<Data> chartData = color2dataMap.computeIfAbsent( chartColor, k -> new ArrayList<>() );
			chartData.add( new Data( value, dot, System.nanoTime() / 1000000) );

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

		void setSecondWidth( int secondWidth ) {
			this.secondWidth = secondWidth;
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

			int secondWidth = (int) (this.secondWidth * scaleFactor);
			int seqGapWidth = (int) (NEW_SEQUENCE_GAP * scaleFactor);

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
			int twoHundredMillisWidth = secondWidth / 5;
			for( int i = twoHundredMillisWidth; i < width; i += twoHundredMillisWidth ) {
				g.setColor( (i % secondWidth != 0) ? lineColor : lineColor2 );
				g.drawLine( i, 0, i, height );
			}

			// paint lines
			for( Map.Entry<Color, List<Data>> e : color2dataMap.entrySet() ) {
				List<Data> chartData = e.getValue();
				Color chartColor = e.getKey();
				if( FlatLaf.isLafDark() )
					chartColor = new HSLColor( chartColor ).adjustTone( 50 );
				Color temporaryValueColor = new Color( (chartColor.getRGB() & 0xffffff) | 0x40000000, true );

				long seqTime = 0;
				int seqX = 0;
				long ptime = 0;
				int px = 0;
				int py = 0;
				int pcount = 0;

				g.setColor( chartColor );

				boolean first = true;
				int size = chartData.size();
				for( int i = 0; i < size; i++ ) {
					Data data = chartData.get( i );
					int dy = (int) ((height - 1) * data.value);

					if( data.dot ) {
						int dotx = px;
						if( i > 0 && data.time > ptime + NEW_SEQUENCE_TIME_LAG )
							dotx += seqGapWidth;
						int o = UIScale.scale( 1 );
						int s = UIScale.scale( 3 );
						g.fillRect( dotx - o, dy - o, s, s );
						continue;
					}

					if( data.time > ptime + NEW_SEQUENCE_TIME_LAG ) {
						if( !first && pcount == 0 )
							g.drawLine( px, py, px + (int) (4 * scaleFactor), py );

						// start new sequence
						seqTime = data.time;
						seqX = !first ? px + seqGapWidth : 0;
						px = seqX;
						pcount = 0;
						first = false;
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

		/**
		 * One or two values between two equal values are considered "temporary",
		 * which means that they are the target value for the following scroll animation.
		 */
		private boolean isTemporaryValue( List<Data> chartData, int i ) {
			if( i == 0 || i == chartData.size() - 1 )
				return false;

			Data dataBefore = chartData.get( i - 1 );
			Data dataAfter = chartData.get( i + 1 );

			if( dataBefore.dot || dataAfter.dot )
				return false;

			double valueBefore = dataBefore.value;
			double valueAfter = dataAfter.value;

			return valueBefore == valueAfter ||
				(i < chartData.size() - 2 && valueBefore == chartData.get( i + 2 ).value) ||
				(i > 1 && chartData.get( i - 2 ).value == valueAfter);
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
					int dx = (int) (seqX + (((data.time - seqTime) / 1000.) * secondWidth));
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
			return secondWidth;
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

	//---- class JChartColor --------------------------------------------------

	static class JChartColor
		extends JComponent
	{
		@Override
		public Dimension getPreferredSize() {
			return new Dimension( UIScale.scale( 24 ), UIScale.scale( 12 ) );
		}

		@Override
		protected void paintComponent( Graphics g ) {
			g.setColor( getForeground() );
			g.fillRect( 0, 0, UIScale.scale( 24 ), UIScale.scale( 12 ) );
		}
	}
}
