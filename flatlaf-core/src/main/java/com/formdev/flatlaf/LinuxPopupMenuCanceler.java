/*
 * Copyright 2025 FormDev Software GmbH
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

package com.formdev.flatlaf;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Cancels (hides) popup menus on Linux.
 * <p>
 * On Linux, popups are not hidden under following conditions, which results in
 * misplaced popups:
 * <ul>
 *   <li>window moved or resized
 *   <li>window maximized or restored
 *   <li>window iconified
 *   <li>window deactivated (e.g. activated other application)
 * </ul>
 *
 * On Windows and macOS, popups are automatically hidden.
 * <p>
 * The implementation is similar to what's done in
 * {@code javax.swing.plaf.basic.BasicPopupMenuUI.MouseGrabber},
 * but only hides popup in some conditions.
 *
 * @author Karl Tauber
 */
class LinuxPopupMenuCanceler
	extends WindowAdapter
	implements ChangeListener, ComponentListener
{
	private MenuElement[] lastPathSelectedPath;
	private Window window;

	LinuxPopupMenuCanceler() {
		MenuSelectionManager msm = MenuSelectionManager.defaultManager();
		msm.addChangeListener( this );

		lastPathSelectedPath = msm.getSelectedPath();
		if( lastPathSelectedPath.length > 0 )
			addWindowListeners( lastPathSelectedPath[0] );
	}

	void uninstall() {
		MenuSelectionManager.defaultManager().removeChangeListener( this );
	}

	private void addWindowListeners( MenuElement selected ) {
		// see BasicPopupMenuUI.MouseGrabber.grabWindow()
		Component invoker = selected.getComponent();
		if( invoker instanceof JPopupMenu )
			invoker = ((JPopupMenu)invoker).getInvoker();
		window = (invoker instanceof Window)
			? (Window) invoker
			: SwingUtilities.windowForComponent( invoker );

		if( window != null ) {
			window.addWindowListener( this );
			window.addComponentListener( this );
		}
	}

	private void removeWindowListeners() {
		if( window != null ) {
			window.removeWindowListener( this );
			window.removeComponentListener( this );
			window = null;
		}
	}

	private void cancelPopupMenu() {
		try {
			MenuSelectionManager msm = MenuSelectionManager.defaultManager();
			MenuElement[] selectedPath = msm.getSelectedPath();
			for( MenuElement e : selectedPath ) {
				if( e instanceof JPopupMenu )
					((JPopupMenu)e).putClientProperty( "JPopupMenu.firePopupMenuCanceled", true );
			}
			msm.clearSelectedPath();
		} catch( RuntimeException ex ) {
			removeWindowListeners();
			throw ex;
		} catch( Error ex ) {
			removeWindowListeners();
			throw ex;
		}
	}

	//---- ChangeListener ----

	@Override
	public void stateChanged( ChangeEvent e ) {
		MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();

		if( selectedPath.length == 0 )
			removeWindowListeners();
		else if( lastPathSelectedPath.length == 0 )
			addWindowListeners( selectedPath[0] );

		lastPathSelectedPath = selectedPath;
	}

	//---- WindowListener ----

	@Override
	public void windowIconified( WindowEvent e ) {
		cancelPopupMenu();
	}

	@Override
	public void windowDeactivated( WindowEvent e ) {
		cancelPopupMenu();
	}

	@Override
	public void windowClosing( WindowEvent e ) {
		cancelPopupMenu();
	}

	//---- ComponentListener ----

	@Override
	public void componentResized( ComponentEvent e ) {
		cancelPopupMenu();
	}

	@Override
	public void componentMoved( ComponentEvent e ) {
		cancelPopupMenu();
	}

	@Override
	public void componentShown( ComponentEvent e ) {
	}

	@Override
	public void componentHidden( ComponentEvent e ) {
		cancelPopupMenu();
	}
}
