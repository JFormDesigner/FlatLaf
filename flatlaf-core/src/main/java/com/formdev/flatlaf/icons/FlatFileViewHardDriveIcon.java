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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.UIManager;

/**
 * "hard drive" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Objects.Grey						Color
 *
 * @author Karl Tauber
 */
public class FlatFileViewHardDriveIcon
	extends FlatAbstractIcon
{
	public FlatFileViewHardDriveIcon() {
		super( 16, 16, UIManager.getColor( "Objects.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <path fill="#6E6E6E" fill-rule="evenodd" d="M2,6 L14,6 L14,10 L2,10 L2,6 Z M12,8 L12,9 L13,9 L13,8 L12,8 Z M10,8 L10,9 L11,9 L11,8 L10,8 Z"/>
			</svg>
		*/

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new Rectangle2D.Float( 2, 6, 12, 4 ), false );
		path.append( new Rectangle2D.Float( 12, 8, 1, 1 ), false );
		path.append( new Rectangle2D.Float( 10, 8, 1, 1 ), false );
		g.fill( path );
	}
}
