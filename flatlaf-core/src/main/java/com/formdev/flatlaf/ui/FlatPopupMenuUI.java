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

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPopupMenu}.
 *
 * <!-- BasicPopupMenuUI -->
 *
 * @uiDefault PopupMenu.font							Font
 * @uiDefault PopupMenu.background						Color
 * @uiDefault PopupMenu.foreground						Color
 * @uiDefault PopupMenu.border							Border
 *
 * @author Karl Tauber
 */
public class FlatPopupMenuUI
	extends BasicPopupMenuUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatPopupMenuUI();
	}
}
