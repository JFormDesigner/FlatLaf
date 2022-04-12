/*
 * Copyright 2021 FormDev Software GmbH
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

package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import com.formdev.flatlaf.util.UIScale;

/**
 * Cell border for {@code sun.swing.table.DefaultTableCellHeaderRenderer}
 * (used by {@link javax.swing.table.JTableHeader}).
 * <p>
 * Uses separate cell margins from UI defaults to allow easy customizing.
 *
 * @author Karl Tauber
 * @since 1.2
 */
public class FlatTableHeaderBorder
	extends FlatEmptyBorder
{
	protected Color separatorColor = UIManager.getColor( "TableHeader.separatorColor" );
	protected Color bottomSeparatorColor = UIManager.getColor( "TableHeader.bottomSeparatorColor" );
	/** @since 1.6 */ protected boolean showTrailingVerticalLine = UIManager.getBoolean( "TableHeader.showTrailingVerticalLine" );

	public FlatTableHeaderBorder() {
		super( UIManager.getInsets( "TableHeader.cellMargins" ) );
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		JTableHeader header = (JTableHeader) SwingUtilities.getAncestorOfClass( JTableHeader.class, c );
		if( header != null ) {
			if( header.getUI() instanceof FlatTableHeaderUI ) {
				FlatTableHeaderUI ui = (FlatTableHeaderUI) header.getUI();
				if( ui.cellMargins != null ) {
					Insets m = ui.cellMargins;
					return scaleInsets( c, insets, m.top, m.left, m.bottom, m.right );
				}
			}
		}

		return super.getBorderInsets( c, insets );
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		JTableHeader header = (JTableHeader) SwingUtilities.getAncestorOfClass( JTableHeader.class, c );
		boolean leftToRight = (header != null ? header : c).getComponentOrientation().isLeftToRight();
		boolean paintLeft = !leftToRight;
		boolean paintRight = leftToRight;
		Color separatorColor = this.separatorColor;
		Color bottomSeparatorColor = this.bottomSeparatorColor;

		if( header != null ) {
			int hx = SwingUtilities.convertPoint( c, x, y, header ).x;
			if( isDraggedColumn( header, hx ) )
				paintLeft = paintRight = true;
			else {
				if( hx <= 0 && !leftToRight && hideTrailingVerticalLine( header ) )
					paintLeft = false;
				if( hx + width >= header.getWidth() && leftToRight && hideTrailingVerticalLine( header ) )
					paintRight = false;
			}

			// Because this border is always shared for all table headers,
			// get border specific style from FlatTableHeaderUI.
			if( header.getUI() instanceof FlatTableHeaderUI ) {
				FlatTableHeaderUI ui = (FlatTableHeaderUI) header.getUI();
				if( ui.separatorColor != null )
					separatorColor = ui.separatorColor;
				if( ui.bottomSeparatorColor != null )
					bottomSeparatorColor = ui.bottomSeparatorColor;
			}
		}

		float lineWidth = UIScale.scale( 1f );

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			// paint column separator lines
			g2.setColor( separatorColor );
			if( paintLeft )
				g2.fill( new Rectangle2D.Float( x, y, lineWidth, height - lineWidth ) );
			if( paintRight )
				g2.fill( new Rectangle2D.Float( x + width - lineWidth, y, lineWidth, height - lineWidth ) );

			// paint bottom line
			g2.setColor( bottomSeparatorColor );
			g2.fill( new Rectangle2D.Float( x, y + height - lineWidth, width, lineWidth ) );
		} finally {
			g2.dispose();
		}
	}

	protected boolean isDraggedColumn( JTableHeader header, int x ) {
		TableColumn draggedColumn = header.getDraggedColumn();
		if( draggedColumn == null )
			return false;

		int draggedDistance = header.getDraggedDistance();
		if( draggedDistance == 0 )
			return false;

		int columnCount = header.getColumnModel().getColumnCount();
		for( int i = 0; i < columnCount; i++ ) {
			if( header.getHeaderRect( i ).x + draggedDistance == x )
				return true;
		}

		return false;
	}

	protected boolean hideTrailingVerticalLine( JTableHeader header ) {
		if( header.getUI() instanceof FlatTableHeaderUI ) {
			FlatTableHeaderUI ui = (FlatTableHeaderUI) header.getUI();
			if( ui.showTrailingVerticalLine != null )
				return !ui.showTrailingVerticalLine;
		}

		if( showTrailingVerticalLine )
			return false;

		// do not hide if table header is not a child of a scroll pane
		Container viewport = header.getParent();
		Container viewportParent = (viewport != null) ? viewport.getParent() : null;
		if( !(viewportParent instanceof JScrollPane) )
			return false;

		// do not hide if table header is not the column header of the scroll pane
		JScrollPane scrollPane = (JScrollPane) viewportParent;
		JViewport columnHeader = scrollPane.getColumnHeader();
		if( viewport != columnHeader )
			return false;

		// hide if vertical scroll bar is not shown
		JScrollBar vsb = scrollPane.getVerticalScrollBar();
		if( vsb == null || !vsb.isVisible() )
			return true;

		// if "ScrollPane.fillUpperCorner" is true, then javax.swing.ScrollPaneLayout
		// extends the vertical scrollbar into the upper right/left corner
		return vsb.getY() == viewport.getY();
	}
}
