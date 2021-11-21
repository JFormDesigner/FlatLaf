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
import java.awt.FontMetrics;
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
	private static final Color CHART_MATERIAL_3 = Color.pink;
	private static final Color CHART_MATERIAL_4 = Color.cyan;
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
		material3TextField.setBorder( new AnimatedMaterialLabeledBorder() );
		material4TextField.setBorder( new AnimatedMaterialLabeledBorder() );

		minimalTextField.setBorder( new AnimatedMinimalTestBorder() );

		fade1TextField.putClientProperty( CHART_COLOR_KEY, CHART_FADE_1 );
		fade2TextField.putClientProperty( CHART_COLOR_KEY, CHART_FADE_2 );
		material1TextField.putClientProperty( CHART_COLOR_KEY, CHART_MATERIAL_1 );
		material2TextField.putClientProperty( CHART_COLOR_KEY, CHART_MATERIAL_2 );
		material3TextField.putClientProperty( CHART_COLOR_KEY, CHART_MATERIAL_3 );
		material4TextField.putClientProperty( CHART_COLOR_KEY, CHART_MATERIAL_4 );
		minimalTextField.putClientProperty( CHART_COLOR_KEY, CHART_MINIMAL );

		fade1ChartColor.setForeground( CHART_FADE_1 );
		fade2ChartColor.setForeground( CHART_FADE_2 );
		material1ChartColor.setForeground( CHART_MATERIAL_1 );
		material2ChartColor.setForeground( CHART_MATERIAL_2 );
		material3ChartColor.setForeground( CHART_MATERIAL_3 );
		material4ChartColor.setForeground( CHART_MATERIAL_4 );
		minimalChartColor.setForeground( CHART_MINIMAL );

		material3TextField.putClientProperty( AnimatedMaterialLabeledBorder.LABEL_TEXT_KEY, "Label" );
		material4TextField.putClientProperty( AnimatedMaterialLabeledBorder.LABEL_TEXT_KEY, "Label" );
		material4TextField.setText( "Text" );
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
		material3TextField = new JTextField();
		material3ChartColor = new FlatAnimatorTest.JChartColor();
		material4TextField = new JTextField();
		material4ChartColor = new FlatAnimatorTest.JChartColor();
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
		add(lineChartPanel, "cell 2 0 1 12");
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

		//---- material3TextField ----
		material3TextField.putClientProperty("FlatLaf.styleClass", "large");
		add(material3TextField, "cell 0 6");
		add(material3ChartColor, "cell 1 6");

		//---- material4TextField ----
		material4TextField.putClientProperty("FlatLaf.styleClass", "large");
		add(material4TextField, "cell 0 7");
		add(material4ChartColor, "cell 1 7");

		//---- label1 ----
		label1.setText("Minimal:");
		add(label1, "cell 0 8");
		add(minimalTextField, "cell 0 9");
		add(minimalChartColor, "cell 1 9");

		//---- durationLabel ----
		durationLabel.setText("Duration:");
		add(durationLabel, "cell 0 11");

		//---- durationField ----
		durationField.setModel(new SpinnerNumberModel(200, 100, null, 50));
		add(durationField, "cell 0 11");
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
	private JTextField material3TextField;
	private FlatAnimatorTest.JChartColor material3ChartColor;
	private JTextField material4TextField;
	private FlatAnimatorTest.JChartColor material4ChartColor;
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

	//---- class AnimatedMaterialLabeledBorder --------------------------------

	/**
	 * Experimental text field border that:
	 * - paints a label above the text, or in center if text field is empty
	 * - paint border only at bottom
	 * - animates focus indicator at bottom
	 */
	private class AnimatedMaterialLabeledBorder
		extends AnimatedMaterialBorder
	{
		static final String LABEL_TEXT_KEY = "JTextField.labelText";

		private static final float LABEL_FONT_SCALE = 0.75f;

		@Override
		public void paintAnimated( Component c, Graphics2D g, int x, int y, int width, int height, float animatedValue ) {
			super.paintAnimated( c, g, x, y, width, height, animatedValue );

			JComponent jc = (JComponent) c;
			String label = (String) jc.getClientProperty( LABEL_TEXT_KEY );
			if( label == null )
				return;

			FontMetrics fm = c.getFontMetrics( c.getFont() );
			int labelFontHeight = Math.round( fm.getHeight() * LABEL_FONT_SCALE );

			int tx = UIScale.scale( 7 );
			int ty = y + labelFontHeight - UIScale.scale( 2 );
			float sf = LABEL_FONT_SCALE;

			if( ((JTextField)c).getDocument().getLength() == 0 ) {
				// paint label in center of text field if it is empty
				int ty2 = ((height - fm.getHeight()) / 2) + labelFontHeight;
				ty += (ty2 - ty) * (1 - animatedValue);
				sf += (1 - LABEL_FONT_SCALE) * (1 - animatedValue);
			}

			Graphics2D g2 = (Graphics2D) g.create();
			try {
				g2.translate( tx, ty );
				g2.scale( sf, sf );
				g2.setColor( ColorFunctions.mix( Color.red, Color.gray, animatedValue ) );

				FlatUIUtils.drawString( jc, g2, label, 0, 0 );
			} finally {
				g2.dispose();
			}
		}

		@Override
		public void repaintDuringAnimation( Component c, int x, int y, int width, int height ) {
			c.repaint( x, y, width, height );
		}

		@Override
		public Insets getBorderInsets( Component c, Insets insets ) {
			super.getBorderInsets( c, insets );

			FontMetrics fm = c.getFontMetrics( c.getFont() );
			int labelFontHeight = Math.round( fm.getHeight() * LABEL_FONT_SCALE );
			insets.top = labelFontHeight;
			insets.bottom = UIScale.scale( 5 );
			return insets;
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
