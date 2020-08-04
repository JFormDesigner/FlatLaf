/*
 * Copyright 2020 FormDev Software GmbH
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

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatSmoothScrollingTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatSmoothScrollingTest" );
			UIManager.put( "ScrollBar.showButtons", true );
			frame.showFrame( FlatSmoothScrollingTest::new );
		} );
	}

	FlatSmoothScrollingTest() {
		initComponents();

		scrollPane1.getVerticalScrollBar().addAdjustmentListener( new AdjustmentHandler( "list vert" ) );
		scrollPane1.getHorizontalScrollBar().addAdjustmentListener( new AdjustmentHandler( "list horz" ) );

		ArrayList<String> items = new ArrayList<>();
		for( char ch = '0'; ch < 'z'; ch++ ) {
			char[] chars = new char[ch - '0' + 1];
			Arrays.fill( chars, ch );
			items.add( new String( chars ) );
		}

		list1.setModel( new AbstractListModel<String>() {
			@Override
			public int getSize() {
				return items.size();
			}

			@Override
			public String getElementAt( int index ) {
				return items.get( index );
			}
		} );
	}

	private void smoothScrollingChanged() {
		UIManager.put( "ScrollPane.smoothScrolling", smoothScrollingCheckBox.isSelected() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		smoothScrollingCheckBox = new JCheckBox();
		listLabel = new JLabel();
		scrollPane1 = new JScrollPane();
		list1 = new JList<>();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[200]",
			// rows
			"[]" +
			"[::200,grow,fill]"));

		//---- smoothScrollingCheckBox ----
		smoothScrollingCheckBox.setText("Smooth scrolling");
		smoothScrollingCheckBox.setSelected(true);
		smoothScrollingCheckBox.addActionListener(e -> smoothScrollingChanged());
		add(smoothScrollingCheckBox, "cell 0 0 2 1,alignx left,growx 0");

		//---- listLabel ----
		listLabel.setText("JList:");
		add(listLabel, "cell 0 1,aligny top,growy 0");

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(list1);
		}
		add(scrollPane1, "cell 1 1,growx");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox smoothScrollingCheckBox;
	private JLabel listLabel;
	private JScrollPane scrollPane1;
	private JList<String> list1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class AdjustmentHandler --------------------------------------------

	private static class AdjustmentHandler
		implements AdjustmentListener
	{
		private final String name;
		private int count;

		AdjustmentHandler( String name ) {
			this.name = name;
		}

		@Override
		public void adjustmentValueChanged( AdjustmentEvent e ) {
			System.out.printf( "%s (%d):  %s  %3d  %b%n",
				name, ++count,
				adjustmentType2Str( e.getAdjustmentType() ),
				e.getValue(),
				e.getValueIsAdjusting() );
		}

		private String adjustmentType2Str( int adjustmentType ) {
			switch( adjustmentType ) {
				case AdjustmentEvent.UNIT_INCREMENT:  return "UNIT_INCREMENT";
				case AdjustmentEvent.UNIT_DECREMENT:  return "UNIT_DECREMENT";
				case AdjustmentEvent.BLOCK_INCREMENT: return "BLOCK_INCREMENT";
				case AdjustmentEvent.BLOCK_DECREMENT: return "BLOCK_DECREMENT";
				case AdjustmentEvent.TRACK:           return "TRACK";
				default:                              return "unknown type";
			}
		}
	}
}
