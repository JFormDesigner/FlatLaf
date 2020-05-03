/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.extras;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.RGBImageFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatIconColors;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.GrayFilter;
import com.formdev.flatlaf.util.UIScale;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

/**
 * @author Karl Tauber
 */
public class FlatSVGIcon
	implements Icon
{
	// use own SVG universe so that it can not be cleared from anywhere
	private static final SVGUniverse svgUniverse = new SVGUniverse();

	private final String name;
	private SVGDiagram diagram;
	private boolean dark;

	public FlatSVGIcon( String name ) {
		this.name = name;
	}

	private void update() {
		if( dark == isDarkLaf() && diagram != null )
			return;

		dark = isDarkLaf();
		URL url = getIconURL( name, dark );
		if( url == null & dark )
			url = getIconURL( name, false );

		// load/get image
		try {
			diagram = svgUniverse.getDiagram( url.toURI() );
		} catch( URISyntaxException ex ) {
			ex.printStackTrace();
		}
	}

	private URL getIconURL( String name, boolean dark ) {
		if( dark ) {
			int dotIndex = name.lastIndexOf( '.' );
			name = name.substring( 0, dotIndex ) + "_dark" + name.substring( dotIndex );
		}
		return FlatSVGIcon.class.getClassLoader().getResource( name );
	}

	@Override
	public int getIconWidth() {
		update();
		return (int) UIScale.scale( (diagram != null) ? diagram.getWidth() : 16 );
	}

	@Override
	public int getIconHeight() {
		update();
		return (int) UIScale.scale( (diagram != null) ? diagram.getHeight() : 16 );
	}

	@Override
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		update();

		Rectangle clipBounds = g.getClipBounds();
		if( clipBounds != null && !clipBounds.intersects( new Rectangle( x, y, getIconWidth(), getIconHeight() ) ) )
			return;

		// get gray filter
		RGBImageFilter grayFilter = null;
		if( c != null && !c.isEnabled() ) {
			Object grayFilterObj = UIManager.get( "Component.grayFilter" );
			grayFilter = (grayFilterObj instanceof RGBImageFilter)
				? (RGBImageFilter) grayFilterObj
				: GrayFilter.createDisabledIconFilter( dark );
		}

		Graphics2D g2 = new GraphicsFilter( (Graphics2D) g.create(), ColorFilter.getInstance(), grayFilter );

		try {
			FlatUIUtils.setRenderingHints( g2 );
			g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );

			paintSvg( g2, x, y );
		} finally {
			g2.dispose();
		}
	}

	private void paintSvg( Graphics2D g, int x, int y ) {
		if( diagram == null ) {
			paintSvgError( g, x, y );
			return;
		}

		g.translate( x, y );
		g.clipRect( 0, 0, getIconWidth(), getIconHeight() );

		UIScale.scaleGraphics( g );

		diagram.setIgnoringClipHeuristic( true );

		try {
			diagram.render( g );
		} catch( SVGException ex ) {
			paintSvgError( g, 0, 0 );
		}
	}

	private void paintSvgError( Graphics2D g, int x, int y ) {
		g.setColor( Color.red );
		g.fillRect( x, y, getIconWidth(), getIconHeight() );
	}

	private static Boolean darkLaf;

	private static boolean isDarkLaf() {
		if( darkLaf == null ) {
			lafChanged();

			UIManager.addPropertyChangeListener( e -> {
				lafChanged();
			} );
		}

		return darkLaf;
	}

	private static void lafChanged() {
		LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
		darkLaf = (lookAndFeel instanceof FlatLaf && ((FlatLaf)lookAndFeel).isDark());
	}

	//---- class ColorFilter --------------------------------------------------

	public static class ColorFilter
	{
		private static ColorFilter instance;

		private final Map<Integer, String> rgb2keyMap = new HashMap<>();

		public static ColorFilter getInstance() {
			if( instance == null )
				instance = new ColorFilter();
			return instance;
		}

		public ColorFilter() {
			for( FlatIconColors c : FlatIconColors.values() )
				rgb2keyMap.put( c.rgb, c.key );
		}

		public Color filter( Color color ) {
			String colorKey = rgb2keyMap.get( color.getRGB() & 0xffffff );
			if( colorKey == null )
				return color;

			Color newColor = UIManager.getColor( colorKey );
			if( newColor == null )
				return color;

			return (newColor.getAlpha() != color.getAlpha())
				? new Color( (newColor.getRGB() & 0x00ffffff) | (color.getRGB() & 0xff000000) )
				: newColor;
		};
	}

	//---- class GraphicsFilter -----------------------------------------------

	private static class GraphicsFilter
		extends Graphics2DProxy
	{
		private final ColorFilter colorFilter;
		private final RGBImageFilter grayFilter;

		public GraphicsFilter( Graphics2D delegate, ColorFilter colorFilter, RGBImageFilter grayFilter ) {
			super( delegate );
			this.colorFilter = colorFilter;
			this.grayFilter = grayFilter;
		}

		@Override
		public void setColor( Color c ) {
			super.setColor( filterColor( c ) );
		}

		@Override
		public void setPaint( Paint paint ) {
			if( paint instanceof Color )
				paint = filterColor( (Color) paint );
			super.setPaint( paint );
		}

		private Color filterColor( Color color ) {
			if( colorFilter != null )
				color = colorFilter.filter( color );
			if( grayFilter != null ) {
				int oldRGB = color.getRGB();
				int newRGB = grayFilter.filterRGB( 0, 0, oldRGB );
				color = (newRGB != oldRGB) ? new Color( newRGB, true ) : color;
			}
			return color;
		}
	}
}
