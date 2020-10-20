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
	private final int initialTabCount;
	private boolean autoMoreTabs;

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatContainerTest" );
			frame.useApplyComponentOrientation = true;
			frame.showFrame( FlatContainerTest::new );
		} );
	}

	public FlatContainerTest() {
		initComponents();

		addInitialTabs( tabbedPane1, tabbedPane2, tabbedPane3, tabbedPane4 );
		initialTabCount = tabbedPane1.getTabCount();

		tabsClosableCheckBox.setSelected( true );
		tabsClosableChanged();

		tabScrollCheckBox.setSelected( true );
		tabScrollChanged();
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
		moreTabsSpinnerChanged();

		autoMoreTabs = false;

		moreTabsSpinner.setEnabled( moreTabsCheckBox.isSelected() );
	}

	private void moreTabsSpinnerChanged() {
		addRemoveMoreTabs( tabbedPane1 );
		addRemoveMoreTabs( tabbedPane2 );
		addRemoveMoreTabs( tabbedPane3 );
		addRemoveMoreTabs( tabbedPane4 );
	}

	private void addRemoveMoreTabs( JTabbedPane tabbedPane ) {
		int oldTabCount = tabbedPane.getTabCount();
		int newTabCount = initialTabCount + (moreTabsCheckBox.isSelected() ? (Integer) moreTabsSpinner.getValue() : 0);

		if( newTabCount > oldTabCount ) {
			for( int i = oldTabCount + 1; i <= newTabCount; i++ )
				addTab( tabbedPane, "Tab " + i, "tab content " + i );
		} else if( newTabCount < oldTabCount ) {
			while( tabbedPane.getTabCount() > newTabCount )
				tabbedPane.removeTabAt( tabbedPane.getTabCount() - 1 );
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
		if( customTabsCheckBox.isSelected() ) {
			tabbedPane.setTabComponentAt( 1, new JButton( tabbedPane1.getTitleAt( 1 ) ) );
			tabbedPane.setTabComponentAt( 3, createCustomTab( tabbedPane1.getTitleAt( 3 ) ) );
		} else {
			tabbedPane.setTabComponentAt( 1, null );
			tabbedPane.setTabComponentAt( 3, null );
		}
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
		String value = null;
		switch( (String) hiddenTabsNavigationField.getSelectedItem() ) {
			case "moreTabsButton":	value = TABBED_PANE_HIDDEN_TABS_NAVIGATION_MORE_TABS_BUTTON; break;
			case "arrowButtons":	value = TABBED_PANE_HIDDEN_TABS_NAVIGATION_ARROW_BUTTONS; break;
		}

		putTabbedPanesClientProperty( TABBED_PANE_HIDDEN_TABS_NAVIGATION, value );
	}

	private void tabBackForegroundChanged() {
		boolean enabled = tabBackForegroundCheckBox.isSelected();
		tabbedPane1.setBackgroundAt( 0, enabled ? Color.red : null );
		tabbedPane1.setForegroundAt( 1, enabled ? Color.red : null );
	}

	private void leadingComponentChanged() {
		leadingTrailingComponentChanged( leadingComponentCheckBox.isSelected(), TABBED_PANE_LEADING_COMPONENT, "Leading", 4 );
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
				c.setBackground( Color.cyan );
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
			Component c = tabbedPane.getComponentAt( 1 );
			((JComponent)c).putClientProperty( TABBED_PANE_TAB_CLOSABLE, value );
		}
	}

	private void smallerTabHeightChanged() {
		Integer tabHeight = smallerTabHeightCheckBox.isSelected() ? 20 : null;
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
			Component c = tabbedPane.getComponentAt( 1 );
			((JComponent)c).putClientProperty( TABBED_PANE_TAB_INSETS, insets );
		}
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
		moreTabsSpinner = new JSpinner();
		tabScrollCheckBox = new JCheckBox();
		showTabSeparatorsCheckBox = new JCheckBox();
		hideContentSeparatorCheckBox = new JCheckBox();
		tabIconsCheckBox = new JCheckBox();
		tabIconSizeSpinner = new JSpinner();
		customBorderCheckBox = new JCheckBox();
		customTabsCheckBox = new JCheckBox();
		hasFullBorderCheckBox = new JCheckBox();
		JLabel tabPlacementLabel = new JLabel();
		tabPlacementField = new JComboBox<>();
		JLabel hiddenTabsNavigationLabel = new JLabel();
		hiddenTabsNavigationField = new JComboBox<>();
		tabBackForegroundCheckBox = new JCheckBox();
		leadingComponentCheckBox = new JCheckBox();
		trailingComponentCheckBox = new JCheckBox();
		tabsClosableCheckBox = new JCheckBox();
		secondTabClosableCheckBox = new TriStateCheckBox();
		smallerTabHeightCheckBox = new JCheckBox();
		smallerInsetsCheckBox = new JCheckBox();
		secondTabWiderCheckBox = new JCheckBox();
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
					"[fill]" +
					"[]" +
					"[]" +
					"[fill]",
					// rows
					"[center]" +
					"[]" +
					"[]" +
					"[]" +
					"[]"));

				//---- moreTabsCheckBox ----
				moreTabsCheckBox.setText("More tabs");
				moreTabsCheckBox.setMnemonic('M');
				moreTabsCheckBox.addActionListener(e -> moreTabsChanged());
				panel14.add(moreTabsCheckBox, "cell 0 0");

				//---- moreTabsSpinner ----
				moreTabsSpinner.setModel(new SpinnerNumberModel(4, 0, null, 1));
				moreTabsSpinner.setEnabled(false);
				moreTabsSpinner.addChangeListener(e -> moreTabsSpinnerChanged());
				panel14.add(moreTabsSpinner, "cell 1 0");

				//---- tabScrollCheckBox ----
				tabScrollCheckBox.setText("Use scroll layout");
				tabScrollCheckBox.setMnemonic('S');
				tabScrollCheckBox.addActionListener(e -> tabScrollChanged());
				panel14.add(tabScrollCheckBox, "cell 2 0,alignx left,growx 0");

				//---- showTabSeparatorsCheckBox ----
				showTabSeparatorsCheckBox.setText("Show tab separators");
				showTabSeparatorsCheckBox.addActionListener(e -> showTabSeparatorsChanged());
				panel14.add(showTabSeparatorsCheckBox, "cell 3 0");

				//---- hideContentSeparatorCheckBox ----
				hideContentSeparatorCheckBox.setText("Hide content separator");
				hideContentSeparatorCheckBox.addActionListener(e -> hideContentSeparatorChanged());
				panel14.add(hideContentSeparatorCheckBox, "cell 4 0");

				//---- tabIconsCheckBox ----
				tabIconsCheckBox.setText("Tab icons");
				tabIconsCheckBox.addActionListener(e -> tabIconsChanged());
				panel14.add(tabIconsCheckBox, "cell 0 1");

				//---- tabIconSizeSpinner ----
				tabIconSizeSpinner.setModel(new SpinnerListModel(new String[] {"16", "24", "32", "48", "64"}));
				tabIconSizeSpinner.setEnabled(false);
				tabIconSizeSpinner.addChangeListener(e -> tabIconsChanged());
				panel14.add(tabIconSizeSpinner, "cell 1 1");

				//---- customBorderCheckBox ----
				customBorderCheckBox.setText("Custom border");
				customBorderCheckBox.addActionListener(e -> customBorderChanged());
				panel14.add(customBorderCheckBox, "cell 2 1");

				//---- customTabsCheckBox ----
				customTabsCheckBox.setText("Custom tabs");
				customTabsCheckBox.addActionListener(e -> customTabsChanged());
				panel14.add(customTabsCheckBox, "cell 3 1");

				//---- hasFullBorderCheckBox ----
				hasFullBorderCheckBox.setText("Show content border");
				hasFullBorderCheckBox.addActionListener(e -> hasFullBorderChanged());
				panel14.add(hasFullBorderCheckBox, "cell 4 1,alignx left,growx 0");

				//---- tabPlacementLabel ----
				tabPlacementLabel.setText("Tab placement:");
				panel14.add(tabPlacementLabel, "cell 0 2");

				//---- tabPlacementField ----
				tabPlacementField.setModel(new DefaultComboBoxModel<>(new String[] {
					"default",
					"top",
					"bottom",
					"left",
					"right"
				}));
				tabPlacementField.addActionListener(e -> tabPlacementChanged());
				panel14.add(tabPlacementField, "cell 1 2");

				//---- hiddenTabsNavigationLabel ----
				hiddenTabsNavigationLabel.setText("Hidden tabs navigation:");
				panel14.add(hiddenTabsNavigationLabel, "cell 2 2");

				//---- hiddenTabsNavigationField ----
				hiddenTabsNavigationField.setModel(new DefaultComboBoxModel<>(new String[] {
					"default",
					"moreTabsButton",
					"arrowButtons"
				}));
				hiddenTabsNavigationField.addActionListener(e -> hiddenTabsNavigationChanged());
				panel14.add(hiddenTabsNavigationField, "cell 3 2");

				//---- tabBackForegroundCheckBox ----
				tabBackForegroundCheckBox.setText("Tab back/foreground");
				tabBackForegroundCheckBox.addActionListener(e -> tabBackForegroundChanged());
				panel14.add(tabBackForegroundCheckBox, "cell 4 2");

				//---- leadingComponentCheckBox ----
				leadingComponentCheckBox.setText("Leading");
				leadingComponentCheckBox.addActionListener(e -> leadingComponentChanged());
				panel14.add(leadingComponentCheckBox, "cell 0 3");

				//---- trailingComponentCheckBox ----
				trailingComponentCheckBox.setText("Trailing");
				trailingComponentCheckBox.addActionListener(e -> trailingComponentChanged());
				panel14.add(trailingComponentCheckBox, "cell 1 3");

				//---- tabsClosableCheckBox ----
				tabsClosableCheckBox.setText("Tabs closable");
				tabsClosableCheckBox.addActionListener(e -> tabsClosableChanged());
				panel14.add(tabsClosableCheckBox, "cell 2 3");

				//---- secondTabClosableCheckBox ----
				secondTabClosableCheckBox.setText("Second Tab closable");
				secondTabClosableCheckBox.addActionListener(e -> secondTabClosableChanged());
				panel14.add(secondTabClosableCheckBox, "cell 3 3");

				//---- smallerTabHeightCheckBox ----
				smallerTabHeightCheckBox.setText("Smaller tab height");
				smallerTabHeightCheckBox.addActionListener(e -> smallerTabHeightChanged());
				panel14.add(smallerTabHeightCheckBox, "cell 0 4 2 1");

				//---- smallerInsetsCheckBox ----
				smallerInsetsCheckBox.setText("Smaller insets");
				smallerInsetsCheckBox.addActionListener(e -> smallerInsetsChanged());
				panel14.add(smallerInsetsCheckBox, "cell 2 4");

				//---- secondTabWiderCheckBox ----
				secondTabWiderCheckBox.setText("Second Tab wider");
				secondTabWiderCheckBox.addActionListener(e -> secondTabWiderChanged());
				panel14.add(secondTabWiderCheckBox, "cell 3 4");
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
	private JSpinner moreTabsSpinner;
	private JCheckBox tabScrollCheckBox;
	private JCheckBox showTabSeparatorsCheckBox;
	private JCheckBox hideContentSeparatorCheckBox;
	private JCheckBox tabIconsCheckBox;
	private JSpinner tabIconSizeSpinner;
	private JCheckBox customBorderCheckBox;
	private JCheckBox customTabsCheckBox;
	private JCheckBox hasFullBorderCheckBox;
	private JComboBox<String> tabPlacementField;
	private JComboBox<String> hiddenTabsNavigationField;
	private JCheckBox tabBackForegroundCheckBox;
	private JCheckBox leadingComponentCheckBox;
	private JCheckBox trailingComponentCheckBox;
	private JCheckBox tabsClosableCheckBox;
	private TriStateCheckBox secondTabClosableCheckBox;
	private JCheckBox smallerTabHeightCheckBox;
	private JCheckBox smallerInsetsCheckBox;
	private JCheckBox secondTabWiderCheckBox;
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
