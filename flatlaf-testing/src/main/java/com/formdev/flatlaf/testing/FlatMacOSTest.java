/*
 * Copyright 2024 FormDev Software GmbH
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

import java.awt.*;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatNativeMacLibrary;
import com.formdev.flatlaf.util.SystemInfo;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatMacOSTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, FlatMacOSTest.class.getSimpleName() );
			frame.applyComponentOrientationToFrame = true;

			JRootPane rootPane = frame.getRootPane();
			rootPane.putClientProperty( "apple.awt.fullWindowContent", true );
			rootPane.putClientProperty( "apple.awt.transparentTitleBar", true );
			rootPane.putClientProperty( "apple.awt.windowTitleVisible", false );

			frame.showFrame( FlatMacOSTest::new );
		} );
	}

	FlatMacOSTest() {
		initComponents();

		if( SystemInfo.isMacFullWindowContentSupported ) {
			fullWindowContentHint.setVisible( false );
			transparentTitleBarHint.setVisible( false );
		}
		if( SystemInfo.isJava_17_orLater ) {
			windowTitleVisibleHint.setVisible( false );
			buttonsSpacingHint.setVisible( false );
		}

		placeholderPanel.putClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "mac zeroInFullScreen" );
		UIManager.put( "FlatLaf.debug.panel.showPlaceholders", true );
	}

	@Override
	public void addNotify() {
		super.addNotify();

		JRootPane rootPane = getRootPane();
		fullWindowContentCheckBox.setSelected( FlatClientProperties.clientPropertyBoolean( rootPane, "apple.awt.fullWindowContent", false ) );
		transparentTitleBarCheckBox.setSelected( FlatClientProperties.clientPropertyBoolean( rootPane, "apple.awt.transparentTitleBar", false ) );
		windowTitleVisibleCheckBox.setSelected( FlatClientProperties.clientPropertyBoolean( rootPane, "apple.awt.windowTitleVisible", true ) );

		rootPane.addPropertyChangeListener( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_BOUNDS, e -> {
			Rectangle bounds = (Rectangle) e.getNewValue();
			fullWindowContentButtonsBoundsField.setText( bounds2string( bounds ) );
		} );
		updateNativeButtonBounds();
	}

	@Override
	public void updateUI() {
		super.updateUI();

		if( nativeButtonsBoundsField != null )
			updateNativeButtonBounds();
	}

	private void fullWindowContentChanged() {
		getRootPane().putClientProperty( "apple.awt.fullWindowContent", fullWindowContentCheckBox.isSelected() );
	}

	private void transparentTitleBarChanged() {
		getRootPane().putClientProperty( "apple.awt.transparentTitleBar", transparentTitleBarCheckBox.isSelected() );
	}

	private void windowTitleVisibleChanged() {
		getRootPane().putClientProperty( "apple.awt.windowTitleVisible", windowTitleVisibleCheckBox.isSelected() );
	}

	private void buttonsSpacingChanged() {
		String buttonsSpacing = null;
		if( buttonsSpacingMediumRadioButton.isSelected() )
			buttonsSpacing = FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_MEDIUM;
		else if( buttonsSpacingLargeRadioButton.isSelected() )
			buttonsSpacing = FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE;

		getRootPane().putClientProperty( FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING, buttonsSpacing );

		updateNativeButtonBounds();
	}

	private void updateNativeButtonBounds() {
		if( !FlatNativeMacLibrary.isLoaded() )
			return;

		Window window = SwingUtilities.windowForComponent( this );
		Rectangle bounds = FlatNativeMacLibrary.getWindowButtonsBounds( window );
		nativeButtonsBoundsField.setText( bounds2string( bounds ) );
	}

	private String bounds2string( Rectangle bounds ) {
		return (bounds != null)
			? bounds.width + ", " + bounds.height + "   @ " + bounds.x + ", " + bounds.y
			: "null";
	}

	private void toggleFullScreen() {
		if( !FlatNativeMacLibrary.isLoaded() )
			return;

		Window window = SwingUtilities.windowForComponent( this );
		FlatNativeMacLibrary.toggleWindowFullScreen( window );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel1 = new JPanel();
		placeholderPanel = new JPanel();
		JPanel panel2 = new JPanel();
		fullWindowContentCheckBox = new JCheckBox();
		fullWindowContentHint = new JLabel();
		transparentTitleBarCheckBox = new JCheckBox();
		transparentTitleBarHint = new JLabel();
		windowTitleVisibleCheckBox = new JCheckBox();
		windowTitleVisibleHint = new JLabel();
		JLabel buttonsSpacingLabel = new JLabel();
		buttonsSpacingDefaultRadioButton = new JRadioButton();
		buttonsSpacingMediumRadioButton = new JRadioButton();
		buttonsSpacingLargeRadioButton = new JRadioButton();
		buttonsSpacingHint = new JLabel();
		JLabel fullWindowContentButtonsBoundsLabel = new JLabel();
		fullWindowContentButtonsBoundsField = new JLabel();
		JLabel nativeButtonsBoundsLabel = new JLabel();
		nativeButtonsBoundsField = new JLabel();
		JButton toggleFullScreenButton = new JButton();

		//======== this ========
		setLayout(new BorderLayout());

		//======== panel1 ========
		{
			panel1.setLayout(new BorderLayout());

			//======== placeholderPanel ========
			{
				placeholderPanel.setBackground(Color.green);
				placeholderPanel.setLayout(new FlowLayout());
			}
			panel1.add(placeholderPanel, BorderLayout.WEST);
		}
		add(panel1, BorderLayout.PAGE_START);

		//======== panel2 ========
		{
			panel2.setLayout(new MigLayout(
				"ltr,insets dialog,hidemode 3",
				// columns
				"[left]" +
				"[left]" +
				"[left]" +
				"[left]para" +
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[fill]" +
				"[]" +
				"[]para" +
				"[]"));

			//---- fullWindowContentCheckBox ----
			fullWindowContentCheckBox.setText("fullWindowContent");
			fullWindowContentCheckBox.addActionListener(e -> fullWindowContentChanged());
			panel2.add(fullWindowContentCheckBox, "cell 0 0");

			//---- fullWindowContentHint ----
			fullWindowContentHint.setText("requires Java 12, 11.0.8 or 8u292");
			fullWindowContentHint.setForeground(Color.red);
			panel2.add(fullWindowContentHint, "cell 4 0");

			//---- transparentTitleBarCheckBox ----
			transparentTitleBarCheckBox.setText("transparentTitleBar");
			transparentTitleBarCheckBox.addActionListener(e -> transparentTitleBarChanged());
			panel2.add(transparentTitleBarCheckBox, "cell 0 1");

			//---- transparentTitleBarHint ----
			transparentTitleBarHint.setText("requires Java 12, 11.0.8 or 8u292");
			transparentTitleBarHint.setForeground(Color.red);
			panel2.add(transparentTitleBarHint, "cell 4 1");

			//---- windowTitleVisibleCheckBox ----
			windowTitleVisibleCheckBox.setText("windowTitleVisible");
			windowTitleVisibleCheckBox.addActionListener(e -> windowTitleVisibleChanged());
			panel2.add(windowTitleVisibleCheckBox, "cell 0 2");

			//---- windowTitleVisibleHint ----
			windowTitleVisibleHint.setText("requires Java 17");
			windowTitleVisibleHint.setForeground(Color.red);
			panel2.add(windowTitleVisibleHint, "cell 4 2");

			//---- buttonsSpacingLabel ----
			buttonsSpacingLabel.setText("Buttons spacing:");
			panel2.add(buttonsSpacingLabel, "cell 0 3");

			//---- buttonsSpacingDefaultRadioButton ----
			buttonsSpacingDefaultRadioButton.setText("Default");
			buttonsSpacingDefaultRadioButton.setSelected(true);
			buttonsSpacingDefaultRadioButton.addActionListener(e -> buttonsSpacingChanged());
			panel2.add(buttonsSpacingDefaultRadioButton, "cell 1 3");

			//---- buttonsSpacingMediumRadioButton ----
			buttonsSpacingMediumRadioButton.setText("Medium");
			buttonsSpacingMediumRadioButton.addActionListener(e -> buttonsSpacingChanged());
			panel2.add(buttonsSpacingMediumRadioButton, "cell 2 3");

			//---- buttonsSpacingLargeRadioButton ----
			buttonsSpacingLargeRadioButton.setText("Large");
			buttonsSpacingLargeRadioButton.addActionListener(e -> buttonsSpacingChanged());
			panel2.add(buttonsSpacingLargeRadioButton, "cell 3 3");

			//---- buttonsSpacingHint ----
			buttonsSpacingHint.setText("requires Java 17");
			buttonsSpacingHint.setForeground(Color.red);
			panel2.add(buttonsSpacingHint, "cell 4 3");

			//---- fullWindowContentButtonsBoundsLabel ----
			fullWindowContentButtonsBoundsLabel.setText("Buttons bounds:");
			panel2.add(fullWindowContentButtonsBoundsLabel, "cell 0 4");

			//---- fullWindowContentButtonsBoundsField ----
			fullWindowContentButtonsBoundsField.setText("null");
			panel2.add(fullWindowContentButtonsBoundsField, "cell 1 4 3 1");

			//---- nativeButtonsBoundsLabel ----
			nativeButtonsBoundsLabel.setText("Native buttons bounds:");
			panel2.add(nativeButtonsBoundsLabel, "cell 0 5");

			//---- nativeButtonsBoundsField ----
			nativeButtonsBoundsField.setText("null");
			panel2.add(nativeButtonsBoundsField, "cell 1 5 3 1");

			//---- toggleFullScreenButton ----
			toggleFullScreenButton.setText("Toggle Full Screen");
			toggleFullScreenButton.addActionListener(e -> toggleFullScreen());
			panel2.add(toggleFullScreenButton, "cell 0 6");
		}
		add(panel2, BorderLayout.CENTER);

		//---- buttonsSpacingButtonGroup ----
		ButtonGroup buttonsSpacingButtonGroup = new ButtonGroup();
		buttonsSpacingButtonGroup.add(buttonsSpacingDefaultRadioButton);
		buttonsSpacingButtonGroup.add(buttonsSpacingMediumRadioButton);
		buttonsSpacingButtonGroup.add(buttonsSpacingLargeRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel placeholderPanel;
	private JCheckBox fullWindowContentCheckBox;
	private JLabel fullWindowContentHint;
	private JCheckBox transparentTitleBarCheckBox;
	private JLabel transparentTitleBarHint;
	private JCheckBox windowTitleVisibleCheckBox;
	private JLabel windowTitleVisibleHint;
	private JRadioButton buttonsSpacingDefaultRadioButton;
	private JRadioButton buttonsSpacingMediumRadioButton;
	private JRadioButton buttonsSpacingLargeRadioButton;
	private JLabel buttonsSpacingHint;
	private JLabel fullWindowContentButtonsBoundsField;
	private JLabel nativeButtonsBoundsField;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
