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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
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
	public static final int DEFAULT_ARROW_WIDTH = 9;

	protected boolean chevron;
	protected Color foreground;
	protected Color disabledForeground;
	protected Color hoverForeground;
	protected Color hoverBackground;
	protected Color pressedForeground;
	protected Color pressedBackground;

	private int arrowWidth = DEFAULT_ARROW_WIDTH;
	private float arrowThickness = 1;
	private float xOffset = 0;
	private float yOffset = 0;
	private boolean roundBorderAutoXOffset = true;

	private boolean hover;
	private boolean pressed;

	public FlatArrowButton( int direction, String type, Color foreground, Color disabledForeground,
		Color hoverForeground, Color hoverBackground, Color pressedForeground, Color pressedBackground )
	{
		super( direction, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE );
		updateStyle( type, foreground, disabledForeground, hoverForeground, hoverBackground,
			pressedForeground, pressedBackground );

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
					if( SwingUtilities.isLeftMouseButton( e ) ) {
						pressed = true;
						repaint();
					}
				}

				@Override
				public void mouseReleased( MouseEvent e ) {
					if( SwingUtilities.isLeftMouseButton( e ) ) {
						pressed = false;
						repaint();
					}
				}
			} );
		}
	}

	/** @since 2 */
	public void updateStyle( String type, Color foreground, Color disabledForeground,
		Color hoverForeground, Color hoverBackground, Color pressedForeground, Color pressedBackground )
	{
		this.chevron = FlatUIUtils.isChevron( type );
		this.foreground = foreground;
		this.disabledForeground = disabledForeground;
		this.hoverForeground = hoverForeground;
		this.hoverBackground = hoverBackground;
		this.pressedForeground = pressedForeground;
		this.pressedBackground = pressedBackground;
	}

	public int getArrowWidth() {
		return arrowWidth;
	}

	public void setArrowWidth( int arrowWidth ) {
		this.arrowWidth = arrowWidth;
	}

	/** @since 3 */
	public float getArrowThickness() {
		return arrowThickness;
	}

	/** @since 3 */
	public void setArrowThickness( float arrowThickness ) {
		this.arrowThickness = arrowThickness;
	}

	protected boolean isHover() {
		return hover;
	}

	protected boolean isPressed() {
		return pressed;
	}

	public float getXOffset() {
		return xOffset;
	}

	public void setXOffset( float xOffset ) {
		this.xOffset = xOffset;
	}

	public float getYOffset() {
		return yOffset;
	}

	public void setYOffset( float yOffset ) {
		this.yOffset = yOffset;
	}

	/** @since 3 */
	public boolean isRoundBorderAutoXOffset() {
		return roundBorderAutoXOffset;
	}

	/** @since 3 */
	public void setRoundBorderAutoXOffset( boolean roundBorderAutoXOffset ) {
		this.roundBorderAutoXOffset = roundBorderAutoXOffset;
	}

	protected Color deriveBackground( Color background ) {
		return background;
	}

	protected Color deriveForeground( Color foreground ) {
		return FlatUIUtils.deriveColor( foreground, this.foreground );
	}

	/**
	 * Returns the color used to paint the arrow.
	 *
	 * @since 1.2
	 */
	protected Color getArrowColor() {
		return isEnabled()
			? (pressedForeground != null && isPressed()
				? pressedForeground
				: (hoverForeground != null && isHover()
					? hoverForeground
					: foreground))
			: disabledForeground;
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
		g.setColor( deriveForeground( getArrowColor() ) );
		paintArrow( (Graphics2D) g );

		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}

	protected void paintBackground( Graphics2D g ) {
		g.fillRect( 0, 0, getWidth(), getHeight() );
	}

	protected void paintArrow( Graphics2D g ) {
		int x = 0;

		// move arrow for round borders
		if( isRoundBorderAutoXOffset() ) {
			Container parent = getParent();
			boolean vert = (direction == NORTH || direction == SOUTH);
			if( vert && parent instanceof JComponent && FlatUIUtils.hasRoundBorder( (JComponent) parent ) )
				x -= scale( parent.getComponentOrientation().isLeftToRight() ? 1 : -1 );
		}

		FlatUIUtils.paintArrow( g, x, 0, getWidth(), getHeight(), getDirection(), chevron,
			getArrowWidth(), getArrowThickness(), getXOffset(), getYOffset() );
	}
}
