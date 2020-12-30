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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Supplier;
import javax.swing.*;
import com.formdev.flatlaf.extras.components.*;
import net.miginfocom.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
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
	private File dir;

	public static void main( String[] args ) {
		File dir = new File( args.length > 0
			? args[0]
			: "." );

		SwingUtilities.invokeLater( () -> {
			FlatLightLaf.install();
			FlatInspector.install( "ctrl alt shift X" );
			FlatUIDefaultsInspector.install( "ctrl shift alt Y" );

			FlatThemeFileEditor frame = new FlatThemeFileEditor();

			frame.loadDirectory( dir );

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setSize( Math.min( UIScale.scale( 800 ), screenSize.width ),
				screenSize.height - UIScale.scale( 100 ) );
			frame.setLocationRelativeTo( null );
			frame.setVisible( true );
		} );
	}

	public FlatThemeFileEditor() {
		initComponents();
	}

	private void loadDirectory( File dir ) {
		this.dir = dir;

		try {
			directoryField.setText( dir.getCanonicalPath() );
		} catch( IOException ex ) {
			directoryField.setText( dir.getAbsolutePath() );
		}

		for( File file : getPropertiesFiles( dir ) )
			openFile( file );
	}

	private void updateDirectory() {
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
				openFile( file );
		}
	}

	private File[] getPropertiesFiles( File dir ) {
		File[] propertiesFiles = dir.listFiles( (d, name) -> {
			return name.endsWith( ".properties" );
		} );
		Arrays.sort( propertiesFiles );
		return propertiesFiles;
	}

	private void openFile( File file ) {
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
	}

	private boolean saveAll() {
		for( FlatThemeEditorPane themeEditorPane : getThemeEditorPanes() ) {
			if( !themeEditorPane.saveIfDirty() )
				return false;
		}
		return true;
	}

	private void exit() {
		if( saveAll() )
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
		int index = tabbedPane.getSelectedIndex() + 1;
		if( index >= tabbedPane.getTabCount() )
			index = 0;
		tabbedPane.setSelectedIndex( index );
	}

	private void previousEditor() {
		int index = tabbedPane.getSelectedIndex() - 1;
		if( index < 0 )
			index = tabbedPane.getTabCount() - 1;
		tabbedPane.setSelectedIndex( index );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		menuBar = new JMenuBar();
		fileMenu = new JMenu();
		saveAllMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		windowMenu = new JMenu();
		nextEditorMenuItem = new JMenuItem();
		previousEditorMenuItem = new JMenuItem();
		controlPanel = new JPanel();
		directoryLabel = new JLabel();
		directoryField = new JTextField();
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
				previousEditorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()|KeyEvent.SHIFT_MASK));
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
				"[grow,fill]",
				// rows
				"[]"));

			//---- directoryLabel ----
			directoryLabel.setText("Directory:");
			controlPanel.add(directoryLabel, "cell 0 0");

			//---- directoryField ----
			directoryField.setEditable(false);
			directoryField.setFocusable(false);
			controlPanel.add(directoryField, "cell 1 0");
		}
		contentPane.add(controlPanel, BorderLayout.NORTH);

		//======== tabbedPane ========
		{
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		}
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem saveAllMenuItem;
	private JMenuItem exitMenuItem;
	private JMenu windowMenu;
	private JMenuItem nextEditorMenuItem;
	private JMenuItem previousEditorMenuItem;
	private JPanel controlPanel;
	private JLabel directoryLabel;
	private JTextField directoryField;
	private FlatTabbedPane tabbedPane;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
