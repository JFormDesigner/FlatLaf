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

	protected final boolean chevron;
	protected final Color foreground;
	protected final Color disabledForeground;
	protected final Color hoverForeground;
	protected final Color hoverBackground;
	protected final Color pressedForeground;
	protected final Color pressedBackground;

	private int arrowWidth = DEFAULT_ARROW_WIDTH;
	private int xOffset = 0;
	private int yOffset = 0;

	private boolean hover;
	private boolean pressed;

	public FlatArrowButton( int direction, String type, Color foreground, Color disabledForeground,
		Color hoverForeground, Color hoverBackground, Color pressedForeground, Color pressedBackground )
	{
		super( direction, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE );

		this.chevron = FlatUIUtils.isChevron( type );
		this.foreground = foreground;
		this.disabledForeground = disabledForeground;
		this.hoverForeground = hoverForeground;
		this.hoverBackground = hoverBackground;
		this.pressedForeground = pressedForeground;
		this.pressedBackground = pressedBackground;

		setOpaque( false );
		setBorder( null );

		if( hoverForeground != null || hoverBackground != null ||
			pressedForeground != null || pressedBackground != null )
		{
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

	protected Color deriveForeground( Color foreground ) {
		return FlatUIUtils.deriveColor( foreground, this.foreground );
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
		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

		// paint hover or pressed background
		if( isEnabled() ) {
			Color background = (pressedBackground != null && isPressed())
				? pressedBackground
				: (hoverBackground != null && isHover()
					? hoverBackground
					: null);

			if( background != null ) {
				g.setColor( deriveBackground( background ) );
				paintBackground( (Graphics2D) g );
			}
		}

		// paint arrow
		g.setColor( deriveForeground( isEnabled()
			? (pressedForeground != null && isPressed()
				? pressedForeground
				: (hoverForeground != null && isHover()
					? hoverForeground
					: foreground))
			: disabledForeground ) );
		paintArrow( (Graphics2D) g );

		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}

	protected void paintBackground( Graphics2D g ) {
		g.fillRect( 0, 0, getWidth(), getHeight() );
	}

	protected void paintArrow( Graphics2D g ) {
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

		int x = Math.round( (getWidth() - rw) / 2f + scale( (float) xOffset ) );
		int y = Math.round( (getHeight() - rh) / 2f + scale( (float) yOffset ) );

		// move arrow for round borders
		Container parent = getParent();
		if( vert && parent instanceof JComponent && FlatUIUtils.hasRoundBorder( (JComponent) parent ) )
			x -= scale( parent.getComponentOrientation().isLeftToRight() ? 1 : -1 );

		// paint arrow
		g.translate( x, y );
/*debug
		debugPaint( g, vert, rw, rh );
debug*/
		Shape arrowShape = createArrowShape( direction, chevron, w, h );
		if( chevron ) {
			g.setStroke( new BasicStroke( scale( 1f ) ) );
			g.draw( arrowShape );
		} else {
			// triangle
			g.fill( arrowShape );
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
