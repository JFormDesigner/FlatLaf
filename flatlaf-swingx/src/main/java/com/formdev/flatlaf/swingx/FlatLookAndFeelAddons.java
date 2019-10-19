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

package com.formdev.flatlaf.swingx;

import javax.swing.UIManager;
import org.jdesktop.swingx.plaf.basic.BasicLookAndFeelAddons;
import com.formdev.flatlaf.FlatLaf;

/**
 * SwingX LaF addon.
 *
 * This class is required because without this class, the system addon would be used,
 * which may result in wrong UI defaults. (e.g. background of TaskPaneContainer)
 *
 * @author Karl Tauber
 */
public class FlatLookAndFeelAddons
	extends BasicLookAndFeelAddons
{
	@Override
	protected boolean matches() {
		return UIManager.getLookAndFeel() instanceof FlatLaf;
	}
}
