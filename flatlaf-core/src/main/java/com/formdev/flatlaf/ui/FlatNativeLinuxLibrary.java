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
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Native methods for Linux.
 * <p>
 * <b>Note</b>: This is private API. Do not use!
 *
 * @author Karl Tauber
 * @since 2.5
 */
class FlatNativeLinuxLibrary
{
	private static int API_VERSION_LINUX = 3001;

	/**
	 * Checks whether native library is loaded/available.
	 * <p>
	 * <b>Note</b>: It is required to invoke this method before invoking any other
	 *              method of this class. Otherwise, the native library may not be loaded.
	 */
	static boolean isLoaded() {
		return SystemInfo.isLinux && FlatNativeLibrary.isLoaded( API_VERSION_LINUX );
	}

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
}
