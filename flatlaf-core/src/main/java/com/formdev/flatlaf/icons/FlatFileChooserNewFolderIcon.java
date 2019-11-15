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
 * "new folder" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Actions.Grey							Color
 *
 * @author Karl Tauber
 */
public class FlatFileChooserNewFolderIcon
	extends FlatAbstractIcon
{
	public FlatFileChooserNewFolderIcon() {
		super( 16, 16, UIManager.getColor( "Actions.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <polygon fill="#6E6E6E" points="2 3 5.5 3 7 5 14 5 14 8 11 8 11 10 9 10 9 13 2 13"/>
			    <path fill="#59A869" d="M14,11 L16,11 L16,13 L14,13 L14,15 L12,15 L12,13 L10,13 L10,11 L12,11 L12,9 L14,9 L14,11 Z"/>
			  </g>
			</svg>
		*/

		g.fill( FlatUIUtils.createPath( 2,3, 5.5,3, 7,5, 14,5, 14,8, 11,8, 11,10, 9,10, 9,13, 2,13 ) );
		g.fill( FlatUIUtils.createPath( 14,11, 16,11, 16,13, 14,13, 14,15, 12,15, 12,13, 10,13, 10,11, 12,11, 12,9, 14,9, 14,11 ) );
	}
}
