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
import javax.swing.border.AbstractBorder;
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
	private static final Color CHART_FADE_1 = Color.blue;
	private static final Color CHART_FADE_2 = Color.red;
	private static final Color CHART_MATERIAL_1 = Color.green;
	private static final Color CHART_MATERIAL_2 = Color.magenta;
	private static final Color CHART_MINIMAL = Color.orange;

	private static final String CHART_COLOR_KEY = "chartColor";

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatAnimatedBorderTest" );
			frame.showFrame( FlatAnimatedBorderTest::new );
		} );
	}

	FlatAnimatedBorderTest() {
		initComponents();

		fade1TextField.setBorder( new AnimatedFocusFadeBorder() );
		fade2TextField.setBorder( new AnimatedFocusFadeBorder() );

		material1TextField.setBorder( new AnimatedMaterialBorder() );
		material2TextField.setBorder( new AnimatedMaterialBorder() );

		minimalTextField.setBorder( new AnimatedMinimalTestBorder() );

		fade1TextField.putClientProperty( CHART_COLOR_KEY, CHART_FADE_1 );
		fade2TextField.putClientProperty( CHART_COLOR_KEY, CHART_FADE_2 );
		material1TextField.putClientProperty( CHART_COLOR_KEY, CHART_MATERIAL_1 );
		material2TextField.putClientProperty( CHART_COLOR_KEY, CHART_MATERIAL_2 );
		minimalTextField.putClientProperty( CHART_COLOR_KEY, CHART_MINIMAL );

		fade1ChartColor.setForeground( CHART_FADE_1 );
		fade2ChartColor.setForeground( CHART_FADE_2 );
		material1ChartColor.setForeground( CHART_MATERIAL_1 );
		material2ChartColor.setForeground( CHART_MATERIAL_2 );
		minimalChartColor.setForeground( CHART_MINIMAL );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label3 = new JLabel();
		lineChartPanel = new FlatAnimatorTest.LineChartPanel();
		fade1TextField = new JTextField();
		fade1ChartColor = new FlatAnimatorTest.JChartColor();
		fade2TextField = new JTextField();
		fade2ChartColor = new FlatAnimatorTest.JChartColor();
		label2 = new JLabel();
		material1TextField = new JTextField();
		material1ChartColor = new FlatAnimatorTest.JChartColor();
		material2TextField = new JTextField();
		material2ChartColor = new FlatAnimatorTest.JChartColor();
		label1 = new JLabel();
		minimalTextField = new JTextField();
		minimalChartColor = new FlatAnimatorTest.JChartColor();
		durationLabel = new JLabel();
		durationField = new JSpinner();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[fill]para" +
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
		add(lineChartPanel, "cell 2 0 1 10");
		add(fade1TextField, "cell 0 1");
		add(fade1ChartColor, "cell 1 1");
		add(fade2TextField, "cell 0 2");
		add(fade2ChartColor, "cell 1 2");

		//---- label2 ----
		label2.setText("Material:");
		add(label2, "cell 0 3");
		add(material1TextField, "cell 0 4");
		add(material1ChartColor, "cell 1 4");
		add(material2TextField, "cell 0 5");
		add(material2ChartColor, "cell 1 5");

		//---- label1 ----
		label1.setText("Minimal:");
		add(label1, "cell 0 6");
		add(minimalTextField, "cell 0 7");
		add(minimalChartColor, "cell 1 7");

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
	private FlatAnimatorTest.LineChartPanel lineChartPanel;
	private JTextField fade1TextField;
	private FlatAnimatorTest.JChartColor fade1ChartColor;
	private JTextField fade2TextField;
	private FlatAnimatorTest.JChartColor fade2ChartColor;
	private JLabel label2;
	private JTextField material1TextField;
	private FlatAnimatorTest.JChartColor material1ChartColor;
	private JTextField material2TextField;
	private FlatAnimatorTest.JChartColor material2ChartColor;
	private JLabel label1;
	private JTextField minimalTextField;
	private FlatAnimatorTest.JChartColor minimalChartColor;
	private JLabel durationLabel;
	private JSpinner durationField;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class AnimatedMaterialBorder ---------------------------------------

	/**
	 * Experimental text field border that:
	 * - animates focus indicator color and border width
	 */
	private class AnimatedFocusFadeBorder
		extends AbstractBorder
		implements AnimatedBorder
	{
		// needed because otherwise the empty paint method in superclass
		// javax.swing.border.AbstractBorder would be used
		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			paintWithAnimation( c, g, x, y, width, height );
		}

		@Override
		public void paintAnimated( Component c, Graphics2D g, int x, int y, int width, int height, float animatedValue ) {
			FlatUIUtils.setRenderingHints( g );

			// border width is 1 if not focused and 2 if focused
			float lw = UIScale.scale( 1 + animatedValue );

			// paint border
			Color color = ColorFunctions.mix( Color.red, Color.lightGray, animatedValue );
			FlatUIUtils.paintOutlinedComponent( g, x, y, width, height, 0, 0, 0, lw, 0,
				null, color, null );

			if( animatedValue != 0 && animatedValue != 1 ) {
				Color chartColor = (Color) ((JComponent)c).getClientProperty( CHART_COLOR_KEY );
				lineChartPanel.lineChart.addValue( animatedValue, chartColor );
			}
		}

		@Override
		public Insets getBorderInsets( Component c, Insets insets ) {
			insets.top = insets.bottom = UIScale.scale( 3 );
			insets.left = insets.right = UIScale.scale( 7 );
			return insets;
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
		extends AbstractBorder
		implements AnimatedBorder
	{
		// needed because otherwise the empty paint method in superclass
		// javax.swing.border.AbstractBorder would be used
		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			paintWithAnimation( c, g, x, y, width, height );
		}

		@Override
		public void paintAnimated( Component c, Graphics2D g, int x, int y, int width, int height, float animatedValue ) {
			FlatUIUtils.setRenderingHints( g );

			// use paintAtScale1x() for consistent line thickness when scaled
			HiDPIUtils.paintAtScale1x( g, x, y, width, height,
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

			if( animatedValue != 0 && animatedValue != 1 ) {
				Color chartColor = (Color) ((JComponent)c).getClientProperty( CHART_COLOR_KEY );
				lineChartPanel.lineChart.addValue( animatedValue, chartColor );
			}
		}

		@Override
		public void repaintDuringAnimation( Component c, int x, int y, int width, int height ) {
			// limit repaint to bottom border
			int lh = UIScale.scale( 2 );
			c.repaint( x, y + height - lh, width, lh );
		}

		@Override
		public Insets getBorderInsets( Component c, Insets insets ) {
			insets.top = insets.bottom = UIScale.scale( 3 );
			insets.left = insets.right = UIScale.scale( 7 );
			return insets;
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
		public void paintAnimated( Component c, Graphics2D g, int x, int y, int width, int height, float animatedValue ) {
			int lh = UIScale.scale( 2 );

			g.setColor( Color.blue );
			g.fillRect( x, y + height - lh, Math.round( width * animatedValue ), lh );

			if( animatedValue != 0 && animatedValue != 1 ) {
				Color chartColor = (Color) ((JComponent)c).getClientProperty( CHART_COLOR_KEY );
				lineChartPanel.lineChart.addValue( animatedValue, chartColor );
			}
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
			return UIScale.scale( new Insets( 3, 7, 3, 7 ) );
		}

		@Override
		public boolean isBorderOpaque() {
			return false;
		}
	}
}
