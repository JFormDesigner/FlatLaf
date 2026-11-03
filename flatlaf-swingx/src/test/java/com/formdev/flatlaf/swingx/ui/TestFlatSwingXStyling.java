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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.JXTitledPanel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.UIScale;

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

	@Test
	void taskPane() {
		JXTaskPane c = new JXTaskPane();
		FlatTaskPaneUI ui = (FlatTaskPaneUI) c.getUI();

		// BasicTaskPaneUI fields
		c.putClientProperty( FlatClientProperties.STYLE, "titleHeight: 30; roundHeight: 8" );
		assertEquals( 30, ui.getStyleableValue( c, "titleHeight" ) );
		assertEquals( 8, ui.getStyleableValue( c, "roundHeight" ) );

		// pane border fields
		Object defaultBorderColor = ui.getStyleableValue( c, "borderColor" );
		c.putClientProperty( FlatClientProperties.STYLE,
			"borderColor: #f00; titleForeground: #0f0; specialTitleBackground: #00f; specialTitleForeground: #ff0;"
			+ "titleBackgroundGradientStart: #0ff; titleOver: #f0f; specialTitleOver: #fff" );
		assertEquals( Color.red, ui.getStyleableValue( c, "borderColor" ) );
		assertEquals( Color.green, ui.getStyleableValue( c, "titleForeground" ) );
		assertEquals( Color.blue, ui.getStyleableValue( c, "specialTitleBackground" ) );
		assertEquals( Color.yellow, ui.getStyleableValue( c, "specialTitleForeground" ) );
		assertEquals( Color.cyan, ui.getStyleableValue( c, "titleBackgroundGradientStart" ) );
		assertEquals( Color.magenta, ui.getStyleableValue( c, "titleOver" ) );
		assertEquals( Color.white, ui.getStyleableValue( c, "specialTitleOver" ) );

		c.putClientProperty( FlatClientProperties.STYLE, null );
		assertEquals( defaultBorderColor, ui.getStyleableValue( c, "borderColor" ) );

		Map<String, Class<?>> infos = ui.getStyleableInfos( c );
		assertEquals( int.class, infos.get( "titleHeight" ) );
		assertEquals( int.class, infos.get( "roundHeight" ) );
		assertEquals( Color.class, infos.get( "borderColor" ) );
		assertEquals( Color.class, infos.get( "specialTitleOver" ) );
	}

	@Test
	void tipOfTheDay() {
		JXTipOfTheDay c = new JXTipOfTheDay();
		FlatTipOfTheDayUI ui = (FlatTipOfTheDayUI) c.getUI();

		JComponent tipArea = (JComponent) ((BorderLayout)c.getLayout()).getLayoutComponent( BorderLayout.CENTER );

		Insets defaultTipAreaInsets = ui.tipAreaInsets;
		c.putClientProperty( FlatClientProperties.STYLE, "tipAreaInsets: 1,2,3,4; background: #f00" );
		assertEquals( new Insets( 1, 2, 3, 4 ), ui.tipAreaInsets );
		assertEquals( UIScale.scale( new Insets( 1, 2, 3, 4 ) ), tipArea.getInsets() );
		assertEquals( Color.red, c.getBackground() );
		assertEquals( Insets.class, ui.getStyleableInfos( c ).get( "tipAreaInsets" ) );

		c.putClientProperty( FlatClientProperties.STYLE, null );
		assertEquals( defaultTipAreaInsets, ui.tipAreaInsets );
		assertEquals( UIScale.scale( defaultTipAreaInsets ), tipArea.getInsets() );
	}

	@Test
	void titledPanel() {
		JXTitledPanel c = new JXTitledPanel();
		FlatTitledPanelUI ui = (FlatTitledPanelUI) c.getUI();

		c.putClientProperty( FlatClientProperties.STYLE,
			"titleBackground: #f00; borderColor: #0f0; captionInsets: 1,2,3,4; titleForeground: #00f" );
		assertEquals( Color.red, ui.titleBackground );
		assertEquals( Color.green, ui.borderColor );
		assertEquals( new Insets( 1, 2, 3, 4 ), ui.captionInsets );
		assertEquals( Color.blue, c.getTitleForeground() );
		assertEquals( Color.green, ((FlatLineBorder)c.getBorder()).getLineColor() );

		Map<String, Class<?>> infos = ui.getStyleableInfos( c );
		assertEquals( Color.class, infos.get( "titleBackground" ) );
		assertEquals( Color.class, infos.get( "borderColor" ) );
		assertEquals( Insets.class, infos.get( "captionInsets" ) );
	}
}
