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
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JLabel}.
 *
 * @uiDefault Label.background			Color	only used if opaque
 * @uiDefault Label.foreground			Color
 * @uiDefault Label.disabledForeground	Color
 * @uiDefault Label.font				Font
 *
 * @author Karl Tauber
 */
public class FlatLabelUI
	extends BasicLabelUI
{
	private Color disabledForeground;

	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatLabelUI();
		return instance;
	}

	@Override
	protected void installDefaults( JLabel c ) {
		super.installDefaults( c );

		disabledForeground = UIManager.getColor( "Label.disabledForeground" );
	}

	@Override
	protected void paintDisabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
		int mnemIndex = l.getDisplayedMnemonicIndex();
		g.setColor( disabledForeground );
		FlatUIUtils.drawStringUnderlineCharAt( l, g, s, mnemIndex, textX, textY );
	}
}
