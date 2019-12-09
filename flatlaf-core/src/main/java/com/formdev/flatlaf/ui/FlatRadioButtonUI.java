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
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicRadioButtonUI;

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

	@Override
	public void paint( Graphics g, JComponent c ) {
		// fill background even if opaque if
		// - used as cell renderer (because of selection background)
		// - if background was explicitly set to a non-UIResource color
		if( !c.isOpaque() &&
			(c.getParent() instanceof CellRendererPane || !(c.getBackground() instanceof UIResource)) )
		{
			g.setColor( c.getBackground() );
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
		}

		super.paint( g, c );
	}

	@Override
	protected void paintText( Graphics g, AbstractButton b, Rectangle textRect, String text ) {
		FlatButtonUI.paintText( g, b, textRect, text, b.isEnabled() ? b.getForeground() : disabledText );
	}
}
