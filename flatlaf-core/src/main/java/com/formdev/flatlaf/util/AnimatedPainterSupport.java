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
import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 * Animation support class that stores the animation state and implements the animation.
 *
 * @author Karl Tauber
 * @since 2
 */
class AnimatedPainterSupport
{
	private final int valueIndex;

	private float startValue;
	private float targetValue;
	private float animatedValue;
	private float fraction;

	private Animator animator;

	// last bounds of the paint area needed to repaint while animating
	private int x;
	private int y;
	private int width;
	private int height;

	private AnimatedPainterSupport( int valueIndex ) {
		this.valueIndex = valueIndex;
	}

	static void paint( AnimatedPainter painter, Component c, Graphics2D g,
		int x, int y, int width, int height )
	{
		// get animatable component values
		float[] values = painter.getAnimatableValues( c );

		if( !isAnimationEnabled( painter, c ) ) {
			// paint without animation if animation is disabled or
			// component is not a JComponent and therefore does not support
			// client properties, which are required to keep animation state
			painter.paintAnimated( c, g, x, y, width, height, values );
			return;
		}

		JComponent jc = (JComponent) c;
		Object key = painter.getAnimationClientPropertyKey();
		AnimatedPainterSupport[] ass = (AnimatedPainterSupport[]) jc.getClientProperty( key );

		// check whether length of values array has changed
		if( ass != null && ass.length != values.length ) {
			// cancel all running animations
			for( int i = 0; i < ass.length; i++ ) {
				AnimatedPainterSupport as = ass[i];
				if( as.animator != null )
					as.animator.cancel();
			}
			ass = null;
		}

		if( ass == null ) {
			ass = new AnimatedPainterSupport[values.length];
			jc.putClientProperty( key, ass );
		}

		for( int i = 0; i < ass.length; i++ ) {
			AnimatedPainterSupport as = ass[i];
			float value = values[i];

			if( as == null ) {
				// painted first time --> do not animate, but remember current component value
				as = new AnimatedPainterSupport( i );
				as.startValue = as.targetValue = as.animatedValue = value;
				ass[i] = as;
			} else if( value != as.targetValue ) {
				// value changed --> (re)start animation

				int animationDuration = painter.getAnimationDuration( as.valueIndex, value );

				// do not animate if animation duration (for current value) is zero
				if( animationDuration <= 0 ) {
					if( as.animator != null ) {
						as.animator.cancel();
						as.animator = null;
					}
					as.startValue = as.targetValue = as.animatedValue = value;
					as.fraction = 0;
					continue;
				}

				if( as.animator == null ) {
					// create animator
					AnimatedPainterSupport as2 = as;
					as.animator = new Animator( 1, fraction -> {
						// check whether component was removed while animation is running
						if( !c.isDisplayable() ) {
							as2.animator.stop();
							return;
						}

						// compute animated value
						as2.animatedValue = as2.startValue + ((as2.targetValue - as2.startValue) * fraction);
						as2.fraction = fraction;

						// repaint
						painter.repaintDuringAnimation( c, as2.x, as2.y, as2.width, as2.height );
					}, () -> {
						as2.startValue = as2.animatedValue = as2.targetValue;
						as2.animator = null;
					} );
				}

				if( as.animator.isRunning() ) {
					// if animation is still running, restart it from the current
					// animated value to the new target value with reduced duration
					as.animator.cancel();
					int duration2 = (int) (animationDuration * as.fraction);
					if( duration2 > 0 )
						as.animator.setDuration( duration2 );
					as.startValue = as.animatedValue;
				} else {
					// new animation
					as.animator.setDuration( animationDuration );

					as.animatedValue = as.startValue;
				}

				// update animator for new value
				as.animator.setResolution( painter.getAnimationResolution( as.valueIndex, value ) );
				as.animator.setInterpolator( painter.getAnimationInterpolator( as.valueIndex, value ) );

				// start animation
				as.targetValue = value;
				as.animator.start();
			}

			as.x = x;
			as.y = y;
			as.width = width;
			as.height = height;
		}

		float[] animatedValues = new float[ass.length];
		for( int i = 0; i < ass.length; i++ )
			animatedValues[i] = ass[i].animatedValue;

		painter.paintAnimated( c, g, x, y, width, height, animatedValues );
	}

	private static boolean isAnimationEnabled( AnimatedPainter painter, Component c ) {
		return Animator.useAnimation() && painter.isAnimationEnabled() && c instanceof JComponent;
	}

	static void saveRepaintLocation( AnimatedPainter painter, Component c, int x, int y ) {
		if( !isAnimationEnabled( painter, c ) )
			return;

		AnimatedPainterSupport[] ass = (AnimatedPainterSupport[]) ((JComponent)c).getClientProperty( painter.getAnimationClientPropertyKey() );
		if( ass != null ) {
			for( int i = 0; i < ass.length; i++ ) {
				AnimatedPainterSupport as = ass[i];
				as.x = x;
				as.y = y;
			}
		}
	}
}
