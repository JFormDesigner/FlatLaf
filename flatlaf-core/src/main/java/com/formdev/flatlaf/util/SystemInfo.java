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

package com.formdev.flatlaf.util;

import java.util.Locale;

/**
 * Provides information about the current system.
 *
 * @author Karl Tauber
 */
public class SystemInfo
{
	public static final boolean IS_WINDOWS;
	public static final boolean IS_MAC;
	public static final boolean IS_LINUX;

	static {
		String osName = System.getProperty( "os.name" ).toLowerCase( Locale.ENGLISH );

		IS_WINDOWS = osName.startsWith( "windows" );
		IS_MAC = osName.startsWith( "mac" );
		IS_LINUX = osName.startsWith( "linux" );
	}
}
