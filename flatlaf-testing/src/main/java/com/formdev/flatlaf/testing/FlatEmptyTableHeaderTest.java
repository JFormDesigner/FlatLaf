package com.formdev.flatlaf.testing;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class FlatEmptyTableHeaderTest
	extends JScrollPane
{

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatEmptyTableHeaderTest" );
			frame.showFrame( FlatEmptyTableHeaderTest::new );
		} );
	}

	FlatEmptyTableHeaderTest() {
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
		panel.add( new JScrollPane( new JTable( new DefaultTableModel(
			new Object[][] { { "row1" }, { "row2" }, { "row3" }, { "row4" }, { "row5" } }, new Object[] { "" } ) ) ) );
		panel.add( new JScrollPane( new JTable(
			new DefaultTableModel( new Object[][] { { "row1" }, { "row2" }, { "row3" }, { "row4" }, { "row5" } },
				new Object[] { "classic header table" } ) ) ) );
		setViewportView( panel );
	}

}
