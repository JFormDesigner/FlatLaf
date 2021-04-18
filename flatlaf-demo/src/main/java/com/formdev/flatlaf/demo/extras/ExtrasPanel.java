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
import com.formdev.flatlaf.util.HSLColor;
import net.miginfocom.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.function.Function;

/**
 * @author Karl Tauber
 */
public class ExtrasPanel
	extends JPanel
{
	private Timer rainbowIconTimer;
	private int rainbowCounter = 0;

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

		initRainbowIcon();
	}

	private void initRainbowIcon() {
		FlatSVGIcon icon = new FlatSVGIcon( "com/formdev/flatlaf/demo/extras/svg/informationDialog.svg" );
		icon.setColorFilter( new ColorFilter( color -> {
			rainbowCounter += 1;
			rainbowCounter %= 255;
			return Color.getHSBColor( rainbowCounter / 255f, 1, 1 );
		} ) );
		rainbowIcon.setIcon( icon );

		rainbowIconTimer = new Timer( 30, e -> {
			rainbowIcon.repaint();
		} );

		// start rainbow timer only if panel is shown ("Extras" tab is active)
		addHierarchyListener( e -> {
			if( e.getID() == HierarchyEvent.HIERARCHY_CHANGED &&
				(e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 )
			{
				if( isShowing() )
					rainbowIconTimer.start();
				else
					rainbowIconTimer.stop();
			}
		} );
	}

	private void addSVGIcon( String name ) {
		svgIconsPanel.add( new JLabel( new FlatSVGIcon( "com/formdev/flatlaf/demo/extras/svg/" + name ) ) );
	}

	private void triStateCheckBox1Changed() {
		triStateLabel1.setText( triStateCheckBox1.getState().toString() );
	}

	private void redChanged() {
		brighterToggleButton.setSelected( false );

		Function<Color, Color> mapper = null;
		if( redToggleButton.isSelected() ) {
			float[] redHSL = HSLColor.fromRGB( Color.red );
			mapper = color -> {
				float[] hsl = HSLColor.fromRGB( color );
				return HSLColor.toRGB( redHSL[0], 70, hsl[2] );
			};
		}
		FlatSVGIcon.ColorFilter.getInstance().setMapper( mapper );

		// repaint whole application window because global color filter also affects
		// icons in menubar, toolbar, etc.
		SwingUtilities.windowForComponent( this ).repaint();
	}

	private void brighterChanged() {
		redToggleButton.setSelected( false );

		FlatSVGIcon.ColorFilter.getInstance().setMapper( brighterToggleButton.isSelected()
			? color -> color.brighter().brighter()
			: null );

		// repaint whole application window because global color filter also affects
		// icons in menubar, toolbar, etc.
		SwingUtilities.windowForComponent( this ).repaint();
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
		rainbowIcon = new JLabel();
		label7 = new JLabel();
		redToggleButton = new JToggleButton();
		brighterToggleButton = new JToggleButton();

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
		add(separator1, "cell 1 4 2 1,growx");

		//---- label5 ----
		label5.setText("Color filters can be also applied to icons. Globally or for each instance.");
		add(label5, "cell 1 5 2 1");

		//---- label6 ----
		label6.setText("Rainbow color filter");
		add(label6, "cell 1 6 2 1");
		add(rainbowIcon, "cell 1 6 2 1");

		//---- label7 ----
		label7.setText("Global icon color filter");
		add(label7, "cell 1 7 2 1");

		//---- redToggleButton ----
		redToggleButton.setText("Toggle RED");
		redToggleButton.addActionListener(e -> redChanged());
		add(redToggleButton, "cell 1 7 2 1");

		//---- brighterToggleButton ----
		brighterToggleButton.setText("Toggle brighter");
		brighterToggleButton.addActionListener(e -> brighterChanged());
		add(brighterToggleButton, "cell 1 7 2 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label4;
	private JLabel label1;
	private FlatTriStateCheckBox triStateCheckBox1;
	private JLabel triStateLabel1;
	private JLabel label2;
	private JPanel svgIconsPanel;
	private JLabel label3;
	private JSeparator separator1;
	private JLabel label5;
	private JLabel label6;
	private JLabel rainbowIcon;
	private JLabel label7;
	private JToggleButton redToggleButton;
	private JToggleButton brighterToggleButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
