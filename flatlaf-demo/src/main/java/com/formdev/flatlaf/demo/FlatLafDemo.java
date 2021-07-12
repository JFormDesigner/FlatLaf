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

import java.awt.Dimension;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * @author Karl Tauber
 */
public class FlatLafDemo
{
	static final String PREFS_ROOT_PATH = "/flatlaf-demo";
	static final String KEY_TAB = "tab";

	static boolean screenshotsMode = Boolean.parseBoolean( System.getProperty( "flatlaf.demo.screenshotsMode" ) );

	public static void main( String[] args ) {
		// on macOS enable screen menu bar
		if( SystemInfo.isMacOS && System.getProperty( "apple.laf.useScreenMenuBar" ) == null )
			System.setProperty( "apple.laf.useScreenMenuBar", "true" );

		if( FlatLafDemo.screenshotsMode && !SystemInfo.isJava_9_orLater && System.getProperty( "flatlaf.uiScale" ) == null )
			System.setProperty( "flatlaf.uiScale", "2x" );

		SwingUtilities.invokeLater( () -> {
			DemoPrefs.init( PREFS_ROOT_PATH );

			// application specific UI defaults
			FlatLaf.registerCustomDefaultsSource( "com.formdev.flatlaf.demo" );

			// set look and feel
			DemoPrefs.setupLaf( args );

			// install inspectors
			FlatInspector.install( "ctrl shift alt X" );
			FlatUIDefaultsInspector.install( "ctrl shift alt Y" );

			// create frame
			DemoFrame frame = new DemoFrame();

			if( FlatLafDemo.screenshotsMode )
				frame.setPreferredSize( new Dimension( 1660, 840 ) );

			// show frame
			frame.pack();
			frame.setLocationRelativeTo( null );
			frame.setVisible( true );
		} );
	}
}
