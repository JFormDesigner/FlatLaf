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

		int w = scale( 9 );
		int h = scale( 5 );
		int x = Math.round( (width - w) / 2f );
		int y = Math.round( (height - h) / 2f );

		// arrow for SOUTH direction
		Path2D arrow = new Path2D.Float();
		arrow.moveTo( x, y );
		arrow.lineTo( x + w, y );
		arrow.lineTo( x + (w / 2f), y + h );
		arrow.closePath();

		// rotate arrow if necessary
		if( direction == WEST ) {
			g2.translate( width, 0 );
			g2.rotate( Math.toRadians( 90 ) );
		} else if( direction == EAST ) {
			g2.translate( 0, height );
			g2.rotate( Math.toRadians( 270 ) );
		} else if( direction == NORTH ) {
			g2.translate( width, height );
			g2.rotate( Math.toRadians( 180 ) );
		}

		// paint arrow
		g.setColor( enabled
			? (hover && hoverForeground != null ? hoverForeground : foreground)
			: disabledForeground );
		g2.fill( arrow );
	}
}
