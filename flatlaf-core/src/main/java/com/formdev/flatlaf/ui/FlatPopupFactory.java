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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import com.formdev.flatlaf.FlatClientProperties;
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
	private Method java8getPopupMethod;
	private Method java9getPopupMethod;

	@Override
	public Popup getPopup( Component owner, Component contents, int x, int y )
		throws IllegalArgumentException
	{
		Point pt = fixToolTipLocation( contents, x, y );
		if( pt != null ) {
			x = pt.x;
			y = pt.y;
		}

		boolean forceHeavyWeight = isOptionEnabled( owner, contents, FlatClientProperties.POPUP_FORCE_HEAVY_WEIGHT, "Popup.forceHeavyWeight" );

		if( !isOptionEnabled( owner, contents, FlatClientProperties.POPUP_DROP_SHADOW_PAINTED, "Popup.dropShadowPainted" ) )
			return new NonFlashingPopup( getPopupForScreenOfOwner( owner, contents, x, y, forceHeavyWeight ), contents );

		// macOS and Linux adds drop shadow to heavy weight popups
		if( SystemInfo.isMacOS || SystemInfo.isLinux )
			return new NonFlashingPopup( getPopupForScreenOfOwner( owner, contents, x, y, true ), contents );

		// create drop shadow popup
		return new DropShadowPopup( getPopupForScreenOfOwner( owner, contents, x, y, forceHeavyWeight ), owner, contents );
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

			// remove contents component from popup window
			if( popupWindow instanceof JWindow )
				((JWindow)popupWindow).getContentPane().removeAll();

			// dispose unused popup
			// (do not invoke popup.hide() because this would cache the popup window)
			popupWindow.dispose();

			// avoid endless loop (should newer happen; PopupFactory cache size is 5)
			if( ++count > 10 )
				return popup;
		}
	}

	/**
	 * Shows the given popup and, if necessary, fixes the location of a heavy weight popup window.
	 * <p>
	 * On a dual screen setup, where screens use different scale factors, it may happen
	 * that the window location changes when showing a heavy weight popup window.
	 * E.g. when opening an dialog on the secondary screen and making combobox popup visible.
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

	private boolean isOptionEnabled( Component owner, Component contents, String clientKey, String uiKey ) {
		if( owner instanceof JComponent ) {
			Boolean b = FlatClientProperties.clientPropertyBooleanStrict( (JComponent) owner, clientKey, null );
			if( b != null )
				return b;
		}

		if( contents instanceof JComponent ) {
			Boolean b = FlatClientProperties.clientPropertyBooleanStrict( (JComponent) contents, clientKey, null );
			if( b != null )
				return b;
		}

		return UIManager.getBoolean( uiKey );
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
				if( java9getPopupMethod == null ) {
					java9getPopupMethod = PopupFactory.class.getDeclaredMethod(
						"getPopup", Component.class, Component.class, int.class, int.class, boolean.class );
				}
				return (Popup) java9getPopupMethod.invoke( this, owner, contents, x, y, true );
			} else {
				// Java 8
				if( java8getPopupMethod == null ) {
					java8getPopupMethod = PopupFactory.class.getDeclaredMethod(
						"getPopup", Component.class, Component.class, int.class, int.class, int.class );
					java8getPopupMethod.setAccessible( true );
				}
				return (Popup) java8getPopupMethod.invoke( this, owner, contents, x, y, /*HEAVY_WEIGHT_POPUP*/ 2 );
			}
		} catch( NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException ex ) {
			// ignore
			return null;
		}
	}

	/**
	 * Usually ToolTipManager places a tooltip at (mouseLocation.x, mouseLocation.y + 20).
	 * In case that the tooltip would be partly outside of the screen,
	 * ToolTipManagerthe changes the location so that the entire tooltip fits on screen.
	 * But this can place the tooltip under the mouse location and hide the owner component.
	 * <p>
	 * This method checks whether the current mouse location is within tooltip bounds
	 * and corrects the y-location so that the tooltip is placed above the mouse location.
	 */
	private Point fixToolTipLocation( Component contents, int x, int y ) {
		if( !(contents instanceof JToolTip) || !wasInvokedFromToolTipManager() )
			return null;

		Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
		Dimension tipSize = contents.getPreferredSize();

		// check whether mouse location is within tooltip bounds
		Rectangle tipBounds = new Rectangle( x, y, tipSize.width, tipSize.height );
		if( !tipBounds.contains( mouseLocation ) )
			return null;

		// place tooltip above mouse location
		return new Point( x, mouseLocation.y - tipSize.height - UIScale.scale( 20 ) );
	}

	private boolean wasInvokedFromToolTipManager() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for( StackTraceElement stackTraceElement : stackTrace ) {
			if( "javax.swing.ToolTipManager".equals( stackTraceElement.getClassName() ) &&
				"showTipWindow".equals( stackTraceElement.getMethodName() ) )
			  return true;
		}
		return false;
	}

	//---- class NonFlashingPopup ---------------------------------------------

	private class NonFlashingPopup
		extends Popup
	{
		private Popup delegate;
		private Component contents;

		// heavy weight
		protected Window popupWindow;
		private Color oldPopupWindowBackground;

		NonFlashingPopup( Popup delegate, Component contents ) {
			this.delegate = delegate;
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

		@Override
		public void show() {
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
		public void hide() {
			if( delegate != null ) {
				delegate.hide();
				delegate = null;
				contents = null;
			}

			if( popupWindow != null ) {
				// restore background so that it can not affect other LaFs (when switching)
				// because popup windows are cached and reused
				popupWindow.setBackground( oldPopupWindowBackground );
				popupWindow = null;
			}
		}
	}

	//---- class DropShadowPopup ----------------------------------------------

	private class DropShadowPopup
		extends NonFlashingPopup
	{
		private final Component owner;

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
		private Color oldDropShadowWindowBackground;

		DropShadowPopup( Popup delegate, Component owner, Component contents ) {
			super( delegate, contents );
			this.owner = owner;

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
				JPanel dropShadowPanel = new JPanel();
				dropShadowPanel.setBorder( createDropShadowBorder() );
				dropShadowPanel.setOpaque( false );

				// set preferred size of drop shadow panel
				Dimension prefSize = popupWindow.getPreferredSize();
				Insets insets = dropShadowPanel.getInsets();
				dropShadowPanel.setPreferredSize( new Dimension(
					prefSize.width + insets.left + insets.right,
					prefSize.height + insets.top + insets.bottom ) );

				// create heavy weight popup for drop shadow
				int x = popupWindow.getX() - insets.left;
				int y = popupWindow.getY() - insets.top;
				dropShadowDelegate = getPopupForScreenOfOwner( owner, dropShadowPanel, x, y, true );

				// make drop shadow popup window translucent
				dropShadowWindow = SwingUtilities.windowForComponent( dropShadowPanel );
				if( dropShadowWindow != null ) {
					oldDropShadowWindowBackground = dropShadowWindow.getBackground();
					dropShadowWindow.setBackground( new Color( 0, true ) );
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

		private Border createDropShadowBorder() {
			return new FlatDropShadowBorder(
				UIManager.getColor( "Popup.dropShadowColor" ),
				UIManager.getInsets( "Popup.dropShadowInsets" ),
				FlatUIUtils.getUIFloat( "Popup.dropShadowOpacity", 0.5f ) );
		}

		@Override
		public void show() {
			if( dropShadowDelegate != null )
				showPopupAndFixLocation( dropShadowDelegate, dropShadowWindow );

			if( mediumWeightPanel != null )
				showMediumWeightDropShadow();

			super.show();

			// fix location of light weight popup in case it has left or top drop shadow
			if( lightComp != null ) {
				Insets insets = lightComp.getInsets();
				if( insets.left != 0 || insets.top != 0 )
					lightComp.setLocation( lightComp.getX() - insets.left, lightComp.getY() - insets.top );
			}
		}

		@Override
		public void hide() {
			if( dropShadowDelegate != null ) {
				dropShadowDelegate.hide();
				dropShadowDelegate = null;
			}

			if( mediumWeightPanel != null ) {
				hideMediumWeightDropShadow();
				dropShadowPanel = null;
				mediumWeightPanel = null;
			}

			super.hide();

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
					if( dropShadowPanel != null && mediumWeightPanel != null ) {
						Point location = mediumWeightPanel.getLocation();
						Insets insets = dropShadowPanel.getInsets();
						dropShadowPanel.setLocation( location.x - insets.left, location.y - insets.top );
					}
				}

				@Override
				public void componentResized( ComponentEvent e ) {
					if( dropShadowPanel != null )
						dropShadowPanel.setSize( FlatUIUtils.addInsets( mediumWeightPanel.getSize(), dropShadowPanel.getInsets() ) );
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
	}
}
