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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JSeparator}.
 *
 * @author Karl Tauber
 */
public class FlatSeparatorUI
	extends BasicSeparatorUI
{
	private static final int WIDTH = 2;

	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatSeparatorUI();
		return instance;
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		g.setColor( c.getForeground() );

		if( ((JSeparator)c).getOrientation() == JSeparator.VERTICAL )
			((Graphics2D)g).fill( new Rectangle2D.Float( 0, 0, scale( (float) WIDTH ), c.getHeight() ) );
		else
			((Graphics2D)g).fill( new Rectangle2D.Float( 0, 0, c.getWidth(), scale( (float) WIDTH ) ) );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		if( ((JSeparator) c).getOrientation() == JSeparator.VERTICAL )
			return new Dimension( scale( WIDTH ), 0 );
		else
			return new Dimension( 0, scale( WIDTH ) );
	}
}
