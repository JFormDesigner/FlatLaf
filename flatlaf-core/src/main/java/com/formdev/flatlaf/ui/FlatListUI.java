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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JList}.
 *
 * <!-- BasicListUI -->
 *
 * @uiDefault List.font								Font
 * @uiDefault List.background						Color
 * @uiDefault List.foreground						Color
 * @uiDefault List.selectionBackground				Color
 * @uiDefault List.selectionForeground				Color
 * @uiDefault List.dropLineColor					Color
 * @uiDefault List.border							Border
 * @uiDefault List.cellRenderer						ListCellRenderer
 * @uiDefault FileChooser.listFont					Font		used if client property List.isFileList is true
 *
 * <!-- DefaultListCellRenderer -->
 *
 * @uiDefault List.cellNoFocusBorder				Border
 * @uiDefault List.focusCellHighlightBorder			Border
 * @uiDefault List.focusSelectedCellHighlightBorder	Border
 * @uiDefault List.dropCellBackground				Color
 * @uiDefault List.dropCellForeground				Color
 *
 * <!-- FlatListUI -->
 *
 * @uiDefault List.selectionInactiveBackground		Color
 * @uiDefault List.selectionInactiveForeground		Color
 *
 * <!-- FlatListCellBorder -->
 *
 * @uiDefault List.cellMargins						Insets
 * @uiDefault List.cellFocusColor					Color
 * @uiDefault List.showCellFocusIndicator			boolean
 *
 * @author Karl Tauber
 */
public class FlatListUI
	extends BasicListUI
{
	protected Color selectionBackground;
	protected Color selectionForeground;
	protected Color selectionInactiveBackground;
	protected Color selectionInactiveForeground;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatListUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		selectionBackground = UIManager.getColor( "List.selectionBackground" );
		selectionForeground = UIManager.getColor( "List.selectionForeground" );
		selectionInactiveBackground = UIManager.getColor( "List.selectionInactiveBackground" );
		selectionInactiveForeground = UIManager.getColor( "List.selectionInactiveForeground" );

		toggleSelectionColors();
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		selectionBackground = null;
		selectionForeground = null;
		selectionInactiveBackground = null;
		selectionInactiveForeground = null;
	}

	@Override
	protected FocusListener createFocusListener() {
		return new BasicListUI.FocusHandler() {
			@Override
			public void focusGained( FocusEvent e ) {
				super.focusGained( e );
				toggleSelectionColors();
			}

			@Override
			public void focusLost( FocusEvent e ) {
				super.focusLost( e );
				toggleSelectionColors();
			}
		};
	}

	/**
	 * Toggle selection colors from focused to inactive and vice versa.
	 *
	 * This is not a optimal solution but much easier than rewriting the whole paint methods.
	 *
	 * Using a LaF specific renderer was avoided because often a custom renderer is
	 * already used in applications. Then either the inactive colors are not used,
	 * or the application has to be changed to extend a FlatLaf renderer.
	 */
	private void toggleSelectionColors() {
		if( FlatUIUtils.isPermanentFocusOwner( list ) ) {
			if( list.getSelectionBackground() == selectionInactiveBackground )
				list.setSelectionBackground( selectionBackground );
			if( list.getSelectionForeground() == selectionInactiveForeground )
				list.setSelectionForeground( selectionForeground );
		} else {
			if( list.getSelectionBackground() == selectionBackground )
				list.setSelectionBackground( selectionInactiveBackground );
			if( list.getSelectionForeground() == selectionForeground )
				list.setSelectionForeground( selectionInactiveForeground );
		}
	}
}
