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

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicViewportUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JViewport}.
 *
 * <!-- BasicViewportUI -->
 *
 * @uiDefault Viewport.font					Font	unused
 * @uiDefault Viewport.background			Color
 * @uiDefault Viewport.foreground			Color	unused
 *
 * @author Karl Tauber
 */
public class FlatViewportUI
	extends BasicViewportUI
{
	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatViewportUI();
		return instance;
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		Component view = ((JViewport)c).getView();
		if( c.isOpaque() && view instanceof JTable ) {
			// paint viewport background in same color as table background
			g.setColor( view.getBackground() );
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );

			paint( g, c );
		} else
			super.update( g, c );
	}
}
