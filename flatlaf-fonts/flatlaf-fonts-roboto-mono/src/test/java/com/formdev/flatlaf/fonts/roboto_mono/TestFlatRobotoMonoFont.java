/*
 * Copyright 2023 FormDev Software GmbH
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

package com.formdev.flatlaf.fonts.roboto_mono;

import java.awt.Font;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Karl Tauber
 */
class TestFlatRobotoMonoFont
{
	@Test
	void testFont() {
		FlatRobotoMonoFont.install();

		testFont( FlatRobotoMonoFont.FAMILY, Font.PLAIN, 13 );
		testFont( FlatRobotoMonoFont.FAMILY, Font.ITALIC, 13 );
		testFont( FlatRobotoMonoFont.FAMILY, Font.BOLD, 13 );
		testFont( FlatRobotoMonoFont.FAMILY, Font.BOLD | Font.ITALIC, 13 );
	}

	private void testFont( String family, int style, int size ) {
		Font actual = new Font( family, style, size );
		assertEquals( family, actual.getFamily() );
		assertEquals( style, actual.getStyle() );
		assertEquals( size, actual.getSize() );
	}
}
