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
 * Paints a scaled (usually 1px thick) line around the component.
 * The line thickness is not added to the border insets.
 * The insets should be at least have line thickness (usually 1,1,1,1).
 *
 * @author Karl Tauber
 */
public class FlatLineBorder
	extends FlatEmptyBorder
{
	private final Color lineColor;
	private final float lineThickness;
	/** @since 2 */ private final int arc;

	public FlatLineBorder( Insets insets, Color lineColor ) {
		this( insets, lineColor, 1f, 0 );
	}

	/** @since 2 */
	public FlatLineBorder( Insets insets, Color lineColor, float lineThickness, int arc ) {
		super( insets );
		this.lineColor = lineColor;
		this.lineThickness = lineThickness;
		this.arc = arc;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public float getLineThickness() {
		return lineThickness;
	}

	/** @since 2 */
	public int getArc() {
		return arc;
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );
			FlatUIUtils.paintOutlinedComponent( g2, x, y, width, height,
				0, 0, 0, scale( getLineThickness() ), scale( getArc() ), null, getLineColor(), null );
		} finally {
			g2.dispose();
		}
	}
}
