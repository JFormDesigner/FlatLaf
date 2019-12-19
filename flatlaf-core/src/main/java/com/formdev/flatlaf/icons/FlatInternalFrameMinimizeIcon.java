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
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "minimize" (actually "restore") icon for {@link javax.swing.JInternalFrame}.
 *
 * @uiDefault InternalFrame.iconColor			Color
 *
 * @author Karl Tauber
 */
public class FlatInternalFrameMinimizeIcon
	extends FlatAbstractIcon
{
	public FlatInternalFrameMinimizeIcon() {
		super( 16, 16, UIManager.getColor( "InternalFrame.iconColor" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		Path2D r1 = FlatUIUtils.createRectangle( 5, 3, 8, 8, 1 );
		Path2D r2 = FlatUIUtils.createRectangle( 3, 5, 8, 8, 1 );

		Area area = new Area( r1 );
		area.subtract( new Area( new Rectangle2D.Float( 3, 5, 8, 8 ) ) );
		g.fill( area );

		g.fill( r2 );
	}
}
