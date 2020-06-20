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
	// platforms
	public static final boolean IS_WINDOWS;
	public static final boolean IS_MAC;
	public static final boolean IS_LINUX;

	// OS versions
	public static final boolean IS_MAC_OS_10_11_EL_CAPITAN_OR_LATER;
	public static final boolean IS_MAC_OS_10_15_CATALINA_OR_LATER;

	// Java versions
	public static final boolean IS_JAVA_9_OR_LATER;

	// Java VMs
	public static final boolean IS_JETBRAINS_JVM;

	// UI toolkits
	public static final boolean IS_KDE;

	static {
		// platforms
		String osName = System.getProperty( "os.name" ).toLowerCase( Locale.ENGLISH );
		IS_WINDOWS = osName.startsWith( "windows" );
		IS_MAC = osName.startsWith( "mac" );
		IS_LINUX = osName.startsWith( "linux" );

		// OS versions
		long osVersion = scanVersion( System.getProperty( "os.version" ) );
		IS_MAC_OS_10_11_EL_CAPITAN_OR_LATER = (IS_MAC && osVersion >= toVersion( 10, 11, 0, 0 ));
		IS_MAC_OS_10_15_CATALINA_OR_LATER = (IS_MAC && osVersion >= toVersion( 10, 15, 0, 0 ));

		// Java versions
		long javaVersion = scanVersion( System.getProperty( "java.version" ) );
		IS_JAVA_9_OR_LATER = (javaVersion >= toVersion( 9, 0, 0, 0 ));

		// Java VMs
		IS_JETBRAINS_JVM = System.getProperty( "java.vm.vendor", "Unknown" )
			.toLowerCase( Locale.ENGLISH ).contains( "jetbrains" );

		// UI toolkits
		IS_KDE = (IS_LINUX && System.getenv( "KDE_FULL_SESSION" ) != null);
	}

	public static long scanVersion( String version ) {
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

	public static long toVersion( int major, int minor, int micro, int patch ) {
		return ((long) major << 48) + ((long) minor << 32) + ((long) micro << 16) + patch;
	}
}
