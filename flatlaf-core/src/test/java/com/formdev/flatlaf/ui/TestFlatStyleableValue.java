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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import com.formdev.flatlaf.icons.*;
import com.formdev.flatlaf.ui.FlatInternalFrameUI.FlatInternalFrameBorder;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableObject;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.UnknownStyleException;
import com.formdev.flatlaf.ui.TestFlatStyling.CustomCheckBoxIcon;
import com.formdev.flatlaf.ui.TestFlatStyling.CustomIcon;
import com.formdev.flatlaf.ui.TestFlatStyling.CustomRadioButtonIcon;

/**
 * @author Karl Tauber
 */
public class TestFlatStyleableValue
{
	private final Random random = new Random();

	@BeforeAll
	static void setup() {
		TestUtils.setup( false );

		Set<String> excludes = new HashSet<>();
		TestUtils.checkImplementedTests( excludes, TestFlatStyleableValue.class,
			TestFlatStyleableInfo.class );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
	}

	private Map<String, Class<?>> expectedStyleableInfos;
	private final Set<String> testedKeys = new HashSet<>();

	@BeforeEach
	void beforeTest() {
		expectedStyleableInfos = null;
		testedKeys.clear();
	}

	@AfterEach
	void afterTest() {
		if( expectedStyleableInfos == null )
			throw new AssertionFailedError( "missing 'expectedStyleableInfos'" );

		TestUtils.assertSetEquals( expectedStyleableInfos.keySet(), testedKeys, "untested keys" );
	}

	private void testString( JComponent c, StyleableUI ui, String key, String value ) {
		applyStyle( c, ui, String.format( "%s: %s", key, value ) );
		assertEquals( value, ui.getStyleableValue( c, key ) );
		testedKeys.add( key );
	}

	private void testBoolean( JComponent c, StyleableUI ui, String key ) {
		boolean value = random.nextBoolean();
		applyStyle( c, ui, String.format( "%s: %s", key, value ) );
		assertEquals( value, ui.getStyleableValue( c, key ) );
		testedKeys.add( key );
	}

	private void testInteger( JComponent c, StyleableUI ui, String key ) {
		int value = random.nextInt();
		applyStyle( c, ui, String.format( "%s: %d", key, value ) );
		assertEquals( value, ui.getStyleableValue( c, key ) );
		testedKeys.add( key );
	}

	private void testFloat( JComponent c, StyleableUI ui, String key ) {
		float value = Math.round( random.nextFloat() * 100f ) / 100f;
		applyStyle( c, ui, String.format( Locale.ENGLISH, "%s: %f", key, value ) );
		assertEquals( value, ui.getStyleableValue( c, key ) );
		testedKeys.add( key );
	}

	private void testColor( JComponent c, StyleableUI ui, String key ) {
		int rgb = random.nextInt() & 0xffffff;
		applyStyle( c, ui, String.format( "%s: #%06x", key, rgb ) );
		assertEquals( new Color( rgb ), ui.getStyleableValue( c, key ) );
		testedKeys.add( key );
	}

	private void testInsets( JComponent c, StyleableUI ui, String key ) {
		int top = random.nextInt();
		int left = random.nextInt();
		int bottom = random.nextInt();
		int right = random.nextInt();
		applyStyle( c,ui, String.format( "%s: %d,%d,%d,%d", key, top, left, bottom, right ) );
		assertEquals( new Insets( top, left, bottom, right ), ui.getStyleableValue( c, key ) );
		testedKeys.add( key );
	}

	private void testDimension( JComponent c, StyleableUI ui, String key ) {
		int width = random.nextInt();
		int height = random.nextInt();
		applyStyle( c,ui, String.format( "%s: %d,%d", key, width, height ) );
		assertEquals( new Dimension( width, height ), ui.getStyleableValue( c, key ) );
		testedKeys.add( key );
	}

	private void testFont( JComponent c, StyleableUI ui, String key, String style, Font expectedFont ) {
		applyStyle( c,ui, String.format( "%s: %s", key, style ) );
		assertEquals( expectedFont, ui.getStyleableValue( c, key ) );
		testedKeys.add( key );
	}

	private void testIcon( JComponent c, StyleableUI ui, String key, String classname, Icon expectedIcon ) {
		applyStyle( c,ui, String.format( "%s: %s", key, classname ) );
		assertEquals( expectedIcon, ui.getStyleableValue( c, key ) );
		testedKeys.add( key );
	}

	private void applyStyle( JComponent c, StyleableUI ui, String style ) {
		Class<?> uiClass = ui.getClass();
		Class<?> compClass = c.getClass();
		CacheKey key = new CacheKey( uiClass, compClass );

		Method m = methodCache.get( key );
		if( m == null )
			m = findMethod( uiClass, "applyStyle", Object.class );
		if( m == null )
			m = findMethod( uiClass, "applyStyle", compClass, Object.class );
		if( m == null )
			m = findMethod( uiClass, "applyStyle", compClass.getSuperclass(), Object.class );
		if( m == null )
			m = findMethod( uiClass, "applyStyle", compClass.getSuperclass().getSuperclass(), Object.class );
		if( m == null ) {
			Assertions.fail( "missing method '" + uiClass
				+ ".applyStyle( Object )' or 'applyStyle( " + compClass.getSimpleName()
				+ ", Object )'" );
			return;
		}
		methodCache.put( key, m );

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

	private Method findMethod( Class<?> uiClass, String name, Class<?>... parameterTypes ) {
		for( Class<?> cls = uiClass; cls != null; cls = cls.getSuperclass() ) {
			try {
				return cls.getDeclaredMethod( name, parameterTypes );
			} catch( Exception ex ) {
				// ignore
			}
		}
		return null;
	}

	private static class CacheKey {
		private final Class<?> uiClass;
		private final Class<?> compClass;

		CacheKey( Class<?> uiClass, Class<?> compClass ) {
			this.uiClass = uiClass;
			this.compClass = compClass;
		}

		@Override
		public boolean equals( Object obj ) {
			return obj instanceof CacheKey &&
				uiClass == ((CacheKey)obj).uiClass &&
				compClass == ((CacheKey)obj).compClass;
		}

		@Override
		public int hashCode() {
			return uiClass.hashCode() * 13 + compClass.hashCode();
		}
	}

	private final Map<CacheKey, Method> methodCache = new HashMap<>();

	private void testValueString( Object obj, String key, String value ) {
		testValue( obj, key, value );
	}

	private void testValueBoolean( Object obj, String key ) {
		testValue( obj, key, random.nextBoolean() );
	}

	private void testValueInteger( Object obj, String key ) {
		testValue( obj, key, random.nextInt() );
	}

	private void testValueFloat( Object obj, String key ) {
		testValue( obj, key, Math.round( random.nextFloat() * 100f ) / 100f );
	}

	private void testValueColor( Object obj, String key ) {
		testValue( obj, key, new Color( random.nextInt() & 0xffffff ) );
	}

	private void testValueInsets( Object obj, String key ) {
		testValue( obj, key, new Insets( random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt() ) );
	}

	private void testValueDimension( Object obj, String key ) {
		testValue( obj, key, new Dimension( random.nextInt(), random.nextInt() ) );
	}

	private void testValue( Object obj, String key, Object value ) {
		assertInstanceOf( StyleableObject.class, obj );

		StyleableObject sobj = (StyleableObject) obj;
		sobj.applyStyleProperty( key, value );
		Object actualValue = sobj.getStyleableValue( key );

		assertEquals( value, actualValue );

		testedKeys.add( key );
	}

	//---- components ---------------------------------------------------------

	@Test
	void button() {
		JButton c = new JButton();
		FlatButtonUI ui = (FlatButtonUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		button( c, ui );

		//---- FlatHelpButtonIcon ----

		testFloat( c, ui, "help.scale" );

		testInteger( c, ui, "help.focusWidth" );
		testColor( c, ui, "help.focusColor" );
		testFloat( c, ui, "help.innerFocusWidth" );
		testInteger( c, ui, "help.borderWidth" );

		testColor( c, ui, "help.borderColor" );
		testColor( c, ui, "help.disabledBorderColor" );
		testColor( c, ui, "help.focusedBorderColor" );
		testColor( c, ui, "help.hoverBorderColor" );
		testColor( c, ui, "help.background" );
		testColor( c, ui, "help.disabledBackground" );
		testColor( c, ui, "help.focusedBackground" );
		testColor( c, ui, "help.hoverBackground" );
		testColor( c, ui, "help.pressedBackground" );
		testColor( c, ui, "help.questionMarkColor" );
		testColor( c, ui, "help.disabledQuestionMarkColor" );
	}

	private void button( AbstractButton c, FlatButtonUI ui ) {
		testInteger( c, ui, "minimumWidth" );

		testColor( c, ui, "focusedBackground" );
		testColor( c, ui, "focusedForeground" );
		testColor( c, ui, "hoverBackground" );
		testColor( c, ui, "hoverForeground" );
		testColor( c, ui, "pressedBackground" );
		testColor( c, ui, "pressedForeground" );
		testColor( c, ui, "selectedBackground" );
		testColor( c, ui, "selectedForeground" );
		testColor( c, ui, "disabledBackground" );
		testColor( c, ui, "disabledText" );
		testColor( c, ui, "disabledSelectedBackground" );
		testColor( c, ui, "disabledSelectedForeground" );

		testColor( c, ui, "default.background" );
		testColor( c, ui, "default.foreground" );
		testColor( c, ui, "default.focusedBackground" );
		testColor( c, ui, "default.focusedForeground" );
		testColor( c, ui, "default.hoverBackground" );
		testColor( c, ui, "default.hoverForeground" );
		testColor( c, ui, "default.pressedBackground" );
		testColor( c, ui, "default.pressedForeground" );
		testBoolean( c, ui, "default.boldText" );

		testBoolean( c, ui, "paintShadow" );
		testInteger( c, ui, "shadowWidth" );
		testColor( c, ui, "shadowColor" );
		testColor( c, ui, "default.shadowColor" );

		testInsets( c, ui, "toolbar.spacingInsets" );
		testColor( c, ui, "toolbar.hoverBackground" );
		testColor( c, ui, "toolbar.hoverForeground" );
		testColor( c, ui, "toolbar.pressedBackground" );
		testColor( c, ui, "toolbar.pressedForeground" );
		testColor( c, ui, "toolbar.selectedBackground" );
		testColor( c, ui, "toolbar.selectedForeground" );
		testColor( c, ui, "toolbar.disabledSelectedBackground" );
		testColor( c, ui, "toolbar.disabledSelectedForeground" );

		testString( c, ui, "buttonType", "help" );
		testBoolean( c, ui, "squareSize" );
		testInteger( c, ui, "minimumHeight" );

		// border
		flatButtonBorder( c, ui );
	}

	@Test
	void checkBox() {
		checkBox( new JCheckBox() );
	}

	@Test
	void checkBox2() {
		checkBox( new JCheckBox( new CustomCheckBoxIcon() ) );
	}

	@Test
	void checkBox3() {
		checkBox( new JCheckBox( new CustomIcon() ) );
	}

	private void checkBox( JCheckBox c ) {
		FlatCheckBoxUI ui = (FlatCheckBoxUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		// FlatCheckBoxUI extends FlatRadioButtonUI
		radioButton( ui, c );

		// necessary to clear FlatRadioButtonUI.oldStyleValues because
		// ui.applyStyle(...) operates on shared instance
		ui.uninstallUI( c );
	}

	@Test
	void comboBox() {
		JComboBox<Object> c = new JComboBox<>();
		FlatComboBoxUI ui = (FlatComboBoxUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInsets( c, ui, "padding" );

		testInteger( c, ui, "minimumWidth" );
		testInteger( c, ui, "editorColumns" );
		testString( c, ui, "buttonStyle", "auto" );
		testString( c, ui, "arrowType", "chevron" );

		testColor( c, ui, "editableBackground" );
		testColor( c, ui, "focusedBackground" );
		testColor( c, ui, "disabledBackground" );
		testColor( c, ui, "disabledForeground" );

		testColor( c, ui, "buttonBackground" );
		testColor( c, ui, "buttonFocusedBackground" );
		testColor( c, ui, "buttonEditableBackground" );
		testFloat( c, ui, "buttonSeparatorWidth" );
		testColor( c, ui, "buttonSeparatorColor" );
		testColor( c, ui, "buttonDisabledSeparatorColor" );
		testColor( c, ui, "buttonArrowColor" );
		testColor( c, ui, "buttonDisabledArrowColor" );
		testColor( c, ui, "buttonHoverArrowColor" );
		testColor( c, ui, "buttonPressedArrowColor" );

		testColor( c, ui, "popupBackground" );
		testInsets( c, ui, "popupInsets" );
		testInsets( c, ui, "selectionInsets" );
		testInteger( c, ui, "selectionArc" );

		// border
		flatRoundBorder( c, ui );
	}

	@Test
	void editorPane() {
		JEditorPane c = new JEditorPane();
		FlatEditorPaneUI ui = (FlatEditorPaneUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInteger( c, ui, "minimumWidth" );
		testColor( c, ui, "disabledBackground" );
		testColor( c, ui, "inactiveBackground" );
		testColor( c, ui, "focusedBackground" );
	}

	@Test
	void formattedTextField() {
		JFormattedTextField c = new JFormattedTextField();
		FlatFormattedTextFieldUI ui = (FlatFormattedTextFieldUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		// FlatFormattedTextFieldUI extends FlatTextFieldUI
		textField( c, ui );
	}

	@Test
	void internalFrame() {
		JInternalFrame c = new JInternalFrame();
		FlatInternalFrameUI ui = (FlatInternalFrameUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testColor( c, ui, "activeBorderColor" );
		testColor( c, ui, "inactiveBorderColor" );
		testInteger( c, ui, "borderLineWidth" );
		testBoolean( c, ui, "dropShadowPainted" );
		testInsets( c, ui, "borderMargins" );

		testColor( c, ui, "activeDropShadowColor" );
		testInsets( c, ui, "activeDropShadowInsets" );
		testFloat( c, ui, "activeDropShadowOpacity" );
		testColor( c, ui, "inactiveDropShadowColor" );
		testInsets( c, ui, "inactiveDropShadowInsets" );
		testFloat( c, ui, "inactiveDropShadowOpacity" );
	}

	@Test
	void label() {
		JLabel c = new JLabel();
		FlatLabelUI ui = (FlatLabelUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testColor( c, ui, "disabledForeground" );
		testInteger( c, ui, "arc" );
	}

	@Test
	void list() {
		JList<Object> c = new JList<>();
		FlatListUI ui = (FlatListUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testColor( c, ui, "selectionBackground" );
		testColor( c, ui, "selectionForeground" );
		testColor( c, ui, "selectionInactiveBackground" );
		testColor( c, ui, "selectionInactiveForeground" );
		testColor( c, ui, "alternateRowColor" );
		testInsets( c, ui, "selectionInsets" );
		testInteger( c, ui, "selectionArc" );

		// FlatListCellBorder
		testInsets( c, ui, "cellMargins" );
		testColor( c, ui, "cellFocusColor" );
		testBoolean( c, ui, "showCellFocusIndicator" );
	}

	@Test
	void menuBar() {
		JMenuBar c = new JMenuBar();
		FlatMenuBarUI ui = (FlatMenuBarUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInsets( c, ui, "itemMargins" );
		testInsets( c, ui, "selectionInsets" );
		testInsets( c, ui, "selectionEmbeddedInsets" );
		testInteger( c, ui, "selectionArc" );
		testColor( c, ui, "hoverBackground" );
		testColor( c, ui, "selectionBackground" );
		testColor( c, ui, "selectionForeground" );
		testColor( c, ui, "underlineSelectionBackground" );
		testColor( c, ui, "underlineSelectionColor" );
		testInteger( c, ui, "underlineSelectionHeight" );

		// FlatMenuBarBorder
		testColor( c, ui, "borderColor" );
	}

	@Test
	void menu() {
		JMenu c = new JMenu();
		FlatMenuUI ui = (FlatMenuUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		menuItem( c, ui );
		menuItem_arrowIcon( c, ui );
	}

	@Test
	void menuItem() {
		JMenuItem c = new JMenuItem();
		FlatMenuItemUI ui = (FlatMenuItemUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		menuItem( c, ui );
	}

	@Test
	void checkBoxMenuItem() {
		JCheckBoxMenuItem c = new JCheckBoxMenuItem();
		FlatCheckBoxMenuItemUI ui = (FlatCheckBoxMenuItemUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		menuItem( c, ui );
		menuItem_checkIcon( c, ui );
	}

	@Test
	void radioButtonMenuItem() {
		JRadioButtonMenuItem c = new JRadioButtonMenuItem();
		FlatRadioButtonMenuItemUI ui = (FlatRadioButtonMenuItemUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		menuItem( c, ui );
		menuItem_checkIcon( c, ui );
	}

	private void menuItem( JComponent c, StyleableUI ui ) {
		testColor( c, ui, "selectionBackground" );
		testColor( c, ui, "selectionForeground" );
		testColor( c, ui, "disabledForeground" );
		testColor( c, ui, "acceleratorForeground" );
		testColor( c, ui, "acceleratorSelectionForeground" );
		testFont( c, ui, "acceleratorFont", "italic 12 monospaced", new Font( "monospaced", Font.ITALIC, 12 ) );

		menuItemRenderer( c, ui );
	}

	private void menuItemRenderer( JComponent c, StyleableUI ui ) {
		testBoolean( c, ui, "verticallyAlignText" );
		testInteger( c, ui, "minimumWidth" );
		testDimension( c, ui, "minimumIconSize" );
		testInteger( c, ui, "textAcceleratorGap" );
		testInteger( c, ui, "textNoAcceleratorGap" );
		testInteger( c, ui, "acceleratorArrowGap" );

		testColor( c, ui, "checkBackground" );
		testInsets( c, ui, "checkMargins" );

		testInsets( c, ui, "selectionInsets" );
		testInteger( c, ui, "selectionArc" );

		testColor( c, ui, "underlineSelectionBackground" );
		testColor( c, ui, "underlineSelectionCheckBackground" );
		testColor( c, ui, "underlineSelectionColor" );
		testInteger( c, ui, "underlineSelectionHeight" );
	}

	private void menuItem_checkIcon( JComponent c, StyleableUI ui ) {
		testFloat( c, ui, "icon.scale" );

		testColor( c, ui, "icon.checkmarkColor" );
		testColor( c, ui, "icon.disabledCheckmarkColor" );
		testColor( c, ui, "selectionForeground" );
	}

	private void menuItem_arrowIcon( JComponent c, StyleableUI ui ) {
		testFloat( c, ui, "icon.scale" );

		testString( c, ui, "icon.arrowType", "chevron" );
		testColor( c, ui, "icon.arrowColor" );
		testColor( c, ui, "icon.disabledArrowColor" );
		testColor( c, ui, "selectionForeground" );
	}

	@Test
	void panel() {
		JPanel c = new JPanel();
		FlatPanelUI ui = (FlatPanelUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInteger( c, ui, "arc" );
	}

	@Test
	void passwordField() {
		JPasswordField c = new JPasswordField();
		FlatPasswordFieldUI ui = (FlatPasswordFieldUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		// FlatPasswordFieldUI extends FlatTextFieldUI
		textField( c, ui );

		testBoolean( c, ui, "showCapsLock" );
		testBoolean( c, ui, "showRevealButton" );

		// capsLockIcon
		testFloat( c, ui, "capsLockIconScale" );
		testColor( c, ui, "capsLockIconColor" );

		// border
		flatTextBorder( c, ui );
	}

	@Test
	void popupMenu() {
		JPopupMenu c = new JPopupMenu();
		FlatPopupMenuUI ui = (FlatPopupMenuUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testString( c, ui, "arrowType", "chevron" );
		testColor( c, ui, "scrollArrowColor" );
		testColor( c, ui, "hoverScrollArrowBackground" );

		testInsets( c, ui, "borderInsets" );
		testColor( c, ui, "borderColor" );
	}

	@Test
	void popupMenuSeparator() {
		JPopupMenu.Separator c = new JPopupMenu.Separator();
		FlatPopupMenuSeparatorUI ui = (FlatPopupMenuSeparatorUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		// FlatPopupMenuSeparatorUI extends FlatSeparatorUI
		separator( ui, c );
	}

	@Test
	void progressBar() {
		JProgressBar c = new JProgressBar();
		FlatProgressBarUI ui = (FlatProgressBarUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInteger( c, ui, "arc" );
		testDimension( c, ui, "horizontalSize" );
		testDimension( c, ui, "verticalSize" );

		testBoolean( c, ui, "largeHeight" );
		testBoolean( c, ui, "square" );
	}

	@Test
	void radioButton() {
		radioButton( new JRadioButton() );
	}

	@Test
	void radioButton2() {
		radioButton( new JRadioButton( new CustomRadioButtonIcon() ) );
	}

	@Test
	void radioButton3() {
		radioButton( new JRadioButton( new CustomIcon() ) );
	}

	private void radioButton( JRadioButton c ) {
		FlatRadioButtonUI ui = (FlatRadioButtonUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		assertTrue( ui.getDefaultIcon() instanceof FlatRadioButtonIcon );

		radioButton( ui, c );

		if( !(c.getIcon() instanceof CustomIcon) )
			testFloat( c, ui, "icon.centerDiameter" );

		// necessary to clear FlatRadioButtonUI.oldStyleValues because
		// ui.applyStyle(...) operates on shared instance
		ui.uninstallUI( c );
	}

	private void radioButton( FlatRadioButtonUI ui, AbstractButton b ) {
		testColor( b, ui, "disabledText" );

		//---- icon ----

		if( b.getIcon() instanceof CustomIcon ) {
			try {
				ui.applyStyle( b, "icon.focusWidth: 1.23" );
				assertTrue( false );
			} catch( UnknownStyleException ex ) {
				assertEquals( new UnknownStyleException( "icon.focusWidth" ).getMessage(), ex.getMessage() );
			}
			assertEquals( null, ui.getStyleableValue( b, "icon.focusWidth" ) );
			return;
		}

		testFloat( b, ui, "icon.scale" );

		testFloat( b, ui, "icon.focusWidth" );
		testColor( b, ui, "icon.focusColor" );
		testFloat( b, ui, "icon.borderWidth" );
		testFloat( b, ui, "icon.selectedBorderWidth" );
		testFloat( b, ui, "icon.disabledSelectedBorderWidth" );
		testFloat( b, ui, "icon.indeterminateBorderWidth" );
		testFloat( b, ui, "icon.disabledIndeterminateBorderWidth" );
		testInteger( b, ui, "icon.arc" );

		// enabled
		testColor( b, ui, "icon.borderColor" );
		testColor( b, ui, "icon.background" );
		testColor( b, ui, "icon.selectedBorderColor" );
		testColor( b, ui, "icon.selectedBackground" );
		testColor( b, ui, "icon.checkmarkColor" );
		testColor( b, ui, "icon.indeterminateBorderColor" );
		testColor( b, ui, "icon.indeterminateBackground" );
		testColor( b, ui, "icon.indeterminateCheckmarkColor" );

		// disabled
		testColor( b, ui, "icon.disabledBorderColor" );
		testColor( b, ui, "icon.disabledBackground" );
		testColor( b, ui, "icon.disabledSelectedBorderColor" );
		testColor( b, ui, "icon.disabledSelectedBackground" );
		testColor( b, ui, "icon.disabledCheckmarkColor" );
		testColor( b, ui, "icon.disabledIndeterminateBorderColor" );
		testColor( b, ui, "icon.disabledIndeterminateBackground" );
		testColor( b, ui, "icon.disabledIndeterminateCheckmarkColor" );

		// focused
		testColor( b, ui, "icon.focusedBorderColor" );
		testColor( b, ui, "icon.focusedBackground" );
		testColor( b, ui, "icon.focusedSelectedBorderColor" );
		testColor( b, ui, "icon.focusedSelectedBackground" );
		testColor( b, ui, "icon.focusedCheckmarkColor" );
		testColor( b, ui, "icon.focusedIndeterminateBorderColor" );
		testColor( b, ui, "icon.focusedIndeterminateBackground" );
		testColor( b, ui, "icon.focusedIndeterminateCheckmarkColor" );

		// hover
		testColor( b, ui, "icon.hoverBorderColor" );
		testColor( b, ui, "icon.hoverBackground" );
		testColor( b, ui, "icon.hoverSelectedBorderColor" );
		testColor( b, ui, "icon.hoverSelectedBackground" );
		testColor( b, ui, "icon.hoverCheckmarkColor" );
		testColor( b, ui, "icon.hoverIndeterminateBorderColor" );
		testColor( b, ui, "icon.hoverIndeterminateBackground" );
		testColor( b, ui, "icon.hoverIndeterminateCheckmarkColor" );

		// pressed
		testColor( b, ui, "icon.pressedBorderColor" );
		testColor( b, ui, "icon.pressedBackground" );
		testColor( b, ui, "icon.pressedSelectedBorderColor" );
		testColor( b, ui, "icon.pressedSelectedBackground" );
		testColor( b, ui, "icon.pressedCheckmarkColor" );
		testColor( b, ui, "icon.pressedIndeterminateBorderColor" );
		testColor( b, ui, "icon.pressedIndeterminateBackground" );
		testColor( b, ui, "icon.pressedIndeterminateCheckmarkColor" );
	}

	@Test
	void scrollBar() {
		JScrollBar c = new JScrollBar();
		FlatScrollBarUI ui = (FlatScrollBarUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testColor( c, ui, "track" );
		testColor( c, ui, "thumb" );
		testInteger( c, ui, "width" );
		testDimension( c, ui, "minimumThumbSize" );
		testDimension( c, ui, "maximumThumbSize" );
		testBoolean( c, ui, "allowsAbsolutePositioning" );

		testDimension( c, ui, "minimumButtonSize" );
		testInsets( c, ui, "trackInsets" );
		testInsets( c, ui, "thumbInsets" );
		testInteger( c, ui, "trackArc" );
		testInteger( c, ui, "thumbArc" );
		testColor( c, ui, "hoverTrackColor" );
		testColor( c, ui, "hoverThumbColor" );
		testBoolean( c, ui, "hoverThumbWithTrack" );
		testColor( c, ui, "pressedTrackColor" );
		testColor( c, ui, "pressedThumbColor" );
		testBoolean( c, ui, "pressedThumbWithTrack" );

		testBoolean( c, ui, "showButtons" );
		testString( c, ui, "arrowType", "chevron" );
		testColor( c, ui, "buttonArrowColor" );
		testColor( c, ui, "buttonDisabledArrowColor" );
		testColor( c, ui, "hoverButtonBackground" );
		testColor( c, ui, "pressedButtonBackground" );
	}

	@Test
	void scrollPane() {
		JScrollPane c = new JScrollPane();
		FlatScrollPaneUI ui = (FlatScrollPaneUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		// border
		flatScrollPaneBorder( c, ui );

		testBoolean( c, ui, "showButtons" );
	}

	@Test
	void separator() {
		JSeparator c = new JSeparator();
		FlatSeparatorUI ui = (FlatSeparatorUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		separator( ui, c );
	}

	private void separator( FlatSeparatorUI ui, JSeparator c ) {
		testInteger( c, ui, "height" );
		testInteger( c, ui, "stripeWidth" );
		testInteger( c, ui, "stripeIndent" );
	}

	@Test
	void slider() {
		JSlider c = new JSlider();
		FlatSliderUI ui = (FlatSliderUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInteger( c, ui, "trackWidth" );
		testDimension( c, ui, "thumbSize" );
		testInteger( c, ui, "focusWidth" );
		testFloat( c, ui, "thumbBorderWidth" );

		testColor( c, ui, "trackValueColor" );
		testColor( c, ui, "trackColor" );
		testColor( c, ui, "thumbColor" );
		testColor( c, ui, "thumbBorderColor" );
		testColor( c, ui, "focusedColor" );
		testColor( c, ui, "focusedThumbBorderColor" );
		testColor( c, ui, "hoverThumbColor" );
		testColor( c, ui, "pressedThumbColor" );
		testColor( c, ui, "disabledTrackColor" );
		testColor( c, ui, "disabledThumbColor" );
		testColor( c, ui, "disabledThumbBorderColor" );
		testColor( c, ui, "tickColor" );
	}

	@Test
	void spinner() {
		JSpinner c = new JSpinner();
		FlatSpinnerUI ui = (FlatSpinnerUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInteger( c, ui, "minimumWidth" );
		testString( c, ui, "buttonStyle", "button" );
		testString( c, ui, "arrowType", "chevron" );
		testColor( c, ui, "disabledBackground" );
		testColor( c, ui, "disabledForeground" );
		testColor( c, ui, "focusedBackground" );
		testColor( c, ui, "buttonBackground" );
		testFloat( c, ui, "buttonSeparatorWidth" );
		testColor( c, ui, "buttonSeparatorColor" );
		testColor( c, ui, "buttonDisabledSeparatorColor" );
		testColor( c, ui, "buttonArrowColor" );
		testColor( c, ui, "buttonDisabledArrowColor" );
		testColor( c, ui, "buttonHoverArrowColor" );
		testColor( c, ui, "buttonPressedArrowColor" );
		testInsets( c, ui, "padding" );

		// border
		flatRoundBorder( c, ui );
	}

	@Test
	void splitPane() {
		JSplitPane c = new JSplitPane();
		FlatSplitPaneUI ui = (FlatSplitPaneUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testString( c, ui, "arrowType", "chevron" );
		testColor( c, ui, "draggingColor" );
		testColor( c, ui, "hoverColor" );
		testColor( c, ui, "pressedColor" );
		testColor( c, ui, "oneTouchArrowColor" );
		testColor( c, ui, "oneTouchHoverArrowColor" );
		testColor( c, ui, "oneTouchPressedArrowColor" );

		testString( c, ui, "style", "grip" );
		testColor( c, ui, "gripColor" );
		testInteger( c, ui, "gripDotCount" );
		testInteger( c, ui, "gripDotSize" );
		testInteger( c, ui, "gripGap" );
	}

	@Test
	void tabbedPane() {
		JTabbedPane c = new JTabbedPane();
		FlatTabbedPaneUI ui = (FlatTabbedPaneUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInsets( c, ui, "tabInsets" );
		testInsets( c, ui, "tabAreaInsets" );
		testInteger( c, ui, "textIconGap" );

		testColor( c, ui, "disabledForeground" );

		testColor( c, ui, "selectedBackground" );
		testColor( c, ui, "selectedForeground" );
		testColor( c, ui, "underlineColor" );
		testColor( c, ui, "inactiveUnderlineColor" );
		testColor( c, ui, "disabledUnderlineColor" );
		testColor( c, ui, "hoverColor" );
		testColor( c, ui, "hoverForeground" );
		testColor( c, ui, "focusColor" );
		testColor( c, ui, "focusForeground" );
		testColor( c, ui, "tabSeparatorColor" );
		testColor( c, ui, "contentAreaColor" );

		testInteger( c, ui, "minimumTabWidth" );
		testInteger( c, ui, "maximumTabWidth" );
		testInteger( c, ui, "tabHeight" );
		testInteger( c, ui, "tabSelectionHeight" );
		testInteger( c, ui, "cardTabSelectionHeight" );
		testInteger( c, ui, "tabArc" );
		testInteger( c, ui, "tabSelectionArc" );
		testInteger( c, ui, "cardTabArc" );
		testInsets( c, ui, "selectedInsets" );
		testInsets( c, ui, "tabSelectionInsets" );
		testInteger( c, ui, "contentSeparatorHeight" );
		testBoolean( c, ui, "showTabSeparators" );
		testBoolean( c, ui, "tabSeparatorsFullHeight" );
		testBoolean( c, ui, "hasFullBorder" );
		testBoolean( c, ui, "tabsOpaque" );
		testBoolean( c, ui, "rotateTabRuns" );

		testString( c, ui, "tabType", "card" );
		testString( c, ui, "tabsPopupPolicy", "asNeeded" );
		testString( c, ui, "scrollButtonsPolicy", "asNeeded" );
		testString( c, ui, "scrollButtonsPlacement", "both" );

		testString( c, ui, "tabAreaAlignment", "leading" );
		testString( c, ui, "tabAlignment", "center" );
		testString( c, ui, "tabWidthMode", "preferred" );
		testString( c, ui, "tabRotation", "none" );

		testString( c, ui, "arrowType", "chevron" );
		testInsets( c, ui, "buttonInsets" );
		testInteger( c, ui, "buttonArc" );
		testColor( c, ui, "buttonHoverBackground" );
		testColor( c, ui, "buttonPressedBackground" );

		testString( c, ui, "moreTabsButtonToolTipText", "Gimme more" );
		testString( c, ui, "tabCloseToolTipText", "Close me" );

		testBoolean( c, ui, "showContentSeparator" );
		testBoolean( c, ui, "hideTabAreaWithOneTab" );
		testBoolean( c, ui, "tabClosable" );
		testString( c, ui, "tabIconPlacement", "top" );

		// FlatTabbedPaneCloseIcon
		testFloat( c, ui, "closeScale" );
		testDimension( c, ui, "closeSize" );
		testInteger( c, ui, "closeArc" );
		testFloat( c, ui, "closeCrossPlainSize" );
		testFloat( c, ui, "closeCrossFilledSize" );
		testFloat( c, ui, "closeCrossLineWidth" );
		testColor( c, ui, "closeBackground" );
		testColor( c, ui, "closeForeground" );
		testColor( c, ui, "closeHoverBackground" );
		testColor( c, ui, "closeHoverForeground" );
		testColor( c, ui, "closePressedBackground" );
		testColor( c, ui, "closePressedForeground" );
	}

	@Test
	void table() {
		JTable c = new JTable();
		FlatTableUI ui = (FlatTableUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testBoolean( c, ui, "showTrailingVerticalLine" );
		testColor( c, ui, "selectionBackground" );
		testColor( c, ui, "selectionForeground" );
		testColor( c, ui, "selectionInactiveBackground" );
		testColor( c, ui, "selectionInactiveForeground" );
		testInsets( c, ui, "selectionInsets" );
		testInteger( c, ui, "selectionArc" );

		// FlatTableCellBorder
		testInsets( c, ui, "cellMargins" );
		testColor( c, ui, "cellFocusColor" );
		testBoolean( c, ui, "showCellFocusIndicator" );
	}

	@Test
	void tableHeader() {
		JTableHeader c = new JTableHeader();
		FlatTableHeaderUI ui = (FlatTableHeaderUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testColor( c, ui, "hoverBackground" );
		testColor( c, ui, "hoverForeground" );
		testColor( c, ui, "pressedBackground" );
		testColor( c, ui, "pressedForeground" );
		testColor( c, ui, "bottomSeparatorColor" );
		testInteger( c, ui, "height" );
		testString( c, ui, "sortIconPosition", "top" );

		// FlatTableHeaderBorder
		testInsets( c, ui, "cellMargins" );
		testColor( c, ui, "separatorColor" );
		testBoolean( c, ui, "showTrailingVerticalLine" );

		// FlatAscendingSortIcon and FlatDescendingSortIcon
		testString( c, ui, "arrowType", "chevron" );
		testColor( c, ui, "sortIconColor" );
	}

	@Test
	void textArea() {
		JTextArea c = new JTextArea();
		FlatTextAreaUI ui = (FlatTextAreaUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInteger( c, ui, "minimumWidth" );
		testColor( c, ui, "disabledBackground" );
		testColor( c, ui, "inactiveBackground" );
		testColor( c, ui, "focusedBackground" );
	}

	@Test
	void textField() {
		JTextField c = new JTextField();
		FlatTextFieldUI ui = (FlatTextFieldUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		textField( c, ui );
	}

	private void textField( JTextComponent c, FlatTextFieldUI ui ) {
		testInteger( c, ui, "minimumWidth" );
		testColor( c, ui, "disabledBackground" );
		testColor( c, ui, "inactiveBackground" );
		testColor( c, ui, "placeholderForeground" );
		testColor( c, ui, "focusedBackground" );
		testInteger( c, ui, "iconTextGap" );
		testIcon( c, ui, "leadingIcon", TestIcon.class.getName(), new TestIcon() );
		testIcon( c, ui, "trailingIcon", TestIcon.class.getName(), new TestIcon() );

		testBoolean( c, ui, "showClearButton" );

		// border
		flatTextBorder( c, ui );
	}

	@Test
	void textPane() {
		JTextPane c = new JTextPane();
		FlatTextPaneUI ui = (FlatTextPaneUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInteger( c, ui, "minimumWidth" );
		testColor( c, ui, "disabledBackground" );
		testColor( c, ui, "inactiveBackground" );
		testColor( c, ui, "focusedBackground" );
	}

	@Test
	void toggleButton() {
		JToggleButton b = new JToggleButton();
		FlatToggleButtonUI ui = (FlatToggleButtonUI) b.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( b );

		// FlatToggleButtonUI extends FlatButtonUI
		button( b, ui );

		testInteger( b, ui, "tab.underlineHeight" );
		testColor( b, ui, "tab.underlineColor" );
		testColor( b, ui, "tab.disabledUnderlineColor" );
		testColor( b, ui, "tab.selectedBackground" );
		testColor( b, ui, "tab.selectedForeground" );
		testColor( b, ui, "tab.hoverBackground" );
		testColor( b, ui, "tab.hoverForeground" );
		testColor( b, ui, "tab.focusBackground" );
		testColor( b, ui, "tab.focusForeground" );
	}

	@Test
	void toolBar() {
		JToolBar c = new JToolBar();
		FlatToolBarUI ui = (FlatToolBarUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testBoolean( c, ui, "focusableButtons" );
		testBoolean( c, ui, "arrowKeysOnlyNavigation" );
		testInteger( c, ui, "hoverButtonGroupArc" );
		testColor( c, ui, "hoverButtonGroupBackground" );

		testInsets( c, ui, "borderMargins" );
		testColor( c, ui, "gripColor" );

		testInteger( c, ui, "separatorWidth" );
		testColor( c, ui, "separatorColor" );
	}

	@Test
	void toolBarSeparator() {
		JToolBar.Separator c = new JToolBar.Separator();
		FlatToolBarSeparatorUI ui = (FlatToolBarSeparatorUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testInteger( c, ui, "separatorWidth" );
		testColor( c, ui, "separatorColor" );
	}

	@Test
	void tree() {
		JTree c = new JTree();
		FlatTreeUI ui = (FlatTreeUI) c.getUI();
		expectedStyleableInfos = ui.getStyleableInfos( c );

		testColor( c, ui, "selectionBackground" );
		testColor( c, ui, "selectionForeground" );
		testColor( c, ui, "selectionInactiveBackground" );
		testColor( c, ui, "selectionInactiveForeground" );
		testColor( c, ui, "selectionBorderColor" );
		testColor( c, ui, "alternateRowColor" );
		testInsets( c, ui, "selectionInsets" );
		testInteger( c, ui, "selectionArc" );
		testBoolean( c, ui, "wideSelection" );
		testBoolean( c, ui, "wideCellRenderer" );
		testBoolean( c, ui, "showCellFocusIndicator" );

		testBoolean( c, ui, "paintSelection" );

		// icons
		testString( c, ui, "icon.arrowType", "chevron" );
		testColor( c, ui, "icon.expandedColor" );
		testColor( c, ui, "icon.collapsedColor" );
		testColor( c, ui, "icon.leafColor" );
		testColor( c, ui, "icon.closedColor" );
		testColor( c, ui, "icon.openColor" );
	}

	//---- component borders --------------------------------------------------

	private void flatButtonBorder( JComponent c, StyleableUI ui ) {
		flatBorder( c, ui );

		testInteger( c, ui, "arc" );

		testColor( c, ui, "borderColor" );
		testColor( c, ui, "disabledBorderColor" );
		testColor( c, ui, "focusedBorderColor" );
		testColor( c, ui, "hoverBorderColor" );
		testColor( c, ui, "pressedBorderColor" );

		testColor( c, ui, "selectedBorderColor" );
		testColor( c, ui, "disabledSelectedBorderColor" );
		testColor( c, ui, "focusedSelectedBorderColor" );
		testColor( c, ui, "hoverSelectedBorderColor" );
		testColor( c, ui, "pressedSelectedBorderColor" );

		testFloat( c, ui, "default.borderWidth" );
		testColor( c, ui, "default.borderColor" );
		testColor( c, ui, "default.focusedBorderColor" );
		testColor( c, ui, "default.focusColor" );
		testColor( c, ui, "default.hoverBorderColor" );
		testColor( c, ui, "default.pressedBorderColor" );

		testFloat( c, ui, "toolbar.focusWidth" );
		testColor( c, ui, "toolbar.focusColor" );
		testInsets( c, ui, "toolbar.margin" );
		testInsets( c, ui, "toolbar.spacingInsets" );
	}

	private void flatRoundBorder( JComponent c, StyleableUI ui ) {
		flatBorder( c, ui );

		testInteger( c, ui, "arc" );
		testBoolean( c, ui, "roundRect" );
	}

	private void flatScrollPaneBorder( JComponent c, StyleableUI ui ) {
		flatBorder( c, ui );

		testInteger( c, ui, "arc" );
	}

	private void flatTextBorder( JComponent c, StyleableUI ui ) {
		flatBorder( c, ui );

		testInteger( c, ui, "arc" );
		testBoolean( c, ui, "roundRect" );
	}

	private void flatBorder( JComponent c, StyleableUI ui ) {
		testInteger( c, ui, "focusWidth" );
		testFloat( c, ui, "innerFocusWidth" );
		testFloat( c, ui, "innerOutlineWidth" );
		testFloat( c, ui, "borderWidth" );

		testColor( c, ui, "focusColor" );
		testColor( c, ui, "borderColor" );
		testColor( c, ui, "disabledBorderColor" );
		testColor( c, ui, "focusedBorderColor" );

		testColor( c, ui, "error.borderColor" );
		testColor( c, ui, "error.focusedBorderColor" );
		testColor( c, ui, "warning.borderColor" );
		testColor( c, ui, "warning.focusedBorderColor" );
		testColor( c, ui, "success.borderColor" );
		testColor( c, ui, "success.focusedBorderColor" );
		testColor( c, ui, "custom.borderColor" );

		testString( c, ui, "outline", "error" );
		testColor( c, ui, "outlineColor" );
		testColor( c, ui, "outlineFocusedColor" );
	}

	//---- borders ------------------------------------------------------------

	@Test
	void flatButtonBorder() {
		FlatButtonBorder border = new FlatButtonBorder();
		expectedStyleableInfos = border.getStyleableInfos();

		// FlatButtonBorder extends FlatBorder
		flatBorder( border );

		testValueInteger( border, "arc" );

		testValueColor( border, "borderColor" );
		testValueColor( border, "disabledBorderColor" );
		testValueColor( border, "focusedBorderColor" );
		testValueColor( border, "hoverBorderColor" );
		testValueColor( border, "pressedBorderColor" );

		testValueColor( border, "selectedBorderColor" );
		testValueColor( border, "disabledSelectedBorderColor" );
		testValueColor( border, "focusedSelectedBorderColor" );
		testValueColor( border, "hoverSelectedBorderColor" );
		testValueColor( border, "pressedSelectedBorderColor" );

		testValueFloat( border, "default.borderWidth" );
		testValueColor( border, "default.borderColor" );
		testValueColor( border, "default.focusedBorderColor" );
		testValueColor( border, "default.focusColor" );
		testValueColor( border, "default.hoverBorderColor" );
		testValueColor( border, "default.pressedBorderColor" );

		testValueFloat( border, "toolbar.focusWidth" );
		testValueColor( border, "toolbar.focusColor" );
		testValueInsets( border, "toolbar.margin" );
		testValueInsets( border, "toolbar.spacingInsets" );
	}

	@Test
	void flatRoundBorder() {
		FlatRoundBorder border = new FlatRoundBorder();
		expectedStyleableInfos = border.getStyleableInfos();

		// FlatRoundBorder extends FlatBorder
		flatBorder( border );

		testValueInteger( border, "arc" );
		testValueBoolean( border, "roundRect" );
	}

	@Test
	void flatScrollPaneBorder() {
		FlatScrollPaneBorder border = new FlatScrollPaneBorder();
		expectedStyleableInfos = border.getStyleableInfos();

		// FlatScrollPaneBorder extends FlatBorder
		flatBorder( border );

		testValueInteger( border, "arc" );
	}

	@Test
	void flatTextBorder() {
		FlatTextBorder border = new FlatTextBorder();
		expectedStyleableInfos = border.getStyleableInfos();

		// FlatTextBorder extends FlatBorder
		flatBorder( border );

		testValueInteger( border, "arc" );
		testValueBoolean( border, "roundRect" );
	}

	@Test
	void flatBorder() {
		FlatBorder border = new FlatBorder();
		expectedStyleableInfos = border.getStyleableInfos();

		flatBorder( border );
	}

	private void flatBorder( FlatBorder border ) {
		testValueInteger( border, "focusWidth" );
		testValueFloat( border, "innerFocusWidth" );
		testValueFloat( border, "innerOutlineWidth" );
		testValueFloat( border, "borderWidth" );

		testValueColor( border, "focusColor" );
		testValueColor( border, "borderColor" );
		testValueColor( border, "disabledBorderColor" );
		testValueColor( border, "focusedBorderColor" );

		testValueColor( border, "error.borderColor" );
		testValueColor( border, "error.focusedBorderColor" );
		testValueColor( border, "warning.borderColor" );
		testValueColor( border, "warning.focusedBorderColor" );
		testValueColor( border, "success.borderColor" );
		testValueColor( border, "success.focusedBorderColor" );
		testValueColor( border, "custom.borderColor" );

		testValueString( border, "outline", "error" );
		testValueColor( border, "outlineColor" );
		testValueColor( border, "outlineFocusedColor" );
	}

	@Test
	void flatDropShadowBorder() {
		FlatDropShadowBorder border = new FlatDropShadowBorder();
		expectedStyleableInfos = border.getStyleableInfos();

		testValueColor( border, "shadowColor" );
		testValueInsets( border, "shadowInsets" );
		testValueFloat( border, "shadowOpacity" );
	}

	@Test
	void flatMenuBarBorder() {
		FlatMenuBarBorder border = new FlatMenuBarBorder();
		expectedStyleableInfos = border.getStyleableInfos();

		testValueColor( border, "borderColor" );
	}

	@Test
	void flatPopupMenuBorder() {
		FlatPopupMenuBorder border = new FlatPopupMenuBorder();
		expectedStyleableInfos = border.getStyleableInfos();

		testValueInsets( border, "borderInsets" );
		testValueColor( border, "borderColor" );
	}

	@Test
	void flatInternalFrameBorder() {
		FlatInternalFrameBorder border = new FlatInternalFrameBorder();
		expectedStyleableInfos = border.getStyleableInfos();

		testValueColor( border, "activeBorderColor" );
		testValueColor( border, "inactiveBorderColor" );
		testValueInteger( border, "borderLineWidth" );
		testValueBoolean( border, "dropShadowPainted" );
		testValueInsets( border, "borderMargins" );

		testValueColor( border, "activeDropShadowColor" );
		testValueInsets( border, "activeDropShadowInsets" );
		testValueFloat( border, "activeDropShadowOpacity" );
		testValueColor( border, "inactiveDropShadowColor" );
		testValueInsets( border, "inactiveDropShadowInsets" );
		testValueFloat( border, "inactiveDropShadowOpacity" );
	}

	//---- icons --------------------------------------------------------------

	@Test
	void flatCheckBoxIcon() {
		FlatCheckBoxIcon icon = new FlatCheckBoxIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		flatCheckBoxIcon( icon );
	}

	@Test
	void flatRadioButtonIcon() {
		FlatRadioButtonIcon icon = new FlatRadioButtonIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		// FlatRadioButtonIcon extends FlatCheckBoxIcon
		flatCheckBoxIcon( icon );

		testValueFloat( icon, "centerDiameter" );
	}

	private void flatCheckBoxIcon( FlatCheckBoxIcon icon ) {
		testValueFloat( icon, "scale" );

		testValueFloat( icon, "focusWidth" );
		testValueColor( icon, "focusColor" );
		testValueFloat( icon, "borderWidth" );
		testValueFloat( icon, "selectedBorderWidth" );
		testValueFloat( icon, "disabledSelectedBorderWidth" );
		testValueFloat( icon, "indeterminateBorderWidth" );
		testValueFloat( icon, "disabledIndeterminateBorderWidth" );
		testValueInteger( icon, "arc" );

		// enabled
		testValueColor( icon, "borderColor" );
		testValueColor( icon, "background" );
		testValueColor( icon, "selectedBorderColor" );
		testValueColor( icon, "selectedBackground" );
		testValueColor( icon, "checkmarkColor" );
		testValueColor( icon, "indeterminateBorderColor" );
		testValueColor( icon, "indeterminateBackground" );
		testValueColor( icon, "indeterminateCheckmarkColor" );

		// disabled
		testValueColor( icon, "disabledBorderColor" );
		testValueColor( icon, "disabledBackground" );
		testValueColor( icon, "disabledSelectedBorderColor" );
		testValueColor( icon, "disabledSelectedBackground" );
		testValueColor( icon, "disabledCheckmarkColor" );
		testValueColor( icon, "disabledIndeterminateBorderColor" );
		testValueColor( icon, "disabledIndeterminateBackground" );
		testValueColor( icon, "disabledIndeterminateCheckmarkColor" );

		// focused
		testValueColor( icon, "focusedBorderColor" );
		testValueColor( icon, "focusedBackground" );
		testValueColor( icon, "focusedSelectedBorderColor" );
		testValueColor( icon, "focusedSelectedBackground" );
		testValueColor( icon, "focusedCheckmarkColor" );
		testValueColor( icon, "focusedIndeterminateBorderColor" );
		testValueColor( icon, "focusedIndeterminateBackground" );
		testValueColor( icon, "focusedIndeterminateCheckmarkColor" );

		// hover
		testValueColor( icon, "hoverBorderColor" );
		testValueColor( icon, "hoverBackground" );
		testValueColor( icon, "hoverSelectedBorderColor" );
		testValueColor( icon, "hoverSelectedBackground" );
		testValueColor( icon, "hoverCheckmarkColor" );
		testValueColor( icon, "hoverIndeterminateBorderColor" );
		testValueColor( icon, "hoverIndeterminateBackground" );
		testValueColor( icon, "hoverIndeterminateCheckmarkColor" );

		// pressed
		testValueColor( icon, "pressedBorderColor" );
		testValueColor( icon, "pressedBackground" );
		testValueColor( icon, "pressedSelectedBorderColor" );
		testValueColor( icon, "pressedSelectedBackground" );
		testValueColor( icon, "pressedCheckmarkColor" );
		testValueColor( icon, "pressedIndeterminateBorderColor" );
		testValueColor( icon, "pressedIndeterminateBackground" );
		testValueColor( icon, "pressedIndeterminateCheckmarkColor" );
	}

	@Test
	void flatCheckBoxMenuItemIcon() {
		FlatCheckBoxMenuItemIcon icon = new FlatCheckBoxMenuItemIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		flatCheckBoxMenuItemIcon( icon );
	}

	@Test
	void flatRadioButtonMenuItemIcon() {
		FlatRadioButtonMenuItemIcon icon = new FlatRadioButtonMenuItemIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		// FlatRadioButtonMenuItemIcon extends FlatCheckBoxMenuItemIcon
		flatCheckBoxMenuItemIcon( icon );
	}

	private void flatCheckBoxMenuItemIcon( FlatCheckBoxMenuItemIcon icon ) {
		testValueFloat( icon, "scale" );

		testValueColor( icon, "checkmarkColor" );
		testValueColor( icon, "disabledCheckmarkColor" );
		testValueColor( icon, "selectionForeground" );
	}

	@Test
	void flatMenuArrowIcon() {
		FlatMenuArrowIcon icon = new FlatMenuArrowIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		testValueFloat( icon, "scale" );

		testValueString( icon, "arrowType", "chevron" );
		testValueColor( icon, "arrowColor" );
		testValueColor( icon, "disabledArrowColor" );
		testValueColor( icon, "selectionForeground" );
	}

	@Test
	void flatHelpButtonIcon() {
		FlatHelpButtonIcon icon = new FlatHelpButtonIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		testValueFloat( icon, "scale" );

		testValueInteger( icon, "focusWidth" );
		testValueColor( icon, "focusColor" );
		testValueFloat( icon, "innerFocusWidth" );
		testValueInteger( icon, "borderWidth" );

		testValueColor( icon, "borderColor" );
		testValueColor( icon, "disabledBorderColor" );
		testValueColor( icon, "focusedBorderColor" );
		testValueColor( icon, "hoverBorderColor" );
		testValueColor( icon, "background" );
		testValueColor( icon, "disabledBackground" );
		testValueColor( icon, "focusedBackground" );
		testValueColor( icon, "hoverBackground" );
		testValueColor( icon, "pressedBackground" );
		testValueColor( icon, "questionMarkColor" );
		testValueColor( icon, "disabledQuestionMarkColor" );
	}

	@Test
	void flatClearIcon() {
		FlatClearIcon icon = new FlatClearIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		testValueFloat( icon, "clearIconScale" );

		testValueColor( icon, "clearIconColor" );
		testValueColor( icon, "clearIconHoverColor" );
		testValueColor( icon, "clearIconPressedColor" );
	}

	@Test
	void flatSearchIcon() {
		FlatSearchIcon icon = new FlatSearchIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		flatSearchIcon( icon );
	}

	@Test
	void flatSearchWithHistoryIcon() {
		FlatSearchWithHistoryIcon icon = new FlatSearchWithHistoryIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		flatSearchIcon( icon );
	}

	private void flatSearchIcon( FlatSearchIcon icon ) {
		testValueFloat( icon, "searchIconScale" );

		testValueColor( icon, "searchIconColor" );
		testValueColor( icon, "searchIconHoverColor" );
		testValueColor( icon, "searchIconPressedColor" );
	}

	@Test
	void flatCapsLockIcon() {
		FlatCapsLockIcon icon = new FlatCapsLockIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		testValueFloat( icon, "capsLockIconScale" );
		testValueColor( icon, "capsLockIconColor" );
	}

	@Test
	void flatTabbedPaneCloseIcon() {
		FlatTabbedPaneCloseIcon icon = new FlatTabbedPaneCloseIcon();
		expectedStyleableInfos = icon.getStyleableInfos();

		testValueFloat( icon, "closeScale" );

		testValueDimension( icon, "closeSize" );
		testValueInteger( icon, "closeArc" );
		testValueFloat( icon, "closeCrossPlainSize" );
		testValueFloat( icon, "closeCrossFilledSize" );
		testValueFloat( icon, "closeCrossLineWidth" );
		testValueColor( icon, "closeBackground" );
		testValueColor( icon, "closeForeground" );
		testValueColor( icon, "closeHoverBackground" );
		testValueColor( icon, "closeHoverForeground" );
		testValueColor( icon, "closePressedBackground" );
		testValueColor( icon, "closePressedForeground" );
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
