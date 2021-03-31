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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Helper class to load native library (.dll, .so or .dylib) stored in Jar.
 * <p>
 * Copies native library to users temporary folder before loading it.
 *
 * @author Karl Tauber
 * @since 1.1
 */
public class NativeLibrary
{
	private static final String DELETE_SUFFIX = ".delete";
	private static boolean deletedTemporary;

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

		File tempFile = null;
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
			Path tempPath = createTempFile( libraryName );
			tempFile = tempPath.toFile();

			// copy library to temporary file
			try( InputStream in = libraryUrl.openStream() ) {
				Files.copy( in, tempPath, StandardCopyOption.REPLACE_EXISTING );
			}

			// load library
			System.load( tempFile.getCanonicalPath() );

			// delete library
			deleteOrMarkForDeletion( tempFile );

			return true;
		} catch( Throwable ex ) {
			log( null, ex );

			if( tempFile != null )
				deleteOrMarkForDeletion( tempFile );
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

	private static void log( String msg, Throwable thrown ) {
		LoggingFacade.INSTANCE.logSevere( msg, thrown );
	}

	private static Path createTempFile( String libraryName ) throws IOException {
		int sep = libraryName.lastIndexOf( '/' );
		String name = (sep >= 0) ? libraryName.substring( sep + 1 ) : libraryName;

		int dot = name.lastIndexOf( '.' );
		String prefix = ((dot >= 0) ? name.substring( 0, dot ) : name) + '-';
		String suffix = (dot >= 0) ? name.substring( dot ) : "";

		Path tempDir = getTempDir();
		if( tempDir != null ) {
			deleteTemporaryFiles( tempDir );

			return Files.createTempFile( tempDir, prefix, suffix );
		} else
			return Files.createTempFile( prefix, suffix );
	}

	private static Path getTempDir() throws IOException {
		if( SystemInfo.isWindows ) {
			// On Windows, where File.delete() and File.deleteOnExit() does not work
			// for loaded native libraries, they will be deleted on next application startup.
			// The default temporary directory may contain hundreds or thousands of files.
			// To make searching for "marked for deletion" files as fast as possible,
			// use a sub directory that contains only our temporary native libraries.
			Path tempDir = Paths.get( System.getProperty( "java.io.tmpdir" ) + "/flatlaf.temp" );
			Files.createDirectories( tempDir );
			return tempDir;
		} else
			return null; // use standard temporary directory
	}

	private static void deleteTemporaryFiles( Path tempDir ) {
		if( deletedTemporary )
			return;
		deletedTemporary = true;

		File[] markerFiles = tempDir.toFile().listFiles( (dir, name) -> name.endsWith( DELETE_SUFFIX ) );
		if( markerFiles == null )
			return;

		for( File markerFile : markerFiles ) {
			File toDeleteFile = new File( markerFile.getParent(), StringUtils.removeTrailing( markerFile.getName(), DELETE_SUFFIX ) );
			if( !toDeleteFile.exists() || toDeleteFile.delete() )
				markerFile.delete();
		}
	}

	private static void deleteOrMarkForDeletion( File file ) {
		// try to delete the native library
		if( file.delete() )
			return;

		// not possible to delete on Windows because native library file is locked
		// --> create "to delete" marker file (used at next startup)
		try {
			File markFile = new File( file.getParent(), file.getName() + DELETE_SUFFIX );
			markFile.createNewFile();
		} catch( IOException ex2 ) {
			// ignore
		}
	}
}
