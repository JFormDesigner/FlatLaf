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

import static com.formdev.flatlaf.ui.FlatNativeMacLibrary.*;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox.State;
import com.formdev.flatlaf.testing.FlatSystemFileChooserTest.DummyModalDialog;
import com.formdev.flatlaf.ui.FlatNativeMacLibrary;
import com.formdev.flatlaf.util.SystemInfo;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatSystemFileChooserMacTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		// macOS  (see https://www.formdev.com/flatlaf/macos/)
		if( SystemInfo.isMacOS ) {
			// enable screen menu bar
			// (moves menu bar from JFrame window to top of screen)
			System.setProperty( "apple.laf.useScreenMenuBar", "true" );

			// appearance of window title bars
			// possible values:
			//   - "system": use current macOS appearance (light or dark)
			//   - "NSAppearanceNameAqua": use light appearance
			//   - "NSAppearanceNameDarkAqua": use dark appearance
			// (needs to be set on main thread; setting it on AWT thread does not work)
			System.setProperty( "apple.awt.application.appearance", "system" );
		}

		SwingUtilities.invokeLater( () -> {
			if( !FlatNativeMacLibrary.isLoaded() ) {
				JOptionPane.showMessageDialog( null, "FlatLaf native library not loaded" );
				return;
			}

			FlatTestFrame frame = FlatTestFrame.create( args, "FlatSystemFileChooserMacTest" );
			FlatSystemFileChooserTest.addListeners( frame );
			frame.showFrame( FlatSystemFileChooserMacTest::new );
			frame.setJMenuBar( menuBar1 );
		} );
	}

	FlatSystemFileChooserMacTest() {
		initComponents();

		fileTypesField.setSelectedItem( null );
	}

	private void open() {
		openOrSave( true, false );
	}

	private void save() {
		openOrSave( false, false );
	}

	private void openDirect() {
		openOrSave( true, true );
	}

	private void saveDirect() {
		openOrSave( false, true );
	}

	private void openOrSave( boolean open, boolean direct ) {
		Window frame = SwingUtilities.windowForComponent( this );
		if( ownerFrameRadioButton.isSelected() )
			openOrSave( open, direct, frame );
		else if( ownerDialogRadioButton.isSelected() )
			new DummyModalDialog( frame, owner -> openOrSave( open, direct, owner ) ).setVisible( true );
		else
			openOrSave( open, direct, null );
	}

	private void openOrSave( boolean open, boolean direct, Window owner ) {
		String title = n( titleField.getText() );
		String prompt = n( promptField.getText() );
		String message = n( messageField.getText() );
		String filterFieldLabel = n( filterFieldLabelField.getText() );
		String nameFieldLabel = n( nameFieldLabelField.getText() );
		String nameFieldStringValue = n( nameFieldStringValueField.getText() );
		String directoryURL = n( directoryURLField.getText() );
		AtomicInteger optionsSet = new AtomicInteger();
		AtomicInteger optionsClear = new AtomicInteger();

		// NSOpenPanel
		if( canChooseFilesCheckBox.isSelected() )
			optionsSet.set( optionsSet.get() | FC_canChooseFiles );
		if( canChooseDirectoriesCheckBox.isSelected() )
			optionsSet.set( optionsSet.get() | FC_canChooseDirectories );
		o( FC_resolvesAliases, resolvesAliasesCheckBox, optionsSet, optionsClear );
		o( FC_allowsMultipleSelection, allowsMultipleSelectionCheckBox, optionsSet, optionsClear );
		if( accessoryViewDisclosedCheckBox.isSelected() )
			optionsSet.set( optionsSet.get() | FC_accessoryViewDisclosed );

		// NSSavePanel
		o( FC_showsTagField, showsTagFieldCheckBox, optionsSet, optionsClear );
		o( FC_canCreateDirectories, canCreateDirectoriesCheckBox, optionsSet, optionsClear );
		o( FC_canSelectHiddenExtension, canSelectHiddenExtensionCheckBox, optionsSet, optionsClear );
		o( FC_showsHiddenFiles, showsHiddenFilesCheckBox, optionsSet, optionsClear );
		o( FC_extensionHidden, extensionHiddenCheckBox, optionsSet, optionsClear );
		o( FC_allowsOtherFileTypes, allowsOtherFileTypesCheckBox, optionsSet, optionsClear );
		o( FC_treatsFilePackagesAsDirectories, treatsFilePackagesAsDirectoriesCheckBox, optionsSet, optionsClear );

		// custom
		if( showSingleFilterFieldCheckBox.isSelected() )
			optionsSet.set( optionsSet.get() | FC_showSingleFilterField );

		String fileTypesStr = n( (String) fileTypesField.getSelectedItem() );
		String[] fileTypes = {};
		if( fileTypesStr != null ) {
			if( !fileTypesStr.endsWith( ",null" ) )
				fileTypesStr += ",null";
			fileTypes = fileTypesStr.trim().split( "[,]+" );
			for( int i = 0; i < fileTypes.length; i++ ) {
				if( "null".equals( fileTypes[i] ) )
					fileTypes[i] = null;
			}
		}
		int fileTypeIndex = fileTypeIndexSlider.getValue();

		FlatNativeMacLibrary.FileChooserCallback callback = (files, hwndFileDialog) -> {
			System.out.println( "  -- callback " + hwndFileDialog + "  " + Arrays.toString( files ) );
			if( showMessageDialogOnOKCheckBox.isSelected() ) {
				int result = FlatNativeMacLibrary.showMessageDialog( hwndFileDialog,
					JOptionPane.INFORMATION_MESSAGE,
					"primary text", "secondary text", 0, "Yes", "No" );
				System.out.println( "     result   " + result );
				if( result != 0 )
					return false;
			}
			return true;
		};

		int dark = FlatLaf.isLafDark() ? 1 : 0;
		if( direct ) {
			String[] files = FlatNativeMacLibrary.showFileChooser( owner, dark, open,
				title, prompt, message, filterFieldLabel,
				nameFieldLabel, nameFieldStringValue, directoryURL,
				optionsSet.get(), optionsClear.get(), callback, fileTypeIndex, fileTypes );

			filesField.setText( (files != null) ? Arrays.toString( files ).replace( ',', '\n' ) : "null" );
		} else {
			SecondaryLoop secondaryLoop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();

			String[] fileTypes2 = fileTypes;
			new Thread( () -> {
				String[] files = FlatNativeMacLibrary.showFileChooser( owner, dark, open,
					title, prompt, message, filterFieldLabel,
					nameFieldLabel, nameFieldStringValue, directoryURL,
					optionsSet.get(), optionsClear.get(), callback, fileTypeIndex, fileTypes2 );

				System.out.println( "    secondaryLoop.exit() returned " + secondaryLoop.exit() );

				SwingUtilities.invokeLater( () -> {
					filesField.setText( (files != null) ? Arrays.toString( files ).replace( ',', '\n' ) : "null" );
				} );
			} ).start();

			System.out.println( "---- enter secondary loop ----" );
			System.out.println( "---- secondary loop exited (secondaryLoop.enter() returned " + secondaryLoop.enter() + ") ----" );
		}
	}

	private static String n( String s ) {
		return s != null && !s.isEmpty() ? s : null;
	}

	private static void o( int option, FlatTriStateCheckBox checkBox, AtomicInteger optionsSet, AtomicInteger optionsClear ) {
		if( checkBox.getState() == State.SELECTED )
			optionsSet.set( optionsSet.get() | option );
		else if( checkBox.getState() == State.UNSELECTED )
			optionsClear.set( optionsClear.get() | option );
	}

	private void menuItemAction() {
		System.out.println( "menu item action" );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel ownerLabel = new JLabel();
		ownerFrameRadioButton = new JRadioButton();
		ownerDialogRadioButton = new JRadioButton();
		ownerNullRadioButton = new JRadioButton();
		JPanel ownerSpacer = new JPanel(null);
		JLabel titleLabel = new JLabel();
		titleField = new JTextField();
		JPanel panel1 = new JPanel();
		JLabel options1Label = new JLabel();
		canChooseFilesCheckBox = new JCheckBox();
		canChooseDirectoriesCheckBox = new JCheckBox();
		resolvesAliasesCheckBox = new FlatTriStateCheckBox();
		allowsMultipleSelectionCheckBox = new FlatTriStateCheckBox();
		accessoryViewDisclosedCheckBox = new JCheckBox();
		JLabel options2Label = new JLabel();
		showsTagFieldCheckBox = new FlatTriStateCheckBox();
		canCreateDirectoriesCheckBox = new FlatTriStateCheckBox();
		canSelectHiddenExtensionCheckBox = new FlatTriStateCheckBox();
		showsHiddenFilesCheckBox = new FlatTriStateCheckBox();
		extensionHiddenCheckBox = new FlatTriStateCheckBox();
		allowsOtherFileTypesCheckBox = new FlatTriStateCheckBox();
		treatsFilePackagesAsDirectoriesCheckBox = new FlatTriStateCheckBox();
		JLabel options3Label = new JLabel();
		showSingleFilterFieldCheckBox = new JCheckBox();
		JLabel promptLabel = new JLabel();
		promptField = new JTextField();
		JLabel messageLabel = new JLabel();
		messageField = new JTextField();
		JLabel filterFieldLabelLabel = new JLabel();
		filterFieldLabelField = new JTextField();
		JLabel nameFieldLabelLabel = new JLabel();
		nameFieldLabelField = new JTextField();
		JLabel nameFieldStringValueLabel = new JLabel();
		nameFieldStringValueField = new JTextField();
		JLabel directoryURLLabel = new JLabel();
		directoryURLField = new JTextField();
		JLabel fileTypesLabel = new JLabel();
		fileTypesField = new JComboBox<>();
		JLabel fileTypeIndexLabel = new JLabel();
		fileTypeIndexSlider = new JSlider();
		JButton openButton = new JButton();
		JButton saveButton = new JButton();
		JButton openDirectButton = new JButton();
		JButton saveDirectButton = new JButton();
		showMessageDialogOnOKCheckBox = new JCheckBox();
		JScrollPane filesScrollPane = new JScrollPane();
		filesField = new JTextArea();
		menuBar1 = new JMenuBar();
		JMenu menu1 = new JMenu();
		JMenuItem menuItem1 = new JMenuItem();
		JMenuItem menuItem2 = new JMenuItem();
		JMenu menu2 = new JMenu();
		JMenuItem menuItem3 = new JMenuItem();
		JMenuItem menuItem4 = new JMenuItem();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[left]" +
			"[grow,fill]" +
			"[fill]",
			// rows
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[grow,fill]"));

		//---- ownerLabel ----
		ownerLabel.setText("owner");
		add(ownerLabel, "cell 0 0");

		//---- ownerFrameRadioButton ----
		ownerFrameRadioButton.setText("JFrame");
		ownerFrameRadioButton.setSelected(true);
		add(ownerFrameRadioButton, "cell 1 0");

		//---- ownerDialogRadioButton ----
		ownerDialogRadioButton.setText("JDialog");
		add(ownerDialogRadioButton, "cell 1 0");

		//---- ownerNullRadioButton ----
		ownerNullRadioButton.setText("null");
		add(ownerNullRadioButton, "cell 1 0");
		add(ownerSpacer, "cell 1 0,growx");

		//---- titleLabel ----
		titleLabel.setText("title");
		add(titleLabel, "cell 0 1");
		add(titleField, "cell 1 1");

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"insets 2,hidemode 3",
				// columns
				"[left]",
				// rows
				"[]" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]para" +
				"[]" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]para" +
				"[]" +
				"[]"));

			//---- options1Label ----
			options1Label.setText("NSOpenPanel options:");
			panel1.add(options1Label, "cell 0 0");

			//---- canChooseFilesCheckBox ----
			canChooseFilesCheckBox.setText("canChooseFiles");
			canChooseFilesCheckBox.setSelected(true);
			panel1.add(canChooseFilesCheckBox, "cell 0 1");

			//---- canChooseDirectoriesCheckBox ----
			canChooseDirectoriesCheckBox.setText("canChooseDirectories");
			panel1.add(canChooseDirectoriesCheckBox, "cell 0 2");

			//---- resolvesAliasesCheckBox ----
			resolvesAliasesCheckBox.setText("resolvesAliases");
			resolvesAliasesCheckBox.setState(FlatTriStateCheckBox.State.SELECTED);
			panel1.add(resolvesAliasesCheckBox, "cell 0 3");

			//---- allowsMultipleSelectionCheckBox ----
			allowsMultipleSelectionCheckBox.setText("allowsMultipleSelection");
			panel1.add(allowsMultipleSelectionCheckBox, "cell 0 4");

			//---- accessoryViewDisclosedCheckBox ----
			accessoryViewDisclosedCheckBox.setText("accessoryViewDisclosed");
			panel1.add(accessoryViewDisclosedCheckBox, "cell 0 5");

			//---- options2Label ----
			options2Label.setText("NSOpenPanel and NSSavePanel options:");
			panel1.add(options2Label, "cell 0 6");

			//---- showsTagFieldCheckBox ----
			showsTagFieldCheckBox.setText("showsTagField");
			panel1.add(showsTagFieldCheckBox, "cell 0 7");

			//---- canCreateDirectoriesCheckBox ----
			canCreateDirectoriesCheckBox.setText("canCreateDirectories");
			panel1.add(canCreateDirectoriesCheckBox, "cell 0 8");

			//---- canSelectHiddenExtensionCheckBox ----
			canSelectHiddenExtensionCheckBox.setText("canSelectHiddenExtension");
			panel1.add(canSelectHiddenExtensionCheckBox, "cell 0 9");

			//---- showsHiddenFilesCheckBox ----
			showsHiddenFilesCheckBox.setText("showsHiddenFiles");
			panel1.add(showsHiddenFilesCheckBox, "cell 0 10");

			//---- extensionHiddenCheckBox ----
			extensionHiddenCheckBox.setText("extensionHidden");
			panel1.add(extensionHiddenCheckBox, "cell 0 11");

			//---- allowsOtherFileTypesCheckBox ----
			allowsOtherFileTypesCheckBox.setText("allowsOtherFileTypes");
			panel1.add(allowsOtherFileTypesCheckBox, "cell 0 12");

			//---- treatsFilePackagesAsDirectoriesCheckBox ----
			treatsFilePackagesAsDirectoriesCheckBox.setText("treatsFilePackagesAsDirectories");
			panel1.add(treatsFilePackagesAsDirectoriesCheckBox, "cell 0 13");

			//---- options3Label ----
			options3Label.setText("Custom options:");
			panel1.add(options3Label, "cell 0 14");

			//---- showSingleFilterFieldCheckBox ----
			showSingleFilterFieldCheckBox.setText("showSingleFilterField");
			panel1.add(showSingleFilterFieldCheckBox, "cell 0 15");
		}
		add(panel1, "cell 2 1 1 10,aligny top,growy 0");

		//---- promptLabel ----
		promptLabel.setText("prompt");
		add(promptLabel, "cell 0 2");
		add(promptField, "cell 1 2");

		//---- messageLabel ----
		messageLabel.setText("message");
		add(messageLabel, "cell 0 3");
		add(messageField, "cell 1 3");

		//---- filterFieldLabelLabel ----
		filterFieldLabelLabel.setText("filterFieldLabel");
		add(filterFieldLabelLabel, "cell 0 4");
		add(filterFieldLabelField, "cell 1 4");

		//---- nameFieldLabelLabel ----
		nameFieldLabelLabel.setText("nameFieldLabel");
		add(nameFieldLabelLabel, "cell 0 5");
		add(nameFieldLabelField, "cell 1 5");

		//---- nameFieldStringValueLabel ----
		nameFieldStringValueLabel.setText("nameFieldStringValue");
		add(nameFieldStringValueLabel, "cell 0 6");
		add(nameFieldStringValueField, "cell 1 6");

		//---- directoryURLLabel ----
		directoryURLLabel.setText("directoryURL");
		add(directoryURLLabel, "cell 0 7");
		add(directoryURLField, "cell 1 7");

		//---- fileTypesLabel ----
		fileTypesLabel.setText("fileTypes");
		add(fileTypesLabel, "cell 0 8");

		//---- fileTypesField ----
		fileTypesField.setEditable(true);
		fileTypesField.setModel(new DefaultComboBoxModel<>(new String[] {
			"Text Files,txt,null",
			"All Files,*,null",
			"Text Files,txt,null,PDF Files,pdf,null,All Files,*,null",
			"Text and PDF Files,txt,pdf,null",
			"Compressed,zip,gz,null,Disk Images,dmg,null"
		}));
		add(fileTypesField, "cell 1 8");

		//---- fileTypeIndexLabel ----
		fileTypeIndexLabel.setText("fileTypeIndex");
		add(fileTypeIndexLabel, "cell 0 9");

		//---- fileTypeIndexSlider ----
		fileTypeIndexSlider.setMaximum(10);
		fileTypeIndexSlider.setMajorTickSpacing(1);
		fileTypeIndexSlider.setValue(0);
		fileTypeIndexSlider.setPaintLabels(true);
		fileTypeIndexSlider.setSnapToTicks(true);
		add(fileTypeIndexSlider, "cell 1 9");

		//---- openButton ----
		openButton.setText("Open...");
		openButton.addActionListener(e -> open());
		add(openButton, "cell 0 11 3 1");

		//---- saveButton ----
		saveButton.setText("Save...");
		saveButton.addActionListener(e -> save());
		add(saveButton, "cell 0 11 3 1");

		//---- openDirectButton ----
		openDirectButton.setText("Open (no-thread)...");
		openDirectButton.addActionListener(e -> openDirect());
		add(openDirectButton, "cell 0 11 3 1");

		//---- saveDirectButton ----
		saveDirectButton.setText("Save (no-thread)...");
		saveDirectButton.addActionListener(e -> saveDirect());
		add(saveDirectButton, "cell 0 11 3 1");

		//---- showMessageDialogOnOKCheckBox ----
		showMessageDialogOnOKCheckBox.setText("show message dialog on OK");
		add(showMessageDialogOnOKCheckBox, "cell 0 11 3 1");

		//======== filesScrollPane ========
		{

			//---- filesField ----
			filesField.setRows(8);
			filesScrollPane.setViewportView(filesField);
		}
		add(filesScrollPane, "cell 0 12 3 1,growx");

		//======== menuBar1 ========
		{

			//======== menu1 ========
			{
				menu1.setText("text");

				//---- menuItem1 ----
				menuItem1.setText("text");
				menuItem1.addActionListener(e -> menuItemAction());
				menu1.add(menuItem1);

				//---- menuItem2 ----
				menuItem2.setText("text");
				menuItem2.addActionListener(e -> menuItemAction());
				menu1.add(menuItem2);
			}
			menuBar1.add(menu1);

			//======== menu2 ========
			{
				menu2.setText("text");

				//---- menuItem3 ----
				menuItem3.setText("text");
				menuItem3.addActionListener(e -> menuItemAction());
				menu2.add(menuItem3);

				//---- menuItem4 ----
				menuItem4.setText("text");
				menuItem4.addActionListener(e -> menuItemAction());
				menu2.add(menuItem4);
			}
			menuBar1.add(menu2);
		}

		//---- ownerButtonGroup ----
		ButtonGroup ownerButtonGroup = new ButtonGroup();
		ownerButtonGroup.add(ownerFrameRadioButton);
		ownerButtonGroup.add(ownerDialogRadioButton);
		ownerButtonGroup.add(ownerNullRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JRadioButton ownerFrameRadioButton;
	private JRadioButton ownerDialogRadioButton;
	private JRadioButton ownerNullRadioButton;
	private JTextField titleField;
	private JCheckBox canChooseFilesCheckBox;
	private JCheckBox canChooseDirectoriesCheckBox;
	private FlatTriStateCheckBox resolvesAliasesCheckBox;
	private FlatTriStateCheckBox allowsMultipleSelectionCheckBox;
	private JCheckBox accessoryViewDisclosedCheckBox;
	private FlatTriStateCheckBox showsTagFieldCheckBox;
	private FlatTriStateCheckBox canCreateDirectoriesCheckBox;
	private FlatTriStateCheckBox canSelectHiddenExtensionCheckBox;
	private FlatTriStateCheckBox showsHiddenFilesCheckBox;
	private FlatTriStateCheckBox extensionHiddenCheckBox;
	private FlatTriStateCheckBox allowsOtherFileTypesCheckBox;
	private FlatTriStateCheckBox treatsFilePackagesAsDirectoriesCheckBox;
	private JCheckBox showSingleFilterFieldCheckBox;
	private JTextField promptField;
	private JTextField messageField;
	private JTextField filterFieldLabelField;
	private JTextField nameFieldLabelField;
	private JTextField nameFieldStringValueField;
	private JTextField directoryURLField;
	private JComboBox<String> fileTypesField;
	private JSlider fileTypeIndexSlider;
	private JCheckBox showMessageDialogOnOKCheckBox;
	private JTextArea filesField;
	private static JMenuBar menuBar1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
