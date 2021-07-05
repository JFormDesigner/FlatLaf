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
import static com.formdev.flatlaf.FlatClientProperties.STYLE;
import java.util.function.Supplier;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Karl Tauber
 */
public class TestFlatTextComponents
{
	@BeforeAll
	static void setup() {
		TestUtils.setup( false );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
	}

	@Test
	void editorPane_updateBackground() {
		textComponent_updateBackground( "EditorPane", JEditorPane::new );
	}

	@Test
	void formattedTextField_updateBackground() {
		textComponent_updateBackground( "FormattedTextField", JFormattedTextField::new );
	}

	@Test
	void passwordField_updateBackground() {
		textComponent_updateBackground( "PasswordField", JPasswordField::new );
	}

	@Test
	void textArea_updateBackground() {
		textComponent_updateBackground( "TextArea", JTextArea::new );
	}

	@Test
	void textField_updateBackground() {
		textComponent_updateBackground( "TextField", JTextField::new );
	}

	@Test
	void textPane_updateBackground() {
		textComponent_updateBackground( "TextPane", JTextPane::new );
	}

	@Test
	void basicTextField_updateBackground() {
		textComponent_updateBackground( "TextField", () -> {
			JTextField c = new JTextField();
			c.setUI( new BasicTextFieldUI() );
			return c;
		} );
	}

	private void textComponent_updateBackground( String prefix, Supplier<JTextComponent> createTextComponent ) {
		ColorUIResource background = new ColorUIResource( 0xff0000 );
		ColorUIResource inactiveBackground = new ColorUIResource( 0x00ff00 );
		ColorUIResource disabledBackground = new ColorUIResource( 0x0000ff );

		UIManager.put( prefix + ".background", background );
		UIManager.put( prefix + ".inactiveBackground", inactiveBackground );
		UIManager.put( prefix + ".disabledBackground", disabledBackground );

		JTextComponent c = createTextComponent.get();

		// without styling
		assertEquals( background, c.getBackground() );
		c.setEditable( false ); assertEquals( inactiveBackground, c.getBackground() );
		c.setEnabled( false );  assertEquals( disabledBackground, c.getBackground() );
		c.setEditable( true );  assertEquals( disabledBackground, c.getBackground() );
		c.setEnabled( true );   assertEquals( background, c.getBackground() );


		if( !c.getUI().getClass().getSimpleName().startsWith( "Flat" ) )
			return;


		// with styling

		ColorUIResource inactiveBackground1 = new ColorUIResource( 0x00ee00 );
		ColorUIResource disabledBackground1 = new ColorUIResource( 0x0000ee );
		ColorUIResource inactiveBackground2 = new ColorUIResource( 0x00dd00 );
		ColorUIResource disabledBackground2 = new ColorUIResource( 0x0000dd );
		String style1 = "inactiveBackground: #00ee00; disabledBackground: #0000ee";
		String style2 = "inactiveBackground: #00dd00; disabledBackground: #0000dd";

		c.putClientProperty( STYLE, style1 );

		assertEquals( background, c.getBackground() );
		c.setEditable( false ); assertEquals( inactiveBackground1, c.getBackground() );
		c.setEnabled( false );  assertEquals( disabledBackground1, c.getBackground() );
		c.setEditable( true );  assertEquals( disabledBackground1, c.getBackground() );
		c.setEnabled( true );   assertEquals( background, c.getBackground() );

		c.putClientProperty( STYLE, null );
		assertEquals( background, c.getBackground() );

		c.setEditable( false );
		c.putClientProperty( STYLE, style1 );
		assertEquals( inactiveBackground1, c.getBackground() );

		c.putClientProperty( STYLE, null );
		assertEquals( inactiveBackground, c.getBackground() );

		c.setEnabled( false );
		c.putClientProperty( STYLE, style1 );
		assertEquals( disabledBackground1, c.getBackground() );


		// change from style1 to style2
		c.putClientProperty( STYLE, style2 );
		assertEquals( disabledBackground2, c.getBackground() );

		c.setEnabled( true );
		assertEquals( inactiveBackground2, c.getBackground() );


		// remove style
		c.putClientProperty( STYLE, null );
		assertEquals( inactiveBackground, c.getBackground() );
	}
}
