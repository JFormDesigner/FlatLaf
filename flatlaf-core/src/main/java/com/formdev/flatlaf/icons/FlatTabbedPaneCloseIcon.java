/*
 * Copyright 2020 FormDev Software GmbH
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

package com.formdev.flatlaf.icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "close" icon for closable tabs in {@link javax.swing.JTabbedPane}.
 *
 * @uiDefault TabbedPane.closeSize						Dimension
 * @uiDefault TabbedPane.closeArc						int
 * @uiDefault TabbedPane.closeCrossPlainSize			float
 * @uiDefault TabbedPane.closeCrossFilledSize			float
 * @uiDefault TabbedPane.closeCrossLineWidth			float
 * @uiDefault TabbedPane.closeBackground				Color
 * @uiDefault TabbedPane.closeForeground				Color
 * @uiDefault TabbedPane.closeHoverBackground			Color
 * @uiDefault TabbedPane.closeHoverForeground			Color
 * @uiDefault TabbedPane.closePressedBackground			Color
 * @uiDefault TabbedPane.closePressedForeground			Color
 *
 * @author Karl Tauber
 */
public class FlatTabbedPaneCloseIcon
	extends FlatAbstractIcon
{
	protected final Dimension size = UIManager.getDimension( "TabbedPane.closeSize" );
	protected final int arc = UIManager.getInt( "TabbedPane.closeArc" );
	protected final float crossPlainSize = FlatUIUtils.getUIFloat( "TabbedPane.closeCrossPlainSize", 7.5f );
	protected final float crossFilledSize = FlatUIUtils.getUIFloat( "TabbedPane.closeCrossFilledSize", crossPlainSize );
	protected final float closeCrossLineWidth = FlatUIUtils.getUIFloat( "TabbedPane.closeCrossLineWidth", 1f );
	protected final Color background = UIManager.getColor( "TabbedPane.closeBackground" );
	protected final Color foreground = UIManager.getColor( "TabbedPane.closeForeground" );
	protected final Color hoverBackground = UIManager.getColor( "TabbedPane.closeHoverBackground" );
	protected final Color hoverForeground = UIManager.getColor( "TabbedPane.closeHoverForeground" );
	protected final Color pressedBackground = UIManager.getColor( "TabbedPane.closePressedBackground" );
	protected final Color pressedForeground = UIManager.getColor( "TabbedPane.closePressedForeground" );

	public FlatTabbedPaneCloseIcon() {
		super( 16, 16, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		// paint background
		Color bg = FlatButtonUI.buttonStateColor( c, background, null, null, hoverBackground, pressedBackground );
		if( bg != null ) {
			g.setColor( FlatUIUtils.deriveColor( bg, c.getBackground() ) );
			g.fillRoundRect( (width - size.width) / 2, (height - size.height) / 2,
				size.width, size.height, arc, arc );
		}

		// set cross color
		Color fg = FlatButtonUI.buttonStateColor( c, foreground, null, null, hoverForeground, pressedForeground );
		g.setColor( FlatUIUtils.deriveColor( fg, c.getForeground() ) );

		float mx = width / 2;
		float my = height / 2;
		float r = ((bg != null) ? crossFilledSize : crossPlainSize) / 2;

		// paint cross
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new Line2D.Float( mx - r, my - r, mx + r, my + r ), false );
		path.append( new Line2D.Float( mx - r, my + r, mx + r, my - r ), false );
		g.setStroke( new BasicStroke( closeCrossLineWidth ) );
		g.draw( path );
	}
}
