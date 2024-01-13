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

package com.formdev.flatlaf.demo;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import com.formdev.flatlaf.util.UIScale;

/**
 * A panel that implements the {@link Scrollable} interface.
 *
 * @author Karl Tauber
 */
public class ScrollablePanel
	extends JPanel
	implements Scrollable
{
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension( getPreferredSize().width, UIScale.scale( 400 ) );
	}

	@Override
	public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
		return UIScale.scale( 50 );
	}

	@Override
	public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
		return (orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
}
