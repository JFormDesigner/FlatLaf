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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Karl Tauber
 */
class FlatThemePropertiesBaseManager
{
	private final Map<String, MyBasePropertyProvider> providers = new HashMap<>();

	FlatThemePropertiesSupport.BasePropertyProvider create( String name, FlatThemePropertiesSupport propertiesSupport ) {
		MyBasePropertyProvider provider = new MyBasePropertyProvider( name, propertiesSupport );
		providers.put( name, provider );
		return provider;
	}

	void clear() {
		providers.clear();
	}

	private static List<String> baseFiles( String name, String baseTheme ) {
		ArrayList<String> result = new ArrayList<>();

		// core themes
		switch( name ) {
			case "FlatLaf":
				result.add( "FlatLightLaf" );
				break;

			case "FlatLightLaf":
			case "FlatDarkLaf":
				result.add( "FlatLaf" );
				break;

			case "FlatIntelliJLaf":
				result.add( "FlatLightLaf" );
				result.add( "FlatLaf" );
				break;

			case "FlatDarculaLaf":
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
					result.add( "FlatLightLaf" );
					result.add( "FlatLaf" );
					break;
			}
		}

		return result;
	}

	//---- class MyBasePropertyProvider ---------------------------------------

	private class MyBasePropertyProvider
		implements FlatThemePropertiesSupport.BasePropertyProvider
	{
		private final String name;
		private final FlatThemePropertiesSupport propertiesSupport;

		private List<String> baseFiles;
		private String lastBaseTheme;

		MyBasePropertyProvider( String name, FlatThemePropertiesSupport propertiesSupport ) {
			this.name = name;
			this.propertiesSupport = propertiesSupport;
		}

		@Override
		public String getProperty( String key, String baseTheme ) {
			updateBaseFiles( baseTheme );

			for( String baseFile : baseFiles ) {
				String value = getPropertyFromBase( baseFile, key );
				if( value != null )
					return value;
			}

			return null;
		}

		private String getPropertyFromBase( String baseFile, String key ) {
			MyBasePropertyProvider provider = providers.get( baseFile );
			return (provider != null)
				? provider.propertiesSupport.getProperties().getProperty( key )
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
			updateBaseFiles( baseTheme );

			for( String baseFile : baseFiles ) {
				MyBasePropertyProvider provider = providers.get( baseFile );
				if( provider == null )
					continue;

				for( Object key : provider.propertiesSupport.getProperties().keySet() )
					allKeys.add( (String) key );
			}
		}
	}
}
