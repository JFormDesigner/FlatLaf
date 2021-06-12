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
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextPaneUI;
import com.formdev.flatlaf.util.HiDPIUtils;

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
 * @uiDefault Component.isIntelliJTheme			boolean
 * @uiDefault TextPane.focusedBackground		Color	optional
 *
 * @author Karl Tauber
 */
public class FlatTextPaneUI
	extends BasicTextPaneUI
{
	protected int minimumWidth;
	protected boolean isIntelliJTheme;
	protected Color focusedBackground;

	private Object oldHonorDisplayProperties;
	private FocusListener focusListener;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTextPaneUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		String prefix = getPropertyPrefix();
		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		isIntelliJTheme = UIManager.getBoolean( "Component.isIntelliJTheme" );
		focusedBackground = UIManager.getColor( prefix + ".focusedBackground" );

		// use component font and foreground for HTML text
		oldHonorDisplayProperties = getComponent().getClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES );
		getComponent().putClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES, true );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		focusedBackground = null;

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
		super.propertyChange( e );
		FlatEditorPaneUI.propertyChange( getComponent(), e );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return FlatEditorPaneUI.applyMinimumWidth( c, super.getPreferredSize( c ), minimumWidth );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return FlatEditorPaneUI.applyMinimumWidth( c, super.getMinimumSize( c ), minimumWidth );
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
