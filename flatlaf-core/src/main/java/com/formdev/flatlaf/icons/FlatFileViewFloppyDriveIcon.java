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
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "floppy drive" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Objects.Grey							Color
 *
 * @author Karl Tauber
 */
public class FlatFileViewFloppyDriveIcon
	extends FlatAbstractIcon
{
	public FlatFileViewFloppyDriveIcon() {
		super( 16, 16, UIManager.getColor( "Objects.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd" stroke-linejoin="round">
			    <path stroke="#6E6E6E" d="M3.5,2.5 L11.5,2.5 L11.5,2.5 L13.5,4.5 L13.5,12.5 C13.5,13.0522847 13.0522847,13.5 12.5,13.5 L3.5,13.5 C2.94771525,13.5 2.5,13.0522847 2.5,12.5 L2.5,3.5 C2.5,2.94771525 2.94771525,2.5 3.5,2.5 Z"/>
			    <polyline stroke="#6E6E6E" points="4.5 13 4.5 9.5 11.5 9.5 11.5 13"/>
			    <polyline stroke="#6E6E6E" points="5.5 3 5.5 5.5 10.5 5.5 10.5 3"/>
			  </g>
			</svg>
		*/

		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		g.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );

		g.draw( FlatUIUtils.createPath( 3.5,2.5, 11.5,2.5, 11.5,2.5, 13.5,4.5,
			13.5,12.5, FlatUIUtils.QUAD_TO, 13.5,13.5, 12.5,13.5,
			3.5,13.5,  FlatUIUtils.QUAD_TO, 2.5,13.5,  2.5,12.5,
			2.5,3.5,   FlatUIUtils.QUAD_TO, 2.5,2.5,   3.5,2.5 ) );
		g.draw( FlatUIUtils.createPath( false, 4.5,13, 4.5,9.5, 11.5,9.5, 11.5,13 ) );
		g.draw( FlatUIUtils.createPath( false, 5.5,3, 5.5,5.5, 10.5,5.5, 10.5,3 ) );
	}
}
