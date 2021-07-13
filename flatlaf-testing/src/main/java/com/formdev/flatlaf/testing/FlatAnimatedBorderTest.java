/*
 * Copyright 2021 FormDev Software GmbH
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import com.formdev.flatlaf.ui.FlatMarginBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.AnimatedBorder;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatAnimatedBorderTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatAnimatedBorderTest" );
			frame.showFrame( FlatAnimatedBorderTest::new );
		} );
	}

	FlatAnimatedBorderTest() {
		initComponents();

		textField5.setBorder( new AnimatedFocusFadeBorder() );
		textField6.setBorder( new AnimatedFocusFadeBorder() );

		textField1.setBorder( new AnimatedMaterialBorder() );
		textField2.setBorder( new AnimatedMaterialBorder() );

		textField4.setBorder( new AnimatedMinimalTestBorder() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label3 = new JLabel();
		textField5 = new JTextField();
		textField6 = new JTextField();
		label2 = new JLabel();
		textField1 = new JTextField();
		textField2 = new JTextField();
		label1 = new JLabel();
		textField4 = new JTextField();
		durationLabel = new JLabel();
		durationField = new JSpinner();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]",
			// rows
			"[]" +
			"[]" +
			"[]para" +
			"[]" +
			"[]" +
			"[]para" +
			"[]" +
			"[]" +
			"[grow]" +
			"[]"));

		//---- label3 ----
		label3.setText("Fade:");
		add(label3, "cell 0 0");
		add(textField5, "cell 0 1");
		add(textField6, "cell 0 2");

		//---- label2 ----
		label2.setText("Material:");
		add(label2, "cell 0 3");
		add(textField1, "cell 0 4");
		add(textField2, "cell 0 5");

		//---- label1 ----
		label1.setText("Minimal:");
		add(label1, "cell 0 6");
		add(textField4, "cell 0 7");

		//---- durationLabel ----
		durationLabel.setText("Duration:");
		add(durationLabel, "cell 0 9");

		//---- durationField ----
		durationField.setModel(new SpinnerNumberModel(200, 100, null, 50));
		add(durationField, "cell 0 9");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label3;
	private JTextField textField5;
	private JTextField textField6;
	private JLabel label2;
	private JTextField textField1;
	private JTextField textField2;
	private JLabel label1;
	private JTextField textField4;
	private JLabel durationLabel;
	private JSpinner durationField;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class AnimatedMaterialBorder ---------------------------------------

	/**
	 * Experimental text field border that:
	 * - animates focus indicator color and border width
	 */
	private class AnimatedFocusFadeBorder
		extends FlatMarginBorder
		implements AnimatedBorder
	{
		// needed because otherwise the empty paint method in superclass
		// javax.swing.border.AbstractBorder would be used
		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			AnimationSupport.paintBorder( this, c, g, x, y, width, height );
		}

		@Override
		public void paintBorderAnimated( Component c, Graphics g, int x, int y, int width, int height, float animatedValue ) {
			FlatUIUtils.setRenderingHints( g );

			// border width is 1 if not focused and 2 if focused
			float lw = UIScale.scale( 1 + animatedValue );

			// paint border
			g.setColor( ColorFunctions.mix( Color.red, Color.lightGray, animatedValue ) );
			FlatUIUtils.paintComponentBorder( (Graphics2D) g, x, y, width, height, 0, lw, 0 );
		}

		@Override
		public float getValue( Component c ) {
			return FlatUIUtils.isPermanentFocusOwner( c ) ? 1 : 0;
		}

		@Override
		public int getAnimationDuration() {
			return (Integer) durationField.getValue();
		}
	}

	//---- class AnimatedMaterialBorder ---------------------------------------

	/**
	 * Experimental text field border that:
	 * - paint border only at bottom
	 * - animates focus indicator at bottom
	 */
	private class AnimatedMaterialBorder
		extends FlatMarginBorder
		implements AnimatedBorder
	{
		// needed because otherwise the empty paint method in superclass
		// javax.swing.border.AbstractBorder would be used
		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			AnimationSupport.paintBorder( this, c, g, x, y, width, height );
		}

		@Override
		public void paintBorderAnimated( Component c, Graphics g, int x, int y, int width, int height, float animatedValue ) {
			FlatUIUtils.setRenderingHints( g );

			// use paintAtScale1x() for consistent line thickness when scaled
			HiDPIUtils.paintAtScale1x( (Graphics2D) g, x, y, width, height,
				(g2d, x2, y2, width2, height2, scaleFactor) -> {
					float lh = (float) (UIScale.scale( 1f ) * scaleFactor);

					g2d.setColor( Color.gray );
					g2d.fill( new Rectangle2D.Float( x2, y2 + height2 - lh, width2, lh ) );

					if( animatedValue > 0 ) {
						lh = (float) (UIScale.scale( 2f ) * scaleFactor);
						int lw = Math.round( width2 * animatedValue );

						g2d.setColor( Color.red );
						g2d.fill( new Rectangle2D.Float( x2 + (width2 - lw) / 2, y2 + height2 - lh, lw, lh ) );
					}
				} );
		}

		@Override
		public float getValue( Component c ) {
			return FlatUIUtils.isPermanentFocusOwner( c ) ? 1 : 0;
		}

		@Override
		public int getAnimationDuration() {
			return (Integer) durationField.getValue();
		}
	}

	//---- class AnimatedMinimalTestBorder ------------------------------------

	/**
	 * Minimal example for an animated border.
	 */
	private class AnimatedMinimalTestBorder
		implements AnimatedBorder
	{
		@Override
		public void paintBorderAnimated( Component c, Graphics g, int x, int y, int width, int height, float animatedValue ) {
			int lh = UIScale.scale( 2 );

			g.setColor( Color.blue );
			g.fillRect( x, y + height - lh, Math.round( width * animatedValue ), lh );
		}

		@Override
		public float getValue( Component c ) {
			return FlatUIUtils.isPermanentFocusOwner( c ) ? 1 : 0;
		}

		@Override
		public int getAnimationDuration() {
			return (Integer) durationField.getValue();
		}

		@Override
		public Insets getBorderInsets( Component c ) {
			return UIScale.scale( new Insets( 4, 4, 4, 4 ) );
		}

		@Override
		public boolean isBorderOpaque() {
			return false;
		}
	}
}
