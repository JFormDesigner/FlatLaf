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

package com.formdev.flatlaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.ref.WeakReference;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Show/hide mnemonics.
 *
 * @author Karl Tauber
 */
class MnemonicHandler
	implements KeyEventPostProcessor, ChangeListener
{
	private static boolean showMnemonics;
	private static WeakReference<Window> lastShowMnemonicWindow;
	private static WindowListener windowListener;

	static boolean isShowMnemonics() {
		return showMnemonics || !UIManager.getBoolean( "Component.hideMnemonics" );
	}

	void install() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor( this );
		MenuSelectionManager.defaultManager().addChangeListener( this );
	}

	void uninstall() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor( this );
		MenuSelectionManager.defaultManager().removeChangeListener( this );
	}

	@Override
	public boolean postProcessKeyEvent( KeyEvent e ) {
		int keyCode = e.getKeyCode();
		if( SystemInfo.IS_MAC ) {
			// Ctrl+Alt keys must be pressed on Mac
			if( keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT )
				showMnemonics( shouldShowMnemonics( e ) && e.isControlDown() && e.isAltDown(), e.getComponent() );
		} else {
			// Alt key must be pressed on Windows and Linux
			if( keyCode == KeyEvent.VK_ALT )
				showMnemonics( shouldShowMnemonics( e ), e.getComponent() );
		}

		return false;
	}

	private boolean shouldShowMnemonics( KeyEvent e ) {
		return e.getID() == KeyEvent.KEY_PRESSED ||
			MenuSelectionManager.defaultManager().getSelectedPath().length > 0;
	}

	@Override
	public void stateChanged( ChangeEvent e ) {
		MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
		if( selectedPath.length > 0 ) {
			// show mnemonics when a menu item is selected
			showMnemonics( true, (Component) selectedPath[0] );
		} else {
			// hide mnemonics when menu selection was canceled
			showMnemonics( false, null );
		}
	}

	private void showMnemonics( boolean show, Component c ) {
		if( show == showMnemonics )
			return;

		showMnemonics = show;

		// check whether it is necessary to repaint
		if( !UIManager.getBoolean( "Component.hideMnemonics" ) )
			return;

		if( show ) {
			// get root pane
			JRootPane rootPane = SwingUtilities.getRootPane( c );
			if( rootPane == null )
				return;

			// get window
			Window window = SwingUtilities.getWindowAncestor( rootPane );
			if( window == null )
				return;

			// repaint components with mnemonics in focused window
			repaintMnemonics( window );

			// hide mnemonics if window is deactivated (e.g. Alt+Tab to another window)
			windowListener = new WindowAdapter() {
				@Override
				public void windowDeactivated( WindowEvent e ) {
					// use invokeLater() to avoid that the listener is removed
					// while the listener queue is iterated to fire this event
					EventQueue.invokeLater( () -> {
						showMnemonics( false, c );
					} );
				}
			};
			window.addWindowListener( windowListener );

			lastShowMnemonicWindow = new WeakReference<>( window );
		} else if( lastShowMnemonicWindow != null ) {
			Window window = lastShowMnemonicWindow.get();
			if( window != null ) {
				repaintMnemonics( window );

				if( windowListener != null ) {
					window.removeWindowListener( windowListener );
					windowListener = null;
				}
			}

			lastShowMnemonicWindow = null;
		}
	}

	private void repaintMnemonics( Container container ) {
		for( Component c : container.getComponents() ) {
			if( !c.isVisible() )
				continue;

			if( hasMnemonic( c ) )
				c.repaint();

			if( c instanceof Container )
				repaintMnemonics( (Container) c );
		}
	}

	private boolean hasMnemonic( Component c ) {
		if( c instanceof JLabel && ((JLabel)c).getDisplayedMnemonicIndex() >= 0 )
			return true;

		if( c instanceof AbstractButton && ((AbstractButton)c).getDisplayedMnemonicIndex() >= 0 )
			return true;

		if( c instanceof JTabbedPane ) {
			JTabbedPane tabPane = (JTabbedPane) c;
			int tabCount = tabPane.getTabCount();
			for( int i = 0; i < tabCount; i++ ) {
				if( tabPane.getDisplayedMnemonicIndexAt( i ) >= 0 )
					return true;
			}
		}

		return false;
	}
}
