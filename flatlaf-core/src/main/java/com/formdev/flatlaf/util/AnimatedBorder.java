/*
 * Copyright 2021 FormDev Software GmbH
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
import javax.swing.JComponent;
import javax.swing.border.Border;
import com.formdev.flatlaf.util.Animator.Interpolator;

/**
 * Border that automatically animates painting on component value changes.
 * <p>
 * {@link #getValue(Component)} returns the value of the component.
 * If the value changes, then {@link #paintBorderAnimated(Component, Graphics, int, int, int, int, float)}
 * is invoked multiple times with animated value (from old value to new value).
 * <p>
 * Example for an animated border:
 * <pre>
 * private class AnimatedMinimalTestBorder
 *     implements AnimatedBorder
 * {
 *     &#64;Override
 *     public void paintBorderAnimated( Component c, Graphics g, int x, int y, int width, int height, float animatedValue ) {
 *         int lh = UIScale.scale( 2 );
 *
 *         g.setColor( Color.blue );
 *         g.fillRect( x, y + height - lh, Math.round( width * animatedValue ), lh );
 *     }
 *
 *     &#64;Override
 *     public float getValue( Component c ) {
 *         return c.isFocusOwner() ? 1 : 0;
 *     }
 *
 *     &#64;Override
 *     public Insets getBorderInsets( Component c ) {
 *         return UIScale.scale( new Insets( 4, 4, 4, 4 ) );
 *     }
 *
 *     &#64;Override public boolean isBorderOpaque() { return false; }
 * }
 *
 * // sample usage
 * JTextField textField = new JTextField();
 * textField.setBorder( new AnimatedMinimalTestBorder() );
 * </pre>
 *
 * Animation works only if the component passed to {@link #paintBorder(Component, Graphics, int, int, int, int)}
 * is a instance of {@link JComponent}.
 * A client property is set on the component to store the animation state.
 *
 * @author Karl Tauber
 * @since 1.5
 */
public interface AnimatedBorder
	extends Border
{
	@Override
	default void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		AnimationSupport.paintBorder( this, c, g, x, y, width, height );
	}

	/**
	 * Paints the border for the given animated value.
	 *
	 * @param c the component that this border belongs to
	 * @param g the graphics context
	 * @param x the x coordinate of the border
	 * @param y the y coordinate of the border
	 * @param width the width coordinate of the border
	 * @param height the height coordinate of the border
	 * @param animatedValue the animated value, which is either equal to what {@link #getValue(Component)}
	 *     returned, or somewhere between the previous value and the latest value
	 *     that {@link #getValue(Component)} returned
	 */
	void paintBorderAnimated( Component c, Graphics g, int x, int y, int width, int height, float animatedValue );

	/**
	 * Repaint the animated part of the border.
	 * <p>
	 * Useful to limit the repaint region. E.g. if only the bottom border is animated.
	 * If more than one border side is animated (e.g. bottom and right side), then it
	 * makes no sense to do separate repaints because the Swing repaint manager unions
	 * the regions and the whole component is repainted.
	 * <p>
	 * The default implementation repaints the whole component.
	 */
	default void repaintBorder( Component c, int x, int y, int width, int height ) {
		c.repaint( x, y, width, height );
	}

	/**
	 * Gets the value of the component.
	 * <p>
	 * This can be any value and depends on the component.
	 * If the value changes, then this class animates from the old value to the new one.
	 * <p>
	 * For a text field this could be {@code 0} for not focused and {@code 1} for focused.
	 */
	float getValue( Component c );

	/**
	 * Returns whether animation is enabled for this border (default is {@code true}).
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

		// last bounds of the border needed to repaint while animating
		private int x;
		private int y;
		private int width;
		private int height;

		public static void paintBorder( AnimatedBorder border, Component c, Graphics g,
			int x, int y, int width, int height )
		{
			if( !isAnimationEnabled( border, c ) ) {
				// paint without animation if animation is disabled or
				// component is not a JComponent and therefore does not support
				// client properties, which are required to keep animation state
				paintBorderImpl( border, c, g, x, y, width, height, null );
				return;
			}

			JComponent jc = (JComponent) c;
			Object key = border.getClientPropertyKey();
			AnimationSupport as = (AnimationSupport) jc.getClientProperty( key );
			if( as == null ) {
				// painted first time --> do not animate, but remember current component value
				as = new AnimationSupport();
				as.startValue = as.targetValue = as.animatedValue = border.getValue( c );
				jc.putClientProperty( key, as );
			} else {
				// get component value
				float value = border.getValue( c );

				if( value != as.targetValue ) {
					// value changed --> (re)start animation

					if( as.animator == null ) {
						// create animator
						AnimationSupport as2 = as;
						as.animator = new Animator( border.getAnimationDuration(), fraction -> {
							// check whether component was removed while animation is running
							if( !c.isDisplayable() ) {
								as2.animator.stop();
								return;
							}

							// compute animated value
							as2.animatedValue = as2.startValue + ((as2.targetValue - as2.startValue) * fraction);
							as2.fraction = fraction;

							// repaint border
							border.repaintBorder( c, as2.x, as2.y, as2.width, as2.height );
						}, () -> {
							as2.startValue = as2.animatedValue = as2.targetValue;
							as2.animator = null;
						} );
					}

					if( as.animator.isRunning() ) {
						// if animation is still running, restart it from the current
						// animated value to the new target value with reduced duration
						as.animator.cancel();
						int duration2 = (int) (border.getAnimationDuration() * as.fraction);
						if( duration2 > 0 )
							as.animator.setDuration( duration2 );
						as.startValue = as.animatedValue;
					} else {
						// new animation
						as.animator.setDuration( border.getAnimationDuration() );
						as.animator.setResolution( border.getAnimationResolution() );
						as.animator.setInterpolator( border.getAnimationInterpolator() );

						as.animatedValue = as.startValue;
					}

					as.targetValue = value;
					as.animator.start();
				}
			}

			as.x = x;
			as.y = y;
			as.width = width;
			as.height = height;

			paintBorderImpl( border, c, g, x, y, width, height, as );
		}

		private static void paintBorderImpl( AnimatedBorder border, Component c, Graphics g,
			int x, int y, int width, int height, AnimationSupport as )
		{
			float value = (as != null) ? as.animatedValue : border.getValue( c );
			border.paintBorderAnimated( c, g, x, y, width, height, value );
		}

		private static boolean isAnimationEnabled( AnimatedBorder border, Component c ) {
			return Animator.useAnimation() && border.isAnimationEnabled() && c instanceof JComponent;
		}
	}
}
