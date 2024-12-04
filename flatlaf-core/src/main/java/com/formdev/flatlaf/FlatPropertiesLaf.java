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

package com.formdev.flatlaf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

/**
 * A Flat LaF that is able to load UI defaults from properties passed to the constructor.
 * <p>
 * Specify the base theme in the properties with {@code @baseTheme=<baseTheme>}.
 * Allowed values for {@code <baseTheme>} are {@code light} (the default), {@code dark},
 * {@code intellij}, {@code darcula}, {@code maclight} or {@code macdark}.
 * <p>
 * The properties are applied after loading the base theme and may overwrite base properties.
 * All features of FlatLaf properties files are available.
 *
 * @author Karl Tauber
 */
public class FlatPropertiesLaf
	extends FlatLaf
{
	private final String name;
	private final String baseTheme;
	private final boolean dark;
	private final Properties properties;

	public FlatPropertiesLaf( String name, File propertiesFile )
		throws IOException
	{
		this( name, new FileInputStream( propertiesFile ) );
	}

	public FlatPropertiesLaf( String name, InputStream in )
		throws IOException
	{
		this( name, loadProperties( in ) );
	}

	private static Properties loadProperties( InputStream in )
		throws IOException
	{
		Properties properties = new Properties();
		try( InputStream in2 = in ) {
			properties.load( in2 );
		}
		return properties;
	}

	public FlatPropertiesLaf( String name, Properties properties ) {
		this.name = name;
		this.properties = properties;

		baseTheme = properties.getProperty( "@baseTheme", "light" );
		dark = "dark".equalsIgnoreCase( baseTheme ) || "darcula".equalsIgnoreCase( baseTheme ) ||
			"macdark".equalsIgnoreCase( baseTheme );
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return name;
	}

	@Override
	public boolean isDark() {
		return dark;
	}

	public Properties getProperties() {
		return properties;
	}

	@Override
	protected ArrayList<Class<?>> getLafClassesForDefaultsLoading() {
		ArrayList<Class<?>> lafClasses = new ArrayList<>();
		lafClasses.add( FlatLaf.class );
		switch( baseTheme.toLowerCase( Locale.ENGLISH ) ) {
			default:
			case "light":
				lafClasses.add( FlatLightLaf.class );
				break;

			case "dark":
				lafClasses.add( FlatDarkLaf.class );
				break;

			case "intellij":
				lafClasses.add( FlatLightLaf.class );
				lafClasses.add( FlatIntelliJLaf.class );
				break;

			case "darcula":
				lafClasses.add( FlatDarkLaf.class );
				lafClasses.add( FlatDarculaLaf.class );
				break;

			case "maclight":
				lafClasses.add( FlatLightLaf.class );
				lafClasses.add( FlatMacLightLaf.class );
				break;

			case "macdark":
				lafClasses.add( FlatDarkLaf.class );
				lafClasses.add( FlatMacDarkLaf.class );
				break;
		}
		return lafClasses;
	}

	@Override
	protected Properties getAdditionalDefaults() {
		return properties;
	}
}
