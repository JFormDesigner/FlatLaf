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

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

/**
 * Border for {@link javax.swing.JMenuBar}.
 *
 * @uiDefault MenuBar.borderColor			Color
 *
 * @author Karl Tauber
 */
public class FlatMenuBarBorder
	extends FlatMarginBorder
{
	private final Color borderColor = UIManager.getColor( "MenuBar.borderColor" );

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			float lineHeight = scale( (float) 1 );

			FlatUIUtils.setRenderingHints( g2 );
			g2.setColor( borderColor );
			g2.fill( new Rectangle2D.Float( x, y + height - lineHeight, width, lineHeight ) );
		} finally {
			g2.dispose();
		}
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		// BasicBorders.MarginBorder does not handle JMenuBar margin
		Insets margin = (c instanceof JMenuBar) ? ((JMenuBar)c).getMargin() : new Insets( 0, 0, 0, 0 );

		insets.top = scale( margin.top );
		insets.left = scale( margin.left );
		insets.bottom = scale( margin.bottom + 1 );
		insets.right = scale( margin.right );
		return insets;
	}
}
