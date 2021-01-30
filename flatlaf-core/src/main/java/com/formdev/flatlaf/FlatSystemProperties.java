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

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Defines/documents own system properties used in FlatLaf.
 *
 * @author Karl Tauber
 */
public interface FlatSystemProperties
{
	/**
	 * Specifies a custom scale factor used to scale the UI.
	 * <p>
	 * If Java runtime scales (Java 9 or later), this scale factor is applied on top
	 * of the Java system scale factor. Java 8 does not scale and this scale factor
	 * replaces the user scale factor that FlatLaf computes based on the font.
	 * To replace the Java 9+ system scale factor, use system property "sun.java2d.uiScale",
	 * which has the same syntax as this one.
	 * <p>
	 * <strong>Allowed Values</strong> e.g. {@code 1.5}, {@code 1.5x}, {@code 150%} or {@code 144dpi} (96dpi is 100%)<br>
	 */
	String UI_SCALE = "flatlaf.uiScale";

	/**
	 * Specifies whether user scaling mode is enabled.
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> {@code true}
	 */
	String UI_SCALE_ENABLED = "flatlaf.uiScale.enabled";

	/**
	 * Specifies whether Ubuntu font should be used on Ubuntu Linux.
	 * By default, if not running in a JetBrains Runtime, the Liberation Sans font
	 * is used because there are rendering issues (in Java) with Ubuntu fonts.
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> {@code false}
	 */
	String USE_UBUNTU_FONT = "flatlaf.useUbuntuFont";

	/**
	 * Specifies whether custom look and feel window decorations should be used
	 * when creating {@code JFrame} or {@code JDialog}.
	 * <p>
	 * If this system property is set, FlatLaf invokes {@link JFrame#setDefaultLookAndFeelDecorated(boolean)}
	 * and {@link JDialog#setDefaultLookAndFeelDecorated(boolean)} on LaF initialization.
	 * <p>
	 * (requires Window 10)
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> none
	 */
	String USE_WINDOW_DECORATIONS = "flatlaf.useWindowDecorations";

	/**
	 * Specifies whether JetBrains Runtime custom window decorations should be used
	 * when creating {@code JFrame} or {@code JDialog}.
	 * Requires that the application runs in a
	 * <a href="https://confluence.jetbrains.com/display/JBR/JetBrains+Runtime">JetBrains Runtime</a>
	 * (based on OpenJDK).
	 * <p>
	 * Setting this to {@code true} forces using JetBrains Runtime custom window decorations
	 * even if they are not enabled by the application.
	 * <p>
	 * (requires Window 10)
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> {@code true}
	 */
	String USE_JETBRAINS_CUSTOM_DECORATIONS = "flatlaf.useJetBrainsCustomDecorations";

	/**
	 * Specifies whether menubar is embedded into custom window decorations.
	 * <p>
	 * (requires Window 10)
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> {@code true}
	 */
	String MENUBAR_EMBEDDED = "flatlaf.menuBarEmbedded";

	/**
	 * Specifies whether animations are enabled.
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> {@code true}
	 */
	String ANIMATION = "flatlaf.animation";

	/**
	 * Specifies whether vertical text position is corrected when UI is scaled on HiDPI screens.
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> {@code true}
	 */
	String USE_TEXT_Y_CORRECTION = "flatlaf.useTextYCorrection";

	/**
	 * Checks whether a system property is set and returns {@code true} if its value
	 * is {@code "true"} (case-insensitive), otherwise it returns {@code false}.
	 * If the system property is not set, {@code defaultValue} is returned.
	 */
	static boolean getBoolean( String key, boolean defaultValue ) {
		String value = System.getProperty( key );
		return (value != null) ? Boolean.parseBoolean( value ) : defaultValue;
	}

	/**
	 * Checks whether a system property is set and returns {@code Boolean.TRUE} if its value
	 * is {@code "true"} (case-insensitive) or returns {@code Boolean.FALSE} if its value
	 * is {@code "false"} (case-insensitive). Otherwise {@code defaultValue} is returned.
	 */
	static Boolean getBooleanStrict( String key, Boolean defaultValue ) {
		String value = System.getProperty( key );
		if( "true".equalsIgnoreCase( value ) )
			return Boolean.TRUE;
		if( "false".equalsIgnoreCase( value ) )
			return Boolean.FALSE;
		return defaultValue;
	}
}
