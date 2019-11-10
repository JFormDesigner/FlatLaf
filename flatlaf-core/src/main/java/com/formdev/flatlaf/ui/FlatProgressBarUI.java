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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JProgressBar}.
 *
 * <!-- BasicProgressBarUI -->
 *
 * @uiDefault ProgressBar.font						Font
 * @uiDefault ProgressBar.background				Color
 * @uiDefault ProgressBar.foreground				Color
 * @uiDefault ProgressBar.selectionBackground		Color
 * @uiDefault ProgressBar.selectionForeground		Color
 * @uiDefault ProgressBar.border					Border
 * @uiDefault ProgressBar.horizontalSize			Dimension	default is 146,12
 * @uiDefault ProgressBar.verticalSize				Dimension	default is 12,146
 * @uiDefault ProgressBar.repaintInterval			int		default is 50 milliseconds
 * @uiDefault ProgressBar.cycleTime					int		default is 3000 milliseconds
 *
 * @author Karl Tauber
 */
public class FlatProgressBarUI
	extends BasicProgressBarUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatProgressBarUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installProperty( progressBar, "opaque", false );
	}

	@Override
	protected Dimension getPreferredInnerHorizontal() {
		return UIScale.scale( super.getPreferredInnerHorizontal() );
	}

	@Override
	protected Dimension getPreferredInnerVertical() {
		return UIScale.scale( super.getPreferredInnerVertical() );
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		if( c.isOpaque() )
			FlatUIUtils.paintParentBackground( g, c );

		paint( g, c );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		Insets insets = progressBar.getInsets();
		int x = insets.left;
		int y = insets.top;
		int width = progressBar.getWidth() - (insets.right + insets.left);
		int height = progressBar.getHeight() - (insets.top + insets.bottom);

		if( width <= 0 || height <= 0 )
			return;

		boolean horizontal = (progressBar.getOrientation() == JProgressBar.HORIZONTAL);
		int arc = horizontal ? height : width;

		FlatUIUtils.setRenderingHints( (Graphics2D) g );

		// paint track
		g.setColor( progressBar.getBackground() );
		((Graphics2D)g).fill( new RoundRectangle2D.Float( x, y, width, height, arc, arc ) );

		// paint progress
		if( progressBar.isIndeterminate() ) {
			boxRect = getBox( boxRect );
			if( boxRect != null ) {
				g.setColor( progressBar.getForeground() );
				((Graphics2D)g).fill( new RoundRectangle2D.Float( boxRect.x, boxRect.y,
					boxRect.width, boxRect.height, arc, arc ) );
			}

			if( progressBar.isStringPainted() )
				paintString( g, x, y, width, height, 0, insets );
		} else {
			int amountFull = getAmountFull( insets, width, height );

			g.setColor( progressBar.getForeground() );
			((Graphics2D)g).fill( horizontal
				? new RoundRectangle2D.Float( c.getComponentOrientation().isLeftToRight() ? x : x + (width - amountFull),
					y, amountFull, height, arc, arc )
				: new RoundRectangle2D.Float( x, y + (height - amountFull), width, amountFull, arc, arc ) );

			if( progressBar.isStringPainted() )
				paintString( g, x, y, width, height, amountFull, insets );
		}
	}
}
