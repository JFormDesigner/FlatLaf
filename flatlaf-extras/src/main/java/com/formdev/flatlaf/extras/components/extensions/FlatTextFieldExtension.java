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
import javax.swing.JFormattedTextField;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import com.formdev.flatlaf.extras.components.FlatFormattedTextField;
import com.formdev.flatlaf.extras.components.FlatPasswordField;
import com.formdev.flatlaf.extras.components.FlatTextField;

/**
 * Extension interface for {@link JTextField} (and subclasses) that provides
 * easy access to FlatLaf specific client properties.
 * <p>
 * Use this interface if you already have a subclass of {@link JTextField},
 * {@link JFormattedTextField} or {@link JPasswordField}
 * in your project and want add access to FlatLaf features to your class.
 * Otherwise use {@link FlatTextField}, {@link FlatFormattedTextField} or {@link FlatPasswordField}.
 *
 * @author Karl Tauber
 */
public interface FlatTextFieldExtension
	extends FlatComponentExtension
{
	/**
	 * Returns the placeholder text that is only painted if the text field is empty.
	 */
	default String getPlaceholderText() {
		return (String) getClientProperty( PLACEHOLDER_TEXT );
	}

	/**
	 * Sets the placeholder text that is only painted if the text field is empty.
	 */
	default void setPlaceholderText( String placeholderText ) {
		putClientProperty( PLACEHOLDER_TEXT, placeholderText );
	}


	// NOTE: enum names must be equal to allowed strings
	enum SelectAllOnFocusPolicy { never, once, always };

	/**
	 * Returns whether all text is selected when the text component gains focus.
	 */
	default SelectAllOnFocusPolicy getSelectAllOnFocusPolicy() {
		return getClientPropertyEnumString( SELECT_ALL_ON_FOCUS_POLICY, SelectAllOnFocusPolicy.class,
			"TextComponent.selectAllOnFocusPolicy", SelectAllOnFocusPolicy.once );
	}

	/**
	 * Specifies whether all text is selected when the text component gains focus.
	 */
	default void setSelectAllOnFocusPolicy( SelectAllOnFocusPolicy selectAllOnFocusPolicy ) {
		putClientPropertyEnumString( SELECT_ALL_ON_FOCUS_POLICY, selectAllOnFocusPolicy );
	}
}
