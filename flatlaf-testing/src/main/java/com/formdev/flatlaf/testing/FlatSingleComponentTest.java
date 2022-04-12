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

import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.demo.DemoPrefs;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;

/**
 * @author Karl Tauber
 */
public class FlatSingleComponentTest
	extends JFrame
{
	private static final String PREFS_ROOT_PATH = "/flatlaf-test-single";
	private static final String KEY_SCALE_FACTOR = "scaleFactor";

	private final JLabel infoLabel;

	private JComponent createSingleComponent() {
		return new JButton( "hello" );
	}

	public static void main( String[] args ) {
		DemoPrefs.init( PREFS_ROOT_PATH );

		// set scale factor
		if( System.getProperty( FlatSystemProperties.UI_SCALE ) == null ) {
			String scaleFactor = DemoPrefs.getState().get( KEY_SCALE_FACTOR, null );
			if( scaleFactor != null )
				System.setProperty( FlatSystemProperties.UI_SCALE, scaleFactor );
		}

		// install inspectors
		FlatInspector.install( "ctrl shift alt X" );
		FlatUIDefaultsInspector.install( "ctrl shift alt Y" );

		// disable animated Laf change
		System.setProperty( "flatlaf.animatedLafChange", "false" );

		// test loading custom defaults from package
		FlatLaf.registerCustomDefaultsSource( "com.formdev.flatlaf.testing.customdefaults" );

		// set look and feel
		DemoPrefs.setupLaf( args );

		// create and show frame
		new FlatSingleComponentTest();
	}

	private FlatSingleComponentTest() {
		super( "FlatSingleComponentTest" );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		JComponent c = createSingleComponent();

		Container contentPane = getContentPane();
		contentPane.setLayout( new MigLayout( null, null, "[][grow]") );
		contentPane.add( c );

		infoLabel = new JLabel();
		infoLabel.setEnabled( false );
		contentPane.add( infoLabel, "newline, aligny bottom,growy 0" );

		// register F1, F2, ... keys to switch to Light, Dark or other LaFs
		registerSwitchToLookAndFeel( "F1", FlatLightLaf.class.getName() );
		registerSwitchToLookAndFeel( "F2", FlatDarkLaf.class.getName() );
		registerSwitchToLookAndFeel( "F3", FlatIntelliJLaf.class.getName() );
		registerSwitchToLookAndFeel( "F4", FlatDarculaLaf.class.getName() );

		registerSwitchToLookAndFeel( "F8", FlatTestLaf.class.getName() );

		if( SystemInfo.isWindows )
			registerSwitchToLookAndFeel( "F9", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		else if( SystemInfo.isMacOS )
			registerSwitchToLookAndFeel( "F9", "com.apple.laf.AquaLookAndFeel" );
		else if( SystemInfo.isLinux )
			registerSwitchToLookAndFeel( "F9", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" );
		registerSwitchToLookAndFeel( "F12", MetalLookAndFeel.class.getName() );
		registerSwitchToLookAndFeel( "F11", NimbusLookAndFeel.class.getName() );

		// register Alt+F1, F2, ... keys to change scale factor
		registerScaleFactor( "alt F1", "1" );
		registerScaleFactor( "alt F2", "1.25" );
		registerScaleFactor( "alt F3", "1.5" );
		registerScaleFactor( "alt F4", "1.75" );
		registerScaleFactor( "alt F5", "2" );
		registerScaleFactor( "alt F6", "2.5" );
		registerScaleFactor( "alt F7", "3" );
		registerScaleFactor( "alt F8", "3.5" );
		registerScaleFactor( "alt F9", "4" );
		registerScaleFactor( "alt F10", "5" );
		registerScaleFactor( "alt F11", "6" );
		registerScaleFactor( "alt F12", null );

		// register Alt+R key to toggle component orientation
		((JComponent)getContentPane()).registerKeyboardAction(
			e -> {
				applyComponentOrientation( getComponentOrientation().isLeftToRight()
					? ComponentOrientation.RIGHT_TO_LEFT
					: ComponentOrientation.LEFT_TO_RIGHT );
				revalidate();
				repaint();
			},
			KeyStroke.getKeyStroke( "alt R" ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		// register ESC key to close frame
		((JComponent)getContentPane()).registerKeyboardAction(
			e -> {
				dispose();
			},
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		// update info
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowOpened( WindowEvent e ) {
				updateInfo();
			}
		} );

		// update info when moved to another screen
		addComponentListener( new ComponentAdapter() {
			@Override
			public void componentMoved( ComponentEvent e ) {
				updateInfo();
			}
		} );

		UIManager.addPropertyChangeListener( e -> {
			if( "lookAndFeel".equals( e.getPropertyName() ) ) {
				EventQueue.invokeLater( () -> {
					// update info because user scale factor may change
					updateInfo();
				} );
			}
		} );

		UIScale.addPropertyChangeListener( e -> {
			// update info because user scale factor may change
			updateInfo();
		} );

		setMinimumSize( UIScale.scale( new Dimension( 300, 150 ) ) );
		pack();
		setLocationRelativeTo( null );
		setVisible( true );
	}

	private void updateInfo() {
		double systemScaleFactor = UIScale.getSystemScaleFactor( getGraphicsConfiguration() );
		float userScaleFactor = UIScale.getUserScaleFactor();
		infoLabel.setText( " (Java " + System.getProperty( "java.version" )
			+ (systemScaleFactor != 1 ? (";  system scale factor " + systemScaleFactor) : "")
			+ (userScaleFactor != 1 ? (";  user scale factor " + userScaleFactor) : "")
			+ (systemScaleFactor == 1 && userScaleFactor == 1 ? "; no scaling" : "")
			+ ")" );
	}

	private void registerSwitchToLookAndFeel( String keyStrokeStr, String lafClassName ) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke( keyStrokeStr );
		if( keyStroke == null )
			throw new IllegalArgumentException( "Invalid key stroke '" + keyStrokeStr + "'" );

		((JComponent)getContentPane()).registerKeyboardAction(
			e -> {
				applyLookAndFeel( lafClassName );
			},
			keyStroke,
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	private void applyLookAndFeel( String lafClassName ) {
		try {
			UIManager.setLookAndFeel( lafClassName );
			FlatLaf.updateUI();
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	private void registerScaleFactor( String keyStrokeStr, String scaleFactor ) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke( keyStrokeStr );
		if( keyStroke == null )
			throw new IllegalArgumentException( "Invalid key stroke '" + keyStrokeStr + "'" );

		((JComponent)getContentPane()).registerKeyboardAction(
			e -> {
				applyScaleFactor( scaleFactor );
			},
			keyStroke,
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	private void applyScaleFactor( String scaleFactor ) {
		if( scaleFactor != null ) {
			System.setProperty( FlatSystemProperties.UI_SCALE, scaleFactor );
			DemoPrefs.getState().put( KEY_SCALE_FACTOR, scaleFactor );
		} else {
			System.clearProperty( FlatSystemProperties.UI_SCALE );
			DemoPrefs.getState().remove( KEY_SCALE_FACTOR );
		}

		applyLookAndFeel( UIManager.getLookAndFeel().getClass().getName() );
		pack();
	}
}
