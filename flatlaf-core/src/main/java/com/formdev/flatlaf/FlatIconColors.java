/*
 * Copyright 2020 FormDev Software GmbH
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

/**
 * Default color palette for action icons and object icons.
 * <p>
 * The idea is to use only this well-defined set of colors in SVG icons, and
 * then they are replaced at runtime to dark variants or to other theme colors.
 * Then a single SVG icon (light variant) can be used for dark themes too.
 * IntelliJ Platform uses this mechanism to allow themes to change IntelliJ Platform icons.
 * <p>
 * Use the {@code *_DARK} colors only in {@code *_dark.svg} files.
 * <p>
 * The colors are based on IntelliJ Platform
 *   <a href="https://jetbrains.design/intellij/principles/icons/#action-icons">Action icons</a>
 * and
 *   <a href="https://jetbrains.design/intellij/principles/icons/#noun-icons">Noun icons</a>
 * <p>
 * These colors may be changed by IntelliJ Platform themes.
 * <p>
 * You may use these colors also in your application (outside of SVG icons), but do
 * not use the RGB values defined in this enum.<br>
 * Instead, use {@code UIManager.getColor( FlatIconColors.ACTIONS_GREY.key )}.
 *
 * @author Karl Tauber
 */
public enum FlatIconColors
{
	// colors for action icons
	// see https://jetbrains.design/intellij/principles/icons/#action-icons
	ACTIONS_RED				( 0xDB5860, "Actions.Red", true, false ),
	ACTIONS_RED_DARK		( 0xC75450, "Actions.Red", false, true ),
	ACTIONS_YELLOW			( 0xEDA200, "Actions.Yellow", true, false ),
	ACTIONS_YELLOW_DARK		( 0xF0A732, "Actions.Yellow", false, true ),
	ACTIONS_GREEN			( 0x59A869, "Actions.Green", true, false ),
	ACTIONS_GREEN_DARK		( 0x499C54, "Actions.Green", false, true ),
	ACTIONS_BLUE			( 0x389FD6, "Actions.Blue", true, false ),
	ACTIONS_BLUE_DARK		( 0x3592C4, "Actions.Blue", false, true ),
	ACTIONS_GREY			( 0x6E6E6E, "Actions.Grey", true, false ),
	ACTIONS_GREY_DARK		( 0xAFB1B3, "Actions.Grey", false, true ),
	ACTIONS_GREYINLINE		( 0x7F8B91, "Actions.GreyInline", true, false ),
	ACTIONS_GREYINLINE_DARK	( 0x7F8B91, "Actions.GreyInline", false, true ),

	// colors for object icons
	// see https://jetbrains.design/intellij/principles/icons/#noun-icons
	OBJECTS_GREY			( 0x9AA7B0, "Objects.Grey" ),
	OBJECTS_BLUE			( 0x40B6E0, "Objects.Blue" ),
	OBJECTS_GREEN			( 0x62B543, "Objects.Green" ),
	OBJECTS_YELLOW			( 0xF4AF3D, "Objects.Yellow" ),
	OBJECTS_YELLOW_DARK		( 0xD9A343, "Objects.YellowDark" ),
	OBJECTS_PURPLE			( 0xB99BF8, "Objects.Purple" ),
	OBJECTS_PINK			( 0xF98B9E, "Objects.Pink" ),
	OBJECTS_RED				( 0xF26522, "Objects.Red" ),
	OBJECTS_RED_STATUS		( 0xE05555, "Objects.RedStatus" ),
	OBJECTS_GREEN_ANDROID	( 0xA4C639, "Objects.GreenAndroid" ),
	OBJECTS_BLACK_TEXT		( 0x231F20, "Objects.BlackText" );

	public final int rgb;
	public final String key;

	public final boolean light;
	public final boolean dark;

	FlatIconColors( int rgb, String key ) {
		this( rgb, key, true, true );
	}

	FlatIconColors( int rgb, String key, boolean light, boolean dark ) {
		this.rgb = rgb;
		this.key = key;
		this.light = light;
		this.dark = dark;
	}
}
