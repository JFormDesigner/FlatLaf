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
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.html.HTMLEditorKit;
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

		// make sure that a plain popup factory is used (otherwise sub-menu rendering
		// is "jittery" on Mac, where AquaLookAndFeel installs its own popup factory)
		if( PopupFactory.getSharedInstance().getClass() != PopupFactory.class )
			PopupFactory.setSharedInstance( new PopupFactory() );

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

		// restore default link color
		new HTMLEditorKit().getStyleSheet().addRule( "a { color: blue; }" );

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

		// add Metal resource bundle, which is required for FlatFileChooserUI
		defaults.addResourceBundle( "com.sun.swing.internal.plaf.metal.resources.metal" );

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
		initIconColors( defaults, isDark() );

		// load defaults from properties
		List<Class<?>> lafClassesForDefaultsLoading = getLafClassesForDefaultsLoading();
		if( lafClassesForDefaultsLoading != null )
			UIDefaultsLoader.loadDefaultsFromProperties( lafClassesForDefaultsLoading, defaults );
		else
			UIDefaultsLoader.loadDefaultsFromProperties( getClass(), defaults );

		// use Aqua MenuBarUI if Mac screen menubar is enabled
		if( useScreenMenuBar )
			defaults.put( "MenuBarUI", aquaMenuBarUI );

		// update link color in HTML text
		Color linkColor = defaults.getColor( "Component.linkColor" );
		if( linkColor != null ) {
			new HTMLEditorKit().getStyleSheet().addRule(
				String.format( "a { color: #%06x; }", linkColor.getRGB() & 0xffffff ) );
		}

		return defaults;
	}

	List<Class<?>> getLafClassesForDefaultsLoading() {
		return null;
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
	 */
	public static void initIconColors( UIDefaults defaults, boolean dark ) {
		// colors for action icons
		// see https://jetbrains.design/intellij/principles/icons/#action-icons
		defaults.put( "Actions.Red",            new ColorUIResource( !dark ? 0xDB5860 : 0xC75450 ) );
		defaults.put( "Actions.Yellow",         new ColorUIResource( !dark ? 0xEDA200 : 0xF0A732 ) );
		defaults.put( "Actions.Green",          new ColorUIResource( !dark ? 0x59A869 : 0x499C54 ) );
		defaults.put( "Actions.Blue",           new ColorUIResource( !dark ? 0x389FD6 : 0x3592C4 ) );
		defaults.put( "Actions.Grey",           new ColorUIResource( !dark ? 0x6E6E6E : 0xAFB1B3 ) );
		defaults.put( "Actions.GreyInline",     new ColorUIResource( !dark ? 0x7F8B91 : 0x7F8B91 ) );

		// colors for object icons
		// see https://jetbrains.design/intellij/principles/icons/#noun-icons
		defaults.put( "Objects.Grey",           new ColorUIResource( 0x9AA7B0 ) );
		defaults.put( "Objects.Blue",           new ColorUIResource( 0x40B6E0 ) );
		defaults.put( "Objects.Green",          new ColorUIResource( 0x62B543 ) );
		defaults.put( "Objects.Yellow",         new ColorUIResource( 0xF4AF3D ) );
		defaults.put( "Objects.YellowDark",     new ColorUIResource( 0xD9A343 ) );
		defaults.put( "Objects.Purple",         new ColorUIResource( 0xB99BF8 ) );
		defaults.put( "Objects.Pink",           new ColorUIResource( 0xF98B9E ) );
		defaults.put( "Objects.Red",            new ColorUIResource( 0xF26522 ) );
		defaults.put( "Objects.RedStatus",      new ColorUIResource( 0xE05555 ) );
		defaults.put( "Objects.GreenAndroid",   new ColorUIResource( 0xA4C639 ) );
		defaults.put( "Objects.BlackText",      new ColorUIResource( 0x231F20 ) );
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
