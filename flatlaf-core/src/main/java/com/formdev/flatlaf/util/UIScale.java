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
import java.awt.GraphicsEnvironment;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.DimensionUIResource;
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
 *
 * 2) user scaling mode
 *
 * This mode is for Java 8 compatibility and can be removed when changing minimum
 * required Java version to 9.
 * The user scale factor is computed based on the used font.
 * The JRE does not scale anything.
 * So we have to invoke {@link #scale(float)} where necessary.
 * There is only one user scale factor for all displays.
 *
 * @author Karl Tauber
 */
public class UIScale
{
	private static final boolean DEBUG = false;

	//---- system scaling (Java 9) --------------------------------------------

	private static Boolean jreHiDPI;

	private static boolean isJreHiDPIEnabled() {
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

	//---- user scaling (Java 8) ----------------------------------------------

	private static float scaleFactor = 1;

	static {
		if( isEnabled() ) {
			// listener to update scale factor if LaF changed or if Label.font changed
			// (e.g. option "Override default fonts" in IntelliJ IDEA)
			PropertyChangeListener listener = new PropertyChangeListener() {
				@Override
				public void propertyChange( PropertyChangeEvent e ) {
					String propName = e.getPropertyName();
					if( "lookAndFeel".equals( propName ) ) {
						// it is not necessary (and possible) to remove listener of old LaF defaults
						if( e.getNewValue() instanceof LookAndFeel )
							UIManager.getLookAndFeelDefaults().addPropertyChangeListener( this );
						updateScaleFactor();
					} else if( "Label.font".equals( propName ) )
						updateScaleFactor();
				}
			};
			UIManager.addPropertyChangeListener( listener );
			UIManager.getLookAndFeelDefaults().addPropertyChangeListener( listener );

			updateScaleFactor();
		}
	}

	private static void updateScaleFactor() {
		if( !isEnabled() )
			return;

		// use font size to calculate scale factor (instead of DPI)
		// because even if we are on a HiDPI display it is not sure
		// that a larger font size is set by the current LaF
		// (e.g. can avoid large icons with small text)
		Font font = UIManager.getFont( "Label.font" );

		// default font size
		float fontSizeDivider = 12f;

		if( SystemInfo.IS_WINDOWS ) {
			// Windows LaF uses Tahoma font rather than the actual Windows system font (Segoe UI),
			// and its size is always ca. 10% smaller than the actual system font size.
			// Tahoma 11 is used at 100%
			if( "Tahoma".equals( font.getFamily() ) )
				fontSizeDivider = 11f;
		} else if( SystemInfo.IS_LINUX ) {
			// default font size for Unity and Gnome is 15
			fontSizeDivider = 15f;
		}

		setUserScaleFactor( font.getSize() / fontSizeDivider );
	}

	private static boolean isEnabled() {
		if( isJreHiDPIEnabled() )
			return false; // disable user scaling if JRE scales

		// same as in IntelliJ IDEA
		String hidpi = System.getProperty( "hidpi" );
		return (hidpi != null) ? Boolean.parseBoolean( hidpi ) : true;
	}

	public static float getUserScaleFactor() {
		return scaleFactor;
	}

	private static void setUserScaleFactor( float scaleFactor ) {
		if( scaleFactor <= 1f )
			scaleFactor = 1f;
		else // round scale factor to 1/4
			scaleFactor = Math.round( scaleFactor * 4f ) / 4f;

		UIScale.scaleFactor = scaleFactor;

		if( DEBUG )
			System.out.println( "HiDPI scale factor " + scaleFactor );
	}

	public static float scale( float value ) {
		return (scaleFactor == 1) ? value : (value * scaleFactor);
	}

	public static int scale( int value ) {
		return (scaleFactor == 1) ? value : Math.round( value * scaleFactor );
	}

	public static float unscale( float value ) {
		return (scaleFactor == 1f) ? value : (value / scaleFactor);
	}

	public static int unscale( int value ) {
		return (scaleFactor == 1f) ? value : Math.round( value / scaleFactor );
	}

	public static void scaleGraphics( Graphics2D g ) {
		if( scaleFactor != 1f )
			g.scale( scaleFactor, scaleFactor );
	}

	public static Dimension scale( Dimension dimension ) {
		return (dimension == null || scaleFactor == 1f)
			? dimension
			: (dimension instanceof UIResource
				? new DimensionUIResource( scale( dimension.width ), scale( dimension.height ) )
				: new Dimension          ( scale( dimension.width ), scale( dimension.height ) ));
	}
}
