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
import java.awt.Graphics2D;
import javax.swing.JComponent;
import com.formdev.flatlaf.util.Animator.Interpolator;

/**
 * Painter that automatically animates painting on component value(s) changes.
 * <p>
 * {@link #getValues(Component)} returns the value(s) of the component.
 * If the value(s) have changed, then {@link #paintAnimated(Component, Graphics2D, int, int, int, int, float[])}
 * is invoked multiple times with animated value(s) (from old value(s) to new value(s)).
 * If {@link #getValues(Component)} returns multiple values, then each value gets its own independent animation.
 * <p>
 * See {@link AnimatedBorder} or {@link AnimatedIcon} for examples.
 * <p>
 * Animation works only if the component passed to {@link #paintWithAnimation(Component, Graphics, int, int, int, int)}
 * is a instance of {@link JComponent}.
 * A client property is set on the component to store the animation state.
 *
 * @author Karl Tauber
 * @since 2
 */
public interface AnimatedPainter
{
	/**
	 * Starts painting.
	 * Either invokes {@link #paintAnimated(Component, Graphics2D, int, int, int, int, float[])}
	 * once to paint current value(s) (see {@link #getValues(Component)}. Or if value(s) has
	 * changed, compared to last painting, then it starts an animation and invokes
	 * {@link #paintAnimated(Component, Graphics2D, int, int, int, int, float[])}
	 * multiple times with animated value(s) (from old value(s) to new value(s)).
	 *
	 * @param c the component that this painter belongs to
	 * @param g the graphics context
	 * @param x the x coordinate of the paint area
	 * @param y the y coordinate of the paint area
	 * @param width the width of the paint area
	 * @param height the height of the paint area
	 */
	default void paintWithAnimation( Component c, Graphics g, int x, int y, int width, int height ) {
		AnimatedPainterSupport.paint( this, c, g, x, y, width, height );
	}

	/**
	 * Paints the given (animated) value(s).
	 * <p>
	 * Invoked from {@link #paintWithAnimation(Component, Graphics, int, int, int, int)}.
	 *
	 * @param c the component that this painter belongs to
	 * @param g the graphics context
	 * @param x the x coordinate of the paint area
	 * @param y the y coordinate of the paint area
	 * @param width the width of the paint area
	 * @param height the height of the paint area
	 * @param animatedValues the animated values, which are either equal to what {@link #getValues(Component)}
	 *     returned, or somewhere between the previous values and the latest values
	 *     that {@link #getValues(Component)} returned
	 */
	void paintAnimated( Component c, Graphics2D g, int x, int y, int width, int height, float[] animatedValues );

	/**
	 * Invoked from animator to repaint an area.
	 * <p>
	 * Useful to limit the repaint region. E.g. if only the bottom border is animated.
	 * If more than one border side is animated (e.g. bottom and right side), then it
	 * makes no sense to do separate repaints because the Swing repaint manager unions
	 * the regions and the whole component is repainted.
	 * <p>
	 * The default implementation repaints the whole given area.
	 */
	default void repaintDuringAnimation( Component c, int x, int y, int width, int height ) {
		c.repaint( x, y, width, height );
	}

	/**
	 * Gets the value(s) of the component.
	 * <p>
	 * This can be any value and depends on the component.
	 * If the value(s) changes, then this class animates from the old value(s) to the new ones.
	 * If multiple values are returned, then each value gets its own independent animation.
	 * <p>
	 * For a toggle button this could be {@code 0} for off and {@code 1} for on.
	 */
	float[] getValues( Component c );

	/**
	 * Returns whether animation is enabled for this painter (default is {@code true}).
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
	 * Returns the duration of the animation in milliseconds (default is 150)
	 * for the given value index and value.
	 */
	default int getAnimationDuration( int valueIndex, float value ) {
		return getAnimationDuration();
	}

	/**
	 * Returns the resolution of the animation in milliseconds (default is 10)
	 * for the given value index and value.
	 * Resolution is the amount of time between timing events.
	 */
	default int getAnimationResolution( int valueIndex, float value ) {
		return getAnimationResolution();
	}

	/**
	 * Returns the interpolator for the animation
	 * for the given value index and value.
	 * Default is {@link CubicBezierEasing#STANDARD_EASING}.
	 */
	default Interpolator getAnimationInterpolator( int valueIndex, float value ) {
		return getAnimationInterpolator();
	}

	/**
	 * Returns the client property key used to store the animation support.
	 */
	default Object getClientPropertyKey() {
		return getClass();
	}

	/**
	 * Saves location for repainting animated area with
	 * {@link AnimatedPainter#repaintDuringAnimation(Component, int, int, int, int)}.
	 * Only needed when graphics context passed to {@link #paintWithAnimation(Component, Graphics, int, int, int, int)}
	 * uses transformed location.
	 */
	static void saveRepaintLocation( AnimatedPainter painter, Component c, int x, int y ) {
		AnimatedPainterSupport.saveRepaintLocation( painter, c, x, y );
	}
}
