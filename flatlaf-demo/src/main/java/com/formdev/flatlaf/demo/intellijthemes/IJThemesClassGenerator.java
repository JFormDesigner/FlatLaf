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

package com.formdev.flatlaf.demo.intellijthemes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * This tool creates look and feel classes for all themes listed in themes.json.
 *
 * @author Karl Tauber
 */
public class IJThemesClassGenerator
{
	public static void main( String[] args ) {
		IJThemesManager themesManager = new IJThemesManager();
		themesManager.loadBundledThemes();

		String toPath = "../flatlaf-intellij-themes/src/main/java" + IJThemesPanel.THEMES_PACKAGE + "..";

		StringBuilder allInfos = new StringBuilder();
		StringBuilder markdownTable = new StringBuilder();
		markdownTable.append( "Name | Class\n" );
		markdownTable.append( "-----|------\n" );

		for( IJThemeInfo ti : themesManager.bundledThemes ) {
			if( ti.sourceCodeUrl == null )
				continue;

			generateClass( ti, toPath, allInfos, markdownTable );
		}

		Path out = new File( toPath, "FlatAllIJThemes.java" ).toPath();
		String allThemes = (CLASS_HEADER + ALL_THEMES_TEMPLATE)
			.replace( "${subPackage}", "" )
			.replace( "${allInfos}", allInfos );
		writeFile( out, allThemes );

		System.out.println( markdownTable );
	}

	private static void generateClass( IJThemeInfo ti, String toPath, StringBuilder allInfos, StringBuilder markdownTable ) {
		String resourceName = ti.resourceName;
		String resourcePath = null;
		int resSep = resourceName.indexOf( '/' );
		if( resSep >= 0 ) {
			resourcePath = resourceName.substring( 0, resSep );
			resourceName = resourceName.substring( resSep + 1 );
		}

		String name = ti.name;
		int nameSep = name.indexOf( '/' );
		if( nameSep >= 0 )
			name = name.substring( nameSep + 1 ).trim();

		String themeName = name;
		StringBuilder buf = new StringBuilder();
		if( "material-theme-ui-lite".equals( resourcePath ) ) {
			themeName += " (Material)";
			buf.append( "MT" );
		}

		for( String n : name.split( "[ \\-]" ) ) {
			if( n.length() == 0 || n.equals( "-" ) )
				continue;

			if( Character.isUpperCase( n.charAt( 0 ) ) )
				buf.append( n );
			else
				buf.append( Character.toUpperCase( n.charAt( 0 ) ) ).append( n.substring( 1 ) );
		}

		String subPackage = (resourcePath != null) ? '.' + resourcePath.replace( "-", "" ) : "";
		String themeClass = "Flat" + buf + "IJTheme";
		String themeFile = resourceName;

		String classBody = (CLASS_HEADER + CLASS_TEMPLATE)
			.replace( "${subPackage}", subPackage )
			.replace( "${themeClass}", themeClass )
			.replace( "${themeFile}", themeFile )
			.replace( "${themeName}", themeName );

		File toDir = new File( toPath );
		if( resourcePath != null )
			toDir = new File( toDir, resourcePath.replace( "-", "" ) );

		Path out = new File( toDir, themeClass + ".java" ).toPath();
		writeFile( out, classBody );

		if( allInfos.length() > 0 )
			allInfos.append( '\n' );
		allInfos.append( THEME_TEMPLATE
			.replace( "${subPackage}", subPackage )
			.replace( "${themeClass}", themeClass )
			.replace( "${themeName}", themeName )
			.replace( "${dark}", Boolean.toString( ti.dark ) ) );

		markdownTable.append( String.format( "[%s](%s) | `com.formdev.flatlaf.intellijthemes%s.%s`\n",
			themeName, ti.sourceCodeUrl, subPackage, themeClass ) );
	}

	private static void writeFile( Path out, String content ) {
		try {
			Files.write( out, content.getBytes( StandardCharsets.ISO_8859_1 ),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING );
		} catch( IOException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	private static final String CLASS_HEADER =
		"/*\n" +
		" * Copyright 2020 FormDev Software GmbH\n" +
		" *\n" +
		" * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
		" * you may not use this file except in compliance with the License.\n" +
		" * You may obtain a copy of the License at\n" +
		" *\n" +
		" *     https://www.apache.org/licenses/LICENSE-2.0\n" +
		" *\n" +
		" * Unless required by applicable law or agreed to in writing, software\n" +
		" * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
		" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
		" * See the License for the specific language governing permissions and\n" +
		" * limitations under the License.\n" +
		" */\n" +
		"\n" +
		"package com.formdev.flatlaf.intellijthemes${subPackage};\n" +
		"\n" +
		"//\n" +
		"// DO NOT MODIFY\n" +
		"// Generated with com.formdev.flatlaf.demo.intellijthemes.IJThemesClassGenerator\n" +
		"//\n" +
		"\n";

	private static final String CLASS_TEMPLATE =
		"import com.formdev.flatlaf.IntelliJTheme;\n" +
		"\n" +
		"/**\n" +
		" * @author Karl Tauber\n" +
		" */\n" +
		"public class ${themeClass}\n" +
		"	extends IntelliJTheme.ThemeLaf\n" +
		"{\n" +
		"	public static final String NAME = \"${themeName}\";\n" +
		"\n" +
		"	public static boolean setup() {\n" +
		"		try {\n" +
		"			return setup( new ${themeClass}() );\n" +
		"		} catch( RuntimeException ex ) {\n" +
		"			return false;\n" +
		"		}\n" +
		"	}\n" +
		"\n" +
		"	public static void installLafInfo() {\n" +
		"		installLafInfo( NAME, ${themeClass}.class );\n" +
		"	}\n" +
		"\n" +
		"	public ${themeClass}() {\n" +
		"		super( Utils.loadTheme( \"${themeFile}\" ) );\n" +
		"	}\n" +
		"\n" +
		"	@Override\n" +
		"	public String getName() {\n" +
		"		return NAME;\n" +
		"	}\n" +
		"}\n";

	private static final String ALL_THEMES_TEMPLATE =
		"import javax.swing.UIManager.LookAndFeelInfo;\n" +
		"\n" +
		"/**\n" +
		" * @author Karl Tauber\n" +
		" */\n" +
		"public class FlatAllIJThemes\n" +
		"{\n" +
		"	public static final FlatIJLookAndFeelInfo[] INFOS = {\n" +
		"${allInfos}\n" +
		"	};\n" +
		"\n" +
		"	//---- class FlatIJLookAndFeelInfo ----------------------------------------\n" +
		"\n" +
		"	public static class FlatIJLookAndFeelInfo\n" +
		"		extends LookAndFeelInfo\n" +
		"	{\n" +
		"		private final boolean dark;\n" +
		"\n" +
		"		public FlatIJLookAndFeelInfo( String name, String className, boolean dark ) {\n" +
		"			super( name, className );\n" +
		"			this.dark = dark;\n" +
		"		}\n" +
		"\n" +
		"		public boolean isDark() {\n" +
		"			return dark;\n" +
		"		}\n" +
		"	}\n" +
		"}\n";

	private static final String THEME_TEMPLATE =
		"		new FlatIJLookAndFeelInfo( \"${themeName}\", \"com.formdev.flatlaf.intellijthemes${subPackage}.${themeClass}\", ${dark} ),";
}
