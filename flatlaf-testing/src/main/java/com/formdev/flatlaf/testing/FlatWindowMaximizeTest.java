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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;

/**
 * @author Karl Tauber
 */
public class FlatWindowMaximizeTest
{
	public static void main( String[] args ) {
		System.out.println( "Java version: " + System.getProperty( "java.version" ) );

		// Windows Laf
		try {
			UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
		testMaximize( JFrame.MAXIMIZED_BOTH );
//		testMaximize( JFrame.ICONIFIED );
//		testMaximize( JFrame.ICONIFIED | JFrame.MAXIMIZED_BOTH );
//		testMaximize( JFrame.NORMAL );

		// FlatLaf
		FlatDarkLaf.setup();
		testMaximize( JFrame.MAXIMIZED_BOTH );
//		testMaximize( JFrame.ICONIFIED );
//		testMaximize( JFrame.ICONIFIED | JFrame.MAXIMIZED_BOTH );
//		testMaximize( JFrame.NORMAL );

		System.exit( 0 );
	}

	private static void testMaximize( int state ) {
		System.out.println();
		System.out.println( "---- " + state + " - " + UIManager.getLookAndFeel().getClass().getSimpleName() + " ----" );

		// only maximize
		testMaximize( "MAX", state, frame -> {
			frame.setExtendedState( state );
		} );

		// pack/size before maximize
		testMaximize( "pack MAX", state, frame -> {
			frame.pack();
			frame.setExtendedState( state );
		} );
		testMaximize( "size MAX", state, frame -> {
			frame.setSize( 1000, 500 );
			frame.setExtendedState( state );
		} );

		// pack/size after maximize
		testMaximize( "MAX pack", state, frame -> {
			frame.setExtendedState( state );
			frame.pack();
		} );
		testMaximize( "MAX size", state, frame -> {
			frame.setExtendedState( state );
			frame.setSize( 1000, 500 );
		} );

		// pack and size before maximize
		testMaximize( "pack size MAX", state, frame -> {
			frame.pack();
			frame.setSize( 1000, 500 );
			frame.setExtendedState( state );
		} );
		testMaximize( "size pack MAX", state, frame -> {
			frame.setSize( 1000, 500 );
			frame.pack();
			frame.setExtendedState( state );
		} );

		// pack/size before maximize and size/pack after maximize
		testMaximize( "pack MAX size", state, frame -> {
			frame.pack();
			frame.setExtendedState( state );
			frame.setSize( 1000, 500 );
		} );
		testMaximize( "size MAX pack", state, frame -> {
			frame.setSize( 1000, 500 );
			frame.setExtendedState( state );
			frame.pack();
		} );

		// pack and size after maximize
		testMaximize( "MAX size pack", state, frame -> {
			frame.setExtendedState( state );
			frame.setSize( 1000, 500 );
			frame.pack();
		} );
		testMaximize( "MAX pack size", state, frame -> {
			frame.setExtendedState( state );
			frame.pack();
			frame.setSize( 1000, 500 );
		} );

		// 1. create invisible frame
		// 2. create dialog with invisible frame as owner
		// 3. pack dialog, which invokes frame.addNotify()
		// 4. show frame
		testMaximize( "MAX dialog.pack", state, true, frame -> {
			frame.setExtendedState( state );

			JDialog dialog = new JDialog( frame );
			dialog.pack(); // this invokes frame.addNotify()
		} );
	}

	private static void testMaximize( String msg, int expectedState, Consumer<JFrame> testFunc ) {
		testMaximize( msg, expectedState, false, testFunc );
	}

	private static void testMaximize( String msg, int expectedState, boolean showLater, Consumer<JFrame> testFunc ) {
		JFrame[] pFrame = new JFrame[1];
		EventQueue.invokeLater( () -> {
			JFrame frame = new JFrame( "test" );
			frame.setName( msg );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

//			addWindowListener( frame );
//			addComponentListener( frame );

			((JComponent) frame.getContentPane()).registerKeyboardAction( e -> {
				System.exit( 0 );
			}, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

			JButton button = new JButton( msg );
			button.setName( "button - " + msg );
//			addComponentListener( button );
			frame.add( button );
			frame.setLocation( 100, 50 );
			testFunc.accept( frame );
			if( !showLater )
				frame.setVisible( true );
			pFrame[0] = frame;
		} );

		try {
			if( showLater ) {
				Thread.sleep( 500 );

				EventQueue.invokeLater( () -> {
					pFrame[0].setVisible( true );
				} );
			}

			Thread.sleep( 500 );

			EventQueue.invokeAndWait( () -> {
				int state = pFrame[0].getExtendedState();
				System.out.printf( "    %-15s: %d  %s\n", msg, state, (state != expectedState ? "  FAILED" : "") );
			} );
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings( "unused" )
	private static void addWindowListener( JFrame frame ) {
		frame.addWindowListener( new WindowListener() {
			@Override
			public void windowOpened( WindowEvent e ) {
				print( "windowOpened", e.getWindow().getName() );
			}

			@Override
			public void windowClosing( WindowEvent e ) {
				print( "windowClosing", e.getWindow().getName() );
			}

			@Override
			public void windowClosed( WindowEvent e ) {
				print( "windowClosed", e.getWindow().getName() );
			}

			@Override
			public void windowIconified( WindowEvent e ) {
				print( "windowIconified", e.getWindow().getName() );
			}

			@Override
			public void windowDeiconified( WindowEvent e ) {
				print( "windowDeiconified", e.getWindow().getName() );
			}

			@Override
			public void windowActivated( WindowEvent e ) {
				print( "windowActivated", e.getWindow().getName() );
			}

			@Override
			public void windowDeactivated( WindowEvent e ) {
				print( "windowDeactivated", e.getWindow().getName() );
			}
		} );

		frame.addWindowStateListener( e -> {
			print( "windowStateChanged", e.getOldState() + " -> " + e.getNewState() + "   " + e.getWindow().getName() );
		} );
	}

	@SuppressWarnings( "unused" )
	private static void addComponentListener( Component comp ) {
		comp.addComponentListener( new ComponentListener() {
			@Override
			public void componentResized( ComponentEvent e ) {
				Component c = e.getComponent();
				print( "componentResized", c.getName() + "   " + c.getWidth() + "," + c.getHeight() );
			}

			@Override
			public void componentMoved( ComponentEvent e ) {
				Component c = e.getComponent();
				print( "componentMoved", e.getComponent().getName() + "   " + c.getX() + "," + c.getY() );
			}

			@Override
			public void componentShown( ComponentEvent e ) {
				print( "componentShown", e.getComponent().getName() );
			}

			@Override
			public void componentHidden( ComponentEvent e ) {
				print( "componentHidden", e.getComponent().getName() );
			}
		} );
	}

	private static void print( String key, String value ) {
		System.out.printf( "        %-20s %s\n", key, value );
	}
}
