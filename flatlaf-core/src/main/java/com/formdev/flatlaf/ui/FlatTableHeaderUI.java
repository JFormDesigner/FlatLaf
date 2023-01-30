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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;
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
 * @uiDefault TableHeader.hoverBackground		Color	optional
 * @uiDefault TableHeader.hoverForeground		Color	optional
 * @uiDefault TableHeader.pressedBackground		Color	optional
 * @uiDefault TableHeader.pressedForeground		Color	optional
 * @uiDefault TableHeader.bottomSeparatorColor	Color
 * @uiDefault TableHeader.height				int
 * @uiDefault TableHeader.sortIconPosition		String	right (default), left, top or bottom
 *
 * <!-- FlatTableHeaderBorder -->
 *
 * @uiDefault TableHeader.cellMargins			Insets
 * @uiDefault TableHeader.separatorColor		Color
 * @uiDefault TableHeader.bottomSeparatorColor	Color
 * @uiDefault TableHeader.showTrailingVerticalLine	boolean
 *
 * <!-- FlatAscendingSortIcon and FlatDescendingSortIcon -->
 *
 * @uiDefault Component.arrowType				String	chevron (default) or triangle
 * @uiDefault Table.sortIconColor				Color
 *
 * @author Karl Tauber
 */
public class FlatTableHeaderUI
	extends BasicTableHeaderUI
	implements StyleableUI
{
	/** @since 3.1 */ @Styleable protected Color hoverBackground;
	/** @since 3.1 */ @Styleable protected Color hoverForeground;
	/** @since 3.1 */ @Styleable protected Color pressedBackground;
	/** @since 3.1 */ @Styleable protected Color pressedForeground;
	@Styleable protected Color bottomSeparatorColor;
	@Styleable protected int height;
	@Styleable(type=String.class) protected int sortIconPosition;

	// for FlatTableHeaderBorder
	/** @since 2 */ @Styleable protected Insets cellMargins;
	/** @since 2 */ @Styleable protected Color separatorColor;
	/** @since 2 */ @Styleable protected Boolean showTrailingVerticalLine;

	// for FlatAscendingSortIcon and FlatDescendingSortIcon
	// (needs to be public because icon classes are in another package)
	/** @since 2 */ @Styleable public String arrowType;
	/** @since 2 */ @Styleable public Color sortIconColor;

	private PropertyChangeListener propertyChangeListener;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTableHeaderUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		hoverBackground = UIManager.getColor( "TableHeader.hoverBackground" );
		hoverForeground = UIManager.getColor( "TableHeader.hoverForeground" );
		pressedBackground = UIManager.getColor( "TableHeader.pressedBackground" );
		pressedForeground = UIManager.getColor( "TableHeader.pressedForeground" );
		bottomSeparatorColor = UIManager.getColor( "TableHeader.bottomSeparatorColor" );
		height = UIManager.getInt( "TableHeader.height" );
		sortIconPosition = parseSortIconPosition( UIManager.getString( "TableHeader.sortIconPosition" ) );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		hoverBackground = null;
		hoverForeground = null;
		pressedBackground = null;
		pressedForeground = null;
		bottomSeparatorColor = null;

		oldStyleValues = null;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		propertyChangeListener = FlatStylingSupport.createPropertyChangeListener( header, this::installStyle, null );
		header.addPropertyChangeListener( propertyChangeListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		header.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( header, "TableHeader" ) );
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
		if( key.equals( "sortIconPosition" ) && value instanceof String )
			value = parseSortIconPosition( (String) value );

		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, header, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		if( key.equals( "sortIconPosition" ) ) {
			switch( sortIconPosition ) {
				default:
				case SwingConstants.RIGHT:	return "right";
				case SwingConstants.LEFT:	return "left";
				case SwingConstants.TOP:	return "top";
				case SwingConstants.BOTTOM:	return "bottom";
			}
		}

		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	private static int parseSortIconPosition( String str ) {
		if( str == null )
			str = "";

		switch( str ) {
			default:
			case "right":	return SwingConstants.RIGHT;
			case "left":	return SwingConstants.LEFT;
			case "top":		return SwingConstants.TOP;
			case "bottom":	return SwingConstants.BOTTOM;
		}
	}

	@Override
	protected MouseInputListener createMouseInputListener() {
		return new FlatMouseInputHandler();
	}

	// overridden and made public to allow usage in custom renderers
	@Override
	public int getRolloverColumn() {
		return super.getRolloverColumn();
	}

	@Override
	protected void rolloverColumnUpdated( int oldColumn, int newColumn ) {
		header.repaint( header.getHeaderRect( oldColumn ) );
		header.repaint( header.getHeaderRect( newColumn ) );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		fixDraggedAndResizingColumns( header );

		TableColumnModel columnModel = header.getColumnModel();
		if( columnModel.getColumnCount() <= 0 )
			return;

		// compute total width of all columns
		int columnCount = columnModel.getColumnCount();
		int totalWidth = 0;
		for( int i = 0; i < columnCount; i++ )
			totalWidth += columnModel.getColumn( i ).getWidth();

		if( totalWidth < header.getWidth() ) {
			// do not paint bottom separator if JTableHeader.setDefaultRenderer() was used
			TableCellRenderer defaultRenderer = header.getDefaultRenderer();
			boolean paintBottomSeparator = isSystemDefaultRenderer( defaultRenderer );
			if( !paintBottomSeparator && header.getTable() != null ) {
				// check whether the renderer delegates to the system default renderer
				Component rendererComponent = defaultRenderer.getTableCellRendererComponent(
					header.getTable(), "", false, false, -1, 0 );
				paintBottomSeparator = isSystemDefaultRenderer( rendererComponent );
			}

			if( paintBottomSeparator ) {
				int w = c.getWidth() - totalWidth;
				int x = header.getComponentOrientation().isLeftToRight() ? c.getWidth() - w : 0;
				paintBottomSeparator( g, c, x, w );
			}
		}

		// temporary use own default renderer
		FlatTableCellHeaderRenderer tempRenderer = new FlatTableCellHeaderRenderer( header.getDefaultRenderer() );
		header.setDefaultRenderer( tempRenderer );

		// paint header
		super.paint( g, c );

		// restore default renderer
		tempRenderer.reset();
		header.setDefaultRenderer( tempRenderer.delegate );
	}

	private boolean isSystemDefaultRenderer( Object headerRenderer ) {
		String rendererClassName = headerRenderer.getClass().getName();
		return rendererClassName.equals( "sun.swing.table.DefaultTableCellHeaderRenderer" ) ||
			   rendererClassName.equals( "sun.swing.FilePane$AlignableTableHeaderRenderer" );
	}

	protected void paintBottomSeparator( Graphics g, JComponent c, int x, int w ) {
		float lineWidth = UIScale.scale( 1f );

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			// paint bottom line
			g2.setColor( bottomSeparatorColor );
			g2.fill( new Rectangle2D.Float( x, c.getHeight() - lineWidth, w, lineWidth ) );
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

	static void fixDraggedAndResizingColumns( JTableHeader header ) {
		if( header == null )
			return;

		// Dragged column may be outdated in the case that the table structure
		// was changed from a table header popup menu action. In this case
		// the paint methods in BasicTableHeaderUI and BasicTableUI would throw exceptions.
		TableColumn draggedColumn = header.getDraggedColumn();
		if( draggedColumn != null && !isValidColumn( header.getColumnModel(), draggedColumn ) )
			header.setDraggedColumn( null );

		// also clear outdated resizing column (although this seems not cause exceptions)
		TableColumn resizingColumn = header.getResizingColumn();
		if( resizingColumn != null && !isValidColumn( header.getColumnModel(), resizingColumn ) )
			header.setResizingColumn( null );
	}

	private static boolean isValidColumn( TableColumnModel cm, TableColumn column ) {
		int count = cm.getColumnCount();
		for( int i = 0; i < count; i++ ) {
			if( cm.getColumn( i ) == column )
				return true;
		}
		return false;
	}

	//---- class FlatTableCellHeaderRenderer ----------------------------------

	/**
	 * A delegating header renderer that is only used to paint hover and pressed
	 * background/foreground and to paint sort arrows at top, bottom or left position.
	 */
	private class FlatTableCellHeaderRenderer
		implements TableCellRenderer, Border, UIResource
	{
		private final TableCellRenderer delegate;

		private JLabel l;
		private Color oldBackground;
		private Color oldForeground;
		private Boolean oldOpaque;
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

			// hover and pressed background/foreground
			TableColumn draggedColumn = header.getDraggedColumn();
			Color background = null;
			Color foreground = null;
			if( draggedColumn != null && header.getTable().convertColumnIndexToView( draggedColumn.getModelIndex() ) == column ) {
				background = pressedBackground;
				foreground = pressedForeground;
			} else if( getRolloverColumn() == column ) {
				background = hoverBackground;
				foreground = hoverForeground;
			}
			if( background != null ) {
				if( oldBackground == null )
					oldBackground = l.getBackground();
				if( oldOpaque == null )
					oldOpaque = l.isOpaque();
				l.setBackground( FlatUIUtils.deriveColor( background, header.getBackground() ) );
				l.setOpaque( true );
			}
			if( foreground != null ) {
				if( oldForeground == null )
					oldForeground = l.getForeground();
				l.setForeground( FlatUIUtils.deriveColor( foreground, header.getForeground() ) );
			}

			// sort icon
			if( sortIconPosition == SwingConstants.LEFT ) {
				// left
				if( oldHorizontalTextPosition < 0 )
					oldHorizontalTextPosition = l.getHorizontalTextPosition();
				l.setHorizontalTextPosition( SwingConstants.RIGHT );
			} else if( sortIconPosition == SwingConstants.TOP || sortIconPosition == SwingConstants.BOTTOM ) {
				// top or bottom
				sortIcon = l.getIcon();
				origBorder = l.getBorder();
				l.setIcon( null );
				l.setBorder( this );
			}

			return l;
		}

		void reset() {
			if( l == null )
				return;

			if( oldBackground != null )
				l.setBackground( oldBackground );
			if( oldForeground != null )
				l.setForeground( oldForeground );
			if( oldOpaque != null )
				l.setOpaque( oldOpaque );
			if( oldHorizontalTextPosition >= 0 )
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

	//---- class FlatMouseInputHandler ----------------------------------------

	/** @since 1.6 */
	protected class FlatMouseInputHandler
		extends MouseInputHandler
	{
		Cursor oldCursor;

		@Override
		public void mouseMoved( MouseEvent e ) {
			// restore old cursor, which is necessary because super.mouseMoved() swaps cursors
			if( oldCursor != null ) {
				header.setCursor( oldCursor );
				oldCursor = null;
			}

			super.mouseMoved( e );

			// if resizing last column is not possible, then Swing still shows a resize cursor,
			// which can be confusing for the user --> change cursor to standard cursor
			JTable table;
			int column;
			if( header.isEnabled() &&
				(table = header.getTable()) != null &&
				table.getAutoResizeMode() != JTable.AUTO_RESIZE_OFF &&
				header.getCursor() == Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) &&
				(column = header.columnAtPoint( e.getPoint() )) >= 0 &&
				column == header.getColumnModel().getColumnCount() - 1 )
			{
				// mouse is in last column
				Rectangle r = header.getHeaderRect( column );
				r.grow( -3, 0 );
				if( !r.contains( e.getX(), e.getY() ) ) {
					// mouse is in left or right resize area of last column
					boolean isResizeLastColumn = (e.getX() >= r.x + (r.width / 2));
					if( !header.getComponentOrientation().isLeftToRight() )
						isResizeLastColumn = !isResizeLastColumn;

					if( isResizeLastColumn ) {
						// resize is not possible --> change cursor to standard cursor
						oldCursor = header.getCursor();
						header.setCursor( Cursor.getDefaultCursor() );
					}
				}
			}
		}
	}
}
