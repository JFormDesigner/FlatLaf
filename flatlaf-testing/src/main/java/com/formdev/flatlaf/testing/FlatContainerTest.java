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

package com.formdev.flatlaf.testing;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_HAS_FULL_BORDER;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_SHOW_CONTENT_SEPARATOR;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_SHOW_TAB_SEPARATORS;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.util.ScaledImageIcon;
import com.jgoodies.forms.layout.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatContainerTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatContainerTest" );
			frame.showFrame( FlatContainerTest::new );
		} );
	}

	public FlatContainerTest() {
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
			if( tabCount > 4 ) {
				for( int i = 0; i < 5; i++ )
					tabbedPane.removeTabAt( tabbedPane.getTabCount() - 1 );
			}
		}
	}

	private void addInitialTabs( JTabbedPane... tabbedPanes ) {
		for( JTabbedPane tabbedPane : tabbedPanes ) {
			tabbedPane.addTab( "Tab 1", new Panel1() );

			JComponent tab2 = new Panel2();
			tab2.setBorder( new LineBorder( Color.magenta ) );
			tabbedPane.addTab( "Second Tab", tab2 );

			addTab( tabbedPane, "Disabled", "tab content 3" );
			tabbedPane.setEnabledAt( 2, false );

			tabbedPane.addTab( "Tab 4", new JLabel( "non-opaque content", SwingConstants.CENTER ) );
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

	private void tabIconsChanged() {
		boolean showTabIcons = tabIconsCheckBox.isSelected();

		setTabIcons( tabbedPane1, showTabIcons );
		setTabIcons( tabbedPane2, showTabIcons );
		setTabIcons( tabbedPane3, showTabIcons );
		setTabIcons( tabbedPane4, showTabIcons );

		tabIconSizeSpinner.setEnabled( showTabIcons );
	}

	private void setTabIcons( JTabbedPane tabbedPane, boolean showTabIcons ) {
		Object iconSize = tabIconSizeSpinner.getValue();

		Icon icon = showTabIcons
			? new ScaledImageIcon( new ImageIcon( getClass().getResource( "/com/formdev/flatlaf/testing/test" + iconSize + ".png" ) ) )
			: null;
		tabbedPane.setIconAt( 0, icon );
		tabbedPane.setIconAt( 1, icon );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel9 = new JPanel();
		JLabel splitPaneLabel = new JLabel();
		JSplitPane splitPane3 = new JSplitPane();
		JSplitPane splitPane1 = new JSplitPane();
		FlatContainerTest.Panel1 panel15 = new FlatContainerTest.Panel1();
		FlatContainerTest.Panel2 panel21 = new FlatContainerTest.Panel2();
		JSplitPane splitPane2 = new JSplitPane();
		JPanel panel12 = new JPanel();
		JLabel label3 = new JLabel();
		JPanel panel13 = new JPanel();
		JLabel label4 = new JLabel();
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
		tabIconsCheckBox = new JCheckBox();
		tabIconSizeSpinner = new JSpinner();
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
			panel9.setOpaque(false);
			panel9.setLayout(new FormLayout(
				"70dlu:grow, $ugap, 70dlu:grow",
				"default, $lgap, fill:70dlu, $pgap, pref, $lgap, 2*(fill:80dlu:grow, $ugap), pref"));

			//---- splitPaneLabel ----
			splitPaneLabel.setText("JSplitPane:");
			panel9.add(splitPaneLabel, cc.xy(1, 1));

			//======== splitPane3 ========
			{
				splitPane3.setResizeWeight(0.5);

				//======== splitPane1 ========
				{
					splitPane1.setResizeWeight(0.5);
					splitPane1.setOneTouchExpandable(true);

					//---- panel15 ----
					panel15.setBackground(new Color(217, 163, 67));
					splitPane1.setLeftComponent(panel15);

					//---- panel21 ----
					panel21.setBackground(new Color(98, 181, 67));
					splitPane1.setRightComponent(panel21);
				}
				splitPane3.setLeftComponent(splitPane1);

				//======== splitPane2 ========
				{
					splitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
					splitPane2.setResizeWeight(0.5);
					splitPane2.setOneTouchExpandable(true);

					//======== panel12 ========
					{
						panel12.setBackground(new Color(242, 101, 34));
						panel12.setLayout(new BorderLayout());

						//---- label3 ----
						label3.setText("TOP");
						label3.setHorizontalAlignment(SwingConstants.CENTER);
						label3.setForeground(Color.white);
						panel12.add(label3, BorderLayout.CENTER);
					}
					splitPane2.setTopComponent(panel12);

					//======== panel13 ========
					{
						panel13.setBackground(new Color(64, 182, 224));
						panel13.setLayout(new BorderLayout());

						//---- label4 ----
						label4.setText("BOTTOM");
						label4.setHorizontalAlignment(SwingConstants.CENTER);
						label4.setForeground(Color.white);
						panel13.add(label4, BorderLayout.CENTER);
					}
					splitPane2.setBottomComponent(panel13);
				}
				splitPane3.setRightComponent(splitPane2);
			}
			panel9.add(splitPane3, cc.xywh(1, 3, 3, 1));

			//---- tabbedPaneLabel ----
			tabbedPaneLabel.setText("JTabbedPane:");
			panel9.add(tabbedPaneLabel, cc.xy(1, 5));
			panel9.add(tabbedPane1, cc.xy(1, 7));

			//======== tabbedPane3 ========
			{
				tabbedPane3.setTabPlacement(SwingConstants.LEFT);
			}
			panel9.add(tabbedPane3, cc.xy(3, 7));

			//======== tabbedPane2 ========
			{
				tabbedPane2.setTabPlacement(SwingConstants.BOTTOM);
			}
			panel9.add(tabbedPane2, cc.xy(1, 9));

			//======== tabbedPane4 ========
			{
				tabbedPane4.setTabPlacement(SwingConstants.RIGHT);
			}
			panel9.add(tabbedPane4, cc.xy(3, 9));

			//======== panel14 ========
			{
				panel14.setOpaque(false);
				panel14.setLayout(new MigLayout(
					"insets 0,hidemode 3",
					// columns
					"[]" +
					"[]" +
					"[]" +
					"[fill]" +
					"[]" +
					"[fill]" +
					"[fill]",
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

				//---- tabIconsCheckBox ----
				tabIconsCheckBox.setText("Tab icons");
				tabIconsCheckBox.addActionListener(e -> tabIconsChanged());
				panel14.add(tabIconsCheckBox, "cell 5 0");

				//---- tabIconSizeSpinner ----
				tabIconSizeSpinner.setModel(new SpinnerListModel(new String[] {"16", "24", "32", "48", "64"}));
				tabIconSizeSpinner.setEnabled(false);
				tabIconSizeSpinner.addChangeListener(e -> tabIconsChanged());
				panel14.add(tabIconSizeSpinner, "cell 6 0");
			}
			panel9.add(panel14, cc.xywh(1, 11, 3, 1));
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
	private JCheckBox tabIconsCheckBox;
	private JSpinner tabIconSizeSpinner;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class Tab1Panel ----------------------------------------------------

	private static class Panel1
		extends JPanel
	{
		private Panel1() {
			initComponents();
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			JLabel label1 = new JLabel();
			JTextField textField4 = new JTextField();
			JButton button3 = new JButton();

			//======== this ========
			setLayout(new MigLayout(
				"hidemode 3,align center center",
				// columns
				"[fill]" +
				"[fill]" +
				"[fill]",
				// rows
				"[fill]"));

			//---- label1 ----
			label1.setText("text");
			add(label1, "cell 0 0");

			//---- textField4 ----
			textField4.setText("some text");
			add(textField4, "cell 1 0");

			//---- button3 ----
			button3.setText("...");
			add(button3, "cell 2 0");
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}

	//---- class Tab2Panel ----------------------------------------------------

	private static class Panel2
		extends JPanel
	{
		private Panel2() {
			initComponents();
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			JTextField textField5 = new JTextField();
			JButton button4 = new JButton();

			//======== this ========
			setLayout(new MigLayout(
				"insets 0,hidemode 3,align center center",
				// columns
				"[fill]" +
				"[fill]",
				// rows
				"[fill]"));

			//---- textField5 ----
			textField5.setText("more text");
			add(textField5, "cell 0 0");

			//---- button4 ----
			button4.setText("...");
			add(button4, "cell 1 0");
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}
}
