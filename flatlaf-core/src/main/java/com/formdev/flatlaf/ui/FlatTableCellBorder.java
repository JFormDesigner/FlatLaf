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
import java.awt.Graphics;
import java.awt.Insets;
import java.util.function.Function;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.TableUI;

/**
 * Cell border for {@link javax.swing.table.DefaultTableCellRenderer}
 * (used by {@link javax.swing.JTable}).
 * <p>
 * Uses separate cell margins from UI defaults to allow easy customizing.
 *
 * @author Karl Tauber
 */
public class FlatTableCellBorder
	extends FlatLineBorder
{
	/** @since 2 */ protected boolean showCellFocusIndicator = UIManager.getBoolean( "Table.showCellFocusIndicator" );

	private Component c;

	protected FlatTableCellBorder() {
		super( UIManager.getInsets( "Table.cellMargins" ), UIManager.getColor( "Table.cellFocusColor" ) );
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		Insets m = getStyleFromTableUI( c, ui -> ui.cellMargins );
		if( m != null )
			return scaleInsets( c, insets, m.top, m.left, m.bottom, m.right );

		return super.getBorderInsets( c, insets );
	}

	@Override
	public Color getLineColor() {
		if( c != null ) {
			Color color = getStyleFromTableUI( c, ui -> ui.cellFocusColor );
			if( color != null )
				return color;
		}
		return super.getLineColor();
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		this.c = c;
		super.paintBorder( c, g, x, y, width, height );
		this.c = null;
	}

	/**
	 * Because this border is always shared for all tables,
	 * get border specific style from FlatTableUI.
	 */
	static <T> T getStyleFromTableUI( Component c, Function<FlatTableUI, T> f ) {
		JTable table = (JTable) SwingUtilities.getAncestorOfClass( JTable.class, c );
		if( table != null ) {
			TableUI ui = table.getUI();
			if( ui instanceof FlatTableUI )
				return f.apply( (FlatTableUI) ui );
		}
		return null;
	}

	//---- class Default ------------------------------------------------------

	/**
	 * Border for unselected cell that uses margins, but does not paint focus indicator border.
	 */
	public static class Default
		extends FlatTableCellBorder
	{
		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			// do not paint focus indicator border
		}
	}

	//---- class Focused ------------------------------------------------------

	/**
	 * Border for focused unselected cell that uses margins and paints focus indicator border.
	 */
	public static class Focused
		extends FlatTableCellBorder
	{
		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			if( c != null && c.getClass().getName().equals( "javax.swing.JTable$BooleanRenderer" ) ) {
				// boolean renderer in JTable does not use Table.focusSelectedCellHighlightBorder
				// if cell is selected and focused (as DefaultTableCellRenderer does)
				// --> delegate to Table.focusSelectedCellHighlightBorder
				//     to make FlatLaf "focus indicator border hiding" work
				JTable table = (JTable) SwingUtilities.getAncestorOfClass( JTable.class, c );
				if( table != null &&
					c.getForeground() == table.getSelectionForeground() &&
					c.getBackground() == table.getSelectionBackground() )
				{
					Border border = UIManager.getBorder( "Table.focusSelectedCellHighlightBorder" );
					if( border != null ) {
						border.paintBorder( c, g, x, y, width, height );
						return;
					}
				}
			}

			super.paintBorder( c, g, x, y, width, height );
		}
	}

	//---- class Selected -----------------------------------------------------

	/**
	 * Border for selected cell that uses margins and paints focus indicator border
	 * <ul>
	 * <li>if enabled (Table.showCellFocusIndicator=true) or
	 * <li>if column selection is enabled and row selection disabled or
	 * <li>if cells in only one row are selected and at least one cell is editable
	 * </ul>
	 * The reason for this logic is to hide the focus indicator when it is not needed,
	 * and only show it when there are editable cells and the user needs to know
	 * which cell is focused to start editing.
	 * <p>
	 * To avoid possible performance issues, checking for editable cell is limited
	 * to {@link #maxCheckCellsEditable}.
	 */
	public static class Selected
		extends FlatTableCellBorder
	{
		/** @since 3.1 */
		public int maxCheckCellsEditable = 50;

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			Boolean b = getStyleFromTableUI( c, ui -> ui.showCellFocusIndicator );
			boolean showCellFocusIndicator = (b != null) ? b : this.showCellFocusIndicator;

			if( !showCellFocusIndicator ) {
				JTable table = (JTable) SwingUtilities.getAncestorOfClass( JTable.class, c );
				if( table != null && !shouldShowCellFocusIndicator( table ) )
					return;
			}

			super.paintBorder( c, g, x, y, width, height );
		}

		/**
		 * Returns whether focus indicator border should be shown.
		 *
		 * @since 3.1
		 */
		protected boolean shouldShowCellFocusIndicator( JTable table ) {
			// show always for column only selection
			// (do not check for editable cells because there may be thousands or millions of rows)
			if( !table.getRowSelectionAllowed() )
				return true;

			// do not show if more than one row is selected
			// (unlikely that user wants edit cell in this case)
			if( table.getSelectedRowCount() != 1 )
				return false;

			int selectedRow = table.getSelectedRow();

			if( table.getColumnSelectionAllowed() ) {
				// column and row selection are allowed --> cell selection enabled
				int selectedColumnCount = table.getSelectedColumnCount();

				// do not show if exactly one cell is selected
				if( selectedColumnCount == 1 )
					return false;

				// show always if there are too many columns to check for editable
				if( selectedColumnCount > maxCheckCellsEditable )
					return true;

				// check whether at least one selected cell is editable
				for( int selectedColumn : table.getSelectedColumns() ) {
					if( table.isCellEditable( selectedRow, selectedColumn ) )
						return true;
				}
			} else {
				// row selection enabled
				int columnCount = table.getColumnCount();

				// show always if there are too many columns to check for editable
				if( columnCount > maxCheckCellsEditable )
					return true;

				// check whether at least one selected cell is editable
				for( int column = 0; column < columnCount; column++ ) {
					if( table.isCellEditable( selectedRow, column ) )
						return true;
				}
			}

			return false;
		}
	}
}
