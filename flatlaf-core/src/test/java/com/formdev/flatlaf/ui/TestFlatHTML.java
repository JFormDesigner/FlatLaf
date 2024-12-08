/*
 * Copyright 2024 FormDev Software GmbH
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.View;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Karl Tauber
 */
public class TestFlatHTML
{
	private final String body = "some <small>small</small> text";
	private final String bodyInBody = "<body>" + body + "</body>";
	private final String bodyPlain = "some small text";

	@BeforeAll
	static void setup() {
		TestUtils.setup( false );
		TestUtils.scaleFont( 2 );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
	}

	@Test
	void simple() {
		testHtmlBaseSize( "<html>${BASE_SIZE_IN_HEAD}" + body + "</html>", bodyPlain );
		testHtmlBaseSize( "<html>${BASE_SIZE_IN_HEAD}" + bodyInBody + "</html>", bodyPlain );
	}

	@Test
	void htmlWithHeadTag() {
		testHtmlBaseSize( "<html><head>${BASE_SIZE}<title>test</title><head>" + body + "</html>", bodyPlain );
		testHtmlBaseSize( "<html><head>${BASE_SIZE}<title>test</title><head>" + bodyInBody + "</html>", bodyPlain );

		testHtmlBaseSize( "<html><head id=\"abc\">${BASE_SIZE}<title>test</title><head>" + body + "</html>", bodyPlain );
		testHtmlBaseSize( "<html><head id=\"abc\">${BASE_SIZE}<title>test</title><head>" + bodyInBody + "</html>", bodyPlain );
	}

	@Test
	void htmlWithStyleTag() {
		testHtmlBaseSize( "<html>${BASE_SIZE}<style>body { color: #f00; }</style>" + bodyInBody + "</html>", bodyPlain );
		testHtmlBaseSize( "<html>${BASE_SIZE}<style>body { color: #f00; }</style><h1>header1</h1>" + body + "</html>", "header1\n" + bodyPlain );

		testHtmlBaseSize( "<html>${BASE_SIZE}<style type='text/css'>body { color: #f00; }</style>" + bodyInBody + "</html>", bodyPlain );
		testHtmlBaseSize( "<html>${BASE_SIZE}<style type='text/css'>body { color: #f00; }</style><h1>header1</h1>" + body + "</html>", "header1\n" + bodyPlain );
	}

	@Test
	void htmlOnComponentWithNullFont() {
		assertDoesNotThrow( () -> {
			JLabel label = new JLabel();
			label.setFont( null );
			label.setText( "<html>foo<br>bar</html>" );
		} );
	}

	private void testHtmlBaseSize( String html, String expectedPlain ) {
		testHtmlBaseSizeImpl( html, expectedPlain );
		testHtmlBaseSizeImpl( html.toUpperCase( Locale.ENGLISH ), expectedPlain.toUpperCase( Locale.ENGLISH ) );
	}

	private void testHtmlBaseSizeImpl( String html, String expectedPlain ) {
		String baseSize = "<style>BASE_SIZE " + UIManager.getFont( "Label.font" ).getSize() + "</style>";
		String baseSizeInHead = "<head>" + baseSize + "</head>";

		String expectedHtml = html.replace( "${BASE_SIZE}", baseSize ).replace( "${BASE_SIZE_IN_HEAD}", baseSizeInHead );
		html = html.replace( "${BASE_SIZE}", "" ).replace( "${BASE_SIZE_IN_HEAD}", "" );

		testHtml( html, expectedHtml, expectedPlain );
	}

	private void testHtml( String html, String expectedHtml, String expectedPlain ) {
		FlatHTML.testUpdateRenderer = (c, newHtml) -> {
			assertEquals( expectedHtml, newHtml );
			assertEquals( expectedPlain, getPlainText( c ) );
		};
		new JLabel( html );
		FlatHTML.testUpdateRenderer = null;
	}

	private String getPlainText( JComponent c ) {
		View view = (View) c.getClientProperty( BasicHTML.propertyKey );
		if( view == null )
			return null;

		Document doc = view.getDocument();
		try {
			return doc.getText( 0, doc.getLength() ).trim();
		} catch( BadLocationException ex ) {
			ex.printStackTrace();
			return null;
		}
	}
}
