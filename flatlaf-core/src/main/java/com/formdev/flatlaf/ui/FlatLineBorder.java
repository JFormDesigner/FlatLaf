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

/**
 * Line border for various components.
 *
 * Paints a scaled 1px thick line around the component.
 * The line thickness is not included in the border insets.
 * The insets should be at least 1,1,1,1.
 *
 * @author Karl Tauber
 */
public class FlatLineBorder
	extends FlatEmptyBorder
{
	private final Color lineColor;

	public FlatLineBorder( Insets insets, Color lineColor ) {
		super( insets );
		this.lineColor = lineColor;
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );
			g2.setColor( lineColor );
			FlatUIUtils.drawRoundRectangle( g2, x, y, width, height, 0f, scale( 1f ), 0f );
		} finally {
			g2.dispose();
		}
	}
}
