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
import java.util.StringTokenizer;

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

	public static final boolean IS_JAVA_9_OR_LATER;

	public static final boolean IS_JETBRAINS_JVM;

	static {
		String osName = System.getProperty( "os.name" ).toLowerCase( Locale.ENGLISH );

		IS_WINDOWS = osName.startsWith( "windows" );
		IS_MAC = osName.startsWith( "mac" );
		IS_LINUX = osName.startsWith( "linux" );

		int javaVersion = scanVersion( System.getProperty( "java.version" ) );
		IS_JAVA_9_OR_LATER = (javaVersion >= toVersion( 9, 0, 0, 0 ));

		IS_JETBRAINS_JVM = System.getProperty( "java.vm.vendor", "Unknown" )
			.toLowerCase( Locale.ENGLISH ).contains( "jetbrains" );
	}

	private static int scanVersion( String version ) {
		int major = 1;
		int minor = 0;
		int micro = 0;
		int patch = 0;
		try {
			StringTokenizer st = new StringTokenizer( version, "._-+" );
			major = Integer.parseInt( st.nextToken() );
			minor = Integer.parseInt( st.nextToken() );
			micro = Integer.parseInt( st.nextToken() );
			patch = Integer.parseInt( st.nextToken() );
		} catch( Exception ex ) {
			// ignore
		}

		return toVersion( major, minor, micro, patch );
	}

	private static int toVersion( int major, int minor, int micro, int patch ) {
		return (major << 24) + (minor << 16) + (micro << 8) + patch;
	}
}
