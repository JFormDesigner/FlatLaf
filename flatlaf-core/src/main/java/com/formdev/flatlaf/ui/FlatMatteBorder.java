/*
 * Copyright 2026 FormDev Software
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

package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import com.formdev.flatlaf.util.HiDPIUtils;

/**
 * Matte border that paints solid color lines with individual widths at each side.
 * <p>
 * Similar to {@link javax.swing.border.MatteBorder}, but the line widths (insets) are scaled.
 * Sides with zero width are not painted.
 *
 * @author Karl Tauber
 * @since 3.8
 */
public class FlatMatteBorder
	extends FlatEmptyBorder
{
	private final Color matteColor;

	public FlatMatteBorder( int top, int left, int bottom, int right, Color matteColor ) {
		super( top, left, bottom, right );
		this.matteColor = matteColor;
	}

	public FlatMatteBorder( Insets insets, Color matteColor ) {
		super( insets );
		this.matteColor = matteColor;
	}

	public Color getMatteColor() {
		return matteColor;
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		Color matteColor = getMatteColor();
		if( matteColor == null )
			return;

		Insets insets = getBorderInsets( c );
		if( insets.top <= 0 && insets.left <= 0 && insets.bottom <= 0 && insets.right <= 0 )
			return;

		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
		Color oldColor = g.getColor();
		g.setColor( matteColor );

		// paint at scale 1x to avoid clipping on right and bottom edges at 125%, 150% or 175%
		HiDPIUtils.paintAtScale1x( (Graphics2D) g, x, y, width, height,
			(g2d, x2, y2, width2, height2, scaleFactor) -> {
				Path2D border = new Path2D.Float( Path2D.WIND_EVEN_ODD );
				border.append( new Rectangle2D.Double( x2, y2, width2, height2 ), false );
				border.append( new Rectangle2D.Double( x2 + (insets.left * scaleFactor),
					y2 + (insets.top * scaleFactor),
					width2 - ((insets.left + insets.right) * scaleFactor),
					height2 - ((insets.top + insets.bottom) * scaleFactor) ), false );
				g2d.fill( border );
			} );

		g.setColor( oldColor );
		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}
}
