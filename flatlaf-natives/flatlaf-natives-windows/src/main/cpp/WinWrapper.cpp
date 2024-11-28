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

typedef LONG (WINAPI *RtlGetVersion_Type)( OSVERSIONINFO* );

extern "C"
JNIEXPORT jlong JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_getOSBuildNumberImpl
	( JNIEnv* env, jclass cls )
{
	OSVERSIONINFO info;
	info.dwOSVersionInfoSize = sizeof( info );
	info.dwBuildNumber = 0;

	// use RtlGetVersion for the case that app manifest does not specify Windows 10+ compatibility
	// https://www.codeproject.com/Articles/5336372/Windows-Version-Detection
	HMODULE ntdllModule = ::GetModuleHandleA( "ntdll.dll" );
	if( ntdllModule != NULL ) {
		RtlGetVersion_Type pRtlGetVersion = (RtlGetVersion_Type) ::GetProcAddress( ntdllModule, "RtlGetVersion" );
		if( pRtlGetVersion != NULL && pRtlGetVersion( &info ) == 0 )
			return info.dwBuildNumber;
	}

	// fallback
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
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_dwmSetWindowAttributeBOOL
	( JNIEnv* env, jclass cls, jlong hwnd, jint attribute, jboolean value )
{
	if( hwnd == 0 )
		return FALSE;

	BOOL attr = value;
	return ::DwmSetWindowAttribute( reinterpret_cast<HWND>( hwnd ), attribute, &attr, sizeof( attr ) ) == S_OK;
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_dwmSetWindowAttributeDWORD
	( JNIEnv* env, jclass cls, jlong hwnd, jint attribute, jint value )
{
	if( hwnd == 0 )
		return FALSE;

	DWORD attr = value;
	return ::DwmSetWindowAttribute( reinterpret_cast<HWND>( hwnd ), attribute, &attr, sizeof( attr ) ) == S_OK;
}
