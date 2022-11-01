/*
 * Copyright 2022 FormDev Software GmbH
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

package com.formdev.flatlaf.themes;

import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;

/**
 * A Flat LaF that imitates macOS dark look.
 * <p>
 * The UI defaults are loaded from {@code FlatMacDarkLaf.properties},
 * {@code FlatDarkLaf.properties} and {@code FlatLaf.properties}.
 *
 * @author Karl Tauber
 * @since 3
 */
public class FlatMacDarkLaf
	extends FlatDarkLaf
{
	public static final String NAME = "FlatLaf macOS Dark";

	/**
	 * Sets the application look and feel to this LaF
	 * using {@link UIManager#setLookAndFeel(javax.swing.LookAndFeel)}.
	 */
	public static boolean setup() {
		return setup( new FlatMacDarkLaf() );
	}

	/**
	 * Adds this look and feel to the set of available look and feels.
	 * <p>
	 * Useful if your application uses {@link UIManager#getInstalledLookAndFeels()}
	 * to query available LaFs and display them to the user in a combobox.
	 */
	public static void installLafInfo() {
		installLafInfo( NAME, FlatMacDarkLaf.class );
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "FlatLaf macOS Dark Look and Feel";
	}

	@Override
	public boolean isDark() {
		return true;
	}
}
