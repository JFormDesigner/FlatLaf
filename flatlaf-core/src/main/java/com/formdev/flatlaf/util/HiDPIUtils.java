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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import javax.swing.JComponent;
import com.formdev.flatlaf.FlatSystemProperties;

/**
 * @author Karl Tauber
 */
public class HiDPIUtils
{
	public interface Painter {
		void paint( Graphics2D g, int x, int y, int width, int height, double scaleFactor );
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
		AffineTransform t = g.getTransform();

		// get scale X/Y and shear X/Y
		final double scaleX = t.getScaleX();
		final double scaleY = t.getScaleY();
		final double shearX = t.getShearX();
		final double shearY = t.getShearY();

		// check whether rotated
		// (also check for negative scale X/Y because shear X/Y are zero for 180 degrees rotation)
		boolean rotated = (shearX != 0 || shearY != 0 || scaleX <= 0 || scaleY <= 0);

		// calculate non rotated scale factors
		final double realScaleX, realScaleY;
		if( rotated ) {
			// resulting scale X/Y values are always positive
			realScaleX = Math.hypot( scaleX, shearX );
			realScaleY = Math.hypot( scaleY, shearY );
		} else {
			// make scale X/Y positive
			realScaleX = Math.abs( scaleX );
			realScaleY = Math.abs( scaleY );
		}

		// check whether scaled
		if( realScaleX == 1 && realScaleY == 1 ) {
			painter.paint( g, x, y, width, height, 1 );
			return;
		}

		// calculate x and y (this is equal to t.translate( x, y ))
		double px = (x * scaleX) + (y * shearX) + t.getTranslateX();
		double py = (y * scaleY) + (x * shearY) + t.getTranslateY();

		// scale rectangle
		Rectangle2D.Double scaledRect = scale( realScaleX, realScaleY, px, py, width, height );

		try {
			// unscale to factor 1.0, keep rotation and move origin (to whole numbers)
			AffineTransform t1x;
			if( rotated ) {
				t1x = new AffineTransform( scaleX, shearY, shearX, scaleY,
					Math.floor( scaledRect.x ), Math.floor( scaledRect.y ) );
				t1x.scale( 1. / realScaleX, 1. / realScaleY );
			} else
				t1x = new AffineTransform( 1, 0, 0, 1, Math.floor( scaledRect.x ), Math.floor( scaledRect.y ) );
			g.setTransform( t1x );

			int swidth = (int) scaledRect.width;
			int sheight = (int) scaledRect.height;

			// paint
			painter.paint( g, 0, 0, swidth, sheight, realScaleX );
		} finally {
			// restore original transform
			g.setTransform( t );
		}
	}

	/**
	 * Scales a rectangle in the same way as the JRE does in
	 * sun.java2d.pipe.PixelToParallelogramConverter.fillRectangle(),
	 * which is used by Graphics.fillRect().
	 */
	private static Rectangle2D.Double scale( double scaleX, double scaleY, double px, double py, int width, int height ) {
		double newX = normalize( px );
		double newY = normalize( py );
		double newWidth  = normalize( px + (width  * scaleX) ) - newX;
		double newHeight = normalize( py + (height * scaleY) ) - newY;

		return new Rectangle2D.Double( newX, newY, newWidth, newHeight );
	}

	private static double normalize( double value ) {
		return Math.floor( value + 0.25 ) + 0.25;
	}


	private static Boolean useTextYCorrection;

	private static boolean useTextYCorrection() {
		if( useTextYCorrection == null )
			useTextYCorrection = FlatSystemProperties.getBoolean( FlatSystemProperties.USE_TEXT_Y_CORRECTION, true );
		return useTextYCorrection;
	}

	/**
	 * When painting text on HiDPI screens and the JRE scales, then the text is
	 * painted too far down on some operating systems.
	 * The higher the system scale factor is, the more.
	 * <p>
	 * This method computes a correction value for the Y position.
	 */
	public static float computeTextYCorrection( Graphics2D g ) {
		if( !useTextYCorrection() || !SystemInfo.isWindows )
			return 0;

		if( !SystemInfo.isJava_9_orLater ) {
			// Java 8
			float scaleFactor = getUserScaleFactor();
			if( scaleFactor > 1 ) {
				switch( g.getFont().getFamily() ) {
					case "Segoe UI":
					case "Segoe UI Light":
					case "Segoe UI Semibold":
						return -((scaleFactor == 2.25f || scaleFactor == 4f ? 0.875f : 0.625f) * scaleFactor);

					case "Noto Sans":
					case "Open Sans":
						return -(0.3f * scaleFactor);

					case "Verdana":
						return -((scaleFactor < 2 ? 0.4f : 0.3f) * scaleFactor);
				}
			}
		} else {
			// Java 9 and later

			// Text is painted at slightly different Y positions depending on scale factor
			// and Y position of component.
			// The exact reason is not yet known (to me), but there are several factors:
			// - fractional scale factors result in fractional component Y device coordinates
			// - fractional text Y device coordinates are rounded for horizontal lines of characters
			// - maybe different rounding methods for drawing primitives (e.g. rectangle) and text
			// - Java adds 0.5 to X/Y positions before drawing string in BufferedTextPipe.enqueueGlyphList()

			// this is not the optimal solution, but works very good in most cases
			// (tested with class FlatPaintingStringTest on Windows 11)

			switch( g.getFont().getFamily() ) {
				case "Segoe UI":
				case "Segoe UI Light":
				case "Segoe UI Semibold":
				case "Verdana":
				case Font.DIALOG:
				case Font.SANS_SERIF:
					return correctionForScaleY( g, CORRECTION_SEGOE_UI );

				case "Tahoma":
					return correctionForScaleY( g, CORRECTION_TAHOMA );

				case "Inter":
				case "Inter Light":
				case "Inter Semi Bold":
				case "Roboto":
				case "Roboto Light":
				case "Roboto Medium":
					return correctionForScaleY( g, CORRECTION_INTER );

				case "Noto Sans":
				case "Open Sans":
					return correctionForScaleY( g, CORRECTION_OPEN_SANS );
			}
		}

		return 0;
	}

	private static final float[]
		SCALE_FACTORS        = {  1.25f,   1.5f,    1.75f,   2f,      2.25f,   2.5f,    3f,      3.5f,    4f     },

		CORRECTION_SEGOE_UI  = { -0.5f,   -0.5f,   -0.625f, -0.75f,  -0.75f,  -0.75f,  -0.75f,  -0.75f,  -0.875f },
		CORRECTION_TAHOMA    = { -0.25f,  -0.25f,  -0.25f,  -0f,     -0.125f, -0.125f, -0.125f, -0.125f, -0f     },
		CORRECTION_INTER     = { -0.25f,  -0.25f,  -0.25f,  -0f,     -0.125f, -0.125f, -0f,     -0.25f,  -0f     },
		CORRECTION_OPEN_SANS = { -0.5f,   -0.25f,  -0.25f,  -0f,     -0.25f,  -0.25f,  -0f,     -0.25f,  -0.25f  };

	private static float correctionForScaleY( Graphics2D g, float[] correction ) {
		if( correction.length != 9 )
			throw new IllegalArgumentException();

		double scaleY = g.getTransform().getScaleY();
		return (scaleY < 1.25) ? 0 : correction[scaleFactor2index( (float) scaleY )];
	}

	private static int scaleFactor2index( float scaleFactor ) {
		for( int i = 0; i < SCALE_FACTORS.length; i++ ) {
			if( scaleFactor <= SCALE_FACTORS[i] )
				return i;
		}
		return SCALE_FACTORS.length - 1;
	}

	private static Boolean useDebugScaleFactor;

	private static boolean useDebugScaleFactor() {
		if( useDebugScaleFactor == null )
			useDebugScaleFactor = FlatSystemProperties.getBoolean( "FlatLaf.debug.HiDPIUtils.useDebugScaleFactor", false );
		return useDebugScaleFactor;
	}

	private static float getUserScaleFactor() {
		return !useDebugScaleFactor()
			? UIScale.getUserScaleFactor()
			: Float.parseFloat( System.getProperty( "FlatLaf.debug.HiDPIUtils.debugScaleFactor", "1" ) );
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
