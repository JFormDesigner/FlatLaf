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
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.UIManager;

/**
 * "hard drive" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Objects.Grey						Color
 *
 * @author Karl Tauber
 */
public class FlatFileViewHardDriveIcon
	extends FlatAbstractIcon
{
	public FlatFileViewHardDriveIcon() {
		super( 16, 16, UIManager.getColor( "Objects.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <rect width="11" height="5" x="2.5" y="5.5" stroke="#6E6E6E" rx="1"/>
			    <circle cx="11.5" cy="8.5" r="1" fill="#6E6E6E"/>
			    <circle cx="9.5" cy="8.5" r="1" fill="#6E6E6E"/>
			  </g>
			</svg>
		*/

		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		g.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );

		g.draw( new RoundRectangle2D.Float( 2.5f, 5.5f, 11, 5, 2, 2 ) );
		g.fill( new Ellipse2D.Float( 10.8f, 7.8f, 1.4f, 1.4f ) );
		g.fill( new Ellipse2D.Float( 8.8f, 7.8f, 1.4f, 1.4f ) );
	}
}
