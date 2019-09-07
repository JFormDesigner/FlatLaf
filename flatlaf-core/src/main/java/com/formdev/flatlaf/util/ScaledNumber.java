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

import static com.formdev.flatlaf.util.UIScale.scale;

/**
 * A number that scales its value.
 *
 * NOTE:
 * Using ScaledNumber in UI defaults works only if the value is get with
 * sun.swing.DefaultLookup.getInt(), which is used by some basic UI delegates,
 * because this method uses "instanceof Number".
 * UIManager.getInt() on the other hand uses "instanceof Integer" and does not work.
 *
 * @author Karl Tauber
 */
public class ScaledNumber
	extends Number
{
	private final int value;

	public ScaledNumber( int value ) {
		this.value = value;
	}

	@Override
	public int intValue() {
		return scale( value );
	}

	@Override
	public long longValue() {
		return scale( value );
	}

	@Override
	public float floatValue() {
		return scale( (float) value );
	}

	@Override
	public double doubleValue() {
		return scale( (float) value );
	}

	@Override
	public int hashCode() {
		return Integer.hashCode( value );
	}

	@Override
	public boolean equals( Object obj ) {
		return (obj instanceof ScaledNumber)
			? (value == ((ScaledNumber)obj).value)
			: false;
	}

	@Override
	public String toString() {
		return Integer.toString( value );
	}
}
