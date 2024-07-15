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
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatCheckBoxIcon;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.HiDPIUtils;
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
 * @uiDefault Table.selectionInsets						Insets
 * @uiDefault Table.selectionArc						int
 * @uiDefault Table.paintOutsideAlternateRows			boolean
 * @uiDefault Table.editorSelectAllOnStartEditing		boolean
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
	/** @since 3.5 */ @Styleable protected Insets selectionInsets;
	/** @since 3.5 */ @Styleable protected int selectionArc;

	// for FlatTableCellBorder
	/** @since 2 */ @Styleable protected Insets cellMargins;
	/** @since 2 */ @Styleable protected Color cellFocusColor;
	/** @since 2 */ @Styleable protected Boolean showCellFocusIndicator;

	private boolean oldShowHorizontalLines;
	private boolean oldShowVerticalLines;
	private Dimension oldIntercellSpacing;
	private TableCellRenderer oldBooleanRenderer;

	private PropertyChangeListener propertyChangeListener;
	private ComponentListener outsideAlternateRowsListener;
	private ListSelectionListener rowSelectionListener;
	private TableColumnModelListener columnSelectionListener;
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
		selectionInsets = UIManager.getInsets( "Table.selectionInsets" );
		selectionArc = UIManager.getInt( "Table.selectionArc" );

		toggleSelectionColors();

		int rowHeight = FlatUIUtils.getUIInt( "Table.rowHeight", 16 );
		if( rowHeight > 0 )
			LookAndFeel.installProperty( table, "rowHeight", UIScale.scale( rowHeight ) );

		FlatTablePropertyWatcher watcher = FlatTablePropertyWatcher.get( table );
		if( watcher != null )
			watcher.enabled = false;

		if( !showHorizontalLines && (watcher == null || !watcher.showHorizontalLinesChanged) ) {
			oldShowHorizontalLines = table.getShowHorizontalLines();
			table.setShowHorizontalLines( false );
		}
		if( !showVerticalLines && (watcher == null || !watcher.showVerticalLinesChanged) ) {
			oldShowVerticalLines = table.getShowVerticalLines();
			table.setShowVerticalLines( false );
		}

		if( intercellSpacing != null && (watcher == null || !watcher.intercellSpacingChanged) ) {
			oldIntercellSpacing = table.getIntercellSpacing();
			table.setIntercellSpacing( intercellSpacing );
		}

		if( watcher != null )
			watcher.enabled = true;
		else
			table.addPropertyChangeListener( new FlatTablePropertyWatcher() );

		// install boolean renderer
		oldBooleanRenderer = table.getDefaultRenderer( Boolean.class );
		if( oldBooleanRenderer instanceof UIResource )
			table.setDefaultRenderer( Boolean.class, new FlatBooleanRenderer() );
		else
			oldBooleanRenderer = null;
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		selectionBackground = null;
		selectionForeground = null;
		selectionInactiveBackground = null;
		selectionInactiveForeground = null;

		oldStyleValues = null;

		FlatTablePropertyWatcher watcher = FlatTablePropertyWatcher.get( table );
		if( watcher != null )
			watcher.enabled = false;

		// restore old show horizontal/vertical lines (if not modified)
		if( !showHorizontalLines && oldShowHorizontalLines && !table.getShowHorizontalLines() &&
			(watcher == null || !watcher.showHorizontalLinesChanged) )
		  table.setShowHorizontalLines( true );
		if( !showVerticalLines && oldShowVerticalLines && !table.getShowVerticalLines() &&
			(watcher == null || !watcher.showVerticalLinesChanged) )
		  table.setShowVerticalLines( true );

		// restore old intercell spacing (if not modified)
		if( intercellSpacing != null && table.getIntercellSpacing().equals( intercellSpacing ) &&
			(watcher == null || !watcher.intercellSpacingChanged) )
		  table.setIntercellSpacing( oldIntercellSpacing );

		if( watcher != null )
			watcher.enabled = true;

		// uninstall boolean renderer
		if( table.getDefaultRenderer( Boolean.class ) instanceof FlatBooleanRenderer ) {
			if( oldBooleanRenderer instanceof Component ) {
				// because the old renderer component was not attached to any component hierarchy,
				// its UI was not yet updated, and it is necessary to do it here
				SwingUtilities.updateComponentTreeUI( (Component) oldBooleanRenderer );
			}
			table.setDefaultRenderer( Boolean.class, oldBooleanRenderer );
		}
		oldBooleanRenderer = null;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		propertyChangeListener = e -> {
			switch( e.getPropertyName() ) {
				case "selectionModel":
					if( rowSelectionListener != null ) {
						Object oldModel = e.getOldValue();
						Object newModel = e.getNewValue();
						if( oldModel != null )
							((ListSelectionModel)oldModel).removeListSelectionListener( rowSelectionListener );
						if( newModel != null )
							((ListSelectionModel)newModel).addListSelectionListener( rowSelectionListener );
					}
					break;

				case "columnModel":
					if( columnSelectionListener != null ) {
						Object oldModel = e.getOldValue();
						Object newModel = e.getNewValue();
						if( oldModel != null )
							((TableColumnModel)oldModel).removeColumnModelListener( columnSelectionListener );
						if( newModel != null )
							((TableColumnModel)newModel).addColumnModelListener( columnSelectionListener );
					}
					break;

				case FlatClientProperties.COMPONENT_FOCUS_OWNER:
					toggleSelectionColors();
					break;

				case FlatClientProperties.STYLE:
				case FlatClientProperties.STYLE_CLASS:
					installStyle();
					table.revalidate();
					HiDPIUtils.repaint( table );
					break;
			}
		};
		table.addPropertyChangeListener( propertyChangeListener );

		if( selectionArc > 0 )
			installRepaintRoundedSelectionListeners();
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		table.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;

		if( outsideAlternateRowsListener != null ) {
			table.removeComponentListener( outsideAlternateRowsListener );
			outsideAlternateRowsListener = null;
		}
		if( rowSelectionListener != null ) {
			table.getSelectionModel().removeListSelectionListener( rowSelectionListener );
			rowSelectionListener = null;
		}
		if( columnSelectionListener != null ) {
			table.getColumnModel().removeColumnModelListener( columnSelectionListener );
			columnSelectionListener = null;
		}
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

	@Override
	protected void installKeyboardActions() {
		super.installKeyboardActions();

		if( UIManager.getBoolean( "Table.editorSelectAllOnStartEditing" ) ) {
			// get shared action map, used for all tables
			ActionMap map = SwingUtilities.getUIActionMap( table );
			if( map != null )
				StartEditingAction.install( map, "startEditing" );
		}
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
		if( "rowHeight".equals( key ) && value instanceof Integer )
			value = UIScale.scale( (Integer) value );
		else if( "selectionArc".equals( key ) && value instanceof Integer && (Integer) value > 0 )
			installRepaintRoundedSelectionListeners();

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
			double lineOffset = (1. - lineThickness) + 0.05; // adding 0.05 to fix line location in some cases

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
							super.fill( new Rectangle2D.Double( x, y + lineOffset, width, lineThickness ) );
							return;
						}
						if( verticalLines && width == 1 && y == 0 && wasInvokedFromPaintGrid() ) {
							super.fill( new Rectangle2D.Double( x + lineOffset, y, lineThickness, height ) );
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

		// rounded selection or selection insets
		if( selectionArc > 0 || (selectionInsets != null && !FlatUIUtils.isInsetsEmpty( selectionInsets )) )
			g = new RoundedSelectionGraphics( g, UIManager.getColor( "Table.alternateRowColor" ) );

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
			int rowCount = table.getRowCount();

			// paint alternating empty rows below the table
			int tableHeight = table.getHeight();
			if( tableHeight < viewportHeight ) {
				int tableWidth = table.getWidth();
				int rowHeight = table.getRowHeight();

				g.setColor( alternateColor );

				int x = viewport.getComponentOrientation().isLeftToRight() ? 0 : viewportWidth - tableWidth;
				for( int y = tableHeight, row = rowCount; y < viewportHeight; y += rowHeight, row++ ) {
					if( row % 2 != 0 )
						paintAlternateRowBackground( g, -1, -1, x, y, tableWidth, rowHeight );
				}

				// add listener on demand
				if( outsideAlternateRowsListener == null && table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF ) {
					outsideAlternateRowsListener = new FlatOutsideAlternateRowsListener();
					table.addComponentListener( outsideAlternateRowsListener );
				}
			}
		}
	}

	/**
	 * Paints (rounded) alternate row background.
	 * Supports {@link #selectionArc} and {@link #selectionInsets}.
	 * <p>
	 * <b>Note:</b> This method is only invoked if either selection arc
	 *              is greater than zero or if selection insets are not empty.
	 *
	 * @since 3.5
	 */
	protected void paintAlternateRowBackground( Graphics g, int row, int column, int x, int y, int width, int height ) {
		Insets insets = (selectionInsets != null) ? (Insets) selectionInsets.clone() : null;
		float arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight;
		arcTopLeft = arcTopRight = arcBottomLeft = arcBottomRight = UIScale.scale( selectionArc / 2f );

		if( column >= 0 ) {
			// selection insets

			// selection arc
			if( column > 0 ) {
				if( insets != null )
					insets.left = 0;

				if( table.getComponentOrientation().isLeftToRight() )
					arcTopLeft = arcBottomLeft = 0;
				else
					arcTopRight = arcBottomRight = 0;
			}
			if( column < table.getColumnCount() - 1 ) {
				if( insets != null )
					insets.right = 0;

				if( table.getComponentOrientation().isLeftToRight() )
					arcTopRight = arcBottomRight = 0;
				else
					arcTopLeft = arcBottomLeft = 0;
			}
		}

		FlatUIUtils.paintSelection( (Graphics2D) g, x, y, width, height,
			UIScale.scale( insets ), arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight, 0 );
	}

	/**
	 * Paints (rounded) cell selection.
	 * Supports {@link #selectionArc} and {@link #selectionInsets}.
	 * <p>
	 * <b>Note:</b> This method is only invoked if either selection arc
	 *              is greater than zero or if selection insets are not empty.
	 *
	 * @since 3.5
	 */
	protected void paintCellSelection( Graphics g, int row, int column, int x, int y, int width, int height ) {
		boolean rowSelAllowed = table.getRowSelectionAllowed();
		boolean colSelAllowed = table.getColumnSelectionAllowed();
		boolean rowSelOnly = rowSelAllowed && !colSelAllowed;
		boolean colSelOnly = colSelAllowed && !rowSelAllowed;
		boolean cellOnlySel = rowSelAllowed && colSelAllowed;

		// get selection state of surrounding cells
		boolean leftSelected    = (column > 0 && (rowSelOnly || table.isCellSelected( row, column - 1 )));
		boolean topSelected     = (row    > 0 && (colSelOnly || table.isCellSelected( row - 1, column )));
		boolean rightSelected   = (column < table.getColumnCount() - 1 && (rowSelOnly || table.isCellSelected( row, column + 1 )));
		boolean bottomSelected  = (row    < table.getRowCount() - 1    && (colSelOnly || table.isCellSelected( row + 1, column )));
		if( !table.getComponentOrientation().isLeftToRight() ) {
			boolean temp = leftSelected;
			leftSelected = rightSelected;
			rightSelected = temp;
		}

		// selection insets
		// (insets are applied to whole row if row-only selection is used,
		//  or to whole column if column-only selection is used,
		//  or to cell if cell selection is used)
		Insets insets = (selectionInsets != null) ? (Insets) selectionInsets.clone() : null;
		if( insets != null ) {
			if( rowSelOnly && leftSelected )
				insets.left = 0;
			if( rowSelOnly && rightSelected )
				insets.right = 0;
			if( colSelOnly && topSelected )
				insets.top = 0;
			if( colSelOnly && bottomSelected )
				insets.bottom = 0;
		}

		// selection arc
		float arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight;
		arcTopLeft = arcTopRight = arcBottomLeft = arcBottomRight = UIScale.scale( selectionArc / 2f );
		if( selectionArc > 0 ) {
			// note that intercellSpacing is not considered as a gap because
			// grid lines are usually painted to intercell space
			boolean hasRowGap = (rowSelOnly || cellOnlySel) && insets != null && (insets.top != 0 || insets.bottom != 0);
			boolean hasColGap = (colSelOnly || cellOnlySel) && insets != null && (insets.left != 0 || insets.right != 0);

			if( leftSelected && !hasColGap )
				arcTopLeft = arcBottomLeft = 0;
			if( rightSelected && !hasColGap )
				arcTopRight = arcBottomRight = 0;
			if( topSelected && !hasRowGap )
				arcTopLeft = arcTopRight = 0;
			if( bottomSelected && !hasRowGap )
				arcBottomLeft = arcBottomRight = 0;
		}

		FlatUIUtils.paintSelection( (Graphics2D) g, x, y, width, height,
			UIScale.scale( insets ), arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight, 0 );
	}

	/**
	 * Paints a cell selection at the given coordinates.
	 * The selection color must be set on the graphics context.
	 * <p>
	 * This method is intended for use in custom cell renderers to support
	 * {@link #selectionArc} and {@link #selectionInsets}.
	 *
	 * @since 3.5
	 */
	public static void paintCellSelection( JTable table, Graphics g, int row, int column, int x, int y, int width, int height ) {
		if( !(table.getUI() instanceof FlatTableUI) )
			return;

		FlatTableUI ui = (FlatTableUI) table.getUI();
		ui.paintCellSelection( g, row, column, x, y, width, height );
	}

	private void installRepaintRoundedSelectionListeners() {
		if( rowSelectionListener == null ) {
			rowSelectionListener = this::repaintRoundedRowSelection;
			table.getSelectionModel().addListSelectionListener( rowSelectionListener );
		}

		if( columnSelectionListener == null ) {
			columnSelectionListener = new TableColumnModelListener() {
				@Override
				public void columnSelectionChanged( ListSelectionEvent e ) {
					repaintRoundedColumnSelection( e );
				}
				@Override public void columnRemoved( TableColumnModelEvent e ) {}
				@Override public void columnMoved( TableColumnModelEvent e ) {}
				@Override public void columnMarginChanged( ChangeEvent e ) {}
				@Override public void columnAdded( TableColumnModelEvent e ) {}
			};
			table.getColumnModel().addColumnModelListener( columnSelectionListener );
		}
	}

	private void repaintRoundedRowSelection( ListSelectionEvent e ) {
		if( selectionArc <= 0 || !table.getRowSelectionAllowed() )
			return;

		int rowCount = table.getRowCount();
		int columnCount = table.getColumnCount();
		if( rowCount <= 0 || columnCount <= 0 )
			return;

		// repaint including rows before and after changed selection
		int firstRow = Math.max( 0, Math.min( e.getFirstIndex() - 1, rowCount - 1 ) );
		int lastRow = Math.max( 0, Math.min( e.getLastIndex() + 1, rowCount - 1 ) );
		Rectangle firstRect = table.getCellRect( firstRow, 0, false );
		Rectangle lastRect = table.getCellRect( lastRow, columnCount - 1, false );
		table.repaint( firstRect.union( lastRect ) );
	}

	private void repaintRoundedColumnSelection( ListSelectionEvent e ) {
		if( selectionArc <= 0 || !table.getColumnSelectionAllowed() )
			return;

		int rowCount = table.getRowCount();
		int columnCount = table.getColumnCount();
		if( rowCount <= 0 || columnCount <= 0 )
			return;

		// limit to selected rows for cell selection
		int firstRow = 0;
		int lastRow = rowCount - 1;
		if( table.getRowSelectionAllowed() ) {
			firstRow = table.getSelectionModel().getMinSelectionIndex();
			lastRow = table.getSelectionModel().getMaxSelectionIndex();
		}

		// repaint including columns before and after changed selection
		int firstColumn = Math.max( 0, Math.min( e.getFirstIndex() - 1, columnCount - 1 ) );
		int lastColumn = Math.max( 0, Math.min( e.getLastIndex() + 1, columnCount - 1 ) );
		Rectangle firstRect = table.getCellRect( firstRow, firstColumn, false );
		Rectangle lastRect = table.getCellRect( lastRow, lastColumn, false );
		table.repaint( firstRect.union( lastRect ) );
	}

	//---- class RoundedSelectionGraphics -------------------------------------

	/**
	 * Because selection painting is done in the cell renderer, it would be
	 * necessary to require a FlatLaf specific renderer to implement rounded selection.
	 * Using a LaF specific renderer was avoided because often a custom renderer is
	 * already used in applications. Then either the rounded selection is not used,
	 * or the application has to be changed to extend a FlatLaf renderer.
	 * <p>
	 * To solve this, a graphics proxy is used that paints rounded selection
	 * if row/column/cell is selected and the renderer wants to fill the background.
	 */
	private class RoundedSelectionGraphics
		extends Graphics2DProxy
	{
		private final Color alternateRowColor;

		// used to avoid endless loop in case that paintCellSelection() invokes
		// g.fillRect() with full bounds (selectionInsets is 0,0,0,0)
		private boolean inPaintSelection;

		RoundedSelectionGraphics( Graphics delegate, Color alternateRowColor ) {
			super( (Graphics2D) delegate );
			this.alternateRowColor = alternateRowColor;
		}

		@Override
		public Graphics create() {
			return new RoundedSelectionGraphics( super.create(), alternateRowColor );
		}

		@Override
		public Graphics create( int x, int y, int width, int height ) {
			return new RoundedSelectionGraphics( super.create( x, y, width, height ), alternateRowColor );
		}

		@Override
		public void fillRect( int x, int y, int width, int height ) {
			if( fillCellSelection( x, y, width, height ) )
				return;

			super.fillRect( x, y, width, height );
		}

		@Override
		public void fill( Shape shape ) {
			if( shape instanceof Rectangle2D ) {
				Rectangle2D r = (Rectangle2D) shape;
				double x = r.getX();
				double y = r.getY();
				double width = r.getWidth();
				double height = r.getHeight();
				if( x == (int) x && y == (int) y && width == (int) width && height == (int) height ) {
					if( fillCellSelection( (int) x, (int) y, (int) width, (int) height ) )
						return;
				}
			}

			super.fill( shape );
		}

		private boolean fillCellSelection( int x, int y, int width, int height ) {
			if( inPaintSelection )
				return false;

			Color color;
			Component rendererComponent;
			if( x == 0 && y == 0 &&
				((color = getColor()) == table.getSelectionBackground() ||
				 (alternateRowColor != null && color == alternateRowColor)) &&
				(rendererComponent = findActiveRendererComponent()) != null &&
				width == rendererComponent.getWidth() &&
				height == rendererComponent.getHeight() )
			{
				Point location = rendererComponent.getLocation();
				int row = table.rowAtPoint( location );
				int column = table.columnAtPoint( location );
				if( row >= 0 && column >= 0 ) {
					inPaintSelection = true;
					if( color == table.getSelectionBackground() )
						paintCellSelection( this, row, column, x, y, width, height );
					else
						paintAlternateRowBackground( this, row, column, x, y, width, height );
					inPaintSelection = false;

					return true;
				}
			}
			return false;
		}

		/**
		 * A CellRendererPane may contain multiple components, if multiple renderers
		 * are used. Inactive renderer components have size {@code 0x0}.
		 */
		private Component findActiveRendererComponent() {
			int count = rendererPane.getComponentCount();
			for( int i = 0; i < count; i++ ) {
				Component c = rendererPane.getComponent( i );
				if( c.getWidth() > 0 && c.getHeight() > 0 )
					return c;
			}
			return null;
		}
	}

	//---- class OutsideAlternateRowsListener ---------------------------------

	/**
	 * Used if table auto-resize-mode is off to repaint outside alternate rows
	 * when table width changed (column resized) or component orientation changed.
	 */
	private class FlatOutsideAlternateRowsListener
		extends ComponentAdapter
	{
		@Override
		public void componentHidden( ComponentEvent e ) {
			Container viewport = SwingUtilities.getUnwrappedParent( table );
			if( viewport instanceof JViewport )
				HiDPIUtils.repaint( viewport );
		}

		@Override
		public void componentMoved( ComponentEvent e ) {
			repaintAreaBelowTable();
		}

		@Override
		public void componentResized( ComponentEvent e ) {
			repaintAreaBelowTable();
		}

		private void repaintAreaBelowTable() {
			Container viewport = SwingUtilities.getUnwrappedParent( table );
			if( viewport instanceof JViewport ) {
				int viewportHeight = viewport.getHeight();
				int tableHeight = table.getHeight();
				if( tableHeight < viewportHeight )
					HiDPIUtils.repaint( viewport, 0, tableHeight, viewport.getWidth(), viewportHeight - tableHeight );
			}
		}
	}

	//---- class FlatTablePropertyWatcher -------------------------------------

	/**
	 * Listener that watches for change of some table properties from application code.
	 * This information is used in {@link FlatTableUI#installDefaults()} and
	 * {@link FlatTableUI#uninstallDefaults()} to decide whether FlatLaf modifies those properties.
	 * If they are modified in application code, FlatLaf no longer changes them.
	 *
	 * The listener is added once for each table, but never removed.
	 * So switching Laf/theme reuses existing listener.
	 */
	private static class FlatTablePropertyWatcher
		implements PropertyChangeListener
	{
		boolean enabled = true;
		boolean showHorizontalLinesChanged;
		boolean showVerticalLinesChanged;
		boolean intercellSpacingChanged;

		static FlatTablePropertyWatcher get( JTable table ) {
			for( PropertyChangeListener l : table.getPropertyChangeListeners() ) {
				if( l instanceof FlatTablePropertyWatcher )
					return (FlatTablePropertyWatcher) l;
			}
			return null;
		}

		//---- interface PropertyChangeListener ----

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			if( !enabled )
				return;

			switch( e.getPropertyName() ) {
				case "showHorizontalLines":	showHorizontalLinesChanged = true; break;
				case "showVerticalLines":	showVerticalLinesChanged = true; break;
				case "rowMargin":			intercellSpacingChanged = true; break;
			}
		}
	}

	//---- class FlatBooleanRenderer ------------------------------------------

	private static class FlatBooleanRenderer
		extends DefaultTableCellRenderer
		implements UIResource
	{
		private boolean selected;

		FlatBooleanRenderer() {
			setHorizontalAlignment( SwingConstants.CENTER );
			setIcon( new FlatCheckBoxIcon() {
				@Override
				protected boolean isSelected( Component c ) {
					return selected;
				}
			} );
		}

		@Override
		protected void setValue( Object value ) {
			selected = (value != null && (Boolean) value);
		}
	}

	//---- class StartEditingAction -------------------------------------------

	private static class StartEditingAction
		extends FlatUIAction
	{
		static void install( ActionMap map, String key ) {
			Action oldAction = map.get( key );
			if( oldAction == null || oldAction instanceof StartEditingAction )
				return; // not found or already installed

			map.put( key, new StartEditingAction( oldAction ) );
		}

		private StartEditingAction( Action delegate ) {
			super( delegate );
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			JTable table = (JTable) e.getSource();

			Component oldEditorComp = table.getEditorComponent();

			delegate.actionPerformed( e );

			// select all text in editor if editing starts with F2 key
			Component editorComp = table.getEditorComponent();
			if( oldEditorComp == null && editorComp instanceof JTextField )
				((JTextField)editorComp).selectAll();
		}
	}
}
