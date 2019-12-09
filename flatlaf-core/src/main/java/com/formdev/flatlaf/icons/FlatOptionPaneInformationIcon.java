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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * "Information" icon for {@link javax.swing.JOptionPane}.
 *
 * @uiDefault OptionPane.icon.informationColor		Color	optional; defaults to Actions.Blue
 * @uiDefault Actions.Blue							Color
 *
 * @author Karl Tauber
 */
public class FlatOptionPaneInformationIcon
	extends FlatOptionPaneAbstractIcon
{
	public FlatOptionPaneInformationIcon() {
		super( "OptionPane.icon.informationColor", "Actions.Blue" );
	}

	/*
		<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">
		  <g fill="none" fill-rule="evenodd">
		    <circle cx="16" cy="16" r="14" fill="#389FD6"/>
		    <rect width="4" height="11" x="14" y="14" fill="#FFF"/>
		    <rect width="4" height="4" x="14" y="7" fill="#FFF"/>
		  </g>
		</svg>
	*/

	@Override
	protected Shape createOutside() {
		return new Ellipse2D.Float( 2, 2, 28, 28 );
	}

	@Override
	protected Shape createInside() {
		Path2D inside = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		inside.append( new Rectangle2D.Float( 14, 14, 4, 11 ), false );
		inside.append( new Rectangle2D.Float( 14, 7, 4, 4 ), false );
		return inside;
	}
}
