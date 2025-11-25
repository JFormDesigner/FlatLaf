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
	`java-library`
	`flatlaf-toolchain`
	`flatlaf-module-info`
}

dependencies {
	implementation( project( ":flatlaf-core" ) )
	implementation( project( ":flatlaf-extras" ) )
	implementation( project( ":flatlaf-fonts-inter" ) )
}

flatlafModuleInfo {
	dependsOn( ":flatlaf-core:jar" )
	dependsOn( ":flatlaf-extras:jar" )
	dependsOn( ":flatlaf-fonts-inter:jar" )
}

interface InjectedOps {
	@get:Inject val fs: FileSystemOperations
}

tasks {
	register( "build-for-debugging" ) {
		group = "build"

		dependsOn( "build" )

		// necessary for configuration cache
		val jar = project.tasks["jar"].outputs.files
		val runtimeJars = configurations.runtimeClasspath.get().files
		val injectedOps = project.objects.newInstance<InjectedOps>()

		doLast {
			injectedOps.fs.copy {
				from( jar )
				from( runtimeJars )
				into( "run" )
				rename( "-[0-9][0-9.]*[0-9]", "-999" )
			}
		}
	}
}
