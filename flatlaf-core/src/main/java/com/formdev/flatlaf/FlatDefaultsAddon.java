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

import java.io.InputStream;

/**
 * Addon for FlatLaf UI defaults.
 *
 * Allows loading of additional .properties files from addon JARs.
 * {@link java.util.ServiceLoader} is used to load extensions of this class from addon JARs.
 *
 * If you extend this class in a addon JAR, you also have to add a text file named
 * {@code META-INF/services/com.formdev.flatlaf.FlatDefaultsAddon}
 * to the addon JAR. The file must contain a single line with the class name.
 *
 * See 'flatlaf-swingx' addon for an example
 *
 * @author Karl Tauber
 */
public abstract class FlatDefaultsAddon
{
	/**
	 * Finds an addon .properties file for the given LaF class and returns
	 * it as input stream. Or {@code null} if not found.
	 */
	public abstract InputStream getDefaults( Class<?> lafClass );
}
