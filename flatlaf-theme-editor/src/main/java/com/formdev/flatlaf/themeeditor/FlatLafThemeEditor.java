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

package com.formdev.flatlaf.themeeditor;

import com.formdev.flatlaf.util.SystemInfo;

/**
 * FlatLaf Theme Editor
 *
 * @author Karl Tauber
 */
public class FlatLafThemeEditor
{
	public static void main( String[] args ) {
		// macOS  (see https://www.formdev.com/flatlaf/macos/)
		if( SystemInfo.isMacOS ) {
			// enable screen menu bar
			// (moves menu bar from JFrame window to top of screen)
			System.setProperty( "apple.laf.useScreenMenuBar", "true" );

			// application name used in screen menu bar
			// (in first menu after the "apple" menu)
			System.setProperty( "apple.awt.application.name", "FlatLaf Theme Editor" );

			// appearance of window title bars
			// possible values:
			//   - "system": use current macOS appearance (light or dark)
			//   - "NSAppearanceNameAqua": use light appearance
			//   - "NSAppearanceNameDarkAqua": use dark appearance
			// (must be set on main thread and before AWT/Swing is initialized;
			//  setting it on AWT thread does not work)
			System.setProperty( "apple.awt.application.appearance", "system" );
		}

		FlatThemeFileEditor.launch( args );
	}
}
