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
import javax.swing.JSlider;
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
		boolean selected = paintLabel.isSelected();
		horizontalRangeSlider.setPaintLabels( selected );
		verticalRangeSlider.setPaintLabels( selected );
		horizontalSlider.setPaintLabels( selected );
		verticalSlider.setPaintLabels( selected );
	}

	private void paintTicks() {
		boolean selected = paintTick.isSelected();
		horizontalRangeSlider.setPaintTicks( selected );
		verticalRangeSlider.setPaintTicks( selected );
		horizontalSlider.setPaintTicks( selected );
		verticalSlider.setPaintTicks( selected );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel tabbedPaneLabel = new JLabel();
		JLabel horizontalLabel = new JLabel();
		horizontalRangeSlider = new RangeSlider();
		horizontalSlider = new JSlider();
		horizontalRangeSlider2 = new RangeSlider();
		horizontalSlider2 = new JSlider();
		JLabel verticalLabel = new JLabel();
		verticalRangeSlider = new RangeSlider();
		verticalSlider = new JSlider();
		verticalRangeSlider2 = new RangeSlider();
		verticalSlider2 = new JSlider();
		paintTick = new JCheckBox();
		paintLabel = new JCheckBox();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[left]" +
			"[240,left]",
			// rows
			"[fill]" +
			"[center]" +
			"[]" +
			"[]" +
			"[]" +
			"[grow,fill]" +
			"[]"));

		//---- tabbedPaneLabel ----
		tabbedPaneLabel.setText("RangeSlider:");
		add(tabbedPaneLabel, "cell 0 0");

		//---- horizontalLabel ----
		horizontalLabel.setText("Horizontal");
		add(horizontalLabel, "cell 0 1");

		//---- horizontalRangeSlider ----
		horizontalRangeSlider.setLowValue(30);
		horizontalRangeSlider.setHighValue(80);
		horizontalRangeSlider.setMajorTickSpacing(10);
		horizontalRangeSlider.setMinorTickSpacing(5);
		horizontalRangeSlider.setPaintTicks(true);
		horizontalRangeSlider.setPaintLabels(true);
		add(horizontalRangeSlider, "cell 1 1,growx");

		//---- horizontalSlider ----
		horizontalSlider.setMinorTickSpacing(5);
		horizontalSlider.setPaintTicks(true);
		horizontalSlider.setMajorTickSpacing(10);
		horizontalSlider.setPaintLabels(true);
		horizontalSlider.setValue(30);
		add(horizontalSlider, "cell 1 2,growx");

		//---- horizontalRangeSlider2 ----
		horizontalRangeSlider2.setLowValue(30);
		horizontalRangeSlider2.setHighValue(80);
		add(horizontalRangeSlider2, "cell 1 3,growx");

		//---- horizontalSlider2 ----
		horizontalSlider2.setValue(30);
		add(horizontalSlider2, "cell 1 4,growx");

		//---- verticalLabel ----
		verticalLabel.setText("Vertical");
		add(verticalLabel, "cell 0 5,aligny top,growy 0");

		//---- verticalRangeSlider ----
		verticalRangeSlider.setOrientation(SwingConstants.VERTICAL);
		verticalRangeSlider.setLowValue(30);
		verticalRangeSlider.setHighValue(80);
		verticalRangeSlider.setMajorTickSpacing(10);
		verticalRangeSlider.setMinorTickSpacing(5);
		verticalRangeSlider.setPaintTicks(true);
		verticalRangeSlider.setPaintLabels(true);
		add(verticalRangeSlider, "cell 1 5,alignx left,growx 0");

		//---- verticalSlider ----
		verticalSlider.setMinorTickSpacing(5);
		verticalSlider.setPaintTicks(true);
		verticalSlider.setMajorTickSpacing(10);
		verticalSlider.setPaintLabels(true);
		verticalSlider.setOrientation(SwingConstants.VERTICAL);
		verticalSlider.setValue(30);
		add(verticalSlider, "cell 1 5");

		//---- verticalRangeSlider2 ----
		verticalRangeSlider2.setOrientation(SwingConstants.VERTICAL);
		verticalRangeSlider2.setLowValue(30);
		verticalRangeSlider2.setHighValue(80);
		add(verticalRangeSlider2, "cell 1 5");

		//---- verticalSlider2 ----
		verticalSlider2.setOrientation(SwingConstants.VERTICAL);
		verticalSlider2.setValue(30);
		add(verticalSlider2, "cell 1 5");

		//---- paintTick ----
		paintTick.setText("PaintTicks");
		paintTick.setMnemonic('T');
		paintTick.setSelected(true);
		paintTick.addActionListener(e -> paintTicks());
		add(paintTick, "cell 0 6 2 1");

		//---- paintLabel ----
		paintLabel.setText("PaintLabels");
		paintLabel.setMnemonic('L');
		paintLabel.setSelected(true);
		paintLabel.addActionListener(e -> paintLabels());
		add(paintLabel, "cell 0 6 2 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private RangeSlider horizontalRangeSlider;
	private JSlider horizontalSlider;
	private RangeSlider horizontalRangeSlider2;
	private JSlider horizontalSlider2;
	private RangeSlider verticalRangeSlider;
	private JSlider verticalSlider;
	private RangeSlider verticalRangeSlider2;
	private JSlider verticalSlider2;
	private JCheckBox paintTick;
	private JCheckBox paintLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
