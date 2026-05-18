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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatTitlePane.TitleBarCaptionHitTest;
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

		if( !options.startsWith( SystemInfo.isMacOS ? "mac" : "win" ) ||
			!c.isDisplayable() ||
			(rootPane = SwingUtilities.getRootPane( c )) == null ||
			(bounds = (Rectangle) rootPane.getClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_BOUNDS )) == null )
		  return new Dimension( 0, 0 );

		if( options.length() > 3 ) {
			if( (options.contains( "leftToRight" ) && !c.getComponentOrientation().isLeftToRight()) ||
				(options.contains( "rightToLeft" ) && c.getComponentOrientation().isLeftToRight()) )
			  return new Dimension( 0, 0 );
		}

		// On macOS, the client property is updated very late when toggling full screen,
		// which results in "jumping" layout after full screen toggle finished.
		// To avoid that, get up-to-date buttons bounds from macOS.
		if( SystemInfo.isMacFullWindowContentSupported && FlatNativeMacLibrary.isLoaded() ) {
			Rectangle r = FlatNativeMacLibrary.getWindowButtonsBounds( SwingUtilities.windowForComponent( c ) );
			if( r != null )
				bounds = r;
		}

		int width = bounds.width;
		int height = bounds.height;

		if( options.length() > 3 ) {
			if( width == 0 && options.contains( "zeroInFullScreen" ) )
				height = 0;

			if( options.contains( "horizontal" ) )
				height = 0;
			if( options.contains( "vertical" ) )
				width = 0;
		}

		return new Dimension( width, height );
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
		if( !SystemInfo.isMacFullWindowContentSupported || !rootPane.isDisplayable() )
			return;

		Rectangle bounds = null;
		if( FlatClientProperties.clientPropertyBoolean( rootPane, "apple.awt.fullWindowContent", false ) ) {
			bounds = FlatNativeMacLibrary.isLoaded()
				? FlatNativeMacLibrary.getWindowButtonsBounds( SwingUtilities.windowForComponent( rootPane ) )
				: new Rectangle( 68, 28 ); // default size
		}
		rootPane.putClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_BOUNDS, bounds );
	}

	static void macUninstallFullWindowContentButtonsBoundsProperty( JRootPane rootPane ) {
		if( !SystemInfo.isMacFullWindowContentSupported )
			return;

		rootPane.putClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_BOUNDS, null );
	}

	static void debugPaint( Graphics g, JComponent c ) {
		if( !UIManager.getBoolean( KEY_DEBUG_SHOW_PLACEHOLDERS ) )
			return;

		int width = c.getWidth();
		int height = c.getHeight();
		if( width <= 0 || height <= 0 )
			return;

		// draw red figure
		g.setColor( Color.red );
		debugPaintRect( g, new Rectangle( width, height ) );

		// draw magenta figure if buttons bounds are not equal to placeholder bounds
		JRootPane rootPane;
		Rectangle bounds;
		if( (rootPane = SwingUtilities.getRootPane( c )) != null &&
			(bounds = (Rectangle) rootPane.getClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_BOUNDS )) != null &&
			(bounds.width != width || bounds.height != height) )
		{
			g.setColor( Color.magenta );
			debugPaintRect( g, SwingUtilities.convertRectangle( rootPane, bounds, c ) );
		}
	}

	private static void debugPaintRect( Graphics g, Rectangle r ) {
		// draw rectangle
		g.drawRect( r.x, r.y, r.width - 1, r.height - 1 );

		// draw diagonal cross
		int x2 = r.x + r.width - 1;
		int y2 = r.y + r.height - 1;
		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
		g.drawLine( r.x, r.y, x2, y2 );
		g.drawLine( r.x, y2, x2, r.y );
		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}

	/**
	 * Returns whether there is a component at the given location that processes
	 * mouse events; checks {@link FlatClientProperties#COMPONENT_TITLE_BAR_CAPTION}
	 * and {@link TitleBarCaptionHitTest} along the way.
	 * <p>
	 * Used by {@link FlatTitlePane} (Windows native window decorations).
	 *
	 * @param skip optional component that is skipped during traversal
	 *             (used to skip {@code FlatTitlePane.mouseLayer})
	 */
	static boolean isTitleBarCaptionAt( Component c, int x, int y, Component skip ) {
		if( !c.isDisplayable() || !c.isVisible() || !contains( c, x, y ) || c == skip )
			return true; // continue checking with next component

		// check enabled component that has mouse listeners
		if( c.isEnabled() &&
			(c.getMouseListeners().length > 0 ||
			 c.getMouseMotionListeners().length > 0) )
		{
			if( !(c instanceof JComponent) )
				return false; // assume that this is not a caption because the component has mouse listeners

			// check client property boolean value
			Object caption = ((JComponent)c).getClientProperty( FlatClientProperties.COMPONENT_TITLE_BAR_CAPTION );
			if( caption instanceof Boolean )
				return (boolean) caption;

			// if component is not fully layouted, do not invoke function
			// because it is too dangerous that the function tries to layout the component,
			// which could cause a dead lock
			if( !c.isValid() ) {
				// revalidate if necessary so that it is valid when invoked again later
				EventQueue.invokeLater( () -> {
					Window w = SwingUtilities.windowForComponent( c );
					if( w != null )
						w.revalidate();
					else
						c.revalidate();
				} );

				return false; // assume that this is not a caption because the component has mouse listeners
			}

			if( caption instanceof Function ) {
				// check client property function value
				@SuppressWarnings( "unchecked" )
				Function<Point, Boolean> hitTest = (Function<Point, Boolean>) caption;
				Boolean result = hitTest.apply( new Point( x, y ) );
				if( result != null )
					return result;
			} else {
				// check component UI
				ComponentUI ui = JavaCompatibility2.getUI( (JComponent) c );
				if( !(ui instanceof TitleBarCaptionHitTest) )
					return false; // assume that this is not a caption because the component has mouse listeners

				Boolean result = ((TitleBarCaptionHitTest)ui).isTitleBarCaptionAt( x, y );
				if( result != null )
					return result;
			}

			// else continue checking children
		}

		// check children
		if( c instanceof Container ) {
			for( Component child : ((Container)c).getComponents() ) {
				if( !isTitleBarCaptionAt( child, x - child.getX(), y - child.getY(), skip ) )
					return false;
			}
		}
		return true;
	}

	/**
	 * Same as {@link Component#contains(int, int)}, but not using that method
	 * because it may be overridden by custom components and invoke code that
	 * tries to request AWT tree lock on 'AWT-Windows' thread.
	 * This could freeze the application if AWT tree is already locked on 'AWT-EventQueue' thread.
	 */
	private static boolean contains( Component c, int x, int y ) {
		return x >= 0 && y >= 0 && x < c.getWidth() && y < c.getHeight();
	}

	//---- macOS title bar caption support ------------------------------------
	//
	// On macOS in fullWindowContent mode, the title bar area is transparent and
	// Swing content extends into it. To make components marked with
	// JComponent.titleBarCaption act as window-draggable caption (even when they
	// have mouse listeners), we register a per-window callback with the native
	// library. The native side (MacTitleBarCaption.mm) intercepts left mouse
	// down events in the title bar area before AWT dispatch and, on caption
	// points, hands off to -[NSWindow performWindowDragWithEvent:].

	static void macInstallTitleBarCaption( JRootPane rootPane ) {
		if( !SystemInfo.isMacFullWindowContentSupported || !FlatNativeMacLibrary.isLoaded() )
			return;

		Window window = SwingUtilities.getWindowAncestor( rootPane );
		if( window == null || !window.isDisplayable() )
			return;

		FlatNativeMacLibrary.setupFullWindowContentTitleBarCaption( window,
			new MacTitleBarCaptionCallback( rootPane ) );
	}

	static void macUninstallTitleBarCaption( JRootPane rootPane ) {
		if( !SystemInfo.isMacFullWindowContentSupported || !FlatNativeMacLibrary.isLoaded() )
			return;

		Window window = SwingUtilities.getWindowAncestor( rootPane );
		if( window == null || !window.isDisplayable() )
			return;

		FlatNativeMacLibrary.removeFullWindowContentTitleBarCaption( window );
	}

	private static class MacTitleBarCaptionCallback
		implements FlatNativeMacLibrary.FullWindowContentTitleBarCaptionCallback
	{
		private final WeakReference<JRootPane> rootPaneRef;

		MacTitleBarCaptionCallback( JRootPane rootPane ) {
			this.rootPaneRef = new WeakReference<>( rootPane );
		}

		/**
		 * Invoked on the AppKit main thread (not the AWT event dispatching
		 * thread), before the mouse event is dispatched to AWT.
		 * Must return quickly and must not change any component property or
		 * layout because this could cause a dead lock.
		 */
		@Override
		public boolean isTitleBarCaptionAt( int x, int y ) {
			JRootPane rootPane = rootPaneRef.get();
			if( rootPane == null || !rootPane.isShowing() )
				return false;

			// bail out quickly if click is below the title bar
			Rectangle buttonsBounds = (Rectangle) rootPane.getClientProperty(
				FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_BOUNDS );
			int titleBarHeight = (buttonsBounds != null && buttonsBounds.height > 0)
				? buttonsBounds.height
				: 28; // default size, matches macUpdateFullWindowContentButtonsBoundsProperty()

			// convert from AWT window coordinates to layeredPane coordinates
			// by walking the parent chain; not using SwingUtilities.convertPoint()
			// because that calls Component.getLocationOnScreen(), which acquires
			// the AWT tree lock and could cause a dead lock here
			JLayeredPane layeredPane = rootPane.getLayeredPane();
			int dx = 0, dy = 0;
			for( Component c = layeredPane; c != null && !(c instanceof Window); c = c.getParent() ) {
				dx += c.getX();
				dy += c.getY();
			}
			int lx = x - dx;
			int ly = y - dy;
			if( ly < 0 || ly >= titleBarHeight )
				return false;

			return FullWindowContentSupport.isTitleBarCaptionAt( layeredPane, lx, ly, null );
		}
	}
}
