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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "up folder" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Actions.Grey							Color
 * @uiDefault Actions.Blue							Color
 *
 * @author Karl Tauber
 */
public class FlatFileChooserUpFolderIcon
	extends FlatAbstractIcon
{
	private final Color blueColor = UIManager.getColor( "Actions.Blue" );

	public FlatFileChooserUpFolderIcon() {
		super( 16, 16, UIManager.getColor( "Actions.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <polygon fill="#6E6E6E" points="2 3 5.5 3 7 5 9 5 9 9 13 9 13 5 14 5 14 13 2 13"/>
			    <path fill="#389FD6" d="M12,4 L12,8 L10,8 L10,4 L8,4 L11,1 L14,4 L12,4 Z"/>
			  </g>
			</svg>
		*/

		g.fill( FlatUIUtils.createPath( 2,3, 5.5,3, 7,5, 9,5, 9,9, 13,9, 13,5, 14,5, 14,13, 2,13 ) );

		g.setColor( blueColor );
		g.fill( FlatUIUtils.createPath( 12,4, 12,8, 10,8, 10,4, 8,4, 11,1, 14,4, 12,4 ) );
	}
}
