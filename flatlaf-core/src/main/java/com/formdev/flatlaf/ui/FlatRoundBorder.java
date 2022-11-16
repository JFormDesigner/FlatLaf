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

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JSpinner;
import javax.swing.UIManager;
import javax.swing.plaf.SpinnerUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;

/**
 * Border for various components (e.g. {@link javax.swing.JComboBox}).
 *
 * @uiDefault Component.arc					int
 *
 * @author Karl Tauber
 */
public class FlatRoundBorder
	extends FlatBorder
{
	@Styleable protected int arc = UIManager.getInt( "Component.arc" );

	// only used via styling (not in UI defaults, but has likewise client properties)
	/** @since 2 */ @Styleable protected Boolean roundRect;

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		// make mac style spinner border smaller (border does not surround arrow buttons)
		if( isMacStyleSpinner( c ) ) {
			int macStyleButtonsWidth = ((FlatSpinnerUI)((JSpinner)c).getUI()).getMacStyleButtonsWidth();
			width -= macStyleButtonsWidth;
			if( !c.getComponentOrientation().isLeftToRight() )
				x += macStyleButtonsWidth;
		}

		super.paintBorder( c, g, x, y, width, height );
	}

	@Override
	protected int getArc( Component c ) {
		if( isCellEditor( c ) )
			return 0;

		Boolean roundRect = FlatUIUtils.isRoundRect( c );
		if( roundRect == null )
			roundRect = this.roundRect;
		return roundRect != null
			? (roundRect ? Short.MAX_VALUE : 0)
			: (isMacStyleSpinner( c ) ? 0 : arc);
	}

	private boolean isMacStyleSpinner( Component c ) {
		if( c instanceof JSpinner ) {
			SpinnerUI ui = ((JSpinner)c).getUI();
			if( ui instanceof FlatSpinnerUI )
				return ((FlatSpinnerUI)ui).isMacStyle();
		}
		return false;
	}
}
