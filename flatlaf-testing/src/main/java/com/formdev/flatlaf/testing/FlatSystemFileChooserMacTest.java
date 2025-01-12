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
import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox.State;
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
//			addListeners( frame );
			frame.showFrame( FlatSystemFileChooserMacTest::new );
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

		if( direct ) {
			String[] files = FlatNativeMacLibrary.showFileChooser( open,
				title, prompt, message, filterFieldLabel,
				nameFieldLabel, nameFieldStringValue, directoryURL,
				optionsSet.get(), optionsClear.get(), callback, fileTypeIndex, fileTypes );

			filesField.setText( (files != null) ? Arrays.toString( files ).replace( ',', '\n' ) : "null" );
		} else {
			SecondaryLoop secondaryLoop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();

			String[] fileTypes2 = fileTypes;
			new Thread( () -> {
				String[] files = FlatNativeMacLibrary.showFileChooser( open,
					title, prompt, message, filterFieldLabel,
					nameFieldLabel, nameFieldStringValue, directoryURL,
					optionsSet.get(), optionsClear.get(), callback, fileTypeIndex, fileTypes2 );

				System.out.println( "    secondaryLoop.exit() returned " + secondaryLoop.exit() );

				EventQueue.invokeLater( () -> {
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

	@SuppressWarnings( "unused" )
	private static void addListeners( Window w ) {
		w.addWindowListener( new WindowListener() {
			@Override
			public void windowOpened( WindowEvent e ) {
				System.out.println( e );
			}

			@Override
			public void windowIconified( WindowEvent e ) {
				System.out.println( e );
			}

			@Override
			public void windowDeiconified( WindowEvent e ) {
				System.out.println( e );
			}

			@Override
			public void windowDeactivated( WindowEvent e ) {
				System.out.println( e );
			}

			@Override
			public void windowClosing( WindowEvent e ) {
				System.out.println( e );
			}

			@Override
			public void windowClosed( WindowEvent e ) {
				System.out.println( e );
			}

			@Override
			public void windowActivated( WindowEvent e ) {
				System.out.println( e );
			}
		} );
		w.addWindowStateListener( new WindowStateListener() {
			@Override
			public void windowStateChanged( WindowEvent e ) {
				System.out.println( e );
			}
		} );
		w.addWindowFocusListener( new WindowFocusListener() {
			@Override
			public void windowLostFocus( WindowEvent e ) {
				System.out.println( e );
			}

			@Override
			public void windowGainedFocus( WindowEvent e ) {
				System.out.println( e );
			}
		} );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		titleLabel = new JLabel();
		titleField = new JTextField();
		panel1 = new JPanel();
		options1Label = new JLabel();
		canChooseFilesCheckBox = new JCheckBox();
		canChooseDirectoriesCheckBox = new JCheckBox();
		resolvesAliasesCheckBox = new FlatTriStateCheckBox();
		allowsMultipleSelectionCheckBox = new FlatTriStateCheckBox();
		accessoryViewDisclosedCheckBox = new JCheckBox();
		options2Label = new JLabel();
		showsTagFieldCheckBox = new FlatTriStateCheckBox();
		canCreateDirectoriesCheckBox = new FlatTriStateCheckBox();
		canSelectHiddenExtensionCheckBox = new FlatTriStateCheckBox();
		showsHiddenFilesCheckBox = new FlatTriStateCheckBox();
		extensionHiddenCheckBox = new FlatTriStateCheckBox();
		allowsOtherFileTypesCheckBox = new FlatTriStateCheckBox();
		treatsFilePackagesAsDirectoriesCheckBox = new FlatTriStateCheckBox();
		options3Label = new JLabel();
		showSingleFilterFieldCheckBox = new JCheckBox();
		promptLabel = new JLabel();
		promptField = new JTextField();
		messageLabel = new JLabel();
		messageField = new JTextField();
		filterFieldLabelLabel = new JLabel();
		filterFieldLabelField = new JTextField();
		nameFieldLabelLabel = new JLabel();
		nameFieldLabelField = new JTextField();
		nameFieldStringValueLabel = new JLabel();
		nameFieldStringValueField = new JTextField();
		directoryURLLabel = new JLabel();
		directoryURLField = new JTextField();
		fileTypesLabel = new JLabel();
		fileTypesField = new JComboBox<>();
		fileTypeIndexLabel = new JLabel();
		fileTypeIndexSlider = new JSlider();
		openButton = new JButton();
		saveButton = new JButton();
		openDirectButton = new JButton();
		saveDirectButton = new JButton();
		showMessageDialogOnOKCheckBox = new JCheckBox();
		filesScrollPane = new JScrollPane();
		filesField = new JTextArea();

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
			"[grow,fill]"));

		//---- titleLabel ----
		titleLabel.setText("title");
		add(titleLabel, "cell 0 0");
		add(titleField, "cell 1 0");

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
		add(panel1, "cell 2 0 1 10,aligny top,growy 0");

		//---- promptLabel ----
		promptLabel.setText("prompt");
		add(promptLabel, "cell 0 1");
		add(promptField, "cell 1 1");

		//---- messageLabel ----
		messageLabel.setText("message");
		add(messageLabel, "cell 0 2");
		add(messageField, "cell 1 2");

		//---- filterFieldLabelLabel ----
		filterFieldLabelLabel.setText("filterFieldLabel");
		add(filterFieldLabelLabel, "cell 0 3");
		add(filterFieldLabelField, "cell 1 3");

		//---- nameFieldLabelLabel ----
		nameFieldLabelLabel.setText("nameFieldLabel");
		add(nameFieldLabelLabel, "cell 0 4");
		add(nameFieldLabelField, "cell 1 4");

		//---- nameFieldStringValueLabel ----
		nameFieldStringValueLabel.setText("nameFieldStringValue");
		add(nameFieldStringValueLabel, "cell 0 5");
		add(nameFieldStringValueField, "cell 1 5");

		//---- directoryURLLabel ----
		directoryURLLabel.setText("directoryURL");
		add(directoryURLLabel, "cell 0 6");
		add(directoryURLField, "cell 1 6");

		//---- fileTypesLabel ----
		fileTypesLabel.setText("fileTypes");
		add(fileTypesLabel, "cell 0 7");

		//---- fileTypesField ----
		fileTypesField.setEditable(true);
		fileTypesField.setModel(new DefaultComboBoxModel<>(new String[] {
			"Text Files,txt,null",
			"All Files,*,null",
			"Text Files,txt,null,PDF Files,pdf,null,All Files,*,null",
			"Text and PDF Files,txt,pdf,null",
			"Compressed,zip,gz,null,Disk Images,dmg,null"
		}));
		add(fileTypesField, "cell 1 7");

		//---- fileTypeIndexLabel ----
		fileTypeIndexLabel.setText("fileTypeIndex");
		add(fileTypeIndexLabel, "cell 0 8");

		//---- fileTypeIndexSlider ----
		fileTypeIndexSlider.setMaximum(10);
		fileTypeIndexSlider.setMajorTickSpacing(1);
		fileTypeIndexSlider.setValue(0);
		fileTypeIndexSlider.setPaintLabels(true);
		fileTypeIndexSlider.setSnapToTicks(true);
		add(fileTypeIndexSlider, "cell 1 8");

		//---- openButton ----
		openButton.setText("Open...");
		openButton.addActionListener(e -> open());
		add(openButton, "cell 0 10 3 1");

		//---- saveButton ----
		saveButton.setText("Save...");
		saveButton.addActionListener(e -> save());
		add(saveButton, "cell 0 10 3 1");

		//---- openDirectButton ----
		openDirectButton.setText("Open (no-thread)...");
		openDirectButton.addActionListener(e -> openDirect());
		add(openDirectButton, "cell 0 10 3 1");

		//---- saveDirectButton ----
		saveDirectButton.setText("Save (no-thread)...");
		saveDirectButton.addActionListener(e -> saveDirect());
		add(saveDirectButton, "cell 0 10 3 1");

		//---- showMessageDialogOnOKCheckBox ----
		showMessageDialogOnOKCheckBox.setText("show message dialog on OK");
		add(showMessageDialogOnOKCheckBox, "cell 0 10 3 1");

		//======== filesScrollPane ========
		{

			//---- filesField ----
			filesField.setRows(8);
			filesScrollPane.setViewportView(filesField);
		}
		add(filesScrollPane, "cell 0 11 3 1,growx");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel titleLabel;
	private JTextField titleField;
	private JPanel panel1;
	private JLabel options1Label;
	private JCheckBox canChooseFilesCheckBox;
	private JCheckBox canChooseDirectoriesCheckBox;
	private FlatTriStateCheckBox resolvesAliasesCheckBox;
	private FlatTriStateCheckBox allowsMultipleSelectionCheckBox;
	private JCheckBox accessoryViewDisclosedCheckBox;
	private JLabel options2Label;
	private FlatTriStateCheckBox showsTagFieldCheckBox;
	private FlatTriStateCheckBox canCreateDirectoriesCheckBox;
	private FlatTriStateCheckBox canSelectHiddenExtensionCheckBox;
	private FlatTriStateCheckBox showsHiddenFilesCheckBox;
	private FlatTriStateCheckBox extensionHiddenCheckBox;
	private FlatTriStateCheckBox allowsOtherFileTypesCheckBox;
	private FlatTriStateCheckBox treatsFilePackagesAsDirectoriesCheckBox;
	private JLabel options3Label;
	private JCheckBox showSingleFilterFieldCheckBox;
	private JLabel promptLabel;
	private JTextField promptField;
	private JLabel messageLabel;
	private JTextField messageField;
	private JLabel filterFieldLabelLabel;
	private JTextField filterFieldLabelField;
	private JLabel nameFieldLabelLabel;
	private JTextField nameFieldLabelField;
	private JLabel nameFieldStringValueLabel;
	private JTextField nameFieldStringValueField;
	private JLabel directoryURLLabel;
	private JTextField directoryURLField;
	private JLabel fileTypesLabel;
	private JComboBox<String> fileTypesField;
	private JLabel fileTypeIndexLabel;
	private JSlider fileTypeIndexSlider;
	private JButton openButton;
	private JButton saveButton;
	private JButton openDirectButton;
	private JButton saveDirectButton;
	private JCheckBox showMessageDialogOnOKCheckBox;
	private JScrollPane filesScrollPane;
	private JTextArea filesField;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
