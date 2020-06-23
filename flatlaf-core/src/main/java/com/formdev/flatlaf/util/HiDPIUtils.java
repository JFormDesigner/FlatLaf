/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import javax.swing.JComponent;

/**
 * @author Karl Tauber
 */
public class HiDPIUtils
{
	public interface Painter {
		public void paint( Graphics2D g, int x, int y, int width, int height, double scaleFactor );
	}

	public static void paintAtScale1x( Graphics2D g, JComponent c, Painter painter ) {
		paintAtScale1x( g, 0, 0, c.getWidth(), c.getHeight(), painter );
	}

	/**
	 * Paint at system scale factor 1x to avoid rounding issues at 125%, 150% and 175% scaling.
	 * <p>
	 * Scales the given Graphics2D down to 100% and invokes the
	 * given painter passing scaled x, y, width and height.
	 * <p>
	 * Uses the same scaling calculation as the JRE uses.
	 */
	public static void paintAtScale1x( Graphics2D g, int x, int y, int width, int height, Painter painter ) {
		// save original transform
		AffineTransform transform = g.getTransform();

		// check whether scaled
		if( transform.getScaleX() == 1 && transform.getScaleY() == 1 ) {
			painter.paint( g, x, y, width, height, 1 );
			return;
		}

		// scale rectangle
		Rectangle2D.Double scaledRect = scale( transform, x, y, width, height );

		try {
			// unscale to factor 1.0 and move origin (to whole numbers)
			g.setTransform( new AffineTransform( 1, 0, 0, 1,
				Math.floor( scaledRect.x ), Math.floor( scaledRect.y ) ) );

			int swidth = (int) scaledRect.width;
			int sheight = (int) scaledRect.height;

			// paint
			painter.paint( g, 0, 0, swidth, sheight, transform.getScaleX() );
		} finally {
			// restore original transform
			g.setTransform( transform );
		}
	}

	/**
	 * Scales a rectangle in the same way as the JRE does in
	 * sun.java2d.pipe.PixelToParallelogramConverter.fillRectangle(),
	 * which is used by Graphics.fillRect().
	 */
	private static Rectangle2D.Double scale( AffineTransform transform, int x, int y, int width, int height ) {
		double dx1 = transform.getScaleX();
		double dy2 = transform.getScaleY();
		double px = x * dx1 + transform.getTranslateX();
		double py = y * dy2 + transform.getTranslateY();
		dx1 *= width;
		dy2 *= height;

		double newx = normalize( px );
		double newy = normalize( py );
		dx1 = normalize( px + dx1 ) - newx;
		dy2 = normalize( py + dy2 ) - newy;

		return new Rectangle2D.Double( newx, newy, dx1, dy2 );
	}

	private static double normalize( double value ) {
		return Math.floor( value + 0.25 ) + 0.25;
	}


	private static Boolean useTextYCorrection;

	private static boolean useTextYCorrection() {
		if( useTextYCorrection == null )
			useTextYCorrection = Boolean.valueOf( System.getProperty( "flatlaf.useTextYCorrection", "true" ) );
		return useTextYCorrection;
	}

	/**
	 * When painting text on HiDPI screens and the JRE scales, then the text is
	 * painted too far down on some operating systems.
	 * The higher the system scale factor is, the more.
	 * <p>
	 * This methods computes a correction value for the Y position.
	 */
	public static float computeTextYCorrection( Graphics2D g ) {
		if( !useTextYCorrection() || !SystemInfo.IS_WINDOWS )
			return 0;

		if( !SystemInfo.IS_JAVA_9_OR_LATER )
			return UIScale.getUserScaleFactor() > 1 ? -UIScale.scale( 0.625f ) : 0;

		AffineTransform t = g.getTransform();
		double scaleY = t.getScaleY();
		if( scaleY < 1.25 )
			return 0;

		// Text is painted at slightly different Y positions depending on scale factor
		// and Y position of component.
		// The exact reason is not yet known (to me), but there are several factors:
		// - fractional scale factors result in fractional component Y device coordinates
		// - fractional text Y device coordinates are rounded for horizontal lines of characters
		// - maybe different rounding methods for drawing primitives (e.g. rectangle) and text
		// - Java adds 0.5 to X/Y positions in before drawing string in BufferedTextPipe.enqueueGlyphList()

		// this is not the optimal solution, but works very good in most cases
		// (tested with class FlatPaintingStringTest on Windows 10 with font "Segoe UI")
		if( scaleY <= 1.25 )
			return -0.875f;
		if( scaleY <= 1.5 )
			return -0.625f;
		if( scaleY <= 1.75 )
			return -0.875f;
		if( scaleY <= 2.0 )
			return -0.75f;
		if( scaleY <= 2.25 )
			return -0.875f;
		if( scaleY <= 3.5 )
			return -0.75f;
		return -0.875f;
	}

	/**
	 * Applies Y correction and draws the given string at the specified location.
	 * The provided component is used to query text properties and anti-aliasing hints.
	 * <p>
	 * Use this method instead of {@link Graphics#drawString(String, int, int)} for correct anti-aliasing.
	 * <p>
	 * Replacement for {@code SwingUtilities2.drawString()}.
	 */
	public static void drawStringWithYCorrection( JComponent c, Graphics2D g, String text, int x, int y ) {
		drawStringUnderlineCharAtWithYCorrection( c, g, text, -1, x, y );
	}

	/**
	 * Applies Y correction and draws the given string at the specified location underlining the specified character.
	 * The provided component is used to query text properties and anti-aliasing hints.
	 * <p>
	 * Replacement for {@code SwingUtilities2.drawStringUnderlineCharAt()}.
	 */
	public static void drawStringUnderlineCharAtWithYCorrection( JComponent c,
		Graphics2D g, String text, int underlinedIndex, int x, int y )
	{
		float yCorrection = computeTextYCorrection( g );
		if( yCorrection != 0 ) {
			g.translate( 0, yCorrection );
			JavaCompatibility.drawStringUnderlineCharAt( c, g, text, underlinedIndex, x, y );
			g.translate( 0, -yCorrection );
		} else
			JavaCompatibility.drawStringUnderlineCharAt( c, g, text, underlinedIndex, x, y );
	}

	/**
	 * Creates a graphics object and applies Y correction to string drawing methods.
	 * If no Y correction is necessary, the passed in graphics object is returned.
	 */
	public static Graphics2D createGraphicsTextYCorrection( Graphics2D g ) {
		float yCorrection = computeTextYCorrection( g );
		if( yCorrection == 0 )
			return g;

		return new Graphics2DProxy( g ) {
			@Override
			public void drawString( String str, int x, int y ) {
				super.drawString( str, x, y + yCorrection );
			}

			@Override
			public void drawString( String str, float x, float y ) {
				super.drawString( str, x, y + yCorrection );
			}

			@Override
			public void drawString( AttributedCharacterIterator iterator, int x, int y ) {
				super.drawString( iterator, x, y + yCorrection );
			}

			@Override
			public void drawString( AttributedCharacterIterator iterator, float x, float y ) {
				super.drawString( iterator, x, y + yCorrection );
			}

			@Override
			public void drawChars( char[] data, int offset, int length, int x, int y ) {
				super.drawChars( data, offset, length, x, Math.round( y + yCorrection ) );
			}

			@Override
			public void drawBytes( byte[] data, int offset, int length, int x, int y ) {
				super.drawBytes( data, offset, length, x, Math.round( y + yCorrection ) );
			}

			@Override
			public void drawGlyphVector( GlyphVector g, float x, float y ) {
				super.drawGlyphVector( g, x, y + yCorrection );
			}
		};
	}
}
