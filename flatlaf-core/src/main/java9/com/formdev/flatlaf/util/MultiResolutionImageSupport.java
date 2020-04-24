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

import java.awt.Image;
import java.awt.image.AbstractMultiResolutionImage;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.MultiResolutionImage;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;
import javax.swing.ImageIcon;

/**
 * Support for multi-resolution images available since Java 9.
 *
 * @author Karl Tauber
 */
public class MultiResolutionImageSupport
{
	public static boolean isAvailable() {
		return true;
	}

	public static boolean isMultiResolutionImage( Image image ) {
		return image instanceof MultiResolutionImage;
	}

	public static Image create( int baseImageIndex, Image... resolutionVariants ) {
		return new BaseMultiResolutionImage( baseImageIndex, resolutionVariants );
	}

	public static Image map( Image image, Function<Image, Image> mapper ) {
		return image instanceof MultiResolutionImage
			? new MappedMultiResolutionImage( image, mapper )
			: mapper.apply( image );
	}

	//---- class MappedMultiResolutionImage -----------------------------------

	private static class MappedMultiResolutionImage
		extends AbstractMultiResolutionImage
	{
		private final Image mrImage;
		private final Function<Image, Image> mapper;
		private final IdentityHashMap<Image, Image> cache = new IdentityHashMap<>();

		MappedMultiResolutionImage( Image mrImage, Function<Image, Image> mapper ) {
			assert mrImage instanceof MultiResolutionImage;

			this.mrImage = mrImage;
			this.mapper = mapper;
		}

		@Override
		public Image getResolutionVariant( double destImageWidth, double destImageHeight ) {
			Image variant = ((MultiResolutionImage)mrImage).getResolutionVariant( destImageWidth, destImageHeight );
			return mapAndCacheImage( variant );
		}

		@Override
		public List<Image> getResolutionVariants() {
			List<Image> variants = ((MultiResolutionImage)mrImage).getResolutionVariants();
			List<Image> mappedVariants = new ArrayList<>();
			for( Image image : variants )
				mappedVariants.add( mapAndCacheImage( image ) );
			return mappedVariants;
		}

		@Override
		protected Image getBaseImage() {
			return mapAndCacheImage( mrImage );
		}

		private Image mapAndCacheImage( Image image ) {
			return cache.computeIfAbsent( image, img -> {
				return new ImageIcon( mapper.apply( img ) ).getImage();
			} );
		}
	}
}
