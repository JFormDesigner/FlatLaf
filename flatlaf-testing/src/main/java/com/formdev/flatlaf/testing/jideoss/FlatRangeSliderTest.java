package com.formdev.flatlaf.testing.jideoss;

import com.formdev.flatlaf.testing.FlatTestFrame;
import com.formdev.flatlaf.testing.FlatTestPanel;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.*;
import com.jidesoft.swing.JideTabbedPane;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_HAS_FULL_BORDER;

public class FlatRangeSliderTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatRangeSliderTest" );
			LookAndFeelFactory.installJideExtension();
			frame.showFrame( FlatRangeSliderTest::new );

			UIManager.addPropertyChangeListener( e -> {
				if( "lookAndFeel".equals( e.getPropertyName() ) ) {
					LookAndFeelFactory.installJideExtension();
				}
			} );
		} );
	}

	FlatRangeSliderTest() {
		initComponents();
	}


	private void hasFullBorderChanged() {
		Boolean hasFullBorder = hasFullBorderCheckBox.isSelected() ? true : null;
//		tabbedPane1.putClientProperty( TABBED_PANE_HAS_FULL_BORDER, hasFullBorder );
	}

	private void moreTabsChanged() {
		boolean moreTabs = moreTabsCheckBox.isSelected();
//		addRemoveMoreTabs( tabbedPane1, moreTabs );
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
        // Generated using JFormDesigner Evaluation license - unknown
        JPanel panel9 = new JPanel();
        JLabel tabbedPaneLabel = new JLabel();
        RangeSlider rangeSlider1 = new RangeSlider();
        JPanel panel14 = new JPanel();
        moreTabsCheckBox = new JCheckBox();
        tabScrollCheckBox = new JCheckBox();
        hasFullBorderCheckBox = new JCheckBox();
        JPanel panel10 = new JPanel();
        JLabel jidePopupLabel = new JLabel();
        JButton showJidePopupButton = new JButton();

        //======== this ========
        setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border. EmptyBorder
        ( 0, 0, 0, 0) , "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn", javax. swing. border. TitledBorder. CENTER, javax. swing. border
        . TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font .BOLD ,12 ), java. awt
        . Color. red) , getBorder( )) );  addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void
        propertyChange (java .beans .PropertyChangeEvent e) {if ("\u0062ord\u0065r" .equals (e .getPropertyName () )) throw new RuntimeException( )
        ; }} );
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
            tabbedPaneLabel.setText("RangeSlider:");
            panel9.add(tabbedPaneLabel, CC.xy(1, 1));
            panel9.add(rangeSlider1, CC.xy(1, 3));

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
            panel9.add(panel14, CC.xywh(1, 7, 3, 1));

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
            panel9.add(panel10, CC.xy(1, 9));
        }
        add(panel9, "cell 0 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	private void tabScrollChanged() {

	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JCheckBox moreTabsCheckBox;
    private JCheckBox tabScrollCheckBox;
    private JCheckBox hasFullBorderCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
