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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.formdev.flatlaf.ui.TestUtils.assertLogged;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.BorderUIResource.CompoundBorderUIResource;
import javax.swing.UIDefaults.ActiveValue;
import javax.swing.UIDefaults.LazyValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.ui.FlatMatteBorder;
import com.formdev.flatlaf.ui.TestUtils;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.GrayFilter;
import com.formdev.flatlaf.util.UIScale;
import com.formdev.flatlaf.util.ColorFunctions;
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
	//---- logging ------------------------------------------------------------

	@BeforeAll
	static void setup() {
		TestUtils.setupLogCapture();
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanupLogCapture();
	}

	@BeforeEach
	void beforeEach() {
		TestUtils.clearLogCapture();
	}

	@AfterEach
	void afterEach() {
		TestUtils.assertNoUnexpectedLog();
	}

	//---- general ----------------------------------------------------------

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

		assertEquals( 'a', UIDefaultsLoader.parseValue( "dummy", "a", char.class ) );
		assertEquals( 'a', UIDefaultsLoader.parseValue( "dummy", "a", Character.class ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummy", "123", int.class ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummy", "123", Integer.class ) );
		assertEquals( 1.23f, UIDefaultsLoader.parseValue( "dummy", "1.23", float.class ) );
		assertEquals( 1.23f, UIDefaultsLoader.parseValue( "dummy", "1.23", Float.class ) );

		assertEquals( new Insets( 1,2,3,4 ), UIDefaultsLoader.parseValue( "dummy", "1,2,3,4", Insets.class ) );
		assertEquals( new Dimension( 1,2 ), UIDefaultsLoader.parseValue( "dummy", "1,2", Dimension.class ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "dummy", "#f00", Color.class ) );

		Object font = UIDefaultsLoader.parseValue( "dummy", "bold", Font.class );
		assertInstanceOf( ActiveValue.class, font );
		assertInstanceOf( Font.class, createValue( font ) );

		Object border = UIDefaultsLoader.parseValue( "dummy", "1,2,3,4", Border.class );
		assertInstanceOf( FlatEmptyBorder.class, createValue( border ) );

		Object icon = UIDefaultsLoader.parseValue( "dummy", TestInstance.class.getName(), javax.swing.Icon.class );
		assertEquals( new TestInstance(), createValue( icon ) );

		// type of key is ignored if java type is given
		assertEquals( "#f00", UIDefaultsLoader.parseValue( "dummyColor", "#f00", String.class ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummySize", "123", int.class ) );

		assertThrows( new IllegalArgumentException( "unsupported value type 'java.lang.Object'" ),
			() -> UIDefaultsLoader.parseValue( "dummy", "123", Object.class ) );
	}

	@Test
	void parseValueTypeFromKey() {
		// string
		assertEquals( "123", UIDefaultsLoader.parseValue( "ButtonUI", "123", null ) );
		assertEquals( "com.example.MyButtonUI", UIDefaultsLoader.parseValue( "ButtonUI", "com.example.MyButtonUI", null ) );

		// color
		assertThrows( new IllegalArgumentException( "invalid color 'abc'" ), () -> UIDefaultsLoader.parseValue( "Button.background", "abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid color 'abc'" ), () -> UIDefaultsLoader.parseValue( "Button.foreground", "abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid color 'abc'" ), () -> UIDefaultsLoader.parseValue( "Button.hoverBackground", "abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid color 'abc'" ), () -> UIDefaultsLoader.parseValue( "Button.disabledForeground", "abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid color 'abc'" ), () -> UIDefaultsLoader.parseValue( "background", "abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid color 'abc'" ), () -> UIDefaultsLoader.parseValue( "foreground", "abc", null ) );
		assertEquals( "abc", UIDefaultsLoader.parseValue( "Button.playground", "abc", null ) );

		// insets
		Insets insets = new Insets( 1,2,3,4 );
		assertEquals( insets, UIDefaultsLoader.parseValue( "Button.margin", "1,2,3,4", null ) );
		assertEquals( insets, UIDefaultsLoader.parseValue( "margin", "1,2,3,4", null ) );
		assertEquals( insets, UIDefaultsLoader.parseValue( "Button.padding", "1,2,3,4", null ) );
		assertEquals( insets, UIDefaultsLoader.parseValue( "padding", "1,2,3,4", null ) );
		assertEquals( insets, UIDefaultsLoader.parseValue( "Button.tabMargins", "1,2,3,4", null ) );
		assertEquals( insets, UIDefaultsLoader.parseValue( "Button.tabInsets", "1,2,3,4", null ) );

		// integer or float
		assertEquals( 12, UIDefaultsLoader.parseValue( "Button.minimumHeight", "12", null ) );
		assertEquals( 1.5f, UIDefaultsLoader.parseValue( "Button.minimumHeight", "1.5", null ) );

		// gray filter
		GrayFilter grayFilter = (GrayFilter) createValue( UIDefaultsLoader.parseValue( "Component.grayFilter", "25,-25,100", null ) );
		assertEquals( 25, grayFilter.getBrightness() );
		assertEquals( -25, grayFilter.getContrast() );
		assertEquals( 100, grayFilter.getAlpha() );

		// styles are not parsed
		assertEquals( "foreground: #f00; arc: 5", UIDefaultsLoader.parseValue( "[style]Button.dummy", "foreground: #f00; arc: 5", null ) );
		assertEquals( "  #f00  ", UIDefaultsLoader.parseValue( "[style]Button.dummy", "  #f00  ", null ) );
	}

	@Test
	void parseValueKnownTypes() {
		// key ends with "Border", but is a color
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "activeCaptionBorder", "#f00", null ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "inactiveCaptionBorder", "#f00", null ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "windowBorder", "#f00", null ) );

		// key ends with "Size", but is an integer
		assertEquals( 5, UIDefaultsLoader.parseValue( "SplitPane.dividerSize", "5", null ) );
		assertEquals( 5, UIDefaultsLoader.parseValue( "SplitPaneDivider.gripDotSize", "5", null ) );
		assertEquals( 5, UIDefaultsLoader.parseValue( "dividerSize", "5", null ) );
		assertEquals( 5, UIDefaultsLoader.parseValue( "gripDotSize", "5", null ) );

		// key ends with "Size", but is a float
		assertEquals( 7.5f, UIDefaultsLoader.parseValue( "TabbedPane.closeCrossPlainSize", "7.5", null ) );
		assertEquals( 7.5f, UIDefaultsLoader.parseValue( "TabbedPane.closeCrossFilledSize", "7.5", null ) );
		assertEquals( 7.5f, UIDefaultsLoader.parseValue( "closeCrossPlainSize", "7.5", null ) );
		assertEquals( 7.5f, UIDefaultsLoader.parseValue( "closeCrossFilledSize", "7.5", null ) );

		// key does not end with "Size", but is a dimension
		assertEquals( new Dimension( 1,2 ), UIDefaultsLoader.parseValue( "Table.intercellSpacing", "1,2", null ) );
		assertEquals( new Dimension( 1,2 ), UIDefaultsLoader.parseValue( "intercellSpacing", "1,2", null ) );
	}

	@Test
	void parseValueUnknownType() {
		assertEquals( null, UIDefaultsLoader.parseValue( "dummy", "", null ) );
		assertEquals( null, UIDefaultsLoader.parseValue( "dummy", "  ", null ) );
		assertEquals( null, UIDefaultsLoader.parseValue( "dummy", " null ", null ) );
		assertEquals( true, UIDefaultsLoader.parseValue( "dummy", " true ", null ) );

		// integer
		assertEquals( 3, UIDefaultsLoader.parseValue( "dummy", "+3", null ) );
		assertEquals( -1, UIDefaultsLoader.parseValue( "dummy", "-1", null ) );

		// float
		assertEquals( 0.5f, UIDefaultsLoader.parseValue( "dummy", ".5", null ) );
		assertEquals( -1.5f, UIDefaultsLoader.parseValue( "dummy", "-1.5", null ) );

		// string
		assertEquals( "1abc", UIDefaultsLoader.parseValue( "dummy", "1abc", null ) );
		assertEquals( "1,2", UIDefaultsLoader.parseValue( "dummy", "1,2", null ) );
		assertEquals( " hello ", UIDefaultsLoader.parseValue( "dummy", "\" hello \"", null ) );
	}

	@Test
	void parseTypePrefix() {
		assertEquals( "123", UIDefaultsLoader.parseValue( "dummy", "{string}123", null ) );
		assertEquals( "#f00", UIDefaultsLoader.parseValue( "dummyColor", "{string}#f00", null ) );
		assertEquals( true, UIDefaultsLoader.parseValue( "dummy", "{boolean}true", null ) );
		assertEquals( 'a', UIDefaultsLoader.parseValue( "dummy", "{character}a", null ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummy", "{integer}123", null ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummy", "{Integer}123", null ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummyWidth", "{integerOrFloat}123", null ) );
		assertEquals( 1f, UIDefaultsLoader.parseValue( "dummy", "{float}1", null ) );
		assertEquals( new Insets( 1,2,3,4 ), UIDefaultsLoader.parseValue( "dummy", "{insets}1,2,3,4", null ) );
		assertEquals( new Dimension( 1,2 ), UIDefaultsLoader.parseValue( "dummy", "{dimension}1,2", null ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "dummy", "{color}#f00", null ) );
		assertInstanceOf( FlatEmptyBorder.class, createValue( UIDefaultsLoader.parseValue( "dummy", "{border}1,2,3,4", null ) ) );
		assertInstanceOf( ActiveValue.class, UIDefaultsLoader.parseValue( "dummy", "{font}bold", null ) );

		// scaled values
		assertEquals( UIScale.scale( 10 ), createValue( UIDefaultsLoader.parseValue( "dummy", "{scaledInteger}10", null ) ) );
		assertEquals( UIScale.scale( 1.5f ), createValue( UIDefaultsLoader.parseValue( "dummy", "{scaledFloat}1.5", null ) ) );
		assertEquals( UIScale.scale( new Insets( 1,2,3,4 ) ), createValue( UIDefaultsLoader.parseValue( "dummy", "{scaledInsets}1,2,3,4", null ) ) );
		assertEquals( UIScale.scale( new Dimension( 1,2 ) ), createValue( UIDefaultsLoader.parseValue( "dummy", "{scaledDimension}1,2", null ) ) );

		// instance and class
		String className = TestInstance.class.getName();
		assertEquals( new TestInstance(), createValue( UIDefaultsLoader.parseValue( "dummy", "{instance}" + className, null ) ) );
		assertEquals( new TestInstance( 123 ), createValue( UIDefaultsLoader.parseValue( "dummy", "{instance}" + className + "(123)", null ) ) );
		assertEquals( TestInstance.class, createValue( UIDefaultsLoader.parseValue( "dummy", "{class}" + className, null ) ) );
		assertEquals( null, createValue( UIDefaultsLoader.parseValue( "dummy", "{class}com.example.NotExisting", null ) ) );
		assertLogged( "FlatLaf: Failed to find class 'com.example.NotExisting'." );

		// gray filter
		GrayFilter grayFilter = (GrayFilter) createValue( UIDefaultsLoader.parseValue( "dummy", "{grayFilter}10,20,30", null ) );
		assertEquals( 10, grayFilter.getBrightness() );
		assertEquals( 20, grayFilter.getContrast() );
		assertEquals( 30, grayFilter.getAlpha() );

		// unknown type is not removed from value
		assertEquals( "{foo}123", UIDefaultsLoader.parseValue( "dummy", "{foo}123", null ) );
		assertEquals( "{integer123", UIDefaultsLoader.parseValue( "dummy", "{integer123", null ) );
	}

	private static Object createValue( Object value ) {
		if( value instanceof LazyValue )
			return ((LazyValue)value).createValue( null );
		if( value instanceof ActiveValue )
			return ((ActiveValue)value).createValue( null );
		throw new IllegalArgumentException( "not a LazyValue or ActiveValue: " + value );
	}

	/**
	 * Parses value with resolving of variables and property references
	 * (same as in UIDefaultsLoader.loadDefaultsFromProperties()).
	 */
	private static Object parseValue( String key, String value, Map<String, String> props ) {
		Function<String, String> propertiesGetter = props::get;
		Function<String, String> resolver = v -> UIDefaultsLoader.resolveValue( v, propertiesGetter );
		return UIDefaultsLoader.parseValue( key, resolver.apply( value ), null, null, resolver, Collections.emptyList() );
	}

	//---- borders ------------------------------------------------------------

	@Test
	void parseBorders() {
		Insets insets = new Insets( 1,2,3,4 );
		assertBorderEquals( new FlatEmptyBorder( insets ), "1,2,3,4" );
		assertBorderEquals( new FlatEmptyBorder( insets ), "1,2,3,4,,," );
		assertBorderEquals( new FlatLineBorder( insets, Color.red ), "1,2,3,4,#f00" );
		assertBorderEquals( new FlatLineBorder( insets, Color.red, 2.5f, -1 ), "1,2,3,4,#f00,2.5" );
		assertBorderEquals( new FlatLineBorder( insets, Color.red, 2.5f, 6 ), "1,2,3,4,#f00,2.5,6" );
		assertBorderEquals( new FlatLineBorder( insets, Color.red, 1, 6 ), "1,2,3,4,#f00,,6" );
		assertBorderEquals( new FlatLineBorder( insets, null, 0, 6 ), "1,2,3,4,,,6" );
		assertBorderEquals( new FlatLineBorder( insets, new Color( 0x800000 ), 1, -1 ), "1,2,3,4,darken(#f00,25%)" );

		// instances with parameters
		assertBorderEquals( new EmptyBorder( insets ), "javax.swing.border.EmptyBorder(1,2,3,4)" );
		assertBorderEquals( new EmptyBorder( insets ), "javax.swing.border.EmptyBorder,1,2,3,4" );
		assertBorderEquals( new LineBorder( Color.red, 2, true ), "javax.swing.border.LineBorder(#f00,2,true)" );
		assertBorderEquals( new LineBorder( Color.red, 2, true ), "javax.swing.border.LineBorder,#f00,2,true" );
		assertBorderEquals( new MatteBorder( insets, Color.red ), "javax.swing.border.MatteBorder(1,2,3,4,#f00)" );
		assertBorderEquals( new MatteBorder( insets, Color.red ), "javax.swing.border.MatteBorder,1,2,3,4,#f00" );
	}

	@Test
	void parseBorderFunctions() {
		Insets insets = new Insets( 1,2,3,4 );

		// empty border
		assertBorderEquals( new FlatEmptyBorder( insets ), "empty(1,2,3,4)" );
		assertBorderEquals( new FlatEmptyBorder( insets ), "empty( 1, 2, 3, 4 )" );

		// line border
		Insets insets0 = new Insets( 0,0,0,0 );
		Insets insets1 = new Insets( 1,1,1,1 );
		Insets insets3 = new Insets( 3,3,3,3 );
		assertBorderEquals( new FlatLineBorder( insets1, Color.red ), "line(#f00)" );
		assertBorderEquals( new FlatLineBorder( insets3, Color.red, 2.5f, -1 ), "line(#f00,2.5)" );
		assertBorderEquals( new FlatLineBorder( insets3, Color.red, 2.5f, 6 ), "line(#f00,2.5,6)" );
		assertBorderEquals( new FlatLineBorder( insets1, Color.red, 1, 6 ), "line(#f00,,6)" );
		assertBorderEquals( new FlatLineBorder( insets0, null, 0, 6 ), "line(,,6)" );
		assertBorderEquals( new FlatLineBorder( insets1, new Color( 0x800000 ), 1, -1 ), "line(darken(#f00,25%))" );

		// line border with insets
		assertBorderEquals( new FlatLineBorder( insets, Color.red ), "line(1,2,3,4,#f00)" );
		assertBorderEquals( new FlatLineBorder( insets, Color.red, 2.5f, -1 ), "line(1,2,3,4,#f00,2.5)" );
		assertBorderEquals( new FlatLineBorder( insets, Color.red, 2.5f, 6 ), "line( 1, 2, 3, 4, #f00, 2.5, 6 )" );
		assertBorderEquals( new FlatLineBorder( insets, Color.red, 1, 6 ), "line(1,2,3,4,#f00,,6)" );
		assertBorderEquals( new FlatLineBorder( insets, null, 0, 6 ), "line(1,2,3,4,,,6)" );
		assertBorderEquals( new FlatLineBorder( insets, new Color( 0x800000 ), 1, -1 ), "line(1,2,3,4,darken(#f00,25%))" );

		// matte border
		assertBorderEquals( new FlatMatteBorder( insets, Color.red ), "matte(1,2,3,4,#f00)" );
		assertBorderEquals( new FlatMatteBorder( new Insets( 1,0,0,0 ), Color.red ), "matte(1,0,0,0,#f00)" );

		// compound border
		assertBorderEquals( new CompoundBorderUIResource(
				new FlatLineBorder( insets1, Color.red ),
				new FlatEmptyBorder( insets ) ),
			"line(#f00), empty(1,2,3,4)" );
		assertBorderEquals( new CompoundBorderUIResource(
				new FlatMatteBorder( new Insets( 0,0,1,0 ), Color.red ),
				new CompoundBorderUIResource(
					new FlatEmptyBorder( insets ),
					new FlatLineBorder( insets3, Color.blue, 2.5f, 6 ) ) ),
			"matte(0,0,1,0,#f00), empty( 1, 2, 3, 4 ), line(darken(#00f,0%), 2.5, 6)" );
	}

	private void assertBorderEquals( Border expected, String actualStyle ) {
		Border actual = (Border) parseBorder( actualStyle );
		assertBorderEquals( expected, actual );
	}

	private void assertBorderEquals( Border expected, Border actual ) {
		assertEquals( expected.getClass(), actual.getClass() );
		if( expected instanceof FlatEmptyBorder )
			assertEquals( ((FlatEmptyBorder)expected).getBorderInsets(), ((FlatEmptyBorder)actual).getBorderInsets() );
		if( expected instanceof FlatLineBorder ) {
			FlatLineBorder e = (FlatLineBorder) expected;
			FlatLineBorder a = (FlatLineBorder) actual;
			assertEquals( e.getLineColor(), a.getLineColor() );
			assertEquals( e.getLineThickness(), a.getLineThickness() );
			assertEquals( e.getArc(), a.getArc() );
		}
		if( expected instanceof FlatMatteBorder )
			assertEquals( ((FlatMatteBorder)expected).getMatteColor(), ((FlatMatteBorder)actual).getMatteColor() );
		if( expected instanceof CompoundBorder ) {
			CompoundBorder e = (CompoundBorder) expected;
			CompoundBorder a = (CompoundBorder) actual;
			assertBorderEquals( e.getOutsideBorder(), a.getOutsideBorder() );
			assertBorderEquals( e.getInsideBorder(), a.getInsideBorder() );
		}
	}

	private static Object parseBorder( String value ) {
		Object v = UIDefaultsLoader.parseValue( "dummyBorder", value, null );
		assertInstanceOf( LazyValue.class, v );
		return ((LazyValue)v).createValue( null );
	}

	//---- fonts --------------------------------------------------------------

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

	@Test
	void parseFontsWithBaseFont() {
		UIManager.put( "defaultFont", new Font( Font.DIALOG, Font.PLAIN, 10 ) );
		UIManager.put( "dummyBaseFont", new Font( Font.MONOSPACED, Font.BOLD, 20 ) );
		try {
			assertFontEquals( Font.MONOSPACED, Font.BOLD, 20, "$dummyBaseFont" );
			assertFontEquals( Font.MONOSPACED, Font.BOLD, 22, "+2 $dummyBaseFont" );
			assertFontEquals( Font.MONOSPACED, Font.PLAIN, 20, "-bold $dummyBaseFont" );
			assertFontEquals( Font.MONOSPACED, Font.ITALIC, 30, "italic 150% $dummyBaseFont" );
			assertFontEquals( Font.DIALOG, Font.BOLD, 20, "Dialog $dummyBaseFont" );

			// not existing base font --> defaultFont
			assertFontEquals( Font.DIALOG, Font.BOLD, 10, "bold $?dummyNotExisting" );

			// quoted family
			assertFontEquals( Font.MONOSPACED, Font.PLAIN, 10, "\"Monospaced\"" );
			assertFontEquals( Font.MONOSPACED, Font.PLAIN, 10, "'Monospaced'" );
		} finally {
			UIManager.put( "defaultFont", null );
			UIManager.put( "dummyBaseFont", null );
		}
	}

	private void assertFontEquals( String name, int style, int size, String actualStyle ) {
		assertEquals(
			new Font( name, style, size ),
			((ActiveValue)UIDefaultsLoader.parseValue( "dummyFont", actualStyle, null )).createValue( null ) );
	}

	//---- instance -----------------------------------------------------------

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

		// color function as parameter
		assertEquals( new TestInstance( new Color( 0x800000 ) ),
			createValue( UIDefaultsLoader.parseValue( "dummyIcon", className + "(darken(#f00,25%))", null ) ) );

		// no matching constructor
		assertEquals( null, createValue( UIDefaultsLoader.parseValue( "dummyIcon", className + "(1,2,3,4,5)", null ) ) );
		assertLogged( "FlatLaf: Failed to instantiate '" + className + "': no constructor found for parameters '1,2,3,4,5'." );
		assertEquals( null, createValue( UIDefaultsLoader.parseValue( "dummyIcon", className + ",1,2,3,4,5", null ) ) );
		assertLogged( "FlatLaf: Failed to instantiate '" + className + "': no constructor found for parameters '1,2,3,4,5'." );

		// class not found
		assertEquals( null, createValue( UIDefaultsLoader.parseValue( "dummyIcon", "com.example.NotExisting", null ) ) );
		assertLogged( "FlatLaf: Failed to instantiate 'com.example.NotExisting'." );
	}

	private void assertInstanceEquals( TestInstance expected, String params ) {
		String value = TestInstance.class.getName() + (params != null ? "(" + params + ")" : "()");
		assertEquals( expected, ((LazyValue)UIDefaultsLoader.parseValue( "dummyIcon", value, null )).createValue( null ) );

		value = TestInstance.class.getName() + (params != null ? "," + params : "");
		assertEquals( expected, ((LazyValue)UIDefaultsLoader.parseValue( "dummyIcon", value, null )).createValue( null ) );
	}

	//---- colors -------------------------------------------------------------

	@Test
	void parseHexColors() {
		assertEquals( new Color( 0xff0000 ), parseColor( "#f00" ) );
		assertEquals( new Color( 0xff0000 ), parseColor( "#F00" ) );
		assertEquals( new Color( 0xaabbcc ), parseColor( "#abc" ) );
		assertEquals( new Color( 0xaabbcc ), parseColor( "#ABC" ) );
		assertEquals( new Color( 0x88ff0000, true ), parseColor( "#f008" ) );
		assertEquals( new Color( 0x123456 ), parseColor( "#123456" ) );
		assertEquals( new Color( 0xabcdef ), parseColor( "#ABCDEF" ) );
		assertEquals( new Color( 0x80123456, true ), parseColor( "#12345680" ) );
		assertEquals( new Color( 0x00123456, true ), parseColor( "#12345600" ) );
		assertEquals( new Color( 0x123456 ), parseColor( "#123456ff" ) );

		assertEquals( 0xffaabbcc, UIDefaultsLoader.parseColorRGBA( "#abc" ) );
		assertEquals( 0xddaabbcc, UIDefaultsLoader.parseColorRGBA( "#abcd" ) );
		assertEquals( 0xff123456, UIDefaultsLoader.parseColorRGBA( "#123456" ) );
		assertEquals( 0x78123456, UIDefaultsLoader.parseColorRGBA( "#12345678" ) );
	}

	@Test
	void parseColorFunctions() {
		// lighten
		assertEquals( new Color( 0xff6666 ), parseColor( "lighten(#f00, 20%)" ) );
		assertEquals( new Color( 0xff3333 ), parseColor( "lighten(#f00, 20%, relative)" ) );
		assertEquals( new Color( 0xaaaaaa ), parseColor( "lighten(#ddd, 20%, autoInverse)" ) );
		assertEquals( new Color( 0xb1b1b1 ), parseColor( "lighten(#ddd, 20%, relative autoInverse)" ) );
		assertEquals( null,                  parseColor( "lighten(systemColor(dummyNotExisting), 20%)" ) );

		// darken
		assertEquals( new Color( 0x990000 ), parseColor( "darken(#f00, 20%)" ) );
		assertEquals( new Color( 0xcc0000 ), parseColor( "darken(#f00, 20%, relative)" ) );
		assertEquals( new Color( 0x555555 ), parseColor( "darken(#222, 20%, autoInverse)" ) );
		assertEquals( new Color( 0x292929 ), parseColor( "darken(#222, 20%, relative autoInverse)" ) );
		assertEquals( null,                  parseColor( "darken(systemColor(dummyNotExisting), 20%)" ) );

		// saturate
		assertEquals( new Color( 0xf32e2e ), parseColor( "saturate(#d44, 20%)" ) );
		assertEquals( new Color( 0xec3535 ), parseColor( "saturate(#d44, 20%, relative)" ) );
		assertEquals( new Color( 0xc75a5a ), parseColor( "saturate(#d44, 20%, autoInverse)" ) );
		assertEquals( new Color( 0xce5353 ), parseColor( "saturate(#d44, 20%, relative autoInverse)" ) );
		assertEquals( null,                  parseColor( "saturate(systemColor(dummyNotExisting), 20%)" ) );

		// desaturate
		assertEquals( new Color( 0x745858 ), parseColor( "desaturate(#844, 20%)" ) );
		assertEquals( new Color( 0x814b4b ), parseColor( "desaturate(#844, 20%, relative)" ) );
		assertEquals( new Color( 0x9c3030 ), parseColor( "desaturate(#844, 20%, autoInverse)" ) );
		assertEquals( new Color( 0x8f3d3d ), parseColor( "desaturate(#844, 20%, relative autoInverse)" ) );
		assertEquals( null,                  parseColor( "desaturate(systemColor(dummyNotExisting), 20%)" ) );

		// fadein
		assertEquals( new Color( 0xddff0000, true ), parseColor( "fadein(#f00a, 20%)" ) );
		assertEquals( new Color( 0xccff0000, true ), parseColor( "fadein(#f00a, 20%, relative)" ) );
		assertEquals( new Color( 0x77ff0000, true ), parseColor( "fadein(#f00a, 20%, autoInverse)" ) );
		assertEquals( new Color( 0x88ff0000, true ), parseColor( "fadein(#f00a, 20%, relative autoInverse)" ) );
		assertEquals( null,                          parseColor( "fadein(systemColor(dummyNotExisting), 20%)" ) );

		// fadeout
		assertEquals( new Color( 0x11ff0000, true ), parseColor( "fadeout(#f004, 20%)" ) );
		assertEquals( new Color( 0x36ff0000, true ), parseColor( "fadeout(#f004, 20%, relative)" ) );
		assertEquals( new Color( 0x77ff0000, true ), parseColor( "fadeout(#f004, 20%, autoInverse)" ) );
		assertEquals( new Color( 0x52ff0000, true ), parseColor( "fadeout(#f004, 20%, relative autoInverse)" ) );
		assertEquals( null,                          parseColor( "fadeout(systemColor(dummyNotExisting), 20%)" ) );

		// fade
		assertEquals( new Color( 0x33ff0000, true ), parseColor( "fade(#f00, 20%)" ) );
		assertEquals( new Color( 0xccff0000, true ), parseColor( "fade(#ff000010, 80%)" ) );
		assertEquals( null,                          parseColor( "fade(systemColor(dummyNotExisting), 20%)" ) );

		// spin
		assertEquals( new Color( 0xffaa00 ), parseColor( "spin(#f00, 40)" ) );
		assertEquals( new Color( 0xff00aa ), parseColor( "spin(#f00, -40)" ) );
		assertEquals( null,                  parseColor( "spin(systemColor(dummyNotExisting), 40)" ) );

		// changeHue / changeSaturation / changeLightness / changeAlpha
		assertEquals( new Color( 0xffaa00 ), parseColor( "changeHue(#f00, 40)" ) );
		assertEquals( new Color( 0xb34d4d ), parseColor( "changeSaturation(#f00, 40%)" ) );
		assertEquals( new Color( 0xcc0000 ), parseColor( "changeLightness(#f00, 40%)" ) );
		assertEquals( new Color( 0x66ff0000, true ), parseColor( "changeAlpha(#f00, 40%)" ) );
		assertEquals( null,                  parseColor( "changeHue(systemColor(dummyNotExisting), 40)" ) );
		assertEquals( null,                  parseColor( "changeSaturation(systemColor(dummyNotExisting), 40%)" ) );
		assertEquals( null,                  parseColor( "changeLightness(systemColor(dummyNotExisting), 40%)" ) );
		assertEquals( null,                  parseColor( "changeAlpha(systemColor(dummyNotExisting), 40%)" ) );

		// mix
		assertEquals( new Color( 0x808000 ), parseColor( "mix(#f00, #0f0)" ) );
		assertEquals( new Color( 0xbf4000 ), parseColor( "mix(#f00, #0f0, 75%)" ) );
		assertEquals( new Color( 0x808000 ), parseColor( "mix(#f00, #0f0, rgb)" ) );
		assertEquals( new Color( 0xbcbc00 ), parseColor( "mix(#f00, #0f0, lrgb)" ) );
		assertEquals( new Color( 0xd0a800 ), parseColor( "mix(#f00, #0f0, oklab)" ) );
		assertEquals( new Color( 0xbf4000 ), parseColor( "mix(#f00, #0f0, 75%, rgb)" ) );
		assertEquals( new Color( 0xe18900 ), parseColor( "mix(#f00, #0f0, 75%, lrgb)" ) );
		assertEquals( new Color( 0xed7300 ), parseColor( "mix(#f00, #0f0, 75%, oklab)" ) );
		assertEquals( null,                  parseColor( "mix(systemColor(dummyNotExisting),#0f0)" ) );
		assertEquals( null,                  parseColor( "mix(#0f0,systemColor(dummyNotExisting))" ) );

		// tint
		assertEquals( new Color( 0xff80ff ), parseColor( "tint(#f0f)" ) );
		assertEquals( new Color( 0xffbfff ), parseColor( "tint(#f0f, 75%)" ) );
		assertEquals( new Color( 0xff80ff ), parseColor( "tint(#f0f, rgb)" ) );
		assertEquals( new Color( 0xffbcff ), parseColor( "tint(#f0f, lrgb)" ) );
		assertEquals( new Color( 0xffa6ff ), parseColor( "tint(#f0f, oklab)" ) );
		assertEquals( new Color( 0xffbfff ), parseColor( "tint(#f0f, 75%, rgb)" ) );
		assertEquals( new Color( 0xffe1ff ), parseColor( "tint(#f0f, 75%, lrgb)" ) );
		assertEquals( new Color( 0xffd4ff ), parseColor( "tint(#f0f, 75%, oklab)" ) );
		assertEquals( null,                  parseColor( "tint(systemColor(dummyNotExisting))" ) );

		// shade
		assertEquals( new Color( 0x800080 ), parseColor( "shade(#f0f)" ) );
		assertEquals( new Color( 0x400040 ), parseColor( "shade(#f0f, 75%)" ) );
		assertEquals( new Color( 0x800080 ), parseColor( "shade(#f0f, rgb)" ) );
		assertEquals( new Color( 0xbc00bc ), parseColor( "shade(#f0f, lrgb)" ) );
		assertEquals( new Color( 0x630063 ), parseColor( "shade(#f0f, oklab)" ) );
		assertEquals( new Color( 0x400040 ), parseColor( "shade(#f0f, 75%, rgb)" ) );
		assertEquals( new Color( 0x890089 ), parseColor( "shade(#f0f, 75%, lrgb)" ) );
		assertEquals( new Color( 0x220022 ), parseColor( "shade(#f0f, 75%, oklab)" ) );
		assertEquals( null,                  parseColor( "shade(systemColor(dummyNotExisting))" ) );

		// contrast
		assertEquals( new Color( 0x0000ff ), parseColor( "contrast(#bbb, #00f, #0f0)" ) );
		assertEquals( new Color( 0x00ff00 ), parseColor( "contrast(#444, #00f, #0f0)" ) );
		assertEquals( new Color( 0x00ff00 ), parseColor( "contrast(#bbb, #00f, #0f0, 60%)" ) );
		// default threshold is 43%
		assertEquals( new Color( 0x0000ff ), parseColor( "contrast(#b0b0b0, #00f, #0f0)" ) );
		assertEquals( new Color( 0x00ff00 ), parseColor( "contrast(#afafaf, #00f, #0f0)" ) );
		// with color functions as parameters
		assertEquals( new Color( 0x000099 ), parseColor( "contrast(lighten(#444, 50%), darken(#00f, 20%), #0f0)" ) );
		// missing color
		assertEquals( null,                  parseColor( "contrast(systemColor(dummyNotExisting), #00f, #0f0)" ) );

		// rgb / rgba
		assertEquals( new Color( 0x5a8120 ), parseColor( "rgb(90, 129, 32)" ) );
		assertEquals( new Color( 0x5a8120 ), parseColor( "rgb(90,129,32)" ) );
		assertEquals( new Color( 0x197fb2 ), parseColor( "rgb(10%,50%,70%)" ) );
		assertEquals( new Color( 0x197f46 ), parseColor( "rgb(10%,50%,70)" ) );
		assertEquals( new Color( 0x405a8120, true ), parseColor( "rgba(90, 129, 32, 64)" ) );
		assertEquals( new Color( 0x335a8120, true ), parseColor( "rgba(90, 129, 32, 20%)" ) );

		// deprecated rgba
		assertEquals( new Color( 0x80ff0000, true ), parseColor( "rgba(#f00, 128)" ) );
		assertEquals( new Color( 0x7fff0000, true ), parseColor( "rgba(#f00, 50%)" ) );
		assertEquals( new Color( 0x80990000, true ), parseColor( "rgba(darken(#f00, 20%), 128)" ) );

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
		assertEquals( new Color( 0xe18900 ), parseColorLazy( "mix(#f00, dummyColor, 75%, lrgb lazy)", new Color( 0x00ff00 ) ) );
		assertEquals( new Color( 0xd0a800 ), parseColorLazy( "mix(#f00, dummyColor, lazy oklab)", new Color( 0x00ff00 ) ) );

		// not a color
		assertEquals( null, parseColorLazy( "darken(dummyColor, 20%, lazy)", null ) );
		assertLogged( "FlatLaf: 'dummyColor' not found in UI defaults." );

		// tint
		assertEquals( new Color( 0xff80ff ), parseColorLazy( "tint(dummyColor, lazy)", new Color( 0xff00ff ) ) );
		assertEquals( new Color( 0xffbfff ), parseColorLazy( "tint(dummyColor, 75%, lazy)", new Color( 0xff00ff ) ) );

		// shade
		assertEquals( new Color( 0x800080 ), parseColorLazy( "shade(dummyColor, lazy)", new Color( 0xff00ff ) ) );
		assertEquals( new Color( 0x400040 ), parseColorLazy( "shade(dummyColor, 75%, lazy)", new Color( 0xff00ff ) ) );
	}

	@Test
	void parseDerivedColorFunctions() {
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
		assertDerivedColorEquals( new Color( 0xbcbc00 ), "mix(#f00, #0f0, lrgb derived)",       new Mix2( new Color( 0xff0000 ), 50, ColorFunctions.LRGB ) );
		assertDerivedColorEquals( new Color( 0xed7300 ), "mix(#f00, #0f0, 75%, oklab derived)", new Mix2( new Color( 0xff0000 ), 75, ColorFunctions.OKLAB ) );
		assertDerivedColorEquals( new Color( 0xffbcff ), "tint(#f0f, derived lrgb)",            new Mix2( new Color( 0xffffff ), 50, ColorFunctions.LRGB ) );

		// tint
		assertDerivedColorEquals( new Color( 0xff80ff ), "tint(#f0f, derived)",           new Mix2( new Color( 0xffffff ), 50 ) );
		assertDerivedColorEquals( new Color( 0xffbfff ), "tint(#f0f, 75%, derived)",      new Mix2( new Color( 0xffffff ), 75 ) );

		// shade
		assertDerivedColorEquals( new Color( 0x800080 ), "shade(#f0f, derived)",          new Mix2( new Color( 0x000000 ), 50 ) );
		assertDerivedColorEquals( new Color( 0x400040 ), "shade(#f0f, 75%, derived)",     new Mix2( new Color( 0x000000 ), 75 ) );

		// nested derived functions are joined
		assertDerivedColorEquals( new Color( 0xff0000 ), "lighten(darken(#f00, 10%, derived), 10%, derived)",
			new HSLIncreaseDecrease( 2, false, 10, false, true ),
			new HSLIncreaseDecrease( 2, true,  10, false, true ) );
		assertDerivedColorEquals( new Color( 0xff3333 ), "fade(lighten(#f00, 10%, derived), 100%, derived)",
			new HSLIncreaseDecrease( 2, true,  10, false, true ),
			new Fade( 100 ) );

		// nested non-derived function is not joined
		assertDerivedColorEquals( new Color( 0xff0000 ), "lighten(darken(#f00, 10%), 10%, derived)",
			new HSLIncreaseDecrease( 2, true,  10, false, true ) );

		// derived base color from reference is not joined
		Map<String, String> props = new HashMap<>();
		props.put( "@base", "darken(#f00, 10%, derived)" );
		Object actual = parseValue( "dummyColor", "lighten(@base, 10%, derived)", props );
		assertInstanceOf( DerivedColor.class, actual );
		assertEquals( new Color( 0xff0000 ), actual );
		ColorFunction[] functions = ((DerivedColor)actual).getFunctions();
		assertEquals( 1, functions.length );
		assertColorFunctionEquals( new HSLIncreaseDecrease( 2, true,  10, false, true ), functions[0] );
	}

	@Test
	void parseColorOver() {
		// opaque foreground
		assertEquals( new Color( 0xff0000 ), parseColor( "over(#f00, #00f)" ) );

		// translucent foreground
		assertEquals( new Color( 0x80007f ), parseColor( "over(#ff000080, #0000ff)" ) );
		assertEquals( new Color( 0x0000ff ), parseColor( "over(#ff000000, #0000ff)" ) );
		assertEquals( new Color( 0x990000 ), parseColor( "over(darken(#f00, 20%), #00f)" ) );

		// missing background
		assertEquals( new Color( 0xff0000 ), parseColor( "over(#ff000080, systemColor(dummyNotExisting))" ) );

		// missing foreground
		assertEquals( null, parseColor( "over(systemColor(dummyNotExisting), #00f)" ) );
	}

	@Test
	void options() {
		assertTrue( UIDefaultsLoader.hasOption( "derived", "derived" ) );
		assertTrue( UIDefaultsLoader.hasOption( "derived ", "derived" ) );
		assertTrue( UIDefaultsLoader.hasOption( " derived", "derived" ) );
		assertTrue( UIDefaultsLoader.hasOption( "  derived  ", "derived" ) );
		assertTrue( UIDefaultsLoader.hasOption( "derived lazy", "derived" ) );
		assertTrue( UIDefaultsLoader.hasOption( "lazy derived", "derived" ) );
		assertTrue( UIDefaultsLoader.hasOption( "relative autoInverse derived", "autoInverse" ) );
		assertTrue( UIDefaultsLoader.hasOption( "lazy\tderived", "derived" ) );

		assertFalse( UIDefaultsLoader.hasOption( "", "lazy" ) );
		assertFalse( UIDefaultsLoader.hasOption( "relative", "lazy" ) );
		assertFalse( UIDefaultsLoader.hasOption( "no-lazy", "lazy" ) );
		assertFalse( UIDefaultsLoader.hasOption( "lazy-no", "lazy" ) );
		assertFalse( UIDefaultsLoader.hasOption( "lazyderived", "lazy" ) );
		assertFalse( UIDefaultsLoader.hasOption( "noAutoInverse", "autoInverse" ) );
		assertFalse( UIDefaultsLoader.hasOption( "derived noAutoInverse", "autoInverse" ) );
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
			assertEquals( e.method, a.method );
		} else
			assertTrue( false );
	}

	private static Object parseColor( String value ) {
		return UIDefaultsLoader.parseValue( "dummyColor", value, null );
	}

	private static Object parseColorLazy( String value, Color actual ) {
		UIManager.put( "dummyColor", actual );
		Object v = UIDefaultsLoader.parseValue( "dummyColor", value, null );
		assertInstanceOf( LazyValue.class, v );
		return ((LazyValue)v).createValue( null );
	}

	//---- if() ---------------------------------------------------------------

	@Test
	void parseIf() {
		assertEquals( 1, UIDefaultsLoader.parseValue( "dummy", "if(true, 1, 2)", null ) );
		assertEquals( 2, UIDefaultsLoader.parseValue( "dummy", "if(false, 1, 2)", null ) );
		assertEquals( 2, UIDefaultsLoader.parseValue( "dummy", "if(null, 1, 2)", null ) );
		assertEquals( 2, UIDefaultsLoader.parseValue( "dummy", "if(0, 1, 2)", null ) );
		assertEquals( 1, UIDefaultsLoader.parseValue( "dummy", "if(5, 1, 2)", null ) );
		assertEquals( 1, UIDefaultsLoader.parseValue( "dummy", "if(abc, 1, 2)", null ) );
		assertEquals( 1, UIDefaultsLoader.parseValue( "dummy", "if(#f00, 1, 2)", null ) );
		assertEquals( 2, UIDefaultsLoader.parseValue( "dummy", "if(#f0, 1, 2)", null ) ); // invalid color

		// value type from key
		assertEquals( 2.5f, UIDefaultsLoader.parseValue( "dummyWidth", "if(false, 1, 2.5)", null ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "dummyColor", "if(true, #f00, #0f0)", null ) );
		assertEquals( new Color( 0x990000 ), UIDefaultsLoader.parseValue( "dummyColor", "if(true, darken(#f00, 20%), #0f0)", null ) );

		// nested
		assertEquals( 3, UIDefaultsLoader.parseValue( "dummy", "if(false, 1, if(true, 3, 4))", null ) );

		// used as parameter in color function
		assertEquals( new Color( 0xff6666 ), parseColor( "lighten(if(true, #f00, #000), 20%)" ) );
		assertEquals( new Color( 0x333333 ), parseColor( "lighten(if(false, #f00, #000), 20%)" ) );

		// with variables and properties
		Map<String, String> props = new HashMap<>();
		props.put( "@true", "true" );
		props.put( "@zero", "0" );
		props.put( "@red", "#f00" );
		props.put( "Some.color", "#00f" );
		assertEquals( new Color( 0xff0000 ), parseValue( "dummyColor", "if(@true, @red, #0f0)", props ) );
		assertEquals( new Color( 0x00ff00 ), parseValue( "dummyColor", "if(@zero, @red, #0f0)", props ) );
		assertEquals( new Color( 0x0000ff ), parseValue( "dummyColor", "if($Some.color, $Some.color, #0f0)", props ) );
		assertEquals( new Color( 0x00ff00 ), parseValue( "dummyColor", "if(@missing, @red, #0f0)", props ) );
		assertEquals( new Color( 0x00ff00 ), parseValue( "dummyColor", "if($?Missing.color, @red, #0f0)", props ) );
		assertEquals( new Color( 0xff6666 ), parseValue( "dummyColor", "lighten(if(@true, @red, #000), 20%)", props ) );
	}

	//---- lazy() -------------------------------------------------------------

	@Test
	void parseLazy() {
		UIManager.put( "dummyLazyTarget", Color.red );
		try {
			assertEquals( Color.red, createValue( UIDefaultsLoader.parseValue( "dummy", "lazy(dummyLazyTarget)", null ) ) );
			assertEquals( Color.red, createValue( UIDefaultsLoader.parseValue( "dummy", "lazy( dummyLazyTarget )", null ) ) );
			assertEquals( Color.red, createValue( UIDefaultsLoader.parseValue( "dummyColor", "lazy(dummyLazyTarget)", null ) ) );
			assertEquals( Color.red, createValue( UIDefaultsLoader.parseValue( "dummy", "lazy(?dummyLazyTarget)", null ) ) );
			assertEquals( null, createValue( UIDefaultsLoader.parseValue( "dummy", "lazy(?dummyNotExisting)", null ) ) );
			assertEquals( null, createValue( UIDefaultsLoader.parseValue( "dummy", "lazy(dummyNotExisting)", null ) ) );
			assertLogged( "FlatLaf: 'dummyNotExisting' not found in UI defaults." );

			assertEquals( Color.red, UIDefaultsLoader.lazyUIManagerGet( "dummyLazyTarget" ) );
			assertEquals( Color.red, UIDefaultsLoader.lazyUIManagerGet( "?dummyLazyTarget" ) );
			assertEquals( null, UIDefaultsLoader.lazyUIManagerGet( "?dummyNotExisting" ) );
			assertEquals( null, UIDefaultsLoader.lazyUIManagerGet( "dummyNotExisting" ) );
			assertLogged( "FlatLaf: 'dummyNotExisting' not found in UI defaults." );
		} finally {
			UIManager.put( "dummyLazyTarget", null );
		}

		// used as parameter in color function
		Map<String, String> props = new HashMap<>();
		props.put( "Some.color", "#f00" );
		props.put( "Some.lazyColor", "lazy(Some.color)" );
		assertEquals( new Color( 0x990000 ), parseValue( "dummyColor", "darken(lazy(Some.color), 20%)", props ) );
		assertEquals( new Color( 0x990000 ), parseValue( "dummyColor", "darken($Some.lazyColor, 20%)", props ) );
	}

	//---- systemColor() ------------------------------------------------------

	@Test
	void parseSystemColor() {
		Function<String, Color> oldSystemColorGetter = FlatLaf.getSystemColorGetter();
		try {
			// without system color getter
			FlatLaf.setSystemColorGetter( null );
			assertEquals( null, parseColor( "systemColor(accent)" ) );
			assertEquals( null, parseColor( "systemColor(accent, null)" ) );
			assertEquals( new Color( 0xff0000 ), parseColor( "systemColor(accent, #f00)" ) );
			assertEquals( new Color( 0x990000 ), parseColor( "systemColor(accent, darken(#f00, 20%))" ) );
			assertEquals( new Color( 0x990000 ), parseColor( "darken(systemColor(accent, #f00), 20%)" ) );
			assertEquals( null, parseColor( "darken(systemColor(accent), 20%)" ) );
			assertEquals( null, UIDefaultsLoader.parseValue( "dummy", "systemColor(accent)", null ) );

			// with system color getter
			FlatLaf.setSystemColorGetter( name -> name.equals( "accent" ) ? Color.blue : null );
			assertEquals( Color.blue, parseColor( "systemColor(accent)" ) );
			assertEquals( Color.blue, parseColor( "systemColor(accent, #f00)" ) );
			assertEquals( new Color( 0xff0000 ), parseColor( "systemColor(other, #f00)" ) );
			assertEquals( null, parseColor( "systemColor(other)" ) );
			assertEquals( new Color( 0x000099 ), parseColor( "darken(systemColor(accent, #f00), 20%)" ) );

			// with variable
			Map<String, String> props = new HashMap<>();
			props.put( "@default", "#0f0" );
			assertEquals( new Color( 0x00ff00 ), parseValue( "dummyColor", "systemColor(other, @default)", props ) );
		} finally {
			FlatLaf.setSystemColorGetter( oldSystemColorGetter );
		}
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
		assertThrows( new IllegalArgumentException( "invalid border '1,abc,3,4' (invalid insets '1,abc,3,4')" ), () -> parseBorder( "1,abc,3,4" ) );
		assertThrows( new IllegalArgumentException( "invalid border '1,2,3' (invalid insets '1,2,3')" ), () -> parseBorder( "1,2,3" ) );
		assertThrows( new IllegalArgumentException( "invalid border '1,2,3,,,' (invalid insets '1,2,3,,,')" ), () -> parseBorder( "1,2,3,,," ) );
		assertThrows( new IllegalArgumentException( "invalid border '1,2,3,4,#f0' (invalid color '#f0')" ), () -> parseBorder( "1,2,3,4,#f0" ) );
		assertThrows( new IllegalArgumentException( "invalid border '1,2,3,4,#f00,2.5abc' (invalid float '2.5abc')" ), () -> parseBorder( "1,2,3,4,#f00,2.5abc" ) );
		assertThrows( new IllegalArgumentException( "invalid border '1,2,3,4,#f00,2.5,6abc' (invalid integer '6abc')" ), () -> parseBorder( "1,2,3,4,#f00,2.5,6abc" ) );
	}

	@Test
	void parseInvalidBorderFunctions() {
		assertThrows( new IllegalArgumentException( "invalid border 'empty(1,abc,3,4)' (invalid insets '1,abc,3,4')" ), () -> parseBorder( "empty(1,abc,3,4)" ) );
		assertThrows( new IllegalArgumentException( "invalid border 'empty(1,2,3)' (invalid insets '1,2,3')" ), () -> parseBorder( "empty(1,2,3)" ) );
		assertThrows( new IllegalArgumentException( "invalid border 'empty(1,2,3,,,)' (invalid insets '1,2,3,,,')" ), () -> parseBorder( "empty(1,2,3,,,)" ) );
		assertThrows( new IllegalArgumentException( "invalid border 'line(1,2,3,4,#f0)' (invalid color '#f0')" ), () -> parseBorder( "line(1,2,3,4,#f0)" ) );
		assertThrows( new IllegalArgumentException( "invalid border 'line(1,2,3,4,#f00,2.5abc)' (invalid float '2.5abc')" ), () -> parseBorder( "line(1,2,3,4,#f00,2.5abc)" ) );
		assertThrows( new IllegalArgumentException( "invalid border 'line(1,2,3,4,#f00,2.5,6abc)' (invalid integer '6abc')" ), () -> parseBorder( "line(1,2,3,4,#f00,2.5,6abc)" ) );

		assertThrows( new IllegalArgumentException( "invalid border 'empty()' (missing parameters in function 'empty()')" ), () -> parseBorder( "empty()" ) );
		assertThrows( new IllegalArgumentException( "invalid border 'line()' (missing parameters in function 'line()')" ), () -> parseBorder( "line()" ) );
		assertThrows( new IllegalArgumentException( "invalid border 'matte()' (missing parameters in function 'matte()')" ), () -> parseBorder( "matte()" ) );

		assertThrowsExactly( IllegalArgumentException.class, () -> parseBorder( "line(1,2,3,4)" ) );
		assertThrowsExactly( IllegalArgumentException.class, () -> parseBorder( "line(1,2,3,#f00)" ) );
		assertThrowsExactly( IllegalArgumentException.class, () -> parseBorder( "matte(1,2,3,4)" ) );
		assertThrowsExactly( IllegalArgumentException.class, () -> parseBorder( "matte(1,2,3,x,#f00)" ) );

		assertThrows( new IllegalArgumentException( "invalid border 'line(#f00), foo(1)' (unknown border function 'foo(1)')" ), () -> parseBorder( "line(#f00), foo(1)" ) );
		assertThrowsExactly( IllegalArgumentException.class, () -> parseBorder( "line(#f00) empty(1,2,3,4)" ) );
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

	@Test
	void parseInvalidColorFunctions() {
		assertMissingParams( "dummyColor", "lighten()" );
		assertMissingParams( "dummyColor", "lighten(#f00)" );
		assertMissingParams( "dummyColor", "darken(#f00)" );
		assertMissingParams( "dummyColor", "saturate(#f00)" );
		assertMissingParams( "dummyColor", "desaturate(#f00)" );
		assertMissingParams( "dummyColor", "fadein(#f00)" );
		assertMissingParams( "dummyColor", "fadeout(#f00)" );
		assertMissingParams( "dummyColor", "fade(#f00)" );
		assertMissingParams( "dummyColor", "spin(#f00)" );
		assertMissingParams( "dummyColor", "changeHue(#f00)" );
		assertMissingParams( "dummyColor", "changeSaturation(#f00)" );
		assertMissingParams( "dummyColor", "changeLightness(#f00)" );
		assertMissingParams( "dummyColor", "changeAlpha(#f00)" );
		assertMissingParams( "dummyColor", "mix(#f00)" );
		assertMissingParams( "dummyColor", "tint()" );
		assertMissingParams( "dummyColor", "shade()" );
		assertMissingParams( "dummyColor", "contrast(#f00)" );
		assertMissingParams( "dummyColor", "rgb(90)" );
		assertMissingParams( "dummyColor", "rgba(90)" );
		assertMissingParams( "dummyColor", "hsl(90)" );
		assertMissingParams( "dummyColor", "hsla(90)" );
		assertMissingParams( "dummyColor", "over(#f00)" );
		assertMissingParams( "dummyColor", "systemColor()" );
		assertMissingParams( "dummyColor", "darken(lazy(), 10%)", "lazy()" );
		assertMissingParams( "dummyColor", "darken(if(true,#f00), 10%)", "if(true,#f00)" );
		assertMissingParams( "dummyColor", "if(true,#f00)" );
		assertMissingParams( "dummy", "if(true,1)" );
		assertMissingParams( "dummy", "if(true,1,2,3)" );

		assertThrows( new IllegalArgumentException( "unknown color function 'foo(#f00)'" ), () -> parseColor( "foo(#f00)" ) );
		assertThrows( new IllegalArgumentException( "unknown color function 'foo(#f00)'" ), () -> parseColor( "darken(foo(#f00), 10%)" ) );
		assertThrows( new IllegalArgumentException( "missing opening parenthesis in function 'abc)'" ), () -> parseColor( "abc)" ) );

		// invalid parameters
		assertThrows( new NumberFormatException( "integer '256' out of range (0-255)" ), () -> parseColor( "rgb(256, 0, 0)" ) );
		assertThrows( new NumberFormatException( "integer '-1' out of range (0-255)" ), () -> parseColor( "rgb(0, -1, 0)" ) );
		assertThrows( new NumberFormatException( "invalid integer 'abc'" ), () -> parseColor( "rgb(0, 0, abc)" ) );
		assertThrows( new IllegalArgumentException( "percentage out of range (0-100%) '101%'" ), () -> parseColor( "rgba(0, 0, 0, 101%)" ) );
		assertThrows( new NumberFormatException( "integer '361' out of range (0-360)" ), () -> parseColor( "hsl(361, 50%, 50%)" ) );
		assertThrows( new NumberFormatException( "invalid percentage '50'" ), () -> parseColor( "hsl(90, 50, 50%)" ) );
		assertThrows( new NumberFormatException( "invalid percentage '20'" ), () -> parseColor( "lighten(#f00, 20)" ) );
		assertThrows( new NumberFormatException( "invalid percentage 'abc%'" ), () -> parseColor( "fade(#f00, abc%)" ) );
		assertThrows( new NumberFormatException( "invalid integer '1.5'" ), () -> parseColor( "spin(#f00, 1.5)" ) );
		assertThrows( new IllegalArgumentException( "invalid color '#f0'" ), () -> parseColor( "darken(#f0, 10%)" ) );
		assertThrows( new IllegalArgumentException( "invalid color '#f0'" ), () -> parseColor( "mix(#f00, #f0)" ) );

		// endless recursion
		StringBuilder buf = new StringBuilder();
		for( int i = 0; i < 110; i++ )
			buf.append( "lighten(" );
		buf.append( "#f00" );
		for( int i = 0; i < 110; i++ )
			buf.append( ",1%)" );
		Throwable ex = assertThrowsExactly( IllegalArgumentException.class, () -> parseColor( buf.toString() ) );
		assertTrue( ex.getMessage().startsWith( "endless recursion in color function " ), ex.getMessage() );

		// still works after endless recursion
		assertEquals( new Color( 0xff6666 ), parseColor( "lighten(#f00, 20%)" ) );
	}

	@Test
	void parseInvalidHexColors() {
		assertThrows( new IllegalArgumentException( "invalid color '#'" ), () -> parseColor( "#" ) );
		assertThrows( new IllegalArgumentException( "invalid color '#ff'" ), () -> parseColor( "#ff" ) );
		assertThrows( new IllegalArgumentException( "invalid color '#12345'" ), () -> parseColor( "#12345" ) );
		assertThrows( new IllegalArgumentException( "invalid color '#1234567'" ), () -> parseColor( "#1234567" ) );
		assertThrows( new IllegalArgumentException( "invalid color '#123456789'" ), () -> parseColor( "#123456789" ) );
		assertThrows( new IllegalArgumentException( "invalid color '#ggg'" ), () -> parseColor( "#ggg" ) );
		assertThrows( new IllegalArgumentException( "invalid color '#12345g'" ), () -> parseColor( "#12345g" ) );
		assertThrows( new IllegalArgumentException( "invalid color 'f000'" ), () -> parseColor( "f000" ) );
	}

	@Test
	void parseInvalidTypePrefix() {
		assertThrows( new NumberFormatException( "invalid integer 'abc'" ), () -> UIDefaultsLoader.parseValue( "dummy", "{integer}abc", null ) );
		assertThrows( new NumberFormatException( "invalid float 'abc'" ), () -> UIDefaultsLoader.parseValue( "dummy", "{float}abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid boolean 'abc'" ), () -> UIDefaultsLoader.parseValue( "dummy", "{boolean}abc", null ) );
		assertThrows( new NumberFormatException( "invalid integer 'abc'" ), () -> UIDefaultsLoader.parseValue( "dummy", "{scaledInteger}abc", null ) );
		assertThrows( new NumberFormatException( "invalid float 'abc'" ), () -> UIDefaultsLoader.parseValue( "dummy", "{scaledFloat}abc", null ) );
		assertThrows( new IllegalArgumentException( "invalid insets '1,2,3'" ), () -> UIDefaultsLoader.parseValue( "dummy", "{scaledInsets}1,2,3", null ) );
		assertThrows( new IllegalArgumentException( "invalid size '1'" ), () -> UIDefaultsLoader.parseValue( "dummy", "{scaledDimension}1", null ) );
	}

	@Test
	void parseInvalidGrayFilter() {
		assertThrows( new IllegalArgumentException( "invalid gray filter '1,2'" ), () -> UIDefaultsLoader.parseValue( "Component.grayFilter", "1,2", null ) );
		assertThrows( new IllegalArgumentException( "invalid gray filter 'a,b,c'" ), () -> UIDefaultsLoader.parseValue( "Component.grayFilter", "a,b,c", null ) );
		assertThrows( new IllegalArgumentException( "invalid gray filter '1,2,x'" ), () -> UIDefaultsLoader.parseValue( "dummy", "{grayFilter}1,2,x", null ) );
	}

	private void assertMissingParams( String key, String value ) {
		assertMissingParams( key, value, value );
	}

	private void assertMissingParams( String key, String value, String function ) {
		assertThrows( new IllegalArgumentException( "missing parameters in function '" + function + "'" ),
			() -> UIDefaultsLoader.parseValue( key, value, null ) );
	}

	private void assertThrows( Throwable expected, Executable executable ) {
		Throwable actual = assertThrowsExactly( expected.getClass(), executable );
		assertEquals( expected.getMessage(), actual.getMessage() );
	}

	//---- UIDefaultsLoader helper methods ------------------------------------

	@Test
	void resolveValue() {
		Map<String, String> props = new HashMap<>();
		props.put( "@var", "#f00" );
		props.put( "@var2", "@var" );
		props.put( "@self", "@self" );
		props.put( "Button.background", "@var" );
		props.put( "Button.foreground", "$Button.background" );
		Function<String, String> getter = props::get;

		assertEquals( "abc", UIDefaultsLoader.resolveValue( "abc", getter ) );
		assertEquals( "abc", UIDefaultsLoader.resolveValue( "  abc  ", getter ) );
		assertEquals( "#f00", UIDefaultsLoader.resolveValue( "@var", getter ) );
		assertEquals( "#f00", UIDefaultsLoader.resolveValue( " @var ", getter ) );
		assertEquals( "#f00", UIDefaultsLoader.resolveValue( "@var2", getter ) );
		assertEquals( "#f00", UIDefaultsLoader.resolveValue( "$Button.background", getter ) );
		assertEquals( "#f00", UIDefaultsLoader.resolveValue( "$Button.foreground", getter ) );
		assertEquals( "#f00", UIDefaultsLoader.resolveValue( "$?Button.background", getter ) );
		assertEquals( "null", UIDefaultsLoader.resolveValue( "$?Missing.key", getter ) );

		// references are not resolved within functions
		assertEquals( "darken(@var,10%)", UIDefaultsLoader.resolveValue( "darken(@var,10%)", getter ) );

		assertThrows( new IllegalArgumentException( "variable or property '@missing' not found" ), () -> UIDefaultsLoader.resolveValue( "@missing", getter ) );
		assertThrows( new IllegalArgumentException( "variable or property 'Missing.key' not found" ), () -> UIDefaultsLoader.resolveValue( "$Missing.key", getter ) );
		assertThrows( new IllegalArgumentException( "endless recursion in variable or property '@self'" ), () -> UIDefaultsLoader.resolveValue( "@self", getter ) );

		// references within functions are resolved when parsing
		assertEquals( new Color( 0xcc0000 ), parseValue( "dummyColor", "darken(@var, 10%)", props ) );
		assertEquals( new Color( 0xcc0000 ), parseValue( "dummyColor", "darken($Button.foreground, 10%)", props ) );
		assertEquals( new Color( 0x808000 ), parseValue( "dummyColor", "mix(@var, #0f0)", props ) );
		assertEquals( new Color( 0xff0000 ), parseValue( "dummyColor", "over(@var, #00f)", props ) );
		assertBorderEquals( new FlatLineBorder( new Insets( 1,1,1,1 ), Color.red ), (Border) createValue( parseValue( "dummyBorder", "line(@var)", props ) ) );
		assertBorderEquals( new FlatMatteBorder( new Insets( 1,2,3,4 ), Color.red ), (Border) createValue( parseValue( "dummyBorder", "matte(1,2,3,4,$Button.background)", props ) ) );
		assertBorderEquals( new FlatLineBorder( new Insets( 1,2,3,4 ), Color.red ), (Border) createValue( parseValue( "dummyBorder", "1,2,3,4,@var", props ) ) );
		assertEquals( new TestInstance( Color.red ), createValue( parseValue( "dummyIcon", TestInstance.class.getName() + "(@var)", props ) ) );
	}

	@Test
	void resolveValueFromUIManager() {
		Map<String, String> variables = new HashMap<>();
		variables.put( "@accent", "#f00" );
		variables.put( "@ref", "@accent" );
		variables.put( "@prop", "$dummyRefColor" );
		UIManager.put( UIDefaultsLoader.KEY_VARIABLES, variables );
		UIManager.put( "dummyRefColor", new Color( 0x123456 ) );
		UIManager.put( "dummyRefColorAlpha", new Color( 0x80123456, true ) );
		UIManager.put( "dummyRefInt", 5 );
		try {
			assertEquals( "abc", UIDefaultsLoader.resolveValueFromUIManager( "abc" ) );
			assertEquals( "#f00", UIDefaultsLoader.resolveValueFromUIManager( "@accent" ) );
			assertEquals( "#f00", UIDefaultsLoader.resolveValueFromUIManager( "@ref" ) );
			assertEquals( "#123456", UIDefaultsLoader.resolveValueFromUIManager( "@prop" ) );
			assertEquals( "#123456", UIDefaultsLoader.resolveValueFromUIManager( "$dummyRefColor" ) );
			assertEquals( "#12345680", UIDefaultsLoader.resolveValueFromUIManager( "$dummyRefColorAlpha" ) );

			assertThrows( new IllegalArgumentException( "variable '@missing' not found" ), () -> UIDefaultsLoader.resolveValueFromUIManager( "@missing" ) );
			assertThrows( new IllegalArgumentException( "property 'dummyMissing' not found" ), () -> UIDefaultsLoader.resolveValueFromUIManager( "$dummyMissing" ) );
			assertThrows( new IllegalArgumentException( "property value type 'java.lang.Integer' not supported in references" ),
				() -> UIDefaultsLoader.resolveValueFromUIManager( "$dummyRefInt" ) );
		} finally {
			UIManager.put( UIDefaultsLoader.KEY_VARIABLES, null );
			UIManager.put( "dummyRefColor", null );
			UIManager.put( "dummyRefColorAlpha", null );
			UIManager.put( "dummyRefInt", null );
		}

		// no variables in UIManager
		assertThrows( new IllegalArgumentException( "variable '@accent' not found" ), () -> UIDefaultsLoader.resolveValueFromUIManager( "@accent" ) );
	}

	@Test
	void uiPropertiesPrefixes() throws IOException {
		String platformPrefix = FlatLaf.getUIKeyPlatformPrefixes().iterator().next();
		String otherPlatformPrefix = platformPrefix.equals( "[win]" ) ? "[mac]" : "[win]";

		for( boolean dark : new boolean[] { false, true } ) {
			String lightOrDark = dark ? "[dark]" : "[light]";
			String otherLightOrDark = dark ? "[light]" : "[dark]";

			Properties properties = UIDefaultsLoader.newUIProperties( dark );
			properties.put( "a", "1" );
			properties.put( lightOrDark + "b", "2" );
			properties.put( otherLightOrDark + "c", "3" );
			properties.put( platformPrefix + "d", "4" );
			properties.put( otherPlatformPrefix + "e", "5" );
			properties.put( lightOrDark + platformPrefix + "f", "6" );
			properties.put( platformPrefix + lightOrDark + "g", "7" );
			properties.put( lightOrDark + otherPlatformPrefix + "h", "8" );
			properties.put( "[style]i", "9" );
			properties.put( lightOrDark + "[style]j", "10" );
			properties.put( otherLightOrDark + "[style]k", "11" );
			properties.put( "[unknown-prefix]l", "12" );
			properties.put( "[invalid-prefix m", "13" );

			Properties expected = new Properties();
			expected.put( "a", "1" );
			expected.put( "b", "2" );
			expected.put( "d", "4" );
			expected.put( "f", "6" );
			expected.put( "g", "7" );
			expected.put( "[style]i", "9" );
			expected.put( "[style]j", "10" );
			assertEquals( expected, properties );

			// prefixed value overrides
			properties.put( lightOrDark + "a", "a2" );
			assertEquals( "a2", properties.getProperty( "a" ) );
		}

		// properties are loaded as UTF-8
		Properties properties = UIDefaultsLoader.newUIProperties( false );
		properties.load( new ByteArrayInputStream( "key = ä€".getBytes( StandardCharsets.UTF_8 ) ) );
		assertEquals( "ä€", properties.getProperty( "key" ) );
	}

	@Test
	void loadDefaultsFromProperties() {
		UIDefaults defaults = new UIDefaults();
		defaults.put( "Button.arc", 1 );
		defaults.put( "Label.arc", 1 );
		defaults.put( "Other.foo", 1 );
		defaults.put( "arc", 1 );

		Properties additionalDefaults = new Properties();
		additionalDefaults.put( "*.arc", "5" );
		additionalDefaults.put( "@var", "#f00" );
		additionalDefaults.put( "Label.arc", "7" );
		additionalDefaults.put( "Button.background", "@var" );
		additionalDefaults.put( "Button.foreground", "darken($Button.background, 20%)" );
		additionalDefaults.put( "Button.disabledForeground", "@missing" );
		additionalDefaults.put( "[light]Button.selectedForeground", "#0f0" );
		additionalDefaults.put( "[dark]Button.selectedForeground", "#00f" );

		UIDefaultsLoader.loadDefaultsFromProperties( Collections.emptyList(), Collections.emptyList(),
			null, additionalDefaults, false, defaults );

		assertLogged( "FlatLaf: Failed to parse: 'Button.disabledForeground=@missing'",
			"variable or property '@missing' not found" );

		// wildcards
		assertEquals( 5, defaults.get( "Button.arc" ) );
		assertEquals( 7, defaults.get( "Label.arc" ) );
		assertEquals( 1, defaults.get( "Other.foo" ) );
		assertEquals( 1, defaults.get( "arc" ) );
		assertFalse( defaults.containsKey( "*.arc" ) );
		assertFalse( defaults.containsKey( "New.arc" ) );

		// variables and references
		assertEquals( new Color( 0xff0000 ), defaults.get( "Button.background" ) );
		assertEquals( new Color( 0x990000 ), defaults.get( "Button.foreground" ) );
		assertFalse( defaults.containsKey( "Button.disabledForeground" ) );
		assertFalse( defaults.containsKey( "@var" ) );
		assertEquals( Collections.singletonMap( "@var", "#f00" ), defaults.get( UIDefaultsLoader.KEY_VARIABLES ) );

		// prefixes
		assertEquals( new Color( 0x00ff00 ), defaults.get( "Button.selectedForeground" ) );
	}

	@Test
	void splitFunctionParams() {
		assertEquals( Collections.emptyList(), UIDefaultsLoader.splitFunctionParams( "", ',' ) );
		assertEquals( Collections.emptyList(), UIDefaultsLoader.splitFunctionParams( "  ", ',' ) );
		assertEquals( Arrays.asList( "a" ), UIDefaultsLoader.splitFunctionParams( " a ", ',' ) );
		assertEquals( Arrays.asList( "a", "b", "c" ), UIDefaultsLoader.splitFunctionParams( "a, b ,c", ',' ) );
		assertEquals( Arrays.asList( "a", "(b,c)", "d" ), UIDefaultsLoader.splitFunctionParams( "a,(b,c),d", ',' ) );
		assertEquals( Arrays.asList( "f(a, g(b, c))", "h(d)" ), UIDefaultsLoader.splitFunctionParams( "f(a, g(b, c)), h(d)", ',' ) );
		assertEquals( Arrays.asList( "a", "", "" ), UIDefaultsLoader.splitFunctionParams( "a,,", ',' ) );
		assertEquals( Arrays.asList( "", "a" ), UIDefaultsLoader.splitFunctionParams( ",a", ',' ) );
		assertEquals( Arrays.asList( "a", "b,c" ), UIDefaultsLoader.splitFunctionParams( "a;b,c", ';' ) );
	}

	@Test
	void parsePercentage() {
		assertEquals( 0, UIDefaultsLoader.parsePercentage( "0%" ) );
		assertEquals( 50, UIDefaultsLoader.parsePercentage( "50%" ) );
		assertEquals( 100, UIDefaultsLoader.parsePercentage( "100%" ) );

		assertThrows( new NumberFormatException( "invalid percentage '50'" ), () -> UIDefaultsLoader.parsePercentage( "50" ) );
		assertThrows( new NumberFormatException( "invalid percentage 'abc%'" ), () -> UIDefaultsLoader.parsePercentage( "abc%" ) );
		assertThrows( new NumberFormatException( "invalid percentage '%'" ), () -> UIDefaultsLoader.parsePercentage( "%" ) );
		assertThrows( new IllegalArgumentException( "percentage out of range (0-100%) '101%'" ), () -> UIDefaultsLoader.parsePercentage( "101%" ) );
		assertThrows( new IllegalArgumentException( "percentage out of range (0-100%) '-1%'" ), () -> UIDefaultsLoader.parsePercentage( "-1%" ) );
	}

	@Test
	void simpleClassName() {
		assertEquals( "FlatLaf", UIDefaultsLoader.simpleClassName( FlatLaf.class ) );
		assertEquals( "TestUIDefaultsLoader$TestInstance", UIDefaultsLoader.simpleClassName( TestInstance.class ) );
	}

	@Test
	void lafClassesForDefaultsLoading() {
		assertEquals( Arrays.asList( FlatLaf.class, FlatLightLaf.class, FlatIntelliJLaf.class ),
			UIDefaultsLoader.getLafClassesForDefaultsLoading( FlatIntelliJLaf.class ) );
		assertEquals( Arrays.asList( FlatLaf.class, FlatDarkLaf.class ),
			UIDefaultsLoader.getLafClassesForDefaultsLoading( FlatDarkLaf.class ) );
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
