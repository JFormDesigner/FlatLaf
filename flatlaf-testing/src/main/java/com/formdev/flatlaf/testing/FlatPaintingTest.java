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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatPaintingTest
	extends JScrollPane
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatPaintingTest" );
			frame.showFrame( FlatPaintingTest::new );
		} );
	}

	FlatPaintingTest() {
		initComponents();

		Hashtable<Integer, JLabel> labels = new Hashtable<>();
		for( int i = 0; i <= 5; i++ )
			labels.put( i * 10, new JLabel( Integer.toString( i ) ) );
		focusInnerWidthSlider.setLabelTable( labels );
	}

	@Override
	public void updateUI() {
		super.updateUI();

		getHorizontalScrollBar().setUnitIncrement( UIScale.scale( 25 ) );
		getVerticalScrollBar().setUnitIncrement( UIScale.scale( 25 ) );
	}

	private void focusWidthFractionChanged() {
		float focusWidthFraction = focusWidthFractionSlider.getValue() / 100f;

		FlatTestFrame.updateComponentsRecur( (Container) getViewport().getView(), (c, type) -> {
			if( c instanceof BorderPainter )
				((BorderPainter)c).focusWidthFraction = focusWidthFraction;
		} );

		repaint();
	}

	private void focusInnerWidthChanged() {
		float focusInnerWidth = focusInnerWidthSlider.getValue() / 10f;

		FlatTestFrame.updateComponentsRecur( (Container) getViewport().getView(), (c, type) -> {
			if( c instanceof BorderPainter )
				((BorderPainter)c).focusInnerWidth = focusInnerWidth;
		} );

		repaint();
	}

	private void translucentChanged() {
		boolean translucent = translucentCheckBox.isSelected();

		FlatTestFrame.updateComponentsRecur( (Container) getViewport().getView(), (c, type) -> {
			if( c instanceof BorderPainter )
				((BorderPainter)c).translucent = translucent;
		} );

		repaint();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		FlatTestPanel flatTestPanel1 = new FlatTestPanel();
		FlatPaintingTest.BorderPainter borderPainter9 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter1 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter6 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter13 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter25 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter17 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter21 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter29 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter10 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter2 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter7 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter14 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter30 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter18 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter22 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter28 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter11 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter3 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter5 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter15 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter31 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter19 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter23 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter27 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter12 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter4 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter8 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter16 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter32 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter20 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter24 = new FlatPaintingTest.BorderPainter();
		FlatPaintingTest.BorderPainter borderPainter26 = new FlatPaintingTest.BorderPainter();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JLabel label3 = new JLabel();
		JLabel label4 = new JLabel();
		JLabel label8 = new JLabel();
		JLabel label5 = new JLabel();
		JLabel label6 = new JLabel();
		JLabel label7 = new JLabel();
		JPanel panel6 = new JPanel();
		JPanel panel7 = new JPanel();
		JLabel focusWidthFractionLabel = new JLabel();
		focusWidthFractionSlider = new JSlider();
		JLabel focusInnerWidthLabel = new JLabel();
		focusInnerWidthSlider = new JSlider();
		translucentCheckBox = new JCheckBox();

		//======== this ========
		setBorder(null);

		//======== flatTestPanel1 ========
		{
			flatTestPanel1.setLayout(new MigLayout(
				"ltr,insets dialog,hidemode 3",
				// columns
				"[fill]" +
				"[fill]" +
				"[fill]" +
				"[fill]" +
				"[fill]" +
				"[fill]" +
				"[fill]" +
				"[fill]" +
				"[fill]",
				// rows
				"[top]" +
				"[top]" +
				"[top]" +
				"[top]" +
				"[]para" +
				"[]"));

			//---- borderPainter9 ----
			borderPainter9.setScale(8.0F);
			borderPainter9.setPaintBorder(false);
			borderPainter9.setPaintFocus(false);
			borderPainter9.setFocusWidth(0);
			flatTestPanel1.add(borderPainter9, "cell 0 0");

			//---- borderPainter1 ----
			borderPainter1.setScale(8.0F);
			borderPainter1.setPaintBorder(false);
			borderPainter1.setPaintFocus(false);
			flatTestPanel1.add(borderPainter1, "cell 1 0");

			//---- borderPainter6 ----
			borderPainter6.setScale(8.0F);
			borderPainter6.setPaintBorder(false);
			borderPainter6.setPaintFocus(false);
			borderPainter6.setW(25);
			borderPainter6.setArc(10);
			flatTestPanel1.add(borderPainter6, "cell 2 0");

			//---- borderPainter13 ----
			borderPainter13.setScale(8.0F);
			borderPainter13.setPaintBorder(false);
			borderPainter13.setPaintFocus(false);
			borderPainter13.setW(25);
			borderPainter13.setArc(20);
			flatTestPanel1.add(borderPainter13, "cell 3 0");

			//---- borderPainter25 ----
			borderPainter25.setScale(8.0F);
			borderPainter25.setPaintBorder(false);
			borderPainter25.setPaintFocus(false);
			borderPainter25.setArc(20);
			flatTestPanel1.add(borderPainter25, "cell 4 0");

			//---- borderPainter17 ----
			borderPainter17.setScale(8.0F);
			borderPainter17.setPaintBorder(false);
			borderPainter17.setPaintFocus(false);
			borderPainter17.setFocusWidth(0);
			borderPainter17.setArc(0);
			flatTestPanel1.add(borderPainter17, "cell 5 0");

			//---- borderPainter21 ----
			borderPainter21.setScale(8.0F);
			borderPainter21.setPaintBorder(false);
			borderPainter21.setPaintFocus(false);
			borderPainter21.setArc(0);
			flatTestPanel1.add(borderPainter21, "cell 6 0");

			//---- borderPainter29 ----
			borderPainter29.setScale(8.0F);
			borderPainter29.setArc(3);
			borderPainter29.setFocusWidth(1);
			borderPainter29.setLineWidth(3);
			flatTestPanel1.add(borderPainter29, "cell 8 0");

			//---- borderPainter10 ----
			borderPainter10.setScale(8.0F);
			borderPainter10.setPaintBackground(false);
			borderPainter10.setPaintFocus(false);
			borderPainter10.setFocusWidth(0);
			flatTestPanel1.add(borderPainter10, "cell 0 1");

			//---- borderPainter2 ----
			borderPainter2.setScale(8.0F);
			borderPainter2.setPaintBackground(false);
			borderPainter2.setPaintFocus(false);
			flatTestPanel1.add(borderPainter2, "cell 1 1");

			//---- borderPainter7 ----
			borderPainter7.setScale(8.0F);
			borderPainter7.setPaintBackground(false);
			borderPainter7.setPaintFocus(false);
			borderPainter7.setW(25);
			borderPainter7.setArc(10);
			flatTestPanel1.add(borderPainter7, "cell 2 1");

			//---- borderPainter14 ----
			borderPainter14.setScale(8.0F);
			borderPainter14.setPaintBackground(false);
			borderPainter14.setPaintFocus(false);
			borderPainter14.setW(25);
			borderPainter14.setArc(20);
			flatTestPanel1.add(borderPainter14, "cell 3 1");

			//---- borderPainter30 ----
			borderPainter30.setScale(8.0F);
			borderPainter30.setPaintBackground(false);
			borderPainter30.setPaintFocus(false);
			borderPainter30.setArc(20);
			flatTestPanel1.add(borderPainter30, "cell 4 1");

			//---- borderPainter18 ----
			borderPainter18.setScale(8.0F);
			borderPainter18.setPaintBackground(false);
			borderPainter18.setPaintFocus(false);
			borderPainter18.setFocusWidth(0);
			borderPainter18.setArc(0);
			flatTestPanel1.add(borderPainter18, "cell 5 1");

			//---- borderPainter22 ----
			borderPainter22.setScale(8.0F);
			borderPainter22.setPaintBackground(false);
			borderPainter22.setPaintFocus(false);
			borderPainter22.setArc(0);
			flatTestPanel1.add(borderPainter22, "cell 6 1");

			//---- borderPainter28 ----
			borderPainter28.setScale(8.0F);
			borderPainter28.setArc(2);
			borderPainter28.setFocusWidth(1);
			borderPainter28.setLineWidth(3);
			flatTestPanel1.add(borderPainter28, "cell 8 1");

			//---- borderPainter11 ----
			borderPainter11.setScale(8.0F);
			borderPainter11.setPaintBorder(false);
			borderPainter11.setPaintBackground(false);
			borderPainter11.setFocusWidth(0);
			flatTestPanel1.add(borderPainter11, "cell 0 2");

			//---- borderPainter3 ----
			borderPainter3.setScale(8.0F);
			borderPainter3.setPaintBorder(false);
			borderPainter3.setPaintBackground(false);
			flatTestPanel1.add(borderPainter3, "cell 1 2");

			//---- borderPainter5 ----
			borderPainter5.setScale(8.0F);
			borderPainter5.setPaintBorder(false);
			borderPainter5.setPaintBackground(false);
			borderPainter5.setArc(10);
			borderPainter5.setW(25);
			flatTestPanel1.add(borderPainter5, "cell 2 2");

			//---- borderPainter15 ----
			borderPainter15.setScale(8.0F);
			borderPainter15.setPaintBorder(false);
			borderPainter15.setPaintBackground(false);
			borderPainter15.setArc(20);
			borderPainter15.setW(25);
			flatTestPanel1.add(borderPainter15, "cell 3 2");

			//---- borderPainter31 ----
			borderPainter31.setScale(8.0F);
			borderPainter31.setPaintBorder(false);
			borderPainter31.setPaintBackground(false);
			borderPainter31.setArc(20);
			flatTestPanel1.add(borderPainter31, "cell 4 2");

			//---- borderPainter19 ----
			borderPainter19.setScale(8.0F);
			borderPainter19.setPaintBorder(false);
			borderPainter19.setPaintBackground(false);
			borderPainter19.setFocusWidth(0);
			borderPainter19.setArc(0);
			flatTestPanel1.add(borderPainter19, "cell 5 2");

			//---- borderPainter23 ----
			borderPainter23.setScale(8.0F);
			borderPainter23.setPaintBorder(false);
			borderPainter23.setPaintBackground(false);
			borderPainter23.setArc(0);
			flatTestPanel1.add(borderPainter23, "cell 6 2");

			//---- borderPainter27 ----
			borderPainter27.setScale(8.0F);
			borderPainter27.setArc(1);
			borderPainter27.setFocusWidth(1);
			borderPainter27.setLineWidth(3);
			flatTestPanel1.add(borderPainter27, "cell 8 2");

			//---- borderPainter12 ----
			borderPainter12.setScale(8.0F);
			borderPainter12.setFocusWidth(0);
			flatTestPanel1.add(borderPainter12, "cell 0 3");

			//---- borderPainter4 ----
			borderPainter4.setScale(8.0F);
			flatTestPanel1.add(borderPainter4, "cell 1 3");

			//---- borderPainter8 ----
			borderPainter8.setScale(8.0F);
			borderPainter8.setW(25);
			borderPainter8.setArc(10);
			flatTestPanel1.add(borderPainter8, "cell 2 3");

			//---- borderPainter16 ----
			borderPainter16.setScale(8.0F);
			borderPainter16.setW(25);
			borderPainter16.setArc(20);
			flatTestPanel1.add(borderPainter16, "cell 3 3");

			//---- borderPainter32 ----
			borderPainter32.setScale(8.0F);
			borderPainter32.setArc(20);
			flatTestPanel1.add(borderPainter32, "cell 4 3");

			//---- borderPainter20 ----
			borderPainter20.setScale(8.0F);
			borderPainter20.setFocusWidth(0);
			borderPainter20.setArc(0);
			flatTestPanel1.add(borderPainter20, "cell 5 3");

			//---- borderPainter24 ----
			borderPainter24.setScale(8.0F);
			borderPainter24.setArc(0);
			flatTestPanel1.add(borderPainter24, "cell 6 3");

			//---- borderPainter26 ----
			borderPainter26.setScale(8.0F);
			borderPainter26.setArc(0);
			borderPainter26.setFocusWidth(1);
			borderPainter26.setLineWidth(3);
			flatTestPanel1.add(borderPainter26, "cell 8 3");

			//---- label1 ----
			label1.setText("fw 0,  lw 1,  arc 6");
			flatTestPanel1.add(label1, "cell 0 4");

			//---- label2 ----
			label2.setText("fw 2,  lw 1,  arc 6");
			flatTestPanel1.add(label2, "cell 1 4");

			//---- label3 ----
			label3.setText("fw 2,  lw 1,  arc 10");
			flatTestPanel1.add(label3, "cell 2 4");

			//---- label4 ----
			label4.setText("fw 2,  lw 1,  arc 20");
			flatTestPanel1.add(label4, "cell 3 4");

			//---- label8 ----
			label8.setText("fw 2,  lw 1,  arc 20");
			flatTestPanel1.add(label8, "cell 4 4");

			//---- label5 ----
			label5.setText("fw 0,  lw 1,  arc 0");
			flatTestPanel1.add(label5, "cell 5 4");

			//---- label6 ----
			label6.setText("fw 2,  lw 1,  arc 0");
			flatTestPanel1.add(label6, "cell 6 4");

			//---- label7 ----
			label7.setText("fw 1,  lw 3,  arc 3,2,1,0");
			flatTestPanel1.add(label7, "cell 8 4");

			//======== panel6 ========
			{
				panel6.setLayout(new MigLayout(
					"insets 0,hidemode 3",
					// columns
					"[grow,fill]",
					// rows
					"[]unrel"));

				//======== panel7 ========
				{
					panel7.setBorder(new TitledBorder("Outlined Component Control"));
					panel7.setLayout(new MigLayout(
						"hidemode 3",
						// columns
						"[fill]" +
						"[fill]",
						// rows
						"[]" +
						"[]" +
						"[]"));

					//---- focusWidthFractionLabel ----
					focusWidthFractionLabel.setText("Focus width fraction:");
					panel7.add(focusWidthFractionLabel, "cell 0 0");

					//---- focusWidthFractionSlider ----
					focusWidthFractionSlider.setValue(100);
					focusWidthFractionSlider.setMajorTickSpacing(25);
					focusWidthFractionSlider.setPaintLabels(true);
					focusWidthFractionSlider.addChangeListener(e -> focusWidthFractionChanged());
					panel7.add(focusWidthFractionSlider, "cell 1 0");

					//---- focusInnerWidthLabel ----
					focusInnerWidthLabel.setText("Focus inner width:");
					panel7.add(focusInnerWidthLabel, "cell 0 1");

					//---- focusInnerWidthSlider ----
					focusInnerWidthSlider.setPaintLabels(true);
					focusInnerWidthSlider.setValue(10);
					focusInnerWidthSlider.setMaximum(50);
					focusInnerWidthSlider.addChangeListener(e -> focusInnerWidthChanged());
					panel7.add(focusInnerWidthSlider, "cell 1 1");

					//---- translucentCheckBox ----
					translucentCheckBox.setText("translucent");
					translucentCheckBox.addActionListener(e -> translucentChanged());
					panel7.add(translucentCheckBox, "cell 0 2 2 1,alignx left,growx 0");
				}
				panel6.add(panel7, "cell 0 0");
			}
			flatTestPanel1.add(panel6, "cell 6 5 3 1,aligny top,growy 0");
		}
		setViewportView(flatTestPanel1);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JSlider focusWidthFractionSlider;
	private JSlider focusInnerWidthSlider;
	private JCheckBox translucentCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class BorderPainter ------------------------------------------------

	public static class BorderPainter
		extends JComponent
	{
		private static final Color TRANSLUCENT_BLUE = new Color( 0x880000ff, true );
		private static final Color TRANSLUCENT_RED = new Color( 0x88ff0000, true );

		private int w = 20;
		private int h = 20;
		private int focusWidth = 2;
		private int lineWidth = 1;
		private int arc = 6;
		private float scale = 1;
		private boolean paintBackground = true;
		private boolean paintBorder = true;
		private boolean paintFocus = true;

		float focusWidthFraction = 1;
		float focusInnerWidth = 1;
		boolean translucent;

		public BorderPainter() {
		}

		public int getW() {
			return w;
		}

		public void setW( int w ) {
			this.w = w;
		}

		public int getH() {
			return h;
		}

		public void setH( int h ) {
			this.h = h;
		}

		public int getFocusWidth() {
			return focusWidth;
		}

		public void setFocusWidth( int focusWidth ) {
			this.focusWidth = focusWidth;
		}

		public int getLineWidth() {
			return lineWidth;
		}

		public void setLineWidth( int lineWidth ) {
			this.lineWidth = lineWidth;
		}

		public int getArc() {
			return arc;
		}

		public void setArc( int arc ) {
			this.arc = arc;
		}

		public float getScale() {
			return scale;
		}

		public void setScale( float scale ) {
			this.scale = scale;
		}

		public boolean isPaintBackground() {
			return paintBackground;
		}

		public void setPaintBackground( boolean paintBackground ) {
			this.paintBackground = paintBackground;
		}

		public boolean isPaintBorder() {
			return paintBorder;
		}

		public void setPaintBorder( boolean paintBorder ) {
			this.paintBorder = paintBorder;
		}

		public boolean isPaintFocus() {
			return paintFocus;
		}

		public void setPaintFocus( boolean paintFocus ) {
			this.paintFocus = paintFocus;
		}

		@Override
		public Dimension getPreferredSize() {
			return UIScale.scale( new Dimension( (int) ((w + 2) * scale), (int) ((h + 2) * scale) ) );
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		@Override
		protected void paintComponent( Graphics g ) {
			Graphics2D g2 = (Graphics2D) g;
			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g2 );

			g2.scale( scale, scale );
			g2.translate( 1, 1 );

			int width = UIScale.scale( w );
			int height = UIScale.scale( h );
			float focusWidth = UIScale.scale( (float) this.focusWidth );
			float focusInnerWidth = UIScale.scale( this.focusInnerWidth );
			float lineWidth = UIScale.scale( (float) this.lineWidth );
			float arc = UIScale.scale( (float) this.arc );

			Color background = paintBackground ? Color.green : null;
			Color focusColor = paintFocus ? (translucent ? TRANSLUCENT_BLUE : Color.blue) : null;
			Color borderColor = paintBorder ? (translucent ? TRANSLUCENT_RED : Color.red) : null;

			FlatUIUtils.paintOutlinedComponent( g2, 0, 0, width, height,
				focusWidth, focusWidthFraction, focusInnerWidth, lineWidth, arc,
				focusColor, borderColor, background );

			HiDPIUtils.paintAtScale1x( g2, 0, 0, width, height,
				(g2d, x2, y2, width2, height2, scaleFactor) -> {
					int gap = 3;
					g2d.setColor( Color.magenta );
					g2d.drawRect( x2 - gap, y2 - gap, width2 + (gap * 2) - 1, height2 + (gap * 2) - 1 );
				} );

			FlatUIUtils.resetRenderingHints( g2, oldRenderingHints );
		}
	}
}
