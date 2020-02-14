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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * Border for {@link javax.swing.JToolBar}.
 *
 * @uiDefault ToolBar.borderMargins				Insets
 * @uiDefault ToolBar.gripColor					Color
 *
 * @author Karl Tauber
 */
public class FlatToolBarBorder
	extends FlatMarginBorder
{
	private static final int DOT_COUNT = 4;
	private static final int DOT_SIZE = 2;
	private static final int GRIP_WIDTH = DOT_SIZE * 3;

	protected final Color gripColor = UIManager.getColor( "ToolBar.gripColor" );

	public FlatToolBarBorder() {
		super( UIManager.getInsets( "ToolBar.borderMargins" ) );
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		// paint grip
		if( c instanceof JToolBar && ((JToolBar)c).isFloatable() ) {
			Graphics2D g2 = (Graphics2D) g.create();
			try {
				FlatUIUtils.setRenderingHints( g2 );

				g2.setColor( gripColor );
				paintGrip( c, g2, x, y, width, height );
			} finally {
				g2.dispose();
			}
		}
	}

	protected void paintGrip( Component c, Graphics g, int x, int y, int width, int height ) {
		int dotSize = scale( DOT_SIZE );
		int gapSize = dotSize;
		int gripSize = (dotSize * DOT_COUNT) + ((gapSize * (DOT_COUNT - 1)));

		// include toolbar margin in grip position calculation
		Insets insets = getBorderInsets( c );

		// calculate grip position
		boolean horizontal = ((JToolBar)c).getOrientation() == SwingConstants.HORIZONTAL;
		if( horizontal ) {
			if( c.getComponentOrientation().isLeftToRight() )
				x += insets.left - (dotSize * 2);
			else
				x += width - insets.right + dotSize;
			y += Math.round( (height - gripSize) / 2f );
		} else {
			// vertical
			x += Math.round( (width - gripSize) / 2f );
			y += insets.top - (dotSize * 2);
		}

		// paint dots
		for( int i = 0; i < DOT_COUNT; i++ ) {
			g.fillOval( x, y, dotSize, dotSize );
			if( horizontal )
				y += dotSize + gapSize;
			else
				x += dotSize + gapSize;
		}
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		insets = super.getBorderInsets( c, insets );

		// add grip inset if floatable
		if( c instanceof JToolBar && ((JToolBar)c).isFloatable() ) {
			int gripInset = scale( GRIP_WIDTH );
			if( ((JToolBar)c).getOrientation() == SwingConstants.HORIZONTAL ) {
				if( c.getComponentOrientation().isLeftToRight() )
					insets.left += gripInset;
				else
					insets.right += gripInset;
			} else
				insets.top += gripInset;
		}

		return insets;
	}
}
