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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 * @author Karl Tauber
 */
class FlatCompletionProvider
	extends DefaultCompletionProvider
{
	FlatCompletionProvider() {
		// load all keys
		HashSet<String> keys = new HashSet<>();
		try {
			try( InputStream in = getClass().getResourceAsStream( "/com/formdev/flatlaf/themeeditor/FlatLafUIKeys.txt" ) ) {
				if( in != null ) {
					try( BufferedReader reader = new BufferedReader( new InputStreamReader( in, "UTF-8" ) ) ) {
						String key;
						while( (key = reader.readLine()) != null ) {
							keys.add( key );
						}
					}
				}
			}
		} catch( IOException ex ) {
			ex.printStackTrace(); // TODO
		}

		// collect key parts
		HashSet<String> keyParts = new HashSet<>();
		for( String key : keys ) {
			int delimIndex = key.length() + 1;
			while( (delimIndex = key.lastIndexOf( '.', delimIndex - 1 )) >= 0 )
				keyParts.add( key.substring( 0, delimIndex + 1 ) );
		}

		// add key parts
		for( String keyPart : keyParts )
			addCompletion( new BasicCompletion( this, keyPart ) );

		// add all keys
		for( String key : keys )
			addCompletion( new BasicCompletion( this, key ) );
	}

	@Override
	protected boolean isValidChar( char ch ) {
		return super.isValidChar( ch ) || ch == '.';
	}
}
