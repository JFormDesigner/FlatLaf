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

import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTabbedPaneCloseIcon;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class TabsPanel
	extends JPanel
{
	TabsPanel() {
		initComponents();

		initTabPlacementTabs( tabPlacementTabbedPane );

		initScrollLayoutTabs( scrollLayoutTabbedPane );
		initWrapLayoutTabs( wrapLayoutTabbedPane );

		initClosableTabs( closableTabsTabbedPane );

		initCustomComponentsTabs( customComponentsTabbedPane );

		initMinimumTabWidth( minimumTabWidthTabbedPane );
		initMaximumTabWidth( maximumTabWidthTabbedPane );

		initTabIconPlacement( iconTopTabbedPane, SwingConstants.TOP );
		initTabIconPlacement( iconBottomTabbedPane, SwingConstants.BOTTOM );
		initTabIconPlacement( iconLeadingTabbedPane, SwingConstants.LEADING );
		initTabIconPlacement( iconTrailingTabbedPane, SwingConstants.TRAILING );

		initTabAreaAlignment( alignLeadingTabbedPane, TABBED_PANE_ALIGN_LEADING );
		initTabAreaAlignment( alignCenterTabbedPane, TABBED_PANE_ALIGN_CENTER );
		initTabAreaAlignment( alignTrailingTabbedPane, TABBED_PANE_ALIGN_TRAILING );
		initTabAreaAlignment( alignFillTabbedPane, TABBED_PANE_ALIGN_FILL );

		initTabAlignment( tabAlignLeadingTabbedPane, SwingConstants.LEADING );
		initTabAlignment( tabAlignCenterTabbedPane, SwingConstants.CENTER );
		initTabAlignment( tabAlignTrailingTabbedPane, SwingConstants.TRAILING );
		initTabAlignment( tabAlignVerticalTabbedPane, SwingConstants.TRAILING );

		initTabWidthMode( widthPreferredTabbedPane, TABBED_PANE_TAB_WIDTH_MODE_PREFERRED );
		initTabWidthMode( widthEqualTabbedPane, TABBED_PANE_TAB_WIDTH_MODE_EQUAL );
		initTabWidthMode( widthCompactTabbedPane, TABBED_PANE_TAB_WIDTH_MODE_COMPACT );
	}

	private void initTabPlacementTabs( JTabbedPane tabbedPane ) {
		addTab( tabbedPane, "Tab 1", "tab content 1" );

		JComponent tab2 = createTab( "tab content 2" );
		tab2.setBorder( new LineBorder( Color.magenta ) );
		tabbedPane.addTab( "Second Tab", tab2 );

		addTab( tabbedPane, "Disabled", "tab content 3" );
		tabbedPane.setEnabledAt( 2, false );
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

	private void tabPlacementChanged() {
		int tabPlacement = JTabbedPane.TOP;
		if( bottomPlacementButton.isSelected() )
			tabPlacement = JTabbedPane.BOTTOM;
		else if( leftPlacementButton.isSelected() )
			tabPlacement = JTabbedPane.LEFT;
		else if( rightPlacementButton.isSelected() )
			tabPlacement = JTabbedPane.RIGHT;

		tabPlacementTabbedPane.setTabPlacement( tabPlacement );
	}

	private void scrollChanged() {
		boolean scroll = scrollButton.isSelected();
		tabPlacementTabbedPane.setTabLayoutPolicy( scroll ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT );

		int extraTabCount = 7;
		if( scroll ) {
			int tabCount = tabPlacementTabbedPane.getTabCount();
			for( int i = tabCount + 1; i <= tabCount + extraTabCount; i++ )
				addTab( tabPlacementTabbedPane, "Tab " + i, "tab content " + i );
		} else {
			for( int i = 0; i < extraTabCount; i++ )
				tabPlacementTabbedPane.removeTabAt( tabPlacementTabbedPane.getTabCount() - 1 );
		}
	}

	private void borderChanged() {
		Boolean hasFullBorder = borderButton.isSelected() ? true : null;
		tabPlacementTabbedPane.putClientProperty( TABBED_PANE_HAS_FULL_BORDER, hasFullBorder );
	}

	private void initScrollLayoutTabs( JTabbedPane tabbedPane ) {
		tabbedPane.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );
		addDefaultTabsNoContent( tabbedPane, 9 );
	}

	private void initWrapLayoutTabs( JTabbedPane tabbedPane ) {
		tabbedPane.setTabLayoutPolicy( JTabbedPane.WRAP_TAB_LAYOUT );
		addDefaultTabsNoContent( tabbedPane, 9 );

		wrapLayoutTabbedPane.setVisible( false );
		wrapLayoutNoteLabel.setVisible( false );
	}

	private void tabLayoutChanged() {
		boolean scroll = scrollTabLayoutButton.isSelected();

		scrollLayoutTabbedPane.setVisible( scroll );
		scrollLayoutNoteLabel.setVisible( scroll );
		wrapLayoutTabbedPane.setVisible( !scroll );
		wrapLayoutNoteLabel.setVisible( !scroll );
	}

	private void initClosableTabs( JTabbedPane tabbedPane ) {
		tabbedPane.putClientProperty( TABBED_PANE_TAB_CLOSABLE, true );
		tabbedPane.putClientProperty( TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close" );
		tabbedPane.putClientProperty( TABBED_PANE_TAB_CLOSE_CALLBACK,
			(BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
				AWTEvent e = EventQueue.getCurrentEvent();
				int modifiers = (e instanceof MouseEvent) ? ((MouseEvent)e).getModifiers() : 0;
				JOptionPane.showMessageDialog( this, "Closed tab '" + tabPane.getTitleAt( tabIndex ) + "'."
					+ "\n\n(modifiers: " + MouseEvent.getMouseModifiersText( modifiers ) + ")",
					"Tab Closed", JOptionPane.PLAIN_MESSAGE );
			} );

		addDefaultTabsNoContent( tabbedPane, 3 );
	}

	private void initCustomComponentsTabs( JTabbedPane tabbedPane ) {
		addDefaultTabsNoContent( tabbedPane, 2 );
		customComponentsChanged();
	}

	private void customComponentsChanged() {
		JToolBar leading = null;
		JToolBar trailing = null;
		if( leadingComponentButton.isSelected() ) {
			leading = new JToolBar();
			leading.setFloatable( false );
			leading.setBorder( null );
			leading.add( new JButton( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/project.svg" ) ) );
		}
		if( trailingComponentButton.isSelected() ) {
			trailing = new JToolBar();
			trailing.setFloatable( false );
			trailing.setBorder( null );
			trailing.add( new JButton( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/buildLoadChanges.svg" ) ) );
			trailing.add( Box.createHorizontalGlue() );
			trailing.add( new JButton( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/commit.svg" ) ) );
			trailing.add( new JButton( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/diff.svg" ) ) );
			trailing.add( new JButton( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/listFiles.svg" ) ) );
		}
		customComponentsTabbedPane.putClientProperty( TABBED_PANE_LEADING_COMPONENT, leading );
		customComponentsTabbedPane.putClientProperty( TABBED_PANE_TRAILING_COMPONENT, trailing );
	}

	private void addDefaultTabsNoContent( JTabbedPane tabbedPane, int count ) {
		tabbedPane.addTab( "Tab 1", null );
		tabbedPane.addTab( "Second Tab", null );
		if( count >= 3 )
			tabbedPane.addTab( "3rd Tab", null );

		for( int i = 4; i <= count; i++ )
			tabbedPane.addTab( "Tab " + i, null );
	}

	private void closeButtonStyleChanged() {
		// WARNING:
		//   Do not use this trick to style individual tabbed panes in own code.
		//   Instead use one styling for all tabbed panes in your application.
		if( circleCloseButton.isSelected() ) {
			UIManager.put( "TabbedPane.closeArc", 999 );
			UIManager.put( "TabbedPane.closeCrossFilledSize", 5.5f );
			UIManager.put( "TabbedPane.closeIcon", new FlatTabbedPaneCloseIcon() );
			closableTabsTabbedPane.updateUI();
			UIManager.put( "TabbedPane.closeArc", null );
			UIManager.put( "TabbedPane.closeCrossFilledSize", null );
			UIManager.put( "TabbedPane.closeIcon", null );
		} else if( redCrossCloseButton.isSelected() ) {
			UIManager.put( "TabbedPane.closeHoverForeground", Color.red );
			UIManager.put( "TabbedPane.closePressedForeground", Color.red );
			UIManager.put( "TabbedPane.closeHoverBackground", new Color( 0, true ) );
			UIManager.put( "TabbedPane.closeIcon", new FlatTabbedPaneCloseIcon() );
			closableTabsTabbedPane.updateUI();
			UIManager.put( "TabbedPane.closeHoverForeground", null );
			UIManager.put( "TabbedPane.closePressedForeground", null );
			UIManager.put( "TabbedPane.closeHoverBackground", null );
			UIManager.put( "TabbedPane.closeIcon", null );
		} else
			closableTabsTabbedPane.updateUI();
	}

	private void initTabIconPlacement( JTabbedPane tabbedPane, int iconPlacement ) {
		boolean topOrBottom = (iconPlacement == SwingConstants.TOP || iconPlacement == SwingConstants.BOTTOM);
		int iconSize = topOrBottom ? 24 : 16;
		tabbedPane.putClientProperty( TABBED_PANE_TAB_ICON_PLACEMENT, iconPlacement );
		if( topOrBottom ) {
			tabbedPane.putClientProperty( TABBED_PANE_TAB_AREA_ALIGNMENT, TABBED_PANE_ALIGN_FILL );
			tabbedPane.putClientProperty( TABBED_PANE_TAB_WIDTH_MODE, TABBED_PANE_TAB_WIDTH_MODE_EQUAL );
		}
		tabbedPane.addTab( "Search", new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/search.svg", iconSize, iconSize ), null );
		tabbedPane.addTab( "Recents", new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/RecentlyUsed.svg", iconSize, iconSize ), null );
		if( topOrBottom )
			tabbedPane.addTab( "Favorites", new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/favorite.svg", iconSize, iconSize ), null );
	}

	private void initTabAreaAlignment( JTabbedPane tabbedPane, String tabAreaAlignment ) {
		tabbedPane.putClientProperty( TABBED_PANE_TAB_AREA_ALIGNMENT, tabAreaAlignment );
		tabbedPane.addTab( "Search", null );
		tabbedPane.addTab( "Recents", null );
	}

	private void initTabAlignment( JTabbedPane tabbedPane, int tabAlignment ) {
		boolean vertical = (tabbedPane.getTabPlacement() == JTabbedPane.LEFT || tabbedPane.getTabPlacement() == JTabbedPane.RIGHT);
		tabbedPane.putClientProperty( TABBED_PANE_TAB_ALIGNMENT, tabAlignment );
		if( !vertical )
			tabbedPane.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH, 80 );
		tabbedPane.addTab( "A", null );
		if( vertical ) {
			tabbedPane.addTab( "Search", null );
			tabbedPane.addTab( "Recents", null );
		}
	}

	private void initTabWidthMode( JTabbedPane tabbedPane, String tabWidthMode ) {
		tabbedPane.putClientProperty( TABBED_PANE_TAB_WIDTH_MODE, tabWidthMode );
		if( tabWidthMode.equals( TABBED_PANE_TAB_WIDTH_MODE_COMPACT ) ) {
			tabbedPane.addTab( "Search", new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/search.svg", 16, 16 ), null );
			tabbedPane.addTab( "Recents", new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/RecentlyUsed.svg", 16, 16 ), null );
			tabbedPane.addTab( "Favorites", new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/favorite.svg", 16, 16 ), null );
		} else {
			tabbedPane.addTab( "Short", null );
			tabbedPane.addTab( "Longer Title", null );
		}
	}

	private void initMinimumTabWidth( JTabbedPane tabbedPane ) {
		tabbedPane.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH, 80 );
		tabbedPane.addTab( "A", null );
		tabbedPane.addTab( "Very long title", null );
	}

	private void initMaximumTabWidth( JTabbedPane tabbedPane ) {
		tabbedPane.putClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH, 80 );
		tabbedPane.addTab( "Very long title", null );
		tabbedPane.addTab( "B", null );
		tabbedPane.addTab( "C", null );
	}

	private void tabsPopupPolicyChanged() {
		String tabsPopupPolicy = popupNeverButton.isSelected() ? TABBED_PANE_POLICY_NEVER : null;
		putTabbedPanesClientProperty( TABBED_PANE_TABS_POPUP_POLICY, tabsPopupPolicy );
	}

	private void scrollButtonsPolicyChanged() {
		String scrollButtonsPolicy = scrollAsNeededButton.isSelected()
			? TABBED_PANE_POLICY_AS_NEEDED
			: (scrollNeverButton.isSelected()
				? TABBED_PANE_POLICY_NEVER
				: null);
		putTabbedPanesClientProperty( TABBED_PANE_SCROLL_BUTTONS_POLICY, scrollButtonsPolicy );
	}

	private void scrollButtonsPlacementChanged() {
		String scrollButtonsPlacement = scrollTrailingButton.isSelected() ? TABBED_PANE_PLACEMENT_TRAILING : null;
		putTabbedPanesClientProperty( TABBED_PANE_SCROLL_BUTTONS_PLACEMENT, scrollButtonsPlacement );
	}

	private void showTabSeparatorsChanged() {
		Boolean showTabSeparators = showTabSeparatorsCheckBox.isSelected() ? true : null;
		putTabbedPanesClientProperty( TABBED_PANE_SHOW_TAB_SEPARATORS, showTabSeparators );
	}

	private void putTabbedPanesClientProperty( String key, Object value ) {
		updateTabbedPanesRecur( this, tabbedPane -> tabbedPane.putClientProperty( key, value ) );
	}

	private void updateTabbedPanesRecur( Container container, Consumer<JTabbedPane> action ) {
		for( Component c : container.getComponents() ) {
			if( c instanceof JTabbedPane ) {
				JTabbedPane tabPane = (JTabbedPane)c;
				action.accept( tabPane );
			}

			if( c instanceof Container )
				updateTabbedPanesRecur( (Container) c, action );
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel1 = new JPanel();
		JLabel tabPlacementLabel = new JLabel();
		tabPlacementToolBar = new JToolBar();
		topPlacementButton = new JToggleButton();
		bottomPlacementButton = new JToggleButton();
		leftPlacementButton = new JToggleButton();
		rightPlacementButton = new JToggleButton();
		scrollButton = new JToggleButton();
		borderButton = new JToggleButton();
		tabPlacementTabbedPane = new JTabbedPane();
		JLabel tabLayoutLabel = new JLabel();
		tabLayoutToolBar = new JToolBar();
		scrollTabLayoutButton = new JToggleButton();
		wrapTabLayoutButton = new JToggleButton();
		scrollLayoutNoteLabel = new JLabel();
		wrapLayoutNoteLabel = new JLabel();
		scrollLayoutTabbedPane = new JTabbedPane();
		wrapLayoutTabbedPane = new JTabbedPane();
		JLabel closableTabsLabel = new JLabel();
		closableTabsToolBar = new JToolBar();
		squareCloseButton = new JToggleButton();
		circleCloseButton = new JToggleButton();
		redCrossCloseButton = new JToggleButton();
		closableTabsTabbedPane = new JTabbedPane();
		JLabel tabAreaComponentsLabel = new JLabel();
		tabAreaComponentsToolBar = new JToolBar();
		leadingComponentButton = new JToggleButton();
		trailingComponentButton = new JToggleButton();
		customComponentsTabbedPane = new JTabbedPane();
		JPanel panel2 = new JPanel();
		JLabel tabIconPlacementLabel = new JLabel();
		JLabel tabIconPlacementNodeLabel = new JLabel();
		iconTopTabbedPane = new JTabbedPane();
		iconBottomTabbedPane = new JTabbedPane();
		iconLeadingTabbedPane = new JTabbedPane();
		iconTrailingTabbedPane = new JTabbedPane();
		JLabel tabAreaAlignmentLabel = new JLabel();
		JLabel tabAreaAlignmentNoteLabel = new JLabel();
		alignLeadingTabbedPane = new JTabbedPane();
		alignCenterTabbedPane = new JTabbedPane();
		alignTrailingTabbedPane = new JTabbedPane();
		alignFillTabbedPane = new JTabbedPane();
		JPanel panel3 = new JPanel();
		JLabel tabWidthModeLabel = new JLabel();
		JLabel tabWidthModeNoteLabel = new JLabel();
		widthPreferredTabbedPane = new JTabbedPane();
		widthEqualTabbedPane = new JTabbedPane();
		widthCompactTabbedPane = new JTabbedPane();
		JLabel minMaxTabWidthLabel = new JLabel();
		minimumTabWidthTabbedPane = new JTabbedPane();
		maximumTabWidthTabbedPane = new JTabbedPane();
		JLabel tabAlignmentLabel = new JLabel();
		panel5 = new JPanel();
		JLabel tabAlignmentNoteLabel = new JLabel();
		JLabel tabAlignmentNoteLabel2 = new JLabel();
		tabAlignLeadingTabbedPane = new JTabbedPane();
		tabAlignVerticalTabbedPane = new JTabbedPane();
		tabAlignCenterTabbedPane = new JTabbedPane();
		tabAlignTrailingTabbedPane = new JTabbedPane();
		separator2 = new JSeparator();
		JPanel panel4 = new JPanel();
		scrollButtonsPolicyLabel = new JLabel();
		scrollButtonsPolicyToolBar = new JToolBar();
		scrollAsNeededSingleButton = new JToggleButton();
		scrollAsNeededButton = new JToggleButton();
		scrollNeverButton = new JToggleButton();
		scrollButtonsPlacementLabel = new JLabel();
		scrollButtonsPlacementToolBar = new JToolBar();
		scrollBothButton = new JToggleButton();
		scrollTrailingButton = new JToggleButton();
		tabsPopupPolicyLabel = new JLabel();
		tabsPopupPolicyToolBar = new JToolBar();
		popupAsNeededButton = new JToggleButton();
		popupNeverButton = new JToggleButton();
		showTabSeparatorsCheckBox = new JCheckBox();

		//======== this ========
		setName("this");
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[grow,fill]para" +
			"[fill]para" +
			"[fill]",
			// rows
			"[grow,fill]para" +
			"[]" +
			"[]"));

		//======== panel1 ========
		{
			panel1.setName("panel1");
			panel1.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[grow,fill]",
				// rows
				"[]" +
				"[fill]para" +
				"[]0" +
				"[]" +
				"[]para" +
				"[]" +
				"[]para" +
				"[]" +
				"[]"));

			//---- tabPlacementLabel ----
			tabPlacementLabel.setText("Tab placement");
			tabPlacementLabel.setFont(tabPlacementLabel.getFont().deriveFont(tabPlacementLabel.getFont().getSize() + 4f));
			tabPlacementLabel.setName("tabPlacementLabel");
			panel1.add(tabPlacementLabel, "cell 0 0");

			//======== tabPlacementToolBar ========
			{
				tabPlacementToolBar.setFloatable(false);
				tabPlacementToolBar.setBorder(BorderFactory.createEmptyBorder());
				tabPlacementToolBar.setName("tabPlacementToolBar");

				//---- topPlacementButton ----
				topPlacementButton.setText("top");
				topPlacementButton.setSelected(true);
				topPlacementButton.setFont(topPlacementButton.getFont().deriveFont(topPlacementButton.getFont().getSize() - 2f));
				topPlacementButton.setName("topPlacementButton");
				topPlacementButton.addActionListener(e -> tabPlacementChanged());
				tabPlacementToolBar.add(topPlacementButton);

				//---- bottomPlacementButton ----
				bottomPlacementButton.setText("bottom");
				bottomPlacementButton.setFont(bottomPlacementButton.getFont().deriveFont(bottomPlacementButton.getFont().getSize() - 2f));
				bottomPlacementButton.setName("bottomPlacementButton");
				bottomPlacementButton.addActionListener(e -> tabPlacementChanged());
				tabPlacementToolBar.add(bottomPlacementButton);

				//---- leftPlacementButton ----
				leftPlacementButton.setText("left");
				leftPlacementButton.setFont(leftPlacementButton.getFont().deriveFont(leftPlacementButton.getFont().getSize() - 2f));
				leftPlacementButton.setName("leftPlacementButton");
				leftPlacementButton.addActionListener(e -> tabPlacementChanged());
				tabPlacementToolBar.add(leftPlacementButton);

				//---- rightPlacementButton ----
				rightPlacementButton.setText("right");
				rightPlacementButton.setFont(rightPlacementButton.getFont().deriveFont(rightPlacementButton.getFont().getSize() - 2f));
				rightPlacementButton.setName("rightPlacementButton");
				rightPlacementButton.addActionListener(e -> tabPlacementChanged());
				tabPlacementToolBar.add(rightPlacementButton);
				tabPlacementToolBar.addSeparator();

				//---- scrollButton ----
				scrollButton.setText("scroll");
				scrollButton.setFont(scrollButton.getFont().deriveFont(scrollButton.getFont().getSize() - 2f));
				scrollButton.setName("scrollButton");
				scrollButton.addActionListener(e -> scrollChanged());
				tabPlacementToolBar.add(scrollButton);

				//---- borderButton ----
				borderButton.setText("border");
				borderButton.setFont(borderButton.getFont().deriveFont(borderButton.getFont().getSize() - 2f));
				borderButton.setName("borderButton");
				borderButton.addActionListener(e -> borderChanged());
				tabPlacementToolBar.add(borderButton);
			}
			panel1.add(tabPlacementToolBar, "cell 0 0,alignx right,growx 0");

			//======== tabPlacementTabbedPane ========
			{
				tabPlacementTabbedPane.setName("tabPlacementTabbedPane");
			}
			panel1.add(tabPlacementTabbedPane, "cell 0 1,width 300:300,height 100:100");

			//---- tabLayoutLabel ----
			tabLayoutLabel.setText("Tab layout");
			tabLayoutLabel.setFont(tabLayoutLabel.getFont().deriveFont(tabLayoutLabel.getFont().getSize() + 4f));
			tabLayoutLabel.setName("tabLayoutLabel");
			panel1.add(tabLayoutLabel, "cell 0 2");

			//======== tabLayoutToolBar ========
			{
				tabLayoutToolBar.setFloatable(false);
				tabLayoutToolBar.setBorder(BorderFactory.createEmptyBorder());
				tabLayoutToolBar.setName("tabLayoutToolBar");

				//---- scrollTabLayoutButton ----
				scrollTabLayoutButton.setText("scroll");
				scrollTabLayoutButton.setFont(scrollTabLayoutButton.getFont().deriveFont(scrollTabLayoutButton.getFont().getSize() - 2f));
				scrollTabLayoutButton.setSelected(true);
				scrollTabLayoutButton.setName("scrollTabLayoutButton");
				scrollTabLayoutButton.addActionListener(e -> tabLayoutChanged());
				tabLayoutToolBar.add(scrollTabLayoutButton);

				//---- wrapTabLayoutButton ----
				wrapTabLayoutButton.setText("wrap");
				wrapTabLayoutButton.setFont(wrapTabLayoutButton.getFont().deriveFont(wrapTabLayoutButton.getFont().getSize() - 2f));
				wrapTabLayoutButton.setName("wrapTabLayoutButton");
				wrapTabLayoutButton.addActionListener(e -> tabLayoutChanged());
				tabLayoutToolBar.add(wrapTabLayoutButton);
			}
			panel1.add(tabLayoutToolBar, "cell 0 2,alignx right,growx 0");

			//---- scrollLayoutNoteLabel ----
			scrollLayoutNoteLabel.setText("(use mouse wheel to scroll; arrow button shows hidden tabs)");
			scrollLayoutNoteLabel.setEnabled(false);
			scrollLayoutNoteLabel.setFont(scrollLayoutNoteLabel.getFont().deriveFont(scrollLayoutNoteLabel.getFont().getSize() - 2f));
			scrollLayoutNoteLabel.setName("scrollLayoutNoteLabel");
			panel1.add(scrollLayoutNoteLabel, "cell 0 3");

			//---- wrapLayoutNoteLabel ----
			wrapLayoutNoteLabel.setText("(probably better to use scroll layout?)");
			wrapLayoutNoteLabel.setEnabled(false);
			wrapLayoutNoteLabel.setFont(wrapLayoutNoteLabel.getFont().deriveFont(wrapLayoutNoteLabel.getFont().getSize() - 2f));
			wrapLayoutNoteLabel.setName("wrapLayoutNoteLabel");
			panel1.add(wrapLayoutNoteLabel, "cell 0 3");

			//======== scrollLayoutTabbedPane ========
			{
				scrollLayoutTabbedPane.setName("scrollLayoutTabbedPane");
			}
			panel1.add(scrollLayoutTabbedPane, "cell 0 4");

			//======== wrapLayoutTabbedPane ========
			{
				wrapLayoutTabbedPane.setName("wrapLayoutTabbedPane");
			}
			panel1.add(wrapLayoutTabbedPane, "cell 0 4,width 100:100,height pref*2px");

			//---- closableTabsLabel ----
			closableTabsLabel.setText("Closable tabs");
			closableTabsLabel.setFont(closableTabsLabel.getFont().deriveFont(closableTabsLabel.getFont().getSize() + 4f));
			closableTabsLabel.setName("closableTabsLabel");
			panel1.add(closableTabsLabel, "cell 0 5");

			//======== closableTabsToolBar ========
			{
				closableTabsToolBar.setFloatable(false);
				closableTabsToolBar.setBorder(BorderFactory.createEmptyBorder());
				closableTabsToolBar.setName("closableTabsToolBar");

				//---- squareCloseButton ----
				squareCloseButton.setText("square");
				squareCloseButton.setFont(squareCloseButton.getFont().deriveFont(squareCloseButton.getFont().getSize() - 2f));
				squareCloseButton.setSelected(true);
				squareCloseButton.setName("squareCloseButton");
				squareCloseButton.addActionListener(e -> closeButtonStyleChanged());
				closableTabsToolBar.add(squareCloseButton);

				//---- circleCloseButton ----
				circleCloseButton.setText("circle");
				circleCloseButton.setFont(circleCloseButton.getFont().deriveFont(circleCloseButton.getFont().getSize() - 2f));
				circleCloseButton.setName("circleCloseButton");
				circleCloseButton.addActionListener(e -> closeButtonStyleChanged());
				closableTabsToolBar.add(circleCloseButton);

				//---- redCrossCloseButton ----
				redCrossCloseButton.setText("red cross");
				redCrossCloseButton.setFont(redCrossCloseButton.getFont().deriveFont(redCrossCloseButton.getFont().getSize() - 2f));
				redCrossCloseButton.setName("redCrossCloseButton");
				redCrossCloseButton.addActionListener(e -> closeButtonStyleChanged());
				closableTabsToolBar.add(redCrossCloseButton);
			}
			panel1.add(closableTabsToolBar, "cell 0 5,alignx right,growx 0");

			//======== closableTabsTabbedPane ========
			{
				closableTabsTabbedPane.setName("closableTabsTabbedPane");
			}
			panel1.add(closableTabsTabbedPane, "cell 0 6");

			//---- tabAreaComponentsLabel ----
			tabAreaComponentsLabel.setText("Custom tab area components");
			tabAreaComponentsLabel.setFont(tabAreaComponentsLabel.getFont().deriveFont(tabAreaComponentsLabel.getFont().getSize() + 4f));
			tabAreaComponentsLabel.setName("tabAreaComponentsLabel");
			panel1.add(tabAreaComponentsLabel, "cell 0 7");

			//======== tabAreaComponentsToolBar ========
			{
				tabAreaComponentsToolBar.setFloatable(false);
				tabAreaComponentsToolBar.setBorder(BorderFactory.createEmptyBorder());
				tabAreaComponentsToolBar.setName("tabAreaComponentsToolBar");

				//---- leadingComponentButton ----
				leadingComponentButton.setText("leading");
				leadingComponentButton.setFont(leadingComponentButton.getFont().deriveFont(leadingComponentButton.getFont().getSize() - 2f));
				leadingComponentButton.setSelected(true);
				leadingComponentButton.setName("leadingComponentButton");
				leadingComponentButton.addActionListener(e -> customComponentsChanged());
				tabAreaComponentsToolBar.add(leadingComponentButton);

				//---- trailingComponentButton ----
				trailingComponentButton.setText("trailing");
				trailingComponentButton.setFont(trailingComponentButton.getFont().deriveFont(trailingComponentButton.getFont().getSize() - 2f));
				trailingComponentButton.setSelected(true);
				trailingComponentButton.setName("trailingComponentButton");
				trailingComponentButton.addActionListener(e -> customComponentsChanged());
				tabAreaComponentsToolBar.add(trailingComponentButton);
			}
			panel1.add(tabAreaComponentsToolBar, "cell 0 7,alignx right,growx 0");

			//======== customComponentsTabbedPane ========
			{
				customComponentsTabbedPane.setName("customComponentsTabbedPane");
			}
			panel1.add(customComponentsTabbedPane, "cell 0 8");
		}
		add(panel1, "cell 0 0");

		//======== panel2 ========
		{
			panel2.setName("panel2");
			panel2.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[grow,fill]",
				// rows
				"[]0" +
				"[]" +
				"[fill]" +
				"[center]" +
				"[center]" +
				"[center]para" +
				"[center]0" +
				"[]" +
				"[center]" +
				"[center]" +
				"[center]" +
				"[]"));

			//---- tabIconPlacementLabel ----
			tabIconPlacementLabel.setText("Tab icon placement");
			tabIconPlacementLabel.setFont(tabIconPlacementLabel.getFont().deriveFont(tabIconPlacementLabel.getFont().getSize() + 4f));
			tabIconPlacementLabel.setName("tabIconPlacementLabel");
			panel2.add(tabIconPlacementLabel, "cell 0 0");

			//---- tabIconPlacementNodeLabel ----
			tabIconPlacementNodeLabel.setText("(top/bottom/leading/trailing)");
			tabIconPlacementNodeLabel.setEnabled(false);
			tabIconPlacementNodeLabel.setFont(tabIconPlacementNodeLabel.getFont().deriveFont(tabIconPlacementNodeLabel.getFont().getSize() - 2f));
			tabIconPlacementNodeLabel.setName("tabIconPlacementNodeLabel");
			panel2.add(tabIconPlacementNodeLabel, "cell 0 1");

			//======== iconTopTabbedPane ========
			{
				iconTopTabbedPane.setName("iconTopTabbedPane");
			}
			panel2.add(iconTopTabbedPane, "cell 0 2");

			//======== iconBottomTabbedPane ========
			{
				iconBottomTabbedPane.setName("iconBottomTabbedPane");
			}
			panel2.add(iconBottomTabbedPane, "cell 0 3");

			//======== iconLeadingTabbedPane ========
			{
				iconLeadingTabbedPane.setName("iconLeadingTabbedPane");
			}
			panel2.add(iconLeadingTabbedPane, "cell 0 4");

			//======== iconTrailingTabbedPane ========
			{
				iconTrailingTabbedPane.setName("iconTrailingTabbedPane");
			}
			panel2.add(iconTrailingTabbedPane, "cell 0 5");

			//---- tabAreaAlignmentLabel ----
			tabAreaAlignmentLabel.setText("Tab area alignment");
			tabAreaAlignmentLabel.setFont(tabAreaAlignmentLabel.getFont().deriveFont(tabAreaAlignmentLabel.getFont().getSize() + 4f));
			tabAreaAlignmentLabel.setName("tabAreaAlignmentLabel");
			panel2.add(tabAreaAlignmentLabel, "cell 0 6");

			//---- tabAreaAlignmentNoteLabel ----
			tabAreaAlignmentNoteLabel.setText("(leading/center/trailing/fill)");
			tabAreaAlignmentNoteLabel.setEnabled(false);
			tabAreaAlignmentNoteLabel.setFont(tabAreaAlignmentNoteLabel.getFont().deriveFont(tabAreaAlignmentNoteLabel.getFont().getSize() - 2f));
			tabAreaAlignmentNoteLabel.setName("tabAreaAlignmentNoteLabel");
			panel2.add(tabAreaAlignmentNoteLabel, "cell 0 7");

			//======== alignLeadingTabbedPane ========
			{
				alignLeadingTabbedPane.setName("alignLeadingTabbedPane");
			}
			panel2.add(alignLeadingTabbedPane, "cell 0 8");

			//======== alignCenterTabbedPane ========
			{
				alignCenterTabbedPane.setName("alignCenterTabbedPane");
			}
			panel2.add(alignCenterTabbedPane, "cell 0 9");

			//======== alignTrailingTabbedPane ========
			{
				alignTrailingTabbedPane.setName("alignTrailingTabbedPane");
			}
			panel2.add(alignTrailingTabbedPane, "cell 0 10");

			//======== alignFillTabbedPane ========
			{
				alignFillTabbedPane.setName("alignFillTabbedPane");
			}
			panel2.add(alignFillTabbedPane, "cell 0 11");
		}
		add(panel2, "cell 1 0,growy");

		//======== panel3 ========
		{
			panel3.setName("panel3");
			panel3.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[grow,fill]",
				// rows
				"[]0" +
				"[]" +
				"[]" +
				"[]" +
				"[]para" +
				"[]" +
				"[]" +
				"[]para" +
				"[]0" +
				"[]"));

			//---- tabWidthModeLabel ----
			tabWidthModeLabel.setText("Tab width mode");
			tabWidthModeLabel.setFont(tabWidthModeLabel.getFont().deriveFont(tabWidthModeLabel.getFont().getSize() + 4f));
			tabWidthModeLabel.setName("tabWidthModeLabel");
			panel3.add(tabWidthModeLabel, "cell 0 0");

			//---- tabWidthModeNoteLabel ----
			tabWidthModeNoteLabel.setText("(preferred/equal/compact)");
			tabWidthModeNoteLabel.setFont(tabWidthModeNoteLabel.getFont().deriveFont(tabWidthModeNoteLabel.getFont().getSize() - 2f));
			tabWidthModeNoteLabel.setEnabled(false);
			tabWidthModeNoteLabel.setName("tabWidthModeNoteLabel");
			panel3.add(tabWidthModeNoteLabel, "cell 0 1");

			//======== widthPreferredTabbedPane ========
			{
				widthPreferredTabbedPane.setName("widthPreferredTabbedPane");
			}
			panel3.add(widthPreferredTabbedPane, "cell 0 2");

			//======== widthEqualTabbedPane ========
			{
				widthEqualTabbedPane.setName("widthEqualTabbedPane");
			}
			panel3.add(widthEqualTabbedPane, "cell 0 3");

			//======== widthCompactTabbedPane ========
			{
				widthCompactTabbedPane.setName("widthCompactTabbedPane");
			}
			panel3.add(widthCompactTabbedPane, "cell 0 4");

			//---- minMaxTabWidthLabel ----
			minMaxTabWidthLabel.setText("Minimum/maximum tab width");
			minMaxTabWidthLabel.setFont(minMaxTabWidthLabel.getFont().deriveFont(minMaxTabWidthLabel.getFont().getSize() + 4f));
			minMaxTabWidthLabel.setName("minMaxTabWidthLabel");
			panel3.add(minMaxTabWidthLabel, "cell 0 5");

			//======== minimumTabWidthTabbedPane ========
			{
				minimumTabWidthTabbedPane.setName("minimumTabWidthTabbedPane");
			}
			panel3.add(minimumTabWidthTabbedPane, "cell 0 6");

			//======== maximumTabWidthTabbedPane ========
			{
				maximumTabWidthTabbedPane.setName("maximumTabWidthTabbedPane");
			}
			panel3.add(maximumTabWidthTabbedPane, "cell 0 7");

			//---- tabAlignmentLabel ----
			tabAlignmentLabel.setText("Tab title alignment");
			tabAlignmentLabel.setFont(tabAlignmentLabel.getFont().deriveFont(tabAlignmentLabel.getFont().getSize() + 4f));
			tabAlignmentLabel.setName("tabAlignmentLabel");
			panel3.add(tabAlignmentLabel, "cell 0 8");

			//======== panel5 ========
			{
				panel5.setName("panel5");
				panel5.setLayout(new MigLayout(
					"insets 0,hidemode 3",
					// columns
					"[grow,fill]para" +
					"[fill]",
					// rows
					"[]" +
					"[]" +
					"[]" +
					"[]"));

				//---- tabAlignmentNoteLabel ----
				tabAlignmentNoteLabel.setText("(leading/center/trailing)");
				tabAlignmentNoteLabel.setEnabled(false);
				tabAlignmentNoteLabel.setFont(tabAlignmentNoteLabel.getFont().deriveFont(tabAlignmentNoteLabel.getFont().getSize() - 2f));
				tabAlignmentNoteLabel.setName("tabAlignmentNoteLabel");
				panel5.add(tabAlignmentNoteLabel, "cell 0 0");

				//---- tabAlignmentNoteLabel2 ----
				tabAlignmentNoteLabel2.setText("(trailing)");
				tabAlignmentNoteLabel2.setEnabled(false);
				tabAlignmentNoteLabel2.setFont(tabAlignmentNoteLabel2.getFont().deriveFont(tabAlignmentNoteLabel2.getFont().getSize() - 2f));
				tabAlignmentNoteLabel2.setName("tabAlignmentNoteLabel2");
				panel5.add(tabAlignmentNoteLabel2, "cell 1 0,alignx right,growx 0");

				//======== tabAlignLeadingTabbedPane ========
				{
					tabAlignLeadingTabbedPane.setName("tabAlignLeadingTabbedPane");
				}
				panel5.add(tabAlignLeadingTabbedPane, "cell 0 1");

				//======== tabAlignVerticalTabbedPane ========
				{
					tabAlignVerticalTabbedPane.setTabPlacement(SwingConstants.LEFT);
					tabAlignVerticalTabbedPane.setName("tabAlignVerticalTabbedPane");
				}
				panel5.add(tabAlignVerticalTabbedPane, "cell 1 1 1 3,growy");

				//======== tabAlignCenterTabbedPane ========
				{
					tabAlignCenterTabbedPane.setName("tabAlignCenterTabbedPane");
				}
				panel5.add(tabAlignCenterTabbedPane, "cell 0 2");

				//======== tabAlignTrailingTabbedPane ========
				{
					tabAlignTrailingTabbedPane.setName("tabAlignTrailingTabbedPane");
				}
				panel5.add(tabAlignTrailingTabbedPane, "cell 0 3");
			}
			panel3.add(panel5, "cell 0 9");
		}
		add(panel3, "cell 2 0");

		//---- separator2 ----
		separator2.setName("separator2");
		add(separator2, "cell 0 1 3 1");

		//======== panel4 ========
		{
			panel4.setName("panel4");
			panel4.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[]" +
				"[fill]para" +
				"[fill]" +
				"[fill]para",
				// rows
				"[]" +
				"[center]"));

			//---- scrollButtonsPolicyLabel ----
			scrollButtonsPolicyLabel.setText("Scroll buttons policy:");
			scrollButtonsPolicyLabel.setName("scrollButtonsPolicyLabel");
			panel4.add(scrollButtonsPolicyLabel, "cell 0 0");

			//======== scrollButtonsPolicyToolBar ========
			{
				scrollButtonsPolicyToolBar.setFloatable(false);
				scrollButtonsPolicyToolBar.setBorder(BorderFactory.createEmptyBorder());
				scrollButtonsPolicyToolBar.setName("scrollButtonsPolicyToolBar");

				//---- scrollAsNeededSingleButton ----
				scrollAsNeededSingleButton.setText("asNeededSingle");
				scrollAsNeededSingleButton.setFont(scrollAsNeededSingleButton.getFont().deriveFont(scrollAsNeededSingleButton.getFont().getSize() - 2f));
				scrollAsNeededSingleButton.setSelected(true);
				scrollAsNeededSingleButton.setName("scrollAsNeededSingleButton");
				scrollAsNeededSingleButton.addActionListener(e -> scrollButtonsPolicyChanged());
				scrollButtonsPolicyToolBar.add(scrollAsNeededSingleButton);

				//---- scrollAsNeededButton ----
				scrollAsNeededButton.setText("asNeeded");
				scrollAsNeededButton.setFont(scrollAsNeededButton.getFont().deriveFont(scrollAsNeededButton.getFont().getSize() - 2f));
				scrollAsNeededButton.setName("scrollAsNeededButton");
				scrollAsNeededButton.addActionListener(e -> scrollButtonsPolicyChanged());
				scrollButtonsPolicyToolBar.add(scrollAsNeededButton);

				//---- scrollNeverButton ----
				scrollNeverButton.setText("never");
				scrollNeverButton.setFont(scrollNeverButton.getFont().deriveFont(scrollNeverButton.getFont().getSize() - 2f));
				scrollNeverButton.setName("scrollNeverButton");
				scrollNeverButton.addActionListener(e -> scrollButtonsPolicyChanged());
				scrollButtonsPolicyToolBar.add(scrollNeverButton);
			}
			panel4.add(scrollButtonsPolicyToolBar, "cell 1 0");

			//---- scrollButtonsPlacementLabel ----
			scrollButtonsPlacementLabel.setText("Scroll buttons placement:");
			scrollButtonsPlacementLabel.setName("scrollButtonsPlacementLabel");
			panel4.add(scrollButtonsPlacementLabel, "cell 2 0");

			//======== scrollButtonsPlacementToolBar ========
			{
				scrollButtonsPlacementToolBar.setFloatable(false);
				scrollButtonsPlacementToolBar.setBorder(BorderFactory.createEmptyBorder());
				scrollButtonsPlacementToolBar.setName("scrollButtonsPlacementToolBar");

				//---- scrollBothButton ----
				scrollBothButton.setText("both");
				scrollBothButton.setFont(scrollBothButton.getFont().deriveFont(scrollBothButton.getFont().getSize() - 2f));
				scrollBothButton.setSelected(true);
				scrollBothButton.setName("scrollBothButton");
				scrollBothButton.addActionListener(e -> scrollButtonsPlacementChanged());
				scrollButtonsPlacementToolBar.add(scrollBothButton);

				//---- scrollTrailingButton ----
				scrollTrailingButton.setText("trailing");
				scrollTrailingButton.setFont(scrollTrailingButton.getFont().deriveFont(scrollTrailingButton.getFont().getSize() - 2f));
				scrollTrailingButton.setName("scrollTrailingButton");
				scrollTrailingButton.addActionListener(e -> scrollButtonsPlacementChanged());
				scrollButtonsPlacementToolBar.add(scrollTrailingButton);
			}
			panel4.add(scrollButtonsPlacementToolBar, "cell 3 0");

			//---- tabsPopupPolicyLabel ----
			tabsPopupPolicyLabel.setText("Tabs popup policy:");
			tabsPopupPolicyLabel.setName("tabsPopupPolicyLabel");
			panel4.add(tabsPopupPolicyLabel, "cell 0 1");

			//======== tabsPopupPolicyToolBar ========
			{
				tabsPopupPolicyToolBar.setFloatable(false);
				tabsPopupPolicyToolBar.setBorder(BorderFactory.createEmptyBorder());
				tabsPopupPolicyToolBar.setName("tabsPopupPolicyToolBar");

				//---- popupAsNeededButton ----
				popupAsNeededButton.setText("asNeeded");
				popupAsNeededButton.setFont(popupAsNeededButton.getFont().deriveFont(popupAsNeededButton.getFont().getSize() - 2f));
				popupAsNeededButton.setSelected(true);
				popupAsNeededButton.setName("popupAsNeededButton");
				popupAsNeededButton.addActionListener(e -> tabsPopupPolicyChanged());
				tabsPopupPolicyToolBar.add(popupAsNeededButton);

				//---- popupNeverButton ----
				popupNeverButton.setText("never");
				popupNeverButton.setFont(popupNeverButton.getFont().deriveFont(popupNeverButton.getFont().getSize() - 2f));
				popupNeverButton.setName("popupNeverButton");
				popupNeverButton.addActionListener(e -> tabsPopupPolicyChanged());
				tabsPopupPolicyToolBar.add(popupNeverButton);
			}
			panel4.add(tabsPopupPolicyToolBar, "cell 1 1");

			//---- showTabSeparatorsCheckBox ----
			showTabSeparatorsCheckBox.setText("Show tab separators");
			showTabSeparatorsCheckBox.setName("showTabSeparatorsCheckBox");
			showTabSeparatorsCheckBox.addActionListener(e -> showTabSeparatorsChanged());
			panel4.add(showTabSeparatorsCheckBox, "cell 2 1 2 1");
		}
		add(panel4, "cell 0 2 3 1");

		//---- tabPlacementButtonGroup ----
		ButtonGroup tabPlacementButtonGroup = new ButtonGroup();
		tabPlacementButtonGroup.add(topPlacementButton);
		tabPlacementButtonGroup.add(bottomPlacementButton);
		tabPlacementButtonGroup.add(leftPlacementButton);
		tabPlacementButtonGroup.add(rightPlacementButton);

		//---- tabLayoutButtonGroup ----
		ButtonGroup tabLayoutButtonGroup = new ButtonGroup();
		tabLayoutButtonGroup.add(scrollTabLayoutButton);
		tabLayoutButtonGroup.add(wrapTabLayoutButton);

		//---- closableTabsButtonGroup ----
		ButtonGroup closableTabsButtonGroup = new ButtonGroup();
		closableTabsButtonGroup.add(squareCloseButton);
		closableTabsButtonGroup.add(circleCloseButton);
		closableTabsButtonGroup.add(redCrossCloseButton);

		//---- scrollButtonsPolicyButtonGroup ----
		ButtonGroup scrollButtonsPolicyButtonGroup = new ButtonGroup();
		scrollButtonsPolicyButtonGroup.add(scrollAsNeededSingleButton);
		scrollButtonsPolicyButtonGroup.add(scrollAsNeededButton);
		scrollButtonsPolicyButtonGroup.add(scrollNeverButton);

		//---- scrollButtonsPlacementButtonGroup ----
		ButtonGroup scrollButtonsPlacementButtonGroup = new ButtonGroup();
		scrollButtonsPlacementButtonGroup.add(scrollBothButton);
		scrollButtonsPlacementButtonGroup.add(scrollTrailingButton);

		//---- tabsPopupPolicyButtonGroup ----
		ButtonGroup tabsPopupPolicyButtonGroup = new ButtonGroup();
		tabsPopupPolicyButtonGroup.add(popupAsNeededButton);
		tabsPopupPolicyButtonGroup.add(popupNeverButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		if( FlatLafDemo.screenshotsMode ) {
			Component[] components = new Component[] {
				tabPlacementLabel, tabPlacementToolBar, tabPlacementTabbedPane,
				iconBottomTabbedPane, iconTrailingTabbedPane,
				alignLeadingTabbedPane, alignTrailingTabbedPane, alignFillTabbedPane,
				panel3, separator2, panel4,
			};

			for( Component c : components )
				c.setVisible( false );

			// remove gaps
			MigLayout layout1 = (MigLayout) panel1.getLayout();
			AC rowSpecs1 = ConstraintParser.parseRowConstraints( (String) layout1.getRowConstraints() );
			rowSpecs1.gap( "0!", 0, 1 );
			layout1.setRowConstraints( rowSpecs1 );

			MigLayout layout2 = (MigLayout) panel2.getLayout();
			AC rowSpecs2 = ConstraintParser.parseRowConstraints( (String) layout2.getRowConstraints() );
			rowSpecs2.gap( "0!", 2, 4, 8 );
			layout2.setRowConstraints( rowSpecs2 );
		}
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JToolBar tabPlacementToolBar;
	private JToggleButton topPlacementButton;
	private JToggleButton bottomPlacementButton;
	private JToggleButton leftPlacementButton;
	private JToggleButton rightPlacementButton;
	private JToggleButton scrollButton;
	private JToggleButton borderButton;
	private JTabbedPane tabPlacementTabbedPane;
	private JToolBar tabLayoutToolBar;
	private JToggleButton scrollTabLayoutButton;
	private JToggleButton wrapTabLayoutButton;
	private JLabel scrollLayoutNoteLabel;
	private JLabel wrapLayoutNoteLabel;
	private JTabbedPane scrollLayoutTabbedPane;
	private JTabbedPane wrapLayoutTabbedPane;
	private JToolBar closableTabsToolBar;
	private JToggleButton squareCloseButton;
	private JToggleButton circleCloseButton;
	private JToggleButton redCrossCloseButton;
	private JTabbedPane closableTabsTabbedPane;
	private JToolBar tabAreaComponentsToolBar;
	private JToggleButton leadingComponentButton;
	private JToggleButton trailingComponentButton;
	private JTabbedPane customComponentsTabbedPane;
	private JTabbedPane iconTopTabbedPane;
	private JTabbedPane iconBottomTabbedPane;
	private JTabbedPane iconLeadingTabbedPane;
	private JTabbedPane iconTrailingTabbedPane;
	private JTabbedPane alignLeadingTabbedPane;
	private JTabbedPane alignCenterTabbedPane;
	private JTabbedPane alignTrailingTabbedPane;
	private JTabbedPane alignFillTabbedPane;
	private JTabbedPane widthPreferredTabbedPane;
	private JTabbedPane widthEqualTabbedPane;
	private JTabbedPane widthCompactTabbedPane;
	private JTabbedPane minimumTabWidthTabbedPane;
	private JTabbedPane maximumTabWidthTabbedPane;
	private JPanel panel5;
	private JTabbedPane tabAlignLeadingTabbedPane;
	private JTabbedPane tabAlignVerticalTabbedPane;
	private JTabbedPane tabAlignCenterTabbedPane;
	private JTabbedPane tabAlignTrailingTabbedPane;
	private JSeparator separator2;
	private JLabel scrollButtonsPolicyLabel;
	private JToolBar scrollButtonsPolicyToolBar;
	private JToggleButton scrollAsNeededSingleButton;
	private JToggleButton scrollAsNeededButton;
	private JToggleButton scrollNeverButton;
	private JLabel scrollButtonsPlacementLabel;
	private JToolBar scrollButtonsPlacementToolBar;
	private JToggleButton scrollBothButton;
	private JToggleButton scrollTrailingButton;
	private JLabel tabsPopupPolicyLabel;
	private JToolBar tabsPopupPolicyToolBar;
	private JToggleButton popupAsNeededButton;
	private JToggleButton popupNeverButton;
	private JCheckBox showTabSeparatorsCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
