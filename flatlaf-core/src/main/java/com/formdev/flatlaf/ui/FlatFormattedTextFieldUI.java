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

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JFormattedTextField}.
 *
 * <!-- BasicTextFieldUI -->
 *
 * @uiDefault FormattedTextField.font					Font
 * @uiDefault FormattedTextField.background				Color
 * @uiDefault FormattedTextField.foreground				Color	also used if not editable
 * @uiDefault FormattedTextField.caretForeground		Color
 * @uiDefault FormattedTextField.selectionBackground	Color
 * @uiDefault FormattedTextField.selectionForeground	Color
 * @uiDefault FormattedTextField.disabledBackground		Color	used if not enabled
 * @uiDefault FormattedTextField.inactiveBackground		Color	used if not editable
 * @uiDefault FormattedTextField.inactiveForeground		Color	used if not enabled (yes, this is confusing; this should be named disabledForeground)
 * @uiDefault FormattedTextField.border					Border
 * @uiDefault FormattedTextField.margin					Insets
 * @uiDefault FormattedTextField.caretBlinkRate			int		default is 500 milliseconds
 *
 * <!-- FlatTextFieldUI -->
 *
 * @uiDefault Component.minimumWidth					int
 * @uiDefault FormattedTextField.placeholderForeground	Color
 * @uiDefault FormattedTextField.focusedBackground		Color	optional
 * @uiDefault FormattedTextField.iconTextGap			int		optional, default is 4
 * @uiDefault TextComponent.selectAllOnFocusPolicy		String	never, once (default) or always
 * @uiDefault TextComponent.selectAllOnMouseClick		boolean
 *
 * @author Karl Tauber
 */
public class FlatFormattedTextFieldUI
	extends FlatTextFieldUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatFormattedTextFieldUI();
	}

	@Override
	protected String getPropertyPrefix() {
		return "FormattedTextField";
	}

	/** @since 2 */
	@Override
	String getStyleType() {
		return "FormattedTextField";
	}
}
