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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.JTableHeader;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTable}.
 *
 * <!-- BasicTableUI -->
 *
 * @uiDefault Table.font								Font
 * @uiDefault Table.background							Color
 * @uiDefault Table.foreground							Color
 * @uiDefault Table.selectionBackground					Color
 * @uiDefault Table.selectionForeground					Color
 * @uiDefault Table.gridColor							Color
 * @uiDefault Table.scrollPaneBorder					Border
 * @uiDefault Table.dropLineColor						Color
 * @uiDefault Table.dropLineShortColor					Color
 *
 * <!-- DefaultTableCellRenderer -->
 *
 * @uiDefault Table.cellNoFocusBorder					Border
 * @uiDefault Table.focusCellHighlightBorder			Border
 * @uiDefault Table.focusSelectedCellHighlightBorder	Border
 * @uiDefault Table.dropCellBackground					Color
 * @uiDefault Table.dropCellForeground					Color
 * @uiDefault Table.alternateRowColor					Color
 * @uiDefault Table.focusCellBackground					Color
 * @uiDefault Table.focusCellForeground					Color
 *
 * <!-- FlatTableUI -->
 *
 * @uiDefault Table.rowHeight							int
 * @uiDefault Table.showHorizontalLines					boolean
 * @uiDefault Table.showVerticalLines					boolean
 * @uiDefault Table.showTrailingVerticalLine			boolean
 * @uiDefault Table.intercellSpacing					Dimension
 * @uiDefault Table.selectionInactiveBackground			Color
 * @uiDefault Table.selectionInactiveForeground			Color
 * @uiDefault Table.paintOutsideAlternateRows			boolean
 *
 * <!-- FlatTableCellBorder -->
 *
 * @uiDefault Table.cellMargins							Insets
 * @uiDefault Table.cellFocusColor						Color
 * @uiDefault Table.showCellFocusIndicator				boolean
 *
 * <!-- FlatInputMaps -->
 *
 * @uiDefault Table.consistentHomeEndKeyBehavior		boolean
 *
 * @author Karl Tauber
 */
public class FlatTableUI
	extends BasicTableUI
	implements StyleableUI, FlatViewportUI.ViewportPainter
{
	protected boolean showHorizontalLines;
	protected boolean showVerticalLines;
	/** @since 1.6 */ @Styleable protected boolean showTrailingVerticalLine;
	protected Dimension intercellSpacing;

	@Styleable protected Color selectionBackground;
	@Styleable protected Color selectionForeground;
	@Styleable protected Color selectionInactiveBackground;
	@Styleable protected Color selectionInactiveForeground;

	// for FlatTableCellBorder
	/** @since 2 */ @Styleable protected Insets cellMargins;
	/** @since 2 */ @Styleable protected Color cellFocusColor;
	/** @since 2 */ @Styleable protected Boolean showCellFocusIndicator;

	private boolean oldShowHorizontalLines;
	private boolean oldShowVerticalLines;
	private Dimension oldIntercellSpacing;

	private PropertyChangeListener propertyChangeListener;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTableUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		showHorizontalLines = UIManager.getBoolean( "Table.showHorizontalLines" );
		showVerticalLines = UIManager.getBoolean( "Table.showVerticalLines" );
		showTrailingVerticalLine = UIManager.getBoolean( "Table.showTrailingVerticalLine" );
		intercellSpacing = UIManager.getDimension( "Table.intercellSpacing" );

		selectionBackground = UIManager.getColor( "Table.selectionBackground" );
		selectionForeground = UIManager.getColor( "Table.selectionForeground" );
		selectionInactiveBackground = UIManager.getColor( "Table.selectionInactiveBackground" );
		selectionInactiveForeground = UIManager.getColor( "Table.selectionInactiveForeground" );

		toggleSelectionColors();

		int rowHeight = FlatUIUtils.getUIInt( "Table.rowHeight", 16 );
		if( rowHeight > 0 )
			LookAndFeel.installProperty( table, "rowHeight", UIScale.scale( rowHeight ) );

		if( !showHorizontalLines ) {
			oldShowHorizontalLines = table.getShowHorizontalLines();
			table.setShowHorizontalLines( false );
		}
		if( !showVerticalLines ) {
			oldShowVerticalLines = table.getShowVerticalLines();
			table.setShowVerticalLines( false );
		}

		if( intercellSpacing != null ) {
			oldIntercellSpacing = table.getIntercellSpacing();
			table.setIntercellSpacing( intercellSpacing );
		}
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		selectionBackground = null;
		selectionForeground = null;
		selectionInactiveBackground = null;
		selectionInactiveForeground = null;

		oldStyleValues = null;

		// restore old show horizontal/vertical lines (if not modified)
		if( !showHorizontalLines && oldShowHorizontalLines && !table.getShowHorizontalLines() )
			table.setShowHorizontalLines( true );
		if( !showVerticalLines && oldShowVerticalLines && !table.getShowVerticalLines() )
			table.setShowVerticalLines( true );

		// restore old intercell spacing (if not modified)
		if( intercellSpacing != null && table.getIntercellSpacing().equals( intercellSpacing ) )
			table.setIntercellSpacing( oldIntercellSpacing );
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		propertyChangeListener = e -> {
			switch( e.getPropertyName() ) {
				case FlatClientProperties.COMPONENT_FOCUS_OWNER:
					toggleSelectionColors();
					break;

				case FlatClientProperties.STYLE:
				case FlatClientProperties.STYLE_CLASS:
					installStyle();
					table.revalidate();
					table.repaint();
					break;
			}
		};
		table.addPropertyChangeListener( propertyChangeListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		table.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;
	}

	@Override
	protected FocusListener createFocusListener() {
		return new BasicTableUI.FocusHandler() {
			@Override
			public void focusGained( FocusEvent e ) {
				super.focusGained( e );
				toggleSelectionColors();
			}

			@Override
			public void focusLost( FocusEvent e ) {
				super.focusLost( e );

				// use invokeLater for the case that the window is deactivated
				EventQueue.invokeLater( () -> {
					toggleSelectionColors();
				} );
			}
		};
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( table, "Table" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		Color oldSelectionBackground = selectionBackground;
		Color oldSelectionForeground = selectionForeground;
		Color oldSelectionInactiveBackground = selectionInactiveBackground;
		Color oldSelectionInactiveForeground = selectionInactiveForeground;

		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		// update selection background
		if( selectionBackground != oldSelectionBackground ) {
			Color selBg = table.getSelectionBackground();
			if( selBg == oldSelectionBackground )
				table.setSelectionBackground( selectionBackground );
			else if( selBg == oldSelectionInactiveBackground )
				table.setSelectionBackground( selectionInactiveBackground );
		}

		// update selection foreground
		if( selectionForeground != oldSelectionForeground ) {
			Color selFg = table.getSelectionForeground();
			if( selFg == oldSelectionForeground )
				table.setSelectionForeground( selectionForeground );
			else if( selFg == oldSelectionInactiveForeground )
				table.setSelectionForeground( selectionInactiveForeground );
		}
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, table, key, value );
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

	/**
	 * Toggle selection colors from focused to inactive and vice versa.
	 *
	 * This is not an optimal solution but much easier than rewriting the whole paint methods.
	 *
	 * Using a LaF specific renderer was avoided because often a custom renderer is
	 * already used in applications. Then either the inactive colors are not used,
	 * or the application has to be changed to extend a FlatLaf renderer.
	 */
	private void toggleSelectionColors() {
		if( table == null )
			return;

		if( FlatUIUtils.isPermanentFocusOwner( table ) ) {
			if( table.getSelectionBackground() == selectionInactiveBackground )
				table.setSelectionBackground( selectionBackground );
			if( table.getSelectionForeground() == selectionInactiveForeground )
				table.setSelectionForeground( selectionForeground );
		} else {
			if( table.getSelectionBackground() == selectionBackground )
				table.setSelectionBackground( selectionInactiveBackground );
			if( table.getSelectionForeground() == selectionForeground )
				table.setSelectionForeground( selectionInactiveForeground );
		}
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		FlatTableHeaderUI.fixDraggedAndResizingColumns( table.getTableHeader() );

		boolean horizontalLines = table.getShowHorizontalLines();
		boolean verticalLines = table.getShowVerticalLines();
		if( horizontalLines || verticalLines ) {
			// fix grid painting issues in BasicTableUI
			//   - do not paint last vertical grid line if line is on right edge of scroll pane
			//   - fix unstable grid line thickness when scaled at 125%, 150%, 175%, 225%, ...
			//     which paints either 1px or 2px lines depending on location
			//   - on Java 9+, fix wrong grid line thickness in dragged column

			boolean hideLastVerticalLine = hideLastVerticalLine();
			int tableWidth = table.getWidth();
			JTableHeader header = table.getTableHeader();
			boolean isDragging = (header != null && header.getDraggedColumn() != null);

			double systemScaleFactor = UIScale.getSystemScaleFactor( (Graphics2D) g );
			double lineThickness = (1. / systemScaleFactor) * (int) systemScaleFactor;

			// Java 8 uses drawLine() to paint grid lines
			// Java 9+ uses fillRect() to paint grid lines (except for dragged column)
			g = new Graphics2DProxy( (Graphics2D) g ) {
				@Override
				public void drawLine( int x1, int y1, int x2, int y2 ) {
					// do not paint last vertical line
					if( hideLastVerticalLine && verticalLines &&
						x1 == x2 && y1 == 0 && x1 == tableWidth - 1 &&
						wasInvokedFromPaintGrid() )
					  return;

					// on Java 9+, fix wrong grid line thickness in dragged column
					if( isDragging &&
						SystemInfo.isJava_9_orLater &&
						((horizontalLines && y1 == y2) || (verticalLines && x1 == x2)) &&
						wasInvokedFromMethod( "paintDraggedArea" ) )
					{
						if( y1 == y2 ) {
							// horizontal grid line
							super.fill( new Rectangle2D.Double( x1, y1, x2 - x1 + 1, lineThickness ) );
						} else if( x1 == x2 ) {
							// vertical grid line
							super.fill( new Rectangle2D.Double( x1, y1, lineThickness, y2 - y1 + 1 ) );
						}
						return;
					}

					super.drawLine( x1, y1, x2, y2 );
				}

				@Override
				public void fillRect( int x, int y, int width, int height ) {
					// do not paint last vertical line
					if( hideLastVerticalLine && verticalLines &&
						width == 1 && y == 0 && x == tableWidth - 1 &&
						wasInvokedFromPaintGrid() )
					  return;

					// reduce line thickness to avoid unstable painted line thickness
					if( lineThickness != 1 ) {
						if( horizontalLines && height == 1 && wasInvokedFromPaintGrid() ) {
							super.fill( new Rectangle2D.Double( x, y, width, lineThickness ) );
							return;
						}
						if( verticalLines && width == 1 && y == 0 && wasInvokedFromPaintGrid() ) {
							super.fill( new Rectangle2D.Double( x, y, lineThickness, height ) );
							return;
						}
					}

					super.fillRect( x, y, width, height );
				}

				private boolean wasInvokedFromPaintGrid() {
					return wasInvokedFromMethod( "paintGrid" );
				}

				private boolean wasInvokedFromMethod( String methodName ) {
					return StackUtils.wasInvokedFrom( BasicTableUI.class.getName(), methodName, 8 );
				}
			};
		}

		super.paint( g, c );
	}

	protected boolean hideLastVerticalLine() {
		if( showTrailingVerticalLine )
			return false;

		// do not hide if table is not a child of a scroll pane
		Container viewport = SwingUtilities.getUnwrappedParent( table );
		Container viewportParent = (viewport != null) ? viewport.getParent() : null;
		if( !(viewportParent instanceof JScrollPane) )
			return false;

		// do not hide last vertical line if table is smaller than viewport
		if( table.getX() + table.getWidth() < viewport.getWidth() )
			return false;

		// in left-to-right:
		//   - do not hide last vertical line if table used as row header in scroll pane
		// in right-to-left:
		//   - hide last vertical line if table used as row header in scroll pane
		//   - do not hide last vertical line if table is in center and scroll pane has row header
		JScrollPane scrollPane = (JScrollPane) viewportParent;
		JViewport rowHeader = scrollPane.getRowHeader();
		return scrollPane.getComponentOrientation().isLeftToRight()
			? (viewport != rowHeader)
			: (viewport == rowHeader || rowHeader == null);
	}

	/** @since 2.3 */
	@Override
	public void paintViewport( Graphics g, JComponent c, JViewport viewport ) {
		int viewportWidth = viewport.getWidth();
		int viewportHeight = viewport.getHeight();

		// fill viewport background in same color as table background
		if( viewport.isOpaque() ) {
			g.setColor( table.getBackground() );
			g.fillRect( 0, 0, viewportWidth, viewportHeight );
		}

		// paint alternating empty rows
		boolean paintOutside = UIManager.getBoolean( "Table.paintOutsideAlternateRows" );
		Color alternateColor;
		if( paintOutside && (alternateColor = UIManager.getColor( "Table.alternateRowColor" )) != null ) {
			g.setColor( alternateColor );

			int rowCount = table.getRowCount();

			// paint alternating empty rows below the table
			int tableHeight = table.getHeight();
			if( tableHeight < viewportHeight ) {
				int tableWidth = table.getWidth();
				int rowHeight = table.getRowHeight();

				for( int y = tableHeight, row = rowCount; y < viewportHeight; y += rowHeight, row++ ) {
					if( row % 2 != 0 )
						g.fillRect( 0, y, tableWidth, rowHeight );
				}
			}
		}
	}
}
