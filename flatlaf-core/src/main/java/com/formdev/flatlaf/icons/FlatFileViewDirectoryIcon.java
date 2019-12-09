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
 * "directory" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Objects.Grey						Color
 *
 * @author Karl Tauber
 */
public class FlatFileViewDirectoryIcon
	extends FlatAbstractIcon
{
	public FlatFileViewDirectoryIcon() {
		super( 16, 16, UIManager.getColor( "Objects.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <polygon fill="#6E6E6E" fill-rule="evenodd" points="1 2 6 2 8 4 15 4 15 13 1 13"/>
			</svg>
		*/

		g.fill( FlatUIUtils.createPath( 1,2, 6,2, 8,4, 15,4, 15,13, 1,13 ) );
	}
}
