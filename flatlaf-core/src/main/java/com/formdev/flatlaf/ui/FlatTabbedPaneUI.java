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
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTabbedPane}.
 *
 * @clientProperty JTabbedPane.hasFullBorder			boolean
 *
 * @uiDefault TabbedPane.font							Font
 * @uiDefault TabbedPane.background						Color
 * @uiDefault TabbedPane.foreground						Color
 * @uiDefault TabbedPane.disabledForeground				Color
 * @uiDefault TabbedPane.selectedForeground				Color
 * @uiDefault TabbedPane.underlineColor					Color
 * @uiDefault TabbedPane.disabledUnderlineColor			Color
 * @uiDefault TabbedPane.hoverColor						Color
 * @uiDefault TabbedPane.focusColor						Color
 * @uiDefault TabbedPane.textIconGap					int
 * @uiDefault TabbedPane.tabInsets						Insets
 * @uiDefault TabbedPane.tabAreaInsets					Insets
 * @uiDefault TabbedPane.tabHeight						int
 * @uiDefault TabbedPane.tabSelectionHeight				int
 * @uiDefault TabbedPane.contentSeparatorWidth			int
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

	protected int tabHeight;
	protected int tabSelectionHeight;
	protected int contentSeparatorHeight;

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

		tabHeight = UIManager.getInt( "TabbedPane.tabHeight" );
		tabSelectionHeight = UIManager.getInt( "TabbedPane.tabSelectionHeight" );
		contentSeparatorHeight = UIManager.getInt( "TabbedPane.contentSeparatorHeight" );

		// scale
		textIconGap = scale( textIconGap );
		tabInsets = scale( tabInsets );
		selectedTabPadInsets = scale( selectedTabPadInsets );
		tabAreaInsets = scale( tabAreaInsets );
		tabHeight = scale( tabHeight );
		tabSelectionHeight = scale( tabSelectionHeight );
		contentSeparatorHeight = scale( contentSeparatorHeight );
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
		return super.calculateTabWidth( tabPlacement, tabIndex, metrics ) - 3;
	}

	@Override
	protected int calculateTabHeight( int tabPlacement, int tabIndex, int fontHeight ) {
		return Math.max( super.calculateTabHeight( tabPlacement, tabIndex, fontHeight ) - 2, tabHeight );
	}

	/**
	 * The content border insets are used to create a separator line between tabs and content.
	 */
	@Override
	protected Insets getContentBorderInsets( int tabPlacement ) {
		boolean hasFullBorder = (tabPane.getClientProperty( "JTabbedPane.hasFullBorder" ) == Boolean.TRUE);
		Insets insets = hasFullBorder
			? new Insets( contentSeparatorHeight, contentSeparatorHeight, contentSeparatorHeight, contentSeparatorHeight )
			: new Insets( contentSeparatorHeight, 0, 0, 0 );
		rotateInsets( insets, contentBorderInsets, tabPlacement );
		return contentBorderInsets;
	}

	@Override
	protected int getTabLabelShiftX( int tabPlacement, int tabIndex, boolean isSelected ) {
		return 0;
	}

	@Override
	protected int getTabLabelShiftY( int tabPlacement, int tabIndex, boolean isSelected ) {
		return 0;
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
		SwingUtilities2.drawStringUnderlineCharAt( tabPane, g, title, mnemIndex,
			textRect.x, textRect.y + metrics.getAscent() );
	}

	@Override
	protected void paintTabBackground( Graphics g, int tabPlacement, int tabIndex,
		int x, int y, int w, int h, boolean isSelected )
	{
		boolean enabled = tabPane.isEnabled();
		g.setColor( enabled && getRolloverTab() == tabIndex
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

	@Override
	protected void paintContentBorderTopEdge( Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h ) {
	}

	@Override
	protected void paintContentBorderLeftEdge( Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h ) {
	}

	@Override
	protected void paintContentBorderBottomEdge( Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h ) {
	}

	@Override
	protected void paintContentBorderRightEdge( Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h ) {
	}

	@Override
	protected void paintFocusIndicator( Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
		Rectangle iconRect, Rectangle textRect, boolean isSelected )
	{
	}
}
