/*
 * Copyright 2026 FormDev Software GmbH
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

package com.formdev.flatlaf.swingx.ui;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;
import java.awt.Insets;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXMonthView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatBorder;

/**
 * @author Karl Tauber
 */
public class TestFlatSwingXStyling
{
	@BeforeAll
	static void setup() throws UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel( new FlatLightLaf() );
	}

	@AfterAll
	static void cleanup() throws UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel( new javax.swing.plaf.metal.MetalLookAndFeel() );
	}

	@Test
	void datePicker() {
		JXDatePicker c = new JXDatePicker();
		FlatDatePickerUI ui = (FlatDatePickerUI) c.getUI();

		// FlatDatePickerUI
		c.putClientProperty( FlatClientProperties.STYLE, "padding: 1,2,3,4" );
		assertEquals( new Insets( 1, 2, 3, 4 ), ui.padding );
		c.putClientProperty( FlatClientProperties.STYLE, "arrowType: chevron" );
		assertEquals( "chevron", ui.arrowType );
		c.putClientProperty( FlatClientProperties.STYLE, "disabledBackground: #fff" );
		assertEquals( Color.white, ui.disabledBackground );
		c.putClientProperty( FlatClientProperties.STYLE, "buttonBackground: #fff" );
		assertEquals( Color.white, ui.buttonBackground );
		c.putClientProperty( FlatClientProperties.STYLE, "buttonArrowColor: #fff" );
		assertEquals( Color.white, ui.buttonArrowColor );
		c.putClientProperty( FlatClientProperties.STYLE, "buttonDisabledArrowColor: #fff" );
		assertEquals( Color.white, ui.buttonDisabledArrowColor );
		c.putClientProperty( FlatClientProperties.STYLE, "buttonHoverArrowColor: #fff" );
		assertEquals( Color.white, ui.buttonHoverArrowColor );
		c.putClientProperty( FlatClientProperties.STYLE, "buttonPressedArrowColor: #fff" );
		assertEquals( Color.white, ui.buttonPressedArrowColor );

		// FlatDatePickerBorder (also used for separator line)
		Color defaultBorderColor = ui.borderColor;
		c.putClientProperty( FlatClientProperties.STYLE, "borderColor: #f00; disabledBorderColor: #0f0; arc: 10" );
		assertEquals( Color.red, ui.borderColor );
		assertEquals( Color.green, ui.disabledBorderColor );
		assertEquals( Color.red, ui.getStyleableValue( c, "borderColor" ) );
		assertEquals( 10, ui.getStyleableValue( c, "arc" ) );
		assertTrue( c.getBorder() instanceof FlatDatePickerBorder );

		// reset style
		c.putClientProperty( FlatClientProperties.STYLE, null );
		assertEquals( defaultBorderColor, ui.borderColor );
		assertNotEquals( new Insets( 1, 2, 3, 4 ), ui.padding );

		// JComponent properties
		c.putClientProperty( FlatClientProperties.STYLE, "background: #fff; foreground: #000" );
		assertEquals( Color.white, c.getBackground() );
		assertEquals( Color.black, c.getForeground() );

		// styleable infos
		Map<String, Class<?>> infos = ui.getStyleableInfos( c );
		assertEquals( Insets.class, infos.get( "padding" ) );
		assertEquals( String.class, infos.get( "arrowType" ) );
		assertEquals( Color.class, infos.get( "buttonBackground" ) );
		assertEquals( Color.class, infos.get( "borderColor" ) );
		assertTrue( infos.keySet().containsAll( new FlatBorder().getStyleableInfos().keySet() ) );
	}

	@Test
	void busyLabel() {
		JXBusyLabel c = new JXBusyLabel();
		FlatBusyLabelUI ui = (FlatBusyLabelUI) c.getUI();

		Color defaultDisabledForeground = ui.disabledForeground;
		c.putClientProperty( FlatClientProperties.STYLE, "disabledForeground: #f00; foreground: #0f0" );
		assertEquals( Color.red, ui.disabledForeground );
		assertEquals( Color.green, c.getForeground() );
		assertEquals( Color.red, ui.getStyleableValue( c, "disabledForeground" ) );
		assertEquals( Color.class, ui.getStyleableInfos( c ).get( "disabledForeground" ) );

		c.putClientProperty( FlatClientProperties.STYLE, null );
		assertEquals( defaultDisabledForeground, ui.disabledForeground );
	}

	@Test
	void hyperlink() {
		JXHyperlink c = new JXHyperlink();
		FlatHyperlinkUI ui = (FlatHyperlinkUI) c.getUI();

		Color defaultDisabledText = ui.disabledText;
		c.putClientProperty( FlatClientProperties.STYLE, "disabledText: #f00; unclickedColor: #0f0; clickedColor: #00f" );
		assertEquals( Color.red, ui.disabledText );
		assertEquals( Color.green, c.getUnclickedColor() );
		assertEquals( Color.blue, c.getClickedColor() );
		assertEquals( Color.class, ui.getStyleableInfos( c ).get( "disabledText" ) );

		c.putClientProperty( FlatClientProperties.STYLE, null );
		assertEquals( defaultDisabledText, ui.disabledText );
	}

	@Test
	void monthView() {
		JXMonthView c = new JXMonthView();
		FlatMonthViewUI ui = (FlatMonthViewUI) c.getUI();

		c.putClientProperty( FlatClientProperties.STYLE, "todayColor: #f00; todayBackground: #0f0; boxPaddingX: 5" );
		assertEquals( Color.red, ui.todayColor );
		assertEquals( Color.green, c.getTodayBackground() );
		assertEquals( 5, c.getBoxPaddingX() );
		assertEquals( Color.class, ui.getStyleableInfos( c ).get( "todayColor" ) );
	}
}
