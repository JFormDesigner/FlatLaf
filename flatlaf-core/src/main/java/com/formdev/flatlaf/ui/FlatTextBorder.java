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
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;

/**
 * Border for various text components (e.g. {@link javax.swing.JTextField}).
 *
 * @uiDefault TextComponent.arc				int
 *
 * @author Karl Tauber
 */
public class FlatTextBorder
	extends FlatBorder
{
	@Styleable protected int arc = UIManager.getInt( "TextComponent.arc" );

	// only used via styling (not in UI defaults, but has likewise client properties)
	/** @since 2 */ @Styleable protected Boolean roundRect;

	@Override
	protected int getArc( Component c ) {
		if( isCellEditor( c ) )
			return 0;

		Boolean roundRect = FlatUIUtils.isRoundRect( c );
		if( roundRect == null )
			roundRect = this.roundRect;
		return roundRect != null ? (roundRect ? Short.MAX_VALUE : 0) : arc;
	}
}
