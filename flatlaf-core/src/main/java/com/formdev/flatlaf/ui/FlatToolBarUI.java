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
import java.awt.Component;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Objects;
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
 * TODO document used UI defaults of superclass
 *
 * @uiDefault ToolBar.buttonMargins						Insets
 *
 * @author Karl Tauber
 */
public class FlatToolBarUI
	extends BasicToolBarUI
{
	private Border rolloverBorder;
	private final HashMap<AbstractButton, Color> backgroundTable = new HashMap<>();

	/** Cache non-UIResource button color. */
	private Color buttonBackground;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatToolBarUI();
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		rolloverBorder = null;
		buttonBackground = null;
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
	protected void setBorderToRollover( Component c ) {
		super.setBorderToRollover( c );
		setToRollover( c );
	}

	@Override
	protected void setBorderToNonRollover( Component c ) {
		super.setBorderToNonRollover( c );
		setToRollover( c );
	}

	@Override
	protected void setBorderToNormal( Component c ) {
		super.setBorderToNormal( c );
		setToNormal( c );
	}

	private void setToRollover( Component c ) {
		if( c instanceof AbstractButton ) {
			AbstractButton b = (AbstractButton) c;

			Color background = backgroundTable.get( b );
			if( background == null || background instanceof UIResource )
				backgroundTable.put( b, b.getBackground() );

			if( b.getBackground() instanceof UIResource )
				b.setBackground( getButtonBackground() );
		}
	}

	private void setToNormal( Component c ) {
		if( c instanceof AbstractButton ) {
			AbstractButton b = (AbstractButton) c;

			Color background = backgroundTable.remove( b );
			b.setBackground( background );
		}
	}

	private Color getButtonBackground() {
		// use toolbar background as button background
		// must be not an instance of UIResource
		Color toolBarBackground = toolBar.getBackground();
		if( !Objects.equals( buttonBackground, toolBarBackground ) )
			buttonBackground = FlatUIUtils.nonUIResource( toolBarBackground );
		return buttonBackground;
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
