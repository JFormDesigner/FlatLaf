/*
 * Copyright 2026 FormDev Software GmbH
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

package com.formdev.flatlaf.icons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Predicate;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatRadioButtonUI;
import com.formdev.flatlaf.ui.TestUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestFlatCheckBoxIcon
{
	@BeforeEach
	void setup() {
		TestUtils.setup( false );
		UIManager.put( "CheckBox.icon.style", "filled" );
		UIManager.put( "RadioButton.icon.style", "filled" );
		UIManager.put( "CheckBox.icon[filled].selectedBackground", Color.blue );
		UIManager.put( "CheckBox.icon.disabledBackground", new Color( 0, 0, 40, 26 ) );
	}

	@AfterEach
	void cleanup() {
		for( String key : new String[] { "CheckBox.icon.style", "RadioButton.icon.style",
			"CheckBox.icon[filled].selectedBackground", "CheckBox.icon.disabledBackground",
			"CheckBox.icon.backgroundOpaque", "CheckBox.icon.borderWidth" } )
		{
			UIManager.put( key, null );
		}
		TestUtils.cleanup();
	}

	@Test
	void translucentDisabledBackground() {
		for( boolean radio : new boolean[] { false, true } ) {
			for( int borderWidth : new int[] { 0, 1 } ) {
				UIManager.put( "CheckBox.icon.borderWidth", borderWidth );
				for( Boolean opaque : new Boolean[] { null, true, false } ) {
					UIManager.put( "CheckBox.icon.backgroundOpaque", opaque );
					FlatCheckBoxIcon icon = radio ? new FlatRadioButtonIcon() : new FlatCheckBoxIcon();
					AbstractButton button = radio ? new JRadioButton() : new JCheckBox();
					button.setEnabled( false );
					button.setSelected( true );
					for( boolean indeterminate : new boolean[] { false, true } ) {
						button.putClientProperty( FlatClientProperties.SELECTED_STATE,
							indeterminate ? FlatClientProperties.SELECTED_STATE_INDETERMINATE : null );
						if( Boolean.FALSE.equals( opaque ) ) {
							assertBackground( icon, button, Color.white, new Color( 229, 229, 233 ) );
							assertBackground( icon, button, new Color( 120, 180, 210 ), new Color( 108, 162, 193 ) );
						} else
							assertBackground( icon, button, Color.white, new Color( 0, 0, 233 ) );
					}
				}
			}
		}
	}

	@Test
	void transparentFocusedBackgroundStyle() {
		for( boolean radio : new boolean[] { false, true } ) {
			AbstractButton button = radio ? new JRadioButton() : new JCheckBox();
			button.putClientProperty( FlatClientProperties.STYLE,
				"icon.backgroundOpaque: false; icon.focusWidth: 2; "
				+ "icon.focusedBackground: #0000281a; icon.focusedSelectedBackground: #0000281a" );
			button.putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER, (Predicate<JComponent>) c -> true );
			FlatCheckBoxIcon icon = (FlatCheckBoxIcon) ((FlatRadioButtonUI)button.getUI()).getDefaultIcon();
			for( boolean selected : new boolean[] { false, true } ) {
				button.setSelected( selected );
				assertBackground( icon, button, Color.white, new Color( 229, 229, 233 ) );
				assertBackground( icon, button, new Color( 120, 180, 210 ), new Color( 108, 162, 193 ) );
			}
			button.putClientProperty( FlatClientProperties.STYLE,
				"icon.backgroundOpaque: false; icon.focusWidth: 2; icon.focusedSelectedBackground: #0000" );
			icon = (FlatCheckBoxIcon) ((FlatRadioButtonUI)button.getUI()).getDefaultIcon();
			assertBackground( icon, button, Color.white, Color.white );
		}
	}

	private void assertBackground( FlatCheckBoxIcon icon, AbstractButton button, Color background, Color expected ) {
		BufferedImage image = new BufferedImage( 20, 20, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = image.createGraphics();
		try {
			g.setColor( background );
			g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
			icon.paintIcon( button, g, 0, 0 );
		} finally {
			g.dispose();
		}
		assertEquals( expected.getRGB(), image.getRGB( 7, 3 ),
			icon.getClass().getSimpleName() + " on " + background );
	}
}
