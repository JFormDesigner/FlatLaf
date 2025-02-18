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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * "close" icon for windows (frames and dialogs).
 *
 * @uiDefault TitlePane.closeBackground					Color	optional
 * @uiDefault TitlePane.closeForeground					Color	optional
 * @uiDefault TitlePane.closeInactiveBackground			Color	optional
 * @uiDefault TitlePane.closeInactiveForeground			Color	optional
 * @uiDefault TitlePane.closeHoverBackground			Color	optional
 * @uiDefault TitlePane.closeHoverForeground			Color	optional
 * @uiDefault TitlePane.closePressedBackground			Color	optional
 * @uiDefault TitlePane.closePressedForeground			Color	optional
 *
 * @author Karl Tauber
 */
public class FlatWindowCloseIcon
	extends FlatWindowAbstractIcon
{
	public FlatWindowCloseIcon() {
		this( null );
	}

	/** @since 3.2 */
	public FlatWindowCloseIcon( String windowStyle ) {
		super( windowStyle,
			FlatUIUtils.getSubUIColor( "TitlePane.closeBackground", windowStyle ),
			FlatUIUtils.getSubUIColor( "TitlePane.closeForeground", windowStyle ),
			FlatUIUtils.getSubUIColor( "TitlePane.closeInactiveBackground", windowStyle ),
			FlatUIUtils.getSubUIColor( "TitlePane.closeInactiveForeground", windowStyle ),
			FlatUIUtils.getSubUIColor( "TitlePane.closeHoverBackground", windowStyle ),
			FlatUIUtils.getSubUIColor( "TitlePane.closeHoverForeground", windowStyle ),
			FlatUIUtils.getSubUIColor( "TitlePane.closePressedBackground", windowStyle ),
			FlatUIUtils.getSubUIColor( "TitlePane.closePressedForeground", windowStyle ) );
	}

	@Override
	protected void paintIconAt1x( Graphics2D g, int x, int y, int width, int height, double scaleFactor ) {
		int iwh = (int) (symbolHeight * scaleFactor);
		int ix = x + ((width - iwh) / 2);
		int iy = y + ((height - iwh) / 2);
		int ix2 = ix + iwh - 1;
		int iy2 = iy + iwh - 1;
		boolean isWindows10 = SystemInfo.isWindows_10_orLater && !SystemInfo.isWindows_11_orLater;
		float thickness = Math.max( isWindows10 ? (int) scaleFactor : (float) scaleFactor, 1 );

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD, 4 );
		path.moveTo( ix, iy );
		path.lineTo( ix2, iy2 );
		path.moveTo( ix, iy2 );
		path.lineTo( ix2, iy );
		g.setStroke( new BasicStroke( thickness ) );
		g.draw( path );
	}
}
