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
import java.awt.Graphics;
import java.awt.Rectangle;
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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.demo.DemoPrefs;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.themes.*;
import com.formdev.flatlaf.ui.FlatListUI;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.StringUtils;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class IJThemesPanel
	extends JPanel
{
	public static final String THEMES_PACKAGE = "/com/formdev/flatlaf/intellijthemes/themes/";

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
	private boolean isAdjustingThemesList;
	private long lastLafChangeTime = System.currentTimeMillis();

	public IJThemesPanel() {
		initComponents();

		saveButton.setEnabled( false );
		sourceCodeButton.setEnabled( false );

		// create renderer
		themesList.setCellRenderer( new DefaultListCellRenderer() {
			private int index;
			private boolean isSelected;
			private int titleHeight;

			@Override
			public Component getListCellRendererComponent( JList<?> list, Object value,
				int index, boolean isSelected, boolean cellHasFocus )
			{
				this.index = index;
				this.isSelected = isSelected;
				this.titleHeight = 0;

				String title = categories.get( index );
				String name = ((IJThemeInfo)value).name;
				int sep = name.indexOf( '/' );
				if( sep >= 0 )
					name = name.substring( sep + 1 ).trim();

				JComponent c = (JComponent) super.getListCellRendererComponent( list, name, index, isSelected, cellHasFocus );
				c.setToolTipText( buildToolTip( (IJThemeInfo) value ) );
				if( title != null ) {
					Border titledBorder = new ListCellTitledBorder( themesList, title );
					c.setBorder( new CompoundBorder( titledBorder, c.getBorder() ) );
					titleHeight = titledBorder.getBorderInsets( c ).top;
				}
				return c;
			}

			@Override
			public boolean isOpaque() {
				return !isSelectedTitle();
			}

			@Override
			protected void paintComponent( Graphics g ) {
				if( isSelectedTitle() ) {
					g.setColor( getBackground() );
					FlatListUI.paintCellSelection( themesList, g, index, 0, titleHeight, getWidth(), getHeight() - titleHeight );
				}

				super.paintComponent( g );
			}

			private boolean isSelectedTitle() {
				return titleHeight > 0 && isSelected && UIManager.getLookAndFeel() instanceof FlatLaf;
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
		int filterLightDark = filterComboBox.getSelectedIndex();
		boolean showLight = (filterLightDark != 2);
		boolean showDark = (filterLightDark != 1);

		// load theme infos
		themesManager.loadBundledThemes();
		themesManager.loadThemesFromDirectory();

		// sort themes by name
		Comparator<? super IJThemeInfo> comparator = (t1, t2) -> t1.name.compareToIgnoreCase( t2.name );
		themesManager.bundledThemes.sort( comparator );
		themesManager.moreThemes.sort( comparator );

		// remember selection (must be invoked before clearing themes field)
		IJThemeInfo oldSel = themesList.getSelectedValue();

		themes.clear();
		categories.clear();

		// add core themes at beginning
		categories.put( themes.size(), "Core Themes" );
		if( showLight )
			themes.add( new IJThemeInfo( "FlatLaf Light", null, false, null, null, null, null, null, FlatLightLaf.class.getName() ) );
		if( showDark )
			themes.add( new IJThemeInfo( "FlatLaf Dark", null, true, null, null, null, null, null, FlatDarkLaf.class.getName() ) );
		if( showLight )
			themes.add( new IJThemeInfo( "FlatLaf IntelliJ", null, false, null, null, null, null, null, FlatIntelliJLaf.class.getName() ) );
		if( showDark )
			themes.add( new IJThemeInfo( "FlatLaf Darcula", null, true, null, null, null, null, null, FlatDarculaLaf.class.getName() ) );

		if( showLight )
			themes.add( new IJThemeInfo( "FlatLaf macOS Light", null, false, null, null, null, null, null, FlatMacLightLaf.class.getName() ) );
		if( showDark )
			themes.add( new IJThemeInfo( "FlatLaf macOS Dark", null, true, null, null, null, null, null, FlatMacDarkLaf.class.getName() ) );

		// add themes from directory
		categories.put( themes.size(), "Current Directory" );
		themes.addAll( themesManager.moreThemes );

		// add uncategorized bundled themes
		categories.put( themes.size(), "IntelliJ Themes" );
		for( IJThemeInfo ti : themesManager.bundledThemes ) {
			boolean show = (showLight && !ti.dark) || (showDark && ti.dark);
			if( show && !ti.name.contains( "/" ) )
				themes.add( ti );
		}

		// add categorized bundled themes
		String lastCategory = null;
		for( IJThemeInfo ti : themesManager.bundledThemes ) {
			boolean show = (showLight && !ti.dark) || (showDark && ti.dark);
			int sep = ti.name.indexOf( '/' );
			if( !show || sep < 0 )
				continue;

			String category = ti.name.substring( 0, sep ).trim();
			if( !Objects.equals( lastCategory, category ) ) {
				lastCategory = category;
				categories.put( themes.size(), category );
			}

			themes.add( ti );
		}

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

			// select first theme if none selected
			if( themesList.getSelectedIndex() < 0 )
				themesList.setSelectedIndex( 0 );
		}

		// scroll selection into visible area
		int sel = themesList.getSelectedIndex();
		if( sel >= 0 ) {
			Rectangle bounds = themesList.getCellBounds( sel, sel );
			if( bounds != null )
				themesList.scrollRectToVisible( bounds );
		}
	}

	public void selectPreviousTheme() {
		int sel = themesList.getSelectedIndex();
		if( sel > 0 )
			themesList.setSelectedIndex( sel - 1 );
	}

	public void selectNextTheme() {
		int sel = themesList.getSelectedIndex();
		themesList.setSelectedIndex( sel + 1 );
	}

	private void themesListValueChanged( ListSelectionEvent e ) {
		IJThemeInfo themeInfo = themesList.getSelectedValue();
		boolean bundledTheme = (themeInfo != null && themeInfo.resourceName != null);
		saveButton.setEnabled( bundledTheme );
		sourceCodeButton.setEnabled( bundledTheme );

		if( e.getValueIsAdjusting() || isAdjustingThemesList )
			return;

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

			FlatAnimatedLafChange.showSnapshot();

			try {
				UIManager.setLookAndFeel( themeInfo.lafClassName );
			} catch( Exception ex ) {
				LoggingFacade.INSTANCE.logSevere( null, ex );
				showInformationDialog( "Failed to create '" + themeInfo.lafClassName + "'.", ex );
			}
		} else if( themeInfo.themeFile != null ) {
			FlatAnimatedLafChange.showSnapshot();

			try {
				if( themeInfo.themeFile.getName().endsWith( ".properties" ) ) {
				    FlatLaf.setup( new FlatPropertiesLaf( themeInfo.name, themeInfo.themeFile ) );
				} else
				    FlatLaf.setup( IntelliJTheme.createLaf( new FileInputStream( themeInfo.themeFile ) ) );

				DemoPrefs.getState().put( DemoPrefs.KEY_LAF_THEME, DemoPrefs.FILE_PREFIX + themeInfo.themeFile );
			} catch( Exception ex ) {
				LoggingFacade.INSTANCE.logSevere( null, ex );
				showInformationDialog( "Failed to load '" + themeInfo.themeFile + "'.", ex );
			}
		} else {
			FlatAnimatedLafChange.showSnapshot();

			IntelliJTheme.setup( getClass().getResourceAsStream( THEMES_PACKAGE + themeInfo.resourceName ) );
		    DemoPrefs.getState().put( DemoPrefs.KEY_LAF_THEME, DemoPrefs.RESOURCE_PREFIX + themeInfo.resourceName );
		}

		// update all components
		FlatLaf.updateUI();
		FlatAnimatedLafChange.hideSnapshotWithAnimation();
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
			Files.copy( getClass().getResourceAsStream( THEMES_PACKAGE + themeInfo.resourceName ),
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
				Files.copy( getClass().getResourceAsStream( THEMES_PACKAGE + themeInfo.licenseFile ),
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

		String themeUrl = themeInfo.sourceCodeUrl;
		if( themeInfo.sourceCodePath != null )
			themeUrl += '/' + themeInfo.sourceCodePath;
		themeUrl = themeUrl.replace( " ", "%20" );
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
		if( "lookAndFeel".equals( e.getPropertyName() ) ) {
			selectedCurrentLookAndFeel();
			lastLafChangeTime = System.currentTimeMillis();
		}
	}

	private void windowActivated() {
		// refresh themes list on window activation
		if( themesManager.hasThemesFromDirectoryChanged() )
			updateThemesList();
		else {
			// check whether core .properties files of current Laf have changed
			// in development environment since last Laf change and reload theme
			LookAndFeel laf = UIManager.getLookAndFeel();
			if( laf instanceof FlatLaf ) {
				List<Class<?>> lafClasses = new ArrayList<>();

				if( laf instanceof IntelliJTheme.ThemeLaf ) {
					boolean dark = ((FlatLaf)laf).isDark();
					lafClasses.add( FlatLaf.class );
					lafClasses.add( dark ? FlatDarkLaf.class : FlatLightLaf.class );
					lafClasses.add( dark ? FlatDarculaLaf.class : FlatIntelliJLaf.class );
					lafClasses.add( IntelliJTheme.ThemeLaf.class );
				} else {
					for( Class<?> lafClass = laf.getClass();
						FlatLaf.class.isAssignableFrom( lafClass );
						lafClass = lafClass.getSuperclass() )
					{
						lafClasses.add( 0, lafClass );
					}
				}

				boolean reload = false;
				for( Class<?> lafClass : lafClasses ) {
					String propertiesName = '/' + lafClass.getName().replace( '.', '/' ) + ".properties";
					URL url = lafClass.getResource( propertiesName );
					if( url != null && "file".equals( url.getProtocol() ) ) {
						try {
							File file = new File( url.toURI() );
							if( file.lastModified() > lastLafChangeTime ) {
								reload = true;
								break;
							}
						} catch( URISyntaxException ex ) {
							// ignore
						}
					}
				}

				if( reload )
					setTheme( themesList.getSelectedValue() );
			}
		}
	}

	private void selectedCurrentLookAndFeel() {
		LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
		String theme = UIManager.getLookAndFeelDefaults().getString( DemoPrefs.THEME_UI_KEY );

		if( theme == null && (lookAndFeel instanceof IntelliJTheme.ThemeLaf || lookAndFeel instanceof FlatPropertiesLaf) )
			return;

		Predicate<IJThemeInfo> test;
		if( theme != null && theme.startsWith( DemoPrefs.RESOURCE_PREFIX ) ) {
			String resourceName = theme.substring( DemoPrefs.RESOURCE_PREFIX.length() );
			test = ti -> Objects.equals( ti.resourceName, resourceName );
		} else if( theme != null && theme.startsWith( DemoPrefs.FILE_PREFIX ) ) {
			File themeFile = new File( theme.substring( DemoPrefs.FILE_PREFIX.length() ) );
			test = ti -> Objects.equals( ti.themeFile, themeFile );
		} else {
			String lafClassName = lookAndFeel.getClass().getName();
			test = ti -> Objects.equals( ti.lafClassName, lafClassName );
		}

		int newSel = -1;
		for( int i = 0; i < themes.size(); i++ ) {
			if( test.test( themes.get( i ) ) ) {
				newSel = i;
				break;
			}
		}

		isAdjustingThemesList = true;
		if( newSel >= 0 ) {
			if( newSel != themesList.getSelectedIndex() )
				themesList.setSelectedIndex( newSel );
		} else
			themesList.clearSelection();
		isAdjustingThemesList = false;
	}

	private void filterChanged() {
		updateThemesList();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel themesLabel = new JLabel();
		toolBar = new JToolBar();
		saveButton = new JButton();
		sourceCodeButton = new JButton();
		filterComboBox = new JComboBox<>();
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
			saveButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/download.svg"));
			saveButton.addActionListener(e -> saveTheme());
			toolBar.add(saveButton);

			//---- sourceCodeButton ----
			sourceCodeButton.setToolTipText("Opens the source code repository of selected IntelliJ theme in the browser.");
			sourceCodeButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/github.svg"));
			sourceCodeButton.addActionListener(e -> browseSourceCode());
			toolBar.add(sourceCodeButton);
		}
		add(toolBar, "cell 0 0,alignx right,growx 0");

		//---- filterComboBox ----
		filterComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
			"all",
			"light",
			"dark"
		}));
		filterComboBox.putClientProperty("JComponent.minimumWidth", 0);
		filterComboBox.setFocusable(false);
		filterComboBox.addActionListener(e -> filterChanged());
		add(filterComboBox, "cell 0 0,alignx right,growx 0");

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
	private JComboBox<String> filterComboBox;
	private JScrollPane themesScrollPane;
	private JList<IJThemeInfo> themesList;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
