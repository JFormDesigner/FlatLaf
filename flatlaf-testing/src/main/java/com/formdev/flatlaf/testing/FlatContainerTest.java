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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.components.FlatTabbedPane;
import com.formdev.flatlaf.extras.components.FlatTabbedPane.*;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
import com.formdev.flatlaf.icons.FlatInternalFrameCloseIcon;
import com.formdev.flatlaf.util.ScaledImageIcon;
import com.jgoodies.forms.factories.CC;
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

		tabTypeComboBox.init( TabType.class, true );

		tabPlacementField.init( TabPlacement.class, true );
		iconPlacementField.init( TabIconPlacement.class, true );
		tabsPopupPolicyField.init( TabsPopupPolicy.class, true );
		scrollButtonsPolicyField.init( ScrollButtonsPolicy.class, true );
		scrollButtonsPlacementField.init( ScrollButtonsPlacement.class, true );
		tabAreaAlignmentField.init( TabAreaAlignment.class, true );
		tabAlignmentField.init( TabAlignment.class, true );
		tabWidthModeField.init( TabWidthMode.class, true );
		tabRotationField.init( TabRotation.class, true );

		tabCountChanged();

		tabsClosableCheckBox.setSelected( true );
		tabsClosableChanged();
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabCloseToolTipText( "Close" );

		tabScrollCheckBox.setSelected( true );
		tabScrollChanged();
	}

	private void continuousLayoutChanged() {
		boolean continuousLayout = continuousLayoutCheckBox.isSelected();
		splitPane1.setContinuousLayout( continuousLayout );
		splitPane2.setContinuousLayout( continuousLayout );
		splitPane3.setContinuousLayout( continuousLayout );
	}

	private void showOnlyOne() {
		boolean showOnlyOne = showOnlyOneCheckBox.isSelected();

		tabbedPane2.setVisible( !showOnlyOne );
		tabbedPane3.setVisible( !showOnlyOne );
		tabbedPane4.setVisible( !showOnlyOne );

		int span = showOnlyOne ? 3 : 1;
		FormLayout formLayout = (FormLayout) tabbedPane1.getParent().getLayout();
		formLayout.setConstraints( tabbedPane1, CC.xywh( 1, 7, span, span ) );
	}

	private void tabScrollChanged() {
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
		putTabbedPanesClientProperty( TABBED_PANE_SHOW_TAB_SEPARATORS, showTabSeparators );
	}

	private void hideContentSeparatorChanged() {
		Boolean showContentSeparator = hideContentSeparatorCheckBox.isSelected() ? false : null;
		putTabbedPanesClientProperty( TABBED_PANE_SHOW_CONTENT_SEPARATOR, showContentSeparator );
	}

	private void hideTabAreaWithOneTabChanged() {
		Boolean hideTabAreaWithOneTab = hideTabAreaWithOneTabCheckBox.isSelected() ? true : null;
		putTabbedPanesClientProperty( TABBED_PANE_HIDE_TAB_AREA_WITH_ONE_TAB, hideTabAreaWithOneTab );
	}

	private void hasFullBorderChanged() {
		Boolean hasFullBorder = hasFullBorderCheckBox.isSelected() ? true : null;
		putTabbedPanesClientProperty( TABBED_PANE_HAS_FULL_BORDER, hasFullBorder );
	}

	private void putTabbedPanesClientProperty( String key, Object value ) {
		for( JTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.putClientProperty( key, value );
	}

	private void tabCountChanged() {
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabCountChanged( tabbedPane );
	}

	private void tabCountChanged( FlatTabbedPane tabbedPane ) {
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

	private void addTab( FlatTabbedPane tabbedPane ) {
		switch( tabbedPane.getTabCount() ) {
			case 0:
				tabbedPane.addTab( "Tab 1", null, new Panel1(), "First tab." );
				break;

			case 1:
				JComponent tab2 = new Panel2();
				tab2.setBorder( new LineBorder( Color.magenta ) );
				tabbedPane.addTab( "Second Tab", null, tab2, "This is the second tab." );
				tabbedPane.setTabCloseToolTipText( 1, "Close Second Tab" );
				break;

			case 2:
				tabbedPane.addTab( "Disabled", createTab( "tab content 3" ) );
				tabbedPane.setEnabledAt( 2, false );
				tabbedPane.setToolTipTextAt( 2, "Disabled tab." );
				tabbedPane.setTabCloseToolTipText( 2, "Close Disabled tab" );
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
		iconPlacementField.setEnabled( tabIconsCheckBox.isSelected() );
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

	private void iconPlacementChanged() {
		TabIconPlacement value = iconPlacementField.getSelectedValue();
		int iconPlacement = (value != null) ? value.value : -1;
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabIconPlacement( iconPlacement );
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

	private void tabsPopupPolicyChanged() {
		TabsPopupPolicy value = tabsPopupPolicyField.getSelectedValue();
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabsPopupPolicy( value );
	}

	private void scrollButtonsPolicyChanged() {
		ScrollButtonsPolicy value = scrollButtonsPolicyField.getSelectedValue();
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setScrollButtonsPolicy( value );
	}

	private void scrollButtonsPlacementChanged() {
		ScrollButtonsPlacement value = scrollButtonsPlacementField.getSelectedValue();
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setScrollButtonsPlacement( value );
	}

	private void tabAreaAlignmentChanged() {
		TabAreaAlignment value = tabAreaAlignmentField.getSelectedValue();
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabAreaAlignment( value );
	}

	private void tabAlignmentChanged() {
		TabAlignment value = tabAlignmentField.getSelectedValue();
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabAlignment( value );
	}

	private void tabWidthModeChanged() {
		TabWidthMode value = tabWidthModeField.getSelectedValue();
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabWidthMode( value );
	}

	private void tabRotationChanged() {
		TabRotation value = tabRotationField.getSelectedValue();
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabRotation( value );
	}

	private void tabTypeChanged() {
		TabType value = tabTypeComboBox.getSelectedValue();
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabType( value );
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
		leadingTrailingComponentChanged( leadingComponentCheckBox.isSelected(), TABBED_PANE_LEADING_COMPONENT, "L", 4 );
	}

	private void trailingComponentChanged() {
		leadingTrailingComponentChanged( trailingComponentCheckBox.isSelected(), TABBED_PANE_TRAILING_COMPONENT, "Trailing", 12 );
	}

	private void leadingTrailingComponentChanged( boolean enabled, String key, String text, int gap ) {
		for( JTabbedPane tabbedPane : allTabbedPanes ) {
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
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabsClosable( closable );

		if( closable ) {
			for( FlatTabbedPane tabbedPane : allTabbedPanes ) {
				tabbedPane.setTabCloseCallback( (tabbedPane2, tabIndex) -> {
					String tabTitle = tabbedPane2.getTitleAt( tabIndex );
					AWTEvent e = EventQueue.getCurrentEvent();
					int modifiers = (e instanceof MouseEvent) ? ((MouseEvent)e).getModifiers() : 0;

					tabbedPane2.removeTabAt( tabIndex );

					JOptionPane.showMessageDialog( this, "Closed tab '" + tabTitle + "'."
						+ "\n\n(modifiers: " + MouseEvent.getMouseModifiersText( modifiers ) + ")",
						"Tab Closed", JOptionPane.PLAIN_MESSAGE );
				} );
			}
		}
	}

	private void secondTabClosableChanged() {
		Boolean closable = secondTabClosableCheckBox.getChecked();

		for( FlatTabbedPane tabbedPane : allTabbedPanes ) {
			if( tabbedPane.getTabCount() > 1 ) {
				if( closable != null )
					tabbedPane.setTabClosable( 1, closable );
				else {
					JComponent c = (JComponent) tabbedPane.getComponentAt( 1 );
					c.putClientProperty( TABBED_PANE_TAB_CLOSABLE, null );
				}
			}
		}
	}

	private void tabAreaInsetsChanged() {
		Insets insets = tabAreaInsetsCheckBox.isSelected() ? new Insets( 5, 5, 10, 10 ) : null;
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabAreaInsets( insets );
	}

	private void smallerTabHeightChanged() {
		int tabHeight = smallerTabHeightCheckBox.isSelected() ? 26 : -1;
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabHeight( tabHeight );
	}

	private void smallerInsetsChanged() {
		Insets insets = smallerInsetsCheckBox.isSelected() ? new Insets( 2, 2, 2, 2 ) : null;
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setTabInsets( insets );
	}

	private void secondTabWiderChanged() {
		Insets insets = secondTabWiderCheckBox.isSelected() ? new Insets( 4, 20, 4, 20 ) : null;

		for( FlatTabbedPane tabbedPane : allTabbedPanes ) {
			if( tabbedPane.getTabCount() > 1 )
				tabbedPane.setTabInsets( 1, insets );
		}
	}

	private void minimumTabWidthChanged() {
		int minimumTabWidth = minimumTabWidthCheckBox.isSelected() ? 100 : -1;
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setMinimumTabWidth( minimumTabWidth );
	}

	private void maximumTabWidthChanged() {
		int maximumTabWidth = maximumTabWidthCheckBox.isSelected() ? 60 : -1;
		for( FlatTabbedPane tabbedPane : allTabbedPanes )
			tabbedPane.setMaximumTabWidth( maximumTabWidth );
	}

	private void customWheelScrollingChanged() {
		if( customMouseWheelScroller != null ) {
			for( FlatTabbedPane tabbedPane : allTabbedPanes )
				tabbedPane.removeMouseWheelListener( customMouseWheelScroller );
			customMouseWheelScroller = null;
		}

		if( customWheelScrollingCheckBox.isSelected() ) {
			customMouseWheelScroller = new MouseWheelListener() {
				@Override
				public void mouseWheelMoved( MouseWheelEvent e ) {
					if( e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL ) {
						JTabbedPane tabbedPane = (JTabbedPane) e.getComponent();
						ActionMap actionMap = tabbedPane.getActionMap();
						Action scrollAction = actionMap.get( (e.getWheelRotation() < 0)
							? "scrollTabsBackwardAction" : "scrollTabsForwardAction" );
						if( scrollAction != null && scrollAction.isEnabled() )
							scrollAction.actionPerformed( new ActionEvent( tabbedPane, 0, "" ) );
					}
				}
			};
			for( FlatTabbedPane tabbedPane : allTabbedPanes )
				tabbedPane.addMouseWheelListener( customMouseWheelScroller );
		}
	}

	private void contextMenuChanged() {
		if( contextMenuListener != null ) {
			for( FlatTabbedPane tabbedPane : allTabbedPanes )
				tabbedPane.removeMouseListener( contextMenuListener );
			contextMenuListener = null;
		}

		if( contextMenuCheckBox.isSelected() ) {
			contextMenuListener = new MouseAdapter() {
				@Override
				public void mousePressed( MouseEvent e ) {
					popupMenu( e );
				}
				@Override
				public void mouseReleased( MouseEvent e ) {
					popupMenu( e );
				}
				private void popupMenu( MouseEvent e ) {
					if( !e.isPopupTrigger() )
						return;

					JTabbedPane tabbedPane = (JTabbedPane) e.getComponent();
					int tabIndex = tabbedPane.indexAtLocation( e.getX(), e.getY() );
					if( tabIndex < 0 )
						return;

					JPopupMenu popupMenu = new JPopupMenu();
					popupMenu.add( "item 1" );
					popupMenu.add( "item 2" );
					popupMenu.add( "item 3" );
					popupMenu.show( tabbedPane, e.getX(), e.getY() );
				}
			};
			for( FlatTabbedPane tabbedPane : allTabbedPanes )
				tabbedPane.addMouseListener( contextMenuListener );
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel9 = new JPanel();
		JLabel splitPaneLabel = new JLabel();
		continuousLayoutCheckBox = new JCheckBox();
		splitPane3 = new JSplitPane();
		splitPane1 = new JSplitPane();
		FlatContainerTest.Panel1 panel15 = new FlatContainerTest.Panel1();
		FlatContainerTest.Panel2 panel21 = new FlatContainerTest.Panel2();
		splitPane2 = new JSplitPane();
		JPanel panel12 = new JPanel();
		JLabel label3 = new JLabel();
		JPanel panel13 = new JPanel();
		JLabel label4 = new JLabel();
		JLabel tabbedPaneLabel = new JLabel();
		showOnlyOneCheckBox = new JCheckBox();
		tabbedPane1 = new FlatTabbedPane();
		tabbedPane3 = new FlatTabbedPane();
		tabbedPane2 = new FlatTabbedPane();
		tabbedPane4 = new FlatTabbedPane();
		FlatTestFrame.NoRightToLeftPanel tabbedPaneControlPanel = new FlatTestFrame.NoRightToLeftPanel();
		tabScrollCheckBox = new JCheckBox();
		JLabel tabCountLabel = new JLabel();
		tabCountSpinner = new JSpinner();
		customTabsCheckBox = new JCheckBox();
		htmlTabsCheckBox = new JCheckBox();
		multiLineTabsCheckBox = new JCheckBox();
		JLabel tabPlacementLabel = new JLabel();
		tabPlacementField = new FlatTestEnumSelector<>();
		tabBackForegroundCheckBox = new JCheckBox();
		JLabel tabsPopupPolicyLabel = new JLabel();
		tabsPopupPolicyField = new FlatTestEnumSelector<>();
		tabIconsCheckBox = new JCheckBox();
		tabIconSizeSpinner = new JSpinner();
		iconPlacementField = new FlatTestEnumSelector<>();
		JLabel scrollButtonsPolicyLabel = new JLabel();
		scrollButtonsPolicyField = new FlatTestEnumSelector<>();
		tabsClosableCheckBox = new JCheckBox();
		JLabel scrollButtonsPlacementLabel = new JLabel();
		scrollButtonsPlacementField = new FlatTestEnumSelector<>();
		secondTabClosableCheckBox = new FlatTriStateCheckBox();
		JLabel tabAreaAlignmentLabel = new JLabel();
		tabAreaAlignmentField = new FlatTestEnumSelector<>();
		JLabel tabWidthModeLabel = new JLabel();
		tabWidthModeField = new FlatTestEnumSelector<>();
		JLabel tabAlignmentLabel = new JLabel();
		tabAlignmentField = new FlatTestEnumSelector<>();
		JLabel tabRotationLabel = new JLabel();
		tabRotationField = new FlatTestEnumSelector<>();
		JLabel tabTypeLabel = new JLabel();
		tabTypeComboBox = new FlatTestEnumSelector<>();
		leadingComponentCheckBox = new JCheckBox();
		customBorderCheckBox = new JCheckBox();
		tabAreaInsetsCheckBox = new JCheckBox();
		trailingComponentCheckBox = new JCheckBox();
		hasFullBorderCheckBox = new JCheckBox();
		smallerTabHeightCheckBox = new JCheckBox();
		minimumTabWidthCheckBox = new JCheckBox();
		hideContentSeparatorCheckBox = new JCheckBox();
		smallerInsetsCheckBox = new JCheckBox();
		maximumTabWidthCheckBox = new JCheckBox();
		showTabSeparatorsCheckBox = new JCheckBox();
		secondTabWiderCheckBox = new JCheckBox();
		hideTabAreaWithOneTabCheckBox = new JCheckBox();
		customWheelScrollingCheckBox = new JCheckBox();
		contextMenuCheckBox = new JCheckBox();
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

			//---- continuousLayoutCheckBox ----
			continuousLayoutCheckBox.setText("Continuous Layout");
			continuousLayoutCheckBox.setSelected(true);
			continuousLayoutCheckBox.addActionListener(e -> continuousLayoutChanged());
			panel9.add(continuousLayoutCheckBox, cc.xy(3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

			//======== splitPane3 ========
			{
				splitPane3.setResizeWeight(0.5);

				//======== splitPane1 ========
				{
					splitPane1.setResizeWeight(0.5);
					splitPane1.setOneTouchExpandable(true);

					//---- panel15 ----
					panel15.setBackground(new Color(0xd9a343));
					splitPane1.setLeftComponent(panel15);

					//---- panel21 ----
					panel21.setBackground(new Color(0x62b543));
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
						panel12.setBackground(new Color(0xf26522));
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
						panel13.setBackground(new Color(0x40b6e0));
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

			//---- showOnlyOneCheckBox ----
			showOnlyOneCheckBox.setText("show only one tabbed pane");
			showOnlyOneCheckBox.setMnemonic('W');
			showOnlyOneCheckBox.addActionListener(e -> showOnlyOne());
			panel9.add(showOnlyOneCheckBox, cc.xy(3, 5, CellConstraints.RIGHT, CellConstraints.DEFAULT));
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
				tabbedPaneControlPanel.putClientProperty("FlatLaf.internal.testing.ignore", true);
				tabbedPaneControlPanel.setLayout(new MigLayout(
					"insets 0,hidemode 3",
					// columns
					"[]" +
					"[]" +
					"[]" +
					"[]",
					// rows
					"[center]" +
					"[]" +
					"[]" +
					"[]" +
					"[]" +
					"[]" +
					"[]" +
					"[]para" +
					"[]" +
					"[]para" +
					"[]" +
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
				tabbedPaneControlPanel.add(customTabsCheckBox, "cell 2 0 2 1");

				//---- htmlTabsCheckBox ----
				htmlTabsCheckBox.setText("HTML");
				htmlTabsCheckBox.addActionListener(e -> htmlTabsChanged());
				tabbedPaneControlPanel.add(htmlTabsCheckBox, "cell 2 0 2 1");

				//---- multiLineTabsCheckBox ----
				multiLineTabsCheckBox.setText("multi-line");
				multiLineTabsCheckBox.addActionListener(e -> htmlTabsChanged());
				tabbedPaneControlPanel.add(multiLineTabsCheckBox, "cell 2 0 2 1");

				//---- tabPlacementLabel ----
				tabPlacementLabel.setText("Tab placement:");
				tabbedPaneControlPanel.add(tabPlacementLabel, "cell 0 1");

				//---- tabPlacementField ----
				tabPlacementField.addActionListener(e -> tabPlacementChanged());
				tabbedPaneControlPanel.add(tabPlacementField, "cell 1 1");

				//---- tabBackForegroundCheckBox ----
				tabBackForegroundCheckBox.setText("Tab back/foreground");
				tabBackForegroundCheckBox.addActionListener(e -> tabBackForegroundChanged());
				tabbedPaneControlPanel.add(tabBackForegroundCheckBox, "cell 2 1 2 1");

				//---- tabsPopupPolicyLabel ----
				tabsPopupPolicyLabel.setText("Tabs popup policy:");
				tabbedPaneControlPanel.add(tabsPopupPolicyLabel, "cell 0 2");

				//---- tabsPopupPolicyField ----
				tabsPopupPolicyField.addActionListener(e -> tabsPopupPolicyChanged());
				tabbedPaneControlPanel.add(tabsPopupPolicyField, "cell 1 2");

				//---- tabIconsCheckBox ----
				tabIconsCheckBox.setText("Tab icons");
				tabIconsCheckBox.addActionListener(e -> tabIconsChanged());
				tabbedPaneControlPanel.add(tabIconsCheckBox, "cell 2 2 2 1");

				//---- tabIconSizeSpinner ----
				tabIconSizeSpinner.setModel(new SpinnerListModel(new String[] {"16", "24", "32", "48", "64"}));
				tabIconSizeSpinner.setEnabled(false);
				tabIconSizeSpinner.addChangeListener(e -> tabIconsChanged());
				tabbedPaneControlPanel.add(tabIconSizeSpinner, "cell 2 2 2 1");

				//---- iconPlacementField ----
				iconPlacementField.setEnabled(false);
				iconPlacementField.addActionListener(e -> iconPlacementChanged());
				tabbedPaneControlPanel.add(iconPlacementField, "cell 2 2 2 1");

				//---- scrollButtonsPolicyLabel ----
				scrollButtonsPolicyLabel.setText("Scroll buttons policy:");
				tabbedPaneControlPanel.add(scrollButtonsPolicyLabel, "cell 0 3");

				//---- scrollButtonsPolicyField ----
				scrollButtonsPolicyField.addActionListener(e -> scrollButtonsPolicyChanged());
				tabbedPaneControlPanel.add(scrollButtonsPolicyField, "cell 1 3");

				//---- tabsClosableCheckBox ----
				tabsClosableCheckBox.setText("Tabs closable");
				tabsClosableCheckBox.addActionListener(e -> tabsClosableChanged());
				tabbedPaneControlPanel.add(tabsClosableCheckBox, "cell 2 3 2 1");

				//---- scrollButtonsPlacementLabel ----
				scrollButtonsPlacementLabel.setText("Scroll buttons placement:");
				tabbedPaneControlPanel.add(scrollButtonsPlacementLabel, "cell 0 4");

				//---- scrollButtonsPlacementField ----
				scrollButtonsPlacementField.addActionListener(e -> scrollButtonsPlacementChanged());
				tabbedPaneControlPanel.add(scrollButtonsPlacementField, "cell 1 4");

				//---- secondTabClosableCheckBox ----
				secondTabClosableCheckBox.setText("Second Tab closable");
				secondTabClosableCheckBox.addActionListener(e -> secondTabClosableChanged());
				tabbedPaneControlPanel.add(secondTabClosableCheckBox, "cell 2 4 2 1");

				//---- tabAreaAlignmentLabel ----
				tabAreaAlignmentLabel.setText("Tab area alignment:");
				tabbedPaneControlPanel.add(tabAreaAlignmentLabel, "cell 0 5");

				//---- tabAreaAlignmentField ----
				tabAreaAlignmentField.addActionListener(e -> tabAreaAlignmentChanged());
				tabbedPaneControlPanel.add(tabAreaAlignmentField, "cell 1 5");

				//---- tabWidthModeLabel ----
				tabWidthModeLabel.setText("Tab width mode:");
				tabbedPaneControlPanel.add(tabWidthModeLabel, "cell 2 5");

				//---- tabWidthModeField ----
				tabWidthModeField.addActionListener(e -> tabWidthModeChanged());
				tabbedPaneControlPanel.add(tabWidthModeField, "cell 3 5");

				//---- tabAlignmentLabel ----
				tabAlignmentLabel.setText("Tab title alignment:");
				tabbedPaneControlPanel.add(tabAlignmentLabel, "cell 0 6");

				//---- tabAlignmentField ----
				tabAlignmentField.addActionListener(e -> tabAlignmentChanged());
				tabbedPaneControlPanel.add(tabAlignmentField, "cell 1 6");

				//---- tabRotationLabel ----
				tabRotationLabel.setText("Tab rotation:");
				tabbedPaneControlPanel.add(tabRotationLabel, "cell 2 6");

				//---- tabRotationField ----
				tabRotationField.addActionListener(e -> tabRotationChanged());
				tabbedPaneControlPanel.add(tabRotationField, "cell 3 6");

				//---- tabTypeLabel ----
				tabTypeLabel.setText("Tab type:");
				tabbedPaneControlPanel.add(tabTypeLabel, "cell 0 7");

				//---- tabTypeComboBox ----
				tabTypeComboBox.addActionListener(e -> tabTypeChanged());
				tabbedPaneControlPanel.add(tabTypeComboBox, "cell 1 7");

				//---- leadingComponentCheckBox ----
				leadingComponentCheckBox.setText("Leading component");
				leadingComponentCheckBox.addActionListener(e -> leadingComponentChanged());
				tabbedPaneControlPanel.add(leadingComponentCheckBox, "cell 0 8");

				//---- customBorderCheckBox ----
				customBorderCheckBox.setText("Custom border");
				customBorderCheckBox.addActionListener(e -> customBorderChanged());
				tabbedPaneControlPanel.add(customBorderCheckBox, "cell 1 8");

				//---- tabAreaInsetsCheckBox ----
				tabAreaInsetsCheckBox.setText("Tab area insets (5,5,10,10)");
				tabAreaInsetsCheckBox.addActionListener(e -> tabAreaInsetsChanged());
				tabbedPaneControlPanel.add(tabAreaInsetsCheckBox, "cell 2 8 2 1");

				//---- trailingComponentCheckBox ----
				trailingComponentCheckBox.setText("Trailing component");
				trailingComponentCheckBox.addActionListener(e -> trailingComponentChanged());
				tabbedPaneControlPanel.add(trailingComponentCheckBox, "cell 0 9");

				//---- hasFullBorderCheckBox ----
				hasFullBorderCheckBox.setText("Show content border");
				hasFullBorderCheckBox.addActionListener(e -> hasFullBorderChanged());
				tabbedPaneControlPanel.add(hasFullBorderCheckBox, "cell 1 9,alignx left,growx 0");

				//---- smallerTabHeightCheckBox ----
				smallerTabHeightCheckBox.setText("Smaller tab height (26)");
				smallerTabHeightCheckBox.addActionListener(e -> smallerTabHeightChanged());
				tabbedPaneControlPanel.add(smallerTabHeightCheckBox, "cell 2 9 2 1");

				//---- minimumTabWidthCheckBox ----
				minimumTabWidthCheckBox.setText("Minimum tab width (100)");
				minimumTabWidthCheckBox.addActionListener(e -> minimumTabWidthChanged());
				tabbedPaneControlPanel.add(minimumTabWidthCheckBox, "cell 0 10");

				//---- hideContentSeparatorCheckBox ----
				hideContentSeparatorCheckBox.setText("Hide content separator");
				hideContentSeparatorCheckBox.addActionListener(e -> hideContentSeparatorChanged());
				tabbedPaneControlPanel.add(hideContentSeparatorCheckBox, "cell 1 10");

				//---- smallerInsetsCheckBox ----
				smallerInsetsCheckBox.setText("Smaller tab insets (2,2,2,2)");
				smallerInsetsCheckBox.addActionListener(e -> smallerInsetsChanged());
				tabbedPaneControlPanel.add(smallerInsetsCheckBox, "cell 2 10 2 1");

				//---- maximumTabWidthCheckBox ----
				maximumTabWidthCheckBox.setText("Maximum tab width (60)");
				maximumTabWidthCheckBox.addActionListener(e -> maximumTabWidthChanged());
				tabbedPaneControlPanel.add(maximumTabWidthCheckBox, "cell 0 11");

				//---- showTabSeparatorsCheckBox ----
				showTabSeparatorsCheckBox.setText("Show tab separators");
				showTabSeparatorsCheckBox.addActionListener(e -> showTabSeparatorsChanged());
				tabbedPaneControlPanel.add(showTabSeparatorsCheckBox, "cell 1 11");

				//---- secondTabWiderCheckBox ----
				secondTabWiderCheckBox.setText("Second Tab insets wider (4,20,4,20)");
				secondTabWiderCheckBox.addActionListener(e -> secondTabWiderChanged());
				tabbedPaneControlPanel.add(secondTabWiderCheckBox, "cell 2 11 2 1");

				//---- hideTabAreaWithOneTabCheckBox ----
				hideTabAreaWithOneTabCheckBox.setText("Hide tab area with one tab");
				hideTabAreaWithOneTabCheckBox.addActionListener(e -> hideTabAreaWithOneTabChanged());
				tabbedPaneControlPanel.add(hideTabAreaWithOneTabCheckBox, "cell 1 12");

				//---- customWheelScrollingCheckBox ----
				customWheelScrollingCheckBox.setText("Custom wheel scrolling");
				customWheelScrollingCheckBox.addActionListener(e -> customWheelScrollingChanged());
				tabbedPaneControlPanel.add(customWheelScrollingCheckBox, "cell 2 12 2 1");

				//---- contextMenuCheckBox ----
				contextMenuCheckBox.setText("Context menu on tabs");
				contextMenuCheckBox.addActionListener(e -> contextMenuChanged());
				tabbedPaneControlPanel.add(contextMenuCheckBox, "cell 2 13 2 1");
			}
			panel9.add(tabbedPaneControlPanel, cc.xywh(1, 11, 3, 1));
		}
		add(panel9, "cell 0 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		allTabbedPanes = new FlatTabbedPane[] { tabbedPane1, tabbedPane2, tabbedPane3, tabbedPane4 };
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox continuousLayoutCheckBox;
	private JSplitPane splitPane3;
	private JSplitPane splitPane1;
	private JSplitPane splitPane2;
	private JCheckBox showOnlyOneCheckBox;
	private FlatTabbedPane tabbedPane1;
	private FlatTabbedPane tabbedPane3;
	private FlatTabbedPane tabbedPane2;
	private FlatTabbedPane tabbedPane4;
	private JCheckBox tabScrollCheckBox;
	private JSpinner tabCountSpinner;
	private JCheckBox customTabsCheckBox;
	private JCheckBox htmlTabsCheckBox;
	private JCheckBox multiLineTabsCheckBox;
	private FlatTestEnumSelector<TabPlacement> tabPlacementField;
	private JCheckBox tabBackForegroundCheckBox;
	private FlatTestEnumSelector<TabsPopupPolicy> tabsPopupPolicyField;
	private JCheckBox tabIconsCheckBox;
	private JSpinner tabIconSizeSpinner;
	private FlatTestEnumSelector<TabIconPlacement> iconPlacementField;
	private FlatTestEnumSelector<ScrollButtonsPolicy> scrollButtonsPolicyField;
	private JCheckBox tabsClosableCheckBox;
	private FlatTestEnumSelector<ScrollButtonsPlacement> scrollButtonsPlacementField;
	private FlatTriStateCheckBox secondTabClosableCheckBox;
	private FlatTestEnumSelector<TabAreaAlignment> tabAreaAlignmentField;
	private FlatTestEnumSelector<TabWidthMode> tabWidthModeField;
	private FlatTestEnumSelector<TabAlignment> tabAlignmentField;
	private FlatTestEnumSelector<TabRotation> tabRotationField;
	private FlatTestEnumSelector<TabType> tabTypeComboBox;
	private JCheckBox leadingComponentCheckBox;
	private JCheckBox customBorderCheckBox;
	private JCheckBox tabAreaInsetsCheckBox;
	private JCheckBox trailingComponentCheckBox;
	private JCheckBox hasFullBorderCheckBox;
	private JCheckBox smallerTabHeightCheckBox;
	private JCheckBox minimumTabWidthCheckBox;
	private JCheckBox hideContentSeparatorCheckBox;
	private JCheckBox smallerInsetsCheckBox;
	private JCheckBox maximumTabWidthCheckBox;
	private JCheckBox showTabSeparatorsCheckBox;
	private JCheckBox secondTabWiderCheckBox;
	private JCheckBox hideTabAreaWithOneTabCheckBox;
	private JCheckBox customWheelScrollingCheckBox;
	private JCheckBox contextMenuCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	private FlatTabbedPane[] allTabbedPanes;
	private MouseWheelListener customMouseWheelScroller;
	private MouseListener contextMenuListener;

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

	//---- enum TabIconPlacement ----------------------------------------------

	enum TabIconPlacement {
		leading( SwingConstants.LEADING ),
		trailing( SwingConstants.TRAILING ),
		top( SwingConstants.TOP ),
		bottom( SwingConstants.BOTTOM );

		public final int value;

		TabIconPlacement( int value ) {
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
