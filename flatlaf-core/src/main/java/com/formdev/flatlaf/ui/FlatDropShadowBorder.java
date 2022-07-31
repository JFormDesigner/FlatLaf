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
import java.util.Map;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableBorder;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * Paints a drop shadow border around the component.
 * Supports 1-sided, 2-side, 3-sided or 4-sided drop shadows.
 * <p>
 * The shadow insets allow specifying drop shadow thickness for each side.
 * A zero or negative value hides the drop shadow on that side.
 * A negative value can be used to indent the drop shadow on corners.
 * E.g. -4 on left indents drop shadow at top-left and bottom-left corners by 4 pixels.
 *
 * @author Karl Tauber
 */
public class FlatDropShadowBorder
	extends FlatEmptyBorder
	implements StyleableBorder
{
	@Styleable protected Color shadowColor;
	@Styleable protected Insets shadowInsets;
	@Styleable protected float shadowOpacity;

	private int shadowSize;
	private Image shadowImage;
	private Color lastShadowColor;
	private float lastShadowOpacity;
	private int lastShadowSize;
	private double lastSystemScaleFactor;
	private float lastUserScaleFactor;

	public FlatDropShadowBorder() {
		this( null );
	}

	public FlatDropShadowBorder( Color shadowColor ) {
		this( shadowColor, 4, 0.5f );
	}

	public FlatDropShadowBorder( Color shadowColor, int shadowSize, float shadowOpacity ) {
		this( shadowColor, new Insets( -shadowSize, -shadowSize, shadowSize, shadowSize ), shadowOpacity );
	}

	public FlatDropShadowBorder( Color shadowColor, Insets shadowInsets, float shadowOpacity ) {
		super( nonNegativeInsets( shadowInsets ) );

		this.shadowColor = shadowColor;
		this.shadowInsets = shadowInsets;
		this.shadowOpacity = shadowOpacity;

		shadowSize = maxInset( shadowInsets );
	}

	private static Insets nonNegativeInsets( Insets shadowInsets ) {
		return new Insets( Math.max( shadowInsets.top, 0 ), Math.max( shadowInsets.left, 0 ),
			Math.max( shadowInsets.bottom, 0 ), Math.max( shadowInsets.right, 0 ) );
	}

	private int maxInset( Insets shadowInsets ) {
		return Math.max(
			Math.max( shadowInsets.left, shadowInsets.right ),
			Math.max( shadowInsets.top, shadowInsets.bottom ) );
	}

	/** @since 2 */
	@Override
	public Object applyStyleProperty( String key, Object value ) {
		Object oldValue = FlatStylingSupport.applyToAnnotatedObject( this, key, value );
		if( key.equals( "shadowInsets" ) ) {
			applyStyleProperty( nonNegativeInsets( shadowInsets ) );
			shadowSize = maxInset( shadowInsets );
		}
		return oldValue;
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos() {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		if( shadowSize <= 0 )
			return;

		HiDPIUtils.paintAtScale1x( (Graphics2D) g, x, y, width, height, this::paintImpl );
	}

	private void paintImpl( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
		Color shadowColor = (this.shadowColor != null) ? this.shadowColor : g.getColor();
		int shadowSize = scale( this.shadowSize, scaleFactor );

		// create and cache shadow image
		float userScaleFactor = UIScale.getUserScaleFactor();
		if( shadowImage == null ||
			!shadowColor.equals( lastShadowColor ) ||
			lastShadowOpacity != shadowOpacity ||
			lastShadowSize != shadowSize ||
			lastSystemScaleFactor != scaleFactor ||
			lastUserScaleFactor != userScaleFactor )
		{
			shadowImage = createShadowImage( shadowColor, shadowSize, shadowOpacity,
				(float) (scaleFactor * userScaleFactor) );
			lastShadowColor = shadowColor;
			lastShadowOpacity = shadowOpacity;
			lastShadowSize = shadowSize;
			lastSystemScaleFactor = scaleFactor;
			lastUserScaleFactor = userScaleFactor;
		}

/*debug
		int m = shadowImage.getWidth( null );
		Color oldColor = g.getColor();
		g.setColor( Color.lightGray );
		g.drawRect( x - m - 1, y - m - 1, m + 1, m + 1 );
		g.setColor( Color.white );
		g.fillRect( x - m, y - m, m, m );
		g.drawImage( shadowImage, x - m, y - m, null );
		g.setColor( oldColor );
debug*/

		int left = scale( shadowInsets.left, scaleFactor );
		int right = scale( shadowInsets.right, scaleFactor );
		int top = scale( shadowInsets.top, scaleFactor );
		int bottom = scale( shadowInsets.bottom, scaleFactor );

		// shadow outer coordinates
		int x1o = x - Math.min( left, 0 );
		int y1o = y - Math.min( top, 0 );
		int x2o = x + width + Math.min( right, 0 );
		int y2o = y + height + Math.min( bottom, 0 );

		// shadow inner coordinates
		int x1i = x1o + shadowSize;
		int y1i = y1o + shadowSize;
		int x2i = x2o - shadowSize;
		int y2i = y2o - shadowSize;

		int wh = (shadowSize * 2) - 1;
		int center = shadowSize - 1;

		// left-top edge
		if( left > 0 || top > 0 ) {
			g.drawImage( shadowImage, x1o, y1o, x1i, y1i,
				0, 0, center, center, null );
		}

		// top shadow
		if( top > 0 ) {
			g.drawImage( shadowImage, x1i, y1o, x2i, y1i,
				center, 0, center + 1, center, null );
		}

		// right-top edge
		if( right > 0 || top > 0 ) {
			g.drawImage( shadowImage, x2i, y1o, x2o, y1i,
				center, 0, wh, center, null );
		}

		// left shadow
		if( left > 0 ) {
			g.drawImage( shadowImage, x1o, y1i, x1i, y2i,
				0, center, center, center + 1, null );
		}

		// right shadow
		if( right > 0 ) {
			g.drawImage( shadowImage, x2i, y1i, x2o, y2i,
				center, center, wh, center + 1, null );
		}

		// left-bottom edge
		if( left > 0 || bottom > 0 ) {
			g.drawImage( shadowImage, x1o, y2i, x1i, y2o,
				0, center, center, wh, null );
		}

		// bottom shadow
		if( bottom > 0 ) {
			g.drawImage( shadowImage, x1i, y2i, x2i, y2o,
				center, center, center + 1, wh, null );
		}

		// right-bottom edge
		if( right > 0 || bottom > 0 ) {
			g.drawImage( shadowImage, x2i, y2i, x2o, y2o,
				center, center, wh, wh, null );
		}
	}

	private int scale( int value, double scaleFactor ) {
		return (int) Math.ceil( UIScale.scale( value ) * scaleFactor );
	}

	private static BufferedImage createShadowImage( Color shadowColor, int shadowSize,
		float shadowOpacity, float scaleFactor )
	{
		int shadowRGB = shadowColor.getRGB() & 0xffffff;
		int shadowAlpha = (int) (255 * shadowOpacity);
		Color startColor = new Color( shadowRGB | ((shadowAlpha & 0xff) << 24), true );
		Color midColor = new Color( shadowRGB | (((shadowAlpha / 2) & 0xff) << 24), true );
		Color endColor = new Color( shadowRGB, true );

/*debug
		startColor = Color.red;
		midColor = Color.green;
		endColor = Color.blue;
debug*/

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
