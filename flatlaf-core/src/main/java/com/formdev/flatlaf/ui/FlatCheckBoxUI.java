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
 * Provides the Flat LaF UI delegate for {@link javax.swing.JCheckBox}.
 *
 * <!-- BasicRadioButtonUI -->
 *
 * @uiDefault CheckBox.font						Font
 * @uiDefault CheckBox.background				Color
 * @uiDefault CheckBox.foreground				Color
 * @uiDefault CheckBox.border					Border
 * @uiDefault CheckBox.margin					Insets
 * @uiDefault CheckBox.rollover					boolean
 * @uiDefault CheckBox.icon						Icon
 *
 * <!-- FlatRadioButtonUI -->
 *
 * @uiDefault CheckBox.iconTextGap				int
 * @uiDefault CheckBox.disabledText				Color
 *
 * @author Karl Tauber
 */
public class FlatCheckBoxUI
	extends FlatRadioButtonUI
{
	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c ) && !FlatUIUtils.needsLightAWTPeer( c )
			? FlatUIUtils.createSharedUI( FlatCheckBoxUI.class, () -> new FlatCheckBoxUI( true ) )
			: new FlatCheckBoxUI( false );
	}

	/** @since 2 */
	protected FlatCheckBoxUI( boolean shared ) {
		super( shared );
	}

	@Override
	public String getPropertyPrefix() {
		return "CheckBox.";
	}

	/** @since 2 */
	@Override
	String getStyleType() {
		return "CheckBox";
	}
}
