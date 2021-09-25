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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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

	/** @since 2 */
	public interface StyleableUI {
		Map<String, Class<?>> getStyleableInfos( JComponent c );
	}

	/** @since 2 */
	public interface StyleableBorder {
		Object applyStyleProperty( String key, Object value );
		Map<String, Class<?>> getStyleableInfos();
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
			if( str.trim().isEmpty() )
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
		if( style == null || style.trim().isEmpty() )
			return null;

		Map<String, Object> map = null;

		// split style into parts and process them
		for( String part : StringUtils.split( style, ';' ) ) {
			// ignore empty parts
			part = part.trim();
			if( part.isEmpty() )
				continue;

			// find separator colon
			int sepIndex = part.indexOf( ':' );
			if( sepIndex < 0 )
				throw new IllegalArgumentException( "missing colon in '" + part + "'" );

			// split into key and value
			String key = part.substring( 0, sepIndex ).trim();
			String value = part.substring( sepIndex + 1 ).trim();
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
	 * @throws UnknownStyleException if object does not have a annotated field with given name
	 * @throws IllegalArgumentException if value type does not fit to expected type
	 */
	public static Object applyToAnnotatedObject( Object obj, String key, Object value )
		throws UnknownStyleException, IllegalArgumentException
	{
		String fieldName = key;
		int dotIndex = key.indexOf( '.' );
		if( dotIndex >= 0 ) {
			// remove first dot in key and change subsequent character to uppercase
			fieldName = key.substring( 0, dotIndex )
				+ Character.toUpperCase( key.charAt( dotIndex + 1 ) )
				+ key.substring( dotIndex + 2 );
		}

		return applyToField( obj, fieldName, key, value, field -> {
			Styleable styleable = field.getAnnotation( Styleable.class );
			return styleable != null && styleable.dot() == (dotIndex >= 0);
		} );
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
				if( predicate == null || predicate.test( f ) ) {
					if( !isValidField( f ) )
						throw new IllegalArgumentException( "field '" + cls.getName() + "." + fieldName + "' is final or static" );

					try {
						// necessary to access protected fields in other packages
						f.setAccessible( true );

						// get old value and set new value
						Object oldValue = f.get( obj );
						f.set( obj, value );
						return oldValue;
					} catch( IllegalAccessException ex ) {
						throw new IllegalArgumentException( "failed to access field '" + cls.getName() + "." + fieldName + "'" );
					}
				}
			} catch( NoSuchFieldException ex ) {
				// field not found in class --> try superclass
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

	private static boolean isValidField( Field f ) {
		int modifiers = f.getModifiers();
		return (modifiers & (Modifier.FINAL|Modifier.STATIC)) == 0 && !f.isSynthetic();
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
			setter.invoke( obj, value );
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
	 * @throws UnknownStyleException if object does not have a annotated field with given name
	 * @throws IllegalArgumentException if value type does not fit to expected type
	 */
	public static Object applyToAnnotatedObjectOrComponent( Object obj, Object comp, String key, Object value ) {
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

	public static Object getStyle( JComponent c ) {
		return c.getClientProperty( FlatClientProperties.STYLE );
	}

	static PropertyChangeListener createPropertyChangeListener( JComponent c,
		Consumer<Object> applyStyle, PropertyChangeListener superListener )
	{
		return e -> {
			if( superListener != null )
				superListener.propertyChange( e );

			if( FlatClientProperties.STYLE.equals( e.getPropertyName() ) ) {
				applyStyle.accept( e.getNewValue() );
				c.revalidate();
				c.repaint();
			}
		};
	}

	static Border cloneBorder( Border border ) {
		Class<? extends Border> borderClass = border.getClass();
		try {
			return borderClass.getDeclaredConstructor().newInstance();
		} catch( Exception ex ) {
			throw new IllegalArgumentException( "failed to clone border '" + borderClass.getName() + "'" );
		}
	}

	static Icon cloneIcon( Icon icon ) {
		Class<? extends Icon> iconClass = icon.getClass();
		try {
			return iconClass.getDeclaredConstructor().newInstance();
		} catch( Exception ex ) {
			throw new IllegalArgumentException( "failed to clone icon '" + iconClass.getName() + "'" );
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
		Map<String, Class<?>> infos = new LinkedHashMap<>();
		collectAnnotatedStyleableInfos( obj, infos );
		collectStyleableInfos( border, infos );
		return infos;
	}

	/**
	 * Search for all fields annotated with {@link Styleable} and add them to the given map.
	 * The key is the name of the field and the value the type of the field.
	 */
	public static void collectAnnotatedStyleableInfos( Object obj, Map<String, Class<?>> infos ) {
		Class<?> cls = obj.getClass();

		for(;;) {
			for( Field f : cls.getDeclaredFields() ) {
				if( !isValidField( f ) )
					continue;

				Styleable styleable = f.getAnnotation( Styleable.class );
				if( styleable == null )
					continue;

				String name = f.getName();
				Class<?> type = f.getType();

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
}
