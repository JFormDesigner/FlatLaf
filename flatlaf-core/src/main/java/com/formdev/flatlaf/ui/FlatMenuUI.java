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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JMenu}.
 *
 * <!-- BasicMenuUI -->
 *
 * @uiDefault Menu.font												Font
 * @uiDefault Menu.background										Color
 * @uiDefault Menu.foreground										Color
 * @uiDefault Menu.disabledForeground								Color
 * @uiDefault Menu.selectionBackground								Color
 * @uiDefault Menu.selectionForeground								Color
 * @uiDefault Menu.acceleratorForeground							Color
 * @uiDefault Menu.acceleratorSelectionForeground					Color
 * @uiDefault MenuItem.acceleratorFont								Font		defaults to MenuItem.font
 * @uiDefault MenuItem.acceleratorDelimiter							String
 * @uiDefault Menu.border											Border
 * @uiDefault Menu.borderPainted									boolean
 * @uiDefault Menu.margin											Insets
 * @uiDefault Menu.arrowIcon										Icon
 * @uiDefault Menu.checkIcon										Icon
 * @uiDefault Menu.opaque											boolean
 * @uiDefault Menu.crossMenuMnemonic								boolean	default is false
 * @uiDefault Menu.useMenuBarBackgroundForTopLevel					boolean	default is false
 * @uiDefault MenuBar.background									Color	used if Menu.useMenuBarBackgroundForTopLevel is true
 *
 * <!-- FlatMenuUI -->
 *
 * @uiDefault MenuItem.iconTextGap									int
 * @uiDefault MenuBar.hoverBackground								Color
 *
 * @author Karl Tauber
 */
public class FlatMenuUI
	extends BasicMenuUI
{
	private Color hoverBackground;
	private FlatMenuItemRenderer renderer;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatMenuUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installProperty( menuItem, "iconTextGap", FlatUIUtils.getUIInt( "MenuItem.iconTextGap", 4 ) );

		menuItem.setRolloverEnabled( true );

		hoverBackground = UIManager.getColor( "MenuBar.hoverBackground" );
		renderer = createRenderer();
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		hoverBackground = null;
		renderer = null;
	}

	protected FlatMenuItemRenderer createRenderer() {
		return new FlatMenuRenderer( menuItem, checkIcon, arrowIcon, acceleratorFont, acceleratorDelimiter );
	}

	@Override
	protected MouseInputListener createMouseInputListener( JComponent c ) {
		return new BasicMenuUI.MouseInputHandler() {
			@Override
			public void mouseEntered( MouseEvent e ) {
				super.mouseEntered( e );
				rollover( e, true );
			}

			@Override
			public void mouseExited( MouseEvent e ) {
				super.mouseExited( e );
				rollover( e, false );
			}

			private void rollover( MouseEvent e, boolean rollover ) {
				JMenu menu = (JMenu) e.getSource();
				if( menu.isTopLevelMenu() && menu.isRolloverEnabled() ) {
					menu.getModel().setRollover( rollover );
					menu.repaint();
				}
			}
		};
	}

	@Override
	protected Dimension getPreferredMenuItemSize( JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap ) {
		return renderer.getPreferredMenuItemSize();
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		renderer.paintMenuItem( g, selectionBackground, selectionForeground, disabledForeground,
			acceleratorForeground, acceleratorSelectionForeground );
	}

	//---- class FlatMenuRenderer ---------------------------------------------

	protected class FlatMenuRenderer
		extends FlatMenuItemRenderer
	{
		protected FlatMenuRenderer( JMenuItem menuItem, Icon checkIcon, Icon arrowIcon,
			Font acceleratorFont, String acceleratorDelimiter )
		{
			super( menuItem, checkIcon, arrowIcon, acceleratorFont, acceleratorDelimiter );
		}

		@Override
		protected void paintBackground( Graphics g, Color selectionBackground ) {
			ButtonModel model = menuItem.getModel();
			if( model.isRollover() && !model.isArmed() && !model.isSelected() &&
				model.isEnabled() && ((JMenu)menuItem).isTopLevelMenu() )
			{
				g.setColor( FlatUIUtils.deriveColor( hoverBackground, menuItem.getBackground() ) );
				g.fillRect( 0, 0, menuItem.getWidth(), menuItem.getHeight() );
			} else
				super.paintBackground( g, selectionBackground );
		}
	}
}
