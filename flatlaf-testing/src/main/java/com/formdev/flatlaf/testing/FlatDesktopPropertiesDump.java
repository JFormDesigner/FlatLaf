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

package com.formdev.flatlaf.testing;

import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import javax.swing.SwingUtilities;

/**
 * Dumps desktop properties.
 *
 * @author Karl Tauber
 */
public class FlatDesktopPropertiesDump
{
	public static void main( String[] args ) {
		System.setProperty( "line.separator", "\n" );

		SwingUtilities.invokeLater( () -> {
			printDesktopProperties();
		} );
	}

	private static void printDesktopProperties() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		String osName = System.getProperty( "os.name" ).toLowerCase( Locale.ENGLISH );

		// Java scale factor
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice().getDefaultConfiguration();
		double javaScale = gc.getDefaultTransform().getScaleX();
		float fontScale;

		double screenScale = javaScale;
		float textScale = 1;

		if( osName.startsWith( "windows" ) ) {
			// text scale factor
			Font winFont = (Font) toolkit.getDesktopProperty( "win.messagebox.font" );
			fontScale = roundToQuater( winFont.getSize() / 12f );
			textScale = fontScale;

			// Java 8
			if( System.getProperty( "java.version" ).startsWith( "1.8" ) ) {
				// screen scale factor
				Font winFont2 = (Font) toolkit.getDesktopProperty( "win.defaultGUI.font" );
				screenScale = roundToQuater( winFont2.getSize() / 11f );

				// text scale factor
				textScale = roundToQuater( textScale / (float) screenScale );
			}
		} else
			throw new RuntimeException( "OS not supported" );

		File dir = new File( "dumps/desktop-properties" );
		dir.mkdirs();

		String osPrefix = osName.startsWith( "windows" ) ? "win"
			: osName.startsWith( "mac" ) ? "mac"
			: osName.startsWith( "linux" ) ? "linux"
			: "unknown";
		String javaVersion = System.getProperty( "java.version" );

		File file = new File( dir, "desktop-properties-" + osPrefix + "-" + javaVersion
			+ "--" + screenScale + "s-" + textScale + "t.txt" );

		try( PrintStream out = new PrintStream( file ) ) {
			out.println( "Java version:   " + System.getProperty( "java.version" ) );
			out.println( "OS:             " + System.getProperty( "os.name" ) );
			out.println();

			out.println( "Screen scale:   " + screenScale );
			out.println( "Text scale:     " + textScale );
			out.println();

			out.println( "Java scale:     " + javaScale );
			out.println( "Font scale:     " + fontScale );
			out.println();

			String[] winPropNames = (String[]) toolkit.getDesktopProperty( "win.propNames" );
			for( String propName : winPropNames ) {
				Object value = toolkit.getDesktopProperty( propName );
				out.printf( "%-40s %s\n", propName, String.valueOf( value ) );
			}
		} catch( IOException ex ) {
			ex.printStackTrace();
		}
	}

	private static float roundToQuater( float value ) {
		return Math.round( value * 4f ) / 4f;
	}
}
