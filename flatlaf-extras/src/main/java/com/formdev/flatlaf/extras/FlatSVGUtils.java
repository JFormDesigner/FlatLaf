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

package com.formdev.flatlaf.extras;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JWindow;
import com.formdev.flatlaf.util.MultiResolutionImageSupport;
import com.formdev.flatlaf.util.SystemInfo;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;

/**
 * Utility methods for SVG.
 *
 * @author Karl Tauber
 */
public class FlatSVGUtils
{
	/**
	 * Creates from the given SVG a list of icon images with different sizes that
	 * can be used for windows headers. The SVG should have a size of 16x16,
	 * otherwise it is scaled.
	 * <p>
	 * If running on Windows in Java 9 or later and multi-resolution image support is available,
	 * then a single multi-resolution image is returned that creates images on demand
	 * for requested sizes from SVG.
	 * This has the advantage that only images for used sizes are created.
	 * Also if unusual sizes are requested (e.g. 18x18), then they are created from SVG.
	 * <p>
	 * If using Java modules, the package containing the SVG must be opened in {@code module-info.java}.
	 * Otherwise use {@link #createWindowIconImages(URL)}.
	 *
	 * @param svgName the name of the SVG resource (a '/'-separated path)
	 * @return list of icon images with different sizes (16x16, 20x20, 24x24, 28x28, 32x32, 48x48 and 64x64)
	 * @throws RuntimeException if failed to load or render SVG file
	 * @see JWindow#setIconImages(List)
	 */
	public static List<Image> createWindowIconImages( String svgName ) {
		return createWindowIconImages( getResource( svgName ) );
	}

	/**
	 * Creates from the given SVG a list of icon images with different sizes that
	 * can be used for windows headers. The SVG should have a size of 16x16,
	 * otherwise it is scaled.
	 * <p>
	 * If running on Windows in Java 9 or later and multi-resolution image support is available,
	 * then a single multi-resolution image is returned that creates images on demand
	 * for requested sizes from SVG.
	 * This has the advantage that only images for used sizes are created.
	 * Also if unusual sizes are requested (e.g. 18x18), then they are created from SVG.
	 * <p>
	 * This method is useful if using Java modules and the package containing the SVG
	 * is not opened in {@code module-info.java}.
	 * E.g. {@code createWindowIconImages( getClass().getResource( "/com/myapp/myicon.svg" ) )}.
	 *
	 * @param svgUrl the URL of the SVG resource
	 * @return list of icon images with different sizes (16x16, 20x20, 24x24, 28x28, 32x32, 48x48 and 64x64)
	 * @throws RuntimeException if failed to load or render SVG file
	 * @see JWindow#setIconImages(List)
	 * @since 2
	 */
	public static List<Image> createWindowIconImages( URL svgUrl ) {
		SVGDiagram diagram = loadSVG( svgUrl );

		if( SystemInfo.isWindows && MultiResolutionImageSupport.isAvailable() ) {
			// use a multi-resolution image that creates images on demand for requested sizes
			return Collections.singletonList( MultiResolutionImageSupport.create( 0,
				new Dimension[] {
					// Listing all these sizes here is actually not necessary because
					// any size is created on demand when
					// MultiResolutionImage.getResolutionVariant(double destImageWidth, double destImageHeight)
					// is invoked.
					// This sizes are only used by MultiResolutionImage.getResolutionVariants().
					new Dimension( 16, 16 ),	// 100%
					new Dimension( 20, 20 ),	// 125%
					new Dimension( 24, 24 ),	// 150%
					new Dimension( 28, 28 ),	// 175%
					new Dimension( 32, 32 ),	// 200%
					new Dimension( 48, 48 ),	// 300%
					new Dimension( 64, 64 ),	// 400%
			}, dim -> {
				return svg2image( diagram, dim.width, dim.height );
			} ) );
		} else {
			return Arrays.asList(
				svg2image( diagram, 16, 16 ),	// 100%
				svg2image( diagram, 20, 20 ),	// 125%
				svg2image( diagram, 24, 24 ),	// 150%
				svg2image( diagram, 28, 28 ),	// 175%
				svg2image( diagram, 32, 32 ),	// 200%
				svg2image( diagram, 48, 48 ),	// 300%
				svg2image( diagram, 64, 64 )	// 400%
			);
		}
	}

	/**
	 * Creates a buffered image and renders the given SVG into it.
	 * <p>
	 * If using Java modules, the package containing the SVG must be opened in {@code module-info.java}.
	 * Otherwise use {@link #svg2image(URL, int, int)}.
	 *
	 * @param svgName the name of the SVG resource (a '/'-separated path)
	 * @param width the width of the image
	 * @param height the height of the image
	 * @return the image
	 * @throws RuntimeException if failed to load or render SVG file
	 */
	public static BufferedImage svg2image( String svgName, int width, int height ) {
		return svg2image( getResource( svgName ), width, height );
	}

	/**
	 * Creates a buffered image and renders the given SVG into it.
	 * <p>
	 * This method is useful if using Java modules and the package containing the SVG
	 * is not opened in {@code module-info.java}.
	 * E.g. {@code svg2image( getClass().getResource( "/com/myapp/myicon.svg" ), 24, 24 )}.
	 *
	 * @param svgUrl the URL of the SVG resource
	 * @param width the width of the image
	 * @param height the height of the image
	 * @return the image
	 * @throws RuntimeException if failed to load or render SVG file
	 * @since 2
	 */
	public static BufferedImage svg2image( URL svgUrl, int width, int height ) {
		return svg2image( loadSVG( svgUrl ), width, height );
	}

	/**
	 * Creates a buffered image and renders the given SVG into it.
	 * <p>
	 * If using Java modules, the package containing the SVG must be opened in {@code module-info.java}.
	 * Otherwise use {@link #svg2image(URL, float)}.
	 *
	 * @param svgName the name of the SVG resource (a '/'-separated path)
	 * @param scaleFactor the amount by which the SVG size is scaled
	 * @return the image
	 * @throws RuntimeException if failed to load or render SVG file
	 */
	public static BufferedImage svg2image( String svgName, float scaleFactor ) {
		return svg2image( getResource( svgName ), scaleFactor );
	}

	/**
	 * Creates a buffered image and renders the given SVG into it.
	 * <p>
	 * This method is useful if using Java modules and the package containing the SVG
	 * is not opened in {@code module-info.java}.
	 * E.g. {@code svg2image( getClass().getResource( "/com/myapp/myicon.svg" ), 1.5f )}.
	 *
	 * @param svgUrl the URL of the SVG resource
	 * @param scaleFactor the amount by which the SVG size is scaled
	 * @return the image
	 * @throws RuntimeException if failed to load or render SVG file
	 * @since 2
	 */
	public static BufferedImage svg2image( URL svgUrl, float scaleFactor ) {
		SVGDiagram diagram = loadSVG( svgUrl );
		int width = (int) (diagram.getWidth() * scaleFactor);
		int height = (int) (diagram.getHeight() * scaleFactor);
		return svg2image( diagram, width, height );
	}

	/**
	 * Creates a buffered image and renders the given SVGDiagram into it.
	 *
	 * @param diagram the SVG diagram
	 * @param width the width of the image
	 * @param height the height of the image
	 * @return the image
	 * @throws RuntimeException if failed to render SVG file
	 */
	public static BufferedImage svg2image( SVGDiagram diagram, int width, int height ) {
		try {
			BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

			Graphics2D g = image.createGraphics();
			try {
				g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );

				double sx = width / diagram.getWidth();
				double sy = height / diagram.getHeight();
				if( sx != 1 || sy != 1 )
					g.scale( sx, sy );

				diagram.setIgnoringClipHeuristic( true );

				diagram.render( g );
			} finally {
				g.dispose();
			}
			return image;

		} catch( SVGException ex ) {
			throw new RuntimeException( ex );
		}
	}

	private static URL getResource( String svgName ) {
		return FlatSVGUtils.class.getResource( svgName );
	}

	private static SVGDiagram loadSVG( URL url ) {
		return FlatSVGIcon.loadSVG( FlatSVGIcon.url2uri( url ) );
	}
}
