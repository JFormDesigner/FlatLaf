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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.formdev.flatlaf.ui.TestUtils.assertMapEquals;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.icons.*;

/**
 * @author Karl Tauber
 */
public class TestFlatStyleableInfo
{
	@BeforeAll
	static void setup() {
		TestUtils.setup( false );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
	}

	private Map<String, Class<?>> expectedMap( Object... keyValuePairs ) {
		Map<String, Class<?>> map = new LinkedHashMap<>();
		expectedMap( map, keyValuePairs );
		return map;
	}

	private void expectedMap( Map<String, Class<?>> map, Object... keyValuePairs ) {
		for( int i = 0; i < keyValuePairs.length; i += 2 )
			map.put( (String) keyValuePairs[i], (Class<?>) keyValuePairs[i+1] );
	}

	//---- components ---------------------------------------------------------

	@Test
	void button() {
		JButton b = new JButton();
		FlatButtonUI ui = (FlatButtonUI) b.getUI();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		button( expected );

		//---- FlatHelpButtonIcon ----

		expectedMap( expected,
			"help.focusWidth", int.class,
			"help.focusColor", Color.class,
			"help.innerFocusWidth", float.class,
			"help.borderWidth", int.class,

			"help.borderColor", Color.class,
			"help.disabledBorderColor", Color.class,
			"help.focusedBorderColor", Color.class,
			"help.hoverBorderColor", Color.class,
			"help.background", Color.class,
			"help.disabledBackground", Color.class,
			"help.focusedBackground", Color.class,
			"help.hoverBackground", Color.class,
			"help.pressedBackground", Color.class,
			"help.questionMarkColor", Color.class,
			"help.disabledQuestionMarkColor", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( b ) );
	}

	private void button( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"minimumWidth", int.class,

			"focusedBackground", Color.class,
			"focusedForeground", Color.class,
			"hoverBackground", Color.class,
			"hoverForeground", Color.class,
			"pressedBackground", Color.class,
			"pressedForeground", Color.class,
			"selectedBackground", Color.class,
			"selectedForeground", Color.class,
			"disabledBackground", Color.class,
			"disabledText", Color.class,
			"disabledSelectedBackground", Color.class,
			"disabledSelectedForeground", Color.class,

			"default.background", Color.class,
			"default.foreground", Color.class,
			"default.focusedBackground", Color.class,
			"default.focusedForeground", Color.class,
			"default.hoverBackground", Color.class,
			"default.hoverForeground", Color.class,
			"default.pressedBackground", Color.class,
			"default.pressedForeground", Color.class,
			"default.boldText", boolean.class,

			"paintShadow", boolean.class,
			"shadowWidth", int.class,
			"shadowColor", Color.class,
			"default.shadowColor", Color.class,

			"toolbar.spacingInsets", Insets.class,
			"toolbar.hoverBackground", Color.class,
			"toolbar.hoverForeground", Color.class,
			"toolbar.pressedBackground", Color.class,
			"toolbar.pressedForeground", Color.class,
			"toolbar.selectedBackground", Color.class,
			"toolbar.selectedForeground", Color.class,
			"toolbar.disabledSelectedBackground", Color.class,
			"toolbar.disabledSelectedForeground", Color.class,

			"buttonType", String.class,
			"squareSize", boolean.class,
			"minimumHeight", int.class
		);

		// border
		flatButtonBorder( expected );
	}

	@Test
	void checkBox() {
		JCheckBox c = new JCheckBox();
		FlatCheckBoxUI ui = (FlatCheckBoxUI) c.getUI();

		assertTrue( ui.getDefaultIcon() instanceof FlatCheckBoxIcon );

		// FlatCheckBoxUI extends FlatRadioButtonUI
		Map<String, Class<?>> expected = new LinkedHashMap<>();
		radioButton( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void comboBox() {
		JComboBox<Object> c = new JComboBox<>();
		FlatComboBoxUI ui = (FlatComboBoxUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"padding", Insets.class,

			"minimumWidth", int.class,
			"editorColumns", int.class,
			"buttonStyle", String.class,
			"arrowType", String.class,

			"editableBackground", Color.class,
			"focusedBackground", Color.class,
			"disabledBackground", Color.class,
			"disabledForeground", Color.class,

			"buttonBackground", Color.class,
			"buttonFocusedBackground", Color.class,
			"buttonEditableBackground", Color.class,
			"buttonSeparatorWidth", float.class,
			"buttonSeparatorColor", Color.class,
			"buttonDisabledSeparatorColor", Color.class,
			"buttonArrowColor", Color.class,
			"buttonDisabledArrowColor", Color.class,
			"buttonHoverArrowColor", Color.class,
			"buttonPressedArrowColor", Color.class,

			"popupBackground", Color.class,
			"popupInsets", Insets.class,
			"selectionInsets", Insets.class,
			"selectionArc", int.class
		);

		// border
		flatRoundBorder( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void editorPane() {
		JEditorPane c = new JEditorPane();
		FlatEditorPaneUI ui = (FlatEditorPaneUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"minimumWidth", int.class,
			"disabledBackground", Color.class,
			"inactiveBackground", Color.class,
			"focusedBackground", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void formattedTextField() {
		JFormattedTextField c = new JFormattedTextField();
		FlatFormattedTextFieldUI ui = (FlatFormattedTextFieldUI) c.getUI();

		// FlatFormattedTextFieldUI extends FlatTextFieldUI
		Map<String, Class<?>> expected = new LinkedHashMap<>();
		textField( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void internalFrame() {
		JInternalFrame c = new JInternalFrame();
		FlatInternalFrameUI ui = (FlatInternalFrameUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"activeBorderColor", Color.class,
			"inactiveBorderColor", Color.class,
			"borderLineWidth", int.class,
			"dropShadowPainted", boolean.class,
			"borderMargins", Insets.class,

			"activeDropShadowColor", Color.class,
			"activeDropShadowInsets", Insets.class,
			"activeDropShadowOpacity", float.class,
			"inactiveDropShadowColor", Color.class,
			"inactiveDropShadowInsets", Insets.class,
			"inactiveDropShadowOpacity", float.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void label() {
		JLabel c = new JLabel();
		FlatLabelUI ui = (FlatLabelUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"disabledForeground", Color.class,
			"arc", int.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void list() {
		JList<Object> c = new JList<>();
		FlatListUI ui = (FlatListUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"selectionBackground", Color.class,
			"selectionForeground", Color.class,
			"selectionInactiveBackground", Color.class,
			"selectionInactiveForeground", Color.class,
			"alternateRowColor", Color.class,
			"selectionInsets", Insets.class,
			"selectionArc", int.class,

			// FlatListCellBorder
			"cellMargins", Insets.class,
			"cellFocusColor", Color.class,
			"showCellFocusIndicator", Boolean.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void menuBar() {
		JMenuBar c = new JMenuBar();
		FlatMenuBarUI ui = (FlatMenuBarUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"itemMargins", Insets.class,
			"selectionInsets", Insets.class,
			"selectionEmbeddedInsets", Insets.class,
			"selectionArc", int.class,
			"hoverBackground", Color.class,
			"selectionBackground", Color.class,
			"selectionForeground", Color.class,
			"underlineSelectionBackground", Color.class,
			"underlineSelectionColor", Color.class,
			"underlineSelectionHeight", int.class,

			// FlatMenuBarBorder
			"borderColor", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void menu() {
		JMenu c = new JMenu();
		FlatMenuUI ui = (FlatMenuUI) c.getUI();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		menuItem( expected );
		menuItem_arrowIcon( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void menuItem() {
		JMenuItem c = new JMenuItem();
		FlatMenuItemUI ui = (FlatMenuItemUI) c.getUI();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		menuItem( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void checkBoxMenuItem() {
		JCheckBoxMenuItem c = new JCheckBoxMenuItem();
		FlatCheckBoxMenuItemUI ui = (FlatCheckBoxMenuItemUI) c.getUI();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		menuItem( expected );
		menuItem_checkIcon( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void radioButtonMenuItem() {
		JRadioButtonMenuItem c = new JRadioButtonMenuItem();
		FlatRadioButtonMenuItemUI ui = (FlatRadioButtonMenuItemUI) c.getUI();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		menuItem( expected );
		menuItem_checkIcon( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	private void menuItem( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"selectionBackground", Color.class,
			"selectionForeground", Color.class,
			"disabledForeground", Color.class,
			"acceleratorForeground", Color.class,
			"acceleratorSelectionForeground", Color.class,
			"acceleratorFont", Font.class
		);

		menuItemRenderer( expected );
	}

	private void menuItemRenderer( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"verticallyAlignText", boolean.class,
			"minimumWidth", int.class,
			"minimumIconSize", Dimension.class,
			"textAcceleratorGap", int.class,
			"textNoAcceleratorGap", int.class,
			"acceleratorArrowGap", int.class,

			"checkBackground", Color.class,
			"checkMargins", Insets.class,

			"selectionInsets", Insets.class,
			"selectionArc", int.class,

			"underlineSelectionBackground", Color.class,
			"underlineSelectionCheckBackground", Color.class,
			"underlineSelectionColor", Color.class,
			"underlineSelectionHeight", int.class
		);
	}

	private void menuItem_checkIcon( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"icon.checkmarkColor", Color.class,
			"icon.disabledCheckmarkColor", Color.class,
			"selectionForeground", Color.class
		);
	}

	private void menuItem_arrowIcon( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"icon.arrowType", String.class,
			"icon.arrowColor", Color.class,
			"icon.disabledArrowColor", Color.class,
			"selectionForeground", Color.class
		);
	}

	@Test
	void panel() {
		JPanel c = new JPanel();
		FlatPanelUI ui = (FlatPanelUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"arc", int.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void passwordField() {
		JPasswordField c = new JPasswordField();
		FlatPasswordFieldUI ui = (FlatPasswordFieldUI) c.getUI();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		expectedMap( expected,
			"showCapsLock", boolean.class,
			"showRevealButton", boolean.class
		);

		// FlatPasswordFieldUI extends FlatTextFieldUI
		textField( expected );

		expectedMap( expected,
			// capsLockIcon
			"capsLockIconColor", Color.class
		);

		// border
		flatTextBorder( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void popupMenu() {
		JPopupMenu c = new JPopupMenu();
		FlatPopupMenuUI ui = (FlatPopupMenuUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"arrowType", String.class,
			"scrollArrowColor", Color.class,
			"hoverScrollArrowBackground", Color.class,

			"borderInsets", Insets.class,
			"borderColor", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void popupMenuSeparator() {
		JPopupMenu.Separator c = new JPopupMenu.Separator();
		FlatPopupMenuSeparatorUI ui = (FlatPopupMenuSeparatorUI) c.getUI();

		Map<String, Class<?>> expected = new LinkedHashMap<>();

		// FlatPopupMenuSeparatorUI extends FlatSeparatorUI
		separator( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void progressBar() {
		JProgressBar c = new JProgressBar();
		FlatProgressBarUI ui = (FlatProgressBarUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"arc", int.class,
			"horizontalSize", Dimension.class,
			"verticalSize", Dimension.class,

			"largeHeight", boolean.class,
			"square", boolean.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void radioButton() {
		JRadioButton c = new JRadioButton();
		FlatRadioButtonUI ui = (FlatRadioButtonUI) c.getUI();

		assertTrue( ui.getDefaultIcon() instanceof FlatRadioButtonIcon );

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		radioButton( expected );

		expectedMap( expected,
			"icon.centerDiameter", float.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	private void radioButton( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"disabledText", Color.class,

			//---- icon ----

			"icon.focusWidth", float.class,
			"icon.focusColor", Color.class,
			"icon.borderWidth", float.class,
			"icon.selectedBorderWidth", float.class,
			"icon.disabledSelectedBorderWidth", float.class,
			"icon.indeterminateBorderWidth", float.class,
			"icon.disabledIndeterminateBorderWidth", float.class,
			"icon.arc", int.class,

			// enabled
			"icon.borderColor", Color.class,
			"icon.background", Color.class,
			"icon.selectedBorderColor", Color.class,
			"icon.selectedBackground", Color.class,
			"icon.checkmarkColor", Color.class,
			"icon.indeterminateBorderColor", Color.class,
			"icon.indeterminateBackground", Color.class,
			"icon.indeterminateCheckmarkColor", Color.class,

			// disabled
			"icon.disabledBorderColor", Color.class,
			"icon.disabledBackground", Color.class,
			"icon.disabledSelectedBorderColor", Color.class,
			"icon.disabledSelectedBackground", Color.class,
			"icon.disabledCheckmarkColor", Color.class,
			"icon.disabledIndeterminateBorderColor", Color.class,
			"icon.disabledIndeterminateBackground", Color.class,
			"icon.disabledIndeterminateCheckmarkColor", Color.class,

			// focused
			"icon.focusedBorderColor", Color.class,
			"icon.focusedBackground", Color.class,
			"icon.focusedSelectedBorderColor", Color.class,
			"icon.focusedSelectedBackground", Color.class,
			"icon.focusedCheckmarkColor", Color.class,
			"icon.focusedIndeterminateBorderColor", Color.class,
			"icon.focusedIndeterminateBackground", Color.class,
			"icon.focusedIndeterminateCheckmarkColor", Color.class,

			// hover
			"icon.hoverBorderColor", Color.class,
			"icon.hoverBackground", Color.class,
			"icon.hoverSelectedBorderColor", Color.class,
			"icon.hoverSelectedBackground", Color.class,
			"icon.hoverCheckmarkColor", Color.class,
			"icon.hoverIndeterminateBorderColor", Color.class,
			"icon.hoverIndeterminateBackground", Color.class,
			"icon.hoverIndeterminateCheckmarkColor", Color.class,

			// pressed
			"icon.pressedBorderColor", Color.class,
			"icon.pressedBackground", Color.class,
			"icon.pressedSelectedBorderColor", Color.class,
			"icon.pressedSelectedBackground", Color.class,
			"icon.pressedCheckmarkColor", Color.class,
			"icon.pressedIndeterminateBorderColor", Color.class,
			"icon.pressedIndeterminateBackground", Color.class,
			"icon.pressedIndeterminateCheckmarkColor", Color.class
		);
	}

	@Test
	void scrollBar() {
		JScrollBar c = new JScrollBar();
		FlatScrollBarUI ui = (FlatScrollBarUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"track", Color.class,
			"thumb", Color.class,
			"width", int.class,
			"minimumThumbSize", Dimension.class,
			"maximumThumbSize", Dimension.class,
			"allowsAbsolutePositioning", boolean.class,

			"minimumButtonSize", Dimension.class,
			"trackInsets", Insets.class,
			"thumbInsets", Insets.class,
			"trackArc", int.class,
			"thumbArc", int.class,
			"hoverTrackColor", Color.class,
			"hoverThumbColor", Color.class,
			"hoverThumbWithTrack", boolean.class,
			"pressedTrackColor", Color.class,
			"pressedThumbColor", Color.class,
			"pressedThumbWithTrack", boolean.class,

			"showButtons", boolean.class,
			"arrowType", String.class,
			"buttonArrowColor", Color.class,
			"buttonDisabledArrowColor", Color.class,
			"hoverButtonBackground", Color.class,
			"pressedButtonBackground", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void scrollPane() {
		JScrollPane c = new JScrollPane();
		FlatScrollPaneUI ui = (FlatScrollPaneUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"showButtons", Boolean.class
		);

		// border
		flatScrollPaneBorder( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void separator() {
		JSeparator c = new JSeparator();
		FlatSeparatorUI ui = (FlatSeparatorUI) c.getUI();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		separator( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	private void separator( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"height", int.class,
			"stripeWidth", int.class,
			"stripeIndent", int.class
		);
	}

	@Test
	void slider() {
		JSlider c = new JSlider();
		FlatSliderUI ui = (FlatSliderUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"trackWidth", int.class,
			"thumbSize", Dimension.class,
			"focusWidth", int.class,
			"thumbBorderWidth", float.class,

			"trackValueColor", Color.class,
			"trackColor", Color.class,
			"thumbColor", Color.class,
			"thumbBorderColor", Color.class,
			"focusedColor", Color.class,
			"focusedThumbBorderColor", Color.class,
			"hoverThumbColor", Color.class,
			"pressedThumbColor", Color.class,
			"disabledTrackColor", Color.class,
			"disabledThumbColor", Color.class,
			"disabledThumbBorderColor", Color.class,
			"tickColor", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void spinner() {
		JSpinner c = new JSpinner();
		FlatSpinnerUI ui = (FlatSpinnerUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"minimumWidth", int.class,
			"buttonStyle", String.class,
			"arrowType", String.class,
			"disabledBackground", Color.class,
			"disabledForeground", Color.class,
			"focusedBackground", Color.class,
			"buttonBackground", Color.class,
			"buttonSeparatorWidth", float.class,
			"buttonSeparatorColor", Color.class,
			"buttonDisabledSeparatorColor", Color.class,
			"buttonArrowColor", Color.class,
			"buttonDisabledArrowColor", Color.class,
			"buttonHoverArrowColor", Color.class,
			"buttonPressedArrowColor", Color.class,
			"padding", Insets.class
		);

		// border
		flatRoundBorder( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void splitPane() {
		JSplitPane c = new JSplitPane();
		FlatSplitPaneUI ui = (FlatSplitPaneUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"arrowType", String.class,
			"draggingColor", Color.class,
			"hoverColor", Color.class,
			"pressedColor", Color.class,
			"oneTouchArrowColor", Color.class,
			"oneTouchHoverArrowColor", Color.class,
			"oneTouchPressedArrowColor", Color.class,

			"style", String.class,
			"gripColor", Color.class,
			"gripDotCount", int.class,
			"gripDotSize", int.class,
			"gripGap", int.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void tabbedPane() {
		JTabbedPane c = new JTabbedPane();
		FlatTabbedPaneUI ui = (FlatTabbedPaneUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"tabInsets", Insets.class,
			"tabAreaInsets", Insets.class,
			"textIconGap", int.class,

			"disabledForeground", Color.class,

			"selectedBackground", Color.class,
			"selectedForeground", Color.class,
			"underlineColor", Color.class,
			"inactiveUnderlineColor", Color.class,
			"disabledUnderlineColor", Color.class,
			"hoverColor", Color.class,
			"hoverForeground", Color.class,
			"focusColor", Color.class,
			"focusForeground", Color.class,
			"tabSeparatorColor", Color.class,
			"contentAreaColor", Color.class,

			"minimumTabWidth", int.class,
			"maximumTabWidth", int.class,
			"tabHeight", int.class,
			"tabSelectionHeight", int.class,
			"cardTabSelectionHeight", int.class,
			"tabArc", int.class,
			"tabSelectionArc", int.class,
			"cardTabArc", int.class,
			"selectedInsets", Insets.class,
			"tabSelectionInsets", Insets.class,
			"contentSeparatorHeight", int.class,
			"showTabSeparators", boolean.class,
			"tabSeparatorsFullHeight", boolean.class,
			"hasFullBorder", boolean.class,
			"tabsOpaque", boolean.class,
			"rotateTabRuns", boolean.class,

			"tabType", String.class,
			"tabsPopupPolicy", String.class,
			"scrollButtonsPolicy", String.class,
			"scrollButtonsPlacement", String.class,

			"tabAreaAlignment", String.class,
			"tabAlignment", String.class,
			"tabWidthMode", String.class,
			"tabRotation", String.class,

			"arrowType", String.class,
			"buttonInsets", Insets.class,
			"buttonArc", int.class,
			"buttonHoverBackground", Color.class,
			"buttonPressedBackground", Color.class,

			"moreTabsButtonToolTipText", String.class,
			"tabCloseToolTipText", String.class,

			"showContentSeparator", boolean.class,
			"hideTabAreaWithOneTab", boolean.class,
			"tabClosable", boolean.class,
			"tabIconPlacement", int.class,

			// FlatTabbedPaneCloseIcon
			"closeSize", Dimension.class,
			"closeArc", int.class,
			"closeCrossPlainSize", float.class,
			"closeCrossFilledSize", float.class,
			"closeCrossLineWidth", float.class,
			"closeBackground", Color.class,
			"closeForeground", Color.class,
			"closeHoverBackground", Color.class,
			"closeHoverForeground", Color.class,
			"closePressedBackground", Color.class,
			"closePressedForeground", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void table() {
		JTable c = new JTable();
		FlatTableUI ui = (FlatTableUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"showTrailingVerticalLine", boolean.class,
			"selectionBackground", Color.class,
			"selectionForeground", Color.class,
			"selectionInactiveBackground", Color.class,
			"selectionInactiveForeground", Color.class,
			"selectionInsets", Insets.class,
			"selectionArc", int.class,

			// FlatTableCellBorder
			"cellMargins", Insets.class,
			"cellFocusColor", Color.class,
			"showCellFocusIndicator", Boolean.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void tableHeader() {
		JTableHeader c = new JTableHeader();
		FlatTableHeaderUI ui = (FlatTableHeaderUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"hoverBackground", Color.class,
			"hoverForeground", Color.class,
			"pressedBackground", Color.class,
			"pressedForeground", Color.class,
			"bottomSeparatorColor", Color.class,
			"height", int.class,
			"sortIconPosition", String.class,

			// FlatTableHeaderBorder
			"cellMargins", Insets.class,
			"separatorColor", Color.class,
			"showTrailingVerticalLine", Boolean.class,

			// FlatAscendingSortIcon and FlatDescendingSortIcon
			"arrowType", String.class,
			"sortIconColor", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void textArea() {
		JTextArea c = new JTextArea();
		FlatTextAreaUI ui = (FlatTextAreaUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"minimumWidth", int.class,
			"disabledBackground", Color.class,
			"inactiveBackground", Color.class,
			"focusedBackground", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void textField() {
		JTextField c = new JTextField();
		FlatTextFieldUI ui = (FlatTextFieldUI) c.getUI();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		textField( expected );

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	private void textField( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"minimumWidth", int.class,
			"disabledBackground", Color.class,
			"inactiveBackground", Color.class,
			"placeholderForeground", Color.class,
			"focusedBackground", Color.class,
			"iconTextGap", int.class,
			"leadingIcon", Icon.class,
			"trailingIcon", Icon.class,
			"showClearButton", boolean.class
		);

		// border
		flatTextBorder( expected );
	}

	@Test
	void textPane() {
		JTextPane c = new JTextPane();
		FlatTextPaneUI ui = (FlatTextPaneUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"minimumWidth", int.class,
			"disabledBackground", Color.class,
			"inactiveBackground", Color.class,
			"focusedBackground", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void toggleButton() {
		JToggleButton b = new JToggleButton();
		FlatToggleButtonUI ui = (FlatToggleButtonUI) b.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"tab.underlineHeight", int.class,
			"tab.underlineColor", Color.class,
			"tab.disabledUnderlineColor", Color.class,
			"tab.selectedBackground", Color.class,
			"tab.selectedForeground", Color.class,
			"tab.hoverBackground", Color.class,
			"tab.hoverForeground", Color.class,
			"tab.focusBackground", Color.class,
			"tab.focusForeground", Color.class
		);

		// FlatToggleButtonUI extends FlatButtonUI
		button( expected );

		assertMapEquals( expected, ui.getStyleableInfos( b ) );
	}

	@Test
	void toolBar() {
		JToolBar c = new JToolBar();
		FlatToolBarUI ui = (FlatToolBarUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"focusableButtons", boolean.class,
			"arrowKeysOnlyNavigation", boolean.class,
			"hoverButtonGroupArc", int.class,
			"hoverButtonGroupBackground", Color.class,

			"borderMargins", Insets.class,
			"gripColor", Color.class,

			"separatorWidth", Integer.class,
			"separatorColor", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void toolBarSeparator() {
		JToolBar.Separator c = new JToolBar.Separator();
		FlatToolBarSeparatorUI ui = (FlatToolBarSeparatorUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"separatorWidth", int.class,
			"separatorColor", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	@Test
	void tree() {
		JTree c = new JTree();
		FlatTreeUI ui = (FlatTreeUI) c.getUI();

		Map<String, Class<?>> expected = expectedMap(
			"selectionBackground", Color.class,
			"selectionForeground", Color.class,
			"selectionInactiveBackground", Color.class,
			"selectionInactiveForeground", Color.class,
			"selectionBorderColor", Color.class,
			"alternateRowColor", Color.class,
			"selectionInsets", Insets.class,
			"selectionArc", int.class,
			"wideSelection", boolean.class,
			"wideCellRenderer", boolean.class,
			"showCellFocusIndicator", boolean.class,

			"paintSelection", boolean.class,

			// icons
			"icon.arrowType", String.class,
			"icon.expandedColor", Color.class,
			"icon.collapsedColor", Color.class,
			"icon.leafColor", Color.class,
			"icon.closedColor", Color.class,
			"icon.openColor", Color.class
		);

		assertMapEquals( expected, ui.getStyleableInfos( c ) );
	}

	//---- component borders --------------------------------------------------

	private void flatButtonBorder( Map<String, Class<?>> expected ) {
		flatBorder( expected );

		expectedMap( expected,
			"arc", int.class,

			"borderColor", Color.class,
			"disabledBorderColor", Color.class,
			"focusedBorderColor", Color.class,
			"hoverBorderColor", Color.class,
			"pressedBorderColor", Color.class,

			"selectedBorderColor", Color.class,
			"disabledSelectedBorderColor", Color.class,
			"focusedSelectedBorderColor", Color.class,
			"hoverSelectedBorderColor", Color.class,
			"pressedSelectedBorderColor", Color.class,

			"default.borderWidth", float.class,
			"default.borderColor", Color.class,
			"default.focusedBorderColor", Color.class,
			"default.focusColor", Color.class,
			"default.hoverBorderColor", Color.class,
			"default.pressedBorderColor", Color.class,

			"toolbar.focusWidth", float.class,
			"toolbar.focusColor", Color.class,
			"toolbar.margin", Insets.class,
			"toolbar.spacingInsets", Insets.class
		);
	}

	private void flatRoundBorder( Map<String, Class<?>> expected ) {
		flatBorder( expected );

		expectedMap( expected,
			"arc", int.class,
			"roundRect", Boolean.class
		);
	}

	private void flatScrollPaneBorder( Map<String, Class<?>> expected ) {
		flatBorder( expected );

		expectedMap( expected,
			"arc", int.class
		);
	}

	private void flatTextBorder( Map<String, Class<?>> expected ) {
		flatBorder( expected );

		expectedMap( expected,
			"arc", int.class,
			"roundRect", Boolean.class
		);
	}

	private void flatBorder( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"focusWidth", int.class,
			"innerFocusWidth", float.class,
			"innerOutlineWidth", float.class,
			"borderWidth", float.class,

			"focusColor", Color.class,
			"borderColor", Color.class,
			"disabledBorderColor", Color.class,
			"focusedBorderColor", Color.class,

			"error.borderColor", Color.class,
			"error.focusedBorderColor", Color.class,
			"warning.borderColor", Color.class,
			"warning.focusedBorderColor", Color.class,
			"success.borderColor", Color.class,
			"success.focusedBorderColor", Color.class,
			"custom.borderColor", Color.class,

			"outline", String.class,
			"outlineColor", Color.class,
			"outlineFocusedColor", Color.class
		);
	}

	//---- borders ------------------------------------------------------------

	@Test
	void flatButtonBorder() {
		FlatButtonBorder border = new FlatButtonBorder();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		flatButtonBorder( expected );

		assertMapEquals( expected, border.getStyleableInfos() );
	}

	@Test
	void flatRoundBorder() {
		FlatRoundBorder border = new FlatRoundBorder();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		flatRoundBorder( expected );

		assertMapEquals( expected, border.getStyleableInfos() );
	}

	@Test
	void flatTextBorder() {
		FlatTextBorder border = new FlatTextBorder();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		flatTextBorder( expected );

		assertMapEquals( expected, border.getStyleableInfos() );
	}

	@Test
	void flatBorder() {
		FlatBorder border = new FlatBorder();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		flatBorder( expected );

		assertMapEquals( expected, border.getStyleableInfos() );
	}

	//---- icons --------------------------------------------------------------

	@Test
	void flatCheckBoxIcon() {
		FlatCheckBoxIcon icon = new FlatCheckBoxIcon();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		flatCheckBoxIcon( expected );

		assertMapEquals( expected, icon.getStyleableInfos() );
	}

	@Test
	void flatRadioButtonIcon() {
		FlatRadioButtonIcon icon = new FlatRadioButtonIcon();

		// FlatRadioButtonIcon extends FlatCheckBoxIcon
		Map<String, Class<?>> expected = new LinkedHashMap<>();
		flatCheckBoxIcon( expected );

		expectedMap( expected,
			"centerDiameter", float.class
		);

		assertMapEquals( expected, icon.getStyleableInfos() );
	}

	private void flatCheckBoxIcon( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"focusWidth", float.class,
			"focusColor", Color.class,
			"borderWidth", float.class,
			"selectedBorderWidth", float.class,
			"disabledSelectedBorderWidth", float.class,
			"indeterminateBorderWidth", float.class,
			"disabledIndeterminateBorderWidth", float.class,
			"arc", int.class,

			// enabled
			"borderColor", Color.class,
			"background", Color.class,
			"selectedBorderColor", Color.class,
			"selectedBackground", Color.class,
			"checkmarkColor", Color.class,
			"indeterminateBorderColor", Color.class,
			"indeterminateBackground", Color.class,
			"indeterminateCheckmarkColor", Color.class,

			// disabled
			"disabledBorderColor", Color.class,
			"disabledBackground", Color.class,
			"disabledSelectedBorderColor", Color.class,
			"disabledSelectedBackground", Color.class,
			"disabledCheckmarkColor", Color.class,
			"disabledIndeterminateBorderColor", Color.class,
			"disabledIndeterminateBackground", Color.class,
			"disabledIndeterminateCheckmarkColor", Color.class,

			// focused
			"focusedBorderColor", Color.class,
			"focusedBackground", Color.class,
			"focusedSelectedBorderColor", Color.class,
			"focusedSelectedBackground", Color.class,
			"focusedCheckmarkColor", Color.class,
			"focusedIndeterminateBorderColor", Color.class,
			"focusedIndeterminateBackground", Color.class,
			"focusedIndeterminateCheckmarkColor", Color.class,

			// hover
			"hoverBorderColor", Color.class,
			"hoverBackground", Color.class,
			"hoverSelectedBorderColor", Color.class,
			"hoverSelectedBackground", Color.class,
			"hoverCheckmarkColor", Color.class,
			"hoverIndeterminateBorderColor", Color.class,
			"hoverIndeterminateBackground", Color.class,
			"hoverIndeterminateCheckmarkColor", Color.class,

			// pressed
			"pressedBorderColor", Color.class,
			"pressedBackground", Color.class,
			"pressedSelectedBorderColor", Color.class,
			"pressedSelectedBackground", Color.class,
			"pressedCheckmarkColor", Color.class,
			"pressedIndeterminateBorderColor", Color.class,
			"pressedIndeterminateBackground", Color.class,
			"pressedIndeterminateCheckmarkColor", Color.class
		);
	}

	@Test
	void flatCheckBoxMenuItemIcon() {
		FlatCheckBoxMenuItemIcon icon = new FlatCheckBoxMenuItemIcon();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		flatCheckBoxMenuItemIcon( expected );

		assertMapEquals( expected, icon.getStyleableInfos() );
	}

	@Test
	void flatRadioButtonMenuItemIcon() {
		FlatRadioButtonMenuItemIcon icon = new FlatRadioButtonMenuItemIcon();

		// FlatRadioButtonMenuItemIcon extends FlatCheckBoxMenuItemIcon
		Map<String, Class<?>> expected = new LinkedHashMap<>();
		flatCheckBoxMenuItemIcon( expected );

		assertMapEquals( expected, icon.getStyleableInfos() );
	}

	private void flatCheckBoxMenuItemIcon( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"checkmarkColor", Color.class,
			"disabledCheckmarkColor", Color.class,
			"selectionForeground", Color.class
		);
	}

	@Test
	void flatMenuArrowIcon() {
		FlatMenuArrowIcon icon = new FlatMenuArrowIcon();

		Map<String, Class<?>> expected = new LinkedHashMap<>();
		flatMenuArrowIcon( expected );

		assertMapEquals( expected, icon.getStyleableInfos() );
	}

	private void flatMenuArrowIcon( Map<String, Class<?>> expected ) {
		expectedMap( expected,
			"arrowType", String.class,
			"arrowColor", Color.class,
			"disabledArrowColor", Color.class,
			"selectionForeground", Color.class
		);
	}

	@Test
	void flatHelpButtonIcon() {
		FlatHelpButtonIcon icon = new FlatHelpButtonIcon();

		Map<String, Class<?>> expected = expectedMap(
			"focusWidth", int.class,
			"focusColor", Color.class,
			"innerFocusWidth", float.class,
			"borderWidth", int.class,

			"borderColor", Color.class,
			"disabledBorderColor", Color.class,
			"focusedBorderColor", Color.class,
			"hoverBorderColor", Color.class,
			"background", Color.class,
			"disabledBackground", Color.class,
			"focusedBackground", Color.class,
			"hoverBackground", Color.class,
			"pressedBackground", Color.class,
			"questionMarkColor", Color.class,
			"disabledQuestionMarkColor", Color.class
		);

		assertMapEquals( expected, icon.getStyleableInfos() );
	}
}
