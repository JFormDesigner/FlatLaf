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

extern "C"
JNIEXPORT void JNICALL Java_com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder_00024WndProc_updateFrame
	( JNIEnv* env, jobject obj, jlong hwnd, jint state )
{
	FlatWndProc::updateFrame( reinterpret_cast<HWND>( hwnd ), state );
}

extern "C"
JNIEXPORT void JNICALL Java_com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder_00024WndProc_setWindowBackground
	( JNIEnv* env, jobject obj, jlong hwnd, jint r, jint g, jint b )
{
	FlatWndProc::setWindowBackground( reinterpret_cast<HWND>( hwnd ), r, g, b );
}

extern "C"
JNIEXPORT void JNICALL Java_com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder_00024WndProc_showWindow
	( JNIEnv* env, jobject obj, jlong hwnd, jint cmd )
{
	::ShowWindow( reinterpret_cast<HWND>( hwnd ), cmd );
}

//---- class FlatWndProc fields -----------------------------------------------

int FlatWndProc::initialized = 0;
jmethodID FlatWndProc::onNcHitTestMID;
jmethodID FlatWndProc::isFullscreenMID;
jmethodID FlatWndProc::fireStateChangedLaterOnceMID;

HWNDMap* FlatWndProc::hwndMap;

#define java_awt_Frame_ICONIFIED			1
#define java_awt_Frame_MAXIMIZED_BOTH		(4 | 2)

//---- class FlatWndProc methods ----------------------------------------------

FlatWndProc::FlatWndProc() {
	jvm = NULL;
	env = NULL;
	obj = NULL;
	hwnd = NULL;
	defaultWndProc = NULL;
	wmSizeWParam = -1;
	background = NULL;
	isMovingOrSizing = false;
	isMoving = false;
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
	updateFrame( hwnd, 0 );

	// cleanup
	env->DeleteGlobalRef( fwp->obj );
	if( fwp->background != NULL )
		::DeleteObject( fwp->background );
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

void FlatWndProc::updateFrame( HWND hwnd, int state ) {
	// Following SetWindowPos() sends a WM_SIZE(SIZE_RESTORED) message to the window
	// (although SWP_NOSIZE is set), which would prevent maximizing/minimizing
	// when making the frame visible.
	// AWT uses WM_SIZE wParam SIZE_RESTORED to update JFrame.extendedState and
	// removes MAXIMIZED_BOTH and ICONIFIED. (see method AwtFrame::WmSize() in awt_Frame.cpp)
	// To avoid this, change WM_SIZE wParam to SIZE_MAXIMIZED or SIZE_MINIMIZED if necessary.
	FlatWndProc* fwp = (FlatWndProc*) hwndMap->get( hwnd );
	if( fwp != NULL ) {
		if( (state & java_awt_Frame_ICONIFIED) != 0 )
			fwp->wmSizeWParam = SIZE_MINIMIZED;
		else if( (state & java_awt_Frame_MAXIMIZED_BOTH) == java_awt_Frame_MAXIMIZED_BOTH )
			fwp->wmSizeWParam = SIZE_MAXIMIZED;
		else
			fwp->wmSizeWParam = -1;
	}

	// this sends WM_NCCALCSIZE and removes/shows the window title bar
	::SetWindowPos( hwnd, hwnd, 0, 0, 0, 0,
		SWP_FRAMECHANGED | SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER | SWP_NOACTIVATE );

	if( fwp != NULL )
		fwp->wmSizeWParam = -1;
}

void FlatWndProc::setWindowBackground( HWND hwnd, int r, int g, int b ) {
	FlatWndProc* fwp = (FlatWndProc*) hwndMap->get( hwnd );
	if( fwp == NULL )
		return;

	// delete old background brush
	if( fwp->background != NULL )
		::DeleteObject( fwp->background );

	// create new background brush
	fwp->background = ::CreateSolidBrush( RGB( r, g, b ) );
}

LRESULT CALLBACK FlatWndProc::StaticWindowProc( HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam ) {
	FlatWndProc* fwp = (FlatWndProc*) hwndMap->get( hwnd );
	if( fwp == NULL )
		return 0;
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

		case WM_NCMOUSEMOVE:
			// if mouse is moved over some non-client areas,
			// send it also to the client area to allow Swing to process it
			// (required for Windows 11 maximize button)
			if( wParam == HTMINBUTTON || wParam == HTMAXBUTTON || wParam == HTCLOSE ||
				wParam == HTCAPTION || wParam == HTSYSMENU )
			  sendMessageToClientArea( hwnd, WM_MOUSEMOVE, lParam );
			break;

		case WM_NCLBUTTONDOWN:
		case WM_NCLBUTTONUP:
			// if left mouse was pressed/released over minimize/maximize/close button,
			// send it also to the client area to allow Swing to process it
			// (required for Windows 11 maximize button)
			if( wParam == HTMINBUTTON || wParam == HTMAXBUTTON || wParam == HTCLOSE ) {
				int uClientMsg = (uMsg == WM_NCLBUTTONDOWN) ? WM_LBUTTONDOWN : WM_LBUTTONUP;
				sendMessageToClientArea( hwnd, uClientMsg, lParam );
				return 0;
			}
			break;

		case WM_NCRBUTTONUP:
			if( wParam == HTCAPTION || wParam == HTSYSMENU )
				openSystemMenu( hwnd, GET_X_LPARAM( lParam ), GET_Y_LPARAM( lParam ) );
			break;

		case WM_DWMCOLORIZATIONCOLORCHANGED:
			fireStateChangedLaterOnce();
			break;

		case WM_SIZE:
			if( wmSizeWParam >= 0 )
				wParam = wmSizeWParam;
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

		case WM_ERASEBKGND:
			// do not erase background while the user is moving the window,
			// otherwise there may be rendering artifacts on HiDPI screens with Java 9+
			// when dragging the window partly offscreen and back into the screen bounds
			if( isMoving )
				return FALSE;

			return WmEraseBkgnd( hwnd, uMsg, wParam, lParam );

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
	if( background != NULL )
		::DeleteObject( background );
	hwndMap->remove( hwnd );
	delete this;

	// call original AWT window procedure because it may fire window closed event in AwtWindow::WmDestroy()
	return ::CallWindowProc( defaultWndProc2, hwnd, uMsg, wParam, lParam );
}

/**
 * Handle WM_ERASEBKGND
 *
 * https://docs.microsoft.com/en-us/windows/win32/winmsg/wm-erasebkgnd
 */
LRESULT FlatWndProc::WmEraseBkgnd( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam ) {
	if( background == NULL )
		return FALSE;

	// fill background
	HDC hdc = (HDC) wParam;
	RECT rect;
	::GetClientRect( hwnd, &rect );
	::FillRect( hdc, &rect, background );
	return TRUE;
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
		// When a window is maximized, its size is actually a little bit larger
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

	// get mouse x/y in window coordinates
	LRESULT xy = screen2windowCoordinates( hwnd, lParam );
	int x = GET_X_LPARAM( xy );
	int y = GET_Y_LPARAM( xy );

	int resizeBorderHeight = getResizeHandleHeight();
	bool isOnResizeBorder = (y < resizeBorderHeight) &&
		(::GetWindowLong( hwnd, GWL_STYLE ) & WS_THICKFRAME) != 0;

	return onNcHitTest( x, y, isOnResizeBorder );
}

/**
 * Converts screen coordinates to window coordinates.
 */
LRESULT FlatWndProc::screen2windowCoordinates( HWND hwnd, LPARAM lParam ) {
	// get window rectangle needed to convert mouse x/y from screen to window coordinates
	RECT rcWindow;
	::GetWindowRect( hwnd, &rcWindow );

	// get mouse x/y in window coordinates
	int x = GET_X_LPARAM( lParam ) - rcWindow.left;
	int y = GET_Y_LPARAM( lParam ) - rcWindow.top;

	return MAKELONG( x, y );
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

void FlatWndProc::sendMessageToClientArea( HWND hwnd, int uMsg, LPARAM lParam ) {
	// get mouse x/y in window coordinates
	LRESULT xy = screen2windowCoordinates( hwnd, lParam );

	// send message
	::SendMessage( hwnd, uMsg, 0, xy );
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
