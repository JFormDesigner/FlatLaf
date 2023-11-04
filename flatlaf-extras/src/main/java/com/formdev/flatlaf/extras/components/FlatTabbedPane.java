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
import java.awt.Component;
import java.awt.Insets;
import java.util.function.BiConsumer;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * Subclass of {@link JTabbedPane} that provides easy access to FlatLaf specific client properties.
 *
 * @author Karl Tauber
 */
public class FlatTabbedPane
	extends JTabbedPane
	implements FlatComponentExtension, FlatStyleableComponent
{
	/**
	 * Returns whether separators are shown between tabs.
	 */
	public boolean isShowTabSeparators() {
		return getClientPropertyBoolean( TABBED_PANE_SHOW_TAB_SEPARATORS, "TabbedPane.showTabSeparators" );
	}

	/**
	 * Specifies whether separators are shown between tabs.
	 */
	public void setShowTabSeparators( boolean showTabSeparators ) {
		putClientProperty( TABBED_PANE_SHOW_TAB_SEPARATORS, showTabSeparators );
	}


	/**
	 * Returns whether the separator between tabs area and content area should be shown.
	 */
	public boolean isShowContentSeparators() {
		return getClientPropertyBoolean( TABBED_PANE_SHOW_CONTENT_SEPARATOR, true );
	}

	/**
	 * Specifies whether the separator between tabs area and content area should be shown.
	 */
	public void setShowContentSeparators( boolean showContentSeparators ) {
		putClientPropertyBoolean( TABBED_PANE_SHOW_CONTENT_SEPARATOR, showContentSeparators, true );
	}


	/**
	 * Returns whether a full border is painted around a tabbed pane.
	 */
	public boolean isHasFullBorder() {
		return getClientPropertyBoolean( TABBED_PANE_HAS_FULL_BORDER, "TabbedPane.hasFullBorder" );
	}

	/**
	 * Specifies whether a full border is painted around a tabbed pane.
	 */
	public void setHasFullBorder( boolean hasFullBorder ) {
		putClientProperty( TABBED_PANE_HAS_FULL_BORDER, hasFullBorder );
	}


	/**
	 * Returns whether the tab area should be hidden if it contains only one tab.
	 */
	public boolean isHideTabAreaWithOneTab() {
		return getClientPropertyBoolean( TABBED_PANE_HIDE_TAB_AREA_WITH_ONE_TAB, false );
	}

	/**
	 * Specifies whether the tab area should be hidden if it contains only one tab.
	 */
	public void setHideTabAreaWithOneTab( boolean hideTabAreaWithOneTab ) {
		putClientPropertyBoolean( TABBED_PANE_HIDE_TAB_AREA_WITH_ONE_TAB, hideTabAreaWithOneTab, false );
	}


	/**
	 * Returns the minimum width of a tab.
	 */
	public int getMinimumTabWidth() {
		return getClientPropertyInt( TABBED_PANE_MINIMUM_TAB_WIDTH, "TabbedPane.minimumTabWidth" );
	}

	/**
	 * Specifies the minimum width of a tab.
	 */
	public void setMinimumTabWidth( int minimumTabWidth ) {
		putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH, (minimumTabWidth >= 0) ? minimumTabWidth : null );
	}


	/**
	 * Returns the minimum width of the tab at the given tab index.
	 */
	public int getMinimumTabWidth( int tabIndex ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		return clientPropertyInt( c, TABBED_PANE_MINIMUM_TAB_WIDTH, 0 );
	}

	/**
	 * Specifies the minimum width of the tab at the given tab index.
	 */
	public void setMinimumTabWidth( int tabIndex, int minimumTabWidth ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		c.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH, (minimumTabWidth >= 0) ? minimumTabWidth : null );
	}


	/**
	 * Returns the maximum width of a tab.
	 */
	public int getMaximumTabWidth() {
		return getClientPropertyInt( TABBED_PANE_MAXIMUM_TAB_WIDTH, "TabbedPane.maximumTabWidth" );
	}

	/**
	 * Specifies the maximum width of a tab.
	 * <p>
	 * Applied only if tab does not have a custom tab component
	 * (see {@link javax.swing.JTabbedPane#setTabComponentAt(int, java.awt.Component)}).
	 */
	public void setMaximumTabWidth( int maximumTabWidth ) {
		putClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH, (maximumTabWidth >= 0) ? maximumTabWidth : null );
	}


	/**
	 * Returns the maximum width of the tab at the given tab index.
	 */
	public int getMaximumTabWidth( int tabIndex ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		return clientPropertyInt( c, TABBED_PANE_MAXIMUM_TAB_WIDTH, 0 );
	}

	/**
	 * Specifies the maximum width of the tab at the given tab index.
	 * <p>
	 * Applied only if tab does not have a custom tab component
	 * (see {@link javax.swing.JTabbedPane#setTabComponentAt(int, java.awt.Component)}).
	 */
	public void setMaximumTabWidth( int tabIndex, int maximumTabWidth ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		c.putClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH, (maximumTabWidth >= 0) ? maximumTabWidth : null );
	}


	/**
	 * Returns the minimum height of a tab.
	 */
	public int getTabHeight() {
		return getClientPropertyInt( TABBED_PANE_TAB_HEIGHT, "TabbedPane.tabHeight" );
	}

	/**
	 * Specifies the minimum height of a tab.
	 *
	 * @see #setTabInsets(Insets)
	 */
	public void setTabHeight( int tabHeight ) {
		putClientProperty( TABBED_PANE_TAB_HEIGHT, (tabHeight >= 0) ? tabHeight : null );
	}


	/**
	 * Returns the insets of a tab.
	 */
	public Insets getTabInsets() {
		return getClientPropertyInsets( TABBED_PANE_TAB_INSETS, "TabbedPane.tabInsets" );
	}

	/**
	 * Specifies the insets of a tab.
	 *
	 * @see #setTabHeight(int)
	 */
	public void setTabInsets( Insets tabInsets ) {
		putClientProperty( TABBED_PANE_TAB_INSETS, tabInsets );
	}


	/**
	 * Returns the insets of the tab at the given tab index.
	 */
	public Insets getTabInsets( int tabIndex ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		return (Insets) c.getClientProperty( TABBED_PANE_TAB_INSETS );
	}

	/**
	 * Specifies the insets of the tab at the given tab index.
	 *
	 * @see #setTabHeight(int)
	 */
	public void setTabInsets( int tabIndex, Insets tabInsets ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		c.putClientProperty( TABBED_PANE_TAB_INSETS, tabInsets );
	}


	/**
	 * Returns the insets of the tab area.
	 */
	public Insets getTabAreaInsets() {
		return getClientPropertyInsets( TABBED_PANE_TAB_AREA_INSETS, "TabbedPane.tabAreaInsets" );
	}

	/**
	 * Specifies the insets of the tab area.
	 */
	public void setTabAreaInsets( Insets tabAreaInsets ) {
		putClientProperty( TABBED_PANE_TAB_AREA_INSETS, tabAreaInsets );
	}


	/**
	 * Returns whether all tabs are closable.
	 */
	public boolean isTabsClosable() {
		return getClientPropertyBoolean( TABBED_PANE_TAB_CLOSABLE, false );
	}

	/**
	 * Specifies whether all tabs are closable.
	 * If set to {@code true}, all tabs in that tabbed pane are closable.
	 * To make individual tabs closable, use {@link #setTabClosable(int, boolean)}.
	 * <p>
	 * Note that you have to specify a callback (see {@link #setTabCloseCallback(BiConsumer)})
	 * that is invoked when the user clicks a tab close button.
	 * The callback is responsible for closing the tab.
	 */
	public void setTabsClosable( boolean tabClosable ) {
		putClientPropertyBoolean( TABBED_PANE_TAB_CLOSABLE, tabClosable, false );
	}


	/**
	 * Returns whether the tab at the given tab index is closable.
	 */
	public Boolean isTabClosable( int tabIndex ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		Object value = c.getClientProperty( TABBED_PANE_TAB_CLOSABLE );
		return (value instanceof Boolean) ? (boolean) value : isTabsClosable();
	}

	/**
	 * Specifies whether the tab at the given tab index is closable.
	 * To make all tabs closable, use {@link #setTabsClosable(boolean)}.
	 * <p>
	 * Note that you have to specify a callback (see {@link #setTabCloseCallback(BiConsumer)})
	 * that is invoked when the user clicks a tab close button.
	 * The callback is responsible for closing the tab.
	 */
	public void setTabClosable( int tabIndex, boolean tabClosable ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		c.putClientProperty( TABBED_PANE_TAB_CLOSABLE, tabClosable );
	}


	/**
	 * Returns the tooltip text used for tab close buttons.
	 */
	public String getTabCloseToolTipText() {
		return (String) getClientProperty( TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT );
	}

	/**
	 * Specifies the tooltip text used for tab close buttons.
	 */
	public void setTabCloseToolTipText( String tabCloseToolTipText ) {
		putClientProperty( TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, tabCloseToolTipText );
	}


	/**
	 * Returns the tooltip text used for tab close button at the given tab index.
	 */
	public String getTabCloseToolTipText( int tabIndex ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		return (String) c.getClientProperty( TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT );
	}

	/**
	 * Specifies the tooltip text used for tab close button at the given tab index.
	 */
	public void setTabCloseToolTipText( int tabIndex, String tabCloseToolTipText ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		c.putClientProperty( TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, tabCloseToolTipText );
	}


	/**
	 * Returns the callback that is invoked when a tab close button is clicked.
	 * The callback is responsible for closing the tab.
	 */
	@SuppressWarnings( "unchecked" )
	public BiConsumer<JTabbedPane, Integer> getTabCloseCallback() {
		return (BiConsumer<JTabbedPane, Integer>) getClientProperty( TABBED_PANE_TAB_CLOSE_CALLBACK );
	}

	/**
	 * Specifies the callback that is invoked when a tab close button is clicked.
	 * The callback is responsible for closing the tab.
	 * <p>
	 * Use a {@link java.util.function.BiConsumer}&lt;javax.swing.JTabbedPane, Integer&gt;
	 * that receives the tabbed pane and the tab index as parameters:
	 * <pre>{@code
	 * myTabbedPane.setTabCloseCallback( (tabbedPane, tabIndex) -> {
	 *     // close tab here
	 * } );
	 * }</pre>
	 * If you need to check whether a modifier key (e.g. Alt or Shift) was pressed
	 * while the user clicked the tab close button, use {@link java.awt.EventQueue#getCurrentEvent}
	 * to get current event, check whether it is a {@link java.awt.event.MouseEvent}
	 * and invoke its methods. E.g.
	 * <pre>{@code
	 * AWTEvent e = EventQueue.getCurrentEvent();
	 * boolean shift = (e instanceof MouseEvent) ? ((MouseEvent)e).isShiftDown() : false;
	 * }</pre>
	 */
	public void setTabCloseCallback( BiConsumer<JTabbedPane, Integer> tabCloseCallback ) {
		putClientProperty( TABBED_PANE_TAB_CLOSE_CALLBACK, tabCloseCallback );
	}


	/**
	 * Returns the callback that is invoked when the tab close button at the given tab index is clicked.
	 * The callback is responsible for closing the tab.
	 */
	@SuppressWarnings( "unchecked" )
	public BiConsumer<JTabbedPane, Integer> getTabCloseCallback( int tabIndex ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		return (BiConsumer<JTabbedPane, Integer>) c.getClientProperty( TABBED_PANE_TAB_CLOSE_CALLBACK );
	}

	/**
	 * Specifies the callback that is invoked when the tab close button at the given tab index is clicked.
	 * The callback is responsible for closing the tab.
	 *
	 * @see #setTabCloseCallback(BiConsumer)
	 */
	public void setTabCloseCallback( int tabIndex, BiConsumer<JTabbedPane, Integer> tabCloseCallback ) {
		JComponent c = (JComponent) getComponentAt( tabIndex );
		c.putClientProperty( TABBED_PANE_TAB_CLOSE_CALLBACK, tabCloseCallback );
	}


	// NOTE: enum names must be equal to allowed strings
	/** @since 2 */ public enum TabType { underlined, card }

	/**
	 * Returns type of selected tab.
	 *
	 * @since 2
	 */
	public TabType getTabType() {
		return getClientPropertyEnumString( TABBED_PANE_TAB_TYPE, TabType.class,
			"TabbedPane.tabType", TabType.underlined );
	}

	/**
	 * Specifies type of selected tab.
	 *
	 * @since 2
	 */
	public void setTabType( TabType tabType ) {
		putClientPropertyEnumString( TABBED_PANE_TAB_TYPE, tabType );
	}


	// NOTE: enum names must be equal to allowed strings
	public enum TabsPopupPolicy { never, asNeeded }

	/**
	 * Returns the display policy for the "more tabs" button,
	 * which shows a popup menu with the (partly) hidden tabs.
	 */
	public TabsPopupPolicy getTabsPopupPolicy() {
		return getClientPropertyEnumString( TABBED_PANE_TABS_POPUP_POLICY, TabsPopupPolicy.class,
			"TabbedPane.tabsPopupPolicy", TabsPopupPolicy.asNeeded );
	}

	/**
	 * Specifies the display policy for the "more tabs" button,
	 * which shows a popup menu with the (partly) hidden tabs.
	 */
	public void setTabsPopupPolicy( TabsPopupPolicy tabsPopupPolicy ) {
		putClientPropertyEnumString( TABBED_PANE_TABS_POPUP_POLICY, tabsPopupPolicy );
	}


	// NOTE: enum names must be equal to allowed strings
	public enum ScrollButtonsPolicy { never, asNeeded, asNeededSingle }

	/**
	 * Returns the display policy for the forward/backward scroll arrow buttons.
	 */
	public ScrollButtonsPolicy getScrollButtonsPolicy() {
		return getClientPropertyEnumString( TABBED_PANE_SCROLL_BUTTONS_POLICY, ScrollButtonsPolicy.class,
			"TabbedPane.scrollButtonsPolicy", ScrollButtonsPolicy.asNeededSingle );
	}

	/**
	 * Specifies the display policy for the forward/backward scroll arrow buttons.
	 */
	public void setScrollButtonsPolicy( ScrollButtonsPolicy scrollButtonsPolicy ) {
		putClientPropertyEnumString( TABBED_PANE_SCROLL_BUTTONS_POLICY, scrollButtonsPolicy );
	}


	// NOTE: enum names must be equal to allowed strings
	public enum ScrollButtonsPlacement { both, trailing }

	/**
	 * Returns the placement of the forward/backward scroll arrow buttons.
	 */
	public ScrollButtonsPlacement getScrollButtonsPlacement() {
		return getClientPropertyEnumString( TABBED_PANE_SCROLL_BUTTONS_PLACEMENT, ScrollButtonsPlacement.class,
			"TabbedPane.scrollButtonsPlacement", ScrollButtonsPlacement.both );
	}

	/**
	 * Specifies the placement of the forward/backward scroll arrow buttons.
	 */
	public void setScrollButtonsPlacement( ScrollButtonsPlacement scrollButtonsPlacement ) {
		putClientPropertyEnumString( TABBED_PANE_SCROLL_BUTTONS_PLACEMENT, scrollButtonsPlacement );
	}


	// NOTE: enum names must be equal to allowed strings
	public enum TabAreaAlignment { leading, trailing, center, fill }

	/**
	 * Returns the alignment of the tab area.
	 */
	public TabAreaAlignment getTabAreaAlignment() {
		return getClientPropertyEnumString( TABBED_PANE_TAB_AREA_ALIGNMENT, TabAreaAlignment.class,
			"TabbedPane.tabAreaAlignment", TabAreaAlignment.leading );
	}

	/**
	 * Specifies the alignment of the tab area.
	 */
	public void setTabAreaAlignment( TabAreaAlignment tabAreaAlignment ) {
		putClientPropertyEnumString( TABBED_PANE_TAB_AREA_ALIGNMENT, tabAreaAlignment );
	}


	// NOTE: enum names must be equal to allowed strings
	public enum TabAlignment { leading, trailing, center }

	/**
	 * Returns the horizontal alignment of the tab title and icon.
	 */
	public TabAlignment getTabAlignment() {
		return getClientPropertyEnumString( TABBED_PANE_TAB_ALIGNMENT, TabAlignment.class,
			"TabbedPane.tabAlignment", TabAlignment.center );
	}

	/**
	 * Specifies the horizontal alignment of the tab title and icon.
	 */
	public void setTabAlignment( TabAlignment tabAlignment ) {
		putClientPropertyEnumString( TABBED_PANE_TAB_ALIGNMENT, tabAlignment );
	}


	// NOTE: enum names must be equal to allowed strings
	public enum TabWidthMode { preferred, equal, compact }

	/**
	 * Returns how the tabs should be sized.
	 */
	public TabWidthMode getTabWidthMode() {
		return getClientPropertyEnumString( TABBED_PANE_TAB_WIDTH_MODE, TabWidthMode.class,
			"TabbedPane.tabWidthMode", TabWidthMode.preferred );
	}

	/**
	 * Specifies how the tabs should be sized.
	 */
	public void setTabWidthMode( TabWidthMode tabWidthMode ) {
		putClientPropertyEnumString( TABBED_PANE_TAB_WIDTH_MODE, tabWidthMode );
	}


	// NOTE: enum names must be equal to allowed strings
	/** @since 3.3 */ public enum TabRotation { none, auto, left, right }

	/**
	 * Returns how the tabs should be rotated.
	 *
	 * @since 3.3
	 */
	public TabRotation getTabRotation() {
		return getClientPropertyEnumString( TABBED_PANE_TAB_ROTATION, TabRotation.class,
			"TabbedPane.tabRotation", TabRotation.none );
	}

	/**
	 * Specifies how the tabs should be rotated.
	 *
	 * @since 3.3
	 */
	public void setTabRotation( TabRotation tabRotation ) {
		putClientPropertyEnumString( TABBED_PANE_TAB_ROTATION, tabRotation );
	}


	/**
	 * Returns the tab icon placement (relative to tab title).
	 */
	public int getTabIconPlacement() {
		return getClientPropertyInt( TABBED_PANE_TAB_ICON_PLACEMENT, SwingConstants.LEADING );
	}

	/**
	 * Specifies the tab icon placement (relative to tab title).
	 * <p>
	 * Allowed Values are:
	 * <ul>
	 *     <li>{@link SwingConstants#LEADING} (default)
	 *     <li>{@link SwingConstants#TRAILING}
	 *     <li>{@link SwingConstants#TOP}
	 *     <li>{@link SwingConstants#BOTTOM}
	 * </ul>
	 */
	public void setTabIconPlacement( int tabIconPlacement ) {
		putClientProperty( TABBED_PANE_TAB_ICON_PLACEMENT, (tabIconPlacement >= 0) ? tabIconPlacement : null );
	}


	/**
	 * Returns a component that will be placed at the leading edge of the tabs area.
	 */
	public Component getLeadingComponent() {
		return (Component) getClientProperty( TABBED_PANE_LEADING_COMPONENT );
	}

	/**
	 * Specifies a component that will be placed at the leading edge of the tabs area.
	 * <p>
	 * For top and bottom tab placement, the layed out component size will be
	 * the preferred component width and the tab area height.<br>
	 * For left and right tab placement, the layed out component size will be
	 * the tab area width and the preferred component height.
	 */
	public void setLeadingComponent( Component leadingComponent ) {
		putClientProperty( TABBED_PANE_LEADING_COMPONENT, leadingComponent );
	}


	/**
	 * Returns a component that will be placed at the trailing edge of the tabs area.
	 */
	public Component getTrailingComponent() {
		return (Component) getClientProperty( TABBED_PANE_TRAILING_COMPONENT );
	}

	/**
	 * Specifies a component that will be placed at the trailing edge of the tabs area.
	 * <p>
	 * For top and bottom tab placement, the layed out component size will be
	 * the available horizontal space (minimum is preferred component width) and the tab area height.<br>
	 * For left and right tab placement, the layed out component size will be
	 * the tab area width and the available vertical space (minimum is preferred component height).
	 */
	public void setTrailingComponent( Component trailingComponent ) {
		putClientProperty( TABBED_PANE_TRAILING_COMPONENT, trailingComponent );
	}
}
