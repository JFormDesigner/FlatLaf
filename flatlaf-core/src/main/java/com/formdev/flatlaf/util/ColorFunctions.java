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
		// convert RGB to HSL
		float[] hsl = HSLColor.fromRGB( color );
		float alpha = color.getAlpha() / 255f;
		float[] hsla = { hsl[0], hsl[1], hsl[2], alpha * 100 };

		// apply color functions
		for( ColorFunction function : functions )
			function.apply( hsla );

		// convert HSL to RGB
		return HSLColor.toRGB( hsla[0], hsla[1], hsla[2], hsla[3] / 100 );
	}

	public static float clamp( float value ) {
		return (value < 0)
			? 0
			: ((value > 100)
				? 100
				: value);
	}

	/**
	 * Returns a color that is a mixture of two colors.
	 * <p>
	 * This can be used to animate a color change from {@code color1} to {@code color2}
	 * by invoking this method multiple times with growing {@code weight} (from 0 to 1).
	 *
	 * @param color1 first color
	 * @param color2 second color
	 * @param weight the weight (in range 0-1) to mix the two colors.
	 *               Larger weight uses more of first color, smaller weight more of second color.
	 * @return mixture of colors
	 */
	public static Color mix( Color color1, Color color2, float weight ) {
		if( weight >= 1 )
			return color1;
		if( weight <= 0 )
			return color2;

		int r1 = color1.getRed();
		int g1 = color1.getGreen();
		int b1 = color1.getBlue();
		int a1 = color1.getAlpha();

		int r2 = color2.getRed();
		int g2 = color2.getGreen();
		int b2 = color2.getBlue();
		int a2 = color2.getAlpha();

		return new Color(
			Math.round( r2 + ((r1 - r2) * weight) ),
			Math.round( g2 + ((g1 - g2) * weight) ),
			Math.round( b2 + ((b1 - b2) * weight) ),
			Math.round( a2 + ((a1 - a2) * weight) ) );
	}

	/**
	 * Mix color with white, which makes the color brighter.
	 * This is the same as {@link #mix}{@code (Color.white, color, weight)}.
	 *
	 * @param color second color
	 * @param weight the weight (in range 0-1) to mix the two colors.
	 *               Larger weight uses more of first color, smaller weight more of second color.
	 * @return mixture of colors
	 * @since 1.6
	 */
	public static Color tint( Color color, float weight ) {
		return mix( Color.white, color, weight );
	}

	/**
	 * Mix color with black, which makes the color darker.
	 * This is the same as {@link #mix}{@code (Color.black, color, weight)}.
	 *
	 * @param color second color
	 * @param weight the weight (in range 0-1) to mix the two colors.
	 *               Larger weight uses more of first color, smaller weight more of second color.
	 * @return mixture of colors
	 * @since 1.6
	 */
	public static Color shade( Color color, float weight ) {
		return mix( Color.black, color, weight );
	}

	/**
	 * Calculates the luma (perceptual brightness) of the given color.
	 * <p>
	 * Uses SMPTE C / Rec. 709 coefficients, as recommended in
	 * <a href="https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef">WCAG 2.0</a>.
	 *
	 * @param color a color
	 * @return the luma (in range 0-1)
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Luma_(video)">https://en.wikipedia.org/wiki/Luma_(video)</a>
	 * @since 1.6
	 */
	public static float luma( Color color ) {
		// see https://en.wikipedia.org/wiki/Luma_(video)
		// see https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
		// see https://github.com/less/less.js/blob/master/packages/less/src/less/tree/color.js
		float r = gammaCorrection( color.getRed() / 255f );
		float g = gammaCorrection( color.getGreen() / 255f );
		float b = gammaCorrection( color.getBlue() / 255f );
		return (0.2126f * r) + (0.7152f * g) + (0.0722f * b);
	}

	private static float gammaCorrection( float value ) {
		return (value <= 0.03928f)
			? value / 12.92f
			: (float) Math.pow( (value + 0.055) / 1.055, 2.4 );
	}

	//---- interface ColorFunction --------------------------------------------

	public interface ColorFunction {
		void apply( float[] hsla );
	}

	//---- class HSLIncreaseDecrease ------------------------------------------

	/**
	 * Increase or decrease hue, saturation, luminance or alpha of a color in the HSL color space
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
		public void apply( float[] hsla ) {
			float amount2 = increase ? amount : -amount;

			if( hslIndex == 0 ) {
				// hue is range 0-360
				hsla[0] = (hsla[0] + amount2) % 360;
				return;
			}

			amount2 = autoInverse && shouldInverse( hsla ) ? -amount2 : amount2;
			hsla[hslIndex] = clamp( relative
				? (hsla[hslIndex] * ((100 + amount2) / 100))
				: (hsla[hslIndex] + amount2) );
		}

		protected boolean shouldInverse( float[] hsla ) {
			return increase
				? hsla[hslIndex] > 65
				: hsla[hslIndex] < 35;
		}

		@Override
		public String toString() {
			String name;
			switch( hslIndex ) {
				case 0: name = "spin"; break;
				case 1: name = increase ? "saturate" : "desaturate"; break;
				case 2: name = increase ? "lighten" : "darken"; break;
				case 3: name = increase ? "fadein" : "fadeout"; break;
				default: throw new IllegalArgumentException();
			}
			return String.format( "%s(%.0f%%%s%s)", name, amount,
				(relative ? " relative" : ""),
				(autoInverse ? " autoInverse" : "") );
		}
	}

	//---- class HSLChange ----------------------------------------------------

	/**
	 * Set the hue, saturation, luminance or alpha of a color.
	 *
	 * @since 1.6
	 */
	public static class HSLChange
		implements ColorFunction
	{
		public final int hslIndex;
		public final float value;

		public HSLChange( int hslIndex, float value ) {
			this.hslIndex = hslIndex;
			this.value = value;
		}

		@Override
		public void apply( float[] hsla ) {
			hsla[hslIndex] = (hslIndex == 0)
				? value % 360
				: clamp( value );
		}

		@Override
		public String toString() {
			String name;
			switch( hslIndex ) {
				case 0: name = "changeHue"; break;
				case 1: name = "changeSaturation"; break;
				case 2: name = "changeLightness"; break;
				case 3: name = "changeAlpha"; break;
				default: throw new IllegalArgumentException();
			}
			return String.format( "%s(%.0f%s)", name, value, (hslIndex == 0 ? "" : "%") );
		}
	}

	//---- class Fade ---------------------------------------------------------

	/**
	 * Set the alpha of a color.
	 */
	public static class Fade
		implements ColorFunction
	{
		public final float amount;

		public Fade( float amount ) {
			this.amount = amount;
		}

		@Override
		public void apply( float[] hsla ) {
			hsla[3] = clamp( amount );
		}

		@Override
		public String toString() {
			return String.format( "fade(%.0f%%)", amount );
		}
	}

	//---- class Mix ----------------------------------------------------------

	/**
	 * Mix two colors.
	 *
	 * @since 1.6
	 */
	public static class Mix
		implements ColorFunction
	{
		public final Color color2;
		public final float weight;

		public Mix( Color color2, float weight ) {
			this.color2 = color2;
			this.weight = weight;
		}

		@Override
		public void apply( float[] hsla ) {
			// convert from HSL to RGB because color mixing is done on RGB values
			Color color1 = HSLColor.toRGB( hsla[0], hsla[1], hsla[2], hsla[3] / 100 );

			// mix
			Color color = mix( color1, color2, weight / 100 );

			// convert RGB to HSL
			float[] hsl = HSLColor.fromRGB( color );
			System.arraycopy( hsl, 0, hsla, 0, hsl.length );
			hsla[3] = (color.getAlpha() / 255f) * 100;
		}

		@Override
		public String toString() {
			return String.format( "mix(#%08x,%.0f%%)", color2.getRGB(), weight );
		}
	}
}
