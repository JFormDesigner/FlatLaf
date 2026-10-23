/*
 * Copyright 2026 FormDev Software, Karl Tauber
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

package com.formdev.flatlaf.testing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * @author Karl Tauber
 */
public class FlatTableHiDPIRenderingTest
	extends FlatSingleComponentTest
{
	public static void main( String[] args ) {
//		System.setProperty( FlatSystemProperties.USE_WINDOW_DECORATIONS, "false" );

		EventQueue.invokeLater( () -> {
			launch( FlatTableHiDPIRenderingTest::new, args );
		} );
	}

	@Override
	protected JComponent createSingleComponent() {
		UIManager.put( "Table.showVerticalLines", true );
		UIManager.put( "Table.showHorizontalLines", true );
		UIManager.put( "Table.intercellSpacing", new Dimension( 1, 1 ) );
		UIManager.put( "Table.gridColor", new Color( 0xd9d9d9 ) );
//		UIManager.put( "Table.alternateRowColor", new Color( 0xc9c9c9 ) );
//		UIManager.put( "Table.paintOutsideAlternateRows", true );
		UIManager.put( "Table.rowHeight", 21 );
//		UIManager.put( "Table.selectionArc", 999 );
//		UIManager.put( "Table.selectionInsets", new Insets( 2, 2, 2, 2 ) );
		UIManager.put( "Table.cellMargins", new Insets( 1, 3, 1, 3 ) );

		AbstractTableModel tableModel = new AbstractTableModel() {
			@Override public int getRowCount() { return 50; }
			@Override public int getColumnCount() { return 3; }

			@Override
			public String getColumnName( int column ) {
				switch( column ) {
					case 0: return "cell (logical px)";
					case 1: return "cell (device px)";
					case 2: return "gap (device px)";
				}
				return super.getColumnName( column );
			}

			@Override
			public Object getValueAt( int rowIndex, int columnIndex ) {
				return "Cell " + rowIndex + " / " + columnIndex;
			}
		};

		JTable table = new JTable( tableModel );
		JScrollPane scrollPane = new JScrollPane( table );

		table.setDefaultRenderer( Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column )
			{
				JComponent c = (JComponent) super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
				c.setOpaque( true );
				if( !isSelected )
					c.setBackground( new Color( 221, 238, 255 ) );

				Rectangle r = table.getCellRect( row, 0, false );

				if( column == 0 )
					setText( row + ":   y " + r.y + "   h " + r.height );
				else if( column == 1 ) {
					double scaleFactor = UIScale.getSystemScaleFactor( table.getGraphicsConfiguration() );
					AffineTransform t = new AffineTransform( scaleFactor, 0, 0, scaleFactor, 0, 0 );
					Rectangle dr = HiDPIUtils.scaleTo1x( t, r.x, r.y, r.width, r.height );
					setText( "y " + dr.y + "   h " + dr.height );
				} else if( column == 2 ) {
					double scaleFactor = UIScale.getSystemScaleFactor( table.getGraphicsConfiguration() );
					AffineTransform t = new AffineTransform( scaleFactor, 0, 0, scaleFactor, 0, 0 );
					Rectangle gapr = HiDPIUtils.scaleTo1x( t, r.x + r.width, r.y + r.height, 1, 1 );
					setText( "w " + gapr.width + "   h " + gapr.height );
				}
				return c;
			}

/*
			@Override
			protected void paintComponent( java.awt.Graphics g ) {
				int w = getWidth();
				int h = getHeight();

				g.setColor( new Color( 221, 238, 255 ) );
				g.fillRect( 0, 0, w, h );

				g.setColor( g.getColor().darker() );
				g.fillRect( w - 6, 0, 6, 6 );
				g.fillRect( 0, h - 6, 6, 6 );

				super.paintComponent( g );
			}
*/
		} );
//		table.setGridColor( Color.green );
//		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
//		table.selectAll();
//		table.setRowSelectionInterval( 2, 2 );

		return scrollPane;
	}
}
