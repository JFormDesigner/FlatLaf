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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.function.Function;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ToolBarUI;
import com.formdev.flatlaf.util.UIScale;

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
	private static final int GRIP_SIZE = DOT_SIZE * 3;

	protected Color gripColor = UIManager.getColor( "ToolBar.gripColor" );

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

				Color color = getStyleFromToolBarUI( c, ui -> ui.gripColor );
				g2.setColor( (color != null) ? color : gripColor );
				paintGrip( c, g2, x, y, width, height );
			} finally {
				g2.dispose();
			}
		}
	}

	protected void paintGrip( Component c, Graphics g, int x, int y, int width, int height ) {
		Rectangle r = calculateGripBounds( c, x, y, width, height );
		FlatUIUtils.paintGrip( g, r.x, r.y, r.width, r.height,
			((JToolBar)c).getOrientation() == SwingConstants.VERTICAL,
			DOT_COUNT, DOT_SIZE, DOT_SIZE, false );
	}

	protected Rectangle calculateGripBounds( Component c, int x, int y, int width, int height ) {
		// include toolbar margin in grip bounds calculation
		Insets insets = super.getBorderInsets( c, new Insets( 0, 0, 0, 0 ) );
		Rectangle r = FlatUIUtils.subtractInsets( new Rectangle( x, y, width, height ), insets );

		// calculate grip bounds
		int gripSize = UIScale.scale( GRIP_SIZE );
		if( ((JToolBar)c).getOrientation() == SwingConstants.HORIZONTAL ) {
			if( !c.getComponentOrientation().isLeftToRight() )
				r.x = r.x + r.width - gripSize;
			r.width = gripSize;
		} else
			r.height = gripSize;

		return r;
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		Insets m = getStyleFromToolBarUI( c, ui -> ui.borderMargins );
		if( m != null ) {
			int t = top, l = left, b = bottom, r = right;
			top = m.top; left = m.left; bottom = m.bottom; right = m.right;
			insets = super.getBorderInsets( c, insets );
			top = t; left = l; bottom = b; right = r;
		} else
			insets = super.getBorderInsets( c, insets );

		// add grip inset if floatable
		if( c instanceof JToolBar && ((JToolBar)c).isFloatable() ) {
			int gripInset = UIScale.scale( GRIP_SIZE );
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

	/**
	 * Because this border is shared for all toolbars,
	 * get border specific style from FlatToolBarUI.
	 */
	static <T> T getStyleFromToolBarUI( Component c, Function<FlatToolBarUI, T> f ) {
		if( c instanceof JToolBar ) {
			ToolBarUI ui = ((JToolBar)c).getUI();
			if( ui instanceof FlatToolBarUI )
				return f.apply( (FlatToolBarUI) ui );
		}
		return null;
	}
}
