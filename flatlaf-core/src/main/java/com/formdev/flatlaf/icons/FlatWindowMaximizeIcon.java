/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.icons;

import java.awt.Graphics2D;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * "maximize" icon for windows (frames and dialogs).
 *
 * @author Karl Tauber
 */
public class FlatWindowMaximizeIcon
	extends FlatWindowAbstractIcon
{
	public FlatWindowMaximizeIcon() {
		this( null );
	}

	/** @since 3.2 */
	public FlatWindowMaximizeIcon( String windowStyle ) {
		super( windowStyle );
	}

	@Override
	protected void paintIconAt1x( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
		int iwh = (int) (symbolHeight * scaleFactor);
		int ix = x + ((width - iwh) / 2);
		int iy = y + ((height - iwh) / 2);
		boolean isWindows10 = SystemInfo.isWindows_10_orLater && !SystemInfo.isWindows_11_orLater;
		float thickness = Math.max( isWindows10 ? (int) scaleFactor : (float) scaleFactor, 1 );
		int arc = Math.max( (int) (1.5 * scaleFactor), 2 );

		g.fill( SystemInfo.isWindows_11_orLater
			? FlatUIUtils.createRoundRectangle( ix, iy, iwh, iwh, thickness, arc, arc, arc, arc )
			: FlatUIUtils.createRectangle( ix, iy, iwh, iwh, thickness ) );
	}
}
