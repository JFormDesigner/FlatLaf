/*
 * Created by JFormDesigner on Tue Aug 27 21:47:02 CEST 2019
 */

package com.formdev.flatlaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.layout.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatContainerTest
	extends JPanel
{
	public static void main( String[] args ) {
		FlatTestFrame frame = FlatTestFrame.create( args, "FlatContainerTest" );
		frame.showFrame( new FlatContainerTest() );
	}

	public FlatContainerTest() {
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
		tabbedPane1.putClientProperty( "JTabbedPane.hasFullBorder", hasFullBorder );
		tabbedPane2.putClientProperty( "JTabbedPane.hasFullBorder", hasFullBorder );
		tabbedPane3.putClientProperty( "JTabbedPane.hasFullBorder", hasFullBorder );
		tabbedPane4.putClientProperty( "JTabbedPane.hasFullBorder", hasFullBorder );
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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel9 = new JPanel();
		JLabel tabbedPaneLabel = new JLabel();
		tabbedPane1 = new JTabbedPane();
		JPanel panel1 = new JPanel();
		JLabel label1 = new JLabel();
		JPanel panel2 = new JPanel();
		JLabel label2 = new JLabel();
		tabbedPane3 = new JTabbedPane();
		JPanel panel5 = new JPanel();
		JLabel label5 = new JLabel();
		JPanel panel6 = new JPanel();
		JLabel label6 = new JLabel();
		tabbedPane2 = new JTabbedPane();
		JPanel panel3 = new JPanel();
		JLabel label3 = new JLabel();
		JPanel panel4 = new JPanel();
		JLabel label4 = new JLabel();
		tabbedPane4 = new JTabbedPane();
		JPanel panel7 = new JPanel();
		JLabel label7 = new JLabel();
		JPanel panel8 = new JPanel();
		JLabel label8 = new JLabel();
		tabScrollCheckBox = new JCheckBox();
		hasFullBorderCheckBox = new JCheckBox();
		moreTabsCheckBox = new JCheckBox();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setLayout(new MigLayout(
			"insets 0,hidemode 3",
			// columns
			"[grow,fill]" +
			"[fill]",
			// rows
			"[grow,fill]" +
			"[]"));

		//======== panel9 ========
		{
			panel9.setLayout(new FormLayout(
				"70dlu:grow, $lcgap, 70dlu:grow",
				"pref, 2*($lgap, fill:70dlu:grow), 2*($lgap, pref)"));

			//---- tabbedPaneLabel ----
			tabbedPaneLabel.setText("JTabbedPane:");
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

			//---- tabScrollCheckBox ----
			tabScrollCheckBox.setText("tabLayoutPolicy = SCROLL");
			tabScrollCheckBox.setMnemonic('S');
			tabScrollCheckBox.addActionListener(e -> tabScrollChanged());
			panel9.add(tabScrollCheckBox, cc.xy(1, 7, CellConstraints.LEFT, CellConstraints.DEFAULT));

			//---- hasFullBorderCheckBox ----
			hasFullBorderCheckBox.setText("JTabbedPane.hasFullBorder");
			hasFullBorderCheckBox.setMnemonic('F');
			hasFullBorderCheckBox.addActionListener(e -> hasFullBorderChanged());
			panel9.add(hasFullBorderCheckBox, cc.xy(3, 7, CellConstraints.LEFT, CellConstraints.DEFAULT));

			//---- moreTabsCheckBox ----
			moreTabsCheckBox.setText("more tabs");
			moreTabsCheckBox.setMnemonic('M');
			moreTabsCheckBox.addActionListener(e -> moreTabsChanged());
			panel9.add(moreTabsCheckBox, cc.xy(1, 9, CellConstraints.LEFT, CellConstraints.DEFAULT));
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
	private JCheckBox hasFullBorderCheckBox;
	private JCheckBox moreTabsCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
