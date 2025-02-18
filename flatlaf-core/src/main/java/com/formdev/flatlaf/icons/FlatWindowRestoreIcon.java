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
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * "restore" icon for windows (frames and dialogs).
 *
 * @author Karl Tauber
 */
public class FlatWindowRestoreIcon
	extends FlatWindowAbstractIcon
{
	public FlatWindowRestoreIcon() {
		this( null );
	}

	/** @since 3.2 */
	public FlatWindowRestoreIcon( String windowStyle ) {
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
		int arcOuter = (int) (arc + (1.5 * scaleFactor));

		int rwh = (int) ((symbolHeight - 2) * scaleFactor);
		int ro2 = iwh - rwh;

		// upper-right rectangle
		Path2D r1 = SystemInfo.isWindows_11_orLater
			? FlatUIUtils.createRoundRectangle( ix + ro2, iy, rwh, rwh, thickness, arc, arcOuter, arc, arc )
			: FlatUIUtils.createRectangle( ix + ro2, iy, rwh, rwh, thickness );

		// lower-left rectangle
		Path2D r2 = SystemInfo.isWindows_11_orLater
			? FlatUIUtils.createRoundRectangle( ix, iy + ro2, rwh, rwh, thickness, arc, arc, arc, arc )
			: FlatUIUtils.createRectangle( ix, iy + ro2, rwh, rwh, thickness );

		// paint upper-right rectangle
		Area area = new Area( r1 );
		if( SystemInfo.isWindows_11_orLater ) {
			area.subtract( new Area( new Rectangle2D.Float( ix, (float) (iy + scaleFactor), rwh, rwh ) ) );
			area.subtract( new Area( new Rectangle2D.Float( (float) (ix + scaleFactor), iy + ro2, rwh, rwh ) ) );
		} else
			area.subtract( new Area( new Rectangle2D.Float( ix, iy + ro2, rwh, rwh ) ) );
		g.fill( area );

		// paint lower-left rectangle
		g.fill( r2 );
	}
}
