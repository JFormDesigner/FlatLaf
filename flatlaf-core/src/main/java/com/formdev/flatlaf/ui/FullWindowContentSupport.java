/*
 * Copyright 2024 FormDev Software GmbH
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * @author Karl Tauber
 */
class FullWindowContentSupport
{
	private static final String KEY_DEBUG_SHOW_PLACEHOLDERS = "FlatLaf.debug.panel.showPlaceholders";

	private static ArrayList<WeakReference<JComponent>> placeholders = new ArrayList<>();

	static Dimension getPlaceholderPreferredSize( JComponent c, String options ) {
		JRootPane rootPane;
		Rectangle bounds;

		if( options.startsWith( SystemInfo.isMacOS ? "mac" : "win" ) &&
			c.isDisplayable() &&
			(rootPane = SwingUtilities.getRootPane( c )) != null &&
			(bounds = (Rectangle) rootPane.getClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_BOUNDS )) != null )
		{
			// On macOS, the client property is updated very late when toggling full screen,
			// which results in "jumping" layout after full screen toggle finished.
			// To avoid that, get up-to-date buttons bounds from macOS.
			if( SystemInfo.isMacFullWindowContentSupported && FlatNativeMacLibrary.isLoaded() ) {
				Rectangle r = FlatNativeMacLibrary.getWindowButtonsBounds( SwingUtilities.windowForComponent( c ) );
				if( r != null )
					bounds = r;
			}

			if( options.length() > 3 ) {
				if( options.contains( "horizontal" ) )
					return new Dimension( bounds.width, 0 );
				if( options.contains( "vertical" ) )
					return new Dimension( 0, bounds.height );
			}

			return bounds.getSize();
		}

		// default to 0,0
		return new Dimension();
	}

	static void registerPlaceholder( JComponent c ) {
		synchronized( placeholders ) {
			if( indexOfPlaceholder( c ) < 0 )
				placeholders.add( new WeakReference<>( c ) );
		}
	}

	static void unregisterPlaceholder( JComponent c ) {
		synchronized( placeholders ) {
			int index = indexOfPlaceholder( c );
			if( index >= 0 )
				placeholders.remove( index );
		}
	}

	private static int indexOfPlaceholder( JComponent c ) {
		int size = placeholders.size();
		for( int i = 0; i < size; i++ ) {
			if( placeholders.get( i ).get() == c )
				return i;
		}
		return -1;
	}

	static void revalidatePlaceholders( Component container ) {
		synchronized( placeholders ) {
			if( placeholders.isEmpty() )
				return;

			for( Iterator<WeakReference<JComponent>> it = placeholders.iterator(); it.hasNext(); ) {
				WeakReference<JComponent> ref = it.next();
				JComponent c = ref.get();

				// remove already released placeholder
				if( c == null ) {
					it.remove();
					continue;
				}

				// revalidate placeholder if is in given container
				if( SwingUtilities.isDescendingFrom( c, container ) )
					c.revalidate();
			}
		}
	}

	static ComponentListener macInstallListeners( JRootPane rootPane ) {
		ComponentListener l = new ComponentAdapter() {
			boolean lastFullScreen;

			@Override
			public void componentResized( ComponentEvent e ) {
				Window window = SwingUtilities.windowForComponent( rootPane );
				if( window == null )
					return;

				boolean fullScreen = FlatNativeMacLibrary.isLoaded() && FlatNativeMacLibrary.isWindowFullScreen( window );
				if( fullScreen == lastFullScreen )
					return;

				lastFullScreen = fullScreen;
				macUpdateFullWindowContentButtonsBoundsProperty( rootPane );
			}
		};

		rootPane.addComponentListener( l );
		return l;
	}

	static void macUninstallListeners( JRootPane rootPane, ComponentListener l ) {
		if( l != null )
			rootPane.removeComponentListener( l );
	}

	static void macUpdateFullWindowContentButtonsBoundsProperty( JRootPane rootPane ) {
		if( !SystemInfo.isMacFullWindowContentSupported ||
			!rootPane.isDisplayable() ||
			!FlatClientProperties.clientPropertyBoolean( rootPane, "apple.awt.fullWindowContent", false ) )
		  return;

		Rectangle bounds = FlatNativeMacLibrary.isLoaded()
			? FlatNativeMacLibrary.getWindowButtonsBounds( SwingUtilities.windowForComponent( rootPane ) )
			: new Rectangle( 68, 28 ); // default size
		rootPane.putClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_BOUNDS, bounds );
	}

	static void debugPaint( Graphics g, JComponent c ) {
		if( !UIManager.getBoolean( KEY_DEBUG_SHOW_PLACEHOLDERS ) )
			return;

		int width = c.getWidth() - 1;
		int height = c.getHeight() - 1;
		if( width <= 0 || height <= 0 )
			return;

		g.setColor( Color.red );
		g.drawRect( 0, 0, width, height );

		// draw diagonal cross
		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
		g.drawLine( 0, 0, width, height );
		g.drawLine( 0, height, width, 0 );
		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}
}
