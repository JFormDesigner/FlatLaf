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

open class SigtestExtension {
	/** packages written to signature file and checked against signature file */
	var packages: List<String> = emptyList()
}

val extension = project.extensions.create<SigtestExtension>( "flatlafSigtest" )


plugins {
	java
}

val sigtest = configurations.create( "sigtest" )

// version catalog accessor 'libs' is not available in precompiled script plugins
val libs = project.extensions.getByType<VersionCatalogsExtension>().named( "libs" )

dependencies {
	sigtest( libs.findLibrary( "sigtest" ).get() )
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
 */
fun sigtestArgs( signatureFile: File, packages: List<String> ): List<String> {
	val args = mutableListOf(
		"-FileName", signatureFile.absolutePath,
		"-ApiVersion", version.toString(),
		"-BootCP", "8", // Java version
		"-Classpath", tasks.jar.get().outputs.files.asPath )
	for( pkg in packages )
		args += listOf( "-PackageWithoutSubpackages", pkg )
	return args
}

tasks {
	check {
		dependsOn( "sigtestCheck" )
	}

	register<JavaExec>( "sigtestGenerate" ) {
		group = "verification"
		dependsOn( "jar" )

		val signatureFile = layout.projectDirectory.file( "${project.name}-sigtest.txt" ).asFile

		javaLauncher = sigtestJavaLauncher()
		classpath = sigtest
		mainClass = "com.sun.tdk.signaturetest.Setup"
		args( "-static", "-ErrorAll", "-KeepFile" )
		args( sigtestArgs( signatureFile, extension.packages ) )
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
		dependsOn( "jar" )

		val signatureFile = layout.projectDirectory.file( "${project.name}-sigtest.txt" ).asFile

		javaLauncher = sigtestJavaLauncher()
		classpath = sigtest
		mainClass = "com.sun.tdk.signaturetest.SignatureTest"
		args( "-static", "-b", "-Mode", "bin" )
		args( sigtestArgs( signatureFile, extension.packages ) )
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
		}
	}
}
