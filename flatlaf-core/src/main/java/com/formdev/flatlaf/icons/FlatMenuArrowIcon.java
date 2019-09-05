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
import java.awt.geom.Path2D;
import javax.swing.JMenu;
import javax.swing.UIManager;

/**
 * "arrow" icon for {@link javax.swing.JMenu}.
 *
 * @uiDefault Menu.icon.arrowColor				Color
 * @uiDefault Menu.icon.disabledArrowColor		Color
 * @uiDefault Menu.selectionForeground			Color
 *
 * @author Karl Tauber
 */
public class FlatMenuArrowIcon
	extends FlatAbstractIcon
{
	protected final Color arrowColor = UIManager.getColor( "Menu.icon.arrowColor" );
	protected final Color disabledArrowColor = UIManager.getColor( "Menu.icon.disabledArrowColor" );
	protected final Color selectionForeground = UIManager.getColor( "Menu.selectionForeground" );

	public FlatMenuArrowIcon() {
		super( 5, 10, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		Path2D arrow = new Path2D.Float();
		arrow.moveTo( 0, 0.5 );
		arrow.lineTo( 0, 9.5 );
		arrow.lineTo( 5, 5 );
		arrow.closePath();

		if( !c.getComponentOrientation().isLeftToRight() )
			g.rotate( Math.toRadians( 180 ), width / 2., height / 2. );

		g.setColor( getArrowColor( c ) );
		g.fill( arrow );
	}

	private Color getArrowColor( Component c ) {
		if( c instanceof JMenu && ((JMenu)c).isSelected() )
			return selectionForeground;

		return c.isEnabled() ? arrowColor : disabledArrowColor;
	}
}
