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

import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.TriStateCheckBox;
import com.formdev.flatlaf.icons.FlatInternalFrameCloseIcon;
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
			frame.useApplyComponentOrientation = true;
			frame.showFrame( FlatContainerTest::new );
		} );
	}

	public FlatContainerTest() {
		initComponents();

		tabCountChanged();

		tabsClosableCheckBox.setSelected( true );
		tabsClosableChanged();
		putTabbedPanesClientProperty( TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close" );

		tabScrollCheckBox.setSelected( true );
		tabScrollChanged();
	}

	private void tabScrollChanged() {
		int tabLayoutPolicy = tabScrollCheckBox.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT;
		tabbedPane1.setTabLayoutPolicy( tabLayoutPolicy );
		tabbedPane2.setTabLayoutPolicy( tabLayoutPolicy );
		tabbedPane3.setTabLayoutPolicy( tabLayoutPolicy );
		tabbedPane4.setTabLayoutPolicy( tabLayoutPolicy );

		int tabCount = (Integer) tabCountSpinner.getValue();
		if( tabLayoutPolicy == JTabbedPane.SCROLL_TAB_LAYOUT && tabCount == 4 )
			tabCountSpinner.setValue( 8 );
		else if( tabLayoutPolicy == JTabbedPane.WRAP_TAB_LAYOUT && tabCount == 8 )
			tabCountSpinner.setValue( 4 );
	}

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

	private void tabCountChanged() {
		tabCountChanged( tabbedPane1 );
		tabCountChanged( tabbedPane2 );
		tabCountChanged( tabbedPane3 );
		tabCountChanged( tabbedPane4 );
	}

	private void tabCountChanged( JTabbedPane tabbedPane ) {
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

	private void addTab( JTabbedPane tabbedPane ) {
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
		setTabIcons( tabbedPane1 );
		setTabIcons( tabbedPane2 );
		setTabIcons( tabbedPane3 );
		setTabIcons( tabbedPane4 );

		tabIconSizeSpinner.setEnabled( tabIconsCheckBox.isSelected() );
	}

	private void setTabIcons( JTabbedPane tabbedPane ) {
		boolean showTabIcons = tabIconsCheckBox.isSelected();
		Object iconSize = tabIconSizeSpinner.getValue();

		Icon icon = showTabIcons
			? new ScaledImageIcon( new ImageIcon( getClass().getResource( "/com/formdev/flatlaf/testing/test" + iconSize + ".png" ) ) )
			: null;
		int tabCount = tabbedPane.getTabCount();
		for( int i = 0; i < tabCount; i++ )
			tabbedPane.setIconAt( i, icon );
	}

	private void customBorderChanged() {
		Border border = customBorderCheckBox.isSelected()
			? new MatteBorder( 10, 20, 25, 35, Color.green )
			: null;

		tabbedPane1.setBorder( border );
		tabbedPane2.setBorder( border );
		tabbedPane3.setBorder( border );
		tabbedPane4.setBorder( border );
	}

	private void customTabsChanged() {
		customTabsChanged( tabbedPane1 );
		customTabsChanged( tabbedPane2 );
		customTabsChanged( tabbedPane3 );
		customTabsChanged( tabbedPane4 );
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

	private void tabPlacementChanged() {
		int tabPlacement = -1;
		switch( (String) tabPlacementField.getSelectedItem() ) {
			case "top":		tabPlacement = SwingConstants.TOP; break;
			case "bottom":	tabPlacement = SwingConstants.BOTTOM; break;
			case "left":	tabPlacement = SwingConstants.LEFT; break;
			case "right":	tabPlacement = SwingConstants.RIGHT; break;
		}

		tabbedPane1.setTabPlacement( (tabPlacement >= 0) ? tabPlacement : SwingConstants.TOP );
		tabbedPane2.setTabPlacement( (tabPlacement >= 0) ? tabPlacement : SwingConstants.BOTTOM );
		tabbedPane3.setTabPlacement( (tabPlacement >= 0) ? tabPlacement : SwingConstants.LEFT );
		tabbedPane4.setTabPlacement( (tabPlacement >= 0) ? tabPlacement : SwingConstants.RIGHT );
	}

	private void hiddenTabsNavigationChanged() {
		String value = (String) hiddenTabsNavigationField.getSelectedItem();
		if( "default".equals( value ) )
			value = null;
		putTabbedPanesClientProperty( TABBED_PANE_HIDDEN_TABS_NAVIGATION, value );
	}

	private void tabAreaAlignmentChanged() {
		String value = (String) tabAreaAlignmentField.getSelectedItem();
		if( "default".equals( value ) )
			value = null;
		putTabbedPanesClientProperty( TABBED_PANE_TAB_AREA_ALIGNMENT, value );
	}

	private void tabWidthModeChanged() {
		String value = (String) tabWidthModeField.getSelectedItem();
		if( "default".equals( value ) )
			value = null;
		putTabbedPanesClientProperty( TABBED_PANE_TAB_WIDTH_MODE, value );
	}

	private void tabBackForegroundChanged() {
		tabBackForegroundChanged( tabbedPane1 );
		tabBackForegroundChanged( tabbedPane2 );
		tabBackForegroundChanged( tabbedPane3 );
		tabBackForegroundChanged( tabbedPane4 );
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
		leadingTrailingComponentChanged( leadingComponentCheckBox.isSelected(), TABBED_PANE_LEADING_COMPONENT, "L", 4 );
	}

	private void trailingComponentChanged() {
		leadingTrailingComponentChanged( trailingComponentCheckBox.isSelected(), TABBED_PANE_TRAILING_COMPONENT, "Trailing", 12 );
	}

	private void leadingTrailingComponentChanged( boolean enabled, String key, String text, int gap ) {
		JTabbedPane[] tabbedPanes = new JTabbedPane[] { tabbedPane1, tabbedPane2, tabbedPane3, tabbedPane4 };
		for( JTabbedPane tabbedPane : tabbedPanes ) {
			JComponent c = null;
			if( enabled ) {
				c = new JLabel( text );
				c.setOpaque( true );
				c.setBackground( key.equals( TABBED_PANE_LEADING_COMPONENT ) ? Color.cyan : Color.orange );
				c.setBorder( new EmptyBorder( gap, gap, gap, gap ) );
			}
			tabbedPane.putClientProperty( key, c );
		}
	}

	private void tabsClosableChanged() {
		boolean closable = tabsClosableCheckBox.isSelected();
		putTabbedPanesClientProperty( TABBED_PANE_TAB_CLOSABLE, closable ? true : null );

		if( closable ) {
			putTabbedPanesClientProperty( TABBED_PANE_TAB_CLOSE_CALLBACK,
				(BiConsumer<JTabbedPane, Integer>) (tabbedPane, tabIndex) -> {
					AWTEvent e = EventQueue.getCurrentEvent();
					int modifiers = (e instanceof MouseEvent) ? ((MouseEvent)e).getModifiers() : 0;
					JOptionPane.showMessageDialog( this, "Closed tab '" + tabbedPane.getTitleAt( tabIndex ) + "'."
						+ "\n\n(modifiers: " + MouseEvent.getMouseModifiersText( modifiers ) + ")",
						"Tab Closed", JOptionPane.PLAIN_MESSAGE );
				} );
		}
	}

	private void secondTabClosableChanged() {
		Boolean value = secondTabClosableCheckBox.getValue();

		JTabbedPane[] tabbedPanes = new JTabbedPane[] { tabbedPane1, tabbedPane2, tabbedPane3, tabbedPane4 };
		for( JTabbedPane tabbedPane : tabbedPanes ) {
			if( tabbedPane.getTabCount() > 1 ) {
				Component c = tabbedPane.getComponentAt( 1 );
				((JComponent)c).putClientProperty( TABBED_PANE_TAB_CLOSABLE, value );
			}
		}
	}

	private void tabAreaInsetsChanged() {
		Insets insets = tabAreaInsetsCheckBox.isSelected() ? new Insets( 5, 5, 10, 10 ) : null;
		putTabbedPanesClientProperty( TABBED_PANE_TAB_AREA_INSETS, insets );
	}

	private void smallerTabHeightChanged() {
		Integer tabHeight = smallerTabHeightCheckBox.isSelected() ? 26 : null;
		putTabbedPanesClientProperty( TABBED_PANE_TAB_HEIGHT, tabHeight );
	}

	private void smallerInsetsChanged() {
		Insets insets = smallerInsetsCheckBox.isSelected() ? new Insets( 2, 2, 2, 2 ) : null;
		putTabbedPanesClientProperty( TABBED_PANE_TAB_INSETS, insets );
	}

	private void secondTabWiderChanged() {
		Insets insets = secondTabWiderCheckBox.isSelected() ? new Insets( 4, 20, 4, 20 ) : null;

		JTabbedPane[] tabbedPanes = new JTabbedPane[] { tabbedPane1, tabbedPane2, tabbedPane3, tabbedPane4 };
		for( JTabbedPane tabbedPane : tabbedPanes ) {
			if( tabbedPane.getTabCount() > 1 ) {
				Component c = tabbedPane.getComponentAt( 1 );
				((JComponent)c).putClientProperty( TABBED_PANE_TAB_INSETS, insets );
			}
		}
	}

	private void minimumTabWidthChanged() {
		Integer minimumTabWidth = minimumTabWidthCheckBox.isSelected() ? 100 : null;
		putTabbedPanesClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH, minimumTabWidth );
	}

	private void maximumTabWidthChanged() {
		Integer maximumTabWidth = maximumTabWidthCheckBox.isSelected() ? 60 : null;
		putTabbedPanesClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH, maximumTabWidth );
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
		FlatTestFrame.NoRightToLeftPanel tabbedPaneControlPanel = new FlatTestFrame.NoRightToLeftPanel();
		tabScrollCheckBox = new JCheckBox();
		JLabel tabCountLabel = new JLabel();
		tabCountSpinner = new JSpinner();
		customTabsCheckBox = new JCheckBox();
		JLabel hiddenTabsNavigationLabel = new JLabel();
		hiddenTabsNavigationField = new JComboBox<>();
		tabBackForegroundCheckBox = new JCheckBox();
		JLabel tabPlacementLabel = new JLabel();
		tabPlacementField = new JComboBox<>();
		tabIconsCheckBox = new JCheckBox();
		tabIconSizeSpinner = new JSpinner();
		JLabel tabAreaAlignmentLabel = new JLabel();
		tabAreaAlignmentField = new JComboBox<>();
		JLabel tabWidthModeLabel = new JLabel();
		tabWidthModeField = new JComboBox<>();
		tabsClosableCheckBox = new JCheckBox();
		customBorderCheckBox = new JCheckBox();
		tabAreaInsetsCheckBox = new JCheckBox();
		secondTabClosableCheckBox = new TriStateCheckBox();
		hasFullBorderCheckBox = new JCheckBox();
		smallerTabHeightCheckBox = new JCheckBox();
		leadingComponentCheckBox = new JCheckBox();
		hideContentSeparatorCheckBox = new JCheckBox();
		smallerInsetsCheckBox = new JCheckBox();
		trailingComponentCheckBox = new JCheckBox();
		showTabSeparatorsCheckBox = new JCheckBox();
		secondTabWiderCheckBox = new JCheckBox();
		minimumTabWidthCheckBox = new JCheckBox();
		maximumTabWidthCheckBox = new JCheckBox();
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
				"default, $lgap, fill:70dlu, $pgap, pref, $lgap, fill:80dlu:grow, $ugap, fill:80dlu:grow, $pgap, pref"));

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

			//======== tabbedPaneControlPanel ========
			{
				tabbedPaneControlPanel.setOpaque(false);
				tabbedPaneControlPanel.setLayout(new MigLayout(
					"insets 0,hidemode 3",
					// columns
					"[]" +
					"[fill]" +
					"[]",
					// rows
					"[center]" +
					"[]" +
					"[]" +
					"[]para" +
					"[]" +
					"[]para" +
					"[]" +
					"[]para" +
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
				tabbedPaneControlPanel.add(tabCountSpinner, "cell 1 0");

				//---- customTabsCheckBox ----
				customTabsCheckBox.setText("Custom tabs");
				customTabsCheckBox.addActionListener(e -> customTabsChanged());
				tabbedPaneControlPanel.add(customTabsCheckBox, "cell 2 0");

				//---- hiddenTabsNavigationLabel ----
				hiddenTabsNavigationLabel.setText("Hidden tabs navigation:");
				tabbedPaneControlPanel.add(hiddenTabsNavigationLabel, "cell 0 1");

				//---- hiddenTabsNavigationField ----
				hiddenTabsNavigationField.setModel(new DefaultComboBoxModel<>(new String[] {
					"default",
					"moreTabsButton",
					"arrowButtons"
				}));
				hiddenTabsNavigationField.addActionListener(e -> hiddenTabsNavigationChanged());
				tabbedPaneControlPanel.add(hiddenTabsNavigationField, "cell 1 1");

				//---- tabBackForegroundCheckBox ----
				tabBackForegroundCheckBox.setText("Tab back/foreground");
				tabBackForegroundCheckBox.addActionListener(e -> tabBackForegroundChanged());
				tabbedPaneControlPanel.add(tabBackForegroundCheckBox, "cell 2 1");

				//---- tabPlacementLabel ----
				tabPlacementLabel.setText("Tab placement:");
				tabbedPaneControlPanel.add(tabPlacementLabel, "cell 0 2");

				//---- tabPlacementField ----
				tabPlacementField.setModel(new DefaultComboBoxModel<>(new String[] {
					"default",
					"top",
					"bottom",
					"left",
					"right"
				}));
				tabPlacementField.addActionListener(e -> tabPlacementChanged());
				tabbedPaneControlPanel.add(tabPlacementField, "cell 1 2");

				//---- tabIconsCheckBox ----
				tabIconsCheckBox.setText("Tab icons");
				tabIconsCheckBox.addActionListener(e -> tabIconsChanged());
				tabbedPaneControlPanel.add(tabIconsCheckBox, "cell 2 2");

				//---- tabIconSizeSpinner ----
				tabIconSizeSpinner.setModel(new SpinnerListModel(new String[] {"16", "24", "32", "48", "64"}));
				tabIconSizeSpinner.setEnabled(false);
				tabIconSizeSpinner.addChangeListener(e -> tabIconsChanged());
				tabbedPaneControlPanel.add(tabIconSizeSpinner, "cell 2 2");

				//---- tabAreaAlignmentLabel ----
				tabAreaAlignmentLabel.setText("Tab area alignment:");
				tabbedPaneControlPanel.add(tabAreaAlignmentLabel, "cell 0 3");

				//---- tabAreaAlignmentField ----
				tabAreaAlignmentField.setModel(new DefaultComboBoxModel<>(new String[] {
					"default",
					"leading",
					"trailing",
					"center",
					"fill"
				}));
				tabAreaAlignmentField.addActionListener(e -> tabAreaAlignmentChanged());
				tabbedPaneControlPanel.add(tabAreaAlignmentField, "cell 1 3");

				//---- tabWidthModeLabel ----
				tabWidthModeLabel.setText("Tab width mode:");
				tabbedPaneControlPanel.add(tabWidthModeLabel, "cell 2 3");

				//---- tabWidthModeField ----
				tabWidthModeField.setModel(new DefaultComboBoxModel<>(new String[] {
					"default",
					"preferred",
					"equal",
					"compact"
				}));
				tabWidthModeField.addActionListener(e -> tabWidthModeChanged());
				tabbedPaneControlPanel.add(tabWidthModeField, "cell 2 3");

				//---- tabsClosableCheckBox ----
				tabsClosableCheckBox.setText("Tabs closable");
				tabsClosableCheckBox.addActionListener(e -> tabsClosableChanged());
				tabbedPaneControlPanel.add(tabsClosableCheckBox, "cell 0 4");

				//---- customBorderCheckBox ----
				customBorderCheckBox.setText("Custom border");
				customBorderCheckBox.addActionListener(e -> customBorderChanged());
				tabbedPaneControlPanel.add(customBorderCheckBox, "cell 1 4");

				//---- tabAreaInsetsCheckBox ----
				tabAreaInsetsCheckBox.setText("Tab area insets (5,5,10,10)");
				tabAreaInsetsCheckBox.addActionListener(e -> tabAreaInsetsChanged());
				tabbedPaneControlPanel.add(tabAreaInsetsCheckBox, "cell 2 4");

				//---- secondTabClosableCheckBox ----
				secondTabClosableCheckBox.setText("Second Tab closable");
				secondTabClosableCheckBox.addActionListener(e -> secondTabClosableChanged());
				tabbedPaneControlPanel.add(secondTabClosableCheckBox, "cell 0 5");

				//---- hasFullBorderCheckBox ----
				hasFullBorderCheckBox.setText("Show content border");
				hasFullBorderCheckBox.addActionListener(e -> hasFullBorderChanged());
				tabbedPaneControlPanel.add(hasFullBorderCheckBox, "cell 1 5,alignx left,growx 0");

				//---- smallerTabHeightCheckBox ----
				smallerTabHeightCheckBox.setText("Smaller tab height (26)");
				smallerTabHeightCheckBox.addActionListener(e -> smallerTabHeightChanged());
				tabbedPaneControlPanel.add(smallerTabHeightCheckBox, "cell 2 5");

				//---- leadingComponentCheckBox ----
				leadingComponentCheckBox.setText("Leading component");
				leadingComponentCheckBox.addActionListener(e -> leadingComponentChanged());
				tabbedPaneControlPanel.add(leadingComponentCheckBox, "cell 0 6");

				//---- hideContentSeparatorCheckBox ----
				hideContentSeparatorCheckBox.setText("Hide content separator");
				hideContentSeparatorCheckBox.addActionListener(e -> hideContentSeparatorChanged());
				tabbedPaneControlPanel.add(hideContentSeparatorCheckBox, "cell 1 6");

				//---- smallerInsetsCheckBox ----
				smallerInsetsCheckBox.setText("Smaller tab insets (2,2,2,2)");
				smallerInsetsCheckBox.addActionListener(e -> smallerInsetsChanged());
				tabbedPaneControlPanel.add(smallerInsetsCheckBox, "cell 2 6");

				//---- trailingComponentCheckBox ----
				trailingComponentCheckBox.setText("Trailing component");
				trailingComponentCheckBox.addActionListener(e -> trailingComponentChanged());
				tabbedPaneControlPanel.add(trailingComponentCheckBox, "cell 0 7");

				//---- showTabSeparatorsCheckBox ----
				showTabSeparatorsCheckBox.setText("Show tab separators");
				showTabSeparatorsCheckBox.addActionListener(e -> showTabSeparatorsChanged());
				tabbedPaneControlPanel.add(showTabSeparatorsCheckBox, "cell 1 7");

				//---- secondTabWiderCheckBox ----
				secondTabWiderCheckBox.setText("Second Tab insets wider (4,20,4,20)");
				secondTabWiderCheckBox.addActionListener(e -> secondTabWiderChanged());
				tabbedPaneControlPanel.add(secondTabWiderCheckBox, "cell 2 7");

				//---- minimumTabWidthCheckBox ----
				minimumTabWidthCheckBox.setText("Minimum tab width (100)");
				minimumTabWidthCheckBox.addActionListener(e -> minimumTabWidthChanged());
				tabbedPaneControlPanel.add(minimumTabWidthCheckBox, "cell 2 8");

				//---- maximumTabWidthCheckBox ----
				maximumTabWidthCheckBox.setText("Maximum tab width (60)");
				maximumTabWidthCheckBox.addActionListener(e -> maximumTabWidthChanged());
				tabbedPaneControlPanel.add(maximumTabWidthCheckBox, "cell 2 9");
			}
			panel9.add(tabbedPaneControlPanel, cc.xywh(1, 11, 3, 1));
		}
		add(panel9, "cell 0 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTabbedPane tabbedPane1;
	private JTabbedPane tabbedPane3;
	private JTabbedPane tabbedPane2;
	private JTabbedPane tabbedPane4;
	private JCheckBox tabScrollCheckBox;
	private JSpinner tabCountSpinner;
	private JCheckBox customTabsCheckBox;
	private JComboBox<String> hiddenTabsNavigationField;
	private JCheckBox tabBackForegroundCheckBox;
	private JComboBox<String> tabPlacementField;
	private JCheckBox tabIconsCheckBox;
	private JSpinner tabIconSizeSpinner;
	private JComboBox<String> tabAreaAlignmentField;
	private JComboBox<String> tabWidthModeField;
	private JCheckBox tabsClosableCheckBox;
	private JCheckBox customBorderCheckBox;
	private JCheckBox tabAreaInsetsCheckBox;
	private TriStateCheckBox secondTabClosableCheckBox;
	private JCheckBox hasFullBorderCheckBox;
	private JCheckBox smallerTabHeightCheckBox;
	private JCheckBox leadingComponentCheckBox;
	private JCheckBox hideContentSeparatorCheckBox;
	private JCheckBox smallerInsetsCheckBox;
	private JCheckBox trailingComponentCheckBox;
	private JCheckBox showTabSeparatorsCheckBox;
	private JCheckBox secondTabWiderCheckBox;
	private JCheckBox minimumTabWidthCheckBox;
	private JCheckBox maximumTabWidthCheckBox;
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
