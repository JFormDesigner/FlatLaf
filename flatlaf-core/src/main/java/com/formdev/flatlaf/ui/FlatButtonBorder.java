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

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicBorders;
import static com.formdev.flatlaf.util.UIScale.*;

/**
 * Border for {@link javax.swing.JButton}.
 *
 * @author Karl Tauber
 */
public class FlatButtonBorder
	extends BasicBorders.MarginBorder
{
	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		if( !FlatButtonUI.isContentAreaFilled( c ) )
			return;

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			float focusWidth = getFocusWidth();
			float lineWidth = getLineWidth();
			float arc = scale( 6f ); //TODO

			if( c.hasFocus() ) {
				g2.setColor( UIManager.getColor( FlatButtonUI.isDefaultButton( c )
					? "Button.default.focusColor" : "Component.focusColor" ) );
				FlatUIUtils.paintOutlineBorder( g2, x, y, width, height, focusWidth, lineWidth, arc );
			}

			g2.setPaint( getBorderColor( c ) );
			FlatUIUtils.drawRoundRectangle( g2, x, y, width, height, focusWidth, lineWidth, arc );
		} finally {
			g2.dispose();
		}
	}

	private Paint getBorderColor( Component c ) {
		if( c.isEnabled() ) {
			boolean def = FlatButtonUI.isDefaultButton( c );
			if( c.hasFocus() )
				return UIManager.getColor( def ? "Button.default.focusedBorderColor" : "Button.focusedBorderColor" );

			Color startColor = UIManager.getColor( def ? "Button.default.startBorderColor" : "Button.startBorderColor" );
			Color endColor = UIManager.getColor( def ? "Button.default.endBorderColor" : "Button.endBorderColor" );
			return (startColor.equals( endColor ) )
				? startColor
				: new GradientPaint( 0, getFocusWidth(), startColor,
					0, c.getHeight() - getFocusWidth() - 1f, endColor );
		} else
			return UIManager.getColor( "Button.disabledBorderColor" );
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		float ow = getFocusWidth() + getLineWidth();

		insets = super.getBorderInsets( c, insets );
		insets.top = round( scale( (float) insets.top ) + ow );
		insets.left = round( scale( (float) insets.left ) + ow );
		insets.bottom = round( scale( (float) insets.bottom ) + ow );
		insets.right = round( scale( (float) insets.right ) + ow );
		return insets;
	}

	protected float getFocusWidth() {
		//TODO
		return scale( 2f );
	}

	protected float getLineWidth() {
		//TODO
		return scale( 1f );
	}
}
