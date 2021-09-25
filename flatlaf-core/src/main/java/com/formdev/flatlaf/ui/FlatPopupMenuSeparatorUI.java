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
 * @uiDefault Separator.background				Color	unused
 * @uiDefault Separator.foreground				Color
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
	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatPopupMenuSeparatorUI.class, () -> new FlatPopupMenuSeparatorUI( true ) )
			: new FlatPopupMenuSeparatorUI( false );
	}

	/** @since 2 */
	protected FlatPopupMenuSeparatorUI( boolean shared ) {
		super( shared );
	}

	@Override
	protected String getPropertyPrefix() {
		return "PopupMenuSeparator";
	}

	/** @since 2 */
	@Override
	String getStyleType() {
		return "PopupMenuSeparator";
	}
}
