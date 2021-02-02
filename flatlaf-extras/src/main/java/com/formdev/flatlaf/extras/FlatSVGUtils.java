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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.swing.JWindow;
import com.kitfox.svg.SVGCache;
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
	 *
	 * @param svgName the name of the SVG resource (a '/'-separated path)
	 * @return list of icon images with different sizes (16x16, 24x24, 32x32, 48x48 and 64x64)
	 * @throws RuntimeException if failed to load or render SVG file
	 * @see JWindow#setIconImages(List)
	 */
	public static List<Image> createWindowIconImages( String svgName ) {
		SVGDiagram diagram = loadSVG( svgName );

		return Arrays.asList(
			svg2image( diagram, 16, 16 ),
			svg2image( diagram, 24, 24 ),
			svg2image( diagram, 32, 32 ),
			svg2image( diagram, 48, 48 ),
			svg2image( diagram, 64, 64 )
		);
	}

	/**
	 * Creates a buffered image and renders the given SVG into it.
	 *
	 * @param svgName the name of the SVG resource (a '/'-separated path)
	 * @param width the width of the image
	 * @param height the height of the image
	 * @return the image
	 * @throws RuntimeException if failed to load or render SVG file
	 */
	public static BufferedImage svg2image( String svgName, int width, int height ) {
		return svg2image( loadSVG( svgName ), width, height );
	}

	/**
	 * Creates a buffered image and renders the given SVG into it.
	 *
	 * @param svgName the name of the SVG resource (a '/'-separated path)
	 * @param scaleFactor the amount by which the SVG size is scaled
	 * @return the image
	 * @throws RuntimeException if failed to load or render SVG file
	 */
	public static BufferedImage svg2image( String svgName, float scaleFactor ) {
		SVGDiagram diagram = loadSVG( svgName );
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

	/**
	 * Loads a SVG file.
	 *
	 * @param svgName the name of the SVG resource (a '/'-separated path)
	 * @return the SVG diagram
	 * @throws RuntimeException if failed to load SVG file
	 */
	private static SVGDiagram loadSVG( String svgName ) {
		try {
			URL url = FlatSVGUtils.class.getResource( svgName );
			return SVGCache.getSVGUniverse().getDiagram( url.toURI() );
		} catch( URISyntaxException ex ) {
			throw new RuntimeException( ex );
		}
	}
}
