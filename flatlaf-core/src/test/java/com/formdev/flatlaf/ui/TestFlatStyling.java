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
import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.*;
import com.formdev.flatlaf.util.ColorFunctions;

/**
 * @author Karl Tauber
 */
public class TestFlatStyling
{
	@BeforeAll
	static void setup() {
		HashMap<String, String> globalExtraDefaults = new HashMap<>();
		globalExtraDefaults.put( "@var1", "#f00" );
		globalExtraDefaults.put( "@var2", "@var1" );
		globalExtraDefaults.put( "var2Resolved", "@var2" );
		FlatLaf.setGlobalExtraDefaults( globalExtraDefaults );

		TestUtils.setup( false );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();

		FlatLaf.setGlobalExtraDefaults( null );
	}

	@Test
	void parse() {
		assertEquals( null, FlatStylingSupport.parse( null ) );
		assertEquals( null, FlatStylingSupport.parse( "" ) );
		assertEquals( null, FlatStylingSupport.parse( "  " ) );
		assertEquals( null, FlatStylingSupport.parse( ";" ) );
		assertEquals( null, FlatStylingSupport.parse( " ; ; " ) );

		assertEquals(
			expectedMap( "background", Color.WHITE ),
			FlatStylingSupport.parse( "background: #fff" ) );
		assertEquals(
			expectedMap( "background", Color.WHITE, "foreground", Color.BLACK ),
			FlatStylingSupport.parse( "background: #fff; foreground: #000" ) );
		assertEquals(
			expectedMap( "background", Color.WHITE, "foreground", Color.BLACK, "someWidth", 20 ),
			FlatStylingSupport.parse( "background: #fff; foreground: #000; someWidth: 20" ) );
	}

	@Test
	void parseIfFunction() {
		testColorStyle( 0x00ff00, "if(#000,#0f0,#dfd)" );
		testColorStyle( 0xddffdd, "if(null,#0f0,#dfd)" );
		testColorStyle( 0x00ff00, "if(true,#0f0,#dfd)" );
		testColorStyle( 0xddffdd, "if(false,#0f0,#dfd)" );
		testColorStyle( 0x00ff00, "if(1,#0f0,#dfd)" );
		testColorStyle( 0xddffdd, "if(0,#0f0,#dfd)" );

		// nested
		testColorStyle( 0xff6666, "if(true,lighten(#f00,20%),darken(#f00,20%))" );
		testColorStyle( 0x990000, "if(false,lighten(#f00,20%),darken(#f00,20%))" );
		testColorStyle( 0xddffdd, "if($undefinedProp,#0f0,#dfd)" );
		testColorStyle( 0x33ff33, "lighten(if(#000,#0f0,#dfd), 10%)" );
	}

	@Test
	void parseColorFunctions() {
		// rgb, rgba, hsl, hsla
		testColorStyle( 0x0c2238, "rgb(12,34,56)" );
		testColorStyle( 0x4e0c2238, "rgba(12,34,56,78)" );
		testColorStyle( 0xb57869, "hsl(12,34%,56%)" );
		testColorStyle( 0xc7b57869, "hsla(12,34%,56%,78%)" );

		// lighten, darken
		testColorStyle( 0xff6666, "lighten(#f00,20%)" );
		testColorStyle( 0x990000, "darken(#f00,20%)" );

		// saturate, desaturate
		testColorStyle( 0x9c3030, "saturate(#844,20%)" );
		testColorStyle( 0x745858, "desaturate(#844,20%)" );

		// fadein, fadeout, fade
		testColorStyle( 0x4dff0000, "fadein(#ff000000,30%)" );
		testColorStyle( 0x99ff0000, "fadeout(#ff0000,40%)" );
		testColorStyle( 0x80ff0000, "fade(#ff0000,50%)" );

		// spin
		testColorStyle( 0xffaa00, "spin(#f00,40)" );
		testColorStyle( 0xff00aa, "spin(#f00,-40)" );

		// changeHue, changeSaturation, changeLightness, changeAlpha
		testColorStyle( 0x00ffff, "changeHue(#f00,180)" );
		testColorStyle( 0xbf4040, "changeSaturation(#f00,50%)" );
		testColorStyle( 0xff9999, "changeLightness(#f00,80%)" );
		testColorStyle( 0x80ff0000, "changeAlpha(#f00,50%)" );

		// mix
		testColorStyle( 0x1ae600, "mix(#f00,#0f0,10%)" );
		testColorStyle( 0x40bf00, "mix(#f00,#0f0,25%)" );
		testColorStyle( 0x808000, "mix(#f00,#0f0)" );
		testColorStyle( 0xbf4000, "mix(#f00,#0f0,75%)" );
		testColorStyle( 0xe61a00, "mix(#f00,#0f0,90%)" );

		// tint
		testColorStyle( 0xff40ff, "tint(#f0f,25%)" );
		testColorStyle( 0xff80ff, "tint(#f0f)" );
		testColorStyle( 0xffbfff, "tint(#f0f,75%)" );

		// shade
		testColorStyle( 0xbf00bf, "shade(#f0f,25%)" );
		testColorStyle( 0x800080, "shade(#f0f)" );
		testColorStyle( 0x400040, "shade(#f0f,75%)" );

		// contrast
		testColorStyle( 0xffffff, "contrast(#111,#000,#fff)" );
		testColorStyle( 0x000000, "contrast(#eee,#000,#fff)" );

		// nested
		testColorStyle( 0xd1c7c7, "saturate(darken(#fff,20%),10%)" );
		testColorStyle( 0xcf00cf, "shade(shade(#f0f,10%),10%)" );
		testColorStyle( 0xba00ba, "shade(shade(shade(#f0f,10%),10%),10%)" );
		testColorStyle( 0x000000, "contrast(contrast(#222,#111,#eee),contrast(#eee,#000,#fff),contrast(#111,#000,#fff))" );
	}

	@Test
	void parseReferences() {
		UIManager.put( "Test.background", Color.white );
		assertEquals( Color.white, UIManager.getColor( "Test.background" ) );

		testColorStyle( 0xffffff, "$Test.background" );
		testColorStyle( 0xcccccc, "darken($Test.background,20%)" );
		testColorStyle( 0xd1c7c7, "saturate(darken($Test.background,20%),10%)" );

		testStyle( "hideMnemonics", true, "$Component.hideMnemonics" );
		testStyle( "arc", 6, "$Button.arc" );
		testStyle( "dropShadowOpacity", 0.15f, "$Popup.dropShadowOpacity" );
		testStyle( "margin", new Insets( 2, 14, 2, 14 ) , "$Button.margin" );
		testStyle( "iconSize", new Dimension( 64, 64 ), "$DesktopIcon.iconSize" );
		testStyle( "arrowType", "chevron", "$Component.arrowType" );
	}

	@Test
	void parseVariables() {
		Color background = UIManager.getColor( "Panel.background" );

		testColorStyle( background.getRGB(), "@background" );
		testColorStyle(
			ColorFunctions.darken( background, 0.2f ).getRGB(),
			"darken(@background,20%)" );
		testColorStyle(
			ColorFunctions.saturate( ColorFunctions.darken( background, 0.2f ), 0.1f ).getRGB(),
			"saturate(darken(@background,20%),10%)" );
	}

	@Test
	void parseRecursiveVariables() {
		Color background = UIManager.getColor( "var2Resolved" );

		testColorStyle( background.getRGB(), "@var2" );
	}

	private void testColorStyle( int expectedRGB, String style ) {
		testStyle( "background", new Color( expectedRGB, (expectedRGB & 0xff000000) != 0 ), style );
	}

	private void testStyle( String key, Object expected, String style ) {
		assertEquals(
			expectedMap( key, expected ),
			FlatStylingSupport.parse( key + ": " + style ) );
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
		ui.applyStyle( b, "help.innerFocusWidth: 0.5" );
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
		ui.applyStyle( b, "focusedForeground: #fff" );
		ui.applyStyle( b, "hoverBackground: #fff" );
		ui.applyStyle( b, "hoverForeground: #fff" );
		ui.applyStyle( b, "pressedBackground: #fff" );
		ui.applyStyle( b, "pressedForeground: #fff" );
		ui.applyStyle( b, "selectedBackground: #fff" );
		ui.applyStyle( b, "selectedForeground: #fff" );
		ui.applyStyle( b, "disabledBackground: #fff" );
		ui.applyStyle( b, "disabledText: #fff" );
		ui.applyStyle( b, "disabledSelectedBackground: #fff" );
		ui.applyStyle( b, "disabledSelectedForeground: #fff" );

		ui.applyStyle( b, "default.background: #fff" );
		ui.applyStyle( b, "default.foreground: #fff" );
		ui.applyStyle( b, "default.focusedBackground: #fff" );
		ui.applyStyle( b, "default.focusedForeground: #fff" );
		ui.applyStyle( b, "default.hoverBackground: #fff" );
		ui.applyStyle( b, "default.hoverForeground: #fff" );
		ui.applyStyle( b, "default.pressedBackground: #fff" );
		ui.applyStyle( b, "default.pressedForeground: #fff" );
		ui.applyStyle( b, "default.boldText: true" );

		ui.applyStyle( b, "paintShadow: true" );
		ui.applyStyle( b, "shadowWidth: 2" );
		ui.applyStyle( b, "shadowColor: #fff" );
		ui.applyStyle( b, "default.shadowColor: #fff" );

		ui.applyStyle( b, "toolbar.spacingInsets: 1,2,3,4" );
		ui.applyStyle( b, "toolbar.hoverBackground: #fff" );
		ui.applyStyle( b, "toolbar.hoverForeground: #fff" );
		ui.applyStyle( b, "toolbar.pressedBackground: #fff" );
		ui.applyStyle( b, "toolbar.pressedForeground: #fff" );
		ui.applyStyle( b, "toolbar.selectedBackground: #fff" );
		ui.applyStyle( b, "toolbar.selectedForeground: #fff" );
		ui.applyStyle( b, "toolbar.disabledSelectedBackground: #fff" );
		ui.applyStyle( b, "toolbar.disabledSelectedForeground: #fff" );

		ui.applyStyle( b, "buttonType: help" );
		ui.applyStyle( b, "squareSize: true" );
		ui.applyStyle( b, "minimumHeight: 100" );

		// border
		flatButtonBorder( style -> ui.applyStyle( b, style ) );

		// JComponent properties
		ui.applyStyle( b, "background: #fff" );
		ui.applyStyle( b, "foreground: #fff" );
		ui.applyStyle( b, "border: 2,2,2,2,#f00" );
		ui.applyStyle( b, "font: italic 12 monospaced" );

		// AbstractButton properties
		ui.applyStyle( b, "margin: 2,2,2,2" );
		ui.applyStyle( b, "iconTextGap: 4" );
	}

	@Test
	void checkBox() {
		JCheckBox c = new JCheckBox();
		FlatCheckBoxUI ui = (FlatCheckBoxUI) c.getUI();

		assertTrue( ui.getDefaultIcon() instanceof FlatCheckBoxIcon );

		// FlatCheckBoxUI extends FlatRadioButtonUI
		radioButton( ui, c );
	}

	@Test
	void comboBox() {
		JComboBox<Object> c = new JComboBox<>();
		FlatComboBoxUI ui = (FlatComboBoxUI) c.getUI();

		ui.applyStyle( "padding: 1,2,3,4" );

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "editorColumns: 10" );
		ui.applyStyle( "buttonStyle: auto" );
		ui.applyStyle( "arrowType: chevron" );

		ui.applyStyle( "editableBackground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "disabledForeground: #fff" );

		ui.applyStyle( "buttonBackground: #fff" );
		ui.applyStyle( "buttonFocusedBackground: #fff" );
		ui.applyStyle( "buttonEditableBackground: #fff" );
		ui.applyStyle( "buttonSeparatorWidth: 1.5" );
		ui.applyStyle( "buttonSeparatorColor: #fff" );
		ui.applyStyle( "buttonDisabledSeparatorColor: #fff" );
		ui.applyStyle( "buttonArrowColor: #fff" );
		ui.applyStyle( "buttonDisabledArrowColor: #fff" );
		ui.applyStyle( "buttonHoverArrowColor: #fff" );
		ui.applyStyle( "buttonPressedArrowColor: #fff" );

		ui.applyStyle( "popupBackground: #fff" );
		ui.applyStyle( "popupInsets: 1,2,3,4" );
		ui.applyStyle( "selectionInsets: 1,2,3,4" );
		ui.applyStyle( "selectionArc: 8" );

		// border
		flatRoundBorder( style -> ui.applyStyle( style ) );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );

		// JComboBox properties
		ui.applyStyle( "maximumRowCount: 20" );
	}

	@Test
	void editorPane() {
		JEditorPane c = new JEditorPane();
		FlatEditorPaneUI ui = (FlatEditorPaneUI) c.getUI();

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );

		// JTextComponent properties
		ui.applyStyle( "caretColor: #fff" );
		ui.applyStyle( "selectionColor: #fff" );
		ui.applyStyle( "selectedTextColor: #fff" );
		ui.applyStyle( "disabledTextColor: #fff" );
		ui.applyStyle( "margin: 2,2,2,2" );
	}

	@Test
	void formattedTextField() {
		JFormattedTextField c = new JFormattedTextField();
		FlatFormattedTextFieldUI ui = (FlatFormattedTextFieldUI) c.getUI();

		// FlatFormattedTextFieldUI extends FlatTextFieldUI
		textField( ui );
	}

	@Test
	void internalFrame() {
		JInternalFrame c = new JInternalFrame();
		FlatInternalFrameUI ui = (FlatInternalFrameUI) c.getUI();

		ui.applyStyle( "activeBorderColor: #fff" );
		ui.applyStyle( "inactiveBorderColor: #fff" );
		ui.applyStyle( "borderLineWidth: 123" );
		ui.applyStyle( "dropShadowPainted: false" );
		ui.applyStyle( "borderMargins: 1,2,3,4" );

		ui.applyStyle( "activeDropShadowColor: #fff" );
		ui.applyStyle( "activeDropShadowInsets: 1,2,3,4" );
		ui.applyStyle( "activeDropShadowOpacity: 0.5" );
		ui.applyStyle( "inactiveDropShadowColor: #fff" );
		ui.applyStyle( "inactiveDropShadowInsets: 1,2,3,4" );
		ui.applyStyle( "inactiveDropShadowOpacity: 0.5" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
	}

	@Test
	void label() {
		JLabel c = new JLabel();
		FlatLabelUI ui = (FlatLabelUI) c.getUI();

		ui.applyStyle( c, "disabledForeground: #fff" );
		ui.applyStyle( c, "arc: 8" );

		// JComponent properties
		ui.applyStyle( c, "background: #fff" );
		ui.applyStyle( c, "foreground: #fff" );
		ui.applyStyle( c, "border: 2,2,2,2,#f00" );
		ui.applyStyle( c, "font: italic 12 monospaced" );

		// JLabel properties
		ui.applyStyle( c, "icon: com.formdev.flatlaf.icons.FlatTreeExpandedIcon" );
	}

	@Test
	void list() {
		JList<Object> c = new JList<>();
		FlatListUI ui = (FlatListUI) c.getUI();

		ui.applyStyle( "selectionBackground: #fff" );
		ui.applyStyle( "selectionForeground: #fff" );
		ui.applyStyle( "selectionInactiveBackground: #fff" );
		ui.applyStyle( "selectionInactiveForeground: #fff" );
		ui.applyStyle( "alternateRowColor: #fff" );
		ui.applyStyle( "selectionInsets: 1,2,3,4" );
		ui.applyStyle( "selectionArc: 8" );

		// FlatListCellBorder
		ui.applyStyle( "cellMargins: 1,2,3,4" );
		ui.applyStyle( "cellFocusColor: #fff" );
		ui.applyStyle( "showCellFocusIndicator: true" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );

		// JList properties
		ui.applyStyle( "visibleRowCount: 20" );
	}

	@Test
	void menuBar() {
		JMenuBar c = new JMenuBar();
		FlatMenuBarUI ui = (FlatMenuBarUI) c.getUI();

		ui.applyStyle( "itemMargins: 1,2,3,4" );
		ui.applyStyle( "selectionInsets: 1,2,3,4" );
		ui.applyStyle( "selectionEmbeddedInsets: 1,2,3,4" );
		ui.applyStyle( "selectionArc: 8" );
		ui.applyStyle( "hoverBackground: #fff" );
		ui.applyStyle( "selectionBackground: #fff" );
		ui.applyStyle( "selectionForeground: #fff" );
		ui.applyStyle( "underlineSelectionBackground: #fff" );
		ui.applyStyle( "underlineSelectionColor: #fff" );
		ui.applyStyle( "underlineSelectionHeight: 3" );

		// FlatMenuBarBorder
		ui.applyStyle( "borderColor: #fff" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );
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
	}

	@Test
	void checkBoxMenuItem() {
		JCheckBoxMenuItem c = new JCheckBoxMenuItem();
		FlatCheckBoxMenuItemUI ui = (FlatCheckBoxMenuItemUI) c.getUI();

		Consumer<String> applyStyle = style -> ui.applyStyle( style );
		menuItem( applyStyle );
		menuItem_checkIcon( applyStyle );
	}

	@Test
	void radioButtonMenuItem() {
		JRadioButtonMenuItem c = new JRadioButtonMenuItem();
		FlatRadioButtonMenuItemUI ui = (FlatRadioButtonMenuItemUI) c.getUI();

		Consumer<String> applyStyle = style -> ui.applyStyle( style );
		menuItem( applyStyle );
		menuItem_checkIcon( applyStyle );
	}

	private void menuItem( Consumer<String> applyStyle ) {
		applyStyle.accept( "selectionBackground: #fff" );
		applyStyle.accept( "selectionForeground: #fff" );
		applyStyle.accept( "disabledForeground: #fff" );
		applyStyle.accept( "acceleratorForeground: #fff" );
		applyStyle.accept( "acceleratorSelectionForeground: #fff" );
		applyStyle.accept( "acceleratorFont: italic 12 monospaced" );

		menuItemRenderer( applyStyle );

		// JComponent properties
		applyStyle.accept( "background: #fff" );
		applyStyle.accept( "foreground: #fff" );
		applyStyle.accept( "border: 2,2,2,2,#f00" );
		applyStyle.accept( "font: italic 12 monospaced" );

		// AbstractButton properties
		applyStyle.accept( "margin: 2,2,2,2" );
		applyStyle.accept( "iconTextGap: 4" );
	}

	private void menuItemRenderer( Consumer<String> applyStyle ) {
		applyStyle.accept( "verticallyAlignText: false" );
		applyStyle.accept( "minimumWidth: 10" );
		applyStyle.accept( "minimumIconSize: 16,16" );
		applyStyle.accept( "textAcceleratorGap: 28" );
		applyStyle.accept( "textNoAcceleratorGap: 6" );
		applyStyle.accept( "acceleratorArrowGap: 2" );

		applyStyle.accept( "checkBackground: #fff" );
		applyStyle.accept( "checkMargins: 1,2,3,4" );

		applyStyle.accept( "selectionInsets: 1,2,3,4" );
		applyStyle.accept( "selectionArc: 8" );

		applyStyle.accept( "underlineSelectionBackground: #fff" );
		applyStyle.accept( "underlineSelectionCheckBackground: #fff" );
		applyStyle.accept( "underlineSelectionColor: #fff" );
		applyStyle.accept( "underlineSelectionHeight: 3" );
	}

	private void menuItem_checkIcon( Consumer<String> applyStyle ) {
		applyStyle.accept( "icon.checkmarkColor: #fff" );
		applyStyle.accept( "icon.disabledCheckmarkColor: #fff" );
		applyStyle.accept( "selectionForeground: #fff" );
	}

	private void menuItem_arrowIcon( Consumer<String> applyStyle ) {
		applyStyle.accept( "icon.arrowType: chevron" );
		applyStyle.accept( "icon.arrowColor: #fff" );
		applyStyle.accept( "icon.disabledArrowColor: #fff" );
		applyStyle.accept( "selectionForeground: #fff" );
	}

	@Test
	void panel() {
		JPanel c = new JPanel();
		FlatPanelUI ui = (FlatPanelUI) c.getUI();

		ui.applyStyle( c, "arc: 8" );

		// JComponent properties
		ui.applyStyle( c, "background: #fff" );
		ui.applyStyle( c, "foreground: #fff" );
		ui.applyStyle( c, "border: 2,2,2,2,#f00" );
		ui.applyStyle( c, "font: italic 12 monospaced" );
	}

	@Test
	void passwordField() {
		JPasswordField c = new JPasswordField();
		FlatPasswordFieldUI ui = (FlatPasswordFieldUI) c.getUI();

		// FlatPasswordFieldUI extends FlatTextFieldUI
		textField( ui );

		ui.applyStyle( "showCapsLock: true" );
		ui.applyStyle( "showRevealButton: true" );

		// capsLockIcon
		ui.applyStyle( "capsLockIconColor: #fff" );

		// border
		flatTextBorder( style -> ui.applyStyle( style ) );
	}

	@Test
	void popupMenu() {
		JPopupMenu c = new JPopupMenu();
		FlatPopupMenuUI ui = (FlatPopupMenuUI) c.getUI();

		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "scrollArrowColor: #fff" );
		ui.applyStyle( "hoverScrollArrowBackground: #fff" );

		ui.applyStyle( "borderInsets: 1,2,3,4" );
		ui.applyStyle( "borderColor: #fff" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
	}

	@Test
	void popupMenuSeparator() {
		JPopupMenu.Separator c = new JPopupMenu.Separator();
		FlatPopupMenuSeparatorUI ui = (FlatPopupMenuSeparatorUI) c.getUI();

		// FlatPopupMenuSeparatorUI extends FlatSeparatorUI
		separator( ui, c );
	}

	@Test
	void progressBar() {
		JProgressBar c = new JProgressBar();
		FlatProgressBarUI ui = (FlatProgressBarUI) c.getUI();

		ui.applyStyle( "arc: 5" );
		ui.applyStyle( "horizontalSize: 100,12" );
		ui.applyStyle( "verticalSize: 12,100" );

		ui.applyStyle( "largeHeight: true" );
		ui.applyStyle( "square: true" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );
	}

	@Test
	void radioButton() {
		JRadioButton c = new JRadioButton();
		FlatRadioButtonUI ui = (FlatRadioButtonUI) c.getUI();

		assertTrue( ui.getDefaultIcon() instanceof FlatRadioButtonIcon );

		radioButton( ui, c );

		ui.applyStyle( c, "icon.centerDiameter: 8" );
	}

	private void radioButton( FlatRadioButtonUI ui, AbstractButton b ) {
		ui.applyStyle( b, "disabledText: #fff" );

		// JComponent properties
		ui.applyStyle( b, "background: #fff" );
		ui.applyStyle( b, "foreground: #fff" );
		ui.applyStyle( b, "border: 2,2,2,2,#f00" );
		ui.applyStyle( b, "font: italic 12 monospaced" );

		// AbstractButton properties
		ui.applyStyle( b, "margin: 2,2,2,2" );
		ui.applyStyle( b, "iconTextGap: 4" );

		//---- icon ----

		ui.applyStyle( b, "icon.focusWidth: 1.5" );
		ui.applyStyle( b, "icon.focusColor: #fff" );
		ui.applyStyle( b, "icon.borderWidth: 1.5" );
		ui.applyStyle( b, "icon.selectedBorderWidth: 1.5" );
		ui.applyStyle( b, "icon.disabledSelectedBorderWidth: 1.5" );
		ui.applyStyle( b, "icon.arc: 5" );

		// enabled
		ui.applyStyle( b, "icon.borderColor: #fff" );
		ui.applyStyle( b, "icon.background: #fff" );
		ui.applyStyle( b, "icon.selectedBorderColor: #fff" );
		ui.applyStyle( b, "icon.selectedBackground: #fff" );
		ui.applyStyle( b, "icon.checkmarkColor: #fff" );

		// disabled
		ui.applyStyle( b, "icon.disabledBorderColor: #fff" );
		ui.applyStyle( b, "icon.disabledBackground: #fff" );
		ui.applyStyle( b, "icon.disabledSelectedBorderColor: #fff" );
		ui.applyStyle( b, "icon.disabledSelectedBackground: #fff" );
		ui.applyStyle( b, "icon.disabledCheckmarkColor: #fff" );

		// focused
		ui.applyStyle( b, "icon.focusedBorderColor: #fff" );
		ui.applyStyle( b, "icon.focusedBackground: #fff" );
		ui.applyStyle( b, "icon.focusedSelectedBorderColor: #fff" );
		ui.applyStyle( b, "icon.focusedSelectedBackground: #fff" );
		ui.applyStyle( b, "icon.focusedCheckmarkColor: #fff" );

		// hover
		ui.applyStyle( b, "icon.hoverBorderColor: #fff" );
		ui.applyStyle( b, "icon.hoverBackground: #fff" );
		ui.applyStyle( b, "icon.hoverSelectedBorderColor: #fff" );
		ui.applyStyle( b, "icon.hoverSelectedBackground: #fff" );
		ui.applyStyle( b, "icon.hoverCheckmarkColor: #fff" );

		// pressed
		ui.applyStyle( b, "icon.pressedBorderColor: #fff" );
		ui.applyStyle( b, "icon.pressedBackground: #fff" );
		ui.applyStyle( b, "icon.pressedSelectedBorderColor: #fff" );
		ui.applyStyle( b, "icon.pressedSelectedBackground: #fff" );
		ui.applyStyle( b, "icon.pressedCheckmarkColor: #fff" );
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

		ui.applyStyle( "minimumButtonSize: 1,2" );
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

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
	}

	@Test
	void scrollPane() {
		JScrollPane c = new JScrollPane();
		FlatScrollPaneUI ui = (FlatScrollPaneUI) c.getUI();

		// border
		flatScrollPaneBorder( style -> ui.applyStyle( style ) );

		ui.applyStyle( "showButtons: true" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "viewportBorder: 2,2,2,2,#f00" );
	}

	@Test
	void separator() {
		JSeparator c = new JSeparator();
		FlatSeparatorUI ui = (FlatSeparatorUI) c.getUI();

		separator( ui, c );
	}

	private void separator( FlatSeparatorUI ui, JSeparator c ) {
		ui.applyStyle( c, "height: 6" );
		ui.applyStyle( c, "stripeWidth: 2" );
		ui.applyStyle( c, "stripeIndent: 10" );

		// JComponent properties
		ui.applyStyle( c, "background: #fff" );
		ui.applyStyle( c, "foreground: #fff" );
		ui.applyStyle( c, "border: 2,2,2,2,#f00" );
	}

	@Test
	void slider() {
		JSlider c = new JSlider();
		FlatSliderUI ui = (FlatSliderUI) c.getUI();

		ui.applyStyle( "trackWidth: 2" );
		ui.applyStyle( "thumbSize: 12,12" );
		ui.applyStyle( "focusWidth: 4" );
		ui.applyStyle( "thumbBorderWidth: 1.5" );

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

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );

		// JSlider properties
		ui.applyStyle( "minimum: 0" );
		ui.applyStyle( "maximum: 50" );
		ui.applyStyle( "value: 20" );
		ui.applyStyle( "extent: 5" );
		ui.applyStyle( "majorTickSpacing: 10" );
		ui.applyStyle( "minorTickSpacing: 10" );
		ui.applyStyle( "inverted: true" );
		ui.applyStyle( "paintLabels: true" );
		ui.applyStyle( "paintTicks: true" );
		ui.applyStyle( "paintTrack: true" );
		ui.applyStyle( "snapToTicks: true" );
	}

	@Test
	void spinner() {
		JSpinner c = new JSpinner();
		FlatSpinnerUI ui = (FlatSpinnerUI) c.getUI();

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "buttonStyle: button" );
		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "disabledForeground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );
		ui.applyStyle( "buttonBackground: #fff" );
		ui.applyStyle( "buttonSeparatorWidth: 1.5" );
		ui.applyStyle( "buttonSeparatorColor: #fff" );
		ui.applyStyle( "buttonDisabledSeparatorColor: #fff" );
		ui.applyStyle( "buttonArrowColor: #fff" );
		ui.applyStyle( "buttonDisabledArrowColor: #fff" );
		ui.applyStyle( "buttonHoverArrowColor: #fff" );
		ui.applyStyle( "buttonPressedArrowColor: #fff" );
		ui.applyStyle( "padding: 1,2,3,4" );

		// border
		flatRoundBorder( style -> ui.applyStyle( style ) );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );
	}

	@Test
	void splitPane() {
		JSplitPane c = new JSplitPane();
		FlatSplitPaneUI ui = (FlatSplitPaneUI) c.getUI();

		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "draggingColor: #fff" );
		ui.applyStyle( "hoverColor: #fff" );
		ui.applyStyle( "pressedColor: #fff" );
		ui.applyStyle( "oneTouchArrowColor: #fff" );
		ui.applyStyle( "oneTouchHoverArrowColor: #fff" );
		ui.applyStyle( "oneTouchPressedArrowColor: #fff" );

		ui.applyStyle( "style: grip" );
		ui.applyStyle( "gripColor: #fff" );
		ui.applyStyle( "gripDotCount: 3" );
		ui.applyStyle( "gripDotSize: 3" );
		ui.applyStyle( "gripGap: 2" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );

		// JSplitPane properties
		ui.applyStyle( "dividerSize: 20" );
	}

	@Test
	void tabbedPane() {
		JTabbedPane c = new JTabbedPane();
		FlatTabbedPaneUI ui = (FlatTabbedPaneUI) c.getUI();

		ui.applyStyle( "tabInsets: 1,2,3,4" );
		ui.applyStyle( "tabAreaInsets: 1,2,3,4" );
		ui.applyStyle( "textIconGap: 4" );

		ui.applyStyle( "disabledForeground: #fff" );

		ui.applyStyle( "selectedBackground: #fff" );
		ui.applyStyle( "selectedForeground: #fff" );
		ui.applyStyle( "underlineColor: #fff" );
		ui.applyStyle( "inactiveUnderlineColor: #fff" );
		ui.applyStyle( "disabledUnderlineColor: #fff" );
		ui.applyStyle( "hoverColor: #fff" );
		ui.applyStyle( "hoverForeground: #fff" );
		ui.applyStyle( "focusColor: #fff" );
		ui.applyStyle( "focusForeground: #fff" );
		ui.applyStyle( "tabSeparatorColor: #fff" );
		ui.applyStyle( "contentAreaColor: #fff" );

		ui.applyStyle( "minimumTabWidth: 50" );
		ui.applyStyle( "maximumTabWidth: 100" );
		ui.applyStyle( "tabHeight: 30" );
		ui.applyStyle( "tabSelectionHeight: 3" );
		ui.applyStyle( "cardTabSelectionHeight: 2" );
		ui.applyStyle( "tabArc: 3" );
		ui.applyStyle( "tabSelectionArc: 4" );
		ui.applyStyle( "cardTabArc: 5" );
		ui.applyStyle( "selectedInsets: 1,2,3,4" );
		ui.applyStyle( "tabSelectionInsets: 1,2,3,4" );
		ui.applyStyle( "contentSeparatorHeight: 1" );
		ui.applyStyle( "showTabSeparators: false" );
		ui.applyStyle( "tabSeparatorsFullHeight: false" );
		ui.applyStyle( "hasFullBorder: false" );
		ui.applyStyle( "tabsOpaque: false" );
		ui.applyStyle( "rotateTabRuns: false" );

		ui.applyStyle( "tabType: card" );
		ui.applyStyle( "tabsPopupPolicy: asNeeded" );
		ui.applyStyle( "scrollButtonsPolicy: asNeeded" );
		ui.applyStyle( "scrollButtonsPlacement: both" );

		ui.applyStyle( "tabAreaAlignment: leading" );
		ui.applyStyle( "tabAlignment: center" );
		ui.applyStyle( "tabWidthMode: preferred" );
		ui.applyStyle( "tabRotation: none" );

		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "buttonInsets: 1,2,3,4" );
		ui.applyStyle( "buttonArc: 3" );
		ui.applyStyle( "buttonHoverBackground: #fff" );
		ui.applyStyle( "buttonPressedBackground: #fff" );

		ui.applyStyle( "moreTabsButtonToolTipText: Gimme more" );
		ui.applyStyle( "tabCloseToolTipText: Close me" );

		ui.applyStyle( "showContentSeparator: true" );
		ui.applyStyle( "hideTabAreaWithOneTab: true" );
		ui.applyStyle( "tabClosable: true" );
		ui.applyStyle( "tabIconPlacement: top" );

		// FlatTabbedPaneCloseIcon
		ui.applyStyle( "closeSize: 16,16" );
		ui.applyStyle( "closeArc: 4" );
		ui.applyStyle( "closeCrossPlainSize: 7.5" );
		ui.applyStyle( "closeCrossFilledSize: 7.5" );
		ui.applyStyle( "closeCrossLineWidth: 1" );
		ui.applyStyle( "closeBackground: #fff" );
		ui.applyStyle( "closeForeground: #fff" );
		ui.applyStyle( "closeHoverBackground: #fff" );
		ui.applyStyle( "closeHoverForeground: #fff" );
		ui.applyStyle( "closePressedBackground: #fff" );
		ui.applyStyle( "closePressedForeground: #fff" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );
	}

	@Test
	void table() {
		JTable c = new JTable();
		FlatTableUI ui = (FlatTableUI) c.getUI();

		ui.applyStyle( "showTrailingVerticalLine: true" );
		ui.applyStyle( "selectionBackground: #fff" );
		ui.applyStyle( "selectionForeground: #fff" );
		ui.applyStyle( "selectionInactiveBackground: #fff" );
		ui.applyStyle( "selectionInactiveForeground: #fff" );
		ui.applyStyle( "selectionInsets: 1,2,3,4" );
		ui.applyStyle( "selectionArc: 8" );

		// FlatTableCellBorder
		ui.applyStyle( "cellMargins: 1,2,3,4" );
		ui.applyStyle( "cellFocusColor: #fff" );
		ui.applyStyle( "showCellFocusIndicator: true" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "font: italic 12 monospaced" );

		// JTable properties
		ui.applyStyle( "fillsViewportHeight: true" );
		ui.applyStyle( "rowHeight: 30" );
		ui.applyStyle( "showHorizontalLines: true" );
		ui.applyStyle( "showVerticalLines: true" );
		ui.applyStyle( "intercellSpacing: 1,1" );
	}

	@Test
	void tableHeader() {
		JTableHeader c = new JTableHeader();
		FlatTableHeaderUI ui = (FlatTableHeaderUI) c.getUI();

		ui.applyStyle( "hoverBackground: #fff" );
		ui.applyStyle( "hoverForeground: #fff" );
		ui.applyStyle( "pressedBackground: #fff" );
		ui.applyStyle( "pressedForeground: #fff" );
		ui.applyStyle( "bottomSeparatorColor: #fff" );
		ui.applyStyle( "height: 20" );
		ui.applyStyle( "sortIconPosition: top" );

		// FlatTableHeaderBorder
		ui.applyStyle( "cellMargins: 1,2,3,4" );
		ui.applyStyle( "separatorColor: #fff" );
		ui.applyStyle( "showTrailingVerticalLine: true" );

		// FlatAscendingSortIcon and FlatDescendingSortIcon
		ui.applyStyle( "arrowType: chevron" );
		ui.applyStyle( "sortIconColor: #fff" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "font: italic 12 monospaced" );
	}

	@Test
	void textArea() {
		JTextArea c = new JTextArea();
		FlatTextAreaUI ui = (FlatTextAreaUI) c.getUI();

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );

		// JTextComponent properties
		ui.applyStyle( "caretColor: #fff" );
		ui.applyStyle( "selectionColor: #fff" );
		ui.applyStyle( "selectedTextColor: #fff" );
		ui.applyStyle( "disabledTextColor: #fff" );
		ui.applyStyle( "margin: 2,2,2,2" );
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
		ui.applyStyle( "iconTextGap: 4" );
		ui.applyStyle( "leadingIcon: com.formdev.flatlaf.icons.FlatSearchIcon" );
		ui.applyStyle( "trailingIcon: com.formdev.flatlaf.icons.FlatClearIcon" );

		ui.applyStyle( "showClearButton: true" );

		// border
		flatTextBorder( style -> ui.applyStyle( style ) );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );

		// JTextComponent properties
		ui.applyStyle( "caretColor: #fff" );
		ui.applyStyle( "selectionColor: #fff" );
		ui.applyStyle( "selectedTextColor: #fff" );
		ui.applyStyle( "disabledTextColor: #fff" );
		ui.applyStyle( "margin: 2,2,2,2" );
	}

	@Test
	void textPane() {
		JTextPane c = new JTextPane();
		FlatTextPaneUI ui = (FlatTextPaneUI) c.getUI();

		ui.applyStyle( "minimumWidth: 100" );
		ui.applyStyle( "disabledBackground: #fff" );
		ui.applyStyle( "inactiveBackground: #fff" );
		ui.applyStyle( "focusedBackground: #fff" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );

		// JTextComponent properties
		ui.applyStyle( "caretColor: #fff" );
		ui.applyStyle( "selectionColor: #fff" );
		ui.applyStyle( "selectedTextColor: #fff" );
		ui.applyStyle( "disabledTextColor: #fff" );
		ui.applyStyle( "margin: 2,2,2,2" );
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
		ui.applyStyle( b, "tab.selectedForeground: #fff" );
		ui.applyStyle( b, "tab.hoverBackground: #fff" );
		ui.applyStyle( b, "tab.hoverForeground: #fff" );
		ui.applyStyle( b, "tab.focusBackground: #fff" );
		ui.applyStyle( b, "tab.focusForeground: #fff" );
	}

	@Test
	void toolBar() {
		JToolBar c = new JToolBar();
		FlatToolBarUI ui = (FlatToolBarUI) c.getUI();

		ui.applyStyle( "focusableButtons: true" );
		ui.applyStyle( "arrowKeysOnlyNavigation: true" );
		ui.applyStyle( "hoverButtonGroupArc: 12" );
		ui.applyStyle( "hoverButtonGroupBackground: #fff" );

		ui.applyStyle( "borderMargins: 1,2,3,4" );
		ui.applyStyle( "gripColor: #fff" );

		ui.applyStyle( "separatorWidth: 6" );
		ui.applyStyle( "separatorColor: #fff" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );

		// JToolBar properties
		ui.applyStyle( "borderPainted: true" );
		ui.applyStyle( "floatable: true" );
		ui.applyStyle( "margin: 2,2,2,2" );
		ui.applyStyle( "rollover: true" );
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
		ui.applyStyle( "alternateRowColor: #fff" );
		ui.applyStyle( "selectionInsets: 1,2,3,4" );
		ui.applyStyle( "selectionArc: 8" );
		ui.applyStyle( "wideSelection: true" );
		ui.applyStyle( "wideCellRenderer: true" );
		ui.applyStyle( "showCellFocusIndicator: true" );

		ui.applyStyle( "paintSelection: false" );

		// icons
		ui.applyStyle( "icon.arrowType: chevron" );
		ui.applyStyle( "icon.expandedColor: #fff" );
		ui.applyStyle( "icon.collapsedColor: #fff" );
		ui.applyStyle( "icon.leafColor: #fff" );
		ui.applyStyle( "icon.closedColor: #fff" );
		ui.applyStyle( "icon.openColor: #fff" );

		// JComponent properties
		ui.applyStyle( "background: #fff" );
		ui.applyStyle( "foreground: #fff" );
		ui.applyStyle( "border: 2,2,2,2,#f00" );
		ui.applyStyle( "font: italic 12 monospaced" );

		// JTree properties
		ui.applyStyle( "rootVisible: true" );
		ui.applyStyle( "rowHeight: 30" );
		ui.applyStyle( "scrollsOnExpand: true" );
		ui.applyStyle( "showsRootHandles: true" );
		ui.applyStyle( "visibleRowCount: 20" );
	}

	//---- component borders --------------------------------------------------

	private void flatButtonBorder( Consumer<String> applyStyle ) {
		flatBorder( applyStyle );

		applyStyle.accept( "arc: 6" );

		applyStyle.accept( "borderColor: #fff" );
		applyStyle.accept( "disabledBorderColor: #fff" );
		applyStyle.accept( "focusedBorderColor: #fff" );
		applyStyle.accept( "hoverBorderColor: #fff" );

		applyStyle.accept( "default.borderWidth: 2" );
		applyStyle.accept( "default.borderColor: #fff" );
		applyStyle.accept( "default.focusedBorderColor: #fff" );
		applyStyle.accept( "default.focusColor: #fff" );
		applyStyle.accept( "default.hoverBorderColor: #fff" );

		applyStyle.accept( "toolbar.focusWidth: 1.5" );
		applyStyle.accept( "toolbar.focusColor: #fff" );
		applyStyle.accept( "toolbar.margin: 1,2,3,4" );
		applyStyle.accept( "toolbar.spacingInsets: 1,2,3,4" );
	}

	private void flatRoundBorder( Consumer<String> applyStyle ) {
		flatBorder( applyStyle );

		applyStyle.accept( "arc: 6" );
		applyStyle.accept( "roundRect: true" );
	}

	private void flatScrollPaneBorder( Consumer<String> applyStyle ) {
		flatBorder( applyStyle );

		applyStyle.accept( "arc: 6" );
	}

	private void flatTextBorder( Consumer<String> applyStyle ) {
		flatBorder( applyStyle );

		applyStyle.accept( "arc: 6" );
		applyStyle.accept( "roundRect: true" );
	}

	private void flatBorder( Consumer<String> applyStyle ) {
		applyStyle.accept( "focusWidth: 2" );
		applyStyle.accept( "innerFocusWidth: 0.5" );
		applyStyle.accept( "innerOutlineWidth: 1.5" );
		applyStyle.accept( "borderWidth: 1" );

		applyStyle.accept( "focusColor: #fff" );
		applyStyle.accept( "borderColor: #fff" );
		applyStyle.accept( "disabledBorderColor: #fff" );
		applyStyle.accept( "focusedBorderColor: #fff" );

		applyStyle.accept( "error.borderColor: #fff" );
		applyStyle.accept( "error.focusedBorderColor: #fff" );
		applyStyle.accept( "warning.borderColor: #fff" );
		applyStyle.accept( "warning.focusedBorderColor: #fff" );
		applyStyle.accept( "success.borderColor: #fff" );
		applyStyle.accept( "success.focusedBorderColor: #fff" );
		applyStyle.accept( "custom.borderColor: desaturate(#f00,50%,relative derived noAutoInverse)" );

		applyStyle.accept( "outline: error" );
		applyStyle.accept( "outlineColor: #fff" );
		applyStyle.accept( "outlineFocusedColor: #fff" );
	}

	//---- borders ------------------------------------------------------------

	@Test
	void flatButtonBorder() {
		FlatButtonBorder border = new FlatButtonBorder();

		// FlatButtonBorder extends FlatBorder
		flatBorder( border );

		border.applyStyleProperty( "arc", 6 );

		border.applyStyleProperty( "borderColor", Color.WHITE );
		border.applyStyleProperty( "disabledBorderColor", Color.WHITE );
		border.applyStyleProperty( "focusedBorderColor", Color.WHITE );
		border.applyStyleProperty( "hoverBorderColor", Color.WHITE );
		border.applyStyleProperty( "pressedBorderColor", Color.WHITE );

		border.applyStyleProperty( "selectedBorderColor", Color.WHITE );
		border.applyStyleProperty( "disabledSelectedBorderColor", Color.WHITE );
		border.applyStyleProperty( "focusedSelectedBorderColor", Color.WHITE );
		border.applyStyleProperty( "hoverSelectedBorderColor", Color.WHITE );
		border.applyStyleProperty( "pressedSelectedBorderColor", Color.WHITE );

		border.applyStyleProperty( "default.borderWidth", 2 );
		border.applyStyleProperty( "default.borderColor", Color.WHITE );
		border.applyStyleProperty( "default.focusedBorderColor", Color.WHITE );
		border.applyStyleProperty( "default.focusColor", Color.WHITE );
		border.applyStyleProperty( "default.hoverBorderColor", Color.WHITE );
		border.applyStyleProperty( "default.pressedBorderColor", Color.WHITE );

		border.applyStyleProperty( "toolbar.focusWidth", 1.5f );
		border.applyStyleProperty( "toolbar.focusColor", Color.WHITE );
		border.applyStyleProperty( "toolbar.margin", new Insets( 1, 2, 3, 4 ) );
		border.applyStyleProperty( "toolbar.spacingInsets", new Insets( 1, 2, 3, 4 ) );
	}

	@Test
	void flatRoundBorder() {
		FlatRoundBorder border = new FlatRoundBorder();

		// FlatRoundBorder extends FlatBorder
		flatBorder( border );

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
		border.applyStyleProperty( "borderWidth", 1 );

		border.applyStyleProperty( "focusColor", Color.WHITE );
		border.applyStyleProperty( "borderColor", Color.WHITE );
		border.applyStyleProperty( "disabledBorderColor", Color.WHITE );
		border.applyStyleProperty( "focusedBorderColor", Color.WHITE );

		border.applyStyleProperty( "error.borderColor", Color.WHITE );
		border.applyStyleProperty( "error.focusedBorderColor", Color.WHITE );
		border.applyStyleProperty( "warning.borderColor", Color.WHITE );
		border.applyStyleProperty( "warning.focusedBorderColor", Color.WHITE );
		border.applyStyleProperty( "success.borderColor", Color.WHITE );
		border.applyStyleProperty( "success.focusedBorderColor", Color.WHITE );
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

		icon.applyStyleProperty( "centerDiameter", 8f );
	}

	private void flatCheckBoxIcon( FlatCheckBoxIcon icon ) {
		icon.applyStyleProperty( "focusWidth", 1.5f );
		icon.applyStyleProperty( "focusColor", Color.WHITE );
		icon.applyStyleProperty( "borderWidth", 1.5f );
		icon.applyStyleProperty( "selectedBorderWidth", 1.5f );
		icon.applyStyleProperty( "disabledSelectedBorderWidth", 1.5f );
		icon.applyStyleProperty( "indeterminateBorderWidth", 1.5f );
		icon.applyStyleProperty( "disabledIndeterminateBorderWidth", 1.5f );
		icon.applyStyleProperty( "arc", 5 );

		// enabled
		icon.applyStyleProperty( "borderColor", Color.WHITE );
		icon.applyStyleProperty( "background", Color.WHITE );
		icon.applyStyleProperty( "selectedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "selectedBackground", Color.WHITE );
		icon.applyStyleProperty( "checkmarkColor", Color.WHITE );
		icon.applyStyleProperty( "indeterminateBorderColor", Color.WHITE );
		icon.applyStyleProperty( "indeterminateBackground", Color.WHITE );
		icon.applyStyleProperty( "indeterminateCheckmarkColor", Color.WHITE );

		// disabled
		icon.applyStyleProperty( "disabledBorderColor", Color.WHITE );
		icon.applyStyleProperty( "disabledBackground", Color.WHITE );
		icon.applyStyleProperty( "disabledSelectedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "disabledSelectedBackground", Color.WHITE );
		icon.applyStyleProperty( "disabledCheckmarkColor", Color.WHITE );
		icon.applyStyleProperty( "disabledIndeterminateBorderColor", Color.WHITE );
		icon.applyStyleProperty( "disabledIndeterminateBackground", Color.WHITE );
		icon.applyStyleProperty( "disabledIndeterminateCheckmarkColor", Color.WHITE );

		// focused
		icon.applyStyleProperty( "focusedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "focusedBackground", Color.WHITE );
		icon.applyStyleProperty( "focusedSelectedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "focusedSelectedBackground", Color.WHITE );
		icon.applyStyleProperty( "focusedCheckmarkColor", Color.WHITE );
		icon.applyStyleProperty( "focusedIndeterminateBorderColor", Color.WHITE );
		icon.applyStyleProperty( "focusedIndeterminateBackground", Color.WHITE );
		icon.applyStyleProperty( "focusedIndeterminateCheckmarkColor", Color.WHITE );

		// hover
		icon.applyStyleProperty( "hoverBorderColor", Color.WHITE );
		icon.applyStyleProperty( "hoverBackground", Color.WHITE );
		icon.applyStyleProperty( "hoverSelectedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "hoverSelectedBackground", Color.WHITE );
		icon.applyStyleProperty( "hoverCheckmarkColor", Color.WHITE );
		icon.applyStyleProperty( "hoverIndeterminateBorderColor", Color.WHITE );
		icon.applyStyleProperty( "hoverIndeterminateBackground", Color.WHITE );
		icon.applyStyleProperty( "hoverIndeterminateCheckmarkColor", Color.WHITE );

		// pressed
		icon.applyStyleProperty( "pressedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "pressedBackground", Color.WHITE );
		icon.applyStyleProperty( "pressedSelectedBorderColor", Color.WHITE );
		icon.applyStyleProperty( "pressedSelectedBackground", Color.WHITE );
		icon.applyStyleProperty( "pressedCheckmarkColor", Color.WHITE );
		icon.applyStyleProperty( "pressedIndeterminateBorderColor", Color.WHITE );
		icon.applyStyleProperty( "pressedIndeterminateBackground", Color.WHITE );
		icon.applyStyleProperty( "pressedIndeterminateCheckmarkColor", Color.WHITE );
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

	@Test
	void flatClearIcon() {
		FlatClearIcon icon = new FlatClearIcon();

		icon.applyStyleProperty( "clearIconColor", Color.WHITE );
		icon.applyStyleProperty( "clearIconHoverColor", Color.WHITE );
		icon.applyStyleProperty( "clearIconPressedColor", Color.WHITE );
	}

	@Test
	void flatSearchIcon() {
		FlatSearchIcon icon = new FlatSearchIcon();

		flatSearchIcon( icon );
	}

	@Test
	void flatSearchWithHistoryIcon() {
		FlatSearchWithHistoryIcon icon = new FlatSearchWithHistoryIcon();

		flatSearchIcon( icon );
	}

	private void flatSearchIcon( FlatSearchIcon icon ) {
		icon.applyStyleProperty( "searchIconColor", Color.WHITE );
		icon.applyStyleProperty( "searchIconHoverColor", Color.WHITE );
		icon.applyStyleProperty( "searchIconPressedColor", Color.WHITE );
	}

	//---- enums --------------------------------------------------------------

	enum SomeEnum { enumValue1, enumValue2 }

	static class ClassWithEnum {
		SomeEnum enum1;
	}

	@Test
	void enumField() {
		ClassWithEnum c = new ClassWithEnum();
		FlatStylingSupport.applyToField( c, "enum1", "enum1", "enumValue1" );
		FlatStylingSupport.applyToField( c, "enum1", "enum1", "enumValue2" );
	}

	@Test
	void enumProperty() {
		JList<Object> c = new JList<>();
		FlatListUI ui = (FlatListUI) c.getUI();
		ui.applyStyle( "dropMode: INSERT" );
	}

	@Test
	void enumUIDefaults() {
		UIManager.put( "test.enum", SomeEnum.enumValue1.toString() );
		assertEquals( SomeEnum.enumValue1, FlatUIUtils.getUIEnum( "test.enum", SomeEnum.class, null ) );

		UIManager.put( "test.enum", "unknown" );
		assertEquals( null, FlatUIUtils.getUIEnum( "test.enum", SomeEnum.class, null ) );

		UIManager.put( "test.enum", null );
		assertEquals( SomeEnum.enumValue1, FlatUIUtils.getUIEnum( "test.enum", SomeEnum.class, SomeEnum.enumValue1 ) );
	}
}
