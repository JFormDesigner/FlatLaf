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
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * The base class for all Flat LaFs.
 *
 * @author Karl Tauber
 */
public abstract class FlatLaf
	extends BasicLookAndFeel
{
	private static final String VARIABLE_PREFIX = "@";
	private static final String GLOBAL_PREFIX = "*.";

	private BasicLookAndFeel base;

	@Override
	public String getID() {
		return getName();
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return true;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}

	@Override
	public void initialize() {
		getBase().initialize();

		super.initialize();
	}

	@Override
	public void uninitialize() {
		if( base != null )
			base.uninitialize();

		super.uninitialize();
	}

	/**
	 * Get/create base LaF. This is used to grab base UI defaults from different LaFs.
	 * E.g. on Mac from system dependent LaF, otherwise from Metal LaF.
	 */
	private BasicLookAndFeel getBase() {
		if( base == null )
			base = new MetalLookAndFeel();
		return base;
	}

	@Override
	public UIDefaults getDefaults() {
		UIDefaults defaults = getBase().getDefaults();

		// initialize some defaults (for overriding) that are used in basic UI delegates,
		// but are not set in MetalLookAndFeel or BasicLookAndFeel
		Color control = defaults.getColor( "control" );
		defaults.put( "EditorPane.disabledBackground", control );
		defaults.put( "FormattedTextField.disabledBackground", control );
		defaults.put( "PasswordField.disabledBackground", control );
		defaults.put( "TextArea.disabledBackground", control );
		defaults.put( "TextField.disabledBackground", control );
		defaults.put( "TextPane.disabledBackground", control );

		initFonts( defaults );
		loadDefaultsFromProperties( defaults );

		return defaults;
	}

	private void initFonts( UIDefaults defaults ) {
		FontUIResource uiFont = null;

		//TODO
		if( SystemInfo.IS_WINDOWS ) {
			Font winFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty( "win.messagebox.font" );
			if( winFont != null )
				uiFont = new FontUIResource( winFont );
		}

		if( uiFont == null )
			return;

		// override fonts
		for( Object key : defaults.keySet() ) {
			if( key instanceof String && ((String)key).endsWith( ".font" ) )
				defaults.put( key, uiFont );
		}
	}

	/**
	 * Load properties associated to Flat LaF classes and add to UI defaults.
	 *
	 * Each class that extend this class may have its own .properties file
	 * in the same package as the class. Properties from superclasses are loaded
	 * first to give subclasses a chance to override defaults.
	 * E.g. if running FlatDarkLaf, then the FlatLaf.properties is loaded first
	 * and FlatDarkLaf.properties loaded second.
	 */
	private void loadDefaultsFromProperties( UIDefaults defaults ) {
		// determine classes in class hierarchy in reverse order
		ArrayList<Class<?>> lafClasses = new ArrayList<>();
		for( Class<?> lafClass = getClass();
			FlatLaf.class.isAssignableFrom( lafClass );
			lafClass = lafClass.getSuperclass() )
		{
			lafClasses.add( 0, lafClass );
		}

		try {
			// load properties files
			Properties properties = new Properties();
			for( Class<?> lafClass : lafClasses ) {
				String propertiesName = "/" + lafClass.getName().replace( '.', '/' ) + ".properties";
				try( InputStream in = lafClass.getResourceAsStream( propertiesName ) ) {
					if( in != null )
						properties.load( in );
				}
			}

			// get globals, which override all other defaults that end with same suffix
			HashMap<String, Object> globals = new HashMap<>();
			for( Map.Entry<Object, Object> e : properties.entrySet() ) {
				String key = (String) e.getKey();
				if( !key.startsWith( GLOBAL_PREFIX ) )
					continue;

				String value = resolveVariable( properties, (String) e.getValue() );
				globals.put( key.substring( GLOBAL_PREFIX.length() ), parseValue( key, value ) );
			}

			// override UI defaults with globals
			for( Object key : defaults.keySet() ) {
				if( key instanceof String && ((String)key).contains( "." ) ) {
					String skey = (String) key;
					String globalKey = skey.substring( skey.lastIndexOf( '.' ) + 1 );
					Object globalValue = globals.get( globalKey );
					if( globalValue != null )
						defaults.put( key, globalValue );
				}
			}

			// add non-global properties to UI defaults
			for( Map.Entry<Object, Object> e : properties.entrySet() ) {
				String key = (String) e.getKey();
				if( key.startsWith( VARIABLE_PREFIX ) || key.startsWith( GLOBAL_PREFIX ) )
					continue;

				String value = resolveVariable( properties, (String) e.getValue() );
				defaults.put( key, parseValue( key, value ) );
			}
		} catch( IOException ex ) {
			ex.printStackTrace();
		}
	}

	private String resolveVariable( Properties properties, String value ) {
		if( !value.startsWith( VARIABLE_PREFIX ) )
			return value;

		String newValue = properties.getProperty( value );
		if( newValue == null )
			System.err.println( "variable '" + value + "' not found" );

		return newValue;
	}

	private Object parseValue( String key, String value ) {
		value = value.trim();

		// null, false, true
		switch( value ) {
			case "null":		return null;
			case "false":	return false;
			case "true":		return true;
		}

		// borders
		if( key.endsWith( ".border" ) )
			return parseBorder( value );

		// icons
		if( key.endsWith( ".icon" ) )
			return parseInstance( value );

		// insets
		if( key.endsWith( ".margin" ) )
			return parseInsets( value );

		// colors
		ColorUIResource color = parseColor( value );
		if( color != null )
			return color;

		// integer
		Integer integer = parseInteger( value, false );
		if( integer != null )
			return integer;

		// string
		return value;
	}

	private Object parseBorder( String value ) {
		return parseInstance( value );
	}

	private Object parseInstance( String value ) {
		return (LazyValue) t -> {
			try {
				return Class.forName( value ).newInstance();
			} catch( InstantiationException | IllegalAccessException | ClassNotFoundException ex ) {
				ex.printStackTrace();
				return null;
			}
		};
	}

	private Insets parseInsets( String value ) {
		List<String> numbers = split( value, ',' );
		try {
			return new InsetsUIResource(
				Integer.parseInt( numbers.get( 0 ) ),
				Integer.parseInt( numbers.get( 1 ) ),
				Integer.parseInt( numbers.get( 2 ) ),
				Integer.parseInt( numbers.get( 3 ) ) );
		} catch( NumberFormatException ex ) {
			System.err.println( "invalid insets '" + value + "'" );
			throw ex;
		}
	}

	private ColorUIResource parseColor( String value ) {
		try {
			if( value.length() == 6 ) {
				int rgb = Integer.parseInt( value, 16 );
				return new ColorUIResource( rgb );
			}
			if( value.length() == 8 ) {
				int rgba = Integer.parseInt( value, 16 );
				return new ColorUIResource( new Color( rgba, true ) );
			}
		} catch( NumberFormatException ex ) {
			// not a color --> ignore
		}
		return null;
	}

	private Integer parseInteger( String value, boolean reportError ) {
		try {
			return Integer.parseInt( value );
		} catch( NumberFormatException ex ) {
			if( reportError ) {
				System.err.println( "invalid integer '" + value + "'" );
				throw ex;
			}
		}
		return null;
	}

	private static List<String> split( String str, char delim ) {
		ArrayList<String> strs = new ArrayList<>();
		int delimIndex = str.indexOf( delim );
		int index = 0;
		while( delimIndex >= 0 ) {
			strs.add( str.substring( index, delimIndex ) );
			index = delimIndex + 1;
			delimIndex = str.indexOf( delim, index );
		}
		strs.add( str.substring( index ) );

		return strs;
	}
}
