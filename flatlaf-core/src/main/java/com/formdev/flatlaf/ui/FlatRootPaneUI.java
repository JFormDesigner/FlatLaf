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
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
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
	// check this field before using class JBRCustomDecorations to avoid unnecessary loading of that class
	static final boolean canUseJBRCustomDecorations
		= SystemInfo.isJetBrainsJVM_11_orLater && SystemInfo.isWindows_10_orLater;

	protected final Color borderColor = UIManager.getColor( "TitlePane.borderColor" );

	protected JRootPane rootPane;
	protected FlatTitlePane titlePane;
	protected FlatWindowResizer windowResizer;

	private LayoutManager oldLayout;

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

		if( canUseJBRCustomDecorations )
			JBRCustomDecorations.install( rootPane );
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

		uninstallClientDecorations();
		rootPane = null;
	}

	@Override
	protected void installDefaults( JRootPane c ) {
		super.installDefaults( c );

		// Update background color of JFrame or JDialog parent to avoid bad border
		// on HiDPI screens when switching from light to dark Laf.
		// The background of JFrame is initialized in JFrame.frameInit() and
		// the background of JDialog in JDialog.dialogInit(),
		// but it was not updated when switching Laf.
		Container parent = c.getParent();
		if( parent instanceof JFrame || parent instanceof JDialog ) {
			Color background = parent.getBackground();
			if( background == null || background instanceof UIResource )
				parent.setBackground( UIManager.getColor( "control" ) );
		}

		// enable dark window appearance on macOS when running in JetBrains Runtime
		if( SystemInfo.isJetBrainsJVM && SystemInfo.isMacOS_10_14_Mojave_orLater )
			c.putClientProperty( "jetbrains.awt.windowDarkAppearance", FlatLaf.isLafDark() );
	}

	protected void installClientDecorations() {
		boolean isJBRSupported = canUseJBRCustomDecorations && JBRCustomDecorations.isSupported();

		// install border
		if( rootPane.getWindowDecorationStyle() != JRootPane.NONE && !isJBRSupported )
			LookAndFeel.installBorder( rootPane, "RootPane.border" );
		else
			LookAndFeel.uninstallBorder( rootPane );

		// install title pane
		setTitlePane( createTitlePane() );

		// install layout
		oldLayout = rootPane.getLayout();
		rootPane.setLayout( createRootLayout() );

		// install window resizer
		if( !isJBRSupported )
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

	protected void setTitlePane( FlatTitlePane newTitlePane ) {
		JLayeredPane layeredPane = rootPane.getLayeredPane();

		if( titlePane != null )
			layeredPane.remove( titlePane );

		if( newTitlePane != null )
			layeredPane.add( newTitlePane, TITLE_PANE_LAYER );

		titlePane = newTitlePane;
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

			case FlatClientProperties.MENU_BAR_EMBEDDED:
				if( titlePane != null ) {
					titlePane.menuBarChanged();
					rootPane.revalidate();
					rootPane.repaint();
				}
				break;
		}
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

			Dimension titlePaneSize = (titlePane != null)
				? getSizeFunc.apply( titlePane )
				: new Dimension();
			Dimension contentSize = (rootPane.getContentPane() != null)
				? getSizeFunc.apply( rootPane.getContentPane() )
				: rootPane.getSize();

			int width = Math.max( titlePaneSize.width, contentSize.width );
			int height = titlePaneSize.height + contentSize.height;
			if( titlePane == null || !titlePane.isMenuBarEmbedded() ) {
				JMenuBar menuBar = rootPane.getJMenuBar();
				Dimension menuBarSize = (menuBar != null && menuBar.isVisible())
					? getSizeFunc.apply( menuBar )
					: new Dimension();

				width = Math.max( width, menuBarSize.width );
				height += menuBarSize.height;
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

			if( rootPane.getLayeredPane() != null )
				rootPane.getLayeredPane().setBounds( x, y, width, height );
			if( rootPane.getGlassPane() != null )
				rootPane.getGlassPane().setBounds( x, y, width, height );

			int nextY = 0;
			if( !isFullScreen && titlePane != null ) {
				Dimension prefSize = titlePane.getPreferredSize();
				titlePane.setBounds( 0, 0, width, prefSize.height );
				nextY += prefSize.height;
			}

			JMenuBar menuBar = rootPane.getJMenuBar();
			if( menuBar != null && menuBar.isVisible() ) {
				if( !isFullScreen && titlePane != null && titlePane.isMenuBarEmbedded() ) {
					titlePane.validate();
					menuBar.setBounds( titlePane.getMenuBarBounds() );
				} else {
					Dimension prefSize = menuBar.getPreferredSize();
					menuBar.setBounds( 0, nextY, width, prefSize.height );
					nextY += prefSize.height;
				}
			}

			Container contentPane = rootPane.getContentPane();
			if( contentPane != null )
				contentPane.setBounds( 0, nextY, width, Math.max( height - nextY, 0 ) );

			if( titlePane != null )
				titlePane.menuBarLayouted();
		}

		@Override
		public void invalidateLayout( Container parent ) {
			if( titlePane != null )
				titlePane.menuBarChanged();
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

	public static class FlatWindowBorder
		extends BorderUIResource.EmptyBorderUIResource
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
				// hide border if window is maximized
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
			boolean active = parent instanceof Window ? ((Window)parent).isActive() : false;

			g.setColor( FlatUIUtils.deriveColor( active ? activeBorderColor : inactiveBorderColor, baseBorderColor ) );
			HiDPIUtils.paintAtScale1x( (Graphics2D) g, x, y, width, height, this::paintImpl );
		}

		private void paintImpl( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
			g.drawRect( x, y, width - 1, height - 1 );
		}

		protected boolean isWindowMaximized( Component c ) {
			Container parent = c.getParent();
			return parent instanceof Frame
				? (((Frame)parent).getExtendedState() & Frame.MAXIMIZED_BOTH) != 0
				: false;
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
				parent instanceof JDialog;
		}
	}
}
