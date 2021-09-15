/*
 * Copyright 2021 FormDev Software GmbH
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import org.junit.jupiter.api.Test;

/**
 * @author Karl Tauber
 */
public class TestUIDefaultsLoader
{
	@Test
	void parseValue() {
		assertEquals( null, UIDefaultsLoader.parseValue( "dummy", "null", null ) );
		assertEquals( false, UIDefaultsLoader.parseValue( "dummy", "false", null ) );
		assertEquals( true, UIDefaultsLoader.parseValue( "dummy", "true", null ) );

		assertEquals( "hello", UIDefaultsLoader.parseValue( "dummy", "hello", null ) );
		assertEquals( "hello", UIDefaultsLoader.parseValue( "dummy", "\"hello\"", null ) );
		assertEquals( "null", UIDefaultsLoader.parseValue( "dummy", "\"null\"", null ) );

		assertEquals( 'a', UIDefaultsLoader.parseValue( "dummyChar", "a", null ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummy", "123", null ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummyWidth", "123", null ) );
		assertEquals( 1.23f, UIDefaultsLoader.parseValue( "dummy", "1.23", null ) );
		assertEquals( 1.23f, UIDefaultsLoader.parseValue( "dummyWidth", "{float}1.23", null ) );

		assertEquals( new Insets( 2,2,2,2 ), UIDefaultsLoader.parseValue( "dummyInsets", "2,2,2,2", null ) );
		assertEquals( new Dimension( 2,2 ), UIDefaultsLoader.parseValue( "dummySize", "2,2", null ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "dummy", "#f00", null ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "dummyColor", "#f00", null ) );
	}

	@Test
	void parseValueWithJavaType() {
		assertEquals( null, UIDefaultsLoader.parseValue( "dummy", "null", String.class ) );
		assertEquals( false, UIDefaultsLoader.parseValue( "dummy", "false", boolean.class ) );
		assertEquals( true, UIDefaultsLoader.parseValue( "dummy", "true", Boolean.class ) );

		assertEquals( "hello", UIDefaultsLoader.parseValue( "dummy", "hello", String.class ) );
		assertEquals( "hello", UIDefaultsLoader.parseValue( "dummy", "\"hello\"", String.class ) );
		assertEquals( "null", UIDefaultsLoader.parseValue( "dummy", "\"null\"", String.class ) );
		assertEquals( null, UIDefaultsLoader.parseValue( "dummy", "null", String.class ) );

		assertEquals( 'a', UIDefaultsLoader.parseValue( "dummy", "a", char.class ) );
		assertEquals( 'a', UIDefaultsLoader.parseValue( "dummy", "a", Character.class ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummy", "123", int.class ) );
		assertEquals( 123, UIDefaultsLoader.parseValue( "dummy", "123", Integer.class ) );
		assertEquals( 1.23f, UIDefaultsLoader.parseValue( "dummy", "1.23", float.class ) );
		assertEquals( 1.23f, UIDefaultsLoader.parseValue( "dummy", "1.23", Float.class ) );

		assertEquals( new Insets( 2,2,2,2 ), UIDefaultsLoader.parseValue( "dummy", "2,2,2,2", Insets.class ) );
		assertEquals( new Dimension( 2,2 ), UIDefaultsLoader.parseValue( "dummy", "2,2", Dimension.class ) );
		assertEquals( new Color( 0xff0000 ), UIDefaultsLoader.parseValue( "dummy", "#f00", Color.class ) );
	}
}
