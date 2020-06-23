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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.UIScale;

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
 * @uiDefault Button.minimumWidth				int
 * @uiDefault Button.iconTextGap				int
 * @uiDefault Button.startBackground			Color	optional; if set, a gradient paint is used and Button.background is ignored
 * @uiDefault Button.endBackground				Color	optional; if set, a gradient paint is used
 * @uiDefault Button.focusedBackground			Color	optional
 * @uiDefault Button.hoverBackground			Color	optional
 * @uiDefault Button.pressedBackground			Color	optional
 * @uiDefault Button.disabledBackground			Color	optional
 * @uiDefault Button.disabledText				Color
 * @uiDefault Button.default.background			Color
 * @uiDefault Button.default.startBackground	Color	optional; if set, a gradient paint is used and Button.default.background is ignored
 * @uiDefault Button.default.endBackground		Color	optional; if set, a gradient paint is used
 * @uiDefault Button.default.foreground			Color
 * @uiDefault Button.default.focusedBackground	Color	optional
 * @uiDefault Button.default.hoverBackground	Color	optional
 * @uiDefault Button.default.pressedBackground	Color	optional
 * @uiDefault Button.default.boldText			boolean
 * @uiDefault Button.paintShadow				boolean	default is false
 * @uiDefault Button.shadowWidth				int		default is 2
 * @uiDefault Button.shadowColor				Color	optional
 * @uiDefault Button.default.shadowColor		Color	optional
 * @uiDefault Button.toolbar.spacingInsets		Insets
 * @uiDefault Button.toolbar.hoverBackground	Color
 * @uiDefault Button.toolbar.pressedBackground	Color
 *
 * @author Karl Tauber
 */
public class FlatButtonUI
	extends BasicButtonUI
{
	protected int minimumWidth;
	protected int iconTextGap;

	protected Color startBackground;
	protected Color endBackground;
	protected Color focusedBackground;
	protected Color hoverBackground;
	protected Color pressedBackground;
	protected Color disabledBackground;
	protected Color disabledText;

	protected Color defaultBackground;
	protected Color defaultEndBackground;
	protected Color defaultForeground;
	protected Color defaultFocusedBackground;
	protected Color defaultHoverBackground;
	protected Color defaultPressedBackground;
	protected boolean defaultBoldText;

	protected int shadowWidth;
	protected Color shadowColor;
	protected Color defaultShadowColor;

	protected Insets toolbarSpacingInsets;
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

			minimumWidth = UIManager.getInt( prefix + "minimumWidth" );
			iconTextGap = FlatUIUtils.getUIInt( prefix + "iconTextGap", 4 );

			startBackground = UIManager.getColor( prefix + "startBackground" );
			endBackground = UIManager.getColor( prefix + "endBackground" );
			focusedBackground = UIManager.getColor( prefix + "focusedBackground" );
			hoverBackground = UIManager.getColor( prefix + "hoverBackground" );
			pressedBackground = UIManager.getColor( prefix + "pressedBackground" );
			disabledBackground = UIManager.getColor( prefix + "disabledBackground" );
			disabledText = UIManager.getColor( prefix + "disabledText" );

			if( UIManager.getBoolean( "Button.paintShadow" ) ) {
				shadowWidth = FlatUIUtils.getUIInt( "Button.shadowWidth", 2 );
				shadowColor = UIManager.getColor( "Button.shadowColor" );
				defaultShadowColor = UIManager.getColor( "Button.default.shadowColor" );
			} else {
				shadowWidth = 0;
				shadowColor = null;
				defaultShadowColor = null;
			}

			defaultBackground = FlatUIUtils.getUIColor( "Button.default.startBackground", "Button.default.background" );
			defaultEndBackground = UIManager.getColor( "Button.default.endBackground" );
			defaultForeground = UIManager.getColor( "Button.default.foreground" );
			defaultFocusedBackground = UIManager.getColor( "Button.default.focusedBackground" );
			defaultHoverBackground = UIManager.getColor( "Button.default.hoverBackground" );
			defaultPressedBackground = UIManager.getColor( "Button.default.pressedBackground" );
			defaultBoldText = UIManager.getBoolean( "Button.default.boldText" );

			toolbarSpacingInsets = UIManager.getInsets( "Button.toolbar.spacingInsets" );
			toolbarHoverBackground = UIManager.getColor( prefix + "toolbar.hoverBackground" );
			toolbarPressedBackground = UIManager.getColor( prefix + "toolbar.pressedBackground" );

			helpButtonIcon = UIManager.getIcon( "HelpButton.icon" );

			defaults_initialized = true;
		}

		if( startBackground != null ) {
			Color bg = b.getBackground();
			if( bg == null || bg instanceof UIResource )
				b.setBackground( startBackground );
		}

		LookAndFeel.installProperty( b, "opaque", false );
		LookAndFeel.installProperty( b, "iconTextGap", scale( iconTextGap ) );

		MigLayoutVisualPadding.install( b );
	}

	@Override
	protected void uninstallDefaults( AbstractButton b ) {
		super.uninstallDefaults( b );

		MigLayoutVisualPadding.uninstall( b );
		defaults_initialized = false;
	}

	@Override
	protected BasicButtonListener createButtonListener( AbstractButton b ) {
		return new BasicButtonListener( b ) {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {
				super.propertyChange( e );
				FlatButtonUI.this.propertyChange( b, e );
			}
		};
	}

	protected void propertyChange( AbstractButton b, PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case MINIMUM_WIDTH:
			case MINIMUM_HEIGHT:
				b.revalidate();
				break;

			case BUTTON_TYPE:
				b.revalidate();
				b.repaint();
				break;
		}
	}

	static boolean isContentAreaFilled( Component c ) {
		return !(c instanceof AbstractButton) || ((AbstractButton)c).isContentAreaFilled();
	}

	static boolean isDefaultButton( Component c ) {
		return c instanceof JButton && ((JButton)c).isDefaultButton();
	}

	/**
	 * Returns true if the button has an icon but no text,
	 * or it it does not have an icon and the text is either "..." or one character.
	 */
	static boolean isIconOnlyOrSingleCharacterButton( Component c ) {
		if( !(c instanceof JButton) && !(c instanceof JToggleButton) )
			return false;

		Icon icon = ((AbstractButton)c).getIcon();
		String text = ((AbstractButton)c).getText();
		return (icon != null && (text == null || text.isEmpty())) ||
			(icon == null && text != null && ("...".equals( text ) || text.length() == 1));
	}

	// same indices as in parameters to clientPropertyChoice()
	static final int TYPE_OTHER = -1;
	static final int TYPE_SQUARE = 0;
	static final int TYPE_ROUND_RECT = 1;

	static int getButtonType( Component c ) {
		return (c instanceof AbstractButton)
			? clientPropertyChoice( (AbstractButton) c, BUTTON_TYPE, BUTTON_TYPE_SQUARE, BUTTON_TYPE_ROUND_RECT )
			: TYPE_OTHER;
	}

	static boolean isHelpButton( Component c ) {
		return c instanceof JButton && clientPropertyEquals( (JButton) c, BUTTON_TYPE, BUTTON_TYPE_HELP );
	}

	static boolean isToolBarButton( Component c ) {
		return c.getParent() instanceof JToolBar ||
			(c instanceof AbstractButton && clientPropertyEquals( (AbstractButton) c, BUTTON_TYPE, BUTTON_TYPE_TOOLBAR_BUTTON ));
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

		if( isContentAreaFilled( c ) )
			paintBackground( g, c );

		paint( g, c );
	}

	protected void paintBackground( Graphics g, JComponent c ) {
		Color background = getBackground( c );
		if( background != null ) {
			Graphics2D g2 = (Graphics2D) g.create();
			try {
				FlatUIUtils.setRenderingHints( g2 );

				boolean isToolBarButton = isToolBarButton( c );
				float focusWidth = isToolBarButton ? 0 : FlatUIUtils.getBorderFocusWidth( c );
				float arc = FlatUIUtils.getBorderArc( c );

				boolean def = isDefaultButton( c );

				int x = 0;
				int y = 0;
				int width = c.getWidth();
				int height = c.getHeight();

				if( isToolBarButton ) {
					Insets spacing = UIScale.scale( toolbarSpacingInsets );
					x += spacing.left;
					y += spacing.top;
					width -= spacing.left + spacing.right;
					height -= spacing.top + spacing.bottom;
				}

				// paint shadow
				Color shadowColor = def ? defaultShadowColor : this.shadowColor;
				if( !isToolBarButton && shadowColor != null && shadowWidth > 0 && focusWidth > 0 &&
					!FlatUIUtils.isPermanentFocusOwner( c ) && c.isEnabled() )
				{
					g2.setColor( shadowColor );
					g2.fill( new RoundRectangle2D.Float( focusWidth, focusWidth + UIScale.scale( (float) shadowWidth ),
						width - focusWidth * 2, height - focusWidth * 2, arc, arc ) );
				}

				// paint background
				Color startBg = def ? defaultBackground : startBackground;
				Color endBg = def ? defaultEndBackground : endBackground;
				if( background == startBg && endBg != null && !startBg.equals( endBg ) )
					g2.setPaint( new GradientPaint( 0, 0, startBg, 0, height, endBg ) );
				else
					g2.setColor( FlatUIUtils.deriveColor( background, def ? defaultBackground : c.getBackground() ) );

				FlatUIUtils.paintComponentBackground( g2, x, y, width, height, focusWidth, arc );
			} finally {
				g2.dispose();
			}
		}
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		super.paint( FlatLabelUI.createGraphicsHTMLTextYCorrection( g, c ), c );
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
			return disabledBackground;

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

		if( focusedColor != null && FlatUIUtils.isPermanentFocusOwner( c ) )
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
		if( prefSize == null )
			return null;

		// make button square if it is a single-character button
		// or apply minimum width, if not in toolbar and not a icon-only or single-character button
		if( isIconOnlyOrSingleCharacterButton( c ) ) {
			// make only single-character buttons square to allow non-square icon-only buttons
			if( ((AbstractButton)c).getIcon() == null )
				prefSize.width = Math.max( prefSize.width, prefSize.height );
		} else if( !isToolBarButton( c ) && c.getBorder() instanceof FlatButtonBorder ) {
			float focusWidth = FlatUIUtils.getBorderFocusWidth( c );
			prefSize.width = Math.max( prefSize.width, scale( FlatUIUtils.minimumWidth( c, minimumWidth ) ) + Math.round( focusWidth * 2 ) );
			prefSize.height = Math.max( prefSize.height, scale( FlatUIUtils.minimumHeight( c, 0 ) ) + Math.round( focusWidth * 2 ) );
		}

		return prefSize;
	}
}
