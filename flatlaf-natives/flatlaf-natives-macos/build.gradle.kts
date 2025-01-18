/*
 * Copyright 2023 FormDev Software GmbH
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

val minOsARM64 = "11.0"
val minOsX86_64 = "10.14"

plugins {
	`cpp-library`
	`flatlaf-cpp-library`
	`flatlaf-jni-headers`
}

flatlafJniHeaders {
	headers = listOf(
		"com_formdev_flatlaf_ui_FlatNativeLibrary.h",
		"com_formdev_flatlaf_ui_FlatNativeMacLibrary.h"
	)
}

library {
	targetMachines = listOf(
		machines.macOS.architecture( "arm64" ),
		machines.macOS.x86_64
	)
}

var javaHome = System.getProperty( "java.home" )
if( javaHome.endsWith( "jre" ) )
	javaHome += "/.."

interface InjectedExecOps { @get:Inject val execOps: ExecOperations }
val injected = project.objects.newInstance<InjectedExecOps>()

tasks {
	register( "build-natives" ) {
		group = "build"
		description = "Builds natives"

		if( org.gradle.internal.os.OperatingSystem.current().isMacOsX() )
			dependsOn( "linkReleaseArm64", "linkReleaseX86-64" )
	}

	withType<CppCompile>().configureEach {
		onlyIf { name.contains( "Release" ) }

		// generate and copy needed JNI headers
		dependsOn( "jni-headers" )

		includes.from(
			"${javaHome}/include",
			"${javaHome}/include/darwin"
		)

		// compile Objective-C++ sources
		source.from( files( "src/main/objcpp" )
			.asFileTree.matching { include( "**/*.mm" ) } )

		val isARM64 = name.contains( "Arm64" )
		val minOs = if( isARM64 ) minOsARM64 else minOsX86_64

		compilerArgs.addAll( toolChain.map {
			when( it ) {
				is Gcc, is Clang -> listOf( "-x", "objective-c++", "-mmacosx-version-min=$minOs" )
				else -> emptyList()
			}
		} )
	}

	withType<LinkSharedLibrary>().configureEach {
		onlyIf { name.contains( "Release" ) }

		val nativesDir = project( ":flatlaf-core" ).projectDir.resolve( "src/main/resources/com/formdev/flatlaf/natives" )
		val isARM64 = name.contains( "Arm64" )
		val minOs = if( isARM64 ) minOsARM64 else minOsX86_64
		val libraryName = if( isARM64 ) "libflatlaf-macos-arm64.dylib" else "libflatlaf-macos-x86_64.dylib"

		linkerArgs.addAll( toolChain.map {
			when( it ) {
				is Gcc, is Clang -> listOf( "-lobjc", "-framework", "Cocoa", "-mmacosx-version-min=$minOs" )
				else -> emptyList()
			}
		} )

		doLast {
			// sign shared library
//			injected.execOps.exec { commandLine( "codesign", "-s", "FormDev Software GmbH", "--timestamp", "${linkedFile.asFile.get()}" ) }

			// copy shared library to flatlaf-core resources
			copy {
				from( linkedFile )
				into( nativesDir )
				rename( linkedFile.get().asFile.name, libraryName )
			}

/*dump
			val dylib = linkedFile.asFile.get()
			val dylibDir = dylib.parent
			injected.execOps.exec { commandLine( "size", dylib ); standardOutput = FileOutputStream( "$dylibDir/size.txt" ) }
			injected.execOps.exec { commandLine( "size", "-m", dylib ); standardOutput = FileOutputStream( "$dylibDir/size-m.txt" ) }
			injected.execOps.exec {
				commandLine( "objdump",
					// commands
					"--archive-headers",
					"--section-headers",
					"--private-headers",
					"--reloc",
					"--dynamic-reloc",
					"--raw-clang-ast",
					"--syms",
					"--unwind-info",
					// options
					"--bind",
//					"--private-header",
					// files
					dylib )
				standardOutput = FileOutputStream( "$dylibDir/objdump.txt" )
			}
			injected.execOps.exec { commandLine( "objdump", "--disassemble-all", dylib ); standardOutput = FileOutputStream( "$dylibDir/disassemble.txt" ) }
			injected.execOps.exec { commandLine( "objdump", "--full-contents", dylib ); standardOutput = FileOutputStream( "$dylibDir/full-contents.txt" ) }
dump*/
		}
	}
}
