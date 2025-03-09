/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.demo;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * @author Karl Tauber
 */
public class DemoPrefs
{
	public static final String KEY_LAF_CLASS_NAME = "lafClassName";
	public static final String KEY_LAF_THEME_FILE = "lafThemeFile";
	public static final String KEY_SYSTEM_SCALE_FACTOR = "systemScaleFactor";
	public static final String KEY_LAF_SYNC = "lafSync";

	private static Preferences state;

	public static Preferences getState() {
		return state;
	}

	public static void init( String rootPath ) {
		state = Preferences.userRoot().node( rootPath );
	}

	public static void setupLaf( String[] args ) {
//		com.formdev.flatlaf.demo.intellijthemes.IJThemesDump.install();

		// set look and feel
		try {
			if( args.length > 0 )
				UIManager.setLookAndFeel( args[0] );
			else
				restoreLaf();
		} catch( Throwable ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );

			// fallback
			FlatLightLaf.setup();
		}

		// remember active look and feel
		UIManager.addPropertyChangeListener( e -> {
			if( "lookAndFeel".equals( e.getPropertyName() ) ) {
				state.put( KEY_LAF_CLASS_NAME, UIManager.getLookAndFeel().getClass().getName() );
				notifyLafChange();
			}
		} );
	}

	private static void restoreLaf()
		throws Exception
	{
		String lafClassName = state.get( KEY_LAF_CLASS_NAME, FlatLightLaf.class.getName() );
		if( FlatPropertiesLaf.class.getName().equals( lafClassName ) ||
			IntelliJTheme.ThemeLaf.class.getName().equals( lafClassName ) )
		{
			String themeFileName = state.get( KEY_LAF_THEME_FILE, "" );
			if( !themeFileName.isEmpty() ) {
				File themeFile = new File( themeFileName );

				if( themeFileName.endsWith( ".properties" ) ) {
					String themeName = StringUtils.removeTrailing( themeFile.getName(), ".properties" );
					FlatLaf.setup( new FlatPropertiesLaf( themeName, themeFile ) );
				} else
					FlatLaf.setup( IntelliJTheme.createLaf( new FileInputStream( themeFile ) ) );
			} else
				FlatLightLaf.setup();
		} else
			UIManager.setLookAndFeel( lafClassName );
	}

	public static void initSystemScale() {
		if( System.getProperty( "sun.java2d.uiScale" ) == null ) {
			String scaleFactor = state.get( KEY_SYSTEM_SCALE_FACTOR, null );
			if( scaleFactor != null ) {
				System.setProperty( "sun.java2d.uiScale", scaleFactor );

				System.out.println( "FlatLaf: setting 'sun.java2d.uiScale' to " + scaleFactor );
				System.out.println( "         use 'Alt+Shift+F1...12' to change it to 1x...4x" );
			}
		}
	}

	/**
	 * register Alt+Shift+F1, F2, ... F12 keys to change system scale factor
	 */
	public static void registerSystemScaleFactors( JFrame frame ) {
		registerSystemScaleFactor( frame, "alt shift F1", null );
		registerSystemScaleFactor( frame, "alt shift F2", "1" );

		if( SystemInfo.isWindows ) {
			registerSystemScaleFactor( frame, "alt shift F3", "1.25" );
			registerSystemScaleFactor( frame, "alt shift F4", "1.5" );
			registerSystemScaleFactor( frame, "alt shift F5", "1.75" );
			registerSystemScaleFactor( frame, "alt shift F6", "2" );
			registerSystemScaleFactor( frame, "alt shift F7", "2.25" );
			registerSystemScaleFactor( frame, "alt shift F8", "2.5" );
			registerSystemScaleFactor( frame, "alt shift F9", "2.75" );
			registerSystemScaleFactor( frame, "alt shift F10", "3" );
			registerSystemScaleFactor( frame, "alt shift F11", "3.5" );
			registerSystemScaleFactor( frame, "alt shift F12", "4" );
		} else {
			// Java on macOS and Linux supports only integer scale factors
			registerSystemScaleFactor( frame, "alt shift F3", "2" );
			registerSystemScaleFactor( frame, "alt shift F4", "3" );
			registerSystemScaleFactor( frame, "alt shift F5", "4" );

		}
	}

	private static void registerSystemScaleFactor( JFrame frame, String keyStrokeStr, String scaleFactor ) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke( keyStrokeStr );
		if( keyStroke == null )
			throw new IllegalArgumentException( "Invalid key stroke '" + keyStrokeStr + "'" );

		((JComponent)frame.getContentPane()).registerKeyboardAction(
			e -> applySystemScaleFactor( frame, scaleFactor ),
			keyStroke,
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	private static void applySystemScaleFactor( JFrame frame, String scaleFactor ) {
		if( JOptionPane.showConfirmDialog( frame,
				"Change system scale factor to "
				+ (scaleFactor != null ? scaleFactor : "default")
				+ " and exit?",
				frame.getTitle(), JOptionPane.YES_NO_OPTION ) != JOptionPane.YES_OPTION )
			return;

		if( scaleFactor != null )
			state.put( KEY_SYSTEM_SCALE_FACTOR, scaleFactor );
		else
			state.remove( KEY_SYSTEM_SCALE_FACTOR );

		System.exit( 0 );
	}

	//---- inter-process Laf change notification/synchronisation --------------

	// used for FlatLaf development when running multiple testing apps

	private static final String MULTICAST_ADDRESS = "224.63.31.41";
	private static final int MULTICAST_PORT = 36584;
	private static final long PROCESS_ID = System.nanoTime();

	private static Thread thread;
	private static boolean notifyEnabled = true;

	public static void initLafSync( JFrame frame ) {
		((JComponent)frame.getContentPane()).registerKeyboardAction(
			e -> enableDisableLafSync(),
			KeyStroke.getKeyStroke( "alt shift S" ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		if( state.getBoolean( KEY_LAF_SYNC, false ) ) {
			System.out.println( "FlatLaf: theme sync enabled; use Alt+Shift+S to disable it" );
			listenToLafChange();
		}
	}

	private static void enableDisableLafSync() {
		boolean enabled = !state.getBoolean( KEY_LAF_SYNC, false );
		state.putBoolean( KEY_LAF_SYNC, enabled );

		if( enabled )
			listenToLafChange();
		else if( thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	private static void listenToLafChange() {
		if( thread != null )
			return;

		thread = new Thread( "FlatLaf Laf change listener" ) {
			MulticastSocket socket;

			@Override
			public void run() {
				try( MulticastSocket socket = new MulticastSocket( MULTICAST_PORT ) ) {
					this.socket = socket;
					socket.joinGroup( InetAddress.getByName( MULTICAST_ADDRESS ) );

					byte[] buffer = new byte[Long.BYTES];
					for(;;) {
						DatagramPacket packet = new DatagramPacket( buffer, buffer.length );
						socket.receive( packet );

						long id = ByteBuffer.wrap( buffer ).getLong();
						if( id == PROCESS_ID )
							continue; // was sent from this process

						EventQueue.invokeLater( () -> {
							notifyEnabled = false;
							try {
								restoreLaf();
								FlatLaf.updateUI();
							} catch( Throwable ex ) {
								LoggingFacade.INSTANCE.logSevere( null, ex );
							} finally {
								notifyEnabled = true;
							}
						} );
					}
				} catch( IOException ex ) {
					if( ex instanceof SocketException && "Socket closed".equals( ex.getMessage() ) )
						return; // interrupted

					LoggingFacade.INSTANCE.logSevere( null, ex );
				}
			}

			@Override
			public void interrupt() {
				super.interrupt();
				socket.close();
			}
		};
		thread.setDaemon( true );
		thread.start();
	}

	private static void notifyLafChange() {
		if( thread == null || !notifyEnabled )
			return;

		EventQueue.invokeLater( () -> {
			try( MulticastSocket socket = new MulticastSocket() ) {
				InetAddress address = InetAddress.getByName( MULTICAST_ADDRESS );
				byte[] buffer = ByteBuffer.wrap( new byte[Long.BYTES] ).putLong( PROCESS_ID ).array();
				DatagramPacket packet = new DatagramPacket( buffer, buffer.length, address, MULTICAST_PORT );
				socket.send( packet );
			} catch( IOException ex ) {
				LoggingFacade.INSTANCE.logSevere( null, ex );
			}
		} );
	}
}
