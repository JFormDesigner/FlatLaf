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

package com.formdev.flatlaf.testing.glazedlists;

import java.util.Comparator;
import javax.swing.*;
import com.formdev.flatlaf.testing.*;
import com.formdev.flatlaf.testing.FlatTestFrame;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatGlazedListsTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatGlazedListsTest" );
			frame.showFrame( FlatGlazedListsTest::new );
		} );
	}

	FlatGlazedListsTest() {
		initComponents();

		EventList<Item> itemEventList = new BasicEventList<>();
		itemEventList.add( new Item( "item 1", "item 1b", "January", 123, null ) );
		itemEventList.add( new Item( "item 2", "item 2b", "February", 456, true ) );
		itemEventList.add( new Item( "item 3", null, "March", null, false ) );
		itemEventList.add( new Item( "item 4", null, "April", 234, true ) );
		itemEventList.add( new Item( "item 5", null, "May", null, false ) );
		itemEventList.add( new Item( "item 6", null, "June", null, null ) );
		itemEventList.add( new Item( "item 7", null, "July", null, null ) );
		itemEventList.add( new Item( "item 8", null, "August", null, null ) );
		itemEventList.add( new Item( "item 9", null, "September", null, null ) );
		itemEventList.add( new Item( "item 10", null, "October", null, null ) );
		itemEventList.add( new Item( "item 11", null, "November", null, null ) );
		itemEventList.add( new Item( "item 12", null, "December", null, null ) );

		Comparator<Item> itemComparator = Comparator.comparing( Item::getName );
		SortedList<Item> sortedItems = new SortedList<>( itemEventList, itemComparator );
		AdvancedTableModel<Item> tableModel = GlazedListsSwing.eventTableModelWithThreadProxyList( sortedItems, new ItemTableFormat() );
		itemsTable.setModel( tableModel );
		TableComparatorChooser<Item> tableComparatorChooser = TableComparatorChooser.install(
			itemsTable, sortedItems, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE );
		tableComparatorChooser.appendComparator( 0, 0, false );

		TableComparatorChooser.setIconPath( "resources/windowsxp" );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		scrollPane1 = new JScrollPane();
		itemsTable = new JTable();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[grow,fill]",
			// rows
			"[grow,fill]"));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(itemsTable);
		}
		add(scrollPane1, "cell 0 0,growx,width 300");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrollPane1;
	private JTable itemsTable;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class Item ---------------------------------------------------------

	private static class Item
	{
		final String name;
		final String desc;
		final String month;
		final Integer number;
		final Boolean bool;

		Item( String name, String desc, String month, Integer number, Boolean bool ) {
			this.name = name;
			this.desc = desc;
			this.month = month;
			this.number = number;
			this.bool = bool;
		}

		String getName() {
			return name;
		}
	}

	//---- class ItemTableFormat ----------------------------------------------

	private static class ItemTableFormat
		implements TableFormat<Item>
	{
		private static String[] COLUMN_NAMES = {
			"Name",
			"Description",
			"Month",
			"Integer",
			"Boolean",
		};

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public String getColumnName( int column ) {
			return COLUMN_NAMES[column];
		}

		@Override
		public Object getColumnValue( Item item, int column ) {
			switch( column ) {
				case 0: return item.name;
				case 1: return item.desc;
				case 2: return item.month;
				case 3: return item.number;
				case 4: return item.bool;
			}
			throw new IllegalStateException();
		}
	}
}
