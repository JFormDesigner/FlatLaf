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
 * "open" icon for {@link javax.swing.JTree} used by {@link javax.swing.tree.DefaultTreeCellRenderer}.
 *
 * @uiDefault Tree.icon.openColor			Color
 *
 * @author Karl Tauber
 */
public class FlatTreeOpenIcon
	extends FlatAbstractIcon
{
	private Path2D path;

	public FlatTreeOpenIcon() {
		super( 16, 16, UIManager.getColor( "Tree.icon.openColor" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		FlatTreeCollapsedIcon.setStyleColorFromTreeUI( c, g, ui -> ui.iconOpenColor );

		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <path fill="none" stroke="#6E6E6E" d="M2,13.5 L4.11538462,8.42307692 C4.34828895,7.86410651 4.89444872,7.5 5.5,7.5 L14.75,7.5 C15.0261424,7.5 15.25,7.72385763 15.25,8 C15.25,8.06601301 15.2369281,8.13137261 15.2115385,8.19230769 L13.3846154,12.5769231 C13.151711,13.1358935 12.6055513,13.5 12,13.5 L3,13.5 C2.17157288,13.5 1.5,12.8284271 1.5,12 L1.5,4 C1.5,3.17157288 2.17157288,2.5 3,2.5 L6.29289322,2.5 C6.42550146,2.5 6.55267842,2.55267842 6.64644661,2.64644661 L8.5,4.5 L8.5,4.5 L12,4.5 C12.8284271,4.5 13.5,5.17157288 13.5,6 L13.5,6.5 L13.5,6.5"/>
			</svg>
		*/

		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		g.setStroke( new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ) );

		if( path == null ) {
			double arc = 1.5;
			double arc2 = 0.5;
			path = FlatUIUtils.createPath( false,
				// bottom-left of opend part
				2,13.5,
				// top-left of opend part
				FlatUIUtils.ROUNDED, 4.5,7.5, arc,
				// top-right of opend part
				FlatUIUtils.ROUNDED, 15.5,7.5, arc2,

				// bottom-right
				FlatUIUtils.ROUNDED, 13,13.5, arc,
				// bottom-left
				1.5+arc,13.5,   FlatUIUtils.QUAD_TO, 1.5,13.5,  1.5,13.5-arc,
				// top-left
				1.5,2.5+arc,    FlatUIUtils.QUAD_TO, 1.5,2.5,   1.5+arc,2.5,
				// top-mid-left
				6.5-arc2,2.5,   FlatUIUtils.QUAD_TO, 6.5,2.5,   6.5+arc2,2.5+arc2,
				// top-mid-right
				8.5,4.5,
				// top-right
				13.5-arc,4.5,   FlatUIUtils.QUAD_TO, 13.5,4.5,  13.5,4.5+arc,
				13.5,6.5 );
		}
		g.draw( path );
	}
}
