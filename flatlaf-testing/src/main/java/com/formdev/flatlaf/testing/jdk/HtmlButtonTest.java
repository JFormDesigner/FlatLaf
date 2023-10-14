package com.formdev.flatlaf.testing.jdk;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * https://github.com/openjdk/jdk/pull/8407#issuecomment-1761583430
 */
public class HtmlButtonTest
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatLightLaf.setup();

			JFrame frame = new JFrame( "HTML Button Test" );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

			JPanel panel = new JPanel( new GridBagLayout() );
			panel.setBorder( new EmptyBorder( 20, 20, 20, 20 ) );

			createButtons( panel, "center", SwingConstants.CENTER, SwingConstants.CENTER, null );
			createButtons( panel, "left", SwingConstants.LEFT, SwingConstants.CENTER, null );
			createButtons( panel, "right", SwingConstants.RIGHT, SwingConstants.CENTER, null );

			createButtons( panel, "center with margin 30,4,4,4", SwingConstants.CENTER, SwingConstants.CENTER, new Insets( 30, 4, 4, 4 ) );
			createButtons( panel, "left with margin 30,4,4,4", SwingConstants.LEFT, SwingConstants.CENTER, new Insets( 30, 4, 4, 4 ) );
			createButtons( panel, "left/top with margin 30,4,4,4", SwingConstants.LEFT, SwingConstants.TOP, new Insets( 30, 4, 4, 4 ) );

			frame.add( new JLabel( "Java version " + System.getProperty( "java.version" ) ), BorderLayout.NORTH );
			frame.add( panel );
			frame.pack();
			frame.setVisible( true );
		} );
	}

	private static void createButtons( JPanel panel, String text, int horizontalAlignment, int verticalAlignment, Insets margin ) {
		JButton button = new JButton( text );
		button.setHorizontalAlignment( horizontalAlignment );
		button.setVerticalAlignment( verticalAlignment );
		if( margin != null )
			button.setMargin( margin );
		panel.add( button, new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 4, 4, 4, 4 ), 0, 0 ) );

		JButton htmlButton = new JButton( "<html>HTML " + text + "</html>" );
		htmlButton.setHorizontalAlignment( horizontalAlignment );
		htmlButton.setVerticalAlignment( verticalAlignment );
		if( margin != null )
			htmlButton.setMargin( margin );
		panel.add( htmlButton, new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 4, 4, 24, 4 ), 0, 0 ) );
	}
}