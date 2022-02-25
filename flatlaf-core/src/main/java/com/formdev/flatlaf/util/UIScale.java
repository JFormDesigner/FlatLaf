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

package com.formdev.flatlaf.util;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import com.formdev.flatlaf.FlatSystemProperties;

/**
 * This class handles scaling in Swing UIs.
 * It computes user scaling factor based on font size and
 * provides methods to scale integer, float, {@link Dimension} and {@link Insets}.
 * This class is look and feel independent.
 * <p>
 * Two scaling modes are supported by FlatLaf for HiDPI displays:
 *
 * <h2>1) system scaling mode</h2>
 *
 * This mode is supported since Java 9 on all platforms and in some Java 8 VMs
 * (e.g. Apple and JetBrains). The JRE determines the scale factor per-display and
 * adds a scaling transformation to the graphics object.
 * E.g. invokes {@code java.awt.Graphics2D.scale( 1.5, 1.5 )} for 150%.
 * So the JRE does the scaling itself.
 * E.g. when you draw a 10px line, a 15px line is drawn on screen.
 * The scale factor may be different for each connected display.
 * The scale factor may change for a window when moving the window from one display to another one.
 *
 * <h2>2) user scaling mode</h2>
 *
 * This mode is mainly for Java 8 compatibility, but is also used on Linux
 * or if the default font is changed.
 * The user scale factor is computed based on the used font.
 * The JRE does not scale anything.
 * So we have to invoke {@link #scale(float)} where necessary.
 * There is only one user scale factor for all displays.
 * The user scale factor may change if the active LaF, "defaultFont" or "Label.font" has changed.
 * If system scaling mode is available the user scale factor is usually 1,
 * but may be larger on Linux or if the default font is changed.
 *
 * @author Karl Tauber
 */
public class UIScale
{
	private static final boolean DEBUG = false;

	private static PropertyChangeSupport changeSupport;

	public static void addPropertyChangeListener( PropertyChangeListener listener ) {
		if( changeSupport == null )
			changeSupport = new PropertyChangeSupport( UIScale.class );
		changeSupport.addPropertyChangeListener( listener );
	}

	public static void removePropertyChangeListener( PropertyChangeListener listener ) {
		if( changeSupport == null )
			return;
		changeSupport.removePropertyChangeListener( listener );
	}

	//---- system scaling (Java 9) --------------------------------------------

	private static Boolean jreHiDPI;

	/**
	 * Returns whether system scaling is enabled.
	 */
	public static boolean isSystemScalingEnabled() {
		if( jreHiDPI != null )
			return jreHiDPI;

		jreHiDPI = false;

		if( SystemInfo.isJava_9_orLater ) {
			// Java 9 and later supports per-monitor scaling
			jreHiDPI = true;
		} else if( SystemInfo.isJetBrainsJVM ) {
			// IntelliJ IDEA ships its own JetBrains Java 8 JRE that may support per-monitor scaling
			// see com.intellij.ui.JreHiDpiUtil.isJreHiDPIEnabled()
			try {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				Class<?> sunGeClass = Class.forName( "sun.java2d.SunGraphicsEnvironment" );
				if( sunGeClass.isInstance( ge ) ) {
					Method m = sunGeClass.getDeclaredMethod( "isUIScaleOn" );
					jreHiDPI = (Boolean) m.invoke( ge );
				}
			} catch( Throwable ex ) {
				// ignore
			}
		}

		return jreHiDPI;
	}

	/**
	 * Returns the system scale factor for the given graphics context.
	 */
	public static double getSystemScaleFactor( Graphics2D g ) {
		return isSystemScalingEnabled() ? getSystemScaleFactor( g.getDeviceConfiguration() ) : 1;
	}

	/**
	 * Returns the system scale factor for the given graphics configuration.
	 */
	public static double getSystemScaleFactor( GraphicsConfiguration gc ) {
		return (isSystemScalingEnabled() && gc != null) ? gc.getDefaultTransform().getScaleX() : 1;
	}

	//---- user scaling (Java 8) ----------------------------------------------

	private static float scaleFactor = 1;
	private static boolean initialized;

	private static void initialize() {
		if( initialized )
			return;
		initialized = true;

		if( !isUserScalingEnabled() )
			return;

		// listener to update scale factor if LaF changed, "defaultFont" or "Label.font" changed
		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {
				switch( e.getPropertyName() ) {
					case "lookAndFeel":
						// it is not necessary (and possible) to remove listener of old LaF defaults
						if( e.getNewValue() instanceof LookAndFeel )
							UIManager.getLookAndFeelDefaults().addPropertyChangeListener( this );
						updateScaleFactor();
						break;

					case "defaultFont":
					case "Label.font":
						updateScaleFactor();
						break;
				}
			}
		};
		UIManager.addPropertyChangeListener( listener );
		UIManager.getDefaults().addPropertyChangeListener( listener );
		UIManager.getLookAndFeelDefaults().addPropertyChangeListener( listener );

		updateScaleFactor();
	}

	private static void updateScaleFactor() {
		if( !isUserScalingEnabled() )
			return;

		// apply custom scale factor specified in system property "flatlaf.uiScale"
		float customScaleFactor = getCustomScaleFactor();
		if( customScaleFactor > 0 ) {
			setUserScaleFactor( customScaleFactor, false );
			return;
		}

		// use font size to calculate scale factor (instead of DPI)
		// because even if we are on a HiDPI display it is not sure
		// that a larger font size is set by the current LaF
		// (e.g. can avoid large icons with small text)
		Font font = UIManager.getFont( "defaultFont" );
		if( font == null )
			font = UIManager.getFont( "Label.font" );

		setUserScaleFactor( computeFontScaleFactor( font ), true );
	}

	/**
	 * For internal use only.
	 *
	 * @since 2
	 */
	public static float computeFontScaleFactor( Font font ) {
		if( SystemInfo.isWindows ) {
			// Special handling for Windows to be compatible with OS scaling,
			// which distinguish between "screen scaling" and "text scaling".
			//  - Windows "screen scaling" scales everything (text, icon, gaps, etc)
			//    and may have different scaling factors for each screen.
			//  - Windows "text scaling" increases only the font size, but on all screens.
			//
			// Both can be changed by the user in the Windows 10 Settings:
			//  - Settings > Display > Scale and layout
			//  - Settings > Ease of Access > Display > Make text bigger (100% - 225%)
			if( font instanceof UIResource ) {
				Font uiFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty( "win.messagebox.font" );
				if( uiFont == null || uiFont.getSize() == font.getSize() ) {
					if( isSystemScalingEnabled() ) {
						// Do not apply own scaling if the JRE scales using Windows screen scale factor.
						// If user increases font size in Windows 10 settings, desktop property
						// "win.messagebox.font" is changed and FlatLaf uses the larger font.
						return 1;
					} else {
						// If the JRE does not scale (Java 8), the size of the UI font
						// (usually from desktop property "win.messagebox.font")
						// combines the Windows screen and text scale factors.
						// But the font in desktop property "win.defaultGUI.font" is only
						// scaled with the Windows screen scale factor. So use it to compute
						// our scale factor that is equal to Windows screen scale factor.
						Font winFont = (Font) Toolkit.getDefaultToolkit().getDesktopProperty( "win.defaultGUI.font" );
						return computeScaleFactor( (winFont != null) ? winFont : font );
					}
				}
			}

			// If font was explicitly set from outside (is not a UIResource),
			// or was set in FlatLaf properties files (is a UIResource),
			// use it to compute scale factor. This allows applications to
			// use custom fonts (e.g. that the user can change in UI) and
			// get scaling if a larger font size is used.
			// E.g. FlatLaf Demo supports increasing font size in "Font" menu and UI scales.
		}

		return computeScaleFactor( font );
	}

	private static float computeScaleFactor( Font font ) {
		// default font size
		float fontSizeDivider = 12f;

		if( SystemInfo.isWindows ) {
			// Windows LaF uses Tahoma font rather than the actual Windows system font (Segoe UI),
			// and its size is always ca. 10% smaller than the actual system font size.
			// Tahoma 11 is used at 100%
			if( "Tahoma".equals( font.getFamily() ) )
				fontSizeDivider = 11f;
		} else if( SystemInfo.isMacOS ) {
			// default font size on macOS is 13
			fontSizeDivider = 13f;
		} else if( SystemInfo.isLinux ) {
			// default font size for Unity and Gnome is 15 and for KDE it is 13
			fontSizeDivider = SystemInfo.isKDE ? 13f : 15f;
		}

		return font.getSize() / fontSizeDivider;
	}

	private static boolean isUserScalingEnabled() {
		return FlatSystemProperties.getBoolean( FlatSystemProperties.UI_SCALE_ENABLED, true );
	}

	/**
	 * Applies a custom scale factor given in system property "flatlaf.uiScale"
	 * to the given font.
	 */
	public static FontUIResource applyCustomScaleFactor( FontUIResource font ) {
		if( !isUserScalingEnabled() )
			return font;

		float scaleFactor = getCustomScaleFactor();
		if( scaleFactor <= 0 )
			return font;

		float fontScaleFactor = computeScaleFactor( font );
		if( scaleFactor == fontScaleFactor )
			return font;

		int newFontSize = Math.max( Math.round( (font.getSize() / fontScaleFactor) * scaleFactor ), 1 );
		return new FontUIResource( font.deriveFont( (float) newFontSize ) );
	}

	/**
	 * Get custom scale factor specified in system property "flatlaf.uiScale".
	 */
	private static float getCustomScaleFactor() {
		return parseScaleFactor( System.getProperty( FlatSystemProperties.UI_SCALE ) );
	}

	/**
	 * Similar to sun.java2d.SunGraphicsEnvironment.getScaleFactor(String)
	 */
	private static float parseScaleFactor( String s ) {
		if( s == null )
			return -1;

		float units = 1;
		if( s.endsWith( "x" ) )
			s = s.substring( 0, s.length() - 1 );
		else if( s.endsWith( "dpi" ) ) {
			units = 96;
			s = s.substring( 0, s.length() - 3 );
		} else if( s.endsWith( "%" ) ) {
			units = 100;
			s = s.substring( 0, s.length() - 1 );
		}

		try {
			float scale = Float.parseFloat( s );
			return scale > 0 ? scale / units : -1;
		} catch( NumberFormatException ex ) {
			return -1;
		}
	}

	/**
	 * Returns the user scale factor.
	 */
	public static float getUserScaleFactor() {
		initialize();
		return scaleFactor;
	}

	/**
	 * Sets the user scale factor.
	 */
	private static void setUserScaleFactor( float scaleFactor, boolean normalize ) {
		if( normalize ) {
			if( scaleFactor < 1f ) {
				scaleFactor = FlatSystemProperties.getBoolean( FlatSystemProperties.UI_SCALE_ALLOW_SCALE_DOWN, false )
					? Math.round( scaleFactor * 10f ) / 10f // round small scale factor to 1/10
					: 1f;
			} else if( scaleFactor > 1f ) // round scale factor to 1/4
				scaleFactor = Math.round( scaleFactor * 4f ) / 4f;
		}

		// minimum scale factor
		scaleFactor = Math.max( scaleFactor, 0.1f );

		float oldScaleFactor = UIScale.scaleFactor;
		UIScale.scaleFactor = scaleFactor;

		if( DEBUG )
			System.out.println( "HiDPI scale factor " + scaleFactor );

		if( changeSupport != null )
			changeSupport.firePropertyChange( "userScaleFactor", oldScaleFactor, scaleFactor );
	}

	/**
	 * Multiplies the given value by the user scale factor.
	 */
	public static float scale( float value ) {
		initialize();
		return (scaleFactor == 1) ? value : (value * scaleFactor);
	}

	/**
	 * Multiplies the given value by the user scale factor and rounds the result.
	 */
	public static int scale( int value ) {
		initialize();
		return (scaleFactor == 1) ? value : Math.round( value * scaleFactor );
	}

	/**
	 * Similar as {@link #scale(int)} but always "rounds down".
	 * <p>
	 * For use in special cases. {@link #scale(int)} is the preferred method.
	 */
	public static int scale2( int value ) {
		initialize();
		return (scaleFactor == 1) ? value : (int) (value * scaleFactor);
	}

	/**
	 * Divides the given value by the user scale factor.
	 */
	public static float unscale( float value ) {
		initialize();
		return (scaleFactor == 1f) ? value : (value / scaleFactor);
	}

	/**
	 * Divides the given value by the user scale factor and rounds the result.
	 */
	public static int unscale( int value ) {
		initialize();
		return (scaleFactor == 1f) ? value : Math.round( value / scaleFactor );
	}

	/**
	 * If user scale factor is not 1, scale the given graphics context by invoking
	 * {@link Graphics2D#scale(double, double)} with user scale factor.
	 */
	public static void scaleGraphics( Graphics2D g ) {
		initialize();
		if( scaleFactor != 1f )
			g.scale( scaleFactor, scaleFactor );
	}

	/**
	 * Scales the given dimension with the user scale factor.
	 * <p>
	 * If user scale factor is 1, then the given dimension is simply returned.
	 * Otherwise, a new instance of {@link Dimension} or {@link DimensionUIResource}
	 * is returned, depending on whether the passed dimension implements {@link UIResource}.
	 */
	public static Dimension scale( Dimension dimension ) {
		initialize();
		return (dimension == null || scaleFactor == 1f)
			? dimension
			: (dimension instanceof UIResource
				? new DimensionUIResource( scale( dimension.width ), scale( dimension.height ) )
				: new Dimension          ( scale( dimension.width ), scale( dimension.height ) ));
	}

	/**
	 * Scales the given insets with the user scale factor.
	 * <p>
	 * If user scale factor is 1, then the given insets is simply returned.
	 * Otherwise, a new instance of {@link Insets} or {@link InsetsUIResource}
	 * is returned, depending on whether the passed dimension implements {@link UIResource}.
	 */
	public static Insets scale( Insets insets ) {
		initialize();
		return (insets == null || scaleFactor == 1f)
			? insets
			: (insets instanceof UIResource
				? new InsetsUIResource( scale( insets.top ), scale( insets.left ), scale( insets.bottom ), scale( insets.right ) )
				: new Insets          ( scale( insets.top ), scale( insets.left ), scale( insets.bottom ), scale( insets.right ) ));
	}
}
