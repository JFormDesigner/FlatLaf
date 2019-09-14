/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.ui;

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTabbedPane}.
 *
 * @clientProperty JTabbedPane.hasFullBorder			boolean
 *
 * @uiDefault TabbedPane.font							Font
 * @uiDefault TabbedPane.background						Color
 * @uiDefault TabbedPane.foreground						Color
 * @uiDefault TabbedPane.shadow							Color	used for scroll arrows and cropped line
 * @uiDefault TabbedPane.disabledForeground				Color
 * @uiDefault TabbedPane.selectedForeground				Color
 * @uiDefault TabbedPane.underlineColor					Color
 * @uiDefault TabbedPane.disabledUnderlineColor			Color
 * @uiDefault TabbedPane.hoverColor						Color
 * @uiDefault TabbedPane.focusColor						Color
 * @uiDefault TabbedPane.contentAreaColor				Color
 * @uiDefault TabbedPane.textIconGap					int
 * @uiDefault TabbedPane.tabInsets						Insets
 * @uiDefault TabbedPane.tabAreaInsets					Insets
 * @uiDefault TabbedPane.tabHeight						int
 * @uiDefault TabbedPane.tabSelectionHeight				int
 * @uiDefault TabbedPane.contentSeparatorHeight			int
 * @uiDefault TabbedPane.hasFullBorder					boolean
 *
 * @author Karl Tauber
 */
public class FlatTabbedPaneUI
	extends BasicTabbedPaneUI
{
	protected Color disabledForeground;
	protected Color selectedForeground;
	protected Color underlineColor;
	protected Color disabledUnderlineColor;
	protected Color hoverColor;
	protected Color focusColor;
	protected Color contentAreaColor;

	protected int tabHeight;
	protected int tabSelectionHeight;
	protected int contentSeparatorHeight;
	protected boolean hasFullBorder;
	protected boolean tabsOverlapBorder;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTabbedPaneUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		disabledForeground = UIManager.getColor( "TabbedPane.disabledForeground" );
		selectedForeground = UIManager.getColor( "TabbedPane.selectedForeground" );
		underlineColor = UIManager.getColor( "TabbedPane.underlineColor" );
		disabledUnderlineColor = UIManager.getColor( "TabbedPane.disabledUnderlineColor" );
		hoverColor = UIManager.getColor( "TabbedPane.hoverColor" );
		focusColor = UIManager.getColor( "TabbedPane.focusColor" );
		contentAreaColor = UIManager.getColor( "TabbedPane.contentAreaColor" );

		tabHeight = UIManager.getInt( "TabbedPane.tabHeight" );
		tabSelectionHeight = UIManager.getInt( "TabbedPane.tabSelectionHeight" );
		contentSeparatorHeight = UIManager.getInt( "TabbedPane.contentSeparatorHeight" );
		hasFullBorder = UIManager.getBoolean( "TabbedPane.hasFullBorder" );
		tabsOverlapBorder = UIManager.getBoolean( "TabbedPane.tabsOverlapBorder" );

		// scale
		textIconGap = scale( textIconGap );
		tabInsets = scale( tabInsets );
		selectedTabPadInsets = scale( selectedTabPadInsets );
		tabAreaInsets = scale( tabAreaInsets );
		tabHeight = scale( tabHeight );
		tabSelectionHeight = scale( tabSelectionHeight );
		contentSeparatorHeight = scale( contentSeparatorHeight );

		MigLayoutVisualPadding.install( tabPane, null );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		disabledForeground = null;
		selectedForeground = null;
		underlineColor = null;
		disabledUnderlineColor = null;
		hoverColor = null;
		focusColor = null;
		contentAreaColor = null;

		MigLayoutVisualPadding.uninstall( tabPane );
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return new BasicTabbedPaneUI.PropertyChangeHandler() {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {
				super.propertyChange( e );

				if( "JTabbedPane.hasFullBorder".equals( e.getPropertyName() ) ) {
					tabPane.revalidate();
					tabPane.repaint();
				}
			}
		};
	}

	@Override
	protected JButton createScrollButton( int direction ) {
		// this method is invoked before installDefaults(), so we can not use color fields here
		return new FlatArrowButton( direction, UIManager.getColor("TabbedPane.shadow"),
			UIManager.getColor( "TabbedPane.disabledForeground" ), null,
			UIManager.getColor( "TabbedPane.hoverColor" ) );
	}

	@Override
	protected void setRolloverTab( int index ) {
		int oldIndex = getRolloverTab();
		super.setRolloverTab( index );

		if( index == oldIndex )
			return;

		// repaint old and new hover tabs
		repaintTab( oldIndex );
		repaintTab( index );
	}

	private void repaintTab( int tabIndex ) {
		if( tabIndex < 0 || tabIndex >= tabPane.getTabCount() )
			return;

		Rectangle r = getTabBounds( tabPane, tabIndex );
		if( r != null )
			tabPane.repaint( r );
	}

	@Override
	protected int calculateTabWidth( int tabPlacement, int tabIndex, FontMetrics metrics ) {
		return super.calculateTabWidth( tabPlacement, tabIndex, metrics ) - 3 // was added by superclass
			+ (!isTopOrBottom( tabPlacement ) && isScrollTabLayout() ? contentSeparatorHeight : 0);
	}

	@Override
	protected int calculateTabHeight( int tabPlacement, int tabIndex, int fontHeight ) {
		return Math.max( tabHeight, super.calculateTabHeight( tabPlacement, tabIndex, fontHeight ) - 2 /* was added by superclass */ )
			+ (isTopOrBottom( tabPlacement ) && isScrollTabLayout() ? contentSeparatorHeight : 0);
	}

	/**
	 * The content border insets are used to create a separator between tabs and content.
	 * Except in scroll tab policy, where the separator is painted in paintTabArea().
	 * If client property JTabbedPane.hasFullBorder is true, then the content border insets
	 * are also used for the border.
	 */
	@Override
	protected Insets getContentBorderInsets( int tabPlacement ) {
		boolean hasFullBorder = this.hasFullBorder || (tabPane.getClientProperty( "JTabbedPane.hasFullBorder" ) == Boolean.TRUE);
		int sh = contentSeparatorHeight;
		Insets insets = hasFullBorder ? new Insets( sh, sh, sh, sh ) : new Insets( sh, 0, 0, 0 );

		if( isScrollTabLayout() )
			insets.top = 0;

		rotateInsets( insets, contentBorderInsets, tabPlacement );
		return contentBorderInsets;
	}

	@Override
	protected int getTabLabelShiftX( int tabPlacement, int tabIndex, boolean isSelected ) {
		if( isScrollTabLayout() && !isTopOrBottom( tabPlacement ) ) {
			float shift = contentSeparatorHeight / 2f;
			return Math.round( tabPlacement == LEFT ? -shift : shift );
		} else
			return 0;
	}

	@Override
	protected int getTabLabelShiftY( int tabPlacement, int tabIndex, boolean isSelected ) {
		if( isScrollTabLayout() && isTopOrBottom( tabPlacement ) ) {
			float shift = contentSeparatorHeight / 2f;
			return Math.round( tabPlacement == TOP ? -shift : shift );
		} else
			return 0;
	}

	@Override
	protected void paintTabArea( Graphics g, int tabPlacement, int selectedIndex ) {
		if( isScrollTabLayout() ) {
			// paint separator between tabs and content
			Rectangle bounds = g.getClipBounds();
			g.setColor( contentAreaColor );

			if( tabPlacement == TOP || tabPlacement == BOTTOM ) {
				int y = (tabPlacement == TOP) ? bounds.y + bounds.height - contentSeparatorHeight : bounds.y;
				g.fillRect( bounds.x, y, bounds.x + bounds.width, contentSeparatorHeight );
			} else {
				int x = (tabPlacement == LEFT) ? bounds.x + bounds.width - contentSeparatorHeight : bounds.x;
				g.fillRect( x, bounds.y, contentSeparatorHeight, bounds.y + bounds.height );
			}
		}

		super.paintTabArea( g, tabPlacement, selectedIndex );
	}

	@Override
	protected void paintText( Graphics g, int tabPlacement, Font font, FontMetrics metrics,
		int tabIndex, String title, Rectangle textRect, boolean isSelected )
	{
		g.setFont( font );

		// html
		View view = getTextViewForTab( tabIndex );
		if( view != null ) {
			view.paint( g, textRect );
			return;
		}

		// plain text
		Color color;
		if( tabPane.isEnabled() && tabPane.isEnabledAt( tabIndex ) ) {
			color = tabPane.getForegroundAt( tabIndex );
			if( isSelected && (color instanceof UIResource) && selectedForeground != null )
				color = selectedForeground;
		} else
			color = disabledForeground;

		int mnemIndex = tabPane.getDisplayedMnemonicIndexAt( tabIndex );

		g.setColor( color );
		FlatUIUtils.drawStringUnderlineCharAt( tabPane, g, title, mnemIndex,
			textRect.x, textRect.y + metrics.getAscent() );
	}

	@Override
	protected void paintTabBackground( Graphics g, int tabPlacement, int tabIndex,
		int x, int y, int w, int h, boolean isSelected )
	{
		if( isScrollTabLayout() ) {
			// make tab bounds smaller for separator between tabs and content
			if( tabPlacement == TOP || tabPlacement == BOTTOM ) {
				if( tabPlacement == BOTTOM )
					y += contentSeparatorHeight;
				h -= contentSeparatorHeight;
			} else {
				if( tabPlacement == RIGHT )
					x += contentSeparatorHeight;
				w -= contentSeparatorHeight;
			}
		}

		// paint tab background
		boolean enabled = tabPane.isEnabled();
		g.setColor( enabled && tabPane.isEnabledAt( tabIndex ) && getRolloverTab() == tabIndex
			? hoverColor
			: (enabled && isSelected && tabPane.hasFocus()
				? focusColor
				: tabPane.getBackgroundAt( tabIndex )) );
		g.fillRect( x, y, w, h );
	}

	@Override
	protected void paintTabBorder( Graphics g, int tabPlacement, int tabIndex,
		int x, int y, int w, int h, boolean isSelected )
	{
		if( !isSelected )
			return;

		g.setColor( tabPane.isEnabled() ? underlineColor : disabledUnderlineColor );

		Insets contentInsets = getContentBorderInsets( tabPlacement );

		// paint underline selection
		switch( tabPlacement ) {
			case TOP:
			default:
				int sy = y + h + contentInsets.top - tabSelectionHeight;
				g.fillRect( x, sy, w, tabSelectionHeight );
				break;

			case BOTTOM:
				g.fillRect( x, y - contentInsets.bottom, w, tabSelectionHeight );
				break;

			case LEFT:
				int sx = x + w + contentInsets.left - tabSelectionHeight;
				g.fillRect( sx, y, tabSelectionHeight, h );
				break;

			case RIGHT:
				g.fillRect( x - contentInsets.right, y, tabSelectionHeight, h );
				break;
		}
	}

	/**
	 * Actually does the nearly the same as super.paintContentBorder() but
	 *   - content pane is always opaque
	 *   - not using UIManager.getColor("TabbedPane.contentAreaColor") to be GUI builder friendly
	 *   - not invoking paintContentBorder*Edge() methods
	 */
	@Override
	protected void paintContentBorder( Graphics g, int tabPlacement, int selectedIndex ) {
		if( tabPane.getTabCount() <= 0 )
			return;

		Insets insets = tabPane.getInsets();
		Insets tabAreaInsets = getTabAreaInsets( tabPlacement );

		int x = insets.left;
		int y = insets.top;
		int w = tabPane.getWidth() - insets.right - insets.left;
		int h = tabPane.getHeight() - insets.top - insets.bottom;

		// remove tabs from bounds
		switch( tabPlacement ) {
			case LEFT:
				x += calculateTabAreaWidth( tabPlacement, runCount, maxTabWidth );
				if( tabsOverlapBorder )
					x -= tabAreaInsets.right;
				w -= (x - insets.left);
				break;
			case RIGHT:
				w -= calculateTabAreaWidth( tabPlacement, runCount, maxTabWidth );
				if( tabsOverlapBorder )
					w += tabAreaInsets.left;
				break;
			case BOTTOM:
				h -= calculateTabAreaHeight( tabPlacement, runCount, maxTabHeight );
				if( tabsOverlapBorder )
					h += tabAreaInsets.top;
				break;
			case TOP:
			default:
				y += calculateTabAreaHeight( tabPlacement, runCount, maxTabHeight );
				if( tabsOverlapBorder )
					y -= tabAreaInsets.bottom;
				h -= (y - insets.top);
		}

		// paint content area
		g.setColor( contentAreaColor );
		g.fillRect( x, y, w, h );
	}

	@Override
	protected void paintFocusIndicator( Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
		Rectangle iconRect, Rectangle textRect, boolean isSelected )
	{
	}

	private boolean isScrollTabLayout() {
		return tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT;
	}

	private boolean isTopOrBottom( int tabPlacement ) {
		return tabPlacement == TOP || tabPlacement == BOTTOM;
	}
}
