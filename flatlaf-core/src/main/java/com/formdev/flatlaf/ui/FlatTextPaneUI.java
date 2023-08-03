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
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.Caret;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTextPane}.
 *
 * <!-- BasicTextPaneUI -->
 *
 * @uiDefault TextPane.font						Font
 * @uiDefault TextPane.background				Color
 * @uiDefault TextPane.foreground				Color	also used if not editable
 * @uiDefault TextPane.caretForeground			Color
 * @uiDefault TextPane.selectionBackground		Color
 * @uiDefault TextPane.selectionForeground		Color
 * @uiDefault TextPane.disabledBackground		Color	used if not enabled
 * @uiDefault TextPane.inactiveBackground		Color	used if not editable
 * @uiDefault TextPane.inactiveForeground		Color	used if not enabled (yes, this is confusing; this should be named disabledForeground)
 * @uiDefault TextPane.border					Border
 * @uiDefault TextPane.margin					Insets
 * @uiDefault TextPane.caretBlinkRate			int		default is 500 milliseconds
 *
 * <!-- FlatTextPaneUI -->
 *
 * @uiDefault Component.minimumWidth			int
 * @uiDefault TextPane.focusedBackground		Color	optional
 *
 * @author Karl Tauber
 */
public class FlatTextPaneUI
	extends BasicTextPaneUI
	implements StyleableUI
{
	@Styleable protected int minimumWidth;
	private Color background;
	@Styleable protected Color disabledBackground;
	@Styleable protected Color inactiveBackground;
	@Styleable protected Color focusedBackground;

	private Color oldDisabledBackground;
	private Color oldInactiveBackground;

	private Insets defaultMargin;

	private Object oldHonorDisplayProperties;
	private FocusListener focusListener;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTextPaneUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		String prefix = getPropertyPrefix();
		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		background = UIManager.getColor( prefix + ".background" );
		disabledBackground = UIManager.getColor( prefix + ".disabledBackground" );
		inactiveBackground = UIManager.getColor( prefix + ".inactiveBackground" );
		focusedBackground = UIManager.getColor( prefix + ".focusedBackground" );

		defaultMargin = UIManager.getInsets( prefix + ".margin" );

		// use component font and foreground for HTML text
		oldHonorDisplayProperties = getComponent().getClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES );
		getComponent().putClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES, true );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		background = null;
		disabledBackground = null;
		inactiveBackground = null;
		focusedBackground = null;

		oldDisabledBackground = null;
		oldInactiveBackground = null;

		oldStyleValues = null;

		getComponent().putClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES, oldHonorDisplayProperties );
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		// necessary to update focus background
		focusListener = new FlatUIUtils.RepaintFocusListener( getComponent(), c -> focusedBackground != null );
		getComponent().addFocusListener( focusListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		getComponent().removeFocusListener( focusListener );
		focusListener = null;
	}

	@Override
	protected Caret createCaret() {
		return new FlatCaret( null, false );
	}

	@Override
	protected void propertyChange( PropertyChangeEvent e ) {
		// invoke updateBackground() before super.propertyChange()
		String propertyName = e.getPropertyName();
		if( "editable".equals( propertyName ) || "enabled".equals( propertyName ) )
			updateBackground();

		super.propertyChange( e );
		FlatEditorPaneUI.propertyChange( getComponent(), e, this::installStyle );
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( getComponent(), "TextPane" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldDisabledBackground = disabledBackground;
		oldInactiveBackground = inactiveBackground;

		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		updateBackground();
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, getComponent(), key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	private void updateBackground() {
		FlatTextFieldUI.updateBackground( getComponent(), background,
			disabledBackground, inactiveBackground,
			oldDisabledBackground, oldInactiveBackground );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return FlatEditorPaneUI.applyMinimumWidth( c, super.getPreferredSize( c ), minimumWidth, defaultMargin );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return FlatEditorPaneUI.applyMinimumWidth( c, super.getMinimumSize( c ), minimumWidth, defaultMargin );
	}

	@Override
	protected void paintSafely( Graphics g ) {
		super.paintSafely( HiDPIUtils.createGraphicsTextYCorrection( (Graphics2D) g ) );
	}

	@Override
	protected void paintBackground( Graphics g ) {
		FlatEditorPaneUI.paintBackground( g, getComponent(), focusedBackground );
	}
}
