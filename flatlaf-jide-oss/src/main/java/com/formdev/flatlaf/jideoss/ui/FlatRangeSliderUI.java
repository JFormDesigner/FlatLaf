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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import com.formdev.flatlaf.ui.FlatSliderUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.jidesoft.plaf.basic.BasicRangeSliderUI;

/**
 * Provides the Flat LaF UI delegate for {@link com.jidesoft.swing.RangeSlider}.
 */
public class FlatRangeSliderUI
	extends BasicRangeSliderUI
{
	protected int trackWidth;
	protected int thumbWidth;

	protected Color trackColor;
	protected Color thumbColor;
	protected Color focusColor;
	protected Color hoverTrackColor;
	protected Color hoverThumbColor;
	protected Color pressedTrackColor;
	protected Color pressedThumbColor;
	protected Color disabledTrackColor;
	protected Color disabledThumbColor;

	private Rectangle firstThumbRect;

	public static ComponentUI createUI( JComponent c ) {
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
		thumbWidth = UIManager.getInt( "Slider.thumbWidth" );

		trackColor = UIManager.getColor( "Slider.trackColor" );
		thumbColor = UIManager.getColor( "Slider.thumbColor" );
		focusColor = FlatUIUtils.getUIColor( "Slider.focusedColor", "Component.focusColor" );
		hoverTrackColor = FlatUIUtils.getUIColor( "Slider.hoverTrackColor", "Slider.hoverThumbColor" );
		hoverThumbColor = UIManager.getColor( "Slider.hoverThumbColor" );
		pressedTrackColor = FlatUIUtils.getUIColor( "Slider.pressedTrackColor", "Slider.pressedThumbColor" );
		pressedThumbColor = UIManager.getColor( "Slider.pressedThumbColor" );
		disabledTrackColor = UIManager.getColor( "Slider.disabledTrackColor" );
		disabledThumbColor = UIManager.getColor( "Slider.disabledThumbColor" );
	}

	@Override
	protected void uninstallDefaults( JSlider slider ) {
		super.uninstallDefaults( slider );

		trackColor = null;
		thumbColor = null;
		focusColor = null;
		hoverTrackColor = null;
		hoverThumbColor = null;
		pressedTrackColor = null;
		pressedThumbColor = null;
		disabledTrackColor = null;
		disabledThumbColor = null;
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

		second = false;
		super.paint( g, c );

		Rectangle clip = g.getClipBounds();

		firstThumbRect = new Rectangle( thumbRect );

		second = true;
		Point p = adjustThumbForHighValue();

		if( clip.intersects( thumbRect ) ) {
			paintTrack( g );
			paintThumb( g );
		}

		restoreThumbForLowValue( p );
		second = false;
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
			if( enabled ) {
				if( slider.getComponentOrientation().isLeftToRight() ) {
					int cw = thumbRect.x + (thumbRect.width / 2) - trackRect.x;
					if( second ) {
						track = new RoundRectangle2D.Float( trackRect.x + cw, y, trackRect.width - cw, tw, arc, arc );
						int firstCw = firstThumbRect.x + (firstThumbRect.width / 2) - trackRect.x;
						coloredTrack = new RoundRectangle2D.Float( trackRect.x + firstCw, y, cw - firstCw, tw, arc, arc );
					} else
						track = new RoundRectangle2D.Float( trackRect.x, y, cw, tw, arc, arc );
				} else {
					int cw = trackRect.x + trackRect.width - thumbRect.x - (thumbRect.width / 2);
					if( second ) {
						int firstCw = trackRect.x + trackRect.width - firstThumbRect.x - (firstThumbRect.width / 2);
						track = new RoundRectangle2D.Float( trackRect.x, y, trackRect.width - cw, tw, arc, arc );
						coloredTrack = new RoundRectangle2D.Float( trackRect.x + trackRect.width - cw, y, cw - firstCw, tw, arc, arc );
					} else
						track = new RoundRectangle2D.Float( trackRect.x + trackRect.width - cw, y, cw, tw, arc, arc );
				}
			} else
				track = new RoundRectangle2D.Float( trackRect.x, y, trackRect.width, tw, arc, arc );
		} else {
			float x = trackRect.x + (trackRect.width - tw) / 2f;
			if( enabled ) {
				int ch = thumbRect.y + (thumbRect.height / 2) - trackRect.y;
				if( second ) {
					int firstCh = firstThumbRect.y + (firstThumbRect.height / 2) - trackRect.y;
					track = new RoundRectangle2D.Float( x, trackRect.y, tw, ch, arc, arc );
					coloredTrack = new RoundRectangle2D.Float( x, trackRect.y + ch, tw, firstCh - ch, arc, arc );
				} else
					track = new RoundRectangle2D.Float( x, trackRect.y + ch, tw, trackRect.height - ch, arc, arc );
			} else
				track = new RoundRectangle2D.Float( x, trackRect.y, tw, trackRect.height, arc, arc );
		}

		if( coloredTrack != null ) {
			boolean trackHover = hover && rollover1 && rollover2;
			boolean trackPressed = pressed1 && pressed2;

			Color color = FlatSliderUI.stateColor( slider, trackHover, trackPressed,
				thumbColor, null, null, hoverTrackColor, pressedTrackColor );

			g.setColor( FlatUIUtils.deriveColor( color, thumbColor ) );
			((Graphics2D)g).fill( coloredTrack );
		}

		g.setColor( enabled ? trackColor : disabledTrackColor );
		((Graphics2D)g).fill( track );
	}

	@Override
	public void paintThumb( Graphics g ) {
		boolean thumbHover = hover && ((!second && rollover1) || (second && rollover2));
		boolean thumbPressed = (!second && pressed1) || (second && pressed2);

		Color color = FlatSliderUI.stateColor( slider, thumbHover, thumbPressed,
			thumbColor, disabledThumbColor, focusColor, hoverThumbColor, pressedThumbColor );
		color = FlatUIUtils.deriveColor( color, thumbColor );

		FlatSliderUI.paintThumb( g, slider, thumbRect, isRoundThumb(), color );
	}

	protected boolean isRoundThumb() {
		return !slider.getPaintTicks() && !slider.getPaintLabels();
	}
}
