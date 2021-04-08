package com.formdev.flatlaf.extras;

import java.awt.*;
import java.awt.image.RGBImageFilter;

/**
 * A simplified RGBImageFilter that presents individual rgba components as a Color object.
 * Can be used to modify the color of a {@link FlatSVGIcon}-
 */
public abstract class FlatRGBFilter extends RGBImageFilter
{
	@Override
	public int filterRGB(int x, int y, int rgb) {
		return filterRGB(new Color(rgb)).getRGB();
	}

	/**
	 * @param c Original color
	 * @return Modified color
	 */
	public abstract Color filterRGB(Color c);
}

