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

package com.formdev.flatlaf.util;

import java.awt.Component;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.FlatNativeLinuxLibrary;
import com.formdev.flatlaf.ui.FlatNativeMacLibrary;
import com.formdev.flatlaf.ui.FlatNativeWindowsLibrary;

/**
 * Gives access to operating system file dialogs.
 * <p>
 * There are some limitations and incompatibilities to {@link JFileChooser} because
 * operating system file dialogs do not offer all features that {@code JFileChooser} provides.
 * On the other hand, operating system file dialogs offer features out of the box
 * that {@code JFileChooser} do not offer (e.g. ask for overwrite on save).
 * So this class offers only features that are available on all platforms.
 * <p>
 * The API is (mostly) compatible with {@link JFileChooser}.
 * To use this class in existing code, do a string replace from {@code JFileChooser} to {@code SystemFileChooser}.
 * If there are no compile errors, then there is a good chance that it works without further changes.
 * If there are compile errors, then you're using a feature that {@code SystemFileChooser} does not support.
 * <p>
 * Supported platforms are <b>Windows 10+</b>, <b>macOS 10.14+</b> and <b>Linux with GTK 3</b>.
 * {@code JFileChooser} is used on unsupported platforms or if GTK 3 is not installed.
 * <p>
 * {@code SystemFileChooser} requires FlatLaf native libraries (usually contained in flatlaf.jar).
 * If not available or disabled (via {@link FlatSystemProperties#USE_NATIVE_LIBRARY}
 * or {@link FlatSystemProperties#USE_SYSTEM_FILE_CHOOSER}), then {@code JFileChooser} is used.
 *
 * <h2>Limitations/incompatibilities compared to JFileChooser</h2>
 *
 * <ul>
 *   <li><b>Open File</b> and <b>Select Folder</b> dialogs always warn about not existing files/folders.
 *       The operating system shows a warning dialog to inform the user.
 *       It is not possible to customize that warning dialog.
 *       The file chooser stays open.
 *   <li><b>Save File</b> dialog always asks whether an existing file should be overwritten.
 *       The operating system shows a question dialog to ask the user whether he wants to overwrite the file or not.
 *       If user selects "Yes", the file chooser closes. If user selects "No", the file chooser stays open.
 *       It is not possible to customize that question dialog.
 *   <li><b>Save File</b> dialog does not support multi-selection.
 *   <li>For selection mode {@link #DIRECTORIES_ONLY}, dialog type {@link #SAVE_DIALOG} is ignored.
 *       Operating system file dialogs support folder selection only in "Open" dialogs.
 *   <li>{@link JFileChooser#FILES_AND_DIRECTORIES} is not supported.
 *   <li>{@link #getSelectedFiles()} returns selected file also in single selection mode.
 *       {@link JFileChooser#getSelectedFiles()} only in multi selection mode.
 *   <li>Only file name extension filters (see {@link FileNameExtensionFilter}) are supported.
 *   <li>If adding choosable file filters and {@link #isAcceptAllFileFilterUsed()} is {@code true},
 *       then the <b>All Files</b> filter is placed at the end of the combobox list
 *       (as usual in current operating systems) and the first choosable filter is selected by default.
 *       {@code JFileChooser}, on the other hand, adds <b>All Files</b> filter
 *       as first item and selects it by default.
 *       Use {@code chooser.addChoosableFileFilter( chooser.getAcceptAllFileFilter() )}
 *       to place <b>All Files</b> filter somewhere else.
 * </ul>
 *
 * @author Karl Tauber
 * @since 3.6
 */
public class SystemFileChooser
{
	/** @see JFileChooser#OPEN_DIALOG */
	public static final int OPEN_DIALOG = JFileChooser.OPEN_DIALOG;

	/** @see JFileChooser#SAVE_DIALOG */
	public static final int SAVE_DIALOG = JFileChooser.SAVE_DIALOG;

	/** @see JFileChooser#CANCEL_OPTION */
	public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;

	/** @see JFileChooser#APPROVE_OPTION */
	public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;

	/** @see JFileChooser#FILES_ONLY */
	public static final int FILES_ONLY = JFileChooser.FILES_ONLY;

	/** @see JFileChooser#DIRECTORIES_ONLY */
	public static final int DIRECTORIES_ONLY = JFileChooser.DIRECTORIES_ONLY;

	private int dialogType = OPEN_DIALOG;
	private String dialogTitle;
	private String approveButtonText;
	private int approveButtonMnemonic = 0;
	private int fileSelectionMode = FILES_ONLY;
	private boolean multiSelection;
	private boolean useFileHiding = true;

	private File currentDirectory;
	private File selectedFile;
	private File[] selectedFiles;

	private final ArrayList<FileFilter> filters = new ArrayList<>();
	private FileFilter fileFilter;
	private AcceptAllFileFilter acceptAllFileFilter;
	private boolean useAcceptAllFileFilter = true;

	/**
	 * If {@code fc.addChoosableFileFilter(fc.getAcceptAllFileFilter())} is invoked from user code,
	 * then this flag is set to {@code false} and subsequent invocations of {@code fc.addChoosableFileFilter(...)}
	 * no longer insert added filters before the "All Files" filter.
	 * This allows custom ordering the "All Files" filter.
	 */
	private boolean keepAcceptAllAtEnd = true;

	/** @see JFileChooser#JFileChooser() */
	public SystemFileChooser() {
		this( (File) null );
	}

	/**  @see JFileChooser#JFileChooser(String) */
	public SystemFileChooser( String currentDirectoryPath ) {
		this( (currentDirectoryPath != null)
			? FileSystemView.getFileSystemView().createFileObject( currentDirectoryPath )
			: null );
	}

	/**  @see JFileChooser#JFileChooser(File) */
	public SystemFileChooser( File currentDirectory ) {
		setCurrentDirectory( currentDirectory );

		addChoosableFileFilter( getAcceptAllFileFilter() );
		keepAcceptAllAtEnd = true;
	}

	/** @see JFileChooser#showOpenDialog(Component) */
	public int showOpenDialog( Component parent ) {
		setDialogType( OPEN_DIALOG );
		return showDialogImpl( parent );
	}

	/** @see JFileChooser#showSaveDialog(Component) */
	public int showSaveDialog( Component parent ) {
		setDialogType( SAVE_DIALOG );
		return showDialogImpl( parent );
	}

	/** @see JFileChooser#showDialog(Component, String) */
	public int showDialog( Component parent, String approveButtonText ) {
		if( approveButtonText != null )
			setApproveButtonText( approveButtonText );
		return showDialogImpl( parent );
	}

	/** @see JFileChooser#getDialogType() */
	public int getDialogType() {
		return dialogType;
	}

	/** @see JFileChooser#setDialogType(int) */
	public void setDialogType( int dialogType ) {
		if( dialogType != OPEN_DIALOG && dialogType != SAVE_DIALOG )
			throw new IllegalArgumentException( "Invalid dialog type " + dialogType );

		this.dialogType = dialogType;
	}

	/** @see JFileChooser#getDialogTitle() */
	public String getDialogTitle() {
		return dialogTitle;
	}

	/** @see JFileChooser#setDialogTitle(String) */
	public void setDialogTitle( String dialogTitle ) {
		this.dialogTitle = dialogTitle;
	}

	/** @see JFileChooser#getApproveButtonText() */
	public String getApproveButtonText() {
		return approveButtonText;
	}

	/** @see JFileChooser#setApproveButtonText(String) */
	public void setApproveButtonText( String approveButtonText ) {
		this.approveButtonText = approveButtonText;
	}

	/** @see JFileChooser#getApproveButtonMnemonic() */
	public int getApproveButtonMnemonic() {
		return approveButtonMnemonic;
	}

	/** @see JFileChooser#setApproveButtonMnemonic(int) */
	public void setApproveButtonMnemonic( int mnemonic ) {
		approveButtonMnemonic = mnemonic;
	}

	/** @see JFileChooser#setApproveButtonMnemonic(char) */
	public void setApproveButtonMnemonic( char mnemonic ) {
		int vk = mnemonic;
		if( vk >= 'a' && vk <= 'z' )
			vk -= 'a' - 'A';
		setApproveButtonMnemonic( vk );
	}

	/** @see JFileChooser#getFileSelectionMode() */
	public int getFileSelectionMode() {
		return fileSelectionMode;
	}

	/** @see JFileChooser#setFileSelectionMode(int) */
	public void setFileSelectionMode( int fileSelectionMode ) {
		if( fileSelectionMode != FILES_ONLY && fileSelectionMode != DIRECTORIES_ONLY )
			throw new IllegalArgumentException( "Invalid file selection mode " + fileSelectionMode );

		this.fileSelectionMode = fileSelectionMode;
	}

	/** @see JFileChooser#isFileSelectionEnabled() */
	public boolean isFileSelectionEnabled() {
		return fileSelectionMode == FILES_ONLY;
	}

	/** @see JFileChooser#isDirectorySelectionEnabled() */
	public boolean isDirectorySelectionEnabled() {
		return fileSelectionMode == DIRECTORIES_ONLY;
	}

	/** @see JFileChooser#isMultiSelectionEnabled() */
	public boolean isMultiSelectionEnabled() {
		return multiSelection;
	}

	/** @see JFileChooser#setMultiSelectionEnabled(boolean) */
	public void setMultiSelectionEnabled( boolean multiSelection ) {
		this.multiSelection = multiSelection;
	}

	/** @see JFileChooser#isFileHidingEnabled() */
	public boolean isFileHidingEnabled() {
		return useFileHiding;
	}

	/** @see JFileChooser#setFileHidingEnabled(boolean) */
	public void setFileHidingEnabled( boolean useFileHiding ) {
		this.useFileHiding = useFileHiding;
	}

	/** @see JFileChooser#getCurrentDirectory() */
	public File getCurrentDirectory() {
		return currentDirectory;
	}

	/** @see JFileChooser#setCurrentDirectory(File) */
	public void setCurrentDirectory( File dir ) {
		// for compatibility with JFileChooser
		if( dir != null && !dir.exists() )
			return;
		if( dir == null )
			dir = FileSystemView.getFileSystemView().getDefaultDirectory();

		currentDirectory = dir;
	}

	/** @see JFileChooser#getSelectedFile() */
	public File getSelectedFile() {
		return selectedFile;
	}

	/** @see JFileChooser#setSelectedFile(File) */
	public void setSelectedFile( File file ) {
		selectedFile = file;

		// for compatibility with JFileChooser
		if( file != null &&
			file.isAbsolute() &&
			!FileSystemView.getFileSystemView().isParent( getCurrentDirectory(), file ) )
		  setCurrentDirectory( file.getParentFile() );
	}

	/** @see JFileChooser#getSelectedFiles() */
	public File[] getSelectedFiles() {
		return (selectedFiles != null) ? selectedFiles.clone() : new File[0];
	}

	/** @see JFileChooser#setSelectedFiles(File[]) */
	public void setSelectedFiles( File[] selectedFiles ) {
		if( selectedFiles != null && selectedFiles.length > 0 ) {
			this.selectedFiles = selectedFiles.clone();
			setSelectedFile( selectedFiles[0] );
		} else {
			this.selectedFiles = null;
			setSelectedFile( null );
		}
	}

	/** @see JFileChooser#getChoosableFileFilters() */
	public FileFilter[] getChoosableFileFilters() {
		return filters.toArray( new FileFilter[filters.size()] );
	}

	/** @see JFileChooser#addChoosableFileFilter(javax.swing.filechooser.FileFilter) */
	public void addChoosableFileFilter( FileFilter filter ) {
		if( filter == getAcceptAllFileFilter() )
			keepAcceptAllAtEnd = false;

		if( filter == null || filters.contains( filter ) )
			return;

		if( !(filter instanceof FileNameExtensionFilter) && !(filter instanceof AcceptAllFileFilter) )
			throw new IllegalArgumentException( "Filter class not supported: " + filter.getClass().getName() );

		// either insert filter before "All Files" filter, or append to the end
		int size = filters.size();
		if( keepAcceptAllAtEnd && size > 0 && (filters.get( size - 1 ) == getAcceptAllFileFilter()) )
			filters.add( size - 1, filter );
		else
			filters.add( filter );

		// initialize current filter
		if( fileFilter == null || (filters.size() == 2 && filters.get( 1 ) == getAcceptAllFileFilter()) )
			setFileFilter( filter );
	}

	/** @see JFileChooser#removeChoosableFileFilter(javax.swing.filechooser.FileFilter) */
	public boolean removeChoosableFileFilter( FileFilter filter ) {
		if( !filters.remove( filter ) )
			return false;

		// update current filter if necessary
		if( filter == getFileFilter() ) {
			if( isAcceptAllFileFilterUsed() && filter != getAcceptAllFileFilter() )
				setFileFilter( getAcceptAllFileFilter() );
			else
				setFileFilter( !filters.isEmpty() ? filters.get( 0 ) : null );
		}

		return true;
	}

	/** @see JFileChooser#resetChoosableFileFilters() */
	public void resetChoosableFileFilters() {
		filters.clear();
		setFileFilter( null );
		if( isAcceptAllFileFilterUsed() ) {
			addChoosableFileFilter( getAcceptAllFileFilter() );
			keepAcceptAllAtEnd = true;
		}
	}

	/** @see JFileChooser#getAcceptAllFileFilter() */
	public FileFilter getAcceptAllFileFilter() {
		if( acceptAllFileFilter == null )
			acceptAllFileFilter = new AcceptAllFileFilter();
		return acceptAllFileFilter;
	}

	/** @see JFileChooser#isAcceptAllFileFilterUsed() */
	public boolean isAcceptAllFileFilterUsed() {
		return useAcceptAllFileFilter;
	}

	/** @see JFileChooser#setAcceptAllFileFilterUsed(boolean) */
	public void setAcceptAllFileFilterUsed( boolean acceptAll ) {
		useAcceptAllFileFilter = acceptAll;

		removeChoosableFileFilter( getAcceptAllFileFilter() );
		if( acceptAll ) {
			addChoosableFileFilter( getAcceptAllFileFilter() );
			keepAcceptAllAtEnd = true;
		}
	}

	/** @see JFileChooser#getFileFilter() */
	public FileFilter getFileFilter() {
		return fileFilter;
	}

	/** @see JFileChooser#setFileFilter(javax.swing.filechooser.FileFilter) */
	public void setFileFilter( FileFilter filter ) {
		this.fileFilter = filter;
	}

	private int indexOfCurrentFilter() {
		return filters.indexOf( fileFilter );
	}

	private boolean hasOnlyAcceptAll() {
		return filters.size() == 1 && filters.get( 0 ) == getAcceptAllFileFilter();
	}

	private int showDialogImpl( Component parent ) {
		File[] files = getProvider().showDialog( parent, this );
		setSelectedFiles( files );
		return (files != null) ? APPROVE_OPTION : CANCEL_OPTION;
	}

	private FileChooserProvider getProvider() {
		if( !FlatSystemProperties.getBoolean( FlatSystemProperties.USE_SYSTEM_FILE_CHOOSER, true ) )
			return new SwingFileChooserProvider();

		if( SystemInfo.isWindows_10_orLater && FlatNativeWindowsLibrary.isLoaded() )
			return new WindowsFileChooserProvider();
		else if( SystemInfo.isMacOS && FlatNativeMacLibrary.isLoaded() )
			return new MacFileChooserProvider();
		else if( SystemInfo.isLinux && FlatNativeLinuxLibrary.isLoaded() && FlatNativeLinuxLibrary.isGtk3Available() )
			return new LinuxFileChooserProvider();
		else // unknown platform or FlatLaf native library not loaded
			return new SwingFileChooserProvider();
	}

	//---- interface FileChooserProvider --------------------------------------

	private interface FileChooserProvider {
		File[] showDialog( Component parent, SystemFileChooser fc );
	}

	//---- class SystemFileChooserProvider ------------------------------------

	private static abstract class SystemFileChooserProvider
		implements FileChooserProvider
	{
		@Override
		public File[] showDialog( Component parent, SystemFileChooser fc ) {
			Window owner = (parent instanceof Window)
				? (Window) parent
				: (parent != null) ? SwingUtilities.windowForComponent( parent ) : null;
			AtomicReference<String[]> filenamesRef = new AtomicReference<>();

			// create secondary event look and invoke system file dialog on a new thread
			SecondaryLoop secondaryLoop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
			new Thread( () -> {
				filenamesRef.set( showSystemDialog( owner, fc ) );
				secondaryLoop.exit();
			}, "FlatLaf SystemFileChooser" ).start();
			secondaryLoop.enter();

			String[] filenames = filenamesRef.get();

			// fallback to Swing file chooser if system file dialog failed or is not available
			if( filenames == null )
				return new SwingFileChooserProvider().showDialog( parent, fc );

			// canceled?
			if( filenames.length == 0 )
				return null;

			// convert file names to file objects
			FileSystemView fsv = FileSystemView.getFileSystemView();
			File[] files = new File[filenames.length];
			for( int i = 0; i < filenames.length; i++ )
				files[i] = fsv.createFileObject( filenames[i] );
			return files;
		}

		abstract String[] showSystemDialog( Window owner, SystemFileChooser fc );
	}

	//---- class WindowsFileChooserProvider -----------------------------------

	private static class WindowsFileChooserProvider
		extends SystemFileChooserProvider
	{
		@Override
		String[] showSystemDialog( Window owner, SystemFileChooser fc ) {
			boolean open = (fc.getDialogType() == OPEN_DIALOG);
			String approveButtonText = fc.getApproveButtonText();
			int approveButtonMnemonic = fc.getApproveButtonMnemonic();
			String fileName = null;
			String folder = null;
			String saveAsItem = null;

			// approve button text and mnemonic
			if( approveButtonText != null ) {
				approveButtonText = approveButtonText.replace( "&", "&&" );
				if( approveButtonMnemonic > 0 ) {
					int mnemonicIndex = approveButtonText.toUpperCase( Locale.ENGLISH ).indexOf( approveButtonMnemonic );
					if( mnemonicIndex >= 0 ) {
						approveButtonText = approveButtonText.substring( 0, mnemonicIndex )
							+ '&' + approveButtonText.substring( mnemonicIndex );
					}
				}
			}

			// paths
			File currentDirectory = fc.getCurrentDirectory();
			File selectedFile = fc.getSelectedFile();
			if( selectedFile != null ) {
				if( selectedFile.exists() && !open )
					saveAsItem = selectedFile.getAbsolutePath();
				else {
					fileName = selectedFile.getName();
					folder = selectedFile.getParent();
				}
			} else if( currentDirectory != null )
				folder = currentDirectory.getAbsolutePath();

			// options
			int optionsSet = FlatNativeWindowsLibrary.FOS_OVERWRITEPROMPT;
			int optionsClear = 0;
			if( fc.isDirectorySelectionEnabled() )
				optionsSet |= FlatNativeWindowsLibrary.FOS_PICKFOLDERS;
			if( fc.isMultiSelectionEnabled() )
				optionsSet |= FlatNativeWindowsLibrary.FOS_ALLOWMULTISELECT;
			if( !fc.isFileHidingEnabled() )
				optionsSet |= FlatNativeWindowsLibrary.FOS_FORCESHOWHIDDEN;

			// filter
			int fileTypeIndex = 0;
			ArrayList<String> fileTypes = new ArrayList<>();
			if( !fc.isDirectorySelectionEnabled() ) {
				if( !fc.hasOnlyAcceptAll() ) {
					fileTypeIndex = fc.indexOfCurrentFilter();
					for( FileFilter filter : fc.getChoosableFileFilters() ) {
						if( filter instanceof FileNameExtensionFilter ) {
							fileTypes.add( filter.getDescription() );
							fileTypes.add( "*." + String.join( ";*.", ((FileNameExtensionFilter)filter).getExtensions() ) );
						} else if( filter instanceof AcceptAllFileFilter ) {
							fileTypes.add( filter.getDescription() );
							fileTypes.add( "*.*" );
						}
					}
				}

				// if there are no file types
				// - for Save dialog add "All Files", otherwise Windows would show an empty "Save as type" combobox
				// - for Open dialog, Windows hides the combobox
				if( !open && fileTypes.isEmpty() ) {
					fileTypes.add( fc.getAcceptAllFileFilter().getDescription() );
					fileTypes.add( "*.*" );
				}
			}

			// show system file dialog
			return FlatNativeWindowsLibrary.showFileChooser( owner, open,
				fc.getDialogTitle(), approveButtonText, null, fileName,
				folder, saveAsItem, null, null, optionsSet, optionsClear, null,
				fileTypeIndex, fileTypes.toArray( new String[fileTypes.size()] ) );
		}
	}

	//---- class MacFileChooserProvider ---------------------------------------

	private static class MacFileChooserProvider
		extends SystemFileChooserProvider
	{
		@Override
		String[] showSystemDialog( Window owner, SystemFileChooser fc ) {
			boolean open = (fc.getDialogType() == OPEN_DIALOG);
			String nameFieldStringValue = null;
			String directoryURL = null;

			// paths
			File currentDirectory = fc.getCurrentDirectory();
			File selectedFile = fc.getSelectedFile();
			if( selectedFile != null ) {
				if( selectedFile.isDirectory() )
					directoryURL = selectedFile.getAbsolutePath();
				else {
					nameFieldStringValue = selectedFile.getName();
					directoryURL = selectedFile.getParent();
				}
			} else if( currentDirectory != null )
				directoryURL = currentDirectory.getAbsolutePath();

			// options
			int optionsSet = FlatNativeMacLibrary.FC_accessoryViewDisclosed;
			int optionsClear = 0;
			if( fc.isDirectorySelectionEnabled() ) {
				optionsSet |= FlatNativeMacLibrary.FC_canChooseDirectories;
				optionsClear |= FlatNativeMacLibrary.FC_canChooseFiles;
				open = true;
			}
			if( fc.isMultiSelectionEnabled() )
				optionsSet |= FlatNativeMacLibrary.FC_allowsMultipleSelection;
			if( !fc.isFileHidingEnabled() )
				optionsSet |= FlatNativeMacLibrary.FC_showsHiddenFiles;

			// filter
			int fileTypeIndex = 0;
			ArrayList<String> fileTypes = new ArrayList<>();
			if( !fc.isDirectorySelectionEnabled() && !fc.hasOnlyAcceptAll() ) {
				fileTypeIndex = fc.indexOfCurrentFilter();
				for( FileFilter filter : fc.getChoosableFileFilters() ) {
					if( filter instanceof FileNameExtensionFilter ) {
						fileTypes.add( filter.getDescription() );
						for( String ext : ((FileNameExtensionFilter)filter).getExtensions() )
							fileTypes.add( ext );
						fileTypes.add( null );
					} else if( filter instanceof AcceptAllFileFilter ) {
						fileTypes.add( filter.getDescription() );
						fileTypes.add( "*" );
						fileTypes.add( null );
					}
				}
			}

			// show system file dialog
			return FlatNativeMacLibrary.showFileChooser( open,
				fc.getDialogTitle(), fc.getApproveButtonText(), null, null, null,
				nameFieldStringValue, directoryURL, optionsSet, optionsClear, null,
				fileTypeIndex, fileTypes.toArray( new String[fileTypes.size()] ) );
		}
	}

	//---- class LinuxFileChooserProvider -------------------------------------

	private static class LinuxFileChooserProvider
		extends SystemFileChooserProvider
	{
		@Override
		String[] showSystemDialog( Window owner, SystemFileChooser fc ) {
			boolean open = (fc.getDialogType() == OPEN_DIALOG);
			String approveButtonText = fc.getApproveButtonText();
			int approveButtonMnemonic = fc.getApproveButtonMnemonic();
			String currentName = null;
			String currentFolder = null;

			// approve button text and mnemonic
			if( approveButtonText != null ) {
				approveButtonText = approveButtonText.replace( "_", "__" );
				if( approveButtonMnemonic > 0 ) {
					int mnemonicIndex = approveButtonText.toUpperCase( Locale.ENGLISH ).indexOf( approveButtonMnemonic );
					if( mnemonicIndex >= 0 ) {
						approveButtonText = approveButtonText.substring( 0, mnemonicIndex )
							+ '_' + approveButtonText.substring( mnemonicIndex );
					}
				}
			}

			// paths
			File currentDirectory = fc.getCurrentDirectory();
			File selectedFile = fc.getSelectedFile();
			if( selectedFile != null ) {
				if( selectedFile.isDirectory() )
					currentFolder = selectedFile.getAbsolutePath();
				else {
					currentName = selectedFile.getName();
					currentFolder = selectedFile.getParent();
				}
			} else if( currentDirectory != null )
				currentFolder = currentDirectory.getAbsolutePath();

			// options
			int optionsSet = FlatNativeLinuxLibrary.FC_do_overwrite_confirmation;
			int optionsClear = 0;
			if( fc.isDirectorySelectionEnabled() )
				optionsSet |= FlatNativeLinuxLibrary.FC_select_folder;
			if( fc.isMultiSelectionEnabled() )
				optionsSet |= FlatNativeLinuxLibrary.FC_select_multiple;
			if( !fc.isFileHidingEnabled() )
				optionsSet |= FlatNativeLinuxLibrary.FC_show_hidden;
			else // necessary because GTK seems to be remember last state and re-use it for new file dialogs
				optionsClear |= FlatNativeLinuxLibrary.FC_show_hidden;

			// filter
			int fileTypeIndex = 0;
			ArrayList<String> fileTypes = new ArrayList<>();
			if( !fc.isDirectorySelectionEnabled() && !fc.hasOnlyAcceptAll() ) {
				fileTypeIndex = fc.indexOfCurrentFilter();
				for( FileFilter filter : fc.getChoosableFileFilters() ) {
					if( filter instanceof FileNameExtensionFilter ) {
						fileTypes.add( filter.getDescription() );
						for( String ext : ((FileNameExtensionFilter)filter).getExtensions() )
							fileTypes.add( caseInsensitiveGlobPattern( ext ) );
						fileTypes.add( null );
					} else if( filter instanceof AcceptAllFileFilter ) {
						fileTypes.add( filter.getDescription() );
						fileTypes.add( "*" );
						fileTypes.add( null );
					}
				}
			}

			// show system file dialog
			return FlatNativeLinuxLibrary.showFileChooser( owner, open,
				fc.getDialogTitle(), approveButtonText, currentName, currentFolder,
				optionsSet, optionsClear, null, fileTypeIndex, fileTypes.toArray( new String[fileTypes.size()] ) );
		}

		private String caseInsensitiveGlobPattern( String ext ) {
			StringBuilder buf = new StringBuilder();
			buf.append( "*." );
			int len = ext.length();
			for( int i = 0; i < len; i++ ) {
				char ch = ext.charAt( i );
				if( Character.isLetter( ch ) ) {
					buf.append( '[' )
						.append( Character.toLowerCase( ch ) )
						.append( Character.toUpperCase( ch ) )
						.append( ']' );
				} else
					buf.append( ch );
			}
			return buf.toString();
		}
	}

	//---- class SwingFileChooserProvider -------------------------------------

	private static class SwingFileChooserProvider
		implements FileChooserProvider
	{
		@Override
		public File[] showDialog( Component parent, SystemFileChooser fc ) {
			JFileChooser chooser = new JFileChooser() {
				@Override
				public void approveSelection() {
					File[] files = isMultiSelectionEnabled()
						? getSelectedFiles()
						: new File[] { getSelectedFile() };

					if( getDialogType() == OPEN_DIALOG || isDirectorySelectionEnabled() ) {
						if( !checkMustExist( this, files ) )
							return;
					} else {
						if( !checkOverwrite( this, files ) )
							return;
					}
					super.approveSelection();
				}
			};

			chooser.setDialogType( fc.getDialogType() );
			chooser.setDialogTitle( fc.getDialogTitle() );
			chooser.setApproveButtonText( fc.getApproveButtonText() );
			chooser.setApproveButtonMnemonic( fc.getApproveButtonMnemonic() );
			chooser.setFileSelectionMode( fc.getFileSelectionMode() );
			chooser.setMultiSelectionEnabled( fc.isMultiSelectionEnabled() );
			chooser.setFileHidingEnabled( fc.isFileHidingEnabled() );

			// system file dialogs do not support multi-selection for Save File dialogs
			if( chooser.isMultiSelectionEnabled() &&
				chooser.getDialogType() == JFileChooser.SAVE_DIALOG &&
				!chooser.isDirectorySelectionEnabled() )
			  chooser.setMultiSelectionEnabled( false );

			// filter
			if( !fc.isDirectorySelectionEnabled() && !fc.hasOnlyAcceptAll() ) {
				FileFilter currentFilter = fc.getFileFilter();
				for( FileFilter filter : fc.getChoosableFileFilters() ) {
					javax.swing.filechooser.FileFilter jfilter = convertFilter( filter, chooser );
					if( jfilter == null )
						continue;

					chooser.addChoosableFileFilter( jfilter );
					if( filter == currentFilter ) {
						chooser.setFileFilter( jfilter );
						currentFilter = null;
					}
				}
				if( currentFilter != null ) {
					javax.swing.filechooser.FileFilter jfilter = convertFilter( currentFilter, chooser );
					if( jfilter != null )
						chooser.setFileFilter( jfilter );
				}
			}

			// paths
			chooser.setCurrentDirectory( fc.getCurrentDirectory() );
			chooser.setSelectedFile( fc.getSelectedFile() );

			if( chooser.showDialog( parent, null ) != JFileChooser.APPROVE_OPTION )
				return null;

			return chooser.isMultiSelectionEnabled()
				? chooser.getSelectedFiles()
				: new File[] { chooser.getSelectedFile() };
		}

		private javax.swing.filechooser.FileFilter convertFilter( FileFilter filter, JFileChooser chooser ) {
			if( filter instanceof FileNameExtensionFilter ) {
				return new javax.swing.filechooser.FileNameExtensionFilter(
					((FileNameExtensionFilter)filter).getDescription(),
					((FileNameExtensionFilter)filter).getExtensions() );
			} else if( filter instanceof AcceptAllFileFilter )
				return chooser.getAcceptAllFileFilter();
			else
				return null;
		}

		private static boolean checkMustExist( JFileChooser chooser, File[] files ) {
			for( File file : files ) {
				if( !file.exists() ) {
					String title = chooser.getDialogTitle();
					JOptionPane.showMessageDialog( chooser,
						file.getName() + (chooser.isDirectorySelectionEnabled()
							? "\nPath does not exist.\nCheck the path and try again."
							: "\nFile not found.\nCheck the file name and try again."),
						(title != null) ? title : "Open",
						JOptionPane.WARNING_MESSAGE );
					return false;
				}
			}
			return true;
		}

		private static boolean checkOverwrite( JFileChooser chooser, File[] files ) {
			for( File file : files ) {
				if( file.exists() ) {
					String title = chooser.getDialogTitle();
					Locale l = chooser.getLocale();
					Object[] options = {
						UIManager.getString( "OptionPane.yesButtonText", l ),
						UIManager.getString( "OptionPane.noButtonText", l ),				};
					int result = JOptionPane.showOptionDialog( chooser,
						file.getName() + " already exists.\nDo you want to replace it?",
						"Confirm " + (title != null ? title : "Save"),
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
						null, options, options[1] );
					return (result == 0);
				}
			}
			return true;
		}
	}

	//---- class FileFilter ---------------------------------------------------

	/** @see javax.swing.filechooser.FileFilter */
	public static abstract class FileFilter {
		/** @see javax.swing.filechooser.FileFilter#getDescription() */
		public abstract String getDescription();
	}

	//---- class FileNameExtensionFilter --------------------------------------

	/** @see javax.swing.filechooser.FileNameExtensionFilter */
	public static final class FileNameExtensionFilter
		extends FileFilter
	{
		private final String description;
		private final String[] extensions;

		/** @see javax.swing.filechooser.FileNameExtensionFilter#FileNameExtensionFilter(String, String...) */
		public FileNameExtensionFilter( String description, String... extensions ) {
			if( extensions == null || extensions.length == 0 )
				throw new IllegalArgumentException( "Missing extensions" );
			for( String extension : extensions ) {
				if( extension == null || extension.isEmpty() )
					throw new IllegalArgumentException( "Extension is null or empty string" );
				if( extension.indexOf( '.' ) >= 0 || extension.indexOf( '*' ) >= 0 )
					throw new IllegalArgumentException( "Extension must not contain '.' or '*'" );
			}

			this.description = description;
			this.extensions = extensions.clone();
		}

		/** @see javax.swing.filechooser.FileNameExtensionFilter#getDescription() */
		@Override
		public String getDescription() {
			return description;
		}

		/** @see javax.swing.filechooser.FileNameExtensionFilter#getExtensions() */
		public String[] getExtensions() {
			return extensions.clone();
		}

		@Override
		public String toString() {
			return super.toString() + "[description=" + description + " extensions=" + Arrays.toString( extensions ) + "]";
		}
	}

	//---- class AcceptAllFileFilter ------------------------------------------

	private static final class AcceptAllFileFilter
		extends FileFilter
	{
		@Override
		public String getDescription() {
			return UIManager.getString( "FileChooser.acceptAllFileFilterText" );
		}
	}
}
