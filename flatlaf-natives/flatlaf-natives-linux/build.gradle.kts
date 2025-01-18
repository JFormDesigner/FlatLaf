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
			"${javaHome}/include/linux"
		)

		compilerArgs.addAll( toolChain.map {
			when( it ) {
				is Gcc, is Clang -> listOf()
				else -> emptyList()
			}
		} )
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
				is Gcc, is Clang -> listOf( "-L${jawtPath}", "-l${jawt}" )
				else -> emptyList()
			}
		} )

		doLast {
			// copy shared library to flatlaf-core resources
			copy {
				from( linkedFile )
				into( nativesDir )
				rename( "libflatlaf-natives-linux.so", libraryName )
			}
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

				"$src/ApiVersion.cpp",
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
				"$objDir/X11WmUtils.o",

				"-L${layout.projectDirectory}/lib/aarch64",
				"-ljawt",
			)

			doLast {
				// copy shared library to flatlaf-core resources
				copy {
					from( "$outDir/$libraryName" )
					into( nativesDir )
				}
			}
		}
	}
}
