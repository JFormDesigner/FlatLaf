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
import java.awt.Component;
import java.awt.Insets;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

/**
 * Border for {@link javax.swing.JMenu}, {@link javax.swing.JMenuItem},
 * {@link javax.swing.JCheckBoxMenuItem} and {@link javax.swing.JRadioButtonMenuItem}.
 *
 * @uiDefault MenuBar.itemMargins				Insets
 *
 * @author Karl Tauber
 */
public class FlatMenuItemBorder
	extends FlatMarginBorder
{
	private final Insets menuBarItemMargins = UIManager.getInsets( "MenuBar.itemMargins" );

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		if( c.getParent() instanceof JMenuBar ) {
			insets.top = scale( menuBarItemMargins.top );
			insets.left = scale( menuBarItemMargins.left );
			insets.bottom = scale( menuBarItemMargins.bottom + 1 );
			insets.right = scale( menuBarItemMargins.right );
			return insets;
		} else
			return super.getBorderInsets( c, insets );
	}
}
