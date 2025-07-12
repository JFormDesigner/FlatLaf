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
import javax.swing.JOptionPane;
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
	private static int API_VERSION_WINDOWS = 1002;

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


	/**
	 * FILEOPENDIALOGOPTIONS
	 * see https://learn.microsoft.com/en-us/windows/win32/api/shobjidl_core/ne-shobjidl_core-_fileopendialogoptions
	 *
	 * @since 3.6
	 */
	public static final int
		FOS_OVERWRITEPROMPT = 0x2,		// default for Save
		FOS_STRICTFILETYPES = 0x4,
		FOS_NOCHANGEDIR = 0x8,			// default
		FOS_PICKFOLDERS = 0x20,
		FOS_FORCEFILESYSTEM = 0x40,
		FOS_ALLNONSTORAGEITEMS = 0x80,
		FOS_NOVALIDATE = 0x100,
		FOS_ALLOWMULTISELECT = 0x200,
		FOS_PATHMUSTEXIST = 0x800,		// default
		FOS_FILEMUSTEXIST = 0x1000,		// default for Open
		FOS_CREATEPROMPT = 0x2000,
		FOS_SHAREAWARE = 0x4000,
		FOS_NOREADONLYRETURN = 0x8000,	// default for Save
		FOS_NOTESTFILECREATE = 0x10000,
		FOS_HIDEMRUPLACES = 0x20000,
		FOS_HIDEPINNEDPLACES = 0x40000,
		FOS_NODEREFERENCELINKS = 0x100000,
		FOS_OKBUTTONNEEDSINTERACTION = 0x200000,
		FOS_DONTADDTORECENT = 0x2000000,
		FOS_FORCESHOWHIDDEN = 0x10000000,
		FOS_DEFAULTNOMINIMODE = 0x20000000,
		FOS_FORCEPREVIEWPANEON = 0x40000000,
		FOS_SUPPORTSTREAMABLEITEMS = 0x80000000;

	/**
	 * Shows the Windows system
	 * <a href="https://learn.microsoft.com/en-us/windows/win32/shell/common-file-dialog">file dialogs</a>
	 * <a href="https://learn.microsoft.com/en-us/windows/win32/api/shobjidl_core/nn-shobjidl_core-ifileopendialog">IFileOpenDialog</a> or
	 * <a href="https://learn.microsoft.com/en-us/windows/win32/api/shobjidl_core/nn-shobjidl_core-ifilesavedialog">IFileSaveDialog</a>.
	 * <p>
	 * <b>Note:</b> This method blocks the current thread until the user closes
	 * the file dialog. It is highly recommended to invoke it from a new thread
	 * to avoid blocking the AWT event dispatching thread.
	 *
	 * @param owner the owner of the file dialog; or {@code null}
	 * @param open if {@code true}, shows the open dialog; if {@code false}, shows the save dialog
	 * @param title text displayed in dialog title; or {@code null}
	 * @param okButtonLabel text displayed in default button; or {@code null}.
	 *        Use '&amp;' for mnemonics (e.g. "&amp;Choose").
	 *        Use '&amp;&amp;' for '&amp;' character (e.g. "Choose &amp;&amp; Quit").
	 * @param fileNameLabel text displayed in front of the filename text field; or {@code null}
	 * @param fileName user-editable filename currently shown in the filename field; or {@code null}
	 * @param folder current directory shown in the dialog; or {@code null}
	 * @param saveAsItem file to be used as the initial entry in a Save As dialog; or {@code null}.
	 *        File name is shown in filename text field, folder is selected in view.
	 *        To be used for saving files that already exist. For new files use {@code fileName}.
	 * @param defaultFolder folder used as a default if there is not a recently used folder value available; or {@code null}.
	 *        Windows somewhere stores default folder on a per-app basis.
	 *        So this is probably used only once when the app opens a file dialog for first time.
	 * @param defaultExtension default extension to be added to file name in save dialog; or {@code null}
	 * @param optionsSet options to set; see {@code FOS_*} constants
	 * @param optionsClear options to clear; see {@code FOS_*} constants
	 * @param callback approve callback; or {@code null}
	 * @param fileTypeIndex the file type that appears as selected (zero-based)
	 * @param fileTypes file types that the dialog can open or save.
	 *        Pairs of strings are required for each filter.
	 *        First string is the display name of the filter shown in the combobox (e.g. "Text Files").
	 *        Second string is the filter pattern (e.g. "*.txt", "*.exe;*.dll" or "*.*").
	 * @return file path(s) that the user selected; an empty array if canceled;
	 *         or {@code null} on failures (no dialog shown)
	 *
	 * @since 3.6
	 */
	public native static String[] showFileChooser( Window owner, boolean open,
		String title, String okButtonLabel, String fileNameLabel, String fileName,
		String folder, String saveAsItem, String defaultFolder, String defaultExtension,
		int optionsSet, int optionsClear, FileChooserCallback callback,
		int fileTypeIndex, String... fileTypes );

	/** @since 3.6 */
	public interface FileChooserCallback {
		boolean approve( String[] files, long hwndFileDialog );
	}

	/**
	 * Shows a modal Windows message dialog.
	 * <p>
	 * For use in {@link FileChooserCallback} only.
	 *
	 * @param hwndParent the parent of the message box
	 * @param messageType type of message being displayed:
	 *        {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE},
	 *        {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE} or
	 *        {@link JOptionPane#PLAIN_MESSAGE}
	 * @param title dialog box title; or {@code null} to use title from parent window
	 * @param text message to be displayed
	 * @param defaultButton index of the default button, which can be pressed using ENTER key
	 * @param buttons texts of the buttons.
	 *        Use '&amp;' for mnemonics (e.g. "&amp;Choose").
	 *        Use '&amp;&amp;' for '&amp;' character (e.g. "Choose &amp;&amp; Quit").
	 * @return index of pressed button; or -1 for ESC key
	 *
	 * @since 3.6
	 */
	public native static int showMessageDialog( long hwndParent, int messageType,
		String title, String text, int defaultButton, String... buttons );

	/**
	 * Shows a Windows message box
	 * <a href="https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-messagebox">MessageBox</a>.
	 * <p>
	 * For use in {@link FileChooserCallback} only.
	 *
	 * @param hwndParent the parent of the message box
	 * @param text message to be displayed
	 * @param caption dialog box title
	 * @param type see <a href="https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-messagebox#parameters">MessageBox parameter uType</a>
	 * @return see <a href="https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-messagebox#return-value">MessageBox Return value</a>
	 *
	 * @since 3.6
	 */
	public native static int showMessageBox( long hwndParent, String text, String caption, int type );
}
