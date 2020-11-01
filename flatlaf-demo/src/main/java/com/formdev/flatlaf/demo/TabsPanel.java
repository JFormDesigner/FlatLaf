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

package com.formdev.flatlaf.demo;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_HAS_FULL_BORDER;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_SHOW_CONTENT_SEPARATOR;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_SHOW_TAB_SEPARATORS;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.layout.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class TabsPanel
	extends JPanel
{
	TabsPanel() {
		initComponents();

		addInitialTabs( tabbedPane1, tabbedPane2, tabbedPane3, tabbedPane4 );
	}

	private void tabScrollChanged() {
		int tabLayoutPolicy = tabScrollCheckBox.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT;
		tabbedPane1.setTabLayoutPolicy( tabLayoutPolicy );
		tabbedPane2.setTabLayoutPolicy( tabLayoutPolicy );
		tabbedPane3.setTabLayoutPolicy( tabLayoutPolicy );
		tabbedPane4.setTabLayoutPolicy( tabLayoutPolicy );

		if( !autoMoreTabs && tabScrollCheckBox.isSelected() && !moreTabsCheckBox.isSelected() ) {
			moreTabsCheckBox.setSelected( true );
			moreTabsChanged();
			autoMoreTabs = true;
		} else if( autoMoreTabs && !tabScrollCheckBox.isSelected() && moreTabsCheckBox.isSelected() ) {
			moreTabsCheckBox.setSelected( false );
			moreTabsChanged();
			autoMoreTabs = false;
		}
	}

	private boolean autoMoreTabs;

	private void showTabSeparatorsChanged() {
		Boolean showTabSeparators = showTabSeparatorsCheckBox.isSelected() ? true : null;
		putTabbedPanesClientProperty( TABBED_PANE_SHOW_TAB_SEPARATORS, showTabSeparators );
	}

	private void hideContentSeparatorChanged() {
		Boolean showContentSeparator = hideContentSeparatorCheckBox.isSelected() ? false : null;
		putTabbedPanesClientProperty( TABBED_PANE_SHOW_CONTENT_SEPARATOR, showContentSeparator );
	}

	private void hasFullBorderChanged() {
		Boolean hasFullBorder = hasFullBorderCheckBox.isSelected() ? true : null;
		putTabbedPanesClientProperty( TABBED_PANE_HAS_FULL_BORDER, hasFullBorder );
	}

	private void putTabbedPanesClientProperty( String key, Object value ) {
		tabbedPane1.putClientProperty( key, value );
		tabbedPane2.putClientProperty( key, value );
		tabbedPane3.putClientProperty( key, value );
		tabbedPane4.putClientProperty( key, value );
	}

	private void moreTabsChanged() {
		boolean moreTabs = moreTabsCheckBox.isSelected();
		addRemoveMoreTabs( tabbedPane1, moreTabs );
		addRemoveMoreTabs( tabbedPane2, moreTabs );
		addRemoveMoreTabs( tabbedPane3, moreTabs );
		addRemoveMoreTabs( tabbedPane4, moreTabs );

		autoMoreTabs = false;
	}

	private void addRemoveMoreTabs( JTabbedPane tabbedPane, boolean add ) {
		if( add ) {
			addTab( tabbedPane, "Tab 4", "tab content 4" );
			addTab( tabbedPane, "Tab 5", "tab content 5" );
			addTab( tabbedPane, "Tab 6", "tab content 6" );
			addTab( tabbedPane, "Tab 7", "tab content 7" );
			addTab( tabbedPane, "Tab 8", "tab content 8" );
		} else {
			int tabCount = tabbedPane.getTabCount();
			if( tabCount > 3 ) {
				for( int i = 0; i < 5; i++ )
					tabbedPane.removeTabAt( tabbedPane.getTabCount() - 1 );
			}
		}
	}

	private void addInitialTabs( JTabbedPane... tabbedPanes ) {
		for( JTabbedPane tabbedPane : tabbedPanes ) {
			String placement = "unknown";
			switch( tabbedPane.getTabPlacement() ) {
				case JTabbedPane.TOP:	placement = "TOP"; break;
				case JTabbedPane.BOTTOM:	placement = "BOTTOM"; break;
				case JTabbedPane.LEFT:	placement = "LEFT"; break;
				case JTabbedPane.RIGHT:	placement = "RIGHT"; break;
			}
			addTab( tabbedPane, "Tab 1", "<html><center>" + placement + "<br>tab placement</center></html>" );

			JComponent tab2 = createTab( "tab content 2" );
			tab2.setBorder( new LineBorder( Color.magenta ) );
			tabbedPane.addTab( "Second Tab", tab2 );

			addTab( tabbedPane, "Disabled", "tab content 3" );
			tabbedPane.setEnabledAt( 2, false );
		}
	}

	private void addTab( JTabbedPane tabbedPane, String title, String text ) {
		tabbedPane.addTab( title, createTab( text ) );
	}

	private JComponent createTab( String text ) {
		JLabel label = new JLabel( text );
		label.setHorizontalAlignment( SwingConstants.CENTER );

		JPanel tab = new JPanel( new BorderLayout() );
		tab.add( label, BorderLayout.CENTER );
		return tab;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel9 = new JPanel();
		JLabel tabbedPaneLabel = new JLabel();
		tabbedPane1 = new JTabbedPane();
		tabbedPane3 = new JTabbedPane();
		tabbedPane2 = new JTabbedPane();
		tabbedPane4 = new JTabbedPane();
		JPanel panel14 = new JPanel();
		moreTabsCheckBox = new JCheckBox();
		tabScrollCheckBox = new JCheckBox();
		showTabSeparatorsCheckBox = new JCheckBox();
		hideContentSeparatorCheckBox = new JCheckBox();
		hasFullBorderCheckBox = new JCheckBox();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[grow,fill]",
			// rows
			"[grow,fill]"));

		//======== panel9 ========
		{
			panel9.setLayout(new FormLayout(
				"70dlu:grow, $ugap, 70dlu:grow",
				"pref, $lgap, 2*(fill:80dlu:grow, $ugap), pref"));

			//---- tabbedPaneLabel ----
			tabbedPaneLabel.setText("JTabbedPane:");
			panel9.add(tabbedPaneLabel, cc.xy(1, 1));
			panel9.add(tabbedPane1, cc.xy(1, 3));

			//======== tabbedPane3 ========
			{
				tabbedPane3.setTabPlacement(SwingConstants.LEFT);
			}
			panel9.add(tabbedPane3, cc.xy(3, 3));

			//======== tabbedPane2 ========
			{
				tabbedPane2.setTabPlacement(SwingConstants.BOTTOM);
			}
			panel9.add(tabbedPane2, cc.xy(1, 5));

			//======== tabbedPane4 ========
			{
				tabbedPane4.setTabPlacement(SwingConstants.RIGHT);
			}
			panel9.add(tabbedPane4, cc.xy(3, 5));

			//======== panel14 ========
			{
				panel14.setLayout(new MigLayout(
					"insets 0,hidemode 3",
					// columns
					"[]" +
					"[]" +
					"[]" +
					"[fill]" +
					"[]",
					// rows
					"[center]"));

				//---- moreTabsCheckBox ----
				moreTabsCheckBox.setText("More tabs");
				moreTabsCheckBox.setMnemonic('M');
				moreTabsCheckBox.addActionListener(e -> moreTabsChanged());
				panel14.add(moreTabsCheckBox, "cell 0 0");

				//---- tabScrollCheckBox ----
				tabScrollCheckBox.setText("Use scroll layout");
				tabScrollCheckBox.setMnemonic('S');
				tabScrollCheckBox.addActionListener(e -> tabScrollChanged());
				panel14.add(tabScrollCheckBox, "cell 1 0,alignx left,growx 0");

				//---- showTabSeparatorsCheckBox ----
				showTabSeparatorsCheckBox.setText("Show tab separators");
				showTabSeparatorsCheckBox.addActionListener(e -> showTabSeparatorsChanged());
				panel14.add(showTabSeparatorsCheckBox, "cell 2 0");

				//---- hideContentSeparatorCheckBox ----
				hideContentSeparatorCheckBox.setText("Hide content separator");
				hideContentSeparatorCheckBox.addActionListener(e -> hideContentSeparatorChanged());
				panel14.add(hideContentSeparatorCheckBox, "cell 3 0");

				//---- hasFullBorderCheckBox ----
				hasFullBorderCheckBox.setText("Show content border");
				hasFullBorderCheckBox.addActionListener(e -> hasFullBorderChanged());
				panel14.add(hasFullBorderCheckBox, "cell 4 0,alignx left,growx 0");
			}
			panel9.add(panel14, cc.xywh(1, 7, 3, 1));
		}
		add(panel9, "cell 0 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTabbedPane tabbedPane1;
	private JTabbedPane tabbedPane3;
	private JTabbedPane tabbedPane2;
	private JTabbedPane tabbedPane4;
	private JCheckBox moreTabsCheckBox;
	private JCheckBox tabScrollCheckBox;
	private JCheckBox showTabSeparatorsCheckBox;
	private JCheckBox hideContentSeparatorCheckBox;
	private JCheckBox hasFullBorderCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
