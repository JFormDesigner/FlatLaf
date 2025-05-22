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

import Flatlaf_publish_gradle.NativeArtifact

plugins {
	`java-library`
	`flatlaf-toolchain`
	`flatlaf-module-info`
	`flatlaf-java9`
	`flatlaf-publish`
}

val sigtest = configurations.create( "sigtest" )

dependencies {
	testImplementation( libs.junit )
	testRuntimeOnly( libs.junit.launcher )

	// https://github.com/jtulach/netbeans-apitest
	sigtest( libs.sigtest )
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks {
	compileJava {
		// generate JNI headers
		options.headerOutputDirectory = layout.buildDirectory.dir( "generated/jni-headers" )
	}

	jar {
		archiveBaseName = "flatlaf"

		doLast {
			ReorderJarEntries.reorderJarEntries( outputs.files.singleFile );
		}
	}

	named<Jar>( "sourcesJar" ) {
		archiveBaseName = "flatlaf"
	}

	named<Jar>( "javadocJar" ) {
		archiveBaseName = "flatlaf"
	}

	register<Zip>( "jarNoNatives" ) {
		group = "build"
		dependsOn( "jar" )

		archiveBaseName = "flatlaf"
		archiveClassifier = "no-natives"
		archiveExtension = "jar"
		destinationDirectory = layout.buildDirectory.dir( "libs" )

		from( zipTree( jar.get().archiveFile.get().asFile ) )
		exclude( "com/formdev/flatlaf/natives/**" )
	}

	withType<AbstractPublishToMaven>().configureEach {
		dependsOn( "jarNoNatives" )
	}

	withType<Sign>().configureEach {
		dependsOn( "jarNoNatives" )
	}

	check {
		dependsOn( "sigtestCheck" )
	}

	test {
		useJUnitPlatform()
		testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

		if( JavaVersion.current() >= JavaVersion.VERSION_1_9 )
			jvmArgs( listOf( "--add-opens", "java.desktop/javax.swing.plaf.basic=ALL-UNNAMED" ) )
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
					"packages" to "com.formdev.flatlaf,com.formdev.flatlaf.themes,com.formdev.flatlaf.util",
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

	val natives = "src/main/resources/com/formdev/flatlaf/natives"
	nativeArtifacts = listOf(
		NativeArtifact( tasks.getByName( "jarNoNatives" ).outputs.files.asPath, "no-natives", "jar" ),

		NativeArtifact( "${natives}/flatlaf-windows-x86.dll",       "windows-x86",    "dll" ),
		NativeArtifact( "${natives}/flatlaf-windows-x86_64.dll",    "windows-x86_64", "dll" ),
		NativeArtifact( "${natives}/flatlaf-windows-arm64.dll",     "windows-arm64",  "dll" ),
		NativeArtifact( "${natives}/libflatlaf-macos-arm64.dylib",  "macos-arm64",    "dylib" ),
		NativeArtifact( "${natives}/libflatlaf-macos-x86_64.dylib", "macos-x86_64",   "dylib" ),
		NativeArtifact( "${natives}/libflatlaf-linux-x86_64.so",    "linux-x86_64",   "so" ),
		NativeArtifact( "${natives}/libflatlaf-linux-arm64.so",     "linux-arm64",    "so" ),
	)

	// Maven Central Snapshots repo currently does not accept .dylib files
	if( version.toString().endsWith( "-SNAPSHOT" ) )
		nativeArtifacts = nativeArtifacts?.filter { it.type != "dylib" }
}
