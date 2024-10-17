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

package com.formdev.flatlaf.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HiDPIUtils;

/**
 * Base class for window icons.
 *
 * @uiDefault TitlePane.buttonSize						Dimension
 * @uiDefault TitlePane.buttonSymbolHeight				int
 * @uiDefault TitlePane.buttonHoverBackground			Color
 * @uiDefault TitlePane.buttonPressedBackground			Color
 *
 * @author Karl Tauber
 */
public abstract class FlatWindowAbstractIcon
	extends FlatAbstractIcon
{
	private final int symbolHeight;
	private final Color hoverBackground;
	private final Color pressedBackground;

	/** @since 3.2 */
	protected FlatWindowAbstractIcon( String windowStyle ) {
		this( FlatUIUtils.getSubUIDimension( "TitlePane.buttonSize", windowStyle ),
			FlatUIUtils.getSubUIInt( "TitlePane.buttonSymbolHeight", windowStyle, 10 ),
			FlatUIUtils.getSubUIColor( "TitlePane.buttonHoverBackground", windowStyle ),
			FlatUIUtils.getSubUIColor( "TitlePane.buttonPressedBackground", windowStyle ) );
	}

	/** @since 3.2 */
	protected FlatWindowAbstractIcon( Dimension size, int symbolHeight, Color hoverBackground, Color pressedBackground ) {
		super( size.width, size.height, null );
		this.symbolHeight = symbolHeight;
		this.hoverBackground = hoverBackground;
		this.pressedBackground = pressedBackground;
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		g.setColor( getForeground( c ) );
		HiDPIUtils.paintAtScale1x( g, 0, 0, width, height, this::paintIconAt1x );
	}

	protected abstract void paintIconAt1x( Graphics2D g, int x, int y, int width, int height, double scaleFactor );

	/** @since 3.5.2 */
	@Override
	protected void paintBackground( Component c, Graphics2D g, int x, int y ) {
		Color background = FlatButtonUI.buttonStateColor( c, null, null, null, hoverBackground, pressedBackground );
		if( background != null ) {
			// disable antialiasing for background rectangle painting to avoid blurry edges when scaled (e.g. at 125% or 175%)
			Object oldHint = g.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );

			// fill background of whole component
			g.setColor( FlatUIUtils.deriveColor( background, c.getBackground() ) );
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );

			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, oldHint );
		}
	}

	protected Color getForeground( Component c ) {
		return c.getForeground();
	}

	/** @since 3.2 */
	protected int getSymbolHeight() {
		return symbolHeight;
	}
}
