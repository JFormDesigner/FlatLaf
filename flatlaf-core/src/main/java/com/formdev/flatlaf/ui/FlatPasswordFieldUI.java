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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JToggleButton;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PasswordView;
import javax.swing.text.View;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatCapsLockIcon;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.util.UIScale;

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
 * @uiDefault PasswordField.iconTextGap				int		optional, default is 4
 * @uiDefault TextComponent.selectAllOnFocusPolicy	String	never, once (default) or always
 * @uiDefault TextComponent.selectAllOnMouseClick	boolean
 *
 * <!-- FlatPasswordFieldUI -->
 *
 * @uiDefault PasswordField.echoChar				character
 * @uiDefault PasswordField.showCapsLock			boolean
 * @uiDefault PasswordField.showRevealButton		boolean
 * @uiDefault PasswordField.capsLockIcon			Icon
 * @uiDefault PasswordField.revealIcon				Icon
 *
 * @author Karl Tauber
 */
public class FlatPasswordFieldUI
	extends FlatTextFieldUI
{
	// used to preserve reveal button state when switching theme
	private static final String KEY_REVEAL_SELECTED = "FlatLaf.internal.FlatPasswordFieldUI.revealSelected";

	private Character echoChar;

	@Styleable protected boolean showCapsLock;
	/** @since 2 */ @Styleable protected boolean showRevealButton;
	protected Icon capsLockIcon;
	/** @since 2 */ protected Icon revealIcon;

	private KeyListener capsLockListener;
	private boolean capsLockIconShared = true;
	private JToggleButton revealButton;
	private boolean uninstallEchoChar;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatPasswordFieldUI();
	}

	@Override
	protected String getPropertyPrefix() {
		return "PasswordField";
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installRevealButton();
	}

	@Override
	public void uninstallUI( JComponent c ) {
		uninstallRevealButton();

		super.uninstallUI( c );
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		String prefix = getPropertyPrefix();
		echoChar = (Character) UIManager.get( prefix + ".echoChar" );
		if( echoChar != null )
			LookAndFeel.installProperty( getComponent(), "echoChar", echoChar );

		showCapsLock = UIManager.getBoolean( "PasswordField.showCapsLock" );
		showRevealButton = UIManager.getBoolean( "PasswordField.showRevealButton" );
		capsLockIcon = UIManager.getIcon( "PasswordField.capsLockIcon" );
		revealIcon = UIManager.getIcon( "PasswordField.revealIcon" );
		capsLockIconShared = true;
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		capsLockIcon = null;
		revealIcon = null;
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
				if( e.getKeyCode() == KeyEvent.VK_CAPS_LOCK ) {
					e.getComponent().repaint();
					scrollCaretToVisible();
				}
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

	/** @since 2 */
	@Override
	String getStyleType() {
		return "PasswordField";
	}

	@Override
	protected void applyStyle( Object style ) {
		boolean oldShowRevealButton = showRevealButton;

		super.applyStyle( style );

		if( showRevealButton != oldShowRevealButton ) {
			uninstallRevealButton();
			installRevealButton();
		}
	}

	/** @since 2 */
	@Override
	protected Object applyStyleProperty( String key, Object value ) {
		if( key.equals( "capsLockIconColor" ) && capsLockIcon instanceof FlatCapsLockIcon ) {
			if( capsLockIconShared ) {
				capsLockIcon = FlatStylingSupport.cloneIcon( capsLockIcon );
				capsLockIconShared = false;
			}
			return ((FlatCapsLockIcon)capsLockIcon).applyStyleProperty( key, value );
		}

		return super.applyStyleProperty( key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		Map<String, Class<?>> infos = super.getStyleableInfos( c );
		infos.put( "capsLockIconColor", Color.class );
		return infos;
	}

	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		if( key.equals( "capsLockIconColor" ) && capsLockIcon instanceof FlatCapsLockIcon )
			return ((FlatCapsLockIcon)capsLockIcon).getStyleableValue( key );

		return super.getStyleableValue( c, key );
	}

	@Override
	public View create( Element elem ) {
		return new PasswordView( elem );
	}

	/** @since 2 */
	@Override
	protected void paintIcons( Graphics g, Rectangle r ) {
		super.paintIcons( g, r );

		if( isCapsLockVisible() )
			paintCapsLock( g, r );
	}

	/** @since 2 */
	protected void paintCapsLock( Graphics g, Rectangle r ) {
		JTextComponent c = getComponent();
		int x = c.getComponentOrientation().isLeftToRight()
			? r.x + r.width - capsLockIcon.getIconWidth()
			: r.x;
		int y = r.y + Math.round( (r.height - capsLockIcon.getIconHeight()) / 2f );
		capsLockIcon.paintIcon( c, g, x, y );
	}

	/** @since 2 */
	@Override
	protected boolean hasTrailingIcon() {
		return super.hasTrailingIcon() || isCapsLockVisible();
	}

	/** @since 2 */
	@Override
	protected int getTrailingIconWidth() {
		return super.getTrailingIconWidth()
			+ (isCapsLockVisible() ? capsLockIcon.getIconWidth() + UIScale.scale( iconTextGap ) : 0);
	}

	/** @since 1.4 */
	protected boolean isCapsLockVisible() {
		if( !showCapsLock )
			return false;

		return FlatUIUtils.isPermanentFocusOwner( getComponent() ) &&
			Toolkit.getDefaultToolkit().getLockingKeyState( KeyEvent.VK_CAPS_LOCK );
	}

	/** @since 2 */
	protected void installRevealButton() {
		if( showRevealButton ) {
			revealButton = createRevealButton();
			updateRevealButton();
			installLayout();
			getComponent().add( revealButton );
		}
	}

	/** @since 2 */
	protected JToggleButton createRevealButton() {
		JPasswordField c = (JPasswordField) getComponent();
		JToggleButton button = new JToggleButton( revealIcon, !c.echoCharIsSet() );
		button.setName( "PasswordField.revealButton" );
		prepareLeadingOrTrailingComponent( button );
		button.putClientProperty( FlatClientProperties.STYLE_CLASS, "inTextField revealButton" );
		if( FlatClientProperties.clientPropertyBoolean( c, KEY_REVEAL_SELECTED, false ) ) {
			button.setSelected( true );
			updateEchoChar( true );
		}
		button.addActionListener( e -> {
			boolean selected = button.isSelected();
			updateEchoChar( selected );
			c.putClientProperty( KEY_REVEAL_SELECTED, selected );
		} );
		return button;
	}

	/** @since 2.5 */
	protected void updateRevealButton() {
		if( revealButton == null )
			return;

		JTextComponent c = getComponent();
		boolean visible = c.isEnabled();
		if( visible != revealButton.isVisible() ) {
			revealButton.setVisible( visible );
			c.revalidate();
			c.repaint();

			if( !visible ) {
				revealButton.setSelected( false );
				updateEchoChar( false );
				getComponent().putClientProperty( KEY_REVEAL_SELECTED, null );
			}
		}
	}

	@Override
	protected void propertyChange( PropertyChangeEvent e ) {
		super.propertyChange( e );

		switch( e.getPropertyName() ) {
			case "enabled":
				updateRevealButton();
				break;
		}
	}

	private void updateEchoChar( boolean selected ) {
		char newEchoChar = selected
			? 0
			: (echoChar != null ? echoChar : '*');

		JPasswordField c = (JPasswordField) getComponent();
		if( newEchoChar == c.getEchoChar() )
			return;

		// set echo char
		LookAndFeel.installProperty( c, "echoChar", newEchoChar );

		// check whether was able to set echo char via LookAndFeel.installProperty()
		// if not, then echo char was explicitly changed via JPasswordField.setEchoChar()
		char actualEchoChar = c.getEchoChar();
		if( actualEchoChar != newEchoChar ) {
			if( selected && actualEchoChar != 0 ) {
				// use explicitly set echo char
				echoChar = actualEchoChar;
				uninstallEchoChar = true;
			}

			c.setEchoChar( newEchoChar );
		}
	}

	/** @since 2 */
	protected void uninstallRevealButton() {
		if( revealButton != null ) {
			if( uninstallEchoChar && revealButton.isSelected() )
				((JPasswordField)getComponent()).setEchoChar( echoChar );

			getComponent().remove( revealButton );
			revealButton = null;
		}
	}

	@Override
	protected JComponent[] getTrailingComponents() {
		return new JComponent[] { trailingComponent, revealButton, clearButton };
	}
}
