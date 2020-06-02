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

import java.awt.Color;
import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicRootPaneUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JRootPane}.
 *
 * @author Karl Tauber
 */
public class FlatRootPaneUI
	extends BasicRootPaneUI
{
	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatRootPaneUI();
		return instance;
	}

	@Override
	protected void installDefaults( JRootPane c ) {
		super.installDefaults( c );

		// Update background color of JFrame or JDialog parent to avoid bad border
		// on HiDPI screens when switching from light to dark Laf.
		// The background of JFrame is initialized in JFrame.frameInit() and
		// the background of JDialog in JDialog.dialogInit(),
		// but it was not updated when switching Laf.
		Container parent = c.getParent();
		if( parent instanceof JFrame || parent instanceof JDialog ) {
			Color background = parent.getBackground();
			if( background == null || background instanceof UIResource )
				parent.setBackground( UIManager.getColor( "control" ) );
		}
	}
}
