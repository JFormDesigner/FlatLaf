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

package com.formdev.flatlaf.testing.modular.app;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;

/**
 * @author Karl Tauber
 */
public class FlatModularAppTest
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatInterFont.installBasic();

			FlatLaf.registerCustomDefaultsSource(
				FlatModularAppTest.class.getResource( "/com/formdev/flatlaf/testing/modular/app/themes/" ) );
			FlatLightLaf.setup();

			JButton button1 = new JButton( "Hello" );
			JButton button2 = new JButton( "World" );
			JScrollBar scrollBar = new JScrollBar();
			scrollBar.putClientProperty( FlatClientProperties.STYLE, "track: #0f0" );

			button1.setIcon( new FlatSVGIcon(
				FlatModularAppTest.class.getResource( "/com/formdev/flatlaf/testing/modular/app/icons/copy.svg" ) ) );

			JPanel panel = new JPanel();
			panel.add( new JLabel( "Hello World" ) );
			panel.add( button1 );
			panel.add( button2 );
			panel.add( scrollBar );

			JFrame frame = new JFrame( "FlatModularAppTest" );
			frame.setIconImages( FlatSVGUtils.createWindowIconImages(
				FlatModularAppTest.class.getResource( "/com/formdev/flatlaf/testing/modular/app/icons/copy.svg" ) ) );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.getContentPane().add( panel );
			frame.pack();
			frame.setLocationRelativeTo( null );
			frame.setVisible( true );
		} );
	}
}
