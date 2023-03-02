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

import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.FlatNativeWindowBorder.WindowTopBorder;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF title bar.
 *
 * @uiDefault TitlePane.font								Font
 * @uiDefault TitlePane.background							Color
 * @uiDefault TitlePane.inactiveBackground					Color
 * @uiDefault TitlePane.foreground							Color
 * @uiDefault TitlePane.inactiveForeground					Color
 * @uiDefault TitlePane.embeddedForeground					Color
 * @uiDefault TitlePane.borderColor							Color	optional
 * @uiDefault TitlePane.unifiedBackground					boolean
 * @uiDefault TitlePane.showIcon							boolean
 * @uiDefault TitlePane.showIconInDialogs					boolean
 * @uiDefault TitlePane.noIconLeftGap						int
 * @uiDefault TitlePane.iconSize							Dimension
 * @uiDefault TitlePane.iconMargins							Insets
 * @uiDefault TitlePane.titleMargins						Insets
 * @uiDefault TitlePane.menuBarEmbedded						boolean
 * @uiDefault TitlePane.titleMinimumWidth					int
 * @uiDefault TitlePane.buttonMinimumWidth					int
 * @uiDefault TitlePane.buttonMaximizedHeight				int
 * @uiDefault TitlePane.centerTitle							boolean
 * @uiDefault TitlePane.centerTitleIfMenuBarEmbedded		boolean
 * @uiDefault TitlePane.showIconBesideTitle					boolean
 * @uiDefault TitlePane.menuBarTitleGap						int
 * @uiDefault TitlePane.menuBarResizeHeight					int
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
	private static final String KEY_DEBUG_SHOW_RECTANGLES = "FlatLaf.debug.titlebar.showRectangles";

	/** @since 2.5 */ protected final Font titleFont = UIManager.getFont( "TitlePane.font" );
	protected final Color activeBackground = UIManager.getColor( "TitlePane.background" );
	protected final Color inactiveBackground = UIManager.getColor( "TitlePane.inactiveBackground" );
	protected final Color activeForeground = UIManager.getColor( "TitlePane.foreground" );
	protected final Color inactiveForeground = UIManager.getColor( "TitlePane.inactiveForeground" );
	protected final Color embeddedForeground = UIManager.getColor( "TitlePane.embeddedForeground" );
	protected final Color borderColor = UIManager.getColor( "TitlePane.borderColor" );

	/** @since 2 */ protected final boolean showIcon = FlatUIUtils.getUIBoolean( "TitlePane.showIcon", true );
	/** @since 2.5 */ protected final boolean showIconInDialogs = FlatUIUtils.getUIBoolean( "TitlePane.showIconInDialogs", true );
	/** @since 2 */ protected final int noIconLeftGap = FlatUIUtils.getUIInt( "TitlePane.noIconLeftGap", 8 );
	protected final Dimension iconSize = UIManager.getDimension( "TitlePane.iconSize" );
	/** @since 2.4 */ protected final int titleMinimumWidth = FlatUIUtils.getUIInt( "TitlePane.titleMinimumWidth", 60 );
	/** @since 2.4 */ protected final int buttonMinimumWidth = FlatUIUtils.getUIInt( "TitlePane.buttonMinimumWidth", 30 );
	protected final int buttonMaximizedHeight = UIManager.getInt( "TitlePane.buttonMaximizedHeight" );
	protected final boolean centerTitle = UIManager.getBoolean( "TitlePane.centerTitle" );
	protected final boolean centerTitleIfMenuBarEmbedded = FlatUIUtils.getUIBoolean( "TitlePane.centerTitleIfMenuBarEmbedded", true );
	/** @since 2.4 */ protected final boolean showIconBesideTitle = UIManager.getBoolean( "TitlePane.showIconBesideTitle" );
	protected final int menuBarTitleGap = FlatUIUtils.getUIInt( "TitlePane.menuBarTitleGap", 40 );
	/** @since 2.4 */ protected final int menuBarTitleMinimumGap = FlatUIUtils.getUIInt( "TitlePane.menuBarTitleMinimumGap", 12 );
	/** @since 2.4 */ protected final int menuBarResizeHeight = FlatUIUtils.getUIInt( "TitlePane.menuBarResizeHeight", 4 );

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

		applyComponentOrientation( rootPane.getComponentOrientation() );
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
				// compute available bounds
				Insets insets = target.getInsets();
				int x = insets.left;
				int y = insets.top;
				int w = target.getWidth() - insets.left - insets.right;
				int h = target.getHeight() - insets.top - insets.bottom;

				// compute widths
				int leftWidth = leftPanel.getPreferredSize().width;
				int buttonsWidth = buttonPanel.getPreferredSize().width;
				int titleWidth = w - leftWidth - buttonsWidth;
				int minTitleWidth = UIScale.scale( titleMinimumWidth );

				// increase minimum width if icon is show besides the title
				Icon icon = titleLabel.getIcon();
				if( icon != null ) {
					Insets iconInsets = iconLabel.getInsets();
					int iconTextGap = titleLabel.getComponentOrientation().isLeftToRight() ? iconInsets.right : iconInsets.left;
					minTitleWidth += icon.getIconWidth() + iconTextGap;
				}

				// if title is too small, reduce width of buttons
				if( titleWidth < minTitleWidth ) {
					buttonsWidth = Math.max( buttonsWidth - (minTitleWidth - titleWidth), buttonPanel.getMinimumSize().width );
					titleWidth = w - leftWidth - buttonsWidth;
				}

				// if title is still too small, reduce width of left panel (icon and embedded menu bar)
				if( titleWidth < minTitleWidth ) {
					int minLeftWidth = iconLabel.isVisible()
						? iconLabel.getWidth() - iconLabel.getInsets().right
						: UIScale.scale( noIconLeftGap );
					leftWidth = Math.max( leftWidth - (minTitleWidth - titleWidth), minLeftWidth );
					titleWidth = w - leftWidth - buttonsWidth;
				}

				if( target.getComponentOrientation().isLeftToRight() ) {
					// left-to-right
					leftPanel.setBounds( x, y, leftWidth, h );
					titleLabel.setBounds( x + leftWidth, y, titleWidth, h );
					buttonPanel.setBounds( x + leftWidth + titleWidth, y, buttonsWidth, h );
				} else {
					// right-to-left
					buttonPanel.setBounds( x, y, buttonsWidth, h );
					titleLabel.setBounds( x + buttonsWidth, y, titleWidth, h );
					leftPanel.setBounds( x + buttonsWidth + titleWidth, y, leftWidth, h );
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

		// initially hide buttons that are only supported in frames
		iconifyButton.setVisible( false );
		maximizeButton.setVisible( false );
		restoreButton.setVisible( false );

		buttonPanel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				if( buttonMaximizedHeight > 0 && isWindowMaximized() && !hasVisibleEmbeddedMenuBar( rootPane.getJMenuBar() ) ) {
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
			// so we add the iconify/maximize/restore buttons, and they are shown
			// later in frameStateChanged(), which is invoked from addNotify()

			buttonPanel.add( iconifyButton );
			buttonPanel.add( maximizeButton );
			buttonPanel.add( restoreButton );
		}
		buttonPanel.add( closeButton );
	}

	protected JButton createButton( String iconKey, String accessibleName, ActionListener action ) {
		JButton button = new JButton( UIManager.getIcon( iconKey ) ) {
			@Override
			public Dimension getMinimumSize() {
				// allow the button to shrink if space is rare
				return new Dimension( UIScale.scale( buttonMinimumWidth ), super.getMinimumSize().height );
			}
		};
		button.setFocusable( false );
		button.setContentAreaFilled( false );
		button.setBorder( BorderFactory.createEmptyBorder() );
		button.putClientProperty( AccessibleContext.ACCESSIBLE_NAME_PROPERTY, accessibleName );
		button.addActionListener( action );
		return button;
	}

	protected void activeChanged( boolean active ) {
		Color background = clientPropertyColor( rootPane, TITLE_BAR_BACKGROUND, null );
		Color foreground = clientPropertyColor( rootPane, TITLE_BAR_FOREGROUND, null );
		Color titleForeground = foreground;
		if( background == null )
			background = FlatUIUtils.nonUIResource( active ? activeBackground : inactiveBackground );
		if( foreground == null ) {
			foreground = FlatUIUtils.nonUIResource( active ? activeForeground : inactiveForeground );
			titleForeground = (active && hasVisibleEmbeddedMenuBar( rootPane.getJMenuBar() ))
				? FlatUIUtils.nonUIResource( embeddedForeground )
				: foreground;
		}

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

		updateVisibility();

		if( window instanceof Frame ) {
			Frame frame = (Frame) window;

			if( isWindowMaximized() &&
				!(SystemInfo.isLinux && FlatNativeLinuxLibrary.isWMUtilsSupported( window )) &&
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
		}
	}

	/** @since 3 */
	protected void updateVisibility() {
		titleLabel.setVisible( clientPropertyBoolean( rootPane, TITLE_BAR_SHOW_TITLE, true ) );
		closeButton.setVisible( clientPropertyBoolean( rootPane, TITLE_BAR_SHOW_CLOSE, true ) );

		if( window instanceof Frame ) {
			Frame frame = (Frame) window;
			boolean maximizable = frame.isResizable() && clientPropertyBoolean( rootPane, TITLE_BAR_SHOW_MAXIMIZE, true );
			boolean maximized = isWindowMaximized();

			iconifyButton.setVisible( clientPropertyBoolean( rootPane, TITLE_BAR_SHOW_ICONIFFY, true ) );
			maximizeButton.setVisible( maximizable && !maximized );
			restoreButton.setVisible( maximizable && maximized );
		} else {
			// hide buttons because they are only supported in frames
			iconifyButton.setVisible( false );
			maximizeButton.setVisible( false );
			restoreButton.setVisible( false );
		}
	}

	protected void updateIcon() {
		boolean defaultShowIcon = showIcon;
		if( !showIconInDialogs && rootPane.getParent() instanceof JDialog )
			defaultShowIcon = false;

		// get window images
		List<Image> images = null;
		if( clientPropertyBoolean( rootPane, TITLE_BAR_SHOW_ICON, defaultShowIcon ) ) {
			images = window.getIconImages();
			if( images.isEmpty() ) {
				// search in owners
				for( Window owner = window.getOwner(); owner != null; owner = owner.getOwner() ) {
					images = owner.getIconImages();
					if( !images.isEmpty() )
						break;
				}
			}
		}

		boolean hasIcon = (images != null && !images.isEmpty());

		// set icon
		iconLabel.setIcon( hasIcon && !showIconBesideTitle ? new FlatTitlePaneIcon( images, iconSize ) : null );
		titleLabel.setIcon( hasIcon && showIconBesideTitle ? new FlatTitlePaneIcon( images, iconSize ) : null );

		// show/hide icon
		iconLabel.setVisible( hasIcon && !showIconBesideTitle );
		leftPanel.setBorder( hasIcon && !showIconBesideTitle ? null : FlatUIUtils.nonUIResource( new FlatEmptyBorder( 0, noIconLeftGap, 0, 0 ) ) );

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
	 * Returns whether this title pane currently has a visible and embedded menubar.
	 */
	protected boolean hasVisibleEmbeddedMenuBar( JMenuBar menuBar ) {
		return menuBar != null && menuBar.isVisible() && isMenuBarEmbedded();
	}

	/**
	 * Returns whether the menubar should be embedded into the title pane.
	 */
	protected boolean isMenuBarEmbedded() {
		// not storing value of "TitlePane.menuBarEmbedded" in class to allow changing at runtime
		return FlatUIUtils.getBoolean( rootPane,
			FlatSystemProperties.MENUBAR_EMBEDDED,
			FlatClientProperties.MENU_BAR_EMBEDDED,
			"TitlePane.menuBarEmbedded",
			false );
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

	protected void titleBarColorsChanged() {
		activeChanged( window == null || window.isActive() );
		repaint();
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
		doLayout();
	}

	@Override
	public void paint( Graphics g ) {
		super.paint( g );

		if( !UIManager.getBoolean( KEY_DEBUG_SHOW_RECTANGLES ) )
			return;

		if( debugTitleBarHeight > 0 ) {
			g.setColor( Color.green );
			g.drawLine( 0, debugTitleBarHeight, getWidth(), debugTitleBarHeight );
		}
		if( debugHitTestSpots != null ) {
			for( Rectangle r : debugHitTestSpots )
				paintRect( g, Color.red, r );
		}
		paintRect( g, Color.cyan, debugCloseButtonBounds );
		paintRect( g, Color.blue, debugAppIconBounds );
		paintRect( g, Color.blue, debugMinimizeButtonBounds );
		paintRect( g, Color.magenta, debugMaximizeButtonBounds );
		paintRect( g, Color.cyan, debugCloseButtonBounds );
	}

	private void paintRect( Graphics g, Color color, Rectangle r ) {
		if( r == null )
			return;

		g.setColor( color );
		Point offset = SwingUtilities.convertPoint( this, 0, 0, window );
		g.drawRect( r.x - offset.x, r.y - offset.y, r.width - 1, r.height - 1 );
	}

	@Override
	protected void paintComponent( Graphics g ) {
		// not storing value of "TitlePane.unifiedBackground" in class to allow changing at runtime
		g.setColor( (UIManager.getBoolean( "TitlePane.unifiedBackground" ) &&
				clientPropertyColor( rootPane, TITLE_BAR_BACKGROUND, null ) == null)
			? FlatUIUtils.getParentBackground( this )
			: getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
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

	/** @since 2.4 */
	protected boolean isWindowMaximized() {
		// Windows and macOS use always MAXIMIZED_BOTH.
		// Only Linux uses MAXIMIZED_VERT and MAXIMIZED_HORIZ (when dragging window to left or right edge).
		// (searched jdk source code)
		return window instanceof Frame && (((Frame)window).getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
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
		if( !FlatNativeWindowBorder.showWindow( frame, FlatNativeWindowBorder.Provider.SW_MAXIMIZE ) ) {
			int oldState = frame.getExtendedState();
			int newState = oldState | Frame.MAXIMIZED_BOTH;

			if( SystemInfo.isLinux ) {
				// Linux supports vertical and horizontal maximization:
				//   - dragging a window to left or right edge of screen vertically maximizes
				//     the window to the left or right half of the screen
				//   - don't know whether user can do horizontal maximization
				// (Windows and macOS use only MAXIMIZED_BOTH)
				//
				// If a window is maximized vertically or horizontally (but not both),
				// then Frame.setExtendedState() behaves not as expected on Linux.
				// E.g. if window state is MAXIMIZED_VERT, calling setExtendedState(MAXIMIZED_BOTH)
				// changes state to MAXIMIZED_HORIZ. But calling setExtendedState(MAXIMIZED_HORIZ)
				// changes state from MAXIMIZED_VERT to MAXIMIZED_BOTH.
				// Seems to be a bug in sun.awt.X11.XNETProtocol.requestState(),
				// which does some strange state XOR-ing...
				if( (oldState & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_VERT )
					newState = oldState & ~Frame.MAXIMIZED_BOTH | Frame.MAXIMIZED_HORIZ;
			}

			frame.setExtendedState( newState );
		}
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

			if( SystemInfo.isWindows && !isMaximizedBoundsFixed() ) {
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

	private void maximizeOrRestore() {
		if( !(window instanceof Frame) || !((Frame)window).isResizable() )
			return;

		if( isWindowMaximized() )
			restore();
		else
			maximize();
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

	// used to invoke updateNativeTitleBarHeightAndHitTestSpots() only once from latest invokeLater()
	private int laterCounter;

	protected void updateNativeTitleBarHeightAndHitTestSpotsLater() {
		laterCounter++;
		EventQueue.invokeLater( () -> {
			laterCounter--;
			if( laterCounter == 0 )
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

		if( !showIconBesideTitle && iconLabel.isVisible() ) {
			// compute real icon size (without insets; 1px larger for easier hitting)
			Point location = SwingUtilities.convertPoint( iconLabel, 0, 0, window );
			Insets iconInsets = iconLabel.getInsets();
			Rectangle iconBounds = new Rectangle(
				location.x + iconInsets.left - 1,
				location.y + iconInsets.top - 1,
				iconLabel.getWidth() - iconInsets.left - iconInsets.right + 2,
				iconLabel.getHeight() - iconInsets.top - iconInsets.bottom + 2 );

			// if frame is maximized, increase icon bounds to upper-left corner
			// of window to allow closing window via double-click in upper-left corner
			if( isWindowMaximized() ) {
				iconBounds.height += iconBounds.y;
				iconBounds.y = 0;

				if( window.getComponentOrientation().isLeftToRight() ) {
					iconBounds.width += iconBounds.x;
					iconBounds.x = 0;
				} else
					iconBounds.width += iconInsets.right;
			}

			if( hasJBRCustomDecoration() )
				hitTestSpots.add( iconBounds );
			else
				appIconBounds = iconBounds;
		} else if( showIconBesideTitle && titleLabel.getIcon() != null && titleLabel.getUI() instanceof FlatTitleLabelUI ) {
			FlatTitleLabelUI ui = (FlatTitleLabelUI) titleLabel.getUI();

			// compute real icon bounds
			Insets insets = titleLabel.getInsets();
			Rectangle viewR = new Rectangle( insets.left, insets.top,
				titleLabel.getWidth() - insets.left - insets.right,
				titleLabel.getHeight() - insets.top - insets.bottom );
			Rectangle iconR = new Rectangle();
			Rectangle textR = new Rectangle();
			ui.layoutCL( titleLabel, titleLabel.getFontMetrics( titleLabel.getFont() ),
				titleLabel.getText(), titleLabel.getIcon(),
				viewR, iconR, textR );

			// Windows shows the window system menu only in the upper-left corner
			if( iconR.x == 0 ) {
				// convert icon location to window coordinates
				Point location = SwingUtilities.convertPoint( titleLabel, 0, 0, window );
				iconR.x += location.x;
				iconR.y += location.y;

				// make icon bounds 1px larger for easier hitting
				iconR.x -= 1;
				iconR.y -= 1;
				iconR.width += 2;
				iconR.height += 2;

				if( hasJBRCustomDecoration() )
					hitTestSpots.add( iconR );
				else
					appIconBounds = iconR;
			}
		}

		Rectangle r = getNativeHitTestSpot( buttonPanel );
		if( r != null )
			hitTestSpots.add( r );

		JMenuBar menuBar = rootPane.getJMenuBar();
		if( hasVisibleEmbeddedMenuBar( menuBar ) ) {
			r = getNativeHitTestSpot( menuBar );
			if( r != null ) {
				// if frame is resizable and not maximized, make menu bar hit test spot smaller at top
				// to have a small area above the menu bar to resize the window
				if( window instanceof Frame && ((Frame)window).isResizable() && !isWindowMaximized() ) {
					// limit to 8, because Windows does not use a larger height
					int resizeHeight = UIScale.scale( Math.min( menuBarResizeHeight, 8 ) );
					r.y += resizeHeight;
					r.height -= resizeHeight;
				}

				int count = menuBar.getComponentCount();
				for( int i = count - 1; i >= 0; i-- ) {
					Component c = menuBar.getComponent( i );
					if( c instanceof Box.Filler ||
						(c instanceof JComponent && clientPropertyBoolean( (JComponent) c, COMPONENT_TITLE_BAR_CAPTION, false ) ) )
					{
						// If menu bar is embedded and contains a horizontal glue or caption component,
						// then split the hit test spot so that
						// the glue/caption component area can be used to move the window.

						Point glueLocation = SwingUtilities.convertPoint( c, 0, 0, window );
						int x2 = glueLocation.x + c.getWidth();
						Rectangle r2;
						if( getComponentOrientation().isLeftToRight() ) {
							r2 = new Rectangle( x2, r.y, (r.x + r.width) - x2, r.height );

							r.width = glueLocation.x - r.x;
						} else {
							r2 = new Rectangle( r.x, r.y, glueLocation.x - r.x, r.height );

							r.width = (r.x + r.width) - x2;
							r.x = x2;
						}
						if( r2.width > 0 )
							hitTestSpots.add( r2 );
					}
				}

				hitTestSpots.add( r );
			}
		}

		Rectangle minimizeButtonBounds = boundsInWindow( iconifyButton );
		Rectangle maximizeButtonBounds = boundsInWindow( maximizeButton.isVisible() ? maximizeButton : restoreButton );
		Rectangle closeButtonBounds = boundsInWindow( closeButton );

		FlatNativeWindowBorder.setTitleBarHeightAndHitTestSpots( window, titleBarHeight,
			hitTestSpots, appIconBounds, minimizeButtonBounds, maximizeButtonBounds, closeButtonBounds );

		debugTitleBarHeight = titleBarHeight;
		debugHitTestSpots = hitTestSpots;
		debugAppIconBounds = appIconBounds;
		debugMinimizeButtonBounds = minimizeButtonBounds;
		debugMaximizeButtonBounds = maximizeButtonBounds;
		debugCloseButtonBounds = closeButtonBounds;
		if( UIManager.getBoolean( KEY_DEBUG_SHOW_RECTANGLES ) )
			repaint();
	}

	private Rectangle boundsInWindow( JComponent c ) {
		return c.isShowing()
			? SwingUtilities.convertRectangle( c.getParent(), c.getBounds(), window )
			: null;
	}

	protected Rectangle getNativeHitTestSpot( JComponent c ) {
		Dimension size = c.getSize();
		if( size.width <= 0 || size.height <= 0 )
			return null;

		Point location = SwingUtilities.convertPoint( c, 0, 0, window );
		Rectangle r = new Rectangle( location, size );
		return r;
	}

	private int debugTitleBarHeight;
	private List<Rectangle> debugHitTestSpots;
	private Rectangle debugAppIconBounds;
	private Rectangle debugMinimizeButtonBounds;
	private Rectangle debugMaximizeButtonBounds;
	private Rectangle debugCloseButtonBounds;

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

			if( !SystemInfo.isWindows_11_orLater && hasNativeCustomDecoration() && !isWindowMaximized() )
				insets = FlatUIUtils.addInsets( insets, WindowTopBorder.getInstance().getBorderInsets() );

			return insets;
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			// paint bottom border
			Border menuBarBorder = getMenuBarBorder();
			if( menuBarBorder != null ) {
				// if menu bar is embedded, paint menu bar border
				menuBarBorder.paintBorder( rootPane.getJMenuBar(), g, x, y, width, height );
			} else if( borderColor != null && (rootPane.getJMenuBar() == null || !rootPane.getJMenuBar().isVisible()) ) {
				// paint border between title pane and content if border color is specified
				float lineHeight = UIScale.scale( (float) 1 );
				FlatUIUtils.paintFilledRectangle( g, borderColor, x, y + height - lineHeight, width, lineHeight );
			}

			if( !SystemInfo.isWindows_11_orLater && hasNativeCustomDecoration() && !isWindowMaximized() )
				WindowTopBorder.getInstance().paintBorder( c, g, x, y, width, height );
		}

		protected Border getMenuBarBorder() {
			JMenuBar menuBar = rootPane.getJMenuBar();
			return hasVisibleEmbeddedMenuBar( menuBar ) ? menuBar.getBorder() : null;
		}
	}

	//---- class FlatTitleLabelUI ---------------------------------------------

	/** @since 1.1 */
	protected class FlatTitleLabelUI
		extends FlatLabelUI
	{
		protected FlatTitleLabelUI() {
			super( false );
		}

		@Override
		protected void installDefaults( JLabel c ) {
			super.installDefaults( c );

			if( titleFont != null )
				c.setFont( titleFont );
		}

		@Override
		protected String layoutCL( JLabel label, FontMetrics fontMetrics, String text, Icon icon,
			Rectangle viewR, Rectangle iconR, Rectangle textR )
		{
			JMenuBar menuBar = rootPane.getJMenuBar();
			boolean hasEmbeddedMenuBar = hasVisibleEmbeddedMenuBar( menuBar );
			boolean hasEmbeddedLeadingMenus = hasEmbeddedMenuBar && hasLeadingMenus( menuBar );
			boolean leftToRight = getComponentOrientation().isLeftToRight();

			if( hasEmbeddedMenuBar ) {
				int minGap = UIScale.scale( menuBarTitleMinimumGap );

				// apply minimum leading gap (between embedded menu bar and title)
				if( hasEmbeddedLeadingMenus ) {
					if( leftToRight )
						viewR.x += minGap;
					viewR.width -= minGap;
				}

				// apply minimum trailing gap (between title and right aligned components of embedded menu bar)
				Component horizontalGlue = findHorizontalGlue( menuBar );
				if( horizontalGlue != null && menuBar.getComponent( menuBar.getComponentCount() - 1 ) != horizontalGlue ) {
					if( !leftToRight )
						viewR.x += minGap;
					viewR.width -= minGap;
				}
			}

			// compute icon width and gap (if icon is show besides the title)
			int iconTextGap = 0;
			int iconWidthAndGap = 0;
			if( icon != null ) {
				Insets iconInsets = iconLabel.getInsets();
				iconTextGap = leftToRight ? iconInsets.right : iconInsets.left;
				iconWidthAndGap = icon.getIconWidth() + iconTextGap;
			}

			// layout title and icon (if show besides the title)
			String clippedText = SwingUtilities.layoutCompoundLabel( label, fontMetrics, text, icon,
				label.getVerticalAlignment(), label.getHorizontalAlignment(),
				label.getVerticalTextPosition(), label.getHorizontalTextPosition(),
				viewR, iconR, textR,
				iconTextGap );

			// compute text X location
			if( !clippedText.equals( text ) ) {
				// if text is clipped, align to left (or right)
				textR.x = leftToRight
					? viewR.x + iconWidthAndGap
					: viewR.x + viewR.width - iconWidthAndGap - textR.width;
			} else {
				int leadingGap = hasEmbeddedLeadingMenus ? UIScale.scale( menuBarTitleGap - menuBarTitleMinimumGap ) : 0;

				boolean center = hasEmbeddedLeadingMenus ? centerTitleIfMenuBarEmbedded : centerTitle;
				if( center ) {
					// If window is wide enough, center title within window bounds.
					// Otherwise, center within free space (label bounds).
					Container parent = label.getParent();
					int centeredTextX = (parent != null) ? ((parent.getWidth() - textR.width - iconWidthAndGap) / 2) + iconWidthAndGap - label.getX() : -1;
					textR.x = (centeredTextX >= viewR.x + leadingGap && centeredTextX + textR.width <= viewR.x + viewR.width - leadingGap)
						? centeredTextX
						: viewR.x + ((viewR.width - textR.width - iconWidthAndGap) / 2) + iconWidthAndGap;
				} else {
					// leading aligned with leading gap, which is reduced if space is rare
					textR.x = leftToRight
						? Math.min( viewR.x + leadingGap + iconWidthAndGap, viewR.x + viewR.width - textR.width )
						: Math.max( viewR.x + viewR.width - leadingGap - iconWidthAndGap - textR.width, viewR.x );
				}
			}

			// compute icon X location (relative to text X location)
			if( icon != null ) {
				iconR.x = leftToRight
					? textR.x - iconWidthAndGap
					: textR.x + textR.width + iconTextGap;
			}

			return clippedText;
		}

		private boolean hasLeadingMenus( JMenuBar menuBar ) {
			// check whether menu bar is empty
			if( menuBar.getComponentCount() == 0 || menuBar.getWidth() == 0 )
				return false;

			// check whether menu bar has a leading glue component
			// (no menus/components at left side)
			Component horizontalGlue = findHorizontalGlue( menuBar );
			if( horizontalGlue != null ) {
				boolean leftToRight = getComponentOrientation().isLeftToRight();
				if( (leftToRight && horizontalGlue.getX() == 0) ||
					(!leftToRight && horizontalGlue.getX() + horizontalGlue.getWidth() == menuBar.getWidth()) )
				  return false;
			}

			return true;
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

			if( !SystemInfo.isWindows_11_orLater && hasNativeCustomDecoration() )
				WindowTopBorder.getInstance().repaintBorder( FlatTitlePane.this );

			repaintWindowBorder();
		}

		@Override
		public void windowDeactivated( WindowEvent e ) {
			activeChanged( false );
			updateNativeTitleBarHeightAndHitTestSpots();

			if( !SystemInfo.isWindows_11_orLater && hasNativeCustomDecoration() )
				WindowTopBorder.getInstance().repaintBorder( FlatTitlePane.this );

			repaintWindowBorder();
		}

		@Override
		public void windowStateChanged( WindowEvent e ) {
/*debug
			System.out.println( "state " + e.getOldState() + " -> " + e.getNewState() + "     "
				+ ((e.getNewState() & Frame.MAXIMIZED_HORIZ) != 0 ? " HORIZ" : "")
				+ ((e.getNewState() & Frame.MAXIMIZED_VERT) != 0 ? " VERT" : "")
			);
debug*/

			frameStateChanged();
			updateNativeTitleBarHeightAndHitTestSpots();
		}

		//---- interface MouseListener ----

		private Point dragOffset;
		private boolean linuxNativeMove;
		private long lastSingleClickWhen;

		@Override
		public void mouseClicked( MouseEvent e ) {
			// on Linux, when using native library, the mouse clicked event
			// is usually not sent and maximize/restore is done in mouse pressed event
			// this check is here for the case that a mouse clicked event comes thru for some reason
			if( linuxNativeMove && SystemInfo.isLinux && FlatNativeLinuxLibrary.isWMUtilsSupported( window ) ) {
				// see comment in mousePressed()
				if( lastSingleClickWhen != 0 && (e.getWhen() - lastSingleClickWhen) <= getMultiClickInterval() ) {
					lastSingleClickWhen = 0;
					maximizeOrRestore();
				}
				return;
			}

			if( e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton( e ) ) {
				if( e.getSource() == iconLabel ) {
					// double-click on icon closes window
					close();
				} else if( !hasNativeCustomDecoration() ) {
					// maximize/restore on double-click
					maximizeOrRestore();
				}
			}
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			if( window == null )
				return; // should newer occur

			// on Linux, show window menu
			if( SwingUtilities.isRightMouseButton( e ) &&
				SystemInfo.isLinux && FlatNativeLinuxLibrary.isWMUtilsSupported( window ) )
			{
				e.consume();
				FlatNativeLinuxLibrary.showWindowMenu( window, e );
				return;
			}

			if( !SwingUtilities.isLeftMouseButton( e ) )
				return;

			dragOffset = SwingUtilities.convertPoint( FlatTitlePane.this, e.getPoint(), window );
			linuxNativeMove = false;

			// on Linux, move or maximize/restore window
			if( SystemInfo.isLinux && FlatNativeLinuxLibrary.isWMUtilsSupported( window ) ) {
				// The fired Java mouse events, when doing a double-click and the first click
				// sends a _NET_WM_MOVERESIZE message, are different for various Linux distributions:
				//   CentOS 7      (GNOME 3.28.2, X11):   PRESSED(clickCount=1) PRESSED(clickCount=2) RELEASED(clickCount=2)
				//   Ubuntu 20.04  (GNOME 3.36.1, X11):   PRESSED(clickCount=1) PRESSED(clickCount=2) RELEASED(clickCount=2)
				//   Ubuntu 22.04  (GNOME 42.2, Wayland): PRESSED(clickCount=1)                       RELEASED(clickCount=1) CLICKED(clickCount=1)
				//   Kubuntu 22.04 (KDE 5.24.4, X11):     PRESSED(clickCount=1) PRESSED(clickCount=1) RELEASED(clickCount=1)

				// double-click is not always recognized in Java when using _NET_WM_MOVERESIZE message
				int clickCount = e.getClickCount();
				if( clickCount == 1 && lastSingleClickWhen != 0 && (e.getWhen() - lastSingleClickWhen) <= getMultiClickInterval() )
					clickCount = 2;

				switch( clickCount ) {
					case 1:
						// move window via _NET_WM_MOVERESIZE message
						e.consume();
						linuxNativeMove = FlatNativeLinuxLibrary.moveOrResizeWindow( window, e, FlatNativeLinuxLibrary.MOVE );
						lastSingleClickWhen = e.getWhen();
						break;

					case 2:
						// maximize/restore on double-click
						// also done here because no mouse clicked event is sent when using _NET_WM_MOVERESIZE message
						lastSingleClickWhen = 0;
						maximizeOrRestore();
						break;
				}
			}
		}

		private int getMultiClickInterval() {
			Object value = Toolkit.getDefaultToolkit().getDesktopProperty( "awt.multiClickInterval" );
			return (value instanceof Integer) ? (Integer) value : 500;
		}

		@Override public void mouseReleased( MouseEvent e ) {}
		@Override public void mouseEntered( MouseEvent e ) {}
		@Override public void mouseExited( MouseEvent e ) {}

		//---- interface MouseMotionListener ----

		@Override
		public void mouseDragged( MouseEvent e ) {
			if( window == null || dragOffset == null )
				return; // should newer occur

			if( linuxNativeMove )
				return;

			if( !SwingUtilities.isLeftMouseButton( e ) )
				return;

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
