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
import java.nio.file.FileAlreadyExistsException;
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
	 * <p>
	 * Note regarding Java Platform Module System (JPMS):
	 * If classloader is {@code null}, the library can be only loaded from the module
	 * that contains this class.
	 * If classloader is not {@code null}, then the package that contains the library
	 * must be specified as "open" in module-info.java of the module that contains the library.
	 *
	 * @param libraryName resource name of the native library (without "lib" prefix and without extension)
	 * @param classLoader the classloader used to locate the library, or {@code null}
	 * @param supported whether the native library is supported on the current platform
	 */
	public NativeLibrary( String libraryName, ClassLoader classLoader, boolean supported ) {
		this.loaded = supported
			? loadLibraryFromJar( libraryName, classLoader )
			: false;
	}

	/**
	 * Load native library from given file.
	 *
	 * @param libraryFile the file of the native library
	 * @param supported whether the native library is supported on the current platform
	 * @since 2
	 */
	public NativeLibrary( File libraryFile, boolean supported ) {
		this.loaded = supported
			? loadLibraryFromFile( libraryFile )
			: false;
	}

	/**
	 * Load native library using {@link System#loadLibrary(String)}.
	 * Searches for the library in classloader of caller
	 * (using {@link ClassLoader#findLibrary(String)}) and in paths specified
	 * in system properties {@code sun.boot.library.path} and {@code java.library.path}.
	 *
	 * @param libraryName name of the native library (without "lib" prefix and without extension)
	 * @param supported whether the native library is supported on the current platform
	 * @since 2.6
	 */
	public NativeLibrary( String libraryName, boolean supported ) {
		this.loaded = supported
			? loadLibraryFromSystem( libraryName )
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
		URL libraryUrl = (classLoader != null)
			? classLoader.getResource( libraryName )
			: NativeLibrary.class.getResource( "/" + libraryName );
		if( libraryUrl == null ) {
			LoggingFacade.INSTANCE.logSevere( "Library '" + libraryName + "' not found", null );
			return false;
		}

		File tempFile = null;
		try {
			// for development environment
			if( "file".equals( libraryUrl.getProtocol() ) ) {
				String binPath = libraryUrl.getPath();
				String srcPath = binPath.replace( "flatlaf-core/bin/main/", "flatlaf-core/src/main/resources/" );
				File libraryFile = new File( srcPath ); // use from 'src' folder if available
				if( !libraryFile.isFile() )
					libraryFile = new File( binPath ); // use from 'bin' or 'output' folder if available
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
			LoggingFacade.INSTANCE.logSevere( ex.getMessage(), ex );

			if( tempFile != null )
				deleteOrMarkForDeletion( tempFile );
			return false;
		}
	}

	private boolean loadLibraryFromFile( File libraryFile ) {
		try {
			System.load( libraryFile.getAbsolutePath() );
			return true;
		} catch( Throwable ex ) {
			LoggingFacade.INSTANCE.logSevere( ex.getMessage(), ex );
			return false;
		}
	}

	private boolean loadLibraryFromSystem( String libraryName ) {
		try {
			System.loadLibrary( libraryName );
			return true;
		} catch( Throwable ex ) {
			String message = ex.getMessage();

			// do not log error if library was not found
			// thrown in ClassLoader.loadLibrary(Class<?> fromClass, String name, boolean isAbsolute)
			if( ex instanceof UnsatisfiedLinkError && message != null && message.contains( "java.library.path" ) )
				return false;

			LoggingFacade.INSTANCE.logSevere( message, ex );
			return false;
		}
	}

	/**
	 * Add prefix and suffix to library name.
	 * <ul>
	 * <li>Windows: libraryName + ".dll"
	 * <li>macOS: "lib" + libraryName + ".dylib"
	 * <li>Linux: "lib" + libraryName + ".so"
	 * </ul>
	 */
	private static String decorateLibraryName( String libraryName ) {
		int sep = libraryName.lastIndexOf( '/' );
		return (sep >= 0)
			? libraryName.substring( 0, sep + 1 ) + System.mapLibraryName( libraryName.substring( sep + 1 ) )
			: System.mapLibraryName( libraryName );
	}

	private static Path createTempFile( String libraryName ) throws IOException {
		int sep = libraryName.lastIndexOf( '/' );
		String name = (sep >= 0) ? libraryName.substring( sep + 1 ) : libraryName;

		int dot = name.lastIndexOf( '.' );
		String prefix = ((dot >= 0) ? name.substring( 0, dot ) : name) + '-';
		String suffix = (dot >= 0) ? name.substring( dot ) : "";

		Path tempDir = getTempDir();

		// Note:
		// Not using Files.createTempFile() here because it uses random number generator SecureRandom,
		// which may take 5-10 seconds to initialize under particular conditions.

		// Use current time in nanoseconds instead of a random number.
		// To avoid (theoretical) collisions, append a counter.
		long nanoTime = System.nanoTime();
		for( int i = 0;; i++ ) {
			String s = prefix + Long.toUnsignedString( nanoTime ) + i + suffix;
			try {
				return Files.createFile( tempDir.resolve( s ) );
			} catch( FileAlreadyExistsException ex ) {
				// ignore --> increment counter and try again
			}
		}
	}

	private static Path getTempDir() throws IOException {
		// get standard temporary directory
		String tmpdir = System.getProperty( "java.io.tmpdir" );

		if( SystemInfo.isWindows ) {
			// On Windows, where File.delete() and File.deleteOnExit() does not work
			// for loaded native libraries, they will be deleted on next application startup.
			// The default temporary directory may contain hundreds or thousands of files.
			// To make searching for "marked for deletion" files as fast as possible,
			// use a subdirectory that contains only our temporary native libraries.
			tmpdir += "\\flatlaf.temp";
		}

		// create temporary directory
		Path tempDir = Paths.get( tmpdir );
		Files.createDirectories( tempDir );

		// delete no longer needed temporary files (from already exited applications)
		if( SystemInfo.isWindows )
			deleteTemporaryFiles( tempDir );

		return tempDir;
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
