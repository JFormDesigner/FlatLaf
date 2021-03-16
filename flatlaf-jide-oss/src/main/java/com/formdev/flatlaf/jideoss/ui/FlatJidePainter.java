/*
 * Copyright 2021 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.jideoss.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.JideButton;

/**
 * @author Karl Tauber
 */
public class FlatJidePainter
	extends BasicPainter
{
	protected final int arc = UIManager.getInt( "Button.arc" );

	public static ThemePainter getInstance() {
		// always create a new instance of Laf switching
		return new FlatJidePainter();
	}

	@Override
	protected void paintBackground( JComponent c, Graphics g, Rectangle rect,
		Color borderColor, Color background, int orientation )
	{
		if( c instanceof JideButton && ((JideButton)c).getButtonStyle() == JideButton.TOOLBAR_STYLE ) {
			Color oldColor = g.getColor();
			g.setColor( FlatUIUtils.deriveColor( background, c.getBackground() ) );
			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

			FlatUIUtils.paintComponentBackground( (Graphics2D) g, rect.x, rect.y,
				rect.width, rect.height, 0, UIScale.scale( (float) arc ) );

			FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
			g.setColor( oldColor );
		} else
			super.paintBackground( c, g, rect, borderColor, background, orientation );
	}
}
