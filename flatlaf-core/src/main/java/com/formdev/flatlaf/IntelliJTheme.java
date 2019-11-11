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

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import com.formdev.flatlaf.json.Json;
import com.formdev.flatlaf.json.ParseException;

/**
 * @author Karl Tauber
 */
public class IntelliJTheme
{
	public final String name;
	public final boolean dark;
	public final String author;

	private final Map<String, String> colors;
	private final Map<String, Object> ui;
	private final Map<String, Object> icons;

	private Map<String, Color> namedColors = Collections.emptyMap();

	public static boolean install( InputStream in )
		throws IOException, ParseException
	{
		try {
		    return FlatLaf.install( createLaf( in ) );
		} catch( Exception ex ) {
		    System.err.println( "Failed to initialize look and feel" );
		    ex.printStackTrace();
		    return false;
		}
	}

	public static FlatLaf createLaf( InputStream in )
		throws IOException, ParseException
	{
		return createLaf( new IntelliJTheme( in ) );
	}

	public static FlatLaf createLaf( IntelliJTheme theme ) {
		FlatLaf laf = theme.dark
			? new DarkLaf( theme )
			: new LightLaf( theme );
		return laf;
	}

	@SuppressWarnings( "unchecked" )
	public IntelliJTheme( InputStream in )
		throws IOException, ParseException
	{
		Map<String, Object> json;
	    try( Reader reader = new InputStreamReader( in, StandardCharsets.UTF_8 ) ) {
	    		json = (Map<String, Object>) Json.parse( reader );
		}

	    name = (String) json.get( "name" );
	    dark = Boolean.parseBoolean( (String) json.get( "dark" ) );
	    author = (String) json.get( "author" );

	    colors = (Map<String, String>) json.get( "colors" );
	    ui = (Map<String, Object>) json.get( "ui" );
	    icons = (Map<String, Object>) json.get( "icons" );
	}

	private void applyProperties( UIDefaults defaults ) {
		if( ui == null )
			return;

		loadColorPalette( defaults );

		// convert Json "ui" structure to UI defaults
		ArrayList<Object> defaultsKeysCache = new ArrayList<>();
		for( Map.Entry<String, Object> e : ui.entrySet() )
			apply( e.getKey(), e.getValue(), defaults, defaultsKeysCache );
	}

	private void loadColorPalette( UIDefaults defaults ) {
		if( colors == null )
			return;

		namedColors = new HashMap<>();

		for( Map.Entry<String, String> e : colors.entrySet() ) {
			String value = e.getValue();
			ColorUIResource color = UIDefaultsLoader.parseColor( value );
			if( color != null ) {
				String key = e.getKey();
				namedColors.put( key, color );
				defaults.put( "ColorPalette." + e.getKey(), color );
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	private void apply( String key, Object value, UIDefaults defaults, ArrayList<Object> defaultsKeysCache ) {
		if( value instanceof Map ) {
			for( Map.Entry<String, Object> e : ((Map<String, Object>)value).entrySet() )
				apply( key + '.' + e.getKey(), e.getValue(), defaults, defaultsKeysCache );
		} else {
			String valueStr = value.toString();

			// map named colors
			Object uiValue = namedColors.get( valueStr );

			// parse value
			if( uiValue == null )
				uiValue = UIDefaultsLoader.parseValue( key, valueStr );

			if( key.startsWith( "*." ) ) {
				// wildcard
				String tail = key.substring( 1 );

				// because we can not iterate over the UI defaults keys while
				// modifying UI defaults in the same loop, we have to copy the keys
				if( defaultsKeysCache.size() != defaults.size() ) {
					defaultsKeysCache.clear();
					Enumeration<Object> e = defaults.keys();
					while( e.hasMoreElements() )
						defaultsKeysCache.add( e.nextElement() );
				}

				// replace all values in UI defaults that match the wildcard key
				for( Object k : defaultsKeysCache ) {
					if( k instanceof String && ((String)k).endsWith( tail ) )
						defaults.put( k, uiValue );
				}
			} else
				defaults.put( key, uiValue );
		}
	}

	//---- class LightLaf -----------------------------------------------------

	public static class LightLaf
		extends FlatIntelliJLaf
	{
		private final IntelliJTheme theme;

		public LightLaf( IntelliJTheme theme ) {
			this.theme = theme;
		}

		@Override
		public String getName() {
			return theme.name;
		}

		@Override
		public String getDescription() {
			return theme.name;
		}

		@Override
		public UIDefaults getDefaults() {
			UIDefaults defaults = super.getDefaults();
			theme.applyProperties( defaults );
			return defaults;
		}
	}

	//---- class DarkLaf ------------------------------------------------------

	public static class DarkLaf
		extends FlatDarculaLaf
	{
		private final IntelliJTheme theme;

		public DarkLaf( IntelliJTheme theme ) {
			this.theme = theme;
		}

		@Override
		public String getName() {
			return theme.name;
		}

		@Override
		public String getDescription() {
			return theme.name;
		}

		@Override
		public UIDefaults getDefaults() {
			UIDefaults defaults = super.getDefaults();
			theme.applyProperties( defaults );
			return defaults;
		}
	}
}
