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
 * "details view" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Actions.Grey							Color
 *
 * @author Karl Tauber
 */
public class FlatFileChooserDetailsViewIcon
	extends FlatAbstractIcon
{
	public FlatFileChooserDetailsViewIcon() {
		super( 16, 16, UIManager.getColor( "Actions.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <rect width="2" height="2" x="2" y="3" fill="#6E6E6E"/>
			    <rect width="2" height="2" x="2" y="7" fill="#6E6E6E"/>
			    <rect width="2" height="2" x="2" y="11" fill="#6E6E6E"/>
			    <rect width="8" height="2" x="6" y="3" fill="#6E6E6E"/>
			    <rect width="8" height="2" x="6" y="7" fill="#6E6E6E"/>
			    <rect width="8" height="2" x="6" y="11" fill="#6E6E6E"/>
			  </g>
			</svg>
		*/

		g.fillRect( 2, 3, 2, 2 );
		g.fillRect( 2, 7, 2, 2 );
		g.fillRect( 2, 11, 2, 2 );
		g.fillRect( 6, 3, 8, 2 );
		g.fillRect( 6, 7, 8, 2 );
		g.fillRect( 6, 11, 8, 2 );
	}
}
