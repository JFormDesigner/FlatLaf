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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;

/**
 * Icon for {@link javax.swing.JRadioButton}.
 * <p>
 * <strong>Note</strong>:
 *       If Component.focusWidth is greater than zero, then the outer focus border
 *       is painted outside of the icon bounds. Make sure that the radiobutton
 *       has margins, which are equal or greater than focusWidth.
 *
 * @uiDefault RadioButton.icon.style					String	optional; "outlined"/null (default) or "filled"
 * @uiDefault RadioButton.icon.centerDiameter			int or float
 *
 * @author Karl Tauber
 */
public class FlatRadioButtonIcon
	extends FlatCheckBoxIcon
{
	@Styleable protected float centerDiameter = getUIFloat( "RadioButton.icon.centerDiameter", 8, style );

	@Override
	protected String getPropertyPrefix() {
		return "RadioButton.";
	}

	@Override
	protected void paintFocusBorder( Component c, Graphics2D g ) {
		// the outer focus border is painted outside of the icon
		float wh = ICON_SIZE + (focusWidth * 2);
		g.fill( new Ellipse2D.Float( -focusWidth, -focusWidth, wh, wh ) );
	}

	@Override
	protected void paintBorder( Component c, Graphics2D g, float borderWidth ) {
		if( borderWidth == 0 )
			return;

		g.fillOval( 0, 0, 15, 15 );
	}

	@Override
	protected void paintBackground( Component c, Graphics2D g, float borderWidth ) {
		float xy = borderWidth;
		float wh = 15 - (borderWidth * 2);
		g.fill( new Ellipse2D.Float( xy, xy, wh, wh ) );
	}

	@Override
	protected void paintCheckmark( Component c, Graphics2D g ) {
		float xy = (ICON_SIZE - centerDiameter) / 2f;
		g.fill( new Ellipse2D.Float( xy, xy, centerDiameter, centerDiameter ) );
	}
}
