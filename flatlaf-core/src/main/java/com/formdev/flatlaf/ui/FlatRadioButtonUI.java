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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import com.formdev.flatlaf.icons.FlatCheckBoxIcon;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JRadioButton}.
 *
 * <!-- BasicRadioButtonUI -->
 *
 * @uiDefault RadioButton.font						Font
 * @uiDefault RadioButton.background				Color
 * @uiDefault RadioButton.foreground				Color
 * @uiDefault RadioButton.border					Border
 * @uiDefault RadioButton.margin					Insets
 * @uiDefault RadioButton.rollover					boolean
 * @uiDefault RadioButton.icon						Icon
 *
 * <!-- FlatRadioButtonUI -->
 *
 * @uiDefault RadioButton.iconTextGap				int
 * @uiDefault RadioButton.disabledText				Color
 *
 * @author Karl Tauber
 */
public class FlatRadioButtonUI
	extends BasicRadioButtonUI
{
	protected int iconTextGap;
	protected Color disabledText;

	private boolean defaults_initialized = false;

	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatRadioButtonUI();
		return instance;
	}

	@Override
	public void installDefaults( AbstractButton b ) {
		super.installDefaults( b );

		if( !defaults_initialized ) {
			String prefix = getPropertyPrefix();

			iconTextGap = FlatUIUtils.getUIInt( prefix + "iconTextGap", 4 );
			disabledText = UIManager.getColor( prefix + "disabledText" );

			defaults_initialized = true;
		}

		LookAndFeel.installProperty( b, "opaque", false );
		LookAndFeel.installProperty( b, "iconTextGap", scale( iconTextGap ) );

		MigLayoutVisualPadding.install( b, null );
	}

	@Override
	protected void uninstallDefaults( AbstractButton b ) {
		super.uninstallDefaults( b );

		MigLayoutVisualPadding.uninstall( b );
		defaults_initialized = false;
	}

	private static Insets tempInsets = new Insets( 0, 0, 0, 0 );

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		Dimension size = super.getPreferredSize( c );
		if( size == null )
			return null;

		// small insets fix
		int focusWidth = getIconFocusWidth( c );
		if( focusWidth > 0 ) {
			// Increase preferred width and height if insets were explicitly reduced (e.g. with
			// an EmptyBorder) and icon has a focus width, which is not included in icon size.
			// Otherwise the component may be too small and outer focus border may be cut off.
			Insets insets = c.getInsets( tempInsets );
			size.width += Math.max( focusWidth - insets.left, 0 ) + Math.max( focusWidth - insets.right, 0 );
			size.height += Math.max( focusWidth - insets.top, 0 ) + Math.max( focusWidth - insets.bottom, 0 );
		}

		return size;
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		// fill background even if not opaque if
		// - contentAreaFilled is true and
		// - if background was explicitly set to a non-UIResource color
		if( !c.isOpaque() &&
			((AbstractButton)c).isContentAreaFilled() &&
			!(c.getBackground() instanceof UIResource) )
		{
			g.setColor( c.getBackground() );
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
		}

		// small insets fix
		int focusWidth = getIconFocusWidth( c );
		if( focusWidth > 0 ) {
			boolean ltr = c.getComponentOrientation().isLeftToRight();
			Insets insets = c.getInsets( tempInsets );
			int leftOrRightInset = ltr ? insets.left : insets.right;
			if( focusWidth > leftOrRightInset ) {
				// The left (or right) inset is smaller than the focus width, which may be
				// the case if insets were explicitly reduced (e.g. with an EmptyBorder).
				// In this case the width has been increased in getPreferredSize() and
				// here it is necessary to fix icon and text painting location.
				int offset = focusWidth - leftOrRightInset;
				if( !ltr )
					offset = -offset;

				// move the graphics origin to the left (or right)
				g.translate( offset, 0 );
				super.paint( g, c );
				g.translate( -offset, 0 );
				return;
			}
		}

		super.paint( FlatLabelUI.createGraphicsHTMLTextYCorrection( g, c ), c );
	}

	@Override
	protected void paintText( Graphics g, AbstractButton b, Rectangle textRect, String text ) {
		FlatButtonUI.paintText( g, b, textRect, text, b.isEnabled() ? b.getForeground() : disabledText );
	}

	private int getIconFocusWidth( JComponent c ) {
		AbstractButton b = (AbstractButton) c;
		return (b.getIcon() == null && getDefaultIcon() instanceof FlatCheckBoxIcon)
			? UIScale.scale( ((FlatCheckBoxIcon)getDefaultIcon()).focusWidth )
			: 0;
	}
}
