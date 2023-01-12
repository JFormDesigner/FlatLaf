/*
 * Copyright 2023 FormDev Software GmbH
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

package com.formdev.flatlaf.fonts.roboto_mono;

import com.formdev.flatlaf.util.FontUtils;

/**
 * The Roboto Mono font family.
 * <p>
 * Font home page: <a href="https://fonts.google.com/specimen/Roboto+Mono">https://fonts.google.com/specimen/Roboto+Mono</a><br>
 * GitHub project: <a href="https://github.com/googlefonts/RobotoMono">https://github.com/googlefonts/RobotoMono</a>
 * <p>
 * To install the font, invoke following once (e.g. in your {@code main()} method; on AWT thread).
 * <p>
 * For lazy loading use:
 * <pre>{@code
 * FlatRobotoMonoFont.installLazy();
 * }</pre>
 * <p>
 * Or load immediately with:
 * <pre>{@code
 * FlatRobotoMonoFont.install();
 * }</pre>
 * <p>
 * Use as application monospaced font (invoke before setting up FlatLaf):
 * <pre>{@code
 * FlatLaf.setPreferredMonospacedFontFamily( FlatRobotoMonoFont.FAMILY );
 * }</pre>
 * <p>
 * Create single fonts:
 * <pre>{@code
 * new Font( FlatRobotoMonoFont.FAMILY, Font.PLAIN, 12 );
 * new Font( FlatRobotoMonoFont.FAMILY, Font.ITALIC, 12 );
 * new Font( FlatRobotoMonoFont.FAMILY, Font.BOLD, 12 );
 * new Font( FlatRobotoMonoFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
 * }</pre>
 * <p>
 * If using lazy loading, invoke following before creating the font:
 * <pre>{@code
 * FontUtils.loadFontFamily( FlatRobotoMonoFont.FAMILY );
 * }</pre>
 * <p>
 * E.g.:
 * <pre>{@code
 * FontUtils.loadFontFamily( FlatRobotoMonoFont.FAMILY );
 * Font font = new Font( FlatRobotoMonoFont.FAMILY, Font.PLAIN, 12 );
 * }</pre>
 * <p>
 * Or use following:
 * <pre>{@code
 * Font font = FontUtils.getCompositeFont( FlatRobotoMonoFont.FAMILY, Font.PLAIN, 12 );
 * }</pre>
 *
 * @author Karl Tauber
 */
public class FlatRobotoMonoFont
{
	/**
	 * Family name for basic styles (regular, italic and bold).
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatRobotoMonoFont.FAMILY, Font.PLAIN, 12 );
	 * new Font( FlatRobotoMonoFont.FAMILY, Font.ITALIC, 12 );
	 * new Font( FlatRobotoMonoFont.FAMILY, Font.BOLD, 12 );
	 * new Font( FlatRobotoMonoFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
	 * }</pre>
	 */
	public static final String FAMILY = "Roboto Mono";

	/**
	 * Use for {@link #installStyle(String)} to install single font styles.
	 */
	public static final String
		// basic styles
		STYLE_REGULAR = "RobotoMono-Regular.ttf",
		STYLE_ITALIC = "RobotoMono-Italic.ttf",
		STYLE_BOLD = "RobotoMono-Bold.ttf",
		STYLE_BOLD_ITALIC = "RobotoMono-BoldItalic.ttf";


	private FlatRobotoMonoFont() {}

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
		FontUtils.registerFontFamilyLoader( FAMILY, FlatRobotoMonoFont::install );
	}

	/**
	 * Creates and registers the fonts for all styles.
	 * <p>
	 * When using FlatLaf, consider using {@link #installLazy()}.
	 */
	public static void install() {
		installStyle( STYLE_REGULAR );
		installStyle( STYLE_ITALIC );
		installStyle( STYLE_BOLD );
		installStyle( STYLE_BOLD_ITALIC );
	}

	/**
	 * Creates and registers the font for the given style.
	 * See {@code STYLE_} constants.
	 */
	public static boolean installStyle( String name ) {
		return FontUtils.installFont( FlatRobotoMonoFont.class.getResource( name ) );
	}
}
