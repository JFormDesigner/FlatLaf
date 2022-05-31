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

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

/**
 * The JetBrains Mono font family.
 * <p>
 * Font home page: <a href="https://www.jetbrains.com/mono">https://www.jetbrains.com/mono</a><br>
 * GitHub project: <a href="https://github.com/JetBrains/JetBrainsMono">https://github.com/JetBrains/JetBrainsMono</a>
 * <p>
 * To install the font, invoke following once (e.g. in your {@code main()} method; on AWT thread):
 * <pre>{@code
 * FlatJetBrainsMonoFont.install();
 * }</pre>
 * <p>
 * Use as default monospaced font:
 * <pre>{@code
 * FlatLaf.setPreferredMonospacedFontFamily( FlatJetBrainsMonoFont.FAMILY );
 * }</pre>
 * <p>
 * Create fonts:
 * <pre>{@code
 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 12 );
 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.ITALIC, 12 );
 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.BOLD, 12 );
 * new Font( FlatJetBrainsMonoFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
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
	 * Creates and registers the fonts for all styles.
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
		try( InputStream in = FlatJetBrainsMonoFont.class.getResourceAsStream( name ) ) {
			Font font = Font.createFont( Font.TRUETYPE_FONT, in );
			return GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont( font );
		} catch( FontFormatException ex ) {
			ex.printStackTrace();
			return false;
		} catch( IOException ex ) {
			ex.printStackTrace();
			return false;
		}
	}
}
