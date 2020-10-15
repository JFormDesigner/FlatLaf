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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
import com.formdev.flatlaf.util.JavaCompatibility;
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
 * @uiDefault TabbedPane.hiddenTabsNavigation			String	moreTabsButton (default) or arrowButtons
 * @uiDefault ScrollPane.smoothScrolling				boolean
 *
 * @author Karl Tauber
 */
public class FlatTabbedPaneUI
	extends BasicTabbedPaneUI
{
	// hidden tabs navigation types
	protected static final int MORE_TABS_BUTTON = 0;
	protected static final int ARROW_BUTTONS = 1;

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

	protected int hiddenTabsNavigation = MORE_TABS_BUTTON;

	protected String moreTabsButtonToolTipText;

	protected JViewport tabViewport;
	protected FlatWheelTabScroller wheelTabScroller;

	private JButton moreTabsButton;

	private Handler handler;
	private boolean blockRollover;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTabbedPaneUI();
	}

	@Override
	protected void installDefaults() {
		if( UIManager.getBoolean( "TabbedPane.tabsOverlapBorder" ) ) {
			// Force BasicTabbedPaneUI.tabsOverlapBorder to false,
			// which is necessary for "more tabs" button to work correctly.
			//
			// If it would be true, class TabbedPaneScrollLayout would invoke TabbedPaneLayout.padSelectedTab(),
			// which would modify rectangle of selected tab in a wrong way (for wrap tab layout policy).
			// This would cause tab painting issues when scrolled and
			// missing "more tabs" button if last tab is selected.
			//
			// All methods of BasicTabbedPaneUI that use tabsOverlapBorder (except
			// the one method mentioned above) are overridden.
			//
			// This is normally not invoked because the default value for
			// TabbedPane.tabsOverlapBorder is false in all FlatLaf themes.
			// Anyway, 3rd party themes may have changed it.
			// So make sure that it works anyway to avoid issues.
			Object oldValue = UIManager.put( "TabbedPane.tabsOverlapBorder", false );
			super.installDefaults();
			UIManager.put( "TabbedPane.tabsOverlapBorder", oldValue );
		} else
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

		Locale l = tabPane.getLocale();
		moreTabsButtonToolTipText = UIManager.getString( "TabbedPane.moreTabsButtonToolTipText", l );

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
	protected void installComponents() {
		super.installComponents();

		// find scrollable tab viewport
		tabViewport = null;
		if( isScrollTabLayout() ) {
			for( Component c : tabPane.getComponents() ) {
				if( c instanceof JViewport && c.getClass().getName().equals( "javax.swing.plaf.basic.BasicTabbedPaneUI$ScrollableTabViewport" ) ) {
					tabViewport = (JViewport) c;
					break;
				}
			}
		}

		installHiddenTabsNavigation();
	}

	@Override
	protected void uninstallComponents() {
		// uninstall hidden tabs navigation before invoking super.uninstallComponents() for
		// correct uninstallation of BasicTabbedPaneUI tab scroller support
		uninstallHiddenTabsNavigation();

		super.uninstallComponents();

		tabViewport = null;
	}

	protected void installHiddenTabsNavigation() {
		// initialize here because used in installHiddenTabsNavigation() before installDefaults() was invoked
		String hiddenTabsNavigationStr = (String) tabPane.getClientProperty( TABBED_PANE_HIDDEN_TABS_NAVIGATION );
		if( hiddenTabsNavigationStr == null )
			hiddenTabsNavigationStr = UIManager.getString( "TabbedPane.hiddenTabsNavigation" );
		hiddenTabsNavigation = parseHiddenTabsNavigation( hiddenTabsNavigationStr );

		if( hiddenTabsNavigation != MORE_TABS_BUTTON ||
			!isScrollTabLayout() ||
			tabViewport == null )
		  return;

		// At this point, BasicTabbedPaneUI already has installed
		// TabbedPaneScrollLayout (in super.createLayoutManager()) and
		// ScrollableTabSupport, ScrollableTabViewport, ScrollableTabPanel, etc
		// (in super.installComponents()).

		// install own layout manager that delegates to original layout manager
		tabPane.setLayout( createScrollLayoutManager( (TabbedPaneLayout) tabPane.getLayout() ) );

		// create and add "more tabs" button
		moreTabsButton = createMoreTabsButton();
		tabPane.add( moreTabsButton );
	}

	protected void uninstallHiddenTabsNavigation() {
		// restore layout manager before invoking super.uninstallComponents() for
		// correct uninstallation of BasicTabbedPaneUI tab scroller support
		if( tabPane.getLayout() instanceof FlatTabbedPaneScrollLayout )
			tabPane.setLayout( ((FlatTabbedPaneScrollLayout)tabPane.getLayout()).delegate );

		if( moreTabsButton != null ) {
			tabPane.remove( moreTabsButton );
			moreTabsButton = null;
		}
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		tabPane.addMouseListener( getHandler() );
		tabPane.addMouseMotionListener( getHandler() );
		tabPane.addComponentListener( getHandler() );

		if( tabViewport != null && (wheelTabScroller = createWheelTabScroller()) != null ) {
			// ideally we would add the mouse listeners to the viewport, but then the
			// mouse listener of the tabbed pane would not receive events while
			// the mouse pointer is over the viewport
			tabPane.addMouseWheelListener( wheelTabScroller );
			tabPane.addMouseMotionListener( wheelTabScroller );
			tabPane.addMouseListener( wheelTabScroller );
		}
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		if( handler != null ) {
			tabPane.removeMouseListener( handler );
			tabPane.removeMouseMotionListener( handler );
			tabPane.removeComponentListener( handler );
			handler = null;
		}

		if( wheelTabScroller != null ) {
			wheelTabScroller.uninstall();

			tabPane.removeMouseWheelListener( wheelTabScroller );
			tabPane.removeMouseMotionListener( wheelTabScroller );
			tabPane.removeMouseListener( wheelTabScroller );
			wheelTabScroller = null;
		}
	}

	private Handler getHandler() {
		if( handler == null )
			handler = new Handler();
		return handler;
	}

	protected FlatWheelTabScroller createWheelTabScroller() {
		return new FlatWheelTabScroller();
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		Handler handler = getHandler();
		handler.propertyChangeDelegate = super.createPropertyChangeListener();
		return handler;
	}

	@Override
	protected ChangeListener createChangeListener() {
		Handler handler = getHandler();
		handler.changeDelegate = super.createChangeListener();
		return handler;
	}

	protected LayoutManager createScrollLayoutManager( TabbedPaneLayout delegate ) {
		return new FlatTabbedPaneScrollLayout( delegate );
	}

	protected JButton createMoreTabsButton() {
		return new FlatMoreTabsButton();
	}

	@Override
	protected JButton createScrollButton( int direction ) {
		return new FlatScrollableTabButton( direction );
	}

	protected void setRolloverTab( int x, int y ) {
		setRolloverTab( tabForCoordinate( tabPane, x, y ) );
	}

	@Override
	protected void setRolloverTab( int index ) {
		if( blockRollover )
			return;

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
		if( contentSeparatorHeight == 0 || !clientPropertyBoolean( tabPane, TABBED_PANE_SHOW_CONTENT_SEPARATOR, true ) )
			return new Insets( 0, 0, 0, 0 );

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
	public void paint( Graphics g, JComponent c ) {
		ensureCurrentLayout();

		int tabPlacement = tabPane.getTabPlacement();
		int selectedIndex = tabPane.getSelectedIndex();

		paintContentBorder( g, tabPlacement, selectedIndex );

		if( !isScrollTabLayout() )
			paintTabArea( g, tabPlacement, selectedIndex );
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

		// clip title if "more tabs" button is used
		// (normally this is done by invoker, but fails in this case)
		if( hiddenTabsNavigation == MORE_TABS_BUTTON &&
			tabViewport != null &&
			(tabPlacement == TOP || tabPlacement == BOTTOM) )
		{
			Rectangle viewRect = tabViewport.getViewRect();
			viewRect.width -= 4; // subtract width of cropped edge
			if( !viewRect.contains( textRect ) ) {
				Rectangle r = viewRect.intersection( textRect );
				title = JavaCompatibility.getClippedString( null, metrics, title, r.width );
			}
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
		if( clipBounds != null &&
			this.contentSeparatorHeight != 0 &&
			clientPropertyBoolean( tabPane, TABBED_PANE_SHOW_CONTENT_SEPARATOR, true ) )
		{
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
			case TOP:
			default:
				y += calculateTabAreaHeight( tabPlacement, runCount, maxTabHeight );
				if( tabsOverlapBorder )
					y -= tabAreaInsets.bottom;
				h -= (y - insets.top);
				break;

			case BOTTOM:
				h -= calculateTabAreaHeight( tabPlacement, runCount, maxTabHeight );
				if( tabsOverlapBorder )
					h += tabAreaInsets.top;
				break;

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
		}

		if( contentSeparatorHeight != 0 && clientPropertyBoolean( tabPane, TABBED_PANE_SHOW_CONTENT_SEPARATOR, true ) ) {
			// compute insets for separator or full border
			boolean hasFullBorder = clientPropertyBoolean( tabPane, TABBED_PANE_HAS_FULL_BORDER, this.hasFullBorder );
			int sh = scale( contentSeparatorHeight * 100 ); // multiply by 100 because rotateInsets() does not use floats
			Insets ci = new Insets( 0, 0, 0, 0 );
			rotateInsets( hasFullBorder ? new Insets( sh, sh, sh, sh ) : new Insets( sh, 0, 0, 0 ), ci, tabPlacement );

			// paint content separator or full border
			g.setColor( contentAreaColor );
			Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
			path.append( new Rectangle2D.Float( x, y, w, h ), false );
			path.append( new Rectangle2D.Float( x + (ci.left / 100f), y + (ci.top / 100f),
				w - (ci.left / 100f) - (ci.right / 100f), h - (ci.top / 100f) - (ci.bottom / 100f) ), false );
			((Graphics2D)g).fill( path );
		}

		// repaint selection in scroll-tab-layout because it may be painted before
		// the content border was painted (from BasicTabbedPaneUI$ScrollableTabPanel)
		if( isScrollTabLayout() && selectedIndex >= 0 && tabViewport != null ) {
			Rectangle tabRect = getTabBounds( tabPane, selectedIndex );

			Shape oldClip = g.getClip();
			g.setClip( tabViewport.getBounds() );
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
	public int tabForCoordinate( JTabbedPane pane, int x, int y ) {
		if( moreTabsButton != null ) {
			// convert x,y from JTabbedPane coordinate space to ScrollableTabPanel coordinate space
			Point viewPosition = tabViewport.getViewPosition();
			x = x - tabViewport.getX() + viewPosition.x;
			y = y - tabViewport.getY() + viewPosition.y;

			// check whether point is within viewport
			if( !tabViewport.getViewRect().contains( x, y ) )
				return -1;
		}

		return super.tabForCoordinate( pane, x, y );
	}

	@Override
	protected Rectangle getTabBounds( int tabIndex, Rectangle dest ) {
		if( moreTabsButton != null ) {
			// copy tab bounds to dest
			dest.setBounds( rects[tabIndex] );

			// convert tab bounds to coordinate space of JTabbedPane
			Point viewPosition = tabViewport.getViewPosition();
			dest.x = dest.x + tabViewport.getX() - viewPosition.x;
			dest.y = dest.y + tabViewport.getY() - viewPosition.y;
			return dest;
		} else
			return super.getTabBounds( tabIndex, dest );
	}

	protected void ensureCurrentLayout() {
		// since super.ensureCurrentLayout() is private,
		// use super.getTabRunCount() as workaround
		super.getTabRunCount( tabPane );
	}

	private boolean isLastInRun( int tabIndex ) {
		int run = getRunForTab( tabPane.getTabCount(), tabIndex );
		return lastTabInRun( tabPane.getTabCount(), run ) == tabIndex;
	}

	private boolean isScrollTabLayout() {
		return tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT;
	}

	protected boolean isSmoothScrollingEnabled() {
		if( !Animator.useAnimation() )
			return false;

		// Note: Getting UI value "ScrollPane.smoothScrolling" here to allow
		// applications to turn smooth scrolling on or off at any time
		// (e.g. in application options dialog).
		return UIManager.getBoolean( "ScrollPane.smoothScrolling" );
	}

	protected static int parseHiddenTabsNavigation( String str ) {
		if( str == null )
			return MORE_TABS_BUTTON;

		switch( str ) {
			default:
			case "moreTabsButton":	return MORE_TABS_BUTTON;
			case "arrowButtons":	return ARROW_BUTTONS;
		}
	}

	private void runWithOriginalLayoutManager( Runnable runnable ) {
		LayoutManager layout = tabPane.getLayout();
		if( layout instanceof FlatTabbedPaneScrollLayout ) {
			// temporary change layout manager because the runnable may use
			// BasicTabbedPaneUI.scrollableTabLayoutEnabled()
			tabPane.setLayout( ((FlatTabbedPaneScrollLayout)layout).delegate );
			runnable.run();
			tabPane.setLayout( layout );
		} else
			runnable.run();
	}

	protected void ensureSelectedTabIsVisible() {
		if( tabPane == null || tabViewport == null )
			return;

		int selectedIndex = tabPane.getSelectedIndex();
		if( selectedIndex < 0 )
			return;

		Rectangle tabBounds = getTabBounds( tabPane, selectedIndex );
		tabViewport.scrollRectToVisible( tabBounds );
	}

	//---- class FlatMoreTabsButton -------------------------------------------

	protected class FlatMoreTabsButton
		extends FlatArrowButton
		implements ActionListener, PopupMenuListener
	{
		private boolean popupVisible;

		public FlatMoreTabsButton() {
			// this method is invoked before installDefaults(), so we can not use color fields here
			super( SOUTH, UIManager.getString( "Component.arrowType" ),
				UIManager.getColor( "TabbedPane.foreground" ),
				UIManager.getColor( "TabbedPane.disabledForeground" ), null,
				UIManager.getColor( "TabbedPane.hoverColor" ) );

			updateDirection();
			setToolTipText( moreTabsButtonToolTipText );
			addActionListener( this );
		}

		protected void updateDirection() {
			int direction;
			switch( tabPane.getTabPlacement() ) {
				default:
				case TOP:	direction = SOUTH; break;
				case BOTTOM:	direction = NORTH; break;
				case LEFT:	direction = EAST; break;
				case RIGHT:	direction = WEST; break;

			}
			setDirection( direction );
		}

		@Override
		public void paint( Graphics g ) {
			// paint arrow button near separator line
			if( direction == EAST || direction == WEST ) {
				int xoffset = (getWidth() / 2) - getHeight();
				setXOffset( (direction == EAST) ? xoffset : -xoffset );
			}

			super.paint( g );
		}

		@Override
		protected boolean isHover() {
			return super.isHover() || popupVisible;
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			if( tabViewport == null )
				return;

			// detect (partly) hidden tabs and build popup menu
			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.addPopupMenuListener( this );
			Rectangle viewRect = tabViewport.getViewRect();
			int lastIndex = -1;
			for( int i = 0; i < rects.length; i++ ) {
				if( !viewRect.contains( rects[i] ) ) {
					// add separator between leading and trailing tabs
					if( lastIndex >= 0 && lastIndex + 1 != i )
						popupMenu.addSeparator();
					lastIndex = i;

					// create menu item for tab
					popupMenu.add( createMenuItem( i ) );
				}
			}

			// show popup menu
			int buttonWidth = getWidth();
			int buttonHeight = getHeight();
			int x = 0;
			int y = 0;
			Dimension popupSize = popupMenu.getPreferredSize();
			switch( tabPane.getTabPlacement() ) {
				default:
				case TOP:	x = buttonWidth - popupSize.width; y = buttonHeight; break;
				case BOTTOM:	x = buttonWidth - popupSize.width; y = -popupSize.height; break;
				case LEFT:	x = buttonWidth; y = buttonHeight - popupSize.height; break;
				case RIGHT:	x = -popupSize.width; y = buttonHeight - popupSize.height; break;

			}
			popupMenu.show( this, x, y );
		}

		protected JMenuItem createMenuItem( int index ) {
			JMenuItem menuItem = new JMenuItem( tabPane.getTitleAt( index ), tabPane.getIconAt( index ) );
			menuItem.setDisabledIcon( tabPane.getDisabledIconAt( index ) );
			menuItem.setToolTipText( tabPane.getToolTipTextAt( index ) );

			Color foregroundAt = tabPane.getForegroundAt( index );
			if( foregroundAt != tabPane.getForeground() )
				menuItem.setForeground( foregroundAt );

			Color backgroundAt = tabPane.getBackgroundAt( index );
			if( backgroundAt != tabPane.getBackground() ) {
				menuItem.setBackground( backgroundAt );
				menuItem.setOpaque( true );
			}

			if( !tabPane.isEnabledAt( index ) )
				menuItem.setEnabled( false );

			menuItem.addActionListener( e -> tabPane.setSelectedIndex( index ) );
			return menuItem;
		}

		@Override
		public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
			popupVisible = true;
			repaint();
		}

		@Override
		public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
			popupVisible = false;
			repaint();
		}

		@Override
		public void popupMenuCanceled( PopupMenuEvent e ) {
			popupVisible = false;
			repaint();
		}
	}

	//---- class FlatScrollableTabButton --------------------------------------

	protected class FlatScrollableTabButton
		extends FlatArrowButton
		implements MouseListener
	{
		private Timer autoRepeatTimer;

		protected FlatScrollableTabButton( int direction ) {
			// this method is invoked before installDefaults(), so we can not use color fields here
			super( direction, UIManager.getString( "Component.arrowType" ),
				UIManager.getColor( "TabbedPane.foreground" ),
				UIManager.getColor( "TabbedPane.disabledForeground" ), null,
				UIManager.getColor( "TabbedPane.hoverColor" ) );

			addMouseListener( this );
		}

		@Override
		public Dimension getPreferredSize() {
			// Use half width/height if "more tabs" button is used, because size of
			// "more tabs" button is the union of the backward and forward scroll buttons.
			// With this "trick", viewport gets correct size.
			boolean halfSize = (hiddenTabsNavigation == MORE_TABS_BUTTON);

			Dimension size = super.getPreferredSize();
			if( direction == WEST || direction == EAST ) {
				return new Dimension(
					halfSize ? ((size.width / 2) + scale( 4 )) : size.width,
					Math.max( size.height, maxTabHeight ) );
			} else {
				return new Dimension(
					Math.max( size.width, maxTabWidth ),
					halfSize ? ((size.height / 2) + scale( 4 )) : size.height );
			}
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			if( SwingUtilities.isLeftMouseButton( e ) && isEnabled() ) {
				if( autoRepeatTimer == null ) {
					// using same delays as in BasicScrollBarUI and BasicSpinnerUI
					autoRepeatTimer = new Timer( 60, e2 -> {
						if( isEnabled() )
							doClick();
					} );
					autoRepeatTimer.setInitialDelay( 300 );
				}

				autoRepeatTimer.start();
			}
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			if( autoRepeatTimer != null )
				autoRepeatTimer.stop();
		}

		@Override
		public void mouseClicked( MouseEvent e ) {
		}

		@Override
		public void mouseEntered( MouseEvent e ) {
			if( autoRepeatTimer != null && isPressed() )
				autoRepeatTimer.start();
		}

		@Override
		public void mouseExited( MouseEvent e ) {
			if( autoRepeatTimer != null )
				autoRepeatTimer.stop();
		}
	}

	//---- class FlatWheelTabScroller -----------------------------------------

	protected class FlatWheelTabScroller
		extends MouseAdapter
	{
		private int lastMouseX;
		private int lastMouseY;

		private boolean inViewport;
		private boolean scrolled;
		private Timer rolloverTimer;
		private Timer exitedTimer;

		private Animator animator;
		private Point startViewPosition;
		private Point targetViewPosition;

		protected void uninstall() {
			if( rolloverTimer != null )
				rolloverTimer.stop();
			if( exitedTimer != null )
				exitedTimer.stop();
			if( animator != null )
				animator.cancel();
		}

		@Override
		public void mouseWheelMoved( MouseWheelEvent e ) {
			// because this listener receives mouse events for the whole tabbed pane,
			// we have to check whether the mouse is located over the viewport
			if( !isInViewport( e.getX(), e.getY() ) )
				return;

			lastMouseX = e.getX();
			lastMouseY = e.getY();

			double preciseWheelRotation = e.getPreciseWheelRotation();

			// compute new view position
			Point viewPosition = (targetViewPosition != null)
				? targetViewPosition
				: tabViewport.getViewPosition();
			Dimension viewSize = tabViewport.getViewSize();
			int x = viewPosition.x;
			int y = viewPosition.y;
			int tabPlacement = tabPane.getTabPlacement();
			if( tabPlacement == TOP || tabPlacement == BOTTOM ) {
				x += maxTabHeight * preciseWheelRotation;
				x = Math.min( Math.max( x, 0 ), viewSize.width - tabViewport.getWidth() );
			} else {
				y += maxTabHeight * preciseWheelRotation;
				y = Math.min( Math.max( y, 0 ), viewSize.height - tabViewport.getHeight() );
			}

			// check whether view position has changed
			Point newViewPosition = new Point( x, y );
			if( newViewPosition.equals( viewPosition ) )
				return;

			// update view position
			if( preciseWheelRotation != 0 &&
				preciseWheelRotation != e.getWheelRotation() )
			{
				// do not use animation for precise scrolling (e.g. with trackpad)

				// stop running animation (if any)
				if( animator != null )
					animator.stop();

				tabViewport.setViewPosition( newViewPosition );
				updateRolloverDelayed();
			} else
				setViewPositionAnimated( newViewPosition );

			scrolled = true;
		}

		protected void setViewPositionAnimated( Point viewPosition ) {
			// check whether position is equal to current position
			if( viewPosition.equals( tabViewport.getViewPosition() ) )
				return;

			// do not use animation if disabled
			if( !isSmoothScrollingEnabled() ) {
				tabViewport.setViewPosition( viewPosition );
				updateRolloverDelayed();
				return;
			}

			// remember start and target view positions
			startViewPosition = tabViewport.getViewPosition();
			targetViewPosition = viewPosition;

			// create animator
			if( animator == null ) {
				// using same delays as in FlatScrollBarUI
				int duration = 200;
				int resolution = 10;

				animator = new Animator( duration, fraction -> {
					if( tabViewport == null || !tabViewport.isShowing() ) {
						animator.stop();
						return;
					}

					// update view position
					int x = startViewPosition.x + Math.round( (targetViewPosition.x - startViewPosition.x) * fraction );
					int y = startViewPosition.y + Math.round( (targetViewPosition.y - startViewPosition.y) * fraction );
					tabViewport.setViewPosition( new Point( x, y ) );
				}, () -> {
					startViewPosition = targetViewPosition = null;

					if( tabPane != null )
						setRolloverTab( lastMouseX, lastMouseY );
				} );

				animator.setResolution( resolution );
				animator.setInterpolator( new CubicBezierEasing( 0.5f, 0.5f, 0.5f, 1 ) );
			}

			// restart animator
			animator.restart();
		}

		protected void updateRolloverDelayed() {
			blockRollover = true;

			// keep rollover on last tab until it would move to another tab, then clear it
			int oldIndex = getRolloverTab();
			if( oldIndex >= 0 ) {
				int index = tabForCoordinate( tabPane, lastMouseX, lastMouseY );
				if( index >= 0 && index != oldIndex ) {
					// clear if moved to another tab
					blockRollover = false;
					setRolloverTab( -1 );
					blockRollover = true;
				}
			}

			// create timer
			if( rolloverTimer == null ) {
				rolloverTimer = new Timer( 150, e -> {
					blockRollover = false;

					// highlight tab at mouse location
					if( tabPane != null )
						setRolloverTab( lastMouseX, lastMouseY );
				} );
				rolloverTimer.setRepeats( false );
			}

			// restart timer
			rolloverTimer.restart();
		}

		@Override
		public void mouseMoved( MouseEvent e ) {
			checkViewportExited( e.getX(), e.getY() );
		}

		@Override
		public void mouseExited( MouseEvent e ) {
			// this event occurs also if mouse is moved to a custom tab component
			// that handles mouse events (e.g. a close button)
			checkViewportExited( e.getX(), e.getY() );
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			// for the case that the tab was only partly visible before the user clicked it
			setRolloverTab( e.getX(), e.getY() );
		}

		protected boolean isInViewport( int x, int y ) {
			return (tabViewport != null && tabViewport.getBounds().contains( x, y ) );
		}

		protected void checkViewportExited( int x, int y ) {
			lastMouseX = x;
			lastMouseY = y;

			boolean wasInViewport = inViewport;
			inViewport = isInViewport( x, y );

			if( inViewport != wasInViewport ) {
				if( !inViewport )
					viewportExited();
				else if( exitedTimer != null )
					exitedTimer.stop();
			}
		}

		protected void viewportExited() {
			if( !scrolled )
				return;

			if( exitedTimer == null ) {
				exitedTimer = new Timer( 500, e -> ensureSelectedTabVisible() );
				exitedTimer.setRepeats( false );
			}

			exitedTimer.start();
		}

		protected void ensureSelectedTabVisible() {
			// check whether UI delegate was uninstalled because this method is invoked via timer
			if( tabPane == null || tabViewport == null )
				return;

			if( !scrolled || tabViewport == null )
				return;
			scrolled = false;

			// scroll selected tab into visible area
			ensureSelectedTabIsVisible();
		}
	}

	//---- class Handler ------------------------------------------------------

	private class Handler
		extends MouseAdapter
		implements PropertyChangeListener, ChangeListener, ComponentListener
	{
		PropertyChangeListener propertyChangeDelegate;
		ChangeListener changeDelegate;

		//---- interface MouseListener ----

		@Override
		public void mouseEntered( MouseEvent e ) {
			// this is necessary for "more tabs" button
			setRolloverTab( e.getX(), e.getY() );
		}

		@Override
		public void mouseExited( MouseEvent e ) {
			// this event occurs also if mouse is moved to a custom tab component
			// that handles mouse events (e.g. a close button)
			// --> make sure that the tab stays highlighted
			setRolloverTab( e.getX(), e.getY() );
		}

		//---- interface MouseMotionListener ----

		@Override
		public void mouseMoved( MouseEvent e ) {
			// this is necessary for "more tabs" button
			setRolloverTab( e.getX(), e.getY() );
		}

		//---- interface PropertyChangeListener ----

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			// invoke delegate listener
			switch( e.getPropertyName() ) {
				case "tabPlacement":
				case "opaque":
				case "background":
				case "indexForTabComponent":
					runWithOriginalLayoutManager( () -> {
						propertyChangeDelegate.propertyChange( e );
					} );
					break;

				default:
					propertyChangeDelegate.propertyChange( e );
					break;
			}

			// handle event
			switch( e.getPropertyName() ) {
				case "tabPlacement":
					if( moreTabsButton instanceof FlatMoreTabsButton )
						((FlatMoreTabsButton)moreTabsButton).updateDirection();
					break;

				case TABBED_PANE_SHOW_TAB_SEPARATORS:
				case TABBED_PANE_SHOW_CONTENT_SEPARATOR:
				case TABBED_PANE_HAS_FULL_BORDER:
				case TABBED_PANE_TAB_HEIGHT:
					tabPane.revalidate();
					tabPane.repaint();
					break;

				case TABBED_PANE_HIDDEN_TABS_NAVIGATION:
					uninstallHiddenTabsNavigation();
					installHiddenTabsNavigation();
					tabPane.repaint();
					break;
			}
		}

		//---- interface ChangeListener ----

		@Override
		public void stateChanged( ChangeEvent e ) {
			changeDelegate.stateChanged( e );

			// scroll selected tab into visible area
			if( moreTabsButton != null )
				ensureSelectedTabIsVisible();
		}

		//---- interface ComponentListener ----

		@Override
		public void componentResized( ComponentEvent e ) {
			// make sure that selected tab stays visible when component size changed
			EventQueue.invokeLater( () -> {
				ensureSelectedTabIsVisible();
			} );
		}

		@Override public void componentMoved( ComponentEvent e ) {}
		@Override public void componentShown( ComponentEvent e ) {}
		@Override public void componentHidden( ComponentEvent e ) {}
	}

	//---- class FlatTabbedPaneScrollLayout -----------------------------------

	/**
	 * Layout manager used if "TabbedPane.hiddenTabsNavigation" is "moreTabsButton".
	 * <p>
	 * Although this class delegates all methods to the original layout manager
	 * {@link BasicTabbedPaneUI.TabbedPaneScrollLayout}, which extends
	 * {@link BasicTabbedPaneUI.TabbedPaneLayout}, it is necessary that this class
	 * also extends {@link TabbedPaneLayout} to avoid a {@code ClassCastException}
	 * in {@link BasicTabbedPaneUI}.ensureCurrentLayout().
	 */
	protected class FlatTabbedPaneScrollLayout
		extends TabbedPaneLayout
		implements LayoutManager
	{
		private final TabbedPaneLayout delegate;

		protected FlatTabbedPaneScrollLayout( TabbedPaneLayout delegate ) {
			this.delegate = delegate;
		}

		@Override
		public void calculateLayoutInfo() {
			delegate.calculateLayoutInfo();
		}

		//---- interface LayoutManager ----

		@Override
		public void addLayoutComponent( String name, Component comp ) {
			delegate.addLayoutComponent( name, comp );
		}

		@Override
		public void removeLayoutComponent( Component comp ) {
			delegate.removeLayoutComponent( comp );
		}

		@Override
		public Dimension preferredLayoutSize( Container parent ) {
			return delegate.preferredLayoutSize( parent );
		}

		@Override
		public Dimension minimumLayoutSize( Container parent ) {
			return delegate.minimumLayoutSize( parent );
		}

		@Override
		public void layoutContainer( Container parent ) {
			// delegate to original layout manager and let it layout tabs and buttons
			//
			// runWithOriginalLayoutManager() is necessary for correct locations
			// of tab components layed out in TabbedPaneLayout.layoutTabComponents()
			runWithOriginalLayoutManager( () -> {
				delegate.layoutContainer( parent );
			} );

			// check whether scroll buttons are visible, which is changed by original
			// layout manager depending on whether there is enough room for all tabs
			boolean scrollButtonsVisible = false;
			Rectangle buttonsBounds = null;
			for( Component c : tabPane.getComponents() ) {
				if( c instanceof FlatScrollableTabButton && c.isVisible() ) {
					scrollButtonsVisible = true;

					// compute union bounds of all scroll buttons
					Rectangle r = c.getBounds();
					buttonsBounds = (buttonsBounds != null) ? buttonsBounds.union( r ) : r;

					// hide scroll button
					c.setVisible( false );
				}
			}

			// show/hide "more tabs" button and layout it
			moreTabsButton.setVisible( scrollButtonsVisible );
			if( buttonsBounds != null )
				moreTabsButton.setBounds( buttonsBounds );
		}
	}
}
