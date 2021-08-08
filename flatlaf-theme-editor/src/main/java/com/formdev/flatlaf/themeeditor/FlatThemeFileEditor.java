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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.prefs.Preferences;
import javax.swing.*;
import net.miginfocom.swing.*;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * TODO
 *
 * @author Karl Tauber
 */
public class FlatThemeFileEditor
	extends JFrame
{
	private static final String PREFS_ROOT_PATH = "/flatlaf-theme-editor";
	private static final String KEY_DIRECTORIES = "directories";
	private static final String KEY_RECENT_DIRECTORY = "recentDirectory";
	private static final String KEY_RECENT_FILE = "recentFile";
	private static final String KEY_WINDOW_BOUNDS = "windowBounds";
	private static final String KEY_LAF = "laf";
	private static final String KEY_FONT_SIZE_INCR = "fontSizeIncr";

	private File dir;
	private Preferences state;
	private boolean inLoadDirectory;

	public static void main( String[] args ) {
		File dir = (args.length > 0)
			? new File( args[0] )
			: null;

		SwingUtilities.invokeLater( () -> {
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
		initComponents();

		openDirectoryButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/menu-open.svg" ) );
		if( UIManager.getLookAndFeel() instanceof FlatDarkLaf )
			darkLafMenuItem.setSelected( true );

		restoreState();
		restoreWindowBounds();

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
			JOptionPane.showMessageDialog( parentComponent,
				"Directory '" + dir + "' does not contain properties files.",
				getTitle(), JOptionPane.INFORMATION_MESSAGE );
			return false;
		}

		return true;
	}

	private void directoryChanged() {
		if( inLoadDirectory )
			return;

		Object selectedItem = directoryField.getSelectedItem();
		if( selectedItem == null )
			return;

		File dir = new File( (String) selectedItem );
		if( checkDirectory( this, dir ) )
			loadDirectory( dir );
		else {
			// remove from directories history
			directoryField.removeItem( selectedItem );
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

		inLoadDirectory = true;

		// close all open editors
		int tabCount = tabbedPane.getTabCount();
		for( int i = tabCount - 1; i >= 0; i-- )
			tabbedPane.removeTabAt( i );

		// update directory field
		DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) directoryField.getModel();
		String dirStr = dir.getAbsolutePath();
		int indexOf = model.getIndexOf( dirStr );
		if( indexOf < 0 )
			model.addElement( dirStr );
		directoryField.setSelectedItem( dirStr );

		// open all properties files in directory
		String recentFile = state.get( KEY_RECENT_FILE, null );
		for( File file : getPropertiesFiles( dir ) )
			openFile( file, file.getName().equals( recentFile ) );

		FlatThemeEditorPane themeEditorPane = (FlatThemeEditorPane) tabbedPane.getSelectedComponent();
		if( themeEditorPane != null )
			themeEditorPane.requestFocusInWindow();

		saveState();

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
		Arrays.sort( propertiesFiles );
		return propertiesFiles;
	}

	private void openFile( File file, boolean select ) {
		FlatThemeEditorPane themeEditorPane = new FlatThemeEditorPane();
		try {
			themeEditorPane.load( file );
		} catch( IOException ex ) {
			ex.printStackTrace(); // TODO
		}

		Supplier<String> titleFun = () -> {
			return (themeEditorPane.isDirty() ? "* " : "")
				+ StringUtils.removeTrailing( themeEditorPane.getFile().getName(), ".properties" );
		};
		themeEditorPane.addPropertyChangeListener( FlatThemeEditorPane.DIRTY_PROPERTY, e -> {
			int index = tabbedPane.indexOfComponent( themeEditorPane );
			if( index >= 0 )
				tabbedPane.setTitleAt( index, titleFun.get() );
		} );

		tabbedPane.addTab( titleFun.get(), null, themeEditorPane, file.getAbsolutePath() );

		if( select )
			tabbedPane.setSelectedComponent( themeEditorPane );
	}

	private void selectedTabChanged() {
		if( inLoadDirectory )
			return;

		FlatThemeEditorPane themeEditorPane = (FlatThemeEditorPane) tabbedPane.getSelectedComponent();
		String filename = (themeEditorPane != null) ? themeEditorPane.getFile().getName() : null;
		putPrefsString( state, KEY_RECENT_FILE, filename );
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

	private void nextEditor() {
		if( tabbedPane.getTabCount() == 0 )
			return;

		int index = tabbedPane.getSelectedIndex() + 1;
		if( index >= tabbedPane.getTabCount() )
			index = 0;
		tabbedPane.setSelectedIndex( index );
	}

	private void previousEditor() {
		if( tabbedPane.getTabCount() == 0 )
			return;

		int index = tabbedPane.getSelectedIndex() - 1;
		if( index < 0 )
			index = tabbedPane.getTabCount() - 1;
		tabbedPane.setSelectedIndex( index );
	}

	private void find() {
		FlatThemeEditorPane themeEditorPane = (FlatThemeEditorPane) tabbedPane.getSelectedComponent();
		if( themeEditorPane != null )
			themeEditorPane.showFindReplaceBar();
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

	private void restoreState() {
		state = Preferences.userRoot().node( PREFS_ROOT_PATH );

		// restore directories history
		String[] directories = getPrefsStrings( state, KEY_DIRECTORIES );
		SortedComboBoxModel<String> model = new SortedComboBoxModel<>( directories );
		directoryField.setModel( model );
	}

	private void saveState() {
		// save directories history
		ComboBoxModel<String> model = directoryField.getModel();
		String[] directories = new String[model.getSize()];
		for( int i = 0; i < directories.length; i++ )
			directories[i] = model.getElementAt( i );
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
					int x = Integer.parseInt( list.get( 0 ) );
					int y = Integer.parseInt( list.get( 1 ) );
					int w = Integer.parseInt( list.get( 2 ) );
					int h = Integer.parseInt( list.get( 3 ) );

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
		state.put( KEY_WINDOW_BOUNDS, r.x + "," + r.y + ',' + r.width + ',' + r.height );
	}

	private static void putPrefsString( Preferences prefs, String key, String value ) {
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
		saveAllMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		editMenu = new JMenu();
		findMenuItem = new JMenuItem();
		viewMenu = new JMenu();
		lightLafMenuItem = new JRadioButtonMenuItem();
		darkLafMenuItem = new JRadioButtonMenuItem();
		incrFontSizeMenuItem = new JMenuItem();
		decrFontSizeMenuItem = new JMenuItem();
		resetFontSizeMenuItem = new JMenuItem();
		windowMenu = new JMenu();
		nextEditorMenuItem = new JMenuItem();
		previousEditorMenuItem = new JMenuItem();
		controlPanel = new JPanel();
		directoryLabel = new JLabel();
		directoryField = new JComboBox<>();
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
				openDirectoryMenuItem.addActionListener(e -> openDirectory());
				fileMenu.add(openDirectoryMenuItem);

				//---- saveAllMenuItem ----
				saveAllMenuItem.setText("Save All");
				saveAllMenuItem.setMnemonic('S');
				saveAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
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
				findMenuItem.setText("Find/Replace...");
				findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				findMenuItem.setMnemonic('F');
				findMenuItem.addActionListener(e -> find());
				editMenu.add(findMenuItem);
			}
			menuBar.add(editMenu);

			//======== viewMenu ========
			{
				viewMenu.setText("View");
				viewMenu.setMnemonic('V');

				//---- lightLafMenuItem ----
				lightLafMenuItem.setText("Light Laf");
				lightLafMenuItem.setMnemonic('L');
				lightLafMenuItem.setSelected(true);
				lightLafMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
				lightLafMenuItem.addActionListener(e -> lightLaf());
				viewMenu.add(lightLafMenuItem);

				//---- darkLafMenuItem ----
				darkLafMenuItem.setText("Dark Laf");
				darkLafMenuItem.setMnemonic('D');
				darkLafMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
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
			}
			menuBar.add(viewMenu);

			//======== windowMenu ========
			{
				windowMenu.setText("Window");
				windowMenu.setMnemonic('W');

				//---- nextEditorMenuItem ----
				nextEditorMenuItem.setText("Next Editor");
				nextEditorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				nextEditorMenuItem.setMnemonic('N');
				nextEditorMenuItem.addActionListener(e -> nextEditor());
				windowMenu.add(nextEditorMenuItem);

				//---- previousEditorMenuItem ----
				previousEditorMenuItem.setText("Previous Editor");
				previousEditorMenuItem.setMnemonic('P');
				previousEditorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()|KeyEvent.SHIFT_DOWN_MASK));
				previousEditorMenuItem.addActionListener(e -> previousEditor());
				windowMenu.add(previousEditorMenuItem);
			}
			menuBar.add(windowMenu);
		}
		setJMenuBar(menuBar);

		//======== controlPanel ========
		{
			controlPanel.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[grow,fill]" +
				"[fill]",
				// rows
				"[]"));

			//---- directoryLabel ----
			directoryLabel.setText("Directory:");
			controlPanel.add(directoryLabel, "cell 0 0");

			//---- directoryField ----
			directoryField.setEditable(false);
			directoryField.setFocusable(false);
			directoryField.addActionListener(e -> directoryChanged());
			controlPanel.add(directoryField, "cell 1 0");

			//---- openDirectoryButton ----
			openDirectoryButton.setFocusable(false);
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

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(lightLafMenuItem);
		buttonGroup1.add(darkLafMenuItem);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem openDirectoryMenuItem;
	private JMenuItem saveAllMenuItem;
	private JMenuItem exitMenuItem;
	private JMenu editMenu;
	private JMenuItem findMenuItem;
	private JMenu viewMenu;
	private JRadioButtonMenuItem lightLafMenuItem;
	private JRadioButtonMenuItem darkLafMenuItem;
	private JMenuItem incrFontSizeMenuItem;
	private JMenuItem decrFontSizeMenuItem;
	private JMenuItem resetFontSizeMenuItem;
	private JMenu windowMenu;
	private JMenuItem nextEditorMenuItem;
	private JMenuItem previousEditorMenuItem;
	private JPanel controlPanel;
	private JLabel directoryLabel;
	private JComboBox<String> directoryField;
	private JButton openDirectoryButton;
	private FlatTabbedPane tabbedPane;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class SortedComboBoxModel ------------------------------------------

	private static class SortedComboBoxModel<E>
		extends DefaultComboBoxModel<E>
	{
		private Comparator<E> comparator;

		public SortedComboBoxModel( E[] items ) {
			this( items, null );
		}

		public SortedComboBoxModel( E[] items, Comparator<E> c ) {
			super( sort( items, c ) );
			this.comparator = c;
		}

		@Override
		public void addElement( E obj ) {
			if( getSize() == 0 ) {
				super.addElement( obj );
			} else {
				int index = binarySearch( this, obj, comparator );
				insertElementAt( obj, (index < 0) ? ((-index)-1) : index );
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
					return mid; // key found
			}
			return -(low + 1); // key not found.
	    }
	}
}
