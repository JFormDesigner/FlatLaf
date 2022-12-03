/*
 * Copyright 2022 FormDev Software GmbH
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.FlatArrowButton;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatPaintingArrowsTest
	extends JPanel
{
	private Timer timer = null;

	public static void main( String[] args ) {
		System.setProperty( FlatSystemProperties.UI_SCALE, "1x" );
		System.setProperty( "sun.java2d.uiScale", "1x" );

		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatPaintingArrowsTest" );
			frame.showFrame( FlatPaintingArrowsTest::new );
		} );
	}

	FlatPaintingArrowsTest() {
		initComponents();

		Hashtable<Integer, JLabel> labels = new Hashtable<>();
		for( int i = 100; i <= 600; i += 100 )
			labels.put( i, new JLabel( String.format( "%dx", i / 100 ) ) );
		pixelsScaleSlider.setLabelTable( labels );

		scrollPane.getHorizontalScrollBar().setUnitIncrement( UIScale.scale( 25 ) );
		scrollPane.getVerticalScrollBar().setUnitIncrement( UIScale.scale( 25 ) );

		scaleChanged();
		pixelsChanged();

		// repaint to see code changes immediately when running in debugger
		timer = new Timer( 500, e -> {
			// stop timer to allow application to exit
			if( !isDisplayable() )
				timer.stop();

			repaint();
		} );
		timer.start();
	}

	private void scaleChanged() {
		int scale = Math.max( scaleSlider.getValue(), 1 );
		FlatTestFrame.updateComponentsRecur( panel, (c, type) -> {
			if( c instanceof ArrowPainter ) {
				((ArrowPainter)c).scale = scale;
				c.revalidate();
			}
		} );

		panel.revalidate();
		panel.repaint();
	}

	private void pixelsChanged() {
		boolean paintPixels = pixelsCheckBox.isSelected();
		boolean paintVector = vectorCheckBox.isSelected();

		vectorCheckBox.setEnabled( paintPixels );

		FlatTestFrame.updateComponentsRecur( panel, (c, type) -> {
			if( c instanceof ArrowPainter ) {
				((ArrowPainter)c).paintPixels = paintPixels;
				((ArrowPainter)c).paintVector = paintVector;
			}
		} );

		panel.repaint();
	}

	private void pixelsScaleChanged() {
		float paintPixelsScale = pixelsScaleSlider.getValue() / 100f;

		FlatTestFrame.updateComponentsRecur( panel, (c, type) -> {
			if( c instanceof ArrowPainter )
				((ArrowPainter)c).paintPixelsScale = paintPixelsScale;
		} );

		panel.repaint();
	}

	private void arrowSizeChanged() {
		int width = (int) arrowWidthSpinner.getValue();
		int height = (int) arrowHeightSpinner.getValue();
		int arrowSize = (int) arrowSizeSpinner.getValue();
		float arrowThickness = (float) arrowThicknessSpinner.getValue();

		FlatTestFrame.updateComponentsRecur( panel, (c, type) -> {
			if( c instanceof ArrowPainter ) {
				ArrowPainter painter = (ArrowPainter) c;
				painter.setW( painter.isHalfWidth() ? width / 2 : width );
				painter.setH( painter.isHalfHeight() ? height / 2 : height );
				painter.arrowSize = arrowSize;
				painter.arrowThickness = arrowThickness;
			}
		} );

		revalidate();
		repaint();
	}

	private void offsetChanged() {
		float offset = (float) offsetSpinner.getValue();
		System.out.println( offset );

		arrowPainter5.setYOffset( offset );
		arrowPainter6.setYOffset( -offset );

		arrowPainter7.setXOffset( offset );
		arrowPainter8.setXOffset( -offset );

		arrowPainter13.setYOffset( offset );
		arrowPainter14.setYOffset( -offset );

		arrowPainter15.setXOffset( offset );
		arrowPainter16.setXOffset( -offset );

		repaint();
	}

	private void arrowButtonChanged() {
		boolean button = buttonCheckBox.isSelected();

		FlatTestFrame.updateComponentsRecur( panel, (c, type) -> {
			if( c instanceof ArrowPainter )
				((ArrowPainter)c).button = button;
		} );

		repaint();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		scrollPane = new JScrollPane();
		panel = new FlatTestPanel();
		FlatPaintingArrowsTest.ArrowPainter arrowPainter1 = new FlatPaintingArrowsTest.ArrowPainter();
		FlatPaintingArrowsTest.ArrowPainter arrowPainter2 = new FlatPaintingArrowsTest.ArrowPainter();
		FlatPaintingArrowsTest.ArrowPainter arrowPainter3 = new FlatPaintingArrowsTest.ArrowPainter();
		FlatPaintingArrowsTest.ArrowPainter arrowPainter4 = new FlatPaintingArrowsTest.ArrowPainter();
		JPanel panel2 = new JPanel();
		arrowPainter5 = new FlatPaintingArrowsTest.ArrowPainter();
		arrowPainter6 = new FlatPaintingArrowsTest.ArrowPainter();
		JPanel panel3 = new JPanel();
		arrowPainter7 = new FlatPaintingArrowsTest.ArrowPainter();
		arrowPainter8 = new FlatPaintingArrowsTest.ArrowPainter();
		FlatPaintingArrowsTest.ArrowPainter arrowPainter9 = new FlatPaintingArrowsTest.ArrowPainter();
		FlatPaintingArrowsTest.ArrowPainter arrowPainter10 = new FlatPaintingArrowsTest.ArrowPainter();
		FlatPaintingArrowsTest.ArrowPainter arrowPainter11 = new FlatPaintingArrowsTest.ArrowPainter();
		FlatPaintingArrowsTest.ArrowPainter arrowPainter12 = new FlatPaintingArrowsTest.ArrowPainter();
		JPanel panel4 = new JPanel();
		arrowPainter13 = new FlatPaintingArrowsTest.ArrowPainter();
		arrowPainter14 = new FlatPaintingArrowsTest.ArrowPainter();
		JPanel panel5 = new JPanel();
		arrowPainter15 = new FlatPaintingArrowsTest.ArrowPainter();
		arrowPainter16 = new FlatPaintingArrowsTest.ArrowPainter();
		JPanel panel1 = new JPanel();
		JLabel scaleLabel = new JLabel();
		scaleSlider = new JSlider();
		pixelsCheckBox = new JCheckBox();
		vectorCheckBox = new JCheckBox();
		pixelsScaleLabel = new JLabel();
		pixelsScaleSlider = new JSlider();
		JPanel panel55 = new JPanel();
		JLabel arrowWidthLabel = new JLabel();
		arrowWidthSpinner = new JSpinner();
		JLabel arrowHeightLabel = new JLabel();
		arrowHeightSpinner = new JSpinner();
		JLabel arrowSizeLabel = new JLabel();
		arrowSizeSpinner = new JSpinner();
		JLabel arrowThicknessLabel = new JLabel();
		arrowThicknessSpinner = new JSpinner();
		JLabel offsetLabel = new JLabel();
		offsetSpinner = new JSpinner();
		buttonCheckBox = new JCheckBox();

		//======== this ========
		setLayout(new BorderLayout());

		//======== scrollPane ========
		{
			scrollPane.setBorder(null);

			//======== panel ========
			{
				panel.setLayout(new MigLayout(
					"ltr,insets dialog,hidemode 3",
					// columns
					"[fill]" +
					"[fill]" +
					"[fill]" +
					"[fill]" +
					"[fill]" +
					"[fill]",
					// rows
					"para[]" +
					"[]"));
				panel.add(arrowPainter1, "cell 0 0,align left top,grow 0 0");

				//---- arrowPainter2 ----
				arrowPainter2.setDirection(1);
				panel.add(arrowPainter2, "cell 1 0,align left top,grow 0 0");

				//---- arrowPainter3 ----
				arrowPainter3.setDirection(7);
				panel.add(arrowPainter3, "cell 2 0,align left top,grow 0 0");

				//---- arrowPainter4 ----
				arrowPainter4.setDirection(3);
				panel.add(arrowPainter4, "cell 3 0,align left top,grow 0 0");

				//======== panel2 ========
				{
					panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));

					//---- arrowPainter5 ----
					arrowPainter5.setDirection(1);
					arrowPainter5.setH(10);
					arrowPainter5.setHalfHeight(true);
					arrowPainter5.setYOffset(1);
					panel2.add(arrowPainter5);

					//---- arrowPainter6 ----
					arrowPainter6.setH(10);
					arrowPainter6.setHalfHeight(true);
					arrowPainter6.setYOffset(-1);
					panel2.add(arrowPainter6);
				}
				panel.add(panel2, "cell 4 0,align left top,grow 0 0");

				//======== panel3 ========
				{
					panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

					//---- arrowPainter7 ----
					arrowPainter7.setDirection(7);
					arrowPainter7.setW(10);
					arrowPainter7.setHalfWidth(true);
					arrowPainter7.setXOffset(1);
					panel3.add(arrowPainter7);

					//---- arrowPainter8 ----
					arrowPainter8.setDirection(3);
					arrowPainter8.setW(10);
					arrowPainter8.setHalfWidth(true);
					arrowPainter8.setXOffset(-1);
					panel3.add(arrowPainter8);
				}
				panel.add(panel3, "cell 5 0,align left top,grow 0 0");

				//---- arrowPainter9 ----
				arrowPainter9.setChevron(false);
				panel.add(arrowPainter9, "cell 0 1,align left top,grow 0 0");

				//---- arrowPainter10 ----
				arrowPainter10.setDirection(1);
				arrowPainter10.setChevron(false);
				panel.add(arrowPainter10, "cell 1 1,align left top,grow 0 0");

				//---- arrowPainter11 ----
				arrowPainter11.setDirection(7);
				arrowPainter11.setChevron(false);
				panel.add(arrowPainter11, "cell 2 1,align left top,grow 0 0");

				//---- arrowPainter12 ----
				arrowPainter12.setDirection(3);
				arrowPainter12.setChevron(false);
				panel.add(arrowPainter12, "cell 3 1,align left top,grow 0 0");

				//======== panel4 ========
				{
					panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));

					//---- arrowPainter13 ----
					arrowPainter13.setDirection(1);
					arrowPainter13.setH(10);
					arrowPainter13.setChevron(false);
					arrowPainter13.setHalfHeight(true);
					arrowPainter13.setYOffset(1);
					panel4.add(arrowPainter13);

					//---- arrowPainter14 ----
					arrowPainter14.setH(10);
					arrowPainter14.setChevron(false);
					arrowPainter14.setHalfHeight(true);
					arrowPainter14.setYOffset(-1);
					panel4.add(arrowPainter14);
				}
				panel.add(panel4, "cell 4 1,align left top,grow 0 0");

				//======== panel5 ========
				{
					panel5.setLayout(new BoxLayout(panel5, BoxLayout.X_AXIS));

					//---- arrowPainter15 ----
					arrowPainter15.setDirection(7);
					arrowPainter15.setW(10);
					arrowPainter15.setChevron(false);
					arrowPainter15.setHalfWidth(true);
					arrowPainter15.setXOffset(1);
					panel5.add(arrowPainter15);

					//---- arrowPainter16 ----
					arrowPainter16.setDirection(3);
					arrowPainter16.setW(10);
					arrowPainter16.setChevron(false);
					arrowPainter16.setHalfWidth(true);
					arrowPainter16.setXOffset(-1);
					panel5.add(arrowPainter16);
				}
				panel.add(panel5, "cell 5 1,align left top,grow 0 0");
			}
			scrollPane.setViewportView(panel);
		}
		add(scrollPane, BorderLayout.CENTER);

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"flowy,hidemode 3",
				// columns
				"[fill]" +
				"[grow,fill]para" +
				"[fill]" +
				"[fill]" +
				"[fill]",
				// rows
				"[]" +
				"[]"));

			//---- scaleLabel ----
			scaleLabel.setText("Scale:");
			scaleLabel.setLabelFor(scaleSlider);
			scaleLabel.setDisplayedMnemonic('S');
			panel1.add(scaleLabel, "cell 0 0");

			//---- scaleSlider ----
			scaleSlider.setMaximum(64);
			scaleSlider.setValue(16);
			scaleSlider.setPaintTicks(true);
			scaleSlider.setMajorTickSpacing(8);
			scaleSlider.setPaintLabels(true);
			scaleSlider.setMinorTickSpacing(1);
			scaleSlider.setSnapToTicks(true);
			scaleSlider.addChangeListener(e -> scaleChanged());
			panel1.add(scaleSlider, "cell 1 0");

			//---- pixelsCheckBox ----
			pixelsCheckBox.setText("pixels");
			pixelsCheckBox.setMnemonic('P');
			pixelsCheckBox.setSelected(true);
			pixelsCheckBox.addActionListener(e -> pixelsChanged());
			panel1.add(pixelsCheckBox, "cell 2 0");

			//---- vectorCheckBox ----
			vectorCheckBox.setText("vector");
			vectorCheckBox.setMnemonic('V');
			vectorCheckBox.setSelected(true);
			vectorCheckBox.addActionListener(e -> pixelsChanged());
			panel1.add(vectorCheckBox, "cell 2 0");

			//---- pixelsScaleLabel ----
			pixelsScaleLabel.setText("Scale:");
			pixelsScaleLabel.setLabelFor(pixelsScaleSlider);
			pixelsScaleLabel.setDisplayedMnemonic('C');
			panel1.add(pixelsScaleLabel, "cell 3 0");

			//---- pixelsScaleSlider ----
			pixelsScaleSlider.setMinimum(100);
			pixelsScaleSlider.setMaximum(600);
			pixelsScaleSlider.setMinorTickSpacing(25);
			pixelsScaleSlider.setMajorTickSpacing(100);
			pixelsScaleSlider.setSnapToTicks(true);
			pixelsScaleSlider.setPaintTicks(true);
			pixelsScaleSlider.setPaintLabels(true);
			pixelsScaleSlider.addChangeListener(e -> pixelsScaleChanged());
			panel1.add(pixelsScaleSlider, "cell 4 0");

			//======== panel55 ========
			{
				panel55.setBorder(new TitledBorder("Arrow Control"));
				panel55.setLayout(new MigLayout(
					"hidemode 3",
					// columns
					"[fill]" +
					"[fill]unrel" +
					"[fill]" +
					"[fill]unrel" +
					"[fill]" +
					"[fill]unrel" +
					"[fill]" +
					"[fill]unrel" +
					"[fill]" +
					"[fill]unrel" +
					"[fill]",
					// rows
					"[]"));

				//---- arrowWidthLabel ----
				arrowWidthLabel.setText("Width:");
				panel55.add(arrowWidthLabel, "cell 0 0");

				//---- arrowWidthSpinner ----
				arrowWidthSpinner.setModel(new SpinnerNumberModel(20, 0, null, 1));
				arrowWidthSpinner.addChangeListener(e -> arrowSizeChanged());
				panel55.add(arrowWidthSpinner, "cell 1 0");

				//---- arrowHeightLabel ----
				arrowHeightLabel.setText("Height:");
				panel55.add(arrowHeightLabel, "cell 2 0");

				//---- arrowHeightSpinner ----
				arrowHeightSpinner.setModel(new SpinnerNumberModel(20, 0, null, 1));
				arrowHeightSpinner.addChangeListener(e -> arrowSizeChanged());
				panel55.add(arrowHeightSpinner, "cell 3 0");

				//---- arrowSizeLabel ----
				arrowSizeLabel.setText("Arrow Size:");
				panel55.add(arrowSizeLabel, "cell 4 0");

				//---- arrowSizeSpinner ----
				arrowSizeSpinner.setModel(new SpinnerNumberModel(9, 2, null, 1));
				arrowSizeSpinner.addChangeListener(e -> arrowSizeChanged());
				panel55.add(arrowSizeSpinner, "cell 5 0");

				//---- arrowThicknessLabel ----
				arrowThicknessLabel.setText("Arrow Thickness:");
				panel55.add(arrowThicknessLabel, "cell 6 0");

				//---- arrowThicknessSpinner ----
				arrowThicknessSpinner.setModel(new SpinnerNumberModel(1.0F, 0.25F, null, 0.25F));
				arrowThicknessSpinner.addChangeListener(e -> arrowSizeChanged());
				panel55.add(arrowThicknessSpinner, "cell 7 0");

				//---- offsetLabel ----
				offsetLabel.setText("Offset:");
				panel55.add(offsetLabel, "cell 8 0");

				//---- offsetSpinner ----
				offsetSpinner.setModel(new SpinnerNumberModel(1.0F, null, null, 0.05F));
				offsetSpinner.addChangeListener(e -> offsetChanged());
				panel55.add(offsetSpinner, "cell 9 0");

				//---- buttonCheckBox ----
				buttonCheckBox.setText("FlatArrowButton");
				buttonCheckBox.addActionListener(e -> arrowButtonChanged());
				panel55.add(buttonCheckBox, "cell 10 0,alignx left,growx 0");
			}
			panel1.add(panel55, "cell 0 1 5 1");
		}
		add(panel1, BorderLayout.NORTH);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrollPane;
	private FlatTestPanel panel;
	private FlatPaintingArrowsTest.ArrowPainter arrowPainter5;
	private FlatPaintingArrowsTest.ArrowPainter arrowPainter6;
	private FlatPaintingArrowsTest.ArrowPainter arrowPainter7;
	private FlatPaintingArrowsTest.ArrowPainter arrowPainter8;
	private FlatPaintingArrowsTest.ArrowPainter arrowPainter13;
	private FlatPaintingArrowsTest.ArrowPainter arrowPainter14;
	private FlatPaintingArrowsTest.ArrowPainter arrowPainter15;
	private FlatPaintingArrowsTest.ArrowPainter arrowPainter16;
	private JSlider scaleSlider;
	private JCheckBox pixelsCheckBox;
	private JCheckBox vectorCheckBox;
	private JLabel pixelsScaleLabel;
	private JSlider pixelsScaleSlider;
	private JSpinner arrowWidthSpinner;
	private JSpinner arrowHeightSpinner;
	private JSpinner arrowSizeSpinner;
	private JSpinner arrowThicknessSpinner;
	private JSpinner offsetSpinner;
	private JCheckBox buttonCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class ArrowPainter -------------------------------------------------

	public static class ArrowPainter
		extends JComponent
	{
		private int w = 20;
		private int h = 20;
		private int direction = SwingConstants.SOUTH;
		private boolean chevron = true;
		private float xOffset = 0;
		private float yOffset = 0;
		private boolean halfWidth;
		private boolean halfHeight;
		public boolean button;

		int arrowSize = FlatArrowButton.DEFAULT_ARROW_WIDTH;
		float arrowThickness = 1;
		int scale = 4;
		boolean paintPixels;
		boolean paintVector;
		float paintPixelsScale = 1;

		public ArrowPainter() {
		}

		public int getW() {
			return w;
		}

		public void setW( int w ) {
			this.w = w;
			invalidate();
		}

		public int getH() {
			return h;
		}

		public void setH( int h ) {
			this.h = h;
			invalidate();
		}

		public int getDirection() {
			return direction;
		}

		public void setDirection( int direction ) {
			this.direction = direction;
		}

		public boolean isChevron() {
			return chevron;
		}

		public void setChevron( boolean chevron ) {
			this.chevron = chevron;
		}

		public float getXOffset() {
			return xOffset;
		}

		public void setXOffset( float xOffset ) {
			this.xOffset = xOffset;
		}

		public float getYOffset() {
			return yOffset;
		}

		public void setYOffset( float yOffset ) {
			this.yOffset = yOffset;
		}

		public boolean isHalfWidth() {
			return halfWidth;
		}

		public void setHalfWidth( boolean halfWidth ) {
			this.halfWidth = halfWidth;
		}

		public boolean isHalfHeight() {
			return halfHeight;
		}

		public void setHalfHeight( boolean halfHeight ) {
			this.halfHeight = halfHeight;
		}

		@Override
		public Dimension getPreferredSize() {
			return UIScale.scale( new Dimension( w * scale, h * scale ) );
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		@Override
		protected void paintComponent( Graphics g ) {
			Graphics2D g2 = (Graphics2D) g;
			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g2 );

			int width = UIScale.scale( w );
			int height = UIScale.scale( h );

			// paint arrow scaled
			if( !paintPixels ) {
				// paint arrow as vector
				AffineTransform oldTransform = g2.getTransform();
				g2.scale( scale, scale );
				g.setColor( Color.blue );
				paintArrow( g2, width, height );
				g2.setTransform( oldTransform );
			} else {
				// paint arrow as pixels
				int bitmapWidth = Math.round( width * paintPixelsScale );
				int bitmapHeight = Math.round( height * paintPixelsScale );

				// paint icon to buffered image
				BufferedImage bi = new BufferedImage( bitmapWidth, bitmapHeight, BufferedImage.TYPE_INT_ARGB );
				Graphics2D bg = bi.createGraphics();
				try {
					FlatUIUtils.setRenderingHints( bg );

					bg.scale( paintPixelsScale, paintPixelsScale );
					bg.setColor( Color.blue );
					paintArrow( bg, width, height );
				} finally {
					bg.dispose();
				}

				// draw scaled-up image
				g2.drawImage( bi, 0, 0, getWidth(), getHeight(), null );

				if( paintVector ) {
					AffineTransform oldTransform = g2.getTransform();
					g2.scale( scale, scale );
					g.setColor( new Color( (Color.red.getRGB() & 0xffffff) | (0xa0 << 24), true ) );
					paintArrow( g2, width, height );
					g2.setTransform( oldTransform );
				}
			}

			// paint border and grid
			HiDPIUtils.paintAtScale1x( g2, 0, 0, getWidth(), getHeight(),
				(g2d, x2, y2, width2, height2, scaleFactor) -> {
					// draw border
					g2d.setColor( Color.magenta );
					g2d.drawRect( x2, y2, width2 - 1, height2 - 1 );

					// draw grid
					float pixelSize = scale / paintPixelsScale;
					if( pixelSize >= 4 ) {
						Color lineColor1 = new Color( (Color.blue.getRGB() & 0xffffff) | (0x20 << 24), true );
						Color lineColor2 = new Color( (Color.blue.getRGB() & 0xffffff) | (0x60 << 24), true );
						for( float x = x2 + pixelSize, i = 1; x < x2 + width2; x += pixelSize, i++ ) {
							g2d.setColor( ((int)i % 4 == 0) ? lineColor2 : lineColor1 );
							g2d.drawLine( Math.round( x ), y2 + 1, Math.round( x ), y2 + height2 - 2 );
						}
						for( float y = y2 + pixelSize, i = 1; y < y2 + height2; y += pixelSize, i++ ) {
							g2d.setColor( ((int)i % 4 == 0) ? lineColor2 : lineColor1 );
							g2d.drawLine( x2 + 1, Math.round( y ), x2 + width2 - 2, Math.round( y ) );
						}
					}
				} );

			FlatUIUtils.resetRenderingHints( g2, oldRenderingHints );
		}

		private void paintArrow( Graphics2D g, int width, int height ) {
			// do not paint in JFormDesigner because it may use a different FlatLaf version
			if( !Beans.isDesignTime() ) {
				FlatUIUtils.paintArrow( g, 0, 0, width, height,
					direction, chevron, arrowSize, arrowThickness, xOffset, yOffset );
			}

			if( button ) {
				FlatArrowButton arrowButton = new FlatArrowButton( direction,
					chevron ? null : "triangle", Color.black, null, null, null, null, null );
				arrowButton.setArrowWidth( arrowSize );
				arrowButton.setXOffset( xOffset );
				arrowButton.setYOffset( yOffset );
				arrowButton.setSize( width, height );
				arrowButton.paint( g );
			}
		}
	}
}
