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
import java.awt.Color;
import org.junit.jupiter.api.Test;

/**
 * @author Karl Tauber
 */
public class TestColorFunctions
{
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
}
