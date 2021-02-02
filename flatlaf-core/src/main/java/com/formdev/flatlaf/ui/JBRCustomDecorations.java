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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatSystemProperties;
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
	private static boolean initialized;
	private static Method Window_hasCustomDecoration;
	private static Method Window_setHasCustomDecoration;
	private static Method WWindowPeer_setCustomDecorationHitTestSpots;
	private static Method WWindowPeer_setCustomDecorationTitleBarHeight;
	private static Method AWTAccessor_getComponentAccessor;
	private static Method AWTAccessor_ComponentAccessor_getPeer;

	public static boolean isSupported() {
		initialize();
		return Window_setHasCustomDecoration != null;
	}

	static void install( JRootPane rootPane ) {
		if( !isSupported() )
			return;

		// check whether root pane already has a parent, which is the case when switching LaF
		if( rootPane.getParent() != null )
			return;

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
					install( (Window) parent );

				// use invokeLater to remove listener to avoid that listener
				// is removed while listener queue is processed
				EventQueue.invokeLater( () -> {
					rootPane.removeHierarchyListener( this );
				} );
			}
		};
		rootPane.addHierarchyListener( addListener );
	}

	static void install( Window window ) {
		if( !isSupported() )
			return;

		// do not enable JBR decorations if LaF provides decorations
		if( UIManager.getLookAndFeel().getSupportsWindowDecorations() )
			return;

		if( window instanceof JFrame ) {
			JFrame frame = (JFrame) window;

			// do not enable JBR decorations if JFrame should use system window decorations
			// and if not forced to use JBR decorations
			if( !JFrame.isDefaultLookAndFeelDecorated() &&
				!FlatSystemProperties.getBoolean( FlatSystemProperties.USE_JETBRAINS_CUSTOM_DECORATIONS, false ))
			  return;

			// do not enable JBR decorations if frame is undecorated
			if( frame.isUndecorated() )
				return;

			// enable JBR custom window decoration for window
			setHasCustomDecoration( frame );

			// enable Swing window decoration
			frame.getRootPane().setWindowDecorationStyle( JRootPane.FRAME );

		} else if( window instanceof JDialog ) {
			JDialog dialog = (JDialog) window;

			// do not enable JBR decorations if JDialog should use system window decorations
			// and if not forced to use JBR decorations
			if( !JDialog.isDefaultLookAndFeelDecorated() &&
				!FlatSystemProperties.getBoolean( FlatSystemProperties.USE_JETBRAINS_CUSTOM_DECORATIONS, false ))
			  return;

			// do not enable JBR decorations if dialog is undecorated
			if( dialog.isUndecorated() )
				return;

			// enable JBR custom window decoration for window
			setHasCustomDecoration( dialog );

			// enable Swing window decoration
			dialog.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
		}
	}

	static boolean hasCustomDecoration( Window window ) {
		if( !isSupported() )
			return false;

		try {
			return (Boolean) Window_hasCustomDecoration.invoke( window );
		} catch( Exception ex ) {
			Logger.getLogger( FlatLaf.class.getName() ).log( Level.SEVERE, null, ex );
			return false;
		}
	}

	static void setHasCustomDecoration( Window window ) {
		if( !isSupported() )
			return;

		try {
			Window_setHasCustomDecoration.invoke( window );
		} catch( Exception ex ) {
			Logger.getLogger( FlatLaf.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}

	static void setHitTestSpotsAndTitleBarHeight( Window window, List<Rectangle> hitTestSpots, int titleBarHeight ) {
		if( !isSupported() )
			return;

		try {
			Object compAccessor = AWTAccessor_getComponentAccessor.invoke( null );
			Object peer = AWTAccessor_ComponentAccessor_getPeer.invoke( compAccessor, window );
			WWindowPeer_setCustomDecorationHitTestSpots.invoke( peer, hitTestSpots );
			WWindowPeer_setCustomDecorationTitleBarHeight.invoke( peer, titleBarHeight );
		} catch( Exception ex ) {
			Logger.getLogger( FlatLaf.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}

	private static void initialize() {
		if( initialized )
			return;
		initialized = true;

		// requires JetBrains Runtime 11 and Windows 10
		if( !SystemInfo.isJetBrainsJVM_11_orLater || !SystemInfo.isWindows_10_orLater )
			return;

		if( !FlatSystemProperties.getBoolean( FlatSystemProperties.USE_JETBRAINS_CUSTOM_DECORATIONS, true ) )
			return;

		try {
			Class<?> awtAcessorClass = Class.forName( "sun.awt.AWTAccessor" );
			Class<?> compAccessorClass = Class.forName( "sun.awt.AWTAccessor$ComponentAccessor" );
			AWTAccessor_getComponentAccessor = awtAcessorClass.getDeclaredMethod( "getComponentAccessor" );
			AWTAccessor_ComponentAccessor_getPeer = compAccessorClass.getDeclaredMethod( "getPeer", Component.class );

			Class<?> peerClass = Class.forName( "sun.awt.windows.WWindowPeer" );
			WWindowPeer_setCustomDecorationHitTestSpots = peerClass.getDeclaredMethod( "setCustomDecorationHitTestSpots", List.class );
			WWindowPeer_setCustomDecorationTitleBarHeight = peerClass.getDeclaredMethod( "setCustomDecorationTitleBarHeight", int.class );
			WWindowPeer_setCustomDecorationHitTestSpots.setAccessible( true );
			WWindowPeer_setCustomDecorationTitleBarHeight.setAccessible( true );

			Window_hasCustomDecoration = Window.class.getDeclaredMethod( "hasCustomDecoration" );
			Window_setHasCustomDecoration = Window.class.getDeclaredMethod( "setHasCustomDecoration" );
			Window_hasCustomDecoration.setAccessible( true );
			Window_setHasCustomDecoration.setAccessible( true );
		} catch( Exception ex ) {
			// ignore
		}
	}

	//---- class JBRWindowTopBorder -------------------------------------------

	static class JBRWindowTopBorder
		extends BorderUIResource.EmptyBorderUIResource
	{
		private static JBRWindowTopBorder instance;

		private final Color defaultActiveBorder = new Color( 0x707070 );
		private final Color inactiveLightColor = new Color( 0xaaaaaa );

		private boolean colorizationAffectsBorders;
		private Color activeColor = defaultActiveBorder;

		static JBRWindowTopBorder getInstance() {
			if( instance == null )
				instance = new JBRWindowTopBorder();
			return instance;
		}

        private JBRWindowTopBorder() {
			super( 1, 0, 0, 0 );

			colorizationAffectsBorders = calculateAffectsBorders();
			activeColor = calculateActiveBorderColor();

			Toolkit toolkit = Toolkit.getDefaultToolkit();
			toolkit.addPropertyChangeListener( "win.dwm.colorizationColor.affects.borders", e -> {
				colorizationAffectsBorders = calculateAffectsBorders();
				activeColor = calculateActiveBorderColor();
			} );

			PropertyChangeListener l = e -> {
				activeColor = calculateActiveBorderColor();
			};
			toolkit.addPropertyChangeListener( "win.dwm.colorizationColor", l );
			toolkit.addPropertyChangeListener( "win.dwm.colorizationColorBalance", l );
			toolkit.addPropertyChangeListener( "win.frame.activeBorderColor", l );
		}

		private boolean calculateAffectsBorders() {
			Object value = Toolkit.getDefaultToolkit().getDesktopProperty( "win.dwm.colorizationColor.affects.borders" );
			return (value instanceof Boolean) ? (Boolean) value : true;
		}

		private Color calculateActiveBorderColor() {
			if( !colorizationAffectsBorders )
				return defaultActiveBorder;

			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Color colorizationColor = (Color) toolkit.getDesktopProperty( "win.dwm.colorizationColor" );
			if( colorizationColor != null ) {
				Object colorizationColorBalanceObj = toolkit.getDesktopProperty( "win.dwm.colorizationColorBalance" );
				if( colorizationColorBalanceObj instanceof Integer ) {
					int colorizationColorBalance = (Integer) colorizationColorBalanceObj;
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
				return colorizationColor;
			}

			Color activeBorderColor = (Color) toolkit.getDesktopProperty( "win.frame.activeBorderColor" );
			return (activeBorderColor != null) ? activeBorderColor : UIManager.getColor( "MenuBar.borderColor" );
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			Window window = SwingUtilities.windowForComponent( c );
			boolean active = (window != null) ? window.isActive() : false;

			// paint top border
			//  - in light themes
			//  - in dark themes only for active windows if colorization affects borders
			boolean paintTopBorder = !FlatLaf.isLafDark() || (active && colorizationAffectsBorders);
			if( !paintTopBorder )
				return;

			g.setColor( active ? activeColor : inactiveLightColor );
			HiDPIUtils.paintAtScale1x( (Graphics2D) g, x, y, width, height, this::paintImpl );
		}

		private void paintImpl( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
			g.drawRect( x, y, width - 1, 0 );
		}

		void repaintBorder( Component c ) {
			c.repaint( 0, 0, c.getWidth(), 1 );
		}
	}
}
