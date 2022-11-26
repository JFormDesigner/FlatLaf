/*
 * Copyright 2022 FormDev Software GmbH
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

package com.formdev.flatlaf.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.plaf.UIResource;
import javax.swing.text.StyleContext;

/**
 * Utility methods for fonts.
 *
 * @author Karl Tauber
 * @since 3
 */
public class FontUtils
{
	private static Map<String, Runnable> loadersMap;

	/**
	 * Gets a composite font for the given family, style and size.
	 * A composite font that is able to display all Unicode characters.
	 * The font family is loaded if necessary via {@link #loadFontFamily(String)}.
	 * <p>
	 * To get fonts derived from returned fonts, it is recommended to use one of the
	 * {@link Font#deriveFont} methods instead of invoking this method.
	 */
	public static Font getCompositeFont( String family, int style, int size ) {
		loadFontFamily( family );

		// using StyleContext.getFont() here because it uses
		// sun.font.FontUtilities.getCompositeFontUIResource()
		// and creates a composite font that is able to display all Unicode characters
		Font font = StyleContext.getDefaultStyleContext().getFont( family, style, size );

		// always return non-UIResource font to avoid side effects when using font
		// because Swing uninstalls UIResource fonts when switching L&F
		// (StyleContext.getFont() may return a UIResource)
		if( font instanceof UIResource )
			font = font.deriveFont( font.getStyle() );

		return font;
	}

	/**
	 * Registers a font family for lazy loading via {@link #loadFontFamily(String)}.
	 * <p>
	 * The given runnable is invoked when the given font family should be loaded.
	 * The runnable should invoke {@link #installFont(URL)} to load and register font(s)
	 * for the family.
	 * A family may consist of up to four font files for the supported font styles:
	 * regular (plain), italic, bold and bold-italic.
	 */
	public static void registerFontFamilyLoader( String family, Runnable loader ) {
		if( loadersMap == null )
			loadersMap = new HashMap<>();
		loadersMap.put( family, loader );
	}

	/**
	 * Loads a font family previously registered via {@link #registerFontFamilyLoader(String, Runnable)}.
	 * If the family is already loaded or no londer is registered for that family, nothing happens.
	 */
	public static void loadFontFamily( String family ) {
		if( !hasLoaders() )
			return;

		Runnable loader = loadersMap.remove( family );
		if( loader != null )
			loader.run();

		if( loadersMap.isEmpty() )
			loadersMap = null;
	}

	/**
	 * Loads a font file from the given url and registers it in the graphics environment.
	 * Uses {@link Font#createFont(int, InputStream)} and {@link GraphicsEnvironment#registerFont(Font)}.
	 */
	public static boolean installFont( URL url ) {
		try( InputStream in = url.openStream() ) {
			Font font = Font.createFont( Font.TRUETYPE_FONT, in );
			return GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont( font );
		} catch( FontFormatException | IOException ex ) {
			LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to install font " + url, ex );
			return false;
		}
	}

	/**
	 * Returns all font familiy names available in the graphics environment.
	 * This invokes {@link GraphicsEnvironment#getAvailableFontFamilyNames()} and
	 * appends families registered for lazy loading via {@link #registerFontFamilyLoader(String, Runnable)}
	 * to the result.
	 */
	public static String[] getAvailableFontFamilyNames() {
		String[] availableFontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		if( !hasLoaders() )
			return availableFontFamilyNames;

		// append families that are not yet loaded
		ArrayList<String> result = new ArrayList<>( availableFontFamilyNames.length + loadersMap.size() );
		for( String name : availableFontFamilyNames )
			result.add( name );
		for( String name : loadersMap.keySet() ) {
			if( !result.contains( name ) )
				result.add( name );
		}

		return result.toArray( new String[result.size()] );
	}

	/**
	 * Returns all fonts available in the graphics environment.
	 * This first loads all families registered for lazy loading via {@link #registerFontFamilyLoader(String, Runnable)}
	 * and then invokes {@link GraphicsEnvironment#getAllFonts()}.
	 */
	public static Font[] getAllFonts() {
		if( hasLoaders() ) {
			// load all registered families
			String[] families = loadersMap.keySet().toArray( new String[loadersMap.size()] );
			for( String family : families )
				loadFontFamily( family );
		}

		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	}

	private static boolean hasLoaders() {
		return loadersMap != null && !loadersMap.isEmpty();
	}
}
