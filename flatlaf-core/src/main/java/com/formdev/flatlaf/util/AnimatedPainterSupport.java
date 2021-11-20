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

/**
 * Animation support class that stores the animation state and implements the animation.
 *
 * @author Karl Tauber
 * @since 2
 */
class AnimatedPainterSupport
{
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

	static void paint( AnimatedPainter painter, Component c, Graphics g,
		int x, int y, int width, int height )
	{
		if( !isAnimationEnabled( painter, c ) ) {
			// paint without animation if animation is disabled or
			// component is not a JComponent and therefore does not support
			// client properties, which are required to keep animation state
			paintImpl( painter, c, g, x, y, width, height, null );
			return;
		}

		JComponent jc = (JComponent) c;
		Object key = painter.getClientPropertyKey();
		AnimatedPainterSupport as = (AnimatedPainterSupport) jc.getClientProperty( key );
		if( as == null ) {
			// painted first time --> do not animate, but remember current component value
			as = new AnimatedPainterSupport();
			as.startValue = as.targetValue = as.animatedValue = painter.getValue( c );
			jc.putClientProperty( key, as );
		} else {
			// get component value
			float value = painter.getValue( c );

			if( value != as.targetValue ) {
				// value changed --> (re)start animation

				if( as.animator == null ) {
					// create animator
					AnimatedPainterSupport as2 = as;
					as.animator = new Animator( painter.getAnimationDuration(), fraction -> {
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
					int duration2 = (int) (painter.getAnimationDuration() * as.fraction);
					if( duration2 > 0 )
						as.animator.setDuration( duration2 );
					as.startValue = as.animatedValue;
				} else {
					// new animation
					as.animator.setDuration( painter.getAnimationDuration() );
					as.animator.setResolution( painter.getAnimationResolution() );
					as.animator.setInterpolator( painter.getAnimationInterpolator() );

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

		paintImpl( painter, c, g, x, y, width, height, as );
	}

	private static void paintImpl( AnimatedPainter painter, Component c, Graphics g,
		int x, int y, int width, int height, AnimatedPainterSupport as )
	{
		float value = (as != null) ? as.animatedValue : painter.getValue( c );
		painter.paintAnimated( c, (Graphics2D) g, x, y, width, height, value );
	}

	private static boolean isAnimationEnabled( AnimatedPainter painter, Component c ) {
		return Animator.useAnimation() && painter.isAnimationEnabled() && c instanceof JComponent;
	}

	static void saveLocation( AnimatedPainter painter, Component c, int x, int y ) {
		if( !isAnimationEnabled( painter, c ) )
			return;

		AnimatedPainterSupport as = (AnimatedPainterSupport) ((JComponent)c).getClientProperty( painter.getClientPropertyKey() );
		if( as != null ) {
			as.x = x;
			as.y = y;
		}
	}
}
