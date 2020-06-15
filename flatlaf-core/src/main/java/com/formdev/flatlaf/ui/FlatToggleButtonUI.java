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
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
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
 * @uiDefault ToggleButton.pressedBackground			Color
 * @uiDefault ToggleButton.disabledBackground			Color	optional
 * @uiDefault ToggleButton.disabledText					Color
 * @uiDefault ToggleButton.toolbar.hoverBackground		Color
 * @uiDefault ToggleButton.toolbar.pressedBackground	Color
 *
 * <!-- FlatToggleButtonUI -->
 *
 * @uiDefault ToggleButton.selectedBackground			Color
 * @uiDefault ToggleButton.selectedForeground			Color
 * @uiDefault ToggleButton.disabledSelectedBackground	Color
 * @uiDefault ToggleButton.toolbar.selectedBackground	Color
 *
 * @uiDefault ToggleButton.tab.underlineHeight			int
 * @uiDefault ToggleButton.tab.underlineColor			Color
 * @uiDefault ToggleButton.tab.disabledUnderlineColor	Color
 * @uiDefault ToggleButton.tab.selectedBackground		Color	optional
 * @uiDefault ToggleButton.tab.hoverBackground			Color
 * @uiDefault ToggleButton.tab.focusBackground			Color
 *
 *
 * @author Karl Tauber
 */
public class FlatToggleButtonUI
	extends FlatButtonUI
{
	protected Color selectedBackground;
	protected Color selectedForeground;
	protected Color disabledSelectedBackground;

	protected Color toolbarSelectedBackground;

	protected int tabUnderlineHeight;
	protected Color tabUnderlineColor;
	protected Color tabDisabledUnderlineColor;
	protected Color tabSelectedBackground;
	protected Color tabHoverBackground;
	protected Color tabFocusBackground;

	private boolean defaults_initialized = false;

	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatToggleButtonUI();
		return instance;
	}

	@Override
	protected String getPropertyPrefix() {
		return "ToggleButton.";
	}

	@Override
	protected void installDefaults( AbstractButton b ) {
		super.installDefaults( b );

		if( !defaults_initialized ) {
			selectedBackground = UIManager.getColor( "ToggleButton.selectedBackground" );
			selectedForeground = UIManager.getColor( "ToggleButton.selectedForeground" );
			disabledSelectedBackground = UIManager.getColor( "ToggleButton.disabledSelectedBackground" );

			toolbarSelectedBackground = UIManager.getColor( "ToggleButton.toolbar.selectedBackground" );

			tabUnderlineHeight = UIManager.getInt( "ToggleButton.tab.underlineHeight" );
			tabUnderlineColor = UIManager.getColor( "ToggleButton.tab.underlineColor" );
			tabDisabledUnderlineColor = UIManager.getColor( "ToggleButton.tab.disabledUnderlineColor" );
			tabSelectedBackground = UIManager.getColor( "ToggleButton.tab.selectedBackground" );
			tabHoverBackground = UIManager.getColor( "ToggleButton.tab.hoverBackground" );
			tabFocusBackground = UIManager.getColor( "ToggleButton.tab.focusBackground" );

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

			case TAB_BUTTON_UNDERLINE_HEIGHT:
			case TAB_BUTTON_UNDERLINE_COLOR:
			case TAB_BUTTON_SELECTED_BACKGROUND:
				b.repaint();
				break;
		}
	}

	static boolean isTabButton( Component c ) {
		return c instanceof JToggleButton && clientPropertyEquals( (JToggleButton) c, BUTTON_TYPE, BUTTON_TYPE_TAB );
	}

	@Override
	protected void paintBackground( Graphics g, JComponent c ) {
		if( isTabButton( c ) ) {
			int height = c.getHeight();
			int width = c.getWidth();
			boolean selected = ((AbstractButton)c).isSelected();

			// paint background
			Color background = buttonStateColor( c,
				selected ? clientPropertyColor( c, TAB_BUTTON_SELECTED_BACKGROUND, tabSelectedBackground ) : null,
				null, tabFocusBackground, tabHoverBackground, null );
			if( background != null ) {
				g.setColor( background );
				g.fillRect( 0, 0, width, height );
			}

			// paint underline if selected
			if( selected ) {
				int underlineHeight = UIScale.scale( clientPropertyInt( c, TAB_BUTTON_UNDERLINE_HEIGHT, tabUnderlineHeight ) );
				g.setColor( c.isEnabled()
					? clientPropertyColor( c, TAB_BUTTON_UNDERLINE_COLOR, tabUnderlineColor )
					: tabDisabledUnderlineColor );
				g.fillRect( 0, height - underlineHeight, width, underlineHeight );
			}
		} else
			super.paintBackground( g, c );
	}

	@Override
	protected Color getBackground( JComponent c ) {
		ButtonModel model = ((AbstractButton)c).getModel();

		if( model.isSelected() ) {
			// in toolbar use same colors for disabled and enabled because
			// we assume that toolbar icon is shown disabled
			boolean toolBarButton = isToolBarButton( c );
			return buttonStateColor( c,
				toolBarButton ? toolbarSelectedBackground : selectedBackground,
				toolBarButton ? toolbarSelectedBackground : disabledSelectedBackground,
				null, null,
				toolBarButton ? toolbarPressedBackground : pressedBackground );
		}

		return super.getBackground( c );
	}

	@Override
	protected Color getForeground( JComponent c ) {
		ButtonModel model = ((AbstractButton)c).getModel();

		if( model.isSelected() && !isToolBarButton( c ) )
			return selectedForeground;

		return super.getForeground( c );
	}
}
