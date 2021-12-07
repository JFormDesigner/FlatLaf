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

package com.formdev.flatlaf.swingx.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.UIManager;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.icon.ColumnControlIcon;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Column control icon for {@link JXTable}.
 * Replaces {@link ColumnControlIcon}.
 *
 * @author Karl Tauber
 */
public class FlatColumnControlIcon
	extends FlatAbstractIcon
{
	protected Color iconColor = UIManager.getColor( "ColumnControlButton.iconColor" );

	public FlatColumnControlIcon() {
		super( 10, 10, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		g.setColor( iconColor );

		// table
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new Rectangle2D.Float( 1, 0, 8, 8 ), false ); // table outline
		path.append( new Rectangle2D.Float( 2, 1, 2, 1 ), false ); // subtract top left
		path.append( new Rectangle2D.Float( 5, 1, 3, 1 ), false ); // subtract top right
		path.append( new Rectangle2D.Float( 2, 3, 2, 4 ), false ); // subtract bottom left
		path.append( new Rectangle2D.Float( 5, 3, 3, 4 ), false ); // subtract bottom right

		// subtract area for arrow from table
		Area area = new Area( path );
		area.subtract( new Area( new Rectangle2D.Float( 3, 5, 7, 5 ) ) );

		// paint table
		g.fill( area );

		// paint triangle arrow
		g.fill( FlatUIUtils.createPath( 3,6, 6.5,10, 10,6 ) );
	}
}
