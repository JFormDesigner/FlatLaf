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
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.Caret;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTextArea}.
 *
 * <!-- BasicTextAreaUI -->
 *
 * @uiDefault TextArea.font						Font
 * @uiDefault TextArea.background				Color
 * @uiDefault TextArea.foreground				Color	also used if not editable
 * @uiDefault TextArea.caretForeground			Color
 * @uiDefault TextArea.selectionBackground		Color
 * @uiDefault TextArea.selectionForeground		Color
 * @uiDefault TextArea.inactiveForeground		Color	used if not enabled (yes, this is confusing; this should be named disabledForeground)
 * @uiDefault TextArea.border					Border
 * @uiDefault TextArea.margin					Insets
 * @uiDefault TextArea.caretBlinkRate			int		default is 500 milliseconds
 *
 * <!-- FlatTextAreaUI -->
 *
 * @uiDefault Component.minimumWidth			int
 * @uiDefault Component.isIntelliJTheme			boolean
 * @uiDefault TextArea.disabledBackground		Color	used if not enabled
 * @uiDefault TextArea.inactiveBackground		Color	used if not editable
 * @uiDefault TextArea.focusedBackground		Color	optional
 *
 * @author Karl Tauber
 */
public class FlatTextAreaUI
	extends BasicTextAreaUI
	implements StyleableUI
{
	@Styleable protected int minimumWidth;
	protected boolean isIntelliJTheme;
	private Color background;
	@Styleable protected Color disabledBackground;
	@Styleable protected Color inactiveBackground;
	@Styleable protected Color focusedBackground;

	private Color oldDisabledBackground;
	private Color oldInactiveBackground;

	private Insets defaultMargin;

	private FocusListener focusListener;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTextAreaUI();
	}

	@Override
	public void installUI( JComponent c ) {
		if( FlatUIUtils.needsLightAWTPeer( c ) )
			FlatUIUtils.runWithLightAWTPeerUIDefaults( () -> installUIImpl( c ) );
		else
			installUIImpl( c );
	}

	private void installUIImpl( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		isIntelliJTheme = UIManager.getBoolean( "Component.isIntelliJTheme" );
		background = UIManager.getColor( "TextArea.background" );
		disabledBackground = UIManager.getColor( "TextArea.disabledBackground" );
		inactiveBackground = UIManager.getColor( "TextArea.inactiveBackground" );
		focusedBackground = UIManager.getColor( "TextArea.focusedBackground" );

		defaultMargin = UIManager.getInsets( "TextArea.margin" );
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
			applyStyle( FlatStylingSupport.getResolvedStyle( getComponent(), "TextArea" ) );
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
		return applyMinimumWidth( c, super.getPreferredSize( c ) );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return applyMinimumWidth( c, super.getMinimumSize( c ) );
	}

	private Dimension applyMinimumWidth( JComponent c, Dimension size ) {
		// do not apply minimum width if JTextArea.columns is set
		if( c instanceof JTextArea && ((JTextArea)c).getColumns() > 0 )
			return size;

		return FlatEditorPaneUI.applyMinimumWidth( c, size, minimumWidth, defaultMargin );
	}

	@Override
	protected void paintSafely( Graphics g ) {
		super.paintSafely( HiDPIUtils.createGraphicsTextYCorrection( (Graphics2D) g ) );
	}

	@Override
	protected void paintBackground( Graphics g ) {
		FlatEditorPaneUI.paintBackground( g, getComponent(), isIntelliJTheme, focusedBackground );
	}
}
