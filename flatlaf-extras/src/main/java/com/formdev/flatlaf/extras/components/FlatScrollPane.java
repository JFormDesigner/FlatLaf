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
import java.awt.Color;
import javax.swing.JScrollPane;

/**
 * Subclass of {@link JScrollPane} that provides easy access to FlatLaf specific client properties.
 *
 * @author Karl Tauber
 */
public class FlatScrollPane
	extends JScrollPane
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


	/**
	 * Returns whether the scroll pane uses smooth scrolling.
	 */
	public boolean isSmoothScrolling() {
		return getClientPropertyBoolean( SCROLL_PANE_SMOOTH_SCROLLING, "ScrollPane.smoothScrolling" );
	}

	/**
	 * Specifies whether the scroll pane uses smooth scrolling.
	 */
	public void setSmoothScrolling( boolean smoothScrolling ) {
		putClientProperty( SCROLL_PANE_SMOOTH_SCROLLING, smoothScrolling );
	}


	/**
	 * Returns the outline color of the component border.
	 */
	public Object getOutline() {
		return getClientProperty( OUTLINE );
	}

	/**
	 * Specifies the outline color of the component border.
	 * <p>
	 * Allowed Values are:
	 * <ul>
	 *     <li>{@code null}
	 *     <li>string {@code "error"}
	 *     <li>string {@code "warning"}
	 *     <li>any color (type {@link Color})
	 *     <li>an array of two colors (type {@link Color}[2]) where the first color
	 *         is for focused state and the second for unfocused state
	 * </ul>
	 */
	public void setOutline( Object outline ) {
		putClientProperty( OUTLINE, outline );
	}
}
