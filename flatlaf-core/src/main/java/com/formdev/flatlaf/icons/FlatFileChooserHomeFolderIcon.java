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
 * "home folder" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Actions.Grey							Color
 *
 * @author Karl Tauber
 */
public class FlatFileChooserHomeFolderIcon
	extends FlatAbstractIcon
{
	public FlatFileChooserHomeFolderIcon() {
		super( 16, 16, UIManager.getColor( "Actions.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <polygon fill="#6E6E6E" fill-rule="evenodd" points="2 8 8 2 14 8 12 8 12 13 9 13 9 10 7 10 7 13 4 13 4 8"/>
			</svg>
		*/

		g.fill( FlatUIUtils.createPath( 2,8, 8,2, 14,8, 12,8, 12,13, 9,13, 9,10, 7,10, 7,13, 4,13, 4,8 ) );
	}
}
