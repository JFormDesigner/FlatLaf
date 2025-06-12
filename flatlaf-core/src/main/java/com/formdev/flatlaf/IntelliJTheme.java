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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import com.formdev.flatlaf.json.Json;
import com.formdev.flatlaf.json.ParseException;
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

	private Map<String, String> jsonColors;
	private Map<String, Object> jsonUI;
	private Map<String, Object> jsonIcons;

	private Map<String, String> namedColors = Collections.emptyMap();

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
			LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to load IntelliJ theme", ex );
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

	    jsonColors = (Map<String, String>) json.get( "colors" );
	    jsonUI = (Map<String, Object>) json.get( "ui" );
	    jsonIcons = (Map<String, Object>) json.get( "icons" );
	}

	private void applyProperties( Properties properties ) {
		if( jsonUI == null )
			return;

		put( properties, "Component.isIntelliJTheme", "true" );

		// enable button shadows
		put( properties, "Button.paintShadow", "true" );
		put( properties, "Button.shadowWidth", dark ? "2" : "1" );

		Map<String, String> themeSpecificProps = removeThemeSpecificProps( properties );
		Set<String> jsonUIKeys = new HashSet<>();

		// Json node "colors"
		loadNamedColors( properties, jsonUIKeys );

		// convert Json "ui" structure to UI properties
		for( Map.Entry<String, Object> e : jsonUI.entrySet() )
			apply( e.getKey(), e.getValue(), properties, jsonUIKeys );

		// set FlatLaf variables
		copyIfSetInJson( properties, jsonUIKeys, "@background", "Panel.background", "*.background" );
		copyIfSetInJson( properties, jsonUIKeys, "@foreground", "CheckBox.foreground", "*.foreground" );
		copyIfSetInJson( properties, jsonUIKeys, "@accentBaseColor",
			"ColorPalette.accent", // Material UI Lite, Hiberbee
			"ColorPalette.accentColor", // Dracula, One Dark
			"ProgressBar.foreground",
			"*.selectionBackground" );
		copyIfSetInJson( properties, jsonUIKeys, "@accentUnderlineColor", "*.underlineColor", "TabbedPane.underlineColor" );
		copyIfSetInJson( properties, jsonUIKeys, "@selectionBackground", "*.selectionBackground" );
		copyIfSetInJson( properties, jsonUIKeys, "@selectionForeground", "*.selectionForeground" );
		copyIfSetInJson( properties, jsonUIKeys, "@selectionInactiveBackground", "*.selectionInactiveBackground" );
		copyIfSetInJson( properties, jsonUIKeys, "@selectionInactiveForeground", "*.selectionInactiveForeground" );

		// Json node "icons/ColorPalette"
		applyIconsColorPalette( properties );

		// apply "CheckBox.icon." colors
		applyCheckBoxColors( properties );

		// copy values
		for( Map.Entry<String, String> e : uiKeyCopying.entrySet() ) {
			Object value = properties.get( e.getValue() );
			if( value != null )
				put( properties, e.getKey(), value );
		}

		// IDEA does not paint button background if disabled, but FlatLaf does
		put( properties, "Button.disabledBackground", "@disabledBackground" );
		put( properties, "ToggleButton.disabledBackground", "@disabledBackground" );

		// fix Button
		fixStartEnd( properties, jsonUIKeys, "Button.startBackground", "Button.endBackground", "Button.background" );
		fixStartEnd( properties, jsonUIKeys, "Button.startBorderColor", "Button.endBorderColor", "Button.borderColor" );
		fixStartEnd( properties, jsonUIKeys, "Button.default.startBackground", "Button.default.endBackground", "Button.default.background" );
		fixStartEnd( properties, jsonUIKeys, "Button.default.startBorderColor", "Button.default.endBorderColor", "Button.default.borderColor" );

		// IDEA uses TextField.background for editable ComboBox and Spinner
		Object textFieldBackground = get( properties, themeSpecificProps, "TextField.background" );
		put( properties, "ComboBox.editableBackground", textFieldBackground );
		put( properties, "Spinner.background", textFieldBackground );

		// some themes specify colors for TextField.background, but forget to specify it for other components
		// (probably because those components are not used in IntelliJ IDEA)
		putAll( properties, textFieldBackground,
			"EditorPane.background",
			"FormattedTextField.background",
			"PasswordField.background",
			"TextArea.background",
			"TextPane.background"
		);
		putAll( properties, get( properties, themeSpecificProps, "TextField.selectionBackground" ),
			"EditorPane.selectionBackground",
			"FormattedTextField.selectionBackground",
			"PasswordField.selectionBackground",
			"TextArea.selectionBackground",
			"TextPane.selectionBackground"
		);
		putAll( properties, get( properties, themeSpecificProps, "TextField.selectionForeground" ),
			"EditorPane.selectionForeground",
			"FormattedTextField.selectionForeground",
			"PasswordField.selectionForeground",
			"TextArea.selectionForeground",
			"TextPane.selectionForeground"
		);

		// fix disabled and not-editable backgrounds for text components, combobox and spinner
		// (IntelliJ IDEA does not use those colors; instead it used background color of parent)
		putAll( properties, "@disabledBackground",
			"ComboBox.disabledBackground",
			"EditorPane.disabledBackground", "EditorPane.inactiveBackground",
			"FormattedTextField.disabledBackground", "FormattedTextField.inactiveBackground",
			"PasswordField.disabledBackground", "PasswordField.inactiveBackground",
			"Spinner.disabledBackground",
			"TextArea.disabledBackground", "TextArea.inactiveBackground",
			"TextField.disabledBackground", "TextField.inactiveBackground",
			"TextPane.disabledBackground", "TextPane.inactiveBackground"
		);

		// fix DesktopPane background (use Panel.background and make it 5% darker/lighter)
		put( properties, "Desktop.background", dark ? "lighten($Panel.background,5%)" : "darken($Panel.background,5%)" );

		// limit tree row height
		String rowHeightStr = (String) properties.get( "Tree.rowHeight" );
		int rowHeight = (rowHeightStr != null) ? Integer.parseInt( rowHeightStr ) : 0;
		if( rowHeight > 22 )
			put( properties, "Tree.rowHeight", "22" );

		// get (and remove) theme specific wildcard replacements, which override all other properties that end with same suffix
		HashMap<String, String> wildcardProps = new HashMap<>();
		Iterator<Map.Entry<String, String>> it = themeSpecificProps.entrySet().iterator();
		while( it.hasNext() ) {
			Map.Entry<String, String> e = it.next();
			String key = e.getKey();
			if( key.startsWith( "*." ) ) {
				wildcardProps.put( key, e.getValue() );
				it.remove();
			}
		}

		// override properties with theme specific wildcard replacements
		if( !wildcardProps.isEmpty() ) {
			for( Map.Entry<String, String> e : wildcardProps.entrySet() )
				applyWildcard( properties, e.getKey(), e.getValue() );
		}

		// apply theme specific properties at the end to allow overwriting
		for( Map.Entry<String, String> e : themeSpecificProps.entrySet() ) {
			String key = e.getKey();
			String value = e.getValue();

			// append styles to existing styles
			if( key.startsWith( "[style]" ) ) {
				String oldValue = (String) properties.get( key );
				if( oldValue != null )
					value = oldValue + "; " + value;
			}

			put( properties, key, value );
		}

		// let Java release memory
		jsonColors = null;
		jsonUI = null;
		jsonIcons = null;
	}

	private String get( Properties properties, Map<String, String> themeSpecificProps, String key ) {
		return themeSpecificProps.getOrDefault( key, (String) properties.get( key ) );
	}

	private void put( Properties properties, Object key, Object value ) {
		if( value != null )
			properties.put( key, value );
		else
			properties.remove( key );
	}

	private void putAll( Properties properties, Object value, String... keys ) {
		for( String key : keys )
			put( properties, key, value );
	}

	private void copyIfSetInJson( Properties properties, Set<String> jsonUIKeys, String destKey, String... srcKeys ) {
		for( String srcKey : srcKeys ) {
			if( jsonUIKeys.contains( srcKey ) ) {
				Object value = properties.get( srcKey );
				if( value != null ) {
					put( properties, destKey, value );
					break;
				}
			}
		}
	}

	private void fixStartEnd( Properties properties, Set<String> jsonUIKeys, String startKey, String endKey, String key ) {
		if( jsonUIKeys.contains( startKey ) && jsonUIKeys.contains( endKey ) )
			put( properties, key, "$" + startKey );
	}

	private Map<String, String> removeThemeSpecificProps( Properties properties ) {
		// search for theme specific properties keys
		ArrayList<String> themeSpecificKeys = new ArrayList<>();
		for( Object key : properties.keySet() ) {
			if( ((String)key).startsWith( "{" ) )
				themeSpecificKeys.add( (String) key );
		}

		// special prefixes (priority from highest to lowest)
		String currentThemePrefix = '{' + name.replace( ' ', '_' ) + '}';
		String currentThemeAndAuthorPrefix = '{' + name.replace( ' ', '_' ) + "---" + author.replace( ' ', '_' ) + '}';
		String currentAuthorPrefix = "{author-" + author.replace( ' ', '_' ) + '}';
		String lightOrDarkPrefix = dark ? "{*-dark}" : "{*-light}";
		String allThemesPrefix = "{*}";
		String[] prefixes = { currentThemePrefix, currentThemeAndAuthorPrefix, currentAuthorPrefix, lightOrDarkPrefix, allThemesPrefix };

		// collect values for special prefixes in its own maps
		@SuppressWarnings( "unchecked" )
		Map<String, String>[] maps = new Map[prefixes.length];
		for( int i = 0; i < maps.length; i++ )
			maps[i] = new HashMap<>();

		// remove theme specific properties and remember only those for current theme
		for( String key : themeSpecificKeys ) {
			String value = (String) properties.remove( key );
			for( int i = 0; i < prefixes.length; i++ ) {
				String prefix = prefixes[i];
				if( key.startsWith( prefix ) ) {
					maps[i].put( key.substring( prefix.length() ), value );
					break;
				}
			}
		}

		// copy values into single map (from lowest to highest priority)
		Map<String, String> themeSpecificProps = new HashMap<>();
		for( int i = maps.length - 1; i >= 0; i-- )
			themeSpecificProps.putAll( maps[i] );
		return themeSpecificProps;
	}

	/**
	 * http://www.jetbrains.org/intellij/sdk/docs/reference_guide/ui_themes/themes_customize.html#defining-named-colors
	 */
	private void loadNamedColors( Properties properties, Set<String> jsonUIKeys ) {
		if( jsonColors == null )
			return;

		namedColors = new HashMap<>();

		for( Map.Entry<String, String> e : jsonColors.entrySet() ) {
			String value = e.getValue();
			if( canParseColor( value ) ) {
				String key = e.getKey();
				namedColors.put( key, value );

				String uiKey = "ColorPalette." + key;
				put( properties, uiKey, value );

				// this is only necessary for copyIfSetInJson() (used for accent color)
				jsonUIKeys.add( uiKey );
			}
		}
	}

	/**
	 * http://www.jetbrains.org/intellij/sdk/docs/reference_guide/ui_themes/themes_customize.html#custom-ui-control-colors
	 */
	@SuppressWarnings( "unchecked" )
	private void apply( String key, Object value, Properties properties, Set<String> jsonUIKeys ) {
		if( value instanceof Map ) {
			Map<String, Object> map = (Map<String, Object>)value;
			if( map.containsKey( "os.default" ) || map.containsKey( "os.windows" ) || map.containsKey( "os.mac" ) || map.containsKey( "os.linux" ) ) {
				String osKey = SystemInfo.isWindows ? "os.windows"
					: SystemInfo.isMacOS ? "os.mac"
					: SystemInfo.isLinux ? "os.linux" : null;
				if( osKey != null && map.containsKey( osKey ) )
					apply( key, map.get( osKey ), properties, jsonUIKeys );
				else if( map.containsKey( "os.default" ) )
					apply( key, map.get( "os.default" ), properties, jsonUIKeys );
			} else {
				for( Map.Entry<String, Object> e : map.entrySet() )
					apply( key + '.' + e.getKey(), e.getValue(), properties, jsonUIKeys );
			}
		} else {
			if( "".equals( value ) )
				return; // ignore empty value

			// ignore some properties that affect sizes
			if( key.endsWith( ".border" ) ||
				key.endsWith( ".rowHeight" ) ||
				key.equals( "ComboBox.padding" ) ||
				key.equals( "Spinner.padding" ) ||
				key.equals( "Tree.leftChildIndent" ) ||
				key.equals( "Tree.rightChildIndent" ) )
			  return; // ignore

			// ignore icons
			if( key.endsWith( "Icon" ) )
				return; // ignore

			// map keys
			key = uiKeyMapping.getOrDefault( key, key );
			if( key.isEmpty() )
				return; // ignore key

			// exclude properties (1st level)
			int dot = key.indexOf( '.' );
			if( dot > 0 && uiKeyExcludesStartsWith.contains( key.substring( 0, dot + 1 ) ) )
				return;

			// exclude properties (2st level)
			int dot2 = (dot > 0) ? key.indexOf( '.', dot + 1 ) : -1;
			if( dot2 > 0 && uiKeyExcludesStartsWith.contains( key.substring( 0, dot2 + 1 ) ) )
				return;

			// exclude properties (contains)
			for( String s : uiKeyExcludesContains ) {
				if( key.contains( s ) )
					return;
			}

			if( uiKeyDoNotOverride.contains( key ) && jsonUIKeys.contains( key ) )
				return;

			jsonUIKeys.add( key );

			String valueStr = value.toString().trim();

			// map named colors
			String uiValue = namedColors.get( valueStr );

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
					UIDefaultsLoader.parseValue( key, valueStr, null );
					uiValue = valueStr;
				} catch( RuntimeException ex ) {
					UIDefaultsLoader.logParseError( key, valueStr, ex, true );
					return; // ignore invalid value
				}
			}

			// wildcards
			if( applyWildcard( properties, key, uiValue ) )
				return;

			put( properties, key, uiValue );
		}
	}

	private boolean applyWildcard( Properties properties, String key, String value ) {
		if( !key.startsWith( "*." ) )
			return false;

		String tail = key.substring( 1 );

		// because we can not iterate over the properties keys while
		// modifying properties in the same loop, we have to copy the keys
		String[] keys = properties.keySet().toArray( new String[properties.size()] );

		// replace all values in properties that match the wildcard key
		for( String k : keys ) {
			if( k.startsWith( "*" ) ||
				k.startsWith( "@" ) ||
				k.startsWith( "HelpButton." ) ||
				k.startsWith( "JX" ) ||
				k.startsWith( "Jide" ) ||
				k.startsWith( "ProgressBar.selection" ) ||
				k.startsWith( "TitlePane." ) ||
				k.startsWith( "ToggleButton.tab." ) ||
				k.equals( "Desktop.background" ) ||
				k.equals( "DesktopIcon.background" ) ||
				k.equals( "TabbedPane.focusColor" ) ||
				k.endsWith( ".hoverBackground" ) ||
				k.endsWith( ".pressedBackground" ) )
			  continue;

			// support replacing of mapped keys
			// (e.g. set ComboBox.buttonEditableBackground to *.background
			// because it is mapped from ComboBox.ArrowButton.background)
			String km = uiKeyInverseMapping.getOrDefault( k, k );
			if( km.endsWith( tail ) && !k.startsWith( "CheckBox.icon." ) )
				put( properties, k, value );
		}

		// Note: also add wildcards to properties and let UIDefaultsLoader
		//       process it on BasicLookAndFeel UI defaults
		put( properties, key, value );

		return true;
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

	private void applyIconsColorPalette( Properties properties ) {
		if( jsonIcons == null )
			return;

		Object palette = jsonIcons.get( "ColorPalette" );
		if( !(palette instanceof Map) )
			return;

		@SuppressWarnings( "unchecked" )
		Map<String, Object> colorPalette = (Map<String, Object>) palette;
		for( Map.Entry<String, Object> e : colorPalette.entrySet() ) {
			String key = e.getKey();
			Object value = e.getValue();
			if( key.startsWith( "Checkbox." ) || key.startsWith( "#" ) || !(value instanceof String) )
				continue;

			if( dark )
				key = StringUtils.removeTrailing( key, ".Dark" );

			String color = toColor( (String) value );
			if( color != null )
				put( properties, key, color );
		}
	}

	private String toColor( String value ) {
		if( value.startsWith( "##" ) )
			value = fixColorIfValid( value.substring( 1 ), value );

		// map named colors
		String color = namedColors.get( value );

		// parse color
		return (color != null) ? color : (canParseColor( value ) ? value : null);
	}

	private boolean canParseColor( String value ) {
		try {
			return UIDefaultsLoader.parseColor( value ) != null;
		} catch( IllegalArgumentException ex ) {
			LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to parse color: '" + value + '\'', ex );
			return false;
		}
	}

	/**
	 * Because IDEA uses SVGs for check boxes and radio buttons, the colors for
	 * these two components are specified in "icons > ColorPalette".
	 * FlatLaf uses vector icons and expects colors for the two components in properties.
	 */
	private void applyCheckBoxColors( Properties properties ) {
		if( jsonIcons == null )
			return;

		Object palette = jsonIcons.get( "ColorPalette" );
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

			if( dark )
				key = StringUtils.removeTrailing( key, ".Dark" );

			String newKey = checkboxKeyMapping.get( key );
			if( newKey != null ) {
				String checkBoxIconPrefix = "CheckBox.icon.";
				if( !dark && newKey.startsWith( checkBoxIconPrefix ) )
					newKey = "CheckBox.icon[filled].".concat( newKey.substring( checkBoxIconPrefix.length() ) );

				String color = toColor( (String) value );
				if( color != null ) {
					put( properties, newKey, color );

					String key2 = checkboxDuplicateColors.get( key + ".Dark");
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
							put( properties, newKey2, color );
					}
				}

				checkboxModified = true;
			}
		}

		// update hover, pressed and focused colors
		if( checkboxModified ) {
			// for non-filled checkbox/radiobutton used in dark themes
			properties.remove( "CheckBox.icon.focusWidth" );
			put( properties, "CheckBox.icon.hoverBorderColor", properties.get( "CheckBox.icon.focusedBorderColor" ) );

			// for filled checkbox/radiobutton used in light themes
			properties.remove( "CheckBox.icon[filled].focusWidth" );
			put( properties, "CheckBox.icon[filled].hoverBorderColor", properties.get( "CheckBox.icon[filled].focusedBorderColor" ) );
			put( properties, "CheckBox.icon[filled].focusedSelectedBackground", properties.get( "CheckBox.icon[filled].selectedBackground" ) );

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
					Object color = properties.get( key );
					if( color != null )
						put( properties, key, "fade(" + color + ", 65%)" );
				}
			}
		}
	}

	private static final Set<String> uiKeyExcludesStartsWith;
	private static final String[] uiKeyExcludesContains;
	private static final Set<String> uiKeyDoNotOverride;
	/** Rename UI default keys (key --> value). */
	private static final Map<String, String> uiKeyMapping = new HashMap<>();
	/** Copy UI default keys (value --> key). */
	private static final Map<String, String> uiKeyCopying = new LinkedHashMap<>();
	private static final Map<String, String> uiKeyInverseMapping = new HashMap<>();
	private static final Map<String, String> checkboxKeyMapping = new HashMap<>();
	private static final Map<String, String> checkboxDuplicateColors = new HashMap<>();

	static {
		// IntelliJ UI properties that are not used in FlatLaf
		uiKeyExcludesStartsWith = new HashSet<>( Arrays.asList(
			"ActionButton.", "ActionToolbar.", "ActionsList.", "AppInspector.", "AssignedMnemonic.", "Autocomplete.",
			"AvailableMnemonic.",
			"Badge.", "Banner.", "BigSpinner.", "Bookmark.", "BookmarkIcon.", "BookmarkMnemonicAssigned.", "BookmarkMnemonicAvailable.",
			"BookmarkMnemonicCurrent.", "BookmarkMnemonicIcon.", "Borders.", "Breakpoint.",
			"Canvas.", "CellEditor.", "Code.", "CodeWithMe.", "ColumnControlButton.", "CombinedDiff.", "ComboBoxButton.",
			"CompilationCharts.", "CompletionPopup.", "ComplexPopup.", "Content.", "ContextHelp.", "CurrentMnemonic.", "Counter.",
			"Debugger.", "DebuggerPopup.", "DebuggerTabs.", "DefaultTabs.", "Dialog.", "DialogWrapper.",
			"DisclosureButton.", "DragAndDrop.",
			"Editor.", "EditorGroupsTabs.", "EditorTabs.",
			"FileColor.", "FindPopup.", "FlameGraph.", "Focus.",
			"Git.", "Github.", "GotItTooltip.", "Group.", "Gutter.", "GutterTooltip.",
			"HeaderColor.", "HelpTooltip.", "Hg.",
			"IconBadge.", "InformationHint.", "InlineBanner.", "InplaceRefactoringPopup.",
			"Lesson.", "LineProfiler.", "Link.", "LiveIndicator.",
			"MainMenu.", "MainToolbar.", "MainWindow.", "MemoryIndicator.", "MlModelBinding.", "MnemonicIcon.",
			"NavBar.", "NewClass.", "NewPSD.", "Notification.", "Notifications.", "NotificationsToolwindow.",
			"OnePixelDivider.", "OptionButton.", "Outline.",
			"ParameterInfo.", "PresentationAssistant.", "Plugins.", "Profiler.", "ProgressIcon.", "PsiViewer.",
			"Resizable.", "Review.", "ReviewList.", "RunToolbar.", "RunWidget.",
			"ScreenView.", "SearchEverywhere.", "SearchFieldWithExtension.", "SearchMatch.", "SearchOption.",
			"SearchResults.", "SegmentedButton.", "Settings.", "SidePanel.", "Space.", "SpeedSearch.", "StateWidget.",
			"StatusBar.", "StripeToolbar.",
			"Tag.", "TipOfTheDay.", "ToolbarComboWidget.", "ToolWindow.", "TrialWidget.",
			"UIDesigner.", "UnattendedHostStatus.",
			"ValidationTooltip.", "VersionControl.",
			"WelcomeScreen.",

			// lower case
			"darcula.", "dropArea.", "icons.", "intellijlaf.", "macOSWindow.", "material.", "tooltips.",

			// possible typos in .theme.json files
			"Checkbox.", "Toolbar.", "Tooltip.", "UiDesigner.", "link."
		) );
		uiKeyExcludesContains = new String[] {
			".darcula."
		};

		uiKeyDoNotOverride = new HashSet<>( Arrays.asList(
			"TabbedPane.selectedForeground"
		) );

		// "*."
		uiKeyMapping.put( "*.fontFace", "" ); // ignore (used in OnePauintxi themes)
		uiKeyMapping.put( "*.fontSize", "" ); // ignore (used in OnePauintxi themes)

		// Button
		uiKeyMapping.put( "Button.minimumSize", "" ); // ignore (used in Material Theme UI Lite)

		// CheckBox.iconSize
		uiKeyMapping.put( "CheckBox.iconSize", "" ); // ignore (used in Rider themes)

		// ComboBox
		uiKeyMapping.put( "ComboBox.background",                        "" ); // ignore
		uiKeyMapping.put( "ComboBox.buttonBackground",                  "" ); // ignore
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
		uiKeyMapping.put( "Component.inactiveSuccessFocusColor", "Component.success.borderColor" );
		uiKeyMapping.put( "Component.successFocusColor",         "Component.success.focusedBorderColor" );

		// Label
		uiKeyMapping.put( "Label.disabledForegroundColor", "" ); // ignore (used in Material Theme UI Lite)

		// Link
		uiKeyMapping.put( "Link.activeForeground", "Component.linkColor" );

		// Menu
		uiKeyMapping.put( "Menu.border",                "Menu.margin" );
		uiKeyMapping.put( "MenuItem.border",            "MenuItem.margin" );
		uiKeyMapping.put( "PopupMenu.border",           "PopupMenu.borderInsets" );

		// IDEA uses List.selectionBackground also for menu selection
		uiKeyCopying.put( "Menu.selectionBackground",                "List.selectionBackground" );
		uiKeyCopying.put( "MenuItem.selectionBackground",            "List.selectionBackground" );
		uiKeyCopying.put( "CheckBoxMenuItem.selectionBackground",    "List.selectionBackground" );
		uiKeyCopying.put( "RadioButtonMenuItem.selectionBackground", "List.selectionBackground" );

		// ProgressBar: IDEA uses ProgressBar.trackColor and ProgressBar.progressColor
		uiKeyMapping.put( "ProgressBar.background",    "" ); // ignore
		uiKeyMapping.put( "ProgressBar.foreground",    "" ); // ignore
		uiKeyMapping.put( "ProgressBar.trackColor",    "ProgressBar.background" );
		uiKeyMapping.put( "ProgressBar.progressColor", "ProgressBar.foreground" );

		// RadioButton
		uiKeyMapping.put( "RadioButton.iconSize", "" ); // ignore (used in Rider themes)

		// ScrollBar
		uiKeyMapping.put( "ScrollBar.trackColor", "ScrollBar.track" );
		uiKeyMapping.put( "ScrollBar.thumbColor", "ScrollBar.thumb" );

		// Separator
		uiKeyMapping.put( "Separator.separatorColor", "Separator.foreground" );

		// Slider
		uiKeyMapping.put( "Slider.buttonColor", "Slider.thumbColor" );
		uiKeyMapping.put( "Slider.buttonBorderColor", "" ); // ignore
		uiKeyMapping.put( "Slider.thumb", "" ); // ignore (used in Material Theme UI Lite)
		uiKeyMapping.put( "Slider.track", "" ); // ignore (used in Material Theme UI Lite)
		uiKeyMapping.put( "Slider.trackDisabled", "" ); // ignore (used in Material Theme UI Lite)
		uiKeyMapping.put( "Slider.trackWidth", "" ); // ignore (used in Material Theme UI Lite)

		// TabbedPane
		uiKeyMapping.put( "DefaultTabs.underlinedTabBackground", "TabbedPane.selectedBackground" );
		uiKeyMapping.put( "DefaultTabs.underlinedTabForeground", "TabbedPane.selectedForeground" );
		uiKeyMapping.put( "DefaultTabs.inactiveUnderlineColor",  "TabbedPane.inactiveUnderlineColor" );
		uiKeyMapping.put( "TabbedPane.tabAreaInsets", "" ); // ignore (used in Material Theme UI Lite)

		// TableHeader
		uiKeyMapping.put( "TableHeader.cellBorder", "" ); // ignore (used in Material Theme UI Lite)
		uiKeyMapping.put( "TableHeader.height", "" ); // ignore (used in Material Theme UI Lite)

		// TitlePane
		uiKeyMapping.put( "TitlePane.infoForeground",         "TitlePane.foreground" );
		uiKeyMapping.put( "TitlePane.inactiveInfoForeground", "TitlePane.inactiveForeground" );

		for( Map.Entry<String, String> e : uiKeyMapping.entrySet() )
			uiKeyInverseMapping.put( e.getValue(), e.getKey() );

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
		void applyAdditionalProperties( Properties properties ) {
			theme.applyProperties( properties );
		}

		@Override
		protected ArrayList<Class<?>> getLafClassesForDefaultsLoading() {
			ArrayList<Class<?>> lafClasses = UIDefaultsLoader.getLafClassesForDefaultsLoading( getClass() );
			lafClasses.add( 1, theme.dark ? FlatDarkLaf.class : FlatLightLaf.class );
			lafClasses.add( 2, theme.dark ? FlatDarculaLaf.class : FlatIntelliJLaf.class );
			return lafClasses;
		}
	}
}
