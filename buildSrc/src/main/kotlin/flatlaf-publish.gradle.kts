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


open class NativeArtifact( val fileName: String, val classifier: String, val type: String ) {}

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

			from( components["java"] )

			pom {
				afterEvaluate {
					this@pom.name.set( extension.name )
					this@pom.description.set( extension.description )
				}
				url.set( "https://github.com/JFormDesigner/FlatLaf" )

				licenses {
					license {
						name.set( "The Apache License, Version 2.0" )
						url.set( "https://www.apache.org/licenses/LICENSE-2.0.txt" )
					}
				}

				developers {
					developer {
						name.set( "Karl Tauber" )
						organization.set( "FormDev Software GmbH" )
						organizationUrl.set( "https://www.formdev.com/" )
					}
				}

				scm {
					connection.set( "scm:git:git://github.com/JFormDesigner/FlatLaf.git" )
					url.set( "https://github.com/JFormDesigner/FlatLaf" )
				}

				issueManagement {
					system.set( "GitHub" )
					url.set( "https://github.com/JFormDesigner/FlatLaf/issues" )
				}
			}

			afterEvaluate {
				extension.nativeArtifacts?.forEach {
					artifact( artifacts.add( "archives", file( it.fileName ) ) {
						classifier = it.classifier
						type = it.type
					} )
				}
			}
		}
	}

	repositories {
		maven {
			name = "OSSRH"

			val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
			val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
			url = uri( if( rootProject.hasProperty( "release" ) ) releasesRepoUrl else snapshotsRepoUrl )

			credentials {
				// get from gradle.properties
				val ossrhUsername: String? by project
				val ossrhPassword: String? by project

				username = System.getenv( "OSSRH_USERNAME" ) ?: ossrhUsername
				password = System.getenv( "OSSRH_PASSWORD" ) ?: ossrhPassword
			}
		}
	}
}

signing {
	// get from gradle.properties
	val signingKey: String? by project
	val signingPassword: String? by project

	val key = System.getenv( "SIGNING_KEY" ) ?: signingKey
	val password = System.getenv( "SIGNING_PASSWORD" ) ?: signingPassword

	useInMemoryPgpKeys( key, password )
	sign( publishing.publications["maven"] )
}

// disable signing of snapshots
tasks.withType<Sign>().configureEach {
	onlyIf { rootProject.hasProperty( "release" ) }
}

// check whether parallel build is enabled
tasks.withType<PublishToMavenRepository>().configureEach {
	doFirst {
		if( System.getProperty( "org.gradle.parallel" ) == "true" )
			throw RuntimeException( "Publishing does not work correctly with enabled parallel build. Disable parallel build with VM option '-Dorg.gradle.parallel=false'." )
	}
}
