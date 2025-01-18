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

plugins {
	`cpp-library`
	`flatlaf-cpp-library`
	`flatlaf-jni-headers`
}

flatlafJniHeaders {
	headers = listOf(
		"com_formdev_flatlaf_ui_FlatNativeLibrary.h",
		"com_formdev_flatlaf_ui_FlatNativeWindowsLibrary.h",
		"com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder.h",
		"com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder_WndProc.h"
	)
}

library {
	targetMachines = listOf(
		machines.windows.x86,
		machines.windows.x86_64,
		machines.windows.architecture( "aarch64" )
	)
}

var javaHome = System.getProperty( "java.home" )
if( javaHome.endsWith( "jre" ) )
	javaHome += "/.."

tasks {
	register( "build-natives" ) {
		group = "build"
		description = "Builds natives"

		if( org.gradle.internal.os.OperatingSystem.current().isWindows() )
			dependsOn( "linkReleaseX86", "linkReleaseX86-64", "linkReleaseAarch64" )
	}

	withType<CppCompile>().configureEach {
		onlyIf { name.contains( "Release" ) }

		// generate and copy needed JNI headers
		dependsOn( "jni-headers" )

		includes.from(
			"${javaHome}/include",
			"${javaHome}/include/win32"
		)

		compilerArgs.addAll( toolChain.map {
			when( it ) {
				is Gcc, is Clang -> listOf( "-O2", "-DUNICODE" )
				is VisualCpp -> listOf( "/O2", "/Zl", "/GS-", "/DUNICODE" )
				else -> emptyList()
			}
		} )
	}

	withType<LinkSharedLibrary>().configureEach {
		onlyIf { name.contains( "Release" ) }

		val nativesDir = project( ":flatlaf-core" ).projectDir.resolve( "src/main/resources/com/formdev/flatlaf/natives" )
		val isX86 = name.contains("X86")
		val is64Bit = name.contains( "64" )
		val libraryName = if( is64Bit && isX86 ) "flatlaf-windows-x86_64.dll" else if( isX86 ) "flatlaf-windows-x86.dll" else "flatlaf-windows-arm64.dll"

		linkerArgs.addAll( toolChain.map {
			when( it ) {
				is Gcc, is Clang -> listOf( "-lUser32", "-lGdi32", "-lshell32", "-lAdvAPI32", "-lKernel32", "-lDwmapi" )
				is VisualCpp -> listOf( "User32.lib", "Gdi32.lib", "shell32.lib", "AdvAPI32.lib", "Kernel32.lib", "Dwmapi.lib", "/NODEFAULTLIB" )
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
		}
	}
}
