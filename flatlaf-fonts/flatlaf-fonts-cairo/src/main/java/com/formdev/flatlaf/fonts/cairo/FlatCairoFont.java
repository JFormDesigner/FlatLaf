/*
 * Copyright 2022 FormDev Software GmbH
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

package com.formdev.flatlaf.fonts.cairo;

import com.formdev.flatlaf.util.FontUtils;

/**
 * The Cairo font family.
 * <p>
 * Font home page: <a href="https://fonts.google.com/specimen/Cairo">https://fonts.google.com/specimen/Cairo</a>
 * <p>
 * To install the font, invoke following once (e.g. in your {@code main()} method; on AWT thread).
 * <p>
 * For lazy loading use:
 * <pre>{@code
 * FlatCairoFont.installLazy();
 * }</pre>
 * <p>
 * Or load immediately with:
 * <pre>{@code
 * FlatCairoFont.install();
 * // or
 * FlatCairoFont.installBasic();
 * FlatCairoFont.installLight();
 * FlatCairoFont.installSemiBold();
 * }</pre>
 * <p>
 * Use as application font (invoke before setting up FlatLaf):
 * <pre>{@code
 * FlatLaf.setPreferredFontFamily( FlatCairoFont.FAMILY );
 * FlatLaf.setPreferredLightFontFamily( FlatCairoFont.FAMILY_LIGHT );
 * FlatLaf.setPreferredSemiboldFontFamily( FlatCairoFont.FAMILY_SEMIBOLD );
 * }</pre>
 *
 * @author Mohammed Al Zahrani
 */
public class FlatCairoFont
{
	/**
	 * Family name for basic styles (regular and bold).
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatCairoFont.FAMILY, Font.PLAIN, 12 );
	 * new Font( FlatCairoFont.FAMILY, Font.BOLD, 12 );
	 * }</pre>
	 */
	public static final String FAMILY = "Cairo";

	/**
	 * Family name for light styles.
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatCairoFont.FAMILY_LIGHT, Font.PLAIN, 12 );
	 * }</pre>
	 */
	public static final String FAMILY_LIGHT = "Cairo Light";

	/**
	 * Family name for semibold styles.
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatCairoFont.FAMILY_SEMIBOLD, Font.PLAIN, 12 );
	 * }</pre>
	 */
	public static final String FAMILY_SEMIBOLD = "Cairo SemiBold";

	/**
	 * Optional family names for other weights (if you want to reference them explicitly).
	 */
	public static final String
		FAMILY_EXTRALIGHT = "Cairo ExtraLight",
		FAMILY_MEDIUM     = "Cairo Medium",
		FAMILY_BOLD       = "Cairo Bold",
		FAMILY_EXTRABOLD  = "Cairo ExtraBold",
		FAMILY_BLACK      = "Cairo Black";

	/**
	 * Use for {@link #installStyle(String)} to install single font style.
	 */
	public static final String
		STYLE_REGULAR     = "Cairo-Regular.ttf",
		STYLE_EXTRALIGHT  = "Cairo-ExtraLight.ttf",
		STYLE_LIGHT       = "Cairo-Light.ttf",
		STYLE_MEDIUM      = "Cairo-Medium.ttf",
		STYLE_SEMIBOLD    = "Cairo-SemiBold.ttf",
		STYLE_BOLD        = "Cairo-Bold.ttf",
		STYLE_EXTRABOLD   = "Cairo-ExtraBold.ttf",
		STYLE_BLACK       = "Cairo-Black.ttf";

	private FlatCairoFont() {}

	/**
	 * Registers the font family loaders for lazy loading.
	 */
	public static void installLazy() {
		FontUtils.registerFontFamilyLoader( FAMILY, FlatCairoFont::installBasic );
		FontUtils.registerFontFamilyLoader( FAMILY_LIGHT, FlatCairoFont::installLight );
		FontUtils.registerFontFamilyLoader( FAMILY_SEMIBOLD, FlatCairoFont::installSemiBold );

		// optional extra families
		FontUtils.registerFontFamilyLoader( FAMILY_EXTRALIGHT, FlatCairoFont::installExtraLight );
		FontUtils.registerFontFamilyLoader( FAMILY_MEDIUM, FlatCairoFont::installMedium );
		FontUtils.registerFontFamilyLoader( FAMILY_BOLD, FlatCairoFont::installBold );
		FontUtils.registerFontFamilyLoader( FAMILY_EXTRABOLD, FlatCairoFont::installExtraBold );
		FontUtils.registerFontFamilyLoader( FAMILY_BLACK, FlatCairoFont::installBlack );
	}

	/**
	 * Creates and registers the fonts for all styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void install() {
		installExtraLight();
		installLight();
		installBasic();
		installMedium();
		installSemiBold();
		installBold();
		installExtraBold();
		installBlack();
	}

	/**
	 * Creates and registers the fonts for basic styles.
	 * (Here: regular + bold)
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installBasic() {
		installStyle( STYLE_REGULAR );
		installStyle( STYLE_BOLD );
	}

	/**
	 * Creates and registers the fonts for extra light styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installExtraLight() {
		installStyle( STYLE_EXTRALIGHT );
	}

	/**
	 * Creates and registers the fonts for light styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installLight() {
		installStyle( STYLE_LIGHT );
	}

	/**
	 * Creates and registers the fonts for medium styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installMedium() {
		installStyle( STYLE_MEDIUM );
	}

	/**
	 * Creates and registers the fonts for semibold styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installSemiBold() {
		installStyle( STYLE_SEMIBOLD );
	}

	/**
	 * Creates and registers the fonts for bold styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installBold() {
		installStyle( STYLE_BOLD );
	}

	/**
	 * Creates and registers the fonts for extra bold styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installExtraBold() {
		installStyle( STYLE_EXTRABOLD );
	}

	/**
	 * Creates and registers the fonts for black styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installBlack() {
		installStyle( STYLE_BLACK );
	}

	/**
	 * Creates and registers the font for the given style.
	 * See {@code STYLE_} constants.
	 */
	public static boolean installStyle( String name ) {
		return FontUtils.installFont( FlatCairoFont.class.getResource( name ) );
	}
}
