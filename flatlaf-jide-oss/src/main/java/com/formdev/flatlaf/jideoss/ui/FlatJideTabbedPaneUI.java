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
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_SHOW_TAB_SEPARATORS;
import static com.formdev.flatlaf.FlatClientProperties.clientPropertyBoolean;
import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import com.formdev.flatlaf.icons.FlatTabbedPaneCloseIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicJideTabbedPaneUI;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.swing.JideTabbedPane.NoFocusButton;

/**
 * Provides the Flat LaF UI delegate for {@link com.jidesoft.swing.JideTabbedPane}.
 *
 * @author Karl Tauber
 */
public class FlatJideTabbedPaneUI
	extends BasicJideTabbedPaneUI
{
	protected Color selectedBackground;
	protected Color underlineColor;
	protected Color disabledUnderlineColor;
	protected Color hoverColor;
	protected Color focusColor;
	protected Color tabSeparatorColor;
	protected Color contentAreaColor;

	protected int tabHeight;
	protected int tabSelectionHeight;
	protected int contentSeparatorHeight;
	protected boolean showTabSeparators;
	protected boolean tabSeparatorsFullHeight;
	protected boolean hasFullBorder;
	protected boolean tabsOverlapBorder;

	protected Icon closeIcon;

	protected int closeButtonLeftMarginUnscaled;
	protected int closeButtonRightMarginUnscaled;

	private Object[] oldRenderingHints;

	public static ComponentUI createUI( JComponent c ) {
		// usually JIDE would invoke this in JideTabbedPane.updateUI(),
		// but it does not because FlatLaf already has added the UI class to the UI defaults
		LookAndFeelFactory.installJideExtension();

		return new FlatJideTabbedPaneUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		_background = UIDefaultsLookup.getColor( "JideTabbedPane.background" );

		selectedBackground = UIManager.getColor( "TabbedPane.selectedBackground" );
		underlineColor = UIManager.getColor( "TabbedPane.underlineColor" );
		disabledUnderlineColor = UIManager.getColor( "TabbedPane.disabledUnderlineColor" );
		hoverColor = UIManager.getColor( "TabbedPane.hoverColor" );
		focusColor = UIManager.getColor( "TabbedPane.focusColor" );
		tabSeparatorColor = UIManager.getColor( "TabbedPane.tabSeparatorColor" );
		contentAreaColor = UIManager.getColor( "TabbedPane.contentAreaColor" );

		tabHeight = UIManager.getInt( "TabbedPane.tabHeight" );
		tabSelectionHeight = UIManager.getInt( "TabbedPane.tabSelectionHeight" );
		contentSeparatorHeight = UIManager.getInt( "TabbedPane.contentSeparatorHeight" );
		showTabSeparators = UIManager.getBoolean( "TabbedPane.showTabSeparators" );
		tabSeparatorsFullHeight = UIManager.getBoolean( "TabbedPane.tabSeparatorsFullHeight" );
		hasFullBorder = UIManager.getBoolean( "TabbedPane.hasFullBorder" );
		tabsOverlapBorder = UIManager.getBoolean( "TabbedPane.tabsOverlapBorder" );

		closeIcon = new FlatJideTabCloseIcon();

		closeButtonLeftMarginUnscaled = _closeButtonLeftMargin;
		closeButtonRightMarginUnscaled = _closeButtonRightMargin;

		// scale
		_textIconGap = scale( _textIconGap );
		tabHeight = scale( tabHeight );
		tabSelectionHeight = scale( tabSelectionHeight );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		selectedBackground = null;
		underlineColor = null;
		disabledUnderlineColor = null;
		hoverColor = null;
		focusColor = null;
		tabSeparatorColor = null;
		contentAreaColor = null;
		closeIcon = null;
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		PropertyChangeListener superListener = super.createPropertyChangeListener();
		return e -> {
			superListener.propertyChange( e );

			switch( e.getPropertyName() ) {
				case JideTabbedPane.PROPERTY_SELECTED_INDEX:
					repaintTab( (Integer) e.getOldValue() );
					repaintTab( (Integer) e.getNewValue() );
					break;

				case JideTabbedPane.PROPERTY_TAB_AREA_INSETS:
				case JideTabbedPane.PROPERTY_TAB_INSETS:
				case JideTabbedPane.GRIPPER_PROPERTY:
				case TABBED_PANE_SHOW_TAB_SEPARATORS:
				case TABBED_PANE_HAS_FULL_BORDER:
					_tabPane.revalidate();
					_tabPane.repaint();
					break;
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
	protected LayoutManager createLayoutManager() {
		return (_tabPane.getTabLayoutPolicy() == JideTabbedPane.SCROLL_TAB_LAYOUT)
			? new FlatJideTabbedPaneScrollLayout()
			: super.createLayoutManager();
	}

	@Override
	protected int calculateTabHeight( int tabPlacement, int tabIndex, FontMetrics metrics ) {
		updateCloseButtonMargins();
		return Math.max( tabHeight, super.calculateTabHeight( tabPlacement, tabIndex, metrics ) );
	}

	@Override
	protected int calculateTabWidth( int tabPlacement, int tabIndex, FontMetrics metrics ) {
		updateCloseButtonMargins();
		return Math.max( tabHeight, super.calculateTabWidth( tabPlacement, tabIndex, metrics ) - 3 );
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

	@Override
	protected int getLeftMargin() {
		return 0;
	}

	@Override
	protected int getTabGap() {
		return 0;
	}

	@Override
	protected int getLayoutSize() {
		return 0;
	}

	@Override
	protected int getTabRightPadding() {
		return 0;
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
		boolean hasFullBorder = clientPropertyBoolean( _tabPane, TABBED_PANE_HAS_FULL_BORDER, this.hasFullBorder );
		int sh = scale( contentSeparatorHeight );
		Insets insets = hasFullBorder ? new Insets( sh, sh, sh, sh ) : new Insets( sh, 0, 0, 0 );

		Insets contentBorderInsets = new Insets( 0, 0, 0, 0 );
		rotateInsets( insets, contentBorderInsets, tabPlacement );

		return contentBorderInsets;
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		oldRenderingHints = FlatUIUtils.setRenderingHints( g );

		super.update( g, c );

		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
		oldRenderingHints = null;
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
			: (enabled && isSelected && FlatUIUtils.isPermanentFocusOwner( _tabPane )
				? focusColor
				: (selectedBackground != null && enabled && isSelected
					? selectedBackground
					: _tabPane.getBackgroundAt( tabIndex ))) );
		g.fillRect( x, y, w, h );
	}

	@Override
	protected void paintText( Graphics g, int tabPlacement, Font font, FontMetrics metrics,
		int tabIndex, String title, Rectangle textRect, boolean isSelected )
	{
		FlatUIUtils.runWithoutRenderingHints( g, oldRenderingHints, () -> {
			super.paintText( g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected );
		} );
	}

	@Override
	protected void paintTabBorder( Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
		boolean isSelected )
	{
		// paint tab separators
		if( clientPropertyBoolean( _tabPane, TABBED_PANE_SHOW_TAB_SEPARATORS, showTabSeparators ) &&
			!isLastInRun( tabIndex ) )
		  paintTabSeparator( g, tabPlacement, x, y, w, h );

		if( isSelected )
			paintTabSelection( g, tabPlacement, x, y, w, h );
	}

	protected void paintTabSeparator( Graphics g, int tabPlacement, int x, int y, int w, int h ) {
		float sepWidth = UIScale.scale( 1f );
		float offset = tabSeparatorsFullHeight ? 0 : UIScale.scale( 5f );

		g.setColor( (tabSeparatorColor != null) ? tabSeparatorColor : contentAreaColor );
		if( tabPlacement == LEFT || tabPlacement == RIGHT ) {
			// paint tab separator at bottom side
			((Graphics2D)g).fill( new Rectangle2D.Float( x + offset, y + h - sepWidth, w - (offset * 2), sepWidth ) );
		} else if( _tabPane.getComponentOrientation().isLeftToRight() ) {
			// paint tab separator at right side
			((Graphics2D)g).fill( new Rectangle2D.Float( x + w - sepWidth, y + offset, sepWidth, h - (offset * 2) ) );
		} else {
			// paint tab separator at left side
			((Graphics2D)g).fill( new Rectangle2D.Float( x, y + offset, sepWidth, h - (offset * 2) ) );
		}
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
		boolean hasFullBorder = clientPropertyBoolean( _tabPane, TABBED_PANE_HAS_FULL_BORDER, this.hasFullBorder );
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

	@Override
	protected void layoutLabel( int tabPlacement, FontMetrics metrics, int tabIndex, String title, Icon icon,
		Rectangle tabRect, Rectangle iconRect, Rectangle textRect, boolean isSelected )
	{
		if( tabPlacement == LEFT || tabPlacement == RIGHT ) {
			Rectangle tabRect2 = new Rectangle( 0, 0, tabRect.height, tabRect.width );
			Rectangle iconRect2 = new Rectangle();
			Rectangle textRect2 = new Rectangle();

			super.layoutLabel( TOP, metrics, tabIndex, title, icon, tabRect2, iconRect2, textRect2, isSelected );

			textRect.x = tabRect.x + textRect2.y;
			textRect.y = tabRect.y + textRect2.x;
			textRect.width = textRect2.height;
			textRect.height = textRect2.width;

			if( tabPlacement == LEFT )
				textRect.y += metrics.getHeight() / 2;

			iconRect.x = tabRect.x + iconRect2.y;
			iconRect.y = tabRect.y + iconRect2.x;
			iconRect.width = iconRect2.height;
			iconRect.height = iconRect2.width;
		} else
			super.layoutLabel( tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected );
	}

	@Override
	protected Rectangle getTabsTextBoundsAt( int tabIndex ) {
		Rectangle rect = super.getTabsTextBoundsAt( tabIndex );
		rect.x += getTabInsets( _tabPane.getTabPlacement(), tabIndex ).left;
		return rect;
	}

	private boolean isLastInRun( int tabIndex ) {
		int run = getRunForTab( _tabPane.getTabCount(), tabIndex );
		return lastTabInRun( _tabPane.getTabCount(), run ) == tabIndex;
	}

	@Override
	protected void ensureCurrentRects( int leftMargin, int tabCount ) {
		int oldFitStyleBoundSize = _fitStyleBoundSize;
		int oldFitStyleFirstTabMargin = _fitStyleFirstTabMargin;
		int oldCompressedStyleNoIconRectSize = _compressedStyleNoIconRectSize;
		int oldCompressedStyleIconMargin = _compressedStyleIconMargin;
		int oldFixedStyleRectSize = _fixedStyleRectSize;

		_fitStyleBoundSize = scale( _fitStyleBoundSize );
		_fitStyleFirstTabMargin = scale( _fitStyleFirstTabMargin );
		_compressedStyleNoIconRectSize = scale( _compressedStyleNoIconRectSize );
		_compressedStyleIconMargin = scale( _compressedStyleIconMargin );
		_fixedStyleRectSize = scale( _fixedStyleRectSize );

		super.ensureCurrentRects( leftMargin, tabCount );

		_fitStyleBoundSize = oldFitStyleBoundSize;
		_fitStyleFirstTabMargin = oldFitStyleFirstTabMargin;
		_compressedStyleNoIconRectSize = oldCompressedStyleNoIconRectSize;
		_compressedStyleIconMargin = oldCompressedStyleIconMargin;
		_fixedStyleRectSize = oldFixedStyleRectSize;
	}

	@Override
	public void ensureCloseButtonCreated() {
		super.ensureCloseButtonCreated();

		if( _closeButtons == null )
			return;

		// make sure that close buttons use our icon and do not fill background
		for( JButton closeButton : _closeButtons ) {
			if( closeButton.getIcon() != closeIcon )
				closeButton.setIcon( closeIcon );
			if( closeButton.isContentAreaFilled() )
				closeButton.setContentAreaFilled( false );
		}
	}

	protected void updateCloseButtonMargins() {
		// scale close button margins
		_closeButtonLeftMargin = scale( closeButtonLeftMarginUnscaled );
		_closeButtonRightMargin = scale( closeButtonRightMarginUnscaled );

		// since close button size is hardcoded to 16x16 in NoFocusButton.getPreferredSize(),
		// add difference between scaled and unscaled close button size to margins
		int offset = (closeIcon.getIconWidth() - 16) / 2;
		_closeButtonLeftMargin += offset;
		_closeButtonRightMargin += offset;
	}

	//---- class FlatJideTabbedPaneScrollLayout -------------------------------

	protected class FlatJideTabbedPaneScrollLayout
		extends TabbedPaneScrollLayout
	{
		@Override
		public void layoutContainer( Container parent ) {
			updateCloseButtonMargins();

			super.layoutContainer( parent );

			updateCloseButtons();
		}

		private void updateCloseButtons() {
			if( !scrollableTabLayoutEnabled() || !isShowCloseButton() || !isShowCloseButtonOnTab() )
				return;

			Color background = _tabPane.getBackground();

			for( int i = 0; i < _closeButtons.length; i++ ) {
				JButton closeButton = _closeButtons[i];
				if( closeButton.getWidth() == 0 || closeButton.getHeight() == 0 )
					continue; // not visible

				closeButton.setBounds( getTabCloseBounds( i ) );
				closeButton.setBackground( background );
			}
		}

		private Rectangle getTabCloseBounds( int tabIndex ) {
			int iconWidth = closeIcon.getIconWidth();
			int iconHeight = closeIcon.getIconHeight();
			Rectangle tabRect = _rects[tabIndex];
			Insets tabInsets = getTabInsets( _tabPane.getTabPlacement(), tabIndex );

			// use one-third of right/left tab insets as gap between tab text and close button
			if( _tabPane.getTabPlacement() == JideTabbedPane.TOP || _tabPane.getTabPlacement() == JideTabbedPane.BOTTOM ) {
				return new Rectangle(
					_tabPane.getComponentOrientation().isLeftToRight()
						? (tabRect.x + tabRect.width - (tabInsets.right / 3 * 2) - iconWidth)
						: (tabRect.x + (tabInsets.left / 3 * 2)),
					tabRect.y + (tabRect.height - iconHeight) / 2,
					iconWidth,
					iconHeight );
			} else {
				return new Rectangle(
					tabRect.x + (tabRect.width - iconWidth) / 2,
					tabRect.y + tabRect.height - (tabInsets.bottom / 3 * 2) - iconHeight,
					iconWidth,
					iconHeight );
			}
		}
	}

	//---- class FlatJideTabAreaArrowIcon -------------------------------------

	protected class FlatJideTabCloseIcon
		extends FlatTabbedPaneCloseIcon
	{
		@Override
		public void paintIcon( Component c, Graphics g, int x, int y ) {
			NoFocusButton button = (NoFocusButton) c;

			if( _tabPane.isShowCloseButtonOnMouseOver() && !button.isMouseOver() ) {
				Object property = _tabPane.getClientProperty( "JideTabbedPane.mouseOverTabIndex" );
				if( property instanceof Integer && button.getIndex() >= 0 && (Integer) property != button.getIndex() )
					return;
			}

			super.paintIcon( c, g, x, y );
		}
	}
}
