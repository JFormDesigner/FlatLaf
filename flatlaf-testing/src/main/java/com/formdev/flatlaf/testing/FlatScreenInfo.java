/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.testing;

import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import javax.swing.SwingUtilities;

/**
 * Displays information about screens connected to a computer.
 *
 * This is a single-file program that can be compiled/run without any other
 * FlatLaf code or dependencies.
 *
 * Since Java 11, you can run this program from source with:
 *     java FlatScreenInfo.java
 *
 * @author Karl Tauber
 */
public class FlatScreenInfo
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			printScreenInfo();
		} );
	}

	private static void printScreenInfo() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreenDevice = graphicsEnvironment.getDefaultScreenDevice();
		GraphicsDevice[] screenDevices = graphicsEnvironment.getScreenDevices();

		System.out.print( "Scale factors:  " );
		for( GraphicsDevice gd : screenDevices ) {
			GraphicsConfiguration gc = gd.getDefaultConfiguration();

			if( gd != screenDevices[0] )
				System.out.print( " / " );

			System.out.print( (int) (gc.getDefaultTransform().getScaleX() * 100) );
			System.out.print( "%" );
		}
		System.out.println();

		System.out.println( "Java version:   " + System.getProperty( "java.version" ) );
		System.out.println( "Java vendor:    " + System.getProperty( "java.vendor" ) );

		for( GraphicsDevice gd : screenDevices ) {
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			DisplayMode displayMode = gd.getDisplayMode();
			Rectangle bounds = gc.getBounds();
			int width = displayMode.getWidth();
			int height = displayMode.getHeight();
			double boundsScaleX = (bounds.width > width)
				? (double) bounds.width / (double) width
				: -((double) width / (double) bounds.width);
			double boundsScaleY = (bounds.height > height)
				? (double) bounds.height / (double) height
				: -((double) height / (double) bounds.height);
			boundsScaleX = Math.round( boundsScaleX * 1000. ) / 1000.;
			boundsScaleY = Math.round( boundsScaleY * 1000. ) / 1000.;
			Insets screenInsets = toolkit.getScreenInsets( gc );
			AffineTransform defaultTransform = gc.getDefaultTransform();
			double scaleX = defaultTransform.getScaleX();
			double scaleY = defaultTransform.getScaleY();

			System.out.println();
			System.out.print( "ID:      " + gd.getIDstring() );
			if( gd == defaultScreenDevice )
				System.out.print( " (main)" );
			System.out.println();

			System.out.printf( "Size:    %d x %d / %d Bit / %d Hz%n",
				displayMode.getWidth(), displayMode.getHeight(),
				displayMode.getBitDepth(), displayMode.getRefreshRate() );
			System.out.printf( "Bounds:  %d x %d / x %d / y %d",
				bounds.width, bounds.height, bounds.x, bounds.y );
			if( Math.abs( boundsScaleX ) != 1 || Math.abs( boundsScaleY ) != 1 )
				System.out.printf( "   (scale %s)", toString( boundsScaleX, boundsScaleY ) );
			System.out.println();
			System.out.printf( "Insets:  left %d / right %d / top %d / bottom %d%n",
				screenInsets.left, screenInsets.right, screenInsets.top, screenInsets.bottom );
			System.out.println( "Scale:   " + toString( scaleX, scaleY ) );

			// report warning if screen bounds intersects with another screen
			// https://github.com/JFormDesigner/FlatLaf/issues/177
			for( GraphicsDevice gd2 : screenDevices ) {
				if( gd2 == gd )
					continue;

				Rectangle bounds2 = gd2.getDefaultConfiguration().getBounds();
				if( bounds2.intersects( bounds ) ) {
					System.out.println( "Warning: bounds of this screen intersect with bounds of " + gd2.getIDstring() );
					System.out.println( "         this can lead to misplaced popups" );
				}
			}
		}
	}

	private static String toString( double scaleX, double scaleY ) {
		return (scaleX == scaleY)
			? String.valueOf( scaleX )
			: scaleX + " / " + scaleY;
	}
}
