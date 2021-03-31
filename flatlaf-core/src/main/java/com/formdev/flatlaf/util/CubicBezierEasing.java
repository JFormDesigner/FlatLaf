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

/**
 * An interpolator for {@link Animator} that uses a cubic bezier curve.
 *
 * @author Karl Tauber
 */
public class CubicBezierEasing
	implements Animator.Interpolator
{
	/**
	 * Standard easing as specified in Material design (0.4, 0, 0.2, 1).
	 *
	 * @see <a href="https://material.io/design/motion/speed.html#easing">https://material.io/design/motion/speed.html#easing</a>
	 */
	public static final CubicBezierEasing STANDARD_EASING = new CubicBezierEasing( 0.4f, 0f, 0.2f, 1f );

	// common cubic-bezier easing functions (same as in CSS)
	// https://developer.mozilla.org/en-US/docs/Web/CSS/easing-function
	public static final CubicBezierEasing EASE = new CubicBezierEasing( 0.25f, 0.1f, 0.25f, 1f );
	public static final CubicBezierEasing EASE_IN = new CubicBezierEasing( 0.42f, 0f, 1f, 1f );
	public static final CubicBezierEasing EASE_IN_OUT = new CubicBezierEasing( 0.42f, 0f, 0.58f, 1f );
	public static final CubicBezierEasing EASE_OUT = new CubicBezierEasing( 0f, 0f, 0.58f, 1f );

	private final float x1;
	private final float y1;
	private final float x2;
	private final float y2;

	/**
	 * Creates a cubic bezier easing interpolator with the given control points.
	 * The start point of the cubic bezier curve is always 0,0 and the end point 1,1.
	 *
	 * @param x1 the x coordinate of the first control point in range [0, 1]
	 * @param y1 the y coordinate of the first control point in range [0, 1]
	 * @param x2 the x coordinate of the second control point in range [0, 1]
	 * @param y2 the y coordinate of the second control point in range [0, 1]
	 */
	public CubicBezierEasing( float x1, float y1, float x2, float y2 ) {
		if( x1 < 0 || x1 > 1 || y1 < 0 || y1 > 1 ||
			x2 < 0 || x2 > 1 || y2 < 0 || y2 > 1 )
		  throw new IllegalArgumentException( "control points must be in range [0, 1]");

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public float interpolate( float fraction ) {
		if( fraction <= 0 || fraction >= 1 )
			return fraction;

		// use binary search
		float low = 0;
		float high = 1;
		while( true ) {
			float mid = (low + high) / 2;
			float estimate = cubicBezier( mid, x1, x2 );
			if( Math.abs( fraction - estimate ) < 0.0005f )
				return cubicBezier( mid, y1, y2 );
			if( estimate < fraction )
				low = mid;
			else
				high = mid;
		}
	}

	/**
	 * Computes the x or y point on a cubic bezier curve for a given t value.
	 *
	 * https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Cubic_B%C3%A9zier_curves
	 *
	 * The general cubic bezier formula is:
	 *   x = b0*x0 + b1*x1 + b2*x2 + b3*x3
	 *   y = b0*y0 + b1*y1 + b2*y2 + b3*y3
	 *
	 * where:
	 *   b0 = (1-t)^3
	 *   b1 = 3 * t * (1-t)^2
	 *   b2 = 3 * t^2 * (1-t)
	 *   b3 = t^3
	 *
	 *  x0,y0 is always 0,0 and x3,y3 is 1,1, so we can simplify to:
	 *   x = b1*x1 + b2*x2 + b3
	 *   y = b1*x1 + b2*x2 + b3
	 */
	private static float cubicBezier( float t, float xy1, float xy2 ) {
		float invT = (1 - t);
		float b1 = 3 * t * (invT * invT);
		float b2 = 3 * (t * t) * invT;
		float b3 = t * t * t;
		return (b1 * xy1) + (b2 * xy2) + b3;
	}
}
