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

#include <windows.h>
#include "HWNDMap.h"

/**
 * @author Karl Tauber
 */
class FlatWndProc
{
public:
	static HWND install( JNIEnv *env, jobject obj, jobject window );
	static void uninstall( JNIEnv *env, jobject obj, HWND hwnd );
	static void updateFrame( HWND hwnd, int state );
	static void setWindowBackground( HWND hwnd, int r, int g, int b );

private:
	static int initialized;
	static jmethodID onNcHitTestMID;
	static jmethodID isFullscreenMID;
	static jmethodID fireStateChangedLaterOnceMID;

	static HWNDMap* hwndMap;
	static DWORD osBuildNumber;

	JavaVM* jvm;
	JNIEnv* env; // attached to AWT-Windows/Win32 thread
	jobject obj;
	HWND hwnd;
	WNDPROC defaultWndProc;
	int wmSizeWParam;
	HBRUSH background;
	bool isMovingOrSizing;
	bool isMoving;

	FlatWndProc();
	static void initIDs( JNIEnv *env, jobject obj );

	static LRESULT CALLBACK StaticWindowProc( HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam );
	LRESULT CALLBACK WindowProc( HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam );
	LRESULT WmDestroy( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam );
	LRESULT WmEraseBkgnd( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam );
	LRESULT WmNcCalcSize( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam );
	LRESULT WmNcHitTest( HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam );

	LRESULT screen2windowCoordinates( HWND hwnd, LPARAM lParam );
	int getResizeHandleHeight();
	bool hasAutohideTaskbar( UINT edge, RECT rcMonitor );
	BOOL isFullscreen();
	int onNcHitTest( int x, int y, boolean isOnResizeBorder );
	void fireStateChangedLaterOnce();
	JNIEnv* getEnv();

	void sendMessageToClientArea( HWND hwnd, int uMsg, LPARAM lParam );
	void openSystemMenu( HWND hwnd, int x, int y );
	void setMenuItemState( HMENU systemMenu, int item, bool enabled );
};
