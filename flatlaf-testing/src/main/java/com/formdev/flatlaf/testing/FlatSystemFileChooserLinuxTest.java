/*
 * Copyright 2025 FormDev Software GmbH
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

import static com.formdev.flatlaf.ui.FlatNativeLinuxLibrary.*;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox.State;
import com.formdev.flatlaf.ui.FlatNativeLinuxLibrary;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatSystemFileChooserLinuxTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			if( !FlatNativeLinuxLibrary.isLoaded() ) {
				JOptionPane.showMessageDialog( null, "FlatLaf native library not loaded" );
				return;
			}

			FlatTestFrame frame = FlatTestFrame.create( args, "FlatSystemFileChooserLinuxTest" );
			addListeners( frame );
			frame.showFrame( FlatSystemFileChooserLinuxTest::new );
		} );
	}

	FlatSystemFileChooserLinuxTest() {
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
		else if( ownerDialogRadioButton.isSelected() ) {
			JDialog dialog = new JDialog( frame, "Dummy Modal Dialog", Dialog.DEFAULT_MODALITY_TYPE );
			dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
			dialog.addWindowListener( new WindowAdapter() {
				@Override
				public void windowOpened( WindowEvent e ) {
					openOrSave( open, direct, dialog );
				}
			} );
			dialog.setSize( 1200, 1000 );
			dialog.setLocationRelativeTo( this );
			dialog.setVisible( true );
		} else
			openOrSave( open, direct, null );
	}

	private void openOrSave( boolean open, boolean direct, Window owner ) {
		String title = n( titleField.getText() );
		String okButtonLabel = n( okButtonLabelField.getText() );
		String currentName = n( currentNameField.getText() );
		String currentFolder = n( currentFolderField.getText() );
		AtomicInteger optionsSet = new AtomicInteger();
		AtomicInteger optionsClear = new AtomicInteger();

		o( FC_select_folder, select_folderCheckBox, optionsSet, optionsClear );
		o( FC_select_multiple, select_multipleCheckBox, optionsSet, optionsClear );
		o( FC_show_hidden, show_hiddenCheckBox, optionsSet, optionsClear );
		o( FC_local_only, local_onlyCheckBox, optionsSet, optionsClear );
		o( FC_do_overwrite_confirmation, do_overwrite_confirmationCheckBox, optionsSet, optionsClear );
		o( FC_create_folders, create_foldersCheckBox, optionsSet, optionsClear );

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

		if( direct ) {
			String[] files = FlatNativeLinuxLibrary.showFileChooser( owner, open,
				title, okButtonLabel, currentName, currentFolder,
				optionsSet.get(), optionsClear.get(), fileTypeIndex, fileTypes );

			filesField.setText( (files != null) ? Arrays.toString( files ).replace( ',', '\n' ) : "null" );
		} else {
			SecondaryLoop secondaryLoop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();

			String[] fileTypes2 = fileTypes;
			new Thread( () -> {
				String[] files = FlatNativeLinuxLibrary.showFileChooser( owner, open,
					title, okButtonLabel, currentName, currentFolder,
					optionsSet.get(), optionsClear.get(), fileTypeIndex, fileTypes2 );

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
		ownerLabel = new JLabel();
		ownerFrameRadioButton = new JRadioButton();
		ownerDialogRadioButton = new JRadioButton();
		ownerNullRadioButton = new JRadioButton();
		ownerSpacer = new JPanel(null);
		titleLabel = new JLabel();
		titleField = new JTextField();
		panel1 = new JPanel();
		select_folderCheckBox = new FlatTriStateCheckBox();
		select_multipleCheckBox = new FlatTriStateCheckBox();
		do_overwrite_confirmationCheckBox = new FlatTriStateCheckBox();
		create_foldersCheckBox = new FlatTriStateCheckBox();
		show_hiddenCheckBox = new FlatTriStateCheckBox();
		local_onlyCheckBox = new FlatTriStateCheckBox();
		okButtonLabelLabel = new JLabel();
		okButtonLabelField = new JTextField();
		currentNameLabel = new JLabel();
		currentNameField = new JTextField();
		currentFolderLabel = new JLabel();
		currentFolderField = new JTextField();
		fileTypesLabel = new JLabel();
		fileTypesField = new JComboBox<>();
		fileTypeIndexLabel = new JLabel();
		fileTypeIndexSlider = new JSlider();
		openButton = new JButton();
		saveButton = new JButton();
		openDirectButton = new JButton();
		saveDirectButton = new JButton();
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
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]"));

			//---- select_folderCheckBox ----
			select_folderCheckBox.setText("select_folder");
			select_folderCheckBox.setAllowIndeterminate(false);
			select_folderCheckBox.setState(FlatTriStateCheckBox.State.UNSELECTED);
			panel1.add(select_folderCheckBox, "cell 0 0");

			//---- select_multipleCheckBox ----
			select_multipleCheckBox.setText("select_multiple");
			select_multipleCheckBox.setState(FlatTriStateCheckBox.State.UNSELECTED);
			select_multipleCheckBox.setAllowIndeterminate(false);
			panel1.add(select_multipleCheckBox, "cell 0 1");

			//---- do_overwrite_confirmationCheckBox ----
			do_overwrite_confirmationCheckBox.setText("do_overwrite_confirmation");
			panel1.add(do_overwrite_confirmationCheckBox, "cell 0 2");

			//---- create_foldersCheckBox ----
			create_foldersCheckBox.setText("create_folders");
			panel1.add(create_foldersCheckBox, "cell 0 3");

			//---- show_hiddenCheckBox ----
			show_hiddenCheckBox.setText("show_hidden");
			panel1.add(show_hiddenCheckBox, "cell 0 4");

			//---- local_onlyCheckBox ----
			local_onlyCheckBox.setText("local_only");
			panel1.add(local_onlyCheckBox, "cell 0 5");
		}
		add(panel1, "cell 2 1 1 6,aligny top,growy 0");

		//---- okButtonLabelLabel ----
		okButtonLabelLabel.setText("okButtonLabel");
		add(okButtonLabelLabel, "cell 0 2");
		add(okButtonLabelField, "cell 1 2");

		//---- currentNameLabel ----
		currentNameLabel.setText("currentName");
		add(currentNameLabel, "cell 0 3");
		add(currentNameField, "cell 1 3");

		//---- currentFolderLabel ----
		currentFolderLabel.setText("currentFolder");
		add(currentFolderLabel, "cell 0 4");
		add(currentFolderField, "cell 1 4");

		//---- fileTypesLabel ----
		fileTypesLabel.setText("fileTypes");
		add(fileTypesLabel, "cell 0 5");

		//---- fileTypesField ----
		fileTypesField.setEditable(true);
		fileTypesField.setModel(new DefaultComboBoxModel<>(new String[] {
			"Text Files,*.txt,null",
			"All Files,*,null",
			"Text Files,*.txt,null,PDF Files,*.pdf,null,All Files,*,null",
			"Text and PDF Files,*.txt,*.pdf,null"
		}));
		add(fileTypesField, "cell 1 5");

		//---- fileTypeIndexLabel ----
		fileTypeIndexLabel.setText("fileTypeIndex");
		add(fileTypeIndexLabel, "cell 0 6");

		//---- fileTypeIndexSlider ----
		fileTypeIndexSlider.setMaximum(10);
		fileTypeIndexSlider.setMajorTickSpacing(1);
		fileTypeIndexSlider.setValue(0);
		fileTypeIndexSlider.setPaintLabels(true);
		fileTypeIndexSlider.setSnapToTicks(true);
		add(fileTypeIndexSlider, "cell 1 6");

		//---- openButton ----
		openButton.setText("Open...");
		openButton.addActionListener(e -> open());
		add(openButton, "cell 0 7 3 1");

		//---- saveButton ----
		saveButton.setText("Save...");
		saveButton.addActionListener(e -> save());
		add(saveButton, "cell 0 7 3 1");

		//---- openDirectButton ----
		openDirectButton.setText("Open (no-thread)...");
		openDirectButton.addActionListener(e -> openDirect());
		add(openDirectButton, "cell 0 7 3 1");

		//---- saveDirectButton ----
		saveDirectButton.setText("Save (no-thread)...");
		saveDirectButton.addActionListener(e -> saveDirect());
		add(saveDirectButton, "cell 0 7 3 1");

		//======== filesScrollPane ========
		{

			//---- filesField ----
			filesField.setRows(8);
			filesScrollPane.setViewportView(filesField);
		}
		add(filesScrollPane, "cell 0 8 3 1,growx");

		//---- ownerButtonGroup ----
		ButtonGroup ownerButtonGroup = new ButtonGroup();
		ownerButtonGroup.add(ownerFrameRadioButton);
		ownerButtonGroup.add(ownerDialogRadioButton);
		ownerButtonGroup.add(ownerNullRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel ownerLabel;
	private JRadioButton ownerFrameRadioButton;
	private JRadioButton ownerDialogRadioButton;
	private JRadioButton ownerNullRadioButton;
	private JPanel ownerSpacer;
	private JLabel titleLabel;
	private JTextField titleField;
	private JPanel panel1;
	private FlatTriStateCheckBox select_folderCheckBox;
	private FlatTriStateCheckBox select_multipleCheckBox;
	private FlatTriStateCheckBox do_overwrite_confirmationCheckBox;
	private FlatTriStateCheckBox create_foldersCheckBox;
	private FlatTriStateCheckBox show_hiddenCheckBox;
	private FlatTriStateCheckBox local_onlyCheckBox;
	private JLabel okButtonLabelLabel;
	private JTextField okButtonLabelField;
	private JLabel currentNameLabel;
	private JTextField currentNameField;
	private JLabel currentFolderLabel;
	private JTextField currentFolderField;
	private JLabel fileTypesLabel;
	private JComboBox<String> fileTypesField;
	private JLabel fileTypeIndexLabel;
	private JSlider fileTypeIndexSlider;
	private JButton openButton;
	private JButton saveButton;
	private JButton openDirectButton;
	private JButton saveDirectButton;
	private JScrollPane filesScrollPane;
	private JTextArea filesField;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
