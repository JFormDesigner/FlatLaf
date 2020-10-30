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
import javax.swing.SwingConstants;

/**
 * @author Karl Tauber
 */
public interface FlatClientProperties
{
	//---- JButton ------------------------------------------------------------

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
	 * Specifies whether the button preferred size will be made square (quadratically).
	 * <p>
	 * <strong>Components</strong> {@link javax.swing.JButton} and {@link javax.swing.JToggleButton}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String SQUARE_SIZE = "JButton.squareSize";

	//---- JComponent ---------------------------------------------------------

	/**
	 * Specifies minimum width of a component.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JButton}, {@link javax.swing.JToggleButton},
	 * {@link javax.swing.JComboBox}, {@link javax.swing.JSpinner} and {@link javax.swing.text.JTextComponent}<br>
	 * <strong>Value type</strong> {@link java.lang.Integer}
	 */
	String MINIMUM_WIDTH = "JComponent.minimumWidth";

	/**
	 * Specifies minimum height of a component.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JButton} and {@link javax.swing.JToggleButton}<br>
	 * <strong>Value type</strong> {@link java.lang.Integer}
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
	 * {@link javax.swing.JTextField}, {@link javax.swing.JFormattedTextField} and {@link javax.swing.JPasswordField}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String COMPONENT_ROUND_RECT = "JComponent.roundRect";

	//---- Popup --------------------------------------------------------------

	/**
	 * Specifies whether a drop shadow is painted if the component is shown in a popup
	 * or if the component is the owner of another component that is shown in a popup.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JComponent}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String POPUP_DROP_SHADOW_PAINTED = "Popup.dropShadowPainted";

	//---- JProgressBar -------------------------------------------------------

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

	//---- JRootPane ----------------------------------------------------------

	/**
	 * Specifies whether the menu bar is embedded into the title pane if custom
	 * window decorations are enabled. Default is {@code true}.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JRootPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String MENU_BAR_EMBEDDED = "JRootPane.menuBarEmbedded";

	//---- JScrollBar ---------------------------------------------------------

	/**
	 * Specifies whether the decrease/increase arrow buttons of a scrollbar are shown.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JScrollBar} or {@link javax.swing.JScrollPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String SCROLL_BAR_SHOW_BUTTONS = "JScrollBar.showButtons";

	/**
	 * Specifies whether the scroll pane uses smooth scrolling.
	 * <p>
	 * <strong>Component</strong> {{@link javax.swing.JScrollPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String SCROLL_PANE_SMOOTH_SCROLLING = "JScrollPane.smoothScrolling";

	//---- JTabbedPane --------------------------------------------------------

	/**
	 * Specifies whether separators are shown between tabs.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String TABBED_PANE_SHOW_TAB_SEPARATORS = "JTabbedPane.showTabSeparators";

	/**
	 * Specifies whether the separator between tabs area and content area should be shown.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String TABBED_PANE_SHOW_CONTENT_SEPARATOR = "JTabbedPane.showContentSeparator";

	/**
	 * Specifies whether a full border is painted around a tabbed pane.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 */
	String TABBED_PANE_HAS_FULL_BORDER = "JTabbedPane.hasFullBorder";

	/**
	 * Specifies the minimum width of a tab.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}
	 * or tab content components (see {@link javax.swing.JTabbedPane#setComponentAt(int, java.awt.Component)})<br>
	 * <strong>Value type</strong> {@link java.lang.Integer}
	 */
	String TABBED_PANE_MINIMUM_TAB_WIDTH = "JTabbedPane.minimumTabWidth";

	/**
	 * Specifies the maximum width of a tab.
	 * <p>
	 * Applied only if tab does not have a custom tab component
	 * (see {@link javax.swing.JTabbedPane#setTabComponentAt(int, java.awt.Component)}).
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}
	 * or tab content components (see {@link javax.swing.JTabbedPane#setComponentAt(int, java.awt.Component)})<br>
	 * <strong>Value type</strong> {@link java.lang.Integer}
	 */
	String TABBED_PANE_MAXIMUM_TAB_WIDTH = "JTabbedPane.maximumTabWidth";

	/**
	 * Specifies the height of a tab.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Integer}
	 */
	String TABBED_PANE_TAB_HEIGHT = "JTabbedPane.tabHeight";

	/**
	 * Specifies the insets of a tab.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}
	 * or tab content components (see {@link javax.swing.JTabbedPane#setComponentAt(int, java.awt.Component)})<br>
	 * <strong>Value type</strong> {@link java.awt.Insets}
	 */
	String TABBED_PANE_TAB_INSETS = "JTabbedPane.tabInsets";

	/**
	 * Specifies the insets of the tab area.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.awt.Insets}
	 */
	String TABBED_PANE_TAB_AREA_INSETS = "JTabbedPane.tabAreaInsets";

	/**
	 * Specifies whether tabs are closable.
	 * If set to {@code true} on a tabbed pane component, all tabs in that tabbed pane are closable.
	 * To make individual tabs closable, set it to {@code true} on a tab content component.
	 * <p>
	 * Note that you have to specify a callback (see {@link #TABBED_PANE_TAB_CLOSABLE})
	 * that is invoked when the user clicks a tab close button.
	 * The callback is responsible for closing the tab.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}
	 * or tab content components (see {@link javax.swing.JTabbedPane#setComponentAt(int, java.awt.Component)})<br>
	 * <strong>Value type</strong> {@link java.lang.Boolean}
	 *
	 * @see #TABBED_PANE_TAB_CLOSE_CALLBACK
	 */
	String TABBED_PANE_TAB_CLOSABLE = "JTabbedPane.tabClosable";

	/**
	 * Specifies the tooltip text used for tab close buttons.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}
	 * or tab content components (see {@link javax.swing.JTabbedPane#setComponentAt(int, java.awt.Component)})<br>
	 * <strong>Value type</strong> {@link java.lang.String}
	 */
	String TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT = "JTabbedPane.tabCloseToolTipText";

	/**
	 * Specifies the callback that is invoked when a tab close button is clicked.
	 * The callback is responsible for closing the tab.
	 * <p>
	 * Either use a {@link java.util.function.IntConsumer} that received the tab index as parameter:
	 * <pre>{@code
	 * myTabbedPane.putClientProperty( "JTabbedPane.tabCloseCallback",
	 *     (IntConsumer) tabIndex -> {
	 *         // close tab here
	 *     } );
	 * }</pre>
	 * Or use a {@link java.util.function.BiConsumer}&lt;javax.swing.JTabbedPane, Integer&gt;
	 * that received the tabbed pane and the tab index as parameters:
	 * <pre>{@code
	 * myTabbedPane.putClientProperty( "JTabbedPane.tabCloseCallback",
	 *     (BiConsumer<JTabbedPane, Integer>) (tabbedPane, tabIndex) -> {
	 *         // close tab here
	 *     } );
	 * }</pre>
	 * If you need to check whether a modifier key (e.g. Alt or Shift) was pressed
	 * while the user clicked the tab close button, use {@link java.awt.EventQueue#getCurrentEvent}
	 * to get current event, check whether it is a {@link java.awt.event.MouseEvent}
	 * and invoke its methods. E.g.
	 * <pre>{@code
	 * AWTEvent e = EventQueue.getCurrentEvent();
	 * boolean shift = (e instanceof MouseEvent) ? ((MouseEvent)e).isShiftDown() : false;
	 * }</pre>
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}
	 * or tab content components (see {@link javax.swing.JTabbedPane#setComponentAt(int, java.awt.Component)})<br>
	 * <strong>Value type</strong> {@link java.util.function.IntConsumer}
	 * or {@link java.util.function.BiConsumer}&lt;javax.swing.JTabbedPane, Integer&gt;
	 *
	 * @see #TABBED_PANE_TAB_CLOSABLE
	 */
	String TABBED_PANE_TAB_CLOSE_CALLBACK = "JTabbedPane.tabCloseCallback";

	/**
	 * Specifies how to navigate to hidden tabs.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.String}<br>
	 * <strong>Allowed Values</strong> {@link #TABBED_PANE_HIDDEN_TABS_NAVIGATION_MORE_TABS_BUTTON}
	 * or {@link #TABBED_PANE_HIDDEN_TABS_NAVIGATION_ARROW_BUTTONS}
	 */
	String TABBED_PANE_HIDDEN_TABS_NAVIGATION = "JTabbedPane.hiddenTabsNavigation";

	/**
	 * Use "more tabs" button for navigation to hidden tabs.
	 *
	 * @see #TABBED_PANE_HIDDEN_TABS_NAVIGATION
	 */
	String TABBED_PANE_HIDDEN_TABS_NAVIGATION_MORE_TABS_BUTTON = "moreTabsButton";

	/**
	 * Use forward/backward buttons for navigation to hidden tabs.
	 *
	 * @see #TABBED_PANE_HIDDEN_TABS_NAVIGATION
	 */
	String TABBED_PANE_HIDDEN_TABS_NAVIGATION_ARROW_BUTTONS = "arrowButtons";

	/**
	 * Specifies the alignment of the tab area.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.String}<br>
	 * <strong>Allowed Values</strong> {@link #TABBED_PANE_TAB_AREA_ALIGN_LEADING} (default),
	 * {@link #TABBED_PANE_TAB_AREA_ALIGN_TRAILING}, {@link #TABBED_PANE_TAB_AREA_ALIGN_CENTER}
	 * or {@link #TABBED_PANE_TAB_AREA_ALIGN_FILL}
	 */
	String TABBED_PANE_TAB_AREA_ALIGNMENT = "JTabbedPane.tabAreaAlignment";

	/**
	 * Align the tab area to the leading edge.
	 *
	 * @see #TABBED_PANE_TAB_AREA_ALIGNMENT
	 */
	String TABBED_PANE_TAB_AREA_ALIGN_LEADING = "leading";

	/**
	 * Align the tab area to the trailing edge.
	 *
	 * @see #TABBED_PANE_TAB_AREA_ALIGNMENT
	 */
	String TABBED_PANE_TAB_AREA_ALIGN_TRAILING = "trailing";

	/**
	 * Align the tab area to center.
	 *
	 * @see #TABBED_PANE_TAB_AREA_ALIGNMENT
	 */
	String TABBED_PANE_TAB_AREA_ALIGN_CENTER = "center";

	/**
	 * Stretch tabs to fill all available space.
	 *
	 * @see #TABBED_PANE_TAB_AREA_ALIGNMENT
	 */
	String TABBED_PANE_TAB_AREA_ALIGN_FILL = "fill";

	/**
	 * Specifies how the tabs should be sized.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.String}<br>
	 * <strong>Allowed Values</strong> {@link #TABBED_PANE_TAB_WIDTH_MODE_PREFERRED} (default),
	 * {@link #TABBED_PANE_TAB_WIDTH_MODE_EQUAL} or {@link #TABBED_PANE_TAB_WIDTH_MODE_COMPACT}
	 */
	String TABBED_PANE_TAB_WIDTH_MODE = "JTabbedPane.tabWidthMode";

	/**
	 * Tab width is adjusted to tab icon and title.
	 *
	 * @see #TABBED_PANE_TAB_WIDTH_MODE
	 */
	String TABBED_PANE_TAB_WIDTH_MODE_PREFERRED = "preferred";

	/**
	 * All tabs in a tabbed pane has same width.
	 *
	 * @see #TABBED_PANE_TAB_WIDTH_MODE
	 */
	String TABBED_PANE_TAB_WIDTH_MODE_EQUAL = "equal";

	/**
	 * Unselected tabs are smaller because they show only the tab icon, but no tab title.
	 * Selected tabs show both.
	 *
	 * @see #TABBED_PANE_TAB_WIDTH_MODE
	 */
	String TABBED_PANE_TAB_WIDTH_MODE_COMPACT = "compact";

	/**
	 * Specifies the tab icon placement (relative to tab title).
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.lang.Integer}<br>
	 * <strong>Allowed Values</strong> {@link SwingConstants#LEADING} (default),
	 * {@link SwingConstants#TRAILING}, {@link SwingConstants#TOP}
	 * or {@link SwingConstants#BOTTOM}
	 */
	String TABBED_PANE_TAB_ICON_PLACEMENT = "JTabbedPane.tabIconPlacement";

	/**
	 * Specifies a component that will be placed at the leading edge of the tabs area.
	 * <p>
	 * For top and bottom tab placement, the layed out component size will be
	 * the preferred component width and the tab area height.<br>
	 * For left and right tab placement, the layed out component size will be
	 * the tab area width and the preferred component height.
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.awt.Component}
	 */
	String TABBED_PANE_LEADING_COMPONENT = "JTabbedPane.leadingComponent";

	/**
	 * Specifies a component that will be placed at the trailing edge of the tabs area.
	 * <p>
	 * For top and bottom tab placement, the layed out component size will be
	 * the available horizontal space (minimum is preferred component width) and the tab area height.<br>
	 * For left and right tab placement, the layed out component size will be
	 * the tab area width and the available vertical space (minimum is preferred component height).
	 * <p>
	 * <strong>Component</strong> {@link javax.swing.JTabbedPane}<br>
	 * <strong>Value type</strong> {@link java.awt.Component}
	 */
	String TABBED_PANE_TRAILING_COMPONENT = "JTabbedPane.trailingComponent";

	//---- JTextField ---------------------------------------------------------

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

	//---- JToggleButton ------------------------------------------------------

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

	//---- helper methods -----------------------------------------------------

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
	 * Checks whether a client property of a component is a {@link Boolean} and returns its value.
	 * If the client property is not set, or not a {@link Boolean}, defaultValue is returned.
	 */
	static Boolean clientPropertyBooleanStrict( JComponent c, String key, Boolean defaultValue ) {
		Object value = c.getClientProperty( key );
		return (value instanceof Boolean) ? (Boolean) value : defaultValue;
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
