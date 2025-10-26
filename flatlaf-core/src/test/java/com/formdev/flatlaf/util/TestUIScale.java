/*
 * Copyright 2025 FormDev Software GmbH
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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.awt.Font;
import java.util.Collections;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatSystemProperties;

/**
 * @author Karl Tauber
 */
public class TestUIScale
{
	private static Map<String, String> FONT_EXTRA_DEFAULTS_1x = Collections.singletonMap(
		"defaultFont", "{instance}java.awt.Font,Dialog,0,12" );
	private static Map<String, String> FONT_EXTRA_DEFAULTS_1_5x = Collections.singletonMap(
		"defaultFont", "{instance}java.awt.Font,Dialog,0,18" );

	@BeforeAll
	static void setup() {
		UIScale.inUnitTests = true;

		// disable platform specific fonts
		System.setProperty( "flatlaf.uiScale.fontSizeDivider", "12" );
		FlatLaf.setGlobalExtraDefaults( FONT_EXTRA_DEFAULTS_1x );
	}

	@AfterAll
	static void cleanup() throws UnsupportedLookAndFeelException {
		System.clearProperty( "flatlaf.uiScale.fontSizeDivider" );
		FlatLaf.setGlobalExtraDefaults( null );

		UIScale.inUnitTests = false;
	}

	@AfterEach
	void afterEach() throws UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel( new MetalLookAndFeel() );
		UIManager.put( "defaultFont", null );
		UIManager.put( "Label.font", null );
		FlatLaf.setGlobalExtraDefaults( FONT_EXTRA_DEFAULTS_1x );

		UIScale.tests_uninitialize();
	}

	@Test
	void testCustomScaleFactor() {
		System.setProperty( FlatSystemProperties.UI_SCALE, "1.25x" );
		assertScaleFactor( 1.25f );

		System.setProperty( FlatSystemProperties.UI_SCALE, "2x" );
		UIScale.tests_uninitialize();
		assertScaleFactor( 2f );

		System.clearProperty( FlatSystemProperties.UI_SCALE );
	}

	@Test
	void testLabelFontScaling() {
		assertInstanceOf( MetalLookAndFeel.class, UIManager.getLookAndFeel() );

		testLabelFont(  8, 1f );
		testLabelFont(  9, 1f );
		testLabelFont( 10, 1f );
		testLabelFont( 11, 1f );
		testLabelFont( 12, 1f );
		testLabelFont( 13, 1f );
		testLabelFont( 14, 1.25f );
		testLabelFont( 15, 1.25f );
		testLabelFont( 16, 1.25f );
		testLabelFont( 17, 1.5f );
		testLabelFont( 18, 1.5f );
		testLabelFont( 19, 1.5f );
		testLabelFont( 20, 1.75f );
		testLabelFont( 21, 1.75f );
		testLabelFont( 22, 1.75f );
		testLabelFont( 23, 2f );
		testLabelFont( 24, 2f );
		testLabelFont( 25, 2f );
		testLabelFont( 26, 2.25f );
	}

	private void testLabelFont( int fontSize, float expectedScaleFactor ) {
		UIManager.put( "Label.font", new Font( Font.DIALOG, Font.PLAIN, fontSize ) );
		assertScaleFactor( expectedScaleFactor );
	}

	@Test
	void testDefaultFontScaling() {
		FlatLightLaf.setup();

		testDefaultFont(  8, 1f );
		testDefaultFont(  9, 1f );
		testDefaultFont( 10, 1f );
		testDefaultFont( 11, 1f );
		testDefaultFont( 12, 1f );
		testDefaultFont( 13, 1f );
		testDefaultFont( 14, 1.25f );
		testDefaultFont( 15, 1.25f );
		testDefaultFont( 16, 1.25f );
		testDefaultFont( 17, 1.5f );
		testDefaultFont( 18, 1.5f );
		testDefaultFont( 19, 1.5f );
		testDefaultFont( 20, 1.75f );
		testDefaultFont( 21, 1.75f );
		testDefaultFont( 22, 1.75f );
		testDefaultFont( 23, 2f );
		testDefaultFont( 24, 2f );
		testDefaultFont( 25, 2f );
		testDefaultFont( 26, 2.25f );
	}

	private void testDefaultFont( int fontSize, float expectedScaleFactor ) {
		UIManager.put( "defaultFont", new Font( Font.DIALOG, Font.PLAIN, fontSize ) );
		assertScaleFactor( expectedScaleFactor );
	}

	@Test
	void testInitialScaleFactorAndFontSizes() {
		FlatLightLaf.setup();
		assertScaleFactorAndFontSizes( 1f, 12, -1 );

		FlatLaf.setGlobalExtraDefaults( FONT_EXTRA_DEFAULTS_1_5x );
		FlatDarkLaf.setup();
		assertScaleFactorAndFontSizes( 1.5f, 18, -1 );
	}

	@Test
	void zoom_Metal() {
		UIScale.setZoomFactor( 1.1f );
		assertScaleFactor( 1.1f );

		UIScale.setZoomFactor( 1.3f );
		assertScaleFactor( 1.3f );

		UIScale.setZoomFactor( 2.3f );
		assertScaleFactor( 2.3f );
	}

	@Test
	void zoom_1x() {
		FlatLightLaf.setup();
		testZoom( 0.7f,  0.7f,   8, -1 );
		testZoom( 0.75f, 0.75f,  9, -1 );
		testZoom( 0.8f,  0.8f,  10, -1 );
		testZoom( 0.9f,  0.9f,  11, -1 );
		testZoom( 1f,    1f,    12, -1 );
		testZoom( 1.1f,  1.1f,  13, -1 );
		testZoom( 1.2f,  1.2f,  14, -1 );
		testZoom( 1.25f, 1.25f, 15, -1 );
		testZoom( 1.3f,  1.3f,  16, -1 );
		testZoom( 1.4f,  1.4f,  17, -1 );
		testZoom( 1.5f,  1.5f,  18, -1 );
		testZoom( 1.6f,  1.6f,  19, -1 );
		testZoom( 1.7f,  1.7f,  20, -1 );
		testZoom( 1.75f, 1.75f, 21, -1 );
		testZoom( 1.8f,  1.8f,  22, -1 );
		testZoom( 1.9f,  1.9f,  23, -1 );
		testZoom( 2f,    2f,    24, -1 );
		testZoom( 2.25f, 2.25f, 27, -1 );
		testZoom( 2.5f,  2.5f,  30, -1 );
		testZoom( 2.75f, 2.75f, 33, -1 );
		testZoom( 3f,    3f,    36, -1 );
		testZoom( 4f,    4f,    48, -1 );
	}

	@Test
	void zoom_1_5x() {
		FlatLaf.setGlobalExtraDefaults( FONT_EXTRA_DEFAULTS_1_5x );
		FlatLightLaf.setup();

		testZoom( 0.7f,  1.05f, 13, -1 );
		testZoom( 0.75f, 1.13f, 14, -1 );
		testZoom( 0.8f,  1.2f,  14, -1 );
		testZoom( 0.9f,  1.35f, 16, -1 );
		testZoom( 1f,    1.5f,  18, -1 );
		testZoom( 1.1f,  1.65f, 20, -1 );
		testZoom( 1.2f,  1.8f,  22, -1 );
		testZoom( 1.25f, 1.88f, 23, -1 );
		testZoom( 1.3f,  1.95f, 23, -1 );
		testZoom( 1.4f,  2.1f,  25, -1 );
		testZoom( 1.5f,  2.25f, 27, -1 );
		testZoom( 1.6f,  2.4f,  29, -1 );
		testZoom( 1.7f,  2.55f, 31, -1 );
		testZoom( 1.75f, 2.63f, 32, -1 );
		testZoom( 1.8f,  2.7f,  32, -1 );
		testZoom( 1.9f,  2.85f, 34, -1 );
		testZoom( 2f,    3f,    36, -1 );
		testZoom( 2.25f, 3.38f, 41, -1 );
		testZoom( 2.5f,  3.75f, 45, -1 );
		testZoom( 2.75f, 4.13f, 50, -1 );
		testZoom( 3f,    4.5f,  54, -1 );
		testZoom( 4f,    6f,    72, -1 );
	}

	@Test
	void zoomAppFont_1x() {
		FlatLightLaf.setup();
		UIManager.put( "defaultFont", new Font( Font.DIALOG, Font.PLAIN, 14 ) );

		testZoom( 1f,    1.25f, 12, 14 );
		testZoom( 1.1f,  1.38f, 13, 15 );
		testZoom( 1.25f, 1.56f, 15, 17 );
		testZoom( 1.5f,  1.88f, 18, 20 );
		testZoom( 1.75f, 2.19f, 21, 23 );
		testZoom( 2f,    2.5f,  24, 26 );
		testZoom( 1f,    1.25f, 12, 13 );
		testZoom( 2f,    2.5f,  24, 26 );
	}

	@Test
	void zoomWithLafChange() {
		FlatLightLaf.setup();
		assertScaleFactorAndFontSizes( 1f, 12, -1 );
		testZoom( 1.1f,  1.1f,  13, -1 );

		FlatDarkLaf.setup();
		assertScaleFactorAndFontSizes( 1.1f, 13, -1 );
		testZoom( 1.2f,  1.2f,  14, -1 );

		FlatLightLaf.setup();
		assertScaleFactorAndFontSizes( 1.2f, 14, -1 );
		testZoom( 1.3f,  1.3f,  16, -1 );

		FlatLaf.setGlobalExtraDefaults( FONT_EXTRA_DEFAULTS_1_5x );
		FlatDarkLaf.setup();
		assertScaleFactorAndFontSizes( 1.95f, 23, -1 );
		testZoom( 1.4f,  2.1f,  25, -1 );

		FlatLightLaf.setup();
		assertScaleFactorAndFontSizes( 2.1f, 25, -1 );
		testZoom( 1.5f,  2.25f,  27, -1 );
	}

	@Test
	void zoomWithDefaultFontChange() {
		FlatLightLaf.setup();
		assertScaleFactorAndFontSizes( 1f, 12, -1 );

		float zoom1 = 1.4f;
		testZoom( zoom1,  zoom1,  17, -1 );
		testDefaultFont(  8, z( zoom1, 1f ) );
		testDefaultFont(  9, z( zoom1, 1f ) );
		testDefaultFont( 10, z( zoom1, 1f ) );
		testDefaultFont( 11, z( zoom1, 1f ) );
		testDefaultFont( 12, z( zoom1, 1f ) );
		testDefaultFont( 13, z( zoom1, 1f ) );
		testDefaultFont( 14, z( zoom1, 1.25f ) );
		testDefaultFont( 15, z( zoom1, 1.25f ) );
		testDefaultFont( 16, z( zoom1, 1.25f ) );
		testDefaultFont( 17, z( zoom1, 1.5f ) );
		testDefaultFont( 18, z( zoom1, 1.5f ) );
		testDefaultFont( 19, z( zoom1, 1.5f ) );
		testDefaultFont( 20, z( zoom1, 1.75f ) );
		testDefaultFont( 21, z( zoom1, 1.75f ) );
		testDefaultFont( 22, z( zoom1, 1.75f ) );
		testDefaultFont( 23, z( zoom1, 2f ) );
		testDefaultFont( 24, z( zoom1, 2f ) );
		testDefaultFont( 25, z( zoom1, 2f ) );
		testDefaultFont( 26, z( zoom1, 2.25f ) );

		float zoom2 = 1.8f;
		testZoom( zoom2,  4.05f,  22, 33 );
		testDefaultFont(  8, z( zoom2, 1f ) );
		testDefaultFont(  9, z( zoom2, 1f ) );
		testDefaultFont( 10, z( zoom2, 1f ) );
		testDefaultFont( 11, z( zoom2, 1f ) );
		testDefaultFont( 12, z( zoom2, 1f ) );
		testDefaultFont( 13, z( zoom2, 1f ) );
		testDefaultFont( 14, z( zoom2, 1.25f ) );
		testDefaultFont( 15, z( zoom2, 1.25f ) );
		testDefaultFont( 16, z( zoom2, 1.25f ) );
		testDefaultFont( 17, z( zoom2, 1.5f ) );
		testDefaultFont( 18, z( zoom2, 1.5f ) );
		testDefaultFont( 19, z( zoom2, 1.5f ) );
		testDefaultFont( 20, z( zoom2, 1.75f ) );
		testDefaultFont( 21, z( zoom2, 1.75f ) );
		testDefaultFont( 22, z( zoom2, 1.75f ) );
		testDefaultFont( 23, z( zoom2, 2f ) );
		testDefaultFont( 24, z( zoom2, 2f ) );
		testDefaultFont( 25, z( zoom2, 2f ) );
		testDefaultFont( 26, z( zoom2, 2.25f ) );
	}

	private static float z( float zoom, float scale ) {
		// round scale factor to 1/100
		return Math.round( (zoom * scale) * 100f ) / 100f;
	}

	private static void testZoom( float zoomFactor, float expectedScaleFactor,
		int expectedLafFontSize, int expectedAppFontSize )
	{
		UIScale.setZoomFactor( zoomFactor );
		assertScaleFactorAndFontSizes( expectedScaleFactor, expectedLafFontSize, expectedAppFontSize );
	}

	private static void assertScaleFactorAndFontSizes( float expectedScaleFactor,
		int expectedLafFontSize, int expectedAppFontSize )
	{
		assertScaleFactor( expectedScaleFactor );

		Font lafFont = UIManager.getLookAndFeelDefaults().getFont( "defaultFont" );
		Font appFont = UIManager.getFont( "defaultFont" );
		assertEquals( expectedLafFontSize, lafFont.getSize() );
		if( expectedAppFontSize > 0 ) {
			assertNotEquals( lafFont, appFont );
			assertEquals( expectedAppFontSize, appFont.getSize() );
		} else
			assertEquals( lafFont, appFont );
	}

	private static void assertScaleFactor( float expectedScaleFactor ) {
		assertEquals( expectedScaleFactor, UIScale.getUserScaleFactor() );
	}
}
