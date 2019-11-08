/*
 * Created by JFormDesigner on Tue Aug 27 21:47:02 CEST 2019
 */

package com.formdev.flatlaf.testing;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_HAS_FULL_BORDER;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel panel9 = new JPanel();
		JLabel splitPaneLabel = new JLabel();
		JSplitPane splitPane3 = new JSplitPane();
		JSplitPane splitPane1 = new JSplitPane();
		JPanel panel10 = new JPanel();
		JPanel panel11 = new JPanel();
		JSplitPane splitPane2 = new JSplitPane();
		JPanel panel12 = new JPanel();
		JPanel panel13 = new JPanel();
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
		JPanel panel14 = new JPanel();
		moreTabsCheckBox = new JCheckBox();
		tabScrollCheckBox = new JCheckBox();
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
			panel9.setOpaque(false);
			panel9.setLayout(new FormLayout(
				"70dlu:grow, $lcgap, 70dlu:grow",
				"default, $lgap, fill:70dlu, $lgap, pref, 2*($lgap, fill:70dlu:grow), $lgap, pref"));

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

					//======== panel10 ========
					{
						panel10.setBackground(Color.orange);
						panel10.setLayout(new FlowLayout());
					}
					splitPane1.setLeftComponent(panel10);

					//======== panel11 ========
					{
						panel11.setBackground(Color.magenta);
						panel11.setLayout(new FlowLayout());
					}
					splitPane1.setRightComponent(panel11);
				}
				splitPane3.setLeftComponent(splitPane1);

				//======== splitPane2 ========
				{
					splitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
					splitPane2.setResizeWeight(0.5);
					splitPane2.setOneTouchExpandable(true);

					//======== panel12 ========
					{
						panel12.setBackground(Color.orange);
						panel12.setLayout(new FlowLayout());
					}
					splitPane2.setTopComponent(panel12);

					//======== panel13 ========
					{
						panel13.setBackground(Color.magenta);
						panel13.setLayout(new FlowLayout());
					}
					splitPane2.setBottomComponent(panel13);
				}
				splitPane3.setRightComponent(splitPane2);
			}
			panel9.add(splitPane3, cc.xywh(1, 3, 3, 1));

			//---- tabbedPaneLabel ----
			tabbedPaneLabel.setText("JTabbedPane:");
			panel9.add(tabbedPaneLabel, cc.xy(1, 5));

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
			panel9.add(tabbedPane1, cc.xy(1, 7));

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
			panel9.add(tabbedPane3, cc.xy(3, 7));

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
			panel9.add(tabbedPane2, cc.xy(1, 9));

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
			panel9.add(tabbedPane4, cc.xy(3, 9));

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
				tabScrollCheckBox.addActionListener(e -> tabScrollChanged());
				panel14.add(tabScrollCheckBox, "cell 1 0,alignx left,growx 0");

				//---- hasFullBorderCheckBox ----
				hasFullBorderCheckBox.setText("JTabbedPane.hasFullBorder");
				hasFullBorderCheckBox.setMnemonic('F');
				hasFullBorderCheckBox.addActionListener(e -> hasFullBorderChanged());
				panel14.add(hasFullBorderCheckBox, "cell 2 0,alignx left,growx 0");
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
	private JCheckBox hasFullBorderCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
