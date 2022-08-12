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
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import javax.swing.JComponent;

/**
 * Provides Java version compatibility methods.
 * <p>
 * WARNING: This is private API and may change.
 *
 * @author Karl Tauber
 */
public class JavaCompatibility
{
	private static MethodHandle drawStringUnderlineCharAtMethod;
	private static MethodHandle getClippedStringMethod;

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
					MethodType mt = MethodType.methodType( void.class, SystemInfo.isJava_9_orLater
						? new Class[] { JComponent.class, Graphics2D.class, String.class, int.class, float.class, float.class }
						: new Class[] { JComponent.class, Graphics.class, String.class, int.class, int.class, int.class } );
					drawStringUnderlineCharAtMethod = MethodHandles.publicLookup().findStatic( cls, "drawStringUnderlineCharAt", mt );
				} catch( Exception ex ) {
					LoggingFacade.INSTANCE.logSevere( null, ex );
					throw new RuntimeException( ex );
				}
			}
		}

		try {
			if( SystemInfo.isJava_9_orLater )
				drawStringUnderlineCharAtMethod.invoke( c, (Graphics2D) g, text, underlinedIndex, (float) x, (float) y );
			else
				drawStringUnderlineCharAtMethod.invoke( c, g, text, underlinedIndex, x, y );
		} catch( Throwable ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
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
					MethodType mt = MethodType.methodType( String.class, JComponent.class, FontMetrics.class, String.class, int.class );
					getClippedStringMethod = MethodHandles.publicLookup().findStatic( cls, SystemInfo.isJava_9_orLater
							? "getClippedString"
							: "clipStringIfNecessary",
						mt );
				} catch( Exception ex ) {
					LoggingFacade.INSTANCE.logSevere( null, ex );
					throw new RuntimeException( ex );
				}
			}
		}

		try {
			return (String) getClippedStringMethod.invoke( c, fm, string, availTextWidth );
		} catch( Throwable ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
			throw new RuntimeException( ex );
		}
	}
}
