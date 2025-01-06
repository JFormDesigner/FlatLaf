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
	targetMachines = listOf( machines.linux.x86_64 )
}

var javaHome = System.getProperty( "java.home" )
if( javaHome.endsWith( "jre" ) )
	javaHome += "/.."

tasks {
	register( "build-natives" ) {
		group = "build"
		description = "Builds natives"

		if( org.gradle.internal.os.OperatingSystem.current().isLinux )
			dependsOn( "linkRelease" )
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
			"/usr/lib/x86_64-linux-gnu/glib-2.0/include",
			"/usr/include/gdk-pixbuf-2.0",
			"/usr/include/atk-1.0",
			"/usr/include/cairo",
			"/usr/include/pango-1.0",
			"/usr/include/harfbuzz",
		)

		compilerArgs.addAll( toolChain.map {
			when( it ) {
				is Gcc, is Clang -> listOf()
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
		val libraryName = "libflatlaf-linux-x86_64.so"
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
				rename( "libflatlaf-natives-linux.so", libraryName )
			}
		}
	}
}
