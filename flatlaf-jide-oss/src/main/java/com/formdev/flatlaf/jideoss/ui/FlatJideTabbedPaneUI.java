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

package com.formdev.flatlaf.jideoss.ui;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_HAS_FULL_BORDER;
import static com.formdev.flatlaf.FlatClientProperties.clientPropertyEquals;
import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicJideTabbedPaneUI;
import com.jidesoft.swing.JideTabbedPane;

/**
 * Provides the Flat LaF UI delegate for {@link com.jidesoft.swing.JideTabbedPane}.
 *
 * @author Karl Tauber
 */
public class FlatJideTabbedPaneUI
	extends BasicJideTabbedPaneUI
{
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
		return new FlatJideTabbedPaneUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		_background = UIDefaultsLookup.getColor( "JideTabbedPane.background" );

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
		_textIconGap = scale( _textIconGap );
		tabHeight = scale( tabHeight );
		tabSelectionHeight = scale( tabSelectionHeight );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		underlineColor = null;
		disabledUnderlineColor = null;
		hoverColor = null;
		focusColor = null;
		contentAreaColor = null;
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return new PropertyChangeHandler() {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {
				super.propertyChange( e );

				String propertyName = e.getPropertyName();
				if( JideTabbedPane.PROPERTY_SELECTED_INDEX.equals( propertyName ) ) {
					repaintTab( (Integer) e.getOldValue() );
					repaintTab( (Integer) e.getNewValue() );
				} else if( FlatClientProperties.TABBED_PANE_HAS_FULL_BORDER.equals( propertyName ) ) {
					_tabPane.revalidate();
					_tabPane.repaint();
				}
			}
		};
	}

	private void repaintTab( int tabIndex ) {
		if( tabIndex < 0 || tabIndex >= _tabPane.getTabCount() )
			return;

		Rectangle r = getTabBounds( _tabPane, tabIndex );
		if( r != null )
			_tabPane.repaint( r );
	}

	@Override
	protected MouseListener createMouseListener() {
		return new RolloverMouseHandler();
	}

	@Override
	protected MouseMotionListener createMouseMotionListener() {
		return new RolloverMouseMotionHandler();
	}

	@Override
	protected int calculateTabHeight( int tabPlacement, int tabIndex, FontMetrics metrics ) {
		return Math.max( tabHeight, super.calculateTabHeight( tabPlacement, tabIndex, metrics ) );
	}

	@Override
	protected int calculateTabWidth( int tabPlacement, int tabIndex, FontMetrics metrics ) {
		return Math.max( tabHeight, super.calculateTabWidth( tabPlacement, tabIndex, metrics ) );
	}

	@Override
	protected Insets getTabInsets( int tabPlacement, int tabIndex ) {
		return scale( super.getTabInsets( tabPlacement, tabIndex ) );
	}

	@Override
	protected Insets getSelectedTabPadInsets( int tabPlacement ) {
		return scale( super.getSelectedTabPadInsets( tabPlacement ) );
	}

	@Override
	protected Insets getTabAreaInsets( int tabPlacement ) {
		return scale( super.getTabAreaInsets( tabPlacement ) );
	}

	@Override
	protected int getTabShape() {
		return JideTabbedPane.SHAPE_BOX;
	}

	/**
	 * The content border insets are used to create a separator between tabs and content.
	 * If client property JTabbedPane.hasFullBorder is true, then the content border insets
	 * are also used for the border.
	 */
	@Override
	protected Insets getContentBorderInsets( int tabPlacement ) {
		return FlatUIUtils.addInsets( getContentBorderInsets0( tabPlacement ),
			scale( super.getContentBorderInsets( tabPlacement ) ) );
	}

	private Insets getContentBorderInsets0( int tabPlacement ) {
		boolean hasFullBorder = this.hasFullBorder || clientPropertyEquals( _tabPane, TABBED_PANE_HAS_FULL_BORDER, true );
		int sh = scale( contentSeparatorHeight );
		Insets insets = hasFullBorder ? new Insets( sh, sh, sh, sh ) : new Insets( sh, 0, 0, 0 );

		Insets contentBorderInsets = new Insets( 0, 0, 0, 0 );
		rotateInsets( insets, contentBorderInsets, tabPlacement );

		return contentBorderInsets;
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		FlatUIUtils.setRenderingHints( (Graphics2D) g );

		super.update( g, c );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		super.paint( g, c );

		// must paint tab area after content border was painted
		if( !scrollableTabLayoutEnabled() && _tabPane.getTabCount() > 0 )
			paintTabArea( g, _tabPane.getTabPlacement(), _tabPane.getSelectedIndex(), c );
	}

	@Override
	protected void paintTabBackground( Graphics g, int tabPlacement, int tabIndex,
		int x, int y, int w, int h, boolean isSelected )
	{
		// paint tab background
		boolean enabled = _tabPane.isEnabled();
		g.setColor( enabled && _tabPane.isEnabledAt( tabIndex ) &&
				(_indexMouseOver == tabIndex || (_closeButtons != null && ((JideTabbedPane.NoFocusButton)_closeButtons[tabIndex]).isMouseOver()))
			? hoverColor
			: (enabled && isSelected && _tabPane.hasFocus()
				? focusColor
				: _tabPane.getBackgroundAt( tabIndex )) );
		g.fillRect( x, y, w, h );
	}

	@Override
	protected void paintTabBorder( Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
		boolean isSelected )
	{
		if( isSelected )
			paintTabSelection( g, tabPlacement, x, y, w, h );
	}

	protected void paintTabSelection( Graphics g, int tabPlacement,  int x, int y, int w, int h ) {
		// increase clip bounds in scroll-tab-layout to paint over the separator line
		Rectangle clipBounds = scrollableTabLayoutEnabled() ? g.getClipBounds() : null;
		if( clipBounds != null ) {
			Rectangle newClipBounds = new Rectangle( clipBounds );
			int contentSeparatorHeight = scale( this.contentSeparatorHeight );
			switch( tabPlacement ) {
				case TOP:
				default:
					newClipBounds.height += contentSeparatorHeight;
					break;

				case BOTTOM:
					newClipBounds.y -= contentSeparatorHeight;
					newClipBounds.height += contentSeparatorHeight;
					break;

				case LEFT:
					newClipBounds.width += contentSeparatorHeight;
					break;

				case RIGHT:
					newClipBounds.x -= contentSeparatorHeight;
					newClipBounds.width += contentSeparatorHeight;
					break;
			}
			g.setClip( newClipBounds );
		}

		g.setColor( _tabPane.isEnabled() ? underlineColor : disabledUnderlineColor );

		Insets contentInsets = getContentBorderInsets0( tabPlacement );

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

		if( clipBounds != null )
			g.setClip( clipBounds );
	}

	/**
	 * Actually does the nearly the same as super.paintContentBorder() but
	 *   - not invoking paintContentBorder*Edge() methods
	 *   - repaint selection
	 */
	@Override
	protected void paintContentBorder( Graphics g, int tabPlacement, int selectedIndex ) {
		if( _tabPane.getTabCount() <= 0 )
			return;

		Insets insets = _tabPane.getInsets();
		Insets tabAreaInsets = getTabAreaInsets( tabPlacement );

		int x = insets.left;
		int y = insets.top;
		int w = _tabPane.getWidth() - insets.right - insets.left;
		int h = _tabPane.getHeight() - insets.top - insets.bottom;

		Dimension lsize = isTabLeadingComponentVisible() ? _tabLeadingComponent.getPreferredSize() : new Dimension();
		Dimension tsize = isTabTrailingComponentVisible() ? _tabTrailingComponent.getPreferredSize() : new Dimension();

		// remove tabs from bounds
		switch( tabPlacement ) {
			case LEFT:
				x += Math.max( calculateTabAreaWidth( tabPlacement, _runCount, _maxTabWidth ),
					Math.max( lsize.width, tsize.width ) );
				if( tabsOverlapBorder )
					x -= tabAreaInsets.right;
				w -= (x - insets.left);
				break;
			case RIGHT:
				w -= calculateTabAreaWidth( tabPlacement, _runCount, _maxTabWidth );
				if( tabsOverlapBorder )
					w += tabAreaInsets.left;
				break;
			case BOTTOM:
				h -= calculateTabAreaHeight( tabPlacement, _runCount, _maxTabHeight );
				if( tabsOverlapBorder )
					h += tabAreaInsets.top;
				break;
			case TOP:
			default:
				y += Math.max( calculateTabAreaHeight( tabPlacement, _runCount, _maxTabHeight ),
					Math.max( lsize.height, tsize.height ) );
				if( tabsOverlapBorder )
					y -= tabAreaInsets.bottom;
				h -= (y - insets.top);
		}

		// compute insets for separator or full border
		boolean hasFullBorder = this.hasFullBorder || clientPropertyEquals( _tabPane, TABBED_PANE_HAS_FULL_BORDER, true );
		int sh = scale( contentSeparatorHeight * 100 ); // multiply by 100 because rotateInsets() does not use floats
		Insets ci = new Insets( 0, 0, 0, 0 );
		rotateInsets( hasFullBorder ? new Insets( sh, sh, sh, sh ) : new Insets( sh, 0, 0, 0 ), ci, tabPlacement );

		// paint content area
		g.setColor( contentAreaColor );
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new Rectangle2D.Float( x, y, w, h ), false );
		path.append( new Rectangle2D.Float( x + (ci.left / 100f), y + (ci.top / 100f),
			w - (ci.left / 100f) - (ci.right / 100f), h - (ci.top / 100f) - (ci.bottom / 100f) ), false );
		((Graphics2D)g).fill( path );

		// repaint selection in scroll-tab-layout because it may be painted before
		// the content border was painted (from BasicTabbedPaneUI$ScrollableTabPanel)
		if( scrollableTabLayoutEnabled() && selectedIndex >= 0 && _tabScroller != null && _tabScroller.viewport != null ) {
			Rectangle tabRect = getTabBounds( _tabPane, selectedIndex );

			Shape oldClip = g.getClip();
			g.setClip( _tabScroller.viewport.getBounds() );
			paintTabSelection( g, tabPlacement, tabRect.x, tabRect.y, tabRect.width, tabRect.height );
			g.setClip( oldClip );
		}
	}

	@Override
	protected void paintFocusIndicator( Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
		Rectangle iconRect, Rectangle textRect, boolean isSelected )
	{
	}
}
