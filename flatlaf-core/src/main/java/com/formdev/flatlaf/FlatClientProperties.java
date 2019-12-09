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

package com.formdev.flatlaf;

import java.util.Objects;
import javax.swing.JComponent;

/**
 * @author Karl Tauber
 */
public interface FlatClientProperties
{
	/**
	 * Specifies type of a button.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JButton}<br>
	 * <strong>Value type</strong> {@link java.lang.String}<br>
	 * <strong>Allowed Values</strong> {@link BUTTON_TYPE_HELP}
	 */
	String BUTTON_TYPE = "JButton.buttonType";

	/**
	 * Paint a help button (circle with question mark).
	 *
	 * @see #BUTTON_TYPE
	 */
	String BUTTON_TYPE_HELP = "help";

	/**
	 * Specifies selected state of a checkbox.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JCheckBox}<br>
	 * <strong>Value type</strong> {@link java.lang.String}<br>
	 * <strong>Allowed Values</strong> {@link SELECTED_STATE_INDETERMINATE}
	 */
	String SELECTED_STATE = "JButton.selectedState";

	/**
	 * Paint an indeterminate state on a checkbox.
	 *
	 * @see #SELECTED_STATE
	 */
	String SELECTED_STATE_INDETERMINATE = "indeterminate";

	/**
	 * Specifies whether the decrease/increase arrow buttons of a scrollbar are shown.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JScrollBar} or {@link javax.swing.JScrollPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String SCROLL_BAR_SHOW_BUTTONS = "JScrollBar.showButtons";

	/**
	 * Specifies whether a full border is painted around a tabbed pane.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String TABBED_PANE_HAS_FULL_BORDER = "JTabbedPane.hasFullBorder";

	/**
	 * Checks whether a client property of a component has the given value.
	 */
	static boolean clientPropertyEquals( JComponent c, String key, Object value ) {
		return Objects.equals( c.getClientProperty( key ), value );
	}
}
