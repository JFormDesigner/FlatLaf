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
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "file" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Objects.Grey						Color
 *
 * @author Karl Tauber
 */
public class FlatFileViewFileIcon
	extends FlatAbstractIcon
{
	public FlatFileViewFileIcon() {
		super( 16, 16, UIManager.getColor( "Objects.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <polygon fill="#6E6E6E" points="8 6 8 1 13 1 13 15 3 15 3 6"/>
			    <polygon fill="#6E6E6E" points="3 5 7 5 7 1"/>
			  </g>
			</svg>
		*/

		g.fill( FlatUIUtils.createPath( 8,6, 8,1, 13,1, 13,15, 3,15, 3,6 ) );
		g.fill( FlatUIUtils.createPath( 3,5, 7,5, 7,1 ) );
	}
}
