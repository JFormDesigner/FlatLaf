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
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTree}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault Tree.border							Border
 * @uiDefault Tree.selectionBackground				Color
 * @uiDefault Tree.selectionForeground				Color
 * @uiDefault Tree.selectionInactiveBackground		Color
 * @uiDefault Tree.selectionInactiveForeground		Color
 *
 * @author Karl Tauber
 */
public class FlatTreeUI
	extends BasicTreeUI
{
	protected Color selectionBackground;
	protected Color selectionForeground;
	protected Color selectionInactiveBackground;
	protected Color selectionInactiveForeground;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTreeUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installBorder( tree, "Tree.border" );

		selectionBackground = UIManager.getColor( "Tree.selectionBackground" );
		selectionForeground = UIManager.getColor( "Tree.selectionForeground" );
		selectionInactiveBackground = UIManager.getColor( "Tree.selectionInactiveBackground" );
		selectionInactiveForeground = UIManager.getColor( "Tree.selectionInactiveForeground" );

		// scale
		int rowHeight = FlatUIUtils.getUIInt( "Tree.rowHeight", 16 );
		if( rowHeight > 0 )
			LookAndFeel.installProperty( tree, "rowHeight", UIScale.scale( rowHeight ) );
		setLeftChildIndent( UIScale.scale( getLeftChildIndent() ) );
		setRightChildIndent( UIScale.scale( getRightChildIndent() ) );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		LookAndFeel.uninstallBorder( tree );

		selectionBackground = null;
		selectionForeground = null;
		selectionInactiveBackground = null;
		selectionInactiveForeground = null;
	}

	/**
	 * Same as super.paintRow(), but uses inactive selection background/foreground if tree is not focused.
	 */
	@Override
	protected void paintRow( Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row,
		boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf )
	{
		if( editingComponent != null && editingRow == row )
			return;

		boolean hasFocus = tree.hasFocus();
		boolean cellHasFocus = hasFocus && (row == getLeadSelectionRow());
		boolean isSelected = tree.isRowSelected( row );

		// get renderer component
		Component rendererComponent = currentCellRenderer.getTreeCellRendererComponent( tree,
			path.getLastPathComponent(), isSelected, isExpanded, isLeaf, row, cellHasFocus );

		// apply inactive selection background/foreground if tree is not focused
		Color oldBackgroundSelectionColor = null;
		if( isSelected && !hasFocus ) {
			if( rendererComponent instanceof DefaultTreeCellRenderer ) {
				DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) rendererComponent;
				if( renderer.getBackgroundSelectionColor() == selectionBackground ) {
					oldBackgroundSelectionColor = renderer.getBackgroundSelectionColor();
					renderer.setBackgroundSelectionColor( selectionInactiveBackground );
				}
			} else {
				if( rendererComponent.getBackground() == selectionBackground )
					rendererComponent.setBackground( selectionInactiveBackground );
			}

			if( rendererComponent.getForeground() == selectionForeground )
				rendererComponent.setForeground( selectionInactiveForeground );
		}

		// paint renderer
		rendererPane.paintComponent( g, rendererComponent, tree, bounds.x, bounds.y, bounds.width, bounds.height, true );

		// restore background selection color
		if( oldBackgroundSelectionColor != null )
			((DefaultTreeCellRenderer)rendererComponent).setBackgroundSelectionColor( oldBackgroundSelectionColor );
	}
}
