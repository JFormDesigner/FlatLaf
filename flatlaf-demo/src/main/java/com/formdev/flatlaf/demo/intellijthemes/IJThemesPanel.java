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
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class IJThemesPanel
	extends JPanel
{
	private final IJThemesManager themesManager = new IJThemesManager();
	private final List<IJThemeInfo> themes = new ArrayList<>();
	private final PropertyChangeListener lafListener = this::lafChanged;

	public IJThemesPanel() {
		initComponents();

		// load theme infos
		themesManager.loadBundledThemes();
		themesManager.loadThemesFromDirectory();

		// sort themes by name
		Comparator<? super IJThemeInfo> comparator = (t1, t2) -> t1.name.compareToIgnoreCase( t2.name );
		themesManager.bundledThemes.sort( comparator );
		themesManager.moreThemes.sort( comparator );

		HashMap<Integer, String> categories = new HashMap<>();

		// add core themes at beginning
		categories.put( themes.size(), "Core Themes" );
		themes.add( new IJThemeInfo( "Flat Light", null, null, null, FlatLightLaf.class.getName() ) );
		themes.add( new IJThemeInfo( "Flat Dark", null, null, null, FlatDarkLaf.class.getName() ) );
		themes.add( new IJThemeInfo( "Flat IntelliJ", null, null, null, FlatIntelliJLaf.class.getName() ) );
		themes.add( new IJThemeInfo( "Flat Darcula", null, null, null, FlatDarculaLaf.class.getName() ) );

		// add bundles themes
		categories.put( themes.size(), "IntelliJ Themes" );
		themes.addAll( themesManager.bundledThemes );

		// add themes from directory
		categories.put( themes.size(), "Current Directory" );
		themes.addAll( themesManager.moreThemes );

		// create renderer
		themesList.setCellRenderer( new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent( JList<?> list, Object value,
				int index, boolean isSelected, boolean cellHasFocus )
			{
				String title = categories.get( index );
				JComponent c = (JComponent) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				if( title != null )
					c.setBorder( new CompoundBorder( new ListCellTitledBorder( themesList, title ), c.getBorder() ) );
				return c;
			}
		} );

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
	}

	private void themesListValueChanged( ListSelectionEvent e ) {
		if( e.getValueIsAdjusting() )
			return;

		EventQueue.invokeLater( () -> {
			setTheme( themesList.getSelectedValue() );
		} );
	}

	private void setTheme( IJThemeInfo themeInfo ) {
		if( themeInfo == null )
			return;

		// change look and feel
		if( themeInfo.lafClassName != null ) {
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

	private void showInformationDialog( String message, Exception ex ) {
		JOptionPane.showMessageDialog( null, message + "\n\n" + ex.getMessage(),
			"FlatLaf", JOptionPane.INFORMATION_MESSAGE );
	}

	@Override
	public void addNotify() {
		super.addNotify();

		selectedCurrentLookAndFeel();
		UIManager.addPropertyChangeListener( lafListener );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		UIManager.removePropertyChangeListener( lafListener );
	}

	void lafChanged( PropertyChangeEvent e ) {
		if( "lookAndFeel".equals( e.getPropertyName() ) )
			selectedCurrentLookAndFeel();
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
		themesScrollPane = new JScrollPane();
		themesList = new JList<>();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[grow,fill]",
			// rows
			"[]" +
			"[grow,fill]"));

		//---- themesLabel ----
		themesLabel.setText("Themes:");
		add(themesLabel, "cell 0 0");

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
	private JScrollPane themesScrollPane;
	private JList<IJThemeInfo> themesList;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
