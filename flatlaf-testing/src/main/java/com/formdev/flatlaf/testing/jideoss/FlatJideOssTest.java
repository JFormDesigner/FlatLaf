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

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_HAS_FULL_BORDER;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.testing.*;
import com.formdev.flatlaf.testing.FlatTestFrame;
import com.jgoodies.forms.layout.*;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatJideOssTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatJideOssTest" );
			LookAndFeelFactory.installJideExtension();
			frame.showFrame( FlatJideOssTest::new );

			UIManager.addPropertyChangeListener( e -> {
				if( "lookAndFeel".equals( e.getPropertyName() ) ) {
					LookAndFeelFactory.installJideExtension();
				}
			} );
		} );
	}

	FlatJideOssTest() {
		initComponents();
	}

	private void tabScrollChanged() {
		int tabLayoutPolicy = tabScrollCheckBox.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT;
		tabbedPane1.setTabLayoutPolicy( tabLayoutPolicy );
		tabbedPane2.setTabLayoutPolicy( tabLayoutPolicy );
		tabbedPane3.setTabLayoutPolicy( tabLayoutPolicy );
		tabbedPane4.setTabLayoutPolicy( tabLayoutPolicy );
	}

	private void hasFullBorderChanged() {
		Boolean hasFullBorder = hasFullBorderCheckBox.isSelected() ? true : null;
		tabbedPane1.putClientProperty( TABBED_PANE_HAS_FULL_BORDER, hasFullBorder );
		tabbedPane2.putClientProperty( TABBED_PANE_HAS_FULL_BORDER, hasFullBorder );
		tabbedPane3.putClientProperty( TABBED_PANE_HAS_FULL_BORDER, hasFullBorder );
		tabbedPane4.putClientProperty( TABBED_PANE_HAS_FULL_BORDER, hasFullBorder );
	}

	private void moreTabsChanged() {
		boolean moreTabs = moreTabsCheckBox.isSelected();
		addRemoveMoreTabs( tabbedPane1, moreTabs );
		addRemoveMoreTabs( tabbedPane2, moreTabs );
		addRemoveMoreTabs( tabbedPane3, moreTabs );
		addRemoveMoreTabs( tabbedPane4, moreTabs );
	}

	private void addRemoveMoreTabs( JTabbedPane tabbedPane, boolean add ) {
		if( add ) {
			tabbedPane.addTab( "Tab 4", new JLabel( "tab 4" ) );
			tabbedPane.addTab( "Tab 5", new JLabel( "tab 5" ) );
		} else {
			int tabCount = tabbedPane.getTabCount();
			if( tabCount > 3 ) {
				for( int i = 0; i < 2; i++ )
					tabbedPane.removeTabAt( tabbedPane.getTabCount() - 1 );
			}
		}
	}

	private void showJidePopupButtonActionPerformed( ActionEvent e ) {
		Component invoker = (Component) e.getSource();

		JPanel panel = new JPanel( new MigLayout() );
		panel.add( new JLabel( "Name:") );
		panel.add( new JTextField( 20 ) );

		JidePopup popupMenu = new JidePopup();
		popupMenu.add( panel );
		popupMenu.setDetached( true );
		popupMenu.setOwner( invoker );
		popupMenu.showPopup();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel9 = new JPanel();
		JLabel tabbedPaneLabel = new JLabel();
		tabbedPane1 = new JideTabbedPane();
		JPanel panel1 = new JPanel();
		JLabel label1 = new JLabel();
		JPanel panel2 = new JPanel();
		JLabel label2 = new JLabel();
		tabbedPane3 = new JideTabbedPane();
		JPanel panel5 = new JPanel();
		JLabel label5 = new JLabel();
		JPanel panel6 = new JPanel();
		JLabel label6 = new JLabel();
		tabbedPane2 = new JideTabbedPane();
		JPanel panel3 = new JPanel();
		JLabel label3 = new JLabel();
		JPanel panel4 = new JPanel();
		JLabel label4 = new JLabel();
		tabbedPane4 = new JideTabbedPane();
		JPanel panel7 = new JPanel();
		JLabel label7 = new JLabel();
		JPanel panel8 = new JPanel();
		JLabel label8 = new JLabel();
		JPanel panel14 = new JPanel();
		moreTabsCheckBox = new JCheckBox();
		tabScrollCheckBox = new JCheckBox();
		hasFullBorderCheckBox = new JCheckBox();
		JPanel panel10 = new JPanel();
		JLabel jidePopupLabel = new JLabel();
		JButton showJidePopupButton = new JButton();
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
				"70dlu:grow, $lcgap, 70dlu:grow",
				"pref, 2*($lgap, fill:70dlu:grow), $lgap, pref, $lgap, default"));

			//---- tabbedPaneLabel ----
			tabbedPaneLabel.setText("JideTabbedPane:");
			panel9.add(tabbedPaneLabel, cc.xy(1, 1));

			//======== tabbedPane1 ========
			{

				//======== panel1 ========
				{
					panel1.setLayout(new FlowLayout());

					//---- label1 ----
					label1.setText("TOP");
					panel1.add(label1);
				}
				tabbedPane1.addTab("Tab 1", panel1);

				//======== panel2 ========
				{
					panel2.setBorder(new LineBorder(Color.magenta));
					panel2.setLayout(new FlowLayout());
				}
				tabbedPane1.addTab("Tab 2", panel2);

				//---- label2 ----
				label2.setText("text");
				tabbedPane1.addTab("Tab 3", label2);
			}
			panel9.add(tabbedPane1, cc.xy(1, 3));

			//======== tabbedPane3 ========
			{
				tabbedPane3.setTabPlacement(SwingConstants.LEFT);

				//======== panel5 ========
				{
					panel5.setLayout(new FlowLayout());

					//---- label5 ----
					label5.setText("LEFT");
					panel5.add(label5);
				}
				tabbedPane3.addTab("Tab 1", panel5);

				//======== panel6 ========
				{
					panel6.setBorder(new LineBorder(Color.magenta));
					panel6.setLayout(new FlowLayout());
				}
				tabbedPane3.addTab("Tab 2", panel6);

				//---- label6 ----
				label6.setText("text");
				tabbedPane3.addTab("Tab 3", label6);
			}
			panel9.add(tabbedPane3, cc.xy(3, 3));

			//======== tabbedPane2 ========
			{
				tabbedPane2.setTabPlacement(SwingConstants.BOTTOM);

				//======== panel3 ========
				{
					panel3.setLayout(new FlowLayout());

					//---- label3 ----
					label3.setText("BOTTOM");
					panel3.add(label3);
				}
				tabbedPane2.addTab("Tab 1", panel3);

				//======== panel4 ========
				{
					panel4.setBorder(new LineBorder(Color.magenta));
					panel4.setLayout(new FlowLayout());
				}
				tabbedPane2.addTab("Tab 2", panel4);
				tabbedPane2.setEnabledAt(1, false);

				//---- label4 ----
				label4.setText("text");
				tabbedPane2.addTab("Tab 3", label4);
			}
			panel9.add(tabbedPane2, cc.xy(1, 5));

			//======== tabbedPane4 ========
			{
				tabbedPane4.setTabPlacement(SwingConstants.RIGHT);

				//======== panel7 ========
				{
					panel7.setLayout(new FlowLayout());

					//---- label7 ----
					label7.setText("RIGHT");
					panel7.add(label7);
				}
				tabbedPane4.addTab("Tab 1", panel7);

				//======== panel8 ========
				{
					panel8.setBorder(new LineBorder(Color.magenta));
					panel8.setLayout(new FlowLayout());
				}
				tabbedPane4.addTab("Tab 2", panel8);

				//---- label8 ----
				label8.setText("text");
				tabbedPane4.addTab("Tab 3", label8);
			}
			panel9.add(tabbedPane4, cc.xy(3, 5));

			//======== panel14 ========
			{
				panel14.setOpaque(false);
				panel14.setLayout(new MigLayout(
					"insets 0,hidemode 3",
					// columns
					"[]" +
					"[]" +
					"[]",
					// rows
					"[center]"));

				//---- moreTabsCheckBox ----
				moreTabsCheckBox.setText("more tabs");
				moreTabsCheckBox.setMnemonic('M');
				moreTabsCheckBox.addActionListener(e -> moreTabsChanged());
				panel14.add(moreTabsCheckBox, "cell 0 0");

				//---- tabScrollCheckBox ----
				tabScrollCheckBox.setText("tabLayoutPolicy = SCROLL");
				tabScrollCheckBox.setMnemonic('S');
				tabScrollCheckBox.setSelected(true);
				tabScrollCheckBox.addActionListener(e -> tabScrollChanged());
				panel14.add(tabScrollCheckBox, "cell 1 0,alignx left,growx 0");

				//---- hasFullBorderCheckBox ----
				hasFullBorderCheckBox.setText("JTabbedPane.hasFullBorder");
				hasFullBorderCheckBox.setMnemonic('F');
				hasFullBorderCheckBox.addActionListener(e -> hasFullBorderChanged());
				panel14.add(hasFullBorderCheckBox, "cell 2 0,alignx left,growx 0");
			}
			panel9.add(panel14, cc.xywh(1, 7, 3, 1));

			//======== panel10 ========
			{
				panel10.setLayout(new MigLayout(
					"insets 3 0 3 3,hidemode 3",
					// columns
					"[fill]" +
					"[fill]",
					// rows
					"[]"));

				//---- jidePopupLabel ----
				jidePopupLabel.setText("JidePopup:");
				panel10.add(jidePopupLabel, "cell 0 0");

				//---- showJidePopupButton ----
				showJidePopupButton.setText("show JidePopup");
				showJidePopupButton.addActionListener(e -> showJidePopupButtonActionPerformed(e));
				panel10.add(showJidePopupButton, "cell 1 0");
			}
			panel9.add(panel10, cc.xy(1, 9));
		}
		add(panel9, "cell 0 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JideTabbedPane tabbedPane1;
	private JideTabbedPane tabbedPane3;
	private JideTabbedPane tabbedPane2;
	private JideTabbedPane tabbedPane4;
	private JCheckBox moreTabsCheckBox;
	private JCheckBox tabScrollCheckBox;
	private JCheckBox hasFullBorderCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
