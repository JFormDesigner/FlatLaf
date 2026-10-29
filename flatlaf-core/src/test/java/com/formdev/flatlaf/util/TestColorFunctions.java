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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import org.junit.jupiter.api.Test;

/**
 * @author Karl Tauber
 */
public class TestColorFunctions
{
	private static final float DELTA = 0.0001f;

	@Test
	void colorFunctions() {
		// lighten, darken
		assertEquals( new Color( 0xff6666 ), ColorFunctions.lighten( Color.red, 0.2f ) );
		assertEquals( new Color( 0x990000 ), ColorFunctions.darken( Color.red, 0.2f ) );

		// saturate, desaturate
		assertEquals( new Color( 0x9c3030 ), ColorFunctions.saturate( new Color( 0x884444 ), 0.2f ) );
		assertEquals( new Color( 0x745858 ), ColorFunctions.desaturate( new Color( 0x884444 ), 0.2f ) );

		// spin
		assertEquals( new Color( 0xffaa00 ), ColorFunctions.spin( Color.red,40 ) );
		assertEquals( new Color( 0xff00aa ), ColorFunctions.spin( Color.red,-40 ) );

		// fade
		assertEquals( new Color( 0x33ff0000, true ), ColorFunctions.fade( Color.red, 0.2f ) );
		assertEquals( new Color( 0xccff0000, true ), ColorFunctions.fade( Color.red, 0.8f ) );
		assertEquals( new Color( 0xccff0000, true ), ColorFunctions.fade( new Color( 0x10ff0000, true ), 0.8f ) );

		// mix
		assertEquals( new Color( 0x1ae600 ), ColorFunctions.mix( Color.red, Color.green, 0.1f ) );
		assertEquals( new Color( 0x40bf00 ), ColorFunctions.mix( Color.red, Color.green, 0.25f ) );
		assertEquals( new Color( 0x808000 ), ColorFunctions.mix( Color.red, Color.green, 0.5f ) );
		assertEquals( new Color( 0xbf4000 ), ColorFunctions.mix( Color.red, Color.green, 0.75f ) );
		assertEquals( new Color( 0xe61a00 ), ColorFunctions.mix( Color.red, Color.green, 0.9f ) );

		// tint
		assertEquals( new Color( 0xff40ff ), ColorFunctions.tint( Color.magenta, 0.25f ) );
		assertEquals( new Color( 0xff80ff ), ColorFunctions.tint( Color.magenta, 0.5f ) );
		assertEquals( new Color( 0xffbfff ), ColorFunctions.tint( Color.magenta, 0.75f ) );

		// shade
		assertEquals( new Color( 0xbf00bf ), ColorFunctions.shade( Color.magenta, 0.25f ) );
		assertEquals( new Color( 0x800080 ), ColorFunctions.shade( Color.magenta, 0.5f ) );
		assertEquals( new Color( 0x400040 ), ColorFunctions.shade( Color.magenta, 0.75f ) );
	}

	@Test
	void colorFunctionsWithColorSpaces() {
		// mix
		assertEquals( new Color( 0x808000 ), ColorFunctions.mix( Color.red, Color.green, 0.5f, ColorFunctions.RGB ) );
		assertEquals( new Color( 0xbcbc00 ), ColorFunctions.mix( Color.red, Color.green, 0.5f, ColorFunctions.LRGB ) );
		assertEquals( new Color( 0xd0a800 ), ColorFunctions.mix( Color.red, Color.green, 0.5f, ColorFunctions.OKLAB ) );
		assertEquals( new Color( 0xbf4000 ), ColorFunctions.mix( Color.red, Color.green, 0.75f, ColorFunctions.RGB ) );
		assertEquals( new Color( 0xe18900 ), ColorFunctions.mix( Color.red, Color.green, 0.75f, ColorFunctions.LRGB ) );
		assertEquals( new Color( 0xed7300 ), ColorFunctions.mix( Color.red, Color.green, 0.75f, ColorFunctions.OKLAB ) );

		// tint
		assertEquals( new Color( 0xff80ff ), ColorFunctions.tint( Color.magenta, 0.5f, ColorFunctions.RGB ) );
		assertEquals( new Color( 0xffbcff ), ColorFunctions.tint( Color.magenta, 0.5f, ColorFunctions.LRGB ) );
		assertEquals( new Color( 0xffa6ff ), ColorFunctions.tint( Color.magenta, 0.5f, ColorFunctions.OKLAB ) );
		assertEquals( new Color( 0xffbfff ), ColorFunctions.tint( Color.magenta, 0.75f, ColorFunctions.RGB ) );
		assertEquals( new Color( 0xffe1ff ), ColorFunctions.tint( Color.magenta, 0.75f, ColorFunctions.LRGB ) );
		assertEquals( new Color( 0xffd4ff ), ColorFunctions.tint( Color.magenta, 0.75f, ColorFunctions.OKLAB ) );

		// shade
		assertEquals( new Color( 0x800080 ), ColorFunctions.shade( Color.magenta, 0.5f, ColorFunctions.RGB ) );
		assertEquals( new Color( 0xbc00bc ), ColorFunctions.shade( Color.magenta, 0.5f, ColorFunctions.LRGB ) );
		assertEquals( new Color( 0x630063 ), ColorFunctions.shade( Color.magenta, 0.5f, ColorFunctions.OKLAB ) );
		assertEquals( new Color( 0x400040 ), ColorFunctions.shade( Color.magenta, 0.75f, ColorFunctions.RGB ) );
		assertEquals( new Color( 0x890089 ), ColorFunctions.shade( Color.magenta, 0.75f, ColorFunctions.LRGB ) );
		assertEquals( new Color( 0x220022 ), ColorFunctions.shade( Color.magenta, 0.75f, ColorFunctions.OKLAB ) );
	}

	@Test
	void luma() {
		assertEquals( 0, ColorFunctions.luma( Color.black ) );
		assertEquals( 1, ColorFunctions.luma( Color.white ) );

		assertEquals( 0.2126f, ColorFunctions.luma( Color.red ) );
		assertEquals( 0.7152f, ColorFunctions.luma( Color.green ) );
		assertEquals( 0.0722f, ColorFunctions.luma( Color.blue ) );

		assertEquals( 0.9278f, ColorFunctions.luma( Color.yellow ) );
		assertEquals( 0.7874f, ColorFunctions.luma( Color.cyan ) );

		assertEquals( 0.051269464f, ColorFunctions.luma( Color.darkGray ) );
		assertEquals( 0.21586052f, ColorFunctions.luma( Color.gray ) );
		assertEquals( 0.52711517f, ColorFunctions.luma( Color.lightGray ) );
	}

	@Test
	void isDark() {
		assertTrue( ColorFunctions.isDark( Color.black ) );
		assertTrue( ColorFunctions.isDark( Color.darkGray ) );
		assertTrue( ColorFunctions.isDark( Color.gray ) );
		assertTrue( ColorFunctions.isDark( Color.red ) );
		assertTrue( ColorFunctions.isDark( Color.blue ) );

		assertFalse( ColorFunctions.isDark( Color.white ) );
		assertFalse( ColorFunctions.isDark( Color.lightGray ) );
		assertFalse( ColorFunctions.isDark( Color.green ) );
		assertFalse( ColorFunctions.isDark( Color.yellow ) );
		assertFalse( ColorFunctions.isDark( Color.cyan ) );
	}

	@Test
	void linearRGB() {
		// common 8-bit values
		assertArrayEquals( new float[] { 0, 0.000304f, 0.002428f, 1 }, ColorFunctions.toLinearRGB( new Color( 0, 1, 8 ) ), DELTA );
		assertArrayEquals( new float[] { 0.003035f, 0.003347f, 0.051269f, 1 }, ColorFunctions.toLinearRGB( new Color( 10, 11, 64 ) ), DELTA );
		assertArrayEquals( new float[] { 0.215861f, 0.527115f, 1, 1 }, ColorFunctions.toLinearRGB( new Color( 128, 192, 255 ) ), DELTA );

		// alpha must not change
		assertArrayEquals( new float[] { 0, 0, 0, 0.5f }, ColorFunctions.toLinearRGB( new Color( 0, 0, 0, 0.5f ) ) );
		assertEquals( new Color( 0, 0, 0, 0.5f ), ColorFunctions.fromLinearRGB( ColorFunctions.toLinearRGB( new Color( 0, 0, 0, 0.5f ) ) ) );

		// all 256 8-bit values
		for( int i = 0; i < 255; i += 3 ) {
			Color c = new Color( i, i + 1, i + 2 );
			assertEquals( c, ColorFunctions.fromLinearRGB( ColorFunctions.toLinearRGB( c ) ) );
			assertEquals(
				ColorFunctions.mix( c, Color.green, 0.3f, ColorFunctions.LRGB ),
				ColorFunctions.mix( Color.green, c, 1 - 0.3f, ColorFunctions.LRGB ) );
		}
	}

	@Test
	void oklab() {
		// known reference values
		assertArrayEquals( new float[] { 0, 0, 0, 1 }, ColorFunctions.toOklab( Color.black ) );
		assertArrayEquals( new float[] { 1, 0, 0, 1 }, ColorFunctions.toOklab( Color.white ), DELTA );
		assertArrayEquals( new float[] { 0.6280f, 0.2249f, 0.1258f, 1 }, ColorFunctions.toOklab( Color.red ), DELTA );
		assertArrayEquals( new float[] { 0.8664f, -0.2339f, 0.1795f, 1 }, ColorFunctions.toOklab( Color.green ), DELTA );
		assertArrayEquals( new float[] { 0.4520f, -0.0324f, -0.3116f, 1 }, ColorFunctions.toOklab( Color.blue ), DELTA );

		// gray
		assertArrayEquals( new float[] { 0.8077962f, 0, 0, 1 }, ColorFunctions.toOklab( Color.lightGray ), DELTA );
		assertArrayEquals( new float[] { 0.5998708f, 0, 0, 1 }, ColorFunctions.toOklab( Color.gray ), DELTA );

		// round trip
		oklabRoundTrip( Color.white );
		oklabRoundTrip( Color.lightGray );
		oklabRoundTrip( Color.gray );
		oklabRoundTrip( Color.darkGray );
		oklabRoundTrip( Color.black );
		oklabRoundTrip( Color.red );
		oklabRoundTrip( Color.pink );
		oklabRoundTrip( Color.orange );
		oklabRoundTrip( Color.yellow );
		oklabRoundTrip( Color.green );
		oklabRoundTrip( Color.magenta );
		oklabRoundTrip( Color.cyan );
		oklabRoundTrip( Color.blue );
	}

	private void oklabRoundTrip( Color c ) {
		assertEquals( c.getRGB(), ColorFunctions.fromOklab( ColorFunctions.toOklab( c ) ).getRGB() );
		assertEquals(
			ColorFunctions.mix( c, Color.green, 0.3f, ColorFunctions.OKLAB ),
			ColorFunctions.mix( Color.green, c, 1 - 0.3f, ColorFunctions.OKLAB ) );
	}
}
