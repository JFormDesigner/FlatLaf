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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
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
 * @uiDefault Slider.thumbWidth				int
 * @uiDefault Slider.trackColor				Color
 * @uiDefault Slider.thumbColor				Color
 * @uiDefault Slider.focusedColor			Color	optional; defaults to Component.focusColor
 * @uiDefault Slider.hoverColor				Color	optional; defaults to Slider.focusedColor
 * @uiDefault Slider.disabledForeground		Color	used for track and thumb is disabled
 *
 * @author Karl Tauber
 */
public class FlatSliderUI
	extends BasicSliderUI
{
	private int trackWidth;
	private int thumbWidth;

	private Color trackColor;
	private Color thumbColor;
	private Color focusColor;
	private Color hoverColor;
	private Color disabledForeground;

	private MouseListener hoverListener;
	private boolean hover;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatSliderUI();
	}

	public FlatSliderUI() {
		super( null );
	}

	@Override
	protected void installListeners( JSlider slider ) {
		super.installListeners( slider );

		hoverListener = new FlatUIUtils.HoverListener( slider, h -> {
			hover = h;
		} );
		slider.addMouseListener( hoverListener );
	}

	@Override
	protected void uninstallListeners( JSlider slider ) {
		super.uninstallListeners( slider );

		slider.removeMouseListener( hoverListener );
		hoverListener = null;
	}

	@Override
	protected void installDefaults( JSlider slider ) {
		super.installDefaults( slider );

		LookAndFeel.installProperty( slider, "opaque", false );

		trackWidth = UIManager.getInt( "Slider.trackWidth" );
		thumbWidth = UIManager.getInt( "Slider.thumbWidth" );

		trackColor = UIManager.getColor( "Slider.trackColor" );
		thumbColor = UIManager.getColor( "Slider.thumbColor" );
		focusColor = FlatUIUtils.getUIColor( "Slider.focusedColor", "Component.focusColor" );
		hoverColor = FlatUIUtils.getUIColor( "Slider.hoverColor", focusColor );
		disabledForeground = UIManager.getColor( "Slider.disabledForeground" );
	}

	@Override
	protected void uninstallDefaults( JSlider slider ) {
		super.uninstallDefaults( slider );

		trackColor = null;
		thumbColor = null;
		focusColor = null;
		hoverColor = null;
		disabledForeground = null;
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
		return new Dimension( UIScale.scale( thumbWidth ), UIScale.scale( thumbWidth ) );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		FlatUIUtils.setRenderingHints( (Graphics2D) g );

		super.paint( g, c );
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
			g.setColor( FlatUIUtils.deriveColor( FlatUIUtils.isPermanentFocusOwner( slider ) ? focusColor : (hover ? hoverColor : thumbColor), thumbColor ) );
			((Graphics2D)g).fill( coloredTrack );
		}

		g.setColor( enabled ? trackColor : disabledForeground );
		((Graphics2D)g).fill( track );
	}

	@Override
	public void paintThumb( Graphics g ) {
		g.setColor( FlatUIUtils.deriveColor( slider.isEnabled()
			? (FlatUIUtils.isPermanentFocusOwner( slider ) ? focusColor : (hover ? hoverColor : thumbColor))
			: disabledForeground,
			thumbColor ) );

		if( isRoundThumb() )
			g.fillOval( thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height );
		else {
			double w = thumbRect.width;
			double h = thumbRect.height;
			double wh = w / 2;

			Path2D thumb = FlatUIUtils.createPath( 0,0, w,0, w,(h - wh), wh,h,  0,(h - wh) );

			Graphics2D g2 = (Graphics2D) g.create();
			try {
				g2.translate( thumbRect.x, thumbRect.y );
				if( slider.getOrientation() == JSlider.VERTICAL ) {
					if( slider.getComponentOrientation().isLeftToRight() ) {
						g2.translate( 0, thumbRect.height );
						g2.rotate( Math.toRadians( 270 ) );
					} else {
						g2.translate( thumbRect.width, 0 );
						g2.rotate( Math.toRadians( 90 ) );
					}
				}
				g2.fill( thumb );
			} finally {
				g2.dispose();
			}
		}
	}

	private boolean isRoundThumb() {
		return !slider.getPaintTicks() && !slider.getPaintLabels();
	}
}
