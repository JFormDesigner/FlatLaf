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

package com.formdev.flatlaf.demo.intellijthemes;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.StringUtils;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class IJThemesPanel
	extends JPanel
{
	private final IJThemesManager themesManager = new IJThemesManager();
	private final List<IJThemeInfo> themes = new ArrayList<>();
	private final HashMap<Integer, String> categories = new HashMap<>();
	private final PropertyChangeListener lafListener = this::lafChanged;
	private final WindowListener windowListener = new WindowAdapter() {
		@Override
		public void windowActivated( WindowEvent e ) {
			IJThemesPanel.this.windowActivated();
		}
	};
	private Window window;

	private File lastDirectory;

	public IJThemesPanel() {
		initComponents();

		saveButton.setEnabled( false );
		sourceCodeButton.setEnabled( false );
		saveButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/download.svg" ) );
		sourceCodeButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/github.svg" ) );

		// create renderer
		themesList.setCellRenderer( new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent( JList<?> list, Object value,
				int index, boolean isSelected, boolean cellHasFocus )
			{
				String title = categories.get( index );
				String name = ((IJThemeInfo)value).name;
				int sep = name.indexOf( '/' );
				if( sep >= 0 )
					name = name.substring( sep + 1 ).trim();

				JComponent c = (JComponent) super.getListCellRendererComponent( list, name, index, isSelected, cellHasFocus );
				c.setToolTipText( buildToolTip( (IJThemeInfo) value ) );
				if( title != null )
					c.setBorder( new CompoundBorder( new ListCellTitledBorder( themesList, title ), c.getBorder() ) );
				return c;
			}

			private String buildToolTip( IJThemeInfo ti ) {
				if( ti.themeFile != null )
					return ti.themeFile.getPath();
				if( ti.resourceName == null )
					return ti.name;

				return "Name: " + ti.name
					+ "\nLicense: " + ti.license
					+ "\nSource Code: " + ti.sourceCodeUrl;
			}
		} );

		updateThemesList();
	}

	private void updateThemesList() {
		// load theme infos
		themesManager.loadBundledThemes();
		themesManager.loadThemesFromDirectory();

		// sort themes by name
		Comparator<? super IJThemeInfo> comparator = (t1, t2) -> t1.name.compareToIgnoreCase( t2.name );
		themesManager.bundledThemes.sort( comparator );
		themesManager.moreThemes.sort( comparator );

		themes.clear();
		categories.clear();

		// add core themes at beginning
		categories.put( themes.size(), "Core Themes" );
		themes.add( new IJThemeInfo( "Flat Light", null, null, null, null, null, null, FlatLightLaf.class.getName() ) );
		themes.add( new IJThemeInfo( "Flat Dark", null, null, null, null, null, null, FlatDarkLaf.class.getName() ) );
		themes.add( new IJThemeInfo( "Flat IntelliJ", null, null, null, null, null, null, FlatIntelliJLaf.class.getName() ) );
		themes.add( new IJThemeInfo( "Flat Darcula", null, null, null, null, null, null, FlatDarculaLaf.class.getName() ) );

		// add uncategorized bundled themes
		categories.put( themes.size(), "IntelliJ Themes" );
		for( IJThemeInfo ti : themesManager.bundledThemes ) {
			if( !ti.name.contains( "/" ) )
				themes.add( ti );
		}

		// add categorized bundled themes
		String lastCategory = null;
		for( IJThemeInfo ti : themesManager.bundledThemes ) {
			int sep = ti.name.indexOf( '/' );
			if( sep < 0 )
				continue;

			String category = ti.name.substring( 0, sep ).trim();
			if( !Objects.equals( lastCategory, category ) ) {
				lastCategory = category;
				categories.put( themes.size(), category );
			}

			themes.add( ti );
		}

		// add themes from directory
		categories.put( themes.size(), "Current Directory" );
		themes.addAll( themesManager.moreThemes );

		// remember selection
		IJThemeInfo oldSel = themesList.getSelectedValue();

		// fill themes list
		themesList.setModel( new AbstractListModel<IJThemeInfo>() {
			@Override
			public int getSize() {
				return themes.size();
			}
			@Override
			public IJThemeInfo getElementAt( int index ) {
				return themes.get( index );
			}
		} );

		// restore selection
		if( oldSel != null ) {
			for( int i = 0; i < themes.size(); i++ ) {
				IJThemeInfo theme = themes.get( i );
				if( oldSel.name.equals( theme.name ) &&
					Objects.equals( oldSel.resourceName, theme.resourceName ) &&
					Objects.equals( oldSel.themeFile, theme.themeFile ) &&
					Objects.equals( oldSel.lafClassName, theme.lafClassName ) )
				{
					themesList.setSelectedIndex( i );
					break;
				}
			}
		}
	}

	private void themesListValueChanged( ListSelectionEvent e ) {
		if( e.getValueIsAdjusting() )
			return;

		IJThemeInfo themeInfo = themesList.getSelectedValue();
		boolean bundledTheme = (themeInfo != null && themeInfo.resourceName != null);
		saveButton.setEnabled( bundledTheme );
		sourceCodeButton.setEnabled( bundledTheme );

		EventQueue.invokeLater( () -> {
			setTheme( themeInfo );
		} );
	}

	private void setTheme( IJThemeInfo themeInfo ) {
		if( themeInfo == null )
			return;

		// change look and feel
		if( themeInfo.lafClassName != null ) {
			if( themeInfo.lafClassName.equals( UIManager.getLookAndFeel().getClass().getName() ) )
				return;

			try {
				UIManager.setLookAndFeel( themeInfo.lafClassName );
			} catch( Exception ex ) {
				ex.printStackTrace();
				showInformationDialog( "Failed to create '" + themeInfo.lafClassName + "'.", ex );
			}
		} else if( themeInfo.themeFile != null ) {
			try {
			    FlatLaf.install( IntelliJTheme.createLaf( new FileInputStream( themeInfo.themeFile ) ) );
			} catch( Exception ex ) {
				ex.printStackTrace();
				showInformationDialog( "Failed to load '" + themeInfo.themeFile + "'.", ex );
			}
		} else
			IntelliJTheme.install( getClass().getResourceAsStream( themeInfo.resourceName ) );

		// update all components
		FlatLaf.updateUI();
	}

	private void saveTheme() {
		IJThemeInfo themeInfo = themesList.getSelectedValue();
		if( themeInfo == null || themeInfo.resourceName == null )
			return;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile( new File( lastDirectory, themeInfo.resourceName ) );
		if( fileChooser.showSaveDialog( SwingUtilities.windowForComponent( this ) ) != JFileChooser.APPROVE_OPTION )
			return;

		File file = fileChooser.getSelectedFile();
		lastDirectory = file.getParentFile();

		// save theme
		try {
			Files.copy( getClass().getResourceAsStream( themeInfo.resourceName ),
				file.toPath(), StandardCopyOption.REPLACE_EXISTING );
		} catch( IOException ex ) {
			showInformationDialog( "Failed to save theme to '" + file + "'.", ex );
			return;
		}

		// save license
		if( themeInfo.licenseFile != null ) {
			try {
				File licenseFile = new File( file.getParentFile(),
					StringUtils.removeTrailing( file.getName(), ".theme.json" ) +
					themeInfo.licenseFile.substring( themeInfo.licenseFile.indexOf( '.' ) ) );
				Files.copy( getClass().getResourceAsStream( themeInfo.licenseFile ),
					licenseFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
			} catch( IOException ex ) {
				showInformationDialog( "Failed to save theme license to '" + file + "'.", ex );
				return;
			}
		}
	}

	private void browseSourceCode() {
		IJThemeInfo themeInfo = themesList.getSelectedValue();
		if( themeInfo == null || themeInfo.resourceName == null )
			return;

		String themeUrl = (themeInfo.sourceCodeUrl + '/' + themeInfo.sourceCodePath).replace( " ", "%20" );
		try {
			Desktop.getDesktop().browse( new URI( themeUrl ) );
		} catch( IOException | URISyntaxException ex ) {
			showInformationDialog( "Failed to browse '" + themeUrl + "'.", ex );
		}
	}

	private void showInformationDialog( String message, Exception ex ) {
		JOptionPane.showMessageDialog( SwingUtilities.windowForComponent( this ),
			message + "\n\n" + ex.getMessage(),
			"FlatLaf", JOptionPane.INFORMATION_MESSAGE );
	}

	@Override
	public void addNotify() {
		super.addNotify();

		selectedCurrentLookAndFeel();
		UIManager.addPropertyChangeListener( lafListener );

		window = SwingUtilities.windowForComponent( this );
		if( window != null )
			window.addWindowListener( windowListener );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		UIManager.removePropertyChangeListener( lafListener );

		if( window != null ) {
			window.removeWindowListener( windowListener );
			window = null;
		}
	}

	private void lafChanged( PropertyChangeEvent e ) {
		if( "lookAndFeel".equals( e.getPropertyName() ) )
			selectedCurrentLookAndFeel();
	}

	private void windowActivated() {
		// refresh themes list on window activation
		if( themesManager.hasThemesFromDirectoryChanged() )
			updateThemesList();
	}

	private void selectedCurrentLookAndFeel() {
		LookAndFeel lookAndFeel = UIManager.getLookAndFeel();

		int newSel = -1;
		if( !(lookAndFeel instanceof IntelliJTheme.ThemeLaf) ) {
			String lafClassName = lookAndFeel.getClass().getName();
			for( int i = 0; i < themes.size(); i++ ) {
				if( lafClassName.equals( themes.get( i ).lafClassName ) ) {
					newSel = i;
					break;
				}
			}

			if( newSel >= 0 )
				themesList.setSelectedIndex( newSel );
			else
				themesList.clearSelection();
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel themesLabel = new JLabel();
		toolBar = new JToolBar();
		saveButton = new JButton();
		sourceCodeButton = new JButton();
		themesScrollPane = new JScrollPane();
		themesList = new JList<>();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[grow,fill]",
			// rows
			"[]3" +
			"[grow,fill]"));

		//---- themesLabel ----
		themesLabel.setText("Themes:");
		add(themesLabel, "cell 0 0");

		//======== toolBar ========
		{
			toolBar.setFloatable(false);

			//---- saveButton ----
			saveButton.setToolTipText("Save .theme.json of selected IntelliJ theme to file.");
			saveButton.addActionListener(e -> saveTheme());
			toolBar.add(saveButton);

			//---- sourceCodeButton ----
			sourceCodeButton.setToolTipText("Opens the source code repository of selected IntelliJ theme in the browser.");
			sourceCodeButton.addActionListener(e -> browseSourceCode());
			toolBar.add(sourceCodeButton);
		}
		add(toolBar, "cell 0 0,alignx right,growx 0");

		//======== themesScrollPane ========
		{

			//---- themesList ----
			themesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			themesList.addListSelectionListener(e -> themesListValueChanged(e));
			themesScrollPane.setViewportView(themesList);
		}
		add(themesScrollPane, "cell 0 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JToolBar toolBar;
	private JButton saveButton;
	private JButton sourceCodeButton;
	private JScrollPane themesScrollPane;
	private JList<IJThemeInfo> themesList;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
