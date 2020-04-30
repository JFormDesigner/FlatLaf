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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JMenuBar}.
 *
 * <!-- BasicMenuBarUI -->
 *
 * @uiDefault MenuBar.font								Font
 * @uiDefault MenuBar.background						Color
 * @uiDefault MenuBar.foreground						Color
 * @uiDefault MenuBar.border							Border
 *
 * @author Karl Tauber
 */
public class FlatMenuBarUI
	extends BasicMenuBarUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatMenuBarUI();
	}

	/*
	 * WARNING: This class is not used on macOS if screen menu bar is enabled.
	 *          Do not add any functionality here.
	 */

	@Override
	protected void installKeyboardActions() {
		super.installKeyboardActions();

		ActionMap map = SwingUtilities.getUIActionMap( menuBar );
		if( map == null ) {
			map = new ActionMapUIResource();
			SwingUtilities.replaceUIActionMap( menuBar, map );
		}
		map.put( "takeFocus", new TakeFocus() );
	}

	//---- class TakeFocus ----------------------------------------------------

	/**
	 * Activates the menu bar and shows mnemonics.
	 * On Windows, the popup of the first menu is not shown.
	 * On other platforms, the popup of the first menu is shown.
	 */
	private static class TakeFocus
		extends AbstractAction
	{
		@Override
		public void actionPerformed( ActionEvent e ) {
			JMenuBar menuBar = (JMenuBar) e.getSource();
			JMenu menu = menuBar.getMenu( 0 );
			if( menu != null ) {
				MenuSelectionManager.defaultManager().setSelectedPath( SystemInfo.IS_WINDOWS
					? new MenuElement[] { menuBar, menu }
					: new MenuElement[] { menuBar, menu, menu.getPopupMenu() } );

				FlatLaf.showMnemonics( menuBar );
			}
		}
	}
}
