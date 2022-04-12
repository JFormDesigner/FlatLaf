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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.JTableHeader;
import com.formdev.flatlaf.ui.FlatTableHeaderUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "ascendingSort" icon for {@link javax.swing.table.JTableHeader}.
 *
 * @uiDefault Component.arrowType				String	chevron (default) or triangle
 * @uiDefault Table.sortIconColor				Color
 *
 * @author Karl Tauber
 */
public class FlatAscendingSortIcon
	extends FlatAbstractIcon
{
	protected boolean chevron = FlatUIUtils.isChevron( UIManager.getString( "Component.arrowType" ) );
	protected Color sortIconColor = UIManager.getColor( "Table.sortIconColor" );

	public FlatAscendingSortIcon() {
		super( 10, 5, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		boolean chevron = this.chevron;
		Color sortIconColor = this.sortIconColor;

		// Because this icon is always shared for all table headers,
		// get icon specific style from FlatTableHeaderUI.
		JTableHeader tableHeader = (JTableHeader) SwingUtilities.getAncestorOfClass( JTableHeader.class, c );
		if( tableHeader != null ) {
			TableHeaderUI ui = tableHeader.getUI();
			if( ui instanceof FlatTableHeaderUI ) {
				FlatTableHeaderUI fui = (FlatTableHeaderUI) ui;
				if( fui.arrowType != null )
					chevron = FlatUIUtils.isChevron( fui.arrowType );
				if( fui.sortIconColor != null )
					sortIconColor = fui.sortIconColor;
			}
		}

		g.setColor( sortIconColor );
		paintArrow( c, g, chevron );
	}

	protected void paintArrow( Component c, Graphics2D g, boolean chevron ) {
		if( chevron ) {
			// chevron arrow
			Path2D path = FlatUIUtils.createPath( false, 1,4, 5,0, 9,4 );
			g.setStroke( new BasicStroke( 1f ) );
			g.draw( path );
		} else {
			// triangle arrow
			g.fill( FlatUIUtils.createPath( 0.5,5, 5,0, 9.5,5 ) );
		}
	}
}
