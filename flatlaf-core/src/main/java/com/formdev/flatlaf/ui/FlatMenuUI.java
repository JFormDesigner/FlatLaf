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
import javax.swing.plaf.basic.BasicMenuUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JMenu}.
 *
 * <!-- BasicMenuUI -->
 *
 * @uiDefault Menu.font								Font
 * @uiDefault Menu.background						Color
 * @uiDefault Menu.foreground						Color
 * @uiDefault Menu.disabledForeground				Color
 * @uiDefault Menu.selectionBackground				Color
 * @uiDefault Menu.selectionForeground				Color
 * @uiDefault Menu.acceleratorForeground			Color
 * @uiDefault Menu.acceleratorSelectionForeground	Color
 * @uiDefault MenuItem.acceleratorFont				Font		defaults to MenuItem.font
 * @uiDefault MenuItem.acceleratorDelimiter			String
 * @uiDefault Menu.border							Border
 * @uiDefault Menu.borderPainted					boolean
 * @uiDefault Menu.margin							Insets
 * @uiDefault Menu.arrowIcon						Icon
 * @uiDefault Menu.checkIcon						Icon
 * @uiDefault Menu.opaque							boolean
 * @uiDefault Menu.evenHeight						boolean
 * @uiDefault Menu.crossMenuMnemonic				boolean	default is false
 * @uiDefault Menu.useMenuBarBackgroundForTopLevel	boolean	default is false
 * @uiDefault MenuBar.background					Color	used if Menu.useMenuBarBackgroundForTopLevel is true
 *
 * @author Karl Tauber
 */
public class FlatMenuUI
	extends BasicMenuUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatMenuUI();
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
