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

package com.formdev.flatlaf.demo.extras;

import javax.swing.*;
import com.formdev.flatlaf.extras.*;
import com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
import net.miginfocom.swing.*;
import java.awt.*;

/**
 * @author Karl Tauber
 */
public class ExtrasPanel
	extends JPanel
{
	public int counter = 0;

	public ExtrasPanel() {
		initComponents();

		triStateLabel1.setText( triStateCheckBox1.getState().toString() );

		addSVGIcon( "actions/copy.svg" );
		addSVGIcon( "actions/colors.svg" );
		addSVGIcon( "actions/execute.svg" );
		addSVGIcon( "actions/suspend.svg" );
		addSVGIcon( "actions/intentionBulb.svg" );
		addSVGIcon( "actions/quickfixOffBulb.svg" );

		addSVGIcon( "objects/abstractClass.svg" );
		addSVGIcon( "objects/abstractMethod.svg" );
		addSVGIcon( "objects/annotationtype.svg" );
		addSVGIcon( "objects/annotationtype.svg" );
		addSVGIcon( "objects/css.svg" );
		addSVGIcon( "objects/javaScript.svg" );
		addSVGIcon( "objects/xhtml.svg" );

		addSVGIcon( "errorDialog.svg" );
		addSVGIcon( "informationDialog.svg" );
		addSVGIcon( "warningDialog.svg" );
	}

	private void addSVGIcon( String name ) {
		svgIconsPanel.add( new JLabel( new FlatSVGIcon( "com/formdev/flatlaf/demo/extras/svg/" + name ) ) );
	}

	private void triStateCheckBox1Changed() {
		triStateLabel1.setText( triStateCheckBox1.getState().toString() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label4 = new JLabel();
		label1 = new JLabel();
		triStateCheckBox1 = new FlatTriStateCheckBox();
		triStateLabel1 = new JLabel();
		label2 = new JLabel();
		svgIconsPanel = new JPanel();
		label3 = new JLabel();
		separator1 = new JSeparator();
		label5 = new JLabel();
		label6 = new JLabel();
		label7 = new JLabel();
		rainbowIcon = new JLabel();
		toggleButton1 = new JToggleButton();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[]" +
			"[]" +
			"[left]",
			// rows
			"[]para" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]"));

		//---- label4 ----
		label4.setText("Note: Components on this page require the flatlaf-extras library.");
		add(label4, "cell 0 0 3 1");

		//---- label1 ----
		label1.setText("TriStateCheckBox:");
		add(label1, "cell 0 1");

		//---- triStateCheckBox1 ----
		triStateCheckBox1.setText("Three States");
		triStateCheckBox1.addActionListener(e -> triStateCheckBox1Changed());
		add(triStateCheckBox1, "cell 1 1");

		//---- triStateLabel1 ----
		triStateLabel1.setText("text");
		triStateLabel1.setEnabled(false);
		add(triStateLabel1, "cell 2 1,gapx 30");

		//---- label2 ----
		label2.setText("SVG Icons:");
		add(label2, "cell 0 2");

		//======== svgIconsPanel ========
		{
			svgIconsPanel.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[grow,center]"));
		}
		add(svgIconsPanel, "cell 1 2 2 1");

		//---- label3 ----
		label3.setText("The icons may change colors when switching to another theme.");
		add(label3, "cell 1 3 2 1");

		add(separator1, "cell 1 4, grow");

		//---- label5 ----
		label5.setText("Color filters can be also applied to icons. Globally or for each instance.");
		add(label5, "cell 1 5");

		//---- label6 ----
		label6.setText( "Rainbow color filter" );
		add(label6, "cell 1 6");

		//---- rainbowIcon ----
		rainbowIcon = createRainbowIcon("informationDialog.svg");
		add(rainbowIcon, "cell 1 6");

		//---- label7 ----
		label7.setText( "Global icon color filter" );
		add(label7, "cell 1 7");

		// ---- button1 ----
		toggleButton1.setText( "Toggle brighter" );
		add(toggleButton1, "cell 1 7");

		// ---- toggleButton1 ----
		toggleButton1.addActionListener( (e) -> {
			if (toggleButton1.isSelected())
				FlatSVGIcon.ColorFilter.getInstance().setMapper( color -> color.brighter() );
			else
				FlatSVGIcon.ColorFilter.getInstance().setMapper( null );
			SwingUtilities.getRootPane( toggleButton1 ).repaint();
		} );

		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	private JLabel createRainbowIcon(String name) {
		FlatSVGIcon rainbowIcon = new FlatSVGIcon( "com/formdev/flatlaf/demo/extras/svg/" + name);
		rainbowIcon.setColorFilter( new ColorFilter( (color) -> {
			counter+=1;
			counter%=255;
			return Color.getHSBColor(counter/255f, 1, 1);
		}) );
		JLabel label = new JLabel(rainbowIcon);
		new Timer(30, (e) -> {
			label.repaint();
		}).start();
		return label;
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label4;
	private JLabel label1;
	private FlatTriStateCheckBox triStateCheckBox1;
	private JLabel triStateLabel1;
	private JLabel label2;
	private JPanel svgIconsPanel;
	private JLabel label3;
	private JLabel label5;
	private JLabel label6;
	private JLabel label7;
	private JSeparator separator1;
	private JLabel rainbowIcon;
	private JToggleButton toggleButton1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
