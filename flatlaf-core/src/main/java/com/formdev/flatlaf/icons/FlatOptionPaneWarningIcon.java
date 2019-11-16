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
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "Warning" icon for {@link javax.swing.JOptionPane}.
 *
 * @uiDefault OptionPane.icon.warningColor			Color	optional; defaults to Actions.Yellow
 * @uiDefault Actions.Yellow						Color
 *
 * @author Karl Tauber
 */
public class FlatOptionPaneWarningIcon
	extends FlatOptionPaneAbstractIcon
{
	public FlatOptionPaneWarningIcon() {
		super( "OptionPane.icon.warningColor", "Actions.Yellow" );
	}

	/*
		<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">
		  <g fill="none" fill-rule="evenodd">
		    <polygon fill="#EDA200" points="16 2 31 28 1 28"/>
		    <rect width="4" height="8" x="14" y="10" fill="#FFF"/>
		    <rect width="4" height="4" x="14" y="21" fill="#FFF"/>
		  </g>
		</svg>
	*/

	@Override
	protected Shape createOutside() {
		return FlatUIUtils.createPath( 16,2, 31,28, 1,28 );
	}

	@Override
	protected Shape createInside() {
		Path2D inside = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		inside.append( new Rectangle2D.Float( 14, 10, 4, 8 ), false );
		inside.append( new Rectangle2D.Float( 14, 21, 4, 4 ), false );
		return inside;
	}
}
