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

import java.io.FileInputStream;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.demo.intellijthemes.IJThemesPanel;

/**
 * @author Karl Tauber
 */
public class DemoPrefs
{
	public static final String KEY_LAF = "laf";
	public static final String KEY_LAF_INTELLIJ_THEME = "lafIntelliJTheme";

	public static final String RESOURCE_PREFIX = "res:";
	public static final String FILE_PREFIX = "file:";

	public static final String INTELLIJ_THEME_UI_KEY = "__FlatLaf.demo.intelliJTheme";

	private static Preferences state;

	public static Preferences getState() {
		return state;
	}

	public static void init( String rootPath ) {
		state = Preferences.userRoot().node( rootPath );
	}

	public static void initLaf( String[] args ) {
		// set look and feel
		try {
			if( args.length > 0 )
				UIManager.setLookAndFeel( args[0] );
			else {
				String lafClassName = state.get( KEY_LAF, FlatLightLaf.class.getName() );
				if( IntelliJTheme.ThemeLaf.class.getName().equals( lafClassName ) ) {
					String intelliJTheme = state.get( KEY_LAF_INTELLIJ_THEME, "" );
					if( intelliJTheme.startsWith( RESOURCE_PREFIX ) )
						IntelliJTheme.install( IJThemesPanel.class.getResourceAsStream( intelliJTheme.substring( RESOURCE_PREFIX.length() ) ) );
					else if( intelliJTheme.startsWith( FILE_PREFIX ) )
					    FlatLaf.install( IntelliJTheme.createLaf( new FileInputStream( intelliJTheme.substring( FILE_PREFIX.length() ) ) ) );
					else
						FlatLightLaf.install();

					if( !intelliJTheme.isEmpty() )
						UIManager.getLookAndFeelDefaults().put( INTELLIJ_THEME_UI_KEY, intelliJTheme );
				} else
					UIManager.setLookAndFeel( lafClassName );
			}
		} catch( Exception ex ) {
			ex.printStackTrace();

			// fallback
			FlatLightLaf.install();
		}

		// remember active look and feel
		UIManager.addPropertyChangeListener( e -> {
			if( "lookAndFeel".equals( e.getPropertyName() ) )
				state.put( KEY_LAF, UIManager.getLookAndFeel().getClass().getName() );
		} );
	}
}
