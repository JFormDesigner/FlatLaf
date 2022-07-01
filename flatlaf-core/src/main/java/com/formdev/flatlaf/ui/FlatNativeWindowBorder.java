/*
 * Copyright 2021 FormDev Software GmbH
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
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.JBRCustomDecorations.JBRWindowTopBorder;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Support for custom window decorations with native window border.
 *
 * @author Karl Tauber
 * @since 1.1
 */
public class FlatNativeWindowBorder
{
	// can use window decorations if:
	// - on Windows 10 or later
	// - not if system property "sun.java2d.opengl" is true on Windows 10
	// - not when running in JetBrains Projector, Webswing or WinPE
	// - not disabled via system property
	private static final boolean canUseWindowDecorations =
		SystemInfo.isWindows_10_orLater &&
		(SystemInfo.isWindows_11_orLater || !FlatSystemProperties.getBoolean( "sun.java2d.opengl", false )) &&
		!SystemInfo.isProjector &&
		!SystemInfo.isWebswing &&
		!SystemInfo.isWinPE &&
		FlatSystemProperties.getBoolean( FlatSystemProperties.USE_WINDOW_DECORATIONS, true );

	// check this field before using class JBRCustomDecorations to avoid unnecessary loading of that class
	private static final boolean canUseJBRCustomDecorations =
		canUseWindowDecorations &&
		SystemInfo.isJetBrainsJVM_11_orLater &&
		FlatSystemProperties.getBoolean( FlatSystemProperties.USE_JETBRAINS_CUSTOM_DECORATIONS, false );

	private static Boolean supported;
	private static Provider nativeProvider;

	public static boolean isSupported() {
		if( canUseJBRCustomDecorations )
			return JBRCustomDecorations.isSupported();

		initialize();
		return supported;
	}

	static Object install( JRootPane rootPane ) {
		if( canUseJBRCustomDecorations )
			return JBRCustomDecorations.install( rootPane );

		if( !isSupported() )
			return null;

		// do nothing if root pane has a parent that is not a window (e.g. a JInternalFrame)
		Container parent = rootPane.getParent();
		if( parent != null && !(parent instanceof Window) )
			return null;

		// Check whether root pane already has a window, which is the case when
		// switching from another LaF to FlatLaf.
		// Also check whether the window is displayable, which is required to install
		// FlatLaf native window border.
		// If the window is not displayable, then it was probably closed/disposed but not yet removed
		// from the list of windows that AWT maintains and returns with Window.getWindows().
		// It could be also be a window that is currently hidden, but may be shown later.
		if( parent instanceof Window && parent.isDisplayable() )
			install( (Window) parent );

		// Install FlatLaf native window border, which must be done late,
		// when the native window is already created, because it needs access to the window.
		// Uninstall FlatLaf native window border when window is disposed (or root pane removed).
		// "ancestor" property change event is fired from JComponent.addNotify() and removeNotify().
		PropertyChangeListener ancestorListener = e -> {
			Object newValue = e.getNewValue();
			if( newValue instanceof Window )
				install( (Window) newValue );
			else if( newValue == null && e.getOldValue() instanceof Window )
				uninstall( (Window) e.getOldValue() );
		};
		rootPane.addPropertyChangeListener( "ancestor", ancestorListener );
		return ancestorListener;
	}

	static void install( Window window ) {
		if( hasCustomDecoration( window ) )
			return;

		// do not enable native window border if LaF provides decorations
		if( UIManager.getLookAndFeel().getSupportsWindowDecorations() )
			return;

		if( window instanceof JFrame ) {
			JFrame frame = (JFrame) window;
			JRootPane rootPane = frame.getRootPane();

			// check whether disabled via system property, client property or UI default
			if( !useWindowDecorations( rootPane ) )
				return;

			// do not enable native window border if frame is undecorated
			if( frame.isUndecorated() )
				return;

			// enable native window border for window
			setHasCustomDecoration( frame, true );

			// avoid double window title bar if enabling native window border failed
			if( !hasCustomDecoration( frame ) )
				return;

			// enable Swing window decoration
			rootPane.setWindowDecorationStyle( JRootPane.FRAME );

		} else if( window instanceof JDialog ) {
			JDialog dialog = (JDialog) window;
			JRootPane rootPane = dialog.getRootPane();

			// check whether disabled via system property, client property or UI default
			if( !useWindowDecorations( rootPane ) )
				return;

			// do not enable native window border if dialog is undecorated
			if( dialog.isUndecorated() )
				return;

			// enable native window border for window
			setHasCustomDecoration( dialog, true );

			// avoid double window title bar if enabling native window border failed
			if( !hasCustomDecoration( dialog ) )
				return;

			// enable Swing window decoration
			rootPane.setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
		}
	}

	static void uninstall( JRootPane rootPane, Object data ) {
		if( canUseJBRCustomDecorations ) {
			JBRCustomDecorations.uninstall( rootPane, data );
			return;
		}

		if( !isSupported() )
			return;

		// remove listener
		if( data instanceof PropertyChangeListener )
			rootPane.removePropertyChangeListener( "ancestor", (PropertyChangeListener) data );

		// do not uninstall when switching to another FlatLaf theme and if still enabled
		if( UIManager.getLookAndFeel() instanceof FlatLaf && useWindowDecorations( rootPane ) )
			return;

		// uninstall native window border
		Container parent = rootPane.getParent();
		if( parent instanceof Window )
			uninstall( (Window) parent );
	}

	private static void uninstall( Window window ) {
		if( !hasCustomDecoration( window ) )
			return;

		// disable native window border for window
		setHasCustomDecoration( window, false );

		if( window instanceof JFrame ) {
			JFrame frame = (JFrame) window;

			// disable Swing window decoration
			frame.getRootPane().setWindowDecorationStyle( JRootPane.NONE );

		} else if( window instanceof JDialog ) {
			JDialog dialog = (JDialog) window;

			// disable Swing window decoration
			dialog.getRootPane().setWindowDecorationStyle( JRootPane.NONE );
		}
	}

	private static boolean useWindowDecorations( JRootPane rootPane ) {
		return FlatUIUtils.getBoolean( rootPane,
			FlatSystemProperties.USE_WINDOW_DECORATIONS,
			FlatClientProperties.USE_WINDOW_DECORATIONS,
			"TitlePane.useWindowDecorations",
			false );
	}

	public static boolean hasCustomDecoration( Window window ) {
		if( canUseJBRCustomDecorations )
			return JBRCustomDecorations.hasCustomDecoration( window );

		if( !isSupported() )
			return false;

		return nativeProvider.hasCustomDecoration( window );
	}

	public static void setHasCustomDecoration( Window window, boolean hasCustomDecoration ) {
		if( canUseJBRCustomDecorations ) {
			JBRCustomDecorations.setHasCustomDecoration( window, hasCustomDecoration );
			return;
		}

		if( !isSupported() )
			return;

		nativeProvider.setHasCustomDecoration( window, hasCustomDecoration );
	}

	static void setTitleBarHeightAndHitTestSpots( Window window, int titleBarHeight,
		List<Rectangle> hitTestSpots, Rectangle appIconBounds, Rectangle minimizeButtonBounds,
		Rectangle maximizeButtonBounds, Rectangle closeButtonBounds )
	{
		if( canUseJBRCustomDecorations ) {
			JBRCustomDecorations.setTitleBarHeightAndHitTestSpots( window, titleBarHeight, hitTestSpots );
			return;
		}

		if( !isSupported() )
			return;

		nativeProvider.updateTitleBarInfo( window, titleBarHeight, hitTestSpots,
			appIconBounds, minimizeButtonBounds, maximizeButtonBounds, closeButtonBounds );
	}

	static boolean showWindow( Window window, int cmd ) {
		if( canUseJBRCustomDecorations || !isSupported() )
			return false;

		return nativeProvider.showWindow( window, cmd );
	}

	private static void initialize() {
		if( supported != null )
			return;
		supported = false;

		if( !canUseWindowDecorations )
			return;

		try {
/*
			Class<?> cls = Class.forName( "com.formdev.flatlaf.natives.jna.windows.FlatWindowsNativeWindowBorder" );
			java.lang.reflect.Method m = cls.getMethod( "getInstance" );
			setNativeProvider( (Provider) m.invoke( null ) );
*/
			setNativeProvider( FlatWindowsNativeWindowBorder.getInstance() );
		} catch( Exception ex ) {
			// ignore
		}
	}

	/** @since 1.1.1 */
	public static void setNativeProvider( Provider provider ) {
		if( nativeProvider != null )
			throw new IllegalStateException();

		nativeProvider = provider;
		supported = (nativeProvider != null);
	}

	//---- interface Provider -------------------------------------------------

	public interface Provider
	{
		boolean hasCustomDecoration( Window window );
		void setHasCustomDecoration( Window window, boolean hasCustomDecoration );
		void updateTitleBarInfo( Window window, int titleBarHeight, List<Rectangle> hitTestSpots,
			Rectangle appIconBounds, Rectangle minimizeButtonBounds, Rectangle maximizeButtonBounds,
			Rectangle closeButtonBounds );

		// commands for showWindow(); values must match Win32 API
		// https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-showwindow
		int SW_MAXIMIZE = 3;
		int SW_MINIMIZE = 6;
		int SW_RESTORE = 9;
		boolean showWindow( Window window, int cmd );

		boolean isColorizationColorAffectsBorders();
		Color getColorizationColor();
		int getColorizationColorBalance();

		void addChangeListener( ChangeListener l );
		void removeChangeListener( ChangeListener l );
	}

	//---- class WindowTopBorder -------------------------------------------

	/**
	 * Window top border used on Windows 10.
	 * No longer needed since Windows 11.
	 */
	static class WindowTopBorder
		extends JBRCustomDecorations.JBRWindowTopBorder
	{
		private static WindowTopBorder instance;

		static JBRWindowTopBorder getInstance() {
			if( canUseJBRCustomDecorations )
				return JBRWindowTopBorder.getInstance();

			if( instance == null )
				instance = new WindowTopBorder();
			return instance;
		}

		@Override
		void installListeners() {
			nativeProvider.addChangeListener( e -> {
				update();

				// repaint top borders of all windows
				for( Window window : Window.getWindows() ) {
					if( window.isDisplayable() )
						window.repaint( 0, 0, window.getWidth(), 1 );
				}
			} );
		}

		@Override
		boolean isColorizationColorAffectsBorders() {
			return nativeProvider.isColorizationColorAffectsBorders();
		}

		@Override
		Color getColorizationColor() {
			return nativeProvider.getColorizationColor();
		}

		@Override
		int getColorizationColorBalance() {
			return nativeProvider.getColorizationColorBalance();
		}
	}
}
