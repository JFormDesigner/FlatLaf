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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatTitlePane;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * Base class for window icons.
 *
 * @uiDefault TitlePane.buttonSize						Dimension
 * @uiDefault TitlePane.buttonInsets					Insets	optional
 * @uiDefault TitlePane.buttonArc						int		optional
 * @uiDefault TitlePane.buttonSymbolHeight				int
 * @uiDefault TitlePane.buttonBackground				Color	optional
 * @uiDefault TitlePane.buttonForeground				Color	optional
 * @uiDefault TitlePane.buttonInactiveBackground		Color	optional
 * @uiDefault TitlePane.buttonInactiveForeground		Color	optional
 * @uiDefault TitlePane.buttonHoverBackground			Color	optional
 * @uiDefault TitlePane.buttonHoverForeground			Color	optional
 * @uiDefault TitlePane.buttonPressedBackground			Color	optional
 * @uiDefault TitlePane.buttonPressedForeground			Color	optional
 *
 * @author Karl Tauber
 */
public abstract class FlatWindowAbstractIcon
	extends FlatAbstractIcon
{
	/** @since 3.6 */ protected final Insets insets;
	/** @since 3.6 */ protected final int arc;
	/** @since 3.6 */ protected final int symbolHeight;

	/** @since 3.6 */ protected final Color background;
	/** @since 3.6 */ protected final Color foreground;
	/** @since 3.6 */ protected final Color inactiveBackground;
	/** @since 3.6 */ protected final Color inactiveForeground;
	protected final Color hoverBackground;
	/** @since 3.6 */ protected final Color hoverForeground;
	protected final Color pressedBackground;
	/** @since 3.6 */ protected final Color pressedForeground;

	/** @since 3.2 */
	protected FlatWindowAbstractIcon( String windowStyle ) {
		this( windowStyle, null, null, null, null, null, null, null, null );
	}

	/** @since 3.6 */
	protected FlatWindowAbstractIcon( String windowStyle,
		Color background, Color foreground, Color inactiveBackground, Color inactiveForeground,
		Color hoverBackground, Color hoverForeground, Color pressedBackground, Color pressedForeground )
	{
		this( FlatUIUtils.getSubUIDimension( "TitlePane.buttonSize", windowStyle ),
			FlatUIUtils.getSubUIInsets( "TitlePane.buttonInsets", windowStyle ),
			FlatUIUtils.getSubUIInt( "TitlePane.buttonArc", windowStyle, 0 ),
			FlatUIUtils.getSubUIInt( "TitlePane.buttonSymbolHeight", windowStyle, 10 ),
			(background != null) ? background : FlatUIUtils.getSubUIColor( "TitlePane.buttonBackground", windowStyle ),
			(foreground != null) ? foreground : FlatUIUtils.getSubUIColor( "TitlePane.buttonForeground", windowStyle ),
			(inactiveBackground != null) ? inactiveBackground : FlatUIUtils.getSubUIColor( "TitlePane.buttonInactiveBackground", windowStyle ),
			(inactiveForeground != null) ? inactiveForeground : FlatUIUtils.getSubUIColor( "TitlePane.buttonInactiveForeground", windowStyle ),
			(hoverBackground != null) ? hoverBackground : FlatUIUtils.getSubUIColor( "TitlePane.buttonHoverBackground", windowStyle ),
			(hoverForeground != null) ? hoverForeground : FlatUIUtils.getSubUIColor( "TitlePane.buttonHoverForeground", windowStyle ),
			(pressedBackground != null) ? pressedBackground : FlatUIUtils.getSubUIColor( "TitlePane.buttonPressedBackground", windowStyle ),
			(pressedForeground != null) ? pressedForeground : FlatUIUtils.getSubUIColor( "TitlePane.buttonPressedForeground", windowStyle ) );
	}

	/** @since 3.6 */
	protected FlatWindowAbstractIcon( Dimension size, Insets insets, int arc, int symbolHeight,
		Color background, Color foreground, Color inactiveBackground, Color inactiveForeground,
		Color hoverBackground, Color hoverForeground, Color pressedBackground, Color pressedForeground )
	{
		super( size.width, size.height, null );
		this.insets = (insets != null) ? insets : new Insets( 0, 0, 0, 0 );
		this.arc = arc;
		this.symbolHeight = symbolHeight;

		this.background = background;
		this.foreground = foreground;
		this.inactiveBackground = inactiveBackground;
		this.inactiveForeground = inactiveForeground;
		this.hoverBackground = hoverBackground;
		this.hoverForeground = hoverForeground;
		this.pressedBackground = pressedBackground;
		this.pressedForeground = pressedForeground;
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
		Color bg = null;
		if( background != null || inactiveBackground != null ) {
			Window window = SwingUtilities.windowForComponent( c );
			bg = (window == null || window.isActive()) ? background : inactiveBackground;
		}

		Color background = FlatButtonUI.buttonStateColor( c, bg, null, null, hoverBackground, pressedBackground );
		if( background != null ) {
			Insets insets = UIScale.scale( this.insets );
			float arc = UIScale.scale( (float) this.arc );

			// derive color from title pane background
			if( background instanceof DerivedColor ) {
				Container titlePane = SwingUtilities.getAncestorOfClass( FlatTitlePane.class, c );
				Component baseComp = (titlePane != null) ? titlePane : c;
				background = FlatUIUtils.deriveColor( background, baseComp.getBackground() );
			}

			g.setColor( background );
			FlatUIUtils.paintComponentBackground( g, insets.left, insets.top,
				c.getWidth() - insets.left - insets.right,
				c.getHeight() - insets.top - insets.bottom,
				0, arc );
		}
	}

	protected Color getForeground( Component c ) {
		Color fg = null;
		if( foreground != null || inactiveForeground != null ) {
			Window window = SwingUtilities.windowForComponent( c );
			fg = (window == null || window.isActive()) ? foreground : inactiveForeground;
		}
		return FlatButtonUI.buttonStateColor( c, (fg != null) ? fg : c.getForeground(),
			null, null, hoverForeground, pressedForeground );
	}
}
