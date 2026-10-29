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
	/**
	 * sRGB color space.
	 *
	 * @since 3.8
	 */
	public static final int RGB = 0;

	/**
	 * Linear-light sRGB color space.
	 *
	 * @since 3.8
	 */
	public static final int LRGB = 1;

	/**
	 * <a href="https://bottosson.github.io/posts/oklab/">Oklab</a> color space.
	 *
	 * @since 3.8
	 */
	public static final int OKLAB = 2;

	/**
	 * Increase the lightness of a color in HSL color space by an absolute amount.
	 * <p>
	 * Consider using {@link #tint(Color, float)} as alternative.
	 *
	 * @param color base color
	 * @param amount the amount (in range 0-1) that is added to the lightness
	 * @return new color
	 * @since 2
	 */
	public static Color lighten( Color color, float amount ) {
		return hslIncreaseDecrease( color, amount, 2, true );
	}

	/**
	 * Decrease the lightness of a color in HSL color space by an absolute amount.
	 * <p>
	 * Consider using {@link #shade(Color, float)} as alternative.
	 *
	 * @param color base color
	 * @param amount the amount (in range 0-1) that is subtracted from the lightness
	 * @return new color
	 * @since 2
	 */
	public static Color darken( Color color, float amount ) {
		return hslIncreaseDecrease( color, amount, 2, false );
	}

	/**
	 * Increase the saturation of a color in HSL color space by an absolute amount.
	 *
	 * @param color base color
	 * @param amount the amount (in range 0-1) that is added to the saturation
	 * @return new color
	 * @since 2
	 */
	public static Color saturate( Color color, float amount ) {
		return hslIncreaseDecrease( color, amount, 1, true );
	}

	/**
	 * Decrease the saturation of a color in HSL color space by an absolute amount.
	 *
	 * @param color base color
	 * @param amount the amount (in range 0-1) that is subtracted from the saturation
	 * @return new color
	 * @since 2
	 */
	public static Color desaturate( Color color, float amount ) {
		return hslIncreaseDecrease( color, amount, 1, false );
	}

	/**
	 * Rotate the hue angle (0-360) of a color in HSL color space in either direction.
	 *
	 * @param color base color
	 * @param angle the number of degrees to rotate (in range -360 - 360)
	 * @return new color
	 * @since 2
	 */
	public static Color spin( Color color, float angle ) {
		return hslIncreaseDecrease( color, angle, 0, true );
	}

	private static Color hslIncreaseDecrease( Color color, float amount, int hslIndex, boolean increase ) {
		// convert RGB to HSL
		float[] hsl = HSLColor.fromRGB( color );
		float alpha = color.getAlpha() / 255f;

		// apply HSL color change
		float amount2 = increase ? amount : -amount;
		if( hslIndex == 0 )
			hsl[0] = (hsl[0] + amount2) % 360;
		else
			hsl[hslIndex] = clamp( hsl[hslIndex] + (amount2 * 100) );

		// convert HSL to RGB
		return HSLColor.toRGB( hsl[0], hsl[1], hsl[2], alpha );
	}

	/**
	 * Set the opacity (alpha) of a color.
	 *
	 * @param color base color
	 * @param amount the amount (in range 0-1) of the new opacity
	 * @return new color
	 * @since 3
	 */
	public static Color fade( Color color, float amount ) {
		int newAlpha = Math.round( 255 * amount );
		return new Color( (color.getRGB() & 0xffffff) | (newAlpha << 24), true );
	}

	/**
	 * Returns a color that is a mixture of two colors.
	 * Uses {@link #RGB} interpolation method.
	 * For better results use {@link #OKLAB} or {@link #LRGB} and {@link #mix(Color, Color, float, int)}.
	 * <p>
	 * This can be used to animate a color change from {@code color1} to {@code color2}
	 * by invoking this method multiple times with decreasing {@code weight} (from 1 to 0).
	 *
	 * @param color1 first color
	 * @param color2 second color
	 * @param weight the weight of first color (in range 0-1), used to mix the two colors.
	 *               Weight of second color is {@code 1-weight}.
	 *               Larger weight uses more of first color, smaller weight more of second color.
	 * @return mixture of colors
	 */
	public static Color mix( Color color1, Color color2, float weight ) {
		return mix( color1, color2, weight, RGB );
	}

	/**
	 * Returns a color that is a mixture of two colors, using give interpolation method.
	 * For best results use {@link #OKLAB} or {@link #LRGB}.
	 * <p>
	 * This can be used to animate a color change from {@code color1} to {@code color2}
	 * by invoking this method multiple times with decreasing {@code weight} (from 1 to 0).
	 *
	 * @param color1 first color
	 * @param color2 second color
	 * @param weight the weight of first color (in range 0-1), used to mix the two colors.
	 *               Weight of second color is {@code 1-weight}.
	 *               Larger weight uses more of first color, smaller weight more of second color.
	 * @param method interpolation method used to mix the colors:
	 *               {@link #RGB}, {@link #LRGB} or {@link #OKLAB}
	 * @return mixture of colors
	 * @since 3.8
	 */
	public static Color mix( Color color1, Color color2, float weight, int method ) {
		if( weight >= 1 )
			return color1;
		if( weight <= 0 )
			return color2;
		if( color1.equals( color2 ) )
			return color1;

		switch( method ) {
			case RGB:			return mixRGB( color1, color2, weight );
			case LRGB:			return mixLinearRGB( color1, color2, weight );
			case OKLAB:			return mixOklab( color1, color2, weight );
			default:			throw new IllegalArgumentException();
		}
	}

	private static Color mixRGB( Color color1, Color color2, float weight ) {
		return new Color(
			Math.round( lerp( color1.getRed(),   color2.getRed(),   weight ) ),
			Math.round( lerp( color1.getGreen(), color2.getGreen(), weight ) ),
			Math.round( lerp( color1.getBlue(),  color2.getBlue(),  weight ) ),
			Math.round( lerp( color1.getAlpha(), color2.getAlpha(), weight ) ) );
	}

	private static Color mixLinearRGB( Color color1, Color color2, float weight ) {
		float[] lrgb1 = toLinearRGB( color1 );
		float[] lrgb2 = toLinearRGB( color2 );

		return fromLinearRGB( new float[] {
			lerp( lrgb1[0], lrgb2[0], weight ),
			lerp( lrgb1[1], lrgb2[1], weight ),
			lerp( lrgb1[2], lrgb2[2], weight ),
			lerp( lrgb1[3], lrgb2[3], weight ),
		} );
	}

	private static Color mixOklab( Color color1, Color color2, float weight ) {
		float[] oklab1 = toOklab( color1 );
		float[] oklab2 = toOklab( color2 );

		return fromOklab( new float[] {
			lerp( oklab1[0], oklab2[0], weight ),
			lerp( oklab1[1], oklab2[1], weight ),
			lerp( oklab1[2], oklab2[2], weight ),
			lerp( oklab1[3], oklab2[3], weight ),
		} );
	}

	private static float lerp( float value1, float value2, float weight ) {
		return value2 + ((value1 - value2) * weight);
	}

	/**
	 * Mix color with white, which makes the color lighter.
	 * This is the same as {@link #mix}{@code (Color.white, color, weight)}.
	 * Uses {@link #RGB} interpolation method.
	 * For better results use {@link #OKLAB} or {@link #LRGB} and {@link #tint(Color, float, int)}.
	 *
	 * @param color color to mix with white
	 * @param weight the weight of white (in range 0-1), used to mix the two colors.
	 *               Weight of given color is {@code 1-weight}.
	 *               Larger weight uses more of white, smaller weight more of given color.
	 * @return mixture of colors
	 * @since 2
	 */
	public static Color tint( Color color, float weight ) {
		return mix( Color.white, color, weight );
	}

	/**
	 * Mix color with white, which makes the color lighter.
	 * This is the same as {@link #mix}{@code (Color.white, color, weight)}.
	 * For best results use {@link #OKLAB} or {@link #LRGB}.
	 *
	 * @param color color to mix with white
	 * @param weight the weight of white (in range 0-1), used to mix the two colors.
	 *               Weight of given color is {@code 1-weight}.
	 *               Larger weight uses more of white, smaller weight more of given color.
	 * @param method interpolation method used to mix the colors:
	 *               {@link #RGB}, {@link #LRGB} or {@link #OKLAB}
	 * @return mixture of colors
	 * @since 3.8
	 */
	public static Color tint( Color color, float weight, int method ) {
		return mix( Color.white, color, weight, method );
	}

	/**
	 * Mix color with black, which makes the color darker.
	 * This is the same as {@link #mix}{@code (Color.black, color, weight)}.
	 * Uses {@link #RGB} interpolation method.
	 * For better results use {@link #OKLAB} or {@link #LRGB} and {@link #shade(Color, float, int)}.
	 *
	 * @param color color to mix with black
	 * @param weight the weight of black (in range 0-1), used to mix the two colors.
	 *               Weight of given color is {@code 1-weight}.
	 *               Larger weight uses more of black, smaller weight more of given color.
	 * @return mixture of colors
	 * @since 2
	 */
	public static Color shade( Color color, float weight ) {
		return mix( Color.black, color, weight );
	}

	/**
	 * Mix color with black, which makes the color darker.
	 * This is the same as {@link #mix}{@code (Color.black, color, weight)}.
	 * For best results use {@link #OKLAB} or {@link #LRGB}.
	 *
	 * @param color color to mix with black
	 * @param weight the weight of black (in range 0-1), used to mix the two colors.
	 *               Weight of given color is {@code 1-weight}.
	 *               Larger weight uses more of black, smaller weight more of given color.
	 * @param method interpolation method used to mix the colors:
	 *               {@link #RGB}, {@link #LRGB} or {@link #OKLAB}
	 * @return mixture of colors
	 * @since 3.8
	 */
	public static Color shade( Color color, float weight, int method ) {
		return mix( Color.black, color, weight, method );
	}

	/**
	 * Calculates the relative luminance of the given color, as defined in
	 * <a href="https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef">WCAG 2.0</a>
	 * (sRGB components are linearized, then weighted with Rec. 709 coefficients).
	 * <p>
	 * Note: Despite the method name, this is not luma (which is computed from
	 * gamma-encoded components) and is not perceptually uniform
	 * (e.g. middle gray {@code #777777} has a luminance of about 0.18).
	 * The alpha component is ignored.
	 *
	 * @param color a color
	 * @return the relative luminance (in range 0-1)
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Relative_luminance">https://en.wikipedia.org/wiki/Relative_luminance</a>
	 * @since 2
	 */
	public static float luma( Color color ) {
		// see https://en.wikipedia.org/wiki/Relative_luminance
		// see https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
		// see https://github.com/less/less.js/blob/master/packages/less/lib/less/tree/color.js
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

	/**
	 * Returns whether the given color is dark.
	 * Uses {@link #luma(Color)} with a threshold of 43%
	 * (same as default threshold of color function {@code contrast()} in properties files).
	 *
	 * @param color a color
	 * @return {@code true} if the color is dark
	 * @since 3.8
	 */
	public static boolean isDark( Color color ) {
		return luma( color ) < 0.43f;
	}

	/**
	 * Applies the given color functions to the given color and returns the new color.
	 */
	public static Color applyFunctions( Color color, ColorFunction... functions ) {
		// if having only a single function of type Mix, then avoid four unnecessary conversions:
		//     1. RGB to HSL in this method
		//     2. HSL to RGB in Mix.apply()
		//        mix
		//     3. RGB to HSL in Mix.apply()
		//     4. HSL to RGB in this method
		if( functions.length == 1 && functions[0] instanceof Mix ) {
			Mix mixFunction = (Mix) functions[0];
			return mix( color, mixFunction.color2, mixFunction.weight / 100 );
		} else if( functions.length == 1 && functions[0] instanceof Mix2 ) {
			Mix2 mixFunction = (Mix2) functions[0];
			return mix( mixFunction.color1, color, mixFunction.weight / 100, mixFunction.method );
		}

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

	/**
	 * Clamps the given value between 0 and 100.
	 */
	public static float clamp( float value ) {
		return (value < 0) ? 0 : ((value > 100) ? 100 : value);
	}

	/**
	 * Clamps the given value between 0 and 1.
	 */
	private static float clamp1( float value ) {
		return (value < 0) ? 0 : ((value > 1) ? 1 : value);
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
	 * Mix two colors using {@link ColorFunctions#mix(Color, Color, float)}.
	 * First color is passed to {@link #apply(float[])}.
	 * Second color is {@link #color2}.
	 * <p>
	 * Use {@link Mix2} to tint or shade color.
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

	//---- class Mix2 ---------------------------------------------------------

	/**
	 * Mix two colors using {@link ColorFunctions#mix(Color, Color, float)}.
	 * First color is {@link #color1}.
	 * Second color is passed to {@link #apply(float[])}.
	 *
	 * @since 3.6
	 */
	public static class Mix2
		implements ColorFunction
	{
		public final Color color1;
		public final float weight;
		/** @since 3.8 */ public final int method;

		public Mix2( Color color1, float weight ) {
			this( color1, weight, RGB );
		}

		/** @since 3.8 */
		public Mix2( Color color1, float weight, int method ) {
			this.color1 = color1;
			this.weight = weight;
			this.method = method;
		}

		@Override
		public void apply( float[] hsla ) {
			// convert from HSL to RGB because color mixing is done on RGB values
			Color color2 = HSLColor.toRGB( hsla[0], hsla[1], hsla[2], hsla[3] / 100 );

			// mix
			Color color = mix( color1, color2, weight / 100, method );

			// convert RGB to HSL
			float[] hsl = HSLColor.fromRGB( color );
			System.arraycopy( hsl, 0, hsla, 0, hsl.length );
			hsla[3] = (color.getAlpha() / 255f) * 100;
		}

		@Override
		public String toString() {
			return String.format( "mix2(#%08x,%.0f%%)", color1.getRGB(), weight );
		}
	}

	//---- linear RGB ---------------------------------------------------------

	static float[] toLinearRGB( Color color ) {
		float[] rgb = color.getRGBComponents( null );
		return new float[] {
			toLinearRGB( rgb[0] ),
			toLinearRGB( rgb[1] ),
			toLinearRGB( rgb[2] ),
			rgb[3],
		};
	}

	static Color fromLinearRGB( float[] lrgb ) {
		return new Color(
			clamp1( fromLinearRGB( lrgb[0] ) ),
			clamp1( fromLinearRGB( lrgb[1] ) ),
			clamp1( fromLinearRGB( lrgb[2] ) ),
			lrgb[3] );
	}

	// source https://bottosson.github.io/posts/colorwrong/#what-can-we-do%3F

	private static float fromLinearRGB( float x ) {
		if( x >= 0.0031308f )
			return (float) (1.055 * Math.pow( x, 1.0 / 2.4 ) - 0.055f);
		else
			return 12.92f * x;
	}

	private static float toLinearRGB( float x ) {
		if( x >= 0.04045f )
			return (float) Math.pow( (x + 0.055) / (1 + 0.055), 2.4 );
		else
			return x / 12.92f;
	}

	//---- Oklab --------------------------------------------------------------

	// source https://bottosson.github.io/posts/oklab/

	@SuppressWarnings( "FloatingPointLiteralPrecision" ) // Error Prone
	static float[] toOklab( Color color ) {
		float[] lrgb = toLinearRGB( color );
		float r = lrgb[0];
		float g = lrgb[1];
		float b = lrgb[2];

		float l = 0.4122214708f * r + 0.5363325363f * g + 0.0514459929f * b;
		float m = 0.2119034982f * r + 0.6806995451f * g + 0.1073969566f * b;
		float s = 0.0883024619f * r + 0.2817188376f * g + 0.6299787005f * b;

		float l_ = (float) Math.cbrt( l );
		float m_ = (float) Math.cbrt( m );
		float s_ = (float) Math.cbrt( s );

		return new float[] {
			0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_,
			1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_,
			0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_,
			lrgb[3]
		};
	}

	@SuppressWarnings( "FloatingPointLiteralPrecision" ) // Error Prone
	static Color fromOklab( float[] oklab ) {
		float L = oklab[0];
		float a = oklab[1];
		float b = oklab[2];

		float l_ = L + 0.3963377774f * a + 0.2158037573f * b;
		float m_ = L - 0.1055613458f * a - 0.0638541728f * b;
		float s_ = L - 0.0894841775f * a - 1.2914855480f * b;

		float l = l_*l_*l_;
		float m = m_*m_*m_;
		float s = s_*s_*s_;

		return fromLinearRGB( new float[] {
			+4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s,
			-1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s,
			-0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s,
			oklab[3]
		} );
	}
}
