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

package com.formdev.flatlaf.util;

import java.awt.Color;
import javax.swing.plaf.ColorUIResource;
import com.formdev.flatlaf.util.ColorFunctions.ColorFunction;

/**
 * A (red) color that acts as a placeholder in UI defaults.
 * The actual color is derived from another color,
 * which is modified by the given color functions.
 *
 * @author Karl Tauber
 */
public class DerivedColor
	extends ColorUIResource
{
	private final ColorFunction[] functions;

	private boolean hasBaseOfDefaultColor;
	private int baseOfDefaultColorRGB;

	public DerivedColor( Color defaultColor, ColorFunction... functions ) {
		super( (defaultColor != null) ? defaultColor : Color.red );
		this.functions = functions;
	}

	public Color derive( Color baseColor ) {
		if( (hasBaseOfDefaultColor && baseOfDefaultColorRGB == baseColor.getRGB()) || baseColor == this )
			return this; // return default color

		Color result = ColorFunctions.applyFunctions( baseColor, functions );

		// if the result is equal to the default color, then the original base color
		// was passed, and we can cache this to avoid color calculations
		if( !hasBaseOfDefaultColor && result.getRGB() == this.getRGB() ) {
			hasBaseOfDefaultColor = true;
			baseOfDefaultColorRGB = baseColor.getRGB();
		}

		return result;
	}

	public ColorFunction[] getFunctions() {
		return functions;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append( super.toString() );

		for( ColorFunction function : functions ) {
			buf.append( '\n' );
			buf.append( function.toString() );
		}

		return buf.toString();
	}
}
