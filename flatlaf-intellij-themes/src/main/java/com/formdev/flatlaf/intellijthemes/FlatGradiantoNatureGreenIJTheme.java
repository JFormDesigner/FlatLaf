/*
 * Copyright 2020 FormDev Software GmbH
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

package com.formdev.flatlaf.intellijthemes;

//
// DO NOT MODIFY
// Generated with com.formdev.flatlaf.demo.intellijthemes.IJThemesClassGenerator
//

import com.formdev.flatlaf.IntelliJTheme;

/**
 * @author Karl Tauber
 */
public class FlatGradiantoNatureGreenIJTheme
	extends IntelliJTheme.ThemeLaf
{
	public static final String NAME = "Gradianto Nature Green";

	public static boolean setup() {
		try {
			return setup( new FlatGradiantoNatureGreenIJTheme() );
		} catch( RuntimeException ex ) {
			return false;
		}
	}

	/**
	 * @deprecated use {@link #setup()} instead; this method will be removed in a future version
	 */
	@Deprecated
	public static boolean install() {
		return setup();
	}

	public static void installLafInfo() {
		installLafInfo( NAME, FlatGradiantoNatureGreenIJTheme.class );
	}

	public FlatGradiantoNatureGreenIJTheme() {
		super( Utils.loadTheme( "Gradianto_Nature_Green.theme.json" ) );
	}

	@Override
	public String getName() {
		return NAME;
	}
}
