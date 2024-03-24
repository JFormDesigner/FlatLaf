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

import static com.formdev.flatlaf.FlatClientProperties.*;
import javax.swing.JSplitPane;

/**
 * Subclass of {@link JSplitPane} that provides easy access to FlatLaf specific client properties.
 *
 * @author Karl Tauber
 * @since 2
 */
public class FlatSplitPane
	extends JSplitPane
	implements FlatComponentExtension, FlatStyleableComponent
{
	// NOTE: enum names must be equal to allowed strings
	/** @since 3.4.1 */ public enum ExpandableSide { both, left, right }

	/**
	 * Returns what side of the spilt pane is allowed to expand
	 * via one-touch expanding arrow buttons.
	 *
	 * @since 3.4.1
	 */
	public ExpandableSide getExpandableSide() {
		return getClientPropertyEnumString( SPLIT_PANE_EXPANDABLE_SIDE, ExpandableSide.class,
			null, ExpandableSide.both );
	}

	/**
	 * Specifies what side of the spilt pane is allowed to expand
	 * via one-touch expanding arrow buttons.
	 *
	 * @since 3.4.1
	 */
	public void setExpandableSide( ExpandableSide expandableSide ) {
		putClientPropertyEnumString( SPLIT_PANE_EXPANDABLE_SIDE, expandableSide );
	}
}
