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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Function;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.ScaledNumber;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * The base class for all Flat LaFs.
 *
 * @author Karl Tauber
 */
public abstract class FlatLaf
	extends BasicLookAndFeel
{
	private static final String TYPE_PREFIX = "{";
	private static final String TYPE_PREFIX_END = "}";
	private static final String VARIABLE_PREFIX = "@";
	private static final String REF_PREFIX = VARIABLE_PREFIX + "@";
	private static final String OPTIONAL_PREFIX = "?";
	private static final String GLOBAL_PREFIX = "*.";

	private BasicLookAndFeel base;

	private String desktopPropertyName;
	private PropertyChangeListener desktopPropertyListener;

	private KeyEventPostProcessor mnemonicListener;
	private static boolean altKeyPressed;

	public static boolean install( LookAndFeel newLookAndFeel ) {
		try {
		    UIManager.setLookAndFeel( newLookAndFeel );
		    return true;
		} catch( Exception ex ) {
		    System.err.println( "Failed to initialize look and feel " + newLookAndFeel.getClass().getName() );
		    return false;
		}
	}

	@Override
	public String getID() {
		return getName();
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return true;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}

	@Override
	public void initialize() {
		getBase().initialize();

		super.initialize();

		// add mnemonic listener
		mnemonicListener = e -> {
			if( e.getKeyCode() == KeyEvent.VK_ALT )
				altKeyChanged( e.getID() == KeyEvent.KEY_PRESSED );
			return false;
		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor( mnemonicListener );

		// listen to desktop property changes to update UI if system font or scaling changes
		if( SystemInfo.IS_WINDOWS ) {
			// Windows 10 allows increasing font size independent of scaling:
			//   Settings > Ease of Access > Display > Make text bigger (100% - 225%)
			desktopPropertyName = "win.messagebox.font";
		} else if( SystemInfo.IS_LINUX ) {
			// Linux/Gnome allows extra scaling and larger text:
			//   Settings > Devices > Displays > Scale (100% or 200%)
			//   Settings > Universal access > Large Text (off or on, 125%)
			desktopPropertyName = "gnome.Xft/DPI";
		}
		if( desktopPropertyName != null ) {
			desktopPropertyListener = e -> {
				reSetLookAndFeel();
			};
			Toolkit.getDefaultToolkit().addPropertyChangeListener( desktopPropertyName, desktopPropertyListener );
		}
	}

	@Override
	public void uninitialize() {
		// remove desktop property listener
		if( desktopPropertyListener != null ) {
			Toolkit.getDefaultToolkit().removePropertyChangeListener( desktopPropertyName, desktopPropertyListener );
			desktopPropertyName = null;
			desktopPropertyListener = null;
		}

		// remove mnemonic listener
		if( mnemonicListener != null ) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor( mnemonicListener );
			mnemonicListener = null;
		}

		if( base != null )
			base.uninitialize();

		super.uninitialize();
	}

	/**
	 * Get/create base LaF. This is used to grab base UI defaults from different LaFs.
	 * E.g. on Mac from system dependent LaF, otherwise from Metal LaF.
	 */
	private BasicLookAndFeel getBase() {
		if( base == null ) {
			if( SystemInfo.IS_MAC ) {
				// use Mac Aqua LaF as base
				try {
					base = (BasicLookAndFeel) Class.forName( "com.apple.laf.AquaLookAndFeel" ).newInstance();
				} catch( Exception ex ) {
					ex.printStackTrace();
					throw new IllegalStateException();
				}
			} else
				base = new MetalLookAndFeel();
		}
		return base;
	}

	@Override
	public UIDefaults getDefaults() {
		UIDefaults defaults = getBase().getDefaults();

		// initialize some defaults (for overriding) that are used in basic UI delegates,
		// but are not set in MetalLookAndFeel or BasicLookAndFeel
		Color control = defaults.getColor( "control" );
		defaults.put( "EditorPane.disabledBackground", control );
		defaults.put( "EditorPane.inactiveBackground", control );
		defaults.put( "FormattedTextField.disabledBackground", control );
		defaults.put( "PasswordField.disabledBackground", control );
		defaults.put( "TextArea.disabledBackground", control );
		defaults.put( "TextArea.inactiveBackground", control );
		defaults.put( "TextField.disabledBackground", control );
		defaults.put( "TextPane.disabledBackground", control );
		defaults.put( "TextPane.inactiveBackground", control );

		// initialize some own defaults (for overriding)
		defaults.put( "Spinner.disabledBackground", control );
		defaults.put( "Spinner.disabledForeground", control );

		// remember MenuBarUI from Mac Aqua LaF if Mac screen menubar is enabled
		boolean useScreenMenuBar = SystemInfo.IS_MAC && "true".equals( System.getProperty( "apple.laf.useScreenMenuBar" ) );
		Object aquaMenuBarUI = useScreenMenuBar ? defaults.get( "MenuBarUI" ) : null;

		initFonts( defaults );
		loadDefaultsFromProperties( defaults );

		// use Aqua MenuBarUI if Mac screen menubar is enabled
		if( useScreenMenuBar )
			defaults.put( "MenuBarUI", aquaMenuBarUI );

		return defaults;
	}

	private void initFonts( UIDefaults defaults ) {
		FontUIResource uiFont = null;

		if( SystemInfo.IS_WINDOWS ) {
			Font winFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty( "win.messagebox.font" );
			if( winFont != null )
				uiFont = new FontUIResource( winFont );

		} else if( SystemInfo.IS_MAC ) {
			Font font = defaults.getFont( "Label.font" );

			if( SystemInfo.IS_MAC_OS_10_11_EL_CAPITAN_OR_LATER ) {
				// use San Francisco Text font
				font = new FontUIResource( ".SF NS Text", font.getStyle(), font.getSize() );
			}

			uiFont = (font instanceof FontUIResource) ? (FontUIResource) font : new FontUIResource( font );

		} else if( SystemInfo.IS_LINUX ) {
			Font font = LinuxFontPolicy.getFont();
			uiFont = (font instanceof FontUIResource) ? (FontUIResource) font : new FontUIResource( font );
		}

		if( uiFont == null )
			return;

		uiFont = UIScale.applyCustomScaleFactor( uiFont );

		// override fonts
		for( Object key : defaults.keySet() ) {
			if( key instanceof String && ((String)key).endsWith( ".font" ) )
				defaults.put( key, uiFont );
		}
		defaults.put( "MenuItem.acceleratorFont", uiFont );
	}

	/**
	 * Load properties associated to Flat LaF classes and add to UI defaults.
	 *
	 * Each class that extend this class may have its own .properties file
	 * in the same package as the class. Properties from superclasses are loaded
	 * first to give subclasses a chance to override defaults.
	 * E.g. if running FlatDarkLaf, then the FlatLaf.properties is loaded first
	 * and FlatDarkLaf.properties loaded second.
	 */
	private void loadDefaultsFromProperties( UIDefaults defaults ) {
		// determine classes in class hierarchy in reverse order
		ArrayList<Class<?>> lafClasses = new ArrayList<>();
		for( Class<?> lafClass = getClass();
			FlatLaf.class.isAssignableFrom( lafClass );
			lafClass = lafClass.getSuperclass() )
		{
			lafClasses.add( 0, lafClass );
		}

		try {
			// load properties files
			Properties properties = new Properties();
			ServiceLoader<FlatDefaultsAddon> addonLoader = ServiceLoader.load( FlatDefaultsAddon.class );
			for( Class<?> lafClass : lafClasses ) {
				// load core properties
				String propertiesName = "/" + lafClass.getName().replace( '.', '/' ) + ".properties";
				try( InputStream in = lafClass.getResourceAsStream( propertiesName ) ) {
					if( in != null )
						properties.load( in );
				}

				// load properties from addons
				for( FlatDefaultsAddon addon : addonLoader ) {
					try( InputStream in = addon.getDefaults( lafClass ) ) {
						if( in != null )
							properties.load( in );
					}
				}
			}

			Function<String, String> resolver = value -> {
				return resolveValue( properties, value );
			};

			// get globals, which override all other defaults that end with same suffix
			HashMap<String, Object> globals = new HashMap<>();
			for( Map.Entry<Object, Object> e : properties.entrySet() ) {
				String key = (String) e.getKey();
				if( !key.startsWith( GLOBAL_PREFIX ) )
					continue;

				String value = resolveValue( properties, (String) e.getValue() );
				globals.put( key.substring( GLOBAL_PREFIX.length() ), parseValue( key, value, resolver ) );
			}

			// override UI defaults with globals
			for( Object key : defaults.keySet() ) {
				if( key instanceof String && ((String)key).contains( "." ) ) {
					String skey = (String) key;
					String globalKey = skey.substring( skey.lastIndexOf( '.' ) + 1 );
					Object globalValue = globals.get( globalKey );
					if( globalValue != null )
						defaults.put( key, globalValue );
				}
			}

			// add non-global properties to UI defaults
			for( Map.Entry<Object, Object> e : properties.entrySet() ) {
				String key = (String) e.getKey();
				if( key.startsWith( VARIABLE_PREFIX ) || key.startsWith( GLOBAL_PREFIX ) )
					continue;

				String value = resolveValue( properties, (String) e.getValue() );
				defaults.put( key, parseValue( key, value, resolver ) );
			}
		} catch( IOException ex ) {
			ex.printStackTrace();
		}
	}

	private String resolveValue( Properties properties, String value ) {
		if( !value.startsWith( VARIABLE_PREFIX ) )
			return value;

		if( value.startsWith( REF_PREFIX ) )
			value = value.substring( REF_PREFIX.length() );

		boolean optional = false;
		if( value.startsWith( OPTIONAL_PREFIX ) ) {
			value = value.substring( OPTIONAL_PREFIX.length() );
			optional = true;
		}

		String newValue = properties.getProperty( value );
		if( newValue == null ) {
			if( optional )
				return "null";

			System.err.println( "variable or reference '" + value + "' not found" );
			throw new IllegalArgumentException( value );
		}

		return resolveValue( properties, newValue );
	}

	private enum ValueType { UNKNOWN, STRING, INTEGER, BORDER, ICON, INSETS, SIZE, COLOR, SCALEDNUMBER }

	private Object parseValue( String key, String value, Function<String, String> resolver ) {
		value = value.trim();

		// null, false, true
		switch( value ) {
			case "null":	return null;
			case "false":	return false;
			case "true":	return true;
		}

		ValueType valueType = ValueType.UNKNOWN;

		// check whether value type is specified in the value
		if( value.startsWith( TYPE_PREFIX ) ) {
			int end = value.indexOf( TYPE_PREFIX_END );
			if( end != -1 ) {
				try {
					String typeStr = value.substring( TYPE_PREFIX.length(), end );
					valueType = ValueType.valueOf( typeStr.toUpperCase( Locale.ENGLISH ) );

					// remove type from value
					value = value.substring( end + TYPE_PREFIX_END.length() );
				} catch( IllegalArgumentException ex ) {
					// ignore
				}
			}
		}

		// determine value type from key
		if( valueType == ValueType.UNKNOWN ) {
			if( key.endsWith( ".border" ) || key.endsWith( "Border" ) )
				valueType = ValueType.BORDER;
			else if( key.endsWith( ".icon" ) || key.endsWith( "Icon" ) )
				valueType = ValueType.ICON;
			else if( key.endsWith( ".margin" ) || key.endsWith( ".padding" ) ||
					 key.endsWith( "Margins" ) || key.endsWith( "Insets" ) )
				valueType = ValueType.INSETS;
			else if( key.endsWith( "Size" ) )
				valueType = ValueType.SIZE;
			else if( key.endsWith( "Width" ) || key.endsWith( "Height" ) )
				valueType = ValueType.INTEGER;
		}

		// parse value
		switch( valueType ) {
			case STRING:		return value;
			case INTEGER:		return parseInteger( value, true );
			case BORDER:		return parseBorder( value, resolver );
			case ICON:			return parseInstance( value );
			case INSETS:		return parseInsets( value );
			case SIZE:			return parseSize( value );
			case COLOR:			return parseColor( value, true );
			case SCALEDNUMBER:	return parseScaledNumber( value );
			case UNKNOWN:
			default:
				// colors
				ColorUIResource color = parseColor( value, false );
				if( color != null )
					return color;

				// integer
				Integer integer = parseInteger( value, false );
				if( integer != null )
					return integer;

				// string
				return value;
		}
	}

	private Object parseBorder( String value, Function<String, String> resolver ) {
		if( value.indexOf( ',' ) >= 0 ) {
			// top,left,bottom,right[,lineColor]
			List<String> parts = split( value, ',' );
			Insets insets = parseInsets( value );
			ColorUIResource lineColor = (parts.size() == 5)
				? parseColor( resolver.apply( parts.get( 4 ) ), true )
				: null;

			return (LazyValue) t -> {
				return (lineColor != null)
					? new FlatLineBorder( insets, lineColor )
					: new FlatEmptyBorder( insets );
			};
		} else
			return parseInstance( value );
	}

	private Object parseInstance( String value ) {
		return (LazyValue) t -> {
			try {
				return Class.forName( value ).newInstance();
			} catch( InstantiationException | IllegalAccessException | ClassNotFoundException ex ) {
				ex.printStackTrace();
				return null;
			}
		};
	}

	private Insets parseInsets( String value ) {
		List<String> numbers = split( value, ',' );
		try {
			return new InsetsUIResource(
				Integer.parseInt( numbers.get( 0 ) ),
				Integer.parseInt( numbers.get( 1 ) ),
				Integer.parseInt( numbers.get( 2 ) ),
				Integer.parseInt( numbers.get( 3 ) ) );
		} catch( NumberFormatException ex ) {
			System.err.println( "invalid insets '" + value + "'" );
			throw ex;
		}
	}

	private Dimension parseSize( String value ) {
		List<String> numbers = split( value, ',' );
		try {
			return new DimensionUIResource(
				Integer.parseInt( numbers.get( 0 ) ),
				Integer.parseInt( numbers.get( 1 ) ) );
		} catch( NumberFormatException ex ) {
			System.err.println( "invalid size '" + value + "'" );
			throw ex;
		}
	}

	private ColorUIResource parseColor( String value, boolean reportError ) {
		try {
			int rgb = Integer.parseInt( value, 16 );
			if( value.length() == 6 )
				return new ColorUIResource( rgb );
			if( value.length() == 8 )
				return new ColorUIResource( new Color( rgb, true ) );

			if( reportError )
				throw new NumberFormatException( value );
		} catch( NumberFormatException ex ) {
			if( reportError ) {
				System.err.println( "invalid color '" + value + "'" );
				throw ex;
			}
			// not a color --> ignore
		}
		return null;
	}

	private Integer parseInteger( String value, boolean reportError ) {
		try {
			return Integer.parseInt( value );
		} catch( NumberFormatException ex ) {
			if( reportError ) {
				System.err.println( "invalid integer '" + value + "'" );
				throw ex;
			}
		}
		return null;
	}

	private ScaledNumber parseScaledNumber( String value ) {
		try {
			return new ScaledNumber( Integer.parseInt( value ) );
		} catch( NumberFormatException ex ) {
			System.err.println( "invalid integer '" + value + "'" );
			throw ex;
		}
	}

	public static List<String> split( String str, char delim ) {
		ArrayList<String> strs = new ArrayList<>();
		int delimIndex = str.indexOf( delim );
		int index = 0;
		while( delimIndex >= 0 ) {
			strs.add( str.substring( index, delimIndex ) );
			index = delimIndex + 1;
			delimIndex = str.indexOf( delim, index );
		}
		strs.add( str.substring( index ) );

		return strs;
	}

	private static void reSetLookAndFeel() {
		EventQueue.invokeLater( () -> {
			try {
				// re-set current LaF
				LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
				UIManager.setLookAndFeel( lookAndFeel );

				// must fire property change events ourself because old and new LaF are the same
				PropertyChangeEvent e = new PropertyChangeEvent( UIManager.class, "lookAndFeel", lookAndFeel, lookAndFeel );
				for( PropertyChangeListener l : UIManager.getPropertyChangeListeners() )
					l.propertyChange( e );

				// update UI
				updateUI();
			} catch( UnsupportedLookAndFeelException ex ) {
				ex.printStackTrace();
			}
		} );
	}

	/**
	 * Update UI of all application windows.
	 * Invoke after changing LaF.
	 */
	public static void updateUI() {
		for( Window w : Window.getWindows() )
			SwingUtilities.updateComponentTreeUI( w );
	}

	public static boolean isShowMnemonics() {
		return altKeyPressed || !UIManager.getBoolean( "Component.hideMnemonics" );
	}

	private static void altKeyChanged( boolean pressed ) {
		if( pressed == altKeyPressed )
			return;

		altKeyPressed = pressed;

		// check whether it is necessary to repaint
		if( !UIManager.getBoolean( "Component.hideMnemonics" ) )
			return;

		// get focus owner
		Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if( focusOwner == null )
			return;

		// get focused window
		Window window = SwingUtilities.windowForComponent( focusOwner );
		if( window == null )
			return;

		// repaint components with mnemonics in focused window
		repaintMnemonics( window );
	}

	private static void repaintMnemonics( Container container ) {
		for( Component c : container.getComponents() ) {
			if( hasMnemonic( c ) )
				c.repaint();

			if( c instanceof Container )
				repaintMnemonics( (Container) c );
		}
	}

	private static boolean hasMnemonic( Component c ) {
		if( c instanceof JLabel && ((JLabel)c).getDisplayedMnemonicIndex() >= 0 )
			return true;

		if( c instanceof AbstractButton && ((AbstractButton)c).getDisplayedMnemonicIndex() >= 0 )
			return true;

		if( c instanceof JTabbedPane ) {
			JTabbedPane tabPane = (JTabbedPane) c;
			int tabCount = tabPane.getTabCount();
			for( int i = 0; i < tabCount; i++ ) {
				if( tabPane.getDisplayedMnemonicIndexAt( i ) >= 0 )
					return true;
			}
		}

		return false;
	}
}
