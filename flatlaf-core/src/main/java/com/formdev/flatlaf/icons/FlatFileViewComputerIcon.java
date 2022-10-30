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
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.UIManager;

/**
 * "computer" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Objects.Grey						Color
 *
 * @author Karl Tauber
 */
public class FlatFileViewComputerIcon
	extends FlatAbstractIcon
{
	public FlatFileViewComputerIcon() {
		super( 16, 16, UIManager.getColor( "Objects.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <rect width="11" height="7" x="2.5" y="3.5" stroke="#6E6E6E" rx="1"/>
			    <line x1="8" x2="8" y1="11" y2="12" stroke="#6E6E6E"/>
			    <line x1="4.5" x2="11.5" y1="12.5" y2="12.5" stroke="#6E6E6E" stroke-linecap="round"/>
			  </g>
			</svg>
		*/

		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		g.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );

		g.draw( new RoundRectangle2D.Float( 2.5f, 3.5f, 11, 7, 2, 2 ) );
		g.drawLine( 8, 11, 8, 12 );
		g.draw( new Line2D.Float( 4.5f, 12.5f, 11.5f, 12.5f ) );
	}
}
