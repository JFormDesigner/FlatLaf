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

#include <jawt.h>
#include <linux/jawt_md.h>
#include <X11/Xatom.h>
#include "com_formdev_flatlaf_ui_FlatNativeLinuxLibrary.h"

/**
 * @author Karl Tauber
 * @since 2.5
 */


bool sendEvent( JNIEnv *env, jobject window, const char *atom_name,
	long data0, long data1, long data2, long data3, long data4 );
bool isWMHintSupported( Display* display, Window rootWindow, Atom atom );
Window getWindowHandle( JNIEnv* env, JAWT* awt, jobject window, Display** display_return );


//---- JNI methods ------------------------------------------------------------

/**
 * Send _NET_WM_MOVERESIZE to window to initiate moving or resizing.
 *
 * https://specifications.freedesktop.org/wm-spec/wm-spec-latest.html#idm45446104441728
 * https://gitlab.gnome.org/GNOME/gtk/-/blob/main/gdk/x11/gdksurface-x11.c#L3841-3881
 */
extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeLinuxLibrary_xMoveOrResizeWindow
	( JNIEnv *env, jclass cls, jobject window, jint x, jint y, jint direction )
{
	return sendEvent( env, window,
		"_NET_WM_MOVERESIZE",
		x,
		y,
		direction,
		Button1,		// left mouse button
		1 );			// source indication
}

/**
 * Send _GTK_SHOW_WINDOW_MENU to window to show system window menu.
 *
 * https://docs.gtk.org/gdk3/method.Window.show_window_menu.html
 * https://gitlab.gnome.org/GNOME/gtk/-/blob/main/gdk/x11/gdksurface-x11.c#L4751-4801
 */
extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeLinuxLibrary_xShowWindowMenu
	( JNIEnv *env, jclass cls, jobject window, jint x, jint y )
{
	// TODO pass useful value for (input?) devide id ?
	//
	// not used in Mutter and Metacity window manager (but maybe in other WMs?): 
	//     https://github.com/GNOME/mutter/blob/5e5480e620ed5b307902d913f89f5937cc01a28f/src/x11/window-x11.c#L3437
	//     https://github.com/GNOME/metacity/blob/7c1cc3ca1d8131499b9cf2ef50b295602ffd6112/src/core/window.c#L5699
	// not used in KWin:
	//     https://github.com/KDE/kwin/blob/7e1617c2808b7c9b23a8c786327fc88212e10b32/src/netinfo.cpp#L222

	return sendEvent( env, window,
		"_GTK_SHOW_WINDOW_MENU",
		0,				//TODO device id
		x,
		y,
		0,
		0 );
}

bool sendEvent( JNIEnv *env, jobject window, const char *atom_name,
	long data0, long data1, long data2, long data3, long data4 )
{
	// get the AWT
	JAWT awt;
	awt.version = JAWT_VERSION_1_4;
	if( !JAWT_GetAWT( env, &awt ) )
		return false;

	// get Xlib window and display from AWT window
	Display* display;
	Window w = getWindowHandle( env, &awt, window, &display );
	if( w == 0 )
		return false;

	awt.Lock( env );

	Window rootWindow = XDefaultRootWindow( display );

	// check whether window manager supports message
	Atom atom = XInternAtom( display, atom_name, false );
	if( !isWMHintSupported( display, rootWindow, atom ) ) {
		awt.Unlock( env );
		return false;
	}

	// ungrab (mouse) pointer and keyboard to allow the window manager to grab them
	XUngrabPointer( display, CurrentTime );
	XUngrabKeyboard( display, CurrentTime );

	// build event structure
	XClientMessageEvent xclient = { 0 };
	xclient.type = ClientMessage;
	xclient.window = w;
	xclient.message_type = atom;
	xclient.format = 32;
	xclient.data.l[0] = data0;
	xclient.data.l[1] = data1;
	xclient.data.l[2] = data2;
	xclient.data.l[3] = data3;
	xclient.data.l[4] = data4;

	// send event
	XSendEvent( display, rootWindow, False,
		SubstructureRedirectMask | SubstructureNotifyMask,
		(XEvent*) &xclient );

	awt.Unlock( env );
	return true;
}


bool isWMHintSupported( Display* display, Window rootWindow, Atom atom ) {
	Atom type;
	int format;
	unsigned long n_atoms;
	unsigned long bytes_after;
	Atom* atoms;

	// get all supported hints
	XGetWindowProperty( display, rootWindow,
		XInternAtom( display, "_NET_SUPPORTED", false ),
		0, 0xffff, False, XA_ATOM,
		&type, &format, &n_atoms, &bytes_after, (unsigned char**) &atoms );

	if( atoms == NULL )
		return false;

	if( type != XA_ATOM ) {
		XFree( atoms );
		return false;
	}

	bool supported = false;
	for( int i = 0; i < n_atoms; i++ ) {
		if( atoms[i] == atom ) {
			supported = true;
			break;
		}
	}

	XFree( atoms );
	return supported;
}

Window getWindowHandle( JNIEnv* env, JAWT* awt, jobject window, Display** display_return ) {
	jawt_DrawingSurface* ds = awt->GetDrawingSurface( env, window );
	if( ds == NULL )
		return 0;

	jint lock = ds->Lock( ds );
	if( (lock & JAWT_LOCK_ERROR) != 0 ) {
		awt->FreeDrawingSurface( ds );
		return 0;
	}

	JAWT_DrawingSurfaceInfo* dsi = ds->GetDrawingSurfaceInfo( ds );
	JAWT_X11DrawingSurfaceInfo* xdsi = (JAWT_X11DrawingSurfaceInfo*) dsi->platformInfo;

	Window handle = xdsi->drawable;
	*display_return = xdsi->display;

	ds->FreeDrawingSurfaceInfo( dsi );
	ds->Unlock( ds );
	awt->FreeDrawingSurface( ds );

	return handle;
}
