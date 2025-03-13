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
import javax.swing.border.Border;

/**
 * Border that automatically animates painting on component value changes.
 * <p>
 * {@link #getAnimatableValues(Component)} returns the animatable value(s) of the component.
 * If the value(s) have changed, then {@link #paintAnimated(Component, Graphics2D, int, int, int, int, float[])}
 * is invoked multiple times with animated value(s) (from old value(s) to new value(s)).
 * If {@link #getAnimatableValues(Component)} returns multiple values, then each value
 * gets its own independent animation, which may start/end at different points in time,
 * may have different duration, resolution and interpolator.
 * <p>
 * Example for an animated border:
 * <pre>
 * private class MyAnimatedBorder
 *     implements AnimatedBorder
 * {
 *     &#64;Override
 *     public float[] getAnimatableValues( Component c ) {
 *         return new float[] { c.isFocusOwner() ? 1 : 0 };
 *     }
 *
 *     &#64;Override
 *     public void paintAnimated( Component c, Graphics2D g, int x, int y, int width, int height, float[] animatedValues ) {
 *         int lh = UIScale.scale( 2 );
 *
 *         g.setColor( Color.blue );
 *         g.fillRect( x, y + height - lh, Math.round( width * animatedValues[0] ), lh );
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
 * textField.setBorder( new MyAnimatedBorder() );
 * </pre>
 *
 * Animation works only if the component passed to {@link #paintBorder(Component, Graphics, int, int, int, int)}
 * is a instance of {@link JComponent}.
 * A client property is set on the component to store the animation state.
 *
 * @author Karl Tauber
 * @since 2
 */
public interface AnimatedBorder
	extends Border, AnimatedPainter
{
	/**
	 * Invokes {@link #paintWithAnimation(Component, Graphics, int, int, int, int)}.
	 */
	@Override
	default void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		paintWithAnimation( c, g, x, y, width, height );
	}
}
