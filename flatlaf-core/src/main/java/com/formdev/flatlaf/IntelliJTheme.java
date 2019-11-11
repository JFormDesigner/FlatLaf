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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.UIDefaults;
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
		Map<String, String> properties = convertToProperties();
		applyProperties( properties, defaults );
	}

	private static void applyProperties( Map<String, String> properties, UIDefaults defaults ) {
		// globals
		ArrayList<Object> keys = Collections.list( defaults.keys() );
		for( Map.Entry<String, String> e : properties.entrySet() ) {
			String key = e.getKey();
			if( !key.startsWith( "*." ) )
				continue;

			String tail = key.substring( 1 );
			Object uiValue = UIDefaultsLoader.parseValue( key, e.getValue() );
			for( Object k : keys ) {
				if( k instanceof String && ((String)k).endsWith( tail ) )
					defaults.put( k, uiValue );
			}
		}

		// non-globals
		for( Map.Entry<String, String> e : properties.entrySet() ) {
			String key = e.getKey();
			if( key.startsWith( "*." ) )
				continue;

			Object uiValue = UIDefaultsLoader.parseValue( key, e.getValue() );
			defaults.put( key, uiValue );
		}
	}

	private Map<String, String> convertToProperties() {
		Map<String, String> properties = new LinkedHashMap<>();
		if( ui == null )
			return properties;

		// convert json structure to properties map
		for( Map.Entry<String, Object> e : ui.entrySet() )
			addToProperties( e.getKey(), e.getValue(), properties );

		return properties;
	}

	@SuppressWarnings( "unchecked" )
	private void addToProperties( String key, Object value, Map<String, String> properties ) {
		if( value instanceof Map ) {
			for( Map.Entry<String, Object> e : ((Map<String, Object>)value).entrySet() )
				addToProperties( key + '.' + e.getKey(), e.getValue(), properties );
		} else {
			String valueStr = value.toString();

			// map colors
			if( colors != null )
				valueStr = colors.getOrDefault( valueStr, valueStr );

			properties.put( key, valueStr );
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
