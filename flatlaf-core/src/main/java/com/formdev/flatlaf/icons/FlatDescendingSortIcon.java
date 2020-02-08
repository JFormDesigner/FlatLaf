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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "descendingSort" icon for {@link javax.swing.table.JTableHeader}.
 *
 * @uiDefault Component.arrowType				String	triangle (default) or chevron
 * @uiDefault Table.sortIconColor				Color
 *
 * @author Karl Tauber
 */
public class FlatDescendingSortIcon
	extends FlatAbstractIcon
{
	protected final boolean chevron = "chevron".equals( UIManager.getString( "Component.arrowType" ) );
	protected final Color sortIconColor = UIManager.getColor( "Table.sortIconColor" );

	public FlatDescendingSortIcon() {
		super( 10, 5, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		g.setColor( sortIconColor );
		if( chevron ) {
			// chevron arrow
			Path2D path = FlatUIUtils.createPath( false, 1,0, 5,4, 9,0 );
			g.setStroke( new BasicStroke( 1f ) );
			g.draw( path );
		} else {
			// triangle arrow
			g.fill( FlatUIUtils.createPath( 0.5,0, 5,5, 9.5,0 ) );
		}
	}
}
