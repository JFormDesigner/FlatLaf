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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.NativeLibrary;
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

	private static NativeLibrary nativeLibrary;
	private static FlatWindowsNativeWindowBorder instance;

	static FlatNativeWindowBorder.Provider getInstance() {
		// requires Windows 10
		if( !SystemInfo.isWindows_10_orLater )
			return null;

		// load native library
		if( nativeLibrary == null ) {
			if( !SystemInfo.isJava_9_orLater ) {
				// In Java 8, load jawt.dll (part of JRE) explicitly because it
				// is not found when running application with <jdk>/bin/java.exe.
				// When using <jdk>/jre/bin/java.exe, it is found.
				// jawt.dll is located in <jdk>/jre/bin/.
				// Java 9 and later does not have this problem.
				try {
					System.loadLibrary( "jawt" );
				} catch( Exception ex ) {
					LoggingFacade.INSTANCE.logSevere( null, ex );
				}
			}

			String libraryName = "com/formdev/flatlaf/natives/flatlaf-windows-x86";
			if( SystemInfo.isX86_64 )
				libraryName += "_64";

			nativeLibrary = new NativeLibrary( libraryName, null, true );
		}

		// check whether native library was successfully loaded
		if( !nativeLibrary.isLoaded() )
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
	 * Tell the window whether the application wants use custom decorations.
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
		WndProc wndProc = new WndProc( window );
		if( wndProc.hwnd == 0 )
			return;

		windowsMap.put( window, wndProc );
	}

	private void uninstall( Window window ) {
		WndProc wndProc = windowsMap.remove( window );
		if( wndProc != null )
			wndProc.uninstall();
	}

	@Override
	public void setTitleBarHeight( Window window, int titleBarHeight ) {
		WndProc wndProc = windowsMap.get( window );
		if( wndProc == null )
			return;

		wndProc.titleBarHeight = titleBarHeight;
	}

	@Override
	public void setTitleBarHitTestSpots( Window window, List<Rectangle> hitTestSpots ) {
		WndProc wndProc = windowsMap.get( window );
		if( wndProc == null )
			return;

		wndProc.hitTestSpots = hitTestSpots.toArray( new Rectangle[hitTestSpots.size()] );
	}

	@Override
	public void setTitleBarAppIconBounds( Window window, Rectangle appIconBounds ) {
		WndProc wndProc = windowsMap.get( window );
		if( wndProc == null )
			return;

		wndProc.appIconBounds = (appIconBounds != null) ? new Rectangle( appIconBounds ) : null;
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
	{
		// WM_NCHITTEST mouse position codes
		private static final int
			HTCLIENT = 1,
			HTCAPTION = 2,
			HTSYSMENU = 3,
			HTTOP = 12;

		private Window window;
		private final long hwnd;

		private int titleBarHeight;
		private Rectangle[] hitTestSpots;
		private Rectangle appIconBounds;

		WndProc( Window window ) {
			this.window = window;

			hwnd = installImpl( window );
			if( hwnd == 0 )
				return;

			// remove the OS window title bar
			updateFrame( hwnd, (window instanceof JFrame) ? ((JFrame)window).getExtendedState() : 0 );
		}

		void uninstall() {
			uninstallImpl( hwnd );

			// cleanup
			window = null;
		}

		private native long installImpl( Window window );
		private native void uninstallImpl( long hwnd );
		private native void updateFrame( long hwnd, int state );
		private native void showWindow( long hwnd, int cmd );

		// invoked from native code
		private int onNcHitTest( int x, int y, boolean isOnResizeBorder ) {
			// scale-down mouse x/y
			Point pt = scaleDown( x, y );
			int sx = pt.x;
			int sy = pt.y;

			// return HTSYSMENU if mouse is over application icon
			//   - left-click on HTSYSMENU area shows system menu
			//   - double-left-click sends WM_CLOSE
			if( appIconBounds != null && appIconBounds.contains( sx, sy ) )
				return HTSYSMENU;

			boolean isOnTitleBar = (sy < titleBarHeight);

			if( isOnTitleBar ) {
				// use a second reference to the array to avoid that it can be changed
				// in another thread while processing the array
				Rectangle[] hitTestSpots2 = hitTestSpots;
				for( Rectangle spot : hitTestSpots2 ) {
					if( spot.contains( sx, sy ) )
						return HTCLIENT;
				}
				return isOnResizeBorder ? HTTOP : HTCAPTION;
			}

			return isOnResizeBorder ? HTTOP : HTCLIENT;
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
