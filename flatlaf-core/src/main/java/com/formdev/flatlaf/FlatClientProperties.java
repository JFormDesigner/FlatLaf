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

import java.awt.Color;
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
	 * <strong>Components</strong> {@link javax.swing.JButton} and {@link javax.swing.JToggleButton}<br>
	 * <strong>Value type</strong> {@link java.lang.String}<br>
	 * <strong>Allowed Values</strong> {@link #BUTTON_TYPE_SQUARE}, {@link #BUTTON_TYPE_ROUND_RECT},
	 * {@link #BUTTON_TYPE_TAB}, {@link #BUTTON_TYPE_HELP} and {@link BUTTON_TYPE_TOOLBAR_BUTTON}
	 */
	String BUTTON_TYPE = "JButton.buttonType";

	/**
	 * Paint the button with square edges.
	 * <p>
	 * <strong>Components</strong> {@link javax.swing.JButton} and {@link javax.swing.JToggleButton}
	 *
	 * @see #BUTTON_TYPE
	 */
	String BUTTON_TYPE_SQUARE = "square";

	/**
	 * Paint the button with round edges.
	 * <p>
	 * <strong>Components</strong> {@link javax.swing.JButton} and {@link javax.swing.JToggleButton}
	 *
	 * @see #BUTTON_TYPE
	 */
	String BUTTON_TYPE_ROUND_RECT = "roundRect";

	/**
	 * Paint the toggle button in tab style.
	 * <p>
	 * <strong>Components</strong> {@link javax.swing.JToggleButton}
	 *
	 * @see #BUTTON_TYPE
	 */
	String BUTTON_TYPE_TAB = "tab";

	/**
	 * Paint a help button (circle with question mark).
	 * <p>
	 * <strong>Components</strong> {@link javax.swing.JButton}
	 *
	 * @see #BUTTON_TYPE
	 */
	String BUTTON_TYPE_HELP = "help";

	/**
	 * Paint the button in toolbar style.
	 * <p>
	 * <strong>Components</strong> {@link javax.swing.JButton} and {@link javax.swing.JToggleButton}
	 *
	 * @see #BUTTON_TYPE
	 */
	String BUTTON_TYPE_TOOLBAR_BUTTON = "toolBarButton";

	/**
	 * Specifies selected state of a checkbox.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JCheckBox}<br>
	 * <strong>Value type</strong> {@link java.lang.String}<br>
	 * <strong>Allowed Values</strong> {@link #SELECTED_STATE_INDETERMINATE}
	 */
	String SELECTED_STATE = "JButton.selectedState";

	/**
	 * Paint an indeterminate state on a checkbox.
	 *
	 * @see #SELECTED_STATE
	 */
	String SELECTED_STATE_INDETERMINATE = "indeterminate";

	/**
	 * Specifies minimum width of a component.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JButton}, {@link javax.swing.JToggleButton},
	 * {@link javax.swing.JComboBox}, {@link javax.swing.JSpinner} and {@link javax.swing.text.JTextComponent}<br>
	 * <strong>Value type</strong> {@link java.lang.Integer}<br>
	 */
	String MINIMUM_WIDTH = "JComponent.minimumWidth";

	/**
	 * Specifies minimum height of a component.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JButton} and {@link javax.swing.JToggleButton}<br>
	 * <strong>Value type</strong> {@link java.lang.Integer}<br>
	 */
	String MINIMUM_HEIGHT = "JComponent.minimumHeight";

	/**
	 * Specifies the outline color of the component border.
	 * <p>
	 * <strong>Components</strong> {@link javax.swing.JButton}, {@link javax.swing.JComboBox},
	 * {@link javax.swing.JFormattedTextField}, {@link javax.swing.JPasswordField},
	 * {@link javax.swing.JScrollPane}, {@link javax.swing.JSpinner},
	 * {@link javax.swing.JTextField} and {@link javax.swing.JToggleButton}<br>
	 * <strong>Value type</strong> {@link java.lang.String} or {@link java.awt.Color} or {@link java.awt.Color}[2]<br>
	 * <strong>Allowed Values</strong> {@link #OUTLINE_ERROR}, {@link #OUTLINE_WARNING},
	 * any color (type {@link java.awt.Color}) or an array of two colors (type {@link java.awt.Color}[2])
	 * where the first color is for focused state and the second for unfocused state
	 */
	String OUTLINE = "JComponent.outline";

	/**
	 * Paint the component border in another color (usually reddish) to indicate an error.
	 *
	 * @see #OUTLINE
	 */
	String OUTLINE_ERROR = "error";

	/**
	 * Paint the component border in another color (usually yellowish) to indicate a warning.
	 *
	 * @see #OUTLINE
	 */
	String OUTLINE_WARNING = "warning";

	/**
	 * Paint the component with round edges.
	 * <p>
	 * <strong>Components</strong> {@link javax.swing.JComboBox}, {@link javax.swing.JSpinner},
	 * {@link javax.swing.JTextField}, {@link javax.swing.JFormattedTextField} and {@link javax.swing.JPasswordField}
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String COMPONENT_ROUND_RECT = "JComponent.roundRect";

	/**
	 * Specifies whether a drop shadow is painted if the component is shown in a popup
	 * or if the component is the owner of another component that is shown in a popup.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JComponent}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String POPUP_DROP_SHADOW_PAINTED = "Popup.dropShadowPainted";

	/**
	 * Specifies whether the progress bar has always the larger height even if no string is painted.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JProgressBar}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String PROGRESS_BAR_LARGE_HEIGHT = "JProgressBar.largeHeight";

	/**
	 * Specifies whether the progress bar is paint with square edges.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JProgressBar}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String PROGRESS_BAR_SQUARE = "JProgressBar.square";

	/**
	 * Specifies whether the decrease/increase arrow buttons of a scrollbar are shown.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JScrollBar} or {@link javax.swing.JScrollPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String SCROLL_BAR_SHOW_BUTTONS = "JScrollBar.showButtons";

	/**
	 * Specifies whether separators are shown between tabs.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String TABBED_PANE_SHOW_TAB_SEPARATORS = "JTabbedPane.showTabSeparators";

	/**
	 * Specifies whether a full border is painted around a tabbed pane.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String TABBED_PANE_HAS_FULL_BORDER = "JTabbedPane.hasFullBorder";

	/**
	 * Specifies the height of a tab.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Integer}
	 */
	String TABBED_PANE_TAB_HEIGHT = "JTabbedPane.tabHeight";

	/**
	 * Specifies whether all text is selected when the text component gains focus.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTextField} (and subclasses)<br>
	 * <strong>Value type</strong> {@link java.lang.String}<br>
	 * <strong>Allowed Values</strong> {@link #SELECT_ALL_ON_FOCUS_POLICY_NEVER},
	 * {@link #SELECT_ALL_ON_FOCUS_POLICY_ONCE} (default) or
	 * {@link #SELECT_ALL_ON_FOCUS_POLICY_ALWAYS}
	 */
	String SELECT_ALL_ON_FOCUS_POLICY = "JTextField.selectAllOnFocusPolicy";

	/**
	 * Never select all text when the text component gains focus.
	 *
	 * @see #SELECT_ALL_ON_FOCUS_POLICY
	 */
	String SELECT_ALL_ON_FOCUS_POLICY_NEVER = "never";

	/**
	 * Select all text when the text component gains focus for the first time
	 * and selection was not modified (is at end of text).
	 * This is the default.
	 *
	 * @see #SELECT_ALL_ON_FOCUS_POLICY
	 */
	String SELECT_ALL_ON_FOCUS_POLICY_ONCE = "once";

	/**
	 * Always select all text when the text component gains focus.
	 *
	 * @see #SELECT_ALL_ON_FOCUS_POLICY
	 */
	String SELECT_ALL_ON_FOCUS_POLICY_ALWAYS = "always";

	/**
	 * Placeholder text that is only painted if the text field is empty.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTextField} (and subclasses) or {@link javax.swing.JComboBox}<br>
	 * <strong>Value type</strong> {@link java.lang.String}
	 */
	String PLACEHOLDER_TEXT = "JTextField.placeholderText";

	/**
	 * Height of underline if toggle button type is {@link #BUTTON_TYPE_TAB}.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JToggleButton}<br>
	 * <strong>Value type</strong> {@link java.lang.Integer}
	 */
	String TAB_BUTTON_UNDERLINE_HEIGHT = "JToggleButton.tab.underlineHeight";

	/**
	 * Color of underline if toggle button type is {@link #BUTTON_TYPE_TAB}.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JToggleButton}<br>
	 * <strong>Value type</strong> {@link java.awt.Color}
	 */
	String TAB_BUTTON_UNDERLINE_COLOR = "JToggleButton.tab.underlineColor";

	/**
	 * Background color if selected and toggle button type is {@link #BUTTON_TYPE_TAB}.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JToggleButton}<br>
	 * <strong>Value type</strong> {@link java.awt.Color}
	 */
	String TAB_BUTTON_SELECTED_BACKGROUND = "JToggleButton.tab.selectedBackground";

	/**
	 * Checks whether a client property of a component has the given value.
	 */
	static boolean clientPropertyEquals( JComponent c, String key, Object value ) {
		return Objects.equals( c.getClientProperty( key ), value );
	}

	/**
	 * Checks whether a client property of a component is a boolean and returns its value.
	 * If the client property is not set, or not a boolean, defaultValue is returned.
	 */
	static boolean clientPropertyBoolean( JComponent c, String key, boolean defaultValue ) {
		Object value = c.getClientProperty( key );
		return (value instanceof Boolean) ? (boolean) value : defaultValue;
	}

	/**
	 * Checks whether a client property of a component is an integer and returns its value.
	 * If the client property is not set, or not an integer, defaultValue is returned.
	 */
	static int clientPropertyInt( JComponent c, String key, int defaultValue ) {
		Object value = c.getClientProperty( key );
		return (value instanceof Integer) ? (int) value : defaultValue;
	}

	/**
	 * Checks whether a client property of a component is a color and returns its value.
	 * If the client property is not set, or not a color, defaultValue is returned.
	 */
	static Color clientPropertyColor( JComponent c, String key, Color defaultValue ) {
		Object value = c.getClientProperty( key );
		return (value instanceof Color) ? (Color) value : defaultValue;
	}

	static int clientPropertyChoice( JComponent c, String key, String... choices ) {
		Object value = c.getClientProperty( key );
		for( int i = 0; i < choices.length; i++ ) {
			if( choices[i].equals( value ) )
				return i;

		}
		return -1;
	}
}
