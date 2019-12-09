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
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPopupMenu.Separator}.
 *
 * <!-- BasicSeparatorUI -->
 *
 * @uiDefault PopupMenuSeparator.background		Color	unused
 * @uiDefault PopupMenuSeparator.foreground		Color
 *
 * <!-- FlatSeparatorUI -->
 *
 * @uiDefault PopupMenuSeparator.height			int		height (or width) of the component; may be larger than stripe
 * @uiDefault PopupMenuSeparator.stripeWidth	int		width of the stripe
 * @uiDefault PopupMenuSeparator.stripeIndent	int		indent of stripe from top (or left); allows positioning of stripe within component
 *
 * @author Karl Tauber
 */
public class FlatPopupMenuSeparatorUI
	extends FlatSeparatorUI
{
	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatPopupMenuSeparatorUI();
		return instance;
	}

	@Override
	protected String getPropertyPrefix() {
		return "PopupMenuSeparator";
	}
}
