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
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JToolBar.Separator}.
 *
 * <!-- FlatToolBarSeparatorUI -->
 *
 * @uiDefault ToolBar.separatorWidth				int
 * @uiDefault ToolBar.separatorColor				Color
 *
 * @author Karl Tauber
 */
public class FlatToolBarSeparatorUI
	extends BasicToolBarSeparatorUI
{
	private static final int LINE_WIDTH = 1;

	protected int separatorWidth;
	protected Color separatorColor;

	private boolean defaults_initialized = false;

	private static ComponentUI instance;

	public static ComponentUI createUI( JComponent c ) {
		if( instance == null )
			instance = new FlatToolBarSeparatorUI();
		return instance;
	}

	@Override
	protected void installDefaults( JSeparator c ) {
		super.installDefaults( c );

		if( !defaults_initialized ) {
			separatorWidth = UIManager.getInt( "ToolBar.separatorWidth" );
			separatorColor = UIManager.getColor( "ToolBar.separatorColor" );

			defaults_initialized = true;
		}

		// necessary for vertical toolbars if separator size was set using setSeparatorSize()
		// (otherwise there will be a gap on the left side of the vertical toolbar)
		c.setAlignmentX( 0 );
	}

	@Override
	protected void uninstallDefaults( JSeparator s ) {
		super.uninstallDefaults( s );
		defaults_initialized = false;
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		Dimension size = ((JToolBar.Separator)c).getSeparatorSize();

		if( size != null )
			return scale( size );

		// make sure that gap on left and right side of line have same size
		int sepWidth = (scale( (separatorWidth - LINE_WIDTH) / 2 ) * 2) + scale( LINE_WIDTH );

		boolean vertical = isVertical( c );
		return new Dimension( vertical ? sepWidth : 0, vertical ? 0 : sepWidth );
	}

	@Override
	public Dimension getMaximumSize( JComponent c ) {
		Dimension size = getPreferredSize( c );
		if( isVertical( c ) )
			return new Dimension( size.width, Short.MAX_VALUE );
		else
			return new Dimension( Short.MAX_VALUE, size.height );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		int width = c.getWidth();
		int height = c.getHeight();
		float lineWidth = scale( 1f );
		float offset = scale( 2f );

		FlatUIUtils.setRenderingHints( (Graphics2D) g );
		g.setColor( separatorColor );

		if( isVertical( c ) )
			((Graphics2D)g).fill( new Rectangle2D.Float( Math.round( (width - lineWidth) / 2f ), offset, lineWidth, height - (offset * 2) ) );
		else
			((Graphics2D)g).fill( new Rectangle2D.Float( offset, Math.round( (height - lineWidth) / 2f ), width - (offset * 2), lineWidth ) );
	}

	private boolean isVertical( JComponent c ) {
		return ((JToolBar.Separator)c).getOrientation() == SwingConstants.VERTICAL;
	}
}
