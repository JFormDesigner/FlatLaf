package com.formdev.flatlaf.testing.jideoss;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
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
        JPanel mainPanel = new JPanel();
        JLabel tabbedPaneLabel = new JLabel();
        JLabel horizontalLabel = new JLabel();
        horizontalRangeSlider = new RangeSlider();
        JLabel verticalLabel = new JLabel();
        verticalRangeSlider = new RangeSlider();
        JPanel configurationPanel = new JPanel();
        paintTick = new JCheckBox();
        paintLabel = new JCheckBox();

        //======== this ========
        setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax
        . swing. border. EmptyBorder( 0, 0, 0, 0) , "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn", javax. swing
        . border. TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM, new java .awt .
        Font ("Dia\u006cog" ,java .awt .Font .BOLD ,12 ), java. awt. Color. red
        ) , getBorder( )) );  addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override
        public void propertyChange (java .beans .PropertyChangeEvent e) {if ("\u0062ord\u0065r" .equals (e .getPropertyName (
        ) )) throw new RuntimeException( ); }} );
        setLayout(new MigLayout(
            "insets dialog,hidemode 3",
            // columns
            "[grow,fill]",
            // rows
            "[grow,fill]"));

        //======== mainPanel ========
        {
            mainPanel.setOpaque(false);
            mainPanel.setLayout(new FormLayout(
                "70dlu:grow, $lcgap, 70dlu:grow",
                "pref, 2*($lgap, fill:70dlu:grow), $lgap, pref"));

            //---- tabbedPaneLabel ----
            tabbedPaneLabel.setText("RangeSlider:");
            mainPanel.add(tabbedPaneLabel, CC.xy(1, 1));

            //---- horizontalLabel ----
            horizontalLabel.setText("Horizontal");
            mainPanel.add(horizontalLabel, CC.xy(1, 3));
            mainPanel.add(horizontalRangeSlider, CC.xy(3, 3));

            //---- verticalLabel ----
            verticalLabel.setText("Vertical");
            mainPanel.add(verticalLabel, CC.xy(1, 5));
            mainPanel.add(verticalRangeSlider, CC.xy(3, 5));

            //======== configurationPanel ========
            {
                configurationPanel.setOpaque(false);
                configurationPanel.setLayout(new MigLayout(
                    "insets 0,hidemode 3",
                    // columns
                    "[]" +
                    "[]" +
                    "[]",
                    // rows
                    "[center]"));

                //---- paintTick ----
                paintTick.setText("PaintTicks");
                paintTick.setMnemonic('T');
                paintTick.setSelected(true);
                paintTick.addActionListener(e -> paintTicks());
                configurationPanel.add(paintTick, "cell 0 0");

                //---- paintLabel ----
                paintLabel.setText("PaintLabels");
                paintLabel.setMnemonic('L');
                paintLabel.setSelected(true);
                paintLabel.addActionListener(e -> paintLabels());
                configurationPanel.add(paintLabel, "cell 2 0,alignx left,growx 0");
            }
            mainPanel.add(configurationPanel, CC.xywh(1, 7, 3, 1));
        }
        add(mainPanel, "cell 0 0");
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
    private RangeSlider horizontalRangeSlider;
    private RangeSlider verticalRangeSlider;
    private JCheckBox paintTick;
    private JCheckBox paintLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
