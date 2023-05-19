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

package com.formdev.flatlaf.swingx.ui;

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link org.jdesktop.swingx.JXTaskPane}.
 *
 * @author Karl Tauber
 */
public class FlatTaskPaneUI
	extends BasicTaskPaneUI
{
	private Color background;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTaskPaneUI();
	}

	@Override
	protected void installDefaults() {
		if( group.getContentPane() instanceof JComponent ) {
			// remove default SwingX content border, which may be still set when switching LaF
			JComponent content = (JComponent) group.getContentPane();
			Border contentBorder = content.getBorder();
			if( contentBorder instanceof CompoundBorder &&
				((CompoundBorder)contentBorder).getOutsideBorder() instanceof BasicTaskPaneUI.ContentPaneBorder &&
				((CompoundBorder)contentBorder).getInsideBorder() instanceof EmptyBorder )
			{
				content.setBorder( null );
			}

			// set non-UIResource color to background to avoid that it is lost when switching LaF
			background = UIManager.getColor( "TaskPane.background" );
			Color bg = content.getBackground();
			if( bg == null || bg instanceof UIResource ) {
				content.setBackground( new Color( background.getRGB(), true ) );
			}
		}

		roundHeight = FlatUIUtils.getUIInt( "TaskPane.roundHeight", UIManager.getInt( "Component.arc" ) );

		super.installDefaults();
	}

	@Override
	public void uninstallUI( JComponent c ) {
		if( group.getContentPane() instanceof JComponent ) {
			// uninstall our content border, because it does not implement UIResource,
			// to allow other LaF to install its own border
			JComponent content = (JComponent) group.getContentPane();
			if( content.getBorder() instanceof FlatContentPaneBorder )
				content.setBorder( null );

			// replace our non-UIResouce background with UIResouce background
			// to allow next LaF to overwrite it
			if( background.equals( content.getBackground() ) )
				content.setBackground( background );
			background = null;
		}


		super.uninstallUI( c );
	}

	@Override
	protected int getTitleHeight( Component c ) {
		return Math.max( super.getTitleHeight( c ), UIScale.scale( titleHeight ) );
	}

	@Override
	protected int getRoundHeight() {
		return UIScale.scale( roundHeight );
	}

	@Override
	protected Border createPaneBorder() {
		return new FlatPaneBorder();
	}

	@Override
	protected Border createContentPaneBorder() {
		return new FlatContentPaneBorder( UIManager.getColor( "TaskPane.borderColor" ),
			UIManager.getInsets( "TaskPane.contentInsets" ) );
	}

	//---- class FlatContentPaneBorder ----------------------------------------

	/**
	 * The content pane border.
	 */
	private static class FlatContentPaneBorder
		extends EmptyBorder
	{
		Color color;

		FlatContentPaneBorder( Color color, Insets insets ) {
			super( insets );
			this.color = color;

			// add space for the line border
			left++;
			right++;
			bottom++;
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			if( color == null )
				return;

			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

			g.setColor( color );

			float lineWidth = UIScale.scale( 1f );
			Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
			path.append( new Rectangle2D.Float( x, y, width, height ), false );
			path.append( new Rectangle2D.Float( x + lineWidth, y, width - (lineWidth * 2), height - lineWidth ), false );
			((Graphics2D)g).fill( path );

			FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
		}

		@Override
		public Insets getBorderInsets() {
			return new Insets( scale( top ), scale( left ), scale( bottom ), scale( right ) );
		}

		@Override
		public Insets getBorderInsets( Component c, Insets insets ) {
			insets.left = scale( left );
			insets.top = scale( top );
			insets.right = scale( right );
			insets.bottom = scale( bottom );
			return insets;
		}
	}

	//---- class FlatPaneBorder -----------------------------------------------

	private class FlatPaneBorder
		extends PaneBorder
	{
		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

			super.paintBorder( c, g, x, y, width, height );

			FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
		}

		@Override
		protected void paintTitleBackground( JXTaskPane group, Graphics g ) {
			int width = group.getWidth();
			int height = getTitleHeight( group ) ;
			float arc = UIScale.scale( (float) roundHeight );
			float lineWidth = UIScale.scale( 1f );

			// paint background
			g.setColor( group.isSpecial() ? specialTitleBackground : titleBackgroundGradientStart );
			((Graphics2D)g).fill( FlatUIUtils.createRoundRectanglePath( lineWidth, lineWidth,
				width - (lineWidth * 2), height - (lineWidth * 2), arc - lineWidth, arc - lineWidth, 0, 0 ) );

			// paint border
			if( borderColor != null ) {
				g.setColor( borderColor );
				((Graphics2D)g).fill( FlatUIUtils.createRoundRectangle( 0, 0, width, height, lineWidth, arc, arc, 0, 0 ) );
			}
		}

		@Override
		protected void paintExpandedControls( JXTaskPane group, Graphics g, int x, int y, int width, int height ) {
			g.setColor( getPaintColor( group ) );
			paintChevronControls( group, g, x, y, width, height );
		}

		@Override
		protected void paintChevronControls( JXTaskPane group, Graphics g, int x, int y, int width, int height ) {
			Graphics2D g2 = (Graphics2D) g;

			// scale chevron size
			float cw = scale( 7f );
			float ch = scale( 3.5f );

			// create arrow shape
			int direction = group.isCollapsed() ? SwingConstants.SOUTH : SwingConstants.NORTH;
			Shape arrowShape = FlatUIUtils.createArrowShape( direction, true, cw, ch );

			// fix position of controls
			x = group.getComponentOrientation().isLeftToRight() ? (group.getWidth() - width - y) : y;

			// compute chevron position
			int cx = (int) (x + width / 2 - cw / 2);
			int cy = (int) (y + height / 2 - ch);
			float offset = ch + UIScale.scale( 1f );

			// set stroke with scaled width
			g2.setStroke( new BasicStroke( scale( 1f ) ) );

			// paint
			g2.translate( cx, cy );
			FlatUIUtils.drawShapePure( g2, arrowShape );
			g2.translate( 0, offset );
			FlatUIUtils.drawShapePure( g2, arrowShape );
			g2.translate( -cx, -(cy + offset) );
		}

		@Override
		protected void paintTitle( JXTaskPane group, Graphics g, Color textColor,
			int x, int y, int width, int height )
		{
			// scale title position
			int titleX = UIScale.scale( 3 );
			int titleWidth = group.getWidth() - getTitleHeight(group) - titleX;
			if( !group.getComponentOrientation().isLeftToRight() ) {
				// right-to-left
				titleX = group.getWidth() - titleX - titleWidth;
			}

			super.paintTitle( group, g, textColor, titleX, y, titleWidth, height );
		}

		@Override
		protected void paintFocus( Graphics g, Color paintColor, int x, int y, int width, int height ) {
			// scale focus rectangle
			int sx = UIScale.scale( x );
			int sy = UIScale.scale( y );
			int swidth = width - (sx - x) * 2;
			int sheight = height - (sy - y) * 2;

			super.paintFocus( g, paintColor, sx, sy, swidth, sheight );
		}

		@Override
		protected boolean isMouseOverBorder() {
			return true;
		}
	}
}
