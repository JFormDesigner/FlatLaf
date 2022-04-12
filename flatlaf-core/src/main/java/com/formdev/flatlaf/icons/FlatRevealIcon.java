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

package com.formdev.flatlaf.icons;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.UIManager;

/**
 * "eye" icon for {@link javax.swing.JPasswordField}.
 *
 * @uiDefault PasswordField.revealIconColor				Color
 *
 * @author Karl Tauber
 * @since 2
 */
public class FlatRevealIcon
	extends FlatAbstractIcon
{
	public FlatRevealIcon() {
		super( 16, 16, UIManager.getColor( "PasswordField.revealIconColor" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new Ellipse2D.Float( 5.15f, 6.15f, 5.7f, 5.7f ), false );
		path.append( new Ellipse2D.Float( 6, 7, 4, 4 ), false );
		g.fill( path );

		Path2D path2 = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path2.append( new Ellipse2D.Float( 2.15f, 4.15f, 11.7f, 11.7f ), false );
		path2.append( new Ellipse2D.Float( 3, 5, 10, 10 ), false );
		Area area = new Area( path2 );
		area.subtract( new Area( new Rectangle2D.Float( 0, 9.5f, 16, 16 ) ) );
		g.fill( area );
	}
}
