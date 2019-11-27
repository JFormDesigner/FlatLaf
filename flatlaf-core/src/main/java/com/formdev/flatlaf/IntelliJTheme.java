/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import com.formdev.flatlaf.json.Json;
import com.formdev.flatlaf.json.ParseException;
import com.formdev.flatlaf.util.StringUtils;

/**
 * This class supports loading IntelliJ .theme.json files and using them as a Laf.
 *
 * .theme.json files are used by Theme plugins for IntelliJ IDEA and other
 * JetBrains IDEs that are based on IntelliJ platform.
 *
 * Here you can find IntelliJ Theme plugins:
 *   https://plugins.jetbrains.com/search?tags=Theme
 *
 * The IntelliJ .theme.json file are documented here:
 *   http://www.jetbrains.org/intellij/sdk/docs/reference_guide/ui_themes/themes_customize.html
 *
 * @author Karl Tauber
 */
public class IntelliJTheme
{
	public final String name;
	public final boolean dark;
	public final String author;

	private final Map<String, String> colors;
	private final Map<String, Object> ui;
	private final Map<String, Object> icons;

	private Map<String, ColorUIResource> namedColors = Collections.emptyMap();

	/**
	 * Loads a IntelliJ .theme.json file from the given input stream,
	 * creates a Laf instance for it and installs it.
	 *
	 * The input stream is automatically closed.
	 * Using a buffered input stream is not necessary.
	 */
	public static boolean install( InputStream in ) {
		try {
		    return FlatLaf.install( createLaf( in ) );
		} catch( Exception ex ) {
		    System.err.println( "Failed to load IntelliJ theme" );
		    ex.printStackTrace();
		    return false;
		}
	}

	/**
	 * Loads a IntelliJ .theme.json file from the given input stream and
	 * creates a Laf instance for it.
	 *
	 * The input stream is automatically closed.
	 * Using a buffered input stream is not necessary.
	 */
	public static FlatLaf createLaf( InputStream in )
		throws IOException, ParseException
	{
		return createLaf( new IntelliJTheme( in ) );
	}

	/**
	 * Creates a Laf instance for the given IntelliJ theme.
	 */
	public static FlatLaf createLaf( IntelliJTheme theme ) {
		return new ThemeLaf( theme );
	}

	/**
	 * Loads a IntelliJ .theme.json file from the given input stream.
	 *
	 * The input stream is automatically closed.
	 * Using a buffered input stream is not necessary.
	 */
	@SuppressWarnings( "unchecked" )
	public IntelliJTheme( InputStream in )
		throws IOException, ParseException
	{
		Map<String, Object> json;
	    try( Reader reader = new InputStreamReader( in, StandardCharsets.UTF_8 ) ) {
	    		json = (Map<String, Object>) Json.parse( reader );
		}

	    name = (String) json.get( "name" );
	    dark = Boolean.parseBoolean( (String) json.get( "dark" ) );
	    author = (String) json.get( "author" );

	    colors = (Map<String, String>) json.get( "colors" );
	    ui = (Map<String, Object>) json.get( "ui" );
	    icons = (Map<String, Object>) json.get( "icons" );
	}

	private void applyProperties( UIDefaults defaults ) {
		if( ui == null )
			return;

		defaults.put( "Component.isIntelliJTheme", true );

		loadNamedColors( defaults );

		// convert Json "ui" structure to UI defaults
		ArrayList<Object> defaultsKeysCache = new ArrayList<>();
		Set<String> uiKeys = new HashSet<>();
		for( Map.Entry<String, Object> e : ui.entrySet() )
			apply( e.getKey(), e.getValue(), defaults, defaultsKeysCache, uiKeys );

		applyColorPalette( defaults );
		applyCheckBoxColors( defaults );

		// IDEA uses TextField.background for editable ComboBox and Spinner
		defaults.put( "ComboBox.editableBackground", defaults.get( "TextField.background" ) );
		defaults.put( "Spinner.background", defaults.get( "TextField.background" ) );

		// Spinner arrow button always has same colors as ComboBox arrow button
		defaults.put( "Spinner.buttonBackground", defaults.get( "ComboBox.buttonEditableBackground" ) );
		defaults.put( "Spinner.buttonArrowColor", defaults.get( "ComboBox.buttonArrowColor" ) );
		defaults.put( "Spinner.buttonDisabledArrowColor", defaults.get( "ComboBox.buttonDisabledArrowColor" ) );

		// some themes specify colors for TextField.background, but forget to specify it for other components
		// (probably because those components are not used in IntelliJ)
		if( uiKeys.contains( "TextField.background" ) ) {
			Object textFieldBackground = defaults.get( "TextField.background" );
			if( !uiKeys.contains( "FormattedTextField.background" ) )
				defaults.put( "FormattedTextField.background", textFieldBackground );
			if( !uiKeys.contains( "PasswordField.background" ) )
				defaults.put( "PasswordField.background", textFieldBackground );
			if( !uiKeys.contains( "EditorPane.background" ) )
				defaults.put( "EditorPane.background", textFieldBackground );
			if( !uiKeys.contains( "TextArea.background" ) )
				defaults.put( "TextArea.background", textFieldBackground );
			if( !uiKeys.contains( "TextPane.background" ) )
				defaults.put( "TextPane.background", textFieldBackground );
			if( !uiKeys.contains( "Spinner.background" ) )
				defaults.put( "Spinner.background", textFieldBackground );
		}
	}

	/**
	 * http://www.jetbrains.org/intellij/sdk/docs/reference_guide/ui_themes/themes_customize.html#defining-named-colors
	 */
	private void loadNamedColors( UIDefaults defaults ) {
		if( colors == null )
			return;

		namedColors = new HashMap<>();

		for( Map.Entry<String, String> e : colors.entrySet() ) {
			String value = e.getValue();
			ColorUIResource color = UIDefaultsLoader.parseColor( value );
			if( color != null ) {
				String key = e.getKey();
				namedColors.put( key, color );
				defaults.put( "ColorPalette." + e.getKey(), color );
			}
		}
	}

	/**
	 * http://www.jetbrains.org/intellij/sdk/docs/reference_guide/ui_themes/themes_customize.html#custom-ui-control-colors
	 */
	@SuppressWarnings( "unchecked" )
	private void apply( String key, Object value, UIDefaults defaults, ArrayList<Object> defaultsKeysCache, Set<String> uiKeys ) {
		if( value instanceof Map ) {
			for( Map.Entry<String, Object> e : ((Map<String, Object>)value).entrySet() )
				apply( key + '.' + e.getKey(), e.getValue(), defaults, defaultsKeysCache, uiKeys );
		} else {
			uiKeys.add( key );

			// map keys
			key = uiKeyMapping.getOrDefault( key, key );
			if( key.isEmpty() )
				return; // ignore key

			String valueStr = value.toString();

			// map named colors
			Object uiValue = namedColors.get( valueStr );

			// parse value
			if( uiValue == null ) {
				// fix errors (missing '#' for colors)
				if( !valueStr.startsWith( "#" ) && (key.endsWith( "ground" ) || key.endsWith( "Color" )) )
					valueStr = "#" + valueStr;
				else if( key.endsWith( ".border" ) || key.endsWith( "Border" ) ) {
					List<String> parts = StringUtils.split( valueStr, ',' );
					if( parts.size() == 5 && !parts.get( 4 ).startsWith( "#" ) ) {
						parts.set( 4, "#" + parts.get( 4 ) );
						valueStr = String.join( ",", parts );
					}
				}

				// parse value
				try {
					uiValue = UIDefaultsLoader.parseValue( key, valueStr );
				} catch( RuntimeException ex ) {
					UIDefaultsLoader.logParseError( key, valueStr, ex );
					return; // ignore invalid value
				}
			}

			if( key.startsWith( "*." ) ) {
				// wildcard
				String tail = key.substring( 1 );

				// because we can not iterate over the UI defaults keys while
				// modifying UI defaults in the same loop, we have to copy the keys
				if( defaultsKeysCache.size() != defaults.size() ) {
					defaultsKeysCache.clear();
					Enumeration<Object> e = defaults.keys();
					while( e.hasMoreElements() )
						defaultsKeysCache.add( e.nextElement() );
				}

				// replace all values in UI defaults that match the wildcard key
				for( Object k : defaultsKeysCache ) {
					if( k instanceof String ) {
						// support replacing of mapped keys
						// (e.g. set ComboBox.buttonEditableBackground to *.background
						// because it is mapped from ComboBox.ArrowButton.background)
						String km = uiKeyInverseMapping.getOrDefault( k, (String) k );
						if( km.endsWith( tail ) && !noWildcardReplace.contains( k ) && !((String)k).startsWith( "CheckBox.icon." ) )
							defaults.put( k, uiValue );
					}
				}
			} else
				defaults.put( key, uiValue );
		}
	}

	private void applyColorPalette( UIDefaults defaults ) {
		if( icons == null )
			return;

		Object palette = icons.get( "ColorPalette" );
		if( !(palette instanceof Map) )
			return;

		@SuppressWarnings( "unchecked" )
		Map<String, Object> colorPalette = (Map<String, Object>) palette;
		for( Map.Entry<String, Object> e : colorPalette.entrySet() ) {
			String key = e.getKey();
			Object value = e.getValue();
			if( key.startsWith( "Checkbox." ) || !(value instanceof String) )
				continue;

			if( dark )
				key = StringUtils.removeTrailing( key, ".Dark" );

			ColorUIResource color = toColor( (String) value );
			if( color != null )
				defaults.put( key, color );
		}
	}

	private ColorUIResource toColor( String value ) {
		// map named colors
		ColorUIResource color = namedColors.get( value );

		// parse color
		return (color != null) ? color : UIDefaultsLoader.parseColor( value );
	}

	/**
	 * Because IDEA uses SVGs for check boxes and radio buttons the colors for
	 * this two components are specified in "icons > ColorPalette".
	 * FlatLaf uses vector icons and expects colors for the two components in UI defaults.
	 */
	private void applyCheckBoxColors( UIDefaults defaults ) {
		if( icons == null )
			return;

		Object palette = icons.get( "ColorPalette" );
		if( !(palette instanceof Map) )
			return;

		boolean checkboxModified = false;
		@SuppressWarnings( "unchecked" )
		Map<String, Object> colorPalette = (Map<String, Object>) palette;
		for( Map.Entry<String, Object> e : colorPalette.entrySet() ) {
			String key = e.getKey();
			Object value = e.getValue();
			if( !key.startsWith( "Checkbox." ) || !(value instanceof String) )
				continue;

			if( key.equals( "Checkbox.Background.Default" ) ||
				key.equals( "Checkbox.Foreground.Selected" ) )
			{
				// This two keys do not work correctly in IDEA because they
				// map SVG color "#ffffff" to another color, but checkBox.svg and
				// radio.svg (in package com.intellij.ide.ui.laf.icons.intellij)
				// use "#fff". So use white to get same appearance as in IDEA.
				value = "#ffffff";
			}

			if( dark )
				key = StringUtils.removeTrailing( key, ".Dark" );

			String newKey = checkboxKeyMapping.get( key );
			if( newKey != null ) {
				ColorUIResource color = toColor( (String) value );
				if( color != null )
					defaults.put( newKey, color );

				checkboxModified = true;
			}
		}

		// When IDEA replaces colors in SVGs it uses color values and not the keys
		// from com.intellij.ide.ui.UITheme.colorPalette, but there are some keys that
		// have same color value:
		//   - Checkbox.Background.Default.Dark  has same color as  Checkbox.Background.Selected.Dark
		//   - Checkbox.Border.Default.Dark      has same color as  Checkbox.Border.Selected.Dark
		//   - Checkbox.Focus.Thin.Default.Dark  has same color as  Checkbox.Focus.Thin.Selected.Dark
		//
		// So if only e.g. Checkbox.Background.Default.Dark is specified in .theme.json,
		// then this color is also used for Checkbox.Background.Selected.Dark.
		// Occurs e.g. in "Dark purple" theme.
		fixCheckBoxColor( defaults, colorPalette, "Checkbox.Background.Default.Dark", "Checkbox.Background.Selected.Dark" );
		fixCheckBoxColor( defaults, colorPalette, "Checkbox.Border.Default.Dark",     "Checkbox.Border.Selected.Dark" );
		fixCheckBoxColor( defaults, colorPalette, "Checkbox.Focus.Thin.Default.Dark", "Checkbox.Focus.Thin.Selected.Dark" );

		// remove hover and pressed colors
		if( checkboxModified ) {
			defaults.remove( "CheckBox.icon.hoverBorderColor" );
			defaults.remove( "CheckBox.icon.focusedBackground" );
			defaults.remove( "CheckBox.icon.hoverBackground" );
			defaults.remove( "CheckBox.icon.pressedBackground" );
			defaults.remove( "CheckBox.icon.selectedHoverBackground" );
			defaults.remove( "CheckBox.icon.selectedPressedBackground" );
		}
	}

	private void fixCheckBoxColor( UIDefaults defaults, Map<String, Object> colorPalette, String key1, String key2 ) {
		if( colorPalette.containsKey( key1 ) == colorPalette.containsKey( key2 ) )
			return;

		String newKey1 = checkboxKeyMapping.get( StringUtils.removeTrailing( key1, ".Dark" ) );
		String newKey2 = checkboxKeyMapping.get( StringUtils.removeTrailing( key2, ".Dark" ) );
		if( colorPalette.containsKey( key1 ) )
			defaults.put( newKey2, defaults.get( newKey1 ) );
		else
			defaults.put( newKey1, defaults.get( newKey2 ) );
	}

	private static Map<String, String> uiKeyMapping = new HashMap<>();
	private static Map<String, String> uiKeyInverseMapping = new HashMap<>();
	private static Map<String, String> checkboxKeyMapping = new HashMap<>();
	private static Set<String> noWildcardReplace = new HashSet<>();

	static {
		// Button
		// IDEA buttons support gradient for background and border, but FlatLaf does not
		uiKeyMapping.put( "Button.startBackground",          "Button.background" );
		uiKeyMapping.put( "Button.startBorderColor",         "Button.borderColor" );
		uiKeyMapping.put( "Button.default.startBackground",  "Button.default.background" );
		uiKeyMapping.put( "Button.default.startBorderColor", "Button.default.borderColor" );
		uiKeyMapping.put( "Button.endBackground",            "" ); // ignore
		uiKeyMapping.put( "Button.endBorderColor",           "" ); // ignore
		uiKeyMapping.put( "Button.default.endBackground",    "" ); // ignore
		uiKeyMapping.put( "Button.default.endBorderColor",   "" ); // ignore

		// ComboBox
		uiKeyMapping.put( "ComboBox.background",                        "" ); // ignore
		uiKeyMapping.put( "ComboBox.nonEditableBackground",             "ComboBox.background" );
		uiKeyMapping.put( "ComboBox.ArrowButton.background",            "ComboBox.buttonEditableBackground" );
		uiKeyMapping.put( "ComboBox.ArrowButton.disabledIconColor",     "ComboBox.buttonDisabledArrowColor" );
		uiKeyMapping.put( "ComboBox.ArrowButton.iconColor",             "ComboBox.buttonArrowColor" );
		uiKeyMapping.put( "ComboBox.ArrowButton.nonEditableBackground", "ComboBox.buttonBackground" );

		// ProgressBar
		uiKeyMapping.put( "ProgressBar.background",    "" ); // ignore
		uiKeyMapping.put( "ProgressBar.foreground",    "" ); // ignore
		uiKeyMapping.put( "ProgressBar.trackColor",    "ProgressBar.background" );
		uiKeyMapping.put( "ProgressBar.progressColor", "ProgressBar.foreground" );

		// ScrollBar
		uiKeyMapping.put( "ScrollBar.trackColor", "ScrollBar.track" );
		uiKeyMapping.put( "ScrollBar.thumbColor", "ScrollBar.thumb" );

		// Slider
		uiKeyMapping.put( "Slider.trackWidth", "" ); // ignore (used in Material Theme UI Lite)

		for( Map.Entry<String, String> e : uiKeyMapping.entrySet() )
			uiKeyInverseMapping.put( e.getValue(), e.getKey() );

		checkboxKeyMapping.put( "Checkbox.Background.Default",  "CheckBox.icon.background" );
		checkboxKeyMapping.put( "Checkbox.Background.Disabled", "CheckBox.icon.disabledBackground" );
		checkboxKeyMapping.put( "Checkbox.Border.Default",      "CheckBox.icon.borderColor" );
		checkboxKeyMapping.put( "Checkbox.Border.Disabled",     "CheckBox.icon.disabledBorderColor" );
		checkboxKeyMapping.put( "Checkbox.Focus.Thin.Default",  "CheckBox.icon.focusedBorderColor" );
		checkboxKeyMapping.put( "Checkbox.Focus.Wide",          "CheckBox.icon.focusedColor" );
		checkboxKeyMapping.put( "Checkbox.Foreground.Disabled", "CheckBox.icon.disabledCheckmarkColor" );
		checkboxKeyMapping.put( "Checkbox.Background.Selected", "CheckBox.icon.selectedBackground" );
		checkboxKeyMapping.put( "Checkbox.Border.Selected",     "CheckBox.icon.selectedBorderColor" );
		checkboxKeyMapping.put( "Checkbox.Foreground.Selected", "CheckBox.icon.checkmarkColor" );
		checkboxKeyMapping.put( "Checkbox.Focus.Thin.Selected", "CheckBox.icon.selectedFocusedBorderColor" );

		// because FlatLaf uses Button.background and Button.borderColor,
		// but IDEA uses Button.startBackground and Button.startBorderColor,
		// our default button background and border colors may be replaced by
		// wildcard *.background and *.borderColor colors
		noWildcardReplace.add( "Button.background" );
		noWildcardReplace.add( "Button.borderColor" );
		noWildcardReplace.add( "Button.default.background" );
		noWildcardReplace.add( "Button.default.borderColor" );
		noWildcardReplace.add( "ToggleButton.background" );
	}

	//---- class ThemeLaf -----------------------------------------------------

	public static class ThemeLaf
		extends FlatLaf
	{
		private final IntelliJTheme theme;

		public ThemeLaf( IntelliJTheme theme ) {
			this.theme = theme;
		}

		@Override
		public String getName() {
			return theme.name;
		}

		@Override
		public String getDescription() {
			return theme.name;
		}

		@Override
		public boolean isDark() {
			return theme.dark;
		}

		public IntelliJTheme getTheme() {
			return theme;
		}

		@Override
		public UIDefaults getDefaults() {
			UIDefaults defaults = super.getDefaults();
			theme.applyProperties( defaults );
			return defaults;
		}

		@Override
		ArrayList<Class<?>> getLafClassesForDefaultsLoading() {
			ArrayList<Class<?>> lafClasses = new ArrayList<>();
			lafClasses.add( FlatLaf.class );
			lafClasses.add( theme.dark ? FlatDarkLaf.class : FlatLightLaf.class );
			lafClasses.add( theme.dark ? FlatDarculaLaf.class : FlatIntelliJLaf.class );
			lafClasses.add( ThemeLaf.class );
			return lafClasses;
		}
	}
}
