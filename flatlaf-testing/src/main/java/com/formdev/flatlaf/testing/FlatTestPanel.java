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

package com.formdev.flatlaf.testing;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Karl Tauber
 */
public class FlatTestPanel
	extends JPanel
{
	@Override
	protected void paintComponent( Graphics g ) {
		int width = getWidth();
		int height = getHeight();

		if( isOpaque() ) {
			g.setColor( super.getBackground() );
			g.fillRect( 0, 0, width, height );
		}

		if( isPaintBackgroundPattern() ) {
			g.setColor( Color.magenta );
			for( int y = 0; y < height; y += 2 )
				g.drawLine( 0, y, width - 1, y );
		}
	}

	/**
	 * Overridden to see which components paint background with color from parent.
	 */
	@Override
	public Color getBackground() {
		return isPaintBackgroundPattern() ? Color.red : super.getBackground();
	}

	private boolean isPaintBackgroundPattern() {
		FlatTestFrame frame = (FlatTestFrame) SwingUtilities.getAncestorOfClass( FlatTestFrame.class, this );
		return frame != null && frame.isPaintBackgroundPattern();
	}
}
