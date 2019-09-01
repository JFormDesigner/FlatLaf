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

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JButton}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault Component.focusWidth				int
 * @uiDefault Button.arc						int
 * @uiDefault Button.disabledText				Color
 * @uiDefault Button.default.background			Color
 * @uiDefault Button.default.foreground			Color
 *
 * @author Karl Tauber
 */
public class FlatButtonUI
	extends BasicButtonUI
{
	protected int focusWidth;
	protected int arc;

	protected Color disabledText;
	protected Color defaultBackground;
	protected Color defaultForeground;

	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatButtonUI();
		return instance;
	}

	@Override
	protected void installDefaults( AbstractButton b ) {
		super.installDefaults( b );

		focusWidth = UIManager.getInt( "Component.focusWidth" );
		arc = UIManager.getInt( "Button.arc" );

		disabledText = UIManager.getColor( "Button.disabledText" );
		defaultBackground = UIManager.getColor( "Button.default.background" );
		defaultForeground = UIManager.getColor( "Button.default.foreground" );
	}

	static boolean isContentAreaFilled( Component c ) {
		return !(c instanceof AbstractButton) || ((AbstractButton)c).isContentAreaFilled();
	}

	static boolean isDefaultButton( Component c ) {
		return c instanceof JButton && ((JButton)c).isDefaultButton();
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		if( c.isOpaque() && FlatButtonUI.isContentAreaFilled( c ) ) {
			FlatUIUtils.paintParentBackground( g, c );

			if( c.isEnabled() ) {
				Graphics2D g2 = (Graphics2D) g.create();
				try {
					FlatUIUtils.setRenderingHints( g2 );

					float focusWidth = (c.getBorder() instanceof FlatBorder) ? scale( (float) this.focusWidth ) : 0;
					float arc = (c.getBorder() instanceof FlatButtonBorder) ? scale( (float) this.arc ) : 0;

					g2.setColor( getBackground( c ) );
					FlatUIUtils.fillRoundRectangle( g2, 0, 0, c.getWidth(), c.getHeight(), focusWidth, arc );
				} finally {
					g2.dispose();
				}
			}
		}

		paint( g, c );
	}

	@Override
	protected void paintText( Graphics g, JComponent c, Rectangle textRect, String text ) {
		AbstractButton b = (AbstractButton) c;
		FontMetrics fm = c.getFontMetrics( c.getFont() );
		int mnemonicIndex = b.getDisplayedMnemonicIndex();

		g.setColor( b.getModel().isEnabled() ? getForeground( c ) : disabledText );
		FlatUIUtils.drawStringUnderlineCharAt( c, g, text, mnemonicIndex,
			textRect.x + getTextShiftOffset(),
			textRect.y + fm.getAscent() + getTextShiftOffset() );
	}

	private Color getBackground( Component c ) {
		boolean def = FlatButtonUI.isDefaultButton( c );
		return def ? defaultBackground : c.getBackground();
	}

	private Color getForeground( Component c ) {
		boolean def = FlatButtonUI.isDefaultButton( c );
		return def ? defaultForeground : c.getForeground();
	}
}
