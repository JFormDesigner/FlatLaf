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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JEditorPane}.
 *
 * <!-- BasicEditorPaneUI -->
 *
 * @uiDefault EditorPane.font					Font
 * @uiDefault EditorPane.background				Color	also used if not editable
 * @uiDefault EditorPane.foreground				Color
 * @uiDefault EditorPane.caretForeground		Color
 * @uiDefault EditorPane.selectionBackground	Color
 * @uiDefault EditorPane.selectionForeground	Color
 * @uiDefault EditorPane.disabledBackground		Color	used if not enabled
 * @uiDefault EditorPane.inactiveBackground		Color	used if not editable
 * @uiDefault EditorPane.inactiveForeground		Color	used if not enabled (yes, this is confusing; this should be named disabledForeground)
 * @uiDefault EditorPane.border					Border
 * @uiDefault EditorPane.margin					Insets
 * @uiDefault EditorPane.caretBlinkRate			int		default is 500 milliseconds
 *
 * <!-- FlatEditorPaneUI -->
 *
 * @uiDefault Component.minimumWidth			int
 * @uiDefault Component.isIntelliJTheme			boolean
 * @uiDefault EditorPane.focusedBackground		Color	optional
 *
 * @author Karl Tauber
 */
public class FlatEditorPaneUI
	extends BasicEditorPaneUI
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

	private Object oldHonorDisplayProperties;
	private FocusListener focusListener;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatEditorPaneUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		applyStyle( FlatStylingSupport.getStyle( c ) );
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		String prefix = getPropertyPrefix();
		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		isIntelliJTheme = UIManager.getBoolean( "Component.isIntelliJTheme" );
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
	protected void propertyChange( PropertyChangeEvent e ) {
		// invoke updateBackground() before super.propertyChange()
		String propertyName = e.getPropertyName();
		if( "editable".equals( propertyName ) || "enabled".equals( propertyName ) )
			updateBackground();

		super.propertyChange( e );
		propertyChange( getComponent(), e, this::applyStyle );
	}

	static void propertyChange( JTextComponent c, PropertyChangeEvent e, Consumer<Object> applyStyle ) {
		switch( e.getPropertyName() ) {
			case FlatClientProperties.MINIMUM_WIDTH:
				c.revalidate();
				break;

			case FlatClientProperties.STYLE:
				applyStyle.accept( e.getNewValue() );
				c.revalidate();
				c.repaint();
				break;
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

	private void updateBackground() {
		FlatTextFieldUI.updateBackground( getComponent(), background,
			disabledBackground, inactiveBackground,
			oldDisabledBackground, oldInactiveBackground );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return applyMinimumWidth( c, super.getPreferredSize( c ), minimumWidth, defaultMargin );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return applyMinimumWidth( c, super.getMinimumSize( c ), minimumWidth, defaultMargin );
	}

	static Dimension applyMinimumWidth( JComponent c, Dimension size, int minimumWidth, Insets defaultMargin ) {
		// do not apply minimum width if JTextComponent.margin is set
		if( !FlatTextFieldUI.hasDefaultMargins( c, defaultMargin ) )
			return size;

		// Assume that text area is in a scroll pane (that displays the border)
		// and subtract 1px border line width.
		// Using "(scale( 1 ) * 2)" instead of "scale( 2 )" to deal with rounding
		// issues. E.g. at scale factor 1.5 the first returns 4, but the second 3.
		minimumWidth = FlatUIUtils.minimumWidth( c, minimumWidth );
		size.width = Math.max( size.width, scale( minimumWidth ) - (scale( 1 ) * 2) );
		return size;
	}

	@Override
	protected void paintSafely( Graphics g ) {
		super.paintSafely( HiDPIUtils.createGraphicsTextYCorrection( (Graphics2D) g ) );
	}

	@Override
	protected void paintBackground( Graphics g ) {
		paintBackground( g, getComponent(), isIntelliJTheme, focusedBackground );
	}

	static void paintBackground( Graphics g, JTextComponent c, boolean isIntelliJTheme, Color focusedBackground ) {
		g.setColor( FlatTextFieldUI.getBackground( c, isIntelliJTheme, focusedBackground ) );
		g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
	}
}
