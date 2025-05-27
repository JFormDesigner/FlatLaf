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
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.Icon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
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
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SoftCache;
import com.formdev.flatlaf.util.StringUtils;
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
	private static final String WILDCARD_PREFIX = "*.";

	static final String KEY_VARIABLES = "FlatLaf.internal.variables";
	static final String KEY_PROPERTIES = "FlatLaf.internal.properties";

	private static int parseColorDepth;

	private static Map<String, ColorUIResource> systemColorCache;
	private static final SoftCache<String, Object> fontCache = new SoftCache<>();

	static ArrayList<Class<?>> getLafClassesForDefaultsLoading( Class<?> lookAndFeelClass ) {
		// determine classes in class hierarchy in reverse order
		ArrayList<Class<?>> lafClasses = new ArrayList<>();
		for( Class<?> lafClass = lookAndFeelClass;
			FlatLaf.class.isAssignableFrom( lafClass );
			lafClass = lafClass.getSuperclass() )
		{
			lafClasses.add( 0, lafClass );
		}
		return lafClasses;
	}

	static Properties newUIProperties( boolean dark ) {
		// UI key prefixes
		String lightOrDarkPrefix = FlatLaf.getUIKeyLightOrDarkPrefix( dark );
		Set<String> platformPrefixes = FlatLaf.getUIKeyPlatformPrefixes();
		Set<String> specialPrefixes = FlatLaf.getUIKeySpecialPrefixes();

		return new Properties() {
			@Override
			public synchronized Object put( Object k, Object value ) {
				// process key prefixes (while loading properties files)
				String key = (String) k;
				while( key.startsWith( "[" ) ) {
					int closeIndex = key.indexOf( ']' );
					if( closeIndex < 0 )
						return null; // ignore property with invalid prefix

					String prefix = key.substring( 0, closeIndex + 1 );

					if( specialPrefixes.contains( prefix ) )
						break; // keep special prefix

					if( !lightOrDarkPrefix.equals( prefix ) && !platformPrefixes.contains( prefix ) )
						return null; // ignore property

					// prefix is known and enabled --> remove prefix
					key = key.substring( closeIndex + 1 );
				}

				return super.put( key, value );
			}
		};
	}

	static void loadDefaultsFromProperties( List<Class<?>> lafClasses, List<FlatDefaultsAddon> addons,
		Consumer<Properties> intellijThemesHook, Properties additionalDefaults, boolean dark, UIDefaults defaults )
	{
		try {
			// temporary cache system colors while loading defaults,
			// which avoids that system color getter is invoked multiple times
			systemColorCache = (FlatLaf.getSystemColorGetter() != null) ? new HashMap<>() : null;

			// all properties files will be loaded into this map
			Properties properties = newUIProperties( dark );

			// load core properties files
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

			// apply IntelliJ themes properties
			if( intellijThemesHook != null )
				intellijThemesHook.accept( properties );

			// load custom properties files (usually provided by applications)
			List<Object> customDefaultsSources = FlatLaf.getCustomDefaultsSources();
			int size = (customDefaultsSources != null) ? customDefaultsSources.size() : 0;
			for( int i = 0; i < size; i++ ) {
				Object source = customDefaultsSources.get( i );
				if( source instanceof String && i + 1 < size ) {
					// load from package in classloader
					String packageName = (String) source;
					ClassLoader classLoader = (ClassLoader) customDefaultsSources.get( ++i );

					// use class loader also for instantiating classes specified in values
					if( classLoader != null && !addonClassLoaders.contains( classLoader ) )
						addonClassLoaders.add( classLoader );

					packageName = packageName.replace( '.', '/' );
					if( classLoader == null )
						classLoader = FlatLaf.class.getClassLoader();

					for( Class<?> lafClass : lafClasses ) {
						String propertiesName = packageName + '/' + simpleClassName( lafClass ) + ".properties";
						try( InputStream in = classLoader.getResourceAsStream( propertiesName ) ) {
							if( in != null )
								properties.load( in );
						}
					}
				} else if( source instanceof URL ) {
					// load from package URL
					URL packageUrl = (URL) source;
					for( Class<?> lafClass : lafClasses ) {
						URL propertiesUrl = new URL( packageUrl + simpleClassName( lafClass ) + ".properties" );

						try( InputStream in = propertiesUrl.openStream() ) {
							properties.load( in );
						} catch( FileNotFoundException ex ) {
							// ignore
						}
					}
				} else if( source instanceof File ) {
					// load from folder
					File folder = (File) source;
					for( Class<?> lafClass : lafClasses ) {
						File propertiesFile = new File( folder, simpleClassName( lafClass ) + ".properties" );
						if( !propertiesFile.isFile() )
							continue;

						try( InputStream in = new FileInputStream( propertiesFile ) ) {
							properties.load( in );
						}
					}
				}
			}

			// add additional defaults
			if( additionalDefaults != null )
				properties.putAll( additionalDefaults );

			// get (and remove) wildcard replacements, which override all other defaults that end with same suffix
			HashMap<String, String> wildcards = new HashMap<>();
			Iterator<Entry<Object, Object>> it = properties.entrySet().iterator();
			while( it.hasNext() ) {
				Entry<Object, Object> e = it.next();
				String key = (String) e.getKey();
				if( key.startsWith( WILDCARD_PREFIX ) ) {
					wildcards.put( key.substring( WILDCARD_PREFIX.length() ), (String) e.getValue() );
					it.remove();
				}
			}

			// override UI defaults with wildcard replacements
			for( Object key : defaults.keySet() ) {
				int dot;
				if( !(key instanceof String) ||
					properties.containsKey( key ) ||
					(dot = ((String)key).lastIndexOf( '.' )) < 0 )
				  continue;

				String wildcardKey = ((String)key).substring( dot + 1 );
				String wildcardValue = wildcards.get( wildcardKey );
				if( wildcardValue != null )
					properties.put( key, wildcardValue );
			}

			Function<String, String> propertiesGetter = key -> {
				return properties.getProperty( key );
			};
			Function<String, String> resolver = value -> {
				return resolveValue( value, propertiesGetter );
			};

			// parse and add properties to UI defaults
			Map<String, String> variables = new HashMap<>( 50 );
			for( Map.Entry<Object, Object> e : properties.entrySet() ) {
				String key = (String) e.getKey();
				if( key.startsWith( VARIABLE_PREFIX ) ) {
					variables.put( key, (String) e.getValue() );
					continue;
				}

				String value = (String) e.getValue();
				try {
					value = resolveValue( value, propertiesGetter );
					defaults.put( key, parseValue( key, value, null, null, resolver, addonClassLoaders ) );
				} catch( RuntimeException ex ) {
					logParseError( key, value, ex, true );
				}
			}

			// remember variables in defaults to allow using them in styles
			defaults.put( KEY_VARIABLES, variables );

			// remember properties (for testing)
			if( FlatSystemProperties.getBoolean( KEY_PROPERTIES, false ) ) {
				Properties properties2 = new Properties();
				properties2.putAll( properties );
				for( Map.Entry<String, String> e : wildcards.entrySet() )
					properties2.put( WILDCARD_PREFIX + e.getKey(), e.getValue() );
				defaults.put( KEY_PROPERTIES, properties2 );
			}

			// clear/disable system color cache
			systemColorCache = null;
		} catch( IOException ex ) {
			LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to load properties files.", ex );
		}
	}

	/**
	 * Similar to Class.getSimpleName(), but includes enclosing class for nested classes.
	 */
	static String simpleClassName( Class<?> cls ) {
		String className = cls.getName();
		return className.substring( className.lastIndexOf( '.' ) + 1 );
	}

	static void logParseError( String key, String value, RuntimeException ex, boolean severe ) {
		String message = "FlatLaf: Failed to parse: '" + key + '=' + value + '\'';
		if( severe )
			LoggingFacade.INSTANCE.logSevere( message, ex );
		else
			LoggingFacade.INSTANCE.logConfig( message, ex );
	}

	static String resolveValue( String value, Function<String, String> propertiesGetter )
		throws IllegalArgumentException
	{
		value = value.trim();
		String value0 = value;

		if( value.startsWith( PROPERTY_PREFIX ) )
			value = value.substring( PROPERTY_PREFIX.length() );
		else if( !value.startsWith( VARIABLE_PREFIX ) )
			return value;

		boolean optional = false;
		if( value.startsWith( OPTIONAL_PREFIX ) ) {
			value = value.substring( OPTIONAL_PREFIX.length() );
			optional = true;
		}

		String newValue = propertiesGetter.apply( value );
		if( newValue == null ) {
			if( optional )
				return "null";

			throw new IllegalArgumentException( "variable or property '" + value + "' not found" );
		}

		if( newValue.equals( value0 ) )
			throw new IllegalArgumentException( "endless recursion in variable or property '" + value + "'" );

		return resolveValue( newValue, propertiesGetter );
	}

	static String resolveValueFromUIManager( String value )
		throws IllegalArgumentException
	{
		if( value.startsWith( VARIABLE_PREFIX ) ) {
			@SuppressWarnings( "unchecked" )
			Map<String, String> variables = (Map<String, String>) UIManager.get( KEY_VARIABLES );
			String newValue = (variables != null) ? variables.get( value ) : null;
			if( newValue == null )
				throw new IllegalArgumentException( "variable '" + value + "' not found" );

			return resolveValueFromUIManager( newValue );
		}

		if( !value.startsWith( PROPERTY_PREFIX ) )
			return value;

		String key = value.substring( PROPERTY_PREFIX.length() );
		Object newValue = UIManager.get( key );
		if( newValue == null )
			throw new IllegalArgumentException( "property '" + key + "' not found" );

		// convert binary color to string
		if( newValue instanceof Color ) {
			Color color = (Color) newValue;
			int rgb = color.getRGB() & 0xffffff;
			int alpha = color.getAlpha();
			return (alpha != 255)
				? String.format( "#%06x%02x", rgb, alpha )
				: String.format( "#%06x", rgb );
		}

		throw new IllegalArgumentException( "property value type '" + newValue.getClass().getName() + "' not supported in references" );
	}

	enum ValueType { UNKNOWN, STRING, BOOLEAN, CHARACTER, INTEGER, INTEGERORFLOAT, FLOAT, BORDER, ICON, INSETS, DIMENSION, COLOR, FONT,
		SCALEDINTEGER, SCALEDFLOAT, SCALEDINSETS, SCALEDDIMENSION, INSTANCE, CLASS, GRAYFILTER, NULL, LAZY }

	private static final ValueType[] tempResultValueType = new ValueType[1];
	private static Map<Class<?>, ValueType> javaValueTypes;
	private static Map<String, ValueType> knownValueTypes;

	static Object parseValue( String key, String value, Class<?> valueType )
		throws IllegalArgumentException
	{
		return parseValue( key, value, valueType, null, v -> v, Collections.emptyList() );
	}

	static Object parseValue( String key, String value, Class<?> javaValueType, ValueType[] resultValueType,
		Function<String, String> resolver, List<ClassLoader> addonClassLoaders )
			throws IllegalArgumentException
	{
		if( resultValueType == null )
			resultValueType = tempResultValueType;

		// do not parse styles here
		if( key.startsWith( "[style]" ) ) {
			resultValueType[0] = ValueType.STRING;
			return value;
		}

		value = value.trim();

		// null
		if( value.equals( "null" ) || value.isEmpty() ) {
			resultValueType[0] = ValueType.NULL;
			return null;
		}

		// check for function "if"
		//     Syntax: if(condition,trueValue,falseValue)
		//       - condition: evaluates to true if:
		//           - is not "null"
		//           - is not "false"
		//           - is not an integer with zero value
		//       - trueValue: used if condition is true
		//       - falseValue: used if condition is false
		if( value.startsWith( "if(" ) && value.endsWith( ")" ) ) {
			List<String> params = splitFunctionParams( value.substring( 3, value.length() - 1 ), ',' );
			if( params.size() != 3 )
				throw newMissingParametersException( value );

			boolean ifCondition = parseCondition( params.get( 0 ), resolver, addonClassLoaders );
			String ifValue = params.get( ifCondition ? 1 : 2 );
			return parseValue( key, resolver.apply( ifValue ), javaValueType, resultValueType, resolver, addonClassLoaders );
		}

		ValueType valueType = ValueType.UNKNOWN;

		if( javaValueType != null ) {
			if( javaValueTypes == null ) {
				// create lazy
				javaValueTypes = new HashMap<>();
				javaValueTypes.put( String.class, ValueType.STRING );
				javaValueTypes.put( boolean.class, ValueType.BOOLEAN );
				javaValueTypes.put( Boolean.class, ValueType.BOOLEAN );
				javaValueTypes.put( char.class, ValueType.CHARACTER );
				javaValueTypes.put( Character.class, ValueType.CHARACTER );
				javaValueTypes.put( int.class, ValueType.INTEGER );
				javaValueTypes.put( Integer.class, ValueType.INTEGER );
				javaValueTypes.put( float.class, ValueType.FLOAT );
				javaValueTypes.put( Float.class, ValueType.FLOAT );
				javaValueTypes.put( Border.class, ValueType.BORDER );
				javaValueTypes.put( Icon.class, ValueType.ICON );
				javaValueTypes.put( Insets.class, ValueType.INSETS );
				javaValueTypes.put( Dimension.class, ValueType.DIMENSION );
				javaValueTypes.put( Color.class, ValueType.COLOR );
				javaValueTypes.put( Font.class, ValueType.FONT );
			}

			// map java value type to parser value type
			valueType = javaValueTypes.get( javaValueType );
			if( valueType == null )
				throw new IllegalArgumentException( "unsupported value type '" + javaValueType.getName() + "'" );

			// remove '"' from strings
			if( valueType == ValueType.STRING && value.startsWith( "\"" ) && value.endsWith( "\"" ) )
				value = value.substring( 1, value.length() - 1 );
		} else {
			// false, true
			switch( value ) {
				case "false":	resultValueType[0] = ValueType.BOOLEAN; return false;
				case "true":	resultValueType[0] = ValueType.BOOLEAN; return true;
			}

			// check for function "lazy"
			//     Syntax: lazy(uiKey)
			if( value.startsWith( "lazy(" ) && value.endsWith( ")" ) ) {
				resultValueType[0] = ValueType.LAZY;
				String uiKey = StringUtils.substringTrimmed( value, 5, value.length() - 1 );
				return (LazyValue) t -> {
					return lazyUIManagerGet( uiKey );
				};
			}

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

			if( valueType == ValueType.UNKNOWN ) {
				if( knownValueTypes == null ) {
					// create lazy
					knownValueTypes = new HashMap<>();
					// system colors
					knownValueTypes.put( "activeCaptionBorder", ValueType.COLOR );
					knownValueTypes.put( "inactiveCaptionBorder", ValueType.COLOR );
					knownValueTypes.put( "windowBorder", ValueType.COLOR );
					// SplitPane
					knownValueTypes.put( "SplitPane.dividerSize", ValueType.INTEGER );
					knownValueTypes.put( "SplitPaneDivider.gripDotSize", ValueType.INTEGER );
					knownValueTypes.put( "dividerSize", ValueType.INTEGER );
					knownValueTypes.put( "gripDotSize", ValueType.INTEGER );
					// TabbedPane
					knownValueTypes.put( "TabbedPane.closeCrossPlainSize", ValueType.FLOAT );
					knownValueTypes.put( "TabbedPane.closeCrossFilledSize", ValueType.FLOAT );
					knownValueTypes.put( "closeCrossPlainSize", ValueType.FLOAT );
					knownValueTypes.put( "closeCrossFilledSize", ValueType.FLOAT );
					// Table
					knownValueTypes.put( "Table.intercellSpacing", ValueType.DIMENSION );
					knownValueTypes.put( "intercellSpacing", ValueType.DIMENSION );
				}

				valueType = knownValueTypes.getOrDefault( key, ValueType.UNKNOWN );
			}

			// determine value type from key
			if( valueType == ValueType.UNKNOWN ) {
				if( key.endsWith( "UI" ) )
					valueType = ValueType.STRING;
				else if( key.endsWith( "Color" ) ||
					(key.endsWith( "ground" ) &&
					 (key.endsWith( ".background" ) || key.endsWith( "Background" ) || key.equals( "background" ) ||
					  key.endsWith( ".foreground" ) || key.endsWith( "Foreground" ) || key.equals( "foreground" ))) )
					valueType = ValueType.COLOR;
				else if( key.endsWith( ".font" ) || key.endsWith( "Font" ) || key.equals( "font" ) )
					valueType = ValueType.FONT;
				else if( key.endsWith( ".border" ) || key.endsWith( "Border" ) || key.equals( "border" ) )
					valueType = ValueType.BORDER;
				else if( key.endsWith( ".icon" ) || key.endsWith( "Icon" ) || key.equals( "icon" ) )
					valueType = ValueType.ICON;
				else if( key.endsWith( ".margin" ) || key.equals( "margin" ) ||
						 key.endsWith( ".padding" ) || key.equals( "padding" ) ||
						 key.endsWith( "Margins" ) || key.endsWith( "Insets" ) )
					valueType = ValueType.INSETS;
				else if( key.endsWith( "Size" ) )
					valueType = ValueType.DIMENSION;
				else if( key.endsWith( "Width" ) || key.endsWith( "Height" ) )
					valueType = ValueType.INTEGERORFLOAT;
				else if( key.endsWith( "Char" ) )
					valueType = ValueType.CHARACTER;
				else if( key.endsWith( "grayFilter" ) )
					valueType = ValueType.GRAYFILTER;
			}
		}

		resultValueType[0] = valueType;

		// parse value
		switch( valueType ) {
			case STRING:		return value;
			case BOOLEAN:		return parseBoolean( value );
			case CHARACTER:		return parseCharacter( value );
			case INTEGER:		return parseInteger( value );
			case INTEGERORFLOAT:return parseIntegerOrFloat( value );
			case FLOAT:			return parseFloat( value );
			case BORDER:		return parseBorder( value, resolver, addonClassLoaders );
			case ICON:			return parseInstance( value, resolver, addonClassLoaders );
			case INSETS:		return parseInsets( value );
			case DIMENSION:		return parseDimension( value );
			case COLOR:			return parseColorOrFunction( value, resolver );
			case FONT:			return parseFont( value );
			case SCALEDINTEGER:	return parseScaledInteger( value );
			case SCALEDFLOAT:	return parseScaledFloat( value );
			case SCALEDINSETS:	return parseScaledInsets( value );
			case SCALEDDIMENSION:return parseScaledDimension( value );
			case INSTANCE:		return parseInstance( value, resolver, addonClassLoaders );
			case CLASS:			return parseClass( value, addonClassLoaders );
			case GRAYFILTER:	return parseGrayFilter( value );
			case UNKNOWN:
			default:
				// string
				if( value.startsWith( "\"" ) && value.endsWith( "\"" ) ) {
					resultValueType[0] = ValueType.STRING;
					return value.substring( 1, value.length() - 1 );
				}

				// colors
				if( value.startsWith( "#" ) || value.endsWith( ")" ) ) {
					Object color = parseColorOrFunction( value, resolver );
					resultValueType[0] = (color != null) ? ValueType.COLOR : ValueType.NULL;
					return color;
				}

				// integer or float
				char firstChar = value.charAt( 0 );
				if( (firstChar >= '0' && firstChar <= '9') ||
					firstChar == '-' || firstChar == '+' || firstChar == '.' )
				{
					// integer
					try {
						Integer integer = parseInteger( value );
						resultValueType[0] = ValueType.INTEGER;
						return integer;
					} catch( NumberFormatException ex ) {
						// ignore
					}

					// float
					try {
						Float f = parseFloat( value );
						resultValueType[0] = ValueType.FLOAT;
						return f;
					} catch( NumberFormatException ex ) {
						// ignore
					}
				}

				// string
				resultValueType[0] = ValueType.STRING;
				return value;
		}
	}

	private static boolean parseCondition( String condition,
		Function<String, String> resolver, List<ClassLoader> addonClassLoaders )
	{
		try {
			Object conditionValue = parseValue( "", resolver.apply( condition ), null, null, resolver, addonClassLoaders );
			return (conditionValue != null &&
				!conditionValue.equals( false ) &&
				!conditionValue.equals( 0 ) );
		} catch( IllegalArgumentException ex ) {
			// ignore errors (e.g. variable or property not found) and evaluate to false
			return false;
		}
	}

	private static Object parseBorder( String value, Function<String, String> resolver, List<ClassLoader> addonClassLoaders )
		throws IllegalArgumentException
	{
		if( value.indexOf( ',' ) >= 0 ) {
			// Syntax: top,left,bottom,right[,lineColor[,lineThickness[,arc]]]
			List<String> parts = splitFunctionParams( value, ',' );
			try {
				Insets insets = parseInsets( value );
				ColorUIResource lineColor = (parts.size() >= 5 && !parts.get( 4 ).isEmpty())
					? (ColorUIResource) parseColorOrFunction( resolver.apply( parts.get( 4 ) ), resolver )
					: null;
				float lineThickness = (parts.size() >= 6 && !parts.get( 5 ).isEmpty())
					? parseFloat( parts.get( 5 ) )
					: 1f;
				int arc = (parts.size() >= 7) && !parts.get( 6 ).isEmpty()
					? parseInteger( parts.get( 6 ) )
					: -1;

				return (LazyValue) t -> {
					return (lineColor != null || arc > 0)
						? new FlatLineBorder( insets, lineColor, lineThickness, arc )
						: new FlatEmptyBorder( insets );
				};
			} catch( RuntimeException ex ) {
				throw new IllegalArgumentException( "invalid border '" + value + "' (" + ex.getMessage() + ")" );
			}
		} else
			return parseInstance( value, resolver, addonClassLoaders );
	}

	private static Object parseInstance( String value, Function<String, String> resolver, List<ClassLoader> addonClassLoaders ) {
		return (LazyValue) t -> {
			try {
				if( value.indexOf( ',' ) >= 0 ) {
					// Syntax: className,param1,param2,...
					List<String> parts = splitFunctionParams( value, ',' );
					String className = parts.get( 0 );
					Class<?> cls = findClass( className, addonClassLoaders );

					Constructor<?>[] constructors = cls.getDeclaredConstructors();
					Object result = invokeConstructorOrStaticMethod( constructors, parts, resolver );
					if( result != null )
						return result;

					LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to instantiate '" + className
						+ "': no constructor found for parameters '"
						+ value.substring( value.indexOf( ',' + 1 ) ) + "'.", null );
					return null;
				} else
					return findClass( value, addonClassLoaders ).getDeclaredConstructor().newInstance();
			} catch( Exception ex ) {
				LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to instantiate '" + value + "'.", ex );
				return null;
			}
		};
	}

	private static Object parseClass( String value, List<ClassLoader> addonClassLoaders ) {
		return (LazyValue) t -> {
			try {
				return findClass( value, addonClassLoaders );
			} catch( ClassNotFoundException ex ) {
				LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to find class '" + value + "'.", ex );
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

	private static Insets parseInsets( String value )
		throws IllegalArgumentException
	{
		List<String> numbers = StringUtils.split( value, ',', true, false );
		try {
			return new InsetsUIResource(
				Integer.parseInt( numbers.get( 0 ) ),
				Integer.parseInt( numbers.get( 1 ) ),
				Integer.parseInt( numbers.get( 2 ) ),
				Integer.parseInt( numbers.get( 3 ) ) );
		} catch( NumberFormatException | IndexOutOfBoundsException ex ) {
			throw new IllegalArgumentException( "invalid insets '" + value + "'" );
		}
	}

	private static Dimension parseDimension( String value )
		throws IllegalArgumentException
	{
		List<String> numbers = StringUtils.split( value, ',', true, false );
		try {
			return new DimensionUIResource(
				Integer.parseInt( numbers.get( 0 ) ),
				Integer.parseInt( numbers.get( 1 ) ) );
		} catch( NumberFormatException | IndexOutOfBoundsException ex ) {
			throw new IllegalArgumentException( "invalid size '" + value + "'" );
		}
	}

	private static Object parseColorOrFunction( String value, Function<String, String> resolver )
		throws IllegalArgumentException
	{
		if( value.endsWith( ")" ) )
			return parseColorFunctions( value, resolver );

		return parseColor( value );
	}

	/**
	 * Parses a hex color in  {@code #RGB}, {@code #RGBA}, {@code #RRGGBB} or {@code #RRGGBBAA}
	 * format and returns it as color object.
	 */
	static ColorUIResource parseColor( String value )
		throws IllegalArgumentException
	{
		int rgba = parseColorRGBA( value );
		return ((rgba & 0xff000000) == 0xff000000)
			? new ColorUIResource( rgba )
			: new ColorUIResource( new Color( rgba, true ) );
	}

	/**
	 * Parses a hex color in  {@code #RGB}, {@code #RGBA}, {@code #RRGGBB} or {@code #RRGGBBAA}
	 * format and returns it as {@code rgba} integer suitable for {@link java.awt.Color},
	 * which includes alpha component in bits 24-31.
	 */
	static int parseColorRGBA( String value )
		throws IllegalArgumentException
	{
		int len = value.length();
		if( (len != 4 && len != 5 && len != 7 && len != 9) || value.charAt( 0 ) != '#' )
			throw newInvalidColorException( value );

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
				throw newInvalidColorException( value );

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

	private static IllegalArgumentException newInvalidColorException( String value ) {
		return new IllegalArgumentException( "invalid color '" + value + "'" );
	}

	private static Object parseColorFunctions( String value, Function<String, String> resolver )
		throws IllegalArgumentException
	{
		int paramsStart = value.indexOf( '(' );
		if( paramsStart < 0 )
			throw new IllegalArgumentException( "missing opening parenthesis in function '" + value + "'" );

		String function = StringUtils.substringTrimmed( value, 0, paramsStart );
		List<String> params = splitFunctionParams( value.substring( paramsStart + 1, value.length() - 1 ), ',' );
		if( params.isEmpty() )
			throw newMissingParametersException( value );

		if( parseColorDepth > 100 )
			throw new IllegalArgumentException( "endless recursion in color function '" + value + "'" );

		parseColorDepth++;
		try {
			switch( function ) {
				case "if":			return parseColorIf( value, params, resolver );
				case "lazy":		return parseColorLazy( value, params, resolver );
				case "systemColor":	return parseColorSystemColor( value, params, resolver );
				case "rgb":			return parseColorRgbOrRgba( false, params, resolver );
				case "rgba":		return parseColorRgbOrRgba( true, params, resolver );
				case "hsl":			return parseColorHslOrHsla( false, params );
				case "hsla":		return parseColorHslOrHsla( true, params );
				case "lighten":		return parseColorHSLIncreaseDecrease( 2, true, params, resolver );
				case "darken":		return parseColorHSLIncreaseDecrease( 2, false, params, resolver );
				case "saturate":	return parseColorHSLIncreaseDecrease( 1, true, params, resolver );
				case "desaturate":	return parseColorHSLIncreaseDecrease( 1, false, params, resolver );
				case "fadein":		return parseColorHSLIncreaseDecrease( 3, true, params, resolver );
				case "fadeout":		return parseColorHSLIncreaseDecrease( 3, false, params, resolver );
				case "fade":		return parseColorFade( params, resolver );
				case "spin":		return parseColorSpin( params, resolver );
				case "changeHue":		return parseColorChange( 0, params, resolver );
				case "changeSaturation":return parseColorChange( 1, params, resolver );
				case "changeLightness":	return parseColorChange( 2, params, resolver );
				case "changeAlpha":		return parseColorChange( 3, params, resolver );
				case "mix":				return parseColorMix( null, params, resolver );
				case "tint":			return parseColorMix( "#fff", params, resolver );
				case "shade":			return parseColorMix( "#000", params, resolver );
				case "contrast":		return parseColorContrast( params, resolver );
				case "over":			return parseColorOver( params, resolver );
			}
		} finally {
			parseColorDepth--;
		}

		throw new IllegalArgumentException( "unknown color function '" + value + "'" );
	}

	/**
	 * Syntax: if(condition,trueValue,falseValue)
	 * <p>
	 * This "if" function is only used if the "if" is passed as parameter to another
	 * color function. Otherwise, the general "if" function is used.
	 */
	private static Object parseColorIf( String value, List<String> params, Function<String, String> resolver )
		throws IllegalArgumentException
	{
		if( params.size() != 3 )
			throw newMissingParametersException( value );

		boolean ifCondition = parseCondition( params.get( 0 ), resolver, Collections.emptyList() );
		String ifValue = params.get( ifCondition ? 1 : 2 );
		return parseColorOrFunction( resolver.apply( ifValue ), resolver );
	}

	/**
	 * Syntax: lazy(uiKey)
	 * <p>
	 * This "lazy" function is only used if the "lazy" is passed as parameter to another
	 * color function. Otherwise, the general "lazy" function is used.
	 * <p>
	 * Note: The color is resolved immediately, not lazy, because it is passed as parameter to another color function.
	 * So e.g. {@code darken(lazy(List.background), 10%)} is the same as {@code darken($List.background, 10%)}.
	 * <p>
	 * Only useful if a property is defined as lazy and that property is used
	 * in another property's color function. E.g.
	 *
	 * <pre>{@code
	 * someProperty = lazy(List.background)
	 * anotherProperty = darken($someProperty, 10%)
	 * }</pre>
	 */
	private static Object parseColorLazy( String value, List<String> params, Function<String, String> resolver )
		throws IllegalArgumentException
	{
		if( params.size() != 1 )
			throw newMissingParametersException( value );

		return parseColorOrFunction( resolver.apply( PROPERTY_PREFIX + params.get( 0 ) ), resolver );
	}

	/**
	 * Syntax: systemColor(name[,defaultValue])
	 *   - name: system color name
	 *   - defaultValue: default color value used if system color is not available
	 */
	private static Object parseColorSystemColor( String value, List<String> params, Function<String, String> resolver )
		throws IllegalArgumentException
	{
		if( params.size() < 1 )
			throw newMissingParametersException( value );

		ColorUIResource systemColor = getSystemColor( params.get( 0 ) );
		if( systemColor != null )
			return systemColor;

		String defaultValue = (params.size() > 1) ? params.get( 1 ) : "";
		if( defaultValue.equals( "null" ) || defaultValue.isEmpty() )
			return null;

		return parseColorOrFunction( resolver.apply( defaultValue ), resolver );
	}

	private static ColorUIResource getSystemColor( String name ) {
		Function<String, Color> systemColorGetter = FlatLaf.getSystemColorGetter();
		if( systemColorGetter == null )
			return null;

		// use containsKey() because value may be null
		if( systemColorCache != null && systemColorCache.containsKey( name ) )
			return systemColorCache.get( name );

		Color color = systemColorGetter.apply( name );
		ColorUIResource uiColor = (color != null) ? new ColorUIResource( color ) : null;

		if( systemColorCache != null )
			systemColorCache.put( name, uiColor );

		return uiColor;
	}

	/**
	 * Syntax: rgb(red,green,blue) or rgba(red,green,blue,alpha)
	 *   - red:   an integer 0-255 or a percentage 0-100%
	 *   - green: an integer 0-255 or a percentage 0-100%
	 *   - blue:  an integer 0-255 or a percentage 0-100%
	 *   - alpha: an integer 0-255 or a percentage 0-100%
	 */
	private static ColorUIResource parseColorRgbOrRgba( boolean hasAlpha, List<String> params,
		Function<String, String> resolver )
			throws IllegalArgumentException
	{
		if( hasAlpha && params.size() == 2 ) {
			// syntax rgba(color,alpha), which allows adding alpha to any color
			// NOTE: this syntax is deprecated
			//       use fade(color,alpha) instead
			String colorStr = params.get( 0 );
			int alpha = parseInteger( params.get( 1 ), 0, 255, true );

			ColorUIResource color = (ColorUIResource) parseColorOrFunction( resolver.apply( colorStr ), resolver );
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
	private static ColorUIResource parseColorHslOrHsla( boolean hasAlpha, List<String> params )
		throws IllegalArgumentException
	{
		int hue = parseInteger( params.get( 0 ), 0, 360, false );
		int saturation = parsePercentage( params.get( 1 ) );
		int lightness = parsePercentage( params.get( 2 ) );
		int alpha = hasAlpha ? parsePercentage( params.get( 3 ) ) : 100;

		float[] hsl = { hue, saturation, lightness };
		return new ColorUIResource( HSLColor.toRGB( hsl, alpha / 100f ) );
	}

	/**
	 * Syntax: lighten(color,amount[,options]) or darken(color,amount[,options]) or
	 *         saturate(color,amount[,options]) or desaturate(color,amount[,options]) or
	 *         fadein(color,amount[,options]) or fadeout(color,amount[,options])
	 *   - color: a color (e.g. #f00) or a color function
	 *   - amount: percentage 0-100%
	 *   - options: [relative] [autoInverse] [noAutoInverse] [derived] [lazy]
	 */
	private static Object parseColorHSLIncreaseDecrease( int hslIndex, boolean increase,
		List<String> params, Function<String, String> resolver )
			throws IllegalArgumentException
	{
		String colorStr = params.get( 0 );
		int amount = parsePercentage( params.get( 1 ) );
		boolean relative = false;
		boolean autoInverse = false;
		boolean derived = false;
		boolean lazy = false;

		if( params.size() > 2 ) {
			String options = params.get( 2 );
			relative = options.contains( "relative" );
			autoInverse = options.contains( "autoInverse" );
			derived = options.contains( "derived" );
			lazy = options.contains( "lazy" );

			// use autoInverse by default for derived colors, except if noAutoInverse is set
			if( derived && !options.contains( "noAutoInverse" ) )
				autoInverse = true;
		}

		// create function
		ColorFunction function = new ColorFunctions.HSLIncreaseDecrease(
			hslIndex, increase, amount, relative, autoInverse );

		if( lazy )
			return newLazyColorFunction( colorStr, function );

		// parse base color, apply function and create derived color
		return parseFunctionBaseColor( colorStr, function, derived, resolver );
	}

	/**
	 * Syntax: fade(color,amount[,options])
	 *   - color: a color (e.g. #f00) or a color function
	 *   - amount: percentage 0-100%
	 *   - options: [derived] [lazy]
	 */
	private static Object parseColorFade( List<String> params, Function<String, String> resolver )
		throws IllegalArgumentException
	{
		String colorStr = params.get( 0 );
		int amount = parsePercentage( params.get( 1 ) );
		boolean derived = false;
		boolean lazy = false;

		if( params.size() > 2 ) {
			String options = params.get( 2 );
			derived = options.contains( "derived" );
			lazy = options.contains( "lazy" );
		}

		// create function
		ColorFunction function = new ColorFunctions.Fade( amount );

		if( lazy )
			return newLazyColorFunction( colorStr, function );

		// parse base color, apply function and create derived color
		return parseFunctionBaseColor( colorStr, function, derived, resolver );
	}

	/**
	 * Syntax: spin(color,angle[,options])
	 *   - color: a color (e.g. #f00) or a color function
	 *   - angle: number of degrees to rotate
	 *   - options: [derived] [lazy]
	 */
	private static Object parseColorSpin( List<String> params, Function<String, String> resolver )
		throws IllegalArgumentException
	{
		String colorStr = params.get( 0 );
		int amount = parseInteger( params.get( 1 ) );
		boolean derived = false;
		boolean lazy = false;

		if( params.size() > 2 ) {
			String options = params.get( 2 );
			derived = options.contains( "derived" );
			lazy = options.contains( "lazy" );
		}

		// create function
		ColorFunction function = new ColorFunctions.HSLIncreaseDecrease( 0, true, amount, false, false );

		if( lazy )
			return newLazyColorFunction( colorStr, function );

		// parse base color, apply function and create derived color
		return parseFunctionBaseColor( colorStr, function, derived, resolver );
	}

	/**
	 * Syntax: changeHue(color,value[,options]) or
	 *         changeSaturation(color,value[,options]) or
	 *         changeLightness(color,value[,options]) or
	 *         changeAlpha(color,value[,options])
	 *   - color: a color (e.g. #f00) or a color function
	 *   - value: for hue: number of degrees; otherwise: percentage 0-100%
	 *   - options: [derived] [lazy]
	 */
	private static Object parseColorChange( int hslIndex,
		List<String> params, Function<String, String> resolver )
			throws IllegalArgumentException
	{
		String colorStr = params.get( 0 );
		int value = (hslIndex == 0)
			? parseInteger( params.get( 1 ) )
			: parsePercentage( params.get( 1 ) );
		boolean derived = false;
		boolean lazy = false;

		if( params.size() > 2 ) {
			String options = params.get( 2 );
			derived = options.contains( "derived" );
			lazy = options.contains( "lazy" );
		}

		// create function
		ColorFunction function = new ColorFunctions.HSLChange( hslIndex, value );

		if( lazy )
			return newLazyColorFunction( colorStr, function );

		// parse base color, apply function and create derived color
		return parseFunctionBaseColor( colorStr, function, derived, resolver );
	}

	/**
	 * Syntax: mix(color1,color2[,weight][,options]) or
	 *         tint(color[,weight][,options]) or
	 *         shade(color[,weight][,options])
	 *   - color1: a color (e.g. #f00) or a color function
	 *   - color2: a color (e.g. #f00) or a color function
	 *   - weight: the weight (in range 0-100%) to mix the two colors
	 *             larger weight uses more of first color, smaller weight more of second color
	 *   - options: [derived] [lazy]
	 */
	private static Object parseColorMix( String color1Str, List<String> params, Function<String, String> resolver )
		throws IllegalArgumentException
	{
		int i = 0;
		if( color1Str == null )
			color1Str = params.get( i++ );
		String color2Str = params.get( i++ );
		int weight = 50;
		boolean derived = false;
		boolean lazy = false;

		if( params.size() > i ) {
			String weightStr = params.get( i );
			if( !weightStr.isEmpty() && Character.isDigit( weightStr.charAt( 0 ) ) ) {
				weight = parsePercentage( weightStr );
				i++;
			}
		}
		if( params.size() > i ) {
			String options = params.get( i );
			derived = options.contains( "derived" );
			lazy = options.contains( "lazy" );
		}

		// parse second color
		ColorUIResource color1 = (ColorUIResource) parseColorOrFunction( resolver.apply( color1Str ), resolver );
		if( color1 == null )
			return null;

		// create function
		ColorFunction function = new ColorFunctions.Mix2( color1, weight );

		if( lazy )
			return newLazyColorFunction( color2Str, function );

		// parse first color, apply function and create mixed color
		return parseFunctionBaseColor( color2Str, function, derived, resolver );
	}

	/**
	 * Syntax: contrast(color,dark,light[,threshold])
	 *   - color: a color to compare against
	 *   - dark: a designated dark color (e.g. #000) or a color function
	 *   - light: a designated light color (e.g. #fff) or a color function
	 *   - threshold: the threshold (in range 0-100%) to specify where the transition
	 *                from "dark" to "light" is (default is 43%)
	 */
	private static Object parseColorContrast( List<String> params, Function<String, String> resolver )
		throws IllegalArgumentException
	{
		String colorStr = params.get( 0 );
		String darkStr = params.get( 1 );
		String lightStr = params.get( 2 );
		int threshold = (params.size() > 3) ? parsePercentage( params.get( 3 ) ) : 43;

		// parse color to compare against
		ColorUIResource color = (ColorUIResource) parseColorOrFunction( resolver.apply( colorStr ), resolver );
		if( color == null )
			return null;

		// check luma and determine whether to use dark or light color
		String darkOrLightColor = (ColorFunctions.luma( color ) * 100 < threshold)
			? lightStr
			: darkStr;

		// parse dark or light color
		return parseColorOrFunction( resolver.apply( darkOrLightColor ), resolver );
	}

	/**
	 * Syntax: over(foreground,background)
	 *   - foreground: a foreground color (e.g. #f00) or a color function;
	 *                 the alpha of this color is used as weight to mix the two colors
	 *   - background: a background color (e.g. #f00) or a color function
	 */
	private static ColorUIResource parseColorOver( List<String> params, Function<String, String> resolver )
		throws IllegalArgumentException
	{
		String foregroundStr = params.get( 0 );
		String backgroundStr = params.get( 1 );

		// parse foreground color
		ColorUIResource foreground = (ColorUIResource) parseColorOrFunction( resolver.apply( foregroundStr ), resolver );
		if( foreground == null || foreground.getAlpha() == 255 )
			return foreground;

		// foreground color without alpha
		ColorUIResource foreground2 = new ColorUIResource( foreground.getRGB() );

		// parse background color
		ColorUIResource background = (ColorUIResource) parseColorOrFunction( resolver.apply( backgroundStr ), resolver );
		if( background == null )
			return foreground2;

		// create new color
		float weight = foreground.getAlpha() / 255f;
		return new ColorUIResource( ColorFunctions.mix( foreground2, background, weight ) );
	}

	private static Object parseFunctionBaseColor( String colorStr, ColorFunction function,
		boolean derived, Function<String, String> resolver )
			throws IllegalArgumentException
	{
		// parse base color
		String resolvedColorStr = resolver.apply( colorStr );
		ColorUIResource baseColor = (ColorUIResource) parseColorOrFunction( resolvedColorStr, resolver );
		if( baseColor == null )
			return null;

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

	private static LazyValue newLazyColorFunction( String uiKey, ColorFunction function ) {
		return (LazyValue) t -> {
			Object color = lazyUIManagerGet( uiKey );
			return (color instanceof Color)
				? new ColorUIResource( ColorFunctions.applyFunctions( (Color) color, function ) )
				: null;
		};
	}

	/**
	 * Syntax: [normal] [bold|+bold|-bold] [italic|+italic|-italic] [<size>|+<incr>|-<decr>|<percent>%] [family[, family]] [$baseFontKey]
	 */
	private static Object parseFont( String value )
		throws IllegalArgumentException
	{
		Object font = fontCache.get( value );
		if( font != null )
			return font;

		int style = -1;
		int styleChange = 0;
		int absoluteSize = 0;
		int relativeSize = 0;
		float scaleSize = 0;
		List<String> families = null;
		String baseFontKey = null;

		// use StreamTokenizer to split string because it supports quoted strings
		StreamTokenizer st = new StreamTokenizer( new StringReader( value ) );
		st.resetSyntax();
		st.wordChars( ' ' + 1, 255 );
		st.whitespaceChars( 0, ' ' );
		st.whitespaceChars( ',', ',' ); // ignore ','
		st.quoteChar( '"' );
		st.quoteChar( '\'' );

		try {
			while( st.nextToken() != StreamTokenizer.TT_EOF ) {
				String param = st.sval;
				switch( param ) {
					// font style
					case "normal":
						style = 0;
						break;

					case "bold":
						if( style == -1 )
							style = 0;
						style |= Font.BOLD;
						break;

					case "italic":
						if( style == -1 )
							style = 0;
						style |= Font.ITALIC;
						break;

					case "+bold":   styleChange |= Font.BOLD; break;
					case "-bold":   styleChange |= Font.BOLD << 16; break;
					case "+italic": styleChange |= Font.ITALIC; break;
					case "-italic": styleChange |= Font.ITALIC << 16; break;

					default:
						char firstChar = param.charAt( 0 );
						if( Character.isDigit( firstChar ) || firstChar == '+' || firstChar == '-' ) {
							// font size
							if( absoluteSize != 0 || relativeSize != 0 || scaleSize != 0 )
								throw new IllegalArgumentException( "size specified more than once in '" + value + "'" );

							if( firstChar == '+' || firstChar == '-' )
								relativeSize = parseInteger( param );
							else if( param.endsWith( "%" ) )
								scaleSize = parseInteger( param.substring( 0, param.length() - 1 ) ) / 100f;
							else
								absoluteSize = parseInteger( param );
						} else if( firstChar == '$' ) {
							// reference to base font
							if( baseFontKey != null )
								throw new IllegalArgumentException( "baseFontKey specified more than once in '" + value + "'" );

							baseFontKey = param.substring( 1 );
						} else {
							// font family
							if( families == null )
								families = Collections.singletonList( param );
							else {
								if( families.size() == 1 )
									families = new ArrayList<>( families );
								families.add( param );
							}
						}
						break;
				}
			}
		} catch( RuntimeException | IOException ex ) {
			throw new IllegalArgumentException(  "invalid font '" + value + "' (" + ex.getMessage() + ")" );
		}

		if( style != -1 && styleChange != 0 )
			throw new IllegalArgumentException( "invalid font '" + value + "': can not mix absolute style (e.g. 'bold') with derived style (e.g. '+italic')" );
		if( styleChange != 0 ) {
			if( (styleChange & Font.BOLD) != 0 && (styleChange & (Font.BOLD << 16)) != 0 )
				throw new IllegalArgumentException( "invalid font '" + value + "': can not use '+bold' and '-bold'" );
			if( (styleChange & Font.ITALIC) != 0 && (styleChange & (Font.ITALIC << 16)) != 0 )
				throw new IllegalArgumentException( "invalid font '" + value + "': can not use '+italic' and '-italic'" );
		}

		font = new FlatLaf.ActiveFont( baseFontKey, families, style, styleChange, absoluteSize, relativeSize, scaleSize );
		fontCache.put( value, font );
		return font;
	}

	private static int parsePercentage( String value )
		throws IllegalArgumentException, NumberFormatException
	{
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

	private static Boolean parseBoolean( String value )
		throws IllegalArgumentException
	{
		switch( value ) {
			case "false":	return false;
			case "true":	return true;
		}
		throw new IllegalArgumentException( "invalid boolean '" + value + "'" );
	}

	private static Character parseCharacter( String value )
		throws IllegalArgumentException
	{
		if( value.length() != 1 )
			throw new IllegalArgumentException( "invalid character '" + value + "'" );
		return value.charAt( 0 );
	}

	private static Integer parseInteger( String value, int min, int max, boolean allowPercentage )
		throws IllegalArgumentException, NumberFormatException
	{
		if( allowPercentage && value.endsWith( "%" ) ) {
			int percent = parsePercentage( value );
			return (max * percent) / 100;
		}

		Integer integer = parseInteger( value );
		if( integer < min || integer > max )
			throw new NumberFormatException( "integer '" + value + "' out of range (" + min + '-' + max + ')' );
		return integer;
	}

	private static Integer parseInteger( String value )
		throws NumberFormatException
	{
		try {
			return Integer.parseInt( value );
		} catch( NumberFormatException ex ) {
			throw new NumberFormatException( "invalid integer '" + value + "'" );
		}
	}

	private static Number parseIntegerOrFloat( String value )
		throws NumberFormatException
	{
		try {
			return Integer.parseInt( value );
		} catch( NumberFormatException ex ) {
			try {
				return Float.parseFloat( value );
			} catch( NumberFormatException ex2 ) {
				throw new NumberFormatException( "invalid integer or float '" + value + "'" );
			}
		}
	}

	private static Float parseFloat( String value )
		throws NumberFormatException
	{
		try {
			return Float.parseFloat( value );
		} catch( NumberFormatException ex ) {
			throw new NumberFormatException( "invalid float '" + value + "'" );
		}
	}

	private static ActiveValue parseScaledInteger( String value )
		throws NumberFormatException
	{
		int val = parseInteger( value );
		return t -> {
			return UIScale.scale( val );
		};
	}

	private static ActiveValue parseScaledFloat( String value )
		throws NumberFormatException
	{
		float val = parseFloat( value );
		return t -> {
			return UIScale.scale( val );
		};
	}

	private static ActiveValue parseScaledInsets( String value )
		throws IllegalArgumentException
	{
		Insets insets = parseInsets( value );
		return t -> {
			return UIScale.scale( insets );
		};
	}

	private static ActiveValue parseScaledDimension( String value )
		throws IllegalArgumentException
	{
		Dimension dimension = parseDimension( value );
		return t -> {
			return UIScale.scale( dimension );
		};
	}

	private static Object parseGrayFilter( String value )
		throws IllegalArgumentException
	{
		List<String> numbers = StringUtils.split( value, ',', true, false );
		try {
			int brightness = Integer.parseInt( numbers.get( 0 ) );
			int contrast = Integer.parseInt( numbers.get( 1 ) );
			int alpha = Integer.parseInt( numbers.get( 2 ) );

			return (LazyValue) t -> {
				return new GrayFilter( brightness, contrast, alpha );
			};
		} catch( NumberFormatException | IndexOutOfBoundsException ex ) {
			throw new IllegalArgumentException( "invalid gray filter '" + value + "'" );
		}
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
				strs.add( StringUtils.substringTrimmed( str, start, i ) );
				start = i + 1;
			}
		}

		// last param
		String s = StringUtils.substringTrimmed( str, start );
		if( !s.isEmpty() || !strs.isEmpty() )
			strs.add( s );

		return strs;
	}

	private static Object invokeConstructorOrStaticMethod( Executable[] constructorsOrMethods,
			List<String> parts, Function<String, String> resolver )
		throws Exception
	{
		// order constructors/methods by parameter types:
		// - String parameters to the end
		// - int before float parameters
		constructorsOrMethods = constructorsOrMethods.clone();
		Arrays.sort( constructorsOrMethods, (c1, c2) -> {
			Class<?>[] ptypes1 = c1.getParameterTypes();
			Class<?>[] ptypes2 = c2.getParameterTypes();
			if( ptypes1.length != ptypes2.length )
				return ptypes1.length - ptypes2.length;

			for( int i = 0; i < ptypes1.length; i++ ) {
				Class<?> pt1 = ptypes1[i];
				Class<?> pt2 = ptypes2[i];

				if( pt1 == pt2 )
					continue;

				// order methods with String parameters to the end
				if( pt1 == String.class )
					return 2;
				if( pt2 == String.class )
					return -2;

				// order int before float
				if( pt1 == int.class )
					return -1;
				if( pt2 == int.class )
					return 1;
			}
			return 0;
		} );

		// search for best constructor/method for given parameter values
		for( Executable cm : constructorsOrMethods ) {
			if( cm.getParameterCount() != parts.size() - 1 )
				continue;

			Object[] params = parseMethodParams( cm.getParameterTypes(), parts, resolver );
			if( params == null )
				continue;

			// invoke constructor or static method
			if( cm instanceof Constructor )
				return ((Constructor<?>)cm).newInstance( params );
			else
				return ((Method)cm).invoke( null, params );
		}

		return null;
	}

	private static Object[] parseMethodParams( Class<?>[] paramTypes, List<String> parts, Function<String, String> resolver ) {
		Object[] params = new Object[paramTypes.length];
		try {
			for( int i = 0; i < params.length; i++ ) {
				Class<?> paramType = paramTypes[i];
				String paramValue = parts.get( i + 1 );
				if( paramType == String.class )
					params[i] = paramValue;
				else if( paramType == boolean.class )
					params[i] = parseBoolean( paramValue );
				else if( paramType == int.class )
					params[i] = parseInteger( paramValue );
				else if( paramType == float.class )
					params[i] = parseFloat( paramValue );
				else if( paramType == Color.class )
					params[i] = parseColorOrFunction( resolver.apply( paramValue ), resolver );
				else
					return null; // unsupported parameter type
			}
		} catch( IllegalArgumentException ex ) {
			return null; // failed to parse parameter for expected parameter type
		}
		return params;
	}

	/**
	 * For use in LazyValue to get value for given key from UIManager and report error
	 * if not found. If key is prefixed by '?', then no error is reported.
	 */
	static Object lazyUIManagerGet( String uiKey ) {
		boolean optional = false;
		if( uiKey.startsWith( OPTIONAL_PREFIX ) ) {
			uiKey = uiKey.substring( OPTIONAL_PREFIX.length() );
			optional = true;
		}

		Object value = UIManager.get( uiKey );
		if( value == null && !optional )
			LoggingFacade.INSTANCE.logSevere( "FlatLaf: '" + uiKey + "' not found in UI defaults.", null );
		return value;
	}

	private static IllegalArgumentException newMissingParametersException( String value ) {
		return new IllegalArgumentException( "missing parameters in function '" + value + "'" );
	}
}
