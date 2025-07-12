/*
 * Copyright 2025 FormDev Software GmbH
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
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;

/**
 * @author Karl Tauber
 */
public class TestFlatButton
{
	@BeforeAll
	static void setup() {
		String[] defs = {
			"Button.background", "#000001",
			"Button.foreground", "#000002",
			"Button.focusedBackground", "#000003",
			"Button.focusedForeground", "#000004",
			"Button.hoverBackground", "#000005",
			"Button.hoverForeground", "#000006",
			"Button.pressedBackground", "#000007",
			"Button.pressedForeground", "#000008",
			"Button.selectedBackground", "#000009",
			"Button.selectedForeground", "#00000a",
			"Button.disabledBackground", "#00000b",
			"Button.disabledText", "#00000c",
			"Button.disabledSelectedBackground", "#00000d",
			"Button.disabledSelectedForeground", "#00000e",

			"Button.default.background", "#000101",
			"Button.default.foreground", "#000102",
			"Button.default.focusedBackground", "#000103",
			"Button.default.focusedForeground", "#000104",
			"Button.default.hoverBackground", "#000105",
			"Button.default.hoverForeground", "#000106",
			"Button.default.pressedBackground", "#000107",
			"Button.default.pressedForeground", "#000108",

			"Button.toolbar.hoverBackground", "#000201",
			"Button.toolbar.hoverForeground", "#000202",
			"Button.toolbar.pressedBackground", "#000203",
			"Button.toolbar.pressedForeground", "#000204",
			"Button.toolbar.selectedBackground", "#000205",
			"Button.toolbar.selectedForeground", "#000206",
			"Button.toolbar.disabledSelectedBackground", "#000207",
			"Button.toolbar.disabledSelectedForeground", "#000208",
		};

		HashMap<String, String> globalExtraDefaults = new HashMap<>();
		for( int i = 0; i < defs.length; i += 2 )
			globalExtraDefaults.put( defs[i], defs[i+1] );
		FlatLaf.setGlobalExtraDefaults( globalExtraDefaults );

		TestUtils.setup( false );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
	}

	@Test
	void background() {
		JButton b = new JButton();

		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			UIManager.getColor( "Button.background" ),
			UIManager.getColor( "Button.disabledBackground" ),
			UIManager.getColor( "Button.focusedBackground" ),
			UIManager.getColor( "Button.hoverBackground" ),
			UIManager.getColor( "Button.pressedBackground" ) );

		// selected
		b.setSelected( true );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			UIManager.getColor( "Button.selectedBackground" ),
			UIManager.getColor( "Button.disabledSelectedBackground" ),
			null,
			null,
			UIManager.getColor( "Button.pressedBackground" ) );
		b.setSelected( false );

		// default
		JRootPane rootPane = new JRootPane();
		rootPane.getContentPane().add( b );
		rootPane.setDefaultButton( b );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			UIManager.getColor( "Button.default.background" ),
			UIManager.getColor( "Button.disabledBackground" ),
			UIManager.getColor( "Button.default.focusedBackground" ),
			UIManager.getColor( "Button.default.hoverBackground" ),
			UIManager.getColor( "Button.default.pressedBackground" ) );
		rootPane.getContentPane().remove( b );
	}

	@Test
	void foreground() {
		JButton b = new JButton();

		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			UIManager.getColor( "Button.foreground" ),
			UIManager.getColor( "Button.disabledText" ),
			UIManager.getColor( "Button.focusedForeground" ),
			UIManager.getColor( "Button.hoverForeground" ),
			UIManager.getColor( "Button.pressedForeground" ) );

		// selected
		b.setSelected( true );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			UIManager.getColor( "Button.selectedForeground" ),
			FlatUIUtils.getUIColor( "Button.disabledSelectedForeground", "Button.disabledText" ),
			null,
			null,
			UIManager.getColor( "Button.pressedForeground" ) );
		b.setSelected( false );

		// default
		JRootPane rootPane = new JRootPane();
		rootPane.getContentPane().add( b );
		rootPane.setDefaultButton( b );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			UIManager.getColor( "Button.default.foreground" ),
			UIManager.getColor( "Button.disabledText" ),
			UIManager.getColor( "Button.default.focusedForeground" ),
			UIManager.getColor( "Button.default.hoverForeground" ),
			UIManager.getColor( "Button.default.pressedForeground" ) );
		rootPane.getContentPane().remove( b );
	}

	@Test
	void backgroundExplicit() {
		JButton b = new JButton();

		Color c = new Color( 0x020001 );
		b.setBackground( c );

		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			c,
			UIManager.getColor( "Button.disabledBackground" ),
			null,
			UIManager.getColor( "Button.hoverBackground" ),
			UIManager.getColor( "Button.pressedBackground" ) );

		// selected
		b.setSelected( true );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			UIManager.getColor( "Button.selectedBackground" ),
			UIManager.getColor( "Button.disabledSelectedBackground" ),
			null,
			null,
			UIManager.getColor( "Button.pressedBackground" ) );
		b.setSelected( false );

		// default
		JRootPane rootPane = new JRootPane();
		rootPane.getContentPane().add( b );
		rootPane.setDefaultButton( b );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			c,
			UIManager.getColor( "Button.disabledBackground" ),
			null,
			UIManager.getColor( "Button.default.hoverBackground" ),
			UIManager.getColor( "Button.default.pressedBackground" ) );
		rootPane.getContentPane().remove( b );
	}

	@Test
	void foregroundExplicit() {
		JButton b = new JButton();

		Color c = new Color( 0x020001 );
		b.setForeground( c );

		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			c,
			UIManager.getColor( "Button.disabledText" ),
			null,
			UIManager.getColor( "Button.hoverForeground" ),
			UIManager.getColor( "Button.pressedForeground" ) );

		// selected
		b.setSelected( true );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			c,
			FlatUIUtils.getUIColor( "Button.disabledSelectedForeground", "Button.disabledText" ),
			null,
			null,
			UIManager.getColor( "Button.pressedForeground" ) );
		b.setSelected( false );

		// default
		JRootPane rootPane = new JRootPane();
		rootPane.getContentPane().add( b );
		rootPane.setDefaultButton( b );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			c,
			UIManager.getColor( "Button.disabledText" ),
			null,
			UIManager.getColor( "Button.default.hoverForeground" ),
			UIManager.getColor( "Button.default.pressedForeground" ) );
		rootPane.getContentPane().remove( b );
	}

	@Test
	void backgroundStyled() {
		JButton b = new JButton();

		b.putClientProperty( FlatClientProperties.STYLE,
			"background: #020001;" +
			"disabledBackground: #020002;" +
			"focusedBackground: #020003;" +
			"hoverBackground: #020004;" +
			"pressedBackground: #020005;" +
			"selectedBackground: #020006;" +
			"disabledSelectedBackground: #020007;" );

		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			new Color( 0x020001 ),
			new Color( 0x020002 ),
			new Color( 0x020003 ),
			new Color( 0x020004 ),
			new Color( 0x020005 ) );

		// selected
		b.setSelected( true );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			new Color( 0x020006 ),
			new Color( 0x020007 ),
			null,
			null,
			new Color( 0x020005 ) );
		b.setSelected( false );


		Color c = new Color( 0x0a0001 );
		b.setBackground( c );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			c,
			new Color( 0x020002 ),
			c,
			new Color( 0x020004 ),
			new Color( 0x020005 ) );
		b.setSelected( true );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			new Color( 0x020006 ),
			new Color( 0x020007 ),
			null,
			null,
			new Color( 0x020005 ) );
		b.setSelected( false );


		b = new JButton();
		b.putClientProperty( FlatClientProperties.STYLE,
			"default.background: #020101;" +
			"disabledBackground: #020102;" +
			"default.focusedBackground: #020103;" +
			"default.hoverBackground: #020104;" +
			"default.pressedBackground: #020105;" );

		// default
		JRootPane rootPane = new JRootPane();
		rootPane.getContentPane().add( b );
		rootPane.setDefaultButton( b );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getBackground( b2 ),
			new Color( 0x020101 ),
			new Color( 0x020102 ),
			new Color( 0x020103 ),
			new Color( 0x020104 ),
			new Color( 0x020105 ) );
		rootPane.getContentPane().remove( b );
	}

	@Test
	void foregroundStyled() {
		JButton b = new JButton();

		b.putClientProperty( FlatClientProperties.STYLE,
			"foreground: #020001;" +
			"disabledText: #020002;" +
			"focusedForeground: #020003;" +
			"hoverForeground: #020004;" +
			"pressedForeground: #020005;" +
			"selectedForeground: #020006;" +
			"disabledSelectedForeground: #020007;" );

		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			new Color( 0x020001 ),
			new Color( 0x020002 ),
			new Color( 0x020003 ),
			new Color( 0x020004 ),
			new Color( 0x020005 ) );

		// selected
		b.setSelected( true );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			new Color( 0x020006 ),
			new Color( 0x020007 ),
			null,
			null,
			new Color( 0x020005 ) );
		b.setSelected( false );


		Color c = new Color( 0x0a0001 );
		b.setForeground( c );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			c,
			new Color( 0x020002 ),
			c,
			new Color( 0x020004 ),
			new Color( 0x020005 ) );
		b.setSelected( true );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			c,
			new Color( 0x020007 ),
			null,
			null,
			new Color( 0x020005 ) );
		b.setSelected( false );


		b = new JButton();
		b.putClientProperty( FlatClientProperties.STYLE,
			"default.foreground: #020101;" +
			"disabledText: #020102;" +
			"default.focusedForeground: #020103;" +
			"default.hoverForeground: #020104;" +
			"default.pressedForeground: #020105;" );

		// default
		JRootPane rootPane = new JRootPane();
		rootPane.getContentPane().add( b );
		rootPane.setDefaultButton( b );
		testButtonColors( b, b2 -> ((FlatButtonUI)b2.getUI()).getForeground( b2 ),
			new Color( 0x020101 ),
			new Color( 0x020102 ),
			new Color( 0x020103 ),
			new Color( 0x020104 ),
			new Color( 0x020105 ) );
		rootPane.getContentPane().remove( b );
	}

	private void testButtonColors( JButton b, Function<JButton, Color> f,
		Color expectedEnabled, Color expectedDisabled, Color expectedFocused,
		Color expectedHover, Color expectedPressed
		)
	{
		assertEquals( expectedEnabled, f.apply( b ) );

		// disabled
		b.setEnabled( false );
		assertEquals( expectedDisabled, f.apply( b ) );
		b.setEnabled( true );

		// focused
		if( expectedFocused != null ) {
			b.putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER, (Predicate<JComponent>) c -> true );
			assertEquals( expectedFocused, f.apply( b ) );
			b.putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER, null );
		}

		// hover
		if( expectedHover != null ) {
			b.getModel().setRollover( true );
			assertEquals( expectedHover, f.apply( b ) );
			b.getModel().setRollover( false );
		}

		// pressed
		if( expectedPressed != null ) {
			b.getModel().setPressed( true );
			assertEquals( expectedPressed, f.apply( b ) );
			b.getModel().setPressed( false );
		}
	}
}
