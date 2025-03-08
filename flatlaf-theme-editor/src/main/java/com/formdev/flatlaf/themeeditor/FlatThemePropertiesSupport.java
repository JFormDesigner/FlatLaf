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

package com.formdev.flatlaf.themeeditor;

import java.awt.Color;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import javax.swing.UIDefaults;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.text.BadLocationException;
import com.formdev.flatlaf.UIDefaultsLoaderAccessor;

/**
 * Supports parsing content of text area in FlatLaf properties syntax.
 *
 * @author Karl Tauber
 */
class FlatThemePropertiesSupport
	implements DocumentListener
{
	private final FlatSyntaxTextArea textArea;
	private final Function<String, String> propertiesGetter;
	private final Function<String, String> resolver;
	private BasePropertyProvider basePropertyProvider;

	// caches
	private Properties propertiesCache;
	private final Map<Integer, Object> parsedValueCache = new HashMap<>();
	private final Map<String, Object> parsedValueCache2 = new HashMap<>();
	private Set<String> allKeysCache;
	private String baseTheme;
	private boolean lastDark;

	private static long globalCacheInvalidationCounter;
	private long cacheInvalidationCounter;

	private static Set<String> wildcardKeys;

	FlatThemePropertiesSupport( FlatSyntaxTextArea textArea ) {
		this.textArea = textArea;

		propertiesGetter = key -> {
			return getPropertyOrWildcard( key );
		};
		resolver = v -> {
			return resolveValue( v );
		};

		textArea.getDocument().addDocumentListener( this );
	}

	void setBasePropertyProvider( BasePropertyProvider basePropertyProvider ) {
		this.basePropertyProvider = basePropertyProvider;
	}

	private String resolveValue( String value )
		throws IllegalArgumentException
	{
		return UIDefaultsLoaderAccessor.resolveValue( value, propertiesGetter );
	}

	Object getParsedValueAtLine( int line ) {
		autoClearCache();

		Integer lineKey = line;
		Object parsedValue = parsedValueCache.get( lineKey );
		if( parsedValue != null )
			return !(parsedValue instanceof Exception) ? parsedValue : null;

		KeyValue keyValue = getKeyValueAtLine( line );
		if( keyValue == null )
			return null;

		try {
			Object[] resultValueType = new Object[1];
			String value = resolveValue( keyValue.value );
			parsedValue = UIDefaultsLoaderAccessor.parseValue( keyValue.key, value, resultValueType, resolver );
			parsedValueCache.put( lineKey, parsedValue );
			return parsedValue;
		} catch( Exception ex ) {
			System.out.println( textArea.getFileName() + ": " + ex.getMessage() ); //TODO
			parsedValueCache.put( lineKey, ex );
			return null;
		}
	}

	private KeyValue getKeyValueAtLine( int line ) {
		try {
			// get text at line
			int startOffset = textArea.getLineStartOffset( line );
			int endOffset = textArea.getLineEndOffset( line );
			String text = textArea.getText( startOffset, endOffset - startOffset );

			// remove trailing backslash from multi-line properties usually used for styles
			if( text.endsWith( "\\\n" ) )
				text = text.substring( 0, text.length() - 2 ).trim();
			// remove trailing semicolon from styles
			if( text.endsWith( ";" ) )
				text = text.substring( 0, text.length() - 1 ).trim();

			// remove key starting with "[style]" so that first property name
			// in CSS styled value is used as key to detect value type
			if( text.startsWith( "[style]" ) ) {
				int sepIndex = text.indexOf( '=' );
				if( sepIndex >= 0 )
					text = text.substring( sepIndex + 1 );
			}

			// parse line
			Properties properties = new Properties();
			properties.load( new StringReader( text ) );
			if( properties.isEmpty() )
				return null;

			// get key and value for line
			String key = (String) properties.keys().nextElement();
			String value = properties.getProperty( key );
			return new KeyValue( key, value );
		} catch( BadLocationException | IOException ex ) {
			// ignore
			return null;
		}
	}

	Object getParsedProperty( String key ) {
		Object parsedValue = parsedValueCache2.get( key );
		if( parsedValue != null )
			return !(parsedValue instanceof Exception) ? parsedValue : null;

		String str = getPropertyOrWildcard( key );
		if( str == null )
			return null;

		try {
			Object[] resultValueType = new Object[1];
			String value = resolveValue( str );
			parsedValue = UIDefaultsLoaderAccessor.parseValue( key, value, resultValueType, resolver );
			parsedValueCache2.put( key, parsedValue );
			return parsedValue;
		} catch( Exception ex ) {
			System.out.println( textArea.getFileName() + ": " + ex.getMessage() ); //TODO
			parsedValueCache2.put( key, ex );
			return null;
		}
	}

	private String getPropertyOrWildcard( String key ) {
		String value = getProperty( key );
		if( value != null )
			return value;

		if( !isKeyAllowedForWildcard( key ) )
			return null;

		int lastDotIndex = key.lastIndexOf( '.' );
		if( lastDotIndex < 0 )
			return null;

		String wildcardKey = "*.".concat( key.substring( lastDotIndex + 1 ) );
		return getProperty( wildcardKey );
	}

	String getProperty( String key ) {
		// look in current text area
		String value = getProperties().getProperty( key );
		if( value != null )
			return value;

		if( basePropertyProvider == null )
			return null;

		// look in base properties files
		return basePropertyProvider.getProperty( key, getBaseTheme() );
	}

	Properties getProperties() {
		if( propertiesCache != null )
			return propertiesCache;

		String text = textArea.getText();
		try {
			propertiesCache = UIDefaultsLoaderAccessor.newUIProperties( lastDark );
			propertiesCache.load( new StringReader( text ) );

			// re-load if dark has changed (getBaseTheme() invokes getProperties()!!!)
			boolean dark = isDark( getBaseTheme() );
			if( lastDark != dark ) {
				lastDark = dark;
				propertiesCache = UIDefaultsLoaderAccessor.newUIProperties( lastDark );
				propertiesCache.load( new StringReader( text ) );
			}
		} catch( IOException ex ) {
			ex.printStackTrace(); //TODO
		}
		return propertiesCache;
	}

	Set<String> getAllKeys() {
		autoClearCache();

		if( allKeysCache != null )
			return allKeysCache;

		allKeysCache = new HashSet<>();

		for( Object key : getProperties().keySet() )
			allKeysCache.add( (String) key );

		// look in base properties files
		if( basePropertyProvider != null )
			basePropertyProvider.addAllKeys( allKeysCache, getBaseTheme() );

		return allKeysCache;
	}

	static boolean isDark( String baseTheme ) {
		return "dark".equals( baseTheme ) || "darcula".equals( baseTheme ) || "macdark".equals( baseTheme );
	}

	private String getBaseTheme() {
		if( baseTheme == null )
			baseTheme = getProperties().getProperty( "@baseTheme", "light" );
		return baseTheme;
	}

	private void clearCache() {
		propertiesCache = null;
		parsedValueCache.clear();
		parsedValueCache2.clear();
		allKeysCache = null;
		baseTheme = null;

		// increase global cache invalidation counter to allow auto-clear caches
		globalCacheInvalidationCounter++;
		cacheInvalidationCounter = globalCacheInvalidationCounter;
	}

	/**
	 * Clear caches that may depend on other editors if cache of another editor was invalidated.
	 */
	private void autoClearCache() {
		if( cacheInvalidationCounter == globalCacheInvalidationCounter )
			return;

		parsedValueCache.clear();
		parsedValueCache2.clear();
		allKeysCache = null;
	}

	static Set<String> getKeysForWildcard( String key ) {
		if( !key.startsWith( "*." ) )
			return Collections.emptySet();

		loadKeysAllowedForWildcard();

		String suffix = key.substring( 1 );
		Set<String> result = new HashSet<>();
		for( String k : wildcardKeys ) {
			if( k.endsWith( suffix ) )
				result.add( k );
		}
		return result;
	}

	private static boolean isKeyAllowedForWildcard( String key ) {
		loadKeysAllowedForWildcard();
		return wildcardKeys.contains( key );
	}

	private static void loadKeysAllowedForWildcard() {
		if( wildcardKeys != null )
			return;
		wildcardKeys = new HashSet<>();

		UIDefaults basicDefaults = new BasicLookAndFeel() {
			@Override public String getName() { return "Basic"; }
			@Override public String getID() { return "Basic"; }
			@Override public String getDescription() { return "Basic"; }
			@Override public boolean isNativeLookAndFeel() { return false; }
			@Override public boolean isSupportedLookAndFeel() { return true; }
		}.getDefaults();

		for( Object key : basicDefaults.keySet() ) {
			if( key instanceof String )
				wildcardKeys.add( (String) key );
		}

		// same as added in FlatLaf.getDefaults()
		wildcardKeys.addAll( Arrays.asList(
			"Button.disabledBackground",
			"EditorPane.disabledBackground",
			"EditorPane.inactiveBackground",
			"FormattedTextField.disabledBackground",
			"PasswordField.disabledBackground",
			"Spinner.disabledBackground",
			"TextArea.disabledBackground",
			"TextArea.inactiveBackground",
			"TextField.disabledBackground",
			"TextPane.disabledBackground",
			"TextPane.inactiveBackground",
			"ToggleButton.disabledBackground",

			"Button.disabledText",
			"CheckBox.disabledText",
			"CheckBoxMenuItem.disabledForeground",
			"Menu.disabledForeground",
			"MenuItem.disabledForeground",
			"RadioButton.disabledText",
			"RadioButtonMenuItem.disabledForeground",
			"Spinner.disabledForeground",
			"ToggleButton.disabledText",

			"DesktopIcon.foreground"
		) );
	}

	//---- interface DocumentListener ----

	@Override
	public void insertUpdate( DocumentEvent e ) {
		clearCache();
	}

	@Override
	public void removeUpdate( DocumentEvent e ) {
		clearCache();
	}

	@Override
	public void changedUpdate( DocumentEvent e ) {
	}

	//---- class KeyValue -----------------------------------------------------

	static class CacheLineInfo {
		Object parsedValue;
		Object valueType;
		Exception parseError;

		Color origColor;
	}

	//---- class KeyValue -----------------------------------------------------

	static class KeyValue {
		final String key;
		final String value;

		KeyValue( String key, String value ) {
			this.key = key;
			this.value = value;
		}
	}

	//---- interface BasePropertyProvider -------------------------------------

	interface BasePropertyProvider {
		String getProperty( String key, String baseTheme );
		void addAllKeys( Set<String> allKeys, String baseTheme );
	}
}
