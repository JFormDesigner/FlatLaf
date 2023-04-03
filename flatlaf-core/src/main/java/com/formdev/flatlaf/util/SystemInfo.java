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
import com.formdev.flatlaf.ui.FlatNativeWindowsLibrary;

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
	/** @since 2 */ public static final boolean isWindows_11_orLater;
	public static final boolean isMacOS_10_11_ElCapitan_orLater;
	public static final boolean isMacOS_10_14_Mojave_orLater;
	public static final boolean isMacOS_10_15_Catalina_orLater;

	// OS architecture
	/** @since 2 */ public static final boolean isX86;
	/** @since 1.1 */ public static final boolean isX86_64;
	/** @since 2 */ public static final boolean isAARCH64;

	// Java versions
	public static final long javaVersion;
	public static final boolean isJava_9_orLater;
	public static final boolean isJava_11_orLater;
	/** @since 2.3 */ public static final boolean isJava_12_orLater;
	public static final boolean isJava_15_orLater;
	/** @since 2 */ public static final boolean isJava_17_orLater;
	/** @since 2 */ public static final boolean isJava_18_orLater;

	// Java VMs
	public static final boolean isJetBrainsJVM;
	public static final boolean isJetBrainsJVM_11_orLater;

	// UI toolkits
	public static final boolean isKDE;

	// other
	/** @since 1.1 */ public static final boolean isProjector;
	/** @since 1.1.2 */ public static final boolean isWebswing;
	/** @since 1.1.1 */ public static final boolean isWinPE;

	// features
	/** @since 2.3 */ public static final boolean isMacFullWindowContentSupported;

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

		// OS architecture
		String osArch = System.getProperty( "os.arch" );
		isX86 = osArch.equals( "x86" );
		isX86_64 = osArch.equals( "amd64" ) || osArch.equals( "x86_64" );
		isAARCH64 = osArch.equals( "aarch64" );

		// Java versions
		javaVersion = scanVersion( System.getProperty( "java.version" ) );
		isJava_9_orLater = (javaVersion >= toVersion( 9, 0, 0, 0 ));
		isJava_11_orLater = (javaVersion >= toVersion( 11, 0, 0, 0 ));
		isJava_12_orLater = (javaVersion >= toVersion( 12, 0, 0, 0 ));
		isJava_15_orLater = (javaVersion >= toVersion( 15, 0, 0, 0 ));
		isJava_17_orLater = (javaVersion >= toVersion( 17, 0, 0, 0 ));
		isJava_18_orLater = (javaVersion >= toVersion( 18, 0, 0, 0 ));

		// Java VMs
		isJetBrainsJVM = System.getProperty( "java.vm.vendor", "Unknown" )
			.toLowerCase( Locale.ENGLISH ).contains( "jetbrains" );
		isJetBrainsJVM_11_orLater = isJetBrainsJVM && isJava_11_orLater;

		// UI toolkits
		isKDE = (isLinux && System.getenv( "KDE_FULL_SESSION" ) != null);

		// other
		isProjector = Boolean.getBoolean( "org.jetbrains.projector.server.enable" );
		isWebswing = (System.getProperty( "webswing.rootDir" ) != null);
		isWinPE = isWindows && "X:\\Windows\\System32".equalsIgnoreCase( System.getProperty( "user.dir" ) );

		// features
		// available since Java 12; backported to Java 11.0.8 and 8u292
		isMacFullWindowContentSupported = isMacOS &&
			(javaVersion >= toVersion( 11, 0, 8, 0 ) ||
			 (javaVersion >= toVersion( 1, 8, 0, 292 ) && !isJava_9_orLater));


		// Note: Keep following at the end of this block because (optional) loading
		//       of native library uses fields of this class. E.g. isX86_64

		// Windows 11 detection is implemented in Java 8u321, 11.0.14, 17.0.2 and 18 (or later).
		// (see https://bugs.openjdk.java.net/browse/JDK-8274840)
		// For older Java versions, use native library to get OS build number.
		boolean isWin_11_orLater = false;
		try {
			isWin_11_orLater = isWindows_10_orLater &&
				(scanVersion( StringUtils.removeLeading( osName, "windows " ) ) >= toVersion( 11, 0, 0, 0 ) ||
				 (FlatNativeWindowsLibrary.isLoaded() && FlatNativeWindowsLibrary.getOSBuildNumber() >= 22000));
		} catch( Throwable ex ) {
			// catch to avoid that application can not start if native library is not up-to-date
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
		isWindows_11_orLater = isWin_11_orLater;
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
