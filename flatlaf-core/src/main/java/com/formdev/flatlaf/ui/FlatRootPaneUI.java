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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Window;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.function.Function;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.RootPaneUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicRootPaneUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JRootPane}.
 *
 * <!-- FlatRootPaneUI -->
 *
 * @uiDefault RootPane.border								Border
 * @uiDefault RootPane.activeBorderColor					Color
 * @uiDefault RootPane.inactiveBorderColor					Color
 * @uiDefault TitlePane.borderColor							Color	optional
 *
 * <!-- FlatWindowResizer -->
 *
 * @uiDefault RootPane.font									Font	unused
 * @uiDefault RootPane.background							Color
 * @uiDefault RootPane.foreground							Color	unused
 * @uiDefault RootPane.borderDragThickness					int
 * @uiDefault RootPane.cornerDragWidth						int
 * @uiDefault RootPane.honorFrameMinimumSizeOnResize		boolean
 * @uiDefault RootPane.honorDialogMinimumSizeOnResize		boolean
 *
 * @author Karl Tauber
 */
public class FlatRootPaneUI
	extends BasicRootPaneUI
{
	protected final Color borderColor = UIManager.getColor( "TitlePane.borderColor" );

	protected JRootPane rootPane;
	protected FlatTitlePane titlePane;
	protected FlatWindowResizer windowResizer;

	private Object nativeWindowBorderData;
	private LayoutManager oldLayout;
	private ComponentListener macFullWindowContentListener;
	private PropertyChangeListener macWindowBackgroundListener;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatRootPaneUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		rootPane = (JRootPane) c;

		if( rootPane.getWindowDecorationStyle() != JRootPane.NONE )
			installClientDecorations();
		else
			installBorder();

		installNativeWindowBorder();
		macInstallFullWindowContentSupport();
	}

	protected void installBorder() {
		if( borderColor != null ) {
			Border b = rootPane.getBorder();
			if( b == null || b instanceof UIResource )
				rootPane.setBorder( new FlatWindowTitleBorder( borderColor ) );
		}
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		uninstallNativeWindowBorder();
		uninstallClientDecorations();
		macUninstallFullWindowContentSupport();
		rootPane = null;
	}

	@Override
	protected void installDefaults( JRootPane c ) {
		super.installDefaults( c );

		// Give the root pane useful background, foreground and font.
		// Background is used for title bar and menu bar if native window decorations
		// and unified background are enabled.
		// Foreground and font are usually not used, but set for completeness.
		// Not using LookAndFeel.installColorsAndFont() here because it will not work
		// because the properties are null by default but inherit non-null values from parent.
		if( !c.isBackgroundSet() || c.getBackground() instanceof UIResource )
			c.setBackground( UIManager.getColor( "RootPane.background" ) );
		if( !c.isForegroundSet() || c.getForeground() instanceof UIResource )
			c.setForeground( UIManager.getColor( "RootPane.foreground" ) );
		if( !c.isFontSet() || c.getFont() instanceof UIResource )
			c.setFont( UIManager.getFont( "RootPane.font" ) );

		// Update background color of JFrame or JDialog parent to avoid bad border
		// on HiDPI screens when switching from light to dark Laf.
		// Window background color is also used in native window decorations
		// to fill background when window is initially shown or when resizing window.
		// The background of JFrame is initialized in JFrame.frameInit() and
		// the background of JDialog in JDialog.dialogInit(),
		// but it was not updated when switching Laf.
		Container parent = c.getParent();
		if( parent instanceof JFrame || parent instanceof JDialog ) {
			Color background = parent.getBackground();
			if( background == null || background instanceof UIResource )
				parent.setBackground( UIManager.getColor( "control" ) );
		}

		macClearBackgroundForTranslucentWindow( c );
	}

	@Override
	protected void uninstallDefaults( JRootPane c ) {
		super.uninstallDefaults( c );

		// uninstall background, foreground and font because not all Lafs set them
		if( c.isBackgroundSet() && c.getBackground() instanceof UIResource )
			c.setBackground( null );
		if( c.isForegroundSet() && c.getForeground() instanceof UIResource )
			c.setForeground( null );
		if( c.isFontSet() && c.getFont() instanceof UIResource )
			c.setFont( null );
	}

	@Override
	protected void installListeners( JRootPane root ) {
		super.installListeners( root );

		if( SystemInfo.isMacFullWindowContentSupported )
			macFullWindowContentListener = FullWindowContentSupport.macInstallListeners( root );
		macInstallWindowBackgroundListener( root );
	}

	@Override
	protected void uninstallListeners( JRootPane root ) {
		super.uninstallListeners( root );

		if( SystemInfo.isMacFullWindowContentSupported ) {
			FullWindowContentSupport.macUninstallListeners( root, macFullWindowContentListener );
			macFullWindowContentListener = null;
		}
		macUninstallWindowBackgroundListener( root );
	}

	/** @since 1.1.2 */
	protected void installNativeWindowBorder() {
		nativeWindowBorderData = FlatNativeWindowBorder.install( rootPane );
	}

	/** @since 1.1.2 */
	protected void uninstallNativeWindowBorder() {
		FlatNativeWindowBorder.uninstall( rootPane, nativeWindowBorderData );
		nativeWindowBorderData = null;
	}

	/** @since 1.1.2 */
	public static void updateNativeWindowBorder( JRootPane rootPane ) {
		RootPaneUI rui = rootPane.getUI();
		if( !(rui instanceof FlatRootPaneUI) )
			return;

		FlatRootPaneUI ui = (FlatRootPaneUI) rui;
		ui.uninstallNativeWindowBorder();
		ui.installNativeWindowBorder();
	}

	protected void installClientDecorations() {
		boolean isNativeWindowBorderSupported = FlatNativeWindowBorder.isSupported();

		// install border
		if( rootPane.getWindowDecorationStyle() != JRootPane.NONE && !isNativeWindowBorderSupported )
			LookAndFeel.installBorder( rootPane, "RootPane.border" );
		else
			LookAndFeel.uninstallBorder( rootPane );

		// install title pane
		setTitlePane( createTitlePane() );

		// install layout
		oldLayout = rootPane.getLayout();
		rootPane.setLayout( createRootLayout() );

		// install window resizer
		if( !isNativeWindowBorderSupported )
			windowResizer = createWindowResizer();
	}

	protected void uninstallClientDecorations() {
		LookAndFeel.uninstallBorder( rootPane );
		setTitlePane( null );

		if( windowResizer != null ) {
			windowResizer.uninstall();
			windowResizer = null;
		}

		if( oldLayout != null ) {
			rootPane.setLayout( oldLayout );
			oldLayout = null;
		}

		if( rootPane.getWindowDecorationStyle() == JRootPane.NONE ) {
			rootPane.revalidate();
			rootPane.repaint();
		}
	}

	protected FlatRootLayout createRootLayout() {
		return new FlatRootLayout();
	}

	protected FlatWindowResizer createWindowResizer() {
		return new FlatWindowResizer.WindowResizer( rootPane );
	}

	protected FlatTitlePane createTitlePane() {
		return new FlatTitlePane( rootPane );
	}

	// layer title pane under frame content layer to allow placing menu bar over title pane
	protected final static Integer TITLE_PANE_LAYER = JLayeredPane.FRAME_CONTENT_LAYER - 1;
	private final static Integer TITLE_PANE_MOUSE_LAYER = JLayeredPane.FRAME_CONTENT_LAYER - 2;
	private final static Integer WINDOW_TOP_BORDER_LAYER = Integer.MAX_VALUE;

	// for fullWindowContent mode, layer title pane over frame content layer to allow placing title bar buttons over content
	/** @since 3.4 */
	protected final static Integer TITLE_PANE_FULL_WINDOW_CONTENT_LAYER = JLayeredPane.FRAME_CONTENT_LAYER + 1;

	private Integer getLayerForTitlePane() {
		return isFullWindowContent( rootPane ) ? TITLE_PANE_FULL_WINDOW_CONTENT_LAYER : TITLE_PANE_LAYER;
	}

	protected void setTitlePane( FlatTitlePane newTitlePane ) {
		JLayeredPane layeredPane = rootPane.getLayeredPane();

		if( titlePane != null ) {
			layeredPane.remove( titlePane );
			layeredPane.remove( titlePane.mouseLayer );
			if( titlePane.windowTopBorderLayer != null )
				layeredPane.remove( titlePane.windowTopBorderLayer );
		}

		if( newTitlePane != null ) {
			layeredPane.add( newTitlePane, getLayerForTitlePane() );
			layeredPane.add( newTitlePane.mouseLayer, TITLE_PANE_MOUSE_LAYER );
			if( newTitlePane.windowTopBorderLayer != null && newTitlePane.isWindowTopBorderNeeded() && isFullWindowContent( rootPane ) )
				layeredPane.add( newTitlePane.windowTopBorderLayer, WINDOW_TOP_BORDER_LAYER );
		}

		titlePane = newTitlePane;
	}

	private void macInstallFullWindowContentSupport() {
		if( !SystemInfo.isMacOS )
			return;

		// set window buttons spacing
		if( isMacButtonsSpacingSupported() && rootPane.isDisplayable() ) {
			int buttonsSpacing = FlatNativeMacLibrary.BUTTONS_SPACING_DEFAULT;
			String value = (String) rootPane.getClientProperty( FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING );
			if( value != null ) {
				switch( value ) {
					case FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_MEDIUM:
						buttonsSpacing = FlatNativeMacLibrary.BUTTONS_SPACING_MEDIUM;
						break;

					case FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE:
						buttonsSpacing = FlatNativeMacLibrary.BUTTONS_SPACING_LARGE;
						break;
				}
			}

			FlatNativeMacLibrary.setWindowButtonsSpacing( getParentWindow( rootPane ), buttonsSpacing );
		}

		// update buttons bounds client property
		FullWindowContentSupport.macUpdateFullWindowContentButtonsBoundsProperty( rootPane );
	}

	private void macUninstallFullWindowContentSupport() {
		if( !SystemInfo.isMacOS )
			return;

		// do not uninstall when switching to another FlatLaf theme
		if( UIManager.getLookAndFeel() instanceof FlatLaf )
			return;

		// reset window buttons spacing
		if( isMacButtonsSpacingSupported() )
			FlatNativeMacLibrary.setWindowButtonsSpacing( getParentWindow( rootPane ), FlatNativeMacLibrary.BUTTONS_SPACING_DEFAULT );

		// remove buttons bounds client property
		FullWindowContentSupport.macUninstallFullWindowContentButtonsBoundsProperty( rootPane );
	}

	private boolean isMacButtonsSpacingSupported() {
		return SystemInfo.isMacOS && SystemInfo.isJava_17_orLater && FlatNativeMacLibrary.isLoaded();
	}

	private void macInstallWindowBackgroundListener( JRootPane c ) {
		if( !SystemInfo.isMacOS )
			return;

		Window window = getParentWindow( c );
		if( window != null && macWindowBackgroundListener == null ) {
			macWindowBackgroundListener = e -> macClearBackgroundForTranslucentWindow( c );
			window.addPropertyChangeListener( "background", macWindowBackgroundListener );
		}
	}

	private void macUninstallWindowBackgroundListener( JRootPane c ) {
		if( !SystemInfo.isMacOS )
			return;

		Window window = getParentWindow( c );
		if( window != null && macWindowBackgroundListener != null ) {
			window.removePropertyChangeListener( "background", macWindowBackgroundListener );
			macWindowBackgroundListener = null;
		}
	}

	/**
	 * When setting window background to translucent color (alpha < 255),
	 * Swing paints that window translucent on Windows and Linux, but not on macOS.
	 * The reason for this is that FlatLaf sets the background color of the root pane,
	 * and Swing behaves a bit differently on macOS than on other platforms in that case.
	 * Other L&Fs do not set root pane background, which is {@code null} by default.
	 * <p>
	 * To fix this problem, set the root pane background to {@code null}
	 * if windows uses a translucent background.
	 */
	private void macClearBackgroundForTranslucentWindow( JRootPane c ) {
		if( !SystemInfo.isMacOS )
			return;

		Window window = getParentWindow( c );
		if( window != null ) {
			Color windowBackground = window.getBackground();
			if( windowBackground != null &&
				windowBackground.getAlpha() < 255 &&
				c.getBackground() instanceof UIResource )
			{
				// clear root pane background
				c.setBackground( null );
			}
		}
	}

	private Window getParentWindow( JRootPane c ) {
		// not using SwingUtilities.windowForComponent() or SwingUtilities.getWindowAncestor()
		// here because root panes may be nested and used anywhere (e.g. in JInternalFrame)
		// but we're only interested in the "root" root pane, which is a direct child of the window
		Container parent = c.getParent();
		return (parent instanceof Window) ? (Window) parent : null;
	}

	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		super.propertyChange( e );

		switch( e.getPropertyName() ) {
			case "windowDecorationStyle":
				uninstallClientDecorations();
				if( rootPane.getWindowDecorationStyle() != JRootPane.NONE )
					installClientDecorations();
				else
					installBorder();
				break;

			case FlatClientProperties.USE_WINDOW_DECORATIONS:
				updateNativeWindowBorder( rootPane );
				break;

			case FlatClientProperties.MENU_BAR_EMBEDDED:
				if( titlePane != null ) {
					titlePane.menuBarChanged();
					rootPane.revalidate();
					rootPane.repaint();
				}
				break;

			case FlatClientProperties.TITLE_BAR_SHOW_ICON:
				if( titlePane != null )
					titlePane.updateIcon();
				break;

			case FlatClientProperties.TITLE_BAR_SHOW_TITLE:
			case FlatClientProperties.TITLE_BAR_SHOW_ICONIFFY:
			case FlatClientProperties.TITLE_BAR_SHOW_MAXIMIZE:
			case FlatClientProperties.TITLE_BAR_SHOW_CLOSE:
				if( titlePane != null )
					titlePane.updateVisibility();
				break;

			case FlatClientProperties.TITLE_BAR_BACKGROUND:
			case FlatClientProperties.TITLE_BAR_FOREGROUND:
				if( titlePane != null )
					titlePane.titleBarColorsChanged();
				break;

			case FlatClientProperties.TITLE_BAR_HEIGHT:
				if( titlePane != null )
					titlePane.revalidate();
				break;

			case FlatClientProperties.FULL_WINDOW_CONTENT:
				if( titlePane != null ) {
					rootPane.getLayeredPane().setLayer( titlePane, getLayerForTitlePane() );
					if( titlePane.windowTopBorderLayer != null ) {
						JLayeredPane layeredPane = rootPane.getLayeredPane();
						if( titlePane.isWindowTopBorderNeeded() && isFullWindowContent( rootPane ) )
							layeredPane.add( titlePane.windowTopBorderLayer, WINDOW_TOP_BORDER_LAYER );
						else
							layeredPane.remove( titlePane.windowTopBorderLayer );
					}
					titlePane.updateIcon();
					titlePane.updateVisibility();
					titlePane.updateFullWindowContentButtonsBoundsProperty();
				}
				FullWindowContentSupport.revalidatePlaceholders( rootPane );
				rootPane.revalidate();
				break;

			case FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_BOUNDS:
				FullWindowContentSupport.revalidatePlaceholders( rootPane );
				break;

			case FlatClientProperties.GLASS_PANE_FULL_HEIGHT:
				rootPane.revalidate();
				break;

			case FlatClientProperties.WINDOW_STYLE:
				if( rootPane.isDisplayable() )
					throw new IllegalComponentStateException( "The client property 'Window.style' must be set before the window becomes displayable." );
				break;

			case "ancestor":
				if( e.getNewValue() instanceof Window ) {
					if( titlePane != null && !Objects.equals( titlePane.windowStyle, FlatTitlePane.getWindowStyle( rootPane ) ) )
						setTitlePane( createTitlePane() );

					macClearBackgroundForTranslucentWindow( rootPane );
				}

				macUninstallWindowBackgroundListener( rootPane );
				macInstallWindowBackgroundListener( rootPane );

				// FlatNativeMacLibrary.setWindowButtonsSpacing() and
				// FullWindowContentSupport.macUpdateFullWindowContentButtonsBoundsProperty()
				// require a native window, but setting the client properties
				// "apple.awt.fullWindowContent" or FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING
				// is usually done before the native window is created
				// --> try again when native window is created
				if( e.getNewValue() instanceof Window )
					macInstallFullWindowContentSupport();
				break;

			case FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING:
				macInstallFullWindowContentSupport();
				break;

			case "apple.awt.fullWindowContent":
				if( SystemInfo.isMacFullWindowContentSupported )
					FullWindowContentSupport.macUpdateFullWindowContentButtonsBoundsProperty( rootPane );
				break;
		}
	}

	/** @since 3.4 */
	protected static boolean isFullWindowContent( JRootPane rootPane ) {
		return FlatClientProperties.clientPropertyBoolean( rootPane, FlatClientProperties.FULL_WINDOW_CONTENT, false );
	}

	protected static boolean isMenuBarEmbedded( JRootPane rootPane ) {
		FlatTitlePane titlePane = getTitlePane( rootPane );
		return titlePane != null && titlePane.isMenuBarEmbedded();
	}

	/** @since 2.4 */
	protected static FlatTitlePane getTitlePane( JRootPane rootPane ) {
		RootPaneUI ui = rootPane.getUI();
		return ui instanceof FlatRootPaneUI ? ((FlatRootPaneUI)ui).titlePane : null;
	}

	//---- class FlatRootLayout -----------------------------------------------

	protected class FlatRootLayout
		implements LayoutManager2
	{
		@Override public void addLayoutComponent( String name, Component comp ) {}
		@Override public void addLayoutComponent( Component comp, Object constraints ) {}
		@Override public void removeLayoutComponent( Component comp ) {}

		@Override
		public Dimension preferredLayoutSize( Container parent ) {
			return computeLayoutSize( parent, c -> c.getPreferredSize() );
		}

		@Override
		public Dimension minimumLayoutSize( Container parent ) {
			return computeLayoutSize( parent, c -> c.getMinimumSize() );
		}

		@Override
		public Dimension maximumLayoutSize( Container parent ) {
			return new Dimension( Integer.MAX_VALUE, Integer.MAX_VALUE );
		}

		private Dimension computeLayoutSize( Container parent, Function<Component, Dimension> getSizeFunc ) {
			JRootPane rootPane = (JRootPane) parent;

			Dimension contentSize = (rootPane.getContentPane() != null)
				? getSizeFunc.apply( rootPane.getContentPane() )
				: rootPane.getSize(); // same as in JRootPane.RootLayout.preferredLayoutSize()

			int width = contentSize.width; // title pane width is not considered here
			int height = contentSize.height;
			if( titlePane != null && !isFullWindowContent( rootPane ) )
				height += getSizeFunc.apply( titlePane ).height;
			if( titlePane == null || !titlePane.isMenuBarEmbedded() ) {
				JMenuBar menuBar = rootPane.getJMenuBar();
				if( menuBar != null && menuBar.isVisible() ) {
					Dimension menuBarSize = getSizeFunc.apply( menuBar );
					width = Math.max( width, menuBarSize.width );
					height += menuBarSize.height;
				}
			}

			Insets insets = rootPane.getInsets();

			return new Dimension(
				width + insets.left + insets.right,
				height + insets.top + insets.bottom );
		}

		@Override
		public void layoutContainer( Container parent ) {
			JRootPane rootPane = (JRootPane) parent;
			boolean isFullScreen = FlatUIUtils.isFullScreen( rootPane );

			Insets insets = rootPane.getInsets();
			int x = insets.left;
			int y = insets.top;
			int width = rootPane.getWidth() - insets.left - insets.right;
			int height = rootPane.getHeight() - insets.top - insets.bottom;

			// layered pane
			if( rootPane.getLayeredPane() != null )
				rootPane.getLayeredPane().setBounds( x, y, width, height );

			// title pane (is a child of layered pane)
			int nextY = 0;
			if( titlePane != null ) {
				int prefHeight = !isFullScreen ? titlePane.getPreferredSize().height : 0;
				boolean isFullWindowContent = isFullWindowContent( rootPane );
				if( isFullWindowContent && !UIManager.getBoolean( FlatTitlePane.KEY_DEBUG_SHOW_RECTANGLES ) ) {
					// place title bar into top-right corner
					int tw = Math.min( titlePane.getPreferredSize().width, width );
					int tx = titlePane.getComponentOrientation().isLeftToRight() ? width - tw : 0;
					titlePane.setBounds( tx, 0, tw, prefHeight );
				} else
					titlePane.setBounds( 0, 0, width, prefHeight );

				titlePane.mouseLayer.setBounds( 0, 0, width, prefHeight );
				if( titlePane.windowTopBorderLayer != null ) {
					boolean show = isFullWindowContent && !titlePane.isWindowMaximized() && !isFullScreen;
					if( show )
						titlePane.windowTopBorderLayer.setBounds( 0, 0, width, 1 );
					titlePane.windowTopBorderLayer.setVisible( show );
				}

				if( !isFullWindowContent )
					nextY += prefHeight;
			}

			// glass pane
			if( rootPane.getGlassPane() != null ) {
				boolean fullHeight = FlatClientProperties.clientPropertyBoolean(
					rootPane, FlatClientProperties.GLASS_PANE_FULL_HEIGHT, false );
				int offset = fullHeight ? 0 : nextY;
				rootPane.getGlassPane().setBounds( x, y + offset, width, height - offset );
			}

			// menu bar (is a child of layered pane)
			JMenuBar menuBar = rootPane.getJMenuBar();
			if( menuBar != null && menuBar.isVisible() ) {
				boolean embedded = !isFullScreen && titlePane != null && titlePane.isMenuBarEmbedded();
				if( embedded ) {
					titlePane.validate();
					menuBar.setBounds( titlePane.getMenuBarBounds() );
				} else {
					int mx = 0;
					int mw = width;
					if( titlePane != null && isFullWindowContent( rootPane ) ) {
						// make menu bar width smaller to avoid that it overlaps title bar buttons
						int tw = Math.min( titlePane.getPreferredSize().width, width );
						mw -= tw;
						if( !titlePane.getComponentOrientation().isLeftToRight() )
							mx = tw;
					}

					Dimension prefSize = menuBar.getPreferredSize();
					menuBar.setBounds( mx, nextY, mw, prefSize.height );
					nextY += prefSize.height;
				}
			}

			// content pane (is a child of layered pane)
			Container contentPane = rootPane.getContentPane();
			if( contentPane != null )
				contentPane.setBounds( 0, nextY, width, Math.max( height - nextY, 0 ) );

			// title pane
			if( titlePane != null )
				titlePane.menuBarLayouted();
		}

		@Override
		public void invalidateLayout( Container parent ) {
			if( titlePane != null )
				titlePane.menuBarInvalidate();
		}

		@Override
		public float getLayoutAlignmentX( Container target ) {
			return 0;
		}

		@Override
		public float getLayoutAlignmentY( Container target ) {
			return 0;
		}
	}

	//---- class FlatWindowBorder ---------------------------------------------

	/**
	 * Window border used for non-native window decorations.
	 */
	public static class FlatWindowBorder
		extends FlatEmptyBorder
	{
		protected final Color activeBorderColor = UIManager.getColor( "RootPane.activeBorderColor" );
		protected final Color inactiveBorderColor = UIManager.getColor( "RootPane.inactiveBorderColor" );
		protected final Color baseBorderColor = UIManager.getColor( "Panel.background" );

		public FlatWindowBorder() {
			super( 1, 1, 1, 1 );
		}

		@Override
		public Insets getBorderInsets( Component c, Insets insets ) {
			if( isWindowMaximized( c ) || FlatUIUtils.isFullScreen( c ) ) {
				// hide border if window is maximized or full screen
				insets.top = insets.left = insets.bottom = insets.right = 0;
				return insets;
			} else
				return super.getBorderInsets( c, insets );
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			if( isWindowMaximized( c ) || FlatUIUtils.isFullScreen( c ) )
				return;

			Container parent = c.getParent();
			boolean active = parent instanceof Window && ((Window)parent).isActive();

			g.setColor( FlatUIUtils.deriveColor( active ? activeBorderColor : inactiveBorderColor, baseBorderColor ) );
			HiDPIUtils.paintAtScale1x( (Graphics2D) g, x, y, width, height, this::paintImpl );
		}

		private void paintImpl( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
			float lineWidth = (float) (UIScale.scale( 1f ) * scaleFactor);
			g.fill( FlatUIUtils.createRectangle( x, y, width, height, lineWidth ) );
			FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
		}

		protected boolean isWindowMaximized( Component c ) {
			Container parent = c.getParent();
			return parent instanceof Frame && (((Frame)parent).getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
		}
	}

	//---- class FlatWindowTitleBorder ----------------------------------------

	private static class FlatWindowTitleBorder
		extends BorderUIResource.EmptyBorderUIResource
	{
		private final Color borderColor;

		FlatWindowTitleBorder( Color borderColor ) {
			super( 0, 0, 0, 0 );
			this.borderColor = borderColor;
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			if( showBorder( c ) ) {
				float lineHeight = UIScale.scale( (float) 1 );
				FlatUIUtils.paintFilledRectangle( g, borderColor, x, y, width, lineHeight );
			}
		}

		@Override
		public Insets getBorderInsets( Component c, Insets insets ) {
			insets.set( showBorder( c ) ? 1 : 0, 0, 0, 0 );
			return insets;
		}

		private boolean showBorder( Component c ) {
			Container parent = c.getParent();
			return
				(parent instanceof JFrame &&
				 (((JFrame)parent).getJMenuBar() == null ||
				  !((JFrame)parent).getJMenuBar().isVisible())) ||
				(parent instanceof JDialog &&
				 (((JDialog)parent).getJMenuBar() == null ||
				  !((JDialog)parent).getJMenuBar().isVisible()));
		}
	}
}
