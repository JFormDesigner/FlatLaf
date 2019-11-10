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

import static com.formdev.flatlaf.util.UIScale.scale;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JRadioButtonMenuItem}.
 *
 * <!-- BasicRadioButtonMenuItemUI -->
 *
 * @uiDefault RadioButtonMenuItem.font								Font
 * @uiDefault RadioButtonMenuItem.background						Color
 * @uiDefault RadioButtonMenuItem.foreground						Color
 * @uiDefault RadioButtonMenuItem.disabledForeground				Color
 * @uiDefault RadioButtonMenuItem.selectionBackground				Color
 * @uiDefault RadioButtonMenuItem.selectionForeground				Color
 * @uiDefault RadioButtonMenuItem.acceleratorForeground				Color
 * @uiDefault RadioButtonMenuItem.acceleratorSelectionForeground	Color
 * @uiDefault MenuItem.acceleratorFont								Font		defaults to MenuItem.font
 * @uiDefault MenuItem.acceleratorDelimiter							String
 * @uiDefault RadioButtonMenuItem.border							Border
 * @uiDefault RadioButtonMenuItem.borderPainted						boolean
 * @uiDefault RadioButtonMenuItem.margin							Insets
 * @uiDefault RadioButtonMenuItem.arrowIcon							Icon
 * @uiDefault RadioButtonMenuItem.checkIcon							Icon
 * @uiDefault RadioButtonMenuItem.opaque							boolean
 * @uiDefault RadioButtonMenuItem.evenHeight						boolean
 *
 * @author Karl Tauber
 */
public class FlatRadioButtonMenuItemUI
	extends BasicRadioButtonMenuItemUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatRadioButtonMenuItemUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		// scale
		defaultTextIconGap = scale( defaultTextIconGap );
	}

	/**
	 * Scale defaultTextIconGap again if iconTextGap property has changed.
	 */
	@Override
	protected PropertyChangeListener createPropertyChangeListener( JComponent c ) {
		PropertyChangeListener superListener = super.createPropertyChangeListener( c );
		return e -> {
			superListener.propertyChange( e );
			if( e.getPropertyName() == "iconTextGap" )
				defaultTextIconGap = scale( defaultTextIconGap );
		};
	}
}
