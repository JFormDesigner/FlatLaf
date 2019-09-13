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
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JList}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault List.selectionInactiveBackground		Color
 * @uiDefault List.selectionInactiveForeground		Color
 *
 * @author Karl Tauber
 */
public class FlatListUI
	extends BasicListUI
{
	protected Color selectionInactiveBackground;
	protected Color selectionInactiveForeground;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatListUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		selectionInactiveBackground = UIManager.getColor( "List.selectionInactiveBackground" );
		selectionInactiveForeground = UIManager.getColor( "List.selectionInactiveForeground" );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		selectionInactiveBackground = null;
		selectionInactiveForeground = null;
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		if( !list.hasFocus() ) {
			// apply inactive selection background/foreground if list is not focused
			Color oldSelectionBackground = list.getSelectionBackground();
			Color oldSelectionForeground = list.getSelectionForeground();
			list.setSelectionBackground( selectionInactiveBackground );
			list.setSelectionForeground( selectionInactiveForeground );

			super.paint( g, c );

			list.setSelectionBackground( oldSelectionBackground );
			list.setSelectionForeground( oldSelectionForeground );
		} else
			super.paint( g, c );
	}
}
