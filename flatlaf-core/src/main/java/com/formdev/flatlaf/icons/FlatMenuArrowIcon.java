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
import javax.swing.JMenu;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "arrow" icon for {@link javax.swing.JMenu}.
 *
 * @uiDefault Component.arrowType				String	triangle (default) or chevron
 * @uiDefault Menu.icon.arrowColor				Color
 * @uiDefault Menu.icon.disabledArrowColor		Color
 * @uiDefault Menu.selectionForeground			Color
 * @uiDefault MenuItem.selectionType			String
 *
 * @author Karl Tauber
 */
public class FlatMenuArrowIcon
	extends FlatAbstractIcon
{
	protected final boolean chevron = "chevron".equals( UIManager.getString( "Component.arrowType" ) );
	protected final Color arrowColor = UIManager.getColor( "Menu.icon.arrowColor" );
	protected final Color disabledArrowColor = UIManager.getColor( "Menu.icon.disabledArrowColor" );
	protected final Color selectionForeground = UIManager.getColor( "Menu.selectionForeground" );

	public FlatMenuArrowIcon() {
		super( 6, 10, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		if( !c.getComponentOrientation().isLeftToRight() )
			g.rotate( Math.toRadians( 180 ), width / 2., height / 2. );

		g.setColor( getArrowColor( c ) );
		if( chevron ) {
			// chevron arrow
			Path2D path = FlatUIUtils.createPath( false, 1,1, 5,5, 1,9 );
			g.setStroke( new BasicStroke( 1f ) );
			g.draw( path );
		} else {
			// triangle arrow
			g.fill( FlatUIUtils.createPath( 0,0.5, 5,5, 0,9.5 ) );
		}
	}

	protected Color getArrowColor( Component c ) {
		if( c instanceof JMenu && ((JMenu)c).isSelected() && !isUnderlineSelection() )
			return selectionForeground;

		return c.isEnabled() ? arrowColor : disabledArrowColor;
	}

	protected boolean isUnderlineSelection() {
		// not storing value of "MenuItem.selectionType" in class to allow changing at runtime
		return "underline".equals( UIManager.getString( "MenuItem.selectionType" ) );
	}
}
