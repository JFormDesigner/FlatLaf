package com.formdev.flatlaf.testing;

import java.awt.FlowLayout;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;

public class SwingFrameStateTest
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatLightLaf.setup();

			if( SystemInfo.isLinux )
				JFrame.setDefaultLookAndFeelDecorated( true );

			JFrame frame = new JFrame( "SwingFrameStateTest" );

			JButton restoreButton = new JButton( "Restore" );
			JButton vertButton = new JButton( "Max. Vert." );
			JButton horizButton = new JButton( "Max. Horiz." );
			JButton bothButton = new JButton( "Max. Both" );
			restoreButton.addActionListener( e -> frame.setExtendedState( 0 ) );
			vertButton.addActionListener( e -> frame.setExtendedState( Frame.MAXIMIZED_VERT ) );
			horizButton.addActionListener( e -> frame.setExtendedState( Frame.MAXIMIZED_HORIZ ) );
			bothButton.addActionListener( e -> frame.setExtendedState( Frame.MAXIMIZED_BOTH ) );

			JPanel panel = new JPanel( new FlowLayout() );
			panel.add( restoreButton );
			panel.add( vertButton );
			panel.add( horizButton );
			panel.add( bothButton );

			JLabel stateInfo = new JLabel();
			frame.addWindowStateListener( e -> {
				int state = frame.getExtendedState();
				stateInfo.setText( " state "
					+ ((state & Frame.MAXIMIZED_VERT) != 0 ? "VERT " : "")
					+ ((state & Frame.MAXIMIZED_HORIZ) != 0 ? "HORIZ " : "")
				);
			} );
			panel.add( stateInfo );

			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.getContentPane().add( panel );
			frame.setSize( 700, 300 );
			frame.setLocationRelativeTo( null );
			frame.setVisible( true );
		} );
	}
}
