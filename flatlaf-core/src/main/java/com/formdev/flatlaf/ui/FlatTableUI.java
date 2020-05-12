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
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTable}.
 *
 * <!-- BasicTableUI -->
 *
 * @uiDefault Table.font								Font
 * @uiDefault Table.background							Color
 * @uiDefault Table.foreground							Color
 * @uiDefault Table.selectionBackground					Color
 * @uiDefault Table.selectionForeground					Color
 * @uiDefault Table.gridColor							Color
 * @uiDefault Table.scrollPaneBorder					Border
 * @uiDefault Table.dropLineColor						Color
 * @uiDefault Table.dropLineShortColor					Color
 *
 * <!-- DefaultTableCellRenderer -->
 *
 * @uiDefault Table.cellNoFocusBorder					Border
 * @uiDefault Table.focusCellHighlightBorder			Border
 * @uiDefault Table.focusSelectedCellHighlightBorder	Border
 * @uiDefault Table.dropCellBackground					Color
 * @uiDefault Table.dropCellForeground					Color
 * @uiDefault Table.alternateRowColor					Color
 * @uiDefault Table.focusCellBackground					Color
 * @uiDefault Table.focusCellForeground					Color
 *
 * <!-- FlatTableUI -->
 *
 * @uiDefault Table.rowHeight							int
 * @uiDefault Table.showHorizontalLines					boolean
 * @uiDefault Table.showVerticalLines					boolean
 * @uiDefault Table.intercellSpacing					Dimension
 * @uiDefault Table.selectionInactiveBackground			Color
 * @uiDefault Table.selectionInactiveForeground			Color
 *
 * <!-- FlatTableCellBorder -->
 *
 * @uiDefault Table.cellMargins							Insets
 * @uiDefault Table.cellFocusColor						Color
 * @uiDefault Table.showCellFocusIndicator				boolean
 *
 * @author Karl Tauber
 */
public class FlatTableUI
	extends BasicTableUI
{
	protected boolean showHorizontalLines;
	protected boolean showVerticalLines;
	protected Dimension intercellSpacing;

	protected Color selectionBackground;
	protected Color selectionForeground;
	protected Color selectionInactiveBackground;
	protected Color selectionInactiveForeground;

	private boolean oldShowHorizontalLines;
	private boolean oldShowVerticalLines;
	private Dimension oldIntercellSpacing;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTableUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		showHorizontalLines = UIManager.getBoolean( "Table.showHorizontalLines" );
		showVerticalLines = UIManager.getBoolean( "Table.showVerticalLines" );
		intercellSpacing = UIManager.getDimension( "Table.intercellSpacing" );

		selectionBackground = UIManager.getColor( "Table.selectionBackground" );
		selectionForeground = UIManager.getColor( "Table.selectionForeground" );
		selectionInactiveBackground = UIManager.getColor( "Table.selectionInactiveBackground" );
		selectionInactiveForeground = UIManager.getColor( "Table.selectionInactiveForeground" );

		toggleSelectionColors();

		int rowHeight = FlatUIUtils.getUIInt( "Table.rowHeight", 16 );
		if( rowHeight > 0 )
			LookAndFeel.installProperty( table, "rowHeight", UIScale.scale( rowHeight ) );

		if( !showHorizontalLines ) {
			oldShowHorizontalLines = table.getShowHorizontalLines();
			table.setShowHorizontalLines( false );
		}
		if( !showVerticalLines ) {
			oldShowVerticalLines = table.getShowVerticalLines();
			table.setShowVerticalLines( false );
		}

		if( intercellSpacing != null ) {
			oldIntercellSpacing = table.getIntercellSpacing();
			table.setIntercellSpacing( intercellSpacing );
		}

		// checkbox is non-opaque in FlatLaf and therefore would not paint selection
		// --> make checkbox renderer opaque (but opaque in Metal or Windows LaF)
		TableCellRenderer booleanRenderer = table.getDefaultRenderer( Boolean.class );
		if( booleanRenderer instanceof JCheckBox )
			((JCheckBox)booleanRenderer).setOpaque( true );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		selectionBackground = null;
		selectionForeground = null;
		selectionInactiveBackground = null;
		selectionInactiveForeground = null;

		// restore old show horizontal/vertical lines (if not modified)
		if( !showHorizontalLines && oldShowHorizontalLines && !table.getShowHorizontalLines() )
			table.setShowHorizontalLines( true );
		if( !showVerticalLines && oldShowVerticalLines && !table.getShowVerticalLines() )
			table.setShowVerticalLines( true );

		// restore old intercell spacing (if not modified)
		if( intercellSpacing != null && table.getIntercellSpacing().equals( intercellSpacing ) )
			table.setIntercellSpacing( oldIntercellSpacing );
	}

	@Override
	protected FocusListener createFocusListener() {
		return new BasicTableUI.FocusHandler() {
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
		if( FlatUIUtils.isPermanentFocusOwner( table ) ) {
			if( table.getSelectionBackground() == selectionInactiveBackground )
				table.setSelectionBackground( selectionBackground );
			if( table.getSelectionForeground() == selectionInactiveForeground )
				table.setSelectionForeground( selectionForeground );
		} else {
			if( table.getSelectionBackground() == selectionBackground )
				table.setSelectionBackground( selectionInactiveBackground );
			if( table.getSelectionForeground() == selectionForeground )
				table.setSelectionForeground( selectionInactiveForeground );
		}
	}
}
