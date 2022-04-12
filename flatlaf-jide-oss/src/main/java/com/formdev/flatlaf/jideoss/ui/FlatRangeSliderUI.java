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

package com.formdev.flatlaf.jideoss.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import com.formdev.flatlaf.ui.FlatSliderUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.basic.BasicRangeSliderUI;

/**
 * Provides the Flat LaF UI delegate for {@link com.jidesoft.swing.RangeSlider}.
 */
public class FlatRangeSliderUI
	extends BasicRangeSliderUI
{
	protected int trackWidth;
	protected Dimension thumbSize;
	protected int focusWidth;
	/** @since 2 */ protected float thumbBorderWidth;

	protected Color trackValueColor;
	protected Color trackColor;
	protected Color thumbColor;
	protected Color thumbBorderColor;
	protected Color focusBaseColor;
	protected Color focusedColor;
	protected Color focusedThumbBorderColor;
	protected Color hoverTrackColor;
	protected Color hoverThumbColor;
	protected Color pressedTrackColor;
	protected Color pressedThumbColor;
	protected Color disabledTrackColor;
	protected Color disabledThumbColor;
	protected Color disabledThumbBorderColor;

	private Color defaultBackground;
	private Color defaultForeground;

	private Object[] oldRenderingHints;

	public static ComponentUI createUI( JComponent c ) {
		// usually JIDE would invoke this in RangeSlider.updateUI(),
		// but it does not because FlatLaf already has added the UI class to the UI defaults
		LookAndFeelFactory.installJideExtension();

		return new FlatRangeSliderUI();
	}

	public FlatRangeSliderUI() {
		super( null );
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		// update label UIs, which is necessary because RangeSlider does not invoke JSlider.updateLabelUIs()
		updateLabelUIs( c );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		// update label UIs also on uninstall to avoid light labels when switching
		// from dark FlatLaf theme to another Laf
		updateLabelUIs( c );

		super.uninstallUI( c );
	}

	protected void updateLabelUIs( JComponent c ) {
		Dictionary<?,?> labelTable = ((JSlider)c).getLabelTable();
		if( labelTable == null )
			return;

		Enumeration<?> e = labelTable.elements();
		while( e.hasMoreElements() ) {
			JComponent label = (JComponent) e.nextElement();
			label.updateUI();
			label.setSize( label.getPreferredSize() );
		}
	}

	@Override
	protected void installDefaults( JSlider slider ) {
		super.installDefaults( slider );

		LookAndFeel.installProperty( slider, "opaque", false );

		trackWidth = UIManager.getInt( "Slider.trackWidth" );
		thumbSize = UIManager.getDimension( "Slider.thumbSize" );
		focusWidth = FlatUIUtils.getUIInt( "Slider.focusWidth", 4 );
		thumbBorderWidth = FlatUIUtils.getUIFloat( "Slider.thumbBorderWidth", 1 );

		trackValueColor = FlatUIUtils.getUIColor( "Slider.trackValueColor", "Slider.thumbColor" );
		trackColor = UIManager.getColor( "Slider.trackColor" );
		thumbColor = UIManager.getColor( "Slider.thumbColor" );
		thumbBorderColor = UIManager.getColor( "Slider.thumbBorderColor" );
		focusBaseColor = UIManager.getColor( "Component.focusColor" );
		focusedColor = FlatUIUtils.getUIColor( "Slider.focusedColor", focusBaseColor );
		focusedThumbBorderColor = FlatUIUtils.getUIColor( "Slider.focusedThumbBorderColor", "Component.focusedBorderColor" );
		hoverTrackColor = FlatUIUtils.getUIColor( "Slider.hoverTrackColor", "Slider.hoverThumbColor" );
		hoverThumbColor = UIManager.getColor( "Slider.hoverThumbColor" );
		pressedTrackColor = FlatUIUtils.getUIColor( "Slider.pressedTrackColor", "Slider.pressedThumbColor" );
		pressedThumbColor = UIManager.getColor( "Slider.pressedThumbColor" );
		disabledTrackColor = UIManager.getColor( "Slider.disabledTrackColor" );
		disabledThumbColor = UIManager.getColor( "Slider.disabledThumbColor" );
		disabledThumbBorderColor = FlatUIUtils.getUIColor( "Slider.disabledThumbBorderColor", "Component.disabledBorderColor" );

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
		hoverTrackColor = null;
		hoverThumbColor = null;
		pressedTrackColor = null;
		pressedThumbColor = null;
		disabledTrackColor = null;
		disabledThumbColor = null;
		disabledThumbBorderColor = null;

		defaultBackground = null;
		defaultForeground = null;
	}

	@Override
	protected TrackListener createTrackListener( JSlider slider ) {
		return new FlatRangeTrackListener( super.createTrackListener( slider ) );
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

		// compute a baseline so that the track is vertically centered
		FontMetrics fm = slider.getFontMetrics( slider.getFont() );
		return trackRect.y + Math.round( (trackRect.height - fm.getHeight()) / 2f ) + fm.getAscent() - 1;
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
		return FlatSliderUI.calcThumbSize( slider, thumbSize, focusWidth );
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
		Point p = adjustThumbForHighValue();
		g.drawRect( thumbRect.x, thumbRect.y, thumbRect.width - 1, thumbRect.height - 1 );
		restoreThumbForLowValue( p );
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

		// get rectangle of second thumb
		Point p = adjustThumbForHighValue();
		Rectangle thumbRect2 = new Rectangle( thumbRect );
		restoreThumbForLowValue( p );

		RoundRectangle2D coloredTrack = null;
		RoundRectangle2D track;
		if( slider.getOrientation() == JSlider.HORIZONTAL ) {
			float y = trackRect.y + (trackRect.height - tw) / 2f;
			if( enabled ) {
				Rectangle thumbRect1 = thumbRect;
				if( !slider.getComponentOrientation().isLeftToRight() ) {
					Rectangle temp = thumbRect1;
					thumbRect1 = thumbRect2;
					thumbRect2 = temp;
				}

				int cx = thumbRect1.x + (thumbRect1.width / 2);
				int cw = thumbRect2.x - thumbRect1.x;
				coloredTrack = new RoundRectangle2D.Float( cx, y, cw, tw, arc, arc );
			}
			track = new RoundRectangle2D.Float( trackRect.x, y, trackRect.width, tw, arc, arc );
		} else {
			float x = trackRect.x + (trackRect.width - tw) / 2f;
			if( enabled ) {
				int cy = thumbRect2.y + (thumbRect2.height / 2);
				int ch = thumbRect.y - thumbRect2.y;
				coloredTrack = new RoundRectangle2D.Float( x, cy, tw, ch, arc, arc );
			}
			track = new RoundRectangle2D.Float( x, trackRect.y, tw, trackRect.height, arc, arc );
		}

		g.setColor( enabled ? getTrackColor() : disabledTrackColor );
		((Graphics2D)g).fill( track );

		if( coloredTrack != null ) {
			boolean trackHover = hover && rollover1 && rollover2;
			boolean trackPressed = pressed1 && pressed2;

			Color trackValueColor = getTrackValueColor();
			Color color = FlatSliderUI.stateColor( slider, trackHover, trackPressed,
				trackValueColor, null, null, hoverTrackColor, pressedTrackColor );

			g.setColor( FlatUIUtils.deriveColor( color, trackValueColor ) );
			((Graphics2D)g).fill( coloredTrack );
		}
	}

	@Override
	public void paintThumb( Graphics g ) {
		boolean thumbHover = hover && ((!second && rollover1) || (second && rollover2));
		boolean thumbPressed = (!second && pressed1) || (second && pressed2);

		Color thumbColor = getThumbColor();
		Color color = FlatSliderUI.stateColor( slider, thumbHover, thumbPressed,
			thumbColor, disabledThumbColor, null, hoverThumbColor, pressedThumbColor );
		color = FlatUIUtils.deriveColor( color, thumbColor );

		Color foreground = slider.getForeground();
		Color borderColor = (thumbBorderColor != null && foreground == defaultForeground)
			? FlatSliderUI.stateColor( slider, false, false, thumbBorderColor, disabledThumbBorderColor, focusedThumbBorderColor, null, null )
			: null;

		Color focusedColor = FlatUIUtils.deriveColor( this.focusedColor,
			(foreground != defaultForeground) ? foreground : focusBaseColor );

		FlatSliderUI.paintThumb( g, slider, thumbRect, isRoundThumb(), color, borderColor, focusedColor, thumbBorderWidth, focusWidth );
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

	protected boolean isRoundThumb() {
		return !slider.getPaintTicks() && !slider.getPaintLabels();
	}

	//---- class FlatRangeTrackListener ---------------------------------------

	protected class FlatRangeTrackListener
		extends RangeTrackListener
	{
		public FlatRangeTrackListener( TrackListener listener ) {
			super( listener );
		}

		@Override
		public void mousePressed( MouseEvent e ) {
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
			int handle = getMouseHandle( x, y );

			// clicked on thumb --> let super class do the work
			if( handle != MOUSE_HANDLE_LOWER && handle != MOUSE_HANDLE_UPPER ) {
				super.mousePressed( e );
				return;
			}

			if( UIManager.getBoolean( "Slider.onlyLeftMouseButtonDrag" ) &&
				!SwingUtilities.isLeftMouseButton( e ) )
			  return;

			// get low or high thumb rectangle
			Rectangle thumbRect = FlatRangeSliderUI.this.thumbRect;
			if( handle == MOUSE_HANDLE_UPPER ) {
				Point p = adjustThumbForHighValue();
				thumbRect = new Rectangle( FlatRangeSliderUI.this.thumbRect );
				restoreThumbForLowValue( p );
			}

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
		}
	}
}
