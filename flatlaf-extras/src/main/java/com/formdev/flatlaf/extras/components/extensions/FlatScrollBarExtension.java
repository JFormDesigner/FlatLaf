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

package com.formdev.flatlaf.extras.components.extensions;

import static com.formdev.flatlaf.FlatClientProperties.*;
import javax.swing.JScrollBar;
import com.formdev.flatlaf.extras.components.FlatScrollBar;

/**
 * Extension interface for {@link JScrollBar} that provides
 * easy access to FlatLaf specific client properties.
 * <p>
 * Use this interface if you already have a subclass of {@link JScrollBar}
 * in your project and want add access to FlatLaf features to your class.
 * Otherwise use {@link FlatScrollBar}.
 *
 * @author Karl Tauber
 */
public interface FlatScrollBarExtension
	extends FlatComponentExtension
{
	/**
	 * Returns whether the decrease/increase arrow buttons of a scrollbar are shown.
	 */
	default boolean isShowButtons() {
		return getClientPropertyBoolean( SCROLL_BAR_SHOW_BUTTONS, "ScrollBar.showButtons" );
	}

	/**
	 * Specifies whether the decrease/increase arrow buttons of a scrollbar are shown.
	 */
	default void setShowButtons( boolean showButtons ) {
		putClientProperty( SCROLL_BAR_SHOW_BUTTONS, showButtons );
	}
}
