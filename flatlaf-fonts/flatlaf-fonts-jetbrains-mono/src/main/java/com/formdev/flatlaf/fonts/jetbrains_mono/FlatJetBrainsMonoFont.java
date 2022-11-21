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

package com.formdev.flatlaf.fonts.jetbrains_mono;

import com.formdev.flatlaf.util.FontUtils;

/**
 * The JetBrains Mono font family.
 * <p>
 * Font home page: <a href="https://www.jetbrains.com/mono">https://www.jetbrains.com/mono</a><br>
 * GitHub project: <a href="https://github.com/JetBrains/JetBrainsMono">https://github.com/JetBrains/JetBrainsMono</a>
 * <p>
 * To install the font, invoke following once (e.g. in your {@code main()} method; on AWT thread).
 * <p>
 * For lazy loading use:
 * <pre>{@code
 * FlatJetBrainsMonoFont.installLazy();
 * }</pre>
 * <p>
 * Or load immediately with:
 * <pre>{@code
 * FlatJetBrainsMonoFont.install();
 * }</pre>
 * <p>
 * Use as application monospaced font (invoke before setting up FlatLaf):
 * <pre>{@code
 * FlatLaf.setPreferredMonospacedFontFamily( FlatJetBrainsMonoFont.FAMILY );
 * }</pre>
 * <p>
 * Create single fonts:
 * <pre>{@code
 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 12 );
 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.ITALIC, 12 );
 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.BOLD, 12 );
 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
 * }</pre>
 * <p>
 * If using lazy loading, invoke following before creating the font:
 * <pre>{@code
 * FontUtils.loadFontFamily( FlatJetBrainsMonoFont.FAMILY );
 * }</pre>
 * <p>
 * E.g.:
 * <pre>{@code
 * FontUtils.loadFontFamily( FlatJetBrainsMonoFont.FAMILY );
 * Font font = new Font( FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 12 );
 * }</pre>
 * <p>
 * Or use following:
 * <pre>{@code
 * Font font = FontUtils.getCompositeFont( FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 12 );
 * }</pre>
 *
 * @author Karl Tauber
 */
public class FlatJetBrainsMonoFont
{
	/**
	 * Family name for basic styles (regular, italic and bold).
	 * <p>
	 * Usage:
	 * <pre>{@code
	 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 12 );
	 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.ITALIC, 12 );
	 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.BOLD, 12 );
	 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
	 * }</pre>
	 */
	public static final String FAMILY = "JetBrains Mono";

	/**
	 * Use for {@link #installStyle(String)} to install single font styles.
	 */
	public static final String
		// basic styles
		STYLE_REGULAR = "JetBrainsMono-Regular.ttf",
		STYLE_ITALIC = "JetBrainsMono-Italic.ttf",
		STYLE_BOLD = "JetBrainsMono-Bold.ttf",
		STYLE_BOLD_ITALIC = "JetBrainsMono-BoldItalic.ttf";


	private FlatJetBrainsMonoFont() {}

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
		FontUtils.registerFontFamilyLoader( FAMILY, FlatJetBrainsMonoFont::install );
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
		return FontUtils.installFont( FlatJetBrainsMonoFont.class.getResource( name ) );
	}
}
