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

package com.formdev.flatlaf;

import java.util.Collections;
import java.util.Properties;
import java.util.function.Function;
import com.formdev.flatlaf.UIDefaultsLoader.ValueType;

/**
 * Enable accessing package private methods of {@link UIDefaultsLoader}.
 *
 * @author Karl Tauber
 */
public class UIDefaultsLoaderAccessor
{
	public static final String KEY_VARIABLES = UIDefaultsLoader.KEY_VARIABLES;

	public static Object UNKNOWN = ValueType.UNKNOWN;
	public static Object STRING = ValueType.STRING;
	public static Object BOOLEAN = ValueType.BOOLEAN;
	public static Object CHARACTER = ValueType.CHARACTER;
	public static Object INTEGER = ValueType.INTEGER;
	public static Object FLOAT = ValueType.FLOAT;
	public static Object BORDER = ValueType.BORDER;
	public static Object ICON = ValueType.ICON;
	public static Object INSETS = ValueType.INSETS;
	public static Object DIMENSION = ValueType.DIMENSION;
	public static Object COLOR = ValueType.COLOR;
	public static Object SCALEDINTEGER = ValueType.SCALEDINTEGER;
	public static Object SCALEDFLOAT = ValueType.SCALEDFLOAT;
	public static Object SCALEDINSETS = ValueType.SCALEDINSETS;
	public static Object SCALEDDIMENSION = ValueType.SCALEDDIMENSION;
	public static Object INSTANCE = ValueType.INSTANCE;
	public static Object CLASS = ValueType.CLASS;
	public static Object GRAYFILTER = ValueType.GRAYFILTER;
	public static Object NULL = ValueType.NULL;
	public static Object LAZY = ValueType.LAZY;

	public static String resolveValue( String value, Function<String, String> propertiesGetter )
		throws IllegalArgumentException
	{
		return UIDefaultsLoader.resolveValue( value, propertiesGetter );
	}

	public static Object parseValue( String key, String value, Object[] resultValueType,
		Function<String, String> resolver )
			throws IllegalArgumentException
	{
		ValueType[] resultValueType2 = new ValueType[1];
		Object result = UIDefaultsLoader.parseValue( key, value, null,
			resultValueType2, resolver, Collections.emptyList() );
		resultValueType[0] = resultValueType2[0];
		return result;
	}

	public static int parseColorRGBA( String value )
		throws IllegalArgumentException
	{
		return UIDefaultsLoader.parseColorRGBA( value );
	}

	public static Properties newUIProperties( boolean dark ) {
		return UIDefaultsLoader.newUIProperties( dark );
	}
}
