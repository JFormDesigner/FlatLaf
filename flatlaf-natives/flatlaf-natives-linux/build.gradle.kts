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

var javaHome = System.getProperty( "java.home" )
if( javaHome.endsWith( "jre" ) && !file( "${javaHome}/include" ).exists() )
	javaHome += "/.."

tasks {
	register( "build-natives" ) {
		group = "build"
		description = "Builds natives"

		if( org.gradle.internal.os.OperatingSystem.current().isLinux ) {
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
			"${javaHome}/include",
			"${javaHome}/include/linux",

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
				is Gcc, is Clang -> listOf( "-fvisibility=hidden" )
				else -> emptyList()
			}
		} )

		doFirst {
			// check required Java version
			if( JavaVersion.current() < JavaVersion.VERSION_11 ) {
				println()
				println( "WARNING: Java 11 or later required to build Linux native library (running ${System.getProperty( "java.version" )})" )
				println( "         Native library built with older Java versions throw following exception when running in Java 17+:" )
				println( "         java.lang.UnsatisfiedLinkError: .../libjawt.so: version `SUNWprivate_1.1' not found" )
				println()
			}
		}
	}

	withType<LinkSharedLibrary>().configureEach {
		onlyIf { name.contains( "Release" ) }

		val nativesDir = project( ":flatlaf-core" ).projectDir.resolve( "src/main/resources/com/formdev/flatlaf/natives" )
		val libraryName = if( name.contains( "X86-64" ) ) "libflatlaf-linux-x86_64.so" else "libflatlaf-linux-arm64.so"
		val jawt = "jawt"
		var jawtPath = "${javaHome}/lib"
		if( JavaVersion.current() == JavaVersion.VERSION_1_8 )
			jawtPath += "/amd64"

		linkerArgs.addAll( toolChain.map {
			when( it ) {
				is Gcc, is Clang -> listOf( "-L${jawtPath}", "-l${jawt}", "-lgtk-3" )
				else -> emptyList()
			}
		} )

		doLast {
			// copy shared library to flatlaf-core resources
			copy {
				from( linkedFile )
				into( nativesDir )
				rename( linkedFile.get().asFile.name, libraryName )
			}

//			dump( linkedFile.asFile.get(), true )
		}
	}

	if( org.gradle.internal.os.OperatingSystem.current().isLinux &&
		System.getProperty( "os.arch" ) == "amd64" &&
		file( "/usr/bin/aarch64-linux-gnu-gcc" ).exists() )
	{
		register<Exec>( "compileCrossAarch64Cpp" ) {
			val include = layout.projectDirectory.dir( "src/main/headers" )
			val src = layout.projectDirectory.dir( "src/main/cpp" )
			workingDir = file( layout.buildDirectory.dir( "obj/main/release/aarch64-cross" ) )

			doFirst {
				workingDir.mkdirs()
			}

			commandLine = listOf(
				"aarch64-linux-gnu-gcc",
				"-c",
				"-fPIC",
				"-fvisibility=hidden",
				"-O3",
				"-I", "${javaHome}/include",
				"-I", "${javaHome}/include/linux",
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

				"$src/ApiVersion.cpp",
				"$src/GtkFileChooser.cpp",
				"$src/GtkMessageDialog.cpp",
				"$src/JNIUtils.cpp",
				"$src/X11WmUtils.cpp",
			)
		}

		register<Exec>( "linkCrossAarch64" ) {
			dependsOn( "compileCrossAarch64Cpp" )

			val nativesDir = project( ":flatlaf-core" ).projectDir.resolve( "src/main/resources/com/formdev/flatlaf/natives" )
			val libraryName = "libflatlaf-linux-arm64.so"
			val outDir = file( layout.buildDirectory.dir( "lib/main/release/aarch64-cross" ) )
			val objDir = file( layout.buildDirectory.dir( "obj/main/release/aarch64-cross" ) )

			doFirst {
				outDir.mkdirs()
			}

			commandLine = listOf(
				"aarch64-linux-gnu-gcc",
				"-shared",
				"-Wl,-soname,$libraryName",
				"-o", "$outDir/$libraryName",

				"$objDir/ApiVersion.o",
				"$objDir/GtkFileChooser.o",
				"$objDir/GtkMessageDialog.o",
				"$objDir/JNIUtils.o",
				"$objDir/X11WmUtils.o",

				"-lstdc++",
				"-L${layout.projectDirectory}/lib/aarch64",
				"-ljawt",
				"-lgtk-3",
			)

			doLast {
				// copy shared library to flatlaf-core resources
				copy {
					from( "$outDir/$libraryName" )
					into( nativesDir )
				}

//				dump( file( "$outDir/$libraryName" ), false )
			}
		}
	}
}

/*dump
interface InjectedExecOps { @get:Inject val execOps: ExecOperations }
val injected = project.objects.newInstance<InjectedExecOps>()

fun dump( dylib: File, disassemble: Boolean ) {

	val dylibDir = dylib.parent
	injected.execOps.exec { commandLine( "size", dylib ); standardOutput = FileOutputStream( "$dylibDir/size.txt" ) }
	injected.execOps.exec {
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
		injected.execOps.exec { commandLine( "objdump", "--disassemble-all", dylib ); standardOutput = FileOutputStream( "$dylibDir/disassemble.txt" ) }
	injected.execOps.exec { commandLine( "objdump", "--full-contents", dylib ); standardOutput = FileOutputStream( "$dylibDir/full-contents.txt" ) }
	injected.execOps.exec { commandLine( "hexdump", dylib ); standardOutput = FileOutputStream( "$dylibDir/hexdump.txt" ) }
}
dump*/
