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
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import javax.swing.JComponent;
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
	 * @param style the style in CSS syntax
	 * @param applyProperty function that is invoked to apply the properties;
	 *                      first parameter is the key, second the binary value;
	 *                      the function must return the old value
	 * @return map of old values modified by the given style, or {@code null}
	 * @throws IllegalArgumentException on syntax errors
	 */
	public static Map<String, Object> parse( Map<String, Object> oldStyleValues,
		String style, BiFunction<String, Object, Object> applyProperty ) throws IllegalArgumentException
	{
		// restore previous values
		if( oldStyleValues != null ) {
			for( Entry<String, Object> e : oldStyleValues.entrySet() )
				applyProperty.apply( e.getKey(), e.getValue() );
		}

		// ignore empty style
		if( style == null || style.trim().isEmpty() )
			return null;

		Map<String, Object> oldValues = null;

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
			Object val = FlatLaf.parseDefaultsValue( key, value );
			Object oldValue = applyProperty.apply( key, val );

			// remember previous value
			if( oldValues == null )
				oldValues = new HashMap<>();
			oldValues.put( key, oldValue );
		}

		return oldValues;
	}

	public static boolean hasStyle( JComponent c ) {
		return getStyle( c ) != null;
	}

	public static String getStyle( JComponent c ) {
		return toString( c.getClientProperty( FlatClientProperties.COMPONENT_STYLE ) );
	}

	static String toString( Object style ) {
		return (style instanceof String) ? (String) style : null;
	}
}
