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

import static com.formdev.flatlaf.util.UIScale.*;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.plaf.basic.BasicBorders;

/**
 * Border that scales component margin.
 *
 * @author Karl Tauber
 */
public class FlatMarginBorder
	extends BasicBorders.MarginBorder
{
	protected int left, right, top, bottom;

	public FlatMarginBorder() {
		left = right = top = bottom = 0;
	}

	public FlatMarginBorder( Insets insets ) {
		left = insets.left;
		top = insets.top;
		right = insets.right;
		bottom = insets.bottom;
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		insets = super.getBorderInsets( c, insets );
		insets.top = scale( insets.top + top );
		insets.left = scale( insets.left + left );
		insets.bottom = scale( insets.bottom + bottom );
		insets.right = scale( insets.right + right );
		return insets;
	}
}
