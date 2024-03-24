/*
 * Copyright 2023 FormDev Software GmbH
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

import java.awt.Rectangle;
import java.awt.Window;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Native methods for macOS.
 * <p>
 * <b>Note</b>: This is private API. Do not use!
 *
 * <h2>Methods that use windows as parameter</h2>
 *
 * For all methods that accept a {@link java.awt.Window} as parameter,
 * the underlying macOS window must be already created,
 * otherwise the method fails. You can use following to ensure this:
 * <pre>{@code
 * if( !window.isDisplayable() )
 *     window.addNotify();
 * }</pre>
 * or invoke the method after packing the window. E.g.
 * <pre>{@code
 * window.pack();
 * }</pre>
 *
 * @author Karl Tauber
 * @since 3.3
 */
public class FlatNativeMacLibrary
{
	private static int API_VERSION_MACOS = 2001;

	/**
	 * Checks whether native library is loaded/available.
	 * <p>
	 * <b>Note</b>: It is required to invoke this method before invoking any other
	 *              method of this class. Otherwise, the native library may not be loaded.
	 */
	public static boolean isLoaded() {
		return SystemInfo.isMacOS && FlatNativeLibrary.isLoaded( API_VERSION_MACOS );
	}

	public native static boolean setWindowRoundedBorder( Window window, float radius, float borderWidth, int borderColor );

	/** @since 3.4 */
	public static final int
		BUTTONS_SPACING_DEFAULT = 0,
		BUTTONS_SPACING_MEDIUM = 1,
		BUTTONS_SPACING_LARGE = 2;

	/** @since 3.4 */ public native static boolean setWindowButtonsSpacing( Window window, int buttonsSpacing );
	/** @since 3.4 */ public native static Rectangle getWindowButtonsBounds( Window window );
	/** @since 3.4 */ public native static boolean isWindowFullScreen( Window window );
	/** @since 3.4 */ public native static boolean toggleWindowFullScreen( Window window );
}
