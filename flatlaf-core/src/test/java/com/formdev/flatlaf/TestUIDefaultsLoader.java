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

package com.formdev.flatlaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.Objects;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.UIDefaults.ActiveValue;
import javax.swing.UIDefaults.LazyValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.ColorFunctions.ColorFunction;
import com.formdev.flatlaf.util.ColorFunctions.Fade;
import com.formdev.flatlaf.util.ColorFunctions.HSLChange;
import com.formdev.flatlaf.util.ColorFunctions.HSLIncreaseDecrease;
import com.formdev.flatlaf.util.ColorFunctions.Mix;
import com.formdev.flatlaf.util.ColorFunctions.Mix2;

/**
 * @author Karl Tauber
 */
public class TestUIDefaultsLoader
{
	@Test
	void parseValue() {
		assertEquals( null, UIDefaultsLoader.parseValue( "dummy", "null", null ) );
		assertEquals( false, UIDefaultsLoader.parseValue( "dummy", "false", null ) );
		assertEquals( true, UIDefaultsLoader.parseValue( "dummy", "true", null ) );

		assertEquals( "hello", UIDefaultsLoader.parseValue( "dummy", "hello", null ) );
		assertEquals( "hello", UIDefaultsLoader.parseValue( "dummy", "\"hello\"", null ) );
		assertEquals( "null", UIDefaultsLoader.parseValue( "dummy", "\"null\"", null ) );

		assertEquals( 'a', UIDefaultsLoader.parseValue( "dummyChar", "a", null ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummy", "123", null ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummyWidth", "123", null ) );
		assertEquals( 1.23f, UIDefaultsLoader.parseValue( "dummy", "1.23", null ) );
		assertEquals( 1.23f, UIDefaultsLoader.parseValue( "dummyWidth", "1.23", null ) );

		assertEquals( new Insets( 1,2,3,4 ), UIDefaultsLoader.parseValue( "dummyInsets", "1,2,3,4", null ) );
		assertEquals( new Dimension( 1,2 ), UIDefaultsLoader.parseValue( "dummySize", "1,2", null ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "dummy", "#f00", null ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "dummyColor", "#f00", null ) );
	}

	@Test
	void parseValueWithJavaType() {
		assertEquals( null, UIDefaultsLoader.parseValue( "dummy", "null", String.class ) );
		assertEquals( false, UIDefaultsLoader.parseValue( "dummy", "false", boolean.class ) );
		assertEquals( true, UIDefaultsLoader.parseValue( "dummy", "true", Boolean.class ) );

		assertEquals( "hello", UIDefaultsLoader.parseValue( "dummy", "hello", String.class ) );
		assertEquals( "hello", UIDefaultsLoader.parseValue( "dummy", "\"hello\"", String.class ) );
		assertEquals( "null", UIDefaultsLoader.parseValue( "dummy", "\"null\"", String.class ) );
		assertEquals( null, UIDefaultsLoader.parseValue( "dummy", "null", String.class ) );

		assertEquals( 'a', UIDefaultsLoader.parseValue( "dummy", "a", char.class ) );
		assertEquals( 'a', UIDefaultsLoader.parseValue( "dummy", "a", Character.class ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummy", "123", int.class ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummy", "123", Integer.class ) );
		assertEquals( 1.23f, UIDefaultsLoader.parseValue( "dummy", "1.23", float.class ) );
		assertEquals( 1.23f, UIDefaultsLoader.parseValue( "dummy", "1.23", Float.class ) );

		assertEquals( new Insets( 1,2,3,4 ), UIDefaultsLoader.parseValue( "dummy", "1,2,3,4", Insets.class ) );
		assertEquals( new Dimension( 1,2 ), UIDefaultsLoader.parseValue( "dummy", "1,2", Dimension.class ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "dummy", "#f00", Color.class ) );
	}

	@Test
	void parseBorders() {
		Insets insets = new Insets( 1,2,3,4 );
		assertBorderEquals( new FlatEmptyBorder( insets ), "1,2,3,4" );
		assertBorderEquals( new FlatEmptyBorder( insets ), "1,2,3,4,,," );
		assertBorderEquals( new FlatLineBorder( insets, Color.red ), "1,2,3,4,#f00" );
		assertBorderEquals( new FlatLineBorder( insets, Color.red, 2.5f, -1 ), "1,2,3,4,#f00,2.5" );
		assertBorderEquals( new FlatLineBorder( insets, Color.red, 2.5f, 6 ), "1,2,3,4,#f00,2.5,6" );
		assertBorderEquals( new FlatLineBorder( insets, Color.red, 1, 6 ), "1,2,3,4,#f00,,6" );
		assertBorderEquals( new FlatLineBorder( insets, null, 1, 6 ), "1,2,3,4,,,6" );
	}

	private void assertBorderEquals( Border expected, String actualStyle ) {
		Border actual = (Border) ((LazyValue)UIDefaultsLoader.parseValue( "dummyBorder", actualStyle, null )).createValue( null );
		assertEquals( expected.getClass(), actual.getClass() );
		if( expected instanceof FlatEmptyBorder )
			assertEquals( ((FlatEmptyBorder)actual).getBorderInsets(), ((FlatEmptyBorder)expected).getBorderInsets() );
		if( expected instanceof FlatLineBorder ) {
			FlatLineBorder a = (FlatLineBorder) actual;
			FlatLineBorder e = (FlatLineBorder) expected;
			assertEquals( a.getLineColor(), e.getLineColor() );
			assertEquals( a.getLineThickness(), e.getLineThickness() );
			assertEquals( a.getArc(), e.getArc() );
		}
	}

	@Test
	void parseFonts() {
		// style
		UIManager.put( "defaultFont", new Font( Font.DIALOG, Font.PLAIN, 10 ) );
		assertFontEquals( Font.DIALOG, Font.PLAIN, 10, "normal" );
		assertFontEquals( Font.DIALOG, Font.BOLD, 10, "bold" );
		assertFontEquals( Font.DIALOG, Font.ITALIC, 10, "italic" );
		assertFontEquals( Font.DIALOG, Font.BOLD|Font.ITALIC, 10, "bold italic" );

		// derived style
		assertFontEquals( Font.DIALOG, Font.BOLD, 10, "+bold" );
		assertFontEquals( Font.DIALOG, Font.ITALIC, 10, "+italic" );
		assertFontEquals( Font.DIALOG, Font.BOLD|Font.ITALIC, 10, "+bold +italic" );
		UIManager.put( "defaultFont", new Font( Font.DIALOG, Font.BOLD|Font.ITALIC, 10 ) );
		assertFontEquals( Font.DIALOG, Font.ITALIC, 10, "-bold" );
		assertFontEquals( Font.DIALOG, Font.BOLD, 10, "-italic" );
		assertFontEquals( Font.DIALOG, Font.PLAIN, 10, "-bold -italic" );
		UIManager.put( "defaultFont", new Font( Font.DIALOG, Font.BOLD, 10 ) );
		assertFontEquals( Font.DIALOG, Font.ITALIC, 10, "-bold +italic" );

		// size
		UIManager.put( "defaultFont", new Font( Font.DIALOG, Font.PLAIN, 10 ) );
		assertFontEquals( Font.DIALOG, Font.PLAIN, 12, "12" );
		assertFontEquals( Font.DIALOG, Font.PLAIN, 13, "+3" );
		assertFontEquals( Font.DIALOG, Font.PLAIN, 6, "-4" );
		assertFontEquals( Font.DIALOG, Font.PLAIN, 15, "150%" );

		// family
		assertFontEquals( Font.MONOSPACED, Font.PLAIN, 10, "Monospaced" );
		assertFontEquals( Font.MONOSPACED, Font.PLAIN, 10, "Monospaced, Dialog" );
		assertFontEquals( Font.DIALOG, Font.PLAIN, 10, "Dialog, Monospaced" );

		// unknown family
		assertFontEquals( Font.MONOSPACED, Font.PLAIN, 12, "normal 12 UnknownFamily, Monospaced" );
		assertFontEquals( Font.DIALOG, Font.PLAIN, 12, "normal 12 UnknownFamily, Dialog" );
		assertFontEquals( Font.DIALOG, Font.PLAIN, 12, "normal 12 UnknownFamily, 'Another unknown family'" );

		// all
		assertFontEquals( Font.MONOSPACED, Font.BOLD, 13, "bold 13 Monospaced" );
		assertFontEquals( Font.DIALOG, Font.ITALIC, 14, "italic 14 Dialog" );
		assertFontEquals( Font.DIALOG, Font.BOLD|Font.ITALIC, 15, "bold italic 15 Dialog" );

		UIManager.put( "defaultFont", null );
	}

	private void assertFontEquals( String name, int style, int size, String actualStyle ) {
		assertEquals(
			new Font( name, style, size ),
			((ActiveValue)UIDefaultsLoader.parseValue( "dummyFont", actualStyle, null )).createValue( null ) );
	}

	@Test
	void parseInstance() {
		String className = TestInstance.class.getName();
		assertEquals( new TestInstance(), ((LazyValue)UIDefaultsLoader.parseValue( "dummyIcon", className, null )).createValue( null ) );
		assertInstanceEquals( new TestInstance(), null );
		assertInstanceEquals( new TestInstance( "some string" ), "some string" );
		assertInstanceEquals( new TestInstance( false ), "false" );
		assertInstanceEquals( new TestInstance( true ), "true" );
		assertInstanceEquals( new TestInstance( 123 ), "123" );
		assertInstanceEquals( new TestInstance( 123.456f ), "123.456" );
		assertInstanceEquals( new TestInstance( Color.red ), "#f00" );
		assertInstanceEquals( new TestInstance( "some string", true ), "some string, true" );
		assertInstanceEquals( new TestInstance( "some string", true, 123 ), "some string, true, 123" );
		assertInstanceEquals( new TestInstance( "some string", 123, true ), "some string, 123, true" );
		assertInstanceEquals( new TestInstance( "some string", 123.456f, true ), "some string, 123.456, true" );
		assertInstanceEquals( new TestInstance( 123, "some string" ), "123, some string" );
	}

	private void assertInstanceEquals( TestInstance expected, String params ) {
		String value = TestInstance.class.getName() + (params != null ? "," + params : "");
		assertEquals( expected, ((LazyValue)UIDefaultsLoader.parseValue( "dummyIcon", value, null )).createValue( null ) );
	}

	@Test
	void parseColorFunctions() {
		// lighten
		assertEquals( new Color( 0xff6666 ), parseColor( "lighten(#f00, 20%)" ) );
		assertEquals( new Color( 0xff3333 ), parseColor( "lighten(#f00, 20%, relative)" ) );
		assertEquals( new Color( 0xaaaaaa ), parseColor( "lighten(#ddd, 20%, autoInverse)" ) );
		assertEquals( new Color( 0xb1b1b1 ), parseColor( "lighten(#ddd, 20%, relative autoInverse)" ) );

		// darken
		assertEquals( new Color( 0x990000 ), parseColor( "darken(#f00, 20%)" ) );
		assertEquals( new Color( 0xcc0000 ), parseColor( "darken(#f00, 20%, relative)" ) );
		assertEquals( new Color( 0x555555 ), parseColor( "darken(#222, 20%, autoInverse)" ) );
		assertEquals( new Color( 0x292929 ), parseColor( "darken(#222, 20%, relative autoInverse)" ) );

		// saturate
		assertEquals( new Color( 0xf32e2e ), parseColor( "saturate(#d44, 20%)" ) );
		assertEquals( new Color( 0xec3535 ), parseColor( "saturate(#d44, 20%, relative)" ) );
		assertEquals( new Color( 0xc75a5a ), parseColor( "saturate(#d44, 20%, autoInverse)" ) );
		assertEquals( new Color( 0xce5353 ), parseColor( "saturate(#d44, 20%, relative autoInverse)" ) );

		// desaturate
		assertEquals( new Color( 0x745858 ), parseColor( "desaturate(#844, 20%)" ) );
		assertEquals( new Color( 0x814b4b ), parseColor( "desaturate(#844, 20%, relative)" ) );
		assertEquals( new Color( 0x9c3030 ), parseColor( "desaturate(#844, 20%, autoInverse)" ) );
		assertEquals( new Color( 0x8f3d3d ), parseColor( "desaturate(#844, 20%, relative autoInverse)" ) );

		// fadein
		assertEquals( new Color( 0xddff0000, true ), parseColor( "fadein(#f00a, 20%)" ) );
		assertEquals( new Color( 0xccff0000, true ), parseColor( "fadein(#f00a, 20%, relative)" ) );
		assertEquals( new Color( 0x77ff0000, true ), parseColor( "fadein(#f00a, 20%, autoInverse)" ) );
		assertEquals( new Color( 0x88ff0000, true ), parseColor( "fadein(#f00a, 20%, relative autoInverse)" ) );

		// fadeout
		assertEquals( new Color( 0x11ff0000, true ), parseColor( "fadeout(#f004, 20%)" ) );
		assertEquals( new Color( 0x36ff0000, true ), parseColor( "fadeout(#f004, 20%, relative)" ) );
		assertEquals( new Color( 0x77ff0000, true ), parseColor( "fadeout(#f004, 20%, autoInverse)" ) );
		assertEquals( new Color( 0x52ff0000, true ), parseColor( "fadeout(#f004, 20%, relative autoInverse)" ) );

		// fade
		assertEquals( new Color( 0x33ff0000, true ), parseColor( "fade(#f00, 20%)" ) );
		assertEquals( new Color( 0xccff0000, true ), parseColor( "fade(#ff000010, 80%)" ) );

		// spin
		assertEquals( new Color( 0xffaa00 ), parseColor( "spin(#f00, 40)" ) );
		assertEquals( new Color( 0xff00aa ), parseColor( "spin(#f00, -40)" ) );

		// changeHue / changeSaturation / changeLightness / changeAlpha
		assertEquals( new Color( 0xffaa00 ), parseColor( "changeHue(#f00, 40)" ) );
		assertEquals( new Color( 0xb34d4d ), parseColor( "changeSaturation(#f00, 40%)" ) );
		assertEquals( new Color( 0xcc0000 ), parseColor( "changeLightness(#f00, 40%)" ) );
		assertEquals( new Color( 0x66ff0000, true ), parseColor( "changeAlpha(#f00, 40%)" ) );

		// mix
		assertEquals( new Color( 0x808000 ), parseColor( "mix(#f00, #0f0)" ) );
		assertEquals( new Color( 0xbf4000 ), parseColor( "mix(#f00, #0f0, 75%)" ) );

		// tint
		assertEquals( new Color( 0xff80ff ), parseColor( "tint(#f0f)" ) );
		assertEquals( new Color( 0xffbfff ), parseColor( "tint(#f0f, 75%)" ) );

		// shade
		assertEquals( new Color( 0x800080 ), parseColor( "shade(#f0f)" ) );
		assertEquals( new Color( 0x400040 ), parseColor( "shade(#f0f, 75%)" ) );

		// contrast
		assertEquals( new Color( 0x0000ff ), parseColor( "contrast(#bbb, #00f, #0f0)" ) );
		assertEquals( new Color( 0x00ff00 ), parseColor( "contrast(#444, #00f, #0f0)" ) );
		assertEquals( new Color( 0x00ff00 ), parseColor( "contrast(#bbb, #00f, #0f0, 60%)" ) );

		// rgb / rgba
		assertEquals( new Color( 0x5a8120 ), parseColor( "rgb(90, 129, 32)" ) );
		assertEquals( new Color( 0x5a8120 ), parseColor( "rgb(90, 129, 32)" ) );
		assertEquals( new Color( 0x197fb2 ), parseColor( "rgb(10%,50%,70%)" ) );
		assertEquals( new Color( 0x197f46 ), parseColor( "rgb(10%,50%,70)" ) );
		assertEquals( new Color( 0x405a8120, true ), parseColor( "rgba(90, 129, 32, 64)" ) );
		assertEquals( new Color( 0x335a8120, true ), parseColor( "rgba(90, 129, 32, 20%)" ) );

		// hsl / hsla
		assertEquals( new Color( 0x7fff00 ), parseColor( "hsl(90, 100%, 50%)" ) );
		assertEquals( new Color( 0x337fff00, true ), parseColor( "hsla(90, 100%, 50%, 20%)" ) );
	}

	@Test
	void parseLazyColorFunctions() {
		// lighten
		assertEquals( new Color( 0xff6666 ), parseColorLazy( "lighten(dummyColor, 20%, lazy)", new Color( 0xff0000 ) ) );

		// darken
		assertEquals( new Color( 0x990000 ), parseColorLazy( "darken(dummyColor, 20%, lazy)", new Color( 0xff0000 ) ) );

		// saturate
		assertEquals( new Color( 0xf32e2e ), parseColorLazy( "saturate(dummyColor, 20%, lazy)", new Color( 0xdd4444 ) ) );

		// desaturate
		assertEquals( new Color( 0x745858 ), parseColorLazy( "desaturate(dummyColor, 20%, lazy)", new Color( 0x884444 ) ) );

		// fadein
		assertEquals( new Color( 0xddff0000, true ), parseColorLazy( "fadein(dummyColor, 20%, lazy)", new Color( 0xaaff0000, true ) ) );

		// fadeout
		assertEquals( new Color( 0x11ff0000, true ), parseColorLazy( "fadeout(dummyColor, 20%, lazy)", new Color( 0x44ff0000, true ) ) );

		// fade
		assertEquals( new Color( 0x33ff0000, true ), parseColorLazy( "fade(dummyColor, 20%, lazy)", new Color( 0xff0000 ) ) );
		assertEquals( new Color( 0xccff0000, true ), parseColorLazy( "fade(dummyColor, 80%, lazy)", new Color( 0x10ff0000, true ) ) );

		// spin
		assertEquals( new Color( 0xffaa00 ), parseColorLazy( "spin(dummyColor, 40, lazy)", new Color( 0xff0000 ) ) );
		assertEquals( new Color( 0xff00aa ), parseColorLazy( "spin(dummyColor, -40, lazy)", new Color( 0xff0000 ) ) );

		// changeHue / changeSaturation / changeLightness / changeAlpha
		assertEquals( new Color( 0xffaa00 ), parseColorLazy( "changeHue(dummyColor, 40, lazy)", new Color( 0xff0000 ) ) );
		assertEquals( new Color( 0xb34d4d ), parseColorLazy( "changeSaturation(dummyColor, 40%, lazy)", new Color( 0xff0000 ) ) );
		assertEquals( new Color( 0xcc0000 ), parseColorLazy( "changeLightness(dummyColor, 40%, lazy)", new Color( 0xff0000 ) ) );
		assertEquals( new Color( 0x66ff0000, true ), parseColorLazy( "changeAlpha(dummyColor, 40%, lazy)", new Color( 0xff0000 ) ) );

		// mix
		assertEquals( new Color( 0x808000 ), parseColorLazy( "mix(#f00, dummyColor, lazy)", new Color( 0x00ff00 ) ) );
		assertEquals( new Color( 0xbf4000 ), parseColorLazy( "mix(#f00, dummyColor, 75%, lazy)", new Color( 0x00ff00 ) ) );

		// tint
		assertEquals( new Color( 0xff80ff ), parseColorLazy( "tint(dummyColor, lazy)", new Color( 0xff00ff ) ) );
		assertEquals( new Color( 0xffbfff ), parseColorLazy( "tint(dummyColor, 75%, lazy)", new Color( 0xff00ff ) ) );

		// shade
		assertEquals( new Color( 0x800080 ), parseColorLazy( "shade(dummyColor, lazy)", new Color( 0xff00ff ) ) );
		assertEquals( new Color( 0x400040 ), parseColorLazy( "shade(dummyColor, 75%, lazy)", new Color( 0xff00ff ) ) );
	}

	@Test
	void parseDerivedColorFunctions() {
		// mix
		assertDerivedColorEquals( new Color( 0x808000 ), "mix(#f00, #0f0, derived)", new Mix2( Color.red, 50 ) );
		assertDerivedColorEquals( new Color( 0xbf4000 ), "mix(#f00, #0f0, 75%, derived)", new Mix2( Color.red, 75 ) );

		// tint
		assertDerivedColorEquals( new Color( 0xff80ff ), "tint(#f0f, derived)", new Mix2( Color.white, 50 ) );
		assertDerivedColorEquals( new Color( 0xffbfff ), "tint(#f0f, 75%, derived)", new Mix2( Color.white, 75 ) );

		// shade
		assertDerivedColorEquals( new Color( 0x800080 ), "shade(#f0f, derived)", new Mix2( Color.black, 50 ) );
		assertDerivedColorEquals( new Color( 0x400040 ), "shade(#f0f, 75%, derived)", new Mix2( Color.black, 75 ) );


		// lighten
		assertDerivedColorEquals( new Color( 0xff6666 ), "lighten(#f00, 20%, derived)",                                 new HSLIncreaseDecrease( 2, true,  20, false, true  ) );
		assertDerivedColorEquals( new Color( 0xff3333 ), "lighten(#f00, 20%, derived relative)",                        new HSLIncreaseDecrease( 2, true,  20, true,  true  ) );
		assertDerivedColorEquals( new Color( 0xffffff ), "lighten(#ddd, 20%, derived noAutoInverse)",                   new HSLIncreaseDecrease( 2, true,  20, false, false ) );
		assertDerivedColorEquals( new Color( 0xffffff ), "lighten(#ddd, 20%, derived relative noAutoInverse)",          new HSLIncreaseDecrease( 2, true,  20, true,  false ) );

		// darken
		assertDerivedColorEquals( new Color( 0x990000 ), "darken(#f00, 20%, derived)",                                  new HSLIncreaseDecrease( 2, false, 20, false, true  ) );
		assertDerivedColorEquals( new Color( 0xcc0000 ), "darken(#f00, 20%, derived relative)",                         new HSLIncreaseDecrease( 2, false, 20, true,  true  ) );
		assertDerivedColorEquals( new Color( 0x000000 ), "darken(#222, 20%, derived noAutoInverse)",                    new HSLIncreaseDecrease( 2, false, 20, false, false ) );
		assertDerivedColorEquals( new Color( 0x1b1b1b ), "darken(#222, 20%, derived relative noAutoInverse)",           new HSLIncreaseDecrease( 2, false, 20, true,  false ) );

		// saturate
		assertDerivedColorEquals( new Color( 0xc75a5a ), "saturate(#d44, 20%, derived)",                                new HSLIncreaseDecrease( 1, true,  20, false, true  ) );
		assertDerivedColorEquals( new Color( 0xce5353 ), "saturate(#d44, 20%, derived relative)",                       new HSLIncreaseDecrease( 1, true,  20, true,  true  ) );
		assertDerivedColorEquals( new Color( 0xf32e2e ), "saturate(#d44, 20%, derived noAutoInverse)",                  new HSLIncreaseDecrease( 1, true,  20, false, false ) );
		assertDerivedColorEquals( new Color( 0xec3535 ), "saturate(#d44, 20%, derived relative noAutoInverse)",         new HSLIncreaseDecrease( 1, true,  20, true,  false ) );

		// desaturate
		assertDerivedColorEquals( new Color( 0x9c3030 ), "desaturate(#844, 20%, derived)",                              new HSLIncreaseDecrease( 1, false, 20, false, true  ) );
		assertDerivedColorEquals( new Color( 0x8f3d3d ), "desaturate(#844, 20%, derived relative)",                     new HSLIncreaseDecrease( 1, false, 20, true,  true  ) );
		assertDerivedColorEquals( new Color( 0x745858 ), "desaturate(#844, 20%, derived noAutoInverse)",                new HSLIncreaseDecrease( 1, false, 20, false, false ) );
		assertDerivedColorEquals( new Color( 0x814b4b ), "desaturate(#844, 20%, derived relative noAutoInverse)",       new HSLIncreaseDecrease( 1, false, 20, true,  false ) );

		// fadein
		assertDerivedColorEquals( new Color( 0x77ff0000, true ), "fadein(#f00a, 20%, derived)",                         new HSLIncreaseDecrease( 3, true,  20, false, true  ) );
		assertDerivedColorEquals( new Color( 0x88ff0000, true ), "fadein(#f00a, 20%, derived relative)",                new HSLIncreaseDecrease( 3, true,  20, true,  true  ) );
		assertDerivedColorEquals( new Color( 0xddff0000, true ), "fadein(#f00a, 20%, derived noAutoInverse)",           new HSLIncreaseDecrease( 3, true,  20, false, false ) );
		assertDerivedColorEquals( new Color( 0xccff0000, true ), "fadein(#f00a, 20%, derived relative noAutoInverse)",  new HSLIncreaseDecrease( 3, true,  20, true,  false ) );

		// fadeout
		assertDerivedColorEquals( new Color( 0x77ff0000, true ), "fadeout(#f004, 20%, derived)",                        new HSLIncreaseDecrease( 3, false, 20, false, true  ) );
		assertDerivedColorEquals( new Color( 0x52ff0000, true ), "fadeout(#f004, 20%, derived relative)",               new HSLIncreaseDecrease( 3, false, 20, true,  true  ) );
		assertDerivedColorEquals( new Color( 0x11ff0000, true ), "fadeout(#f004, 20%, derived noAutoInverse)",          new HSLIncreaseDecrease( 3, false, 20, false, false ) );
		assertDerivedColorEquals( new Color( 0x36ff0000, true ), "fadeout(#f004, 20%, derived relative noAutoInverse)", new HSLIncreaseDecrease( 3, false, 20, true,  false ) );

		// fade
		assertDerivedColorEquals( new Color( 0x33ff0000, true ), "fade(#f00, 20%, derived)",      new Fade( 20 ) );
		assertDerivedColorEquals( new Color( 0xccff0000, true ), "fade(#ff000010, 80%, derived)", new Fade( 80 ) );

		// spin
		assertDerivedColorEquals( new Color( 0xffaa00 ), "spin(#f00, 40, derived)",  new HSLIncreaseDecrease( 0, true, 40, false, false ) );
		assertDerivedColorEquals( new Color( 0xff00aa ), "spin(#f00, -40, derived)", new HSLIncreaseDecrease( 0, true, -40, false, false ) );

		// changeHue / changeSaturation / changeLightness / changeAlpha
		assertDerivedColorEquals( new Color( 0xffaa00 ), "changeHue(#f00, 40, derived)",            new HSLChange( 0, 40 ) );
		assertDerivedColorEquals( new Color( 0xb34d4d ), "changeSaturation(#f00, 40%, derived)",    new HSLChange( 1, 40 ) );
		assertDerivedColorEquals( new Color( 0xcc0000 ), "changeLightness(#f00, 40%, derived)",     new HSLChange( 2, 40 ) );
		assertDerivedColorEquals( new Color( 0x66ff0000, true ), "changeAlpha(#f00, 40%, derived)", new HSLChange( 3, 40 ) );

		// mix
		assertDerivedColorEquals( new Color( 0x808000 ), "mix(#f00, #0f0, derived)",      new Mix2( new Color( 0xff0000 ), 50 ) );
		assertDerivedColorEquals( new Color( 0xbf4000 ), "mix(#f00, #0f0, 75%, derived)", new Mix2( new Color( 0xff0000 ), 75 ) );

		// tint
		assertDerivedColorEquals( new Color( 0xff80ff ), "tint(#f0f, derived)",           new Mix2( new Color( 0xffffff ), 50 ) );
		assertDerivedColorEquals( new Color( 0xffbfff ), "tint(#f0f, 75%, derived)",      new Mix2( new Color( 0xffffff ), 75 ) );

		// shade
		assertDerivedColorEquals( new Color( 0x800080 ), "shade(#f0f, derived)",          new Mix2( new Color( 0x000000 ), 50 ) );
		assertDerivedColorEquals( new Color( 0x400040 ), "shade(#f0f, 75%, derived)",     new Mix2( new Color( 0x000000 ), 75 ) );
	}

	private void assertDerivedColorEquals( Color expectedColor, String actualStyle, ColorFunction... expectedFunctions ) {
		Object actual = parseColor( actualStyle );
		assertInstanceOf( DerivedColor.class, actual );
		assertEquals( expectedColor, actual );

		ColorFunction[] actualFunctions = ((DerivedColor)actual).getFunctions();
		assertEquals( expectedFunctions.length, actualFunctions.length );
		for( int i = 0; i < expectedFunctions.length; i++ )
			assertColorFunctionEquals( expectedFunctions[i], actualFunctions[i] );
	}

	private void assertColorFunctionEquals( ColorFunction expected, ColorFunction actual ) {
		assertEquals( expected.getClass(), actual.getClass() );

		if( expected instanceof HSLIncreaseDecrease ) {
			HSLIncreaseDecrease e = (HSLIncreaseDecrease) expected;
			HSLIncreaseDecrease a = (HSLIncreaseDecrease) actual;
			assertEquals( e.hslIndex, a.hslIndex );
			assertEquals( e.increase, a.increase );
			assertEquals( e.amount, a.amount );
			assertEquals( e.relative, a.relative );
			assertEquals( e.autoInverse, a.autoInverse );
		} else if( expected instanceof HSLChange ) {
			HSLChange e = (HSLChange) expected;
			HSLChange a = (HSLChange) actual;
			assertEquals( e.hslIndex, a.hslIndex );
			assertEquals( e.value, a.value );
		} else if( expected instanceof Fade ) {
			Fade e = (Fade) expected;
			Fade a = (Fade) actual;
			assertEquals( e.amount, a.amount );
		} else if( expected instanceof Mix ) {
			Mix e = (Mix) expected;
			Mix a = (Mix) actual;
			assertEquals( e.color2, a.color2 );
			assertEquals( e.weight, a.weight );
		} else if( expected instanceof Mix2 ) {
			Mix2 e = (Mix2) expected;
			Mix2 a = (Mix2) actual;
			assertEquals( e.color1, a.color1 );
			assertEquals( e.weight, a.weight );
		} else
			assertTrue( false );
	}

	private Object parseColor( String value ) {
		return UIDefaultsLoader.parseValue( "dummyColor", value, null );
	}

	private Object parseColorLazy( String value, Color actual ) {
		UIManager.put( "dummyColor", actual );
		Object v = UIDefaultsLoader.parseValue( "dummyColor", value, null );
		assertInstanceOf( LazyValue.class, v );
		return ((LazyValue)v).createValue( null );
	}

	//---- invalid values -----------------------------------------------------

	@Test
	void parseInvalidValue() {
		assertThrows( new IllegalArgumentException( "invalid character 'abc'" ), () -> UIDefaultsLoader.parseValue( "dummyChar", "abc", null ) );
		assertThrows( new NumberFormatException( "invalid integer or float '123abc'" ), () -> UIDefaultsLoader.parseValue( "dummyWidth", "123abc", null ) );
		assertThrows( new NumberFormatException( "invalid integer or float '1.23abc'" ), () -> UIDefaultsLoader.parseValue( "dummyWidth", "1.23abc", null ) );

		assertThrows( new IllegalArgumentException( "invalid insets '1,abc,3,4'" ), () -> UIDefaultsLoader.parseValue( "dummyInsets", "1,abc,3,4", null ) );
		assertThrows( new IllegalArgumentException( "invalid insets '1,2,3'" ), () -> UIDefaultsLoader.parseValue( "dummyInsets", "1,2,3", null ) );
		assertThrows( new IllegalArgumentException( "invalid size '1abc'" ), () -> UIDefaultsLoader.parseValue( "dummySize", "1abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid size '1'" ), () -> UIDefaultsLoader.parseValue( "dummySize", "1", null ) );
		assertThrows( new IllegalArgumentException( "invalid color '#f0'" ), () -> UIDefaultsLoader.parseValue( "dummy", "#f0", null ) );
		assertThrows( new IllegalArgumentException( "invalid color '#f0'" ), () -> UIDefaultsLoader.parseValue( "dummyColor", "#f0", null ) );
	}

	@Test
	void parseInvalidValueWithJavaType() {
		assertThrows( new IllegalArgumentException( "invalid boolean 'falseyy'" ), () -> UIDefaultsLoader.parseValue( "dummy", "falseyy", boolean.class ) );
		assertThrows( new IllegalArgumentException( "invalid boolean 'falseyy'" ), () -> UIDefaultsLoader.parseValue( "dummy", "falseyy", Boolean.class ) );

		assertThrows( new IllegalArgumentException( "invalid character 'abc'" ), () -> UIDefaultsLoader.parseValue( "dummyChar", "abc", char.class ) );
		assertThrows( new IllegalArgumentException( "invalid character 'abc'" ), () -> UIDefaultsLoader.parseValue( "dummyChar", "abc", Character.class ) );
		assertThrows( new NumberFormatException( "invalid integer '123abc'" ), () -> UIDefaultsLoader.parseValue( "dummyWidth", "123abc", int.class ) );
		assertThrows( new NumberFormatException( "invalid integer '123abc'" ), () -> UIDefaultsLoader.parseValue( "dummyWidth", "123abc", Integer.class ) );
		assertThrows( new NumberFormatException( "invalid float '1.23abc'" ), () -> UIDefaultsLoader.parseValue( "dummyWidth", "1.23abc", float.class ) );
		assertThrows( new NumberFormatException( "invalid float '1.23abc'" ), () -> UIDefaultsLoader.parseValue( "dummyWidth", "1.23abc", Float.class ) );

		assertThrows( new IllegalArgumentException( "invalid insets '1,abc,3'" ), () -> UIDefaultsLoader.parseValue( "dummyInsets", "1,abc,3", Insets.class ) );
		assertThrows( new IllegalArgumentException( "invalid insets '1,2,3'" ), () -> UIDefaultsLoader.parseValue( "dummyInsets", "1,2,3", Insets.class ) );
		assertThrows( new IllegalArgumentException( "invalid size '1abc'" ), () -> UIDefaultsLoader.parseValue( "dummySize", "1abc", Dimension.class ) );
		assertThrows( new IllegalArgumentException( "invalid size '1'" ), () -> UIDefaultsLoader.parseValue( "dummySize", "1", Dimension.class ) );
		assertThrows( new IllegalArgumentException( "invalid color '#f0'" ), () -> UIDefaultsLoader.parseValue( "dummy", "#f0", Color.class ) );
		assertThrows( new IllegalArgumentException( "invalid color '#f0'" ), () -> UIDefaultsLoader.parseValue( "dummyColor", "#f0", Color.class ) );
	}

	@Test
	void parseInvalidBorders() {
		assertThrows( new IllegalArgumentException( "invalid border '1,abc,3,4' (invalid insets '1,abc,3,4')" ), () -> UIDefaultsLoader.parseValue( "dummyBorder", "1,abc,3,4", null ) );
		assertThrows( new IllegalArgumentException( "invalid border '1,2,3' (invalid insets '1,2,3')" ), () -> UIDefaultsLoader.parseValue( "dummyBorder", "1,2,3", null ) );
		assertThrows( new IllegalArgumentException( "invalid border '1,2,3,,,' (invalid insets '1,2,3,,,')" ), () -> UIDefaultsLoader.parseValue( "dummyBorder", "1,2,3,,,", null ) );
		assertThrows( new IllegalArgumentException( "invalid border '1,2,3,4,#f0' (invalid color '#f0')" ), () -> UIDefaultsLoader.parseValue( "dummyBorder", "1,2,3,4,#f0", null ) );
		assertThrows( new IllegalArgumentException( "invalid border '1,2,3,4,#f00,2.5abc' (invalid float '2.5abc')" ), () -> UIDefaultsLoader.parseValue( "dummyBorder", "1,2,3,4,#f00,2.5abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid border '1,2,3,4,#f00,2.5,6abc' (invalid integer '6abc')" ), () -> UIDefaultsLoader.parseValue( "dummyBorder", "1,2,3,4,#f00,2.5,6abc", null ) );
	}

	@Test
	void parseInvalidFonts() {
		// size
		assertThrows( new IllegalArgumentException( "invalid font '12abc' (invalid integer '12abc')" ),    () -> UIDefaultsLoader.parseValue( "dummyFont", "12abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid font '+12abc' (invalid integer '+12abc')" ),  () -> UIDefaultsLoader.parseValue( "dummyFont", "+12abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid font '+3abc' (invalid integer '+3abc')" ),    () -> UIDefaultsLoader.parseValue( "dummyFont", "+3abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid font '-4abc' (invalid integer '-4abc')" ),    () -> UIDefaultsLoader.parseValue( "dummyFont", "-4abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid font '150abc%' (invalid integer '150abc')" ), () -> UIDefaultsLoader.parseValue( "dummyFont", "150abc%", null ) );
		assertThrows( new IllegalArgumentException( "invalid font 'bold 13abc Monospaced' (invalid integer '13abc')" ), () -> UIDefaultsLoader.parseValue( "dummyFont", "bold 13abc Monospaced", null ) );

		// invalid combinations of styles
		assertThrows( new IllegalArgumentException( "invalid font 'bold +italic': can not mix absolute style (e.g. 'bold') with derived style (e.g. '+italic')" ), () -> UIDefaultsLoader.parseValue( "dummyFont", "bold +italic", null ) );
		assertThrows( new IllegalArgumentException( "invalid font '+bold -bold': can not use '+bold' and '-bold'" ), () -> UIDefaultsLoader.parseValue( "dummyFont", "+bold -bold", null ) );
		assertThrows( new IllegalArgumentException( "invalid font '+italic -italic': can not use '+italic' and '-italic'" ), () -> UIDefaultsLoader.parseValue( "dummyFont", "+italic -italic", null ) );
	}

	private void assertThrows( Throwable expected, Executable executable ) {
		Throwable actual = assertThrowsExactly( expected.getClass(), executable );
		assertEquals( expected.getMessage(), actual.getMessage() );
	}

	//---- class TestInstance -------------------------------------------------

	@SuppressWarnings( "EqualsHashCode" ) // Error Prone
	public static class TestInstance
	{
		private String s;
		private boolean b;
		private int i;
		private float f;
		private Color color;

		public TestInstance() {
		}

		public TestInstance( String s ) {
			this.s = s;
		}

		public TestInstance( boolean b ) {
			this.b = b;
		}

		public TestInstance( int i ) {
			this.i = i;
		}

		public TestInstance( float f ) {
			this.f = f;
		}

		public TestInstance( Color color ) {
			this.color = color;
		}

		public TestInstance( String s, boolean b ) {
			this.s = s;
			this.b = b;
		}

		public TestInstance( String s, boolean b, int i ) {
			this.s = s;
			this.b = b;
			this.i = i;
		}

		public TestInstance( String s, int i, boolean b ) {
			this.s = s;
			this.b = b;
			this.i = i;
		}

		public TestInstance( String s, float f, boolean b ) {
			this.s = s;
			this.b = b;
			this.f = f;
		}

		protected TestInstance( int i, String s ) {
			this.s = s;
			this.i = i;
		}

		@Override
		public boolean equals( Object obj ) {
			if( !(obj instanceof TestInstance) )
				return false;

			TestInstance inst = (TestInstance) obj;
			return Objects.equals( s, inst.s ) &&
				b == inst.b &&
				i == inst.i &&
				f == inst.f &&
				Objects.equals( color, inst.color );
		}
	}
}
