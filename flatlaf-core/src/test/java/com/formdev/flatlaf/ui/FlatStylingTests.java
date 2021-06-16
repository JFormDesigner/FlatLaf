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

package com.formdev.flatlaf.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @author Karl Tauber
 */
public class FlatStylingTests
{
	@Test
	void parse() {
		assertEquals( null, FlatStyleSupport.parse( null ) );
		assertEquals( null, FlatStyleSupport.parse( "" ) );
		assertEquals( null, FlatStyleSupport.parse( "  " ) );
		assertEquals( null, FlatStyleSupport.parse( ";" ) );
		assertEquals( null, FlatStyleSupport.parse( " ; ; " ) );

		assertEquals(
			expectedMap( "background", Color.WHITE ),
			FlatStyleSupport.parse( "background: #fff" ) );
		assertEquals(
			expectedMap( "background", Color.WHITE, "foreground", Color.BLACK ),
			FlatStyleSupport.parse( "background: #fff; foreground: #000" ) );
		assertEquals(
			expectedMap( "background", Color.WHITE, "foreground", Color.BLACK, "someWidth", 20 ),
			FlatStyleSupport.parse( "background: #fff; foreground: #000; someWidth: 20" ) );
	}

	private Map<Object, Object> expectedMap( Object... keyValuePairs ) {
		Map<Object, Object> map = new HashMap<>();
		for( int i = 0; i < keyValuePairs.length; i += 2 )
			map.put( keyValuePairs[i], keyValuePairs[i+1] );
		return map;
	}

	@Test
	void checkbox() {
		FlatCheckBoxUI ui = new FlatCheckBoxUI( false );

		ui.applyStyle( "disabledText: #fff" );
	}

	@Test
	void radiobutton() {
		FlatRadioButtonUI ui = new FlatRadioButtonUI( false );

		ui.applyStyle( "disabledText: #fff" );
	}

	@Test
	void slider() {
		FlatSliderUI ui = new FlatSliderUI();

		ui.applyStyle( "trackWidth: 2" );
		ui.applyStyle( "thumbSize: 12,12" );
		ui.applyStyle( "focusWidth: 4" );

		ui.applyStyle( "trackValueColor: #fff" );
		ui.applyStyle( "trackColor: #fff" );
		ui.applyStyle( "thumbColor: #fff" );
		ui.applyStyle( "thumbBorderColor: #fff" );
		ui.applyStyle( "focusedColor: #fff" );
		ui.applyStyle( "focusedThumbBorderColor: #fff" );
		ui.applyStyle( "hoverThumbColor: #fff" );
		ui.applyStyle( "pressedThumbColor: #fff" );
		ui.applyStyle( "disabledTrackColor: #fff" );
		ui.applyStyle( "disabledThumbColor: #fff" );
		ui.applyStyle( "disabledThumbBorderColor: #fff" );
		ui.applyStyle( "tickColor: #fff" );
	}
}
