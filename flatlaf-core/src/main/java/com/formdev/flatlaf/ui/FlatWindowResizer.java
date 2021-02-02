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
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.UIManager;
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
	protected final static Integer WINDOW_RESIZER_LAYER = JLayeredPane.DRAG_LAYER + 1;

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

		Container cont = (resizeComp instanceof JRootPane) ? ((JRootPane)resizeComp).getLayeredPane() : resizeComp;
		Object cons = (cont instanceof JLayeredPane) ? WINDOW_RESIZER_LAYER : null;
		cont.add( topDragComp, cons, 0 );
		cont.add( bottomDragComp, cons, 1 );
		cont.add( leftDragComp, cons, 2 );
		cont.add( rightDragComp, cons, 3 );

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

		Container cont = topDragComp.getParent();
		cont.remove( topDragComp );
		cont.remove( bottomDragComp );
		cont.remove( leftDragComp );
		cont.remove( rightDragComp );
	}

	public void doLayout() {
		if( !topDragComp.isVisible() )
			return;

		int x = 0;
		int y = 0;
		int width = resizeComp.getWidth();
		int height = resizeComp.getHeight();
		if( width == 0 || height == 0 )
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
	protected abstract boolean honorMinimumSizeOnResize();
	protected abstract Dimension getWindowMinimumSize();

	protected void beginResizing( int direction ) {}
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

		public WindowResizer( JRootPane rootPane ) {
			super( rootPane );
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
		protected boolean honorMinimumSizeOnResize() {
			return
				(honorFrameMinimumSizeOnResize && window instanceof Frame) ||
				(honorDialogMinimumSizeOnResize && window instanceof Dialog);
		}

		@Override
		protected Dimension getWindowMinimumSize() {
			return window.getMinimumSize();
		}

		@Override
		boolean isDialog() {
			return window instanceof Dialog;
		}

		@Override
		public void windowStateChanged( WindowEvent e ) {
			updateVisibility();
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
		protected boolean honorMinimumSizeOnResize() {
			return true;
		}

		@Override
		protected Dimension getWindowMinimumSize() {
			return getFrame().getMinimumSize();
		}

		@Override
		protected void beginResizing( int direction ) {
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
			if( !isWindowResizable() )
				return;

			int xOnScreen = e.getXOnScreen();
			int yOnScreen = e.getYOnScreen();
			Rectangle windowBounds = getWindowBounds();

			// compute offsets of mouse position to window edges
			dragLeftOffset = xOnScreen - windowBounds.x;
			dragTopOffset = yOnScreen - windowBounds.y;
			dragRightOffset = windowBounds.x + windowBounds.width - xOnScreen;
			dragBottomOffset = windowBounds.y + windowBounds.height - yOnScreen;

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
			beginResizing( direction );
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			if( !isWindowResizable() )
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
			if( !isWindowResizable() )
				return;

			int xOnScreen = e.getXOnScreen();
			int yOnScreen = e.getYOnScreen();

			// Get current window bounds and compute new bounds based them.
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
				newBounds.height += (oldBounds.y - newBounds.y);
			}

			// bottom
			if( resizeDir == S_RESIZE_CURSOR || resizeDir == SW_RESIZE_CURSOR || resizeDir == SE_RESIZE_CURSOR )
				newBounds.height = (yOnScreen + dragBottomOffset) - newBounds.y;

			// left
			if( resizeDir == W_RESIZE_CURSOR || resizeDir == NW_RESIZE_CURSOR || resizeDir == SW_RESIZE_CURSOR ) {
				newBounds.x = xOnScreen - dragLeftOffset;
				newBounds.width += (oldBounds.x - newBounds.x);
			}

			// right
			if( resizeDir == E_RESIZE_CURSOR || resizeDir == NE_RESIZE_CURSOR || resizeDir == SE_RESIZE_CURSOR )
				newBounds.width = (xOnScreen + dragRightOffset) - newBounds.x;

			// apply minimum window size
			Dimension minimumSize = honorMinimumSizeOnResize() ? getWindowMinimumSize() : null;
			if( minimumSize == null )
				minimumSize = UIScale.scale( new Dimension( 150, 50 ) );
			if( newBounds.width < minimumSize.width ) {
				if( newBounds.x != oldBounds.x )
					newBounds.x -= (minimumSize.width - newBounds.width);
				newBounds.width = minimumSize.width;
			}
			if( newBounds.height < minimumSize.height ) {
				if( newBounds.y != oldBounds.y )
					newBounds.y -= (minimumSize.height - newBounds.height);
				newBounds.height = minimumSize.height;
			}

			// set window bounds
			if( !newBounds.equals( oldBounds ) )
				setWindowBounds( newBounds );
		}
	}
}
