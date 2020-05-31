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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.beans.PropertyChangeEvent;
import java.util.function.Function;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JRootPane}.
 *
 * @author Karl Tauber
 */
public class FlatRootPaneUI
	extends BasicRootPaneUI
{
	private JRootPane rootPane;
	private FlatTitlePane titlePane;
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

		if( SystemInfo.IS_JETBRAINS_JVM_11_OR_LATER && SystemInfo.IS_WINDOWS_10_OR_LATER )
			JBRCustomDecorations.install( rootPane );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		uninstallClientDecorations();
		rootPane = null;
	}

	private void installClientDecorations() {
		// install title pane
		setTitlePane( new FlatTitlePane( rootPane ) );

		// install layout
		oldLayout = rootPane.getLayout();
		rootPane.setLayout( new FlatRootLayout() );
	}

	private void uninstallClientDecorations() {
		setTitlePane( null );

		if( oldLayout != null ) {
			rootPane.setLayout( oldLayout );
			oldLayout = null;
		}

		if( rootPane.getWindowDecorationStyle() == JRootPane.NONE ) {
			rootPane.revalidate();
			rootPane.repaint();
		}
	}

	// layer title pane under frame content layer to allow placing menu bar over title pane
	private final static Integer TITLE_PANE_LAYER = JLayeredPane.FRAME_CONTENT_LAYER - 1;

	private void setTitlePane( FlatTitlePane newTitlePane ) {
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

	private static class FlatRootLayout
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
			FlatTitlePane titlePane = getTitlePane( rootPane );

			Dimension titlePaneSize = (titlePane != null)
				? getSizeFunc.apply( titlePane )
				: new Dimension();
			Dimension contentSize = (rootPane.getContentPane() != null)
				? getSizeFunc.apply( rootPane.getContentPane() )
				: rootPane.getSize();

			int width = Math.max( titlePaneSize.width, contentSize.width );
			int height = titlePaneSize.height + contentSize.height;
			if( titlePane == null || !titlePane.isMenuBarEmbedded() ) {
				Dimension menuBarSize = (rootPane.getJMenuBar() != null)
					? getSizeFunc.apply( rootPane.getJMenuBar() )
					: new Dimension();

				width = Math.max( width, menuBarSize.width );
				height += menuBarSize.height;
			}

			Insets insets = rootPane.getInsets();

			return new Dimension(
				width + insets.left + insets.right,
				height + insets.top + insets.bottom );
		}

		private FlatTitlePane getTitlePane( JRootPane rootPane ) {
			return (rootPane.getWindowDecorationStyle() != JRootPane.NONE &&
					rootPane.getUI() instanceof FlatRootPaneUI)
				? ((FlatRootPaneUI)rootPane.getUI()).titlePane
				: null;
		}

		@Override
		public void layoutContainer( Container parent ) {
			JRootPane rootPane = (JRootPane) parent;

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
			FlatTitlePane titlePane = getTitlePane( rootPane );
			if( titlePane != null ) {
				Dimension prefSize = titlePane.getPreferredSize();
				titlePane.setBounds( 0, 0, width, prefSize.height );
				nextY += prefSize.height;
			}

			JMenuBar menuBar = rootPane.getJMenuBar();
			if( menuBar != null ) {
				if( titlePane != null && titlePane.isMenuBarEmbedded() ) {
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
		}

		@Override
		public void invalidateLayout( Container parent ) {
			FlatTitlePane titlePane = getTitlePane( (JRootPane) parent );
			if( titlePane != null && titlePane.isMenuBarEmbedded() )
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
}
