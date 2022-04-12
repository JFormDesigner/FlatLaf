/*
 * Copyright 2020 FormDev Software GmbH
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

package com.formdev.flatlaf.extras.components;

import static com.formdev.flatlaf.FlatClientProperties.*;
import javax.swing.JScrollBar;

/**
 * Subclass of {@link JScrollBar} that provides easy access to FlatLaf specific client properties.
 *
 * @author Karl Tauber
 */
public class FlatScrollBar
	extends JScrollBar
	implements FlatComponentExtension, FlatStyleableComponent
{
	/**
	 * Returns whether the decrease/increase arrow buttons of a scrollbar are shown.
	 */
	public boolean isShowButtons() {
		return getClientPropertyBoolean( SCROLL_BAR_SHOW_BUTTONS, "ScrollBar.showButtons" );
	}

	/**
	 * Specifies whether the decrease/increase arrow buttons of a scrollbar are shown.
	 */
	public void setShowButtons( boolean showButtons ) {
		putClientProperty( SCROLL_BAR_SHOW_BUTTONS, showButtons );
	}
}
