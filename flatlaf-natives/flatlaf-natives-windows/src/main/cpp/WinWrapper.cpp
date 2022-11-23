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

// avoid inlining of printf()
#define _NO_CRT_STDIO_INLINE

#include <windows.h>
#include <dwmapi.h>
#include "com_formdev_flatlaf_ui_FlatNativeWindowsLibrary.h"

/**
 * @author Karl Tauber
 */

// see FlatWndProc.cpp
HWND getWindowHandle( JNIEnv* env, jobject window );

//---- Utility ----------------------------------------------------------------

extern "C"
JNIEXPORT jlong JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_getOSBuildNumberImpl
	( JNIEnv* env, jclass cls )
{
	OSVERSIONINFO info;
	info.dwOSVersionInfoSize = sizeof( info );
	if( !::GetVersionEx( &info ) )
		return 0;
	return info.dwBuildNumber;
}

extern "C"
JNIEXPORT jlong JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_getHWND
	( JNIEnv* env, jclass cls, jobject window )
{
	return reinterpret_cast<jlong>( getWindowHandle( env, window ) );
}

//---- Desktop Window Manager (DWM) -------------------------------------------

// define constants that may not available in older development environments

#ifndef DWMWA_COLOR_DEFAULT

#define DWMWA_WINDOW_CORNER_PREFERENCE		33
#define DWMWA_BORDER_COLOR					34

typedef enum  {
  DWMWCP_DEFAULT = 0,
  DWMWCP_DONOTROUND = 1,
  DWMWCP_ROUND = 2,
  DWMWCP_ROUNDSMALL = 3
} DWM_WINDOW_CORNER_PREFERENCE;

// Use this constant to reset any window part colors to the system default behavior
#define DWMWA_COLOR_DEFAULT 0xFFFFFFFF

// Use this constant to specify that a window part should not be rendered
#define DWMWA_COLOR_NONE    0xFFFFFFFE

#endif


extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_setWindowCornerPreference
	( JNIEnv* env, jclass cls, jlong hwnd, jint cornerPreference )
{
	if( hwnd == 0 )
		return FALSE;

	DWM_WINDOW_CORNER_PREFERENCE attr = (DWM_WINDOW_CORNER_PREFERENCE) cornerPreference;
	return ::DwmSetWindowAttribute( reinterpret_cast<HWND>( hwnd ), DWMWA_WINDOW_CORNER_PREFERENCE, &attr, sizeof( attr ) ) == S_OK;
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_setWindowBorderColor
	( JNIEnv* env, jclass cls, jlong hwnd, jint red, jint green, jint blue )
{
	if( hwnd == 0 )
		return FALSE;

	COLORREF attr;
	if( red == -1 ) 
		attr = DWMWA_COLOR_DEFAULT;
	else if( red == -2 )
		attr = DWMWA_COLOR_NONE;
	else
		attr = RGB( red, green, blue );
	return ::DwmSetWindowAttribute( reinterpret_cast<HWND>( hwnd ), DWMWA_BORDER_COLOR, &attr, sizeof( attr ) ) == S_OK;
}
