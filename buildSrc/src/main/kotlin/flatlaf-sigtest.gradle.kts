/*
 * Copyright 2026 FormDev Software
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

/*
 * Public API signature generation/check using sigtest.
 * https://github.com/eclipse-ee4j/jakartaee-tck-tools/tree/master/tools/sigtest
 *
 * Tasks:
 *   sigtestGenerate - generates signature file '<project-name>-sigtest.txt'
 *   sigtestCheck    - checks API against signature file (part of 'check')
 */

import java.io.ByteArrayOutputStream

abstract class SigtestExtension {
	/** packages written to signature file and checked against signature file */
	abstract val packages: ListProperty<String>
}

val extension = project.extensions.create<SigtestExtension>( "flatlafSigtest" )


plugins {
	java
}

// version catalog accessor 'libs' is not available in precompiled script plugins
val libs = project.extensions.getByType<VersionCatalogsExtension>().named( "libs" )

val sigtest = configurations.register( "sigtest" )

dependencies {
	sigtest( libs.findLibrary( "sigtest" ).get() )
}

tasks {
	check {
		dependsOn( "sigtestCheck" )
	}

	register<JavaExec>( "sigtestGenerate" ) {
		group = "verification"

		val signatureFile = layout.projectDirectory.file( "${project.name}-sigtest.txt" ).asFile
		outputs.file( signatureFile ).withPropertyName( "signatureFile" )

		javaLauncher = sigtestJavaLauncher()
		classpath = files( sigtest )
		mainClass = "com.sun.tdk.signaturetest.Setup"
		args( "-static", "-ErrorAll", "-KeepFile" )
		argumentProviders.add( sigtestArgs( signatureFile ) )
		isIgnoreExitValue = true

		doFirst {
			// capture output (only logged on failure)
			val output = ByteArrayOutputStream()
			(this as JavaExec).standardOutput = output
			errorOutput = output
		}

		doLast {
			// exit value 95 means passed
			val exitValue = (this as JavaExec).executionResult.get().exitValue
			if( exitValue != 95 ) {
				logger.error( standardOutput.toString() )
				throw GradleException( "Sigtest generate failed (exit value $exitValue)" )
			}

			// use LF line endings in signature file
			val content = signatureFile.readText( Charsets.ISO_8859_1 )
			val newContent = content.replace( "\r\n", "\n" )
			if( newContent != content )
				signatureFile.writeText( newContent, Charsets.ISO_8859_1 )
		}
	}

	register<JavaExec>( "sigtestCheck" ) {
		group = "verification"

		// if both tasks are run, check against the newly generated signature file
		mustRunAfter( "sigtestGenerate" )

		val signatureFile = layout.projectDirectory.file( "${project.name}-sigtest.txt" ).asFile
		inputs.file( signatureFile )
			.withPropertyName( "signatureFile" )
			.withPathSensitivity( PathSensitivity.NONE )

		// marker file written on success, which allows the task to be up-to-date
		val markerFile = layout.buildDirectory.file( "sigtest/sigtestCheck-passed.txt" ).get().asFile
		outputs.file( markerFile ).withPropertyName( "markerFile" )

		javaLauncher = sigtestJavaLauncher()
		classpath = files( sigtest )
		mainClass = "com.sun.tdk.signaturetest.SignatureTest"
		args( "-static", "-b", "-Mode", "bin" )
		argumentProviders.add( sigtestArgs( signatureFile ) )
		isIgnoreExitValue = true

		doFirst {
			// capture output (only logged on failure)
			val output = ByteArrayOutputStream()
			(this as JavaExec).standardOutput = output
			errorOutput = output
		}

		doLast {
			// exit value 95 means passed
			val exitValue = (this as JavaExec).executionResult.get().exitValue
			if( exitValue != 95 ) {
				logger.error( standardOutput.toString() )
				throw GradleException( "Sigtest check failed (exit value $exitValue)" )
			}

			markerFile.writeText( "passed\n" )
		}
	}
}

/**
 * Java launcher for sigtest, which requires Java 11+.
 * Use same Java version that runs Gradle (instead of default toolchain, which is usually Java 8).
 */
fun sigtestJavaLauncher() = javaToolchains.launcherFor {
	languageVersion = JavaLanguageVersion.current()
}

/**
 * Common arguments for com.sun.tdk.signaturetest.Setup and SignatureTest.
 * Evaluated lazily (at execution time).
 * Declaring the classpath as input also infers the task dependency on the jar task.
 */
fun sigtestArgs( signatureFile: File ): CommandLineArgumentProvider {
	val apiVersion = provider { version.toString() }
	val jarFiles = files( tasks.jar )
	val packages = extension.packages
	return object : CommandLineArgumentProvider {
		// declared as task output (sigtestGenerate) or input (sigtestCheck)
		@get:Internal val signatureFile = signatureFile
		@get:Input val apiVersion = apiVersion
		@get:Classpath val classpath = jarFiles
		@get:Input val packages = packages

		override fun asArguments(): Iterable<String> {
			val args = mutableListOf(
				"-FileName", signatureFile.absolutePath,
				"-ApiVersion", apiVersion.get(),
				"-BootCP", "8", // Java version
				"-Classpath", classpath.asPath )
			for( pkg in packages.get() )
				args += listOf( "-PackageWithoutSubpackages", pkg )
			return args
		}
	}
}
