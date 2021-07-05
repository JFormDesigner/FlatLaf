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

import java.awt.Font;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatSystemProperties;

/**
 * @author Karl Tauber
 */
public class TestUtils
{
	public static final float[] FACTORS = new float[] { 1f, 1.25f, 1.5f, 1.75f, 2f, 2.25f, 2.5f, 2.75f, 3f, 3.25f, 3.5f, 3.75f, 4f, 5f, 6f };

	public static void setup( boolean withFocus ) {
		System.setProperty( FlatSystemProperties.UI_SCALE, "1x" );
		if( withFocus )
			FlatIntelliJLaf.setup();
		else
			FlatLightLaf.setup();
		System.clearProperty( FlatSystemProperties.UI_SCALE );
	}

	public static void cleanup() {
		UIManager.put( "defaultFont", null );
	}

	public static void scaleFont( float factor ) {
		Font defaultFont = UIManager.getLookAndFeelDefaults().getFont( "defaultFont" );
		UIManager.put( "defaultFont", defaultFont.deriveFont( (float) Math.round( defaultFont.getSize() * factor ) ) );
	}

	public static void resetFont() {
		UIManager.put( "defaultFont", null );
	}
}
