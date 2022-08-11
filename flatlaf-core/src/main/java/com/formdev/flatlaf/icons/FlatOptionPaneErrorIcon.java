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
import java.awt.geom.RoundRectangle2D;

/**
 * "Error" icon for {@link javax.swing.JOptionPane}.
 *
 * @uiDefault OptionPane.icon.errorColor			Color	optional; defaults to Actions.Red
 * @uiDefault Actions.Red							Color
 *
 * @author Karl Tauber
 */
public class FlatOptionPaneErrorIcon
	extends FlatOptionPaneAbstractIcon
{
	public FlatOptionPaneErrorIcon() {
		super( "OptionPane.icon.errorColor", "Actions.Red" );
	}

	/*
		<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">
		  <g fill="none" fill-rule="evenodd">
		    <circle cx="16" cy="16" r="14" fill="#DB5860"/>
		    <rect width="4" height="12" x="14" y="7" fill="#FFF" rx="2"/>
		    <circle cx="16" cy="23" r="2" fill="#FFF"/>
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
		inside.append( new RoundRectangle2D.Float( 14, 7, 4, 12, 4, 4 ), false );
		inside.append( new Ellipse2D.Float( 14, 21, 4, 4 ), false );
		return inside;
	}
}
