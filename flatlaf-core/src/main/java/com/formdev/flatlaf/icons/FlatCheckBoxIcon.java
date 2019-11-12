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

import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Icon for {@link javax.swing.JCheckBox}.
 *
 * Note: If Component.focusWidth is greater than zero, then the outline focus border
 *       is painted outside of the icon bounds. Make sure that the checkbox
 *       has margins, which are equal or greater than focusWidth.
 *
 * @uiDefault Component.focusWidth						int
 * @uiDefault Component.focusColor						Color
 * @uiDefault CheckBox.icon.focusedColor				Color	optional; defaults to Component.focusColor
 * @uiDefault CheckBox.icon.borderColor					Color
 * @uiDefault CheckBox.icon.disabledBorderColor			Color
 * @uiDefault CheckBox.icon.selectedBorderColor			Color
 * @uiDefault CheckBox.icon.focusedBorderColor			Color
 * @uiDefault CheckBox.icon.hoverBorderColor			Color	optional
 * @uiDefault CheckBox.icon.selectedFocusedBorderColor	Color	optional
 * @uiDefault CheckBox.icon.background					Color
 * @uiDefault CheckBox.icon.disabledBackground			Color
 * @uiDefault CheckBox.icon.focusedBackground			Color	optional
 * @uiDefault CheckBox.icon.hoverBackground				Color	optional
 * @uiDefault CheckBox.icon.pressedBackground			Color	optional
 * @uiDefault CheckBox.icon.selectedBackground			Color
 * @uiDefault CheckBox.icon.selectedHoverBackground		Color	optional
 * @uiDefault CheckBox.icon.selectedPressedBackground	Color	optional
 * @uiDefault CheckBox.icon.checkmarkColor				Color
 * @uiDefault CheckBox.icon.disabledCheckmarkColor		Color
 * @uiDefault CheckBox.arc								int
 *
 * @author Karl Tauber
 */
public class FlatCheckBoxIcon
	extends FlatAbstractIcon
{
	protected final int focusWidth = UIManager.getInt( "Component.focusWidth" );
	protected final Color focusColor = FlatUIUtils.getUIColor( "CheckBox.icon.focusedColor",
		UIManager.getColor( "Component.focusColor" ) );
	protected final int arc = FlatUIUtils.getUIInt( "CheckBox.arc", 2 );

	protected final Color borderColor = UIManager.getColor( "CheckBox.icon.borderColor" );
	protected final Color disabledBorderColor = UIManager.getColor( "CheckBox.icon.disabledBorderColor" );
	protected final Color selectedBorderColor = UIManager.getColor( "CheckBox.icon.selectedBorderColor" );
	protected final Color focusedBorderColor = UIManager.getColor( "CheckBox.icon.focusedBorderColor" );
	protected final Color hoverBorderColor = UIManager.getColor( "CheckBox.icon.hoverBorderColor" );
	protected final Color selectedFocusedBorderColor = UIManager.getColor( "CheckBox.icon.selectedFocusedBorderColor" );
	protected final Color background = UIManager.getColor( "CheckBox.icon.background" );
	protected final Color disabledBackground = UIManager.getColor( "CheckBox.icon.disabledBackground" );
	protected final Color focusedBackground = UIManager.getColor( "CheckBox.icon.focusedBackground" );
	protected final Color hoverBackground = UIManager.getColor( "CheckBox.icon.hoverBackground" );
	protected final Color pressedBackground = UIManager.getColor( "CheckBox.icon.pressedBackground" );
	protected final Color selectedBackground = UIManager.getColor( "CheckBox.icon.selectedBackground" );
	protected final Color selectedHoverBackground = UIManager.getColor( "CheckBox.icon.selectedHoverBackground" );
	protected final Color selectedPressedBackground = UIManager.getColor( "CheckBox.icon.selectedPressedBackground" );
	protected final Color checkmarkColor = UIManager.getColor( "CheckBox.icon.checkmarkColor" );
	protected final Color disabledCheckmarkColor = UIManager.getColor( "CheckBox.icon.disabledCheckmarkColor" );

	static final int ICON_SIZE = 15;

	public FlatCheckBoxIcon() {
		super( ICON_SIZE, ICON_SIZE, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g2 ) {
		boolean indeterminate = c instanceof JComponent && clientPropertyEquals( (JComponent) c, SELECTED_STATE, SELECTED_STATE_INDETERMINATE );
		boolean selected = indeterminate || (c instanceof AbstractButton && ((AbstractButton)c).isSelected());

		// paint focused border
		if( c.hasFocus() && focusWidth > 0 ) {
			g2.setColor( focusColor );
			paintFocusBorder( g2 );
		}

		// paint border
		g2.setColor( FlatButtonUI.buttonStateColor( c,
			selected ? selectedBorderColor : borderColor,
			disabledBorderColor,
			selected && selectedFocusedBorderColor != null ? selectedFocusedBorderColor : focusedBorderColor,
			hoverBorderColor,
			null ) );
		paintBorder( g2 );

		// paint background
		FlatUIUtils.setColor( g2, FlatButtonUI.buttonStateColor( c,
			selected ? selectedBackground : background,
			disabledBackground,
			focusedBackground,
			selected && selectedHoverBackground != null ? selectedHoverBackground : hoverBackground,
			selected && selectedPressedBackground != null ? selectedPressedBackground : pressedBackground ),
			background );
		paintBackground( g2 );

		// paint checkmark
		if( selected || indeterminate ) {
			g2.setColor( c.isEnabled() ? checkmarkColor : disabledCheckmarkColor );
			if( indeterminate )
				paintIndeterminate( g2 );
			else
				paintCheckmark( g2 );
		}
	}

	protected void paintFocusBorder( Graphics2D g2 ) {
		// the outline focus border is painted outside of the icon
		int wh = ICON_SIZE - 1 + (focusWidth * 2);
		int arcwh = (arc + focusWidth) * 2;
		g2.fillRoundRect( -focusWidth + 1, -focusWidth, wh, wh, arcwh, arcwh );
	}

	protected void paintBorder( Graphics2D g2 ) {
		int arcwh = arc * 2;
		g2.fillRoundRect( 1, 0, 14, 14, arcwh, arcwh );
	}

	protected void paintBackground( Graphics2D g2 ) {
		int arcwh = (arc * 2) - 1;
		g2.fillRoundRect( 2, 1, 12, 12, arcwh, arcwh );
	}

	protected void paintCheckmark( Graphics2D g2 ) {
		Path2D.Float path = new Path2D.Float();
		path.moveTo( 4.5f, 7.5f );
		path.lineTo( 6.6f, 10f );
		path.lineTo( 11.25f, 3.5f );

		g2.setStroke( new BasicStroke( 1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
		g2.draw( path );
	}

	protected void paintIndeterminate( Graphics2D g2 ) {
		g2.fill( new RoundRectangle2D.Float( 3.75f, 5.75f, 8.5f, 2.5f, 2f, 2f ) );
	}
}
