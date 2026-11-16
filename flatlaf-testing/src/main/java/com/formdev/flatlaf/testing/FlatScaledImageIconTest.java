/*
 * Copyright 2026 FormDev Software
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

package com.formdev.flatlaf.testing;

import java.awt.EventQueue;
import javax.swing.*;
import com.formdev.flatlaf.util.ScaledImageIcon;
import net.miginfocom.swing.MigLayout;

/**
 * @author Karl Tauber
 */
public class FlatScaledImageIconTest
	extends FlatSingleComponentTest
{
	public static void main( String[] args ) {
		EventQueue.invokeLater( () -> {
			launch( FlatScaledImageIconTest::new, args );
		} );
	}

	@Override
	protected JComponent createSingleComponent() {
		JPanel panel = new JPanel( new MigLayout() );

		String[] iconNames = {
			"test16.png",
			"test24.png",
			"test32.png",
			"test48.png",

			"animated-spinner16.gif",
			"animated-spinner24.gif",
			"animated-spinner32.gif",
			"animated-spinner48.gif",
		};

		for( String iconName : iconNames ) {
			ImageIcon imageIcon = new ImageIcon( getClass().getResource( "/com/formdev/flatlaf/testing/" + iconName ) );
			ScaledImageIcon icon = new ScaledImageIcon( imageIcon );

			panel.add( new JLabel( imageIcon ), "newline" );
			panel.add( new JButton( imageIcon ) );
			panel.add( new JLabel( icon ) );
			panel.add( new JButton( icon ) );
			panel.add( new JButton( new ScaledImageIcon( imageIcon ) ) );
		}

		return panel;
	}
}
