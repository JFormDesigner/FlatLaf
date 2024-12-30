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
	private static int API_VERSION_MACOS = 2002;

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


	/** @since 3.6 */
	public static final int
		// NSOpenPanel
		FC_canChooseFiles					= 1 << 0,  // default
		FC_canChooseDirectories				= 1 << 1,
		FC_resolvesAliases_NO				= 1 << 2,  // default
		FC_allowsMultipleSelection			= 1 << 3,
		// NSSavePanel
		FC_showsTagField_YES				= 1 << 8,  // default for Save
		FC_showsTagField_NO					= 1 << 9,  // default for Open
		FC_canCreateDirectories_YES			= 1 << 10, // default for Save
		FC_canCreateDirectories_NO			= 1 << 11, // default for Open
		FC_canSelectHiddenExtension			= 1 << 12,
		FC_showsHiddenFiles					= 1 << 14,
		FC_extensionHidden					= 1 << 16,
		FC_allowsOtherFileTypes				= 1 << 18,
		FC_treatsFilePackagesAsDirectories	= 1 << 20;

	/**
	 * Shows the macOS system file dialogs
	 * <a href="https://developer.apple.com/documentation/appkit/nsopenpanel?language=objc">NSOpenPanel</a> or
	 * <a href="https://developer.apple.com/documentation/appkit/nssavepanel?language=objc">NSSavePanel</a>.
	 * <p>
	 * <b>Note:</b> This method blocks the current thread until the user closes
	 * the file dialog. It is highly recommended to invoke it from a new thread
	 * to avoid blocking the AWT event dispatching thread.
	 *
	 * @param open if {@code true}, shows the open dialog; if {@code false}, shows the save dialog
	 * @param title text displayed at top of save dialog (not used in open dialog)
	 * @param prompt text displayed in default button
	 * @param message text displayed at top of open/save dialogs
	 * @param nameFieldLabel text displayed in front of the filename text field in save dialog (not used in open dialog)
	 * @param nameFieldStringValue user-editable filename currently shown in the name field in save dialog (not used in open dialog)
	 * @param directoryURL current directory shown in the dialog
	 * @param options see {@code FC_*} constants
	 * @param allowedFileTypes allowed filename extensions (e.g. "txt")
	 * @return file path(s) that the user selected, or {@code null} if canceled
	 *
	 * @since 3.6
	 */
	public native static String[] showFileChooser( boolean open,
		String title, String prompt, String message, String nameFieldLabel,
		String nameFieldStringValue, String directoryURL, int options,
		String... allowedFileTypes );
}
