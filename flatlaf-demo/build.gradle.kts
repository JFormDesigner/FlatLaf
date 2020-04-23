/*
 * Copyright 2019 FormDev Software GmbH
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
	id( "com.jfrog.bintray" )

	// Although artifactory plugin is not used in this subproject, the plugin is required
	// because otherwise gradle fails with following error:
	//     Caused by: org.codehaus.groovy.runtime.typehandling.GroovyCastException:
	//     Cannot cast object 'task ':bintrayUpload''
	//     with class 'com.jfrog.bintray.gradle.tasks.BintrayUploadTask_Decorated'
	//     to class 'com.jfrog.bintray.gradle.tasks.BintrayUploadTask'
	id( "com.jfrog.artifactory" )
}

dependencies {
	implementation( project( ":flatlaf-core" ) )
	implementation( project( ":flatlaf-extras" ) )
	implementation( project( ":flatlaf-intellij-themes" ) )
	implementation( "com.miglayout:miglayout-swing:5.2" )
	implementation( "com.jgoodies:jgoodies-forms:1.9.0" )
}

tasks {
	jar {
		dependsOn( ":flatlaf-core:jar" )
		dependsOn( ":flatlaf-extras:jar" )
		dependsOn( ":flatlaf-intellij-themes:jar" )

		manifest {
			attributes( "Main-Class" to "com.formdev.flatlaf.demo.FlatLafDemo" )
		}

		exclude( "META-INF/versions/**" )

		// include all dependencies in jar
		from( {
			configurations.runtimeClasspath.get()
				.filter { it.name.endsWith( "jar" ) }
				.map { zipTree( it ).matching {
					exclude( "META-INF/LICENSE" )
				} }
		} )
	}
}

bintray {
	user = rootProject.extra["bintray.user"] as String?
	key = rootProject.extra["bintray.key"] as String?

	setConfigurations( "archives" )

	with( pkg ) {
		repo = "flatlaf"
		name = "flatlaf-demo"
		setLicenses( "Apache-2.0" )
		vcsUrl = "https://github.com/JFormDesigner/FlatLaf"

		with( version ) {
			name = project.version.toString()
		}

		publish = rootProject.extra["bintray.publish"] as Boolean
		dryRun = rootProject.extra["bintray.dryRun"] as Boolean
	}
}
