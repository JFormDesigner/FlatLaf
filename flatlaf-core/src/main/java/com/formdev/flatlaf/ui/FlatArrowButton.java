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

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import javax.swing.JComponent;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * Button that draws a scaled arrow in one direction.
 *
 * @author Karl Tauber
 */
public class FlatArrowButton
	extends BasicArrowButton
	implements UIResource
{
	public static final int DEFAULT_ARROW_WIDTH = 8;

	private final boolean chevron;
	private final Color foreground;
	private final Color disabledForeground;
	private final Color hoverForeground;
	private final Color hoverBackground;
	private final Color pressedBackground;

	private int arrowWidth = DEFAULT_ARROW_WIDTH;
	private int xOffset = 0;
	private int yOffset = 0;

	private boolean hover;
	private boolean pressed;

	public FlatArrowButton( int direction, String type, Color foreground, Color disabledForeground,
		Color hoverForeground, Color hoverBackground )
	{
		this( direction, type, foreground, disabledForeground, hoverForeground, hoverBackground, null );
	}

	public FlatArrowButton( int direction, String type, Color foreground, Color disabledForeground,
		Color hoverForeground, Color hoverBackground, Color pressedBackground )
	{
		super( direction, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE );

		this.chevron = "chevron".equals( type );
		this.foreground = foreground;
		this.disabledForeground = disabledForeground;
		this.hoverForeground = hoverForeground;
		this.hoverBackground = hoverBackground;
		this.pressedBackground = pressedBackground;

		setOpaque( false );
		setBorder( null );

		if( hoverForeground != null || hoverBackground != null || pressedBackground != null ) {
			addMouseListener( new MouseAdapter() {
				@Override
				public void mouseEntered( MouseEvent e ) {
					hover = true;
					repaint();
				}

				@Override
				public void mouseExited( MouseEvent e ) {
					hover = false;
					repaint();
				}

				@Override
				public void mousePressed( MouseEvent e ) {
					pressed = true;
					repaint();
				}

				@Override
				public void mouseReleased( MouseEvent e ) {
					pressed = false;
					repaint();
				}
			} );
		}
	}

	public int getArrowWidth() {
		return arrowWidth;
	}

	public void setArrowWidth( int arrowWidth ) {
		this.arrowWidth = arrowWidth;
	}

	protected boolean isHover() {
		return hover;
	}

	protected boolean isPressed() {
		return pressed;
	}

	public int getXOffset() {
		return xOffset;
	}

	public void setXOffset( int xOffset ) {
		this.xOffset = xOffset;
	}

	public int getYOffset() {
		return yOffset;
	}

	public void setYOffset( int yOffset ) {
		this.yOffset = yOffset;
	}

	protected Color deriveBackground( Color background ) {
		return background;
	}

	@Override
	public Dimension getPreferredSize() {
		return scale( super.getPreferredSize() );
	}

	@Override
	public Dimension getMinimumSize() {
		return scale( super.getMinimumSize() );
	}

	@Override
	public void paint( Graphics g ) {
		Graphics2D g2 = (Graphics2D)g;
		FlatUIUtils.setRenderingHints( g2 );

		int width = getWidth();
		int height = getHeight();
		boolean enabled = isEnabled();

		// paint hover or pressed background
		if( enabled ) {
			Color background = (pressedBackground != null && isPressed())
				? deriveBackground( pressedBackground )
				: ((hoverBackground != null && isHover())
					? deriveBackground( hoverBackground )
					: null);

			if( background != null ) {
				g.setColor( background );
				g.fillRect( 0, 0, width, height );
			}
		}

		int direction = getDirection();
		boolean vert = (direction == NORTH || direction == SOUTH);

		// compute width/height
		int w = scale( arrowWidth + (chevron ? 0 : 1) );
		int h = scale( (arrowWidth / 2) + (chevron ? 0 : 1) );

		// rotate width/height
		int rw = vert ? w : h;
		int rh = vert ? h : w;

		// chevron lines end 1px outside of width/height
		if( chevron ) {
			// add 1px to width/height for position calculation only
			rw++;
			rh++;
		}

		int x = Math.round( (width - rw) / 2f + scale( (float) xOffset ) );
		int y = Math.round( (height - rh) / 2f + scale( (float) yOffset ) );

		// move arrow for round borders
		Container parent = getParent();
		if( vert && parent instanceof JComponent && FlatUIUtils.hasRoundBorder( (JComponent) parent ) )
			x -= scale( parent.getComponentOrientation().isLeftToRight() ? 1 : -1 );

		// paint arrow
		g.setColor( enabled
			? (isHover() && hoverForeground != null ? hoverForeground : foreground)
			: disabledForeground );
		g.translate( x, y );
/*debug
		debugPaint( g2, vert, rw, rh );
debug*/
		Shape arrowShape = createArrowShape( direction, chevron, w, h );
		if( chevron ) {
			g2.setStroke( new BasicStroke( scale( 1f ) ) );
			g2.draw( arrowShape );
		} else {
			// triangle
			g2.fill( arrowShape );
		}
		g.translate( -x, -y );
	}

	public static Shape createArrowShape( int direction, boolean chevron, float w, float h ) {
		switch( direction ) {
			case NORTH:	return FlatUIUtils.createPath( !chevron, 0,h, (w / 2f),0, w,h );
			case SOUTH:	return FlatUIUtils.createPath( !chevron, 0,0, (w / 2f),h, w,0 );
			case WEST:	return FlatUIUtils.createPath( !chevron, h,0, 0,(w / 2f), h,w );
			case EAST:	return FlatUIUtils.createPath( !chevron, 0,0, h,(w / 2f), 0,w );
			default:	return new Path2D.Float();
		}
	}

/*debug
	private void debugPaint( Graphics g, boolean vert, int w, int h ) {
		Color oldColor = g.getColor();
		g.setColor( Color.red );
		g.drawRect( 0, 0, w - 1, h - 1 );

		int xy1 = -2;
		int xy2 = h + 1;
		for( int i = 0; i < 20; i++ ) {
			g.drawRect( vert ? 0 : xy1, vert ? xy1 : 0, 0, 0 );
			g.drawRect( vert ? 0 : xy2, vert ? xy2 : 0, 0, 0 );
			xy1 -= 2;
			xy2 += 2;
		}
		g.setColor( oldColor );
	}
debug*/
}
