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
	}

	//---- class Selected -----------------------------------------------------

	/**
	 * Border for selected cell that uses margins and paints focus indicator border
	 * if enabled (Table.showCellFocusIndicator=true) or at least one selected cell is editable.
	 */
	public static class Selected
		extends FlatTableCellBorder
	{
		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			Boolean b = getStyleFromTableUI( c, ui -> ui.showCellFocusIndicator );
			boolean showCellFocusIndicator = (b != null) ? b : this.showCellFocusIndicator;

			if( !showCellFocusIndicator ) {
				JTable table = (JTable) SwingUtilities.getAncestorOfClass( JTable.class, c );
				if( table != null && !isSelectionEditable( table ) )
					return;
			}

			super.paintBorder( c, g, x, y, width, height );
		}

		/**
		 * Checks whether at least one selected cell is editable.
		 */
		protected boolean isSelectionEditable( JTable table ) {
			if( table.getRowSelectionAllowed() ) {
				int columnCount = table.getColumnCount();
				int[] selectedRows = table.getSelectedRows();
				for( int selectedRow : selectedRows ) {
					for( int column = 0; column < columnCount; column++ ) {
						if( table.isCellEditable( selectedRow, column ) )
							return true;
					}
				}
			}

			if( table.getColumnSelectionAllowed() ) {
				int rowCount = table.getRowCount();
				int[] selectedColumns = table.getSelectedColumns();
				for( int selectedColumn : selectedColumns ) {
					for( int row = 0; row < rowCount; row++ ) {
						if( table.isCellEditable( row, selectedColumn ) )
							return true;
					}
				}
			}

			return false;
		}
	}
}
