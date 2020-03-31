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

/**
 * Two scaling modes are supported for HiDPI displays:
 *
 * 1) system scaling mode
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
 * 2) user scaling mode
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

	public static boolean isSystemScalingEnabled() {
		if( jreHiDPI != null )
			return jreHiDPI;

		jreHiDPI = false;

		if( SystemInfo.IS_JAVA_9_OR_LATER ) {
			// Java 9 and later supports per-monitor scaling
			jreHiDPI = true;
		} else if( SystemInfo.IS_JETBRAINS_JVM ) {
			// IntelliJ IDEA ships its own JetBrains Java 8 JRE that may supports per-monitor scaling
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

	public static double getSystemScaleFactor( Graphics2D g ) {
		return isSystemScalingEnabled() ? g.getDeviceConfiguration().getDefaultTransform().getScaleX() : 1;
	}

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

		// use font size to calculate scale factor (instead of DPI)
		// because even if we are on a HiDPI display it is not sure
		// that a larger font size is set by the current LaF
		// (e.g. can avoid large icons with small text)
		Font font = UIManager.getFont( "defaultFont" );
		if( font == null )
			font = UIManager.getFont( "Label.font" );

		setUserScaleFactor( computeScaleFactor( font ) );
	}

	private static float computeScaleFactor( Font font ) {
		// default font size
		float fontSizeDivider = 12f;

		if( SystemInfo.IS_WINDOWS ) {
			// Windows LaF uses Tahoma font rather than the actual Windows system font (Segoe UI),
			// and its size is always ca. 10% smaller than the actual system font size.
			// Tahoma 11 is used at 100%
			if( "Tahoma".equals( font.getFamily() ) )
				fontSizeDivider = 11f;
		} else if( SystemInfo.IS_MAC ) {
			// default font size on macOS is 13
			fontSizeDivider = 13f;
		} else if( SystemInfo.IS_LINUX ) {
			// default font size for Unity and Gnome is 15 and for KDE it is 13
			fontSizeDivider = SystemInfo.IS_KDE ? 13f : 15f;
		}

		return font.getSize() / fontSizeDivider;
	}

	private static boolean isUserScalingEnabled() {
		// same as in IntelliJ IDEA
		String hidpi = System.getProperty( "hidpi" );
		return (hidpi != null) ? Boolean.parseBoolean( hidpi ) : true;
	}

	/**
	 * Applies a custom scale factor given in system property "flatlaf.uiScale"
	 * to the given font.
	 */
	public static FontUIResource applyCustomScaleFactor( FontUIResource font ) {
		String uiScale = System.getProperty( "flatlaf.uiScale" );
		float scaleFactor = parseScaleFactor( uiScale );
		if( scaleFactor <= 0 )
			return font;

		float fontScaleFactor = computeScaleFactor( font );
		if( scaleFactor == fontScaleFactor )
			return font;

		int newFontSize = Math.round( (font.getSize() / fontScaleFactor) * scaleFactor );
		return new FontUIResource( font.deriveFont( (float) newFontSize ) );
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

	public static float getUserScaleFactor() {
		initialize();
		return scaleFactor;
	}

	private static void setUserScaleFactor( float scaleFactor ) {
		if( scaleFactor <= 1f )
			scaleFactor = 1f;
		else // round scale factor to 1/4
			scaleFactor = Math.round( scaleFactor * 4f ) / 4f;

		float oldScaleFactor = UIScale.scaleFactor;
		UIScale.scaleFactor = scaleFactor;

		if( DEBUG )
			System.out.println( "HiDPI scale factor " + scaleFactor );

		if( changeSupport != null )
			changeSupport.firePropertyChange( "userScaleFactor", oldScaleFactor, scaleFactor );
	}

	public static float scale( float value ) {
		initialize();
		return (scaleFactor == 1) ? value : (value * scaleFactor);
	}

	public static int scale( int value ) {
		initialize();
		return (scaleFactor == 1) ? value : Math.round( value * scaleFactor );
	}

	/**
	 * Similar as scale(int) but always "rounds down".
	 */
	public static int scale2( int value ) {
		initialize();
		return (scaleFactor == 1) ? value : (int) (value * scaleFactor);
	}

	public static float unscale( float value ) {
		initialize();
		return (scaleFactor == 1f) ? value : (value / scaleFactor);
	}

	public static int unscale( int value ) {
		initialize();
		return (scaleFactor == 1f) ? value : Math.round( value / scaleFactor );
	}

	public static void scaleGraphics( Graphics2D g ) {
		initialize();
		if( scaleFactor != 1f )
			g.scale( scaleFactor, scaleFactor );
	}

	public static Dimension scale( Dimension dimension ) {
		initialize();
		return (dimension == null || scaleFactor == 1f)
			? dimension
			: (dimension instanceof UIResource
				? new DimensionUIResource( scale( dimension.width ), scale( dimension.height ) )
				: new Dimension          ( scale( dimension.width ), scale( dimension.height ) ));
	}

	public static Insets scale( Insets insets ) {
		initialize();
		return (insets == null || scaleFactor == 1f)
			? insets
			: (insets instanceof UIResource
				? new InsetsUIResource( scale( insets.top ), scale( insets.left ), scale( insets.bottom ), scale( insets.right ) )
				: new Insets          ( scale( insets.top ), scale( insets.left ), scale( insets.bottom ), scale( insets.right ) ));
	}
}
