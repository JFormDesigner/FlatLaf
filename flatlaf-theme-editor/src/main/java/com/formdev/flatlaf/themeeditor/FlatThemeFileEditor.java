/*
 * Copyright 2020 FormDev Software GmbH
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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.prefs.Preferences;
import javax.lang.model.SourceVersion;
import javax.swing.*;
import net.miginfocom.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.fonts.roboto_mono.FlatRobotoMonoFont;
import com.formdev.flatlaf.icons.FlatClearIcon;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * TODO
 *
 * @author Karl Tauber
 */
class FlatThemeFileEditor
	extends JFrame
{
	static final String PREFS_ROOT_PATH = "/flatlaf-theme-editor";
	private static final String KEY_DIRECTORIES = "directories";
	private static final String KEY_RECENT_DIRECTORY = "recentDirectory";
	private static final String KEY_RECENT_FILE = "recentFile";
	private static final String KEY_WINDOW_BOUNDS = "windowBounds";
	private static final String KEY_PREVIEW = "preview";
	private static final String KEY_LAF = "laf";
	private static final String KEY_FONT_SIZE_INCR = "fontSizeIncr";
	private static final String KEY_SHOW_HSL_COLORS = "showHslColors";
	private static final String KEY_SHOW_RGB_COLORS = "showRgbColors";
	private static final String KEY_SHOW_COLOR_LUMA = "showColorLuma";

	private File dir;
	private Preferences state;
	private boolean inLoadDirectory;

	private final FlatThemePropertiesBaseManager propertiesBaseManager = new FlatThemePropertiesBaseManager();
	private final JButton newButton;

	static void launch( String[] args ) {
		File dir = (args.length > 0)
			? new File( args[0] )
			: null;

		Locale.setDefault( Locale.ENGLISH );
		System.setProperty( "user.language", "en" );

		SwingUtilities.invokeLater( () -> {
			FlatInterFont.installLazy();
			FlatJetBrainsMonoFont.installLazy();
			FlatRobotoFont.installLazy();
			FlatRobotoMonoFont.installLazy();

			FlatLaf.registerCustomDefaultsSource( "com.formdev.flatlaf.themeeditor" );

			try {
				String laf = Preferences.userRoot().node( PREFS_ROOT_PATH ).get( KEY_LAF, FlatLightLaf.class.getName() );
				UIManager.setLookAndFeel( laf );
			} catch( Exception ex ) {
				FlatLightLaf.setup();
			}

			FlatInspector.install( "ctrl alt shift X" );
			FlatUIDefaultsInspector.install( "ctrl shift alt Y" );

			FlatThemeFileEditor frame = new FlatThemeFileEditor( dir );
			frame.setVisible( true );
		} );
	}

	private FlatThemeFileEditor( File dir ) {
		setIconImages( FlatSVGUtils.createWindowIconImages( "/com/formdev/flatlaf/themeeditor/FlatLaf.svg" ) );

		initComponents();

		directoryField.setRenderer( new DirectoryRenderer( directoryField ) );

		if( UIManager.getLookAndFeel() instanceof FlatDarkLaf )
			darkLafMenuItem.setSelected( true );

		// highlight selected tab
		tabbedPane.setStyle(
			"[light]selectedBackground: lighten($TabbedPane.background,5%);" +
			" [dark]selectedBackground: darken($TabbedPane.background,5%)" );

		// add "+" button to tabbed pane
		newButton = new JButton( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/add.svg" ) );
		newButton.setToolTipText( "New Properties File" );
		newButton.addActionListener( e -> newPropertiesFile() );
		JToolBar trailingToolBar = new JToolBar();
		trailingToolBar.setFloatable( false );
		trailingToolBar.add( newButton );
		tabbedPane.setTrailingComponent( trailingToolBar );

		restoreState();
		restoreWindowBounds();

		addWindowListener( new WindowAdapter() {
			@Override
			public void windowActivated( WindowEvent e ) {
				for( FlatThemeEditorPane themeEditorPane : getThemeEditorPanes() )
					themeEditorPane.windowActivated();
			}
		} );

		// load directory
		if( dir == null ) {
			String recentDirectory = state.get( KEY_RECENT_DIRECTORY, null );
			if( recentDirectory != null )
				dir = new File( recentDirectory );
		}
		if( dir != null && !dir.isDirectory() )
			dir = null;
		if( dir != null )
			loadDirectory( dir );
		else if( directoryField.getSelectedItem() != null )
			loadDirectory( (File) directoryField.getSelectedItem() );

		enableDisableActions();

		// macOS  (see https://www.formdev.com/flatlaf/macos/)
		if( SystemInfo.isMacOS ) {
			// hide menu items that are in macOS application menu
			exitMenuItem.setVisible( false );
			aboutMenuItem.setVisible( false );

			if( SystemInfo.isMacFullWindowContentSupported ) {
				// expand window content into window title bar and make title bar transparent
				rootPane.putClientProperty( "apple.awt.fullWindowContent", true );
				rootPane.putClientProperty( "apple.awt.transparentTitleBar", true );
				rootPane.putClientProperty( FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING, FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE );

				// hide window title
				if( SystemInfo.isJava_17_orLater )
					rootPane.putClientProperty( "apple.awt.windowTitleVisible", false );
				else
					setTitle( null );
			}

			// enable full screen mode for this window (for Java 8 - 10; not necessary for Java 11+)
			if( !SystemInfo.isJava_11_orLater )
				rootPane.putClientProperty( "apple.awt.fullscreenable", true );
		}

		// integrate into macOS screen menu
		FlatDesktop.setAboutHandler( this::about );
		FlatDesktop.setQuitHandler( response -> {
			if( !saveAll() ) {
				response.cancelQuit();
				return;
			}

			saveWindowBounds();
			response.performQuit();
		} );
	}

	private void openDirectory() {
		// save all currently open editors before showing directory chooser
		if( !saveAll() )
			return;

		// choose directory
		JFileChooser chooser = new JFileChooser( dir ) {
			@Override
			public void approveSelection() {
				if( !checkDirectory( this, getSelectedFile() ) )
					return;

				super.approveSelection();
			}
		};
		chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		if( chooser.showOpenDialog( this ) != JFileChooser.APPROVE_OPTION )
			return;

		File selectedFile = chooser.getSelectedFile();
		if( selectedFile == null || selectedFile.equals( dir ) )
			return;

		// open new directory
		loadDirectory( selectedFile );
	}

	private boolean checkDirectory( Component parentComponent, File dir ) {
		if( !dir.isDirectory() ) {
			JOptionPane.showMessageDialog( parentComponent,
				"Directory '" + dir + "' does not exist.",
				getTitle(), JOptionPane.INFORMATION_MESSAGE );
			return false;
		}

		if( getPropertiesFiles( dir ).length == 0 ) {
			UIManager.put( "OptionPane.sameSizeButtons", false );
			int result = JOptionPane.showOptionDialog( parentComponent,
				"Directory '" + dir + "' does not contain properties files.\n\n"
				+ "Do you want create a new theme in this directory?\n\n"
				+ "Or do you want modify/extend core themes and create empty"
				+ " 'FlatLightLaf.properties' and 'FlatDarkLaf.properties' files in this directory?",
				getTitle(), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
				new Object[] { "New Theme", "Modify Core Themes", "Cancel" }, null );
			UIManager.put( "OptionPane.sameSizeButtons", null );

			if( result == 0 )
				return newPropertiesFile( dir );
			else if( result == 1 ) {
				try {
					String content =
						"# To use this in your application, make sure that this properties file\n" +
						"# is included in your application JAR (e.g. in package `com.myapp.themes`)\n" +
						"# and invoke `FlatLaf.registerCustomDefaultsSource( \"com.myapp.themes\" );`\n" +
						"# before setting the look and feel.\n" +
						"# https://www.formdev.com/flatlaf/how-to-customize/#application_properties\n" +
						"\n";
					writeFile( new File( dir, "FlatLightLaf.properties" ), content );
					writeFile( new File( dir, "FlatDarkLaf.properties" ), content );
					return true;
				} catch( IOException ex ) {
					ex.printStackTrace();

					JOptionPane.showMessageDialog( parentComponent,
						"Failed to create 'FlatLightLaf.properties' or 'FlatDarkLaf.properties'." );
				}
			}
			return false;
		}

		return true;
	}

	private void directoryChanged() {
		if( inLoadDirectory )
			return;

		File dir = (File) directoryField.getSelectedItem();
		if( dir == null )
			return;

		if( checkDirectory( this, dir ) )
			loadDirectory( dir );
		else {
			// remove from directories history
			directoryField.removeItem( dir );
			directoryField.setSelectedItem( this.dir.getAbsolutePath() );
			saveState();
		}
	}

	private void loadDirectory( File dir ) {
		dir = getCanonicalFile( dir );
		if( Objects.equals( this.dir, dir ) || !dir.isDirectory() )
			return;

		// save all currently open editors
		if( !saveAll() )
			return;

		this.dir = dir;
		propertiesBaseManager.clear();

		inLoadDirectory = true;

		// close all open editors
		int tabCount = tabbedPane.getTabCount();
		for( int i = tabCount - 1; i >= 0; i-- )
			tabbedPane.removeTabAt( i );

		// update directory field
		DefaultComboBoxModel<File> model = (DefaultComboBoxModel<File>) directoryField.getModel();
		int indexOf = model.getIndexOf( dir );
		if( indexOf < 0 )
			model.addElement( dir );
		directoryField.setSelectedItem( dir );

		// open all properties files in directory
		String recentFile = state.get( KEY_RECENT_FILE, null );
		for( File file : getPropertiesFiles( dir ) )
			openFile( file, file.getName().equals( recentFile ) );

		SwingUtilities.invokeLater( () -> {
			activateEditor();
			notifyEditorSelected();
		} );
		saveState();
		enableDisableActions();

		inLoadDirectory = false;
	}

	private void updateDirectory() {
		if( dir == null )
			return;

		// update open tabs and remove tabs if file was removed
		HashSet<File> openFiles = new HashSet<>();
		FlatThemeEditorPane[] themeEditorPanes = getThemeEditorPanes();
		for( int i = themeEditorPanes.length - 1; i >= 0; i-- ) {
			FlatThemeEditorPane themeEditorPane = themeEditorPanes[i];
			if( themeEditorPane.reloadIfNecessary() )
				openFiles.add( themeEditorPane.getFile() );
			else
				tabbedPane.removeTabAt( i );
		}

		// open newly created files
		for( File file : getPropertiesFiles( dir ) ) {
			if( !openFiles.contains( file ) )
				openFile( file, false );
		}
	}

	private File getCanonicalFile( File dir ) {
		if( dir == null )
			return null;

		try {
			return dir.getCanonicalFile();
		} catch( IOException ex ) {
			return dir.getAbsoluteFile();
		}
	}

	private File[] getPropertiesFiles( File dir ) {
		File[] propertiesFiles = dir.listFiles( (d, name) -> {
			return name.endsWith( ".properties" );
		} );
		Arrays.sort( propertiesFiles, (f1, f2) -> {
			String n1 = toSortName( f1.getName() );
			String n2 = toSortName( f2.getName() );
			return n1.compareToIgnoreCase( n2 );
		} );

		File themesDir = new File( dir, "themes" );
		if( !themesDir.isDirectory() )
			return propertiesFiles;

		// get files from "themes" subdirectory
		File[] themesFiles = getPropertiesFiles( themesDir );
		File[] allFiles = new File[propertiesFiles.length + themesFiles.length];
		System.arraycopy( propertiesFiles, 0, allFiles, 0, propertiesFiles.length );
		System.arraycopy( themesFiles, 0, allFiles, propertiesFiles.length, themesFiles.length );
		return allFiles;
	}

	private String toSortName( String name ) {
		switch( name ) {
			case "FlatLaf.properties":			return "\0\0";
			case "FlatLightLaf.properties":		return "\0\1";
			case "FlatDarkLaf.properties":		return "\0\2";
			case "FlatIntelliJLaf.properties":	return "\0\3";
			case "FlatDarculaLaf.properties":	return "\0\4";
			case "FlatMacLightLaf.properties":	return "\0\5";
			case "FlatMacDarkLaf.properties":	return "\0\6";
			default:							return name;
		}
	}

	private void openFile( File file, boolean select ) {
		FlatThemeEditorPane themeEditorPane = new FlatThemeEditorPane();
		themeEditorPane.updateFontSize( getFontSizeIncr() );
		try {
			themeEditorPane.load( file );
		} catch( IOException ex ) {
			ex.printStackTrace(); // TODO
		}

		themeEditorPane.initBasePropertyProvider( propertiesBaseManager );

		Supplier<String> titleFun = () -> {
			return (themeEditorPane.isDirty() ? "* " : "")
				+ StringUtils.removeTrailing( themeEditorPane.getFile().getName(), ".properties" );
		};
		themeEditorPane.addPropertyChangeListener( FlatThemeEditorPane.DIRTY_PROPERTY, e -> {
			int index = tabbedPane.indexOfComponent( themeEditorPane );
			if( index >= 0 )
				tabbedPane.setTitleAt( index, titleFun.get() );
		} );

		if( state.getBoolean( KEY_PREVIEW, true ) )
			themeEditorPane.showPreview( true );

		tabbedPane.addTab( titleFun.get(), null, themeEditorPane, file.getAbsolutePath() );

		if( select )
			tabbedPane.setSelectedComponent( themeEditorPane );
	}

	private void selectedTabChanged() {
		if( inLoadDirectory )
			return;

		enableDisableActions();

		FlatThemeEditorPane themeEditorPane = (FlatThemeEditorPane) tabbedPane.getSelectedComponent();
		String filename = (themeEditorPane != null) ? themeEditorPane.getFile().getName() : null;
		putPrefsString( state, KEY_RECENT_FILE, filename );

		notifyEditorSelected();
	}

	private void enableDisableActions() {
		boolean dirOpen = (directoryField.getSelectedItem() != null);
		boolean editorOpen = (dirOpen && tabbedPane.getSelectedIndex() >= 0);

		// enable/disable buttons
		newButton.setEnabled( dirOpen );

		// enable/disable menu items
		newPropertiesFileMenuItem.setEnabled( dirOpen );
		saveAllMenuItem.setEnabled( editorOpen );
		findMenuItem.setEnabled( editorOpen );
		insertColorMenuItem.setEnabled( editorOpen );
		activateEditorMenuItem.setEnabled( editorOpen );
		nextEditorMenuItem.setEnabled( editorOpen );
		previousEditorMenuItem.setEnabled( editorOpen );
	}

	private boolean newPropertiesFile() {
		return newPropertiesFile( dir );
	}

	private boolean newPropertiesFile( File dir ) {
		String title = "New Properties File";
		JTextField themeNameField = new JTextField();
		JComboBox<String> baseThemeField = new JComboBox<>( new String[] {
			FlatLightLaf.NAME,
			FlatDarkLaf.NAME,
			FlatIntelliJLaf.NAME,
			FlatDarculaLaf.NAME,
			FlatMacLightLaf.NAME,
			FlatMacDarkLaf.NAME,
		} );
		JCheckBox genJavaClassCheckBox = new JCheckBox( "Generate Java class" );
		genJavaClassCheckBox.setMnemonic( 'G' );

		File themesDir = new File( dir, "themes" );
		JCheckBox useThemesDirCheckBox = themesDir.isDirectory()
			? new JCheckBox( "Create in 'themes' directory", true )
			: null;

		JOptionPane optionPane = new JOptionPane( new Object[] {
			new JLabel( "Theme name:" ),
			themeNameField,
			new JLabel( "Base Theme:" ),
			baseThemeField,
			genJavaClassCheckBox,
			useThemesDirCheckBox,
		}, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION ) {
			@Override
			public void selectInitialValue() {
				super.selectInitialValue();
				themeNameField.requestFocusInWindow();
			}

			@Override
			public void setValue( Object newValue ) {
				if( Objects.equals( newValue, JOptionPane.OK_OPTION ) ) {
					String themeName = themeNameField.getText().trim();
					if( themeName.isEmpty() )
						return;

					if( !SourceVersion.isIdentifier( themeName ) ) {
						JOptionPane.showMessageDialog( this,
							"'" + themeName + "' is not a valid Java identifier.",
							title, JOptionPane.INFORMATION_MESSAGE );
						return;
					}

					File dir2 = (useThemesDirCheckBox != null && useThemesDirCheckBox.isSelected()) ? themesDir : dir;
					File file = new File( dir2, themeName + ".properties" );
					if( file.exists() ) {
						JOptionPane.showMessageDialog( this, "Theme '" + themeName + "' already exists.", title, JOptionPane.INFORMATION_MESSAGE );
						return;
					}

					try {
						String baseTheme = (String) baseThemeField.getSelectedItem();
						createTheme( file, baseTheme );
						if( genJavaClassCheckBox.isSelected() )
							createThemeClass( dir2, themeName, baseTheme );
						openFile( file, true );
					} catch( IOException ex ) {
						ex.printStackTrace();

						JOptionPane.showMessageDialog( this,
							"Failed to create '" + file + "'." );
						return;
					}
				}

				super.setValue( newValue );
			}
		};

		JDialog dialog = optionPane.createDialog( this, title );
		dialog.setVisible( true );

		return Objects.equals( optionPane.getValue(), JOptionPane.OK_OPTION );
	}

	private void createTheme( File file, String baseTheme )
		throws IOException
	{
		StringBuilder buf = new StringBuilder();
		buf.append( "# base theme (light, dark, intellij, darcula, maclight or macdark); only used by theme editor\n" );
		switch( baseTheme ) {
			case FlatLightLaf.NAME:		buf.append( "@baseTheme = light\n" ); break;
			case FlatDarkLaf.NAME:		buf.append( "@baseTheme = dark\n" ); break;
			case FlatIntelliJLaf.NAME:	buf.append( "@baseTheme = intellij\n" ); break;
			case FlatDarculaLaf.NAME:	buf.append( "@baseTheme = darcula\n" ); break;
			case FlatMacLightLaf.NAME:	buf.append( "@baseTheme = maclight\n" ); break;
			case FlatMacDarkLaf.NAME:	buf.append( "@baseTheme = macdark\n" ); break;
		}

		writeFile( file, buf.toString() );
	}

	private void createThemeClass( File dir, String themeName, String baseTheme )
		throws IOException
	{
		// search for "resources" parent directory that has "java" directory at same level
		File classDir = dir;
		String subPath = null;
		String pkg = null;
		for( File d = dir; d != null; d = d.getParentFile() ) {
			String name = d.getName();
			if( name.equals( "resources" ) ) {
				File javaDir = new File( d.getParentFile(), "java" );
				if( javaDir.isDirectory() ) {
					classDir = new File( javaDir, subPath );
					classDir.mkdirs();
					pkg = subPath.replace( '/', '.' );
				}
				break;
			}
			subPath = (subPath != null) ? (name + '/' + subPath) : name;
		}

		// search for "java" or "src" parent directories for package statement
		if( pkg == null ) {
			String pkg2 = null;
			for( File d = dir; d != null; d = d.getParentFile() ) {
				String name = d.getName();
				if( name.equals( "java" ) || name.equals( "src" )) {
					pkg = pkg2;
					break;
				}
				pkg2 = (pkg2 != null) ? (name + '.' + pkg2) : name;
			}
		}

		// do not overwrite exiting class
		File file = new File( classDir, themeName + ".java" );
		if( file.exists() )
			return;

		String themeBaseClass;
		switch( baseTheme ) {
			default:
			case FlatLightLaf.NAME:		themeBaseClass = "FlatLightLaf"; break;
			case FlatDarkLaf.NAME:		themeBaseClass = "FlatDarkLaf"; break;
			case FlatIntelliJLaf.NAME:	themeBaseClass = "FlatIntelliJLaf"; break;
			case FlatDarculaLaf.NAME:	themeBaseClass = "FlatDarculaLaf"; break;
			case FlatMacLightLaf.NAME:	themeBaseClass = "FlatMacLightLaf"; break;
			case FlatMacDarkLaf.NAME:	themeBaseClass = "FlatMacDarkLaf"; break;
		}

		String themeBasePackage = "";
		switch( baseTheme ) {
			case FlatMacLightLaf.NAME:
			case FlatMacDarkLaf.NAME:
				themeBasePackage = "themes.";
				break;
		}

		String pkgStmt = (pkg != null) ? "package " + pkg + ";\n\n" : "";
		String classBody = CLASS_TEMPLATE
			.replace( "${themeClass}", themeName )
			.replace( "${themeBasePackage}", themeBasePackage )
			.replace( "${themeBaseClass}", themeBaseClass );

		writeFile( file, pkgStmt + classBody );
	}

	private static final String CLASS_TEMPLATE =
		"import com.formdev.flatlaf.${themeBasePackage}${themeBaseClass};\n" +
		"\n" +
		"public class ${themeClass}\n" +
		"	extends ${themeBaseClass}\n" +
		"{\n" +
		"	public static final String NAME = \"${themeClass}\";\n" +
		"\n" +
		"	public static boolean setup() {\n" +
		"		return setup( new ${themeClass}() );\n" +
		"	}\n" +
		"\n" +
		"	public static void installLafInfo() {\n" +
		"		installLafInfo( NAME, ${themeClass}.class );\n" +
		"	}\n" +
		"\n" +
		"	@Override\n" +
		"	public String getName() {\n" +
		"		return NAME;\n" +
		"	}\n" +
		"}\n";

	private static void writeFile( File file, String content )
		throws IOException
	{
		try(
			FileOutputStream out = new FileOutputStream( file );
			Writer writer = new OutputStreamWriter( out, "UTF-8" );
		) {
			writer.write( content );
		}
	}

	private boolean saveAll() {
		for( FlatThemeEditorPane themeEditorPane : getThemeEditorPanes() ) {
			if( !themeEditorPane.saveIfDirty() )
				return false;
		}
		return true;
	}

	private void exit() {
		if( !saveAll() )
			return;

		saveWindowBounds();
		System.exit( 0 );
	}

	private void windowClosing() {
		exit();
	}

	private void windowActivated() {
		updateDirectory();
	}

	private void windowDeactivated() {
		saveAll();
	}

	private FlatThemeEditorPane[] getThemeEditorPanes() {
		FlatThemeEditorPane[] result = new FlatThemeEditorPane[tabbedPane.getTabCount()];
		for( int i = 0; i < result.length; i++ )
			result[i] = (FlatThemeEditorPane) tabbedPane.getComponentAt( i );
		return result;
	}

	private void notifyEditorSelected() {
		FlatThemeEditorPane themeEditorPane = (FlatThemeEditorPane) tabbedPane.getSelectedComponent();
		if( themeEditorPane != null )
			themeEditorPane.selected();
	}

	private void activateEditor() {
		FlatThemeEditorPane themeEditorPane = (FlatThemeEditorPane) tabbedPane.getSelectedComponent();
		if( themeEditorPane != null )
			themeEditorPane.requestFocusInWindow();
	}

	private void nextEditor() {
		notifyTabbedPaneAction( tabbedPane.getActionMap().get( "navigatePageDown" ) );
	}

	private void previousEditor() {
		notifyTabbedPaneAction( tabbedPane.getActionMap().get( "navigatePageUp" ) );
	}

	private void notifyTabbedPaneAction( Action action ) {
		if( action != null && action.isEnabled() )
			action.actionPerformed( new ActionEvent( tabbedPane, ActionEvent.ACTION_PERFORMED, null ) );
	}

	private void find() {
		FlatThemeEditorPane themeEditorPane = (FlatThemeEditorPane) tabbedPane.getSelectedComponent();
		if( themeEditorPane != null )
			themeEditorPane.showFindReplaceBar( true );
	}

	private void insertColor() {
		FlatThemeEditorPane themeEditorPane = (FlatThemeEditorPane) tabbedPane.getSelectedComponent();
		if( themeEditorPane != null )
			themeEditorPane.notifyTextAreaAction( FlatSyntaxTextAreaActions.insertColorAction );
	}

	private void pickColor() {
		FlatThemeEditorPane themeEditorPane = (FlatThemeEditorPane) tabbedPane.getSelectedComponent();
		if( themeEditorPane != null )
			themeEditorPane.notifyTextAreaAction( FlatSyntaxTextAreaActions.pickColorAction );
	}

	private void showHidePreview() {
		boolean show = previewMenuItem.isSelected();
		for( FlatThemeEditorPane themeEditorPane : getThemeEditorPanes() )
			themeEditorPane.showPreview( show );
		putPrefsBoolean( state, KEY_PREVIEW, show, true );
	}

	private void lightLaf() {
		applyLookAndFeel( FlatLightLaf.class.getName() );
	}

	private void darkLaf() {
		applyLookAndFeel( FlatDarkLaf.class.getName() );
	}

	private void applyLookAndFeel( String lafClassName ) {
		if( UIManager.getLookAndFeel().getClass().getName().equals( lafClassName ) )
			return;

		try {
			UIManager.setLookAndFeel( lafClassName );
			FlatLaf.updateUI();
			for( FlatThemeEditorPane themeEditorPane : getThemeEditorPanes() )
				themeEditorPane.updateTheme();
			state.put( KEY_LAF, lafClassName );
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	private void incrFontSize() {
		applyFontSizeIncr( getFontSizeIncr() + 1 );
	}

	private void decrFontSize() {
		applyFontSizeIncr( getFontSizeIncr() - 1 );
	}

	private void resetFontSize() {
		applyFontSizeIncr( 0 );
	}

	private void applyFontSizeIncr( int sizeIncr ) {
		if( sizeIncr < -5 )
			sizeIncr = -5;
		if( sizeIncr == getFontSizeIncr() )
			return;

		for( FlatThemeEditorPane themeEditorPane : getThemeEditorPanes() )
			themeEditorPane.updateFontSize( sizeIncr );
		state.putInt( KEY_FONT_SIZE_INCR, sizeIncr );
	}

	private int getFontSizeIncr() {
		return state.getInt( KEY_FONT_SIZE_INCR, 0 );
	}

	private void colorModelChanged() {
		FlatThemeEditorOverlay.showHSL = showHSLColorsMenuItem.isSelected();
		FlatThemeEditorOverlay.showRGB = showRGBColorsMenuItem.isSelected();
		FlatThemeEditorOverlay.showLuma = showColorLumaMenuItem.isSelected();

		putPrefsBoolean( state, KEY_SHOW_HSL_COLORS, FlatThemeEditorOverlay.showHSL, true );
		putPrefsBoolean( state, KEY_SHOW_RGB_COLORS, FlatThemeEditorOverlay.showRGB, false );
		putPrefsBoolean( state, KEY_SHOW_COLOR_LUMA, FlatThemeEditorOverlay.showLuma, false );

		repaint();
	}

	private void about() {
		JLabel titleLabel = new JLabel( "FlatLaf Theme Editor" );
		titleLabel.putClientProperty( FlatClientProperties.STYLE_CLASS, "h1" );

		String link = "https://www.formdev.com/flatlaf/";
		JLabel linkLabel = new JLabel( "<html><a href=\"#\">" + link + "</a></html>" );
		linkLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		linkLabel.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent e ) {
				try {
					Desktop.getDesktop().browse( new URI( link ) );
				} catch( IOException | URISyntaxException ex ) {
					JOptionPane.showMessageDialog( linkLabel,
						"Failed to open '" + link + "' in browser.",
						"About", JOptionPane.PLAIN_MESSAGE );
				}
			}
		} );


		JOptionPane.showMessageDialog( this,
			new Object[] {
				titleLabel,
				"Edits FlatLaf Swing look and feel theme files",
				" ",
				"Copyright 2019-" + Year.now() + " FormDev Software GmbH",
				linkLabel,
			},
			"About", JOptionPane.PLAIN_MESSAGE );
	}

	private void restoreState() {
		state = Preferences.userRoot().node( PREFS_ROOT_PATH );

		// restore directories history
		String[] directories = getPrefsStrings( state, KEY_DIRECTORIES );
		SortedComboBoxModel<File> model = new SortedComboBoxModel<>( new File[0],
			(file1, file2) -> {
				// replace path separator with zero to order shorter names before longer
				// (e.g. c:\dir\sub before c:\dir2\sub)
				String path1 = file1.getPath().replace( '/', '\0' ).replace( '\\', '\0' );
				String path2 = file2.getPath().replace( '/', '\0' ).replace( '\\', '\0' );
				return path1.compareToIgnoreCase( path2 );
			} );
		for( String dirStr : directories ) {
			File dir = new File( dirStr );
			if( dir.isDirectory() )
				model.addElement( dir );
		}
		directoryField.setModel( model );

		// restore overlay color models
		FlatThemeEditorOverlay.showHSL = state.getBoolean( KEY_SHOW_HSL_COLORS, true );
		FlatThemeEditorOverlay.showRGB = state.getBoolean( KEY_SHOW_RGB_COLORS, false );
		FlatThemeEditorOverlay.showLuma = state.getBoolean( KEY_SHOW_COLOR_LUMA, false );

		// restore menu item selection
		previewMenuItem.setSelected( state.getBoolean( KEY_PREVIEW, true ) );
		showHSLColorsMenuItem.setSelected( FlatThemeEditorOverlay.showHSL );
		showRGBColorsMenuItem.setSelected( FlatThemeEditorOverlay.showRGB );
		showColorLumaMenuItem.setSelected( FlatThemeEditorOverlay.showLuma );
	}

	private void saveState() {
		// save directories history
		ComboBoxModel<File> model = directoryField.getModel();
		String[] directories = new String[model.getSize()];
		for( int i = 0; i < directories.length; i++ )
			directories[i] = model.getElementAt( i ).getAbsolutePath();
		putPrefsStrings( state, KEY_DIRECTORIES, directories );

		// save recent directory
		putPrefsString( state, KEY_RECENT_DIRECTORY, dir.getAbsolutePath() );
	}

	private void restoreWindowBounds() {
		String windowBoundsStr = state.get( KEY_WINDOW_BOUNDS, null );
		if( windowBoundsStr != null ) {
			List<String> list = StringUtils.split( windowBoundsStr, ',' );
			if( list.size() >= 4 ) {
				try {
					int x = UIScale.scale( Integer.parseInt( list.get( 0 ) ) );
					int y = UIScale.scale( Integer.parseInt( list.get( 1 ) ) );
					int w = UIScale.scale( Integer.parseInt( list.get( 2 ) ) );
					int h = UIScale.scale( Integer.parseInt( list.get( 3 ) ) );

					// limit to screen size
					GraphicsConfiguration gc = getGraphicsConfiguration();
					if( gc != null ) {
						Rectangle screenBounds = gc.getBounds();
						Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets( gc );
						Rectangle r = FlatUIUtils.subtractInsets( screenBounds, screenInsets );

						w = Math.min( w, r.width );
						h = Math.min( h, r.height );
						x = Math.max( Math.min( x, r.width - w ), r.x );
						y = Math.max( Math.min( y, r.height - h ), r.y );
					}

					// On macOS, the window may be empty if it spans the whole screen height
					// and client property apple.awt.fullWindowContent is set to true.
					// Invoking addNotify() before setting window bounds fixes this issue.
					if( SystemInfo.isMacOS && !isDisplayable() )
						addNotify();

					setBounds( x, y, w, h );
					return;
				} catch( NumberFormatException ex ) {
					// ignore
				}
			}
		}

		// default window size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize( Math.min( UIScale.scale( 800 ), screenSize.width ),
			screenSize.height - UIScale.scale( 100 ) );
		setLocationRelativeTo( null );
	}

	private void saveWindowBounds() {
		Rectangle r = getBounds();
		int x = UIScale.unscale( r.x );
		int y = UIScale.unscale( r.y );
		int width = UIScale.unscale( r.width );
		int height = UIScale.unscale( r.height );
		state.put( KEY_WINDOW_BOUNDS, x + "," + y + ',' + width + ',' + height );
	}

	static void putPrefsBoolean( Preferences prefs, String key, boolean value, boolean defaultValue ) {
		if( value != defaultValue )
			prefs.putBoolean( key, value );
		else
			prefs.remove( key );
	}

	static void putPrefsString( Preferences prefs, String key, String value ) {
		if( !StringUtils.isEmpty( value ) )
			prefs.put( key, value );
		else
			prefs.remove( key );
	}

	private static String[] getPrefsStrings( Preferences prefs, String key ) {
		ArrayList<String> arr = new ArrayList<>();
		for( int i = 0; i < 10000; i++ ) {
			String s = prefs.get( key+(i+1), null );
			if( s == null )
				break;
			arr.add( s );
		}
		return arr.toArray( new String[arr.size()] );
	}

	private static void putPrefsStrings( Preferences prefs, String key, String[] strings ) {
		for( int i = 0; i < strings.length; i++ )
			prefs.put( key+(i+1), strings[i] );

		for( int i = strings.length; prefs.get( key+(i+1), null ) != null; i++ )
			prefs.remove( key+(i+1) );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		menuBar = new JMenuBar();
		fileMenu = new JMenu();
		openDirectoryMenuItem = new JMenuItem();
		newPropertiesFileMenuItem = new JMenuItem();
		saveAllMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		editMenu = new JMenu();
		findMenuItem = new JMenuItem();
		insertColorMenuItem = new JMenuItem();
		pickColorMenuItem = new JMenuItem();
		viewMenu = new JMenu();
		previewMenuItem = new JCheckBoxMenuItem();
		lightLafMenuItem = new JRadioButtonMenuItem();
		darkLafMenuItem = new JRadioButtonMenuItem();
		incrFontSizeMenuItem = new JMenuItem();
		decrFontSizeMenuItem = new JMenuItem();
		resetFontSizeMenuItem = new JMenuItem();
		showHSLColorsMenuItem = new JCheckBoxMenuItem();
		showRGBColorsMenuItem = new JCheckBoxMenuItem();
		showColorLumaMenuItem = new JCheckBoxMenuItem();
		windowMenu = new JMenu();
		activateEditorMenuItem = new JMenuItem();
		nextEditorMenuItem = new JMenuItem();
		previousEditorMenuItem = new JMenuItem();
		helpMenu = new JMenu();
		aboutMenuItem = new JMenuItem();
		controlPanel = new JPanel();
		JPanel macFullWindowContentButtonsPlaceholder = new JPanel();
		directoryLabel = new JLabel();
		directoryField = new FlatThemeFileEditor.DirectoryComboBox();
		openDirectoryButton = new JButton();
		tabbedPane = new FlatTabbedPane();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("FlatLaf Theme Editor");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				FlatThemeFileEditor.this.windowActivated();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				FlatThemeFileEditor.this.windowClosing();
			}
			@Override
			public void windowDeactivated(WindowEvent e) {
				FlatThemeFileEditor.this.windowDeactivated();
			}
		});
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== menuBar ========
		{

			//======== fileMenu ========
			{
				fileMenu.setText("File");

				//---- openDirectoryMenuItem ----
				openDirectoryMenuItem.setText("Open Directory...");
				openDirectoryMenuItem.setMnemonic('O');
				openDirectoryMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				openDirectoryMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/themeeditor/icons/menu-open.svg"));
				openDirectoryMenuItem.addActionListener(e -> openDirectory());
				fileMenu.add(openDirectoryMenuItem);

				//---- newPropertiesFileMenuItem ----
				newPropertiesFileMenuItem.setText("New Properties File...");
				newPropertiesFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				newPropertiesFileMenuItem.setMnemonic('N');
				newPropertiesFileMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/themeeditor/icons/add.svg"));
				newPropertiesFileMenuItem.addActionListener(e -> newPropertiesFile());
				fileMenu.add(newPropertiesFileMenuItem);

				//---- saveAllMenuItem ----
				saveAllMenuItem.setText("Save All");
				saveAllMenuItem.setMnemonic('S');
				saveAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				saveAllMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/themeeditor/icons/menu-saveall.svg"));
				saveAllMenuItem.addActionListener(e -> saveAll());
				fileMenu.add(saveAllMenuItem);
				fileMenu.addSeparator();

				//---- exitMenuItem ----
				exitMenuItem.setText("Exit");
				exitMenuItem.setMnemonic('X');
				exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				exitMenuItem.addActionListener(e -> exit());
				fileMenu.add(exitMenuItem);
			}
			menuBar.add(fileMenu);

			//======== editMenu ========
			{
				editMenu.setText("Edit");
				editMenu.setMnemonic('E');

				//---- findMenuItem ----
				findMenuItem.setText("Find/Replace");
				findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				findMenuItem.setMnemonic('F');
				findMenuItem.addActionListener(e -> find());
				editMenu.add(findMenuItem);
				editMenu.addSeparator();

				//---- insertColorMenuItem ----
				insertColorMenuItem.setText("Insert Color");
				insertColorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				insertColorMenuItem.addActionListener(e -> insertColor());
				editMenu.add(insertColorMenuItem);

				//---- pickColorMenuItem ----
				pickColorMenuItem.setText("Pick Color from Screen");
				pickColorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()|KeyEvent.SHIFT_DOWN_MASK));
				pickColorMenuItem.addActionListener(e -> pickColor());
				editMenu.add(pickColorMenuItem);
			}
			menuBar.add(editMenu);

			//======== viewMenu ========
			{
				viewMenu.setText("View");
				viewMenu.setMnemonic('V');

				//---- previewMenuItem ----
				previewMenuItem.setText("Preview");
				previewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				previewMenuItem.addActionListener(e -> showHidePreview());
				viewMenu.add(previewMenuItem);
				viewMenu.addSeparator();

				//---- lightLafMenuItem ----
				lightLafMenuItem.setText("Light Laf");
				lightLafMenuItem.setMnemonic('L');
				lightLafMenuItem.setSelected(true);
				lightLafMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.ALT_DOWN_MASK));
				lightLafMenuItem.addActionListener(e -> lightLaf());
				viewMenu.add(lightLafMenuItem);

				//---- darkLafMenuItem ----
				darkLafMenuItem.setText("Dark Laf");
				darkLafMenuItem.setMnemonic('D');
				darkLafMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, KeyEvent.ALT_DOWN_MASK));
				darkLafMenuItem.addActionListener(e -> darkLaf());
				viewMenu.add(darkLafMenuItem);
				viewMenu.addSeparator();

				//---- incrFontSizeMenuItem ----
				incrFontSizeMenuItem.setText("Increase Font Size");
				incrFontSizeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				incrFontSizeMenuItem.addActionListener(e -> incrFontSize());
				viewMenu.add(incrFontSizeMenuItem);

				//---- decrFontSizeMenuItem ----
				decrFontSizeMenuItem.setText("Decrease Font Size");
				decrFontSizeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				decrFontSizeMenuItem.addActionListener(e -> decrFontSize());
				viewMenu.add(decrFontSizeMenuItem);

				//---- resetFontSizeMenuItem ----
				resetFontSizeMenuItem.setText("Reset Font Size");
				resetFontSizeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				resetFontSizeMenuItem.addActionListener(e -> resetFontSize());
				viewMenu.add(resetFontSizeMenuItem);
				viewMenu.addSeparator();

				//---- showHSLColorsMenuItem ----
				showHSLColorsMenuItem.setText("Show HSL colors");
				showHSLColorsMenuItem.addActionListener(e -> colorModelChanged());
				viewMenu.add(showHSLColorsMenuItem);

				//---- showRGBColorsMenuItem ----
				showRGBColorsMenuItem.setText("Show RGB colors (hex)");
				showRGBColorsMenuItem.addActionListener(e -> colorModelChanged());
				viewMenu.add(showRGBColorsMenuItem);

				//---- showColorLumaMenuItem ----
				showColorLumaMenuItem.setText("Show color luma");
				showColorLumaMenuItem.addActionListener(e -> colorModelChanged());
				viewMenu.add(showColorLumaMenuItem);
			}
			menuBar.add(viewMenu);

			//======== windowMenu ========
			{
				windowMenu.setText("Window");
				windowMenu.setMnemonic('W');

				//---- activateEditorMenuItem ----
				activateEditorMenuItem.setText("Activate Editor");
				activateEditorMenuItem.setMnemonic('A');
				activateEditorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
				activateEditorMenuItem.addActionListener(e -> activateEditor());
				windowMenu.add(activateEditorMenuItem);

				//---- nextEditorMenuItem ----
				nextEditorMenuItem.setText("Next Editor");
				nextEditorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				nextEditorMenuItem.setMnemonic('N');
				nextEditorMenuItem.addActionListener(e -> nextEditor());
				windowMenu.add(nextEditorMenuItem);

				//---- previousEditorMenuItem ----
				previousEditorMenuItem.setText("Previous Editor");
				previousEditorMenuItem.setMnemonic('P');
				previousEditorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				previousEditorMenuItem.addActionListener(e -> previousEditor());
				windowMenu.add(previousEditorMenuItem);
			}
			menuBar.add(windowMenu);

			//======== helpMenu ========
			{
				helpMenu.setText("Help");
				helpMenu.setMnemonic('H');

				//---- aboutMenuItem ----
				aboutMenuItem.setText("About");
				aboutMenuItem.setMnemonic('A');
				aboutMenuItem.addActionListener(e -> about());
				helpMenu.add(aboutMenuItem);
			}
			menuBar.add(helpMenu);
		}
		setJMenuBar(menuBar);

		//======== controlPanel ========
		{
			controlPanel.setLayout(new MigLayout(
				"insets panel,hidemode 3",
				// columns
				"[fill]" +
				"[grow,fill]" +
				"[fill]",
				// rows
				"[]"));

			//======== macFullWindowContentButtonsPlaceholder ========
			{
				macFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
			}
			controlPanel.add(macFullWindowContentButtonsPlaceholder, "west");

			//---- directoryLabel ----
			directoryLabel.setText("Directory:");
			controlPanel.add(directoryLabel, "cell 0 0");

			//---- directoryField ----
			directoryField.setEditable(false);
			directoryField.setFocusable(false);
			directoryField.setMaximumRowCount(30);
			directoryField.addActionListener(e -> directoryChanged());
			controlPanel.add(directoryField, "cell 1 0");

			//---- openDirectoryButton ----
			openDirectoryButton.setFocusable(false);
			openDirectoryButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/themeeditor/icons/menu-open.svg"));
			openDirectoryButton.addActionListener(e -> openDirectory());
			controlPanel.add(openDirectoryButton, "cell 2 0");
		}
		contentPane.add(controlPanel, BorderLayout.NORTH);

		//======== tabbedPane ========
		{
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			tabbedPane.setFocusable(false);
			tabbedPane.addChangeListener(e -> selectedTabChanged());
		}
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		//---- lafButtonGroup ----
		ButtonGroup lafButtonGroup = new ButtonGroup();
		lafButtonGroup.add(lightLafMenuItem);
		lafButtonGroup.add(darkLafMenuItem);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		// on macOS, panel on left side of control bar is a placeholder for title bar buttons in fullWindowContent mode
		macFullWindowContentButtonsPlaceholder.putClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "mac" );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem openDirectoryMenuItem;
	private JMenuItem newPropertiesFileMenuItem;
	private JMenuItem saveAllMenuItem;
	private JMenuItem exitMenuItem;
	private JMenu editMenu;
	private JMenuItem findMenuItem;
	private JMenuItem insertColorMenuItem;
	private JMenuItem pickColorMenuItem;
	private JMenu viewMenu;
	private JCheckBoxMenuItem previewMenuItem;
	private JRadioButtonMenuItem lightLafMenuItem;
	private JRadioButtonMenuItem darkLafMenuItem;
	private JMenuItem incrFontSizeMenuItem;
	private JMenuItem decrFontSizeMenuItem;
	private JMenuItem resetFontSizeMenuItem;
	private JCheckBoxMenuItem showHSLColorsMenuItem;
	private JCheckBoxMenuItem showRGBColorsMenuItem;
	private JCheckBoxMenuItem showColorLumaMenuItem;
	private JMenu windowMenu;
	private JMenuItem activateEditorMenuItem;
	private JMenuItem nextEditorMenuItem;
	private JMenuItem previousEditorMenuItem;
	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	private JPanel controlPanel;
	private JLabel directoryLabel;
	private JComboBox<File> directoryField;
	private JButton openDirectoryButton;
	private FlatTabbedPane tabbedPane;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class SortedComboBoxModel ------------------------------------------

	private static class SortedComboBoxModel<E>
		extends DefaultComboBoxModel<E>
	{
		private final Comparator<E> comparator;

		SortedComboBoxModel( E[] items, Comparator<E> c ) {
			super( sort( items, c ) );
			this.comparator = c;
		}

		@Override
		public void addElement( E obj ) {
			if( getSize() == 0 ) {
				super.addElement( obj );
			} else {
				int index = binarySearch( this, obj, comparator );
				insertElementAt( obj, (index < 0) ? (-index - 1) : index );
			}
		}

		static <E> E[] sort( E[] items, Comparator<E> c ) {
			// clone array
			items = items.clone();

			Arrays.sort( items, c );
			return items;
		}

		@SuppressWarnings("unchecked")
		static <E> int binarySearch( ListModel<E> model, E key, Comparator<E> c ) {
			int low = 0;
			int high = model.getSize() - 1;

			while( low <= high ) {
				int mid = (low + high) / 2;
				E midVal = model.getElementAt( mid );
				int cmp;
				if( c != null )
					cmp = c.compare( midVal, key );
				else
					cmp = ((Comparable<E>)midVal).compareTo( key );

				if( cmp < 0 )
					low = mid + 1;
				else if( cmp > 0 )
					high = mid - 1;
				else
					return mid; // found
			}

			// not found
			return -(low + 1);
	    }
	}

	//---- class DirectoryComboBox --------------------------------------------

	private class DirectoryComboBox
		extends JComboBox<File>
	{
		static final int CLEAR_WIDTH = 24;

		@Override
		public void setSelectedIndex( int index ) {
			if( isClearHit() ) {
				removeItemAt( index );
				saveState();
				return;
			}

			super.setSelectedIndex( index );
		}

		@Override
		public void setPopupVisible( boolean v ) {
			if( isClearHit() )
				return;

			super.setPopupVisible( v );
		}

		private boolean isClearHit() {
			AWTEvent currentEvent = EventQueue.getCurrentEvent();
			if( currentEvent instanceof MouseEvent && currentEvent.getSource() instanceof JList ) {
				MouseEvent e = (MouseEvent) currentEvent;
				JList<?> list = (JList<?>) currentEvent.getSource();
				if( e.getX() >= list.getWidth() - UIScale.scale( CLEAR_WIDTH ) )
					return true;
			}
			return false;
		}
	}

	//---- class DirectoryRenderer --------------------------------------------

	private static class DirectoryRenderer
		extends DefaultListCellRenderer
	{
		private static class MyClearIcon
			extends FlatClearIcon
		{
			void setClearIconColor( Color color ) {
				clearIconColor = color;
			}
		}

		private final JComboBox<File> comboBox;
		private final MyClearIcon clearIcon = new MyClearIcon();
		private boolean paintClearIcon;
		private Color highlightColor;

		DirectoryRenderer( JComboBox<File> comboBox ) {
			this.comboBox = comboBox;
		}

		@Override
		public Component getListCellRendererComponent( JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus )
		{
			if( index > 0 && !isSelected ) {
				File dir = (File) value;
				File previousDir = (File) list.getModel().getElementAt( index - 1 );
				String path = dir.getAbsolutePath();
				String previousPath = previousDir.getAbsolutePath();
				for( File d = dir.getParentFile(); d != null; d = d.getParentFile() ) {
					String p = d.getAbsolutePath();
					if( previousPath.startsWith( p ) && d.getParent() != null ) {
						value = "<html>" + toDimmedText( p ) + path.substring( p.length() ) + "</html>";
						break;
					}
				}
			}

			super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

			highlightColor =(index >= 0 && index == comboBox.getSelectedIndex())
				? list.getSelectionBackground()
				: null;

			paintClearIcon = isSelected;
			if( paintClearIcon )
				clearIcon.setClearIconColor( getForeground() );

			return this;
		}

		private static String toDimmedText( String text ) {
			Color color = UIManager.getColor( "Label.disabledForeground" );
			if( color == null )
				color = UIManager.getColor( "Label.disabledText" );
			if( color == null )
				color = Color.GRAY;
			return String.format( "<span color=\"#%06x\">%s</span>",
				color.getRGB() & 0xffffff, text );
		}

		@Override
		protected void paintComponent( Graphics g ) {
			super.paintComponent( g );

			if( highlightColor != null ) {
				g.setColor( new Color( 0x33000000 | (highlightColor.getRGB() & 0xffffff), true ) );
				g.fillRect( 0, 0, getWidth(), getHeight() );
			}

			if( paintClearIcon ) {
				int width = UIScale.scale( DirectoryComboBox.CLEAR_WIDTH );
				int height = getHeight();
				int x = getWidth() - width;
				int y = 0;

				// make clear button area brighter
				g.setColor( new Color( 0x33ffffff, true ) );
				g.fillRect( x, y, width, height );

				// paint clear icon
				int ix = x + ((width - clearIcon.getIconWidth()) / 2);
				int iy = y + ((height - clearIcon.getIconHeight()) / 2);
				clearIcon.paintIcon( this, g, ix, iy );
			}
		}
	}
}
