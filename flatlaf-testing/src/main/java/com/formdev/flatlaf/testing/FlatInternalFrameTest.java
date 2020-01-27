/*
 * Created by JFormDesigner on Tue Aug 27 21:47:02 CEST 2019
 */

package com.formdev.flatlaf.testing;

import java.awt.*;
import java.beans.PropertyVetoException;
import javax.swing.*;
import com.formdev.flatlaf.icons.FlatFileViewFloppyDriveIcon;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatInternalFrameTest
	extends FlatTestPanel
{
	private static final int GAP = 20;

	private final int frameX;
	private final int frameY;
	private int frameCount = 0;

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatInternalFrameTest" );
			frame.showFrame( FlatInternalFrameTest::new );
		} );
	}

	public FlatInternalFrameTest() {
		initComponents();

		frameX = palette.getX() + palette.getWidth() + UIScale.scale( GAP );
		frameY = UIScale.scale( GAP );

		createInternalFrame();
	}

	private void createInternalFrame() {
		String title = titleField.getText();
		if( title.isEmpty() )
			title = "Frame " + (frameCount + 1);

		JInternalFrame internalFrame = new JInternalFrame( title,
			resizableCheckBox.isSelected(),
			closableCheckBox.isSelected(),
			maximizableCheckBox.isSelected(),
			iconifiableCheckBox.isSelected() );

		if( iconCheckBox.isSelected() )
			internalFrame.setFrameIcon( new FlatFileViewFloppyDriveIcon() );

		JPanel panel = new JPanel() {
			private final Color color = new Color( (int) (Math.random() * 0xffffff) | 0x20000000, true );

			@Override
			protected void paintComponent( Graphics g ) {
				super.paintComponent( g );

				g.setColor( color );
				g.fillRect( 20, 20, getWidth() - 40, getHeight() - 40 );
			}
		};
		internalFrame.setContentPane( panel );

		if( !palette.getComponentOrientation().isLeftToRight() )
			internalFrame.setComponentOrientation( ComponentOrientation.RIGHT_TO_LEFT );

		internalFrame.setBounds( frameX + UIScale.scale( GAP ) * (frameCount % 10),
			frameY + UIScale.scale( GAP ) * (frameCount % 10), UIScale.scale( 200 ), UIScale.scale( 200 ) );
		desktopPane.add( internalFrame, JLayeredPane.DEFAULT_LAYER );

		try {
			internalFrame.setSelected( true );
		} catch( PropertyVetoException ex ) {
			// ignore
		}

		internalFrame.show();

		frameCount++;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		desktopPane = new JDesktopPane();
		palette = new JInternalFrame();
		resizableCheckBox = new JCheckBox();
		closableCheckBox = new JCheckBox();
		iconifiableCheckBox = new JCheckBox();
		maximizableCheckBox = new JCheckBox();
		iconCheckBox = new JCheckBox();
		titleLabel = new JLabel();
		titleField = new JTextField();
		createFrameButton = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[grow,fill]",
			// rows
			"[grow,fill]"));

		//======== desktopPane ========
		{

			//======== palette ========
			{
				palette.setVisible(true);
				palette.setTitle("Internal Frame Generator");
				palette.setResizable(true);
				palette.putClientProperty("JInternalFrame.isPalette", true);
				palette.setIconifiable(true);
				Container paletteContentPane = palette.getContentPane();
				paletteContentPane.setLayout(new MigLayout(
					"hidemode 3",
					// columns
					"[fill]" +
					"[fill]",
					// rows
					"[fill]0" +
					"[]0" +
					"[]0" +
					"[]unrel" +
					"[]unrel"));

				//---- resizableCheckBox ----
				resizableCheckBox.setText("Resizable");
				resizableCheckBox.setSelected(true);
				paletteContentPane.add(resizableCheckBox, "cell 0 0,alignx left,growx 0");

				//---- closableCheckBox ----
				closableCheckBox.setText("Closable");
				closableCheckBox.setSelected(true);
				paletteContentPane.add(closableCheckBox, "cell 1 0,alignx left,growx 0");

				//---- iconifiableCheckBox ----
				iconifiableCheckBox.setText("Iconifiable");
				iconifiableCheckBox.setSelected(true);
				paletteContentPane.add(iconifiableCheckBox, "cell 0 1,alignx left,growx 0");

				//---- maximizableCheckBox ----
				maximizableCheckBox.setText("Maximizable");
				maximizableCheckBox.setSelected(true);
				paletteContentPane.add(maximizableCheckBox, "cell 1 1,alignx left,growx 0");

				//---- iconCheckBox ----
				iconCheckBox.setText("Frame icon");
				paletteContentPane.add(iconCheckBox, "cell 0 2");

				//---- titleLabel ----
				titleLabel.setText("Frame title:");
				paletteContentPane.add(titleLabel, "cell 0 3");
				paletteContentPane.add(titleField, "cell 1 3");

				//---- createFrameButton ----
				createFrameButton.setText("Create Frame");
				createFrameButton.addActionListener(e -> createInternalFrame());
				paletteContentPane.add(createFrameButton, "cell 1 4,alignx right,growx 0");
			}
			desktopPane.add(palette, JLayeredPane.PALETTE_LAYER);
			palette.setBounds(15, 25, 220, 185);
		}
		add(desktopPane, "cell 0 0,width 600,height 600");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		if( UIScale.getUserScaleFactor() > 1 )
			palette.setSize( UIScale.scale( palette.getSize() ) );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JDesktopPane desktopPane;
	private JInternalFrame palette;
	private JCheckBox resizableCheckBox;
	private JCheckBox closableCheckBox;
	private JCheckBox iconifiableCheckBox;
	private JCheckBox maximizableCheckBox;
	private JCheckBox iconCheckBox;
	private JLabel titleLabel;
	private JTextField titleField;
	private JButton createFrameButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
