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

package com.formdev.flatlaf.fonts.inter;

import com.formdev.flatlaf.util.FontUtils;

/**
 * The Inter font family.
 * <p>
 * <strong>Note</strong>: This font does not work correctly in older Java 8 versions
 * (before 8u212) and in Java 9 because it is displayed way too large.
 * <p>
 * Font home page: <a href="https://rsms.me/inter/">https://rsms.me/inter/</a><br>
 * GitHub project: <a href="https://github.com/rsms/inter">https://github.com/rsms/inter</a>
 * <p>
 * To install the font, invoke following once (e.g. in your {@code main()} method; on AWT thread).
 * <p>
 * For lazy loading use:
 * <pre>{@code
 * FlatInterFont.installLazy();
 * }</pre>
 * <p>
 * Or load immediately with:
 * <pre>{@code
 * FlatInterFont.install();
 * // or
 * FlatInterFont.installBasic();
 * FlatInterFont.installLight();
 * FlatInterFont.installSemiBold();
 * }</pre>
 * <p>
 * Use as application font (invoke before setting up FlatLaf):
 * <pre>{@code
 * FlatLaf.setPreferredFontFamily( FlatInterFont.FAMILY );
 * FlatLaf.setPreferredLightFontFamily( FlatInterFont.FAMILY_LIGHT );
 * FlatLaf.setPreferredSemiboldFontFamily( FlatInterFont.FAMILY_SEMIBOLD );
 * }</pre>
 * <p>
 * Create single fonts:
 * <pre>{@code
 * new Font( FlatInterFont.FAMILY, Font.PLAIN, 12 );
 * new Font( FlatInterFont.FAMILY, Font.ITALIC, 12 );
 * new Font( FlatInterFont.FAMILY, Font.BOLD, 12 );
 * new Font( FlatInterFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
 * new Font( FlatInterFont.FAMILY_LIGHT, Font.PLAIN, 12 );
 * new Font( FlatInterFont.FAMILY_LIGHT, Font.ITALIC, 12 );
 * new Font( FlatInterFont.FAMILY_SEMIBOLD, Font.PLAIN, 12 );
 * new Font( FlatInterFont.FAMILY_SEMIBOLD, Font.ITALIC, 12 );
 * }</pre>
 * <p>
 * If using lazy loading, invoke one of following before creating the font:
 * <pre>{@code
 * FontUtils.loadFontFamily( FlatInterFont.FAMILY );
 * FontUtils.loadFontFamily( FlatInterFont.FAMILY_LIGHT );
 * FontUtils.loadFontFamily( FlatInterFont.FAMILY_SEMIBOLD );
 * }</pre>
 * <p>
 * E.g.:
 * <pre>{@code
 * FontUtils.loadFontFamily( FlatInterFont.FAMILY );
 * Font font = new Font( FlatInterFont.FAMILY, Font.PLAIN, 12 );
 * }</pre>
 * <p>
 * Or use following:
 * <pre>{@code
 * Font font = FontUtils.getCompositeFont( FlatInterFont.FAMILY, Font.PLAIN, 12 );
 * }</pre>
 *
 * @author Karl Tauber
 */
public class FlatInterFont
{
	/**
	 * Family name for basic styles (regular, italic and bold).
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatInterFont.FAMILY, Font.PLAIN, 12 );
	 * new Font( FlatInterFont.FAMILY, Font.ITALIC, 12 );
	 * new Font( FlatInterFont.FAMILY, Font.BOLD, 12 );
	 * new Font( FlatInterFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
	 * }</pre>
	 */
	public static final String FAMILY = "Inter";

	/**
	 * Family name for light styles.
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatInterFont.FAMILY_LIGHT, Font.PLAIN, 12 );
	 * new Font( FlatInterFont.FAMILY_LIGHT, Font.ITALIC, 12 );
	 * }</pre>
	 */
	public static final String FAMILY_LIGHT = "Inter Light";

	/**
	 * Family name for semibold styles.
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatInterFont.FAMILY_SEMIBOLD, Font.PLAIN, 12 );
	 * new Font( FlatInterFont.FAMILY_SEMIBOLD, Font.ITALIC, 12 );
	 * }</pre>
	 */
	public static final String FAMILY_SEMIBOLD = "Inter SemiBold";

	/**
	 * Use for {@link #installStyle(String)} to install single font style.
	 */
	public static final String
		// basic styles
		STYLE_REGULAR = "Inter-Regular.otf",
		STYLE_ITALIC = "Inter-Italic.otf",
		STYLE_BOLD = "Inter-Bold.otf",
		STYLE_BOLD_ITALIC = "Inter-BoldItalic.otf",

		// light
		STYLE_LIGHT = "Inter-Light.otf",
		STYLE_LIGHT_ITALIC = "Inter-LightItalic.otf",

		// semibold
		STYLE_SEMIBOLD = "Inter-SemiBold.otf",
		STYLE_SEMIBOLD_ITALIC = "Inter-SemiBoldItalic.otf";


	private FlatInterFont() {}

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
		FontUtils.registerFontFamilyLoader( FAMILY, FlatInterFont::installBasic );
		FontUtils.registerFontFamilyLoader( FAMILY_LIGHT, FlatInterFont::installLight );
		FontUtils.registerFontFamilyLoader( FAMILY_SEMIBOLD, FlatInterFont::installSemiBold );
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
	 * Creates and registers the fonts for semibold styles.
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
		return FontUtils.installFont( FlatInterFont.class.getResource( name ) );
	}
}
