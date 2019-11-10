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

package com.formdev.flatlaf.ui;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicColorChooserUI;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JColorChooser}.
 *
 * <!-- BasicColorChooserUI -->
 *
 * @uiDefault ColorChooser.font							Font
 * @uiDefault ColorChooser.background					Color
 * @uiDefault ColorChooser.foreground					Color
 * @uiDefault ColorChooser.showPreviewPanelText			boolean
 * @uiDefault ColorChooser.swatchesSwatchSize			Dimension
 * @uiDefault ColorChooser.swatchesRecentSwatchSize		Dimension
 *
 * @author Karl Tauber
 */
public class FlatColorChooserUI
	extends BasicColorChooserUI
{
	public static ComponentUI createUI( JComponent c ) {
		return  new FlatColorChooserUI();
	}

	@Override
	public void installUI( JComponent c ) {
		if( UIScale.getUserScaleFactor() != 1f ) {
			// temporary scale swatch sizes
			Dimension swatchSize = UIManager.getDimension( "ColorChooser.swatchesSwatchSize" );
			Dimension swatchSize2 = UIManager.getDimension( "ColorChooser.swatchesRecentSwatchSize" );
			UIManager.put( "ColorChooser.swatchesSwatchSize", UIScale.scale( swatchSize ) );
			UIManager.put( "ColorChooser.swatchesRecentSwatchSize", UIScale.scale( swatchSize2 ) );

			super.installUI( c );

			UIManager.put( "ColorChooser.swatchesSwatchSize", null );
			UIManager.put( "ColorChooser.swatchesRecentSwatchSize", null );
		} else
			super.installUI( c );
	}
}
