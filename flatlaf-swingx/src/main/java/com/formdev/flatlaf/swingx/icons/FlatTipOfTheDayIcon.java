/*
 * Copyright 2025 FormDev Software GmbH
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

package com.formdev.flatlaf.swingx.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "light bulb" icon for {@link org.jdesktop.swingx.JXTipOfTheDay}.
 *
 * @uiDefault TipOfTheDay.icon.bulbColor						Color
 * @uiDefault TipOfTheDay.icon.socketColor						Color
 *
 * @author Karl Tauber
 * @since 3.6
 */
public class FlatTipOfTheDayIcon
	extends FlatAbstractIcon
{
	protected final Color bulbColor = UIManager.getColor( "TipOfTheDay.icon.bulbColor" );
	protected final Color socketColor = UIManager.getColor( "TipOfTheDay.icon.socketColor" );

	public FlatTipOfTheDayIcon() {
		super( 24, 24, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/* source: https://intellij-icons.jetbrains.design/#AllIcons-expui-codeInsight-intentionBulb
			<!-- Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license. -->
			<svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
				<rect x="5.70142" y="12" width="4.6" height="1" fill="#6C707E"/>
				<path d="M6 14H10C10 14.5523 9.55228 15 9 15H7C6.44772 15 6 14.5523 6 14Z" fill="#6C707E"/>
				<path fill-rule="evenodd" clip-rule="evenodd" d="M10.8704 9.14748C12.0417 8.27221 12.8 6.87465 12.8 5.3C12.8 2.64903 10.6509 0.5 7.99995 0.5C5.34898 0.5 3.19995 2.64903 3.19995 5.3C3.19995 6.87464 3.95817 8.27218 5.12943 9.14746L5.49994 11H10.4999L10.8704 9.14748Z" fill="#FFAF0F"/>
			</svg>
		*/

		// scale because SVG coordinates are for 16x16 icon, but this icon is 24x24
		g.scale( 1.5, 1.5 );

		g.setColor( socketColor );
		g.fill( new Rectangle2D.Double( 5.70142, 12, 4.6, 1 ) );

		// M6 14H10C10 14.5523 9.55228 15 9 15H7C6.44772 15 6 14.5523 6 14Z
		g.fill( FlatUIUtils.createPath(
			6,14, 10,14,
			FlatUIUtils.CURVE_TO, 10,14.5523, 9.55228,15, 9,15,
			7,15,
			FlatUIUtils.CURVE_TO, 6.44772,15, 6,14.5523, 6,14 ) );

		g.setColor( bulbColor );

		// M10.8704 9.14748C12.0417 8.27221 12.8 6.87465 12.8 5.3C12.8 2.64903 10.6509 0.5 7.99995 0.5C5.34898 0.5 3.19995 2.64903 3.19995 5.3C3.19995 6.87464 3.95817 8.27218 5.12943 9.14746L5.49994 11H10.4999L10.8704 9.14748Z
		g.fill( FlatUIUtils.createPath(
			10.8704,9.14748,
			FlatUIUtils.CURVE_TO, 12.0417,8.27221, 12.8,6.87465, 12.8,5.3,
			FlatUIUtils.CURVE_TO, 12.8,2.64903, 10.6509,0.5, 7.99995,0.5,
			FlatUIUtils.CURVE_TO, 5.34898,0.5, 3.19995,2.64903, 3.19995,5.3,
			FlatUIUtils.CURVE_TO, 3.19995,6.87464, 3.95817,8.27218, 5.12943,9.14746,
			5.49994,11, 10.4999,11, 10.8704,9.14748 ) );
	}
}
