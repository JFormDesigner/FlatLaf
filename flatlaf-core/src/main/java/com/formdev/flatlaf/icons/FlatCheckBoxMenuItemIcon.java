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
import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

/**
 * Icon for {@link javax.swing.JCheckBoxMenuItem}.
 *
 * @uiDefault MenuItemCheckBox.icon.checkmarkColor				Color
 * @uiDefault MenuItemCheckBox.icon.disabledCheckmarkColor		Color
 * @uiDefault MenuItem.selectionForeground						Color
 * @uiDefault MenuItem.selectionType							String
 *
 * @author Karl Tauber
 */
public class FlatCheckBoxMenuItemIcon
	extends FlatAbstractIcon
{
	protected final Color checkmarkColor = UIManager.getColor( "MenuItemCheckBox.icon.checkmarkColor" );
	protected final Color disabledCheckmarkColor = UIManager.getColor( "MenuItemCheckBox.icon.disabledCheckmarkColor" );
	protected final Color selectionForeground = UIManager.getColor( "MenuItem.selectionForeground" );

	public FlatCheckBoxMenuItemIcon() {
		super( 15, 15, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g2 ) {
		boolean selected = (c instanceof AbstractButton) && ((AbstractButton)c).isSelected();

		// paint checkmark
		if( selected ) {
			g2.setColor( getCheckmarkColor( c ) );
			paintCheckmark( g2 );
		}
	}

	protected void paintCheckmark( Graphics2D g2 ) {
		Path2D.Float path = new Path2D.Float();
		path.moveTo( 4.5f, 7.5f );
		path.lineTo( 6.6f, 10f );
		path.lineTo( 11.25f, 3.5f );

		g2.setStroke( new BasicStroke( 1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
		g2.draw( path );
	}

	protected Color getCheckmarkColor( Component c ) {
		if( c instanceof JMenuItem && ((JMenuItem)c).isArmed() && !isUnderlineSelection() )
			return selectionForeground;

		return c.isEnabled() ? checkmarkColor : disabledCheckmarkColor;
	}

	protected boolean isUnderlineSelection() {
		// not storing value of "MenuItem.selectionType" in class to allow changing at runtime
		return "underline".equals( UIManager.getString( "MenuItem.selectionType" ) );
	}
}
