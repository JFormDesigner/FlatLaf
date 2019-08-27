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

package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.UIManager;
import com.formdev.flatlaf.util.UIScale;

/**
 * Utility methods for UI delegates.
 *
 * @author Karl Tauber
 */
public class FlatUIUtils
{
	public static final boolean MAC_USE_QUARTZ = Boolean.getBoolean( "apple.awt.graphics.UseQuartz" );

	public static Rectangle subtract( Rectangle r, Insets insets ) {
		return new Rectangle(
			r.x + insets.left,
			r.y + insets.top,
			r.width - insets.left - insets.right,
			r.height - insets.top - insets.bottom );
	}

	public static Color getUIColor( String key, int defaultColorRGB ) {
		Color color = UIManager.getColor( key );
		return (color != null) ? color : new Color( defaultColorRGB );
	}

	public static int getUIInt( String key, int defaultValue ) {
		Object value = UIManager.get( key );
		return (value instanceof Integer) ? (Integer) value : defaultValue;
	}

	/**
	 * Sets rendering hints used for painting.
	 */
	public static void setRenderingHints( Graphics2D g ) {
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,
			MAC_USE_QUARTZ ? RenderingHints.VALUE_STROKE_PURE : RenderingHints.VALUE_STROKE_NORMALIZE );
	}

	/**
	 * Draws a round rectangle.
	 */
	public static void drawRoundRectangle( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float lineWidth, float arc )
	{
		float arc2 = arc > lineWidth ? arc - lineWidth : 0f;

		RoundRectangle2D.Float r1 = new RoundRectangle2D.Float(
			x + focusWidth, y + focusWidth,
			width - focusWidth * 2, height - focusWidth * 2, arc, arc );
		RoundRectangle2D.Float r2 = new RoundRectangle2D.Float(
			r1.x + lineWidth, r1.y + lineWidth,
			r1.width - lineWidth * 2, r1.height - lineWidth * 2, arc2, arc2 );

		Path2D border = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		border.append( r1, false );
		border.append( r2, false );
		g.fill( border );
	}

	/**
	 * Fills a round rectangle.
	 */
	public static void fillRoundRectangle( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float arc )
	{
		g.fill( new RoundRectangle2D.Float(
			x + focusWidth, y + focusWidth,
			width - focusWidth * 2, height - focusWidth * 2, arc, arc ) );
	}

	/**
	 * Fill background with parent's background color because the visible component
	 * is smaller than its bounds (for the focus decoration).
	 */
	public static void paintParentBackground( Graphics g, JComponent c ) {
		Container parent = findOpaqueParent( c );
		if( parent != null ) {
			g.setColor( parent.getBackground() );
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
		}
	}

	/**
	 * Find the first parent that is opaque.
	 */
	private static Container findOpaqueParent( Container c ) {
		while( (c = c.getParent()) != null ) {
			if( c.isOpaque() )
				return c;
		}
		return null;
	}

	/**
	 * Paints an outline border.
	 */
	public static void paintOutlineBorder( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float lineWidth, float arc )
	{
		float x1 = x;
		float y1 = y;
		float x2 = x1 + width;
		float y2 = y1 + height;

		float outerArc = (arc > 0) ? arc + focusWidth - UIScale.scale( 2f ) : focusWidth;
		Path2D outerRect = createOutlinePath( x1, y1, x2, y2, outerArc );

		float ow = focusWidth + lineWidth;
		Path2D innerRect = createOutlinePath( x1 + ow, y1 + ow, x2 - ow, y2 - ow, outerArc - ow );

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( outerRect, false );
		path.append( innerRect, false );
		g.fill( path );
	}

	private static Path2D createOutlinePath( float x1, float y1, float x2, float y2, float arc ) {
		Path2D rect = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		rect.moveTo( x2 - arc, y1 );
		rect.quadTo( x2, y1, x2, y1 + arc );
		rect.lineTo( x2, y2 - arc );
		rect.quadTo( x2, y2, x2 - arc, y2 );
		rect.lineTo( x1 + arc, y2 );
		rect.quadTo( x1, y2, x1, y2 - arc );
		rect.lineTo( x1, y1 + arc );
		rect.quadTo( x1, y1, x1 + arc, y1 );
		rect.closePath();
		return rect;
	}
}
