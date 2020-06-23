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

import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JProgressBar}.
 *
 * <!-- BasicProgressBarUI -->
 *
 * @uiDefault ProgressBar.font						Font
 * @uiDefault ProgressBar.background				Color
 * @uiDefault ProgressBar.foreground				Color
 * @uiDefault ProgressBar.selectionBackground		Color
 * @uiDefault ProgressBar.selectionForeground		Color
 * @uiDefault ProgressBar.border					Border
 * @uiDefault ProgressBar.horizontalSize			Dimension	default is 146,12
 * @uiDefault ProgressBar.verticalSize				Dimension	default is 12,146
 * @uiDefault ProgressBar.repaintInterval			int		default is 50 milliseconds
 * @uiDefault ProgressBar.cycleTime					int		default is 3000 milliseconds
 *
 * <!-- FlatProgressBarUI -->
 *
 * @uiDefault ProgressBar.arc						int
 *
 * @author Karl Tauber
 */
public class FlatProgressBarUI
	extends BasicProgressBarUI
{
	protected int arc;
	protected Dimension horizontalSize;
	protected Dimension verticalSize;

	private PropertyChangeListener propertyChangeListener;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatProgressBarUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installProperty( progressBar, "opaque", false );

		arc = UIManager.getInt( "ProgressBar.arc" );
		horizontalSize = UIManager.getDimension( "ProgressBar.horizontalSize" );
		verticalSize = UIManager.getDimension( "ProgressBar.verticalSize" );
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		propertyChangeListener = e -> {
			switch( e.getPropertyName() ) {
				case PROGRESS_BAR_LARGE_HEIGHT:
				case PROGRESS_BAR_SQUARE:
					progressBar.revalidate();
					progressBar.repaint();
					break;
			}
		};
		progressBar.addPropertyChangeListener( propertyChangeListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		progressBar.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		Dimension size = super.getPreferredSize( c );

		if( progressBar.isStringPainted() || clientPropertyBoolean( c, PROGRESS_BAR_LARGE_HEIGHT, false ) ) {
			// recalculate progress height/width to make it smaller
			Insets insets = progressBar.getInsets();
			FontMetrics fm = progressBar.getFontMetrics( progressBar.getFont() );
			if( progressBar.getOrientation() == JProgressBar.HORIZONTAL )
				size.height = Math.max( fm.getHeight() + insets.top + insets.bottom, getPreferredInnerHorizontal().height );
			else
				size.width = Math.max( fm.getHeight() + insets.left + insets.right, getPreferredInnerVertical().width );
		}

		return size;
	}

	@Override
	protected Dimension getPreferredInnerHorizontal() {
		return UIScale.scale( horizontalSize );
	}

	@Override
	protected Dimension getPreferredInnerVertical() {
		return UIScale.scale( verticalSize );
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		if( c.isOpaque() )
			FlatUIUtils.paintParentBackground( g, c );

		paint( g, c );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		Insets insets = progressBar.getInsets();
		int x = insets.left;
		int y = insets.top;
		int width = progressBar.getWidth() - (insets.right + insets.left);
		int height = progressBar.getHeight() - (insets.top + insets.bottom);

		if( width <= 0 || height <= 0 )
			return;

		boolean horizontal = (progressBar.getOrientation() == JProgressBar.HORIZONTAL);
		int arc = clientPropertyBoolean( c, PROGRESS_BAR_SQUARE, false )
			? 0
			: Math.min( UIScale.scale( this.arc ), horizontal ? height : width );

		FlatUIUtils.setRenderingHints( (Graphics2D) g );

		// paint track
		RoundRectangle2D.Float trackShape = new RoundRectangle2D.Float( x, y, width, height, arc, arc );
		g.setColor( progressBar.getBackground() );
		((Graphics2D)g).fill( trackShape );

		// paint progress
		if( progressBar.isIndeterminate() ) {
			boxRect = getBox( boxRect );
			if( boxRect != null ) {
				g.setColor( progressBar.getForeground() );
				((Graphics2D)g).fill( new RoundRectangle2D.Float( boxRect.x, boxRect.y,
					boxRect.width, boxRect.height, arc, arc ) );
			}

			if( progressBar.isStringPainted() )
				paintString( g, x, y, width, height, 0, insets );
		} else {
			int amountFull = getAmountFull( insets, width, height );

			RoundRectangle2D.Float progressShape = horizontal
				? new RoundRectangle2D.Float( c.getComponentOrientation().isLeftToRight() ? x : x + (width - amountFull),
					y, amountFull, height, arc, arc )
				: new RoundRectangle2D.Float( x, y + (height - amountFull), width, amountFull, arc, arc );

			g.setColor( progressBar.getForeground() );
			if( amountFull < (horizontal ? height : width) ) {
				// special painting for low amounts to avoid painting outside of track
				Area area = new Area( trackShape );
				area.intersect( new Area( progressShape ) );
				((Graphics2D)g).fill( area );
			} else
				((Graphics2D)g).fill( progressShape );

			if( progressBar.isStringPainted() )
				paintString( g, x, y, width, height, amountFull, insets );
		}
	}

	@Override
	protected void paintString( Graphics g, int x, int y, int width, int height, int amountFull, Insets b ) {
		super.paintString( HiDPIUtils.createGraphicsTextYCorrection( (Graphics2D) g ), x, y, width, height, amountFull, b );
	}

	@Override
	protected void setAnimationIndex( int newValue ) {
		super.setAnimationIndex( newValue );

		// On HiDPI screens at 125%, 150% and 175% scaling, it occurs that antialiased painting
		// may paint one pixel outside of the clipping area. This results in visual artifacts
		// in indeterminate mode when the progress moves around.
		// Unfortunately it is not safe to invoke getBox() from here (may throw NPE),
		// which makes it impractical to get progress box and repaint increased box.
		// Only solution is to repaint whole progress bar.
		double systemScaleFactor = UIScale.getSystemScaleFactor( progressBar.getGraphicsConfiguration() );
		if( (int) systemScaleFactor != systemScaleFactor )
			progressBar.repaint();
	}
}
