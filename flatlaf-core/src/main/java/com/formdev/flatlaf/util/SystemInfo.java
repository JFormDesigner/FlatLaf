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
	public static final boolean isWindows;
	public static final boolean isMacOS;
	public static final boolean isLinux;

	// OS versions
	public static final long osVersion;
	public static final boolean isWindows_10_orLater;
	public static final boolean isMacOS_10_11_ElCapitan_orLater;
	public static final boolean isMacOS_10_14_Mojave_orLater;
	public static final boolean isMacOS_10_15_Catalina_orLater;

	// Java versions
	public static final long javaVersion;
	public static final boolean isJava_9_orLater;
	public static final boolean isJava_11_orLater;
	public static final boolean isJava_15_orLater;

	// Java VMs
	public static final boolean isJetBrainsJVM;
	public static final boolean isJetBrainsJVM_11_orLater;

	// UI toolkits
	public static final boolean isKDE;

	static {
		// platforms
		String osName = System.getProperty( "os.name" ).toLowerCase( Locale.ENGLISH );
		isWindows = osName.startsWith( "windows" );
		isMacOS = osName.startsWith( "mac" );
		isLinux = osName.startsWith( "linux" );

		// OS versions
		osVersion = scanVersion( System.getProperty( "os.version" ) );
		isWindows_10_orLater = (isWindows && osVersion >= toVersion( 10, 0, 0, 0 ));
		isMacOS_10_11_ElCapitan_orLater = (isMacOS && osVersion >= toVersion( 10, 11, 0, 0 ));
		isMacOS_10_14_Mojave_orLater = (isMacOS && osVersion >= toVersion( 10, 14, 0, 0 ));
		isMacOS_10_15_Catalina_orLater = (isMacOS && osVersion >= toVersion( 10, 15, 0, 0 ));

		// Java versions
		javaVersion = scanVersion( System.getProperty( "java.version" ) );
		isJava_9_orLater = (javaVersion >= toVersion( 9, 0, 0, 0 ));
		isJava_11_orLater = (javaVersion >= toVersion( 11, 0, 0, 0 ));
		isJava_15_orLater = (javaVersion >= toVersion( 15, 0, 0, 0 ));

		// Java VMs
		isJetBrainsJVM = System.getProperty( "java.vm.vendor", "Unknown" )
			.toLowerCase( Locale.ENGLISH ).contains( "jetbrains" );
		isJetBrainsJVM_11_orLater = isJetBrainsJVM && isJava_11_orLater;

		// UI toolkits
		isKDE = (isLinux && System.getenv( "KDE_FULL_SESSION" ) != null);
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
