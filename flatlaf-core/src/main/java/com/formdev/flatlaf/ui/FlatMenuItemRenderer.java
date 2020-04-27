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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import com.formdev.flatlaf.FlatLaf;

/**
 * Renderer for menu items.
 *
 * @author Karl Tauber
 */
public class FlatMenuItemRenderer
{
	protected final JMenuItem menuItem;
	protected final Icon checkIcon;
	protected final Icon arrowIcon;
	protected final Font acceleratorFont;
	protected final String acceleratorDelimiter;

	protected final int minimumWidth;
	protected final Dimension minimumIconSize;
	protected final int textAcceleratorGap;
	protected final int textArrowGap;

	protected FlatMenuItemRenderer( JMenuItem menuItem, Icon checkIcon, Icon arrowIcon,
		Font acceleratorFont, String acceleratorDelimiter )
	{
		this.menuItem = menuItem;
		this.checkIcon = checkIcon;
		this.arrowIcon = arrowIcon;
		this.acceleratorFont = acceleratorFont;
		this.acceleratorDelimiter = acceleratorDelimiter;

		minimumWidth = UIManager.getInt( "MenuItem.minimumWidth" );
		Dimension minimumIconSize = UIManager.getDimension( "MenuItem.minimumIconSize" );
		this.minimumIconSize = (minimumIconSize != null) ? minimumIconSize : new Dimension( 16, 16 );
		this.textAcceleratorGap = FlatUIUtils.getUIInt( "MenuItem.textAcceleratorGap", 28 );
		this.textArrowGap = FlatUIUtils.getUIInt( "MenuItem.textArrowGap", 8 );
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
			menuItem.getFontMetrics( menuItem.getFont() ), menuItem.getText(), getIconForLayout(),
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
			width += scale( textAcceleratorGap );

			FontMetrics accelFm = menuItem.getFontMetrics( acceleratorFont );
			width += SwingUtilities.computeStringWidth( accelFm, accelText );
			height = Math.max( accelFm.getHeight(), height );
		}

		// arrow size
		if( !isTopLevelMenu && arrowIcon != null ) {
			// gap between text and arrow
			if( accelText == null )
				width += scale( textArrowGap );

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
		Rectangle accelRect, Rectangle arrowRect )
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
		if( menuItem.getComponentOrientation().isLeftToRight() ) {
			// left-to-right
			arrowRect.x = viewRect.x + viewRect.width - arrowRect.width;
			accelRect.x = arrowRect.x - accelRect.width;
		} else {
			// right-to-left
			arrowRect.x = viewRect.x;
			accelRect.x = arrowRect.x + arrowRect.width;
		}

		// width of accelerator, arrow and gap
		int accelArrowWidth = accelRect.width + arrowRect.width;
		if( accelText != null )
			accelArrowWidth += scale( textAcceleratorGap );
		else if( !isTopLevelMenu && arrowIcon != null )
			accelArrowWidth += scale( textArrowGap );

		// label rectangle is view rectangle subtracted by accelerator, arrow and gap
		Rectangle labelRect = new Rectangle( viewRect );
		labelRect.width -= accelArrowWidth;
		if( !menuItem.getComponentOrientation().isLeftToRight() )
			labelRect.x += accelArrowWidth;

		// layout icon and text
		SwingUtilities.layoutCompoundLabel( menuItem,
			menuItem.getFontMetrics( menuItem.getFont() ), menuItem.getText(), getIconForLayout(),
			menuItem.getVerticalAlignment(), menuItem.getHorizontalAlignment(),
			menuItem.getVerticalTextPosition(), menuItem.getHorizontalTextPosition(),
			labelRect, iconRect, textRect, scale( menuItem.getIconTextGap() ) );
	}

	private static int centerOffset( int wh1, int wh2 ) {
		return (wh1 != wh2) ? Math.round( (wh1 - wh2) / 2f ) : 0;
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

		layout( viewRect, iconRect, textRect, accelRect, arrowRect );

/*debug
		g.setColor( Color.red ); g.drawRect( viewRect.x, viewRect.y, viewRect.width - 1, viewRect.height - 1 );
		g.setColor( Color.blue ); g.drawRect( iconRect.x, iconRect.y, iconRect.width - 1, iconRect.height - 1 );
		g.setColor( Color.cyan ); g.drawRect( textRect.x, textRect.y, textRect.width - 1, textRect.height - 1 );
		g.setColor( Color.magenta ); g.drawRect( accelRect.x, accelRect.y, accelRect.width - 1, accelRect.height - 1 );
		g.setColor( Color.orange ); g.drawRect( arrowRect.x, arrowRect.y, arrowRect.width - 1, arrowRect.height - 1 );
debug*/

		paintBackground( g, selectionBackground );
		paintIcon( g, iconRect, getIconForPainting() );
		paintText( g, textRect, menuItem.getText(), selectionForeground, disabledForeground );
		paintAccelerator( g, accelRect, getAcceleratorText(), acceleratorForeground, acceleratorSelectionForeground, disabledForeground );
		if( !isTopLevelMenu( menuItem ) )
			paintArrowIcon( g, arrowRect, arrowIcon );
	}

	protected void paintBackground( Graphics g, Color selectionBackground ) {
		boolean armedOrSelected = isArmedOrSelected( menuItem );
		if( menuItem.isOpaque() || armedOrSelected ) {
			g.setColor( armedOrSelected ? selectionBackground : menuItem.getBackground() );
			g.fillRect( 0, 0, menuItem.getWidth(), menuItem.getHeight() );
		}
	}

	protected void paintIcon( Graphics g, Rectangle iconRect, Icon icon ) {
		paintIcon( g, menuItem, icon, iconRect );
	}

	protected void paintText( Graphics g, Rectangle textRect, String text, Color selectionForeground, Color disabledForeground ) {
		View htmlView = (View) menuItem.getClientProperty( BasicHTML.propertyKey );
		if( htmlView != null ) {
			htmlView.paint( g, textRect );
			return;
		}

		int mnemonicIndex = FlatLaf.isShowMnemonics() ? menuItem.getDisplayedMnemonicIndex() : -1;

		paintText( g, menuItem, textRect, text, mnemonicIndex, menuItem.getFont(),
			menuItem.getForeground(), selectionForeground, disabledForeground );
	}

	protected void paintAccelerator( Graphics g, Rectangle accelRect, String accelText,
		Color foreground, Color selectionForeground, Color disabledForeground )
	{
		paintText( g, menuItem, accelRect, accelText, -1, acceleratorFont,
			foreground, selectionForeground, disabledForeground );
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

	protected static boolean isArmedOrSelected( JMenuItem menuItem ) {
		return menuItem.isArmed() || (menuItem instanceof JMenu && menuItem.isSelected());
	}

	protected static boolean isTopLevelMenu( JMenuItem menuItem ) {
		return menuItem instanceof JMenu && ((JMenu)menuItem).isTopLevelMenu();
	}

	private Icon getIconForPainting() {
		if( checkIcon != null && !isTopLevelMenu( menuItem ) )
			return checkIcon;

		Icon icon = menuItem.getIcon();
		if( icon == null )
			return null;

		if( !menuItem.isEnabled() )
			return menuItem.getDisabledIcon();

		if( menuItem.getModel().isPressed() && menuItem.isArmed() ) {
			Icon pressedIcon = menuItem.getPressedIcon();
			if( pressedIcon != null )
				return pressedIcon;
		}

		return icon;
	}

	private Icon getIconForLayout() {
		if( isTopLevelMenu( menuItem ) ) {
			Icon icon = menuItem.getIcon();
			return (icon != null) ? new MinSizeIcon( icon ) : null;
		}

		return new MinSizeIcon( (checkIcon != null) ? checkIcon : menuItem.getIcon() );
	}

	private KeyStroke cachedAccelerator;
	private String cachedAcceleratorText;

	private String getAcceleratorText() {
		KeyStroke accelerator = menuItem.getAccelerator();
		if( accelerator == null )
			return null;

		if( accelerator == cachedAccelerator )
			return cachedAcceleratorText;

		StringBuilder buf = new StringBuilder();
		int modifiers = accelerator.getModifiers();
		if( modifiers != 0 )
			buf.append( InputEvent.getModifiersExText( modifiers ) ).append( acceleratorDelimiter );

		int keyCode = accelerator.getKeyCode();
		if( keyCode != 0 )
			buf.append( KeyEvent.getKeyText( keyCode ) );
		else
			buf.append( accelerator.getKeyChar() );

		cachedAccelerator = accelerator;
		cachedAcceleratorText = buf.toString();

		return cachedAcceleratorText;
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
}
