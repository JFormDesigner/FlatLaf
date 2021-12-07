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

package com.formdev.flatlaf.testing;

import java.awt.Container;
import java.awt.FlowLayout;
import java.util.Random;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * @author Karl Tauber
 */
public class FlatStressTest
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatLightLaf.setup();
			new FlatStressTest();
		} );
	}

	protected FlatStressTest() {
		JFrame frame = new JFrame( "FlatStressTest" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		Container contentPane = frame.getContentPane();
		contentPane.setLayout( new FlowLayout() );

		contentPane.add( createStressTest() );

		frame.setSize( 800, 600 );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}

	private JComponent createStressTest() {
		return createComboBoxStressTest();
	}

	// for https://github.com/JFormDesigner/FlatLaf/issues/432
	// simulates StackOverflowError in FlatComboBoxUI when doing stuff in various threads
	//
	// requires adding `Thread.sleep( 1 );` to `FlatComboBoxUI.CellPaddingBorder.install()`
	// after invocation of `uninstall()`
	private JComponent createComboBoxStressTest() {
		Random random = new Random();

		JComboBox<String> comboBox = new JComboBox<>();
		comboBox.putClientProperty( FlatClientProperties.MINIMUM_WIDTH, 0 );
		for( int i = 0; i < 100; i++ )
			comboBox.addItem( Integer.toString( random.nextInt() ) );

		Thread thread = new Thread( () -> {
			for(;;) {
				comboBox.setSelectedIndex( random.nextInt( comboBox.getItemCount() ) );
				comboBox.putClientProperty( FlatClientProperties.MINIMUM_WIDTH, random.nextInt( 500 ) );
			}
		});
		thread.setDaemon( true );
		thread.start();

		return comboBox;
	}
}
