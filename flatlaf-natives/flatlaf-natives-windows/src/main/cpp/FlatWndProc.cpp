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

// avoid inlining of printf()
#define _NO_CRT_STDIO_INLINE

#include <windows.h>
#include <windowsx.h>
#include <shellapi.h>
#include <jawt.h>
#include <jawt_md.h>
#include "FlatWndProc.h"
#include "com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder_WndProc.h"

/**
 * @author Karl Tauber
 */

//---- JNI methods ------------------------------------------------------------

extern "C"
JNIEXPORT jlong JNICALL Java_com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder_00024WndProc_installImpl
	( JNIEnv *env, jobject obj, jobject window )
{
	return reinterpret_cast<jlong>( FlatWndProc::install( env, obj, window ) );
}

extern "C"
JNIEXPORT void JNICALL Java_com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder_00024WndProc_uninstallImpl
	( JNIEnv* env, jobject obj, jlong hwnd )
{
	FlatWndProc::uninstall( env, obj, reinterpret_cast<HWND>( hwnd ) );
}

//---- class FlatWndProc fields -----------------------------------------------

int FlatWndProc::initialized = 0;
jmethodID FlatWndProc::onNcHitTestMID;
jmethodID FlatWndProc::isFullscreenMID;
jmethodID FlatWndProc::fireStateChangedLaterOnceMID;

HWNDMap* FlatWndProc::hwndMap;

//---- class FlatWndProc methods ----------------------------------------------

FlatWndProc::FlatWndProc() {
	jvm = NULL;
	env = NULL;
	obj = NULL;
	hwnd = NULL;
	defaultWndProc = NULL;
}

HWND FlatWndProc::install( JNIEnv *env, jobject obj, jobject window ) {
	initIDs( env, obj );

	if( initialized < 0 )
		return 0;

	// create HWND map
	if( hwndMap == NULL )
		hwndMap = new HWNDMap();

	// get window handle
	HWND hwnd = getWindowHandle( env, window );
	if( hwnd == NULL || hwndMap->get( hwnd ) != NULL )
		return 0;

	FlatWndProc* fwp = new FlatWndProc();
	env->GetJavaVM( &fwp->jvm );
	fwp->obj = env->NewGlobalRef( obj );
	fwp->hwnd = hwnd;
	hwndMap->put( hwnd, fwp );

	// replace window procedure
	fwp->defaultWndProc = reinterpret_cast<WNDPROC>(
		::SetWindowLongPtr( hwnd, GWLP_WNDPROC, (LONG_PTR) FlatWndProc::StaticWindowProc ) );

	// remove the OS window title bar
	fwp->updateFrame();

	return hwnd;
}

void FlatWndProc::uninstall( JNIEnv *env, jobject obj, HWND hwnd ) {
	if( hwnd == NULL )
		return;

	FlatWndProc* fwp = (FlatWndProc*) hwndMap->get( hwnd );
	if( fwp == NULL )
		return;

	hwndMap->remove( hwnd );

	// restore original window procedure
	::SetWindowLongPtr( hwnd, GWLP_WNDPROC, (LONG_PTR) fwp->defaultWndProc );

	// show the OS window title bar
	fwp->updateFrame();

	// cleanup
	env->DeleteGlobalRef( fwp->obj );
	delete fwp;
}

void FlatWndProc::initIDs( JNIEnv *env, jobject obj ) {
	if( initialized )
		return;

	initialized = -1;

	jclass cls = env->GetObjectClass( obj );
	onNcHitTestMID = env->GetMethodID( cls, "onNcHitTest", "(IIZ)I" );
	isFullscreenMID = env->GetMethodID( cls, "isFullscreen", "()Z" );
	fireStateChangedLaterOnceMID = env->GetMethodID( cls, "fireStateChangedLaterOnce", "()V" );

	// check whether all IDs were found
	if( onNcHitTestMID != NULL &&
		isFullscreenMID != NULL &&
		fireStateChangedLaterOnceMID != NULL )
	  initialized = 1;
}

void FlatWndProc::updateFrame() {
	// this sends WM_NCCALCSIZE and removes/shows the window title bar
	::SetWindowPos( hwnd, hwnd, 0, 0, 0, 0,
		SWP_FRAMECHANGED | SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER );
}

LRESULT CALLBACK FlatWndProc::StaticWindowProc( HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam ) {
	FlatWndProc* fwp = (FlatWndProc*) hwndMap->get( hwnd );
	return fwp->WindowProc( hwnd, uMsg, wParam, lParam );
}

/**
 * NOTE: This method is invoked on the AWT-Windows thread (not the AWT-EventQueue thread).
 */
LRESULT CALLBACK FlatWndProc::WindowProc( HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam ) {
	switch( uMsg ) {
		case WM_NCCALCSIZE:
			return WmNcCalcSize( hwnd, uMsg, wParam, lParam );

		case WM_NCHITTEST:
			return WmNcHitTest( hwnd, uMsg, wParam, lParam );

		case WM_NCRBUTTONUP:
			if( wParam == HTCAPTION || wParam == HTSYSMENU )
				openSystemMenu( hwnd, GET_X_LPARAM( lParam ), GET_Y_LPARAM( lParam ) );
			break;

		case WM_DWMCOLORIZATIONCOLORCHANGED:
			fireStateChangedLaterOnce();
			break;

		case WM_DESTROY:
			return WmDestroy( hwnd, uMsg, wParam, lParam );
	}

	return ::CallWindowProc( defaultWndProc, hwnd, uMsg, wParam, lParam );
}
/**
 * Handle WM_DESTROY
 *
 * https://docs.microsoft.com/en-us/windows/win32/winmsg/wm-destroy
 */
LRESULT FlatWndProc::WmDestroy( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam ) {
	// restore original window procedure
	::SetWindowLongPtr( hwnd, GWLP_WNDPROC, (LONG_PTR) defaultWndProc );

	WNDPROC defaultWndProc2 = defaultWndProc;

	// cleanup
	getEnv()->DeleteGlobalRef( obj );
	hwndMap->remove( hwnd );
	delete this;

	// call original AWT window procedure because it may fire window closed event in AwtWindow::WmDestroy()
	return ::CallWindowProc( defaultWndProc2, hwnd, uMsg, wParam, lParam );
}

/**
 * Handle WM_NCCALCSIZE
 *
 * https://docs.microsoft.com/en-us/windows/win32/winmsg/wm-nccalcsize
 *
 * See also NonClientIslandWindow::_OnNcCalcSize() here:
 * https://github.com/microsoft/terminal/blob/main/src/cascadia/WindowsTerminal/NonClientIslandWindow.cpp
 */
LRESULT FlatWndProc::WmNcCalcSize( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam ) {
	if( wParam != TRUE )
		return ::CallWindowProc( defaultWndProc, hwnd, uMsg, wParam, lParam );

	NCCALCSIZE_PARAMS* params = reinterpret_cast<NCCALCSIZE_PARAMS*>( lParam );

	// store the original top before the default window proc applies the default frame
	int originalTop = params->rgrc[0].top;

	// apply the default frame
	LRESULT lResult = ::CallWindowProc( defaultWndProc, hwnd, uMsg, wParam, lParam );
	if( lResult != 0 )
		return lResult;

	// re-apply the original top from before the size of the default frame was applied
	params->rgrc[0].top = originalTop;

	bool isMaximized = ::IsZoomed( hwnd );
	if( isMaximized && !isFullscreen() ) {
		// When a window is maximized, its size is actually a little bit more
		// than the monitor's work area. The window is positioned and sized in
		// such a way that the resize handles are outside of the monitor and
		// then the window is clipped to the monitor so that the resize handle
		// do not appear because you don't need them (because you can't resize
		// a window when it's maximized unless you restore it).
		params->rgrc[0].top += getResizeHandleHeight();

		// check whether taskbar is in the autohide state
		APPBARDATA autohide{ 0 };
		autohide.cbSize = sizeof( autohide );
		UINT state = (UINT) ::SHAppBarMessage( ABM_GETSTATE, &autohide );
		if( (state & ABS_AUTOHIDE) != 0 ) {
			// get monitor info
			// (using MONITOR_DEFAULTTONEAREST finds right monitor when restoring from minimized)
			HMONITOR hMonitor = ::MonitorFromWindow( hwnd, MONITOR_DEFAULTTONEAREST );
			MONITORINFO monitorInfo{ 0 };
			::GetMonitorInfo( hMonitor, &monitorInfo );

			// If there's a taskbar on any side of the monitor, reduce our size
			// a little bit on that edge.
			if( hasAutohideTaskbar( ABE_TOP, monitorInfo.rcMonitor ) )
				params->rgrc[0].top++;
			if( hasAutohideTaskbar( ABE_BOTTOM, monitorInfo.rcMonitor ) )
				params->rgrc[0].bottom--;
			if( hasAutohideTaskbar( ABE_LEFT, monitorInfo.rcMonitor ) )
				params->rgrc[0].left++;
			if( hasAutohideTaskbar( ABE_RIGHT, monitorInfo.rcMonitor ) )
				params->rgrc[0].right--;
		}
	}

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
LRESULT FlatWndProc::WmNcHitTest( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam ) {
	// this will handle the left, right and bottom parts of the frame because we didn't change them
	LRESULT lResult = ::CallWindowProc( defaultWndProc, hwnd, uMsg, wParam, lParam );
	if( lResult != HTCLIENT )
		return lResult;

	// get window rectangle needed to convert mouse x/y from screen to window coordinates
	RECT rcWindow;
	::GetWindowRect( hwnd, &rcWindow );

	// get mouse x/y in window coordinates
	int x = GET_X_LPARAM( lParam ) - rcWindow.left;
	int y = GET_Y_LPARAM( lParam ) - rcWindow.top;

	int resizeBorderHeight = getResizeHandleHeight();
	bool isOnResizeBorder = (y < resizeBorderHeight) &&
		(::GetWindowLong( hwnd, GWL_STYLE ) & WS_THICKFRAME) != 0;

	return onNcHitTest( x, y, isOnResizeBorder );
}

/**
 * Returns the height of the little space at the top of the window used to
 * resize the window.
 *
 * See also NonClientIslandWindow::_GetResizeHandleHeight() here:
 * https://github.com/microsoft/terminal/blob/main/src/cascadia/WindowsTerminal/NonClientIslandWindow.cpp
 */
int FlatWndProc::getResizeHandleHeight() {
	int dpi = ::GetDpiForWindow( hwnd );

	// there isn't a SM_CYPADDEDBORDER for the Y axis
	return ::GetSystemMetricsForDpi( SM_CXPADDEDBORDER, dpi )
		 + ::GetSystemMetricsForDpi( SM_CYSIZEFRAME, dpi );
}

/**
 * Returns whether there is an autohide taskbar on the given edge.
 */
bool FlatWndProc::hasAutohideTaskbar( UINT edge, RECT rcMonitor ) {
	APPBARDATA data{ 0 };
	data.cbSize = sizeof( data );
	data.uEdge = edge;
	data.rc = rcMonitor;
	HWND hTaskbar = (HWND) ::SHAppBarMessage( ABM_GETAUTOHIDEBAREX, &data );
	return hTaskbar != nullptr;
}

BOOL FlatWndProc::isFullscreen() {
	JNIEnv* env = getEnv();
	if( env == NULL )
		return FALSE;

	return env->CallBooleanMethod( obj, isFullscreenMID );
}

int FlatWndProc::onNcHitTest( int x, int y, boolean isOnResizeBorder ) {
	JNIEnv* env = getEnv();
	if( env == NULL )
		return isOnResizeBorder ? HTTOP : HTCLIENT;

	return env->CallIntMethod( obj, onNcHitTestMID, (jint) x, (jint) y, (jboolean) isOnResizeBorder );
}

void FlatWndProc::fireStateChangedLaterOnce() {
	JNIEnv* env = getEnv();
	if( env == NULL )
		return;

	env->CallVoidMethod( obj, fireStateChangedLaterOnceMID );
}

// similar to JNU_GetEnv() in jni_util.c
JNIEnv* FlatWndProc::getEnv() {
	if( env != NULL )
		return env;

	jvm->GetEnv( (void **) &env, JNI_VERSION_1_2 );
	return env;
}

/**
 * Opens the window's system menu.
 * The system menu is the menu that opens when the user presses Alt+Space or
 * right clicks on the title bar
 */
void FlatWndProc::openSystemMenu( HWND hwnd, int x, int y ) {
	// get system menu
	HMENU systemMenu = ::GetSystemMenu( hwnd, false );

	// update system menu
	LONG style = ::GetWindowLong( hwnd, GWL_STYLE );
	bool isMaximized = ::IsZoomed( hwnd );
	setMenuItemState( systemMenu, SC_RESTORE, isMaximized );
	setMenuItemState( systemMenu, SC_MOVE, !isMaximized );
	setMenuItemState( systemMenu, SC_SIZE, (style & WS_THICKFRAME) != 0 && !isMaximized );
	setMenuItemState( systemMenu, SC_MINIMIZE, (style & WS_MINIMIZEBOX) != 0 );
	setMenuItemState( systemMenu, SC_MAXIMIZE, (style & WS_MAXIMIZEBOX) != 0 && !isMaximized );
	setMenuItemState( systemMenu, SC_CLOSE, true );

	// make "Close" item the default to be consistent with the system menu shown
	// when pressing Alt+Space
	::SetMenuDefaultItem( systemMenu, SC_CLOSE, 0 );

	// show system menu
	int ret = ::TrackPopupMenu( systemMenu, TPM_RETURNCMD, x, y, 0, hwnd, nullptr );
	if( ret != 0 )
		::PostMessage( hwnd, WM_SYSCOMMAND, ret, 0 );
}

void FlatWndProc::setMenuItemState( HMENU systemMenu, int item, bool enabled ) {
	MENUITEMINFO mii{ 0 };
	mii.cbSize = sizeof( mii );
	mii.fMask = MIIM_STATE;
	mii.fType = MFT_STRING;
	mii.fState = enabled ? MF_ENABLED : MF_DISABLED;
	::SetMenuItemInfo( systemMenu, item, FALSE, &mii );
}

HWND FlatWndProc::getWindowHandle( JNIEnv* env, jobject window ) {
	JAWT awt;
	awt.version = JAWT_VERSION_1_4;
	if( !JAWT_GetAWT( env, &awt ) )
		return 0;

	jawt_DrawingSurface* ds = awt.GetDrawingSurface( env, window );
	if( ds == NULL )
		return 0;

	jint lock = ds->Lock( ds );
	if( (lock & JAWT_LOCK_ERROR) != 0 ) {
		awt.FreeDrawingSurface( ds );
		return 0;
	}

	JAWT_DrawingSurfaceInfo* dsi = ds->GetDrawingSurfaceInfo( ds );
	JAWT_Win32DrawingSurfaceInfo* wdsi = (JAWT_Win32DrawingSurfaceInfo*) dsi->platformInfo;

	HWND hwnd = wdsi->hwnd;

	ds->FreeDrawingSurfaceInfo( dsi );
	ds->Unlock( ds );
	awt.FreeDrawingSurface( ds );

	return hwnd;
}
