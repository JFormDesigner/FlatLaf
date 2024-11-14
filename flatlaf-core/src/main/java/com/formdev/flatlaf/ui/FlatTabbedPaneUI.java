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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatTabbedPaneCloseIcon;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.UnknownStyleException;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.JavaCompatibility;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.StringUtils;
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
 * @uiDefault TabbedPane.shadow							Color	used for cropped line
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
 * @uiDefault TabbedPane.disabledForeground				Color
 * @uiDefault TabbedPane.selectedBackground				Color	optional
 * @uiDefault TabbedPane.selectedForeground				Color	optional
 * @uiDefault TabbedPane.underlineColor					Color
 * @uiDefault TabbedPane.inactiveUnderlineColor			Color
 * @uiDefault TabbedPane.disabledUnderlineColor			Color
 * @uiDefault TabbedPane.hoverColor						Color	optional
 * @uiDefault TabbedPane.hoverForeground				Color	optional
 * @uiDefault TabbedPane.focusColor						Color	optional
 * @uiDefault TabbedPane.focusForeground				Color	optional
 * @uiDefault TabbedPane.tabSeparatorColor				Color	optional; defaults to TabbedPane.contentAreaColor
 * @uiDefault TabbedPane.contentAreaColor				Color
 * @uiDefault TabbedPane.minimumTabWidth				int		optional
 * @uiDefault TabbedPane.maximumTabWidth				int		optional
 * @uiDefault TabbedPane.tabHeight						int
 * @uiDefault TabbedPane.tabSelectionHeight				int
 * @uiDefault TabbedPane.cardTabSelectionHeight			int
 * @uiDefault TabbedPane.tabArc							int
 * @uiDefault TabbedPane.tabSelectionArc				int
 * @uiDefault TabbedPane.cardTabArc						int
 * @uiDefault TabbedPane.selectedInsets					Insets
 * @uiDefault TabbedPane.tabSelectionInsets				Insets
 * @uiDefault TabbedPane.contentSeparatorHeight			int
 * @uiDefault TabbedPane.showTabSeparators				boolean
 * @uiDefault TabbedPane.tabSeparatorsFullHeight		boolean
 * @uiDefault TabbedPane.hasFullBorder					boolean
 * @uiDefault TabbedPane.rotateTabRuns					boolean
 *
 * @uiDefault TabbedPane.tabLayoutPolicy				String	wrap (default) or scroll
 * @uiDefault TabbedPane.tabType						String	underlined (default) or card
 * @uiDefault TabbedPane.tabsPopupPolicy				String	never or asNeeded (default)
 * @uiDefault TabbedPane.scrollButtonsPolicy			String	never, asNeeded or asNeededSingle (default)
 * @uiDefault TabbedPane.scrollButtonsPlacement			String	both (default) or trailing
 *
 * @uiDefault TabbedPane.tabAreaAlignment				String	leading (default), center, trailing or fill
 * @uiDefault TabbedPane.tabAlignment					String	leading, center (default) or trailing
 * @uiDefault TabbedPane.tabWidthMode					String	preferred (default), equal or compact
 * @uiDefault TabbedPane.tabRotation					String	none (default), auto, left or right
 * @uiDefault ScrollPane.smoothScrolling				boolean
 * @uiDefault TabbedPane.closeIcon						Icon
 *
 * @uiDefault TabbedPane.arrowType						String	chevron (default) or triangle
 * @uiDefault TabbedPane.buttonInsets					Insets
 * @uiDefault TabbedPane.buttonArc						int
 * @uiDefault TabbedPane.buttonHoverBackground			Color
 * @uiDefault TabbedPane.buttonPressedBackground		Color
 *
 * @uiDefault TabbedPane.moreTabsButtonToolTipText		String
 * @uiDefault TabbedPane.tabCloseToolTipText			String
 *
 * @author Karl Tauber
 */
public class FlatTabbedPaneUI
	extends BasicTabbedPaneUI
	implements StyleableUI, FlatTitlePane.TitleBarCaptionHitTest
{
	// tab type
	/** @since 2 */ protected static final int TAB_TYPE_UNDERLINED = 0;
	/** @since 2 */ protected static final int TAB_TYPE_CARD = 1;

	// tabs popup policy / scroll arrows policy
	protected static final int NEVER = 0;
//	protected static final int ALWAYS = 1;
	protected static final int AS_NEEDED = 2;
	protected static final int AS_NEEDED_SINGLE = 3;

	// scroll arrows placement
	protected static final int BOTH = 100;

	// tab area alignment
	protected static final int FILL = 100;

	// tab width mode
	protected static final int WIDTH_MODE_PREFERRED = 0;
	protected static final int WIDTH_MODE_EQUAL = 1;
	protected static final int WIDTH_MODE_COMPACT = 2;

	// tab rotation
	/** @since 3.3 */ protected static final int NONE = -1;
	/** @since 3.3 */ protected static final int AUTO = -2;

	private static Set<KeyStroke> focusForwardTraversalKeys;
	private static Set<KeyStroke> focusBackwardTraversalKeys;

	protected Color foreground;
	@Styleable protected Color disabledForeground;
	@Styleable protected Color selectedBackground;
	@Styleable protected Color selectedForeground;
	@Styleable protected Color underlineColor;
	/** @since 2.2 */ @Styleable protected Color inactiveUnderlineColor;
	@Styleable protected Color disabledUnderlineColor;
	@Styleable protected Color hoverColor;
	/** @since 3.1 */ @Styleable protected Color hoverForeground;
	@Styleable protected Color focusColor;
	/** @since 3.1 */ @Styleable protected Color focusForeground;
	@Styleable protected Color tabSeparatorColor;
	@Styleable protected Color contentAreaColor;

	private int textIconGapUnscaled;
	@Styleable protected int minimumTabWidth;
	@Styleable protected int maximumTabWidth;
	@Styleable protected int tabHeight;
	@Styleable protected int tabSelectionHeight;
	/** @since 2 */ @Styleable protected int cardTabSelectionHeight;
	/** @since 3.2 */ @Styleable protected int tabArc;
	/** @since 3.2 */ @Styleable protected int tabSelectionArc;
	/** @since 3.2 */ @Styleable protected int cardTabArc;
	/** @since 3.2 */ @Styleable protected Insets selectedInsets;
	/** @since 3.2 */ @Styleable protected Insets tabSelectionInsets;
	@Styleable protected int contentSeparatorHeight;
	@Styleable protected boolean showTabSeparators;
	@Styleable protected boolean tabSeparatorsFullHeight;
	@Styleable protected boolean hasFullBorder;
	@Styleable protected boolean tabsOpaque = true;
	/** @since 2.5 */ @Styleable protected boolean rotateTabRuns = true;

	@Styleable(type=String.class) private int tabType;
	@Styleable(type=String.class) private int tabsPopupPolicy;
	@Styleable(type=String.class) private int scrollButtonsPolicy;
	@Styleable(type=String.class) private int scrollButtonsPlacement;

	@Styleable(type=String.class) private int tabAreaAlignment;
	@Styleable(type=String.class) private int tabAlignment;
	@Styleable(type=String.class) private int tabWidthMode;
	/** @since 3.3 */ @Styleable(type=String.class) private int tabRotation;
	protected Icon closeIcon;

	@Styleable protected String arrowType;
	@Styleable protected Insets buttonInsets;
	@Styleable protected int buttonArc;
	@Styleable protected Color buttonHoverBackground;
	@Styleable protected Color buttonPressedBackground;

	@Styleable protected String moreTabsButtonToolTipText;
	/** @since 2 */ @Styleable protected String tabCloseToolTipText;

	// only used via styling (not in UI defaults, but has likewise client properties)
	/** @since 2 */ @Styleable protected boolean showContentSeparator = true;
	/** @since 2 */ @Styleable protected boolean hideTabAreaWithOneTab;
	/** @since 2 */ @Styleable protected boolean tabClosable;
	/** @since 2 */ @Styleable protected int tabIconPlacement = LEADING;

	protected JViewport tabViewport;
	protected FlatWheelTabScroller wheelTabScroller;

	private JButton tabCloseButton;
	private JButton moreTabsButton;
	private Container leadingComponent;
	private Container trailingComponent;

	private Dimension scrollBackwardButtonPrefSize;

	private Handler handler;
	private boolean blockRollover;
	private boolean rolloverTabClose;
	private boolean pressedTabClose;
	private boolean inBasicLayoutContainer;

	private Object[] oldRenderingHints;
	private Map<String, Object> oldStyleValues;
	private boolean closeIconShared = true;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTabbedPaneUI();
	}

	@Override
	public void installUI( JComponent c ) {
		// initialize tab layout policy (if specified)
		String tabLayoutPolicyStr = UIManager.getString( "TabbedPane.tabLayoutPolicy" );
		if( tabLayoutPolicyStr != null ) {
			int tabLayoutPolicy;
			switch( tabLayoutPolicyStr ) {
				default:
				case "wrap":		tabLayoutPolicy = JTabbedPane.WRAP_TAB_LAYOUT; break;
				case "scroll":	tabLayoutPolicy = JTabbedPane.SCROLL_TAB_LAYOUT; break;
			}
			((JTabbedPane)c).setTabLayoutPolicy( tabLayoutPolicy );
		}

		// initialize this defaults here because they are used in constructor
		// of FlatTabAreaButton, which is invoked before installDefaults()
		arrowType = UIManager.getString( "TabbedPane.arrowType" );
		foreground = UIManager.getColor( "TabbedPane.foreground" );
		disabledForeground = UIManager.getColor( "TabbedPane.disabledForeground" );
		buttonHoverBackground = UIManager.getColor( "TabbedPane.buttonHoverBackground" );
		buttonPressedBackground = UIManager.getColor( "TabbedPane.buttonPressedBackground" );

		super.installUI( c );

		FlatSelectedTabRepainter.install();
		installStyle();
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

		selectedBackground = UIManager.getColor( "TabbedPane.selectedBackground" );
		selectedForeground = UIManager.getColor( "TabbedPane.selectedForeground" );
		underlineColor = UIManager.getColor( "TabbedPane.underlineColor" );
		inactiveUnderlineColor = FlatUIUtils.getUIColor( "TabbedPane.inactiveUnderlineColor", underlineColor );
		disabledUnderlineColor = UIManager.getColor( "TabbedPane.disabledUnderlineColor" );
		hoverColor = UIManager.getColor( "TabbedPane.hoverColor" );
		hoverForeground = UIManager.getColor( "TabbedPane.hoverForeground" );
		focusColor = UIManager.getColor( "TabbedPane.focusColor" );
		focusForeground = UIManager.getColor( "TabbedPane.focusForeground" );
		tabSeparatorColor = UIManager.getColor( "TabbedPane.tabSeparatorColor" );
		contentAreaColor = UIManager.getColor( "TabbedPane.contentAreaColor" );

		textIconGapUnscaled = UIManager.getInt( "TabbedPane.textIconGap" );
		minimumTabWidth = UIManager.getInt( "TabbedPane.minimumTabWidth" );
		maximumTabWidth = UIManager.getInt( "TabbedPane.maximumTabWidth" );
		tabHeight = UIManager.getInt( "TabbedPane.tabHeight" );
		tabSelectionHeight = UIManager.getInt( "TabbedPane.tabSelectionHeight" );
		cardTabSelectionHeight = UIManager.getInt( "TabbedPane.cardTabSelectionHeight" );
		tabArc = UIManager.getInt( "TabbedPane.tabArc" );
		tabSelectionArc = UIManager.getInt( "TabbedPane.tabSelectionArc" );
		cardTabArc = UIManager.getInt( "TabbedPane.cardTabArc" );
		selectedInsets = UIManager.getInsets( "TabbedPane.selectedInsets" );
		tabSelectionInsets = UIManager.getInsets( "TabbedPane.tabSelectionInsets" );
		contentSeparatorHeight = UIManager.getInt( "TabbedPane.contentSeparatorHeight" );
		showTabSeparators = UIManager.getBoolean( "TabbedPane.showTabSeparators" );
		tabSeparatorsFullHeight = UIManager.getBoolean( "TabbedPane.tabSeparatorsFullHeight" );
		hasFullBorder = UIManager.getBoolean( "TabbedPane.hasFullBorder" );
		tabsOpaque = UIManager.getBoolean( "TabbedPane.tabsOpaque" );
		rotateTabRuns = FlatUIUtils.getUIBoolean( "TabbedPane.rotateTabRuns", true );

		tabType = parseTabType( UIManager.getString( "TabbedPane.tabType" ) );
		tabsPopupPolicy = parseTabsPopupPolicy( UIManager.getString( "TabbedPane.tabsPopupPolicy" ) );
		scrollButtonsPolicy = parseScrollButtonsPolicy( UIManager.getString( "TabbedPane.scrollButtonsPolicy" ) );
		scrollButtonsPlacement = parseScrollButtonsPlacement( UIManager.getString( "TabbedPane.scrollButtonsPlacement" ) );

		tabAreaAlignment = parseAlignment( UIManager.getString( "TabbedPane.tabAreaAlignment" ), LEADING );
		tabAlignment = parseAlignment( UIManager.getString( "TabbedPane.tabAlignment" ), CENTER );
		tabWidthMode = parseTabWidthMode( UIManager.getString( "TabbedPane.tabWidthMode" ) );
		tabRotation = parseTabRotation( UIManager.getString( "TabbedPane.tabRotation" ) );
		closeIcon = UIManager.getIcon( "TabbedPane.closeIcon" );
		closeIconShared = true;

		buttonInsets = UIManager.getInsets( "TabbedPane.buttonInsets" );
		buttonArc = UIManager.getInt( "TabbedPane.buttonArc" );

		Locale l = tabPane.getLocale();
		moreTabsButtonToolTipText = UIManager.getString( "TabbedPane.moreTabsButtonToolTipText", l );
		tabCloseToolTipText = UIManager.getString( "TabbedPane.tabCloseToolTipText", l );

		// scale
		textIconGap = scale( textIconGapUnscaled );

		// replace focus forward/backward traversal keys with TAB/Shift+TAB because
		// the default also includes Ctrl+TAB/Ctrl+Shift+TAB, which we need to switch tabs
		if( focusForwardTraversalKeys == null ) {
			focusForwardTraversalKeys = Collections.singleton( KeyStroke.getKeyStroke( KeyEvent.VK_TAB, 0 ) );
			focusBackwardTraversalKeys = Collections.singleton( KeyStroke.getKeyStroke( KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK ) );
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

		foreground = null;
		disabledForeground = null;
		selectedBackground = null;
		selectedForeground = null;
		underlineColor = null;
		inactiveUnderlineColor = null;
		disabledUnderlineColor = null;
		hoverColor = null;
		hoverForeground = null;
		focusColor = null;
		focusForeground = null;
		tabSeparatorColor = null;
		contentAreaColor = null;
		closeIcon = null;

		buttonHoverBackground = null;
		buttonPressedBackground = null;

		oldStyleValues = null;

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

		tabCloseButton = null;
		tabViewport = null;
	}

	protected void installHiddenTabsNavigation() {
		if( !isScrollTabLayout() || tabViewport == null )
			return;

		// At this point, BasicTabbedPaneUI already has installed
		// TabbedPaneScrollLayout (in super.createLayoutManager()) and
		// ScrollableTabSupport, ScrollableTabViewport, ScrollableTabPanel, etc.
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

	@Override
	protected void installKeyboardActions() {
		super.installKeyboardActions();

		// get shared action map, used for all tabbed panes
		ActionMap map = SwingUtilities.getUIActionMap( tabPane );
		if( map != null ) {
			// this is required for the case that those actions are used from outside
			// (e.g. wheel tab scroller in NetBeans)
			RunWithOriginalLayoutManagerDelegateAction.install( map, "scrollTabsForwardAction" );
			RunWithOriginalLayoutManagerDelegateAction.install( map, "scrollTabsBackwardAction" );
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
	protected FocusListener createFocusListener() {
		Handler handler = getHandler();
		handler.focusDelegate = super.createFocusListener();
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

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( tabPane, "TabbedPane" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		// update buttons
		for( Component c : tabPane.getComponents() ) {
			if( c instanceof FlatTabAreaButton )
				((FlatTabAreaButton)c).updateStyle();
		}
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		// close icon
		if( key.startsWith( "close" ) ) {
			if( !(closeIcon instanceof FlatTabbedPaneCloseIcon) )
				return new UnknownStyleException( key );

			if( closeIconShared ) {
				closeIcon = FlatStylingSupport.cloneIcon( closeIcon );
				closeIconShared = false;
			}

			return ((FlatTabbedPaneCloseIcon)closeIcon).applyStyleProperty( key, value );
		}

		if( value instanceof String ) {
			switch( key ) {
				case "tabType": value = parseTabType( (String) value ); break;
				case "tabsPopupPolicy": value = parseTabsPopupPolicy( (String) value ); break;
				case "scrollButtonsPolicy": value = parseScrollButtonsPolicy( (String) value ); break;
				case "scrollButtonsPlacement": value = parseScrollButtonsPlacement( (String) value ); break;

				case "tabAreaAlignment": value = parseAlignment( (String) value, LEADING ); break;
				case "tabAlignment": value = parseAlignment( (String) value, CENTER ); break;
				case "tabWidthMode": value = parseTabWidthMode( (String) value ); break;
				case "tabRotation": value = parseTabRotation( (String) value ); break;

				case "tabIconPlacement": value = parseTabIconPlacement( (String) value ); break;
			}
		} else {
			Object oldValue;
			switch( key ) {
				// BasicTabbedPaneUI
				case "tabInsets": oldValue = tabInsets; tabInsets = (Insets) value; return oldValue;
				case "tabAreaInsets": oldValue = tabAreaInsets; tabAreaInsets = (Insets) value; return oldValue;
				case "textIconGap":
					oldValue = textIconGapUnscaled;
					textIconGapUnscaled = (int) value;
					textIconGap = scale( textIconGapUnscaled );
					return oldValue;
			}
		}

		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, tabPane, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		Map<String, Class<?>> infos = new FlatStylingSupport.StyleableInfosMap<>();
		infos.put( "tabInsets", Insets.class );
		infos.put( "tabAreaInsets", Insets.class );
		infos.put( "textIconGap", int.class );
		FlatStylingSupport.collectAnnotatedStyleableInfos( this, infos );
		if( closeIcon instanceof FlatTabbedPaneCloseIcon )
			infos.putAll( ((FlatTabbedPaneCloseIcon)closeIcon).getStyleableInfos() );
		return infos;
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		// close icon
		if( key.startsWith( "close" ) ) {
			return (closeIcon instanceof FlatTabbedPaneCloseIcon)
				? ((FlatTabbedPaneCloseIcon)closeIcon).getStyleableValue( key )
				: null;
		}

		switch( key ) {
			// BasicTabbedPaneUI
			case "tabInsets":		return tabInsets;
			case "tabAreaInsets":	return tabAreaInsets;
			case "textIconGap":		return textIconGapUnscaled;

			// FlatTabbedPaneUI
			case "tabType":
				switch( tabType ) {
					default:
					case TAB_TYPE_UNDERLINED:	return TABBED_PANE_TAB_TYPE_UNDERLINED;
					case TAB_TYPE_CARD:			return TABBED_PANE_TAB_TYPE_CARD;
				}

			case "tabsPopupPolicy":
				switch( tabsPopupPolicy ) {
					default:
					case AS_NEEDED:				return TABBED_PANE_POLICY_AS_NEEDED;
					case NEVER:					return TABBED_PANE_POLICY_NEVER;
				}

			case "scrollButtonsPolicy":
				switch( scrollButtonsPolicy ) {
					default:
					case AS_NEEDED_SINGLE:		return TABBED_PANE_POLICY_AS_NEEDED_SINGLE;
					case AS_NEEDED:				return TABBED_PANE_POLICY_AS_NEEDED;
					case NEVER:					return TABBED_PANE_POLICY_NEVER;
				}

			case "scrollButtonsPlacement":
				switch( scrollButtonsPlacement ) {
					default:
					case BOTH:					return TABBED_PANE_PLACEMENT_BOTH;
					case TRAILING:				return TABBED_PANE_PLACEMENT_TRAILING;
				}

			case "tabAreaAlignment":	return alignmentToString( tabAreaAlignment, TABBED_PANE_ALIGN_LEADING );
			case "tabAlignment":			return alignmentToString( tabAlignment, TABBED_PANE_ALIGN_CENTER );

			case "tabWidthMode":
				switch( tabWidthMode ) {
					default:
					case WIDTH_MODE_PREFERRED:	return TABBED_PANE_TAB_WIDTH_MODE_PREFERRED;
					case WIDTH_MODE_EQUAL:		return TABBED_PANE_TAB_WIDTH_MODE_EQUAL;
					case WIDTH_MODE_COMPACT:	return TABBED_PANE_TAB_WIDTH_MODE_COMPACT;
				}

			case "tabRotation":
				switch( tabRotation ) {
					default:
					case NONE:					return TABBED_PANE_TAB_ROTATION_NONE;
					case AUTO:					return TABBED_PANE_TAB_ROTATION_AUTO;
					case LEFT:					return TABBED_PANE_TAB_ROTATION_LEFT;
					case RIGHT:					return TABBED_PANE_TAB_ROTATION_RIGHT;
				}

			case "tabIconPlacement":
				switch( tabIconPlacement ) {
					default:
					case LEADING:				return "leading";
					case TRAILING:				return "trailing";
					case TOP:					return "top";
					case BOTTOM:				return "bottom";
				}
		}

		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
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

		if( index != oldIndex )
			repaintRolloverLaterOnce( oldIndex );
	}

	private boolean repaintRolloverPending;

	/**
	 * Repaint rollover tab, but deferred and only once.
	 * This is to avoid unnecessary repaints in case of temporary changes to rollover tab.
	 *
	 * E.g. when moving mouse over a single tab, a re-layout may occur and
	 * set rollover tab to -1, in BasicTabbedPaneUI.TabbedPaneLayout.layoutContainer() and
	 * BasicTabbedPaneUI.TabbedPaneScrollLayout.layoutContainer(), and subsequently
	 * change rollover tab back to previous value.
	 */
	private void repaintRolloverLaterOnce( int oldIndex ) {
		if( repaintRolloverPending )
			return;
		repaintRolloverPending = true;

		EventQueue.invokeLater( () -> {
			repaintRolloverPending = false;
			if( tabPane == null )
				return;

			int index = getRolloverTab();
			if( index != oldIndex ) {
				// repaint old and new hover tabs
				repaintTab( oldIndex );
				repaintTab( index );
			}
		} );
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
		if( r == null )
			return;

		// increase size of repaint region to include part of content border
		if( contentSeparatorHeight > 0 &&
			clientPropertyBoolean( tabPane, TABBED_PANE_SHOW_CONTENT_SEPARATOR, true ) )
		{
			int sh = scale( contentSeparatorHeight );
			switch( tabPane.getTabPlacement() ) {
				default:
				case TOP:    r.height += sh; break;
				case BOTTOM: r.height += sh; r.y -= sh; break;
				case LEFT:   r.width  += sh; break;
				case RIGHT:  r.width  += sh; r.x -= sh; break;
			}
		}

		HiDPIUtils.repaint( tabPane, r );
	}

	private boolean inCalculateEqual;

	@Override
	protected int calculateTabWidth( int tabPlacement, int tabIndex, FontMetrics metrics ) {
		return (getRealTabRotation( tabPlacement ) == NONE)
			? calculateTabWidthImpl( tabPlacement, tabIndex, metrics, false )
			: calculateTabHeightImpl( tabPlacement, tabIndex, metrics.getHeight(), true );
	}

	private int calculateTabWidthImpl( int tabPlacement, int tabIndex, FontMetrics metrics, boolean rotated ) {
		int tabWidthMode = getTabWidthMode();
		if( tabWidthMode == WIDTH_MODE_EQUAL && isHorizontalOrRotated( tabPlacement ) && !inCalculateEqual ) {
			inCalculateEqual = true;
			try {
				return isHorizontalTabPlacement( tabPlacement )
					? calculateMaxTabWidth( tabPlacement )
					: calculateMaxTabHeight( tabPlacement );
			} finally {
				inCalculateEqual = false;
			}
		}

		// update textIconGap before used in super class
		textIconGap = scale( textIconGapUnscaled );

		int tabWidth;
		Icon icon;
		if( tabWidthMode == WIDTH_MODE_COMPACT &&
			tabIndex != tabPane.getSelectedIndex() &&
			isHorizontalOrRotated( tabPlacement ) &&
			tabPane.getTabComponentAt( tabIndex ) == null &&
			(icon = getIconForTab( tabIndex )) != null )
		{
			Insets tabInsets = getTabInsets( tabPlacement, tabIndex );
			tabWidth = icon.getIconWidth() + tabInsets.left + tabInsets.right;
		} else {
			int iconPlacement = clientPropertyInt( tabPane, TABBED_PANE_TAB_ICON_PLACEMENT, tabIconPlacement );
			if( (iconPlacement == TOP || iconPlacement == BOTTOM) &&
				tabPane.getTabComponentAt( tabIndex ) == null &&
				(icon = getIconForTab( tabIndex )) != null )
			{
				// TOP and BOTTOM icon placement
				tabWidth = icon.getIconWidth();

				View view = getTextViewForTab( tabIndex );
				if( view != null )
					tabWidth = Math.max( tabWidth, (int) view.getPreferredSpan( View.X_AXIS ) );
				else {
					String title = tabPane.getTitleAt( tabIndex );
					if( title != null )
						tabWidth = Math.max( tabWidth, metrics.stringWidth( title ) );
				}

				Insets tabInsets = getTabInsets( tabPlacement, tabIndex );
				tabWidth += tabInsets.left + tabInsets.right;
			} else {
				tabWidth = super.calculateTabWidth( tabPlacement, tabIndex, metrics ) - 3 /* was added by superclass */;

				// tab components are not rotated
				Component tabComponent;
				if( rotated && (tabComponent = tabPane.getTabComponentAt( tabIndex )) != null ) {
					Dimension prefSize = tabComponent.getPreferredSize();
					tabWidth = tabWidth - prefSize.width + prefSize.height;
				}
			}
		}

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
		return (getRealTabRotation( tabPlacement ) == NONE)
			? calculateTabHeightImpl( tabPlacement, tabIndex, fontHeight, false )
			: calculateTabWidthImpl( tabPlacement, tabIndex, getFontMetrics(), true );
	}

	private int calculateTabHeightImpl( int tabPlacement, int tabIndex, int fontHeight, boolean rotated ) {
		int tabHeight;

		Icon icon;
		int iconPlacement = clientPropertyInt( tabPane, TABBED_PANE_TAB_ICON_PLACEMENT, tabIconPlacement );
		if( (iconPlacement == TOP || iconPlacement == BOTTOM) &&
			tabPane.getTabComponentAt( tabIndex ) == null &&
			(icon = getIconForTab( tabIndex )) != null )
		{
			// TOP and BOTTOM icon placement
			tabHeight = icon.getIconHeight();

			View view = getTextViewForTab( tabIndex );
			if( view != null )
				tabHeight += (int) view.getPreferredSpan( View.Y_AXIS ) + scale( textIconGapUnscaled );
			else if( tabPane.getTitleAt( tabIndex ) != null )
				tabHeight += fontHeight + scale( textIconGapUnscaled );

			Insets tabInsets = getTabInsets( tabPlacement, tabIndex );
			tabHeight += tabInsets.top + tabInsets.bottom;
		} else {
			tabHeight = super.calculateTabHeight( tabPlacement, tabIndex, fontHeight ) - 2 /* was added by superclass */;

			// tab components are not rotated
			Component tabComponent;
			if( rotated && (tabComponent = tabPane.getTabComponentAt( tabIndex )) != null ) {
				Dimension prefSize = tabComponent.getPreferredSize();
				tabHeight = tabHeight - prefSize.height + prefSize.width;
			}
		}

		return Math.max( tabHeight, scale( clientPropertyInt( tabPane, TABBED_PANE_TAB_HEIGHT, this.tabHeight ) ) );
	}

	@Override
	protected int calculateMaxTabWidth( int tabPlacement ) {
		return hideTabArea() ? 0 : super.calculateMaxTabWidth( tabPlacement );
	}

	@Override
	protected int calculateMaxTabHeight( int tabPlacement ) {
		return hideTabArea() ? 0 : super.calculateMaxTabHeight( tabPlacement );
	}

	@Override
	protected int calculateTabAreaWidth( int tabPlacement, int vertRunCount, int maxTabWidth ) {
		return hideTabArea() ? 0 : super.calculateTabAreaWidth( tabPlacement, vertRunCount, maxTabWidth );
	}

	@Override
	protected int calculateTabAreaHeight( int tabPlacement, int horizRunCount, int maxTabHeight ) {
		return hideTabArea() ? 0 : super.calculateTabAreaHeight( tabPlacement, horizRunCount, maxTabHeight );
	}

	@Override
	protected Insets getTabInsets( int tabPlacement, int tabIndex ) {
		Object value = getTabClientProperty( tabIndex, TABBED_PANE_TAB_INSETS );
		return scale( (value instanceof Insets)
			? (Insets) value
			: super.getTabInsets( tabPlacement, tabIndex ) );
	}

	/** @since 3.3 */
	protected Insets getTabInsetsRotated( int tabPlacement, int tabIndex, int rotation ) {
		Insets insets = getTabInsets( tabPlacement, tabIndex );
		switch( rotation ) {
			case LEFT:	return new Insets( insets.right, insets.top, insets.left, insets.bottom );
			case RIGHT:	return new Insets( insets.left, insets.bottom, insets.right, insets.top );
			default:	return insets;
		}
	}

	@Override
	protected Insets getSelectedTabPadInsets( int tabPlacement ) {
		return new Insets( 0, 0, 0, 0 );
	}

	protected Insets getRealTabAreaInsets( int tabPlacement ) {
		// this is to avoid potential NPE in ensureSelectedTabIsVisible()
		// (see https://github.com/JFormDesigner/FlatLaf/issues/299)
		// but now should actually never occur because added more checks to
		// ensureSelectedTabIsVisibleLater() and ensureSelectedTabIsVisible()
		if( tabAreaInsets == null )
			tabAreaInsets = new Insets( 0, 0, 0, 0 );

		Insets currentTabAreaInsets = super.getTabAreaInsets( tabPlacement );
		Insets insets = (Insets) currentTabAreaInsets.clone();

		Object value = tabPane.getClientProperty( TABBED_PANE_TAB_AREA_INSETS );
		if( value instanceof Insets )
			rotateInsets( (Insets) value, insets, tabPlacement );

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
			if( isHorizontalTabPlacement( tabPlacement ) ) {
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
		if( hideTabArea() || contentSeparatorHeight == 0 || !clientPropertyBoolean( tabPane, TABBED_PANE_SHOW_CONTENT_SEPARATOR, showContentSeparator ) )
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
		if( isTabClosable( tabIndex ) && getRealTabRotation( tabPlacement ) == NONE ) {
			int shift = closeIcon.getIconWidth() / 2;
			return isLeftToRight() ? -shift : shift;
		}
		return 0;
	}

	@Override
	protected int getTabLabelShiftY( int tabPlacement, int tabIndex, boolean isSelected ) {
		if( isTabClosable( tabIndex ) && getRealTabRotation( tabPlacement ) != NONE ) {
			int shift = closeIcon.getIconHeight() / 2;
			return isLeftToRight() ? shift : -shift;
		}
		return 0;
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
		if( hideTabArea() )
			return;

		ensureCurrentLayout();

		int tabPlacement = tabPane.getTabPlacement();
		int selectedIndex = tabPane.getSelectedIndex();

		paintContentBorder( g, tabPlacement, selectedIndex );

		// fill tabs area background
		// - for rounded cards use partly rounded rectangle
		if( tabsOpaque && !tabPane.isOpaque() && tabPane.getTabCount() > 0 ) {
			Rectangle tr = null;
			if( isScrollTabLayout() ) {
				// scroll layout: use tab viewport bounds and add visible buttons
				tr = tabViewport.getBounds();
				for( Component child : tabPane.getComponents() ) {
					if( child instanceof FlatTabAreaButton && child.isVisible() )
						tr = tr.union( child.getBounds() );
				}
			} else {
				// wrap layout: use union of all tab rectangles
				for( Rectangle r : rects )
					tr = (tr != null) ? tr.union( r ) : r;
			}

			if( tr != null ) {
				g.setColor( tabPane.getBackground() );

				if( (getTabType() == TAB_TYPE_CARD) && cardTabArc > 0 ) {
					((Graphics2D)g).fill( createCardTabOuterPath( tabPlacement, tr.x, tr.y, tr.width, tr.height ) );
				} else
					g.fillRect( tr.x, tr.y, tr.width, tr.height );
			}
		}

		if( !isScrollTabLayout() )
			paintTabArea( g, tabPlacement, selectedIndex );
	}

	@Override
	protected void paintTabArea( Graphics g, int tabPlacement, int selectedIndex ) {
		// need to set rendering hints here too because this method is also invoked
		// from BasicTabbedPaneUI.ScrollableTabPanel.paintComponent()
		Object[] oldHints = FlatUIUtils.setRenderingHints( g );

		super.paintTabArea( g, tabPlacement, selectedIndex );

		FlatUIUtils.resetRenderingHints( g, oldHints );
	}

	@Override
	protected void paintTab( Graphics g, int tabPlacement, Rectangle[] rects,
		int tabIndex, Rectangle iconRect, Rectangle textRect )
	{
		Rectangle tabRect = rects[tabIndex];
		int x = tabRect.x;
		int y = tabRect.y;
		int w = tabRect.width;
		int h = tabRect.height;
		boolean isSelected = (tabIndex == tabPane.getSelectedIndex());

		// paint background
		if( tabsOpaque || tabPane.isOpaque() )
			paintTabBackground( g, tabPlacement, tabIndex, x, y, w, h, isSelected );

		// paint border
		paintTabBorder( g, tabPlacement, tabIndex, x, y, w, h, isSelected );

		// paint tab close button
		if( isTabClosable( tabIndex ) )
			paintTabCloseButton( g, tabIndex, x, y, w, h );

		// paint selection indicator
		if( isSelected )
			paintTabSelection( g, tabPlacement, tabIndex, x, y, w, h );

		if( tabPane.getTabComponentAt( tabIndex ) != null )
			return;

		// layout title and icon
		String title = tabPane.getTitleAt( tabIndex );
		Icon icon = getIconForTab( tabIndex );
		Font font = tabPane.getFont();
		FontMetrics metrics = tabPane.getFontMetrics( font );
		boolean isCompact = (icon != null && !isSelected && getTabWidthMode() == WIDTH_MODE_COMPACT && isHorizontalOrRotated( tabPlacement ));
		if( isCompact )
			title = null;
		String clippedTitle = layoutAndClipLabel( tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected );

/*debug
		g.setColor( Color.red );
		g.drawRect( tabRect.x, tabRect.y, tabRect.width - 1, tabRect.height - 1 );
		g.setColor( Color.green );
		Rectangle tabRect2 = FlatUIUtils.subtractInsets( tabRect, getTabInsetsRotated( tabPlacement, tabIndex, getRealTabRotation( tabPlacement ) ) );
		g.drawRect( tabRect2.x, tabRect2.y, tabRect2.width - 1, tabRect2.height - 1 );
		g.setColor( Color.blue );
		g.drawRect( iconRect.x, iconRect.y, iconRect.width - 1, iconRect.height - 1 );
		g.setColor( Color.magenta );
		g.drawRect( textRect.x, textRect.y, textRect.width - 1, textRect.height - 1 );
		g.setColor( Color.orange );
		Rectangle closeHitArea = getTabCloseHitArea( tabIndex );
		if( moreTabsButton != null ) {
			Point viewPosition = tabViewport.getViewPosition();
			closeHitArea.x -= tabViewport.getX() - viewPosition.x;
			closeHitArea.y -= tabViewport.getY() - viewPosition.y;
		}
		g.drawRect( closeHitArea.x, closeHitArea.y, closeHitArea.width - 1, closeHitArea.height - 1 );
debug*/

		// special title clipping for scroll layout where title of last visible tab on right side may be truncated
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
		if( !isCompact )
			paintText( g, tabPlacement, font, metrics, tabIndex, clippedTitle, textRect, isSelected );
		paintIcon( g, tabPlacement, tabIndex, icon, iconRect, isSelected );
	}

	@Override
	protected void paintText( Graphics g, int tabPlacement, Font font, FontMetrics metrics,
		int tabIndex, String title, Rectangle textRect, boolean isSelected )
	{
		g.setFont( font );

		FlatUIUtils.runWithoutRenderingHints( g, oldRenderingHints, () -> {
			// html
			View view = getTextViewForTab( tabIndex );
			if( view != null ) {
				AffineTransform oldTransform = rotateGraphics( g, tabPlacement, textRect );
				Rectangle textRect2 = (oldTransform != null)
					? new Rectangle( textRect.x, textRect.y, textRect.height, textRect.width )
					: textRect;

				view.paint( g, textRect2 );

				if( oldTransform != null )
					((Graphics2D)g).setTransform( oldTransform );
				return;
			}

			// rotate text if necessary
			AffineTransform oldTransform = rotateGraphics( g, tabPlacement, textRect );

			// plain text
			int mnemIndex = FlatLaf.isShowMnemonics() ? tabPane.getDisplayedMnemonicIndexAt( tabIndex ) : -1;
			g.setColor( getTabForeground( tabPlacement, tabIndex, isSelected ) );
			FlatUIUtils.drawStringUnderlineCharAt( tabPane, g, title, mnemIndex,
				textRect.x, textRect.y + metrics.getAscent() );

			if( oldTransform != null )
				((Graphics2D)g).setTransform( oldTransform );
		} );
	}

	@Override
	protected void paintIcon( Graphics g, int tabPlacement, int tabIndex, Icon icon, Rectangle iconRect, boolean isSelected ) {
		if( icon == null )
			return;

		// clip icon painting (also done in JDK since Java 10)
		Shape oldClip = g.getClip();
		((Graphics2D)g).clip( iconRect );

		// rotate icon if necessary
		AffineTransform oldTransform = rotateGraphics( g, tabPlacement, iconRect );

		// paint icon
		icon.paintIcon( tabPane, g, iconRect.x, iconRect.y );

		if( oldTransform != null )
			((Graphics2D)g).setTransform( oldTransform );
		g.setClip( oldClip );
	}

	private AffineTransform rotateGraphics( Graphics g, int tabPlacement, Rectangle r ) {
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform oldTransform = null;

		int rotation = getRealTabRotation( tabPlacement );
		if( rotation == LEFT ) {
			oldTransform = g2.getTransform();
			g2.translate( 0, r.height );
			g2.rotate( Math.toRadians( 270 ), r.x, r.y );
		} else if( rotation == RIGHT ) {
			oldTransform = g2.getTransform();
			g2.translate( r.width, 0 );
			g2.rotate( Math.toRadians( 90 ), r.x, r.y );
		}

		return oldTransform;
	}

	/** @since 3.1 */
	protected Color getTabForeground( int tabPlacement, int tabIndex, boolean isSelected ) {
		// tabbed pane or tab is disabled
		if( !tabPane.isEnabled() || !tabPane.isEnabledAt( tabIndex ) )
			return disabledForeground;

		// hover
		if( hoverForeground != null && getRolloverTab() == tabIndex )
			return hoverForeground;

		// tab foreground (if set)
		Color foreground = tabPane.getForegroundAt( tabIndex );
		if( foreground != tabPane.getForeground() )
			return foreground;

		// focused and selected
		if( focusForeground != null && isSelected && FlatUIUtils.isPermanentFocusOwner( tabPane ) )
			return focusForeground;
		if( selectedForeground != null && isSelected )
			return selectedForeground;

		return foreground;
	}

	@Override
	protected void paintTabBackground( Graphics g, int tabPlacement, int tabIndex,
		int x, int y, int w, int h, boolean isSelected )
	{
		boolean isCard = (getTabType() == TAB_TYPE_CARD);

		// apply insets
		if( !isCard && selectedInsets != null ) {
			Insets insets = new Insets( 0, 0, 0, 0 );
			rotateInsets( selectedInsets, insets, tabPane.getTabPlacement() );

			x += scale( insets.left );
			y += scale( insets.top );
			w -= scale( insets.left + insets.right );
			h -= scale( insets.top + insets.bottom );
		}

		// paint tab background
		Color background = getTabBackground( tabPlacement, tabIndex, isSelected );
		if( background != tabPane.getBackground() ) {
			g.setColor( FlatUIUtils.deriveColor( background, tabPane.getBackground() ) );
			if( !isCard && tabArc > 0 ) {
				float arc = scale( (float) tabArc ) / 2f;
				FlatUIUtils.paintSelection( (Graphics2D) g, x, y, w, h, null, arc, arc, arc, arc, 0 );
			} else if( isCard && cardTabArc > 0 )
				((Graphics2D)g).fill( createCardTabOuterPath( tabPlacement, x, y, w, h ) );
			else
				g.fillRect( x, y, w, h );
		}
	}

	/** @since 2 */
	protected Color getTabBackground( int tabPlacement, int tabIndex, boolean isSelected ) {
		Color background = tabPane.getBackgroundAt( tabIndex );

		// tabbed pane or tab is disabled
		if( !tabPane.isEnabled() || !tabPane.isEnabledAt( tabIndex ) )
			return background;

		// hover
		if( hoverColor != null && getRolloverTab() == tabIndex )
			return hoverColor;

		// tab background (if set)
		if( background != tabPane.getBackground() )
			return background;

		// focused and selected
		if( focusColor != null && isSelected && FlatUIUtils.isPermanentFocusOwner( tabPane ) )
			return focusColor;
		if( selectedBackground != null && isSelected )
			return selectedBackground;

		return background;
	}

	@Override
	protected void paintTabBorder( Graphics g, int tabPlacement, int tabIndex,
		int x, int y, int w, int h, boolean isSelected )
	{
		// paint tab separators
		if( clientPropertyBoolean( tabPane, TABBED_PANE_SHOW_TAB_SEPARATORS, showTabSeparators ) &&
			!isLastInRun( tabIndex ) )
		{
			if( getTabType() == TAB_TYPE_CARD ) {
				// some separators need to be omitted if selected tab is painted as card
				int selectedIndex = tabPane.getSelectedIndex();
				if( tabIndex != selectedIndex - 1 && tabIndex != selectedIndex )
					paintTabSeparator( g, tabPlacement, x, y, w, h );
			} else
				paintTabSeparator( g, tabPlacement, x, y, w, h );
		}

		// paint active tab border
		if( isSelected && getTabType() == TAB_TYPE_CARD )
			paintCardTabBorder( g, tabPlacement, tabIndex, x, y, w, h );
	}

	/** @since 2 */
	protected void paintCardTabBorder( Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h ) {
		Graphics2D g2 = (Graphics2D) g;

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( createCardTabOuterPath( tabPlacement, x, y, w, h ), false );
		path.append( createCardTabInnerPath( tabPlacement, x, y, w, h ), false );

		g.setColor( (tabSeparatorColor != null) ? tabSeparatorColor : contentAreaColor );
		g2.fill( path );
	}

	/** @since 3.2 */
	protected Shape createCardTabOuterPath( int tabPlacement, int x, int y, int w, int h ) {
		float arc = scale( (float) cardTabArc ) / 2f;

		switch( tabPlacement ) {
			default:
			case TOP:    return FlatUIUtils.createRoundRectanglePath( x, y, w, h, arc, arc, 0, 0 );
			case BOTTOM: return FlatUIUtils.createRoundRectanglePath( x, y, w, h, 0, 0, arc, arc );
			case LEFT:   return FlatUIUtils.createRoundRectanglePath( x, y, w, h, arc, 0, arc, 0 );
			case RIGHT:  return FlatUIUtils.createRoundRectanglePath( x, y, w, h, 0, arc, 0, arc );
		}
	}

	/** @since 3.2 */
	protected Shape createCardTabInnerPath( int tabPlacement, int x, int y, int w, int h ) {
		float bw = scale( (float) contentSeparatorHeight );
		float arc = (scale( (float) cardTabArc ) / 2f) - bw;

		switch( tabPlacement ) {
			default:
			case TOP:    return FlatUIUtils.createRoundRectanglePath( x + bw, y + bw, w - (bw * 2), h - bw,       arc, arc, 0, 0 );
			case BOTTOM: return FlatUIUtils.createRoundRectanglePath( x + bw, y,      w - (bw * 2), h - bw,       0, 0, arc, arc );
			case LEFT:   return FlatUIUtils.createRoundRectanglePath( x + bw, y + bw, w - bw,       h - (bw * 2), arc, 0, arc, 0 );
			case RIGHT:  return FlatUIUtils.createRoundRectanglePath( x,      y + bw, w - bw,       h - (bw * 2), 0, arc, 0, arc );
		}
	}

	protected void paintTabCloseButton( Graphics g, int tabIndex, int x, int y, int w, int h ) {
		// create tab close button
		if( tabCloseButton == null ) {
			tabCloseButton = new TabCloseButton();
			tabCloseButton.setVisible( false );
		}

		// update state of tab close button
		boolean rollover = (tabIndex == getRolloverTab());
		ButtonModel bm = tabCloseButton.getModel();
		bm.setRollover( rollover && isRolloverTabClose() );
		bm.setPressed( rollover && isPressedTabClose() );

		// copy colors from tabbed pane because close icon uses derives colors
		tabCloseButton.setBackground( tabPane.getBackground() );
		tabCloseButton.setForeground( tabPane.getForeground() );

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

	protected void paintTabSelection( Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h ) {
		g.setColor( tabPane.isEnabled()
			? (isTabbedPaneOrChildFocused() ? underlineColor : inactiveUnderlineColor)
			: disabledUnderlineColor );

		boolean isCard = (getTabType() == TAB_TYPE_CARD);
		boolean atBottom = !isCard;
		Insets contentInsets = atBottom
			? ((!rotateTabRuns && runCount > 1 && !isScrollTabLayout() && getRunForTab( tabPane.getTabCount(), tabIndex ) > 0)
				? new Insets( 0, 0, 0, 0 )
				: getContentBorderInsets( tabPlacement ))
			: null;

		int tabSelectionHeight = scale( isCard ? cardTabSelectionHeight : this.tabSelectionHeight );
		float arc = scale( (float) (isCard ? cardTabArc : tabSelectionArc) ) / 2f;
		int sx = x, sy = y, sw = w, sh = h;
		switch( tabPlacement ) {
			case TOP:
			default:
				sy = atBottom ? (y + h + contentInsets.top - tabSelectionHeight) : y;
				sh = tabSelectionHeight;
				break;

			case BOTTOM:
				sy = atBottom ? (y - contentInsets.bottom) : (y + h - tabSelectionHeight);
				sh = tabSelectionHeight;
				break;

			case LEFT:
				sx = atBottom ? (x + w + contentInsets.left - tabSelectionHeight) : x;
				sw = tabSelectionHeight;
				break;

			case RIGHT:
				sx = atBottom ? (x - contentInsets.right) : (x + w - tabSelectionHeight);
				sw = tabSelectionHeight;
				break;
		}

		// apply insets
		if( !isCard && tabSelectionInsets != null ) {
			Insets insets = new Insets( 0, 0, 0, 0 );
			rotateInsets( tabSelectionInsets, insets, tabPane.getTabPlacement() );

			sx += scale( insets.left );
			sy += scale( insets.top );
			sw -= scale( insets.left + insets.right );
			sh -= scale( insets.top + insets.bottom );
		}

		// paint underline selection
		if( arc <= 0 )
			g.fillRect( sx, sy, sw, sh );
		else {
			if( isCard ) {
				Area area = new Area( createCardTabOuterPath( tabPlacement, x, y, w, h ) );
				area.intersect( new Area( new Rectangle2D.Float( sx, sy, sw, sh ) ) );
				((Graphics2D)g).fill( area );
			} else
				FlatUIUtils.paintSelection( (Graphics2D) g, sx, sy, sw, sh, null, arc, arc, arc, arc, 0 );
		}
	}

	/** @since 2.2 */
	@SuppressWarnings( "unchecked" )
	protected boolean isTabbedPaneOrChildFocused() {
		KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

		Object value = tabPane.getClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER );
		if( value instanceof Predicate ) {
			return ((Predicate<JComponent>)value).test( tabPane ) &&
				FlatUIUtils.isInActiveWindow( tabPane, keyboardFocusManager.getActiveWindow() );
		}

		Component focusOwner = keyboardFocusManager.getPermanentFocusOwner();
		return focusOwner != null &&
			SwingUtilities.isDescendingFrom( focusOwner, tabPane ) &&
			FlatUIUtils.isInActiveWindow( focusOwner, keyboardFocusManager.getActiveWindow() );
	}

	/**
	 * Actually does nearly the same as super.paintContentBorder() but
	 *   - not using UIManager.getColor("TabbedPane.contentAreaColor") to be GUI builder friendly
	 *   - tabsOverlapBorder is always true
	 *   - paint full border (if enabled)
	 *   - not invoking paintContentBorder*Edge() methods
	 *   - repaint selection
	 *   - painting active tab border style
	 */
	@Override
	protected void paintContentBorder( Graphics g, int tabPlacement, int selectedIndex ) {
		if( tabPane.getTabCount() <= 0 ||
			contentSeparatorHeight == 0 ||
			!clientPropertyBoolean( tabPane, TABBED_PANE_SHOW_CONTENT_SEPARATOR, showContentSeparator ) )
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

		// create path for content separator or full border
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new Rectangle2D.Float( x, y, w, h ), false );
		path.append( new Rectangle2D.Float( x + (ci.left / 100f), y + (ci.top / 100f),
			w - (ci.left / 100f) - (ci.right / 100f), h - (ci.top / 100f) - (ci.bottom / 100f) ), false );

		// add gap for selected tab to path
		if( getTabType() == TAB_TYPE_CARD && selectedIndex >= 0 ) {
			float csh = scale( (float) contentSeparatorHeight );

			Rectangle tabRect = getTabBounds( tabPane, selectedIndex );
			Rectangle2D.Float innerTabRect = new Rectangle2D.Float( tabRect.x + csh, tabRect.y + csh,
				tabRect.width - (csh * 2), tabRect.height - (csh * 2) );

			// Ensure that the separator outside the tabViewport is present (doesn't get cutoff by the active tab)
			// If left unsolved the active tab is "visible" in the separator (the gap) even when outside the viewport
			if( tabViewport != null )
				Rectangle2D.intersect( tabViewport.getBounds(), innerTabRect, innerTabRect );

			Rectangle2D.Float gap = null;
			if( isHorizontalTabPlacement( tabPlacement ) ) {
				if( innerTabRect.width > 0 ) {
					float y2 = (tabPlacement == TOP) ? y : y + h - csh;
					gap = new Rectangle2D.Float( innerTabRect.x, y2, innerTabRect.width, csh );
				}
			} else {
				if( innerTabRect.height > 0 ) {
					float x2 = (tabPlacement == LEFT) ? x : x + w - csh;
					gap = new Rectangle2D.Float( x2, innerTabRect.y, csh, innerTabRect.height );
				}
			}

			if( gap != null ) {
				path.append( gap, false );

				// fill gap in case that the tab is colored (e.g. focused or hover)
				Color background = getTabBackground( tabPlacement, selectedIndex, true );
				g.setColor( FlatUIUtils.deriveColor( background, tabPane.getBackground() ) );
				((Graphics2D)g).fill( gap );
			}
		}

		// paint content separator or full border
		g.setColor( contentAreaColor );
		((Graphics2D)g).fill( path );

		// repaint selection in scroll-tab-layout because it may be painted before
		// the content border was painted (from BasicTabbedPaneUI$ScrollableTabPanel)
		if( isScrollTabLayout() && selectedIndex >= 0 && tabViewport != null ) {
			Rectangle tabRect = getTabBounds( tabPane, selectedIndex );

			// clip to "scrolling sides" of viewport
			// (left and right if horizontal, top and bottom if vertical)
			Shape oldClip = g.getClip();
			Rectangle vr = tabViewport.getBounds();
			if( isHorizontalTabPlacement( tabPlacement ) )
				g.clipRect( vr.x, 0, vr.width, tabPane.getHeight() );
			else
				g.clipRect( 0, vr.y, tabPane.getWidth(), vr.height );

			paintTabSelection( g, tabPlacement, selectedIndex, tabRect.x, tabRect.y, tabRect.width, tabRect.height );
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
		int rotation = getRealTabRotation( tabPlacement );
		boolean leftToRight = isLeftToRight();

		// remove tab insets and space for close button from the tab rectangle
		// to get correctly clipped title
		tabRect = FlatUIUtils.subtractInsets( tabRect, getTabInsetsRotated( tabPlacement, tabIndex, rotation ) );
		if( isTabClosable( tabIndex ) ) {
			if( rotation == NONE ) {
				int iconWidth = closeIcon.getIconWidth();
				tabRect.width -= iconWidth;
				if( !leftToRight )
					tabRect.x += iconWidth;
			} else {
				int iconHeight = closeIcon.getIconHeight();
				tabRect.height -= iconHeight;
				if( (rotation == LEFT && leftToRight) || (rotation == RIGHT && !leftToRight) )
					tabRect.y += iconHeight;
			}
		}

		// icon placement
		int verticalTextPosition;
		int horizontalTextPosition;
		switch( clientPropertyInt( tabPane, TABBED_PANE_TAB_ICON_PLACEMENT, tabIconPlacement ) ) {
			default:
			case LEADING:  verticalTextPosition = CENTER; horizontalTextPosition = TRAILING; break;
			case TRAILING: verticalTextPosition = CENTER; horizontalTextPosition = LEADING; break;
			case TOP:      verticalTextPosition = BOTTOM; horizontalTextPosition = CENTER; break;
			case BOTTOM:   verticalTextPosition = TOP;    horizontalTextPosition = CENTER; break;
		}

		// reset rectangles
		textRect.setBounds( 0, 0, 0, 0 );
		iconRect.setBounds( 0, 0, 0, 0 );

		// temporary set "html" client property on tabbed pane, which is used by SwingUtilities.layoutCompoundLabel()
		View view = getTextViewForTab( tabIndex );
		if( view != null )
			tabPane.putClientProperty( "html", view );

		// layout label
		String clippedTitle = (rotation == NONE)
			? SwingUtilities.layoutCompoundLabel( tabPane, metrics, title, icon,
				CENTER, getTabAlignment( tabIndex ), verticalTextPosition, horizontalTextPosition,
				tabRect, iconRect, textRect, scale( textIconGapUnscaled ) )
			: layoutVerticalCompoundLabel( rotation, tabPane, metrics, title, icon,
				CENTER, getTabAlignment( tabIndex ), verticalTextPosition, horizontalTextPosition,
				tabRect, iconRect, textRect, scale( textIconGapUnscaled ) );

		// remove temporary client property
		tabPane.putClientProperty( "html", null );

		return clippedTitle;
	}

	private String layoutVerticalCompoundLabel( int rotation, JComponent c, FontMetrics fm, String text, Icon icon,
		int verticalAlignment, int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition,
		Rectangle viewR, Rectangle iconR, Rectangle textR, int textIconGap )
	{
		// layout non-rotated
		Rectangle viewR2 = new Rectangle( viewR.height, viewR.width );
		String clippedTitle = SwingUtilities.layoutCompoundLabel( c, fm, text, icon,
			verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition,
			viewR2, iconR, textR, textIconGap );

		// rotate icon and text rectangles
		if( rotation == LEFT ) {
			rotateLeft( viewR, iconR );
			rotateLeft( viewR, textR );
		} else {
			rotateRight( viewR, iconR );
			rotateRight( viewR, textR );
		}

		return clippedTitle;
	}

	private void rotateLeft( Rectangle viewR, Rectangle r ) {
		int x = viewR.x + r.y;
		int y = viewR.y + (viewR.height - (r.x + r.width));
		r.setBounds( x, y, r.height, r.width );
	}

	private void rotateRight( Rectangle viewR, Rectangle r ) {
		int x = viewR.x + (viewR.width - (r.y + r.height));
		int y = viewR.y + r.x;
		r.setBounds( x, y, r.height, r.width );
	}

	/** @since 3.3 */
	protected int getRealTabRotation( int tabPlacement ) {
		int rotation = getTabRotation();
		int realRotation = (rotation == AUTO)
			? (tabPlacement == LEFT ? LEFT : (tabPlacement == RIGHT ? RIGHT : NONE))
			: (rotation == LEFT || rotation == RIGHT ? rotation : NONE);
		assert realRotation == NONE || realRotation == LEFT || realRotation == RIGHT;
		return realRotation;
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
		int tabPlacement = tabPane.getTabPlacement();
		int rotation = getRealTabRotation( tabPlacement );
		Insets tabInsets = getTabInsetsRotated( tabPlacement, tabIndex, rotation );
		boolean leftToRight = isLeftToRight();

		// use one-third of right/left tab insets as gap between tab text and close button
		if( rotation == NONE ) {
			dest.x = leftToRight
				? (x + w - (tabInsets.right / 3 * 2) - iconWidth)		// right
				: (x + (tabInsets.left / 3 * 2));						// left
			dest.y = y + (h - iconHeight) / 2;
		} else {
			dest.x = x + (w - iconWidth) / 2;
			dest.y = ((rotation == RIGHT && leftToRight) || (rotation == LEFT && !leftToRight))
				? (y + h - (tabInsets.bottom / 3 * 2) - iconHeight)		// bottom
				: (y + (tabInsets.top / 3 * 2));						// top
		}
		dest.width = iconWidth;
		dest.height = iconHeight;
		return dest;
	}

	protected Rectangle getTabCloseHitArea( int tabIndex ) {
		Rectangle tabRect = getTabBounds( tabPane, tabIndex );
		Rectangle tabCloseRect = getTabCloseBounds( tabIndex, tabRect.x, tabRect.y, tabRect.width, tabRect.height, calcRect );
		return (getRealTabRotation( tabPane.getTabPlacement() ) == NONE)
			? new Rectangle( tabCloseRect.x, tabRect.y, tabCloseRect.width, tabRect.height )
			: new Rectangle( tabRect.x, tabCloseRect.y, tabRect.width, tabCloseRect.height );
	}

	protected boolean isTabClosable( int tabIndex ) {
		if( tabIndex < 0 )
			return false;

		Object value = getTabClientProperty( tabIndex, TABBED_PANE_TAB_CLOSABLE );
		return (value instanceof Boolean) ? (boolean) value : tabClosable;
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

	@Override
	protected boolean shouldRotateTabRuns( int tabPlacement ) {
		return rotateTabRuns;
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

	/** @since 3.3 */
	protected boolean isHorizontalTabPlacement( int tabPlacement ) {
		return tabPlacement == TOP || tabPlacement == BOTTOM;
	}

	/**
	 * Returns {@code true} if tab placement is top/bottom and text is painted horizontally or
	 * if tab placement is left/right and text is painted vertically (rotated).
	 */
	private boolean isHorizontalOrRotated( int tabPlacement ) {
		return isHorizontalTabPlacement( tabPlacement ) == (getRealTabRotation( tabPlacement ) == NONE);
	}

	protected boolean isSmoothScrollingEnabled() {
		if( !Animator.useAnimation() )
			return false;

		// Note: Getting UI value "ScrollPane.smoothScrolling" here to allow
		// applications to turn smooth scrolling on or off at any time
		// (e.g. in application options dialog).
		return UIManager.getBoolean( "ScrollPane.smoothScrolling" );
	}

	protected boolean hideTabArea() {
		return tabPane.getTabCount() == 1 &&
			leadingComponent == null &&
			trailingComponent == null &&
			clientPropertyBoolean( tabPane, TABBED_PANE_HIDE_TAB_AREA_WITH_ONE_TAB, hideTabAreaWithOneTab );
	}

	/** @since 2 */
	protected int getTabType() {
		Object value = tabPane.getClientProperty( TABBED_PANE_TAB_TYPE );

		return (value instanceof String)
			? parseTabType( (String) value )
			: tabType;
	}

	protected int getTabsPopupPolicy() {
		Object value = tabPane.getClientProperty( TABBED_PANE_TABS_POPUP_POLICY );

		return (value instanceof String)
			? parseTabsPopupPolicy( (String) value )
			: tabsPopupPolicy;
	}

	protected int getScrollButtonsPolicy() {
		Object value = tabPane.getClientProperty( TABBED_PANE_SCROLL_BUTTONS_POLICY );

		return (value instanceof String)
			? parseScrollButtonsPolicy( (String) value )
			: scrollButtonsPolicy;
	}

	protected int getScrollButtonsPlacement() {
		Object value = tabPane.getClientProperty( TABBED_PANE_SCROLL_BUTTONS_PLACEMENT );

		return (value instanceof String)
			? parseScrollButtonsPlacement( (String) value )
			: scrollButtonsPlacement;
	}

	protected int getTabAreaAlignment() {
		Object value = tabPane.getClientProperty( TABBED_PANE_TAB_AREA_ALIGNMENT );
		if( value instanceof Integer )
			return (Integer) value;

		return (value instanceof String)
			? parseAlignment( (String) value, LEADING )
			: tabAreaAlignment;
	}

	protected int getTabAlignment( int tabIndex ) {
		Object value = getTabClientProperty( tabIndex, TABBED_PANE_TAB_ALIGNMENT );
		if( value instanceof Integer )
			return (Integer) value;

		return (value instanceof String)
			? parseAlignment( (String) value, CENTER )
			: tabAlignment;
	}

	protected int getTabWidthMode() {
		Object value = tabPane.getClientProperty( TABBED_PANE_TAB_WIDTH_MODE );

		return (value instanceof String)
			? parseTabWidthMode( (String) value )
			: tabWidthMode;
	}

	/** @since 3.3 */
	protected int getTabRotation() {
		Object value = tabPane.getClientProperty( TABBED_PANE_TAB_ROTATION );
		if( value instanceof Integer )
			return (Integer) value;

		return (value instanceof String)
			? parseTabRotation( (String) value )
			: tabRotation;
	}

	/** @since 2 */
	protected static int parseTabType( String str ) {
		if( str == null )
			return TAB_TYPE_UNDERLINED;

		switch( str ) {
			default:
			case TABBED_PANE_TAB_TYPE_UNDERLINED:		return TAB_TYPE_UNDERLINED;
			case TABBED_PANE_TAB_TYPE_CARD:				return TAB_TYPE_CARD;
		}
	}

	protected static int parseTabsPopupPolicy( String str ) {
		if( str == null )
			return AS_NEEDED;

		switch( str ) {
			default:
			case TABBED_PANE_POLICY_AS_NEEDED:			return AS_NEEDED;
			case TABBED_PANE_POLICY_NEVER:				return NEVER;
		}
	}

	protected static int parseScrollButtonsPolicy( String str ) {
		if( str == null )
			return AS_NEEDED_SINGLE;

		switch( str ) {
			default:
			case TABBED_PANE_POLICY_AS_NEEDED_SINGLE:	return AS_NEEDED_SINGLE;
			case TABBED_PANE_POLICY_AS_NEEDED:			return AS_NEEDED;
			case TABBED_PANE_POLICY_NEVER:				return NEVER;
		}
	}

	protected static int parseScrollButtonsPlacement( String str ) {
		if( str == null )
			return BOTH;

		switch( str ) {
			default:
			case TABBED_PANE_PLACEMENT_BOTH:			return BOTH;
			case TABBED_PANE_PLACEMENT_TRAILING:		return TRAILING;
		}
	}

	protected static int parseAlignment( String str, int defaultValue ) {
		if( str == null )
			return defaultValue;

		switch( str ) {
			case TABBED_PANE_ALIGN_LEADING:		return LEADING;
			case TABBED_PANE_ALIGN_TRAILING:	return TRAILING;
			case TABBED_PANE_ALIGN_CENTER:		return CENTER;
			case TABBED_PANE_ALIGN_FILL:		return FILL;
			default:							return defaultValue;
		}
	}

	private static String alignmentToString( int value, String defaultValue ) {
		switch( value ) {
			case LEADING:		return TABBED_PANE_ALIGN_LEADING;
			case TRAILING:		return TABBED_PANE_ALIGN_TRAILING;
			case CENTER:		return TABBED_PANE_ALIGN_CENTER;
			case FILL:			return TABBED_PANE_ALIGN_FILL;
			default:			return defaultValue;
		}
	}

	protected static int parseTabWidthMode( String str ) {
		if( str == null )
			return WIDTH_MODE_PREFERRED;

		switch( str ) {
			default:
			case TABBED_PANE_TAB_WIDTH_MODE_PREFERRED:	return WIDTH_MODE_PREFERRED;
			case TABBED_PANE_TAB_WIDTH_MODE_EQUAL:		return WIDTH_MODE_EQUAL;
			case TABBED_PANE_TAB_WIDTH_MODE_COMPACT:	return WIDTH_MODE_COMPACT;
		}
	}

	/** @since 3.3 */
	protected static int parseTabRotation( String str ) {
		if( str == null )
			return WIDTH_MODE_PREFERRED;

		switch( str ) {
			default:
			case TABBED_PANE_TAB_ROTATION_NONE:		return NONE;
			case TABBED_PANE_TAB_ROTATION_AUTO:		return AUTO;
			case TABBED_PANE_TAB_ROTATION_LEFT:		return LEFT;
			case TABBED_PANE_TAB_ROTATION_RIGHT:	return RIGHT;
		}
	}

	protected static int parseTabIconPlacement( String str ) {
		if( str == null )
			return LEADING;

		switch( str ) {
			default:
			case "leading":		return LEADING;
			case "trailing":	return TRAILING;
			case "top":			return TOP;
			case "bottom":		return BOTTOM;
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
		// do nothing if not yet displayable or if not invoked from dispatch thread,
		// which may be the case when creating/modifying in another thread
		if( !tabPane.isDisplayable() || !EventQueue.isDispatchThread() )
			return;

		EventQueue.invokeLater( () -> {
			ensureSelectedTabIsVisible();
		} );
	}

	protected void ensureSelectedTabIsVisible() {
		if( tabPane == null || tabViewport == null || !tabPane.isDisplayable() )
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
		}
	}

	private void stretchTabsWidth( int sw, boolean leftToRight ) {
		int rsw = sw / rects.length;
		int x = rects[0].x - (leftToRight ? 0 : rsw);
		for( int i = 0; i < rects.length; i++ ) {
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

	//---- interface FlatTitlePane.TitleBarCaptionHitTest ----

	/** @since 3.4 */
	@Override
	public Boolean isTitleBarCaptionAt( int x, int y ) {
		// Note: not using tabForCoordinate() here because this may validate layout and cause dead lock

		if( moreTabsButton != null ) {
			// convert x,y from JTabbedPane coordinate space to ScrollableTabPanel coordinate space
			Point viewPosition = tabViewport.getViewPosition();
			x = x - tabViewport.getX() + viewPosition.x;
			y = y - tabViewport.getY() + viewPosition.y;

			// check whether point is within viewport
			if( !tabViewport.getViewRect().contains( x, y ) )
				return null; // check children
		}

		for( int i = 0; i < rects.length; i++ ) {
			if( rects[i].contains( x, y ) )
				return false;
		}

		return null; // check children
	}

	//---- class TabCloseButton -----------------------------------------------

	private static class TabCloseButton
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

		@SuppressWarnings( "deprecation" )
		@Override
		public void reshape( int x, int y, int w, int h ) {
			// Avoid that leading/trailing tab area components are temporary moved/resized
			// to content area bounds (done in BasicTabbedPaneUI.TabbedPaneLayout.layoutContainer()
			// and in BasicTabbedPaneUI.TabbedPaneScrollLayout.layoutContainer())
			// and subsequently moved/resized to its final bounds within the tab area.
			// This avoids an unnecessary repaint (and maybe re-layout) of the content area.
			if( inBasicLayoutContainer )
				return;

			super.reshape( x, y, w, h );
		}
	}

	//---- class FlatTabAreaButton --------------------------------------------

	protected class FlatTabAreaButton
		extends FlatArrowButton
	{
		public FlatTabAreaButton( int direction ) {
			super( direction, arrowType,
				FlatTabbedPaneUI.this.foreground, FlatTabbedPaneUI.this.disabledForeground,
				null, buttonHoverBackground, null, buttonPressedBackground );
			setArrowWidth( 11 );
		}

		protected void updateStyle() {
			updateStyle( arrowType,
				FlatTabbedPaneUI.this.foreground, FlatTabbedPaneUI.this.disabledForeground,
				null, buttonHoverBackground, null, buttonPressedBackground );
		}

		@Override
		protected Color deriveBackground( Color background ) {
			return FlatUIUtils.deriveColor( background, tabPane.getBackground() );
		}

		@Override
		protected void paintBackground( Graphics2D g ) {
			// rotate button insets
			Insets insets = new Insets( 0, 0, 0, 0 );
			rotateInsets( buttonInsets, insets, tabPane.getTabPlacement() );

			// use UIScale.scale2() here because this gives smaller insets at 150% and 175%
			int top = UIScale.scale2( insets.top );
			int left = UIScale.scale2( insets.left );
			int bottom = UIScale.scale2( insets.bottom );
			int right = UIScale.scale2( insets.right );

			FlatUIUtils.paintComponentBackground( g, left, top,
				getWidth() - left - right,
				getHeight() - top - bottom,
				0, scale( (float) buttonArc ) );
		}
	}

	//---- class FlatMoreTabsButton -------------------------------------------

	protected class FlatMoreTabsButton
		extends FlatTabAreaButton
		implements ActionListener, PopupMenuListener
	{
		private boolean popupVisible;

		public FlatMoreTabsButton() {
			super( SOUTH );

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
				int xoffset = Math.max( UIScale.unscale( (getWidth() - getHeight()) / 2 ) - 4, 0 );
				setXOffset( (direction == EAST) ? xoffset : -xoffset );
			} else
				setXOffset( 0 );

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
					popupMenu.add( createTabMenuItem( i ) );
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

		protected JMenuItem createTabMenuItem( int tabIndex ) {
			// search for tab name in these places
			//   1. tab title
			//   2. text of label or text component in custom tab component (including children)
			//   3. accessible name of tab
			//   4. accessible name of custom tab component (including children)
			//   5. string "n. Tab"
			String title = tabPane.getTitleAt( tabIndex );
			if( StringUtils.isEmpty( title ) ) {
				Component tabComp = tabPane.getTabComponentAt( tabIndex );
				if( tabComp != null )
					title = findTabTitle( tabComp );
				if( StringUtils.isEmpty( title ) )
					title = tabPane.getAccessibleContext().getAccessibleChild( tabIndex ).getAccessibleContext().getAccessibleName();
				if( StringUtils.isEmpty( title ) && tabComp instanceof Accessible )
					title = findTabTitleInAccessible( (Accessible) tabComp );
				if( StringUtils.isEmpty( title ) )
					title = (tabIndex + 1) + ". Tab";
			}

			JMenuItem menuItem = new JMenuItem( title, tabPane.getIconAt( tabIndex ) );
			menuItem.setDisabledIcon( tabPane.getDisabledIconAt( tabIndex ) );
			menuItem.setToolTipText( tabPane.getToolTipTextAt( tabIndex ) );

			Color foregroundAt = tabPane.getForegroundAt( tabIndex );
			if( foregroundAt != tabPane.getForeground() )
				menuItem.setForeground( foregroundAt );

			Color backgroundAt = tabPane.getBackgroundAt( tabIndex );
			if( backgroundAt != tabPane.getBackground() ) {
				menuItem.setBackground( backgroundAt );
				menuItem.setOpaque( true );
			}

			if( !tabPane.isEnabled() || !tabPane.isEnabledAt( tabIndex ) )
				menuItem.setEnabled( false );

			menuItem.addActionListener( e -> selectTab( tabIndex ) );
			return menuItem;
		}

		/**
		 * Search for label or text component in custom tab component and return its text.
		 */
		private String findTabTitle( Component c ) {
			String title = null;
			if( c instanceof JLabel )
				title = ((JLabel)c).getText();
			else if( c instanceof JTextComponent )
				title = ((JTextComponent)c).getText();

			if( !StringUtils.isEmpty( title ) )
				return title;

			if( c instanceof Container ) {
				for( Component child : ((Container)c).getComponents() ) {
					title = findTabTitle( child );
					if( title != null )
						return title;
				}
			}

			return null;
		}

		/**
		 * Search for accessible name.
		 */
		private String findTabTitleInAccessible( Accessible accessible ) {
			AccessibleContext context = accessible.getAccessibleContext();
			if( context == null )
				return null;

			String title = context.getAccessibleName();
			if( !StringUtils.isEmpty( title ) )
				return title;

			int childrenCount = context.getAccessibleChildrenCount();
			for( int i = 0; i < childrenCount; i++ ) {
				title = findTabTitleInAccessible( context.getAccessibleChild( i ) );
				if( title != null )
					return title;
			}

			return null;
		}

		protected void selectTab( int tabIndex ) {
			tabPane.setSelectedIndex( tabIndex );
			ensureSelectedTabIsVisible();
		}

		@Override
		public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
			popupVisible = true;
			HiDPIUtils.repaint( this );
		}

		@Override
		public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
			popupVisible = false;
			HiDPIUtils.repaint( this );
		}

		@Override
		public void popupMenuCanceled( PopupMenuEvent e ) {
			popupVisible = false;
			HiDPIUtils.repaint( this );
		}
	}

	//---- class FlatScrollableTabButton --------------------------------------

	protected class FlatScrollableTabButton
		extends FlatTabAreaButton
		implements MouseListener
	{
		private Timer autoRepeatTimer;

		protected FlatScrollableTabButton( int direction ) {
			super( direction );

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
			// disable wheel scrolling if application has added its own mouse wheel listener
			if( tabPane.getMouseWheelListeners().length > 1 )
				return;

			// because this listener receives mouse events for the whole tabbed pane,
			// we have to check whether the mouse is located over the viewport
			if( !isInViewport( e.getX(), e.getY() ) )
				return;

			lastMouseX = e.getX();
			lastMouseY = e.getY();

			double preciseWheelRotation = e.getPreciseWheelRotation();
			boolean isPreciseWheel = (preciseWheelRotation != 0 && preciseWheelRotation != e.getWheelRotation());
			int amount = (int) (maxTabHeight * preciseWheelRotation);

			// scroll at least one pixel to avoid "hanging"
			if( amount == 0 ) {
				if( preciseWheelRotation > 0 )
					amount = 1;
				else if( preciseWheelRotation < 0 )
					amount = -1;
			}

			// compute new view position
			Point viewPosition = (targetViewPosition != null)
				? targetViewPosition
				: tabViewport.getViewPosition();
			Dimension viewSize = tabViewport.getViewSize();
			boolean horizontal = isHorizontalTabPlacement( tabPane.getTabPlacement() );
			int x = viewPosition.x;
			int y = viewPosition.y;
			if( horizontal )
				x += isLeftToRight() ? amount : -amount;
			else
				y += amount;

			// In case of having scroll buttons on both sides and hiding disabled buttons,
			// the viewport is moved when the scroll backward button becomes visible
			// or is hidden. For non-precise wheel scrolling (e.g. mouse wheel on Windows),
			// this is no problem because the scroll amount is at least a tab-height.
			// For precise wheel scrolling (e.g. touchpad on Mac), this is a problem
			// because it is possible to scroll by a fraction of a tab-height.
			if( isPreciseWheel &&
				getScrollButtonsPlacement() == BOTH &&
				getScrollButtonsPolicy() == AS_NEEDED_SINGLE &&
				(isLeftToRight() || !horizontal) && // scroll buttons are hidden in right-to-left
				scrollBackwardButtonPrefSize != null )
			{
				// special cases for scrolling with touchpad or high-resolution wheel:
				//   1. if view is at 0/0 and scrolling right/down, then the scroll backward button
				//      becomes visible, which moves the viewport right/down by the width/height of
				//      the button --> add button width/height to new view position so that
				//      tabs seems to stay in place at screen
				//   2. if scrolling left/up to the beginning, then the scroll backward button
				//      becomes hidden, which moves the viewport left/up by the width/height of
				//      the button --> set new view position to 0/0 so that
				//      tabs seems to stay in place at screen
				if( horizontal ) {
					//
					if( viewPosition.x == 0 && x > 0 )
						x += scrollBackwardButtonPrefSize.width;
					else if( amount < 0 && x <= scrollBackwardButtonPrefSize.width )
						x = 0;
				} else {
					if( viewPosition.y == 0 && y > 0 )
						y += scrollBackwardButtonPrefSize.height;
					else if( amount < 0 && y <= scrollBackwardButtonPrefSize.height )
						y = 0;
				}
			}

			// limit new view position
			if( horizontal )
				x = Math.min( Math.max( x, 0 ), viewSize.width - tabViewport.getWidth() );
			else
				y = Math.min( Math.max( y, 0 ), viewSize.height - tabViewport.getHeight() );

			// check whether view position has changed
			Point newViewPosition = new Point( x, y );
			if( newViewPosition.equals( viewPosition ) )
				return;

			// update view position
			if( isPreciseWheel ) {
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

			if( !scrolled )
				return;
			scrolled = false;

			// scroll selected tab into visible area
			ensureSelectedTabIsVisible();
		}
	}

	//---- class Handler ------------------------------------------------------

	private class Handler
		implements MouseListener, MouseMotionListener, PropertyChangeListener,
			ChangeListener, ComponentListener, ContainerListener, FocusListener
	{
		MouseListener mouseDelegate;
		PropertyChangeListener propertyChangeDelegate;
		ChangeListener changeDelegate;
		FocusListener focusDelegate;

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

			if( !isPressedTabClose() && SwingUtilities.isLeftMouseButton( e ) )
				mouseDelegate.mousePressed( e );
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			if( isPressedTabClose() ) {
				updateRollover( e );
				if( pressedTabIndex >= 0 && pressedTabIndex == getRolloverTab() ) {
					restoreTabToolTip();
					closeTab( pressedTabIndex );
				}
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
			boolean hitClose = isTabClosable( tabIndex ) && getTabCloseHitArea( tabIndex ).contains( x, y );
			if( e.getID() == MouseEvent.MOUSE_PRESSED && SwingUtilities.isLeftMouseButton( e ) )
				pressedTabIndex = hitClose ? tabIndex : -1;
			setRolloverTabClose( hitClose );
			setPressedTabClose( hitClose && tabIndex == pressedTabIndex );

			// update tooltip
			if( tabIndex >= 0 && hitClose ) {
				Object closeTip = getTabClientProperty( tabIndex, TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT );
				if( closeTip == null )
					closeTip = tabCloseToolTipText;
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

			restoreTabToolTip();

			lastTipTabIndex = tabIndex;
			lastTip = tabPane.getToolTipTextAt( lastTipTabIndex );
			tabPane.setToolTipTextAt( lastTipTabIndex, closeTip );
		}

		private void restoreTabToolTip() {
			if( lastTipTabIndex < 0 )
				return;

			if( lastTipTabIndex < tabPane.getTabCount() )
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
				case TABBED_PANE_TAB_TYPE:
					HiDPIUtils.repaint( tabPane );
					break;

				case TABBED_PANE_SHOW_CONTENT_SEPARATOR:
				case TABBED_PANE_HAS_FULL_BORDER:
				case TABBED_PANE_HIDE_TAB_AREA_WITH_ONE_TAB:
				case TABBED_PANE_MINIMUM_TAB_WIDTH:
				case TABBED_PANE_MAXIMUM_TAB_WIDTH:
				case TABBED_PANE_TAB_HEIGHT:
				case TABBED_PANE_TAB_INSETS:
				case TABBED_PANE_TAB_AREA_INSETS:

				case TABBED_PANE_TABS_POPUP_POLICY:
				case TABBED_PANE_SCROLL_BUTTONS_POLICY:
				case TABBED_PANE_SCROLL_BUTTONS_PLACEMENT:

				case TABBED_PANE_TAB_AREA_ALIGNMENT:
				case TABBED_PANE_TAB_ALIGNMENT:
				case TABBED_PANE_TAB_WIDTH_MODE:
				case TABBED_PANE_TAB_ROTATION:
				case TABBED_PANE_TAB_ICON_PLACEMENT:
				case TABBED_PANE_TAB_CLOSABLE:
					tabPane.revalidate();
					HiDPIUtils.repaint( tabPane );
					break;

				case TABBED_PANE_LEADING_COMPONENT:
					uninstallLeadingComponent();
					installLeadingComponent();
					tabPane.revalidate();
					HiDPIUtils.repaint( tabPane );
					ensureSelectedTabIsVisibleLater();
					break;

				case TABBED_PANE_TRAILING_COMPONENT:
					uninstallTrailingComponent();
					installTrailingComponent();
					tabPane.revalidate();
					HiDPIUtils.repaint( tabPane );
					ensureSelectedTabIsVisibleLater();
					break;

				case STYLE:
				case STYLE_CLASS:
					installStyle();
					tabPane.revalidate();
					HiDPIUtils.repaint( tabPane );
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
				case TABBED_PANE_TAB_ALIGNMENT:
				case TABBED_PANE_TAB_CLOSABLE:
					tabPane.revalidate();
					HiDPIUtils.repaint( tabPane );
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

		//---- interface FocusListener ----

		@Override
		public void focusGained( FocusEvent e ) {
			focusDelegate.focusGained( e );
			repaintTab( tabPane.getSelectedIndex() );
		}

		@Override
		public void focusLost( FocusEvent e ) {
			focusDelegate.focusLost( e );
			repaintTab( tabPane.getSelectedIndex() );
		}
	}

	//---- class FlatTabbedPaneLayout -----------------------------------------

	/**
	 * Layout manager for wrap tab layout policy (and base class for scroll tab layout policy).
	 * <p>
	 * Component hierarchy for wrap tab layout policy:
	 * <pre>{@code
	 * JTabbedPane
	 *    +- 1...n tab content components
	 *    +- (optional) BasicTabbedPaneUI.TabContainer (extends JPanel)
	 *    |  +- 1..n tab components (shown in tab area)
	 *    +- (optional) ContainerUIResource (extends JPanel)
	 *    |  +- leading component
	 *    +- (optional) ContainerUIResource (extends JPanel)
	 *       +- trailing component
	 * }</pre>
	 * <p>
	 * Instead of using {@code super.layoutContainer(Container)} and fixing some
	 * component bounds, this class implements {@code layoutContainer(Container)}
	 * and moves/resizes components only once.
	 * This avoids that some components are moved/resized twice, which would unnecessary
	 * repaint and relayout tabbed pane. In some special case this resulted in
	 * "endless" layouting and repainting when using nested tabbed panes (top and
	 * bottom tab placement) and RSyntaxTextArea (with enabled line-wrapping) as tab content.
	 */
	protected class FlatTabbedPaneLayout
		extends TabbedPaneLayout
	{
		@Override
		protected Dimension calculateSize( boolean minimum ) {
			if( isContentEmpty() )
				return calculateTabAreaSize();

			return super.calculateSize( minimum );
		}

		/**
		 * Check whether all content components are either {@code null} or have zero preferred size.
		 * <p>
		 * If {@code true}, assume that the tabbed pane is used without any content and
		 * use the size of the tab area (single run) as minimum/preferred size.
		 */
		protected boolean isContentEmpty() {
			int tabCount = tabPane.getTabCount();
			if( tabCount == 0 )
				return false;

			for( int i = 0; i < tabCount; i++ ) {
				Component c = tabPane.getComponentAt( i );
				if( c != null ) {
					Dimension cs = c.getPreferredSize();
					if( cs.width != 0 || cs.height != 0 )
						return false;
				}
			}

			return true;
		}

		/**
		 * Calculate preferred size of the tab area.
		 * Used only if {@link #isContentEmpty()} returns {@code true}.
		 */
		protected Dimension calculateTabAreaSize() {
			int tabPlacement = tabPane.getTabPlacement();
			boolean horizontal = isHorizontalTabPlacement( tabPlacement );
			FontMetrics metrics = getFontMetrics();
			int fontHeight = metrics.getHeight();

			// calculate size of tabs
			int width = 0;
			int height = 0;
			int tabCount = tabPane.getTabCount();
			for( int i = 0; i < tabCount; i++ ) {
				if( horizontal ) {
					width += calculateTabWidth( tabPlacement, i, metrics );
					height = Math.max( height, calculateTabHeight( tabPlacement, i, fontHeight ) );
				} else {
					width = Math.max( width, calculateTabWidth( tabPlacement, i, metrics ) );
					height += calculateTabHeight( tabPlacement, i, fontHeight );
				}
			}

			// add content separator thickness
			if( horizontal )
				height += scale( contentSeparatorHeight );
			else
				width += scale( contentSeparatorHeight );

			// add insets
			Insets insets = tabPane.getInsets();
			Insets tabAreaInsets = getTabAreaInsets( tabPlacement );
			return new Dimension(
				width + insets.left + insets.right + tabAreaInsets.left + tabAreaInsets.right,
				height + insets.bottom + insets.top + tabAreaInsets.top + tabAreaInsets.bottom );
		}

		@SuppressWarnings( "deprecation" )
		@Override
		public void layoutContainer( Container parent ) {
			setRolloverTab( -1 );
			calculateLayoutInfo();

			// update visible component
			boolean shouldChangeFocus = false;
			int selectedIndex = tabPane.getSelectedIndex();
			if( selectedIndex >= 0 ) {
				// change visible component only if tab content is not null
				// (see comments in JTabbedPane.fireStateChanged()
				// and in BasicTabbedPaneUI.TabbedPaneLayout.layoutContainer())
				Component oldComp = getVisibleComponent();
				Component newComp = tabPane.getComponentAt( selectedIndex );
				if( newComp != null && newComp != oldComp ) {
					shouldChangeFocus = (SwingUtilities.findFocusOwner( oldComp ) != null);
					setVisibleComponent( newComp );
				}
			} else
				setVisibleComponent( null );

			// layout
			layoutContainerImpl();

			// for compatibility with super class; usually not done here
			// because already done in JTabbedPane.fireStateChanged()
			if( shouldChangeFocus ) {
				// use action because BasicTabbedPaneUI.requestFocusForVisibleComponent()
				// and SwingUtilities2.tabbedPaneChangeFocusTo() are internal methods
				Action action = tabPane.getActionMap().get( "requestFocusForVisibleComponent" );
				if( action != null )
					action.actionPerformed( new ActionEvent( tabPane, ActionEvent.ACTION_PERFORMED, null ) );
			}
		}

		/** @since 3.3 */
		protected void layoutContainerImpl() {
			int tabPlacement = tabPane.getTabPlacement();
			int tabAreaAlignment = getTabAreaAlignment();
			Insets tabAreaInsets = getRealTabAreaInsets( tabPlacement );
			boolean leftToRight = isLeftToRight();

			// tab area bounds
			Rectangle tr = getTabAreaLayoutBounds( tabPlacement, tabAreaInsets );

			// layout tab area
			if( tabPlacement == TOP || tabPlacement == BOTTOM ) {
				// fix x-locations of tabs in right-to-left component orientation
				if( !leftToRight )
					shiftTabs( tabPane.getInsets().left + tabAreaInsets.right + getTrailingPreferredWidth(), 0 );

				// layout left and right components
				layoutLeftAndRightComponents( tr, tabAreaAlignment, tabAreaInsets, (runCount == 1), true, leftToRight );
			} else { // LEFT and RIGHT tab placement
				// layout top and bottom components
				layoutTopAndBottomComponents( tr, tabAreaAlignment, tabAreaInsets, (runCount == 1), true );
			}

			// layout content area components
			// (must be done after layouting tab area, which updates tab rectangles,
			// which are used to layout tab components in layoutTabComponents())
			layoutChildComponents();
		}

		/** @since 3.3 */
		protected void layoutChildComponents() {
			if( tabPane.getComponentCount() == 0 )
				return;

			Rectangle contentAreaBounds = getContentAreaLayoutBounds( tabPane.getTabPlacement(), tabAreaInsets );
			for( Component c : tabPane.getComponents() )
				layoutChildComponent( c, contentAreaBounds );
		}

		/** @since 3.3 */
		protected void layoutChildComponent( Component c, Rectangle contentAreaBounds ) {
			if( c == leadingComponent || c == trailingComponent )
				return;

			if( isTabContainer( c ) )
				layoutTabContainer( c );
			else
				c.setBounds( contentAreaBounds );
		}

		boolean isTabContainer( Component c ) {
			return c.getClass().getName().equals( "javax.swing.plaf.basic.BasicTabbedPaneUI$TabContainer" );
		}

		/**
		 * Layouts container used for custom components in tabs.
		 */
		private void layoutTabContainer( Component tabContainer ) {
			int tabPlacement = tabPane.getTabPlacement();
			Rectangle bounds = tabPane.getBounds();
			Insets insets = tabPane.getInsets();
			Insets contentInsets = getContentBorderInsets( tabPlacement );

			boolean horizontal = isHorizontalTabPlacement( tabPlacement );
			int tabAreaWidth = !horizontal ? calculateTabAreaWidth( tabPlacement, runCount, maxTabWidth ) : 0;
			int tabAreaHeight = horizontal ? calculateTabAreaHeight( tabPlacement, runCount, maxTabHeight ) : 0;
			int w = (tabAreaWidth != 0)
				? tabAreaWidth + insets.left + insets.right + contentInsets.left + contentInsets.right
				: bounds.width;
			int h = (tabAreaHeight != 0)
				? tabAreaHeight + insets.top + insets.bottom + contentInsets.top + contentInsets.bottom
				: bounds.height;

			int x = (tabPlacement == RIGHT) ? bounds.width - w : 0;
			int y = (tabPlacement == BOTTOM) ? bounds.height - h : 0;

			tabContainer.setBounds( x, y, w, h );

			// layout tab components in tab container
			layoutTabComponents( tabContainer );
		}

		/**
		 * Layouts custom components in tabs.
		 */
		void layoutTabComponents( Component tabContainer ) {
			if( tabContainer instanceof Container && ((Container)tabContainer).getComponentCount() == 0 )
				return;

			int tabPlacement = tabPane.getTabPlacement();
			int selectedTabIndex = tabPane.getSelectedIndex();
			Rectangle r = new Rectangle();
			int deltaX = -tabContainer.getX();
			int deltaY = -tabContainer.getY();

			if( isScrollTabLayout() ) {
				// convert delta x,y from JTabbedPane coordinate space to ScrollableTabPanel coordinate space
				Point viewPosition = tabViewport.getViewPosition();
				deltaX = deltaX - tabViewport.getX() + viewPosition.x;
				deltaY = deltaY - tabViewport.getY() + viewPosition.y;
			}

			int tabCount = tabPane.getTabCount();
			for( int i = 0; i < tabCount; i++ ) {
				Component c = tabPane.getTabComponentAt( i );
				if( c == null )
					continue;

				// outer bounds
				Rectangle tabBounds = getTabBounds( i, r );
				Insets tabInsets = getTabInsets( tabPlacement, i );
				int ox = tabBounds.x + tabInsets.left + deltaX;
				int oy = tabBounds.y + tabInsets.top + deltaY;
				int ow = tabBounds.width - tabInsets.left - tabInsets.right;
				int oh = tabBounds.height - tabInsets.top - tabInsets.bottom;

				// center
				Dimension prefSize = c.getPreferredSize();
				int x = ox + ((ow - prefSize.width) / 2);
				int y = oy + ((oh - prefSize.height) / 2);

				// shift
				boolean selected = (i == selectedTabIndex);
				x += getTabLabelShiftX( tabPlacement, i, selected );
				y += getTabLabelShiftY( tabPlacement, i, selected );

				c.setBounds( x, y, prefSize.width, prefSize.height );
			}
		}

		/**
		 * Returns bounds for content components.
		 */
		Rectangle getContentAreaLayoutBounds( int tabPlacement, Insets tabAreaInsets ) {
			int tabPaneWidth = tabPane.getWidth();
			int tabPaneHeight = tabPane.getHeight();
			Insets insets = tabPane.getInsets();
			Insets contentInsets = getContentBorderInsets( tabPlacement );

			boolean horizontal = isHorizontalTabPlacement( tabPlacement );
			int tabAreaWidth = !horizontal ? calculateTabAreaWidth( tabPlacement, runCount, maxTabWidth ) : 0;
			int tabAreaHeight = horizontal ? calculateTabAreaHeight( tabPlacement, runCount, maxTabHeight ) : 0;

			Rectangle cr = new Rectangle();
			cr.x = insets.left + contentInsets.left;
			cr.y = insets.top + contentInsets.top;
			cr.width = tabPaneWidth - insets.left - insets.right - contentInsets.left - contentInsets.right - tabAreaWidth;
			cr.height = tabPaneHeight - insets.top - insets.bottom - contentInsets.top - contentInsets.bottom - tabAreaHeight;
			if( tabPlacement == TOP )
				cr.y += tabAreaHeight;
			else if( tabPlacement == LEFT )
				cr.x += tabAreaWidth;
			return cr;
		}

		/**
		 * Returns bounds for leading/trailing components and tab area.
		 *
		 * Note: Returns bounds for first tabs row only.
		 * For multi-rows tabs in wrap mode, the returned bounds does not include full tab area.
		 */
		Rectangle getTabAreaLayoutBounds( int tabPlacement, Insets tabAreaInsets ) {
			int tabPaneWidth = tabPane.getWidth();
			int tabPaneHeight = tabPane.getHeight();
			Insets insets = tabPane.getInsets();

			Rectangle tr = new Rectangle();
			if( tabPlacement == TOP || tabPlacement == BOTTOM ) {
				// tab area height (maxTabHeight is zero if tab count is zero)
				int tabAreaHeight = (maxTabHeight > 0)
					? maxTabHeight
					: Math.max(
						Math.max( getLeadingPreferredHeight(), getTrailingPreferredHeight() ),
						scale( clientPropertyInt( tabPane, TABBED_PANE_TAB_HEIGHT, tabHeight ) ) );

				// tab area bounds
				tr.x = insets.left;
				tr.y = (tabPlacement == TOP)
					? insets.top + tabAreaInsets.top
					: (tabPaneHeight - insets.bottom - tabAreaInsets.bottom - tabAreaHeight);
				tr.width = tabPaneWidth - insets.left - insets.right;
				tr.height = tabAreaHeight;
			} else { // LEFT and RIGHT tab placement
				// tab area width (maxTabWidth is zero if tab count is zero)
				int tabAreaWidth = (maxTabWidth > 0)
					? maxTabWidth
					: Math.max( getLeadingPreferredWidth(), getTrailingPreferredWidth() );

				// tab area bounds
				tr.x = (tabPlacement == LEFT)
					? insets.left + tabAreaInsets.left
					: (tabPaneWidth - insets.right - tabAreaInsets.right - tabAreaWidth);
				tr.y = insets.top;
				tr.width = tabAreaWidth;
				tr.height = tabPaneHeight - insets.top - insets.bottom;
			}
			return tr;
		}

		Rectangle layoutLeftAndRightComponents( Rectangle tr, int tabAreaAlignment, Insets tabAreaInsets,
			boolean useTabAreaAlignment, boolean shiftTabs, boolean leftToRight )
		{
			int leadingWidth = getLeadingPreferredWidth();
			int trailingWidth = getTrailingPreferredWidth();

			// apply tab area alignment
			if( useTabAreaAlignment && rects.length > 0 ) {
				int availWidth = tr.width - leadingWidth - trailingWidth - tabAreaInsets.left - tabAreaInsets.right;
				int totalTabWidth = rectsTotalWidth( leftToRight );
				int diff = availWidth - totalTabWidth;

				switch( tabAreaAlignment ) {
					case LEADING:
						trailingWidth += diff;
						break;

					case TRAILING:
						if( shiftTabs )
							shiftTabs( leftToRight ? diff : -diff, 0 );
						leadingWidth += diff;
						break;

					case CENTER:
						if( shiftTabs )
							shiftTabs( (leftToRight ? diff : -diff) / 2, 0 );
						leadingWidth += diff / 2;
						trailingWidth += diff - (diff / 2);
						break;

					case FILL:
						stretchTabsWidth( diff, leftToRight );
						break;
				}
			} else if( rects.length == 0 )
				trailingWidth = tr.width - leadingWidth;

			// layout left component
			Container leftComponent = leftToRight ? leadingComponent : trailingComponent;
			int leftWidth = leftToRight ? leadingWidth : trailingWidth;
			if( leftComponent != null )
				leftComponent.setBounds( tr.x, tr.y, leftWidth, tr.height );

			// layout right component
			Container rightComponent = leftToRight ? trailingComponent : leadingComponent;
			int rightWidth = leftToRight ? trailingWidth : leadingWidth;
			if( rightComponent != null )
				rightComponent.setBounds( tr.x + tr.width - rightWidth, tr.y, rightWidth, tr.height );

			// return new tab area bounds reduced by left/right components and applied tab area alignment
			Rectangle r = new Rectangle( tr );
			r.x += leftWidth;
			r.width -= leftWidth + rightWidth;
			return r;
		}

		Rectangle layoutTopAndBottomComponents( Rectangle tr, int tabAreaAlignment, Insets tabAreaInsets,
			boolean useTabAreaAlignment, boolean shiftTabs )
		{
			int topHeight = getLeadingPreferredHeight();
			int bottomHeight = getTrailingPreferredHeight();

			// apply tab area alignment
			if( useTabAreaAlignment && rects.length > 0 ) {
				int availHeight = tr.height - topHeight - bottomHeight - tabAreaInsets.top - tabAreaInsets.bottom;
				int totalTabHeight = rectsTotalHeight();
				int diff = availHeight - totalTabHeight;

				switch( tabAreaAlignment ) {
					case LEADING:
						bottomHeight += diff;
						break;

					case TRAILING:
						if( shiftTabs )
							shiftTabs( 0, diff );
						topHeight += diff;
						break;

					case CENTER:
						if( shiftTabs )
							shiftTabs( 0, diff / 2 );
						topHeight += diff / 2;
						bottomHeight += diff - (diff / 2);
						break;

					case FILL:
						stretchTabsHeight( diff );
						break;
				}
			} else if( rects.length == 0 )
				bottomHeight = tr.height - topHeight;

			// layout top component
			if( leadingComponent != null )
				leadingComponent.setBounds( tr.x, tr.y, tr.width, topHeight );

			// layout bottom component
			if( trailingComponent != null )
				trailingComponent.setBounds( tr.x, tr.y + tr.height - bottomHeight, tr.width, bottomHeight );

			// return new tab area bounds reduced by top/bottom components and applied tab area alignment
			Rectangle r = new Rectangle( tr );
			r.y += topHeight;
			r.height -= topHeight + bottomHeight;
			return r;
		}
	}

	//---- class FlatTabbedPaneScrollLayout -----------------------------------

	/**
	 * Layout manager used for scroll tab layout policy.
	 * <p>
	 * Component hierarchy for scroll tab layout policy:
	 * <pre>{@code
	 * JTabbedPane
	 *    +- 1...n tab content components
	 *    +- BasicTabbedPaneUI.ScrollableTabViewport (extends JViewport)
	 *    |  +- BasicTabbedPaneUI.ScrollableTabPanel (extends JPanel)
	 *    |     +- (optional) BasicTabbedPaneUI.TabContainer (extends JPanel)
	 *    |        +- 1..n tab components (shown in tab area)
	 *    +- FlatScrollableTabButton (scroll forward)
	 *    +- FlatScrollableTabButton (scroll backward)
	 *    +- FlatMoreTabsButton
	 *    +- (optional) ContainerUIResource (extends JPanel)
	 *    |  +- leading component
	 *    +- (optional) ContainerUIResource (extends JPanel)
	 *       +- trailing component
	 * }</pre>
	 * <p>
	 * Instead of using {@code super.layoutContainer(Container)} and fixing some
	 * component bounds, this class implements {@code layoutContainer(Container)}
	 * and moves/resizes components only once.
	 * This avoids that some components are moved/resized twice, which would unnecessary
	 * repaint and relayout tabbed pane. In some special case this resulted in
	 * "endless" layouting and repainting when using nested tabbed panes (top and
	 * bottom tab placement) and RSyntaxTextArea (with enabled line-wrapping) as tab content.
	 * <p>
	 * Although this class delegates nearly all methods to the original layout manager
	 * {@code BasicTabbedPaneUI.TabbedPaneScrollLayout}, which extends
	 * {@link BasicTabbedPaneUI.TabbedPaneLayout}, it is necessary that this class
	 * also extends {@link TabbedPaneLayout} to avoid a {@code ClassCastException}
	 * in {@link BasicTabbedPaneUI}.ensureCurrentLayout().
	 */
	protected class FlatTabbedPaneScrollLayout
		extends FlatTabbedPaneLayout
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

		@Override
		protected Dimension calculateTabAreaSize() {
			Dimension size = super.calculateTabAreaSize();

			// limit width/height in scroll layout
			if( isHorizontalTabPlacement( tabPane.getTabPlacement() ) )
				size.width = Math.min( size.width, scale( 100 ) );
			else
				size.height = Math.min( size.height, scale( 100 ) );
			return size;
		}

		//---- interface LayoutManager ----

		@Override
		public Dimension preferredLayoutSize( Container parent ) {
			if( isContentEmpty() )
				return calculateTabAreaSize();

			return delegate.preferredLayoutSize( parent );
		}

		@Override
		public Dimension minimumLayoutSize( Container parent ) {
			if( isContentEmpty() )
				return calculateTabAreaSize();

			return delegate.minimumLayoutSize( parent );
		}

		@Override
		public void addLayoutComponent( String name, Component comp ) {
			delegate.addLayoutComponent( name, comp );
		}

		@Override
		public void removeLayoutComponent( Component comp ) {
			delegate.removeLayoutComponent( comp );
		}

		/** @since 3.3 */
		@Override
		protected void layoutContainerImpl() {
			// layout content area components
			layoutChildComponents();

			int tabsPopupPolicy = getTabsPopupPolicy();
			int scrollButtonsPolicy = getScrollButtonsPolicy();
			int scrollButtonsPlacement = getScrollButtonsPlacement();

			boolean useMoreTabsButton = (tabsPopupPolicy == AS_NEEDED);
			boolean useScrollButtons = (scrollButtonsPolicy == AS_NEEDED || scrollButtonsPolicy == AS_NEEDED_SINGLE);
			boolean hideDisabledScrollButtons = (scrollButtonsPolicy == AS_NEEDED_SINGLE && scrollButtonsPlacement == BOTH);
			boolean trailingScrollButtons = (scrollButtonsPlacement == TRAILING);

			// For right-to-left, always use "more tabs" button for horizontal scrolling
			// because methods scrollForward() and scrollBackward() in class
			// BasicTabbedPaneUI.ScrollableTabSupport do not work for right-to-left.
			//
			// One problem is that BasicTabbedPaneUI.getClosestTab(), which is used
			// to compute leadingTabIndex, does not work for right-to-left because is uses "binary" search
			// on rects[] to find tab, but rects[] is ordered in reverse order for right-to-left.
			// So leadingTabIndex is either zero or tabCount.
			// Therefore increasing/decreasing leadingTabIndex in scrollForward()
			// and scrollBackward() does not work as expected.
			// Also backward/forward scroll buttons are not correctly enabled/disabled.
			//
			// Fixing this would require replacing nearly whole functionality of class
			// BasicTabbedPaneUI.ScrollableTabSupport, which is not possible because it is private.
			boolean leftToRight = isLeftToRight();
			if( !leftToRight && isHorizontalTabPlacement( tabPane.getTabPlacement() ) ) {
				useMoreTabsButton = true;
				useScrollButtons = false;
			}

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

			if( backwardButton == null || forwardButton == null )
				return; // should never occur

			int tabPlacement = tabPane.getTabPlacement();
			int tabAreaAlignment = getTabAreaAlignment();
			Insets tabAreaInsets = getRealTabAreaInsets( tabPlacement );
			boolean moreTabsButtonVisible = false;
			boolean backwardButtonVisible = false;
			boolean forwardButtonVisible = false;

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

			// tab area bounds
			Rectangle tr = getTabAreaLayoutBounds( tabPlacement, tabAreaInsets );

			// layout tab area
			if( tabPlacement == TOP || tabPlacement == BOTTOM ) {
				// avoid that tab area "jump" to the right when backward button becomes hidden
				if( useScrollButtons && hideDisabledScrollButtons ) {
					Point viewPosition = tabViewport.getViewPosition();
					if( viewPosition.x <= backwardButton.getPreferredSize().width )
						tabViewport.setViewPosition( new Point( 0, viewPosition.y ) );
				}

				int availWidth = tr.width - getLeadingPreferredWidth() - getTrailingPreferredWidth() - tabAreaInsets.left - tabAreaInsets.right;
				int totalTabWidth = (rects.length > 0) ? rectsTotalWidth( leftToRight ) : 0;

				// layout left and right components
				boolean useTabAreaAlignment = (totalTabWidth < availWidth);
				Rectangle tr2 = layoutLeftAndRightComponents( tr, tabAreaAlignment, tabAreaInsets, useTabAreaAlignment, false, leftToRight );

				// layout tab viewport and buttons
				if( rects.length > 0 ) {
					int x = tr2.x + (leftToRight ? tabAreaInsets.left : tabAreaInsets.right);
					int w = tr2.width - tabAreaInsets.left - tabAreaInsets.right;
					int y = tr2.y;
					int h = tr2.height;

					if( w < totalTabWidth ) {
						// available width is too small for all tabs --> need buttons

						// layout more button on trailing side
						if( useMoreTabsButton ) {
							int buttonWidth = moreTabsButton.getPreferredSize().width;
							moreTabsButton.setBounds( leftToRight ? (x + w - buttonWidth) : x, y, buttonWidth, h );
							x += leftToRight ? 0 : buttonWidth;
							w -= buttonWidth;
							moreTabsButtonVisible = true;
						}

						// layout scroll buttons
						if( useScrollButtons ) {
							// the tabViewport view size is set in
							// BasicTabbedPaneUI.TabbedPaneScrollLayout.calculateTabRects(),
							// which is called from calculateLayoutInfo()
							Point viewPosition = tabViewport.getViewPosition();
							Dimension viewSize = tabViewport.getViewSize();

							// layout forward button on trailing side
							if( !hideDisabledScrollButtons || viewSize.width - viewPosition.x > w ) {
								int buttonWidth = forwardButton.getPreferredSize().width;
								forwardButton.setBounds( x + w - buttonWidth, y, buttonWidth, h );
								w -= buttonWidth;
								forwardButtonVisible = true;
							}

							// layout backward button
							if( !hideDisabledScrollButtons || viewPosition.x > 0 ) {
								int buttonWidth = backwardButton.getPreferredSize().width;
								if( trailingScrollButtons ) {
									// on trailing side
									backwardButton.setBounds( x + w - buttonWidth, y, buttonWidth, h );
								} else {
									// on leading side
									backwardButton.setBounds( x, y, buttonWidth, h );
									x += buttonWidth;
								}
								w -= buttonWidth;
								backwardButtonVisible = true;
							}
						}
					}

					tabViewport.setBounds( x, y, w, h );

					if( !leftToRight ) {
						// layout viewport so that we can get correct view width below
						tabViewport.doLayout();

						// fix x-locations of tabs so that they are right-aligned in the view
						shiftTabs( tabViewport.getView().getWidth() - (rects[0].x + rects[0].width), 0 );
					}
				}
			} else { // LEFT and RIGHT tab placement
				// avoid that tab area "jump" to the top when backward button becomes hidden
				if( useScrollButtons && hideDisabledScrollButtons ) {
					Point viewPosition = tabViewport.getViewPosition();
					if( viewPosition.y <= backwardButton.getPreferredSize().height )
						tabViewport.setViewPosition( new Point( viewPosition.x, 0 ) );
				}

				int availHeight = tr.height - getLeadingPreferredHeight() - getTrailingPreferredHeight() - tabAreaInsets.top - tabAreaInsets.bottom;
				int totalTabHeight = (rects.length > 0) ? rectsTotalHeight() : 0;

				// layout top and bottom components
				boolean useTabAreaAlignment = (totalTabHeight < availHeight);
				Rectangle tr2 = layoutTopAndBottomComponents( tr, tabAreaAlignment, tabAreaInsets, useTabAreaAlignment, false );

				// layout tab viewport and buttons
				if( rects.length > 0 ) {
					int y = tr2.y + tabAreaInsets.top;
					int h = tr2.height - tabAreaInsets.top - tabAreaInsets.bottom;
					int x = tr2.x;
					int w = tr2.width;

					if( h < totalTabHeight ) {
						// available height is too small for all tabs --> need buttons

						// layout more button on bottom side
						if( useMoreTabsButton ) {
							int buttonHeight = moreTabsButton.getPreferredSize().height;
							moreTabsButton.setBounds( x, y + h - buttonHeight, w, buttonHeight );
							h -= buttonHeight;
							moreTabsButtonVisible = true;
						}

						// layout scroll buttons
						if( useScrollButtons ) {
							// the tabViewport view size is set in
							// BasicTabbedPaneUI.TabbedPaneScrollLayout.calculateTabRects(),
							// which is called from calculateLayoutInfo()
							Point viewPosition = tabViewport.getViewPosition();
							Dimension viewSize = tabViewport.getViewSize();

							// layout forward button on bottom side
							if( !hideDisabledScrollButtons || viewSize.height - viewPosition.y > h ) {
								int buttonHeight = forwardButton.getPreferredSize().height;
								forwardButton.setBounds( x, y + h - buttonHeight, w, buttonHeight );
								h -= buttonHeight;
								forwardButtonVisible = true;
							}

							// layout backward button
							if( !hideDisabledScrollButtons || viewPosition.y > 0 ) {
								int buttonHeight = backwardButton.getPreferredSize().height;
								if( trailingScrollButtons ) {
									// on bottom side
									backwardButton.setBounds( x, y + h - buttonHeight, w, buttonHeight );
								} else {
									// on top side
									backwardButton.setBounds( x, y, w, buttonHeight );
									y += buttonHeight;
								}
								h -= buttonHeight;
								backwardButtonVisible = true;
							}
						}
					}

					tabViewport.setBounds( x, y, w, h );
				}
			}

			// layout tab components in tab container
			Component view = tabViewport.getView();
			if( view instanceof Container && ((Container)view).getComponentCount() > 0 ) {
				for( Component c : ((Container)view).getComponents() ) {
					if( isTabContainer( c ) ) {
						layoutTabComponents( c );
						break;
					}
				}
			}

			// show/hide viewport and buttons
			tabViewport.setVisible( rects.length > 0 );
			moreTabsButton.setVisible( moreTabsButtonVisible );
			backwardButton.setVisible( backwardButtonVisible );
			forwardButton.setVisible( forwardButtonVisible );

			scrollBackwardButtonPrefSize = backwardButton.getPreferredSize();
		}

		/** @since 3.3 */
		@Override
		protected void layoutChildComponent( Component c, Rectangle contentAreaBounds ) {
			if( c == tabViewport || c instanceof FlatTabAreaButton || c == leadingComponent || c == trailingComponent )
				return;

			c.setBounds( contentAreaBounds );
		}
	}

	//---- class RunWithOriginalLayoutManagerDelegateAction -------------------

	private static class RunWithOriginalLayoutManagerDelegateAction
		extends FlatUIAction
	{
		static void install( ActionMap map, String key ) {
			Action oldAction = map.get( key );
			if( oldAction == null || oldAction instanceof RunWithOriginalLayoutManagerDelegateAction )
				return; // not found or already installed

			map.put( key, new RunWithOriginalLayoutManagerDelegateAction( oldAction ) );
		}

		private RunWithOriginalLayoutManagerDelegateAction( Action delegate ) {
			super( delegate );
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
			ComponentUI ui = tabbedPane.getUI();
			if( ui instanceof FlatTabbedPaneUI ) {
				((FlatTabbedPaneUI)ui).runWithOriginalLayoutManager( () -> {
					delegate.actionPerformed( e );
				} );
			} else
				delegate.actionPerformed( e );
		}
	}

	//---- class FlatSelectedTabRepainter -------------------------------------

	private static class FlatSelectedTabRepainter
		implements PropertyChangeListener
	{
		private static FlatSelectedTabRepainter instance;

		private KeyboardFocusManager keyboardFocusManager;

		static void install() {
			synchronized( FlatSelectedTabRepainter.class ) {
				if( instance != null )
					return;

				instance = new FlatSelectedTabRepainter();
			}
		}

		FlatSelectedTabRepainter() {
			keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			keyboardFocusManager.addPropertyChangeListener( this );
		}

		private void uninstall() {
			synchronized( FlatSelectedTabRepainter.class ) {
				if( instance == null )
					return;

				keyboardFocusManager.removePropertyChangeListener( this );
				keyboardFocusManager = null;
				instance = null;
			}
		}

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			// uninstall if no longer using FlatLaf
			if( !(UIManager.getLookAndFeel() instanceof FlatLaf) ) {
				uninstall();
				return;
			}

			switch( e.getPropertyName() ) {
				case "permanentFocusOwner":
					Object oldValue = e.getOldValue();
					Object newValue = e.getNewValue();
					if( oldValue instanceof Component )
						repaintSelectedTabs( (Component) oldValue );
					if( newValue instanceof Component )
						repaintSelectedTabs( (Component) newValue );
					break;

				case "activeWindow":
					Component permanentFocusOwner = keyboardFocusManager.getPermanentFocusOwner();
					if( permanentFocusOwner != null )
						repaintSelectedTabs( permanentFocusOwner );
					break;
			}
		}

		private void repaintSelectedTabs( Component c ) {
			// Use invokeLater because this method may be invoked while UI update
			// is in progress. This may happen if a focusable component (e.g. text field)
			// is used as tab component (see JTabbedPane.setTabComponentAt()).
			// uninstallTabContainer() removes all components from tabbed pane and
			// the text field looses focus.
			EventQueue.invokeLater( () -> {
				// because this is invoked later, check whether component is still displayable
				if( !c.isDisplayable() )
					return;

				if( c instanceof JTabbedPane )
					repaintSelectedTab( (JTabbedPane) c );

				Component c2 = c;
				while( (c2 = SwingUtilities.getAncestorOfClass( JTabbedPane.class, c2 )) != null )
					repaintSelectedTab( (JTabbedPane) c2 );
			} );
		}

		private void repaintSelectedTab( JTabbedPane tabbedPane ) {
			TabbedPaneUI ui = tabbedPane.getUI();
			if( ui instanceof FlatTabbedPaneUI )
				((FlatTabbedPaneUI) ui).repaintTab( tabbedPane.getSelectedIndex() );
		}
	}
}
