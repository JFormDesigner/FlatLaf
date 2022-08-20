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

package com.formdev.flatlaf.natives.jna.linux;

import java.awt.Window;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;

/**
 * @author Karl Tauber
 * @since 2.5
 */
public class X11WmUtils
{
	/**
	 * Send _NET_WM_MOVERESIZE to window to initiate moving or resizing.
	 *
	 * Warning: Although the implementation of this method is (nearly) identical
	 *          to the C++ implementation, this one does not work correctly.
	 *          DO NOT USE.
	 *
	 * https://specifications.freedesktop.org/wm-spec/wm-spec-latest.html#idm45446104441728
	 * https://gitlab.gnome.org/GNOME/gtk/-/blob/main/gdk/x11/gdksurface-x11.c#L3841-3881
	 */
	public static boolean xMoveOrResizeWindow( Window window, int x, int y, int direction ) {
		System.out.println( "---- move or resize window: " + x + "," + y );
		return sendEvent( window,
			"_NET_WM_MOVERESIZE",
			x,
			y,
			direction,
			X11.Button1,		// left mouse button
			1 );				// source indication
	}

	/**
	 * Send _GTK_SHOW_WINDOW_MENU to window to show system window menu.
	 *
	 * Warning: Although the implementation of this method is (nearly) identical
	 *          to the C++ implementation, this one does not work correctly.
	 *          DO NOT USE.
	 *
	 * https://docs.gtk.org/gdk3/method.Window.show_window_menu.html
	 * https://gitlab.gnome.org/GNOME/gtk/-/blob/main/gdk/x11/gdksurface-x11.c#L4751-4801
	 */
	public static boolean xShowWindowMenu( Window window, int x, int y ) {
		System.out.println( "---- show window menu: " + x + "," + y );
		return sendEvent( window,
			"_GTK_SHOW_WINDOW_MENU",
			0,				// device id TODO
			x,
			y,
			0,
			0 );
	}

	private static boolean sendEvent( Window window, String atom_name,
		long data0, long data1, long data2, long data3, long data4 )
	{
		sun.awt.SunToolkit.awtLock();
		try {

			// open display and get root window
			X11.Display display = X11.INSTANCE.XOpenDisplay( null );
			X11.Window root = X11.INSTANCE.XDefaultRootWindow( display );

			// get X11 window ID for AWT window
			Pointer p = Native.getComponentPointer( window );
			long windowsId = Pointer.nativeValue( p );
			System.out.println( "WindowId = " + windowsId );

			// ungrab pointer and keyboard to allow the window manager to grab them
			System.out.println( "Ungrab Pointer =  " + X11Ext.INSTANCE.XUngrabPointer( display, new NativeLong( 0 ) ) );
			System.out.println( "Ungrab Keyboard = " + X11.INSTANCE.XUngrabKeyboard( display, new NativeLong( 0 ) ) );

			// build event structure
			X11.Window w = new X11.Window( windowsId );
			X11.XEvent event = new X11.XEvent();
			event.type = X11.ClientMessage;
			event.setType( X11.XClientMessageEvent.class );
			event.xclient.type = X11.ClientMessage;
			event.xclient.serial = new NativeLong( 0 );
			event.xclient.send_event = 1;
			event.xclient.message_type = X11.INSTANCE.XInternAtom( display, atom_name, false );
			event.xclient.display = display;
			event.xclient.window = w;
			event.xclient.format = 32;
			event.xclient.data.setType( NativeLong[].class );
			event.xclient.data.l[0] = new NativeLong( data0 );
			event.xclient.data.l[1] = new NativeLong( data1 );
			event.xclient.data.l[2] = new NativeLong( data2 );
			event.xclient.data.l[3] = new NativeLong( data3 );
			event.xclient.data.l[4] = new NativeLong( data4 );

			// send event
			System.out.println( "SendEvent = " + X11.INSTANCE.XSendEvent( display, root, 0,
				new NativeLong( X11.SubstructureNotifyMask | X11.SubstructureRedirectMask ), event ) );

			System.out.println( "Flush = " + X11.INSTANCE.XFlush( display ) );
			System.out.println( "CloseDisplay = " + X11.INSTANCE.XCloseDisplay( display ) );
			System.out.println( "Done" );

		} finally {
			sun.awt.SunToolkit.awtUnlock();
		}

		return true;
	}

	//----- interface X11Ext --------------------------------------------------

	interface X11Ext
		extends X11
	{
		X11Ext INSTANCE = Native.load( "X11", X11Ext.class );

		int XUngrabPointer( Display display, NativeLong time );
	}
}
