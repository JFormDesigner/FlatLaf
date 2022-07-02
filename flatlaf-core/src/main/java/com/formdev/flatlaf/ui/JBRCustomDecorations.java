/*
 * Copyright 2020 FormDev Software GmbH
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
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Support for custom window decorations provided by JetBrains Runtime (based on OpenJDK).
 * Requires that the application runs on Windows 10 in a JetBrains Runtime 11 or later.
 * <ul>
 *   <li><a href="https://confluence.jetbrains.com/display/JBR/JetBrains+Runtime">https://confluence.jetbrains.com/display/JBR/JetBrains+Runtime</a></li>
 *   <li><a href="https://github.com/JetBrains/JetBrainsRuntime">https://github.com/JetBrains/JetBrainsRuntime</a></li>
 * </ul>
 *
 * @author Karl Tauber
 */
public class JBRCustomDecorations
{
	private static Boolean supported;
	private static Method Window_hasCustomDecoration;
	private static Method Window_setHasCustomDecoration;
	private static Method WWindowPeer_setCustomDecorationTitleBarHeight;
	private static Method WWindowPeer_setCustomDecorationHitTestSpots;
	private static Method AWTAccessor_getComponentAccessor;
	private static Method AWTAccessor_ComponentAccessor_getPeer;

	public static boolean isSupported() {
		initialize();
		return supported;
	}

	static Object install( JRootPane rootPane ) {
		if( !isSupported() )
			return null;

		// check whether root pane already has a parent, which is the case when switching LaF
		Container parent = rootPane.getParent();
		if( parent != null ) {
			if( parent instanceof Window )
				FlatNativeWindowBorder.install( (Window) parent );
			return null;
		}

		// Use hierarchy listener to wait until the root pane is added to a window.
		// Enabling JBR decorations must be done very early, probably before
		// window becomes displayable (window.isDisplayable()). Tried also using
		// "ancestor" property change event on root pane, but this is invoked too late.
		HierarchyListener addListener = new HierarchyListener() {
			@Override
			public void hierarchyChanged( HierarchyEvent e ) {
				if( e.getChanged() != rootPane || (e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) == 0 )
					return;

				Container parent = e.getChangedParent();
				if( parent instanceof Window )
					FlatNativeWindowBorder.install( (Window) parent );

				// remove listener since it is actually not possible to uninstall JBR decorations
				// use invokeLater to remove listener to avoid that listener
				// is removed while listener queue is processed
				EventQueue.invokeLater( () -> {
					rootPane.removeHierarchyListener( this );
				} );
			}
		};
		rootPane.addHierarchyListener( addListener );
		return addListener;
	}

	static void uninstall( JRootPane rootPane, Object data ) {
		// remove listener (if not yet done)
		if( data instanceof HierarchyListener )
			rootPane.removeHierarchyListener( (HierarchyListener) data );

		// since it is actually not possible to uninstall JBR decorations,
		// simply reduce titleBarHeight so that it is still possible to resize window
		// and remove hitTestSpots
		Container parent = rootPane.getParent();
		if( parent instanceof Window )
			setHasCustomDecoration( (Window) parent, false );
	}

	static boolean hasCustomDecoration( Window window ) {
		if( !isSupported() )
			return false;

		try {
			return (Boolean) Window_hasCustomDecoration.invoke( window );
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
			return false;
		}
	}

	static void setHasCustomDecoration( Window window, boolean hasCustomDecoration ) {
		if( !isSupported() )
			return;

		try {
			if( hasCustomDecoration )
				Window_setHasCustomDecoration.invoke( window );
			else
				setTitleBarHeightAndHitTestSpots( window, 4, Collections.emptyList() );
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	static void setTitleBarHeightAndHitTestSpots( Window window, int titleBarHeight, List<Rectangle> hitTestSpots ) {
		if( !isSupported() )
			return;

		try {
			Object compAccessor = AWTAccessor_getComponentAccessor.invoke( null );
			Object peer = AWTAccessor_ComponentAccessor_getPeer.invoke( compAccessor, window );
			WWindowPeer_setCustomDecorationTitleBarHeight.invoke( peer, titleBarHeight );
			WWindowPeer_setCustomDecorationHitTestSpots.invoke( peer, hitTestSpots );
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	private static void initialize() {
		if( supported != null )
			return;
		supported = false;

		// requires JetBrains Runtime 11 and Windows 10
		if( !SystemInfo.isJetBrainsJVM_11_orLater || !SystemInfo.isWindows_10_orLater )
			return;

		try {
			Class<?> awtAcessorClass = Class.forName( "sun.awt.AWTAccessor" );
			Class<?> compAccessorClass = Class.forName( "sun.awt.AWTAccessor$ComponentAccessor" );
			AWTAccessor_getComponentAccessor = awtAcessorClass.getDeclaredMethod( "getComponentAccessor" );
			AWTAccessor_ComponentAccessor_getPeer = compAccessorClass.getDeclaredMethod( "getPeer", Component.class );

			Class<?> peerClass = Class.forName( "sun.awt.windows.WWindowPeer" );
			WWindowPeer_setCustomDecorationTitleBarHeight = peerClass.getDeclaredMethod( "setCustomDecorationTitleBarHeight", int.class );
			WWindowPeer_setCustomDecorationHitTestSpots = peerClass.getDeclaredMethod( "setCustomDecorationHitTestSpots", List.class );
			WWindowPeer_setCustomDecorationTitleBarHeight.setAccessible( true );
			WWindowPeer_setCustomDecorationHitTestSpots.setAccessible( true );

			Window_hasCustomDecoration = Window.class.getDeclaredMethod( "hasCustomDecoration" );
			Window_setHasCustomDecoration = Window.class.getDeclaredMethod( "setHasCustomDecoration" );
			Window_hasCustomDecoration.setAccessible( true );
			Window_setHasCustomDecoration.setAccessible( true );

			supported = true;
		} catch( Exception ex ) {
			// ignore
		}
	}

	//---- class JBRWindowTopBorder -------------------------------------------

	static class JBRWindowTopBorder
		extends BorderUIResource.EmptyBorderUIResource
	{
		private static JBRWindowTopBorder instance;

		private final Color activeLightColor = new Color( 0x707070 );
		private final Color activeDarkColor = new Color( 0x2D2E2F );
		private final Color inactiveLightColor = new Color( 0xaaaaaa );
		private final Color inactiveDarkColor = new Color( 0x494A4B );

		private boolean colorizationAffectsBorders;
		private Color activeColor;

		static JBRWindowTopBorder getInstance() {
			if( instance == null )
				instance = new JBRWindowTopBorder();
			return instance;
		}

        JBRWindowTopBorder() {
			super( 1, 0, 0, 0 );

			update();
			installListeners();
        }

        void update() {
			colorizationAffectsBorders = isColorizationColorAffectsBorders();
			activeColor = calculateActiveBorderColor();
        }

        void installListeners() {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			toolkit.addPropertyChangeListener( "win.dwm.colorizationColor.affects.borders", e -> {
				colorizationAffectsBorders = isColorizationColorAffectsBorders();
				activeColor = calculateActiveBorderColor();
			} );

			PropertyChangeListener l = e -> {
				activeColor = calculateActiveBorderColor();
			};
			toolkit.addPropertyChangeListener( "win.dwm.colorizationColor", l );
			toolkit.addPropertyChangeListener( "win.dwm.colorizationColorBalance", l );
			toolkit.addPropertyChangeListener( "win.frame.activeBorderColor", l );
		}

        boolean isColorizationColorAffectsBorders() {
			Object value = Toolkit.getDefaultToolkit().getDesktopProperty( "win.dwm.colorizationColor.affects.borders" );
			return (value instanceof Boolean) ? (Boolean) value : true;
		}

        Color getColorizationColor() {
			return (Color) Toolkit.getDefaultToolkit().getDesktopProperty( "win.dwm.colorizationColor" );
        }

        int getColorizationColorBalance() {
			Object value = Toolkit.getDefaultToolkit().getDesktopProperty( "win.dwm.colorizationColorBalance" );
			return (value instanceof Integer) ? (Integer) value : -1;
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
