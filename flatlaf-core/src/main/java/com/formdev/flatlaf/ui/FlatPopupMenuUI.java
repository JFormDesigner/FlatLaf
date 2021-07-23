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

import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStyleSupport.StyleableUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPopupMenu}.
 *
 * <!-- BasicPopupMenuUI -->
 *
 * @uiDefault PopupMenu.font							Font
 * @uiDefault PopupMenu.background						Color
 * @uiDefault PopupMenu.foreground						Color
 * @uiDefault PopupMenu.border							Border
 *
 * @author Karl Tauber
 */
public class FlatPopupMenuUI
	extends BasicPopupMenuUI
	implements StyleableUI
{
	private PropertyChangeListener propertyChangeListener;
	private Map<String, Object> oldStyleValues;
	private AtomicBoolean borderShared;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatPopupMenuUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		applyStyle( FlatStyleSupport.getStyle( c ) );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		oldStyleValues = null;
		borderShared = null;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		propertyChangeListener = FlatStyleSupport.createPropertyChangeListener( popupMenu, this::applyStyle, null );
		popupMenu.addPropertyChangeListener( FlatClientProperties.STYLE, propertyChangeListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		popupMenu.removePropertyChangeListener( FlatClientProperties.STYLE, propertyChangeListener );
		propertyChangeListener = null;
	}

	/**
	 * @since TODO
	 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStyleSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
	}

	/**
	 * @since TODO
	 */
	protected Object applyStyleProperty( String key, Object value ) {
		if( borderShared == null )
			borderShared = new AtomicBoolean( true );
		return FlatStyleSupport.applyToAnnotatedObjectOrBorder( this, key, value, popupMenu, borderShared );
	}

	/**
	 * @since TODO
	 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStyleSupport.getAnnotatedStyleableInfos( this, popupMenu.getBorder() );
	}
}
