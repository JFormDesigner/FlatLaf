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

package com.formdev.flatlaf.natives.jna.windows;

import static com.sun.jna.platform.win32.ShellAPI.*;
import static com.sun.jna.platform.win32.WinReg.*;
import static com.sun.jna.platform.win32.WinUser.*;
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
import com.formdev.flatlaf.ui.FlatNativeWindowBorder;
import com.formdev.flatlaf.util.SystemInfo;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WTypes.LPWSTR;
import com.sun.jna.platform.win32.WinDef.HMENU;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.WindowProc;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

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
public class FlatWindowsNativeWindowBorder
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

	public static FlatNativeWindowBorder.Provider getInstance() {
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
		WndProc wndProc = new WndProc( window );
		windowsMap.put( window, wndProc );
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

		User32.INSTANCE.ShowWindow( wndProc.hwnd, cmd );
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

	private static int registryGetIntValue( String key, String valueName, int defaultValue ) {
		try {
			return Advapi32Util.registryGetIntValue( HKEY_CURRENT_USER, key, valueName );
		} catch( RuntimeException ex ) {
			return defaultValue;
		}
	}

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
		implements WindowProc, PropertyChangeListener
	{
		private static final int GWLP_WNDPROC = -4;

		private static final int
			WM_MOVE = 0x0003,
			WM_ERASEBKGND = 0x0014,
			WM_NCCALCSIZE = 0x0083,
			WM_NCHITTEST = 0x0084,

			WM_NCMOUSEMOVE = 0x00A0,
			WM_NCLBUTTONDOWN = 0x00A1,
			WM_NCLBUTTONUP = 0x00A2,
			WM_NCRBUTTONUP = 0x00A5,

			WM_MOUSEMOVE= 0x0200,
			WM_LBUTTONDOWN = 0x0201,
			WM_LBUTTONUP = 0x0202,

			WM_MOVING = 0x0216,
			WM_ENTERSIZEMOVE = 0x0231,
			WM_EXITSIZEMOVE = 0x0232,

			WM_DPICHANGED = 0x02E0,

			WM_DWMCOLORIZATIONCOLORCHANGED = 0x0320;

		// WM_SIZE wParam
		private static final int
			SIZE_MINIMIZED = 1,
			SIZE_MAXIMIZED = 2;

		// WM_NCHITTEST mouse position codes
		private static final int
			HTCLIENT = 1,
			HTCAPTION = 2,
			HTSYSMENU = 3,
			HTMINBUTTON = 8,
			HTMAXBUTTON = 9,
			HTTOP = 12,
			HTCLOSE = 20;

		private static final int ABS_AUTOHIDE = 0x0000001;
		private static final int ABM_GETAUTOHIDEBAREX = 0x0000000b;

		private static final int
			SC_SIZE = 0xF000,
			SC_MOVE = 0xF010,
			SC_MINIMIZE = 0xF020,
			SC_MAXIMIZE = 0xF030,
			SC_CLOSE = 0xF060,
			SC_RESTORE = 0xF120;

		private static final int
			MIIM_STATE = 0x00000001,
			MFT_STRING = 0x00000000,
			MF_ENABLED = 0x00000000,
			MF_DISABLED = 0x00000002,
			TPM_RETURNCMD = 0x0100;

		private Window window;
		private final HWND hwnd;
		private final LONG_PTR defaultWndProc;
		private int wmSizeWParam = -1;
		private HBRUSH background;
		private boolean isMovingOrSizing;
		private boolean isMoving;

		// Swing coordinates/values may be scaled on a HiDPI screen
		private int titleBarHeight;
		private Predicate<Point> captionHitTestCallback;
		private Rectangle appIconBounds;
		private Rectangle minimizeButtonBounds;
		private Rectangle maximizeButtonBounds;
		private Rectangle closeButtonBounds;

		WndProc( Window window ) {
			this.window = window;

			// get window handle
			hwnd = new HWND( Native.getComponentPointer( window ) );

			// replace window procedure
			if( SystemInfo.isX86_64 )
				defaultWndProc = User32Ex.INSTANCE.SetWindowLongPtr( hwnd, GWLP_WNDPROC, this );
			else
				defaultWndProc = User32Ex.INSTANCE.SetWindowLong( hwnd, GWLP_WNDPROC, this );

			// remove the OS window title bar
			updateFrame( (window instanceof JFrame) ? ((JFrame)window).getExtendedState() : 0 );

			// set window background (used when resizing window)
			updateWindowBackground();
			window.addPropertyChangeListener( "background", this );
		}

		void uninstall() {
			window.removePropertyChangeListener( "background", this );

			// restore original window procedure
			if( SystemInfo.isX86_64 )
				User32Ex.INSTANCE.SetWindowLongPtr( hwnd, GWLP_WNDPROC, defaultWndProc );
			else
				User32Ex.INSTANCE.SetWindowLong( hwnd, GWLP_WNDPROC, defaultWndProc );

			// show the OS window title bar
			updateFrame( 0 );

			// cleanup
			if( background != null )
				GDI32.INSTANCE.DeleteObject( background );
			window = null;
		}

		private void updateFrame( int state ) {
			// Following SetWindowPos() sends a WM_SIZE(SIZE_RESTORED) message to the window
			// (although SWP_NOSIZE is set), which would prevent maximizing/minimizing
			// when making the frame visible.
			// AWT uses WM_SIZE wParam SIZE_RESTORED to update JFrame.extendedState and
			// removes MAXIMIZED_BOTH and ICONIFIED. (see method AwtFrame::WmSize() in awt_Frame.cpp)
			// To avoid this, change WM_SIZE wParam to SIZE_MAXIMIZED or SIZE_MINIMIZED if necessary.
			if( (state & JFrame.ICONIFIED) != 0 )
				wmSizeWParam = SIZE_MINIMIZED;
			else if( (state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH )
				wmSizeWParam = SIZE_MAXIMIZED;
			else
				wmSizeWParam = -1;

			// this sends WM_NCCALCSIZE and removes/shows the window title bar
			User32.INSTANCE.SetWindowPos( hwnd, hwnd, 0, 0, 0, 0,
				SWP_FRAMECHANGED | SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER | SWP_NOACTIVATE );

			wmSizeWParam = -1;
		}

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			updateWindowBackground();
		}

		private void updateWindowBackground() {
			Color bg = window.getBackground();
			if( bg != null )
				setWindowBackground( bg.getRed(), bg.getGreen(), bg.getBlue() );
		}

		private void setWindowBackground( int r, int g, int b ) {
			// delete old background brush
			if( background != null )
				GDI32.INSTANCE.DeleteObject( background );

			// create new background brush
			background = GDI32Ex.INSTANCE.CreateSolidBrush( RGB( r, g, b ) );
		}

		/**
		 * NOTE: This method is invoked on the AWT-Windows thread (not the AWT-EventQueue thread).
		 */
		@Override
		public LRESULT callback( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam ) {
			long wparam = wParam.longValue();
			switch( uMsg ) {
				case WM_NCCALCSIZE:
					return WmNcCalcSize( hwnd, uMsg, wParam, lParam );

				case WM_NCHITTEST:
					return WmNcHitTest( hwnd, uMsg, wParam, lParam );

				case WM_NCMOUSEMOVE:
					// if mouse is moved over some non-client areas,
					// send it also to the client area to allow Swing to process it
					// (required for Windows 11 maximize button)
					if( wparam == HTMINBUTTON || wparam == HTMAXBUTTON || wparam == HTCLOSE ||
						wparam == HTCAPTION || wparam == HTSYSMENU )
					  sendMessageToClientArea( hwnd, WM_MOUSEMOVE, lParam );
					break;

				case WM_NCLBUTTONDOWN:
				case WM_NCLBUTTONUP:
					// if left mouse was pressed/released over minimize/maximize/close button,
					// send it also to the client area to allow Swing to process it
					// (required for Windows 11 maximize button)
					if( wparam == HTMINBUTTON || wparam == HTMAXBUTTON || wparam == HTCLOSE ) {
						int uClientMsg = (uMsg == WM_NCLBUTTONDOWN) ? WM_LBUTTONDOWN : WM_LBUTTONUP;
						sendMessageToClientArea( hwnd, uClientMsg, lParam );
						return new LRESULT( 0 );
					}
					break;

				case WM_NCRBUTTONUP:
					if( wparam == HTCAPTION || wparam == HTSYSMENU )
						openSystemMenu( hwnd, GET_X_LPARAM( lParam ), GET_Y_LPARAM( lParam ) );
					break;

				case WM_DWMCOLORIZATIONCOLORCHANGED:
					fireStateChangedLaterOnce();
					break;

				case WM_SIZE:
					if( wmSizeWParam >= 0 )
						wParam = new WPARAM( wmSizeWParam );
					break;

				case WM_ENTERSIZEMOVE:
					isMovingOrSizing = true;
					break;

				case WM_EXITSIZEMOVE:
					isMovingOrSizing = isMoving = false;
					break;

				case WM_MOVE:
				case WM_MOVING:
					if( isMovingOrSizing )
						isMoving = true;
					break;

				case WM_DPICHANGED:
					LRESULT lResult = User32Ex.INSTANCE.CallWindowProc( defaultWndProc, hwnd, uMsg, wParam, lParam );

					// if window is maximized and DPI/scaling changed, then Windows
					// does not send a subsequent WM_SIZE message and Java window bounds,
					// which depend on scale factor, are not updated
					boolean isMaximized = User32Ex.INSTANCE.IsZoomed( hwnd );
					if( isMaximized ) {
						MyRECT r = new MyRECT( new Pointer( lParam.longValue() ) );
						int width = r.right - r.left;
						int height = r.bottom - r.top;
						User32Ex.INSTANCE.CallWindowProc( defaultWndProc, hwnd, WM_SIZE, new WPARAM( SIZE_MAXIMIZED ), MAKELPARAM( width, height ) );
					}

					return lResult;

				case WM_ERASEBKGND:
					// do not erase background while the user is moving the window,
					// otherwise there may be rendering artifacts on HiDPI screens with Java 9+
					// when dragging the window partly offscreen and back into the screen bounds
					if( isMoving )
						return new LRESULT( 0 );

					return WmEraseBkgnd( hwnd, uMsg, wParam, lParam );

				case WM_DESTROY:
					return WmDestroy( hwnd, uMsg, wParam, lParam );
			}

			return User32Ex.INSTANCE.CallWindowProc( defaultWndProc, hwnd, uMsg, wParam, lParam );
		}

		/**
		 * Handle WM_DESTROY
		 *
		 * https://docs.microsoft.com/en-us/windows/win32/winmsg/wm-destroy
		 */
		private LRESULT WmDestroy( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam ) {
			// call original AWT window procedure because it may fire window closed event in AwtWindow::WmDestroy()
			LRESULT lResult = User32Ex.INSTANCE.CallWindowProc( defaultWndProc, hwnd, uMsg, wParam, lParam );

			// restore original window procedure
			if( SystemInfo.isX86_64 )
				User32Ex.INSTANCE.SetWindowLongPtr( hwnd, GWLP_WNDPROC, defaultWndProc );
			else
				User32Ex.INSTANCE.SetWindowLong( hwnd, GWLP_WNDPROC, defaultWndProc );

			// cleanup
			windowsMap.remove( window );
			if( background != null )
				GDI32.INSTANCE.DeleteObject( background );
			window = null;

			return lResult;
		}

		/**
		 * Handle WM_ERASEBKGND
		 *
		 * https://docs.microsoft.com/en-us/windows/win32/winmsg/wm-erasebkgnd
		 */
		LRESULT WmEraseBkgnd( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam ) {
			if( background == null )
				return new LRESULT( 0 );

			// fill background
			HDC hdc = new HDC( wParam.toPointer() );
		    RECT rect = new RECT();
		    User32.INSTANCE.GetClientRect( hwnd, rect );
		    User32Ex.INSTANCE.FillRect( hdc, rect, background );
		    return new LRESULT( 1 );
		}

		/**
		 * Handle WM_NCCALCSIZE
		 *
		 * https://docs.microsoft.com/en-us/windows/win32/winmsg/wm-nccalcsize
		 *
		 * See also NonClientIslandWindow::_OnNcCalcSize() here:
		 * https://github.com/microsoft/terminal/blob/main/src/cascadia/WindowsTerminal/NonClientIslandWindow.cpp
		 */
		private LRESULT WmNcCalcSize( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam ) {
			if( wParam.intValue() != 1 )
				return User32Ex.INSTANCE.CallWindowProc( defaultWndProc, hwnd, uMsg, wParam, lParam );

			NCCALCSIZE_PARAMS params = new NCCALCSIZE_PARAMS( new Pointer( lParam.longValue() ) );

			// store the original top before the default window proc applies the default frame
			int originalTop = params.rgrc[0].top;

			// apply the default frame
			LRESULT lResult = User32Ex.INSTANCE.CallWindowProc( defaultWndProc, hwnd, uMsg, wParam, lParam );
			if( lResult.longValue() != 0 )
				return lResult;

			// re-read params from native memory because defaultWndProc changed it
			params.read();

			// re-apply the original top from before the size of the default frame was applied
			params.rgrc[0].top = originalTop;

			boolean isMaximized = User32Ex.INSTANCE.IsZoomed( hwnd );
			if( isMaximized && !isFullscreen() ) {
				// When a window is maximized, its size is actually a little bit larger
				// than the monitor's work area. The window is positioned and sized in
				// such a way that the resize handles are outside of the monitor and
				// then the window is clipped to the monitor so that the resize handle
				// do not appear because you don't need them (because you can't resize
				// a window when it's maximized unless you restore it).
				params.rgrc[0].top += getResizeHandleHeight();

				// check whether taskbar is in the autohide state
				APPBARDATA autohide = new APPBARDATA();
				autohide.cbSize = new DWORD( autohide.size() );
				int state = Shell32.INSTANCE.SHAppBarMessage( new DWORD( ABM_GETSTATE ), autohide ).intValue();
				if( (state & ABS_AUTOHIDE) != 0 ) {
					// get monitor info
					// (using MONITOR_DEFAULTTONEAREST finds right monitor when restoring from minimized)
					HMONITOR hMonitor = User32.INSTANCE.MonitorFromWindow( hwnd, MONITOR_DEFAULTTONEAREST );
					MONITORINFO monitorInfo = new MONITORINFO();
					User32.INSTANCE.GetMonitorInfo( hMonitor, monitorInfo );

					// If there's a taskbar on any side of the monitor, reduce our size
					// a little bit on that edge.
					if( hasAutohideTaskbar( ABE_TOP, monitorInfo.rcMonitor ) )
						params.rgrc[0].top++;
					if( hasAutohideTaskbar( ABE_BOTTOM, monitorInfo.rcMonitor ) )
						params.rgrc[0].bottom--;
					if( hasAutohideTaskbar( ABE_LEFT, monitorInfo.rcMonitor ) )
						params.rgrc[0].left++;
					if( hasAutohideTaskbar( ABE_RIGHT, monitorInfo.rcMonitor ) )
						params.rgrc[0].right--;
				}
			} else if( SystemInfo.isWindows_11_orLater ) {
				// For Windows 11, add border thickness to top, which is necessary to make the whole Java area visible.
				// This also avoids that a black line is sometimes painted on top window border.
				// Note: Do not increase top on Windows 10 because this would not hide Windows title bar.
				IntByReference borderThickness = new IntByReference();
				if( DWMApi.INSTANCE.DwmGetWindowAttribute( hwnd, DWMApi.DWMWA_VISIBLE_FRAME_BORDER_THICKNESS,
						borderThickness.getPointer(), 4 ) == WinError.S_OK.intValue() )
					params.rgrc[0].top += borderThickness.getValue();
			}

			// write changed params back to native memory
			params.write();

			return lResult;
		}

		/**
		 * Handle WM_NCHITTEST
		 *
		 * https://docs.microsoft.com/en-us/windows/win32/inputdev/wm-nchittest
		 *
		 * See also NonClientIslandWindow::_OnNcHitTest() here:
		 * https://github.com/microsoft/terminal/blob/main/src/cascadia/WindowsTerminal/NonClientIslandWindow.cpp
		 */
		private LRESULT WmNcHitTest( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam ) {
			// this will handle the left, right and bottom parts of the frame because we didn't change them
			LRESULT lResult = User32Ex.INSTANCE.CallWindowProc( defaultWndProc, hwnd, uMsg, wParam, lParam );
			if( lResult.longValue() != HTCLIENT )
				return lResult;

			// get mouse x/y in window coordinates
			LRESULT xy = screen2windowCoordinates( hwnd, lParam );
			int x = GET_X_LPARAM( xy );
			int y = GET_Y_LPARAM( xy );

			// scale-down mouse x/y because Swing coordinates/values may be scaled on a HiDPI screen
			Point pt = scaleDown( x, y );

			// return HTSYSMENU if mouse is over application icon
			//   - left-click on HTSYSMENU area shows system menu
			//   - double-left-click sends WM_CLOSE
			if( contains( appIconBounds, pt ) )
				return new LRESULT( HTSYSMENU );

			// return HTMINBUTTON if mouse is over minimize button
			//   - hovering mouse over HTMINBUTTON area shows tooltip on Windows 10/11
			if( contains( minimizeButtonBounds, pt ) )
				return new LRESULT( HTMINBUTTON );

			// return HTMAXBUTTON if mouse is over maximize/restore button
			//   - hovering mouse over HTMAXBUTTON area shows tooltip on Windows 10
			//   - hovering mouse over HTMAXBUTTON area shows snap layouts menu on Windows 11
			//     https://docs.microsoft.com/en-us/windows/apps/desktop/modernize/apply-snap-layout-menu
			if( contains( maximizeButtonBounds, pt ) )
				return new LRESULT( HTMAXBUTTON );

			// return HTCLOSE if mouse is over close button
			//   - hovering mouse over HTCLOSE area shows tooltip on Windows 10/11
			if( contains( closeButtonBounds, pt ) )
				return new LRESULT( HTCLOSE );

			int resizeBorderHeight = getResizeHandleHeight();
			boolean isOnResizeBorder = (y < resizeBorderHeight) &&
				(User32.INSTANCE.GetWindowLong( hwnd, GWL_STYLE ) & WS_THICKFRAME) != 0;

			// return HTTOP if mouse is over top resize border
			//   - hovering mouse shows vertical resize cursor
			//   - left-click and drag vertically resizes window
			if( isOnResizeBorder )
				return new LRESULT( HTTOP );

			boolean isOnTitleBar = (pt.y < titleBarHeight);
			if( isOnTitleBar ) {
				// return HTCLIENT if mouse is over any Swing component in title bar
				// that processes mouse events (e.g. buttons, menus, etc)
				//   - Windows ignores mouse events in this area
				try {
					if( captionHitTestCallback != null && !captionHitTestCallback.test( pt ) )
						return new LRESULT( HTCLIENT );
				} catch( Throwable ex ) {
					// ignore
				}

				// return HTCAPTION if mouse is over title bar
				//   - right-click shows system menu
				//   - double-left-click maximizes/restores window size
				return new LRESULT( HTCAPTION );
			}

			// return HTCLIENT
			//   - Windows ignores mouse events in this area
			return new LRESULT( HTCLIENT );
		}

		private boolean contains( Rectangle rect, Point pt ) {
			return (rect != null && rect.contains( pt ) );
		}

		/**
		 * Converts screen coordinates to window coordinates.
		 */
		private LRESULT screen2windowCoordinates( HWND hwnd, LPARAM lParam ) {
			// get window rectangle needed to convert mouse x/y from screen to window coordinates
			RECT rcWindow = new RECT();
			User32.INSTANCE.GetWindowRect( hwnd, rcWindow );

			// get mouse x/y in window coordinates
			int x = GET_X_LPARAM( lParam ) - rcWindow.left;
			int y = GET_Y_LPARAM( lParam ) - rcWindow.top;

			return new LRESULT( MAKELONG( x, y ) );
		}

		/**
		 * Returns the height of the little space at the top of the window used to
		 * resize the window.
		 *
		 * See also NonClientIslandWindow::_GetResizeHandleHeight() here:
		 * https://github.com/microsoft/terminal/blob/main/src/cascadia/WindowsTerminal/NonClientIslandWindow.cpp
		 */
		private int getResizeHandleHeight() {
			int dpi = User32Ex.INSTANCE.GetDpiForWindow( hwnd );

			// there isn't a SM_CYPADDEDBORDER for the Y axis
			return User32Ex.INSTANCE.GetSystemMetricsForDpi( SM_CXPADDEDBORDER, dpi )
				 + User32Ex.INSTANCE.GetSystemMetricsForDpi( SM_CYSIZEFRAME, dpi );
		}

		/**
		 * Returns whether there is an autohide taskbar on the given edge.
		 */
		private boolean hasAutohideTaskbar( int edge, RECT rcMonitor ) {
			APPBARDATA data = new APPBARDATA();
			data.cbSize = new DWORD( data.size() );
			data.uEdge = new UINT( edge );
			data.rc = rcMonitor;
			UINT_PTR hTaskbar = Shell32.INSTANCE.SHAppBarMessage( new DWORD( ABM_GETAUTOHIDEBAREX ), data );
			return hTaskbar.longValue() != 0;
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

		private boolean isFullscreen() {
			GraphicsConfiguration gc = window.getGraphicsConfiguration();
			if( gc == null )
				return false;
			return gc.getDevice().getFullScreenWindow() == window;
		}

		/**
		 * Same implementation as GET_X_LPARAM(lp) macro in windowsx.h.
		 * X-coordinate is in the low-order short and may be negative.
		 *
		 * https://docs.microsoft.com/en-us/windows/win32/inputdev/wm-nchittest#remarks
		 */
		private int GET_X_LPARAM( LONG_PTR lParam ) {
			return (short) (lParam.longValue() & 0xffff);
		}

		/**
		 * Same implementation as GET_Y_LPARAM(lp) macro in windowsx.h.
		 * Y-coordinate is in the high-order short and may be negative.
		 *
		 * https://docs.microsoft.com/en-us/windows/win32/inputdev/wm-nchittest#remarks
		 */
		private int GET_Y_LPARAM( LONG_PTR lParam ) {
			return (short) ((lParam.longValue() >> 16) & 0xffff);
		}

		/**
		 * Same implementation as MAKELONG(wLow, wHigh) macro in windef.h.
		 */
		private long MAKELONG( int low, int high ) {
			return (low & 0xffff) | ((high & 0xffff) << 16);
		}

		/**
		 * Same implementation as MAKELPARAM(l, h) macro in winuser.h.
		 */
		private LPARAM MAKELPARAM( int low, int high ) {
			return new LPARAM( MAKELONG( low, high ) );
		}

		/**
		 * Same implementation as RGB(r,g,b) macro in wingdi.h.
		 */
		private DWORD RGB( int r, int g, int b ) {
			return new DWORD( (r & 0xff) | ((g & 0xff) << 8) | ((b & 0xff) << 16) );
		}

		private void sendMessageToClientArea( HWND hwnd, int uMsg, LPARAM lParam ) {
			// get mouse x/y in window coordinates
			LRESULT xy = screen2windowCoordinates( hwnd, lParam );

			// send message
			User32.INSTANCE.SendMessage( hwnd, uMsg, new WPARAM(), new LPARAM( xy.longValue() ) );
		}

		/**
		 * Opens the window's system menu.
		 * The system menu is the menu that opens when the user presses Alt+Space or
		 * right clicks on the title bar
		 */
		private void openSystemMenu( HWND hwnd, int x, int y ) {
			// get system menu
			HMENU systemMenu = User32Ex.INSTANCE.GetSystemMenu( hwnd, false );

			// update system menu
			int style = User32.INSTANCE.GetWindowLong( hwnd, GWL_STYLE );
			boolean isMaximized = User32Ex.INSTANCE.IsZoomed( hwnd );
			setMenuItemState( systemMenu, SC_RESTORE, isMaximized );
			setMenuItemState( systemMenu, SC_MOVE, !isMaximized );
			setMenuItemState( systemMenu, SC_SIZE, (style & WS_THICKFRAME) != 0 && !isMaximized );
			setMenuItemState( systemMenu, SC_MINIMIZE, (style & WS_MINIMIZEBOX) != 0 );
			setMenuItemState( systemMenu, SC_MAXIMIZE, (style & WS_MAXIMIZEBOX) != 0 && !isMaximized );
			setMenuItemState( systemMenu, SC_CLOSE, true );

			// make "Close" item the default to be consistent with the system menu shown
			// when pressing Alt+Space
			User32Ex.INSTANCE.SetMenuDefaultItem( systemMenu, SC_CLOSE, 0 );

			// show system menu
			int ret = User32Ex.INSTANCE.TrackPopupMenu( systemMenu, TPM_RETURNCMD,
				x, y, 0, hwnd, null ).intValue();
			if( ret != 0 )
				User32Ex.INSTANCE.PostMessage( hwnd, WM_SYSCOMMAND, new WPARAM( ret ), null );
		}

		private void setMenuItemState( HMENU systemMenu, int item, boolean enabled ) {
			MENUITEMINFO mii = new MENUITEMINFO();
			mii.cbSize = new UINT( mii.size() );
			mii.fMask = new UINT( MIIM_STATE );
			mii.fType = new UINT( MFT_STRING );
			mii.fState = new UINT( enabled ? MF_ENABLED : MF_DISABLED );
			User32Ex.INSTANCE.SetMenuItemInfo( systemMenu, item, false, mii );
		}
	}

	//---- interface User32Ex -------------------------------------------------

	private interface User32Ex
		extends User32
	{
		User32Ex INSTANCE = Native.load( "user32", User32Ex.class, W32APIOptions.DEFAULT_OPTIONS );

		LONG_PTR SetWindowLongPtr( HWND hWnd, int nIndex, WindowProc wndProc );
		LONG_PTR SetWindowLongPtr( HWND hWnd, int nIndex, LONG_PTR wndProc );
		LONG_PTR SetWindowLong( HWND hWnd, int nIndex, WindowProc wndProc );
		LONG_PTR SetWindowLong( HWND hWnd, int nIndex, LONG_PTR wndProc );
		LRESULT CallWindowProc( LONG_PTR lpPrevWndFunc, HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam );

		int FillRect( HDC hDC, RECT lprc, HBRUSH hbr );

		int GetDpiForWindow( HWND hwnd );
		int GetSystemMetricsForDpi( int nIndex, int dpi );

		boolean IsZoomed( HWND hWnd );

		HMENU GetSystemMenu( HWND hWnd, boolean bRevert );
		boolean SetMenuItemInfo( HMENU hmenu, int item, boolean fByPositon, MENUITEMINFO lpmii );
		boolean SetMenuDefaultItem( HMENU hMenu, int uItem, int fByPos );
		BOOL TrackPopupMenu( HMENU hMenu, int uFlags, int x, int y, int nReserved, HWND hWnd, RECT prcRect );
	}

	//---- interface GDI32Ex --------------------------------------------------

	private interface GDI32Ex
		extends GDI32
	{
		GDI32Ex INSTANCE = Native.load( "gdi32", GDI32Ex.class, W32APIOptions.DEFAULT_OPTIONS );

		HBRUSH CreateSolidBrush( DWORD color );
	}

	//---- interface DWMApi ---------------------------------------------------

	private interface DWMApi
		extends StdCallLibrary
	{
		DWMApi INSTANCE = Native.load( "dwmapi", DWMApi.class, W32APIOptions.DEFAULT_OPTIONS );

		int DWMWA_VISIBLE_FRAME_BORDER_THICKNESS = 37;

		int DwmGetWindowAttribute( HWND hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute );
	}

	//---- class NCCALCSIZE_PARAMS --------------------------------------------

	@FieldOrder( { "rgrc" } )
	public static class NCCALCSIZE_PARAMS
		extends Structure
	{
		// real structure contains 3 rectangles, but only first one is needed here
		public RECT[] rgrc = new RECT[1];
//		public WINDOWPOS lppos;

		public NCCALCSIZE_PARAMS( Pointer pointer ) {
			super( pointer );
			read();
		}
	}

	//---- class MyRECT -------------------------------------------------------

	@FieldOrder( { "left", "top", "right", "bottom" } )
	public static class MyRECT
		extends Structure
	{
		public int left;
		public int top;
		public int right;
		public int bottom;

		public MyRECT( Pointer pointer ) {
			super( pointer );
			read();
		}
	}

	//---- class MENUITEMINFO -------------------------------------------------

	@FieldOrder( { "cbSize", "fMask", "fType", "fState", "wID", "hSubMenu",
		"hbmpChecked", "hbmpUnchecked", "dwItemData", "dwTypeData", "cch", "hbmpItem" } )
	public static class MENUITEMINFO
		extends Structure
	{
		public UINT cbSize;
		public UINT fMask;
		public UINT fType;
		public UINT fState;
		public UINT wID;
		public HMENU hSubMenu;
		public HBITMAP hbmpChecked;
		public HBITMAP hbmpUnchecked;
		public ULONG_PTR dwItemData;
		public LPWSTR dwTypeData;
		public UINT cch;
		public HBITMAP hbmpItem;
	}
}
