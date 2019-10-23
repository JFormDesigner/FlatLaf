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

package com.formdev.flatlaf.demo;

import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * @author Karl Tauber
 */
public class FlatLafDemo
{
	static final String PREFS_ROOT_PATH = "/flatlaf-demo";
	static final String KEY_LAF = "laf";
	static final String KEY_TAB = "tab";

	static Preferences prefs;

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			prefs = Preferences.userRoot().node( PREFS_ROOT_PATH );

			// set look and feel
			try {
				if( args.length > 0 )
					UIManager.setLookAndFeel( args[0] );
				else {
					String lafClassName = prefs.get( KEY_LAF, FlatLightLaf.class.getName() );
					UIManager.setLookAndFeel( lafClassName );
				}
			} catch( Exception ex ) {
				ex.printStackTrace();

				// fallback
				FlatLightLaf.install();
			}

			// create frame
			DemoFrame frame = new DemoFrame();

			// show frame
			frame.pack();
			frame.setLocationRelativeTo( null );
			frame.setVisible( true );
		} );
	}
}
