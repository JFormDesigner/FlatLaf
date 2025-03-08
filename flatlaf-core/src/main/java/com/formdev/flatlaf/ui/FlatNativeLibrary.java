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
import java.net.URL;
import java.security.CodeSource;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.NativeLibrary;
import com.formdev.flatlaf.util.StringUtils;
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
	private static boolean initialized;
	private static NativeLibrary nativeLibrary;

	private native static int getApiVersion();

	/**
	 * Loads native library (if available) and returns whether loaded successfully.
	 * Returns {@code false} if no native library is available.
	 */
	static synchronized boolean isLoaded( int apiVersion ) {
		initialize( apiVersion );
		return (nativeLibrary != null) ? nativeLibrary.isLoaded() : false;
	}

	private static void initialize( int apiVersion ) {
		if( initialized )
			return;
		initialized = true;

		if( !FlatSystemProperties.getBoolean( FlatSystemProperties.USE_NATIVE_LIBRARY, true ) )
			return;

		String classifier;
		String ext;
		boolean unknownArch = false;
		if( SystemInfo.isWindows_10_orLater && (SystemInfo.isX86 || SystemInfo.isX86_64 || SystemInfo.isAARCH64) ) {
			// Windows: requires Windows 10/11 (x86, x86_64 or aarch64)

			if( SystemInfo.isAARCH64 )
				classifier = "windows-arm64";
			else if( SystemInfo.isX86_64 )
				classifier = "windows-x86_64";
			else
				classifier = "windows-x86";

			ext = "dll";

			// Do not load jawt.dll (part of JRE) here explicitly because
			// the FlatLaf native library flatlaf.dll may be loaded very early on Windows
			// (e.g. from class com.formdev.flatlaf.util.SystemInfo) and before AWT is
			// initialized (and awt.dll is loaded). Loading jawt.dll also loads awt.dll.
			// In Java 8, loading jawt.dll before AWT is initialized may load
			// a wrong version of awt.dll if a newer Java version (e.g. 19)
			// is in PATH environment variable. Then Java 19 awt.dll and Java 8 awt.dll
			// are loaded at same time and calling JAWT_GetAWT() crashes the application.
			//
			// To avoid this, flatlaf.dll is not linked to jawt.dll,
			// which avoids loading jawt.dll when flatlaf.dll is loaded.
			// Instead, flatlaf.dll dynamically loads jawt.dll when first used,
			// which is guaranteed after AWT initialization.

		} else if( SystemInfo.isMacOS_10_14_Mojave_orLater && (SystemInfo.isAARCH64 || SystemInfo.isX86_64) ) {
			// macOS: requires macOS 10.14 or later (arm64 or x86_64)

			classifier = SystemInfo.isAARCH64 ? "macos-arm64" : "macos-x86_64";
			ext = "dylib";

		} else if( SystemInfo.isLinux ) {
			// Linux: x86_64 or aarch64 (but also supports unknown architectures)

			classifier = SystemInfo.isAARCH64 ? "linux-arm64"
				: (SystemInfo.isX86_64 ? "linux-x86_64"
					: "linux-" + sanitize( System.getProperty( "os.arch" ) ));
			ext = "so";
			unknownArch = !SystemInfo.isX86_64 && !SystemInfo.isAARCH64;

			// Load libjawt.so (part of JRE) explicitly because it is not found
			// in all Java versions/distributions.
			// E.g. not found in Java 13 and later from openjdk.java.net.
			// There seems to be also differences between distributions.
			// E.g. Adoptium Java 17 does not need this, but Java 17 from openjdk.java.net does.
			loadJAWT();
		} else
			return; // no native library available for current OS or CPU architecture

		// load native library
		NativeLibrary nativeLibrary = createNativeLibrary( classifier, ext, unknownArch );
		if( !nativeLibrary.isLoaded() )
			return;

		// check API version (and check whether library works)
		try {
			int actualApiVersion = getApiVersion();
			if( actualApiVersion != apiVersion ) {
				LoggingFacade.INSTANCE.logSevere( "FlatLaf: Wrong API version in native library (expected "
					+ apiVersion + ", actual " + actualApiVersion + "). Ignoring native library.", null );
				return;
			}
		} catch( Throwable ex ) {
			// could be a UnsatisfiedLinkError in case that loading native library
			// from temp directory was blocked by some OS security mechanism
			LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to get API version of native library. Ignoring native library.", ex );
			return;
		}

		FlatNativeLibrary.nativeLibrary = nativeLibrary;
	}

	private static NativeLibrary createNativeLibrary( String classifier, String ext, boolean unknownArch ) {
		String libraryName = "flatlaf-" + classifier;

		// load from "java.library.path" or from path specified in system property "flatlaf.nativeLibraryPath"
		String libraryPath = System.getProperty( FlatSystemProperties.NATIVE_LIBRARY_PATH );
		if( libraryPath != null ) {
			if( "system".equals( libraryPath ) ) {
				NativeLibrary library = new NativeLibrary( libraryName, true );
				if( library.isLoaded() )
					return library;

				if( !unknownArch ) {
					LoggingFacade.INSTANCE.logSevere( "FlatLaf: Native library '" + System.mapLibraryName( libraryName )
						+ "' not found in java.library.path '" + System.getProperty( "java.library.path" )
						+ "'. Using extracted native library instead.", null );
				}
			} else {
				// try standard library naming scheme
				// (same as in flatlaf.jar in package 'com/formdev/flatlaf/natives')
				File libraryFile = new File( libraryPath, System.mapLibraryName( libraryName ) );
				if( libraryFile.exists() )
					return new NativeLibrary( libraryFile, true );

				// try Maven naming scheme
				// (see https://www.formdev.com/flatlaf/native-libraries/)
				String libraryName2 = null;
				File jarFile = getJarFile();
				if( jarFile != null ) {
					libraryName2 = buildLibraryName( jarFile, classifier, ext );
					File libraryFile2 = new File( libraryPath, libraryName2 );
					if( libraryFile2.exists() )
						return new NativeLibrary( libraryFile2, true );
				}

				if( !unknownArch ) {
					LoggingFacade.INSTANCE.logSevere( "FlatLaf: Native library '"
						+ libraryFile.getName()
						+ (libraryName2 != null ? ("' or '" + libraryName2) : "")
						+ "' not found in '" + libraryFile.getParentFile().getAbsolutePath()
						+ "'. Using extracted native library instead.", null );
				}
			}
		}

		// load from beside the FlatLaf jar
		// e.g. for flatlaf-3.1.jar, load flatlaf-3.1-windows-x86_64.dll (from same directory)
		File libraryFile = findLibraryBesideJar( classifier, ext );
		if( libraryFile != null )
			return new NativeLibrary( libraryFile, true );

		// load from FlatLaf jar (extract native library to temp folder)
		return new NativeLibrary( "com/formdev/flatlaf/natives/" + libraryName, null, !unknownArch );
	}

	/**
	 * Search for a native library beside the jar that contains this class
	 * (usually the FlatLaf jar).
	 * The native library must be in the same directory (or in "../bin" if jar is in "lib")
	 * as the jar and have the same basename as the jar.
	 * If FlatLaf jar is repackaged into fat/uber application jar, "-flatlaf" is appended to jar basename.
	 * The classifier and the extension are appended to the jar basename.
	 *
	 * E.g.
	 *     flatlaf-3.1.jar
	 *     flatlaf-3.1-windows-x86_64.dll
	 *     flatlaf-3.1-linux-x86_64.so
	 */
	private static File findLibraryBesideJar( String classifier, String ext ) {
		// get location of FlatLaf jar (or fat/uber application jar)
		File jarFile = getJarFile();
		if( jarFile == null )
			return null;

		// build library file
		String libraryName = buildLibraryName( jarFile, classifier, ext );
		File jarDir = jarFile.getParentFile();

		// check whether native library exists in same directory as jar
		File libraryFile = new File( jarDir, libraryName );
		if( libraryFile.isFile() )
			return libraryFile;

		// if jar is in "lib" directory, then also check whether native library exists
		// in "../bin" directory
		if( jarDir.getName().equalsIgnoreCase( "lib" ) ) {
			libraryFile = new File( jarDir.getParentFile(), "bin/" + libraryName );
			if( libraryFile.isFile() )
				return libraryFile;
		}

		// special case: support Gradle cache when running in development environment
		//   <user-home>/.gradle/caches/modules-2/files-2.1/com.formdev/flatlaf/<version>/<hash-1>/flatlaf-<version>.jar
		//   <user-home>/.gradle/caches/modules-2/files-2.1/com.formdev/flatlaf/<version>/<hash-2>/flatlaf-<version>-windows-x86_64.dll
		String path = jarDir.getAbsolutePath().replace( '\\', '/' );
		if( path.contains( "/.gradle/caches/" ) ) {
			File versionDir = jarDir.getParentFile();
			if( libraryName.contains( versionDir.getName() ) ) {
				File[] dirs = versionDir.listFiles();
				if( dirs != null ) {
					for( File dir : dirs ) {
						libraryFile = new File( dir, libraryName );
						if( libraryFile.isFile() )
							return libraryFile;
					}
				}
			}
		}

		// native library not found
		return null;
	}

	private static File getJarFile() {
		try {
			// get location of FlatLaf jar
			CodeSource codeSource = FlatNativeLibrary.class.getProtectionDomain().getCodeSource();
			URL jarUrl = (codeSource != null) ? codeSource.getLocation() : null;
			if( jarUrl == null )
				return null;

			// if url is not a file, then we're running in a special environment (e.g. WebStart)
			if( !"file".equals( jarUrl.getProtocol() ) )
				return null;

			File jarFile = new File( jarUrl.toURI() );

			// if jarFile is a directory, then we're in a development environment
			if( !jarFile.isFile() )
				return null;

			return jarFile;
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( ex.getMessage(), ex );
			return null;
		}
	}

	private static String buildLibraryName( File jarFile, String classifier, String ext ) {
		String jarName = jarFile.getName();
		String jarBasename = jarName.substring( 0, jarName.lastIndexOf( '.' ) );

		// remove classifier "no-natives" (if used)
		jarBasename = StringUtils.removeTrailing( jarBasename, "-no-natives" );

		return jarBasename
			+ (jarBasename.contains( "flatlaf" ) ? "" : "-flatlaf")
			+ '-' + classifier + '.' + ext;
	}

	/**
	 * Allow only 'a'-'z', 'A'-'Z', '0'-'9', '_' and '-' in filenames.
	 */
	private static String sanitize( String s ) {
		return s.replaceAll( "[^a-zA-Z0-9_-]", "_" );
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
