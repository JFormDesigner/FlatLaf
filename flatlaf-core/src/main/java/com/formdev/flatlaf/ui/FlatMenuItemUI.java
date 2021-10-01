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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.UnknownStyleException;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JMenuItem}.
 *
 * <!-- BasicMenuItemUI -->
 *
 * @uiDefault MenuItem.font											Font
 * @uiDefault MenuItem.background									Color
 * @uiDefault MenuItem.foreground									Color
 * @uiDefault MenuItem.disabledForeground							Color
 * @uiDefault MenuItem.selectionBackground							Color
 * @uiDefault MenuItem.selectionForeground							Color
 * @uiDefault MenuItem.acceleratorForeground						Color
 * @uiDefault MenuItem.acceleratorSelectionForeground				Color
 * @uiDefault MenuItem.acceleratorFont								Font		defaults to MenuItem.font
 * @uiDefault MenuItem.acceleratorDelimiter							String
 * @uiDefault MenuItem.border										Border
 * @uiDefault MenuItem.borderPainted								boolean
 * @uiDefault MenuItem.margin										Insets
 * @uiDefault MenuItem.arrowIcon									Icon
 * @uiDefault MenuItem.checkIcon									Icon
 * @uiDefault MenuItem.opaque										boolean
 *
 * <!-- FlatMenuItemUI -->
 *
 * @uiDefault MenuItem.iconTextGap									int
 *
 * @author Karl Tauber
 */
public class FlatMenuItemUI
	extends BasicMenuItemUI
	implements StyleableUI
{
	private FlatMenuItemRenderer renderer;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatMenuItemUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installProperty( menuItem, "iconTextGap", FlatUIUtils.getUIInt( "MenuItem.iconTextGap", 4 ) );

		renderer = createRenderer();
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		renderer = null;
		oldStyleValues = null;
	}

	protected FlatMenuItemRenderer createRenderer() {
		return new FlatMenuItemRenderer( menuItem, checkIcon, arrowIcon, acceleratorFont, acceleratorDelimiter );
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener( JComponent c ) {
		return FlatStylingSupport.createPropertyChangeListener( c, this::installStyle, super.createPropertyChangeListener( c ) );
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( menuItem, "MenuItem" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		try {
			return renderer.applyStyleProperty( key, value );
		} catch ( UnknownStyleException ex ) {
			// ignore
		}

		Object oldValue;
		switch( key ) {
			// BasicMenuItemUI
			case "selectionBackground": oldValue = selectionBackground; selectionBackground = (Color) value; return oldValue;
			case "selectionForeground": oldValue = selectionForeground; selectionForeground = (Color) value; return oldValue;
			case "disabledForeground": oldValue = disabledForeground; disabledForeground = (Color) value; return oldValue;
			case "acceleratorForeground": oldValue = acceleratorForeground; acceleratorForeground = (Color) value; return oldValue;
			case "acceleratorSelectionForeground": oldValue = acceleratorSelectionForeground; acceleratorSelectionForeground = (Color) value; return oldValue;
		}

		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, menuItem, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return getStyleableInfos( renderer );
	}

	static Map<String, Class<?>> getStyleableInfos( FlatMenuItemRenderer renderer ) {
		Map<String, Class<?>> infos = new FlatStylingSupport.StyleableInfosMap<>();
		infos.put( "selectionBackground", Color.class );
		infos.put( "selectionForeground", Color.class );
		infos.put( "disabledForeground", Color.class );
		infos.put( "acceleratorForeground", Color.class );
		infos.put( "acceleratorSelectionForeground", Color.class );
		infos.putAll( renderer.getStyleableInfos() );
		return infos;
	}

	@Override
	protected Dimension getPreferredMenuItemSize( JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap ) {
		return renderer.getPreferredMenuItemSize();
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		renderer.paintMenuItem( g, selectionBackground, selectionForeground, disabledForeground,
			acceleratorForeground, acceleratorSelectionForeground );
	}
}
