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
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.util.function.Predicate;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.BorderUIResource;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.HiDPIUtils;
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

	private static Boolean supported;
	private static Provider nativeProvider;

	public static boolean isSupported() {
		initialize();
		return supported;
	}

	static Object install( JRootPane rootPane ) {
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
		if( !isSupported() )
			return false;

		return nativeProvider.hasCustomDecoration( window );
	}

	public static void setHasCustomDecoration( Window window, boolean hasCustomDecoration ) {
		if( !isSupported() )
			return;

		nativeProvider.setHasCustomDecoration( window, hasCustomDecoration );
	}

	static void setTitleBarHeightAndHitTestSpots( Window window, int titleBarHeight,
		Predicate<Point> captionHitTestCallback, Rectangle appIconBounds, Rectangle minimizeButtonBounds,
		Rectangle maximizeButtonBounds, Rectangle closeButtonBounds )
	{
		if( !isSupported() )
			return;

		nativeProvider.updateTitleBarInfo( window, titleBarHeight, captionHitTestCallback,
			appIconBounds, minimizeButtonBounds, maximizeButtonBounds, closeButtonBounds );
	}

	static boolean showWindow( Window window, int cmd ) {
		if( !isSupported() )
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
		void updateTitleBarInfo( Window window, int titleBarHeight, Predicate<Point> captionHitTestCallback,
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
		extends BorderUIResource.EmptyBorderUIResource
	{
		private static WindowTopBorder instance;

		private final Color activeLightColor = new Color( 0x707070 );
		private final Color activeDarkColor = new Color( 0x2D2E2F );
		private final Color inactiveLightColor = new Color( 0xaaaaaa );
		private final Color inactiveDarkColor = new Color( 0x494A4B );

		private boolean colorizationAffectsBorders;
		private Color activeColor;

		static WindowTopBorder getInstance() {
			if( instance == null )
				instance = new WindowTopBorder();
			return instance;
		}

		WindowTopBorder() {
			super( 1, 0, 0, 0 );

			update();
			installListeners();
        }

        void update() {
			colorizationAffectsBorders = isColorizationColorAffectsBorders();
			activeColor = calculateActiveBorderColor();
		}

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

		boolean isColorizationColorAffectsBorders() {
			return nativeProvider.isColorizationColorAffectsBorders();
		}

		Color getColorizationColor() {
			return nativeProvider.getColorizationColor();
		}

		int getColorizationColorBalance() {
			return nativeProvider.getColorizationColorBalance();
		}

		private Color calculateActiveBorderColor() {
			if( !colorizationAffectsBorders )
				return null;

			Color colorizationColor = getColorizationColor();
			if( colorizationColor != null ) {
				int colorizationColorBalance = getColorizationColorBalance();
				if( colorizationColorBalance < 0 || colorizationColorBalance > 100 )
					colorizationColorBalance = 100;

				if( colorizationColorBalance == 0 )
					return new Color( 0xD9D9D9 );
				if( colorizationColorBalance == 100 )
					return colorizationColor;

				float alpha = colorizationColorBalance / 100.0f;
				float remainder = 1 - alpha;
				int r = Math.round( colorizationColor.getRed() * alpha + 0xD9 * remainder );
				int g = Math.round( colorizationColor.getGreen() * alpha + 0xD9 * remainder );
				int b = Math.round( colorizationColor.getBlue() * alpha + 0xD9 * remainder );

				// avoid potential IllegalArgumentException in Color constructor
				r = Math.min( Math.max( r, 0 ), 255 );
				g = Math.min( Math.max( g, 0 ), 255 );
				b = Math.min( Math.max( b, 0 ), 255 );

				return new Color( r, g, b );
			}

			Color activeBorderColor = (Color) Toolkit.getDefaultToolkit().getDesktopProperty( "win.frame.activeBorderColor" );
			return (activeBorderColor != null) ? activeBorderColor : UIManager.getColor( "MenuBar.borderColor" );
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			Window window = SwingUtilities.windowForComponent( c );
			boolean active = window != null && window.isActive();
			boolean dark = FlatLaf.isLafDark();

			g.setColor( active
				? (activeColor != null ? activeColor : (dark ? activeDarkColor : activeLightColor))
				: (dark ? inactiveDarkColor : inactiveLightColor) );
			HiDPIUtils.paintAtScale1x( (Graphics2D) g, x, y, width, height, this::paintImpl );
		}

		private void paintImpl( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
			g.fillRect( x, y, width, 1 );
		}

		void repaintBorder( Component c ) {
			c.repaint( 0, 0, c.getWidth(), 1 );
		}
	}
}
