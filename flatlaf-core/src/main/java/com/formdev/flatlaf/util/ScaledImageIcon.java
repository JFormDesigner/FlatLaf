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
import java.awt.image.ImageObserver;
import java.lang.ref.WeakReference;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Scales the given image icon using the system and user scale factors and
 * paints the icon at system scale factor 1x. This gives best scaling quality.
 * If the given image icon supports multiple resolutions, the best resolution
 * variant is used. The last scaled image is cached for faster repainting.
 * <p>
 * Animated images (e.g. animated GIFs) are supported. On each new frame,
 * the component is repainted. Animated images are not cached, but painted scaled directly.
 * Note that only the component that most recently painted the icon is repainted.
 * If an icon with an animated image is used in multiple components, then only one of
 * them is animated. Use separate {@code ScaledImageIcon} instances in this case.
 * <p>
 * If an image observer is set on the image icon (see {@link ImageIcon#setImageObserver(java.awt.image.ImageObserver)}),
 * then the notifications are forwarded to that observer instead of repainting the component.
 * This is necessary for animation in cell renderers (e.g. in lists, tables or trees),
 * where the observer should repaint the cell.
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
	private volatile Image lastImage;

	// for animated images
	private WeakReference<Component> lastComponent;
	private ImageObserver frameObserver;
	private volatile boolean animated;

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

		// remember component, which is repainted on new frames of animated images
		if( c != null && (lastComponent == null || lastComponent.get() != c) )
			lastComponent = new WeakReference<>( c );

		// paint input image icon if not necessary to scale
		if( scaleFactor == 1 && imageIcon != null && iconWidth == imageIcon.getIconWidth() && iconHeight == imageIcon.getIconHeight() ) {
			// Paint image directly instead of using imageIcon.paintIcon(c,...):
			// - consistent with scaled case, where imageIcon.paintIcon() is also not used
			// - ImageIcon.paintIcon(c,...) uses the component as image observer,
			//   but JLabel.imageUpdate() and AbstractButton.imageUpdate() ignore images
			//   that are not from an ImageIcon set as icon of the component
			//   (it is this ScaledImageIcon) --> animated images (e.g. GIFs) would not be painted
			g.drawImage( imageIcon.getImage(), x, y, getFrameObserver() );
			return;
		}

		// paint cached scaled icon
		// (use local variable because lastImage may be reset from image animator thread)
		Image cachedImage = lastImage;
		if( !animated &&
			systemScaleFactor == lastSystemScaleFactor &&
			userScaleFactor == lastUserScaleFactor &&
			cachedImage != null )
		{
			paintImage( g, cachedImage, systemScaleFactor, x, y, -1, -1 );
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

		// paint animated images (e.g. animated GIFs) directly scaled, without caching,
		// to avoid creating a new buffered image for each frame
		// (animated is set in image observer when it receives first FRAMEBITS notification;
		// so the first frame is painted and cached below, but all other frames are painted here)
		if( animated ) {
			lastImage = null;

			Graphics2D g2 = (Graphics2D) g.create();
			try {
				g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, scalingInterpolation );
				paintImage( g2, image, systemScaleFactor, x, y, destImageWidth, destImageHeight );
			} finally {
				g2.dispose();
			}
			return;
		}

		// scale image if necessary to destination size
		if( imageWidth != destImageWidth || imageHeight != destImageHeight )
			image = scaleImage( image, destImageWidth, destImageHeight, scalingInterpolation );

		// cache image
		lastSystemScaleFactor = systemScaleFactor;
		lastUserScaleFactor = userScaleFactor;
		lastImage = image;

		// paint image
		paintImage( g, image, systemScaleFactor, x, y, -1, -1 );
	}

	protected Image getResolutionVariant( int destImageWidth, int destImageHeight ) {
		return MultiResolutionImageSupport.getResolutionVariant(
			imageIcon.getImage(), destImageWidth, destImageHeight );
	}

	private void paintImage( Graphics g, Image image, double systemScaleFactor,
		int x, int y, int destImageWidth, int destImageHeight )
	{
		// image observer is necessary if image was not scaled and is animated
		ImageObserver observer = (image instanceof BufferedImage) ? null : getFrameObserver();

		if( systemScaleFactor > 1 ) {
			HiDPIUtils.paintAtScale1x( (Graphics2D) g, x, y, 100, 100, // width and height are not used
				(g2, x2, y2, width2, height2, scaleFactor2) -> {
					drawImage( g2, image, x2, y2, destImageWidth, destImageHeight, observer );
				} );
		} else
			drawImage( g, image, x, y, destImageWidth, destImageHeight, observer );
	}

	private void drawImage( Graphics g, Image image, int x, int y, int width, int height, ImageObserver observer ) {
		if( width < 0 || height < 0 )
			g.drawImage( image, x, y, observer );
		else
			g.drawImage( image, x, y, width, height, observer );
	}

	/**
	 * Scales the given image to the target dimensions.
	 *
	 * This is the same what imgscalr library (https://github.com/rkalla/imgscalr)
	 * would do when invoking Scalr.resize().
	 */
	private BufferedImage scaleImage( Image image, int targetWidth, int targetHeight,
		Object scalingInterpolation )
	{
		// image observer is necessary to get notified about new frames of animated images
		ImageObserver observer = (image instanceof BufferedImage) ? null : getFrameObserver();

		BufferedImage bufferedImage = new BufferedImage( targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = bufferedImage.createGraphics();
		try {
			g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, scalingInterpolation );
			g.drawImage( image, 0, 0, targetWidth, targetHeight, observer );
		} finally {
			g.dispose();
		}
		return bufferedImage;
	}

	/**
	 * Returns an image observer that is notified by AWT when a new frame of
	 * an animated image (e.g. animated GIF) is available.
	 * It discards the cached scaled image and forwards the notification to the
	 * image observer of the image icon (if set), or repaints the component.
	 */
	private ImageObserver getFrameObserver() {
		if( frameObserver == null ) {
			frameObserver = (img, infoflags, x, y, width, height) -> {
				// FRAMEBITS is only sent for images with multiple frames (e.g. animated GIFs);
				// static images send ALLBITS (if not yet fully loaded)
				if( (infoflags & ImageObserver.FRAMEBITS) != 0 )
					animated = true;

				boolean newFrame = (infoflags & (ImageObserver.FRAMEBITS | ImageObserver.ALLBITS)) != 0;
				if( newFrame )
					lastImage = null;

				// forward to image observer of image icon (e.g. used in cell renderers)
				ImageObserver observer = (imageIcon != null) ? imageIcon.getImageObserver() : null;
				if( observer != null )
					return observer.imageUpdate( img, infoflags, x, y, width, height );

				Component c = (lastComponent != null) ? lastComponent.get() : null;
				// stop receiving notifications if component was garbage collected or is not showing
				// (same as in JLabel.imageUpdate(); stops the animation of the image to save resources;
				// when component is painted again, then this observer is registered again)
				if( c == null || !c.isShowing() )
					return false;

				if( newFrame )
					c.repaint();

				// same as in Component.imageUpdate()
				return (infoflags & (ImageObserver.ALLBITS | ImageObserver.ABORT)) == 0;
			};
		}
		return frameObserver;
	}
}
