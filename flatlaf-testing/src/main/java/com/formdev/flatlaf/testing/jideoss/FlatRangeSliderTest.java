package com.formdev.flatlaf.testing.jideoss;

import java.awt.event.*;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.*;
import com.formdev.flatlaf.testing.FlatTestFrame;
import com.formdev.flatlaf.testing.FlatTestPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.RangeSlider;
import net.miginfocom.swing.MigLayout;

public class FlatRangeSliderTest
	extends FlatTestPanel
{

	private RangeSlider horizontalRangeSlider;
	private RangeSlider verticalRangeSlider;

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

	private void paintLabels() {
		horizontalRangeSlider.setPaintLabels( paintLabel.isSelected() );
		verticalRangeSlider.setPaintLabels( paintLabel.isSelected() );
	}

	private void paintTicks() {
		horizontalRangeSlider.setPaintTicks( paintTick.isSelected() );
		verticalRangeSlider.setPaintTicks( paintTick.isSelected() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        JPanel panel9 = new JPanel();
        JLabel tabbedPaneLabel = new JLabel();
        JLabel label1 = new JLabel();
        horizontalRangeSlider = new RangeSlider();
        JLabel label2 = new JLabel();
        verticalRangeSlider = new RangeSlider();
        JPanel panel14 = new JPanel();
        paintTick = new JCheckBox();
        paintLabel = new JCheckBox();

        //======== this ========
        setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0
        ,0,0,0), "JF\u006frm\u0044es\u0069gn\u0065r \u0045va\u006cua\u0074io\u006e",javax.swing.border.TitledBorder.CENTER,javax.swing.border.TitledBorder.BOTTOM
        ,new java.awt.Font("D\u0069al\u006fg",java.awt.Font.BOLD,12),java.awt.Color.red),
         getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){@Override public void propertyChange(java.beans.PropertyChangeEvent e
        ){if("\u0062or\u0064er".equals(e.getPropertyName()))throw new RuntimeException();}});
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
                "pref, 2*($lgap, fill:70dlu:grow), $lgap, pref"));

            //---- tabbedPaneLabel ----
            tabbedPaneLabel.setText("RangeSlider:");
            panel9.add(tabbedPaneLabel, CC.xy(1, 1));

            //---- label1 ----
            label1.setText("Horizontal");
            panel9.add(label1, CC.xy(1, 3));
            panel9.add(horizontalRangeSlider, CC.xy(3, 3));

            //---- label2 ----
            label2.setText("Vertical");
            panel9.add(label2, CC.xy(1, 5));
            panel9.add(verticalRangeSlider, CC.xy(3, 5));

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

                //---- paintTick ----
                paintTick.setText("PaintTicks");
                paintTick.setMnemonic('M');
                paintTick.addActionListener(e -> paintTicks());
                panel14.add(paintTick, "cell 0 0");

                //---- paintLabel ----
                paintLabel.setText("PaintLabels");
                paintLabel.setMnemonic('F');
                paintLabel.addActionListener(e -> paintLabels());
                panel14.add(paintLabel, "cell 2 0,alignx left,growx 0");
            }
            panel9.add(panel14, CC.xywh(1, 7, 3, 1));
        }
        add(panel9, "cell 0 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		horizontalRangeSlider.setOrientation( SwingConstants.HORIZONTAL );
		horizontalRangeSlider.setMinimum( 0 );
		horizontalRangeSlider.setMaximum( 100 );
		horizontalRangeSlider.setLowValue( 10 );
		horizontalRangeSlider.setHighValue( 90 );
		horizontalRangeSlider.setLabelTable( horizontalRangeSlider.createStandardLabels( 10 ) );
		horizontalRangeSlider.setMinorTickSpacing( 5 );
		horizontalRangeSlider.setMajorTickSpacing( 10 );
		horizontalRangeSlider.setPaintTicks( true );
		horizontalRangeSlider.setPaintLabels( true );

		verticalRangeSlider.setOrientation( SwingConstants.VERTICAL );
		verticalRangeSlider.setMinimum( 0 );
		verticalRangeSlider.setMaximum( 100 );
		verticalRangeSlider.setLowValue( 10 );
		verticalRangeSlider.setHighValue( 90 );
		verticalRangeSlider.setLabelTable( horizontalRangeSlider.createStandardLabels( 10 ) );
		verticalRangeSlider.setMinorTickSpacing( 5 );
		verticalRangeSlider.setMajorTickSpacing( 10 );
		verticalRangeSlider.setPaintTicks( true );
		verticalRangeSlider.setPaintLabels( true );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JCheckBox paintTick;
    private JCheckBox paintLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
