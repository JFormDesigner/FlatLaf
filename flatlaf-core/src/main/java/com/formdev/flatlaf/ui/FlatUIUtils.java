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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
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
import javax.swing.plaf.ColorUIResource;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.JavaCompatibility;
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

	public static Color nonUIResource( Color c ) {
		return (c instanceof ColorUIResource) ? new Color( c.getRGB(), true ) : c;
	}

	public static boolean isTableCellEditor( Component c ) {
		return c instanceof JComponent && Boolean.TRUE.equals( ((JComponent)c).getClientProperty( "JComboBox.isTableCellEditor" ) );
	}

	/**
	 * Sets rendering hints used for painting.
	 */
	public static void setRenderingHints( Graphics2D g ) {
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,
			MAC_USE_QUARTZ ? RenderingHints.VALUE_STROKE_PURE : RenderingHints.VALUE_STROKE_NORMALIZE );
	}

	public static void setColor( Graphics g, Color color, Color baseColor ) {
		if( color instanceof DerivedColor )
			color = ((DerivedColor)color).derive( baseColor );
		g.setColor( color );
	}

	/**
	 * Draws a round rectangle.
	 */
	public static void drawRoundRectangle( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float lineWidth, float arc )
	{
		float arc2 = arc > lineWidth ? arc - lineWidth : 0f;

		RoundRectangle2D.Float r1 = new RoundRectangle2D.Float(
			x + focusWidth, y + focusWidth,
			width - focusWidth * 2, height - focusWidth * 2, arc, arc );
		RoundRectangle2D.Float r2 = new RoundRectangle2D.Float(
			r1.x + lineWidth, r1.y + lineWidth,
			r1.width - lineWidth * 2, r1.height - lineWidth * 2, arc2, arc2 );

		Path2D border = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		border.append( r1, false );
		border.append( r2, false );
		g.fill( border );
	}

	/**
	 * Fills a round rectangle.
	 */
	public static void fillRoundRectangle( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float arc )
	{
		g.fill( new RoundRectangle2D.Float(
			x + focusWidth, y + focusWidth,
			width - focusWidth * 2, height - focusWidth * 2, arc, arc ) );
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
	 * Paints an outline border.
	 */
	public static void paintOutlineBorder( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float lineWidth, float arc )
	{
		float outerArc = (arc > 0) ? arc + focusWidth - UIScale.scale( 2f ) : focusWidth;
		float ow = focusWidth + lineWidth;

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( createOutlinePath( x, y, width, height, outerArc ), false );
		path.append( createOutlinePath( x + ow, y + ow, width - (ow * 2), height - (ow * 2), outerArc - ow ), false );
		g.fill( path );
	}

	private static Shape createOutlinePath( float x, float y, float width, float height, float arc ) {
		return createRoundRectanglePath( x, y, width, height, arc, arc, arc, arc );
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
	 * Creates a filled rounded rectangle shape and allows specifying the radius or each corner.
	 */
	public static Shape createRoundRectanglePath( float x, float y, float width, float height,
		float arcTopLeft, float arcTopRight, float arcBottomLeft, float arcBottomRight )
	{
		if( arcTopLeft <= 0 && arcTopRight <= 0 && arcBottomLeft <= 0 && arcBottomRight <= 0 )
			return new Rectangle2D.Float( x, y, width, height );

		if( arcTopLeft < 0 )
			arcTopLeft = 0;
		if( arcTopRight < 0 )
			arcTopRight = 0;
		if( arcBottomLeft < 0 )
			arcBottomLeft = 0;
		if( arcBottomRight < 0 )
			arcBottomRight = 0;

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

	public static Path2D createPath( double... points ) {
		return createPath( true, points );
	}

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
	 * Draws the given string at the specified location using text properties
	 * and anti-aliasing hints from the provided component.
	 *
	 * Use this method instead of Graphics.drawString() for correct anti-aliasing.
	 *
	 * Replacement for SwingUtilities2.drawString()
	 */
	public static void drawString( JComponent c, Graphics g, String text, int x, int y ) {
		JavaCompatibility.drawStringUnderlineCharAt( c, g, text, -1, x, y );
	}

	/**
	 * Draws the given string at the specified location underlining the specified
	 * character. The provided component is used to query text properties and
	 * anti-aliasing hints.
	 *
	 * Replacement for SwingUtilities2.drawStringUnderlineCharAt()
	 */
	public static void drawStringUnderlineCharAt( JComponent c, Graphics g,
		String text, int underlinedIndex, int x, int y )
	{
		JavaCompatibility.drawStringUnderlineCharAt( c, g, text, underlinedIndex, x, y );
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
