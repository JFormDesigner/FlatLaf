/*
 * Copyright 2022 FormDev Software GmbH
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

package com.formdev.flatlaf.icons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import com.formdev.flatlaf.ui.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TestFlatIconPaintingNullComponent
{
	static Graphics graphics;

	@BeforeAll
	static void setup() {
		TestUtils.setup( false );
		graphics = new BufferedImage( 32, 32, BufferedImage.TYPE_INT_ARGB ).getGraphics();
		graphics.setColor( Color.white );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
		graphics = null;
	}

	@Test
	void flatHelpButtonIcon() {
		paintWithoutException( new FlatHelpButtonIcon() );
	}

	@Test
	void flatMenuArrowIcon() {
		paintWithoutException( new FlatMenuArrowIcon() );
	}

	@Test
	void flatSearchIcon() {
		paintWithoutException( new FlatSearchIcon() );
	}

	private void paintWithoutException( Icon icon ) {
		graphics.clearRect( 0, 0, 32, 32 );
		assertDoesNotThrow( () -> icon.paintIcon( null, graphics, 0, 0 ) );
	}
}
