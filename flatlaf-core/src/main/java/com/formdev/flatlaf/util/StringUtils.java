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

package com.formdev.flatlaf.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for strings.
 *
 * @author Karl Tauber
 */
public class StringUtils
{
	public static String removeLeading( String string, String leading ) {
		return string.startsWith( leading )
			? string.substring( leading.length() )
			: string;
	}

	public static String removeTrailing( String string, String trailing ) {
		return string.endsWith( trailing )
			? string.substring( 0, string.length() - trailing.length() )
			: string;
	}

	public static List<String> split( String str, char delim ) {
		ArrayList<String> strs = new ArrayList<>();
		int delimIndex = str.indexOf( delim );
		int index = 0;
		while( delimIndex >= 0 ) {
			strs.add( str.substring( index, delimIndex ) );
			index = delimIndex + 1;
			delimIndex = str.indexOf( delim, index );
		}
		strs.add( str.substring( index ) );

		return strs;
	}
}
