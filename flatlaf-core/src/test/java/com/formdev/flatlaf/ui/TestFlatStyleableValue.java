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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.lang.reflect.Method;
import java.util.Locale;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.icons.FlatCheckBoxIcon;
import com.formdev.flatlaf.icons.FlatCheckBoxMenuItemIcon;
import com.formdev.flatlaf.icons.FlatClearIcon;
import com.formdev.flatlaf.icons.FlatHelpButtonIcon;
import com.formdev.flatlaf.icons.FlatMenuArrowIcon;
import com.formdev.flatlaf.icons.FlatRadioButtonIcon;
import com.formdev.flatlaf.icons.FlatRadioButtonMenuItemIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;

/**
 * @author Karl Tauber
 */
public class TestFlatStyleableValue
{
	@BeforeAll
	static void setup() {
		TestUtils.setup( false );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
	}

	private void testString( JComponent c, StyleableUI ui, String key, String value ) {
		applyStyle( c, ui, String.format( "%s: %s", key, value ) );
		assertEquals( value, ui.getStyleableValue( c, key ) );
	}

	private void testBoolean( JComponent c, StyleableUI ui, String key, boolean value ) {
		applyStyle( c, ui, String.format( "%s: %s", key, value ) );
		assertEquals( value, ui.getStyleableValue( c, key ) );
	}

	private void testInteger( JComponent c, StyleableUI ui, String key, int value ) {
		applyStyle( c, ui, String.format( "%s: %d", key, value ) );
		assertEquals( value, ui.getStyleableValue( c, key ) );
	}

	private void testFloat( JComponent c, StyleableUI ui, String key, float value ) {
		applyStyle( c, ui, String.format( Locale.ENGLISH, "%s: %f", key, value ) );
		assertEquals( value, ui.getStyleableValue( c, key ) );
	}

	private void testColor( JComponent c, StyleableUI ui, String key, int rgb ) {
		applyStyle( c, ui, String.format( "%s: #%06x", key, rgb ) );
		assertEquals( new Color( rgb ), ui.getStyleableValue( c, key ) );
	}

	private void testInsets( JComponent c, StyleableUI ui, String key, int top, int left, int bottom, int right ) {
		applyStyle( c,ui, String.format( "%s: %d,%d,%d,%d", key, top, left, bottom, right ) );
		assertEquals( new Insets( top, left, bottom, right ), ui.getStyleableValue( c, key ) );
	}

	private void testDimension( JComponent c, StyleableUI ui, String key, int width, int height ) {
		applyStyle( c,ui, String.format( "%s: %d,%d", key, width, height ) );
		assertEquals( new Dimension( width, height ), ui.getStyleableValue( c, key ) );
	}

	private void testFont( JComponent c, StyleableUI ui, String key, String style, Font expectedFont ) {
		applyStyle( c,ui, String.format( "%s: %s", key, style ) );
		assertEquals( expectedFont, ui.getStyleableValue( c, key ) );
	}

	private void testIcon( JComponent c, StyleableUI ui, String key, String classname, Icon expectedIcon ) {
		applyStyle( c,ui, String.format( "%s: %s", key, classname ) );
		assertEquals( expectedIcon, ui.getStyleableValue( c, key ) );
	}

	private void applyStyle( JComponent c, StyleableUI ui, String style ) {
		Method m = findMethod( ui, "applyStyle", Object.class );
		if( m == null )
			m = findMethod( ui, "applyStyle", c.getClass(), Object.class );
		if( m == null )
			m = findMethod( ui, "applyStyle", c.getClass().getSuperclass(), Object.class );
		if( m == null )
			m = findMethod( ui, "applyStyle", c.getClass().getSuperclass().getSuperclass(), Object.class );
		if( m == null ) {
			Assertions.fail( "missing method '" + ui.getClass()
				+ ".applyStyle( Object )' or 'applyStyle( " + c.getClass().getSimpleName()
				+ ", Object )'" );
			return;
		}

		try {
			m.setAccessible( true );
			if( m.getParameterCount() == 1 )
				m.invoke( ui, style );
			else
				m.invoke( ui, c, style );
		} catch( Exception ex ) {
			Assertions.fail( "failed to invoke 'applyStyle()'", ex );
		}
	}

	private Method findMethod( StyleableUI ui, String name, Class<?>... parameterTypes ) {
		for( Class<?> cls = ui.getClass(); cls != null; cls = cls.getSuperclass() ) {
			try {
				return cls.getDeclaredMethod( name, parameterTypes );
			} catch( Exception ex ) {
				// ignore
			}
		}
		return null;
	}

	private void testValue( Object obj, String key, Object value ) {
		try {
			Method m = obj.getClass().getMethod( "applyStyleProperty", String.class, Object.class );
			m.invoke( obj, key, value );

			m = obj.getClass().getMethod( "getStyleableValue", String.class );
			Object actualValue = m.invoke( obj, key );

			assertEquals( value, actualValue );
		} catch( Exception ex ) {
			Assertions.fail( ex );
		}
	}

	//---- components ---------------------------------------------------------

	@Test
	void button() {
		JButton c = new JButton();
		FlatButtonUI ui = (FlatButtonUI) c.getUI();

		button( c, ui );

		//---- FlatHelpButtonIcon ----

		testInteger( c, ui, "help.focusWidth", 123 );
		testColor( c, ui, "help.focusColor", 0x123456 );
		testFloat( c, ui, "help.innerFocusWidth", 1.23f );
		testInteger( c, ui, "help.borderWidth", 123 );

		testColor( c, ui, "help.borderColor", 0x123456 );
		testColor( c, ui, "help.disabledBorderColor", 0x354678 );
		testColor( c, ui, "help.focusedBorderColor", 0x123456 );
		testColor( c, ui, "help.hoverBorderColor", 0x123456 );
		testColor( c, ui, "help.background", 0x132456 );
		testColor( c, ui, "help.disabledBackground", 0x123456 );
		testColor( c, ui, "help.focusedBackground", 0x123456 );
		testColor( c, ui, "help.hoverBackground", 0x123456 );
		testColor( c, ui, "help.pressedBackground", 0x123456 );
		testColor( c, ui, "help.questionMarkColor", 0x123456 );
		testColor( c, ui, "help.disabledQuestionMarkColor", 0x123456 );
	}

	private void button( AbstractButton c, FlatButtonUI ui ) {
		testInteger( c, ui, "minimumWidth", 123 );

		testColor( c, ui, "focusedBackground", 0x123456 );
		testColor( c, ui, "focusedForeground", 0x123456 );
		testColor( c, ui, "hoverBackground", 0x123456 );
		testColor( c, ui, "hoverForeground", 0x123456 );
		testColor( c, ui, "pressedBackground", 0x123456 );
		testColor( c, ui, "pressedForeground", 0x123456 );
		testColor( c, ui, "selectedBackground", 0x123456 );
		testColor( c, ui, "selectedForeground", 0x123456 );
		testColor( c, ui, "disabledBackground", 0x123456 );
		testColor( c, ui, "disabledText", 0x123456 );
		testColor( c, ui, "disabledSelectedBackground", 0x123456 );
		testColor( c, ui, "disabledSelectedForeground", 0x123456 );

		testColor( c, ui, "default.background", 0x123456 );
		testColor( c, ui, "default.foreground", 0x123456 );
		testColor( c, ui, "default.focusedBackground", 0x123456 );
		testColor( c, ui, "default.focusedForeground", 0x123456 );
		testColor( c, ui, "default.hoverBackground", 0x123456 );
		testColor( c, ui, "default.hoverForeground", 0x123456 );
		testColor( c, ui, "default.pressedBackground", 0x123456 );
		testColor( c, ui, "default.pressedForeground", 0x123456 );
		testBoolean( c, ui, "default.boldText", true );

		testBoolean( c, ui, "paintShadow", true );
		testInteger( c, ui, "shadowWidth", 123 );
		testColor( c, ui, "shadowColor", 0x123456 );
		testColor( c, ui, "default.shadowColor", 0x123456 );

		testInsets( c, ui, "toolbar.spacingInsets", 1,2,3,4 );
		testColor( c, ui, "toolbar.hoverBackground", 0x123456 );
		testColor( c, ui, "toolbar.hoverForeground", 0x123456 );
		testColor( c, ui, "toolbar.pressedBackground", 0x123456 );
		testColor( c, ui, "toolbar.pressedForeground", 0x123456 );
		testColor( c, ui, "toolbar.selectedBackground", 0x123456 );
		testColor( c, ui, "toolbar.selectedForeground", 0x123456 );
		testColor( c, ui, "toolbar.disabledSelectedBackground", 0x123456 );
		testColor( c, ui, "toolbar.disabledSelectedForeground", 0x123456 );

		testString( c, ui, "buttonType", "help" );
		testBoolean( c, ui, "squareSize", true );
		testInteger( c, ui, "minimumHeight", 123 );

		// border
		flatButtonBorder( c, ui );
	}

	@Test
	void checkBox() {
		JCheckBox c = new JCheckBox();
		FlatCheckBoxUI ui = (FlatCheckBoxUI) c.getUI();

		// FlatCheckBoxUI extends FlatRadioButtonUI
		radioButton( ui, c );
	}

	@Test
	void comboBox() {
		JComboBox<Object> c = new JComboBox<>();
		FlatComboBoxUI ui = (FlatComboBoxUI) c.getUI();

		testInsets( c, ui, "padding", 1,2,3,4 );

		testInteger( c, ui, "minimumWidth", 123 );
		testInteger( c, ui, "editorColumns", 123 );
		testString( c, ui, "buttonStyle", "auto" );
		testString( c, ui, "arrowType", "chevron" );

		testColor( c, ui, "editableBackground", 123456 );
		testColor( c, ui, "focusedBackground", 0x123456 );
		testColor( c, ui, "disabledBackground", 0x123456 );
		testColor( c, ui, "disabledForeground", 0x123456 );

		testColor( c, ui, "buttonBackground", 0x123456 );
		testColor( c, ui, "buttonFocusedBackground", 0x123456 );
		testColor( c, ui, "buttonEditableBackground", 0x123456 );
		testFloat( c, ui, "buttonSeparatorWidth", 1.23f );
		testColor( c, ui, "buttonSeparatorColor", 0x123456 );
		testColor( c, ui, "buttonDisabledSeparatorColor", 0x123456 );
		testColor( c, ui, "buttonArrowColor", 0x123456 );
		testColor( c, ui, "buttonDisabledArrowColor", 0x123456 );
		testColor( c, ui, "buttonHoverArrowColor", 0x123456 );
		testColor( c, ui, "buttonPressedArrowColor", 0x123456 );

		testColor( c, ui, "popupBackground", 0x123456 );
		testInsets( c, ui, "popupInsets", 1,2,3,4 );
		testInsets( c, ui, "selectionInsets", 1,2,3,4 );
		testInteger( c, ui, "selectionArc", 123 );

		// border
		flatRoundBorder( c, ui );
	}

	@Test
	void editorPane() {
		JEditorPane c = new JEditorPane();
		FlatEditorPaneUI ui = (FlatEditorPaneUI) c.getUI();

		testInteger( c, ui, "minimumWidth", 123 );
		testColor( c, ui, "disabledBackground", 0x123456 );
		testColor( c, ui, "inactiveBackground", 0x123456 );
		testColor( c, ui, "focusedBackground", 0x123456 );
	}

	@Test
	void formattedTextField() {
		JFormattedTextField c = new JFormattedTextField();
		FlatFormattedTextFieldUI ui = (FlatFormattedTextFieldUI) c.getUI();

		// FlatFormattedTextFieldUI extends FlatTextFieldUI
		textField( c, ui );
	}

	@Test
	void internalFrame() {
		JInternalFrame c = new JInternalFrame();
		FlatInternalFrameUI ui = (FlatInternalFrameUI) c.getUI();

		testColor( c, ui, "activeBorderColor", 0x123456 );
		testColor( c, ui, "inactiveBorderColor", 0x123456 );
		testInteger( c, ui, "borderLineWidth", 123 );
		testBoolean( c, ui, "dropShadowPainted", false );
		testInsets( c, ui, "borderMargins", 1,2,3,4 );

		testColor( c, ui, "activeDropShadowColor", 0x123456 );
		testInsets( c, ui, "activeDropShadowInsets", 1,2,3,4 );
		testFloat( c, ui, "activeDropShadowOpacity", 1.23f );
		testColor( c, ui, "inactiveDropShadowColor", 0x123456 );
		testInsets( c, ui, "inactiveDropShadowInsets", 1,2,3,4 );
		testFloat( c, ui, "inactiveDropShadowOpacity", 1.23f );
	}

	@Test
	void label() {
		JLabel c = new JLabel();
		FlatLabelUI ui = (FlatLabelUI) c.getUI();

		testColor( c, ui, "disabledForeground", 0x123456 );
		testInteger( c, ui, "arc", 123 );
	}

	@Test
	void list() {
		JList<Object> c = new JList<>();
		FlatListUI ui = (FlatListUI) c.getUI();

		testColor( c, ui, "selectionBackground", 0x123456 );
		testColor( c, ui, "selectionForeground", 0x123456 );
		testColor( c, ui, "selectionInactiveBackground", 0x123456 );
		testColor( c, ui, "selectionInactiveForeground", 0x123456 );
		testColor( c, ui, "alternateRowColor", 0x123456 );
		testInsets( c, ui, "selectionInsets", 1,2,3,4 );
		testInteger( c, ui, "selectionArc", 123 );

		// FlatListCellBorder
		testInsets( c, ui, "cellMargins", 1,2,3,4 );
		testColor( c, ui, "cellFocusColor", 0x123456 );
		testBoolean( c, ui, "showCellFocusIndicator", true );
	}

	@Test
	void menuBar() {
		JMenuBar c = new JMenuBar();
		FlatMenuBarUI ui = (FlatMenuBarUI) c.getUI();

		testInsets( c, ui, "itemMargins", 1,2,3,4 );
		testColor( c, ui, "hoverBackground", 0x123456 );
		testColor( c, ui, "selectionBackground", 0x123456 );
		testColor( c, ui, "selectionForeground", 0x123456 );
		testColor( c, ui, "underlineSelectionBackground", 0x123456 );
		testColor( c, ui, "underlineSelectionColor", 0x123456 );
		testInteger( c, ui, "underlineSelectionHeight", 123 );

		// FlatMenuBarBorder
		testColor( c, ui, "borderColor", 0x123456 );
	}

	@Test
	void menu() {
		JMenu c = new JMenu();
		FlatMenuUI ui = (FlatMenuUI) c.getUI();

		menuItem( c, ui );
		menuItem_arrowIcon( c, ui );
	}

	@Test
	void menuItem() {
		JMenuItem c = new JMenuItem();
		FlatMenuItemUI ui = (FlatMenuItemUI) c.getUI();

		menuItem( c, ui );
	}

	@Test
	void checkBoxMenuItem() {
		JCheckBoxMenuItem c = new JCheckBoxMenuItem();
		FlatCheckBoxMenuItemUI ui = (FlatCheckBoxMenuItemUI) c.getUI();

		menuItem( c, ui );
		menuItem_checkIcon( c, ui );
	}

	@Test
	void radioButtonMenuItem() {
		JRadioButtonMenuItem c = new JRadioButtonMenuItem();
		FlatRadioButtonMenuItemUI ui = (FlatRadioButtonMenuItemUI) c.getUI();

		menuItem( c, ui );
		menuItem_checkIcon( c, ui );
	}

	private void menuItem( JComponent c, StyleableUI ui ) {
		testColor( c, ui, "selectionBackground", 0x123456 );
		testColor( c, ui, "selectionForeground", 0x123456 );
		testColor( c, ui, "disabledForeground", 0x123456 );
		testColor( c, ui, "acceleratorForeground", 0x123456 );
		testColor( c, ui, "acceleratorSelectionForeground", 0x123456 );
		testFont( c, ui, "acceleratorFont", "italic 12 monospaced", new Font( "monospaced", Font.ITALIC, 12 ) );

		menuItemRenderer( c, ui );
	}

	private void menuItemRenderer( JComponent c, StyleableUI ui ) {
		testBoolean( c, ui, "verticallyAlignText", false );
		testInteger( c, ui, "minimumWidth", 123 );
		testDimension( c, ui, "minimumIconSize", 1,2 );
		testInteger( c, ui, "textAcceleratorGap", 123 );
		testInteger( c, ui, "textNoAcceleratorGap", 123 );
		testInteger( c, ui, "acceleratorArrowGap", 123 );

		testColor( c, ui, "checkBackground", 0x123456 );
		testInsets( c, ui, "checkMargins", 1,2,3,4 );

		testColor( c, ui, "underlineSelectionBackground", 0x123456 );
		testColor( c, ui, "underlineSelectionCheckBackground", 0x123456 );
		testColor( c, ui, "underlineSelectionColor", 0x123456 );
		testInteger( c, ui, "underlineSelectionHeight", 123 );
	}

	private void menuItem_checkIcon( JComponent c, StyleableUI ui ) {
		testColor( c, ui, "icon.checkmarkColor", 0x123456 );
		testColor( c, ui, "icon.disabledCheckmarkColor", 0x123456 );
		testColor( c, ui, "selectionForeground", 0x123456 );
	}

	private void menuItem_arrowIcon( JComponent c, StyleableUI ui ) {
		testString( c, ui, "icon.arrowType", "chevron" );
		testColor( c, ui, "icon.arrowColor", 0x123456 );
		testColor( c, ui, "icon.disabledArrowColor", 0x123456 );
		testColor( c, ui, "selectionForeground", 0x123456 );
	}

	@Test
	void panel() {
		JPanel c = new JPanel();
		FlatPanelUI ui = (FlatPanelUI) c.getUI();

		testInteger( c, ui, "arc", 123 );
	}

	@Test
	void passwordField() {
		JPasswordField c = new JPasswordField();
		FlatPasswordFieldUI ui = (FlatPasswordFieldUI) c.getUI();

		// FlatPasswordFieldUI extends FlatTextFieldUI
		textField( c, ui );

		testBoolean( c, ui, "showCapsLock", true );
		testBoolean( c, ui, "showRevealButton", true );

		// capsLockIcon
		testColor( c, ui, "capsLockIconColor", 0x123456 );

		// border
		flatTextBorder( c, ui );
	}

	@Test
	void popupMenu() {
		JPopupMenu c = new JPopupMenu();
		FlatPopupMenuUI ui = (FlatPopupMenuUI) c.getUI();

		testString( c, ui, "arrowType", "chevron" );
		testColor( c, ui, "scrollArrowColor", 0x123456 );
		testColor( c, ui, "hoverScrollArrowBackground", 0x123456 );

		testInsets( c, ui, "borderInsets", 1,2,3,4 );
		testColor( c, ui, "borderColor", 0x123456 );
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

		testInteger( c, ui, "arc", 123 );
		testDimension( c, ui, "horizontalSize", 1,2 );
		testDimension( c, ui, "verticalSize", 1,2 );

		testBoolean( c, ui, "largeHeight", true );
		testBoolean( c, ui, "square", true );
	}

	@Test
	void radioButton() {
		JRadioButton c = new JRadioButton();
		FlatRadioButtonUI ui = (FlatRadioButtonUI) c.getUI();

		assertTrue( ui.getDefaultIcon() instanceof FlatRadioButtonIcon );

		radioButton( ui, c );

		testFloat( c, ui, "icon.centerDiameter", 1.23f );
	}

	private void radioButton( FlatRadioButtonUI ui, AbstractButton b ) {
		testColor( b, ui, "disabledText", 0x123456 );

		//---- icon ----

		testFloat( b, ui, "icon.focusWidth", 1.23f );
		testColor( b, ui, "icon.focusColor", 0x123456 );
		testFloat( b, ui, "icon.borderWidth", 1.23f );
		testFloat( b, ui, "icon.selectedBorderWidth", 1.23f );
		testFloat( b, ui, "icon.disabledSelectedBorderWidth", 1.23f );
		testInteger( b, ui, "icon.arc", 123 );

		// enabled
		testColor( b, ui, "icon.borderColor", 0x123456 );
		testColor( b, ui, "icon.background", 0x901324 );
		testColor( b, ui, "icon.selectedBorderColor", 0x123456 );
		testColor( b, ui, "icon.selectedBackground", 0x123456 );
		testColor( b, ui, "icon.checkmarkColor", 0x123456 );

		// disabled
		testColor( b, ui, "icon.disabledBorderColor", 0x123456 );
		testColor( b, ui, "icon.disabledBackground", 0x123456 );
		testColor( b, ui, "icon.disabledSelectedBorderColor", 0x123456 );
		testColor( b, ui, "icon.disabledSelectedBackground", 0x123456 );
		testColor( b, ui, "icon.disabledCheckmarkColor", 0x123456 );

		// focused
		testColor( b, ui, "icon.focusedBorderColor", 0x123456 );
		testColor( b, ui, "icon.focusedBackground", 0x123456 );
		testColor( b, ui, "icon.focusedSelectedBorderColor", 0x123456 );
		testColor( b, ui, "icon.focusedSelectedBackground", 0x123456 );
		testColor( b, ui, "icon.focusedCheckmarkColor", 0x123456 );

		// hover
		testColor( b, ui, "icon.hoverBorderColor", 0x123456 );
		testColor( b, ui, "icon.hoverBackground", 0x123456 );
		testColor( b, ui, "icon.hoverSelectedBorderColor", 0x123456 );
		testColor( b, ui, "icon.hoverSelectedBackground", 0x123456 );
		testColor( b, ui, "icon.hoverCheckmarkColor", 0x123456 );

		// pressed
		testColor( b, ui, "icon.pressedBorderColor", 0x123456 );
		testColor( b, ui, "icon.pressedBackground", 0x123456 );
		testColor( b, ui, "icon.pressedSelectedBorderColor", 0x123456 );
		testColor( b, ui, "icon.pressedSelectedBackground", 0x123456 );
		testColor( b, ui, "icon.pressedCheckmarkColor", 0x123456 );
	}

	@Test
	void scrollBar() {
		JScrollBar c = new JScrollBar();
		FlatScrollBarUI ui = (FlatScrollBarUI) c.getUI();

		testColor( c, ui, "track", 0x123456 );
		testColor( c, ui, "thumb", 0x123456 );
		testInteger( c, ui, "width", 123 );
		testDimension( c, ui, "minimumThumbSize", 1,2 );
		testDimension( c, ui, "maximumThumbSize", 1,2 );
		testBoolean( c, ui, "allowsAbsolutePositioning", true );

		testDimension( c, ui, "minimumButtonSize", 1,2 );
		testInsets( c, ui, "trackInsets", 1,2,3,4 );
		testInsets( c, ui, "thumbInsets", 1,2,3,4 );
		testInteger( c, ui, "trackArc", 123 );
		testInteger( c, ui, "thumbArc", 123 );
		testColor( c, ui, "hoverTrackColor", 0x123456 );
		testColor( c, ui, "hoverThumbColor", 0x123456 );
		testBoolean( c, ui, "hoverThumbWithTrack", true );
		testColor( c, ui, "pressedTrackColor", 0x123456 );
		testColor( c, ui, "pressedThumbColor", 0x123456 );
		testBoolean( c, ui, "pressedThumbWithTrack", true );

		testBoolean( c, ui, "showButtons", true );
		testString( c, ui, "arrowType", "chevron" );
		testColor( c, ui, "buttonArrowColor", 0x123456 );
		testColor( c, ui, "buttonDisabledArrowColor", 0x123456 );
		testColor( c, ui, "hoverButtonBackground", 0x123456 );
		testColor( c, ui, "pressedButtonBackground", 0x123456 );
	}

	@Test
	void scrollPane() {
		JScrollPane c = new JScrollPane();
		FlatScrollPaneUI ui = (FlatScrollPaneUI) c.getUI();

		// border
		flatScrollPaneBorder( c, ui );

		testBoolean( c, ui, "showButtons", true );
	}

	@Test
	void separator() {
		JSeparator c = new JSeparator();
		FlatSeparatorUI ui = (FlatSeparatorUI) c.getUI();

		separator( ui, c );
	}

	private void separator( FlatSeparatorUI ui, JSeparator c ) {
		testInteger( c, ui, "height", 123 );
		testInteger( c, ui, "stripeWidth", 123 );
		testInteger( c, ui, "stripeIndent", 123 );
	}

	@Test
	void slider() {
		JSlider c = new JSlider();
		FlatSliderUI ui = (FlatSliderUI) c.getUI();

		testInteger( c, ui, "trackWidth", 123 );
		testDimension( c, ui, "thumbSize", 1,2 );
		testInteger( c, ui, "focusWidth", 123 );
		testFloat( c, ui, "thumbBorderWidth", 1.23f );

		testColor( c, ui, "trackValueColor", 0x123456 );
		testColor( c, ui, "trackColor", 0x123456 );
		testColor( c, ui, "thumbColor", 0x123456 );
		testColor( c, ui, "thumbBorderColor", 0x123456 );
		testColor( c, ui, "focusedColor", 0x123456 );
		testColor( c, ui, "focusedThumbBorderColor", 0x123456 );
		testColor( c, ui, "hoverThumbColor", 0x123456 );
		testColor( c, ui, "pressedThumbColor", 0x123456 );
		testColor( c, ui, "disabledTrackColor", 0x123456 );
		testColor( c, ui, "disabledThumbColor", 0x123456 );
		testColor( c, ui, "disabledThumbBorderColor", 0x123456 );
		testColor( c, ui, "tickColor", 0x123456 );
	}

	@Test
	void spinner() {
		JSpinner c = new JSpinner();
		FlatSpinnerUI ui = (FlatSpinnerUI) c.getUI();

		testInteger( c, ui, "minimumWidth", 123 );
		testString( c, ui, "buttonStyle", "button" );
		testString( c, ui, "arrowType", "chevron" );
		testColor( c, ui, "disabledBackground", 0x123456 );
		testColor( c, ui, "disabledForeground", 0x123456 );
		testColor( c, ui, "focusedBackground", 0x123456 );
		testColor( c, ui, "buttonBackground", 0x123456 );
		testFloat( c, ui, "buttonSeparatorWidth", 1.23f );
		testColor( c, ui, "buttonSeparatorColor", 0x123456 );
		testColor( c, ui, "buttonDisabledSeparatorColor", 0x123456 );
		testColor( c, ui, "buttonArrowColor", 0x123456 );
		testColor( c, ui, "buttonDisabledArrowColor", 0x123456 );
		testColor( c, ui, "buttonHoverArrowColor", 0x123456 );
		testColor( c, ui, "buttonPressedArrowColor", 0x123456 );
		testInsets( c, ui, "padding", 1,2,3,4 );

		// border
		flatRoundBorder( c, ui );
	}

	@Test
	void splitPane() {
		JSplitPane c = new JSplitPane();
		FlatSplitPaneUI ui = (FlatSplitPaneUI) c.getUI();

		testString( c, ui, "arrowType", "chevron" );
		testColor( c, ui, "draggingColor", 0x123456 );
		testColor( c, ui, "hoverColor", 0x123456 );
		testColor( c, ui, "pressedColor", 0x123456 );
		testColor( c, ui, "oneTouchArrowColor", 0x123456 );
		testColor( c, ui, "oneTouchHoverArrowColor", 0x123456 );
		testColor( c, ui, "oneTouchPressedArrowColor", 0x123456 );

		testString( c, ui, "style", "grip" );
		testColor( c, ui, "gripColor", 0x123456 );
		testInteger( c, ui, "gripDotCount", 123 );
		testInteger( c, ui, "gripDotSize", 123 );
		testInteger( c, ui, "gripGap", 123 );
	}

	@Test
	void tabbedPane() {
		JTabbedPane c = new JTabbedPane();
		FlatTabbedPaneUI ui = (FlatTabbedPaneUI) c.getUI();

		testInsets( c, ui, "tabInsets", 1,2,3,4 );
		testInsets( c, ui, "tabAreaInsets", 1,2,3,4 );
		testInteger( c, ui, "textIconGap", 123 );

		testColor( c, ui, "disabledForeground", 0x123456 );

		testColor( c, ui, "selectedBackground", 0x123456 );
		testColor( c, ui, "selectedForeground", 0x123456 );
		testColor( c, ui, "underlineColor", 0x123456 );
		testColor( c, ui, "inactiveUnderlineColor", 0x123456 );
		testColor( c, ui, "disabledUnderlineColor", 0x123456 );
		testColor( c, ui, "hoverColor", 0x123456 );
		testColor( c, ui, "hoverForeground", 0x123456 );
		testColor( c, ui, "focusColor", 0x123456 );
		testColor( c, ui, "focusForeground", 0x123456 );
		testColor( c, ui, "tabSeparatorColor", 0x123456 );
		testColor( c, ui, "contentAreaColor", 0x123456 );

		testInteger( c, ui, "minimumTabWidth", 123 );
		testInteger( c, ui, "maximumTabWidth", 123 );
		testInteger( c, ui, "tabHeight", 123 );
		testInteger( c, ui, "tabSelectionHeight", 123 );
		testInteger( c, ui, "cardTabSelectionHeight", 123 );
		testInteger( c, ui, "tabArc", 123 );
		testInteger( c, ui, "tabSelectionArc", 123 );
		testInteger( c, ui, "cardTabArc", 123 );
		testInsets( c, ui, "selectedInsets", 1,2,3,4 );
		testInsets( c, ui, "tabSelectionInsets", 1,2,3,4 );
		testInteger( c, ui, "contentSeparatorHeight", 123 );
		testBoolean( c, ui, "showTabSeparators", false );
		testBoolean( c, ui, "tabSeparatorsFullHeight", false );
		testBoolean( c, ui, "hasFullBorder", false );
		testBoolean( c, ui, "tabsOpaque", false );
		testBoolean( c, ui, "rotateTabRuns", false );

		testString( c, ui, "tabType", "card" );
		testString( c, ui, "tabsPopupPolicy", "asNeeded" );
		testString( c, ui, "scrollButtonsPolicy", "asNeeded" );
		testString( c, ui, "scrollButtonsPlacement", "both" );

		testString( c, ui, "tabAreaAlignment", "leading" );
		testString( c, ui, "tabAlignment", "center" );
		testString( c, ui, "tabWidthMode", "preferred" );
		testString( c, ui, "tabRotation", "none" );

		testString( c, ui, "arrowType", "chevron" );
		testInsets( c, ui, "buttonInsets", 1,2,3,4 );
		testInteger( c, ui, "buttonArc", 123 );
		testColor( c, ui, "buttonHoverBackground", 0x123456 );
		testColor( c, ui, "buttonPressedBackground", 0x123456 );

		testString( c, ui, "moreTabsButtonToolTipText", "Gimme more" );
		testString( c, ui, "tabCloseToolTipText", "Close me" );

		testBoolean( c, ui, "showContentSeparator", true );
		testBoolean( c, ui, "hideTabAreaWithOneTab", true );
		testBoolean( c, ui, "tabClosable", true );
		testString( c, ui, "tabIconPlacement", "top" );

		// FlatTabbedPaneCloseIcon
		testDimension( c, ui, "closeSize", 1,2 );
		testInteger( c, ui, "closeArc", 123 );
		testFloat( c, ui, "closeCrossPlainSize", 1.23f );
		testFloat( c, ui, "closeCrossFilledSize", 1.23f );
		testFloat( c, ui, "closeCrossLineWidth", 1.23f );
		testColor( c, ui, "closeBackground", 0x123456 );
		testColor( c, ui, "closeForeground", 0x123456 );
		testColor( c, ui, "closeHoverBackground", 0x123456 );
		testColor( c, ui, "closeHoverForeground", 0x123456 );
		testColor( c, ui, "closePressedBackground", 0x123456 );
		testColor( c, ui, "closePressedForeground", 0x123456 );
	}

	@Test
	void table() {
		JTable c = new JTable();
		FlatTableUI ui = (FlatTableUI) c.getUI();

		testBoolean( c, ui, "showTrailingVerticalLine", true );
		testColor( c, ui, "selectionBackground", 0x123456 );
		testColor( c, ui, "selectionForeground", 0x123456 );
		testColor( c, ui, "selectionInactiveBackground", 0x123456 );
		testColor( c, ui, "selectionInactiveForeground", 0x901324 );
		testInsets( c, ui, "selectionInsets", 1,2,3,4 );
		testInteger( c, ui, "selectionArc", 123 );

		// FlatTableCellBorder
		testInsets( c, ui, "cellMargins", 1,2,3,4 );
		testColor( c, ui, "cellFocusColor", 0x123456 );
		testBoolean( c, ui, "showCellFocusIndicator", true );
	}

	@Test
	void tableHeader() {
		JTableHeader c = new JTableHeader();
		FlatTableHeaderUI ui = (FlatTableHeaderUI) c.getUI();

		testColor( c, ui, "hoverBackground", 0x123456 );
		testColor( c, ui, "hoverForeground", 0x123456 );
		testColor( c, ui, "pressedBackground", 0x123456 );
		testColor( c, ui, "pressedForeground", 0x123456 );
		testColor( c, ui, "bottomSeparatorColor", 0x123456 );
		testInteger( c, ui, "height", 123 );
		testString( c, ui, "sortIconPosition", "top" );

		// FlatTableHeaderBorder
		testInsets( c, ui, "cellMargins", 1,2,3,4 );
		testColor( c, ui, "separatorColor", 0x123456 );
		testBoolean( c, ui, "showTrailingVerticalLine", true );

		// FlatAscendingSortIcon and FlatDescendingSortIcon
		testString( c, ui, "arrowType", "chevron" );
		testColor( c, ui, "sortIconColor", 0x123456 );
	}

	@Test
	void textArea() {
		JTextArea c = new JTextArea();
		FlatTextAreaUI ui = (FlatTextAreaUI) c.getUI();

		testInteger( c, ui, "minimumWidth", 123 );
		testColor( c, ui, "disabledBackground", 0x123456 );
		testColor( c, ui, "inactiveBackground", 0x123456 );
		testColor( c, ui, "focusedBackground", 0x123456 );
	}

	@Test
	void textField() {
		JTextField c = new JTextField();
		FlatTextFieldUI ui = (FlatTextFieldUI) c.getUI();

		textField( c, ui );
	}

	private void textField( JTextComponent c, FlatTextFieldUI ui ) {
		testInteger( c, ui, "minimumWidth", 123 );
		testColor( c, ui, "disabledBackground", 0x123456 );
		testColor( c, ui, "inactiveBackground", 0x123456 );
		testColor( c, ui, "placeholderForeground", 0x123456 );
		testColor( c, ui, "focusedBackground", 0x123456 );
		testInteger( c, ui, "iconTextGap", 123 );
		testIcon( c, ui, "leadingIcon", TestIcon.class.getName(), new TestIcon() );
		testIcon( c, ui, "trailingIcon", TestIcon.class.getName(), new TestIcon() );

		testBoolean( c, ui, "showClearButton", true );

		// border
		flatTextBorder( c, ui );
	}

	@Test
	void textPane() {
		JTextPane c = new JTextPane();
		FlatTextPaneUI ui = (FlatTextPaneUI) c.getUI();

		testInteger( c, ui, "minimumWidth", 123 );
		testColor( c, ui, "disabledBackground", 0x123456 );
		testColor( c, ui, "inactiveBackground", 0x123456 );
		testColor( c, ui, "focusedBackground", 0x123456 );
	}

	@Test
	void toggleButton() {
		JToggleButton b = new JToggleButton();
		FlatToggleButtonUI ui = (FlatToggleButtonUI) b.getUI();

		// FlatToggleButtonUI extends FlatButtonUI
		button( b, ui );

		testInteger( b, ui, "tab.underlineHeight", 123 );
		testColor( b, ui, "tab.underlineColor", 0x123456 );
		testColor( b, ui, "tab.disabledUnderlineColor", 0x123456 );
		testColor( b, ui, "tab.selectedBackground", 0x123456 );
		testColor( b, ui, "tab.selectedForeground", 0x123456 );
		testColor( b, ui, "tab.hoverBackground", 0x123456 );
		testColor( b, ui, "tab.hoverForeground", 0x123456 );
		testColor( b, ui, "tab.focusBackground", 0x123456 );
		testColor( b, ui, "tab.focusForeground", 0x123456 );
	}

	@Test
	void toolBar() {
		JToolBar c = new JToolBar();
		FlatToolBarUI ui = (FlatToolBarUI) c.getUI();

		testBoolean( c, ui, "focusableButtons", true );
		testBoolean( c, ui, "arrowKeysOnlyNavigation", true );

		testInsets( c, ui, "borderMargins", 1,2,3,4 );
		testColor( c, ui, "gripColor", 0x123456 );

		testInteger( c, ui, "separatorWidth", 123 );
		testColor( c, ui, "separatorColor", 0x123456 );
	}

	@Test
	void toolBarSeparator() {
		JToolBar.Separator c = new JToolBar.Separator();
		FlatToolBarSeparatorUI ui = (FlatToolBarSeparatorUI) c.getUI();

		testInteger( c, ui, "separatorWidth", 123 );
		testColor( c, ui, "separatorColor", 0x123456 );
	}

	@Test
	void tree() {
		JTree c = new JTree();
		FlatTreeUI ui = (FlatTreeUI) c.getUI();

		testColor( c, ui, "selectionBackground", 0x123456 );
		testColor( c, ui, "selectionForeground", 0x123456 );
		testColor( c, ui, "selectionInactiveBackground", 0x123456 );
		testColor( c, ui, "selectionInactiveForeground", 0x123456 );
		testColor( c, ui, "selectionBorderColor", 0x123456 );
		testColor( c, ui, "alternateRowColor", 0x123456 );
		testInsets( c, ui, "selectionInsets", 1,2,3,4 );
		testInteger( c, ui, "selectionArc", 123 );
		testBoolean( c, ui, "wideSelection", true );
		testBoolean( c, ui, "wideCellRenderer", true );
		testBoolean( c, ui, "showCellFocusIndicator", true );

		testBoolean( c, ui, "paintSelection", false );

		// icons
		testString( c, ui, "icon.arrowType", "chevron" );
		testColor( c, ui, "icon.expandedColor", 0x123456 );
		testColor( c, ui, "icon.collapsedColor", 0x123456 );
		testColor( c, ui, "icon.leafColor", 0x123456 );
		testColor( c, ui, "icon.closedColor", 0x123456 );
		testColor( c, ui, "icon.openColor", 0x123456 );
	}

	//---- component borders --------------------------------------------------

	private void flatButtonBorder( JComponent c, StyleableUI ui ) {
		flatBorder( c, ui );

		testInteger( c, ui, "arc", 123 );

		testColor( c, ui, "borderColor", 0x123456 );
		testColor( c, ui, "disabledBorderColor", 0x123456 );
		testColor( c, ui, "focusedBorderColor", 0x123456 );
		testColor( c, ui, "hoverBorderColor", 0x123456 );
		testColor( c, ui, "pressedBorderColor", 0x123456 );

		testColor( c, ui, "selectedBorderColor", 0x123456 );
		testColor( c, ui, "disabledSelectedBorderColor", 0x123456 );
		testColor( c, ui, "focusedSelectedBorderColor", 0x123456 );
		testColor( c, ui, "hoverSelectedBorderColor", 0x123456 );
		testColor( c, ui, "pressedSelectedBorderColor", 0x123456 );

		testFloat( c, ui, "default.borderWidth", 1.23f );
		testColor( c, ui, "default.borderColor", 0x123456 );
		testColor( c, ui, "default.focusedBorderColor", 0x123456 );
		testColor( c, ui, "default.focusColor", 0x123456 );
		testColor( c, ui, "default.hoverBorderColor", 0x123456 );
		testColor( c, ui, "default.pressedBorderColor", 0x123456 );

		testFloat( c, ui, "toolbar.focusWidth", 1.23f );
		testColor( c, ui, "toolbar.focusColor", 0x123456 );
		testInsets( c, ui, "toolbar.margin", 1,2,3,4 );
		testInsets( c, ui, "toolbar.spacingInsets", 1,2,3,4 );
	}

	private void flatRoundBorder( JComponent c, StyleableUI ui ) {
		flatBorder( c, ui );

		testInteger( c, ui, "arc", 123 );
		testBoolean( c, ui, "roundRect", true );
	}

	private void flatScrollPaneBorder( JComponent c, StyleableUI ui ) {
		flatBorder( c, ui );

		testInteger( c, ui, "arc", 123 );
	}

	private void flatTextBorder( JComponent c, StyleableUI ui ) {
		flatBorder( c, ui );

		testInteger( c, ui, "arc", 123 );
		testBoolean( c, ui, "roundRect", true );
	}

	private void flatBorder( JComponent c, StyleableUI ui ) {
		testInteger( c, ui, "focusWidth", 123 );
		testFloat( c, ui, "innerFocusWidth", 1.23f );
		testFloat( c, ui, "innerOutlineWidth", 1.23f );
		testFloat( c, ui, "borderWidth", 1.23f );

		testColor( c, ui, "focusColor", 0x123456 );
		testColor( c, ui, "borderColor", 0x123456 );
		testColor( c, ui, "disabledBorderColor", 0x123456 );
		testColor( c, ui, "focusedBorderColor", 0x123456 );

		testColor( c, ui, "error.borderColor", 0x123456 );
		testColor( c, ui, "error.focusedBorderColor", 0x123456 );
		testColor( c, ui, "warning.borderColor", 0x123456 );
		testColor( c, ui, "warning.focusedBorderColor", 0x123456 );
		testColor( c, ui, "success.borderColor", 0x123456 );
		testColor( c, ui, "success.focusedBorderColor", 0x123456 );
		testColor( c, ui, "custom.borderColor", 0x123456 );

		testString( c, ui, "outline", "error" );
		testColor( c, ui, "outlineColor", 0x123456 );
		testColor( c, ui, "outlineFocusedColor", 0x123456 );
	}

	//---- borders ------------------------------------------------------------

	@Test
	void flatButtonBorder() {
		FlatButtonBorder border = new FlatButtonBorder();

		// FlatButtonBorder extends FlatBorder
		flatBorder( border );

		testValue( border, "arc", 6 );

		testValue( border, "borderColor", Color.WHITE );
		testValue( border, "disabledBorderColor", Color.WHITE );
		testValue( border, "focusedBorderColor", Color.WHITE );
		testValue( border, "hoverBorderColor", Color.WHITE );
		testValue( border, "pressedBorderColor", Color.WHITE );

		testValue( border, "selectedBorderColor", Color.WHITE );
		testValue( border, "disabledSelectedBorderColor", Color.WHITE );
		testValue( border, "focusedSelectedBorderColor", Color.WHITE );
		testValue( border, "hoverSelectedBorderColor", Color.WHITE );
		testValue( border, "pressedSelectedBorderColor", Color.WHITE );

		testValue( border, "default.borderWidth", 2f );
		testValue( border, "default.borderColor", Color.WHITE );
		testValue( border, "default.focusedBorderColor", Color.WHITE );
		testValue( border, "default.focusColor", Color.WHITE );
		testValue( border, "default.hoverBorderColor", Color.WHITE );
		testValue( border, "default.pressedBorderColor", Color.WHITE );

		testValue( border, "toolbar.focusWidth", 1.5f );
		testValue( border, "toolbar.focusColor", Color.WHITE );
		testValue( border, "toolbar.margin", new Insets( 1, 2, 3, 4 ) );
		testValue( border, "toolbar.spacingInsets", new Insets( 1, 2, 3, 4 ) );
	}

	@Test
	void flatRoundBorder() {
		FlatRoundBorder border = new FlatRoundBorder();

		// FlatRoundBorder extends FlatBorder
		flatBorder( border );

		testValue( border, "arc", 6 );
		testValue( border, "roundRect", true );
	}

	@Test
	void flatScrollPaneBorder() {
		FlatScrollPaneBorder border = new FlatScrollPaneBorder();

		// FlatScrollPaneBorder extends FlatBorder
		flatBorder( border );

		testValue( border, "arc", 6 );
	}

	@Test
	void flatTextBorder() {
		FlatTextBorder border = new FlatTextBorder();

		// FlatTextBorder extends FlatBorder
		flatBorder( border );

		testValue( border, "arc", 6 );
		testValue( border, "roundRect", true );
	}

	@Test
	void flatBorder() {
		FlatBorder border = new FlatBorder();

		flatBorder( border );
	}

	private void flatBorder( FlatBorder border ) {
		testValue( border, "focusWidth", 2 );
		testValue( border, "innerFocusWidth", 0.5f );
		testValue( border, "innerOutlineWidth", 1.5f );
		testValue( border, "borderWidth", 1f );

		testValue( border, "focusColor", Color.WHITE );
		testValue( border, "borderColor", Color.WHITE );
		testValue( border, "disabledBorderColor", Color.WHITE );
		testValue( border, "focusedBorderColor", Color.WHITE );

		testValue( border, "error.borderColor", Color.WHITE );
		testValue( border, "error.focusedBorderColor", Color.WHITE );
		testValue( border, "warning.borderColor", Color.WHITE );
		testValue( border, "warning.focusedBorderColor", Color.WHITE );
		testValue( border, "success.borderColor", Color.WHITE );
		testValue( border, "success.focusedBorderColor", Color.WHITE );
		testValue( border, "custom.borderColor", Color.WHITE );
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

		testValue( icon, "centerDiameter", 8f );
	}

	private void flatCheckBoxIcon( FlatCheckBoxIcon icon ) {
		testValue( icon, "focusWidth", 1.5f );
		testValue( icon, "focusColor", Color.WHITE );
		testValue( icon, "borderWidth", 1.5f );
		testValue( icon, "selectedBorderWidth", 1.5f );
		testValue( icon, "disabledSelectedBorderWidth", 1.5f );
		testValue( icon, "indeterminateBorderWidth", 1.5f );
		testValue( icon, "disabledIndeterminateBorderWidth", 1.5f );
		testValue( icon, "arc", 5 );

		// enabled
		testValue( icon, "borderColor", Color.WHITE );
		testValue( icon, "background", Color.WHITE );
		testValue( icon, "selectedBorderColor", Color.WHITE );
		testValue( icon, "selectedBackground", Color.WHITE );
		testValue( icon, "checkmarkColor", Color.WHITE );
		testValue( icon, "indeterminateBorderColor", Color.WHITE );
		testValue( icon, "indeterminateBackground", Color.WHITE );
		testValue( icon, "indeterminateCheckmarkColor", Color.WHITE );

		// disabled
		testValue( icon, "disabledBorderColor", Color.WHITE );
		testValue( icon, "disabledBackground", Color.WHITE );
		testValue( icon, "disabledSelectedBorderColor", Color.WHITE );
		testValue( icon, "disabledSelectedBackground", Color.WHITE );
		testValue( icon, "disabledCheckmarkColor", Color.WHITE );
		testValue( icon, "disabledIndeterminateBorderColor", Color.WHITE );
		testValue( icon, "disabledIndeterminateBackground", Color.WHITE );
		testValue( icon, "disabledIndeterminateCheckmarkColor", Color.WHITE );

		// focused
		testValue( icon, "focusedBorderColor", Color.WHITE );
		testValue( icon, "focusedBackground", Color.WHITE );
		testValue( icon, "focusedSelectedBorderColor", Color.WHITE );
		testValue( icon, "focusedSelectedBackground", Color.WHITE );
		testValue( icon, "focusedCheckmarkColor", Color.WHITE );
		testValue( icon, "focusedIndeterminateBorderColor", Color.WHITE );
		testValue( icon, "focusedIndeterminateBackground", Color.WHITE );
		testValue( icon, "focusedIndeterminateCheckmarkColor", Color.WHITE );

		// hover
		testValue( icon, "hoverBorderColor", Color.WHITE );
		testValue( icon, "hoverBackground", Color.WHITE );
		testValue( icon, "hoverSelectedBorderColor", Color.WHITE );
		testValue( icon, "hoverSelectedBackground", Color.WHITE );
		testValue( icon, "hoverCheckmarkColor", Color.WHITE );
		testValue( icon, "hoverIndeterminateBorderColor", Color.WHITE );
		testValue( icon, "hoverIndeterminateBackground", Color.WHITE );
		testValue( icon, "hoverIndeterminateCheckmarkColor", Color.WHITE );

		// pressed
		testValue( icon, "pressedBorderColor", Color.WHITE );
		testValue( icon, "pressedBackground", Color.WHITE );
		testValue( icon, "pressedSelectedBorderColor", Color.WHITE );
		testValue( icon, "pressedSelectedBackground", Color.WHITE );
		testValue( icon, "pressedCheckmarkColor", Color.WHITE );
		testValue( icon, "pressedIndeterminateBorderColor", Color.WHITE );
		testValue( icon, "pressedIndeterminateBackground", Color.WHITE );
		testValue( icon, "pressedIndeterminateCheckmarkColor", Color.WHITE );
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
		testValue( icon, "checkmarkColor", Color.WHITE );
		testValue( icon, "disabledCheckmarkColor", Color.WHITE );
		testValue( icon, "selectionForeground", Color.WHITE );
	}

	@Test
	void flatMenuArrowIcon() {
		FlatMenuArrowIcon icon = new FlatMenuArrowIcon();

		testValue( icon, "arrowType", "chevron" );
		testValue( icon, "arrowColor", Color.WHITE );
		testValue( icon, "disabledArrowColor", Color.WHITE );
		testValue( icon, "selectionForeground", Color.WHITE );
	}

	@Test
	void flatHelpButtonIcon() {
		FlatHelpButtonIcon icon = new FlatHelpButtonIcon();

		testValue( icon, "focusWidth", 2 );
		testValue( icon, "focusColor", Color.WHITE );
		testValue( icon, "innerFocusWidth", 0.5f );
		testValue( icon, "borderWidth", 1 );

		testValue( icon, "borderColor", Color.WHITE );
		testValue( icon, "disabledBorderColor", Color.WHITE );
		testValue( icon, "focusedBorderColor", Color.WHITE );
		testValue( icon, "hoverBorderColor", Color.WHITE );
		testValue( icon, "background", Color.WHITE );
		testValue( icon, "disabledBackground", Color.WHITE );
		testValue( icon, "focusedBackground", Color.WHITE );
		testValue( icon, "hoverBackground", Color.WHITE );
		testValue( icon, "pressedBackground", Color.WHITE );
		testValue( icon, "questionMarkColor", Color.WHITE );
		testValue( icon, "disabledQuestionMarkColor", Color.WHITE );
	}

	@Test
	void flatClearIcon() {
		FlatClearIcon icon = new FlatClearIcon();

		testValue( icon, "clearIconColor", Color.WHITE );
		testValue( icon, "clearIconHoverColor", Color.WHITE );
		testValue( icon, "clearIconPressedColor", Color.WHITE );
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
		testValue( icon, "searchIconColor", Color.WHITE );
		testValue( icon, "searchIconHoverColor", Color.WHITE );
		testValue( icon, "searchIconPressedColor", Color.WHITE );
	}

	//---- class TestIcon -----------------------------------------------------

	@SuppressWarnings( "EqualsHashCode" ) // Error Prone
	public static class TestIcon
		implements Icon
	{
		@Override public void paintIcon( Component c, Graphics g, int x, int y ) {}
		@Override public int getIconWidth() { return 0; }
		@Override public int getIconHeight() { return 0; }

		@Override
		public boolean equals( Object obj ) {
			return obj instanceof TestIcon;
		}
	}
}
