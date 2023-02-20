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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Scales the given image icon using the system and user scale factors and
 * paints the icon at system scale factor 1x. This gives best scaling quality.
 * If the given image icon supports multiple resolutions, the best resolution
 * variant is used. The last scaled image is cached for faster repainting.
 *
 * @author Karl Tauber
 */
public class ScaledImageIcon
	implements Icon
{
	private final ImageIcon imageIcon;
	private final int iconWidth;
	private final int iconHeight;

	private double lastSystemScaleFactor;
	private float lastUserScaleFactor;
	private Image lastImage;

	public ScaledImageIcon( ImageIcon imageIcon ) {
		this( imageIcon, imageIcon.getIconWidth(), imageIcon.getIconHeight() );
	}

	public ScaledImageIcon( ImageIcon imageIcon, int iconWidth, int iconHeight ) {
		this.imageIcon = imageIcon;
		this.iconWidth = iconWidth;
		this.iconHeight = iconHeight;
	}

	@Override
	public int getIconWidth() {
		return UIScale.scale( iconWidth );
	}

	@Override
	public int getIconHeight() {
		return UIScale.scale( iconHeight );
	}

	@Override
	public void paintIcon( Component c, Graphics g, int x, int y ) {
/*debug
		g.setColor( Color.red );
		g.drawRect( x, y, getIconWidth() - 1, getIconHeight() - 1 );
debug*/

		// scale factor
		double systemScaleFactor = UIScale.getSystemScaleFactor( (Graphics2D) g );
		float userScaleFactor = UIScale.getUserScaleFactor();
		double scaleFactor = systemScaleFactor * userScaleFactor;

		// paint input image icon if not necessary to scale
		if( scaleFactor == 1 && imageIcon != null && iconWidth == imageIcon.getIconWidth() && iconHeight == imageIcon.getIconHeight() ) {
			imageIcon.paintIcon( c, g, x, y );
			return;
		}

		// paint cached scaled icon
		if( systemScaleFactor == lastSystemScaleFactor &&
			userScaleFactor == lastUserScaleFactor &&
			lastImage != null )
		{
			paintLastImage( g, x, y );
			return;
		}

		// destination image size
		int destImageWidth = (int) Math.round( iconWidth * scaleFactor );
		int destImageHeight = (int) Math.round( iconHeight * scaleFactor );

		// get resolution variant of image if it is a multi-resolution image
		Image image = getResolutionVariant( destImageWidth, destImageHeight );

		// size of image
		int imageWidth = -1;
		int imageHeight = -1;

		if (image != null) {
			imageWidth = image.getWidth( null );
			imageHeight = image.getHeight( null );
		}

		// paint red rectangle if image has invalid size (e.g. not found)
		if( imageWidth < 0 || imageHeight < 0 ) {
			g.setColor( Color.red );
			g.fillRect( x, y, getIconWidth(), getIconHeight() );
			return;
		}

		// scale image if necessary to destination size
		if( imageWidth != destImageWidth || imageHeight != destImageHeight ) {
			// determine scaling method; default is "quality"
			Object scalingInterpolation = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
			float imageScaleFactor = (float) destImageWidth / (float) imageWidth;
			if( ((int) imageScaleFactor) == imageScaleFactor &&
				imageScaleFactor > 1f &&
				imageWidth <= 16 &&
				imageHeight <= 16 )
			{
				// use "speed" scaling for small icons if the scale factor is an integer
				// to avoid blurred icons
				scalingInterpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
			}

			// scale image
			BufferedImage bufferedImage = image2bufferedImage( image );
			image = scaleImage( bufferedImage, destImageWidth, destImageHeight, scalingInterpolation );
		}

		// cache image
		lastSystemScaleFactor = systemScaleFactor;
		lastUserScaleFactor = userScaleFactor;
		lastImage = image;

		// paint image
		paintLastImage( g, x, y );
	}

	protected Image getResolutionVariant( int destImageWidth, int destImageHeight ) {
		return MultiResolutionImageSupport.getResolutionVariant(
			imageIcon.getImage(), destImageWidth, destImageHeight );
	}

	private void paintLastImage( Graphics g, int x, int y ) {
		if( lastSystemScaleFactor > 1 ) {
			HiDPIUtils.paintAtScale1x( (Graphics2D) g, x, y, 100, 100, // width and height are not used
				(g2, x2, y2, width2, height2, scaleFactor2) -> {
					g2.drawImage( lastImage, x2, y2, null );
				} );
		} else
			g.drawImage( lastImage, x, y, null );
	}

	/**
	 * Scales the given image to the target dimensions.
	 *
	 * This is the same what imgscalr library (https://github.com/rkalla/imgscalr)
	 * would do when invoking Scalr.resize().
	 */
	private BufferedImage scaleImage( BufferedImage image, int targetWidth, int targetHeight,
		Object scalingInterpolation )
	{
		BufferedImage bufferedImage = new BufferedImage( targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = bufferedImage.createGraphics();
		try {
			g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, scalingInterpolation );
			g.drawImage( image, 0, 0, targetWidth, targetHeight, null );
		} finally {
			g.dispose();
		}
		return bufferedImage;

	}

	private BufferedImage image2bufferedImage( Image image ) {
		if( image instanceof BufferedImage )
			return (BufferedImage) image;

		BufferedImage bufferedImage = new BufferedImage( image.getWidth( null ),
			image.getHeight( null ), BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = bufferedImage.createGraphics();
		try {
			g.drawImage( image, 0, 0, null );
		} finally {
			g.dispose();
		}
		return bufferedImage;
	}
}
