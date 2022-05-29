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

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

/**
 * The Inter font family.
 * <p>
 * <strong>Note:</strong> This font requires <strong>Java 10 or later</strong>.
 * It is displayed too large in Java 8 and 9.
 * <p>
 * Font home page: <a href="https://rsms.me/inter/">https://rsms.me/inter/</a><br>
 * GitHub project: <a href="https://github.com/rsms/inter">https://github.com/rsms/inter</a>
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
	public static final String FAMILY_SEMIBOLD = "Inter Semi Bold";

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
	 * Creates and registers the fonts for all styles.
	 */
	public static void install() {
		// basic styles
		installStyle( STYLE_REGULAR );
		installStyle( STYLE_ITALIC );
		installStyle( STYLE_BOLD );
		installStyle( STYLE_BOLD_ITALIC );

		// light
		installStyle( STYLE_LIGHT );
		installStyle( STYLE_LIGHT_ITALIC );

		// semibold
		installStyle( STYLE_SEMIBOLD );
		installStyle( STYLE_SEMIBOLD_ITALIC );
	}

	/**
	 * Creates and registers the font for the given style.
	 * See {@code STYLE_} constants.
	 */
	public static boolean installStyle( String name ) {
		try( InputStream in = FlatInterFont.class.getResourceAsStream( name ) ) {
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
