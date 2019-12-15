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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
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

	public static void paintAtScale1x( Graphics2D g, int x, int y, int width, int height, Painter painter ) {
		paintAtScale1x( g, x, y, width, height, UIScale.getSystemScaleFactor( g ), painter );
	}

	/**
	 * Paint at system scale factor 1x to avoid rounding issues at 125%, 150% and 175% scaling.
	 * <p>
	 * Scales the given Graphics2D down to 100% and invokes the
	 * given painter passing scaled x, y, width and height.
	 * <p>
	 * Uses the same scaling calculation as the JRE uses.
	 */
	public static void paintAtScale1x( Graphics2D g, int x, int y, int width, int height,
		double scaleFactor, Painter painter )
	{
		if( scaleFactor == 1 ) {
			painter.paint( g, x, y, width, height, 1 );
			return;
		}

		// save original transform
		AffineTransform transform = g.getTransform();

		// scale rectangle
		Rectangle2D.Double scaledRect = scale( transform, x, y, width, height );

		try {
			// unscale to factor 1.0
			double scale = 1.0 / scaleFactor;
			g.scale( scale, scale );

			// compute origin delta x/y
			double dx = Math.floor( scaledRect.x ) - transform.getTranslateX();
			double dy = Math.floor( scaledRect.y ) - transform.getTranslateY();

			// move origin to make sure that origin x/y are at whole numbers
			if( dx != 0 || dy != 0 )
				g.translate( dx, dy );

			int swidth = (int) scaledRect.width;
			int sheight = (int) scaledRect.height;

			// paint
			painter.paint( g, 0, 0, swidth, sheight, scaleFactor );
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
}
