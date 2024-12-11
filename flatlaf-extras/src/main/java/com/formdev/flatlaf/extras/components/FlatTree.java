/*
 * Copyright 2021 FormDev Software GmbH
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

package com.formdev.flatlaf.extras.components;

import static com.formdev.flatlaf.FlatClientProperties.*;
import javax.swing.JTree;

/**
 * Subclass of {@link JTree} that provides easy access to FlatLaf specific client properties.
 *
 * @author Karl Tauber
 */
public class FlatTree
	extends JTree
	implements FlatComponentExtension, FlatStyleableComponent
{
	/**
	 * Returns whether tree shows a wide selection
	 */
	public boolean isWideSelection() {
		return getClientPropertyBoolean( TREE_WIDE_SELECTION, "Tree.wideSelection" );
	}

	/**
	 * Specifies whether tree shows a wide selection
	 */
	public void setWideSelection( boolean wideSelection ) {
		putClientProperty( TREE_WIDE_SELECTION, wideSelection );
	}

	/**
	 * Returns whether tree uses a wide cell renderer.
	 *
	 * @since 3.6
	 */
	public boolean isWideCellRenderer() {
		return getClientPropertyBoolean( TREE_WIDE_CELL_RENDERER, "Tree.wideCellRenderer" );
	}

	/**
	 * Specifies whether tree uses a wide cell renderer.
	 *
	 * @since 3.6
	 */
	public void setWideCellRenderer( boolean wideCellRenderer ) {
		putClientProperty( TREE_WIDE_CELL_RENDERER, wideCellRenderer );
	}

	/**
	 * Returns whether tree item selection is painted. Default is {@code true}.
	 * If set to {@code false}, then the tree cell renderer is responsible for painting selection.
	 */
	public boolean isPaintSelection() {
		return getClientPropertyBoolean( TREE_PAINT_SELECTION, true );
	}

	/**
	 * Specifies whether tree item selection is painted. Default is {@code true}.
	 * If set to {@code false}, then the tree cell renderer is responsible for painting selection.
	 */
	public void setPaintSelection( boolean paintSelection ) {
		putClientProperty( TREE_PAINT_SELECTION, paintSelection );
	}
}
