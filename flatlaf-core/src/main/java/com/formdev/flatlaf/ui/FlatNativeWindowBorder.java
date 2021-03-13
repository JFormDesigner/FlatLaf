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
import java.awt.Rectangle;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.JBRCustomDecorations.JBRWindowTopBorder;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Support for custom window decorations with native window border.
 *
 * @author Karl Tauber
 */
public class FlatNativeWindowBorder
{
	// check this field before using class JBRCustomDecorations to avoid unnecessary loading of that class
	private static final boolean canUseJBRCustomDecorations
		= SystemInfo.isJetBrainsJVM_11_orLater && SystemInfo.isWindows_10_orLater;

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

		// Check whether root pane already has a window, which is the case when switching LaF.
		// Also check whether the window is displayable, which is required to install
		// FlatLaf native window border.
		// If the window is not displayable, then it was probably closed/disposed but not yet removed
		// from the list of windows that AWT maintains and returns with Window.getWindows().
		// It could be also be a window that is currently hidden, but may be shown later.
		Window window = SwingUtilities.windowForComponent( rootPane );
		if( window != null && window.isDisplayable() ) {
			install( window, FlatSystemProperties.USE_WINDOW_DECORATIONS );
			return null;
		}

		// Install FlatLaf native window border, which must be done late,
		// when the native window is already created, because it needs access to the window.
		// "ancestor" property change event is fired from JComponent.addNotify() and removeNotify().
		PropertyChangeListener ancestorListener = e -> {
			Object newValue = e.getNewValue();
			if( newValue instanceof Window )
				install( (Window) newValue, FlatSystemProperties.USE_WINDOW_DECORATIONS );
			else if( newValue == null && e.getOldValue() instanceof Window )
				uninstall( (Window) e.getOldValue() );
		};
		rootPane.addPropertyChangeListener( "ancestor", ancestorListener );
		return ancestorListener;
	}

	static void install( Window window, String systemPropertyKey ) {
		if( hasCustomDecoration( window ) )
			return;

		// do not enable native window border if LaF provides decorations
		if( UIManager.getLookAndFeel().getSupportsWindowDecorations() )
			return;

		if( window instanceof JFrame ) {
			JFrame frame = (JFrame) window;

			// do not enable native window border if JFrame should use system window decorations
			// and if not forced to use FlatLaf/JBR native window decorations
			if( !JFrame.isDefaultLookAndFeelDecorated() &&
				!UIManager.getBoolean( "TitlePane.useWindowDecorations" ) &&
				!FlatSystemProperties.getBoolean( systemPropertyKey, false ) )
			  return;

			// do not enable native window border if frame is undecorated
			if( frame.isUndecorated() )
				return;

			// enable native window border for window
			setHasCustomDecoration( frame, true );

			// enable Swing window decoration
			frame.getRootPane().setWindowDecorationStyle( JRootPane.FRAME );

		} else if( window instanceof JDialog ) {
			JDialog dialog = (JDialog) window;

			// do not enable native window border if JDialog should use system window decorations
			// and if not forced to use FlatLaf/JBR native window decorations
			if( !JDialog.isDefaultLookAndFeelDecorated() &&
				!UIManager.getBoolean( "TitlePane.useWindowDecorations" ) &&
				!FlatSystemProperties.getBoolean( systemPropertyKey, false ) )
			  return;

			// do not enable native window border if dialog is undecorated
			if( dialog.isUndecorated() )
				return;

			// enable native window border for window
			setHasCustomDecoration( dialog, true );

			// enable Swing window decoration
			dialog.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
		}
	}

	static void uninstall( JRootPane rootPane, Object data ) {
		if( canUseJBRCustomDecorations ) {
			JBRCustomDecorations.uninstall( rootPane, data );
			return;
		}

		// remove listener
		if( data instanceof PropertyChangeListener )
			rootPane.removePropertyChangeListener( "ancestor", (PropertyChangeListener) data );

		// uninstall native window border, except when switching to another FlatLaf theme
		Window window = SwingUtilities.windowForComponent( rootPane );
		if( window != null )
			uninstall( window );
	}

	private static void uninstall( Window window ) {
		if( !hasCustomDecoration( window ) )
			return;

		// do not uninstall when switching to another FlatLaf theme
		if( UIManager.getLookAndFeel() instanceof FlatLaf )
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
		List<Rectangle> hitTestSpots, Rectangle appIconBounds )
	{
		if( canUseJBRCustomDecorations ) {
			JBRCustomDecorations.setTitleBarHeightAndHitTestSpots( window, titleBarHeight, hitTestSpots );
			return;
		}

		if( !isSupported() )
			return;

		nativeProvider.setTitleBarHeight( window, titleBarHeight );
		nativeProvider.setTitleBarHitTestSpots( window, hitTestSpots );
		nativeProvider.setTitleBarAppIconBounds( window, appIconBounds );
	}

	private static void initialize() {
		if( supported != null )
			return;
		supported = false;

		// requires Windows 10
		if( !SystemInfo.isWindows_10_orLater )
			return;

		// check whether disabled via system property
		if( !FlatSystemProperties.getBoolean( FlatSystemProperties.USE_WINDOW_DECORATIONS, true ) )
			return;

		try {
/*
			Class<?> cls = Class.forName( "com.formdev.flatlaf.natives.jna.windows.FlatWindowsNativeWindowBorder" );
			Method m = cls.getMethod( "getInstance" );
			nativeProvider = (Provider) m.invoke( null );
*/
			nativeProvider = FlatWindowsNativeWindowBorder.getInstance();

			supported = (nativeProvider != null);
		} catch( Exception ex ) {
			// ignore
		}
	}

	//---- interface Provider -------------------------------------------------

	public interface Provider
	{
		boolean hasCustomDecoration( Window window );
		void setHasCustomDecoration( Window window, boolean hasCustomDecoration );
		void setTitleBarHeight( Window window, int titleBarHeight );
		void setTitleBarHitTestSpots( Window window, List<Rectangle> hitTestSpots );
		void setTitleBarAppIconBounds( Window window, Rectangle appIconBounds );

		boolean isColorizationColorAffectsBorders();
		Color getColorizationColor();
		int getColorizationColorBalance();

		void addChangeListener( ChangeListener l );
		void removeChangeListener( ChangeListener l );
	}

	//---- class WindowTopBorder -------------------------------------------

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
