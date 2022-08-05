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

package com.formdev.flatlaf;

import javax.swing.UIManager;

/**
 * A Flat LaF that has a dark color scheme.
 * <p>
 * The UI defaults are loaded from {@code FlatDarkLaf.properties} and {@code FlatLaf.properties}.
 *
 * @author Karl Tauber
 */
public class FlatDarkLaf
	extends FlatLaf
{
	public static final String NAME = "FlatLaf Dark";

	/**
	 * Sets the application look and feel to this LaF
	 * using {@link UIManager#setLookAndFeel(javax.swing.LookAndFeel)}.
	 *
	 * @since 1.2
	 */
	public static boolean setup() {
		return setup( new FlatDarkLaf() );
	}

	/**
	 * @deprecated use {@link #setup()} instead; this method will be removed in a future version
	 */
	@Deprecated
	public static boolean install() {
		return setup();
	}

	/**
	 * Adds this look and feel to the set of available look and feels.
	 * <p>
	 * Useful if your application uses {@link UIManager#getInstalledLookAndFeels()}
	 * to query available LaFs and display them to the user in a combobox.
	 */
	public static void installLafInfo() {
		installLafInfo( NAME, FlatDarkLaf.class );
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "FlatLaf Dark Look and Feel";
	}

	@Override
	public boolean isDark() {
		return true;
	}
}
