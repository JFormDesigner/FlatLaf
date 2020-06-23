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
 * @uiDefault CheckBox.icon.style						String	optional; "outline"/null (default) or "filled"
 * @uiDefault Component.focusWidth						int
 * @uiDefault Component.focusColor						Color
 * @uiDefault CheckBox.icon.focusWidth					int		optional; defaults to Component.focusWidth
 * @uiDefault CheckBox.icon.focusColor					Color	optional; defaults to Component.focusColor
 * @uiDefault CheckBox.icon.borderColor					Color
 * @uiDefault CheckBox.icon.background					Color
 * @uiDefault CheckBox.icon.selectedBorderColor			Color
 * @uiDefault CheckBox.icon.selectedBackground			Color
 * @uiDefault CheckBox.icon.checkmarkColor				Color
 * @uiDefault CheckBox.icon.disabledBorderColor			Color
 * @uiDefault CheckBox.icon.disabledBackground			Color
 * @uiDefault CheckBox.icon.disabledCheckmarkColor		Color
 * @uiDefault CheckBox.icon.focusedBorderColor			Color
 * @uiDefault CheckBox.icon.focusedBackground			Color	optional
 * @uiDefault CheckBox.icon.selectedFocusedBorderColor	Color	optional
 * @uiDefault CheckBox.icon.selectedFocusedBackground	Color	optional
 * @uiDefault CheckBox.icon.hoverBorderColor			Color	optional
 * @uiDefault CheckBox.icon.hoverBackground				Color	optional
 * @uiDefault CheckBox.icon.selectedHoverBackground		Color	optional
 * @uiDefault CheckBox.icon.pressedBackground			Color	optional
 * @uiDefault CheckBox.icon.selectedPressedBackground	Color	optional
 * @uiDefault CheckBox.arc								int
 *
 * @author Karl Tauber
 */
public class FlatCheckBoxIcon
	extends FlatAbstractIcon
{
	protected final String style = UIManager.getString( "CheckBox.icon.style" );
	public final int focusWidth = getUIInt( "CheckBox.icon.focusWidth",
		UIManager.getInt( "Component.focusWidth" ), style );
	protected final Color focusColor = FlatUIUtils.getUIColor( "CheckBox.icon.focusColor",
		UIManager.getColor( "Component.focusColor" ) );
	protected final int arc = FlatUIUtils.getUIInt( "CheckBox.arc", 2 );

	// enabled
	protected final Color borderColor = getUIColor( "CheckBox.icon.borderColor", style );
	protected final Color background = getUIColor( "CheckBox.icon.background", style );
	protected final Color selectedBorderColor = getUIColor( "CheckBox.icon.selectedBorderColor", style );
	protected final Color selectedBackground = getUIColor( "CheckBox.icon.selectedBackground", style );
	protected final Color checkmarkColor = getUIColor( "CheckBox.icon.checkmarkColor", style );

	// disabled
	protected final Color disabledBorderColor = getUIColor( "CheckBox.icon.disabledBorderColor", style );
	protected final Color disabledBackground = getUIColor( "CheckBox.icon.disabledBackground", style );
	protected final Color disabledCheckmarkColor = getUIColor( "CheckBox.icon.disabledCheckmarkColor", style );

	// focused
	protected final Color focusedBorderColor = getUIColor( "CheckBox.icon.focusedBorderColor", style );
	protected final Color focusedBackground = getUIColor( "CheckBox.icon.focusedBackground", style );
	protected final Color selectedFocusedBorderColor = getUIColor( "CheckBox.icon.selectedFocusedBorderColor", style );
	protected final Color selectedFocusedBackground = getUIColor( "CheckBox.icon.selectedFocusedBackground", style );
	protected final Color selectedFocusedCheckmarkColor = getUIColor( "CheckBox.icon.selectedFocusedCheckmarkColor", style );

	// hover
	protected final Color hoverBorderColor = getUIColor( "CheckBox.icon.hoverBorderColor", style );
	protected final Color hoverBackground = getUIColor( "CheckBox.icon.hoverBackground", style );
	protected final Color selectedHoverBackground = getUIColor( "CheckBox.icon.selectedHoverBackground", style );

	// pressed
	protected final Color pressedBackground = getUIColor( "CheckBox.icon.pressedBackground", style );
	protected final Color selectedPressedBackground = getUIColor( "CheckBox.icon.selectedPressedBackground", style );

	protected static Color getUIColor( String key, String style ) {
		if( style != null ) {
			Color color = UIManager.getColor( styleKey( key, style ) );
			if( color != null )
				return color;
		}
		return UIManager.getColor( key );
	}

	protected static int getUIInt( String key, int defaultValue, String style ) {
		if( style != null ) {
			Object value = UIManager.get( styleKey( key, style ) );
			if( value instanceof Integer )
				return (Integer) value;
		}
		return FlatUIUtils.getUIInt( key, defaultValue );
	}

	private static String styleKey( String key, String style ) {
		return key.replace( ".icon.", ".icon[" + style + "]." );
	}

	static final int ICON_SIZE = 15;

	public FlatCheckBoxIcon() {
		super( ICON_SIZE, ICON_SIZE, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g2 ) {
		boolean indeterminate = c instanceof JComponent && clientPropertyEquals( (JComponent) c, SELECTED_STATE, SELECTED_STATE_INDETERMINATE );
		boolean selected = indeterminate || (c instanceof AbstractButton && ((AbstractButton)c).isSelected());
		boolean isFocused = FlatUIUtils.isPermanentFocusOwner( c );

		// paint focused border
		if( isFocused && focusWidth > 0 ) {
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
		g2.setColor( FlatUIUtils.deriveColor( FlatButtonUI.buttonStateColor( c,
			selected ? selectedBackground : background,
			disabledBackground,
			(selected && selectedFocusedBackground != null) ? selectedFocusedBackground : focusedBackground,
			(selected && selectedHoverBackground != null) ? selectedHoverBackground : hoverBackground,
			(selected && selectedPressedBackground != null) ? selectedPressedBackground : pressedBackground ),
			background ) );
		paintBackground( g2 );

		// paint checkmark
		if( selected || indeterminate ) {
			g2.setColor( c.isEnabled()
				? ((selected && isFocused && selectedFocusedCheckmarkColor != null)
					? selectedFocusedCheckmarkColor
					: checkmarkColor)
				: disabledCheckmarkColor );
			if( indeterminate )
				paintIndeterminate( g2 );
			else
				paintCheckmark( g2 );
		}
	}

	protected void paintFocusBorder( Graphics2D g2 ) {
		// the outline focus border is painted outside of the icon
		int wh = ICON_SIZE - 1 + (focusWidth * 2);
		int arcwh = arc + (focusWidth * 2);
		g2.fillRoundRect( -focusWidth + 1, -focusWidth, wh, wh, arcwh, arcwh );
	}

	protected void paintBorder( Graphics2D g2 ) {
		int arcwh = arc;
		g2.fillRoundRect( 1, 0, 14, 14, arcwh, arcwh );
	}

	protected void paintBackground( Graphics2D g2 ) {
		int arcwh = arc - 1;
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
