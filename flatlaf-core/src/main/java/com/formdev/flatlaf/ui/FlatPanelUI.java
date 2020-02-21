/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
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
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPanel}.
 *
 * <!-- BasicPanelUI -->
 *
 * @uiDefault Panel.font				Font	unused
 * @uiDefault Panel.background			Color	only used if opaque
 * @uiDefault Panel.foreground			Color
 * @uiDefault Panel.border				Border
 *
 * @author Karl Tauber
 */
public class FlatPanelUI
	extends BasicPanelUI
{
	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatPanelUI();
		return instance;
	}
}
