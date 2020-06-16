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
import java.awt.geom.Rectangle2D;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.table.JTableHeader}.
 *
 * <!-- BasicTableHeaderUI -->
 *
 * @uiDefault TableHeader.font					Font
 * @uiDefault TableHeader.background			Color
 * @uiDefault TableHeader.foreground			Color
 *
 * <!-- FlatTableHeaderUI -->
 *
 * @uiDefault TableHeader.separatorColor		Color
 * @uiDefault TableHeader.bottomSeparatorColor	Color
 * @uiDefault TableHeader.height				int
 * @uiDefault TableHeader.sortIconPosition		String	right (default), left, top or bottom
 *
 * @author Karl Tauber
 */
public class FlatTableHeaderUI
	extends BasicTableHeaderUI
{
	protected Color separatorColor;
	protected Color bottomSeparatorColor;
	protected int height;
	protected int sortIconPosition;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTableHeaderUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		separatorColor = UIManager.getColor( "TableHeader.separatorColor" );
		bottomSeparatorColor = UIManager.getColor( "TableHeader.bottomSeparatorColor" );
		height = UIManager.getInt( "TableHeader.height" );
		switch( Objects.toString( UIManager.getString( "TableHeader.sortIconPosition" ), "right" ) ) {
			default:
			case "right":	sortIconPosition = SwingConstants.RIGHT; break;
			case "left":	sortIconPosition = SwingConstants.LEFT; break;
			case "top":		sortIconPosition = SwingConstants.TOP; break;
			case "bottom":	sortIconPosition = SwingConstants.BOTTOM; break;
		}
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		separatorColor = null;
		bottomSeparatorColor = null;
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		// do not paint borders if JTableHeader.setDefaultRenderer() was used
		TableCellRenderer defaultRenderer = header.getDefaultRenderer();
		boolean paintBorders = isSystemDefaultRenderer( defaultRenderer );
		if( !paintBorders && header.getColumnModel().getColumnCount() > 0 ) {
			// check whether the renderer delegates to the system default renderer
			Component rendererComponent = defaultRenderer.getTableCellRendererComponent(
				header.getTable(), "", false, false, -1, 0 );
			paintBorders = isSystemDefaultRenderer( rendererComponent );
		}

		if( paintBorders )
			paintColumnBorders( g, c );

		// temporary use own default renderer if necessary
		FlatTableCellHeaderRenderer sortIconRenderer = null;
		if( sortIconPosition != SwingConstants.RIGHT ) {
			sortIconRenderer = new FlatTableCellHeaderRenderer( header.getDefaultRenderer() );
			header.setDefaultRenderer( sortIconRenderer );
		}

		// paint header
		super.paint( g, c );

		// restore default renderer
		if( sortIconRenderer != null ) {
			sortIconRenderer.reset();
			header.setDefaultRenderer( sortIconRenderer.delegate );
		}

		if( paintBorders )
			paintDraggedColumnBorders( g, c );
	}

	private boolean isSystemDefaultRenderer( Object headerRenderer ) {
		String rendererClassName = headerRenderer.getClass().getName();
		return rendererClassName.equals( "sun.swing.table.DefaultTableCellHeaderRenderer" ) ||
			   rendererClassName.equals( "sun.swing.FilePane$AlignableTableHeaderRenderer" );
	}

	private void paintColumnBorders( Graphics g, JComponent c ) {
		int width = c.getWidth();
		int height = c.getHeight();
		float lineWidth = UIScale.scale( 1f );
		float topLineIndent = lineWidth;
		float bottomLineIndent = lineWidth * 3;
		TableColumnModel columnModel = header.getColumnModel();
		int columnCount = columnModel.getColumnCount();

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			// paint bottom line
			g2.setColor( bottomSeparatorColor );
			g2.fill( new Rectangle2D.Float( 0, height - lineWidth, width, lineWidth ) );

			// paint column separator lines
			g2.setColor( separatorColor );

			int sepCount = columnCount;
			if( header.getTable().getAutoResizeMode() != JTable.AUTO_RESIZE_OFF && !isVerticalScrollBarVisible() )
				sepCount--;

			if( header.getComponentOrientation().isLeftToRight() ) {
				int x = 0;
				for( int i = 0; i < sepCount; i++ ) {
					x += columnModel.getColumn( i ).getWidth();
					g2.fill( new Rectangle2D.Float( x - lineWidth, topLineIndent, lineWidth, height - bottomLineIndent ) );
				}
			} else {
				int x = width;
				for( int i = 0; i < sepCount; i++ ) {
					x -= columnModel.getColumn( i ).getWidth();
					g2.fill( new Rectangle2D.Float( x - (i < sepCount - 1 ? lineWidth : 0),
						topLineIndent, lineWidth, height - bottomLineIndent ) );
				}
			}
		} finally {
			g2.dispose();
		}
	}

	private void paintDraggedColumnBorders( Graphics g, JComponent c ) {
		TableColumn draggedColumn = header.getDraggedColumn();
		if( draggedColumn == null )
			return;

		// find index of dragged column
		TableColumnModel columnModel = header.getColumnModel();
		int columnCount = columnModel.getColumnCount();
		int draggedColumnIndex = -1;
		for( int i = 0; i < columnCount; i++ ) {
			if( columnModel.getColumn( i ) == draggedColumn ) {
				draggedColumnIndex = i;
				break;
			}
		}

		if( draggedColumnIndex < 0 )
			return;

		float lineWidth = UIScale.scale( 1f );
		float topLineIndent = lineWidth;
		float bottomLineIndent = lineWidth * 3;
		Rectangle r = header.getHeaderRect( draggedColumnIndex );
		r.x += header.getDraggedDistance();

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			// paint dragged bottom line
			g2.setColor( bottomSeparatorColor );
			g2.fill( new Rectangle2D.Float( r.x, r.y + r.height - lineWidth, r.width, lineWidth ) );

			// paint dragged column separator lines
			g2.setColor( separatorColor );
			g2.fill( new Rectangle2D.Float( r.x, topLineIndent, lineWidth, r.height - bottomLineIndent ) );
			g2.fill( new Rectangle2D.Float( r.x + r.width - lineWidth, r.y + topLineIndent, lineWidth, r.height - bottomLineIndent ) );
		} finally {
			g2.dispose();
		}
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		Dimension size = super.getPreferredSize( c );
		if( size.height > 0 )
			size.height = Math.max( size.height, UIScale.scale( height ) );
		return size;
	}

	private boolean isVerticalScrollBarVisible() {
		JScrollPane scrollPane = getScrollPane();
		return (scrollPane != null && scrollPane.getVerticalScrollBar() != null)
			? scrollPane.getVerticalScrollBar().isVisible()
			: false;
	}

	private JScrollPane getScrollPane() {
		Container parent = header.getParent();
		if( parent == null )
			return null;

		parent = parent.getParent();
		return (parent instanceof JScrollPane) ? (JScrollPane) parent : null;
	}

	//---- class FlatTableCellHeaderRenderer ----------------------------------

	/**
	 * A delegating header renderer that is only used to paint sort arrows at
	 * top, bottom or left position.
	 */
	private class FlatTableCellHeaderRenderer
		implements TableCellRenderer, Border, UIResource
	{
		private final TableCellRenderer delegate;

		private JLabel l;
		private int oldHorizontalTextPosition = -1;
		private Border origBorder;
		private Icon sortIcon;

		FlatTableCellHeaderRenderer( TableCellRenderer delegate ) {
			this.delegate = delegate;
		}

		@Override
		public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column )
		{
			Component c = delegate.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
			if( !(c instanceof JLabel) )
				return c;

			l = (JLabel) c;

			if( sortIconPosition == SwingConstants.LEFT ) {
				if( oldHorizontalTextPosition < 0 )
					oldHorizontalTextPosition = l.getHorizontalTextPosition();
				l.setHorizontalTextPosition( SwingConstants.RIGHT );
			} else {
				// top or bottom
				sortIcon = l.getIcon();
				origBorder = l.getBorder();
				l.setIcon( null );
				l.setBorder( this );
			}

			return l;
		}

		void reset() {
			if( l != null && sortIconPosition == SwingConstants.LEFT && oldHorizontalTextPosition >= 0 )
				l.setHorizontalTextPosition( oldHorizontalTextPosition );
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			if( origBorder != null )
				origBorder.paintBorder( c, g, x, y, width, height );

			if( sortIcon != null ) {
				int xi = x + ((width - sortIcon.getIconWidth()) / 2);
				int yi = (sortIconPosition == SwingConstants.TOP)
					? y + UIScale.scale( 1 )
					: y + height - sortIcon.getIconHeight()
						- 1 // for gap
						- (int) (1 * UIScale.getUserScaleFactor()); // for bottom border
				sortIcon.paintIcon( c, g, xi, yi );
			}
		}

		@Override
		public Insets getBorderInsets( Component c ) {
			return (origBorder != null) ? origBorder.getBorderInsets( c ) : new Insets( 0, 0, 0, 0 );
		}

		@Override
		public boolean isBorderOpaque() {
			return (origBorder != null) ? origBorder.isBorderOpaque() : false;
		}
	}
}
