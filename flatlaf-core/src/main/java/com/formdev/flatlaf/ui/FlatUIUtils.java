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

package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.UIResource;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * Utility methods for UI delegates.
 *
 * @author Karl Tauber
 */
public class FlatUIUtils
{
	public static final boolean MAC_USE_QUARTZ = Boolean.getBoolean( "apple.awt.graphics.UseQuartz" );

	public static Rectangle addInsets( Rectangle r, Insets insets ) {
		return new Rectangle(
			r.x - insets.left,
			r.y - insets.top,
			r.width + insets.left + insets.right,
			r.height + insets.top + insets.bottom );
	}

	public static Rectangle subtractInsets( Rectangle r, Insets insets ) {
		return new Rectangle(
			r.x + insets.left,
			r.y + insets.top,
			r.width - insets.left - insets.right,
			r.height - insets.top - insets.bottom );
	}

	public static Dimension addInsets( Dimension dim, Insets insets ) {
		return new Dimension(
			dim.width + insets.left + insets.right,
			dim.height + insets.top + insets.bottom );
	}

	public static Insets addInsets( Insets insets1, Insets insets2 ) {
		return new Insets(
			insets1.top + insets2.top,
			insets1.left + insets2.left,
			insets1.bottom + insets2.bottom,
			insets1.right + insets2.right );
	}

	public static void setInsets( Insets dest, Insets src ) {
		dest.top = src.top;
		dest.left = src.left;
		dest.bottom = src.bottom;
		dest.right = src.right;
	}

	public static Color getUIColor( String key, int defaultColorRGB ) {
		Color color = UIManager.getColor( key );
		return (color != null) ? color : new Color( defaultColorRGB );
	}

	public static Color getUIColor( String key, Color defaultColor ) {
		Color color = UIManager.getColor( key );
		return (color != null) ? color : defaultColor;
	}

	public static Color getUIColor( String key, String defaultKey ) {
		Color color = UIManager.getColor( key );
		return (color != null) ? color : UIManager.getColor( defaultKey );
	}

	public static int getUIInt( String key, int defaultValue ) {
		Object value = UIManager.get( key );
		return (value instanceof Integer) ? (Integer) value : defaultValue;
	}

	public static float getUIFloat( String key, float defaultValue ) {
		Object value = UIManager.get( key );
		return (value instanceof Number) ? ((Number)value).floatValue() : defaultValue;
	}

	public static Color nonUIResource( Color c ) {
		return (c instanceof UIResource) ? new Color( c.getRGB(), true ) : c;
	}

	public static Font nonUIResource( Font font ) {
		return (font instanceof UIResource) ? font.deriveFont( font.getStyle() ) : font;
	}

	public static int minimumWidth( JComponent c, int minimumWidth ) {
		return FlatClientProperties.clientPropertyInt( c, FlatClientProperties.MINIMUM_WIDTH, minimumWidth );
	}

	public static int minimumHeight( JComponent c, int minimumHeight ) {
		return FlatClientProperties.clientPropertyInt( c, FlatClientProperties.MINIMUM_HEIGHT, minimumHeight );
	}

	public static boolean isTableCellEditor( Component c ) {
		return c instanceof JComponent && Boolean.TRUE.equals( ((JComponent)c).getClientProperty( "JComboBox.isTableCellEditor" ) );
	}

	public static boolean isPermanentFocusOwner( Component c ) {
		return (KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner() == c);
	}

	public static boolean isRoundRect( Component c ) {
		return c instanceof JComponent && FlatClientProperties.clientPropertyBoolean(
			(JComponent) c, FlatClientProperties.COMPONENT_ROUND_RECT, false );
	}

	/**
	 * Returns the scaled thickness of the outer focus border for the given component.
	 */
	public static float getBorderFocusWidth( JComponent c ) {
		FlatBorder border = getOutsideFlatBorder( c );
		return (border != null)
			? UIScale.scale( (float) border.getFocusWidth( c ) )
			: 0;
	}

	/**
	 * Returns the scaled arc diameter of the border for the given component.
	 */
	public static float getBorderArc( JComponent c ) {
		FlatBorder border = getOutsideFlatBorder( c );
		return (border != null)
			? UIScale.scale( (float) border.getArc( c ) )
			: 0;
	}

	public static boolean hasRoundBorder( JComponent c ) {
		return getBorderArc( c ) >= c.getHeight();
	}

	public static FlatBorder getOutsideFlatBorder( JComponent c ) {
		Border border = c.getBorder();
		for(;;) {
			if( border instanceof FlatBorder )
				return (FlatBorder) border;
			else if( border instanceof CompoundBorder )
				border = ((CompoundBorder)border).getOutsideBorder();
			else
				return null;
		}
	}

	/**
	 * Sets rendering hints used for painting.
	 */
	public static void setRenderingHints( Graphics2D g ) {
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,
			MAC_USE_QUARTZ ? RenderingHints.VALUE_STROKE_PURE : RenderingHints.VALUE_STROKE_NORMALIZE );
	}

	public static Color deriveColor( Color color, Color baseColor ) {
		return (color instanceof DerivedColor)
			? ((DerivedColor)color).derive( baseColor )
			: color;
	}

	/**
	 * Paints an outer border, which is usually a focus border.
	 * <p>
	 * The outside bounds of the painted border are {@code x,y,width,height}.
	 * The line width of the painted border is {@code focusWidth + lineWidth}.
	 * The given arc diameter refers to the inner rectangle ({@code x,y,width,height} minus {@code focusWidth}).
	 *
	 * @see #paintComponentBorder
	 * @see #paintComponentBackground
	 */
	public static void paintComponentOuterBorder( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float lineWidth, float arc )
	{
		double systemScaleFactor = UIScale.getSystemScaleFactor( g );
		if( systemScaleFactor != 1 && systemScaleFactor != 2 ) {
			// paint at scale 1x to avoid clipping on right and bottom edges at 125%, 150% or 175%
			HiDPIUtils.paintAtScale1x( g, x, y, width, height,
				(g2d, x2, y2, width2, height2, scaleFactor) -> {
					paintComponentOuterBorderImpl( g2d, x2, y2, width2, height2,
						(float) (focusWidth * scaleFactor), (float) (lineWidth * scaleFactor), (float) (arc * scaleFactor) );
				} );
			return;
		}

		paintComponentOuterBorderImpl( g, x, y, width, height, focusWidth, lineWidth, arc );
	}

	private static void paintComponentOuterBorderImpl( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float lineWidth, float arc )
	{
		float ow = focusWidth + lineWidth;
		float outerArc = arc + (focusWidth * 2);
		float innerArc = arc - (lineWidth * 2);

		// reduce outer arc slightly for small arcs to make the curve slightly wider
		if( arc > 0 && arc < UIScale.scale( 10 ) )
			outerArc -= UIScale.scale( 2f );

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( createComponentRectangle( x, y, width, height, outerArc ), false );
		path.append( createComponentRectangle( x + ow, y + ow, width - (ow * 2), height - (ow * 2), innerArc ), false );
		g.fill( path );
	}

	/**
	 * Draws the border of a component as round rectangle.
	 * <p>
	 * The outside bounds of the painted border are
	 * {@code x + focusWidth, y + focusWidth, width - (focusWidth * 2), height - (focusWidth * 2)}.
	 * The given arc diameter refers to the painted rectangle (and not to {@code x,y,width,height}).
	 *
	 * @see #paintComponentOuterBorder
	 * @see #paintComponentBackground
	 */
	public static void paintComponentBorder( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float lineWidth, float arc )
	{
		double systemScaleFactor = UIScale.getSystemScaleFactor( g );
		if( systemScaleFactor != 1 && systemScaleFactor != 2 ) {
			// paint at scale 1x to avoid clipping on right and bottom edges at 125%, 150% or 175%
			HiDPIUtils.paintAtScale1x( g, x, y, width, height,
				(g2d, x2, y2, width2, height2, scaleFactor) -> {
					paintComponentBorderImpl( g2d, x2, y2, width2, height2,
						(float) (focusWidth * scaleFactor), (float) (lineWidth * scaleFactor), (float) (arc * scaleFactor) );
				} );
			return;
		}

		paintComponentBorderImpl( g, x, y, width, height, focusWidth, lineWidth, arc );
	}

	private static void paintComponentBorderImpl( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float lineWidth, float arc )
	{
		float x1 = x + focusWidth;
		float y1 = y + focusWidth;
		float width1 = width - focusWidth * 2;
		float height1 = height - focusWidth * 2;
		float arc2 = arc - (lineWidth * 2);

		Shape r1 = createComponentRectangle( x1, y1, width1, height1, arc );
		Shape r2 = createComponentRectangle(
			x1 + lineWidth, y1 + lineWidth,
			width1 - lineWidth * 2, height1 - lineWidth * 2, arc2 );

		Path2D border = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		border.append( r1, false );
		border.append( r2, false );
		g.fill( border );
	}

	/**
	 * Fills the background of a component with a round rectangle.
	 * <p>
	 * The bounds of the painted round rectangle are
	 * {@code x + focusWidth, y + focusWidth, width - (focusWidth * 2), height - (focusWidth * 2)}.
	 * The given arc diameter refers to the painted rectangle (and not to {@code x,y,width,height}).
	 *
	 * @see #paintComponentOuterBorder
	 * @see #paintComponentBorder
	 */
	public static void paintComponentBackground( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float arc )
	{
		double systemScaleFactor = UIScale.getSystemScaleFactor( g );
		if( systemScaleFactor != 1 && systemScaleFactor != 2 ) {
			// paint at scale 1x to avoid clipping on right and bottom edges at 125%, 150% or 175%
			HiDPIUtils.paintAtScale1x( g, x, y, width, height,
				(g2d, x2, y2, width2, height2, scaleFactor) -> {
					paintComponentBackgroundImpl( g2d, x2, y2, width2, height2,
						(float) (focusWidth * scaleFactor), (float) (arc * scaleFactor) );
				} );
			return;
		}

		paintComponentBackgroundImpl( g, x, y, width, height, focusWidth, arc );
	}

	private static void paintComponentBackgroundImpl( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float arc )
	{
		g.fill( createComponentRectangle(
			x + focusWidth, y + focusWidth,
			width - focusWidth * 2, height - focusWidth * 2, arc ) );
	}

	/**
	 * Creates a (rounded) rectangle used to paint components (border, background, etc).
	 * The given arc diameter is limited to min(width,height).
	 */
	public static Shape createComponentRectangle( float x, float y, float w, float h, float arc ) {
		if( arc <= 0 )
			return new Rectangle2D.Float( x, y, w, h );

		arc = Math.min( arc, Math.min( w, h ) );
		return new RoundRectangle2D.Float( x, y, w, h, arc, arc );
	}

	/**
	 * Fill background with parent's background color because the visible component
	 * is smaller than its bounds (for the focus decoration).
	 */
	public static void paintParentBackground( Graphics g, JComponent c ) {
		Container parent = findOpaqueParent( c );
		if( parent != null ) {
			g.setColor( parent.getBackground() );
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
		}
	}

	/**
	 * Gets the background color of the first opaque parent.
	 */
	public static Color getParentBackground( JComponent c ) {
		Container parent = findOpaqueParent( c );
		return (parent != null)
			? parent.getBackground()
			: UIManager.getColor( "Panel.background" ); // fallback, probably never used
	}

	/**
	 * Find the first parent that is opaque.
	 */
	private static Container findOpaqueParent( Container c ) {
		while( (c = c.getParent()) != null ) {
			if( c.isOpaque() )
				return c;
		}
		return null;
	}

	/**
	 * Creates a not-filled rectangle shape with the given line width.
	 */
	public static Path2D createRectangle( float x, float y, float width, float height, float lineWidth ) {
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new Rectangle2D.Float( x, y, width, height ), false );
		path.append( new Rectangle2D.Float( x + lineWidth, y + lineWidth,
			width - (lineWidth * 2), height - (lineWidth * 2) ), false );
		return path;
	}

	/**
	 * Creates a not-filled rounded rectangle shape and allows specifying the line width and the radius or each corner.
	 */
	public static Path2D createRoundRectangle( float x, float y, float width, float height,
		float lineWidth, float arcTopLeft, float arcTopRight, float arcBottomLeft, float arcBottomRight )
	{
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( createRoundRectanglePath( x, y, width, height, arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight ), false );
		path.append( createRoundRectanglePath( x + lineWidth, y + lineWidth, width - (lineWidth * 2), height - (lineWidth * 2),
			arcTopLeft - lineWidth, arcTopRight - lineWidth, arcBottomLeft - lineWidth, arcBottomRight - lineWidth ), false );
		return path;
	}

	/**
	 * Creates a filled rounded rectangle shape and allows specifying the radius of each corner.
	 */
	public static Shape createRoundRectanglePath( float x, float y, float width, float height,
		float arcTopLeft, float arcTopRight, float arcBottomLeft, float arcBottomRight )
	{
		if( arcTopLeft <= 0 && arcTopRight <= 0 && arcBottomLeft <= 0 && arcBottomRight <= 0 )
			return new Rectangle2D.Float( x, y, width, height );

		// limit arcs to min(width,height)
		float maxArc = Math.min( width, height ) / 2;
		arcTopLeft = (arcTopLeft > 0) ? Math.min( arcTopLeft, maxArc ) : 0;
		arcTopRight = (arcTopRight > 0) ? Math.min( arcTopRight, maxArc ) : 0;
		arcBottomLeft = (arcBottomLeft > 0) ? Math.min( arcBottomLeft, maxArc ) : 0;
		arcBottomRight = (arcBottomRight > 0) ? Math.min( arcBottomRight, maxArc ) : 0;

		float x2 = x + width;
		float y2 = y + height;

		Path2D rect = new Path2D.Float();
		rect.moveTo( x2 - arcTopRight, y );
		rect.quadTo( x2, y, x2, y + arcTopRight );
		rect.lineTo( x2, y2 - arcBottomRight );
		rect.quadTo( x2, y2, x2 - arcBottomRight, y2 );
		rect.lineTo( x + arcBottomLeft, y2 );
		rect.quadTo( x, y2, x, y2 - arcBottomLeft );
		rect.lineTo( x, y + arcTopLeft );
		rect.quadTo( x, y, x + arcTopLeft, y );
		rect.closePath();

		return rect;
	}

	/**
	 * Creates a closed path for the given points.
	 */
	public static Path2D createPath( double... points ) {
		return createPath( true, points );
	}

	/**
	 * Creates a open or closed path for the given points.
	 */
	public static Path2D createPath( boolean close, double... points ) {
		Path2D path = new Path2D.Float();
		path.moveTo( points[0], points[1] );
		for( int i = 2; i < points.length; i += 2 )
			path.lineTo( points[i], points[i + 1] );
		if( close )
			path.closePath();
		return path;
	}

	/**
	 * Draws the given string at the specified location.
	 * The provided component is used to query text properties and anti-aliasing hints.
	 * <p>
	 * Use this method instead of {@link Graphics#drawString(String, int, int)} for correct anti-aliasing.
	 * <p>
	 * Replacement for {@code SwingUtilities2.drawString()}.
	 * Uses {@link HiDPIUtils#drawStringWithYCorrection(JComponent, Graphics2D, String, int, int)}.
	 */
	public static void drawString( JComponent c, Graphics g, String text, int x, int y ) {
		HiDPIUtils.drawStringWithYCorrection( c, (Graphics2D) g, text, x, y );
	}

	/**
	 * Draws the given string at the specified location underlining the specified character.
	 * The provided component is used to query text properties and anti-aliasing hints.
	 * <p>
	 * Replacement for {@code SwingUtilities2.drawStringUnderlineCharAt()}.
	 * Uses {@link HiDPIUtils#drawStringUnderlineCharAtWithYCorrection(JComponent, Graphics2D, String, int, int, int)}.
	 */
	public static void drawStringUnderlineCharAt( JComponent c, Graphics g,
		String text, int underlinedIndex, int x, int y )
	{
		// scale underline height if necessary
		if( underlinedIndex >= 0 && UIScale.getUserScaleFactor() > 1 ) {
			g = new Graphics2DProxy( (Graphics2D) g ) {
				@Override
				public void fillRect( int x, int y, int width, int height ) {
					if( height == 1 ) {
						// scale height and correct y position
						// (using 0.9f so that underline height is 1 at scale factor 1.5x)
						height = Math.round( UIScale.scale( 0.9f ) );
						y += height - 1;
					}

					super.fillRect( x, y, width, height );
				}
			};
		}

		HiDPIUtils.drawStringUnderlineCharAtWithYCorrection( c, (Graphics2D) g, text, underlinedIndex, x, y );
	}

	public static boolean hasOpaqueBeenExplicitlySet( JComponent c ) {
		boolean oldOpaque = c.isOpaque();
		LookAndFeel.installProperty( c, "opaque", !oldOpaque );
		boolean explicitlySet = c.isOpaque() == oldOpaque;
		LookAndFeel.installProperty( c, "opaque", oldOpaque );
		return explicitlySet;
	}

	//---- class HoverListener ------------------------------------------------

	public static class HoverListener
		extends MouseAdapter
	{
		private final Component repaintComponent;
		private final Consumer<Boolean> hoverChanged;

		public HoverListener( Component repaintComponent, Consumer<Boolean> hoverChanged ) {
			this.repaintComponent = repaintComponent;
			this.hoverChanged = hoverChanged;
		}

		@Override
		public void mouseEntered( MouseEvent e ) {
			hoverChanged.accept( true );
			repaint();
		}

		@Override
		public void mouseExited( MouseEvent e ) {
			hoverChanged.accept( false );
			repaint();
		}

		private void repaint() {
			if( repaintComponent != null && repaintComponent.isEnabled() )
				repaintComponent.repaint();
		}
	}

	//---- class RepaintFocusListener -----------------------------------------

	public static class RepaintFocusListener
		implements FocusListener
	{
		private final Component repaintComponent;

		public RepaintFocusListener( Component repaintComponent ) {
			this.repaintComponent = repaintComponent;
		}

		@Override
		public void focusGained( FocusEvent e ) {
			repaintComponent.repaint();
		}

		@Override
		public void focusLost( FocusEvent e ) {
			repaintComponent.repaint();
		}
	}
}
