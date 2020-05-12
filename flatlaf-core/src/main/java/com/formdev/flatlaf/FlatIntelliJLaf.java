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
 * A Flat LaF that has a light color scheme and looks like IntelliJ LaF.
 *
 * The UI defaults are loaded from FlatIntelliJLaf.properties, FlatLightLaf.properties and FlatLaf.properties
 *
 * @author Karl Tauber
 */
public class FlatIntelliJLaf
	extends FlatLightLaf
{
	public static boolean install( ) {
		return install( new FlatIntelliJLaf() );
	}

	@Override
	public String getName() {
		return "FlatLaf IntelliJ";
	}

	@Override
	public String getDescription() {
		return "FlatLaf IntelliJ Look and Feel";
	}
}
