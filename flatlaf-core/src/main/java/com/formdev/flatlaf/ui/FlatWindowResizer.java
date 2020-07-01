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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
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
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import com.formdev.flatlaf.util.UIScale;

/**
 * Resizes frames and dialogs.
 *
 * @author Karl Tauber
 */
public class FlatWindowResizer
	extends JComponent
	implements PropertyChangeListener, WindowStateListener, ComponentListener
{
	protected final static Integer WINDOW_RESIZER_LAYER = JLayeredPane.DRAG_LAYER + 1;

	protected final JRootPane rootPane;

	protected final int borderDragThickness = FlatUIUtils.getUIInt( "RootPane.borderDragThickness", 5 );
	protected final int cornerDragWidth = FlatUIUtils.getUIInt( "RootPane.cornerDragWidth", 16 );
	protected final boolean honorMinimumSizeOnResize = UIManager.getBoolean( "RootPane.honorMinimumSizeOnResize" );

	protected Window window;

	public FlatWindowResizer( JRootPane rootPane ) {
		this.rootPane = rootPane;

		setLayout( new BorderLayout() );
		add( createDragBorderComponent( NW_RESIZE_CURSOR, N_RESIZE_CURSOR, NE_RESIZE_CURSOR ), BorderLayout.NORTH );
		add( createDragBorderComponent( SW_RESIZE_CURSOR, S_RESIZE_CURSOR, SE_RESIZE_CURSOR ), BorderLayout.SOUTH );
		add( createDragBorderComponent( NW_RESIZE_CURSOR, W_RESIZE_CURSOR, SW_RESIZE_CURSOR ), BorderLayout.WEST );
		add( createDragBorderComponent( NE_RESIZE_CURSOR, E_RESIZE_CURSOR, SE_RESIZE_CURSOR ), BorderLayout.EAST );

		rootPane.addComponentListener( this );
		rootPane.getLayeredPane().add( this, WINDOW_RESIZER_LAYER );

		if( rootPane.isDisplayable() )
			setBounds( 0, 0, rootPane.getWidth(), rootPane.getHeight() );
	}

	protected DragBorderComponent createDragBorderComponent( int leadingResizeDir, int centerResizeDir, int trailingResizeDir ) {
		return new DragBorderComponent( leadingResizeDir, centerResizeDir, trailingResizeDir );
	}

	public void uninstall() {
		rootPane.removeComponentListener( this );
		rootPane.getLayeredPane().remove( this );
	}

	@Override
	public void addNotify() {
		super.addNotify();

		Container parent = rootPane.getParent();
		window = (parent instanceof Window) ? (Window) parent : null;
		if( window instanceof Frame ) {
			window.addPropertyChangeListener( "resizable", this );
			window.addWindowStateListener( this );
		}

		updateVisibility();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		if( window instanceof Frame ) {
			window.removePropertyChangeListener( "resizable", this );
			window.removeWindowStateListener( this );
		}
		window = null;

		updateVisibility();
	}

	@Override
	protected void paintChildren( Graphics g ) {
		super.paintChildren( g );

		// this is necessary because Dialog.setResizable() does not fire events
		if( window instanceof Dialog )
			updateVisibility();
	}

	private void updateVisibility() {
		boolean visible = isWindowResizable();
		if( visible == getComponent( 0 ).isVisible() )
			return;

		for( Component c : getComponents() )
			c.setVisible( visible );
	}

	private boolean isWindowResizable() {
		if( window instanceof Frame )
			return ((Frame)window).isResizable() && (((Frame)window).getExtendedState() & Frame.MAXIMIZED_BOTH) == 0;
		if( window instanceof Dialog )
			return ((Dialog)window).isResizable();
		return false;
	}

	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		updateVisibility();
	}

	@Override
	public void windowStateChanged( WindowEvent e ) {
		updateVisibility();
	}

	@Override
	public void componentResized( ComponentEvent e ) {
		setBounds( 0, 0, rootPane.getWidth(), rootPane.getHeight() );
		validate();
	}

	@Override public void componentMoved( ComponentEvent e ) {}
	@Override public void componentShown( ComponentEvent e ) {}
	@Override public void componentHidden( ComponentEvent e ) {}

	//---- class DragBorderComponent ------------------------------------------

	protected class DragBorderComponent
		extends JComponent
		implements MouseListener, MouseMotionListener
	{
		private final int leadingResizeDir;
		private final int centerResizeDir;
		private final int trailingResizeDir;

		private int resizeDir = -1;
		private int dragStartMouseX;
		private int dragStartMouseY;
		private Rectangle dragStartWindowBounds;

		protected DragBorderComponent( int leadingResizeDir, int centerResizeDir, int trailingResizeDir ) {
			this.leadingResizeDir = leadingResizeDir;
			this.centerResizeDir = centerResizeDir;
			this.trailingResizeDir = trailingResizeDir;

			setResizeDir( centerResizeDir );
			setVisible( false );

			addMouseListener( this );
			addMouseMotionListener( this );
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

/*debug
		@Override
		protected void paintComponent( Graphics g ) {
			g.setColor( java.awt.Color.red );
			g.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );
		}
debug*/

		@Override
		public void mouseClicked( MouseEvent e ) {
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			if( window == null )
				return;

			dragStartMouseX = e.getXOnScreen();
			dragStartMouseY = e.getYOnScreen();
			dragStartWindowBounds = window.getBounds();
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			dragStartWindowBounds = null;
		}

		@Override
		public void mouseEntered( MouseEvent e ) {
		}

		@Override
		public void mouseExited( MouseEvent e ) {
		}

		@Override
		public void mouseMoved( MouseEvent e ) {
			boolean topBottom = (centerResizeDir == N_RESIZE_CURSOR || centerResizeDir == S_RESIZE_CURSOR);
			int xy = topBottom ? e.getX() : e.getY();
			int wh = topBottom ? getWidth() : getHeight();
			int cornerWH = UIScale.scale( cornerDragWidth - (topBottom ? 0 : borderDragThickness) );

			setResizeDir( xy <= cornerWH
				? leadingResizeDir
				: (xy >= wh - cornerWH
					? trailingResizeDir
					: centerResizeDir) );
		}

		@Override
		public void mouseDragged( MouseEvent e ) {
			if( dragStartWindowBounds == null )
				return;

			if( !isWindowResizable() )
				return;

			int mouseDeltaX = e.getXOnScreen() - dragStartMouseX;
			int mouseDeltaY = e.getYOnScreen() - dragStartMouseY;

			int deltaX = 0;
            int deltaY = 0;
            int deltaWidth = 0;
            int deltaHeight = 0;

			// north
			if( resizeDir == N_RESIZE_CURSOR || resizeDir == NW_RESIZE_CURSOR || resizeDir == NE_RESIZE_CURSOR ) {
				deltaY = mouseDeltaY;
				deltaHeight = -mouseDeltaY;
			}

			// south
			if( resizeDir == S_RESIZE_CURSOR || resizeDir == SW_RESIZE_CURSOR || resizeDir == SE_RESIZE_CURSOR )
				deltaHeight = mouseDeltaY;

			// west
			if( resizeDir == W_RESIZE_CURSOR || resizeDir == NW_RESIZE_CURSOR || resizeDir == SW_RESIZE_CURSOR ) {
				deltaX = mouseDeltaX;
				deltaWidth = -mouseDeltaX;
			}

			// east
			if( resizeDir == E_RESIZE_CURSOR || resizeDir == NE_RESIZE_CURSOR || resizeDir == SE_RESIZE_CURSOR )
				deltaWidth = mouseDeltaX;

			// compute new window bounds
			Rectangle newBounds = new Rectangle( dragStartWindowBounds );
			newBounds.x += deltaX;
			newBounds.y += deltaY;
			newBounds.width += deltaWidth;
			newBounds.height += deltaHeight;

			// apply minimum window size
			Dimension minimumSize = honorMinimumSizeOnResize ? window.getMinimumSize() : null;
			if( minimumSize == null )
				minimumSize = UIScale.scale( new Dimension( 150, 50 ) );
			if( newBounds.width < minimumSize.width ) {
				if( deltaX != 0 )
					newBounds.x -= (minimumSize.width - newBounds.width);
				newBounds.width = minimumSize.width;
			}
			if( newBounds.height < minimumSize.height ) {
				if( deltaY != 0 )
					newBounds.y -= (minimumSize.height - newBounds.height);
				newBounds.height = minimumSize.height;
			}

			// set window bounds
			if( !newBounds.equals( dragStartWindowBounds ) ) {
				window.setBounds( newBounds );

				// immediately layout drag border components
				FlatWindowResizer.this.setBounds( 0, 0, newBounds.width, newBounds.height );
				FlatWindowResizer.this.validate();

				if( Toolkit.getDefaultToolkit().isDynamicLayoutActive() ) {
					window.validate();
					rootPane.repaint();
				}
			}
		}
	}
}
