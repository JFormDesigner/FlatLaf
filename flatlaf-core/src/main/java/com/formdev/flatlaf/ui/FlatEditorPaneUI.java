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

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JEditorPane}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault Component.minimumWidth			int
 *
 * @author Karl Tauber
 */
public class FlatEditorPaneUI
	extends BasicEditorPaneUI
{
	protected int minimumWidth;

	private Object oldHonorDisplayProperties;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatEditorPaneUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		minimumWidth = UIManager.getInt( "Component.minimumWidth" );

		// use component font and foreground for HTML text
		oldHonorDisplayProperties = getComponent().getClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES );
		getComponent().putClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES, true );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		getComponent().putClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES, oldHonorDisplayProperties );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return applyMinimumWidth( super.getPreferredSize( c ) );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return applyMinimumWidth( super.getMinimumSize( c ) );
	}

	private Dimension applyMinimumWidth( Dimension size ) {
		// Assume that text area is in a scroll pane (that displays the border)
		// and subtract 1px border line width.
		// Using "(scale( 1 ) * 2)" instead of "scale( 2 )" to deal with rounding
		// issues. E.g. at scale factor 1.5 the first returns 4, but the second 3.
		size.width = Math.max( size.width, scale( minimumWidth ) - (scale( 1 ) * 2) );
		return size;
	}
}
