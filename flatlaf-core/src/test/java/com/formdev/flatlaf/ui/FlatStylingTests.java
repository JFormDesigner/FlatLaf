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
import java.util.function.Consumer;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.icons.FlatCapsLockIcon;
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
	void editorPane() {
		FlatEditorPaneUI ui = new FlatEditorPaneUI();

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "focusedBackground: #fff" );
	}

	@Test
	void formattedTextField() {
		FlatFormattedTextFieldUI ui = new FlatFormattedTextFieldUI();

		// create border
		UIManager.put( "FormattedTextField.border", new FlatTextBorder() );
		ui.installUI( new JFormattedTextField() );

		// FlatFormattedTextFieldUI extends FlatTextFieldUI
		textField( ui );
	}

	@Test
	void passwordField() {
		FlatPasswordFieldUI ui = new FlatPasswordFieldUI();

		// create border and capsLockIcon
		UIManager.put( "PasswordField.border", new FlatTextBorder() );
		UIManager.put( "PasswordField.capsLockIcon", new FlatCapsLockIcon() );
		ui.installUI( new JPasswordField() );

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "placeholderForeground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );
		ui.applyStyle( "showCapsLock: true" );

		// capsLockIcon
		ui.applyStyle( "capsLockIconColor: #fff" );

		// border
		flatTextBorder( style -> ui.applyStyle( style ) );
	}

	@Test
	void popupMenuSeparator() {
		FlatPopupMenuSeparatorUI ui = new FlatPopupMenuSeparatorUI( false );

		// FlatPopupMenuSeparatorUI extends FlatSeparatorUI
		separator( ui );
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
	void separator() {
		FlatSeparatorUI ui = new FlatSeparatorUI( false );

		separator( ui );
	}

	private void separator( FlatSeparatorUI ui ) {
		ui.applyStyle( "height: 6" );
		ui.applyStyle( "stripeWidth: 2" );
		ui.applyStyle( "stripeIndent: 10" );
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

	@Test
	void splitPane() {
		FlatSplitPaneUI ui = new FlatSplitPaneUI();

		// create divider and one-touch buttons
		ui.installUI( new JSplitPane() );

		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "oneTouchArrowColor: #fff" );
		ui.applyStyle( "oneTouchHoverArrowColor: #fff" );
		ui.applyStyle( "oneTouchPressedArrowColor: #fff" );

		ui.applyStyle( "style: grip" );
		ui.applyStyle( "gripColor: #fff" );
		ui.applyStyle( "gripDotCount: 3" );
		ui.applyStyle( "gripDotSize: {integer}3" );
		ui.applyStyle( "gripGap: 2" );
	}

	@Test
	void textArea() {
		FlatTextAreaUI ui = new FlatTextAreaUI();

		ui.installUI( new JTextArea() );

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );
	}

	@Test
	void textField() {
		FlatTextFieldUI ui = new FlatTextFieldUI();

		// create border
		UIManager.put( "TextField.border", new FlatTextBorder() );
		ui.installUI( new JTextField() );

		textField( ui );
	}

	private void textField( FlatTextFieldUI ui ) {
		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "placeholderForeground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );

		// border
		flatTextBorder( style -> ui.applyStyle( style ) );
	}

	@Test
	void textPane() {
		FlatTextPaneUI ui = new FlatTextPaneUI();

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "focusedBackground: #fff" );
	}

	//---- component borders --------------------------------------------------

	private void flatTextBorder( Consumer<String> applyStyle ) {
		flatBorder( applyStyle );

		applyStyle.accept( "arc: 6" );
	}

	private void flatBorder( Consumer<String> applyStyle ) {
		applyStyle.accept( "focusWidth: 2" );
		applyStyle.accept( "innerFocusWidth: {float}0.5" );
		applyStyle.accept( "innerOutlineWidth: {float}1.5" );
		applyStyle.accept( "focusColor: #fff" );
		applyStyle.accept( "borderColor: #fff" );
		applyStyle.accept( "disabledBorderColor: #fff" );
		applyStyle.accept( "focusedBorderColor: #fff" );

		applyStyle.accept( "error.borderColor: #fff" );
		applyStyle.accept( "error.focusedBorderColor: #fff" );
		applyStyle.accept( "warning.borderColor: #fff" );
		applyStyle.accept( "warning.focusedBorderColor: #fff" );
		applyStyle.accept( "custom.borderColor: desaturate(#f00,50%,relative derived noAutoInverse)" );
	}

	//---- borders ------------------------------------------------------------

	@Test
	void flatTextBorder() {
		FlatTextBorder border = new FlatTextBorder();

		// FlatTextBorder extends FlatBorder
		flatBorder( border );

		border.applyStyleProperty( "arc", 6 );
	}

	@Test
	void flatBorder() {
		FlatBorder border = new FlatBorder();

		flatBorder( border );
	}

	private void flatBorder( FlatBorder border ) {
		border.applyStyleProperty( "focusWidth", 2 );
		border.applyStyleProperty( "innerFocusWidth", 0.5f );
		border.applyStyleProperty( "innerOutlineWidth", 1.5f );
		border.applyStyleProperty( "focusColor", Color.WHITE );
		border.applyStyleProperty( "borderColor", Color.WHITE );
		border.applyStyleProperty( "disabledBorderColor", Color.WHITE );
		border.applyStyleProperty( "focusedBorderColor", Color.WHITE );

		border.applyStyleProperty( "error.borderColor", Color.WHITE );
		border.applyStyleProperty( "error.focusedBorderColor", Color.WHITE );
		border.applyStyleProperty( "warning.borderColor", Color.WHITE );
		border.applyStyleProperty( "warning.focusedBorderColor", Color.WHITE );
		border.applyStyleProperty( "custom.borderColor", Color.WHITE );
	}

	//---- icons --------------------------------------------------------------

	@Test
	void flatCheckBoxIcon() {
		FlatCheckBoxIcon icon = new FlatCheckBoxIcon();

		flatCheckBoxIcon( icon );
	}

	@Test
	void flatRadioButtonIcon() {
		FlatRadioButtonIcon icon = new FlatRadioButtonIcon();

		// FlatRadioButtonIcon extends FlatCheckBoxIcon
		flatCheckBoxIcon( icon );

		icon.applyStyleProperty( "centerDiameter", 8 );
	}

	private void flatCheckBoxIcon( FlatCheckBoxIcon icon ) {
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
