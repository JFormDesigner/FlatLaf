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

package com.formdev.flatlaf.themeeditor;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.HSLColor;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * @author Karl Tauber
 */
class FlatColorPipette
{
	/**
	 *
	 *
	 * @throws AWTException if platform does not allow using robot (e.g. if headless)
	 * @throws UnsupportedOperationException if platform does not support translucent window
	 */
	static void pick( Window owner, boolean modal,
			Consumer<Color> hoverCallback, Consumer<Color> pickCallback )
		throws AWTException, UnsupportedOperationException
	{
		InvisiblePickWindow pickWindow = new InvisiblePickWindow( owner, modal, hoverCallback, pickCallback );
		pickWindow.setVisible( true );
	}

	//---- class InvisiblePickWindow ------------------------------------------

	/**
	 * An invisible window used to receive mouse and keyboard events for the whole screen.
	 */
	private static class InvisiblePickWindow
		extends JDialog
	{
		private final Consumer<Color> hoverCallback;
		private final Consumer<Color> pickCallback;

		private final Robot robot;
		private final Magnifier magnifier;

		private int lastX;
		private int lastY;
		private Color lastHoverColor;

		InvisiblePickWindow( Window owner, boolean modal,
				Consumer<Color> hoverCallback, Consumer<Color> pickCallback )
			throws AWTException, UnsupportedOperationException
		{
			super( owner, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS );
			this.hoverCallback = hoverCallback;
			this.pickCallback = pickCallback;

			setAlwaysOnTop( true );
			setUndecorated( true );
			// macOS: windows with opacity smaller than 0.05 does not receive
			//        mouse clicked/pressed/released events (but mouse moved events)
			setOpacity( SystemInfo.isMacOS ? 0.05f : 0.005f );
			GraphicsConfiguration gc = owner.getGraphicsConfiguration();
			setBounds( (gc != null) ? gc.getBounds() : new Rectangle( Toolkit.getDefaultToolkit().getScreenSize() ) );

			robot = (gc != null) ? new Robot( gc.getDevice() ) : new Robot();
			magnifier = new Magnifier( this, robot );

			MouseAdapter mouseListener = new MouseAdapter() {
				@Override
				public void mouseMoved( MouseEvent e ) {
					// adding location of pick window is necessary for secondary screens
					lastX = e.getX() + getX();
					lastY = e.getY() + getY();

					// get color at mouse location
					// (temporary change opacity to zero to get correct color from robot)
					float oldOpacity = getOpacity();
					setOpacity( 0 );
					if( SystemInfo.isMacOS )
						robot.delay( 20 ); // give macOS some time to update the opacity
					Color color = robot.getPixelColor( lastX, lastY );
					setOpacity( oldOpacity );

					hover( color );
					magnifier.update( lastX, lastY, color );
				}

				@Override
				public void mouseClicked( MouseEvent e ) {
					dispose();

					Color color = null;
					if( SwingUtilities.isLeftMouseButton( e ) ) {
						// on macOS, robot not always returns correct color
						// in mouse clicked event (sometimes black; sometimes
						// includes opacity of disposed window)
						// --> use last hover color on macOS
						color = SystemInfo.isMacOS
							? lastHoverColor
							: robot.getPixelColor( e.getX() + getX(), e.getY() + getY() );
					}
					pick( color );
				}
			};

			KeyAdapter keyListener = new KeyAdapter() {
				@Override
				public void keyPressed( KeyEvent e ) {
					switch( e.getKeyCode() ) {
						case KeyEvent.VK_ESCAPE: dispose(); pick( null ); break;

						// move mouse one pixel using arrow keys
						case KeyEvent.VK_LEFT:  robot.mouseMove( lastX - 1, lastY ); break;
						case KeyEvent.VK_RIGHT: robot.mouseMove( lastX + 1, lastY ); break;
						case KeyEvent.VK_UP:    robot.mouseMove( lastX, lastY - 1 ); break;
						case KeyEvent.VK_DOWN:  robot.mouseMove( lastX, lastY + 1 ); break;
					}
				}
			};

			addMouseListener( mouseListener );
			addMouseMotionListener( mouseListener );
			addKeyListener( keyListener );

			magnifier.addMouseListener( mouseListener );
			magnifier.addMouseMotionListener( mouseListener );
			magnifier.addKeyListener( keyListener );

			magnifier.pack();
		}

		private void hover( Color color ) {
			if( hoverCallback == null || color == null || color.equals( lastHoverColor ) )
				return;

			lastHoverColor = color;

			EventQueue.invokeLater( () -> {
				hoverCallback.accept( color );
			} );
		}

		private void pick( Color color ) {
			EventQueue.invokeLater( () -> {
				pickCallback.accept( color );
			} );
		}
	}

	//---- class Magnifier ----------------------------------------------------

	private static class Magnifier
		extends JWindow
	{
		private final Window owner;
		private final Robot robot;

		private final MagnifierView view;
		private final JLabel infoLabel;

		private final int zoom;
		private final int pixels = 16;
		private Color colorAtMouse;
		private BufferedImage image;

		public Magnifier( Window owner, Robot robot ) {
			super( owner );
			this.owner = owner;
			this.robot = robot;

			zoom = UIScale.scale( 16 );

			getRootPane().setBorder( new FlatLineBorder( new Insets( 2, 2, 2, 2 ), Color.red, 2, 0 ) );

			view = new MagnifierView();
			view.setPreferredSize( new Dimension( pixels * zoom, pixels * zoom ) );

			infoLabel = new JLabel( "#" );
			infoLabel.setIcon(  new ColorIcon() );
			infoLabel.setBorder( new FlatEmptyBorder( 4, 4, 4, 4 ) );

			add( view, BorderLayout.CENTER );
			add( infoLabel, BorderLayout.SOUTH );
		}

		void update( int x, int y, Color colorAtXY ) {
			colorAtMouse = colorAtXY;

			// capture screen at mouse location
			image = robot.createScreenCapture( new Rectangle( x - (pixels / 2), y - (pixels / 2), pixels, pixels ) );

			// update color in info label
			HSLColor hslColor = new HSLColor( colorAtMouse );
			int hue = Math.round( hslColor.getHue() );
			int saturation = Math.round( hslColor.getSaturation() );
			int luminance = Math.round( hslColor.getLuminance() );
			infoLabel.setText( String.format( "#%06x   HSL %d %d %d",
				colorAtMouse.getRGB() & 0xffffff, hue, saturation, luminance ) );

			// place bottom-right to mouse location
			int mx = x + UIScale.scale( 32 );
			int my = y + UIScale.scale( 32 );

			// make sure that it is within screen
			if( mx + getWidth() > owner.getX() + owner.getWidth() )
				mx = x - getWidth() - UIScale.scale( 32 );
			if( my + getHeight() > owner.getY() + owner.getHeight() )
				my = y - getHeight() - UIScale.scale( 32 );

			setLocation( mx, my );
			setVisible( true );
			repaint();
		}

		//---- class MagnifierView ----

		private class MagnifierView
			extends JComponent
		{
			@Override
			public void paint( Graphics g ) {
				int width = getWidth();
				int height = getHeight();

				if( image != null )
					g.drawImage( image, 0, 0, width, height, null );

				int xy = (pixels / 2) * zoom;
				g.setColor( Color.red );
				((Graphics2D)g).setStroke( new BasicStroke( 2 ) );
				g.drawRect( xy - 1, xy - 1, zoom + 2, zoom + 2 );
			}
		}

		//---- class ColorIcon ----

		private class ColorIcon
			extends FlatAbstractIcon
		{
			private static final int WIDTH = 64;
			private static final int HEIGHT = 16;

			public ColorIcon() {
				super( WIDTH, HEIGHT, null );
			}

			@Override
			protected void paintIcon( Component c, Graphics2D g ) {
				g.setColor( colorAtMouse );
				g.fillRect( 0, 0, WIDTH, HEIGHT );
			}
		}
	}
}
