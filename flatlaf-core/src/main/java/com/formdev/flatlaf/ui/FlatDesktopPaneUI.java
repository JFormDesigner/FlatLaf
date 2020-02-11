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

package com.formdev.flatlaf.ui;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JDesktopPane}.
 *
 * <!-- BasicDesktopPaneUI -->
 *
 * @uiDefault Desktop.background					Color
 * @uiDefault Desktop.minOnScreenInsets				Insets
 *
 * @author Karl Tauber
 */
public class FlatDesktopPaneUI
	extends BasicDesktopPaneUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatDesktopPaneUI();
	}

	@Override
	protected void installDesktopManager() {
		desktopManager = desktop.getDesktopManager();
		if( desktopManager == null ) {
			desktopManager = new FlatDesktopManager();
			desktop.setDesktopManager( desktopManager );
		}
	}

	//---- class FlatDesktopManager -------------------------------------------

	private class FlatDesktopManager
		extends DefaultDesktopManager
		implements UIResource
	{
		@Override
		public void iconifyFrame( JInternalFrame f ) {
			super.iconifyFrame( f );

			((FlatDesktopIconUI)f.getDesktopIcon().getUI()).updateDockIcon();
		}
	}
}
