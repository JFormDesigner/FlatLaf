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
	`flatlaf-module-info`
	`flatlaf-java9`
	`flatlaf-publish`
}

val sigtest = configurations.create( "sigtest" )

dependencies {
	testImplementation( "org.junit.jupiter:junit-jupiter-api:5.7.2" )
	testImplementation( "org.junit.jupiter:junit-jupiter-params" )
	testRuntimeOnly( "org.junit.jupiter:junit-jupiter-engine" )

	// https://github.com/jtulach/netbeans-apitest
	sigtest( "org.netbeans.tools:sigtest-maven-plugin:1.4" )
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks {
	compileJava {
		// generate JNI headers
		options.headerOutputDirectory.set( buildDir.resolve( "generated/jni-headers" ) )
	}

	processResources {
		// build native libraries
		if( org.gradle.internal.os.OperatingSystem.current().isWindows )
			dependsOn( ":flatlaf-natives-windows:assemble" )
	}

	jar {
		archiveBaseName.set( "flatlaf" )

		doLast {
			ReorderJarEntries.reorderJarEntries( outputs.files.singleFile );
		}
	}

	named<Jar>( "sourcesJar" ) {
		archiveBaseName.set( "flatlaf" )
	}

	named<Jar>( "javadocJar" ) {
		archiveBaseName.set( "flatlaf" )
	}

	check {
		dependsOn( "sigtestCheck" )
	}

	test {
		useJUnitPlatform()
		testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
	}

	register( "sigtestGenerate" ) {
		group = "verification"
		dependsOn( "jar" )

		doLast {
			ant.withGroovyBuilder {
				"taskdef"(
					"name" to "sigtest",
					"classname" to "org.netbeans.apitest.Sigtest",
					"classpath" to sigtest.asPath )

				"sigtest"(
					"action" to "generate",
					"fileName" to "${project.name}-sigtest.txt",
					"classpath" to jar.get().outputs.files.asPath,
					"packages" to "com.formdev.flatlaf,com.formdev.flatlaf.util",
					"version" to version,
					"release" to "1.8", // Java version
					"failonerror" to "true" )
			}
		}
	}

	register( "sigtestCheck" ) {
		group = "verification"
		dependsOn( "jar" )

		doLast {
			ant.withGroovyBuilder {
				"taskdef"(
					"name" to "sigtest",
					"classname" to "org.netbeans.apitest.Sigtest",
					"classpath" to sigtest.asPath )

				"sigtest"(
					"action" to "check",
					"fileName" to "${project.name}-sigtest.txt",
					"classpath" to jar.get().outputs.files.asPath,
					"packages" to "com.formdev.flatlaf,com.formdev.flatlaf.util",
					"version" to version,
					"release" to "1.8", // Java version
					"failonerror" to "true" )
			}
		}
	}
}

flatlafPublish {
	artifactId = "flatlaf"
	name = "FlatLaf"
	description = "Flat Look and Feel"
}
