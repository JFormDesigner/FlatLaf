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

/**
 * "maximize" icon for windows (frames and dialogs).
 *
 * @author Karl Tauber
 */
public class FlatWindowMaximizeIcon
	extends FlatWindowAbstractIcon
{
	public FlatWindowMaximizeIcon() {
	}

	@Override
	protected void paintIconAt1x( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
		int iwh = (int) (10 * scaleFactor);
		int ix = x + ((width - iwh) / 2);
		int iy = y + ((height - iwh) / 2);
		int thickness = (int) scaleFactor;

		g.fill( FlatUIUtils.createRectangle( ix, iy, iwh, iwh, thickness ) );
	}
}
