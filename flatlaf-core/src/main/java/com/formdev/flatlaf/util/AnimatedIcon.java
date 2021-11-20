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

package com.formdev.flatlaf.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Icon that automatically animates painting on component value changes.
 * <p>
 * {@link #getValue(Component)} returns the value of the component.
 * If the value changes, then {@link #paintIconAnimated(Component, Graphics, int, int, float)}
 * is invoked multiple times with animated value (from old value to new value).
 * <p>
 * Example for an animated icon:
 * <pre>
 * private class AnimatedMinimalTestIcon
 *     implements AnimatedIcon
 * {
 *     &#64;Override public int getIconWidth() { return 100; }
 *     &#64;Override public int getIconHeight() { return 20; }
 *
 *     &#64;Override
 *     public void paintIconAnimated( Component c, Graphics g, int x, int y, float animatedValue ) {
 *         int w = getIconWidth();
 *         int h = getIconHeight();
 *
 *         g.setColor( Color.red );
 *         g.drawRect( x, y, w - 1, h - 1 );
 *         g.fillRect( x, y, Math.round( w * animatedValue ), h );
 *     }
 *
 *     &#64;Override
 *     public float getValue( Component c ) {
 *         return ((AbstractButton)c).isSelected() ? 1 : 0;
 *     }
 * }
 *
 * // sample usage
 * JCheckBox checkBox = new JCheckBox( "test" );
 * checkBox.setIcon( new AnimatedMinimalTestIcon() );
 * </pre>
 *
 * Animation works only if the component passed to {@link #paintIcon(Component, Graphics, int, int)}
 * is a instance of {@link JComponent}.
 * A client property is set on the component to store the animation state.
 *
 * @author Karl Tauber
 */
public interface AnimatedIcon
	extends Icon, AnimatedPainter
{
	/**
	 * Invokes {@link #paintWithAnimation(Component, Graphics, int, int, int, int)}.
	 */
	@Override
	default void paintIcon( Component c, Graphics g, int x, int y ) {
		paintWithAnimation( c, g, x, y, getIconWidth(), getIconHeight() );
	}

	/**
	 * Bridge method that is called from (new) superclass and delegates to
	 * {@link #paintIconAnimated(Component, Graphics, int, int, float)}.
	 * Necessary for API compatibility.
	 *
	 * @since 2
	 */
	@Override
	default void paintAnimated( Component c, Graphics2D g, int x, int y, int width, int height, float animatedValue ) {
		paintIconAnimated( c, g, x, y, animatedValue );
	}

	/**
	 * Paints the icon for the given (animated) value.
	 *
	 * @param c the component that this icon belongs to
	 * @param g the graphics context
	 * @param x the x coordinate of the icon
	 * @param y the y coordinate of the icon
	 * @param animatedValue the animated value, which is either equal to what {@link #getValue(Component)}
	 *     returned, or somewhere between the previous value and the latest value
	 *     that {@link #getValue(Component)} returned
	 */
	void paintIconAnimated( Component c, Graphics g, int x, int y, float animatedValue );

	//---- class AnimationSupport ---------------------------------------------

	/**
	 * Animation support.
	 */
	class AnimationSupport
	{
		public static void paintIcon( AnimatedIcon icon, Component c, Graphics g, int x, int y ) {
			AnimatedPainterSupport.paint( icon, c, g, x, y, icon.getIconWidth(), icon.getIconHeight() );
		}

		public static void saveIconLocation( AnimatedIcon icon, Component c, int x, int y ) {
			AnimatedPainterSupport.saveLocation( icon, c, x, y );
		}
	}
}
