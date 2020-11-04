// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.formdev.flatlaf.util;

import java.awt.image.RGBImageFilter;

// based on https://github.com/JetBrains/intellij-community/blob/3840eab54746f5c4f301bb3ac78f00a980b5fd6e/platform/util/ui/src/com/intellij/util/ui/UIUtil.java#L253-L347

/**
 * An image filter that turns an image into a grayscale image.
 * Used for icons in disabled buttons and labels.
 */
public class GrayFilter
	extends RGBImageFilter
{
	private final float brightness;
	private final float contrast;
	private final int alpha;

	private final int origContrast;
	private final int origBrightness;

	public static GrayFilter createDisabledIconFilter( boolean dark ) {
		return dark
			? new GrayFilter( -20, -70, 100 )
			: new GrayFilter(  25, -25, 100 );
	}

	/**
	 * @param brightness in range [-100..100] where 0 has no effect
	 * @param contrast in range [-100..100] where 0 has no effect
	 * @param alpha in range [0..100] where 0 is transparent, 100 has no effect
	 */
	public GrayFilter( int brightness, int contrast, int alpha ) {
		this.origBrightness = Math.max( -100, Math.min( 100, brightness ) );
		this.origContrast = Math.max( -100, Math.min( 100, contrast ) );
		this.alpha = Math.max( 0, Math.min( 100, alpha ) );

		this.brightness = (float) (Math.pow( origBrightness, 3 ) / (100f * 100f)); // cubic in [0..100]
		this.contrast = origContrast / 100f;

		canFilterIndexColorModel = true;
	}

	public GrayFilter() {
		this( 0, 0, 100 );
	}

	public int getBrightness() {
		return origBrightness;
	}

	public int getContrast() {
		return origContrast;
	}

	public int getAlpha() {
		return alpha;
	}

	@Override
	public int filterRGB( int x, int y, int rgb ) {
		// use NTSC conversion formula
		int gray = (int)(
			0.30 * (rgb >> 16 & 0xff) +
			0.59 * (rgb >> 8 & 0xff) +
			0.11 * (rgb & 0xff));

		if( brightness >= 0 )
			gray = (int) ((gray + brightness * 255) / (1 + brightness));
		else
			gray = (int) (gray / (1 - brightness));

		if( contrast >= 0 ) {
			if( gray >= 127 )
				gray = (int) (gray + (255 - gray) * contrast);
			else
				gray = (int) (gray - gray * contrast);
		} else
			gray = (int) (127 + (gray - 127) * (contrast + 1));

		int a = (alpha != 100)
			? (((rgb >> 24) & 0xff) * alpha / 100) << 24
			: (rgb & 0xff000000);

		return a | (gray << 16) | (gray << 8) | gray;
	}
}
