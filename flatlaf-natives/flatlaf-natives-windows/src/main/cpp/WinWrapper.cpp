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
#include <dxgi.h>
#include "com_formdev_flatlaf_ui_FlatNativeWindowsLibrary.h"

/**
 * @author Karl Tauber
 */

// declare external methods
extern HWND getWindowHandle( JNIEnv* env, jobject window );

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

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_isScreenReaderActive
	( JNIEnv* env, jclass cls )
{
	BOOL screenReader = FALSE;
	if( !::SystemParametersInfo( SPI_GETSCREENREADER, 0, &screenReader, 0 ) )
		return FALSE;
	return screenReader ? TRUE : FALSE;
}

//---- Desktop Window Manager (DWM) -------------------------------------------

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_dwmSetWindowCornerPreference
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

//---- DWM rounded corners ----------------------------------------------------

typedef HRESULT (WINAPI *CreateDXGIFactory1_Type)( REFIID, void** );

/**
 * Returns whether the WARP adapter ("Microsoft Basic Render Driver") has outputs,
 * which means that the desktop is rendered in software (e.g. in VMs without 3D acceleration).
 */
static bool isWarpAdapterUsedForDisplay() {
	// load dxgi.dll dynamically to avoid that it is loaded when loading FlatLaf native library
	// (do not free library because it is usually already loaded by Java2D or other code)
	HMODULE dxgiModule = ::LoadLibraryW( L"dxgi.dll" );
	if( dxgiModule == NULL )
		return false;

	CreateDXGIFactory1_Type pCreateDXGIFactory1 = (CreateDXGIFactory1_Type) ::GetProcAddress( dxgiModule, "CreateDXGIFactory1" );
	if( pCreateDXGIFactory1 == NULL )
		return false;

	IDXGIFactory1* factory = NULL;
	if( FAILED( pCreateDXGIFactory1( __uuidof( IDXGIFactory1 ), reinterpret_cast<void**>( &factory ) ) ) )
		return false;

	bool result = false;
	IDXGIAdapter* adapter = NULL;
	for( UINT i = 0; !result && SUCCEEDED( factory->EnumAdapters( i, &adapter ) ); i++ ) {
		DXGI_ADAPTER_DESC desc;
		if( SUCCEEDED( adapter->GetDesc( &desc ) ) &&
			desc.VendorId == 0x1414 && desc.DeviceId == 0x8C )	// Microsoft Basic Render Driver
		{
			// check whether WARP adapter has outputs (is used to render a monitor)
			IDXGIOutput* output = NULL;
			if( SUCCEEDED( adapter->EnumOutputs( 0, &output ) ) ) {
				output->Release();
				result = true;
			}
		}
		adapter->Release();
	}

	factory->Release();
	return result;
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_isDwmRoundedCornersSupported
	( JNIEnv* env, jclass cls )
{
	// DWM does not round corners (and uses no or a different shadow) for all windows
	// if at least one monitor is rendered using the WARP adapter (software rendering).
	if( isWarpAdapterUsedForDisplay() ) {
		// DWM rounds corners if registry value ForceEffectMode is 2 (even if WARP is used)
		// (read registry value only once because DWM also reads it only on startup)
		static DWORD forceEffectMode = MAXDWORD;
		if( forceEffectMode == MAXDWORD ) {
			DWORD data = 0;
			DWORD cbData = sizeof( data );
			if( ::RegGetValueW( HKEY_LOCAL_MACHINE, L"Software\\Microsoft\\Windows\\Dwm", L"ForceEffectMode",
					RRF_RT_DWORD, NULL, &data, &cbData ) == ERROR_SUCCESS )
				forceEffectMode = data;
			else
				forceEffectMode = 0;
		}

		return forceEffectMode == 2;
	}

	// DWM does not round corners when running in Azure Virtual Desktop (AVD) session.
	// Because we can not detect AVD, disable rounded corners for all remote sessions.
	if( ::GetSystemMetrics( SM_REMOTESESSION ) != 0 )
		return FALSE;

	return TRUE;
}
