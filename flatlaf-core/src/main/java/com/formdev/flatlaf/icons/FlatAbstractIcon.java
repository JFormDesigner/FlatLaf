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

package com.formdev.flatlaf.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * Base class for icons that scales width and height, creates and initializes
 * a scaled graphics context for icon painting.
 * <p>
 * Subclasses do not need to scale icon painting.
 *
 * @author Karl Tauber
 */
public abstract class FlatAbstractIcon
	implements Icon, UIResource
{
	/** Unscaled icon width. */
	protected final int width;
	/** Unscaled icon height. */
	protected final int height;
	protected Color color;

	/** Additional icon scale factor. */
	private float scale = 1;

	public FlatAbstractIcon( int width, int height, Color color ) {
		this.width = width;
		this.height = height;
		this.color = color;
	}

	@Override
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			// for testing
//			g2.setColor( Color.blue );
//			g2.drawRect( x, y, getIconWidth() - 1, getIconHeight() - 1 );

			paintBackground( c, g2, x, y );

			g2.translate( x, y );
			UIScale.scaleGraphics( g2 );
			float scale = getScale();
			if( scale != 1 )
				g2.scale( scale, scale );

			if( color != null )
				g2.setColor( color );

			paintIcon( c, g2 );
		} finally {
			g2.dispose();
		}
	}

	/**
	 * Paints icon background. Default implementation does nothing.
	 * Can be overridden to paint specific icon background.
	 * <p>
	 * The bounds of the area to be filled are:
	 * x, y, {@link #getIconWidth()}, {@link #getIconHeight()}.
	 * <p>
	 * In contrast to {@link #paintIcon(Component, Graphics2D)},
	 * the graphics context {@code g} is not translated and not scaled.
	 *
	 * @since 3.5.2
	 */
	protected void paintBackground( Component c, Graphics2D g, int x, int y ) {
	}

	/**
	 * Paints icon.
	 * <p>
	 * The graphics context is translated and scaled.
	 * This means that icon x,y coordinates are {@code 0,0}
	 * and it is not necessary to scale coordinates within this method.
	 * <p>
	 * The bounds to be used for icon painting are:
	 * 0, 0, {@link #width}, {@link #height}.
	 */
	protected abstract void paintIcon( Component c, Graphics2D g );

	/**
	 * Returns the scaled icon width.
	 */
	@Override
	public int getIconWidth() {
		return scale( UIScale.scale( width ) );
	}

	/**
	 * Returns the scaled icon height.
	 */
	@Override
	public int getIconHeight() {
		return scale( UIScale.scale( height ) );
	}

	/** @since 3.7 */
	public float getScale() {
		return scale;
	}

	/** @since 3.7 */
	public void setScale( float scale ) {
		this.scale = scale;
	}

	/**
	 * Multiplies the given value by the icon scale factor {@link #getScale()} and rounds the result.
	 * <p>
	 * If you want scale a {@code float} or {@code double} value,
	 * simply use: {@code myFloatValue * }{@link #getScale()}.
	 * <p>
	 * Do not use this method when painting icon in {@link #paintIcon(Component, Graphics2D)}.
	 *
	 * @since 3.7
	 */
	protected int scale( int size ) {
		float scale = getScale();
		return (scale == 1) ? size : Math.round( size * scale );
	}
}
