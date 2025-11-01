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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.demo.DemoPrefs;
import com.formdev.flatlaf.util.SystemFileChooser;
import com.formdev.flatlaf.util.SystemInfo;
import li.flor.nativejfilechooser.NativeJFileChooser;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatSystemFileChooserTest
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
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatSystemFileChooserTest" );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); // necessary because of JavaFX
			addListeners( frame );
			frame.showFrame( FlatSystemFileChooserTest::new );
			frame.setJMenuBar( menuBar1 );
		} );
	}

	FlatSystemFileChooserTest() {
		initComponents();

		if( !NativeJFileChooser.FX_AVAILABLE ) {
			javafxOpenButton.setEnabled( false );
			javafxSaveButton.setEnabled( false );
		}

		Preferences state = DemoPrefs.getState();
		currentDirField.setText( state.get( "systemfilechooser.currentdir", "" ) );
		selectedFileField.setText( state.get( "systemfilechooser.selectedfile", "" ) );
		selectedFilesField.setText( state.get( "systemfilechooser.selectedfiles", "" ) );
		currentDirCheckBox.setSelected( state.getBoolean( "systemfilechooser.currentdir.enabled", false ) );
		selectedFileCheckBox.setSelected( state.getBoolean( "systemfilechooser.selectedfile.enabled", false ) );
		selectedFilesCheckBox.setSelected( state.getBoolean( "systemfilechooser.selectedfiles.enabled", false ) );
		persistStateCheckBox.setSelected( state.getBoolean( "systemfilechooser.persistState.enabled", false ) );

		currentDirChanged();
		selectedFileChanged();
		selectedFilesChanged();
		persistStateChanged();
	}

	private void persistStateChanged() {
		boolean b = persistStateCheckBox.isSelected();

		SystemFileChooser.setStateStore( b
			? new SystemFileChooser.StateStore() {
				private static final String KEY_PREFIX = "fileChooser.";

				@Override
				public String get( String key, String def ) {
					String value = DemoPrefs.getState().get( KEY_PREFIX + key, def );
					System.out.println( "GET " + key + " = " + value );
					return value;
				}

				@Override
				public void put( String key, String value ) {
					System.out.println( "PUT " + key + " = " + value );
					if( value != null )
						DemoPrefs.getState().put( KEY_PREFIX + key, value );
					else
						DemoPrefs.getState().remove( KEY_PREFIX + key );
				}
			} : null );

		DemoPrefs.getState().putBoolean( "systemfilechooser.persistState.enabled", b );
	}

	private void open() {
		SystemFileChooser fc = new SystemFileChooser();
		configureSystemFileChooser( fc );
		showWithOwner( owner -> {
			int result = fc.showOpenDialog( owner );
			outputSystemFileChooser( fc, result );
		} );
	}

	private void save() {
		SystemFileChooser fc = new SystemFileChooser();
		configureSystemFileChooser( fc );
		showWithOwner( owner -> {
			int result = fc.showSaveDialog( owner );
			outputSystemFileChooser( fc, result );
		} );
	}

	private void swingOpen() {
		JFileChooser fc = new JFileChooser();
		configureSwingFileChooser( fc );
		showWithOwner( owner -> {
			int result = fc.showOpenDialog( owner );
			outputSwingFileChooser( "Swing", fc, result );
		} );
	}

	private void swingSave() {
		JFileChooser fc = new JFileChooser();
		configureSwingFileChooser( fc );
		showWithOwner( owner -> {
			int result = fc.showSaveDialog( owner );
			outputSwingFileChooser( "Swing", fc, result );
		} );
	}

	private void awtOpen() {
		showWithOwner( owner -> {
			FileDialog fc = (owner instanceof Frame)
				? new FileDialog( (Frame) owner )
				: new FileDialog( (Dialog) owner );
			configureAWTFileChooser( fc, true );
			fc.setVisible( true );
			outputAWTFileChooser( fc );
		} );
	}

	private void awtSave() {
		showWithOwner( owner -> {
			FileDialog fc = (owner instanceof Frame)
				? new FileDialog( (Frame) owner )
				: new FileDialog( (Dialog) owner );
			configureAWTFileChooser( fc, false );
			fc.setVisible( true );
			outputAWTFileChooser( fc );
		} );
	}

	private void javafxOpen() {
		JFileChooser fc = new NativeJFileChooser();
		configureSwingFileChooser( fc );
		showWithOwner( owner -> {
			int result = fc.showOpenDialog( owner );
			outputSwingFileChooser( "JavaFX", fc, result );
		} );
	}

	private void javafxSave() {
		JFileChooser fc = new NativeJFileChooser();
		configureSwingFileChooser( fc );
		showWithOwner( owner -> {
			int result = fc.showSaveDialog( owner );
			outputSwingFileChooser( "JavaFX", fc, result );
		} );
	}

	private void configureSystemFileChooser( SystemFileChooser fc ) {
		fc.setDialogTitle( n( dialogTitleField.getText() ) );
		fc.setApproveButtonText( n( approveButtonTextField.getText() ) );
		fc.setApproveButtonMnemonic( mnemonic( approveButtonMnemonicField.getText() ) );

		// paths
		if( currentDirCheckBox.isSelected() )
			fc.setCurrentDirectory( toFile( currentDirField.getText() ) );
		if( selectedFileCheckBox.isSelected() )
			fc.setSelectedFile( toFile( selectedFileField.getText() ) );
		if( selectedFilesCheckBox.isSelected() )
			fc.setSelectedFiles( toFiles( selectedFilesField.getText() ) );

		// options
		if( directorySelectionCheckBox.isSelected() )
			fc.setFileSelectionMode( SystemFileChooser.DIRECTORIES_ONLY );
		fc.setMultiSelectionEnabled( multiSelectionEnabledCheckBox.isSelected() );
		fc.setFileHidingEnabled( useFileHidingCheckBox.isSelected() );
		if( useSystemFileChooserCheckBox.isSelected() )
			System.clearProperty( FlatSystemProperties.USE_SYSTEM_FILE_CHOOSER );
		else
			System.setProperty( FlatSystemProperties.USE_SYSTEM_FILE_CHOOSER, "false" );

		// filter
		String fileTypesStr = n( (String) fileTypesField.getSelectedItem() );
		String[] fileTypes = {};
		if( fileTypesStr != null )
			fileTypes = fileTypesStr.trim().split( "[,]+" );
		int fileTypeIndex = fileTypeIndexSlider.getValue();
		if( !useAcceptAllFileFilterCheckBox.isSelected() )
			fc.setAcceptAllFileFilterUsed( false );
		for( int i  = 0; i  < fileTypes.length; i += 2 ) {
			fc.addChoosableFileFilter( "*".equals( fileTypes[i+1] )
				? fc.getAcceptAllFileFilter()
				: new SystemFileChooser.FileNameExtensionFilter( fileTypes[i], fileTypes[i+1].split( ";" ) ) );
		}
		SystemFileChooser.FileFilter[] filters = fc.getChoosableFileFilters();
		if( filters.length > 0 )
			fc.setFileFilter( filters[Math.min( Math.max( fileTypeIndex, 0 ), filters.length - 1 )] );

//		fc.putPlatformProperty( SystemFileChooser.WINDOWS_FILE_NAME_LABEL, "My filename label:" );
//		fc.putPlatformProperty( SystemFileChooser.WINDOWS_OPTIONS_SET, FlatNativeWindowsLibrary.FOS_HIDEMRUPLACES );

//		fc.putPlatformProperty( SystemFileChooser.MAC_MESSAGE, "some message" );
//		fc.putPlatformProperty( SystemFileChooser.MAC_NAME_FIELD_LABEL, "My name label:" );
//		fc.putPlatformProperty( SystemFileChooser.MAC_FILTER_FIELD_LABEL, "My filter label" );
//		fc.putPlatformProperty( SystemFileChooser.MAC_TREATS_FILE_PACKAGES_AS_DIRECTORIES, true );
//		fc.putPlatformProperty( SystemFileChooser.MAC_OPTIONS_CLEAR, FlatNativeMacLibrary.FC_showsTagField );

//		fc.putPlatformProperty( SystemFileChooser.LINUX_OPTIONS_CLEAR, FlatNativeLinuxLibrary.FC_create_folders | FlatNativeLinuxLibrary.FC_do_overwrite_confirmation );

		String id = (String) stateStoreIDField.getSelectedItem();
		fc.setStateStoreID( id != null && !id.isEmpty() ? id : null );
	}

	private void configureSwingFileChooser( JFileChooser fc ) {
		fc.setDialogTitle( n( dialogTitleField.getText() ) );
		fc.setApproveButtonText( n( approveButtonTextField.getText() ) );
		fc.setApproveButtonMnemonic( mnemonic( approveButtonMnemonicField.getText() ) );

		// paths
		if( currentDirCheckBox.isSelected() )
			fc.setCurrentDirectory( toFile( currentDirField.getText() ) );
		if( selectedFileCheckBox.isSelected() )
			fc.setSelectedFile( toFile( selectedFileField.getText() ) );
		if( selectedFilesCheckBox.isSelected() )
			fc.setSelectedFiles( toFiles( selectedFilesField.getText() ) );

		// options
		if( directorySelectionCheckBox.isSelected() )
			fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		fc.setMultiSelectionEnabled( multiSelectionEnabledCheckBox.isSelected() );
		fc.setFileHidingEnabled( useFileHidingCheckBox.isSelected() );

		// filter
		String fileTypesStr = n( (String) fileTypesField.getSelectedItem() );
		String[] fileTypes = {};
		if( fileTypesStr != null )
			fileTypes = fileTypesStr.trim().split( "[,]+" );
		int fileTypeIndex = fileTypeIndexSlider.getValue();
		if( !useAcceptAllFileFilterCheckBox.isSelected() )
			fc.setAcceptAllFileFilterUsed( false );
		for( int i  = 0; i  < fileTypes.length; i += 2 ) {
			fc.addChoosableFileFilter( "*".equals( fileTypes[i+1] )
				? fc.getAcceptAllFileFilter()
				: new FileNameExtensionFilter( fileTypes[i], fileTypes[i+1].split( ";" ) ) );
		}
		FileFilter[] filters = fc.getChoosableFileFilters();
		if( filters.length > 0 )
			fc.setFileFilter( filters[Math.min( Math.max( fileTypeIndex, 0 ), filters.length - 1 )] );
	}

	private void configureAWTFileChooser( FileDialog fc, boolean open ) {
		fc.setMode( open ? FileDialog.LOAD : FileDialog.SAVE );
		fc.setTitle( n( dialogTitleField.getText() ) );

		// paths
		if( currentDirCheckBox.isSelected() )
			fc.setDirectory( n( currentDirField.getText() ) );

		// options
		fc.setMultipleMode( multiSelectionEnabledCheckBox.isSelected() );
	}

	private void outputSystemFileChooser( SystemFileChooser fc, int result ) {
		output( "System", fc.getDialogType() == SystemFileChooser.OPEN_DIALOG,
			fc.isDirectorySelectionEnabled(), fc.isMultiSelectionEnabled(),
			"result", result,
			"currentDirectory", fc.getCurrentDirectory(),
			"selectedFile", fc.getSelectedFile(),
			"selectedFiles", fc.getSelectedFiles() );
	}

	private void outputSwingFileChooser( String type, JFileChooser fc, int result ) {
		output( type, fc.getDialogType() == JFileChooser.OPEN_DIALOG,
			fc.isDirectorySelectionEnabled(), fc.isMultiSelectionEnabled(),
			"result", result,
			"currentDirectory", fc.getCurrentDirectory(),
			"selectedFile", fc.getSelectedFile(),
			"selectedFiles", fc.getSelectedFiles() );
	}

	private void outputAWTFileChooser( FileDialog fc ) {
		output( "AWT", fc.getMode() == FileDialog.LOAD, false, fc.isMultipleMode(),
			"files", fc.getFiles(),
			"directory", fc.getDirectory(),
			"file", fc.getFile() );
	}

	private void output( String type, boolean open, boolean directorySelection,
		boolean multiSelection, Object... values )
	{
		outputField.append( "---- " + type + " " + (open ? "Open " : "Save ")
			+ (directorySelection ? " directory-sel " : "")
			+ (multiSelection ? " multi-sel " : "")
			+ "----\n" );

		for( int i = 0; i < values.length; i += 2 ) {
			outputField.append( values[i] + " = " );
			Object value = values[i+1];
			if( value instanceof File[] )
				outputField.append( Arrays.toString( (File[]) value ).replace( ",", "\n    " ) );
			else
				outputField.append( String.valueOf( value ) );
			outputField.append( "\n" );
		}
		outputField.append( "\n" );
		outputField.setCaretPosition( outputField.getDocument().getLength() );
	}

	private static String n( String s ) {
		return !s.isEmpty() ? s : null;
	}

	private static char mnemonic( String s ) {
		return !s.isEmpty() ? s.charAt( 0 ) : 0;
	}

	private void showWithOwner( Consumer<Window> showConsumer ) {
		Window frame = SwingUtilities.windowForComponent( this );
		if( ownerFrameRadioButton.isSelected() )
			showConsumer.accept( frame );
		else if( ownerDialogRadioButton.isSelected() )
			new DummyModalDialog( frame, showConsumer ).setVisible( true );
		else
			showConsumer.accept( null );
	}

	private void currentDirChanged() {
		boolean b = currentDirCheckBox.isSelected();
		currentDirField.setEditable( b );
		currentDirChooseButton.setEnabled( b );

		DemoPrefs.getState().putBoolean( "systemfilechooser.currentdir.enabled", b );
	}

	private void selectedFileChanged() {
		boolean b = selectedFileCheckBox.isSelected();
		selectedFileField.setEditable( b );
		selectedFileChooseButton.setEnabled( b );

		DemoPrefs.getState().putBoolean( "systemfilechooser.selectedfile.enabled", b );
	}

	private void selectedFilesChanged() {
		boolean b = selectedFilesCheckBox.isSelected();
		selectedFilesField.setEditable( b );
		selectedFilesChooseButton.setEnabled( b );

		DemoPrefs.getState().putBoolean( "systemfilechooser.selectedfiles.enabled", b );
	}

	private void chooseCurrentDir() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle( "Current Directory" );
		chooser.setSelectedFile( toFile( currentDirField.getText() ) );
		chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		if( chooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			currentDirField.setText( toString( chooser.getSelectedFile() ) );
			putState( "systemfilechooser.currentdir", currentDirField.getText() );
		}
	}

	private void chooseSelectedFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle( "Selected File" );
		chooser.setSelectedFile( toFile( selectedFileField.getText() ) );
		chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		if( chooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			selectedFileField.setText( toString( chooser.getSelectedFile() ) );
			putState( "systemfilechooser.selectedfile", selectedFileField.getText() );
		}
	}

	private void chooseSelectedFiles() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle( "Selected Files" );
		chooser.setSelectedFiles( toFiles( selectedFilesField.getText() ) );
		chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		chooser.setMultiSelectionEnabled( true );
		if( chooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			selectedFilesField.setText( toString( chooser.getSelectedFiles() ) );
			putState( "systemfilechooser.selectedfiles", selectedFilesField.getText() );
		}
	}

	private static File toFile( String s ) {
		return !s.isEmpty() ? new File( s ) : null;
	}

	private static String toString( File file ) {
		return (file != null) ? file.getAbsolutePath() : null;
	}

	private static File[] toFiles( String s ) {
		return !s.isEmpty()
			? Stream.of( s.split( "," ) ).map( name -> new File( name ) ).toArray( File[]::new )
			: new File[0];
	}

	private static String toString( File[] files ) {
		return (files != null && files.length > 0)
			? String.join( ",", Stream.of( files ).map( file -> file.getAbsolutePath() ).toArray( String[]::new ) )
			: "";
	}

	private static void putState( String key, String value ) {
		if( value.isEmpty() )
			DemoPrefs.getState().remove( key );
		else
			DemoPrefs.getState().put( key, value );
	}

	private void menuItemAction() {
		System.out.println( "menu item action" );
	}

	static void addListeners( Window w ) {
		w.addWindowListener( new WindowListener() {
			@Override
			public void windowOpened( WindowEvent e ) {
				printWindowEvent( e );
			}

			@Override
			public void windowIconified( WindowEvent e ) {
				printWindowEvent( e );
			}

			@Override
			public void windowDeiconified( WindowEvent e ) {
				printWindowEvent( e );
			}

			@Override
			public void windowDeactivated( WindowEvent e ) {
				printWindowEvent( e );
			}

			@Override
			public void windowClosing( WindowEvent e ) {
				printWindowEvent( e );
			}

			@Override
			public void windowClosed( WindowEvent e ) {
				printWindowEvent( e );
			}

			@Override
			public void windowActivated( WindowEvent e ) {
				printWindowEvent( e );
			}
		} );
		w.addWindowStateListener( new WindowStateListener() {
			@Override
			public void windowStateChanged( WindowEvent e ) {
				printWindowEvent( e );
			}
		} );
		w.addWindowFocusListener( new WindowFocusListener() {
			@Override
			public void windowLostFocus( WindowEvent e ) {
				printWindowEvent( e );
			}

			@Override
			public void windowGainedFocus( WindowEvent e ) {
				printWindowEvent( e );
			}
		} );
	}

	static void printWindowEvent( WindowEvent e ) {
		String typeStr;
		switch( e.getID() ) {
			case WindowEvent.WINDOW_OPENED:			typeStr = "WINDOW_OPENED       "; break;
			case WindowEvent.WINDOW_CLOSING:		typeStr = "WINDOW_CLOSING      "; break;
			case WindowEvent.WINDOW_CLOSED:			typeStr = "WINDOW_CLOSED       "; break;
			case WindowEvent.WINDOW_ICONIFIED:		typeStr = "WINDOW_ICONIFIED    "; break;
			case WindowEvent.WINDOW_DEICONIFIED:	typeStr = "WINDOW_DEICONIFIED  "; break;
			case WindowEvent.WINDOW_ACTIVATED:		typeStr = "WINDOW_ACTIVATED    "; break;
			case WindowEvent.WINDOW_DEACTIVATED:	typeStr = "WINDOW_DEACTIVATED  "; break;
			case WindowEvent.WINDOW_GAINED_FOCUS:	typeStr = "WINDOW_GAINED_FOCUS "; break;
			case WindowEvent.WINDOW_LOST_FOCUS:		typeStr = "WINDOW_LOST_FOCUS   "; break;
			case WindowEvent.WINDOW_STATE_CHANGED:	typeStr = "WINDOW_STATE_CHANGED"; break;
			default:								typeStr = "unknown type        "; break;
		}
		Object source = e.getSource();
		Window opposite = e.getOppositeWindow();
		String sourceStr = (source instanceof Component) ? ((Component)source).getName() : String.valueOf( source );
		String oppositeStr = (opposite != null) ? opposite.getName() : null;
		System.out.println( typeStr + "  source " + sourceStr + "   opposite " + oppositeStr );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel ownerLabel = new JLabel();
		ownerFrameRadioButton = new JRadioButton();
		ownerDialogRadioButton = new JRadioButton();
		ownerNullRadioButton = new JRadioButton();
		JPanel ownerSpacer = new JPanel(null);
		JLabel dialogTitleLabel = new JLabel();
		dialogTitleField = new JTextField();
		JPanel panel1 = new JPanel();
		directorySelectionCheckBox = new JCheckBox();
		multiSelectionEnabledCheckBox = new JCheckBox();
		useFileHidingCheckBox = new JCheckBox();
		useSystemFileChooserCheckBox = new JCheckBox();
		persistStateCheckBox = new JCheckBox();
		JLabel stateStoreIDLabel = new JLabel();
		stateStoreIDField = new JComboBox<>();
		JLabel approveButtonTextLabel = new JLabel();
		approveButtonTextField = new JTextField();
		JLabel approveButtonMnemonicLabel = new JLabel();
		approveButtonMnemonicField = new JTextField();
		currentDirCheckBox = new JCheckBox();
		currentDirField = new JTextField();
		currentDirChooseButton = new JButton();
		selectedFileCheckBox = new JCheckBox();
		selectedFileField = new JTextField();
		selectedFileChooseButton = new JButton();
		selectedFilesCheckBox = new JCheckBox();
		selectedFilesField = new JTextField();
		selectedFilesChooseButton = new JButton();
		JLabel fileTypesLabel = new JLabel();
		fileTypesField = new JComboBox<>();
		JLabel fileTypeIndexLabel = new JLabel();
		fileTypeIndexSlider = new JSlider();
		useAcceptAllFileFilterCheckBox = new JCheckBox();
		JButton openButton = new JButton();
		JButton saveButton = new JButton();
		JButton swingOpenButton = new JButton();
		JButton swingSaveButton = new JButton();
		JButton awtOpenButton = new JButton();
		JButton awtSaveButton = new JButton();
		javafxOpenButton = new JButton();
		javafxSaveButton = new JButton();
		JScrollPane outputScrollPane = new JScrollPane();
		outputField = new JTextArea();
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

		//---- dialogTitleLabel ----
		dialogTitleLabel.setText("dialogTitle");
		add(dialogTitleLabel, "cell 0 1");
		add(dialogTitleField, "cell 1 1");

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"insets 2,hidemode 3",
				// columns
				"[left]",
				// rows
				"[]0" +
				"[]0" +
				"[]" +
				"[]para" +
				"[]" +
				"[]"));

			//---- directorySelectionCheckBox ----
			directorySelectionCheckBox.setText("directorySelection");
			panel1.add(directorySelectionCheckBox, "cell 0 0");

			//---- multiSelectionEnabledCheckBox ----
			multiSelectionEnabledCheckBox.setText("multiSelectionEnabled");
			panel1.add(multiSelectionEnabledCheckBox, "cell 0 1");

			//---- useFileHidingCheckBox ----
			useFileHidingCheckBox.setText("useFileHiding");
			useFileHidingCheckBox.setSelected(true);
			panel1.add(useFileHidingCheckBox, "cell 0 2");

			//---- useSystemFileChooserCheckBox ----
			useSystemFileChooserCheckBox.setText("use SystemFileChooser");
			useSystemFileChooserCheckBox.setSelected(true);
			panel1.add(useSystemFileChooserCheckBox, "cell 0 3");

			//---- persistStateCheckBox ----
			persistStateCheckBox.setText("persist state");
			persistStateCheckBox.addActionListener(e -> persistStateChanged());
			panel1.add(persistStateCheckBox, "cell 0 4");

			//---- stateStoreIDLabel ----
			stateStoreIDLabel.setText("ID:");
			panel1.add(stateStoreIDLabel, "cell 0 5");

			//---- stateStoreIDField ----
			stateStoreIDField.setModel(new DefaultComboBoxModel<>(new String[] {
				"abc",
				"def"
			}));
			stateStoreIDField.setEditable(true);
			stateStoreIDField.setSelectedIndex(-1);
			panel1.add(stateStoreIDField, "cell 0 5,growx");
		}
		add(panel1, "cell 2 1 1 7,aligny top,growy 0");

		//---- approveButtonTextLabel ----
		approveButtonTextLabel.setText("approveButtonText");
		add(approveButtonTextLabel, "cell 0 2");
		add(approveButtonTextField, "cell 1 2,growx");

		//---- approveButtonMnemonicLabel ----
		approveButtonMnemonicLabel.setText("approveButtonMnemonic");
		add(approveButtonMnemonicLabel, "cell 1 2");

		//---- approveButtonMnemonicField ----
		approveButtonMnemonicField.setColumns(3);
		add(approveButtonMnemonicField, "cell 1 2");

		//---- currentDirCheckBox ----
		currentDirCheckBox.setText("current directory");
		currentDirCheckBox.addActionListener(e -> currentDirChanged());
		add(currentDirCheckBox, "cell 0 3");
		add(currentDirField, "cell 1 3,growx");

		//---- currentDirChooseButton ----
		currentDirChooseButton.setText("...");
		currentDirChooseButton.addActionListener(e -> chooseCurrentDir());
		add(currentDirChooseButton, "cell 1 3");

		//---- selectedFileCheckBox ----
		selectedFileCheckBox.setText("selected file");
		selectedFileCheckBox.addActionListener(e -> selectedFileChanged());
		add(selectedFileCheckBox, "cell 0 4");
		add(selectedFileField, "cell 1 4,growx");

		//---- selectedFileChooseButton ----
		selectedFileChooseButton.setText("...");
		selectedFileChooseButton.addActionListener(e -> chooseSelectedFile());
		add(selectedFileChooseButton, "cell 1 4");

		//---- selectedFilesCheckBox ----
		selectedFilesCheckBox.setText("selected files");
		selectedFilesCheckBox.addActionListener(e -> selectedFilesChanged());
		add(selectedFilesCheckBox, "cell 0 5");
		add(selectedFilesField, "cell 1 5,growx");

		//---- selectedFilesChooseButton ----
		selectedFilesChooseButton.setText("...");
		selectedFilesChooseButton.addActionListener(e -> chooseSelectedFiles());
		add(selectedFilesChooseButton, "cell 1 5");

		//---- fileTypesLabel ----
		fileTypesLabel.setText("fileTypes");
		add(fileTypesLabel, "cell 0 6");

		//---- fileTypesField ----
		fileTypesField.setEditable(true);
		fileTypesField.setModel(new DefaultComboBoxModel<>(new String[] {
			"Text Files,txt",
			"All Files,*",
			"Text Files,txt,PDF Files,pdf,All Files,*",
			"Text and PDF Files,txt;pdf"
		}));
		add(fileTypesField, "cell 1 6");

		//---- fileTypeIndexLabel ----
		fileTypeIndexLabel.setText("fileTypeIndex");
		add(fileTypeIndexLabel, "cell 0 7");

		//---- fileTypeIndexSlider ----
		fileTypeIndexSlider.setMaximum(10);
		fileTypeIndexSlider.setMajorTickSpacing(1);
		fileTypeIndexSlider.setValue(0);
		fileTypeIndexSlider.setPaintLabels(true);
		fileTypeIndexSlider.setSnapToTicks(true);
		add(fileTypeIndexSlider, "cell 1 7,growx");

		//---- useAcceptAllFileFilterCheckBox ----
		useAcceptAllFileFilterCheckBox.setText("useAcceptAllFileFilter");
		useAcceptAllFileFilterCheckBox.setSelected(true);
		add(useAcceptAllFileFilterCheckBox, "cell 1 7");

		//---- openButton ----
		openButton.setText("Open...");
		openButton.addActionListener(e -> open());
		add(openButton, "cell 0 8 3 1");

		//---- saveButton ----
		saveButton.setText("Save...");
		saveButton.addActionListener(e -> save());
		add(saveButton, "cell 0 8 3 1");

		//---- swingOpenButton ----
		swingOpenButton.setText("Swing Open...");
		swingOpenButton.addActionListener(e -> swingOpen());
		add(swingOpenButton, "cell 0 8 3 1");

		//---- swingSaveButton ----
		swingSaveButton.setText("Swing Save...");
		swingSaveButton.addActionListener(e -> swingSave());
		add(swingSaveButton, "cell 0 8 3 1");

		//---- awtOpenButton ----
		awtOpenButton.setText("AWT Open...");
		awtOpenButton.addActionListener(e -> awtOpen());
		add(awtOpenButton, "cell 0 8 3 1");

		//---- awtSaveButton ----
		awtSaveButton.setText("AWT Save...");
		awtSaveButton.addActionListener(e -> awtSave());
		add(awtSaveButton, "cell 0 8 3 1");

		//---- javafxOpenButton ----
		javafxOpenButton.setText("JavaFX Open...");
		javafxOpenButton.addActionListener(e -> javafxOpen());
		add(javafxOpenButton, "cell 0 8 3 1");

		//---- javafxSaveButton ----
		javafxSaveButton.setText("JavaFX Save...");
		javafxSaveButton.addActionListener(e -> javafxSave());
		add(javafxSaveButton, "cell 0 8 3 1");

		//======== outputScrollPane ========
		{

			//---- outputField ----
			outputField.setRows(20);
			outputScrollPane.setViewportView(outputField);
		}
		add(outputScrollPane, "cell 0 9 3 1,growx");

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
	private JTextField dialogTitleField;
	private JCheckBox directorySelectionCheckBox;
	private JCheckBox multiSelectionEnabledCheckBox;
	private JCheckBox useFileHidingCheckBox;
	private JCheckBox useSystemFileChooserCheckBox;
	private JCheckBox persistStateCheckBox;
	private JComboBox<String> stateStoreIDField;
	private JTextField approveButtonTextField;
	private JTextField approveButtonMnemonicField;
	private JCheckBox currentDirCheckBox;
	private JTextField currentDirField;
	private JButton currentDirChooseButton;
	private JCheckBox selectedFileCheckBox;
	private JTextField selectedFileField;
	private JButton selectedFileChooseButton;
	private JCheckBox selectedFilesCheckBox;
	private JTextField selectedFilesField;
	private JButton selectedFilesChooseButton;
	private JComboBox<String> fileTypesField;
	private JSlider fileTypeIndexSlider;
	private JCheckBox useAcceptAllFileFilterCheckBox;
	private JButton javafxOpenButton;
	private JButton javafxSaveButton;
	private JTextArea outputField;
	private static JMenuBar menuBar1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class DummyModalDialog ---------------------------------------------

	static class DummyModalDialog
		extends JDialog
	{
		private final Consumer<Window> showConsumer;

		DummyModalDialog( Window owner, Consumer<Window> showConsumer ) {
			super( owner );
			this.showConsumer = showConsumer;
			initComponents();
			addListeners( this );
			((JComponent)getContentPane()).registerKeyboardAction(
				e -> dispose(),
				KeyStroke.getKeyStroke( "ESCAPE" ),
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

			if( owner != null ) {
				Point pt = owner.getLocationOnScreen();
				setLocation( pt.x + (getWidth() / 2), pt.y + 40 );
			} else
				setLocationRelativeTo( null );
		}

		private void modalityTypeChanged() {
			if( applicationRadioButton.isSelected() )
				setModalityType( ModalityType.APPLICATION_MODAL );
			else if( documentRadioButton.isSelected() )
				setModalityType( ModalityType.DOCUMENT_MODAL );
			else if( toolkitRadioButton.isSelected() )
				setModalityType( ModalityType.TOOLKIT_MODAL );
			else
				setModalityType( ModalityType.MODELESS );

			setVisible( false );
			setVisible( true );
		}

		private void showModalDialog() {
			new DummyModalDialog( this, showConsumer ).setVisible( true );
		}

		private void showFileDialog() {
			showConsumer.accept( this );
		}

		private void windowOpened() {
			showConsumer.accept( this );
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
			JLabel label1 = new JLabel();
			applicationRadioButton = new JRadioButton();
			documentRadioButton = new JRadioButton();
			toolkitRadioButton = new JRadioButton();
			modelessRadioButton = new JRadioButton();
			JButton showModalDialogButton = new JButton();
			JButton showFileDialogButton = new JButton();

			//======== this ========
			setTitle("Dummy Modal Dialog");
			setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowOpened(WindowEvent e) {
					DummyModalDialog.this.windowOpened();
				}
			});
			Container contentPane = getContentPane();
			contentPane.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[fill]",
				// rows
				"[]0" +
				"[]0" +
				"[]0" +
				"[]" +
				"[]para" +
				"[]" +
				"[198]"));

			//---- label1 ----
			label1.setText("Modality type:");
			contentPane.add(label1, "cell 0 0");

			//---- applicationRadioButton ----
			applicationRadioButton.setText("Application");
			applicationRadioButton.setSelected(true);
			applicationRadioButton.addActionListener(e -> modalityTypeChanged());
			contentPane.add(applicationRadioButton, "cell 1 0");

			//---- documentRadioButton ----
			documentRadioButton.setText("Document");
			documentRadioButton.addActionListener(e -> modalityTypeChanged());
			contentPane.add(documentRadioButton, "cell 1 1");

			//---- toolkitRadioButton ----
			toolkitRadioButton.setText("Toolkit");
			toolkitRadioButton.addActionListener(e -> modalityTypeChanged());
			contentPane.add(toolkitRadioButton, "cell 1 2");

			//---- modelessRadioButton ----
			modelessRadioButton.setText("modeless");
			modelessRadioButton.addActionListener(e -> modalityTypeChanged());
			contentPane.add(modelessRadioButton, "cell 1 3");

			//---- showModalDialogButton ----
			showModalDialogButton.setText("Show Modal Dialog...");
			showModalDialogButton.addActionListener(e -> showModalDialog());
			contentPane.add(showModalDialogButton, "cell 0 4 2 1");

			//---- showFileDialogButton ----
			showFileDialogButton.setText("Show File Dialog...");
			showFileDialogButton.addActionListener(e -> showFileDialog());
			contentPane.add(showFileDialogButton, "cell 0 5 2 1");
			pack();
			setLocationRelativeTo(getOwner());

			//---- modalityTypeButtonGroup ----
			ButtonGroup modalityTypeButtonGroup = new ButtonGroup();
			modalityTypeButtonGroup.add(applicationRadioButton);
			modalityTypeButtonGroup.add(documentRadioButton);
			modalityTypeButtonGroup.add(toolkitRadioButton);
			modalityTypeButtonGroup.add(modelessRadioButton);
			// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
		private JRadioButton applicationRadioButton;
		private JRadioButton documentRadioButton;
		private JRadioButton toolkitRadioButton;
		private JRadioButton modelessRadioButton;
		// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
	}
}
