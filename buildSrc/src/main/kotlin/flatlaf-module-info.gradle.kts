/*
 * Copyright 2020 FormDev Software GmbH
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

open class ModuleInfoExtension {
	var paths: ArrayList<String> = ArrayList()

	fun dependsOn( vararg paths: String ) {
		this.paths.addAll( paths )
	}
}

val extension = project.extensions.create<ModuleInfoExtension>( "flatlafModuleInfo" )


plugins {
	java
}

// for Eclipse IDE project import, exclude if:
// - plugin "eclipse" is applied; e.g. if running in Eclipse IDE with buildship plugin
// - no taskNames specified at command line; e.g. if buildship synchronizes projects
val exclude =
	rootProject.plugins.hasPlugin( "eclipse" ) &&
	gradle.startParameter.taskNames.isEmpty()

if( !exclude ) {
	sourceSets {
		create( "module-info" ) {
			java {
				// include "src/main/java" and "src/main/java9" here to get compile errors if classes are
				// used from other modules that are not specified in module dependencies
				setSrcDirs( listOf( "src/main/module-info", "src/main/java", "src/main/java9" ) )

				// exclude Java 8 source file if an equally named Java 9+ source file exists
				val projectDir = projectDir // necessary for configuration cache
				exclude {
					if( it.isDirectory )
						return@exclude false
					val java9file = File( "${projectDir}/src/main/java9/${it.path}" )
					java9file.exists() && java9file != it.file
				}
			}
		}
	}

	tasks {
		named<JavaCompile>( "compileModuleInfoJava" ) {
			sourceCompatibility = "9"
			targetCompatibility = "9"

			dependsOn( extension.paths )

			options.compilerArgs.add( "--module-path" )
			options.compilerArgs.add( configurations.runtimeClasspath.get().asPath
				+ File.pathSeparator + configurations.compileClasspath.get().asPath )

			// if global toolchain is Java 8, then use Java 11 to build
			if( java.toolchain.languageVersion.get().asInt() < 9 ) {
				javaCompiler.set( javaToolchains.compilerFor {
					languageVersion.set( JavaLanguageVersion.of( 11 ) )
				} )
			}
		}

		jar {
			from( sourceSets["module-info"].output ) {
				include( "module-info.class" )
			}
		}
	}
}
