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
import java.awt.geom.Path2D;
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
	private Path2D path;

	public FlatFileViewFileIcon() {
		super( 16, 16, UIManager.getColor( "Objects.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd" stroke-linejoin="round">
			    <path stroke="#6E6E6E" d="M4,1.5 L8.8,1.5 L8.8,1.5 L13.5,6.2 L13.5,13 C13.5,13.8284271 12.8284271,14.5 12,14.5 L4,14.5 C3.17157288,14.5 2.5,13.8284271 2.5,13 L2.5,3 C2.5,2.17157288 3.17157288,1.5 4,1.5 Z"/>
			    <path stroke="#6E6E6E" d="M8.5,2 L8.5,5 C8.5,5.82842712 9.17157288,6.5 10,6.5 L13,6.5 L13,6.5"/>
			  </g>
			</svg>
		*/

		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		g.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );

		if( path == null ) {
			double arc = 1.5;
			path = FlatUIUtils.createPath( false,
				// top-left
				2.5,1.5+arc,    FlatUIUtils.QUAD_TO, 2.5,1.5,   2.5+arc,1.5,
				// top-right
				8.8,1.5, 13.5,6.2,
				// bottom-right
				13.5,14.5-arc,  FlatUIUtils.QUAD_TO, 13.5,14.5, 13.5-arc,14.5,
				// bottom-left
				2.5+arc,14.5,   FlatUIUtils.QUAD_TO, 2.5,14.5,  2.5,14.5-arc,
				FlatUIUtils.CLOSE_PATH,

				FlatUIUtils.MOVE_TO, 8.5,2,
				8.5,6.5-arc,    FlatUIUtils.QUAD_TO, 8.5,6.5,   8.5+arc,6.5,
				13,6.5 );
		}
		g.draw( path );
	}
}
