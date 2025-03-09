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

package com.formdev.flatlaf.demo.intellijthemes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * This tool updates all IntelliJ themes listed in themes.json by downloading
 * them from the source code repositories.
 *
 * @author Karl Tauber
 */
public class IJThemesUpdater
{
	public static void main( String[] args ) {
		IJThemesManager themesManager = new IJThemesManager();
		themesManager.loadBundledThemes();

		for( IJThemeInfo ti : themesManager.bundledThemes ) {
			if( ti.discontinued )
				continue;

			if( ti.sourceCodeUrl == null || ti.sourceCodePath == null ) {
				System.out.println( "    " + ti.name + " NOT downloaded. Needs manual update from release on JetBrains Plugin portal." );
				continue;
			}

			String fromUrl = ti.sourceCodeUrl + "/" + ti.sourceCodePath;
			if( fromUrl.contains( "github.com" ) )
				fromUrl += "?raw=true";
			else if( fromUrl.contains( "gitlab.com" ) )
				fromUrl = fromUrl.replace( "/blob/", "/raw/" );

			String toPath = "../flatlaf-intellij-themes/src/main/resources" + IJThemesPanel.THEMES_PACKAGE + ti.resourceName;

			download( fromUrl, toPath );
		}
	}

	private static void download( String fromUrl, String toPath ) {
		System.out.println( "Download " + fromUrl );

		Path out = new File( toPath ).toPath();
		try {
			URL url = new URL( fromUrl.replace( " ", "%20" ) );
			URLConnection con = url.openConnection();
			Files.copy( con.getInputStream(), out, StandardCopyOption.REPLACE_EXISTING );
		} catch( IOException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}
}
