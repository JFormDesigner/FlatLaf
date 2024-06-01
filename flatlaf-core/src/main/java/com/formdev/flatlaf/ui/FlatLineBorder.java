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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;

/**
 * Line border for various components.
 * <p>
 * Paints a scaled (usually 1px thick) line around the component.
 * The line thickness is not added to the border insets.
 * The insets should be at least have line thickness (usually 1,1,1,1).
 * <p>
 * For {@link javax.swing.JPanel} and {@link javax.swing.JLabel}, this border
 * can be used paint rounded background (if line color is {@code null}) or
 * paint rounded line border with rounded background.
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
		this( insets, lineColor, 1f, -1 );
	}

	/** @since 2 */
	public FlatLineBorder( Insets insets, Color lineColor, float lineThickness, int arc ) {
		super( insets );
		this.lineColor = lineColor;
		this.lineThickness = lineThickness;
		this.arc = arc;
	}

	/** @since 3.5 */
	public FlatLineBorder( Insets insets, int arc ) {
		this( insets, null, 0, arc );
	}

	public Color getLineColor() {
		return lineColor;
	}

	/**
	 * Returns the (unscaled) line thickness used to paint the border.
	 * The line thickness does not affect the border insets.
	 */
	public float getLineThickness() {
		return lineThickness;
	}

	/**
	 * Returns the (unscaled) arc diameter of the border corners.
	 *
	 * @since 2
	 */
	public int getArc() {
		return arc;
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		if( c instanceof JComponent && ((JComponent)c).getClientProperty( FlatPopupFactory.KEY_POPUP_USES_NATIVE_BORDER ) != null )
			return;

		Color lineColor = getLineColor();
		float lineThickness = getLineThickness();
		if( lineColor == null || lineThickness <= 0 )
			return;

		int arc = getArc();
		if( arc < 0 ) {
			// get arc from label or panel
			ComponentUI ui = (c instanceof JLabel)
				? ((JLabel)c).getUI()
				: (c instanceof JPanel ? ((JPanel)c).getUI() : null);
			if( ui instanceof FlatLabelUI )
				arc = ((FlatLabelUI)ui).arc;
			else if( ui instanceof FlatPanelUI )
				arc = ((FlatPanelUI)ui).arc;

			if( arc < 0 )
				arc = 0;
		}

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );
			FlatUIUtils.paintOutlinedComponent( g2, x, y, width, height,
				0, 0, 0, scale( lineThickness ), scale( arc ), null, lineColor, null );
		} finally {
			g2.dispose();
		}
	}
}
