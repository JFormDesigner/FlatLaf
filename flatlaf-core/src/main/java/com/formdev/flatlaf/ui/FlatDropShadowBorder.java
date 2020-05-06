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

package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * Paints a drop shadow border around the component.
 *
 * @author Karl Tauber
 */
public class FlatDropShadowBorder
	extends FlatEmptyBorder
{
	private final Color shadowColor;
	private final int shadowSize;
	private final int cornerInset;
	private final int shadowAlpha;

	private Image shadowImage;
	private Color lastShadowColor;
	private double lastSystemScaleFactor;
	private float lastUserScaleFactor;

	public FlatDropShadowBorder() {
		this( null );
	}

	public FlatDropShadowBorder( Color shadowColor ) {
		this( shadowColor, 4, 4, 128 );
	}

	public FlatDropShadowBorder( Color shadowColor, int shadowSize, int cornerInset, int shadowAlpha ) {
		super( new Insets( 0, 0, shadowSize, shadowSize ) );
		this.shadowColor = shadowColor;
		this.shadowSize = shadowSize;
		this.cornerInset = cornerInset;
		this.shadowAlpha = shadowAlpha;
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		HiDPIUtils.paintAtScale1x( (Graphics2D) g, x, y, width, height, this::paintImpl );
	}

	private void paintImpl( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
		Color shadowColor = (this.shadowColor != null) ? this.shadowColor : g.getColor();
		int shadowSize = (int) Math.ceil( UIScale.scale( this.shadowSize ) * scaleFactor );
		int cornerInset = (int) Math.ceil( UIScale.scale( this.cornerInset ) * scaleFactor );

		// create and cache shadow image
		float userScaleFactor = UIScale.getUserScaleFactor();
		if( shadowImage == null ||
			!shadowColor.equals( lastShadowColor ) ||
			lastSystemScaleFactor != scaleFactor ||
			lastUserScaleFactor != userScaleFactor )
		{
			shadowImage = createShadowImage( shadowColor, shadowSize, shadowAlpha,
				(float) (scaleFactor * userScaleFactor) );
			lastShadowColor = shadowColor;
			lastSystemScaleFactor = scaleFactor;
			lastUserScaleFactor = userScaleFactor;
		}

/*debug
		int m = shadowImage.getWidth( null );
		g.setColor( Color.lightGray );
		g.drawRect( x - m - 1, y - m - 1, m + 1, m + 1 );
		g.setColor( Color.white );
		g.fillRect( x - m, y - m, m, m );
		g.drawImage( shadowImage, x - m, y - m, null );
debug*/

		int x1c = x + cornerInset;
		int y1c = y + cornerInset;
		int x1cs = x1c + shadowSize;
		int y1cs = y1c + shadowSize;

		int x2s = x + width;
		int y2s = y + height;
		int x2 = x2s - shadowSize;
		int y2 = y2s - shadowSize;

		int wh = (shadowSize * 2) - 1;
		int center = shadowSize - 1;

		// left-bottom edge
		g.drawImage( shadowImage, x1c, y2, x1cs, y2s,
			0, center, shadowSize, wh, null );

		// bottom shadow
		g.drawImage( shadowImage, x1cs, y2, x2, y2s,
			center, center, center + 1, wh, null );

		// right-bottom edge
		g.drawImage( shadowImage, x2, y2, x2s, y2s,
			center, center, wh, wh, null );

		// right shadow
		g.drawImage( shadowImage, x2, y1cs, x2s, y2,
			center, center, wh, center + 1, null );

		// right-top edge
		g.drawImage( shadowImage, x2, y1c, x2s, y1cs,
			center, 0, wh, shadowSize, null );
	}

	private static BufferedImage createShadowImage( Color shadowColor, int shadowSize,
		int shadowAlpha, float scaleFactor )
	{
		int shadowRGB = shadowColor.getRGB() & 0xffffff;
		Color startColor = new Color( shadowRGB | ((shadowAlpha & 0xff) << 24), true );
		Color midColor = new Color( shadowRGB | (((shadowAlpha / 2) & 0xff) << 24), true );
		Color endColor = new Color( shadowRGB, true );

		int wh = (shadowSize * 2) - 1;
		int center = shadowSize - 1;

		RadialGradientPaint p = new RadialGradientPaint( center, center,
			shadowSize - (0.75f * scaleFactor),
			new float[] { 0, 0.35f, 1 },
			new Color[] { startColor, midColor, endColor } );

		BufferedImage image = new BufferedImage( wh, wh, BufferedImage.TYPE_INT_ARGB );

		Graphics2D g = image.createGraphics();
		try {
			g.setPaint( p );
			g.fillRect( 0, 0, wh, wh );
		} finally {
			g.dispose();
		}

		return image;
	}
}
