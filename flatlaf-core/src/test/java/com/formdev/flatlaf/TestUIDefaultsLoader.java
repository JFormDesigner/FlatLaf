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
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;

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
