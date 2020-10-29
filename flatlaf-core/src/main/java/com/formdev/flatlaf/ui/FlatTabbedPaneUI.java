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
import java.awt.BorderLayout;
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
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
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
 * @uiDefault TabbedPane.selectedTabPadInsets			Insets	unused
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
 * @uiDefault TabbedPane.minimumTabWidth				int		optional
 * @uiDefault TabbedPane.maximumTabWidth				int		optional
 * @uiDefault TabbedPane.tabHeight						int
 * @uiDefault TabbedPane.tabSelectionHeight				int
 * @uiDefault TabbedPane.contentSeparatorHeight			int
 * @uiDefault TabbedPane.showTabSeparators				boolean
 * @uiDefault TabbedPane.tabSeparatorsFullHeight		boolean
 * @uiDefault TabbedPane.hasFullBorder					boolean
 * @uiDefault TabbedPane.hiddenTabsNavigation			String	moreTabsButton (default) or arrowButtons
 * @uiDefault TabbedPane.tabAreaAlignment				String	leading (default), center, trailing or fill
 * @uiDefault ScrollPane.smoothScrolling				boolean
 * @uiDefault TabbedPane.closeIcon						Icon
 *
 * @uiDefault TabbedPane.moreTabsButtonToolTipText		String
 *
 * @author Karl Tauber
 */
public class FlatTabbedPaneUI
	extends BasicTabbedPaneUI
{
	// hidden tabs navigation types
	protected static final int MORE_TABS_BUTTON = 0;
	protected static final int ARROW_BUTTONS = 1;

	// tab area alignment
	protected static final int ALIGN_LEADING = 0;
	protected static final int ALIGN_TRAILING = 1;
	protected static final int ALIGN_CENTER = 2;
	protected static final int ALIGN_FILL = 3;

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

	private int textIconGapUnscaled;
	protected int minimumTabWidth;
	protected int maximumTabWidth;
	protected int tabHeight;
	protected int tabSelectionHeight;
	protected int contentSeparatorHeight;
	protected boolean showTabSeparators;
	protected boolean tabSeparatorsFullHeight;
	protected boolean hasFullBorder;
	protected boolean tabsOpaque = true;

	private String hiddenTabsNavigationStr;
	private String tabAreaAlignmentStr;
	protected Icon closeIcon;

	protected String moreTabsButtonToolTipText;

	protected JViewport tabViewport;
	protected FlatWheelTabScroller wheelTabScroller;

	private JButton tabCloseButton;
	private JButton moreTabsButton;
	private Container leadingComponent;
	private Container trailingComponent;

	private Handler handler;
	private boolean blockRollover;
	private boolean rolloverTabClose;
	private boolean pressedTabClose;

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

		textIconGapUnscaled = UIManager.getInt( "TabbedPane.textIconGap" );
		minimumTabWidth = UIManager.getInt( "TabbedPane.minimumTabWidth" );
		maximumTabWidth = UIManager.getInt( "TabbedPane.maximumTabWidth" );
		tabHeight = UIManager.getInt( "TabbedPane.tabHeight" );
		tabSelectionHeight = UIManager.getInt( "TabbedPane.tabSelectionHeight" );
		contentSeparatorHeight = UIManager.getInt( "TabbedPane.contentSeparatorHeight" );
		showTabSeparators = UIManager.getBoolean( "TabbedPane.showTabSeparators" );
		tabSeparatorsFullHeight = UIManager.getBoolean( "TabbedPane.tabSeparatorsFullHeight" );
		hasFullBorder = UIManager.getBoolean( "TabbedPane.hasFullBorder" );
		tabsOpaque = UIManager.getBoolean( "TabbedPane.tabsOpaque" );
		hiddenTabsNavigationStr = UIManager.getString( "TabbedPane.hiddenTabsNavigation" );
		tabAreaAlignmentStr = UIManager.getString( "TabbedPane.tabAreaAlignment" );
		closeIcon = UIManager.getIcon( "TabbedPane.closeIcon" );

		Locale l = tabPane.getLocale();
		moreTabsButtonToolTipText = UIManager.getString( "TabbedPane.moreTabsButtonToolTipText", l );

		// scale
		textIconGap = scale( textIconGapUnscaled );

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

		closeIcon = null;

		MigLayoutVisualPadding.uninstall( tabPane );
	}

	@Override
	protected void installComponents() {
		super.installComponents();

		// create tab close button
		tabCloseButton = new TabCloseButton();
		tabCloseButton.setVisible( false );
		tabPane.add( tabCloseButton );

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
		installLeadingComponent();
		installTrailingComponent();
	}

	@Override
	protected void uninstallComponents() {
		// uninstall hidden tabs navigation before invoking super.uninstallComponents() for
		// correct uninstallation of BasicTabbedPaneUI tab scroller support
		uninstallHiddenTabsNavigation();
		uninstallLeadingComponent();
		uninstallTrailingComponent();

		super.uninstallComponents();

		if( tabCloseButton != null ) {
			tabPane.remove( tabCloseButton );
			tabCloseButton = null;
		}

		tabViewport = null;
	}

	protected void installHiddenTabsNavigation() {
		if( !isScrollTabLayout() || tabViewport == null )
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

	protected void installLeadingComponent() {
		Object c = tabPane.getClientProperty( TABBED_PANE_LEADING_COMPONENT );
		if( c instanceof Component ) {
			leadingComponent = new ContainerUIResource( (Component) c );
			tabPane.add( leadingComponent );
		}
	}

	protected void uninstallLeadingComponent() {
		if( leadingComponent != null ) {
			tabPane.remove( leadingComponent );
			leadingComponent = null;
		}
	}

	protected void installTrailingComponent() {
		Object c = tabPane.getClientProperty( TABBED_PANE_TRAILING_COMPONENT );
		if( c instanceof Component ) {
			trailingComponent = new ContainerUIResource( (Component) c );
			tabPane.add( trailingComponent );
		}
	}

	protected void uninstallTrailingComponent() {
		if( trailingComponent != null ) {
			tabPane.remove( trailingComponent );
			trailingComponent = null;
		}
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		getHandler().installListeners();

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
			handler.uninstallListeners();
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
	protected MouseListener createMouseListener() {
		Handler handler = getHandler();
		handler.mouseDelegate = super.createMouseListener();
		return handler;
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

	@Override
	protected LayoutManager createLayoutManager() {
		if( tabPane.getTabLayoutPolicy() == JTabbedPane.WRAP_TAB_LAYOUT )
			return new FlatTabbedPaneLayout();

		return super.createLayoutManager();
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

	protected boolean isRolloverTabClose() {
		return rolloverTabClose;
	}

	protected void setRolloverTabClose( boolean rollover ) {
		if( rolloverTabClose == rollover )
			return;

		rolloverTabClose = rollover;
		repaintTab( getRolloverTab() );
	}

	protected boolean isPressedTabClose() {
		return pressedTabClose;
	}

	protected void setPressedTabClose( boolean pressed ) {
		if( pressedTabClose == pressed )
			return;

		pressedTabClose = pressed;
		repaintTab( getRolloverTab() );
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
		// update textIconGap before used in super class
		textIconGap = scale( textIconGapUnscaled );

		int tabWidth = super.calculateTabWidth( tabPlacement, tabIndex, metrics ) - 3 /* was added by superclass */;

		// make tab wider if closable
		if( isTabClosable( tabIndex ) )
			tabWidth += closeIcon.getIconWidth();

		// apply minimum and maximum tab width
		int min = getTabClientPropertyInt( tabIndex, TABBED_PANE_MINIMUM_TAB_WIDTH, minimumTabWidth );
		int max = getTabClientPropertyInt( tabIndex, TABBED_PANE_MAXIMUM_TAB_WIDTH, maximumTabWidth );
		if( min > 0 )
			tabWidth = Math.max( tabWidth, scale( min ) );
		if( max > 0 && tabPane.getTabComponentAt( tabIndex ) == null )
			tabWidth = Math.min( tabWidth, scale( max ) );

		return tabWidth;
	}

	@Override
	protected int calculateTabHeight( int tabPlacement, int tabIndex, int fontHeight ) {
		int tabHeight = scale( clientPropertyInt( tabPane, TABBED_PANE_TAB_HEIGHT, this.tabHeight ) );
		return Math.max( tabHeight, super.calculateTabHeight( tabPlacement, tabIndex, fontHeight ) - 2 /* was added by superclass */ );
	}

	@Override
	protected Insets getTabInsets( int tabPlacement, int tabIndex ) {
		Object value = getTabClientProperty( tabIndex, TABBED_PANE_TAB_INSETS );
		return scale( (value instanceof Insets)
			? (Insets) value
			: super.getTabInsets( tabPlacement, tabIndex ) );
	}

	@Override
	protected Insets getSelectedTabPadInsets( int tabPlacement ) {
		return new Insets( 0, 0, 0, 0 );
	}

	protected Insets getRealTabAreaInsets( int tabPlacement ) {
		Insets currentTabAreaInsets = super.getTabAreaInsets( tabPlacement );
		Insets insets = (Insets) currentTabAreaInsets.clone();

		// This is a "trick" to get rid of the cropped edge:
		//     super.getTabAreaInsets() returns private field BasicTabbedPaneUI.currentTabAreaInsets,
		//     which is also used to translate the origin of the cropped edge in
		//     BasicTabbedPaneUI.CroppedEdge.paintComponent().
		//     Giving it large values clips painting of the cropped edge and makes it invisible.
		currentTabAreaInsets.top = currentTabAreaInsets.left = -10000;

		// scale insets (before adding leading/trailing component sizes)
		insets = scale( insets );

		return insets;
	}

	@Override
	protected Insets getTabAreaInsets( int tabPlacement ) {
		Insets insets = getRealTabAreaInsets( tabPlacement );

		// increase insets for wrap layout if using leading/trailing components
		if( tabPane.getTabLayoutPolicy() == JTabbedPane.WRAP_TAB_LAYOUT ) {
			if( isHorizontalTabPlacement() ) {
				insets.left += getLeadingPreferredWidth();
				insets.right += getTrailingPreferredWidth();
			} else {
				insets.top += getLeadingPreferredHeight();
				insets.bottom += getTrailingPreferredHeight();
			}
		}

		return insets;
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
		if( isTabClosable( tabIndex ) ) {
			int shift = closeIcon.getIconWidth() / 2;
			return isLeftToRight() ? -shift : shift;
		}
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
	protected void paintTab( Graphics g, int tabPlacement, Rectangle[] rects,
		int tabIndex, Rectangle iconRect, Rectangle textRect )
	{
		Rectangle tabRect = rects[tabIndex];
		boolean isSelected = (tabIndex == tabPane.getSelectedIndex());

		// paint background
		if( tabsOpaque || tabPane.isOpaque() )
			paintTabBackground( g, tabPlacement, tabIndex, tabRect.x, tabRect.y, tabRect.width, tabRect.height, isSelected );

		// paint border
		paintTabBorder( g, tabPlacement, tabIndex, tabRect.x, tabRect.y, tabRect.width, tabRect.height, isSelected );

		if( tabPane.getTabComponentAt( tabIndex ) != null )
			return;

		// layout title and icon
		String title = tabPane.getTitleAt( tabIndex );
		Icon icon = getIconForTab( tabIndex );
		Font font = tabPane.getFont();
		FontMetrics metrics = tabPane.getFontMetrics( font );
		String clippedTitle = layoutAndClipLabel( tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected );

		// special title clipping for scroll layout where title off last visible tab on right side may be truncated
		if( tabViewport != null && (tabPlacement == TOP || tabPlacement == BOTTOM) ) {
			Rectangle viewRect = tabViewport.getViewRect();
			viewRect.width -= 4; // subtract width of cropped edge
			if( !viewRect.contains( textRect ) ) {
				Rectangle r = viewRect.intersection( textRect );
				if( r.x > viewRect.x )
					clippedTitle = JavaCompatibility.getClippedString( null, metrics, title, r.width );
			}
		}

		// paint title and icon
		paintText( g, tabPlacement, font, metrics, tabIndex, clippedTitle, textRect, isSelected );
		paintIcon( g, tabPlacement, tabIndex, icon, iconRect, isSelected );
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
		// paint tab close button
		if( isTabClosable( tabIndex ) )
			paintTabCloseButton( g, tabIndex, x, y, w, h );

		// paint tab separators
		if( clientPropertyBoolean( tabPane, TABBED_PANE_SHOW_TAB_SEPARATORS, showTabSeparators ) &&
			!isLastInRun( tabIndex ) )
		  paintTabSeparator( g, tabPlacement, x, y, w, h );

		if( isSelected )
			paintTabSelection( g, tabPlacement, x, y, w, h );
	}

	protected void paintTabCloseButton( Graphics g, int tabIndex, int x, int y, int w, int h ) {
		// update state of tab close button
		boolean rollover = (tabIndex == getRolloverTab());
		ButtonModel bm = tabCloseButton.getModel();
		bm.setRollover( rollover && isRolloverTabClose() );
		bm.setPressed( rollover && isPressedTabClose() );

		// paint tab close icon
		Rectangle tabCloseRect = getTabCloseBounds( tabIndex, x, y, w, h, calcRect );
		closeIcon.paintIcon( tabCloseButton, g, tabCloseRect.x, tabCloseRect.y );
	}

	protected void paintTabSeparator( Graphics g, int tabPlacement, int x, int y, int w, int h ) {
		float sepWidth = UIScale.scale( 1f );
		float offset = tabSeparatorsFullHeight ? 0 : UIScale.scale( 5f );

		g.setColor( (tabSeparatorColor != null) ? tabSeparatorColor : contentAreaColor );
		if( tabPlacement == LEFT || tabPlacement == RIGHT ) {
			// paint tab separator at bottom side
			((Graphics2D)g).fill( new Rectangle2D.Float( x + offset, y + h - sepWidth, w - (offset * 2), sepWidth ) );
		} else if( isLeftToRight() ) {
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
		int tabSelectionHeight = scale( this.tabSelectionHeight );
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
	 *   - tabsOverlapBorder is always true
	 *   - paint full border (if enabled)
	 *   - not invoking paintContentBorder*Edge() methods
	 *   - repaint selection
	 */
	@Override
	protected void paintContentBorder( Graphics g, int tabPlacement, int selectedIndex ) {
		if( tabPane.getTabCount() <= 0 ||
			contentSeparatorHeight == 0 ||
			!clientPropertyBoolean( tabPane, TABBED_PANE_SHOW_CONTENT_SEPARATOR, true ) )
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
				y -= tabAreaInsets.bottom;
				h -= (y - insets.top);
				break;

			case BOTTOM:
				h -= calculateTabAreaHeight( tabPlacement, runCount, maxTabHeight );
				h += tabAreaInsets.top;
				break;

			case LEFT:
				x += calculateTabAreaWidth( tabPlacement, runCount, maxTabWidth );
				x -= tabAreaInsets.right;
				w -= (x - insets.left);
				break;

			case RIGHT:
				w -= calculateTabAreaWidth( tabPlacement, runCount, maxTabWidth );
				w += tabAreaInsets.left;
				break;
		}

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

	protected String layoutAndClipLabel( int tabPlacement, FontMetrics metrics, int tabIndex,
		String title, Icon icon, Rectangle tabRect, Rectangle iconRect, Rectangle textRect, boolean isSelected )
	{
		// remove tab insets and space for close button from the tab rectangle
		// to get correctly clipped title
		tabRect = FlatUIUtils.subtractInsets( tabRect, getTabInsets( tabPlacement, tabIndex ) );
		if( isTabClosable( tabIndex ) ) {
			tabRect.width -= closeIcon.getIconWidth();
			if( !isLeftToRight() )
				tabRect.x += closeIcon.getIconWidth();
		}

		// reset rectangles
		textRect.setBounds( 0, 0, 0, 0 );
		iconRect.setBounds( 0, 0, 0, 0 );

		// temporary set "html" client property on tabbed pane, which is used by SwingUtilities.layoutCompoundLabel()
		View view = getTextViewForTab( tabIndex );
		if( view != null )
			tabPane.putClientProperty( "html", view );

		// layout label
		String clippedTitle = SwingUtilities.layoutCompoundLabel( tabPane, metrics, title, icon,
			SwingUtilities.CENTER, SwingUtilities.CENTER,
			SwingUtilities.CENTER, SwingUtilities.TRAILING,
			tabRect, iconRect, textRect, scale( textIconGapUnscaled ) );

		// remove temporary client property
		tabPane.putClientProperty( "html", null );

		return clippedTitle;
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

	protected Rectangle getTabCloseBounds( int tabIndex, int x, int y, int w, int h, Rectangle dest ) {
		int iconWidth = closeIcon.getIconWidth();
		int iconHeight = closeIcon.getIconHeight();
		Insets tabInsets = getTabInsets( tabPane.getTabPlacement(), tabIndex );

		// use one-third of right/left tab insets as gap between tab text and close button
		dest.x = isLeftToRight()
			? (x + w - (tabInsets.right / 3 * 2) - iconWidth)
			: (x + (tabInsets.left / 3 * 2));
		dest.y = y + (h - iconHeight) / 2;
		dest.width = iconWidth;
		dest.height = iconHeight;
		return dest;
	}

	protected Rectangle getTabCloseHitArea( int tabIndex ) {
		Rectangle tabRect = getTabBounds( tabPane, tabIndex );
		Rectangle tabCloseRect = getTabCloseBounds( tabIndex, tabRect.x, tabRect.y, tabRect.width, tabRect.height, calcRect );
		return new Rectangle( tabCloseRect.x, tabRect.y, tabCloseRect.width, tabRect.height );
	}

	protected boolean isTabClosable( int tabIndex ) {
		Object value = getTabClientProperty( tabIndex, TABBED_PANE_TAB_CLOSABLE );
		return (value instanceof Boolean) ? (boolean) value : false;
	}

	@SuppressWarnings( { "unchecked" } )
	protected void closeTab( int tabIndex ) {
		Object callback = getTabClientProperty( tabIndex, TABBED_PANE_TAB_CLOSE_CALLBACK );
		if( callback instanceof IntConsumer )
			((IntConsumer)callback).accept( tabIndex );
		else if( callback instanceof BiConsumer )
			((BiConsumer<JTabbedPane, Integer>)callback).accept( tabPane, tabIndex );
		else {
			throw new RuntimeException( "Missing tab close callback. "
				+ "Set client property 'JTabbedPane.tabCloseCallback' "
				+ "to a 'java.util.function.IntConsumer' "
				+ "or 'java.util.function.BiConsumer<JTabbedPane, Integer>'" );
		}
	}

	protected Object getTabClientProperty( int tabIndex, String key ) {
		if( tabIndex < 0 )
			return null;

		Component c = tabPane.getComponentAt( tabIndex );
		if( c instanceof JComponent ) {
			Object value = ((JComponent)c).getClientProperty( key );
			if( value != null )
				return value;
		}
		return tabPane.getClientProperty( key );
	}

	protected int getTabClientPropertyInt( int tabIndex, String key, int defaultValue ) {
		Object value = getTabClientProperty( tabIndex, key );
		return (value instanceof Integer) ? (int) value : defaultValue;
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

	private boolean isLeftToRight() {
		return tabPane.getComponentOrientation().isLeftToRight();
	}

	protected boolean isHorizontalTabPlacement() {
		int tabPlacement = tabPane.getTabPlacement();
		return tabPlacement == TOP || tabPlacement == BOTTOM;
	}

	protected boolean isSmoothScrollingEnabled() {
		if( !Animator.useAnimation() )
			return false;

		// Note: Getting UI value "ScrollPane.smoothScrolling" here to allow
		// applications to turn smooth scrolling on or off at any time
		// (e.g. in application options dialog).
		return UIManager.getBoolean( "ScrollPane.smoothScrolling" );
	}

	protected int getHiddenTabsNavigation() {
		String str = (String) tabPane.getClientProperty( TABBED_PANE_HIDDEN_TABS_NAVIGATION );
		if( str == null )
			str = hiddenTabsNavigationStr;
		return parseHiddenTabsNavigation( str );
	}

	protected int getTabAreaAlignment() {
		String str = (String) tabPane.getClientProperty( TABBED_PANE_TAB_AREA_ALIGNMENT );
		if( str == null )
			str = tabAreaAlignmentStr;
		return parseTabAreaAlignment( str );
	}

	protected static int parseHiddenTabsNavigation( String str ) {
		if( str == null )
			return MORE_TABS_BUTTON;

		switch( str ) {
			default:
			case TABBED_PANE_HIDDEN_TABS_NAVIGATION_MORE_TABS_BUTTON:	return MORE_TABS_BUTTON;
			case TABBED_PANE_HIDDEN_TABS_NAVIGATION_ARROW_BUTTONS:		return ARROW_BUTTONS;
		}
	}

	protected static int parseTabAreaAlignment( String str ) {
		if( str == null )
			return ALIGN_LEADING;

		switch( str ) {
			default:
			case TABBED_PANE_TAB_AREA_ALIGN_LEADING:	return ALIGN_LEADING;
			case TABBED_PANE_TAB_AREA_ALIGN_TRAILING:	return ALIGN_TRAILING;
			case TABBED_PANE_TAB_AREA_ALIGN_CENTER:		return ALIGN_CENTER;
			case TABBED_PANE_TAB_AREA_ALIGN_FILL:		return ALIGN_FILL;
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

	protected void ensureSelectedTabIsVisibleLater() {
		EventQueue.invokeLater( () -> {
			ensureSelectedTabIsVisible();
		} );
	}

	protected void ensureSelectedTabIsVisible() {
		if( tabPane == null || tabViewport == null )
			return;

		ensureCurrentLayout();

		int selectedIndex = tabPane.getSelectedIndex();
		if( selectedIndex < 0 || selectedIndex >= rects.length )
			return;

		((JComponent)tabViewport.getView()).scrollRectToVisible( (Rectangle) rects[selectedIndex].clone() );
	}

	private int getLeadingPreferredWidth() {
		return (leadingComponent != null) ? leadingComponent.getPreferredSize().width : 0;
	}

	private int getLeadingPreferredHeight() {
		return (leadingComponent != null) ? leadingComponent.getPreferredSize().height : 0;
	}

	private int getTrailingPreferredWidth() {
		return (trailingComponent != null) ? trailingComponent.getPreferredSize().width : 0;
	}

	private int getTrailingPreferredHeight() {
		return (trailingComponent != null) ? trailingComponent.getPreferredSize().height : 0;
	}

	private void shiftTabs( int sx, int sy ) {
		if( sx == 0 && sy == 0 )
			return;

		for( int i = 0; i < rects.length; i++ ) {
			// fix x location in rects
			rects[i].x += sx;
			rects[i].y += sy;

			// fix tab component location
			Component c = tabPane.getTabComponentAt( i );
			if( c != null )
				c.setLocation( c.getX() + sx, c.getY() + sy );
		}
	}

	private void stretchTabsWidth( int sw, boolean leftToRight ) {
		int rsw = sw / rects.length;
		int x = rects[0].x - (leftToRight ? 0 : rsw);
		for( int i = 0; i < rects.length; i++ ) {
			// fix tab component location
			Component c = tabPane.getTabComponentAt( i );
			if( c != null )
				c.setLocation( x + (c.getX() - rects[i].x) + (rsw / 2), c.getY() );

			// fix x location and width in rects
			rects[i].x = x;
			rects[i].width += rsw;

			if( leftToRight )
				x += rects[i].width;
			else if( i + 1 < rects.length )
				x = rects[i].x - rects[i+1].width - rsw;
		}

		// fix width of last tab
		int diff = sw - (rsw * rects.length);
		rects[rects.length-1].width += diff;
		if( !leftToRight )
			rects[rects.length-1].x -= diff;
	}

	private void stretchTabsHeight( int sh ) {
		int rsh = sh / rects.length;
		int y = rects[0].y;
		for( int i = 0; i < rects.length; i++ ) {
			// fix tab component location
			Component c = tabPane.getTabComponentAt( i );
			if( c != null )
				c.setLocation( c.getX(), y + (c.getY() - rects[i].y) + (rsh / 2) );

			// fix y location and height in rects
			rects[i].y = y;
			rects[i].height += rsh;

			y += rects[i].height;
		}

		// fix height of last tab
		rects[rects.length-1].height += (sh - (rsh * rects.length));
	}

	private int rectsTotalWidth( boolean leftToRight ) {
		int last = rects.length - 1;
		return leftToRight
			? (rects[last].x + rects[last].width) - rects[0].x
			: (rects[0].x + rects[0].width) - rects[last].x;
	}

	private int rectsTotalHeight() {
		int last = rects.length - 1;
		return (rects[last].y + rects[last].height) - rects[0].y;
	}

	//---- class TabCloseButton -----------------------------------------------

	private class TabCloseButton
		extends JButton
		implements UIResource
	{
		private TabCloseButton() {
		}
	}

	//---- class ContainerUIResource ------------------------------------------

	private class ContainerUIResource
		extends JPanel
		implements UIResource
	{
		private ContainerUIResource( Component c ) {
			super( new BorderLayout() );
			add( c );
		}
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
		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();
			boolean horizontal = (direction == SOUTH || direction == NORTH);
			int margin = scale( 8 );
			return new Dimension(
				size.width + (horizontal ? margin : 0),
				size.height + (horizontal ? 0 : margin) );
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

			// compute popup menu location
			int buttonWidth = getWidth();
			int buttonHeight = getHeight();
			Dimension popupSize = popupMenu.getPreferredSize();

			int x = isLeftToRight() ? buttonWidth - popupSize.width : 0;
			int y = buttonHeight - popupSize.height;
			switch( tabPane.getTabPlacement() ) {
				default:
				case TOP:		y = buttonHeight; break;
				case BOTTOM:	y = -popupSize.height; break;
				case LEFT:		x = buttonWidth; break;
				case RIGHT:		x = -popupSize.width; break;
			}

			// show popup menu
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

			menuItem.addActionListener( e -> selectTab( index ) );
			return menuItem;
		}

		protected void selectTab( int index ) {
			tabPane.setSelectedIndex( index );
			ensureSelectedTabIsVisible();
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
		protected void fireActionPerformed( ActionEvent event ) {
			runWithOriginalLayoutManager( () -> {
				super.fireActionPerformed( event );
			} );
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
			int amount = (int) (maxTabHeight * preciseWheelRotation);

			// compute new view position
			Point viewPosition = (targetViewPosition != null)
				? targetViewPosition
				: tabViewport.getViewPosition();
			Dimension viewSize = tabViewport.getViewSize();
			int x = viewPosition.x;
			int y = viewPosition.y;
			int tabPlacement = tabPane.getTabPlacement();
			if( tabPlacement == TOP || tabPlacement == BOTTOM ) {
				x += isLeftToRight() ? amount : -amount;
				x = Math.min( Math.max( x, 0 ), viewSize.width - tabViewport.getWidth() );
			} else {
				y += amount;
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
		implements MouseListener, MouseMotionListener, PropertyChangeListener,
			ChangeListener, ComponentListener, ContainerListener
	{
		MouseListener mouseDelegate;
		PropertyChangeListener propertyChangeDelegate;
		ChangeListener changeDelegate;

		private final PropertyChangeListener contentListener = this::contentPropertyChange;

		private int pressedTabIndex = -1;
		private int lastTipTabIndex = -1;
		private String lastTip;

		void installListeners() {
			tabPane.addMouseMotionListener( this );
			tabPane.addComponentListener( this );
			tabPane.addContainerListener( this );

			for( Component c : tabPane.getComponents() ) {
				if( !(c instanceof UIResource) )
					c.addPropertyChangeListener( contentListener );
			}
		}

		void uninstallListeners() {
			tabPane.removeMouseMotionListener( this );
			tabPane.removeComponentListener( this );
			tabPane.removeContainerListener( this );

			for( Component c : tabPane.getComponents() ) {
				if( !(c instanceof UIResource) )
					c.removePropertyChangeListener( contentListener );
			}
		}

		//---- interface MouseListener ----

		@Override
		public void mouseClicked( MouseEvent e ) {
			mouseDelegate.mouseClicked( e );
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			updateRollover( e );

			if( !isPressedTabClose() )
				mouseDelegate.mousePressed( e );
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			if( isPressedTabClose() ) {
				updateRollover( e );
				if( pressedTabIndex >= 0 && pressedTabIndex == getRolloverTab() )
					closeTab( pressedTabIndex );
			} else
				mouseDelegate.mouseReleased( e );

			pressedTabIndex = -1;
			updateRollover( e );
		}

		@Override
		public void mouseEntered( MouseEvent e ) {
			// this is necessary for "more tabs" button
			updateRollover( e );
		}

		@Override
		public void mouseExited( MouseEvent e ) {
			// this event occurs also if mouse is moved to a custom tab component
			// that handles mouse events (e.g. a close button)
			// --> make sure that the tab stays highlighted
			updateRollover( e );
		}

		//---- interface MouseMotionListener ----

		@Override
		public void mouseDragged( MouseEvent e ) {
			updateRollover( e );
		}

		@Override
		public void mouseMoved( MouseEvent e ) {
			updateRollover( e );
		}

		private void updateRollover( MouseEvent e ) {
			int x = e.getX();
			int y = e.getY();

			int tabIndex = tabForCoordinate( tabPane, x, y );

			setRolloverTab( tabIndex );

			// check whether mouse hit tab close area
			boolean hitClose = isTabClosable( tabIndex )
				? getTabCloseHitArea( tabIndex ).contains( x, y )
				: false;
			if( e.getID() == MouseEvent.MOUSE_PRESSED )
				pressedTabIndex = hitClose ? tabIndex : -1;
			setRolloverTabClose( hitClose );
			setPressedTabClose( hitClose && tabIndex == pressedTabIndex );

			// update tooltip
			if( tabIndex >= 0 && hitClose ) {
				Object closeTip = getTabClientProperty( tabIndex, TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT );
				if( closeTip instanceof String )
					setCloseToolTip( tabIndex, (String) closeTip );
				else
					restoreTabToolTip();
			} else
				restoreTabToolTip();
		}

		private void setCloseToolTip( int tabIndex, String closeTip ) {
			if( tabIndex == lastTipTabIndex )
				return; // closeTip already set

			if( tabIndex != lastTipTabIndex )
				restoreTabToolTip();

			lastTipTabIndex = tabIndex;
			lastTip = tabPane.getToolTipTextAt( lastTipTabIndex );
			tabPane.setToolTipTextAt( lastTipTabIndex, closeTip );
		}

		private void restoreTabToolTip() {
			if( lastTipTabIndex < 0 )
				return;

			tabPane.setToolTipTextAt( lastTipTabIndex, lastTip );
			lastTip = null;
			lastTipTabIndex = -1;
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

				case "componentOrientation":
					ensureSelectedTabIsVisibleLater();
					break;

				case TABBED_PANE_SHOW_TAB_SEPARATORS:
				case TABBED_PANE_SHOW_CONTENT_SEPARATOR:
				case TABBED_PANE_HAS_FULL_BORDER:
				case TABBED_PANE_MINIMUM_TAB_WIDTH:
				case TABBED_PANE_MAXIMUM_TAB_WIDTH:
				case TABBED_PANE_TAB_HEIGHT:
				case TABBED_PANE_TAB_INSETS:
				case TABBED_PANE_HIDDEN_TABS_NAVIGATION:
				case TABBED_PANE_TAB_AREA_ALIGNMENT:
				case TABBED_PANE_TAB_CLOSABLE:
					tabPane.revalidate();
					tabPane.repaint();
					break;

				case TABBED_PANE_LEADING_COMPONENT:
					uninstallLeadingComponent();
					installLeadingComponent();
					tabPane.revalidate();
					tabPane.repaint();
					ensureSelectedTabIsVisibleLater();
					break;

				case TABBED_PANE_TRAILING_COMPONENT:
					uninstallTrailingComponent();
					installTrailingComponent();
					tabPane.revalidate();
					tabPane.repaint();
					ensureSelectedTabIsVisibleLater();
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

		protected void contentPropertyChange( PropertyChangeEvent e ) {
			switch( e.getPropertyName() ) {
				case TABBED_PANE_MINIMUM_TAB_WIDTH:
				case TABBED_PANE_MAXIMUM_TAB_WIDTH:
				case TABBED_PANE_TAB_INSETS:
				case TABBED_PANE_TAB_CLOSABLE:
					tabPane.revalidate();
					tabPane.repaint();
					break;
			}
		}

		//---- interface ComponentListener ----

		@Override
		public void componentResized( ComponentEvent e ) {
			// make sure that selected tab stays visible when component size changed
			ensureSelectedTabIsVisibleLater();
		}

		@Override public void componentMoved( ComponentEvent e ) {}
		@Override public void componentShown( ComponentEvent e ) {}
		@Override public void componentHidden( ComponentEvent e ) {}

		//---- interface ContainerListener ----

		@Override
		public void componentAdded( ContainerEvent e ) {
			Component c = e.getChild();
			if( !(c instanceof UIResource) )
				c.addPropertyChangeListener( contentListener );
		}

		@Override
		public void componentRemoved( ContainerEvent e ) {
			Component c = e.getChild();
			if( !(c instanceof UIResource) )
				c.removePropertyChangeListener( contentListener );
		}
	}

	//---- class FlatTabbedPaneLayout -----------------------------------------

	protected class FlatTabbedPaneLayout
		extends TabbedPaneLayout
	{
		@Override
		public void layoutContainer( Container parent ) {
			super.layoutContainer( parent );

			Rectangle bounds = tabPane.getBounds();
			Insets insets = tabPane.getInsets();
			int tabPlacement = tabPane.getTabPlacement();
			int tabAreaAlignment = getTabAreaAlignment();
			Insets tabAreaInsets = getRealTabAreaInsets( tabPlacement );
			boolean leftToRight = isLeftToRight();

			// layout leading and trailing components in tab area
			if( tabPlacement == TOP || tabPlacement == BOTTOM ) {
				// fix x-locations of tabs in right-to-left component orientation
				if( !leftToRight )
					shiftTabs( insets.left + tabAreaInsets.right + getTrailingPreferredWidth(), 0 );

				// tab area height (maxTabHeight is zero if tab count is zero)
				int tabAreaHeight = (maxTabHeight > 0)
					? maxTabHeight
					: Math.max(
						Math.max( getLeadingPreferredHeight(), getTrailingPreferredHeight() ),
						scale( clientPropertyInt( tabPane, TABBED_PANE_TAB_HEIGHT, tabHeight ) ) );

				// tab area bounds
				int tx = insets.left;
				int ty = (tabPlacement == TOP)
					? insets.top + tabAreaInsets.top
					: (bounds.height - insets.bottom - tabAreaInsets.bottom - tabAreaHeight);
				int tw = bounds.width - insets.left - insets.right;
				int th = tabAreaHeight;

				int leadingWidth = getLeadingPreferredWidth();
				int trailingWidth = getTrailingPreferredWidth();

				// apply tab area alignment
				if( runCount == 1 && rects.length > 0 ) {
					int availWidth = tw - leadingWidth - trailingWidth - tabAreaInsets.left - tabAreaInsets.right;
					int totalTabWidth = rectsTotalWidth( leftToRight );
					int diff = availWidth - totalTabWidth;

					switch( tabAreaAlignment ) {
						case ALIGN_LEADING:
							trailingWidth += diff;
							break;

						case ALIGN_TRAILING:
							shiftTabs( leftToRight ? diff : -diff, 0 );
							leadingWidth += diff;
							break;

						case ALIGN_CENTER:
							shiftTabs( (leftToRight ? diff : -diff) / 2, 0 );
							leadingWidth += diff / 2;
							trailingWidth += diff - (diff / 2);
							break;

						case ALIGN_FILL:
							stretchTabsWidth( diff, leftToRight );
							break;
					}
				} else if( rects.length == 0 )
					trailingWidth = tw - leadingWidth;

				// layout left component
				Container leftComponent = leftToRight ? leadingComponent : trailingComponent;
				if( leftComponent != null ) {
					int leftWidth = leftToRight ? leadingWidth : trailingWidth;
					leftComponent.setBounds( tx, ty, leftWidth, th );
				}

				// layout right component
				Container rightComponent = leftToRight ? trailingComponent : leadingComponent;
				if( rightComponent != null ) {
					int rightWidth = leftToRight ? trailingWidth : leadingWidth;
					rightComponent.setBounds( tx + tw - rightWidth, ty, rightWidth, th );
				}
			} else { // LEFT and RIGHT tab placement
				// tab area width (maxTabWidth is zero if tab count is zero)
				int tabAreaWidth = (maxTabWidth > 0)
					? maxTabWidth
					: Math.max( getLeadingPreferredWidth(), getTrailingPreferredWidth() );

				// tab area bounds
				int tx = (tabPlacement == LEFT)
					? insets.left + tabAreaInsets.left
					: (bounds.width - insets.right - tabAreaInsets.right - tabAreaWidth);
				int ty = insets.top;
				int tw = tabAreaWidth;
				int th = bounds.height - insets.top - insets.bottom;

				int topHeight = getLeadingPreferredHeight();
				int bottomHeight = getTrailingPreferredHeight();

				// apply tab area alignment
				if( runCount == 1 && rects.length > 0 ) {
					int availHeight = th - topHeight - bottomHeight - tabAreaInsets.top - tabAreaInsets.bottom;
					int totalTabHeight = rectsTotalHeight();
					int diff = availHeight - totalTabHeight;

					switch( tabAreaAlignment ) {
						case ALIGN_LEADING:
							bottomHeight += diff;
							break;

						case ALIGN_TRAILING:
							shiftTabs( 0, diff );
							topHeight += diff;
							break;

						case ALIGN_CENTER:
							shiftTabs( 0, (diff) / 2 );
							topHeight += diff / 2;
							bottomHeight += diff - (diff / 2);
							break;

						case ALIGN_FILL:
							stretchTabsHeight( diff );
							break;
					}
				} else if( rects.length == 0 )
					bottomHeight = th - topHeight;

				// layout top component
				if( leadingComponent != null )
					leadingComponent.setBounds( tx, ty, tw, topHeight );

				// layout bottom component
				if( trailingComponent != null )
					trailingComponent.setBounds( tx, ty + th - bottomHeight, tw, bottomHeight );
			}
		}
	}

	//---- class FlatTabbedPaneScrollLayout -----------------------------------

	/**
	 * Layout manager used for scroll tab layout policy.
	 * <p>
	 * Although this class delegates all methods to the original layout manager
	 * {@code BasicTabbedPaneUI.TabbedPaneScrollLayout}, which extends
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
		public void layoutContainer( Container parent ) {
			// delegate to original layout manager and let it layout tabs and buttons
			//
			// runWithOriginalLayoutManager() is necessary for correct locations
			// of tab components layed out in TabbedPaneLayout.layoutTabComponents()
			runWithOriginalLayoutManager( () -> {
				delegate.layoutContainer( parent );
			} );

			boolean useMoreButton = (getHiddenTabsNavigation() == MORE_TABS_BUTTON);

			// for right-to-left always use "more tabs" button for horizontal scrolling
			// because methods scrollForward() and scrollBackward() in class
			// BasicTabbedPaneUI.ScrollableTabSupport do not work for right-to-left
			boolean leftToRight = isLeftToRight();
			if( !leftToRight && !useMoreButton && isHorizontalTabPlacement() )
				useMoreButton = true;

			// find backward/forward scroll buttons
			JButton backwardButton = null;
			JButton forwardButton = null;
			for( Component c : tabPane.getComponents() ) {
				if( c instanceof FlatScrollableTabButton ) {
					int direction = ((FlatScrollableTabButton)c).getDirection();
					if( direction == WEST || direction == NORTH )
						backwardButton = (JButton) c;
					else if( direction == EAST || direction == SOUTH )
						forwardButton = (JButton) c;
				}
			}

			if( !useMoreButton && (backwardButton == null || forwardButton == null) )
				return; // should never occur

			Rectangle bounds = tabPane.getBounds();
			Insets insets = tabPane.getInsets();
			int tabPlacement = tabPane.getTabPlacement();
			int tabAreaAlignment = getTabAreaAlignment();
			Insets tabAreaInsets = getRealTabAreaInsets( tabPlacement );
			Dimension moreButtonSize = useMoreButton ? moreTabsButton.getPreferredSize() : null;
			Dimension backwardButtonSize = useMoreButton ? null : backwardButton.getPreferredSize();
			Dimension forwardButtonSize = useMoreButton ? null : forwardButton.getPreferredSize();
			boolean buttonsVisible = false;

			// TabbedPaneScrollLayout adds tabAreaInsets to tab coordinates,
			// but we use it to position the viewport
			if( tabAreaInsets.left != 0 || tabAreaInsets.top != 0 ) {
				// remove tabAreaInsets from tab locations
				shiftTabs( -tabAreaInsets.left, -tabAreaInsets.top );

				// reduce preferred size of view
				Component view = tabViewport.getView();
				Dimension viewSize = view.getPreferredSize();
				boolean horizontal = (tabPlacement == TOP || tabPlacement == BOTTOM);
				view.setPreferredSize( new Dimension(
					viewSize.width - (horizontal ? tabAreaInsets.left : 0),
					viewSize.height - (horizontal ? 0 : tabAreaInsets.top) ) );
			}

			// layout tab area
			if( tabPlacement == TOP || tabPlacement == BOTTOM ) {
				// tab area height (maxTabHeight is zero if tab count is zero)
				int tabAreaHeight = (maxTabHeight > 0)
					? maxTabHeight
					: Math.max(
						Math.max( getLeadingPreferredHeight(), getTrailingPreferredHeight() ),
						scale( clientPropertyInt( tabPane, TABBED_PANE_TAB_HEIGHT, tabHeight ) ) );

				// tab area bounds
				int tx = insets.left;
				int ty = (tabPlacement == TOP)
					? insets.top + tabAreaInsets.top
					: (bounds.height - insets.bottom - tabAreaInsets.bottom - tabAreaHeight);
				int tw = bounds.width - insets.left - insets.right;
				int th = tabAreaHeight;

				int leadingWidth = getLeadingPreferredWidth();
				int trailingWidth = getTrailingPreferredWidth();
				int availWidth = tw - leadingWidth - trailingWidth - tabAreaInsets.left - tabAreaInsets.right;
				int totalTabWidth = (rects.length > 0) ? rectsTotalWidth( leftToRight ) : 0;

				// apply tab area alignment
				if( totalTabWidth < availWidth && rects.length > 0 ) {
					int diff = availWidth - totalTabWidth;
					switch( tabAreaAlignment ) {
						case ALIGN_LEADING:
							trailingWidth += diff;
							break;

						case ALIGN_TRAILING:
							leadingWidth += diff;
							break;

						case ALIGN_CENTER:
							leadingWidth += diff / 2;
							trailingWidth += diff - (diff / 2);
							break;

						case ALIGN_FILL:
							stretchTabsWidth( diff, leftToRight );
							totalTabWidth = rectsTotalWidth( leftToRight );
							break;
					}
				} else if( rects.length == 0 )
					trailingWidth = tw - leadingWidth;

				// layout left component
				Container leftComponent = leftToRight ? leadingComponent : trailingComponent;
				int leftWidth = leftToRight ? leadingWidth : trailingWidth;
				if( leftComponent != null )
					leftComponent.setBounds( tx, ty, leftWidth, th );

				// layout right component
				Container rightComponent = leftToRight ? trailingComponent : leadingComponent;
				int rightWidth = leftToRight ? trailingWidth : leadingWidth;
				if( rightComponent != null )
					rightComponent.setBounds( tx + tw - rightWidth, ty, rightWidth, th );

				// layout tab viewport and buttons
				if( rects.length > 0 ) {
					int txi = tx + leftWidth + (leftToRight ? tabAreaInsets.left : tabAreaInsets.right);
					int twi = tw - leftWidth - rightWidth - tabAreaInsets.left - tabAreaInsets.right;

					// layout viewport and buttons
					int viewportWidth = twi;
					if( viewportWidth < totalTabWidth ) {
						// need buttons
						buttonsVisible = true;
						int buttonsWidth = useMoreButton ? moreButtonSize.width : (backwardButtonSize.width + forwardButtonSize.width);
						viewportWidth = Math.max( viewportWidth - buttonsWidth, 0 );

						if( useMoreButton )
							moreTabsButton.setBounds( leftToRight ? (txi + twi - buttonsWidth) : txi, ty, moreButtonSize.width, th );
						else {
							backwardButton.setBounds( leftToRight ? (txi + twi - buttonsWidth) : txi, ty, backwardButtonSize.width, th );
							forwardButton.setBounds( leftToRight ? (txi + twi - forwardButtonSize.width) : (txi + backwardButtonSize.width), ty, forwardButtonSize.width, th );
						}
						tabViewport.setBounds( leftToRight ? txi : (txi + buttonsWidth), ty, viewportWidth, th );
					} else
						tabViewport.setBounds( txi, ty, viewportWidth, th );

					if( !leftToRight ) {
						// layout viewport so that we can get correct view width below
						tabViewport.doLayout();

						// fix x-locations of tabs so that they are right-aligned in the view
						shiftTabs( tabViewport.getView().getWidth() - (rects[0].x + rects[0].width), 0 );
					}
				}
			} else { // LEFT and RIGHT tab placement
				// tab area width (maxTabWidth is zero if tab count is zero)
				int tabAreaWidth = (maxTabWidth > 0)
					? maxTabWidth
					: Math.max( getLeadingPreferredWidth(), getTrailingPreferredWidth() );

				// tab area bounds
				int tx = (tabPlacement == LEFT)
					? insets.left + tabAreaInsets.left
					: (bounds.width - insets.right - tabAreaInsets.right - tabAreaWidth);
				int ty = insets.top;
				int tw = tabAreaWidth;
				int th = bounds.height - insets.top - insets.bottom;

				int topHeight = getLeadingPreferredHeight();
				int bottomHeight = getTrailingPreferredHeight();
				int availHeight = th - topHeight - bottomHeight - tabAreaInsets.top - tabAreaInsets.bottom;
				int totalTabHeight = (rects.length > 0) ? rectsTotalHeight() : 0;

				// apply tab area alignment
				if( totalTabHeight < availHeight && rects.length > 0 ) {
					int diff = availHeight - totalTabHeight;
					switch( tabAreaAlignment ) {
						case ALIGN_LEADING:
							bottomHeight += diff;
							break;

						case ALIGN_TRAILING:
							topHeight += diff;
							break;

						case ALIGN_CENTER:
							topHeight += diff / 2;
							bottomHeight += diff - (diff / 2);
							break;

						case ALIGN_FILL:
							stretchTabsHeight( diff );
							totalTabHeight = rectsTotalHeight();
							break;
					}
				} else if( rects.length == 0 )
					bottomHeight = th - topHeight;

				// layout top component
				if( leadingComponent != null )
					leadingComponent.setBounds( tx, ty, tw, topHeight );

				// layout bottom component
				if( trailingComponent != null )
					trailingComponent.setBounds( tx, ty + th - bottomHeight, tw, bottomHeight );

				// layout tab viewport and buttons
				if( rects.length > 0 ) {
					int tyi = ty + topHeight + tabAreaInsets.top;
					int thi = th - topHeight - bottomHeight - tabAreaInsets.top - tabAreaInsets.bottom;

					// layout viewport and buttons
					int viewportHeight = thi;
					if( viewportHeight < totalTabHeight ) {
						// need buttons
						buttonsVisible = true;
						int buttonsHeight = useMoreButton ? moreButtonSize.height : (backwardButtonSize.height + forwardButtonSize.height);
						viewportHeight = Math.max( viewportHeight - buttonsHeight, 0 );

						if( useMoreButton )
							moreTabsButton.setBounds( tx, tyi + thi - buttonsHeight, tw, moreButtonSize.height );
						else {
							backwardButton.setBounds( tx, tyi + thi - buttonsHeight, tw, backwardButtonSize.height );
							forwardButton.setBounds( tx, tyi + thi - forwardButtonSize.height, tw, forwardButtonSize.height );
						}
					}
					tabViewport.setBounds( tx, tyi, tw, viewportHeight );
				}
			}

			// show/hide viewport and buttons
			tabViewport.setVisible( rects.length > 0 );
			moreTabsButton.setVisible( useMoreButton && buttonsVisible );
			backwardButton.setVisible( !useMoreButton && buttonsVisible );
			forwardButton.setVisible( !useMoreButton && buttonsVisible );
		}
	}
}
