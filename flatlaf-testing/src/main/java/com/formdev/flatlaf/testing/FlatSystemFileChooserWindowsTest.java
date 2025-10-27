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

import static com.formdev.flatlaf.ui.FlatNativeWindowsLibrary.*;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import com.formdev.flatlaf.demo.DemoPrefs;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox.State;
import com.formdev.flatlaf.testing.FlatSystemFileChooserTest.DummyModalDialog;
import com.formdev.flatlaf.ui.FlatNativeWindowsLibrary;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatSystemFileChooserWindowsTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			if( !FlatNativeWindowsLibrary.isLoaded() ) {
				JOptionPane.showMessageDialog( null, "FlatLaf native library not loaded" );
				return;
			}

			FlatTestFrame frame = FlatTestFrame.create( args, "FlatSystemFileChooserWindowsTest" );
			FlatSystemFileChooserTest.addListeners( frame );
			frame.showFrame( FlatSystemFileChooserWindowsTest::new );
		} );
	}

	FlatSystemFileChooserWindowsTest() {
		initComponents();

		fileTypesField.setSelectedItem( null );

		Preferences state = DemoPrefs.getState();
		messageField.setText( state.get( "systemfilechooser.windows.message", "some message" ) );
		buttonsField.setText( state.get( "systemfilechooser.windows.buttons", "OK" ) );
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
		String okButtonLabel = n( okButtonLabelField.getText() );
		String fileNameLabel = n( fileNameLabelField.getText() );
		String fileName = n( fileNameField.getText() );
		String folder = n( folderField.getText() );
		String saveAsItem = n( saveAsItemField.getText() );
		String defaultFolder = n( defaultFolderField.getText() );
		String defaultExtension = n( defaultExtensionField.getText() );
		AtomicInteger optionsSet = new AtomicInteger();
		AtomicInteger optionsClear = new AtomicInteger();

		o( FOS_OVERWRITEPROMPT, overwritePromptCheckBox, optionsSet, optionsClear );
		o( FOS_STRICTFILETYPES, strictFileTypesCheckBox, optionsSet, optionsClear );
		o( FOS_NOCHANGEDIR, noChangeDirCheckBox, optionsSet, optionsClear );
		o( FOS_PICKFOLDERS, pickFoldersCheckBox, optionsSet, optionsClear );
		o( FOS_FORCEFILESYSTEM, forceFileSystemCheckBox, optionsSet, optionsClear );
		o( FOS_ALLNONSTORAGEITEMS, allNonStorageItemsCheckBox, optionsSet, optionsClear );
		o( FOS_NOVALIDATE, noValidateCheckBox, optionsSet, optionsClear );
		o( FOS_ALLOWMULTISELECT, allowMultiSelectCheckBox, optionsSet, optionsClear );
		o( FOS_PATHMUSTEXIST, pathMustExistCheckBox, optionsSet, optionsClear );
		o( FOS_FILEMUSTEXIST, fileMustExistCheckBox, optionsSet, optionsClear );
		o( FOS_CREATEPROMPT, createPromptCheckBox, optionsSet, optionsClear );
		o( FOS_SHAREAWARE, shareAwareCheckBox, optionsSet, optionsClear );
		o( FOS_NOREADONLYRETURN, noReadOnlyReturnCheckBox, optionsSet, optionsClear );
		o( FOS_NOTESTFILECREATE, noTestFileCreateCheckBox, optionsSet, optionsClear );
		o( FOS_HIDEMRUPLACES, hideMruPlacesCheckBox, optionsSet, optionsClear );
		o( FOS_HIDEPINNEDPLACES, hidePinnedPlacesCheckBox, optionsSet, optionsClear );
		o( FOS_NODEREFERENCELINKS, noDereferenceLinksCheckBox, optionsSet, optionsClear );
		o( FOS_OKBUTTONNEEDSINTERACTION, okButtonNeedsInteractionCheckBox, optionsSet, optionsClear );
		o( FOS_DONTADDTORECENT, dontAddToRecentCheckBox, optionsSet, optionsClear );
		o( FOS_FORCESHOWHIDDEN, forceShowHiddenCheckBox, optionsSet, optionsClear );
		o( FOS_DEFAULTNOMINIMODE, defaultNoMiniModeCheckBox, optionsSet, optionsClear );
		o( FOS_FORCEPREVIEWPANEON, forcePreviewPaneonCheckBox, optionsSet, optionsClear );
		o( FOS_SUPPORTSTREAMABLEITEMS, supportStreamableItemsCheckBox, optionsSet, optionsClear );

		String fileTypesStr = n( (String) fileTypesField.getSelectedItem() );
		String[] fileTypes = {};
		if( fileTypesStr != null )
			fileTypes = fileTypesStr.trim().split( "[,]+" );
		int fileTypeIndex = fileTypeIndexSlider.getValue();

		FlatNativeWindowsLibrary.FileChooserCallback callback = (files, hwndFileDialog) -> {
			System.out.println( "  -- callback " + hwndFileDialog + "  " + Arrays.toString( files ) );
			if( showMessageDialogOnOKCheckBox.isSelected() ) {
				System.out.println( FlatNativeWindowsLibrary.showMessageDialog( hwndFileDialog,
					JOptionPane.INFORMATION_MESSAGE,
					null, "some text", 1, "Yes", "No" ) );
			}
			return true;
		};

		if( direct ) {
			String[] files = FlatNativeWindowsLibrary.showFileChooser( owner, open,
				title, okButtonLabel, fileNameLabel, fileName,
				folder, saveAsItem, defaultFolder, defaultExtension,
				optionsSet.get(), optionsClear.get(), callback, fileTypeIndex, fileTypes );

			filesField.setText( (files != null) ? Arrays.toString( files ).replace( ',', '\n' ) : "null" );
		} else {
			SecondaryLoop secondaryLoop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();

			String[] fileTypes2 = fileTypes;
			new Thread( () -> {
				String[] files = FlatNativeWindowsLibrary.showFileChooser( owner, open,
					title, okButtonLabel, fileNameLabel, fileName,
					folder, saveAsItem, defaultFolder, defaultExtension,
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

	private void messageDialog() {
		long hwnd = getHWND( SwingUtilities.windowForComponent( this ) );
		String message = messageField.getText();
		String[] buttons = buttonsField.getText().trim().split( "[,]+" );

		Preferences state = DemoPrefs.getState();
		state.put( "systemfilechooser.windows.message", message );
		state.put( "systemfilechooser.windows.buttons", buttonsField.getText() );

		System.out.println( FlatNativeWindowsLibrary.showMessageDialog( hwnd,
			JOptionPane.WARNING_MESSAGE, null, message, 1, buttons ) );
	}

	private void messageBox() {
		long hwnd = getHWND( SwingUtilities.windowForComponent( this ) );
		String message = messageField.getText();

		System.out.println( FlatNativeWindowsLibrary.showMessageBox( hwnd, message, null,
			/* MB_ICONINFORMATION */ 0x00000040 | /* MB_YESNO */ 0x00000004 ) );
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
		overwritePromptCheckBox = new FlatTriStateCheckBox();
		pathMustExistCheckBox = new FlatTriStateCheckBox();
		noDereferenceLinksCheckBox = new FlatTriStateCheckBox();
		strictFileTypesCheckBox = new FlatTriStateCheckBox();
		fileMustExistCheckBox = new FlatTriStateCheckBox();
		okButtonNeedsInteractionCheckBox = new FlatTriStateCheckBox();
		noChangeDirCheckBox = new FlatTriStateCheckBox();
		createPromptCheckBox = new FlatTriStateCheckBox();
		dontAddToRecentCheckBox = new FlatTriStateCheckBox();
		pickFoldersCheckBox = new FlatTriStateCheckBox();
		shareAwareCheckBox = new FlatTriStateCheckBox();
		forceShowHiddenCheckBox = new FlatTriStateCheckBox();
		forceFileSystemCheckBox = new FlatTriStateCheckBox();
		noReadOnlyReturnCheckBox = new FlatTriStateCheckBox();
		defaultNoMiniModeCheckBox = new FlatTriStateCheckBox();
		allNonStorageItemsCheckBox = new FlatTriStateCheckBox();
		noTestFileCreateCheckBox = new FlatTriStateCheckBox();
		forcePreviewPaneonCheckBox = new FlatTriStateCheckBox();
		noValidateCheckBox = new FlatTriStateCheckBox();
		hideMruPlacesCheckBox = new FlatTriStateCheckBox();
		supportStreamableItemsCheckBox = new FlatTriStateCheckBox();
		allowMultiSelectCheckBox = new FlatTriStateCheckBox();
		hidePinnedPlacesCheckBox = new FlatTriStateCheckBox();
		JPanel messageDialogPanel = new JPanel();
		JLabel messageLabel = new JLabel();
		JScrollPane messageScrollPane = new JScrollPane();
		messageField = new JTextArea();
		JLabel buttonsLabel = new JLabel();
		buttonsField = new JTextField();
		JLabel okButtonLabelLabel = new JLabel();
		okButtonLabelField = new JTextField();
		JLabel fileNameLabelLabel = new JLabel();
		fileNameLabelField = new JTextField();
		JLabel fileNameLabel = new JLabel();
		fileNameField = new JTextField();
		JLabel folderLabel = new JLabel();
		folderField = new JTextField();
		JLabel saveAsItemLabel = new JLabel();
		saveAsItemField = new JTextField();
		JLabel defaultFolderLabel = new JLabel();
		defaultFolderField = new JTextField();
		JLabel defaultExtensionLabel = new JLabel();
		defaultExtensionField = new JTextField();
		JLabel fileTypesLabel = new JLabel();
		fileTypesField = new JComboBox<>();
		JLabel fileTypeIndexLabel = new JLabel();
		fileTypeIndexSlider = new JSlider();
		JButton openButton = new JButton();
		JButton saveButton = new JButton();
		JButton openDirectButton = new JButton();
		JButton saveDirectButton = new JButton();
		showMessageDialogOnOKCheckBox = new JCheckBox();
		JPanel hSpacer1 = new JPanel(null);
		JButton messageDialogButton = new JButton();
		JButton messageBoxButton = new JButton();
		JScrollPane filesScrollPane = new JScrollPane();
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
				"[left]para" +
				"[left]para" +
				"[left]",
				// rows
				"[]0" +
				"[]0" +
				"[]0" +
				"[]" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[grow]"));

			//---- overwritePromptCheckBox ----
			overwritePromptCheckBox.setText("overwritePrompt");
			panel1.add(overwritePromptCheckBox, "cell 0 0");

			//---- pathMustExistCheckBox ----
			pathMustExistCheckBox.setText("pathMustExist");
			panel1.add(pathMustExistCheckBox, "cell 1 0");

			//---- noDereferenceLinksCheckBox ----
			noDereferenceLinksCheckBox.setText("noDereferenceLinks");
			panel1.add(noDereferenceLinksCheckBox, "cell 2 0");

			//---- strictFileTypesCheckBox ----
			strictFileTypesCheckBox.setText("strictFileTypes");
			panel1.add(strictFileTypesCheckBox, "cell 0 1");

			//---- fileMustExistCheckBox ----
			fileMustExistCheckBox.setText("fileMustExist");
			panel1.add(fileMustExistCheckBox, "cell 1 1");

			//---- okButtonNeedsInteractionCheckBox ----
			okButtonNeedsInteractionCheckBox.setText("okButtonNeedsInteraction");
			panel1.add(okButtonNeedsInteractionCheckBox, "cell 2 1");

			//---- noChangeDirCheckBox ----
			noChangeDirCheckBox.setText("noChangeDir");
			panel1.add(noChangeDirCheckBox, "cell 0 2");

			//---- createPromptCheckBox ----
			createPromptCheckBox.setText("createPrompt");
			panel1.add(createPromptCheckBox, "cell 1 2");

			//---- dontAddToRecentCheckBox ----
			dontAddToRecentCheckBox.setText("dontAddToRecent");
			panel1.add(dontAddToRecentCheckBox, "cell 2 2");

			//---- pickFoldersCheckBox ----
			pickFoldersCheckBox.setText("pickFolders");
			pickFoldersCheckBox.setFont(pickFoldersCheckBox.getFont().deriveFont(pickFoldersCheckBox.getFont().getStyle() | Font.BOLD));
			panel1.add(pickFoldersCheckBox, "cell 0 3");

			//---- shareAwareCheckBox ----
			shareAwareCheckBox.setText("shareAware");
			panel1.add(shareAwareCheckBox, "cell 1 3");

			//---- forceShowHiddenCheckBox ----
			forceShowHiddenCheckBox.setText("forceShowHidden");
			forceShowHiddenCheckBox.setFont(forceShowHiddenCheckBox.getFont().deriveFont(forceShowHiddenCheckBox.getFont().getStyle() | Font.BOLD));
			panel1.add(forceShowHiddenCheckBox, "cell 2 3");

			//---- forceFileSystemCheckBox ----
			forceFileSystemCheckBox.setText("forceFileSystem");
			panel1.add(forceFileSystemCheckBox, "cell 0 4");

			//---- noReadOnlyReturnCheckBox ----
			noReadOnlyReturnCheckBox.setText("noReadOnlyReturn");
			panel1.add(noReadOnlyReturnCheckBox, "cell 1 4");

			//---- defaultNoMiniModeCheckBox ----
			defaultNoMiniModeCheckBox.setText("defaultNoMiniMode");
			panel1.add(defaultNoMiniModeCheckBox, "cell 2 4");

			//---- allNonStorageItemsCheckBox ----
			allNonStorageItemsCheckBox.setText("allNonStorageItems");
			panel1.add(allNonStorageItemsCheckBox, "cell 0 5");

			//---- noTestFileCreateCheckBox ----
			noTestFileCreateCheckBox.setText("noTestFileCreate");
			panel1.add(noTestFileCreateCheckBox, "cell 1 5");

			//---- forcePreviewPaneonCheckBox ----
			forcePreviewPaneonCheckBox.setText("forcePreviewPaneon");
			panel1.add(forcePreviewPaneonCheckBox, "cell 2 5");

			//---- noValidateCheckBox ----
			noValidateCheckBox.setText("noValidate");
			panel1.add(noValidateCheckBox, "cell 0 6");

			//---- hideMruPlacesCheckBox ----
			hideMruPlacesCheckBox.setText("hideMruPlaces");
			panel1.add(hideMruPlacesCheckBox, "cell 1 6");

			//---- supportStreamableItemsCheckBox ----
			supportStreamableItemsCheckBox.setText("supportStreamableItems");
			panel1.add(supportStreamableItemsCheckBox, "cell 2 6");

			//---- allowMultiSelectCheckBox ----
			allowMultiSelectCheckBox.setText("allowMultiSelect");
			allowMultiSelectCheckBox.setFont(allowMultiSelectCheckBox.getFont().deriveFont(allowMultiSelectCheckBox.getFont().getStyle() | Font.BOLD));
			panel1.add(allowMultiSelectCheckBox, "cell 0 7");

			//---- hidePinnedPlacesCheckBox ----
			hidePinnedPlacesCheckBox.setText("hidePinnedPlaces");
			panel1.add(hidePinnedPlacesCheckBox, "cell 1 7");

			//======== messageDialogPanel ========
			{
				messageDialogPanel.setBorder(new TitledBorder("MessageDialog"));
				messageDialogPanel.setLayout(new MigLayout(
					"hidemode 3",
					// columns
					"[fill]" +
					"[grow,fill]",
					// rows
					"[grow,fill]" +
					"[]"));

				//---- messageLabel ----
				messageLabel.setText("Message");
				messageDialogPanel.add(messageLabel, "cell 0 0,aligny top,growy 0");

				//======== messageScrollPane ========
				{

					//---- messageField ----
					messageField.setColumns(40);
					messageField.setRows(4);
					messageScrollPane.setViewportView(messageField);
				}
				messageDialogPanel.add(messageScrollPane, "cell 1 0");

				//---- buttonsLabel ----
				buttonsLabel.setText("Buttons:");
				messageDialogPanel.add(buttonsLabel, "cell 0 1");
				messageDialogPanel.add(buttonsField, "cell 1 1");
			}
			panel1.add(messageDialogPanel, "cell 0 8 3 1,grow");
		}
		add(panel1, "cell 2 1 1 10,growy");

		//---- okButtonLabelLabel ----
		okButtonLabelLabel.setText("okButtonLabel");
		add(okButtonLabelLabel, "cell 0 2");
		add(okButtonLabelField, "cell 1 2");

		//---- fileNameLabelLabel ----
		fileNameLabelLabel.setText("fileNameLabel");
		add(fileNameLabelLabel, "cell 0 3");
		add(fileNameLabelField, "cell 1 3");

		//---- fileNameLabel ----
		fileNameLabel.setText("fileName");
		add(fileNameLabel, "cell 0 4");
		add(fileNameField, "cell 1 4");

		//---- folderLabel ----
		folderLabel.setText("folder");
		add(folderLabel, "cell 0 5");
		add(folderField, "cell 1 5");

		//---- saveAsItemLabel ----
		saveAsItemLabel.setText("saveAsItem");
		add(saveAsItemLabel, "cell 0 6");
		add(saveAsItemField, "cell 1 6");

		//---- defaultFolderLabel ----
		defaultFolderLabel.setText("defaultFolder");
		add(defaultFolderLabel, "cell 0 7");
		add(defaultFolderField, "cell 1 7");

		//---- defaultExtensionLabel ----
		defaultExtensionLabel.setText("defaultExtension");
		add(defaultExtensionLabel, "cell 0 8");
		add(defaultExtensionField, "cell 1 8");

		//---- fileTypesLabel ----
		fileTypesLabel.setText("fileTypes");
		add(fileTypesLabel, "cell 0 9");

		//---- fileTypesField ----
		fileTypesField.setEditable(true);
		fileTypesField.setModel(new DefaultComboBoxModel<>(new String[] {
			"Text Files,*.txt",
			"All Files,*.*",
			"Text Files,*.txt,PDF Files,*.pdf,All Files,*.*",
			"Text and PDF Files,*.txt;*.pdf"
		}));
		add(fileTypesField, "cell 1 9");

		//---- fileTypeIndexLabel ----
		fileTypeIndexLabel.setText("fileTypeIndex");
		add(fileTypeIndexLabel, "cell 0 10");

		//---- fileTypeIndexSlider ----
		fileTypeIndexSlider.setMaximum(10);
		fileTypeIndexSlider.setMajorTickSpacing(1);
		fileTypeIndexSlider.setValue(0);
		fileTypeIndexSlider.setPaintLabels(true);
		fileTypeIndexSlider.setSnapToTicks(true);
		add(fileTypeIndexSlider, "cell 1 10");

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
		add(hSpacer1, "cell 0 11 3 1,growx");

		//---- messageDialogButton ----
		messageDialogButton.setText("MessageDialog...");
		messageDialogButton.addActionListener(e -> messageDialog());
		add(messageDialogButton, "cell 0 11 3 1,alignx right,growx 0");

		//---- messageBoxButton ----
		messageBoxButton.setText("MessageBox...");
		messageBoxButton.addActionListener(e -> messageBox());
		add(messageBoxButton, "cell 0 11 3 1");

		//======== filesScrollPane ========
		{

			//---- filesField ----
			filesField.setRows(8);
			filesScrollPane.setViewportView(filesField);
		}
		add(filesScrollPane, "cell 0 12 3 1,growx");

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
	private FlatTriStateCheckBox overwritePromptCheckBox;
	private FlatTriStateCheckBox pathMustExistCheckBox;
	private FlatTriStateCheckBox noDereferenceLinksCheckBox;
	private FlatTriStateCheckBox strictFileTypesCheckBox;
	private FlatTriStateCheckBox fileMustExistCheckBox;
	private FlatTriStateCheckBox okButtonNeedsInteractionCheckBox;
	private FlatTriStateCheckBox noChangeDirCheckBox;
	private FlatTriStateCheckBox createPromptCheckBox;
	private FlatTriStateCheckBox dontAddToRecentCheckBox;
	private FlatTriStateCheckBox pickFoldersCheckBox;
	private FlatTriStateCheckBox shareAwareCheckBox;
	private FlatTriStateCheckBox forceShowHiddenCheckBox;
	private FlatTriStateCheckBox forceFileSystemCheckBox;
	private FlatTriStateCheckBox noReadOnlyReturnCheckBox;
	private FlatTriStateCheckBox defaultNoMiniModeCheckBox;
	private FlatTriStateCheckBox allNonStorageItemsCheckBox;
	private FlatTriStateCheckBox noTestFileCreateCheckBox;
	private FlatTriStateCheckBox forcePreviewPaneonCheckBox;
	private FlatTriStateCheckBox noValidateCheckBox;
	private FlatTriStateCheckBox hideMruPlacesCheckBox;
	private FlatTriStateCheckBox supportStreamableItemsCheckBox;
	private FlatTriStateCheckBox allowMultiSelectCheckBox;
	private FlatTriStateCheckBox hidePinnedPlacesCheckBox;
	private JTextArea messageField;
	private JTextField buttonsField;
	private JTextField okButtonLabelField;
	private JTextField fileNameLabelField;
	private JTextField fileNameField;
	private JTextField folderField;
	private JTextField saveAsItemField;
	private JTextField defaultFolderField;
	private JTextField defaultExtensionField;
	private JComboBox<String> fileTypesField;
	private JSlider fileTypeIndexSlider;
	private JCheckBox showMessageDialogOnOKCheckBox;
	private JTextArea filesField;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
