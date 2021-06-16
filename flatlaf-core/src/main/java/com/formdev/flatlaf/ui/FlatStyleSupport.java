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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.swing.JComponent;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.StringUtils;

/**
 * Support for styling components in CSS syntax.
 *
 * @author Karl Tauber
 * @since TODO
 */
public class FlatStyleSupport
{
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
	 * @throws IllegalArgumentException on syntax errors
	 * @throws ClassCastException if value type does not fit to expected type 
	 */
	public static Map<String, Object> parseAndApply( Map<String, Object> oldStyleValues,
		Object style, BiFunction<String, Object, Object> applyProperty ) throws IllegalArgumentException
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
		if( value.startsWith( "$" ) )
			return UIManager.get( value.substring( 1 ) );

		return FlatLaf.parseDefaultsValue( key, value );
	}

	public static Object getStyle( JComponent c ) {
		return c.getClientProperty( FlatClientProperties.COMPONENT_STYLE );
	}
}
