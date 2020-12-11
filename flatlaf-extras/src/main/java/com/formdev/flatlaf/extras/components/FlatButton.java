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

package com.formdev.flatlaf.extras.components;

import static com.formdev.flatlaf.FlatClientProperties.*;
import javax.swing.JButton;

/**
 * Subclass of {@link JButton} that provides easy access to FlatLaf specific client properties.
 *
 * @author Karl Tauber
 */
public class FlatButton
	extends JButton
	implements FlatComponentExtension
{
	// NOTE: enum names must be equal to allowed strings
	public enum ButtonType { none, square, roundRect, tab, help, toolBarButton };

	/**
	 * Returns type of a button.
	 */
	public ButtonType getButtonType() {
		return getClientPropertyEnumString( BUTTON_TYPE, ButtonType.class, null, ButtonType.none );
	}

	/**
	 * Specifies type of a button.
	 */
	public void setButtonType( ButtonType buttonType ) {
		if( buttonType == ButtonType.none )
			buttonType = null;
		putClientPropertyEnumString( BUTTON_TYPE, buttonType );
	}


	/**
	 * Returns whether the button preferred size will be made square (quadratically).
	 */
	public boolean isSquareSize() {
		return getClientPropertyBoolean( SQUARE_SIZE, false );
	}

	/**
	 * Specifies whether the button preferred size will be made square (quadratically).
	 */
	public void setSquareSize( boolean squareSize ) {
		putClientPropertyBoolean( SQUARE_SIZE, squareSize, false );
	}


	/**
	 * Returns minimum width of a component.
	 */
	public int getMinimumWidth() {
		return getClientPropertyInt( MINIMUM_WIDTH, "Button.minimumWidth" );
	}

	/**
	 * Specifies minimum width of a component.
	 */
	public void setMinimumWidth( int minimumWidth ) {
		putClientProperty( MINIMUM_WIDTH, (minimumWidth >= 0) ? minimumWidth : null );
	}


	/**
	 * Returns minimum height of a component.
	 */
	public int getMinimumHeight() {
		return getClientPropertyInt( MINIMUM_HEIGHT, 0 );
	}

	/**
	 * Specifies minimum height of a component.
	 */
	public void setMinimumHeight( int minimumHeight ) {
		putClientProperty( MINIMUM_HEIGHT, (minimumHeight >= 0) ? minimumHeight : null );
	}
}
