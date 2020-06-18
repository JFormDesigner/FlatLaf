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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIDefaults.ActiveValue;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.text.StyleContext;
import javax.swing.text.html.HTMLEditorKit;
import com.formdev.flatlaf.ui.FlatPopupFactory;
import com.formdev.flatlaf.util.GrayFilter;
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
	static final Logger LOG = Logger.getLogger( FlatLaf.class.getName() );
	private static final String DESKTOPFONTHINTS = "awt.font.desktophints";

	private String desktopPropertyName;
	private String desktopPropertyName2;
	private PropertyChangeListener desktopPropertyListener;

	private static boolean aquaLoaded;
	private static boolean updateUIPending;

	private PopupFactory oldPopupFactory;
	private MnemonicHandler mnemonicHandler;

	private Consumer<UIDefaults> postInitialization;

	public static boolean install( LookAndFeel newLookAndFeel ) {
		try {
			UIManager.setLookAndFeel( newLookAndFeel );
			return true;
		} catch( Exception ex ) {
			LOG.log( Level.SEVERE, "FlatLaf: Failed to initialize look and feel '" + newLookAndFeel.getClass().getName() + "'.", ex );
			return false;
		}
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
		if( SystemInfo.IS_MAC )
			initializeAqua();

		super.initialize();

		// install popup factory
		oldPopupFactory = PopupFactory.getSharedInstance();
		PopupFactory.setSharedInstance( new FlatPopupFactory() );

		// install mnemonic handler
		mnemonicHandler = new MnemonicHandler();
		mnemonicHandler.install();

		// listen to desktop property changes to update UI if system font or scaling changes
		if( SystemInfo.IS_WINDOWS ) {
			// Windows 10 allows increasing font size independent of scaling:
			//   Settings > Ease of Access > Display > Make text bigger (100% - 225%)
			desktopPropertyName = "win.messagebox.font";
		} else if( SystemInfo.IS_LINUX ) {
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
					String.format( "a { color: #%06x; }", linkColor.getRGB() & 0xffffff ) );
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
		new HTMLEditorKit().getStyleSheet().addRule( "a { color: blue; }" );
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
			if( SystemInfo.IS_JAVA_9_OR_LATER ) {
				Method m = UIManager.class.getMethod( "createLookAndFeel", String.class );
				aquaLaf = (BasicLookAndFeel) m.invoke( null, "Mac OS X" );
			} else
				aquaLaf = (BasicLookAndFeel) Class.forName( aquaLafClassName ).newInstance();
		} catch( Exception ex ) {
			LOG.log( Level.SEVERE, "FlatLaf: Failed to initialize Aqua look and feel '" + aquaLafClassName + "'.", ex );
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
		if( SystemInfo.IS_MAC && Boolean.getBoolean( "apple.laf.useScreenMenuBar" ) ) {
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

		if( SystemInfo.IS_WINDOWS ) {
			Font winFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty( "win.messagebox.font" );
			if( winFont != null )
				uiFont = createCompositeFont( winFont.getFamily(), winFont.getStyle(), winFont.getSize() );

		} else if( SystemInfo.IS_MAC ) {
			String fontName;
			if( SystemInfo.IS_MAC_OS_10_15_CATALINA_OR_LATER ) {
				// use Helvetica Neue font
				fontName = "Helvetica Neue";
			} else if( SystemInfo.IS_MAC_OS_10_11_EL_CAPITAN_OR_LATER ) {
				// use San Francisco Text font
				fontName = ".SF NS Text";
			} else {
				// default font on older systems (see com.apple.laf.AquaFonts)
				fontName = "Lucida Grande";
			}

			uiFont = createCompositeFont( fontName, Font.PLAIN, 13 );

		} else if( SystemInfo.IS_LINUX ) {
			Font font = LinuxFontPolicy.getFont();
			uiFont = (font instanceof FontUIResource) ? (FontUIResource) font : new FontUIResource( font );
		}

		if( uiFont == null )
			uiFont = createCompositeFont( Font.SANS_SERIF, Font.PLAIN, 12 );

		uiFont = UIScale.applyCustomScaleFactor( uiFont );

		// use active value for all fonts to allow changing fonts in all components
		// (similar as in Nimbus L&F) with:
		//     UIManager.put( "defaultFont", myFont );
		Object activeFont =  new ActiveFont( 1 );

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
		if( SystemInfo.IS_JAVA_9_OR_LATER ) {
			Object desktopHints = Toolkit.getDefaultToolkit().getDesktopProperty( DESKTOPFONTHINTS );
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
				defaults.put( key, value );
			} catch( Exception ex ) {
				Logger.getLogger( FlatLaf.class.getName() ).log( Level.SEVERE, null, ex );
				throw new RuntimeException( ex );
			}
		}
	}

	private void putDefaults( UIDefaults defaults, Object value, String... keys ) {
		for( String key : keys )
			defaults.put( key, value );
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
				LOG.log( Level.SEVERE, "FlatLaf: Failed to reinitialize look and feel '" + lookAndFeel.getClass().getName() + "'.", ex );
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

	public static boolean isShowMnemonics() {
		return MnemonicHandler.isShowMnemonics();
	}

	public static void showMnemonics( Component c ) {
		MnemonicHandler.showMnemonics( true, c );
	}

	public static void hideMnemonics() {
		MnemonicHandler.showMnemonics( false, null );
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
}
