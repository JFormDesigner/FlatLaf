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
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPasswordField}.
 *
 * <!-- BasicPasswordFieldUI -->
 *
 * @uiDefault PasswordField.font					Font
 * @uiDefault PasswordField.background				Color
 * @uiDefault PasswordField.foreground				Color	also used if not editable
 * @uiDefault PasswordField.caretForeground			Color
 * @uiDefault PasswordField.selectionBackground		Color
 * @uiDefault PasswordField.selectionForeground		Color
 * @uiDefault PasswordField.disabledBackground		Color	used if not enabled
 * @uiDefault PasswordField.inactiveBackground		Color	used if not editable
 * @uiDefault PasswordField.inactiveForeground		Color	used if not enabled (yes, this is confusing; this should be named disabledForeground)
 * @uiDefault PasswordField.border					Border
 * @uiDefault PasswordField.margin					Insets
 * @uiDefault PasswordField.echoChar				character
 * @uiDefault PasswordField.caretBlinkRate			int		default is 500 milliseconds
 *
 * <!-- FlatPasswordFieldUI -->
 *
 * @uiDefault Component.focusWidth					int
 * @uiDefault Component.minimumWidth				int
 * @uiDefault Component.isIntelliJTheme				boolean
 * @uiDefault PasswordField.placeholderForeground	Color
 *
 * @author Karl Tauber
 */
public class FlatPasswordFieldUI
	extends BasicPasswordFieldUI
{
	protected int focusWidth;
	protected int minimumWidth;
	protected boolean isIntelliJTheme;
	protected Color placeholderForeground;

	private FocusListener focusListener;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatPasswordFieldUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		// use other echoChar on Mac because the default is too large in SF font
		if( SystemInfo.IS_MAC )
			LookAndFeel.installProperty( getComponent(), "echoChar", '\u2022' );

		String prefix = getPropertyPrefix();
		focusWidth = UIManager.getInt( "Component.focusWidth" );
		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		isIntelliJTheme = UIManager.getBoolean( "Component.isIntelliJTheme" );
		placeholderForeground = UIManager.getColor( prefix + ".placeholderForeground" );

		LookAndFeel.installProperty( getComponent(), "opaque", focusWidth == 0 );

		MigLayoutVisualPadding.install( getComponent(), focusWidth );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		placeholderForeground = null;

		MigLayoutVisualPadding.uninstall( getComponent() );
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		focusListener = new FlatUIUtils.RepaintFocusListener( getComponent() );
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

		if( FlatClientProperties.PLACEHOLDER_TEXT.equals( e.getPropertyName() ) )
			getComponent().repaint();
	}

	@Override
	protected void paintSafely( Graphics g ) {
		FlatTextFieldUI.paintBackground( g, getComponent(), focusWidth, isIntelliJTheme );
		FlatTextFieldUI.paintPlaceholder( g, getComponent(), placeholderForeground );
		super.paintSafely( g );
	}

	@Override
	protected void paintBackground( Graphics g ) {
		// background is painted elsewhere
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return applyMinimumWidth( super.getPreferredSize( c ), c );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return applyMinimumWidth( super.getMinimumSize( c ), c );
	}

	private Dimension applyMinimumWidth( Dimension size, JComponent c ) {
		int minimumWidth = FlatUIUtils.minimumWidth( getComponent(), this.minimumWidth );
		int focusWidth = (c.getBorder() instanceof FlatBorder) ? this.focusWidth : 0;
		size.width = Math.max( size.width, scale( minimumWidth + (focusWidth * 2) ) );
		return size;
	}
}
