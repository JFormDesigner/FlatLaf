package com.formdev.flatlaf.ui;

import java.awt.image.RGBImageFilter;

/**
 * Used to create a disabled Icon with the ocean look.
 * <p>
 * Imported from MetalUtils.getOceanDisabledButtonIcon
 */
class FlatDisabledButtonImageFilter extends RGBImageFilter {
	private float min;
	private float factor;

	FlatDisabledButtonImageFilter( int min, int max ) {
		canFilterIndexColorModel = true;
		this.min = ( float ) min;
		this.factor = ( max - min ) / 255f;
	}

	public int filterRGB( int x, int y, int rgb ) {
		// Coefficients are from the sRGB color space:
		int gray = Math.min( 255, ( int ) ( ( ( 0.2125f * ( ( rgb >> 16 ) & 0xFF ) ) +
			( 0.7154f * ( ( rgb >> 8 ) & 0xFF ) ) +
			( 0.0721f * ( rgb & 0xFF )) + .5f ) * factor + min) );

		return ( rgb & 0xff000000 ) | ( gray << 16 ) | ( gray << 8 ) | ( gray );
	}
}
