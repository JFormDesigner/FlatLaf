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

import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeListener;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableField;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableLookupProvider;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JRadioButtonMenuItem}.
 *
 * <!-- BasicRadioButtonMenuItemUI -->
 *
 * @uiDefault RadioButtonMenuItem.font								Font
 * @uiDefault RadioButtonMenuItem.background						Color
 * @uiDefault RadioButtonMenuItem.foreground						Color
 * @uiDefault RadioButtonMenuItem.disabledForeground				Color
 * @uiDefault RadioButtonMenuItem.selectionBackground				Color
 * @uiDefault RadioButtonMenuItem.selectionForeground				Color
 * @uiDefault RadioButtonMenuItem.acceleratorForeground				Color
 * @uiDefault RadioButtonMenuItem.acceleratorSelectionForeground	Color
 * @uiDefault MenuItem.acceleratorFont								Font		defaults to MenuItem.font
 * @uiDefault MenuItem.acceleratorDelimiter							String
 * @uiDefault RadioButtonMenuItem.border							Border
 * @uiDefault RadioButtonMenuItem.borderPainted						boolean
 * @uiDefault RadioButtonMenuItem.margin							Insets
 * @uiDefault RadioButtonMenuItem.arrowIcon							Icon
 * @uiDefault RadioButtonMenuItem.checkIcon							Icon
 * @uiDefault RadioButtonMenuItem.opaque							boolean
 *
 * <!-- FlatRadioButtonMenuItemUI -->
 *
 * @uiDefault MenuItem.iconTextGap									int
 *
 * @author Karl Tauber
 */
@StyleableField( cls=BasicMenuItemUI.class, key="selectionBackground" )
@StyleableField( cls=BasicMenuItemUI.class, key="selectionForeground" )
@StyleableField( cls=BasicMenuItemUI.class, key="disabledForeground" )
@StyleableField( cls=BasicMenuItemUI.class, key="acceleratorForeground" )
@StyleableField( cls=BasicMenuItemUI.class, key="acceleratorSelectionForeground" )

public class FlatRadioButtonMenuItemUI
	extends BasicRadioButtonMenuItemUI
	implements StyleableUI, StyleableLookupProvider
{
	private FlatMenuItemRenderer renderer;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatRadioButtonMenuItemUI();
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

		FlatMenuItemRenderer.clearClientProperties( menuItem.getParent() );
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
			applyStyle( FlatStylingSupport.getResolvedStyle( menuItem, "RadioButtonMenuItem" ) );
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
		return FlatMenuItemUI.applyStyleProperty( menuItem, this, renderer, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatMenuItemUI.getStyleableInfos( this, renderer );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatMenuItemUI.getStyleableValue( this, renderer, key );
	}

	/** @since 2.5 */
	@Override
	public MethodHandles.Lookup getLookupForStyling() {
		// MethodHandles.lookup() is caller sensitive and must be invoked in this class,
		// otherwise it is not possible to access protected fields in JRE superclass
		return MethodHandles.lookup();
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
