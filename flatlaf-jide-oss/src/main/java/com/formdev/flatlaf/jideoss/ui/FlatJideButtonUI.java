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

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.basic.BasicJideButtonUI;

/**
 * Provides the Flat LaF UI delegate for {@link com.jidesoft.swing.JideButton}.
 *
 * @author Karl Tauber
 * @since 1.1
 */
public class FlatJideButtonUI
	extends BasicJideButtonUI
{
	public static ComponentUI createUI( JComponent c ) {
		// usually JIDE would invoke this in JideButton.updateUI(),
		// but it does not because FlatLaf already has added the UI class to the UI defaults
		LookAndFeelFactory.installJideExtension();

		return new FlatJideButtonUI();
	}
}
