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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.JTextComponent;

/**
 * Border for various components (e.g. {@link javax.swing.JTextField}).
 *
 * @author Karl Tauber
 */
public class FlatBorder
	extends BasicBorders.MarginBorder
{
	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			float focusWidth = getFocusWidth();
			float lineWidth = getLineWidth();

			if( isFocused( c ) ) {
				g2.setColor( UIManager.getColor( "Component.focusColor" ) );
				FlatUIUtils.paintOutlineBorder( g2, x, y, width, height, focusWidth, lineWidth, 0 );
			}

			g2.setPaint( getBorderColor( c ) );
			FlatUIUtils.drawRoundRectangle( g2, x, y, width, height, focusWidth, lineWidth, 0 );
		} finally {
			g2.dispose();
		}
	}

	private Paint getBorderColor( Component c ) {
		boolean editable = !(c instanceof JTextComponent) || ((JTextComponent)c).isEditable();
		return UIManager.getColor( c.isEnabled() && editable
			? (isFocused( c )
				? "Component.focusedBorderColor"
				: "Component.borderColor")
			: "Component.disabledBorderColor" );
	}

	private boolean isFocused( Component c ) {
		if( c instanceof JScrollPane ) {
			JViewport viewport = ((JScrollPane)c).getViewport();
			Component view = (viewport != null) ? viewport.getView() : null;
			return (view != null) ? view.hasFocus() : false;
		} else
			return c.hasFocus();
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		float ow = getFocusWidth() + getLineWidth();

		insets = super.getBorderInsets( c, insets );
		insets.top = Math.round( scale( (float) insets.top ) + ow );
		insets.left = Math.round( scale( (float) insets.left ) + ow );
		insets.bottom = Math.round( scale( (float) insets.bottom ) + ow );
		insets.right = Math.round( scale( (float) insets.right ) + ow );
		return insets;
	}

	protected float getFocusWidth() {
		return FlatUIUtils.getFocusWidth();
	}

	protected float getLineWidth() {
		return FlatUIUtils.getLineWidth();
	}
}
