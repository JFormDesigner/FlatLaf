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
import com.formdev.flatlaf.util.ColorFunctions;

/**
 * "leaf" icon for {@link javax.swing.JTree} used by {@link javax.swing.tree.DefaultTreeCellRenderer}.
 *
 * @uiDefault Tree.icon.leafColor			Color
 *
 * @author Karl Tauber
 */
public class FlatTreeLeafIcon
	extends FlatAbstractIcon
{
	public FlatTreeLeafIcon() {
		super( 16, 16, UIManager.getColor( "Tree.icon.leafColor" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		FlatTreeCollapsedIcon.setStyleColorFromTreeUI( c, g, ui -> ui.iconLeafColor );

		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <rect width="11" height="13" x="2.5" y="1.5" stroke="#6E6E6E" rx="1.5"/>
			    <line x1="5.5" x2="10.5" y1="5.5" y2="5.5" stroke="#6E6E6E" stroke-linecap="round" stroke-opacity=".6"/>
			    <line x1="5.5" x2="10.5" y1="8" y2="8" stroke="#6E6E6E" stroke-linecap="round" stroke-opacity=".6"/>
			    <line x1="5.5" x2="10.5" y1="10.5" y2="10.5" stroke="#6E6E6E" stroke-linecap="round" stroke-opacity=".6"/>
			  </g>
			</svg>
		*/

		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		g.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );

		g.draw( new RoundRectangle2D.Float( 2.5f, 1.5f, 11, 13, 3, 3 ) );

		g.setColor( ColorFunctions.fade( g.getColor(), 0.6f ) );
		g.draw( new Line2D.Float( 5.5f, 5.5f, 10.5f, 5.5f ) );
		g.draw( new Line2D.Float( 5.5f, 8, 10.5f, 8 ) );
		g.draw( new Line2D.Float( 5.5f, 10.5f, 10.5f, 10.5f ) );
	}
}
