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
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.ActiveValue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.text.StyleContext;
import javax.swing.text.html.HTMLEditorKit;
import com.formdev.flatlaf.ui.FlatNativeWindowBorder;
import com.formdev.flatlaf.ui.FlatPopupFactory;
import com.formdev.flatlaf.ui.FlatRootPaneUI;
import com.formdev.flatlaf.util.GrayFilter;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.MultiResolutionImageSupport;
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
	private static final String DESKTOPFONTHINTS = "awt.font.desktophints";

	private static List<Object> customDefaultsSources;

	private String desktopPropertyName;
	private String desktopPropertyName2;
	private PropertyChangeListener desktopPropertyListener;

	private static boolean aquaLoaded;
	private static boolean updateUIPending;

	private PopupFactory oldPopupFactory;
	private MnemonicHandler mnemonicHandler;

	private Consumer<UIDefaults> postInitialization;

	/**
	 * Sets the application look and feel to the given LaF
	 * using {@link UIManager#setLookAndFeel(javax.swing.LookAndFeel)}.
	 */
	public static boolean setup( LookAndFeel newLookAndFeel ) {
		try {
			UIManager.setLookAndFeel( newLookAndFeel );
			return true;
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to setup look and feel '" + newLookAndFeel.getClass().getName() + "'.", ex );
			return false;
		}
	}

	/**
	 * @deprecated use {@link #setup(LookAndFeel)} instead; this method will be removed in a future version
	 */
	@Deprecated
	public static boolean install( LookAndFeel newLookAndFeel ) {
		return setup( newLookAndFeel );
	}

	/**
	 * Adds the given look and feel to the set of available look and feels.
	 * <p>
	 * Useful if your application uses {@link UIManager#getInstalledLookAndFeels()}
	 * to query available LaFs and display them to the user in a combobox.
	 */
	public static void installLafInfo( String lafName, Class<? extends LookAndFeel> lafClass ) {
		UIManager.installLookAndFeel( new UIManager.LookAndFeelInfo( lafName, lafClass.getName() ) );
	}

	/**
	 * Returns the look and feel identifier.
	 * <p>
	 * Syntax: "FlatLaf - ${theme-name}"
	 * <p>
	 * Use {@code UIManager.getLookAndFeel().getID().startsWith( "FlatLaf" )}
	 * to check whether the current look and feel is FlatLaf.
	 */
	@Override
	public String getID() {
		return "FlatLaf - " + getName();
	}

	public abstract boolean isDark();

	/**
	 * Checks whether the current look and feel is dark.
	 */
	public static boolean isLafDark() {
		LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
		return lookAndFeel instanceof FlatLaf && ((FlatLaf)lookAndFeel).isDark();
	}

	/**
	 * Returns whether FlatLaf supports custom window decorations.
	 * This depends on the operating system and on the used Java runtime.
	 * <p>
	 * This method returns {@code true} on Windows 10 (see exception below), {@code false} otherwise.
	 * <p>
	 * Returns also {@code false} on Windows 10 if:
	 * <ul>
	 * <li>FlatLaf native window border support is available (requires Windows 10)</li>
	 * <li>running in
	 * <a href="https://confluence.jetbrains.com/display/JBR/JetBrains+Runtime">JetBrains Runtime 11 (or later)</a>
	 * (<a href="https://github.com/JetBrains/JetBrainsRuntime">source code on github</a>)
	 * and JBR supports custom window decorations
	 * </li>
	 * </ul>
	 * In this cases, custom decorations are enabled by the root pane.
	 * Usage of {@link JFrame#setDefaultLookAndFeelDecorated(boolean)} or
	 * {@link JDialog#setDefaultLookAndFeelDecorated(boolean)} is not necessary.
	 */
	@Override
	public boolean getSupportsWindowDecorations() {
		if( SystemInfo.isProjector || SystemInfo.isWebswing || SystemInfo.isWinPE )
			return false;

		if( SystemInfo.isWindows_10_orLater &&
			FlatNativeWindowBorder.isSupported() )
		  return false;

		return SystemInfo.isWindows_10_orLater;
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}

	@Override
	public Icon getDisabledIcon( JComponent component, Icon icon ) {
		if( icon instanceof DisabledIconProvider ) {
			Icon disabledIcon = ((DisabledIconProvider)icon).getDisabledIcon();
			return !(disabledIcon instanceof UIResource) ? new IconUIResource( disabledIcon ) : disabledIcon;
		}

		if( icon instanceof ImageIcon ) {
			Object grayFilter = UIManager.get( "Component.grayFilter" );
			ImageFilter filter = (grayFilter instanceof ImageFilter)
				? (ImageFilter) grayFilter
				: GrayFilter.createDisabledIconFilter( isDark() ); // fallback

			Function<Image, Image> mapper = img -> {
				ImageProducer producer = new FilteredImageSource( img.getSource(), filter );
				return Toolkit.getDefaultToolkit().createImage( producer );
			};

			Image image = ((ImageIcon)icon).getImage();
			return new ImageIconUIResource( MultiResolutionImageSupport.map( image, mapper ) );
		}

		return null;
	}

	@Override
	public void initialize() {
		if( SystemInfo.isMacOS )
			initializeAqua();

		super.initialize();

		// install popup factory
		oldPopupFactory = PopupFactory.getSharedInstance();
		PopupFactory.setSharedInstance( new FlatPopupFactory() );

		// install mnemonic handler
		mnemonicHandler = new MnemonicHandler();
		mnemonicHandler.install();

		// listen to desktop property changes to update UI if system font or scaling changes
		if( SystemInfo.isWindows ) {
			// Windows 10 allows increasing font size independent of scaling:
			//   Settings > Ease of Access > Display > Make text bigger (100% - 225%)
			desktopPropertyName = "win.messagebox.font";
		} else if( SystemInfo.isLinux ) {
			// Linux/Gnome allows changing font in "Tweaks" app
			desktopPropertyName = "gnome.Gtk/FontName";

			// Linux/Gnome allows extra scaling and larger text:
			//   Settings > Devices > Displays > Scale (100% or 200%)
			//   Settings > Universal access > Large Text (off or on, 125%)
			//   "Tweaks" app > Fonts > Scaling Factor (0,5 - 3)
			desktopPropertyName2 = "gnome.Xft/DPI";
		}
		if( desktopPropertyName != null ) {
			desktopPropertyListener = e -> {
				String propertyName = e.getPropertyName();
				if( desktopPropertyName.equals( propertyName ) || propertyName.equals( desktopPropertyName2 ) )
					reSetLookAndFeel();
				else if( DESKTOPFONTHINTS.equals( propertyName ) ) {
					if( UIManager.getLookAndFeel() instanceof FlatLaf ) {
						putAATextInfo( UIManager.getLookAndFeelDefaults() );
						updateUILater();
					}
				}
			};
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			toolkit.addPropertyChangeListener( desktopPropertyName, desktopPropertyListener );
			if( desktopPropertyName2 != null )
				toolkit.addPropertyChangeListener( desktopPropertyName2, desktopPropertyListener );
			toolkit.addPropertyChangeListener( DESKTOPFONTHINTS, desktopPropertyListener );
		}

		// Following code should be ideally in initialize(), but needs color from UI defaults.
		// Do not move this code to getDefaults() to avoid side effects in the case that
		// getDefaults() is directly invoked from 3rd party code. E.g. `new FlatLightLaf().getDefaults()`.
		postInitialization = defaults -> {
			// update link color in HTML text
			Color linkColor = defaults.getColor( "Component.linkColor" );
			if( linkColor != null ) {
				new HTMLEditorKit().getStyleSheet().addRule(
					String.format( "a, address { color: #%06x; }", linkColor.getRGB() & 0xffffff ) );
			}
		};
	}

	@Override
	public void uninitialize() {
		// remove desktop property listener
		if( desktopPropertyListener != null ) {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			toolkit.removePropertyChangeListener( desktopPropertyName, desktopPropertyListener );
			if( desktopPropertyName2 != null )
				toolkit.removePropertyChangeListener( desktopPropertyName2, desktopPropertyListener );
			toolkit.removePropertyChangeListener( DESKTOPFONTHINTS, desktopPropertyListener );
			desktopPropertyName = null;
			desktopPropertyName2 = null;
			desktopPropertyListener = null;
		}

		// uninstall popup factory
		if( oldPopupFactory != null ) {
			PopupFactory.setSharedInstance( oldPopupFactory );
			oldPopupFactory = null;
		}

		// uninstall mnemonic handler
		if( mnemonicHandler != null ) {
			mnemonicHandler.uninstall();
			mnemonicHandler = null;
		}

		// restore default link color
		new HTMLEditorKit().getStyleSheet().addRule( "a, address { color: blue; }" );
		postInitialization = null;

		super.uninitialize();
	}

	/**
	 * Initialize Aqua LaF on macOS, which is required for using Mac screen menubar.
	 * (at least on Java 8, since 9 it seems to work without it)
	 * <p>
	 * This loads the native library "osxui" and initializes JRSUI.
	 * Because both are not unloaded/uninitialized, Aqua LaF is initialized only once.
	 */
	private void initializeAqua() {
		if( aquaLoaded )
			return;

		aquaLoaded = true;

		// create macOS Aqua LaF
		String aquaLafClassName = "com.apple.laf.AquaLookAndFeel";
		BasicLookAndFeel aquaLaf;
		try {
			if( SystemInfo.isJava_9_orLater ) {
				Method m = UIManager.class.getMethod( "createLookAndFeel", String.class );
				aquaLaf = (BasicLookAndFeel) m.invoke( null, "Mac OS X" );
			} else
				aquaLaf = (BasicLookAndFeel) Class.forName( aquaLafClassName ).getDeclaredConstructor().newInstance();
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to initialize Aqua look and feel '" + aquaLafClassName + "'.", ex );
			throw new IllegalStateException();
		}

		// remember popup factory because aquaLaf.initialize() installs its own
		// factory, which makes sub-menu rendering "jittery"
		PopupFactory oldPopupFactory = PopupFactory.getSharedInstance();

		// initialize Aqua LaF
		aquaLaf.initialize();
		aquaLaf.uninitialize();

		// restore popup factory
		PopupFactory.setSharedInstance( oldPopupFactory );
	}

	@Override
	public UIDefaults getDefaults() {
		UIDefaults defaults = super.getDefaults();

		// add flag that indicates whether the LaF is light or dark
		// (can be queried without using FlatLaf API)
		defaults.put( "laf.dark", isDark() );

		// add resource bundle for localized texts
		defaults.addResourceBundle( "com.formdev.flatlaf.resources.Bundle" );

		// initialize some defaults (for overriding) that are used in UI delegates,
		// but are not set in BasicLookAndFeel
		putDefaults( defaults, defaults.getColor( "control" ),
			"Button.disabledBackground",
			"EditorPane.disabledBackground",
			"EditorPane.inactiveBackground",
			"FormattedTextField.disabledBackground",
			"PasswordField.disabledBackground",
			"Spinner.disabledBackground",
			"TextArea.disabledBackground",
			"TextArea.inactiveBackground",
			"TextField.disabledBackground",
			"TextPane.disabledBackground",
			"TextPane.inactiveBackground",
			"ToggleButton.disabledBackground" );
		putDefaults( defaults, defaults.getColor( "textInactiveText" ),
			"Button.disabledText",
			"CheckBox.disabledText",
			"CheckBoxMenuItem.disabledForeground",
			"Menu.disabledForeground",
			"MenuItem.disabledForeground",
			"RadioButton.disabledText",
			"RadioButtonMenuItem.disabledForeground",
			"Spinner.disabledForeground",
			"ToggleButton.disabledText" );
		putDefaults( defaults, defaults.getColor( "textText" ),
			"DesktopIcon.foreground" );

		initFonts( defaults );
		initIconColors( defaults, isDark() );
		FlatInputMaps.initInputMaps( defaults );

		// copy InternalFrame.icon (the Java cup) to TitlePane.icon
		// (using defaults.remove() to avoid that lazy value is resolved and icon loaded here)
		Object icon = defaults.remove( "InternalFrame.icon" );
		defaults.put( "InternalFrame.icon", icon );
		defaults.put( "TitlePane.icon", icon );

		// get addons and sort them by priority
		ServiceLoader<FlatDefaultsAddon> addonLoader = ServiceLoader.load( FlatDefaultsAddon.class );
		List<FlatDefaultsAddon> addons = new ArrayList<>();
		for( FlatDefaultsAddon addon : addonLoader )
			addons.add( addon );
		addons.sort( (addon1, addon2) -> addon1.getPriority() - addon2.getPriority() );

		// load defaults from properties
		List<Class<?>> lafClassesForDefaultsLoading = getLafClassesForDefaultsLoading();
		if( lafClassesForDefaultsLoading != null )
			UIDefaultsLoader.loadDefaultsFromProperties( lafClassesForDefaultsLoading, addons, getAdditionalDefaults(), isDark(), defaults );
		else
			UIDefaultsLoader.loadDefaultsFromProperties( getClass(), addons, getAdditionalDefaults(), isDark(), defaults );

		// use Aqua MenuBarUI if Mac screen menubar is enabled
		if( SystemInfo.isMacOS && Boolean.getBoolean( "apple.laf.useScreenMenuBar" ) ) {
			defaults.put( "MenuBarUI", "com.apple.laf.AquaMenuBarUI" );

			// add defaults necessary for AquaMenuBarUI
			defaults.put( "MenuBar.backgroundPainter", BorderFactory.createEmptyBorder() );
		}

		// initialize text antialiasing
		putAATextInfo( defaults );

		// apply additional defaults (e.g. from IntelliJ themes)
		applyAdditionalDefaults( defaults );

		// allow addons modifying UI defaults
		for( FlatDefaultsAddon addon : addons )
			addon.afterDefaultsLoading( this, defaults );

		// add user scale factor to allow layout managers (e.g. MigLayout) to use it
		defaults.put( "laf.scaleFactor", (ActiveValue) t -> {
			return UIScale.getUserScaleFactor();
		} );

		if( postInitialization != null ) {
			postInitialization.accept( defaults );
			postInitialization = null;
		}

		return defaults;
	}

	void applyAdditionalDefaults( UIDefaults defaults ) {
	}

	protected List<Class<?>> getLafClassesForDefaultsLoading() {
		return null;
	}

	protected Properties getAdditionalDefaults() {
		return null;
	}

	private void initFonts( UIDefaults defaults ) {
		FontUIResource uiFont = null;

		if( SystemInfo.isWindows ) {
			Font winFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty( "win.messagebox.font" );
			if( winFont != null ) {
				if( SystemInfo.isWinPE ) {
					// on WinPE use "win.defaultGUI.font", which is usually Tahoma,
					// because Segoe UI font is not available on WinPE
					Font winPEFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty( "win.defaultGUI.font" );
					if( winPEFont != null )
						uiFont = createCompositeFont( winPEFont.getFamily(), winPEFont.getStyle(), winFont.getSize() );
				} else
					uiFont = createCompositeFont( winFont.getFamily(), winFont.getStyle(), winFont.getSize() );
			}

		} else if( SystemInfo.isMacOS ) {
			String fontName;
			if( SystemInfo.isMacOS_10_15_Catalina_orLater ) {
				if (SystemInfo.isJetBrainsJVM_11_orLater) {
					// See https://youtrack.jetbrains.com/issue/JBR-1915
					fontName = ".AppleSystemUIFont";
				} else {
					// use Helvetica Neue font
					fontName = "Helvetica Neue";
				}
			} else if( SystemInfo.isMacOS_10_11_ElCapitan_orLater ) {
				// use San Francisco Text font
				fontName = ".SF NS Text";
			} else {
				// default font on older systems (see com.apple.laf.AquaFonts)
				fontName = "Lucida Grande";
			}

			uiFont = createCompositeFont( fontName, Font.PLAIN, 13 );

		} else if( SystemInfo.isLinux ) {
			Font font = LinuxFontPolicy.getFont();
			uiFont = (font instanceof FontUIResource) ? (FontUIResource) font : new FontUIResource( font );
		}

		// fallback
		if( uiFont == null )
			uiFont = createCompositeFont( Font.SANS_SERIF, Font.PLAIN, 12 );

		// increase font size if system property "flatlaf.uiScale" is set
		uiFont = UIScale.applyCustomScaleFactor( uiFont );

		// use active value for all fonts to allow changing fonts in all components
		// (similar as in Nimbus L&F) with:
		//     UIManager.put( "defaultFont", myFont );
		Object activeFont = new ActiveFont( 1 );

		// override fonts
		for( Object key : defaults.keySet() ) {
			if( key instanceof String && (((String)key).endsWith( ".font" ) || ((String)key).endsWith( "Font" )) )
				defaults.put( key, activeFont );
		}

		// use smaller font for progress bar
		defaults.put( "ProgressBar.font", new ActiveFont( 0.85f ) );

		// set default font
		defaults.put( "defaultFont", uiFont );
	}

	static FontUIResource createCompositeFont( String family, int style, int size ) {
		// using StyleContext.getFont() here because it uses
		// sun.font.FontUtilities.getCompositeFontUIResource()
		// and creates a composite font that is able to display all Unicode characters
		Font font = StyleContext.getDefaultStyleContext().getFont( family, style, size );
		return (font instanceof FontUIResource) ? (FontUIResource) font : new FontUIResource( font );
	}

	/**
	 * @since 1.1
	 */
	public static ActiveValue createActiveFontValue( float scaleFactor ) {
		return new ActiveFont( scaleFactor );
	}

	/**
	 * Adds the default color palette for action icons and object icons to the given UIDefaults.
	 * <p>
	 * This method is public and static to allow using the color palette with
	 * other LaFs (e.g. Windows LaF). To do so invoke:
	 *   {@code FlatLaf.initIconColors( UIManager.getLookAndFeelDefaults(), false );}
	 * after
	 *   {@code UIManager.setLookAndFeel( ... );}.
	 * <p>
	 * The colors are based on IntelliJ Platform
	 *   <a href="https://jetbrains.design/intellij/principles/icons/#action-icons">Action icons</a>
	 * and
	 *   <a href="https://jetbrains.design/intellij/principles/icons/#noun-icons">Noun icons</a>
	 * <p>
	 * These colors may be changed by IntelliJ Platform themes.
	 */
	public static void initIconColors( UIDefaults defaults, boolean dark ) {
		for( FlatIconColors c : FlatIconColors.values() ) {
			if( c.light == !dark || c.dark == dark )
				defaults.put( c.key, new ColorUIResource( c.rgb ) );
		}
	}

	private void putAATextInfo( UIDefaults defaults ) {
		if ( SystemInfo.isMacOS && SystemInfo.isJetBrainsJVM ) {
			// The awt.font.desktophints property suggests sub-pixel anti-aliasing
			// which renders text with too much weight on macOS in the JetBrains JRE.
			// Use greyscale anti-aliasing instead.
			defaults.put( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		} else if( SystemInfo.isJava_9_orLater ) {
			Object desktopHints = Toolkit.getDefaultToolkit().getDesktopProperty( DESKTOPFONTHINTS );
			if( desktopHints == null )
				desktopHints = fallbackAATextInfo();
			if( desktopHints instanceof Map ) {
				@SuppressWarnings( "unchecked" )
				Map<Object, Object> hints = (Map<Object, Object>) desktopHints;
				Object aaHint = hints.get( RenderingHints.KEY_TEXT_ANTIALIASING );
				if( aaHint != null &&
					aaHint != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF &&
					aaHint != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT )
				{
					defaults.put( RenderingHints.KEY_TEXT_ANTIALIASING, aaHint );
					defaults.put( RenderingHints.KEY_TEXT_LCD_CONTRAST,
						hints.get( RenderingHints.KEY_TEXT_LCD_CONTRAST ) );
				}
			}
		} else {
			// Java 8
			try {
				Object key = Class.forName( "sun.swing.SwingUtilities2" )
					.getField( "AA_TEXT_PROPERTY_KEY" )
					.get( null );
				Object value = Class.forName( "sun.swing.SwingUtilities2$AATextInfo" )
					.getMethod( "getAATextInfo", boolean.class )
					.invoke( null, true );
				if( value == null )
					value = fallbackAATextInfo();
				defaults.put( key, value );
			} catch( Exception ex ) {
				LoggingFacade.INSTANCE.logSevere( null, ex );
				throw new RuntimeException( ex );
			}
		}
	}

	private Object fallbackAATextInfo() {
		// do nothing if explicitly overridden
		if( System.getProperty( "awt.useSystemAAFontSettings" ) != null )
			return null;

		Object aaHint = null;
		Integer lcdContrastHint = null;

		if( SystemInfo.isLinux ) {
			// see sun.awt.UNIXToolkit.getDesktopAAHints()
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			if( toolkit.getDesktopProperty( "gnome.Xft/Antialias" ) == null &&
				toolkit.getDesktopProperty( "fontconfig/Antialias" ) == null )
			{
				// no Gnome or KDE Desktop properties available
				// --> enable antialiasing
				aaHint = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
			}
		}

		if( aaHint == null )
			return null;

		if( SystemInfo.isJava_9_orLater ) {
			Map<Object, Object> hints = new HashMap<>();
			hints.put( RenderingHints.KEY_TEXT_ANTIALIASING, aaHint );
			hints.put( RenderingHints.KEY_TEXT_LCD_CONTRAST, lcdContrastHint );
			return hints;
		} else {
			// Java 8
			try {
				return Class.forName( "sun.swing.SwingUtilities2$AATextInfo" )
					.getConstructor( Object.class, Integer.class )
					.newInstance( aaHint, lcdContrastHint );
			} catch( Exception ex ) {
				LoggingFacade.INSTANCE.logSevere( null, ex );
				throw new RuntimeException( ex );
			}
		}
	}

	private void putDefaults( UIDefaults defaults, Object value, String... keys ) {
		for( String key : keys )
			defaults.put( key, value );
	}

	static List<Object> getCustomDefaultsSources() {
		return customDefaultsSources;
	}

	/**
	 * Registers a package where FlatLaf searches for properties files with custom UI defaults.
	 * <p>
	 * This can be used to specify application specific UI defaults that override UI values
	 * of existing themes or to define own UI values used in custom controls.
	 * <p>
	 * There may be multiple properties files in that package for multiple themes.
	 * The properties file name must match the used theme class names.
	 * E.g. {@code FlatLightLaf.properties} for class {@link FlatLightLaf}
	 * or {@code FlatDarkLaf.properties} for class {@link FlatDarkLaf}.
	 * {@code FlatLaf.properties} is loaded first for all themes.
	 * <p>
	 * These properties files are loaded after theme and addon properties files
	 * and can therefore override all UI defaults.
	 * <p>
	 * Invoke this method before setting the look and feel.
	 *
	 * @param packageName a package name (e.g. "com.myapp.resources")
	 */
	public static void registerCustomDefaultsSource( String packageName ) {
		registerCustomDefaultsSource( packageName, null );
	}

	public static void unregisterCustomDefaultsSource( String packageName ) {
		unregisterCustomDefaultsSource( packageName, null );
	}

	/**
	 * Registers a package where FlatLaf searches for properties files with custom UI defaults.
	 * <p>
	 * See {@link #registerCustomDefaultsSource(String)} for details.
	 *
	 * @param packageName a package name (e.g. "com.myapp.resources")
	 * @param classLoader a class loader used to find resources, or {@code null}
	 */
	public static void registerCustomDefaultsSource( String packageName, ClassLoader classLoader ) {
		if( customDefaultsSources == null )
			customDefaultsSources = new ArrayList<>();
		customDefaultsSources.add( packageName );
		customDefaultsSources.add( classLoader );
	}

	public static void unregisterCustomDefaultsSource( String packageName, ClassLoader classLoader ) {
		if( customDefaultsSources == null )
			return;

		int size = customDefaultsSources.size();
		for( int i = 0; i < size - 1; i++ ) {
			Object source = customDefaultsSources.get( i );
			if( packageName.equals( source ) && customDefaultsSources.get( i + 1 ) == classLoader ) {
				customDefaultsSources.remove( i + 1 );
				customDefaultsSources.remove( i );
				break;
			}
		}
	}

	/**
	 * Registers a folder where FlatLaf searches for properties files with custom UI defaults.
	 * <p>
	 * See {@link #registerCustomDefaultsSource(String)} for details.
	 *
	 * @param folder a folder
	 */
	public static void registerCustomDefaultsSource( File folder ) {
		if( customDefaultsSources == null )
			customDefaultsSources = new ArrayList<>();
		customDefaultsSources.add( folder );
	}

	public static void unregisterCustomDefaultsSource( File folder ) {
		if( customDefaultsSources == null )
			return;

		customDefaultsSources.remove( folder );
	}

	private static void reSetLookAndFeel() {
		EventQueue.invokeLater( () -> {
			LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
			try {
				// re-set current LaF
				UIManager.setLookAndFeel( lookAndFeel );

				// must fire property change events ourself because old and new LaF are the same
				PropertyChangeEvent e = new PropertyChangeEvent( UIManager.class, "lookAndFeel", lookAndFeel, lookAndFeel );
				for( PropertyChangeListener l : UIManager.getPropertyChangeListeners() )
					l.propertyChange( e );

				// update UI
				updateUI();
			} catch( UnsupportedLookAndFeelException ex ) {
				LoggingFacade.INSTANCE.logSevere(  "FlatLaf: Failed to reinitialize look and feel '" + lookAndFeel.getClass().getName() + "'.", ex );
			}
		} );
	}

	/**
	 * Update UI of all application windows immediately.
	 * Invoke after changing LaF.
	 */
	public static void updateUI() {
		for( Window w : Window.getWindows() )
			SwingUtilities.updateComponentTreeUI( w );
	}

	/**
	 * Update UI of all application windows later.
	 */
	public static void updateUILater() {
		synchronized( FlatLaf.class ) {
			if( updateUIPending )
				return;

			updateUIPending = true;
		}

		EventQueue.invokeLater( () -> {
			updateUI();
			synchronized( FlatLaf.class ) {
				updateUIPending = false;
			}
		} );
	}

	/**
	 * Returns whether native window decorations are supported on current platform.
	 * <p>
	 * This requires Windows 10, but may be disabled if running in special environments
	 * (JetBrains Projector, Webswing or WinPE) or if loading native library fails.
	 * If system property {@link FlatSystemProperties#USE_WINDOW_DECORATIONS} is set to
	 * {@code false}, then this method also returns {@code false}.
	 *
	 * @since 1.1.2
	 */
	public static boolean supportsNativeWindowDecorations() {
		return SystemInfo.isWindows_10_orLater && FlatNativeWindowBorder.isSupported();
	}

	/**
	 * Returns whether native window decorations are enabled.
	 *
	 * @since 1.1.2
	 */
	public static boolean isUseNativeWindowDecorations() {
		return UIManager.getBoolean( "TitlePane.useWindowDecorations" );
	}

	/**
	 * Sets whether native window decorations are enabled.
	 * <p>
	 * Existing frames and dialogs will be updated.
	 *
	 * @since 1.1.2
	 */
	public static void setUseNativeWindowDecorations( boolean enabled ) {
		UIManager.put( "TitlePane.useWindowDecorations", enabled );

		if( !(UIManager.getLookAndFeel() instanceof FlatLaf) )
			return;

		// update existing frames and dialogs
		for( Window w : Window.getWindows() ) {
			if( isDisplayableFrameOrDialog( w ) )
				FlatRootPaneUI.updateNativeWindowBorder( ((RootPaneContainer)w).getRootPane() );
		}
	}

	/**
	 * Revalidate and repaint all displayable frames and dialogs.
	 *
	 * @since 1.1.2
	 */
	public static void revalidateAndRepaintAllFramesAndDialogs() {
		for( Window w : Window.getWindows() ) {
			if( isDisplayableFrameOrDialog( w ) ) {
				w.revalidate();
				w.repaint();
			}
		}
	}

	/**
	 * Repaint all displayable frames and dialogs.
	 *
	 * @since 1.1.2
	 */
	public static void repaintAllFramesAndDialogs() {
		for( Window w : Window.getWindows() ) {
			if( isDisplayableFrameOrDialog( w ) )
				w.repaint();
		}
	}

	private static boolean isDisplayableFrameOrDialog( Window w ) {
		return w.isDisplayable() && (w instanceof JFrame || w instanceof JDialog);
	}

	public static boolean isShowMnemonics() {
		return MnemonicHandler.isShowMnemonics();
	}

	public static void showMnemonics( Component c ) {
		MnemonicHandler.showMnemonics( true, c );
	}

	public static void hideMnemonics() {
		MnemonicHandler.showMnemonics( false, null );
	}

	// do not allow overriding to avoid issues in FlatUIUtils.createSharedUI()
	@Override
	public final boolean equals( Object obj ) {
		return super.equals( obj );
	}

	// do not allow overriding to avoid issues in FlatUIUtils.createSharedUI()
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	//---- class ActiveFont ---------------------------------------------------

	private static class ActiveFont
		implements ActiveValue
	{
		private final float scaleFactor;

		// cache (scaled) font
		private Font font;
		private Font lastDefaultFont;

		ActiveFont( float scaleFactor ) {
			this.scaleFactor = scaleFactor;
		}

		@Override
		public Object createValue( UIDefaults table ) {
			Font defaultFont = UIManager.getFont( "defaultFont" );

			// fallback (to avoid NPE in case that this is used in another Laf)
			if( defaultFont == null )
				defaultFont = UIManager.getFont( "Label.font" );

			if( lastDefaultFont != defaultFont ) {
				lastDefaultFont = defaultFont;

				if( scaleFactor != 1 ) {
					// scale font
					int newFontSize = Math.round( defaultFont.getSize() * scaleFactor );
					font = new FontUIResource( defaultFont.deriveFont( (float) newFontSize ) );
				} else {
					// make sure that font is a UIResource for LaF switching
					font = (defaultFont instanceof UIResource)
						? defaultFont
						: new FontUIResource( defaultFont );
				}
			}

			return font;
		}
	}

	//---- class ImageIconUIResource ------------------------------------------

	private static class ImageIconUIResource
		extends ImageIcon
		implements UIResource
	{
		ImageIconUIResource( Image image ) {
			super( image );
		}
	}

	//---- interface DisabledIconProvider -------------------------------------

	/**
	 * A provider for disabled icons.
	 * <p>
	 * This is intended to be implemented by {@link javax.swing.Icon} implementations
	 * that provide the ability to paint disabled state.
	 * <p>
	 * Used in {@link FlatLaf#getDisabledIcon(JComponent, Icon)} to create a disabled icon from an enabled icon.
	 */
	public interface DisabledIconProvider
	{
		/**
		 * Returns an icon with a disabled appearance.
		 *
		 * @return a disabled icon
		 */
		Icon getDisabledIcon();
	}
}
