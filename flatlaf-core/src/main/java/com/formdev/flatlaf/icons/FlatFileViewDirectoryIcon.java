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
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "directory" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Objects.Grey						Color
 *
 * @author Karl Tauber
 */
public class FlatFileViewDirectoryIcon
	extends FlatAbstractIcon
{
	private Path2D path;

	public FlatFileViewDirectoryIcon() {
		super( 16, 16, UIManager.getColor( "Objects.Grey" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <path fill="none" stroke="#6E6E6E" d="M13,13.5 L3,13.5 C2.17157288,13.5 1.5,12.8284271 1.5,12 L1.5,4 C1.5,3.17157288 2.17157288,2.5 3,2.5 L6.29289322,2.5 C6.42550146,2.5 6.55267842,2.55267842 6.64644661,2.64644661 L8.5,4.5 L8.5,4.5 L13,4.5 C13.8284271,4.5 14.5,5.17157288 14.5,6 L14.5,12 C14.5,12.8284271 13.8284271,13.5 13,13.5 Z"/>
			</svg>
		*/

		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );

		if( path == null )
			path = createFolderPath();
		g.draw( path );
	}

	static Path2D createFolderPath() {
		double arc = 1.5;
		double arc2 = 0.5;
		return FlatUIUtils.createPath(
			// bottom-right
			14.5,13.5-arc,  FlatUIUtils.QUAD_TO, 14.5,13.5, 14.5-arc,13.5,
			// bottom-left
			1.5+arc,13.5,   FlatUIUtils.QUAD_TO, 1.5,13.5,  1.5,13.5-arc,
			// top-left
			1.5,2.5+arc,    FlatUIUtils.QUAD_TO, 1.5,2.5,   1.5+arc,2.5,
			// top-mid-left
			6.5-arc2,2.5,   FlatUIUtils.QUAD_TO, 6.5,2.5,   6.5+arc2,2.5+arc2,
			// top-mid-right
			8.5,4.5,
			// top-right
			14.5-arc,4.5,   FlatUIUtils.QUAD_TO, 14.5,4.5,  14.5,4.5+arc );
	}
}
