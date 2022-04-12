/*
 * Copyright 2021 FormDev Software GmbH
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

package com.formdev.flatlaf.testing.screenshots;

import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * Fills entire screen with white or black.
 * Use as background when tacking screenshots to avoid that light/dark background
 * shines through window border.
 *
 * @author Karl Tauber
 */
public class FlatScreenshotsBackground
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatLightLaf.setup();

			JFrame frame = new JFrame( "FlatScreenshotsBackground" );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

			// register ESC key to close frame
			((JComponent)frame.getContentPane()).registerKeyboardAction(
				e -> {
					System.exit( 0 );
				},
				KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ),
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

			JCheckBox black = new JCheckBox( "black" );
			black.setOpaque( false );
			black.setVerticalAlignment( SwingConstants.TOP );
			black.addActionListener( e -> {
				if( black.isSelected() )
					FlatDarkLaf.setup();
				else
					FlatLightLaf.setup();
				FlatLaf.updateUI();

				Color color = black.isSelected() ? Color.black : Color.white;
				frame.getContentPane().setBackground( color );
				frame.getRootPane().putClientProperty( FlatClientProperties.TITLE_BAR_BACKGROUND, color );
				frame.repaint();
			} );
			frame.getContentPane().add( black );

			frame.getContentPane().setBackground( Color.white );
			frame.getRootPane().putClientProperty( FlatClientProperties.TITLE_BAR_BACKGROUND, Color.white );

			frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
			frame.setVisible( true );
		} );
	}
}
