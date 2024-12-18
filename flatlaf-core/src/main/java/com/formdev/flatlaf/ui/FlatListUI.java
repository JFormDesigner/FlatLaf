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
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicListUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JList}.
 *
 * <!-- BasicListUI -->
 *
 * @uiDefault List.font								Font
 * @uiDefault List.background						Color
 * @uiDefault List.foreground						Color
 * @uiDefault List.selectionBackground				Color
 * @uiDefault List.selectionForeground				Color
 * @uiDefault List.alternateRowColor				Color
 * @uiDefault List.dropLineColor					Color
 * @uiDefault List.border							Border
 * @uiDefault List.cellRenderer						ListCellRenderer
 * @uiDefault FileChooser.listFont					Font		used if client property List.isFileList is true
 *
 * <!-- DefaultListCellRenderer -->
 *
 * @uiDefault List.cellNoFocusBorder				Border
 * @uiDefault List.focusCellHighlightBorder			Border
 * @uiDefault List.focusSelectedCellHighlightBorder	Border
 * @uiDefault List.dropCellBackground				Color
 * @uiDefault List.dropCellForeground				Color
 *
 * <!-- FlatListUI -->
 *
 * @uiDefault List.selectionInactiveBackground		Color
 * @uiDefault List.selectionInactiveForeground		Color
 * @uiDefault List.selectionInsets					Insets
 * @uiDefault List.selectionArc						int
 *
 * <!-- FlatListCellBorder -->
 *
 * @uiDefault List.cellMargins						Insets
 * @uiDefault List.cellFocusColor					Color
 * @uiDefault List.showCellFocusIndicator			boolean
 *
 * @author Karl Tauber
 */
public class FlatListUI
	extends BasicListUI
	implements StyleableUI
{
	@Styleable protected Color selectionBackground;
	@Styleable protected Color selectionForeground;
	@Styleable protected Color selectionInactiveBackground;
	@Styleable protected Color selectionInactiveForeground;
	/** @since 3.6 */ @Styleable protected Color alternateRowColor;
	/** @since 3 */ @Styleable protected Insets selectionInsets;
	/** @since 3 */ @Styleable protected int selectionArc;

	// for FlatListCellBorder
	/** @since 2 */ @Styleable protected Insets cellMargins;
	/** @since 2 */ @Styleable protected Color cellFocusColor;
	/** @since 2 */ @Styleable protected Boolean showCellFocusIndicator;

	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatListUI();
	}

	@Override
	public void installUI( JComponent c ) {
		if( FlatUIUtils.needsLightAWTPeer( c ) )
			FlatUIUtils.runWithLightAWTPeerUIDefaults( () -> installUIImpl( c ) );
		else
			installUIImpl( c );
	}

	private void installUIImpl( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		selectionBackground = UIManager.getColor( "List.selectionBackground" );
		selectionForeground = UIManager.getColor( "List.selectionForeground" );
		selectionInactiveBackground = UIManager.getColor( "List.selectionInactiveBackground" );
		selectionInactiveForeground = UIManager.getColor( "List.selectionInactiveForeground" );
		alternateRowColor = UIManager.getColor( "List.alternateRowColor" );
		selectionInsets = UIManager.getInsets( "List.selectionInsets" );
		selectionArc = UIManager.getInt( "List.selectionArc" );

		toggleSelectionColors();
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		selectionBackground = null;
		selectionForeground = null;
		selectionInactiveBackground = null;
		selectionInactiveForeground = null;
		alternateRowColor = null;

		oldStyleValues = null;
	}

	@Override
	protected FocusListener createFocusListener() {
		return new BasicListUI.FocusHandler() {
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
	protected PropertyChangeListener createPropertyChangeListener() {
		PropertyChangeListener superListener = super.createPropertyChangeListener();
		return e -> {
			superListener.propertyChange( e );

			switch( e.getPropertyName() ) {
				case FlatClientProperties.COMPONENT_FOCUS_OWNER:
					toggleSelectionColors();
					break;

				case FlatClientProperties.STYLE:
				case FlatClientProperties.STYLE_CLASS:
					installStyle();
					list.revalidate();
					HiDPIUtils.repaint( list );
					break;
			}
		};
	}

	@Override
	protected ListSelectionListener createListSelectionListener() {
		ListSelectionListener superListener = super.createListSelectionListener();
		return e -> {
			superListener.valueChanged( e );

			// for united rounded selection, repaint parts of the rows/columns that adjoin to the changed rows/columns
			if( useUnitedRoundedSelection( true, true ) &&
				!list.isSelectionEmpty() &&
				(list.getMaxSelectionIndex() - list.getMinSelectionIndex()) >= 1 )
			{
				int size = list.getModel().getSize();
				int firstIndex = Math.min( Math.max( e.getFirstIndex(), 0 ), size - 1 );
				int lastIndex = Math.min( Math.max( e.getLastIndex(), 0 ), size - 1 );
				Rectangle r = getCellBounds( list, firstIndex, lastIndex );
				if( r != null ) {
					int arc = (int) Math.ceil( UIScale.scale( selectionArc / 2f ) );
					HiDPIUtils.repaint( list, r.x - arc, r.y - arc, r.width + (arc * 2), r.height + (arc * 2) );
				}
			}
		};
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( list, "List" ) );
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
			Color selBg = list.getSelectionBackground();
			if( selBg == oldSelectionBackground )
				list.setSelectionBackground( selectionBackground );
			else if( selBg == oldSelectionInactiveBackground )
				list.setSelectionBackground( selectionInactiveBackground );
		}

		// update selection foreground
		if( selectionForeground != oldSelectionForeground ) {
			Color selFg = list.getSelectionForeground();
			if( selFg == oldSelectionForeground )
				list.setSelectionForeground( selectionForeground );
			else if( selFg == oldSelectionInactiveForeground )
				list.setSelectionForeground( selectionInactiveForeground );
		}
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, list, key, value );
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
		if( list == null )
			return;

		if( FlatUIUtils.isPermanentFocusOwner( list ) ) {
			if( list.getSelectionBackground() == selectionInactiveBackground )
				list.setSelectionBackground( selectionBackground );
			if( list.getSelectionForeground() == selectionInactiveForeground )
				list.setSelectionForeground( selectionForeground );
		} else {
			if( list.getSelectionBackground() == selectionBackground )
				list.setSelectionBackground( selectionInactiveBackground );
			if( list.getSelectionForeground() == selectionForeground )
				list.setSelectionForeground( selectionInactiveForeground );
		}
	}

	@SuppressWarnings( "rawtypes" )
	@Override
	protected void paintCell( Graphics g, int row, Rectangle rowBounds, ListCellRenderer cellRenderer,
		ListModel dataModel, ListSelectionModel selModel, int leadIndex )
	{
		boolean isSelected = selModel.isSelectedIndex( row );

		// paint alternating rows
		if( alternateRowColor != null && row % 2 != 0 &&
			!"ComboBox.list".equals( list.getName() ) ) // combobox does not support alternate row color
		{
			g.setColor( alternateRowColor );

			float arc = UIScale.scale( selectionArc / 2f );
			FlatUIUtils.paintSelection( (Graphics2D) g, rowBounds.x, rowBounds.y, rowBounds.width, rowBounds.height,
				UIScale.scale( selectionInsets ), arc, arc, arc, arc, 0 );
		}

		// get renderer component
		@SuppressWarnings( "unchecked" )
		Component rendererComponent = cellRenderer.getListCellRendererComponent( list,
			dataModel.getElementAt( row ), row, isSelected,
			FlatUIUtils.isPermanentFocusOwner( list ) && (row == leadIndex) );

		// use smaller cell width if list is used in JFileChooser
		boolean isFileList = Boolean.TRUE.equals( list.getClientProperty( "List.isFileList" ) );
		int cx, cw;
		if( isFileList ) {
			// see BasicListUI.paintCell()
			cw = Math.min( rowBounds.width, rendererComponent.getPreferredSize().width + 4 );
			cx = list.getComponentOrientation().isLeftToRight()
				? rowBounds.x
				: rowBounds.x + (rowBounds.width - cw);
		} else {
			cx = rowBounds.x;
			cw = rowBounds.width;
		}

		// rounded selection or selection insets
		if( isSelected &&
			!isFileList && // rounded selection is not supported for file list
			(rendererComponent instanceof DefaultListCellRenderer ||
			 rendererComponent instanceof BasicComboBoxRenderer) &&
			(selectionArc > 0 ||
			 (selectionInsets != null && !FlatUIUtils.isInsetsEmpty( selectionInsets ))) )
		{
			// Because selection painting is done in the cell renderer, it would be
			// necessary to require a FlatLaf specific renderer to implement rounded selection.
			// Using a LaF specific renderer was avoided because often a custom renderer is
			// already used in applications. Then either the rounded selection is not used,
			// or the application has to be changed to extend a FlatLaf renderer.
			//
			// To solve this, a graphics proxy is used that paints rounded selection
			// if row is selected and the renderer wants to fill the background.
			class RoundedSelectionGraphics extends Graphics2DProxy {
				// used to avoid endless loop in case that paintCellSelection() invokes
				// g.fillRect() with full bounds (selectionInsets is 0,0,0,0)
				private boolean inPaintSelection;

				RoundedSelectionGraphics( Graphics delegate ) {
					super( (Graphics2D) delegate );
				}

				@Override
				public Graphics create() {
					return new RoundedSelectionGraphics( super.create() );
				}

				@Override
				public Graphics create( int x, int y, int width, int height ) {
					return new RoundedSelectionGraphics( super.create( x, y, width, height ) );
				}

				@Override
				public void fillRect( int x, int y, int width, int height ) {
					if( !inPaintSelection &&
						x == 0 && y == 0 && width == rowBounds.width && height == rowBounds.height &&
						this.getColor() == rendererComponent.getBackground() )
					{
						inPaintSelection = true;
						paintCellSelection( this, row, x, y, width, height );
						inPaintSelection = false;
					} else
						super.fillRect( x, y, width, height );
				}
			}
			g = new RoundedSelectionGraphics( g );
		}

		// paint renderer
		rendererPane.paintComponent( g, rendererComponent, list, cx, rowBounds.y, cw, rowBounds.height, true );
	}

	/**
	 * Paints (rounded) cell selection.
	 * Supports {@link #selectionArc} and {@link #selectionInsets}.
	 * <p>
	 * <b>Note:</b> This method is only invoked if either selection arc
	 *              is greater than zero or if selection insets are not empty.
	 *
	 * @since 3
	 */
	protected void paintCellSelection( Graphics g, int row, int x, int y, int width, int height ) {
		float arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight;
		arcTopLeft = arcTopRight = arcBottomLeft = arcBottomRight = UIScale.scale( selectionArc / 2f );

		if( list.getLayoutOrientation() == JList.VERTICAL ) {
			// layout orientation: VERTICAL
			if( useUnitedRoundedSelection( true, false ) ) {
				if( row > 0 && list.isSelectedIndex( row - 1 ) )
					arcTopLeft = arcTopRight = 0;
				if( row < list.getModel().getSize() - 1 && list.isSelectedIndex( row + 1 ) )
					arcBottomLeft = arcBottomRight = 0;
			}
		} else {
			// layout orientation: VERTICAL_WRAP or HORIZONTAL_WRAP
			Rectangle r = null;
			if( useUnitedRoundedSelection( true, false ) ) {
				// vertical: check whether cells above or below are selected
				r = getCellBounds( list, row, row );

				int topIndex = locationToIndex( list, new Point( r.x, r.y - 1 ) );
				int bottomIndex = locationToIndex( list, new Point( r.x, r.y + r.height ) );

				if( topIndex >= 0 && topIndex != row && list.isSelectedIndex( topIndex ) )
					arcTopLeft = arcTopRight = 0;
				if( bottomIndex >= 0 && bottomIndex != row && list.isSelectedIndex( bottomIndex ) )
					arcBottomLeft = arcBottomRight = 0;
			}

			if( useUnitedRoundedSelection( false, true ) ) {
				// horizontal: check whether cells left or right are selected
				if( r == null )
					r = getCellBounds( list, row, row );

				int leftIndex = locationToIndex( list, new Point( r.x - 1, r.y ) );
				int rightIndex = locationToIndex( list, new Point( r.x + r.width, r.y ) );

				// special handling for the case that last column contains fewer cells than the other columns
				boolean ltr = list.getComponentOrientation().isLeftToRight();
				if( !ltr && leftIndex >= 0 && leftIndex != row && leftIndex == locationToIndex( list, new Point( r.x - 1, r.y - 1 ) ) )
					leftIndex = -1;
				if( ltr && rightIndex >= 0 && rightIndex != row && rightIndex == locationToIndex( list, new Point( r.x + r.width, r.y - 1 ) ) )
					rightIndex = -1;

				if( leftIndex >= 0 && leftIndex != row && list.isSelectedIndex( leftIndex ) )
					arcTopLeft = arcBottomLeft = 0;
				if( rightIndex >= 0 && rightIndex != row && list.isSelectedIndex( rightIndex ) )
					arcTopRight = arcBottomRight = 0;
			}
		}

		FlatUIUtils.paintSelection( (Graphics2D) g, x, y, width, height,
			UIScale.scale( selectionInsets ), arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight, 0 );
	}

	private boolean useUnitedRoundedSelection( boolean vertical, boolean horizontal ) {
		return selectionArc > 0 &&
			(selectionInsets == null ||
			 (vertical && selectionInsets.top == 0 && selectionInsets.bottom == 0) ||
			 (horizontal && selectionInsets.left == 0 && selectionInsets.right == 0));
	}

	/**
	 * Paints a cell selection at the given coordinates.
	 * The selection color must be set on the graphics context.
	 * <p>
	 * This method is intended for use in custom cell renderers
	 * to support {@link #selectionArc} and {@link #selectionInsets}.
	 *
	 * @since 3
	 */
	public static void paintCellSelection( JList<?> list, Graphics g, int row, int x, int y, int width, int height ) {
		if( !(list.getUI() instanceof FlatListUI) )
			return;

		FlatListUI ui = (FlatListUI) list.getUI();
		ui.paintCellSelection( g, row, x, y, width, height );
	}
}
