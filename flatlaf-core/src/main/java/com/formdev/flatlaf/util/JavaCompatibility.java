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

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import com.formdev.flatlaf.FlatLaf;

/**
 * Provides Java version compatibility methods.
 * <p>
 * WARNING: This is private API and may change.
 *
 * @author Karl Tauber
 */
public class JavaCompatibility
{
	private static Method drawStringUnderlineCharAtMethod;
	private static Method getClippedStringMethod;

	/**
	 * Java 8: sun.swing.SwingUtilities2.drawStringUnderlineCharAt( JComponent c,
	 *				Graphics g, String text, int underlinedIndex, int x, int y )
	 * <br>
	 * Java 9: javax.swing.plaf.basic.BasicGraphicsUtils.drawStringUnderlineCharAt( JComponent c,
	 *				Graphics2D g, String string, int underlinedIndex, float x, float y )
	 */
	public static void drawStringUnderlineCharAt( JComponent c, Graphics g,
		String text, int underlinedIndex, int x, int y )
	{
		synchronized( JavaCompatibility.class ) {
			if( drawStringUnderlineCharAtMethod == null ) {
				try {
					Class<?> cls = Class.forName( SystemInfo.isJava_9_orLater
						? "javax.swing.plaf.basic.BasicGraphicsUtils"
						: "sun.swing.SwingUtilities2" );
					drawStringUnderlineCharAtMethod = cls.getMethod( "drawStringUnderlineCharAt", SystemInfo.isJava_9_orLater
						? new Class[] { JComponent.class, Graphics2D.class, String.class, int.class, float.class, float.class }
						: new Class[] { JComponent.class, Graphics.class, String.class, int.class, int.class, int.class } );
				} catch( Exception ex ) {
					Logger.getLogger( FlatLaf.class.getName() ).log( Level.SEVERE, null, ex );
					throw new RuntimeException( ex );
				}
			}
		}

		try {
			if( SystemInfo.isJava_9_orLater )
				drawStringUnderlineCharAtMethod.invoke( null, c, g, text, underlinedIndex, (float) x, (float) y );
			else
				drawStringUnderlineCharAtMethod.invoke( null, c, g, text, underlinedIndex, x, y );
		} catch( IllegalAccessException | IllegalArgumentException | InvocationTargetException ex ) {
			Logger.getLogger( FlatLaf.class.getName() ).log( Level.SEVERE, null, ex );
			throw new RuntimeException( ex );
		}
	}

	/**
	 * Java 8: sun.swing.SwingUtilities2.clipStringIfNecessary( JComponent c,
	 *				FontMetrics fm, String string, int availTextWidth )
	 * <br>
	 * Java 9: javax.swing.plaf.basic.BasicGraphicsUtils.getClippedString( JComponent c,
	 *				FontMetrics fm, String string, int availTextWidth )
	 */
	public static String getClippedString( JComponent c, FontMetrics fm, String string, int availTextWidth ) {
		synchronized( JavaCompatibility.class ) {
			if( getClippedStringMethod == null ) {
				try {
					Class<?> cls = Class.forName( SystemInfo.isJava_9_orLater
						? "javax.swing.plaf.basic.BasicGraphicsUtils"
						: "sun.swing.SwingUtilities2" );
					getClippedStringMethod = cls.getMethod( SystemInfo.isJava_9_orLater
							? "getClippedString"
							: "clipStringIfNecessary",
						new Class[] { JComponent.class, FontMetrics.class, String.class, int.class } );
				} catch( Exception ex ) {
					Logger.getLogger( FlatLaf.class.getName() ).log( Level.SEVERE, null, ex );
					throw new RuntimeException( ex );
				}
			}
		}

		try {
			return (String) getClippedStringMethod.invoke( null, c, fm, string, availTextWidth );
		} catch( IllegalAccessException | IllegalArgumentException | InvocationTargetException ex ) {
			Logger.getLogger( FlatLaf.class.getName() ).log( Level.SEVERE, null, ex );
			throw new RuntimeException( ex );
		}
	}
}
