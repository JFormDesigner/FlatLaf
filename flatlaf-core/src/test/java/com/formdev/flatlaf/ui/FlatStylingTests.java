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
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.*;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.icons.*;

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
	void button() {
		FlatButtonUI ui = new FlatButtonUI( false );

		// create border
		UIManager.put( "Button.border", new FlatButtonBorder() );
		JButton b = new JButton();
		ui.installUI( b );

		button( b, ui );

		//---- FlatHelpButtonIcon ----

		ui.applyStyle( b, "help.focusWidth: 2" );
		ui.applyStyle( b, "help.focusColor: #fff" );
		ui.applyStyle( b, "help.innerFocusWidth: {float}0.5" );
		ui.applyStyle( b, "help.borderWidth: 1" );

		ui.applyStyle( b, "help.borderColor: #fff" );
		ui.applyStyle( b, "help.disabledBorderColor: #fff" );
		ui.applyStyle( b, "help.focusedBorderColor: #fff" );
		ui.applyStyle( b, "help.hoverBorderColor: #fff" );
		ui.applyStyle( b, "help.background: #fff" );
		ui.applyStyle( b, "help.disabledBackground: #fff" );
		ui.applyStyle( b, "help.focusedBackground: #fff" );
		ui.applyStyle( b, "help.hoverBackground: #fff" );
		ui.applyStyle( b, "help.pressedBackground: #fff" );
		ui.applyStyle( b, "help.questionMarkColor: #fff" );
		ui.applyStyle( b, "help.disabledQuestionMarkColor: #fff" );
	}

	private void button( AbstractButton b, FlatButtonUI ui ) {
		ui.applyStyle( b, "minimumWidth: 100" );

		ui.applyStyle( b, "focusedBackground: #fff" );
		ui.applyStyle( b, "hoverBackground: #fff" );
		ui.applyStyle( b, "pressedBackground: #fff" );
		ui.applyStyle( b, "selectedBackground: #fff" );
		ui.applyStyle( b, "selectedForeground: #fff" );
		ui.applyStyle( b, "disabledBackground: #fff" );
		ui.applyStyle( b, "disabledText: #fff" );
		ui.applyStyle( b, "disabledSelectedBackground: #fff" );

		ui.applyStyle( b, "default.background: #fff" );
		ui.applyStyle( b, "default.foreground: #fff" );
		ui.applyStyle( b, "default.focusedBackground: #fff" );
		ui.applyStyle( b, "default.hoverBackground: #fff" );
		ui.applyStyle( b, "default.pressedBackground: #fff" );
		ui.applyStyle( b, "default.boldText: true" );

		ui.applyStyle( b, "paintShadow: true" );
		ui.applyStyle( b, "shadowWidth: 2" );
		ui.applyStyle( b, "shadowColor: #fff" );
		ui.applyStyle( b, "default.shadowColor: #fff" );

		ui.applyStyle( b, "toolbar.spacingInsets: 1,2,3,4" );
		ui.applyStyle( b, "toolbar.hoverBackground: #fff" );
		ui.applyStyle( b, "toolbar.pressedBackground: #fff" );
		ui.applyStyle( b, "toolbar.selectedBackground: #fff" );

		// border
		flatButtonBorder( style -> ui.applyStyle( b, style ) );
	}

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
	void comboBox() {
		FlatComboBoxUI ui = new FlatComboBoxUI();

		// create border and arrow button
		UIManager.put( "ComboBox.border", new FlatRoundBorder() );
		ui.installUI( new JComboBox<>() );

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "editorColumns: 10" );
		ui.applyStyle( "buttonStyle: auto" );
		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "borderColor: #fff" );
		ui.applyStyle( "disabledBorderColor: #fff" );

		ui.applyStyle( "editableBackground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "disabledForeground: #fff" );

		ui.applyStyle( "buttonBackground: #fff" );
		ui.applyStyle( "buttonFocusedBackground: #fff" );
		ui.applyStyle( "buttonEditableBackground: #fff" );
		ui.applyStyle( "buttonArrowColor: #fff" );
		ui.applyStyle( "buttonDisabledArrowColor: #fff" );
		ui.applyStyle( "buttonHoverArrowColor: #fff" );
		ui.applyStyle( "buttonPressedArrowColor: #fff" );

		ui.applyStyle( "popupFocusedBackground: #fff" );

		// border
		flatRoundBorder( style -> ui.applyStyle( style ) );
	}

	@Test
	void editorPane() {
		FlatEditorPaneUI ui = new FlatEditorPaneUI();

		// for FlatEditorPaneUI.updateBackground()
		ui.installUI( new JEditorPane() );

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
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
	void label() {
		FlatLabelUI ui = new FlatLabelUI( false );

		ui.applyStyle( "disabledForeground: #fff" );
	}

	@Test
	void list() {
		FlatListUI ui = new FlatListUI();
		ui.installUI( new JList<>() );

		ui.applyStyle( "selectionBackground: #fff" );
		ui.applyStyle( "selectionForeground: #fff" );
		ui.applyStyle( "selectionInactiveBackground: #fff" );
		ui.applyStyle( "selectionInactiveForeground: #fff" );

		// FlatListCellBorder
		ui.applyStyle( "cellMargins: 1,2,3,4" );
		ui.applyStyle( "cellFocusColor: #fff" );
		ui.applyStyle( "showCellFocusIndicator: true" );
	}

	@Test
	void menu() {
		UIManager.put( "Menu.arrowIcon", new FlatMenuArrowIcon() );
		UIManager.put( "Menu.checkIcon", null );
		FlatMenuUI ui = new FlatMenuUI();
		ui.installUI( new JMenu() );

		Consumer<String> applyStyle = style -> ui.applyStyle( style );
		menuItem( applyStyle );
		menuItem_arrowIcon( applyStyle );
	}

	@Test
	void menuItem() {
		UIManager.put( "MenuItem.arrowIcon", new FlatMenuItemArrowIcon() );
		UIManager.put( "MenuItem.checkIcon", null );
		FlatMenuItemUI ui = new FlatMenuItemUI();
		ui.installUI( new JMenuItem() );

		Consumer<String> applyStyle = style -> ui.applyStyle( style );
		menuItem( applyStyle );
		menuItem_arrowIcon( applyStyle );
	}

	@Test
	void checkBoxMenuItem() {
		UIManager.put( "CheckBoxMenuItem.arrowIcon", new FlatMenuItemArrowIcon() );
		UIManager.put( "CheckBoxMenuItem.checkIcon", new FlatCheckBoxMenuItemIcon() );
		FlatCheckBoxMenuItemUI ui = new FlatCheckBoxMenuItemUI();
		ui.installUI( new JCheckBoxMenuItem() );

		Consumer<String> applyStyle = style -> ui.applyStyle( style );
		menuItem( applyStyle );
		menuItem_arrowIcon( applyStyle );
		menuItem_checkIcon( applyStyle );
	}

	@Test
	void radioButtonMenuItem() {
		UIManager.put( "RadioButtonMenuItem.arrowIcon", new FlatMenuItemArrowIcon() );
		UIManager.put( "RadioButtonMenuItem.checkIcon", new FlatRadioButtonMenuItemIcon() );
		FlatRadioButtonMenuItemUI ui = new FlatRadioButtonMenuItemUI();
		ui.installUI( new JRadioButtonMenuItem() );

		Consumer<String> applyStyle = style -> ui.applyStyle( style );
		menuItem( applyStyle );
		menuItem_arrowIcon( applyStyle );
		menuItem_checkIcon( applyStyle );
	}

	private void menuItem( Consumer<String> applyStyle ) {
		applyStyle.accept( "selectionBackground: #fff" );
		applyStyle.accept( "selectionForeground: #fff" );
		applyStyle.accept( "disabledForeground: #fff" );
		applyStyle.accept( "acceleratorForeground: #fff" );
		applyStyle.accept( "acceleratorSelectionForeground: #fff" );

		menuItemRenderer( applyStyle );
	}

	private void menuItemRenderer( Consumer<String> applyStyle ) {
		applyStyle.accept( "minimumWidth: 10" );
		applyStyle.accept( "minimumIconSize: 16,16" );
		applyStyle.accept( "textAcceleratorGap: 28" );
		applyStyle.accept( "textNoAcceleratorGap: 6" );
		applyStyle.accept( "acceleratorArrowGap: 2" );

		applyStyle.accept( "checkBackground: #fff" );
		applyStyle.accept( "checkMargins: 1,2,3,4" );

		applyStyle.accept( "underlineSelectionBackground: #fff" );
		applyStyle.accept( "underlineSelectionCheckBackground: #fff" );
		applyStyle.accept( "underlineSelectionColor: #fff" );
		applyStyle.accept( "underlineSelectionHeight: 3" );
	}

	private void menuItem_checkIcon( Consumer<String> applyStyle ) {
		applyStyle.accept( "icon.checkmarkColor: #fff" );
		applyStyle.accept( "icon.disabledCheckmarkColor: #fff" );
		applyStyle.accept( "icon.selectionForeground: #fff" );
	}

	private void menuItem_arrowIcon( Consumer<String> applyStyle ) {
		applyStyle.accept( "icon.arrowType: chevron" );
		applyStyle.accept( "icon.arrowColor: #fff" );
		applyStyle.accept( "icon.disabledArrowColor: #fff" );
		applyStyle.accept( "selectionForeground: #fff" );
	}

	@Test
	void passwordField() {
		FlatPasswordFieldUI ui = new FlatPasswordFieldUI();

		// create border and capsLockIcon
		UIManager.put( "PasswordField.border", new FlatTextBorder() );
		UIManager.put( "PasswordField.capsLockIcon", new FlatCapsLockIcon() );
		ui.installUI( new JPasswordField() );

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
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
	void scrollPane() {
		FlatScrollPaneUI ui = new FlatScrollPaneUI();

		// create border
		UIManager.put( "ScrollPane.border", new FlatBorder() );
		ui.installUI( new JScrollPane() );

		// border
		flatBorder( style -> ui.applyStyle( style ) );
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
	void spinner() {
		FlatSpinnerUI ui = new FlatSpinnerUI();

		// create border and arrow buttons
		UIManager.put( "Spinner.border", new FlatRoundBorder() );
		ui.installUI( new JSpinner() );

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "buttonStyle: button" );
		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "borderColor: #fff" );
		ui.applyStyle( "disabledBorderColor: #fff" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "disabledForeground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );
		ui.applyStyle( "buttonBackground: #fff" );
		ui.applyStyle( "buttonArrowColor: #fff" );
		ui.applyStyle( "buttonDisabledArrowColor: #fff" );
		ui.applyStyle( "buttonHoverArrowColor: #fff" );
		ui.applyStyle( "buttonPressedArrowColor: #fff" );
		ui.applyStyle( "padding: 1,2,3,4" );

		// border
		flatRoundBorder( style -> ui.applyStyle( style ) );
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
	void table() {
		FlatTableUI ui = new FlatTableUI();
		ui.installUI( new JTable() );

		ui.applyStyle( "selectionBackground: #fff" );
		ui.applyStyle( "selectionForeground: #fff" );
		ui.applyStyle( "selectionInactiveBackground: #fff" );
		ui.applyStyle( "selectionInactiveForeground: #fff" );

		// FlatTableCellBorder
		ui.applyStyle( "cellMargins: 1,2,3,4" );
		ui.applyStyle( "cellFocusColor: #fff" );
		ui.applyStyle( "showCellFocusIndicator: true" );
	}

	@Test
	void tableHeader() {
		FlatTableHeaderUI ui = new FlatTableHeaderUI();

		ui.applyStyle( "bottomSeparatorColor: #fff" );
		ui.applyStyle( "height: 20" );
		ui.applyStyle( "sortIconPosition: top" );

		// FlatTableHeaderBorder
		ui.applyStyle( "cellMargins: 1,2,3,4" );
		ui.applyStyle( "separatorColor: #fff" );

		// FlatAscendingSortIcon and FlatDescendingSortIcon
		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "sortIconColor: #fff" );
	}

	@Test
	void textArea() {
		FlatTextAreaUI ui = new FlatTextAreaUI();

		// for FlatEditorPaneUI.updateBackground()
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
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
		ui.applyStyle( "placeholderForeground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );

		// border
		flatTextBorder( style -> ui.applyStyle( style ) );
	}

	@Test
	void textPane() {
		FlatTextPaneUI ui = new FlatTextPaneUI();

		// for FlatEditorPaneUI.updateBackground()
		ui.installUI( new JTextPane() );

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );
	}

	@Test
	void toggleButton() {
		FlatToggleButtonUI ui = new FlatToggleButtonUI( false );

		// create border
		UIManager.put( "ToggleButton.border", new FlatButtonBorder() );
		JToggleButton b = new JToggleButton();
		ui.installUI( b );

		// FlatToggleButtonUI extends FlatButtonUI
		button( b, ui );

		ui.applyStyle( b, "tab.underlineHeight: 3" );
		ui.applyStyle( b, "tab.underlineColor: #fff" );
		ui.applyStyle( b, "tab.disabledUnderlineColor: #fff" );
		ui.applyStyle( b, "tab.selectedBackground: #fff" );
		ui.applyStyle( b, "tab.hoverBackground: #fff" );
		ui.applyStyle( b, "tab.focusBackground: #fff" );
	}

	@Test
	void toolBar() {
		FlatToolBarUI ui = new FlatToolBarUI();

		ui.applyStyle( "borderMargins: 1,2,3,4" );
		ui.applyStyle( "gripColor: #fff" );
	}

	@Test
	void toolBarSeparator() {
		FlatToolBarSeparatorUI ui = new FlatToolBarSeparatorUI( false );

		ui.applyStyle( "separatorWidth: 6" );
		ui.applyStyle( "separatorColor: #fff" );
	}

	@Test
	void tree() {
		FlatTreeUI ui = new FlatTreeUI();
		ui.installUI( new JTree() );

		ui.applyStyle( "selectionBackground: #fff" );
		ui.applyStyle( "selectionForeground: #fff" );
		ui.applyStyle( "selectionInactiveBackground: #fff" );
		ui.applyStyle( "selectionInactiveForeground: #fff" );
		ui.applyStyle( "selectionBorderColor: #fff" );
		ui.applyStyle( "wideSelection: true" );
		ui.applyStyle( "showCellFocusIndicator: true" );

		// icons
		ui.applyStyle( "icon.arrowType: chevron" );
		ui.applyStyle( "icon.expandedColor: #fff" );
		ui.applyStyle( "icon.collapsedColor: #fff" );
		ui.applyStyle( "icon.leafColor: #fff" );
		ui.applyStyle( "icon.closedColor: #fff" );
		ui.applyStyle( "icon.openColor: #fff" );
	}

	//---- component borders --------------------------------------------------

	private void flatButtonBorder( Consumer<String> applyStyle ) {
		flatBorder( applyStyle );

		applyStyle.accept( "borderColor: #fff" );
		applyStyle.accept( "disabledBorderColor: #fff" );
		applyStyle.accept( "focusedBorderColor: #fff" );
		applyStyle.accept( "hoverBorderColor: #fff" );

		applyStyle.accept( "default.borderColor: #fff" );
		applyStyle.accept( "default.focusedBorderColor: #fff" );
		applyStyle.accept( "default.focusColor: #fff" );
		applyStyle.accept( "default.hoverBorderColor: #fff" );

		applyStyle.accept( "borderWidth: 1" );
		applyStyle.accept( "default.borderWidth: 2" );
		applyStyle.accept( "toolbar.margin: 1,2,3,4" );
		applyStyle.accept( "toolbar.spacingInsets: 1,2,3,4" );
		applyStyle.accept( "arc: 6" );
	}

	private void flatRoundBorder( Consumer<String> applyStyle ) {
		flatBorder( applyStyle );

		applyStyle.accept( "arc: 6" );
	}

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
	void flatButtonBorder() {
		FlatButtonBorder border = new FlatButtonBorder();

		// FlatButtonBorder extends FlatBorder
		flatBorder( border );

		border.applyStyleProperty( "borderColor", Color.WHITE );
		border.applyStyleProperty( "disabledBorderColor", Color.WHITE );
		border.applyStyleProperty( "focusedBorderColor", Color.WHITE );
		border.applyStyleProperty( "hoverBorderColor", Color.WHITE );

		border.applyStyleProperty( "default.borderColor", Color.WHITE );
		border.applyStyleProperty( "default.focusedBorderColor", Color.WHITE );
		border.applyStyleProperty( "default.focusColor", Color.WHITE );
		border.applyStyleProperty( "default.hoverBorderColor", Color.WHITE );

		border.applyStyleProperty( "borderWidth", 1 );
		border.applyStyleProperty( "default.borderWidth", 2 );
		border.applyStyleProperty( "toolbar.margin", new Insets( 1, 2, 3, 4 ) );
		border.applyStyleProperty( "toolbar.spacingInsets", new Insets( 1, 2, 3, 4 ) );
		border.applyStyleProperty( "arc", 6 );
	}

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

	@Test
	void flatCheckBoxMenuItemIcon() {
		FlatCheckBoxMenuItemIcon icon = new FlatCheckBoxMenuItemIcon();

		flatCheckBoxMenuItemIcon( icon );
	}

	@Test
	void flatRadioButtonMenuItemIcon() {
		FlatRadioButtonMenuItemIcon icon = new FlatRadioButtonMenuItemIcon();

		// FlatRadioButtonMenuItemIcon extends FlatCheckBoxMenuItemIcon
		flatCheckBoxMenuItemIcon( icon );
	}

	private void flatCheckBoxMenuItemIcon( FlatCheckBoxMenuItemIcon icon ) {
		icon.applyStyleProperty( "checkmarkColor", Color.WHITE );
		icon.applyStyleProperty( "disabledCheckmarkColor", Color.WHITE );
		icon.applyStyleProperty( "selectionForeground", Color.WHITE );
	}

	@Test
	void flatMenuArrowIcon() {
		FlatMenuArrowIcon icon = new FlatMenuArrowIcon();

		flatMenuArrowIcon( icon );
	}

	@Test
	void flatMenuItemArrowIcon() {
		FlatMenuItemArrowIcon icon = new FlatMenuItemArrowIcon();

		// FlatMenuItemArrowIcon extends FlatMenuArrowIcon
		flatMenuArrowIcon( icon );
	}

	private void flatMenuArrowIcon( FlatMenuArrowIcon icon ) {
		icon.applyStyleProperty( "arrowType", "chevron" );
		icon.applyStyleProperty( "arrowColor", Color.WHITE );
		icon.applyStyleProperty( "disabledArrowColor", Color.WHITE );
		icon.applyStyleProperty( "selectionForeground", Color.WHITE );
	}
	@Test
	void flatHelpButtonIcon() {
		FlatHelpButtonIcon icon = new FlatHelpButtonIcon();

		icon.applyStyleProperty( "focusWidth", 2 );
		icon.applyStyleProperty( "focusColor", Color.WHITE );
		icon.applyStyleProperty( "innerFocusWidth", 0.5f );
		icon.applyStyleProperty( "borderWidth", 1 );

		icon.applyStyleProperty( "borderColor", Color.WHITE );
		icon.applyStyleProperty( "disabledBorderColor", Color.WHITE );
		icon.applyStyleProperty( "focusedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "hoverBorderColor", Color.WHITE );
		icon.applyStyleProperty( "background", Color.WHITE );
		icon.applyStyleProperty( "disabledBackground", Color.WHITE );
		icon.applyStyleProperty( "focusedBackground", Color.WHITE );
		icon.applyStyleProperty( "hoverBackground", Color.WHITE );
		icon.applyStyleProperty( "pressedBackground", Color.WHITE );
		icon.applyStyleProperty( "questionMarkColor", Color.WHITE );
		icon.applyStyleProperty( "disabledQuestionMarkColor", Color.WHITE );
	}
}
