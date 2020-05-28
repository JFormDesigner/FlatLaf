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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.accessibility.AccessibleContext;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF title bar.
 *
 * @uiDefault TitlePane.background							Color
 * @uiDefault TitlePane.inactiveBackground					Color
 * @uiDefault TitlePane.foreground							Color
 * @uiDefault TitlePane.inactiveForeground					Color
 * @uiDefault TitlePane.iconSize							Dimension
 * @uiDefault TitlePane.iconMargins							Insets
 * @uiDefault TitlePane.titleMargins						Insets
 * @uiDefault TitlePane.buttonMaximizedHeight				int
 * @uiDefault TitlePane.closeIcon							Icon
 * @uiDefault TitlePane.iconifyIcon							Icon
 * @uiDefault TitlePane.maximizeIcon						Icon
 * @uiDefault TitlePane.restoreIcon							Icon
 *
 * @author Karl Tauber
 */
class FlatTitlePane
	extends JComponent
{
	private final Color activeBackground = UIManager.getColor( "TitlePane.background" );
	private final Color inactiveBackground = UIManager.getColor( "TitlePane.inactiveBackground" );
	private final Color activeForeground = UIManager.getColor( "TitlePane.foreground" );
	private final Color inactiveForeground = UIManager.getColor( "TitlePane.inactiveForeground" );

	private final Dimension iconSize = UIManager.getDimension( "TitlePane.iconSize" );
	private final int buttonMaximizedHeight = UIManager.getInt( "TitlePane.buttonMaximizedHeight" );

	private final JRootPane rootPane;

	private JLabel iconLabel;
	private JLabel titleLabel;
	private JPanel buttonPanel;
	private JButton iconifyButton;
	private JButton maximizeButton;
	private JButton restoreButton;
	private JButton closeButton;

	private final Handler handler = new Handler();
	private Window window;

	FlatTitlePane( JRootPane rootPane ) {
		this.rootPane = rootPane;

		addSubComponents();
		activeChanged( true );

		addMouseListener( handler );
		addMouseMotionListener( handler );
	}

	private void addSubComponents() {
		iconLabel = new JLabel();
		titleLabel = new JLabel();
		iconLabel.setBorder( new FlatEmptyBorder( UIManager.getInsets( "TitlePane.iconMargins" ) ) );
		titleLabel.setBorder( new FlatEmptyBorder( UIManager.getInsets( "TitlePane.titleMargins" ) ) );

		createButtons();

		setLayout( new BorderLayout() );
		add( iconLabel, BorderLayout.WEST );
		add( titleLabel, BorderLayout.CENTER );
		add( buttonPanel, BorderLayout.EAST );
	}

	private void createButtons() {
		iconifyButton = createButton( "TitlePane.iconifyIcon", "Iconify", e -> iconify() );
		maximizeButton = createButton( "TitlePane.maximizeIcon", "Maximize", e -> maximize() );
		restoreButton = createButton( "TitlePane.restoreIcon", "Restore", e -> restore() );
		closeButton = createButton( "TitlePane.closeIcon", "Close", e -> close() );

		buttonPanel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				if( buttonMaximizedHeight > 0 &&
					window instanceof Frame &&
					(((Frame)window).getExtendedState() & Frame.MAXIMIZED_BOTH) != 0 )
				{
					// make title pane height smaller when frame is maximized
					size = new Dimension( size.width, Math.min( size.height, UIScale.scale( buttonMaximizedHeight ) ) );
				}
				return size;
			}
		};
		buttonPanel.setOpaque( false );
		buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
		if( rootPane.getWindowDecorationStyle() == JRootPane.FRAME ) {
			// JRootPane.FRAME works only for frames (and not for dialogs)
			// but at this time the owner window type is unknown (not yet added)
			// so we add the iconify/maximize/restore buttons and they are hidden
			// later in frameStateChanged(), which is invoked from addNotify()

			restoreButton.setVisible( false );

			buttonPanel.add( iconifyButton );
			buttonPanel.add( maximizeButton );
			buttonPanel.add( restoreButton );
		}
		buttonPanel.add( closeButton );
	}

	private JButton createButton( String iconKey, String accessibleName, ActionListener action ) {
		JButton button = new JButton( UIManager.getIcon( iconKey ) );
		button.setFocusable( false );
		button.setContentAreaFilled( false );
		button.setBorder( BorderFactory.createEmptyBorder() );
		button.putClientProperty( AccessibleContext.ACCESSIBLE_NAME_PROPERTY, accessibleName );
		button.addActionListener( action );
		return button;
	}

	private void activeChanged( boolean active ) {
		Color background = FlatUIUtils.nonUIResource( active ? activeBackground : inactiveBackground );
		Color foreground = FlatUIUtils.nonUIResource( active ? activeForeground : inactiveForeground );

		setBackground( background );
		titleLabel.setForeground( foreground );

		// this is necessary because hover/pressed colors are derived from background color
		iconifyButton.setBackground( background );
		maximizeButton.setBackground( background );
		restoreButton.setBackground( background );
		closeButton.setBackground( background );
	}

	private void frameStateChanged() {
		if( window == null || rootPane.getWindowDecorationStyle() != JRootPane.FRAME )
			return;

		if( window instanceof Frame ) {
			Frame frame = (Frame) window;
			boolean resizable = frame.isResizable();
			boolean maximized = ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0);

			iconifyButton.setVisible( true );
			maximizeButton.setVisible( resizable && !maximized );
			restoreButton.setVisible( resizable && maximized );
		} else {
			// hide buttons because they are only supported in frames
			iconifyButton.setVisible( false );
			maximizeButton.setVisible( false );
			restoreButton.setVisible( false );

			revalidate();
			repaint();
		}
	}

	private void updateIcon() {
		// get window images
		List<Image> images = window.getIconImages();
		if( images.isEmpty() ) {
			// search in owners
			for( Window owner = window.getOwner(); owner != null; owner = owner.getOwner() ) {
				images = owner.getIconImages();
				if( !images.isEmpty() )
					break;
			}
		}

		// show/hide icon
		boolean hasImages = !images.isEmpty();
		iconLabel.setVisible( hasImages );

		if( hasImages )
			iconLabel.setIcon( FlatTitlePaneIcon.create( images, iconSize ) );
	}

	@Override
	public void addNotify() {
		super.addNotify();

		uninstallWindowListeners();

		window = SwingUtilities.getWindowAncestor( this );
		if( window != null ) {
			frameStateChanged();
			activeChanged( window.isActive() );
			updateIcon();
			titleLabel.setText( getWindowTitle() );
			installWindowListeners();
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		uninstallWindowListeners();
		window = null;
	}

	private String getWindowTitle() {
		if( window instanceof Frame )
			return ((Frame)window).getTitle();
		if( window instanceof Dialog )
			return ((Dialog)window).getTitle();
		return null;
	}

	private void installWindowListeners() {
		if( window == null )
			return;

		window.addPropertyChangeListener( handler );
		window.addWindowListener( handler );
		window.addWindowStateListener( handler );
	}

	private void uninstallWindowListeners() {
		if( window == null )
			return;

		window.removePropertyChangeListener( handler );
		window.removeWindowListener( handler );
		window.removeWindowStateListener( handler );
	}

	@Override
	protected void paintComponent( Graphics g ) {
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
	}

	private void iconify() {
		if( window instanceof Frame ) {
			Frame frame = (Frame) window;
			frame.setExtendedState( frame.getExtendedState() | Frame.ICONIFIED );
		}
	}

	private void maximize() {
		if( window instanceof Frame ) {
			Frame frame = (Frame) window;
			GraphicsConfiguration gc = window.getGraphicsConfiguration();

			// remember current maximized bounds
			Rectangle oldMaximizedBounds = frame.getMaximizedBounds();

			// Scaled screen size, which may be smaller than physical size on Java 9+.
			// E.g. if running a 3840x2160 screen at 200%, scaledSreenSize is 1920x1080.
			// In Java 9+, each screen can have its own scale factor.
			//
			// On Java 8, which does not scale, scaledSreenSize of the primary screen
			// is identical to its physical size. But when the primary screen is scaled,
			// then scaledSreenSize of secondary screens is scaled with the scale factor
			// of the primary screen.
			// E.g. primary 3840x2160 screen at 150%, secondary 1920x1080 screen at 100%,
			// then scaledSreenSize is 3840x2160 on primary and 2880x1560 on secondary.
			Dimension scaledSreenSize = gc.getBounds().getSize();

			// scale screen bounds to get physical screen size
			AffineTransform defaultTransform = gc.getDefaultTransform();
			int screenWidth = (int) (scaledSreenSize.width * defaultTransform.getScaleX());
			int screenHeight = (int) (scaledSreenSize.height * defaultTransform.getScaleY());

			// screen insets are in physical size,
			// except for Java 8 on secondary screens where primary screen is scaled
			Insets screenInsets = window.getToolkit().getScreenInsets( gc );

			// maximized bounds are required in physical size,
			// except for Java 8 on secondary screens where primary screen is scaled
			Rectangle maximizedBounds = new Rectangle( screenInsets.left, screenInsets.top,
				screenWidth - screenInsets.left - screenInsets.right,
				screenHeight - screenInsets.top - screenInsets.bottom );

			// temporary change maximized bounds
			frame.setMaximizedBounds( maximizedBounds );

			// maximize window
			frame.setExtendedState( frame.getExtendedState() | Frame.MAXIMIZED_BOTH );

			// restore old maximized bounds
			frame.setMaximizedBounds( oldMaximizedBounds );
		}
	}

	private void restore() {
		if( window instanceof Frame ) {
			Frame frame = (Frame) window;
			int state = frame.getExtendedState();
			frame.setExtendedState( ((state & Frame.ICONIFIED) != 0)
				? (state & ~Frame.ICONIFIED)
				: (state & ~Frame.MAXIMIZED_BOTH) );
		}
	}

	private void close() {
		if( window != null )
			window.dispatchEvent( new WindowEvent( window, WindowEvent.WINDOW_CLOSING ) );
	}

	//---- class Handler ------------------------------------------------------

	private class Handler
		extends WindowAdapter
		implements PropertyChangeListener, MouseListener, MouseMotionListener
	{
		//---- interface PropertyChangeListener ----

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			switch( e.getPropertyName() ) {
				case "title":
					titleLabel.setText( getWindowTitle() );
					break;

				case "resizable":
					if( window instanceof Frame )
						frameStateChanged();
					break;

				case "iconImage":
					updateIcon();
					break;
			}
		}

		//---- interface WindowListener ----

		@Override
		public void windowActivated( WindowEvent e ) {
			activeChanged( true );
		}

		@Override
		public void windowDeactivated( WindowEvent e ) {
			activeChanged( false );
		}

		@Override
		public void windowStateChanged( WindowEvent e ) {
			frameStateChanged();
		}

		//---- interface MouseListener ----

		private int lastXOnScreen;
		private int lastYOnScreen;

		@Override
		public void mouseClicked( MouseEvent e ) {
			if( e.getClickCount() == 2 &&
				SwingUtilities.isLeftMouseButton( e ) &&
				window instanceof Frame &&
				((Frame)window).isResizable() )
			{
				// maximize/restore on double-click
				Frame frame = (Frame) window;
				if( (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0 )
					restore();
				else
					maximize();
			}
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			lastXOnScreen = e.getXOnScreen();
			lastYOnScreen = e.getYOnScreen();
		}

		@Override public void mouseReleased( MouseEvent e ) {}
		@Override public void mouseEntered( MouseEvent e ) {}
		@Override public void mouseExited( MouseEvent e ) {}

		//---- interface MouseMotionListener ----

		@Override
		public void mouseDragged( MouseEvent e ) {
			int xOnScreen = e.getXOnScreen();
			int yOnScreen = e.getYOnScreen();
			if( lastXOnScreen == xOnScreen && lastYOnScreen == yOnScreen )
				return;

			// restore window if it is maximized
			if( window instanceof Frame ) {
				Frame frame = (Frame) window;
				int state = frame.getExtendedState();
				if( (state & Frame.MAXIMIZED_BOTH) != 0 ) {
					int maximizedX = window.getX();
					int maximizedY = window.getY();

					// restore window size, which also moves window to pre-maximized location
					frame.setExtendedState( state & ~Frame.MAXIMIZED_BOTH );

					int restoredWidth = window.getWidth();
					int newX = maximizedX;
					if( xOnScreen >= maximizedX + restoredWidth - buttonPanel.getWidth() - 10 )
						newX = xOnScreen + buttonPanel.getWidth() + 10 - restoredWidth;

					// move window near mouse
					window.setLocation( newX, maximizedY );
					return;
				}
			}

			// compute new window location
			int newX = window.getX() + (xOnScreen - lastXOnScreen);
			int newY = window.getY() + (yOnScreen - lastYOnScreen);

			// move window
			window.setLocation( newX, newY );

			lastXOnScreen = xOnScreen;
			lastYOnScreen = yOnScreen;
		}

		@Override public void mouseMoved( MouseEvent e ) {}
	}
}
