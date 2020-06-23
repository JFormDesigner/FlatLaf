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
import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTabbedPane}.
 *
 * @clientProperty JTabbedPane.showTabSeparators		boolean
 * @clientProperty JTabbedPane.hasFullBorder			boolean
 *
 * <!-- BasicTabbedPaneUI -->
 *
 * @uiDefault TabbedPane.font							Font
 * @uiDefault TabbedPane.background						Color
 * @uiDefault TabbedPane.foreground						Color
 * @uiDefault TabbedPane.shadow							Color	used for scroll arrows and cropped line
 * @uiDefault TabbedPane.textIconGap					int
 * @uiDefault TabbedPane.tabInsets						Insets
 * @uiDefault TabbedPane.selectedTabPadInsets			Insets
 * @uiDefault TabbedPane.tabAreaInsets					Insets
 * @uiDefault TabbedPane.tabsOverlapBorder				boolean
 * @uiDefault TabbedPane.tabRunOverlay					int
 * @uiDefault TabbedPane.tabsOpaque						boolean
 * @uiDefault TabbedPane.contentOpaque					boolean	unused
 * @uiDefault TabbedPane.opaque							boolean
 * @uiDefault TabbedPane.selectionFollowsFocus			boolean	default is true
 *
 * <!-- FlatTabbedPaneUI -->
 *
 * @uiDefault Component.arrowType						String	triangle (default) or chevron
 * @uiDefault TabbedPane.disabledForeground				Color
 * @uiDefault TabbedPane.selectedBackground				Color	optional
 * @uiDefault TabbedPane.selectedForeground				Color
 * @uiDefault TabbedPane.underlineColor					Color
 * @uiDefault TabbedPane.disabledUnderlineColor			Color
 * @uiDefault TabbedPane.hoverColor						Color
 * @uiDefault TabbedPane.focusColor						Color
 * @uiDefault TabbedPane.tabSeparatorColor				Color	optional; defaults to TabbedPane.contentAreaColor
 * @uiDefault TabbedPane.contentAreaColor				Color
 * @uiDefault TabbedPane.tabHeight						int
 * @uiDefault TabbedPane.tabSelectionHeight				int
 * @uiDefault TabbedPane.contentSeparatorHeight			int
 * @uiDefault TabbedPane.showTabSeparators				boolean
 * @uiDefault TabbedPane.tabSeparatorsFullHeight		boolean
 * @uiDefault TabbedPane.hasFullBorder					boolean
 *
 * @author Karl Tauber
 */
public class FlatTabbedPaneUI
	extends BasicTabbedPaneUI
{
	private static Set<KeyStroke> focusForwardTraversalKeys;
	private static Set<KeyStroke> focusBackwardTraversalKeys;

	protected Color disabledForeground;
	protected Color selectedBackground;
	protected Color selectedForeground;
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

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTabbedPaneUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		disabledForeground = UIManager.getColor( "TabbedPane.disabledForeground" );
		selectedBackground = UIManager.getColor( "TabbedPane.selectedBackground" );
		selectedForeground = UIManager.getColor( "TabbedPane.selectedForeground" );
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

		// scale
		textIconGap = scale( textIconGap );
		tabInsets = scale( tabInsets );
		selectedTabPadInsets = scale( selectedTabPadInsets );
		tabAreaInsets = scale( tabAreaInsets );
		tabHeight = scale( tabHeight );
		tabSelectionHeight = scale( tabSelectionHeight );

		// replace focus forward/backward traversal keys with TAB/Shift+TAB because
		// the default also includes Ctrl+TAB/Ctrl+Shift+TAB, which we need to switch tabs
		if( focusForwardTraversalKeys == null ) {
			focusForwardTraversalKeys = Collections.singleton( KeyStroke.getKeyStroke( KeyEvent.VK_TAB, 0 ) );
			focusBackwardTraversalKeys = Collections.singleton( KeyStroke.getKeyStroke( KeyEvent.VK_TAB, InputEvent.SHIFT_MASK ) );
		}
		// Ideally we should use `LookAndFeel.installProperty( tabPane, "focusTraversalKeysForward", keys )` here
		// instead of `tabPane.setFocusTraversalKeys()`, but WindowsTabbedPaneUI also uses later method
		// and switching from Windows LaF to FlatLaf would not replace the keys and Ctrl+TAB would not work.
		tabPane.setFocusTraversalKeys( KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, focusForwardTraversalKeys );
		tabPane.setFocusTraversalKeys( KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, focusBackwardTraversalKeys );

		MigLayoutVisualPadding.install( tabPane, null );
	}

	@Override
	protected void uninstallDefaults() {
		// restore focus forward/backward traversal keys
		tabPane.setFocusTraversalKeys( KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null );
		tabPane.setFocusTraversalKeys( KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null );

		super.uninstallDefaults();

		disabledForeground = null;
		selectedBackground = null;
		selectedForeground = null;
		underlineColor = null;
		disabledUnderlineColor = null;
		hoverColor = null;
		focusColor = null;
		tabSeparatorColor = null;
		contentAreaColor = null;

		MigLayoutVisualPadding.uninstall( tabPane );
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return new BasicTabbedPaneUI.PropertyChangeHandler() {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {
				super.propertyChange( e );

				switch( e.getPropertyName() ) {
					case TABBED_PANE_SHOW_TAB_SEPARATORS:
					case TABBED_PANE_HAS_FULL_BORDER:
					case TABBED_PANE_TAB_HEIGHT:
						tabPane.revalidate();
						tabPane.repaint();
						break;
				}
			}
		};
	}

	@Override
	protected JButton createScrollButton( int direction ) {
		// this method is invoked before installDefaults(), so we can not use color fields here
		return new FlatArrowButton( direction, UIManager.getString( "Component.arrowType" ),
			UIManager.getColor( "TabbedPane.foreground" ),
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
		return super.calculateTabWidth( tabPlacement, tabIndex, metrics ) - 3 /* was added by superclass */;
	}

	@Override
	protected int calculateTabHeight( int tabPlacement, int tabIndex, int fontHeight ) {
		int tabHeight = clientPropertyInt( tabPane, TABBED_PANE_TAB_HEIGHT, this.tabHeight );
		return Math.max( tabHeight, super.calculateTabHeight( tabPlacement, tabIndex, fontHeight ) - 2 /* was added by superclass */ );
	}

	/**
	 * The content border insets are used to create a separator between tabs and content.
	 * If client property JTabbedPane.hasFullBorder is true, then the content border insets
	 * are also used for the border.
	 */
	@Override
	protected Insets getContentBorderInsets( int tabPlacement ) {
		boolean hasFullBorder = clientPropertyBoolean( tabPane, TABBED_PANE_HAS_FULL_BORDER, this.hasFullBorder );
		int sh = scale( contentSeparatorHeight );
		Insets insets = hasFullBorder ? new Insets( sh, sh, sh, sh ) : new Insets( sh, 0, 0, 0 );

		Insets contentBorderInsets = new Insets( 0, 0, 0, 0 );
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
	public void update( Graphics g, JComponent c ) {
		FlatUIUtils.setRenderingHints( (Graphics2D) g );

		super.update( g, c );
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

		int mnemIndex = FlatLaf.isShowMnemonics() ? tabPane.getDisplayedMnemonicIndexAt( tabIndex ) : -1;

		g.setColor( color );
		FlatUIUtils.drawStringUnderlineCharAt( tabPane, g, title, mnemIndex,
			textRect.x, textRect.y + metrics.getAscent() );
	}

	@Override
	protected void paintTabBackground( Graphics g, int tabPlacement, int tabIndex,
		int x, int y, int w, int h, boolean isSelected )
	{
		// paint tab background
		boolean enabled = tabPane.isEnabled();
		g.setColor( enabled && tabPane.isEnabledAt( tabIndex ) && getRolloverTab() == tabIndex
			? hoverColor
			: (enabled && isSelected && FlatUIUtils.isPermanentFocusOwner( tabPane )
				? focusColor
				: (selectedBackground != null && enabled && isSelected
					? selectedBackground
					: tabPane.getBackgroundAt( tabIndex ))) );
		g.fillRect( x, y, w, h );
	}

	@Override
	protected void paintTabBorder( Graphics g, int tabPlacement, int tabIndex,
		int x, int y, int w, int h, boolean isSelected )
	{
		// paint tab separators
		if( clientPropertyBoolean( tabPane, TABBED_PANE_SHOW_TAB_SEPARATORS, showTabSeparators ) &&
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
		} else if( tabPane.getComponentOrientation().isLeftToRight() ) {
			// paint tab separator at right side
			((Graphics2D)g).fill( new Rectangle2D.Float( x + w - sepWidth, y + offset, sepWidth, h - (offset * 2) ) );
		} else {
			// paint tab separator at left side
			((Graphics2D)g).fill( new Rectangle2D.Float( x, y + offset, sepWidth, h - (offset * 2) ) );
		}
	}

	protected void paintTabSelection( Graphics g, int tabPlacement, int x, int y, int w, int h ) {
		// increase clip bounds in scroll-tab-layout to paint over the separator line
		Rectangle clipBounds = isScrollTabLayout() ? g.getClipBounds() : null;
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

		if( clipBounds != null )
			g.setClip( clipBounds );
	}

	/**
	 * Actually does nearly the same as super.paintContentBorder() but
	 *   - not using UIManager.getColor("TabbedPane.contentAreaColor") to be GUI builder friendly
	 *   - not invoking paintContentBorder*Edge() methods
	 *   - repaint selection
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

		// compute insets for separator or full border
		boolean hasFullBorder = clientPropertyBoolean( tabPane, TABBED_PANE_HAS_FULL_BORDER, this.hasFullBorder );
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
		if( isScrollTabLayout() && selectedIndex >= 0 ) {
			Component scrollableTabViewport = findComponentByClassName( tabPane,
				BasicTabbedPaneUI.class.getName() + "$ScrollableTabViewport" );
			if( scrollableTabViewport != null ) {
				Rectangle tabRect = getTabBounds( tabPane, selectedIndex );

				Shape oldClip = g.getClip();
				g.setClip( scrollableTabViewport.getBounds() );
				paintTabSelection( g, tabPlacement, tabRect.x, tabRect.y, tabRect.width, tabRect.height );
				g.setClip( oldClip );
			}
		}
	}

	@Override
	protected void paintFocusIndicator( Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
		Rectangle iconRect, Rectangle textRect, boolean isSelected )
	{
	}

	private boolean isLastInRun( int tabIndex ) {
		int run = getRunForTab( tabPane.getTabCount(), tabIndex );
		return lastTabInRun( tabPane.getTabCount(), run ) == tabIndex;
	}

	private boolean isScrollTabLayout() {
		return tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT;
	}

	private Component findComponentByClassName( Container c, String className ) {
		for( Component child : c.getComponents() ) {
			if( className.equals( child.getClass().getName() ) )
				return child;

			if( child instanceof Container ) {
				Component c2 = findComponentByClassName( (Container) child, className );
				if( c2 != null )
					return c2;
			}
		}
		return null;
	}
}
