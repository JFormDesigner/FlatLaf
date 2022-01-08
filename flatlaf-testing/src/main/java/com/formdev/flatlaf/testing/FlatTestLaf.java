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

package com.formdev.flatlaf.testing;

import com.formdev.flatlaf.FlatLaf;

/**
 * A Flat LaF that has a test color scheme.
 *
 * The UI defaults are loaded from FlatTestLaf.properties and FlatLaf.properties
 *
 * Used to develop Flat LaF.
 *
 * @author Karl Tauber
 */
public class FlatTestLaf
	extends FlatLaf
{
	@Override
	public String getName() {
		return "FlatLaf Test";
	}

	@Override
	public String getDescription() {
		return "FlatLaf Test Look and Feel";
	}

	@Override
	public boolean isDark() {
		return false;
	}
}
