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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.StackUtils;

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
				case "Inter Semi Bold":	// Inter v3
				case "Inter SemiBold":	// Inter v4
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

			@Override
			public void fillRect( int x, int y, int width, int height ) {
				// fix hard coded black color in HRuleView.paint() of '<hr noshade>'
				if( super.getColor() == Color.black &&
					StackUtils.wasInvokedFrom( "javax.swing.text.html.HRuleView", "paint", 4 ) )
				{
					super.setColor( FlatLaf.isLafDark() ? Color.lightGray : Color.darkGray );
					super.fillRect( x, y, width, height );
					super.setColor( Color.black );
				} else
					super.fillRect( x, y, width, height );
			}
		};
	}

	/**
	 * Repaints the given component.
	 * <p>
	 * See {@link #repaint(Component, int, int, int, int)} for more details.
	 *
	 * @since 3.5
	 */
	public static void repaint( Component c ) {
		repaint( c, 0, 0, c.getWidth(), c.getHeight() );
	}

	/**
	 * Repaints the given component area.
	 * <p>
	 * See {@link #repaint(Component, int, int, int, int)} for more details.
	 *
	 * @since 3.5
	 */
	public static void repaint( Component c, Rectangle r ) {
		repaint( c, r.x, r.y, r.width, r.height );
	}

	/**
	 * Repaints the given component area.
	 * <p>
	 * Invokes {@link Component#repaint(int, int, int, int)} on the given component,
	 * <p>
	 * Use this method, instead of {@code Component.repaint(...)},
	 * to fix a problem in Swing when using scale factors that end on .25 or .75
	 * (e.g. 1.25, 1.75, 2.25, etc) and repainting single components, which may not
	 * repaint right and/or bottom 1px edge of component.
	 * <p>
	 * The problem may occur under following conditions:
	 * <ul>
	 *   <li>using Java 9 or later
	 *   <li>system scale factor is 125%, 175%, 225%, ...
	 *       (Windows only; Java on macOS and Linux does not support fractional scale factors)
	 *   <li>repaint whole component or right/bottom area of component
	 *   <li>component is opaque; or component is contained in a opaque container
	 *       that has same right/bottom bounds as component
	 *   <li>component has bounds that Java/Swing scales different when repainting components
	 * </ul>
	 *
	 * @since 3.5
	 */
	public static void repaint( Component c, int x, int y, int width, int height ) {
		// repaint given component area
		//   Always invoke repaint() on given component, even if also invoked (below)
		//   on one of its ancestors, for the case that component overrides that method.
		//   Also RepaintManager "merges" the two repaints into one.
		c.repaint( x, y, width, height );

		if( RepaintManager.currentManager( c ) instanceof HiDPIRepaintManager )
			return;

		// if necessary, also repaint given area in first ancestor that is larger than component
		// to avoid clipping issue (see needsSpecialRepaint())
		if( needsSpecialRepaint( c, x, y, width, height ) ) {
			int x2 = x + c.getX();
			int y2 = y + c.getY();
			for( Component p = c.getParent(); p != null; p = p.getParent() ) {
				x2 += p.getX();
				y2 += p.getY();
				if( x2 + width < p.getWidth() && y2 + height < p.getHeight() ) {
					p.repaint( x2, y2, width, height );
					break;
				}
			}
		}
	}

	/**
	 * There is a problem in Swing, when using scale factors that end on .25 or .75
	 * (e.g. 1.25, 1.75, 2.25, etc) and repainting single components, which may not
	 * repaint right and/or bottom 1px edge of component.
	 * <p>
	 * The component is first painted to an in-memory image,
	 * and then that image is copied to the screen.
	 * See {@code javax.swing.RepaintManager.PaintManager#paintDoubleBufferedFPScales()}.
	 * <p>
	 * There are two clipping rectangles involved when copying the image to the screen:
	 * {@code sun.java2d.SunGraphics2D#devClip} and
	 * {@code sun.java2d.SunGraphics2D#usrClip}.
	 * <p>
	 * {@code devClip} is the device clipping in physical pixels.
	 * It gets the bounds of the painting component, which is either the passed component,
	 * or if it is non-opaque, then the first opaque ancestor of the passed component.
	 * It is calculated in {@code sun.java2d.SunGraphics2D#constrain()} while
	 * getting a graphics context via {@link JComponent#getGraphics()}.
	 * <p>
	 * {@code usrClip} is the user clipping, which is set via {@link Graphics} clipping methods.
	 * This is done in {@code javax.swing.RepaintManager.PaintManager#paintDoubleBufferedFPScales()}.
	 * <p>
	 * The intersection of {@code devClip} and {@code usrClip}
	 * (computed in {@code sun.java2d.SunGraphics2D#validateCompClip()})
	 * is used to copy the image to the screen.
	 * <p>
	 * Unfortunately different scaling/rounding strategies are used to calculate
	 * the two clipping rectangles, which is the reason of the issue.
	 * <p>
	 * {@code devClip} (see {@code sun.java2d.SunGraphics2D#constrain()}):
	 * <pre>{@code
	 * int devX = (int) (x * scale);
	 * int devWidth = Math.round( width * scale )
	 * }</pre>
	 * {@code usrClip} (see {@code javax.swing.RepaintManager.PaintManager#paintDoubleBufferedFPScales()}):
	 * <pre>{@code
	 * int usrX = (int) Math.ceil( (x * scale) - 0.5 );
	 * int usrWidth = ((int) Math.ceil( ((x + width) * scale) - 0.5 )) - usrX;
	 * }</pre>
	 * X/Y coordinates are always rounded down for {@code devClip}, but rounded up for {@code usrClip}.
	 * Width/height calculation is also different.
	 */
	private static boolean needsSpecialRepaint( Component c, int x, int y, int width, int height ) {
		// no special repaint necessary for Java 8 or for macOS and Linux
		// (Java on those platforms does not support fractional scale factors)
		if( !SystemInfo.isJava_9_orLater || !SystemInfo.isWindows )
			return false;

		// check whether repaint area is empty or no component given
		// (same checks as in javax.swing.RepaintManager.addDirtyRegion0())
		if( width <= 0 || height <= 0 || c == null )
			return false;

		// check whether component has zero size
		// (same checks as in javax.swing.RepaintManager.addDirtyRegion0())
		int compWidth = c.getWidth();
		int compHeight = c.getHeight();
		if( compWidth <= 0 || compHeight <= 0 )
			return false;

		// check whether repaint area does span to right or bottom component edges
		// (in this case, {@code devClip} is always larger than {@code usrClip})
		if( x + width < compWidth && y + height < compHeight )
			return false;

		// if component is not opaque, Swing uses the first opaque ancestor for painting
		if( !c.isOpaque() ) {
			int x2 = x;
			int y2 = y;
			for( Component p = c.getParent(); p != null; p = p.getParent() ) {
				x2 += p.getX();
				y2 += p.getY();
				if( p.isOpaque() ) {
					// check whether repaint area does span to right or bottom edges
					// of the opaque ancestor component
					// (in this case, {@code devClip} is always larger than {@code usrClip})
					if( x2 + width < p.getWidth() && y2 + height < p.getHeight() )
						return false;
					break;
				}
			}
		}

		// check whether Special repaint is necessary for current scale factor
		// (doing this check late because it temporary allocates some memory)
		double scaleFactor = UIScale.getSystemScaleFactor( c.getGraphicsConfiguration() );
		double fraction = scaleFactor - (int) scaleFactor;
		if( fraction == 0 || fraction == 0.5 )
			return false;

		return true;
	}

	/**
	 * Installs a {@link HiDPIRepaintManager} on Windows when running in Java 9+,
	 * but only if default repaint manager is currently installed.
	 * <p>
	 * Invoke once on application startup.
	 * Compatible with all/other LaFs.
	 *
	 * @since 3.5
	 */
	public static void installHiDPIRepaintManager() {
		if( !SystemInfo.isJava_9_orLater || !SystemInfo.isWindows )
			return;

		RepaintManager manager = RepaintManager.currentManager( (Component) null );
		if( manager.getClass() == RepaintManager.class )
			RepaintManager.setCurrentManager( new HiDPIRepaintManager() );
	}

	/**
	 * Similar to {@link #repaint(Component, int, int, int, int)},
	 * but invokes callback instead of invoking {@link Component#repaint(int, int, int, int)}.
	 * <p>
	 * For use in custom repaint managers.
	 *
	 * @since 3.5
	 */
	public static void addDirtyRegion( JComponent c, int x, int y, int width, int height, DirtyRegionCallback callback ) {
		if( needsSpecialRepaint( c, x, y, width, height ) ) {
			int x2 = x + c.getX();
			int y2 = y + c.getY();
			for( Component p = c.getParent(); p != null; p = p.getParent() ) {
				if( x2 + width < p.getWidth() && y2 + height < p.getHeight() && p instanceof JComponent ) {
					callback.addDirtyRegion( (JComponent) p, x2, y2, width, height );
					return;
				}
				x2 += p.getX();
				y2 += p.getY();
			}
		}

		callback.addDirtyRegion( c, x, y, width, height );
	}

	//---- interface DirtyRegionCallback --------------------------------------

	/**
	 * For {@link HiDPIUtils#addDirtyRegion(JComponent, int, int, int, int, DirtyRegionCallback)}.
	 *
	 * @since 3.5
	 */
	public interface DirtyRegionCallback {
		void addDirtyRegion( JComponent c, int x, int y, int w, int h );
	}

	//---- class HiDPIRepaintManager ------------------------------------------

	/**
	 * A repaint manager that fixes a problem in Swing when repainting components
	 * at some scale factors (e.g. 125%, 175%, etc) on Windows.
	 * <p>
	 * Use {@link HiDPIUtils#installHiDPIRepaintManager()} to install it.
	 * <p>
	 * See {@link HiDPIUtils#repaint(Component, int, int, int, int)} for details.
	 *
	 * @since 3.5
	 */
	public static class HiDPIRepaintManager
		extends RepaintManager
	{
		@Override
		public void addDirtyRegion( JComponent c, int x, int y, int w, int h ) {
			HiDPIUtils.addDirtyRegion( c, x, y, w, h, super::addDirtyRegion );
		}
	}
}
