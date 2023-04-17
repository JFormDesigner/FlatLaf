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

import java.awt.Color;
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
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;

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

	private final boolean isMaterialUILite;

	private final Map<String, String> colors;
	private final Map<String, Object> ui;
	private final Map<String, Object> icons;

	private Map<String, ColorUIResource> namedColors = Collections.emptyMap();

	/**
	 * Loads a IntelliJ .theme.json file from the given input stream,
	 * creates a Laf instance for it and sets it up.
	 *
	 * The input stream is automatically closed.
	 * Using a buffered input stream is not necessary.
	 *
	 * @since 1.2
	 */
	public static boolean setup( InputStream in ) {
		try {
		    return FlatLaf.setup( createLaf( in ) );
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere(  "FlatLaf: Failed to load IntelliJ theme", ex );
		    return false;
		}
	}

	/**
	 * @deprecated use {@link #setup(InputStream)} instead; this method will be removed in a future version
	 */
	@Deprecated
	public static boolean install( InputStream in ) {
		return setup( in );
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

		isMaterialUILite = author.equals( "Mallowigi" );

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

		// copy values
		for( Map.Entry<String, String> e : uiKeyCopying.entrySet() ) {
			Object value = defaults.get( e.getValue() );
			if( value != null )
				defaults.put( e.getKey(), value );
		}

		// IDEA does not paint button background if disabled, but FlatLaf does
		Object panelBackground = defaults.get( "Panel.background" );
		defaults.put( "Button.disabledBackground", panelBackground );
		defaults.put( "ToggleButton.disabledBackground", panelBackground );

		// fix Button borders
		copyIfNotSet( defaults, "Button.focusedBorderColor", "Component.focusedBorderColor", uiKeys );
		defaults.put( "Button.hoverBorderColor", defaults.get( "Button.focusedBorderColor" ) );
		defaults.put( "HelpButton.hoverBorderColor", defaults.get( "Button.focusedBorderColor" ) );

		// IDEA uses an SVG icon for the help button, but paints the background with Button.startBackground and Button.endBackground
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

		// fix DesktopPane background (use Panel.background and make it 5% darker/lighter)
		Color desktopBackgroundBase = defaults.getColor( "Panel.background" );
		Color desktopBackground = ColorFunctions.applyFunctions( desktopBackgroundBase,
			new ColorFunctions.HSLIncreaseDecrease( 2, dark, 5, false, true ) );
		defaults.put( "Desktop.background", new ColorUIResource( desktopBackground ) );

		// fix List and Table background colors in Material UI Lite themes
		if( isMaterialUILite ) {
			defaults.put( "List.background", defaults.get( "Tree.background" ) );
			defaults.put( "Table.background", defaults.get( "Tree.background" ) );
		}

		// limit tree row height
		int rowHeight = defaults.getInt( "Tree.rowHeight" );
		if( rowHeight > 22 )
			defaults.put( "Tree.rowHeight", 22 );

		// apply theme specific UI defaults at the end to allow overwriting
		for( Map.Entry<Object, Object> e : themeSpecificDefaults.entrySet() ) {
			Object key = e.getKey();
			Object value = e.getValue();

			// append styles to existing styles
			if( key instanceof String && ((String)key).startsWith( "[style]" ) ) {
				Object oldValue = defaults.get( key );
				if( oldValue != null )
					value = oldValue + "; " + value;
			}

			defaults.put( key, value );
		}
	}

	private Map<Object, Object> removeThemeSpecificDefaults( UIDefaults defaults ) {
		// search for theme specific UI defaults keys
		ArrayList<String> themeSpecificKeys = new ArrayList<>();
		for( Object key : defaults.keySet() ) {
			if( key instanceof String && ((String)key).startsWith( "[" ) && !((String)key).startsWith( "[style]" ) )
				themeSpecificKeys.add( (String) key );
		}

		// remove theme specific UI defaults and remember only those for current theme
		Map<Object, Object> themeSpecificDefaults = new HashMap<>();
		String currentThemePrefix = '[' + name.replace( ' ', '_' ) + ']';
		String currentThemeAndAuthorPrefix = '[' + name.replace( ' ', '_' ) + "---" + author.replace( ' ', '_' ) + ']';
		String currentAuthorPrefix = "[author-" + author.replace( ' ', '_' ) + ']';
		String allThemesPrefix = "[*]";
		String[] prefixes = { currentThemePrefix, currentThemeAndAuthorPrefix, currentAuthorPrefix, allThemesPrefix };
		for( String key : themeSpecificKeys ) {
			Object value = defaults.remove( key );
			for( String prefix : prefixes ) {
				if( key.startsWith( prefix ) ) {
					themeSpecificDefaults.put( key.substring( prefix.length() ), value );
					break;
				}
			}
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
			ColorUIResource color = parseColor( value );
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
			Map<String, Object> map = (Map<String, Object>)value;
			if( map.containsKey( "os.default" ) || map.containsKey( "os.windows" ) || map.containsKey( "os.mac" ) || map.containsKey( "os.linux" ) ) {
				String osKey = SystemInfo.isWindows ? "os.windows"
					: SystemInfo.isMacOS ? "os.mac"
					: SystemInfo.isLinux ? "os.linux" : null;
				if( osKey != null && map.containsKey( osKey ) )
					apply( key, map.get( osKey ), defaults, defaultsKeysCache, uiKeys );
				else if( map.containsKey( "os.default" ) )
					apply( key, map.get( "os.default" ), defaults, defaultsKeysCache, uiKeys );
			} else {
				for( Map.Entry<String, Object> e : map.entrySet() )
					apply( key + '.' + e.getKey(), e.getValue(), defaults, defaultsKeysCache, uiKeys );
			}
		} else {
			if( "".equals( value ) )
				return; // ignore empty value

			uiKeys.add( key );

			// ignore some properties that affect sizes
			if( key.endsWith( ".border" ) ||
				key.endsWith( ".rowHeight" ) ||
				key.equals( "ComboBox.padding" ) ||
				key.equals( "Spinner.padding" ) ||
				key.equals( "Tree.leftChildIndent" ) ||
				key.equals( "Tree.rightChildIndent" ) )
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
					uiValue = UIDefaultsLoader.parseValue( key, valueStr, null );
				} catch( RuntimeException ex ) {
					UIDefaultsLoader.logParseError( key, valueStr, ex, false );
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
					if( k.equals( "Desktop.background" ) ||
						k.equals( "DesktopIcon.background" ) )
					  continue;

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
		return (color != null) ? color : parseColor( value );
	}

	private ColorUIResource parseColor( String value ) {
		try {
			return UIDefaultsLoader.parseColor( value );
		} catch( IllegalArgumentException ex ) {
			return null;
		}
	}

	/**
	 * Because IDEA uses SVGs for check boxes and radio buttons, the colors for
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
				String checkBoxIconPrefix = "CheckBox.icon.";
				if( !dark && newKey.startsWith( checkBoxIconPrefix ) )
					newKey = "CheckBox.icon[filled].".concat( newKey.substring( checkBoxIconPrefix.length() ) );

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

		// update hover, pressed and focused colors
		if( checkboxModified ) {
			// for non-filled checkbox/radiobutton used in dark themes
			defaults.remove( "CheckBox.icon.focusWidth" );
			defaults.put( "CheckBox.icon.hoverBorderColor", defaults.get( "CheckBox.icon.focusedBorderColor" ) );

			// for filled checkbox/radiobutton used in light themes
			defaults.remove( "CheckBox.icon[filled].focusWidth" );
			defaults.put( "CheckBox.icon[filled].hoverBorderColor", defaults.get( "CheckBox.icon[filled].focusedBorderColor" ) );
			defaults.put( "CheckBox.icon[filled].focusedSelectedBackground", defaults.get( "CheckBox.icon[filled].selectedBackground" ) );

			if( dark ) {
				// IDEA Darcula checkBoxFocused.svg, checkBoxSelectedFocused.svg,
				// radioFocused.svg and radioSelectedFocused.svg
				// use opacity=".65" for the border
				// --> add alpha to focused border colors
				String[] focusedBorderColorKeys = {
					"CheckBox.icon.focusedBorderColor",
					"CheckBox.icon.focusedSelectedBorderColor",
					"CheckBox.icon[filled].focusedBorderColor",
					"CheckBox.icon[filled].focusedSelectedBorderColor",
				};
				for( String key : focusedBorderColorKeys ) {
					Color color = defaults.getColor( key );
					if( color != null ) {
						defaults.put( key, new ColorUIResource( new Color(
							(color.getRGB() & 0xffffff) | 0xa6000000, true ) ) );
					}
				}
			}
		}
	}

	private void copyIfNotSet( UIDefaults defaults, String destKey, String srcKey, Set<String> uiKeys ) {
		if( !uiKeys.contains( destKey ) )
			defaults.put( destKey, defaults.get( srcKey ) );
	}

	/** Rename UI default keys (key --> value). */
	private static final Map<String, String> uiKeyMapping = new HashMap<>();
	/** Copy UI default keys (value --> key). */
	private static final Map<String, String> uiKeyCopying = new HashMap<>();
	private static final Map<String, String> uiKeyInverseMapping = new HashMap<>();
	private static final Map<String, String> checkboxKeyMapping = new HashMap<>();
	private static final Map<String, String> checkboxDuplicateColors = new HashMap<>();

	static {
		// ComboBox
		uiKeyMapping.put( "ComboBox.background",                        "" ); // ignore
		uiKeyMapping.put( "ComboBox.nonEditableBackground",             "ComboBox.background" );
		uiKeyMapping.put( "ComboBox.ArrowButton.background",            "ComboBox.buttonEditableBackground" );
		uiKeyMapping.put( "ComboBox.ArrowButton.disabledIconColor",     "ComboBox.buttonDisabledArrowColor" );
		uiKeyMapping.put( "ComboBox.ArrowButton.iconColor",             "ComboBox.buttonArrowColor" );
		uiKeyMapping.put( "ComboBox.ArrowButton.nonEditableBackground", "ComboBox.buttonBackground" );
		uiKeyCopying.put( "ComboBox.buttonSeparatorColor",              "Component.borderColor" );
		uiKeyCopying.put( "ComboBox.buttonDisabledSeparatorColor",      "Component.disabledBorderColor" );

		// Component
		uiKeyMapping.put( "Component.inactiveErrorFocusColor",   "Component.error.borderColor" );
		uiKeyMapping.put( "Component.errorFocusColor",           "Component.error.focusedBorderColor" );
		uiKeyMapping.put( "Component.inactiveWarningFocusColor", "Component.warning.borderColor" );
		uiKeyMapping.put( "Component.warningFocusColor",         "Component.warning.focusedBorderColor" );

		// Link
		uiKeyMapping.put( "Link.activeForeground", "Component.linkColor" );

		// Menu
		uiKeyMapping.put( "Menu.border",                "Menu.margin" );
		uiKeyMapping.put( "MenuItem.border",            "MenuItem.margin" );
		uiKeyCopying.put( "CheckBoxMenuItem.margin",    "MenuItem.margin" );
		uiKeyCopying.put( "RadioButtonMenuItem.margin", "MenuItem.margin" );
		uiKeyMapping.put( "PopupMenu.border",           "PopupMenu.borderInsets" );
		uiKeyCopying.put( "MenuItem.underlineSelectionColor", "TabbedPane.underlineColor" );

		// IDEA uses List.selectionBackground also for menu selection
		uiKeyCopying.put( "Menu.selectionBackground",                "List.selectionBackground" );
		uiKeyCopying.put( "MenuItem.selectionBackground",            "List.selectionBackground" );
		uiKeyCopying.put( "CheckBoxMenuItem.selectionBackground",    "List.selectionBackground" );
		uiKeyCopying.put( "RadioButtonMenuItem.selectionBackground", "List.selectionBackground" );

		// ProgressBar
		uiKeyMapping.put( "ProgressBar.background",    "" ); // ignore
		uiKeyMapping.put( "ProgressBar.foreground",    "" ); // ignore
		uiKeyMapping.put( "ProgressBar.trackColor",    "ProgressBar.background" );
		uiKeyMapping.put( "ProgressBar.progressColor", "ProgressBar.foreground" );
		uiKeyCopying.put( "ProgressBar.selectionForeground", "ProgressBar.background" );
		uiKeyCopying.put( "ProgressBar.selectionBackground", "ProgressBar.foreground" );

		// ScrollBar
		uiKeyMapping.put( "ScrollBar.trackColor", "ScrollBar.track" );
		uiKeyMapping.put( "ScrollBar.thumbColor", "ScrollBar.thumb" );

		// Separator
		uiKeyMapping.put( "Separator.separatorColor", "Separator.foreground" );

		// Slider
		uiKeyMapping.put( "Slider.trackWidth", "" ); // ignore (used in Material Theme UI Lite)
		uiKeyCopying.put( "Slider.trackValueColor", "ProgressBar.foreground" );
		uiKeyCopying.put( "Slider.thumbColor", "ProgressBar.foreground" );
		uiKeyCopying.put( "Slider.trackColor", "ProgressBar.background" );

		// Spinner
		uiKeyCopying.put( "Spinner.buttonSeparatorColor",         "Component.borderColor" );
		uiKeyCopying.put( "Spinner.buttonDisabledSeparatorColor", "Component.disabledBorderColor" );

		// TabbedPane
		uiKeyCopying.put( "TabbedPane.selectedBackground",     "DefaultTabs.underlinedTabBackground" );
		uiKeyCopying.put( "TabbedPane.selectedForeground",     "DefaultTabs.underlinedTabForeground" );
		uiKeyCopying.put( "TabbedPane.inactiveUnderlineColor", "DefaultTabs.inactiveUnderlineColor" );

		// TitlePane
		uiKeyCopying.put( "TitlePane.inactiveBackground",     "TitlePane.background" );
		uiKeyMapping.put( "TitlePane.infoForeground",         "TitlePane.foreground" );
		uiKeyMapping.put( "TitlePane.inactiveInfoForeground", "TitlePane.inactiveForeground" );

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
		checkboxKeyMapping.put( "Checkbox.Focus.Thin.Selected", "CheckBox.icon.focusedSelectedBorderColor" );

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
			return getName();
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
