/*
 * Copyright 2020 FormDev Software GmbH
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

package com.formdev.flatlaf.util;

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;

/**
 * Empty border that scales insets.
 *
 * @author Karl Tauber
 */
public class ScaledEmptyBorder
	extends EmptyBorder
{
	public ScaledEmptyBorder( int top, int left, int bottom, int right ) {
		super( top, left, bottom, right );
	}

	public ScaledEmptyBorder( Insets insets ) {
		super( insets );
	}

	@Override
	public Insets getBorderInsets() {
		return new Insets( scale( top ), scale( left ), scale( bottom ), scale( right ) );
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		insets.left = scale( left );
		insets.top = scale( top );
		insets.right = scale( right );
		insets.bottom = scale( bottom );
		return insets;
	}
}
