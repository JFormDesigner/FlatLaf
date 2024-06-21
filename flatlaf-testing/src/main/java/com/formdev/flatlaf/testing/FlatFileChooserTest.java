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

package com.formdev.flatlaf.testing;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatFileChooserHomeFolderIcon;
import com.formdev.flatlaf.ui.JavaCompatibility2;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatFileChooserTest
	extends FlatTestPanel
{
	private static ShortcutsCount shortcutsCount = ShortcutsCount.home;

	public static void main( String[] args ) {
//		Locale.setDefault( Locale.FRENCH );
//		Locale.setDefault( Locale.GERMAN );
//		Locale.setDefault( Locale.ITALIAN );
//		Locale.setDefault( Locale.JAPANESE );
//		Locale.setDefault( Locale.SIMPLIFIED_CHINESE );
//		Locale.setDefault( Locale.TRADITIONAL_CHINESE );

		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatFileChooserTest" );

			UIManager.put( "FileChooser.shortcuts.filesFunction", (Function<File[], File[]>) files -> {
				if( shortcutsCount == null )
					return files;
				if( shortcutsCount == ShortcutsCount.empty )
					return new File[0];

				ArrayList<File> list = new ArrayList<>( Arrays.asList( files ) );
				if( shortcutsCount == ShortcutsCount.home )
					list.add( 0, new File( System.getProperty( "user.home" ) ) );
				else {
					File home = new File( System.getProperty( "user.home" ) );
					File[] homeFiles = home.listFiles();
					int count = shortcutsCount.value;
					for( int i = 0; i < count; i++ )
						list.add( i < homeFiles.length ? homeFiles[i] : new File( "Dummy " + i ) );
				}
				return list.toArray( new File[list.size()] );
			} );

			UIManager.put( "FileChooser.shortcuts.displayNameFunction", (Function<File, String>) file -> {
				if( file.getAbsolutePath().equals( System.getProperty( "user.home" ) ) )
					return "Home";
				return null;
			} );
			UIManager.put( "FileChooser.shortcuts.iconFunction", (Function<File, Icon>) file -> {
				if( file.getAbsolutePath().equals( System.getProperty( "user.home" ) ) )
					return new FlatFileChooserHomeFolderIcon();
				return null;
			} );

			frame.showFrame( FlatFileChooserTest::new );
		} );
	}

	FlatFileChooserTest() {
		initComponents();

		dialogTypeField.init( DialogType.class, false );
		fileSelectionModeField.init( FileSelectionMode.class, false );
		localesField.init( Locales.class, false );
		shortcutsCountField.init( ShortcutsCount.class, true );
		shortcutsCountField.setSelectedValue( shortcutsCount );

		showControlButtonsCheckBox.setSelected( fileChooser1.getControlButtonsAreShown() );
		multiSelectionCheckBox.setSelected( fileChooser1.isMultiSelectionEnabled() );
		fileHidingCheckBox.setSelected( fileChooser1.isFileHidingEnabled() );
		dragCheckBox.setSelected( fileChooser1.getDragEnabled() );
		filterAllFilesCheckBox.setSelected( fileChooser1.isAcceptAllFileFilterUsed() );

		updateOutput();
	}

	private void fileChooser1PropertyChange( PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case JFileChooser.DIRECTORY_CHANGED_PROPERTY:
			case JFileChooser.SELECTED_FILE_CHANGED_PROPERTY:
			case JFileChooser.SELECTED_FILES_CHANGED_PROPERTY:
				updateOutput();
				break;
		}
	}

	private void updateOutput() {
		currentDirectoryField.setText( String.valueOf( fileChooser1.getCurrentDirectory() ) );
		selectedFileField.setText( String.valueOf( fileChooser1.getSelectedFile() ) );
		selectedFilesField.setText( Arrays.toString( fileChooser1.getSelectedFiles() ) );
	}

	private void dialogTypeChanged() {
		DialogType value = dialogTypeField.getSelectedValue();
		int dialogType = (value != null) ? value.value : JFileChooser.OPEN_DIALOG;

		if( dialogType == JFileChooser.CUSTOM_DIALOG )
			fileChooser1.setApproveButtonText( "Custom" );
		fileChooser1.setDialogType( dialogType );
	}

	private void fileSelectionModeChanged() {
		FileSelectionMode value = fileSelectionModeField.getSelectedValue();
		int mode = (value != null) ? value.value : JFileChooser.FILES_ONLY;

		fileChooser1.setFileSelectionMode( mode );
	}

	private void showControlButtons() {
		fileChooser1.setControlButtonsAreShown( showControlButtonsCheckBox.isSelected() );
		fileChooser1.revalidate();
		fileChooser1.repaint();
	}

	private void showShortcuts() {
		UIManager.put( "FileChooser.noPlacesBar", !showShortcutsCheckBox.isSelected() ? true : null );
		fileChooser1.updateUI();
	}

	private void showAccessory() {
		JPanel accessory = null;
		if( showAccessoryCheckBox.isSelected() ) {
			accessory = new JPanel( new BorderLayout() );
			accessory.setBackground( Color.green );
			accessory.add( new JLabel( "  Accessory  " ), BorderLayout.CENTER );
		}
		fileChooser1.setAccessory( accessory );
		fileChooser1.revalidate();
		fileChooser1.repaint();
	}

	private void multiSelection() {
		fileChooser1.setMultiSelectionEnabled( multiSelectionCheckBox.isSelected() );
	}

	private void fileHiding() {
		fileChooser1.setFileHidingEnabled( fileHidingCheckBox.isSelected() );
	}

	private void drag() {
		fileChooser1.setDragEnabled( dragCheckBox.isSelected() );
	}

	private final FileFilter TEXT_FILTER = new FileNameExtensionFilter( "Text Files", "txt", "md" );
	private final FileFilter IMAGES_FILTER = new FileNameExtensionFilter( "Images", "png", "git", "jpg", "jpeg" );
	private final FileFilter LONG_DESC_FILTER = new FileNameExtensionFilter( "Some long description (.abc, .def, .ghi, .jkl)", "dummy" );
	private final FileFilter EXTRA_LONG_DESC_FILTER = new FileNameExtensionFilter( "Some super extra long description (.abc, .def, .ghi, .jkl, .mno, .pqr, .stu)", "dummy" );

	private void filterChanged() {
		boolean all = filterAllFilesCheckBox.isSelected();
		if( all != fileChooser1.isAcceptAllFileFilterUsed() )
			fileChooser1.setAcceptAllFileFilterUsed( all );

		addRemoveFilter( filterTextFilesCheckBox.isSelected(), TEXT_FILTER );
		addRemoveFilter( filterImagesCheckBox.isSelected(), IMAGES_FILTER );
		addRemoveFilter( filterLongDescCheckBox.isSelected(), LONG_DESC_FILTER );
		addRemoveFilter( filterExtraLongDescCheckBox.isSelected(), EXTRA_LONG_DESC_FILTER );
	}

	private void addRemoveFilter( boolean add, FileFilter filter ) {
		if( add )
			fileChooser1.addChoosableFileFilter( filter );
		else
			fileChooser1.removeChoosableFileFilter( filter );
	}

	private void localesChanged() {
		Locales value = localesField.getSelectedValue();
		Locale locale = (value != null) ? value.value : Locale.ENGLISH;

		SwingUtilities.invokeLater( () -> {
			Locale.setDefault( locale );
			JComponent.setDefaultLocale( locale );
			fileChooser1.setLocale( locale );
			ResourceBundle.clearCache();

			try {
				UIManager.setLookAndFeel( UIManager.getLookAndFeel().getClass().getName() );
				FlatLaf.updateUI();
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		} );
	}

	private void shortcutsCountChanged() {
		shortcutsCount = shortcutsCountField.getSelectedValue();
		fileChooser1.updateUI();
	}

	private void printShortcutFiles() {
		printFiles( JavaCompatibility2.getChooserShortcutPanelFiles( fileChooser1.getFileSystemView() ) );
	}

	private void printComboBoxFiles() {
		printFiles( JavaCompatibility2.getChooserComboBoxFiles( fileChooser1.getFileSystemView() ) );
	}

	private void printRoots() {
		FileSystemView fsv = fileChooser1.getFileSystemView();
		File[] roots = fsv.getRoots();
		printFiles( roots );

		for( File root : roots )
			printFiles( fsv.getFiles( root, true ) );
	}

	private void printFiles( File[] files ) {
		System.out.println( "--------------------------------" );
		FileSystemView fsv = fileChooser1.getFileSystemView();
		for( File file : files ) {
			System.out.printf( "%-30s   ", file );
			System.out.println(
				(fsv.isComputerNode( file ) ? "computer " : "") +
				(fsv.isDrive( file ) ? "drive " : "") +
				(fsv.isFileSystem( file ) ? "fileSystem " : "") +
				(fsv.isFileSystemRoot( file ) ? "fileSystemRoot " : "") +
				(fsv.isFloppyDrive( file ) ? "floppyDrive " : "") +
				(fsv.isHiddenFile( file ) ? "hiddenFile " : "") +
				(fsv.isRoot( file ) ? "root " : "") +
				(fsv.isTraversable( file ) ? "traversable " : "") );
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel fileChooserLabel = new JLabel();
		JPanel panel1 = new JPanel();
		fileChooser1 = new JFileChooser();
		JLabel currentDirectoryLabel = new JLabel();
		currentDirectoryField = new JTextField();
		JLabel selectedFileLabel = new JLabel();
		selectedFileField = new JTextField();
		JLabel selectedFilesLabel = new JLabel();
		selectedFilesField = new JTextField();
		JLabel dialogTypeLabel = new JLabel();
		dialogTypeField = new FlatTestEnumSelector<>();
		showControlButtonsCheckBox = new JCheckBox();
		showShortcutsCheckBox = new JCheckBox();
		showAccessoryCheckBox = new JCheckBox();
		JLabel fileSelectionModeLabel = new JLabel();
		fileSelectionModeField = new FlatTestEnumSelector<>();
		multiSelectionCheckBox = new JCheckBox();
		fileHidingCheckBox = new JCheckBox();
		dragCheckBox = new JCheckBox();
		JLabel filtersLabel = new JLabel();
		filterAllFilesCheckBox = new JCheckBox();
		filterTextFilesCheckBox = new JCheckBox();
		filterImagesCheckBox = new JCheckBox();
		filterLongDescCheckBox = new JCheckBox();
		filterExtraLongDescCheckBox = new JCheckBox();
		printShortcutFilesButton = new JButton();
		printComboBoxFilesButton = new JButton();
		printRootsButton = new JButton();
		JLabel localesLabel = new JLabel();
		localesField = new FlatTestEnumSelector<>();
		JLabel label12 = new JLabel();
		shortcutsCountField = new FlatTestEnumSelector<>();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JLabel label3 = new JLabel();
		JLabel label4 = new JLabel();
		JLabel label5 = new JLabel();
		JLabel label6 = new JLabel();
		JLabel label7 = new JLabel();
		JLabel label8 = new JLabel();
		JLabel label9 = new JLabel();
		JLabel label10 = new JLabel();
		JLabel label11 = new JLabel();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[]" +
			"[grow]",
			// rows
			"[grow,fill]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]para" +
			"[]"));

		//---- fileChooserLabel ----
		fileChooserLabel.setText("JFileChooser:");
		add(fileChooserLabel, "cell 0 0,aligny top,growy 0");

		//======== panel1 ========
		{
			panel1.setBorder(new MatteBorder(4, 4, 4, 4, Color.red));
			panel1.setLayout(new BorderLayout());

			//---- fileChooser1 ----
			fileChooser1.addPropertyChangeListener(e -> fileChooser1PropertyChange(e));
			panel1.add(fileChooser1, BorderLayout.CENTER);
		}
		add(panel1, "cell 1 0 2 1,growx");

		//---- currentDirectoryLabel ----
		currentDirectoryLabel.setText("Current Directory:");
		add(currentDirectoryLabel, "cell 0 1");

		//---- currentDirectoryField ----
		currentDirectoryField.setEditable(false);
		add(currentDirectoryField, "cell 1 1 2 1,growx");

		//---- selectedFileLabel ----
		selectedFileLabel.setText("Selected File:");
		add(selectedFileLabel, "cell 0 2");

		//---- selectedFileField ----
		selectedFileField.setEditable(false);
		add(selectedFileField, "cell 1 2 2 1,growx");

		//---- selectedFilesLabel ----
		selectedFilesLabel.setText("Selected Files:");
		add(selectedFilesLabel, "cell 0 3");

		//---- selectedFilesField ----
		selectedFilesField.setEditable(false);
		add(selectedFilesField, "cell 1 3 2 1,growx");

		//---- dialogTypeLabel ----
		dialogTypeLabel.setText("Dialog Type:");
		add(dialogTypeLabel, "cell 0 4");

		//---- dialogTypeField ----
		dialogTypeField.addActionListener(e -> dialogTypeChanged());
		add(dialogTypeField, "cell 1 4");

		//---- showControlButtonsCheckBox ----
		showControlButtonsCheckBox.setText("Show Control Buttons");
		showControlButtonsCheckBox.addActionListener(e -> showControlButtons());
		add(showControlButtonsCheckBox, "cell 2 4");

		//---- showShortcutsCheckBox ----
		showShortcutsCheckBox.setText("Show Shortcuts");
		showShortcutsCheckBox.setSelected(true);
		showShortcutsCheckBox.addActionListener(e -> showShortcuts());
		add(showShortcutsCheckBox, "cell 2 4");

		//---- showAccessoryCheckBox ----
		showAccessoryCheckBox.setText("Show Accessory");
		showAccessoryCheckBox.addActionListener(e -> showAccessory());
		add(showAccessoryCheckBox, "cell 2 4");

		//---- fileSelectionModeLabel ----
		fileSelectionModeLabel.setText("File Selection Mode:");
		add(fileSelectionModeLabel, "cell 0 5");

		//---- fileSelectionModeField ----
		fileSelectionModeField.addActionListener(e -> fileSelectionModeChanged());
		add(fileSelectionModeField, "cell 1 5");

		//---- multiSelectionCheckBox ----
		multiSelectionCheckBox.setText("Multi Selection");
		multiSelectionCheckBox.addActionListener(e -> multiSelection());
		add(multiSelectionCheckBox, "cell 2 5");

		//---- fileHidingCheckBox ----
		fileHidingCheckBox.setText("File Hiding");
		fileHidingCheckBox.addActionListener(e -> fileHiding());
		add(fileHidingCheckBox, "cell 2 5");

		//---- dragCheckBox ----
		dragCheckBox.setText("Drag");
		dragCheckBox.addActionListener(e -> drag());
		add(dragCheckBox, "cell 2 5");

		//---- filtersLabel ----
		filtersLabel.setText("Filters:");
		add(filtersLabel, "cell 0 6");

		//---- filterAllFilesCheckBox ----
		filterAllFilesCheckBox.setText("All Files");
		filterAllFilesCheckBox.addActionListener(e -> filterChanged());
		add(filterAllFilesCheckBox, "cell 1 6 2 1");

		//---- filterTextFilesCheckBox ----
		filterTextFilesCheckBox.setText("Text Files");
		filterTextFilesCheckBox.addActionListener(e -> filterChanged());
		add(filterTextFilesCheckBox, "cell 1 6 2 1");

		//---- filterImagesCheckBox ----
		filterImagesCheckBox.setText("Images");
		filterImagesCheckBox.addActionListener(e -> filterChanged());
		add(filterImagesCheckBox, "cell 1 6 2 1");

		//---- filterLongDescCheckBox ----
		filterLongDescCheckBox.setText("Long description");
		filterLongDescCheckBox.addActionListener(e -> filterChanged());
		add(filterLongDescCheckBox, "cell 1 6 2 1");

		//---- filterExtraLongDescCheckBox ----
		filterExtraLongDescCheckBox.setText("Extra Long description");
		filterExtraLongDescCheckBox.addActionListener(e -> filterChanged());
		add(filterExtraLongDescCheckBox, "cell 1 6 2 1");

		//---- printShortcutFilesButton ----
		printShortcutFilesButton.setText("Print Shortcut Files");
		printShortcutFilesButton.addActionListener(e -> printShortcutFiles());
		add(printShortcutFilesButton, "cell 1 7 2 1");

		//---- printComboBoxFilesButton ----
		printComboBoxFilesButton.setText("Print ComboBox Files");
		printComboBoxFilesButton.addActionListener(e -> printComboBoxFiles());
		add(printComboBoxFilesButton, "cell 1 7 2 1");

		//---- printRootsButton ----
		printRootsButton.setText("Print Roots");
		printRootsButton.addActionListener(e -> printRoots());
		add(printRootsButton, "cell 1 7 2 1");

		//---- localesLabel ----
		localesLabel.setText("Locales:");
		add(localesLabel, "cell 0 8");

		//---- localesField ----
		localesField.addActionListener(e -> localesChanged());
		add(localesField, "cell 1 8 2 1");

		//---- label12 ----
		label12.setText("Shortcuts:");
		add(label12, "cell 0 9");

		//---- shortcutsCountField ----
		shortcutsCountField.addActionListener(e -> shortcutsCountChanged());
		add(shortcutsCountField, "cell 1 9 2 1");

		//---- label1 ----
		label1.setText("icons:");
		add(label1, "cell 0 10");

		//---- label2 ----
		label2.setIcon(UIManager.getIcon("FileView.directoryIcon"));
		add(label2, "cell 1 10 2 1");

		//---- label3 ----
		label3.setIcon(UIManager.getIcon("FileView.fileIcon"));
		add(label3, "cell 1 10 2 1");

		//---- label4 ----
		label4.setIcon(UIManager.getIcon("FileView.computerIcon"));
		add(label4, "cell 1 10 2 1");

		//---- label5 ----
		label5.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
		add(label5, "cell 1 10 2 1");

		//---- label6 ----
		label6.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
		add(label6, "cell 1 10 2 1");

		//---- label7 ----
		label7.setIcon(UIManager.getIcon("FileChooser.newFolderIcon"));
		add(label7, "cell 1 10 2 1");

		//---- label8 ----
		label8.setIcon(UIManager.getIcon("FileChooser.upFolderIcon"));
		add(label8, "cell 1 10 2 1");

		//---- label9 ----
		label9.setIcon(UIManager.getIcon("FileChooser.homeFolderIcon"));
		add(label9, "cell 1 10 2 1");

		//---- label10 ----
		label10.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
		add(label10, "cell 1 10 2 1");

		//---- label11 ----
		label11.setIcon(UIManager.getIcon("FileChooser.listViewIcon"));
		add(label11, "cell 1 10 2 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JFileChooser fileChooser1;
	private JTextField currentDirectoryField;
	private JTextField selectedFileField;
	private JTextField selectedFilesField;
	private FlatTestEnumSelector<DialogType> dialogTypeField;
	private JCheckBox showControlButtonsCheckBox;
	private JCheckBox showShortcutsCheckBox;
	private JCheckBox showAccessoryCheckBox;
	private FlatTestEnumSelector<FileSelectionMode> fileSelectionModeField;
	private JCheckBox multiSelectionCheckBox;
	private JCheckBox fileHidingCheckBox;
	private JCheckBox dragCheckBox;
	private JCheckBox filterAllFilesCheckBox;
	private JCheckBox filterTextFilesCheckBox;
	private JCheckBox filterImagesCheckBox;
	private JCheckBox filterLongDescCheckBox;
	private JCheckBox filterExtraLongDescCheckBox;
	private JButton printShortcutFilesButton;
	private JButton printComboBoxFilesButton;
	private JButton printRootsButton;
	private FlatTestEnumSelector<Locales> localesField;
	private FlatTestEnumSelector<ShortcutsCount> shortcutsCountField;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- enum DialogType ----------------------------------------------------

	enum DialogType {
		open( JFileChooser.OPEN_DIALOG ),
		save( JFileChooser.SAVE_DIALOG ),
		custom( JFileChooser.CUSTOM_DIALOG );

		public final int value;

		DialogType( int value ) {
			this.value = value;
		}
	}

	//---- enum FileSelectionMode ---------------------------------------------

	enum FileSelectionMode {
		files_only( JFileChooser.FILES_ONLY ),
		directories_only( JFileChooser.DIRECTORIES_ONLY ),
		files_and_directories( JFileChooser.FILES_AND_DIRECTORIES );

		public final int value;

		FileSelectionMode( int value ) {
			this.value = value;
		}
	}

	//---- enum Locales -------------------------------------------------------

	// locales supported by Swing
	// (see https://github.com/openjdk/jdk/tree/master/src/java.desktop/share/classes/com/sun/swing/internal/plaf/metal/resources)
	enum Locales {
		english( Locale.ENGLISH ),
		german( Locale.GERMAN ),
		spanish( new Locale( "es" ) ),
		french( Locale.FRENCH ),
		italian( Locale.ITALIAN ),
		japanese( Locale.JAPANESE ),
		korean( Locale.KOREAN ),
		brazilian_portuguese( new Locale( "pt", "BR" ) ),
		swedish( new Locale( "sv" ) ),
		simplified_chinese( Locale.SIMPLIFIED_CHINESE ),
		traditional_chinese( Locale.TRADITIONAL_CHINESE );

		public final Locale value;

		Locales( Locale value ) {
			this.value = value;
		}
	}

	//---- enum ShortcutsCount ------------------------------------------------

	enum ShortcutsCount {
		empty( -1 ),
		home( -2 ),
		zero( 0 ),
		one( 1 ),
		two( 2 ),
		three( 3 ),
		four( 4 ),
		five( 5 ),
		ten( 10 ),
		twenty( 20 ),
		thirty( 30 );

		public final int value;

		ShortcutsCount( int value ) {
			this.value = value;
		}
	}
}
