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

import java.awt.Color;

/**
 * Functions that modify colors.
 *
 * @author Karl Tauber
 */
public class ColorFunctions
{
	public static Color applyFunctions( Color color, ColorFunction... functions ) {
		float[] hsl = HSLColor.fromRGB( color );
		float alpha = color.getAlpha() / 255f;

		for( ColorFunction function : functions )
			function.apply( hsl );

		return HSLColor.toRGB( hsl, alpha );
	}

	public static float clamp( float value ) {
		return (value < 0)
			? 0
			: ((value > 100)
				? 100
				: value);
	}

	//---- interface ColorFunction --------------------------------------------

	public interface ColorFunction {
		void apply( float[] hsl );
	}

	//---- class HSLIncreaseDecrease ------------------------------------------

	/**
	 * Increase or decrease hue, saturation or luminance of a color in the HSL color space
	 * by an absolute or relative amount.
	 */
	public static class HSLIncreaseDecrease
		implements ColorFunction
	{
		public final int hslIndex;
		public final boolean increase;
		public final float amount;
		public final boolean relative;
		public final boolean autoInverse;

		public HSLIncreaseDecrease( int hslIndex, boolean increase,
			float amount, boolean relative, boolean autoInverse )
		{
			this.hslIndex = hslIndex;
			this.increase = increase;
			this.amount = amount;
			this.relative = relative;
			this.autoInverse = autoInverse;
		}

		@Override
		public void apply( float[] hsl ) {
			float amount2 = increase ? amount : -amount;
			amount2 = autoInverse && shouldInverse( hsl ) ? -amount2 : amount2;
			hsl[hslIndex] = clamp( relative
				? (hsl[hslIndex] * ((100 + amount2) / 100))
				: (hsl[hslIndex] + amount2) );
		}

		protected boolean shouldInverse( float[] hsl ) {
			return increase
				? hsl[hslIndex] >= 50
				: hsl[hslIndex] < 50;
		}
	}
}
