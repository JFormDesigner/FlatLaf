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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Level;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIDefaults.ActiveValue;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.ColorFunctions.ColorFunction;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.GrayFilter;
import com.formdev.flatlaf.util.HSLColor;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

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
	private static final String PROPERTY_PREFIX = "$";
	private static final String OPTIONAL_PREFIX = "?";
	private static final String GLOBAL_PREFIX = "*.";

	static void loadDefaultsFromProperties( Class<?> lookAndFeelClass, List<FlatDefaultsAddon> addons,
		Properties additionalDefaults, boolean dark, UIDefaults defaults )
	{
		// determine classes in class hierarchy in reverse order
		ArrayList<Class<?>> lafClasses = new ArrayList<>();
		for( Class<?> lafClass = lookAndFeelClass;
			FlatLaf.class.isAssignableFrom( lafClass );
			lafClass = lafClass.getSuperclass() )
		{
			lafClasses.add( 0, lafClass );
		}

		loadDefaultsFromProperties( lafClasses, addons, additionalDefaults, dark, defaults );
	}

	static void loadDefaultsFromProperties( List<Class<?>> lafClasses, List<FlatDefaultsAddon> addons,
		Properties additionalDefaults, boolean dark, UIDefaults defaults )
	{
		try {
			// load core properties files
			Properties properties = new Properties();
			for( Class<?> lafClass : lafClasses ) {
				String propertiesName = '/' + lafClass.getName().replace( '.', '/' ) + ".properties";
				try( InputStream in = lafClass.getResourceAsStream( propertiesName ) ) {
					if( in != null )
						properties.load( in );
				}
			}

			// load properties from addons
			for( FlatDefaultsAddon addon : addons ) {
				for( Class<?> lafClass : lafClasses ) {
					try( InputStream in = addon.getDefaults( lafClass ) ) {
						if( in != null )
							properties.load( in );
					}
				}
			}

			// collect addon class loaders
			List<ClassLoader> addonClassLoaders = new ArrayList<>();
			for( FlatDefaultsAddon addon : addons ) {
				ClassLoader addonClassLoader = addon.getClass().getClassLoader();
				if( !addonClassLoaders.contains( addonClassLoader ) )
					addonClassLoaders.add( addonClassLoader );
			}

			// add additional defaults
			if( additionalDefaults != null )
				properties.putAll( additionalDefaults );

			// collect all platform specific keys (but do not modify properties)
			ArrayList<String> platformSpecificKeys = new ArrayList<>();
			for( Object okey : properties.keySet() ) {
				String key = (String) okey;
				if( key.startsWith( "[" ) &&
					(key.startsWith( "[win]" ) ||
					 key.startsWith( "[mac]" ) ||
					 key.startsWith( "[linux]" ) ||
					 key.startsWith( "[light]" ) ||
					 key.startsWith( "[dark]" )) )
				  platformSpecificKeys.add( key );
			}

			// remove platform specific properties and re-add only properties
			// for current platform, but with platform prefix removed
			if( !platformSpecificKeys.isEmpty() ) {
				// handle light/dark specific properties
				String lightOrDarkPrefix = dark ? "[dark]" : "[light]";
				for( String key : platformSpecificKeys ) {
					if( key.startsWith( lightOrDarkPrefix ) )
						properties.put( key.substring( lightOrDarkPrefix.length() ), properties.remove( key ) );
				}

				// handle platform specific properties
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
					globals.put( key.substring( GLOBAL_PREFIX.length() ), parseValue( key, value, resolver, addonClassLoaders ) );
				} catch( RuntimeException ex ) {
					logParseError( Level.SEVERE, key, value, ex );
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
					defaults.put( key, parseValue( key, value, resolver, addonClassLoaders ) );
				} catch( RuntimeException ex ) {
					logParseError( Level.SEVERE, key, value, ex );
				}
			}
		} catch( IOException ex ) {
			FlatLaf.LOG.log( Level.SEVERE, "FlatLaf: Failed to load properties files.", ex );
		}
	}

	static void logParseError( Level level, String key, String value, RuntimeException ex ) {
		FlatLaf.LOG.log( level, "FlatLaf: Failed to parse: '" + key + '=' + value + '\'', ex );
	}

	private static String resolveValue( Properties properties, String value ) {
		if( value.startsWith( PROPERTY_PREFIX ) )
			value = value.substring( PROPERTY_PREFIX.length() );
		else if( !value.startsWith( VARIABLE_PREFIX ) )
			return value;

		boolean optional = false;
		if( value.startsWith( OPTIONAL_PREFIX ) ) {
			value = value.substring( OPTIONAL_PREFIX.length() );
			optional = true;
		}

		String newValue = properties.getProperty( value );
		if( newValue == null ) {
			if( optional )
				return "null";

			throw new IllegalArgumentException( "variable or property '" + value + "' not found" );
		}

		return resolveValue( properties, newValue );
	}

	private enum ValueType { UNKNOWN, STRING, CHARACTER, INTEGER, FLOAT, BORDER, ICON, INSETS, DIMENSION, COLOR,
		SCALEDINTEGER, SCALEDFLOAT, SCALEDINSETS, SCALEDDIMENSION, INSTANCE, CLASS, GRAYFILTER }

	static Object parseValue( String key, String value ) {
		return parseValue( key, value, v -> v, Collections.emptyList() );
	}

	private static Object parseValue( String key, String value, Function<String, String> resolver, List<ClassLoader> addonClassLoaders ) {
		value = value.trim();

		// null, false, true
		switch( value ) {
			case "null":	return null;
			case "false":	return false;
			case "true":	return true;
		}

		// check for function "lazy"
		//     Syntax: lazy(uiKey)
		if( value.startsWith( "lazy(" ) && value.endsWith( ")" ) ) {
			String uiKey = value.substring( 5, value.length() - 1 ).trim();
			return (LazyValue) t -> {
				return lazyUIManagerGet( uiKey );
			};
		}

		ValueType valueType = ValueType.UNKNOWN;

		// check whether value type is specified in the value
		if( value.startsWith( "#" ) )
			valueType = ValueType.COLOR;
		else if( value.startsWith( "\"" ) && value.endsWith( "\"" ) ) {
			valueType = ValueType.STRING;
			value = value.substring( 1, value.length() - 1 );
		} else if( value.startsWith( TYPE_PREFIX ) ) {
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
				valueType = ValueType.DIMENSION;
			else if( key.endsWith( "Width" ) || key.endsWith( "Height" ) )
				valueType = ValueType.INTEGER;
			else if( key.endsWith( "Char" ) )
				valueType = ValueType.CHARACTER;
			else if( key.endsWith( "UI" ) )
				valueType = ValueType.STRING;
			else if( key.endsWith( "grayFilter" ) )
				valueType = ValueType.GRAYFILTER;
		}

		// parse value
		switch( valueType ) {
			case STRING:		return value;
			case CHARACTER:		return parseCharacter( value );
			case INTEGER:		return parseInteger( value, true );
			case FLOAT:			return parseFloat( value, true );
			case BORDER:		return parseBorder( value, resolver, addonClassLoaders );
			case ICON:			return parseInstance( value, addonClassLoaders );
			case INSETS:		return parseInsets( value );
			case DIMENSION:		return parseDimension( value );
			case COLOR:			return parseColorOrFunction( value, resolver, true );
			case SCALEDINTEGER:	return parseScaledInteger( value );
			case SCALEDFLOAT:	return parseScaledFloat( value );
			case SCALEDINSETS:	return parseScaledInsets( value );
			case SCALEDDIMENSION:return parseScaledDimension( value );
			case INSTANCE:		return parseInstance( value, addonClassLoaders );
			case CLASS:			return parseClass( value, addonClassLoaders );
			case GRAYFILTER:	return parseGrayFilter( value );
			case UNKNOWN:
			default:
				// colors
				Object color = parseColorOrFunction( value, resolver, false );
				if( color != null )
					return color;

				// integer
				Integer integer = parseInteger( value, false );
				if( integer != null )
					return integer;

				// float
				Float f = parseFloat( value, false );
				if( f != null )
					return f;

				// string
				return value;
		}
	}

	private static Object parseBorder( String value, Function<String, String> resolver, List<ClassLoader> addonClassLoaders ) {
		if( value.indexOf( ',' ) >= 0 ) {
			// top,left,bottom,right[,lineColor[,lineThickness]]
			List<String> parts = split( value, ',' );
			Insets insets = parseInsets( value );
			ColorUIResource lineColor = (parts.size() >= 5)
				? (ColorUIResource) parseColorOrFunction( resolver.apply( parts.get( 4 ) ), resolver, true )
				: null;
			float lineThickness = (parts.size() >= 6) ? parseFloat( parts.get( 5 ), true ) : 1f;

			return (LazyValue) t -> {
				return (lineColor != null)
					? new FlatLineBorder( insets, lineColor, lineThickness )
					: new FlatEmptyBorder( insets );
			};
		} else
			return parseInstance( value, addonClassLoaders );
	}

	private static Object parseInstance( String value, List<ClassLoader> addonClassLoaders ) {
		return (LazyValue) t -> {
			try {
				return findClass( value, addonClassLoaders ).newInstance();
			} catch( InstantiationException | IllegalAccessException | ClassNotFoundException ex ) {
				FlatLaf.LOG.log( Level.SEVERE, "FlatLaf: Failed to instantiate '" + value + "'.", ex );
				return null;
			}
		};
	}

	private static Object parseClass( String value, List<ClassLoader> addonClassLoaders ) {
		return (LazyValue) t -> {
			try {
				return findClass( value, addonClassLoaders );
			} catch( ClassNotFoundException ex ) {
				FlatLaf.LOG.log( Level.SEVERE, "FlatLaf: Failed to find class '" + value + "'.", ex );
				return null;
			}
		};
	}

	private static Class<?> findClass( String className, List<ClassLoader> addonClassLoaders )
		throws ClassNotFoundException
	{
		try {
			return Class.forName( className );
		} catch( ClassNotFoundException ex ) {
			// search in addons class loaders
			for( ClassLoader addonClassLoader : addonClassLoaders ) {
				try {
					return addonClassLoader.loadClass( className );
				} catch( ClassNotFoundException ex2 ) {
					// ignore
				}
			}
			throw ex;
		}
	}

	private static Insets parseInsets( String value ) {
		List<String> numbers = split( value, ',' );
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

	private static Dimension parseDimension( String value ) {
		List<String> numbers = split( value, ',' );
		try {
			return new DimensionUIResource(
				Integer.parseInt( numbers.get( 0 ) ),
				Integer.parseInt( numbers.get( 1 ) ) );
		} catch( NumberFormatException ex ) {
			throw new IllegalArgumentException( "invalid size '" + value + "'" );
		}
	}

	private static Object parseColorOrFunction( String value, Function<String, String> resolver, boolean reportError ) {
		if( value.endsWith( ")" ) )
			return parseColorFunctions( value, resolver, reportError );

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

	private static Object parseColorFunctions( String value, Function<String, String> resolver, boolean reportError ) {
		int paramsStart = value.indexOf( '(' );
		if( paramsStart < 0 ) {
			if( reportError )
				throw new IllegalArgumentException( "missing opening parenthesis in function '" + value + "'" );
			return null;
		}

		String function = value.substring( 0, paramsStart ).trim();
		List<String> params = splitFunctionParams( value.substring( paramsStart + 1, value.length() - 1 ), ',' );
		if( params.isEmpty() )
			throw new IllegalArgumentException( "missing parameters in function '" + value + "'" );

		switch( function ) {
			case "rgb":			return parseColorRgbOrRgba( false, params, resolver, reportError );
			case "rgba":		return parseColorRgbOrRgba( true, params, resolver, reportError );
			case "hsl":			return parseColorHslOrHsla( false, params );
			case "hsla":		return parseColorHslOrHsla( true, params );
			case "lighten":		return parseColorHSLIncreaseDecrease( 2, true, params, resolver, reportError );
			case "darken":		return parseColorHSLIncreaseDecrease( 2, false, params, resolver, reportError );
			case "saturate":	return parseColorHSLIncreaseDecrease( 1, true, params, resolver, reportError );
			case "desaturate":	return parseColorHSLIncreaseDecrease( 1, false, params, resolver, reportError );
		}

		throw new IllegalArgumentException( "unknown color function '" + value + "'" );
	}

	/**
	 * Syntax: rgb(red,green,blue) or rgba(red,green,blue,alpha) or rgba(color,alpha)
	 *   - red:   an integer 0-255 or a percentage 0-100%
	 *   - green: an integer 0-255 or a percentage 0-100%
	 *   - blue:  an integer 0-255 or a percentage 0-100%
	 *   - alpha: an integer 0-255 or a percentage 0-100%
	 */
	private static ColorUIResource parseColorRgbOrRgba( boolean hasAlpha, List<String> params,
		Function<String, String> resolver, boolean reportError )
	{
		if( hasAlpha && params.size() == 2 ) {
			// syntax rgba(color,alpha), which allows adding alpha to any color
			String colorStr = params.get( 0 );
			int alpha = parseInteger( params.get( 1 ), 0, 255, true );

			ColorUIResource color = (ColorUIResource) parseColorOrFunction( resolver.apply( colorStr ), resolver, reportError );
			return new ColorUIResource( new Color( ((alpha & 0xff) << 24) | (color.getRGB() & 0xffffff), true ) );
		}

		int red = parseInteger( params.get( 0 ), 0, 255, true );
		int green = parseInteger( params.get( 1 ), 0, 255, true );
		int blue = parseInteger( params.get( 2 ), 0, 255, true );
		int alpha = hasAlpha ? parseInteger( params.get( 3 ), 0, 255, true ) : 255;

		return hasAlpha
			? new ColorUIResource( new Color( red, green, blue, alpha ) )
			: new ColorUIResource( red, green, blue );
	}

	/**
	 * Syntax: hsl(hue,saturation,lightness) or hsla(hue,saturation,lightness,alpha)
	 *   - hue: an integer 0-360 representing degrees
	 *   - saturation: a percentage 0-100%
	 *   - lightness: a percentage 0-100%
	 *   - alpha: a percentage 0-100%
	 */
	private static ColorUIResource parseColorHslOrHsla( boolean hasAlpha, List<String> params ) {
		int hue = parseInteger( params.get( 0 ), 0, 360, false );
		int saturation = parsePercentage( params.get( 1 ) );
		int lightness = parsePercentage( params.get( 2 ) );
		int alpha = hasAlpha ? parsePercentage( params.get( 3 ) ) : 100;

		float[] hsl = new float[] { hue, saturation, lightness };
		return new ColorUIResource( HSLColor.toRGB( hsl, alpha / 100f ) );
	}

	/**
	 * Syntax: lighten(color,amount[,options]) or darken(color,amount[,options]) or
	 *         saturate(color,amount[,options]) or desaturate(color,amount[,options])
	 *   - color: a color (e.g. #f00) or a color function
	 *   - amount: percentage 0-100%
	 *   - options: [relative] [autoInverse] [noAutoInverse] [lazy] [derived]
	 */
	private static Object parseColorHSLIncreaseDecrease( int hslIndex, boolean increase,
		List<String> params, Function<String, String> resolver, boolean reportError )
	{
		String colorStr = params.get( 0 );
		int amount = parsePercentage( params.get( 1 ) );
		boolean relative = false;
		boolean autoInverse = false;
		boolean lazy = false;
		boolean derived = false;

		if( params.size() > 2 ) {
			String options = params.get( 2 );
			relative = options.contains( "relative" );
			autoInverse = options.contains( "autoInverse" );
			lazy = options.contains( "lazy" );
			derived = options.contains( "derived" );

			// use autoInverse by default for derived colors, except if noAutoInverse is set
			if( derived && !options.contains( "noAutoInverse" ) )
				autoInverse = true;
		}

		// create function
		ColorFunction function = new ColorFunctions.HSLIncreaseDecrease(
			hslIndex, increase, amount, relative, autoInverse );

		if( lazy ) {
			return (LazyValue) t -> {
				Object color = lazyUIManagerGet( colorStr );
				return (color instanceof Color)
					? new ColorUIResource( ColorFunctions.applyFunctions( (Color) color, function ) )
					: null;
			};
		}

		// parse base color
		String resolvedColorStr = resolver.apply( colorStr );
		ColorUIResource baseColor = (ColorUIResource) parseColorOrFunction( resolvedColorStr, resolver, reportError );

		// apply this function to base color
		Color newColor = ColorFunctions.applyFunctions( baseColor, function );

		if( derived ) {
			ColorFunction[] functions;
			if( baseColor instanceof DerivedColor && resolvedColorStr == colorStr ) {
				// if the base color is also derived, join the color functions
				// but only if base color function is specified directly in this function
				ColorFunction[] baseFunctions = ((DerivedColor)baseColor).getFunctions();
				functions = new ColorFunction[baseFunctions.length + 1];
				System.arraycopy( baseFunctions, 0, functions, 0, baseFunctions.length );
				functions[baseFunctions.length] = function;
			} else
				functions = new ColorFunction[] { function };

			return new DerivedColor( newColor, functions );
		}

		return new ColorUIResource( newColor );
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

	private static Character parseCharacter( String value ) {
		if( value.length() != 1 )
			throw new IllegalArgumentException( "invalid character '" + value + "'" );
		return value.charAt( 0 );
	}

	private static Integer parseInteger( String value, int min, int max, boolean allowPercentage ) {
		if( allowPercentage && value.endsWith( "%" ) ) {
			int percent = parsePercentage( value );
			return (max * percent) / 100;
		}

		Integer integer = parseInteger( value, true );
		if( integer.intValue() < min || integer.intValue() > max )
			throw new NumberFormatException( "integer '" + value + "' out of range (" + min + '-' + max + ')' );
		return integer;
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

	private static Float parseFloat( String value, boolean reportError ) {
		try {
			return Float.parseFloat( value );
		} catch( NumberFormatException ex ) {
			if( reportError )
				throw new NumberFormatException( "invalid float '" + value + "'" );
		}
		return null;
	}

	private static ActiveValue parseScaledInteger( String value ) {
		int val = parseInteger( value, true );
		return (ActiveValue) t -> {
			return UIScale.scale( val );
		};
	}

	private static ActiveValue parseScaledFloat( String value ) {
		float val = parseFloat( value, true );
		return (ActiveValue) t -> {
			return UIScale.scale( val );
		};
	}

	private static ActiveValue parseScaledInsets( String value ) {
		Insets insets = parseInsets( value );
		return (ActiveValue) t -> {
			return UIScale.scale( insets );
		};
	}

	private static ActiveValue parseScaledDimension( String value ) {
		Dimension dimension = parseDimension( value );
		return (ActiveValue) t -> {
			return UIScale.scale( dimension );
		};
	}

	private static Object parseGrayFilter( String value ) {
		List<String> numbers = split( value, ',' );
		try {
			int brightness = Integer.parseInt( numbers.get( 0 ) );
			int contrast = Integer.parseInt( numbers.get( 1 ) );
			int alpha = Integer.parseInt( numbers.get( 2 ) );

			return (LazyValue) t -> {
				return new GrayFilter( brightness, contrast, alpha );
			};
		} catch( NumberFormatException ex ) {
			throw new IllegalArgumentException( "invalid gray filter '" + value + "'" );
		}
	}

	/**
	 * Split string and trim parts.
	 */
	private static List<String> split( String str, char delim ) {
		List<String> result = StringUtils.split( str, delim );

		// trim strings
		int size = result.size();
		for( int i = 0; i < size; i++ )
			result.set( i, result.get( i ).trim() );

		return result;
	}

	/**
	 * Splits function parameters and allows using functions as parameters.
	 * In other words: Delimiters surrounded by '(' and ')' are ignored.
	 */
	private static List<String> splitFunctionParams( String str, char delim ) {
		ArrayList<String> strs = new ArrayList<>();
		int nestLevel = 0;
		int start = 0;
		int strlen = str.length();
		for( int i = 0; i < strlen; i++ ) {
			char ch = str.charAt( i );
			if( ch == '(' )
				nestLevel++;
			else if( ch == ')' )
				nestLevel--;
			else if( nestLevel == 0 && ch == delim ) {
				strs.add( str.substring( start, i ).trim() );
				start = i + 1;
			}
		}
		strs.add( str.substring( start ).trim() );

		return strs;
	}

	/**
	 * For use in LazyValue to get value for given key from UIManager and report error
	 * if not found. If key is prefixed by '?', then no error is reported.
	 */
	private static Object lazyUIManagerGet( String uiKey ) {
		boolean optional = false;
		if( uiKey.startsWith( OPTIONAL_PREFIX ) ) {
			uiKey = uiKey.substring( OPTIONAL_PREFIX.length() );
			optional = true;
		}

		Object value = UIManager.get( uiKey );
		if( value == null && !optional )
			FlatLaf.LOG.log( Level.SEVERE, "FlatLaf: '" + uiKey + "' not found in UI defaults." );
		return value;
	}
}
