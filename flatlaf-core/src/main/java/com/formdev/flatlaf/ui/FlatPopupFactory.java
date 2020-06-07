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
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.SystemInfo;

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
		if( !isDropShadowPainted( owner, contents ) )
			return new NonFlashingPopup( super.getPopup( owner, contents, x, y ), contents );

		// macOS and Linux adds drop shadow to heavy weight popups
		if( SystemInfo.IS_MAC || SystemInfo.IS_LINUX ) {
			Popup popup = getHeavyWeightPopup( owner, contents, x, y );
			if( popup == null )
				popup = super.getPopup( owner, contents, x, y );
			return new NonFlashingPopup( popup, contents );
		}

		// create drop shadow popup
		return new DropShadowPopup( super.getPopup( owner, contents, x, y ), owner, contents );
	}

	private boolean isDropShadowPainted( Component owner, Component contents ) {
		Boolean b = isDropShadowPainted( owner );
		if( b != null )
			return b;

		b = isDropShadowPainted( contents );
		if( b != null )
			return b;

		return UIManager.getBoolean( "Popup.dropShadowPainted" );
	}

	private Boolean isDropShadowPainted( Component c ) {
		if( !(c instanceof JComponent) )
			return null;

		Object value = ((JComponent)c).getClientProperty( FlatClientProperties.POPUP_DROP_SHADOW_PAINTED );
		return (value instanceof Boolean ) ? (Boolean) value : null;
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
			if( SystemInfo.IS_JAVA_9_OR_LATER ) {
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

	//---- class NonFlashingPopup ---------------------------------------------

	private class NonFlashingPopup
		extends Popup
	{
		private Popup delegate;

		// heavy weight
		protected Window popupWindow;
		private Color oldPopupWindowBackground;

		NonFlashingPopup( Popup delegate, Component contents ) {
			this.delegate = delegate;

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
			if( delegate != null )
				delegate.show();
		}

		@Override
		public void hide() {
			if( delegate != null ) {
				delegate.hide();
				delegate = null;
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
				dropShadowDelegate = getHeavyWeightPopup( owner, dropShadowPanel, x, y );

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
				dropShadowDelegate.show();

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

			Window window = SwingUtilities.windowForComponent( owner );
			if( window == null )
				return;

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
