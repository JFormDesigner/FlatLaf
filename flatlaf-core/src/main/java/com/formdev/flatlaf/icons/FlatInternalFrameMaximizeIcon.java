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
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "maximize" icon for {@link javax.swing.JInternalFrame}.
 *
 * @author Karl Tauber
 */
public class FlatInternalFrameMaximizeIcon
	extends FlatInternalFrameAbstractIcon
{
	public FlatInternalFrameMaximizeIcon() {
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		paintBackground( c, g );

		g.setColor( c.getForeground() );
		g.fill( FlatUIUtils.createRectangle( (width / 2) - 4, (height / 2) - 4, 8, 8, 1 ) );
	}
}
