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
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSplitButton;

/**
 * @author Karl Tauber
 * @since 1.1
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
	public void installDefaults() {
		// avoid white background in arrow area of selected split button
		if( _bk0 == null )
			_bk0 = UIManager.getColor( "Panel.background" );

		super.installDefaults();
	}

	@Override
	protected void paintBackground( JComponent c, Graphics g, Rectangle rect,
		Color borderColor, Color background, int orientation )
	{
		if( (c instanceof JideButton && ((JideButton)c).getButtonStyle() == JideButton.TOOLBAR_STYLE) ||
			(c instanceof JideSplitButton && ((JideSplitButton)c).getButtonStyle() == JideButton.TOOLBAR_STYLE) )
		{
			Color oldColor = g.getColor();
			g.setColor( FlatUIUtils.deriveColor( background, c.getBackground() ) );
			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

			if( c instanceof JideSplitButton ) {
				// For split buttons, this method is invoked twice:
				//  - first for main button
				//  - second for arrow button
				// To show a single rounded rectangle for the whole button we always paint
				// the rounded rectangle with component bounds, but clip to the passed rectangle.

				boolean horizontal = (((JideSplitButton)c).getOrientation() == SwingConstants.HORIZONTAL);

				// for vertical orientation, the graphics context is rotated, but 1px wrong
				if( !horizontal )
					g.translate( 0, -1 );

				Shape oldClip = g.getClip();
				g.clipRect( rect.x, rect.y, rect.width, rect.height );

				FlatUIUtils.paintComponentBackground( (Graphics2D) g, 0, 0,
					horizontal ? c.getWidth() : c.getHeight(),
					horizontal ? c.getHeight() : c.getWidth(),
					0, UIScale.scale( (float) arc ) );

				g.setClip( oldClip );

				// paint separator line
				if( rect.x > 0 ) {
					g.setColor( borderColor );
					((Graphics2D)g).fill( new Rectangle2D.Float( rect.x, rect.y, UIScale.scale( 1f ), rect.height ) );
				}

				if( !horizontal )
					g.translate( 0, 1 );
			} else {
				FlatUIUtils.paintComponentBackground( (Graphics2D) g, rect.x, rect.y,
					rect.width, rect.height, 0, UIScale.scale( (float) arc ) );
			}

			FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
			g.setColor( oldColor );
		} else
			super.paintBackground( c, g, rect, borderColor, background, orientation );
	}

	@Override
	public void paintGripper( JComponent c, Graphics g, Rectangle rect, int orientation, int state ) {
		float userScaleFactor = UIScale.getUserScaleFactor();
		if( userScaleFactor > 1 ) {
			// scale gripper
			Graphics2D g2 = (Graphics2D) g.create();
			try {
				g2.translate( rect.x, rect.y );
				g2.scale( userScaleFactor, userScaleFactor );
				Rectangle rect2 = new Rectangle( 0, 0, UIScale.unscale( rect.width ), UIScale.unscale( rect.height ) );
				super.paintGripper( c, g2, rect2, orientation, state );
			} finally {
				g2.dispose();
			}
		} else
			super.paintGripper( c, g, rect, orientation, state );
	}
}
