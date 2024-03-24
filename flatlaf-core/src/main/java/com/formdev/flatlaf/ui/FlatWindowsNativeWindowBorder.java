/*
 * Copyright 2021 FormDev Software GmbH
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

import java.awt.Color;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;

//
// Interesting resources:
//     https://github.com/microsoft/terminal/blob/main/src/cascadia/WindowsTerminal/NonClientIslandWindow.cpp
//     https://docs.microsoft.com/en-us/windows/win32/dwm/customframe
//     https://github.com/JetBrains/JetBrainsRuntime/blob/master/src/java.desktop/windows/native/libawt/windows/awt_Frame.cpp
//     https://github.com/JetBrains/JetBrainsRuntime/commit/d2820524a1aa211b1c49b30f659b9b4d07a6f96e
//     https://github.com/JetBrains/JetBrainsRuntime/pull/18
//     https://medium.com/swlh/customizing-the-title-bar-of-an-application-window-50a4ac3ed27e
//     https://github.com/kalbetredev/CustomDecoratedJFrame
//     https://github.com/Guerra24/NanoUI-win32
//     https://github.com/oberth/custom-chrome
//     https://github.com/rossy/borderless-window
//
//   Windows 11
//     https://docs.microsoft.com/en-us/windows/apps/desktop/modernize/apply-snap-layout-menu
//     https://github.com/dotnet/wpf/issues/4825#issuecomment-930442736
//

/**
 * Native window border support for Windows 10 when using custom decorations.
 * <p>
 * If the application wants to use custom decorations, the Windows 10 title bar is hidden
 * (including minimize, maximize and close buttons), but not the resize borders (including drop shadow).
 * Windows 10 window snapping functionality will remain unaffected:
 * https://support.microsoft.com/en-us/windows/snap-your-windows-885a9b1e-a983-a3b1-16cd-c531795e6241
 *
 * @author Karl Tauber
 * @since 1.1
 */
class FlatWindowsNativeWindowBorder
	implements FlatNativeWindowBorder.Provider
{
	private final Map<Window, WndProc> windowsMap = Collections.synchronizedMap( new IdentityHashMap<>() );
	private final EventListenerList listenerList = new EventListenerList();
	private Timer fireStateChangedTimer;

	private boolean colorizationUpToDate;
	private boolean colorizationColorAffectsBorders;
	private Color colorizationColor;
	private int colorizationColorBalance;

	private static FlatWindowsNativeWindowBorder instance;

	static FlatNativeWindowBorder.Provider getInstance() {
		// requires Windows 10
		if( !SystemInfo.isWindows_10_orLater )
			return null;

		// check whether native library was successfully loaded
		if( !FlatNativeWindowsLibrary.isLoaded() )
			return null;

		// create new instance
		if( instance == null )
			instance = new FlatWindowsNativeWindowBorder();
		return instance;
	}

	private FlatWindowsNativeWindowBorder() {
	}

	@Override
	public boolean hasCustomDecoration( Window window ) {
		return windowsMap.containsKey( window );
	}

	/**
	 * Tell the window whether the application wants to use custom decorations.
	 * If {@code true}, the Windows 10 title bar is hidden (including minimize,
	 * maximize and close buttons), but not the resize borders (including drop shadow).
	 */
	@Override
	public void setHasCustomDecoration( Window window, boolean hasCustomDecoration ) {
		if( hasCustomDecoration )
			install( window );
		else
			uninstall( window );
	}

	private void install( Window window ) {
		// requires Windows 10
		if( !SystemInfo.isWindows_10_orLater )
			return;

		// only JFrame and JDialog are supported
		if( !(window instanceof JFrame) && !(window instanceof JDialog) )
			return;

		// not supported if frame/dialog is undecorated
		if( (window instanceof Frame && ((Frame)window).isUndecorated()) ||
			(window instanceof Dialog && ((Dialog)window).isUndecorated()) )
		  return;

		// check whether already installed
		if( windowsMap.containsKey( window ) )
			return;

		// install
		try {
			WndProc wndProc = new WndProc( window );
			if( wndProc.hwnd == 0 )
				return;

			windowsMap.put( window, wndProc );
		} catch( UnsatisfiedLinkError ex ) {
			// catch for the case that the operating system prevents execution of DLL
			// (e.g. if DLLs in temp folder are restricted)
			// --> continue application without custom decorations
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	private void uninstall( Window window ) {
		WndProc wndProc = windowsMap.remove( window );
		if( wndProc != null )
			wndProc.uninstall();
	}

	@Override
	public void updateTitleBarInfo( Window window, int titleBarHeight, Predicate<Point> captionHitTestCallback,
		Rectangle appIconBounds, Rectangle minimizeButtonBounds, Rectangle maximizeButtonBounds,
		Rectangle closeButtonBounds )
	{
		WndProc wndProc = windowsMap.get( window );
		if( wndProc == null )
			return;

		wndProc.titleBarHeight = titleBarHeight;
		wndProc.captionHitTestCallback = captionHitTestCallback;
		wndProc.appIconBounds = cloneRectange( appIconBounds );
		wndProc.minimizeButtonBounds = cloneRectange( minimizeButtonBounds );
		wndProc.maximizeButtonBounds = cloneRectange( maximizeButtonBounds );
		wndProc.closeButtonBounds = cloneRectange( closeButtonBounds );
	}

	private static Rectangle cloneRectange( Rectangle rect ) {
		return (rect != null) ? new Rectangle( rect ) : null;
	}

	@Override
	public boolean showWindow( Window window, int cmd ) {
		WndProc wndProc = windowsMap.get( window );
		if( wndProc == null )
			return false;

		wndProc.showWindow( wndProc.hwnd, cmd );
		return true;
	}

	@Override
	public boolean isColorizationColorAffectsBorders() {
		updateColorization();
		return colorizationColorAffectsBorders;
	}

	@Override
	public Color getColorizationColor() {
		updateColorization();
		return colorizationColor;
	}

	@Override
	public int getColorizationColorBalance() {
		updateColorization();
		return colorizationColorBalance;
	}

	private void updateColorization() {
		if( colorizationUpToDate )
			return;
		colorizationUpToDate = true;

		String subKey = "SOFTWARE\\Microsoft\\Windows\\DWM";

		int value = registryGetIntValue( subKey, "ColorPrevalence", -1 );
		colorizationColorAffectsBorders = (value > 0);

		value = registryGetIntValue( subKey, "ColorizationColor", -1 );
		colorizationColor = (value != -1) ? new Color( value ) : null;

		colorizationColorBalance = registryGetIntValue( subKey, "ColorizationColorBalance", -1 );
	}

	private native static int registryGetIntValue( String key, String valueName, int defaultValue );

	@Override
	public void addChangeListener( ChangeListener l ) {
		listenerList.add( ChangeListener.class, l );
	}

	@Override
	public void removeChangeListener( ChangeListener l ) {
		listenerList.remove( ChangeListener.class, l );
	}

	private void fireStateChanged() {
		Object[] listeners = listenerList.getListenerList();
		if( listeners.length == 0 )
			return;

		ChangeEvent e = new ChangeEvent( this );
		for( int i = 0; i < listeners.length; i += 2 ) {
			if( listeners[i] == ChangeListener.class )
				((ChangeListener)listeners[i+1]).stateChanged( e );
		}
	}

	/**
	 * Because there may be sent many WM_DWMCOLORIZATIONCOLORCHANGED messages,
	 * slightly delay event firing and fire it only once (on the AWT thread).
	 */
	void fireStateChangedLaterOnce() {
		EventQueue.invokeLater( () -> {
			if( fireStateChangedTimer != null ) {
				fireStateChangedTimer.restart();
				return;
			}

			fireStateChangedTimer = new Timer( 300, e -> {
				fireStateChangedTimer = null;
				colorizationUpToDate = false;

				fireStateChanged();
			} );
			fireStateChangedTimer.setRepeats( false );
			fireStateChangedTimer.start();
		} );
	}

	//---- class WndProc ------------------------------------------------------

	private class WndProc
		implements PropertyChangeListener
	{
		// WM_NCHITTEST mouse position codes
		private static final int
			HTCLIENT = 1,
			HTCAPTION = 2,
			HTSYSMENU = 3,
			HTMINBUTTON = 8,
			HTMAXBUTTON = 9,
			HTTOP = 12,
			HTCLOSE = 20;

		private Window window;
		private final long hwnd;

		// Swing coordinates/values may be scaled on a HiDPI screen
		private int titleBarHeight; // measured from window top edge, which may be out-of-screen if maximized
		private Predicate<Point> captionHitTestCallback;
		private Rectangle appIconBounds;
		private Rectangle minimizeButtonBounds;
		private Rectangle maximizeButtonBounds;
		private Rectangle closeButtonBounds;

		WndProc( Window window ) {
			this.window = window;

			hwnd = installImpl( window );
			if( hwnd == 0 )
				return;

			// remove the OS window title bar
			updateFrame( hwnd, (window instanceof JFrame) ? ((JFrame)window).getExtendedState() : 0 );

			// set window background (used when resizing window)
			updateWindowBackground();
			window.addPropertyChangeListener( "background", this );
		}

		void uninstall() {
			window.removePropertyChangeListener( "background", this );

			uninstallImpl( hwnd );

			// cleanup
			window = null;
		}

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			updateWindowBackground();
		}

		private void updateWindowBackground() {
			Color bg = window.getBackground();
			if( bg != null )
				setWindowBackground( hwnd, bg.getRed(), bg.getGreen(), bg.getBlue() );
		}

		private native long installImpl( Window window );
		private native void uninstallImpl( long hwnd );
		private native void updateFrame( long hwnd, int state );
		private native void setWindowBackground( long hwnd, int r, int g, int b );
		private native void showWindow( long hwnd, int cmd );

		// invoked from native code
		private int onNcHitTest( int x, int y, boolean isOnResizeBorder ) {
			// scale-down mouse x/y because Swing coordinates/values may be scaled on a HiDPI screen
			Point pt = scaleDown( x, y );

			// return HTSYSMENU if mouse is over application icon
			//   - left-click on HTSYSMENU area shows system menu
			//   - double-left-click sends WM_CLOSE
			if( contains( appIconBounds, pt ) )
				return HTSYSMENU;

			// return HTMINBUTTON if mouse is over minimize button
			//   - hovering mouse over HTMINBUTTON area shows tooltip on Windows 10/11
			if( contains( minimizeButtonBounds, pt ) )
				return HTMINBUTTON;

			// return HTMAXBUTTON if mouse is over maximize/restore button
			//   - hovering mouse over HTMAXBUTTON area shows tooltip on Windows 10
			//   - hovering mouse over HTMAXBUTTON area shows snap layouts menu on Windows 11
			//     https://docs.microsoft.com/en-us/windows/apps/desktop/modernize/apply-snap-layout-menu
			if( contains( maximizeButtonBounds, pt ) )
				return HTMAXBUTTON;

			// return HTCLOSE if mouse is over close button
			//   - hovering mouse over HTCLOSE area shows tooltip on Windows 10/11
			if( contains( closeButtonBounds, pt ) )
				return HTCLOSE;

			// return HTTOP if mouse is over top resize border
			//   - hovering mouse shows vertical resize cursor
			//   - left-click and drag vertically resizes window
			if( isOnResizeBorder )
				return HTTOP;

			boolean isOnTitleBar = (pt.y < titleBarHeight);
			if( isOnTitleBar ) {
				// return HTCLIENT if mouse is over any Swing component in title bar
				// that processes mouse events (e.g. buttons, menus, etc)
				//   - Windows ignores mouse events in this area
				try {
					if( captionHitTestCallback != null && !captionHitTestCallback.test( pt ) )
						return HTCLIENT;
				} catch( Throwable ex ) {
					// ignore
				}

				// return HTCAPTION if mouse is over title bar
				//   - right-click shows system menu
				//   - double-left-click maximizes/restores window size
				return HTCAPTION;
			}

			// return HTCLIENT
			//   - Windows ignores mouse events in this area
			return HTCLIENT;
		}

		private boolean contains( Rectangle rect, Point pt ) {
			return (rect != null && rect.contains( pt ) );
		}

		/**
		 * Scales down in the same way as AWT.
		 * See AwtWin32GraphicsDevice::ScaleDownX() and ::ScaleDownY()
		 */
		private Point scaleDown( int x, int y ) {
			GraphicsConfiguration gc = window.getGraphicsConfiguration();
			if( gc == null )
				return new Point( x, y );

			AffineTransform t = gc.getDefaultTransform();
			return new Point( clipRound( x / t.getScaleX() ), clipRound( y / t.getScaleY() ) );
		}

		/**
		 * Rounds in the same way as AWT.
		 * See AwtWin32GraphicsDevice::ClipRound()
		 */
		private int clipRound( double value ) {
			value -= 0.5;
			if( value < Integer.MIN_VALUE )
				return Integer.MIN_VALUE;
			if( value > Integer.MAX_VALUE )
				return Integer.MAX_VALUE;
			return (int) Math.ceil( value );
		}

		// invoked from native code
		private boolean isFullscreen() {
			GraphicsConfiguration gc = window.getGraphicsConfiguration();
			if( gc == null )
				return false;
			return gc.getDevice().getFullScreenWindow() == window;
		}

		// invoked from native code
		private void fireStateChangedLaterOnce() {
			FlatWindowsNativeWindowBorder.this.fireStateChangedLaterOnce();
		}
	}
}
