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

package com.formdev.flatlaf.testing.extras;

import java.awt.*;
import javax.swing.*;
import com.formdev.flatlaf.extras.*;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.testing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatExtrasTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatExtrasTest" );
			frame.showFrame( FlatExtrasTest::new );
		} );
	}

	public FlatExtrasTest() {
		initComponents();

		triStateCheckBox1Changed();
		triStateCheckBox2Changed();

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

		FlatSVGIcon icon = new FlatSVGIcon( "com/formdev/flatlaf/demo/extras/svg/warningDialog.svg" );
		Icon disabledIcon = icon.getDisabledIcon();
		disabledLabel.setIcon( icon );
		disabledButton.setIcon( icon );
		disabledTabbedPane.addTab( "tab", null );
		disabledTabbedPane.setIconAt( 0, icon );

		disabledLabel2.setIcon( icon );
		disabledLabel2.setDisabledIcon( disabledIcon );
		disabledButton2.setIcon( icon );
		disabledButton2.setDisabledIcon( disabledIcon );
		disabledTabbedPane2.addTab( "tab", null );
		disabledTabbedPane2.setIconAt( 0, icon );
		disabledTabbedPane2.setDisabledIconAt( 0, disabledIcon );

		addJSVGIcon( "linearGradient.svg", 64, 128 );
		addJSVGIcon( "stripes.svg", 128, 128 );
		addJSVGIcon( "gradientText0.svg", 128, 128 );
		addJSVGIcon( "gradientText1.svg", 128, 128 );
		addJSVGIcon( "gradientText2.svg", 128, 128 );

		disabledChanged();
	}

	private void addSVGIcon( String name ) {
		svgIconsPanel.add( new JLabel( new FlatSVGIcon( "com/formdev/flatlaf/demo/extras/svg/" + name ) ) );
	}

	private void addJSVGIcon( String name, int width, int height ) {
		gradientIconsPanel.add( new JLabel( new FlatSVGIcon( "com/formdev/flatlaf/testing/extras/jsvg/" + name, width, height ) ) );
	}

	private void triStateCheckBox1Changed() {
		triStateLabel1.setText( triStateCheckBox1.getState().toString() );
	}

	private void triStateCheckBox2Changed() {
		triStateLabel2.setText( triStateCheckBox2.getState().toString() );
	}

	private void triStateCheckBox3Changed() {
		triStateLabel3.setText( triStateCheckBox3.getState().toString() );
	}

	private void disabledChanged() {
		boolean enabled = !disabledCheckBox.isSelected();

		disabledLabel.setEnabled( enabled );
		disabledButton.setEnabled( enabled );
		disabledTabbedPane.setEnabledAt( 0, enabled );

		disabledLabel2.setEnabled( enabled );
		disabledButton2.setEnabled( enabled );
		disabledTabbedPane2.setEnabledAt( 0, enabled );

		for( Component c : gradientIconsPanel.getComponents() )
			c.setEnabled( enabled );
	}

	@Override
	public void updateUI() {
		super.updateUI();

		if( disabledLabel == null )
			return;

		// clear automatically created disabled icons when switching Laf
		disabledLabel.setDisabledIcon( null );
		disabledButton.setDisabledIcon( null );
		disabledTabbedPane.setDisabledIconAt( 0, null );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label1 = new JLabel();
		triStateCheckBox1 = new FlatTriStateCheckBox();
		triStateLabel1 = new JLabel();
		triStateCheckBox3 = new FlatTriStateCheckBox();
		triStateLabel3 = new JLabel();
		triStateCheckBox2 = new FlatTriStateCheckBox();
		triStateLabel2 = new JLabel();
		label2 = new JLabel();
		svgIconsPanel = new JPanel();
		label3 = new JLabel();
		label4 = new JLabel();
		disabledLabel = new JLabel();
		disabledButton = new JButton();
		disabledTabbedPane = new JTabbedPane();
		label5 = new JLabel();
		disabledCheckBox = new JCheckBox();
		disabledLabel2 = new JLabel();
		disabledButton2 = new JButton();
		disabledTabbedPane2 = new JTabbedPane();
		label6 = new JLabel();
		gradientIconsPanel = new JPanel();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[]" +
			"[left]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]"));

		//---- label1 ----
		label1.setText("TriStateCheckBox:");
		add(label1, "cell 0 0");

		//---- triStateCheckBox1 ----
		triStateCheckBox1.setText("three states");
		triStateCheckBox1.addActionListener(e -> triStateCheckBox1Changed());
		add(triStateCheckBox1, "cell 1 0");

		//---- triStateLabel1 ----
		triStateLabel1.setText("text");
		triStateLabel1.setEnabled(false);
		add(triStateLabel1, "cell 2 0,gapx 30");

		//---- triStateCheckBox3 ----
		triStateCheckBox3.setText("alt state cycle order");
		triStateCheckBox3.setAltStateCycleOrder(true);
		triStateCheckBox3.addActionListener(e -> triStateCheckBox3Changed());
		add(triStateCheckBox3, "cell 1 1");

		//---- triStateLabel3 ----
		triStateLabel3.setText("text");
		triStateLabel3.setEnabled(false);
		add(triStateLabel3, "cell 2 1,gapx 30");

		//---- triStateCheckBox2 ----
		triStateCheckBox2.setText("third state disabled");
		triStateCheckBox2.setAllowIndeterminate(false);
		triStateCheckBox2.addActionListener(e -> triStateCheckBox2Changed());
		add(triStateCheckBox2, "cell 1 2");

		//---- triStateLabel2 ----
		triStateLabel2.setText("text");
		triStateLabel2.setEnabled(false);
		add(triStateLabel2, "cell 2 2,gapx 30");

		//---- label2 ----
		label2.setText("SVG Icons:");
		add(label2, "cell 0 3");

		//======== svgIconsPanel ========
		{
			svgIconsPanel.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[grow,center]"));
		}
		add(svgIconsPanel, "cell 1 3 2 1");

		//---- label3 ----
		label3.setText("The icons may change colors when switching to another theme.");
		add(label3, "cell 1 4 2 1");

		//---- label4 ----
		label4.setText("Disabled SVG Icons:");
		add(label4, "cell 0 5");

		//---- disabledLabel ----
		disabledLabel.setText("label");
		add(disabledLabel, "cell 1 5 2 1");

		//---- disabledButton ----
		disabledButton.setText("button");
		add(disabledButton, "cell 1 5 2 1");
		add(disabledTabbedPane, "cell 1 5 2 1");

		//---- label5 ----
		label5.setText("only setIcon()");
		label5.setEnabled(false);
		add(label5, "cell 1 5 2 1,gapx 20");

		//---- disabledCheckBox ----
		disabledCheckBox.setText("disabled");
		disabledCheckBox.setSelected(true);
		disabledCheckBox.setMnemonic('D');
		disabledCheckBox.addActionListener(e -> disabledChanged());
		add(disabledCheckBox, "cell 0 6,alignx left,growx 0");

		//---- disabledLabel2 ----
		disabledLabel2.setText("label");
		add(disabledLabel2, "cell 1 6 2 1");

		//---- disabledButton2 ----
		disabledButton2.setText("button");
		add(disabledButton2, "cell 1 6 2 1");
		add(disabledTabbedPane2, "cell 1 6 2 1");

		//---- label6 ----
		label6.setText("setIcon() and setDisabledIcon()");
		label6.setEnabled(false);
		add(label6, "cell 1 6 2 1,gapx 20");

		//======== gradientIconsPanel ========
		{
			gradientIconsPanel.setLayout(new FlowLayout());
		}
		add(gradientIconsPanel, "cell 1 7 2 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private FlatTriStateCheckBox triStateCheckBox1;
	private JLabel triStateLabel1;
	private FlatTriStateCheckBox triStateCheckBox3;
	private JLabel triStateLabel3;
	private FlatTriStateCheckBox triStateCheckBox2;
	private JLabel triStateLabel2;
	private JLabel label2;
	private JPanel svgIconsPanel;
	private JLabel label3;
	private JLabel label4;
	private JLabel disabledLabel;
	private JButton disabledButton;
	private JTabbedPane disabledTabbedPane;
	private JLabel label5;
	private JCheckBox disabledCheckBox;
	private JLabel disabledLabel2;
	private JButton disabledButton2;
	private JTabbedPane disabledTabbedPane2;
	private JLabel label6;
	private JPanel gradientIconsPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
