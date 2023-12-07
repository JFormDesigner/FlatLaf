/*
 * Copyright 2023 FormDev Software GmbH
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
import javax.swing.*;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.FlatNativeWindowsLibrary;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatWindows11DwmTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			System.setProperty( FlatSystemProperties.USE_WINDOW_DECORATIONS, "false" );

			FlatTestFrame frame = FlatTestFrame.create( args, FlatWindows11DwmTest.class.getSimpleName() );
			frame.showFrame( FlatWindows11DwmTest::new );
		} );
	}

	FlatWindows11DwmTest() {
		initComponents();
	}

	private void modeChanged() {
		if( !checkNativeLibraryLoaded() )
			return;

		boolean dark = darkModeRadioButton.isSelected();
		FlatNativeWindowsLibrary.dwmSetWindowAttributeBOOL( getHWND(), FlatNativeWindowsLibrary.DWMWA_USE_IMMERSIVE_DARK_MODE, dark );
	}

	private void borderColorChanged() {
		if( !checkNativeLibraryLoaded() )
			return;

		Color color = null;
		if( borderRedRadioButton.isSelected() )
			color = Color.red;
		else if( borderGreenRadioButton.isSelected() )
			color = Color.green;
		else if( borderNoneRadioButton.isSelected() )
			color = FlatNativeWindowsLibrary.COLOR_NONE;

		FlatNativeWindowsLibrary.dwmSetWindowAttributeCOLORREF( getHWND(), FlatNativeWindowsLibrary.DWMWA_BORDER_COLOR, color );
	}

	private void captionColorChanged() {
		if( !checkNativeLibraryLoaded() )
			return;

		Color color = null;
		if( captionGreenRadioButton.isSelected() )
			color = Color.green;
		else if( captionYellowRadioButton.isSelected() )
			color = Color.yellow;
		else if( captionBlackRadioButton.isSelected() )
			color = Color.black;

		FlatNativeWindowsLibrary.dwmSetWindowAttributeCOLORREF( getHWND(), FlatNativeWindowsLibrary.DWMWA_CAPTION_COLOR, color );
	}

	private void textColorChanged() {
		if( !checkNativeLibraryLoaded() )
			return;

		Color color = null;
		if( textRedRadioButton.isSelected() )
			color = Color.red;
		else if( textBlueRadioButton.isSelected() )
			color = Color.blue;
		else if( textWhiteRadioButton.isSelected() )
			color = Color.white;

		FlatNativeWindowsLibrary.dwmSetWindowAttributeCOLORREF( getHWND(), FlatNativeWindowsLibrary.DWMWA_TEXT_COLOR, color );
	}

	private void cornerChanged() {
		if( !checkNativeLibraryLoaded() )
			return;

		int cornerPreference = FlatNativeWindowsLibrary.DWMWCP_DEFAULT;
		if( cornerDontRoundRadioButton.isSelected() )
			cornerPreference = FlatNativeWindowsLibrary.DWMWCP_DONOTROUND;
		else if( cornerRoundRadioButton.isSelected() )
			cornerPreference = FlatNativeWindowsLibrary.DWMWCP_ROUND;
		else if( cornerRoundSmallRadioButton.isSelected() )
			cornerPreference = FlatNativeWindowsLibrary.DWMWCP_ROUNDSMALL;

		FlatNativeWindowsLibrary.setWindowCornerPreference( getHWND(), cornerPreference );
	}

	private long getHWND() {
		return FlatNativeWindowsLibrary.getHWND( SwingUtilities.getWindowAncestor( this ) );
	}

	private boolean checkNativeLibraryLoaded() {
		if( FlatNativeWindowsLibrary.isLoaded() )
			return true;

		JOptionPane.showMessageDialog( this, "FlatLaf native windows library not loaded/available." );
		return false;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel modeLabel = new JLabel();
		lightModeRadioButton = new JRadioButton();
		darkModeRadioButton = new JRadioButton();
		JLabel borderLabel = new JLabel();
		borderDefaultRadioButton = new JRadioButton();
		borderRedRadioButton = new JRadioButton();
		borderGreenRadioButton = new JRadioButton();
		borderNoneRadioButton = new JRadioButton();
		JLabel captionLabel = new JLabel();
		captionDefaultRadioButton = new JRadioButton();
		captionGreenRadioButton = new JRadioButton();
		captionYellowRadioButton = new JRadioButton();
		captionBlackRadioButton = new JRadioButton();
		JLabel textLabel = new JLabel();
		textDefaultRadioButton = new JRadioButton();
		textRedRadioButton = new JRadioButton();
		textBlueRadioButton = new JRadioButton();
		textWhiteRadioButton = new JRadioButton();
		JLabel cornerLabel = new JLabel();
		cornerDefaultRadioButton = new JRadioButton();
		cornerDontRoundRadioButton = new JRadioButton();
		cornerRoundRadioButton = new JRadioButton();
		cornerRoundSmallRadioButton = new JRadioButton();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[left]" +
			"[left]" +
			"[left]" +
			"[left]",
			// rows
			"[fill]para" +
			"[]" +
			"[]" +
			"[]" +
			"[]"));

		//---- modeLabel ----
		modeLabel.setText("Mode:");
		add(modeLabel, "cell 0 0");

		//---- lightModeRadioButton ----
		lightModeRadioButton.setText("Light");
		lightModeRadioButton.setSelected(true);
		lightModeRadioButton.addActionListener(e -> modeChanged());
		add(lightModeRadioButton, "cell 1 0");

		//---- darkModeRadioButton ----
		darkModeRadioButton.setText("Dark");
		darkModeRadioButton.addActionListener(e -> modeChanged());
		add(darkModeRadioButton, "cell 2 0");

		//---- borderLabel ----
		borderLabel.setText("Border Color:");
		add(borderLabel, "cell 0 1");

		//---- borderDefaultRadioButton ----
		borderDefaultRadioButton.setText("Default");
		borderDefaultRadioButton.setSelected(true);
		borderDefaultRadioButton.addActionListener(e -> borderColorChanged());
		add(borderDefaultRadioButton, "cell 1 1");

		//---- borderRedRadioButton ----
		borderRedRadioButton.setText("Red");
		borderRedRadioButton.addActionListener(e -> borderColorChanged());
		add(borderRedRadioButton, "cell 2 1");

		//---- borderGreenRadioButton ----
		borderGreenRadioButton.setText("Green");
		borderGreenRadioButton.addActionListener(e -> borderColorChanged());
		add(borderGreenRadioButton, "cell 3 1");

		//---- borderNoneRadioButton ----
		borderNoneRadioButton.setText("None");
		borderNoneRadioButton.addActionListener(e -> borderColorChanged());
		add(borderNoneRadioButton, "cell 4 1");

		//---- captionLabel ----
		captionLabel.setText("Caption Color:");
		add(captionLabel, "cell 0 2");

		//---- captionDefaultRadioButton ----
		captionDefaultRadioButton.setText("Default");
		captionDefaultRadioButton.setSelected(true);
		captionDefaultRadioButton.addActionListener(e -> captionColorChanged());
		add(captionDefaultRadioButton, "cell 1 2");

		//---- captionGreenRadioButton ----
		captionGreenRadioButton.setText("Green");
		captionGreenRadioButton.addActionListener(e -> captionColorChanged());
		add(captionGreenRadioButton, "cell 2 2");

		//---- captionYellowRadioButton ----
		captionYellowRadioButton.setText("Yellow");
		captionYellowRadioButton.addActionListener(e -> captionColorChanged());
		add(captionYellowRadioButton, "cell 3 2");

		//---- captionBlackRadioButton ----
		captionBlackRadioButton.setText("Black");
		captionBlackRadioButton.addActionListener(e -> captionColorChanged());
		add(captionBlackRadioButton, "cell 4 2");

		//---- textLabel ----
		textLabel.setText("Text Color:");
		add(textLabel, "cell 0 3");

		//---- textDefaultRadioButton ----
		textDefaultRadioButton.setText("Default");
		textDefaultRadioButton.setSelected(true);
		textDefaultRadioButton.addActionListener(e -> textColorChanged());
		add(textDefaultRadioButton, "cell 1 3");

		//---- textRedRadioButton ----
		textRedRadioButton.setText("Red");
		textRedRadioButton.addActionListener(e -> textColorChanged());
		add(textRedRadioButton, "cell 2 3");

		//---- textBlueRadioButton ----
		textBlueRadioButton.setText("Blue");
		textBlueRadioButton.addActionListener(e -> textColorChanged());
		add(textBlueRadioButton, "cell 3 3");

		//---- textWhiteRadioButton ----
		textWhiteRadioButton.setText("White");
		textWhiteRadioButton.addActionListener(e -> textColorChanged());
		add(textWhiteRadioButton, "cell 4 3");

		//---- cornerLabel ----
		cornerLabel.setText("Corner:");
		add(cornerLabel, "cell 0 4");

		//---- cornerDefaultRadioButton ----
		cornerDefaultRadioButton.setText("Default");
		cornerDefaultRadioButton.setSelected(true);
		cornerDefaultRadioButton.addActionListener(e -> cornerChanged());
		add(cornerDefaultRadioButton, "cell 1 4 4 1");

		//---- cornerDontRoundRadioButton ----
		cornerDontRoundRadioButton.setText("Don't Round");
		cornerDontRoundRadioButton.addActionListener(e -> cornerChanged());
		add(cornerDontRoundRadioButton, "cell 1 4 4 1");

		//---- cornerRoundRadioButton ----
		cornerRoundRadioButton.setText("Round");
		cornerRoundRadioButton.addActionListener(e -> cornerChanged());
		add(cornerRoundRadioButton, "cell 1 4 4 1");

		//---- cornerRoundSmallRadioButton ----
		cornerRoundSmallRadioButton.setText("Round Small");
		cornerRoundSmallRadioButton.addActionListener(e -> cornerChanged());
		add(cornerRoundSmallRadioButton, "cell 1 4 4 1");

		//---- modeButtonGroup ----
		ButtonGroup modeButtonGroup = new ButtonGroup();
		modeButtonGroup.add(lightModeRadioButton);
		modeButtonGroup.add(darkModeRadioButton);

		//---- borderColorButtonGroup ----
		ButtonGroup borderColorButtonGroup = new ButtonGroup();
		borderColorButtonGroup.add(borderDefaultRadioButton);
		borderColorButtonGroup.add(borderRedRadioButton);
		borderColorButtonGroup.add(borderGreenRadioButton);
		borderColorButtonGroup.add(borderNoneRadioButton);

		//---- captionColorButtonGroup ----
		ButtonGroup captionColorButtonGroup = new ButtonGroup();
		captionColorButtonGroup.add(captionDefaultRadioButton);
		captionColorButtonGroup.add(captionGreenRadioButton);
		captionColorButtonGroup.add(captionYellowRadioButton);
		captionColorButtonGroup.add(captionBlackRadioButton);

		//---- textColorButtonGroup ----
		ButtonGroup textColorButtonGroup = new ButtonGroup();
		textColorButtonGroup.add(textDefaultRadioButton);
		textColorButtonGroup.add(textRedRadioButton);
		textColorButtonGroup.add(textBlueRadioButton);
		textColorButtonGroup.add(textWhiteRadioButton);

		//---- cornerButtonGroup ----
		ButtonGroup cornerButtonGroup = new ButtonGroup();
		cornerButtonGroup.add(cornerDefaultRadioButton);
		cornerButtonGroup.add(cornerDontRoundRadioButton);
		cornerButtonGroup.add(cornerRoundRadioButton);
		cornerButtonGroup.add(cornerRoundSmallRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JRadioButton lightModeRadioButton;
	private JRadioButton darkModeRadioButton;
	private JRadioButton borderDefaultRadioButton;
	private JRadioButton borderRedRadioButton;
	private JRadioButton borderGreenRadioButton;
	private JRadioButton borderNoneRadioButton;
	private JRadioButton captionDefaultRadioButton;
	private JRadioButton captionGreenRadioButton;
	private JRadioButton captionYellowRadioButton;
	private JRadioButton captionBlackRadioButton;
	private JRadioButton textDefaultRadioButton;
	private JRadioButton textRedRadioButton;
	private JRadioButton textBlueRadioButton;
	private JRadioButton textWhiteRadioButton;
	private JRadioButton cornerDefaultRadioButton;
	private JRadioButton cornerDontRoundRadioButton;
	private JRadioButton cornerRoundRadioButton;
	private JRadioButton cornerRoundSmallRadioButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
