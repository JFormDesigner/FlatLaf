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

package com.formdev.flatlaf.fonts.roboto;

import com.formdev.flatlaf.util.FontUtils;

/**
 * The Roboto font family.
 * <p>
 * Font home page: <a href="https://fonts.google.com/specimen/Roboto">https://fonts.google.com/specimen/Roboto</a><br>
 * GitHub project: <a href="https://github.com/googlefonts/roboto">https://github.com/googlefonts/roboto</a>
 * <p>
 * To install the font, invoke following once (e.g. in your {@code main()} method; on AWT thread).
 * <p>
 * For lazy loading use:
 * <pre>{@code
 * FlatRobotoFont.installLazy();
 * }</pre>
 * <p>
 * Or load immediately with:
 * <pre>{@code
 * FlatRobotoFont.install();
 * // or
 * FlatRobotoFont.installBasic();
 * FlatRobotoFont.installLight();
 * FlatRobotoFont.installSemiBold();
 * }</pre>
 * <p>
 * Use as application font (invoke before setting up FlatLaf):
 * <pre>{@code
 * FlatLaf.setPreferredFontFamily( FlatRobotoFont.FAMILY );
 * FlatLaf.setPreferredLightFontFamily( FlatRobotoFont.FAMILY_LIGHT );
 * FlatLaf.setPreferredSemiboldFontFamily( FlatRobotoFont.FAMILY_SEMIBOLD );
 * }</pre>
 * <p>
 * Create single fonts:
 * <pre>{@code
 * new Font( FlatRobotoFont.FAMILY, Font.PLAIN, 12 );
 * new Font( FlatRobotoFont.FAMILY, Font.ITALIC, 12 );
 * new Font( FlatRobotoFont.FAMILY, Font.BOLD, 12 );
 * new Font( FlatRobotoFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
 * new Font( FlatRobotoFont.FAMILY_LIGHT, Font.PLAIN, 12 );
 * new Font( FlatRobotoFont.FAMILY_LIGHT, Font.ITALIC, 12 );
 * new Font( FlatRobotoFont.FAMILY_SEMIBOLD, Font.PLAIN, 12 );
 * new Font( FlatRobotoFont.FAMILY_SEMIBOLD, Font.ITALIC, 12 );
 * }</pre>
 * <p>
 * If using lazy loading, invoke one of following before creating the font:
 * <pre>{@code
 * FontUtils.loadFontFamily( FlatRobotoFont.FAMILY );
 * FontUtils.loadFontFamily( FlatRobotoFont.FAMILY_LIGHT );
 * FontUtils.loadFontFamily( FlatRobotoFont.FAMILY_SEMIBOLD );
 * }</pre>
 * <p>
 * E.g.:
 * <pre>{@code
 * FontUtils.loadFontFamily( FlatRobotoFont.FAMILY );
 * Font font = new Font( FlatRobotoFont.FAMILY, Font.PLAIN, 12 );
 * }</pre>
 * <p>
 * Or use following:
 * <pre>{@code
 * Font font = FontUtils.getCompositeFont( FlatRobotoFont.FAMILY, Font.PLAIN, 12 );
 * }</pre>
 *
 * @author Karl Tauber
 */
public class FlatRobotoFont
{
	/**
	 * Family name for basic styles (regular, italic and bold).
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatRobotoFont.FAMILY, Font.PLAIN, 12 );
	 * new Font( FlatRobotoFont.FAMILY, Font.ITALIC, 12 );
	 * new Font( FlatRobotoFont.FAMILY, Font.BOLD, 12 );
	 * new Font( FlatRobotoFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
	 * }</pre>
	 */
	public static final String FAMILY = "Roboto";

	/**
	 * Family name for light styles.
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatRobotoFont.FAMILY_LIGHT, Font.PLAIN, 12 );
	 * new Font( FlatRobotoFont.FAMILY_LIGHT, Font.ITALIC, 12 );
	 * }</pre>
	 */
	public static final String FAMILY_LIGHT = "Roboto Light";

	/**
	 * Family name for semibold (medium) styles.
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatRobotoFont.FAMILY_SEMIBOLD, Font.PLAIN, 12 );
	 * new Font( FlatRobotoFont.FAMILY_SEMIBOLD, Font.ITALIC, 12 );
	 * }</pre>
	 */
	public static final String FAMILY_SEMIBOLD = "Roboto Medium";

	/**
	 * Use for {@link #installStyle(String)} to install single font style.
	 */
	public static final String
		// basic styles
		STYLE_REGULAR = "Roboto-Regular.ttf",
		STYLE_ITALIC = "Roboto-Italic.ttf",
		STYLE_BOLD = "Roboto-Bold.ttf",
		STYLE_BOLD_ITALIC = "Roboto-BoldItalic.ttf",

		// light
		STYLE_LIGHT = "Roboto-Light.ttf",
		STYLE_LIGHT_ITALIC = "Roboto-LightItalic.ttf",

		// semibold
		STYLE_SEMIBOLD = "Roboto-Medium.ttf",
		STYLE_SEMIBOLD_ITALIC = "Roboto-MediumItalic.ttf";


	private FlatRobotoFont() {}

	/**
	 * Registers the fonts for lazy loading via {@link FontUtils#registerFontFamilyLoader(String, Runnable)}.
	 * <p>
	 * This is the preferred method (when using FlatLaf) to avoid unnecessary loading of maybe unused fonts.
	 * <p>
	 * <strong>Note</strong>: When using '{@code new Font(...)}', you need to first invoke
	 * {@link FontUtils#loadFontFamily(String)} to ensure that the font family is loaded.
	 * When FlatLaf loads a font, or when using {@link FontUtils#getCompositeFont(String, int, int)},
	 * this is done automatically.
	 */
	public static void installLazy() {
		FontUtils.registerFontFamilyLoader( FAMILY, FlatRobotoFont::installBasic );
		FontUtils.registerFontFamilyLoader( FAMILY_LIGHT, FlatRobotoFont::installLight );
		FontUtils.registerFontFamilyLoader( FAMILY_SEMIBOLD, FlatRobotoFont::installSemiBold );
	}

	/**
	 * Creates and registers the fonts for all styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void install() {
		installBasic();
		installLight();
		installSemiBold();
	}

	/**
	 * Creates and registers the fonts for basic styles (regular, italic and bold).
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installBasic() {
		installStyle( STYLE_REGULAR );
		installStyle( STYLE_ITALIC );
		installStyle( STYLE_BOLD );
		installStyle( STYLE_BOLD_ITALIC );
	}

	/**
	 * Creates and registers the fonts for light styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installLight() {
		installStyle( STYLE_LIGHT );
		installStyle( STYLE_LIGHT_ITALIC );
	}

	/**
	 * Creates and registers the fonts for semibold (medium) styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void installSemiBold() {
		installStyle( STYLE_SEMIBOLD );
		installStyle( STYLE_SEMIBOLD_ITALIC );
	}

	/**
	 * Creates and registers the font for the given style.
	 * See {@code STYLE_} constants.
	 */
	public static boolean installStyle( String name ) {
		return FontUtils.installFont( FlatRobotoFont.class.getResource( name ) );
	}
}
