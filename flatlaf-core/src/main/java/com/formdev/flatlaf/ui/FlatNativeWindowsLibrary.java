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

package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.Window;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Native methods for Windows.
 * <p>
 * <b>Note</b>: This is private API. Do not use!
 *
 * @author Karl Tauber
 * @since 3.1
 */
public class FlatNativeWindowsLibrary
{
	private static int API_VERSION_WINDOWS = 1001;

	private static long osBuildNumber = Long.MIN_VALUE;

	/**
	 * Checks whether native library is loaded/available.
	 * <p>
	 * <b>Note</b>: It is required to invoke this method before invoking any other
	 *              method of this class. Otherwise, the native library may not be loaded.
	 */
	public static boolean isLoaded() {
		return SystemInfo.isWindows && FlatNativeLibrary.isLoaded( API_VERSION_WINDOWS );
	}

	/**
	 * Gets the Windows operating system build number.
	 * <p>
	 * Invokes Win32 API method {@code GetVersionEx()} and returns {@code OSVERSIONINFO.dwBuildNumber}.
	 * See https://learn.microsoft.com/en-us/windows/win32/api/sysinfoapi/nf-sysinfoapi-getversionexa
	 */
	public static long getOSBuildNumber() {
		if( osBuildNumber == Long.MIN_VALUE )
			osBuildNumber = getOSBuildNumberImpl();
		return osBuildNumber;
	}

	/**
	 * Invokes Win32 API method {@code GetVersionEx()} and returns {@code OSVERSIONINFO.dwBuildNumber}.
	 * See https://learn.microsoft.com/en-us/windows/win32/api/sysinfoapi/nf-sysinfoapi-getversionexa
	 */
	private native static long getOSBuildNumberImpl();

	/**
	 * Gets the Windows window handle (HWND) for the given Swing window.
	 * <p>
	 * Note that the underlying Windows window must be already created,
	 * otherwise this method returns zero. Use following to ensure this:
	 * <pre>{@code
	 * if( !window.isDisplayable() )
	 *     window.addNotify();
	 * }</pre>
	 * or invoke this method after packing the window. E.g.
	 * <pre>{@code
	 * window.pack();
	 * long hwnd = getHWND( window );
	 * }</pre>
	 */
	public native static long getHWND( Window window );

	/**
	 * DWM_WINDOW_CORNER_PREFERENCE
	 * see https://learn.microsoft.com/en-us/windows/win32/api/dwmapi/ne-dwmapi-dwm_window_corner_preference
	 */
	public static final int
		DWMWCP_DEFAULT = 0,
		DWMWCP_DONOTROUND = 1,
		DWMWCP_ROUND = 2,
		DWMWCP_ROUNDSMALL = 3;

	/**
	 * Sets the rounded corner preference for the window.
	 * Allowed values are {@link #DWMWCP_DEFAULT}, {@link #DWMWCP_DONOTROUND},
	 * {@link #DWMWCP_ROUND} and {@link #DWMWCP_ROUNDSMALL}.
	 * <p>
	 * Invokes Win32 API method {@code DwmSetWindowAttribute(DWMWA_WINDOW_CORNER_PREFERENCE)}.
	 * See https://learn.microsoft.com/en-us/windows/win32/api/dwmapi/nf-dwmapi-dwmsetwindowattribute
	 * <p>
	 * Supported since Windows 11 Build 22000.
	 */
	public native static boolean setWindowCornerPreference( long hwnd, int cornerPreference );

	/**
	 * DWMWINDOWATTRIBUTE
	 * see https://learn.microsoft.com/en-us/windows/win32/api/dwmapi/ne-dwmapi-dwmwindowattribute
	 *
	 * @since 3.3
	 */
	public static final int
		DWMWA_USE_IMMERSIVE_DARK_MODE = 20,
		DWMWA_BORDER_COLOR = 34,
		DWMWA_CAPTION_COLOR = 35,
		DWMWA_TEXT_COLOR = 36;

	/**
	 * Invokes Win32 API method {@code DwmSetWindowAttribute()} with a {@code BOOL} attribute value.
	 * See https://learn.microsoft.com/en-us/windows/win32/api/dwmapi/nf-dwmapi-dwmsetwindowattribute
	 *
	 * @since 3.3
	 */
	public native static boolean dwmSetWindowAttributeBOOL( long hwnd, int attribute, boolean value );

	/**
	 * Invokes Win32 API method {@code DwmSetWindowAttribute()} with a {@code DWORD} attribute value.
	 * See https://learn.microsoft.com/en-us/windows/win32/api/dwmapi/nf-dwmapi-dwmsetwindowattribute
	 *
	 * @since 3.3
	 */
	public native static boolean dwmSetWindowAttributeDWORD( long hwnd, int attribute, int value );

	/** @since 3.3 */
	public static final int
		// use this constant to reset any window part colors to the system default behavior
		DWMWA_COLOR_DEFAULT = 0xFFFFFFFF,
		// use this constant to specify that a window part should not be rendered
		DWMWA_COLOR_NONE = 0xFFFFFFFE;

	/** @since 3.3 */
	public static final Color COLOR_NONE = new Color( 0, true );

	/**
	 * Invokes Win32 API method {@code DwmSetWindowAttribute()} with a {@code COLORREF} attribute value.
	 * See https://learn.microsoft.com/en-us/windows/win32/api/dwmapi/nf-dwmapi-dwmsetwindowattribute
	 * <p>
	 * Supported since Windows 11 Build 22000.
	 *
	 * @since 3.3
	 */
	public static boolean dwmSetWindowAttributeCOLORREF( long hwnd, int attribute, Color color ) {
		// convert color to Windows RGB value
		int rgb = (color == COLOR_NONE)
			? DWMWA_COLOR_NONE
			: (color != null
				? (color.getRed() | (color.getGreen() << 8) | (color.getBlue() << 16))
				: DWMWA_COLOR_DEFAULT);

		// DwmSetWindowAttribute() expects COLORREF as attribute value, which is defined as DWORD
		return dwmSetWindowAttributeDWORD( hwnd, attribute, rgb );
	}
}
