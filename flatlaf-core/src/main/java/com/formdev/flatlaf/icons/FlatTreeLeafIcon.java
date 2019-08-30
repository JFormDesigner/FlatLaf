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
import java.awt.geom.Path2D;
import javax.swing.UIManager;

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
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <path fill="#D8D8D8" d="M8 6L8 1 13 1 13 15 3 15 3 6z"/>
			    <path fill="#D8D8D8" d="M3 5L7 5 7 1z"/>
			  </g>
			</svg>
		*/

		Path2D arrow = new Path2D.Float();
		arrow.moveTo( 8, 6 );
		arrow.lineTo( 8, 1 );
		arrow.lineTo( 13, 1 );
		arrow.lineTo( 13, 15 );
		arrow.lineTo( 3, 15 );
		arrow.lineTo( 3, 6 );
		arrow.closePath();
		g.fill( arrow );

		arrow = new Path2D.Float();
		arrow.moveTo( 3, 5 );
		arrow.lineTo( 7, 5 );
		arrow.lineTo( 7, 1 );
		arrow.closePath();
		g.fill( arrow );
	}
}
