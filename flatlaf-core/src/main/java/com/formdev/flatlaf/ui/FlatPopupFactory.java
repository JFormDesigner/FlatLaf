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
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * A popup factory that adds drop shadows to popups on Windows and Linux.
 * On macOS, heavy weight popups (without drop shadow) are produced and the
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
		if( !UIManager.getBoolean( "Popup.dropShadowPainted" ) && !SystemInfo.IS_MAC )
			return super.getPopup( owner, contents, x, y );

		// always use heavy weight popup because the drop shadow increases
		// the popup size and may overlap the window bounds
		Popup popup = getHeavyWeightPopup( owner, contents, x, y );

		// failed to get heavy weight popup --> do not add drop shadow
		if( popup == null )
			return super.getPopup( owner, contents, x, y );

		// macOS adds drop shadow to heavy weight popups
		if( SystemInfo.IS_MAC )
			return popup;

		// create drop shadow popup
		return new DropShadowPopup( popup, contents );
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

	//---- class DropShadowPopup ----------------------------------------------

	private static class DropShadowPopup
		extends Popup
	{
		private Popup delegate;

		private JComponent parent;
		private Border oldBorder;
		private boolean oldOpaque;

		private Window window;
		private Color oldBackground;

		DropShadowPopup( Popup delegate, Component contents ) {
			this.delegate = delegate;

			if( delegate.getClass().getName().endsWith( "MediumWeightPopup" ) )
				return;

			Dimension size = contents.getPreferredSize();
			if( size.width <= 0 || size.height <= 0 )
				return;

			Container p = contents.getParent();
			if( !(p instanceof JComponent) )
				return;

			parent = (JComponent) p;
			oldBorder = parent.getBorder();
			oldOpaque = parent.isOpaque();
			parent.setBorder( new FlatDropShadowBorder(
				UIManager.getColor( "Popup.dropShadowColor" ),
				UIManager.getInsets( "Popup.dropShadowInsets" ),
				FlatUIUtils.getUIFloat( "Popup.dropShadowOpacity", 0.5f ) ) );
			parent.setOpaque( false );

			window = SwingUtilities.windowForComponent( contents );
			if( window != null ) {
				oldBackground = window.getBackground();
				parent.setBorder( new FillBackgroundBorder( parent.getBorder(), oldBackground ) );
				window.setBackground( new Color( 0, true ) );
				window.setSize( window.getPreferredSize() );
			} else
				parent.setSize( parent.getPreferredSize() );
		}

		@Override
		public void show() {
			delegate.show();
		}

		@Override
		public void hide() {
			if( delegate == null )
				return;

			delegate.hide();

			if( parent != null ) {
				parent.setBorder( oldBorder );
				parent.setOpaque( oldOpaque );
				parent = null;
			}

			if( window != null ) {
				window.setBackground( oldBackground );
				window = null;
			}

			delegate = null;
		}
	}

	//---- class FillBackgroundBorder -----------------------------------------

	/**
	 * Fills the component background with the given color (and delegates border painting).
	 * This avoids that underlying windows may shine thru which may happen because
	 * the heavy weight popup window is transparent and the contained panel is not opaque.
	 */
	private static class FillBackgroundBorder
		implements Border
	{
		private final Border delegate;
		private final Color background;

		FillBackgroundBorder( Border delegate, Color background ) {
			this.delegate = delegate;
			this.background = background;
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			Insets insets = getBorderInsets( c );
			Color oldColor = g.getColor();
			g.setColor( background );
			g.fillRect( x + insets.left, y + insets.top,
				width - insets.left - insets.right, height - insets.top - insets.bottom );

			// restore color because delegate border may use it
			g.setColor( oldColor );

			delegate.paintBorder( c, g, x, y, width, height );
		}

		@Override
		public Insets getBorderInsets( Component c ) {
			return delegate.getBorderInsets( c );
		}

		@Override
		public boolean isBorderOpaque() {
			return delegate.isBorderOpaque();
		}
	}
}
