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

package com.formdev.flatlaf.jideoss.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PopupMenuUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicJideSplitButtonUI;
import com.jidesoft.swing.JideSplitButton;
import com.jidesoft.swing.JideSwingUtilities;

/**
 * Provides the Flat LaF UI delegate for {@link com.jidesoft.swing.JideSplitButton}.
 *
 * @author Karl Tauber
 * @since 1.1
 */
public class FlatJideSplitButtonUI
	extends BasicJideSplitButtonUI
{
	protected String arrowType;
	protected Color buttonArrowColor;
	protected Color buttonDisabledArrowColor;

	public static ComponentUI createUI( JComponent c ) {
		// usually JIDE would invoke this in JideSplitButton.updateUI(),
		// but it does not because FlatLaf already has added the UI class to the UI defaults
		LookAndFeelFactory.installJideExtension();

		// workaround for bug in JideSplitButton, which overrides JMenu.updateUI(),
		// but does not invoke super.updateUI() to update UI of JMenu.popupMenu field
		if( c instanceof JideSplitButton ) {
			JPopupMenu popupMenu = ((JideSplitButton)c).getPopupMenu();
			if( popupMenu != null )
				popupMenu.setUI( (PopupMenuUI) UIManager.getUI( popupMenu ) );
		}

		return new FlatJideSplitButtonUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		arrowType = UIManager.getString( "Component.arrowType" );
		buttonArrowColor = UIManager.getColor( "JideSplitButton.buttonArrowColor" );
		buttonDisabledArrowColor = UIManager.getColor( "JideSplitButton.buttonDisabledArrowColor" );
	}

	@Override
	protected int getRightMargin() {
		// scale margins
		_splitButtonMargin = UIScale.scale( 14 );
		_splitButtonMarginOnMenu = UIScale.scale( 20 );

		return super.getRightMargin();
	}

	@Override
	protected Rectangle getButtonRect( JComponent c, int orientation, int width, int height ) {
		return c.getComponentOrientation().isLeftToRight()
			? new Rectangle( 0, 0, width - _splitButtonMargin + 1, height )
			: new Rectangle( _splitButtonMargin - 1, 0, width - _splitButtonMargin + 1, height );
	}

	@Override
	protected Rectangle getDropDownRect( JComponent c, int orientation, int width, int height ) {
		return c.getComponentOrientation().isLeftToRight()
			? new Rectangle( width - _splitButtonMargin, 0, _splitButtonMargin, height )
			: new Rectangle( 0, 0, _splitButtonMargin, height );
	}

	@Override
	protected void paintText( Graphics g, JMenuItem menuItem, Rectangle textRect, String text ) {
		ButtonModel model = menuItem.getModel();
		if( !model.isEnabled() ||
			(menuItem instanceof JideSplitButton && !((JideSplitButton)menuItem).isButtonEnabled()) )
		{
			FontMetrics fm = menuItem.getFontMetrics( menuItem.getFont() );

			if( !menuItem.getComponentOrientation().isLeftToRight() &&
				menuItem.getComponentOrientation().isHorizontal() )
			{
				Rectangle2D rectText = fm.getStringBounds( text, g );
				textRect.x = (int) (menuItem.getWidth() - textRect.x - rectText.getWidth() + (4 + menuItem.getHeight() / 2 - 1));
			}

			g.setColor( UIDefaultsLookup.getColor( "Button.disabledForeground" ) );
			drawStringUnderlineCharAt( menuItem, g, text, -1, textRect.x, textRect.y + fm.getAscent() );
		} else
			super.paintText( g, menuItem, textRect, text );
	}

	@Override
	protected void paintArrow( JMenuItem menuItem, Graphics g ) {
		g.setColor( menuItem.isEnabled() ? buttonArrowColor : buttonDisabledArrowColor );

		int orientation = JideSwingUtilities.getOrientationOf( menuItem );
		int menuWidth = (orientation == SwingConstants.HORIZONTAL) ? menuItem.getWidth() : menuItem.getHeight();
		int menuHeight = (orientation == SwingConstants.HORIZONTAL) ? menuItem.getHeight() : menuItem.getWidth();
		Rectangle r = getDropDownRect( menuItem, orientation, menuWidth, menuHeight );

		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
		FlatUIUtils.paintArrow( (Graphics2D) g, r.x, r.y, r.width, r.height,
			SwingConstants.SOUTH, FlatUIUtils.isChevron( arrowType ), 6, 1, 0, 0 );
		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}
}
