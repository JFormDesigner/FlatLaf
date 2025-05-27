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
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
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
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
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
import javax.swing.plaf.ComponentUI;
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
 * @uiDefault TitlePane.titleMinimumWidth					int
 * @uiDefault TitlePane.buttonMinimumWidth					int
 * @uiDefault TitlePane.buttonMaximizedHeight				int
 * @uiDefault TitlePane.buttonsGap							int
 * @uiDefault TitlePane.buttonsMargins						Insets
 * @uiDefault TitlePane.buttonsFillVertically				boolean
 * @uiDefault TitlePane.centerTitle							boolean
 * @uiDefault TitlePane.centerTitleIfMenuBarEmbedded		boolean
 * @uiDefault TitlePane.showIconBesideTitle					boolean
 * @uiDefault TitlePane.menuBarEmbedded						boolean
 * @uiDefault TitlePane.menuBarTitleGap						int
 * @uiDefault TitlePane.menuBarTitleMinimumGap				int
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
	static final String KEY_DEBUG_SHOW_RECTANGLES = "FlatLaf.debug.titlebar.showRectangles";
	private static final boolean isWindows_10 = SystemInfo.isWindows_10_orLater && !SystemInfo.isWindows_11_orLater;

	/** @since 2.5 */ protected final Font titleFont;
	protected final Color activeBackground;
	protected final Color inactiveBackground;
	protected final Color activeForeground;
	protected final Color inactiveForeground;
	protected final Color embeddedForeground;
	protected final Color borderColor;

	/** @since 2 */ protected final boolean showIcon;
	/** @since 2.5 */ protected final boolean showIconInDialogs;
	/** @since 2 */ protected final int noIconLeftGap;
	protected final Dimension iconSize;
	/** @since 3.6 */ protected final Insets iconMargins;
	/** @since 3.6 */ protected final Insets titleMargins;
	/** @since 2.4 */ protected final int titleMinimumWidth;
	/** @since 2.4 */ protected final int buttonMinimumWidth;
	protected final int buttonMaximizedHeight;
	/** @since 3.6 */ protected final int buttonsGap;
	/** @since 3.6 */ protected final Insets buttonsMargins;
	/** @since 3.6 */ protected final boolean buttonsFillVertically;
	protected final boolean centerTitle;
	protected final boolean centerTitleIfMenuBarEmbedded;
	/** @since 2.4 */ protected final boolean showIconBesideTitle;
	protected final int menuBarTitleGap;
	/** @since 2.4 */ protected final int menuBarTitleMinimumGap;

	protected final JRootPane rootPane;
	protected final String windowStyle;

	protected JPanel leftPanel;
	protected JLabel iconLabel;
	protected JComponent menuBarPlaceholder;
	protected JLabel titleLabel;
	protected JPanel buttonPanel;
	protected JButton iconifyButton;
	protected JButton maximizeButton;
	protected JButton restoreButton;
	protected JButton closeButton;

	private JComponent iconifyMaximizeGapComp;
	private JComponent maximizeCloseGapComp;

	protected Window window;

	private final Handler handler;

	/**
	 * This panel handles mouse events if FlatLaf window decorations are used
	 * without native window border. E.g. on Linux.
	 * <p>
	 * This panel usually has same bounds as the title pane,
	 * except if fullWindowContent mode is enabled.
	 * <p>
	 * This panel is not a child of the title pane.
	 * Instead it is added by FlatRootPaneUI to the layered pane at a layer
	 * under the title pane and under the frame content.
	 * The separation is necessary for fullWindowContent mode, where the title pane
	 * is layered over the frame content (for title pane buttons), but the mousePanel
	 * needs to be layered under the frame content so that components on content pane
	 * can receive mouse events when located in title area.
	 */
	final JPanel mouseLayer;

	/**
	 * This panel paint a border at the top of the window in fullWindowContent mode,
	 * if FlatLaf window decorations are enabled.
	 * Only used on Windows 10.
	 * <p>
	 * This panel is not a child of the title pane.
	 * Instead it is added by FlatRootPaneUI to the layered pane at a layer over all other layers.
	 */
	final JPanel windowTopBorderLayer;

	public FlatTitlePane( JRootPane rootPane ) {
		this.rootPane = rootPane;

		windowStyle = getWindowStyle( rootPane );

		titleFont = FlatUIUtils.getSubUIFont( "TitlePane.font", windowStyle );
		activeBackground = FlatUIUtils.getSubUIColor( "TitlePane.background", windowStyle );
		inactiveBackground = FlatUIUtils.getSubUIColor( "TitlePane.inactiveBackground", windowStyle );
		activeForeground = FlatUIUtils.getSubUIColor( "TitlePane.foreground", windowStyle );
		inactiveForeground = FlatUIUtils.getSubUIColor( "TitlePane.inactiveForeground", windowStyle );
		embeddedForeground = FlatUIUtils.getSubUIColor( "TitlePane.embeddedForeground", windowStyle );
		// not using windowStyle here because TitlePane.borderColor is also used in FlatRootPaneUI
		borderColor = UIManager.getColor( "TitlePane.borderColor" );

		showIcon = FlatUIUtils.getSubUIBoolean( "TitlePane.showIcon", windowStyle, true );
		showIconInDialogs = FlatUIUtils.getSubUIBoolean( "TitlePane.showIconInDialogs", windowStyle, true );
		noIconLeftGap = FlatUIUtils.getSubUIInt( "TitlePane.noIconLeftGap", windowStyle, 8 );
		iconSize = FlatUIUtils.getSubUIDimension( "TitlePane.iconSize", windowStyle );
		iconMargins = FlatUIUtils.getSubUIInsets( "TitlePane.iconMargins", windowStyle );
		titleMargins = FlatUIUtils.getSubUIInsets( "TitlePane.titleMargins", windowStyle );
		titleMinimumWidth = FlatUIUtils.getSubUIInt( "TitlePane.titleMinimumWidth", windowStyle, 60 );
		buttonMinimumWidth = FlatUIUtils.getSubUIInt( "TitlePane.buttonMinimumWidth", windowStyle, 30 );
		buttonMaximizedHeight = FlatUIUtils.getSubUIInt( "TitlePane.buttonMaximizedHeight", windowStyle, 0 );
		buttonsGap = FlatUIUtils.getSubUIInt( "TitlePane.buttonsGap", windowStyle, 0 );
		buttonsMargins = FlatUIUtils.getSubUIInsets( "TitlePane.buttonsMargins", windowStyle );
		buttonsFillVertically = FlatUIUtils.getSubUIBoolean( "TitlePane.buttonsFillVertically", windowStyle, true );
		centerTitle = FlatUIUtils.getSubUIBoolean( "TitlePane.centerTitle", windowStyle, false );
		centerTitleIfMenuBarEmbedded = FlatUIUtils.getSubUIBoolean( "TitlePane.centerTitleIfMenuBarEmbedded", windowStyle, true );
		showIconBesideTitle = FlatUIUtils.getSubUIBoolean( "TitlePane.showIconBesideTitle", windowStyle, false );
		menuBarTitleGap = FlatUIUtils.getSubUIInt( "TitlePane.menuBarTitleGap", windowStyle, 40 );
		menuBarTitleMinimumGap = FlatUIUtils.getSubUIInt( "TitlePane.menuBarTitleMinimumGap", windowStyle, 12 );


		handler = createHandler();
		setBorder( createTitlePaneBorder() );

		addSubComponents();
		activeChanged( true );

		mouseLayer = new JPanel();
		mouseLayer.setOpaque( false );
		mouseLayer.addMouseListener( handler );
		mouseLayer.addMouseMotionListener( handler );

		if( isWindows_10 && FlatNativeWindowBorder.isSupported() ) {
			windowTopBorderLayer = new JPanel();
			windowTopBorderLayer.setVisible( false );
			windowTopBorderLayer.setOpaque( false );
			windowTopBorderLayer.setBorder( FlatUIUtils.nonUIResource( WindowTopBorder.getInstance() ) );
		} else
			windowTopBorderLayer = null;

		applyComponentOrientation( rootPane.getComponentOrientation() );
	}

	static String getWindowStyle( JRootPane rootPane ) {
		Window w = SwingUtilities.getWindowAncestor( rootPane );
		String defaultWindowStyle = (w != null && w.getType() == Window.Type.UTILITY) ? WINDOW_STYLE_SMALL : null;
		return clientProperty( rootPane, WINDOW_STYLE, defaultWindowStyle, String.class );
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
		iconLabel.setBorder( new FlatEmptyBorder( iconMargins ) );
		titleLabel.setBorder( new FlatEmptyBorder( titleMargins ) );

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
				if( isFullWindowContent() ) {
					super.layoutContainer( target );
					return;
				}

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

				// increase minimum width if icon is shown besides the title
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

				// clear hit-test cache
				lastCaptionHitTestTime = 0;
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

		iconifyMaximizeGapComp = createButtonsGapComp();
		maximizeCloseGapComp = createButtonsGapComp();

		// initially hide buttons that are only supported in frames
		iconifyButton.setVisible( false );
		maximizeButton.setVisible( false );
		restoreButton.setVisible( false );
		iconifyMaximizeGapComp.setVisible( false );
		maximizeCloseGapComp.setVisible( false );

		buttonPanel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				int titleBarHeight = clientPropertyInt( rootPane, TITLE_BAR_HEIGHT, -1 );
				if( titleBarHeight >= 0 )
					return new Dimension( size.width, UIScale.scale( titleBarHeight ) );

				if( buttonMaximizedHeight > 0 && isWindowMaximized() && !hasVisibleEmbeddedMenuBar( rootPane.getJMenuBar() ) ) {
					// make title pane height smaller when frame is maximized
					size = new Dimension( size.width, Math.min( size.height, UIScale.scale( buttonMaximizedHeight + buttonsMargins.top + buttonsMargins.bottom ) ) );
				}
				return size;
			}
		};
		buttonPanel.setOpaque( false );
		buttonPanel.setBorder( FlatUIUtils.nonUIResource( new FlatEmptyBorder( buttonsMargins ) ) );
		buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.LINE_AXIS ) );
		if( rootPane.getWindowDecorationStyle() == JRootPane.FRAME ) {
			// JRootPane.FRAME works only for frames (and not for dialogs)
			// but at this time the owner window type is unknown (not yet added)
			// so we add the iconify/maximize/restore buttons, and they are shown
			// later in frameStateChanged(), which is invoked from addNotify()

			buttonPanel.add( iconifyButton );
			buttonPanel.add( iconifyMaximizeGapComp );
			buttonPanel.add( maximizeButton );
			buttonPanel.add( restoreButton );
			buttonPanel.add( maximizeCloseGapComp );
		}
		buttonPanel.add( closeButton );

		ComponentListener l = new ComponentAdapter() {
			@Override public void componentResized( ComponentEvent e ) { updateFullWindowContentButtonsBoundsProperty(); }
			@Override public void componentMoved( ComponentEvent e ) { updateFullWindowContentButtonsBoundsProperty(); }
		};
		buttonPanel.addComponentListener( l );
		addComponentListener( l );
	}

	protected JButton createButton( String iconKey, String accessibleName, ActionListener action ) {
		JButton button = new JButton( FlatUIUtils.getSubUIIcon( iconKey, windowStyle ) ) {
			@Override
			public Dimension getMinimumSize() {
				// allow the button to shrink if space is rare
				return new Dimension(
					Math.min( UIScale.scale( buttonMinimumWidth ), super.getPreferredSize().width ),
					super.getMinimumSize().height );
			}
			@Override
			public Dimension getMaximumSize() {
				// allow the button to fill whole button area height
				// see also BasicMenuUI.getMaximumSize()
				return buttonsFillVertically
					? new Dimension( super.getMaximumSize().width, Short.MAX_VALUE )
					: super.getMaximumSize();
			}
		};
		button.setFocusable( false );
		button.setContentAreaFilled( false );
		button.setBorder( BorderFactory.createEmptyBorder() );
		button.putClientProperty( AccessibleContext.ACCESSIBLE_NAME_PROPERTY, accessibleName );
		button.addActionListener( action );
		return button;
	}

	private JComponent createButtonsGapComp() {
		JComponent gapComp = new JPanel();
		gapComp.setOpaque( false );
		gapComp.setMinimumSize( new Dimension( 0, 0 ) );
		gapComp.setPreferredSize( new Dimension( UIScale.scale( buttonsGap ), 0 ) );
		return gapComp;
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
		// (since FlatWindowAbstractIcon now invokes FlatTitlePane.getBackground()
		//  to get base color, this is no longer necessary, but keep it for compatibility;
		//  e.g. for custom window icons)
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
		boolean isFullWindowContent = isFullWindowContent();
		leftPanel.setVisible( !isFullWindowContent );
		titleLabel.setVisible( clientPropertyBoolean( rootPane, TITLE_BAR_SHOW_TITLE, true ) && !isFullWindowContent );
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

		boolean iconifyVisible = iconifyButton.isVisible();
		boolean maximizeVisible = maximizeButton.isVisible();
		boolean restoreVisible = restoreButton.isVisible();
		boolean closeVisible = closeButton.isVisible();
		iconifyMaximizeGapComp.setVisible( iconifyVisible && (maximizeVisible || restoreVisible || closeVisible) );
		maximizeCloseGapComp.setVisible( closeVisible && (maximizeVisible || restoreVisible) );
	}

	protected void updateIcon() {
		boolean defaultShowIcon = showIcon;
		if( !showIconInDialogs && rootPane.getParent() instanceof JDialog )
			defaultShowIcon = false;

		// get window images
		List<Image> images = null;
		if( clientPropertyBoolean( rootPane, TITLE_BAR_SHOW_ICON, defaultShowIcon ) && !isFullWindowContent() ) {
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

	void updateFullWindowContentButtonsBoundsProperty() {
		Rectangle bounds = isFullWindowContent()
			? new Rectangle( SwingUtilities.convertPoint( buttonPanel, 0, 0, rootPane ), buttonPanel.getSize() )
			: null;
		rootPane.putClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_BOUNDS, bounds );
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

	/** @since 3.4 */
	protected boolean isFullWindowContent() {
		return FlatRootPaneUI.isFullWindowContent( rootPane );
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
		if( isFullWindowContent() )
			return false;

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

	void menuBarInvalidate() {
		menuBarPlaceholder.invalidate();
	}

	@Override
	public void paint( Graphics g ) {
		super.paint( g );

		if( !UIManager.getBoolean( KEY_DEBUG_SHOW_RECTANGLES ) )
			return;

		if( debugTitleBarHeight > 0 ) {
			// title bar height is measured from window top edge
			int y = SwingUtilities.convertPoint( window, 0, debugTitleBarHeight, this ).y;
			g.setColor( Color.green );
			g.drawLine( 0, y, getWidth(), y );
		}

		g.setColor( Color.red );
		debugPaintComponentWithMouseListener( g, Color.red, rootPane.getLayeredPane(), 0, 0 );

		debugPaintRect( g, Color.blue, debugAppIconBounds );
		debugPaintRect( g, Color.blue, debugMinimizeButtonBounds );
		debugPaintRect( g, Color.magenta, debugMaximizeButtonBounds );
		debugPaintRect( g, Color.cyan, debugCloseButtonBounds );
	}

	private void debugPaintComponentWithMouseListener( Graphics g, Color color, Component c, int x, int y ) {
		if( !c.isDisplayable() || !c.isVisible() || c == mouseLayer ||
			c == iconifyButton || c == maximizeButton || c == restoreButton || c == closeButton )
		  return;

		if( c.getMouseListeners().length > 0 ||
			c.getMouseMotionListeners().length > 0 ||
			c.getMouseWheelListeners().length > 0 )
		{
			g.drawRect( x, y, c.getWidth(), c.getHeight() );
			return;
		}

		if( c instanceof Container ) {
			Rectangle titlePaneBoundsOnWindow = SwingUtilities.convertRectangle( this, new Rectangle( getSize() ), window );
			for( Component child : ((Container)c).getComponents() ) {
				Rectangle compBoundsOnWindow = SwingUtilities.convertRectangle( c, new Rectangle( c.getSize() ), window );
				if( compBoundsOnWindow.intersects( titlePaneBoundsOnWindow ) )
					debugPaintComponentWithMouseListener( g, color, child, x + child.getX(), y + child.getY() );
			}
		}
	}

	private void debugPaintRect( Graphics g, Color color, Rectangle r ) {
		if( r == null )
			return;

		g.setColor( color );
		Point offset = SwingUtilities.convertPoint( this, 0, 0, window );
		g.drawRect( r.x - offset.x, r.y - offset.y, r.width - 1, r.height - 1 );
	}

	@Override
	protected void paintComponent( Graphics g ) {
		if( isFullWindowContent() )
			return;

		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
	}

	@Override
	public Color getBackground() {
		// not storing value of "TitlePane.unifiedBackground" in class to allow changing at runtime
		return (UIManager.getBoolean( "TitlePane.unifiedBackground" ) &&
				clientPropertyColor( rootPane, TITLE_BAR_BACKGROUND, null ) == null)
			? FlatUIUtils.getParentBackground( this )
			: super.getBackground();
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
					newState = (oldState & ~Frame.MAXIMIZED_BOTH) | Frame.MAXIMIZED_HORIZ;
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
			 Objects.equals( oldMaximizedBounds, rootPane.getClientProperty( "_flatlaf.maximizedBounds" ) )) &&
			window.getGraphicsConfiguration() != null )
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
			Insets screenInsets = FlatUIUtils.getScreenInsets( gc );

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

	/**
	 * Returns whether windows uses native window border and has custom decorations enabled.
	 */
	protected boolean hasNativeCustomDecoration() {
		return window != null && FlatNativeWindowBorder.hasCustomDecoration( window );
	}

	boolean isWindowTopBorderNeeded() {
		return isWindows_10 && hasNativeCustomDecoration();
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
		// title bar height must be measured from window top edge
		// (when window is maximized, window y location is e.g. -11 and window top inset is 11)
		for( Component c = this; c != window && c != null; c = c.getParent() )
			titleBarHeight += c.getY();
		// slightly reduce height so that component receives mouseExit events
		if( titleBarHeight > 0 )
			titleBarHeight--;

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

				appIconBounds = iconR;
			}
		}

		Rectangle minimizeButtonBounds = boundsInWindow( iconifyButton );
		Rectangle maximizeButtonBounds = boundsInWindow( maximizeButton.isVisible() ? maximizeButton : restoreButton );
		Rectangle closeButtonBounds = boundsInWindow( closeButton );

		// clear hit-test cache
		lastCaptionHitTestTime = 0;

		FlatNativeWindowBorder.setTitleBarHeightAndHitTestSpots( window, titleBarHeight,
			this::captionHitTest, appIconBounds, minimizeButtonBounds, maximizeButtonBounds, closeButtonBounds );

		debugTitleBarHeight = titleBarHeight;
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

	/**
	 * Returns whether there is a component at the given location, that processes
	 * mouse events. E.g. buttons, menus, etc.
	 * <p>
	 * Note:
	 * <ul>
	 *   <li>This method is invoked often when mouse is moved over window title bar area
	 *       and should therefore return quickly.
	 *   <li>This method is invoked on 'AWT-Windows' thread (not 'AWT-EventQueue' thread)
	 *       while processing Windows messages.
	 *       It <b>must not</b> change any component property or layout because this could cause a dead lock.
	 * </ul>
	 */
	private boolean captionHitTest( Point pt ) {
		// Windows invokes this method every ~200ms, even if the mouse has not moved
		long time = System.currentTimeMillis();
		if( pt.x == lastCaptionHitTestX && pt.y == lastCaptionHitTestY && time < lastCaptionHitTestTime + 300 ) {
			lastCaptionHitTestTime = time;
			return lastCaptionHitTestResult;
		}

		// convert pt from window coordinates to layeredPane coordinates
		Component layeredPane = rootPane.getLayeredPane();
		int x = pt.x;
		int y = pt.y;
		for( Component c = layeredPane; c != window && c != null; c = c.getParent() ) {
			x -= c.getX();
			y -= c.getY();
		}

		lastCaptionHitTestX = pt.x;
		lastCaptionHitTestY = pt.y;
		lastCaptionHitTestTime = time;
		lastCaptionHitTestResult = isTitleBarCaptionAt( layeredPane, x, y );
		return lastCaptionHitTestResult;
	}

	private boolean isTitleBarCaptionAt( Component c, int x, int y ) {
		if( !c.isDisplayable() || !c.isVisible() || !contains( c, x, y ) || c == mouseLayer )
			return true; // continue checking with next component

		// check enabled component that has mouse listeners
		if( c.isEnabled() &&
			(c.getMouseListeners().length > 0 ||
			 c.getMouseMotionListeners().length > 0) )
		{
			if( !(c instanceof JComponent) )
				return false; // assume that this is not a caption because the component has mouse listeners

			// check client property boolean value
			Object caption = ((JComponent)c).getClientProperty( COMPONENT_TITLE_BAR_CAPTION );
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
				if( !isTitleBarCaptionAt( child, x - child.getX(), y - child.getY() ) )
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
	private boolean contains( Component c, int x, int y ) {
		return x >= 0 && y >= 0 && x < c.getWidth() && y < c.getHeight();
	}

	private int lastCaptionHitTestX;
	private int lastCaptionHitTestY;
	private long lastCaptionHitTestTime;
	private boolean lastCaptionHitTestResult;

	private int debugTitleBarHeight;
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

			if( isWindowTopBorderNeeded() && !isWindowMaximized() )
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

			if( isWindowTopBorderNeeded() && !isWindowMaximized() && !isFullWindowContent() )
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

			// compute icon width and gap (if icon is shown besides the title)
			int iconTextGap = 0;
			int iconWidthAndGap = 0;
			if( icon != null ) {
				Insets iconInsets = iconLabel.getInsets();
				iconTextGap = leftToRight ? iconInsets.right : iconInsets.left;
				iconWidthAndGap = icon.getIconWidth() + iconTextGap;
			}

			// layout title and icon (if shown besides the title)
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

			repaintBorder();
		}

		@Override
		public void windowDeactivated( WindowEvent e ) {
			activeChanged( false );
			updateNativeTitleBarHeightAndHitTestSpots();

			repaintBorder();
		}

		private void repaintBorder() {
			// Windows 10 top border
			if( windowTopBorderLayer != null && windowTopBorderLayer.isShowing())
				WindowTopBorder.getInstance().repaintBorder( windowTopBorderLayer );
			else if( isWindowTopBorderNeeded() && !isWindowMaximized() && !isFullWindowContent() )
				WindowTopBorder.getInstance().repaintBorder( FlatTitlePane.this );

			// Window border used for non-native window decorations
			if( rootPane.getBorder() instanceof FlatRootPaneUI.FlatWindowBorder ) {
				// not repainting four areas on the four sides because RepaintManager
				// unions dirty regions, which also results in repaint of whole rootpane
				rootPane.repaint();
			}
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

		@Override
		public void mouseClicked( MouseEvent e ) {
			if( e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton( e ) ) {
				if( SwingUtilities.getDeepestComponentAt( FlatTitlePane.this, e.getX(), e.getY() ) == iconLabel ) {
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

			dragOffset = SwingUtilities.convertPoint( mouseLayer, e.getPoint(), window );
			linuxNativeMove = false;
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

			// on Linux, move window using window manager
			if( SystemInfo.isLinux && FlatNativeLinuxLibrary.isWMUtilsSupported( window ) ) {
				linuxNativeMove = FlatNativeLinuxLibrary.moveOrResizeWindow( window, e, FlatNativeLinuxLibrary.MOVE );
				if( linuxNativeMove )
					return;
			}

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

	//---- interface TitleBarCaptionHitTest -----------------------------------

	/**
	 * For custom components use {@link FlatClientProperties#COMPONENT_TITLE_BAR_CAPTION}
	 * instead of this interface.
	 *
	 * @since 3.4
	 */
	public interface TitleBarCaptionHitTest {
		/**
		 * Invoked for a component that is enabled and has mouse listeners,
		 * to check whether it processes mouse input at the given x/y location.
		 * Useful for components that do not use mouse input on whole component bounds.
		 * E.g. a tabbed pane with a few tabs has some empty space beside the tabs
		 * that can be used to move the window.
		 * <p>
		 * Note:
		 * <ul>
		 *   <li>This method is invoked often when mouse is moved over window title bar area
		 *       and should therefore return quickly.
		 *   <li>This method is invoked on 'AWT-Windows' thread (not 'AWT-EventQueue' thread)
		 *       while processing Windows messages.
		 *       It <b>must not</b> change any component property or layout because this could cause a dead lock.
		 * </ul>
		 *
		 * @return {@code true} if the component is not interested in mouse input at the given location
		 *         {@code false} if the component wants process mouse input at the given location
		 *         {@code null} if the component children should be checked
		 */
		Boolean isTitleBarCaptionAt( int x, int y );
	}
}
