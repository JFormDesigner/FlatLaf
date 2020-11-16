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

package com.formdev.flatlaf.testing.jideoss;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.testing.FlatTestFrame;
import com.formdev.flatlaf.testing.FlatTestPanel;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.RangeSlider;
import net.miginfocom.swing.MigLayout;

public class FlatRangeSliderTest
	extends FlatTestPanel
{

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatRangeSliderTest" );
			LookAndFeelFactory.installJideExtension();
			frame.showFrame( FlatRangeSliderTest::new );

			UIManager.addPropertyChangeListener( e -> {
				if( "lookAndFeel".equals( e.getPropertyName() ) ) {
					LookAndFeelFactory.installJideExtension();
				}
			} );
		} );
	}

	FlatRangeSliderTest() {
		initComponents();
	}

	private void paintLabels() {
		horizontalRangeSlider.setPaintLabels( paintLabel.isSelected() );
		verticalRangeSlider.setPaintLabels( paintLabel.isSelected() );
	}

	private void paintTicks() {
		horizontalRangeSlider.setPaintTicks( paintTick.isSelected() );
		verticalRangeSlider.setPaintTicks( paintTick.isSelected() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel tabbedPaneLabel = new JLabel();
		JLabel horizontalLabel = new JLabel();
		horizontalRangeSlider = new RangeSlider();
		JLabel verticalLabel = new JLabel();
		verticalRangeSlider = new RangeSlider();
		paintTick = new JCheckBox();
		paintLabel = new JCheckBox();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[left]" +
			"[fill]",
			// rows
			"[fill]" +
			"[center]" +
			"[grow,fill]" +
			"[]"));

		//---- tabbedPaneLabel ----
		tabbedPaneLabel.setText("RangeSlider:");
		add(tabbedPaneLabel, "cell 0 0");

		//---- horizontalLabel ----
		horizontalLabel.setText("Horizontal");
		add(horizontalLabel, "cell 0 1");
		add(horizontalRangeSlider, "cell 1 1");

		//---- verticalLabel ----
		verticalLabel.setText("Vertical");
		add(verticalLabel, "cell 0 2,aligny top,growy 0");

		//---- verticalRangeSlider ----
		verticalRangeSlider.setOrientation(SwingConstants.VERTICAL);
		add(verticalRangeSlider, "cell 1 2,alignx left,growx 0");

		//---- paintTick ----
		paintTick.setText("PaintTicks");
		paintTick.setMnemonic('T');
		paintTick.setSelected(true);
		paintTick.addActionListener(e -> paintTicks());
		add(paintTick, "cell 0 3 2 1");

		//---- paintLabel ----
		paintLabel.setText("PaintLabels");
		paintLabel.setMnemonic('L');
		paintLabel.setSelected(true);
		paintLabel.addActionListener(e -> paintLabels());
		add(paintLabel, "cell 0 3 2 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		horizontalRangeSlider.setOrientation( SwingConstants.HORIZONTAL );
		horizontalRangeSlider.setMinimum( 0 );
		horizontalRangeSlider.setMaximum( 100 );
		horizontalRangeSlider.setLowValue( 10 );
		horizontalRangeSlider.setHighValue( 90 );
		horizontalRangeSlider.setLabelTable( horizontalRangeSlider.createStandardLabels( 10 ) );
		horizontalRangeSlider.setMinorTickSpacing( 5 );
		horizontalRangeSlider.setMajorTickSpacing( 10 );
		horizontalRangeSlider.setPaintTicks( true );
		horizontalRangeSlider.setPaintLabels( true );

		verticalRangeSlider.setOrientation( SwingConstants.VERTICAL );
		verticalRangeSlider.setMinimum( 0 );
		verticalRangeSlider.setMaximum( 100 );
		verticalRangeSlider.setLowValue( 10 );
		verticalRangeSlider.setHighValue( 90 );
		verticalRangeSlider.setLabelTable( horizontalRangeSlider.createStandardLabels( 10 ) );
		verticalRangeSlider.setMinorTickSpacing( 5 );
		verticalRangeSlider.setMajorTickSpacing( 10 );
		verticalRangeSlider.setPaintTicks( true );
		verticalRangeSlider.setPaintLabels( true );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private RangeSlider horizontalRangeSlider;
	private RangeSlider verticalRangeSlider;
	private JCheckBox paintTick;
	private JCheckBox paintLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
