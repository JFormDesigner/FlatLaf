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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Base class for internal frame icons.
 *
 * @uiDefault InternalFrame.buttonSize					Dimension
 * @uiDefault InternalFrame.buttonHoverBackground		Color
 * @uiDefault InternalFrame.buttonPressedBackground		Color
 *
 * @author Karl Tauber
 */
public abstract class FlatInternalFrameAbstractIcon
	extends FlatAbstractIcon
{
	private final Color hoverBackground;
	private final Color pressedBackground;

	public FlatInternalFrameAbstractIcon() {
		this( UIManager.getDimension( "InternalFrame.buttonSize" ),
			UIManager.getColor( "InternalFrame.buttonHoverBackground" ),
			UIManager.getColor( "InternalFrame.buttonPressedBackground" ) );
	}

	public FlatInternalFrameAbstractIcon( Dimension size, Color hoverBackground, Color pressedBackground ) {
		super( size.width, size.height, null );
		this.hoverBackground = hoverBackground;
		this.pressedBackground = pressedBackground;
	}

	protected void paintBackground( Component c, Graphics2D g ) {
		Color background = FlatButtonUI.buttonStateColor( c, null, null, null, hoverBackground, pressedBackground );
		if( background != null ) {
			g.setColor( FlatUIUtils.deriveColor( background, c.getBackground() ) );
			g.fillRect( 0, 0, width, height );
		}
	}
}
