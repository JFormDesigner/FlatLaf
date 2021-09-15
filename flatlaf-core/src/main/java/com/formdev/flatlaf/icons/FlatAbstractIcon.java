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

package com.formdev.flatlaf.icons;

import static com.formdev.flatlaf.util.UIScale.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * Base class for icons that scales width and height, creates and initializes
 * a scaled graphics context for icon painting.
 *
 * Subclasses do not need to scale icon painting.
 *
 * @author Karl Tauber
 */
public abstract class FlatAbstractIcon
	implements Icon, UIResource
{
	protected final int width;
	protected final int height;
	protected Color color;

	public FlatAbstractIcon( int width, int height, Color color ) {
		this.width = width;
		this.height = height;
		this.color = color;
	}

	@Override
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			// for testing
//			g2.setColor( Color.blue );
//			g2.drawRect( x, y, getIconWidth() - 1, getIconHeight() - 1 );

			g2.translate( x, y );
			UIScale.scaleGraphics( g2 );

			if( color != null )
				g2.setColor( color );

			paintIcon( c, g2 );
		} finally {
			g2.dispose();
		}
	}

	protected abstract void paintIcon( Component c, Graphics2D g2 );

	@Override
	public int getIconWidth() {
		return scale( width );
	}

	@Override
	public int getIconHeight() {
		return scale( height );
	}
}
