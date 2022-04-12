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

package com.formdev.flatlaf.extras.components;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.UIManager;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Base interface for all FlatLaf component extensions.
 * Extensions use client properties to store property values in components.
 *
 * @author Karl Tauber
 */
public interface FlatComponentExtension
{
	/**
	 * Overrides {@link JComponent#getClientProperty(Object)}.
	 */
	Object getClientProperty( Object key );

	/**
	 * Overrides {@link JComponent#putClientProperty(Object, Object)}.
	 */
	void putClientProperty( Object key, Object value );


	default boolean getClientPropertyBoolean( Object key, String defaultValueKey ) {
		Object value = getClientProperty( key );
		return (value instanceof Boolean) ? (boolean) value : UIManager.getBoolean( defaultValueKey );
	}

	default boolean getClientPropertyBoolean( Object key, boolean defaultValue ) {
		Object value = getClientProperty( key );
		return (value instanceof Boolean) ? (boolean) value : defaultValue;
	}

	default void putClientPropertyBoolean( Object key, boolean value, boolean defaultValue ) {
		putClientProperty( key, (value != defaultValue) ? value : null );
	}


	default int getClientPropertyInt( Object key, String defaultValueKey ) {
		Object value = getClientProperty( key );
		return (value instanceof Integer) ? (int) value : UIManager.getInt( defaultValueKey );
	}

	default int getClientPropertyInt( Object key, int defaultValue ) {
		Object value = getClientProperty( key );
		return (value instanceof Integer) ? (int) value : defaultValue;
	}


	default Color getClientPropertyColor( Object key, String defaultValueKey ) {
		Object value = getClientProperty( key );
		return (value instanceof Color) ? (Color) value : UIManager.getColor( defaultValueKey );
	}

	default Insets getClientPropertyInsets( Object key, String defaultValueKey ) {
		Object value = getClientProperty( key );
		return (value instanceof Insets) ? (Insets) value : UIManager.getInsets( defaultValueKey );
	}


	default <T extends Enum<T>> T getClientPropertyEnumString( Object key, Class<T> enumType,
		String defaultValueKey, T defaultValue )
	{
		Object value = getClientProperty( key );
		if( !(value instanceof String) && defaultValueKey != null )
			value = UIManager.getString( defaultValueKey );
		if( value instanceof String ) {
			try {
				return Enum.valueOf( enumType, (String) value );
			} catch( IllegalArgumentException ex ) {
				LoggingFacade.INSTANCE.logSevere( "FlatLaf: Unknown enum value '" + value + "' in enum '" + enumType.getName() + "'.", ex );
			}
		}
		return defaultValue;
	}

	default <T extends Enum<T>> void putClientPropertyEnumString( Object key, Enum<T> value ) {
		putClientProperty( key, (value != null) ? value.toString() : null );
	}
}
