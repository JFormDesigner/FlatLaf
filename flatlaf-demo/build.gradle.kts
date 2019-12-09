/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

version = rootProject.version

plugins {
	`java-library`
	id( "com.jfrog.bintray" ) version "1.8.4"
}

dependencies {
	implementation( project( ":flatlaf-core" ) )
	implementation( project( ":flatlaf-extras" ) )
	implementation( "com.miglayout:miglayout-swing:5.2" )
	implementation( "com.jgoodies:jgoodies-forms:1.9.0" )
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
	jar {
		dependsOn( ":flatlaf-core:jar" )
		dependsOn( ":flatlaf-extras:jar" )

		manifest {
			attributes( "Main-Class" to "com.formdev.flatlaf.demo.FlatLafDemo" )
		}

		exclude( "META-INF/versions/**" )

		// include all dependencies in jar
		from( {
			configurations.runtimeClasspath.get().filter { it.name.endsWith( "jar" ) }.map { zipTree( it ) }
		} )
	}
}

bintray {
	user = System.getenv( "BINTRAY_USER" ) ?: System.getProperty( "bintray.user" )
	key = System.getenv( "BINTRAY_KEY" ) ?: System.getProperty( "bintray.key" )

	setConfigurations( "archives" )

	with( pkg ) {
		repo = "flatlaf"
		name = "flatlaf-demo"
		setLicenses( "Apache-2.0" )
		vcsUrl = "https://github.com/JFormDesigner/FlatLaf"

		with( version ) {
			name = project.version.toString()
		}

		publish = true
	}
}
