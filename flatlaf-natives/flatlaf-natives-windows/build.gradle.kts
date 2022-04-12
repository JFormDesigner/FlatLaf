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
	id( "dev.nokee.jni-library" ) version "0.4.0"
	id( "dev.nokee.cpp-language" ) version "0.4.0"
}

library {
	targetMachines.set( listOf( machines.windows.x86, machines.windows.x86_64 ) )

	variants.configureEach {
		sharedLibrary {
			compileTasks.configureEach {
				onlyIf { isBuildable }

				// depend on :flatlaf-core:compileJava because it generates the JNI headers
				dependsOn( ":flatlaf-core:compileJava" )

				doFirst {
					println( "Used Tool Chain:" )
					println( "  - ${toolChain.get()}" )
					println( "Available Tool Chains:" )
					toolChains.forEach {
						println( "  - $it" )
					}

					// copy needed JNI headers
					copy {
						from( project( ":flatlaf-core" ).buildDir.resolve( "generated/jni-headers" ) )
						into( "src/main/headers" )
						include(
							"com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder.h",
							"com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder_WndProc.h"
						)
						filter<org.apache.tools.ant.filters.FixCrLfFilter>(
							"eol" to org.apache.tools.ant.filters.FixCrLfFilter.CrLf.newInstance( "lf" )
						)
					}
				}

				compilerArgs.addAll( toolChain.map {
					when( it ) {
						is Gcc, is Clang -> listOf( "-O2", "-DUNICODE" )
						is VisualCpp -> listOf( "/O2", "/Zl", "/GS-", "/DUNICODE" )
						else -> emptyList()
					}
				} )
			}

			linkTask.configure {
				onlyIf { isBuildable }

				val nativesDir = project( ":flatlaf-core" ).projectDir.resolve( "src/main/resources/com/formdev/flatlaf/natives" )
				val is64Bit = targetMachine.architecture.is64Bit
				val libraryName = if( is64Bit ) "flatlaf-windows-x86_64.dll" else "flatlaf-windows-x86.dll"
				val jawt = if( is64Bit ) "lib/jawt-x86_64" else "lib/jawt-x86"

				outputs.file( "$nativesDir/$libraryName" )

				linkerArgs.addAll( toolChain.map {
					when( it ) {
						is Gcc, is Clang -> listOf( "-l${jawt}", "-lUser32", "-lGdi32", "-lshell32", "-lAdvAPI32", "-lKernel32" )
						is VisualCpp -> listOf( "${jawt}.lib", "User32.lib", "Gdi32.lib", "shell32.lib", "AdvAPI32.lib", "Kernel32.lib", "/NODEFAULTLIB" )
						else -> emptyList()
					}
				} )

				doLast {
					// copy shared library to flatlaf-core resources
					copy {
						from( linkedFile )
						into( nativesDir )
						rename( "flatlaf-natives-windows.dll", libraryName )
					}
				}
			}

			for( taskName in listOf( "jarX86", "jarX86-64" ) ) {
				tasks.named( taskName ) {
					onlyIf { false }
				}
			}
		}
	}
}
