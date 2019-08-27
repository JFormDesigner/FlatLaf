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

import static com.formdev.flatlaf.util.UIScale.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import com.formdev.flatlaf.util.UIScale;

/**
 * Icon for {@link javax.swing.JCheckBox}.
 *
 * @uiDefault Component.focusWidth						int
 * @uiDefault Component.focusColor						Color
 * @uiDefault CheckBox.icon.borderColor					Color
 * @uiDefault CheckBox.icon.disabledBorderColor			Color
 * @uiDefault CheckBox.icon.selectedBorderColor			Color
 * @uiDefault CheckBox.icon.focusedBorderColor			Color
 * @uiDefault CheckBox.icon.selectedFocusedBorderColor	Color
 * @uiDefault CheckBox.icon.background					Color
 * @uiDefault CheckBox.icon.disabledBackground			Color
 * @uiDefault CheckBox.icon.selectedBackground			Color
 * @uiDefault CheckBox.icon.checkmarkColor				Color
 * @uiDefault CheckBox.icon.disabledCheckmarkColor		Color
 *
 * @author Karl Tauber
 */
public class FlatCheckBoxIcon
	implements Icon, UIResource
{
	protected final int focusWidth = UIManager.getInt( "Component.focusWidth" );
	protected final Color focusColor = UIManager.getColor( "Component.focusColor" );

	protected final Color borderColor = UIManager.getColor( "CheckBox.icon.borderColor" );
	protected final Color disabledBorderColor = UIManager.getColor( "CheckBox.icon.disabledBorderColor" );
	protected final Color selectedBorderColor = UIManager.getColor( "CheckBox.icon.selectedBorderColor" );
	protected final Color focusedBorderColor = UIManager.getColor( "CheckBox.icon.focusedBorderColor" );
	protected final Color selectedFocusedBorderColor = UIManager.getColor( "CheckBox.icon.selectedFocusedBorderColor" );
	protected final Color background = UIManager.getColor( "CheckBox.icon.background" );
	protected final Color disabledBackground = UIManager.getColor( "CheckBox.icon.disabledBackground" );
	protected final Color selectedBackground = UIManager.getColor( "CheckBox.icon.selectedBackground" );
	protected final Color checkmarkColor = UIManager.getColor( "CheckBox.icon.checkmarkColor" );
	protected final Color disabledCheckmarkColor = UIManager.getColor( "CheckBox.icon.disabledCheckmarkColor" );

	protected final int iconSize = 15 + (focusWidth * 2);

	@Override
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			g2.translate( x, y );
			UIScale.scaleGraphics( g2 );

			boolean enabled = c.isEnabled();
			boolean focused = c.hasFocus();
			boolean selected = (c instanceof AbstractButton) && ((AbstractButton)c).isSelected();

			// paint focused border
			if( focused ) {
				g2.setColor( focusColor );
				paintFocusBorder( g2 );
			}

			// paint border
			g2.setColor( enabled
				? (selected
					? (focused ? selectedFocusedBorderColor : selectedBorderColor)
					: (focused ? focusedBorderColor : borderColor))
				: disabledBorderColor );
			paintBorder( g2 );

			// paint background
			g2.setColor( enabled
				? (selected ? selectedBackground : background)
				: disabledBackground );
			paintBackground( g2 );

			// paint checkmark
			if( selected ) {
				g2.setColor( enabled ? checkmarkColor : disabledCheckmarkColor );
				paintCheckmark( g2 );
			}
		} finally {
			g2.dispose();
		}
	}

	protected void paintFocusBorder( Graphics2D g2 ) {
		g2.fillRoundRect( 1, 0, iconSize - 1, iconSize - 1, 8, 8 );
	}

	protected void paintBorder( Graphics2D g2 ) {
		g2.fillRoundRect( focusWidth + 1, focusWidth, 14, 14, 4, 4 );
	}

	protected void paintBackground( Graphics2D g2 ) {
		g2.fillRoundRect( focusWidth + 2, focusWidth + 1, 12, 12, 4, 4 );
	}

	protected void paintCheckmark( Graphics2D g2 ) {
		Path2D.Float path = new Path2D.Float();
		path.moveTo( 4.5f, 7.5f );
		path.lineTo( 6.6f, 10f );
		path.lineTo( 11.25f, 3.5f );

		g2.translate( focusWidth, focusWidth );
		g2.setStroke( new BasicStroke( 1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
		g2.draw( path );
		g2.translate( -focusWidth, -focusWidth );
	}

	@Override
	public int getIconWidth() {
		return scale( iconSize );
	}

	@Override
	public int getIconHeight() {
		return scale( iconSize );
	}
}
