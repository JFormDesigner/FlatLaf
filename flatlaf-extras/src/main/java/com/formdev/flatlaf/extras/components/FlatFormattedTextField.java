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
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import com.formdev.flatlaf.extras.components.FlatTextField.SelectAllOnFocusPolicy;

/**
 * Subclass of {@link JFormattedTextField} that provides easy access to FlatLaf specific client properties.
 *
 * @author Karl Tauber
 */
public class FlatFormattedTextField
	extends JFormattedTextField
	implements FlatComponentExtension, FlatStyleableComponent
{
	/**
	 * Returns the placeholder text that is only painted if the text field is empty.
	 */
	public String getPlaceholderText() {
		return (String) getClientProperty( PLACEHOLDER_TEXT );
	}

	/**
	 * Sets the placeholder text that is only painted if the text field is empty.
	 */
	public void setPlaceholderText( String placeholderText ) {
		putClientProperty( PLACEHOLDER_TEXT, placeholderText );
	}


	/**
	 * Returns the leading icon that will be placed at the leading edge of the text field.
	 *
	 * @since 2
	 */
	public Icon getLeadingIcon() {
		return (Icon) getClientProperty( TEXT_FIELD_LEADING_ICON );
	}

	/**
	 * Specifies the leading icon that will be placed at the leading edge of the text field.
	 *
	 * @since 2
	 */
	public void setLeadingIcon( Icon leadingIcon ) {
		putClientProperty( TEXT_FIELD_LEADING_ICON, leadingIcon );
	}


	/**
	 * Returns the trailing icon that will be placed at the trailing edge of the text field.
	 *
	 * @since 2
	 */
	public Icon getTrailingIcon() {
		return (Icon) getClientProperty( TEXT_FIELD_TRAILING_ICON );
	}

	/**
	 * Specifies the trailing icon that will be placed at the trailing edge of the text field.
	 *
	 * @since 2
	 */
	public void setTrailingIcon( Icon trailingIcon ) {
		putClientProperty( TEXT_FIELD_TRAILING_ICON, trailingIcon );
	}


	/**
	 * Returns a component that will be placed at the leading edge of the text field.
	 *
	 * @since 2
	 */
	public JComponent getLeadingComponent() {
		return (JComponent) getClientProperty( TEXT_FIELD_LEADING_COMPONENT );
	}

	/**
	 * Specifies a component that will be placed at the leading edge of the text field.
	 * <p>
	 * The component will be positioned inside and aligned to the visible text field border.
	 * There is no gap between the visible border and the component.
	 * The laid out component size will be the preferred component width
	 * and the inner text field height.
	 * <p>
	 * The component should be not opaque because the text field border is painted
	 * slightly inside the usually visible border in some cases.
	 * E.g. when focused (in some themes) or when an outline color is specified
	 * (see {@link #setOutline(Object)}).
	 *
	 * @since 2
	 */
	public void setLeadingComponent( JComponent leadingComponent ) {
		putClientProperty( TEXT_FIELD_LEADING_COMPONENT, leadingComponent );
	}


	/**
	 * Returns a component that will be placed at the trailing edge of the text field.
	 *
	 * @since 2
	 */
	public JComponent getTrailingComponent() {
		return (JComponent) getClientProperty( TEXT_FIELD_TRAILING_COMPONENT );
	}

	/**
	 * Specifies a component that will be placed at the trailing edge of the text field.
	 * <p>
	 * The component will be positioned inside and aligned to the visible text field border.
	 * There is no gap between the visible border and the component.
	 * The laid out component size will be the preferred component width
	 * and the inner text field height.
	 * <p>
	 * The component should be not opaque because the text field border is painted
	 * slightly inside the usually visible border in some cases.
	 * E.g. when focused (in some themes) or when an outline color is specified
	 * (see {@link #setOutline(Object)}).
	 *
	 * @since 2
	 */
	public void setTrailingComponent( JComponent trailingComponent ) {
		putClientProperty( TEXT_FIELD_TRAILING_COMPONENT, trailingComponent );
	}


	/**
	 * Returns whether a "clear" (or "cancel") button is shown.
	 *
	 * @since 2
	 */
	public boolean isShowClearButton() {
		return getClientPropertyBoolean( TEXT_FIELD_SHOW_CLEAR_BUTTON, false );
	}

	/**
	 * Specifies whether a "clear" (or "cancel") button is shown on the trailing side
	 * if the text field is not empty, editable and enabled.
	 *
	 * @since 2
	 */
	public void setShowClearButton( boolean showClearButton ) {
		putClientPropertyBoolean( TEXT_FIELD_SHOW_CLEAR_BUTTON, showClearButton, false );
	}


	/**
	 * Returns whether all text is selected when the text component gains focus.
	 */
	public SelectAllOnFocusPolicy getSelectAllOnFocusPolicy() {
		return getClientPropertyEnumString( SELECT_ALL_ON_FOCUS_POLICY, SelectAllOnFocusPolicy.class,
			"TextComponent.selectAllOnFocusPolicy", SelectAllOnFocusPolicy.once );
	}

	/**
	 * Specifies whether all text is selected when the text component gains focus.
	 */
	public void setSelectAllOnFocusPolicy( SelectAllOnFocusPolicy selectAllOnFocusPolicy ) {
		putClientPropertyEnumString( SELECT_ALL_ON_FOCUS_POLICY, selectAllOnFocusPolicy );
	}


	/**
	 * Returns the padding of the text.
	 *
	 * @since 1.4
	 */
	public Insets getPadding() {
		return (Insets) getClientProperty( TEXT_FIELD_PADDING );
	}

	/**
	 * Specifies the padding of the text.
	 * This changes the location and size of the text view within the component bounds,
	 * but does not affect the size of the component.
	 *
	 * @since 1.4
	 */
	public void setPadding( Insets padding ) {
		putClientProperty( TEXT_FIELD_PADDING, padding );
	}


	/**
	 * Returns minimum width of a component.
	 */
	public int getMinimumWidth() {
		return getClientPropertyInt( MINIMUM_WIDTH, "Component.minimumWidth" );
	}

	/**
	 * Specifies minimum width of a component.
	 */
	public void setMinimumWidth( int minimumWidth ) {
		putClientProperty( MINIMUM_WIDTH, (minimumWidth >= 0) ? minimumWidth : null );
	}


	/**
	 * Returns whether the component is painted with round edges.
	 */
	public boolean isRoundRect() {
		return getClientPropertyBoolean( COMPONENT_ROUND_RECT, false );
	}

	/**
	 * Specifies whether the component is painted with round edges.
	 */
	public void setRoundRect( boolean roundRect ) {
		putClientPropertyBoolean( COMPONENT_ROUND_RECT, roundRect, false );
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
