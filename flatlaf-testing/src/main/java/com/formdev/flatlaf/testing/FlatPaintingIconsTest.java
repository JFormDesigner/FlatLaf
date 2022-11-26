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
import java.util.Hashtable;
import javax.swing.*;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.icons.*;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatPaintingIconsTest
	extends JPanel
{
	private int scale = 16;
	private boolean paintPixels;
	private float paintPixelsScale = 1;
	private Timer timer = null;

	public static void main( String[] args ) {
		System.setProperty( FlatSystemProperties.UI_SCALE, "1x" );
		System.setProperty( "sun.java2d.uiScale", "1x" );

		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatPaintingIconsTest" );
			frame.showFrame( FlatPaintingIconsTest::new );
		} );
	}

	FlatPaintingIconsTest() {
		initComponents();

		Hashtable<Integer, JLabel> labels = new Hashtable<>();
		for( int i = 100; i <= 600; i += 100 )
			labels.put( i, new JLabel( String.format( "%dx", i / 100 ) ) );
		pixelsScaleSlider.setLabelTable( labels );

		scrollPane.getHorizontalScrollBar().setUnitIncrement( UIScale.scale( 25 ) );
		scrollPane.getVerticalScrollBar().setUnitIncrement( UIScale.scale( 25 ) );

		addIconPainter( new FlatFileViewDirectoryIcon(), false );
		addIconPainter( new FlatFileViewFileIcon(), false );
		addIconPainter( new FlatFileViewComputerIcon(), false );
		addIconPainter( new FlatFileViewHardDriveIcon(), false );
		addIconPainter( new FlatFileViewFloppyDriveIcon(), true );

		addIconPainter( new FlatFileChooserNewFolderIcon(), false );
		addIconPainter( new FlatFileChooserUpFolderIcon(), false );
		addIconPainter( new FlatFileChooserHomeFolderIcon(), false );
		addIconPainter( new FlatFileChooserDetailsViewIcon(), false );
		addIconPainter( new FlatFileChooserListViewIcon(), true );

		addIconPainter( new FlatTreeClosedIcon(), false );
		addIconPainter( new FlatTreeOpenIcon(), false );
		addIconPainter( new FlatTreeLeafIcon(), false );
		addIconPainter( new FlatTreeCollapsedIcon(), false );
		addIconPainter( new FlatTreeExpandedIcon(), true );

		addIconPainter( new FlatSearchIcon(), false );
		addIconPainter( new FlatSearchWithHistoryIcon(), false );
		addIconPainter( new FlatClearIcon(), false );
		addIconPainter( new FlatRevealIcon(), false );
		addIconPainter( new FlatCapsLockIcon(), true );


		addIconPainter( new FlatOptionPaneErrorIcon(), false );
		addIconPainter( new FlatOptionPaneInformationIcon(), false );
		addIconPainter( new FlatOptionPaneWarningIcon(), false );
		addIconPainter( new FlatOptionPaneQuestionIcon(), false );
		addIconPainter( new FlatHelpButtonIcon(), true );
/*
		addIconPainter( new FlatAscendingSortIcon(), false );
		addIconPainter( new FlatDescendingSortIcon(), false );
		addIconPainter( new FlatMenuArrowIcon(), true );

		addIconPainter( new FlatWindowIconifyIcon(), false );
		addIconPainter( new FlatWindowMaximizeIcon(), false );
		addIconPainter( new FlatWindowRestoreIcon(), false );
		addIconPainter( new FlatWindowCloseIcon(), false );
		addIconPainter( new FlatTabbedPaneCloseIcon(), true );

		addIconPainter( new FlatInternalFrameIconifyIcon(), false );
		addIconPainter( new FlatInternalFrameMaximizeIcon(), false );
		addIconPainter( new FlatInternalFrameRestoreIcon(), false );
		addIconPainter( new FlatInternalFrameCloseIcon(), true );
*/
		// repaint to see code changes immediately when running in debugger
		timer = new Timer( 500, e -> {
			// stop timer to allow application to exit
			if( !isDisplayable() )
				timer.stop();

			repaint();
		} );
		timer.start();
	}

	private void addIconPainter( Icon icon, boolean wrap ) {
		String name = icon.getClass().getSimpleName();
		panel.add( new JLabel( name ), "split 2, flowy" );
		panel.add( new IconPainter( icon ), wrap ? "wrap" : null );
	}

	private void scaleChanged() {
		scale = Math.max( scaleSlider.getValue(), 1 );
		panel.revalidate();
		panel.repaint();
	}

	private void pixelsChanged() {
		paintPixels = pixelsCheckBox.isSelected();
		pixelsScaleLabel.setEnabled( paintPixels );
		pixelsScaleSlider.setEnabled( paintPixels );
		panel.repaint();
	}

	private void pixelsScaleChanged() {
		paintPixelsScale = pixelsScaleSlider.getValue() / 100f;
		panel.repaint();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		scrollPane = new JScrollPane();
		panel = new JPanel();
		JPanel panel1 = new JPanel();
		JLabel scaleLabel = new JLabel();
		scaleSlider = new JSlider();
		pixelsCheckBox = new JCheckBox();
		pixelsScaleLabel = new JLabel();
		pixelsScaleSlider = new JSlider();

		//======== this ========
		setLayout(new BorderLayout());

		//======== scrollPane ========
		{
			scrollPane.setBorder(null);

			//======== panel ========
			{
				panel.setLayout(new MigLayout(
					"insets dialog,hidemode 3",
					// columns
					"[left]",
					// rows
					"[top]"));
			}
			scrollPane.setViewportView(panel);
		}
		add(scrollPane, BorderLayout.CENTER);

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[grow,fill]para" +
				"[fill]",
				// rows
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
			pixelsCheckBox.addActionListener(e -> pixelsChanged());
			panel1.add(pixelsCheckBox, "cell 2 0");

			//---- pixelsScaleLabel ----
			pixelsScaleLabel.setText("Scale:");
			pixelsScaleLabel.setEnabled(false);
			pixelsScaleLabel.setLabelFor(pixelsScaleSlider);
			pixelsScaleLabel.setDisplayedMnemonic('C');
			panel1.add(pixelsScaleLabel, "cell 2 0");

			//---- pixelsScaleSlider ----
			pixelsScaleSlider.setMinimum(100);
			pixelsScaleSlider.setMaximum(600);
			pixelsScaleSlider.setMinorTickSpacing(25);
			pixelsScaleSlider.setMajorTickSpacing(100);
			pixelsScaleSlider.setSnapToTicks(true);
			pixelsScaleSlider.setPaintTicks(true);
			pixelsScaleSlider.setPaintLabels(true);
			pixelsScaleSlider.setEnabled(false);
			pixelsScaleSlider.addChangeListener(e -> pixelsScaleChanged());
			panel1.add(pixelsScaleSlider, "cell 2 0");
		}
		add(panel1, BorderLayout.NORTH);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrollPane;
	private JPanel panel;
	private JSlider scaleSlider;
	private JCheckBox pixelsCheckBox;
	private JLabel pixelsScaleLabel;
	private JSlider pixelsScaleSlider;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class IconPainter --------------------------------------------------

	public class IconPainter
		extends JComponent
	{
		private final Icon icon;

		public IconPainter( Icon icon ) {
			this.icon = icon;
		}

		private int getScale() {
			int iconWidth = icon.getIconWidth();
			return iconWidth <= 16 ? scale
				: iconWidth <= 24 ? (int) (scale * 0.75)
				: iconWidth <= 32 ? (scale / 2)
				: (int) (scale * (16. / iconWidth));
		}

		@Override
		public Dimension getPreferredSize() {
			int scale = getScale();
			return UIScale.scale( new Dimension( icon.getIconWidth() * scale, icon.getIconHeight() * scale ) );
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		@Override
		protected void paintComponent( Graphics g ) {
			Graphics2D g2 = (Graphics2D) g;
			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g2 );
			int scale = getScale();

			// paint icon scaled
			if( !paintPixels ) {
				// paint icon as vector
				AffineTransform oldTransform = g2.getTransform();
				g2.scale( scale, scale );
				icon.paintIcon( this, g2, 0, 0 );
				g2.setTransform( oldTransform );
			} else {
				// paint icon as pixels
				int width = Math.round( icon.getIconWidth() * paintPixelsScale );
				int height = Math.round( icon.getIconHeight() * paintPixelsScale );

				// paint icon to buffered image
				BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
				Graphics2D bg = bi.createGraphics();
				try {
					FlatUIUtils.setRenderingHints( bg );

					bg.scale( paintPixelsScale, paintPixelsScale );
					icon.paintIcon( this, bg, 0, 0 );
				} finally {
					bg.dispose();
				}

				// draw scaled-up image
				g2.drawImage( bi, 0, 0, getWidth(), getHeight(), null );
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
	}
}
