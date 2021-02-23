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

package com.formdev.flatlaf.nativejna.windows;

import static com.sun.jna.platform.win32.ShellAPI.*;
import static com.sun.jna.platform.win32.WinUser.*;
import java.awt.Dialog;
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
import com.formdev.flatlaf.ui.FlatNativeWindowBorder;
import com.formdev.flatlaf.util.SystemInfo;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
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
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.WindowProc;
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
 */
public class FlatWindowsNativeWindowBorder
	implements FlatNativeWindowBorder.Provider
{
	private final Map<Window, WndProc> windowsMap = Collections.synchronizedMap( new IdentityHashMap<>() );

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
		// requires Windows 10 on x86_64
		if( !SystemInfo.isWindows_10_orLater || !SystemInfo.isX86_64 )
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

	//---- class WndProc ------------------------------------------------------

	private class WndProc
		implements WindowProc
	{
		private static final int GWLP_WNDPROC = -4;

		private static final int
			WM_NCCALCSIZE = 0x0083,
			WM_NCHITTEST = 0x0084,
			WM_NCRBUTTONUP = 0x00A5;

		// WM_NCHITTEST mouse position codes
		private static final int
			HTCLIENT = 1,
			HTCAPTION = 2,
			HTSYSMENU = 3,
			HTTOP = 12;

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
		private final BaseTSD.LONG_PTR defaultWndProc;

		private int titleBarHeight;
		private Rectangle[] hitTestSpots;
		private Rectangle appIconBounds;

		WndProc( Window window ) {
			this.window = window;

			// get window handle
			hwnd = new HWND( Native.getComponentPointer( window ) );

			// replace window procedure
			defaultWndProc = User32Ex.INSTANCE.SetWindowLongPtr( hwnd, GWLP_WNDPROC, this );

			// remove the OS window title bar
			updateFrame();
		}

		void uninstall() {
			// restore original window procedure
			User32Ex.INSTANCE.SetWindowLongPtr( hwnd, GWLP_WNDPROC, defaultWndProc );

			// show the OS window title bar
			updateFrame();

			// cleanup
			window = null;
		}

		private void updateFrame() {
			// this sends WM_NCCALCSIZE and removes/shows the window title bar
			User32.INSTANCE.SetWindowPos( hwnd, hwnd, 0, 0, 0, 0,
				SWP_FRAMECHANGED | SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER );
		}

		/**
		 * NOTE: This method is invoked on the AWT-Windows thread (not the AWT-EventQueue thread).
		 */
		@Override
		public LRESULT callback( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam ) {
			switch( uMsg ) {
				case WM_NCCALCSIZE:
					return WmNcCalcSize( hwnd, uMsg, wParam, lParam );

				case WM_NCHITTEST:
					return WmNcHitTest( hwnd, uMsg, wParam, lParam );

				case WM_NCRBUTTONUP:
					if( wParam.longValue() == HTCAPTION || wParam.longValue() == HTSYSMENU )
						openSystemMenu( hwnd, GET_X_LPARAM( lParam ), GET_Y_LPARAM( lParam ) );
					break;

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
			User32Ex.INSTANCE.SetWindowLongPtr( hwnd, GWLP_WNDPROC, defaultWndProc );

			// cleanup
			windowsMap.remove( window );
			window = null;

			return lResult;
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
				// When a window is maximized, its size is actually a little bit more
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

			// get window rectangle needed to convert mouse x/y from screen to window coordinates
			RECT rcWindow = new RECT();
			User32.INSTANCE.GetWindowRect( hwnd, rcWindow );

			// get mouse x/y in window coordinates
			int x = GET_X_LPARAM( lParam ) - rcWindow.left;
			int y = GET_Y_LPARAM( lParam ) - rcWindow.top;

			// scale-down mouse x/y
			Point pt = scaleDown( x, y );
			int sx = pt.x;
			int sy = pt.y;

			// return HTSYSMENU if mouse is over application icon
			//   - left-click on HTSYSMENU area shows system menu
			//   - double-left-click sends WM_CLOSE
			if( appIconBounds != null && appIconBounds.contains( sx, sy ) )
				return new LRESULT( HTSYSMENU );

			int resizeBorderHeight = getResizeHandleHeight();
			boolean isOnResizeBorder = (y < resizeBorderHeight) &&
				(User32.INSTANCE.GetWindowLong( hwnd, GWL_STYLE ) & WS_THICKFRAME) != 0;
			boolean isOnTitleBar = (sy < titleBarHeight);

			if( isOnTitleBar ) {
				// use a second reference to the array to avoid that it can be changed
				// in another thread while processing the array
				Rectangle[] hitTestSpots2 = hitTestSpots;
				for( Rectangle spot : hitTestSpots2 ) {
					if( spot.contains( sx, sy ) )
						return new LRESULT( HTCLIENT );
				}
				return new LRESULT( isOnResizeBorder ? HTTOP : HTCAPTION );
			}

			return new LRESULT( isOnResizeBorder ? HTTOP : HTCLIENT );
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
		private int GET_X_LPARAM( LPARAM lParam ) {
			return (short) (lParam.longValue() & 0xffff);
		}

		/**
		 * Same implementation as GET_Y_LPARAM(lp) macro in windowsx.h.
		 * Y-coordinate is in the high-order short and may be negative.
		 *
		 * https://docs.microsoft.com/en-us/windows/win32/inputdev/wm-nchittest#remarks
		 */
		private int GET_Y_LPARAM( LPARAM lParam ) {
			return (short) ((lParam.longValue() >> 16) & 0xffff);
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
			boolean isMaximized = User32Ex.INSTANCE.IsZoomed( hwnd );
			setMenuItemState( systemMenu, SC_RESTORE, isMaximized );
			setMenuItemState( systemMenu, SC_MOVE, !isMaximized );
			setMenuItemState( systemMenu, SC_SIZE, !isMaximized );
			setMenuItemState( systemMenu, SC_MINIMIZE, true );
			setMenuItemState( systemMenu, SC_MAXIMIZE, !isMaximized );
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
		LRESULT CallWindowProc( LONG_PTR lpPrevWndFunc, HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam );

		int GetDpiForWindow( HWND hwnd );
		int GetSystemMetricsForDpi( int nIndex, int dpi );

		boolean IsZoomed( HWND hWnd );
		HANDLE GetProp( HWND hWnd, String lpString );

		HMENU GetSystemMenu( HWND hWnd, boolean bRevert );
		boolean SetMenuItemInfo( HMENU hmenu, int item, boolean fByPositon, MENUITEMINFO lpmii );
		boolean SetMenuDefaultItem( HMENU hMenu, int uItem, int fByPos );
		BOOL TrackPopupMenu( HMENU hMenu, int uFlags, int x, int y, int nReserved, HWND hWnd, RECT prcRect );
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
