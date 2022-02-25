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
import java.awt.FontMetrics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.text.StyleContext;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * @author Karl Tauber
 */
class LinuxFontPolicy
{
	static Font getFont() {
		return SystemInfo.isKDE ? getKDEFont() : getGnomeFont();
	}

	/**
	 * Gets the default font for Gnome.
	 */
	private static Font getGnomeFont() {
		// see class com.sun.java.swing.plaf.gtk.PangoFonts background information

		Object fontName = Toolkit.getDefaultToolkit().getDesktopProperty( "gnome.Gtk/FontName" );
		if( !(fontName instanceof String) )
			fontName = "sans 10";

		String family = "";
		int style = Font.PLAIN;
		double dsize = 10;

		// parse pango font description
		// see https://developer.gnome.org/pango/1.46/pango-Fonts.html#pango-font-description-from-string
		StringTokenizer st = new StringTokenizer( (String) fontName );
		while( st.hasMoreTokens() ) {
			String word = st.nextToken();

			// remove trailing ',' (e.g. in "Ubuntu Condensed, 11" or "Ubuntu Condensed, Bold 11")
			if( word.endsWith( "," ) )
				word = word.substring( 0, word.length() - 1 ).trim();

			String lword = word.toLowerCase();
			if( lword.equals( "italic" ) || lword.equals( "oblique" ) )
				style |= Font.ITALIC;
			else if( lword.equals( "bold" ) )
				style |= Font.BOLD;
			else if( Character.isDigit( word.charAt( 0 ) ) ) {
				try {
					dsize = Double.parseDouble( word );
				} catch( NumberFormatException ex ) {
					// ignore
				}
			} else {
				// remove '-' from "Semi-Bold", "Extra-Light", etc
				if( lword.startsWith( "semi-" ) || lword.startsWith( "demi-" ) )
					word = word.substring( 0, 4 ) + word.substring( 5 );
				else if( lword.startsWith( "extra-" ) || lword.startsWith( "ultra-" ) )
					word = word.substring( 0, 5 ) + word.substring( 6 );

				family = family.isEmpty() ? word : (family + ' ' + word);
			}
		}

		// Ubuntu font is rendered poorly (except if running in JetBrains VM)
		// --> use Liberation Sans font
		if( family.startsWith( "Ubuntu" ) &&
			!SystemInfo.isJetBrainsJVM &&
			!FlatSystemProperties.getBoolean( FlatSystemProperties.USE_UBUNTU_FONT, false ) )
		  family = "Liberation Sans";

		// scale font size
		dsize *= getGnomeFontScale();
		int size = (int) (dsize + 0.5);
		if( size < 1 )
			size = 1;

		// handle logical font names
		String logicalFamily = mapFcName( family.toLowerCase() );
		if( logicalFamily != null )
			family = logicalFamily;

		return createFontEx( family, style, size, dsize );
	}

	/**
	 * Create a font for the given family, style and size.
	 * If the font family does not match any font on the system,
	 * then the last word (usually a font weight) from the family name is removed and tried again.
	 * E.g. family 'URW Bookman Light' is not found, but 'URW Bookman' is found.
	 * If still not found, then font of family 'Dialog' is returned.
	 */
	private static Font createFontEx( String family, int style, int size, double dsize ) {
		for(;;) {
			Font font = createFont( family, style, size, dsize );

			if( Font.DIALOG.equals( family ) )
				return font;

			// if the font family does not match any font on the system, "Dialog" family is returned
			if( !Font.DIALOG.equals( font.getFamily() ) ) {
				// check for font problems
				// - font height much larger than expected (e.g. font Inter; Oracle Java 8)
				// - character width is zero (e.g. font Cantarell; Fedora; Oracle Java 8)
				FontMetrics fm = StyleContext.getDefaultStyleContext().getFontMetrics( font );
				if( fm.getHeight() > size * 2 || fm.stringWidth( "a" ) == 0 )
					return createFont( Font.DIALOG, style, size, dsize );

				return font;
			}

			// find last word in family
			int index = family.lastIndexOf( ' ' );
			if( index < 0 )
				return createFont( Font.DIALOG, style, size, dsize );

			// check whether last work contains some font weight (e.g. Ultra-Bold or Heavy)
			String lastWord = family.substring( index + 1 ).toLowerCase();
			if( lastWord.contains( "bold" ) || lastWord.contains( "heavy" ) || lastWord.contains( "black" ) )
				style |= Font.BOLD;

			// remove last word from family and try again
			family = family.substring( 0, index );
		}
	}

	private static Font createFont( String family, int style, int size, double dsize ) {
		Font font = FlatLaf.createCompositeFont( family, style, size );

		// set font size in floating points
		font = font.deriveFont( style, (float) dsize );

		return font;
	}

	private static double getGnomeFontScale() {
		// do not scale font here if JRE scales
		if( isSystemScaling() )
			return 96. / 72.;

		// see class com.sun.java.swing.plaf.gtk.PangoFonts background information

		Object value = Toolkit.getDefaultToolkit().getDesktopProperty( "gnome.Xft/DPI" );
		if( value instanceof Integer ) {
			int dpi = (Integer) value / 1024;
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

	/**
	 * Gets the default font for KDE from KDE configuration files.
	 *
	 * The Swing fonts are not updated when the user changes system font size
	 * (System Settings > Fonts > Force Font DPI). A application restart is necessary.
	 * This is the same behavior as in native KDE applications.
	 *
	 * The "display scale factor" (kdeglobals: [KScreen] > ScaleFactor) is not used
	 * KDE also does not use it to calculate font size. Only forceFontDPI is used by KDE.
	 * If user changes "display scale factor" (System Settings > Display and Monitors >
	 * Displays > Scale Display), the forceFontDPI is also changed to reflect the scale factor.
	 */
	private static Font getKDEFont() {
		List<String> kdeglobals = readConfig( "kdeglobals" );
		List<String> kcmfonts = readConfig( "kcmfonts" );

		String generalFont = getConfigEntry( kdeglobals, "General", "font" );
		String forceFontDPI = getConfigEntry( kcmfonts, "General", "forceFontDPI" );

		String family = "sansserif";
		int style = Font.PLAIN;
		int size = 10;

		if( generalFont != null ) {
			List<String> strs = StringUtils.split( generalFont, ',' );
			try {
				family = strs.get( 0 );
				size = Integer.parseInt( strs.get( 1 ) );
				if( "75".equals( strs.get( 4 ) ) )
					style |= Font.BOLD;
				if( "1".equals( strs.get( 5 ) ) )
					style |= Font.ITALIC;
			} catch( RuntimeException ex ) {
				LoggingFacade.INSTANCE.logConfig( "FlatLaf: Failed to parse 'font=" + generalFont + "'.", ex );
			}
		}

		// font dpi
		int dpi = 96;
		if( forceFontDPI != null && !isSystemScaling() ) {
			try {
				dpi = Integer.parseInt( forceFontDPI );
				if( dpi <= 0 )
					dpi = 96;
				if( dpi < 50 )
					dpi = 50;
			} catch( NumberFormatException ex ) {
				LoggingFacade.INSTANCE.logConfig( "FlatLaf: Failed to parse 'forceFontDPI=" + forceFontDPI + "'.", ex );
			}
		}

		// scale font size
		double fontScale = dpi / 72.0;
		double dsize = size * fontScale;
		size = (int) (dsize + 0.5);
		if( size < 1 )
			size = 1;

		return createFont( family, style, size, dsize );
	}

	private static List<String> readConfig( String filename ) {
		File userHome = new File( System.getProperty( "user.home" ) );

		// search for config file
		String[] configDirs = {
			".config", // KDE 5
			".kde4/share/config", // KDE 4
			".kde/share/config"// KDE 3
		};
		File file = null;
		for( String configDir : configDirs ) {
			file = new File( userHome, configDir + "/" + filename );
			if( file.isFile() )
				break;
		}
		if( !file.isFile() )
			return Collections.emptyList();

		// read config file
		ArrayList<String> lines = new ArrayList<>( 200 );
		try( BufferedReader reader = new BufferedReader( new FileReader( file ) ) ) {
			String line;
			while( (line = reader.readLine()) != null )
				lines.add( line );
		} catch( IOException ex ) {
			LoggingFacade.INSTANCE.logConfig( "FlatLaf: Failed to read '" + filename + "'.", ex );
		}
		return lines;
	}

	private static String getConfigEntry( List<String> config, String group, String key ) {
		int groupLength = group.length();
		int keyLength = key.length();
		boolean inGroup = false;
		for( String line : config ) {
			if( !inGroup ) {
				if( line.length() >= groupLength + 2 &&
					line.charAt( 0 ) == '[' &&
					line.charAt( groupLength + 1 ) == ']' &&
					line.indexOf( group ) == 1 )
				{
					inGroup = true;
				}
			} else {
				if( line.startsWith( "[" ) )
					return null;

				if( line.length() >= keyLength + 2 &&
					line.charAt( keyLength ) == '=' &&
					line.startsWith( key ) )
				{
					return line.substring( keyLength + 1 );
				}
			}
		}
		return null;
	}

	/**
	 * Returns true if the JRE scales, which is the case if:
	 *   - environment variable GDK_SCALE is set and running on Java 9 or later
	 *   - running on JetBrains Runtime 11 or later and scaling is enabled in system Settings
	 */
	private static boolean isSystemScaling() {
		if( GraphicsEnvironment.isHeadless() )
			return true;

		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice().getDefaultConfiguration();
		return UIScale.getSystemScaleFactor( gc ) > 1;
	}
}
