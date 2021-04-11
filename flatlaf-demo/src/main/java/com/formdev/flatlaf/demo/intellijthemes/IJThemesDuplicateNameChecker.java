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

package com.formdev.flatlaf.demo.intellijthemes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import com.formdev.flatlaf.json.Json;
import com.formdev.flatlaf.json.ParseException;

/**
 * This tool checks whether there are duplicate name fields in all theme .json files.
 *
 * This is important for following file, where the name is used for theme specific UI defaults:
 *   flatlaf-core/src/main/resources/com/formdev/flatlaf/IntelliJTheme$ThemeLaf.properties
 *
 * @author Karl Tauber
 */
public class IJThemesDuplicateNameChecker
{
	public static void main( String[] args ) {
		IJThemesManager themesManager = new IJThemesManager();
		themesManager.loadBundledThemes();

		HashSet<String> names = new HashSet<>();
		for( IJThemeInfo ti : themesManager.bundledThemes ) {
			if( ti.sourceCodeUrl == null || ti.sourceCodePath == null )
				continue;

			String jsonPath = "../flatlaf-intellij-themes/src/main/resources" + IJThemesPanel.THEMES_PACKAGE + ti.resourceName;
			String name;
			try {
				name = readNameFromJson( jsonPath );
			} catch( IOException ex ) {
				System.err.println( "Failed to read '" + jsonPath + "'" );
				continue;
			}

			if( names.contains( name ) )
				System.out.println( "Duplicate name '" + name + "'" );
			names.add( name );
		}
	}

	private static String readNameFromJson( String jsonPath ) throws IOException {
		try( Reader reader = new InputStreamReader( new FileInputStream( jsonPath ), StandardCharsets.UTF_8 ) ) {
			@SuppressWarnings( "unchecked" )
			Map<String, Object> json = (Map<String, Object>) Json.parse( reader );
			return (String) json.get( "name" );
		} catch( ParseException ex ) {
			throw new IOException( ex.getMessage(), ex );
		}
	}
}
