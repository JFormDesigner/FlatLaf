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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label1 = new JLabel();
		linearScrollBar = new JScrollBar();
		JLabel label2 = new JLabel();
		easeInOutScrollBar = new JScrollBar();
		startButton = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[grow,fill]",
			// rows
			"[]" +
			"[]" +
			"[]"));

		//---- label1 ----
		label1.setText("Linear:");
		add(label1, "cell 0 0");

		//---- linearScrollBar ----
		linearScrollBar.setOrientation(Adjustable.HORIZONTAL);
		linearScrollBar.setBlockIncrement(1);
		add(linearScrollBar, "cell 1 0");

		//---- label2 ----
		label2.setText("Ease in out:");
		add(label2, "cell 0 1");

		//---- easeInOutScrollBar ----
		easeInOutScrollBar.setOrientation(Adjustable.HORIZONTAL);
		easeInOutScrollBar.setBlockIncrement(1);
		add(easeInOutScrollBar, "cell 1 1");

		//---- startButton ----
		startButton.setText("Start");
		startButton.addActionListener(e -> start());
		add(startButton, "cell 0 2");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollBar linearScrollBar;
	private JScrollBar easeInOutScrollBar;
	private JButton startButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
