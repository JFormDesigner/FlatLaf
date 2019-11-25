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
import java.awt.Dimension;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Function;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.ScaledNumber;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Load UI defaults from properties files associated to Flat LaF classes and add to UI defaults.
 *
 * Each class that extend the LaF class may have its own .properties file
 * in the same package as the class. Properties from superclasses are loaded
 * first to give subclasses a chance to override defaults.
 * E.g. if running FlatDarkLaf, then the FlatLaf.properties is loaded first
 * and FlatDarkLaf.properties loaded second.
 *
 * @author Karl Tauber
 */
class UIDefaultsLoader
{
	private static final String TYPE_PREFIX = "{";
	private static final String TYPE_PREFIX_END = "}";
	private static final String VARIABLE_PREFIX = "@";
	private static final String REF_PREFIX = VARIABLE_PREFIX + "@";
	private static final String OPTIONAL_PREFIX = "?";
	private static final String GLOBAL_PREFIX = "*.";

	static void loadDefaultsFromProperties( Class<?> lookAndFeelClass, UIDefaults defaults ) {
		// determine classes in class hierarchy in reverse order
		ArrayList<Class<?>> lafClasses = new ArrayList<>();
		for( Class<?> lafClass = lookAndFeelClass;
			FlatLaf.class.isAssignableFrom( lafClass );
			lafClass = lafClass.getSuperclass() )
		{
			lafClasses.add( 0, lafClass );
		}

		loadDefaultsFromProperties( lafClasses, defaults );
	}

	static void loadDefaultsFromProperties( List<Class<?>> lafClasses, UIDefaults defaults ) {
		try {
			// load properties files
			Properties properties = new Properties();
			ServiceLoader<FlatDefaultsAddon> addonLoader = ServiceLoader.load( FlatDefaultsAddon.class );
			for( Class<?> lafClass : lafClasses ) {
				// load core properties
				String propertiesName = "/" + lafClass.getName().replace( '.', '/' ) + ".properties";
				try( InputStream in = lafClass.getResourceAsStream( propertiesName ) ) {
					if( in != null )
						properties.load( in );
				}

				// load properties from addons
				for( FlatDefaultsAddon addon : addonLoader ) {
					try( InputStream in = addon.getDefaults( lafClass ) ) {
						if( in != null )
							properties.load( in );
					}
				}
			}

			// collect all platform specific keys (but do not modify properties)
			ArrayList<String> platformSpecificKeys = new ArrayList<>();
			for( Object key : properties.keySet() ) {
				if( ((String)key).startsWith( "[" ) )
					platformSpecificKeys.add( (String) key );
			}

			// remove platform specific properties and re-add only properties
			// for current platform, but with platform prefix removed
			if( !platformSpecificKeys.isEmpty() ) {
				String platformPrefix =
					SystemInfo.IS_WINDOWS ? "[win]" :
					SystemInfo.IS_MAC ? "[mac]" :
					SystemInfo.IS_LINUX ? "[linux]" : "[unknown]";
				for( String key : platformSpecificKeys ) {
					Object value = properties.remove( key );
					if( key.startsWith( platformPrefix ) )
						properties.put( key.substring( platformPrefix.length() ), value );
				}
			}

			Function<String, String> resolver = value -> {
				return resolveValue( properties, value );
			};

			// get globals, which override all other defaults that end with same suffix
			HashMap<String, Object> globals = new HashMap<>();
			for( Map.Entry<Object, Object> e : properties.entrySet() ) {
				String key = (String) e.getKey();
				if( !key.startsWith( GLOBAL_PREFIX ) )
					continue;

				String value = resolveValue( properties, (String) e.getValue() );
				try {
					globals.put( key.substring( GLOBAL_PREFIX.length() ), parseValue( key, value, resolver ) );
				} catch( RuntimeException ex ) {
					logParseError( key, value, ex );
				}
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

				String value = resolveValue( properties, (String) e.getValue() );
				try {
					defaults.put( key, parseValue( key, value, resolver ) );
				} catch( RuntimeException ex ) {
					logParseError( key, value, ex );
				}
			}
		} catch( IOException ex ) {
			ex.printStackTrace();
		}
	}

	static void logParseError( String key, String value, RuntimeException ex ) {
		System.err.println( "Failed to parse: '" + key + '=' + value + '\'' );
		System.err.println( "    " + ex.getMessage() );
	}

	private static String resolveValue( Properties properties, String value ) {
		if( !value.startsWith( VARIABLE_PREFIX ) )
			return value;

		if( value.startsWith( REF_PREFIX ) )
			value = value.substring( REF_PREFIX.length() );

		boolean optional = false;
		if( value.startsWith( OPTIONAL_PREFIX ) ) {
			value = value.substring( OPTIONAL_PREFIX.length() );
			optional = true;
		}

		String newValue = properties.getProperty( value );
		if( newValue == null ) {
			if( optional )
				return "null";

			throw new IllegalArgumentException( "variable or reference '" + value + "' not found" );
		}

		return resolveValue( properties, newValue );
	}

	private enum ValueType { UNKNOWN, STRING, INTEGER, BORDER, ICON, INSETS, SIZE, COLOR, SCALEDNUMBER }

	static Object parseValue( String key, String value ) {
		return parseValue( key, value, v -> v );
	}

	private static Object parseValue( String key, String value, Function<String, String> resolver ) {
		value = value.trim();

		// null, false, true
		switch( value ) {
			case "null":	return null;
			case "false":	return false;
			case "true":	return true;
		}

		ValueType valueType = ValueType.UNKNOWN;

		// check whether value type is specified in the value
		if( value.startsWith( "#" ) )
			valueType = ValueType.COLOR;
		else if( value.startsWith( TYPE_PREFIX ) ) {
			int end = value.indexOf( TYPE_PREFIX_END );
			if( end != -1 ) {
				try {
					String typeStr = value.substring( TYPE_PREFIX.length(), end );
					valueType = ValueType.valueOf( typeStr.toUpperCase( Locale.ENGLISH ) );

					// remove type from value
					value = value.substring( end + TYPE_PREFIX_END.length() );
				} catch( IllegalArgumentException ex ) {
					// ignore
				}
			}
		}

		// determine value type from key
		if( valueType == ValueType.UNKNOWN ) {
			if( key.endsWith( "ground" ) || key.endsWith( "Color" ) )
				valueType = ValueType.COLOR;
			else if( key.endsWith( ".border" ) || key.endsWith( "Border" ) )
				valueType = ValueType.BORDER;
			else if( key.endsWith( ".icon" ) || key.endsWith( "Icon" ) )
				valueType = ValueType.ICON;
			else if( key.endsWith( ".margin" ) || key.endsWith( ".padding" ) ||
					 key.endsWith( "Margins" ) || key.endsWith( "Insets" ) )
				valueType = ValueType.INSETS;
			else if( key.endsWith( "Size" ) )
				valueType = ValueType.SIZE;
			else if( key.endsWith( "Width" ) || key.endsWith( "Height" ) )
				valueType = ValueType.INTEGER;
			else if( key.endsWith( "UI" ) )
				valueType = ValueType.STRING;
		}

		// parse value
		switch( valueType ) {
			case STRING:		return value;
			case INTEGER:		return parseInteger( value, true );
			case BORDER:		return parseBorder( value, resolver );
			case ICON:			return parseInstance( value );
			case INSETS:		return parseInsets( value );
			case SIZE:			return parseSize( value );
			case COLOR:			return parseColorOrFunction( value, true );
			case SCALEDNUMBER:	return parseScaledNumber( value );
			case UNKNOWN:
			default:
				// colors
				ColorUIResource color = parseColorOrFunction( value, false );
				if( color != null )
					return color;

				// integer
				Integer integer = parseInteger( value, false );
				if( integer != null )
					return integer;

				// string
				return value;
		}
	}

	private static Object parseBorder( String value, Function<String, String> resolver ) {
		if( value.indexOf( ',' ) >= 0 ) {
			// top,left,bottom,right[,lineColor]
			List<String> parts = StringUtils.split( value, ',' );
			Insets insets = parseInsets( value );
			ColorUIResource lineColor = (parts.size() == 5)
				? parseColorOrFunction( resolver.apply( parts.get( 4 ) ), true )
				: null;

			return (LazyValue) t -> {
				return (lineColor != null)
					? new FlatLineBorder( insets, lineColor )
					: new FlatEmptyBorder( insets );
			};
		} else
			return parseInstance( value );
	}

	private static Object parseInstance( String value ) {
		return (LazyValue) t -> {
			try {
				return Class.forName( value ).newInstance();
			} catch( InstantiationException | IllegalAccessException | ClassNotFoundException ex ) {
				ex.printStackTrace();
				return null;
			}
		};
	}

	private static Insets parseInsets( String value ) {
		List<String> numbers = StringUtils.split( value, ',' );
		try {
			return new InsetsUIResource(
				Integer.parseInt( numbers.get( 0 ) ),
				Integer.parseInt( numbers.get( 1 ) ),
				Integer.parseInt( numbers.get( 2 ) ),
				Integer.parseInt( numbers.get( 3 ) ) );
		} catch( NumberFormatException ex ) {
			throw new IllegalArgumentException( "invalid insets '" + value + "'" );
		}
	}

	private static Dimension parseSize( String value ) {
		List<String> numbers = StringUtils.split( value, ',' );
		try {
			return new DimensionUIResource(
				Integer.parseInt( numbers.get( 0 ) ),
				Integer.parseInt( numbers.get( 1 ) ) );
		} catch( NumberFormatException ex ) {
			throw new IllegalArgumentException( "invalid size '" + value + "'" );
		}
	}

	private static ColorUIResource parseColorOrFunction( String value, boolean reportError ) {
		if( value.endsWith( ")" ) )
			return parseColorFunctions( value, reportError );

		return parseColor( value, reportError );
	}

	static ColorUIResource parseColor( String value ) {
		return parseColor( value, false );
	}

	private static ColorUIResource parseColor( String value, boolean reportError ) {
		try {
			int rgba = parseColorRGBA( value );
			return ((rgba & 0xff000000) == 0xff000000)
				? new ColorUIResource( rgba )
				: new ColorUIResource( new Color( rgba, true ) );
		} catch( IllegalArgumentException ex ) {
			if( reportError )
				throw new IllegalArgumentException( "invalid color '" + value + "'" );

			// not a color --> ignore
		}
		return null;
	}

	/**
	 * Parses a hex color in  {@code #RGB}, {@code #RGBA}, {@code #RRGGBB} or {@code #RRGGBBAA}
	 * format and returns it as {@code rgba} integer suitable for {@link java.awt.Color},
	 * which includes alpha component in bits 24-31.
	 *
	 * @throws IllegalArgumentException
	 */
	static int parseColorRGBA( String value ) {
		int len = value.length();
		if( (len != 4 && len != 5 && len != 7 && len != 9) || value.charAt( 0 ) != '#' )
			throw new IllegalArgumentException();

		// parse hex
		int n = 0;
		for( int i = 1; i < len; i++ ) {
			char ch = value.charAt( i );

			int digit;
			if( ch >= '0' && ch <= '9' )
				digit = ch - '0';
			else if( ch >= 'a' && ch <= 'f' )
				digit = ch - 'a' + 10;
			else if( ch >= 'A' && ch <= 'F' )
				digit = ch - 'A' + 10;
			else
				throw new IllegalArgumentException();

			n = (n << 4) | digit;
		}

		if( len <= 5 ) {
			// double nibbles
			int n1 = n & 0xf000;
			int n2 = n & 0xf00;
			int n3 = n & 0xf0;
			int n4 = n & 0xf;
			n = (n1 << 16) | (n1 << 12) | (n2 << 12) | (n2 << 8) | (n3 << 8) | (n3 << 4) | (n4 << 4) | n4;
		}

		return (len == 4 || len == 7)
			? (0xff000000 | n) // set alpha to 255
			: (((n >> 8) & 0xffffff) | ((n & 0xff) << 24)); // move alpha from lowest to highest byte
	}

	private static ColorUIResource parseColorFunctions( String value, boolean reportError ) {
		int paramsStart = value.indexOf( '(' );
		if( paramsStart < 0 ) {
			if( reportError )
				throw new IllegalArgumentException( "missing opening parenthesis in function '" + value + "'" );
			return null;
		}

		String function = value.substring( 0, paramsStart ).trim();
		List<String> params = StringUtils.split( value.substring( paramsStart + 1, value.length() - 1 ), ',' );
		if( params.isEmpty() )
			throw new IllegalArgumentException( "missing parameters in function '" + value + "'" );

		switch( function ) {
			case "lighten":		return parseColorLightenOrDarken( true, params, reportError );
			case "darken":		return parseColorLightenOrDarken( false, params, reportError );
		}

		throw new IllegalArgumentException( "unknown color function '" + value + "'" );
	}

	/**
	 * Syntax: lighten(amount[,options]) or darken(amount[,options])
	 *   - amount: percentage 0-100%
	 *   - options: [relative] [autoInverse]
	 */
	private static ColorUIResource parseColorLightenOrDarken( boolean lighten, List<String> params, boolean reportError ) {
		int amount = parsePercentage( params.get( 0 ) );
		boolean relative = false;
		boolean autoInverse = false;

		if( params.size() >= 2 ) {
			String options = params.get( 1 );
			relative = options.contains( "relative" );
			autoInverse = options.contains( "autoInverse" );
		}

		return new DerivedColor( lighten
			? new ColorFunctions.Lighten( amount, relative, autoInverse )
			: new ColorFunctions.Darken( amount, relative, autoInverse ) );
	}

	private static int parsePercentage( String value ) {
		if( !value.endsWith( "%" ) )
			throw new NumberFormatException( "invalid percentage '" + value + "'" );

		int val;
		try {
			val = Integer.parseInt( value.substring( 0, value.length() - 1 ) );
		} catch( NumberFormatException ex ) {
			throw new NumberFormatException( "invalid percentage '" + value + "'" );
		}

		if( val < 0 || val > 100 )
			throw new IllegalArgumentException( "percentage out of range (0-100%) '" + value + "'" );
		return val;
	}

	private static Integer parseInteger( String value, boolean reportError ) {
		try {
			return Integer.parseInt( value );
		} catch( NumberFormatException ex ) {
			if( reportError )
				throw new NumberFormatException( "invalid integer '" + value + "'" );
		}
		return null;
	}

	private static ScaledNumber parseScaledNumber( String value ) {
		try {
			return new ScaledNumber( Integer.parseInt( value ) );
		} catch( NumberFormatException ex ) {
			throw new NumberFormatException( "invalid integer '" + value + "'" );
		}
	}
}
