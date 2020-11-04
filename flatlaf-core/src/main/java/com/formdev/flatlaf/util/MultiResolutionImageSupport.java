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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Support for multi-resolution images available since Java 9.
 *
 * @author Karl Tauber
 */
public class MultiResolutionImageSupport
{
	public static boolean isAvailable() {
		return false;
	}

	public static boolean isMultiResolutionImage( Image image ) {
		return false;
	}

	public static Image create( int baseImageIndex, Image... resolutionVariants ) {
		return resolutionVariants[baseImageIndex];
	}

	public static Image map( Image image, Function<Image, Image> mapper ) {
		return mapper.apply( image );
	}

	public static Image getResolutionVariant( Image image, int destImageWidth, int destImageHeight ) {
		return image;
	}

	public static List<Image> getResolutionVariants( Image image ) {
		return Collections.singletonList( image );
	}
}
