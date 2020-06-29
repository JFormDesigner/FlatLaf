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
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;

/**
 * "close" icon for windows (frames and dialogs).
 *
 * @uiDefault TitlePane.closeHoverBackground			Color
 * @uiDefault TitlePane.closePressedBackground			Color
 * @uiDefault TitlePane.closeHoverForeground			Color
 * @uiDefault TitlePane.closePressedForeground			Color
 *
 * @author Karl Tauber
 */
public class FlatWindowCloseIcon
	extends FlatWindowAbstractIcon
{
	private final Color hoverForeground = UIManager.getColor( "TitlePane.closeHoverForeground" );
	private final Color pressedForeground = UIManager.getColor( "TitlePane.closePressedForeground" );

	public FlatWindowCloseIcon() {
		super( UIManager.getDimension( "TitlePane.buttonSize" ),
			UIManager.getColor( "TitlePane.closeHoverBackground" ),
			UIManager.getColor( "TitlePane.closePressedBackground" ) );
	}

	@Override
	protected void paintIconAt1x( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
		int iwh = (int) (10 * scaleFactor);
		int ix = x + ((width - iwh) / 2);
		int iy = y + ((height - iwh) / 2);
		int ix2 = ix + iwh - 1;
		int iy2 = iy + iwh - 1;
		int thickness = (int) scaleFactor;

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new Line2D.Float( ix, iy, ix2, iy2 ), false );
		path.append( new Line2D.Float( ix, iy2, ix2, iy ), false );
		g.setStroke( new BasicStroke( thickness ) );
		g.draw( path );
	}

	@Override
	protected Color getForeground( Component c ) {
		return FlatButtonUI.buttonStateColor( c, c.getForeground(), null, null, hoverForeground, pressedForeground );
	}
}
