/*
 * Copyright 2021 FormDev Software GmbH
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

package com.formdev.flatlaf.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.formdev.flatlaf.FlatLaf;

/**
 * Helper class to load native library (.dll, .so or .dylib) stored in Jar.
 * <p>
 * Copies native library to users temporary folder before loading it.
 *
 * @author Karl Tauber
 */
public class NativeLibrary
{
	private final boolean loaded;

	/**
	 * Load native library from given classloader.
	 *
	 * @param libraryName resource name of the native library (without "lib" prefix and without extension)
	 * @param classLoader the classloader used to locate the library
	 * @param supported whether the native library is supported on the current platform
	 */
	public NativeLibrary( String libraryName, ClassLoader classLoader, boolean supported ) {
		this.loaded = supported
			? loadLibraryFromJar( libraryName, classLoader )
			: false;
	}

	/**
	 * Returns whether the native library is loaded.
	 * <p>
	 * Returns {@code false} if not supported on current platform as specified in constructor
	 * or if loading failed.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	private static boolean loadLibraryFromJar( String libraryName, ClassLoader classLoader ) {
		// add prefix and suffix to library name
		libraryName = decorateLibraryName( libraryName );

		// find library
		URL libraryUrl = classLoader.getResource( libraryName );
		if( libraryUrl == null ) {
			log( "Library '" + libraryName + "' not found", null );
			return false;
		}

		try {
			// for development environment
			if( "file".equals( libraryUrl.getProtocol() ) ) {
				File libraryFile = new File( libraryUrl.getPath() );
				if( libraryFile.isFile() ) {
					// load library without copying
					System.load( libraryFile.getCanonicalPath() );
					return true;
				}
			}

			// create temporary file
			Path tempPath = Files.createTempFile( "jni", basename( libraryName ) );
			File tempFile = tempPath.toFile();

			//TODO this does not work on Windows
			tempFile.deleteOnExit();

			// copy library to temporary file
			try( InputStream in = libraryUrl.openStream() ) {
				Files.copy( in, tempPath, StandardCopyOption.REPLACE_EXISTING );
			}

			// load library
			System.load( tempFile.getCanonicalPath() );

			return true;
		} catch( Throwable ex ) {
			log( null, ex );
			return false;
		}
	}

	private static String decorateLibraryName( String libraryName ) {
		if( SystemInfo.isWindows )
			return libraryName.concat( ".dll" );

		String suffix = SystemInfo.isMacOS ? ".dylib" : ".so";

		int sep = libraryName.lastIndexOf( '/' );
		return (sep >= 0)
			? libraryName.substring( 0, sep + 1 ) + "lib" + libraryName.substring( sep + 1 ) + suffix
			: "lib" + libraryName + suffix;
	}

	private static String basename( String libName ) {
		int sep = libName.lastIndexOf( '/' );
		return (sep >= 0) ? libName.substring( sep + 1 ) : libName;
	}

	private static void log( String msg, Throwable thrown ) {
		Logger.getLogger( FlatLaf.class.getName() ).log( Level.SEVERE, msg, thrown );
	}
}
