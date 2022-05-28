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

import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.UnknownStyleException;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JToggleButton}.
 *
 * <!-- BasicButtonUI -->
 *
 * @uiDefault ToggleButton.font							Font
 * @uiDefault ToggleButton.background					Color
 * @uiDefault ToggleButton.foreground					Color
 * @uiDefault ToggleButton.border						Border
 * @uiDefault ToggleButton.margin						Insets
 * @uiDefault ToggleButton.rollover						boolean
 *
 * <!-- FlatButtonUI -->
 *
 * @uiDefault Component.focusWidth						int
 * @uiDefault Button.arc								int
 * @uiDefault ToggleButton.minimumWidth					int
 * @uiDefault ToggleButton.iconTextGap					int
 * @uiDefault ToggleButton.startBackground				Color	optional; if set, a gradient paint is used and ToggleButton.background is ignored
 * @uiDefault ToggleButton.endBackground				Color	optional; if set, a gradient paint is used
 * @uiDefault ToggleButton.focusedBackground			Color	optional
 * @uiDefault ToggleButton.focusedForeground			Color	optional
 * @uiDefault ToggleButton.hoverBackground				Color	optional
 * @uiDefault ToggleButton.hoverForeground				Color	optional
 * @uiDefault ToggleButton.pressedBackground			Color	optional
 * @uiDefault ToggleButton.pressedForeground			Color	optional
 * @uiDefault ToggleButton.selectedBackground			Color
 * @uiDefault ToggleButton.selectedForeground			Color
 * @uiDefault ToggleButton.disabledBackground			Color	optional
 * @uiDefault ToggleButton.disabledText					Color
 * @uiDefault ToggleButton.disabledSelectedBackground	Color
 * @uiDefault ToggleButton.disabledSelectedForeground	Color	optional
 * @uiDefault Button.paintShadow						boolean	default is false
 * @uiDefault Button.shadowWidth						int		default is 2
 * @uiDefault Button.shadowColor						Color	optional
 * @uiDefault ToggleButton.toolbar.hoverBackground		Color
 * @uiDefault ToggleButton.toolbar.hoverForeground		Color	optional
 * @uiDefault ToggleButton.toolbar.pressedBackground	Color
 * @uiDefault ToggleButton.toolbar.pressedForeground	Color	optional
 * @uiDefault ToggleButton.toolbar.selectedBackground	Color
 * @uiDefault ToggleButton.toolbar.selectedForeground	Color	optional
 * @uiDefault ToggleButton.toolbar.disabledSelectedBackground	Color	optional
 * @uiDefault ToggleButton.toolbar.disabledSelectedForeground	Color	optional
 *
 * <!-- FlatToggleButtonUI -->
 *
 * @uiDefault ToggleButton.tab.underlineHeight			int
 * @uiDefault ToggleButton.tab.underlineColor			Color
 * @uiDefault ToggleButton.tab.disabledUnderlineColor	Color
 * @uiDefault ToggleButton.tab.selectedBackground		Color	optional
 * @uiDefault ToggleButton.tab.selectedForeground		Color	optional
 * @uiDefault ToggleButton.tab.hoverBackground			Color
 * @uiDefault ToggleButton.tab.hoverForeground			Color	optional
 * @uiDefault ToggleButton.tab.focusBackground			Color
 * @uiDefault ToggleButton.tab.focusForeground			Color	optional
 *
 *
 * @author Karl Tauber
 */
public class FlatToggleButtonUI
	extends FlatButtonUI
{
	@Styleable(dot=true) protected int tabUnderlineHeight;
	@Styleable(dot=true) protected Color tabUnderlineColor;
	@Styleable(dot=true) protected Color tabDisabledUnderlineColor;
	@Styleable(dot=true) protected Color tabSelectedBackground;
	/** @since 2.3 */ @Styleable(dot=true) protected Color tabSelectedForeground;
	@Styleable(dot=true) protected Color tabHoverBackground;
	/** @since 2.3 */ @Styleable(dot=true) protected Color tabHoverForeground;
	@Styleable(dot=true) protected Color tabFocusBackground;
	/** @since 2.3 */ @Styleable(dot=true) protected Color tabFocusForeground;

	private boolean defaults_initialized = false;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatToggleButtonUI.class, () -> new FlatToggleButtonUI( true ) )
			: new FlatToggleButtonUI( false );
	}

	protected FlatToggleButtonUI( boolean shared ) {
		super( shared );
	}

	@Override
	String getStyleType() {
		return "ToggleButton";
	}

	@Override
	protected String getPropertyPrefix() {
		return "ToggleButton.";
	}

	@Override
	protected void installDefaults( AbstractButton b ) {
		super.installDefaults( b );

		if( !defaults_initialized ) {
			tabUnderlineHeight = UIManager.getInt( "ToggleButton.tab.underlineHeight" );
			tabUnderlineColor = UIManager.getColor( "ToggleButton.tab.underlineColor" );
			tabDisabledUnderlineColor = UIManager.getColor( "ToggleButton.tab.disabledUnderlineColor" );
			tabSelectedBackground = UIManager.getColor( "ToggleButton.tab.selectedBackground" );
			tabSelectedForeground = UIManager.getColor( "ToggleButton.tab.selectedForeground" );
			tabHoverBackground = UIManager.getColor( "ToggleButton.tab.hoverBackground" );
			tabHoverForeground = UIManager.getColor( "ToggleButton.tab.hoverForeground" );
			tabFocusBackground = UIManager.getColor( "ToggleButton.tab.focusBackground" );
			tabFocusForeground = UIManager.getColor( "ToggleButton.tab.focusForeground" );

			defaults_initialized = true;
		}
	}

	@Override
	protected void uninstallDefaults( AbstractButton b ) {
		super.uninstallDefaults( b );
		defaults_initialized = false;
	}

	@Override
	protected void propertyChange( AbstractButton b, PropertyChangeEvent e ) {
		super.propertyChange( b, e );

		switch( e.getPropertyName() ) {
			case BUTTON_TYPE:
				if( BUTTON_TYPE_TAB.equals( e.getOldValue() ) || BUTTON_TYPE_TAB.equals( e.getNewValue() ) ) {
					MigLayoutVisualPadding.uninstall( b );
					MigLayoutVisualPadding.install( b );
					b.revalidate();
				}

				b.repaint();
				break;

			case TAB_BUTTON_UNDERLINE_PLACEMENT:
			case TAB_BUTTON_UNDERLINE_HEIGHT:
			case TAB_BUTTON_UNDERLINE_COLOR:
			case TAB_BUTTON_SELECTED_BACKGROUND:
				b.repaint();
				break;
		}
	}

	/** @since 2 */
	@Override
	protected Object applyStyleProperty( AbstractButton b, String key, Object value ) {
		if( key.startsWith( "help." ) )
			throw new UnknownStyleException( key );

		return super.applyStyleProperty( b, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		Map<String, Class<?>> infos = super.getStyleableInfos( c );
		infos.keySet().removeIf( s -> s.startsWith( "help." ) );
		return infos;
	}

	static boolean isTabButton( Component c ) {
		return c instanceof JToggleButton && BUTTON_TYPE_TAB.equals( getButtonTypeStr( (JToggleButton) c ) );
	}

	@Override
	protected void paintBackground( Graphics g, JComponent c ) {
		if( isTabButton( c ) ) {
			int height = c.getHeight();
			int width = c.getWidth();
			boolean selected = ((AbstractButton)c).isSelected();
			Color enabledColor = selected ? clientPropertyColor( c, TAB_BUTTON_SELECTED_BACKGROUND, tabSelectedBackground ) : null;

			// use component background if explicitly set
			if( enabledColor == null ) {
				Color bg = c.getBackground();
				if( isCustomBackground( bg ) )
					enabledColor = bg;
			}

			// paint background
			Color background = buttonStateColor( c, enabledColor,
				null, tabFocusBackground, tabHoverBackground, null );
			if( background != null ) {
				g.setColor( background );
				g.fillRect( 0, 0, width, height );
			}

			// paint underline if selected
			if( selected ) {
				int underlineThickness = UIScale.scale( clientPropertyInt( c, TAB_BUTTON_UNDERLINE_HEIGHT, tabUnderlineHeight ) );
				g.setColor( c.isEnabled()
					? clientPropertyColor( c, TAB_BUTTON_UNDERLINE_COLOR, tabUnderlineColor )
					: tabDisabledUnderlineColor );
				int placement = clientPropertyInt( c, TAB_BUTTON_UNDERLINE_PLACEMENT, SwingConstants.BOTTOM );
				switch (placement) {
					case SwingConstants.TOP:
						g.fillRect( 0, 0, width, underlineThickness );
						break;
					case SwingConstants.LEFT:
						g.fillRect( 0, 0, underlineThickness, height );
						break;
					case SwingConstants.RIGHT:
						g.fillRect( width - underlineThickness, 0, underlineThickness, height );
						break;
					case SwingConstants.BOTTOM:
					default:
						g.fillRect( 0, height - underlineThickness, width, underlineThickness );
				}
			}
		} else
			super.paintBackground( g, c );
	}

	@Override
	protected Color getForeground( JComponent c ) {
		if( isTabButton( c ) ) {
			if( !c.isEnabled() )
				return disabledText;

			if( tabSelectedForeground != null && ((AbstractButton)c).isSelected() )
				return tabSelectedForeground;

			return buttonStateColor( c, c.getForeground(), disabledText,
				tabFocusForeground, tabHoverForeground, null );
		} else
			return super.getForeground( c );
	}
}
