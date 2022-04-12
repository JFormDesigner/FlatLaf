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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

//
// NOTE:
//     This implementation is for Java 8 only.
//     There is also a variant for Java 9 and later.
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
	/**
	 * Checks whether multi-resolution image support is available.
	 * 
	 * @return {@code true} when running on Java 9 or later; {@code false} on Java 8
	 */
	public static boolean isAvailable() {
		return false;
	}

	/**
	 * Checks whether the given image is a multi-resolution image that implements
	 * the interface {@code java.awt.image.MultiResolutionImage}.
	 */
	public static boolean isMultiResolutionImage( Image image ) {
		return false;
	}

	/**
	 * Creates a multi-resolution image from the given resolution variants.
	 * 
	 * @param baseImageIndex index of the base image in the resolution variants array
	 * @param resolutionVariants image resolution variants (sorted by size; smallest first)
	 * @return a multi-resolution image on Java 9 or later; the base image on Java 8
	 */
	public static Image create( int baseImageIndex, Image... resolutionVariants ) {
		return resolutionVariants[baseImageIndex];
	}

	/**
	 * Creates a multi-resolution image for the given dimensions.
	 * Initially the image does not contain any image data.
	 * The real images are created (and cached) on demand by invoking the given producer function.
	 * <p>
	 * The given dimensions array is only used for {@link #getResolutionVariants(Image)}.
	 * The producer function may be invoked with any dimension (that is not contained in 
	 * dimensions array) and is expected to produce an image for the passed in dimension.
	 * 
	 * @param baseImageIndex index of the base image in the dimensions array
	 * @param dimensions dimensions of resolution variants (sorted by size; smallest first)
	 * @param producer producer function that creates a real image for the requested size
	 * @return a multi-resolution image on Java 9 or later; the base image on Java 8
	 */
	public static Image create( int baseImageIndex, Dimension[] dimensions, Function<Dimension, Image> producer ) {
		return producer.apply( dimensions[baseImageIndex] );
	}

	/**
	 * Creates a multi-resolution image that maps images from another multi-resolution image
	 * using the given mapper function.
	 * <p>
	 * Can be used to apply filter to multi-resolution images on demand.
	 * E.g. passed in image is for "enabled" state and mapper function creates images
	 * for "disabled" state.  
	 * 
	 * @param image a multi-resolution image that is mapped using the given mapper function
	 * @param mapper mapper function that maps a single resolution variant to a new image (e.g. applying a filter)
	 * @return a multi-resolution image on Java 9 or later; a mapped image on Java 8
	 */
	public static Image map( Image image, Function<Image, Image> mapper ) {
		return mapper.apply( image );
	}

	/**
	 * Get the image variant that best matches the given width and height.
	 * <p>
	 * If the given image is a multi-resolution image then invokes
	 * {@code java.awt.image.MultiResolutionImage.getResolutionVariant(destImageWidth, destImageHeight)}.
	 * Otherwise, returns the given image.
	 */
	public static Image getResolutionVariant( Image image, int destImageWidth, int destImageHeight ) {
		return image;
	}

	/**
	 * Get a list of all resolution variants.
	 * <p>
	 * If the given image is a multi-resolution image then invokes
	 * {@code java.awt.image.MultiResolutionImage.getResolutionVariants()}.
	 * Otherwise, returns a list containing only the given image.
	 */
	public static List<Image> getResolutionVariants( Image image ) {
		return Collections.singletonList( image );
	}
}
