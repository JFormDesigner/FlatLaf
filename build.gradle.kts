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

import net.ltgt.gradle.errorprone.errorprone

group = "com.formdev"
version = property( if( hasProperty( "release" ) ) "flatlaf.releaseVersion" else "flatlaf.developmentVersion" ) as String

// for PR snapshots change version to 'PR-<pr_number>-SNAPSHOT'
val pullRequestNumber = findProperty( "github.event.pull_request.number" )
if( pullRequestNumber != null )
	version = "PR-${pullRequestNumber}-SNAPSHOT"


allprojects {
	version = rootProject.version

	repositories {
		mavenCentral()
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
val toolchainJavaVersion = System.getProperty( "toolchain" )
if( !toolchainJavaVersion.isNullOrEmpty() )
	println( "Java toolchain ${toolchainJavaVersion}" )
println()


plugins {
	alias( libs.plugins.gradle.nexus.publish.plugin )
	alias( libs.plugins.errorprone ) apply false
}

allprojects {
	tasks {
		withType<JavaCompile>().configureEach {
			sourceCompatibility = "1.8"
			targetCompatibility = "1.8"

			options.encoding = "ISO-8859-1"
			options.isDeprecation = false
		}

		withType<Jar>().configureEach {
			// manifest for all created JARs
			manifest.attributes(
				"Implementation-Vendor" to "FormDev Software GmbH",
				"Implementation-Copyright" to "Copyright (C) 2019-${java.time.LocalDate.now().year} FormDev Software GmbH. All rights reserved.",
				"Implementation-Version" to project.version
			)

			// add META-INF/LICENSE to all created JARs
			from( "${rootDir}/LICENSE" ) {
				into( "META-INF" )
			}
		}

		withType<Javadoc>().configureEach {
			options {
				this as StandardJavadocDocletOptions

				title = "${project.name} $version"
				header = title
				isUse = true
				tags = listOf( "uiDefault", "clientProperty" )
				addStringOption( "Xdoclint:all,-missing", "-Xdoclint:all,-missing" )
				links( "https://docs.oracle.com/en/java/javase/11/docs/api/" )
			}
			isFailOnError = false
		}
	}


	//---- Error Prone ----

	tasks.register( "errorprone" ) {
		group = "verification"
		tasks.withType<JavaCompile>().forEach {
			dependsOn( it )
		}
	}

	val useErrorProne = gradle.startParameter.taskNames.contains( "errorprone" )
	if( useErrorProne ) {
		plugins.withType<JavaPlugin> {
			apply( plugin = libs.plugins.errorprone.get().pluginId )

			dependencies {
				"errorprone"( libs.errorprone )
			}

			tasks.withType<JavaCompile>().configureEach {
				options.compilerArgs.add( "-Werror" )
				options.errorprone {
					disable(
						"ReferenceEquality",	// reports usage of '==' for objects
						"StringSplitter",		// reports String.split()
						"JavaTimeDefaultTimeZone",	// reports Year.now()
						"MissingSummary",		// reports `/** @since 2 */`
						"InvalidBlockTag",		// reports @uiDefault in javadoc
						"AlreadyChecked",		// reports false positives
						"InlineMeSuggester",	// suggests using Error Prone annotations for deprecated methods
						"TypeParameterUnusedInFormals",
						"UnsynchronizedOverridesSynchronized",
						"NonApiType",			// reports ArrayList/HashSet in parameter or return type
					)
					when( project.name ) {
						"flatlaf-intellij-themes" -> disable(
							"MutablePublicArray",	// reports FlatAllIJThemes.INFOS
						)
						"flatlaf-theme-editor" -> disable(
							"CatchAndPrintStackTrace",
						)
						"flatlaf-testing" -> disable(
							"CatchAndPrintStackTrace",
							"JdkObsolete",			// reports Hashtable used for JSlider.setLabelTable()
							"JavaUtilDate",			// reports usage of class Date
						)
					}
				}
			}
		}
	}
}

nexusPublishing {
	repositories {
		sonatype {
			// see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/
			nexusUrl = uri( "https://ossrh-staging-api.central.sonatype.com/service/local/" )
			snapshotRepositoryUrl = uri( "https://central.sonatype.com/repository/maven-snapshots/" )

			// get from gradle.properties
			val sonatypeUsername: String? by project
			val sonatypePassword: String? by project

			username = System.getenv( "SONATYPE_USERNAME" ) ?: sonatypeUsername
			password = System.getenv( "SONATYPE_PASSWORD" ) ?: sonatypePassword
		}
	}
}
