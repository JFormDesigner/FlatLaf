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
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.icons.FlatCheckBoxIcon;
import com.formdev.flatlaf.icons.FlatRadioButtonIcon;

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

	//---- components ---------------------------------------------------------

	@Test
	void checkBox() {
		FlatCheckBoxUI ui = new FlatCheckBoxUI( false );

		// assign icon
		UIManager.put( "CheckBox.icon", new FlatCheckBoxIcon() );
		ui.installDefaults( new JCheckBox() );
		assertTrue( ui.getDefaultIcon() instanceof FlatCheckBoxIcon );

		// FlatCheckBoxUI extends FlatRadioButtonUI
		radioButton( ui );
	}

	@Test
	void progressBar() {
		FlatProgressBarUI ui = new FlatProgressBarUI();

		ui.applyStyle( "arc: 5" );
		ui.applyStyle( "horizontalSize: 100,12" );
		ui.applyStyle( "verticalSize: 12,100" );
	}

	@Test
	void radioButton() {
		FlatRadioButtonUI ui = new FlatRadioButtonUI( false );

		// assign icon
		UIManager.put( "RadioButton.icon", new FlatRadioButtonIcon() );
		ui.installDefaults( new JRadioButton() ); // assign icon
		assertTrue( ui.getDefaultIcon() instanceof FlatRadioButtonIcon );

		radioButton( ui );

		ui.applyStyle( "icon.centerDiameter: 8" );
	}

	private void radioButton( FlatRadioButtonUI ui ) {
		ui.applyStyle( "disabledText: #fff" );

		//---- icon ----

		ui.applyStyle( "icon.focusWidth: 2" );
		ui.applyStyle( "icon.focusColor: #fff" );
		ui.applyStyle( "icon.arc: 5" );

		// enabled
		ui.applyStyle( "icon.borderColor: #fff" );
		ui.applyStyle( "icon.background: #fff" );
		ui.applyStyle( "icon.selectedBorderColor: #fff" );
		ui.applyStyle( "icon.selectedBackground: #fff" );
		ui.applyStyle( "icon.checkmarkColor: #fff" );

		// disabled
		ui.applyStyle( "icon.disabledBorderColor: #fff" );
		ui.applyStyle( "icon.disabledBackground: #fff" );
		ui.applyStyle( "icon.disabledCheckmarkColor: #fff" );

		// focused
		ui.applyStyle( "icon.focusedBorderColor: #fff" );
		ui.applyStyle( "icon.focusedBackground: #fff" );
		ui.applyStyle( "icon.selectedFocusedBorderColor: #fff" );
		ui.applyStyle( "icon.selectedFocusedBackground: #fff" );
		ui.applyStyle( "icon.selectedFocusedCheckmarkColor: #fff" );

		// hover
		ui.applyStyle( "icon.hoverBorderColor: #fff" );
		ui.applyStyle( "icon.hoverBackground: #fff" );
		ui.applyStyle( "icon.selectedHoverBackground: #fff" );

		// pressed
		ui.applyStyle( "icon.pressedBackground: #fff" );
		ui.applyStyle( "icon.selectedPressedBackground: #fff" );
	}

	@Test
	void scrollBar() {
		FlatScrollBarUI ui = new FlatScrollBarUI();

		ui.applyStyle( "track: #fff" );
		ui.applyStyle( "thumb: #fff" );
		ui.applyStyle( "width: 10" );
		ui.applyStyle( "minimumThumbSize: 1,2" );
		ui.applyStyle( "maximumThumbSize: 1,2" );
		ui.applyStyle( "allowsAbsolutePositioning: true" );

		ui.applyStyle( "trackInsets: 1,2,3,4" );
		ui.applyStyle( "thumbInsets: 1,2,3,4" );
		ui.applyStyle( "trackArc: 5" );
		ui.applyStyle( "thumbArc: 10" );
		ui.applyStyle( "hoverTrackColor: #fff" );
		ui.applyStyle( "hoverThumbColor: #fff" );
		ui.applyStyle( "hoverThumbWithTrack: true" );
		ui.applyStyle( "pressedTrackColor: #fff" );
		ui.applyStyle( "pressedThumbColor: #fff" );
		ui.applyStyle( "pressedThumbWithTrack: true" );

		ui.applyStyle( "showButtons: true" );
		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "buttonArrowColor: #fff" );
		ui.applyStyle( "buttonDisabledArrowColor: #fff" );
		ui.applyStyle( "hoverButtonBackground: #fff" );
		ui.applyStyle( "pressedButtonBackground: #fff" );
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

	//---- icons --------------------------------------------------------------

	@Test
	void checkBoxIcon() {
		FlatCheckBoxIcon icon = new FlatCheckBoxIcon();

		checkBoxIcon( icon );
	}

	@Test
	void radioButtonIcon() {
		FlatRadioButtonIcon icon = new FlatRadioButtonIcon();

		// FlatRadioButtonIcon extends FlatCheckBoxIcon
		checkBoxIcon( icon );

		icon.applyStyleProperty( "centerDiameter", 8 );
	}

	private void checkBoxIcon( FlatCheckBoxIcon icon ) {
		icon.applyStyleProperty( "focusWidth", 2 );
		icon.applyStyleProperty( "focusColor", Color.WHITE );
		icon.applyStyleProperty( "arc", 5 );

		// enabled
		icon.applyStyleProperty( "borderColor", Color.WHITE );
		icon.applyStyleProperty( "background", Color.WHITE );
		icon.applyStyleProperty( "selectedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "selectedBackground", Color.WHITE );
		icon.applyStyleProperty( "checkmarkColor", Color.WHITE );

		// disabled
		icon.applyStyleProperty( "disabledBorderColor", Color.WHITE );
		icon.applyStyleProperty( "disabledBackground", Color.WHITE );
		icon.applyStyleProperty( "disabledCheckmarkColor", Color.WHITE );

		// focused
		icon.applyStyleProperty( "focusedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "focusedBackground", Color.WHITE );
		icon.applyStyleProperty( "selectedFocusedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "selectedFocusedBackground", Color.WHITE );
		icon.applyStyleProperty( "selectedFocusedCheckmarkColor", Color.WHITE );

		// hover
		icon.applyStyleProperty( "hoverBorderColor", Color.WHITE );
		icon.applyStyleProperty( "hoverBackground", Color.WHITE );
		icon.applyStyleProperty( "selectedHoverBackground", Color.WHITE );

		// pressed
		icon.applyStyleProperty( "pressedBackground", Color.WHITE );
		icon.applyStyleProperty( "selectedPressedBackground", Color.WHITE );
	}
}
