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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.testing.FlatSmoothScrollingTest.LineChartPanel;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatAnimatorTest
	extends FlatTestPanel
{
	private Animator linearAnimator;
	private Animator easeInOutAnimator;

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatAnimatorTest" );
			frame.showFrame( FlatAnimatorTest::new );
		} );
	}

	FlatAnimatorTest() {
		initComponents();

		updateChartDelayedChanged();

		lineChartPanel.setSecondWidth( 500 );
		mouseWheelTestPanel.lineChartPanel = lineChartPanel;
	}

	private void start() {
		startLinear();
		startEaseInOut();
	}

	private void startLinear() {
		if( linearAnimator != null ) {
			linearAnimator.stop();
			linearAnimator.start();
		} else {
			linearAnimator = new Animator( 1000, fraction -> {
				linearScrollBar.setValue( Math.round( fraction * linearScrollBar.getMaximum() ) );
			} );
			linearAnimator.start();
		}
	}

	private void startEaseInOut() {
		if( easeInOutAnimator != null ) {
			easeInOutAnimator.stop();
			easeInOutAnimator.start();
		} else {
			easeInOutAnimator = new Animator( 1000, fraction -> {
				easeInOutScrollBar.setValue( Math.round( fraction * easeInOutScrollBar.getMaximum() ) );
			} );
			easeInOutAnimator.setInterpolator( CubicBezierEasing.EASE_IN_OUT );
			easeInOutAnimator.start();
		}
	}

	private void updateChartDelayedChanged() {
		lineChartPanel.setUpdateDelayed( updateChartDelayedCheckBox.isSelected() );
	}

	private void clearChart() {
		lineChartPanel.clear();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel linearLabel = new JLabel();
		linearScrollBar = new JScrollBar();
		JLabel easeInOutLabel = new JLabel();
		easeInOutScrollBar = new JScrollBar();
		startButton = new JButton();
		JLabel mouseWheelTestLabel = new JLabel();
		mouseWheelTestPanel = new FlatAnimatorTest.MouseWheelTestPanel();
		JScrollPane lineChartScrollPane = new JScrollPane();
		lineChartPanel = new FlatSmoothScrollingTest.LineChartPanel();
		JLabel lineChartInfoLabel = new JLabel();
		updateChartDelayedCheckBox = new JCheckBox();
		JButton clearChartButton = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[grow,fill]",
			// rows
			"[]" +
			"[]" +
			"[]para" +
			"[top]" +
			"[400,grow,fill]" +
			"[]"));

		//---- linearLabel ----
		linearLabel.setText("Linear:");
		add(linearLabel, "cell 0 0");

		//---- linearScrollBar ----
		linearScrollBar.setOrientation(Adjustable.HORIZONTAL);
		linearScrollBar.setBlockIncrement(1);
		add(linearScrollBar, "cell 1 0");

		//---- easeInOutLabel ----
		easeInOutLabel.setText("Ease in out:");
		add(easeInOutLabel, "cell 0 1");

		//---- easeInOutScrollBar ----
		easeInOutScrollBar.setOrientation(Adjustable.HORIZONTAL);
		easeInOutScrollBar.setBlockIncrement(1);
		add(easeInOutScrollBar, "cell 1 1");

		//---- startButton ----
		startButton.setText("Start");
		startButton.addActionListener(e -> start());
		add(startButton, "cell 0 2");

		//---- mouseWheelTestLabel ----
		mouseWheelTestLabel.setText("Mouse wheel test:");
		add(mouseWheelTestLabel, "cell 0 3");

		//---- mouseWheelTestPanel ----
		mouseWheelTestPanel.setBorder(new LineBorder(Color.red));
		add(mouseWheelTestPanel, "cell 1 3,height 100");

		//======== lineChartScrollPane ========
		{
			lineChartScrollPane.putClientProperty("JScrollPane.smoothScrolling", false);
			lineChartScrollPane.setViewportView(lineChartPanel);
		}
		add(lineChartScrollPane, "cell 0 4 2 1");

		//---- lineChartInfoLabel ----
		lineChartInfoLabel.setText("X: time (500ms per line) / Y: value (10% per line)");
		add(lineChartInfoLabel, "cell 0 5 2 1");

		//---- updateChartDelayedCheckBox ----
		updateChartDelayedCheckBox.setText("Update chart delayed");
		updateChartDelayedCheckBox.setMnemonic('U');
		updateChartDelayedCheckBox.addActionListener(e -> updateChartDelayedChanged());
		add(updateChartDelayedCheckBox, "cell 0 5 2 1,alignx right,growx 0");

		//---- clearChartButton ----
		clearChartButton.setText("Clear Chart");
		clearChartButton.setMnemonic('C');
		clearChartButton.addActionListener(e -> clearChart());
		add(clearChartButton, "cell 0 5 2 1,alignx right,growx 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollBar linearScrollBar;
	private JScrollBar easeInOutScrollBar;
	private JButton startButton;
	private FlatAnimatorTest.MouseWheelTestPanel mouseWheelTestPanel;
	private FlatSmoothScrollingTest.LineChartPanel lineChartPanel;
	private JCheckBox updateChartDelayedCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class MouseWheelTestPanel ------------------------------------------

	static class MouseWheelTestPanel
		extends JPanel
		implements MouseWheelListener
	{
		private static final int MAX_VALUE = 1000;
		private static final int STEP = 100;

		private final JLabel valueLabel;
		private final Animator animator;

		LineChartPanel lineChartPanel;

		private int value;
		private int startValue;
		private int targetValue = -1;

		MouseWheelTestPanel() {
			super( new BorderLayout() );
			valueLabel = new JLabel( String.valueOf( value ), SwingConstants.CENTER );
			valueLabel.setFont( valueLabel.getFont().deriveFont( (float) valueLabel.getFont().getSize() * 2 ) );
			add( valueLabel, BorderLayout.CENTER );
			add( new JLabel( " " ), BorderLayout.NORTH );
			add( new JLabel( "(move mouse into rectangle and rotate mouse wheel)", SwingConstants.CENTER ), BorderLayout.SOUTH );

			int duration = FlatUIUtils.getUIInt( "ScrollPane.smoothScrolling.duration", 200 );
			int resolution = FlatUIUtils.getUIInt( "ScrollPane.smoothScrolling.resolution", 10 );

			animator = new Animator( duration, fraction -> {
				value = startValue + Math.round( (targetValue - startValue) * fraction );
				valueLabel.setText( String.valueOf( value ) );

				lineChartPanel.addValue( value / (double) MAX_VALUE, Color.red );
			}, () -> {
				targetValue = -1;
			} );
			animator.setResolution( resolution );
			animator.setInterpolator( new CubicBezierEasing( 0.5f, 0.5f, 0.5f, 1 ) );

			addMouseWheelListener( this );
		}

		@Override
		public void mouseWheelMoved( MouseWheelEvent e ) {
			double preciseWheelRotation = e.getPreciseWheelRotation();

			// add a dot in the middle of the chart for the wheel rotation
			// for unprecise wheels the rotation value is usually -1 or +1
			// for precise wheels the rotation value is in range ca. -10 to +10,
			// depending how fast the wheel is rotated
			lineChartPanel.addValue( 0.5 + (preciseWheelRotation / 20.), true, Color.red );

			// increase/decrease target value if animation is in progress
			int newValue = (int) ((targetValue < 0 ? value : targetValue) + (STEP * preciseWheelRotation));
			newValue = Math.min( Math.max( newValue, 0 ), MAX_VALUE );

			if( preciseWheelRotation != 0 &&
				preciseWheelRotation != e.getWheelRotation() )
			{
				// do not use animation for precise scrolling (e.g. with trackpad)

				// stop running animation (if any)
				animator.stop();

				value = newValue;
				valueLabel.setText( String.valueOf( value ) );

				lineChartPanel.addValue( value / (double) MAX_VALUE, Color.red );
				return;
			}

			// start next animation at the current value
			startValue = value;
			targetValue = newValue;

			// restart animator
			animator.cancel();
			animator.start();
		}
	}
}
