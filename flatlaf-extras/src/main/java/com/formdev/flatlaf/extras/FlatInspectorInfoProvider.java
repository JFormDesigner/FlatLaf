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
 * {@link #appendInspectorInfo(StringBuilder)} is invoked while {@code FlatInspector} is
 * building the HTML tooltip content for the inspected component. Append zero or more
 * HTML table rows in the form:
 * <pre>
 * "&lt;tr&gt;&lt;td valign=\"top\"&gt;" + key + ":&lt;/td&gt;&lt;td&gt;" + value + "&lt;/td&gt;&lt;/tr&gt;"
 * </pre>
 * The rows are appended after the built-in "FlatLaf Style" row and before the footer
 * that shows the parent inspection level and key bindings hint.
 */
public interface FlatInspectorInfoProvider
{
	/**
	 * Appends additional HTML table rows with component-specific debug information.
	 *
	 * @param buf the buffer used to build the tooltip HTML; append {@code <tr><td>...} rows to it
	 */
	void appendInspectorInfo( StringBuilder buf );
}
