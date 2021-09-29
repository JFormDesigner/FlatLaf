/*
 * Copyright 2021 FormDev Software GmbH
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

import java.util.Map;
import javax.swing.JComponent;
import com.formdev.flatlaf.FlatClientProperties;

/**
 * Interface for all styleable FlatLaf components.
 * <p>
 * If you already have custom subclasses of Swing components, you can add this interface
 * to your components to add styling getter and setter methods to them.
 *
 * @author Karl Tauber
 * @since 2
 */
public interface FlatStyleableComponent
{
	/**
	 * Returns the style of a component as String in CSS syntax ("key1: value1; key2: value2; ...")
	 * or {@code null} if no style has been assigned.
	 *
	 * @see FlatClientProperties#STYLE
	 */
	default String getStyle() {
		return (String) getClientProperty( FlatClientProperties.STYLE );
	}

	/**
	 * Specifies the style of a component as String in CSS syntax ("key1: value1; key2: value2; ...").
	 * <p>
	 * The keys are the same as used in UI defaults, but without component type prefix.
	 * E.g. for UI default {@code Slider.thumbSize} use key {@code thumbSize}.
	 * <p>
	 * The syntax of the CSS values is the same as used in FlatLaf properties files
	 * (<a href="https://www.formdev.com/flatlaf/properties-files/">https://www.formdev.com/flatlaf/properties-files/</a>),
	 * but some features are not supported (e.g. variables).
	 *
	 * @see FlatClientProperties#STYLE
	 */
	default void setStyle( String style ) {
		putClientProperty( FlatClientProperties.STYLE, style );
	}


	/**
	 * Returns the style of a component as {@link java.util.Map}&lt;String, Object&gt;
	 * or {@code null} if no style has been assigned.
	 *
	 * @see FlatClientProperties#STYLE
	 */
	@SuppressWarnings( "unchecked" )
	default Map<String, Object> getStyleMap() {
		return (Map<String, Object>) getClientProperty( FlatClientProperties.STYLE );
	}

	/**
	 * Specifies the style of a component as {@link java.util.Map}&lt;String, Object&gt; with binary values.
	 * <p>
	 * The keys are the same as used in UI defaults, but without component type prefix.
	 * E.g. for UI default {@code Slider.thumbSize} use key {@code thumbSize}.
	 * <p>
	 * The values are not parsed from a string. They must be binary.
	 *
	 * @see FlatClientProperties#STYLE
	 */
	default void setStyleMap( Map<String, Object> styleMap ) {
		putClientProperty( FlatClientProperties.STYLE, styleMap );
	}


	/**
	 * Returns the style class(es) of a component (separated by space characters)
	 * or {@code null} if no style class has been assigned.
	 *
	 * @see FlatClientProperties#STYLE_CLASS
	 */
	default String getStyleClass() {
		return (String) getClientProperty( FlatClientProperties.STYLE_CLASS );
	}

	/**
	 * Specifies the style class(es) of a component (separated by space characters).
	 *
	 * @see FlatClientProperties#STYLE_CLASS
	 */
	default void setStyleClass( String styleClass ) {
		putClientProperty( FlatClientProperties.STYLE_CLASS, styleClass );
	}


	/**
	 * Overrides {@link JComponent#getClientProperty(Object)}.
	 */
	Object getClientProperty( Object key );

	/**
	 * Overrides {@link JComponent#putClientProperty(Object, Object)}.
	 */
	void putClientProperty( Object key, Object value );
}
