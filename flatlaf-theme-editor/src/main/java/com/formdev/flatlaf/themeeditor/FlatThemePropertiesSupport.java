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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
	private Properties propertiesCache;
	private final Map<Integer, Object> parsedValueCache = new HashMap<>();

	private File[] baseFiles;
	private long[] baseFilesLastModified;
	private Properties[] basePropertiesCache;

	FlatThemePropertiesSupport( FlatSyntaxTextArea textArea ) {
		this.textArea = textArea;

		propertiesGetter = key -> {
			return getProperty( key );
		};
		resolver = v -> {
			return resolveValue( v );
		};

		textArea.getDocument().addDocumentListener( this );
	}

	void setBaseFiles( List<File> baseFiles ) {
		int size = baseFiles.size();
		this.baseFiles = baseFiles.toArray( new File[size] );

		baseFilesLastModified = new long[size];
		basePropertiesCache = new Properties[size];
	}

	private String resolveValue( String value ) {
		return UIDefaultsLoaderAccessor.resolveValue( value, propertiesGetter );
	}

	Object getParsedValueAtLine( int line ) {
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
			System.out.println( ex.getMessage() ); //TODO
			parsedValueCache.put( lineKey, ex );
			return null;
		}
	}

	private KeyValue getKeyValueAtLine( int line ) {
		try {
			int startOffset = textArea.getLineStartOffset( line );
			int endOffset = textArea.getLineEndOffset( line );
			String text = textArea.getText( startOffset, endOffset - startOffset );

			Properties properties = new Properties();
			properties.load( new StringReader( text ) );
			if( properties.isEmpty() )
				return null;

			String key = (String) properties.keys().nextElement();
			String value = properties.getProperty( key );
			return new KeyValue( key, value );
		} catch( BadLocationException | IOException ex ) {
			// ignore
			return null;
		}
	}

	private String getProperty( String key ) {
		// look in current text area
		String value = getProperties().getProperty( key );
		if( value != null )
			return value;

		if( baseFiles == null )
			return null;

		// look in base properties files
		for( int i = 0; i < baseFiles.length; i++ ) {
			long lastModified = baseFiles[i].lastModified();
			if( baseFilesLastModified[i] != lastModified ) {
				// (re)load base properties file
				baseFilesLastModified[i] = lastModified;
				basePropertiesCache[i] = new Properties();
				try {
					basePropertiesCache[i].load( new FileInputStream( baseFiles[i] ) );
				} catch( IOException ex ) {
					ex.printStackTrace(); //TODO
				}
			}

			value = basePropertiesCache[i].getProperty( key );
			if( value != null )
				return value;
		}

		return null;
	}

	private Properties getProperties() {
		if( propertiesCache != null )
			return propertiesCache;

		propertiesCache = new Properties();
		try {
			propertiesCache.load( new StringReader( textArea.getText() ) );
		} catch( IOException ex ) {
			ex.printStackTrace(); //TODO
		}
		return propertiesCache;
	}

	private void clearCache() {
		propertiesCache = null;
		parsedValueCache.clear();
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
}
