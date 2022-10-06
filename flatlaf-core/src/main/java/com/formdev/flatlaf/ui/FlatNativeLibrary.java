/*
 * Copyright 2022 FormDev Software GmbH
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

package com.formdev.flatlaf.ui;

import java.io.File;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.NativeLibrary;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Helper class to load FlatLaf native library (.dll, .so or .dylib),
 * if available for current operating system and CPU architecture.
 *
 * @author Karl Tauber
 * @since 2.3
 */
class FlatNativeLibrary
{
	private static NativeLibrary nativeLibrary;

	/**
	 * Loads native library (if available) and returns whether loaded successfully.
	 * Returns {@code false} if no native library is available.
	 */
	static synchronized boolean isLoaded() {
		initialize();
		return (nativeLibrary != null) ? nativeLibrary.isLoaded() : false;
	}

	private static void initialize() {
		if( nativeLibrary != null )
			return;

		String libraryName;
		if( SystemInfo.isWindows_10_orLater && (SystemInfo.isX86 || SystemInfo.isX86_64) ) {
			// Windows: requires Windows 10/11 (x86 or x86_64)

			libraryName = "flatlaf-windows-x86";
			if( SystemInfo.isX86_64 )
				libraryName += "_64";

			// In Java 8, load jawt.dll (part of JRE) explicitly because it
			// is not found when running application with <jdk>/bin/java.exe.
			// When using <jdk>/jre/bin/java.exe, it is found.
			// jawt.dll is located in <jdk>/jre/bin/.
			// Java 9 and later do not have this problem,
			// but load jawt.dll anyway to be on the safe side.
			loadJAWT();
		} else if( SystemInfo.isLinux && SystemInfo.isX86_64 ) {
			// Linux: requires x86_64

			libraryName = "flatlaf-linux-x86_64";

			// Load libjawt.so (part of JRE) explicitly because it is not found
			// in all Java versions/distributions.
			// E.g. not found in Java 13 and later from openjdk.java.net.
			// There seems to be also differences between distributions.
			// E.g. Adoptium Java 17 does not need this, but Java 17 from openjdk.java.net does.
			loadJAWT();
		} else
			return; // no native library available for current OS or CPU architecture

		// load native library
		nativeLibrary = createNativeLibrary( libraryName );
	}

	private static NativeLibrary createNativeLibrary( String libraryName ) {
		String libraryPath = System.getProperty( FlatSystemProperties.NATIVE_LIBRARY_PATH );
		if( libraryPath != null ) {
			if( "system".equals( libraryPath ) ) {
				NativeLibrary library = new NativeLibrary( libraryName, true );
				if( library.isLoaded() )
					return library;

				LoggingFacade.INSTANCE.logSevere( "Did not find library " + libraryName + " in java.library.path, using extracted library instead", null );
			} else {
				File libraryFile = new File( libraryPath, System.mapLibraryName( libraryName ) );
				if( libraryFile.exists() )
					return new NativeLibrary( libraryFile, true );

				LoggingFacade.INSTANCE.logSevere( "Did not find external library " + libraryFile + ", using extracted library instead", null );
			}
		}

		return new NativeLibrary( "com/formdev/flatlaf/natives/" + libraryName, null, true );
	}

	private static void loadJAWT() {
		try {
			System.loadLibrary( "jawt" );
		} catch( UnsatisfiedLinkError ex ) {
			// log error only if native library jawt.dll not already loaded
			String message = ex.getMessage();
			if( message == null || !message.contains( "already loaded in another classloader" ) )
				LoggingFacade.INSTANCE.logSevere( message, ex );
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( ex.getMessage(), ex );
		}
	}
}
