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
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Icon for {@link javax.swing.JRadioButton}.
 *
 * @uiDefault RadioButton.icon.centerDiameter			int
 *
 * @author Karl Tauber
 */
public class FlatRadioButtonIcon
	extends FlatCheckBoxIcon
{
	protected final int centerDiameter = FlatUIUtils.getUIInt( "RadioButton.icon.centerDiameter", 8 );

	@Override
	protected void paintFocusBorder( Graphics2D g2 ) {
		g2.fillOval( 0, 0, iconSize, iconSize );
	}

	@Override
	protected void paintBorder( Graphics2D g2 ) {
		g2.fillOval( focusWidth, focusWidth, 15, 15 );
	}

	@Override
	protected void paintBackground( Graphics2D g2 ) {
		g2.fillOval( focusWidth + 1, focusWidth + 1, 13, 13 );
	}

	@Override
	protected void paintCheckmark( Graphics2D g2 ) {
		float xy = (iconSize - centerDiameter) / 2f;
		g2.fill( new Ellipse2D.Float( xy, xy, centerDiameter, centerDiameter ) );
	}
}
