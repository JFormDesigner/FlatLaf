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

package com.formdev.flatlaf.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * @author Karl Tauber
 */
public class TestStringUtils
{
	@Test
	void split() {
		// empty
		assertEquals(
			Arrays.asList( "" ),
			StringUtils.split( "", ',' ) );

		// not empty
		assertEquals(
			Arrays.asList( "a" ),
			StringUtils.split( "a", ',' ) );
		assertEquals(
			Arrays.asList( "a", "b" ),
			StringUtils.split( "a,b", ',' ) );
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a,b,c", ',' ) );

		// empty parts
		assertEquals(
			Arrays.asList( "", "b", "c" ),
			StringUtils.split( ",b,c", ',' ) );
		assertEquals(
			Arrays.asList( "a", "", "c" ),
			StringUtils.split( "a,,c", ',' ) );
		assertEquals(
			Arrays.asList( "a", "b", "" ),
			StringUtils.split( "a,b,", ',' ) );

		// parts with leading/trailing spaces
		assertEquals(
			Arrays.asList( "a", " b", " c" ),
			StringUtils.split( "a, b, c", ',' ) );
		assertEquals(
			Arrays.asList( " a", "b", "c " ),
			StringUtils.split( " a,b,c ", ',' ) );
		assertEquals(
			Arrays.asList( "  a", "  b  ", "c  " ),
			StringUtils.split( "  a,  b  ,c  ", ',' ) );

		// space delimiter
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a b c", ' ' ) );
		assertEquals(
			Arrays.asList( "a", "", "", "b", "", "c" ),
			StringUtils.split( "a   b  c", ' ' ) );

		// new line delimiter
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a\nb\nc", '\n' ) );
		assertEquals(
			Arrays.asList( "a", "", "", "b", "", "c" ),
			StringUtils.split( "a\n\n\nb\n\nc", '\n' ) );
	}

	@Test
	void splitTrim() {
		// empty
		assertEquals(
			Arrays.asList( "" ),
			StringUtils.split( "", ',', true, false ) );

		// not empty
		assertEquals(
			Arrays.asList( "a" ),
			StringUtils.split( "a", ',', true, false ) );
		assertEquals(
			Arrays.asList( "a", "b" ),
			StringUtils.split( "a,b", ',', true, false ) );
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a,b,c", ',', true, false ) );

		// empty parts
		assertEquals(
			Arrays.asList( "", "b", "c" ),
			StringUtils.split( ",b,c", ',', true, false ) );
		assertEquals(
			Arrays.asList( "a", "", "c" ),
			StringUtils.split( "a,,c", ',', true, false ) );
		assertEquals(
			Arrays.asList( "a", "b", "" ),
			StringUtils.split( "a,b,", ',', true, false ) );

		// parts with leading/trailing spaces
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a, b, c", ',', true, false ) );
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( " a,b,c ", ',', true, false ) );
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "  a,  b  ,c  ", ',', true, false ) );

		// space delimiter
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a b c", ' ', true, false ) );
		assertEquals(
			Arrays.asList( "a", "", "", "b", "", "c" ),
			StringUtils.split( "a   b  c", ' ', true, false ) );

		// new line delimiter
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a\nb\nc", '\n', true, false ) );
		assertEquals(
			Arrays.asList( "a", "", "", "b", "", "c" ),
			StringUtils.split( "a\n\n\nb\n\nc", '\n', true, false ) );
	}

	@Test
	void splitTrimAndExcludeEmpty() {
		// empty
		assertEquals(
			Arrays.asList(),
			StringUtils.split( "", ',', true, true ) );

		// not empty
		assertEquals(
			Arrays.asList( "a" ),
			StringUtils.split( "a", ',', true, true ) );
		assertEquals(
			Arrays.asList( "a", "b" ),
			StringUtils.split( "a,b", ',', true, true ) );
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a,b,c", ',', true, true ) );

		// empty parts
		assertEquals(
			Arrays.asList( "b", "c" ),
			StringUtils.split( ",b,c", ',', true, true ) );
		assertEquals(
			Arrays.asList( "a", "c" ),
			StringUtils.split( "a,,c", ',', true, true ) );
		assertEquals(
			Arrays.asList( "a", "b" ),
			StringUtils.split( "a,b,", ',', true, true ) );

		// parts with leading/trailing spaces
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a, b, c", ',', true, true ) );
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( " a,b,c ", ',', true, true ) );
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "  a,  b  ,c  ", ',', true, true ) );

		// space delimiter
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a b c", ' ', true, true ) );
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a   b  c", ' ', true, true ) );

		// new line delimiter
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a\nb\nc", '\n', true, true ) );
		assertEquals(
			Arrays.asList( "a", "b", "c" ),
			StringUtils.split( "a\n\n\nb\n\nc", '\n', true, true ) );
	}

	@Test
	void substringTrimmed() {
		testSubstringTrimmed( "", 0 );

		testSubstringTrimmed( "a", 0 );
		testSubstringTrimmed( "a", 1 );

		testSubstringTrimmed( "a  ", 0 );
		testSubstringTrimmed( "  a", 0 );
		testSubstringTrimmed( "  a  ", 0 );

		testSubstringTrimmed( "  a  ", 1 );

		testSubstringTrimmed( "  a  ", 0, 3 );
		testSubstringTrimmed( "  a  ", 1, 4 );
	}

	private void testSubstringTrimmed( String str, int beginIndex ) {
		assertEquals(
			str.substring( beginIndex ).trim(),
			StringUtils.substringTrimmed( str, beginIndex ) );
	}

	private void testSubstringTrimmed( String str, int beginIndex, int endIndex ) {
		assertEquals(
			str.substring( beginIndex, endIndex ).trim(),
			StringUtils.substringTrimmed( str, beginIndex, endIndex ) );
	}

	@Test
	void trimmedEmpty() {
		testTrimmedEmpty( "" );
		testTrimmedEmpty( "a" );
		testTrimmedEmpty( " a " );
		testTrimmedEmpty( " " );
		testTrimmedEmpty( "  " );
	}

	private void testTrimmedEmpty( String str ) {
		assertEquals( str.trim().isEmpty(), StringUtils.isTrimmedEmpty( str ) );
	}
}
