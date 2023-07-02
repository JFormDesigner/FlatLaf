/*
 * Copyright 2023 FormDev Software GmbH
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
import javax.swing.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import com.formdev.flatlaf.FlatClientProperties;

/**
 * @author Karl Tauber
 */
public class TestFlatStylingScale
{
	private static final float[] FACTORS = { 1f, 1.5f, 2f };

	@BeforeAll
	static void setup() {
		TestUtils.setup( false );
	}

	@AfterAll
	static void cleanup() {
		TestUtils.cleanup();
	}

	static float[] factors() {
		return FACTORS;
	}

	@ParameterizedTest
	@MethodSource( "factors" )
	void button( float factor ) {
		abstractButton( factor, new JButton() );
	}

	@ParameterizedTest
	@MethodSource( "factors" )
	void checkBox( float factor ) {
		abstractButton( factor, new JCheckBox() );
	}

	@ParameterizedTest
	@MethodSource( "factors" )
	void radioButton( float factor ) {
		abstractButton( factor, new JRadioButton() );
	}

	private void abstractButton( float factor, AbstractButton button ) {
		TestUtils.scaleFont( factor );

		button.putClientProperty( FlatClientProperties.STYLE, "iconTextGap: 100" );
		assertEquals( (int) (100 * factor), button.getIconTextGap() );

		TestUtils.resetFont();
	}

	@ParameterizedTest
	@MethodSource( "factors" )
	void tabbedPane( float factor ) {
		TestUtils.scaleFont( factor );


		JTabbedPane tabbedPane = new JTabbedPane();
		FlatTabbedPaneUI ui = (FlatTabbedPaneUI) tabbedPane.getUI();
		tabbedPane.putClientProperty( FlatClientProperties.STYLE, "textIconGap: 100" );
		assertEquals( 100, ui.getStyleableValue( tabbedPane, "textIconGap" ) );


		TestUtils.resetFont();
	}

	@ParameterizedTest
	@MethodSource( "factors" )
	void table( float factor ) {
		TestUtils.scaleFont( factor );


		JTable table = new JTable();
		table.putClientProperty( FlatClientProperties.STYLE, "rowHeight: 100" );
		assertEquals( (int) (100 * factor), table.getRowHeight() );


		TestUtils.resetFont();
	}

	@ParameterizedTest
	@MethodSource( "factors" )
	void tree( float factor ) {
		TestUtils.scaleFont( factor );


		JTree tree = new JTree();
		tree.putClientProperty( FlatClientProperties.STYLE, "rowHeight: 10" );
		assertEquals( 10 * factor, tree.getRowHeight() );


		TestUtils.resetFont();
	}
}
