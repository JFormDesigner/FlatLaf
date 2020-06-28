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
 * @author Karl Tauber
 */
public class SVGUtils
{
	/**
	 * Creates from the given SVG a list of icon images with different sizes that
	 * can be used for windows headers. The SVG should have a size of 16 x 16.
	 *
	 * @see JWindow#setIconImages(List)
	 */
	public static List<Image> createWindowIconImages( String svgName ) {
		return Arrays.asList(
			svg2image( svgName, 1f ),	// 16 x 16
			svg2image( svgName, 1.5f ),	// 24 x 24
			svg2image( svgName, 2f ),	// 32 x 32
			svg2image( svgName, 3f ),	// 48 x 48
			svg2image( svgName, 4f )		// 64 x 64
		);
	}

	/**
	 * Creates a buffered image and renders the given SVG into it.
	 */
	public static BufferedImage svg2image( String svgName, float scaleFactor ) {
		try {
			URL url = SVGUtils.class.getResource( svgName );
			SVGDiagram diagram = SVGCache.getSVGUniverse().getDiagram( url.toURI() );

			BufferedImage image = new BufferedImage(
				(int) (diagram.getWidth() * scaleFactor),
				(int) (diagram.getHeight() * scaleFactor),
				BufferedImage.TYPE_INT_ARGB );

			Graphics2D g = image.createGraphics();
			try {
				g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );

				if( scaleFactor != 1f )
					g.scale( scaleFactor, scaleFactor );

				diagram.setIgnoringClipHeuristic( true );

				diagram.render( g );
			} finally {
				g.dispose();
			}
			return image;

		} catch( URISyntaxException | SVGException ex ) {
			throw new RuntimeException( ex );
		}
	}
}
