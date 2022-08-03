/*
 * Copyright 2022 FormDev Software GmbH
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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Improves usability of submenus by using a
 * <a href="https://height.app/blog/guide-to-build-context-menus#safe-triangle">safe triangle</a>
 * to avoid that the submenu closes while the user moves the mouse to it.
 *
 * @author Karl Tauber
 */
class SubMenuUsabilityHelper
	implements ChangeListener
{
	private static final String KEY_USE_SAFE_TRIANGLE = "Menu.useSafeTriangle";
	private static final String KEY_SHOW_SAFE_TRIANGLE = "FlatLaf.debug.menu.showSafeTriangle";

	// Using a static field to ensure that there is only one instance in the system.
	// Multiple instances would freeze the application.
	// https://github.com/apache/netbeans/issues/4231#issuecomment-1179616607
	private static SubMenuUsabilityHelper instance;

	private SubMenuEventQueue subMenuEventQueue;
	private SafeTrianglePainter safeTrianglePainter;
	private boolean changePending;

	// mouse location in screen coordinates
	private int mouseX;
	private int mouseY;

	// target popup bounds in screen coordinates
	private int targetX;
	private int targetTopY;
	private int targetBottomY;

	private Rectangle invokerBounds;

	static synchronized boolean install() {
		if( instance != null )
			return false;

		instance = new SubMenuUsabilityHelper();
		MenuSelectionManager.defaultManager().addChangeListener( instance );
		return true;
	}

	static synchronized void uninstall() {
		if( instance == null )
			return;

		MenuSelectionManager.defaultManager().removeChangeListener( instance );
		instance.uninstallEventQueue();
		instance = null;
	}

	@Override
	public void stateChanged( ChangeEvent e ) {
		if( !FlatUIUtils.getUIBoolean( KEY_USE_SAFE_TRIANGLE, true ))
			return;

		// handle menu selection change later, but only once in case of temporary changes
		// e.g. moving mouse from one menu item to another one, fires two events:
		//    1. old menu item is removed from menu selection
		//    2. new menu item is added to menu selection
		synchronized( this ) {
			if( changePending )
				return;
			changePending = true;
		}

		EventQueue.invokeLater( () -> {
			synchronized( this ) {
				changePending = false;
			}
			menuSelectionChanged();
		} );
	}

	private void menuSelectionChanged() {
		MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();

/*debug
		System.out.println( "--- " + path.length );
		for( int i = 0; i < path.length; i++ )
			System.out.println( "   " + i + ": " + path[i].getClass().getName() );
debug*/

		// find submenu in menu selection
		int subMenuIndex = findSubMenu( path );

		// uninstall if there is no submenu in selection
		if( subMenuIndex < 0 || subMenuIndex != path.length - 1 ) {
			uninstallEventQueue();
			return;
		}

		// get current mouse location
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		Point mouseLocation = (pointerInfo != null) ? pointerInfo.getLocation() : new Point();
		mouseX = mouseLocation.x;
		mouseY = mouseLocation.y;

		// check whether popup is showing, which is e.g. not the case if it is empty
		JPopupMenu popup = (JPopupMenu) path[subMenuIndex];
		if( !popup.isShowing() ) {
			uninstallEventQueue();
			return;
		}

		// get invoker screen bounds
		Component invoker = popup.getInvoker();
		invokerBounds = (invoker != null)
			? new Rectangle( invoker.getLocationOnScreen(), invoker.getSize() )
			: null;

		// check whether mouse location is within invoker
		if( invokerBounds != null && !invokerBounds.contains( mouseX, mouseY ) ) {
			uninstallEventQueue();
			return;
		}

		// compute top/bottom target locations
		Point popupLocation = popup.getLocationOnScreen();
		Dimension popupSize = popup.getSize();
		targetX = (mouseX < popupLocation.x + (popupSize.width / 2))
			? popupLocation.x
			: popupLocation.x + popupSize.width;
		targetTopY = popupLocation.y;
		targetBottomY = popupLocation.y + popupSize.height;

		// install own event queue to supress mouse events when mouse is moved within safe triangle
		if( subMenuEventQueue == null )
			subMenuEventQueue = new SubMenuEventQueue();

		// create safe triangle painter
		if( safeTrianglePainter == null && UIManager.getBoolean( KEY_SHOW_SAFE_TRIANGLE ) )
			safeTrianglePainter = new SafeTrianglePainter( popup );
	}

	private void uninstallEventQueue() {
		if( subMenuEventQueue != null ) {
			subMenuEventQueue.uninstall();
			subMenuEventQueue = null;
		}

		if( safeTrianglePainter != null ) {
			safeTrianglePainter.uninstall();
			safeTrianglePainter = null;
		}
	}

	private int findSubMenu( MenuElement[] path ) {
		for( int i = path.length - 1; i >= 1; i-- ) {
			if( path[i] instanceof JPopupMenu &&
				path[i - 1] instanceof JMenu &&
				!((JMenu)path[i - 1]).isTopLevelMenu() )
			  return i;
		}
		return -1;
	}

	private Polygon createSafeTriangle() {
		return new Polygon(
			new int[] { mouseX, targetX, targetX },
			new int[] { mouseY, targetTopY, targetBottomY },
			3 );
	}

	//---- class SubMenuEventQueue --------------------------------------------

	private class SubMenuEventQueue
		extends EventQueue
	{
		private Timer mouseUpdateTimer;
		private Timer timeoutTimer;

		private int newMouseX;
		private int newMouseY;
		private AWTEvent lastMouseEvent;

		SubMenuEventQueue() {
			// timer used to slightly delay update of mouse location used for safe triangle
			mouseUpdateTimer = new Timer( 50, e -> {
				mouseX = newMouseX;
				mouseY = newMouseY;

				if( safeTrianglePainter != null )
					safeTrianglePainter.repaint();
			} );
			mouseUpdateTimer.setRepeats( false );

			// timer used to timeout safe triangle when mouse stops moving
			timeoutTimer = new Timer( 200, e -> {
				if( invokerBounds != null && !invokerBounds.contains( newMouseX, newMouseY ) ) {
					// post last mouse event, which selects menu item at mouse location
					if( lastMouseEvent != null ) {
						postEvent( lastMouseEvent );
						lastMouseEvent = null;
					}

					uninstallEventQueue();
					return;
				}
			} );
			timeoutTimer.setRepeats( false );

			Toolkit.getDefaultToolkit().getSystemEventQueue().push( this );
		}

		void uninstall() {
			mouseUpdateTimer.stop();
			mouseUpdateTimer = null;

			timeoutTimer.stop();
			timeoutTimer = null;

			lastMouseEvent = null;

			super.pop();
		}

		@Override
		protected void dispatchEvent( AWTEvent e ) {
			int id = e.getID();

			if( e instanceof MouseEvent &&
				(id == MouseEvent.MOUSE_MOVED || id == MouseEvent.MOUSE_DRAGGED) )
			{
				newMouseX = ((MouseEvent)e).getXOnScreen();
				newMouseY = ((MouseEvent)e).getYOnScreen();

				if( safeTrianglePainter != null )
					safeTrianglePainter.repaint();

				mouseUpdateTimer.stop();
				timeoutTimer.stop();

				// check whether mouse moved within safe triangle
				if( createSafeTriangle().contains( newMouseX, newMouseY ) ) {
					// update mouse location delayed (this changes the safe triangle)
					mouseUpdateTimer.start();

					timeoutTimer.start();

					// remember last mouse event, which will be posted if the mouse stops moving
					lastMouseEvent = e;

					// ignore mouse event
					return;
				}

				// update mouse location immediately (this changes the safe triangle)
				mouseX = newMouseX;
				mouseY = newMouseY;
			}

			super.dispatchEvent( e );
		}
	}

	//---- class SafeTrianglePainter ------------------------------------------

	private class SafeTrianglePainter
		extends JComponent
	{
		SafeTrianglePainter( JPopupMenu popup ) {
			Window window = SwingUtilities.windowForComponent( popup.getInvoker() );
			if( window instanceof RootPaneContainer ) {
				JLayeredPane layeredPane = ((RootPaneContainer)window).getLayeredPane();
				setSize( layeredPane.getSize() );
				layeredPane.add( this, Integer.valueOf( JLayeredPane.POPUP_LAYER + 1 ) );
			}
		}

		void uninstall() {
			Container parent = getParent();
			if( parent != null ) {
				parent.remove( this );
				parent.repaint();
			}
		}

		@Override
		protected void paintComponent( Graphics g ) {
			Point locationOnScreen = getLocationOnScreen();
			g.translate( -locationOnScreen.x, -locationOnScreen.y );

			g.setColor( Color.red );
			((Graphics2D)g).draw( createSafeTriangle() );
		}
	}
}
