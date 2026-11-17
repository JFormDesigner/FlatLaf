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

import java.io.FileOutputStream

plugins {
	`cpp-library`
	`flatlaf-cpp-library`
	`flatlaf-jni-headers`
}

flatlafJniHeaders {
	headers = listOf(
		"com_formdev_flatlaf_ui_FlatNativeLibrary.h",
		"com_formdev_flatlaf_ui_FlatNativeLinuxLibrary.h"
	)
}

library {
	targetMachines = listOf(
		machines.linux.x86_64,
		machines.linux.architecture( "aarch64" ),
	)
}

// JDK used for JNI headers and libjawt.so (see flatlaf-jni-headers plugin)
val javaHome = flatlafJniHeaders.javaHome

interface InjectedOps {
	@get:Inject val fs: FileSystemOperations
	@get:Inject val e: ExecOperations
}

tasks {
	register( "build-natives" ) {
		group = "build"
		description = "Builds natives"

		if( System.getProperty( "os.name" ).lowercase().startsWith( "linux" ) ) {
			val osArch = System.getProperty( "os.arch" )
			if( osArch == "amd64" ) {
				dependsOn( "linkReleaseX86-64" )
				if( file( "/usr/bin/aarch64-linux-gnu-gcc" ).exists() )
					dependsOn( "linkCrossAarch64" )
			}
			if( osArch == "aarch64" )
				dependsOn( "linkReleaseAarch64" )
		}
	}

	withType<CppCompile>().configureEach {
		onlyIf { name.contains( "Release" ) }

		// generate and copy needed JNI headers
		dependsOn( "jni-headers" )

		includes.from(
			javaHome.dir( "include" ),
			javaHome.dir( "include/linux" ),

			// for GTK
			"/usr/include/gtk-3.0",
			"/usr/include/glib-2.0",
			if( name.contains( "X86-64" ) ) "/usr/lib/x86_64-linux-gnu/glib-2.0/include"
									   else "/usr/lib/aarch64-linux-gnu/glib-2.0/include",
			"/usr/include/gdk-pixbuf-2.0",
			"/usr/include/atk-1.0",
			"/usr/include/cairo",
			"/usr/include/pango-1.0",
			"/usr/include/harfbuzz",
		)

		compilerArgs.addAll( toolChain.map {
			when( it ) {
				// G_DISABLE_CAST_CHECKS: GTK cast macros (e.g. GTK_WINDOW()) become plain C casts
				//   (avoids runtime type checks, which increase library size if built with older GLib)
				is Gcc, is Clang -> listOf( "-fvisibility=hidden", "-DG_DISABLE_CAST_CHECKS" )
				else -> emptyList()
			}
		} )
	}

	withType<LinkSharedLibrary>().configureEach {
		onlyIf { name.contains( "Release" ) }

		val nativesDir = project( ":flatlaf-core" ).projectDir.resolve( "src/main/resources/com/formdev/flatlaf/natives" )
		val libraryName = if( name.contains( "X86-64" ) ) "libflatlaf-linux-x86_64.so" else "libflatlaf-linux-arm64.so"

		linkerArgs.addAll( toolChain.zip( javaHome ) { tc, jdkHome ->
			when( tc ) {
				is Gcc, is Clang -> listOf( "-L${jdkHome.dir( "lib" )}", "-ljawt", "-lgtk-3" )
				else -> emptyList()
			}
		} )

		val injectedOps = project.objects.newInstance<InjectedOps>()

		doLast {
			// copy shared library to flatlaf-core resources
			injectedOps.fs.copy {
				from( linkedFile )
				into( nativesDir )
				rename( linkedFile.get().asFile.name, libraryName )
			}
		}

//		dump( linkedFile, true, injectedOps )
	}

	if( System.getProperty( "os.name" ).lowercase().startsWith( "linux" ) &&
		System.getProperty( "os.arch" ) == "amd64" &&
		file( "/usr/bin/aarch64-linux-gnu-gcc" ).exists() )
	{
		register<Exec>( "compileCrossAarch64Cpp" ) {
			// generate and copy needed JNI headers
			dependsOn( "jni-headers" )

			val include = layout.projectDirectory.dir( "src/main/headers" )
			val sources = crossAarch64Sources()
			workingDir = file( layout.buildDirectory.dir( "obj/main/release/aarch64-cross" ) )

			doFirst {
				workingDir.mkdirs()
			}

			commandLine = listOf(
				"aarch64-linux-gnu-gcc",
				"-c",
				"-fPIC",
				"-fvisibility=hidden",
				"-DG_DISABLE_CAST_CHECKS",
				"-O3",
				"-I", "$include",

				// for GTK
				"-I", "/usr/include/gtk-3.0",
				"-I", "/usr/include/glib-2.0",
				"-I", "/usr/lib/x86_64-linux-gnu/glib-2.0/include",
				"-I", "/usr/include/gdk-pixbuf-2.0",
				"-I", "/usr/include/atk-1.0",
				"-I", "/usr/include/cairo",
				"-I", "/usr/include/pango-1.0",
				"-I", "/usr/include/harfbuzz",
			)

			// JDK include directories and all *.cpp files in src/main/cpp (resolved at execution time)
			val jdkInclude = javaHome.dir( "include" )
			argumentProviders.add( CommandLineArgumentProvider {
				val jdkIncludeDir = jdkInclude.get().asFile
				listOf( "-I", "$jdkIncludeDir", "-I", "$jdkIncludeDir/linux" ) +
					sources.files.map { it.absolutePath }.sorted()
			} )
		}

		register<Exec>( "linkCrossAarch64" ) {
			dependsOn( "compileCrossAarch64Cpp" )

			val nativesDir = project( ":flatlaf-core" ).projectDir.resolve( "src/main/resources/com/formdev/flatlaf/natives" )
			val libraryName = "libflatlaf-linux-arm64.so"
			val outDir = file( layout.buildDirectory.dir( "lib/main/release/aarch64-cross" ) )
			val objDir = file( layout.buildDirectory.dir( "obj/main/release/aarch64-cross" ) )
			val sources = crossAarch64Sources()
			val libs = listOf(
				"-lstdc++",
				"-L${layout.projectDirectory}/lib/aarch64",
				"-ljawt",
				"-lgtk-3",
			)

			doFirst {
				outDir.mkdirs()
			}

			executable = "aarch64-linux-gnu-gcc"
			args(
				"-shared",
				"-Wl,-soname,$libraryName",
				"-o", "$outDir/$libraryName",
			)

			// object files of all *.cpp files in src/main/cpp (resolved at execution time),
			// derived from source files to ignore stale object files of removed sources;
			// libraries must be specified after object files
			argumentProviders.add( CommandLineArgumentProvider {
				sources.files.map { "$objDir/${it.nameWithoutExtension}.o" }.sorted() + libs
			} )

			val injectedOps = project.objects.newInstance<InjectedOps>()

			doLast {
				// copy shared library to flatlaf-core resources
				injectedOps.fs.copy {
					from( "$outDir/$libraryName" )
					into( nativesDir )
				}
			}

//			dump( file( "$outDir/$libraryName" ), false, injectedOps )
		}
	}
}

/**
 * All C++ source files compiled by cross compile tasks.
 */
fun crossAarch64Sources(): FileTree = fileTree( "src/main/cpp" ) { include( "*.cpp" ) }

/*dump
fun Task.dump( f: Any, disassemble: Boolean, injectedOps: InjectedOps ) {
	doLast {
		val dylib = if( f is RegularFileProperty) f.get().asFile else f as File
		val dylibDir = dylib.parent
		injectedOps.e.exec { commandLine( "size", dylib ); standardOutput = FileOutputStream( "$dylibDir/size.txt" ) }
		injectedOps.e.exec {
			commandLine( "objdump",
				// commands
				"--archive-headers",
				"--section-headers",
				"--private-headers",
				"--reloc",
				"--dynamic-reloc",
				"--syms",
				// files
				dylib )
			standardOutput = FileOutputStream( "$dylibDir/objdump.txt" )
		}
		if( disassemble )
			injectedOps.e.exec { commandLine( "objdump", "--disassemble-all", dylib ); standardOutput = FileOutputStream( "$dylibDir/disassemble.txt" ) }
		injectedOps.e.exec { commandLine( "objdump", "--full-contents", dylib ); standardOutput = FileOutputStream( "$dylibDir/full-contents.txt" ) }
		injectedOps.e.exec { commandLine( "hexdump", dylib ); standardOutput = FileOutputStream( "$dylibDir/hexdump.txt" ) }
	}
}
dump*/
