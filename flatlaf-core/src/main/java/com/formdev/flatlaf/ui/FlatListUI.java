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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JList}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault List.selectionInactiveBackground		Color
 * @uiDefault List.selectionInactiveForeground		Color
 *
 * @author Karl Tauber
 */
public class FlatListUI
	extends BasicListUI
{
	protected Color selectionInactiveBackground;
	protected Color selectionInactiveForeground;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatListUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		selectionInactiveBackground = UIManager.getColor( "List.selectionInactiveBackground" );
		selectionInactiveForeground = UIManager.getColor( "List.selectionInactiveForeground" );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		selectionInactiveBackground = null;
		selectionInactiveForeground = null;
	}

	/**
	 * Same as super.paintCell(), but uses inactive selection background/foreground if list is not focused.
	 */
	@Override
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	protected void paintCell( Graphics g, int row, Rectangle rowBounds, ListCellRenderer cellRenderer,
		ListModel dataModel, ListSelectionModel selModel, int leadIndex )
	{
		Object value = dataModel.getElementAt( row );
		boolean hasFocus = list.hasFocus();
		boolean cellHasFocus = hasFocus && (row == leadIndex);
		boolean isSelected = selModel.isSelectedIndex( row );

		// get renderer component
		Component rendererComponent = cellRenderer.getListCellRendererComponent(
			list, value, row, isSelected, cellHasFocus );

		// apply inactive selection background/foreground if list is not focused
		if( isSelected && !hasFocus ) {
			if( rendererComponent.getBackground() == list.getSelectionBackground() )
				rendererComponent.setBackground( selectionInactiveBackground );
			if( rendererComponent.getForeground() == list.getSelectionForeground() )
				rendererComponent.setForeground( selectionInactiveForeground );
		}

		int x = rowBounds.x;
		int width = rowBounds.width;

		// reduce width to preferred width in JFileChooser
		if( Boolean.TRUE.equals( list.getClientProperty( "List.isFileList" ) ) ) {
			int w = Math.min( width, rendererComponent.getPreferredSize().width + 4 );
			if( !list.getComponentOrientation().isLeftToRight() )
				x += (width - w);
			width = w;
		}

		// paint renderer
		rendererPane.paintComponent( g, rendererComponent, list,
			x, rowBounds.y, width, rowBounds.height, true );
	}
}
