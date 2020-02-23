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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "caps lock" icon for {@link javax.swing.JPasswordField}.
 *
 * @uiDefault PasswordField.capsLockIconColor			Color
 *
 * @author Karl Tauber
 */
public class FlatCapsLockIcon
	extends FlatAbstractIcon
{
	public FlatCapsLockIcon() {
		super( 16, 16, UIManager.getColor( "PasswordField.capsLockIconColor" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <rect width="16" height="16" fill="#6E6E6E" rx="3"/>
			    <rect width="6" height="2" x="5" y="12" fill="#FFF"/>
    			<path fill="#FFF" d="M2,8 L8,2 L14,8 L11,8 L11,10 L5,10 L5,8 L2,8 Z"/>
			  </g>
			</svg>
		*/

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new RoundRectangle2D.Float( 0, 0, 16, 16, 6, 6 ), false );
		path.append( new Rectangle2D.Float( 5, 12, 6, 2 ), false );
		path.append( FlatUIUtils.createPath( 2,8, 8,2, 14,8, 11,8, 11,10, 5,10, 5,8 ), false );
		g.fill( path );
	}
}
