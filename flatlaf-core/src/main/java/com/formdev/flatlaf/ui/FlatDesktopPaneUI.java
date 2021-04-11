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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.JComponent;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.plaf.ComponentUI;
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
	private LayoutDockListener layoutDockListener;
	private boolean layoutDockPending;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatDesktopPaneUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		layoutDockLaterOnce();
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		layoutDockListener = new LayoutDockListener();
		desktop.addContainerListener( layoutDockListener );
		desktop.addComponentListener( layoutDockListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		desktop.removeContainerListener( layoutDockListener );
		desktop.removeComponentListener( layoutDockListener );
		layoutDockListener = null;
	}

	private void layoutDockLaterOnce() {
		if( layoutDockPending )
			return;
		layoutDockPending = true;

		EventQueue.invokeLater( () -> {
			layoutDockPending = false;
			if( desktop != null )
				layoutDock();
		} );
	}

	protected void layoutDock() {
		Dimension desktopSize = desktop.getSize();
		int x = 0;
		int y = desktopSize.height;
		int rowHeight = 0;

		for( Component c : desktop.getComponents() ) {
			if( !(c instanceof JDesktopIcon) )
				continue;

			JDesktopIcon icon = (JDesktopIcon) c;
			Dimension iconSize = icon.getPreferredSize();

			if( x + iconSize.width > desktopSize.width ) {
				// new row
				x = 0;
				y -= rowHeight;
				rowHeight = 0;
			}

			icon.setLocation( x, y - iconSize.height );

			x += iconSize.width;
			rowHeight = Math.max( iconSize.height, rowHeight );
		}
	}

	//---- class LayoutDockListener -------------------------------------------

	private class LayoutDockListener
		extends ComponentAdapter
		implements ContainerListener
	{
		@Override
		public void componentAdded( ContainerEvent e ) {
			layoutDockLaterOnce();
		}

		@Override
		public void componentRemoved( ContainerEvent e ) {
			layoutDockLaterOnce();
		}

		@Override
		public void componentResized( ComponentEvent e ) {
			layoutDockLaterOnce();
		}
	}
}
