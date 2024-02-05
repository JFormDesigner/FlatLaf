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

package com.formdev.flatlaf.testing.uidefaults;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.testing.FlatTestLaf;

/**
 * Collects all FlatLaf UI defaults keys and dumps them to a file.
 *
 * @author Karl Tauber
 */
public class UIDefaultsKeysDump
{
	public static void main( String[] args ) {
		Locale.setDefault( Locale.ENGLISH );
		System.setProperty( "sun.java2d.uiScale", "1x" );
		System.setProperty( FlatSystemProperties.UI_SCALE, "1x" );

		File keysFile = new File( "../flatlaf-theme-editor/src/main/resources/com/formdev/flatlaf/themeeditor/FlatLafUIKeys.txt" );

		// load existing keys file
		HashSet<String> keys = new HashSet<>();
		try( BufferedReader reader = new BufferedReader( new InputStreamReader(
			new FileInputStream( keysFile ), StandardCharsets.UTF_8 ) ) )
		{
			String key;
			while( (key = reader.readLine()) != null ) {
				keys.add( key );
			}
		} catch( IOException ex ) {
			ex.printStackTrace();
		}

		// collect keys used in Lafs
		collectKeys( FlatLightLaf.class.getName(), keys );
		collectKeys( FlatDarkLaf.class.getName(), keys );
		collectKeys( FlatIntelliJLaf.class.getName(), keys );
		collectKeys( FlatDarculaLaf.class.getName(), keys );
		collectKeys( FlatTestLaf.class.getName(), keys );

		// remove unused keys (defined in BasicLookAndFeel)
		keys.remove( "Button.textIconGap" );
		keys.remove( "Button.textShiftOffset" );
		keys.remove( "CheckBox.textIconGap" );
		keys.remove( "CheckBox.textShiftOffset" );
		keys.remove( "RadioButton.textIconGap" );
		keys.remove( "RadioButton.textShiftOffset" );
		keys.remove( "TabbedPane.contentOpaque" );
		keys.remove( "TabbedPane.selectedTabPadInsets" );
		keys.remove( "TabbedPane.shadow" );
		keys.remove( "TabbedPane.tabsOverlapBorder" );
		keys.remove( "ToggleButton.textIconGap" );
		keys.remove( "ToggleButton.textShiftOffset" );

		// write key file
		try( Writer fileWriter = new BufferedWriter( new OutputStreamWriter(
			new FileOutputStream( keysFile ), StandardCharsets.UTF_8 ) ) )
		{
			String[] sortedKeys = keys.toArray( new String[keys.size()] );
			Arrays.sort( sortedKeys );
			for( String key : sortedKeys ) {
				fileWriter.write( key );
				fileWriter.write( "\n" );
			}
		} catch( IOException ex ) {
			ex.printStackTrace();
		}
	}

	private static void collectKeys( String lookAndFeelClassName, HashSet<String> keys ) {
		try {
			UIManager.setLookAndFeel( lookAndFeelClassName );
		} catch( Exception ex ) {
			ex.printStackTrace();
			return;
		}

		UIDefaults defaults = UIManager.getLookAndFeel().getDefaults();

		for( Object key : defaults.keySet() ) {
			if( key instanceof String && !ignoreKey( (String) key ) )
				keys.add( (String) key );
		}
	}

	private static boolean ignoreKey( String key ) {
		return key.startsWith( "FlatLaf.internal." ) ||
			key.equals( "Menu.acceleratorFont" ) ||
			key.equals( "CheckBoxMenuItem.acceleratorFont" ) ||
			key.equals( "RadioButtonMenuItem.acceleratorFont" );
	}
}
