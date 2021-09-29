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
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for strings.
 *
 * @author Karl Tauber
 */
public class StringUtils
{
	/**
	 * Returns {@code true} if given string is {@code null} or length is zero.
	 */
	public static boolean isEmpty( String string ) {
		return string == null || string.isEmpty();
	}

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
		return split( str, delim, false, false );
	}

	/**
	 * Splits a string at the specified delimiter.
	 * If trimming is enabled, then leading and trailing whitespace characters are removed.
	 * If excludeEmpty is {@code true}, then only non-empty strings are returned.
	 *
	 * @since 2
	 */
	public static List<String> split( String str, char delim, boolean trim, boolean excludeEmpty ) {
		int delimIndex = str.indexOf( delim );
		if( delimIndex < 0 ) {
			if( trim )
				str = str.trim();
			return !excludeEmpty || !str.isEmpty()
				? Collections.singletonList( str )
				: Collections.emptyList();
		}

		ArrayList<String> strs = new ArrayList<>();
		int index = 0;
		while( delimIndex >= 0 ) {
			add( strs, str, index, delimIndex, trim, excludeEmpty );
			index = delimIndex + 1;
			delimIndex = str.indexOf( delim, index );
		}
		add( strs, str, index, str.length(), trim, excludeEmpty );

		return strs;
	}

	private static void add( List<String> strs, String str, int begin, int end, boolean trim, boolean excludeEmpty ) {
		if( trim ) {
			// skip leading whitespace
			while( begin < end && str.charAt( begin ) <= ' ' )
				begin++;

			// skip trailing whitespace
			while( begin < end && str.charAt( end - 1 ) <= ' ' )
				end--;
		}

		if( !excludeEmpty || end > begin )
			strs.add( str.substring( begin, end ) );
	}
}
