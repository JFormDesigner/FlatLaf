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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.AbstractMultiResolutionImage;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.MultiResolutionImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;
import javax.swing.ImageIcon;

//
// NOTE:
//     This implementation is for Java 9 and later.
//     There is also a variant for Java 8.
//
//     Make sure that the API is in sync.
//

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

	public static Image create( int baseImageIndex, Dimension[] dimensions, Function<Dimension, Image> producer ) {
		return new ProducerMultiResolutionImage( dimensions, producer );
	}

	public static Image map( Image image, Function<Image, Image> mapper ) {
		return image instanceof MultiResolutionImage
			? new MappedMultiResolutionImage( image, mapper )
			: mapper.apply( image );
	}

	public static Image getResolutionVariant( Image image, int destImageWidth, int destImageHeight ) {
		return (image instanceof MultiResolutionImage)
			? ((MultiResolutionImage)image).getResolutionVariant( destImageWidth, destImageHeight )
			: image;
	}

	public static List<Image> getResolutionVariants( Image image ) {
		return (image instanceof MultiResolutionImage)
			? ((MultiResolutionImage)image).getResolutionVariants()
			: Collections.singletonList( image );
	}

	//---- class MappedMultiResolutionImage -----------------------------------

	/**
	 * A multi-resolution image implementation that maps images on demand for requested sizes.
	 */
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

		@Override
		public int getWidth( ImageObserver observer ) {
			return mrImage.getWidth( observer );
		}

		@Override
		public int getHeight( ImageObserver observer ) {
			return mrImage.getHeight( observer );
		}

		@Override
		public ImageProducer getSource() {
			return mrImage.getSource();
		}

		@Override
		public Object getProperty( String name, ImageObserver observer ) {
			return mrImage.getProperty( name, observer );
		}

		private Image mapAndCacheImage( Image image ) {
			return cache.computeIfAbsent( image, img -> {
				// using ImageIcon here makes sure that the image is loaded
				return new ImageIcon( mapper.apply( img ) ).getImage();
			} );
		}
	}

	//---- class ProducerMultiResolutionImage ---------------------------------

	/**
	 * A multi-resolution image implementation that produces images on demand for requested sizes.
	 */
	private static class ProducerMultiResolutionImage
		extends AbstractMultiResolutionImage
	{
		private final Dimension[] dimensions;
		private final Function<Dimension, Image> producer;
		private final HashMap<Dimension, Image> cache = new HashMap<>();

		ProducerMultiResolutionImage( Dimension[] dimensions, Function<Dimension, Image> producer ) {
			this.dimensions = dimensions;
			this.producer = producer;
		}

		@Override
		public Image getResolutionVariant( double destImageWidth, double destImageHeight ) {
			return produceAndCacheImage( new Dimension( (int) destImageWidth, (int) destImageHeight ) );
		}

		@Override
		public List<Image> getResolutionVariants() {
			List<Image> mappedVariants = new ArrayList<>();
			for( Dimension size : dimensions )
				mappedVariants.add( produceAndCacheImage( size ) );
			return mappedVariants;
		}

		@Override
		protected Image getBaseImage() {
			return produceAndCacheImage( dimensions[0] );
		}

		@Override
		public int getWidth( ImageObserver observer ) {
			return dimensions[0].width;
		}

		@Override
		public int getHeight( ImageObserver observer ) {
			return dimensions[0].height;
		}

		private Image produceAndCacheImage( Dimension size ) {
			return cache.computeIfAbsent( size, size2 -> {
				// using ImageIcon here makes sure that the image is loaded
				return new ImageIcon( producer.apply( size2 ) ).getImage();
			} );
		}
	}
}
