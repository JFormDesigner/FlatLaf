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
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JScrollBar}.
 *
 * @author Karl Tauber
 */
public class FlatScrollBarUI
	extends BasicScrollBarUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatScrollBarUI();
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return UIScale.scale( super.getPreferredSize( c ) );
	}

	@Override
	protected JButton createDecreaseButton( int orientation ) {
		return createInvisibleButton();
	}

	@Override
	protected JButton createIncreaseButton( int orientation ) {
		return createInvisibleButton();
	}

	private JButton createInvisibleButton() {
		JButton button = new JButton();
		button.setMinimumSize( new Dimension() );
		button.setMaximumSize( new Dimension() );
		button.setPreferredSize( new Dimension() );
		button.setFocusable( false );
		button.setRequestFocusEnabled( false );
		return button;
	}

	@Override
	protected void paintDecreaseHighlight( Graphics g ) {
		// do not paint
	}

	@Override
	protected void paintIncreaseHighlight( Graphics g ) {
		// do not paint
	}

	@Override
	protected void paintThumb( Graphics g, JComponent c, Rectangle thumbBounds ) {
		if( thumbBounds.isEmpty() || !scrollbar.isEnabled() )
			return;

		g.setColor( thumbColor );
		g.fillRect( thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height );
	}

	@Override
	protected Dimension getMinimumThumbSize() {
		return UIScale.scale( super.getMinimumThumbSize() );
	}

	@Override
	protected Dimension getMaximumThumbSize() {
		return UIScale.scale( super.getMaximumThumbSize() );
	}
}
