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

package com.formdev.flatlaf.ui;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PasswordView;
import javax.swing.text.View;
import com.formdev.flatlaf.icons.FlatCapsLockIcon;
import com.formdev.flatlaf.ui.FlatStyleSupport.Styleable;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPasswordField}.
 *
 * <!-- BasicTextFieldUI -->
 *
 * @uiDefault PasswordField.font					Font
 * @uiDefault PasswordField.background				Color
 * @uiDefault PasswordField.foreground				Color	also used if not editable
 * @uiDefault PasswordField.caretForeground			Color
 * @uiDefault PasswordField.selectionBackground		Color
 * @uiDefault PasswordField.selectionForeground		Color
 * @uiDefault PasswordField.disabledBackground		Color	used if not enabled
 * @uiDefault PasswordField.inactiveBackground		Color	used if not editable
 * @uiDefault PasswordField.inactiveForeground		Color	used if not enabled (yes, this is confusing; this should be named disabledForeground)
 * @uiDefault PasswordField.border					Border
 * @uiDefault PasswordField.margin					Insets
 * @uiDefault PasswordField.caretBlinkRate			int		default is 500 milliseconds
 *
 * <!-- FlatTextFieldUI -->
 *
 * @uiDefault Component.minimumWidth				int
 * @uiDefault Component.isIntelliJTheme				boolean
 * @uiDefault PasswordField.placeholderForeground	Color
 * @uiDefault PasswordField.focusedBackground		Color	optional
 * @uiDefault TextComponent.selectAllOnFocusPolicy	String	never, once (default) or always
 * @uiDefault TextComponent.selectAllOnMouseClick	boolean
 *
 * <!-- FlatPasswordFieldUI -->
 *
 * @uiDefault PasswordField.echoChar				character
 * @uiDefault PasswordField.showCapsLock			boolean
 * @uiDefault PasswordField.capsLockIcon			Icon
 *
 * @author Karl Tauber
 */
public class FlatPasswordFieldUI
	extends FlatTextFieldUI
{
	@Styleable protected boolean showCapsLock;
	protected Icon capsLockIcon;

	private KeyListener capsLockListener;
	private boolean capsLockIconShared = true;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatPasswordFieldUI();
	}

	@Override
	protected String getPropertyPrefix() {
		return "PasswordField";
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		String prefix = getPropertyPrefix();
		Character echoChar = (Character) UIManager.get( prefix + ".echoChar" );
		if( echoChar != null )
			LookAndFeel.installProperty( getComponent(), "echoChar", echoChar );

		showCapsLock = UIManager.getBoolean( "PasswordField.showCapsLock" );
		capsLockIcon = UIManager.getIcon( "PasswordField.capsLockIcon" );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		capsLockIcon = null;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		// update caps lock indicator
		capsLockListener = new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				repaint( e );
			}
			@Override
			public void keyReleased( KeyEvent e ) {
				repaint( e );
			}
			private void repaint( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_CAPS_LOCK )
					e.getComponent().repaint();
			}
		};

		getComponent().addKeyListener( capsLockListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		getComponent().removeKeyListener( capsLockListener );
		capsLockListener = null;
	}

	@Override
	protected void installKeyboardActions() {
		super.installKeyboardActions();

		// map "select-word" action (double-click) to "select-line" action
		ActionMap map = SwingUtilities.getUIActionMap( getComponent() );
		if( map != null && map.get( DefaultEditorKit.selectWordAction ) != null ) {
			Action selectLineAction = map.get( DefaultEditorKit.selectLineAction );
			if( selectLineAction != null )
				map.put( DefaultEditorKit.selectWordAction, selectLineAction );
		}
	}

	@Override
	protected Object applyStyleProperty( String key, Object value ) {
		if( key.equals( "capsLockIconColor" ) && capsLockIcon instanceof FlatCapsLockIcon ) {
			if( capsLockIconShared ) {
				capsLockIcon = FlatStyleSupport.cloneIcon( capsLockIcon );
				capsLockIconShared = false;
			}
			return ((FlatCapsLockIcon)capsLockIcon).applyStyleProperty( key, value );
		}

		return super.applyStyleProperty( key, value );
	}

	@Override
	public View create( Element elem ) {
		return new PasswordView( elem );
	}

	@Override
	protected void paintSafely( Graphics g ) {
		// safe and restore clipping area because super.paintSafely() modifies it
		// and the caps lock icon would be truncated
		Shape oldClip = g.getClip();
		super.paintSafely( g );
		g.setClip( oldClip );

		paintCapsLock( g );
	}

	protected void paintCapsLock( Graphics g ) {
		if( !showCapsLock )
			return;

		JTextComponent c = getComponent();
		if( !FlatUIUtils.isPermanentFocusOwner( c ) ||
			!Toolkit.getDefaultToolkit().getLockingKeyState( KeyEvent.VK_CAPS_LOCK ) )
		  return;

		int y = (c.getHeight() - capsLockIcon.getIconHeight()) / 2;
		int x = c.getWidth() - capsLockIcon.getIconWidth() - y;
		capsLockIcon.paintIcon( c, g, x, y );
	}
}
