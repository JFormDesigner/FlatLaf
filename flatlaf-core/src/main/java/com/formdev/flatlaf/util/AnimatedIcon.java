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
 * {@link #getValues(Component)} returns the value(s) of the component.
 * If the value(s) have changed, then {@link #paintAnimated(Component, Graphics2D, int, int, int, int, float[])}
 * is invoked multiple times with animated value(s) (from old value(s) to new value(s)).
 * If {@link #getValues(Component)} returns multiple values, then each value gets its own independent animation.
 * <p>
 * Example for an animated icon:
 * <pre>
 * private class MyAnimatedIcon
 *     implements AnimatedIcon
 * {
 *     &#64;Override public int getIconWidth() { return 100; }
 *     &#64;Override public int getIconHeight() { return 20; }
 *
 *     &#64;Override
 *     public void paintAnimated( Component c, Graphics2D g, int x, int y, int width, int height, float[] animatedValues ) {
 *         g.setColor( Color.red );
 *         g.drawRect( x, y, width - 1, height - 1 );
 *         g.fillRect( x, y, Math.round( width * animatedValues[0] ), height );
 *     }
 *
 *     &#64;Override
 *     public float[] getValues( Component c ) {
 *         return new float[] { ((AbstractButton)c).isSelected() ? 1 : 0 };
 *     }
 * }
 *
 * // sample usage
 * JCheckBox checkBox = new JCheckBox( "test" );
 * checkBox.setIcon( new MyAnimatedIcon() );
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
	 * {@inheritDoc}
	 *
	 * @since 2
	 */
	@Override
	default void paintAnimated( Component c, Graphics2D g, int x, int y, int width, int height, float[] animatedValues ) {
		paintIconAnimated( c, g, x, y, animatedValues[0] );
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
	 *
	 * @deprecated override {@link #paintAnimated(Component, Graphics2D, int, int, int, int, float[])} instead
	 */
	@Deprecated
	default void paintIconAnimated( Component c, Graphics g, int x, int y, float animatedValue ) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 2
	 */
	@Override
	default float[] getValues( Component c ) {
		return new float[] { getValue( c ) };
	}

	/**
	 * Gets the value of the component.
	 * <p>
	 * This can be any value and depends on the component.
	 * If the value changes, then this class animates from the old value to the new one.
	 * <p>
	 * For a toggle button this could be {@code 0} for off and {@code 1} for on.
	 *
	 * @deprecated override {@link #getValues(Component)} instead
	 */
	@Deprecated
	default float getValue( Component c ) {
		return 0;
	}

	//---- class AnimationSupport ---------------------------------------------

	/**
	 * Animation support.
	 */
	@Deprecated
	class AnimationSupport
	{
		/**
		 * @deprecated use {@link AnimatedPainter#paintWithAnimation(Component, Graphics, int, int, int, int)} instead
		 */
		@Deprecated
		public static void paintIcon( AnimatedIcon icon, Component c, Graphics g, int x, int y ) {
			AnimatedPainterSupport.paint( icon, c, g, x, y, icon.getIconWidth(), icon.getIconHeight() );
		}

		/**
		 * @deprecated use {@link AnimatedPainter#saveRepaintLocation(AnimatedPainter, Component, int, int)} instead
		 */
		@Deprecated
		public static void saveIconLocation( AnimatedIcon icon, Component c, int x, int y ) {
			AnimatedPainterSupport.saveRepaintLocation( icon, c, x, y );
		}
	}
}
