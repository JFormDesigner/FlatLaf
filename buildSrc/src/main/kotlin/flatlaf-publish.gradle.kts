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


/**
 * @param source file name (String, relative to project directory) or any other
 *               notation supported by MavenPublication.artifact() (e.g. a task provider)
 */
open class NativeArtifact( val source: Any, val classifier: String, val extension: String ) {}

open class PublishExtension {
	var artifactId: String? = null
	var name: String? = null
	var description: String? = null
	var nativeArtifacts: List<NativeArtifact>? = null
}

val extension = project.extensions.create<PublishExtension>( "flatlafPublish" )


plugins {
	`maven-publish`
	signing
}

publishing {
	publications {
		create<MavenPublication>( "maven" ) {
			afterEvaluate {
				artifactId = extension.artifactId
			}
			groupId = "com.formdev"

			if( pluginManager.hasPlugin( "java" ) )
				from( components["java"] )
			else if( pluginManager.hasPlugin( "java-platform" ) )
				from( components["javaPlatform"] )

			pom {
				afterEvaluate {
					this@pom.name = extension.name
					this@pom.description = extension.description
				}
				url = "https://github.com/JFormDesigner/FlatLaf"

				licenses {
					license {
						name = "The Apache License, Version 2.0"
						url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
					}
				}

				developers {
					developer {
						name = "Karl Tauber"
						organization = "FormDev Software"
						organizationUrl = "https://www.formdev.com/"
					}
				}

				scm {
					connection = "scm:git:git://github.com/JFormDesigner/FlatLaf.git"
					url = "https://github.com/JFormDesigner/FlatLaf"
				}

				issueManagement {
					system = "GitHub"
					url = "https://github.com/JFormDesigner/FlatLaf/issues"
				}
			}

			afterEvaluate {
				extension.nativeArtifacts?.forEach {
					val source = it.source
					artifact( if( source is String ) file( source ) else source ) {
						classifier = it.classifier
						extension = it.extension
					}
				}
			}
		}
	}
}

signing {
	// get from gradle.properties
	val signingKey = project.findProperty( "signingKey" ) as String?
	val signingPassword = project.findProperty( "signingPassword" ) as String?

	val key = System.getenv( "SIGNING_KEY" ) ?: signingKey
	val password = System.getenv( "SIGNING_PASSWORD" ) ?: signingPassword

	useInMemoryPgpKeys( key, password )
	sign( publishing.publications["maven"] )
}

// disable signing of snapshots
tasks.withType<Sign>().configureEach {
	val release = rootProject.hasProperty( "release" ) // necessary for configuration cache
	onlyIf { release }
}

tasks {
	// check whether parallel build is enabled
	withType<AbstractPublishToMaven>().configureEach {
		// covers '--parallel', '-Dorg.gradle.parallel=true' and 'org.gradle.parallel=true' in gradle.properties
		val parallel = gradle.startParameter.isParallelProjectExecutionEnabled // necessary for configuration cache
		doFirst {
			if( parallel )
				throw RuntimeException( "Publishing does not work correctly with enabled parallel build. Disable parallel build with VM option '-Dorg.gradle.parallel=false'." )
		}
	}

	register( "publishToSonatypeAndCloseStagingRepo" ) {
		group = "publishing"
		description = "Publish to Sonatype Maven Central and close staging repository"

		dependsOn(
			"publishToSonatype",
			":closeSonatypeStagingRepository"
		)
	}
}
