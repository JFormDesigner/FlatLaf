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
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToolBarUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JToolBar}.
 *
 * <!-- BasicToolBarUI -->
 *
 * @uiDefault ToolBar.font								Font
 * @uiDefault ToolBar.background						Color
 * @uiDefault ToolBar.foreground						Color
 * @uiDefault ToolBar.border							Border
 * @uiDefault ToolBar.dockingBackground					Color
 * @uiDefault ToolBar.dockingForeground					Color
 * @uiDefault ToolBar.floatingBackground				Color
 * @uiDefault ToolBar.floatingForeground				Color
 * @uiDefault ToolBar.isRollover						boolean
 *
 * <!-- FlatToolBarUI -->
 *
 * @uiDefault ToolBar.buttonMargins						Insets
 *
 * @author Karl Tauber
 */
public class FlatToolBarUI
	extends BasicToolBarUI
{
	private Border rolloverBorder;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatToolBarUI();
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		rolloverBorder = null;
	}

	@Override
	protected ContainerListener createToolBarContListener() {
		return new ToolBarContListener() {
			@Override
			public void componentAdded( ContainerEvent e ) {
				super.componentAdded( e );

				Component c = e.getChild();
				if( c instanceof AbstractButton )
					c.setFocusable( false );
			}

			@Override
			public void componentRemoved( ContainerEvent e ) {
				super.componentRemoved( e );

				Component c = e.getChild();
				if( c instanceof AbstractButton )
					c.setFocusable( true );
			}
		};
	}

	@Override
	protected Border createRolloverBorder() {
		return getRolloverBorder();
	}

	@Override
	protected Border createNonRolloverBorder() {
		return getRolloverBorder();
	}

	@Override
	protected Border getNonRolloverBorder( AbstractButton b ) {
		return getRolloverBorder();
	}

	private Border getRolloverBorder() {
		if( rolloverBorder == null )
			rolloverBorder = new FlatRolloverMarginBorder();
		return rolloverBorder;
	}

	@Override
	public void setOrientation( int orientation ) {
		if( orientation != toolBar.getOrientation() ) {
			// swap margins if orientation changes when floating
			Insets margin = toolBar.getMargin();
			Insets newMargin = new Insets( margin.left, margin.top, margin.right, margin.bottom );
			if( !newMargin.equals( margin ) )
				toolBar.setMargin( newMargin );
		}

		super.setOrientation( orientation );
	}

	//---- class FlatRolloverMarginBorder -------------------------------------

	/**
	 * Uses button margin only if explicitly set.
	 * Otherwise uses insets specified in constructor.
	 */
	private static class FlatRolloverMarginBorder
		extends EmptyBorder
	{
		public FlatRolloverMarginBorder() {
			super( UIManager.getInsets( "ToolBar.buttonMargins" ) );
		}

		@Override
		public Insets getBorderInsets( Component c, Insets insets ) {
			Insets margin = (c instanceof AbstractButton)
				? ((AbstractButton) c).getMargin()
				: null;

			if( margin == null || margin instanceof UIResource ) {
				insets.top = top;
				insets.left = left;
				insets.bottom = bottom;
				insets.right = right;
			} else {
				// margin explicitly set
				insets.top = margin.top;
				insets.left = margin.left;
				insets.bottom = margin.bottom;
				insets.right = margin.right;
			}

			// scale
			insets.top = scale( insets.top );
			insets.left = scale( insets.left );
			insets.bottom = scale( insets.bottom );
			insets.right = scale( insets.right );

			return insets;
		}
	}
}
