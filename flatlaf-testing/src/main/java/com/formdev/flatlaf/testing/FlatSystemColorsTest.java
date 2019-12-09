/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.testing;

import java.awt.Color;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatSystemColorsTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatSystemColorsTest" );
			frame.showFrame( FlatSystemColorsTest::new );
		} );
	}

	FlatSystemColorsTest() {
		initComponents();

		String[] systemColors = {
			"desktop",
			"activeCaption",
			"activeCaptionText",
			"activeCaptionBorder",
			"inactiveCaption",
			"inactiveCaptionText",
			"inactiveCaptionBorder",
			"window",
			"windowBorder",
			"windowText",
			"menu",
			"menuText",
			"text",
			"textText",
			"textHighlight",
			"textHighlightText",
			"textInactiveText",
			"control",
			"controlText",
			"controlHighlight",
			"controlLtHighlight",
			"controlShadow",
			"controlDkShadow",
			"scrollbar",
			"info",
			"infoText",
		};

		for( String systemColor : systemColors ) {
			systemColorsPanel.add( new JLabel( systemColor ) );
			systemColorsPanel.add( new Preview( systemColor ), "wrap" );
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		systemColorsPanel = new JPanel();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[fill]",
			// rows
			"[grow,fill]"));

		//======== systemColorsPanel ========
		{
			systemColorsPanel.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]"));
		}
		add(systemColorsPanel, "cell 0 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel systemColorsPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class Preview ----

	private static class Preview
		extends JPanel
	{
		private final String colorKey;
		private final JPanel colorPreview;
		private final JLabel colorCode;

		Preview( String colorKey ) {
			super( new MigLayout( "ltr,insets 0", "[50,fill][]", "[fill]0" ) );

			this.colorKey = colorKey;

			colorPreview = new JPanel();
			colorPreview.setOpaque( true );
			colorCode = new JLabel();

			add( colorPreview );
			add( colorCode, "wrap" );

			update();
		}

		@Override
		public void updateUI() {
			super.updateUI();
			update();
		}

		private void update() {
			if( colorKey == null )
				return; // called from constructor

			Color color = UIManager.getColor( colorKey );
			colorPreview.setBackground( (color != null) ? new Color( color.getRGB(), true ) : null );
			colorCode.setText( (color != null) ? String.format( "#%06x", color.getRGB() & 0xffffff ) : "-" );
		}
	}
}
