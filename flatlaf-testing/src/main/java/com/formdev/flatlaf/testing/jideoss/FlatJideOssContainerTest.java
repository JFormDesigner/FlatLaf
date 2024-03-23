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

package com.formdev.flatlaf.testing.jideoss;

import java.awt.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatInternalFrameCloseIcon;
import com.formdev.flatlaf.testing.*;
import com.formdev.flatlaf.testing.FlatTestFrame;
import com.formdev.flatlaf.util.ScaledImageIcon;
import com.jgoodies.forms.layout.*;
import com.jidesoft.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatJideOssContainerTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatJideOssContainerTest" );
			frame.useApplyComponentOrientation = true;
			frame.showFrame( FlatJideOssContainerTest::new );
		} );
	}

	FlatJideOssContainerTest() {
		initComponents();

		tabPlacementField.init( TabPlacement.class, true );
		tabAlignmentField.init( JideTabAlignment.class, false );
		tabResizeModeField.init( JideTabResizeMode.class, true );

		tabCountChanged();

		tabsClosableCheckBox.setSelected( true );
		tabsClosableChanged();

		tabScrollCheckBox.setSelected( true );
		tabScrollChanged();
	}

	private void tabScrollChanged() {
		// JideTabbedPane supports tab closing only in scroll layout
		// --> turn of if necessary to avoid exceptions in BasicJideTabbedPaneUI
		tabsClosableChanged();

		int tabLayoutPolicy = tabScrollCheckBox.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT;
		for( JTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabLayoutPolicy( tabLayoutPolicy );

		int tabCount = (Integer) tabCountSpinner.getValue();
		if( tabLayoutPolicy == JTabbedPane.SCROLL_TAB_LAYOUT && tabCount == 4 )
			tabCountSpinner.setValue( 8 );
		else if( tabLayoutPolicy == JTabbedPane.WRAP_TAB_LAYOUT && tabCount == 8 )
			tabCountSpinner.setValue( 4 );
	}

	private void showTabSeparatorsChanged() {
		Boolean showTabSeparators = showTabSeparatorsCheckBox.isSelected() ? true : null;
		putTabbedPanesClientProperty( FlatClientProperties.TABBED_PANE_SHOW_TAB_SEPARATORS, showTabSeparators );
	}

	private void hideTabAreaWithOneTabChanged() {
		boolean hideTabAreaWithOneTab = hideTabAreaWithOneTabCheckBox.isSelected();
		for( JideTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setHideOneTab( hideTabAreaWithOneTab );
	}

	private void hasFullBorderChanged() {
		Boolean hasFullBorder = hasFullBorderCheckBox.isSelected() ? true : null;
		putTabbedPanesClientProperty( FlatClientProperties.TABBED_PANE_HAS_FULL_BORDER, hasFullBorder );
	}

	private void putTabbedPanesClientProperty( String key, Object value ) {
		for( JTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.putClientProperty( key, value );
	}

	private void tabCountChanged() {
		for( JideTabbedPane tabbedPane : allTabbedPanes )
			tabCountChanged( tabbedPane );
	}

	private void tabCountChanged( JideTabbedPane tabbedPane ) {
		int oldTabCount = tabbedPane.getTabCount();
		int newTabCount = (Integer) tabCountSpinner.getValue();

		if( newTabCount > oldTabCount ) {
			for( int i = oldTabCount + 1; i <= newTabCount; i++ )
				addTab( tabbedPane );
		} else if( newTabCount < oldTabCount ) {
			while( tabbedPane.getTabCount() > newTabCount )
				tabbedPane.removeTabAt( tabbedPane.getTabCount() - 1 );
		}

		customTabsChanged( tabbedPane );
		tabBackForegroundChanged( tabbedPane );
		setTabIcons( tabbedPane );
	}

	private void addTab( JideTabbedPane tabbedPane ) {
		switch( tabbedPane.getTabCount() ) {
			case 0:
				tabbedPane.addTab( "Tab 1", null, new Panel1(), "First tab." );
				break;

			case 1:
				JComponent tab2 = new Panel2();
				tab2.setBorder( new LineBorder( Color.magenta ) );
				tabbedPane.addTab( "Second Tab", null, tab2, "This is the second tab." );
				break;

			case 2:
				tabbedPane.addTab( "Disabled", createTab( "tab content 3" ) );
				tabbedPane.setEnabledAt( 2, false );
				tabbedPane.setToolTipTextAt( 2, "Disabled tab." );
				break;

			case 3:
				tabbedPane.addTab( "Tab 4", new JLabel( "non-opaque content", SwingConstants.CENTER ) );
				break;

			case 4:
				tabbedPane.addTab( "Tab 5", new JLabel( "random background content", SwingConstants.CENTER ) {
					Random random = new Random();

					@Override
					protected void paintComponent( Graphics g ) {
						g.setColor( new Color( random.nextInt() ) );
						g.fillRect( 0, 0, getWidth(), getHeight() );

						super.paintComponent( g );
					}
				} );
				break;

			default:
				int index = tabbedPane.getTabCount() + 1;
				tabbedPane.addTab( "Tab " + index, createTab( "tab content " + index ) );
				break;
		}
	}

	private JComponent createTab( String text ) {
		JLabel label = new JLabel( text );
		label.setHorizontalAlignment( SwingConstants.CENTER );

		JPanel tab = new JPanel( new BorderLayout() );
		tab.add( label, BorderLayout.CENTER );
		return tab;
	}

	private void tabIconsChanged() {
		for( JTabbedPane tabbedPane : allTabbedPanes )
			setTabIcons( tabbedPane );

		tabIconSizeSpinner.setEnabled( tabIconsCheckBox.isSelected() );
	}

	private void setTabIcons( JTabbedPane tabbedPane ) {
		boolean showTabIcons = tabIconsCheckBox.isSelected();
		Object iconSize = tabIconSizeSpinner.getValue();

		Icon icon = null;
		Icon disabledIcon = null;
		if( showTabIcons ) {
			ImageIcon imageIcon = new ImageIcon( getClass().getResource( "/com/formdev/flatlaf/testing/test" + iconSize + ".png" ) );
			icon = new ScaledImageIcon( imageIcon );
			disabledIcon = UIManager.getLookAndFeel().getDisabledIcon( tabbedPane, imageIcon );
			if( disabledIcon instanceof ImageIcon )
				disabledIcon = new ScaledImageIcon( (ImageIcon) disabledIcon );
		}

		int tabCount = tabbedPane.getTabCount();
		for( int i = 0; i < tabCount; i++ ) {
			tabbedPane.setIconAt( i, icon );
			tabbedPane.setDisabledIconAt( i, disabledIcon );
		}
	}

	private void customBorderChanged() {
		Border border = customBorderCheckBox.isSelected()
			? new MatteBorder( 10, 20, 25, 35, Color.green )
			: null;

		for( JTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setBorder( border );
	}

	private void customTabsChanged() {
		for( JTabbedPane tabbedPane : allTabbedPanes )
			customTabsChanged( tabbedPane );
	}

	private void customTabsChanged( JTabbedPane tabbedPane ) {
		boolean customTabs = customTabsCheckBox.isSelected();
		int tabCount = tabbedPane.getTabCount();
		if( tabCount > 1 )
			tabbedPane.setTabComponentAt( 1, customTabs ? new JButton( tabbedPane.getTitleAt( 1 ) ) : null );
		if( tabCount > 3 )
			tabbedPane.setTabComponentAt( 3, customTabs ? createCustomTab( tabbedPane.getTitleAt( 3 ) ) : null );
		if( tabCount > 5 )
			tabbedPane.setTabComponentAt( 5, customTabs ? new JCheckBox( tabbedPane.getTitleAt( 5 ) ) : null );
	}

	private Component createCustomTab( String tabTitle ) {
		JButton closeButton;
		if( UIManager.getLookAndFeel() instanceof FlatLaf ) {
			closeButton = new JButton( new FlatInternalFrameCloseIcon() );
			closeButton.setContentAreaFilled( false );
			closeButton.setBorder( null );
		} else
			closeButton = new JButton( "x" );

		JPanel tab = new JPanel( new BorderLayout( 5, 0 ) );
		tab.setOpaque( false );
		tab.add( closeButton, BorderLayout.EAST );
		tab.add( new JLabel( tabTitle ), BorderLayout.CENTER );
		return tab;
	}

	private void htmlTabsChanged() {
		for( JTabbedPane tabbedPane : allTabbedPanes )
			htmlTabsChanged( tabbedPane );
	}

	private void htmlTabsChanged( JTabbedPane tabbedPane ) {
		boolean html = htmlTabsCheckBox.isSelected();
		boolean multiLine = multiLineTabsCheckBox.isSelected();
		String s = multiLine
			? "<html><b>Bold</b> Tab<br>Second <i>Line</i> "
			: (html ? "<html><b>Bold</b> Tab " : "Tab ");
		int tabCount = tabbedPane.getTabCount();
		if( tabCount > 0 )
			tabbedPane.setTitleAt( 0, s + "1" );
		if( tabCount > 3 )
			tabbedPane.setTitleAt( 3, s + "4" );
	}

	private void tabPlacementChanged() {
		TabPlacement value = tabPlacementField.getSelectedValue();
		int tabPlacement = (value != null) ? value.value : -1;

		tabbedPane1.setTabPlacement( (tabPlacement >= 0) ? tabPlacement : SwingConstants.TOP );
		tabbedPane2.setTabPlacement( (tabPlacement >= 0) ? tabPlacement : SwingConstants.BOTTOM );
		tabbedPane3.setTabPlacement( (tabPlacement >= 0) ? tabPlacement : SwingConstants.LEFT );
		tabbedPane4.setTabPlacement( (tabPlacement >= 0) ? tabPlacement : SwingConstants.RIGHT );
	}

	private void tabAlignmentChanged() {
		JideTabAlignment value = tabAlignmentField.getSelectedValue();
		for( JideTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabAlignment( value.value );
	}

	private void tabResizeModeChanged() {
		JideTabResizeMode value = tabResizeModeField.getSelectedValue();
		int resizeMode = (value != null) ? value.value : JideTabbedPane.RESIZE_MODE_DEFAULT;
		for( JideTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabResizeMode( resizeMode );
	}

	private void tabBackForegroundChanged() {
		for( JTabbedPane tabbedPane : allTabbedPanes )
			tabBackForegroundChanged( tabbedPane );
	}

	private void tabBackForegroundChanged( JTabbedPane tabbedPane ) {
		boolean enabled = tabBackForegroundCheckBox.isSelected();
		int tabCount = tabbedPane.getTabCount();
		if( tabCount > 0 )
			tabbedPane.setBackgroundAt( 0, enabled ? Color.red : null );
		if( tabCount > 1 )
			tabbedPane.setForegroundAt( 1, enabled ? Color.red : null );
	}

	private void leadingComponentChanged() {
		leadingTrailingComponentChanged( leadingComponentCheckBox.isSelected(), true, "L", 4 );
	}

	private void trailingComponentChanged() {
		leadingTrailingComponentChanged( trailingComponentCheckBox.isSelected(), false, "Trailing", 12 );
	}

	private void leadingTrailingComponentChanged( boolean enabled, boolean leading, String text, int gap ) {
		for( JideTabbedPane tabbedPane : allTabbedPanes ) {
			JComponent c = null;
			if( enabled ) {
				c = new JLabel( text );
				c.setOpaque( true );
				c.setBackground( leading ? Color.cyan : Color.orange );
				c.setBorder( new EmptyBorder( gap, gap, gap, gap ) );
				if( leading && (tabbedPane.getTabPlacement() == SwingConstants.TOP || tabbedPane.getTabPlacement() == SwingConstants.BOTTOM) )
					c.setPreferredSize( new Dimension( 20, 80 ) );
			}
			if( leading )
				tabbedPane.setTabLeadingComponent( c );
			else
				tabbedPane.setTabTrailingComponent( c );
		}
	}

	private void tabsClosableChanged() {
		boolean closable = tabsClosableCheckBox.isSelected() && tabScrollCheckBox.isSelected();
		boolean onTab = closable && showCloseButtonOnTabCheckBox.isSelected();

		for( JideTabbedPane tabbedPane : allTabbedPanes ) {
			tabbedPane.setShowCloseButtonOnTab( onTab );
			tabbedPane.setShowCloseButton( closable );
		}
	}

	private void secondTabClosableChanged() {
		boolean closable = secondTabClosableCheckBox.isSelected();

		for( JideTabbedPane tabbedPane : allTabbedPanes ) {
			if( tabbedPane.getTabCount() > 1 )
				tabbedPane.setTabClosableAt( 1, closable );
		}
	}

	private void showCloseButtonOnSelectedTabChanged() {
		boolean onSelected = showCloseButtonOnSelectedTabCheckBox.isSelected();

		for( JideTabbedPane tabbedPane : allTabbedPanes ) {
			tabbedPane.setShowCloseButtonOnSelectedTab( onSelected );
			tabbedPane.revalidate();
			tabbedPane.repaint();
		}
	}

	private void showCloseButtonOnMouseOverChanged() {
		boolean onMouseOver = showCloseButtonOnMouseOverCheckBox.isSelected();

		for( JideTabbedPane tabbedPane : allTabbedPanes ) {
			tabbedPane.setShowCloseButtonOnMouseOver( onMouseOver );
			tabbedPane.revalidate();
			tabbedPane.repaint();
		}
	}

	private void tabAreaInsetsChanged() {
		Insets insets = tabAreaInsetsCheckBox.isSelected() ? new Insets( 5, 5, 10, 10 ) : null;
		for( JideTabbedPane tabbedPane : allTabbedPanes ) {
			tabbedPane.setTabAreaInsets( insets );
			tabbedPane.revalidate();
			tabbedPane.repaint();
		}
	}

	private void smallerInsetsChanged() {
		Insets insets = smallerInsetsCheckBox.isSelected()
			? new Insets( 2, 2, 2, 2 )
			: UIManager.getInsets( "JideTabbedPane.tabInsets" );
		for( JideTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabInsets( insets );
	}

	private void boldActiveTabChanged() {
		boolean boldActiveTab = boldActiveTabCheckBox.isSelected();

		for( JideTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setBoldActiveTab( boldActiveTab );
	}

	private void showTabButtonsChanged() {
		boolean showTabButtons = showTabButtonsCheckBox.isSelected();

		for( JideTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setShowTabButtons( showTabButtons );
	}

	private void showGripperChanged() {
		boolean showGripper = showGripperCheckBox.isSelected();

		for( JideTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setShowGripper( showGripper );
	}

	private void tabEditingAllowedChanged() {
		boolean tabEditingAllowed = tabEditingAllowedCheckBox.isSelected();

		for( JideTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabEditingAllowed( tabEditingAllowed );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel9 = new JPanel();
		JLabel tabbedPaneLabel = new JLabel();
		tabbedPane1 = new JideTabbedPane();
		tabbedPane3 = new JideTabbedPane();
		tabbedPane2 = new JideTabbedPane();
		tabbedPane4 = new JideTabbedPane();
		FlatTestFrame.NoRightToLeftPanel tabbedPaneControlPanel = new FlatTestFrame.NoRightToLeftPanel();
		tabScrollCheckBox = new JCheckBox();
		JLabel tabCountLabel = new JLabel();
		tabCountSpinner = new JSpinner();
		customTabsCheckBox = new JCheckBox();
		htmlTabsCheckBox = new JCheckBox();
		multiLineTabsCheckBox = new JCheckBox();
		tabBackForegroundCheckBox = new JCheckBox();
		tabIconsCheckBox = new JCheckBox();
		tabIconSizeSpinner = new JSpinner();
		tabsClosableCheckBox = new JCheckBox();
		showCloseButtonOnTabCheckBox = new JCheckBox();
		showCloseButtonOnSelectedTabCheckBox = new JCheckBox();
		JLabel tabPlacementLabel = new JLabel();
		tabPlacementField = new FlatTestEnumSelector<>();
		secondTabClosableCheckBox = new JCheckBox();
		showCloseButtonOnMouseOverCheckBox = new JCheckBox();
		JLabel tabAreaAlignmentLabel = new JLabel();
		tabAlignmentField = new FlatTestEnumSelector<>();
		JLabel tabResizeModeLabel = new JLabel();
		tabResizeModeField = new FlatTestEnumSelector<>();
		leadingComponentCheckBox = new JCheckBox();
		customBorderCheckBox = new JCheckBox();
		tabAreaInsetsCheckBox = new JCheckBox();
		trailingComponentCheckBox = new JCheckBox();
		hasFullBorderCheckBox = new JCheckBox();
		boldActiveTabCheckBox = new JCheckBox();
		showTabButtonsCheckBox = new JCheckBox();
		smallerInsetsCheckBox = new JCheckBox();
		showGripperCheckBox = new JCheckBox();
		showTabSeparatorsCheckBox = new JCheckBox();
		tabEditingAllowedCheckBox = new JCheckBox();
		hideTabAreaWithOneTabCheckBox = new JCheckBox();
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
				"pref, $lgap, fill:80dlu:grow, $ugap, fill:80dlu:grow, $pgap, pref"));

			//---- tabbedPaneLabel ----
			tabbedPaneLabel.setText("JideTabbedPane:");
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

			//======== tabbedPaneControlPanel ========
			{
				tabbedPaneControlPanel.setOpaque(false);
				tabbedPaneControlPanel.setLayout(new MigLayout(
					"insets 0,hidemode 3",
					// columns
					"[]" +
					"[]" +
					"[]",
					// rows
					"[center]" +
					"[]" +
					"[]" +
					"[]" +
					"[]" +
					"[]para" +
					"[]" +
					"[]para" +
					"[]" +
					"[]" +
					"[]"));

				//---- tabScrollCheckBox ----
				tabScrollCheckBox.setText("Use scroll layout");
				tabScrollCheckBox.setMnemonic('S');
				tabScrollCheckBox.addActionListener(e -> tabScrollChanged());
				tabbedPaneControlPanel.add(tabScrollCheckBox, "cell 0 0,alignx left,growx 0");

				//---- tabCountLabel ----
				tabCountLabel.setText("Tab count:");
				tabbedPaneControlPanel.add(tabCountLabel, "cell 1 0");

				//---- tabCountSpinner ----
				tabCountSpinner.setModel(new SpinnerNumberModel(4, 0, null, 1));
				tabCountSpinner.addChangeListener(e -> tabCountChanged());
				tabbedPaneControlPanel.add(tabCountSpinner, "cell 1 0,width 80");

				//---- customTabsCheckBox ----
				customTabsCheckBox.setText("Custom tabs");
				customTabsCheckBox.addActionListener(e -> customTabsChanged());
				tabbedPaneControlPanel.add(customTabsCheckBox, "cell 2 0");

				//---- htmlTabsCheckBox ----
				htmlTabsCheckBox.setText("HTML");
				htmlTabsCheckBox.addActionListener(e -> htmlTabsChanged());
				tabbedPaneControlPanel.add(htmlTabsCheckBox, "cell 2 0");

				//---- multiLineTabsCheckBox ----
				multiLineTabsCheckBox.setText("multi-line");
				multiLineTabsCheckBox.addActionListener(e -> htmlTabsChanged());
				tabbedPaneControlPanel.add(multiLineTabsCheckBox, "cell 2 0");

				//---- tabBackForegroundCheckBox ----
				tabBackForegroundCheckBox.setText("Tab back/foreground");
				tabBackForegroundCheckBox.addActionListener(e -> tabBackForegroundChanged());
				tabbedPaneControlPanel.add(tabBackForegroundCheckBox, "cell 2 1");

				//---- tabIconsCheckBox ----
				tabIconsCheckBox.setText("Tab icons");
				tabIconsCheckBox.addActionListener(e -> tabIconsChanged());
				tabbedPaneControlPanel.add(tabIconsCheckBox, "cell 2 2");

				//---- tabIconSizeSpinner ----
				tabIconSizeSpinner.setModel(new SpinnerListModel(new String[] {"16", "24", "32", "48", "64"}));
				tabIconSizeSpinner.setEnabled(false);
				tabIconSizeSpinner.addChangeListener(e -> tabIconsChanged());
				tabbedPaneControlPanel.add(tabIconSizeSpinner, "cell 2 2");

				//---- tabsClosableCheckBox ----
				tabsClosableCheckBox.setText("Tabs closable");
				tabsClosableCheckBox.addActionListener(e -> tabsClosableChanged());
				tabbedPaneControlPanel.add(tabsClosableCheckBox, "cell 2 3");

				//---- showCloseButtonOnTabCheckBox ----
				showCloseButtonOnTabCheckBox.setText("on tab");
				showCloseButtonOnTabCheckBox.setSelected(true);
				showCloseButtonOnTabCheckBox.addActionListener(e -> tabsClosableChanged());
				tabbedPaneControlPanel.add(showCloseButtonOnTabCheckBox, "cell 2 3");

				//---- showCloseButtonOnSelectedTabCheckBox ----
				showCloseButtonOnSelectedTabCheckBox.setText("show on selected");
				showCloseButtonOnSelectedTabCheckBox.addActionListener(e -> showCloseButtonOnSelectedTabChanged());
				tabbedPaneControlPanel.add(showCloseButtonOnSelectedTabCheckBox, "cell 2 3");

				//---- tabPlacementLabel ----
				tabPlacementLabel.setText("Tab placement:");
				tabbedPaneControlPanel.add(tabPlacementLabel, "cell 0 4");

				//---- tabPlacementField ----
				tabPlacementField.addActionListener(e -> tabPlacementChanged());
				tabbedPaneControlPanel.add(tabPlacementField, "cell 1 4");

				//---- secondTabClosableCheckBox ----
				secondTabClosableCheckBox.setText("Second Tab closable");
				secondTabClosableCheckBox.setSelected(true);
				secondTabClosableCheckBox.addActionListener(e -> secondTabClosableChanged());
				tabbedPaneControlPanel.add(secondTabClosableCheckBox, "cell 2 4");

				//---- showCloseButtonOnMouseOverCheckBox ----
				showCloseButtonOnMouseOverCheckBox.setText("show on hover");
				showCloseButtonOnMouseOverCheckBox.addActionListener(e -> showCloseButtonOnMouseOverChanged());
				tabbedPaneControlPanel.add(showCloseButtonOnMouseOverCheckBox, "cell 2 4");

				//---- tabAreaAlignmentLabel ----
				tabAreaAlignmentLabel.setText("Tab alignment:");
				tabbedPaneControlPanel.add(tabAreaAlignmentLabel, "cell 0 5");

				//---- tabAlignmentField ----
				tabAlignmentField.addActionListener(e -> tabAlignmentChanged());
				tabbedPaneControlPanel.add(tabAlignmentField, "cell 1 5");

				//---- tabResizeModeLabel ----
				tabResizeModeLabel.setText("Tab resize mode:");
				tabbedPaneControlPanel.add(tabResizeModeLabel, "cell 2 5");

				//---- tabResizeModeField ----
				tabResizeModeField.addActionListener(e -> tabResizeModeChanged());
				tabbedPaneControlPanel.add(tabResizeModeField, "cell 2 5");

				//---- leadingComponentCheckBox ----
				leadingComponentCheckBox.setText("Leading component");
				leadingComponentCheckBox.addActionListener(e -> leadingComponentChanged());
				tabbedPaneControlPanel.add(leadingComponentCheckBox, "cell 0 6");

				//---- customBorderCheckBox ----
				customBorderCheckBox.setText("Custom border");
				customBorderCheckBox.addActionListener(e -> customBorderChanged());
				tabbedPaneControlPanel.add(customBorderCheckBox, "cell 1 6");

				//---- tabAreaInsetsCheckBox ----
				tabAreaInsetsCheckBox.setText("Tab area insets (5,5,10,10)");
				tabAreaInsetsCheckBox.addActionListener(e -> tabAreaInsetsChanged());
				tabbedPaneControlPanel.add(tabAreaInsetsCheckBox, "cell 2 6");

				//---- trailingComponentCheckBox ----
				trailingComponentCheckBox.setText("Trailing component");
				trailingComponentCheckBox.addActionListener(e -> trailingComponentChanged());
				tabbedPaneControlPanel.add(trailingComponentCheckBox, "cell 0 7");

				//---- hasFullBorderCheckBox ----
				hasFullBorderCheckBox.setText("Show content border");
				hasFullBorderCheckBox.addActionListener(e -> hasFullBorderChanged());
				tabbedPaneControlPanel.add(hasFullBorderCheckBox, "cell 1 7,alignx left,growx 0");

				//---- boldActiveTabCheckBox ----
				boldActiveTabCheckBox.setText("Bold active tab");
				boldActiveTabCheckBox.addActionListener(e -> boldActiveTabChanged());
				tabbedPaneControlPanel.add(boldActiveTabCheckBox, "cell 0 8");

				//---- showTabButtonsCheckBox ----
				showTabButtonsCheckBox.setText("Show tab buttons always");
				showTabButtonsCheckBox.addActionListener(e -> showTabButtonsChanged());
				tabbedPaneControlPanel.add(showTabButtonsCheckBox, "cell 1 8");

				//---- smallerInsetsCheckBox ----
				smallerInsetsCheckBox.setText("Smaller tab insets (2,2,2,2)");
				smallerInsetsCheckBox.addActionListener(e -> smallerInsetsChanged());
				tabbedPaneControlPanel.add(smallerInsetsCheckBox, "cell 2 8");

				//---- showGripperCheckBox ----
				showGripperCheckBox.setText("Show gripper");
				showGripperCheckBox.addActionListener(e -> showGripperChanged());
				tabbedPaneControlPanel.add(showGripperCheckBox, "cell 0 9");

				//---- showTabSeparatorsCheckBox ----
				showTabSeparatorsCheckBox.setText("Show tab separators");
				showTabSeparatorsCheckBox.addActionListener(e -> showTabSeparatorsChanged());
				tabbedPaneControlPanel.add(showTabSeparatorsCheckBox, "cell 1 9");

				//---- tabEditingAllowedCheckBox ----
				tabEditingAllowedCheckBox.setText("Tab editing allowed");
				tabEditingAllowedCheckBox.addActionListener(e -> tabEditingAllowedChanged());
				tabbedPaneControlPanel.add(tabEditingAllowedCheckBox, "cell 0 10");

				//---- hideTabAreaWithOneTabCheckBox ----
				hideTabAreaWithOneTabCheckBox.setText("Hide tab area with one tab");
				hideTabAreaWithOneTabCheckBox.addActionListener(e -> hideTabAreaWithOneTabChanged());
				tabbedPaneControlPanel.add(hideTabAreaWithOneTabCheckBox, "cell 1 10");
			}
			panel9.add(tabbedPaneControlPanel, cc.xywh(1, 7, 3, 1));
		}
		add(panel9, "cell 0 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		allTabbedPanes = new JideTabbedPane[] { tabbedPane1, tabbedPane2, tabbedPane3, tabbedPane4 };
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JideTabbedPane tabbedPane1;
	private JideTabbedPane tabbedPane3;
	private JideTabbedPane tabbedPane2;
	private JideTabbedPane tabbedPane4;
	private JCheckBox tabScrollCheckBox;
	private JSpinner tabCountSpinner;
	private JCheckBox customTabsCheckBox;
	private JCheckBox htmlTabsCheckBox;
	private JCheckBox multiLineTabsCheckBox;
	private JCheckBox tabBackForegroundCheckBox;
	private JCheckBox tabIconsCheckBox;
	private JSpinner tabIconSizeSpinner;
	private JCheckBox tabsClosableCheckBox;
	private JCheckBox showCloseButtonOnTabCheckBox;
	private JCheckBox showCloseButtonOnSelectedTabCheckBox;
	private FlatTestEnumSelector<TabPlacement> tabPlacementField;
	private JCheckBox secondTabClosableCheckBox;
	private JCheckBox showCloseButtonOnMouseOverCheckBox;
	private FlatTestEnumSelector<JideTabAlignment> tabAlignmentField;
	private FlatTestEnumSelector<JideTabResizeMode> tabResizeModeField;
	private JCheckBox leadingComponentCheckBox;
	private JCheckBox customBorderCheckBox;
	private JCheckBox tabAreaInsetsCheckBox;
	private JCheckBox trailingComponentCheckBox;
	private JCheckBox hasFullBorderCheckBox;
	private JCheckBox boldActiveTabCheckBox;
	private JCheckBox showTabButtonsCheckBox;
	private JCheckBox smallerInsetsCheckBox;
	private JCheckBox showGripperCheckBox;
	private JCheckBox showTabSeparatorsCheckBox;
	private JCheckBox tabEditingAllowedCheckBox;
	private JCheckBox hideTabAreaWithOneTabCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	private JideTabbedPane[] allTabbedPanes;

	//---- enum TabPlacement --------------------------------------------------

	enum TabPlacement {
		top( SwingConstants.TOP ),
		bottom( SwingConstants.BOTTOM ),
		left( SwingConstants.LEFT ),
		right( SwingConstants.RIGHT );

		public final int value;

		TabPlacement( int value ) {
			this.value = value;
		}
	}

	//---- enum JideTabAlignment ----------------------------------------------

	enum JideTabAlignment {
		leading( SwingConstants.LEADING ),
		center( SwingConstants.CENTER );

		public final int value;

		JideTabAlignment( int value ) {
			this.value = value;
		}
	}

	//---- enum JideTabResizeMode ---------------------------------------------

	enum JideTabResizeMode {
		none( JideTabbedPane.RESIZE_MODE_NONE ),
		fit( JideTabbedPane.RESIZE_MODE_FIT ),
		fixed( JideTabbedPane.RESIZE_MODE_FIXED ),
		compressed( JideTabbedPane.RESIZE_MODE_COMPRESSED );

		public final int value;

		JideTabResizeMode( int value ) {
			this.value = value;
		}
	}

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
