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

import java.awt.*;
import java.beans.PropertyVetoException;
import javax.swing.*;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
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

		if( iconCheckBox.getState() == FlatTriStateCheckBox.State.SELECTED )
			internalFrame.setFrameIcon( new FlatFileViewFloppyDriveIcon() );
		else if( iconCheckBox.getState() == FlatTriStateCheckBox.State.UNSELECTED )
			internalFrame.setFrameIcon( null );

		if( menuBarCheckBox.isSelected() ) {
			JMenuBar menuBar = new JMenuBar();
			JMenu menu = new JMenu( "I'm a Menu Bar" );
			menu.add( new JMenuItem( "Menu Item" ) );
			menuBar.add( menu );
			internalFrame.setJMenuBar( menuBar );
		}

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

		if( minSizeCheckBox.isSelected() ) {
			internalFrame.setMinimumSize( new Dimension( 300, 150 ) );
			panel.add( new JLabel( "min 300,150" ) );
		}
		if( maxSizeCheckBox.isSelected() ) {
			internalFrame.setMaximumSize( new Dimension( 400, 200 ) );
			panel.add( new JLabel( "max 400,200" ) );
		}

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

	private void customDesktopManagerChanged() {
		desktopPane.setDesktopManager( customDesktopManagerCheckBox.isSelected()
			? new CustomDesktopManager()
			: null );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		desktopPane = new JDesktopPane();
		palette = new JInternalFrame();
		resizableCheckBox = new JCheckBox();
		closableCheckBox = new JCheckBox();
		iconifiableCheckBox = new JCheckBox();
		maximizableCheckBox = new JCheckBox();
		iconCheckBox = new FlatTriStateCheckBox();
		menuBarCheckBox = new JCheckBox();
		minSizeCheckBox = new JCheckBox();
		maxSizeCheckBox = new JCheckBox();
		titleLabel = new JLabel();
		titleField = new JTextField();
		createFrameButton = new JButton();
		panel1 = new JPanel();
		customDesktopManagerCheckBox = new JCheckBox();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[grow,fill]",
			// rows
			"[grow,fill]" +
			"[]"));

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

				//---- menuBarCheckBox ----
				menuBarCheckBox.setText("Menu Bar");
				paletteContentPane.add(menuBarCheckBox, "cell 1 2");

				//---- minSizeCheckBox ----
				minSizeCheckBox.setText("Minimum size 300,150");
				paletteContentPane.add(minSizeCheckBox, "cell 0 3 2 1,alignx left,growx 0");

				//---- maxSizeCheckBox ----
				maxSizeCheckBox.setText("Maximum size 400,200");
				paletteContentPane.add(maxSizeCheckBox, "cell 0 4 2 1,alignx left,growx 0");

				//---- titleLabel ----
				titleLabel.setText("Frame title:");
				paletteContentPane.add(titleLabel, "cell 0 5");
				paletteContentPane.add(titleField, "cell 1 5");

				//---- createFrameButton ----
				createFrameButton.setText("Create Frame");
				createFrameButton.addActionListener(e -> createInternalFrame());
				paletteContentPane.add(createFrameButton, "cell 1 6,alignx right,growx 0");
			}
			desktopPane.add(palette, JLayeredPane.PALETTE_LAYER);
			palette.setBounds(15, 25, 275, 275);
		}
		add(desktopPane, "cell 0 0,width 600,height 600");

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]"));

			//---- customDesktopManagerCheckBox ----
			customDesktopManagerCheckBox.setText("custom desktop manager");
			customDesktopManagerCheckBox.addActionListener(e -> customDesktopManagerChanged());
			panel1.add(customDesktopManagerCheckBox, "cell 0 0");
		}
		add(panel1, "cell 0 1");
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
	private FlatTriStateCheckBox iconCheckBox;
	private JCheckBox menuBarCheckBox;
	private JCheckBox minSizeCheckBox;
	private JCheckBox maxSizeCheckBox;
	private JLabel titleLabel;
	private JTextField titleField;
	private JButton createFrameButton;
	private JPanel panel1;
	private JCheckBox customDesktopManagerCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class CustomDesktopManager -----------------------------------------

	private static class CustomDesktopManager
		extends DefaultDesktopManager
	{
		@Override
		public void activateFrame( JInternalFrame f ) {
			System.out.println( "activateFrame: " + f.getTitle() );
			super.activateFrame( f );
		}

		@Override
		public void deactivateFrame( JInternalFrame f ) {
			System.out.println( "deactivateFrame: " + f.getTitle() );
			super.deactivateFrame( f );
		}

		@Override
		public void iconifyFrame( JInternalFrame f ) {
			System.out.println( "iconifyFrame: " + f.getTitle() );
			super.iconifyFrame( f );
		}

		@Override
		public void deiconifyFrame( JInternalFrame f ) {
			System.out.println( "deiconifyFrame: " + f.getTitle() );
			super.deiconifyFrame( f );
		}
	}
}
