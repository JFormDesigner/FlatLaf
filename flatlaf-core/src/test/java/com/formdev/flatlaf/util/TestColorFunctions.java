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

package com.formdev.flatlaf.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.awt.Color;
import org.junit.jupiter.api.Test;

/**
 * @author Karl Tauber
 */
public class TestColorFunctions
{
	@Test
	void luma() {
		assertEquals( 0, ColorFunctions.luma( Color.black ) );
		assertEquals( 1, ColorFunctions.luma( Color.white ) );

		assertEquals( 0.2126f, ColorFunctions.luma( Color.red ) );
		assertEquals( 0.7152f, ColorFunctions.luma( Color.green ) );
		assertEquals( 0.0722f, ColorFunctions.luma( Color.blue ) );

		assertEquals( 0.9278f, ColorFunctions.luma( Color.yellow ) );
		assertEquals( 0.7874f, ColorFunctions.luma( Color.cyan ) );

		assertEquals( 0.051269464f, ColorFunctions.luma( Color.darkGray ) );
		assertEquals( 0.21586052f, ColorFunctions.luma( Color.gray ) );
		assertEquals( 0.52711517f, ColorFunctions.luma( Color.lightGray ) );
	}
}
