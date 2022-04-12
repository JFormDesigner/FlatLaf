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
import javax.swing.Icon;
import javax.swing.JComponent;
import com.formdev.flatlaf.util.Animator.Interpolator;

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
 * is an instance of {@link JComponent}.
 * A client property is set on the component to store the animation state.
 *
 * @author Karl Tauber
 */
public interface AnimatedIcon
	extends Icon
{
	@Override
	default void paintIcon( Component c, Graphics g, int x, int y ) {
		AnimationSupport.paintIcon( this, c, g, x, y );
	}

	/**
	 * Paints the icon for the given animated value.
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

	/**
	 * Gets the value of the component.
	 * <p>
	 * This can be any value and depends on the component.
	 * If the value changes, then this class animates from the old value to the new one.
	 * <p>
	 * For a toggle button this could be {@code 0} for off and {@code 1} for on.
	 */
	float getValue( Component c );

	/**
	 * Returns whether animation is enabled for this icon (default is {@code true}).
	 */
	default boolean isAnimationEnabled() {
		return true;
	}

	/**
	 * Returns the duration of the animation in milliseconds (default is 150).
	 */
	default int getAnimationDuration() {
		return 150;
	}

	/**
	 * Returns the resolution of the animation in milliseconds (default is 10).
	 * Resolution is the amount of time between timing events.
	 */
	default int getAnimationResolution() {
		return 10;
	}

	/**
	 * Returns the interpolator for the animation.
	 * Default is {@link CubicBezierEasing#STANDARD_EASING}.
	 */
	default Interpolator getAnimationInterpolator() {
		return CubicBezierEasing.STANDARD_EASING;
	}

	/**
	 * Returns the client property key used to store the animation support.
	 */
	default Object getClientPropertyKey() {
		return getClass();
	}

	//---- class AnimationSupport ---------------------------------------------

	/**
	 * Animation support class that stores the animation state and implements the animation.
	 */
	class AnimationSupport
	{
		private float startValue;
		private float targetValue;
		private float animatedValue;
		private float fraction;

		private Animator animator;

		// last x,y coordinates of the icon needed to repaint while animating
		private int x;
		private int y;

		public static void paintIcon( AnimatedIcon icon, Component c, Graphics g, int x, int y ) {
			if( !isAnimationEnabled( icon, c ) ) {
				// paint without animation if animation is disabled or
				// component is not a JComponent and therefore does not support
				// client properties, which are required to keep animation state
				paintIconImpl( icon, c, g, x, y, null );
				return;
			}

			JComponent jc = (JComponent) c;
			Object key = icon.getClientPropertyKey();
			AnimationSupport as = (AnimationSupport) jc.getClientProperty( key );
			if( as == null ) {
				// painted first time --> do not animate, but remember current component value
				as = new AnimationSupport();
				as.startValue = as.targetValue = as.animatedValue = icon.getValue( c );
				as.x = x;
				as.y = y;
				jc.putClientProperty( key, as );
			} else {
				// get component value
				float value = icon.getValue( c );

				if( value != as.targetValue ) {
					// value changed --> (re)start animation

					if( as.animator == null ) {
						// create animator
						AnimationSupport as2 = as;
						as.animator = new Animator( icon.getAnimationDuration(), fraction -> {
							// check whether component was removed while animation is running
							if( !c.isDisplayable() ) {
								as2.animator.stop();
								return;
							}

							// compute animated value
							as2.animatedValue = as2.startValue + ((as2.targetValue - as2.startValue) * fraction);
							as2.fraction = fraction;

							// repaint icon
							c.repaint( as2.x, as2.y, icon.getIconWidth(), icon.getIconHeight() );
						}, () -> {
							as2.startValue = as2.animatedValue = as2.targetValue;
							as2.animator = null;
						} );
					}

					if( as.animator.isRunning() ) {
						// if animation is still running, restart it from the current
						// animated value to the new target value with reduced duration
						as.animator.cancel();
						int duration2 = (int) (icon.getAnimationDuration() * as.fraction);
						if( duration2 > 0 )
							as.animator.setDuration( duration2 );
						as.startValue = as.animatedValue;
					} else {
						// new animation
						as.animator.setDuration( icon.getAnimationDuration() );
						as.animator.setResolution( icon.getAnimationResolution() );
						as.animator.setInterpolator( icon.getAnimationInterpolator() );

						as.animatedValue = as.startValue;
					}

					as.targetValue = value;
					as.animator.start();
				}

				as.x = x;
				as.y = y;
			}

			paintIconImpl( icon, c, g, x, y, as );
		}

		private static void paintIconImpl( AnimatedIcon icon, Component c, Graphics g, int x, int y, AnimationSupport as ) {
			float value = (as != null) ? as.animatedValue : icon.getValue( c );
			icon.paintIconAnimated( c, g, x, y, value );
		}

		private static boolean isAnimationEnabled( AnimatedIcon icon, Component c ) {
			return Animator.useAnimation() && icon.isAnimationEnabled() && c instanceof JComponent;
		}

		public static void saveIconLocation( AnimatedIcon icon, Component c, int x, int y ) {
			if( !isAnimationEnabled( icon, c ) )
				return;

			AnimationSupport as = (AnimationSupport) ((JComponent)c).getClientProperty( icon.getClientPropertyKey() );
			if( as != null ) {
				as.x = x;
				as.y = y;
			}
		}
	}
}
