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

package com.formdev.flatlaf.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.*;
import com.formdev.flatlaf.util.ColorFunctions;

/**
 * @author Karl Tauber
 */
public class TestFlatStyling
{
	@BeforeAll
	static void setup() {
		HashMap<String, String> globalExtraDefaults = new HashMap<>();
		globalExtraDefaults.put( "@var1", "#f00" );
		globalExtraDefaults.put( "@var2", "@var1" );
		globalExtraDefaults.put( "var2Resolved", "@var2" );
		FlatLaf.setGlobalExtraDefaults( globalExtraDefaults );

		TestUtils.setup( false );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();

		FlatLaf.setGlobalExtraDefaults( null );
	}

	@Test
	void parse() {
		assertEquals( null, FlatStylingSupport.parse( null ) );
		assertEquals( null, FlatStylingSupport.parse( "" ) );
		assertEquals( null, FlatStylingSupport.parse( "  " ) );
		assertEquals( null, FlatStylingSupport.parse( ";" ) );
		assertEquals( null, FlatStylingSupport.parse( " ; ; " ) );

		assertEquals(
			expectedMap( "background", Color.WHITE ),
			FlatStylingSupport.parse( "background: #fff" ) );
		assertEquals(
			expectedMap( "background", Color.WHITE, "foreground", Color.BLACK ),
			FlatStylingSupport.parse( "background: #fff; foreground: #000" ) );
		assertEquals(
			expectedMap( "background", Color.WHITE, "foreground", Color.BLACK, "someWidth", 20 ),
			FlatStylingSupport.parse( "background: #fff; foreground: #000; someWidth: 20" ) );
	}

	@Test
	void parseIfFunction() {
		testColorStyle( 0x00ff00, "if(#000,#0f0,#dfd)" );
		testColorStyle( 0xddffdd, "if(null,#0f0,#dfd)" );
		testColorStyle( 0x00ff00, "if(true,#0f0,#dfd)" );
		testColorStyle( 0xddffdd, "if(false,#0f0,#dfd)" );
		testColorStyle( 0x00ff00, "if(1,#0f0,#dfd)" );
		testColorStyle( 0xddffdd, "if(0,#0f0,#dfd)" );

		// nested
		testColorStyle( 0xff6666, "if(true,lighten(#f00,20%),darken(#f00,20%))" );
		testColorStyle( 0x990000, "if(false,lighten(#f00,20%),darken(#f00,20%))" );
		testColorStyle( 0xddffdd, "if($undefinedProp,#0f0,#dfd)" );
		testColorStyle( 0x33ff33, "lighten(if(#000,#0f0,#dfd), 10%)" );
	}

	@Test
	void parseColorFunctions() {
		// rgb, rgba, hsl, hsla
		testColorStyle( 0x0c2238, "rgb(12,34,56)" );
		testColorStyle( 0x4e0c2238, "rgba(12,34,56,78)" );
		testColorStyle( 0xb57869, "hsl(12,34%,56%)" );
		testColorStyle( 0xc7b57869, "hsla(12,34%,56%,78%)" );

		// lighten, darken
		testColorStyle( 0xff6666, "lighten(#f00,20%)" );
		testColorStyle( 0x990000, "darken(#f00,20%)" );

		// saturate, desaturate
		testColorStyle( 0x9c3030, "saturate(#844,20%)" );
		testColorStyle( 0x745858, "desaturate(#844,20%)" );

		// fadein, fadeout, fade
		testColorStyle( 0x4dff0000, "fadein(#ff000000,30%)" );
		testColorStyle( 0x99ff0000, "fadeout(#ff0000,40%)" );
		testColorStyle( 0x80ff0000, "fade(#ff0000,50%)" );

		// spin
		testColorStyle( 0xffaa00, "spin(#f00,40)" );
		testColorStyle( 0xff00aa, "spin(#f00,-40)" );

		// changeHue, changeSaturation, changeLightness, changeAlpha
		testColorStyle( 0x00ffff, "changeHue(#f00,180)" );
		testColorStyle( 0xbf4040, "changeSaturation(#f00,50%)" );
		testColorStyle( 0xff9999, "changeLightness(#f00,80%)" );
		testColorStyle( 0x80ff0000, "changeAlpha(#f00,50%)" );

		// mix
		testColorStyle( 0x1ae600, "mix(#f00,#0f0,10%)" );
		testColorStyle( 0x40bf00, "mix(#f00,#0f0,25%)" );
		testColorStyle( 0x808000, "mix(#f00,#0f0)" );
		testColorStyle( 0xbf4000, "mix(#f00,#0f0,75%)" );
		testColorStyle( 0xe61a00, "mix(#f00,#0f0,90%)" );

		// tint
		testColorStyle( 0xff40ff, "tint(#f0f,25%)" );
		testColorStyle( 0xff80ff, "tint(#f0f)" );
		testColorStyle( 0xffbfff, "tint(#f0f,75%)" );

		// shade
		testColorStyle( 0xbf00bf, "shade(#f0f,25%)" );
		testColorStyle( 0x800080, "shade(#f0f)" );
		testColorStyle( 0x400040, "shade(#f0f,75%)" );

		// contrast
		testColorStyle( 0xffffff, "contrast(#111,#000,#fff)" );
		testColorStyle( 0x000000, "contrast(#eee,#000,#fff)" );

		// nested
		testColorStyle( 0xd1c7c7, "saturate(darken(#fff,20%),10%)" );
		testColorStyle( 0xcf00cf, "shade(shade(#f0f,10%),10%)" );
		testColorStyle( 0xba00ba, "shade(shade(shade(#f0f,10%),10%),10%)" );
		testColorStyle( 0x000000, "contrast(contrast(#222,#111,#eee),contrast(#eee,#000,#fff),contrast(#111,#000,#fff))" );
	}

	@Test
	void parseReferences() {
		UIManager.put( "Test.background", Color.white );
		assertEquals( Color.white, UIManager.getColor( "Test.background" ) );

		testColorStyle( 0xffffff, "$Test.background" );
		testColorStyle( 0xcccccc, "darken($Test.background,20%)" );
		testColorStyle( 0xd1c7c7, "saturate(darken($Test.background,20%),10%)" );

		testStyle( "hideMnemonics", true, "$Component.hideMnemonics" );
		testStyle( "arc", 6, "$Button.arc" );
		testStyle( "dropShadowOpacity", 0.15f, "$Popup.dropShadowOpacity" );
		testStyle( "margin", new Insets( 2, 14, 2, 14 ) , "$Button.margin" );
		testStyle( "iconSize", new Dimension( 64, 64 ), "$DesktopIcon.iconSize" );
		testStyle( "arrowType", "chevron", "$Component.arrowType" );
	}

	@Test
	void parseVariables() {
		Color background = UIManager.getColor( "Panel.background" );

		testColorStyle( background.getRGB(), "@background" );
		testColorStyle(
			ColorFunctions.darken( background, 0.2f ).getRGB(),
			"darken(@background,20%)" );
		testColorStyle(
			ColorFunctions.saturate( ColorFunctions.darken( background, 0.2f ), 0.1f ).getRGB(),
			"saturate(darken(@background,20%),10%)" );
	}

	@Test
	void parseRecursiveVariables() {
		Color background = UIManager.getColor( "var2Resolved" );

		testColorStyle( background.getRGB(), "@var2" );
	}

	private void testColorStyle( int expectedRGB, String style ) {
		testStyle( "background", new Color( expectedRGB, (expectedRGB & 0xff000000) != 0 ), style );
	}

	private void testStyle( String key, Object expected, String style ) {
		assertEquals(
			expectedMap( key, expected ),
			FlatStylingSupport.parse( key + ": " + style ) );
	}

	private Map<Object, Object> expectedMap( Object... keyValuePairs ) {
		Map<Object, Object> map = new HashMap<>();
		for( int i = 0; i < keyValuePairs.length; i += 2 )
			map.put( keyValuePairs[i], keyValuePairs[i+1] );
		return map;
	}

	//---- components ---------------------------------------------------------

	@Test
	void button() {
		JButton b = new JButton();
		FlatButtonUI ui = (FlatButtonUI) b.getUI();

		// JComponent properties
		ui.applyStyle( b, "background: #fff" );
		ui.applyStyle( b, "foreground: #fff" );
		ui.applyStyle( b, "border: 2,2,2,2,#f00" );
		ui.applyStyle( b, "font: italic 12 monospaced" );

		// AbstractButton properties
		ui.applyStyle( b, "margin: 2,2,2,2" );
		ui.applyStyle( b, "iconTextGap: 4" );
	}

	//---- enums --------------------------------------------------------------

	enum SomeEnum { enumValue1, enumValue2 }

	static class ClassWithEnum {
		SomeEnum enum1;
	}

	@Test
	void enumField() {
		ClassWithEnum c = new ClassWithEnum();
		FlatStylingSupport.applyToField( c, "enum1", "enum1", "enumValue1" );
		FlatStylingSupport.applyToField( c, "enum1", "enum1", "enumValue2" );
	}

	@Test
	void enumProperty() {
		JList<Object> c = new JList<>();
		FlatListUI ui = (FlatListUI) c.getUI();
		ui.applyStyle( "dropMode: INSERT" );
	}

	@Test
	void enumUIDefaults() {
		UIManager.put( "test.enum", SomeEnum.enumValue1.toString() );
		assertEquals( SomeEnum.enumValue1, FlatUIUtils.getUIEnum( "test.enum", SomeEnum.class, null ) );

		UIManager.put( "test.enum", "unknown" );
		assertEquals( null, FlatUIUtils.getUIEnum( "test.enum", SomeEnum.class, null ) );

		UIManager.put( "test.enum", null );
		assertEquals( SomeEnum.enumValue1, FlatUIUtils.getUIEnum( "test.enum", SomeEnum.class, SomeEnum.enumValue1 ) );
	}

	//---- class CustomIcon ---------------------------------------------------

	static class CustomIcon
		implements Icon
	{
		@Override public void paintIcon( Component c, Graphics g, int x, int y ) {}
		@Override public int getIconWidth() { return 1; }
		@Override public int getIconHeight() { return 1; }
	}

	//---- class CustomCheckBoxIcon ----------------------------------------

	static class CustomCheckBoxIcon
		extends FlatCheckBoxIcon
	{
		CustomCheckBoxIcon() {
			background = Color.green;
		}
	}

	//---- class CustomRadioButtonIcon ----------------------------------------

	static class CustomRadioButtonIcon
		extends FlatRadioButtonIcon
	{
		CustomRadioButtonIcon() {
			background = Color.green;
		}
	}
}
