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
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTable}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault Table.rowHeight						int
 * @uiDefault Table.selectionInactiveBackground		Color
 * @uiDefault Table.selectionInactiveForeground		Color
 *
 * @author Karl Tauber
 */
public class FlatTableUI
	extends BasicTableUI
{
	protected Color selectionInactiveBackground;
	protected Color selectionInactiveForeground;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTableUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		selectionInactiveBackground = UIManager.getColor( "Table.selectionInactiveBackground" );
		selectionInactiveForeground = UIManager.getColor( "Table.selectionInactiveForeground" );

		int rowHeight = FlatUIUtils.getUIInt( "Table.rowHeight", 16 );
		if( rowHeight > 0 )
			LookAndFeel.installProperty( table, "rowHeight", UIScale.scale( rowHeight ) );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		selectionInactiveBackground = null;
		selectionInactiveForeground = null;
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		if( !table.hasFocus() ) {
			// apply inactive selection background/foreground if table is not focused
			Color oldSelectionBackground = table.getSelectionBackground();
			Color oldSelectionForeground = table.getSelectionForeground();
			table.setSelectionBackground( selectionInactiveBackground );
			table.setSelectionForeground( selectionInactiveForeground );

			super.paint( g, c );

			table.setSelectionBackground( oldSelectionBackground );
			table.setSelectionForeground( oldSelectionForeground );
		} else
			super.paint( g, c );
	}
}
