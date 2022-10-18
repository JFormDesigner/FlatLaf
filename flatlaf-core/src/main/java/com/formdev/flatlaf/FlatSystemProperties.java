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

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.util.UIScale;

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
	 * Since FlatLaf 1.1.2: Scale factors less than 100% are allowed.
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
	 * Specifies whether values smaller than 100% are allowed for the user scale factor
	 * (see {@link UIScale#getUserScaleFactor()}).
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> {@code false}
	 *
	 * @since 1.1.2
	 */
	String UI_SCALE_ALLOW_SCALE_DOWN = "flatlaf.uiScale.allowScaleDown";

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
	 * Specifies whether native window decorations should be used
	 * when creating {@code JFrame} or {@code JDialog}.
	 * <p>
	 * Setting this to {@code true} forces using native window decorations
	 * even if they are not enabled by the application.<br>
	 * Setting this to {@code false} disables using native window decorations.
	 * <p>
	 * This system property has higher priority than client property
	 * {@link FlatClientProperties#USE_WINDOW_DECORATIONS} and
	 * UI default {@code TitlePane.useWindowDecorations}.
	 * <p>
	 * (requires Window 10/11)
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> none
	 */
	String USE_WINDOW_DECORATIONS = "flatlaf.useWindowDecorations";

	/**
	 * Specifies whether JetBrains Runtime custom window decorations should be used
	 * when creating {@code JFrame} or {@code JDialog}.
	 * Requires that the application runs in a
	 * <a href="https://github.com/JetBrains/JetBrainsRuntime/wiki">JetBrains Runtime</a>
	 * (based on OpenJDK).
	 * <p>
	 * Setting this to {@code false} disables using JetBrains Runtime custom window decorations.
	 * Then FlatLaf native window decorations are used.
	 * <p>
	 * (requires Window 10/11)
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> {@code false} (since v2; was {@code true} in v1)
	 */
	String USE_JETBRAINS_CUSTOM_DECORATIONS = "flatlaf.useJetBrainsCustomDecorations";

	/**
	 * Specifies whether the menu bar is embedded into the window title pane
	 * if window decorations are enabled.
	 * <p>
	 * Setting this to {@code true} forces embedding.<br>
	 * Setting this to {@code false} disables embedding.
	 * <p>
	 * This system property has higher priority than client property
	 * {@link FlatClientProperties#MENU_BAR_EMBEDDED} and
	 * UI default {@code TitlePane.menuBarEmbedded}.
	 * <p>
	 * (requires Window 10/11)
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> none
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
	 * Specifies whether FlatLaf updates the UI when the system font changes.
	 * If {@code true}, {@link SwingUtilities#updateComponentTreeUI(java.awt.Component)}
	 * gets invoked for all windows if the system font has changed.
	 * This is the similar to when switching to another look and feel (theme).
	 * Applications that do not work correctly when switching look and feel,
	 * should disable this option to avoid corrupted UI.
	 * <p>
	 * <strong>Allowed Values</strong> {@code false} and {@code true}<br>
	 * <strong>Default</strong> {@code true}
	 * @since 2.5
	 */
	String UPDATE_UI_ON_SYSTEM_FONT_CHANGE = "flatlaf.updateUIOnSystemFontChange";

	/**
	 * Specifies a directory in which the native FlatLaf libraries have been extracted.
	 * The path can be absolute or relative to current application working directory.
	 * This can be used to avoid extraction of the native libraries to the temporary directory at runtime.
	 * <p>
	 * If the value is {@code "system"}, then {@link System#loadLibrary(String)} is
	 * used to load the native library.
	 * Searches for the native library in classloader of caller
	 * (using {@link ClassLoader#findLibrary(String)}) and in paths specified
	 * in system properties {@code sun.boot.library.path} and {@code java.library.path}.
	 * (supported since FlatLaf 2.6)
	 * <p>
	 * If the native library can not loaded from the given path (or via {@link System#loadLibrary(String)}),
	 * then the embedded native library is extracted to the temporary directory and loaded from there.
	 *
	 * @since 2
	 */
	String NATIVE_LIBRARY_PATH = "flatlaf.nativeLibraryPath";

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
