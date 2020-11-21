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

val releaseVersion = "0.44"
val developmentVersion = "0.45-SNAPSHOT"

version = if( java.lang.Boolean.getBoolean( "release" ) ) releaseVersion else developmentVersion

allprojects {
	version = rootProject.version

	repositories {
		jcenter()
	}
}

// check required Java version
if( JavaVersion.current() < JavaVersion.VERSION_1_8 )
	throw RuntimeException( "Java 8 or later required (running ${System.getProperty( "java.version" )})" )

// log version, Gradle and Java versions
println()
println( "-------------------------------------------------------------------------------" )
println( "FlatLaf Version: ${version}" )
println( "Gradle ${gradle.gradleVersion} at ${gradle.gradleHomeDir}" )
println( "Java ${System.getProperty( "java.version" )}" )
println()


extra["bintray.user"] = System.getenv( "BINTRAY_USER" ) ?: System.getProperty( "bintray.user" )
extra["bintray.key"]  = System.getenv( "BINTRAY_KEY" )  ?: System.getProperty( "bintray.key" )

// if true, do not upload to bintray
extra["bintray.dryRun"] = false

// if true, uploaded artifacts are visible to all
// if false, only visible to owner when logged into bintray
extra["bintray.publish"] = false


allprojects {
	tasks {
		withType<JavaCompile>().configureEach {
			sourceCompatibility = "1.8"
			targetCompatibility = "1.8"

			options.encoding = "ISO-8859-1"
		}

		withType<Jar>().configureEach {
			// manifest for all created JARs
			manifest.attributes(mapOf(
				"Implementation-Vendor" to "FormDev Software GmbH",
				"Implementation-Copyright" to "Copyright (C) ${java.time.LocalDate.now().year} FormDev Software GmbH. All rights reserved.",
				"Implementation-Version" to project.version))

			// add META-INF/LICENSE to all created JARs
			from("${rootDir}/LICENSE") {
				into("META-INF")
			}
		}
	}
}
