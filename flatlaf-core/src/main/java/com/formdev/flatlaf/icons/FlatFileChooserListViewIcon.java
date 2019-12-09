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
import javax.swing.UIManager;

/**
 * "list view" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Actions.Grey							Color
 *
 * @author Karl Tauber
 */
public class FlatFileChooserListViewIcon
	extends FlatAbstractIcon
{
	public FlatFileChooserListViewIcon() {
		super( 16, 16, UIManager.getColor( "Actions.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <rect width="4" height="4" x="3" y="3" fill="#6E6E6E"/>
			    <rect width="4" height="4" x="3" y="9" fill="#6E6E6E"/>
			    <rect width="4" height="4" x="9" y="9" fill="#6E6E6E"/>
			    <rect width="4" height="4" x="9" y="3" fill="#6E6E6E"/>
			  </g>
			</svg>
		*/

		g.fillRect( 3, 3, 4, 4 );
		g.fillRect( 3, 9, 4, 4 );
		g.fillRect( 9, 9, 4, 4 );
		g.fillRect( 9, 3, 4, 4 );
	}
}
