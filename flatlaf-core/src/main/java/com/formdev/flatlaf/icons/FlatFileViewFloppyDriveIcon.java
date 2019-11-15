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
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "floppy drive" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Objects.Grey							Color
 *
 * @author Karl Tauber
 */
public class FlatFileViewFloppyDriveIcon
	extends FlatAbstractIcon
{
	public FlatFileViewFloppyDriveIcon() {
		super( 16, 16, UIManager.getColor( "Objects.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <path fill="#6E6E6E" d="M11,14 L11,11 L5,11 L5,14 L2,14 L2,2 L14,2 L14,14 L11,14 Z M4,4 L4,8 L12,8 L12,4 L4,4 Z"/>
			    <rect width="4" height="2" x="6" y="12" fill="#6E6E6E"/>
			  </g>
			</svg>
		*/

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( FlatUIUtils.createPath( 11,14, 11,11, 5,11, 5,14, 2,14, 2,2, 14,2, 14,14, 11,14 ), false );
		path.append( FlatUIUtils.createPath( 4,4, 4,8, 12,8, 12,4, 4,4 ), false );
		g.fill( path );

		g.fillRect( 6, 12, 4, 2 );
	}
}
