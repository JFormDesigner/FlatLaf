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

package com.formdev.flatlaf.ui;

import java.awt.Color;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JToggleButton}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault Component.focusWidth						int
 * @uiDefault ToggleButton.arc							int
 * @uiDefault ToggleButton.disabledText					Color
 * @uiDefault ToggleButton.toolbar.hoverBackground		Color
 * @uiDefault ToggleButton.toolbar.pressedBackground	Color
 * @uiDefault ToggleButton.selectedBackground			Color
 * @uiDefault ToggleButton.selectedForeground			Color
 *
 * @author Karl Tauber
 */
public class FlatToggleButtonUI
	extends FlatButtonUI
{
	protected Color selectedBackground;
	protected Color selectedForeground;
	protected Color disabledSelectedBackground;

	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatToggleButtonUI();
		return instance;
	}

	@Override
	protected String getPropertyPrefix() {
		return "ToggleButton.";
	}

	@Override
	protected void installDefaults( AbstractButton b ) {
		super.installDefaults( b );

		selectedBackground = UIManager.getColor( "ToggleButton.selectedBackground" );
		selectedForeground = UIManager.getColor( "ToggleButton.selectedForeground" );
		disabledSelectedBackground = UIManager.getColor( "ToggleButton.disabledSelectedBackground" );
	}

	@Override
	protected Color getBackground( JComponent c ) {
		ButtonModel model = ((AbstractButton)c).getModel();

		if( model.isSelected() ) {
			return isToolBarButton( c )
				? toolbarPressedBackground
				: (c.isEnabled() ? selectedBackground : disabledSelectedBackground);
		}

		return super.getBackground( c );
	}

	@Override
	protected Color getForeground( JComponent c ) {
		ButtonModel model = ((AbstractButton)c).getModel();

		if( model.isSelected() && !isToolBarButton( c ) )
			return selectedForeground;

		return super.getForeground( c );
	}
}
