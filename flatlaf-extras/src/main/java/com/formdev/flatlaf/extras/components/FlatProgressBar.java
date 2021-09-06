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
import javax.swing.JProgressBar;

/**
 * Subclass of {@link JProgressBar} that provides easy access to FlatLaf specific client properties.
 *
 * @author Karl Tauber
 */
public class FlatProgressBar
	extends JProgressBar
	implements FlatComponentExtension, FlatStyleableComponent
{
	/**
	 * Returns whether the progress bar has always the larger height even if no string is painted.
	 */
	public boolean isLargeHeight() {
		return getClientPropertyBoolean( PROGRESS_BAR_LARGE_HEIGHT, false );
	}

	/**
	 * Specifies whether the progress bar has always the larger height even if no string is painted.
	 */
	public void setLargeHeight( boolean largeHeight ) {
		putClientPropertyBoolean( PROGRESS_BAR_LARGE_HEIGHT, largeHeight, false );
	}


	/**
	 * Returns whether the progress bar is paint with square edges.
	 */
	public boolean isSquare() {
		return getClientPropertyBoolean( PROGRESS_BAR_SQUARE, false );
	}

	/**
	 * Specifies whether the progress bar is paint with square edges.
	 */
	public void setSquare( boolean square ) {
		putClientPropertyBoolean( PROGRESS_BAR_SQUARE, square, false );
	}
}
