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
import java.awt.Graphics2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Base class for internal frame icons.
 *
 * @uiDefault InternalFrame.buttonHoverBackground		Color
 * @uiDefault InternalFrame.buttonPressedBackground		Color
 * @uiDefault Button.arc								int
 *
 * @author Karl Tauber
 */
public abstract class FlatInternalFrameAbstractIcon
	extends FlatAbstractIcon
{
	private final Color hoverBackground;
	private final Color pressedBackground;
	private final int arc = UIManager.getInt( "Button.arc" );

	public FlatInternalFrameAbstractIcon() {
		this( UIManager.getColor( "InternalFrame.buttonHoverBackground" ),
			UIManager.getColor( "InternalFrame.buttonPressedBackground" ) );
	}

	public FlatInternalFrameAbstractIcon( Color hoverBackground, Color pressedBackground ) {
		super( 16, 16, null );
		this.hoverBackground = hoverBackground;
		this.pressedBackground = pressedBackground;
	}

	protected void paintBackground( Component c, Graphics2D g ) {
		Color background = FlatButtonUI.buttonStateColor( c, null, null, null, hoverBackground, pressedBackground );
		if( background != null ) {
			FlatUIUtils.setColor( g, background, c.getBackground() );
			g.fillRoundRect( 0, 0, width, height, arc, arc );
		}
	}
}
