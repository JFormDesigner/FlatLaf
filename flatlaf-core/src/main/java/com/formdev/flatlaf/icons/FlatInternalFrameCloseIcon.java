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

package com.formdev.flatlaf.icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;

/**
 * "close" icon for {@link javax.swing.JInternalFrame}.
 *
 * @uiDefault InternalFrame.buttonSize					Dimension
 * @uiDefault InternalFrame.closeHoverBackground		Color
 * @uiDefault InternalFrame.closePressedBackground		Color
 * @uiDefault InternalFrame.closeHoverForeground		Color
 * @uiDefault InternalFrame.closePressedForeground		Color
 *
 * @author Karl Tauber
 */
public class FlatInternalFrameCloseIcon
	extends FlatInternalFrameAbstractIcon
{
	private final Color hoverForeground = UIManager.getColor( "InternalFrame.closeHoverForeground" );
	private final Color pressedForeground = UIManager.getColor( "InternalFrame.closePressedForeground" );

	public FlatInternalFrameCloseIcon() {
		super( UIManager.getDimension( "InternalFrame.buttonSize" ),
			UIManager.getColor( "InternalFrame.closeHoverBackground" ),
			UIManager.getColor( "InternalFrame.closePressedBackground" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		paintBackground( c, g );

		g.setColor( FlatButtonUI.buttonStateColor( c, c.getForeground(), null, null, hoverForeground, pressedForeground ) );

		float mx = width / 2f;
		float my = height / 2f;
		float r = 3.25f;

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD, 4 );
		path.moveTo( mx - r, my - r );
		path.lineTo( mx + r, my + r );
		path.moveTo( mx - r, my + r );
		path.lineTo( mx + r, my - r );
		g.setStroke( new BasicStroke( 1f ) );
		g.draw( path );
	}
}
