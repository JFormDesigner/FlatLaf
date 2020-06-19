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
import java.util.logging.Level;
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
			FlatLaf.LOG.log( Level.SEVERE, "FlatLaf: Failed to load IntelliJ theme", ex );
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
		throws IOException
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
		throws IOException
	{
		Map<String, Object> json;
	    try( Reader reader = new InputStreamReader( in, StandardCharsets.UTF_8 ) ) {
	    		json = (Map<String, Object>) Json.parse( reader );
		} catch( ParseException ex ) {
			throw new IOException( ex.getMessage(), ex );
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

		// enable button shadows
		defaults.put( "Button.paintShadow", true );
		defaults.put( "Button.shadowWidth", dark ? 2 : 1 );

		Map<Object, Object> themeSpecificDefaults = removeThemeSpecificDefaults( defaults );

		loadNamedColors( defaults );

		// convert Json "ui" structure to UI defaults
		ArrayList<Object> defaultsKeysCache = new ArrayList<>();
		Set<String> uiKeys = new HashSet<>();
		for( Map.Entry<String, Object> e : ui.entrySet() )
			apply( e.getKey(), e.getValue(), defaults, defaultsKeysCache, uiKeys );

		applyColorPalette( defaults );
		applyCheckBoxColors( defaults );

		// IDEA does not paint button background if disabled, but FlatLaf does
		Object panelBackground = defaults.get( "Panel.background" );
		defaults.put( "Button.disabledBackground", panelBackground );
		defaults.put( "ToggleButton.disabledBackground", panelBackground );

		// IDEA uses a SVG icon for the help button, but paints the background with Button.startBackground and Button.endBackground
		Object helpButtonBackground = defaults.get( "Button.startBackground" );
		Object helpButtonBorderColor = defaults.get( "Button.startBorderColor" );
		if( helpButtonBackground == null )
			helpButtonBackground = defaults.get( "Button.background" );
		if( helpButtonBorderColor == null )
			helpButtonBorderColor = defaults.get( "Button.borderColor" );
		defaults.put( "HelpButton.background", helpButtonBackground );
		defaults.put( "HelpButton.borderColor", helpButtonBorderColor );
		defaults.put( "HelpButton.disabledBackground", panelBackground );
		defaults.put( "HelpButton.disabledBorderColor", defaults.get( "Button.disabledBorderColor" ) );
		defaults.put( "HelpButton.focusedBorderColor", defaults.get( "Button.focusedBorderColor" ) );
		defaults.put( "HelpButton.focusedBackground", defaults.get( "Button.focusedBackground" ) );

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

		// fix ToggleButton
		if( !uiKeys.contains( "ToggleButton.startBackground" ) && !uiKeys.contains( "*.startBackground" ) )
			defaults.put( "ToggleButton.startBackground", defaults.get( "Button.startBackground" ) );
		if( !uiKeys.contains( "ToggleButton.endBackground" ) && !uiKeys.contains( "*.endBackground" ) )
			defaults.put( "ToggleButton.endBackground", defaults.get( "Button.endBackground" ) );
		if( !uiKeys.contains( "ToggleButton.foreground" ) && uiKeys.contains( "Button.foreground" ) )
			defaults.put( "ToggleButton.foreground", defaults.get( "Button.foreground" ) );

		// limit tree row height
		int rowHeight = defaults.getInt( "Tree.rowHeight" );
		if( rowHeight > 22 )
			defaults.put( "Tree.rowHeight", 22 );

		// apply theme specific UI defaults at the end to allow overwriting
		defaults.putAll( themeSpecificDefaults );
	}

	private Map<Object, Object> removeThemeSpecificDefaults( UIDefaults defaults ) {
		// search for theme specific UI defaults keys
		ArrayList<String> themeSpecificKeys = new ArrayList<>();
		for( Object key : defaults.keySet() ) {
			if( key instanceof String && ((String)key).startsWith( "[" ) )
				themeSpecificKeys.add( (String) key );
		}

		// remove theme specific UI defaults and remember only those for current theme
		Map<Object, Object> themeSpecificDefaults = new HashMap<>();
		String currentThemePrefix = '[' + name.replace( ' ', '_' ) + ']';
		for( String key : themeSpecificKeys ) {
			Object value = defaults.remove( key );
			if( key.startsWith( currentThemePrefix ) )
				themeSpecificDefaults.put( key.substring( currentThemePrefix.length() ), value );
		}

		return themeSpecificDefaults;
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
				defaults.put( "ColorPalette." + key, color );
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

			// fix ComboBox size and Spinner border in all Material UI Lite themes
			boolean isMaterialUILite = author.equals( "Mallowigi" );
			if( isMaterialUILite && (key.equals( "ComboBox.padding" ) || key.equals( "Spinner.border" )) )
				return; // ignore

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
					valueStr = fixColorIfValid( "#" + valueStr, valueStr );
				else if( valueStr.startsWith( "##" ) )
					valueStr = fixColorIfValid( valueStr.substring( 1 ), valueStr );
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
					UIDefaultsLoader.logParseError( Level.CONFIG, key, valueStr, ex );
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
						if( km.endsWith( tail ) && !((String)k).startsWith( "CheckBox.icon." ) )
							defaults.put( k, uiValue );
					}
				}
			} else
				defaults.put( key, uiValue );
		}
	}

	private String fixColorIfValid( String newColorStr, String colorStr ) {
		try {
			// check whether it is valid
			UIDefaultsLoader.parseColorRGBA( newColorStr );

			return newColorStr;
		} catch( IllegalArgumentException ex ) {
			return colorStr;
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

			String key2 = checkboxDuplicateColors.get( key );

			if( dark )
				key = StringUtils.removeTrailing( key, ".Dark" );

			String newKey = checkboxKeyMapping.get( key );
			if( newKey != null ) {
				ColorUIResource color = toColor( (String) value );
				if( color != null ) {
					defaults.put( newKey, color );

					if( key2 != null ) {
						// When IDEA replaces colors in SVGs it uses color values and not the keys
						// from com.intellij.ide.ui.UITheme.colorPalette, but there are some keys that
						// have same color value:
						//   - Checkbox.Background.Default.Dark  has same color as  Checkbox.Background.Selected.Dark
						//   - Checkbox.Border.Default.Dark      has same color as  Checkbox.Border.Selected.Dark
						//   - Checkbox.Focus.Thin.Default.Dark  has same color as  Checkbox.Focus.Thin.Selected.Dark
						//
						// So if only e.g. Checkbox.Background.Default.Dark is specified in .theme.json,
						// then this color is also used for Checkbox.Background.Selected.Dark.
						//
						// If Checkbox.Background.Default.Dark and Checkbox.Background.Selected.Dark
						// are specified in .theme.json, then the later specified is used for both.
						if( dark )
							key2 = StringUtils.removeTrailing( key2, ".Dark" );

						String newKey2 = checkboxKeyMapping.get( key2 );
						if( newKey2 != null )
							defaults.put( newKey2, color );
					}
				}

				checkboxModified = true;
			}
		}

		// remove hover and pressed colors
		if( checkboxModified ) {
			defaults.remove( "CheckBox.icon.hoverBorderColor" );
			defaults.remove( "CheckBox.icon.focusedBackground" );
			defaults.remove( "CheckBox.icon.hoverBackground" );
			defaults.remove( "CheckBox.icon.pressedBackground" );
			defaults.remove( "CheckBox.icon.selectedHoverBackground" );
			defaults.remove( "CheckBox.icon.selectedPressedBackground" );
		}

		// copy values
		for( Map.Entry<String, String> e : uiKeyCopying.entrySet() )
			defaults.put( e.getKey(), defaults.get( e.getValue() ) );
	}

	private static Map<String, String> uiKeyMapping = new HashMap<>();
	private static Map<String, String> uiKeyCopying = new HashMap<>();
	private static Map<String, String> uiKeyInverseMapping = new HashMap<>();
	private static Map<String, String> checkboxKeyMapping = new HashMap<>();
	private static Map<String, String> checkboxDuplicateColors = new HashMap<>();

	static {
		// ComboBox
		uiKeyMapping.put( "ComboBox.background",                        "" ); // ignore
		uiKeyMapping.put( "ComboBox.nonEditableBackground",             "ComboBox.background" );
		uiKeyMapping.put( "ComboBox.ArrowButton.background",            "ComboBox.buttonEditableBackground" );
		uiKeyMapping.put( "ComboBox.ArrowButton.disabledIconColor",     "ComboBox.buttonDisabledArrowColor" );
		uiKeyMapping.put( "ComboBox.ArrowButton.iconColor",             "ComboBox.buttonArrowColor" );
		uiKeyMapping.put( "ComboBox.ArrowButton.nonEditableBackground", "ComboBox.buttonBackground" );

		// Component
		uiKeyMapping.put( "Component.inactiveErrorFocusColor",   "Component.error.borderColor" );
		uiKeyMapping.put( "Component.errorFocusColor",           "Component.error.focusedBorderColor" );
		uiKeyMapping.put( "Component.inactiveWarningFocusColor", "Component.warning.borderColor" );
		uiKeyMapping.put( "Component.warningFocusColor",         "Component.warning.focusedBorderColor" );

		// Link
		uiKeyMapping.put( "Link.activeForeground", "Component.linkColor" );

		// ProgressBar
		uiKeyMapping.put( "ProgressBar.background",    "" ); // ignore
		uiKeyMapping.put( "ProgressBar.foreground",    "" ); // ignore
		uiKeyMapping.put( "ProgressBar.trackColor",    "ProgressBar.background" );
		uiKeyMapping.put( "ProgressBar.progressColor", "ProgressBar.foreground" );

		// ScrollBar
		uiKeyMapping.put( "ScrollBar.trackColor", "ScrollBar.track" );
		uiKeyMapping.put( "ScrollBar.thumbColor", "ScrollBar.thumb" );

		// Separator
		uiKeyMapping.put( "Separator.separatorColor", "Separator.foreground" );

		// Slider
		uiKeyMapping.put( "Slider.trackWidth", "" ); // ignore (used in Material Theme UI Lite)

		for( Map.Entry<String, String> e : uiKeyMapping.entrySet() )
			uiKeyInverseMapping.put( e.getValue(), e.getKey() );

		uiKeyCopying.put( "ToggleButton.tab.underlineColor",         "TabbedPane.underlineColor" );
		uiKeyCopying.put( "ToggleButton.tab.disabledUnderlineColor", "TabbedPane.disabledUnderlineColor" );
		uiKeyCopying.put( "ToggleButton.tab.selectedBackground",     "TabbedPane.selectedBackground" );
		uiKeyCopying.put( "ToggleButton.tab.hoverBackground",        "TabbedPane.hoverColor" );
		uiKeyCopying.put( "ToggleButton.tab.focusBackground",        "TabbedPane.focusColor" );

		checkboxKeyMapping.put( "Checkbox.Background.Default",  "CheckBox.icon.background" );
		checkboxKeyMapping.put( "Checkbox.Background.Disabled", "CheckBox.icon.disabledBackground" );
		checkboxKeyMapping.put( "Checkbox.Border.Default",      "CheckBox.icon.borderColor" );
		checkboxKeyMapping.put( "Checkbox.Border.Disabled",     "CheckBox.icon.disabledBorderColor" );
		checkboxKeyMapping.put( "Checkbox.Focus.Thin.Default",  "CheckBox.icon.focusedBorderColor" );
		checkboxKeyMapping.put( "Checkbox.Focus.Wide",          "CheckBox.icon.focusColor" );
		checkboxKeyMapping.put( "Checkbox.Foreground.Disabled", "CheckBox.icon.disabledCheckmarkColor" );
		checkboxKeyMapping.put( "Checkbox.Background.Selected", "CheckBox.icon.selectedBackground" );
		checkboxKeyMapping.put( "Checkbox.Border.Selected",     "CheckBox.icon.selectedBorderColor" );
		checkboxKeyMapping.put( "Checkbox.Foreground.Selected", "CheckBox.icon.checkmarkColor" );
		checkboxKeyMapping.put( "Checkbox.Focus.Thin.Selected", "CheckBox.icon.selectedFocusedBorderColor" );

		checkboxDuplicateColors.put( "Checkbox.Background.Default.Dark", "Checkbox.Background.Selected.Dark" );
		checkboxDuplicateColors.put( "Checkbox.Border.Default.Dark",     "Checkbox.Border.Selected.Dark" );
		checkboxDuplicateColors.put( "Checkbox.Focus.Thin.Default.Dark", "Checkbox.Focus.Thin.Selected.Dark" );
		@SuppressWarnings( "unchecked" )
		Map.Entry<String, String>[] entries = checkboxDuplicateColors.entrySet().toArray( new Map.Entry[checkboxDuplicateColors.size()] );
		for( Map.Entry<String, String> e : entries )
			checkboxDuplicateColors.put( e.getValue(), e.getKey() );
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
		void applyAdditionalDefaults( UIDefaults defaults ) {
			theme.applyProperties( defaults );
		}

		@Override
		protected ArrayList<Class<?>> getLafClassesForDefaultsLoading() {
			ArrayList<Class<?>> lafClasses = new ArrayList<>();
			lafClasses.add( FlatLaf.class );
			lafClasses.add( theme.dark ? FlatDarkLaf.class : FlatLightLaf.class );
			lafClasses.add( theme.dark ? FlatDarculaLaf.class : FlatIntelliJLaf.class );
			lafClasses.add( ThemeLaf.class );
			return lafClasses;
		}
	}
}
