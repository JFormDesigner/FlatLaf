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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HiDPIUtils;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatPaintingHiDPITest
	extends JPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatPaintingHiDPITest" );
			frame.showFrame( FlatPaintingHiDPITest::new );
		} );
	}

	FlatPaintingHiDPITest() {
		initComponents();
		sliderChanged();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		reset();
	}

	private void sliderChanged() {
		painter.originX = originXSlider.getValue();
		painter.originY = originYSlider.getValue();
		painter.translateX = translateXSlider.getValue();
		painter.translateY = translateYSlider.getValue();
		painter.scaleX = scaleXSlider.getValue();
		painter.scaleY = scaleYSlider.getValue();
		painter.rotate = rotateSlider.getValue();
		painter.repaint();

		AffineTransform t = new AffineTransform();
		t.translate( painter.translateX, painter.translateY );
		t.scale( painter.scaleX / 100., painter.scaleY / 100. );
		t.rotate( Math.toRadians( painter.rotate ) );

		tScaleXLabel.setText( Double.toString( t.getScaleX() ) );
		tScaleYLabel.setText( Double.toString( t.getScaleY() ) );
		tShearXLabel.setText( Double.toString( t.getShearX() ) );
		tShearYLabel.setText( Double.toString( t.getShearY() ) );
		tTranslateXLabel.setText( Double.toString( t.getTranslateX() ) );
		tTranslateYLabel.setText( Double.toString( t.getTranslateY() ) );

		double scaleX = Math.hypot( t.getScaleX(), t.getShearX() );
		double scaleY = Math.hypot( t.getScaleY(), t.getShearY() );
		if( t.getScaleX() < 0 )
		    scaleX = -scaleX;
		if( t.getScaleY() < 0 )
		    scaleY = -scaleY;

		double rotation = Math.atan2( t.getShearY(), t.getScaleY() );
		double rotationDegrees = Math.toDegrees( rotation );
		cScaleXLabel.setText( Double.toString( scaleX ) );
		cScaleYLabel.setText( Double.toString( scaleY ) );
		cRotationDegreesLabel.setText( Double.toString( rotationDegrees ) );
	}

	private void reset() {
		AffineTransform t = getGraphicsConfiguration().getDefaultTransform();

		originXSlider.setValue( 20 );
		originYSlider.setValue( 10 );
		translateXSlider.setValue( 100 );
		translateYSlider.setValue( 100 );
		scaleXSlider.setValue( (int) (t.getScaleX() * 100) );
		scaleYSlider.setValue( (int) (t.getScaleY() * 100) );
		rotateSlider.setValue( 0 );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel originXLabel = new JLabel();
		originXSlider = new JSlider();
		JLabel originYLabel = new JLabel();
		originYSlider = new JSlider();
		JLabel translateXLabel = new JLabel();
		translateXSlider = new JSlider();
		JLabel translateYLabel = new JLabel();
		translateYSlider = new JSlider();
		JLabel scaleXLabel = new JLabel();
		scaleXSlider = new JSlider();
		JLabel scaleYLabel = new JLabel();
		scaleYSlider = new JSlider();
		JLabel rotateLabel = new JLabel();
		rotateSlider = new JSlider();
		JPanel panel1 = new JPanel();
		JLabel tLabel = new JLabel();
		JLabel xLabel = new JLabel();
		JLabel yLabel = new JLabel();
		JLabel tScaleLabel = new JLabel();
		tScaleXLabel = new JLabel();
		tScaleYLabel = new JLabel();
		JLabel tShearLabel = new JLabel();
		tShearXLabel = new JLabel();
		tShearYLabel = new JLabel();
		JLabel tTranslateLabel = new JLabel();
		tTranslateXLabel = new JLabel();
		tTranslateYLabel = new JLabel();
		JLabel cLabel = new JLabel();
		JLabel cScaleLabel = new JLabel();
		cScaleXLabel = new JLabel();
		cScaleYLabel = new JLabel();
		JLabel cRotationLabel = new JLabel();
		cRotationDegreesLabel = new JLabel();
		JButton resetButton = new JButton();
		painter = new FlatPaintingHiDPITest.HiDPI1xPainter();

		//======== this ========
		setBorder(null);
		setLayout(new MigLayout(
			"hidemode 3",
			// columns
			"[fill]" +
			"[400,fill]para" +
			"[grow,fill]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[grow]"));

		//---- originXLabel ----
		originXLabel.setText("OriginX:");
		add(originXLabel, "cell 0 0");

		//---- originXSlider ----
		originXSlider.setMaximum(500);
		originXSlider.setMajorTickSpacing(100);
		originXSlider.setMinorTickSpacing(25);
		originXSlider.setValue(20);
		originXSlider.setPaintTicks(true);
		originXSlider.addChangeListener(e -> sliderChanged());
		add(originXSlider, "cell 1 0");

		//---- originYLabel ----
		originYLabel.setText("OriginY:");
		add(originYLabel, "cell 0 1");

		//---- originYSlider ----
		originYSlider.setMaximum(500);
		originYSlider.setPaintLabels(true);
		originYSlider.setPaintTicks(true);
		originYSlider.setMajorTickSpacing(100);
		originYSlider.setMinorTickSpacing(25);
		originYSlider.setValue(10);
		originYSlider.addChangeListener(e -> sliderChanged());
		add(originYSlider, "cell 1 1");

		//---- translateXLabel ----
		translateXLabel.setText("TranslateX:");
		add(translateXLabel, "cell 0 2");

		//---- translateXSlider ----
		translateXSlider.setMaximum(500);
		translateXSlider.setPaintTicks(true);
		translateXSlider.setMajorTickSpacing(100);
		translateXSlider.setMinorTickSpacing(25);
		translateXSlider.setValue(100);
		translateXSlider.addChangeListener(e -> sliderChanged());
		add(translateXSlider, "cell 1 2");

		//---- translateYLabel ----
		translateYLabel.setText("TranslateY:");
		add(translateYLabel, "cell 0 3");

		//---- translateYSlider ----
		translateYSlider.setMaximum(500);
		translateYSlider.setPaintLabels(true);
		translateYSlider.setPaintTicks(true);
		translateYSlider.setMajorTickSpacing(100);
		translateYSlider.setMinorTickSpacing(25);
		translateYSlider.setValue(100);
		translateYSlider.addChangeListener(e -> sliderChanged());
		add(translateYSlider, "cell 1 3");

		//---- scaleXLabel ----
		scaleXLabel.setText("ScaleX:");
		add(scaleXLabel, "cell 0 4");

		//---- scaleXSlider ----
		scaleXSlider.setMaximum(400);
		scaleXSlider.setValue(100);
		scaleXSlider.setPaintTicks(true);
		scaleXSlider.setMajorTickSpacing(50);
		scaleXSlider.setSnapToTicks(true);
		scaleXSlider.setMinorTickSpacing(5);
		scaleXSlider.setMinimum(-100);
		scaleXSlider.addChangeListener(e -> sliderChanged());
		add(scaleXSlider, "cell 1 4");

		//---- scaleYLabel ----
		scaleYLabel.setText("ScaleY:");
		add(scaleYLabel, "cell 0 5");

		//---- scaleYSlider ----
		scaleYSlider.setMaximum(400);
		scaleYSlider.setValue(100);
		scaleYSlider.setPaintTicks(true);
		scaleYSlider.setPaintLabels(true);
		scaleYSlider.setMajorTickSpacing(50);
		scaleYSlider.setSnapToTicks(true);
		scaleYSlider.setMinorTickSpacing(5);
		scaleYSlider.setMinimum(-100);
		scaleYSlider.addChangeListener(e -> sliderChanged());
		add(scaleYSlider, "cell 1 5");

		//---- rotateLabel ----
		rotateLabel.setText("Rotate:");
		add(rotateLabel, "cell 0 6");

		//---- rotateSlider ----
		rotateSlider.setMaximum(360);
		rotateSlider.setMinimum(-360);
		rotateSlider.setValue(0);
		rotateSlider.setMajorTickSpacing(90);
		rotateSlider.setPaintLabels(true);
		rotateSlider.setPaintTicks(true);
		rotateSlider.addChangeListener(e -> sliderChanged());
		add(rotateSlider, "cell 1 6");

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[50,fill]" +
				"[50,fill]",
				// rows
				"[]" +
				"[]0" +
				"[]0" +
				"[]para" +
				"[]" +
				"[]0" +
				"[]"));

			//---- tLabel ----
			tLabel.setText("AffineTransform:");
			panel1.add(tLabel, "cell 0 0");

			//---- xLabel ----
			xLabel.setText("X");
			panel1.add(xLabel, "cell 1 0,alignx center,growx 0");

			//---- yLabel ----
			yLabel.setText("Y");
			panel1.add(yLabel, "cell 2 0,alignx center,growx 0");

			//---- tScaleLabel ----
			tScaleLabel.setText("Scale:");
			panel1.add(tScaleLabel, "cell 0 1,gapx indent");

			//---- tScaleXLabel ----
			tScaleXLabel.setText("text");
			panel1.add(tScaleXLabel, "cell 1 1");

			//---- tScaleYLabel ----
			tScaleYLabel.setText("text");
			panel1.add(tScaleYLabel, "cell 2 1");

			//---- tShearLabel ----
			tShearLabel.setText("Shear:");
			panel1.add(tShearLabel, "cell 0 2,gapx indent");

			//---- tShearXLabel ----
			tShearXLabel.setText("text");
			panel1.add(tShearXLabel, "cell 1 2");

			//---- tShearYLabel ----
			tShearYLabel.setText("text");
			panel1.add(tShearYLabel, "cell 2 2");

			//---- tTranslateLabel ----
			tTranslateLabel.setText("Translate:");
			panel1.add(tTranslateLabel, "cell 0 3,gapx indent");

			//---- tTranslateXLabel ----
			tTranslateXLabel.setText("text");
			panel1.add(tTranslateXLabel, "cell 1 3");

			//---- tTranslateYLabel ----
			tTranslateYLabel.setText("text");
			panel1.add(tTranslateYLabel, "cell 2 3");

			//---- cLabel ----
			cLabel.setText("Computed:");
			panel1.add(cLabel, "cell 0 4");

			//---- cScaleLabel ----
			cScaleLabel.setText("Scale:");
			panel1.add(cScaleLabel, "cell 0 5,gapx indent");

			//---- cScaleXLabel ----
			cScaleXLabel.setText("text");
			panel1.add(cScaleXLabel, "cell 1 5");

			//---- cScaleYLabel ----
			cScaleYLabel.setText("text");
			panel1.add(cScaleYLabel, "cell 2 5");

			//---- cRotationLabel ----
			cRotationLabel.setText("Rotation:");
			panel1.add(cRotationLabel, "cell 0 6,gapx indent");

			//---- cRotationDegreesLabel ----
			cRotationDegreesLabel.setText("text");
			panel1.add(cRotationDegreesLabel, "cell 1 6");
		}
		add(panel1, "cell 2 2 1 4,growy");

		//---- resetButton ----
		resetButton.setText("Reset");
		resetButton.addActionListener(e -> reset());
		add(resetButton, "cell 2 6,align left bottom,grow 0 0");
		add(painter, "cell 0 7 3 1,grow,width 600,height 400");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JSlider originXSlider;
	private JSlider originYSlider;
	private JSlider translateXSlider;
	private JSlider translateYSlider;
	private JSlider scaleXSlider;
	private JSlider scaleYSlider;
	private JSlider rotateSlider;
	private JLabel tScaleXLabel;
	private JLabel tScaleYLabel;
	private JLabel tShearXLabel;
	private JLabel tShearYLabel;
	private JLabel tTranslateXLabel;
	private JLabel tTranslateYLabel;
	private JLabel cScaleXLabel;
	private JLabel cScaleYLabel;
	private JLabel cRotationDegreesLabel;
	private FlatPaintingHiDPITest.HiDPI1xPainter painter;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class HiDPI1xPainter -----------------------------------------------

	public static class HiDPI1xPainter
		extends JComponent
	{
		int originX;
		int originY;
		int translateX;
		int translateY;
		int scaleX;
		int scaleY;
		int rotate;

		public HiDPI1xPainter() {
		}

		@Override
		protected void paintComponent( Graphics g ) {
			int width = getWidth();
			int height = getHeight();

			Graphics2D g2 = (Graphics2D) g.create();
			try {
				FlatUIUtils.setRenderingHints( g2 );

				g2.setColor( Color.blue );
				g2.drawRect( 0, 0, width - 1, height - 1 );

				g2.setColor( Color.cyan );
				g2.drawLine( 0, translateY, width, translateY );
				g2.drawLine( translateX, 0, translateX, height );

				g2.translate( translateX, translateY );
				g2.scale( scaleX / 100., scaleY / 100. );
				g2.rotate( Math.toRadians( rotate ) );

				g2.setColor( Color.red );
				g2.fillRect( originX, originY, 100, 50 );

				g2.setColor( Color.green );
				HiDPIUtils.paintAtScale1x( g2, originX, originY, 100, 50,
					(g2d, x2, y2, width2, height2, scaleFactor) -> {
						g2d.fillRect( x2 + 10, y2 + 10, width2 - 20, height2 - 20 );
					} );
			} finally {
				g2.dispose();
			}
		}
	}
}
