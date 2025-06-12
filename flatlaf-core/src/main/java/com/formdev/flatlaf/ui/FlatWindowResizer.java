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

import static java.awt.Cursor.*;
import static javax.swing.SwingConstants.*;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Supplier;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * Resizes frames, dialogs or internal frames.
 * <p>
 * Could also be used to implement resize support for any Swing component
 * by creating a new subclass.
 *
 * @author Karl Tauber
 */
public abstract class FlatWindowResizer
	implements PropertyChangeListener, ComponentListener
{
	protected final JComponent resizeComp;

	protected final int borderDragThickness = FlatUIUtils.getUIInt( "RootPane.borderDragThickness", 5 );
	protected final int cornerDragWidth = FlatUIUtils.getUIInt( "RootPane.cornerDragWidth", 16 );
	protected final boolean honorFrameMinimumSizeOnResize = UIManager.getBoolean( "RootPane.honorFrameMinimumSizeOnResize" );
	protected final boolean honorDialogMinimumSizeOnResize = UIManager.getBoolean( "RootPane.honorDialogMinimumSizeOnResize" );

	protected final DragBorderComponent topDragComp;
	protected final DragBorderComponent bottomDragComp;
	protected final DragBorderComponent leftDragComp;
	protected final DragBorderComponent rightDragComp;

	protected FlatWindowResizer( JComponent resizeComp ) {
		this.resizeComp = resizeComp;

		topDragComp = createDragBorderComponent( NW_RESIZE_CURSOR, N_RESIZE_CURSOR, NE_RESIZE_CURSOR );
		bottomDragComp = createDragBorderComponent( SW_RESIZE_CURSOR, S_RESIZE_CURSOR, SE_RESIZE_CURSOR );
		leftDragComp = createDragBorderComponent( NW_RESIZE_CURSOR, W_RESIZE_CURSOR, SW_RESIZE_CURSOR );
		rightDragComp = createDragBorderComponent( NE_RESIZE_CURSOR, E_RESIZE_CURSOR, SE_RESIZE_CURSOR );

		// for rootpanes, add after glasspane
		int insertIndex = (resizeComp instanceof JRootPane) ? 1 : 0;
		resizeComp.add( topDragComp,    insertIndex++ );
		resizeComp.add( bottomDragComp, insertIndex++ );
		resizeComp.add( leftDragComp,   insertIndex++ );
		resizeComp.add( rightDragComp,  insertIndex++ );

		resizeComp.addComponentListener( this );
		resizeComp.addPropertyChangeListener( "ancestor", this );

		if( resizeComp.isDisplayable() )
			addNotify();
	}

	protected DragBorderComponent createDragBorderComponent( int leadingResizeDir, int centerResizeDir, int trailingResizeDir ) {
		return new DragBorderComponent( leadingResizeDir, centerResizeDir, trailingResizeDir );
	}

	public void uninstall() {
		removeNotify();

		resizeComp.removeComponentListener( this );
		resizeComp.removePropertyChangeListener( "ancestor", this );

		resizeComp.remove( topDragComp );
		resizeComp.remove( bottomDragComp );
		resizeComp.remove( leftDragComp );
		resizeComp.remove( rightDragComp );
	}

	public void doLayout() {
		if( !topDragComp.isVisible() )
			return;

		int x = 0;
		int y = 0;
		int width = resizeComp.getWidth();
		int height = resizeComp.getHeight();
		if( width <= 0 || height <= 0 )
			return;

		Insets resizeInsets = getResizeInsets();
		int thickness = UIScale.scale( borderDragThickness );
		int topThickness = Math.max( resizeInsets.top, thickness );
		int bottomThickness = Math.max( resizeInsets.bottom, thickness );
		int leftThickness = Math.max( resizeInsets.left, thickness );
		int rightThickness = Math.max( resizeInsets.right, thickness );
		int y2 = y + topThickness;
		int height2 = height - topThickness - bottomThickness;

		// set bounds of drag components
		topDragComp.setBounds( x, y, width, topThickness );
		bottomDragComp.setBounds( x, y + height - bottomThickness, width, bottomThickness );
		leftDragComp.setBounds( x, y2, leftThickness, height2 );
		rightDragComp.setBounds( x + width - rightThickness, y2, rightThickness, height2 );

		// set corner drag widths
		int cornerDelta = UIScale.scale( cornerDragWidth - borderDragThickness );
		topDragComp.setCornerDragWidths( leftThickness + cornerDelta, rightThickness + cornerDelta );
		bottomDragComp.setCornerDragWidths( leftThickness + cornerDelta, rightThickness + cornerDelta );
		leftDragComp.setCornerDragWidths( cornerDelta, cornerDelta );
		rightDragComp.setCornerDragWidths( cornerDelta, cornerDelta );
	}

	protected Insets getResizeInsets() {
		return new Insets( 0, 0, 0, 0 );
	}

	protected void addNotify() {
		updateVisibility();
	}

	protected void removeNotify() {
		updateVisibility();
	}

	protected void updateVisibility() {
		boolean visible = isWindowResizable();
		if( visible == topDragComp.isVisible() )
			return;

		topDragComp.setVisible( visible );
		bottomDragComp.setVisible( visible );
		leftDragComp.setVisible( visible );

		// The east component is not hidden, instead its bounds are set to 0,0,1,1 and
		// it is disabled. This is necessary so that DragBorderComponent.paintComponent() is invoked.
		rightDragComp.setEnabled( visible );
		if( visible ) {
			rightDragComp.setVisible( true ); // necessary because it is initially invisible
			doLayout();
		} else
			rightDragComp.setBounds( 0, 0, 1, 1 );
	}

	boolean isDialog() {
		return false;
	}

	protected abstract boolean isWindowResizable();
	protected abstract Rectangle getWindowBounds();
	protected abstract void setWindowBounds( Rectangle r );
	protected abstract boolean limitToParentBounds();
	protected abstract Rectangle getParentBounds();
	protected abstract boolean honorMinimumSizeOnResize();
	protected abstract boolean honorMaximumSizeOnResize();
	protected abstract Dimension getWindowMinimumSize();
	protected abstract Dimension getWindowMaximumSize();

	protected void beginResizing( int resizeDir, MouseEvent e ) {}
	protected void endResizing() {}

	//---- interface PropertyChangeListener ----

	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case "ancestor":
				if( e.getNewValue() != null )
					addNotify();
				else
					removeNotify();
				break;

			case "resizable":
				updateVisibility();
				break;
		}
	}

	//---- interface ComponentListener ----

	@Override
	public void componentResized( ComponentEvent e ) {
		doLayout();
	}

	@Override public void componentMoved( ComponentEvent e ) {}
	@Override public void componentShown( ComponentEvent e ) {}
	@Override public void componentHidden( ComponentEvent e ) {}

	//---- class WindowResizer ------------------------------------------------

	/**
	 * Resizes frames and dialogs.
	 */
	public static class WindowResizer
		extends FlatWindowResizer
		implements WindowStateListener
	{
		protected Window window;

		private final JComponent centerComp;
		private final boolean limitResizeToScreenBounds;

		public WindowResizer( JRootPane rootPane ) {
			super( rootPane );

			// Transparent "center" component that is made visible only while resizing window.
			// It uses same cursor as the area where resize dragging started.
			// This ensures that the cursor shape stays stable while dragging mouse
			// into the window to make window smaller. Otherwise it would toggling between
			// resize and standard cursor because the component layout is not updated
			// fast enough and the mouse cursor is always updated from the component
			// at the mouse location.
			centerComp = new JPanel();
			centerComp.setOpaque( false );
			centerComp.setVisible( false );
			rootPane.add( centerComp, 5 );

			// On Linux, limit window resizing to screen bounds because otherwise
			// there would be a strange effect when the mouse is moved over a sidebar
			// while resizing and the opposite window side is also resized.
			limitResizeToScreenBounds = SystemInfo.isLinux;
		}

		@Override
		public void uninstall() {
			resizeComp.remove( centerComp );

			super.uninstall();
		}

		@Override
		public void doLayout() {
			super.doLayout();

			if( centerComp != null && centerComp.isVisible() )
				centerComp.setBounds( 0, 0, resizeComp.getWidth(), resizeComp.getHeight() );
		}

		@Override
		protected void addNotify() {
			Container parent = resizeComp.getParent();
			window = (parent instanceof Window) ? (Window) parent : null;
			if( window instanceof Frame ) {
				window.addPropertyChangeListener( "resizable", this );
				window.addWindowStateListener( this );
			}

			super.addNotify();
		}

		@Override
		protected void removeNotify() {
			if( window instanceof Frame ) {
				window.removePropertyChangeListener( "resizable", this );
				window.removeWindowStateListener( this );
			}
			window = null;

			super.removeNotify();
		}

		@Override
		protected boolean isWindowResizable() {
			if( FlatUIUtils.isFullScreen( resizeComp ) )
				return false;
			if( window instanceof Frame )
				return ((Frame)window).isResizable() && (((Frame)window).getExtendedState() & Frame.MAXIMIZED_BOTH) == 0;
			if( window instanceof Dialog )
				return ((Dialog)window).isResizable();
			return false;
		}

		@Override
		protected Rectangle getWindowBounds() {
			return window.getBounds();
		}

		@Override
		protected void setWindowBounds( Rectangle r ) {
			window.setBounds( r );

			// immediately layout drag border components
			doLayout();

			if( Toolkit.getDefaultToolkit().isDynamicLayoutActive() ) {
				window.validate();
				resizeComp.repaint();
			}
		}

		@Override
		protected boolean limitToParentBounds() {
			return limitResizeToScreenBounds && window != null && window.getGraphicsConfiguration() != null;
		}

		@Override
		protected Rectangle getParentBounds() {
			GraphicsConfiguration gc = window.getGraphicsConfiguration();
			Rectangle bounds = gc.getBounds();
			Insets insets = FlatUIUtils.getScreenInsets( gc );
			return new Rectangle( bounds.x + insets.left, bounds.y + insets.top,
				bounds.width - insets.left - insets.right,
				bounds.height - insets.top - insets.bottom );
		}

		@Override
		protected boolean honorMinimumSizeOnResize() {
			return
				(honorFrameMinimumSizeOnResize && window instanceof Frame) ||
				(honorDialogMinimumSizeOnResize && window instanceof Dialog);
		}

		@Override
		protected boolean honorMaximumSizeOnResize() {
			return false;
		}

		@Override
		protected Dimension getWindowMinimumSize() {
			return window.getMinimumSize();
		}

		@Override
		protected Dimension getWindowMaximumSize() {
			return window.getMaximumSize();
		}

		@Override
		boolean isDialog() {
			return window instanceof Dialog;
		}

		@Override
		public void windowStateChanged( WindowEvent e ) {
			updateVisibility();
		}

		@Override
		protected void beginResizing( int resizeDir, MouseEvent e ) {
			// on Linux, resize window using window manager
			if( SystemInfo.isLinux && window != null && FlatNativeLinuxLibrary.isWMUtilsSupported( window ) ) {
				int direction = -1;
				switch( resizeDir ) {
					case N_RESIZE_CURSOR:	direction = FlatNativeLinuxLibrary.SIZE_TOP; break;
					case S_RESIZE_CURSOR:	direction = FlatNativeLinuxLibrary.SIZE_BOTTOM; break;
					case W_RESIZE_CURSOR:	direction = FlatNativeLinuxLibrary.SIZE_LEFT; break;
					case E_RESIZE_CURSOR:	direction = FlatNativeLinuxLibrary.SIZE_RIGHT; break;
					case NW_RESIZE_CURSOR:	direction = FlatNativeLinuxLibrary.SIZE_TOPLEFT; break;
					case NE_RESIZE_CURSOR:	direction = FlatNativeLinuxLibrary.SIZE_TOPRIGHT; break;
					case SW_RESIZE_CURSOR:	direction = FlatNativeLinuxLibrary.SIZE_BOTTOMLEFT; break;
					case SE_RESIZE_CURSOR:	direction = FlatNativeLinuxLibrary.SIZE_BOTTOMRIGHT; break;
				}

				if( direction >= 0 && FlatNativeLinuxLibrary.moveOrResizeWindow( window, e, direction ) )
					return;
			}

			centerComp.setBounds( 0, 0, resizeComp.getWidth(), resizeComp.getHeight() );
			centerComp.setCursor( getPredefinedCursor( resizeDir ) );
			centerComp.setVisible( true );
		}

		@Override
		protected void endResizing() {
			centerComp.setVisible( false );
			centerComp.setCursor( null );
		}
	}

	//---- class InternalFrameResizer -----------------------------------------

	/**
	 * Resizes internal frames.
	 */
	public static class InternalFrameResizer
		extends FlatWindowResizer
	{
		protected final Supplier<DesktopManager> desktopManager;

		public InternalFrameResizer( JInternalFrame frame, Supplier<DesktopManager> desktopManager ) {
			super( frame );
			this.desktopManager = desktopManager;

			frame.addPropertyChangeListener( "resizable", this );
		}

		@Override
		public void uninstall() {
			getFrame().removePropertyChangeListener( "resizable", this );

			super.uninstall();
		}

		private JInternalFrame getFrame() {
			return (JInternalFrame) resizeComp;
		}

		@Override
		protected Insets getResizeInsets() {
			return getFrame().getInsets();
		}

		@Override
		protected boolean isWindowResizable() {
			return getFrame().isResizable();
		}

		@Override
		protected Rectangle getWindowBounds() {
			return getFrame().getBounds();
		}

		@Override
		protected void setWindowBounds( Rectangle r ) {
			desktopManager.get().resizeFrame( getFrame(), r.x, r.y, r.width, r.height );
		}

		@Override
		protected boolean limitToParentBounds() {
			return true;
		}

		@Override
		protected Rectangle getParentBounds() {
			return new Rectangle( getFrame().getParent().getSize() );
		}

		@Override
		protected boolean honorMinimumSizeOnResize() {
			return true;
		}

		@Override
		protected boolean honorMaximumSizeOnResize() {
			return true;
		}

		@Override
		protected Dimension getWindowMinimumSize() {
			return getFrame().getMinimumSize();
		}

		@Override
		protected Dimension getWindowMaximumSize() {
			return getFrame().getMaximumSize();
		}

		@Override
		protected void beginResizing( int resizeDir, MouseEvent e ) {
			int direction = 0;
			switch( resizeDir ) {
				case N_RESIZE_CURSOR:	direction = NORTH; break;
				case S_RESIZE_CURSOR:	direction = SOUTH; break;
				case W_RESIZE_CURSOR:	direction = WEST; break;
				case E_RESIZE_CURSOR:	direction = EAST; break;
				case NW_RESIZE_CURSOR:	direction = NORTH_WEST; break;
				case NE_RESIZE_CURSOR:	direction = NORTH_EAST; break;
				case SW_RESIZE_CURSOR:	direction = SOUTH_WEST; break;
				case SE_RESIZE_CURSOR:	direction = SOUTH_EAST; break;
			}
			desktopManager.get().beginResizingFrame( getFrame(), direction );
		}

		@Override
		protected void endResizing() {
			desktopManager.get().endResizingFrame( getFrame() );
		}
	}

	//---- class DragBorderComponent ------------------------------------------

	protected class DragBorderComponent
		extends JComponent
		implements MouseListener, MouseMotionListener
	{
		private final int leadingResizeDir;
		private final int centerResizeDir;
		private final int trailingResizeDir;

		private int resizeDir = -1;

		private int leadingCornerDragWidth;
		private int trailingCornerDragWidth;

		// offsets of mouse position to window edges
		private int dragLeftOffset;
		private int dragRightOffset;
		private int dragTopOffset;
		private int dragBottomOffset;

		protected DragBorderComponent( int leadingResizeDir, int centerResizeDir, int trailingResizeDir ) {
			this.leadingResizeDir = leadingResizeDir;
			this.centerResizeDir = centerResizeDir;
			this.trailingResizeDir = trailingResizeDir;

			setResizeDir( centerResizeDir );
			setVisible( false );

			addMouseListener( this );
			addMouseMotionListener( this );
		}

		void setCornerDragWidths( int leading, int trailing ) {
			leadingCornerDragWidth = leading;
			trailingCornerDragWidth = trailing;
		}

		protected void setResizeDir( int resizeDir ) {
			if( this.resizeDir == resizeDir )
				return;
			this.resizeDir = resizeDir;

			setCursor( getPredefinedCursor( resizeDir ) );
		}

		@Override
		public Dimension getPreferredSize() {
			int thickness = UIScale.scale( borderDragThickness );
			return new Dimension( thickness, thickness );
		}

		@Override
		protected void paintComponent( Graphics g ) {
			super.paintChildren( g );

			// for dialogs: necessary because Dialog.setResizable() does not fire events
			// for frames: necessary because GraphicsDevice.setFullScreenWindow() does not fire events
			updateVisibility();

/*debug
			int width = getWidth();
			int height = getHeight();

			g.setColor( java.awt.Color.blue );
			boolean topOrBottom = (centerResizeDir == N_RESIZE_CURSOR || centerResizeDir == S_RESIZE_CURSOR);
			if( topOrBottom ) {
				g.drawLine( leadingCornerDragWidth, 0, leadingCornerDragWidth, height );
				g.drawLine( width - trailingCornerDragWidth, 0, width - trailingCornerDragWidth, height );
			} else {
				g.drawLine( 0, leadingCornerDragWidth, width, leadingCornerDragWidth );
				g.drawLine( 0, height - trailingCornerDragWidth, width, height - trailingCornerDragWidth );
			}

			g.setColor( java.awt.Color.red );
			g.drawRect( 0, 0, width - 1, height - 1 );
debug*/
		}

		@Override
		public void mouseClicked( MouseEvent e ) {
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			if( !SwingUtilities.isLeftMouseButton( e ) || !isWindowResizable() )
				return;

			int xOnScreen = e.getXOnScreen();
			int yOnScreen = e.getYOnScreen();
			Rectangle windowBounds = getWindowBounds();

			// compute offsets of mouse position to window edges
			dragLeftOffset = xOnScreen - windowBounds.x;
			dragTopOffset = yOnScreen - windowBounds.y;
			dragRightOffset = windowBounds.x + windowBounds.width - xOnScreen;
			dragBottomOffset = windowBounds.y + windowBounds.height - yOnScreen;

			beginResizing( resizeDir, e );
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			if( !SwingUtilities.isLeftMouseButton( e ) || !isWindowResizable() )
				return;

			dragLeftOffset = dragRightOffset = dragTopOffset = dragBottomOffset = 0;

			endResizing();
		}

		@Override public void mouseEntered( MouseEvent e ) {}
		@Override public void mouseExited( MouseEvent e ) {}

		@Override
		public void mouseMoved( MouseEvent e ) {
			boolean topOrBottom = (centerResizeDir == N_RESIZE_CURSOR || centerResizeDir == S_RESIZE_CURSOR);
			int xy = topOrBottom ? e.getX() : e.getY();
			int wh = topOrBottom ? getWidth() : getHeight();

			setResizeDir( xy <= leadingCornerDragWidth
				? leadingResizeDir
				: (xy >= wh - trailingCornerDragWidth
					? trailingResizeDir
					: centerResizeDir) );
		}

		@Override
		public void mouseDragged( MouseEvent e ) {
			if( !SwingUtilities.isLeftMouseButton( e ) || !isWindowResizable() )
				return;

			int xOnScreen = e.getXOnScreen();
			int yOnScreen = e.getYOnScreen();

			// Get current window bounds and compute new bounds based on them.
			// This is necessary because window manager may alter window bounds while resizing.
			// E.g. when having two monitors with different scale factors and resizing
			// a window on first screen to the second screen, then the window manager may
			// decide at some point that the window should be only on second screen
			// and adjusts its bounds.
			Rectangle oldBounds = getWindowBounds();
			Rectangle newBounds = new Rectangle( oldBounds );

			// compute new window bounds

			// top
			if( resizeDir == N_RESIZE_CURSOR || resizeDir == NW_RESIZE_CURSOR || resizeDir == NE_RESIZE_CURSOR ) {
				newBounds.y = yOnScreen - dragTopOffset;
				if( limitToParentBounds() )
					newBounds.y = Math.max( newBounds.y, getParentBounds().y );
				newBounds.height += (oldBounds.y - newBounds.y);
			}

			// bottom
			if( resizeDir == S_RESIZE_CURSOR || resizeDir == SW_RESIZE_CURSOR || resizeDir == SE_RESIZE_CURSOR ) {
				newBounds.height = (yOnScreen + dragBottomOffset) - newBounds.y;
				if( limitToParentBounds() ) {
					Rectangle parentBounds = getParentBounds();
					int parentBottomY = parentBounds.y + parentBounds.height;
					if( newBounds.y + newBounds.height > parentBottomY )
						newBounds.height = parentBottomY - newBounds.y;
				}
			}

			// left
			if( resizeDir == W_RESIZE_CURSOR || resizeDir == NW_RESIZE_CURSOR || resizeDir == SW_RESIZE_CURSOR ) {
				newBounds.x = xOnScreen - dragLeftOffset;
				if( limitToParentBounds() )
					newBounds.x = Math.max( newBounds.x, getParentBounds().x );
				newBounds.width += (oldBounds.x - newBounds.x);
			}

			// right
			if( resizeDir == E_RESIZE_CURSOR || resizeDir == NE_RESIZE_CURSOR || resizeDir == SE_RESIZE_CURSOR ) {
				newBounds.width = (xOnScreen + dragRightOffset) - newBounds.x;
				if( limitToParentBounds() ) {
					Rectangle parentBounds = getParentBounds();
					int parentRightX = parentBounds.x + parentBounds.width;
					if( newBounds.x + newBounds.width > parentRightX )
						newBounds.width = parentRightX - newBounds.x;
				}
			}

			// apply minimum window size
			Dimension minimumSize = honorMinimumSizeOnResize() ? getWindowMinimumSize() : null;
			if( minimumSize == null )
				minimumSize = UIScale.scale( new Dimension( 150, 50 ) );
			if( newBounds.width < minimumSize.width )
				changeWidth( oldBounds, newBounds, minimumSize.width );
			if( newBounds.height < minimumSize.height )
				changeHeight( oldBounds, newBounds, minimumSize.height );

			// apply maximum window size
			if( honorMaximumSizeOnResize() ) {
				Dimension maximumSize = getWindowMaximumSize();
				if( newBounds.width > maximumSize.width )
					changeWidth( oldBounds, newBounds, maximumSize.width );
				if( newBounds.height > maximumSize.height )
					changeHeight( oldBounds, newBounds, maximumSize.height );
			}

			// set window bounds
			if( !newBounds.equals( oldBounds ) )
				setWindowBounds( newBounds );
		}

		private void changeWidth( Rectangle oldBounds, Rectangle newBounds, int width ) {
			if( newBounds.x != oldBounds.x )
				newBounds.x -= (width - newBounds.width);
			newBounds.width = width;
		}

		private void changeHeight( Rectangle oldBounds, Rectangle newBounds, int height ) {
			if( newBounds.y != oldBounds.y )
				newBounds.y -= (height - newBounds.height);
			newBounds.height = height;
		}
	}
}
