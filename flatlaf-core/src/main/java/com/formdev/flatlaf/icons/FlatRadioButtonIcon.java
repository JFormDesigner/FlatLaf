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

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

/**
 * Icon for {@link javax.swing.JRadioButton}.
 *
 * Note: If Component.focusWidth is greater than zero, then the outline focus border
 *       is painted outside of the icon bounds. Make sure that the radiobutton
 *       has margins, which are equal or greater than focusWidth.
 *
 * @uiDefault RadioButton.icon.centerDiameter			int
 *
 * @author Karl Tauber
 */
public class FlatRadioButtonIcon
	extends FlatCheckBoxIcon
{
	protected final int centerDiameter = getUIInt( "RadioButton.icon.centerDiameter", 8, style );

	@Override
	protected void paintFocusBorder( Graphics2D g2 ) {
		// the outline focus border is painted outside of the icon
		int wh = ICON_SIZE + (focusWidth * 2);
		g2.fillOval( -focusWidth, -focusWidth, wh, wh );
	}

	@Override
	protected void paintBorder( Graphics2D g2 ) {
		g2.fillOval( 0, 0, 15, 15 );
	}

	@Override
	protected void paintBackground( Graphics2D g2 ) {
		g2.fillOval( 1, 1, 13, 13 );
	}

	@Override
	protected void paintCheckmark( Graphics2D g2 ) {
		float xy = (ICON_SIZE - centerDiameter) / 2f;
		g2.fill( new Ellipse2D.Float( xy, xy, centerDiameter, centerDiameter ) );
	}
}
