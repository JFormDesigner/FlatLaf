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

import static com.formdev.flatlaf.FlatClientProperties.*;
import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import com.formdev.flatlaf.FlatLaf;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JButton}.
 *
 * <!-- BasicButtonUI -->
 *
 * @uiDefault Button.font						Font
 * @uiDefault Button.background					Color
 * @uiDefault Button.foreground					Color
 * @uiDefault Button.border						Border
 * @uiDefault Button.margin						Insets
 * @uiDefault Button.rollover					boolean
 *
 * <!-- FlatButtonUI -->
 *
 * @uiDefault Component.focusWidth				int
 * @uiDefault Button.arc						int
 * @uiDefault Button.minimumWidth				int
 * @uiDefault Button.iconTextGap				int
 * @uiDefault Button.focusedBackground			Color	optional
 * @uiDefault Button.hoverBackground			Color	optional
 * @uiDefault Button.pressedBackground			Color	optional
 * @uiDefault Button.disabledText				Color
 * @uiDefault Button.default.background			Color
 * @uiDefault Button.default.foreground			Color
 * @uiDefault Button.default.focusedBackground	Color	optional
 * @uiDefault Button.default.hoverBackground	Color	optional
 * @uiDefault Button.default.pressedBackground	Color	optional
 * @uiDefault Button.default.boldText			boolean
 * @uiDefault Button.toolbar.hoverBackground	Color
 * @uiDefault Button.toolbar.pressedBackground	Color
 *
 * @author Karl Tauber
 */
public class FlatButtonUI
	extends BasicButtonUI
{
	protected int focusWidth;
	protected int arc;
	protected int minimumWidth;
	protected int iconTextGap;

	protected Color focusedBackground;
	protected Color hoverBackground;
	protected Color pressedBackground;
	protected Color disabledText;

	protected Color defaultBackground;
	protected Color defaultForeground;
	protected Color defaultFocusedBackground;
	protected Color defaultHoverBackground;
	protected Color defaultPressedBackground;
	protected boolean defaultBoldText;

	protected Color toolbarHoverBackground;
	protected Color toolbarPressedBackground;

	private Icon helpButtonIcon;

	private boolean defaults_initialized = false;

	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatButtonUI();
		return instance;
	}

	@Override
	protected void installDefaults( AbstractButton b ) {
		super.installDefaults( b );

		if( !defaults_initialized ) {
			String prefix = getPropertyPrefix();

			focusWidth = UIManager.getInt( "Component.focusWidth" );
			arc = UIManager.getInt( prefix + "arc" );
			minimumWidth = UIManager.getInt( prefix + "minimumWidth" );
			iconTextGap = FlatUIUtils.getUIInt( prefix + "iconTextGap", 4 );

			focusedBackground = UIManager.getColor( prefix + "focusedBackground" );
			hoverBackground = UIManager.getColor( prefix + "hoverBackground" );
			pressedBackground = UIManager.getColor( prefix + "pressedBackground" );
			disabledText = UIManager.getColor( prefix + "disabledText" );

			defaultBackground = UIManager.getColor( "Button.default.background" );
			defaultForeground = UIManager.getColor( "Button.default.foreground" );
			defaultFocusedBackground = UIManager.getColor( "Button.default.focusedBackground" );
			defaultHoverBackground = UIManager.getColor( "Button.default.hoverBackground" );
			defaultPressedBackground = UIManager.getColor( "Button.default.pressedBackground" );
			defaultBoldText = UIManager.getBoolean( "Button.default.boldText" );

			toolbarHoverBackground = UIManager.getColor( prefix + "toolbar.hoverBackground" );
			toolbarPressedBackground = UIManager.getColor( prefix + "toolbar.pressedBackground" );

			helpButtonIcon = UIManager.getIcon( "HelpButton.icon" );

			defaults_initialized = true;
		}

		LookAndFeel.installProperty( b, "opaque", false );
		LookAndFeel.installProperty( b, "iconTextGap", scale( iconTextGap ) );

		MigLayoutVisualPadding.install( b, focusWidth );
	}

	@Override
	protected void uninstallDefaults( AbstractButton b ) {
		super.uninstallDefaults( b );

		MigLayoutVisualPadding.uninstall( b );
		defaults_initialized = false;
	}

	static boolean isContentAreaFilled( Component c ) {
		return !(c instanceof AbstractButton) || ((AbstractButton)c).isContentAreaFilled();
	}

	static boolean isDefaultButton( Component c ) {
		return c instanceof JButton && ((JButton)c).isDefaultButton();
	}

	static boolean isIconOnlyButton( Component c ) {
		if( !(c instanceof JButton) )
			return false;

		Icon icon = ((JButton)c).getIcon();
		String text = ((JButton)c).getText();
		return (icon != null && (text == null || text.isEmpty())) ||
			(icon == null && text != null && ("...".equals( text ) || text.length() == 1));
	}

	static boolean isHelpButton( Component c ) {
		return c instanceof JButton && clientPropertyEquals( (JButton) c, BUTTON_TYPE, BUTTON_TYPE_HELP );
	}

	static boolean isToolBarButton( JComponent c ) {
		return c.getParent() instanceof JToolBar;
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		// fill background if opaque to avoid garbage if user sets opaque to true
		if( c.isOpaque() )
			FlatUIUtils.paintParentBackground( g, c );

		if( isHelpButton( c ) ) {
			helpButtonIcon.paintIcon( c, g, 0, 0 );
			return;
		}

		if( isContentAreaFilled( c ) ) {
			Color background = getBackground( c );
			if( background != null ) {
				Graphics2D g2 = (Graphics2D) g.create();
				try {
					FlatUIUtils.setRenderingHints( g2 );

					Border border = c.getBorder();
					float focusWidth = (border instanceof FlatBorder) ? scale( (float) this.focusWidth ) : 0;
					float arc = (border instanceof FlatButtonBorder || isToolBarButton( c )) ? scale( (float) this.arc ) : 0;

					FlatUIUtils.setColor( g2, background, isDefaultButton(c) ? defaultBackground : c.getBackground() );
					FlatUIUtils.fillRoundRectangle( g2, 0, 0, c.getWidth(), c.getHeight(), focusWidth, arc );
				} finally {
					g2.dispose();
				}
			}
		}

		paint( g, c );
	}

	@Override
	protected void paintText( Graphics g, AbstractButton b, Rectangle textRect, String text ) {
		if( isHelpButton( b ) )
			return;

		if( defaultBoldText && isDefaultButton( b ) && b.getFont() instanceof UIResource ) {
			Font boldFont = g.getFont().deriveFont( Font.BOLD );
			g.setFont( boldFont );

			int boldWidth = b.getFontMetrics( boldFont ).stringWidth( text );
			if( boldWidth > textRect.width ) {
				textRect.x -= (boldWidth - textRect.width) / 2;
				textRect.width = boldWidth;
			}
		}

		paintText( g, b, textRect, text, b.isEnabled() ? getForeground( b ) : disabledText );
	}

	public static void paintText( Graphics g, AbstractButton b, Rectangle textRect, String text, Color foreground ) {
		FontMetrics fm = b.getFontMetrics( b.getFont() );
		int mnemonicIndex = FlatLaf.isShowMnemonics() ? b.getDisplayedMnemonicIndex() : -1;

		g.setColor( foreground );
		FlatUIUtils.drawStringUnderlineCharAt( b, g, text, mnemonicIndex,
			textRect.x, textRect.y + fm.getAscent() );
	}

	protected Color getBackground( JComponent c ) {
		if( !c.isEnabled() )
			return null;

		// toolbar button
		if( isToolBarButton( c ) ) {
			ButtonModel model = ((AbstractButton)c).getModel();
			if( model.isPressed() )
				return toolbarPressedBackground;
			if( model.isRollover() )
				return toolbarHoverBackground;

			// use background of toolbar
			return c.getParent().getBackground();
		}

		boolean def = isDefaultButton( c );
		return buttonStateColor( c,
			def ? defaultBackground : c.getBackground(),
			null,
			def ? defaultFocusedBackground : focusedBackground,
			def ? defaultHoverBackground : hoverBackground,
			def ? defaultPressedBackground : pressedBackground );
	}

	public static Color buttonStateColor( Component c, Color enabledColor, Color disabledColor,
		Color focusedColor, Color hoverColor, Color pressedColor )
	{
		AbstractButton b = (c instanceof AbstractButton) ? (AbstractButton) c : null;

		if( !c.isEnabled() )
			return disabledColor;

		if( pressedColor != null && b != null && b.getModel().isPressed() )
			return pressedColor;

		if( hoverColor != null && b != null && b.getModel().isRollover() )
			return hoverColor;

		if( focusedColor != null && c.hasFocus() )
			return focusedColor;

		return enabledColor;
	}

	protected Color getForeground( JComponent c ) {
		boolean def = isDefaultButton( c );
		return def ? defaultForeground : c.getForeground();
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		if( isHelpButton( c ) )
			return new Dimension( helpButtonIcon.getIconWidth(), helpButtonIcon.getIconHeight() );

		Dimension prefSize = super.getPreferredSize( c );

		// make button square if it is a icon-only button
		// or apply minimum width, if not in toolbar and not a icon-only button
		if( isIconOnlyButton( c ) )
			prefSize.width = Math.max( prefSize.width, prefSize.height );
		else if( !isToolBarButton( c ) )
			prefSize.width = Math.max( prefSize.width, scale( minimumWidth + (focusWidth * 2) ) );

		return prefSize;
	}
}
