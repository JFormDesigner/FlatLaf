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
	private static long osBuildNumber = Long.MIN_VALUE;

	public static boolean isLoaded() {
		return SystemInfo.isWindows && FlatNativeLibrary.isLoaded();
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
	 * Sets the color of the window border.
	 * The red/green/blue values must be in range {@code 0 - 255}.
	 * If red is {@code -1}, then the system default border color is used (useful to reset the border color).
	 * If red is {@code -2}, then no border is painted.
	 * <p>
	 * Invokes Win32 API method {@code DwmSetWindowAttribute(DWMWA_BORDER_COLOR)}.
	 * See https://learn.microsoft.com/en-us/windows/win32/api/dwmapi/nf-dwmapi-dwmsetwindowattribute
	 * <p>
	 * Supported since Windows 11 Build 22000.
	 */
	public native static boolean setWindowBorderColor( long hwnd, int red, int green, int blue );
}
