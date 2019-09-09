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

package com.formdev.flatlaf.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "descendingSort" icon for {@link javax.swing.table.JTableHeader}.
 *
 * @uiDefault Table.sortIconColor				Color
 *
 * @author Karl Tauber
 */
public class FlatDescendingSortIcon
	extends FlatAbstractIcon
{
	protected final Color sortIconColor = UIManager.getColor( "Table.sortIconColor" );

	public FlatDescendingSortIcon() {
		super( 10, 5, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		g.setColor( sortIconColor );
		g.fill( FlatUIUtils.createPath( 0.5,0, 9.5,0, 5,5 ) );
	}
}
