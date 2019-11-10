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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JLabel}.
 *
 * <!-- BasicLabelUI -->
 *
 * @uiDefault Label.font				Font
 * @uiDefault Label.background			Color	only used if opaque
 * @uiDefault Label.foreground			Color
 *
 * <!-- FlatLabelUI -->
 *
 * @uiDefault Label.disabledForeground	Color
 *
 * @author Karl Tauber
 */
public class FlatLabelUI
	extends BasicLabelUI
{
	private Color disabledForeground;

	private boolean defaults_initialized = false;

	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatLabelUI();
		return instance;
	}

	@Override
	protected void installDefaults( JLabel c ) {
		super.installDefaults( c );

		if( !defaults_initialized ) {
			disabledForeground = UIManager.getColor( "Label.disabledForeground" );

			defaults_initialized = true;
		}
	}

	@Override
	protected void uninstallDefaults( JLabel c ) {
		super.uninstallDefaults( c );
		defaults_initialized = false;
	}

	@Override
	protected void paintEnabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
		int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
		g.setColor( l.getForeground() );
		FlatUIUtils.drawStringUnderlineCharAt( l, g, s, mnemIndex, textX, textY );
	}

	@Override
	protected void paintDisabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
		int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
		g.setColor( disabledForeground );
		FlatUIUtils.drawStringUnderlineCharAt( l, g, s, mnemIndex, textX, textY );
	}

	/**
	 * Overridden to scale iconTextGap.
	 */
	@Override
	protected String layoutCL( JLabel label, FontMetrics fontMetrics, String text, Icon icon, Rectangle viewR,
		Rectangle iconR, Rectangle textR )
	{
		return SwingUtilities.layoutCompoundLabel( label, fontMetrics, text, icon,
			label.getVerticalAlignment(), label.getHorizontalAlignment(),
			label.getVerticalTextPosition(), label.getHorizontalTextPosition(),
			viewR, iconR, textR,
			UIScale.scale( label.getIconTextGap() ) );
	}
}
