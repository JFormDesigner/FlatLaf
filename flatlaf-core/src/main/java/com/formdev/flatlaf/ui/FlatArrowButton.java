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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
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
	private final Color foreground;
	private final Color disabledForeground;
	private final Color hoverForeground;
	private final Color hoverBackground;

	private int xOffset = 0;
	private int yOffset = 0;

	private boolean hover;

	public FlatArrowButton( int direction, Color foreground, Color disabledForeground,
		Color hoverForeground, Color hoverBackground )
	{
		super( direction, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE );

		this.foreground = foreground;
		this.disabledForeground = disabledForeground;
		this.hoverForeground = hoverForeground;
		this.hoverBackground = hoverBackground;

		setOpaque( false );
		setBorder( null );

		if( hoverForeground != null || hoverBackground != null ) {
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
			} );
		}
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

		// paint hover background
		if( enabled && hover && hoverBackground != null ) {
			g.setColor( hoverBackground );
			g.fillRect( 0, 0, width, height );
		}

		boolean vert = (direction == NORTH || direction == SOUTH);

		int w = scale( 9 );
		int h = scale( 5 );
		int x = Math.round( (width - (vert ? w : h)) / 2f + scale( (float) xOffset ) );
		int y = Math.round( (height - (vert ? h : w)) / 2f + scale( (float) yOffset ) );

		// paint arrow
		g.setColor( enabled
			? (hover && hoverForeground != null ? hoverForeground : foreground)
			: disabledForeground );
		g.translate( x, y );
		g2.fill( createArrowShape( direction, w, h ) );
		g.translate( -x, -y );
	}

	public static Shape createArrowShape( int direction, int w, int h ) {
		Path2D arrow = new Path2D.Float();

		switch( direction ) {
			case NORTH:
				arrow.moveTo( 0, h );
				arrow.lineTo( w, h );
				arrow.lineTo( (w / 2f), 0 );
				arrow.closePath();
				break;

			case SOUTH:
				arrow.moveTo( 0, 0 );
				arrow.lineTo( w, 0 );
				arrow.lineTo( (w / 2f), h );
				arrow.closePath();
				break;

			case WEST:
				arrow.moveTo( h, 0 );
				arrow.lineTo( h, w );
				arrow.lineTo( 0, (w / 2f) );
				arrow.closePath();
				break;

			case EAST:
				arrow.moveTo( 0, 0 );
				arrow.lineTo( 0, w );
				arrow.lineTo( h, (w / 2f) );
				arrow.closePath();
				break;
		}

		return arrow;
	}
}
