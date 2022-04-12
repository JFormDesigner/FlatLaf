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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatIconColors;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLaf.DisabledIconProvider;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.GrayFilter;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.MultiResolutionImageSupport;
import com.formdev.flatlaf.util.SoftCache;
import com.formdev.flatlaf.util.UIScale;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

/**
 * An icon that loads and paints SVG.
 *
 * @author Karl Tauber
 */
public class FlatSVGIcon
	extends ImageIcon
	implements DisabledIconProvider
{
	// cache that uses soft references for values, which allows freeing SVG diagrams if no longer used
	private static final SoftCache<URI, SVGDiagram> svgCache = new SoftCache<>();

	// use own SVG universe so that it can not be cleared from anywhere
	private static final SVGUniverse svgUniverse = new SVGUniverse();
	private static int streamNumber;

	private final String name;
	private final int width;
	private final int height;
	private final float scale;
	private final boolean disabled;
	private final ClassLoader classLoader;
	private final URI uri;

	private ColorFilter colorFilter;

	private SVGDiagram diagram;
	private boolean dark;
	private boolean loadFailed;

	/**
	 * Creates an SVG icon from the given resource name.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as icon size.
	 * <p>
	 * If using Java modules, the package containing the icon must be opened in {@code module-info.java}.
	 * Otherwise use {@link #FlatSVGIcon(URL)}.
	 * <p>
	 * This is cheap operation because the icon is only loaded when used.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path; e.g. {@code "com/myapp/myicon.svg"})
	 * @see ClassLoader#getResource(String)
	 */
	public FlatSVGIcon( String name ) {
		this( name, -1, -1, 1, false, null, null );
	}

	/**
	 * Creates an SVG icon from the given resource name.
	 * The SVG file is loaded from the given class loader.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as icon size.
	 * <p>
	 * If using Java modules, the package containing the icon must be opened in {@code module-info.java}.
	 * Otherwise use {@link #FlatSVGIcon(URL)}.
	 * <p>
	 * This is cheap operation because the icon is only loaded when used.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path; e.g. {@code "com/myapp/myicon.svg"})
	 * @param classLoader the class loader used to load the SVG resource
	 * @see ClassLoader#getResource(String)
	 */
	public FlatSVGIcon( String name, ClassLoader classLoader ) {
		this( name, -1, -1, 1, false, classLoader, null );
	}

	/**
	 * Creates an SVG icon from the given resource name with the given width and height.
	 * <p>
	 * The icon is scaled if the given size is different to the size specified in the SVG file.
	 * <p>
	 * If using Java modules, the package containing the icon must be opened in {@code module-info.java}.
	 * Otherwise use {@link #FlatSVGIcon(URL)}.
	 * <p>
	 * This is cheap operation because the icon is only loaded when used.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path; e.g. {@code "com/myapp/myicon.svg"})
	 * @param width the width of the icon
	 * @param height the height of the icon
	 * @see ClassLoader#getResource(String)
	 */
	public FlatSVGIcon( String name, int width, int height ) {
		this( name, width, height, 1, false, null, null );
	}

	/**
	 * Creates an SVG icon from the given resource name with the given width and height.
	 * The SVG file is loaded from the given class loader.
	 * <p>
	 * The icon is scaled if the given size is different to the size specified in the SVG file.
	 * <p>
	 * If using Java modules, the package containing the icon must be opened in {@code module-info.java}.
	 * Otherwise use {@link #FlatSVGIcon(URL)}.
	 * <p>
	 * This is cheap operation because the icon is only loaded when used.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path; e.g. {@code "com/myapp/myicon.svg"})
	 * @param width the width of the icon
	 * @param height the height of the icon
	 * @param classLoader the class loader used to load the SVG resource
	 * @see ClassLoader#getResource(String)
	 */
	public FlatSVGIcon( String name, int width, int height, ClassLoader classLoader ) {
		this( name, width, height, 1, false, classLoader, null );
	}

	/**
	 * Creates an SVG icon from the given resource name that is scaled by the given amount.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as base icon size, which is multiplied
	 * by the given scale factor.
	 * <p>
	 * If using Java modules, the package containing the icon must be opened in {@code module-info.java}.
	 * Otherwise use {@link #FlatSVGIcon(URL)}.
	 * <p>
	 * This is cheap operation because the icon is only loaded when used.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path; e.g. {@code "com/myapp/myicon.svg"})
	 * @param scale the amount by which the icon size is scaled
	 * @see ClassLoader#getResource(String)
	 */
	public FlatSVGIcon( String name, float scale ) {
		this( name, -1, -1, scale, false, null, null );
	}

	/**
	 * Creates an SVG icon from the given resource name that is scaled by the given amount.
	 * The SVG file is loaded from the given class loader.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as base icon size, which is multiplied
	 * by the given scale factor.
	 * <p>
	 * If using Java modules, the package containing the icon must be opened in {@code module-info.java}.
	 * Otherwise use {@link #FlatSVGIcon(URL)}.
	 * <p>
	 * This is cheap operation because the icon is only loaded when used.
	 *
	 * @param name the name of the SVG resource (a '/'-separated path; e.g. {@code "com/myapp/myicon.svg"})
	 * @param scale the amount by which the icon size is scaled
	 * @param classLoader the class loader used to load the SVG resource
	 * @see ClassLoader#getResource(String)
	 */
	public FlatSVGIcon( String name, float scale, ClassLoader classLoader ) {
		this( name, -1, -1, scale, false, classLoader, null );
	}

	/**
	 * Creates an SVG icon from the given URL.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as icon size.
	 * <p>
	 * This method is useful if using Java modules and the package containing the icon
	 * is not opened in {@code module-info.java}.
	 * E.g. {@code new FlatSVGIcon( getClass().getResource( "/com/myapp/myicon.svg" ) )}.
	 * <p>
	 * This is cheap operation because the icon is only loaded when used.
	 *
	 * @param url the URL of the SVG resource
	 * @see ClassLoader#getResource(String)
	 * @since 2
	 */
	public FlatSVGIcon( URL url ) {
		this( null, -1, -1, 1, false, null, url2uri( url ) );
	}

	/**
	 * Creates an SVG icon from the given URI.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as icon size.
	 * <p>
	 * This is cheap operation because the icon is only loaded when used.
	 *
	 * @param uri the URI of the SVG resource
	 * @see ClassLoader#getResource(String)
	 * @since 2
	 */
	public FlatSVGIcon( URI uri ) {
		this( null, -1, -1, 1, false, null, uri );
	}

	/**
	 * Creates an SVG icon from the given file.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as icon size.
	 * <p>
	 * This is cheap operation because the icon is only loaded when used.
	 *
	 * @param file the SVG file
	 * @since 2
	 */
	public FlatSVGIcon( File file ) {
		this( null, -1, -1, 1, false, null, file.toURI() );
	}

	/**
	 * Creates an SVG icon from the given input stream.
	 * <p>
	 * The SVG attributes {@code width} and {@code height} (or {@code viewBox})
	 * in the tag {@code <svg>} are used as icon size.
	 * <p>
	 * The input stream is loaded, parsed and closed immediately.
	 *
	 * @param in the input stream for reading a SVG resource
	 * @throws IOException if an I/O exception occurs
	 * @since 2
	 */
	public FlatSVGIcon( InputStream in ) throws IOException {
		this( null, -1, -1, 1, false, null, loadFromStream( in ) );

		// since the input stream is already loaded and parsed,
		// get diagram here and remove it from cache
		update();
		synchronized( FlatSVGIcon.class ) {
			svgCache.remove( uri );
		}
	}

	private static synchronized URI loadFromStream( InputStream in ) throws IOException {
		try( InputStream in2 = in ) {
			return svgUniverse.loadSVG( in2, "/flatlaf-stream-" + (streamNumber++) );
		}
	}

	/**
	 * Creates a copy of the given icon.
	 * <p>
	 * If the icon has a color filter, then it is shared with the new icon.
	 *
	 * @since 2.0.1
	 */
	public FlatSVGIcon( FlatSVGIcon icon ) {
		this( icon.name, icon.width, icon.height, icon.scale, icon.disabled, icon.classLoader, icon.uri );
		colorFilter = icon.colorFilter;
		diagram = icon.diagram;
		dark = icon.dark;
	}

	protected FlatSVGIcon( String name, int width, int height, float scale, boolean disabled, ClassLoader classLoader, URI uri ) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.disabled = disabled;
		this.classLoader = classLoader;
		this.uri = uri;
	}

	/**
	 * Returns the name of the SVG resource (a '/'-separated path).
	 *
	 * @since 1.2
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the custom icon width specified in {@link #FlatSVGIcon(String, int, int)},
	 * {@link #FlatSVGIcon(String, int, int, ClassLoader)} or {@link #derive(int, int)}.
	 * Otherwise {@code -1} is returned.
	 * <p>
	 * To get the painted icon width, use {@link #getIconWidth()}.
	 *
	 * @since 1.2
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the custom icon height specified in {@link #FlatSVGIcon(String, int, int)},
	 * {@link #FlatSVGIcon(String, int, int, ClassLoader)} or {@link #derive(int, int)}.
	 * Otherwise {@code -1} is returned.
	 * <p>
	 * To get the painted icon height, use {@link #getIconHeight()}.
	 *
	 * @since 1.2
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the amount by which the icon size is scaled. Usually {@code 1}.
	 *
	 * @since 1.2
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Returns whether the icon is pained in "disabled" state.
	 *
	 * @see #getDisabledIcon()
	 * @since 1.2
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Returns the class loader used to load the SVG resource.
	 *
	 * @since 1.2
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * Creates a new icon with given width and height, which is derived from this icon.
	 * <p>
	 * If the icon has a color filter, then it is shared with the new icon.
	 *
	 * @param width the width of the new icon
	 * @param height the height of the new icon
	 * @return a new icon
	 */
	public FlatSVGIcon derive( int width, int height ) {
		if( width == this.width && height == this.height )
			return this;

		FlatSVGIcon icon = new FlatSVGIcon( name, width, height, scale, disabled, classLoader, uri );
		icon.colorFilter = colorFilter;
		icon.diagram = diagram;
		icon.dark = dark;
		return icon;
	}

	/**
	 * Creates a new icon with given scaling, which is derived from this icon.
	 * <p>
	 * If the icon has a color filter, then it is shared with the new icon.
	 *
	 * @param scale the amount by which the icon size is scaled
	 * @return a new icon
	 */
	public FlatSVGIcon derive( float scale ) {
		if( scale == this.scale )
			return this;

		FlatSVGIcon icon = new FlatSVGIcon( name, width, height, scale, disabled, classLoader, uri );
		icon.colorFilter = colorFilter;
		icon.diagram = diagram;
		icon.dark = dark;
		return icon;
	}

	/**
	 * Creates a new icon with disabled appearance, which is derived from this icon.
	 * <p>
	 * If the icon has a color filter, then it is shared with the new icon.
	 *
	 * @return a new icon
	 */
	@Override
	public Icon getDisabledIcon() {
		if( disabled )
			return this;

		FlatSVGIcon icon = new FlatSVGIcon( name, width, height, scale, true, classLoader, uri );
		icon.colorFilter = colorFilter;
		icon.diagram = diagram;
		icon.dark = dark;
		return icon;
	}

	/**
	 * Returns the currently active color filter or {@code null}.
	 *
	 * @since 1.2
	 */
	public ColorFilter getColorFilter() {
		return colorFilter;
	}

	/**
	 * Sets a color filter that can freely modify colors of this icon during painting.
	 * <p>
	 * This method accepts a {@link ColorFilter}. Usually you would want to use a ColorFilter created using the
	 * {@link ColorFilter#ColorFilter(Function)} constructor.
	 * <p>
	 * This can be used to brighten colors of the icon:
	 * <pre>icon.setColorFilter( new FlatSVGIcon.ColorFilter( color -&gt; color.brighter() ) );</pre>
	 * <p>
	 * Using a filter, icons can also be turned monochrome (painted with a single color):
	 * <pre>icon.setColorFilter( new FlatSVGIcon.ColorFilter( color -&gt; Color.RED ) );</pre>
	 * <p>
	 * Note: If a filter is already set, it will be replaced.
	 *
	 * @param colorFilter The color filter
	 * @since 1.2
	 */
	public void setColorFilter( ColorFilter colorFilter ) {
		this.colorFilter = colorFilter;
	}

	private void update() {
		if( loadFailed )
			return;

		if( dark == isDarkLaf() && diagram != null )
			return;

		dark = isDarkLaf();

		// SVGs already loaded via url or input stream can not have light/dark variants
		if( uri != null && diagram != null )
			return;

		URI uri = this.uri;
		if( uri == null ) {
			URL url = getIconURL( name, dark );
			if( url == null & dark )
				url = getIconURL( name, false );

			if( url == null ) {
				loadFailed = true;
				LoggingFacade.INSTANCE.logConfig( "FlatSVGIcon: resource '" + name + "' not found (if using Java modules, check whether icon package is opened in module-info.java)", null );
				return;
			}

			uri = url2uri( url );
		}

		diagram = loadSVG( uri );
		loadFailed = (diagram == null);
	}

	static synchronized SVGDiagram loadSVG( URI uri ) {
		// get from our cache
		SVGDiagram diagram = svgCache.get( uri );
		if( diagram != null )
			return diagram;

		// load/get SVG diagram
		diagram = svgUniverse.getDiagram( uri );

		if( diagram == null ) {
			LoggingFacade.INSTANCE.logSevere( "FlatSVGIcon: failed to load '" + uri + "'", null );
			return null;
		}

		// add to our (soft) cache and remove from SVGUniverse (hard) cache
		svgCache.put( uri, diagram );
		svgUniverse.removeDocument( uri );

		return diagram;
	}

	private URL getIconURL( String name, boolean dark ) {
		if( dark ) {
			int dotIndex = name.lastIndexOf( '.' );
			name = name.substring( 0, dotIndex ) + "_dark" + name.substring( dotIndex );
		}

		ClassLoader cl = (classLoader != null) ? classLoader : FlatSVGIcon.class.getClassLoader();
		return cl.getResource( name );
	}

	/**
	 * Returns whether the SVG file was found.
	 *
	 * @return whether the SVG file was found
	 */
	public boolean hasFound() {
		update();
		return diagram != null;
	}

	/**
	 * Returns the scaled width of the icon.
	 */
	@Override
	public int getIconWidth() {
		if( width > 0 )
			return scaleSize( width );

		update();
		return scaleSize( (diagram != null) ? Math.round( diagram.getWidth() ) : 16 );
	}

	/**
	 * Returns the scaled height of the icon.
	 */
	@Override
	public int getIconHeight() {
		if( height > 0 )
			return scaleSize( height );

		update();
		return scaleSize( (diagram != null) ? Math.round( diagram.getHeight() ) : 16 );
	}

	private int scaleSize( int size ) {
		int scaledSize = UIScale.scale( size );
		if( scale != 1 )
			scaledSize = Math.round( scaledSize * scale );
		return scaledSize;
	}

	@Override
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		update();

		// check whether icon is outside of clipping area
		Rectangle clipBounds = g.getClipBounds();
		if( clipBounds != null && !clipBounds.intersects( new Rectangle( x, y, getIconWidth(), getIconHeight() ) ) )
			return;

		// get gray filter
		RGBImageFilter grayFilter = null;
		if( disabled ) {
			Object grayFilterObj = UIManager.get( "Component.grayFilter" );
			grayFilter = (grayFilterObj instanceof RGBImageFilter)
				? (RGBImageFilter) grayFilterObj
				: GrayFilter.createDisabledIconFilter( dark );
		}

		Graphics2D g2 = new GraphicsFilter( (Graphics2D) g.create(), colorFilter, ColorFilter.getInstance(), grayFilter );

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
		if( width > 0 || height > 0 ) {
			double sx = (width > 0) ? width / diagram.getWidth() : 1;
			double sy = (height > 0) ? height / diagram.getHeight() : 1;
			if( sx != 1 || sy != 1 )
				g.scale( sx, sy );
		}
		if( scale != 1 )
			g.scale( scale, scale );

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

	@Override
	public Image getImage() {
		update();

		// base size
		int iconWidth = getIconWidth();
		int iconHeight = getIconHeight();

		Dimension[] dimensions = new Dimension[] {
			new Dimension( iconWidth, iconHeight ),
			new Dimension( iconWidth * 2, iconHeight * 2 ),
		};

		Function<Dimension, Image> producer = size -> {
			BufferedImage image = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_ARGB );
			Graphics2D g = image.createGraphics();
			try {
				// scale from base size to passed size
				double sx = (size.width > 0) ? (float) size.width / iconWidth : 1;
				double sy = (size.height > 0) ? (float) size.height / iconHeight : 1;
				if( sx != 1 || sy != 1 )
					g.scale( sx, sy );

				paintIcon( null, g, 0, 0 );
			} finally {
				g.dispose();
			}
			return image;
		};

		return MultiResolutionImageSupport.create( 0, dimensions, producer );
	}

	static URI url2uri( URL url ) {
		try {
			return url.toURI();
		} catch( URISyntaxException ex ) {
			throw new IllegalArgumentException( ex );
		}
	}

	private static Boolean darkLaf;

	/**
	 * Checks whether the current look and feel is dark.
	 * <p>
	 * Uses {@link FlatLaf#isLafDark()} and caches the result.
	 *
	 * @since 1.2
	 */
	public static boolean isDarkLaf() {
		if( darkLaf == null ) {
			lafChanged();

			UIManager.addPropertyChangeListener( e -> {
				lafChanged();
			} );
		}

		return darkLaf;
	}

	private static void lafChanged() {
		darkLaf = FlatLaf.isLafDark();
	}

	//---- class ColorFilter --------------------------------------------------

	/**
	 * A color filter that can modify colors of a painted {@link FlatSVGIcon}.
	 * <p>
	 * The ColorFilter modifies color in two ways.
	 * Either using a color map, where specific colors are mapped to different ones.
	 * And/or by modifying the colors in a mapper function.
	 * <p>
	 * When filtering a color, mappings are applied first, then the mapper function is applied.
	 * <p>
	 * Global {@link FlatSVGIcon} ColorFilter can be retrieved using the {@link ColorFilter#getInstance()} method.
	 */
	public static class ColorFilter
	{
		private static ColorFilter instance;

		private Map<Integer, String> rgb2keyMap;
		private Map<Color, Color> colorMap;
		private Map<Color, Color> darkColorMap;
		private Function<Color, Color> mapper;

		/**
		 * Returns the global ColorFilter that is applied to all icons.
		 */
		public static ColorFilter getInstance() {
			if( instance == null ) {
				instance = new ColorFilter();

				// add default color palette
				instance.rgb2keyMap = new HashMap<>();
				for( FlatIconColors c : FlatIconColors.values() )
					instance.rgb2keyMap.put( c.rgb, c.key );
			}
			return instance;
		}

		/**
		 * Creates an empty color filter.
		 */
		public ColorFilter() {
		}

		/**
		 * Creates a color filter with a color modifying function that changes painted colors.
		 * The {@link Function} gets passed the original color and returns a modified one.
		 * <p>
		 * Examples:
		 * A ColorFilter can be used to brighten colors of the icon:
		 * <pre>new ColorFilter( color -&gt; color.brighter() );</pre>
		 * <p>
		 * Using a ColorFilter, icons can also be turned monochrome (painted with a single color):
		 * <pre>new ColorFilter( color -&gt; Color.RED );</pre>
		 *
		 * @param mapper The color mapper function
		 * @since 1.2
		 */
		public ColorFilter( Function<Color, Color> mapper ) {
			setMapper( mapper );
		}

		/**
		 * Returns a color modifying function or {@code null}
		 *
		 * @since 1.2
		 */
		public Function<Color, Color> getMapper() {
			return mapper;
		}

		/**
		 * Sets a color modifying function that changes painted colors.
		 * The {@link Function} gets passed the original color and returns a modified one.
		 * <p>
		 * Examples:
		 * A ColorFilter can be used to brighten colors of the icon:
		 * <pre>filter.setMapper( color -&gt; color.brighter() );</pre>
		 * <p>
		 * Using a ColorFilter, icons can also be turned monochrome (painted with a single color):
		 * <pre>filter.setMapper( color -&gt; Color.RED );</pre>
		 *
		 * @param mapper The color mapper function
		 * @since 1.2
		 */
		public void setMapper( Function<Color, Color> mapper ) {
			this.mapper = mapper;
		}

		/**
		 * Returns the color mappings used for light themes.
		 *
		 * @since 1.2
		 */
		public Map<Color, Color> getLightColorMap() {
			return (colorMap != null)
				? Collections.unmodifiableMap( colorMap )
				: Collections.emptyMap();
		}

		/**
		 * Returns the color mappings used for dark themes.
		 *
		 * @since 1.2
		 */
		public Map<Color, Color> getDarkColorMap() {
			return (darkColorMap != null)
				? Collections.unmodifiableMap( darkColorMap )
				: getLightColorMap();
		}

		/**
		 * Adds color mappings. Used for light and dark themes.
		 */
		public ColorFilter addAll( Map<Color, Color> from2toMap ) {
			ensureColorMap();

			colorMap.putAll( from2toMap );
			if( darkColorMap != null )
				darkColorMap.putAll( from2toMap );
			return this;
		}

		/**
		 * Adds a color mappings, which has different colors for light and dark themes.
		 *
		 * @since 1.2
		 */
		public ColorFilter addAll( Map<Color, Color> from2toLightMap, Map<Color, Color> from2toDarkMap ) {
			ensureColorMap();
			ensureDarkColorMap();

			colorMap.putAll( from2toLightMap );
			darkColorMap.putAll( from2toDarkMap );
			return this;
		}

		/**
		 * Adds a color mapping. Used for light and dark themes.
		 */
		public ColorFilter add( Color from, Color to ) {
			ensureColorMap();

			colorMap.put( from, to );
			if( darkColorMap != null )
				darkColorMap.put( from, to );
			return this;
		}

		/**
		 * Adds a color mapping, which has different colors for light and dark themes.
		 *
		 * @since 1.2
		 */
		public ColorFilter add( Color from, Color toLight, Color toDark ) {
			ensureColorMap();
			ensureDarkColorMap();

			if( toLight != null )
				colorMap.put( from, toLight );
			if( toDark != null )
				darkColorMap.put( from, toDark );
			return this;
		}

		/**
		 * Removes a specific color mapping.
		 */
		public ColorFilter remove( Color from ) {
			if( colorMap != null )
				colorMap.remove( from );
			if( darkColorMap != null )
				darkColorMap.remove( from );
			return this;
		}

		/**
		 * Removes all color mappings.
		 *
		 * @since 1.2
		 */
		public ColorFilter removeAll() {
			colorMap = null;
			darkColorMap = null;
			return this;
		}

		private void ensureColorMap() {
			if( colorMap == null )
				colorMap = new HashMap<>();
		}

		private void ensureDarkColorMap() {
			if( darkColorMap == null )
				darkColorMap = new HashMap<>( colorMap );
		}

		public Color filter( Color color ) {
			// apply mappings
			color = applyMappings( color );

			// apply mapper function
			if( mapper != null )
				color = mapper.apply( color );

			return color;
		};

		private Color applyMappings( Color color ) {
			if( colorMap != null ) {
				Map<Color, Color> map = (darkColorMap != null && isDarkLaf()) ? darkColorMap : colorMap;
				Color newColor = map.get( color );
				if( newColor != null )
					return newColor;
			}

			if( rgb2keyMap != null ) {
				// RGB is mapped to a key in UI defaults, which contains the real color.
				// IntelliJ themes define such theme specific icon colors in .theme.json files.
				String colorKey = rgb2keyMap.get( color.getRGB() & 0xffffff );
				if( colorKey == null )
					return color;

				Color newColor = UIManager.getColor( colorKey );
				if( newColor == null )
					return color;

				// preserve alpha of original color
				return (newColor.getAlpha() != color.getAlpha())
					? new Color( (newColor.getRGB() & 0x00ffffff) | (color.getRGB() & 0xff000000) )
					: newColor;
			}

			return color;
		}

		/**
		 * Creates a color modifying function that uses {@link RGBImageFilter#filterRGB(int, int, int)}.
		 * Can be set to a {@link ColorFilter} using {@link ColorFilter#setMapper(Function)}.
		 *
		 * @see GrayFilter
		 * @since 1.2
		 */
		public static Function<Color, Color> createRGBImageFilterFunction( RGBImageFilter rgbImageFilter ) {
			return color -> {
				int oldRGB = color.getRGB();
				int newRGB = rgbImageFilter.filterRGB( 0, 0, oldRGB );
				return (newRGB != oldRGB) ? new Color( newRGB, true ) : color;
			};
		}
	}

	//---- class GraphicsFilter -----------------------------------------------

	private static class GraphicsFilter
		extends Graphics2DProxy
	{
		private final ColorFilter colorFilter;
		private final ColorFilter globalColorFilter;
		private final RGBImageFilter grayFilter;

		GraphicsFilter( Graphics2D delegate, ColorFilter colorFilter,
			ColorFilter globalColorFilter, RGBImageFilter grayFilter )
		{
			super( delegate );
			this.colorFilter = colorFilter;
			this.globalColorFilter = globalColorFilter;
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
			if( colorFilter != null ) {
				Color newColor = colorFilter.filter( color );
				color = (newColor != color)
					? newColor
					: globalColorFilter.filter( color );
			} else
				color = globalColorFilter.filter( color );

			if( grayFilter != null ) {
				int oldRGB = color.getRGB();
				int newRGB = grayFilter.filterRGB( 0, 0, oldRGB );
				color = (newRGB != oldRGB) ? new Color( newRGB, true ) : color;
			}
			return color;
		}
	}
}
