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

/**
 * "iconify" icon for windows (frames and dialogs).
 *
 * @author Karl Tauber
 */
public class FlatWindowIconifyIcon
	extends FlatWindowAbstractIcon
{
	public FlatWindowIconifyIcon() {
		this( null );
	}

	/** @since 3.2 */
	public FlatWindowIconifyIcon( String windowStyle ) {
		super( windowStyle );
	}

	@Override
	protected void paintIconAt1x( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
		int iw = (int) (symbolHeight * scaleFactor);
		int ih = Math.max( (int) scaleFactor, 1 );
		int ix = x + ((width - iw) / 2);
		int iy = y + ((height - ih) / 2);

		g.fillRect( ix, iy, iw, ih );
	}
}
