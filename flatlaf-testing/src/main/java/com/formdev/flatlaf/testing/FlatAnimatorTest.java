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
import javax.swing.*;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
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
				lineChartPanel.addValue( chartColor, fraction, Integer.MIN_VALUE, "animator" );
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
		lineChartPanel = new LineChartPanel();

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
	private LineChartPanel lineChartPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class JChartColor --------------------------------------------------

	static class JChartColor
		extends JComponent
	{
		@Override
		public Dimension getPreferredSize() {
			return new Dimension( UIScale.scale( 24 ), UIScale.scale( 12 ) );
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		@Override
		protected void paintComponent( Graphics g ) {
			g.setColor( getForeground() );
			g.fillRect( 0, 0, UIScale.scale( 24 ), UIScale.scale( 12 ) );
		}
	}
}
