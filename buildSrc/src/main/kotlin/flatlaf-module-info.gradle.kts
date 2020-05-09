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

if( JavaVersion.current() >= JavaVersion.VERSION_1_9 ) {
	sourceSets {
		create( "module-info" ) {
			java {
				// include "src/main/java" here to get compile errors if classes are
				// used from other modules that are not specified in module dependencies
				setSrcDirs( listOf( "src/main/module-info", "src/main/java" ) )
			}
		}
	}

	tasks {
		named<JavaCompile>( "compileModuleInfoJava" ) {
			sourceCompatibility = "9"
			targetCompatibility = "9"

			dependsOn( extension.paths )

			options.compilerArgs.add( "--module-path" )
			options.compilerArgs.add( configurations.runtimeClasspath.get().asPath )
		}

		jar {
			manifest.attributes( "Multi-Release" to "true" )

			into( "META-INF/versions/9" ) {
				from( sourceSets["module-info"].output ) {
					include( "module-info.class" )
				}
			}
		}
	}
}
