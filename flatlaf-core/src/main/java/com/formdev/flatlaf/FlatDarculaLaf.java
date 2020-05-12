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

/**
 * A Flat LaF that has a dark color scheme and looks like Darcula LaF.
 *
 * The UI defaults are loaded from FlatDarculaLaf.properties, FlatDarkLaf.properties and FlatLaf.properties
 *
 * @author Karl Tauber
 */
public class FlatDarculaLaf
	extends FlatDarkLaf
{
	public static boolean install( ) {
		return install( new FlatDarculaLaf() );
	}

	@Override
	public String getName() {
		return "FlatLaf Darcula";
	}

	@Override
	public String getDescription() {
		return "FlatLaf Darcula Look and Feel";
	}
}
