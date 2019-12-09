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
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JMenuItem}.
 *
 * <!-- BasicMenuItemUI -->
 *
 * @uiDefault MenuItem.font								Font
 * @uiDefault MenuItem.background						Color
 * @uiDefault MenuItem.foreground						Color
 * @uiDefault MenuItem.disabledForeground				Color
 * @uiDefault MenuItem.selectionBackground				Color
 * @uiDefault MenuItem.selectionForeground				Color
 * @uiDefault MenuItem.acceleratorForeground			Color
 * @uiDefault MenuItem.acceleratorSelectionForeground	Color
 * @uiDefault MenuItem.acceleratorFont					Font		defaults to MenuItem.font
 * @uiDefault MenuItem.acceleratorDelimiter				String
 * @uiDefault MenuItem.border							Border
 * @uiDefault MenuItem.borderPainted					boolean
 * @uiDefault MenuItem.margin							Insets
 * @uiDefault MenuItem.arrowIcon						Icon
 * @uiDefault MenuItem.checkIcon						Icon
 * @uiDefault MenuItem.opaque							boolean
 * @uiDefault MenuItem.evenHeight						boolean
 *
 * @author Karl Tauber
 */
public class FlatMenuItemUI
	extends BasicMenuItemUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatMenuItemUI();
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
