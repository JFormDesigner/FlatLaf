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

package com.formdev.flatlaf.themeeditor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIconColors;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.StringUtils;

/**
 * @author Karl Tauber
 */
class FlatThemePropertiesBaseManager
{
	private static Class<?>[] CORE_THEMES = {
		FlatLaf.class,
		FlatLightLaf.class,
		FlatDarkLaf.class,
		FlatIntelliJLaf.class,
		FlatDarculaLaf.class,
		FlatMacLightLaf.class,
		FlatMacDarkLaf.class,
	};

	private final Map<String, MyBasePropertyProvider> providers = new HashMap<>();
	private static Map<String, Properties> coreThemes;
	private static Set<String> definedCoreKeys;

	FlatThemePropertiesSupport.BasePropertyProvider create( File file, FlatThemePropertiesSupport propertiesSupport ) {
		String name = StringUtils.removeTrailing( file.getName(), ".properties" );
		boolean isCoreTheme = file.getParent().replace( '\\', '/' ).endsWith( "/com/formdev/flatlaf" );
		MyBasePropertyProvider provider = new MyBasePropertyProvider( name, propertiesSupport, isCoreTheme );
		providers.put( name, provider );
		return provider;
	}

	void clear() {
		providers.clear();
	}

	static Set<String> getDefindedCoreKeys() {
		if( definedCoreKeys != null )
			return definedCoreKeys;

		loadCoreThemes();

		definedCoreKeys = new HashSet<>();
		for( Properties properties : coreThemes.values() ) {
			outer:
			for( Object k : properties.keySet() ) {
				String key = (String) k;
				if( key.startsWith( "*." ) || key.startsWith( "@" ) )
					continue;

				while( key.startsWith( "[" ) ) {
					int closeIndex = key.indexOf( ']' );
					if( closeIndex < 0 )
						continue outer;

					String prefix = key.substring( 0, closeIndex + 1 );
					if( FlatLaf.getUIKeySpecialPrefixes().contains( prefix ) )
						break; // keep special prefix

					key = key.substring( closeIndex + 1 );
				}
				definedCoreKeys.add( key );
			}
		}
		return definedCoreKeys;
	}

	private static void loadCoreThemes() {
		if( coreThemes != null )
			return;

		coreThemes = new HashMap<>();

		for( Class<?> lafClass : CORE_THEMES ) {
			String propertiesName = '/' + lafClass.getName().replace( '.', '/' ) + ".properties";
			try( InputStream in = lafClass.getResourceAsStream( propertiesName ) ) {
				Properties properties = new Properties();
				if( in != null )
					properties.load( in );
				coreThemes.put( lafClass.getSimpleName(), properties );
			} catch( IOException ex ) {
				ex.printStackTrace();
			}
		}
	}

	private static List<String> baseFiles( String name, String baseTheme ) {
		ArrayList<String> result = new ArrayList<>();

		// core themes
		switch( name ) {
			case "FlatLightLaf":
			case "FlatDarkLaf":
				result.add( "FlatLaf" );
				break;

			case "FlatIntelliJLaf":
			case "FlatMacLightLaf":
				result.add( "FlatLightLaf" );
				result.add( "FlatLaf" );
				break;

			case "FlatDarculaLaf":
			case "FlatMacDarkLaf":
				result.add( "FlatDarkLaf" );
				result.add( "FlatLaf" );
				break;
		}

		// custom themes based on core themes
		if( result.isEmpty() ) {
			if( baseTheme == null )
				baseTheme = "light";

			switch( baseTheme ) {
				default:
				case "light":
					result.add( "FlatLightLaf" );
					result.add( "FlatLaf" );
					break;

				case "dark":
					result.add( "FlatDarkLaf" );
					result.add( "FlatLaf" );
					break;

				case "intellij":
					result.add( "FlatIntelliJLaf" );
					result.add( "FlatLightLaf" );
					result.add( "FlatLaf" );
					break;

				case "darcula":
					result.add( "FlatDarculaLaf" );
					result.add( "FlatDarkLaf" );
					result.add( "FlatLaf" );
					break;

				case "maclight":
					result.add( "FlatMacLightLaf" );
					result.add( "FlatLightLaf" );
					result.add( "FlatLaf" );
					break;

				case "macdark":
					result.add( "FlatMacDarkLaf" );
					result.add( "FlatDarkLaf" );
					result.add( "FlatLaf" );
					break;
			}

			// exclude base properties if editing base properties
			if( name.equals( "FlatLaf" ) )
				result.remove( "FlatLaf" );
		}

		return result;
	}

	//---- class MyBasePropertyProvider ---------------------------------------

	private class MyBasePropertyProvider
		implements FlatThemePropertiesSupport.BasePropertyProvider
	{
		private final String name;
		private final FlatThemePropertiesSupport propertiesSupport;
		private final boolean isCoreTheme;
		private final String coreBaseTheme;

		private List<String> baseFiles;
		private String lastBaseTheme;

		MyBasePropertyProvider( String name, FlatThemePropertiesSupport propertiesSupport, boolean isCoreTheme ) {
			this.name = name;
			this.propertiesSupport = propertiesSupport;
			this.isCoreTheme = isCoreTheme;

			switch( name ) {
				case "FlatLightLaf":	coreBaseTheme = "light"; break;
				case "FlatDarkLaf":		coreBaseTheme = "dark"; break;
				case "FlatIntelliJLaf":	coreBaseTheme = "intellij"; break;
				case "FlatDarculaLaf":	coreBaseTheme = "darcula"; break;
				case "FlatMacLightLaf":	coreBaseTheme = "maclight"; break;
				case "FlatMacDarkLaf":	coreBaseTheme = "macdark"; break;
				default:				coreBaseTheme = null; break;
			}
		}

		@Override
		public String getProperty( String key, String baseTheme ) {
			// override base theme for core themes
			if( coreBaseTheme != null )
				baseTheme = coreBaseTheme;

			updateBaseFiles( baseTheme );

			// search in opened editors
			for( String baseFile : baseFiles ) {
				String value = getPropertyFromBase( baseFile, key );
				if( value != null )
					return value;
			}

			// search in core themes
			if( !isCoreTheme ) {
				loadCoreThemes();

				String value = getPropertyFromCore( name, key );
				if( value != null )
					return value;

				for( String baseFile : baseFiles ) {
					value = getPropertyFromCore( baseFile, key );
					if( value != null )
						return value;
				}
			}

			// search in icon colors
			if( key.startsWith( "Actions." ) || key.startsWith( "Objects." ) ) {
				boolean dark = FlatThemePropertiesSupport.isDark( baseTheme );
				for( FlatIconColors c : FlatIconColors.values() ) {
					if( c.key.equals( key ) && (c.light == !dark || c.dark == dark) )
						return String.format( "#%06x", c.rgb );
				}
			}

			return null;
		}

		private String getPropertyFromBase( String baseFile, String key ) {
			MyBasePropertyProvider provider = providers.get( baseFile );
			return (provider != null)
				? provider.propertiesSupport.getProperties().getProperty( key )
				: null;
		}

		private String getPropertyFromCore( String baseFile, String key ) {
			Properties properties = coreThemes.get( baseFile );
			return (properties != null)
				? properties.getProperty( key )
				: null;
		}

		private void updateBaseFiles( String baseTheme ) {
			if( baseFiles != null && Objects.equals( baseTheme, lastBaseTheme ) )
				return;

			baseFiles = baseFiles( name, baseTheme );
			lastBaseTheme = baseTheme;
		}

		@Override
		public void addAllKeys( Set<String> allKeys, String baseTheme ) {
			// override base theme for core themes
			if( coreBaseTheme != null )
				baseTheme = coreBaseTheme;

			updateBaseFiles( baseTheme );

			// search in opened editors
			for( String baseFile : baseFiles ) {
				MyBasePropertyProvider provider = providers.get( baseFile );
				if( provider == null )
					continue;

				for( Object key : provider.propertiesSupport.getProperties().keySet() )
					allKeys.add( (String) key );
			}

			// search in core themes
			if( !isCoreTheme ) {
				loadCoreThemes();

				copyKeys( coreThemes.get( name ), allKeys );

				for( String baseFile : baseFiles )
					copyKeys( coreThemes.get( baseFile ), allKeys );
			}

			// icon colors
			for( FlatIconColors c : FlatIconColors.values() )
				allKeys.add( c.key );
		}

		private void copyKeys( Properties properties, Set<String> allKeys ) {
			if( properties == null )
				return;

			for( Object key : properties.keySet() )
				allKeys.add( (String) key );
		}
	}
}
