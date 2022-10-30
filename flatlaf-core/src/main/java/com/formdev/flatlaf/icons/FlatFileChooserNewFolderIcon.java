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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import javax.swing.UIManager;

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
	private final Color greenColor = UIManager.getColor( "Actions.Green" );

	public FlatFileChooserNewFolderIcon() {
		super( 16, 16, UIManager.getColor( "Actions.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <path stroke="#6E6E6E" d="M13,13.5 L3,13.5 C2.17157288,13.5 1.5,12.8284271 1.5,12 L1.5,4 C1.5,3.17157288 2.17157288,2.5 3,2.5 L6.29289322,2.5 C6.42550146,2.5 6.55267842,2.55267842 6.64644661,2.64644661 L8.5,4.5 L8.5,4.5 L13,4.5 C13.8284271,4.5 14.5,5.17157288 14.5,6 L14.5,12 C14.5,12.8284271 13.8284271,13.5 13,13.5 Z"/>
			    <line x1="5.5" x2="10.5" y1="9" y2="9" stroke="#59A869" stroke-linecap="round"/>
			    <line x1="8" x2="8" y1="6.5" y2="11.5" stroke="#59A869" stroke-linecap="round"/>
			  </g>
			</svg>
		*/

		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		g.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );

		g.draw( FlatFileViewDirectoryIcon.createFolderPath() );

		g.setColor( greenColor );
		g.draw( new Line2D.Float( 5.5f, 9, 10.5f, 9 ) );
		g.draw( new Line2D.Float( 8, 6.5f, 8, 11.5f ) );
	}
}
