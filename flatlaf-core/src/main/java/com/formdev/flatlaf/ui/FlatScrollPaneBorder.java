/*
 * Copyright 2023 FormDev Software GmbH
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
import java.awt.Insets;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.util.UIScale;

/**
 * Border for {@link javax.swing.JScrollPane}.
 *
 * @uiDefault ScrollPane.arc				int

 * @author Karl Tauber
 * @since 3.3
 */
public class FlatScrollPaneBorder
	extends FlatBorder
{
	@Styleable protected int arc = UIManager.getInt( "ScrollPane.arc" );

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		insets = super.getBorderInsets( c, insets );

		// if view is rounded, increase left and right insets to avoid that the viewport
		// is painted over the rounded border on the corners
		int arc = getArc( c );
		if( arc > 0 ) {
			// increase insets by radius minus lineWidth because radius is measured
			// from the outside of the line, but insets from super include lineWidth
			int padding = UIScale.scale( (arc / 2) - getLineWidth( c ) );
			insets.left += padding;
			insets.right += padding;
		}

		return insets;
	}

	@Override
	protected int getArc( Component c ) {
		if( isCellEditor( c ) )
			return 0;

		return arc;
	}
}
