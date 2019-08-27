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
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTextArea}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault ComboBox.disabledBackground		Color
 * @uiDefault ComboBox.inactiveBackground		Color
 *
 * @author Karl Tauber
 */
public class FlatTextAreaUI
	extends BasicTextAreaUI
{
	protected Color disabledBackground;
	protected Color inactiveBackground;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTextAreaUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		disabledBackground = UIManager.getColor( "TextArea.disabledBackground" );
		inactiveBackground = UIManager.getColor( "TextArea.inactiveBackground" );
	}

	@Override
	protected void paintBackground( Graphics g ) {
		JTextComponent c = getComponent();

		Color background = c.getBackground();
		g.setColor( !(background instanceof UIResource)
			? background
			: (!c.isEnabled()
				? disabledBackground
				: (!c.isEditable() ? inactiveBackground : background)) );
		g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
	}
}
