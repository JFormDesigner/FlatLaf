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

package com.formdev.flatlaf.ui;

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatCheckBoxMenuItemIcon;
import com.formdev.flatlaf.icons.FlatMenuArrowIcon;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.UnknownStyleException;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Renderer for menu items.
 *
 * @uiDefault MenuItem.verticallyAlignText							boolean
 * @uiDefault MenuItem.minimumWidth									int
 * @uiDefault MenuItem.minimumIconSize								Dimension
 * @uiDefault MenuItem.textAcceleratorGap							int
 * @uiDefault MenuItem.textNoAcceleratorGap							int
 * @uiDefault MenuItem.acceleratorArrowGap							int
 * @uiDefault MenuItem.checkBackground								Color
 * @uiDefault MenuItem.checkMargins									Insets
 * @uiDefault MenuItem.selectionInsets								Insets
 * @uiDefault MenuItem.selectionArc									int
 * @uiDefault MenuItem.selectionType								String	null (default) or underline
 * @uiDefault MenuItem.underlineSelectionBackground					Color
 * @uiDefault MenuItem.underlineSelectionCheckBackground			Color
 * @uiDefault MenuItem.underlineSelectionColor						Color
 * @uiDefault MenuItem.underlineSelectionHeight						int
 *
 * @author Karl Tauber
 */
public class FlatMenuItemRenderer
{
	private static final String KEY_MAX_ICONS_WIDTH = "FlatLaf.internal.FlatMenuItemRenderer.maxIconWidth";

	protected final JMenuItem menuItem;
	protected Icon checkIcon;
	protected Icon arrowIcon;
	@Styleable protected Font acceleratorFont;
	protected final String acceleratorDelimiter;

	/** @since 2 */ @Styleable protected boolean verticallyAlignText = FlatUIUtils.getUIBoolean( "MenuItem.verticallyAlignText", true );
	@Styleable protected int minimumWidth = UIManager.getInt( "MenuItem.minimumWidth" );
	@Styleable protected Dimension minimumIconSize;
	@Styleable protected int textAcceleratorGap = FlatUIUtils.getUIInt( "MenuItem.textAcceleratorGap", 28 );
	@Styleable protected int textNoAcceleratorGap = FlatUIUtils.getUIInt( "MenuItem.textNoAcceleratorGap", 6 );
	@Styleable protected int acceleratorArrowGap = FlatUIUtils.getUIInt( "MenuItem.acceleratorArrowGap", 2 );

	@Styleable protected Color checkBackground = UIManager.getColor( "MenuItem.checkBackground" );
	@Styleable protected Insets checkMargins = UIManager.getInsets( "MenuItem.checkMargins" );

	/** @since 3 */ @Styleable protected Insets selectionInsets = UIManager.getInsets( "MenuItem.selectionInsets" );
	/** @since 3 */ @Styleable protected int selectionArc = UIManager.getInt( "MenuItem.selectionArc" );

	@Styleable protected Color underlineSelectionBackground = UIManager.getColor( "MenuItem.underlineSelectionBackground" );
	@Styleable protected Color underlineSelectionCheckBackground = UIManager.getColor( "MenuItem.underlineSelectionCheckBackground" );
	@Styleable protected Color underlineSelectionColor = UIManager.getColor( "MenuItem.underlineSelectionColor" );
	@Styleable protected int underlineSelectionHeight = UIManager.getInt( "MenuItem.underlineSelectionHeight" );

	private boolean iconsShared = true;
	private final Font menuFont = UIManager.getFont( "Menu.font" );

	protected FlatMenuItemRenderer( JMenuItem menuItem, Icon checkIcon, Icon arrowIcon,
		Font acceleratorFont, String acceleratorDelimiter )
	{
		this.menuItem = menuItem;
		this.checkIcon = checkIcon;
		this.arrowIcon = arrowIcon;
		this.acceleratorFont = acceleratorFont;
		this.acceleratorDelimiter = acceleratorDelimiter;

		Dimension minimumIconSize = UIManager.getDimension( "MenuItem.minimumIconSize" );
		this.minimumIconSize = (minimumIconSize != null) ? minimumIconSize : new Dimension( 16, 16 );
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		// style icon
		if( key.startsWith( "icon." ) || key.equals( "selectionForeground" ) ) {
			if( iconsShared ) {
				if( checkIcon instanceof FlatCheckBoxMenuItemIcon )
					checkIcon = FlatStylingSupport.cloneIcon( checkIcon );
				if( arrowIcon instanceof FlatMenuArrowIcon )
					arrowIcon = FlatStylingSupport.cloneIcon( arrowIcon );
				iconsShared = false;
			}

			if( key.startsWith( "icon." ) ) {
				String key2 = key.substring( "icon.".length() );

				try {
					if( checkIcon instanceof FlatCheckBoxMenuItemIcon )
						return ((FlatCheckBoxMenuItemIcon)checkIcon).applyStyleProperty( key2, value );
				} catch ( UnknownStyleException ex ) {
					// ignore
				}

				try {
					if( arrowIcon instanceof FlatMenuArrowIcon )
						return ((FlatMenuArrowIcon)arrowIcon).applyStyleProperty( key2, value );
				} catch ( UnknownStyleException ex ) {
					// ignore
				}

				// keys with prefix "icon." are only for icons
				throw new UnknownStyleException( key );
			} else if( key.equals( "selectionForeground" ) ) {
				// special case: same key is used in icons and in menuitem
				if( checkIcon instanceof FlatCheckBoxMenuItemIcon )
					((FlatCheckBoxMenuItemIcon)checkIcon).applyStyleProperty( key, value );
				if( arrowIcon instanceof FlatMenuArrowIcon )
					((FlatMenuArrowIcon)arrowIcon).applyStyleProperty( key, value );

				// throw exception because the caller should also apply this key
				throw new UnknownStyleException( key );
			}
		}

		return FlatStylingSupport.applyToAnnotatedObject( this, key, value );
	}

	/** @since 2 */
	public Map<String, Class<?>> getStyleableInfos() {
		Map<String, Class<?>> infos = FlatStylingSupport.getAnnotatedStyleableInfos( this );
		if( checkIcon instanceof FlatCheckBoxMenuItemIcon )
			FlatStylingSupport.putAllPrefixKey( infos, "icon.", ((FlatCheckBoxMenuItemIcon)checkIcon).getStyleableInfos() );
		infos.remove( "icon.selectionForeground" );
		if( arrowIcon instanceof FlatMenuArrowIcon )
			FlatStylingSupport.putAllPrefixKey( infos, "icon.", ((FlatMenuArrowIcon)arrowIcon).getStyleableInfos() );
		infos.remove( "icon.selectionForeground" );
		return infos;
	}

	/** @since 2.5 */
	public Object getStyleableValue( String key ) {
		if( key.startsWith( "icon." ) ) {
			String key2 = key.substring( "icon.".length() );
			if( checkIcon instanceof FlatCheckBoxMenuItemIcon )
				return ((FlatCheckBoxMenuItemIcon)checkIcon).getStyleableValue( key2 );
			if( arrowIcon instanceof FlatMenuArrowIcon )
				return ((FlatMenuArrowIcon)arrowIcon).getStyleableValue( key2 );
		}

		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	protected Dimension getPreferredMenuItemSize() {
		int width = 0;
		int height = 0;
		boolean isTopLevelMenu = isTopLevelMenu( menuItem );

		Rectangle viewRect = new Rectangle( 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE );
		Rectangle iconRect = new Rectangle();
		Rectangle textRect = new Rectangle();

		// layout icon and text
		SwingUtilities.layoutCompoundLabel( menuItem,
			menuItem.getFontMetrics( isTopLevelMenu ? getTopLevelFont() : menuItem.getFont() ),
			menuItem.getText(), getIconForLayout(),
			menuItem.getVerticalAlignment(), menuItem.getHorizontalAlignment(),
			menuItem.getVerticalTextPosition(), menuItem.getHorizontalTextPosition(),
			viewRect, iconRect, textRect, scale( menuItem.getIconTextGap() ) );

		// union icon and text rectangles
		Rectangle labelRect = iconRect.union( textRect );
		width += labelRect.width;
		height = Math.max( labelRect.height, height );

		// accelerator size
		String accelText = getAcceleratorText();
		if( accelText != null ) {
			// gap between text and accelerator
			width += scale( !isTopLevelMenu ? textAcceleratorGap : menuItem.getIconTextGap() );

			FontMetrics accelFm = menuItem.getFontMetrics( acceleratorFont );
			width += SwingUtilities.computeStringWidth( accelFm, accelText );
			height = Math.max( accelFm.getHeight(), height );
		}

		// arrow size
		if( !isTopLevelMenu && arrowIcon != null ) {
			// gap between text and arrow
			if( accelText == null )
				width += scale( textNoAcceleratorGap );

			// gap between accelerator and arrow
			width += scale( acceleratorArrowGap );

			width += arrowIcon.getIconWidth();
			height = Math.max( arrowIcon.getIconHeight(), height );
		}

		// add insets
		Insets insets = menuItem.getInsets();
		width += insets.left + insets.right;
		height += insets.top + insets.bottom;

		// minimum width
		if( !isTopLevelMenu ) {
			int minimumWidth = FlatUIUtils.minimumWidth( menuItem, this.minimumWidth );
			width = Math.max( width, scale( minimumWidth ) );
		}

		return new Dimension( width, height );
	}

	private void layout( Rectangle viewRect, Rectangle iconRect, Rectangle textRect,
		Rectangle accelRect, Rectangle arrowRect, Rectangle labelRect )
	{
		boolean isTopLevelMenu = isTopLevelMenu( menuItem );

		// layout arrow
		if( !isTopLevelMenu && arrowIcon != null ) {
			arrowRect.width = arrowIcon.getIconWidth();
			arrowRect.height = arrowIcon.getIconHeight();
		} else
			arrowRect.setSize( 0, 0 );
		arrowRect.y = viewRect.y + centerOffset( viewRect.height, arrowRect.height );

		// layout accelerator
		String accelText = getAcceleratorText();
		if( accelText != null ) {
			FontMetrics accelFm = menuItem.getFontMetrics( acceleratorFont );
			accelRect.width = SwingUtilities.computeStringWidth( accelFm, accelText );
			accelRect.height = accelFm.getHeight();

			accelRect.y = viewRect.y + centerOffset( viewRect.height, accelRect.height );
		} else
			accelRect.setBounds( 0, 0, 0, 0 );

		// compute horizontal positions of accelerator and arrow
		int accelArrowGap = !isTopLevelMenu ? scale( acceleratorArrowGap ) : 0;
		if( menuItem.getComponentOrientation().isLeftToRight() ) {
			// left-to-right
			arrowRect.x = viewRect.x + viewRect.width - arrowRect.width;
			accelRect.x = arrowRect.x - accelArrowGap - accelRect.width;
		} else {
			// right-to-left
			arrowRect.x = viewRect.x;
			accelRect.x = arrowRect.x + accelArrowGap + arrowRect.width;
		}

		// width of accelerator, arrow and gap
		int accelArrowWidth = accelRect.width + arrowRect.width;
		if( accelText != null )
			accelArrowWidth += scale( !isTopLevelMenu ? textAcceleratorGap : menuItem.getIconTextGap() );
		if( !isTopLevelMenu && arrowIcon != null ) {
			if( accelText == null )
				accelArrowWidth += scale( textNoAcceleratorGap );
			accelArrowWidth += scale( acceleratorArrowGap );
		}

		// label rectangle is view rectangle subtracted by accelerator, arrow and gap
		labelRect.setBounds( viewRect );
		labelRect.width -= accelArrowWidth;
		if( !menuItem.getComponentOrientation().isLeftToRight() )
			labelRect.x += accelArrowWidth;

		// layout icon and text
		SwingUtilities.layoutCompoundLabel( menuItem,
			menuItem.getFontMetrics( isTopLevelMenu ? getTopLevelFont() : menuItem.getFont() ),
			menuItem.getText(), getIconForLayout(),
			menuItem.getVerticalAlignment(), menuItem.getHorizontalAlignment(),
			menuItem.getVerticalTextPosition(), menuItem.getHorizontalTextPosition(),
			labelRect, iconRect, textRect, scale( menuItem.getIconTextGap() ) );
	}

	private static int centerOffset( int wh1, int wh2 ) {
		return (wh1 / 2) - (wh2 / 2);
	}

	protected void paintMenuItem( Graphics g, Color selectionBackground, Color selectionForeground,
		Color disabledForeground, Color acceleratorForeground, Color acceleratorSelectionForeground )
	{
		Rectangle viewRect = new Rectangle( menuItem.getWidth(), menuItem.getHeight() );

		// subtract insets
		Insets insets = menuItem.getInsets();
		viewRect.x += insets.left;
		viewRect.y += insets.top;
		viewRect.width -= (insets.left + insets.right);
		viewRect.height -= (insets.top + insets.bottom);

		Rectangle iconRect = new Rectangle();
		Rectangle textRect = new Rectangle();
		Rectangle accelRect = new Rectangle();
		Rectangle arrowRect = new Rectangle();
		Rectangle labelRect = new Rectangle();

		layout( viewRect, iconRect, textRect, accelRect, arrowRect, labelRect );

/*debug
		g.setColor( Color.green ); g.drawRect( viewRect.x, viewRect.y, viewRect.width - 1, viewRect.height - 1 );
		g.setColor( Color.red ); g.drawRect( labelRect.x, labelRect.y, labelRect.width - 1, labelRect.height - 1 );
		g.setColor( Color.blue ); g.drawRect( iconRect.x, iconRect.y, iconRect.width - 1, iconRect.height - 1 );
		g.setColor( Color.cyan ); g.drawRect( textRect.x, textRect.y, textRect.width - 1, textRect.height - 1 );
		g.setColor( Color.magenta ); g.drawRect( accelRect.x, accelRect.y, accelRect.width - 1, accelRect.height - 1 );
		g.setColor( Color.orange ); g.drawRect( arrowRect.x, arrowRect.y, arrowRect.width - 1, arrowRect.height - 1 );
debug*/

		boolean armedOrSelected = isArmedOrSelected( menuItem );
		boolean underlineSelection = isUnderlineSelection();

		paintBackground( g );
		if( armedOrSelected ) {
			if( underlineSelection )
				paintUnderlineSelection( g, underlineSelectionBackground, underlineSelectionColor, underlineSelectionHeight );
			else
				paintSelection( g, selectionBackground, selectionInsets, selectionArc );
		}
		paintIcon( g, iconRect, getIconForPainting(), underlineSelection ? underlineSelectionCheckBackground : checkBackground, selectionBackground );
		paintText( g, textRect, menuItem.getText(), selectionForeground, disabledForeground );
		paintAccelerator( g, accelRect, getAcceleratorText(), acceleratorForeground, acceleratorSelectionForeground, disabledForeground );
		if( !isTopLevelMenu( menuItem ) )
			paintArrowIcon( g, arrowRect, arrowIcon );
	}

	/** @since 3 */
	protected void paintBackground( Graphics g ) {
		if( menuItem.isOpaque() ) {
			g.setColor( menuItem.getBackground() );
			g.fillRect( 0, 0, menuItem.getWidth(), menuItem.getHeight() );
		}
	}

	/** @since 3 */
	protected void paintSelection( Graphics g, Color selectionBackground, Insets selectionInsets, int selectionArc ) {
		float arc = scale( selectionArc / 2f );

		g.setColor( deriveBackground( selectionBackground ) );
		FlatUIUtils.paintSelection( (Graphics2D) g, 0, 0, menuItem.getWidth(), menuItem.getHeight(),
			scale( selectionInsets ), arc, arc, arc, arc, 0 );
	}

	/** @since 3 */
	protected void paintUnderlineSelection( Graphics g, Color underlineSelectionBackground,
		Color underlineSelectionColor, int underlineSelectionHeight )
	{
		int width = menuItem.getWidth();
		int height = menuItem.getHeight();

		// paint background
		g.setColor( deriveBackground( underlineSelectionBackground ) );
		g.fillRect( 0, 0, width, height );

		// paint underline
		int underlineHeight = scale( underlineSelectionHeight );
		g.setColor( underlineSelectionColor );
		if( isTopLevelMenu( menuItem ) ) {
			// paint underline at bottom
			g.fillRect( 0, height - underlineHeight, width, underlineHeight );
		} else if( menuItem.getComponentOrientation().isLeftToRight() ) {
			// paint underline at left side
			g.fillRect( 0, 0, underlineHeight, height );
		} else {
			// paint underline at right side
			g.fillRect( width - underlineHeight, 0, underlineHeight, height );
		}
	}

	protected Color deriveBackground( Color background ) {
		if( !(background instanceof DerivedColor) )
			return background;

		Color baseColor = menuItem.isOpaque()
			? menuItem.getBackground()
			: FlatUIUtils.getParentBackground( menuItem );

		return FlatUIUtils.deriveColor( background, baseColor );
	}

	protected void paintIcon( Graphics g, Rectangle iconRect, Icon icon, Color checkBackground, Color selectionBackground ) {
		// if checkbox/radiobutton menu item is selected and also has a custom icon,
		// then use filled icon background to indicate selection (instead of using checkIcon)
		if( menuItem.isSelected() && checkIcon != null && icon != checkIcon ) {
			Rectangle r = FlatUIUtils.addInsets( iconRect, scale( checkMargins ) );
			g.setColor( FlatUIUtils.deriveColor( checkBackground, selectionBackground ) );
			g.fillRect( r.x, r.y, r.width, r.height );
		}

		paintIcon( g, menuItem, icon, iconRect );
	}

	protected void paintText( Graphics g, Rectangle textRect, String text, Color selectionForeground, Color disabledForeground ) {
		View htmlView = (View) menuItem.getClientProperty( BasicHTML.propertyKey );
		if( htmlView != null ) {
			paintHTMLText( g, menuItem, textRect, htmlView, isUnderlineSelection() ? null : selectionForeground );
			return;
		}

		int mnemonicIndex = FlatLaf.isShowMnemonics() ? menuItem.getDisplayedMnemonicIndex() : -1;
		boolean isTopLevelMenu = isTopLevelMenu( menuItem );
		Color foreground = (isTopLevelMenu ? menuItem.getParent() : menuItem).getForeground();

		paintText( g, menuItem, textRect, text, mnemonicIndex, isTopLevelMenu ? getTopLevelFont() : menuItem.getFont(),
			foreground, isUnderlineSelection() ? foreground : selectionForeground, disabledForeground );
	}

	protected void paintAccelerator( Graphics g, Rectangle accelRect, String accelText,
		Color foreground, Color selectionForeground, Color disabledForeground )
	{
		paintText( g, menuItem, accelRect, accelText, -1, acceleratorFont,
			foreground, isUnderlineSelection() ? foreground : selectionForeground, disabledForeground );
	}

	protected void paintArrowIcon( Graphics g, Rectangle arrowRect, Icon arrowIcon ) {
		paintIcon( g, menuItem, arrowIcon, arrowRect );
	}

	protected static void paintIcon( Graphics g, JMenuItem menuItem, Icon icon, Rectangle iconRect ) {
		if( icon == null )
			return;

		// center because the real icon may be smaller than dimension in iconRect
		int x = iconRect.x + centerOffset( iconRect.width, icon.getIconWidth() );
		int y = iconRect.y + centerOffset( iconRect.height, icon.getIconHeight() );

		// paint
		icon.paintIcon( menuItem, g, x, y );
	}

	protected static void paintText( Graphics g, JMenuItem menuItem,
		Rectangle textRect, String text, int mnemonicIndex, Font font,
		Color foreground, Color selectionForeground, Color disabledForeground )
	{
		if( text == null || text.isEmpty() )
			return;

		FontMetrics fm = menuItem.getFontMetrics( font );

		Font oldFont = g.getFont();
		g.setFont( font );
		g.setColor( !menuItem.isEnabled()
			? disabledForeground
			: (isArmedOrSelected( menuItem )
				? selectionForeground
				: foreground) );

		FlatUIUtils.drawStringUnderlineCharAt( menuItem, g, text, mnemonicIndex,
			textRect.x, textRect.y + fm.getAscent() );

		g.setFont( oldFont );
	}

	protected static void paintHTMLText( Graphics g, JMenuItem menuItem,
		Rectangle textRect, View htmlView, Color selectionForeground )
	{
		// On Windows, using Segoe UI font, Java 15 or older and scaling greater than 1,
		// the width of the HTML view may be initially too small (because component
		// is not connected to a GraphicsConfiguration when getPreferredSize() is invoked).
		// Since Java 16, this seems to be fixed somehow by doing popup menu layout twice.
		//
		// If using a too small width for htmlView.paint(), the view would rearrange
		// its children and wrap them to two lines. To avoid this, use view preferred X span
		// for painting. Core Lafs actually do the same in class MenuItemLayoutHelper.
		//
		// Example HTML text that causes the problem: "<html>some <b>HTML</b> <i>text</i></html>"
		textRect = new Rectangle( textRect );
		textRect.width = (int) htmlView.getPreferredSpan( View.X_AXIS );

		if( isArmedOrSelected( menuItem ) && selectionForeground != null )
			g = new GraphicsProxyWithTextColor( (Graphics2D) g, selectionForeground );

		htmlView.paint( HiDPIUtils.createGraphicsTextYCorrection( (Graphics2D) g ), textRect );
	}

	/**
	 * Returns {@code true} if either the menu item is armed (mouse over item)
	 * or it is a {@code JMenu} and selected (shows submenu).
	 */
	protected static boolean isArmedOrSelected( JMenuItem menuItem ) {
		return menuItem.isArmed() || (menuItem instanceof JMenu && menuItem.isSelected());
	}

	protected static boolean isTopLevelMenu( JMenuItem menuItem ) {
		return menuItem instanceof JMenu && ((JMenu)menuItem).isTopLevelMenu();
	}

	protected boolean isUnderlineSelection() {
		return "underline".equals( UIManager.getString( "MenuItem.selectionType" ) );
	}

	private Font getTopLevelFont() {
		Font font = menuItem.getFont();
		// menu item parent may be null if JMenu.isTopLevelMenu() is overridden
		// and does not check parent (e.g. com.jidesoft.swing.JideMenu.isTopLevelMenu())
		return (font != menuFont || menuItem.getParent() == null)
			? font
			: menuItem.getParent().getFont();
	}

	private Icon getIconForPainting() {
		Icon icon = menuItem.getIcon();

		if( icon == null && checkIcon != null && !isTopLevelMenu( menuItem ) )
			return checkIcon;

		if( icon == null )
			return null;

		if( !menuItem.isEnabled() )
			return menuItem.getDisabledIcon();

		if( menuItem.getModel().isPressed() && menuItem.isArmed() ) {
			Icon pressedIcon = menuItem.getPressedIcon();
			if( pressedIcon != null )
				return pressedIcon;
		}

		if( isArmedOrSelected( menuItem ) ) {
			Icon selectedIcon = menuItem.getSelectedIcon();
			if( selectedIcon != null )
				return selectedIcon;
		}

		return icon;
	}

	private Icon getIconForLayout() {
		Icon icon = menuItem.getIcon();

		if( isTopLevelMenu( menuItem ) )
			return (icon != null) ? new MinSizeIcon( icon ) : null;

		return new MinSizeIcon( (icon != null) ? icon : checkIcon );
	}

	private KeyStroke cachedAccelerator;
	private String cachedAcceleratorText;
	private boolean cachedAcceleratorLeftToRight;

	private String getAcceleratorText() {
		KeyStroke accelerator = menuItem.getAccelerator();
		if( accelerator == null )
			return null;

		boolean leftToRight = menuItem.getComponentOrientation().isLeftToRight();

		if( accelerator == cachedAccelerator && leftToRight == cachedAcceleratorLeftToRight )
			return cachedAcceleratorText;

		cachedAccelerator = accelerator;
		cachedAcceleratorText = getTextForAccelerator( accelerator );
		cachedAcceleratorLeftToRight = leftToRight;

		return cachedAcceleratorText;
	}

	protected String getTextForAccelerator( KeyStroke accelerator ) {
		StringBuilder buf = new StringBuilder();
		boolean leftToRight = menuItem.getComponentOrientation().isLeftToRight();

		// modifiers
		int modifiers = accelerator.getModifiers();
		if( modifiers != 0 ) {
			if( SystemInfo.isMacOS ) {
				if( leftToRight )
					buf.append( getMacOSModifiersExText( modifiers, leftToRight ) );
			} else
				buf.append( InputEvent.getModifiersExText( modifiers ) ).append( acceleratorDelimiter );
		}

		// key
		int keyCode = accelerator.getKeyCode();
		if( keyCode != 0 )
			buf.append( KeyEvent.getKeyText( keyCode ) );
		else
			buf.append( accelerator.getKeyChar() );

		// modifiers if right-to-left on macOS
		if( modifiers != 0 && !leftToRight && SystemInfo.isMacOS )
			buf.append( getMacOSModifiersExText( modifiers, leftToRight ) );

		return buf.toString();
	}

	protected String getMacOSModifiersExText( int modifiers, boolean leftToRight ) {
		StringBuilder buf = new StringBuilder();

		if( (modifiers & InputEvent.CTRL_DOWN_MASK) != 0 )
			buf.append( controlGlyph );
		if( (modifiers & (InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK)) != 0 )
			buf.append( optionGlyph );
		if( (modifiers & InputEvent.SHIFT_DOWN_MASK) != 0 )
			buf.append( shiftGlyph );
		if( (modifiers & InputEvent.META_DOWN_MASK) != 0 )
			buf.append( commandGlyph );

		// reverse order for right-to-left
		if( !leftToRight )
			buf.reverse();

		return buf.toString();
	}

	private static final char
		controlGlyph = 0x2303,
		optionGlyph = 0x2325,
		shiftGlyph = 0x21E7,
		commandGlyph = 0x2318;

	/**
	 * Calculates the maximum width of all menu item icons in the popup.
	 */
	private int getMaxIconsWidth() {
		if( !verticallyAlignText )
			return 0;

		Container parent = menuItem.getParent();
		if( !(parent instanceof JComponent) )
			return 0;

		int maxWidth = FlatClientProperties.clientPropertyInt( (JComponent) parent, KEY_MAX_ICONS_WIDTH, -1 );
		if( maxWidth >= 0 )
			return maxWidth;

		maxWidth = 0;

		for( Component c : parent.getComponents() ) {
			if( !(c instanceof JMenuItem) )
				continue;

			Icon icon = ((JMenuItem)c).getIcon();
			if( icon != null )
				maxWidth = Math.max( maxWidth, icon.getIconWidth() );
		}

		((JComponent)parent).putClientProperty( KEY_MAX_ICONS_WIDTH, maxWidth );
		return maxWidth;
	}

	static void clearClientProperties( Component c ) {
		if( !(c instanceof JComponent) )
			return;

		JComponent jc = (JComponent) c;
		jc.putClientProperty( FlatMenuItemRenderer.KEY_MAX_ICONS_WIDTH, null );
	}

	//---- class MinSizeIcon --------------------------------------------------

	private class MinSizeIcon
		implements Icon
	{
		private final Icon delegate;

		MinSizeIcon( Icon delegate ) {
			this.delegate = delegate;
		}

		@Override
		public int getIconWidth() {
			int iconWidth = (delegate != null) ? delegate.getIconWidth() : 0;
			iconWidth = Math.max( iconWidth, getMaxIconsWidth() );
			return Math.max( iconWidth, scale( minimumIconSize.width ) );
		}

		@Override
		public int getIconHeight() {
			int iconHeight = (delegate != null) ? delegate.getIconHeight() : 0;
			return Math.max( iconHeight, scale( minimumIconSize.height ) );
		}

		@Override
		public void paintIcon( Component c, Graphics g, int x, int y ) {
		}
	}

	//---- class GraphicsProxyWithTextColor -----------------------------------

	private static class GraphicsProxyWithTextColor
		extends Graphics2DProxy
	{
		private final Color textColor;

		GraphicsProxyWithTextColor( Graphics2D delegate, Color textColor ) {
			super( delegate );
			this.textColor = textColor;
		}

		@Override
		public void drawString( String str, int x, int y ) {
			Paint oldPaint = getPaint();
			setPaint( textColor );
			super.drawString( str, x, y );
			setPaint( oldPaint );
		}

		@Override
		public void drawString( String str, float x, float y ) {
			Paint oldPaint = getPaint();
			setPaint( textColor );
			super.drawString( str, x, y );
			setPaint( oldPaint );
		}

		@Override
		public void drawString( AttributedCharacterIterator iterator, int x, int y ) {
			Paint oldPaint = getPaint();
			setPaint( textColor );
			super.drawString( iterator, x, y );
			setPaint( oldPaint );
		}

		@Override
		public void drawString( AttributedCharacterIterator iterator, float x, float y ) {
			Paint oldPaint = getPaint();
			setPaint( textColor );
			super.drawString( iterator, x, y );
			setPaint( oldPaint );
		}

		@Override
		public void drawChars( char[] data, int offset, int length, int x, int y ) {
			Paint oldPaint = getPaint();
			setPaint( textColor );
			super.drawChars( data, offset, length, x, y );
			setPaint( oldPaint );
		}
	}
}
