/*
 * Copyright 2025 FormDev Software GmbH
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * @author Karl Tauber
 */
public class IJThemesDump
{
	// same as UIDefaultsLoader.KEY_PROPERTIES
	private static final String KEY_PROPERTIES = "FlatLaf.internal.properties";

	public static void enablePropertiesRecording() {
		System.setProperty( KEY_PROPERTIES, "true" );
	}

	public static void install() {
		enablePropertiesRecording();

		UIManager.addPropertyChangeListener( e -> {
			if( "lookAndFeel".equals( e.getPropertyName() ) ) {
				LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
				if( lookAndFeel instanceof IntelliJTheme.ThemeLaf ) {
					IntelliJTheme theme = (lookAndFeel.getClass() == IntelliJTheme.ThemeLaf.class)
						? ((IntelliJTheme.ThemeLaf)lookAndFeel).getTheme()
						: null;
					String name = (theme != null) ? theme.name : lookAndFeel.getClass().getSimpleName();
					File dir = new File( "dumps/properties" );
					dumpProperties( dir, name, UIManager.getLookAndFeelDefaults() );
				}
			}
		} );
	}

	public static void dumpProperties( File dir, String name, UIDefaults defaults ) {
		String content = dumpPropertiesToString( defaults );
		if( content == null )
			return;

		// write to file
		File file = new File( dir, name + ".properties" );
		file.getParentFile().mkdirs();
		try( Writer fileWriter = new OutputStreamWriter(
			new FileOutputStream( file ), StandardCharsets.UTF_8 ) )
		{
			fileWriter.write( content );
		} catch( IOException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	public static String dumpPropertiesToString( UIDefaults defaults ) {
		Properties properties = (Properties) defaults.get( KEY_PROPERTIES );
		if( properties == null )
			return null;

		// dump to string
		StringWriter stringWriter = new StringWriter( 100000 );
		PrintWriter out = new PrintWriter( stringWriter );
		out.printf( "@baseTheme = %s%n", FlatLaf.isLafDark() ? "dark" : "light" );
		AtomicReference<String> lastPrefix = new AtomicReference<>();
		properties.entrySet().stream()
			.sorted( (e1, e2) -> ((String)e1.getKey()).compareTo( (String) e2.getKey() ) )
			.forEach( e -> {
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				String prefix = keyPrefix( key );
				if( !prefix.equals( lastPrefix.get() ) ) {
					lastPrefix.set( prefix );
					out.printf( "%n%n#---- %s ----%n%n", prefix );
				}

				out.printf( "%-50s = %s%n", key, value.replace( ";", "; \\\n\t" ) );
			} );

		return stringWriter.toString().replace( "\r", "" );
	}

	private static String keyPrefix( String key ) {
		int dotIndex = key.indexOf( '.' );
		return (dotIndex > 0)
			? key.substring( 0, dotIndex )
			: key.endsWith( "UI" )
				? key.substring( 0, key.length() - 2 )
				: "";
	}
}
