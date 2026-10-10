/*
 * Copyright 2026 FormDev Software GmbH
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

package com.formdev.flatlaf.extras;

/**
 * Implement this interface in a {@link java.awt.Component} to let {@link FlatInspector}
 * show additional, component-specific information in its debug tooltip.
 * <p>
 * {@link #appendInspectorInfo(StringBuilder,boolean)} is invoked while
 * {@code FlatInspector} is building the HTML tooltip content for the inspected component.
 * The rows are appended after the built-in "FlatLaf Style" row and before the footer
 * that shows the parent inspection level and key bindings hint.
 */
public interface FlatInspectorInfoProvider
{
	/**
	 * Appends additional HTML table rows with component-specific debug information.
	 * <p>
	 * Use {@link FlatInspector#appendRow(StringBuilder, String, String)} to append rows,
	 * and the {@code FlatInspector.toString(...)} helpers (for {@link Class},
	 * {@link java.awt.Insets}, {@link java.awt.Color}, {@link java.awt.Font},
	 * {@link javax.swing.border.Border}) to format values the same way the rest of the
	 * tooltip does, e.g.:
	 * <pre>
	 * FlatInspector.appendRow( buf, "Model",
	 *     FlatInspector.toString( model.getClass(), showClassHierarchy ) );
	 * </pre>
	 * Values are inserted as-is (not HTML-escaped), so avoid passing raw user text that
	 * may contain {@code <}, {@code >} or {@code &} unless it is already safe to embed
	 * as HTML.
	 *
	 * @param buf the buffer used to build the tooltip HTML
	 * @param showClassHierarchy whether the user has toggled display of the full class
	 *        hierarchy (Alt key); pass this through to
	 *        {@code FlatInspector.toString(Class, boolean)} calls for consistency
	 */
	void appendInspectorInfo( StringBuilder buf, boolean showClassHierarchy );
}
