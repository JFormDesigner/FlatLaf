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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.accessibility.AccessibleContext;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.FlatNativeWindowBorder.WindowTopBorder;
import com.formdev.flatlaf.util.ScaledImageIcon;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF title bar.
 *
 * @uiDefault TitlePane.background							Color
 * @uiDefault TitlePane.inactiveBackground					Color
 * @uiDefault TitlePane.foreground							Color
 * @uiDefault TitlePane.inactiveForeground					Color
 * @uiDefault TitlePane.embeddedForeground					Color
 * @uiDefault TitlePane.borderColor							Color	optional
 * @uiDefault TitlePane.unifiedBackground					boolean
 * @uiDefault TitlePane.iconSize							Dimension
 * @uiDefault TitlePane.iconMargins							Insets
 * @uiDefault TitlePane.titleMargins						Insets
 * @uiDefault TitlePane.menuBarEmbedded						boolean
 * @uiDefault TitlePane.buttonMaximizedHeight				int
 * @uiDefault TitlePane.centerTitle							boolean
 * @uiDefault TitlePane.centerTitleIfMenuBarEmbedded		boolean
 * @uiDefault TitlePane.menuBarTitleGap						int
 * @uiDefault TitlePane.icon								Icon
 * @uiDefault TitlePane.closeIcon							Icon
 * @uiDefault TitlePane.iconifyIcon							Icon
 * @uiDefault TitlePane.maximizeIcon						Icon
 * @uiDefault TitlePane.restoreIcon							Icon
 *
 * @author Karl Tauber
 */
public class FlatTitlePane
	extends JComponent
{
	protected final Color activeBackground = UIManager.getColor( "TitlePane.background" );
	protected final Color inactiveBackground = UIManager.getColor( "TitlePane.inactiveBackground" );
	protected final Color activeForeground = UIManager.getColor( "TitlePane.foreground" );
	protected final Color inactiveForeground = UIManager.getColor( "TitlePane.inactiveForeground" );
	protected final Color embeddedForeground = UIManager.getColor( "TitlePane.embeddedForeground" );
	protected final Color borderColor = UIManager.getColor( "TitlePane.borderColor" );

	protected final Dimension iconSize = UIManager.getDimension( "TitlePane.iconSize" );
	protected final int buttonMaximizedHeight = UIManager.getInt( "TitlePane.buttonMaximizedHeight" );
	protected final boolean centerTitle = UIManager.getBoolean( "TitlePane.centerTitle" );
	protected final boolean centerTitleIfMenuBarEmbedded = FlatUIUtils.getUIBoolean( "TitlePane.centerTitleIfMenuBarEmbedded", true );
	protected final int menuBarTitleGap = FlatUIUtils.getUIInt( "TitlePane.menuBarTitleGap", 20 );

	protected final JRootPane rootPane;

	protected JPanel leftPanel;
	protected JLabel iconLabel;
	protected JComponent menuBarPlaceholder;
	protected JLabel titleLabel;
	protected JPanel buttonPanel;
	protected JButton iconifyButton;
	protected JButton maximizeButton;
	protected JButton restoreButton;
	protected JButton closeButton;

	protected Window window;

	private final Handler handler;

	public FlatTitlePane( JRootPane rootPane ) {
		this.rootPane = rootPane;

		handler = createHandler();
		setBorder( createTitlePaneBorder() );

		addSubComponents();
		activeChanged( true );

		addMouseListener( handler );
		addMouseMotionListener( handler );

		// necessary for closing window with double-click on icon
		iconLabel.addMouseListener( handler );
	}

	protected FlatTitlePaneBorder createTitlePaneBorder() {
		return new FlatTitlePaneBorder();
	}

	protected Handler createHandler() {
		return new Handler();
	}

	protected void addSubComponents() {
		leftPanel = new JPanel();
		iconLabel = new JLabel();
		titleLabel = new JLabel() {
			@Override
			public void updateUI() {
				setUI( new FlatTitleLabelUI() );
			}
		};
		iconLabel.setBorder( new FlatEmptyBorder( UIManager.getInsets( "TitlePane.iconMargins" ) ) );
		titleLabel.setBorder( new FlatEmptyBorder( UIManager.getInsets( "TitlePane.titleMargins" ) ) );
		titleLabel.setHorizontalAlignment( SwingConstants.CENTER );

		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.LINE_AXIS ) );
		leftPanel.setOpaque( false );
		leftPanel.add( iconLabel );

		menuBarPlaceholder = new JComponent() {
			@Override
			public Dimension getPreferredSize() {
				JMenuBar menuBar = rootPane.getJMenuBar();
				return hasVisibleEmbeddedMenuBar( menuBar ) ? menuBar.getPreferredSize() : new Dimension();
			}
		};
		leftPanel.add( menuBarPlaceholder );

		createButtons();

		setLayout( new BorderLayout() {
			@Override
			public void layoutContainer( Container target ) {
				super.layoutContainer( target );

				// make left panel (with embedded menu bar) smaller if horizontal space is rare
				// to avoid that embedded menu bar overlaps button bar
				Insets insets = target.getInsets();
				int width = target.getWidth() - insets.left - insets.right;
				if( leftPanel.getWidth() + buttonPanel.getWidth() > width ) {
					int oldWidth = leftPanel.getWidth();
					int newWidth = Math.max( width - buttonPanel.getWidth(), 0 );
					leftPanel.setSize( newWidth, leftPanel.getHeight() );
					if( !getComponentOrientation().isLeftToRight() )
						leftPanel.setLocation( leftPanel.getX() + (oldWidth - newWidth), leftPanel.getY() );
				}

				// If menu bar is embedded and contains a horizontal glue component,
				// then move the title label to the same location as the glue component
				// and give it the same width.
				// This allows placing any component on the trailing side of the title pane.
				JMenuBar menuBar = rootPane.getJMenuBar();
				if( hasVisibleEmbeddedMenuBar( menuBar ) ) {
					Component horizontalGlue = findHorizontalGlue( menuBar );
					if( horizontalGlue != null ) {
						Point glueLocation = SwingUtilities.convertPoint( horizontalGlue, 0, 0, titleLabel );
						titleLabel.setBounds( titleLabel.getX() + glueLocation.x, titleLabel.getY(),
							horizontalGlue.getWidth(), titleLabel.getHeight() );
					}
				}
			}
		} );

		add( leftPanel, BorderLayout.LINE_START );
		add( titleLabel, BorderLayout.CENTER );
		add( buttonPanel, BorderLayout.LINE_END );
	}

	protected void createButtons() {
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
		buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.LINE_AXIS ) );
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

	protected JButton createButton( String iconKey, String accessibleName, ActionListener action ) {
		JButton button = new JButton( UIManager.getIcon( iconKey ) );
		button.setFocusable( false );
		button.setContentAreaFilled( false );
		button.setBorder( BorderFactory.createEmptyBorder() );
		button.putClientProperty( AccessibleContext.ACCESSIBLE_NAME_PROPERTY, accessibleName );
		button.addActionListener( action );
		return button;
	}

	protected void activeChanged( boolean active ) {
		boolean hasEmbeddedMenuBar = hasVisibleEmbeddedMenuBar( rootPane.getJMenuBar() );
		Color background = FlatUIUtils.nonUIResource( active ? activeBackground : inactiveBackground );
		Color foreground = FlatUIUtils.nonUIResource( active ? activeForeground : inactiveForeground );
		Color titleForeground = (hasEmbeddedMenuBar && active) ? FlatUIUtils.nonUIResource( embeddedForeground ) : foreground;

		setBackground( background );
		titleLabel.setForeground( titleForeground );
		iconifyButton.setForeground( foreground );
		maximizeButton.setForeground( foreground );
		restoreButton.setForeground( foreground );
		closeButton.setForeground( foreground );

		// this is necessary because hover/pressed colors are derived from background color
		iconifyButton.setBackground( background );
		maximizeButton.setBackground( background );
		restoreButton.setBackground( background );
		closeButton.setBackground( background );
	}

	protected void frameStateChanged() {
		if( window == null || rootPane.getWindowDecorationStyle() != JRootPane.FRAME )
			return;

		if( window instanceof Frame ) {
			Frame frame = (Frame) window;
			boolean resizable = frame.isResizable();
			boolean maximized = ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0);

			iconifyButton.setVisible( true );
			maximizeButton.setVisible( resizable && !maximized );
			restoreButton.setVisible( resizable && maximized );

			if( maximized &&
				rootPane.getClientProperty( "_flatlaf.maximizedBoundsUpToDate" ) == null )
			{
				rootPane.putClientProperty( "_flatlaf.maximizedBoundsUpToDate", null );

				// In case that frame was maximized from custom code (e.g. when restoring
				// window state on application startup), then maximized bounds is not set
				// and the window would overlap Windows task bar.
				// To avoid this, update maximized bounds here and if it has changed
				// re-maximize windows so that maximized bounds are used.
				Rectangle oldMaximizedBounds = frame.getMaximizedBounds();
				updateMaximizedBounds();
				Rectangle newMaximizedBounds = frame.getMaximizedBounds();
				if( newMaximizedBounds != null && !newMaximizedBounds.equals( oldMaximizedBounds ) ) {
					int oldExtendedState = frame.getExtendedState();
					frame.setExtendedState( oldExtendedState & ~Frame.MAXIMIZED_BOTH );
					frame.setExtendedState( oldExtendedState );
				}
			}
		} else {
			// hide buttons because they are only supported in frames
			iconifyButton.setVisible( false );
			maximizeButton.setVisible( false );
			restoreButton.setVisible( false );

			revalidate();
			repaint();
		}
	}

	protected void updateIcon() {
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

		boolean hasIcon = true;

		// set icon
		if( !images.isEmpty() )
			iconLabel.setIcon( FlatTitlePaneIcon.create( images, iconSize ) );
		else {
			// no icon set on window --> use default icon
			Icon defaultIcon = UIManager.getIcon( "TitlePane.icon" );
			if( defaultIcon != null && (defaultIcon.getIconWidth() == 0 || defaultIcon.getIconHeight() == 0) )
				defaultIcon = null;
			if( defaultIcon != null ) {
				if( defaultIcon instanceof ImageIcon )
					defaultIcon = new ScaledImageIcon( (ImageIcon) defaultIcon, iconSize.width, iconSize.height );
				iconLabel.setIcon( defaultIcon );
			} else
				hasIcon = false;
		}

		// show/hide icon
		iconLabel.setVisible( hasIcon );

		updateNativeTitleBarHeightAndHitTestSpotsLater();
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

		updateNativeTitleBarHeightAndHitTestSpotsLater();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		uninstallWindowListeners();
		window = null;
	}

	protected String getWindowTitle() {
		if( window instanceof Frame )
			return ((Frame)window).getTitle();
		if( window instanceof Dialog )
			return ((Dialog)window).getTitle();
		return null;
	}

	protected void installWindowListeners() {
		if( window == null )
			return;

		window.addPropertyChangeListener( handler );
		window.addWindowListener( handler );
		window.addWindowStateListener( handler );
		window.addComponentListener( handler );
	}

	protected void uninstallWindowListeners() {
		if( window == null )
			return;

		window.removePropertyChangeListener( handler );
		window.removeWindowListener( handler );
		window.removeWindowStateListener( handler );
		window.removeComponentListener( handler );
	}

	/**
	 * Returns whether this title pane currently has an visible and embedded menubar.
	 */
	protected boolean hasVisibleEmbeddedMenuBar( JMenuBar menuBar ) {
		return menuBar != null && menuBar.isVisible() && isMenuBarEmbedded();
	}

	/**
	 * Returns whether the menubar should be embedded into the title pane.
	 */
	protected boolean isMenuBarEmbedded() {
		// not storing value of "TitlePane.menuBarEmbedded" in class to allow changing at runtime
		return UIManager.getBoolean( "TitlePane.menuBarEmbedded" ) &&
			FlatClientProperties.clientPropertyBoolean( rootPane, FlatClientProperties.MENU_BAR_EMBEDDED, true ) &&
			FlatSystemProperties.getBoolean( FlatSystemProperties.MENUBAR_EMBEDDED, true );
	}

	protected Rectangle getMenuBarBounds() {
		Insets insets = rootPane.getInsets();
		Rectangle bounds = new Rectangle(
			SwingUtilities.convertPoint( menuBarPlaceholder, -insets.left, -insets.top, rootPane ),
			menuBarPlaceholder.getSize() );

		// add menu bar bottom border insets to bounds so that menu bar overlaps
		// title pane border (menu bar border is painted over title pane border)
		Insets borderInsets = getBorder().getBorderInsets( this );
		bounds.height += borderInsets.bottom;

		// If menu bar is embedded and contains a horizontal glue component,
		// then make the menu bar wider so that it completely overlaps the title label.
		// Since the menu bar is not opaque, the title label is still visible.
		// The title label is moved to the location of the glue component by the layout manager.
		// This allows placing any component on the trailing side of the title pane.
		Component horizontalGlue = findHorizontalGlue( rootPane.getJMenuBar() );
		if( horizontalGlue != null ) {
			boolean leftToRight = getComponentOrientation().isLeftToRight();
			int titleWidth = leftToRight
				? buttonPanel.getX() - (leftPanel.getX() + leftPanel.getWidth())
				: leftPanel.getX() - (buttonPanel.getX() + buttonPanel.getWidth());
			titleWidth = Math.max( titleWidth, 0 ); // title width may be negative
			bounds.width += titleWidth;
			if( !leftToRight )
				bounds.x -= titleWidth;
		}

		return bounds;
	}

	protected Component findHorizontalGlue( JMenuBar menuBar ) {
		if( menuBar == null )
			return null;

		int count = menuBar.getComponentCount();
		for( int i = count - 1; i >= 0; i-- ) {
			Component c = menuBar.getComponent( i );
			if( c instanceof Box.Filler && c.getMaximumSize().width >= Short.MAX_VALUE )
				return c;
		}
		return null;
	}

	protected void menuBarChanged() {
		menuBarPlaceholder.invalidate();

		// necessary for the case that an embedded menu bar is made invisible
		// and a border color is specified
		repaint();

		// update title foreground color
		EventQueue.invokeLater( () -> {
			activeChanged( window == null || window.isActive() );
		} );
	}

	protected void menuBarLayouted() {
		updateNativeTitleBarHeightAndHitTestSpotsLater();
		revalidate();
	}

/*debug
	@Override
	public void paint( Graphics g ) {
		super.paint( g );

		if( debugTitleBarHeight > 0 ) {
			g.setColor( Color.green );
			g.drawLine( 0, debugTitleBarHeight, getWidth(), debugTitleBarHeight );
		}
		if( debugHitTestSpots != null ) {
			g.setColor( Color.blue );
			Point offset = SwingUtilities.convertPoint( this, 0, 0, window );
			for( Rectangle r : debugHitTestSpots )
				g.drawRect( r.x - offset.x, r.y - offset.y, r.width - 1, r.height - 1 );
		}
		if( debugAppIconBounds != null ) {
			g.setColor( Color.red );
			Point offset = SwingUtilities.convertPoint( this, 0, 0, window );
			Rectangle r = debugAppIconBounds;
			g.drawRect( r.x - offset.x, r.y - offset.y, r.width - 1, r.height - 1 );
		}
	}
debug*/

	@Override
	protected void paintComponent( Graphics g ) {
		// not storing value of "TitlePane.unifiedBackground" in class to allow changing at runtime
		if( !UIManager.getBoolean( "TitlePane.unifiedBackground" ) ) {
			g.setColor( getBackground() );
			g.fillRect( 0, 0, getWidth(), getHeight() );
		}
	}

	protected void repaintWindowBorder() {
		int width = rootPane.getWidth();
		int height = rootPane.getHeight();
		Insets insets = rootPane.getInsets();
		rootPane.repaint( 0, 0, width, insets.top ); // top
		rootPane.repaint( 0, 0, insets.left, height ); // left
		rootPane.repaint( 0, height - insets.bottom, width, insets.bottom ); // bottom
		rootPane.repaint( width - insets.right, 0, insets.right, height ); // right
	}

	/**
	 * Iconifies the window.
	 */
	protected void iconify() {
		if( !(window instanceof Frame) )
			return;

		Frame frame = (Frame) window;
		if( !FlatNativeWindowBorder.showWindow( window, FlatNativeWindowBorder.Provider.SW_MINIMIZE ) )
			frame.setExtendedState( frame.getExtendedState() | Frame.ICONIFIED );
	}

	/**
	 * Maximizes the window.
	 */
	protected void maximize() {
		if( !(window instanceof Frame) )
			return;

		Frame frame = (Frame) window;

		updateMaximizedBounds();

		// let our WindowStateListener know that the maximized bounds are up-to-date
		rootPane.putClientProperty( "_flatlaf.maximizedBoundsUpToDate", true );

		// maximize window
		if( !FlatNativeWindowBorder.showWindow( frame, FlatNativeWindowBorder.Provider.SW_MAXIMIZE ) )
			frame.setExtendedState( frame.getExtendedState() | Frame.MAXIMIZED_BOTH );
	}

	protected void updateMaximizedBounds() {
		Frame frame = (Frame) window;

		// set maximized bounds to avoid that maximized window overlaps Windows task bar
		// (if not having native window border and if not modified from the application)
		Rectangle oldMaximizedBounds = frame.getMaximizedBounds();
		if( !hasNativeCustomDecoration() &&
			(oldMaximizedBounds == null ||
			 Objects.equals( oldMaximizedBounds, rootPane.getClientProperty( "_flatlaf.maximizedBounds" ) )) )
		{
			GraphicsConfiguration gc = window.getGraphicsConfiguration();

			// Screen bounds, which may be smaller than physical size on Java 9+.
			// E.g. if running a 3840x2160 screen at 200%, screenBounds.size is 1920x1080.
			// In Java 9+, each screen can have its own scale factor.
			//
			// On Java 8, which does not scale, screenBounds.size of the primary screen
			// is identical to its physical size. But when the primary screen is scaled,
			// then screenBounds.size of secondary screens is scaled with the scale factor
			// of the primary screen.
			// E.g. primary 3840x2160 screen at 150%, secondary 1920x1080 screen at 100%,
			// then screenBounds.size is 3840x2160 on primary and 2880x1560 on secondary.
			Rectangle screenBounds = gc.getBounds();

			int maximizedX = screenBounds.x;
			int maximizedY = screenBounds.y;
			int maximizedWidth = screenBounds.width;
			int maximizedHeight = screenBounds.height;

			if( !isMaximizedBoundsFixed() ) {
				// on Java 8 to 14, maximized x,y are 0,0 based on all screens in a multi-screen environment
				maximizedX = 0;
				maximizedY = 0;

				// scale maximized screen size to get physical screen size for Java 9 to 14
				AffineTransform defaultTransform = gc.getDefaultTransform();
				maximizedWidth = (int) (maximizedWidth * defaultTransform.getScaleX());
				maximizedHeight = (int) (maximizedHeight * defaultTransform.getScaleY());
			}

			// screen insets are in physical size, except for Java 15+
			// (see https://bugs.openjdk.java.net/browse/JDK-8243925)
			// and except for Java 8 on secondary screens where primary screen is scaled
			Insets screenInsets = window.getToolkit().getScreenInsets( gc );

			// maximized bounds are required in physical size, except for Java 15+
			// (see https://bugs.openjdk.java.net/browse/JDK-8231564 and
			//      https://bugs.openjdk.java.net/browse/JDK-8176359)
			// and except for Java 8 on secondary screens where primary screen is scaled
			Rectangle newMaximizedBounds = new Rectangle(
				maximizedX + screenInsets.left,
				maximizedY + screenInsets.top,
				maximizedWidth - screenInsets.left - screenInsets.right,
				maximizedHeight - screenInsets.top - screenInsets.bottom );

			if( !Objects.equals( oldMaximizedBounds, newMaximizedBounds ) ) {
				// change maximized bounds
				frame.setMaximizedBounds( newMaximizedBounds );

				// remember maximized bounds in client property to be able to detect
				// whether maximized bounds are modified from the application
				rootPane.putClientProperty( "_flatlaf.maximizedBounds", newMaximizedBounds );
			}
		}
	}

	/**
	 * Frame.setMaximizedBounds() behaves different on some Java versions after issues
	 *   https://bugs.openjdk.java.net/browse/JDK-8231564 and
	 *   https://bugs.openjdk.java.net/browse/JDK-8176359
	 *   (see also https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8176359)
	 * were fixed in Java 15 and backported to 11.0.8 and 13.0.4.
	 */
	private boolean isMaximizedBoundsFixed() {
		return SystemInfo.isJava_15_orLater ||
			(SystemInfo.javaVersion >= SystemInfo.toVersion( 11, 0, 8, 0 ) &&
			 SystemInfo.javaVersion <  SystemInfo.toVersion( 12, 0, 0, 0 )) ||
			(SystemInfo.javaVersion >= SystemInfo.toVersion( 13, 0, 4, 0 ) &&
			 SystemInfo.javaVersion <  SystemInfo.toVersion( 14, 0, 0, 0 ));
	}

	/**
	 * Restores the window size.
	 */
	protected void restore() {
		if( !(window instanceof Frame) )
			return;

		Frame frame = (Frame) window;
		if( !FlatNativeWindowBorder.showWindow( window, FlatNativeWindowBorder.Provider.SW_RESTORE ) ) {
			int state = frame.getExtendedState();
			frame.setExtendedState( ((state & Frame.ICONIFIED) != 0)
				? (state & ~Frame.ICONIFIED)
				: (state & ~Frame.MAXIMIZED_BOTH) );
		}
	}

	/**
	 * Closes the window.
	 */
	protected void close() {
		if( window != null )
			window.dispatchEvent( new WindowEvent( window, WindowEvent.WINDOW_CLOSING ) );
	}

	private boolean hasJBRCustomDecoration() {
		return window != null && JBRCustomDecorations.hasCustomDecoration( window );
	}

	/**
	 * Returns whether windows uses native window border and has custom decorations enabled.
	 */
	protected boolean hasNativeCustomDecoration() {
		return window != null && FlatNativeWindowBorder.hasCustomDecoration( window );
	}

	protected void updateNativeTitleBarHeightAndHitTestSpotsLater() {
		EventQueue.invokeLater( () -> {
			updateNativeTitleBarHeightAndHitTestSpots();
		} );
	}

	protected void updateNativeTitleBarHeightAndHitTestSpots() {
		if( !isDisplayable() )
			return;

		if( !hasNativeCustomDecoration() )
			return;

		int titleBarHeight = getHeight();
		// slightly reduce height so that component receives mouseExit events
		if( titleBarHeight > 0 )
			titleBarHeight--;

		List<Rectangle> hitTestSpots = new ArrayList<>();
		Rectangle appIconBounds = null;
		if( iconLabel.isVisible() ) {
			// compute real icon size (without insets)
			Point location = SwingUtilities.convertPoint( iconLabel, 0, 0, window );
			Insets iconInsets = iconLabel.getInsets();
			Rectangle iconBounds = new Rectangle(
				location.x + iconInsets.left,
				location.y + iconInsets.top,
				iconLabel.getWidth() - iconInsets.left - iconInsets.right,
				iconLabel.getHeight() - iconInsets.top - iconInsets.bottom );

			if( hasJBRCustomDecoration() )
				hitTestSpots.add( iconBounds );
			else
				appIconBounds = iconBounds;
		}

		Rectangle r = getNativeHitTestSpot( buttonPanel );
		if( r != null )
			hitTestSpots.add( r );

		JMenuBar menuBar = rootPane.getJMenuBar();
		if( hasVisibleEmbeddedMenuBar( menuBar ) ) {
			r = getNativeHitTestSpot( menuBarPlaceholder );
			if( r != null ) {
				Component horizontalGlue = findHorizontalGlue( menuBar );
				if( horizontalGlue != null ) {
					// If menu bar is embedded and contains a horizontal glue component,
					// then split the hit test spot into two spots so that
					// the glue component area can used to move the window.

					Point glueLocation = SwingUtilities.convertPoint( horizontalGlue, 0, 0, window );
					Rectangle r2;
					if( getComponentOrientation().isLeftToRight() ) {
						int trailingWidth = (r.x + r.width - HIT_TEST_SPOT_GROW) - glueLocation.x;
						r.width -= trailingWidth;
						r2 = new Rectangle( glueLocation.x + horizontalGlue.getWidth(), r.y, trailingWidth, r.height );
					} else {
						int leadingWidth = (glueLocation.x + horizontalGlue.getWidth()) - (r.x + HIT_TEST_SPOT_GROW);
						r.x += leadingWidth;
						r.width -= leadingWidth;
						r2 = new Rectangle( glueLocation.x -leadingWidth, r.y, leadingWidth, r.height );
					}
					r2.grow( HIT_TEST_SPOT_GROW, HIT_TEST_SPOT_GROW );
					hitTestSpots.add( r2 );
				}

				hitTestSpots.add( r );
			}
		}

		FlatNativeWindowBorder.setTitleBarHeightAndHitTestSpots( window, titleBarHeight, hitTestSpots, appIconBounds );

/*debug
		debugTitleBarHeight = titleBarHeight;
		debugHitTestSpots = hitTestSpots;
		debugAppIconBounds = appIconBounds;
		repaint();
debug*/
	}

	protected Rectangle getNativeHitTestSpot( JComponent c ) {
		Dimension size = c.getSize();
		if( size.width <= 0 || size.height <= 0 )
			return null;

		Point location = SwingUtilities.convertPoint( c, 0, 0, window );
		Rectangle r = new Rectangle( location, size );
		// slightly increase rectangle so that component receives mouseExit events
		r.grow( HIT_TEST_SPOT_GROW, HIT_TEST_SPOT_GROW );
		return r;
	}

	private static final int HIT_TEST_SPOT_GROW = 2;

/*debug
	private int debugTitleBarHeight;
	private List<Rectangle> debugHitTestSpots;
	private Rectangle debugAppIconBounds;
debug*/

	//---- class FlatTitlePaneBorder ------------------------------------------

	protected class FlatTitlePaneBorder
		extends AbstractBorder
	{
		@Override
		public Insets getBorderInsets( Component c, Insets insets ) {
			super.getBorderInsets( c, insets );

			Border menuBarBorder = getMenuBarBorder();
			if( menuBarBorder != null ) {
				// if menu bar is embedded, add bottom insets of menu bar border
				Insets menuBarInsets = menuBarBorder.getBorderInsets( c );
				insets.bottom += menuBarInsets.bottom;
			} else if( borderColor != null && (rootPane.getJMenuBar() == null || !rootPane.getJMenuBar().isVisible()) )
				insets.bottom += UIScale.scale( 1 );

			if( hasNativeCustomDecoration() )
				insets = FlatUIUtils.addInsets( insets, WindowTopBorder.getInstance().getBorderInsets() );

			return insets;
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			// paint bottom border
			Border menuBarBorder = getMenuBarBorder();
			if( menuBarBorder != null ) {
				// if menu bar is embedded, paint menu bar border
				menuBarBorder.paintBorder( c, g, x, y, width, height );
			} else if( borderColor != null && (rootPane.getJMenuBar() == null || !rootPane.getJMenuBar().isVisible()) ) {
				// paint border between title pane and content if border color is specified
				float lineHeight = UIScale.scale( (float) 1 );
				FlatUIUtils.paintFilledRectangle( g, borderColor, x, y + height - lineHeight, width, lineHeight );
			}

			if( hasNativeCustomDecoration() )
				WindowTopBorder.getInstance().paintBorder( c, g, x, y, width, height );
		}

		protected Border getMenuBarBorder() {
			JMenuBar menuBar = rootPane.getJMenuBar();
			return hasVisibleEmbeddedMenuBar( menuBar ) ? menuBar.getBorder() : null;
		}
	}

	//---- class FlatTitleLabelUI ---------------------------------------------

	/**
	 * @since 1.1
	 */
	protected class FlatTitleLabelUI
		extends FlatLabelUI
	{
		@Override
		protected void paintEnabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
			boolean hasEmbeddedMenuBar = hasVisibleEmbeddedMenuBar( rootPane.getJMenuBar() );
			int labelWidth = l.getWidth();
			int textWidth = labelWidth - (textX * 2);
			int gap = UIScale.scale( menuBarTitleGap );

			// The passed in textX coordinate is always to horizontally center the text within the label bounds.
			// Modify textX so that the text is painted either centered within the window bounds or leading aligned.
			boolean center = hasEmbeddedMenuBar ? centerTitleIfMenuBarEmbedded : centerTitle;
			if( center ) {
				// If window is wide enough, center title within window bounds.
				// Otherwise leave it centered within free space (label bounds).
				int centeredTextX = ((l.getParent().getWidth() - textWidth) / 2) - l.getX();
				if( centeredTextX >= gap && centeredTextX + textWidth <= labelWidth - gap )
					textX = centeredTextX;
			} else {
				// leading aligned
				boolean leftToRight = getComponentOrientation().isLeftToRight();
				Insets insets = l.getInsets();
				int leadingInset = hasEmbeddedMenuBar ? gap : (leftToRight ? insets.left : insets.right);
				int leadingTextX = leftToRight ? leadingInset : labelWidth - leadingInset - textWidth;
				if( leftToRight ? leadingTextX < textX : leadingTextX > textX )
					textX = leadingTextX;
			}

			super.paintEnabledText( l, g, s, textX, textY );
		}
	}

	//---- class Handler ------------------------------------------------------

	protected class Handler
		extends WindowAdapter
		implements PropertyChangeListener, MouseListener, MouseMotionListener, ComponentListener
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

				case "componentOrientation":
					updateNativeTitleBarHeightAndHitTestSpotsLater();
					break;
			}
		}

		//---- interface WindowListener ----

		@Override
		public void windowActivated( WindowEvent e ) {
			activeChanged( true );
			updateNativeTitleBarHeightAndHitTestSpots();

			if( hasNativeCustomDecoration() )
				WindowTopBorder.getInstance().repaintBorder( FlatTitlePane.this );

			repaintWindowBorder();
		}

		@Override
		public void windowDeactivated( WindowEvent e ) {
			activeChanged( false );
			updateNativeTitleBarHeightAndHitTestSpots();

			if( hasNativeCustomDecoration() )
				WindowTopBorder.getInstance().repaintBorder( FlatTitlePane.this );

			repaintWindowBorder();
		}

		@Override
		public void windowStateChanged( WindowEvent e ) {
			frameStateChanged();
			updateNativeTitleBarHeightAndHitTestSpots();
		}

		//---- interface MouseListener ----

		private Point dragOffset;

		@Override
		public void mouseClicked( MouseEvent e ) {
			if( e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton( e ) ) {
				if( e.getSource() == iconLabel ) {
					// double-click on icon closes window
					close();
				} else if( !hasNativeCustomDecoration() &&
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
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			if( window == null )
				return; // should newer occur

			dragOffset = SwingUtilities.convertPoint( FlatTitlePane.this, e.getPoint(), window );
		}

		@Override public void mouseReleased( MouseEvent e ) {}
		@Override public void mouseEntered( MouseEvent e ) {}
		@Override public void mouseExited( MouseEvent e ) {}

		//---- interface MouseMotionListener ----

		@Override
		public void mouseDragged( MouseEvent e ) {
			if( window == null )
				return; // should newer occur

			if( hasNativeCustomDecoration() )
				return; // do nothing if having native window border

			// restore window if it is maximized
			if( window instanceof Frame ) {
				Frame frame = (Frame) window;
				int state = frame.getExtendedState();
				if( (state & Frame.MAXIMIZED_BOTH) != 0 ) {
					int maximizedWidth = window.getWidth();

					// restore window size, which also moves window to pre-maximized location
					frame.setExtendedState( state & ~Frame.MAXIMIZED_BOTH );

					// fix drag offset to ensure that window remains under mouse position
					// for the case that dragging starts in the right area of the maximized window
					int restoredWidth = window.getWidth();
					int center = restoredWidth / 2;
					if( dragOffset.x > center ) {
						// this is same/similar to what Windows 10 does
						if( dragOffset.x > maximizedWidth - center )
							dragOffset.x = restoredWidth - (maximizedWidth - dragOffset.x);
						else
							dragOffset.x = center;
					}
				}
			}

			// compute new window location
			int newX = e.getXOnScreen() - dragOffset.x;
			int newY = e.getYOnScreen() - dragOffset.y;

			if( newX == window.getX() && newY == window.getY() )
				return;

			// move window
			window.setLocation( newX, newY );
		}

		@Override public void mouseMoved( MouseEvent e ) {}

		//---- interface ComponentListener ----

		@Override
		public void componentResized( ComponentEvent e ) {
			updateNativeTitleBarHeightAndHitTestSpotsLater();
		}

		@Override
		public void componentShown( ComponentEvent e ) {
			// necessary for the case that the frame is maximized before it is shown
			frameStateChanged();
		}

		@Override public void componentMoved( ComponentEvent e ) {}
		@Override public void componentHidden( ComponentEvent e ) {}
	}
}
