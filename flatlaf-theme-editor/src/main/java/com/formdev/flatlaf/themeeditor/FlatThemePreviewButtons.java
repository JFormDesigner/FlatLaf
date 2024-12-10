/*
 * Copyright 2021 FormDev Software GmbH
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

package com.formdev.flatlaf.themeeditor;

import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.Component;
import java.util.Objects;
import java.util.function.Predicate;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class FlatThemePreviewButtons
	extends JPanel
{
	private static final String KEY_BUTTON_TYPE = "preview.buttonType";

	private final FlatThemePreview preview;

	FlatThemePreviewButtons( FlatThemePreview preview ) {
		this.preview = preview;

		initComponents();
	}

	void activated() {
		String buttonType = preview.state.get( KEY_BUTTON_TYPE, null );

		if( !Objects.equals( buttonType, getButtonType() ) ) {
			setButtonType( buttonType );
			buttonTypeChanged();
		}
	}

	private String getButtonType() {
		String buttonType = null;
		if( squareButton.isSelected() )
			buttonType = BUTTON_TYPE_SQUARE;
		else if( roundRectButton.isSelected() )
			buttonType = BUTTON_TYPE_ROUND_RECT;
		else if( tabButton.isSelected() )
			buttonType = BUTTON_TYPE_TAB;
		else if( toolBarButtonButton.isSelected() )
			buttonType = BUTTON_TYPE_TOOLBAR_BUTTON;
		else if( borderlessButton.isSelected() )
			buttonType = BUTTON_TYPE_BORDERLESS;
		return buttonType;
	}

	private void setButtonType( String buttonType ) {
		switch( String.valueOf( buttonType ) ) {
			case BUTTON_TYPE_SQUARE:			squareButton.setSelected( true ); break;
			case BUTTON_TYPE_ROUND_RECT:		roundRectButton.setSelected( true ); break;
			case BUTTON_TYPE_TAB:				tabButton.setSelected( true ); break;
			case BUTTON_TYPE_TOOLBAR_BUTTON:	toolBarButtonButton.setSelected( true ); break;
			case BUTTON_TYPE_BORDERLESS:		borderlessButton.setSelected( true ); break;
			default:							noneButton.setSelected( true ); break;
		}
	}

	private void buttonTypeChanged() {
		String buttonType = getButtonType();

		for( Component c : getComponents() ) {
			if( !(c instanceof AbstractButton) )
				continue;

			AbstractButton b = (AbstractButton) c;
			if( !BUTTON_TYPE_HELP.equals( b.getClientProperty( BUTTON_TYPE ) ) )
				b.putClientProperty( BUTTON_TYPE, buttonType );
		}

		FlatThemeFileEditor.putPrefsString( preview.state, KEY_BUTTON_TYPE, buttonType );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel1 = new JPanel();
		JLabel buttonTypeLabel = new JLabel();
		JToolBar buttonTypeToolBar1 = new JToolBar();
		noneButton = new JToggleButton();
		squareButton = new JToggleButton();
		roundRectButton = new JToggleButton();
		tabButton = new JToggleButton();
		JToolBar buttonTypeToolBar2 = new JToolBar();
		toolBarButtonButton = new JToggleButton();
		borderlessButton = new JToggleButton();
		JLabel label11 = new JLabel();
		JLabel label27 = new JLabel();
		JLabel label28 = new JLabel();
		JLabel label5 = new JLabel();
		JLabel label7 = new JLabel();
		JLabel label6 = new JLabel();
		JLabel label8 = new JLabel();
		JLabel label1 = new JLabel();
		FlatThemePreviewButtons.TestStateButton testStateButton1 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton7 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton4 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton10 = new FlatThemePreviewButtons.TestStateButton();
		JLabel label2 = new JLabel();
		FlatThemePreviewButtons.TestStateButton testStateButton2 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton8 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton5 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton11 = new FlatThemePreviewButtons.TestStateButton();
		JLabel label3 = new JLabel();
		FlatThemePreviewButtons.TestStateButton testStateButton3 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton9 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton6 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton12 = new FlatThemePreviewButtons.TestStateButton();
		JLabel label4 = new JLabel();
		FlatThemePreviewButtons.TestStateButton testStateButton13 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton14 = new FlatThemePreviewButtons.TestStateButton();
		JLabel label10 = new JLabel();
		JButton button1 = new JButton();
		FlatThemePreviewButtons.TestDefaultButton testDefaultButton1 = new FlatThemePreviewButtons.TestDefaultButton();
		JLabel label12 = new JLabel();
		JLabel label29 = new JLabel();
		JLabel label30 = new JLabel();
		JLabel label13 = new JLabel();
		JLabel label14 = new JLabel();
		JLabel label15 = new JLabel();
		JLabel label16 = new JLabel();
		JLabel label17 = new JLabel();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton1 = new FlatThemePreviewButtons.TestStateToggleButton();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton5 = new FlatThemePreviewButtons.TestStateToggleButton();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton9 = new FlatThemePreviewButtons.TestStateToggleButton();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton12 = new FlatThemePreviewButtons.TestStateToggleButton();
		JLabel label18 = new JLabel();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton2 = new FlatThemePreviewButtons.TestStateToggleButton();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton6 = new FlatThemePreviewButtons.TestStateToggleButton();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton10 = new FlatThemePreviewButtons.TestStateToggleButton();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton13 = new FlatThemePreviewButtons.TestStateToggleButton();
		JLabel label19 = new JLabel();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton3 = new FlatThemePreviewButtons.TestStateToggleButton();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton7 = new FlatThemePreviewButtons.TestStateToggleButton();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton11 = new FlatThemePreviewButtons.TestStateToggleButton();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton14 = new FlatThemePreviewButtons.TestStateToggleButton();
		JLabel label20 = new JLabel();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton4 = new FlatThemePreviewButtons.TestStateToggleButton();
		FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton8 = new FlatThemePreviewButtons.TestStateToggleButton();
		JLabel label21 = new JLabel();
		JToggleButton toggleButton1 = new JToggleButton();
		JToggleButton toggleButton2 = new JToggleButton();
		JLabel label32 = new JLabel();
		JLabel label9 = new JLabel();
		JLabel label33 = new JLabel();
		JLabel label22 = new JLabel();
		FlatThemePreviewButtons.TestStateButton testStateButton15 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton19 = new FlatThemePreviewButtons.TestStateButton();
		JLabel label23 = new JLabel();
		FlatThemePreviewButtons.TestStateButton testStateButton16 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton20 = new FlatThemePreviewButtons.TestStateButton();
		JLabel label24 = new JLabel();
		FlatThemePreviewButtons.TestStateButton testStateButton17 = new FlatThemePreviewButtons.TestStateButton();
		FlatThemePreviewButtons.TestStateButton testStateButton21 = new FlatThemePreviewButtons.TestStateButton();
		JLabel label25 = new JLabel();
		FlatThemePreviewButtons.TestStateButton testStateButton18 = new FlatThemePreviewButtons.TestStateButton();
		JLabel label26 = new JLabel();
		JButton button2 = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[fill]" +
			"[fill]para" +
			"[fill]" +
			"[fill]para",
			// rows
			"[]para" +
			"[]" +
			"[]0" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]unrel" +
			"[]para" +
			"[]" +
			"[]0" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]unrel" +
			"[]para" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]unrel" +
			"[]"));

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]" +
				"[fill]",
				// rows
				"[]0" +
				"[]"));

			//---- buttonTypeLabel ----
			buttonTypeLabel.setText("Button type:");
			panel1.add(buttonTypeLabel, "cell 0 0");

			//======== buttonTypeToolBar1 ========
			{
				buttonTypeToolBar1.setFloatable(false);
				buttonTypeToolBar1.setBorder(BorderFactory.createEmptyBorder());

				//---- noneButton ----
				noneButton.setText("none");
				noneButton.setSelected(true);
				noneButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
				noneButton.addActionListener(e -> buttonTypeChanged());
				buttonTypeToolBar1.add(noneButton);

				//---- squareButton ----
				squareButton.setText("square");
				squareButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
				squareButton.addActionListener(e -> buttonTypeChanged());
				buttonTypeToolBar1.add(squareButton);

				//---- roundRectButton ----
				roundRectButton.setText("roundRect");
				roundRectButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
				roundRectButton.addActionListener(e -> buttonTypeChanged());
				buttonTypeToolBar1.add(roundRectButton);

				//---- tabButton ----
				tabButton.setText("tab");
				tabButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
				tabButton.addActionListener(e -> buttonTypeChanged());
				buttonTypeToolBar1.add(tabButton);
			}
			panel1.add(buttonTypeToolBar1, "cell 1 0");

			//======== buttonTypeToolBar2 ========
			{
				buttonTypeToolBar2.setFloatable(false);
				buttonTypeToolBar2.setBorder(BorderFactory.createEmptyBorder());

				//---- toolBarButtonButton ----
				toolBarButtonButton.setText("toolBarButton");
				toolBarButtonButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
				toolBarButtonButton.addActionListener(e -> buttonTypeChanged());
				buttonTypeToolBar2.add(toolBarButtonButton);

				//---- borderlessButton ----
				borderlessButton.setText("borderless");
				borderlessButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
				borderlessButton.addActionListener(e -> buttonTypeChanged());
				buttonTypeToolBar2.add(borderlessButton);
			}
			panel1.add(buttonTypeToolBar2, "cell 1 1");
		}
		add(panel1, "cell 0 0 5 1");

		//---- label11 ----
		label11.setText("JButton");
		label11.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(label11, "cell 0 1 3 1");

		//---- label27 ----
		label27.setText("unfocused");
		add(label27, "cell 1 2 2 1,alignx center,growx 0");

		//---- label28 ----
		label28.setText("focused");
		add(label28, "cell 3 2 2 1,alignx center,growx 0");

		//---- label5 ----
		label5.setText("regular");
		label5.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		add(label5, "cell 1 3,alignx center,growx 0");

		//---- label7 ----
		label7.setText("default");
		label7.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		add(label7, "cell 2 3,alignx center,growx 0");

		//---- label6 ----
		label6.setText("regular");
		label6.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		add(label6, "cell 3 3,alignx center,growx 0");

		//---- label8 ----
		label8.setText("default");
		label8.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		add(label8, "cell 4 3,alignx center,growx 0");

		//---- label1 ----
		label1.setText("none");
		add(label1, "cell 0 4");

		//---- testStateButton1 ----
		testStateButton1.setText("OK");
		testStateButton1.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton1, "cell 1 4");

		//---- testStateButton7 ----
		testStateButton7.setText("OK");
		testStateButton7.setStateDefault(true);
		testStateButton7.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton7, "cell 2 4");

		//---- testStateButton4 ----
		testStateButton4.setText("OK");
		testStateButton4.setStateFocused(true);
		testStateButton4.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton4, "cell 3 4");

		//---- testStateButton10 ----
		testStateButton10.setText("OK");
		testStateButton10.setStateFocused(true);
		testStateButton10.setStateDefault(true);
		testStateButton10.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton10, "cell 4 4");

		//---- label2 ----
		label2.setText("hover");
		add(label2, "cell 0 5");

		//---- testStateButton2 ----
		testStateButton2.setText("OK");
		testStateButton2.setStateHover(true);
		testStateButton2.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton2, "cell 1 5");

		//---- testStateButton8 ----
		testStateButton8.setText("OK");
		testStateButton8.setStateHover(true);
		testStateButton8.setStateDefault(true);
		testStateButton8.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton8, "cell 2 5");

		//---- testStateButton5 ----
		testStateButton5.setText("OK");
		testStateButton5.setStateHover(true);
		testStateButton5.setStateFocused(true);
		testStateButton5.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton5, "cell 3 5");

		//---- testStateButton11 ----
		testStateButton11.setText("OK");
		testStateButton11.setStateHover(true);
		testStateButton11.setStateFocused(true);
		testStateButton11.setStateDefault(true);
		testStateButton11.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton11, "cell 4 5");

		//---- label3 ----
		label3.setText("pressed");
		add(label3, "cell 0 6");

		//---- testStateButton3 ----
		testStateButton3.setText("OK");
		testStateButton3.setStatePressed(true);
		testStateButton3.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton3, "cell 1 6");

		//---- testStateButton9 ----
		testStateButton9.setText("OK");
		testStateButton9.setStatePressed(true);
		testStateButton9.setStateDefault(true);
		testStateButton9.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton9, "cell 2 6");

		//---- testStateButton6 ----
		testStateButton6.setText("OK");
		testStateButton6.setStatePressed(true);
		testStateButton6.setStateFocused(true);
		testStateButton6.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton6, "cell 3 6");

		//---- testStateButton12 ----
		testStateButton12.setText("OK");
		testStateButton12.setStatePressed(true);
		testStateButton12.setStateFocused(true);
		testStateButton12.setStateDefault(true);
		testStateButton12.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton12, "cell 4 6");

		//---- label4 ----
		label4.setText("disabled");
		add(label4, "cell 0 7");

		//---- testStateButton13 ----
		testStateButton13.setText("OK");
		testStateButton13.setEnabled(false);
		testStateButton13.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton13, "cell 1 7");

		//---- testStateButton14 ----
		testStateButton14.setText("OK");
		testStateButton14.setEnabled(false);
		testStateButton14.setStateDefault(true);
		testStateButton14.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testStateButton14, "cell 2 7");

		//---- label10 ----
		label10.setText("try me");
		add(label10, "cell 0 8");

		//---- button1 ----
		button1.setText("OK");
		button1.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(button1, "cell 1 8");

		//---- testDefaultButton1 ----
		testDefaultButton1.setText("OK");
		testDefaultButton1.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		add(testDefaultButton1, "cell 2 8");

		//---- label12 ----
		label12.setText("JToggleButton");
		label12.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(label12, "cell 0 9 3 1");

		//---- label29 ----
		label29.setText("unfocused");
		add(label29, "cell 1 10 2 1,alignx center,growx 0");

		//---- label30 ----
		label30.setText("focused");
		add(label30, "cell 3 10 2 1,alignx center,growx 0");

		//---- label13 ----
		label13.setText("unsel.");
		label13.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		add(label13, "cell 1 11,alignx center,growx 0");

		//---- label14 ----
		label14.setText("selected");
		label14.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		add(label14, "cell 2 11,alignx center,growx 0");

		//---- label15 ----
		label15.setText("unsel.");
		label15.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		add(label15, "cell 3 11,alignx center,growx 0");

		//---- label16 ----
		label16.setText("selected");
		label16.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		add(label16, "cell 4 11,alignx center,growx 0");

		//---- label17 ----
		label17.setText("none");
		add(label17, "cell 0 12");

		//---- testStateToggleButton1 ----
		testStateToggleButton1.setText("OK");
		add(testStateToggleButton1, "cell 1 12");

		//---- testStateToggleButton5 ----
		testStateToggleButton5.setText("OK");
		testStateToggleButton5.setStateSelected(true);
		add(testStateToggleButton5, "cell 2 12");

		//---- testStateToggleButton9 ----
		testStateToggleButton9.setText("OK");
		testStateToggleButton9.setStateFocused(true);
		add(testStateToggleButton9, "cell 3 12");

		//---- testStateToggleButton12 ----
		testStateToggleButton12.setText("OK");
		testStateToggleButton12.setStateSelected(true);
		testStateToggleButton12.setStateFocused(true);
		add(testStateToggleButton12, "cell 4 12");

		//---- label18 ----
		label18.setText("hover");
		add(label18, "cell 0 13");

		//---- testStateToggleButton2 ----
		testStateToggleButton2.setText("OK");
		testStateToggleButton2.setStateHover(true);
		add(testStateToggleButton2, "cell 1 13");

		//---- testStateToggleButton6 ----
		testStateToggleButton6.setText("OK");
		testStateToggleButton6.setStateHover(true);
		testStateToggleButton6.setStateSelected(true);
		add(testStateToggleButton6, "cell 2 13");

		//---- testStateToggleButton10 ----
		testStateToggleButton10.setText("OK");
		testStateToggleButton10.setStateHover(true);
		testStateToggleButton10.setStateFocused(true);
		add(testStateToggleButton10, "cell 3 13");

		//---- testStateToggleButton13 ----
		testStateToggleButton13.setText("OK");
		testStateToggleButton13.setStateHover(true);
		testStateToggleButton13.setStateSelected(true);
		testStateToggleButton13.setStateFocused(true);
		add(testStateToggleButton13, "cell 4 13");

		//---- label19 ----
		label19.setText("pressed");
		add(label19, "cell 0 14");

		//---- testStateToggleButton3 ----
		testStateToggleButton3.setText("OK");
		testStateToggleButton3.setStatePressed(true);
		add(testStateToggleButton3, "cell 1 14");

		//---- testStateToggleButton7 ----
		testStateToggleButton7.setText("OK");
		testStateToggleButton7.setStatePressed(true);
		testStateToggleButton7.setStateSelected(true);
		add(testStateToggleButton7, "cell 2 14");

		//---- testStateToggleButton11 ----
		testStateToggleButton11.setText("OK");
		testStateToggleButton11.setStatePressed(true);
		testStateToggleButton11.setStateFocused(true);
		add(testStateToggleButton11, "cell 3 14");

		//---- testStateToggleButton14 ----
		testStateToggleButton14.setText("OK");
		testStateToggleButton14.setStatePressed(true);
		testStateToggleButton14.setStateSelected(true);
		testStateToggleButton14.setStateFocused(true);
		add(testStateToggleButton14, "cell 4 14");

		//---- label20 ----
		label20.setText("disabled");
		add(label20, "cell 0 15");

		//---- testStateToggleButton4 ----
		testStateToggleButton4.setText("OK");
		testStateToggleButton4.setEnabled(false);
		add(testStateToggleButton4, "cell 1 15");

		//---- testStateToggleButton8 ----
		testStateToggleButton8.setText("OK");
		testStateToggleButton8.setEnabled(false);
		testStateToggleButton8.setStateSelected(true);
		add(testStateToggleButton8, "cell 2 15");

		//---- label21 ----
		label21.setText("try me");
		add(label21, "cell 0 16");

		//---- toggleButton1 ----
		toggleButton1.setText("OK");
		add(toggleButton1, "cell 1 16");

		//---- toggleButton2 ----
		toggleButton2.setText("OK");
		toggleButton2.setSelected(true);
		add(toggleButton2, "cell 2 16");

		//---- label32 ----
		label32.setText("Help Button");
		label32.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(label32, "cell 0 17 2 1");

		//---- label9 ----
		label9.setText("unfocused");
		add(label9, "cell 1 18 2 1,alignx center,growx 0");

		//---- label33 ----
		label33.setText("focused");
		add(label33, "cell 3 18 2 1,alignx center,growx 0");

		//---- label22 ----
		label22.setText("none");
		add(label22, "cell 0 19");

		//---- testStateButton15 ----
		testStateButton15.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton15.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton15, "cell 1 19 2 1,alignx center,growx 0");

		//---- testStateButton19 ----
		testStateButton19.setStateFocused(true);
		testStateButton19.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton19.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton19, "cell 3 19 2 1,alignx center,growx 0");

		//---- label23 ----
		label23.setText("hover");
		add(label23, "cell 0 20");

		//---- testStateButton16 ----
		testStateButton16.setStateHover(true);
		testStateButton16.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton16.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton16, "cell 1 20 2 1,alignx center,growx 0");

		//---- testStateButton20 ----
		testStateButton20.setStateHover(true);
		testStateButton20.setStateFocused(true);
		testStateButton20.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton20.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton20, "cell 3 20 2 1,alignx center,growx 0");

		//---- label24 ----
		label24.setText("pressed");
		add(label24, "cell 0 21");

		//---- testStateButton17 ----
		testStateButton17.setStatePressed(true);
		testStateButton17.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton17.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton17, "cell 1 21 2 1,alignx center,growx 0");

		//---- testStateButton21 ----
		testStateButton21.setStatePressed(true);
		testStateButton21.setStateFocused(true);
		testStateButton21.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton21.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton21, "cell 3 21 2 1,alignx center,growx 0");

		//---- label25 ----
		label25.setText("disabled");
		add(label25, "cell 0 22");

		//---- testStateButton18 ----
		testStateButton18.setEnabled(false);
		testStateButton18.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		testStateButton18.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(testStateButton18, "cell 1 22 2 1,alignx center,growx 0");

		//---- label26 ----
		label26.setText("try me");
		add(label26, "cell 0 23");

		//---- button2 ----
		button2.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
		button2.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_HELP);
		add(button2, "cell 1 23 2 1,alignx center,growx 0");

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(noneButton);
		buttonGroup1.add(squareButton);
		buttonGroup1.add(roundRectButton);
		buttonGroup1.add(tabButton);
		buttonGroup1.add(toolBarButtonButton);
		buttonGroup1.add(borderlessButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JToggleButton noneButton;
	private JToggleButton squareButton;
	private JToggleButton roundRectButton;
	private JToggleButton tabButton;
	private JToggleButton toolBarButtonButton;
	private JToggleButton borderlessButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class TestStateButton ----------------------------------------------

	private static class TestStateButton
		extends JButton
	{
		private boolean stateHover;
		private boolean statePressed;
		private boolean stateFocused;
		private boolean stateDefault;

		public TestStateButton() {
			setModel( new DefaultButtonModel() {
				@Override
				public boolean isRollover() {
					return isStateHover();
				}
				@Override
				public boolean isPressed() {
					return isStatePressed();
				}
			} );

			putClientProperty( COMPONENT_FOCUS_OWNER,
				(Predicate<JComponent>) c -> {
					return ((TestStateButton)c).isStateFocused();
				} );
		}

		public boolean isStateHover() {
			return stateHover;
		}

		public void setStateHover( boolean stateHover ) {
			this.stateHover = stateHover;
		}

		public boolean isStatePressed() {
			return statePressed;
		}

		public void setStatePressed( boolean statePressed ) {
			this.statePressed = statePressed;
		}

		public boolean isStateFocused() {
			return stateFocused;
		}

		public void setStateFocused( boolean stateFocused ) {
			this.stateFocused = stateFocused;
		}

		public boolean isStateDefault() {
			return stateDefault;
		}

		public void setStateDefault( boolean stateDefault ) {
			this.stateDefault = stateDefault;
		}

		@Override
		public boolean isDefaultButton() {
			return isStateDefault();
		}
	}

	//---- class TestStateToggleButton ----------------------------------------

	private static class TestStateToggleButton
		extends JToggleButton
	{
		private boolean stateHover;
		private boolean statePressed;
		private boolean stateFocused;
		private boolean stateSelected;

		public TestStateToggleButton() {
			setModel( new DefaultButtonModel() {
				@Override
				public boolean isRollover() {
					return isStateHover();
				}
				@Override
				public boolean isPressed() {
					return isStatePressed();
				}
				@Override
				public boolean isSelected() {
					return isStateSelected();
				}
			} );

			putClientProperty( COMPONENT_FOCUS_OWNER,
				(Predicate<JComponent>) c -> {
					return ((TestStateToggleButton)c).isStateFocused();
				} );
		}

		public boolean isStateHover() {
			return stateHover;
		}

		public void setStateHover( boolean stateHover ) {
			this.stateHover = stateHover;
		}

		public boolean isStatePressed() {
			return statePressed;
		}

		public void setStatePressed( boolean statePressed ) {
			this.statePressed = statePressed;
		}

		public boolean isStateFocused() {
			return stateFocused;
		}

		public void setStateFocused( boolean stateFocused ) {
			this.stateFocused = stateFocused;
		}

		public boolean isStateSelected() {
			return stateSelected;
		}

		public void setStateSelected( boolean stateSelected ) {
			this.stateSelected = stateSelected;
		}
	}

	//---- class TestDefaultButton --------------------------------------------

	private static class TestDefaultButton
		extends JButton
	{
		@Override
		public boolean isDefaultButton() {
			return true;
		}
	}
}
