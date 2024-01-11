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
import java.awt.Color;
import javax.swing.*;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;

/**
 * Subclass of {@link JToggleButton} that provides easy access to FlatLaf specific client properties.
 *
 * @author Karl Tauber
 */
public class FlatToggleButton
	extends JToggleButton
	implements FlatComponentExtension, FlatStyleableComponent
{
	/**
	 * Returns type of button.
	 */
	public ButtonType getButtonType() {
		return getClientPropertyEnumString( BUTTON_TYPE, ButtonType.class, null, ButtonType.none );
	}

	/**
	 * Specifies type of button.
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
		return getClientPropertyInt( MINIMUM_WIDTH, "ToggleButton.minimumWidth" );
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


	/**
	 * Returns the outline color of the component border.
	 */
	public Object getOutline() {
		return getClientProperty( OUTLINE );
	}

	/**
	 * Specifies the outline color of the component border.
	 * <p>
	 * Allowed Values are:
	 * <ul>
	 *     <li>{@code null}
	 *     <li>string {@code "error"}
	 *     <li>string {@code "warning"}
	 *     <li>any color (type {@link Color})
	 *     <li>an array of two colors (type {@link Color}[2]) where the first color
	 *         is for focused state and the second for unfocused state
	 * </ul>
	 */
	public void setOutline( Object outline ) {
		putClientProperty( OUTLINE, outline );
	}

	/**
	 * Returns placement of underline if toggle button type is {@link ButtonType#tab}.
	 * If underline placement is not specified, returns {@link #BOTTOM} as the default
	 * value.
	 *
	 * @since 2.3
	 */
	public int getTabUnderlinePlacement() {
		return getClientPropertyInt( TAB_BUTTON_UNDERLINE_PLACEMENT, BOTTOM );
	}

	/**
	 * Specifies placement of underline if toggle button type is {@link ButtonType#tab}.
	 *
	 * @param placement  One of the following constants defined in SwingConstants:
	 *                   {@link #TOP}, {@link #LEFT}, {@link #BOTTOM}, or {@link #RIGHT}.
	 * @since 2.3
	 */
	public void setTabUnderlinePlacement( int placement ) {
		putClientProperty( TAB_BUTTON_UNDERLINE_PLACEMENT, (placement >= 0) ? placement : null );
	}

	/**
	 * Returns thickness of underline if toggle button type is {@link ButtonType#tab}.
	 */
	public int getTabUnderlineHeight() {
		return getClientPropertyInt( TAB_BUTTON_UNDERLINE_HEIGHT, "ToggleButton.tab.underlineHeight" );
	}

	/**
	 * Specifies thickness of underline if toggle button type is {@link ButtonType#tab}.
	 */
	public void setTabUnderlineHeight( int tabUnderlineHeight ) {
		putClientProperty( TAB_BUTTON_UNDERLINE_HEIGHT, (tabUnderlineHeight >= 0) ? tabUnderlineHeight : null );
	}


	/**
	 * Returns color of underline if toggle button type is {@link ButtonType#tab}.
	 */
	public Color getTabUnderlineColor() {
		return getClientPropertyColor( TAB_BUTTON_UNDERLINE_COLOR, "ToggleButton.tab.underlineColor" );
	}

	/**
	 * Specifies color of underline if toggle button type is {@link ButtonType#tab}.
	 */
	public void setTabUnderlineColor( Color tabUnderlineColor ) {
		putClientProperty( TAB_BUTTON_UNDERLINE_COLOR, tabUnderlineColor );
	}


	/**
	 * Returns background color if selected and toggle button type is {@link ButtonType#tab}.
	 */
	public Color getTabSelectedBackground() {
		return getClientPropertyColor( TAB_BUTTON_SELECTED_BACKGROUND, "ToggleButton.tab.selectedBackground" );
	}

	/**
	 * Specifies background color if selected and toggle button type is {@link ButtonType#tab}.
	 */
	public void setTabSelectedBackground( Color tabSelectedBackground ) {
		putClientProperty( TAB_BUTTON_SELECTED_BACKGROUND, tabSelectedBackground );
	}
}
