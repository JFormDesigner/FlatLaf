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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JSlider}.
 *
 * <!-- BasicSliderUI -->
 *
 * @uiDefault Slider.font					Font
 * @uiDefault Slider.background				Color
 * @uiDefault Slider.foreground				Color	unused
 * @uiDefault Slider.tickColor				Color
 * @uiDefault Slider.horizontalSize			Dimension	preferred horizontal size; height is ignored; computed slider height is used
 * @uiDefault Slider.verticalSize			Dimension	preferred vertical size; width is ignored; computed slider width is used
 * @uiDefault Slider.minimumHorizontalSize	Dimension	height is ignored; computed slider height is used
 * @uiDefault Slider.minimumVerticalSize	Dimension	width is ignored; computed slider width is used
 * @uiDefault Slider.border					Border
 *
 * <!-- FlatSliderUI -->
 *
 * @uiDefault Slider.trackWidth				int
 * @uiDefault Slider.thumbSize				Dimension
 * @uiDefault Slider.focusWidth				int
 * @uiDefault Slider.thumbBorderWidth		int or float
 *
 * @uiDefault Slider.trackValueColor		Color	optional; defaults to Slider.thumbColor
 * @uiDefault Slider.trackColor				Color
 * @uiDefault Slider.thumbColor				Color
 * @uiDefault Slider.thumbBorderColor		Color	optional; if null, no border is painted
 * @uiDefault Slider.focusedColor			Color	optional; defaults to Component.focusColor
 * @uiDefault Slider.focusedThumbBorderColor Color	optional; defaults to Component.focusedBorderColor
 * @uiDefault Slider.hoverThumbColor		Color	optional
 * @uiDefault Slider.pressedThumbColor		Color	optional
 * @uiDefault Slider.disabledTrackColor		Color
 * @uiDefault Slider.disabledThumbColor		Color
 * @uiDefault Slider.disabledThumbBorderColor Color	optional; defaults to Component.disabledBorderColor
 *
 * @author Karl Tauber
 */
public class FlatSliderUI
	extends BasicSliderUI
	implements StyleableUI
{
	@Styleable protected int trackWidth;
	@Styleable protected Dimension thumbSize;
	@Styleable protected int focusWidth;
	/** @since 2 */ @Styleable protected float thumbBorderWidth;

	@Styleable protected Color trackValueColor;
	@Styleable protected Color trackColor;
	@Styleable protected Color thumbColor;
	@Styleable protected Color thumbBorderColor;
	protected Color focusBaseColor;
	@Styleable protected Color focusedColor;
	@Styleable protected Color focusedThumbBorderColor;
	@Styleable protected Color hoverThumbColor;
	@Styleable protected Color pressedThumbColor;
	@Styleable protected Color disabledTrackColor;
	@Styleable protected Color disabledThumbColor;
	@Styleable protected Color disabledThumbBorderColor;
	@Styleable protected Color tickColor;

	private Color defaultBackground;
	private Color defaultForeground;

	protected boolean thumbHover;
	protected boolean thumbPressed;

	private Object[] oldRenderingHints;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatSliderUI();
	}

	public FlatSliderUI() {
		super( null );
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults( JSlider slider ) {
		super.installDefaults( slider );

		LookAndFeel.installProperty( slider, "opaque", false );

		trackWidth = UIManager.getInt( "Slider.trackWidth" );
		thumbSize = UIManager.getDimension( "Slider.thumbSize" );
		if( thumbSize == null ) {
			// fallback for compatibility with old versions
			int thumbWidth = UIManager.getInt( "Slider.thumbWidth" );
			thumbSize = new Dimension( thumbWidth, thumbWidth );
		}
		focusWidth = FlatUIUtils.getUIInt( "Slider.focusWidth", 4 );
		thumbBorderWidth = FlatUIUtils.getUIFloat( "Slider.thumbBorderWidth", 1 );

		trackValueColor = FlatUIUtils.getUIColor( "Slider.trackValueColor", "Slider.thumbColor" );
		trackColor = UIManager.getColor( "Slider.trackColor" );
		thumbColor = UIManager.getColor( "Slider.thumbColor" );
		thumbBorderColor = UIManager.getColor( "Slider.thumbBorderColor" );
		focusBaseColor = UIManager.getColor( "Component.focusColor" );
		focusedColor = FlatUIUtils.getUIColor( "Slider.focusedColor", focusBaseColor );
		focusedThumbBorderColor = FlatUIUtils.getUIColor( "Slider.focusedThumbBorderColor", "Component.focusedBorderColor" );
		hoverThumbColor = UIManager.getColor( "Slider.hoverThumbColor" );
		pressedThumbColor = UIManager.getColor( "Slider.pressedThumbColor" );
		disabledTrackColor = UIManager.getColor( "Slider.disabledTrackColor" );
		disabledThumbColor = UIManager.getColor( "Slider.disabledThumbColor" );
		disabledThumbBorderColor = FlatUIUtils.getUIColor( "Slider.disabledThumbBorderColor", "Component.disabledBorderColor" );
		tickColor = FlatUIUtils.getUIColor( "Slider.tickColor", Color.BLACK ); // see BasicSliderUI.paintTicks()

		defaultBackground = UIManager.getColor( "Slider.background" );
		defaultForeground = UIManager.getColor( "Slider.foreground" );
	}

	@Override
	protected void uninstallDefaults( JSlider slider ) {
		super.uninstallDefaults( slider );

		trackValueColor = null;
		trackColor = null;
		thumbColor = null;
		thumbBorderColor = null;
		focusBaseColor = null;
		focusedColor = null;
		focusedThumbBorderColor = null;
		hoverThumbColor = null;
		pressedThumbColor = null;
		disabledTrackColor = null;
		disabledThumbColor = null;
		disabledThumbBorderColor = null;
		tickColor = null;

		defaultBackground = null;
		defaultForeground = null;

		oldStyleValues = null;
	}

	@Override
	protected TrackListener createTrackListener( JSlider slider ) {
		return new FlatTrackListener();
	}

	@Override
	protected FocusListener createFocusListener( JSlider slider ) {
		return new BasicSliderUI.FocusHandler() {
			@Override
			public void focusGained( FocusEvent e ) {
				super.focusGained( e );
				HiDPIUtils.repaint( slider );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				super.focusLost( e );
				HiDPIUtils.repaint( slider );
			}
		};
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener( JSlider slider ) {
		return FlatStylingSupport.createPropertyChangeListener( slider, this::installStyle,
			super.createPropertyChangeListener( slider ) );
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( slider, "Slider" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, slider, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	@Override
	public int getBaseline( JComponent c, int width, int height ) {
		if( c == null )
			throw new NullPointerException();
		if( width < 0 || height < 0 )
			throw new IllegalArgumentException();

		// no baseline for vertical orientation
		if( slider.getOrientation() == JSlider.VERTICAL )
			return -1;

		// use default font (instead of slider font) because the slider font size
		// may be different to label font size, but we want to align the track/thumb with labels
		Font font = UIManager.getFont( "defaultFont" );
		if( font == null )
			font = slider.getFont();
		FontMetrics fm = slider.getFontMetrics( font );

		// calculate track y coordinate and height
		// (not using field trackRect here because slider size may be [0,0]
		// and field trackRect may have invalid values in this case)
		Insets insets = slider.getInsets();
		int thumbHeight = getThumbSize().height;
		int contentHeight = height - insets.top - insets.bottom - focusInsets.top - focusInsets.bottom;
		int centerSpacing = thumbHeight
			+ (slider.getPaintTicks() ? getTickLength() : 0)
			+ (slider.getPaintLabels() ? getHeightOfTallestLabel() : 0);
		int trackY = insets.top + focusInsets.top + (contentHeight - centerSpacing - 1) / 2;
		int trackHeight = thumbHeight;

		// compute a baseline so that the track is vertically centered
		return trackY + Math.round( (trackHeight - fm.getHeight()) / 2f ) + fm.getAscent() - 1;
	}

	@Override
	public Dimension getPreferredHorizontalSize() {
		return UIScale.scale( super.getPreferredHorizontalSize() );
	}

	@Override
	public Dimension getPreferredVerticalSize() {
		return UIScale.scale( super.getPreferredVerticalSize() );
	}

	@Override
	public Dimension getMinimumHorizontalSize() {
		return UIScale.scale( super.getMinimumHorizontalSize() );
	}

	@Override
	public Dimension getMinimumVerticalSize() {
		return UIScale.scale( super.getMinimumVerticalSize() );
	}

	@Override
	protected int getTickLength() {
		return UIScale.scale( super.getTickLength() );
	}

	@Override
	protected Dimension getThumbSize() {
		return calcThumbSize( slider, thumbSize, focusWidth );
	}

	public static Dimension calcThumbSize( JSlider slider, Dimension thumbSize, int focusWidth ) {
		int fw = UIScale.scale( focusWidth );
		int w = UIScale.scale( thumbSize.width ) + fw + fw;
		int h = UIScale.scale( thumbSize.height ) + fw + fw;
		return (slider.getOrientation() == JSlider.HORIZONTAL)
			? new Dimension( w, h )
			: new Dimension( h, w );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		oldRenderingHints = FlatUIUtils.setRenderingHints( g );

/*debug
		g.setColor( Color.gray );
		g.drawRect( 0, 0, c.getWidth() - 1, c.getHeight() - 1 );
		g.setColor( Color.orange );
		g.drawRect( focusRect.x, focusRect.y, focusRect.width - 1, focusRect.height - 1 );
		g.setColor( Color.magenta );
		g.drawRect( contentRect.x, contentRect.y, contentRect.width - 1, contentRect.height - 1 );
		g.setColor( Color.blue );
		g.drawRect( trackRect.x, trackRect.y, trackRect.width - 1, trackRect.height - 1 );
		g.setColor( Color.red );
		g.drawRect( thumbRect.x, thumbRect.y, thumbRect.width - 1, thumbRect.height - 1 );
		g.setColor( Color.green );
		g.drawRect( tickRect.x, tickRect.y, tickRect.width - 1, tickRect.height - 1 );
		g.setColor( Color.red );
		g.drawRect( labelRect.x, labelRect.y, labelRect.width - 1, labelRect.height - 1 );
debug*/

		super.paint( g, c );

		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
		oldRenderingHints = null;
	}

	@Override
	public void paintLabels( Graphics g ) {
		FlatUIUtils.runWithoutRenderingHints( g, oldRenderingHints, () -> {
			super.paintLabels( g );
		} );
	}

	@Override
	public void paintFocus( Graphics g ) {
		// do not paint dashed focus rectangle
	}

	@Override
	public void paintTrack( Graphics g ) {
		boolean enabled = slider.isEnabled();
		float tw = UIScale.scale( (float) trackWidth );
		float arc = tw;

		RoundRectangle2D coloredTrack = null;
		RoundRectangle2D track;
		if( slider.getOrientation() == JSlider.HORIZONTAL ) {
			float y = trackRect.y + (trackRect.height - tw) / 2f;
			if( enabled && isRoundThumb() ) {
				if( slider.getComponentOrientation().isLeftToRight() ) {
					int cw = thumbRect.x + (thumbRect.width / 2) - trackRect.x;
					coloredTrack = new RoundRectangle2D.Float( trackRect.x, y, cw, tw, arc, arc );
					track = new RoundRectangle2D.Float( trackRect.x + cw, y, trackRect.width - cw, tw, arc, arc );
				} else {
					int cw = trackRect.x + trackRect.width - thumbRect.x - (thumbRect.width / 2);
					coloredTrack = new RoundRectangle2D.Float( trackRect.x + trackRect.width - cw, y, cw, tw, arc, arc );
					track = new RoundRectangle2D.Float( trackRect.x, y, trackRect.width - cw, tw, arc, arc );
				}
			} else
				track = new RoundRectangle2D.Float( trackRect.x, y, trackRect.width, tw, arc, arc );
		} else {
			float x = trackRect.x + (trackRect.width - tw) / 2f;
			if( enabled && isRoundThumb() ) {
				int ch = thumbRect.y + (thumbRect.height / 2) - trackRect.y;
				track = new RoundRectangle2D.Float( x, trackRect.y, tw, ch, arc, arc );
				coloredTrack = new RoundRectangle2D.Float( x, trackRect.y + ch, tw, trackRect.height - ch, arc, arc );
			} else
				track = new RoundRectangle2D.Float( x, trackRect.y, tw, trackRect.height, arc, arc );
		}

		if( coloredTrack != null ) {
			if( slider.getInverted() ) {
				RoundRectangle2D temp = track;
				track = coloredTrack;
				coloredTrack = temp;
			}

			g.setColor( getTrackValueColor() );
			((Graphics2D)g).fill( coloredTrack );
		}

		g.setColor( enabled ? getTrackColor() : disabledTrackColor );
		((Graphics2D)g).fill( track );
	}

	@Override
	public void paintTicks( Graphics g ) {
		// because BasicSliderUI.paintTicks() always uses
		//   g.setColor( UIManager.getColor("Slider.tickColor") )
		// we override this method and use our tickColor field to allow styling
		super.paintTicks( new Graphics2DProxy( (Graphics2D) g ) {
			@Override
			public void setColor( Color c ) {
				super.setColor( tickColor );
			}
		} );
	}

	@Override
	public void paintThumb( Graphics g ) {
		Color thumbColor = getThumbColor();
		Color color = stateColor( slider, thumbHover, thumbPressed,
			thumbColor, disabledThumbColor, null, hoverThumbColor, pressedThumbColor );
		color = FlatUIUtils.deriveColor( color, thumbColor );

		Color foreground = slider.getForeground();
		Color borderColor = (thumbBorderColor != null && foreground == defaultForeground)
			? stateColor( slider, false, false, thumbBorderColor, disabledThumbBorderColor, focusedThumbBorderColor, null, null )
			: null;

		Color focusedColor = FlatUIUtils.deriveColor( this.focusedColor,
			(foreground != defaultForeground) ? foreground : focusBaseColor );

		paintThumb( g, slider, thumbRect, isRoundThumb(), color, borderColor, focusedColor, thumbBorderWidth, focusWidth );
	}

	public static void paintThumb( Graphics g, JSlider slider, Rectangle thumbRect, boolean roundThumb,
		Color thumbColor, Color thumbBorderColor, Color focusedColor, float thumbBorderWidth, int focusWidth )
	{
		double systemScaleFactor = UIScale.getSystemScaleFactor( (Graphics2D) g );
		if( systemScaleFactor != (int) systemScaleFactor ) {
			// paint at scale 1x to avoid clipping on right and bottom edges at 125%, 150% or 175%
			HiDPIUtils.paintAtScale1x( (Graphics2D) g, thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height,
				(g2d, x2, y2, width2, height2, scaleFactor) -> {
					paintThumbImpl( g, slider, x2, y2, width2, height2,
						roundThumb, thumbColor, thumbBorderColor, focusedColor,
						(float) (thumbBorderWidth * scaleFactor),
						(float) (focusWidth * scaleFactor) );
				} );
			return;
		}

		paintThumbImpl( g, slider, thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height,
			roundThumb, thumbColor, thumbBorderColor, focusedColor, thumbBorderWidth, focusWidth );

	}

	private static void paintThumbImpl( Graphics g, JSlider slider, int x, int y, int width, int height,
		boolean roundThumb, Color thumbColor, Color thumbBorderColor, Color focusedColor,
		float thumbBorderWidth, float focusWidth )
	{
		int fw = Math.round( UIScale.scale( focusWidth ) );
		int tx = x + fw;
		int ty = y + fw;
		int tw = width - fw - fw;
		int th = height - fw - fw;
		boolean focused = FlatUIUtils.isPermanentFocusOwner( slider );

		if( roundThumb ) {
			// paint thumb focus border
			if( focused ) {
				g.setColor( focusedColor );
				((Graphics2D)g).fill( createRoundThumbShape( x, y, width, height ) );
			}

			if( thumbBorderColor != null ) {
				// paint thumb border
				g.setColor( thumbBorderColor );
				((Graphics2D)g).fill( createRoundThumbShape( tx, ty, tw, th ) );

				// paint thumb background
				float lw = UIScale.scale( thumbBorderWidth );
				g.setColor( thumbColor );
				((Graphics2D)g).fill( createRoundThumbShape( tx + lw, ty + lw,
					tw - lw - lw, th - lw - lw ) );
			} else {
				// paint thumb background
				g.setColor( thumbColor );
				((Graphics2D)g).fill( createRoundThumbShape( tx, ty, tw, th ) );
			}
		} else {
			Graphics2D g2 = (Graphics2D) g.create();
			try {
				g2.translate( x, y );
				if( slider.getOrientation() == JSlider.VERTICAL ) {
					if( slider.getComponentOrientation().isLeftToRight() ) {
						g2.translate( 0, height );
						g2.rotate( Math.toRadians( 270 ) );
					} else {
						g2.translate( width, 0 );
						g2.rotate( Math.toRadians( 90 ) );
					}

					// rotate thumb width/height
					int temp = tw;
					tw = th;
					th = temp;
				}

				// paint thumb focus border
				if( focused ) {
					g2.setColor( focusedColor );
					g2.fill( createDirectionalThumbShape( 0, 0,
						tw + fw + fw, th + fw + fw + (fw * 0.4142f), fw ) );
				}

				if( thumbBorderColor != null ) {
					// paint thumb border
					g2.setColor( thumbBorderColor );
					g2.fill( createDirectionalThumbShape( fw, fw, tw, th, 0 ) );

					// paint thumb background
					float lw = UIScale.scale( thumbBorderWidth );
					g2.setColor( thumbColor );
					g2.fill( createDirectionalThumbShape( fw + lw, fw + lw,
						tw - lw - lw, th - lw - lw - (lw * 0.4142f), 0 ) );
				} else {
					// paint thumb background
					g2.setColor( thumbColor );
					g2.fill( createDirectionalThumbShape( fw, fw, tw, th, 0 ) );
				}
			} finally {
				g2.dispose();
			}
		}
	}

	public static Shape createRoundThumbShape( float x, float y, float w, float h ) {
		if( w == h )
			return new Ellipse2D.Float( x, y, w, h );
		else {
			float arc = Math.min( w, h );
			return new RoundRectangle2D.Float( x, y, w, h, arc, arc );
		}
	}

	public static Shape createDirectionalThumbShape( float x, float y, float w, float h, float arc ) {
		float wh = w / 2;

		Path2D path = new Path2D.Float( Path2D.WIND_NON_ZERO, 9 );
		path.moveTo( x + wh, y + h );
		path.lineTo( x, y + (h - wh) );
		path.lineTo( x, y + arc );
		path.quadTo( x, y, x + arc, y );
		path.lineTo( x + (w - arc), y );
		path.quadTo( x + w, y, x + w, y + arc );
		path.lineTo( x + w, y + (h - wh) );
		path.closePath();

		return path;
	}

	protected Color getTrackValueColor() {
		Color foreground = slider.getForeground();
		return (foreground != defaultForeground) ? foreground : trackValueColor;
	}

	protected Color getTrackColor() {
		Color backround = slider.getBackground();
		return (backround != defaultBackground) ? backround : trackColor;
	}

	protected Color getThumbColor() {
		Color foreground = slider.getForeground();
		return (foreground != defaultForeground) ? foreground : thumbColor;
	}

	public static Color stateColor( JSlider slider, boolean hover, boolean pressed,
		Color enabledColor, Color disabledColor, Color focusedColor, Color hoverColor, Color pressedColor )
	{
		if( disabledColor != null && !slider.isEnabled() )
			return disabledColor;
		if( pressedColor != null && pressed )
			return pressedColor;
		if( hoverColor != null && hover )
			return hoverColor;
		if( focusedColor != null && FlatUIUtils.isPermanentFocusOwner( slider ) )
			return focusedColor;
		return enabledColor;
	}

	protected boolean isRoundThumb() {
		return !slider.getPaintTicks() && !slider.getPaintLabels();
	}

	@Override
	public void setThumbLocation( int x, int y ) {
		// set new thumb location and compute union of old and new thumb bounds
		Rectangle r = new Rectangle( thumbRect );
		thumbRect.setLocation( x, y );
		SwingUtilities.computeUnion( thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height, r );

		if( !isRoundThumb() ) {
			// the needle of the directional thumb is painted outside of thumbRect
			// --> must increase repaint rectangle

			// increase union rectangle for repaint
			int extra = (int) Math.ceil( UIScale.scale( focusWidth ) * 0.4142f );
			if( slider.getOrientation() == JSlider.HORIZONTAL )
				r.height += extra;
			else {
				r.width += extra;
				if( !slider.getComponentOrientation().isLeftToRight() )
					r.x -= extra;
			}
		}

		HiDPIUtils.repaint( slider, r );
	}

	//---- class FlatTrackListener --------------------------------------------

	protected class FlatTrackListener
		extends TrackListener
	{
		@Override
		public void mouseEntered( MouseEvent e ) {
			setThumbHover( isOverThumb( e ) );
			super.mouseEntered( e );
		}

		@Override
		public void mouseExited( MouseEvent e ) {
			setThumbHover( false );
			super.mouseExited( e );
		}

		@Override
		public void mouseMoved( MouseEvent e ) {
			setThumbHover( isOverThumb( e ) );
			super.mouseMoved( e );
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			setThumbPressed( isOverThumb( e ) );

			if( !slider.isEnabled() )
				return;

			// use "old" behavior when clicking on track
			if( UIManager.getBoolean( "Slider.scrollOnTrackClick" ) ) {
				super.mousePressed( e );
				return;
			}

			// "new" behavior set thumb to mouse location when clicking on track

			int x = e.getX();
			int y = e.getY();

			// clicked on thumb --> let super class do the work
			calculateGeometry();
			if( thumbRect.contains( x, y ) ) {
				super.mousePressed( e );
				return;
			}

			if( UIManager.getBoolean( "Slider.onlyLeftMouseButtonDrag" ) &&
				!SwingUtilities.isLeftMouseButton( e ) )
			  return;

			// move the mouse event coordinates to the center of the thumb
			int tx = thumbRect.x + (thumbRect.width / 2) - x;
			int ty = thumbRect.y + (thumbRect.height / 2) - y;
			e.translatePoint( tx, ty );

			// invoke super mousePressed() to start dragging thumb
			super.mousePressed( e );

			// move the mouse event coordinates back to current mouse location
			e.translatePoint( -tx, -ty );

			// invoke mouseDragged() to update thumb location
			mouseDragged( e );

			setThumbPressed( true );
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			setThumbPressed( false );
			super.mouseReleased( e );
		}

		@Override
		public void mouseDragged( MouseEvent e ) {
			super.mouseDragged( e );

			if( isDragging() &&
				slider.getSnapToTicks() &&
				slider.isEnabled() &&
				!UIManager.getBoolean( "Slider.snapToTicksOnReleased" ) )
			{
				calculateThumbLocation();
				HiDPIUtils.repaint( slider );
			}
		}

		protected void setThumbHover( boolean hover ) {
			if( hover != thumbHover ) {
				thumbHover = hover;
				HiDPIUtils.repaint( slider, thumbRect );
			}
		}

		protected void setThumbPressed( boolean pressed ) {
			if( pressed != thumbPressed ) {
				thumbPressed = pressed;
				HiDPIUtils.repaint( slider, thumbRect );
			}
		}

		protected boolean isOverThumb( MouseEvent e ) {
			return e != null && slider.isEnabled() && thumbRect.contains( e.getX(), e.getY() );
		}
	}
}
