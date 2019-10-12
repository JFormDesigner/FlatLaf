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

package com.formdev.flatlaf;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.StringTokenizer;
import javax.swing.text.StyleContext;

/**
 * @author Karl Tauber
 */
class LinuxFontPolicy
{
	static Font getFont() {
		// see class com.sun.java.swing.plaf.gtk.PangoFonts background information

		Object fontName = Toolkit.getDefaultToolkit().getDesktopProperty( "gnome.Gtk/FontName" );
		if( !(fontName instanceof String) )
			fontName = "sans 10";

        String family = "";
        int style = Font.PLAIN;
        int size = 10;

		StringTokenizer st = new StringTokenizer( (String) fontName );
		while( st.hasMoreTokens() ) {
			String word = st.nextToken();

			if( word.equalsIgnoreCase( "italic" ) )
				style |= Font.ITALIC;
			else if( word.equalsIgnoreCase( "bold" ) )
				style |= Font.BOLD;
			else if( Character.isDigit( word.charAt( 0 ) ) ) {
				try {
					size = Integer.parseInt( word );
				} catch( NumberFormatException ex ) {
					// ignore
				}
			} else
				family = family.isEmpty() ? word : (family + ' ' + word);
		}

		// scale font size
		double dsize = size * getSystemFontScale();
		size = (int) (dsize + 0.5);
		if( size < 1 )
			size = 1;

		// handle logical font names
		String logicalFamily = mapFcName( family.toLowerCase() );
		if( logicalFamily != null )
			family = logicalFamily;

		// using StyleContext.getFont() here because it uses
		// sun.font.FontUtilities.getCompositeFontUIResource()
		Font font = new StyleContext().getFont( family, style, size );

		// set font size in floating points
		font = font.deriveFont( style, (float) dsize );

		return font;
	}

	private static double getSystemFontScale() {
		// see class com.sun.java.swing.plaf.gtk.PangoFonts background information

		Object value = Toolkit.getDefaultToolkit().getDesktopProperty( "gnome.Xft/DPI" );
		if( value instanceof Integer ) {
			int dpi = ((Integer)value).intValue() / 1024;
			if( dpi == -1 )
				dpi = 96;
			if( dpi < 50 )
				dpi = 50;
			return dpi / 72.0;
		} else {
			return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.getNormalizingTransform().getScaleY();
		}
	}

	/**
	 * map GTK/fontconfig names to equivalent JDK logical font name
	 */
	private static String mapFcName( String name ) {
		switch( name ) {
			case "sans":		return "sansserif";
			case "sans-serif":	return "sansserif";
			case "serif":		return "serif";
			case "monospace":	return "monospaced";
		}
		return null;
	}
}
