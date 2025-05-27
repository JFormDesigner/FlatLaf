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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Panel;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowFocusListener;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboPopup;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * A popup factory that adds drop shadows to popups on Windows.
 * On macOS and Linux, heavy weight popups (without drop shadow) are produced and the
 * operating system automatically adds drop shadows.
 *
 * @author Karl Tauber
 */
public class FlatPopupFactory
	extends PopupFactory
{
	static final String KEY_POPUP_USES_NATIVE_BORDER = "FlatLaf.internal.FlatPopupFactory.popupUsesNativeBorder";

	private MethodHandle java8getPopupMethod;
	private MethodHandle java9getPopupMethod;

	private final ArrayList<NonFlashingPopup> stillShownHeavyWeightPopups = new ArrayList<>();

	@Override
	public Popup getPopup( Component owner, Component contents, int x, int y )
		throws IllegalArgumentException
	{
		Point pt = fixToolTipLocation( owner, contents, x, y );
		if( pt != null ) {
			x = pt.x;
			y = pt.y;
		}

		fixLinuxWaylandJava21focusIssue( owner );

		// reuse a heavy weight popup window, which is still shown on screen,
		// to avoid flicker when popup (e.g. tooltip) is moving while mouse is moved
		for( NonFlashingPopup popup : stillShownHeavyWeightPopups ) {
			if( popup.delegate != null &&
				popup.owner == owner &&
				(popup.contents == contents ||
				 (popup.contents instanceof JToolTip && contents instanceof JToolTip)) )
			{
				stillShownHeavyWeightPopups.remove( popup );
				return reuseStillShownHeavyWeightPopups( popup, contents, x, y );
			}
		}

		boolean forceHeavyWeight = isOptionEnabled( owner, contents, FlatClientProperties.POPUP_FORCE_HEAVY_WEIGHT, "Popup.forceHeavyWeight" );

		if( !isOptionEnabled( owner, contents, FlatClientProperties.POPUP_DROP_SHADOW_PAINTED, "Popup.dropShadowPainted" ) || SystemInfo.isProjector || SystemInfo.isWebswing )
			return new NonFlashingPopup( getPopupForScreenOfOwner( owner, contents, x, y, forceHeavyWeight ), owner, contents );

		// macOS and Linux adds drop shadow to heavy weight popups
		if( SystemInfo.isMacOS || SystemInfo.isLinux ) {
			NonFlashingPopup popup = new NonFlashingPopup( getPopupForScreenOfOwner( owner, contents, x, y, true ), owner, contents );
			if( popup.popupWindow != null && isMacOSBorderSupported() )
				setupRoundedBorder( popup.popupWindow, owner, contents );
			return popup;
		}

		// Windows 11 with FlatLaf native library can use rounded corners and shows drop shadow for heavy weight popups
		if( isWindows11BorderSupported() &&
			getBorderCornerRadius( owner, contents ) > 0 )
		{
			NonFlashingPopup popup = new NonFlashingPopup( getPopupForScreenOfOwner( owner, contents, x, y, true ), owner, contents );
			if( popup.popupWindow != null )
				setupRoundedBorder( popup.popupWindow, owner, contents );
			return popup;
		}

		// check whether popup overlaps a heavy weight component
		if( !forceHeavyWeight && overlapsHeavyWeightComponent( owner, contents, x, y ) )
			forceHeavyWeight = true;

		// create drop shadow popup
		Popup popupForScreenOfOwner = getPopupForScreenOfOwner( owner, contents, x, y, forceHeavyWeight );
		GraphicsConfiguration gc = (owner != null) ? owner.getGraphicsConfiguration() : null;
		return (gc != null && gc.isTranslucencyCapable())
			? new DropShadowPopup( popupForScreenOfOwner, owner, contents )
			: new NonFlashingPopup( popupForScreenOfOwner, owner, contents );
	}

	/**
	 * Creates a popup for the screen that the owner component is on.
	 * <p>
	 * PopupFactory caches heavy weight popup windows and reuses them.
	 * On a dual screen setup, if the popup owner has moved from one screen to the other one,
	 * then the cached heavy weight popup window may be connected to the wrong screen.
	 * If the two screens use different scaling factors, then the popup location and size
	 * is scaled when the popup becomes visible, which shows the popup in the wrong location
	 * (or on wrong screen). The re-scaling is done in WWindowPeer.setBounds() (Java 9+).
	 * <p>
	 * To fix this, dispose popup windows that are on wrong screen and get new popup.
	 * <p>
	 * This is a workaround for https://bugs.openjdk.java.net/browse/JDK-8224608
	 */
	private Popup getPopupForScreenOfOwner( Component owner, Component contents, int x, int y, boolean forceHeavyWeight )
		throws IllegalArgumentException
	{
		int count = 0;

		for(;;) {
			// create new or get cached popup
			Popup popup = forceHeavyWeight
				? getHeavyWeightPopup( owner, contents, x, y )
				: super.getPopup( owner, contents, x, y );

			// get heavy weight popup window; is null for non-heavy weight popup
			Window popupWindow = SwingUtilities.windowForComponent( contents );

			// check whether heavy weight popup window is on same screen as owner component
			if( popupWindow == null ||
				owner == null ||
				popupWindow.getGraphicsConfiguration() == owner.getGraphicsConfiguration() )
			  return popup;

			// avoid endless loop (should newer happen; PopupFactory cache size is 5)
			if( ++count > 10 )
				return popup;

			// remove contents component from popup window
			if( popupWindow instanceof JWindow )
				((JWindow)popupWindow).getContentPane().removeAll();

			// dispose unused popup
			// (do not invoke popup.hide() because this would cache the popup window)
			popupWindow.dispose();
		}
	}

	/**
	 * There is no API in Java 8 to force creation of heavy weight popups,
	 * but it is possible with reflection. Java 9 provides a new method.
	 *
	 * When changing FlatLaf system requirements to Java 9+,
	 * then this method can be replaced with:
	 *    return getPopup( owner, contents, x, y, true );
	 */
	private Popup getHeavyWeightPopup( Component owner, Component contents, int x, int y )
		throws IllegalArgumentException
	{
		try {
			if( SystemInfo.isJava_9_orLater ) {
				// Java 9: protected Popup getPopup( Component owner, Component contents, int x, int y, boolean isHeavyWeightPopup )
				if( java9getPopupMethod == null ) {
					MethodType mt = MethodType.methodType( Popup.class, Component.class, Component.class, int.class, int.class, boolean.class );
					java9getPopupMethod = MethodHandles.lookup().findVirtual( PopupFactory.class, "getPopup", mt );
				}
				return (Popup) java9getPopupMethod.invoke( this, owner, contents, x, y, true );
			} else {
				// Java 8: private Popup getPopup( Component owner, Component contents, int ownerX, int ownerY, int popupType )
				if( java8getPopupMethod == null ) {
					Method m = PopupFactory.class.getDeclaredMethod(
						"getPopup", Component.class, Component.class, int.class, int.class, int.class );
					m.setAccessible( true );
					java8getPopupMethod = MethodHandles.lookup().unreflect( m );
				}
				return (Popup) java8getPopupMethod.invoke( this, owner, contents, x, y, /*HEAVY_WEIGHT_POPUP*/ 2 );
			}
		} catch( Throwable ex ) {
			// fallback
			return super.getPopup( owner, contents, x, y );
		}
	}

	private static boolean isOptionEnabled( Component owner, Component contents, String clientKey, String uiKey ) {
		Object value = getOption( owner, contents, clientKey, uiKey );
		return (value instanceof Boolean) ? (Boolean) value : false;
	}

	/**
	 * Get option from:
	 * <ol>
	 * <li>client property {@code clientKey} of {@code owner}
	 * <li>client property {@code clientKey} of {@code contents}
	 * <li>UI property {@code uiKey}
	 * </ol>
	 */
	private static Object getOption( Component owner, Component contents, String clientKey, String uiKey ) {
		for( Component c : new Component[] { owner, contents } ) {
			if( c instanceof JComponent ) {
				Object value = ((JComponent)c).getClientProperty( clientKey );
				if( value != null )
					return value;
			}
		}

		return UIManager.get( uiKey );
	}

	/**
	 * Reuse a heavy weight popup window, which is still shown on screen,
	 * by updating window location and contents.
	 * This avoid flicker when popup (e.g. a tooltip) is moving while mouse is moved.
	 * E.g. overridden JComponent.getToolTipLocation(MouseEvent).
	 * See ToolTipManager.checkForTipChange(MouseEvent).
	 */
	private static NonFlashingPopup reuseStillShownHeavyWeightPopups(
		NonFlashingPopup reusePopup, Component contents, int ownerX, int ownerY )
	{
		// clone popup because PopupFactory.getPopup() should not return old instance
		NonFlashingPopup popup = reusePopup.cloneForReuse();

		// update popup location, size and contents
		popup.reset( contents, ownerX, ownerY );
		return popup;
	}

	//---- tooltips -----------------------------------------------------------

	/**
	 * Usually ToolTipManager places a tooltip at (mouseLocation.x, mouseLocation.y + 20).
	 * In case that the tooltip would be partly outside of the screen,
	 * the ToolTipManager changes the location so that the entire tooltip fits on screen.
	 * But this can place the tooltip under the mouse location and hide the owner component.
	 * <p>
	 * This method checks whether the current mouse location is within tooltip bounds
	 * and corrects the y-location so that the tooltip is placed above the mouse location.
	 */
	private Point fixToolTipLocation( Component owner, Component contents, int x, int y ) {
		if( !(contents instanceof JToolTip) || !wasInvokedFromToolTipManager() || hasTipLocation( owner ) )
			return null;

		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		if( pointerInfo == null )
			return null;

		Point mouseLocation = pointerInfo.getLocation();
		Dimension tipSize = contents.getPreferredSize();

		// check whether mouse location is within tooltip bounds
		Rectangle tipBounds = new Rectangle( x, y, tipSize.width, tipSize.height );
		if( !tipBounds.contains( mouseLocation ) )
			return null;

		// find GraphicsConfiguration at mouse location (similar to ToolTipManager.getDrawingGC())
		GraphicsConfiguration gc = null;
		for( GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices() ) {
			GraphicsConfiguration dgc = device.getDefaultConfiguration();
			if( dgc.getBounds().contains( mouseLocation ) ) {
				gc = dgc;
				break;
			}
		}
		if( gc == null && owner != null )
			gc = owner.getGraphicsConfiguration();
		if( gc == null )
			return null;

		Rectangle screenBounds = gc.getBounds();
		Insets screenInsets = FlatUIUtils.getScreenInsets( gc );
		int screenTop = screenBounds.y + screenInsets.top;

		// place tooltip above mouse location if there is enough space
		int newY =  mouseLocation.y - tipSize.height - UIScale.scale( 20 );
		if( newY < screenTop )
			return null;

		return new Point( x, newY );
	}

	private boolean wasInvokedFromToolTipManager() {
		return StackUtils.wasInvokedFrom( ToolTipManager.class.getName(), "showTipWindow", 8 );
	}

	/**
	 * Checks whether the owner component returns a tooltip location in
	 * JComponent.getToolTipLocation(MouseEvent).
	 */
	private boolean hasTipLocation( Component owner ) {
		if( !(owner instanceof JComponent) )
			return false;

		AWTEvent e = EventQueue.getCurrentEvent();
		MouseEvent me;
		if( e instanceof MouseEvent )
			me = (MouseEvent) e;
		else {
			// no mouse event available because a timer is used to show the tooltip
			// --> create mouse event from current mouse location
			PointerInfo pointerInfo = MouseInfo.getPointerInfo();
			if( pointerInfo == null )
				return false;

			Point location = new Point( pointerInfo.getLocation());
			SwingUtilities.convertPointFromScreen( location, owner );
			me = new MouseEvent( owner, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
				0, location.x, location.y, 0, false );
		}

		return me.getSource() == owner &&
			((JComponent)owner).getToolTipLocation( me ) != null;
	}

	//---- native rounded border ----------------------------------------------

	private static boolean isWindows11BorderSupported() {
		return SystemInfo.isWindows_11_orLater &&
			FlatSystemProperties.getBoolean( FlatSystemProperties.USE_ROUNDED_POPUP_BORDER, true ) &&
			FlatNativeWindowsLibrary.isLoaded();
	}

	private static boolean isMacOSBorderSupported() {
		return SystemInfo.isMacOS &&
			FlatSystemProperties.getBoolean( FlatSystemProperties.USE_ROUNDED_POPUP_BORDER, true ) &&
			FlatNativeMacLibrary.isLoaded();
	}

	private static void setupRoundedBorder( Window popupWindow, Component owner, Component contents ) {
		int borderCornerRadius = getBorderCornerRadius( owner, contents );
		float borderWidth = getRoundedBorderWidth( owner, contents );

		// get Swing border color
		Color borderColor;
		if( contents instanceof JComponent ) {
			Border border = ((JComponent)contents).getBorder();
			border = FlatUIUtils.unwrapNonUIResourceBorder( border );

			// get color from border of contents (e.g. JPopupMenu or JToolTip)
			if( border instanceof FlatLineBorder )
				borderColor = ((FlatLineBorder)border).getLineColor();
			else if( border instanceof LineBorder )
				borderColor = ((LineBorder)border).getLineColor();
			else if( border instanceof EmptyBorder )
				borderColor = FlatNativeWindowsLibrary.COLOR_NONE; // do not paint border
			else
				borderColor = null; // use system default color

			// avoid that FlatLineBorder paints the Swing border
			((JComponent)contents).putClientProperty( KEY_POPUP_USES_NATIVE_BORDER, true );
		} else
			borderColor = null; // use system default color

		if( popupWindow.isDisplayable() ) {
			// native window already created
			setupRoundedBorderImpl( popupWindow, borderCornerRadius, borderWidth, borderColor );
		} else {
			// native window not yet created --> add listener to set native border after window creation
			AtomicReference<HierarchyListener> l = new AtomicReference<>();
			l.set( e -> {
				if( e.getID() == HierarchyEvent.HIERARCHY_CHANGED &&
					(e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 )
				{
					setupRoundedBorderImpl( popupWindow, borderCornerRadius, borderWidth, borderColor );
					popupWindow.removeHierarchyListener( l.get() );
				}
			} );
			popupWindow.addHierarchyListener( l.get() );
		}
	}

	private static void setupRoundedBorderImpl( Window popupWindow, int borderCornerRadius, float borderWidth, Color borderColor ) {
		if( SystemInfo.isWindows ) {
			// get native window handle
			long hwnd = FlatNativeWindowsLibrary.getHWND( popupWindow );

			// set corner preference
			int cornerPreference = (borderCornerRadius <= 4)
				? FlatNativeWindowsLibrary.DWMWCP_ROUNDSMALL  // 4px
				: FlatNativeWindowsLibrary.DWMWCP_ROUND;      // 8px
			FlatNativeWindowsLibrary.setWindowCornerPreference( hwnd, cornerPreference );

			// set border color
			FlatNativeWindowsLibrary.dwmSetWindowAttributeCOLORREF( hwnd, FlatNativeWindowsLibrary.DWMWA_BORDER_COLOR, borderColor );
		} else if( SystemInfo.isMacOS ) {
			if( borderColor == null || borderColor == FlatNativeWindowsLibrary.COLOR_NONE )
				borderWidth = 0;

			// set corner radius, border width and color
			FlatNativeMacLibrary.setWindowRoundedBorder( popupWindow, borderCornerRadius,
				borderWidth, (borderColor != null) ? borderColor.getRGB() : 0 );
		}
	}

	private static void resetWindows11Border( Window popupWindow ) {
		// get window handle
		long hwnd = FlatNativeWindowsLibrary.getHWND( popupWindow );
		if( hwnd == 0 )
			return;

		// reset corner preference
		FlatNativeWindowsLibrary.setWindowCornerPreference( hwnd, FlatNativeWindowsLibrary.DWMWCP_DONOTROUND );
	}

	private static int getBorderCornerRadius( Component owner, Component contents ) {
		String uiKey =
			(contents instanceof BasicComboPopup) ? "ComboBox.borderCornerRadius" :
			(contents instanceof JPopupMenu) ? "PopupMenu.borderCornerRadius" :
			(contents instanceof JToolTip) ? "ToolTip.borderCornerRadius" :
			"Popup.borderCornerRadius";

		Object value = getOption( owner, contents, FlatClientProperties.POPUP_BORDER_CORNER_RADIUS, uiKey );
		return (value instanceof Integer) ? (Integer) value : 0;
	}

	private static float getRoundedBorderWidth( Component owner, Component contents ) {
		String uiKey =
			(contents instanceof BasicComboPopup) ? "ComboBox.roundedBorderWidth" :
			(contents instanceof JPopupMenu) ? "PopupMenu.roundedBorderWidth" :
			(contents instanceof JToolTip) ? "ToolTip.roundedBorderWidth" :
			"Popup.roundedBorderWidth";

		Object value = getOption( owner, contents, FlatClientProperties.POPUP_ROUNDED_BORDER_WIDTH, uiKey );
		return (value instanceof Number) ? ((Number)value).floatValue() : 0;
	}

	//---- fixes --------------------------------------------------------------

	private static boolean overlapsHeavyWeightComponent( Component owner, Component contents, int x, int y ) {
		if( owner == null )
			return false;

		Window window = SwingUtilities.getWindowAncestor( owner );
		if( window == null )
			return false;

		Rectangle r = new Rectangle( new Point( x, y ), contents.getPreferredSize() );
		return overlapsHeavyWeightComponent( window, r );
	}

	private static boolean overlapsHeavyWeightComponent( Component parent, Rectangle r ) {
		if( !parent.isVisible() || !r.intersects( parent.getBounds() ) )
			return false;

		if( !parent.isLightweight() && !(parent instanceof Window) )
			return true;

		if( parent instanceof Container ) {
			Rectangle r2 = new Rectangle( r.x - parent.getX(), r.y - parent.getY(), r.width, r.height );
			for( Component c : ((Container)parent).getComponents() ) {
				if( overlapsHeavyWeightComponent( c, r2 ) )
					return true;
			}
		}

		return false;
	}

	/**
	 * On Linux with Wayland, since Java 21, Swing adds a window focus listener to popup owner/invoker window,
	 * which hides the popup as soon as the owner/invoker window looses focus.
	 * This works fine for light-weight popups.
	 * It also works for heavy-weight popups if they do not request focus.
	 * Because FlatLaf always uses heavy-weight popups, all popups that request focus
	 * are broken since Java 21.
	 *
	 * This method removes the problematic window focus listener.
	 *
	 * https://bugs.openjdk.org/browse/JDK-8280993
	 * https://github.com/openjdk/jdk/pull/13830
	 */
	private static void fixLinuxWaylandJava21focusIssue( Component owner ) {
		// only necessary on Linux when running in Java 21+
		if( owner == null || !SystemInfo.isLinux || SystemInfo.javaVersion < SystemInfo.toVersion( 21, 0, 0, 0 ) )
			return;

		// get window
		Window window = SwingUtilities.getWindowAncestor( owner );
		if( window == null )
			return;

		// remove window focus listener, which was added from class sun.awt.UNIXToolkit since Java 21
		for( WindowFocusListener l : window.getWindowFocusListeners() ) {
			if( "sun.awt.UNIXToolkit$1".equals( l.getClass().getName() ) ) {
				window.removeWindowFocusListener( l );
				break;
			}
		}
	}

	/**
	 * Shows the given popup and, if necessary, fixes the location of a heavy weight popup window.
	 * <p>
	 * On a dual screen setup, where screens use different scale factors, it may happen
	 * that the window location changes when showing a heavy weight popup window.
	 * E.g. when opening a dialog on the secondary screen and making combobox popup visible.
	 * <p>
	 * This is a workaround for https://bugs.openjdk.java.net/browse/JDK-8224608
	 */
	private static void showPopupAndFixLocation( Popup popup, Window popupWindow ) {
		if( popupWindow != null ) {
			// remember location of heavy weight popup window
			int x = popupWindow.getX();
			int y = popupWindow.getY();

			popup.show();

			// restore popup window location if it has changed
			// (probably scaled when screens use different scale factors)
			if( popupWindow.getX() != x || popupWindow.getY() != y )
				popupWindow.setLocation( x, y );
		} else
			popup.show();
	}

	//---- class NonFlashingPopup ---------------------------------------------

	/**
	 * Fixes popup background flashing effect when using dark theme on light platform theme,
	 * where the light popup background is shown for a fraction of a second before
	 * the dark popup content is shown.
	 * This is fixed by setting popup background to content background.
	 * <p>
	 * Defers hiding of heavy weight popup window for an event cycle,
	 * which allows reusing popup window to avoid flicker when "moving" popup.
	 */
	private class NonFlashingPopup
		extends Popup
	{
		private Popup delegate;
		Component owner;
		private Component contents;

		// heavy weight
		Window popupWindow;
		private Color oldPopupWindowBackground;

		private boolean disposed;

		NonFlashingPopup( Popup delegate, Component owner, Component contents ) {
			this.delegate = delegate;
			this.owner = owner;
			this.contents = contents;

			popupWindow = SwingUtilities.windowForComponent( contents );
			if( popupWindow != null ) {
				// heavy weight popup

				// fix background flashing which may occur on some platforms
				// (e.g. macOS and Linux) when using dark theme
				oldPopupWindowBackground = popupWindow.getBackground();
				popupWindow.setBackground( contents.getBackground() );
			}
		}

		private NonFlashingPopup( NonFlashingPopup reusePopup ) {
			delegate = reusePopup.delegate;
			owner = reusePopup.owner;
			contents = reusePopup.contents;
			popupWindow = reusePopup.popupWindow;
			oldPopupWindowBackground = reusePopup.oldPopupWindowBackground;
		}

		NonFlashingPopup cloneForReuse() {
			return new NonFlashingPopup( this );
		}

		@Override
		public final void show() {
			if( disposed )
				return;

			showImpl();
		}

		void showImpl() {
			if( delegate != null ) {
				showPopupAndFixLocation( delegate, popupWindow );

				// increase tooltip size if necessary because it may be too small on HiDPI screens
				//    https://bugs.openjdk.java.net/browse/JDK-8213535
				if( contents instanceof JToolTip && popupWindow == null ) {
					Container parent = contents.getParent();
					if( parent instanceof JPanel ) {
						Dimension prefSize = parent.getPreferredSize();
						if( !prefSize.equals( parent.getSize() ) ) {
							Container mediumWeightPanel = SwingUtilities.getAncestorOfClass( Panel.class, parent );
							Container c = (mediumWeightPanel != null)
								? mediumWeightPanel // medium weight popup
								: parent;           // light weight popup
							c.setSize( prefSize );
							c.validate();
						}
					}
				}
			}
		}

		@Override
		public final void hide() {
			if( disposed )
				return;
			disposed = true;

			// immediately hide non-heavy weight popups or combobox popups
			if( !(popupWindow instanceof JWindow) || contents instanceof BasicComboPopup ) {
				hideImpl();
				return;
			}

			// defer hiding of heavy weight popup window for an event cycle,
			// which allows reusing popup window to avoid flicker when "moving" popup
			((JWindow)popupWindow).getContentPane().removeAll();
			stillShownHeavyWeightPopups.add( this );
			EventQueue.invokeLater( () -> {
				// hide popup if it was not reused
				if( stillShownHeavyWeightPopups.remove( this ) )
					hideImpl();
			} );
		}

		void hideImpl() {
			if( contents instanceof JComponent )
				((JComponent)contents).putClientProperty( KEY_POPUP_USES_NATIVE_BORDER, null );

			if( delegate != null ) {
				delegate.hide();
				delegate = null;
				owner = null;
				contents = null;
			}

			if( popupWindow != null ) {
				// restore background so that it can not affect other LaFs (when switching)
				// because popup windows are cached and reused
				popupWindow.setBackground( oldPopupWindowBackground );
				popupWindow = null;
			}
		}

		void reset( Component contents, int ownerX, int ownerY ) {
			// update popup window location
			popupWindow.setLocation( ownerX, ownerY );

			// replace component in content pane
			Container contentPane = ((JWindow)popupWindow).getContentPane();
			contentPane.removeAll();
			contentPane.add( contents, BorderLayout.CENTER );
			popupWindow.pack();

			// update client property on contents
			if( this.contents != contents ) {
				Object old = (this.contents instanceof JComponent)
					? ((JComponent)this.contents).getClientProperty( KEY_POPUP_USES_NATIVE_BORDER )
					: null;
				if( contents instanceof JComponent )
					((JComponent)contents).putClientProperty( KEY_POPUP_USES_NATIVE_BORDER, old );

				this.contents = contents;
			}
		}
	}

	//---- class DropShadowPopup ----------------------------------------------

	private class DropShadowPopup
		extends NonFlashingPopup
		implements ComponentListener
	{
		// light weight
		private JComponent lightComp;
		private Border oldBorder;
		private boolean oldOpaque;

		// medium weight
		private boolean mediumWeightShown;
		private Panel mediumWeightPanel;
		private JPanel dropShadowPanel;
		private ComponentListener mediumPanelListener;

		// heavy weight
		private Popup dropShadowDelegate;
		private Window dropShadowWindow;
		private JPanel dropShadowPanel2;
		private Color oldDropShadowWindowBackground;

		DropShadowPopup( Popup delegate, Component owner, Component contents ) {
			super( delegate, owner, contents );

			Dimension size = contents.getPreferredSize();
			if( size.width <= 0 || size.height <= 0 )
				return;

			if( popupWindow != null ) {
				// heavy weight popup

				// Since Java has a problem with sub-pixel text rendering on translucent
				// windows, we can not make the popup window translucent for the drop shadow.
				// (see https://bugs.openjdk.java.net/browse/JDK-8215980)
				// The solution is to create a second translucent window that paints
				// the drop shadow and is positioned behind the popup window.

				// create panel that paints the drop shadow
				dropShadowPanel2 = new JPanel();
				dropShadowPanel2.setBorder( createDropShadowBorder() );
				dropShadowPanel2.setOpaque( false );

				// set preferred size of drop shadow panel
				Dimension prefSize = popupWindow.getPreferredSize();
				Insets insets = dropShadowPanel2.getInsets();
				dropShadowPanel2.setPreferredSize( new Dimension(
					prefSize.width + insets.left + insets.right,
					prefSize.height + insets.top + insets.bottom ) );

				// create heavy weight popup for drop shadow
				int x = popupWindow.getX() - insets.left;
				int y = popupWindow.getY() - insets.top;
				dropShadowDelegate = getPopupForScreenOfOwner( owner, dropShadowPanel2, x, y, true );

				// make drop shadow popup window translucent
				dropShadowWindow = SwingUtilities.windowForComponent( dropShadowPanel2 );
				if( dropShadowWindow != null ) {
					oldDropShadowWindowBackground = dropShadowWindow.getBackground();
					dropShadowWindow.setBackground( new Color( 0, true ) );
				}

				// Windows 11: reset corner preference on reused heavy weight popups
				if( SystemInfo.isWindows_11_orLater && FlatNativeWindowsLibrary.isLoaded() ) {
					resetWindows11Border( popupWindow );
					if( dropShadowWindow != null )
						resetWindows11Border( dropShadowWindow );
				}

			} else {
				mediumWeightPanel = (Panel) SwingUtilities.getAncestorOfClass( Panel.class, contents );
				if( mediumWeightPanel != null ) {
					// medium weight popup
					dropShadowPanel = new JPanel();
					dropShadowPanel.setBorder( createDropShadowBorder() );
					dropShadowPanel.setOpaque( false );
					dropShadowPanel.setSize( FlatUIUtils.addInsets( mediumWeightPanel.getSize(), dropShadowPanel.getInsets() ) );
				} else {
					// light weight popup
					Container p = contents.getParent();
					if( !(p instanceof JComponent) )
						return;

					lightComp = (JComponent) p;
					oldBorder = lightComp.getBorder();
					oldOpaque = lightComp.isOpaque();
					lightComp.setBorder( createDropShadowBorder() );
					lightComp.setOpaque( false );
					lightComp.setSize( lightComp.getPreferredSize() );
				}
			}
		}

		private DropShadowPopup( DropShadowPopup reusePopup ) {
			super( reusePopup );

			// not necessary to clone fields used for light/medium weight popups

			// heavy weight
			dropShadowDelegate = reusePopup.dropShadowDelegate;
			dropShadowWindow = reusePopup.dropShadowWindow;
			dropShadowPanel2 = reusePopup.dropShadowPanel2;
			oldDropShadowWindowBackground = reusePopup.oldDropShadowWindowBackground;
		}

		@Override
		NonFlashingPopup cloneForReuse() {
			return new DropShadowPopup( this );
		}

		private Border createDropShadowBorder() {
			return new FlatDropShadowBorder(
				UIManager.getColor( "Popup.dropShadowColor" ),
				UIManager.getInsets( "Popup.dropShadowInsets" ),
				FlatUIUtils.getUIFloat( "Popup.dropShadowOpacity", 0.5f ) );
		}

		@Override
		void showImpl() {
			if( dropShadowDelegate != null )
				showPopupAndFixLocation( dropShadowDelegate, dropShadowWindow );

			if( mediumWeightPanel != null )
				showMediumWeightDropShadow();

			super.showImpl();

			// fix location of light weight popup in case it has left or top drop shadow
			if( lightComp != null ) {
				Insets insets = lightComp.getInsets();
				if( insets.left != 0 || insets.top != 0 )
					lightComp.setLocation( lightComp.getX() - insets.left, lightComp.getY() - insets.top );
			}

			if( popupWindow != null ) {
				removeAllPopupWindowComponentListeners();
				popupWindow.addComponentListener( this );
			}
		}

		@Override
		void hideImpl() {
			if( popupWindow != null )
				removeAllPopupWindowComponentListeners();

			if( dropShadowDelegate != null ) {
				dropShadowDelegate.hide();
				dropShadowDelegate = null;
				dropShadowPanel2 = null;
			}

			if( mediumWeightPanel != null ) {
				hideMediumWeightDropShadow();
				dropShadowPanel = null;
				mediumWeightPanel = null;
			}

			super.hideImpl();

			if( dropShadowWindow != null ) {
				dropShadowWindow.setBackground( oldDropShadowWindowBackground );
				dropShadowWindow = null;
			}

			if( lightComp != null ) {
				lightComp.setBorder( oldBorder );
				lightComp.setOpaque( oldOpaque );
				lightComp = null;
			}
		}

		private void showMediumWeightDropShadow() {
			if( mediumWeightShown )
				return;

			mediumWeightShown = true;

			if( owner == null )
				return;

			Window window = SwingUtilities.windowForComponent( owner );
			if( !(window instanceof RootPaneContainer) )
				return;

			dropShadowPanel.setVisible( false );

			JLayeredPane layeredPane = ((RootPaneContainer)window).getLayeredPane();
			layeredPane.add( dropShadowPanel, JLayeredPane.POPUP_LAYER, 0 );

			moveMediumWeightDropShadow();
			resizeMediumWeightDropShadow();

			mediumPanelListener = new ComponentListener() {
				@Override
				public void componentShown( ComponentEvent e ) {
					if( dropShadowPanel != null )
						dropShadowPanel.setVisible( true );
				}

				@Override
				public void componentHidden( ComponentEvent e ) {
					if( dropShadowPanel != null )
						dropShadowPanel.setVisible( false );
				}

				@Override
				public void componentMoved( ComponentEvent e ) {
					moveMediumWeightDropShadow();
				}

				@Override
				public void componentResized( ComponentEvent e ) {
					resizeMediumWeightDropShadow();
				}
			};
			mediumWeightPanel.addComponentListener( mediumPanelListener );
		}

		private void hideMediumWeightDropShadow() {
			mediumWeightPanel.removeComponentListener( mediumPanelListener );

			Container parent = dropShadowPanel.getParent();
			if( parent != null ) {
				Rectangle bounds = dropShadowPanel.getBounds();
				parent.remove( dropShadowPanel );
				parent.repaint( bounds.x, bounds.y, bounds.width, bounds.height );
			}
		}

		private void moveMediumWeightDropShadow() {
			if( dropShadowPanel != null && mediumWeightPanel != null ) {
				Point location = mediumWeightPanel.getLocation();
				Insets insets = dropShadowPanel.getInsets();
				dropShadowPanel.setLocation( location.x - insets.left, location.y - insets.top );
			}
		}

		private void resizeMediumWeightDropShadow() {
			if( dropShadowPanel != null && mediumWeightPanel != null )
				dropShadowPanel.setSize( FlatUIUtils.addInsets( mediumWeightPanel.getSize(), dropShadowPanel.getInsets() ) );
		}

		@Override
		void reset( Component contents, int ownerX, int ownerY ) {
			if( popupWindow != null )
				removeAllPopupWindowComponentListeners();

			super.reset( contents, ownerX, ownerY );

			updateDropShadowWindowBounds();
		}

		private void updateDropShadowWindowBounds() {
			if( dropShadowWindow == null )
				return;

			// calculate size of drop shadow window
			Dimension size = popupWindow.getSize();
			Insets insets = dropShadowPanel2.getInsets();
			int w = size.width + insets.left + insets.right;
			int h = size.height + insets.top + insets.bottom;

			// update drop shadow popup window bounds
			int x = popupWindow.getX() - insets.left;
			int y = popupWindow.getY() - insets.top;
			dropShadowWindow.setBounds( x, y, w, h );
			dropShadowWindow.validate();
		}

		private void removeAllPopupWindowComponentListeners() {
			// make sure that there is no old component listener
			// necessary because this class is cloned if reusing popup windows
			for( ComponentListener l : popupWindow.getComponentListeners() ) {
				if( l instanceof DropShadowPopup )
					popupWindow.removeComponentListener( l );
			}
		}

		//---- interface ComponentListener ----

		@Override
		public void componentResized( ComponentEvent e ) {
			if( e.getSource() == popupWindow )
				updateDropShadowWindowBounds();
		}

		@Override
		public void componentMoved( ComponentEvent e ) {
			if( e.getSource() == popupWindow )
				updateDropShadowWindowBounds();
		}

		@Override public void componentShown( ComponentEvent e ) {}
		@Override public void componentHidden( ComponentEvent e ) {}
	}
}
