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

import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Native methods for Linux.
 * <p>
 * <b>Note</b>: This is private API. Do not use!
 *
 * @author Karl Tauber
 * @since 2.5
 */
public class FlatNativeLinuxLibrary
{
	private static int API_VERSION_LINUX = 3002;

	/**
	 * Checks whether native library is loaded/available.
	 * <p>
	 * <b>Note</b>: It is required to invoke this method before invoking any other
	 *              method of this class. Otherwise, the native library may not be loaded.
	 */
	public static boolean isLoaded() {
		return SystemInfo.isLinux && FlatNativeLibrary.isLoaded( API_VERSION_LINUX );
	}


	//---- X Window System ----------------------------------------------------

	// direction for _NET_WM_MOVERESIZE message
	// see https://specifications.freedesktop.org/wm-spec/latest/ar01s04.html
	static final int
		SIZE_TOPLEFT     = 0,
		SIZE_TOP         = 1,
		SIZE_TOPRIGHT    = 2,
		SIZE_RIGHT       = 3,
		SIZE_BOTTOMRIGHT = 4,
		SIZE_BOTTOM      = 5,
		SIZE_BOTTOMLEFT  = 6,
		SIZE_LEFT        = 7,
		MOVE             = 8;

	private static Boolean isXWindowSystem;

	private static boolean isXWindowSystem() {
		if( isXWindowSystem == null )
			isXWindowSystem = Toolkit.getDefaultToolkit().getClass().getName().endsWith( ".XToolkit" );
		return isXWindowSystem;
	}

	static boolean isWMUtilsSupported( Window window ) {
		return hasCustomDecoration( window ) && isXWindowSystem() && isLoaded();
	}

	static boolean moveOrResizeWindow( Window window, MouseEvent e, int direction ) {
		Point pt = scale( window, e.getLocationOnScreen() );
		return xMoveOrResizeWindow( window, pt.x, pt.y, direction );

/*
		try {
			Class<?> cls = Class.forName( "com.formdev.flatlaf.natives.jna.linux.X11WmUtils" );
			java.lang.reflect.Method m = cls.getMethod( "xMoveOrResizeWindow", Window.class, int.class, int.class, int.class );
			return (Boolean) m.invoke( null, window, pt.x, pt.y, direction );
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
*/
	}

	static boolean showWindowMenu( Window window, MouseEvent e ) {
		Point pt = scale( window, e.getLocationOnScreen() );
		return xShowWindowMenu( window, pt.x, pt.y );

/*
		try {
			Class<?> cls = Class.forName( "com.formdev.flatlaf.natives.jna.linux.X11WmUtils" );
			java.lang.reflect.Method m = cls.getMethod( "xShowWindowMenu", Window.class, int.class, int.class );
			return (Boolean) m.invoke( null, window, pt.x, pt.y );
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
*/
	}

	private static Point scale( Window window, Point pt ) {
		GraphicsConfiguration gc = window.getGraphicsConfiguration();
		if( gc == null )
			return pt;

		AffineTransform transform = gc.getDefaultTransform();
		int x = (int) Math.round( pt.x * transform.getScaleX() );
		int y = (int) Math.round( pt.y * transform.getScaleY() );
		return new Point( x, y );
	}

	// X Window System
	private static native boolean xMoveOrResizeWindow( Window window, int x, int y, int direction );
	private static native boolean xShowWindowMenu( Window window, int x, int y );

	private static boolean hasCustomDecoration( Window window ) {
		return (window instanceof JFrame && JFrame.isDefaultLookAndFeelDecorated() && ((JFrame)window).isUndecorated()) ||
			(window instanceof JDialog && JDialog.isDefaultLookAndFeelDecorated() && ((JDialog)window).isUndecorated());
	}


	//---- GTK ----------------------------------------------------------------

	private static Boolean isGtk3Available;

	/**
	 * Checks whether GTK 3 is available.
	 * Use this before invoking any native method that uses GTK.
	 * Otherwise the app may terminate immediately if GTK is not installed.
	 * <p>
	 * This works because Java uses {@code dlopen(RTLD_LAZY)} to load JNI libraries,
	 * which only resolves symbols as the code that references them is executed.
	 *
	 * @since 3.6
	 */
	public static boolean isGtk3Available() {
		if( isGtk3Available == null )
			isGtk3Available = isLibAvailable( "libgtk-3.so.0" ) || isLibAvailable( "libgtk-3.so" );
		return isGtk3Available;
	}

	private native static boolean isLibAvailable( String libname );

	/**
	 * https://docs.gtk.org/gtk3/iface.FileChooser.html#properties
	 *
	 * @since 3.6
	 */
	public static final int
		FC_select_folder					= 1 << 0,
		FC_select_multiple					= 1 << 1,
		FC_show_hidden						= 1 << 2,
		FC_local_only						= 1 << 3,  // default
		FC_do_overwrite_confirmation		= 1 << 4,  // GTK 3 only; removed and always-on in GTK 4
		FC_create_folders					= 1 << 5;  // default for Save

	/**
	 * Shows the Linux/GTK system file dialog
	 * <a href="https://docs.gtk.org/gtk3/class.FileChooserDialog.html">GtkFileChooserDialog</a>.
	 * <p>
	 * Uses {@code GTK_FILE_CHOOSER_ACTION_SELECT_FOLDER} if {@link #FC_select_folder} is set in parameter {@code optionsSet}.
	 * Otherwise uses {@code GTK_FILE_CHOOSER_ACTION_OPEN} if parameter {@code open} is {@code true},
	 * or {@code GTK_FILE_CHOOSER_ACTION_SAVE} if {@code false}.
	 * <p>
	 * <b>Note:</b> This method blocks the current thread until the user closes
	 * the file dialog. It is highly recommended to invoke it from a new thread
	 * to avoid blocking the AWT event dispatching thread.
	 *
	 * @param owner the owner of the file dialog; or {@code null}
	 * @param open if {@code true}, shows the open dialog; if {@code false}, shows the save dialog
	 * @param title text displayed in dialog title; or {@code null}
	 * @param okButtonLabel text displayed in default button; or {@code null}.
	 *        Use '_' for mnemonics (e.g. "_Choose")
	 *        Use '__' for '_' character (e.g. "Choose__and__Quit").
	 * @param currentName user-editable filename currently shown in the filename field in save dialog; or {@code null}
	 * @param currentFolder current directory shown in the dialog; or {@code null}
	 * @param optionsSet options to set; see {@code FOS_*} constants
	 * @param optionsClear options to clear; see {@code FOS_*} constants
	 * @param callback approve callback; or {@code null}
	 * @param fileTypeIndex the file type that appears as selected (zero-based)
	 * @param fileTypes file types that the dialog can open or save.
	 *        Two or more strings and {@code null} are required for each filter.
	 *        First string is the display name of the filter shown in the combobox (e.g. "Text Files").
	 *        Subsequent strings are the filter patterns (e.g. "*.txt" or "*").
	 *        {@code null} is required to mark end of filter.
	 * @return file path(s) that the user selected; an empty array if canceled;
	 *         or {@code null} on failures (no dialog shown)
	 *
	 * @since 3.6
	 */
	public native static String[] showFileChooser( Window owner, boolean open,
		String title, String okButtonLabel, String currentName, String currentFolder,
		int optionsSet, int optionsClear, FileChooserCallback callback,
		int fileTypeIndex, String... fileTypes );

	/** @since 3.6 */
	public interface FileChooserCallback {
		boolean approve( String[] files, long hwndFileDialog );
	}

	/**
	 * Shows a GTK message box
	 * <a href="https://docs.gtk.org/gtk3/class.MessageDialog.html">GtkMessageDialog</a>.
	 * <p>
	 * For use in {@link FileChooserCallback} only.
	 *
	 * @param hwndParent the parent of the message box
	 * @param messageType type of message being displayed:
	 *        {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE},
	 *        {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE} or
	 *        {@link JOptionPane#PLAIN_MESSAGE}
	 * @param primaryText primary text; if the dialog has a secondary text,
	 *        this will appear as title in a larger bold font
	 * @param secondaryText secondary text; shown below of primary text; or {@code null}
	 * @param defaultButton index of the default button, which can be pressed using ENTER key
	 * @param buttons texts of the buttons; if no buttons given the a default "OK" button is shown.
	 *        Use '_' for mnemonics (e.g. "_Choose")
	 *        Use '__' for '_' character (e.g. "Choose__and__Quit").
	 * @return index of pressed button; or -1 for ESC key
	 *
	 * @since 3.6
	 */
	public native static int showMessageDialog( long hwndParent, int messageType,
		String primaryText, String secondaryText, int defaultButton, String... buttons );
}
