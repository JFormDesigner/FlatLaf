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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatHelpButtonIcon;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.UnknownStyleException;
import com.formdev.flatlaf.util.LoggingFacade;
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
 * @uiDefault Button.selectedBackground			Color
 * @uiDefault Button.selectedForeground			Color
 * @uiDefault Button.disabledBackground			Color	optional
 * @uiDefault Button.disabledText				Color
 * @uiDefault Button.disabledSelectedBackground	Color
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
 * @uiDefault Button.toolbar.selectedBackground	Color
 *
 * @author Karl Tauber
 */
public class FlatButtonUI
	extends BasicButtonUI
	implements StyleableUI
{
	@Styleable protected int minimumWidth;
	protected int iconTextGap;

	protected Color background;
	protected Color foreground;

	protected Color startBackground;
	protected Color endBackground;
	@Styleable protected Color focusedBackground;
	@Styleable protected Color hoverBackground;
	@Styleable protected Color pressedBackground;
	@Styleable protected Color selectedBackground;
	@Styleable protected Color selectedForeground;
	@Styleable protected Color disabledBackground;
	@Styleable protected Color disabledText;
	@Styleable protected Color disabledSelectedBackground;

	@Styleable(dot=true) protected Color defaultBackground;
	protected Color defaultEndBackground;
	@Styleable(dot=true) protected Color defaultForeground;
	@Styleable(dot=true) protected Color defaultFocusedBackground;
	@Styleable(dot=true) protected Color defaultHoverBackground;
	@Styleable(dot=true) protected Color defaultPressedBackground;
	@Styleable(dot=true) protected boolean defaultBoldText;

	@Styleable protected boolean paintShadow;
	@Styleable protected int shadowWidth;
	@Styleable protected Color shadowColor;
	@Styleable(dot=true) protected Color defaultShadowColor;

	@Styleable(dot=true) protected Color toolbarHoverBackground;
	@Styleable(dot=true) protected Color toolbarPressedBackground;
	@Styleable(dot=true) protected Color toolbarSelectedBackground;

	// only used via styling (not in UI defaults, but has likewise client properties)
	/** @since 2 */ @Styleable protected String buttonType;
	/** @since 2 */ @Styleable protected boolean squareSize;
	/** @since 2 */ @Styleable protected int minimumHeight;

	private Icon helpButtonIcon;
	private Insets defaultMargin;

	private final boolean shared;
	private boolean helpButtonIconShared = true;
	private boolean defaults_initialized = false;
	private Map<String, Object> oldStyleValues;
	private AtomicBoolean borderShared;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatButtonUI.class, () -> new FlatButtonUI( true ) )
			: new FlatButtonUI( false );
	}

	/** @since 2 */
	protected FlatButtonUI( boolean shared ) {
		this.shared = shared;
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle( (AbstractButton) c );
	}

	@Override
	protected void installDefaults( AbstractButton b ) {
		super.installDefaults( b );

		if( !defaults_initialized ) {
			String prefix = getPropertyPrefix();

			minimumWidth = UIManager.getInt( prefix + "minimumWidth" );
			iconTextGap = FlatUIUtils.getUIInt( prefix + "iconTextGap", 4 );

			background = UIManager.getColor( prefix + "background" );
			foreground = UIManager.getColor( prefix + "foreground" );

			startBackground = UIManager.getColor( prefix + "startBackground" );
			endBackground = UIManager.getColor( prefix + "endBackground" );
			focusedBackground = UIManager.getColor( prefix + "focusedBackground" );
			hoverBackground = UIManager.getColor( prefix + "hoverBackground" );
			pressedBackground = UIManager.getColor( prefix + "pressedBackground" );
			selectedBackground = UIManager.getColor( prefix + "selectedBackground" );
			selectedForeground = UIManager.getColor( prefix + "selectedForeground" );
			disabledBackground = UIManager.getColor( prefix + "disabledBackground" );
			disabledText = UIManager.getColor( prefix + "disabledText" );
			disabledSelectedBackground = UIManager.getColor( prefix + "disabledSelectedBackground" );

			defaultBackground = FlatUIUtils.getUIColor( "Button.default.startBackground", "Button.default.background" );
			defaultEndBackground = UIManager.getColor( "Button.default.endBackground" );
			defaultForeground = UIManager.getColor( "Button.default.foreground" );
			defaultFocusedBackground = UIManager.getColor( "Button.default.focusedBackground" );
			defaultHoverBackground = UIManager.getColor( "Button.default.hoverBackground" );
			defaultPressedBackground = UIManager.getColor( "Button.default.pressedBackground" );
			defaultBoldText = UIManager.getBoolean( "Button.default.boldText" );

			paintShadow = UIManager.getBoolean( "Button.paintShadow" );
			shadowWidth = FlatUIUtils.getUIInt( "Button.shadowWidth", 2 );
			shadowColor = UIManager.getColor( "Button.shadowColor" );
			defaultShadowColor = UIManager.getColor( "Button.default.shadowColor" );

			toolbarHoverBackground = UIManager.getColor( prefix + "toolbar.hoverBackground" );
			toolbarPressedBackground = UIManager.getColor( prefix + "toolbar.pressedBackground" );
			toolbarSelectedBackground = UIManager.getColor( prefix + "toolbar.selectedBackground" );

			helpButtonIcon = UIManager.getIcon( "HelpButton.icon" );
			defaultMargin = UIManager.getInsets( prefix + "margin" );

			helpButtonIconShared = true;
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

		oldStyleValues = null;
		borderShared = null;

		MigLayoutVisualPadding.uninstall( b );
		defaults_initialized = false;
	}

	@Override
	protected BasicButtonListener createButtonListener( AbstractButton b ) {
		return new FlatButtonListener( b );
	}

	protected void propertyChange( AbstractButton b, PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case SQUARE_SIZE:
			case MINIMUM_WIDTH:
			case MINIMUM_HEIGHT:
				b.revalidate();
				break;

			case BUTTON_TYPE:
				b.revalidate();
				b.repaint();
				break;

			case OUTLINE:
				b.repaint();
				break;

			case STYLE:
			case STYLE_CLASS:
				if( shared && FlatStylingSupport.hasStyleProperty( b ) ) {
					// unshare component UI if necessary
					// updateUI() invokes installStyle() from installUI()
					b.updateUI();
				} else
					installStyle( b );
				b.revalidate();
				b.repaint();
				break;
		}
	}

	/** @since 2 */
	protected void installStyle( AbstractButton b ) {
		try {
			applyStyle( b, FlatStylingSupport.getResolvedStyle( b, getStyleType() ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	String getStyleType() {
		return "Button";
	}

	/** @since 2 */
	protected void applyStyle( AbstractButton b, Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style,
			(key, value) -> applyStyleProperty( b, key, value ) );
	}

	/** @since 2 */
	protected Object applyStyleProperty( AbstractButton b, String key, Object value ) {
		if( key.startsWith( "help." ) ) {
			if( !(helpButtonIcon instanceof FlatHelpButtonIcon) )
				return new UnknownStyleException( key );

			if( helpButtonIconShared ) {
				helpButtonIcon = FlatStylingSupport.cloneIcon( helpButtonIcon );
				helpButtonIconShared = false;
			}

			key = key.substring( "help.".length() );
			return ((FlatHelpButtonIcon)helpButtonIcon).applyStyleProperty( key, value );
		}

		if( borderShared == null )
			borderShared = new AtomicBoolean( true );
		return FlatStylingSupport.applyToAnnotatedObjectOrBorder( this, key, value, b, borderShared );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		Map<String, Class<?>> infos = FlatStylingSupport.getAnnotatedStyleableInfos( this, c.getBorder() );
		if( helpButtonIcon instanceof FlatHelpButtonIcon )
			FlatStylingSupport.putAllPrefixKey( infos, "help.", ((FlatHelpButtonIcon)helpButtonIcon).getStyleableInfos() );
		return infos;
	}

	static boolean isContentAreaFilled( Component c ) {
		return !(c instanceof AbstractButton) || ((AbstractButton)c).isContentAreaFilled();
	}

	public static boolean isFocusPainted( Component c ) {
		return !(c instanceof AbstractButton) || ((AbstractButton)c).isFocusPainted();
	}

	static boolean isDefaultButton( Component c ) {
		return c instanceof JButton && ((JButton)c).isDefaultButton();
	}

	/**
	 * Returns true if the button has an icon but no text,
	 * or it does not have an icon and the text is either "..." or one character.
	 */
	static boolean isIconOnlyOrSingleCharacterButton( Component c ) {
		if( !(c instanceof JButton) && !(c instanceof JToggleButton) )
			return false;

		Icon icon = ((AbstractButton)c).getIcon();
		String text = ((AbstractButton)c).getText();
		return (icon != null && (text == null || text.isEmpty())) ||
			(icon == null && text != null &&
			 ("...".equals( text ) ||
			  text.length() == 1 ||
			  (text.length() == 2 && Character.isSurrogatePair( text.charAt( 0 ), text.charAt( 1 ) ))));
	}

	static final int TYPE_OTHER = -1;
	static final int TYPE_SQUARE = 0;
	static final int TYPE_ROUND_RECT = 1;

	static int getButtonType( Component c ) {
		if( !(c instanceof AbstractButton) )
			return TYPE_OTHER;

		String value = getButtonTypeStr( (AbstractButton) c );
		if( value == null )
			return TYPE_OTHER;

		switch( value ) {
			case BUTTON_TYPE_SQUARE:		return TYPE_SQUARE;
			case BUTTON_TYPE_ROUND_RECT:	return TYPE_ROUND_RECT;
			default:						return TYPE_OTHER;
		}
	}

	static boolean isHelpButton( Component c ) {
		return c instanceof JButton && BUTTON_TYPE_HELP.equals( getButtonTypeStr( (JButton) c ) );
	}

	static boolean isToolBarButton( Component c ) {
		return c.getParent() instanceof JToolBar ||
			(c instanceof AbstractButton && BUTTON_TYPE_TOOLBAR_BUTTON.equals( getButtonTypeStr( (AbstractButton) c ) ));
	}

	static boolean isBorderlessButton( Component c ) {
		return c instanceof AbstractButton && BUTTON_TYPE_BORDERLESS.equals( getButtonTypeStr( (AbstractButton) c ) );
	}

	static String getButtonTypeStr( AbstractButton c ) {
		// get from client property
		Object value = c.getClientProperty( BUTTON_TYPE );
		if( value instanceof String )
			return (String) value;

		// get from styling property
		ButtonUI ui = c.getUI();
		return (ui instanceof FlatButtonUI) ? ((FlatButtonUI)ui).buttonType : null;
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
		if( background == null )
			return;

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			boolean def = isDefaultButton( c );
			boolean isToolBarButton = isToolBarButton( c );
			float focusWidth = isToolBarButton ? 0 : FlatUIUtils.getBorderFocusWidth( c );
			float arc = FlatUIUtils.getBorderArc( c );
			float textFieldArc = 0;

			// if toolbar button is in leading/trailing component of a text field,
			// increase toolbar button arc to match text field arc (if necessary)
			if( isToolBarButton &&
				FlatClientProperties.clientProperty( c, STYLE_CLASS, "", String.class ).contains( "inTextField" ) )
			{
				JTextField textField = (JTextField) SwingUtilities.getAncestorOfClass( JTextField.class, c );
				if( textField != null )
					textFieldArc = FlatUIUtils.getBorderArc( textField );
			}

			int x = 0;
			int y = 0;
			int width = c.getWidth();
			int height = c.getHeight();

			if( isToolBarButton && c.getBorder() instanceof FlatButtonBorder ) {
				Insets spacing = UIScale.scale( ((FlatButtonBorder)c.getBorder()).toolbarSpacingInsets );
				x += spacing.left;
				y += spacing.top;
				width -= spacing.left + spacing.right;
				height -= spacing.top + spacing.bottom;

				// reduce text field arc
				textFieldArc -= spacing.top + spacing.bottom;
			}

			// increase toolbar button arc to match text field arc (if necessary)
			if( arc < textFieldArc )
				arc = textFieldArc;

			// paint shadow
			Color shadowColor = def ? defaultShadowColor : this.shadowColor;
			if( paintShadow &&
				shadowColor != null && shadowWidth > 0 && focusWidth > 0 && c.isEnabled() &&
				!isToolBarButton && !isBorderlessButton( c ) &&
				!(isFocusPainted( c ) && FlatUIUtils.isPermanentFocusOwner( c )) )
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
				g2.setColor( FlatUIUtils.deriveColor( background, getBackgroundBase( c, def ) ) );

			FlatUIUtils.paintComponentBackground( g2, x, y, width, height, focusWidth, arc );
		} finally {
			g2.dispose();
		}
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		super.paint( FlatLabelUI.createGraphicsHTMLTextYCorrection( g, c ), c );
	}

	@Override
	protected void paintIcon( Graphics g, JComponent c, Rectangle iconRect ) {
		// correct icon location when using bold font for default button
		int xOffset = defaultBoldPlainWidthDiff( c ) / 2;
		if( xOffset > 0 ) {
			boolean ltr = c.getComponentOrientation().isLeftToRight();
			switch( ((AbstractButton)c).getHorizontalTextPosition() ) {
				case SwingConstants.RIGHT:    iconRect.x -= xOffset; break;
				case SwingConstants.LEFT:     iconRect.x += xOffset; break;
				case SwingConstants.TRAILING: iconRect.x -= ltr ? xOffset : -xOffset; break;
				case SwingConstants.LEADING:  iconRect.x += ltr ? xOffset : -xOffset; break;
			}
		}

		super.paintIcon( g, c, iconRect );
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

		paintText( g, b, textRect, text, getForeground( b ) );
	}

	public static void paintText( Graphics g, AbstractButton b, Rectangle textRect, String text, Color foreground ) {
		FontMetrics fm = b.getFontMetrics( b.getFont() );
		int mnemonicIndex = FlatLaf.isShowMnemonics() ? b.getDisplayedMnemonicIndex() : -1;

		g.setColor( foreground );
		FlatUIUtils.drawStringUnderlineCharAt( b, g, text, mnemonicIndex,
			textRect.x, textRect.y + fm.getAscent() );
	}

	protected Color getBackground( JComponent c ) {
		boolean toolBarButton = isToolBarButton( c ) || isBorderlessButton( c );

		// selected state
		if( ((AbstractButton)c).isSelected() ) {
			// in toolbar use same background colors for disabled and enabled because
			// we assume that toolbar icon is shown disabled
			return buttonStateColor( c,
				toolBarButton ? toolbarSelectedBackground : selectedBackground,
				toolBarButton ? toolbarSelectedBackground : disabledSelectedBackground,
				null,
				null,
				toolBarButton ? toolbarPressedBackground : pressedBackground );
		}

		// toolbar button
		if( toolBarButton ) {
			Color bg = c.getBackground();
			return buttonStateColor( c,
				isCustomBackground( bg ) ? bg : null,
				null,
				null,
				toolbarHoverBackground,
				toolbarPressedBackground );
		}

		boolean def = isDefaultButton( c );
		return buttonStateColor( c,
			getBackgroundBase( c, def ),
			disabledBackground,
			isCustomBackground( c.getBackground() ) ? null : (def ? defaultFocusedBackground : focusedBackground),
			def ? defaultHoverBackground : hoverBackground,
			def ? defaultPressedBackground : pressedBackground );
	}

	protected Color getBackgroundBase( JComponent c, boolean def ) {
		// use component background if explicitly set
		Color bg = c.getBackground();
		if( isCustomBackground( bg ) )
			return bg;

		return def ? defaultBackground : bg;
	}

	protected boolean isCustomBackground( Color bg ) {
		return bg != background && (startBackground == null || bg != startBackground);
	}

	public static Color buttonStateColor( Component c, Color enabledColor, Color disabledColor,
		Color focusedColor, Color hoverColor, Color pressedColor )
	{
		if( c == null )
			return enabledColor;

		if( !c.isEnabled() )
			return disabledColor;

		if( c instanceof AbstractButton ) {
			ButtonModel model = ((AbstractButton)c).getModel();

			if( pressedColor != null && model.isPressed() )
				return pressedColor;

			if( hoverColor != null && model.isRollover() )
				return hoverColor;
		}

		if( focusedColor != null && isFocusPainted( c ) && FlatUIUtils.isPermanentFocusOwner( c ) )
			return focusedColor;

		return enabledColor;
	}

	protected Color getForeground( JComponent c ) {
		if( !c.isEnabled() )
			return disabledText;

		if( ((AbstractButton)c).isSelected() && !(isToolBarButton( c ) || isBorderlessButton( c )) )
			return selectedForeground;

		// use component foreground if explicitly set
		Color fg = c.getForeground();
		if( isCustomForeground( fg ) )
			return fg;

		boolean def = isDefaultButton( c );
		return def ? defaultForeground : fg;
	}

	protected boolean isCustomForeground( Color fg ) {
		return fg != foreground;
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		if( isHelpButton( c ) )
			return new Dimension( helpButtonIcon.getIconWidth(), helpButtonIcon.getIconHeight() );

		Dimension prefSize = super.getPreferredSize( c );
		if( prefSize == null )
			return null;

		// increase width when using bold font for default button
		prefSize.width += defaultBoldPlainWidthDiff( c );

		// make square or apply minimum width/height
		boolean isIconOnlyOrSingleCharacter = isIconOnlyOrSingleCharacterButton( c );
		if( clientPropertyBoolean( c, SQUARE_SIZE, squareSize ) ) {
			// make button square (increase width or height so that they are equal)
			prefSize.width = prefSize.height = Math.max( prefSize.width, prefSize.height );
		} else if( isIconOnlyOrSingleCharacter && ((AbstractButton)c).getIcon() == null ) {
			// make single-character-no-icon button square (increase width)
			prefSize.width = Math.max( prefSize.width, prefSize.height );
		} else if( !isIconOnlyOrSingleCharacter && !isToolBarButton( c ) &&
			c.getBorder() instanceof FlatButtonBorder && hasDefaultMargins( c ) )
		{
			// apply minimum width/height
			int fw = Math.round( FlatUIUtils.getBorderFocusWidth( c ) * 2 );
			prefSize.width = Math.max( prefSize.width, scale( FlatUIUtils.minimumWidth( c, minimumWidth ) ) + fw );
			prefSize.height = Math.max( prefSize.height, scale( FlatUIUtils.minimumHeight( c, minimumHeight ) ) + fw );
		}

		return prefSize;
	}

	private int defaultBoldPlainWidthDiff( JComponent c ) {
		if( defaultBoldText && isDefaultButton( c ) && c.getFont() instanceof UIResource ) {
			String text = ((AbstractButton)c).getText();
			if( text == null || text.isEmpty() )
				return 0;

			Font font = c.getFont();
			Font boldFont = font.deriveFont( Font.BOLD );
			int boldWidth = c.getFontMetrics( boldFont ).stringWidth( text );
			int plainWidth = c.getFontMetrics( font ).stringWidth( text );
			if( boldWidth > plainWidth )
				return boldWidth - plainWidth;
		}

		return 0;
	}

	private boolean hasDefaultMargins( JComponent c ) {
		Insets margin = ((AbstractButton)c).getMargin();
		return margin instanceof UIResource && Objects.equals( margin, defaultMargin );
	}

	//---- class FlatButtonListener -------------------------------------------

	protected class FlatButtonListener
		extends BasicButtonListener
	{
		private final AbstractButton b;

		protected FlatButtonListener( AbstractButton b ) {
			super( b );
			this.b = b;
		}

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			super.propertyChange( e );
			FlatButtonUI.this.propertyChange( b, e );
		}
	}
}
