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

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JCheckBoxMenuItem}.
 *
 * <!-- BasicCheckBoxMenuItemUI -->
 *
 * @uiDefault CheckBoxMenuItem.font									Font
 * @uiDefault CheckBoxMenuItem.background							Color
 * @uiDefault CheckBoxMenuItem.foreground							Color
 * @uiDefault CheckBoxMenuItem.disabledForeground					Color
 * @uiDefault CheckBoxMenuItem.selectionBackground					Color
 * @uiDefault CheckBoxMenuItem.selectionForeground					Color
 * @uiDefault CheckBoxMenuItem.acceleratorForeground				Color
 * @uiDefault CheckBoxMenuItem.acceleratorSelectionForeground		Color
 * @uiDefault MenuItem.acceleratorFont								Font		defaults to MenuItem.font
 * @uiDefault MenuItem.acceleratorDelimiter							String
 * @uiDefault CheckBoxMenuItem.border								Border
 * @uiDefault CheckBoxMenuItem.borderPainted						boolean
 * @uiDefault CheckBoxMenuItem.margin								Insets
 * @uiDefault CheckBoxMenuItem.arrowIcon							Icon
 * @uiDefault CheckBoxMenuItem.checkIcon							Icon
 * @uiDefault CheckBoxMenuItem.opaque								boolean
 *
 * <!-- FlatCheckBoxMenuItemUI -->
 *
 * @uiDefault MenuItem.iconTextGap									int
 *
 * @author Karl Tauber
 */
public class FlatCheckBoxMenuItemUI
	extends BasicCheckBoxMenuItemUI
{
	private FlatMenuItemRenderer renderer;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatCheckBoxMenuItemUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installProperty( menuItem, "iconTextGap", FlatUIUtils.getUIInt( "MenuItem.iconTextGap", 4 ) );

		renderer = createRenderer();
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		renderer = null;
	}

	protected FlatMenuItemRenderer createRenderer() {
		return new FlatMenuItemRenderer( menuItem, checkIcon, arrowIcon, acceleratorFont, acceleratorDelimiter );
	}

	@Override
	protected Dimension getPreferredMenuItemSize( JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap ) {
		return renderer.getPreferredMenuItemSize();
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		renderer.paintMenuItem( g, selectionBackground, selectionForeground, disabledForeground,
			acceleratorForeground, acceleratorSelectionForeground );
	}
}
