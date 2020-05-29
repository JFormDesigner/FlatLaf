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

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLaf;
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
		boolean frameIsDefaultLookAndFeelDecorated = JFrame.isDefaultLookAndFeelDecorated();
		boolean dialogIsDefaultLookAndFeelDecorated = JDialog.isDefaultLookAndFeelDecorated();
		boolean lafSupportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();

		// check whether decorations are enabled
		if( !frameIsDefaultLookAndFeelDecorated && !dialogIsDefaultLookAndFeelDecorated )
			return;

		// do not enable JBR decorations if JFrame and JDialog will use LaF decorations
		if( lafSupportsWindowDecorations &&
			frameIsDefaultLookAndFeelDecorated &&
			dialogIsDefaultLookAndFeelDecorated )
		  return;

		if( !isSupported() )
			return;

		// use hierarchy listener to wait until the root pane is added to a window
		HierarchyListener addListener = new HierarchyListener() {
			@Override
			public void hierarchyChanged( HierarchyEvent e ) {
				if( (e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) == 0 )
					return;

				Container parent = e.getChangedParent();
				if( parent instanceof JFrame ) {
					JFrame frame = (JFrame) parent;

					// do not enable JBR decorations if JFrame will use LaF decorations
					if( lafSupportsWindowDecorations && frameIsDefaultLookAndFeelDecorated )
						return;

					// do not enable JBR decorations if frame is undecorated
					if( frame.isUndecorated() )
						return;

					// enable JBR custom window decoration for window
					setHasCustomDecoration( frame );

					// enable Swing window decoration
					rootPane.setWindowDecorationStyle( JRootPane.FRAME );

				} else if( parent instanceof JDialog ) {
					JDialog dialog = (JDialog)parent;

					// do not enable JBR decorations if JDialog will use LaF decorations
					if( lafSupportsWindowDecorations && dialogIsDefaultLookAndFeelDecorated )
						return;

					// do not enable JBR decorations if dialog is undecorated
					if( dialog.isUndecorated() )
						return;

					// enable JBR custom window decoration for window
					setHasCustomDecoration( dialog );

					// enable Swing window decoration
					rootPane.setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
				}

				// use invokeLater to remove listener to avoid that listener
				// is removed while listener queue is processed
				EventQueue.invokeLater( () -> {
					rootPane.removeHierarchyListener( this );
				} );
			}
		};
		rootPane.addHierarchyListener( addListener );
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
		if( !SystemInfo.IS_JETBRAINS_JVM_11_OR_LATER || !SystemInfo.IS_WINDOWS_10_OR_LATER )
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
}
