/*
 * Copyright 2021 FormDev Software GmbH
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

package com.formdev.flatlaf.jideoss.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import com.formdev.flatlaf.ui.FlatPopupMenuUI;
import com.jidesoft.plaf.LookAndFeelFactory;

/**
 * Provides the Flat LaF UI delegate for {@link com.jidesoft.swing.JidePopupMenu}.
 */
public class FlatJidePopupMenuUI
	extends FlatPopupMenuUI
{
	public static ComponentUI createUI( JComponent c ) {
		// usually JIDE would invoke this in JidePopupMenu.updateUI(),
		// but it does not because FlatLaf already has added the UI class to the UI defaults
		LookAndFeelFactory.installJideExtension();

		return new FlatJidePopupMenuUI();
	}

	@Override
	public Popup getPopup( JPopupMenu popupMenu, int x, int y ) {
		// not using BasicJidePopupMenuUI.addScrollPaneIfNecessary() anymore because
		// FlatLaf supports menu scrolling that works better than JIDE menu scrolling
		// (support mouse wheel scrolling, scales arrows)
		return super.getPopup( popupMenu, x, y );
	}
}
