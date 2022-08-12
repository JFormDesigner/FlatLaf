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

package com.formdev.flatlaf.ui;

import java.beans.PropertyChangeListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Support for styling components in CSS syntax.
 *
 * @author Karl Tauber
 * @since 2
 */
public class FlatStylingSupport
{
	/**
	 * Indicates that a field is intended to be used by FlatLaf styling support.
	 * <p>
	 * <strong>Do not rename fields annotated with this annotation.</strong>
	 *
	 * @since 2
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Styleable {
		boolean dot() default false;
		Class<?> type() default Void.class;
	}

	/**
	 * Indicates that a field in the specified (super) class
	 * is intended to be used by FlatLaf styling support.
	 * <p>
	 * Use this annotation, instead of {@link Styleable}, to style fields
	 * in superclasses, where it is not possible to use {@link Styleable}.
	 * <p>
	 * Classes using this annotation may implement {@link StyleableLookupProvider}
	 * to give access to protected fields (in JRE) in modular applications.
	 *
	 * @since 2.5
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(StyleableFields.class)
	public @interface StyleableField {
		Class<?> cls();
		String key();
		String fieldName() default "";
	}

	/**
	 * Container annotation for {@link StyleableField}.
	 *
	 * @since 2.5
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface StyleableFields {
		StyleableField[] value();
	}


	/** @since 2 */
	public interface StyleableUI {
		Map<String, Class<?>> getStyleableInfos( JComponent c );
		/** @since 2.5 */ Object getStyleableValue( JComponent c, String key );
	}

	/** @since 2 */
	public interface StyleableBorder {
		Object applyStyleProperty( String key, Object value );
		Map<String, Class<?>> getStyleableInfos();
		/** @since 2.5 */ Object getStyleableValue( String key );
	}

	/** @since 2.5 */
	public interface StyleableLookupProvider {
		MethodHandles.Lookup getLookupForStyling();
	}


	/**
	 * Returns the style specified in client property {@link FlatClientProperties#STYLE}.
	 */
	public static Object getStyle( JComponent c ) {
		return c.getClientProperty( FlatClientProperties.STYLE );
	}

	/**
	 * Returns the style class(es) specified in client property {@link FlatClientProperties#STYLE_CLASS}.
	 */
	public static Object getStyleClass( JComponent c ) {
		return c.getClientProperty( FlatClientProperties.STYLE_CLASS );
	}

	static boolean hasStyleProperty( JComponent c ) {
		return getStyle( c ) != null || getStyleClass( c ) != null;
	}

	public static Object getResolvedStyle( JComponent c, String type ) {
		Object style = getStyle( c );
		Object styleClass = getStyleClass( c );
		Object styleForClasses = getStyleForClasses( styleClass, type );
		return joinStyles( styleForClasses, style );
	}

	/**
	 * Returns the styles for the given style class(es) and the given type.
	 * <p>
	 * The style rules must be defined in UI defaults either as strings (in CSS syntax)
	 * or as {@link java.util.Map}&lt;String, Object&gt; (with binary values).
	 * The key must be in syntax: {@code [style]type.styleClass}, where the type is optional.
	 * E.g. in FlatLaf properties file:
	 * <pre>{@code
	 * [style]Button.primary = borderColor: #08f; background: #08f; foreground: #fff
	 * [style].secondary = borderColor: #0f8; background: #0f8
	 * }</pre>
	 * or in Java code:
	 * <pre>{@code
	 * UIManager.put( "[style]Button.primary", "borderColor: #08f; background: #08f; foreground: #fff" );
	 * UIManager.put( "[style].secondary", "borderColor: #0f8; background: #0f8" );
	 * }</pre>
	 * The rule "Button.primary" can be applied to buttons only.
	 * The rule ".secondary" can be applied to any component.
	 * <p>
	 * To have similar behavior as in CSS, this method first gets the rule without type,
	 * then the rule with type and concatenates both rules.
	 * E.g. invoking this method with parameters styleClass="foo" and type="Button" does following:
	 * <pre>{@code
	 * return joinStyles(
	 *     UIManager.get( "[style].foo" ),
	 *     UIManager.get( "[style]Button.foo" ) );
	 * }</pre>
	 *
	 * @param styleClass the style class(es) either as string (single class or multiple classes separated by space characters)
	 *                   or as {@code String[]} or {@link java.util.List}&lt;String&gt; (multiple classes)
	 * @param type the type of the component
	 * @return the styles
	 */
	public static Object getStyleForClasses( Object styleClass, String type ) {
		if( styleClass == null )
			return null;

		if( styleClass instanceof String && ((String)styleClass).indexOf( ' ' ) >= 0 )
			styleClass = StringUtils.split( (String) styleClass, ' ', true, true );

		if( styleClass instanceof String )
			return getStyleForClass( ((String)styleClass).trim(), type );
		else if( styleClass instanceof String[] ) {
			Object style = null;
			for( String cls : (String[]) styleClass )
				style = joinStyles( style, getStyleForClass( cls, type ) );
			return style;
		} else if( styleClass instanceof List<?> ) {
			Object style = null;
			for( Object cls : (List<?>) styleClass )
				style = joinStyles( style, getStyleForClass( (String) cls, type ) );
			return style;
		} else
			return null;
	}

	private static Object getStyleForClass( String styleClass, String type ) {
		return joinStyles(
			UIManager.get( "[style]." + styleClass ),
			UIManager.get( "[style]" + type + '.' + styleClass ) );
	}

	/**
	 * Joins two styles. They can be either strings (in CSS syntax)
	 * or {@link java.util.Map}&lt;String, Object&gt; (with binary values).
	 * <p>
	 * If both styles are strings, then a joined string is returned.
	 * If both styles are maps, then a joined map is returned.
	 * If one style is a map and the other style a string, then the string
	 * is parsed (using {@link #parse(String)}) to a map and a joined map is returned.
	 *
	 * @param style1 first style as string or map, or {@code null}
	 * @param style2 second style as string or map, or {@code null}
	 * @return new joined style
	 */
	@SuppressWarnings( "unchecked" )
	public static Object joinStyles( Object style1, Object style2 ) {
		if( style1 == null )
			return style2;
		if( style2 == null )
			return style1;

		// join two strings
		if( style1 instanceof String && style2 instanceof String )
			return style1 + "; " + style2;

		// convert first style to map
		Map<String, Object> map1 = (style1 instanceof String)
			? parse( (String) style1 )
			: (Map<String, Object>) style1;
		if( map1 == null )
			return style2;

		// convert second style to map
		Map<String, Object> map2 = (style2 instanceof String)
			? parse( (String) style2 )
			: (Map<String, Object>) style2;
		if( map2 == null )
			return style1;

		// join two maps
		Map<String, Object> map = new HashMap<>( map1 );
		map.putAll( map2 );
		return map;
	}

	/**
	 * Concatenates two styles in CSS syntax.
	 *
	 * @param style1 first style, or {@code null}
	 * @param style2 second style, or {@code null}
	 * @return concatenation of the two styles separated by a semicolon
	 */
	public static String concatStyles( String style1, String style2 ) {
		if( style1 == null )
			return style2;
		if( style2 == null )
			return style1;
		return style1 + "; " + style2;
	}

	/**
	 * Parses styles in CSS syntax ("key1: value1; key2: value2; ..."),
	 * converts the value strings into binary and invokes the given function
	 * to apply the properties.
	 *
	 * @param oldStyleValues map of old values modified by the previous invocation, or {@code null}
	 * @param style the style in CSS syntax as string, or a Map, or {@code null}
	 * @param applyProperty function that is invoked to apply the properties;
	 *                      first parameter is the key, second the binary value;
	 *                      the function must return the old value
	 * @return map of old values modified by the given style, or {@code null}
	 * @throws UnknownStyleException on unknown style keys
	 * @throws IllegalArgumentException on syntax errors
	 * @throws ClassCastException if value type does not fit to expected type
	 */
	public static Map<String, Object> parseAndApply( Map<String, Object> oldStyleValues,
		Object style, BiFunction<String, Object, Object> applyProperty )
			throws UnknownStyleException, IllegalArgumentException
	{
		// restore previous values
		if( oldStyleValues != null ) {
			for( Map.Entry<String, Object> e : oldStyleValues.entrySet() )
				applyProperty.apply( e.getKey(), e.getValue() );
		}

		// ignore empty style
		if( style == null )
			return null;

		if( style instanceof String ) {
			// handle style in CSS syntax
			String str = (String) style;
			if( StringUtils.isTrimmedEmpty( str ) )
				return null;

			return applyStyle( parse( str ), applyProperty );
		} else if( style instanceof Map ) {
			// handle style of type Map
			@SuppressWarnings( "unchecked" )
			Map<String, Object> map = (Map<String, Object>) style;
			return applyStyle( map, applyProperty );
		} else
			return null;
	}

	private static Map<String, Object> applyStyle( Map<String, Object> style,
		BiFunction<String, Object, Object> applyProperty )
	{
		if( style.isEmpty() )
			return null;

		Map<String, Object> oldValues = new HashMap<>();
		for( Map.Entry<String, Object> e : style.entrySet() ) {
			String key = e.getKey();
			Object newValue = e.getValue();

			// handle key prefix
			if( key.startsWith( "[" ) ) {
				if( (SystemInfo.isWindows && key.startsWith( "[win]" )) ||
					(SystemInfo.isMacOS && key.startsWith( "[mac]" )) ||
					(SystemInfo.isLinux && key.startsWith( "[linux]" )) ||
					(key.startsWith( "[light]" ) && !FlatLaf.isLafDark()) ||
					(key.startsWith( "[dark]" ) && FlatLaf.isLafDark()) )
				{
					// prefix is known and enabled --> remove prefix
					key = key.substring( key.indexOf( ']' ) + 1 );
				} else
					continue;
			}

			Object oldValue = applyProperty.apply( key, newValue );
			oldValues.put( key, oldValue );
		}
		return oldValues;
	}

	/**
	 * Parses styles in CSS syntax ("key1: value1; key2: value2; ..."),
	 * converts the value strings into binary and returns all key/value pairs as map.
	 *
	 * @param style the style in CSS syntax, or {@code null}
	 * @return map of parsed styles, or {@code null}
	 * @throws IllegalArgumentException on syntax errors
	 */
	public static Map<String, Object> parse( String style )
		throws IllegalArgumentException
	{
		if( style == null || StringUtils.isTrimmedEmpty( style ) )
			return null;

		Map<String, Object> map = null;

		// split style into parts and process them
		for( String part : StringUtils.split( style, ';', true, true ) ) {
			// find separator colon
			int sepIndex = part.indexOf( ':' );
			if( sepIndex < 0 )
				throw new IllegalArgumentException( "missing colon in '" + part + "'" );

			// split into key and value
			String key = StringUtils.substringTrimmed( part, 0, sepIndex );
			String value = StringUtils.substringTrimmed( part, sepIndex + 1 );
			if( key.isEmpty() )
				throw new IllegalArgumentException( "missing key in '" + part + "'" );
			if( value.isEmpty() )
				throw new IllegalArgumentException( "missing value in '" + part + "'" );

			// parse value string and convert it into binary value
			if( map == null )
				map = new LinkedHashMap<>();
			map.put( key, parseValue( key, value ) );
		}

		return map;
	}

	private static Object parseValue( String key, String value ) {
		// simple reference
		if( value.startsWith( "$" ) )
			return UIManager.get( value.substring( 1 ) );

		// remove key prefix for correct value type detection
		// (e.g. "[light]padding" would not parse to Insets)
		if( key.startsWith( "[" ) )
			key = key.substring( key.indexOf( ']' ) + 1 );

		// parse string
		return FlatLaf.parseDefaultsValue( key, value, null );
	}

	/**
	 * Applies the given value to an annotated field of the given object.
	 * The field must be annotated with {@link Styleable}.
	 *
	 * @param obj the object
	 * @param key the name of the field
	 * @param value the new value
	 * @return the old value of the field
	 * @throws UnknownStyleException if object does not have an annotated field with given name
	 * @throws IllegalArgumentException if value type does not fit to expected type
	 */
	public static Object applyToAnnotatedObject( Object obj, String key, Object value )
		throws UnknownStyleException, IllegalArgumentException
	{
		String fieldName = keyToFieldName( key );

		return applyToField( obj, fieldName, key, value, field -> {
			Styleable styleable = field.getAnnotation( Styleable.class );
			return styleable != null && styleable.dot() == (fieldName != key);
		} );
	}

	private static String keyToFieldName( String key ) {
		int dotIndex = key.indexOf( '.' );
		if( dotIndex < 0 )
			return key;

		// remove first dot in key and change subsequent character to uppercase
		return key.substring( 0, dotIndex )
			+ Character.toUpperCase( key.charAt( dotIndex + 1 ) )
			+ key.substring( dotIndex + 2 );
	}

	/**
	 * Applies the given value to a field of the given object.
	 *
	 * @param obj the object
	 * @param fieldName the name of the field
	 * @param key the key (only used for error reporting)
	 * @param value the new value
	 * @return the old value of the field
	 * @throws UnknownStyleException if object does not have a field with given name
	 * @throws IllegalArgumentException if value type does not fit to expected type
	 */
	static Object applyToField( Object obj, String fieldName, String key, Object value )
		throws UnknownStyleException, IllegalArgumentException
	{
		return applyToField( obj, fieldName, key, value, null );
	}

	private static Object applyToField( Object obj, String fieldName, String key, Object value, Predicate<Field> predicate )
		throws UnknownStyleException, IllegalArgumentException
	{
		Class<?> cls = obj.getClass();

		for(;;) {
			try {
				Field f = cls.getDeclaredField( fieldName );
				if( predicate == null || predicate.test( f ) )
					return applyToField( f, obj, value, false );
			} catch( NoSuchFieldException ex ) {
				// field not found in class --> try superclass
			}

			for( StyleableField styleableField : cls.getAnnotationsByType( StyleableField.class ) ) {
				if( key.equals( styleableField.key() ) )
					return applyToField( getStyleableField( styleableField ), obj, value, true );
			}

			cls = cls.getSuperclass();
			if( cls == null )
				throw new UnknownStyleException( key );

			if( predicate != null ) {
				String superclassName = cls.getName();
				if( superclassName.startsWith( "java." ) || superclassName.startsWith( "javax." ) )
					throw new UnknownStyleException( key );
			}
		}
	}

	private static Object applyToField( Field f, Object obj, Object value, boolean useMethodHandles ) {
		checkValidField( f );

		if( useMethodHandles && obj instanceof StyleableLookupProvider ) {
			try {
				// use method handles to access protected fields in JRE in modular applications
				MethodHandles.Lookup lookup = ((StyleableLookupProvider)obj).getLookupForStyling();

				// get old value and set new value
				Object oldValue = lookup.unreflectGetter( f ).invoke( obj );
				lookup.unreflectSetter( f ).invoke( obj, convertToEnum( value, f.getType() ) );
				return oldValue;
			} catch( Throwable ex ) {
				throw newFieldAccessFailed( f, ex );
			}
		}

		try {
			// necessary to access protected fields in other packages
			f.setAccessible( true );

			// get old value and set new value
			Object oldValue = f.get( obj );
			f.set( obj, convertToEnum( value, f.getType() ) );
			return oldValue;
		} catch( IllegalAccessException ex ) {
			throw newFieldAccessFailed( f, ex );
		}
	}

	private static Object getFieldValue( Field f, Object obj, boolean useMethodHandles ) {
		checkValidField( f );

		if( useMethodHandles && obj instanceof StyleableLookupProvider ) {
			// use method handles to access protected fields in JRE in modular applications
			try {
				MethodHandles.Lookup lookup = ((StyleableLookupProvider)obj).getLookupForStyling();
				return lookup.unreflectGetter( f ).invoke( obj );
			} catch( Throwable ex ) {
				throw newFieldAccessFailed( f, ex );
			}
		}

		try {
			f.setAccessible( true );
			return f.get( obj );
		} catch( IllegalAccessException ex ) {
			throw newFieldAccessFailed( f, ex );
		}
	}

	private static IllegalArgumentException newFieldAccessFailed( Field f, Throwable ex ) {
		return new IllegalArgumentException( "failed to access field '" + f.getDeclaringClass().getName() + "." + f.getName() + "'", ex );
	}

	private static void checkValidField( Field f ) {
		if( !isValidField( f ) )
			throw new IllegalArgumentException( "field '" + f.getDeclaringClass().getName() + "." + f.getName() + "' is final or static" );
	}

	private static boolean isValidField( Field f ) {
		int modifiers = f.getModifiers();
		return (modifiers & (Modifier.FINAL|Modifier.STATIC)) == 0 && !f.isSynthetic();
	}

	private static Field getStyleableField( StyleableField styleableField ) {
		String fieldName = styleableField.fieldName();
		if( fieldName.isEmpty() )
			fieldName = styleableField.key();

		try {
			return styleableField.cls().getDeclaredField( fieldName );
		} catch( NoSuchFieldException ex ) {
			throw new IllegalArgumentException( "field '" + styleableField.cls().getName() + "." + fieldName + "' not found", ex );
		}
	}

	/**
	 * Applies the given value to a property of the given object.
	 * Works only for properties that have public getter and setter methods.
	 * First the property getter is invoked to get the old value,
	 * then the property setter is invoked to set the new value.
	 *
	 * @param obj the object
	 * @param name the name of the property
	 * @param value the new value
	 * @return the old value of the property
	 * @throws UnknownStyleException if object does not have a property with given name
	 * @throws IllegalArgumentException if value type does not fit to expected type
	 */
	private static Object applyToProperty( Object obj, String name, Object value )
		throws UnknownStyleException, IllegalArgumentException
	{
		Class<?> cls = obj.getClass();
		String getterName = buildMethodName( "get", name );
		String setterName = buildMethodName( "set", name );

		try {
			Method getter;
			try {
				getter = cls.getMethod( getterName );
			} catch( NoSuchMethodException ex ) {
				getter = cls.getMethod( buildMethodName( "is", name ) );
			}
			Method setter = cls.getMethod( setterName, getter.getReturnType() );
			Object oldValue = getter.invoke( obj );
			setter.invoke( obj, convertToEnum( value, getter.getReturnType() ) );
			return oldValue;
		} catch( NoSuchMethodException ex ) {
			throw new UnknownStyleException( name );
		} catch( Exception ex ) {
			throw new IllegalArgumentException( "failed to invoke property methods '" + cls.getName() + "."
				+ getterName + "()' or '" + setterName + "(...)'", ex );
		}
	}

	private static String buildMethodName( String prefix, String name ) {
		int prefixLength = prefix.length();
		int nameLength = name.length();
		char[] chars = new char[prefixLength + nameLength];
		prefix.getChars( 0, prefixLength, chars, 0 );
		name.getChars( 0, nameLength, chars, prefixLength );
		chars[prefixLength] = Character.toUpperCase( chars[prefixLength] );
		return new String( chars );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private static Object convertToEnum( Object value, Class<?> type )
		throws IllegalArgumentException
	{
		// if type is an enum, convert string to enum value
		if( Enum.class.isAssignableFrom( type ) && value instanceof String ) {
			try {
				value = Enum.valueOf( (Class<? extends Enum>) type, (String) value );
			} catch( IllegalArgumentException ex ) {
				throw new IllegalArgumentException( "unknown enum value '" + value + "' in enum '" + type.getName() + "'", ex );
			}
		}
		return value;
	}

	/**
	 * Applies the given value to an annotated field of the given object
	 * or to a property of the given component.
	 * The field must be annotated with {@link Styleable}.
	 * The component property must have public getter and setter methods.
	 *
	 * @param obj the object
	 * @param comp the component, or {@code null}
	 * @param key the name of the field
	 * @param value the new value
	 * @return the old value of the field
	 * @throws UnknownStyleException if object does not have an annotated field with given name
	 * @throws IllegalArgumentException if value type does not fit to expected type
	 */
	public static Object applyToAnnotatedObjectOrComponent( Object obj, Object comp, String key, Object value )
		throws UnknownStyleException, IllegalArgumentException
	{
		try {
			return applyToAnnotatedObject( obj, key, value );
		} catch( UnknownStyleException ex ) {
			try {
				if( comp != null )
					return applyToProperty( comp, key, value );
			} catch( UnknownStyleException ex2 ) {
				// ignore
			}
			throw ex;
		}
	}

	static Object applyToAnnotatedObjectOrBorder( Object obj, String key, Object value,
		JComponent c, AtomicBoolean borderShared )
	{
		try {
			return applyToAnnotatedObject( obj, key, value );
		} catch( UnknownStyleException ex ) {
			// apply to border
			Border border = c.getBorder();
			if( border instanceof StyleableBorder ) {
				if( borderShared.get() ) {
					border = cloneBorder( border );
					c.setBorder( border );
					borderShared.set( false );
				}

				try {
					return ((StyleableBorder)border).applyStyleProperty( key, value );
				} catch( UnknownStyleException ex2 ) {
					// ignore
				}
			}

			// apply to component property
			try {
				return applyToProperty( c, key, value );
			} catch( UnknownStyleException ex2 ) {
				// ignore
			}
			throw ex;
		}
	}

	static PropertyChangeListener createPropertyChangeListener( JComponent c,
		Runnable installStyle, PropertyChangeListener superListener )
	{
		return e -> {
			if( superListener != null )
				superListener.propertyChange( e );

			switch( e.getPropertyName() ) {
				case FlatClientProperties.STYLE:
				case FlatClientProperties.STYLE_CLASS:
					installStyle.run();
					c.revalidate();
					c.repaint();
					break;
			}
		};
	}

	static Border cloneBorder( Border border ) {
		Class<? extends Border> borderClass = border.getClass();
		try {
			return borderClass.getDeclaredConstructor().newInstance();
		} catch( Exception ex ) {
			throw new IllegalArgumentException( "failed to clone border '" + borderClass.getName() + "'", ex );
		}
	}

	static Icon cloneIcon( Icon icon ) {
		Class<? extends Icon> iconClass = icon.getClass();
		try {
			return iconClass.getDeclaredConstructor().newInstance();
		} catch( Exception ex ) {
			throw new IllegalArgumentException( "failed to clone icon '" + iconClass.getName() + "'", ex );
		}
	}

	/**
	 * Returns a map of all fields annotated with {@link Styleable}.
	 * The key is the name of the field and the value the type of the field.
	 */
	public static Map<String, Class<?>> getAnnotatedStyleableInfos( Object obj ) {
		return getAnnotatedStyleableInfos( obj, null );
	}

	public static Map<String, Class<?>> getAnnotatedStyleableInfos( Object obj, Border border ) {
		Map<String, Class<?>> infos = new StyleableInfosMap<>();
		collectAnnotatedStyleableInfos( obj, infos );
		collectStyleableInfos( border, infos );
		return infos;
	}

	/**
	 * Search for all fields annotated with {@link Styleable} and add them to the given map.
	 * The key is the name of the field and the value the type of the field.
	 */
	public static void collectAnnotatedStyleableInfos( Object obj, Map<String, Class<?>> infos ) {
		HashSet<String> processedFields = new HashSet<>();
		Class<?> cls = obj.getClass();

		for(;;) {
			// find fields annotated with 'Styleable'
			for( Field f : cls.getDeclaredFields() ) {
				if( !isValidField( f ) )
					continue;

				Styleable styleable = f.getAnnotation( Styleable.class );
				if( styleable == null )
					continue;

				String name = f.getName();
				Class<?> type = f.getType();

				// for the case that the same field name is used in a class and in
				// one of its superclasses (e.g. field 'borderColor' in FlatButtonBorder
				// and in FlatBorder), do not process field in superclass
				if( processedFields.contains( name ) )
					continue;
				processedFields.add( name );

				// handle "dot" keys (e.g. change field name "iconArrowType" to style key "icon.arrowType")
				if( styleable.dot() ) {
					int len = name.length();
					for( int i = 0; i < len; i++ ) {
						if( Character.isUpperCase( name.charAt( i ) ) ) {
							name = name.substring( 0, i ) + '.'
								+ Character.toLowerCase( name.charAt( i ) )
								+ name.substring( i + 1 );
							break;
						}
					}
				}

				// field has a different type
				if( styleable.type() != Void.class )
					type = styleable.type();

				infos.put( name, type );
			}

			// get fields specified in 'StyleableField' annotation
			for( StyleableField styleableField : cls.getAnnotationsByType( StyleableField.class ) ) {
				String name = styleableField.key();

				// for the case that the same field name is used in a class and in
				// one of its superclasses, do not process field in superclass
				if( processedFields.contains( name ) )
					continue;
				processedFields.add( name );

				Field f = getStyleableField( styleableField );
				infos.put( name, f.getType() );
			}

			cls = cls.getSuperclass();
			if( cls == null )
				return;

			String superclassName = cls.getName();
			if( superclassName.startsWith( "java." ) || superclassName.startsWith( "javax." ) )
				return;
		}
	}

	public static void collectStyleableInfos( Border border, Map<String, Class<?>> infos ) {
		if( border instanceof StyleableBorder )
			infos.putAll( ((StyleableBorder)border).getStyleableInfos() );
	}

	public static void putAllPrefixKey( Map<String, Class<?>> infos, String keyPrefix, Map<String, Class<?>> infos2 ) {
		for( Map.Entry<String, Class<?>> e : infos2.entrySet() )
			infos.put( keyPrefix.concat( e.getKey() ), e.getValue() );
	}

	public static Object getAnnotatedStyleableValue( Object obj, String key ) {
		String fieldName = keyToFieldName( key );
		Class<?> cls = obj.getClass();

		for(;;) {
			try {
				// find field annotated with 'Styleable'
				Field f = cls.getDeclaredField( fieldName );
				Styleable styleable = f.getAnnotation( Styleable.class );
				if( styleable != null ) {
					if( styleable.dot() != (fieldName != key) )
						throw new IllegalArgumentException( "'Styleable.dot' on field '" + fieldName + "' does not match key '" + key + "'" );
					if( styleable.type() != Void.class )
						throw new IllegalArgumentException( "'Styleable.type' on field '" + fieldName + "' not supported" );

					return getFieldValue( f, obj, false );
				}
			} catch( NoSuchFieldException ex ) {
				// field not found in class --> try superclass
			}

			// find field specified in 'StyleableField' annotation
			for( StyleableField styleableField : cls.getAnnotationsByType( StyleableField.class ) ) {
				if( key.equals( styleableField.key() ) )
					return getFieldValue( getStyleableField( styleableField ), obj, true );
			}

			cls = cls.getSuperclass();
			if( cls == null )
				return null;

			String superclassName = cls.getName();
			if( superclassName.startsWith( "java." ) || superclassName.startsWith( "javax." ) )
				return null;
		}
	}

	public static Object getAnnotatedStyleableValue( Object obj, Border border, String key ) {
		if( border instanceof StyleableBorder ) {
			Object value = ((StyleableBorder)border).getStyleableValue( key );
			if( value != null )
				return value;
		}
		return getAnnotatedStyleableValue( obj, key );
	}

	//---- class UnknownStyleException ----------------------------------------

	public static class UnknownStyleException
		extends IllegalArgumentException
	{
		public UnknownStyleException( String key ) {
			super( key );
		}

		@Override
		public String getMessage() {
			return "unknown style '" + super.getMessage() + "'";
		}
	}

	//---- class StyleableInfosMap --------------------------------------------

	static class StyleableInfosMap<K,V>
		extends LinkedHashMap<K,V>
	{
		@Override
		public V put( K key, V value ) {
			V oldValue = super.put( key, value );
			if( oldValue != null )
				throw new IllegalArgumentException( "duplicate key '" + key + "'" );
			return oldValue;
		}

		@Override
		public void putAll( Map<? extends K, ? extends V> m ) {
			for( Map.Entry<? extends K, ? extends V> e : m.entrySet() )
				put( e.getKey(), e.getValue() );
		}
	}
}
