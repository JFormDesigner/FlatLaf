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

package com.formdev.flatlaf.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import com.formdev.flatlaf.util.MultiResolutionImageSupport;
import com.formdev.flatlaf.util.ScaledImageIcon;

/**
 * @author Karl Tauber
 */
public class FlatTitlePaneIcon
	extends ScaledImageIcon
{
	public static Icon create( List<Image> images, Dimension size ) {
		// collect all images including multi-resolution variants
		List<Image> allImages = new ArrayList<>();
		for( Image image : images ) {
			if( MultiResolutionImageSupport.isMultiResolutionImage( image ) )
				allImages.addAll( MultiResolutionImageSupport.getResolutionVariants( image ) );
			else
				allImages.add( image );
		}

		// sort images by size
		allImages.sort( (image1, image2) -> {
			return image1.getWidth( null ) - image2.getWidth( null );
		} );

		// create icon
		return new FlatTitlePaneIcon( allImages, size );
	}

	private final List<Image> images;

	private FlatTitlePaneIcon( List<Image> images, Dimension size ) {
		super( new ImageIcon( images.get( 0 ) ), size.width, size.height );
		this.images = images;
	}

	@Override
	protected Image getResolutionVariant( int destImageWidth, int destImageHeight ) {
		for( Image image : images ) {
			if( destImageWidth <= image.getWidth( null ) &&
				destImageHeight <= image.getHeight( null ) )
			  return image;
		}

		return images.get( images.size() - 1 );
	}
}
