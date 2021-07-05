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
import javax.swing.table.JTableHeader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.icons.*;

/**
 * @author Karl Tauber
 */
public class FlatStylingTests
{
	@BeforeAll
	static void setup() {
		FlatLightLaf.setup();
	}

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
		JButton b = new JButton();
		FlatButtonUI ui = (FlatButtonUI) b.getUI();

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
		JCheckBox c = new JCheckBox();
		FlatCheckBoxUI ui = (FlatCheckBoxUI) c.getUI();

		assertTrue( ui.getDefaultIcon() instanceof FlatCheckBoxIcon );

		// FlatCheckBoxUI extends FlatRadioButtonUI
		radioButton( ui );
	}

	@Test
	void comboBox() {
		JComboBox<Object> c = new JComboBox<>();
		FlatComboBoxUI ui = (FlatComboBoxUI) c.getUI();

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

		ui.applyStyle( "popupBackground: #fff" );

		// border
		flatRoundBorder( style -> ui.applyStyle( style ) );
	}

	@Test
	void editorPane() {
		JEditorPane c = new JEditorPane();
		FlatEditorPaneUI ui = (FlatEditorPaneUI) c.getUI();

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );
	}

	@Test
	void formattedTextField() {
		JFormattedTextField c = new JFormattedTextField();
		FlatFormattedTextFieldUI ui = (FlatFormattedTextFieldUI) c.getUI();

		// FlatFormattedTextFieldUI extends FlatTextFieldUI
		textField( ui );
	}

	@Test
	void label() {
		JLabel c = new JLabel();
		FlatLabelUI ui = (FlatLabelUI) c.getUI();

		ui.applyStyle( "disabledForeground: #fff" );
	}

	@Test
	void list() {
		JList<Object> c = new JList<>();
		FlatListUI ui = (FlatListUI) c.getUI();

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
		JMenu c = new JMenu();
		FlatMenuUI ui = (FlatMenuUI) c.getUI();

		Consumer<String> applyStyle = style -> ui.applyStyle( style );
		menuItem( applyStyle );
		menuItem_arrowIcon( applyStyle );
	}

	@Test
	void menuItem() {
		JMenuItem c = new JMenuItem();
		FlatMenuItemUI ui = (FlatMenuItemUI) c.getUI();

		Consumer<String> applyStyle = style -> ui.applyStyle( style );
		menuItem( applyStyle );
		menuItem_arrowIcon( applyStyle );
	}

	@Test
	void checkBoxMenuItem() {
		JCheckBoxMenuItem c = new JCheckBoxMenuItem();
		FlatCheckBoxMenuItemUI ui = (FlatCheckBoxMenuItemUI) c.getUI();

		Consumer<String> applyStyle = style -> ui.applyStyle( style );
		menuItem( applyStyle );
		menuItem_arrowIcon( applyStyle );
		menuItem_checkIcon( applyStyle );
	}

	@Test
	void radioButtonMenuItem() {
		JRadioButtonMenuItem c = new JRadioButtonMenuItem();
		FlatRadioButtonMenuItemUI ui = (FlatRadioButtonMenuItemUI) c.getUI();

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
		JPasswordField c = new JPasswordField();
		FlatPasswordFieldUI ui = (FlatPasswordFieldUI) c.getUI();

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
		JPopupMenu.Separator c = new JPopupMenu.Separator();
		FlatPopupMenuSeparatorUI ui = (FlatPopupMenuSeparatorUI) c.getUI();

		// FlatPopupMenuSeparatorUI extends FlatSeparatorUI
		separator( ui );
	}

	@Test
	void progressBar() {
		JProgressBar c = new JProgressBar();
		FlatProgressBarUI ui = (FlatProgressBarUI) c.getUI();

		ui.applyStyle( "arc: 5" );
		ui.applyStyle( "horizontalSize: 100,12" );
		ui.applyStyle( "verticalSize: 12,100" );
	}

	@Test
	void radioButton() {
		JRadioButton c = new JRadioButton();
		FlatRadioButtonUI ui = (FlatRadioButtonUI) c.getUI();

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
		JScrollBar c = new JScrollBar();
		FlatScrollBarUI ui = (FlatScrollBarUI) c.getUI();

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
		JScrollPane c = new JScrollPane();
		FlatScrollPaneUI ui = (FlatScrollPaneUI) c.getUI();

		// border
		flatBorder( style -> ui.applyStyle( style ) );
	}

	@Test
	void separator() {
		JSeparator c = new JSeparator();
		FlatSeparatorUI ui = (FlatSeparatorUI) c.getUI();

		separator( ui );
	}

	private void separator( FlatSeparatorUI ui ) {
		ui.applyStyle( "height: 6" );
		ui.applyStyle( "stripeWidth: 2" );
		ui.applyStyle( "stripeIndent: 10" );
	}

	@Test
	void slider() {
		JSlider c = new JSlider();
		FlatSliderUI ui = (FlatSliderUI) c.getUI();

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
		JSpinner c = new JSpinner();
		FlatSpinnerUI ui = (FlatSpinnerUI) c.getUI();

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
		JSplitPane c = new JSplitPane();
		FlatSplitPaneUI ui = (FlatSplitPaneUI) c.getUI();

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
	void tabbedPane() {
		JTabbedPane c = new JTabbedPane();
		FlatTabbedPaneUI ui = (FlatTabbedPaneUI) c.getUI();

		ui.applyStyle( "tabInsets: 1,2,3,4" );
		ui.applyStyle( "tabAreaInsets: 1,2,3,4" );

		ui.applyStyle( "disabledForeground: #fff" );

		ui.applyStyle( "selectedBackground: #fff" );
		ui.applyStyle( "selectedForeground: #fff" );
		ui.applyStyle( "underlineColor: #fff" );
		ui.applyStyle( "disabledUnderlineColor: #fff" );
		ui.applyStyle( "hoverColor: #fff" );
		ui.applyStyle( "focusColor: #fff" );
		ui.applyStyle( "tabSeparatorColor: #fff" );
		ui.applyStyle( "contentAreaColor: #fff" );

		ui.applyStyle( "textIconGap: 4" );
		ui.applyStyle( "minimumTabWidth: 50" );
		ui.applyStyle( "maximumTabWidth: 100" );
		ui.applyStyle( "tabHeight: 30" );
		ui.applyStyle( "tabSelectionHeight: 3" );
		ui.applyStyle( "contentSeparatorHeight: 1" );
		ui.applyStyle( "showTabSeparators: false" );
		ui.applyStyle( "tabSeparatorsFullHeight: false" );
		ui.applyStyle( "hasFullBorder: false" );
		ui.applyStyle( "tabsOpaque: false" );

		ui.applyStyle( "tabsPopupPolicy: asNeeded" );
		ui.applyStyle( "scrollButtonsPolicy: asNeeded" );
		ui.applyStyle( "scrollButtonsPlacement: both" );

		ui.applyStyle( "tabAreaAlignment: leading" );
		ui.applyStyle( "tabAlignment: center" );
		ui.applyStyle( "tabWidthMode: preferred" );

		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "buttonInsets: 1,2,3,4" );
		ui.applyStyle( "buttonArc: 3" );
		ui.applyStyle( "buttonHoverBackground: #fff" );
		ui.applyStyle( "buttonPressedBackground: #fff" );

		ui.applyStyle( "moreTabsButtonToolTipText: Gimme more" );

		// FlatTabbedPaneCloseIcon
		ui.applyStyle( "closeSize: 16,16" );
		ui.applyStyle( "closeArc: 4" );
		ui.applyStyle( "closeCrossPlainSize: {float}7.5" );
		ui.applyStyle( "closeCrossFilledSize: {float}7.5" );
		ui.applyStyle( "closeCrossLineWidth: {float}1" );
		ui.applyStyle( "closeBackground: #fff" );
		ui.applyStyle( "closeForeground: #fff" );
		ui.applyStyle( "closeHoverBackground: #fff" );
		ui.applyStyle( "closeHoverForeground: #fff" );
		ui.applyStyle( "closePressedBackground: #fff" );
		ui.applyStyle( "closePressedForeground: #fff" );
	}

	@Test
	void table() {
		JTable c = new JTable();
		FlatTableUI ui = (FlatTableUI) c.getUI();

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
		JTableHeader c = new JTableHeader();
		FlatTableHeaderUI ui = (FlatTableHeaderUI) c.getUI();

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
		JTextArea c = new JTextArea();
		FlatTextAreaUI ui = (FlatTextAreaUI) c.getUI();

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );
	}

	@Test
	void textField() {
		JTextField c = new JTextField();
		FlatTextFieldUI ui = (FlatTextFieldUI) c.getUI();

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
		JTextPane c = new JTextPane();
		FlatTextPaneUI ui = (FlatTextPaneUI) c.getUI();

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );
	}

	@Test
	void toggleButton() {
		JToggleButton b = new JToggleButton();
		FlatToggleButtonUI ui = (FlatToggleButtonUI) b.getUI();

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
		JToolBar c = new JToolBar();
		FlatToolBarUI ui = (FlatToolBarUI) c.getUI();

		ui.applyStyle( "borderMargins: 1,2,3,4" );
		ui.applyStyle( "gripColor: #fff" );
	}

	@Test
	void toolBarSeparator() {
		JToolBar.Separator c = new JToolBar.Separator();
		FlatToolBarSeparatorUI ui = (FlatToolBarSeparatorUI) c.getUI();

		ui.applyStyle( "separatorWidth: 6" );
		ui.applyStyle( "separatorColor: #fff" );
	}

	@Test
	void tree() {
		JTree c = new JTree();
		FlatTreeUI ui = (FlatTreeUI) c.getUI();

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
